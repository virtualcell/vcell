# vcell-fvsolver

Finite-volume reaction-diffusion-advection PDE solver for computational cell biology, extracted from the `vcell-solvers` monorepo into a standalone CMake project that produces a CLI executable, a Docker image, per-platform release binaries, and a `pyvcell_fvsolver` Python wheel.

**Solvers · 16 PRs (non-bot) · 14 releases · 2024-05 → 2026-06 · key contributors: jcschaff, CodeByDrescher** (all-time commit counts — fgao15 593, gweatherby 464, jcschaff 306, CodeByDrescher 146 — include pre-split `vcell-solvers` history that traveled with the extraction; post-split work in this repo is overwhelmingly jcschaff then CodeByDrescher).

## Project background

`vcell-fvsolver` is the C/C++/Fortran finite-volume PDE solver that VCell uses to integrate reaction-diffusion-advection systems on structured meshes. It consumes VCell solver input files (`.fvinput`, `.vcg`) and emits results as `.log`, `.zip`, `.mesh`, `.meshmetrics`, and `.hdf5` (README). The repo also vendors a bridged copy of Smoldyn (particle-based stochastic spatial solver) under `smoldyn-2.38/` and its VCell integration layer (`bridgeVCellSmoldyn/`), so a single build tree produces both `FiniteVolume_x64` and `smoldyn_x64`. The codebase is the historical VCell FV/Smoldyn source — hence the large vendored C/C++/Fortran/Perl footprint in the language stats — but with all sibling solvers (notably Chombo) stripped out. It is the most-used of the extracted solvers: `pyvcell` calls into it via the `pyvcell_fvsolver` wrapper.

## Timeline

### 2024 H1 — extraction and Python packaging

