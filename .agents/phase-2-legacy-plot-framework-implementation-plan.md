# Phase 2 — Legacy Plot Framework Implementation Plan

## 1. Objective and Definition of Done

Planning record: 2026-09-30, repository HEAD `fd801900025c0d1cd1a9e736b539d76928bb0250`, including the pre-existing Phase 1 working-tree changes. This file proposes future implementation; no production change or verification result is asserted here. The correctly spelled `implementation-plan.md` path controls over the introductory `implemtation-plan.md` typo because it is the only path authorized by the strict write rule. Section 13 retains the requested heading “Autolan” and contains the automated test plan.

Implement all of 2.1–2.5, including SHOULD items, with targeted changes to the existing Swing renderer. Completion requires:

1. Both required automatic-color paths use `ColorUtil.seriesColor`; explicit user colors retain precedence, and non-auto plots retain black fallback.
2. A single shared stroke rule supplies plot curves, histogram outlines and their legend/list samples; width/cap/join/miter/phase match 2.1.
3. `varyLineStyles` defaults true, has working two-way binding, and restores the existing solid 1.5 px stroke when false.
4. One `Path2D.Double` draw per curve preserves phase through offscreen excursions and step transitions; separate curves and actual discontinuities never become connected.
5. Nodes cycle circle/square/triangle/diamond/cross, centered, at least 6 px; node visibility and point-only rendering remain functional.
6. Histogram fill geometry/paint remain unchanged; outlines use the corresponding stroke.
7. Legend samples are at least 50×12, show the actual stroke and corresponding marker, and update immediately with settings.
8. Pointer text begins with the current model-series name and retains the exact existing formatted x,y suffix.
9. Multisource colors, selection, ordering, custom colors and list style indicators agree with the embedded plot.
10. 8.3-a/b/c/d, applicable regression checks, 8.7, CVD/grayscale review and approximately 100,000-point performance validation actually pass, with no unresolved Phase 2 blocker.
11. Only then does the verification document record 2.1–2.5 complete, supported by dated commands, results and evidence links. Broader #1603/#1605 completion is not implied.

**Completion is currently blocked by contradictory acceptance contracts, not by missing code access.** In particular, the current shared dash contract cannot pass the literal 8.3-a/b requirements. Section 3 gives the controlling behavior, alternatives and the required reconciliation gate. Do not silently redefine a test or label an alternative test as a pass of the original. The rest of this plan is executable independently; a claim of 100% completion is not.

## 2. Repository Reconnaissance Findings

### Working-tree baseline and scope

`git status`, full unstaged diff and staged diff were inspected. The staged diff was empty. Existing changes are:

- Modified `.agents/uconn-color-blind-accessibility-verified.md`: Phase 1 completion/evidence and checkboxes, 35 insertions/8 deletions.
- Modified `vcell-util/src/main/java/org/vcell/util/ColorUtil.java`: 57 additive lines for the palette/dash APIs and documentation.
- Untracked `vcell-util/src/test/java/org/vcell/util/ColorAccessibilityTest.java`.
- Untracked `.cursor/colorutil_cvd_palette_dee5da7a.plan.md`, `.cursor/phase-1-1-cvd-safe-palette.md`, `.cursor/phase-1-2-color-accessibility-test.md`.

These are pre-existing work, not Phase 2 edits. Preserve them. The final planning-time `git diff --check` reports pre-existing CRLF/trailing-whitespace warnings in the ColorUtil additions; their bytes were not changed. Distinguish those baseline warnings from new Phase 2 whitespace defects. No existing target plan or repository `AGENTS.md` was found; checked ancestor guidance paths also yielded none. The verification document references an older research baseline `11b1f83d69`; use the current checkout for symbols and record actual baseline revisions for comparisons.

### Confirmed implementation facts

| Area | Repository evidence / consequence |
|---|---|
| Palette | `ColorUtil.seriesColor(int)` rejects negative indexes, cycles six colors: black, #999933, #004488, #8C510A, #0072B2, #CC6677. |
| Dashes | `ColorUtil.seriesDash(int)` rejects negative indexes; `(i / 6) % 4` selects null, {6,3}, {2,2}, {8,3,2,3}. Arrays are defensive copies; all non-null entries are positive and BasicStroke-valid. Colors/dash pairs repeat at 24. |
| Plot stroke | `Plot2DPanel` has `lineBS_10`, `lineBS_15`, `lineBS_20` built with one-argument BasicStroke constructors. The 1.5 stroke is square-cap/miter-join, unlike the requested accessible butt-cap/round-join stroke. Axes and crosshair use the other widths. |
| Line drawing | `drawLinePlot` calls `getLinePlotSegments`, tests each segment against `plotRectHolder`, clips with `GeneralGuiUtils.clipLine`, then draws each separately. It enables antialiasing. |
| Step drawing | `getLinePlotSegments` inserts horizontal-to-next-x then vertical-to-next-y legs when `getBStepMode()` is true. Losing these elbows would alter scientific interpretation. |
| Points | `mapPoints` transforms all `PlotData.getPoints()` into mapped Point2D objects. `nodes[modelIndex]` stores them for pointer lookup. Existing variable `diameter` is really a half-extent: 2 means a 4 px circle; point-only line plots use 3 = 6 px, histogram point-only uses 4 = 8 px. |
| Invalid data | `PlotData.refreshStatistics` marks any NaN/infinity invalid; `Plot2D.visiblePlotsInvalid()` checks visible series. `paintComponent` displays PLOT DISABLED for invalid data/ranges before mapping. There is no explicit missing-data separator API in PlotData. |
| Histogram | `drawHistogram` fills 10-px-wide rectangles from each mapped value to mapped y=0; there is currently no outline. Histogram nodes are gray. |
| Indexes | `paintComponent`: model index `i` addresses data, hints and nodes; an independent dense visible index selects color. Both indexes must survive the refactor. |
| Legend | `PlotPane.LineIcon` is a public non-static inner class with private `LineIcon(Paint)` constructor; 50×2, solid 2 px, modifies caller graphics state. Only this legend constructs this particular class. Langevin has a different LineIcon; leave it alone. |
| Settings | Actual bean is in **vcell-core**, not client: `cbit.plot.Plot2DSettings`. Boolean defaults for nodes/crosshair/snap are true. String property names, lazy PropertyChangeSupport, no property-name constants. |
| Settings lifetime | Bean is not Serializable. Only constructor plus `saveSettings`/`restoreSavedSettings` snapshot are present; no on-disk settings persistence was found in repository references. Settings dialog applies live, Cancel restores via setters. |
| Multisource | `MultisourcePlotListModel` omits time columns, assigns `unsortedIndex`, then sorts by identifier/source or caller comparator. Pane colors use unsorted identity; selected plot order follows sorted selected rows. Renderer is a reused DefaultListCellRenderer JLabel, currently **has no swatch/icon**. |
| Tests | No active tests for these legacy plot classes were found. Existing JUnit Jupiter Fast tests and headless BufferedImage tests are available as conventions; see §13. No legacy-plot benchmark found. |

Important additional paths read: `vcell-core/src/main/java/cbit/plot/{Plot2D,PlotData,Plot2DSettings}.java`, `vcell-client/src/main/java/cbit/vcell/modelopt/gui/MultisourcePlotListModel.java`, `vcell-core/src/main/java/cbit/vcell/modelopt/DataSource.java`, root/client/core/util POMs, and `tools/debug-bridge/README.md`.

## 3. Source-of-Truth and Requirement Mapping

Labels used here: **Confirmed requirement** comes from the prompt/verified plan; **repository-derived** is observed code; **recommendation** is a proposed change, not an existing API; **blocker** prevents truthful completion, not useful independent implementation.

