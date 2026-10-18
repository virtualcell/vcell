<p align="center" width="100%">
 <a href="https://vcell.org">
    <img width="10%" src="https://github.com/biosimulations/biosimulations/blob/dev/docs/src/assets/images/about/partners/vcell.svg">
 </a>
</p>

---
![CI](https://github.com/virtualcell/vcell-solvers/actions/workflows/cd.yml/badge.svg)

# vcell-fvsolver
Virtual Cell Finite Volume solver [virtualcell/vcell-fvsolver](https://github.com/virtualcell/vcell-fvsolver) 
is a reaction-diffusion-advection PDE solver for computational cell biology. 
This solver is used within the Virtual Cell modeling and simulation application [virtualcell/vcell](https://github.com/virtualcell/vcell) 
and as a component in the Virtual Cell Python API [virtualcell/pyvcell](https://github.com/virtualcell/pyvcell) (coming soon).

## The Virtual Cell Project
The Virtual Cell is a modeling and simulation framework for computational biology.  For details see http://vcell.org and http://github.com/virtualcell.

## Docker container
the vcell-fvsolver is available as a docker container at ghcr.io/virtualcell/vcell-fvsolver.

## Standalone executables
FiniteVolume executable can be build on Windows, MacOS, and Linux (see .github/workflows/cd.yml for details). The executables are available in the release section of this repository.

## Python API - pyvcell_fvsolver
The Python API for the VCell Finite Volume solver is a low level wrapper which 
accepts VCell solver input files (.fvinput, .vcg) 
and generates the output files (.log, .zip, .mesh, .meshmetrics, .hdf5).  The 
.functions file is not used by the solver, but is helpful for interpreting the 
results in the context of the original model.

This package is intended to be used by the Virtual Cell Python API [virtualcell/pyvcell](https://github.com/virtualcell/pyvcell) (coming soon).
