# Phase 1.2 — `ColorAccessibilityTest`

This document specifies how to finish item 1.2 of [`.agents/uconn-color-blind-accessibility-verified.md`](../.agents/uconn-color-blind-accessibility-verified.md) so that item can be marked done. Item 1.1 is already done. This item is the Fast unit test that locks the palette from 1.1.

The class already exists at [`vcell-util/src/test/java/org/vcell/util/ColorAccessibilityTest.java`](../vcell-util/src/test/java/org/vcell/util/ColorAccessibilityTest.java). It was added with item 1.1 because 1.1's acceptance check is test 8.2-a. A previous run of the verify command passed: 50 Fast tests, 0 failures, 1 skipped. Do not rewrite that class. Confirm it against the checklist below, re-run the command in this document, and then record the result in the verified plan.

## What "done" means

Item 1.2 is complete when every row below is true and the verified plan says so.

| Requirement | Where it lives now |
|---|---|
| New test class, class-level `@Tag("Fast")` | `ColorAccessibilityTest` |
| WCAG 2.1 relative luminance and contrast, no new libraries | `linearize`, `relativeLuminance`, `contrastAgainstWhite` |
| Machado 2009 severity-1.0 matrices for protan, deutan, and tritan | `PROTAN`, `DEUTAN`, `TRITAN` |
| CIELAB ΔE76 after that simulation | `lab`, `labF`, `deltaE76`, `minDeltaE76` |
| Every `CVD_SAFE_LIGHT` entry ≥ 3.0:1 against white | `cvdSafeLightMeetsContrastOnWhite` |
| Pairwise minimum ΔE76 ≥ 15 under each of the three simulations | `cvdSafeLightSeparatesUnderMachado` |
| `(seriesColor(i), seriesDash(i))` unique for `i` in `0..23` | `seriesStylePairsUniqueForFirst24` |
| `TABLEAU20` entries `0..7` fail that same metric | `tableau20First8FailsTheMetric` |
| Disabled hook named `cvdSafeDarkPaletteMeetsContrastOnDarkBackground` | `@Disabled` method, reason names #1604 |
| `mvn test -pl vcell-util -Dgroups=Fast` passes and this class is in the run | Re-run during execution; then write the counts into the verified plan |

The "≈60 lines" figure in the verified plan is the size of the color-science core, not a cap. The file is longer because the metric helpers and the extra 1.1 locks (exact RGB and alpha, defensive dash copy, rejection of a negative index) live in the same class. Leave those tests in place. Shrinking the file to hit a line count would drop locks that 8.2-a and item 1.1 rely on.

## Metric that must stay

Copy nothing new. The constants already match the "JUnit-portable metric" block in [`.agents/cvd_analysis.py`](../.agents/cvd_analysis.py) (lines 214–233). That script is how the published margin was computed: minimum ΔE76 at least 16.3, threshold 15, and `#999933` at 3.02:1 on white. A "more accurate" matrix would move those numbers and could fail a correct palette.

Keep this pipeline:

1. sRGB channel `c` in `0..1`. If `c <= 0.04045`, use `c / 12.92`. Otherwise use `((c + 0.055) / 1.055) ^ 2.4`.
2. Contrast against opaque white: relative luminance `0.2126 R + 0.7152 G + 0.0722 B`, then `(Llighter + 0.05) / (Ldarker + 0.05)`. Compare with `>= 3.0` on the raw double. Do not round to one decimal first. `#999933` sits just above 3:1; a `> 3` check or a truncated ratio fails a correct color.
3. Multiply the linear RGB vector by the severity-1.0 Machado matrix for the simulation under test.
4. Clip each channel to `[0, 1]` before XYZ. The XYZ matrix is `[[0.4124, 0.3576, 0.1805], [0.2126, 0.7152, 0.0722], [0.0193, 0.1192, 0.9505]]`.
5. CIELAB, D65 white point `0.95047, 1.0, 1.08883`. The cube-root branch is `t > 216/24389`. ΔE76 is Euclidean distance in Lab.
6. Pairwise minimum is the minimum over combinations, not a sample of pairs. Six colors means 15 pairs per simulation. The failure message names the simulation and the actual minimum.

The three matrices:

- protan: `[[0.152286, 1.052583, -0.204868], [0.114503, 0.786281, 0.099216], [-0.003882, -0.048116, 1.051998]]`
- deutan: `[[0.367322, 0.860646, -0.227968], [0.280085, 0.672501, 0.047413], [-0.011820, 0.042940, 0.968881]]`
- tritan: `[[1.255528, -0.076749, -0.178779], [-0.078411, 0.930809, 0.147602], [0.004733, 0.691367, 0.303900]]`

`meetsCvdSafeMetric` is the predicate both the passing palette and the `TABLEAU20` reference use: every color `>= 3.0:1` on white, and minimum ΔE76 `>= 15` for protan, deutan, and tritan. `tableau20First8FailsTheMetric` asserts that predicate is false for `Arrays.copyOf(ColorUtil.TABLEAU20, 8)`. Assert the predicate. Do not pin a measured delta. The point of the reference case is that a bad palette fails, so a future edit that breaks the metric cannot stay green.

## Assertions that must remain

One behavior per test method. These already exist; execution confirms them rather than recreating them.

