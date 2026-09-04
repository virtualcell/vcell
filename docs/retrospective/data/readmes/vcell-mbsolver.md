# VCell Moving Boundary Solver

A C++ solver library for moving-boundary problems in biological systems.
A single build produces three artifacts:

| Artifact | Description |
|---|---|
| `MovingBoundarySolver` | Standalone command-line binary |
| `libMovingBoundaryLib` | Linkable static (or shared) library |
| `vcellmbsolver_py` | Python extension module (pybind11) |

---

## Prerequisites

### All platforms

| Dependency | Notes |
|---|---|
| CMake ≥ 3.13 | <https://cmake.org/download/> |
| C++14 compiler | GCC 7+, Clang 6+, MSVC 2017+ |
| HDF5 (C + C++) | See platform sections below |
| Python 3 + headers | Python 3.8+ recommended |
| pybind11 | `pip install pybind11` or system package |
All former VCell monorepo dependencies (**FronTier**, **ExpressionParser**,
**vcommons**) are bundled in this repo and built automatically — no external
paths required.

---

## Building

### Ubuntu / Debian

```bash
# System dependencies
sudo apt-get install cmake libhdf5-dev python3-dev python3-pip
pip3 install pybind11

# Configure (substitute your actual library paths)
cmake -S . -B build -DCMAKE_BUILD_TYPE=Release

# Build everything
cmake --build build --parallel
```

Output files land in `build/bin/`:

```
build/bin/
  MovingBoundarySolver        # binary
  libMovingBoundaryLib.a      # static library
  vcellmbsolver_py.cpython-*.so  # Python module
```

---

### macOS

```bash
# Dependencies via Homebrew
brew install cmake hdf5 python pybind11

# Configure
cmake -S . -B build -DCMAKE_BUILD_TYPE=Release

# Build everything
cmake --build build --parallel
```

For Apple Silicon the build system detects the architecture automatically and
sets `-DCMAKE_OSX_ARCHITECTURES=arm64`. On Intel Macs it sets `x86_64`.

---

### Windows (Visual Studio)

