# Group: SpringSaLaD / Langevin — 32 issues

The most striking group in the backlog: **26 of 32 are off the project board**, including all 14
issues from the July 2026 grooming pass and the epic that organizes them.

This is a well-described, coherent, largely user-driven body of work that is invisible to anyone
planning from board #1.

**Epics:** three of them, overlapping — `#870` (MVP, 2023, `Active`), `#1729` (bugs & upgrades,
2026, off-board), and `#1482`, which is an umbrella in all but label. See
[04-epic-map.md](04-epic-map.md#c-870-springsalad-mvp-vs-1729-springsalad-bugs--upgrades-vs-1482-langevin-solver-maintenance).

---

## The `#1729` set — 14 issues, already groomed

`#1719`–`#1732` were created on 2026-07-08 from a request list by **Leslie Loew**, decomposed into
standalone issues with the original request quoted in each body and provenance recorded. They are
the best-structured cluster in the repo.

They divide cleanly into three programmes, and the grouping is already stated in the issue bodies:

**a) SpringSaLaD ↔ rule-based Physiology compatibility (5)** — `#1719` (drop the required
Extracellular/Membrane/Intracellular names, `High Priority`), `#1720` (tolerate extra
compartments, `High Priority`), `#1721` (allosteric reactions without explicit bound/unbound
states), `#1722` (volumetric rate constants for membrane-tethered binding), `#1723` (allow
reversible state changes).

These five together are one design change: making SS applications accept ordinary rule-based
physiology instead of a special-cased subset. Doing them piecemeal risks five partial relaxations
of the same validator. **Recommend treating as one work item with five acceptance criteria.**

**b) Multiple-sim-run infrastructure (4)** — `#1726` (raise the cap to 50), `#1727` (proper
per-run status), `#1728` (allow more than 2 runs, with a comprehensible quota message).
Plus `#1734` (batch jobs stay `RUNNING` in the DB after SLURM completion — a detailed 3.9k-char
reconciliation-gap analysis).

`#1734` is a prerequisite hiding in plain sight: raising the run cap while completed runs still
show as `RUNNING` makes the status problem worse, not better. **Sequence `#1734` before `#1726`.**

**c) Results & visualization (3)** — `#1730` (3D visualizations/movies as in native SS), `#1731`
(cluster-size-distribution histograms in multi-run results), plus `#1732` (parameter overrides and
scans — *"until this is implemented, do not allow them"*).

