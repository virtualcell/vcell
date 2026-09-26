# `vcell-ode` + `vcell-mbsolver` — extracted ODE and moving-boundary solvers

Two C/C++ numerical solvers carved out of the legacy [`vcell-solvers`](https://github.com/virtualcell/vcell-solvers) monorepo into standalone repositories, each gaining a modern CMake build and a pybind11 Python binding. **`vcell-ode`** is the collection of ODE numerical libraries/protocols (Sundials CVODE/IDA), rebuilt around a Conan + CMake/Ninja toolchain with optional libcurl live messaging. **`vcell-mbsolver`** is the moving-boundary (deforming-geometry) parabolic PDE solver, extracted with its FronTier/ExpressionParser/vcommons dependencies bundled, shipping a CLI binary, a static lib, and a `pyvcell_mbsolver` extension consumed directly by [`pyvcell`](https://github.com/virtualcell/pyvcell).

`Group: Solvers · PRs (non-bot): 9 (ode 2 / mbsolver 7) · Releases: 4 (all mbsolver) · active span: 2025-06 → 2026-06`

- `vcell-ode` — PRs: 2 · Releases: 0 · span 2025-06 → 2026-04 · Top (all-time, pre-split): fgao15, gweatherby, jcschaff; extraction work by CodeByDrescher
- `vcell-mbsolver` — PRs: 7 · Releases: 4 (v1.0.0 → v1.0.3, all 2026-06-24) · span 2026-06 · Top: bontempiuchc (solver code, 78 commits), jcschaff (packaging, 22)

> **Extraction lineage.** Both repos are extractions from `vcell-solvers`; their *deep* history (the actual numerical code, written years earlier by fgao15, gweatherby, jcschaff, bontempiuchc and others) lives in the monorepo. See the [`vcell-solvers` extraction hub](./vcell-solvers.md) and the sibling extractions [`vcell-fvsolver`](./vcell-fvsolver.md) and [`vcell-stochastic`/`vcell-nfsim`](./vcell-stochastic-nfsim.md). The contributor commit counts above are pre-split totals inherited with the code, not new work in these repos.

## Project background

These are the two solver families that were the last to leave the monorepo. The ODE solver wraps **SUNDIALS** (CVODE for ODE integration, IDA for differential-algebraic / steady-state problems) behind VCell's `IDAWin` driver and `ExpressionParser`, producing `SundialsSolverStandalone` plus a `pyvcell_odesolver` module. The moving-boundary solver couples an implicit diffusion/advection parabolic PDE solve to a **FronTier**-tracked moving interface on a deforming 2-D mesh; a single CMake build yields the `MovingBoundarySolver` CLI, `libMovingBoundaryLib`, and the `pyvcell_mbsolver` (`vcellmbsolver_py`) extension. Both repos bundle the shared `ExpressionParser`/`vcommons`/`VCellMessaging` sources the monorepo kept as submodules, so each builds standalone with no external VCell paths.

## Timeline

### 2025-06 — vcell-ode: initial carve-out

The ODE repo opened with [#2](https://github.com/virtualcell/vcell-ode/pull/2) (CodeByDrescher, "Separating and Building CI/CD"): a mechanical split that pruned non-ODE solvers from the imported monorepo tree and stood up a first cross-platform CI build of SUNDIALS across macOS/Windows/Linux (plus Docker). At this stage the repo still carried legacy build scaffolding (Travis/AppVeyor configs, vendored `Chombo`, etc.) and the diff is dominated by deletions of pruned code — it is an extraction commit, not new solver work. The repo then sat for ~10 months.

### 2026-04 — vcell-ode: Conan modernization and a clean C++20 build

[#8](https://github.com/virtualcell/vcell-ode/pull/8) (CodeByDrescher, "Stabilize new build…") is the substantive entry and the only place real engineering happened in this repo. Verified from the diff/body, it did four distinct things:

- **Build modernization.** Replaced ad-hoc dependency handling with a **Conan** recipe (`conanfile.py`, `VCellODERecipe`) generating `CMakeToolchain`/`CMakeDeps`, pulling `argparse`, `spdlog`, and (conditionally) `libcurl` (`libcurl/[<9.0]`). CMake minimum 3.13→3.16; new options `OPTION_TARGET_PYTHON_BINDING`, `OPTION_TEST_WITH_LOCALHOST`, `OPTION_STATICALLY_LINK`; platform detection consolidated on `WIN32`; a warning-cleanup pass and `sprintf` removal so the C++20 build compiles clean. Six per-platform profiles live in `conan-profiles/CI-CD/` (Linux/macOS/Windows × AMD64/ARM64).
- **Solver architecture.** Input parsing was lifted out of the solver into `VCellSolverInput` + `VCellSolverFactory` behind an abstract `VCellSolver` base; the 273-line hand-rolled argv parser in `SundialsSolverStandalone` was replaced with `argparse`; and a reusable `solve(input, output, tid)` entry point was exposed on `SundialsSolverInterface` so both the CLI and the Python module share one code path.
- **Messaging stabilization.** The 755-line `SimulationMessaging.cpp` was decomposed (down to 194 lines) into `MessageEventManager` (worker thread + queue with documented lock ordering), a `CurlProxy` polymorphism hierarchy (`AbstractCurlProxy`/`NullCurlProxy`/`CurlProxy`) replacing `#ifdef USE_MESSAGING` forests, and namespaced `JobEvent::Status`/`WorkerEvent` types. The body documents concrete fixes from TSAN/UBSAN/leak runs — a data race, a mutex accessed before its constructor finished, UB, memory leaks, an int overflow in a test — and a `std::jthread`→`std::thread` swap for compiler portability.
- **Python + CI.** Added the `pyvcell_odesolver` binding (scikit-build-core + vendored pybind11; `version()` and `solve(cvode_input_file_path, output_file_path, tid=-1)`), a `cibuildwheel` matrix (macOS-15 arm+intel, Windows, Linux x86_64+arm64, Python 3.10–3.13), `lipo` macOS Universal binaries, GHCR manylinux images, and the `mold` linker on Linux. Smoke tests moved from `IDAWin/tests/smoke/smoke.py` to a top-level GoogleTest `tests/` dir plus a `test_basic.py` exercising the wheel.

The repo has **no GitHub releases** — it is a build/library substrate, not a wheel-publishing endpoint, and the Python distribution is gated behind CI rather than tagged.

### 2026-06 — vcell-mbsolver: extraction, wheels, and four same-day releases

The moving-boundary repo was extracted and packaged in a single intense burst (all 7 PRs and all 4 releases on 2026-06-22…24). The C++ solver itself (78 commits attributed to bontempiuchc) arrived with the extraction; the seven PRs (all jcschaff) are the packaging/distribution layer.

[#1](https://github.com/virtualcell/vcell-mbsolver/pull/1) added the **wheel pipeline**, modeled on `vcell-fvsolver`: a `scikit-build-core` `pyproject.toml` driving the existing CMake into a wheel that contains *only* the `vcellmbsolver_py` extension + the `pyvcell_mbsolver` pure-Python wrapper (the static lib, CLI binary, and headers are excluded via a `python` install component and an extension-only build target). The wheel version is scraped from the CMake `project(VCellMovingBoundary VERSION …)` line for a single source of truth, and `BUILD_TESTING`/`OPTION_TARGET_MESSAGING` are forced off for wheel builds. A `wheels.yml` runs `cibuildwheel` across manylinux_2_28 (x86_64 + aarch64), macOS (x86_64 + arm64), and Windows-via-vcpkg, plus an sdist that bundles the `vcell-messaging`/`vcell-expressionparser` submodule sources and the vendored `FronTierLib` so it is self-contained. The release wheels are also **self-contained at runtime** — the wheel-repair step vendors in the HDF5 shared libraries so no system HDF5 is needed to *use* a wheel (per the [v1.0.0 release notes](https://github.com/virtualcell/vcell-mbsolver/releases/tag/v1.0.0)).

Follow-ups hardened the release: [#3](https://github.com/virtualcell/vcell-mbsolver/pull/3) added a TestPyPI dry-run, [#4](https://github.com/virtualcell/vcell-mbsolver/pull/4) a `py.typed` marker, [#5](https://github.com/virtualcell/vcell-mbsolver/pull/5) shipped `_core.pyi` type stubs (525 lines), [#7](https://github.com/virtualcell/vcell-mbsolver/pull/7) extended wheels to CPython 3.14, and [#8](https://github.com/virtualcell/vcell-mbsolver/pull/8) bumped to v1.0.3. The releases [v1.0.0](https://github.com/virtualcell/vcell-mbsolver/releases/tag/v1.0.0) → [v1.0.3](https://github.com/virtualcell/vcell-mbsolver/releases/tag/v1.0.3) all landed on 2026-06-24, mostly packaging-metadata iterations on the same solver.

**FronTier is GCC-only**, and that constraint shaped the distribution. [#6](https://github.com/virtualcell/vcell-mbsolver/pull/6) fixed the *native executable* release pipeline (`build-and-release.yml`), which had been failing on every tag: it **dropped the Windows leg** (the bundled FronTier static lib will not compile under MSVC — issue #2) and added `fail-fast: false` so that the Windows failure no longer cancelled the Linux/macOS jobs and aborted the whole release. Before this fix a tag produced *no artifacts and no release at all*. So both pipelines — the executable pipeline (`build-and-release.yml`) and the wheel pipeline (`wheels.yml`), which run independently on each `v*` tag — are Linux/macOS-only by FronTier's constraint.

### The `pyvcell_mbsolver` observer-callback API

The binding (`python/pyvcellmbsolver.cpp`, pybind11) is the load-bearing contract between this solver and `pyvcell`, and it is deliberately *callback-driven rather than file-driven*: `pyvcell` collects results through observer callbacks, and the binding itself writes no HDF5. It exposes the `MovingBoundarySetup` config (constructible programmatically or via `from_xml`), the `MovingBoundaryParabolicProblem` solver, read-only `GeometryInfo` (the moving front as `(x,y)` problem-domain tuples) and `MeshElementNode` (per-node `x/y/grid_i/grid_j/is_inside/concentration`), and two abstract trampoline base classes — `TimeClient` (one callback per time step) and `ElementClient` (every mesh node every step). The high-level wrapper re-exports these as `TimeStepObserver`/`SimulationObserver` and the `MovingBoundarySolver` facade. Registration uses `py::keep_alive<1,2>()` so observers outlive the run.

One detail is worth flagging because it is a real memory-safety landmine, documented inline in the diff: `ElementClient::element()` deliberately **does not** use `PYBIND11_OVERRIDE_PURE`. The default `py::cast` for a const-ref argument uses `return_value_policy::copy`, which would invoke `Volume`'s copy constructor — and that constructor *steals* the state pointer from the original (`original.vol.state = nullptr`), corrupting the live mesh node and causing a SIGSEGV inside FronTier on the next time step. The override instead casts a non-owning reference (`py::return_value_policy::reference`) under an explicit `gil_scoped_acquire`. A `universe_destroy()` free function resets the `Universe<2>` singleton so independent simulations (or pytest cases) can run in one process.

## Notable PRs / commits

| Link | Date | Author | Why it matters |
|---|---|---|---|
| [vcell-ode #2](https://github.com/virtualcell/vcell-ode/pull/2) | 2025-06-10 | CodeByDrescher | Initial extraction: prune non-ODE solvers, stand up cross-OS SUNDIALS CI build |
| [vcell-ode #8](https://github.com/virtualcell/vcell-ode/pull/8) | 2026-04-14 | CodeByDrescher | Conan + CMake/Ninja toolchain, argparse/spdlog/libcurl deps, `VCellSolver` refactor, messaging decomposed (755→194 LOC) with TSAN/UBSAN fixes, `pyvcell_odesolver` binding + cibuildwheel matrix |
| [vcell-mbsolver #1](https://github.com/virtualcell/vcell-mbsolver/pull/1) | 2026-06-22 | jcschaff | scikit-build-core wheel pipeline; extension-only wheels, self-contained sdist bundling FronTier + submodules; `cibuildwheel` across Linux/macOS/Windows |
| [vcell-mbsolver #5](https://github.com/virtualcell/vcell-mbsolver/pull/5) | 2026-06-24 | jcschaff | `_core.pyi` type stubs (525 lines) — full typed surface of the C++ binding |
| [vcell-mbsolver #6](https://github.com/virtualcell/vcell-mbsolver/pull/6) | 2026-06-24 | jcschaff | Fix the always-failing native-executable release: drop Windows (FronTier is GCC-only), `fail-fast: false` so Linux/macOS still produce artifacts |
| [vcell-mbsolver #7](https://github.com/virtualcell/vcell-mbsolver/pull/7) | 2026-06-24 | jcschaff | Extend wheels to CPython 3.14 |
| — (in-repo) | 2026-06 | bontempiuchc | The moving-boundary C++ solver itself (FronTier coupling, observer clients, `MeshElementNode`) — arrived with the extraction; deep history in `vcell-solvers` |

## Key contributors

- **CodeByDrescher** — sole author of both `vcell-ode` PRs; owns the Conan/CMake modernization, the messaging-layer decomposition and sanitizer fixes, and the ODE Python binding/CI.
- **bontempiuchc** — the moving-boundary solver C++ code (78 pre-split commits); also the primary author across the sibling solver extractions.
- **jcschaff** — all seven `vcell-mbsolver` PRs: wheel/sdist packaging, the `pyvcell_mbsolver` observer-callback binding and its SIGSEGV fix, type stubs, and the dual release pipelines; project lead.
- **fgao15, gweatherby (top pre-split, ode)** — original SUNDIALS/IDAWin/ExpressionParser numerical code inherited from `vcell-solvers`; no new work in the extracted repo.

## Tech & stack notes

- **Languages.** ODE: C (3.5 MB, mostly vendored SUNDIALS/Chombo), C++, Fortran, with CMake + a small Python binding. mbsolver: C (7.0 MB, mostly FronTier), C++ (2.0 MB), CMake, Python, with vestigial Perl/MATLAB from FronTier.
- **Build.** ODE — Conan 2 (`conanfile.py` + `conan-profiles/CI-CD/`) driving CMake ≥3.16 + Ninja; CLang/Mold recommended; C++20; libcurl optional via `OPTION_TARGET_MESSAGING`. mbsolver — plain CMake ≥3.13, C++14, bundled FronTier/ExpressionParser/vcommons, HDF5 (C + C++) + pybind11; build options `BUILD_SHARED_LIBS`, `BUILD_TESTING`, `VARIABLE_SPECIES_STORAGE`.
- **Python distribution.** Both use `scikit-build-core` PEP 517 builds and `cibuildwheel`. mbsolver publishes to PyPI as [`pyvcell-mbsolver`](https://pypi.org/project/pyvcell-mbsolver/) (CPython 3.10–3.14, self-contained wheels with vendored HDF5) via PyPI trusted publishing on `v*` tags; ODE builds wheels in CI but does not release them.
- **Release mechanics.** mbsolver runs two independent pipelines on each `v*` tag — `build-and-release.yml` (native Linux/macOS executable + static lib attached to the GitHub Release) and `wheels.yml` (PyPI wheels). Both are **Linux/macOS-only because FronTier does not build under MSVC**. ODE has Docker/GHCR manylinux images and a CI build matrix but no tagged releases.
- **Consumer.** `pyvcell` depends on the `pyvcell-mbsolver` wheel for its moving-boundary support (`vc.simulate_moving_boundary`), reading results through the observer callbacks rather than from files — see [`pyvcell`](./pyvcell.md) (PR #53, release 0.4.0).
