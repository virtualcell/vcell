# vcell — the Virtual Cell monorepo

The core Java monorepo that *is* VCell: the desktop modeling/simulation client, the
server back-end (database, dispatch, data), the REST APIs (legacy Restlet `/api/v0/`
and modern Quarkus `/api/v1/`), the CLI, and the VCML model representation that every
other repo in the org orbits. This is the flagship of the retrospective.

**Group:** Core platform · **PRs (non-bot):** 740 of 821 · **Releases/tags:** 481+
(670 tags all-time; ~310 since 2018-09) · **Active span:** PRs 2020-02 → 2026-06,
git history back to 2007 · **Key contributors:** jcschaff (325 PRs), danv61 (147),
CodeByDrescher (118), Ezequiel-Valencia (80), gmarupilla (29), moraru (25)

## Project background

VCell models cell biology from biochemical-network and rule-based pathways down to
cell biophysics, supporting ODEs, reaction-diffusion PDEs within cellular geometry,
Gillespie/hybrid stochastic solvers, particle-based spatial simulation, network-free
(NFSim) simulation, and moving-boundary problems. The repo is ~40 MB of Java across
~15 Maven modules (`vcell-core`, `vcell-math`, `vcell-client`, `vcell-server`,
`vcell-api`/`vcell-rest`, `vcell-cli`, `vcell-util`, …), plus a Python ecosystem
(`vcell-cli-utils`, `python-restclient`), a TypeScript Angular webapp (`webapp-ng`),
Perl/XSLT legacy tooling, and Docker/CI glue. The deployed VCell is a client/server
system: a free shared server at vcell.org provides a centralized model database,
cluster computing (SLURM/HPC), and shared storage; the C/C++/Fortran numerical solvers
live in a separate repo (`vcell-solvers`) and are consumed as container images.

A note on evidence: PRs are near-useless before 2022-H2 (only ~60 PRs across 2018–2022,
then 221 in 2022-H2 alone), so the early eras below are reconstructed from git history,
tags, and diffs rather than PRs. Versioning uses a 4-part `MAJOR.MINOR.PATCH.BUILD`
scheme; major lines are 7.2 (2019–20), 7.3 (2020), 7.4 (2021–22), 7.5 (2023), 7.6
(2024), 7.7 (2024–26), and 8.0.0 (2026).

## Timeline (themed milestones)

### 2018–2020 — pre-PR era: server modernization tail, and the BioSimulators/CLI seed