- `cvdSafeLightMeetsContrastOnWhite` — each `CVD_SAFE_LIGHT` entry, message includes the index and the ratio.
- `cvdSafeLightSeparatesUnderMachado` — one minimum per `Simulation` value, message includes the kind and the minimum.
- `seriesStylePairsUniqueForFirst24` — for `i` in `0..23`, the pair of `color.getRGB()` and a dash signature is unique. Signature is `"solid"` when the dash is null, otherwise `Arrays.toString`. Also keep the checks that index 0 is solid, 6 is `{6, 3}`, 12 is `{2, 2}`, 18 is `{8, 3, 2, 3}`, and index 24 repeats index 0. Uniqueness is required only below 24; the repeat at 24 is the intended cycle (`6` colors times `4` dash groups).
- `tableau20First8FailsTheMetric` — `assertFalse(meetsCvdSafeMetric(...))` on the first eight `TABLEAU20` colors.
- `cvdSafeDarkPaletteMeetsContrastOnDarkBackground` — `@Test` and `@Disabled`, reason text naming #1604 and stating it is enabled when a dark-background palette constant exists. The body stays empty. Javadoc points at `cvdSafeLightMeetsContrastOnWhite` as the pattern #1604 should copy. Surefire must report this method skipped, not failed and not passed.

Also leave the three locks added for item 1.1: `cvdSafeLightHasSixDocumentedColors` (six RGB triples, alpha 255, in order), `seriesDashReturnsDefensiveCopy`, and `seriesIndexRejectsNegative`. They are outside the 1.2 bullet list and they stay.

## What must not change

- [`ColorUtil.java`](../vcell-util/src/main/java/org/vcell/util/ColorUtil.java). The color science stays in the test. Production code does not gain a Lab converter.
- `vcell-util/pom.xml`. JUnit Jupiter is already on the test classpath. Do not add `colorspacious`, Matplotlib, Commons Math, or any other dependency.
- `generateAutoColor`, `TABLEAU20`, `DARK20`, `COLORBLIND20`, `LIGHT20`, `OTHERS20`.
- Plot panels, the field viewer, and colormaps. Those are later phases.

## Execution

1. Read `ColorAccessibilityTest` against the done-table above. If a row is already true, leave the code alone.
2. Change code only when a row is false. The expected outcome of the read is that every row is already true.
3. Re-run the command item 1.2 names, from the repository root, with Java 17:

```bash
mvn --batch-mode test -pl vcell-util -Dgroups=Fast
```

Pass is a successful build, `ColorAccessibilityTest` present in the Surefire log, 0 failures for that class, and exactly one skipped test (the disabled dark hook). The rest of the `vcell-util` Fast tests stay green. The previous run was 50 tests, 0 failures, 1 skipped. A new failure in an unrelated Fast test is a regression and blocks marking 1.2 done.

No `-am`, no Docker, no Python. `vcell-util` does not depend on a module changed by this item.

4. Record completion in the verified plan, in the same style as item 1.1. Do this only after the command passes.

In section 7, change the 1.2 heading to:

`**1.2** `vcell-util/src/test/java/org/vcell/util/ColorAccessibilityTest.java` (new, `@Tag("Fast")`) — ✅ done (YYYY-MM-DD)`

Use the date of the passing run. Append a **Result** block:

- The class implements WCAG contrast, the three Machado severity-1.0 matrices, and CIELAB ΔE76 with no new dependencies.
- Assertions cover ≥ 3.0:1 on white, pairwise ΔE76 ≥ 15 under protan, deutan, and tritan, 24 unique `(seriesColor, seriesDash)` pairs, and `TABLEAU20` entries `0..7` failing that predicate.
- `cvdSafeDarkPaletteMeetsContrastOnDarkBackground` is `@Disabled` until #1604 adds a dark-background constant.
- The verify command and its counts: tests run, failures, skipped.

In section 12, mark checklist item 4 done the same way item 3 is marked, including "working tree, not committed" if that is still true.

Leave the #1603 definition-of-done dark-background checkbox unchecked. That bullet also requires the field viewer's dark-scheme text colors to pass 8.5, which is Phase 6. The Swing half of the sentence (the disabled test exists) is what 1.2 delivers. A comment on GitHub issue #1604 is not part of this item's verify line.

## Out of scope

- Enabling or writing the body of the dark-background test. #1604 does that when a dark constant exists.
- Wiring `seriesColor` or `seriesDash` into `Plot2DPanel`, the Langevin panels, or `webapp-viewer`.
- Tests 8.2-b through 8.2-e, 8.3, and the full multi-module Fast run in test 8.1.
- Committing or opening a pull request, unless that is requested separately.

## Risks

Low. The class is already the implementation.

- Replacing the sRGB-to-XYZ constants with a higher-precision matrix changes ΔE76 relative to `.agents/cvd_analysis.py` and can fail the ≥ 15 threshold even though the palette is unchanged.
- Rounding contrast before the comparison fails `#999933`.
- Removing `@Disabled` makes the empty dark-background test fail the build. Removing the method entirely means #1604 has to recreate the hook.
- Marking item 1.2 done without a fresh `mvn test -pl vcell-util -Dgroups=Fast` log leaves the verified plan ahead of the evidence. The Result block should quote that run.