| Requirement | Implementation sections | Verification |
|---|---|---|
| 2.1 colors, strokes, continuous path, markers, histogram | 5, 6, 11 | 8.3-a/b, color/stroke/marker/histogram tests, 8.4-b, 8.8-c/e, performance, 8.7 |
| 2.2 line + marker legend | 7 | 8.3-c; filtering and immediate update tests |
| 2.3 default-on setting and solid opt-out | 8 | Bean/UI/live propagation/Cancel tests, 8.8-e |
| 2.4 series-name prefix | 9 | 8.3-d; snap/non-snap/filter/no-name cases |
| 2.5 multisource styles | 10 | 8.4-b, sorted/subset/custom-color/render-state tests |
| Accessibility objective: 1.4.1 / G111 color plus pattern | 11, 15 | Actual legend-to-curve matching under protan/deutan/tritan/grayscale; no whole-application conformance claim |
| Completion evidence | 19 | All required gates passed, no invented or skipped evidence |

### Exact verification definitions read from the source

- **8.3-a:** headless BufferedImage, 400×300, six curves; series 0 continuous, series 1 has at least one background gap every ≤12 px; solid series 1 fails.
- **8.3-b:** same with 5,000 points per curve; series 1 must retain periodic gaps, detecting per-segment phase restart.
- **8.3-c:** icon i stroke equals `getVisiblePlotStroke(i)`, icon height ≥12. Phase 2.2 additionally requires width ≥50 and centered marker.
- **8.3-d:** synthetic `pointerMoved` event; status starts with the series name.
- **8.7-a:** two reviewers, one ideally with CVD (otherwise Sim Daltonism/Color Oracle), scripted checklist on macOS, Windows and Linux; all yes and screenshots attached to PR. Any no fails.
- **8.7-b:** Windows High Contrast smoke of changed panels; logged, informational for #1604.
- **8.8-c:** styles off matches master rendering aside from color, geometry difference fails.
- **8.8-e:** one-sample spike on a dashed curve visible with styles on **or** off, and toggle documented.
- **8.4-b:** no `generateAutoColor` matches in the two targeted legacy files.
- **8.1:** `mvn test -Dgroups=Fast -pl vcell-util,vcell-core,vcell-client -am`, no new failures relative to baseline.
- **8.6-b:** includes ODE 6+ species and PDE time plots, plus four other out-of-Phase-2 scenarios; all filters must allow matching by style. Phase 2 supplies the legacy subset, not the entire global checkbox.

### Conflicts and controlling decisions

**C1 — dash contract vs acceptance: unresolved completion blocker.** Repository ColorUtil and its pre-existing tests, verified Phase 1, and 2.1's literal `seriesDash(i)` require 0–5 solid. Verified 8.3-a/b require series 1 dashed; §9 goes further and says every i≥1 non-solid. A plain `i % 4` replacement neither respects the current contract nor preserves 24 unique color/dash pairs (6 and 4 have an LCM of 12). With nodes disabled and the first six curves solid, non-color differentiation is also absent; even nodes on repeat shapes at 0 and 5. This is a real accessibility acceptance conflict, not just a test typo.

Authority order means **retain the existing ColorUtil contract as the baseline implementation**, use it directly, and record original 8.3-a/b as blocked/failing until reconciled. Do not remap i to 6*i in Plot2DPanel, test only series 6 and call it series 1, or add a second dash catalog in the client. Additional series-6 continuity tests provide useful evidence but do not pass 8.3-a/b.

Recommended reconciliation before final implementation acceptance: the requirement owner explicitly chooses a revised **shared** dash contract that makes early curves distinguishable, including line-only plots, and reconciles the Phase 1 24-pair invariant and §9's “every i≥1” language. Record an explicit per-index table over at least 0–29, wrap rules, grayscale expectations, and revised util assertions; then implement that one rule in ColorUtil and rerun Phase 1. This is a conditional expansion to the two util files and verification document, not authorization to overwrite existing Phase 1 work. An alternative is to revise 8.3-a/b to exercise series 6 while retaining the current helper; that alone **does not** resolve the non-color differentiation objective and cannot justify 100%. No evidence can make both original contracts true. No mandatory check should be disabled to hide this conflict.

**C2 — exact 8.8-c pixels vs intentional changes.** Larger/different markers and new histogram outlines necessarily alter geometry. Connected Path2D joins also differ from independently capped segments even with the old solid stroke. Preserve data coordinates, step legs, axes and clip; keep the original solid stroke when variation is off. Use nodes-off straight-line fixtures for literal pixel comparison and separate geometry/marker/histogram tests. Record and obtain acceptance of narrowly described intentional raster differences in 8.8-c before completing it; never pretend every channel-only comparison passed. Reverting to per-segment drawing is not an acceptable way to meet this gate.

**C3 — “userDefinedColors != null wins” vs short arrays.** Actual code only overrides when `length > visiblePlotIndex`; missing entries fall through to auto or black. Preserve this behavior, including explicit entries when autoColor is false. The fallback auto branch should use seriesColor even for a short custom array, enabling 8.4-b. Do not introduce null paints or new validation of legacy arrays in this task.

**C4 — invalid data vs gap rendering.** Preserve the existing global invalid-visible-data warning. Do not turn NaN into a new supported partial-data plotting feature. Defensive mapped-point path handling must still split at non-finite points (e.g. arithmetic overflow), rather than bridge across them. That defensive behavior is tested separately from the public invalid-data warning.

**C5 — 21 consumers and icon behavior.** Treat 21 as the source document's historical reach estimate, not an exact current census. The constructor inventory in §15 is reproducible and includes additional vmicro uses. Icons should always be capable of drawing markers; actual legend/list markers reflect `showNodes` and DRAWPOINT. Point-only data must not falsely be advertised as a line; use the small extensions described in §7.

## 4. Current Architecture and Data/Style Flow

`PlotData` → `Plot2D` (names, model-order data/hints, visibility) → `Plot2DPanel.paintComponent` (visible-index paint, model-index geometry) → `drawLinePlot` / `drawHistogram`. `nodes[modelIndex]` is also read by `pointerMoved`; do not stop mapping when line hints are off.

`PlotPane` owns a private `getPlot2DPanel1()` and binds its plot to the embedded panel and data table. `updateLabels` calls `updateLegend`; plot replacement and Plot2D ChangeEvents drive the existing updates. In the legend, `plotIndices[i]` is the model index and **i is the correct visible index**. Labels use `plotLabels[i+1]` for SingleXPlot2D and `plotLabels[2*i+1]` otherwise. Preserve metadata, unit labels, HTML text, truncation and visibility.

`Plot2DSettingsPanel` has public `fieldPlot2DSettings` accessors and a private listener-bound `ivjplot2DSettings1`; `connPtoP2` aligns them. It is initially connected into the plot panel's private settings reference. Existing generated `connPtoP*SetSource/SetTarget` methods use aligning booleans and null guards. The plot likewise mirrors settings as observable panel properties. No new configuration store is needed.

`MultisourcePlotPane.setDataSources` → model refresh/sort → auto colors → selection handler → selected PlotData/name/hint/color arrays → `PlotPane.setPlot2D(plot, colorArr)`. The explicit color array intentionally stabilizes colors across selections. A selected row's visible index is its position among **successfully emitted** series, not its list position or unsortedIndex.

## 5. Implementation Strategy

Keep scope inside the existing paths. Add one package-private helper, proposed `vcell-client/src/main/java/cbit/plot/gui/PlotSeriesStyle.java`, for stroke construction and marker painting. This is a new proposed class, not an existing API. Keep ColorUtil the sole palette/dash authority. No separate renderer, model rewrite, settings store or benchmark dependency.

Proposed helper responsibilities:

- `static BasicStroke stroke(int seriesIndex, boolean varyLineStyles)`: use `ColorUtil.seriesDash(seriesIndex)` with the required constructor when enabled; share the exact old `new BasicStroke(1.5f)` equivalent when disabled. Obtain once per curve, never per point. Initially favor simple per-curve construction; only cache bounded immutable strokes if measured worthwhile, avoiding duplicated dash-index arithmetic.
- A marker painter taking Graphics2D, index, center and size, using five private reusable centered shapes or one reusable mutable shape per curve. Use 6 px line nodes and 8 px histogram point-only nodes to avoid shrinking the latter. Circle/rectangle/triangle/diamond are filled; cross is two solid stroked arms, not fill of an open zero-area path. Use a dedicated solid ~1.5 px stroke for cross, regardless of line dashes.
- Private static marker templates must never escape for caller mutation. Translate and undo on the curve's graphics, or reuse mutable shape coordinates; do not create Graphics2D/Path2D/AffineTransform per node. Marker painter must not leak paint/stroke/transform state.

Use a child Graphics2D once around panel plot rendering or each series, disposed in finally. Preserve axes/crosshair strokes and AA. For icons use a child graphics per icon. No global render-hint changes. Remove `autoContrastColors` from Plot2DPanel only after its last use is replaced; retain multisource color arrays because they also hold custom colors.

## 6. Phase 2.1 — Plot2DPanel

### Color, style and indexes

Change `getVisiblePlotPaint(int visiblePlotIndex)` only in its fallback automatic branch to `ColorUtil.seriesColor(visiblePlotIndex)`. Keep the bounded custom array check and black fallback. Remove obsolete auto palette cache and generator reference. No change to `ColorUtil.generateAutoColor` anywhere else.

Add public `BasicStroke getVisiblePlotStroke(int visiblePlotIndex)` (public matches the existing getVisiblePlotPaint API and permits external style consumers) delegating to PlotSeriesStyle with `getVaryLineStyles()`. Add the panel property and binding described in §8. Solid-off must equal `lineBS_15`, including square caps/miter join. Enabled stroke: width 1.5f, CAP_BUTT, JOIN_ROUND, miter 10f, ColorUtil dash array, phase 0f. Series 0 solid under the current helper. Do not mutate axis/crosshair stroke constants.

In `paintComponent`, capture visible index before incrementing. Pass **both** model index and visible index to private drawing methods, or pass resolved stroke/marker index explicitly. Data, nodes and renderHints always use model index; stroke and marker use visible index. Never use hidden-series model positions as the style index. Advance visible index once per visible series only.

### Continuous path algorithm

1. Preserve null-plot, invalid-data and invalid-range early exits. Handle empty and singleton data without dereferencing a null segments array.
2. Map once into `nodes[modelIndex]`, even for point-only hints. Use the mapped array to build path and paint markers.
3. For DRAWLINE, allocate one `Path2D.Double`, optionally pre-sized to n (2n in step mode). Traverse mapped coordinates in original order; do not sort or decimate.
4. On the first finite point of a run, `moveTo(x,y)`. For each subsequent finite point: ordinary mode `lineTo(x,y)`; step mode `lineTo(nextX, previousY)` then `lineTo(nextX,nextY)` exactly matching the old horizontal/vertical legs. Do not close the path. Repeated x values, backward trajectories and sharp finite jumps are valid data, not inferred gaps.
5. If either mapped coordinate is non-finite, end the current run and start the next finite point with moveTo. Never lineTo an invalid coordinate. Each PlotData starts a new path. There is no other confirmed discontinuity signal to invent.
6. Keep the full path, including offscreen vertices. Optional culling computes an **any segment might intersect** boolean, with a reusable Line2D or rectangle intersection call for each original/step leg. Expand the guard rectangle by stroke half-width to avoid rejecting edge-touching visible strokes. Once true, stop doing culling tests, but continue appending all vertices. Do not clip individual segments, discard offscreen legs, or restart at re-entry.
7. Draw the entire path once if there is a drawable segment and it might intersect. The existing plot rectangle clips the result. Multiple subpaths representing real gaps are allowed within the single draw; a dash restart at a true discontinuity is intentional. Phase must not restart at an ordinary vertex or clip boundary.
8. Draw markers separately when showNodes and DRAWPOINT permit; skip non-finite centers and cull marker bounds against the clip. Preserve original sample count/locations; step elbow points are not nodes.

`getLinePlotSegments` is private and only used for painting; replace/remove it as part of this path conversion. **Do not remove `getLinePlot`**: mouse click selection uses that existing GeneralPath separately. It currently draws straight segments even in step mode; preserve that pre-existing hit-test behavior in this phase instead of silently broadening scope. Dash gaps must not make selection impossible. Test selection before/after.

Retain plotRectHolder dimensions and compact-mode offsets. The plot clip should be intersected with the incoming graphics clip on a child graphics so partial repaint/printing clips are respected. Full-plot rendering remains unchanged. Restore/dispose child state before returning. Guard empty data without masking exceptions in tests: paintComponent catches Throwable, so assertions must verify expected output rather than only “did not throw.”

### Histogram

Resolve the same series stroke once at drawHistogram entry. After each existing `g.fill(rectangle)`, call `g.draw(rectangle)` with that stroke and the existing series paint. Preserve baseline y=0 transform, width normalization, bar ordering and negative-height behavior; do not turn this into a histogram correctness rewrite. Gray nodes remain gray but use the shared marker geometry and appropriate visible index; save/restore paint and solid marker stroke. Keep ordinary and point-only marker sizes at least 6 px, preserving the existing 8 px histogram-only size.

Same-color fill can obscure inward portions of a dashed outline. Validate the outer boundary separately with a sufficiently large bar and record any residual inability to identify filled bars by pattern; do not claim that merely setting a dashed stroke proves perceptual distinction. No hatch/fill redesign is authorized without further scope reconciliation.

## 7. Phase 2.2 — PlotPane Legend / LineIcon

Change the existing inner icon to `public static class LineIcon implements Icon` with public `LineIcon(Paint, Stroke, int markerIndex)` storing immutable inputs. No external caller of the old private constructor exists. Basic behavior: 50×12 or slightly taller if needed for AA padding, line centered at y+height/2 with horizontal inset, centered 6 px marker from PlotSeriesStyle. Paint in a child Graphics2D; use the given stroke for the sample and solid stroke for cross; do not clip its ends at x+width.

Add a small overload accepting `boolean drawLine, boolean drawMarker` for actual render-hint/node parity; the required three-argument constructor defaults both true. When nodes are off or DRAWPOINT absent, no marker; point-only series have a marker but no invented line. Histogram legend shows the series stroke and optional marker, with its gray marker paint handled explicitly if representing histogram node paint. Supply an internal overload for marker paint and size so the histogram point-only 8 px marker is represented without changing the required three-argument constructor; ordinary marker size is 6 px. Keep actual series line paint unchanged.

Recommended narrow facade on PlotPane: `public Icon createVisiblePlotIcon(int visibleIndex)` resolves paint/stroke/model render hints/node state once and builds this icon. Use it inside updateLegend and from the multisource selected-row renderer. For unselected preview rows, provide `public Icon createSeriesStyleIcon(Paint paint, int prospectiveVisibleIndex, int renderHints)` using the same embedded panel's setting and helper; this avoids exposing the entire private panel or duplicating stroke generation. Document that it is a preview, not an existing series.

Register a single listener on the owned Plot2DPanel during initConnections for `varyLineStyles`, `showNodes`, `autoColor`, `userDefinedColors` and histogram-mode changes where needed. Rebuild only legend icons (or call the existing bounded updateLegend) and revalidate/repaint the legend when relevant. Forward a PlotPane `seriesStyle` change notification so the multisource list repaints. Ensure color setter and histogram setter emit change events/repaint as needed; suppress no-op events. On plot/model visibility changes keep the existing updateLabels route and send the same style notification after updates. Do not introduce duplicate listener registrations on every legend rebuild.

Legend click currently passes decorated/truncated HTML label text to `setCurrentPlot`, which expects the raw plot name. This is a pre-existing mismatch relevant to testing current-series selection. Small targeted repair recommended: store the raw `plot.getPlotNames()[plotIndices[i]]` as a client property on the text label and use it in the listener. Keep displayed text/tooltips and click behavior intent unchanged. Verify with long names and units; do not parse HTML to recover identity.