The Docker-microservices server architecture (centralized model database, JMS
messaging, Mongo logging, RabbitMQ, SLURM-backed cluster compute) was largely in place
*before* the 2018-09 retrospective cutoff. What's visible in 2018–2019 commit history is
its consolidation: a SLURM job-failure monitor running *outside* VCell's own messaging
(`02d8da2d`, 2019-08) to catch silently-killed cluster jobs, and ongoing HPC/messaging
config cleanup. The genuinely new work of this era is the **CLI + BioSimulators arc**,
which is also the project's first sustained use of GitHub PRs. gmarupilla's
[#6](https://github.com/virtualcell/vcell/pull/6) (2020-02) containerizes a SED-ML
solver; [#34](https://github.com/virtualcell/vcell/pull/34)/[#35](https://github.com/virtualcell/vcell/pull/35)
(2020-11, ~5.4k lines) merge the separate *Biosimulations_VCell* fork upstream, bringing
the `vcell-cli` module (first commit 2020-05), a Docker image, and CI to run OMEX/SED-ML
archives headlessly. The following year is a long tail of OMEX/SED-ML/HDF5 fidelity work
([#43](https://github.com/virtualcell/vcell/pull/43)–[#69](https://github.com/virtualcell/vcell/pull/69)):
uniform-timecourse interpolation for SED-ML reports, CSV→HDF5 conversion, per-task status
YAML, KiSAO algorithm IDs, and the BioSimulators registry/specs. This makes VCell a
registered BioSimulators engine and is the origin of the interoperability story that
recurs through 2025. Release lines 7.2 (2019–20) and 7.3 (late 2020) ship in parallel.

### 2021 — 7.4 line; CLI maturation and the libCombine/Smoldyn special-casing

7.4 (2021-03 onward) is dominated by desktop-client bug-fixes and continued export work.
The OMEX/SED-ML exporter learns to special-case NFSim and Smoldyn solvers (`acf3030b`,
2021-06), `vcell-cli-utils` (the Python helper for HDF5/plotting) appears (2021-06), and
bilalshaikh42's [#73](https://github.com/virtualcell/vcell/pull/73) re-integrates the
BioSimulators repo. This era is high-frequency patch releases (7.4.0.01 … 7.4.0.34) with
relatively few PRs — most work still lands as direct commits to the desktop GUI.

### 2022-H2 — the PR explosion: PostgreSQL, multi-service Docker, and project-board epics

The single biggest inflection in process: 221 PRs in one half-year (vs. ~60 in the prior
four years combined), as the team adopts PR-based development and an org project board.
The board's epics become thematic anchors: **ui fixes (#1038, 47 items)**, **export
SBML/Omex (#1036, 33)**, **biosimulators non/spatial (#1046/#1069)**, **migrate to
postgresql (#1032)**, **improve NFSim statistics (#1033)**, **keycloak (#1037)**, and
**vcell-rest service (#1040)**. The flagship technical work of the half-year is the start
of the **Oracle → PostgreSQL** migration: by 2022-12 (`ff8e7cc0`) all server services can
run against a bootstrapped Postgres database, and the local Docker stack can target either
backend. moraru contributes 25 PRs in this window (largely model/UI). This is also where
the long 7.4 release tail runs hot (dozens of `7.4.0.xx` tags through 2022).

### 2023 — 7.5 line; the Quarkus REST rewrite and OpenAPI client generation

The defining architectural arc of the project's modern era. The legacy REST API
(`vcell-api`, Restlet-based, `/api/v0/`) had served the desktop client and web for years;
in 2023-10 jcschaff bootstraps a **new Quarkus service** (`vcell-rest`,
[b44dd49d](https://github.com/virtualcell/vcell/commit/b44dd49da1) — a hello-world
resource + SmallRye OpenAPI). Over the next two months this becomes a real service:
`GET /publications` backed by a service bean (2023-11), an admin role gated by a
**toy OIDC/Keycloak** dev provider (`57701a88`, the seed of test-time auth), and—crucially—
**auto-generated client libraries**. The SmallRye-emitted OpenAPI spec drives three
generated clients: a Java client (`vcell-restclient`, 2023-11), a Python client
(`python-restclient`, 2023-11), and a TypeScript-Angular client inside the brand-new
**`webapp-ng`** (2023-12). The Quarkus dev mode is wired to Auth0 with PKCE code-flow,
establishing the auth pattern (Auth0 in prod/dev, Keycloak in tests) that holds to today.
This single quarter introduces four of the repo's current top-level modules at once.

### 2024-H1 — 7.5 line: PostgreSQL goes to production-grade, Auth0 in the desktop client

The Postgres migration crosses the finish line. Quarkus tests/dev run on Postgres
(testcontainers) while Oracle stays in production; dates are mapped to `TIMESTAMP`
(`4e2545d7`), the `vcell-oracle` module is collapsed into a simple Oracle/Postgres factory,
and by 2024-05 the `UsePostgresInProd` toggle is removed (`3f3efd3d`) — Postgres is the
default DB engine, Oracle the production exception. In parallel the **Auth0 OIDC login flow
reaches the desktop Java client** (a multi-month arc through 2024-H1: standalone-config sync,
browser pop-up handling, logout, foreground-after-login), modernizing auth end-to-end. The
generated OpenAPI clients are regenerated against the `/api/v1/` base URL (`0e21dfc1`, 2024-04).

### 2024-H2 → 2025 — 7.6/7.7 lines: solver image consumption, libvcell extraction, generalized stochastics

Several threads converge. **Solver consumption** is formalized: VCell pulls prebuilt
`vcell-solvers` container images (e.g. `vcell-solvers:v0.8.1.x`, 2024-07) instead of
building native solvers in-tree, and HDF5 native libs are replaced with pure-Java
(`io.jhdf`) and `cisc.jhdf5` (7.7.0.6, 2024-11). **`vcell-nativelib` is extracted** to the
new `libvcell` repo (2025-03, `53dfe3c0`) — VCell's model code compiled to a native shared
library so Python (`pyvcell`) can call VCML logic directly; this required de-coupling the
GUI thread-checker and AWT image creation from headless paths
([#1452](https://github.com/virtualcell/vcell/pull/1452)). On the modeling side,
**generalized stochastic simulation** ships (7.6.0.43 / 7.7.0.0, 2024-09): non-mass-action
kinetics (e.g. enzyme kinetics) can now be simulated stochastically by deriving propensities
from the net reaction rate. The **ImageJ/N5 plugin** (cross-ref `vcell-fiji`) gets cloud-ready
N5 export and resampling. AvocadoMoon/CodeByDrescher overhaul the **CLI for BioSimulations**
(7.7.0.19/.20, 2025-02/03) for a large spatial-model speedup and plots-as-reports. 2025 also
sees the **error-handling architecture overhaul** and **BioModel saving routed through the
Quarkus server** (7.7.0.26, 2025-05) — the desktop client increasingly talks to `/api/v1/`
rather than legacy paths.

### 2026 — 8.0.0 line: SpringSaLaD GA, parameter-estimation rebuild, publications, K8s maturity

The major version bump 7.7 → 8.0.0 (2026-05) marks the **SpringSaLaD/Langevin GA public
release event**. Langevin particle-based reaction-diffusion, integrated as a first-class
VCell *application* over the preceding months (stateless/structural sites synthesized for the
solver, batch rendering, result-set transmission), becomes **enabled by default** (8.0.0.01,
[#1693](https://github.com/virtualcell/vcell/pull/1693)); 8.0.x then tightens its job-count
limits to protect cluster resources. Separately, **parameter estimation is rebuilt** as a
REST service (2026-04): new `/api/v1/` optimization endpoints, the legacy socket-based
optimization server and `/api/v0/optimization` removed, COPASI/basico modernized in the
`vcell-opt` Docker image, and Apptainer SIF images pre-built for SLURM instead of pulling
`docker://` at job time ([#1659](https://github.com/virtualcell/vcell/pull/1659)). The
**publications** feature matures in `webapp-ng` (publish endpoint, MathModel support, privacy
field mapped from `versionFlag`). The repo also adopts formal release engineering this year:
a Keep-a-Changelog `CHANGELOG.md` and user-facing `release-notes/major/` narratives, backfilling
the 7.6 → 8.0.0 gap (`277795d2`, 2026-05).

## Notable PRs / commits

| Change | Date | Author | Why it matters |
|---|---|---|---|
| [#34](https://github.com/virtualcell/vcell/pull/34)/[#35](https://github.com/virtualcell/vcell/pull/35) | 2020-11 | gmarupilla | Merge the Biosimulations_VCell fork upstream: `vcell-cli`, Docker image, CI — origin of headless OMEX/SED-ML execution |
| [#43](https://github.com/virtualcell/vcell/pull/43) | 2021-01 | gmarupilla | Uniform-timecourse interpolation for SED-ML reports — BioSimulators output fidelity |
| `ff8e7cc0` | 2022-12 | jcschaff | All services run on a bootstrapped PostgreSQL DB — start of the Oracle→Postgres migration |
| [b44dd49d](https://github.com/virtualcell/vcell/commit/b44dd49da1) | 2023-10 | jcschaff | Bootstrap the Quarkus `vcell-rest` service with SmallRye OpenAPI — the modern `/api/v1/` API |
| `c3813f73` | 2023-11 | jcschaff | Generate `vcell-restclient` (Java) from OpenAPI; Python + TS-Angular clients follow days later |
| `57701a88` | 2023-11 | jcschaff | Admin-gated `/publications` via toy OIDC/Keycloak — test-time auth seed (Auth0 in prod) |
| `64172bc3` | 2023-12 | Ezequiel-Valencia | Angular-TypeScript OpenAPI generation — birth of `webapp-ng` |
| `3f3efd3d` | 2024-05 | (server) | Remove `UsePostgresInProd` — Postgres becomes the default engine |
| 7.6.0.43 / 7.7.0.0 | 2024-09 | danv61/jcschaff | Generalized (non-mass-action) stochastic simulation |
| [#1452](https://github.com/virtualcell/vcell/pull/1452) / `53dfe3c0` | 2025-03 | jcschaff | Extract `vcell-nativelib` → `libvcell` repo; VCML logic callable natively from `pyvcell` |
| 7.7.0.26 | 2025-05 | CodeByDrescher | Error-architecture overhaul + BioModel saving routed through the Quarkus server |
| [#1693](https://github.com/virtualcell/vcell/pull/1693) | 2026-05 | danv61 | SpringSaLaD enabled by default — 8.0.0 GA modality |
| `b7ac2e50`/`629888a7` | 2026-04 | jcschaff | Parameter-estimation rebuilt as REST service; legacy socket server + `/api/v0/optimization` removed; COPASI/basico modernized |
| `277795d2` | 2026-05 | jcschaff | Adopt `CHANGELOG.md` + `release-notes/major/`; backfill 7.6 → 8.0.0 |

## Key contributors

- **jcschaff** (Jim Schaff, lead/owner) — architecture and most pivotal arcs: Quarkus
  REST, OpenAPI client generation, Postgres migration, libvcell extraction, parameter
  estimation rebuild, release engineering. 325 PRs.
- **danv61** — modeling/solver integration, especially the SpringSaLaD/Langevin
  application and stochastic features; 147 PRs, active 2022–2026.
- **CodeByDrescher** (Drescher) — CLI/BioSimulations overhaul, error-handling
  architecture, server-side plumbing; 118 PRs, 2022–2026.
- **Ezequiel-Valencia** — `webapp-ng` Angular front-end, Auth0/OIDC, infra; 80 PRs.
- **gmarupilla** — the founding CLI/BioSimulators/OMEX/SED-ML work, 2020–2021 (29 PRs).
- **moraru** (Ion Moraru) — model/UI contributions concentrated in 2022-H2 (25 PRs).
- **vcfrmgit** — release/service automation account (tags, deploy bookkeeping).

## Tech & stack notes

- **Languages:** Java 17 (primary, ~40 MB) on Maven 3.8+; Python 3.10 (Poetry) for CLI
  utils and generated client; TypeScript/Angular 17 (`webapp-ng`); legacy Perl/XSLT/PLSQL
  tooling; Shell/Dockerfile glue. Solvers (C/C++/Fortran) live in `vcell-solvers` and are
  consumed as images.
- **REST:** Quarkus 3.5.2 (`vcell-rest`, `vcell-server`), `/api/v1/`; legacy Restlet
  `vcell-api` at `/api/v0/` being retired endpoint-by-endpoint. OpenAPI spec (SmallRye)
  drives three generated clients (Java/Python/TS) via OpenAPI Generator v7.1.0.
- **Auth:** Auth0 OIDC in prod/dev; Keycloak (testcontainers) in tests.
- **Databases:** Oracle in production, PostgreSQL for dev/test (testcontainers) and the
  default engine after the 2022–2024 migration.
- **CI/CD:** GitHub Actions (`ci_cd.yml`) — Maven build + Docker image test, parallel test
  groups (Fast, MathGen_IT, SBML_IT, SEDML/VCML/SBML_IT, BSTS_IT, Quarkus), Docker push to
  `ghcr.io` on release. Desktop installers (Install4J) for Windows/Mac/Linux must match the
  deployed server version.
- **Deployment:** Kubernetes via the separate `vcell-fluxcd` repo (overlays prod/stage/dev);
  ingress routes `/api/v1/` → `vcell-rest`, `/api/v0/` → legacy `vcell-api`. (`docker/swarm/`
  is a stale name for build-time config staging — VCell runs on K8s.)
- **Versioning:** 4-part `MAJOR.MINOR.PATCH.BUILD`; ~310 release tags since 2018 reflect a
  fast continuous-delivery cadence on the shared server.
