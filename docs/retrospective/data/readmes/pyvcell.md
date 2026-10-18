# pyvcell

[![Release](https://img.shields.io/github/v/release/virtualcell/pyvcell)](https://img.shields.io/github/v/release/virtualcell/pyvcell)
[![Build status](https://img.shields.io/github/actions/workflow/status/virtualcell/pyvcell/main.yml?branch=main)](https://github.com/virtualcell/pyvcell/actions/workflows/main.yml?query=branch%3Amain)
[![codecov](https://codecov.io/gh/virtualcell/pyvcell/branch/main/graph/badge.svg)](https://codecov.io/gh/virtualcell/pyvcell)
[![Commit activity](https://img.shields.io/github/commit-activity/m/virtualcell/pyvcell)](https://img.shields.io/github/commit-activity/m/virtualcell/pyvcell)
[![License](https://img.shields.io/github/license/virtualcell/pyvcell)](https://img.shields.io/github/license/virtualcell/pyvcell)

[//]: # "[![Open In Colab](https://colab.research.google.com/assets/colab-badge.svg)](https://colab.research.google.com/drive/101BPDYqu4_PupqmunT6Qhextks_VT-8X?usp=sharing)"

This is the python wrapper for vcell modeling and simulation for

1. local scripting of spatial modeling, simulation, data analysis and visualization workflows using Virtual Cell technology
2. access to Virtual Cell remote APIs - with public access or as an authenticated Virtual Cell user.

## Local simulation, analysis and visualization with pyvcell

### download or create new spatial models

- load/save VCML (Virtual Cell’s native format - import/export to VCell UI)
- load/save SBML (Systems Biology Markup Language)
- load from Antimony (friendly textual language for SBML)
- create/edit any VCML object (e.g. species, model, geometry) programmatically

### local editing with Python objects

- edit parameters. add/remove/edit species, compartments, reactions, initial conditions, diffusion coefficients
- create/edit geometry objects, create/edit vcell applications and simulations

### run local simulations

- run local spatial simulations (for reactions, diffusion, and advection) stored in local workspace.

### analyze local simulation result:

- Time-series summary statistics available as NumPy arrays.
- spatiotemporal arrays stored as Zarr datastores and available as NumPy arrays.
- 3D mesh data using VTK unstructured grids, analyzed with VTK

### local visualization

- built-in plotting and 3D visualization via Matplotlib and VTK/PyVista
- make your own plots or 3D renderings.

# installation

The easiest way to install pyvcell is by using the Python Package Index and pip.
We highly recommend setting up a virtual environment for dependency management.
Run the following command to install pyvcell from PyPI

```shell
pip install pyvcell
```

# Usage

```python
import pyvcell.vcml as vc
biomodel = vc.load_vcml_file('path/to/your/model.vcml')
results = vc.simulate(biomodel, "sim1")
results.plotter.plot_concentrations()
```

# Documentation

Full documentation is available at **https://virtualcell.github.io/pyvcell/**

# Examples:

### Antimony example

[![Open in Colab ](https://colab.research.google.com/assets/colab-badge.svg)](https://colab.research.google.com/drive/1aGttld4SKxuC7Vh-h1A7gsPIJT8u00fN?usp=sharing)

### Parameters example

[![Open in Colab ](https://colab.research.google.com/assets/colab-badge.svg)](https://colab.research.google.com/drive/1nNlMvXuZdn7Ay8la0KOKmSwi6dSurkte?usp=sharing)

### Geometry import example

[![Open in Colab ](https://colab.research.google.com/assets/colab-badge.svg)](https://colab.research.google.com/drive/14L3WaV42PEu7NvAQdil7i8mEL4WTPF9h?usp=sharing)
