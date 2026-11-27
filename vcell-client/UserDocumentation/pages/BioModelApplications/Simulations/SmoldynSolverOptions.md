# Smoldyn Solver Options

> **AI-drafted page.** An AI assistant wrote this page from VCell's source code. The VCell
> development team has not yet reviewed it, so it may be incomplete or wrong.
> ([Tracking issue](https://github.com/virtualcell/vcell/issues/2226))

Spatial stochastic simulations, and spatial hybrid simulations that mix particles with
deterministic species, are solved by Smoldyn (see [Numerical Solvers](numerical_solvers.md) for
what it does). In the
[simulation editor](simulationEditor.md), the **Solver** tab of such a simulation has an **Advanced
Solver Options** section for Smoldyn's own settings. It is collapsed at first; click its title to
open it. The section does not appear for other kinds of simulation.

Each value is taken when you leave its field. The **?** buttons next to the fields show a short
description of each setting.

## accuracy

A number from 0 to 10 that trades accuracy for speed. The default, 10, is the most accurate. Lower
values run faster but are less accurate. Smoldyn divides space into virtual boxes and only checks
pairs of molecules that are close enough to react:

| accuracy | Pairs of reactants checked for bimolecular reactions |
| --- | --- |
| 0 to 2.99 | only pairs in the same virtual box |
| 3 to 6.99 | also pairs in nearest neighboring boxes |
| 7 to 10 | pairs in all types of neighboring boxes |

## gauss table size

The size of the lookup table Smoldyn uses to generate random numbers with a Gaussian (normal)
distribution. It must be a power of 2; VCell refuses any other value. The default, 4096, is
appropriate for nearly all models.

## sequence seed

The seed for Smoldyn's random number generator. When the box is unchecked (the default), the
current time is used as the seed, so every run gives a different result. Check **sequence seed** and
enter a whole number to make a run reproducible: the same model with the same seed produces the same
trajectory. If the simulation has a [parameter scan](simulationEditor.md), each run of the scan uses
the seed plus its own run number, so the runs of a scan still differ from each other.

## fast mesh sampling

Checked by default. Before the simulation starts, VCell samples the geometry at high resolution so
that Smoldyn can quickly tell which compartment a point in space belongs to. On rare geometries this
sampling fails and the simulation stops with an error that mentions fast mesh sampling. Unchecking
**fast mesh sampling** works around that, at the cost of a simulation that may take much longer to
run.

## save particle files

Unchecked by default. When checked, Smoldyn also writes the list of every molecule's position to a
file at each output time, in addition to the results VCell normally saves. Leave it unchecked unless
you need the individual particle positions, since these files can be large.

## step multiplier

A whole number, 1 or more; the default is 1. It matters only in a spatial hybrid simulation, where
the particles (Smoldyn) and the deterministic species (the PDE solver) are solved together. Smoldyn
then takes one step for every *step multiplier* steps of the PDE solver, so its time step is the
simulation's time step multiplied by this number. Higher values run faster but are less accurate.
VCell shows an error if you enter a value below 1.

## Related pages

- [Numerical Solvers](numerical_solvers.md)
- [Simulations Editor](simulationEditor.md)
- [Simulations](simulations.md)
