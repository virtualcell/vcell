# Phase 3 Evidence — Langevin Plot Framework (palette, styles, accessible interaction)

**Release SHA under test:** `fc8ebd3aea` (branch `chore/vcell#1605`)
**Date of this evidence session:** 2026-10-02 (EDT)
**Operator:** automated session (Hermes agent) on macOS 26.6.2, JDK 17.0.20.1 (Homebrew OpenJDK)
**Client under test:** `vcell-client` built from the working tree at `fc8ebd3aea` (plus the committed
debug bridge), launched by `tools/debug-bridge/launch-client.sh` with
`VCELL_INSTALL_DIR=~/Applications/VCell_Rel` (bundled install4j JRE), debug bridge on :9123.
**Reviewers:** PENDING HUMAN REVIEW (see `checklists/`). Per the Phase 3 plan, test omissions are
recorded as BLOCKED/NOT TESTED, never PASS.

> **The code changed after these captures — read before reviewing.** Two corrections postdate
> `fc8ebd3aea`: the legend sample now follows "Vary line styles" (committed in `9897dc37bf`, with
> shared wiring added after it), and with lines on the Langevin curve markers are now spaced at
> least four diameters apart so the dash shows between them (working tree on `9897dc37bf`, not
> committed yet). As a result:
>
> - **STALE — recapture from the commit that contains both corrections:** S1, S2, S3, S3a, S4,
>   S6, S6b, S10a, and their filtered images in `cvd/`. All are line plots with nodes on; S4 also
>   shows the old dashed legend samples next to solid curves.
> - **Still valid:** S4a, S5, S7, S8, S8b, S9, S10b. With styles on, the legend stroke is
>   unchanged, and the dialog, table, and bubble views draw no curve markers.
> - **S1 fails as captured.** Dense markers (201 samples, 6 px each) covered the dash: the
>   horizontal runs of the dotted and dash-dot series measure 0 px gaps. The earlier PASS in §3
>   is withdrawn.
> - **S2, S3a and S10a are the same image** (pixel-identical), not three separate states.
>
> Do not run T1–T6 or C1 on the stale captures. §6 records the test runs on the corrected tree.

Scope reminder: **Phase 3 done ≠ #1605 closed ≠ §9/§16 gate passed.** This evidence supports
Phase 3 (§7 Phase 3 items 3.1/3.2, tests 8.3-e/8.4-a, §8 8.6-b/8.7 for the Langevin surfaces,
§15 C series-identity checks) only.

---

## 1. Fixture and data provenance (the plan's Step 3 FLAG)

