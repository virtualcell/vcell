# vcell-solvers — legacy C/C++/Fortran solver monorepo

The original collection of every numerical simulation code VCell drives (PDE, stochastic, network-free, ODE, moving-boundary) plus the shared C++ libraries they depend on. This entry doubles as the **hub for the 2024–2025 solver-extraction narrative**: how these solvers were split out of one monorepo into per-solver repos.

`Group: Solvers · PRs (non-bot): 17 · Releases: 54 · active span: 2019-04→2024-11 · Top (all-time, pre-split): fgao15, gweatherby, jcschaff`

> Contributor counts and most releases predate the 2018-09 retrospective window — this monorepo carries deep history (releases back to `binaries_20170901`), so the top-3 contributors reflect cumulative authorship of code that has since been distributed across the extracted repos.

## Project background

`vcell-solvers` is "a collection of numerical simulation codes used in the Virtual Cell framework" (README). At HEAD it is a single CMake super-project whose top-level `CMakeLists.txt` toggles each engine on/off via `OPTION_TARGET_*` flags. The directories map to the solvers and shared libraries:

- **`VCell` / `bridgeVCellSmoldyn` / `smoldyn-2.38`** — the FiniteVolume reaction-diffusion-advection PDE solver and its Smoldyn particle bridge (`FiniteVolume_x64`).
- **`Stochastic`** (`VCellStoch`) — Gibson (Next Reaction) / Gillespie stochastic solver.
- **`NFsim_v1.11`** — VCell's fork of the network-free rule-based stochastic simulator (`NFsim_x64`).
- **`IDAWin` + `sundials`** — the ODE/CVODE engines (`SundialsSolverStandalone_x64`).
- **`MBSolver` + `FronTierLib`** — the moving-boundary (deforming-geometry) solver.
- **`VCellChombo` / `Chombo`** — the (later disabled) AMR Chombo solver.
- **`Hy3S`, `qhull`, `blas`, `PCGPack`, `netcdf-3.6.2`, `hdf5-1.8.11`, `libzip-1.2.0`** — bundled numerics/IO dependencies.
- **Shared C++ libs:** `ExpressionParser` (math-expression evaluation), `vcommons` (common utilities), `VCellMessaging` (libcurl progress messaging), `VCellZipUtils`.

Languages by bytes are C (32 MB), C++ (29 MB), Fortran (3.6 MB), with Makefile/Perl/Shell/CMake build glue — i.e. a large bundled-dependency monorepo, not a thin solver core.

## Timeline (themed milestones)

### 2017–2019 — Vagrant-VM-per-platform builds (background)

