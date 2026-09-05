# FRAP with Binding

- **Source:** `FRAPBinding_7.2.pdf` (84 pp, 2020-07-24)
- **Superseded by a 7.7 rewrite?** No.
- **Status:** storyline extracted; not yet scripted.

## Objective

Extend the simple FRAP model with binding, use a compartmental (ODE) application to find
steady state, then carry those steady-state values into a spatial application where a
timed laser bleaches a square region.

## Storyline

1. **Physiology — structures.** `c0` → `Cyt` (cytosol), membrane `m0` → `NM` (nuclear
   membrane), compartment → `Nuc` (nucleus).
2. **Physiology — six species in `Nuc`:** `r` (RAN), `rf` (RAN_FITC), `rB` (RAN_Bound),
   `BS` (binding sites), `rfB` (RAN_FITC_Bound), `Laser` (light source).
3. **Reactions.**
   - `r0` "RAN binding": `BS + r → rB`, Kf `.02`, Kr `.1`
   - `r1` "RAN_FITC binding": `BS + rf → rfB`, Kf `.02`, Kr `.1`
   - `r2` "bleaching 1": `rf → r`, kinetic type *General*, rate
     `(Vmax*rf*Laser*((t>1.0)&&(t<1.5)))`, `Vmax = 50`
   - `r3` "bleaching 2": `rfB → rB`, *General*, rate
     `(Vmax2*rfB*Laser*((t>1.0)&&(t<1.5)))`, `Vmax2 = 50`
   - `Laser` is a catalyst on both bleaching reactions.
4. **Compartmental application** named `Compartmental`. Structure sizes: `Cyt` 523.33,
   `Nuc` 26.1665, `NM` 130.8325. Initial conditions `r` 5.0, `rf` 5.0, `BS` 20.0.
   Simulation to ending time `30.0`; run and view.
5. **Spatial application.** Copy `Compartmental`, rename `Spatial`. Add Geometry → New →
   *Analytic Equations (2D)*; `subdomain0` → `Cyt`; add Circle centre `0,0` radius `10`
   → `Nuc`; Edit Domain size `22`, origin `-11`. Map structures to subdomains.
6. **Carry the steady state across.** Open the compartmental results as a spreadsheet,
   select the final concentrations for `BS`, `rB`, `rf`, `rfB`, Copy Cells, then Paste
   All into the spatial application's Initial Condition column.
7. **The laser region.** `Laser` initial condition
   `((x>-2.0)&&(x<2.0)&&(y>-2.0)&&(y<2.0))` — bleaching happens only in that square.
   Enable all reactions on the Reaction specification tab.
8. **Simulation.** Mesh `51` in X, ending time `50.0`, output interval `0.5`. Run, then
   use the line tool for spatial plots and the time-point tool for time plots.

## Blockers for scripting

The reaction network (step 3) is built entirely by dragging on the Reaction Diagram canvas.
The Reactions **table** is the way round it, but with one wrinkle worth knowing before
starting:

`BioModelEditorReactionTableModel.setValueAt` only accepts an equation typed into the
"(add new here, e.g. a+b->c)" row when the model has **exactly one structure** — the same
restriction the species table has. This model has three (`Cyt`, `NM`, `Nuc`), so that route
is closed. Editing the **Equation column of an existing reaction** carries no such
restriction and does the useful part: it parses the equation and *creates any species it
names that do not exist yet*. So the sequence is New Reaction → choose compartment → set
the equation, exactly as the species route works in `simple-frap.sh`.

Not yet verified end to end. Step 6 additionally depends on a completed server-side run,
so the spatial half cannot be reproduced without spending real compute.
