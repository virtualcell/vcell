<p align="center" width="100%">
 <a href="https://vcell.org">
    <img width="10%" src="https://github.com/biosimulations/biosimulations/blob/dev/docs/src/assets/images/about/partners/vcell.svg">
 </a>
</p>

## The Virtual Cell Project
The Virtual Cell is a modeling and simulation framework for computational biology.  For details see http://vcell.org and http://github.com/virtualcell.

---
# vcell-ode
![CI](https://github.com/virtualcell/vcell-ode/actions/workflows/cd.yml/badge.svg)

Virtual Cell ODE [virtualcell/vcell-ode](https://github.com/virtualcell/vcell-ode) is a collection of numerical 
simulation libraries and protocols used to process ODEs in the Virtual Cell framework [virtualcell/vcell](https://github.com/virtualcell/vcell)).

## Building VCell ODE
There are two ways to build VCell ODE, but both start out the same way
### Step 1: Acquire the source-code
The source code can be found online at the GitHub repository.
`git clone https://github.com/virtualcell/vcell.git`

### Step 2: Install Dependencies 
You will need to acquire a unix-style C and C++ compiler suite in order to build VCell ODE. Traditionally, this project 
uses CLang + Mold, but other compiler suites may work. Additionally, you'll need to install the other dependencies for 
VCell ODE, which can be done in one of two ways:

#### Method 1: Conan Toolchain (Recommended)
The VCell ODE project uses the C++ dependency management system called ***conan*** to handle dependency management when 
building VCell ODE. We provide a number of conan-profiles that will provide conan the information need to automatically 
build the desired toolchain for cmake.

#### Method 2: Manual Dependency Installation
If you'd rather manually install all the dependencies build-tools to create VCell CLI, you'll need the following:
##### Dependencies
* If you want live messaging while the solver runs: `libcurl` (add `-DOPTION_TARGET_MESSAGING` to cmake call below)
##### Build Tools
* `cmake` to perform the build configuration
* `ninja` (or equivalent) to perform the actual build process

### Step 3: Invoke the Build
In a shell with conan and/or the other build tools in path, navigate to the project's root directory and 
run the following commands (tested in bash on unix and powershell on windows)

Note that if using `conan`, you'll need to define a profile. use `conan profile detect --force` to generate one automatically, 
or use one of the provided ones in `<project_root>/conan-profiles` [link](https://docs.conan.io/2/reference/config_files/profiles.html)
to further reading on the official `conan` website.
#### Powershell
```pwsh
    mdkir build # Must do if not using conan
    conan install . --output-folder build --build=missing # If building using conan's help
    cd build
    ./conanbuild.ps1 # If building using conan's help
    cmake -B . -S .. -G "Ninja" -DCMAKE_TOOLCHAIN_FILE="conan_toolchain.cmake" -DCMAKE_BUILD_TYPE=Release
    cmake --build . --config Release
```

```bash
    mdkir build # must do if not building conan
    conan install . --output-folder build --build=missing # If building using conan's help
    cd build
    ./conanbuild.sh # If building using conan's help
    cmake -B . -S .. -G "Ninja" -DCMAKE_TOOLCHAIN_FILE="conan_toolchain.cmake" -DCMAKE_BUILD_TYPE=Release
    cmake --build . --config Release
```

### Step 4: Manual user/system install
Currently, VCell ODE does not have an automated installation script. You will either need to move the resulting executables
in `<project_root>/build/bin` to an appropriate folder in path, or put said folder into path for your computer.