The original build model used **one VirtualBox/Vagrant VM per target platform** (`VagrantBoxes/linux32`, `linux64`, `macos`, `win64`, each with a `Vagrantfile` + `build.sh`). The README still documents `vagrant up && vagrant ssh -c /vagrant_numerics/build.sh` per platform — including the note that "Apple only allows a Mac VM to run on Apple hardware." Releases in this era are dense and machine-driven (`solvers-v1.1.0-95…107`, `0.0.x-dev` tags), reflecting CI-cut binaries rather than human milestones. By 2018 Travis CI and AppVeyor were layered on top of the Vagrant scripts ([v0.0.20](https://github.com/virtualcell/vcell-solvers/releases/tag/v0.0.20) "travis build for local solvers", [v0.0.21](https://github.com/virtualcell/vcell-solvers/releases/tag/v0.0.21) "travis and appveyor local builds").

### 2019–2020 — CI maturation on Travis/AppVeyor

The first PRs in-window are correctness and build-infra. [#1](https://github.com/virtualcell/vcell-solvers/pull/1) (fgao15) added volume-region variables to ExpressionParser evaluation. gmarupilla then drove a run of CI work: status badges and Travis/AppVeyor config ([#2](https://github.com/virtualcell/vcell-solvers/pull/2)), iterating the Windows AppVeyor NFSim build ([#3](https://github.com/virtualcell/vcell-solvers/pull/3), [#4](https://github.com/virtualcell/vcell-solvers/pull/4)), cross-platform build READMEs with screenshots ([#5](https://github.com/virtualcell/vcell-solvers/pull/5), [#6](https://github.com/virtualcell/vcell-solvers/pull/6)), and an HDF5 1.12-via-Homebrew fix ([#7](https://github.com/virtualcell/vcell-solvers/pull/7)). The substantive code change of this era is [#8](https://github.com/virtualcell/vcell-solvers/pull/8) — **memory-leak fixes in the Gibson and Jump stochastic solver code paths** (+696/-693 across 5 files), the kind of long-lived C++ correctness work that motivated later unit-testing efforts. Releases [v0.0.26](https://github.com/virtualcell/vcell-solvers/releases/tag/v0.0.26)–[v0.0.30](https://github.com/virtualcell/vcell-solvers/releases/tag/v0.0.30) cover this span.

### 2022 — Migration off Vagrant toward GitHub Actions + Docker

jcschaff began dismantling the per-platform-VM model and moving builds onto GitHub Actions runners and Docker. [#15](https://github.com/virtualcell/vcell-solvers/pull/15) ("14 GitHub build macos", 17 files) and [#19](https://github.com/virtualcell/vcell-solvers/pull/19) ("18 movingboundary build macos", 27 files, +411/-319) ported the macOS and MovingBoundary builds to GH Actions natively. [#21](https://github.com/virtualcell/vcell-solvers/pull/21) added a **Docker container to build/test solvers on `ubuntu:20.04`**, and [#23](https://github.com/virtualcell/vcell-solvers/pull/23) shrank that base image. [#12](https://github.com/virtualcell/vcell-solvers/pull/12) was a one-line NFSim behavioral tweak (commenting out a "forcing location index" path).

### 2023 — Testability: refactor solvers into lib + exe

[#24](https://github.com/virtualcell/vcell-solvers/pull/24) (43 files, +1343/-454) **refactored `VCellStoch` into a separate library + executable so it could carry a unit test** — driven by [vcell#819](https://github.com/virtualcell/vcell/issues/819), iterating "until we uncover the problem." This lib/exe split is the architectural seed of the later per-solver extractions: each engine becomes a linkable library with its own tests, which is exactly the shape the standalone repos require. Release [v0.0.43](https://github.com/virtualcell/vcell-solvers/releases/tag/v0.0.43) ("mac build with versioning and Stoch solver fix") shipped it.

### 2024 — Unified cross-platform build, the trigger for extraction

[#31](https://github.com/virtualcell/vcell-solvers/pull/31) "**Nonchombo build**" (86 files, +1454/-1429) is the pivotal PR. Its goal, per the description, was "a build of common vcell solvers across Docker, native linux, native windows, and native macos (Intel and Arm)." The diff disables the heavy Chombo/AMR path and rewires `CMakeLists.txt` and `cd.yml` so the remaining solvers (FV, Smoldyn, Stochastic, NFsim, MovingBoundary, Sundials) build uniformly across all four targets. The shipped result is **[v0.8.0](https://github.com/virtualcell/vcell-solvers/releases/tag/v0.8.0) — "Unified build on Github for Docker, Linux, macos_arm64, macos_x86_64, windows"** (May 2024), the version bump from the long-running `v0.0.x` line marking the new build regime. The `0.0.44-dev1…4` tags trace the incremental fixes (FV/Gibson, Windows GH-actions compression, NFSim macOS runtime, MovingBoundary on macOS) that led there.

The final `cd.yml` encodes this: a `native-build` matrix over `macos-13, macos-14, windows-latest, ubuntu-latest`, each installing boost/hdf5/ninja/llvm via brew/etc., then `cmake -G Ninja -DOPTION_TARGET_CHOMBO*=OFF … -DOPTION_TARGET_FV_SOLVER=ON …` + `ninja` + `ctest -VV`.

[#33](https://github.com/virtualcell/vcell-solvers/pull/33) "smoke tests on all platforms" (+1647/-58) then added cross-platform smoke tests (NFsim, CVODE/SundialsSolverStandalone, Stochastic; FV/Smoldyn/MovingBoundary left as TODOs) — releases [v0.8.1](https://github.com/virtualcell/vcell-solvers/releases/tag/v0.8.1)–[v0.8.1.3](https://github.com/virtualcell/vcell-solvers/releases/tag/v0.8.1.3) cover the smoke-test and 2-stage-Docker iterations. The last commit to the monorepo is [#34](https://github.com/virtualcell/vcell-solvers/pull/34) ([v0.8.2](https://github.com/virtualcell/vcell-solvers/releases/tag/v0.8.2), Nov 2024), bumping Docker Python 3.9→3.10 for `vcell-batch` compatibility.

### 2024–2025 — The extraction: monorepo → per-solver repos

Once #31/#33 proved each engine could build and self-test standalone, the solvers were **split out of this monorepo into dedicated repositories**, each gaining its own standalone CMake project, **pybind11 Python bindings**, and independent release/wheel pipelines (so the `pyvcell` Python ecosystem can call them directly rather than shelling out to monorepo binaries). The split happened repo-by-repo over ~2 years (repo creation dates):

| Extracted component | New repo | Created | Was, in this monorepo |
|---|---|---|---|
| FiniteVolume PDE | [vcell-fvsolver](https://github.com/virtualcell/vcell-fvsolver) | 2024-05 | `VCell/`, `bridgeVCellSmoldyn/`, `smoldyn-2.38/` |
| ODE / CVODE | [vcell-ode](https://github.com/virtualcell/vcell-ode) | 2025-06 | `IDAWin/`, `sundials/` |
| Network-free | [vcell-nfsim](https://github.com/virtualcell/vcell-nfsim) | 2026-02 | `NFsim_v1.11/` |
| Stochastic | [vcell-stochastic](https://github.com/virtualcell/vcell-stochastic) | 2026-03 | `Stochastic/` (`VCellStoch`) |
| Moving-boundary | [vcell-mbsolver](https://github.com/virtualcell/vcell-mbsolver) | 2026-05 | `MBSolver/`, `FronTierLib/` |
| Messaging | [vcell-messaging](https://github.com/virtualcell/vcell-messaging) | 2026-06 | `VCellMessaging/` |
| Expression parser | [vcell-expressionparser](https://github.com/virtualcell/vcell-expressionparser) | 2026-06 | `ExpressionParser/` |

`vcell-fvsolver` led (May 2024, immediately after the unified build), then `vcell-ode`, and through 2026 the remaining solvers plus the shared `ExpressionParser`/`vcommons`/`VCellMessaging` libraries were peeled off. **Each extracted repo carries its own entry in this retrospective with the detail; their pre-extraction history lives here in `vcell-solvers`, not in the new repos.** After the extractions, this monorepo is effectively frozen (last release [v0.8.2](https://github.com/virtualcell/vcell-solvers/releases/tag/v0.8.2), Nov 2024) — superseded but retained as the historical root and for components not yet re-homed (e.g. Chombo, Hy3S).

## Notable PRs/commits

| PR | Date | Author | Why it matters |
|---|---|---|---|
| [#8](https://github.com/virtualcell/vcell-solvers/pull/8) | 2020-07 | gmarupilla | Memory-leak fixes in Gibson & Jump stochastic code paths (+696/-693) — long-lived C++ correctness work |
| [#15](https://github.com/virtualcell/vcell-solvers/pull/15) | 2022-11 | jcschaff | Ports macOS solver build to GitHub Actions (begins exit from Vagrant VMs) |
| [#19](https://github.com/virtualcell/vcell-solvers/pull/19) | 2022-11 | jcschaff | MovingBoundary build on GH Actions / macOS |
| [#21](https://github.com/virtualcell/vcell-solvers/pull/21) | 2022-11 | jcschaff | Docker container to build/test solvers on ubuntu:20.04 |
| [#24](https://github.com/virtualcell/vcell-solvers/pull/24) | 2023-08 | jcschaff | Refactors `VCellStoch` into lib + exe with a unit test (vcell#819) — architectural seed of extraction |
| [#31](https://github.com/virtualcell/vcell-solvers/pull/31) | 2024-02 | jcschaff | **"Nonchombo build"** — unified FV/Stoch/NFsim/MB/Sundials build across Docker+Linux+Win+macOS(x86/arm); ships as [v0.8.0](https://github.com/virtualcell/vcell-solvers/releases/tag/v0.8.0) |
| [#33](https://github.com/virtualcell/vcell-solvers/pull/33) | 2024-07 | jcschaff | Cross-platform smoke tests (NFsim, CVODE, Stochastic) proving standalone runnability |
| [#34](https://github.com/virtualcell/vcell-solvers/pull/34) | 2024-11 | jcschaff | Final monorepo change: Docker Python 3.9→3.10 ([v0.8.2](https://github.com/virtualcell/vcell-solvers/releases/tag/v0.8.2)) |

## Key contributors

- **fgao15** (593 commits, all-time) — solver internals (ExpressionParser, FV/stochastic numerics); the deep pre-split authorship.
- **gweatherby** (464) — build infrastructure and solver code; the Travis/AppVeyor and dependency plumbing.
- **jcschaff** (222) — owner; drove the 2022–2024 modernization: Vagrant→GitHub-Actions/Docker migration, the lib/exe testability refactor, the unified "Nonchombo" build, and the extraction strategy.
- **gmarupilla** (36, in-window) — 2019–2020 CI configuration and the stochastic memory-leak fixes.
- **CodeByDrescher** (48), **danv61** (17), **mpw6** (41), **xweigit** (11) — historical solver and infra contributors.
- **vcfrmgit** (26) — release/build automation account.

## Tech & stack notes

- **Languages:** C (32 MB) + C++ (29 MB) + Fortran (3.6 MB) cores, with bundled numerics deps (HDF5 1.8.11, NetCDF 3.6.2, SUNDIALS, BLAS, qhull, PCGPack, FronTier, Smoldyn 2.38, libzip).
- **Build:** single top-level **CMake** super-project gated by `OPTION_TARGET_*` flags (one per solver + messaging/parallel/PETSc/Chombo); `Ninja` generator; `ctest` for unit/smoke tests.
- **Original build mechanics:** **VirtualBox + Vagrant, one VM per platform** (`VagrantBoxes/{linux32,linux64,macos,win64}`), later wrapped by Travis CI + AppVeyor.
- **Modern build mechanics:** a single GitHub Actions `cd.yml` with a `native-build` matrix (`macos-13`, `macos-14`, `windows-latest`, `ubuntu-latest`) plus a Docker `build-and-push-image` job pushing to `ghcr.io/virtualcell/vcell-solvers`. Disables Chombo/PETSc/messaging for the common build.
- **Release cadence:** 54 GitHub releases spanning 2017→2024, mostly machine-cut `v0.0.x`/`-dev` tags; the `v0.8.x` line (2024) marks the unified-build regime and the monorepo's effective end-of-life. Per-solver release/wheel pipelines now live in the extracted repos.
