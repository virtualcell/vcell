# Group: Data Export & Visualization — 32 issues

Covers everything between "the simulation finished" and "the user can see or reuse the results":
the export subsystem, the N5/ImageJ path, HDF5 writing, the desktop results viewers, and the new
browser-based field viewer.

**Epics:** `#1199` (Refactor Data Export Services — **Importance 9, second-highest on the board**, but
zero linked children), `#1034` (local use of field data), `#1652` (VFRAP/geometry rework), `#1803`
(web-based field & geometry visualization, off-board).

**Strategic decision required** — see
[04-epic-map.md](04-epic-map.md#decision-2--desktop-pde-viewer-or-browser-viewer). Several issues
here are investments in a desktop viewer that may be being superseded by the browser viewer. That
call determines whether roughly eight of these are worth ranking at all.

---

## Sub-themes

### The export subsystem rewrite (6) — one initiative filed as several

`#1199` (epic), `#1008` (*"almost 2 decades old… could use a re-write"*), `#1330` (enhance export
process), `#1115` (post-processed variables don't show export progress), `#1668` (store the export
request as JSON so a job can be understood after the fact), `#1542` (move export history
server-side).

`#1199` and `#1008` are **the same proposal written twice** — see
[04-epic-map.md](04-epic-map.md#b-1199-refactor-data-export-services-vs-1008-vcell-export-needs-a-face-lift).
The epic has Importance 9 — the second-highest on the board — and no children, while the work that
belongs to it sits unlinked around it. Populating `#1199` would be a cheap and unusually
high-leverage bit of grooming.

`#1199` had been scored but **never given a Priority**, so it did not appear in the ranked queue at
all. Under the board's formula (`Importance + Simplicity`) it computes to **11**, second from the
top; it has since been set and moved to `Queued` — though note it is an epic with zero children, so
it is currently queued as an empty container. `#1473` was in the same position and computes to
**12**, tying the highest score on the board. Both fixed 2026-08-19; see
[03-board-hygiene.md](03-board-hygiene.md).

`#1668` is worth calling out as a small enabler rather than a feature: being unable to read back
what an export job actually requested makes every other bug in this list harder to diagnose.

### N5 / ImageJ (5)

`#1338` (N5 metadata lacks unit length, time unit, time intervals — hard-coded or absent), `#1352`
(every export creates a new N5 dataset → storage balloons on re-export), `#1385` (auto-export N5
on job dispatch), `#1473` (N5 metadata needs the time array plus origin and extent — `Simple (5)`,
Importance 7), `#1555` (export control through the new API, Priority 12/8).

`#1338` and `#1473` are the same defect — N5 output is missing the spatial/temporal metadata that
makes it interpretable — reported ten months apart, and `#1338` is off-board. **Merge.**

`#1352` is the one with an operational cost attached (unbounded storage growth), and it is
unranked in `Pool`.

### HDF5 (3)

`#877` (jHDF suffers a JRE bug on Windows, `Blocked`), `#1894` (`Hdf5PostProcessor` rejects the
statistic name `mean` written by the Chombo solver — and the rejection **aborts all data access for
the run**), `#1964` (`ASCIIExporter` is the last thing needing native HDF5 for writing, blocked on
upstream jhdf#654), `#138` (results not correctly exported to CSV or HDF5 for VCML models).

`#877` was checked (see [05-obsolescence-sweep.md](05-obsolescence-sweep.md)) and the answer is the
opposite of what I first guessed: `io.jhdf 0.13.0` is in the root pom and `ncsa.hdf` is **gone**, so
jHDF is now the *only* HDF5 reader. A JRE bug affecting it on Windows is therefore **more**
consequential than when the issue was filed. **Re-rank upward, do not close.**

`#1894` is the highest-severity item in this section: one unrecognized statistic name makes an
entire run's data unreadable. Small fix, large blast radius, off-board.

### The browser field viewer (4) — active, off-board

`#1803` (epic), `#1859` (labels, color bar, axes, cut plane, picking, time plots, movie export),
`#1867` (color-scale modes), `#1879` (VTK visualization for moving-boundary results).

This is live, recently shipped work (through the 8.0.9/8.0.10 line) and **none of it is on the
board**. `#1859` is a well-organized follow-up list grouped by what each item actually requires.

### Desktop results viewers (7)

`#191` (new GUI design for the spatial results viewer — *design already created and approved*,
Importance 6), `#898` (auto-scale-at-all-times truncates values top and bottom; out-of-scale
elements render black on screen and white on export — Priority 8/6), `#950` (local parameter-scan
results appear only partially, filling in as you toggle between parameters — Priority 7/5), `#832`
(multiple stochastic runs are not displayed — the release notes claim statistics that are not
actually produced), `#174` (results view with envelope and SD for multi-run stochastic), `#772`
(surface-view `.mov` files unreadable by Windows Media Player), `#986` (GIF export produces wildly
incorrect output after the first one, `High Priority`, affects an external collaboration).

`#832` is notable for an honesty problem as much as a technical one: *"In release notes we say
'Statistics displayed and exported for multiple trajectory stochastic simulations'. In fact, we
just run and display an average."* It pairs with `#174` (envelope/SD display) and with SpringSaLaD's
`#1508` — three issues, one missing capability: **proper multi-run statistics display**.

`#986` and `#772` are both export-format defects with named external users affected.

### Field / image data (4)

`#648` (field data needs better geometry integration — a concept deck is attached), `#1034` (epic:
local use of field data), `#167` (API endpoint for field-data retrieval for local runs, Priority
12/8, `High Priority`), `#1652` (rework VFRAP workflows to merge geometry and image data),
`#1494` (lazy-load microscopy data in the PDE viewer).

`#1652` states the underlying problem well: *"Field Data is very complex and most users cannot
figure out 1) that it exists and 2) how to use it."* That is a product problem, and `#648`,
`#1034` and `#167` are all partial technical responses to it. `#1652` should probably be the
parent of the other three rather than a sibling.

### Plotting (1)

`#366` — the CLI Python plotting code needs restructuring to control the seaborn colour palette.
**Verified obsolete:** `seaborn` is gone from the tree (its only trace is a transitive extras line
in another package's lock file), and `#1472` independently records *"Python reliance removed, and
plotting / logging done java side [COMPLETE]"*. → close candidate, see
[05-obsolescence-sweep.md](05-obsolescence-sweep.md).

---

## Recommendations

1. **Merge `#1008` into `#1199`** and populate `#1199` with children (`#1115`, `#1330`, `#1668`,
   `#1542`, `#986`, `#772`). It scores Importance 9 and has no content.
2. **Merge `#1338` into `#1473`** (same N5 metadata gap).
3. **Board the 8 off-board issues**, especially `#1894`.
4. **Resolved by the obsolescence sweep:** close `#366` (seaborn gone); re-rank `#877` **upward**
   (jHDF is now the sole HDF5 reader, not a fading dependency).
5. **Group the multi-run statistics trio** (`#832`, `#174`, SpringSaLaD `#1508`) as one capability.
6. **Take Decision 2** before ranking `#191`, `#1494`, `#898`, `#950`.
7. `#1894` and `#1352` are the two items here with concrete ongoing costs (unreadable runs;
   unbounded storage) and both are unranked. Consider promoting regardless of the rest.

---

## All 32

| # | Title | Opened | Board status | Pri/Imp | Simplicity | Flags |
|---|---|---|---|---|---|---|
| [#950](https://github.com/virtualcell/vcell/issues/950) | Local run Par Scan doesn't show full results | 2023-08 | Queued | 7/5 | Intricate&nbsp;(2) | — |
| [#191](https://github.com/virtualcell/vcell/issues/191) | Implement new GUI design for spatial sim results viewer | 2022-07 | Queued | 8/6 | Intricate&nbsp;(2) | REF thin |
| [#898](https://github.com/virtualcell/vcell/issues/898) | Display of simulation results for spatial does not redraw properly when using auto sca… | 2023-05 | Queued | 8/6 | Intricate&nbsp;(2) | — |
| [#1199](https://github.com/virtualcell/vcell/issues/1199) | Epic: Refactor Data Export Services in VCell | 2024-03 | Queued | 11/9 | Intricate&nbsp;(2) | HP EPIC |
| [#167](https://github.com/virtualcell/vcell/issues/167) | API endpoint for field data retrieval for local runs | 2022-07 | Queued | 12/8 | Moderate&nbsp;(4) | HP thin |
| [#1473](https://github.com/virtualcell/vcell/issues/1473) | ImageJ N5 data export metadata needs to include time array as well origin and extent | 2025-04 | Queued | 12/7 | Simple&nbsp;(5) | — |
| [#1555](https://github.com/virtualcell/vcell/issues/1555) | Export Control Through The New API | 2025-06 | Queued | 12/8 | Moderate&nbsp;(4) | — |
| [#138](https://github.com/virtualcell/vcell/issues/138) | Results are not correctly exported to CSV or HDF5 for VCML models | 2022-03 | Pool | — | Complex&nbsp;(3) | — |
| [#174](https://github.com/virtualcell/vcell/issues/174) | Create results view with envelope and SD for multiple stochastic runs | 2022-07 | Pool | — | Intricate&nbsp;(2) | HP thin |
| [#366](https://github.com/virtualcell/vcell/issues/366) | CLI-python code for creating plots needs refactor to control color palette | 2022-09 | Pool | — | — | — |
| [#648](https://github.com/virtualcell/vcell/issues/648) | Field data needs update and better integration with geometry | 2022-12 | Pool | — | Unknown&nbsp;(0) | — |
| [#772](https://github.com/virtualcell/vcell/issues/772) | Movie files from surface view not opened by windows player | 2023-01 | Pool | — | — | — |
| [#832](https://github.com/virtualcell/vcell/issues/832) | Multiple stochastic runs are not displayed | 2023-03 | Pool | — | — | — |
| [#877](https://github.com/virtualcell/vcell/issues/877) | VCell uses jHDF, which suffers from JRE bug <JDK-4715154> | 2023-05 | Blocked | — | Intricate&nbsp;(2) | BLK |
| [#986](https://github.com/virtualcell/vcell/issues/986) | Issues with Gif export from VCell | 2023-10 | Pool | — | — | HP |
| [#1008](https://github.com/virtualcell/vcell/issues/1008) | VCell Export needs a face-lift | 2023-10 | Pool | — | — | — |
| [#1034](https://github.com/virtualcell/vcell/issues/1034) | Epic: local use of field data | 2023-11 | Pool | — | — | EPIC |
| [#1115](https://github.com/virtualcell/vcell/issues/1115) | Post-Processed Variables Don't Display Export Progress | 2024-01 | Pool | — | Complex&nbsp;(3) | — |
| [#1330](https://github.com/virtualcell/vcell/issues/1330) | Enhance Export Process in VCell Application | 2024-07 | **off-board** | — | — | — |
| [#1338](https://github.com/virtualcell/vcell/issues/1338) | N5 Calibration | 2024-08 | **off-board** | — | — | — |
| [#1352](https://github.com/virtualcell/vcell/issues/1352) | N5 Export Storage appears to produce Overconsumption | 2024-09 | Pool | — | Moderate&nbsp;(4) | — |
| [#1385](https://github.com/virtualcell/vcell/issues/1385) | Add option for automatic export of simulation n5 data with job dispatch | 2024-11 | Pool | — | Complex&nbsp;(3) | — |
| [#1494](https://github.com/virtualcell/vcell/issues/1494) | Lazy load microscopy data in PDE data viewer | 2025-04 | **off-board** | — | — | thin |
| [#1542](https://github.com/virtualcell/vcell/issues/1542) | Export History Should Be Saved on the Server instead of Locally | 2025-06 | Active | — | — | — |
| [#1652](https://github.com/virtualcell/vcell/issues/1652) | Rework VFRAP workflows to merge Geometry and Image (formerly Field) Data | 2026-03 | Pool | — | — | EPIC |
| [#1668](https://github.com/virtualcell/vcell/issues/1668) | Have the Export Job Table Store JSON Version of the Export Request | 2026-04 | Pool | — | — | — |
| [#1803](https://github.com/virtualcell/vcell/issues/1803) | [Epic] Web-based field & geometry visualization in webapp-ng (vtk.js/vtk.wasm) | 2026-07 | **off-board** | — | — | — |
| [#1859](https://github.com/virtualcell/vcell/issues/1859) | 3D field viewer: nice-to-haves (labels, color bar, axes, cut plane, picking, time plot… | 2026-08 | **off-board** | — | — | — |
| [#1867](https://github.com/virtualcell/vcell/issues/1867) | 3D field viewer: color-scale modes — autoscale at current time, autoscale over the tim… | 2026-08 | **off-board** | — | — | — |
| [#1879](https://github.com/virtualcell/vcell/issues/1879) | Field viewer: VTK-based visualization for moving boundary problems (mbsolver) | 2026-08 | **off-board** | — | — | — |
| [#1894](https://github.com/virtualcell/vcell/issues/1894) | Hdf5PostProcessor rejects statistic name 'mean' (written by the chombo solver) and abo… | 2026-08 | **off-board** | — | — | — |
| [#1964](https://github.com/virtualcell/vcell/issues/1964) | ASCIIExporter still needs native HDF5 for writing — blocked on jhdf incremental chunk … | 2026-08 | **off-board** | — | — | — |

**Flags:** `HP` = High Priority label · `BLK` = Blocked · `EPIC` = Type: Epic · `REF` = To Refine · `thin` = body under 80 chars