## 8. Phase 2.3 — Vary Line Styles Setting

Bean: `vcell-core/src/main/java/cbit/plot/Plot2DSettings.java`.

- Add `private boolean fieldVaryLineStyles = true`.
- Add `public boolean getVaryLineStyles()` and `public void setVaryLineStyles(boolean value)`.
- Setter stores old/new and calls existing `firePropertyChange("varyLineStyles", old, value)` boolean overload. PropertyChangeSupport suppresses equal values. Follow string property-name convention; do not redesign all constants.
- Add this property to both saveSettings and restoreSavedSettings, restoring through setter to update all UI listeners.
- No reset-to-factory API exists. New instances default on; snapshot Cancel restores the prior value. No Java serialization migration is needed because the bean is not Serializable. No persisted old instances were found; do not invent XML/preference formats or serialVersionUID. If a future external bean decoder omits the property, the no-arg default remains true.

Panel: `Plot2DSettingsPanel`.

- Add `ivjJCheckBoxVaryLineStyles`, lazy `getJCheckBoxVaryLineStyles()`, name `JCheckBoxVaryLineStyles`, text exactly `Vary line styles`, selected true. Tooltip should explain solid curves for narrow spikes.
- Existing nodes/crosshair/snap occupy grid rows 7/8/9; insert at row 8, move crosshair/snap to 9/10 and adjust final insets/preferred sizing so nothing overlaps or clips. Keep full-width alignment with these controls.
- Add ItemListener dispatch to a new guarded UI→bean connector and bean `varyLineStyles` property dispatch to its bean→UI counterpart. Register in initConnections; perform initial sync and replacement-bean sync in `setplot2DSettings1`. Detach old bean through existing lifecycle. Setting false before binding must not be overwritten by checkbox default true.
- Do not couple enablement to crosshair, nodes or snap. The toggle works with all those off.

Plot2DPanel: mirror `fieldVaryLineStyles = true`, get/set accessor pair and `varyLineStyles` property event. Add source/target connectors with one aligning flag, dispatch from both bean and panel, initial alignment in initConnections and replacement alignment in setplot2DSettings1. Match the existing settings-panel-to-bean initialization order; do not instantiate a second bean in the plot panel. Setter repaints only on an effective change; legend listener handles legend repaint. No axis recomputation or PlotData reconstruction is required. Cancel must restore strokes, plot, legend and multisource icon previews immediately.

## 9. Phase 2.4 — Pointer Status Series Name

Current `pointerMoved` uses `getCurrentPlotIndex()` (a **model** index), searches mapped x coordinates, chooses nearest x sample, and displays `snf.format(independent[i]) + ", " + snf.format(dependent[i])`. It displays these sample coordinates even when snap is off; preserve that behavior. Crosshair still follows mouse when unsnapped. Do not change to interpolated or transformed values.

Prefix only this successful status assignment with `name + ": "`, where name is `getPlot2D().getPlotNames()[modelIndex]`, bounds/null checked. Use raw full name, not legend HTML or a visible-index lookup. If name null/empty/blank, keep the original numeric suffix without a dangling colon; if no valid plot/sample, keep the existing single-space status. No resource-bundle localization convention was found in these strings; use the simple delimiter consistently.

Guard empty data/uninitialized mapped nodes before indexing. Preserve crosshair disabled early return and XOR drawing. `getGraphics()` can be null on a headless/unrealized panel: compute status independently, then draw XOR crosshair only when a graphics context exists, disposing it after use. This small separation enables the synthetic-event test without making pointerMoved public. Alternatively a BufferedImage-backed test subclass can supply getGraphics; the public behavior should still tolerate an unrealized panel. Avoid changing snap search algorithm or optimizing its existing O(n) scan in this task.

Selection sources are initial first-visible selection, next/previous plot keyboard actions, curve clicking and legend clicking. Exercise each important path; visibility changes reset current selection through updateVisiblePlots as before. Histogram uses the same model-series name and numeric suffix.

## 10. Phase 2.5 — MultisourcePlotPane

Keep stable **color identity** via the current unsorted index. In createAutoContrastColors replace generator with an array filled by `ColorUtil.seriesColor(u)` for u=0..size-1. Preserve grow/reuse and explicit `setDataSources(dataSources,colorArray)` semantics; custom color arrays remain authoritative and retain their existing length validation. Do not rename/getAutoContrastColorsInListOrder or change its clone behavior in this phase despite the misleading name after sorting.

Keep **stroke/marker identity** as the main plot's dense selected visible index. Changing the core to global multisource style IDs would violate the simple visible-index contract and unnecessarily add model state. Colors therefore can be stable while dash/shape changes on selection; this is intentional existing color precedence plus new visible style rules, not a mapping error.

Build a row→visible-index map in `selectionModel1_ValueChanged` **when a data series is actually appended**, not merely from selection rank. Clear it when data sources/model contents change. Publish it alongside the new plot and request list repaint. Use DataReference identity/source+identifier or sorted row indexes rebuilt on every refresh; never key only by identifier across multiple sources. Listen to list model content changes so comparator/group changes cannot leave stale mappings. Keep ignoring valueIsAdjusting; rebuild once selection settles. No MultisourcePlotListModel production change is required if its existing list events are used.

Renderer changes, after calling DefaultListCellRenderer:

1. Reset icon and tooltip unconditionally, including invalid/-1 indexes and empty rows. Retain existing text, selection foreground/background, focus border and matched-set alternating background logic.
2. Resolve unsortedIndex through `getSortedDataReferences()`; resolve effective color from autoContrastColors (which can be custom). Guard initialization/replacement states without throwing from rendering.
3. For a plotted selected row, use `getplotPane().createVisiblePlotIcon(mappedVisibleIndex)` so the icon is sourced from the **actual** plot style, hints and settings.
4. For an unselected row, show a prospective icon via createSeriesStyleIcon using its stable color, source render hints, and insertion rank among currently selected rows. Compute prospective insertion rank from successfully emitted rows preceding this row, excluding selected rows that cannot emit a series. Set tooltip explicitly to “Not plotted; style shown for selection.” It is not a claim that a hidden curve exists. When selected, recompute all affected row icons and legend, since subsequent visible indexes shift. If a row cannot emit a plot, clear its icon rather than inventing a current-series identity.
5. Subscribe once to the owned PlotPane's seriesStyle notifications and repaint the list on toggle, Cancel, node changes and plot replacement. Do not create a second settings bean or persistent style preference.

The package already depends on `cbit.plot.gui.PlotPane`; reusing its icon facade introduces no new reverse dependency. The helper remains package-private in plot.gui. Prevent event-order stale colors: after explicit colorArray assignment, refresh an existing selected plot and repaint; on source replacement initialize the color array and mapping before final selection rebuild. Avoid recursive selection rebuilds by using a small updating flag and one final refresh if the model fires intermediate events.

## 11. Shared Style Synchronization

| Representation | Color | Stroke | Marker |
|---|---|---|---|
| Ordinary plot visible i | panel getVisiblePlotPaint(i) | panel getVisiblePlotStroke(i) | shared geometry i%5, shown per hints/nodes |
| Histogram visible i | same fill/outline paint | same panel API | same geometry; existing gray node paint retained |
| Legend i | actual panel paint | actual panel stroke | same helper/size/visibility/hints |
| Multisource selected row | actual selected plot paint (unsorted custom color mapping already applied) | actual visible-index stroke | actual visible-index shape |
| Multisource unselected row | its unsorted identity color | explicitly prospective insertion style | prospective shape under current node setting |

There must be one dash catalog (ColorUtil), one stroke constructor helper, one marker implementation, and one icon renderer. Node cross strokes are deliberately solid for legibility; they are not an alternative dash catalog. Icons must not recompute colors from visible index when explicit multisource/user colors exist.

