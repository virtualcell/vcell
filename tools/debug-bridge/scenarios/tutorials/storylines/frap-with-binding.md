# FRAP with Binding

- **Source:** `FRAPBinding_7.2.pdf` (84 pp, 2020-07-24)
- **Superseded by a 7.7 rewrite?** No.
- **Reproduced by:** `../frap-with-binding.sh`
- **Status:** physiology and the compartmental application reproduced, 0 errors. The spatial half needs a completed server run — see below.

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

## How the reaction network is built without the canvas

Three rules make it expressible in the table views, and all three are load-bearing:

1. **Species first.** An equation cannot place a species.
   `ModelProcessEquation.parseReaction` resolves each name with
   `model.getSpeciesContext(var)` across the whole model and reuses it wherever it already
   lives; an unrecognised name becomes `new SpeciesContext(species, rxnStructure)` — always
   the *reaction's* structure. This model happens to survive auto-creation (everything is
   in `Nuc`), which is exactly what makes it a bad model to generalise from.
2. **Each reaction in the compartment where its participants meet.** All six species are in
   `Nuc`, so all four reactions are too. A reaction spanning compartments belongs on the
   membrane between them — see [phgfp](phgfp.md).
3. **A catalyst is never written in the equation.** The equation grammar is only
   `reactants -> products`; catalysts neither parse nor render, and setting the Equation
   column calls `setReactionParticipants` with reactants and products alone. A catalyst is
   *implied by the kinetic law*: a rate expression naming a species that is neither
   reactant nor product makes it one. `Laser` becomes a catalyst purely by appearing in the
   two bleaching rates — verified, it shows up in the reaction's parameter table as a
   `Variable` with the model reporting no errors.

The New Reaction dialog asks for all three of a reaction's defining properties at once —
where it occurs, its name, its equation — so there is no half-built intermediate state to
step through.

## What is still not scripted

The reaction network (step 3) is built entirely by dragging on the Reaction Diagram canvas.
The Reactions **table** is the way round it, but with one wrinkle worth knowing before
starting:

**Create every species first, then the reactions.** An equation cannot place a species:
`ModelProcessEquation.parseReaction` looks a name up with `model.getSpeciesContext(var)`
across the whole model and reuses it wherever it already lives, but a name it does not
recognise becomes `new SpeciesContext(species, rxnStructure)` — always in the *reaction's*
own structure. So auto-creation is only ever correct when every participant belongs in the
compartment the reaction sits in. Here that happens to hold (all six species and all four
reactions are in `Nuc`), but relying on it is a trap the moment a model spans compartments,
as [phgfp](phgfp.md) does.

Note also that the reaction table's "(add new here, e.g. a+b→c)" placeholder lives in the
**Equation** column, not the Name column as it does for structures and species — and
`setValueAt` only accepts it when the model has **exactly one structure**. With three here
that route is closed anyway; the sequence is New Reaction → choose compartment → set the
equation, mirroring the species route in `simple-frap.sh`.

Step 6 is the wall. The spatial application takes its initial conditions from the
*steady-state concentrations the compartmental simulation produces* — copied off the
results spreadsheet and pasted in. Those numbers only exist once a simulation has actually
run on the VCell servers, so the spatial half cannot be built without spending real
compute. The script stops at the end of the compartmental application, which is complete
and valid on its own.
