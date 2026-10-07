# Phase 4 — Complete model-viewer accessibility implementation plan

Date: 2026-10-02. Status: **PLAN ONLY — implementation and acceptance gates OPEN**.

This is the requested implementation plan, not a record of completed implementation. It expands
[Phase 4 of the verified plan](uconn-color-blind-accessibility-verified.md#phase-4--model-viewer-must-1605)
through its governing §§12–16. Deliver all four original changes plus the diagram semantics,
D4/S8 fixes, geometry identification, palette, boundary, keyboard and assistive-technology work
required by §15 C. Do not call Phase 4 100% complete after only adding strokes and hover text.

The planning baseline is `e21a3024c4f17da197f9a003ff6077e11be5f444`. Source and the live bodies of
[#2139](https://github.com/virtualcell/vcell/issues/2139) and
[#2140](https://github.com/virtualcell/vcell/issues/2140) were inspected on the date above.
Their smaller historical acceptance lists do not override the verified plan's expanded gates.
No Java changes, builds, rendering tests, native QA or conformance certification are part of this
planning edit. Reinspect the implementation baseline before executing this plan.

## 1. Completion contract and scope

| Work package | Required result | Governing traceability |
|---|---|---|
| P4-A | Generic, reaction and rule edges have correct 2.5 px selection strokes, preserved catalyst dashes, distinguishable selected/neighbor states | 4.1–4.3, D1/D2, 8.3-f |
| P4-B | Both diagram families expose identities, relationships, selection and errors through keyboard-operable native controls and accessible state | §15 C/D, §13 applicable keyboard/focus/name-role-state rows |
| P4-C | Rule molecule errors retain a visible non-color cue during selection; all six S8 consumers have readable status text and semantics | D4/S8, #2140 |
| P4-D | Geometry INDEX_TYPE hover readout retains its numbers and appends the correct compartment name | 4.4, G1, 8.3-g |
| P4-E | Every geometry region is identifiable and locatable without hover or hue, including disconnected regions and absent-on-slice handles | G1/G2, §15 C/D |
| P4-F | Geometry palette, swatches and meaningful boundaries have measured contrast and CVD evidence with stable handle identity | G2, #2139 |
| P4-G | Automated, scientific, performance, native and independent acceptance evidence passes on the release candidate | 8.7, §§12/16 |

Phase 4 completion is not full #1605 closure or project-wide WCAG conformance. Those claims still
require all other work packages in the parent plan. Conversely, another issue owning an affected
Phase 4 dependency does not permit marking that dependency complete without integrated evidence.
Bring in shared theme/focus/font/status foundations from §15 B as needed and verify them here.
Use the parent matrix's criterion interpretation for Swing; do not equate a headless paint test
with WCAG or native accessibility acceptance.

Preserve scientific geometry, subvolume handles, coordinates, reaction direction, stoichiometry,
model serialization, solver inputs, and data exports. Presentation screenshots intentionally change.
Do not change persisted scientific colors or `ColorUtil.generateAutoColor` to implement this work.

## 2. Verified implementation entry points and corrections

Paths below are relative to the repository root; symbol names are authoritative when lines drift.

| Current source | Observed behavior and implementation consequence |
|---|---|
| `vcell-core/src/main/java/cbit/gui/graph/EdgeShape.java` | `paintSelf` and `paint_NoAntiAlias` call private `paint0`. Apply stroke selection there so both paths agree. Existing dashed stroke is width 1, cap BUTT, join MITER, miter 10, dash `{5,3}`, phase 10. Solid drawing inherits the incoming stroke. `isInside` tests curve geometry. |
| `vcell-core/src/main/java/cbit/vcell/graph/ReactionParticipantShape.java` | `paintSelf` already chooses dark red for selected start species, red for selected edge, black otherwise; selected width is missing. Arrowheads are filled paths computed from the curve. |
| `vcell-core/src/main/java/cbit/vcell/graph/RuleParticipantEdgeDiagramShape.java` | `paintSelf` uses `forgroundColor` and lacks the reaction renderer's explicit selected-neighbor color calculation. Add equivalent state resolution, not just a replacement of `DASHED_STROKE`. |
| `vcell-core/src/main/java/cbit/gui/graph/GraphModel.java` | `PROPERTY_NAME_SELECTED`, `setSelectedObjects`, `getSelectedObjects` provide a selection synchronization boundary. Selection storage is a set: do not use its iteration order as keyboard order. |
| `vcell-client/src/main/java/cbit/gui/graph/gui/GraphPane.java` and `cbit/vcell/graph/gui/{ReactionCartoonEditorPanel,ReactionCartoonTool}.java` | Canvas/editor integration points for structured navigation, selection synchronization and focus. Existing tool bindings must remain usable. |
| `vcell-core/src/main/java/cbit/vcell/graph/MolecularTypeLargeShape.java` | Error/normal outline paint branches differ only by hue in both highlighted and unhighlighted states. An existing issue-table icon alone does not fix the glyph. |
| `vcell-client/src/main/java/cbit/vcell/geometry/gui/GeometryViewer.java` | `refreshSourceDataInfo` returns early for null geometry or dirty sampled image. `setGeometry` and `propertyChange` must also manage provider/readout lifecycle; current listeners do not directly handle every subvolume rename. |
| `vcell-client/src/main/java/cbit/image/gui/ImagePlaneManagerPanel.java` | `updateInfo(MouseEvent)` is private and mouse-driven, with curve/membrane precedence, numeric coordinates and PDE-specific names. It calls `getInfoJlabel().getGraphics().getFontMetrics()`, unsafe before realization/headlessly. |
| `vcell-core/src/main/java/cbit/image/SourceDataInfo.java` | `getDataAsTypeIndex(x,y,z)` supports int arrays and unsigned bytes (`& 0xFF`), including strides. Use it; do not parse display strings or interpret the voxel's array offset as its handle. |
| `vcell-core/src/main/java/cbit/vcell/geometry/GeometrySpec.java` | Actual lookup is `getSubVolume(int handle)`; `subVolumeForHandle` in the parent plan is conceptual pseudocode. Missing handles can return null. |
| `vcell-client/src/main/java/cbit/vcell/geometry/gui/GeometrySubVolumeTableModel.java` | Existing columns are Name and Value, with editable name/expression behavior. Extend this table for handle identity rather than replacing its editing contract. |
| `vcell-core/src/main/java/cbit/image/DisplayAdapterService.java` | `createContrastColorModel` computes 256 colors; `getHandleColorMap` also consumes it. Palette changes have more consumers than the viewer alone. |

`docs/accessibility/color-audit.md` referenced by the historical issue is absent in this checkout.
Recover/reconcile the audit into the implementation evidence; do not assume the issue's branch file
is present or that prior test results apply to this SHA. `GuiConstants` currently has no shared
error/warning text tokens found by inspection, so P4-C must supply or integrate them.

## 3. P4-0 — Baseline and prerequisites

1. Record branch/SHA, dirty state, applicable repository instructions, Java/Maven/native prerequisites,
   and baseline test results. Preserve unrelated working-tree edits throughout execution.
2. Create/update the parent's `docs/accessibility/{surface-inventory.md,wcag-matrix.csv,color-audit.md,
   institutional-requirements.md,test-protocols.md,support-matrix.md,conformance-report.md}`.
   Store Phase 4 evidence under `docs/accessibility/evidence/phase-4/` or durable linked CI artifacts.
   Assign a named implementer and independent reviewer to P4-A through P4-G before implementation.
3. Inventory live reaction/rule views, collapsed/grouped views, molecule-glyph hosts, geometry
   viewer/subvolume/CSG/mapping/summary views, and all six S8 hosts. Record each rendering and
   interaction path, including read-only views and exported diagram images where shipped.
4. Pin small and realistic dense diagrams, catalyst/reversible/multi-edge reactions, valid/invalid
   rule molecules, analytic/image/CSG geometries, and 1D/2D/3D sampled fixtures. Include handles
   0–7, 8+, 127/128/255, noncontiguous handles, disconnected components, and thin one-voxel regions.
5. Capture pre-change source data/serialization hashes, curve/arrow geometry, screenshots and timings.
   Record actual supported OS/JDK/look-and-feel/AT/bridge versions before claiming platform support.
   Measure warm paint time, keyboard-response latency, region-index build time and memory on small
   and large fixtures. Set and review numeric budgets before renderer/index changes; record both
   absolute latency and regression limits. Unset budgets block the performance gate.

## 4. P4-A — Edge selection rendering

Add the required protected constants in `EdgeShape`:

```java
protected static final BasicStroke SELECTED_STROKE = new BasicStroke(2.5f);
protected static final BasicStroke SELECTED_DASHED_STROKE = new BasicStroke(
    2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
    new float[] {5f, 3f}, 10f);
```

Use an explicit shared 1.0f solid stroke for normal rendering so unrelated caller state cannot
silently change the required normal width. A small protected stroke resolver may select between the
four existing/new combinations. Apply it to the curve in `paint0` and both participant overrides.
Restore the incoming graphics stroke with `try/finally` or a disposable child graphics context;
keep label and subsequent-shape rendering free of stroke/paint/transform leakage.

| Edge selected | Start selected | Generic edge | Reaction/rule participant | Participant color |
|---|---|---|---|---|
| No | No | 1.0 | 1.0 | Black |
| No | Yes | 1.0 | 2.5 | Existing dark red / equivalent rule neighbor state |
| Yes | No | 2.5 | 2.5 | Red |
| Yes | Yes | 2.5 | 2.5 | Red, with neighbor relationship still available in text |

Generic edges depend only on their own selection. Only the participant start selection broadens
their stroke; selecting the end reaction alone must not do so. Keep catalyst `{5,3}` dash phase,
cap and join unchanged. Do not invent a rule catalyst subtype: exercise actual rule subclasses,
and cover the base dashed branch with a fixture if no production rule subclass uses it.

Width identifies emphasis but cannot distinguish direct selection from selected-neighbor emphasis.
Expose explicit visible state text, e.g. “Selected edge” versus “Connected to selected species,”
in the synchronized diagram selection panel. Show participant identity, role, endpoints and
stoichiometry where applicable. Add a local badge/label or selection overlay when a dense/overlapping
diagram cannot associate that text with the emphasized edge; retain an explicit “Show selected”
navigation action and a reversible display-only isolation option for overlapping edges.

Measure the state-carrying indicator against actual adjacent fills, not just red versus black.
Retain red hues where they pass. For failing combinations add a contrast-safe outline/backplate or
theme-aware indicator while retaining the red semantic accent. Verify at least 3:1 for meaningful
non-text indicators and normal text at least 4.5:1. Focus, errors and selection must remain distinct.
Keep curve control points, attachment points, hit-test geometry and arrowhead direction unchanged.

## 5. P4-B — Diagram keyboard and assistive-technology access

Integrate an accessible “Diagram elements” table/tree beside the graph, using native Swing widgets.
This is an interactive counterpart of the diagram, not a static dump. Reuse suitable existing
structured controls if they satisfy the following contract; otherwise add a client-side adapter
and panel. Keep domain identity/selection in `GraphModel`, not in row indices or painted colors.

- Rows expose element name, type, source/target relationships, participant role, direct selection,
  selected-neighbor emphasis, and issue severity/summary. Duplicate names remain unambiguous through
  endpoint/structure context and stable model identity. Grouped views preserve navigation to members.
- Establish deterministic model-based traversal independent of `HashSet` order. Tab/Shift+Tab reaches
  and leaves the panel; arrow/Home/End navigation and standard multiselection work; Enter invokes
  “Show selected.” Provide named buttons/actions for clear selection, show/isolate/reset, and access
  to existing applicable editing/property actions. Preserve existing canvas/tool shortcuts.
- Mouse and keyboard selection update the same `GraphModel` objects. Listen to selection and graph
  rebuild events; guard feedback loops, detach old listeners and preserve selection by identity.
  Deleted objects clear their row/readout; undo, redo, rename, view switching and model replacement
  refresh content without moving focus unexpectedly.
- Provide visible focus and complete accessible names, roles, descriptions and selected states.
  Report neighbor emphasis as a relationship/status, not an incorrect `SELECTED` state on an
  unselected edge. Publish dynamic selection/error changes using native accessible events; avoid
  announcing every repaint or mouse movement. Confirm the announcement with actual AT.
- Make applicable existing edit operations reachable by keyboard through shared editor actions.
  Do not silently disable actions in the table or claim equivalence because reading alone works.
  Inventory create/edit/delete/configure workflows for these hosts and test the equivalent routes.

For molecule-shape panels outside `GraphModel`, connect the same semantic contract to their actual
owner/selection/issue source rather than forcing them into an unrelated graph model. A bridge gap
requires remediation or a complete accessible alternate workflow on that supported platform.

## 6. P4-C — D4 glyph errors and the complete S8 text set

In `MolecularTypeLargeShape`, add a persistent shape-distinct error badge (for example an exclamation
mark in a contrast-safe badge) plus visible “Error” text in the associated issue/selection readout.
Keep the glyph cue in both `isHighlighted()` branches. Reserve layout space so it does not cover the
molecule label, anchor hotspot, components or bonds. Selection must never overwrite the error cue;
expose both states and the issue description programmatically. Use the existing issue source,
not a second independently maintained error flag. Ensure issue resolution clears all representations.

Implement/integrate shared error and warning text tokens in
`vcell-core/src/main/java/cbit/vcell/client/constants/GuiConstants.java` with a background-aware
selection/theme strategy. Reconcile with Phase 7 so there is one shared contract. A hard-coded dark
red that passes white alone is insufficient. Use contrast-safe selected text with explicit severity
where necessary. Normal-sized text must meet 4.5:1 in all applicable backgrounds; assess exceptions
individually. Retain accessible meanings such as “Unmapped,” “Error,” and changed-value text.

Remediate every live branch in these six sources under `vcell-client/src/main/java/`:

| S8 consumer | Required specific handling |
|---|---|
| `cbit/vcell/mapping/gui/StructureMappingTableRenderer.java` | “Unmapped” text, selected/alternate rows; inspect issue borders separately rather than deleting error indication. |
| `cbit/vcell/solver/ode/gui/MathOverridesPanel.java` | Remove-unused-overrides button text, keyboard action and accessible name remain meaningful. |
| `cbit/gui/MultiPurposeTextPanel.java` | Custom painted error-line text uses `Graphics.setColor`, so a `setForeground` search misses it; expose the error location/text through accessible UI. |
| `org/vcell/util/gui/DefaultScrollTableCellRenderer.java` | Changed network-constraint values retain textual/programmatic changed state; reset reused renderer state between rows. |
| `cbit/vcell/microscopy/gui/defineROIwizard/DefineROI_SummaryPanel.java` | Invalid start-index text remains readable and associated with its input/correction action. |
| `cbit/vcell/numericstest/gui/NumericsTestCellRenderer.java` | All error/result branches, including multiple red assignments; selected tree rows and accessible error meaning. |

Search foreground assignments, painted text, aliases and HTML formatting, not just literal red
constants. Record the disposition of remaining red graphical borders/icons separately. Add to the
S8 inventory and fix any additional affected consumer discovered by this scoped sweep. Test shared
token consumers affected outside these six paths; do not mark unrelated Phase 7 work complete.

## 7. P4-D — Index names and robust readout lifecycle

Add nullable `IntFunction<String> indexLabelProvider` and a documented setter to
`ImagePlaneManagerPanel`. Null preserves existing callers' behavior. For defined INDEX_TYPE data,
resolve the value with `getDataAsTypeIndex(ci.x, ci.y, ci.z)` and append `" \"" + name + "\""`
when a nonblank name exists. Keep handle, coordinates, voxel index and all existing numeric text.
RAW_VALUE_TYPE and INT_RGB_TYPE must not invoke the provider. Do not replace existing curve,
membrane, Chombo, PDE or ROI information with a geometry name.

In `GeometryViewer`, install a null-safe lookup using
`geometrySpec.getSubVolume(handle).getName()` for the same geometry snapshot as the sampled data.
Missing handles keep their numeric identity and show “Unknown compartment” in the geometry-specific
readout; the generic optional-provider API adds no quoted null/empty value. Do not allow lookup
failure to break pointer handling; unexpected failures get diagnostic reporting without per-move
log flooding, while the numeric readout remains available.

Clear old provider/data/readout on geometry replacement, null geometry and incompatible source
transitions. Dirty or unavailable sampled data must show an explicit pending/unavailable state,
never new names against stale pixels. Refresh after sampling completes. Track subvolume rename,
addition, deletion and replacement; detach listeners from old objects and update the readout even
when the pointer does not move. Perform Swing mutations on the EDT.

Extract coordinate/data-to-readout formatting from mouse dispatch so keyboard and pointer paths
share one implementation. Keep it package-private or behind a small component contract suitable
for integration testing, rather than exposing arbitrary UI internals. Replace the realization-
dependent font-metrics call with `label.getFontMetrics(label.getFont())`. Trigger label, tooltip
and accessible-description updates consistently, without mouse events for keyboard selection.

## 8. P4-E — Regions without hover or hue

Extend the existing subvolume table with a read-only Handle column and clear selection/state text.
Audit hard-coded column indices, comparators, renderers, editor routing and model/view conversions
when adding it; name and analytic-expression editing must continue working. Name and handle remain
available to AT regardless of swatch color. Disambiguate sampled connected regions from subvolumes:
one subvolume can have multiple disconnected components.

Implement these keyboard-accessible controls in the geometry viewer:

1. Select a subvolume row by name/handle; show persistent “Compartment <name>, handle <h>” text.
   Provide “Show region” and next/previous component actions. Locate an actual voxel and slice in
   the selected component, center it, and display a non-color outline/crosshair and textual identity.
2. Provide named coordinate/index controls and slice/orientation controls to inspect arbitrary
   sampled positions without a pointer. A keyboard-selected voxel reports the same name, handle,
   spatial coordinates and index as hover. The marker identifies the readout's location.
3. Show explicit “Not present on this slice” and “Not present in sampled geometry” states. Offer
   navigation to a containing slice when one exists; do not invent a sample for an empty region.
4. Keep persistent keyboard selection independent from transient hover. Mouse exit restores the
   selected readout; hover must not silently move keyboard focus or reset the chosen region.
5. Expose actions, selected state, coordinates, component count and changes through native accessible
   controls/events. Tab navigation has a visible focus indicator and no trap. Reset restores the
   complete view without changing the model or pixel values.

Use a generation-keyed sampled-data index for handle/component locations. Compute expensive scans
off the EDT; cancel/discard old-generation results after replacement/resampling. Bound memory and
measure performance on large volumes; avoid rescanning the entire volume on each mouse move,
repaint or keystroke. Disable pending navigation with an accessible status until data is ready.
Keep masks/overlays separate from scientific arrays and invalidate them on source changes.

## 9. P4-F — Palette, swatches and boundaries

Implement a deterministic geometry categorical palette in `DisplayAdapterService` and route every
geometry consumer consistently. Inspect transitive `getHandleColorMap()` callers before changing
the shared factory; if an unrelated scientific/export contract requires the legacy factory, add a
geometry-specific factory and explicitly route all geometry consumers to it. Document that choice
and prove the unaffected contract. Never change palette indexing or conflate 256 indexed entries
with the special-color reservation used by value colormaps.

Required consumers: `GeometryViewer`, `GeometrySubVolumeTableCellRenderer`,
`CSGObjectTreeCellRenderer`, `StructureMappingTableModel`, `StructureMappingTableRenderer`,
`GeometrySummaryPanel`, and relevant transitive handle-map users. Refresh cached palettes on a
supported theme change. Name/handle mapping must agree in image, table, tree, summary and mapping UI.

- Select and publish the exact first-eight RGB values only after measuring all pairs for protan,
  deutan and tritan simulation. #2139 asks for minimum CAM02-UCS ΔE′ ≥10. The current
  `ColorAccessibilityTest` uses Machado plus CIELAB ΔE76 for a six-color plot palette; it does not
  prove that eight geometry handles meet this target. Use `.agents/cvd_analysis.py`'s CAM02-UCS
  analysis as a starting point, pin tool versions, and put a reproducible geometry-palette check
  in maintained test tooling. Report each metric by its correct name; no threshold substitution.
- Keep deterministic mapping for handles 8–255, with any retained legacy formula explicitly
  documented. More handles or repeated colors still require names, keyboard location and outlines.
  Do not promise 256 pairwise-distinct perceptual colors or treat ΔE′ as a WCAG criterion.
- Measure fills/swatches against real backgrounds and every meaningful adjacency in fixture maps.
  If neighboring fills do not meet the required 3:1 boundary contrast, render a presentation-only
  boundary/outline that does. A two-tone/adaptive boundary is a candidate, not automatic proof:
  measure the indicator against each adjacent region and preserve thin regions at supported zooms.
- Measure selected-region and focus indicators independently from ordinary boundaries. Retain labels
  or a contrast-safe backplate for text over images. Support grayscale/CVD inspection and actual
  themes/high contrast; simple grayscale conversion is not a contrast or AT test.
- Include a release note because geometry display colors change. Verify unchanged saved model,
  handle-to-subvolume associations, pixel data and scientific exports. Update intentional image
  baselines with reviewed evidence, not bulk replacement of every failing snapshot.

## 10. Automated verification matrix

Add JUnit 5 `@Tag("Fast")` tests in the module owning each implementation. Names below are proposed
test classes, not existing or already-passing tests. Swing construction/events/assertions run on
the EDT. Use real small domain fixtures; a helper-only test cannot establish production paint or
event wiring. Keep core tests independent from client Swing dependencies.

| Test group | Required assertions |
|---|---|
| `EdgeShapeAccessibilityTest` (core) | All solid/dashed × selected states through `paintSelf` and `paint_NoAntiAlias`; 1.0/2.5 widths, exact dash/phase/cap/join, incoming nondefault stroke restored. |
| `ReactionParticipantShapeAccessibilityTest` (core), 8.3-f | Reactant/product/catalyst × neither/edge/start/both/end-only selected; stroke and state precedence, visible catalyst gaps, unchanged label/arrow direction and curve points. |
| `RuleParticipantEdgeAccessibilityTest` (core) | Equivalent real reactant/product rule paths, selected-neighbor handling, dashed base branch and restored graphics state. |
| Diagram rendering regressions (core/client as appropriate) | BufferedImage measurements confirm actual width/gaps without testing only constants; grayscale state cues, dense overlaps, arrows meeting curves, no clipping. Hit-test results before/after match fixed inside/outside probes. |
| `DiagramSelectionAccessibilityTest` (client) | Invoke actual bound actions; selection synchronization in both directions, multi-selection, stable traversal, graph rebuild/deletion/undo/rename, correct neighbor versus selected semantics, listener cleanup and accessible changes. |
| `MolecularTypeLargeShapeAccessibilityTest` (core) | Valid/error × highlighted/unhighlighted, small/large zoom, badge survives selection and avoids anchor/component/label overlap; error removal resets state. |
| `StatusTextAccessibilityTest` (client) | Every S8 branch in actual renderer/panel, normal/selected/alternate/focused/error states, contrast and semantic text, reused renderer reset. Custom painted editor error text is included. |
| `ImagePlaneManagerPanelAccessibilityTest` (client), 8.3-g | Real INDEX_TYPE source + provider yields quoted name and original numbers through the event path and shared keyboard path; byte 128/255, int/strided arrays, nonzero origin/extent, null/blank/missing provider values and undefined data. |
| Readout compatibility (client) | RAW/RGB never call provider; PDE/Chombo/membrane/ROI precedence and zoom/pan descriptions preserved; unrealized component does not throw from font metrics. |
| `GeometryViewerAccessibilityTest` (client) | Actual geometry handle lookup; rename without pointer move; null/replaced/dirty geometry clears stale text; generation races cannot apply old index/provider; zero-dimensional/no-image states. |
| `GeometryRegionNavigationTest` (client) | Sorting/model-view conversion, keyboard row selection, all slice axes, disconnected components, absent/empty regions, next/previous navigation, coordinate bounds, focus, accessible names/states and restoration after hover. |
| `GeometryPaletteAccessibilityTest` (core) + maintained CVD script | 256-entry/indexing contract, first-eight CAM02-UCS target, stable sparse/high-handle mapping, actual background/boundary contrast, consistent consumers; correct independent metrics. |
| Scientific/performance regressions | Byte-for-byte sampled data and equivalent model round trip, unchanged reaction/geometry scientific fields and applicable exports, reviewed render differences, predeclared latency/memory budgets. |

Parameterize 1, 8, 9 and large realistic region counts, minimal/dense diagrams, duplicate names,
overlap, zoom extremes, 100%/200% text scaling and representative actual backgrounds. Measure
contrast using composited colors and identify the precise state-carrying indicator. Do not demand
that every antialias fringe pixel independently meets a foreground threshold or round up failures.

Run from the root with Java 17 and documented Python/native prerequisites:

```sh
mvn test -Dgroups=Fast -pl vcell-util,vcell-core,vcell-client -am
mvn test -Dgroups=Fast
```

During iteration, select the newly added tests with `-Dtest=<actual-class-list>` and
`-Dsurefire.failIfNoSpecifiedTests=false` for reactor dependencies, but verify that every intended
test actually ran in Surefire reports. Do not use missing-test suppression as evidence of a pass.
Run applicable module/scientific regressions identified in P4-0 as well. Record exact commands,
exit codes, counts, disabled/skipped tests and failures. Do not skip native/provider prerequisites
and report those gates green. Inspect the six S8 files for remaining red foreground AND painted
text paths, and check scoped whitespace/diffs after implementation.

## 11. Native, manual and independent acceptance

Two reviewers, at least one independent of implementation, execute the following scripts on the
exact supported Windows/macOS/Linux, JDK, look-and-feel and accessibility-bridge combinations from
the support matrix. Include working NVDA/JAWS, VoiceOver and Orca combinations as applicable to
claimed support; record demonstrated bridge capability. Include normal/light, dark and Windows
high-contrast modes, 100%/200% text and display scaling, grayscale and protan/deutan/tritan views.
Invite CVD and screen-reader users where available; absence does not remove mandatory manual QA.

1. Open reaction and rule models. Without a mouse, find every participant, select an edge, select its
   start species, and select both. State which object is selected versus merely connected. Inspect
   catalyst role, arrow direction and dense overlaps; show/isolate/reset without losing identity.
2. Use keyboard and AT to inspect and edit relevant element properties through the equivalent route.
   Verify focus location, announcements, multiselection, view switches, rename, deletion, undo/redo,
   save and reload. Confirm visual and programmatic selection agree without using hue.
3. Trigger and correct a molecule error while selected and unselected. Identify error and selection
   simultaneously; confirm accessible error detail, local badge and clearing behavior.
4. Trigger each of the six S8 states. Read text in selected/alternate/focus/theme combinations;
   hear the status through AT and complete the associated correction/action without a pointer.
5. Open image, analytic and CSG geometries. With the pointer parked away, identify every region by
   name/handle, locate each disconnected component, inspect coordinates and cross slices. Confirm
   explicit absent/empty states, narrow boundaries and accessible orientation/slice controls.
6. Rename/remove subvolumes, resample, replace geometry and switch to nonspatial geometry. Verify no
   stale names, lingering markers, wrong slices or delayed old-generation updates. Save/reload and
   compare scientific data. Verify pointer hover still appends the correct name (8.3-g live check).
7. Repeat visual tasks under grayscale/CVD simulations and supported contrast modes. Verify actual
   boundary/indicator/text contrast, zoomed arrows, thin regions, focus visibility and unclipped text.

Every result records test ID, parent requirement/criterion, fixture, state, expected/actual result,
SHA/build, exact environment, date, operator/reviewer and durable evidence. Attach screenshots plus
keyboard task logs, accessibility-tree/state captures and AT observations. Screenshots alone cannot
prove keyboard operation or announcements. Label FAIL, BLOCKED and NOT TESTED distinctly; missing
platform access or an inaccessible supported bridge blocks 100% completion.

## 12. Delivery sequence and final gate

Execute in this order; each change set includes its focused automated checks and evidence update:

1. P4-0 baseline, support/owner matrix, fixtures, performance limits and shared foundation contract.
2. P4-A stroke/state rendering; P4-B diagram interaction and semantics; integrate local state cues.
3. P4-C glyph errors and all S8 consumers; integrate shared tokens with affected Phase 7 consumers.
4. P4-D optional provider and readout extraction/lifecycle; P4-E keyboard region navigation/index.
5. P4-F measured palette, all consumers and boundary rendering; scientific/export regression checks.
6. P4-G complete automated suite, platform QA, independent review; repair and rerun affected gates on
   the same final candidate. Update parent-plan status only with actual result/evidence links.

Completion checklist — every box remains open until backed by final-candidate evidence:

- [ ] Original 4.1/4.2/4.3 implemented, 8.3-f expanded across all edge paths, arrow/hit-test regressions pass.
- [ ] Direct/neighbor/error states remain identifiable in dense diagrams without hue; keyboard/AT routes pass.
- [ ] D4 badge and all six S8 consumers remediated; integrated #2140 scope passes every applicable state.
- [ ] Original 4.4 implemented; 8.3-g and provider/lifecycle/compatibility regressions pass.
- [ ] Every sampled region can be named and located without hover, including sparse/disconnected cases.
- [ ] First-eight palette target and all required boundary/focus/selection/text contrast measurements pass.
- [ ] All geometry consumers agree; scientific data/serialization/export invariants and performance budgets pass.
- [ ] Required Fast/module gates pass with actual test counts and no unaddressed applicable failures/skips.
- [ ] 8.7 and §15 C native keyboard, AT, CVD, theme/scaling and independent review evidence complete.
- [ ] Owners/reviewers sign traceability and release note; no Phase 4 dependency is FAIL/BLOCKED/NOT TESTED.

Only then report **“Phase 4 implementation and its required acceptance gates complete.”** Report
remaining project phases separately. A merged patch, green unit tests, filed issues or a completed
planning document alone does not meet this completion contract.