| Item | Record |
|---|---|
| Model | `aaa-aSpringSaLaD-Good`, repo fixture `vcell-core/src/test/resources/org/vcell/sbml/vcml_published/biomodel_315318780.vcml`, opened locally via File>Open>Local (no server account) |
| Application | `Application0` (SpringSaLaD), math `Application0_generated` |
| Simulation | `Simulation0` — Langevin solver, EndTime 0.02 s, output step 1e-4 (201 time points), `TotalNumberOfJobs=1` |
| Species shown | 6 series: `TOTAL_Molecule0, FREE_Molecule0, BOUND_Molecule0, TOTAL_Molecule1, FREE_Molecule1, BOUND_Molecule1` |
| Model edits made for this run | Reaction rules **`allosteric`** and **`transition_free`** set `Enabled=false` in the app's ReactionSpecs table. Reason: the bundled local solver `~/Applications/VCell_Rel/localsolvers/mac64/langevin_x64` (v1.4.6) crashes with `NullPointerException at edu.uchc.cam.langevin.reaction.BindingReactions.getBondLength(BindingReactions.java:212)` via `AllostericReactions.updateBondType` / `TransitionReactions.updateBondType` on those rules. Remaining enabled rules: creation, decay, binding, transition_none. Not a production change; model not saved. |
| Run mechanism | "Native Quick Run" (local solver, nothing saved to any server). Quick Run refuses Langevin runs with `TotalNumberOfJobs > 1` ("Concurrent Jobs not supported for Quick Run"), so the data is a **1-job batch**: `_Avg == _Min == _Max`, `_Std == 0` — the S1 statistics toggles are ON but the SD/min-max envelopes are degenerate by construction. A multi-job batch needs a server run (recorded as a limitation, not a failure). |
| Batch consolidation | Local Quick Run never runs the solver's `postprocess` step (client comment: "Deprecated: Langevin post-processing is now handled server-side"), so the results viewer binds without `_Avg/_Min/_Max/_Std.ida` and `_clusters_*.csv` and the Langevin tabs stay hidden. The consolidation was produced by running the solver's own `postprocess` command (`langevin_x64 postprocess SimID_122317207_0_.langevinInput 1`, 34 ms) on the completed run **before** the viewer bound the data (watcher script `langevin_postprocess_watch.sh`, log in `logs/langevin_watch.log`). SimID of the captured run: **`SimID_122317207_0`** (quick runs get a random temp sim key per run). |
| Screenshot mechanism | Swing debug bridge `GET /screenshot?window=3` — direct component-tree rendering of the results window "Results for Simulation Simulation0" (the `ODEDataViewer` with tabs `View Data`, `Langevin Clusters`, `3D Trajectory`). Occlusion-independent; not composited screen grabs. |

## 2. Input mechanism for keyboard-driven states (S3, S3a, S8b) — read before reviewing

OS-level synthetic keystrokes and mouse events could **not** be delivered to the client in this
session: its macOS Space was not the active Space (a full-screen application owned the active
Space), and the raw `java` client process never reports AWT window activation
(`window.isActive()` stayed false after AX activation, `requestFocus`, `toFront()` and
`com.apple.eawt.Application.activate()`; all verified by an in-JVM probe). The Swing debug
bridge has no keystroke endpoint.

The keyboard states in S3/S3a/S8b were therefore produced by dispatching `KeyEvent`s directly
into the client's AWT event queue via a `jcmd`-loaded debug agent (no repo code involved),
targeted at the plot panels (`c2078` `MoleculePlotPanel`, `c2133` `ClusterPlotPanel`). The
events route through the **same registered `WHEN_FOCUSED` InputMap bindings** a physical
keyboard hits (`registerSeriesKeys`: ctrl N / ctrl P / ctrl I → `selectNextSeries` /
`toggleSeriesIsolation`), and **no pointer event** was used. Observed state transitions match
the test expectations exactly (status text `"<series>"` and `"<series> (only this series)"`).

The keyboard *behavior* itself is additionally proven by `PlotRenderersAccessibilityTest` /
`LangevinLegendAccessibilityTest` (V1 below) and remains a **mandatory manual 8.7 item (C3/C4,
C6, C9)** for the human reviewers — this session's dispatch is a state-staging technique for
the visual/CVD review, not a substitute for the manual keyboard check.

**S8 note:** the option `showBubbleSingleColor` has **no UI control in the product** (the only
caller of `setShowBubbleSingleColor` outside `AbstractPlotPanel` is the accessibility test). S8's
state was set programmatically through the same call the test makes (`setShowBubbleSingleColor(true)`),
via the debug agent, then restored. Recorded here as method provenance.

**S2 status: BLOCKED (focus ring not captured).** `S2-molecule-legend-entry-focused.png` shows
the molecule legend but **without a visible focus ring**. Reason: Swing focus ownership cannot
engage in this session (see above — AWT window activation never engages for this raw-java client
instance, so `requestFocusInWindow`, `requestFocus` and `KeyboardFocusManager.setGlobalFocusOwner`
all leave `hasFocus=false`). Static evidence that the label is focusable and named:
`props c2242` → `focusable: true`, accessible name/description `BOUND_Molecule0`,
listeners mouse=2 key=2 (bridge inspection 2026-10-02). **C6's focus-ring observation must be
performed by a human reviewer on a normally-activated client** (packaged app). Recorded as
NOT TESTED for the ring; do not count S2 as PASS for the ring criterion.

