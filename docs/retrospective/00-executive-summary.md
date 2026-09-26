# VCell Org Retrospective — Executive Summary

**Virtual Cell (VCell)** is a computational cell-biology modeling-and-simulation
platform developed at UConn Health (CCAM) since 1997: a desktop client plus a free
shared client/server system at vcell.org providing a centralized model database,
HPC cluster compute, and shared storage, supporting biochemical-network and
rule-based models, electrophysiology, and compartmental and spatial
(cellular-geometry) simulation across ODE, reaction-diffusion-advection PDE,
Gillespie/hybrid stochastic, network-free, Langevin particle, and moving-boundary
methods. This retrospective covers GitHub-era development from **September 2018
through June 2026** across the **`virtualcell`** and **`cam-center`** (Langevin /
SpringSaLaD) organizations — 28 in-scope repositories grouped into 15 detailed
[per-repo entries](repos/) — written **code-first** (from diffs, releases, tags,
and READMEs as ground truth, with PRs and the project board as corroborating
hints). See [`METHODOLOGY.md`](METHODOLOGY.md) for method and [`DECISIONS.md`](DECISIONS.md)
for working decisions.

---

## Eras

The development arc falls into roughly six overlapping eras. Because VCell only
adopted a PR-based workflow around 2022, the earlier eras are reconstructed from
releases, tags, and commit diffs rather than PRs.

**2018–2022-H1 — Pre-PR server-modernization tail and the BioSimulators/CLI seed.**
The Docker-microservices server (model database, JMS messaging, SLURM-backed
cluster compute) was largely in place before the 2018-09 cutoff; visible work is
its consolidation plus the genuinely new **CLI + BioSimulators arc** that made
VCell a registered BioSimulators engine — OMEX/SED-ML headless execution,
HDF5/CSV report fidelity, and the `vcell_cli_utils` Python helper. Almost no PRs
(~60 across four years); reconstructed from releases (the 7.2–7.4 lines) and
diffs. See [`repos/vcell.md`](repos/vcell.md), [`repos/python-clients-utils.md`](repos/python-clients-utils.md).

**2022-H2 — PR/project-board workflow adoption.** The single biggest process
inflection: ~226 PRs in one half-year (vs. ~60 before), as the team adopted
PR-based development and an org project board whose epics became thematic anchors.
The flagship technical thread is the start of the **Oracle → PostgreSQL**
migration. See [`repos/vcell.md`](repos/vcell.md).

**2023 — The modern-stack quarter (Quarkus REST + three OpenAPI clients +
`webapp-ng`).** In 2023-Q4 the legacy Restlet API (`/api/v0/`) was joined by a new
**Quarkus `vcell-rest` service** (`/api/v1/`) with SmallRye OpenAPI, driving three
auto-generated clients (Java, Python, TypeScript-Angular) and the brand-new
Angular **`webapp-ng`**, with Auth0/Keycloak auth. One quarter introduced four
current top-level modules. See [`repos/vcell.md`](repos/vcell.md).

**2024–2026 — Solver extraction and the Python/GraalVM scriptable stack.** Two
intertwined threads. (1) The C/C++/Fortran solvers were **extracted from the
`vcell-solvers` monorepo into per-solver repos** (each with a CMake build,
pybind11 binding, and wheel pipeline) — a *staggered ~2-year* effort, not a single
event: `vcell-fvsolver` (2024-05), `vcell-ode` (2025-06), then
`vcell-nfsim`/`vcell-stochastic`/`vcell-mbsolver`/`vcell-messaging`/`vcell-expressionparser`
through 2026. (2) A **Python front door** emerged: `pyvcell` (origin 2024-08)
consuming the solver wheels, backed by `libvcell` (VCell's Java core compiled to a
native shared library via **GraalVM native-image** + ctypes). See
[`repos/vcell-solvers.md`](repos/vcell-solvers.md), [`repos/pyvcell.md`](repos/pyvcell.md),
[`repos/libvcell.md`](repos/libvcell.md).

**2024–2026 — Langevin/SpringSaLaD integration → VCell 8.0 GA.** In parallel, the
`cam-center` **LangevinNoVis01** particle solver was modernized (Maven + GraalVM
native image, JMS messaging) and integrated into VCell as the "Langevin"
application, with the **SpringSaLaD** desktop app rebuilt on Maven; this culminated
in the **7.7 → 8.0.0** major bump (2026-05) enabling Langevin/SpringSaLaD by
default. See [`repos/langevin-springsalad.md`](repos/langevin-springsalad.md),
[`repos/vcell.md`](repos/vcell.md).

