# SpringSaLaD Import and Export

The standalone SpringSaLaD program keeps a whole system - molecules, reactions, counters, geometry,
times - in a single plain text file. VCell reads and writes that format, with the extension
**.ssld**, so a model built in one can be opened in the other.

The format is a plain text file divided into named sections, each introduced by a line of asterisks.
The sections are, in order: time information, spatial information, the molecules and their sites,
the files those molecules were built from, then the reactions - decay, transition, allosteric and
binding - then the counters: molecule, state, bond, site property and cluster.

Two things about it are worth knowing before relying on a round trip:

- The **MOLECULE_FILES** section names the PDB source file each molecule was built from, if any.
  VCell looks for those beside the .ssld file and then in a **structure_files** folder next to it. A
  missing one is not fatal - the molecule is imported without it, and a note is written to the log -
  but if a model was produced by the standalone program's "Add PDB from file", keep that folder with
  it.
- The format describes a SpringSaLaD system, not a VCell BioModel. Anything VCell holds that
  SpringSaLaD has no place for - other applications over the same physiology, annotations,
  simulations - is not in the file and does not survive the round trip.

**Importing.** **File > Open > Local...** and pick the .ssld file; it is one of the formats the
model chooser accepts, beside .vcml, .sbml and .bngl. VCell builds a BioModel from it: the molecules
with their sites, states, radii, diffusion rates, colours and positions; the reactions, each already
carrying its subtype; and a [SpringSaLaD application](LangevinApp.md) with the geometry and times
the file described.

**Exporting.** With the SpringSaLaD application selected, **File > Export...** and choose
**SpringSaLaD format (.ssld)** from the format list. The export is of one application, not of the
whole BioModel, because a .ssld file describes exactly one system.

**What can stop an export.** The exporter writes the solver's own input, so a model that could not
be solved cannot be written either. In particular, an application with **no reactions at all**
cannot be exported: with nothing to classify, the model is not recognised as a Langevin model and
the writer stops. Add at least one reaction rule before exporting a model you are still building.

**When to use it.** Import is the quickest way to bring an existing SpringSaLaD model into VCell,
and it is a good deal quicker than rebuilding the site geometry by hand. Export is useful for taking
a model to the standalone program, and for looking at exactly what the solver will be given - the
file is readable, and every number in it came from somewhere in the application.
