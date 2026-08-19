#!/usr/bin/env python3
"""Obsolescence sweep v2 -- two complementary checks.

  A. code identifiers   -- does the class/method/file an issue names still exist?
  B. dependency names   -- is the library an issue is about still declared anywhere?

A miss is a SIGNAL, not a verdict: code is renamed, moved, or lives in a sibling repo.
Output is a triage queue, never a close list.
"""
import json, re, subprocess, collections, os

S = '/private/tmp/claude-504/-Users-jimschaff-Documents-workspace-vcell/fee80085-3f84-4d6a-a600-018fbae0ead1/scratchpad/bl/'
REPO = '/Users/jimschaff/Documents/workspace/vcell/.claude/worktrees/bug+backlog'
SRC_EXT = ('.java', '.py', '.xml', '.sh', '.ts', '.properties', '.yml', '.yaml',
           '.json', '.js', '.html', '.md', '.c', '.cpp', '.h', '.sql', '.cff', '.toml')
FILE_EXT = re.compile(r'\.(java|py|xml|sh|ts|properties|yml|yaml|cpp|h|md|json|js|sql|txt|pptx|docx|csv|zip|omex|vcml|jar)$', re.I)

files = [f for f in subprocess.run(['git', 'ls-files'], cwd=REPO, capture_output=True, text=True)
         .stdout.split('\n') if f]
src = [f for f in files if f.endswith(SRC_EXT)]
paths = set(files)
basenames = {os.path.basename(f) for f in files}
stems = {os.path.splitext(os.path.basename(f))[0] for f in files}

IDENT = re.compile(rb'[A-Za-z_][A-Za-z0-9_]{2,}')
corpus = set()
DEPFILES = []
for f in src:
    p = os.path.join(REPO, f)
    try:
        if os.path.getsize(p) > 2_000_000:
            continue
        raw = open(p, 'rb').read()
    except OSError:
        continue
    for m in IDENT.finditer(raw):
        corpus.add(m.group().decode('ascii', 'ignore'))
    if os.path.basename(f) in ('pom.xml', 'requirements.txt', 'pyproject.toml',
                               'package.json', 'Dockerfile'):
        DEPFILES.append(raw.decode('utf8', 'replace').lower())
DEPBLOB = '\n'.join(DEPFILES)
print(f'indexed {len(src)} source files, {len(corpus)} identifiers, {len(DEPFILES)} dependency manifests')

issues = {i['number']: i for i in json.load(open(S + 'issues.json'))}

OTHER_REPO = re.compile(
    r'vcell-solvers|vcell-fluxcd|vcell-jsbml|pyvcell|LangevinNoVis|cam-center/|vcell-fiji'
    r'|usermaterials|zenodo-maint|Biosimulators_test_suite|temp-biomodels|MBSolver|vcellroot', re.I)

NOISE = {
    'Exception','Error','RuntimeException','NullPointerException','String','Object','Java',
    'Windows','Linux','Mac','MacOS','VCell','BioModel','MathModel','GitHub','Docker','Oracle',
    'SBML','SEDML','SedML','OMEX','HDF5','JSON','XML','API','REST','HTTP','HTTPS','URL','URI',
    'DataAccessException','IllegalArgumentException','ClassCastException','LetsEncrypt',
    'ArrayIndexOutOfBoundsException','NFSim','BioNetGen','Smoldyn','COPASI','Simulation',
    'Application','Geometry','TODO','NOTE','FIXME','PDE','ODE','CSV','PNG','Priority',
    'Importance','BioModels','BioSimulations','BioSimulators','SpringSaLaD','SpringSalad',
    'Langevin','Quarkus','Keycloak','PubMed','ImageJ','README','CLAUDE','MEMORY','SKILL',
}
# documentation / attachment files are not code -- their absence proves nothing
DOC_EXT = re.compile(r'\.(md|pptx|docx|csv|zip|omex|vcml|txt|png|jpg|pdf)$', re.I)

