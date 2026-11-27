# SpringSaLaD — receptor kinase activation

- **Source:** `vcell.org/webstart/SpringSaLaD/SpringSaLaDUsersGuideAndTutorial.pdf`,
  *SpringSaLaD Version 2 User's Guide and Tutorial*, Paul Michalski and Joseph Masison,
  44 pp.
- **Superseded by a rewrite?** **No** — and that is the point of this one. Every other
  document in this corpus describes a VCell workflow that has drifted. This one describes
  a **different program**: a standalone pair of jars, `SpringSalad.jar` and
  `LangevinNoVis01.jar`, with its own "Langevin Dynamics System Setup" GUI. It tells the
  reader to install Java 8 and double-click a jar.
- **Status:** reproduced by [`springsalad-mapk.sh`](../springsalad-mapk.sh) as the VCell
  workflow that replaces it.

## Objective

Build a toy model of receptor kinase activation as a particle-based, spatially resolved,
stochastic simulation: molecules as spheres joined by stiff springs, diffusing in a cube
with a planar membrane, reacting on contact.

## The model

An extracellular ligand `L`, a transmembrane receptor kinase `RK`, and an intracellular
substrate `S` that is activated when `RK` phosphorylates it.

| molecule | sites | states |
|---|---|---|
| `Ligand` | `L` | one, no states |
| `RK` | `B` ligand-binding domain (extracellular) | `State0` (no ligand), `State1` (ligand bound) |
| | `A` transmembrane anchor | inert |
| | `K` kinase domain (intracellular) | `Off`, `On` |
| `Substrate` | `S` | `u`, `p` |

Ligand binds `B`; that binding flips `B` from state 0 to state 1; `B` in state 1
allosterically switches `K` on; `K` phosphorylates `S`. The observable of interest is the
number of phosphorylated `S`.

Initial condition: 20 `Ligand`, 10 `RK`, 20 `Substrate`, all `B` in state 0, all `K` off,
all `S` unphosphorylated. Rate constants are the document's Table 1 — k_L0,on 20 µM⁻¹s⁻¹,
k_L0,off 100 s⁻¹, k_L1,on 40 µM⁻¹s⁻¹, k_L1,off 10 s⁻¹, r0 = r1 = r_act = 1000 s⁻¹,
r_inact 10 s⁻¹, k1 10 µM⁻¹s⁻¹, k2 20 s⁻¹, k3 1000 s⁻¹, r_p 100 s⁻¹, r_u 10 s⁻¹.
The run is 0.1 s total in a 100 × 100 × (200 + 200) nm box, dt_data 1 ms, dt_image 10 ms,
six independent runs on three cores.

## Storyline (the standalone program)

1. **Modelling framework.** Molecules are "a collection of spherical sites linked by
   stiff, unbreakable springs". A *site type* carries a radius (excluded volume), a
   diffusion constant, a colour, and one or more internal states; *sites* are instances of
   a type; *links* are the springs. The worked example is protein kinase A: two regulatory
   and two catalytic sites, three states on the regulatory type, two on the catalytic.
2. **Reactions.** Zeroth order creation (µM/s). First order (s⁻¹): decay, dissociation,
   and state transition — the last restricted by **Free** (site must be unbound),
   **Bound** (site must be bound), **Allosteric** (another named site in the same molecule
   must be in a particular state), or **None** (no condition). Second order bimolecular
   binding (µM⁻¹s⁻¹).
3. **Defining molecules.** Add site types, add sites, place them in 3D, link them. A
   membrane molecule needs an inert anchor site; "Set as 2D" confines a molecule's
   diffusion to the membrane plane. Sites can also be imported from a PDB file.
4. **Defining reactions**, in three panels — binding, transition, allosteric.
5. **Data constructs.** Five counter classes — molecule, state, bond, site property,
   cluster. The first four are on by default; cluster tracking is off because it generates
   large files.
6. **System information.** Geometry: "only a single system geometry, namely, a cubic
   geometry with a single planar membrane at z=0", partitioned for collision detection,
   with the warning that **the minimum partition size must be larger than the diameter of
   the largest site**. Times: total, `dt`, `dt_spring`, `dt_data`, `dt_image`. Initial
   conditions by particle number or concentration.
7. **Saving** the system as a plain text file.
8. **Running simulations** from a Simulation Manager: N independent runs, in parallel over
   a chosen number of cores.
9. **The 3D viewer** — rotate, translate, zoom, a time slider, play at a chosen FPS, a
   run selector, image and movie export, and toggles for time stamp, axes and membrane.
10. **Data tables** — the counter classes crossed with Average, Histogram and Raw data,
    plus per-run wall-clock times.

## What VCell replaces it with

VCell has the whole of this built in: a **SpringSaLaD application** on an ordinary
rule-based physiology, the **Langevin** solver, a **Molecular Structures** editor for site
geometry, and a **3D Trajectory** viewer on the results. The concepts survive; almost
every noun moves.

