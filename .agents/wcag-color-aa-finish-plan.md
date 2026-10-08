# WCAG color AA finish plan

**Date:** 2026-10-07
**Branch under test:** `chore/vcell#1605` at `d1077b2ec5`, which contains the C1–C10
code and evidence. C11 has no commit yet, and the C12 record is uncommitted working tree.
**Status:** execution plan. Not a conformance claim. Not a closure of #1605 or #1603.

This plan is what remains to meet the color-accessibility requirements named below. Provenance
and the phase 0–7 build log stay in
[`.agents/uconn-color-blind-accessibility-verified.md`](uconn-color-blind-accessibility-verified.md).
The 2026-09-30 inventory is
[`docs/accessibility/color-audit.md`](../docs/accessibility/color-audit.md) on
`docs/1605-color-audit` (PR [#2141](https://github.com/virtualcell/vcell/pull/2141)).
That PR was not merged here; every finding row is reproduced with its disposition in
this build in [`docs/accessibility/evidence/2026-10-c12-audit/`](../docs/accessibility/evidence/2026-10-c12-audit/)
(closeout C12, 2026-10-08).

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
| Geometry hover | 1.4.1 | Index hover appends the subvolume name. The slice view also lists every subvolume name, and keyboard selection writes that name into the readout. See R3. | Opened geometry window, closeout C5, 2026-10-07. Down arrow named cytosol and nucleus. Readout `Region: nucleus`. |
| Spatial colormap | 1.4.1, 1.4.11 | Cividis is registered after Gray and BlueRed. Special colors contrast at least 3:1 with both stored endpoints and have names. BlueRed stays the default. | Opened BlueRed results window, closeout C4, 2026-10-07. Client must not send Cividis to an old export server (R2). |
| Field viewer series and text | 1.4.1, 1.4.3, 1.4.11 | Light palette matches `CVD_SAFE_LIGHT`. Dark palette is measured. Traces and swatches use the dash cycle. `#157347` replaces `#2a7`. Dark error and warning colors are in CSS. 32/32 source pairs passed the 8.5 script. 193 Chromium tests passed in the phase 6 log. | fv3d `setTable` run is in the R7 result. Closeout C8, 2026-10-07: live fenics2d probes P1–P3 match by name in grayscale and a Machado protan filter. |
| Overrides table and simulation console | 1.4.3, 1.4.1 | `ERROR_TEXT_COLOR` (`#A40000`) and `WARNING_TEXT_COLOR` (`#8A4B00`). Console lines start with `[Error]`, `[Warning]`, or `[Stopped]`. The six R4 text sites use those colors, with the severity in the words. | Selected rows use the look-and-feel selection ink, measured in R4. `ConstraintPanel` is not a shipped screen (R5). `AnalysisTableRenderer` was recolored in R9. `MyRenderer` leads with the status word and uses ≥ 4.5:1 inks (R4 update, closeout C1). |
| Seven help pages | 1.3.3 | Those pages name a non-color cue. The help target was rebuilt. | Rewritten local and JavaHelp pages are done (closeout C10: Observables names the written state and the question mark). Only the copies published on vcell.org still say “shown in green” (R6). |

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
Failures 0.

**Opened window (2026-10-07, closeout C4):** “Simulation results — BlueRed”, BlueRed
selected. Record `docs/accessibility/evidence/2026-10-c4-c6/record.txt`, screenshot
`c4-results-bluered.png`. `0.0` and `10.0` are black on white at 21.00:1. The five
special names are on the swatches; the lowest short-label contrast is 5.08:1 and the
lowest swatch-on-gap contrast is 3.55:1. `BlueRed selected` and `XY selected` have 2px
black borders. Slider focus is a 3px black border. The pointer and the Right arrow
both read `Value = 4.25`. BlueRed stays the default. The §6 R1 box is checked.

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

Closeout C9, 2026-10-07, is a local RPC pair through `VCRpcRequest`, recorded in
`docs/accessibility/evidence/2026-10-c9-export/`. No release was tagged. An upgraded
target that lists Gray, BlueRed, and Cividis kept the Cividis request on Cividis
(`#00224e` … `#fee838`) and the BlueRed request on BlueRed (`#000080` … `#ff0000`,
the retained data ends). An old target with no `getSupportedExportColorModes` threw
`No such method: getSupportedExportColorModes(org.vcell.util.document.User)`, kept the
request object on BlueRed, and showed the Warning dialog “This server will write
BlueRed. Cividis is not available on the export server.” That local pair is still not
the deployed pass.

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

Opened window (2026-10-07, closeout C5): “Geometry — regions”, the same three names.
The pointer was not used to select. Down arrow selected cytosol, then nucleus, and the
readout ended `Region: nucleus`. The slice image was black; naming did not use that
fill or a hover. Screenshot
`docs/accessibility/evidence/2026-10-c4-c6/c5-geometry-regions.png`. The §6 R3 box
stays checked.

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

`ConstraintPanel` and `ConstraintTableCellRenderer` still contain `Color.red`. R5 records
that the panel is constructed only from its own `main`, so that red is not a shipped screen.
`AnalysisTableRenderer` was recolored in R9. Issue borders stay with the glyph work in R5.
Field validation now also leaves a sentence on the panel; the red border is extra.

**R4 update (2026-10-07, closeout C1):** `MyRenderer` no longer uses `Color.red`,
`Color.gray`, or a hue-only status. The merge-tree label now leads with the status word
(`new:`, `removed:`, `changed:`); the icon and tooltip repeat it. Unselected inks are
`Color.blue` (8.59:1 on white) for new, `#A40000` (8.15:1) for removed, and `#8A4B00`
(6.80:1) for changed — each ≥ 4.5:1 on white, `#e8edff`, and `#FDFCDC`. A selected row
keeps the look-and-feel selection foreground. `MyRendererStatusTextTest` (3 tests)
covers every status on attribute and element nodes, the three papers, and the selected
row. A fresh search finds no other reachable `Color.red` / `Color.RED` foreground used
as status or data text. The §6 R4 box is now checked.

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

**Result (2026-10-07):**

- The molecule glyph paints the word `error` in `#A40000` on a white chip when the molecule has error issues. The red outline stays as extra. `MolecularTypeErrorMarkTest` counts that ink on the chip. Contrast of the ink on white is at least 4.5:1. The issue table was not treated as the cue.
- `OutputOptionsPanel`, `MeshSpecificationPanel`, and `StochSimOptionsPanel` keep the dialog text and also leave that sentence on the panel. Clearing the report removes the sentence and the field's accessible description. `TableCellEditorAutoCompletion` titles the editor border `error` and keeps the recovery sentence as the tooltip and accessible description until editing starts again. The tests call the same show/clear methods the verifiers call.
- Opened `MeshSpecificationPanel` (2026-10-07, closeout C6). Typed `abc` into X and pressed Tab. The Error dialog said `Wrong number format for input string: "abc"`. After it closed, that sentence was still on the panel. Replaced X with `10` and pressed Tab. The sentence cleared. Screenshots in `docs/accessibility/evidence/2026-10-c4-c6/`.
- A spatial match row appends ` match` on the name cell when the row is selected and when it is not. Yellow is painted only when the row is not selected. `SpatialMatchLabelTest` covers both.
- `ConstraintPanelReachabilityTest` walks `src/main` Java and finds `new ConstraintPanel(` only in `ConstraintPanel.java`, in its `main`. The panel is not a shipped screen. It was not deleted, and its red text was not recolored.

Java 17, 2026-10-07: `MolecularTypeErrorMarkTest` 1, `MeshSpecificationValidationTest` 1, `SolverOptionsValidationTest` 2, `TableCellEditorAutoCompletionTest` 1, `SpatialMatchLabelTest` 1, `ConstraintPanelReachabilityTest` 1. Failures 0.

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

**Result (2026-10-07):** `Observables.xml` and `PP_Observables.xml` now say a defined state is the state's name written on the site, and a question mark means the state is not chosen. Yellow and light grey are named only as extra fills. The same pass rewrote the other color-only instructions found in `UserDocumentation`: simulations summary, problems border, geometry mapping squares, reaction-diagram nodes, catalyst toggle, species and reaction depictions, pathway entity types and search marks, output-function `Undefined`, brown non-editable fields, image-geometry region names, BNGL bond indexes, and the trajectory-viewer note that a dark colour can hide a site that is still named in the list.

`mvn process-classes -pl vcell-client -am -Pbuild-documentation` finished BUILD SUCCESS at 2026-10-07T16:04:04-04:00. Generated HTML under `vcell-client/target/classes/vcellDoc` has no `shown in yellow`, `colored yellow`, `shown in red`, `colored distinct`, or `Items in brown`. The pages that still say yellow also name the written state or the question mark. `JavaHelpSearch` was rewritten in that same run. A byte scan of the index finds no `shown in yellow`.

Closeout C10, 2026-10-07, followed Observables in the client’s JavaHelp and on the published site. Record: `docs/accessibility/evidence/2026-10-c10-help/`. The JavaHelp page says the state’s name is written on the site and a question mark means the state is not chosen. The observables properties editor shows site Y with `p` written on it and site `l` with a question mark. The published page at https://vcell.org/webstart/VCell_Tutorials/VCell_Help/topics/ch_2/Physiology/Observables.html still says “A site that has a defined state is always shown in green,” and the published properties page says the same. `webhelp-deploy.yml` was not run. Branch `chore/vcell#1605` is not on origin. §6 R6 stays open.

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

**Result (2026-10-07):** The probe cap stays at six (`MAX_PROBES = seriesColors.length`). Dashes repeat inside that cap, so each probe name is drawn on its trace and listed beside it. Stats curves get the series name on the line as well. The cap was not raised.

On the VTK WebAssembly bundle already in `webapp-viewer/assets/vtk-wasm`, the fixture server served `fv3d` (`sim` 868220316, job 0) at `127.0.0.1:58911`. Rainbow was the first surface: a hue bar, blue at 29.9 and red at 45.3. Cividis changed that bar to dark blue through gray to yellow, and the kymograph gradient stops were `rgb(0,34,78)` and `rgb(254,232,56)`, the Cividis endpoints. The first return to Rainbow updated the kymograph stops to `rgb(0,0,255)`, `rgb(2,255,0)`, and `rgb(255,0,0)` while the 3D bar stayed on Cividis, because `forceBuild` after `setTable` kept the Cividis bytes. Both maps now use `setTable` on the array `fieldLut()` returns. After that change, Rainbow restored the hue bar on the surface and the kymograph together. Screenshots: `docs/accessibility/evidence/2026-10-r7-field-viewer/`.

Filtered review of the six light-palette traces, with the same dashes and the names P1–P6 on each line: protan, deutan, tritan, and grayscale. P2 and P5 share a long dash, and P3 and P6 share a dotted dash, so hue is not a unique match. The names still pair each line with its label in all four views. Those images are drawings of the palette the viewer uses.

Closeout C8, 2026-10-07, captured the live plot. The fixture server served fenics2d (sim `987654321`, job 0) at `127.0.0.1:64504` with three probes. On the dark palette, P1 is `#6c6c6c` solid, P2 is `#999933` dash `6 3`, and P3 is `#556998` dash `2 2`, and each name is drawn at the right end of its line. The same three pairs hold in grayscale and after a Machado 2009 full-severity protanopia matrix in linear sRGB. Sim Daltonism and Color Oracle are not installed on this machine. Captures: `docs/accessibility/evidence/2026-10-c8-probes/`. The full `pytest webapp-viewer/test` suite was not run.

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

**Result (2026-10-07):** `webapp-ng` ships no dark theme. There is no `prefers-color-scheme`
rule and no theme switch. Each badge already carries its state in words: Published,
Archived, Current, Unknown, Public, Private, Shared. Those words are the 1.4.1 cue.

Computed style from `publication-edit.component.css`, at 12px and font-weight 500, so the
4.5:1 text ratio applies:

| Badge | Ink on its background | Ratio |
|---|---|---|
| Published | `#ffffff` on `#1b5e20` | 7.87:1 |
| Archived | `#ffffff` on `#bf360c` | 5.60:1 |
| Current | `#ffffff` on `#424242` | 10.05:1 |
| Unknown | `#212121` on `#e0e0e0` | 12.20:1 |
| Public | `#ffffff` on `#0d47a1` | 8.63:1 |
| Private | `#ffffff` on `#b71c1c` | 6.57:1 |
| Shared | `#ffffff` on `#9c27b0` | 6.30:1 |

Footer links use `#0b3d91` on the Bootstrap `bg-light` paper `#f8f9fa`, 9.53:1. The same
ratios were read in a 980px desktop viewport and in a 375px-wide frame. An earlier 1280px
pass of the badge CSS produced the same badge ratios.

Closeout C7, 2026-10-07, started the Angular dev server at `127.0.0.1:4200` after
`npm install`. Computed styles on the running badges, at innerWidth 976 and 375, match
the table above (12px, weight 500; lowest Archived 5.60:1). The footer had been
commented out of `app.component.html`; that comment is removed, so the link is on the
running shell. Enter on “Accessibility at the University of Connecticut” loaded
`https://accessibility.uconn.edu/` (page title: Home | Accessibility | Office for Inclusion
and Civil Rights | University of Connecticut). The home page that rendered has no status
told only by color. Record: `docs/accessibility/evidence/2026-10-c7-webapp/`. That link
is the university procedures item. It is not a 1.4.1 pass.

### R9 — Other color-only scientific views

These are in the audit. Persisted scientific colors stay in the file format. The non-color
cue is presentation. The 2026-10-07 result is under the table.

| View | Issue | Change | Pass |
|---|---|---|---|
| SpringSaLaD sites | [#2132](https://github.com/virtualcell/vcell/issues/2132) | Labels, outlines, or patterns on the canvas. Keyboard isolation of a site. Saved colors unchanged. | Sites are distinguishable in grayscale. Model serialization and solver input are unchanged. |
| Image-geometry ROI editor | [#2133](https://github.com/virtualcell/vcell/issues/2133) | Named ROI list. Selection and boundary cues that are not hue alone. Keyboard create, select, and edit where the mouse path exists. | A region can be created and selected with no use of its color. ROI data matches the pre-change data. |
| FRAP analysis table | [#2134](https://github.com/virtualcell/vcell/issues/2134) | “NOT IDENTIFIABLE” already exists. The red text on `(255,170,170)` is 2.21:1. Recolor the text to ≥ 4.5:1 on that pink and on white, selected, and hover. | Every row state meets 1.4.3, and the words still say the result. |

**Result (2026-10-07):**

SpringSaLaD. Each visible site type gets one of four marks on the sprite: a ring, a dashed
ring, a cross, or a square. The mark is black or white against the fill so it remains in
grayscale. Alt+I on a focused site checkbox isolates that type, and the canvas then draws
the site-type name beside those sites. `SpringSaladSiteCueTest` paints the same red fill
with a ring and with a cross and the grayscale rasters differ, and isolating a demo
trajectory leaves one checkbox selected. `Colors.java` and the solver writers were not
edited. `colorForName` still returns each stored palette color. `SpringSaladViewerColorTest`,
`SpringSaladSpeciesLegendTest`, and `SpringSaladViewerRenderTest` passed in the same run.

**SpringSaLaD update (2026-10-07, closeout C2):** four marks meant a fifth co-visible
site type repeated a mark. Now, when more types are visible than there are marks, every
sprite also carries its site-type name (`captionFor`), so a repeated mark never has to
be told apart by hue. Runs without `SiteIDs.csv` caption from color and radius in the
legend's fallback shape. New tests: five captions on one fill and one mark are five
different grayscale rasters; the name decision turns on at five visible types and off at
four; a rendered five-type scene with one shared fill paints caption ink past the last
sprite while the four-type render does not. The palette test still resolves every
`Colors` name to its stored color.

ROI editor. The Domain Regions list already shows each name. The list label now reads
`Domain Regions. Selected: <name>`, including `Selected: none` when nothing is selected.
The highlight edge is a black/white checker drawn in the composite. `RoiSelectionCue.isBoundary`
reads the mask and the test asserts the byte array is unchanged. Create is the Add Domain
button. Enter on the list fires the same find action as a double-click. With the image
focused, the arrow keys call `drawHighlight` or the fill property, the same methods the
mouse uses. Paint, Erase, Fill, and the image have accessible names. `RoiSelectionCueTest`
constructed the panel, added a domain named cytosol, and checked the readout, the list,
and the arrow-key binding. A before-and-after raster of a painted ROI was not saved. The
paint path is the existing `drawPaint`.

**ROI update (2026-10-07, closeout C3):** `RoiKeyboardStrokeTest` now paints the stroke.
Two identically prepared editors run the same stroke — two RIGHT arrow actions on one,
a press plus a one-step drag on the other — and the ROI composite pixel buffers are
byte-for-byte equal: both paths reach `drawHighlight`/`drawPaint` with the same brush
size, so the keyboard path writes no different encoding. The same test class creates
region `cytosol` and selects it by name, with no use of its color.

FRAP table. `Color.red` on `(255,170,170)` is 2.21:1, and `#A40000` on that pink is 4.49:1.
The not-identifiable ink is `#5C0000`: 7.96:1 on the pink, 14.43:1 on white, and 13.84:1 on
the hover paper `#FDFCDC`. This renderer has no separate hover background. Unselected
not-identifiable group cells keep the pink. Selected rows use the look-and-feel selection
foreground, because `#5C0000` on the Mac selection blue `rgb(8,74,217)` is 2.06:1. The test
sets that blue and white and still reads `NOT IDENTIFIABLE`. `AnalysisTableContrastTest`:
2 tests, 0 failures. Java 17, 2026-10-07T16:39:27-04:00, with the SpringSaLaD and ROI tests
above: 24 tests, 0 failures.

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

**Result (2026-10-07):** The script was not run. This session had one person on macOS
(darwin 27.0.0) and no second reviewer. Windows and Linux were not used. Sim Daltonism and
Color Oracle were not used for this script. A missing platform is not a pass, so §6 R10
stays open.

---

## 4. Tests to add or re-run

Existing tests stay. Add the ones this plan introduces. Run Java tests with Java 17 and
`mvn test -pl <module> -am` so the reactor uses this tree.

| Check | Pass |
|---|---|
| Re-run `ColorAccessibilityTest`, `Plot2DPanelAccessibilityTest`, `PlotPaneAccessibilityTest`, `Langevin` tests, `DisplayAdapterServiceColormapTest`, `ExportSpecsColorModeFallbackTest`, `GuiConstantsContrastTest`, `SimulationConsolePanelAccessibilityTest` | No new failures |
| `StatusTextContrastTest`, `StructureMappingUnmappedTextTest`, `MathOverridesWarningButtonTest`, `DefineRoiStatusTextTest` | The six R4 text sites are ≥ 4.5:1 on white, the alternate row, the hover paper, and the selection ink actually painted. Selected rows do not use `#A40000` on the Mac selection blue |
| `MyRendererStatusTextTest` | Every merge-tree status word is on the label for attribute and element nodes; inks are ≥ 4.5:1 on white, `#e8edff`, and `#FDFCDC`; a selected row keeps `Tree.selectionForeground`. 3 tests, 0 failures, 2026-10-07 |
| `SpringSaladSiteCueTest` (extended) | Five co-visible types on one fill and one mark are five different grayscale rasters; names appear when more than four types are visible; a rendered five-type scene paints caption ink past the last sprite; palette colors still resolve to `Colors`. 7 tests, 0 failures, 2026-10-07 |
| `RoiKeyboardStrokeTest` | The keyboard stroke and the mouse stroke leave byte-identical ROI pixel buffers; a region is created and selected by name. 2 tests, 0 failures, 2026-10-07 |
| `MolecularTypeErrorMarkTest`, `MeshSpecificationValidationTest`, `SolverOptionsValidationTest`, `TableCellEditorAutoCompletionTest`, `SpatialMatchLabelTest`, `ConstraintPanelReachabilityTest` | The glyph says `error`, validation sentences stay and clear, match rows say `match` when selected, and `ConstraintPanel` is constructed only from its own `main` |
| `AnalysisTableContrastTest` | `#5C0000` is 7.96:1 on `(255,170,170)`, 14.43:1 on white, and 13.84:1 on `#FDFCDC`. Selected rows use white on `rgb(8,74,217)`. The cell still says `NOT IDENTIFIABLE`. 2 tests, 0 failures, 2026-10-07 |
| New: client export request when the server reports no Cividis | `ExportColorModeNegotiationTest` stores BlueRed and builds the notice. A deployed old server and a deployed new server have not been run. |
| `pytest webapp-viewer/test` plus a Cividis `setTable` check on the wasm bundle | The full pytest suite was not run. The fv3d bundle run on 2026-10-07 showed Cividis stops `rgb(0,34,78)` … `rgb(254,232,56)` on the kymograph and the same ramp on the surface. After both maps use `setTable`, Rainbow returns `rgb(0,0,255)` … `rgb(255,0,0)` on both. Closeout C8 matched live probes P1–P3 by name in grayscale and a Machado protan filter |
| `webapp-ng` contrast on badge and status colors | Running app, closeout C7, 2026-10-07. Computed styles at 976px and 375px match the CSS table. Lowest badge is Archived, 5.60:1. Footer link 9.53:1, and Enter opened `https://accessibility.uconn.edu/`. No dark theme ships |
| Help search for color-only instructions | Local HTML and JavaHelp, closeout C10, 2026-10-07: Observables names the written state and the question mark, and the properties editor shows `p` and `?`. The pages on vcell.org still say “shown in green.” `webhelp-deploy.yml` was not run |

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

- [x] R1 has a written BlueRed review, and every failed item in it is fixed. Opened window, closeout C4, 2026-10-07. Pointer and Right arrow both read `Value = 4.25`. BlueRed stays the default.
- [ ] R2 has a real old-server and new-server export run. Labels match the pixels.
- [x] R3 identifies every test region with no hover and no hue. `GeometryRegionListTest`, 2026-10-07. Adjacent contrast-colormap fills are 1.51:1 and 1.71:1, so the name is the cue. Opened window, closeout C5: Down arrow named cytosol and nucleus, readout `Region: nucleus`.
- [x] R4 has no remaining status or data text under 4.5:1, including selected rows. The six listed sites are measured. `ConstraintPanel` is not a shipped screen (R5). `AnalysisTableRenderer` was recolored in R9. `MyRenderer` now leads the label with the status word and uses ≥ 4.5:1 inks (closeout C1, 2026-10-07).
- [x] R5 errors, match rows, and the glyph error are identifiable without hue. `ConstraintPanel` is constructed only from its own `main`. Tests listed in the R5 result, 2026-10-07. Opened mesh panel, closeout C6: the sentence stayed after the Error dialog closed and cleared after X was set to `10`.
- [ ] R6: local JavaHelp was followed on 2026-10-07 (Observables: state name and question mark; the properties editor shows `p` and `?`). The page published at vcell.org still says a defined state is “always shown in green.” `webhelp-deploy.yml` was not run.
- [x] R7: field-viewer Cividis and Rainbow were exercised on the VTK bundle for `fv3d`, and the filtered palette review matched every named trace. 2026-10-07. Closeout C8: live fenics2d probes P1 solid, P2 long dash, and P3 dotted, each name on its line, in grayscale and a Machado protan filter. Sim Daltonism and Color Oracle were not installed. The full `pytest webapp-viewer/test` suite was not run.
- [x] R8: badge text is at least 4.5:1 in the default theme. `webapp-ng` ships no dark theme. Badge words are the state. Running app, closeout C7, 2026-10-07: computed styles at 976px and 375px match the CSS table (lowest Archived 5.60:1). Footer link opened `https://accessibility.uconn.edu/`. The link is not a 1.4.1 pass.
- [x] R9: SpringSaLaD marks differ in grayscale and Alt+I isolates a site; when more than four types are co-visible every sprite also carries its type name (closeout C2). ROI selection is the region name, the boundary checker does not write the mask, and the keyboard stroke and mouse stroke leave byte-identical ROI pixel buffers (closeout C3). FRAP `#5C0000` clears the pink, white, and hover paper, and selected rows keep the selection ink. `Colors.java` and the solver writers were not edited. 24 tests, 0 failures, 2026-10-07T16:39:27-04:00; C1–C3 run 32 tests, 0 failures, 2026-10-07T17:30:10-04:00. A painted-ROI before/after raster was not saved.
- [ ] R10: two reviewers, three desktop platforms, every script item yes. The script was not run. Windows and Linux were not used.
- [x] Every finding row of audit PR #2141 is reproduced with its disposition in the evidence for this build (`docs/accessibility/evidence/2026-10-c12-audit/`, closeout C12, 2026-10-08). The PR itself is not merged.

When those boxes are true, this branch meets **WCAG 2.1 AA color criteria 1.3.3, 1.4.1,
1.4.3, and 1.4.11** for the Swing client under WCAG2ICT, and **WCAG 2.2 AA for those same
four criteria** on `webapp-viewer`, `webapp-ng`, and published help.

That sentence is the whole claim. It does not say VCell conforms to WCAG 2.2 AA, and it
does not close #1603.