DOTTED = re.compile(r'\b([A-Z][A-Za-z0-9]*(?:\.[A-Za-z][A-Za-z0-9_]*)+)\b')
CAMEL  = re.compile(r'\b([A-Z][a-z0-9]+(?:[A-Z][a-z0-9]+){1,})\b')
PATH   = re.compile(r'\b((?:[\w.-]+/){1,}[\w.-]+\.(?:java|py|xml|sh|ts|properties|yml|yaml|cpp|h))\b')

# libraries VCell has or had; check whether still declared in a manifest
DEPS = {
    'jhdf': 'io.jhdf', 'ncsa.hdf': 'ncsa', 'bioformats': 'bioformats', 'seaborn': 'seaborn',
    'jlibsedml': 'jlibsedml', 'jsbml': 'jsbml', 'restlet': 'restlet', 'activemq': 'activemq',
    'artemis': 'artemis', 'mongodb': 'mongo', 'vtk': 'vtk', 'n5': 'n5', 'matplotlib': 'matplotlib',
    'log4j': 'log4j', 'hdf5': 'hdf5', 'bionetgen': 'bionetgen', 'copasi': 'copasi',
}


def classify(body):
    body = re.sub(r'```.*?```', ' ', body or '', flags=re.S)
    out = {'path': set(), 'symbol': set(), 'class': set()}
    for m in PATH.finditer(body):
        out['path'].add(m.group(1))
    for m in DOTTED.finditer(body):
        tok = m.group(1)
        if tok.split('.')[0] in NOISE or DOC_EXT.search(tok):
            continue
        out['path' if FILE_EXT.search(tok) else 'symbol'].add(tok)
    for m in CAMEL.finditer(body):
        tok = m.group(1)
        if tok in NOISE or len(tok) < 6:
            continue
        out['class'].add(tok)
    return out


def exists(kind, tok):
    if kind == 'path':
        b = os.path.basename(tok)
        return tok in paths or b in basenames or os.path.splitext(b)[0] in stems
    if kind == 'symbol':
        return all(p in corpus for p in tok.split('.') if p)
    return tok in corpus or tok in stems


rows = []
for n, iss in sorted(issues.items()):
    body = iss['body'] or ''
    cand = classify(body)
    missing, present = [], []
    for kind, toks in cand.items():
        for t in sorted(toks):
            (present if exists(kind, t) else missing).append((kind, t))
    deps = {}
    low = body.lower()
    for name, needle in DEPS.items():
        if re.search(r'\b' + re.escape(name) + r'\b', low):
            deps[name] = needle in DEPBLOB
    if not (missing or present or deps):
        continue
    rows.append({'n': n, 'title': iss['title'], 'created': iss['createdAt'][:7],
                 'missing': missing, 'present': present, 'deps': deps,
                 'other_repo': bool(OTHER_REPO.search(body))})

json.dump(rows, open(S + 'obsolete2.json', 'w'), indent=1)

gone_dep = [r for r in rows if r['deps'] and not any(r['deps'].values())]
all_missing = [r for r in rows if r['missing'] and not r['present']]
print(f'\nissues naming something checkable: {len(rows)} of {len(issues)} open')
print(f'  every code reference missing : {len(all_missing)}')
print(f'  every named dependency gone  : {len(gone_dep)}')

print('\n===== A. every code reference missing =====')
for r in sorted(all_missing, key=lambda r: -len(r['missing'])):
    tag = '  [names another repo]' if r['other_repo'] else ''
    print(f"#{r['n']} ({r['created']}){tag} {r['title'][:64]}")
    for kind, t in r['missing'][:5]:
        print(f'      {kind:7} {t}')

print('\n===== B. dependency status =====')
for r in sorted(rows, key=lambda r: r['n']):
    if not r['deps']:
        continue
    st = ', '.join(f'{k}={"PRESENT" if v else "GONE"}' for k, v in sorted(r['deps'].items()))
    mark = '  <-- all gone' if not any(r['deps'].values()) else ''
    print(f"#{r['n']:<5} {st:<52}{mark}  {r['title'][:44]}")
