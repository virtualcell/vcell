# Phase 1.1 — `ColorUtil` CVD-safe series palette

This document specifies the code change for item 1.1 of [`.agents/uconn-color-blind-accessibility-verified.md`](../.agents/uconn-color-blind-accessibility-verified.md). It does not implement that change.

Item 1.1’s own acceptance check is unit test 8.2-a. The verified plan files that test as item 1.2, in a new class. This plan includes that class, because 1.1 is not done until 8.2-a passes. Plot wiring, the field viewer, and every other phase stay out of scope.

## Outcome

[`vcell-util/src/main/java/org/vcell/util/ColorUtil.java`](../vcell-util/src/main/java/org/vcell/util/ColorUtil.java) gains one palette and two accessors. Later phases (`Plot2DPanel`, the Langevin panels, `webapp-viewer`) call these instead of inventing their own colors or dash arrays. Nothing that already calls `generateAutoColor`, `TABLEAU20`, `DARK20`, or `COLORBLIND20` changes behavior.

## Why these six colors, in this order

The verified plan measured an exhaustive search over 24 established CVD-oriented colors. The best 6-color subset scores 15.5 on the separation metric; the best 8-color subset drops to 11.9. So identity for more than six series comes from dash pattern, not from a longer palette. Grayscale cannot separate these entries; the dash sequence is what carries identity there (WCAG 1.4.1 technique G111). Contrast on white is what satisfies 1.4.11 for a line on a white plot.

`CVD_SAFE_LIGHT`, greedy maximum-separation order. Do not reorder. Index 0 is black and is the solid series once plots call `seriesDash`.

- `#000000` — 21.0:1 on white
- `#999933` — 3.02:1 (the tight one; a comparison of `>= 3.0` passes, a rounded-down `> 3` check or a 1-decimal truncation fails)
- `#004488` — 9.62:1
- `#8C510A` — 6.37:1
- `#0072B2` — 5.19:1
- `#CC6677` — 3.66:1

Published separation, for the test’s margin: minimum CAM02-UCS ΔE′ is 21.0 / 17.5 / 16.3 / 18.9 (normal / protan / deutan / tritan). The JUnit metric is not CAM02. It is CIELAB ΔE76 after a Machado 2009 severity-1.0 simulation, and that minimum is at least 16.3, so a threshold of 15 has slack.

## API to add

Place the constant and the two methods together, immediately after the `COLORBLIND20` field, so the warning and the replacement sit in one place. Match the file’s existing style: `new Color(r, g, b)` components, not `new Color(0x999933)`. The single-int constructor reads `0xAARRGGBB`, so `new Color(0x999933)` is a fully transparent color.

```java
/**
 * Categorical series colors for light backgrounds, in greedy maximum-separation
 * order. Every entry is at least 3:1 against white. The six entries are not
 * separable in grayscale; {@link #seriesDash(int)} carries that distinction.
 * Do not reorder: {@code seriesColor(0)} is black and is the solid series.
 */
public static final Color[] CVD_SAFE_LIGHT = {
    new Color(0x00, 0x00, 0x00), // #000000
    new Color(0x99, 0x99, 0x33), // #999933
    new Color(0x00, 0x44, 0x88), // #004488
    new Color(0x8C, 0x51, 0x0A), // #8C510A
    new Color(0x00, 0x72, 0xB2), // #0072B2
    new Color(0xCC, 0x66, 0x77)  // #CC6677
};

/** Palette color for series {@code i}, cycling every {@link #CVD_SAFE_LIGHT} entry. */
public static Color seriesColor(int i) { ... }

/**
 * Dash array for series {@code i}, in user-space units for {@code BasicStroke}.
 * {@code null} means solid. The returned array is a copy; callers may mutate it.
 * Cycles every four series-groups of six: solid, {6,3}, {2,2}, {8,3,2,3}.
 */
public static float[] seriesDash(int i) { ... }
```

`seriesColor`:

- `i < 0` throws `IllegalArgumentException`. Plot indexes are non-negative; a negative index should not wrap into a real series.
- Otherwise return `CVD_SAFE_LIGHT[i % CVD_SAFE_LIGHT.length]`. `Color` is immutable, so returning the palette entry is safe.

`seriesDash`:

- `i < 0` throws `IllegalArgumentException`.
- Let `group = (i / 6) % 4` (Java integer division, positive `i`).
- `group == 0` returns `null` (solid). `BasicStroke` treats a null dash array as a solid stroke; later phases pass this value straight through.
- `group == 1` returns a copy of `{6f, 3f}`.
- `group == 2` returns a copy of `{2f, 2f}`.
- `group == 3` returns a copy of `{8f, 3f, 2f, 3f}`.
- Store the three patterns as private static finals and return `Arrays.copyOf` so a caller cannot change the next call’s result.