## 3. Capture inventory (§16.3 fields per artifact)

Criterion scope for all rows: **SC 1.4.1 (Use of Color)** and **SC 1.4.11 (Non-text Contrast)**
aspects of series identity on the Langevin plot surfaces; fixture/SHA/OS/JDK/date as in the
header. Expected-vs-observed per row.

| ID | Surface / state | Expected (plan §3 capture table) | Observed 2026-10-02 | File(s) | Verdict |
|---|---|---|---|---|---|
| S1 | Molecule plot, 6 series selected, Average + Min/Max + Standard Deviation checked | Series lines with distinct styles, legend icons; AVG+SD+MIN/MAX on | 6 step-plot series with distinct dash patterns (solid / {6,3} / {2,2} / {8,3,2,3} cycle per `seriesDash(i)`), legend shows style+name for all 6; all three statistics toggles checked; envelopes degenerate (1-job data, see §1) | `S1-molecule-plot-avg-sd-minmax.png` | **FAIL as captured; STALE.** The legend shows the styles, but on the curves dense markers cover the dash (0 px gaps on the horizontal runs). Fixed by marker spacing; recapture. SD envelope degenerate by data, not by code |
| S2 | Molecule legend, one entry focused | Focus ring on the legend label | Legend rendered; **focus ring absent** — see §2 BLOCKED | `S2-molecule-legend-entry-focused.png` | **BLOCKED / NOT TESTED (ring)** — human reviewer item. **STALE.** Pixel-identical to S3a and S10a |
| S3 | Molecule plot after Ctrl+N ×k then Ctrl+I | Status `"<series> (only this series)"`, other series hidden | Status label reads exactly `BOUND_Molecule0 (only this series)`; only that series drawn | `S3-molecule-keyboard-isolation.png` | PASS for the status and isolation state (via agent dispatch, §2). **STALE** for curve styling (markers) |
| S3a | (supplementary) plain Ctrl+N naming | Status names the series, no pointer | Status reads `TOTAL_Molecule1`; series order wraps | `S3a-molecule-keyboard-naming.png` | PASS for the status text (via agent dispatch, §2). **STALE** for curve styling. Pixel-identical to S2 and S10a |
| S4 | Molecule plot, "Vary line styles" off | All-solid lines; status still names series | Curves verified solid (no dash gaps); status `TOTAL_Molecule1` retained; toggle restored afterwards | `S4-molecule-styles-off-all-solid.png` | **STALE.** Curves are solid, but the legend samples in this capture are still dashed (fixed in `9897dc37bf`); markers predate the spacing fix. Recapture |
| S4a | (supplementary) the Plot Options dialog state | — | `Vary line styles` checkbox unchecked | `S4a-plot-options-vary-line-styles-off.png` | supporting |
| S5 | Molecule data view (Data toggle) | Table with series-name columns | Numeric table with series-name columns (accessible name "Series data") | `S5-molecule-data-view.png` | PASS |
| S6 | Cluster plot, Cluster Mean + Line Plot | ACS/ACO lines, SD band/bar, style pairing | Line plot of the cluster-size series with legend styles | `S6-cluster-mean-line-plot.png` | **STALE.** Only ACS is plotted; the expected ACO line and SD band/bar are missing. Recapture with ACS and ACO selected (and SD, once multi-job data exists) |
| S6b | (supplementary) Cluster Overall + Line Plot | — | Overall-mode line plot | `S6b-cluster-overall-line-plot.png` | supporting. **STALE**; only ACS plotted |
| S7 | Cluster plot, Cluster Counts + Bubble Plot (default multi-color) | Distinct bubble colors/styles | Bubble plot, per-series colors | `S7-cluster-counts-bubble-multicolor.png` | PASS (visual/CVD review pending) |
| S8 | Cluster plot, single-color bubbles on | All-same-color bubbles; keyboard naming still works | Single-color bubble rendering (state set programmatically — §2 note); restored afterwards | `S8-cluster-counts-bubble-single-color.png` | PASS with provenance note |
| S8b | (supplementary) single-color + Ctrl+N | Status names the bubble series | Status names cluster-size series `2…` | `S8b-cluster-single-color-keyboard-naming.png` | PASS (state via agent dispatch, §2) |
| S9 | Cluster data view (Data toggle) | Table | Numeric cluster table | `S9-cluster-data-view.png` | PASS |
| S10a | Same ODEDataViewer window: View Data tab | Both tabs of one window, for C5 focus isolation | View Data tab with molecule plot | `S10a-same-window-view-data-tab.png` | **STALE.** Pixel-identical to S2 and S3a. C5 itself is a manual item |
| S10b | Same window: Langevin Clusters tab | — | Langevin Clusters tab with cluster plot | `S10b-same-window-langevin-clusters-tab.png` | PASS (capture) — C5 itself is a manual item |

