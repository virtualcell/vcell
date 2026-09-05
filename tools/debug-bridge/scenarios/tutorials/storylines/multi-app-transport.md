# BioModel with Multiple Applications — RAN nuclear transport

- **Source:** `MultiAppTransport_7.2.pdf` (112 pp, 2020-05-11) + `MultiApp_Tutorial_Data.csv`
- **Superseded by a 7.7 rewrite?** Partly — image-based geometry is now covered by
  *VCell Tutorial: Image-Based Geometry 7.7* and its Quick Guide.
- **Status:** storyline extracted; only partly scriptable (see blockers).

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

## Blockers for scripting

Step 3 is **image segmentation** — painting and erasing pixels on an image canvas, and
dragging a histogram threshold. That is irreducibly a pixel gesture; there is no model-
level equivalent to address. Steps 5–7 depend on completed server-side runs.

Steps 1–2 (physiology), the `Copy As` application copies, the Specifications edits and
the simulation setup are all ordinary tables and menus and should script cleanly — with the
caveat that the species must be created before the reactions. `RanC_nuc` is in the nucleus
and `RanC_cyt`, `C_cyt` and `Ran_cyt` in the cytoplasm, with a flux across `NM` between
them, so an equation typed against a model missing those species would put every one of
them in the reaction's own compartment. See [phgfp](phgfp.md).
