# Retrospective Methodology

How this VCell organization retrospective was produced — its goal, the evidence
principles that governed it, the phased technique, and how to reproduce or update it.

The single most important idea is in [§2, Evidence hierarchy](#2-evidence-hierarchy):
**diffs are ground truth; PR and commit messages are the least-trusted hints.** VCell
did not adopt a PR-based workflow until ~2022, so the first four years of the window
cannot be read off PRs at all.

---

## 1. Goal & scope

A **comprehensive historical retrospective and feature timeline** of VCell across its
two GitHub organizations — **`virtualcell`** (primary) and **`cam-center`**
(the Langevin / SpringSaLaD particle solvers) — produced at two altitudes:

- a **readable executive summary** and an **org-wide timeline** of what was built and
  why, and
- a **per-repo entry** for every substantive repository (project background + a themed,
  chronological history), each independently useful.

Parameters fixed for this run:

| Parameter | Value |
|---|---|
| **Cutoff** | **2018-09-01.** Everything older is *background*, not timeline. |
| **Pre-cutoff context** | VCell dates to **1997**; the Docker-microservices server, model database, HPC dispatch, and most solvers predate the window and are described as background. |
| **Orgs** | `virtualcell` (33 repos) + `cam-center` (`LangevinNoVis01`, `SpringSaLaD`). |
| **In scope** | All active non-fork `virtualcell` repos (grouped into 15 entries), plus the two `cam-center` solver repos. |
| **Noted, not deep-dived** | Forks: `openapi-generator`, `vcell-jsbml`, `modelbricks-webapp`, `vcellMichael`, `Biosimulators_utils`, `GoogleSummerOfCode`. They are mostly upstream + dependency automation and add noise. |

VCell's shape differs from a typical web org in two ways that shaped the method: it is a
**single dominant monorepo** (`vcell`, 821 PRs, ~310 tags in-window) surrounded by
satellites, and a large fraction of the satellites are **numerical solvers** whose
history was **extracted out of a C/C++ monorepo (`vcell-solvers`) over ~2 years**, so a
solver's early history lives in the old monorepo, not its new repo.

## 2. Evidence hierarchy

The central principle. Sources are trusted in this order, highest first:

1. **Code / diffs** — *ground truth.* What the change actually did.
2. **Releases / tags / CHANGELOG** — the project's own checkpoints of shipped behavior.
3. **Repo README & directory structure** — for the code-first description of *what a
   repo is*, independent of how its work was narrated.
4. **Project board (Epic / Classification)** — org Project #1 "vcell development".
   **Recent era only (2022-07+), partial.**
5. **PR & commit messages** — *lowest.* Hints to verify against the diff, never relied on
   alone.

Why this ordering, concretely for VCell:

- **PRs are near-useless before 2022-H2.** Only **~60 PRs total across 2018–2022**, then
  **226 in 2022-H2 alone**. So the **2018–2022 era is reconstructed from releases, tags,
  commits, and diffs**, not PRs. `vcell` alone has **481 releases** (and ~670 tags
  all-time) carrying that early signal.
- **Many repos do real work via direct commits**, leaving a PR history that is empty or
  only dependency automation. Eleven in-scope repos have **0 PRs** yet ship real code
  (`vcell-messaging`, `vcell-expressionparser`, `vcell-stochastic`, `vcell-nfsim`,
  `vcellwordpress`, `vcdb`, `vcell-api-client`, `vcell-bioformats`, `biomodelsdb_mirror`,
  `PythonHPCBatchScript`, `usermaterials`). These are analyzed via **commits, releases,
  and the `contributors` API**, not PRs.
- **The project board is a partial scaffold.** It begins 2022-07 (818 issues + 400 PRs)
  but only **~36 items are Classified** and **~165 Epic-tagged**, almost all on `vcell`.
  It is useful for thematic Epics in the recent era and otherwise cross-checked against
  diffs.

PR titles, PR bodies, and the board are treated as *leads*. Where a lead disagreed with
the diff, the diff won — and several such corrections were folded back into `catalog.md`
(see [§7](#7-limitations--caveats)).

## 3. Deliverable shape

```
docs/retrospective/
  00-executive-summary.md     ← readable top-level report (eras, numbers, people)
  01-timeline.md              ← org-wide chronological narrative + key transitions
  catalog.md                  ← code-first repo catalog (descriptions, stats, caveats)
  METHODOLOGY.md              ← this file
  repos/<repo>.md             ← one detailed entry per repo or repo-group (15 files)
  data/                       ← raw gh/jq snapshots (provenance, regenerable)
  DECISIONS.md                ← working decision log (scope, grouping, corrections)
  HANDOFF.md                  ← autonomous-resume notes
```

Each `repos/*.md` follows a consistent template:

1. One-line description + a header line (group · PR count · releases/tags · active span · key contributors)
2. **Project background** — code-first, 2–4 sentences (from README/structure, not PRs)
3. **Timeline (themed milestones)** — grouped by era/half-year, narrative with inline
   `#PR`, tag, and commit-hash citations
4. **Notable PRs / commits** and **key contributors**
5. **Tech & stack notes**

The top-level docs (`00`, `01`, `catalog`) are **synthesized from** the per-repo entries,
so summary and detail cannot drift.

## 4. Phases

**Phase 0 — data foundation.** Inventory both orgs and snapshot all raw metadata via
`gh` + `jq` into `data/` (repos, PRs since cutoff, contributors, releases, READMEs,
languages, and the full project board). Committed as **regenerable, auditable**
provenance so every claim can be re-derived without re-hitting the API. Phase 0 also
sizes the effort and surfaces the findings that *drive the method* — the PR-by-half-year
histogram that exposes the eras, and the list of 0-PR direct-commit repos.

**Phase 1a — code-first catalog.** Write `catalog.md`: a description of every in-scope
repo authored from the **repo itself** (README, structure, languages, releases),
deliberately *independent of PR narratives*, plus an at-a-glance stats row. This anchors
"what each repo is" before any PR-driven storytelling.

**Phase 1b — per-repo diff-led timelines.** For each repo (or repo-group), produce
`repos/<repo>.md` by working from the `data/` snapshots **and inspecting real diffs**
via `gh`/`git`, per the evidence hierarchy. PRs and the board supply leads; the diff
decides.

**Phase 2 — synthesis.** Author the top-level docs (`00-executive-summary.md`,
`01-timeline.md`, this `METHODOLOGY.md`) **last**, built **from** the finished per-repo
entries and citing them. No further `gh` calls needed for the narrative.

## 5. Execution model

Phase 1b ran as **parallel research subagents — one per repo/group, each diff-led** and
working from the committed `data/` snapshots plus live `gh`/`git` diff inspection. The
orchestrator reviewed each returned `repos/*.md`, then synthesized the Phase-2 docs from
the reviewed set so detail and summary stay coupled.

Repos were grouped to keep the entry count manageable (28 repos → **15 files**):
substantive solvers each keep their own entry; small, tightly-related repos are folded
(e.g. the two shared C++ libs into `solver-shared-libs.md`, the cam-center pair into
`langevin-springsalad.md`, web repos into `web-presence.md`, deployment/data repos into
`infrastructure.md`). The grouping rationale is logged in `DECISIONS.md`.

This run was **autonomous (no mid-run check-ins)** — resumed from `HANDOFF.md` and
carried to completion, with all non-trivial choices and diff-discovered corrections
recorded in `DECISIONS.md` for owner review rather than blocking on approval.

## 6. Data provenance & regeneration

All raw data lives in [`data/`](data/) (see [`data/README.md`](data/README.md) for the
file manifest). **Captured 2026-06-25.** It is read-only against GitHub and re-runnable.
Representative commands:

```bash
ORG=virtualcell; CUTOFF=2018-09-01; OUT=docs/retrospective/data

# Org inventory
gh repo list $ORG --limit 300 \
  --json name,description,isArchived,isFork,pushedAt,createdAt,primaryLanguage,diskUsage \
  > $OUT/repos_virtualcell.json

# All PRs since cutoff, per repo
gh pr list -R $ORG/<repo> --state all --limit 1000 --search "created:>=$CUTOFF" \
  --json number,title,state,createdAt,mergedAt,closedAt,author,labels,additions,deletions,changedFiles,baseRefName \
  > $OUT/prs/<repo>.json

# Commit counts (cross-checks PR authorship for direct-commit repos)
gh api repos/$ORG/<repo>/contributors > $OUT/contributors/<repo>.json

# Releases, README, languages
gh api repos/$ORG/<repo>/releases --paginate > $OUT/releases/<repo>.json
gh api repos/$ORG/<repo>/readme --jq .content | base64 -d > $OUT/readmes/<repo>.md
gh api repos/$ORG/<repo>/languages > $OUT/langs/<repo>.json
```

Sanity checks used before trusting the data:

```bash
# org-wide PR volume by half-year — reveals the eras (and the 2022-H2 inflection)
cat $OUT/prs/*.json | jq -r '.[] | .createdAt[0:4] + "-H" +
  (if (.createdAt[5:7]|tonumber)<=6 then "1" else "2" end)' | sort | uniq -c

# substantive (non-bot, non-release) PRs for one repo: date | #num | author | files±add/del | title
jq -r '[.[] | select((.author.login // "")|test("bot|renovate|dependabot|semantic")|not)
        | select(.title|test("chore\\(release\\)")|not)]
  | sort_by(.createdAt) | .[]
  | "\(.createdAt[0:10]) | #\(.number) | \(.author.login) | \(.changedFiles)f +\(.additions)/-\(.deletions) | \(.title)"' \
  $OUT/prs/<repo>.json
```

> **`vcell` releases are capped at 100** in `data/releases/vcell.json` (the GitHub
> releases API page). The true picture is **~670 tags all-time (~310 in-window)** — use
> **`git tag`** in a clone for the full set; the JSON is a recent slice only.

## 7. Limitations & caveats

- **Diffs are sampled, not exhaustive, on the largest repos.** `vcell` (821 PRs, 481
  releases) and the long-tail solver history were analyzed by reading **pivotal** diffs
  (largest changes, feature PRs, release boundaries), not every commit. Claims rest on
  representative evidence.
- **Contributor counts are all-time.** They come from the GitHub `contributors` API and
  include **pre-cutoff and pre-split history**. For the extracted solver repos this means
  the top contributors often reflect *inherited* `vcell-solvers` history (e.g. fgao15 /
  gweatherby) rather than who did the post-split work — called out per entry.
- **Project board coverage is partial** (2022-07+, mostly `vcell`, ~36 Classified) and
  was used only as a corroborating hint.
- **Some repos have empty/stub READMEs** and were characterized **only from commits and
  diffs** (e.g. `vcdb`, `usermaterials`, `vcell-messaging`, `vcell-expressionparser`).
- **Diff-discovered corrections to first-pass numbers** were folded back into
  `catalog.md`; the full list is in `DECISIONS.md`. Notable examples: the solver
  extraction was **staggered ~2 years (2024–2026), not a single 2024–25 event**; the
  shared-libs split was only **partly adopted** (some solvers still vendor the sources
  inline); `pyvcell` originates **2024-08**, not 2025; `VCell-AI` uses **OpenAI/Azure
  OpenAI**, not Anthropic; `vcdb` is a **data repo of exported BioModels**, not schema
  tooling; `libvcell` ships via **GraalVM native-image**, not JNI/JPype.

## 8. How to reproduce or update

1. **Re-snapshot Phase 0** with the commands in [§6](#6-data-provenance--regeneration);
   point `OUT` at a scratch dir first if you want to diff against the committed `data/`
   before overwriting it.
2. **Re-derive counts** from `data/prs/*.json` with the `jq` filters above; for `vcell`
   tags/releases beyond the 100 cap, use `git tag` in a clone.
3. **Refresh a repo entry**: re-run the substantive-PR filter and inspect the new diffs
   (`gh pr view <N> -R virtualcell/<repo> --json title,body`, `git log`/`git show`),
   then update `repos/<repo>.md` against the **diffs**, not the messages.
4. **Re-synthesize** `catalog.md`, `01-timeline.md`, and `00-executive-summary.md` from
   the per-repo entries so the top-level docs never drift from the detail.
5. Record any new scope/grouping decisions or diff-discovered corrections in
   `DECISIONS.md`.