Default-on settings, markers and correctly paired legends support color-plus-pattern identification. They do not prove WCAG compliance by themselves; C1 and the finite cycle's collisions must be resolved honestly. Test early series, repeated colors at 6+, grayscale, overlapping curves and nodes-off views. Do not promise unique non-color identities for an unlimited number of simultaneous series. Disabling variation is an explicit scientist preference, not the startup state.

## 12. File-by-File Change Plan

All paths below are proposed later execution scope; only this plan is changed now. Sections referenced supply exact algorithms, events and test responsibilities.

| File / class | Current → required; symbols / dependencies | Compatibility, risk and validation |
|---|---|---|
| `vcell-client/src/main/java/cbit/plot/gui/PlotSeriesStyle.java` (new) | Shared stroke and marker functions described §5; ColorUtil is sole dash source | No public model API; no per-node allocation; geometry/cross/state tests in PlotSeriesStyleTest |
| `vcell-core/src/main/java/cbit/plot/Plot2DSettings.java` | New default-on property, save/restore, PropertyChangeSupport (§8) | Additive bean API, no serialization contract; Plot2DSettingsTest |
| `vcell-client/src/main/java/cbit/plot/gui/Plot2DPanel.java` | getVisiblePlotPaint/Stroke, drawLinePlot/Histogram, paintComponent index flow, settings connectors, pointerMoved, style setter events (§6/8/9) | Preserve model indexes, ranges, step legs, selection helper and old solid stroke; panel rendering/settings/pointer tests |
| `vcell-client/src/main/java/cbit/plot/gui/Plot2DSettingsPanel.java` | Checkbox, grid placement, listeners, guarded connectors, initialization/rebinding (§8) | Cancel and programmatic changes update all views; no broad generated-code cleanup; settings UI tests |
| `vcell-client/src/main/java/cbit/plot/gui/PlotPane.java` | Static stroke+marker LineIcon, icon facades, updateLegend and style forwarding; raw legend click identity (§7) | Preserve existing labels, units, table, compact and legend hiding behavior; icon/state/click/filter tests |
| `vcell-client/src/main/java/cbit/vcell/modelopt/gui/MultisourcePlotPane.java` | Palette loop, explicit row mapping, renderer icons, data/selection/style event refresh (§10) | Preserve custom arrays/sorting/group colors; prevent reused-component leakage; multisource integration tests |
| `vcell-core/src/test/java/cbit/plot/Plot2DSettingsTest.java` (new) | Bean default, event and snapshot contract | JUnit Jupiter Fast; no Swing or dependencies added |
| `vcell-client/src/test/java/cbit/plot/gui/PlotSeriesStyleTest.java` (new) | Primitive strokes, five markers and state isolation | Fast, headless; helper package access, tolerant raster assertions |
| `vcell-client/src/test/java/cbit/plot/gui/Plot2DPanelAccessibilityTest.java` (new) | Color, continuity, step, gap, clipping, nodes, histogram, pointer, 8.8 fixtures | Fast, EDT, BufferedImage; §13 distinguishes original blocked acceptance from supplementary tests |
| `vcell-client/src/test/java/cbit/plot/gui/PlotPaneAccessibilityTest.java` (new) | 8.3-c, style update, filtering, point-only and full-name click | Fast, EDT; no top-level frames |
| `vcell-client/src/test/java/cbit/plot/gui/Plot2DSettingsPanelTest.java` (new) | Two-way binding, replacement, no-op and Cancel propagation | Fast, EDT; component lookup by checkbox name |
| `vcell-client/src/test/java/cbit/vcell/modelopt/gui/MultisourcePlotPaneAccessibilityTest.java` (new) | Sorted/subset/custom colors and actual icon/plot equivalence | Fast, EDT; realistic small DataSource fixtures |
| `.agents/uconn-color-blind-accessibility-verified.md` | Reconcile C1/C2 explicitly; later append dated Result evidence and mark only actually completed items (§19) | No change now. Never claim global multi-phase checks completed from legacy-only evidence |
| Conditional only: `vcell-util/src/main/java/org/vcell/util/ColorUtil.java` and `vcell-util/src/test/java/org/vcell/util/ColorAccessibilityTest.java` | A reconciled shared dash contract if requirement owner selects that resolution of C1 | Preserve pre-existing Phase 1 additions/palette tests and generator; update documented assertions together, then run util suite |

No POM, PlotData, Plot2D, MultisourcePlotListModel, solver generator, Langevin renderer, export-server, localization or persistence change is planned. A lightweight diagnostic can run from test code locally without committing another benchmark class; record its full invocation/fixture in evidence.

## 13. Autolan

### Automated test plan and conventions

JUnit Jupiter 5.10.1 is managed at root; client/core/util depend on Jupiter. Surefire 3.1.2 runs unit tests, Fast is a JUnit tag. Root Java source/target is 17, client compiler override is 16; use a compatible repository JDK (17 supports both), not newer-only client syntax. Existing patterns: `SpringSaladSpeciesLegendTest` and `SpringSaladViewerRenderTest` use headless BufferedImage and content assertions; `ChildWindowDetachTest` and `VCellThreadCheckerTest` use SwingUtilities.invokeAndWait. The detach test intentionally skips headless; the new plot tests must **not** do so.

All new classes use `@Tag("Fast")`. Construct, size, lay out, mutate and paint Swing components on EDT via invokeAndWait, propagating assertion failures. No JFrame, Robot, display server, solver/server connection or screenshot baseline is needed for these unit tests. Plain style/bean tests need no EDT except icon painting with Swing component setup. Use direct properties, component-tree lookup and small package-private seams where needed; do not make private UI internals public solely for tests. For private connector snapshot testing, limited test reflection is preferable to a new public configuration API.

### Concrete test cases