## 4. CVD / grayscale filtering (8.6-b tooling half)

- Script: `.agents/cvd_analysis.py` `image` mode (added in this Phase 3 session; see §5 for its
  self-check). Machado, Oliveira & Fernandes 2009 **severity 1.0 (dichromacy)** matrix multiply
  in linear RGB for `protan` / `deutan` / `tritan`; `grayscale` = WCAG relative luminance
  (0.2126R+0.7152G+0.0722B) through the sRGB transfer function (matches `sim(…,'achroma')`).
- Output: `cvd/<capture>-{protan,deutan,tritan,grayscale}.png` — 15 captures × 4 = 60 images.
- **8.6-b reviewer task (T1–T6 in `checklists/cvd-8.6b-review.md`) is PENDING HUMAN REVIEW.**
  The automated half (filters generated, self-checked) is complete; the PASS condition ("a
  reviewer can match each legend entry to its curve by style, … without hue") is a human task.

## 5. Tooling self-checks (recorded per plan Step 2)

| Check | Result | Log |
|---|---|---|
| deutan output of `#999933` vs `#000000` differs | PASS — max channel diff 166 (`(166,150,58)` vs `(0,0,0)`) | `logs/cvd-image-mode-selfcheck.log` |
| grayscale output single-channel-equal everywhere | PASS (R==G==B for all pixels; `#999933`→148, `#004488`→69) | same |
| four filtered PNGs produced per input | PASS | same |
| existing numeric mode byte-identical to pre-change (`git show HEAD:`) | PASS — `diff` empty | `logs/cvd-numeric-mode-regression.out` |

## 6. Automated verification runs (§5 of the completion plan)

| ID | Command | Result | Log |
|---|---|---|---|
| V1 | `mvn --batch-mode test -pl vcell-client -am -Dgroups=Fast -Djava.awt.headless=true -Dtest=PlotRenderersAccessibilityTest,LangevinSeriesIdentityTest,LangevinLegendAccessibilityTest -Dsurefire.failIfNoSpecifiedTests=false` | **11 tests, 0 failures, 0 errors, 0 skipped — BUILD SUCCESS** (2026-10-02T05:19Z) | `logs/V1-V4-tests-2026-10-02.log` |
| V2 (8.4-a) | `rg -n -e "ColorUtil.TABLEAU20" -e "ColorUtil.DARK20" --glob "*VisualizationPanel.java" vcell-client/src/main/java` | **no matches, exit 1** (expected PASS) (05:25Z) | `logs/static-checks-2026-10-02.log` |
| V3 | `rg -n "TABLEAU20\|DARK20\|generateAutoColor" vcell-client/src/main/java/cbit/plot/gui vcell-client/src/main/java/cbit/vcell/solver/ode/gui` | **no matches, exit 1** (expected PASS) (05:25Z) | same |
| V4 | `mvn --batch-mode test -pl vcell-util,vcell-client -am -Dgroups=Fast … -Dtest=ColorAccessibilityTest,Plot2DPanelAccessibilityTest,PlotPaneAccessibilityTest,PlotSeriesStyleTest,MultisourcePlotPaneAccessibilityTest` | **28 run, 0 failures, 1 skipped (the expected `#1604` dark-palette hook), BUILD SUCCESS** (05:19Z) | `logs/V1-V4-tests-2026-10-02.log` |

Java source tree at these runs is identical to the committed `fc8ebd3aea` tree (this session
changed only documentation, the `.agents` analysis script, and evidence files).

**Re-run on the corrected tree** (working tree on `9897dc37bf` with the marker spacing and shared
legend wiring; same commands; log `logs/V1-V4-tests-2026-10-02-corrections.log`):

| ID | Result (2026-10-02T19:12Z) |
|---|---|
| V1 | **13 tests, 0 failures, 0 errors, 0 skipped** (renderer 8, legend 3, identity 2). New: `denseSeriesWithNodesKeepsDashGapsBetweenMarkers` (failed with a 0 px gap before the spacing change); `legendIconBecomesSolidWhenStylesAreOff` now also checks the legend repaint and fails if the wiring is removed |
| V2 (8.4-a) | no matches, exit 1 (expected PASS) |
| V3 | no matches, exit 1 (expected PASS) |
| V4 | **28 run, 0 failures, 1 skipped** (the expected `#1604` hook) |

## 7. Pending human review (honest gaps — not PASS)

1. **Manual 8.7 (C1–C12)** — two reviewers on macOS, Windows and Linux per the plan. This machine
   is macOS; the macOS capture set exists (§3). **Windows and Linux runs were NOT performed**
   (no such environments in this session). One reviewer must be CVD-affected or use Sim
   Daltonism / Color Oracle. Kit: `checklists/manual-8.7-checklist.md`. C6's focus-ring item
   additionally needs a normally-activated client (§2 S2 BLOCKED).
2. **8.6-b reviewer task (T1–T6)** on the filtered images — kit: `checklists/cvd-8.6b-review.md`.
   PASS condition: every legend entry maps to its curve **by style, without hue**; any
   hue-dependent mapping is a Phase 3 regression. Run it only on the recaptured line plots (item 4),
   not on the stale ones listed at the top of this manifest.
3. **Multi-job batch data** — the captured run is a 1-job batch (Quick Run limitation). If the
   reviewers want visible SD/min-max envelopes (S1, S6/T3), re-capture from a server-side
   20-job Langevin batch (plan Step 3 option (a)).

Not a human-review item, but it must happen before items 1 and 2:

4. **Recapture the stale line plots** (S1, S2/S3a/S10a, S3, S4, S6, S6b) and re-run the filters,
   from the commit that contains the marker-spacing and legend corrections. S6 needs ACS and ACO
   selected. A fixture that produces more than one cluster size is needed before S7/S8 can show
   multi-series bubble identity.

## 8. §16.3 record-field checklist for this evidence set

- criterion IDs: **1.4.1, 1.4.11** (series-identity aspects) — per row in §3 ✓
- surface/workflow/state: Langevin molecule/cluster plot surfaces and states — per row ✓
- expected and observed: §3 table ✓
- fixture: `biomodel_315318780.vcml` (`aaa-aSpringSaLaD-Good`) + recorded rule disables, SimID `SimID_122317207_0` ✓
- release SHA: `fc8ebd3aea` ✓
- OS/JDK: macOS 26.6.2 / OpenJDK 17.0.20.1 ✓
- date: 2026-10-02 ✓
- operator/reviewer: operator = automated session (Hermes agent); reviewers = PENDING ✓ (honest)
- durable evidence links: files under `docs/accessibility/evidence/2026-10-phase-3-langevin/` ✓
