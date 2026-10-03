# NFsim v1.11 with vCell changes

This project provides both a standalone NFsim C++ executable and Python bindings (`pyvcell_nfsim`).

## Building the C++ Executable

### Linux/macOS
``` shell
$ cmake -S . -B build
$ cmake --build build --config Release
```

### Windows
``` shell
> cmake -S . -B build -G "Visual Studio 17 2022" -A x64
> cmake --build build --config Release
```

The executable will be available at `build/bin/NFsim`.

## Building the Python Package

### Prerequisites
- Python 3.9 or later
- pybind11
- scikit-build-core

### Install in Development Mode
```shell
$ pip install -e .
```

This will build the Python extension module and install it in editable mode.

### Build a Wheel
```shell
$ pip install build
$ python -m build
```

The wheel will be created in the `dist/` directory.

### Install from Source
```shell
$ pip install .
```

## Testing

### C++ Tests
Run the NFsim model tests:
```shell
$ ctest --test-dir build -C Release --output-on-failure
```
Note: `-C <config>` is required for multi-config generators to specify which build configuration to test.

### Python Tests
First, activate your virtual environment (if not already activated):
```shell
$ source .venv/bin/activate  # On Linux/macOS
$ .venv\Scripts\activate     # On Windows
```

Install the package in editable mode:
```shell
$ pip install -e .
```

Then install test dependencies:
```shell
$ pip install pytest
```

Run the smoke test:
```shell
$ pytest tests/test_pyvcell_nfsim.py -v
```

To run all tests:
```shell
$ pytest -v
```

## Clean Build Artifacts

### C++ Build
Remove build artifacts:
```shell
$ cmake --build build --target clean
```

Completely remove the build directory:
```shell
# Linux/macOS
$ rm -rf build

# Windows (cmd)
> rmdir /s /q build

# Windows (PowerShell)
> Remove-Item -Recurse -Force build
```

### Python Build
```shell
$ rm -rf dist/ build/ *.egg-info
```