| Class / proposed method | Setup and input | Assertions / regression protected |
|---|---|---|
| Plot2DSettingsTest.defaultsAndEvents | New bean; attach listener; false, false, true | Default true; exactly two varyLineStyles events with correct old/new values, no unrelated events |
| Plot2DSettingsTest.snapshotRestoresVariation | Save false/true in separate cases, mutate all relevant controls, restore; restore before save | Restores saved toggle via event, other settings preserved; no-save restore is no-op |
| PlotSeriesStyleTest.strokeContract | i=0..29 and negative index; variation on/off | Exact ColorUtil dash equality (null-safe), width 1.5, butt/round/10/0 on; `new BasicStroke(1.5f)` equality off; index 0 solid, valid nonempty positive dashes; no per-client remapping |
| PlotSeriesStyleTest.markerCycleAndBounds | Render indices 0..9 at centered 6 px into small ARGB images, cross also under dashed current stroke | 0 equals 5 etc.; distinct five masks/geometry, centered bounding dimensions ≥6 geometrically, cross has solid arms; triangle/diamond not circles; no transform/paint/stroke leakage |
| Plot2DPanelAccessibilityTest.colorPrecedence | Three visible curves; full, short and null user arrays; autoColor true/false; hidden middle model curve | Custom entries unchanged even with auto off; fallback seriesColor or black; visible-index palette mapping, not model-index mapping |
| Plot2DPanelAccessibilityTest.sixCurveDashAcceptance_8_3_a | 400×300, six separated horizontal curves, white background, manual fixed ranges, nodes off, 30 samples | Original contract: series 0 continuous; series 1 recurrent white gaps ≤12 px. **Blocked by C1 with current helper**, retain explicit ledger failure, not a fake passing test |
| Plot2DPanelAccessibilityTest.denseDashAcceptance_8_3_b | Same layout, 5,000 uniformly spaced samples/curve | Same periodic gap assertion; microsegments are much shorter than first dash, so old segment drawing appears solid and must fail; C1 applies |
| Plot2DPanelAccessibilityTest.sharedContractDashContinuity | At least seven separated curves, series 6 dashed with current contract; nodes off; 30 vs 5,000 samples on identical horizontal trajectory | Both have periodic gaps at equivalent positions, series 0 no interior gaps. Supplementary proof only; never relabel as original 8.3-a/b |
| Plot2DPanelAccessibilityTest.clipReentryKeepsPhase | Path starts outside manual range, enters/exits/re-enters, non-integer segment lengths; nodes off | Interior crop agrees with one full reference Path2D clipped to same rectangle; cropped/restarted implementation differs; complete length contributes phase |
| Plot2DPanelAccessibilityTest.stepAndBoundaryGeometry | Finite data with sharp spike, repeated x, backward x, step mode; empty/singleton; finite offscreen curves | Exact step vertices through package-private path-building seam if extracted; no diagonal replacement; singleton draws only node; no exceptions/paint-failure-only blank images; boundary stroke not culled |
| Plot2DPanelAccessibilityTest.invalidDataAndDisconnectedRuns | Visible NaN/infinity fixture, same invalid curve hidden; mapped-point seam with finite/NaN/finite runs | Public warning behavior preserved; hidden invalid data does not disable valid plot; PathIterator has MOVETO at next finite run and no bridge/invalid coordinates. No reinterpretation of user NaN data |
| Plot2DPanelAccessibilityTest.nodeHintsAndHistogram | Five series, hidden model entry, line-only/point-only/both hints; histogram known baseline; nodes on/off | Correct visible marker cycle, ≥6 size, no unwanted points/line, gray histogram nodes retained; fill interior unchanged; exterior bar boundary matches actual series stroke; graphics state restored |
| Plot2DPanelAccessibilityTest.pointerNames_8_3_d | Paint panel once to populate nodes, assign JLabel status, select raw series name, dispatch MOUSE_MOVED to registered listener | Full name + colon + exact snf numeric suffix; hidden-index case correct; snap on/off retains suffix, absent name falls back, empty/no series safely blank; histogram same. No getGraphics null failure |
| Plot2DPanelAccessibilityTest.solidGeometryAndSpike | Styles off, nodes off; same fixed data/manual axes as baseline; one-sample spike on/off | 8.8-c reference comparison where literal geometry equality is applicable; document intentional join deltas (C2). Spike exists at least off, no coordinate/data mutation |
| PlotPaneAccessibilityTest.legendStyle_8_3_c | PlotPane with hidden middle series and enough series to include dashes, layout recursively | Each visible legend icon paint/stroke/marker matches embedded panel at dense i; width≥50/height≥12; icon state preserved; distinct raster samples, line-only/point-only parity |
| PlotPaneAccessibilityTest.legendRefreshAndSelection | Toggle panel/bean, hide/show model, set colors, click long HTML-formatted label, nodes off/on | Immediate icon update without model replacement; correct current model index; unchanged label/unit/tooltip content; no accumulated listeners |
| Plot2DSettingsPanelTest.bindingAndRebinding | Set external bean false before binding; checkbox doClick; bean mutation; replace bean; mutate old bean | UI↔bean sync, replacement uses new bean value, old detached, no loops, default accessible on, crosshair/nodes/snap unaffected |
| Plot2DSettingsPanelTest.cancelRestoresAllStyles | Obtain actual settings through embedded settings panel test seam, save, toggle checkbox, restore | Plot stroke, legend and multisource selected sample all revert immediately; no axis reset |
| MultisourcePlotPaneAccessibilityTest.sortedSubsetStyles | Two sources with deliberately nonalphabetical columns and duplicate identifier across sources; at least 8 entries; default and reversed/group comparator; select noncontiguous rows | Palette by unsortedIndex; selected plot order follows emitted sorted rows; row icon equals actual visible plot icon, including index 6 dashed; no identity collisions |
| MultisourcePlotPaneAccessibilityTest.customColorsAndReuse | Explicit color array, source replacement/grow/shrink, empty selection; reuse renderer across selected/unselected/invalid rows | Custom arrays win; clone getter preserved; correct selected foreground/background and match stripes; no stale icon/tooltip; unselected preview clearly labeled and updates on insertion |
| MultisourcePlotPaneAccessibilityTest.settingsPropagation | Embedded setting/node toggle and restore, unchanged selected plot | List and legend agree immediately, no separate preference; filtered/sorted mapping remains valid |

Pixel tests: sample interior horizontal bands away from axes/ticks/endpoints and marker positions; nodes must be off for gap measurements. Detect painted-vs-background coverage over a small vertical band with tolerance for AA, rather than exact colored RGB equality. Measure distance between background gaps along the interior centerline; do not let label white pixels count as gaps. Compare sparse/dense sampling of the same straight curve. Add deterministic seeded stochastic/step render content tests separately—random walk projections can overlap and legitimately obscure gaps. For reference paths use the same known transform/ranges, stroke and AA settings. Save diagnostic PNGs to test output only on failure, if useful.

The path-building seam, if extracted, should be package-private and used by production rendering, not a separate test-only renderer. Behavioral rendered gaps are the principal proof, not simply counting calls. A test-only Graphics2D forwarding wrapper may count path draws as supplementary performance evidence, excluding axes, marker cross arms and histogram outlines.

### Commands (run during implementation, not during this planning task)

Repository-defined broad gate:

```sh
mvn test -Dgroups=Fast -pl vcell-util,vcell-core,vcell-client -am
```

Focused command assembled from the confirmed reactor modules/Surefire configuration and the proposed test names:

```sh
mvn test -pl vcell-client -am -Dgroups=Fast -Djava.awt.headless=true -Dtest=ColorAccessibilityTest,Plot2DSettingsTest,PlotSeriesStyleTest,Plot2DPanelAccessibilityTest,PlotPaneAccessibilityTest,Plot2DSettingsPanelTest,MultisourcePlotPaneAccessibilityTest -Dsurefire.failIfNoSpecifiedTests=false
mvn test -pl vcell-util -Dgroups=Fast -Dtest=ColorAccessibilityTest
```

The reactor flag avoids stale installed core/util artifacts. `failIfNoSpecifiedTests=false` permits unrelated prerequisite modules without named tests; verify actual Surefire XML contains every expected test and no unintended skip. A successful Maven exit with missing tests is not evidence. Record dependency/build blockers rather than changing POMs to bypass them. Run the broad baseline before implementation and compare after; do not confuse expected pre-existing disabled dark-palette hook with a new Phase 2 skip.

Static gate:

```sh
if rg -n 'generateAutoColor' vcell-client/src/main/java/cbit/plot/gui/Plot2DPanel.java vcell-client/src/main/java/cbit/vcell/modelopt/gui/MultisourcePlotPane.java; then
  echo 'FAIL: legacy automatic-color reference remains'
  exit 1
else
  test "$?" -eq 1
fi
git diff --check
```

No build/test commands were executed as part of planning; doing so would create generated files outside the sole authorized plan path.

## 14. Performance Validation Plan

No suitable legacy plot benchmark or 100,000-point stochastic plotting fixture was confirmed from repository inspection. `VtuGridParserTest` contains timing diagnostics for a different subsystem; do not repurpose that infrastructure. Existing debug bridge provides live UI inspection, not a plot benchmark.

Before editing renderer, capture baseline diagnostic results on the same machine/JDK/render pipeline. Use a disposable local diagnostic built from the test fixtures, record its full source/command with the result, and keep it outside committed product code. Run baseline and candidate from separately identified builds, preserving the dirty Phase 1 state (do not reset this checkout). For comparisons that need master, record the actual revision rather than a moving branch name.

Fixture matrix: 1 and 6/7 curves; 5,000 and 100,000 points **per curve**, fixed-seed bounded random walk plus smooth horizontal line and single-sample spike; normal and step mode; styles on/off; nodes on/off; 400×300 and 1200×800; full range and tightly zoomed/offscreen-heavy range. Store seed, data checksum, ranges, plot dimensions and series count. Treat 600,000/700,000 total points as a stress case, distinct from 100,000 total.

