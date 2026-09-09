# Retrospective — Decision Log (for owner review)

Non-trivial decisions made while executing Phase 1b/2 autonomously. Listed newest
context first. (This file is internal/working — fold into METHODOLOGY.md or delete
before the final PR if not wanted in the published doc.)

## 2026-06-25 — Resume of autonomous session

### D1. `repos/` file set (grouping of 28 repos into 17 entries)
Per HANDOFF, substantive repos get their own entry; trivial/shared ones are folded.
Final set:
- `vcell.md` (Core, deep)
- `vcell-solvers.md` (legacy monorepo + the 2024–25 extraction narrative hub)
- `vcell-fvsolver.md`, `vcell-stochastic.md`, `vcell-nfsim.md`, `vcell-ode.md`,
  `vcell-mbsolver.md`, `vcell-fenics.md` (each extracted solver)
- `solver-shared-libs.md` (folds `vcell-messaging` + `vcell-expressionparser`)
- `langevin-springsalad.md` (the cam-center pair, one file)
- `pyvcell.md`, `libvcell.md` (Python, own entries)
- `python-clients-utils.md` (folds `vcell-api-client` + `vcell_cli_utils` + `PythonHPCBatchScript`)
- `vcell-fiji.md` (incl. a short `vcell-bioformats` archived-sibling section)
- `VCell-AI.md`
- `web-presence.md` (folds `CompCellBio` + `vcellwordpress`)
- `infrastructure.md` (`vcell-fluxcd` as lead + `devops`, `vcdb`, `biomodelsdb_mirror`,
  `usermaterials`, `test_suite` folded)

**Rationale:** keeps the count manageable while honoring "each substantive solver
its own entry." `vcell-fenics` kept separate despite being a one-month-old effort
because it carries 104 PRs (real activity worth its own diff-led look).

### D2. Execution model — parallel research subagents, diff-led
Each `repos/*.md` is produced by a dedicated subagent that works from the local
`data/` snapshots AND inspects real diffs via `gh`/`git` (ground truth per the
evidence hierarchy). I (the orchestrator) review each returned file, then synthesize
the Phase-2 top-level docs FROM the per-repo entries so summary and detail can't drift.
Used the Agent tool (not the Workflow tool) since multi-agent orchestration wasn't
explicitly requested — subagents are within normal latitude.

### D1b. Final file naming (minor deviation from D1)
Two solver pairs were combined into single files (small, tightly related):
`vcell-stochastic-nfsim.md` and `vcell-ode-mbsolver.md` (instead of four separate
files). Net `repos/` set = **15 files**. Everything else as planned in D1.

### D4. Material catalog.md corrections found during diff-led research
The per-repo entries carry these (verified from diffs); folding the most material
ones into `catalog.md` / the exec summary so top-level numbers don't drift:
- **Solver extraction was staggered ~2 years, not a single "2024–2025" event:**
  fvsolver 2024-05, ode 2025-06, then nfsim/stochastic/mbsolver/messaging/
  expressionparser all **2026**. The monorepo froze at v0.8.2 (Nov 2024).
- **Shared-libs split only partly adopted** — `vcell-ode` & `vcell-stochastic`
  still carry ExpressionParser/messaging sources inline (no submodule) as of 2026-06.
- **pyvcell origin is 2024-08** (release 0.0.1, seeded from monorepo `pythonData`),
  not 2025-01 (that's when PR workflow + REST client began).
- **vcell releases.json is capped at 100**; true tag count ~670 all-time (~310
  since 2018-09). Use `git tag` for the full picture.
- **vcell-fenics**: the 104 PRs are ~11 days (Jun 14–25 2026), single-author
  jcschaff, overwhelmingly **Claude-Code agent-driven**; DOLFINx 0.10.x; genuinely
  ingests VCML + cross-validates vs VCell FV/FronTier solvers. Experimental.
- **VCell-AI uses OpenAI/Azure OpenAI** (not Anthropic/Claude); RAG store ingests
  VCell **tutorial docs**, not biomodels (biomodels fetched live via API tools).
- **vcdb is a DATA repo** of exported published BioModels (OMEX/SBML/VCML) for
  export-regression + seeding biosimulations.org — NOT schema/migration tooling.
- **libvcell packaging = GraalVM native-image** (AOT `.so`/`.dylib`/`.dll` via
  `@CEntryPoint`, ctypes + GraalVM isolates), not JPype/JNI.
- **fvsolver also vendors/ships Smoldyn**; its post-split authors are jcschaff then
  CodeByDrescher (the "fgao15/gweatherby" top-3 is inherited pre-split history).
- **vcell_cli_utils** top committer is GMarupilla (Gajendra Marupilla), not
  bilalshaikh42 (who owned releases). Repo later absorbed into the monorepo.
- **vcell-api-client** superseded by pyvcell's vendored client; **CompCellBio** is
  long-lived (created 2020-12, direct-commit, smstaurovsky); **vcellwordpress**
  commits are Feb 2026 (not 2024) and the WordPress→K8s cutover is staged-not-done.

### D3. Phase-2 docs synthesized last
`00-executive-summary.md`, `01-timeline.md`, `METHODOLOGY.md` are written after all
`repos/*.md` exist, citing them. `METHODOLOGY.md` adapts BioSimulations' playbook.
