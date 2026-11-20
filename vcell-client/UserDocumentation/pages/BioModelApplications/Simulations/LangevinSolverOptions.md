# SpringSaLaD (Langevin) Solver Options

> **AI-drafted page.** An AI assistant wrote this page from VCell's source code. The VCell
> development team has not yet reviewed it, so it may be incomplete or wrong.
> ([Tracking issue](https://github.com/virtualcell/vcell/issues/2226))

A simulation in a [SpringSaLaD application](../../RuleBasedChapter/LangevinApp.md) is solved by the
Langevin solver. In the [simulation editor](simulationEditor.md), the **Solver** tab shows the
usual time settings (ending time, time step and output interval) and, below them, a **Langevin
Options Panel** with the settings that only this solver uses. The panel appears only when the
selected solver is the Langevin solver.

## Single or multiple simulations

The Langevin solver is stochastic: every run follows a different trajectory. You choose how many
runs one simulation makes.

- **Single Simulation** - one run. This is the default for a new simulation.
- **Multiple Simulations** - a batch of independent runs of the same model, whose results are
  combined. When you choose it, the **Total Num. of Jobs** field becomes active and is filled with
  20.

**Total Num. of Jobs** is the number of runs in the batch. It must be at least 2 and at most 200.
A value below 2, or text that is not a whole number, is replaced by 20; a value above 200 is
replaced by 200. The new value is taken when you press Enter or leave the field.

Under the field, a line such as "Running 20 concurrent simulations." says how many of the runs
VCell will run at the same time on its cluster. That number is the smaller of the number of jobs
and the cluster's limit (normally 50 at once). If you ask for more jobs than can run at once, the
rest wait in a queue and start as earlier runs finish; the line's tooltip says so.

## Partitions

**Partitions number** with its **X-axis**, **Y-axis** and **Z-axis** fields divides the simulation
box into a grid of smaller boxes along each axis, so that the solver only has to look for
collisions between particles in neighboring boxes. The default is 10 along each axis. Each entry
must be a whole number. See [SpringSaLaD Application](../../RuleBasedChapter/LangevinApp.md) for how
to choose the number: a partition must be larger than the diameter of the largest site.

## Spring Interval and Image Interval

Both are times in seconds.

- **Spring Interval** - the time step used for the springs (the links between the sites of a
  molecule). The default is 1.0E-9 s. It is normally left at its default.
- **Image Interval** - how often a full snapshot of every particle's position is saved. These
  snapshots are what the 3D trajectory viewer plays back; see
  [SpringSaLaD Results](../../RuleBasedChapter/SSResults.md). The default is 1.0E-4 s. It is
  separate from the output interval, which controls how often the numerical results are saved.

If one of these fields, or a partition field, holds something that is not a number, VCell shows an
error and keeps the previous settings.

## Random seed

The solver draws random numbers for diffusion and reactions. Normally it starts from a different
seed for every run, so each run gives a different result.

Check **Set a seed to Langevin random number generator.** to give it a fixed seed instead. The field
next to the box becomes editable and is filled with a default seed, which you can replace with any
whole number. With the same seed and the same model, a run can be reproduced exactly, which is
useful when you want to compare a model before and after a change. Uncheck the box to go back to a
different seed for each run. The **?** button next to the field explains the same thing.

## Related pages

- [SpringSaLaD Application](../../RuleBasedChapter/LangevinApp.md)
- [Introduction to SpringSaLaD Modeling](../../RuleBasedChapter/SpringSaLaDTheory.md)
- [Simulations Editor](simulationEditor.md)
- [Numerical Solvers](numerical_solvers.md)
