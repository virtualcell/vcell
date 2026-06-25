# VCell Org Retrospective — Repo Catalog

Master catalog of repositories in scope for the Virtual Cell retrospective
(since **September 2018**), across the **`virtualcell`** GitHub org and the
**`cam-center`** org (Langevin / SpringSaLaD). Each entry is a **code-first
summary** — written from the repo itself (README, structure, languages,
releases), independent of PR descriptions — followed by an at-a-glance stats row.
Detailed themed timelines live in [`repos/`](repos/).

> **Reading the numbers.** PR counts badly understate older and infrastructure
> work: VCell only adopted a PR-based workflow around 2022, and many repos do
> real work via direct commits to the default branch. Where that's true it's
> called out. **Diffs, releases, and tags are the ground truth**; PRs and the
> project board are corroborating hints. Contributor counts come from the GitHub
> `contributors` API (all-time commit counts) and so include pre-split history
> for the extracted solver repos.

Captured 2026-06-25. Raw data: [`data/`](data/).

---

## Groups

1. [Core monorepo](#1-core-monorepo) — `vcell`
2. [Numerical solvers](#2-numerical-solvers) — `vcell-solvers` + extracted C/C++ solvers, `vcell-fenics`
3. [Langevin / SpringSaLaD (cam-center)](#3-langevin--springsalad-cam-center) — `LangevinNoVis01`, `SpringSaLaD`
4. [Python ecosystem](#4-python-ecosystem) — `pyvcell`, `libvcell`, `vcell-api-client`, `vcell_cli_utils`, `PythonHPCBatchScript`
5. [Integrations & ImageJ](#5-integrations--imagej) — `vcell-fiji`, `vcell-bioformats`
6. [AI & web presence](#6-ai--web-presence) — `VCell-AI`, `CompCellBio`, `vcellwordpress`
7. [Infrastructure, deployment & test data](#7-infrastructure-deployment--test-data) — `vcell-fluxcd`, `devops`, `vcdb`, `biomodelsdb_mirror`, `usermaterials`, `test_suite`

Forks not deep-dived (noted for completeness): `openapi-generator`, `vcell-jsbml`,
`modelbricks-webapp`, `vcellMichael` (archived), `Biosimulators_utils` (archived),
`GoogleSummerOfCode`.

---

## 1. Core monorepo

### `vcell` — the Virtual Cell platform
The flagship Java monorepo (with Python and TypeScript components) that *is*
VCell: a desktop modeling-and-simulation client, a server providing a centralized
model database + cluster computing + shared storage, the REST APIs (a modern
Quarkus service and a legacy Restlet one), the CLI, and the VCML model
representation. It supports biochemical-network and rule-based modeling and
electrophysiology, in compartmental and spatial (cellular-geometry) form, with
ODE, reaction-diffusion-advection PDE, Gillespie/hybrid stochastic, network-free,
and moving-boundary simulation. It orchestrates the external solvers and Python
analysis tooling, and is deployed on Kubernetes. The project dates to 1997; this
repo is the GitHub-era home of essentially all of it.

`Group: Core · PRs: 821 (740 non-bot) · Releases: 481 · PR span: 2020-02→2026-06 (repo 2018→) · Top: jcschaff, danv61, vcfrmgit, gweatherby`

---

## 2. Numerical solvers

The simulation engines VCell drives. Historically these lived in one C/C++/Fortran
monorepo (`vcell-solvers`); in 2024–2025 they were **extracted into per-solver
repositories**, each gaining a standalone CMake build, a pybind11 Python binding,
and independent release/wheel pipelines (so `pyvcell` can call them directly).
Early history for every extracted solver lives in `vcell-solvers`, not the new repo.

### `vcell-solvers` — legacy solver monorepo
The original collection of all VCell numerical codes (FiniteVolume, stochastic,
NFsim, ODE, moving-boundary, plus shared `ExpressionParser`/`vcommons`/messaging),
historically built via Vagrant VMs per platform. Now largely superseded by the
extracted repos but retains the deep history and many releases.

`Group: Solvers · PRs: 17 · Releases: 54 · PR span: 2019-04→2024-11 · Top: fgao15, gweatherby, jcschaff (all-time, pre-split)`

### `vcell-fvsolver` — finite-volume PDE solver
The reaction-diffusion-advection PDE solver, extracted as a standalone CMake
project producing a CLI executable, a Docker image (`ghcr.io/virtualcell/vcell-fvsolver`),
platform release binaries, and a `pyvcell_fvsolver` Python wrapper that consumes
`.fvinput`/`.vcg` files and emits HDF5/mesh output.

`Group: Solvers · PRs: 16 · Releases: 14 · PR span: 2024-05→2026-06 · Top: (all-time, pre-split) fgao15, gweatherby, jcschaff`

### `vcell-stochastic` — stochastic solver
Standalone extraction of the VCell stochastic solver implementing Gibson (Next
Reaction Method) and Gillespie (Direct Method) algorithms for biochemical reaction
networks; ships a static lib, a `VCellStoch` executable, and `vcellstochastic_py`
bindings, with HDF5 multi-trial statistics. **0 PRs — direct-commit + release-driven.**

`Group: Solvers · PRs: 0 · Releases: 8 · Direct-commit repo · Top: bontempiuchc`

### `vcell-nfsim` — network-free solver
VCell's fork of **NFsim v1.11** (network-free rule-based stochastic simulation),
packaged as a standalone CMake executable plus a `pyvcell_nfsim` Python binding
(scikit-build-core wheels). **0 PRs — direct-commit + release-driven.**

`Group: Solvers · PRs: 0 · Releases: 1 · Direct-commit repo · Top: bontempiuchc`

### `vcell-ode` — ODE solvers
The collection of ODE numerical libraries and protocols used by VCell, extracted
with a modern Conan + CMake/Ninja toolchain and optional libcurl messaging.

`Group: Solvers · PRs: 2 · Releases: 0 · PR span: 2025-06→2026-04 · Top: (all-time, pre-split) fgao15, gweatherby, jcschaff`

### `vcell-mbsolver` — moving-boundary solver
The moving-boundary (deforming-geometry) C++ solver, extracted with bundled
FronTier/ExpressionParser/vcommons deps. One build yields a CLI binary, a static
library, and a `pyvcell_mbsolver` extension; CI builds redistributable Linux/macOS
wheels (Windows unsupported — FronTier is GCC-only).

`Group: Solvers · PRs: 7 · Releases: 4 · PR span: 2026-06 · Top: bontempiuchc, jcschaff`

### `vcell-messaging` — shared messaging library
Shared C++ messaging component (formerly the `VCellMessaging` submodule of the
solver monorepo) for live solver-progress messaging via libcurl. **0 PRs — direct-commit, stub README; confirm scope via diffs.**

`Group: Solvers · PRs: 0 · Releases: 0 · Direct-commit repo · Top: bontempiuchc`

### `vcell-expressionparser` — shared expression parser
Shared C++ math-expression-parsing component (formerly the `ExpressionParser`
submodule). **0 PRs — direct-commit, stub README; confirm scope via diffs.**

`Group: Solvers · PRs: 0 · Releases: 0 · Direct-commit repo · Top: bontempiuchc`

### `vcell-fenics` — FEniCS-based solver (experimental, new)
A very recent (June 2026) Python effort built around the FEniCS finite-element
platform — appears to be an experimental/next-generation solver direction. Stub
README; **summary to be confirmed from diffs/commits.**

`Group: Solvers · PRs: 104 · Releases: 0 · PR span: 2026-06 only · Top: jcschaff`

---

## 3. Langevin / SpringSaLaD (cam-center)

### `LangevinNoVis01` — Langevin particle solver
The particle-based, spatial, stochastic Langevin-dynamics solver (Java) that powers
SpringSaLaD. Integrated into VCell as the "Langevin" application/solver. Distributed
as cross-platform releases.

`Group: Langevin · PRs: 23 · Releases: 21 · PR span: 2023-07→2026-06 · Top: danv61, jcschaff, pjmichalski`

### `SpringSaLaD` — particle-based simulation app
Java implementation of the SpringSaLaD algorithm — a spatially resolved, stochastic,
particle-based biochemical simulator based on the Langevin equation (Michalski &
Loew, 2016), sitting on top of the `LangevinNoVis01` solver. Distributed as
cross-platform releases via vcell.org/ssalad.

`Group: Langevin · PRs: 10 · Releases: 19 · PR span: 2024-05→2026-02 · Top: danv61, moraru, Ezequiel-Valencia`

---

## 4. Python ecosystem

### `pyvcell` — Python API for VCell
The Python package (PyPI: `pyvcell`) for local scripting of spatial modeling,
simulation, data analysis and visualization, plus access to VCell remote APIs.
Loads/saves VCML and SBML (and Antimony), edits models programmatically, runs
local spatial simulations, and analyzes results as NumPy/Zarr/VTK with Matplotlib
& PyVista. The modern, scriptable front door to VCell technology.

`Group: Python · PRs: 49 · Releases: 35 · PR span: 2025-01→2026-06 · Top: jcschaff, AlexPatrie, CodeByDrescher`

### `libvcell` — VCell core algorithms as a library
A subset of VCell's (Java) algorithms packaged as a library to back `pyvcell` —
e.g. VCML/SBML handling and model transforms — exposed for the Python stack. (The
README is a cookiecutter stub; specifics to be confirmed from diffs.)

`Group: Python · PRs: 23 · Releases: 20 · PR span: 2025-03→2026-06 · Top: jcschaff, CodeByDrescher`

### `vcell-api-client` — generated Python REST client
Auto-generated (OpenAPI Generator) Python client for the VCell REST API
(`/api/v1/…`): BioModels, MathModels, Publications, Users, Admin/usage. Published
to PyPI as `vcell-api-client`. **0 PRs — generated + direct-commit.**

`Group: Python · PRs: 0 · Releases: 0 · Generated/direct-commit · Top: jcschaff`

### `vcell_cli_utils` — CLI Python utilities
Python helper utilities used by the VCell CLI (BioSimulators-style OMEX/SED-ML
execution and report generation). Published to PyPI as `vcell-cli-utils`.

`Group: Python · PRs: 1 · Releases: 6 · PR span: 2021-06 · Top: bilalshaikh42`

### `PythonHPCBatchScript` — HPC batch runner
Small Python utility to create SLURM jobs that batch-process a directory of OMEX
archives through VCell on the UConn CCAM HPC center. **0 PRs — direct-commit.**

`Group: Python · PRs: 0 · Releases: 0 · Direct-commit repo · Top: CodeByDrescher`

---

## 5. Integrations & ImageJ

### `vcell-fiji` — Fiji/ImageJ plugin
A Fiji (ImageJ) plugin ("VCell View Simulation Results") that reads VCell spatial
simulation results exported in the **N5** format from VCell servers and opens them
in Fiji for analysis. Distributed via a Fiji update site.

`Group: Integrations · PRs: 23 · Releases: 5 · PR span: 2023-07→2025-04 · Top: Ezequiel-Valencia, jcschaff, paulricky`

### `vcell-bioformats` — BioFormats image service (archived)
ImageDataset service implementation based on the BioFormats library, used for
importing microscopy image data. **Archived — no longer used in VCell** (kept for
history; may be removed). **0 PRs.**

`Group: Integrations · PRs: 0 · Releases: 0 · Archived · Top: ctrueden, jcschaff, vcfrmgit`

---

## 6. AI & web presence

### `VCell-AI` — AI platform for biomodel discovery
An AI-powered platform (Google Summer of Code) for discovering/analyzing VCell
biomodels: a Next.js/TypeScript frontend + FastAPI/Python backend, an LLM chatbot
with tool-calling over the VCell API, and a Qdrant vector knowledge base (RAG).

`Group: AI/Web · PRs: 34 · Releases: 9 · PR span: 2025-08→2026-06 · Top: KacemMathlouthi, Ezequiel-Valencia, vcellmike`

### `CompCellBio` — Computational Cell Biology web app
An Angular web application (course/community site for computational cell biology —
to be confirmed from diffs).

`Group: AI/Web · PRs: 4 · Releases: 0 · PR span: 2023-12→2024-01 · Top: smstaurovsky, vcellmike, AlexPatrie`

### `vcellwordpress` — vcell.org WordPress on Kubernetes
Kubernetes deployment artifacts (Kustomize + Bitnami Helm chart + sealed-secrets)
for the vcell.org WordPress site, migrating it off a legacy VM. **0 PRs — direct-commit.**

`Group: AI/Web · PRs: 0 · Releases: 0 · Direct-commit repo · Top: jcschaff`

---

## 7. Infrastructure, deployment & test data

### `vcell-fluxcd` — GitOps Kubernetes config
The FluxCD/Kustomize Kubernetes configuration (HCL + Shell) that deploys VCell —
overlays for prod/stage/dev and the ingress routing the REST/legacy APIs. The
operational backbone of the modern deployment.

`Group: Infra · PRs: 19 · Releases: 0 · PR span: 2024-05→2026-06 · Top: jcschaff, Ezequiel-Valencia`

### `devops` — devops scripts/config
DevOps tooling/config. Stub README; **scope to be confirmed from diffs.**

`Group: Infra · PRs: 1 · Releases: 0 · PR span: 2023-10 · Top: Ezequiel-Valencia, mpw6`

### `vcdb` — database tooling
Database (Shell) tooling for VCell — likely schema/migration/backup utilities.
Empty README; **scope to be confirmed from diffs.** **0 PRs — direct-commit.**

`Group: Infra · PRs: 0 · Releases: 0 · Direct-commit repo · Top: danv61, CodeByDrescher, jcschaff`

### `biomodelsdb_mirror` — regression-test data mirror
A local mirror of BioModels database content used by VCell regression testing to
avoid latency/bandwidth from the official site. Data repo. **0 PRs.**

`Group: Infra · PRs: 0 · Releases: 0 · Data repo · Top: jcschaff`

### `usermaterials` — user materials
User-facing materials (training/example assets — to be confirmed from diffs).
Empty README. **0 PRs — direct-commit.**

`Group: Infra · PRs: 0 · Releases: 0 · Direct-commit repo · Top: vcellmike, ACowan0105, mpw6`

### `test_suite` — regression report generation
Python/Shell regression test-suite report generation. Largely historical (2021).

`Group: Infra · PRs: 1 · Releases: 12 · PR span: 2021-01 · Top: (n/a)`

---

## Stats summary

| Repo | Group | PRs | non-bot | Releases | PR span |
|---|---|--:|--:|--:|---|
| vcell | Core | 821 | 740 | 481 | 2020-02→2026-06 |
| vcell-fenics | Solvers | 104 | 104 | 0 | 2026-06 |
| pyvcell | Python | 49 | 49 | 35 | 2025-01→2026-06 |
| VCell-AI | AI/Web | 34 | 34 | 9 | 2025-08→2026-06 |
| vcell-fiji | Integrations | 23 | 23 | 5 | 2023-07→2025-04 |
| libvcell | Python | 23 | 23 | 20 | 2025-03→2026-06 |
| LangevinNoVis01 | Langevin | 23 | 23 | 21 | 2023-07→2026-06 |
| vcell-fluxcd | Infra | 19 | 19 | 0 | 2024-05→2026-06 |
| vcell-solvers | Solvers | 17 | 17 | 54 | 2019-04→2024-11 |
| vcell-fvsolver | Solvers | 16 | 16 | 14 | 2024-05→2026-06 |
| SpringSaLaD | Langevin | 10 | 10 | 19 | 2024-05→2026-02 |
| vcell-mbsolver | Solvers | 7 | 7 | 4 | 2026-06 |
| CompCellBio | AI/Web | 4 | 4 | 0 | 2023-12→2024-01 |
| vcell-ode | Solvers | 2 | 2 | 0 | 2025-06→2026-04 |
| devops | Infra | 1 | 1 | 0 | 2023-10 |
| vcell_cli_utils | Python | 1 | 1 | 6 | 2021-06 |
| test_suite | Infra | 1 | 1 | 12 | 2021-01 |
| vcell-stochastic | Solvers | 0 | 0 | 8 | direct-commit |
| vcell-nfsim | Solvers | 0 | 0 | 1 | direct-commit |
| vcell-messaging | Solvers | 0 | 0 | 0 | direct-commit |
| vcell-expressionparser | Solvers | 0 | 0 | 0 | direct-commit |
| vcell-api-client | Python | 0 | 0 | 0 | generated |
| PythonHPCBatchScript | Python | 0 | 0 | 0 | direct-commit |
| vcell-bioformats | Integrations | 0 | 0 | 0 | archived |
| vcellwordpress | AI/Web | 0 | 0 | 0 | direct-commit |
| vcdb | Infra | 0 | 0 | 0 | direct-commit |
| biomodelsdb_mirror | Infra | 0 | 0 | 0 | data |
| usermaterials | Infra | 0 | 0 | 0 | direct-commit |

**Totals (in scope):** ~1,155 PRs (1,074 non-bot) · ~700+ releases · 28 repos across 2 orgs.
