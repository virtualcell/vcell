# VCell Quick Start Guide

- **Source:** `VCell_Quickstart_7_Biomodel.pdf` (3 pp, 2019-12-23); an older
  `VCell_Quickstart_6.pdf` also sits in the directory.
- **Superseded by a 7.7 rewrite?** No.
- **Status:** audited by [`quickstart.sh`](../quickstart.sh) — there is no model in it to
  build, so the script checks what the document *asserts* against the current client.
  **5 claims still hold, 3 have gone stale.**

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

## The audit

A reference guide cannot be reproduced by following it — there are no steps. What it has
instead is a couple of dozen assertions about how VCell behaves, and those go stale
silently. `quickstart.sh` checks the ones that are checkable.

### Gone stale

| Claim | What the client does now |
|---|---|
| "VCell supports **VCML and SBML** files" (import) | eight formats: `.xml .vcml .sbml .vfrap .bngl .omex .sedml .ssld`. `.bngl` in particular is the only route to a rule-based model that does not go through the graphics editor |
| "a spherical cell with a 10 micron diameter is **523.33** micrometers cubed" | (4/3)·π·5³ = **523.5988**. Low by about 0.05%, and this is the one claim checkable without VCell at all |
| "diffusion constants… **default to zero** for each molecular species" | a new volume species in a spatial application defaults to **10.0 µm²·s⁻¹** (0.1 on a membrane). The tip goes on to warn that zero "is always illegal when a molecule is involved in a membrane flux", so a reader who trusts it goes looking for a problem that is not there |

### Still true

Four panes; the **Fast** checkbox on the reaction specifications; MatLab and PDF among the
document export formats; and Quick Run, which runs without saving to the database — the
feature every other script in this directory depends on.

### Two traps this audit had to survive

Both would have produced a confident, wrong "stale":

- **The export list is content-dependent.** An empty BioModel is offered five formats; one
  with content, ten. Read it from the model VCell starts with and half the guide looks
  wrong.
- **The MatLab claim is conditional** — the guide says math export "from *compartmental*
  Applications", and Matlab is duly absent while the model has only a spatial one. The
  audit builds one of each before looking.

Not checked, deliberately: **STL, AVS and GIF**. The guide attributes those to the geometry
surface viewer and the physiology cartoon, which are separate panels with their own export
actions. Looking for them in the document export list would be checking the wrong menu.

## Also worth keeping

This is the document that states the **table-view equivalence** the whole scripting
approach rests on, and the claim that a BioModel "is automatically saved to the database
whenever you want to run Simulations" — which Quick Run is the exception to.
