# BioModel with Multiple Applications — RAN nuclear transport

- **Source:** `MultiAppTransport_7.2.pdf` (112 pp, 2020-05-11) + `MultiApp_Tutorial_Data.csv`
- **Superseded by a 7.7 rewrite?** Partly — image-based geometry is now covered by
  *VCell Tutorial: Image-Based Geometry 7.7* and its Quick Guide.
- **Status:** reproduced by [`multi-app-transport.sh`](../multi-app-transport.sh), 0 errors,
  with two documented substitutions (see below).

## Objective

One physiology, solved five different ways. This is the tutorial that teaches that an
Application is a *virtual experiment*, not a property of the model.

## Storyline

1. **Structures**, five: `EC` | `PM` | `Cyt` | `NM` | `Nuc`.
2. **Species and reactions.** `RanC_nuc` (nucleus), a **flux reaction** across `NM`,
   `RanC_cyt`, a reaction node, `C_cyt` (cargo) and `Ran_cyt` (Ran-GTPase) in cytoplasm.
   - Flux rate `kfl * (RanC_cyt - RanC_nuc)`, `Kfl = 2.0`
   - Reaction forward rate `1.0`, reverse rate `1000.0`
3. **Spatial deterministic application** from an **image-based geometry**: import the
   Neuroblastoma image stack, reduce resolution, crop, apply an averaging filter, then
   segment with the histogram/threshold tool into domains `Nuc` and `Cyt`; clean up with
   the paint and eraser tools; Auto-Merge stray regions; add an empty border; set the
   Z domain size to 26 µm. Map structures to subdomains.
4. **Initial conditions.** `RanC_nuc` = `4.5E-4`. Create a simulation, set mesh and
   solver, run, and view results (time point tool → `Plot > Time`).
5. **Non-spatial stochastic copy.** `Copy As > Non-Spatial > Stochastic`, rename, new
   simulation, set time bounds, run. Export the `RanC_cyt` results as CSV — this is the
   `MultiApp_Tutorial_Data.csv` used next.
6. **Non-spatial deterministic copy.** `Copy As > Non-Spatial > Deterministic`. Sizes are
   carried over automatically from the image-based geometry.
7. **Parameter estimation.** On the Parameter Estimation tab, add `Kf` as a parameter to
   fit; import the CSV under *Experimental Data Import*; map the concentration; then
   `Run Task > Solve by Copasi` and compare the estimate to the model value.
8. **Spatial stochastic copy.** `Copy As > Spatial > Stochastic`, switch Specifications →
   Species to *Number of Particles*, set counts, new simulation, mesh, solver, run.

## What the script does, and the two places it differs

Everything: physiology, the image-based geometry, all four applications, a local
stochastic run, a CSV export of it, and a Copasi fit against that CSV. Nothing is saved to
the database and nothing is sent to the VCell compute resources — Quick Run and Copasi
both run locally.

The fit is genuinely noisy, and that is the honest result rather than a defect to hide.
`Kf` has a model value of 1.0 and came back as 1.48, 0.95 and 0.72 on three runs. The
target is a *stochastic* trace of a species that never exceeds four molecules in the whole
cytoplasm — the series is quantised in steps of 4.77e-8 µM, one molecule — so there is
very little in it to fit. The tutorial's own shipped CSV has values a hundred times larger,
from a geometry with a much smaller cytoplasm. What the script demonstrates is the
workflow, and that Copasi lands in the right neighbourhood from data this thin.

The script takes the image stack as its argument:

```bash
curl -O https://vcell.org/webstart/VCell_Tutorials/7.7/NeuroblastomaStack.tif
tools/debug-bridge/scenarios/tutorials/multi-app-transport.sh ./NeuroblastomaStack.tif
```

**Substitution 1 — the flux reaction.** The PDF draws it with the FluxReaction tool, and
`Model.createFluxReaction` has exactly one interactive caller: `ReactionCartoonTool`, on
the canvas. The Reactions table can only make SimpleReactions. So the script builds a
membrane reaction with the same participants, which `MembraneStructureAnalyzer` resolves
through the same machinery — a `ResolvedFlux` per adjacent volume species, reactant
negated and product added. The script checks the generated math rather than asserting the
equivalence: the rate appears as `Cyt_Nuc_membrane::J_flux0` with the `KFlux_NM_Cyt` and
`KFlux_NM_Nuc` factors that carry it into the volumes either side, which is what a
FluxReaction produces.

**Substitution 2 — the segmentation cleanup.** Image segmentation is three different
kinds of gesture, and only one of them is genuinely out of reach:

| Gesture in the PDF | In the script |
|---|---|
| drag across the histogram | `pixelrange 180 255` — the axis IS pixel intensity, so the drag means a range |
| select all but the top regions, Auto-Merge | a position range in the regions list, which is what "all but the top three" is |
| paint and erase individual pixels | **not reproduced** — no model-level equivalent, and none invented |

Skipping the paint step has a visible consequence, and a fix that is a parameter rather
than a gesture. At the PDF's implied cytoplasm threshold the nucleus touches the outside
somewhere instead of being wrapped in cytoplasm, and VCell says so: an unmapped
`Nuc_background_membrane`. Lowering the threshold from 20 to 10 takes in enough of the
dim cytoplasm to enclose the nucleus, and the model then has no warnings at all. That is
the same defect the eraser fixes by hand.

Two numbers the PDF leaves to a screenshot are chosen here and marked as choices: the
image is reduced to 128×128×34 rather than dragged on a slider, and the mesh is 41 rather
than the 79 the image implies — the PDF says "the larger mesh elements to save on your
simulation time" without naming them.

Steps 1–2 (physiology), the `Copy As` application copies, the Specifications edits and
the simulation setup are all ordinary tables and menus and script cleanly — with the
caveat that the species must be created before the reactions. `RanC_nuc` is in the nucleus
and `RanC_cyt`, `C_cyt` and `Ran_cyt` in the cytoplasm, with a flux across `NM` between
them, so an equation typed against a model missing those species would put every one of
them in the reaction's own compartment. See [phgfp](phgfp.md).

The flux itself belongs on `NM`, by the same rule: it joins `RanC_cyt` to `RanC_nuc` across
the nuclear membrane, and a localized reaction spanning two compartments sits on the N−1
interface between them. The cytoplasmic reaction, whose participants are all in `Cyt`,
simply goes there.