For `i` in `0..23` the pair `(seriesColor(i), seriesDash(i))` is unique: color period 6, dash period 4 groups, and `6 * 4 = 24`. At `i == 24` the pair repeats `i == 0`. That repeat is intended.

| i | color index | dash |
|---|---|---|
| 0–5 | 0–5 | null (solid) |
| 6–11 | 0–5 | 6, 3 |
| 12–17 | 0–5 | 2, 2 |
| 18–23 | 0–5 | 8, 3, 2, 3 |
| 24 | 0 | null |

## What must not change

Leave the method bodies and the array contents of `generateAutoColor`, `TABLEAU20`, `DARK20`, and `COLORBLIND20` byte-for-byte the same, including the `System.out.println` debug branch inside `generateAutoColor`. Also leave `LIGHT20` and `OTHERS20` alone; the spec does not mention them, and they are not part of this task.

Callers that depend on exact `generateAutoColor` output:

- [`vcell-core/src/main/java/org/vcell/solver/smoldyn/SmoldynFileWriter.java`](../vcell-core/src/main/java/org/vcell/solver/smoldyn/SmoldynFileWriter.java) line 219 writes those colors into solver input (`seed 5`).
- FRAP and vmicro ROI panels call it with `seed 0`: `SubPlotPanel`, `EstParams_ReacBindingPanel`, `EstParams_OneDiffComponentPanel`, `EstParams_TwoDiffComponentPanel`, `RoiForErrorPanel`, `DefineROI_RoiForErrorPanel`, `DependentROIPanel`, `OptModelParamPanel`.
- [`Plot2DPanel.java`](../vcell-client/src/main/java/cbit/plot/gui/Plot2DPanel.java) line 1790 and [`MultisourcePlotPane.java`](../vcell-client/src/main/java/cbit/vcell/modelopt/gui/MultisourcePlotPane.java) line 80 also call it. Switching them is Phase 2, not this task.

`TABLEAU20` is still used by `MoleculeVisualizationPanel` (line 426) and `DARK20` by `ClusterVisualizationPanel` (line 209). Switching those is Phase 3.

Add Javadoc on `COLORBLIND20` only. State that it failed CVD validation and must not be used for new series colors. Cite the measurements so a later edit does not “fix” the plots by switching to it: among the first 8 colors, minimum CAM02-UCS ΔE′ is 2.9 under deuteranopia and 3.3 under tritanopia; 6 of 20 entries are below 3:1 on white; near-duplicates include `(213,94,0)`/`(200,55,0)` and `(0,114,178)`/`(0,90,160)`. Point readers at `CVD_SAFE_LIGHT` and `seriesColor`. The array itself stays identical.

## Test 8.2-a

New file [`vcell-util/src/test/java/org/vcell/util/ColorAccessibilityTest.java`](../vcell-util/src/test/java/org/vcell/util/ColorAccessibilityTest.java). Follow [`ArrayUtilsTest`](../vcell-util/src/test/java/org/vcell/util/ArrayUtilsTest.java): JUnit Jupiter, class-level `@Tag("Fast")`, no new Maven dependencies. `vcell-util` already depends on `junit-jupiter`. The color-science code lives in the test, not in `ColorUtil`. Production code should not grow a Lab converter for one assertion.

Implement the metric the same way as the “JUnit-portable metric” block in [`.agents/cvd_analysis.py`](../.agents/cvd_analysis.py) (about lines 214–242). Copy those constants. Do not substitute a more precise sRGB-to-XYZ matrix; the published margin (minimum ΔE76 at least 16.3, threshold 15) was computed with these numbers.

- sRGB to linear: channel `c` in `0..1`; if `c <= 0.04045` then `c / 12.92`, else `((c + 0.055) / 1.055) ^ 2.4`. This is the same 0.04045 threshold WCAG 2.1 uses for relative luminance.
- Machado 2009 severity-1.0 matrices, applied to linear sRGB, then clipped to `[0, 1]` before XYZ:
  - protan: `[[0.152286, 1.052583, -0.204868], [0.114503, 0.786281, 0.099216], [-0.003882, -0.048116, 1.051998]]`
  - deutan: `[[0.367322, 0.860646, -0.227968], [0.280085, 0.672501, 0.047413], [-0.011820, 0.042940, 0.968881]]`
  - tritan: `[[1.255528, -0.076749, -0.178779], [-0.078411, 0.930809, 0.147602], [0.004733, 0.691367, 0.303900]]`
