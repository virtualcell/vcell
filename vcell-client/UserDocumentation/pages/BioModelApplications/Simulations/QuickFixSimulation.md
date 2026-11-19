# Solver Quick Fix

> **AI-drafted page.** An AI assistant wrote this page from VCell's source code. The VCell
> development team has not yet reviewed it, so it may be incomplete or wrong.
> ([Tracking issue](https://github.com/virtualcell/vcell/issues/2226))

Each simulation of a BioModel uses a [numerical solver](numerical_solvers.md), chosen on the
**Solver** tab of the [simulation editor](simulationEditor.md). Not every solver can solve every
application. VCell checks each simulation's solver against what its application needs:

- **spatial** or **non-spatial**, from the application's geometry;
- **deterministic**, **stochastic** or **hybrid** (a stochastic application with at least one
  species set to **Force Continuous**);
- **rule-based**, for network-free applications, and **springs** for SpringSaLaD applications.

When the solver lacks something the application needs, for example after the application's
geometry was changed from compartmental to spatial, the simulation gets an error in the
[Problems panel](../../PropertiesPanes/PP_Problems.md): "Solver ... does not support the following
required features", followed by the missing features. Its tooltip lists the solvers that would
work.

## Opening the Quick Fix

Double-click that error in the Problems panel. VCell shows the simulation in the application's
**Simulations** list and opens the **Unsupported Solver** dialog, which says that the current
solver does not support the model, and offers the solvers that do:

- When several solvers would work, each has a button; select the one you want.
- When only one would work, it is named, and already chosen.

If no solver supports everything the application needs, no dialog opens; change the application or
the simulation instead.

## The choices

- **Fix** changes this simulation's solver to the one chosen. It is available once a solver is
  chosen.
- **Fix All** appears only when more than one simulation in the BioModel has this kind of error. It
  changes every such simulation to the chosen solver, where that solver suits it; a simulation for
  which only one other solver would work gets that solver instead, and any other is left as it
  was.
- **Cancel** (or **Enter**, or closing the dialog) changes nothing.

After a fix, check the simulation's other settings on the **Solver** tab (time step, tolerances,
output options), since they depend on the solver.
