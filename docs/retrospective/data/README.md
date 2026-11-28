# Retrospective — raw data (provenance)

Read-only snapshots pulled via `gh` CLI to make the retrospective auditable and
regenerable. Captured 2026-06-25.

## Files

| Path | What |
|---|---|
| `repos_virtualcell.json` | `gh repo list virtualcell` inventory (33 repos) |
| `repos_camcenter.json` | `gh repo list cam-center` inventory (6 repos) |
| `prs/<repo>.json` | All PRs created since 2018-09-01 (number, title, state, dates, author, labels, diff sizes, base branch) |
| `contributors/<repo>.json` | `repos/<org>/<repo>/contributors` — commit counts (cross-checks PR authorship for direct-commit repos) |
| `releases/<repo>.json` | GitHub releases (up to 100) |
| `readmes/<repo>.md` | Repo README at HEAD (source for code-first summaries) |
| `langs/<repo>.json` | GitHub language byte breakdown |
| `project_items.json` | All 1,220 items of org Project #1 "vcell development" with field values (Status, Epic, Classification, Iteration, dates) |

## Scope

- **Cutoff:** 2018-09-01. Pre-cutoff work is background, not timeline.
- **Orgs:** `virtualcell` (primary) + `cam-center` (Langevin + SpringSaLaD solvers).
- **In scope:** all active non-fork virtualcell repos (grouped), plus cam-center
  `LangevinNoVis01` and `SpringSaLaD`.
- **Noted but not deep-dived:** forks (`openapi-generator`, `vcell-jsbml`,
  `modelbricks-webapp`, `vcellMichael`, `Biosimulators_utils`, `GoogleSummerOfCode`).

## Key Phase-0 findings (drive the analysis method)

- **~1,155 in-scope PRs** (1,074 non-bot). `vcell` dominates at 821.
- **PRs are near-useless before 2022-H2** — only ~60 PRs total across 2018–2022,
  then 226 in 2022-H2 alone. The 2018–2022 era must be reconstructed from
  **releases/tags/commits/diffs**, not PRs. `vcell` has **481 releases**.
- **Project board starts 2022-07** and is a *partial* scaffold: 818 issues +
  400 PRs, but only ~36 items Classified and ~165 Epic-tagged, almost all `vcell`.
  Useful for thematic Epics in the recent era; cross-check against diffs.
- **Direct-commit repos (0 PRs, real work):** `vcell-messaging`,
  `vcell-expressionparser`, `vcell-stochastic`, `vcell-nfsim`, `vcellwordpress`,
  `vcdb`, `vcell-api-client`, `vcell-bioformats`, `biomodelsdb_mirror`,
  `PythonHPCBatchScript`, `usermaterials`. Analyze via commits/releases.
- **Solver history is split:** the C/C++ solvers were extracted out of the
  `vcell-solvers` monorepo (54 releases) into per-solver repos — early history
  lives in `vcell-solvers`, not the new repos.

## Evidence hierarchy (per project owner)

Diffs are ground truth. Order of trust: **code/diffs → releases/tags/CHANGELOG →
repo README/structure → project board (Epic/Classification) → PR & commit
messages**. PR titles/descriptions and the board are *hints* to verify against diffs.
