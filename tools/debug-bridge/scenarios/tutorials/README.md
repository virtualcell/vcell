# Reproducing the vcell.org tutorials as scripts

The tutorial PDFs at `vcell.org/webstart/VCell_Tutorials/` are mostly screenshot decks
from 2016–2022, shot against VCell 6.1–7.2. Six of them were refreshed in July 2025 (the
`7.7/` subdirectory); the rest still describe a client that has moved on.

This directory holds two things per tutorial: a **storyline** — what the document actually
teaches, in prose, extracted from the PDF — and, where it could be built, a **script** that
reproduces it against a current client through the [debug bridge](../../README.md).

| Document | Storyline | Script | State |
|---|---|---|---|
| `SimpleFRAP_7.2.pdf` | [simple-frap](storylines/simple-frap.md) | [`simple-frap.sh`](simple-frap.sh) | **reproduced**, 0 errors |
| `MovingBoundaries.pdf` | [moving-boundary](storylines/moving-boundary.md) | [`moving-boundary.sh`](moving-boundary.sh) | **reproduced**, 0 errors |
| `FRAPBinding_7.2.pdf` | [frap-with-binding](storylines/frap-with-binding.md) | — | route identified, not built |
| `PHGFP_7.2.pdf` | [phgfp](storylines/phgfp.md) | — | route identified, not built |
| `MultiAppTransport_7.2.pdf` | [multi-app-transport](storylines/multi-app-transport.md) | — | image segmentation blocks it |
| `Tutorial06_PathwayCommons_6.0.pdf` | [pathway-commons](storylines/pathway-commons.md) | — | depends on a third-party service |
| `VCell_Quickstart_7_Biomodel.pdf` | [quickstart](storylines/quickstart.md) | — | reference guide, nothing to script |
| `VCell6.1_Rule-Based_Tutorial.pdf` + `SingleCompartmentRuleBased.pdf` | [rule-based-egfr](storylines/rule-based-egfr.md) | — | **superseded by the 7.7 rewrite** |
| `VCell6.1_Rule-Based_Ran_Transport_Tutorial.pdf` | [rule-based-ran-transport](storylines/rule-based-ran-transport.md) | — | **superseded by the 7.7 rewrite** |
| `SpatialRuleBasedGuide.pdf` | [spatial-rule-based](storylines/spatial-rule-based.md) | — | reference guide; no current replacement |

## Running one

```bash
mvn compile -pl vcell-client -am -DskipTests
tools/debug-bridge/launch-client.sh
tools/debug-bridge/scenarios/tutorials/simple-frap.sh      # or moving-boundary.sh
```

Each takes a couple of minutes and leaves a complete, valid model on screen. It stops before
`File > Save` and before the green Run button: saving needs a logged-in account and Run
dispatches a real job to shared VCell compute, so whether to spend that is a decision for
whoever is at the keyboard.

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

- **Image segmentation** (`MultiAppTransport`) — painting and erasing pixels on an image,
  and dragging a histogram threshold. There is no model-level way to express it.
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
- **A panel's columns exist before its rows do.** Selecting a spatial process yields a
  parameter table with its four headers immediately and its velocity rows a moment later,
  so `col` succeeded while `row` still saw nothing and returned -1. `row`/`col` now retry
  for 10s instead of trusting a fixed `sleep`.
- **`SpatialProcessPropertyPanel` called itself `"SpatialObjectPropertyPanel"`** — a
  copy-paste slip that gave two different panels the same name.

Naming debt fixed at the source, rather than worked around in the scripts:
`StructuresTable`, `ReactionsTable`, `SpeciesTable`, `MolecularTypeTable`,
`ObservablesTable`, `SubVolumesTable`, `StructureMappingTable`, `SimulationsTable`,
`SpatialObjectsTable`, `SpatialProcessesTable` and their New/Delete buttons,
`SpatialProcessParametersTable`, `SpatialObjectQuantitiesTable`, `subdomainShapeComboBox`,
and the ten shape fields in `AddShapeJPanel`. `ScrollPaneTable` and `SortTable` were each
used by eight or more panels.

## A finding worth passing to whoever owns the tutorials

**Simple FRAP never tells you to map the membrane.** Mapping only `EC` and `Cyt` leaves
`PM` reading *Unmapped* while the model still reports **0 errors** — but VCell then picks
a different solver (SundialsPDE rather than Fully-Implicit). Followed literally, the
tutorial can produce a different simulation than the one it is teaching.

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
