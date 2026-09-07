# Reproducing the vcell.org tutorials as scripts

The tutorial PDFs at `vcell.org/webstart/VCell_Tutorials/` are mostly screenshot decks
from 2016–2022, shot against VCell 6.1–7.2. Six of them were refreshed in July 2025 (the
`7.7/` subdirectory); the rest still describe a client that has moved on.

This directory holds two things per tutorial: a **storyline** — what the document actually
teaches, in prose, extracted from the PDF — and a **script** that reproduces it against a
current client through the [debug bridge](../../README.md).

"Reproduces" means two different things here, because the documents do. Eight of them are
step sequences, and their scripts rebuild the model and run it. The other two are reference
guides with no model in them; their scripts **audit** the documents instead, checking each
claim they make against the client and reporting which ones still hold.

| Document | Storyline | Script | State |
|---|---|---|---|
| `SimpleFRAP_7.2.pdf` | [simple-frap](storylines/simple-frap.md) | [`simple-frap.sh`](simple-frap.sh) | **reproduced**, 0 errors |
| `MovingBoundaries.pdf` | [moving-boundary](storylines/moving-boundary.md) | [`moving-boundary.sh`](moving-boundary.sh) | **reproduced**, 0 errors |
| `FRAPBinding_7.2.pdf` | [frap-with-binding](storylines/frap-with-binding.md) | [`frap-with-binding.sh`](frap-with-binding.sh) | **reproduced** in full, 0 errors |
| `PHGFP_7.2.pdf` | [phgfp](storylines/phgfp.md) | [`phgfp.sh`](phgfp.sh) | **reproduced** in full, 0 errors |
| `MultiAppTransport_7.2.pdf` | [multi-app-transport](storylines/multi-app-transport.md) | [`multi-app-transport.sh`](multi-app-transport.sh) | **reproduced**, 0 errors, two documented substitutions |
| `Tutorial06_PathwayCommons_6.0.pdf` | [pathway-commons](storylines/pathway-commons.md) | [`pathway-commons.sh`](pathway-commons.sh) | **reproduced**, 0 errors — both third-party services verified live |
| `VCell_Quickstart_7_Biomodel.pdf` | [quickstart](storylines/quickstart.md) | [`quickstart.sh`](quickstart.sh) | **audited** — no model to build, so its claims are checked instead: 5 hold, 3 stale |
| `VCell6.1_Rule-Based_Tutorial.pdf` + `SingleCompartmentRuleBased.pdf` | [rule-based-egfr](storylines/rule-based-egfr.md) | [`rule-based-egfr.sh`](rule-based-egfr.sh) | **reproduced** against the 7.7 rewrite; matches the public reference model |
| `VCell6.1_Rule-Based_Ran_Transport_Tutorial.pdf` | [rule-based-ran-transport](storylines/rule-based-ran-transport.md) | [`rule-based-ran-transport.sh`](rule-based-ran-transport.sh) | **reproduced** against the 7.7 rewrite; matches the public reference model |
| `SpatialRuleBasedGuide.pdf` | [spatial-rule-based](storylines/spatial-rule-based.md) | [`spatial-rule-based.sh`](spatial-rule-based.sh) | **audited** — 5 claims hold, 3 stale; still the gap in the current doc set |

## Running one

```bash
mvn compile -pl vcell-client -am -DskipTests
tools/debug-bridge/launch-client.sh
tools/debug-bridge/scenarios/tutorials/simple-frap.sh      # or moving-boundary.sh

# Multi-app needs the image stack the PDF tells you to download:
curl -O https://vcell.org/webstart/VCell_Tutorials/7.7/NeuroblastomaStack.tif
tools/debug-bridge/scenarios/tutorials/multi-app-transport.sh ./NeuroblastomaStack.tif

# Pathway Commons needs a network, and two services outside VCell:
tools/debug-bridge/scenarios/tutorials/pathway-commons.sh

# the two rule-based ones follow the 7.7 rewrites, and import BNGL:
tools/debug-bridge/scenarios/tutorials/rule-based-egfr.sh
tools/debug-bridge/scenarios/tutorials/rule-based-ran-transport.sh

# the two reference guides have no model to build - these audit their claims instead:
tools/debug-bridge/scenarios/tutorials/quickstart.sh
tools/debug-bridge/scenarios/tutorials/spatial-rule-based.sh
```