**2025–2026 — AI / agent-driven experiments and web-presence modernization.**
**VCell-AI** (Google Summer of Code 2025) — an OpenAI/Azure-OpenAI LLM chatbot with
tool-calling over the VCell API plus a Qdrant RAG store of *tutorial docs* — and
**`vcell-fenics`** (June 2026), an experimental, overwhelmingly Claude-Code
agent-driven DOLFINx/FEniCSx finite-element backend that ingests VCML and
cross-validates against VCell's solvers. Alongside, web presence moved to
Kubernetes (`vcellwordpress`). See [`repos/VCell-AI.md`](repos/VCell-AI.md),
[`repos/vcell-fenics.md`](repos/vcell-fenics.md), [`repos/web-presence.md`](repos/web-presence.md).

---

## By the numbers

- **28 repositories** across **2 orgs** (`virtualcell` + `cam-center`).
- **~1,155 in-scope PRs** (1,074 non-bot) since 2018-09; `vcell` dominates at 821
  (740 non-bot).
- **`vcell` tags: ~670 all-time, ~310 since 2018-09** — the `releases.json`
  snapshot is capped at 100, so `git tag` is the true picture (the catalog's "481"
  reflects the GitHub-releases view, not the full tag history). VCell ships on a
  fast continuous-delivery cadence (4-part `MAJOR.MINOR.PATCH.BUILD` versioning).
- **PRs are near-useless before 2022-H2** and the era curve reflects workflow
  adoption, not effort:

  | Half-year | In-scope PRs (approx.) |
  |---|--:|
  | 2018-H2 → 2022-H1 | ~0–60 total |
  | 2022-H2 | 226 |
  | 2023-H1 / 2023-H2 | 97 / 75 |
  | 2024-H1 / 2024-H2 | 123 / 90 |
  | 2025-H1 / 2025-H2 | 135 / 34 |
  | 2026-H1 | 241 |

  The 2026-H1 spike is heavily one repo and one mode: `vcell-fenics`'s 104 PRs are
  a single ~11-day agent-driven sprint by one author.
