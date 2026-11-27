# Overview

VCell provides two styles of developing computational models:

- **BioModels**
- **MathModels**

Each uses a different method to create models to best suit a given user's experience.

**BioModel**

A BioModel is a computational model built by defining the biological components of the model within
a graphical interface. BioModels consist of:

(1) A [Physiology](../Physiology/PhysOverview.md); the cellular structures, species and biochemical
reactions that are components of the model.

(2) [Applications](../BioModelApplications/ApplicationOverview.md); the application of the model to
a specific instance of physiology,in which the initial state and any additional conditions or
constraints on the physiology are defined.

Each [Application](../BioModelApplications/ApplicationOverview.md) allows the user to define the
quantitative values needed to define, run and visualize a Virtual Experiment (i.e. numerical
simulation). Thus, an **Application** includes:

- [Geometry](../BioModelApplications/Geometry/GeometryOverview.md)
- [Specifications](../BioModelApplications/Specifications/SpecificationsOverview.md) (e.g. species,
  reactions)
- [Protocols](../BioModelApplications/Protocols/ProtocolsOverview.md) (e.g. events, electrical
  mapping)
- [Simulations](../BioModelApplications/Simulations/simulations_overview.md), of which there can be
  one or more.
- [Parameter Estimation](../BioModelApplications/ParameterEstimation/PEOverview.md) (if applicable)
  for defining parameters based on experimental data

**MathModel**

MathModels create individual Applications (Virtual Experiments) using the **Virtual Cell Markup
Language** (**VCML**). VCell automatically creates a unique MathModel for each Application within a
BioModel; such MathModels can be created ad hoc or edited using VCML. Note that a MathModel does not
have an associated Physiology. A MathModel consists of a series of definitions for:

- Constants
- Variables
- Functions
- Volume domains
- Membrane domains

See [Generated Math](../BioModelApplications/Simulations/generated_math.md) for more information on
the components of the math description and how to **create** a stand-alone MathModel from an
existing BioModel Application that can then be further edited.