Install prerequisites:
- [CMake](https://cmake.org/download/)
- [Python 3](https://www.python.org/downloads/) (check "Add to PATH")
- HDF5: use the official HDF Group Windows installer or [vcpkg](https://vcpkg.io)
  (`vcpkg install hdf5`)
- pybind11: `pip install pybind11` or vcpkg (`vcpkg install pybind11`)

```bat
cmake -S . -B build

cmake --build build --config Release --parallel
```

If using vcpkg, add `-DCMAKE_TOOLCHAIN_FILE=C:\vcpkg\scripts\buildsystems\vcpkg.cmake`
to the configure step so CMake finds the vcpkg-installed packages automatically.

Output files land in `build\bin\Release\`.

---

## Build options

| CMake variable | Default | Description |
|---|---|---|
| `CMAKE_BUILD_TYPE` | `Release` | `Release`, `Debug`, `RelWithDebInfo`, `MinSizeRel` |
| `BUILD_SHARED_LIBS` | `OFF` | Build `libMovingBoundaryLib` as a shared library instead of static |
| `BUILD_TESTING` | `ON` | Also build the `TestMovingBoundary` test executable |
| `VARIABLE_SPECIES_STORAGE` | `OFF` | Enable dynamic species storage (`-DMB_VARY_MASS`) |

Example — debug build, shared library, no tests:

```bash
cmake -S . -B build \
  -DCMAKE_BUILD_TYPE=Debug \
  -DBUILD_SHARED_LIBS=ON \
  -DBUILD_TESTING=OFF \
cmake --build build --parallel
```

---

## Running the tests

Tests are built by default (`BUILD_TESTING=ON`). After a successful build:

```bash
cd build
ctest --output-on-failure
```

### Test suites

All three suites run automatically when you invoke `ctest`. The Python tests
require `pytest` (`pip install pytest`).

| Suite | Name in ctest | Framework |
|---|---|---|
| Moving boundary unit tests (~150 cases) | `TestMovingBoundary.*` | Google Test |
| Expression parser smoke test | `ExpressionParserTest` | custom main |
| Python wrapper tests | `PyVcellMbSolver` | pytest |

### Python test prerequisites

No manual setup required. CMake creates an isolated virtual environment in
`build/python_venv/` at configure time and installs `pytest` into it
automatically. The venv is recreated on every `cmake -S . -B build` run.

The Python tests import `vcellmbsolver_py` (the compiled extension) and
`vcellmbsolver` (the pure-Python wrapper in `python/`). ctest sets
`PYTHONPATH` so both are importable from the build tree without installation.

### Useful ctest options

```bash
# Run in parallel
ctest --output-on-failure -j$(nproc)

# Run only tests whose name matches a pattern
ctest --output-on-failure -R "algo"
ctest --output-on-failure -R "ExpressionParser"
ctest --output-on-failure -R "PyVcellMbSolver"

# List all registered tests without running them
ctest -N

# Show full output even for passing tests
ctest -V
```

### Running the Python tests directly

After configuring, the venv already has `pytest` installed. Run the suite
directly using the venv's Python:

```bash
PYTHONPATH=build/bin:python build/python_venv/bin/python -m pytest python/tests -v
```

On Windows, substitute `build\python_venv\Scripts\python.exe`.

### Skipping tests

Pass `-DBUILD_TESTING=OFF` at configure time to skip building and registering
the test executables entirely (this also disables the Python tests):

```bash
cmake -S . -B build -DBUILD_TESTING=OFF
```

---

## Using the binary

```bash
./build/bin/MovingBoundarySolver <input.xml> <output.h5>
```

The input file format is described in `metadata/MovingBoundarySolverInputFile.docx`
and validated by `Solver/MovingBoundarySetup.xsd`.

---

## Linking the library

Add to your project's `CMakeLists.txt`:

```cmake
find_library(MB_LIB MovingBoundaryLib HINTS /path/to/vcell-mbsolver/build/bin)
find_path(MB_INCLUDE MovingBoundaryParabolicProblem.h
    HINTS /path/to/vcell-mbsolver/Solver/include)

target_link_libraries(my_target PRIVATE ${MB_LIB})
target_include_directories(my_target PRIVATE ${MB_INCLUDE})
```

Or install the project first and use the installed headers under
`<prefix>/include/vcell-mbsolver/`.

---

## Using the Python module

The bindings are shipped as the **`pyvcell_mbsolver`** package: the compiled
extension is the private submodule `pyvcell_mbsolver._core`, and the high-level
wrapper (`MovingBoundarySolver`, observer base classes) is the package itself.

```python
import sys
sys.path.insert(0, "/path/to/vcell-mbsolver/build/bin")

import pyvcell_mbsolver
from pyvcell_mbsolver import MovingBoundarySolver   # high-level wrapper
from pyvcell_mbsolver import _core                  # low-level C++ API
# See python/pyvcellmbsolver.cpp for the exposed _core API
```

After `cmake --install build --prefix /usr/local`, the package is installed to
the active Python's `site-packages` and importable directly:

```python
import pyvcell_mbsolver
```

---

## Python package & wheels

The repository also ships a [PEP 517](https://peps.python.org/pep-0517/) build
configuration (`pyproject.toml`, using the
[`scikit-build-core`](https://scikit-build-core.readthedocs.io/) backend) that
drives the same CMake build to produce an installable Python wheel. The wheel
contains only the `pyvcell_mbsolver` package (the `_core` extension and its
pure-Python wrapper) — not the C++ library, CLI binary, or headers.

### Install from a downloaded wheel

CI builds redistributable wheels for **Linux** (x86_64 + arm64) and **macOS**
(x86_64 + arm64, macOS 15+) and uploads them as workflow artifacts (see the
**Wheels** GitHub Actions workflow). Download the wheel for your
platform/Python version and:

```bash
pip install pyvcell_mbsolver-<version>-<tags>.whl
python -c "import pyvcell_mbsolver; from pyvcell_mbsolver import _core; print('ok')"
```

The wheels are self-contained: the shared HDF5 libraries are vendored in by the
wheel-repair step (`auditwheel` / `delocate`), so no system HDF5 install is
required to *use* a wheel.

> **Windows is not currently supported.** The bundled FronTier library is
> Unix/GCC code that does not compile under MSVC (its `POINT`/`boolean` types
> clash with the Windows SDK and it relies on GCC anonymous-struct extensions).
> Windows users can build/run under WSL using the Linux wheels.

### Build a wheel locally

Building from source still needs the native toolchain and dependencies from the
[Prerequisites](#prerequisites) section (CMake, a C++14 compiler, HDF5, Boost):

```bash
pip install build
python -m build --wheel          # writes dist/vcellmbsolver-*.whl
pip install dist/vcellmbsolver-*.whl
```

`pip install .` works too. Wheel builds set `BUILD_TESTING=OFF` and
`OPTION_TARGET_MESSAGING=OFF` automatically (see `[tool.scikit-build]` in
`pyproject.toml`).

### Publishing to PyPI

The **Wheels** workflow has a `publish` job that uploads the built wheels and
sdist to PyPI via [trusted publishing](https://docs.pypi.org/trusted-publishers/)
(OIDC — no stored API token). It runs **only on `v*` tag pushes** and stays
dormant until a one-time setup is done on PyPI:

1. Register the project on PyPI (claim the `pyvcell_mbsolver` name).
2. Under the project's *Publishing* settings, add a **trusted publisher**:
   - Owner: `virtualcell`, Repository: `vcell-mbsolver`
   - Workflow: `wheels.yml`, Environment: `pypi`
3. Create the `pypi` environment in the repo settings (optionally with
   reviewers/branch protection).

Once configured, pushing a `vX.Y.Z` tag builds all wheels and publishes them.
Until then, normal pushes and PRs simply produce downloadable wheel artifacts.

#### Dry run on TestPyPI first (recommended)

The workflow also has a `publish-testpypi` job to rehearse the release against
[TestPyPI](https://test.pypi.org) before touching real PyPI. It runs only when
the workflow is **manually dispatched** with the `testpypi` flag, and needs its
own one-time setup mirroring the above:

1. Create a [TestPyPI](https://test.pypi.org) account (separate from PyPI; 2FA
   required).
2. At <https://test.pypi.org/manage/account/publishing/>, add a **pending
   publisher**:
   - PyPI Project Name: `pyvcell_mbsolver`
   - Owner: `virtualcell`, Repository: `vcell-mbsolver`
   - Workflow: `wheels.yml`, Environment: `testpypi`
3. Create a `testpypi` environment in the repo settings.

Then trigger the dry run (no tag needed):

```bash
gh workflow run wheels.yml -f testpypi=true
```

(or **Actions → Wheels → Run workflow**, check the box). It builds all wheels +
sdist and uploads them to TestPyPI; `skip-existing` keeps repeat runs of the
same version from failing. Verify at
<https://test.pypi.org/project/pyvcell-mbsolver/>, then do the real release by
tagging `vX.Y.Z`.
