# PH-GFP binding to PIP2 and IP3

- **Source:** `PHGFP_7.2.pdf` (101 pp, 2020-07-24)
- **Superseded by a 7.7 rewrite?** No.
- **Status:** storyline extracted; not yet scripted.

## Objective

A 3D spatial model of the PH-GFP reporter for PIP2→IP3 conversion. Introduces membrane
species, events, and output functions.

## Storyline

1. **Structures**, five, nested: `EC` | `PM` | `Cyt` | `NM` | `Nuc`.
2. **Species.** Two on `PM`: `PIP2_PM`, `PIP2_PHGFP_PM`. Four in `Cyt`: `IP3_Cyt`,
   `IP3_PHGFP_Cyt`, `PH_GFP_Cyt`, `Stim`.
3. **Reactions.**
   - `PIP2_PH`: `PIP2_PM + PH_GFP_Cyt → PIP2_PHGFP_PM`, Kf `0.12`, Kr `(Kf*KdPIP2PH)`,
     `KdPIP2PH = 2.0 uM`
   - `IP3PH`: `IP3_Cyt + PH_GFP_Cyt → IP3_PHGFP_Cyt`, Kf `10`, Kr `(Kf*KdIP3PH)`,
     `KdIP3PH = 0.1`
   - `r2`: synthesis of `IP3_Cyt`, kinetic type *General*, rate `Ksynth*Stim`,
     `Ksynth = 1.0`; `Stim` is a catalyst.
4. **Steady-state (ODE) application** named `Steady State`. Initial conditions
   `IP3_Cyt` 0.1, `PH_GFP_Cyt` 1.0, `PIP2_PM` 120000. Structure sizes `Nuc` 33.389,
   `Cyt` 489.794, `NM` 49.8, `EC` 476.817, `PM` 501.804.
5. **Events (Protocols tab).** `Activation` at t=5.0 sets `Stim` to `1.0`;
   `Inactivation` at t=6.0 sets `Stim` back to `0`.
6. **Simulation.** Integrator *IDA (Variable Order, Variable Time Step, ODE/DAE)*,
   ending time `30.0`, maximum time step `0.01`, keep every `10`. Run and view.
7. **Spatial application** named `Spatial`. Geometry: *Analytic Equations (3D)*;
   `subdomain0` → `EC`; Sphere radius `5.0` → `Cyt`; Sphere centre `3.5,3.5,3.5`
   radius `2.0` → `Nuc`. Inspect in Surface View, then map structures to subdomains.
8. **Carry the steady state across.** From the `Steady State` results spreadsheet at
   t = 5, copy the cells for `IP3_Cyt`, `IP3_PHGFP_Cyt`, `PH_GFP_Cyt`, `PIP2_PHGFP_PM`,
   `PIP2_PM`, and Paste All into the spatial initial conditions.
9. **Stim in space.** Check `Clamped` for `Stim` and set its initial condition to
   `((t>5)&&(t<6))`.
10. **Simulation.** Mesh `31` in X, ending time `20.0`, output interval `0.2`.
11. **Output function.** Add a function `Fluorescence` = `IP3_PHGFP_Cyt+PH_GFP_Cyt`
    on domain `Cyt`, so the total fluorescent signal can be plotted directly.

## Blockers for scripting

Reaction creation as in [frap-with-binding](frap-with-binding.md), but here the compartment
point is load-bearing rather than incidental. `PIP2_PM` and `PIP2_PHGFP_PM` are on the
membrane while `PH_GFP_Cyt` and `IP3_Cyt` are in the cytosol, so the reaction
`PIP2_PM + PH_GFP_Cyt → PIP2_PHGFP_PM` spans two compartments. Typing that equation into a
model that does not already have those species would create **all three in the reaction's
own structure** — silently, with no error. Every species must be created first, through
New Species → choose compartment, before any equation is typed.

Events, output functions and the 3D geometry are ordinary dialogs and tables and should
script the same way the 2D geometry already does. Step 8 needs a completed run.