- **Project board** (org Project #1, started 2022-07): 818 issues + 400 PRs, but
  only a partial scaffold (~36 Classified, ~165 Epic-tagged), almost all `vcell`.

---

## Compact repo catalog

Grouped; each repo links to its detailed entry. (PRs = non-bot.)

**1. Core monorepo** — [`repos/vcell.md`](repos/vcell.md)
- **`vcell`** — the flagship Java monorepo: desktop client, server, REST APIs
  (Quarkus `/api/v1/` + legacy Restlet `/api/v0/`), CLI, VCML. *821 PRs · ~670 tags.*

**2. Numerical solvers** — [`repos/vcell-solvers.md`](repos/vcell-solvers.md) (extraction hub), [`repos/vcell-fvsolver.md`](repos/vcell-fvsolver.md), [`repos/vcell-ode-mbsolver.md`](repos/vcell-ode-mbsolver.md), [`repos/vcell-stochastic-nfsim.md`](repos/vcell-stochastic-nfsim.md), [`repos/solver-shared-libs.md`](repos/solver-shared-libs.md), [`repos/vcell-fenics.md`](repos/vcell-fenics.md)
- **`vcell-solvers`** — legacy C/C++/Fortran solver monorepo (frozen at v0.8.2, Nov 2024).
- **`vcell-fvsolver`** — finite-volume PDE + bundled Smoldyn; `pyvcell-fvsolver` wheel.
- **`vcell-ode`** / **`vcell-mbsolver`** — SUNDIALS ODE; FronTier moving-boundary (Linux/macOS-only).
- **`vcell-stochastic`** / **`vcell-nfsim`** — Gibson/Gillespie; NFsim v1.11 fork (both direct-commit).
- **`vcell-messaging`** / **`vcell-expressionparser`** — shared C++ libs split out as submodules.
- **`vcell-fenics`** — experimental, agent-driven DOLFINx FEM backend (June 2026).

**3. Langevin / SpringSaLaD (cam-center)** — [`repos/langevin-springsalad.md`](repos/langevin-springsalad.md)
- **`LangevinNoVis01`** — particle-based spatial-stochastic solver (Java + GraalVM native).
- **`SpringSaLaD`** — its model-building/visualization desktop app (Java/Maven).

**4. Python ecosystem** — [`repos/pyvcell.md`](repos/pyvcell.md), [`repos/libvcell.md`](repos/libvcell.md), [`repos/python-clients-utils.md`](repos/python-clients-utils.md)
- **`pyvcell`** — the scriptable Python front door (PyPI `pyvcell`; origin 2024-08).
- **`libvcell`** — VCell Java core as a GraalVM native shared library for `pyvcell`.
- **`vcell-api-client`** / **`vcell_cli_utils`** / **`PythonHPCBatchScript`** — small satellite repos (mostly superseded/absorbed).

**5. Integrations & ImageJ** — [`repos/vcell-fiji.md`](repos/vcell-fiji.md)
- **`vcell-fiji`** — Fiji/ImageJ plugin reading N5-over-S3 simulation results.
- **`vcell-bioformats`** — archived Bio-Formats image service (no longer used).

**6. AI & web presence** — [`repos/VCell-AI.md`](repos/VCell-AI.md), [`repos/web-presence.md`](repos/web-presence.md)
- **`VCell-AI`** — GSoC 2025 LLM-over-biomodels platform (Next.js + FastAPI + Qdrant).
- **`CompCellBio`** — Computational Cell Biology community/workshop Angular site.
- **`vcellwordpress`** — Kustomize/K8s deployment for vcell.org WordPress (cutover staged).

**7. Infrastructure, deployment & test data** — [`repos/infrastructure.md`](repos/infrastructure.md)
- **`vcell-fluxcd`** — FluxCD/Kustomize GitOps backbone of the K8s deployment.
- **`devops`** — Ansible service-restart tooling (superseded).
- **`vcdb`** — **data** repo of exported published BioModels (OMEX/SBML/VCML) for export-regression.
- **`biomodelsdb_mirror`** / **`usermaterials`** / **`test_suite`** — BioModels mirror; tutorial assets; historical regression-report generator.

---

## Key people

- **jcschaff** (Jim Schaff) — project lead/owner; architecture and most pivotal
  arcs across the org: Quarkus REST, OpenAPI client generation, Postgres
  migration, the solver-extraction strategy, `libvcell`/`pyvcell`, parameter
  estimation rebuild, `vcell-fluxcd`, and the `vcell-fenics` experiment.
- **danv61** (Dan Vasilescu) — modeling/solver integration in `vcell`; primary
  maintainer of LangevinNoVis01/SpringSaLaD (binding kinetics, cluster analysis);
  drove the `vcdb` export corpus.
- **gweatherby** (Gerard Weatherby) — deep solver build infrastructure and code in
  `vcell-solvers` (pre-split); the long-standing NFsim/messaging integration.
- **fgao15** — solver internals (ExpressionParser, FV/stochastic numerics); the
  largest pre-split authorship in `vcell-solvers`.
- **moraru** (Ion Moraru) — model/UI contributions in `vcell` and SpringSaLaD.
- **bontempiuchc** (Chris Bontempi) — owns the per-solver extraction packaging:
  `vcell-stochastic`, `vcell-nfsim`, `vcell-mbsolver` C++ code, and the shared libs.
- **CodeByDrescher** (Logan Drescher) — CLI/BioSimulations overhaul and error
  architecture in `vcell`; `pyvcell`/`libvcell` expression translation and build
  modernization; `vcell-ode` and `vcell-fvsolver` toolchain work.
- **AlexPatrie** — early `pyvcell` high-level wrappers and VTK/trame visualization.
- **Ezequiel-Valencia** — `webapp-ng` and Auth0/OIDC in `vcell`; primary
  `vcell-fluxcd` engineer; the `vcell-fiji` plugin; SpringSaLaD installer pipeline.
- **KacemMathlouthi** — GSoC 2025 student; built essentially all of VCell-AI.
- **pjmichalski** (Paul Michalski) + **moraru** — original SpringSaLaD algorithm
  (Michalski & Loew 2016).
- **smstaurovsky** — dominant maintainer of the CompCellBio community site.
- **GMarupilla** (Gajendra Marupilla) — the founding CLI/BioSimulators/OMEX work
  (2020–2021); top committer of `vcell_cli_utils`.
- **vcfrmgit** — release/service automation account (tags, deploy bookkeeping).
