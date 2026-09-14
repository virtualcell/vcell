# FRAP — Fluorescence Redistribution After Photobleaching

- **Source:** `vcell.org/webstart/VCell_Tutorials/SimpleFRAP_7.2.pdf` (45 pp, 2020-07-24)
- **Superseded by a 7.7 rewrite?** No. This is still the canonical first tutorial.
- **Reproduced by:** `../simple-frap.sh` → `../../recordings/tutorials/simple-frap.json`
- **Status:** fully reproduced, model builds with 0 errors.

## Objective

Build the simplest possible spatial model — one diffusing species, no reactions — and
watch it re-fill a bleached square. It exists to introduce the interface, not the biology.

## Storyline

1. **Physiology — three structures.** Start from the empty BioModel. Rename the default
   compartment `c0` to `EC` (extracellular); add a membrane `m0` → `PM` (plasma
   membrane); add a compartment `c1` → `Cyt` (cytosol). Annotate each.
2. **Physiology — one species.** Create a single species inside `Cyt`, named `Dex`
   (fluorescent dextran). No reactions at all.
3. **Application.** New *Deterministic* application, renamed `FRAP`.
4. **Geometry.** Add Geometry → New → *Analytic Equations (2D)*. Rename `subdomain0` to
   `EC`; add an analytic subdomain of shape *Circle*, centre `0,0`, radius `10`, renamed
   `Cyt`. Edit Domain to size `22 × 22 µm` with origin `-11, -11`, giving a circle of
   radius 10 inside a 22 µm square.
5. **Structure mapping.** Map `EC`→`EC`, `Cyt`→`Cyt`, **and the `PM` membrane** to the
   surface between them. The mapping must read *Resolved*.
6. **Specifications.** Dex initial condition is the Boolean
   `(10.0*((x<-5.0)||(x>5.0)||(y<-5.0)||(y>5.0)))` — 10 µM everywhere except the square
   from -5..5 in x and y, which starts bleached to 0. Diffusion constant `20`.
7. **Simulation.** New simulation renamed `FRAP`; mesh `51` in X with aspect ratio
   locked; ending time `3.0`, maximum time step `0.01`, output interval `0.05`.
8. **Run and view.** Save the model, press Run, then open Results: a time slider, a
   variable selector, a line tool for spatial plots, and a point tool for time plots.

## What the script does and does not cover

Steps 1–7 are scripted and verified. Step 8 is deliberately left out: saving needs a
logged-in account and Run dispatches a real job to shared VCell compute. The model is
complete and valid where the script stops.

## Notes for anyone rewriting this tutorial

- The PDF drives the **Reaction Diagram canvas** throughout ("hover on the dotted black
  lines so they turn green"). Every one of those steps has an exact equivalent in the
  **table views**, which is what the script uses; a rewrite could show either.
- The PDF never mentions mapping the membrane, and the model reports **0 errors** with
  `PM` left `Unmapped` — but the chosen solver differs (SundialsPDE vs Fully-Implicit),
  so the tutorial as written can silently produce a different simulation than intended.