Measure after 20 warm-up paints, at least 50 measured paints, three runs: median/p95 wall time using nanoTime, allocation/GC observation (JFR or existing JVM tools if available), retained memory after repeated repaint, and sample draw-count instrumentation. Record total render including mapping, and optionally path-building separately. One curve path draw replaces n−1 or 2(n−1) segment draws; markers still cost O(n). No per-node path or graphics allocation is acceptable without evidence.

Proposed engineering decision threshold, not a repository-established SLA: investigate a repeatable >20% median or p95 regression versus baseline in matched modes; do not accept a major slowdown just because an average passes. Document hardware and noise. Default accessible mode and nodes-on mode need their own absolute timings and reviewer acceptance even where comparison is intentionally different. Resolve regressions before completion or record a blocker, not a silent waiver.

Live EDT checks: resize repeatedly, zoom/restore, hover snap/non-snap, switch current curve and toggle styles on a 100,000-point stochastic result. Record interaction latency and visible freezes; sample event latency during repaint, not just offscreen render times. Existing pointer search is O(n); Phase 2 must not add style allocations to it. No permanent flaky time assertion in Fast tests. No data decimation introduced to manufacture a performance pass.

## 15. Manual QA / 8.7 Plan

### Confirmed consumer inventory and workflow targets

Constructor search used `new (cbit.plot.gui.)?(PlotPane|Plot2DPanel|MultisourcePlotPane|TimeFunctionPanel)` throughout Java sources, plus broader reference searches. These are confirmed class-level uses, not a fabricated count of 21 reachable screens:

- `cbit.vcell.client.data`: ODEDataViewer, PDEDataViewer (multiple creation sites), DataProcessingResultsPanel, ODETimePlotMultipleScansPanel, KymographPanel (line scan and time series).
- `cbit.vcell.simdata.gui.PdeTimePlotMultipleVariablesPanel`.
- `cbit.vcell.client.bionetgen.BNGDataPlotPanel`.
- `org.vcell.optimization.gui`: ConfidenceIntervalPlotPanel, ParameterEstimationRunTaskPanel (direct panel and multisource), ReferenceDataPanel. ProfileDataPlotPanel wraps ConfidenceIntervalPlotPanel.
- `cbit.vcell.microscopy.gui.FRAPDataPanel`.
- `cbit.vcell.microscopy.gui.estparamwizard`: EstParams_OneDiffComponentPanel, EstParams_TwoDiffComponentPanel, EstParams_ReactionOffRatePanel, EstParams_ReacBindingPanel, SubPlotPanel via MultisourcePlotPane.
- `cbit.vcell.mapping.gui.ElectricalStimulusPanel` via `cbit.plot.gui.TimeFunctionPanel`.
- Outside client, `vcell-vmicro`: `org.vcell.vmicro.workflow.gui.OptModelParamPanel`, `org.vcell.vmicro.op.display.DisplayPlotOp`, `DisplayImageOp`.
- `FRAPEstimationPanel_NotUsed` also constructs a pane, but its reachability is **not confirmed**; do not label it exercised production UI. PlotPane/Plot2DPanel demo mains are not extra production consumers. Langevin AbstractPlotPanel references are a separate framework.

Verify reachable workflows using existing local example/results data: ODE results multiple species and scan results; stochastic results step toggle and multi-trial histogram (`PlotPane.setStepViewVisible`); PDE time traces and multiple-variable time plots; kymograph line/time profiles; parameter estimation measured-point vs predicted-line overlay with grouping and custom colors; BNG result plot; FRAP fit/profile plots; electrical stimulus time-function preview. Exact data/model availability for every workflow is **not confirmed from repository inspection**; the implementation operator must record chosen model/result IDs and obtain missing local fixtures. Do not claim these workflows have run.

Debug-bridge commands are documented, not executed now:

```sh
mvn compile -pl vcell-client -am -DskipTests
tools/debug-bridge/launch-client.sh
tools/debug-bridge/scenarios/smoke.sh
tools/debug-bridge/bridge.sh menus
```

Use bridge tree/find/shot and existing tutorial scenario documentation to capture reproducible navigation and screenshots. Launcher expects configured install4j JRE/native libs and defaults to a dev server; authenticate/load approved existing data as needed. Do not manufacture Windows/Linux launch commands from the macOS setup. Missing OS/reviewer/server access is an outstanding manual gate, not an automated pass.

### Checklist each reviewer records per OS and representative workflow

- [ ] Two reviewers identified; CVD experience or simulator identified; OS, JDK, display scale and build revision recorded.
- [ ] Six early series plus ≥7/12 series: identify every visible curve from legend in normal, protan, deutan, tritan and grayscale captures; explicitly test same/similar custom colors. C1 must be resolved first.
- [ ] Nodes on: all five shapes visible and centered, crossings and point-only observations legible; nodes off: styles still communicate the accepted non-color identity contract.
- [ ] Default Vary line styles ON; OFF gives intended solid lines; repeated toggle and dialog Cancel update plot, legend and multisource list immediately.
- [ ] A one-sample spike remains visible at least with toggle off; repeat in step mode, zoomed and resized; document the control for scientists.
- [ ] Auto-color, auto off/black, full/partial custom color arrays and repeated palette cycles behave as tested.
- [ ] Hide/show and reorder/select multisource rows, including equal names from different sources: list/icon/legend matches actual plot; no old icons leak into other rows.
- [ ] Selected/unselected list text, focus borders and grouping backgrounds remain legible; test black series on selected backgrounds and native high-contrast modes.
- [ ] Histogram fill/baseline unchanged, new outline/markers visible, no outlines or gray nodes leak to the next curve. Check large/small/zero and existing negative-value behavior.
- [ ] Pointer shows full current series name and original numeric x,y values; snap, free pointer, crosshair off, curve click, legend click, next/previous selection and hidden selection behave correctly.
- [ ] Axes, tick labels, manual ranges, compact mode, legend hide/show, data table values and label/unit text preserved.
- [ ] Resize/partial repaint/window expose causes no clipped icons, phase jumps from segment restart or residual XOR artifacts; markers stay in plot clip.
- [ ] Large stochastic run remains responsive per §14. Dense markers may obscure dashes; toggle nodes off to inspect continuity and document that distinction.
- [ ] Capture image/printAll from the same Swing component at ordinary and scaled size; compare geometry. Dedicated legacy plot print/export command was **not confirmed** in PlotPane/Plot2DPanel. Do not conflate this with PDE server image/movie export, which is outside Phase 2.
- [ ] All checklist answers yes and screenshots/evidence attached to the implementation PR for macOS, Windows and Linux (8.7-a).
- [ ] Windows High Contrast changed-panel smoke logged with defects routed to #1604; informational 8.7-b is logged even if imperfect.

Keep the Phase 2 ODE/PDE filtered-image evidence linked to 8.6-b without checking the entire six-scenario global item. Record failures in legend matching, including pattern collisions, as real accessibility findings, not as a simulator limitation by default.

## 16. Regression and Compatibility Analysis

Preserve existing public constructors and methods; additions are additive except internal LineIcon construction, whose only caller is local. Leave Plot2D/PlotData formats and numeric arrays unchanged. No database, VCML or serialized settings schema changes. Avoid normalizing line endings across the old CRLF files when implementing.

Keep visibility as the source of dense color/style order. Custom palettes continue to override; background-aware automatic palette generation is intentionally replaced by a light-background palette. Custom dark backgrounds have no validated CVD contract here; record their rendering in QA without claiming dark-mode compliance. Preserve 1.0/2.0 axis/crosshair strokes and XOR handling, point-only hints and all step elbows. Keep scientific coordinate mapping, ranges, selection and data-table content unchanged.

Connected joins, larger markers, prefix text, expanded icons and added histogram outlines are intentional changes; document them so raster differences are not dismissed indiscriminately. Data invalidity behavior remains a warning, not partial interpolation. Do not connect across non-finite mapped values or infer gaps at finite spikes. Preserve unchanged solver-input/FRAP calls to generateAutoColor; util diff should demonstrate no generator algorithm change.

