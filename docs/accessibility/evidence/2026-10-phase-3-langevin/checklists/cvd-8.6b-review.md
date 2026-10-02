# 8.6-b CVD reviewer task — T1–T6 on the filtered Langevin captures (PENDING HUMAN REVIEW)

Plan reference: `.agents/uconn-color-blind-accessibility-verified.md` §8 8.6-b (Langevin scenes),
method per the plan's notes: Machado 2009 **severity 1.0 (dichromacy)** for protan/deutan/tritan
and WCAG-luminance grayscale, produced by `.agents/cvd_analysis.py image`.

> **Hold:** S1, S2, S3, S3a, S4, S6, S6b and S10a (and their filtered images) predate the 2026-10-02
> marker-spacing and legend corrections and are stale. Wait for the recapture listed in
> `../manifest.md` before reviewing them; S1 as captured fails because markers hide the dash.

**PASS condition (the plan's words, in intent):** *"a reviewer can match each legend entry to
its curve by style, … without hue."* **Failure = any mapping that requires hue.** Any failure is
a Phase 3 regression → fix code, re-run V1, re-capture, re-filter.

Reviewer: ____________________  CVD status: ____________________  Date: ____________

Instructions: for EVERY image in `../cvd/` (15 captures × 4 filters = 60), perform the checks
below. Tick per filter per image; record image names in the notes column where anything is
ambiguous.

| Check | Protan | Deutan | Tritan | Grayscale |
|---|---|---|---|---|
| T1 Every legend entry maps to exactly one visible curve, matched **by dash style + marker**, not hue | ☐ | ☐ | ☐ | ☐ |
| T2 Series 0 (black, solid) vs series 6 (`#000000`+dash{6,3} family) remain distinguishable **by style** | ☐ | ☐ | ☐ | ☐ |
| T3 SD envelope/bar remains attributable to its parent series (style/legend pairing; lighter fill). NOTE: the captured data is a 1-job batch so envelopes are degenerate — judge attribution from the legend/series pairing and note "degenerate data" | ☐ | ☐ | ☐ | ☐ |
| T4 Isolation state (S3) is obvious: one curve + status naming it | ☐ | ☐ | ☐ | ☐ |
| T5 Bubbles (S7/S8): single-color mode still identifies the selected series through status/isolation (S8b shows it) | ☐ | ☐ | ☐ | ☐ |
| T6 Legend icons (80×12) keep visible dash gaps at actual size | ☐ | ☐ | ☐ | ☐ |

Image inventory to review (in `../cvd/`, each in `-protan/-deutan/-tritan/-grayscale` variants):

1. `S1-molecule-plot-avg-sd-minmax` — 6 series, styles, legend, statistics on
2. `S2-molecule-legend-entry-focused` — legend WITHOUT focus ring (ring BLOCKED, see manifest §2)
3. `S3-molecule-keyboard-isolation` — single series + `"BOUND_Molecule0 (only this series)"`
4. `S3a-molecule-keyboard-naming` — status `TOTAL_Molecule1`
5. `S4-molecule-styles-off-all-solid` — all-solid curves (T1 does not apply: styles intentionally off)
6. `S4a-plot-options-vary-line-styles-off` — the options dialog state
7. `S5-molecule-data-view` — data table (T1–T3 do not apply; verify column headers stay readable)
8. `S6-cluster-mean-line-plot` — cluster line plot (mean mode)
9. `S6b-cluster-overall-line-plot` — cluster line plot (overall mode)
10. `S7-cluster-counts-bubble-multicolor` — multi-color bubbles
11. `S8-cluster-counts-bubble-single-color` — single-color bubbles
12. `S8b-cluster-single-color-keyboard-naming` — single-color + status naming
13. `S9-cluster-data-view` — cluster data table
14. `S10a-same-window-view-data-tab` — molecule tab of the shared ODEDataViewer
15. `S10b-same-window-langevin-clusters-tab` — cluster tab of the same window

Verdict (PASS / FAIL + notes): ________________________________________________