`#1732` contains its own interim fix: **disable the broken path**. That guard is small, independent
of the feature, and stops users hitting a workflow that silently does nothing. It pairs exactly
with `#1713` ("User is allowed to change rate constants for Spring SaLaD apps, but change is not
applied and no error is announced") — **`#1713` and `#1732` are the same defect**, one reported and
one groomed. Merge them, and ship the guard now regardless of when the feature lands.

---

## Validation correctness (3) — the 2026-08 findings

`#1874` (the diffusion-limited rate check is not unit-aware, so some `Kf` values are read **~600×
too lenient**), `#1875` (`checkOnRate`'s own documented "marginally acceptable" example fails its
own check), `#1876` (four published applications fail validation and cannot generate math).

These came out of adding SpringSaLaD coverage to `MathGenCompareTest`, and they interlock: `#1874`
is a wrong unit conversion in the check, `#1875` is evidence the check has been wrong since it was
written, and `#1876` is four real models it now rejects. They should be fixed and verified as one.

**All three are off-board.** `#1874` in particular says validation has been accepting rates that
are physically impossible by a factor of ~600 — that is a correctness claim about published
science, and it is not on any planning surface.

---

## Structural sites (2 open + 1 to close)

`#1686` (**close** — PR #1685 is merged, see [01](01-close-and-verify.md)), `#1688` (structural
site specification needs a graphical interface — currently pair-by-pair manual selection and
hand-edited coordinates), `#1689` (3D visualization of structural sites).

`#1689`'s body carries a useful design judgment: the original SpringSaLaD used a 3D editing
interface and *"editing was an extremely difficult task"*, which is why VCell chose a 2D
cross-section. So `#1689` is explicitly view-only, not an editor. Worth preserving that
distinction — it is the kind of rationale that gets lost and re-litigated.

---

## Export and external analysis (2, probably 1)

`#1657` ("simularium export for SpringSaLaD") and `#1658` ("export SpringSaLaD sim results for
external viewing/analysis") — both `Active`, **both with completely empty bodies**, both created
2026-04-01, both by @jcschaff.

`#1657` looks like a specific implementation of `#1658`'s general goal (Simularium being one
external viewer). Either merge, or make `#1657` a child of `#1658`. Also overlaps `#1730` (3D
visualizations in VCell). Three issues, one underlying question: *where do SpringSaLaD results get
looked at?*

---

## Solver and infrastructure (4)

`#1482` (Langevin solver code maintenance — `High Priority`, 11 comments, three distinct workstreams
in a 154-char body: fix bugs, implement new algorithms, add client sanity checks), `#1564`
(automate Dan's SpringSaLaD build, which currently requires Apple certs and manual steps — Priority
12/8, the highest-ranked SpringSaLaD item on the board), `#1660` (NPE in
`ParticleMathMapping.refreshMathDescription`, with the offending code quoted), `#1260` (sites
lacking states rejected by SS applications).

`#1482` should be split. An umbrella with 11 comments and three unrelated workstreams cannot be
closed, and "add sanity checks in the client" is plausibly already covered by `#1874`/`#1875`.

---

## Recommendations

1. **Board all 26 off-board issues.** This is the single highest-value action for this group.
2. **Consolidate to one epic** (`#1729`); close `#870` as MVP-delivered, moving its two research
   items (curved surfaces, parallelization) to standalone issues.
3. **Merge `#1713` into `#1732`** and ship the disable-the-broken-path guard independently.
4. **Sequence `#1734` before `#1726`/`#1727`/`#1728`.**
5. **Treat `#1719`–`#1723` as one compatibility work item.**
6. **Fix `#1874`/`#1875`/`#1876` together**, and treat `#1874` as the highest-severity item in the
   group — it is a silent correctness failure, not a usability gap.
7. **Split `#1482`**; merge `#1657`/`#1658`; demote `#1508`/`#1509` from `Type: Epic` to children.

---

## All 32

| # | Title | Opened | Board status | Pri/Imp | Simplicity | Flags |
|---|---|---|---|---|---|---|
| [#1564](https://github.com/virtualcell/vcell/issues/1564) | Automate Dans SpringSalad Build | 2025-07 | Queued | 12/8 | Moderate&nbsp;(4) | — |
| [#870](https://github.com/virtualcell/vcell/issues/870) | Epic: MVP for Spring SaLaD in VCell | 2023-04 | Active | — | — | HP EPIC |
| [#1260](https://github.com/virtualcell/vcell/issues/1260) | Sites that lack states in physiolology are not accepted by SS application (but should … | 2024-05 | Pool | — | Intricate&nbsp;(2) | thin |
| [#1262](https://github.com/virtualcell/vcell/issues/1262) | Update Object Properties for Molecular Structures | 2024-05 | **off-board** | — | — | — |
| [#1268](https://github.com/virtualcell/vcell/issues/1268) | Set z coordinate of membrane-bound site to 0 | 2024-05 | **off-board** | — | — | thin |
| [#1482](https://github.com/virtualcell/vcell/issues/1482) | Langevin Solver code maintenance | 2025-04 | Pool | — | Unknown&nbsp;(0) | HP |
| [#1508](https://github.com/virtualcell/vcell/issues/1508) | Displaying statistics for multiple runs | 2025-05 | — | — | — | HP EPIC |
| [#1509](https://github.com/virtualcell/vcell/issues/1509) | Displaying cluster analysis results | 2025-05 | — | — | — | HP EPIC |
| [#1657](https://github.com/virtualcell/vcell/issues/1657) | simularium export for SpringSaLaD | 2026-04 | Active | — | — | thin |
| [#1658](https://github.com/virtualcell/vcell/issues/1658) | export SpringSaLaD sim results for external viewing/analysis | 2026-04 | Active | — | — | thin |
| [#1660](https://github.com/virtualcell/vcell/issues/1660) | Null pointer in ParticleMathMapping.refreshMathDescription | 2026-04 | Pool | — | — | — |
| [#1688](https://github.com/virtualcell/vcell/issues/1688) | Structural Site specification should have graphical interface | 2026-05 | Pool | — | — | — |
| [#1689](https://github.com/virtualcell/vcell/issues/1689) | 3D vizualization of structural sites | 2026-05 | Pool | — | — | — |
| [#1713](https://github.com/virtualcell/vcell/issues/1713) | Parameter overrides are not allowed in spring SaLaD | 2026-06 | Pool | — | — | — |
| [#1719](https://github.com/virtualcell/vcell/issues/1719) | SpringSaLaD: do not require Extracellular/Membrane/Intracellular compartment names | 2026-07 | **off-board** | — | — | HP |
| [#1720](https://github.com/virtualcell/vcell/issues/1720) | SpringSaLaD: allow additional compartments that are ignored in an SS Application | 2026-07 | **off-board** | — | — | HP |
| [#1721](https://github.com/virtualcell/vcell/issues/1721) | SpringSaLaD: support allosteric reactions without explicit bound/unbound states + swit… | 2026-07 | **off-board** | — | — | — |
| [#1722](https://github.com/virtualcell/vcell/issues/1722) | SpringSaLaD: allow volumetric rate constants for binding of membrane-tethered sites | 2026-07 | **off-board** | — | — | — |
| [#1723](https://github.com/virtualcell/vcell/issues/1723) | SpringSaLaD: relax the requirement that all state changes be irreversible | 2026-07 | **off-board** | — | — | — |
| [#1724](https://github.com/virtualcell/vcell/issues/1724) | SpringSaLaD: copying an SS Application does not correctly copy molecular structures | 2026-07 | **off-board** | — | — | HP |
| [#1725](https://github.com/virtualcell/vcell/issues/1725) | SpringSaLaD: review error/warning handling when a reaction is too fast for the diffusi… | 2026-07 | **off-board** | — | — | HP |
| [#1726](https://github.com/virtualcell/vcell/issues/1726) | SpringSaLaD: increase the number of simultaneous multiple sim runs to 50 | 2026-07 | **off-board** | — | — | — |
| [#1727](https://github.com/virtualcell/vcell/issues/1727) | SpringSaLaD: provide proper status for multiple sim runs | 2026-07 | **off-board** | — | — | — |
| [#1728](https://github.com/virtualcell/vcell/issues/1728) | SpringSaLaD: allow more than 2 multiple sim runs (or a permission/request flow) with a… | 2026-07 | **off-board** | — | — | — |
| [#1729](https://github.com/virtualcell/vcell/issues/1729) | SpringSaLaD in VCell — bugs & high-priority upgrades (epic) | 2026-07 | **off-board** | — | — | EPIC |
| [#1730](https://github.com/virtualcell/vcell/issues/1730) | SpringSaLaD: create 3D visualizations/movies as in the native SS | 2026-07 | **off-board** | — | — | — |
| [#1731](https://github.com/virtualcell/vcell/issues/1731) | SpringSaLaD: include cluster-size-distribution histograms in multiple-run results | 2026-07 | **off-board** | — | — | — |
| [#1732](https://github.com/virtualcell/vcell/issues/1732) | SpringSaLaD: enable parameter overrides & parameter scans (disable until implemented) | 2026-07 | **off-board** | — | — | HP |
| [#1734](https://github.com/virtualcell/vcell/issues/1734) | SpringSaLaD/Langevin batch jobs stay RUNNING in DB after SLURM completion (reconciliat… | 2026-07 | **off-board** | — | — | — |
| [#1874](https://github.com/virtualcell/vcell/issues/1874) | SpringSaLaD: diffusion-limited rate check is not unit-aware, so some Kf values are rea… | 2026-08 | **off-board** | — | — | — |
| [#1875](https://github.com/virtualcell/vcell/issues/1875) | SpringSaLaD: checkOnRate's documented "marginally acceptable" example fails its own ch… | 2026-08 | **off-board** | — | — | REF |
| [#1876](https://github.com/virtualcell/vcell/issues/1876) | SpringSaLaD: four published applications fail validation and cannot generate math | 2026-08 | **off-board** | — | — | — |

**Flags:** `HP` = High Priority label · `BLK` = Blocked · `EPIC` = Type: Epic · `REF` = To Refine · `thin` = body under 80 chars
