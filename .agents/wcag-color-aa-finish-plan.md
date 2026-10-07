# WCAG color AA finish plan

**Date:** 2026-10-07
**Branch under test:** `chore/vcell#1605` at `e21a3024c4` (clean working tree)
**Status:** execution plan. Not a conformance claim. Not a closure of #1605 or #1603.

This plan is what remains to meet the color-accessibility requirements named below. Provenance
and the phase 0–7 build log stay in
[`.agents/uconn-color-blind-accessibility-verified.md`](uconn-color-blind-accessibility-verified.md).
The 2026-09-30 inventory is
[`docs/accessibility/color-audit.md`](../docs/accessibility/color-audit.md) on
`docs/1605-color-audit` (PR [#2141](https://github.com/virtualcell/vcell/pull/2141), open).

---

## 1. Requirement

Two surfaces, two editions. The color criteria themselves are the same text in both editions.

| Surface | Standard | How it applies |
|---|---|---|
| Desktop client (Swing, in-app JavaHelp) | **WCAG 2.1 Level AA**, read with **WCAG2ICT** | Non-web software. Use the W3C Group Note [WCAG2ICT](https://www.w3.org/TR/wcag2ict-22/) (15 November 2024). That note interprets WCAG 2.2 for ICT; every 2.1 AA criterion below is in it. |
| Web content | **WCAG 2.2 Level AA** | [WCAG 2.2](https://www.w3.org/TR/WCAG22/). Covers `webapp-viewer` (the field viewer the desktop serves), `webapp-ng`, and HTML help published on the web. |

### 1.1 Criteria this plan must meet

| ID | Level | Requirement | VCell reading |
|---|---|---|---|
| **1.3.3** Sensory Characteristics | A | Instructions do not rely on shape, color, size, visual location, orientation, or sound alone. | Help, dialogs, and status text name the cue a user can find without hue. |
| **1.4.1** Use of Color | A | Color is not the only visual means of conveying information, indicating an action, prompting a response, or distinguishing a visual element. | A series, selection, region, error, warning, match, or out-of-range state has a second visual cue: text, pattern, shape, position in a named list, or a lightness difference of at least 3:1 where Understanding 1.4.1 allows that. |
| **1.4.3** Contrast (Minimum) | AA | Text and images of text are at least **4.5:1** against the background. Large text (18 pt, or 14 pt bold) is at least **3:1**. Inactive and purely decorative text are exempt. | Use the WCAG size rule, not the UConn “18 pt and smaller” wording. Measure the actual row color, including selected, alternate, and hover rows. |
| **1.4.11** Non-text Contrast | AA | A UI component state, and a graphical object required to understand the content, is at least **3:1** against adjacent colors. A presentation that is essential to the information is exempt. | Plot lines that carry identity, selection strokes, focus rings, and state icons meet 3:1. A measurement gradient may use the essential-presentation exception only for the gradient itself. |

Normative text: [1.3.3](https://www.w3.org/TR/WCAG22/#sensory-characteristics),
[1.4.1](https://www.w3.org/TR/WCAG22/#use-of-color),
[1.4.3](https://www.w3.org/TR/WCAG22/#contrast-minimum),
[1.4.11](https://www.w3.org/TR/WCAG22/#non-text-contrast).
WCAG 2.1 has the same four clauses: [WCAG 2.1](https://www.w3.org/TR/WCAG21/).

### 1.2 What this plan does not close

WCAG 2.2 AA adds criteria that are not color criteria: 2.4.11 Focus Not Obscured, 2.5.7
Dragging Movements, 2.5.8 Target Size, 3.2.6 Consistent Help, 3.3.7 Redundant Entry, and
3.3.8 Accessible Authentication. WCAG2ICT also carries keyboard, name, role, value, and
focus criteria from 2.1 AA. Those stay in the broader programme (#1603, #1604, #1606).
Passing this plan does not authorize a statement that VCell conforms to WCAG 2.2 AA or to
WCAG2ICT as a whole.

UConn’s “avoid red/black” and “avoid red/green” wording is guidance. A red/black pair that
already meets 1.4.1 and 1.4.3 is not a failure by itself. A pair that fails those criteria
is a failure whether or not the guidance names it.

### 1.3 How to apply the heat-map exception

Understanding 1.4.11 treats a color gradient that represents a measurement, such as a heat
map, as an essential presentation. In VCell that exception may cover the **data pixels of a
spatial field** (BlueRed, Gray, Cividis, or the field-viewer rainbow) when all of the
following are true:

1. The gradient encodes a measured scalar, and replacing it with a patterned scale would
   destroy the measurement.
2. The numeric value is available without relying on hue: the hover or keyboard readout,
   the data table, or an export of the values.
3. Everything that is not the measurement stays in scope: axes, legends, color-bar labels,
   controls, focus, selection, NaN, below-min, above-max, not-in-domain, and no-range.

A written exception with no numeric access, or an exception applied to a legend or a
control, is not a pass.

### 1.4 WCAG2ICT notes for the Swing client

- Judge the running application, including platform theme, font scaling, and high contrast
  where the client claims to support them. A headless unit test does not replace that.
- Where the only non-color cue is text inside a custom-painted component, expose that text
  through the component’s accessible name or description. A pixel change that a screen
  reader cannot reach does not satisfy 1.4.1 for a user of assistive technology.
- Do not treat a disabled test or an unmerged audit as evidence.

---

## 2. Already built on this branch

These changes exist in `chore/vcell#1605`. Each row still needs the evidence in the last
column before it can be called a pass. Automated results below are recorded in the phase
log; they are not a human pass.

| Surface | Criterion | In the branch | Evidence still required |
|---|---|---|---|
| Legacy plots (`Plot2DPanel`, `PlotPane`, `MultisourcePlotPane`) | 1.4.1, 1.4.11 | `ColorUtil.CVD_SAFE_LIGHT` and `seriesDash`. One `Path2D` per curve. Legend icon draws the stroke. Status text names the series. “Vary line styles” defaults on. Ctrl+N / Ctrl+P / Ctrl+I name or isolate a series. | Manual 8.7 on macOS, Windows, and Linux. Styles-off and repeated-dash cases must still name the series. |
| Langevin plots | 1.4.1, 1.4.11 | Both panels use `CVD_SAFE_LIGHT`. Legend stroke follows “Vary line styles”. Markers stay spaced while lines are on. | Recapture the stale line-plot screenshots, then the same manual review. |
| Reaction and rule edges | 1.4.1 | Selected stroke is 2.5 px, including when only the start species is selected. Hues are unchanged. | Confirm a shape or label still works where width is not enough (arrowheads, dense diagrams). Manual review. |
| Geometry hover | 1.4.1 | Index hover appends the subvolume name. The slice view also lists every subvolume name, and keyboard selection writes that name into the readout. See R3. | A live geometry window was not reviewed separately from `GeometryRegionListTest`. |
| Spatial colormap | 1.4.1, 1.4.11 | Cividis is registered after Gray and BlueRed. Special colors contrast at least 3:1 with both stored endpoints and have names. BlueRed stays the default. | Review the retained BlueRed default (R1). Client must not send Cividis to an old export server (R2). |
| Field viewer series and text | 1.4.1, 1.4.3, 1.4.11 | Light palette matches `CVD_SAFE_LIGHT`. Dark palette is measured. Traces and swatches use the dash cycle. `#157347` replaces `#2a7`. Dark error and warning colors are in CSS. 32/32 source pairs passed the 8.5 script. 193 Chromium tests passed in the phase 6 log. | Filtered-image review. `setTable` for Cividis was not run against the VTK WebAssembly bundle. |
| Overrides table and simulation console | 1.4.3, 1.4.1 | `ERROR_TEXT_COLOR` (`#A40000`) and `WARNING_TEXT_COLOR` (`#8A4B00`). Console lines start with `[Error]`, `[Warning]`, or `[Stopped]`. The six R4 text sites use those colors, with the severity in the words. | Selected rows use the look-and-feel selection ink, measured in R4. Other `Color.red` text remains under R5 and R9. |
| Seven help pages | 1.3.3 | Those pages name a non-color cue. The help target was rebuilt. | The remaining color-only pages (R6) and the published HTML / JavaHelp index. |

`generateAutoColor`, `TABLEAU20`, `DARK20`, and `COLORBLIND20` are unchanged because solver
and FRAP input depend on their exact values. New UI must not call `generateAutoColor` for
a color that identifies a series.

---

## 3. Remaining work

Do these in order. A later package may use constants from an earlier one. Do not mark a
package done because the code compiles. The acceptance line is the pass.

### R1 — Retained BlueRed default (desktop spatial viewer)

**Criteria:** 1.4.1, 1.4.11, and 1.4.3 for any text drawn on the viewer.
**Why it is open:** BlueRed is still the default, so every existing figure uses it. The
gradient may keep the §1.3 exception. The chrome around it may not.

**Check, on a real results view, with BlueRed selected:**

- Color-bar labels and tick text ≥ 4.5:1.
- Legend swatches for special states are labeled (BM, AM, NN, ND, NR, and the longer names)
  and each indicator is ≥ 3:1 against the colors it actually sits beside.
- Focus, selection, and the slice controls are visible without hue.
- NaN, below-min, above-max, not-in-domain, and no-range are identifiable with the label
  visible, not only by hue.
- The value under the pointer is numeric. The same value is reachable from the keyboard.

**Pass:** a written record of those checks, with screenshots, says each one holds.
**Fail:** any item fails. Fix that item. Do not switch the default to Cividis unless a
reviewer records that BlueRed cannot be fixed and the default change is approved. A silent
default change rewrites every user’s figures.

**Result (2026-10-07):** BlueRed is still the default. The 248 data colors are unchanged
(`BlueRedSpecialColorTest` asserts the low pixel is `(0,0,128)` and the high pixel is
`(255,0,0)`). The eight special-state colors were replaced. The old set used black, white,
and grays; black against the dark-blue end was about 1.3:1, and several grays sat on top of
each other. The new indicators clear 3:1 against the black gap they sit on and against the
neighboring swatch. Below-min also clears the dark-blue end, and above-max clears the red
end. In-range BlueRed pixels are the same; out-of-range pixels in a BlueRed figure use the
new indicators.

Checked on the results-viewer components (`DisplayAdapterServicePanel`, `ImagePlanePanel`,
`ImagePlaneManagerPanel`), painted headlessly. A simulation dataset was not opened.

| Check | Record |
|---|---|
| Color-bar labels and tick text | Min and Max, and the values `0.0` and `10.0`, are black on white. `BlueRedViewerChromeTest` requires ≥ 4.5:1. |
| Legend swatches | Text is `BM Below minimum`, `AM Above maximum`, `NN Not a number`, `ND Not in domain`, `NR No range`. Each swatch is ≥ 3:1 against the black gap and against its neighbor, and the short label is ≥ 4.5:1 on the swatch. |
| Focus, selection, slice controls | The active colormap reads `BlueRed selected` inside a black border. The slice axis reads `XY selected` inside a black border. Slider focus is a 3px black stroke. |
| Special states | The five names above are on the swatches, not only in a tooltip. |
| Pointer value | The info line contains the numeric sample (`Index = 7` in the test). Arrow keys on the image call the same formatter. |

Screenshots: `docs/accessibility/evidence/2026-10-r1-bluered/bluered-legend.png`,
`special-states.png`, `slice-controls.png`.

Java 17, 2026-10-07: `BlueRedSpecialColorTest` 2, `BlueRedViewerChromeTest` 3,
`ImagePlaneManagerPanelAccessibilityTest` 2, `DisplayAdapterServiceColormapTest` 3.
Failures 0. The §6 R1 box stays open until someone repeats this on an opened results window.

### R2 — Export server and an old client (1.4.1)

**Criteria:** 1.4.1. A movie or image whose colors do not match its label is a failure.
**Code today:** `ExportSpecs.setupDisplayAdapterService` logs a warning and renders BlueRed
when the requested mode is not registered. That protects a **new** server. An **old** server
still throws, and a new client that offers Cividis has no check.

**Change:**

- Before the desktop client puts Cividis in an export request, learn whether that server
  has it. If the server does not, keep the request on Gray or BlueRed and show the user
  which mode will actually be written.
- Do not leave the request object saying Cividis after the picture was drawn with BlueRed.

**Pass:** test 8.8-b on a real upgraded server and a real old server. BlueRed output matches
the pre-change BlueRed output. Cividis succeeds only on the upgraded server. The old server
produces a named, supported mode and a visible notice.
**Fail:** an exception, a wrong label, or a palette substitution with no notice.

Deploy the export server before any client build that shows Cividis. That deploy uses the
normal release authorization.

**Result (2026-10-07):** The desktop export path asks the server which colormap ids it can
draw (`DataSetController.getSupportedExportColorModes`). A local run uses Gray, BlueRed, and
Cividis. A remote call that fails with `No such method: getSupportedExportColorModes` is
treated as Gray and BlueRed. `ExportColorModeNegotiation.preferencesForServer` then stores
that mode on the `DisplayPreferences` that go into the request. If the mode changes, the
export panel shows “This server will write BlueRed. Cividis is not available on the export
server.” If a request still names a mode the process cannot draw,
`ExportSpecs.setupDisplayAdapterService` rewrites that same object to BlueRed before the
pixels are produced, so the object does not keep saying Cividis.

Not run: 8.8-b against a deployed upgraded server and a deployed old server. The unit tests
stand in for those two answers. Do not treat them as the deployment test. Ship the export
server before a client build that shows Cividis.

Java 17, 2026-10-07: `ExportColorModeNegotiationTest` 4,
`ExportSpecsColorModeFallbackTest` 4. Failures 0. The §6 R2 box stays open.

### R3 — Geometry identity without hover (1.4.1)

**Criteria:** 1.4.1.
**Code today:** hover text includes the subvolume name (`ImagePlaneManagerPanel`
`indexLabelProvider`, set from `GeometryViewer`). The region list and a keyboard readout
are not done (#2139).

**Change:**

- A list of regions shows each subvolume name. The swatch may stay; the name is the cue.
- Keyboard selection of a region updates a readout with that name. Hover remains an extra.
- Boundaries that a user must see to understand the geometry are ≥ 3:1 against the adjacent
  region, or the name is available without seeing the boundary.

**Pass:** identify every region in a test geometry with the pointer unused. Contrast of
any boundary that still carries meaning is measured.
**Fail:** a region that can be named only by its color or only by hovering.

**Result (2026-10-07):** `GeometryViewer` copies each subvolume name into a Regions list
on the slice view. Selecting a row, including through the list selection model with no
mouse event, sets the info readout to `Region: ` plus that name. Hover still appends the
name and is not required. `GeometryRegionListTest` builds a geometry with extracellular,
cytosol, and nucleus, then selects each row and reads the name back.

The contrast colormap fills for handles 0–1 and 1–2 measure 1.51:1 and 1.71:1. Both are
under 3:1, so a boundary between those fills is not the cue. The name in the list is.

Not run: a separate review of an opened geometry window outside that test. The §6 R3 box
is checked for the fixture the test builds.

### R4 — Remaining red text (1.4.3, and 1.4.1 where hue is the only severity cue)

**Criteria:** 1.4.3 on every row state the cell can paint. 1.4.1 where two severities share
one hue.
**Issue:** [#2140](https://github.com/virtualcell/vcell/issues/2140).

Use `GuiConstants.ERROR_TEXT_COLOR` and `WARNING_TEXT_COLOR`. Add a text tag where the
words do not already say the severity. Measure white, the alternate row, the hover row,
and the selected row. The two constants clear 4.5:1 on white, `#e8edff`, and `#FDFCDC`.
On the Mac selection blue they do not, so a selected row keeps the selection foreground.

| Location | What to change |
|---|---|
| `StructureMappingTableRenderer` | “Unmapped” red text |
| `MathOverridesPanel` | Red button text |
| `MultiPurposeTextPanel` | Error line |
| `DefaultScrollTableCellRenderer` (changed network constraint) | Red text; bold is not a contrast substitute |
| `DefineROI_SummaryPanel` | Red text |
| `NumericsTestCellRenderer` | Red text |

**Pass:** a test, or a measured table, shows ≥ 4.5:1 (or ≥ 3:1 for large text) on each
real background, and the severity is in the text.
**Fail:** any remaining `Color.red` / `Color.RED` foreground that is status or data text.
`GuiConstants.ProblematicTextFieldBorder` is a border, tracked under R5, not this row.

**Result (2026-10-07):** The six text sites no longer use `Color.red` for the words.
Selected rows keep the look-and-feel selection foreground, because `#A40000` on the Mac
selection blue `rgb(8,74,217)` is 1.16:1. White on that blue is 7.02:1. The severity
word stays in the text when the row is selected.

| Site | Ink when not selected | Words | Backgrounds measured |
|---|---|---|---|
| `StructureMappingTableRenderer` Unmapped | `#A40000` | Unmapped | White, and the table selection pair |
| `MathOverridesPanel` button | `#8A4B00` | Warning: remove unused parameter overrides | Button background |
| `MultiPurposeTextPanel` error line | `#A40000` on a white chip | `error` plus the line number | White. The gray gutter is 2.06:1, so the chip is the paper |
| `DefaultScrollTableCellRenderer` changed network constraint | `#8A4B00` | value plus `changed` | White, `#e8edff`, and the selection pair. Hover paper `#FDFCDC` is the same constant the renderer paints |
| `DefineROI_SummaryPanel` | `#A40000` | The sentence already says the bleached ROI is required | Panel background |
| `NumericsTestCellRenderer` | `#A40000` for a failed variable or failed-vars status; `#8A4B00` for the other non-pass statuses | `failed`, or the status already in the label | Tree paper, and white on the tree selection blue |

`#A40000` and `#8A4B00` are at least 4.5:1 on white, `#e8edff`, and `#FDFCDC`. The tests
assert that, and they assert the selection ink against the selection paper the component
paints.

Still `Color.red` text, so the §6 R4 box stays open: `ConstraintPanel` and
`ConstraintTableCellRenderer` (R5), `AnalysisTableRenderer` (R9), and `MyRenderer`.
Issue borders and `ProblematicTextFieldBorder` stay in R5.

Java 17, 2026-10-07: `GeometryRegionListTest` 1, `ImagePlaneManagerPanelAccessibilityTest` 4,
`StatusTextContrastTest` 4, `StructureMappingUnmappedTextTest` 1,
`MathOverridesWarningButtonTest` 1, `DefineRoiStatusTextTest` 1,
`GuiConstantsContrastTest` 1. Failures 0.

### R5 — Errors that are still a color (1.4.1, 1.3.3, 1.4.11)

**Issue:** [#2136](https://github.com/virtualcell/vcell/issues/2136),
[#2140](https://github.com/virtualcell/vcell/issues/2140).

| Item | Change | Pass |
|---|---|---|
| Rule-based glyph error outline (`MolecularTypeLargeShape`, about lines 666–690). Dark red vs dark gray is 1.43:1. | If the glyph is visible without the issue table, add a shape or a text mark on the glyph. If the issue table is always on screen and already has an icon plus words, record that and stop treating the outline as the cue. | A user can name the error without hue. If the outline remains the cue, it is ≥ 3:1 against the adjacent color. |
| Validation after a dialog (`OutputOptionsPanel`, `MeshSpecificationPanel`, `StochSimOptionsPanel`, `TableCellEditorAutoCompletion`) | Keep the text. Where the user must recover after the dialog closes, leave a persistent message, not only a red border. | Keyboard user can find the error, fix it, and see the message clear. |
| Yellow match rows (`DefaultScrollTableCellRenderer`) | Add a match label or icon. Yellow on white is about 1.07:1. | Match rows are identifiable with the yellow removed, including when the row is selected. [#2138](https://github.com/virtualcell/vcell/issues/2138). |
| `ConstraintPanel` | Prove it is not reachable in the shipped client, or fix it the same way as the other error text. | Reachability note from the packaged entry points, or a remediated UI. [#2137](https://github.com/virtualcell/vcell/issues/2137). |

### R6 — Help text (1.3.3)

**Criteria:** 1.3.3. This is web content once the HTML is published, and non-web content
inside JavaHelp.

**Done on the branch:** `simulationEditor.xml`, `simulations.xml`, `PP_Species.xml`,
`PP_ReactionRulesEditor.xml`, `PathwayDiagramView.xml`, `PathLink.xml`,
`SimResultsDataRange.xml`.

**Still color-only:**

- `Observables.xml` and `PP_Observables.xml`: “A site that has a defined state is always
  shown in yellow.” Name the non-color way to see that state, or change the UI so one
  exists and then name it.

**Then:** search `vcell-client/UserDocumentation` again for instructions that use only a
color. Rebuild with `mvn process-classes -pl vcell-client -am -Pbuild-documentation`.
Check the JavaHelp search index and the HTML that is actually published, not only
`target/classes/vcellDoc`.

**Pass:** the search returns no instruction whose only cue is a color, and a reviewer can
follow each rewritten page in the shipped help.
**Fail:** a page in the shipped help still says to look for a color with no other cue.

### R7 — Field viewer, closed on the web edition (1.4.1, 1.4.3, 1.4.11)

**Code today:** series colors, dashes, and the text colors in §2. Rainbow stays the default.
Cividis is a selector choice. The 3D lookup table is supposed to use `setTable` for Cividis.

**Change and checks:**

- Run the viewer against the VTK WebAssembly bundle with a dataset. Select Cividis and
  confirm the surface and the kymograph use the same bytes. Select Rainbow and confirm the
  surface returns to the hue ramp.
- When more series are shown than the dash cycle can uniquely pair with color, the name
  stays on the trace or in the list. Six probes is the current cap; do not raise it without
  that rule.
- Filtered screenshots (protan, deutan, tritan, grayscale) still let a reviewer match each
  named trace to its line.

**Pass:** the bundle run is recorded, and the filtered-image review is recorded.
**Fail:** the 3D view and the kymograph disagree, or a trace can be matched only by hue.

### R8 — Webapp (WCAG 2.2 AA color criteria)

**Issue:** [#2135](https://github.com/virtualcell/vcell/issues/2135).
**Surface:** `webapp-ng`, including publication badges and the footer.

**Change:**

- Badge text ≥ 4.5:1 on its background in the default theme and in any dark theme the app
  ships.
- A state that is only a color gets a text or icon cue.
- The UConn accessibility link required for university websites is a procedures item, not
  a WCAG color criterion. Add it in the same change so the page is not left half done, and
  do not count the link as a 1.4.1 pass.

**Pass:** computed contrast for every badge and state pair, on desktop and a narrow
viewport, and a click-through of the link.
**Fail:** any text under 4.5:1, or a status told only by color.

### R9 — Other color-only scientific views

These are in the audit and are not fixed on the branch. Each one fails 1.4.1, 1.4.3, or
both until the acceptance line is true. Persisted scientific colors stay in the file
format. The non-color cue is presentation.

| View | Issue | Change | Pass |
|---|---|---|---|
| SpringSaLaD sites | [#2132](https://github.com/virtualcell/vcell/issues/2132) | Labels, outlines, or patterns on the canvas. Keyboard isolation of a site. Saved colors unchanged. | Sites are distinguishable in grayscale. Model serialization and solver input are unchanged. |
| Image-geometry ROI editor | [#2133](https://github.com/virtualcell/vcell/issues/2133) | Named ROI list. Selection and boundary cues that are not hue alone. Keyboard create, select, and edit where the mouse path exists. | A region can be created and selected with no use of its color. ROI data matches the pre-change data. |
| FRAP analysis table | [#2134](https://github.com/virtualcell/vcell/issues/2134) | “NOT IDENTIFIABLE” already exists. The red text on `(255,170,170)` is 2.21:1. Recolor the text to ≥ 4.5:1 on that pink and on white, selected, and hover. | Every row state meets 1.4.3, and the words still say the result. |

### R10 — Human review (all four criteria)

Automated tests do not close 1.4.1. Two reviewers, one of them a person with color-vision
deficiency when one is available, otherwise using Sim Daltonism or Color Oracle, run one
script on macOS, Windows, and Linux:

1. Match each legend entry to its curve with the display in grayscale.
2. Name the selected reaction edge without calling it “the red one.”
3. Name a geometry region from the list, without hovering.
4. Read an error, a warning, and a stopped console line.
5. Read a changed parameter override.
6. On a BlueRed spatial view, name a below-min pixel and read the value under the cursor
   from the keyboard.
7. On the field viewer, match three probes to their names.
8. Follow one rewritten help page to the control it describes.

**Pass:** every item is yes, with the OS, JDK, and browser versions written down.
**Fail:** any item is no. A missing platform is not a pass.

---

## 4. Tests to add or re-run

Existing tests stay. Add the ones this plan introduces. Run Java tests with Java 17 and
`mvn test -pl <module> -am` so the reactor uses this tree.

| Check | Pass |
|---|---|
| Re-run `ColorAccessibilityTest`, `Plot2DPanelAccessibilityTest`, `PlotPaneAccessibilityTest`, `Langevin` tests, `DisplayAdapterServiceColormapTest`, `ExportSpecsColorModeFallbackTest`, `GuiConstantsContrastTest`, `SimulationConsolePanelAccessibilityTest` | No new failures |
| `StatusTextContrastTest`, `StructureMappingUnmappedTextTest`, `MathOverridesWarningButtonTest`, `DefineRoiStatusTextTest` | The six R4 text sites are ≥ 4.5:1 on white, the alternate row, the hover paper, and the selection ink actually painted. Selected rows do not use `#A40000` on the Mac selection blue |
| New: FRAP non-identifiable cell on its pink background | ≥ 4.5:1 |
| New: client export request when the server reports no Cividis | `ExportColorModeNegotiationTest` stores BlueRed and builds the notice. A deployed old server and a deployed new server have not been run. |
| `pytest webapp-viewer/test` plus a Cividis `setTable` check on the wasm bundle | Suite green; surface and kymograph share `CIVIDIS_RGB` |
| `webapp-ng` contrast on badge and status colors | ≥ 4.5:1 |
| Help search for color-only instructions | Only lines that also name a non-color cue |

A Fast-group run that reports the known missing-Poetry errors
(`MathOverrideRoundTripTest`, `CopasiOptimizationSolverTest`, `VCellDataTest`) is the
existing baseline, not a regression from this work.

---

## 5. Order

1. **R1** and **R4** and **R6**. They finish criteria on code this branch already started.
2. **R2** before any release that shows Cividis in the client. Server first, then client.
3. **R3**, **R5**, **R9**. These are the views that are still color-only.
4. **R7** and **R8**. Web edition evidence.
5. **R10** last, on the build that contains 1–4. Do not review a stale screenshot set.

---

## 6. Done

This plan is done only when every box below is true for a named build. Checking a box
without the evidence is not allowed.

- [ ] R1 has a written BlueRed review, and every failed item in it is fixed.
- [ ] R2 has a real old-server and new-server export run. Labels match the pixels.
- [x] R3 identifies every test region with no hover and no hue. `GeometryRegionListTest`, 2026-10-07. Adjacent contrast-colormap fills are 1.51:1 and 1.71:1, so the name is the cue.
- [ ] R4 has no remaining status or data text under 4.5:1, including selected rows. The six listed sites are measured. `ConstraintPanel`, `ConstraintTableCellRenderer`, `AnalysisTableRenderer`, and `MyRenderer` still use `Color.red` text.
- [ ] R5 errors, match rows, and the glyph error are identifiable without hue. `ConstraintPanel` is either unreachable in the shipped client or fixed.
- [ ] R6: shipped JavaHelp and published HTML contain no color-only instruction.
- [ ] R7: field-viewer Cividis and Rainbow were exercised on the VTK bundle, and the filtered-image review passed.
- [ ] R8: `webapp-ng` text and states meet 1.4.3 and 1.4.1.
- [ ] R9: SpringSaLaD, ROI editor, and FRAP table meet the pass lines in that section. Saved scientific colors and solver input are unchanged.
- [ ] R10: two reviewers, three desktop platforms, every script item yes.
- [ ] The audit PR #2141 is merged, or its rows are reproduced in the evidence for this build.

When those boxes are true, this branch meets **WCAG 2.1 AA color criteria 1.3.3, 1.4.1,
1.4.3, and 1.4.11** for the Swing client under WCAG2ICT, and **WCAG 2.2 AA for those same
four criteria** on `webapp-viewer`, `webapp-ng`, and published help.

That sentence is the whole claim. It does not say VCell conforms to WCAG 2.2 AA, and it
does not close #1603.
