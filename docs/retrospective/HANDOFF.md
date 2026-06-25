# Retrospective — Autonomous Resume / Handoff

> **For a resumed Claude session: this is your brief. Proceed AUTONOMOUSLY and
> NO-QUESTIONS-ASKED.** All scope decisions below are final. Do not call
> AskUserQuestion. Do not pause for check-ins. Make sensible defaults, note any
> assumptions inline in the docs, and carry the whole task to a committed draft PR.
> Delete this HANDOFF.md as the final step before opening the PR.

## Goal

A comprehensive historical retrospective + feature timeline of the **Virtual Cell**
project across the **`virtualcell`** GitHub org and the **`cam-center`** org
(Langevin + SpringSaLaD), since **2018-09-01**. Modeled on the BioSimulations org
retrospective (PR biosimulations/biosimulations#4895) and its `METHODOLOGY.md`.

## Final scope decisions (do not revisit)

- **Cutoff:** 2018-09-01. Pre-cutoff = background only (VCell dates to 1997).
- **Orgs/repos:** all active non-fork `virtualcell` repos (grouped), plus
  `cam-center/LangevinNoVis01` and `cam-center/SpringSaLaD`. Forks noted but NOT
  deep-dived (`openapi-generator`, `vcell-jsbml`, `modelbricks-webapp`,
  `vcellMichael`, `Biosimulators_utils`, `GoogleSummerOfCode`).
- **Depth:** DEEP — read PR bodies AND inspect diffs for pivotal PRs.
- **Breadth:** all active repos covered; trivial/empty repos grouped & kept short.
- **Execution:** originally "serial with check-ins" — now **autonomous, no check-ins**.
- **Output location:** `docs/retrospective/` in the `vcell` repo, on branch
  `big-docs-task` (worktree: `/Users/jimschaff/Documents/workspace/vcell-docs`).
  Finish with a **draft PR** to `master` (merge commit, never squash).

## Evidence hierarchy (project owner's explicit guidance — CRITICAL)

PR titles/descriptions AND the project board are **inconsistent / low quality**.
**Diffs are ground truth.** When in doubt, read the diff. Order of trust:

1. **Code & diffs** (ground truth)
2. **Releases / tags / CHANGELOG** (what shipped, when)
3. **Repo README / structure / languages** (what each repo *is*)
4. **Project board #1 "vcell development"** (Epic/Classification/Status/Iteration;
   ~2022→now only; partial — cross-check vs diffs)
5. **PR & commit messages** (lowest trust — verify against diffs)

Workflow: **summarize each repo first (from the repo, not PRs), then build each
timeline diff-led.** PRs are near-useless before 2022-H2 (only ~60 PRs across
2018–2022) — reconstruct that era from `vcell`'s 481 releases, tags, and commits.

## Deliverable shape (BioSimulations template)

```
docs/retrospective/
  00-executive-summary.md   ← eras, by-the-numbers, compact catalog, key people  [TODO]
  01-timeline.md            ← org-wide chronological narrative + "Key transitions" [TODO]
  catalog.md                ← repo catalog w/ code-first summaries + stats        [DONE]
  METHODOLOGY.md            ← how this was produced (adapt BioSim's for VCell)    [TODO]
  HANDOFF.md                ← this file (DELETE before final PR)
  repos/<repo>.md           ← one detailed entry per repo/group                   [TODO: 1 exemplar in progress]
  data/...                  ← raw provenance JSON                                  [DONE]
```

Per-repo entry template (each `repos/*.md`):
1. One-line description + header line (group · PR count · releases · active span · key contributors)
2. **Project background** (2–4 sentences, code-first)
3. **Timeline (themed milestones)** — grouped by half-year/era, narrative with inline `#PR`/tag links, DIFF-LED
4. **Notable PRs/commits** table (link · date · author · why-it-matters, verified via diff)
5. **Key contributors**
6. **Tech & stack notes**

Top-level docs are synthesized FROM the per-repo entries so detail and summary never drift.

## Status

- [x] **Phase 0 — data foundation** (`data/`): PR metadata, contributors,
  releases, READMEs, languages for all 28 in-scope repos; org Project #1 (1,220
  items) in `data/project_items.json`; inventories; `data/README.md` provenance.
- [x] **Phase 1a — code-first repo summaries**: all 28 in `catalog.md` (7 groups).
- [ ] **Phase 1b — per-repo themed timelines** (`repos/*.md`): diff-led. NOT STARTED
  except `vcell-fiji` exemplar (in progress). **This is the bulk of remaining work.**
- [ ] **Phase 2 — synthesis + ship**: `00-executive-summary.md`, `01-timeline.md`,
  `METHODOLOGY.md`; delete HANDOFF.md; commit; open draft PR.

## Key facts already established (don't re-derive)

