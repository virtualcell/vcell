# Color-audit rows for this build (closeout C12, 2026-10-08)

PR [#2141](https://github.com/virtualcell/vcell/pull/2141) was not merged here.
Its rows are reproduced below instead, as the closeout plan allows. Source is
`docs/accessibility/color-audit.md` on branch `docs/1605-color-audit` at
`d41affa75f`, which audited `master` at `5407a5548c` (2026-09-30). This build is
`chore/vcell#1605` at `d1077b2ec5` plus this uncommitted C12 record. The audit's
77-file appendix is not copied; it is unchanged on that branch.

"Manual review pending" means finish-plan R10 item work that has not been run.

## Results viewers

| ID | Audit verdict | This build |
|---|---|---|
| P1 line plots | FAIL 1.4.1, 1.4.11 | `CVD_SAFE_LIGHT` plus `seriesDash`, one `Path2D` per curve, legend stroke follows the curve, status text names the series. Manual 8.7 still pending. |
| P2 legend | FAIL 1.4.1 | Legend icon draws the curve's dash and marker. Manual 8.7 still pending. |
| P3 parameter estimation | FAIL 1.4.1 | Same palette and styles as P1. Manual 8.7 still pending. |
| P4 Langevin | GAP palette | Both panels use `CVD_SAFE_LIGHT`. Line-plot screenshots are stale. Manual review still pending. |
| C1 colormap | GAP palette | Cividis is registered after Gray and BlueRed. BlueRed stays the default. Opened BlueRed window, closeout C4. Old export servers still need R2. |
| C2 hover readout | PASS | Pointer and Right arrow both read `Value = 4.25` on the opened window (C4). |
| C3 out-of-range | Follow-up | Special swatches carry their names (`BM`, `AM`, `NN`, `ND`, `NR`) on the opened window (C4). |
| W1 field viewer | FAIL 1.4.1, 1.4.3, GAP | Cividis and Rainbow agree after `setTable` (R7). Live probes P1–P3 match by name in grayscale and a Machado protan filter (C8). Full pytest not run. |
| X1 SpringSaLaD | Follow-up #2132 | More types than marks puts the type name on every sprite (C2). Saved colors unchanged. R9 stays checked. |
| X3 FRAP | 1.4.1 pass, FAIL 1.4.3 | `#5C0000` clears the pink, white, and hover paper. The cell still says `NOT IDENTIFIABLE`. R9 stays checked. |

## Model viewers

| ID | Audit verdict | This build |
|---|---|---|
| D1 reaction edges | FAIL 1.4.1 | Selected stroke is 2.5 px. Shape-or-label confirmation where width is not enough still needs the manual review. |
| D2 other edges | Pass on paper | Same stroke change as D1, for consistency. |
| D3 glyphs | PASS | No change recorded. |
| D4 glyph error outline | Follow-up #2140 | The same error is listed with an icon in the issue tables. |
| G1 geometry | FAIL 1.4.1 | Every region is named from the list; Down arrow named cytosol and nucleus on the opened window (C5). R3 stays checked. |
| G2 palettes | 1.4.1 pass, weak palette | Tracked in #2139. Adjacent fills are 1.51:1 and 1.71:1, so the name is the cue. |
| X2 ROI editor | Follow-up #2133 | The keyboard stroke and the mouse stroke leave byte-identical ROI buffers, and a region is created and selected by name (C3). R9 stays checked. |

## Tables, forms, status and text

| ID | Audit verdict | This build |
|---|---|---|
| S1 issue decoration | PASS | Icons differ in shape. No change recorded. |
| S2 overrides | 1.4.1 pass, FAIL 1.4.3 | `#A40000` and `#8A4B00` with the severity in the words. Measured in R4. |
| S3 console | FAIL 1.4.3 | Lines start with `[Error]`, `[Warning]`, or `[Stopped]`. R5 stays checked. |
| S4 job status | PASS | Status text and tooltip sit next to every icon. No change recorded. |
| S5 validation | PASS | The sentence stays after the dialog closes and clears when the field is valid; tabbed through on the opened mesh panel (C6). R5 stays checked. |
| S6 constraints UI | Not reachable | `ConstraintPanel` is constructed only from its own `main`. R5 stays checked. |
| S7 match yellow / brown | Yellow FAIL 1.4.1 | A selected match row appends ` match`; yellow is painted only when the row is not selected. Tracked in #2138. |
| S8 other red text | 1.4.1 pass, FAIL 1.4.3 | The six sites use ≥ 4.5:1 inks, and `MyRenderer` leads the label with the status word (C1). Tracked in #2140. |
| H1 help | FAIL 1.3.3 | Local and JavaHelp pages name the written state and the question mark (C10). The copies on vcell.org still say "shown in green." R6 stays open. |

## Outside the Java GUI

| ID | Audit verdict | This build |
|---|---|---|
| X4 webapp | Follow-up #2135 | Running app at 976px and 375px matches the CSS table; lowest badge is Archived at 5.60:1. The footer link opened `https://accessibility.uconn.edu/`. R8 stays checked. |
| X5 look and feel | Tracked #1604, #1606 | Out of scope for the color criteria. No change recorded. |
