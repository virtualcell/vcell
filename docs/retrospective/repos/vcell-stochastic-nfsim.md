# `vcell-stochastic` + `vcell-nfsim` — extracted stochastic & network-free solvers

Two standalone C++ stochastic solvers carved out of the [`vcell-solvers`](./vcell-solvers.md)
monorepo in early 2026, each rebuilt as a cross-platform CMake project with a
pybind11/scikit-build Python binding and its own CI-driven release pipeline.
Both are single-author, direct-commit (0 PRs) repos owned by `bontempiuchc`.

`Group: Solvers · PRs (non-bot): 0 · Releases: 9 (8 + 1) · Active span: 2026-02 → 2026-05 · Top: bontempiuchc`

| Repo | PRs | Releases | Span | Top contributor |
|------|-----|----------|------|-----------------|
| [`vcell-stochastic`](https://github.com/virtualcell/vcell-stochastic) | 0 | 8 | 2026-03-20 → 2026-05-15 | bontempiuchc (55 commits, sole author) |
| [`vcell-nfsim`](https://github.com/virtualcell/vcell-nfsim) | 0 | 1 ([v0.1.0](https://github.com/virtualcell/vcell-nfsim/releases/tag/v0.1.0)) | 2026-02-23 → 2026-03-27 | bontempiuchc (27 commits, sole author) |

## Project background

These are two of the per-solver repositories split out of the legacy
`vcell-solvers` C/C++/Fortran monorepo during the 2024–2026 extraction effort
(see the [`vcell-solvers`](./vcell-solvers.md) entry, the extraction hub). Both
are code-first, release-driven projects: the C++ simulation logic is mature and
largely unchanged from its monorepo origin, while nearly all of the new work in
these repos is build-system modernization (CMake presets, multi-platform CI) and
the addition of Python bindings that let VCell's Python ecosystem call the
solvers directly instead of shelling out to executables.

- **`vcell-stochastic`** implements the two classic exact stochastic simulation
  algorithms for biochemical reaction networks: **Gibson** (the Next Reaction
  Method, using an indexed priority queue / `IndexedTree`) and Gillespie's Direct
  Method. It ships a static library (`libVCellStochLib.a`), the `VCellStoch`
  executable, and a `vcellstochastic_py` extension. Multi-trial ensemble runs are
  reduced to mean/variance/min/max statistics written to **HDF5** via
  `MultiTrialStats`. It carries its own copies of the shared `ExpressionParser`,
  `vcommons`, and `VCellMessaging` libraries.
- **`vcell-nfsim`** is VCell's fork of **NFsim v1.11** — network-free, rule-based
  stochastic simulation (the BioNetGen / NFsim engine, including the bundled
  `nauty` graph-canonicalization library and a vendored `muParser`). It is
  packaged as a standalone CMake executable (`NFsim`) plus a `pyvcell_nfsim`
  wheel.

## Timeline

### vcell-nfsim — Feb–Mar 2026: fork, multi-platform CI, then a wheel

The repo opens with a 322k-line "Initial commit"
([`4e25ecaf4`](https://github.com/virtualcell/vcell-nfsim/commit/4e25ecaf4),
2026-02-23) that drops in the entire upstream NFsim v1.11 source tree
(`src/NFcore`, `NFfunction`, `NFinput/output`, `NFreactions`, `NFscheduler`,
`nauty24`), the BioNetGen Perl tooling (`BNG2.pl`, `BNG/`), the v1.11 manual PDF,
and example `models/`. The VCell-specific divergence from stock NFsim lives in
`VCell/src/` — `VCellNFSim.cpp`, `NFMonitor`, and `JMSHolder` — which wire NFsim
into VCell's **JMS simulation-messaging and progress-reporting** harness
(`SimulationMessaging`, `GitDescribe`). These files are not new: their headers are
attributed to gweatherby and dated 2015, so the fork preserves VCell's long-standing
messaging integration that previously lived inside `vcell-solvers`.

Most of the early commits are CI plumbing. `cmake-multi-platform.yml`
([`be46fd1b5`](https://github.com/virtualcell/vcell-nfsim/commit/be46fd1b5)) sets
up Linux/macOS/Windows builds; subsequent commits fight Windows specifics —
installing **Strawberry Perl** for the BNG toolchain, creating symbolic links so
the `oscillatory_system` model test runs, and adding protection macros around
`nauty.h` for MSVC ([`5b1bb3c61`](https://github.com/virtualcell/vcell-nfsim/commit/5b1bb3c61)).
The test strategy ([`5805ba97c`](https://github.com/virtualcell/vcell-nfsim/commit/5805ba97c))
is a smoke test followed by running each `.bngl` model in `models/`.

The Python binding lands on 2026-03-27
([`188e00024`](https://github.com/virtualcell/vcell-nfsim/commit/188e00024)): a
thin pybind11 wrapper (`src/pyvcell-nfsim-main.cpp`) exposing a single
`run_simulation(argMap, verbose)` entry point that calls
`initSystemFromFlags` → `runFromArgs`, plus a `pyproject.toml` using the
**scikit-build-core** backend (authored as "pyvcell_nfsim", Jim Schaff listed as
author, MIT, Python 3.9–3.12). The `publish-python-package.yml` workflow
([`daed8d5e1`](https://github.com/virtualcell/vcell-nfsim/commit/daed8d5e1)) builds
wheels with **cibuildwheel 2.16.5** across ubuntu/windows/macos-14 for cp39–cp312
(skipping musllinux and manylinux_i686) plus an sdist. The lone tagged release
[v0.1.0](https://github.com/virtualcell/vcell-nfsim/releases/tag/v0.1.0) follows
the same day.

### vcell-stochastic — Mar–May 2026: extraction, presets, then bindings

The "Initial commit of standalone stochastic solver"
([`79641c587`](https://github.com/virtualcell/vcell-stochastic/commit/79641c587),
2026-03-20) brings over the `VCellStoch` solver (`Gibson.cpp`, `IndexedTree.cpp`,
`Jump.cpp`, `StochModel/StochVar`, `MultiTrialStats.cpp`) together with vendored
copies of `ExpressionParser`, `vcommons`, and `VCellMessaging`, and a root
`CMakeLists.txt`. Early commits sort out HDF5 discovery (dropping macOS-specific
include-path hardcoding in favor of CMake's own resolution,
[`5921fd8be`](https://github.com/virtualcell/vcell-stochastic/commit/5921fd8be)).

In mid-April the `Tests/` directory is pulled across from `vcell-solvers`
([`997485fa4`](https://github.com/virtualcell/vcell-stochastic/commit/997485fa4)),
Windows builds and a smoke ctest are made to work
([`7feabd2e6`](https://github.com/virtualcell/vcell-stochastic/commit/7feabd2e6)),
and a C++14 portability issue is fixed by removing `#ifdef` guards
([`339dc1e25`](https://github.com/virtualcell/vcell-stochastic/commit/339dc1e25)).
The README documents CMake **presets** (`linux-ninja`, `macos-ninja`,
`windows-msvc-hdf5`, plus `-pybind` variants) and the macOS library-quarantine
workaround.

The bulk of the commit log is iteration on `release.yml` — a long series of
"Update release.yml" commits across late April and May driving a CI build-and-release
pipeline. This wrestles with cross-platform HDF5: refining `HDF5_DIR`/`HDF5_ROOT`
hints ([`3b9b712d4`](https://github.com/virtualcell/vcell-stochastic/commit/3b9b712d4),
[`a8aa1e895`](https://github.com/virtualcell/vcell-stochastic/commit/a8aa1e895))
and configuring a **vcpkg** toolchain for the Windows build
([`a3a2c0549`](https://github.com/virtualcell/vcell-stochastic/commit/a3a2c0549)).
The eight `ci-*` releases are timestamp-tagged CI artifacts produced by this
workflow (four on 2026-04-28 alone, then 2026-05-12/14/15) rather than curated
semantic versions.

The Python binding arrives last
([`81b9f52b6`](https://github.com/virtualcell/vcell-stochastic/commit/81b9f52b6),
2026-04-28; expanded [`1ba887076`](https://github.com/virtualcell/vcell-stochastic/commit/1ba887076),
2026-05-14). `python/src/bindings.cpp` exposes a `vcellstochastic_py` module with
`StochModel`, `Gibson`, and `MultiTrialStats` classes — including a lambda adapter
so `MultiTrialStats.addSample` accepts a Python `list[float]` — and `MultiTrialStats`
gains a `writeOutput`/HDF5 path. A higher-level `python/src/vcellstochastic.py`
wrapper and `test_binding.py` smoke test round it out, and the release workflow is
amended to ship the Python module alongside the binaries
([`42bb0624b`](https://github.com/virtualcell/vcell-stochastic/commit/42bb0624b)).

## Notable commits

| Commit | Date | Repo | Why it matters |
|--------|------|------|----------------|
| [`4e25ecaf4`](https://github.com/virtualcell/vcell-nfsim/commit/4e25ecaf4) | 2026-02-23 | nfsim | Forks all of NFsim v1.11 (322k lines) + BioNetGen + `nauty` into a standalone repo |
| [`be46fd1b5`](https://github.com/virtualcell/vcell-nfsim/commit/be46fd1b5) | 2026-03-12 | nfsim | Multi-platform CMake CI (Linux/macOS/Windows; Strawberry Perl for BNG) |
| [`188e00024`](https://github.com/virtualcell/vcell-nfsim/commit/188e00024) | 2026-03-27 | nfsim | pybind11 `pyvcell_nfsim` binding + scikit-build-core `pyproject.toml` |
| [`daed8d5e1`](https://github.com/virtualcell/vcell-nfsim/commit/daed8d5e1) | 2026-03-27 | nfsim | cibuildwheel pipeline (cp39–cp312) for [v0.1.0](https://github.com/virtualcell/vcell-nfsim/releases/tag/v0.1.0) |
| [`79641c587`](https://github.com/virtualcell/vcell-stochastic/commit/79641c587) | 2026-03-20 | stochastic | Extracts `VCellStoch` + ExpressionParser/vcommons/messaging into standalone repo |
| [`997485fa4`](https://github.com/virtualcell/vcell-stochastic/commit/997485fa4) | 2026-04-16 | stochastic | Brings the `Tests/` suite across from vcell-solvers |
| [`339dc1e25`](https://github.com/virtualcell/vcell-stochastic/commit/339dc1e25) | 2026-04-17 | stochastic | C++14 portability fix (removes `#ifdef` guards) |
| [`52ed29fa9`](https://github.com/virtualcell/vcell-stochastic/commit/52ed29fa9) | 2026-04-24 | stochastic | Adds the CI build-and-release workflow |
| [`a3a2c0549`](https://github.com/virtualcell/vcell-stochastic/commit/a3a2c0549) | 2026-04-24 | stochastic | vcpkg toolchain for Windows HDF5 |
| [`81b9f52b6`](https://github.com/virtualcell/vcell-stochastic/commit/81b9f52b6) | 2026-04-28 | stochastic | `vcellstochastic_py` pybind11 binding (Gibson, MultiTrialStats) |
| [`42bb0624b`](https://github.com/virtualcell/vcell-stochastic/commit/42bb0624b) | 2026-05-15 | stochastic | Ships the high-level Python wrapper module in the release artifacts |

## Key contributors

- **bontempiuchc / Chris Bontempi** — sole author of both repos (55 + 27 commits,
  0 PRs). Drove the extraction from `vcell-solvers`, cross-platform CMake build and
  CI, HDF5/vcpkg configuration, and the pybind11/scikit-build Python bindings.
- **gweatherby (Gerard Weatherby)** — original author (2015) of the VCell-side NFsim
  messaging integration (`VCellNFSim.cpp`, `NFMonitor`, `JMSHolder`) carried into
  the fork; not an active committer in these repos.
- **jcschaff (Jim Schaff)** — listed as author in `pyvcell_nfsim`'s `pyproject.toml`.

## Tech & stack notes

- **Languages:** C++ (C++14) dominant in both; `vcell-nfsim` also carries the
  upstream Perl BioNetGen tooling and a small Fortran/MATLAB footprint from NFsim.
- **Build:** CMake (≥3.13). `vcell-stochastic` uses `CMakePresets.json` (ninja
  presets with `-pybind` variants); both default to static libs and `BUILD_TESTING=ON`.
- **Bindings:** pybind11. `vcell-nfsim` builds wheels via **scikit-build-core** +
  **cibuildwheel** (cp39–cp312, ubuntu/windows/macos-14); `vcell-stochastic` builds
  its `vcellstochastic_py` extension through CMake presets.
- **Key deps:** HDF5 (multi-trial statistics output in stochastic), `nauty` and
  vendored `muParser` (nfsim), libcurl (optional JMS messaging), vcpkg for Windows
  dependency resolution.
- **Release mechanics:** Both are direct-commit, no-PR repos. `vcell-stochastic`
  emits timestamp-tagged `ci-*` artifact releases from `release.yml`;
  `vcell-nfsim` has a single curated `v0.1.0` (`workflow_dispatch`-triggered wheel
  publish). For pre-extraction history of both solvers, see the
  [`vcell-solvers`](./vcell-solvers.md) entry.