Each takes a couple of minutes, leaves a complete valid model on screen — and then **runs
it**, using "Native Quick Run". That executes with the bundled local solvers and saves
nothing to the database, so a scripted tutorial produces real results without an account
and without putting anything on the server. The local install carries every solver these
tutorials need: `SundialsSolverStandalone` (ODE), `FiniteVolume` (PDE), `MovingBoundary`,
and the stochastic ones.

Reading results back out is what makes the multi-stage tutorials reachable. FRAP with
binding takes the steady state of its compartmental run as the initial conditions of its
spatial one, and the script gets those numbers the way the PDF's reader does — off the
results table, at the end of the time course:

    BS 12.807787   rB 3.5961066   rf 1.4038934   rfB 3.5961066

Worth checking rather than trusting: `rB` equals `rfB` because RAN and RAN-FITC start at
5.0 each and compete symmetrically for the same sites; `rf + rfB` is exactly 5.0 and
`BS + rB + rfB` exactly 20.0. Both conservation laws hold.

Multi-app goes further and closes the loop: it exports its stochastic run as CSV and fits
a rate constant back against it with Copasi, locally. `Kf` has a model value of 1.0 and
came back as 1.48, 0.95 and 0.72 on three runs — noisy on purpose rather than by accident,
because the target is a stochastic trace of a species that never exceeds four molecules in
the whole cytoplasm. There is very little in such a series to fit; that Copasi lands in
the right neighbourhood from data this thin is the point.

PH-GFP does the same thing with a subtlety: the value it needs is **not** the end of its
run. Its compartmental application fires an event at t = 5 s, so the last row is a
stimulated state, not a resting one. The tutorial reads the row at t = 5 instead — the
instant before the stimulus — and so does the script:

    IP3_Cyt 0.0910694   IP3_PHGFP_Cyt 0.00893061   PH_GFP_Cyt 0.00966635
    PIP2_PHGFP_PM 576.87   PIP2_PM 119423.13

A variable-time-step integrator picks its own output times, so there is no row number to
hard-code. `result_row_at_time` binary-searches the monotonic time column instead, which
takes about nine reads rather than hundreds and cannot land on the wrong instant.

Nothing in these scripts touches the real mouse or keyboard — every step goes through the
model or is dispatched as an AWT event on the EDT. `glide` and `rbclick` are the only
verbs that move the real pointer, and they exist for filming a replay.

That was not true when this started: `rrow`, `rclick`, `drow` and the non-button branch of
`click` all drove `java.awt.Robot` at absolute screen coordinates, so a scripted run
fought whoever was at the keyboard and broke if the window moved. They now dispatch the
events instead, which also makes them work when the client cannot be activated at all.

On macOS, launch with `VCELL_UI_BACKGROUND=true` to run the client as an accessory app:
no Dock icon, and it never becomes the active application, so it cannot pull you to its
Space when a modal dialog opens. Verified by sampling the frontmost process through a
full run — it stayed on the user's own applications throughout. It does **not** hide the
window; drag it to another Space once, or move it with `bridge.sh wbounds`. Note that
`/iconify` lies in this mode: with no Dock icon there is nowhere to minimise to, so the
frame reports `ICONIFIED` while the window stays on screen.

## The canvas problem, and the way round it

Every one of these tutorials builds its physiology by **drawing on the Reaction Diagram**:
"select the compartment tool, hover on the dotted black lines so they turn green". Those
are pixel gestures on a custom-painted canvas, and the recorder
[deliberately never stores coordinates](../../../../vcell-client/src/main/java/org/vcell/client/debug/UiRecorder.java)
— a recorded pixel breaks on the next relayout and documents nothing.

The way round it is that the diagram is not the only route. The Quick Start guide says so
outright:

> "There are multiple views for browsing and creating elements of a model. For example,
> species can be specified and edited in both Structure and Reaction Diagram views."

So the scripts drive the **table views**, which state the same model as addressable values:
`StructuresTable`, `SpeciesTable`, `StructureMappingTable`, `spceciesContextSpecsTable`.
"Type 20 in the Diffusion Constant column" is a sentence the script can say literally.

What has no table equivalent, and so is genuinely out of reach:

- **Painting and erasing individual pixels** on an image (`MultiAppTransport`). The rest of
  segmentation turned out NOT to be like that, and the distinction is the useful part:

  | Gesture | Reachable? |
  |---|---|
  | drag across the histogram | **yes** — the panel's axis IS pixel intensity, so the drag means "these values". `pixelrange 180 255` says it exactly. |
  | select all but the top regions, Auto-Merge | **yes** — the regions list is ordered by size, and "all but the top three" is a position range. |
  | paint / erase individual pixels | **no** — genuinely per-pixel, and no equivalent is invented for it. |

  Skipping the paint step has a consequence worth knowing, and a fix that is a parameter
  rather than a gesture: at the tutorial's implied cytoplasm threshold the nucleus touches
  the outside somewhere rather than being wrapped in cytoplasm, and VCell says so, as an
  unmapped `Nuc_background_membrane`. Lowering the threshold takes in enough dim cytoplasm
  to enclose the nucleus, and the model then has no warnings at all - which is what the
  eraser is for in the PDF.
- **Building a reaction RULE** (`rule-based-egfr`, `rule-based-ran-transport`). This is the
  one place in the corpus where the canvas has no table behind it, and it fails twice over.
  `BioModelEditorReactionTableModel.isCellEditable` allows the BNGL column only when the row
  is a `ReactionStep`, and a `ReactionRule` is not one — so the cell is read-only. And the
  rule editor has nothing to address: its whole pattern area is a single anonymous
  component, `ReactionRuleEditorPropertiesPanel$1`, 777×200 px with **zero children**, onto
  which every molecule, site, state and bond is painted. Molecules, Species and Observables
  *do* have the BNGL-column route — the tutorial names it itself — so only rules are out of
  reach. Both scripts take the other documented path and import the model as BNGL.
- **Drawing on the Pathway Diagram** (`PathwayCommons`) — "click a corner of the diagram,
  drag your cursor over all entities and release". Avoidable, and the PDF itself says how,
  two pages later: *"Click Pathway Objects to organize the entities into list form"*, and
  from that list the same `Physiology Links > Import into Physiology…` menu.
- **Drawing a flux reaction.** `Model.createFluxReaction` has exactly one interactive
  caller, `ReactionCartoonTool`; the Reactions table can only make SimpleReactions. A
  membrane reaction with the same participants is the way round it, and it resolves
  through the same `MembraneStructureAnalyzer` machinery - which the multi-app script
  checks in the generated math rather than asserting.
- **Reaction-diagram drawing** where a reaction's *topology* is the thing being taught.
  The Reactions table is the way round it: New Reaction → choose compartment → type the
  equation into the **Equation** column, which is also where that table's
  "(add new here, e.g. a+b→c)" placeholder lives — not the Name column, as it does for
  structures and species. (That placeholder row is only accepted when the model has exactly
  one structure, so it is unavailable in every model here anyway.)

  **Create the species before the reactions.** An equation cannot place a species:
  `parseReaction` reuses a name that already exists anywhere in the model, but creates an
  unknown one in the *reaction's* own structure. For a reaction spanning compartments —
  `PIP2_PM + PH_GFP_Cyt → PIP2_PHGFP_PM` — leaning on auto-creation puts every participant
  in one compartment, silently and with no error.

  **A catalyst is never written in the equation.** The grammar is only
  `reactants -> products`; catalysts neither parse nor render, and setting the Equation
  column calls `setReactionParticipants` with reactants and products alone. A catalyst is
  *implied by the kinetic law* — a rate expression naming a species that is neither
  reactant nor product makes it one. In FRAPBinding `Laser` becomes a catalyst purely by
  appearing in the bleaching rates.

  **And put the reaction where the compartments meet.** A localized reaction spanning more
  than one compartment belongs on the interface between them — the N−1 dimensional
  compartment, i.e. the membrane. `PIP2_PM + PH_GFP_Cyt → PIP2_PHGFP_PM` joins a membrane
  species to a volume species, so the reaction goes on `PM`, not in `Cyt`. A reaction whose
  participants all share one compartment simply goes there. This is a modelling rule, not
  something the UI enforces, so a script has to choose correctly rather than pick whatever
  validates.

## What building these scripts changed in the tooling