- XYZ from linear sRGB with `[[0.4124, 0.3576, 0.1805], [0.2126, 0.7152, 0.0722], [0.0193, 0.1192, 0.9505]]`.
- CIELAB D65 white point `0.95047, 1.0, 1.08883`. The f(t) branch is `t > 216/24389`. ΔE76 is Euclidean distance in Lab.
- WCAG contrast against white: relative luminance with the same linearization, coefficients `0.2126, 0.7152, 0.0722`, ratio `(Llighter + 0.05) / (Ldarker + 0.05)`. Compare with `>= 3.0` on the raw double. Do not round to one decimal first.

Assertions, one behavior per test method:

- `cvdSafeLightHasSixDocumentedColors` — length 6 and the six RGB triples above, in that order. This locks the order independently of the metric.
- `cvdSafeLightMeetsContrastOnWhite` — every entry `>= 3.0` against opaque white.
- `cvdSafeLightSeparatesUnderMachado` — pairwise minimum ΔE76 `>= 15` under protan, deutan, and tritan. Six colors means 15 pairs per simulation. Fail the message with the kind and the actual minimum so a regression says which simulation dropped.
- `seriesStylePairsUniqueForFirst24` — for `i` in `0..23`, the pair `(rgb, dash signature)` is unique. Signature is `"solid"` when the dash is null, otherwise the float values. Also assert the four group patterns at `i = 0, 6, 12, 18`, that `seriesColor(i)` equals `seriesColor(i % 6)`, and that `seriesColor(24)` and `seriesDash(24)` repeat index 0.
- `seriesDashReturnsDefensiveCopy` — mutate the array from `seriesDash(6)`; the next call still returns `{6, 3}`.
- `seriesIndexRejectsNegative` — `seriesColor(-1)` and `seriesDash(-1)` throw `IllegalArgumentException`.
- `tableau20First8FailsTheMetric` — the same predicate used above (every color `>= 3:1` on white, and minimum ΔE76 `>= 15` for all three simulations) is false for `TABLEAU20` entries `0..7`. Assert the predicate, not a pinned delta. The point is that the test can fail a bad palette, which is what makes a future edit to `CVD_SAFE_LIGHT` meaningful.
- `cvdSafeDarkPaletteMeetsContrastOnDarkBackground` — `@Disabled`, reason text naming #1604: enable when a dark-background palette constant exists. Empty body, or a comment that the assertion will mirror the white-background contrast test against that constant. This is the hook the verified plan puts on item 1.2; it belongs in this class so #1604 does not have to create the file.

## Verification

```bash
mvn test -pl vcell-util -Dgroups=Fast -Dtest=ColorAccessibilityTest
```

Pass means every assertion in 8.2-a holds, including the `TABLEAU20` reference failure, and the disabled method does not run. Then run the module Fast group without `-Dtest` to confirm the new class is picked up by `@Tag("Fast")` and that existing `vcell-util` tests still pass:

```bash
mvn test -pl vcell-util -Dgroups=Fast
```

No Python, no Docker, no `-am` (nothing else in the reactor changes). Do not run MathGen or Smoldyn golden tests for this task; those are the regression that would fire if `generateAutoColor` were edited (check 8.8-d), and this change does not edit it.

## Out of scope

- `Plot2DPanel.getVisiblePlotPaint`, strokes, `Path2D` dashing, markers, legends (Phase 2).
- `MoleculeVisualizationPanel` / `ClusterVisualizationPanel` / `PlotRenderers` (Phase 3).
- `webapp-viewer` `SERIES_COLORS` (Phase 6). The JavaScript mirror of these six hex values happens there, not here.
- Any edit to solver input, FRAP ROI colors, or colormap registration.

## Risks

Low, and only if the constraints above are missed.

- Reordering the palette silently changes which series is black once Phase 2 lands. The order test prevents that.
- `new Color(int)` alpha bug would ship transparent “black” that still passes an RGB-component test if the test reads `getRed/Green/Blue` (those ignore alpha) and fail on screen. Assert `getAlpha() == 255` in the color-lock test, or construct expected colors with the three-arg constructor and `assertEquals` on all four components.
- A contrast check that rounds `#999933` down to 3.0 and then requires `> 3` fails a correct palette. Use `>= 3.0` on the unrounded ratio.
- Putting the Machado math in `ColorUtil` widens the production API for no caller. Keep it in the test.
