# VCell Stochastic Solver - Standalone Project

This is a standalone version of the VCell Stochastic solver, extracted from the main vcell-solvers repository.

## Overview

The VCell Stochastic solver implements stochastic simulation algorithms (Gibson/Gillespie) for biochemical reaction networks.

## Project Structure

The project is organized as follows:
- CMakeLists.txt: Root build configuration
- CMakePresets.json: Named build presets for common configurations
- VCellStoch: Main solver library and executable with include and src subdirectories
- ExpressionParser: Expression parsing library dependency
- vcommons: Common utilities dependency
- VCellMessaging: Messaging support dependency
- Tests: C++ unit tests
- python: Optional Python bindings (pybind11) and Python wrapper classes
- cmake: CMake modules

## Building

### Prerequisites

- CMake 3.13 or higher
- C++14 compatible compiler
- HDF5 library with C and C++ components
- libcurl (optional, for messaging support)
- Python 3 with development headers and pybind11 (optional, for Python bindings)

### Build Instructions

#### Linux (with preset)

    cmake --preset linux-ninja
    cmake --build --preset linux-ninja

#### macOS (with preset)

    cmake --preset macos-ninja
    cmake --build --preset macos-ninja

Preset builds place output in `build/<preset-name>/bin/`.

#### Linux/macOS (without preset)

    cmake -S . -B build
    cmake --build build --config Release

Output is placed in `build/bin/`.

#### Windows

    cmake --preset windows-msvc-hdf5
    cmake --build --preset windows-msvc-hdf5

If you are not using presets, configure manually and provide the HDF5 installation path:

    cmake -S . -B build -DHDF5_ROOT="C:\Program Files\HDF_Group\HDF5\2.1.0"
    cmake --build build --config Release

If CMake still cannot find HDF5 on Windows, set one or both of these variables before configuring:

- `HDF5_ROOT` — HDF5 installation prefix
- `HDF5_DIR` — directory containing HDF5 CMake package files, if available

The build produces:
- Static library: build/bin/libVCellStochLib.a (Linux/macOS) or build/bin/VCellStochLib.lib (Windows)
- Executable: build/bin/VCellStoch (Linux/macOS) or build\bin\VCellStoch.exe (Windows)

### Build Options

- `BUILD_SHARED_LIBS`: Build shared libraries instead of static (default: OFF)
- `BUILD_TESTING`: Enable tests (default: ON)
- `OPTION_BUILD_PYTHON_BINDINGS`: Build Python bindings via pybind11 (default: OFF)
- `OPTION_TARGET_MESSAGING`: Enable messaging support via libcurl (default: OFF)

### Python Bindings

Use the dedicated presets to build with Python bindings enabled:

#### Linux

    cmake --preset linux-ninja-pybind
    cmake --build --preset linux-ninja-pybind

#### macOS

    cmake --preset macos-ninja-pybind
    cmake --build --preset macos-ninja-pybind

#### Windows

    cmake --preset windows-msvc-pybind
    cmake --build --preset windows-msvc-pybind

Or add `-DOPTION_BUILD_PYTHON_BINDINGS=ON` to any manual configure command.

The compiled extension (`vcellstochastic_py`) is written to `build/<preset-name>/bin/`. A higher-level Python wrapper is provided in `python/src/vcellstochastic.py`:

```python
from vcellstochastic import GibsonSolver, TrialStats

solver = GibsonSolver("model.txt", "output.h5")
solver.run()
```

## Testing

C++ tests are in the `Tests/` directory and use a custom test framework (no external test library required). When Python bindings are built, `test_binding.py` is also registered as a CTest test (`TestPythonBindings`).

### Running Tests

#### Linux/macOS (C++ tests only)

    cmake -S . -B build -DBUILD_TESTING=ON
    cmake --build build
    ctest --test-dir build --verbose

#### Linux/macOS (C++ tests + Python binding test)

    cmake -S . -B build -DBUILD_TESTING=ON -DOPTION_BUILD_PYTHON_BINDINGS=ON
    cmake --build build
    ctest --test-dir build --verbose

Or use the pybind preset (which enables `OPTION_BUILD_PYTHON_BINDINGS` automatically):

    cmake --preset linux-ninja-pybind
    cmake --build --preset linux-ninja-pybind
    ctest --test-dir build/linux-ninja-pybind --verbose

##### if macOS has HDF5 library issues:

    % sudo xattr -d com.apple.quarantine /Applications/HDF_Group/HDF5/2.1.0/lib/libhdf5_cpp.320.1.0.dylib
    % sudo xattr -rd com.apple.quarantine /Applications/HDF_Group/HDF5/2.1.0/lib/

#### Windows (with presets)

    cmake --preset windows-msvc-hdf5 -DBUILD_TESTING=ON
    cmake --build --preset windows-msvc-hdf5
    ctest --test-dir build/windows-msvc-hdf5 --verbose

#### Windows (without presets)

    cmake -S . -B build -DBUILD_TESTING=ON -DHDF5_ROOT="C:\Program Files\HDF_Group\HDF5\2.1.0"
    cmake --build build --config Release
    ctest --test-dir build --verbose

Alternatively, run the test executable directly:

    .\build\windows-msvc-hdf5\bin\TestVCellStoch.exe

## Usage

### Running the Standalone Executable

    ./build/bin/VCellStoch {gibson|gillespie} input_filename output_filename

Options:
- gibson: Use Gibson's algorithm (Next Reaction Method)
- gillespie: Use Gillespie's algorithm (Direct Method)
- input_filename: Path to the input simulation file
- output_filename: Path for the output results

With messaging support (if built with OPTION_TARGET_MESSAGING):

    ./build/bin/VCellStoch {gibson|gillespie} input_filename output_filename [-tid 0]

### Using the Static Library

Link against libVCellStochLib.a (Linux/macOS) or VCellStochLib.lib (Windows) and include the headers from VCellStoch/include/.

Key classes:
- `Gibson`: Main Gibson (Next Reaction Method) algorithm implementation
- `StochModel`: Stochastic model representation (base class)
- `MultiTrialStats`: Accumulates mean, variance, min, and max statistics across multiple simulation trials; writes results to HDF5
- `Jump`: Reaction jump representation
- `StochVar`: Stochastic variable representation

## Cleanup

To remove build artifacts:

    cmake --build build --target clean

To completely remove the build directory:

    rm -rf build

## Installation

To install the built artifacts:

    cmake --install build --prefix /path/to/install

This will install:
- Library to <prefix>/lib/libVCellStochLib.a (Linux/macOS) or <prefix>/lib/VCellStochLib.lib (Windows)
- Executable to <prefix>/bin/VCellStoch
- Headers to <prefix>/include/VCellStoch/

## Platform Support

- Linux (tested on Ubuntu/Debian)
- macOS (Intel and Apple Silicon)
- Windows (with MSVC via presets)

## License

See the main VCell project for licensing information.
