# VCell Quick Start Guide

- **Source:** `VCell_Quickstart_7_Biomodel.pdf` (3 pp, 2019-12-23); an older
  `VCell_Quickstart_6.pdf` also sits in the directory.
- **Superseded by a 7.7 rewrite?** No.
- **Status:** reference guide, not a step sequence — nothing to script.

## What it is

Three dense pages of orientation and tips rather than a worked example. Worth keeping in
this corpus because it is the document that states the **table-view equivalence** the
whole scripting approach rests on:

> "There are multiple views for browsing and creating elements of a model. For example,
> species can be specified and edited in both Structure and Reaction Diagram views."

## Content map

- The four panes: Model Navigation, Database Navigation, Main Workspace, Properties.
- BioModel = Physiology + one or more Applications; Application = Geometry +
  Specifications + Protocols + Simulations.
- Starting points: empty BioModel, a public/Education/Tutorial model from the database,
  BioModels.net import, or `File > Import` (VCML, SBML).
- Working with the Physiology, Applications, Simulations and Results, each as a short
  list of tips.
- Storage and permissions: everything private by default; public/shared access is
  read-only; multiple editions per document.
- Export/import: VCML, SBML, MATLAB (compartmental math only), PDF, NRRD, movies, STL.

## Things in it that are worth checking against current behaviour

- "A BioModel must be internally consistent and **is automatically saved to the database
  whenever you want to run Simulations**" — plus the three prompts (never saved,
  inconsistent, would overwrite results).
- The claim that compartmental applications need correct surface and volume sizes, with
  the worked figure "a spherical cell with a 10 micron diameter is 523.33 µm³" — that
  number is the radius-5 sphere volume, and the same 523.33 appears as `Cyt` in the
  FRAP-with-binding tutorial.
