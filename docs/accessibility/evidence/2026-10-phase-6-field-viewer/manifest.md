# Phase 6 Evidence — Field viewer text and traces (web content)

**Working tree under test:** `chore/vcell#1605` @ `e21a3024c4` + uncommitted Phase 5 (colormap
selector) and Phase 6 (this phase) edits to `webapp-viewer/viewer.js` and
`webapp-viewer/index.html`. Nothing pushed; no PR yet.
**Date of this evidence session:** 2026-10-06 (EDT)
**Operator:** automated session (Hermes agent) on macOS 27.0.1, JDK 17.0.20.1, Python 3.12 venv
(`webapp-viewer/test/requirements.txt`: playwright 1.63.0, pytest 9.1.1), Chromium via Playwright
(SwiftShader WebGL), Node 26.9.0.
**Server under test:** `org.vcell.client.viz.FieldViewerFixtureServer` (the test harness's own
loopback fixture server; the same seam the desktop client serves the viewer through).
**Reviewers:** PENDING HUMAN REVIEW. Test 8.6-b CVD-filtered captures and test 8.7 manual QA for
this surface are NOT RUN — recorded as BLOCKED/NOT TESTED, never PASS.

Scope reminder: **Phase 6 done ≠ #1605 closed ≠ §9/§16 gate passed.** This evidence covers Phase
6 items 6.1 and 6.2 (the field viewer's color and trace-style findings, §5 row W1 / §13 W1) and
test 8.5 for the changed pairs. The remaining §13 rows for this web surface are §15 E and stay open.

---

## 1. What changed (6.1 — viewer.js)

| Change | Where | Detail |
|---|---|---|
| `SERIES_COLORS` → the six `CVD_SAFE_LIGHT` colors | `viewer.js` ~`:1478` | Light scheme draws `ColorUtil.CVD_SAFE_LIGHT` verbatim: `#000000, #999933, #004488, #8C510A, #0072B2, #CC6677`. |
| Dark `color-scheme` variant | same | `SERIES_COLORS_DARK = #6c6c6c, #999933, #556998, #93613a, #6c93c0, #d38691`, computed with `.agents/cvd_analysis.py` (see §3). Selected via `matchMedia('(prefers-color-scheme: dark)')`, swapped live on change. |
| Probe traces get `stroke-dasharray` | `renderTraces` | Each probe carries `dash = seriesDash(colorIndex)`; the polyline and the legend line sample both draw it. |
| Stats traces get `stroke-dasharray` | `renderStatsPlot` | Series *k* takes `seriesDash(k)`; the min/max envelope keeps its translucent fill. |
| List swatches → short SVG line samples | `renderProbes`, `renderStatsPlot` legend | 18×10 SVG with a `<line>` in the series' own stroke color **and** dash pattern (`aria-hidden`; the label text beside it carries the name). |
| Probe identity | `addProbe` | Probes now key on `colorIndex` (0–5) rather than a hex value, so a live scheme flip recolors every drawn surface consistently. Probe cap `MAX_PROBES` is now 6 (was 12) — matches the six-color palette, per plan. |
| Dash sequence | `seriesDash(i)` | Exact JS mirror of `ColorUtil.seriesDash`: 8-slot cycle `solid, {6,3}, {2,2}, {8,3,2,3}, {6,3}, {2,2}, {8,3,2,3}, solid`. With 6 colors, `(color, dash)` pairs stay unique for i < 24, the Phase 1 identity bound. |

## 2. What changed (6.2 — index.html) and test 8.5

Test 8.5 method: WCAG 2.x relative-luminance contrast on source colors, computed with the repo's
own `.agents/cvd_analysis.py` (`cr()`); pairs where the CSS applies opacity were computed on the
alpha-composited effective color. All values below re-measured 2026-10-06 on the final tree.

| Pair (surface, state) | Foreground | Background | Ratio | Need |
|---|---|---|---|---|
| `.readout em` / `.readout.pick`, light | `#157347` | white | 5.87:1 | 4.5 |
| `.readout em`, dark (inside `.readout` opacity .8 → effective `#048ac0`) | `#4dc47d` | `#121212` | 4.82:1 | 4.5 |
| `.readout.pick`, dark | `#4dc47d` | `#121212` | 8.48:1 | 4.5 |
| `button[aria-pressed=true]` text | white | `#157347` | 5.87:1 | 4.5 |
| pressed border (non-text indicator), light/dark | `#157347` | white / `#121212` | 5.87 / 3.19:1 | 3 |
| `.kymo-panel .stale` text | white | `#a85d00` | 4.96:1 | 4.5 |
| `.kymo-panel .note.warn`, light | `#a85d00` | white | 4.96:1 | 4.5 |
| `.kymo-panel .note.warn`, dark | `#ffa43d` | `#121212` | 9.48:1 | 4.5 |
| `.status.err`, light (retained `#c0392b`) | `#c0392b` | white | 5.44:1 | 4.5 |
| `.status.err`, dark override | `#ff6b6b` | `#121212` | 6.75:1 | 4.5 |
| `.plot-panel .curve`/`.curve-dot` fallback (non-text), light/dark | `#157347` / `#4dc47d` | white / `#121212` | 5.87 / 8.48:1 | 3 |
| Series light palette, each on white | `CVD_SAFE_LIGHT` | white | 21.00, 3.02, 9.62, 6.37, 5.19, 3.66 | 3 |
| Series dark palette, each on `#12121a` and `#121212` | `SERIES_COLORS_DARK` | dark canvases | 3.55–6.77 (min 3.42) | 3 |

**Result: 32/32 changed pairs pass** (script: `logs/phase6_8_5_pairs.py`, output
`logs/phase6_8_5_pairs.txt`). Note the dark `.readout em` pair only clears 4.5:1 because of the
parent's `opacity: .8` compositing math; without that compositing the raw pair is 8.48:1.

## 3. How the dark series palette was derived (6.1 "compute with the script")

Script `logs/phase6_dark_palette3.py` (run with the plan's `.agents/cvd_analysis.py` on
`sys.path`): each `CVD_SAFE_LIGHT` color was lightened toward white in linear light (hue
preserved) just enough to clear 3.4:1 on both dark canvas backgrounds; the palette was then
repaired pairwise — pushing the lighter-in-original-palette member further toward white — until
the min pairwise ΔE′ (CAM02-UCS after Machado 2009 severity-1.0 protan/deutan/tritan) reached
the same ≥15 bar `ColorAccessibilityTest` holds `CVD_SAFE_LIGHT` to. Final:

- `#6c6c6c` 3.55/3.57 · `#999933` 6.17/6.21 · `#556998` 3.42/3.44 · `#93613a` 3.56/3.58 ·
  `#6c93c0` 5.83/5.86 · `#d38691` 6.73/6.77 (contrast vs `#12121a` / `#121212`)
- min ΔE′: normal 15.9, protan 15.6, deutan 15.3, tritan 15.3

The naive "minimum lightening that hits 3:1" palette FAILS the CVD metric (min ΔE′ 6.1 —
`#496194` collapses into `#0072b2` under deutan); the joint-contrast-and-ΔE derivation above is
what ships. This is an engineering-aid metric (§15 B), not a WCAG success criterion.

## 4. §15 C / §15 E notes for this phase

- **Repeated dash patterns (§15 C):** with 6 colors the first repeat of a `(color, dash)` pair is
  at series 24 (probes cap at 6; the Stats plot caps at `STATS_MAX_VARIABLES` = 6), so the pair
  cycle never repeats within either panel's capacity. When it does repeat (larger series counts
  or future custom colors), the Phase 2 identity rules apply — accessible selection/isolation,
  not another hue. That work is §15 C / W1 and stays open.
