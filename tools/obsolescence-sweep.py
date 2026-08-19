#!/usr/bin/env python3
"""Obsolescence sweep -- two complementary checks over the open issue backlog.

  A. code identifiers   -- does the class/method/file an issue names still exist?
  B. dependency names   -- is the library an issue is about still declared anywhere?

A miss is a SIGNAL, not a verdict: code is renamed, moved, or lives in a sibling repo.
Output is a triage queue, never a close list -- confirm every finding by hand.
Known false-positive modes are documented in docs/backlog/05-obsolescence-sweep.md.

Usage:
    gh issue list --repo virtualcell/vcell --state open --limit 500 \\
        --json number,title,body,createdAt > issues.json
    python3 tools/obsolescence-sweep.py [issues.json] [--repo PATH]
"""
import json, re, subprocess, collections, os, sys

args = [a for a in sys.argv[1:] if not a.startswith('-')]
ISSUES_JSON = args[0] if args else 'issues.json'
REPO = os.environ.get('VCELL_REPO') or subprocess.run(
    ['git', 'rev-parse', '--show-toplevel'], capture_output=True, text=True).stdout.strip() or '.'
OUT = 'obsolescence-sweep.json'
SRC_EXT = ('.java', '.py', '.xml', '.sh', '.ts', '.properties', '.yml', '.yaml',
           '.json', '.js', '.html', '.md', '.c', '.cpp', '.h', '.sql', '.cff', '.toml')
FILE_EXT = re.compile(r'\.(java|py|xml|sh|ts|properties|yml|yaml|cpp|h|md|json|js|sql|txt|pptx|docx|csv|zip|omex|vcml|jar)$', re.I)

files = [f for f in subprocess.run(['git', 'ls-files'], cwd=REPO, capture_output=True, text=True)
         .stdout.split('\n') if f]
src = [f for f in files if f.endswith(SRC_EXT)]
paths = set(files)
basenames = {os.path.basename(f) for f in files}
stems = {os.path.splitext(os.path.basename(f))[0] for f in files}

# Case-folded indexes. A token that matches only when case is ignored is NOT a miss --
# the code is there, the issue text is just stale. Reported separately as "renamed",
# because that is a different action (correct the issue) from "gone" (consider closing).
paths_ci = collections.defaultdict(set)
for f in files:
    paths_ci[f.lower()].add(f)
    b = os.path.basename(f)
    paths_ci[b.lower()].add(f)
    paths_ci[os.path.splitext(b)[0].lower()].add(f)

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
corpus_ci = collections.defaultdict(set)
for ident in corpus:
    corpus_ci[ident.lower()].add(ident)
for s_ in stems:
    corpus_ci[s_.lower()].add(s_)
DEPBLOB = '\n'.join(DEPFILES)
print(f'indexed {len(src)} source files, {len(corpus)} identifiers, {len(DEPFILES)} dependency manifests')

# An empty index makes EVERY reference look missing -- the most dangerous possible output,
# because it reads as "the whole backlog is obsolete". Refuse to run rather than mislead.
if len(src) < 1000 or not DEPFILES:
    sys.exit(f"error: indexed only {len(src)} source files and {len(DEPFILES)} manifests from "
             f"{REPO!r}.\nThis is not a VCell checkout, or git ls-files failed. Run from inside "
             f"the repo, or set VCELL_REPO=/path/to/vcell. Refusing to report every reference "
             f"as missing.")

issues = {i['number']: i for i in json.load(open(ISSUES_JSON))}

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