`simple-frap.sh` did not work against the bridge as it stood. Each of these was a silent
failure — the script reported success and the model was wrong:

- **The recorder never stored which table column was clicked**, and replay could not send
  one. Every tutorial sets values in named columns (Initial Condition, Diffusion Constant,
  Size), so every recording edited the wrong cell. Now captured as `column` +
  `columnName`, and resolved by header on replay.
- **`setText` did not commit.** Much of VCell's older GUI reads a field from a
  `focusLost` handler, not on Enter — `TimeBoundsPanel` is the clearest case. `setText`
  moves no focus, so mesh size, ending time and maximum time step were displayed and then
  discarded; the dialog reopened showing the old numbers.
- **…and committing the field is still not enough for Edit Simulation.** That dialog
  clones the simulation, edits the clone, and replaces the original in the document only
  on OK — so a `focusLost` writes into the clone and closing the dialog is what writes the
  clone back. Leaving it open discards the lot, silently. `_common.sh`'s `dialog_button`
  clicks the dialog's own OK and then waits for the dialog to actually go away, rather
  than a bare `click text=OK` that resolves against every showing window.
- **`text=` selectors resolved in `/find` but nowhere else.** Popup items are the
  components that need them — VCell builds them on the fly with no `setName`, so
  "In Compartment Cyt" was addressable only as `1/0/1/0/0/2`.
- **Row text was the raw model object.** `findRow` and the recorder stored
  `Feature@4d973a55(name=EC)` — an identity hash that differs every launch — where they
  meant to store what the row displays. Replay silently fell back to positional indices.
- **Two different tables were both named `ScrollPaneTable`**, so a replay resolved the
  geometry subdomain table when it wanted the simulation list.
- **Tabs replayed by index**, though the application tab strip differs between spatial and
  non-spatial applications, so an index names a different tab in each.
- **`bridge.sh findrow` word-split its query**, so `findrow "Analytic Equations (2D)"`
  searched for `Analytic` and quietly selected the 1D row.
- **`doClick()` on a menu item never closed the menu.** A real click dismisses the pop-up
  on its way to firing the action; `doClick` only fires the action. The stale
  "New Application" submenu then shadowed the next `text=` lookup, and the failure
  surfaced several steps later, nowhere near its cause.
- **`trow` is `selectTableRow`.** Calling it on a JTree returns `{"selected": false}` and
  changes nothing — the tree verb is `row`. Two steps in the first version of this script
  were silent no-ops that only worked because the Geometry tab happened to already be
  selected. `_common.sh`'s `must` now stops the run when the bridge reports a step did
  not take, which is how this was found.
- **`click` used `doClick()`**, which invokes only the *action* listeners. The Kinematics
  "New" button builds and shows its pop-up from `MouseAdapter.mousePressed` and so opened
  nothing, while reporting success. Buttons now get a real press/release.
- **`findRow` only ever matched column 0.** A spatial process's parameter table leads with
  a prose description ("surface velocity (x coord)") and carries the name the tutorial says
  — `velocityX` — in the next column. `findrow … --in Parameter` searches a named column.
- **`/tree` truncates a table to 25 rows**, which is right for reading a UI and useless for
  reading a RESULT: a steady state is the last row of a series hundreds long. Hence the
  `readCell` verb — a row index (negative counts back, so -1 is the last) and a column by
  header. The row count was always reported un-truncated, so the end is always findable.
- **The results data table is not "showing".** The results window opens on the plot, with
  the spreadsheet as a hidden card that still holds the data, so the usual "prefer what is
  showing" tie-break has nothing to work with — and two `PlotDataTable`s exist, the
  document window having its own, empty one. `result_cell` resolves it by path within the
  results window instead.
- **Copying an application copies its simulations**, and they keep the source's solver.
  A compartmental simulation copied into what becomes a spatial application still carries
  Combined IDA/CVODE, which cannot run one.
- **Adding a geometry leaves the generated math stale**, and VCell then refuses to open
  the Edit Simulation dialog at all — "Application geometry does not match Simulation
  geometry, Update Math before editing". `RefreshMathButton` on the Generated Math tab is
  the fix, and the same click also settles the solver: a simulation created after a
  refresh gets Fully-Implicit rather than inheriting the ODE one.