- **Styles cannot be shown (§15 C):** the probe list already carries the probe's coordinates as
  text and the legend carries series names as text; the swatches are decorative (`aria-hidden`)
  samples, so identity never rests on color or dash alone.
- **§15 E:** the remaining §13 rows for `webapp-viewer` (landmarks, focus, live status, zoom,
  keyboard, repeatable axe-core scans) are NOT part of Phase 6 and remain open.

## 5. Verification runs (2026-10-06, this tree)

| Check | Command | Result |
|---|---|---|
| 8.5 pair computation | `python3 logs/phase6_dark_palette3.py`, `logs/phase6_8_5_pairs.py` (with `.agents/cvd_analysis.py`) | 32/32 pairs pass (§2); palette derivation log §3 |
| Module parse/load | `node` DOM-stub import of `viewer.js` | loads; only the expected `vtkwasm global not available` (no browser) |
| Probe suite | `pytest webapp-viewer/test/test_probes.py` (Chromium, fixture server) | **72 passed** (168 s) |
| Kymograph + FV membrane suite | `pytest webapp-viewer/test/test_kymograph.py webapp-viewer/test/test_fv_membranes.py` | **72 passed** (123 s) |
| Body-fitted kymograph + membrane curves | `pytest webapp-viewer/test/test_kymograph_bodyfitted.py webapp-viewer/test/test_membrane_curves.py` | **48 passed** (79 s) |
| Colormap LUT JSON | `pytest webapp-viewer/test/test_colormap.py` | **1 passed** |

Full command lines and environment in `logs/pytest-runs.txt`. The suite compiles `vcell-client`
tests via Maven (IntelliJ-bundled mvn, Java 17) and serves fixtures from
`FieldViewerFixtureServer`; no dataset was invented.

## 6. NOT DONE here (honest gaps, per the standing rules)

- **8.6-b for this surface — NOT RUN.** No field-viewer screenshots were captured and no
  Machado/grayscale-filtered images were produced. The existing browser suite asserts structure,
  not appearance. Capturing fv2d/fv3d scenes with 3+ probes and filtering them (protan/deutan/
  tritan/grayscale via `cvd_analysis.py image`) is a follow-up for the reviewer kit.
- **Dark-scheme rendering — verified by computation only.** The dark palette and CSS overrides
  are derived and unit-verified, but no dark-mode screenshot was taken (Playwright
  `color_scheme='dark'` captures not run).
- **8.7 manual QA, two reviewers, OS themes — PENDING HUMAN REVIEW.**
- **§15 E web-semantic rows and repeatable axe-core scans — open, out of Phase 6 scope.**
- Phase 6 completion does NOT close #1605/#1603 and does not satisfy the §9/§16 release gates.