| standalone SpringSaLaD | VCell |
|---|---|
| Molecules, site types, sites, links | a `MolecularType` in **Physiology → Molecules**; geometry in the application's **Molecular Structures** tab |
| radius / diffusion constant / colour | `Radius`, `Diff. Rate`, `Color` columns, per site, per species |
| site states | states on the molecular component — `B~State0~State1` |
| a site's 3D position | `Location`, `X`, `Y`, `Z` columns |
| links (springs) | the **Links** table; `Add Link` / `Delete Link` |
| membrane anchor site | a site named **`Anchor`** with the single state `Anchor`; anchoring itself is set in **Physiology**, on the molecule, not in the application |
| "Set as 2D" | the **Is 2D** column on the species |
| creation / decay | reaction subtypes `Creation` / `Decay`, using the reserved `Source` and `Sink` molecules |
| transition, Free / Bound / None | subtype `Transition` with `TransitionCondition` **Unbound** / **Bound** / **Any** |
| transition, Allosteric | its own subtype, `Allosteric` |
| bimolecular binding | subtype `Binding`, which **must be reversible** — the other four must be irreversible |
| cube with a membrane at z=0 | a 3D analytic geometry the application builds for you: exactly three structures named `Intracellular`, `Membrane`, `Extracellular`, `Intracellular` = `z < N`, `Extracellular` = `1.0` |
| partitions | `Langevin Simulation Options` on the simulation |
| total time, `dt`, `dt_data` | the simulation's time bounds and output interval |
| `dt_spring`, `dt_image` | `Langevin Simulation Options` — spring interval, image interval |
| total runs, parallel, cores | **MultiRun**: total jobs and concurrent jobs |
| Simulation Manager | the application's **Simulations** tab |
| 3D viewer | the **3D Trajectory** tab of the results window |
| Data tables | the results window's plot and spreadsheet, plus the **Langevin Clusters** tab |
| save/load the plain-text system | `.ssld` **import and export** (`File > Export…`, `File > Open`) |

### One naming trap worth reading twice

The transition condition changes name on the way in, and two of the three land on a
*different word for the same thing* rather than an obviously new one:

| the document says | VCell shows | internally |
|---|---|---|
| **None** — no condition | **Any** | `TransitionCondition.NONE`, bond type `?` |
| **Free** — site must be unbound | **Unbound** | `TransitionCondition.FREE`, bond type `-` |
| **Bound** — site must be bound | **Bound** | `TransitionCondition.BOUND`, bond type `+` |

VCell's own source says as much — *"terminology is very confusing"* — because the enum
carries three names for each value: the RBM bond type it is implemented as, the label the
table shows, and the name the `.lngv` solver input uses. A reader who picks the item whose
label matches the document will get the wrong condition twice out of three times.

## How the script is built, and why it stops where it does

**Part 1 builds the model.** Structures, molecules, the membrane anchor, species, the
SpringSaLaD application and the whole of Molecular Structures — every site's compartment,
position, radius, diffusion rate and colour, and the links between them — all through
tables, with no canvas gestures at all. It ends with a complete, error-free SpringSaLaD
model.

**Part 2 does not build the reactions**, and cannot. Reaction *rules* have no textual
route in the client: `BioModelEditorReactionTableModel.isCellEditable` allows the BioNetGen
definition column only for a `ReactionStep`, and a `ReactionRule` is not one, so the cell
is read-only; and the rule editor itself is a single anonymous custom-painted component
with zero children. This is the same wall the two rule-based tutorials hit, filed as
**issue #2068**.

The `.ssld` route — the natural analogue of the BNGL import those two fall back on — is
real, and it is documented here, but it does not help a script *build* a model: the format
is the standalone program's own save file, so writing one by hand means writing the whole
system, geometry and counters included, rather than the few rules that are missing. And
learning the format by exporting first is blocked by the bug below.

So Part 2 opens a model that already has the reactions: **`aaa-aSpringSaLaD-Good`**, a
published model kept in the repository as a test fixture, which carries **exactly one rule
of every SpringSaLaD subtype** — `creation`, `decay`, `transition_none`, `transition_free`,
`transition_bound`, `binding`, `allosteric`. The script reads each rule's subtype and
transition condition back out of the Reactions table, then runs the Langevin solver
locally and drives the 3D Trajectory viewer.

## Bugs found while writing this

- **A site's Y or Z could not be set to a value equal to its X.** All three coordinate
  setters in `MolecularTypeSpecsTableModel` guarded on `c.getX() != res`; only the X one
  was right, so Y and Z edits were silently discarded — no error, no change, the old value
  still in the cell. SpringSaLaD molecules are routinely laid out along an axis with x = 0,
  so "put this site at z = 0" never worked. **Fixed in this branch.**
- **[#2070](https://github.com/virtualcell/vcell/issues/2070)** — exporting a reaction-less
  SpringSaLaD application reports success and writes a 0-byte file. `isLangevin()` decides
  from the first `ParticleJumpProcess`, so a model with no reactions is classified
  non-Langevin; `LangevinLngvWriter` throws `"Langevin Math expected."`;
  `SpringSaLaDExporter.getDocumentAsString` swallows it in a catch-all that returns `null`;
  and the caller still reports *"Export saved as …"*.
- **[#2071](https://github.com/virtualcell/vcell/issues/2071)** — auto-generated `_tot`
  observables are stranded in the first structure. `Model.createObservable` falls back to
  `getStructure(0)` and the Structure column is not editable, so in a three-compartment
  model every auto observable lands in `Extracellular` — a permanent warning for an
  *anchored* molecule. Rebuilding one through `New > In Membrane` swaps it for a different
  warning, because `addObservable` seeds an empty species pattern that setting the
  definition does not consume.
- **[#2072](https://github.com/virtualcell/vcell/issues/2072)** — the reserved `Anchor`
  site defaults to `DARK_GRAY`, and the trajectory viewer draws on black. Every membrane
  molecule has an anchor, so this is not an unusual configuration; the script gives every
  site a light colour for that reason.