- **A panel's columns exist before its rows do.** Selecting a spatial process yields a
  parameter table with its four headers immediately and its velocity rows a moment later,
  so `col` succeeded while `row` still saw nothing and returned -1. `row`/`col` now retry
  for 10s instead of trusting a fixed `sleep`.
- **`SpatialProcessPropertyPanel` called itself `"SpatialObjectPropertyPanel"`** — a
  copy-paste slip that gave two different panels the same name.
- **`ctrl+A` had no equivalent for a table.** Several tutorials select a whole page of one
  and act on it — "press ctrl+a and click Import > Selected Only". The keystroke is a
  statement about the rows the table currently shows, so `trows <selector> 0-` says the
  same thing, over the whole table at once. That also disposes of the PDF's "if a pathway
  extends to multiple pages, click the right arrow icon and repeat".
- **A pop-up could only be driven one level at a time, and that was not reliable.** A
  heavyweight pop-up window left from an earlier pick can stop the next submenu opening at
  all, so `Copy As > Spatial > Stochastic` failed at the first level with the pop-up
  already gone - reported as "menu item 'Spatial' never took", two levels from the cause.
  It was also unnecessary: a `JMenu`'s items exist in its model whether or not anything is
  on screen, so `popupitem "A>B>C"` walks the model and clicks only the leaf. (`JMenu` also
  needed its own branch in `click`: it is a JMenuItem, but it is a door, not an action -
  clearing the selected path and calling `doClick` closes the pop-up and opens nothing.)
- **A substring match on the model tree picked the wrong application.** The tree sorts
  applications, so once one is called `Non-Spatial Deterministic`, `findrow` for
  `Spatial Deterministic` returns it - and every later step acts on the wrong application,
  surfacing much later as a `Copy As` menu that has lost its `Spatial` branch. `navrow` and
  `tree_pick` now pass `--exact` through.
- **Child nodes are ambiguous with two applications expanded.** Every application has a
  `Geometry`, a `Specifications` and a `Simulations` node, so `navselect Simulations` is a
  coin toss between them. Collapse the others.
- **A file dialog cannot be answered the way other dialogs are.** What looks like a place
  to type a path is, on Aqua, the hidden "go to folder" field: `setText` on it reports
  success and changes nothing. `choosefile` goes through the chooser's own model -
  `setSelectedFile` plus `approveSelection`, which is what the approve button does.
- **`setCell` could not tick a checkbox.** The value arrives over HTTP as text, but a
  checkbox column's model casts what it is handed straight to `Boolean` — so a String
  threw on the EDT and the caller saw nothing but a cell that had not changed. PH-GFP
  needs it: `Stim` has to be **Clamped** before its initial condition may depend on `t`.
  `setCell` now converts for columns whose declared class is Boolean.

Naming debt fixed at the source, rather than worked around in the scripts:
`StructuresTable`, `ReactionsTable`, `SpeciesTable`, `MolecularTypeTable`,
`ObservablesTable`, `SubVolumesTable`, `StructureMappingTable`, `SimulationsTable`,
`SpatialObjectsTable`, `SpatialProcessesTable` and their New/Delete buttons,
`SpatialProcessParametersTable`, `SpatialObjectQuantitiesTable`, `subdomainShapeComboBox`,
the ten shape fields in `AddShapeJPanel`, `EventsTable`, `EventActionsTable`,
`EventSingleTimeTextField`, `OutputFunctionsTable`, `FunctionDomainComboBox`,
`PreviousButton`, `FinishButton`, `DomainRegionsList`, `AutoMergeButton`,
`HistogramPanel`, `HistogramApplyButton`, `ParameterEstimationParametersTable`,
`AddEstimationParameterButton`, `ParameterEstimationResultsTable`, `SolveByCopasiButton`,
`ExperimentalDataMappingTable`, `NumberOfParticlesRadioButton`, `PathwayPreviewTable`,
`PathwayPreviewImportButton`, `PathwayObjectsTable`, `PhysiologyLinksButton` and
`ReactionReversibleCheckBox` (a bare checkbox whose label is a separate JLabel, so it had
nothing to be addressed by).
`ScrollPaneTable` and `SortTable` were each used by eight or more panels.

## A finding worth passing to whoever owns the tutorials

