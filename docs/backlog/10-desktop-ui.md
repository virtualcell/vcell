# Group: Desktop Client UI (Swing) — 51 issues

The largest group, and the one with the widest quality spread: it contains both the most
under-described issues in the backlog (the 2022 cohort) and some of the best (the 2026 crash
analyses).

**Epics:** `#1038` (2023, 33 children) and `#1751` (2026, current focus). **These overlap and
should be merged** — see [04-epic-map.md](04-epic-map.md#a-1038-ui-fixes-2023-vs-1751-desktop-ui-bugs-2026).

**Related group:** [19-accessibility.md](19-accessibility.md) is UI work too, separated because it
is driven by an external compliance mandate rather than by user reports.

---

## Sub-themes

Sorting these 51 by *what is broken* rather than by age gives a clearer picture than the flat list.

### Window z-order and modality (5) — a single recurring defect

`#429` (local sim results window pops behind the main window), `#1441` (Add Annotations pop-up
launches below the main window, Mac), `#1078` (uninstall window behind the Windows uninstall menu),
`#304` (spurious save prompt on startup), `#1593` (black screen moving right).

`#429` is from 2022 and `#1441` from 2025, and both describe a window appearing behind its parent.
`#1441` is `Queued` at Priority 9 / Importance 6 and rated `Complex (3)`; `#429` has 6 comments and
sat in `Pool`. Prior work on this codebase found z-order to be a systemic Swing dialog-parenting
problem rather than five unrelated bugs — `docs/windowing-design-patterns.md` exists for exactly
this.

> **Recommendation:** treat these as one defect with several reports. Fix the parenting pattern
> once, verify against all five, close them together. Ranking them separately guarantees the same
> root cause is diagnosed five times.

### Crashes and stability (4) — the highest-confidence work in the group

`#1747` (NPE in `CheckBoxScrollList` cell renderer, crashes on the EDT), `#1848` (`java.awt.Robot`
in `ScrollTable` can crash the JVM), `#858` (`ArrayIndexOutOfBounds` in
`FunctionRangeGenerator.getFunctionStatistics()`, from a user error report), `#1593` (black screen).

`#1747` and `#1848` have precise diagnoses naming the class and the mechanism. **All three of
`#1747`, `#1848` are off-board** and unranked, while vaguer enhancement requests sit `Queued`.
That inversion is the strongest argument in this document for doing the off-board sweep in
[03-board-hygiene.md](03-board-hygiene.md) first.

### Copy / paste (2)

`#792` (smart copy/paste with multiple reactions — NPE, `High Priority`, Priority 12 / Importance 9,
`Complex (3)`) and `#1930` (cannot copy/paste overridden parameters when the override is a
parameter scan — `Active`, with a precise root cause: scan overrides are `ConstantArraySpec`s but
the copy/paste system expects `Expression`s).

`#1930` reads like a specific instance of `#792`'s general problem. Worth checking whether fixing
the copy/paste model properly covers both.

### Orphaned-object cleanup (2)

`#574` (orphaned shapes after deleting species/reactions) and `#575` (orphaned RDF metadata after
the same). Both from Nov 2022, both rated **`Byzantine (1)`** — the hardest tier. Same root:
deletion does not cascade through all the model's parallel representations. These two should be
one issue or an explicit pair; they will be fixed by the same work.

### Annotation UI (4)

`#1440` (Organism field not typeable), `#1441` (pop-up z-order), `#1486` (broken ontology links —
a detailed 2.1k-char body listing exact wrong-vs-right URLs, rated `Simple (5)`), `#223` (expand
search to annotations). `#1486` is unusually well-specified and easy; it is unranked in `Pool`.

### Small, well-defined, low-risk (6) — starter-issue candidates

`#1569` (delete the Set Proxy function — `Simple (5)`, Priority 6), `#1299` (hide My BioModels /
Shared With Me for guests — `Simple (5)`), `#1886` (Mac splash screen still says VCell 7.0),
`#1884` (rename "Revert to Saved" → "Discard Unsaved Changes"), `#1451` (add a "Save as Local"
button to the error message — `Simple (5)`, Importance 7), `#1738` (make BioModel info text
selectable).

> These are the answer to `#1598`, the contributor asking for beginner-friendly work
> ([01-close-and-verify.md](01-close-and-verify.md)). `#1884` in particular is a label change with
> a documented rationale.

### Geometry and mapping UI (5)

`#194` (padding creates a spurious "background" compartment — Priority 8 / **Importance 8**, well
up the ranked slate, but an **empty body**), `#289` (subdomain-name error dialog ignored), `#290`
(kinematics spatial process always binds the first volume object — good 736-char body), `#1712`
(Geometry Mapping window clipped beyond 7 compartments), `#147` (some .stl imports fail, with
specific test files named).

`#194` is the clearest example of the backlog's central problem: one of the highest-Importance UI
issues on the board cannot be worked because nobody wrote down what it does.

### The 2022 enhancement cohort (13)

`#153` `#175` `#176` `#184` `#210` `#221` `#222` `#223` `#224` `#225` — plus `#187`, `#611`, `#1024`.
Ten of these have empty or one-line bodies. Covered in
[02-needs-refinement.md](02-needs-refinement.md); they should be resolved as a batch by their
authors, not ranked individually.

`#224` ("Revive SabioRK using their native search tools") deserves a specific check: SABIO-RK is an
external service and this has been `Shelved` for four years. Confirm the integration is still
wanted and the service still offers what we need before refining it.

---

## Cross-cutting observation

Nineteen of the 51 are from July 2022 and eleven are from 2026. The middle years are thin. The
2022 issues are feature requests written as one-liners; the 2026 issues are defects written with
stack traces. They need completely different handling, and the flat backlog treats them the same.

---

## All 51

| # | Title | Opened | Board status | Pri/Imp | Simplicity | Flags |
|---|---|---|---|---|---|---|
| [#1449](https://github.com/virtualcell/vcell/issues/1449) | Maintain ordering when toggling global flag on variable | 2025-03 | Queued | 5/2 | Complex&nbsp;(3) | thin |
| [#1460](https://github.com/virtualcell/vcell/issues/1460) | VCell names must be unique, but only sbml Ids are required and unqiue; users meanwhile… | 2025-03 | Queued | 5/3 | Intricate&nbsp;(2) | — |
| [#1572](https://github.com/virtualcell/vcell/issues/1572) | Built-in brower does not work on mac when accessing PubMed annotations | 2025-08 | Queued | 5/4 | Byzantine&nbsp;(1) | — |
| [#1299](https://github.com/virtualcell/vcell/issues/1299) | VCell Guest should not have file browser items of "My BioModels" nor "Shared With Me: | 2024-07 | Queued | 6/1 | Simple&nbsp;(5) | thin |
| [#1440](https://github.com/virtualcell/vcell/issues/1440) | Organism is unavailable in Add Annotations pop-up | 2025-02 | Queued | 6/2 | Moderate&nbsp;(4) | — |
| [#1569](https://github.com/virtualcell/vcell/issues/1569) | Delete function to Set Proxy in Account Menu | 2025-08 | Queued | 6/1 | Simple&nbsp;(5) | — |
| [#194](https://github.com/virtualcell/vcell/issues/194) | Geometry editor bug: padding to add background pixels at edges of domain adds new "bac… | 2022-07 | Queued | 8/8 | Unknown&nbsp;(0) | REF thin |
| [#835](https://github.com/virtualcell/vcell/issues/835) | Error Reports from Contact-Us improvements | 2023-03 | Queued | 8/4 | Moderate&nbsp;(4) | — |
| [#1441](https://github.com/virtualcell/vcell/issues/1441) | Add Annotations pop-up window is below the main VCell window | 2025-02 | Queued | 9/6 | Complex&nbsp;(3) | — |
| [#1495](https://github.com/virtualcell/vcell/issues/1495) | VCell Support Automated Email message are too opaque to be very useful | 2025-05 | Queued | 10/6 | Moderate&nbsp;(4) | — |
| [#1565](https://github.com/virtualcell/vcell/issues/1565) | Give users a choice when changing unit systems to either dimensionally transform value… | 2025-07 | Queued | 10/6 | Moderate&nbsp;(4) | — |
| [#792](https://github.com/virtualcell/vcell/issues/792) | Smart Copy/Paste behavior with multiple reactions redesign and implement | 2023-02 | Queued | 12/9 | Complex&nbsp;(3) | HP |
| [#1451](https://github.com/virtualcell/vcell/issues/1451) | Add "Save as Local" to the error message | 2025-03 | Queued | 12/7 | Simple&nbsp;(5) | — |
| [#1607](https://github.com/virtualcell/vcell/issues/1607) | Rule based models cannot delete reactions in graphical editor using right mouse menu. | 2025-12 | Queued | 12/8 | Moderate&nbsp;(4) | — |
| [#147](https://github.com/virtualcell/vcell/issues/147) | Some .stl files import fails | 2022-05 | Shelved | — | — | — |
| [#153](https://github.com/virtualcell/vcell/issues/153) | Make molecular composition layout default in rule-based models | 2022-06 | Pool | — | Unknown&nbsp;(0) | — |
| [#175](https://github.com/virtualcell/vcell/issues/175) | Create annotation fields for Application elements | 2022-07 | Pool | — | Moderate&nbsp;(4) | thin |
| [#176](https://github.com/virtualcell/vcell/issues/176) | Expand identification of catalysts with dashed line to situations where catalyst speci… | 2022-07 | Shelved | — | Unknown&nbsp;(0) | thin |
| [#184](https://github.com/virtualcell/vcell/issues/184) | Add right click to search reactions for Reaction Table under add a new reaction | 2022-07 | Pool | — | Moderate&nbsp;(4) | thin |
| [#187](https://github.com/virtualcell/vcell/issues/187) | Allow assignment of .vcml files to open in VCell in macs | 2022-07 | Blocked | — | Unknown&nbsp;(0) | REF |
| [#210](https://github.com/virtualcell/vcell/issues/210) | Create list of operations for "undo" ability | 2022-07 | Shelved | — | — | REF thin |
| [#221](https://github.com/virtualcell/vcell/issues/221) | Add progress bar or hourglass to time plot to indicate something is happening | 2022-07 | Pool | — | Moderate&nbsp;(4) | REF thin |
| [#222](https://github.com/virtualcell/vcell/issues/222) | Allow right click to paste into initial conditions | 2022-07 | Pool | — | Moderate&nbsp;(4) | REF thin |
| [#223](https://github.com/virtualcell/vcell/issues/223) | Expand search to include annotations | 2022-07 | Shelved | — | — | REF |
| [#224](https://github.com/virtualcell/vcell/issues/224) | Revive SabioRK using their native search tools | 2022-07 | Shelved | — | — | thin |
| [#225](https://github.com/virtualcell/vcell/issues/225) | Allow other panes besides reaction diagram to tear off from main workspace | 2022-07 | Shelved | — | — | thin |
| [#289](https://github.com/virtualcell/vcell/issues/289) | Bug when creating geometry if change subdomain name error window pops up that is subse… | 2022-08 | Pool | — | Moderate&nbsp;(4) | REF |
| [#290](https://github.com/virtualcell/vcell/issues/290) | Defining new Volume Spatial Process in Kinematics always associated with first listed … | 2022-08 | Pool | — | — | — |
| [#304](https://github.com/virtualcell/vcell/issues/304) | Opening VCell brings prompt to save previously exported sim results. | 2022-08 | Pool | — | — | — |
| [#429](https://github.com/virtualcell/vcell/issues/429) | Local sim results window pops behind the main VCell window | 2022-09 | Pool | — | — | REF thin |
| [#574](https://github.com/virtualcell/vcell/issues/574) | Orphaned shapes remain after deleting species / reactions. | 2022-11 | Pool | — | Byzantine&nbsp;(1) | REF |
| [#575](https://github.com/virtualcell/vcell/issues/575) | Orphaned metadata remains after deleting species / reactions with RDF annotations. | 2022-11 | Pool | — | Byzantine&nbsp;(1) | — |
| [#611](https://github.com/virtualcell/vcell/issues/611) | Global parameters are not listed under the Kinetics tab of a Rule-based Reaction | 2022-11 | Pool | — | — | — |
| [#858](https://github.com/virtualcell/vcell/issues/858) | Bug reported: ArrayIndexOutOfBounds in FunctionRangeGenerator.getFunctionStatistics() | 2023-04 | Pool | — | — | — |
| [#1024](https://github.com/virtualcell/vcell/issues/1024) | Parameter Estimation Window retains old values when switching to a new application in … | 2023-11 | Pool | — | Moderate&nbsp;(4) | — |
| [#1038](https://github.com/virtualcell/vcell/issues/1038) | Epic: VCell UI fixes | 2023-11 | Pool | — | — | EPIC |
| [#1078](https://github.com/virtualcell/vcell/issues/1078) | Uninstall window in alpha does not appear on top of Windows Uninstall menu | 2023-12 | Pool | — | Unknown&nbsp;(0) | — |
| [#1486](https://github.com/virtualcell/vcell/issues/1486) | Broken existing links in Annotations (both Rel and Alpha) | 2025-04 | Pool | — | Simple&nbsp;(5) | — |
| [#1591](https://github.com/virtualcell/vcell/issues/1591) | Option to show reaction names in reaction diagram | 2025-09 | Pool | — | — | — |
| [#1593](https://github.com/virtualcell/vcell/issues/1593) | Black screen over VCell, moving to the right | 2025-10 | Shelved | — | — | — |
| [#1701](https://github.com/virtualcell/vcell/issues/1701) | BNGL import error for actions block | 2026-05 | Pool | — | — | — |
| [#1712](https://github.com/virtualcell/vcell/issues/1712) | Geometry Mapping window does not show entire view if too many compartments in model. | 2026-06 | **off-board** | — | — | — |
| [#1738](https://github.com/virtualcell/vcell/issues/1738) | Selectable text in VCell Biomodel | 2026-07 | Pool | — | — | — |
| [#1743](https://github.com/virtualcell/vcell/issues/1743) | Save File dialog box change to put new model name at the top, not the bottom of the di… | 2026-07 | Pool | — | — | — |
| [#1747](https://github.com/virtualcell/vcell/issues/1747) | Client crash (NPE) in CheckBoxScrollList cell renderer when JCheckBox is null | 2026-07 | **off-board** | — | — | — |
| [#1751](https://github.com/virtualcell/vcell/issues/1751) | VCell Desktop UI bugs + AI code-test fixturing (current focus) | 2026-07 | **off-board** | — | — | EPIC |
| [#1785](https://github.com/virtualcell/vcell/issues/1785) | Database Subpanel is rather cluttered and cumbersome to use | 2026-07 | Pool | — | Intricate&nbsp;(2) | — |
| [#1848](https://github.com/virtualcell/vcell/issues/1848) | Remove legacy java.awt.Robot use from the Swing UI (ScrollTable) — it can crash the JVM | 2026-08 | **off-board** | — | — | — |
| [#1884](https://github.com/virtualcell/vcell/issues/1884) | "Revert to Saved" is wrong - change to "Discard Unsaved Changes" | 2026-08 | Pool | — | — | — |
| [#1886](https://github.com/virtualcell/vcell/issues/1886) | Mac splash screen for VCell Rel still shows VCell 7.0 | 2026-08 | Pool | — | — | — |
| [#1930](https://github.com/virtualcell/vcell/issues/1930) | User cannot copy / paste overrided parameters (in edit simulation panel) when the over… | 2026-08 | Active | — | — | — |

**Flags:** `HP` = High Priority label · `BLK` = Blocked · `EPIC` = Type: Epic · `REF` = To Refine · `thin` = body under 80 chars