- ~1,155 in-scope PRs (1,074 non-bot); `vcell` dominates (821 PRs, 481 releases).
- **PR-by-half-year eras:** ~0 (2018–2022-H1) → 226 (2022-H2) → 97/75 (2023) →
  123/90 (2024) → 135/34 (2025) → 241 (2026-H1). The 2022-H2 jump = adoption of a
  PR/project-board workflow, NOT the start of activity.
- **Solver extraction:** C/C++ solvers were split OUT of the `vcell-solvers`
  monorepo (54 releases) into `vcell-fvsolver`, `vcell-stochastic`, `vcell-nfsim`,
  `vcell-ode`, `vcell-mbsolver` (2024–2025), each w/ CMake + pybind11 + wheels.
  Their `contributors` counts include pre-split history (fgao15, gweatherby).
- **Direct-commit repos (0 PRs, real work — use commits/diffs):** `vcell-stochastic`,
  `vcell-nfsim`, `vcell-messaging`, `vcell-expressionparser`, `vcell-api-client`,
  `PythonHPCBatchScript`, `vcellwordpress`, `vcdb`, `usermaterials`, `biomodelsdb_mirror`.
- **Empty/stub READMEs needing diff-confirmation:** `vcell-fenics` (brand-new
  June-2026 FEniCS effort), `vcdb`, `devops`, `usermaterials`, `libvcell` (cookiecutter
  stub but it's Java core algos backing pyvcell), `vcell-messaging`, `vcell-expressionparser`.
- **Project board Epics** (good thematic anchors for 2022+): ui fixes, export
  SBML/Omex, imageJ MVP (vcell-fiji), biosimulators (non/spatial), user docs/training,
  migrate to postgresql, improve NFSim statistics, keycloak, vcell-rest service,
  quarkus_endpoints, solver builds/regression/unit-tests, spring salad, local field data.
- **Key people:** jcschaff (Jim Schaff, lead/owner), danv61, gweatherby (Gerard
  Weatherby), fgao15 (Fei Gao), moraru (Ion Moraru), bontempiuchc (solvers),
  AlexPatrie, CodeByDrescher, Ezequiel-Valencia (fiji/infra), KacemMathlouthi
  (VCell-AI/GSoC), vcellmike, smstaurovsky, pjmichalski, ctrueden (bioformats),
  bilalshaikh42. `vcfrmgit` is a service/release account (treat as automation).

## Repo groups (final)

1. **Core monorepo** — `vcell`
2. **Numerical solvers** — `vcell-solvers`, `vcell-fvsolver`, `vcell-stochastic`,
   `vcell-nfsim`, `vcell-ode`, `vcell-mbsolver`, `vcell-messaging`,
   `vcell-expressionparser`, `vcell-fenics`
3. **Langevin / SpringSaLaD (cam-center)** — `LangevinNoVis01`, `SpringSaLaD`
4. **Python ecosystem** — `pyvcell`, `libvcell`, `vcell-api-client`,
   `vcell_cli_utils`, `PythonHPCBatchScript`
5. **Integrations & ImageJ** — `vcell-fiji`, `vcell-bioformats`
6. **AI & web presence** — `VCell-AI`, `CompCellBio`, `vcellwordpress`
7. **Infrastructure, deployment & test data** — `vcell-fluxcd`, `devops`, `vcdb`,
   `biomodelsdb_mirror`, `usermaterials`, `test_suite`

Suggested `repos/` files: one per group for the solvers' shared/trivial members is
fine (e.g. fold `vcell-messaging`+`vcell-expressionparser` into a shared-libs note,
fold trivial infra repos into one entry), but give `vcell`, `pyvcell`, `vcell-fiji`,
`VCell-AI`, the Langevin pair, and each substantive solver their own entry.

## Useful commands (data already pulled; re-pull only if needed)

```bash
cd /Users/jimschaff/Documents/workspace/vcell-docs/docs/retrospective/data
# chrono non-bot PRs for a repo:
jq -r '[.[]|select((.author.login//"")|test("bot|renovate|dependabot|semantic-release|github-actions")|not)]|sort_by(.createdAt)|.[]|"\(.createdAt[0:10]) | #\(.number) | \(.author.login) | \(.changedFiles)f +\(.additions)/-\(.deletions) | \(.title)"' prs/<repo>.json
# releases:
jq -r 'sort_by(.published_at)|.[]|"\(.tag_name) | \(.published_at[0:10]) | \(.name)"' releases/<repo>.json
# inspect a PR diff (GROUND TRUTH):
gh pr view <N> -R <org>/<repo> --json title,files | jq -r '.title,(.files[]|"\(.path) +\(.additions)/-\(.deletions)")'
gh pr diff <N> -R <org>/<repo>
# vcell early era (pre-PR): use tags/commits
git -C /Users/jimschaff/Documents/workspace/vcell log --oneline --since=2018-09-01 --until=2022-01-01
gh release list -R virtualcell/vcell -L 500
```

Methodology reference (the playbook this follows):
`gh api repos/biosimulations/biosimulations/contents/docs/retrospective/METHODOLOGY.md?ref=docs/org-retrospective-since-2022 --jq .content | base64 -d`
