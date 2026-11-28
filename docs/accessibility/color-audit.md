# VCell color and color-blindness audit (#1605)

The evaluation that [#1605](https://github.com/virtualcell/vcell/issues/1605) asks for: *"verify that
all views, menus, windows, etc. are colorblind-safe"*. It records every place the GUI gives color a
meaning, whether that meaning survives without color, and where each gap is being fixed. It is the
color part of the UConn Health accessibility epic
[#1603](https://github.com/virtualcell/vcell/issues/1603).

- **Audited revision:** `5407a5548c` (`master`, 2026-09-30).
- **Scope:** the desktop client (`vcell-client`, and the GUI code in `vcell-core`, `vcell-util`,
  `vcell-vmicro`), plus the browser field viewer (`webapp-viewer`) that the client serves for
  *View in 3D*. The public website (`webapp-ng`) was checked only far enough to file a follow-up.
- **Not in scope:** text size (#1606), cross-OS appearance and dark mode (#1604), keyboard and
  screen-reader access.

## What "colorblind-safe" is measured against

UConn's [Digital Accessibility Policy](https://policy.uconn.edu/2019/08/02/digital-accessibility-policy/)
covers software UConn develops. Its [procedures](https://accessibility.its.uconn.edu/policy-procedures/)
name **WCAG 2.1 AA** as the technical standard, due 2027-04-26. The procedures scope that standard to
websites, mobile apps and social media. It is used here as the benchmark for the desktop client too,
because it is the only standard UConn names. For color, that means four success criteria:

| Criterion | What it requires |
|---|---|
| [1.4.1 Use of Color](https://www.w3.org/TR/WCAG21/#use-of-color) (A) | Color is not the *only* visual means of conveying information, showing state or distinguishing an element. A lightness difference of at least 3:1 counts as a second means ([Understanding 1.4.1](https://www.w3.org/WAI/WCAG21/Understanding/use-of-color.html)). |
| [1.4.3 Contrast (Minimum)](https://www.w3.org/TR/WCAG21/#contrast-minimum) (AA) | Text at least 4.5:1 against its background (3:1 for large text). |
| [1.4.11 Non-text Contrast](https://www.w3.org/TR/WCAG21/#non-text-contrast) (AA) | Graphics needed to understand content, such as the lines in a graph, at least 3:1 against adjacent colors. Heat maps are exempt as an *essential* presentation ([Understanding 1.4.11](https://www.w3.org/WAI/WCAG21/Understanding/non-text-contrast.html)). |
| [1.3.3 Sensory Characteristics](https://www.w3.org/TR/WCAG21/#sensory-characteristics) (A) | Instructions don't depend on color alone ("the values shown in red"). |

UConn's [color training](https://accessibility.its.uconn.edu/self-paced-learning/colors/) adds a
recommendation: avoid red/green and red/black pairings. It is guidance, not a separate rule. It is
reported below wherever VCell uses one of those pairings.

## Method

1. **Discovery.** Repository-wide search for component and drawing calls that set a chromatic color
   (`setForeground`, `setBackground`, `setColor`, `setPaint`, borders, `StyleConstants`), color
   tables and palettes, and colormap construction. That search found 77 production files; each one
   is dispositioned in [the appendix](#appendix--every-file-that-sets-a-chromatic-color).
2. **Tracing.** Each meaningful use was followed to where it is drawn and checked for a second,
   non-color cue: text, icon shape, border presence, bold, fill, dash, or a readout.
3. **Measurement.** WCAG contrast ratios were computed from the source colors. Color-vision
   deficiency was simulated with the Machado, Oliveira and Fernandes (2009) model: protanopia,
   deuteranopia and tritanopia at full severity, the anomalous forms at half severity, and
   grayscale. Perceptual difference is the distance in the CAM02-UCS color space (ΔE′). Below 10,
   two thin lines are hard to tell apart; below 5 they look the same. Plot colors were measured
   exactly as VCell generates them, including its seeded random generator.
4. **Limits.** This is a static audit with computed measurements. The screenshot review under
   simulated color blindness, on each OS, belongs to the remediation work and is a closing criterion
   for #1605.

## Summary

A row can appear under more than one verdict.

| Verdict | Rows | Meaning |
|---|---|---|
| **FAIL: 1.4.1** | P1, P2, P3, W1, D1, G1, S7 | Meaning is carried by hue alone |
| **FAIL: 1.4.3 / 1.4.11** | P1, W1, X3, S2, S3, S8 | Contrast below threshold |
| **FAIL: 1.3.3** | H1 | Help text gives color-only instructions |
| **GAP: #1603 palette** | P4, C1, W1 | Not a WCAG failure, but #1603 explicitly asks for color-blind palettes |
| **PASS** | C2, D2, D3, S1, S4, S5 | Color is backed by a non-color cue, or the lightness difference is at least 3:1 |
| **FOLLOW-UP** | X1, X2, X3, X4, S5, S6, S7, S8, G2, C3, D4 | Tracked outside the #1605 remediation (see Disposition) |

The model viewers and results viewers named in #1605 hold every 1.4.1 failure except S7. The shared
table, validation and job-status machinery (S1, S4, S5) already pairs color with text or icons.
Each finding with a *Remediation* disposition is fixed under #1605 with its own automated test.

## Findings

IDs are stable; the remediation and its tests refer to them.

### Results viewers

| ID | View · code | How color is used | Verdict | Evidence | Disposition |
|---|---|---|---|---|---|
| P1 | Line plots: `cbit/plot/gui/Plot2DPanel.java` (`drawLinePlot`, `getVisiblePlotPaint`), in 21 production panels: ODE and PDE time plots, parameter estimation, BioNetGen, FRAP, kymograph, electrical stimulus | Curves differ only by hue: every curve is a 1.5 px solid line, and data points are 2–3 px circles. Colors come from `ColorUtil.generateAutoColor(n, background, 0)`, a seeded random generator with no color-blindness check. | **FAIL: 1.4.1**, **FAIL: 1.4.11** | At 6 curves, the worst protan ΔE′ is 3.9. At 5 curves, one line is `(4,255,236)`, which is 1.27:1 on the white background. The crosshair readout shows x,y but not the curve name. | Remediation: per-curve dash pattern and marker, drawn as one path per curve so dashes survive dense data; a measured palette |
| P2 | Plot legend: `cbit/plot/gui/PlotPane.java` (`LineIcon`, `updateLegend`) | 50×2 px solid swatch in the curve color | **FAIL: 1.4.1** | The legend-to-curve match needs hue | Remediation: legend icon draws the curve's dash and marker |
| P3 | Parameter-estimation plot and list: `cbit/vcell/modelopt/gui/MultisourcePlotPane.java` | Own `generateAutoColor` palette. Data is drawn as points and model output as lines. | **FAIL: 1.4.1** (within each group) | Same generator as P1 | Remediation (same palette and styles as P1) |
| P4 | Langevin molecule and cluster plots: `cbit/vcell/solver/ode/gui/{Molecule,Cluster}VisualizationPanel.java`, `cbit/plot/gui/PlotRenderers.java` | `ColorUtil.TABLEAU20` / `DARK20` palettes; curves hue-only; hovering a legend entry dims the other curves | **GAP: #1603 palette** (1.4.1 is partly met by the hover) | Worst protan ΔE′ is 1.6 for both palettes | Remediation: measured palette; dash styles |
| C1 | Spatial image, kymograph, exported images and movies: `cbit/image/DisplayAdapterService.java` (`createBlueRedColorModel0`), registered in `PDEDataContextPanel`, `KymographPanel`, the export server's `PDEOffscreenRenderer` and `RasterExporter`, `IMGExporter`, `vcell-vmicro` `DisplayImageOp` | *BlueRed* rainbow (dark blue → cyan → green → yellow → red); *Gray* is the only alternative | **GAP: #1603 palette**. Not a WCAG failure: the heat map is exempt from 1.4.11, and the hover readout gives the value (C2). | Lightness is not monotonic: J′ peaks at yellow (97) and ends at 60 for red. Two data values 60 steps apart measure ΔE′ 0.2 under protanopia and 0.0 in grayscale. | Remediation: add an opt-in *Cividis* map, registered once for client **and** export server. The server throws on an unknown map name today. BlueRed stays the default pending a decision. |
| C2 | Spatial-image hover readout: `cbit/image/gui/ImagePlaneManagerPanel.java` (`updateInfo`) | Text readout of value, coordinates, volume and membrane names | **PASS** | This is the non-color route that keeps C1 from being a 1.4.1 failure | — |
| C3 | Out-of-range colors: `DisplayAdapterService.createBlueRedSpecialColors` | Below-minimum is black, next to the map's darkest blue | FOLLOW-UP | Black vs `(0,0,128)` is 1.31:1. The legend labels these colors in text. | Covered by the colormap work (a new map needs its own out-of-range colors) |
| W1 | Browser field viewer: `webapp-viewer/viewer.js` (lookup table at `:792`, kymograph at `:2329`, `SERIES_COLORS` at `:1478`), `webapp-viewer/index.html` | Rainbow lookup table. Probe time courses are hue-only (the probe list is labeled). Several text colors are low contrast. This is the only VCell surface that follows OS dark mode. | **FAIL: 1.4.1**, **FAIL: 1.4.3**, **GAP: #1603 palette** | `#2a7` text on white: 2.96:1. White on `#d70`: 3.13:1. `.status.err` on dark `#121212`: 3.44:1. `SERIES_COLORS`: worst deutan ΔE′ 3.2. | Remediation: Cividis option, dashed probe traces, text colors to 4.5:1 or better |
| X1 | SpringSaLaD particle viewer: `org/vcell/util/springsalad/Colors.java`, `SpringSaladViewerCanvas.java` | Site type is shown by sphere color. The colors are model data, saved with the model and written to SpringSaLaD input files. A species legend with show/hide toggles exists. | FOLLOW-UP | Default site order (red, blue, lime, orange…): worst deutan ΔE′ 2.2 | [#2132](https://github.com/virtualcell/vcell/issues/2132) (related: [#2072](https://github.com/virtualcell/vcell/issues/2072)) |
| X3 | FRAP parameter analysis: `cbit/vcell/microscopy/gui/estparamwizard/AnalysisTableRenderer.java` | Non-identifiable results: the text **NOT IDENTIFIABLE**, plus red text on a pink background. (A code comment says "significant in green", but the code draws no green.) | 1.4.1 **PASS** (text). **FAIL: 1.4.3.** | Red on `(255,170,170)`: 2.21:1 | [#2134](https://github.com/virtualcell/vcell/issues/2134) |

### Model viewers

| ID | View · code | How color is used | Verdict | Evidence | Disposition |
|---|---|---|---|---|---|
| D1 | Reaction diagram edges: `cbit/vcell/graph/ReactionParticipantShape.java` (`paintSelf`) | A selected edge turns red; edges of a selected species turn dark red; all others are black. Selection is shown by color only. Catalyst edges are already dashed. | **FAIL: 1.4.1** | Dark red `(178,0,0)` vs black: **2.89:1**, below the 3:1 lightness allowance. Under protanopia it drops to about 2.0:1. Red/black is the pairing UConn's guidance warns about. | Remediation: thicker stroke for selected and related edges |
| D2 | Other diagram edges: `cbit/gui/graph/EdgeShape.java`, `cbit/vcell/graph/RuleParticipantEdgeDiagramShape.java` | Selected edge red, unselected black | 1.4.1 PASS on paper (red vs black is 5.25:1). UConn red/black guidance applies. | — | Remediation: same stroke change as D1, for consistency |
| D3 | Species and molecule glyphs: `SpeciesContextShape`, `MolecularTypeLargeShape`, `MolecularComponent{Large,Small}Shape` (`cbit/vcell/graph`) | Plain species green vs rule-based blue; component error red vs normal yellow. A selected species gets a raised label box. Molecule names are drawn as text. | **PASS** | Green vs blue: 6.5:1 lightness difference. Red vs yellow: 3.7:1. | — |
| D4 | Error outline on rule-based molecule glyphs: `MolecularTypeLargeShape.java` (around `:666`–`:690`) | Dark red outline vs dark gray outline | FOLLOW-UP | 1.43:1, hue only on the glyph. The same error is listed with an icon in the issue tables. | [#2140](https://github.com/virtualcell/vcell/issues/2140) |
| G1 | Geometry viewer image: `cbit/vcell/geometry/gui/GeometryViewer.java` (`setColorMap`, `refreshSourceDataInfo`), with the `ImagePlaneManagerPanel` readout | Each subvolume is a color. The hover readout shows only the numeric handle, and the subvolume table shows name and swatch but no handle. | **FAIL: 1.4.1** | Region-to-subvolume mapping is only by color. The handle-color palette measures deutan ΔE′ 2.0 (handles 1 and 4). | Remediation: the hover readout names the subvolume |
| G2 | Subvolume and CSG palettes: `DisplayAdapterService.createContrastColorModel`, used by `GeometrySubVolumeTableCellRenderer`, `CSGObjectTreeCellRenderer`, `StructureMappingTableModel`/`Renderer`, `GeometrySummaryPanel` | Hue-cycled palette. Every swatch sits next to its name. | 1.4.1 **PASS** (text). Palette weak. | Handles 0–7: worst deutan ΔE′ 2.0; 3 of 8 colors below 3:1 on white | [#2139](https://github.com/virtualcell/vcell/issues/2139) |
| X2 | Image-based geometry ROI editor: `cbit/vcell/geometry/gui/OverlayEditorPanelJAI.java` (`CONTRAST_COLORS`), `ROIMultiPaintManager` | ROI color sequence: red, green, blue… The ROI combo box shows swatch and name. | FOLLOW-UP | Red and green are adjacent defaults. Whether the canvas names the ROI under the cursor was not verified. | [#2133](https://github.com/virtualcell/vcell/issues/2133) |

### Tables, forms, status and text

| ID | View · code | How color is used | Verdict | Evidence | Disposition |
|---|---|---|---|---|---|
| S1 | Issue decoration in every model table: `org/vcell/util/gui/DefaultScrollTableCellRenderer.issueRenderer` | Red or orange row border, **plus** a shape-coded icon (error ⊗, warning ⚠) and a tooltip | **PASS** | The icons differ in shape | — |
| S2 | Parameter overrides table (simulation editor and summary): `cbit/vcell/solver/ode/gui/MathOverridesTableCellRenderer.java:54` | Changed values in red text. The override column being filled is the non-color cue. | 1.4.1 **PASS**. **FAIL: 1.4.3.** | 4.00:1 on white; 3.43:1 on the alternating row `#e8edff` | Remediation: accessible error-text color |
| S3 | Network-generation console: `cbit/vcell/client/desktop/biomodel/SimulationConsolePanel.java:127-143` | Error, Warning and Stopped are all `Color.RED`. Warning (not bold) vs normal (black) differs by hue only. | **FAIL: 1.4.3**, UConn red/black guidance | 4.00:1 on white | Remediation: `[Error]`/`[Warning]`/`[Stopped]` prefixes and compliant colors |
| S4 | Job status: `cbit/vcell/client/desktop/ViewJobsPanel.java`, `org/vcell/util/gui/StatusIcon.java` | Status icon colors, always next to the status **text** (`SchedulerStatus.getDescription()`) and a tooltip. Filter icons sit beside text check boxes. | **PASS** | — | — |
| S5 | Input validation: `GuiConstants.ProblematicTextFieldBorder` in `OutputOptionsPanel`, `MeshSpecificationPanel`, `StochSimOptionsPanel`; `TableCellEditorAutoCompletion`; `EditorScrollTable` | A red border, but only **after** a text error dialog or an automatic error tooltip (3 more usages are commented out) | **PASS** (3.3.1, 1.4.1) | After the dialog closes, the field's lasting state is a red border | [#2136](https://github.com/virtualcell/vcell/issues/2136) (optional inline message) |
| S6 | General constraints UI: `cbit/vcell/constraints/gui/*` (`ConstraintTableCellRenderer`) | Red text for inconsistent constraints | Not reachable | `ConstraintPanel` has no references outside its own package | [#2137](https://github.com/virtualcell/vcell/issues/2137) (remove the dead code) |
| S7 | Shared table renderer: `DefaultScrollTableCellRenderer.java:58,:101-111` | Read-only cells in brown `#964B00`; spatial-process rows that match the selection in yellow | Brown: **PASS**, marginally (3.3:1 vs black text). Yellow: **FAIL: 1.4.1**. | Yellow vs a white or `#e8edff` row: 1.07–1.09:1 | [#2138](https://github.com/virtualcell/vcell/issues/2138) |
| S8 | Other red text: `StructureMappingTableRenderer` ("Unmapped"), `MathOverridesPanel` (red button text), `MultiPurposeTextPanel` (error line, also bold), `DefaultScrollTableCellRenderer:126` (changed network constraint, also bold), `DefineROI_SummaryPanel`, `NumericsTestCellRenderer` | Red text on white or light gray, always with its own words or bold | 1.4.1 **PASS**. **FAIL: 1.4.3.** | `Color.red`: 4.00:1 on white, 3.45:1 on `#eeeeee` | [#2140](https://github.com/virtualcell/vcell/issues/2140) |
| H1 | In-app help: `vcell-client/UserDocumentation/originalXML/topics/…` (`simulationEditor.xml:27`, `simulations.xml:55`, `PP_Species.xml:23`, `PP_ReactionRulesEditor.xml:14`, `PathwayDiagramView.xml:45`, `PathLink.xml:24`, `SimResultsDataRange.xml:28`) | Instructions such as "values that have changed appear in red" and "sites in green must…" | **FAIL: 1.3.3** | — | Remediation: reword to name the non-color cue |

### Outside the Java GUI, or owned by sibling issues

| ID | Area | Finding | Disposition |
|---|---|---|---|
| X4 | Public website `webapp-ng` | Publication badges below 4.5:1: published 2.78, archived 2.16, current 2.68, unknown 4.35, public 3.12, private 3.68. Badges carry text, so 1.4.1 passes. The footer lacks the *Accessibility at UConn* link that the UConn procedures ask university websites to carry. | [#2135](https://github.com/virtualcell/vcell/issues/2135) |
| X5 | Look and feel: `cbit/vcell/client/VCellLookAndFeel.java` | The client uses the system look and feel (Aqua, Windows; Metal on Linux) and has **no dark mode**. It doesn't follow macOS dark appearance, so hard-coded white panels do *not* currently show white-on-white text. On macOS, every UI font is shrunk by 2 pt. | Already tracked: [#1604](https://github.com/virtualcell/vcell/issues/1604) (appearance, dark mode, OS high-contrast settings), [#1606](https://github.com/virtualcell/vcell/issues/1606) (font size) |

## Palettes, measured

Worst pair in each palette, as CAM02-UCS ΔE′ after simulation. Higher is better; below 10 means two
entries are hard to tell apart as lines. "≥3:1" counts entries with at least 3:1 contrast against
white.

| Palette (where used) | Normal | Protan | Deutan | Tritan | Grayscale | ≥3:1 on white |
|---|---:|---:|---:|---:|---:|---:|
| `ColorUtil.TABLEAU20`, first 8 (Langevin molecules) | 22.9 | 1.6 | 4.3 | 9.4 | 1.2 | 5/8 |
| `ColorUtil.DARK20`, first 8 (Langevin clusters) | 8.1 | 1.6 | 3.0 | 4.1 | 1.2 | 6/8 |
| `ColorUtil.COLORBLIND20`, first 8 (unused) | 14.7 | 5.9 | 2.9 | 3.3 | 0.2 | 6/8 |
| `generateAutoColor`, 6 curves (line plots) | 26.8 | 3.9 | 12.4 | 4.3 | 1.2 | 4/6 |
| `createContrastColorModel`, handles 0–7 (geometry) | 18.2 | 9.2 | 2.0 | 11.2 | 0.2 | 5/8 |
| `webapp-viewer` `SERIES_COLORS` (probes) | 9.3 | 5.3 | 3.2 | 3.3 | 0.1 | 8/12 |
| Okabe–Ito, for reference | 20.8 | 14.0 | 14.8 | 11.0 | 0.8 | 5/8 |

`COLORBLIND20` is **not** color-blind safe despite its name. It has near-duplicate pairs:
`(213,94,0)`/`(200,55,0)` and `(0,114,178)`/`(0,90,160)`. Under the 3:1-on-white constraint, the
best achievable worst-pair separation is 15.5 for 6 colors but only 11.9 for 8. Beyond about six
curves, **line style has to carry identity**; no palette can.

| Colormap | Lightness monotonic | Worst ΔE′ between values ≥60 steps apart (protan / deutan / tritan / gray) |
|---|---|---|
| VCell BlueRed | no | 0.2 / 2.2 / 4.5 / 0.0 |
| cividis | yes | 18.7 / 19.5 / 16.7 / 17.4 |
| viridis | yes | 17.5 / 17.3 / 16.8 / 16.2 |

## Appendix — every file that sets a chromatic color

These are all 77 production files matched by the discovery search. A file listed as *styling* uses
color with no meaning of its own: value text in blue, link labels, syntax highlighting, crop or
rubber-band outlines, drag-and-drop targets, grid lines. Color on text that already says what it
means counts as styling too.

| Disposition | Files |
|---|---|
| Findings above | `PlotPane`, `AbstractVisualizationPanel`, `LangevinClustersResultsPanel`, `SimulationConsolePanel`, `MathOverridesTableCellRenderer`, `MathOverridesPanel`, `StructureMappingTableRenderer`, `DefaultScrollTableCellRenderer`, `MultiPurposeTextPanel`, `TableCellEditorAutoCompletion`, `EditorScrollTable`, `MeshSpecificationPanel`, `GuiConstants`, `AnalysisTableRenderer`, `DefineROI_SummaryPanel`, `NumericsTestCellRenderer`, `ConstraintPanel`, `ConstraintTableCellRenderer`, `BoundsNode`, `GeneralConstraintNode`, `MolecularTypeLargeShape`, `MolecularComponentLargeShape`, `MolecularComponentSmallShape`, `RasterExporter` (colormap registration) |
| PASS: a non-color cue carries the meaning | `ScopedExpressionTableCellRenderer` (an error border appears where there was none, plus a tooltip), `CurveRenderer` (selected control point is filled), `MyRenderer` (XML compare: a different icon and tooltip for new and removed nodes), `LangevinPostProcessor` (mean is dots and a line, std dev is bars, both labeled), `HistogramPanel` (selected bars cyan vs gray, 3.15:1), `SpeciesPatternLargeShape` (a highlight fill and border appear), `SpeciesContextSpecLargeShape` (axis colors next to axis labels) |
| Styling only | `BioModelsNetPropertiesPanel`, `BioModelEditorModelPanel`, `BioModelEditorPathwayDiagramPanel`, `BioPaxObjectPropertiesPanel`, `MolecularTypePropertiesPanel`, `ObservablePropertiesPanel`, `ReactionPropertiesPanel`, `ReactionRuleKineticsPropertiesPanel`, `SpeciesPropertiesPanel`, `StructurePropertiesPanel`, `DatabaseSearchPanel`, `DocumentWindowAboutBox`, `SimulationListPanel`, `SimulationSummaryPanel`, `BNGOutputPanel`, `ExportedDataViewer`, `ElectrodePanel`, `VolumeSurfaceCalculatorPanel`, `ChomboMeshSpecificationPanel`, `ChomboSolverSpecPanel`, `DiffRateHelpPanel`, `CopasiOptimizationMethodsHelpPanel`, `LangevinOptionsPanel`, `DataValueSurfaceViewer`, `MathModelCellRenderer`, `MolecularTypeSpecsTableModel`, `FRAPReacOffRateParametersPanel`, `SurfaceRenderer`, `CopyOfImageAttributePanel`, `ImageAttributePanel`, `OverlayImageDisplayJAI`, `VFrap_OverlayImageDisplayJAI`, `ImageContainerPanelTool`, `ImagePaneScroller`, `ImagePaneScrollerTest` (developer harness), `BoxPanel`, `ReactionToolShapeIcon`, `SpeciesSizeShapeIcon`, `GraphModel`, `ReactionContainerShape`, `GeometryContextContainerShape`, `SpeciesPatternRoundShape`, `ExpressionCanvas`, `edu/rpi/graphdrawing/Node`, `vcell-vmicro` `EditorRuler`, `SwingInspector` (debug bridge, not user-facing) |

Named color literals are not the only way a meaning can be colored. Views that get their colors from
`new Color(...)` or from a palette, such as `Plot2DPanel` and `MultisourcePlotPane` through
`ColorUtil.generateAutoColor`, were found by tracing each palette and colormap (`ColorUtil`,
`DisplayAdapterService`, SpringSaLaD `Colors`, `OverlayEditorPanelJAI`, `webapp-viewer`), not by
the search above.