def resolve(kind, tok):
    """-> ('present', None) | ('renamed', what-it-actually-is) | ('missing', None).

    'renamed' means the token matched only case-insensitively. The code exists; the issue
    text is stale. Keeping this distinct from 'missing' is the whole point -- conflating
    them once made #912 look obsolete when SedmlJob.java had merely become SedMLJob.java.
    """
    if kind == 'path':
        b = os.path.basename(tok)
        stem, ext = os.path.splitext(b)
        # A stem match across different extensions is not a match -- 'model.xml' is not
        # 'Model.java'. Only fall back to the bare stem when the token has no extension.
        if tok in paths or b in basenames or (not ext and stem in stems):
            return 'present', None
        keys = [tok.lower(), b.lower()] + ([] if ext else [stem.lower()])
        for key in keys:
            for cand_path in sorted(paths_ci.get(key, ())):
                if not ext or os.path.splitext(cand_path)[1].lower() == ext.lower():
                    return 'renamed', cand_path
        return 'missing', None

    parts = [p for p in tok.split('.') if p] if kind == 'symbol' else [tok]
    if kind == 'symbol':
        if all(p in corpus for p in parts):
            return 'present', None
        gone = [p for p in parts if p not in corpus]
        fixed = [sorted(corpus_ci[p.lower()])[0] for p in gone if corpus_ci.get(p.lower())]
        if len(fixed) == len(gone):
            return 'renamed', '/'.join(fixed)
        return 'missing', None

    if tok in corpus or tok in stems:
        return 'present', None
    hit = corpus_ci.get(tok.lower())
    return ('renamed', sorted(hit)[0]) if hit else ('missing', None)


rows = []
for n, iss in sorted(issues.items()):
    body = iss['body'] or ''
    cand = classify(body)
    missing, present, renamed = [], [], []
    for kind, toks in cand.items():
        for t in sorted(toks):
            verdict, actual = resolve(kind, t)
            if verdict == 'present':
                present.append((kind, t))
            elif verdict == 'renamed':
                renamed.append((kind, t, actual))
            else:
                missing.append((kind, t))
    deps = {}
    low = body.lower()
    for name, needle in DEPS.items():
        if re.search(r'\b' + re.escape(name) + r'\b', low):
            deps[name] = needle in DEPBLOB
    if not (missing or present or renamed or deps):
        continue
    rows.append({'n': n, 'title': iss['title'], 'created': iss['createdAt'][:7],
                 'missing': missing, 'present': present, 'renamed': renamed, 'deps': deps,
                 'other_repo': bool(OTHER_REPO.search(body))})

json.dump(rows, open(OUT, 'w'), indent=1)

gone_dep = [r for r in rows if r['deps'] and not any(r['deps'].values())]
all_missing = [r for r in rows if r['missing'] and not r['present'] and not r['renamed']]
stale_text = [r for r in rows if r['renamed']]
print(f'\nissues naming something checkable: {len(rows)} of {len(issues)} open')
print(f'  every code reference missing : {len(all_missing)}   <- obsolescence signal')
print(f'  stale text (case-only rename): {len(stale_text)}   <- fix the issue, not the code')
print(f'  every named dependency gone  : {len(gone_dep)}')

print('\n===== A. every code reference missing =====')
for r in sorted(all_missing, key=lambda r: -len(r['missing'])):
    tag = '  [names another repo]' if r['other_repo'] else ''
    print(f"#{r['n']} ({r['created']}){tag} {r['title'][:64]}")
    for kind, t in r['missing'][:5]:
        print(f'      {kind:7} {t}')

print('\n===== A2. stale references -- code exists under different case =====')
for r in sorted(stale_text, key=lambda r: r['n']):
    print(f"#{r['n']} ({r['created']}) {r['title'][:64]}")
    for kind, t, actual in r['renamed'][:5]:
        print(f'      {kind:7} {t}  ->  {actual}')

print('\n===== B. dependency status =====')
for r in sorted(rows, key=lambda r: r['n']):
    if not r['deps']:
        continue
    st = ', '.join(f'{k}={"PRESENT" if v else "GONE"}' for k, v in sorted(r['deps'].items()))
    mark = '  <-- all gone' if not any(r['deps'].values()) else ''
    print(f"#{r['n']:<5} {st:<52}{mark}  {r['title'][:44]}")