The repo opens with [#1](https://github.com/virtualcell/vcell-fvsolver/pull/1) (2024-05-28), which extracts the FV solver from `vcell-solvers` at monorepo `v0.8.0` and "strip[s] away other solvers" — the diff is +58/-2,700,463 across 5,174 files, the deletions almost entirely the **Chombo** AMR tree (91 top-level dirs removed) plus the old Travis/AppVeyor CI. The stated intent: make the solver "more nimble" so its dependencies can be upgraded independently of the rest of VCell.

[#2](https://github.com/virtualcell/vcell-fvsolver/pull/2) (2024-06-02) is the pivotal one: it adds the **`pyvcell_fvsolver` Python binding** using **pybind11** + **scikit-build-core**, building an independent CMake target gated on `OPTION_TARGET_PYTHON_BINDING`. A new `src/main.cpp` declares `PYBIND11_MODULE(_core, ...)` exposing exactly two functions — `version()` and `solve(inputFilename, outputDir)` — and `src/pyvcell_fvsolver/__init__.py` re-exports them. The actual entry point is a new `src/SolverMain.cpp` (`int solve(...)`) that creates the output dir, runs `FVSolver`, and disables the messaging path (`#undef USE_MESSAGING`) so the solver runs as a library call rather than a JMS worker. Supporting changes constify `FVSolver`'s `outputPath`/ctor and add `#define _HAS_STD_BYTE 0` guards for the MSVC build. The Dockerfile was modernized in the same PR (Ubuntu 22.04, Ninja, `libboost-all-dev`, python3) with FV-only build flags.

[#4](https://github.com/virtualcell/vcell-fvsolver/pull/4) (2024-06-04) builds **wheels for all platforms**, wiring up **cibuildwheel** (`2.18.1`) for native macOS Intel/ARM + Windows, plus a hand-rolled **manylinux2014** matrix (`quay.io/pypa/manylinux2014_x86_64` + `auditwheel repair`) for CPython 3.9–3.12, with `delocate` (macOS) and `delvewheel` (Windows) for shared-library repair. [#5](https://github.com/virtualcell/vcell-fvsolver/pull/5) follows with CMake settings for statically linked dependencies. First releases land here: [v0.9.0](https://github.com/virtualcell/vcell-fvsolver/releases/tag/v0.9.0) (Python binding), then [v0.9.1](https://github.com/virtualcell/vcell-fvsolver/releases/tag/v0.9.1)/[v0.9.2](https://github.com/virtualcell/vcell-fvsolver/releases/tag/v0.9.2) push `pyvcell-fvsolver` to TestPyPI and then PyPI.

### 2024 H2 → 2025 Q1 — Smoldyn correctness, smoke tests, first real PyPI

[#6](https://github.com/virtualcell/vcell-fvsolver/pull/6) (2024-07-30) adds a **smoke test for FiniteVolume and Smoldyn** comparing solver HDF5 output against checked-in `.hdf5.expected` fixtures. [v0.9.3](https://github.com/virtualcell/vcell-fvsolver/releases/tag/v0.9.3) replaces the legacy vendored **libzip-1.2.0 with libzippp**, and [v0.9.4](https://github.com/virtualcell/vcell-fvsolver/releases/tag/v0.9.4) builds Smoldyn inside the Docker image and puts `bin/` on the PATH. [#7](https://github.com/virtualcell/vcell-fvsolver/pull/7) (2025-01-17) fixes a **Smoldyn error reported by Boris** (an external user); [#8](https://github.com/virtualcell/vcell-fvsolver/pull/8) drops the now-unneeded `libzippp.cpp` patch. [#10](https://github.com/virtualcell/vcell-fvsolver/pull/10) builds standalone Smoldyn and adds ctests, and [#9](https://github.com/virtualcell/vcell-fvsolver/pull/9) cuts the first non-pre-release on PyPI as [v0.9.5](https://github.com/virtualcell/vcell-fvsolver/releases/tag/v0.9.5) / pip `0.1.0`, adding C++ and Python-binding unit tests.

### 2025 Q1–Q2 — Smoldyn performance and toolchain modernization (CodeByDrescher)

[#11](https://github.com/virtualcell/vcell-fvsolver/pull/11) (2025-03-14, +62,765/-1,641) is the largest substantive change and bundles two independent efforts. (1) A **Smoldyn surface/panel lookup speedup**: linear `stringfind(snames, nsrf, name)` scans are replaced by O(1) hashtable lookups, backed by a new C-safe data-structures library (`Hashtable`/`LinkedList`/`Vector` under `smoldyn-2.38/source/lib/ComplexDataStructures/`, FNV-1a hashing with chained buckets and a method-pointer API). New struct indices `snametosrf` and `pnametopanel[PSMAX]` sit alongside the existing arrays, and new entry points `identifypanelinsurfacebyname()` / `identifypanelindexinsurfacebyname()` are threaded through `smolsurface.c`, `smolsim.c`, and a dozen other call sites; the change is intended to be behavior-preserving (numerically identical output). The bridge layer was refactored so Smoldyn owns its VCell integration — `bridgeVCellSmoldyn/vcell_smoldyn.cpp` split into a thin `vcell_smoldyn_entrypoint.cpp` plus `smoldyn-2.38/source/Smoldyn/vcell_smoldyn.cpp`. (2) A **CI/CD modernization to an all-LLVM toolchain**: old `pip.yml`/`wheels.yml` removed in favor of a **Conan**-managed dependency build (hdf5, libaec, libzip, zlib), **clang/clang++/flang** (LLVM 21, no gcc/gfortran), per-platform Conan profiles, dedicated `Dockerfile_fvsolver_manylinux_2_34_{x86_64,aarch64}` images, and a `build_wheels.yml` cibuildwheel matrix (Python 3.10–3.13). Shipped as [0.9.6](https://github.com/virtualcell/vcell-fvsolver/releases/tag/0.9.6). [0.9.7](https://github.com/virtualcell/vcell-fvsolver/releases/tag/0.9.7) follows with Docker bugfixes and test improvements, and [#12](https://github.com/virtualcell/vcell-fvsolver/pull/12) (2025-04-11) deploys PyPI [pypi-0.1.1](https://github.com/virtualcell/vcell-fvsolver/releases/tag/pypi-0.1.1) with the Smoldyn and memory fixes (smoke test now templated via `SimID_*.fvinput.in`).

### 2026 Q2 — Docker publishing and CD hardening

A burst of CD work in 2026: [#14](https://github.com/virtualcell/vcell-fvsolver/pull/14) adds a `workflow_dispatch` **Docker workflow** that builds and pushes multi-arch (`x86_64` + `aarch64` via QEMU/Buildx) `manylinux_2_34` images to GHCR; [#15](https://github.com/virtualcell/vcell-fvsolver/pull/15) experiments with building Windows wheels without cibuildwheel; [#16](https://github.com/virtualcell/vcell-fvsolver/pull/16) fixes the non-manually-dispatched Docker build. [#17](https://github.com/virtualcell/vcell-fvsolver/pull/17) raises the supported **Python version ceiling** ([0.10.2](https://github.com/virtualcell/vcell-fvsolver/releases/tag/0.10.2)), and [#18](https://github.com/virtualcell/vcell-fvsolver/pull/18) makes the CD pipeline **automatically attach platform binaries to GitHub releases** and tightens the macOS `install_name_tool` rpath rewriting (excluding `/usr/lib/`). Releases [0.10.1](https://github.com/virtualcell/vcell-fvsolver/releases/tag/0.10.1) through [0.10.5](https://github.com/virtualcell/vcell-fvsolver/releases/tag/0.10.5) cover the Smoldyn hashing rollout, Python ceiling bump, Python/C++ version-number alignment, and CD bug fixes.

## Notable PRs/commits

| PR | Date | Author | Why it matters |
|----|------|--------|----------------|
| [#1](https://github.com/virtualcell/vcell-fvsolver/pull/1) | 2024-05-28 | jcschaff | Extracts FV solver from `vcell-solvers` v0.8.0; strips Chombo and legacy CI (-2.7M lines) — the split itself |
| [#2](https://github.com/virtualcell/vcell-fvsolver/pull/2) | 2024-06-02 | jcschaff | pybind11 + scikit-build-core binding; `solve()`/`version()` library entry point that `pyvcell` consumes |
| [#4](https://github.com/virtualcell/vcell-fvsolver/pull/4) | 2024-06-04 | jcschaff | cibuildwheel + manylinux2014/delocate/delvewheel wheel pipeline across mac/win/linux, py3.9–3.12 |
| [#6](https://github.com/virtualcell/vcell-fvsolver/pull/6) | 2024-07-30 | jcschaff | Smoke test asserting solver HDF5 output vs checked-in expected fixtures |
| [#7](https://github.com/virtualcell/vcell-fvsolver/pull/7) | 2025-01-17 | jcschaff | Fixes a Smoldyn error reported by an external user (Boris) |
| [#9](https://github.com/virtualcell/vcell-fvsolver/pull/9) | 2025-01-20 | jcschaff | First real PyPI release (0.1.0) with C++ and binding unit tests |
| [#11](https://github.com/virtualcell/vcell-fvsolver/pull/11) | 2025-03-14 | CodeByDrescher | O(1) hashtable Smoldyn surface/panel lookups + full Conan/LLVM(clang/flang) CI overhaul |
| [#12](https://github.com/virtualcell/vcell-fvsolver/pull/12) | 2025-04-11 | jcschaff | PyPI 0.1.1 with Smoldyn + memory fixes; templated smoke-test fixtures |
| [#14](https://github.com/virtualcell/vcell-fvsolver/pull/14) | 2026-04-06 | CodeByDrescher | Multi-arch GHCR Docker image publishing (`manylinux_2_34` x86_64/aarch64) |
| [#18](https://github.com/virtualcell/vcell-fvsolver/pull/18) | 2026-06-10 | CodeByDrescher | Auto-attach platform binaries to releases; fix macOS rpath rewriting |

## Key contributors

- **jcschaff** (Jim Schaff) — drove the entire 2024–early-2025 arc: the extraction, the pybind11 binding and `solve()` entry point, the cross-platform wheel pipeline, smoke tests, libzip→libzippp, and the first PyPI releases.
- **CodeByDrescher** — owns the 2025-Q1-onward modernization: the C-safe hashtable Smoldyn speedup, the Conan + all-LLVM (clang/flang) toolchain migration, multi-arch Docker publishing, Python-ceiling bumps, and CD/release-artifact automation.
- *fgao15, gweatherby, danv61, etc.* appear at the top of the all-time commit counts only because pre-split `vcell-solvers` history carried into this repo; they did not author the post-split PRs above.

## Tech & stack notes

- **Languages:** C++ (~10.6 MB) and C (~6.2 MB) dominate, with vendored Perl (~1 MB), Fortran (~350 KB), and small CMake/Python/Dockerfile/BitBake footprints — the historical VCell + Smoldyn source tree.
- **Build:** CMake-driven, feature-gated by `OPTION_TARGET_*` flags (`FV_SOLVER`, `SMOLDYN_SOLVER`, `PYTHON_BINDING`, `MESSAGING`, `TESTS`). Migrated from GCC/gfortran to an all-**LLVM** toolchain (clang/clang++/flang) with **Conan 2** managing native deps (hdf5, libaec, libzip, zlib).
- **Python packaging:** pybind11 + scikit-build-core, published to PyPI as `pyvcell-fvsolver`; wheels via cibuildwheel (Python 3.10–3.13) with manylinux_2_34 Docker base images, `delocate` (macOS) and `delvewheel` (Windows) repair, and a `lipo` universal-mac step.
- **Outputs:** CLI `FiniteVolume_x64`/`smoldyn_x64` binaries attached to GitHub releases, a multi-arch `ghcr.io/virtualcell/vcell-fvsolver` Docker image, and the PyPI wheel.
- **CI/release:** GitHub Actions `cd.yml` (per-platform native builds + tests + PyPI publish on release) plus a `workflow_dispatch` `docker.yml`. ctest + a Python smoke test (`VCell/tests/smoke/smoke.py`) gate correctness against HDF5-equivalent expected output.
- *Note:* this repo is absent from org Project #1 (no board items reference it); evidence here is entirely diffs + releases + README, consistent with the brief's evidence hierarchy.