Printing through Swing shares the renderer; child graphics protects the caller's transform/clip. No dedicated legacy export pipeline was found to change. Keep other framework tests outside this scope except where a revised shared ColorUtil contract explicitly requires reevaluation.

## 17. Risks and Mitigations

| Risk | Mitigation / gate |
|---|---|
| Contradictory dash requirements and first-six color-only plots | C1 explicit blocker; one reconciled shared contract and real filtered-image matching before 100% |
| 8.8-c incompatible literal pixel requirement | C2 recorded scope/intent decision, baseline fixtures and separate geometry proof; no false pass |
| Dense nodes hide dash gaps / dashes hide spikes | Separate nodes-off gap tests; default markers, solid opt-out, spike QA |
| Dash resets at clipping or step elbows | Full unclipped logical path, one draw, clip/reentry and step tests |
| Invisible stroke at histogram fill edge | Compare external boundary and reviewer matching; do not count state-setting as sufficient evidence |
| Wrong model/visible/list identity | Explicit dual-index method parameters and emitted-series map; filtered/sorted duplicate-name tests |
| Settings drift / Cancel restores only plot | Three-layer bean binding plus seriesStyle notifications; end-to-end restore tests |
| Renderer state or Graphics2D leakage | Reset icon/tooltip, preserve selection colors, child graphics and state assertions |
| 100k allocation/EDT regression | O(n) path, no segment arrays/per-node graphics, reproducible matched measurements |
| Headless “pass” despite swallowed paint exception | Assert actual raster/content and expected node/status results; no “does not throw” only tests |
| Existing user work overwritten | Check status/diff at execution start; preserve Phase 1 files and unrelated changes; no reset/stash/clean |
| Three-OS/reviewer/fixture availability | Explicit manual gate, dated evidence; blocked remains blocked |

## 18. Recommended Implementation Sequence

1. Recheck working tree and source baseline; preserve existing edits. Resolve C1/C2 acceptance decisions first if the execution objective is unconditional 100%; independent work can proceed while decisions remain open.
2. Capture baseline rendering/performance before changing code. Run existing util/broad Fast baseline and record skips/failures.
3. If agreed, reconcile the shared ColorUtil contract and its pre-existing tests/documentation as one distinct prerequisite; no client-only workaround.
4. Add PlotSeriesStyle and primitive tests; add bean property/snapshot tests. These are prerequisites for the rendering and UI layers.
5. Wire Plot2DPanel property, color/stroke APIs and explicit indexes; implement continuous ordinary/step paths, markers and histogram outlines with scoped graphics; add rendering tests.
6. Wire settings checkbox and full two-way initialization/rebinding/Cancel flow; validate live panel behavior.
7. Upgrade LineIcon, icon facades, legend events and raw-name selection; verify legend style and render-hint parity.
8. Add pointer-name prefix with safe headless/empty handling; dispatch real synthetic mouse events in tests.
9. Implement multisource palette, emitted-series mapping, renderer and event propagation; verify sorted subsets/custom colors.
10. Run focused and broad Fast gates, static check and diff review; fix new failures. Execute baseline geometry and spike comparisons with the reconciled C2 evidence standard.
11. Run measured performance and live responsiveness matrix; fix regressions, then complete two-reviewer three-OS 8.7 and filtered-image legacy scenarios.
12. Reconcile requirement ledger with exact evidence, record remaining blockers truthfully, and update verification completion only when §19 gates are all met.

## 19. Phase 2 Completion and Verification-Document Update

Future execution only: edit `.agents/uconn-color-blind-accessibility-verified.md` after implementation and validation. Follow its existing syntax: `— ✅ done (YYYY-MM-DD)` on numbered items; `- **Result:**` paragraphs with real commands/counts; a phase header may use `— ✅ COMPLETE (YYYY-MM-DD)` as Phase 0 does. Add explicit “Phase 2: 100% complete” only when every gate below is satisfied. Never substitute planning completion for product completion.

Maintain an evidence table with columns: requirement/test ID, exact acceptance version (including explicit C1/C2 resolution), revision/build/environment, command or manual procedure, observed result, artifact/PR link, reviewer/date, status. Required rows:

- 2.1, 2.2, 2.3, 2.4, 2.5 implemented and individually reviewed.
- 8.3-a/b/c/d passed under the explicitly reconciled authoritative contract; original contradictions and their resolution retained in the record.
- 8.4-b zero matches; 8.1 no new failures; relevant 8.2-a shared palette tests rerun.
- 8.8-c geometry and intentional-difference evidence, 8.8-e spike/toggle proof.
- Required large-data performance completed and acceptable.
- 8.7-a two reviewers × three OSes, every checklist item yes, screenshots attached; 8.7-b logged.
- Phase 2 portion of 8.6-b passed; no unresolved Phase 2 accessibility or regression blocker.

Mark §7 items 2.1–2.5 done, phase header complete, and §12 execution checklist items 5–8 done with evidence links. Update relevant §9 checkboxes only when their **entire** assertion passes: legacy legend/dash items can be completed after reconciliation; combined checks also naming Langevin, 8.4-a, all six screenshot scenarios, unrelated scientific regressions or full merge queue must remain open until those separate requirements pass. Preserve all Phase 1 working-tree evidence and its committed/uncommitted distinction.

A FAIL, SKIPPED, BLOCKED or NOT RUN mandatory requirement means Phase 2 is not 100%. Preserve partial completed items and remaining blockers, do not fabricate screenshots or reviewer approval. C1 cannot be erased by changing tests without an explicit acceptance-contract decision. C2 cannot be erased by calling geometry differences “only color.” No issue closure, external message, commit, push or deployment is implied by this plan.

## 20. Final Execution Checklist

- [ ] Working-tree baseline captured and pre-existing Phase 1 changes preserved.
- [ ] C1 dash/accessibility contract and C2 raster acceptance reconciled explicitly; no silent client remapping.
- [ ] Shared palette fallback preserves user colors, short-array fallback and auto-off black.
- [ ] Shared enabled stroke uses required parameters; disabled stroke preserves old solid semantics; series 0 solid.
- [ ] One complete path draw per curve, full offscreen geometry retained, step legs correct, no invalid bridging.
- [ ] Five centered marker shapes ≥6 px, solid cross, hints/node visibility and histogram gray nodes preserved.
- [ ] Histogram fills unchanged and actual series-stroked outlines verified.
- [ ] Legend ≥50×12, stroke/marker parity, labels and current-series selection preserved.
- [ ] Default-on setting wired bean↔plot↔checkbox; initialization, replacement and Cancel tested.
- [ ] Full raw series name prefixes pointer numeric text, correct model index and snap behavior preserved.
- [ ] Multisource unsorted color identity vs visible stroke identity traced, selected icons exact, previews explicit, no renderer leakage.
- [ ] Focused Fast tests and broad baseline comparison completed; expected tests actually ran.
- [ ] 8.3-a/b/c/d and static 8.4-b passed; no blocked test disguised as a substitute.
- [ ] Scientific fidelity/spike evidence complete and intentional raster changes recorded.
- [ ] Approximately 100,000-point performance and live EDT responsiveness acceptable.
- [ ] Two reviewers completed macOS/Windows/Linux checklist and filtered-image mapping; evidence attached; High Contrast logged.
- [ ] No unrelated product, solver, export-server or configuration changes; final diff reviewed.
- [ ] Verification document updated with truthful results and only fully earned completion marks.

Planning self-review: all Phase 2 production targets, core bean/data model, shared util implementation and pre-existing tests, 8.3-a–d, 8.7, related regression gates, event/index flows and consumer searches were inspected. No code/tests were changed or run. The two acceptance conflicts are intentionally visible rather than concealed behind a promise that this plan alone makes Phase 2 complete.