**Simple FRAP never tells you to map the membrane.** Mapping only `EC` and `Cyt` leaves
`PM` reading *Unmapped* while the model still reports **0 errors** — but VCell then picks
a different solver (SundialsPDE rather than Fully-Implicit). Followed literally, the
tutorial can produce a different simulation than the one it is teaching.

**Two documents in this set are reference guides, and their claims have drifted** (issue #2069). They
have no steps to follow, so `quickstart.sh` and `spatial-rule-based.sh` check what the
documents *assert* against the client instead. Between them, 10 claims still hold and 6
have gone stale. The ones worth acting on:

- Quick Start says **diffusion constants default to zero**; they default to 10.0 µm²·s⁻¹
  for a volume species and 0.1 on a membrane. The same tip warns that zero "is always
  illegal when a molecule is involved in a membrane flux", so a reader who trusts it goes
  hunting for a problem that is not there.
- Quick Start's worked figure, **"a spherical cell with a 10 micron diameter is 523.33
  micrometers cubed"**, is low by 0.05% — (4/3)·π·5³ is 523.5988.
- Quick Start says import supports **"VCML and SBML"**; it now takes eight formats, and
  `.bngl` among them is the only route to a rule-based model that avoids the graphics
  editor entirely.
- The Spatial Rule-Based guide sends readers to two tutorial models, **`Mix_Reactions_Rules`
  and `RB_Enzyme_Kinetics`**, that are no longer in the Tutorials folder.
- It lists **"only mass-action kinetic laws are supported"** among limitations it calls
  "temporary, will be lifted in future releases" — and that one *has* been lifted, at least
  in part: a rule now offers Henri-Michaelis-Menten (Irreversible) as well. This is the only
  current document covering spatial rule-based modelling, so it is the only place a reader
  would find out.

**There is a public reference model for every one of these tutorials**, in the VCell
database under BioModels → Tutorials: `Tutorial_FRAP`, `Tutorial_FRAPbinding`,
`Tutorial_MovingBoundary`, `Tutorial_MultiApp`, `Tutorial_PathwayCommons`,
`Tutorial_PH-GFP`, `Rule-based_egfr_tutorial`, `Rule-based_Ran_transport`,
`Rule-based_egfr_compart` and `Membrane Frap`. The rule-based tutorials say so outright
("match all values to the model in the Tutorials folder") because they omit most of their
numbers; the others do not mention it. Those models are a stronger check than "0 errors",
and the two rule-based scripts use them as one — both reproduce their reference exactly,
warning count included.

**A rule-based model cannot be built without the graphics editor** — issue #2068. Molecules,
Species and Observables can be stated as BNGL in a table column; reaction rules cannot, and
their editor exposes no components at all. Making that column writable for `ReactionRule`
is the small version of the fix, but not a sufficient one: raw BNGL needs autocomplete over
the available molecule patterns, and site states and bonds have to stay consistent across
reactant and product patterns — which the graphics editor guarantees by construction. What
a more accessible textual route should look like is a design question.

**Pathway Commons works, and the caution in its storyline is now discharged.** Both
services answer: the search goes to the current `pc2` API (v14) and the import pulls BioPAX
from Reactome. One thing is fragile enough to name: the import uses Reactome's *old*
RESTful API, superseded by ContentService, and it answers 400 to a stable `R-HSA-`
identifier — it works only because the client strips the prefix and passes the bare number.
That is the most likely part of this tutorial to break next.

**PH-GFP's spatial half is built with every diffusion constant left at its default**, and
the PDF never mentions them. That is not an omission the script should fix by inventing
numbers, but it is worth an author's attention: the whole point of the spatial application
is that the fluorescent signal spreads.

## Scenario or recording?

Both are here, and they are not equivalent:

- **`simple-frap.sh`** is the durable artifact. It resolves rows by displayed text and
  columns by header *at run time*, and it **asserts** — the structure mapping check stops
  the run rather than letting a silent no-op flow downstream.
- **`../recordings/tutorials/simple-frap.json`** is a faithful capture of one run, and is
  what you want for screenshots (`replay.py --shots`) or a filmed walkthrough
  (`--driver robot`). It is a linear list with no conditionals, so it is more brittle:
  the "structure not mapped" dialog appears or not depending on timing, and a rename step
  records the row's *new* text, which by definition cannot be found before the edit.

Prefer the scenario for verification, the recording for documentation.
