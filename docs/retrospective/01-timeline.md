# VCell Org Retrospective — Org-Wide Timeline

A single chronological narrative stitched from the [per-repo entries](repos/),
grouped by era/half-year. It traces how threads in different repos connect — the
solver extraction feeding the Python stack, the Quarkus REST rewrite feeding the
OpenAPI clients and `webapp-ng`, the Langevin solver feeding the SpringSaLaD app
and VCell 8.0. Diffs/releases/tags are the ground truth; PR counts before 2022-H2
badly understate the work, so the early eras lean on releases and commit history.
Per-repo entries carry the detailed evidence and PR links.

---

## 2018-H2 → 2020 — the pre-PR era (reconstructed from releases)

VCell's client/server architecture and its Docker-microservices server (centralized
model database, JMS messaging, SLURM-backed HPC compute, shared storage) were
largely in place *before* the 2018-09 cutoff. What's visible in 2018–2019 is
consolidation — e.g. a SLURM job-failure monitor running outside VCell's own
messaging to catch silently-killed cluster jobs — shipped via the 7.2/7.3 release
lines rather than PRs ([`repos/vcell.md`](repos/vcell.md)).

The genuinely new work of the era, and the project's **first sustained use of
GitHub PRs**, is the **CLI + BioSimulators arc**: gmarupilla's
[vcell#6](https://github.com/virtualcell/vcell/pull/6) (2020-02) containerized a
SED-ML solver, and [vcell#34](https://github.com/virtualcell/vcell/pull/34)/[#35](https://github.com/virtualcell/vcell/pull/35)
(2020-11) merged the *Biosimulations_VCell* fork upstream, bringing the `vcell-cli`
module, a Docker image, and CI for headless OMEX/SED-ML execution. The companion
Python helper began as its own repo, **`vcell_cli_utils`** (first commit 2021-01,
GMarupilla), handling status-YAML, CSV/HDF5 conversion, and 2-D plots
([`repos/python-clients-utils.md`](repos/python-clients-utils.md)); a sibling
**`test_suite`** repo cut weekly SBML/OMEX regression-report releases through early
2021 ([`repos/infrastructure.md`](repos/infrastructure.md)). On the solver side,
**`vcell-solvers`** in 2019–2020 was CI maturation on Travis/AppVeyor atop the
original Vagrant-VM-per-platform build, plus memory-leak fixes in the Gibson/Jump
stochastic paths ([`repos/vcell-solvers.md`](repos/vcell-solvers.md)).

## 2021 — 7.4 line; CLI maturation (still few PRs)

7.4 is dominated by desktop-client fixes and export work landing as direct commits:
the OMEX/SED-ML exporter learns to special-case NFSim and Smoldyn, `vcell_cli_utils`
matures, and high-frequency patch releases (`7.4.0.01…34`) run with relatively few
PRs ([`repos/vcell.md`](repos/vcell.md)).

## 2022-H2 — the PR explosion: PostgreSQL + project board

The single biggest *process* inflection: **~226 PRs in one half-year** (vs. ~60 in
the prior four years) as the team adopted PR-based development and an org project
board, whose epics (ui fixes, export SBML/Omex, biosimulators, migrate-to-postgresql,
keycloak, vcell-rest) became thematic anchors. The flagship technical thread is the
start of the **Oracle → PostgreSQL migration**: by 2022-12 all server services could
run against a bootstrapped Postgres database
([`repos/vcell.md`](repos/vcell.md)). In `vcell-solvers`, jcschaff began dismantling
the Vagrant-VM model and porting solver builds onto **GitHub Actions + Docker**
(macOS and MovingBoundary builds, an `ubuntu:20.04` build container) — the
groundwork that later enabled extraction ([`repos/vcell-solvers.md`](repos/vcell-solvers.md)).
The CompCellBio community site, alive since 2020-12, continued as a direct-commit
content site ([`repos/web-presence.md`](repos/web-presence.md)).

## 2023 — the modern-stack quarter (Quarkus REST + OpenAPI clients + webapp-ng)

The defining architectural arc of VCell's modern era, almost all in 2023-Q4
([`repos/vcell.md`](repos/vcell.md)). jcschaff bootstrapped a new **Quarkus
`vcell-rest` service** ([b44dd49d](https://github.com/virtualcell/vcell/commit/b44dd49da1),
2023-10) with SmallRye OpenAPI, alongside the legacy Restlet `/api/v0/` API. Within
two months it became a real service (`/publications`, admin-gated by a toy
OIDC/Keycloak dev provider) and — crucially — the SmallRye OpenAPI spec drove
**three auto-generated clients**: a Java client (`vcell-restclient`), a Python
client (`python-restclient`), and a TypeScript-Angular client inside the brand-new
**`webapp-ng`** (Ezequiel-Valencia). Auth0 PKCE was wired for dev/prod, Keycloak for
tests. One quarter introduced four current top-level modules.

Two threads connect outward from here. The OpenAPI generation pattern is the seed of
the later standalone **`vcell-api-client`** (a 2024-05 one-day bootstrap, soon
superseded by the client vendored inside `pyvcell` —
[`repos/python-clients-utils.md`](repos/python-clients-utils.md)) and of `pyvcell`'s
own generated remote client. Separately, in `vcell-solvers`, jcschaff refactored
`VCellStoch` into a **library + executable with a unit test**
([vcell-solvers#24](https://github.com/virtualcell/vcell-solvers/pull/24)) — the
architectural seed of the per-solver extractions, since each engine now needed to be
a linkable, self-testing library ([`repos/vcell-solvers.md`](repos/vcell-solvers.md)).

Two new threads also opened in `cam-center` and Fiji. **LangevinNoVis01** got its
foundational rebuild (jcschaff, 2023-07): Maven + **GraalVM native-image**, a picocli
CLI, and cross-platform CI producing native solver binaries
([`repos/langevin-springsalad.md`](repos/langevin-springsalad.md)). And
**`vcell-fiji`** started (paulricky, then Ezequiel-Valencia) and pivoted to reading
VCell's **N5-over-S3** simulation exports directly into ImageJ
([`repos/vcell-fiji.md`](repos/vcell-fiji.md)). A short-lived **`devops`** Ansible
service-restart role (Sept–Oct 2023) predates the K8s migration and was later
absorbed into `vcell-fluxcd` ([`repos/infrastructure.md`](repos/infrastructure.md)).

## 2024-H1 — Postgres to production-grade; the extraction begins; deployment as code

The **Postgres migration crossed the finish line**: Quarkus tests/dev run on Postgres
(testcontainers) while Oracle stays in production, and the `UsePostgresInProd` toggle
was removed (2024-05) — Postgres became the default engine. The **Auth0 OIDC login
flow reached the desktop Java client**, modernizing auth end to end, and the generated
OpenAPI clients were regenerated against `/api/v1/` ([`repos/vcell.md`](repos/vcell.md)).

This half-year is the hinge for two long arcs. In `vcell-solvers`, the unified
"Nonchombo" cross-platform build ([#31](https://github.com/virtualcell/vcell-solvers/pull/31))
shipped as **v0.8.0** (May 2024), proving every solver could build standalone — and
days later the **first extraction**, **`vcell-fvsolver`** (2024-05), stripped the FV
solver (and bundled Smoldyn) out of the monorepo, added a **pybind11 +
scikit-build-core** binding exposing `solve()`/`version()`, and stood up a
cross-platform wheel pipeline ([`repos/vcell-fvsolver.md`](repos/vcell-fvsolver.md)).
In parallel, **SpringSaLaD** was rebuilt Ant/install4j → **Maven** (jcschaff,
2024-05) and **LangevinNoVis01** gained its **VCell integration** — the
`VCellMessaging`/`MessagingConfig` JMS contract that lets it run as a VCell remote
compute job — plus a genuine physics fix (spring force scaled by site diffusion)
([`repos/langevin-springsalad.md`](repos/langevin-springsalad.md)). And
**`vcell-fluxcd`** opened (2024-05) as the FluxCD/Kustomize **GitOps backbone** of
the K8s deployment, establishing the base/overlay layout and the island-vs-remote
instance model ([`repos/infrastructure.md`](repos/infrastructure.md)).

## 2024-H2 — solver-image consumption, pyvcell's origin, Fiji public release

`vcell` formalized **solver consumption**: it now pulls prebuilt `vcell-solvers`
container images instead of building native solvers in-tree, and replaced native
HDF5 libs with pure-Java ones. **Generalized (non-mass-action) stochastic
simulation** shipped (7.6.0.43/7.7.0.0, 2024-09) ([`repos/vcell.md`](repos/vcell.md)).
`vcell-solvers` froze: its last release **v0.8.2** (Nov 2024) is the monorepo's
effective end-of-life ([`repos/vcell-solvers.md`](repos/vcell-solvers.md)).

The pivotal new thread is the **birth of the Python stack**. **`pyvcell`** began
(2024-08, release 0.0.1) seeded from the monorepo `pythonData` simdata library and
wrapping `pyvcell-fvsolver` — at this stage it could *run* the FV solver and read
its Zarr/VTK output but not yet *author* a model
([`repos/pyvcell.md`](repos/pyvcell.md)). On the Fiji side, the plugin reached its
**v1.0.0 public release** (2024-09) with a custom lazy-chunk `SimCacheLoader`, then a
data-reduction/measurement subsystem (v2.0, 2024-12)
([`repos/vcell-fiji.md`](repos/vcell-fiji.md)). `vcell-fluxcd` hardened the
`s3proxy` that fronts that N5 data ([`repos/infrastructure.md`](repos/infrastructure.md)).

## 2025-H1 — libvcell, pyvcell becomes a modeling toolkit, ode extraction

The Python stack matured into a coherent three-layer system. **`libvcell`** was
extracted (2025-03): VCell's Java core (VCML/SBML translation, math generation,
geometry/region computation) compiled by **GraalVM native-image** into a C-callable
shared library and loaded from Python via **ctypes** (no JVM, no JPype/JNI) — the
load-bearing Java-core dependency under `pyvcell`
([`repos/libvcell.md`](repos/libvcell.md)). This required de-coupling VCell's GUI
thread-checker and AWT paths from headless paths
([vcell#1452](https://github.com/virtualcell/vcell/pull/1452),
[`repos/vcell.md`](repos/vcell.md)). With `libvcell` in place, **`pyvcell` turned
from data-reader into a full modeling toolkit** (Jan–Feb 2025): programmatic VCML
authoring, VTK/trame visualization, the `_internal` public/private boundary, and a
confirmed local-sim pipeline — **`libvcell` generates FV solver input from VCML, then
the `pyvcell-fvsolver` wheel runs it**. A dense burst of releases served a
systems-biology course and Colab demos ([`repos/pyvcell.md`](repos/pyvcell.md)).

The solver extraction continued: **`vcell-ode`** (2025-06) carved the SUNDIALS
ODE/IDA solver out of the monorepo ([`repos/vcell-ode-mbsolver.md`](repos/vcell-ode-mbsolver.md)).
`vcell-fvsolver` got a major Smoldyn surface/panel lookup speedup and a Conan +
all-LLVM (clang/flang) toolchain migration (CodeByDrescher)
([`repos/vcell-fvsolver.md`](repos/vcell-fvsolver.md)). LangevinNoVis01 had its most
active year — **"Boris' new algorithm"** for binding kinetics (`1−e^(−λdt)` with
intrinsic on-rate from the diffusion-limited rate), random-seed reproducibility, and
cluster-analysis post-processing ([`repos/langevin-springsalad.md`](repos/langevin-springsalad.md)).
In `vcell`, the error-handling architecture was overhauled and **BioModel saving was
routed through the Quarkus server** (7.7.0.26, 2025-05) — the desktop client
increasingly talks to `/api/v1/` ([`repos/vcell.md`](repos/vcell.md)).
`vcell-fluxcd` brought the **Auth0 OIDC tenant under Terraform** and wired REST-pod
storage volumes ([`repos/infrastructure.md`](repos/infrastructure.md)).

## 2025-H2 — VCell-AI (GSoC), remote pyvcell, export pipeline

The GSoC 2025 student build of **VCell-AI** landed: a Next.js + FastAPI platform with
an **OpenAI/Azure-OpenAI** function-calling chatbot over the live VCell API and a
**Qdrant RAG store built from VCell tutorial docs** (biomodel data is fetched live via
tools, not embedded). Most of the architecture lives in direct commits on a dev
branch; nine releases (Aug–Oct 2025) were deployment fixes, not features
([`repos/VCell-AI.md`](repos/VCell-AI.md)). `vcell-fluxcd` added the **`overlays/ai/`**
family (ai-backend/ai-frontend/qdrant) to deploy it, and promoted the data **export**
service to a first-class deployment with its own Artemis broker — completing the
prod/stage/dev triad ([`repos/infrastructure.md`](repos/infrastructure.md)). `pyvcell`
was quieter (a VCML-application-parameter parse), setting up the 2026 workshop work
([`repos/pyvcell.md`](repos/pyvcell.md)).

## 2026-H1 — VCell 8.0 GA, the solver-extraction finale, pyvcell remote/MB, and the AI/agent wave

The most active half-year (~241 in-scope PRs), pulling every thread together.

**VCell 8.0.0 GA (2026-05).** The 7.7 → 8.0.0 major bump marks the
**SpringSaLaD/Langevin public release**: Langevin particle-based reaction-diffusion,
integrated as a first-class VCell *application* over the preceding months, became
**enabled by default** ([vcell#1693](https://github.com/virtualcell/vcell/pull/1693)).
Separately, **parameter estimation was rebuilt as a REST service** (`/api/v1/`
optimization endpoints; legacy socket server and `/api/v0/optimization` removed;
COPASI/basico modernized; Apptainer SIF images pre-built for SLURM), the
**publications** feature matured in `webapp-ng`, and the repo adopted formal release
engineering (`CHANGELOG.md`, `release-notes/major/`) backfilling the 7.6 → 8.0.0 gap
([`repos/vcell.md`](repos/vcell.md)). LangevinNoVis01/SpringSaLaD shipped supporting
fixes and **automated install4j installer publishing**
([`repos/langevin-springsalad.md`](repos/langevin-springsalad.md)).

**The solver-extraction finale.** The remaining solvers left the monorepo in a 2026
burst: **`vcell-nfsim`** (Feb), **`vcell-stochastic`** (Mar) — both single-author,
direct-commit, bontempiuchc, each with a pybind11/scikit-build wheel
([`repos/vcell-stochastic-nfsim.md`](repos/vcell-stochastic-nfsim.md)) — and
**`vcell-mbsolver`** (the FronTier moving-boundary solver, packaged by jcschaff in a
single intense June burst: four same-day releases, Linux/macOS-only because FronTier
won't build under MSVC, with a callback-driven binding rather than file-driven
([`repos/vcell-ode-mbsolver.md`](repos/vcell-ode-mbsolver.md))). The shared C++
libraries **`vcell-messaging`** and **`vcell-expressionparser`** were finally split
out (June) as standalone submodule repos — though as of June 2026 only `vcell-mbsolver`
consumes them as submodules; `vcell-ode`/`vcell-stochastic` still carry the sources
inline ([`repos/solver-shared-libs.md`](repos/solver-shared-libs.md)). `vcell-ode`
got its Conan/CMake modernization and `pyvcell_odesolver` binding (CodeByDrescher,
2026-04); `vcell-fvsolver` got multi-arch GHCR Docker publishing and auto-attached
release binaries ([`repos/vcell-ode-mbsolver.md`](repos/vcell-ode-mbsolver.md),
[`repos/vcell-fvsolver.md`](repos/vcell-fvsolver.md)).

**pyvcell completes the loop.** For a March 2026 workshop, `pyvcell` gained a
**session-based remote API** (`vc.connect()` → `VCellSession`/`SimulationJob`) over a
regenerated OpenAPI client, then migrated Poetry → **UV**, restructured into lazy
optional-dependency extras, and added **moving-boundary support** ([0.4.0](https://github.com/virtualcell/pyvcell/releases/tag/0.4.0))
— a two-pass `libvcell` round-trip feeding the new `pyvcell-mbsolver` wheel via
observer callbacks ([`repos/pyvcell.md`](repos/pyvcell.md)). `libvcell` added the
`vcml_to_moving_boundary_input` translator and a `py3-none-<platform>` wheel scheme
to dodge PyPI's project-size ceiling ([`repos/libvcell.md`](repos/libvcell.md)). So the
full chain now reads: **VCML → `libvcell` (GraalVM native) → solver input → extracted
solver wheel (`pyvcell-fvsolver`/`pyvcell-mbsolver`) → `pyvcell` results** —
end-to-end Python, Java-free and C++-free at the orchestration layer.

**The AI/agent-driven experiments.** VCell-AI saw a March 2026 wave of ~28 small
GSoC-2026 applicant PRs, then maintainer consolidation: **BioModels.org (BMDB)
integration** and a dual-DB UI ([VCell-AI#66](https://github.com/virtualcell/VCell-AI/pull/66),
jcschaff) and **Auth0** end to end ([`repos/VCell-AI.md`](repos/VCell-AI.md)). The
most striking new artifact is **`vcell-fenics`** (June 14–25, 2026): an experimental
DOLFINx 0.10.x finite-element backend for cell-migration problems, single-author
(jcschaff) but **overwhelmingly Claude-Code agent-driven** (218/314 commits
Claude-co-authored) — its 104 PRs are one continuous ~11-day sprint that genuinely
imports VCML and cross-validates fields point-by-point against VCell's FV and FronTier
solvers ([`repos/vcell-fenics.md`](repos/vcell-fenics.md)). Web presence modernized in
parallel: **`vcellwordpress`** staged the vcell.org WordPress migration onto K8s
(Kustomize + Bitnami Helm + sealed secrets; cutover documented but not yet executed)
([`repos/web-presence.md`](repos/web-presence.md)). `vcell-fluxcd`'s 2026 work was pure
ops hardening — heap-dump volumes, batch-host repointing, and a notable DNS
`single-request-reopen`/`ndots:2` fix for spurious `sbatch` submit timeouts
([`repos/infrastructure.md`](repos/infrastructure.md)).

---

## Key transitions

The pivotal architectural inflection points, in order:

1. **PR-workflow + project-board adoption (2022-H2).** ~60 PRs in four years → ~226
   in one half-year. Everything before this is reconstructed from releases/tags/diffs;
   everything after is legible through PRs and epics. ([`repos/vcell.md`](repos/vcell.md))

2. **Oracle → PostgreSQL (2022-H2 → 2024-H1).** Started as a board epic; ended with
   Postgres the default engine and Oracle the production exception, with Quarkus
   tests/dev on Postgres testcontainers. ([`repos/vcell.md`](repos/vcell.md))

3. **Restlet → Quarkus REST + generated OpenAPI clients (2023-Q4).** A new `/api/v1/`
   Quarkus service whose SmallRye OpenAPI spec drives Java/Python/TS clients and the
   Angular `webapp-ng`; Auth0/Keycloak auth. The desktop client and Python stack then
   progressively migrate onto `/api/v1/`. ([`repos/vcell.md`](repos/vcell.md))

4. **Monolithic solvers → extracted per-solver repos (2024–2026, staggered).** The
   `vcell-solvers` lib/exe refactor and unified build (2023–2024) enabled extraction:
   fvsolver (2024-05), ode (2025-06), then nfsim/stochastic/mbsolver and the shared
   libs through 2026 — each a standalone CMake project with a pybind11 binding and wheel
   pipeline; the monorepo froze at v0.8.2. ([`repos/vcell-solvers.md`](repos/vcell-solvers.md))

5. **JVM desktop → Python/GraalVM scriptable stack (2024-08 → 2026).** `libvcell`
   (Java core as a GraalVM native shared library, ctypes) + `pyvcell` (PyPI front door)
   + the extracted solver wheels form a Java-free, scriptable, teachable alternative to
   the desktop client — completed with remote sessions and moving-boundary support in
   2026-H1. ([`repos/pyvcell.md`](repos/pyvcell.md), [`repos/libvcell.md`](repos/libvcell.md))

6. **Langevin solver → SpringSaLaD app → VCell 8.0 GA (2023–2026).** The cam-center
   particle solver, modernized (GraalVM native, JMS messaging) and given a new binding
   algorithm, was integrated as VCell's "Langevin" application and the SpringSaLaD app
   rebuilt on Maven, culminating in the 7.7 → 8.0.0 public release with Langevin enabled
   by default. ([`repos/langevin-springsalad.md`](repos/langevin-springsalad.md))

7. **The 2026 AI/agent-driven experiments.** VCell-AI (GSoC 2025, OpenAI/Azure +
   Qdrant RAG) and `vcell-fenics` (an experimental, overwhelmingly Claude-Code
   agent-driven DOLFINx FEM backend, June 2026) mark a shift toward LLM- and
   agent-assisted development and discovery layered on top of the VCell platform.
   ([`repos/VCell-AI.md`](repos/VCell-AI.md), [`repos/vcell-fenics.md`](repos/vcell-fenics.md))
