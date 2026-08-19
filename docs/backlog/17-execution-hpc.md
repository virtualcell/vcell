# Group: Simulation Execution & HPC — 12 issues

Getting simulations to run on the cluster, tracking their state honestly, and not losing their
results. The smallest technical group, but it contains the backlog's only **data-loss** issue.

No epic covers this group.

---

## Sub-themes

### Data integrity — results vs. what the database claims (3)

`#537` (reconcile database status with the filesystem — Priority 4/4, `High Priority`), `#158`
(run all published models currently marked "never ran" — Priority 2/2, `High Priority`), `#1980`
(five confirmed dataset losses on published models, all from 2004–2005 — find the extent and the
cause).

These three are one problem seen at three moments. `#537` sets out a two-step reconciliation:
where the DB claims results exist but the filesystem disagrees, correct the DB; where results
exist but the DB says otherwise, correct the DB the other way. `#158` is about published models
whose status says they never ran. `#1980` is the 2026 forensic follow-up that found actual losses.

`#1980` is the only issue in the backlog documenting **permanently lost scientific data on
published models**, and it is off-board with no assignee. Whatever its priority relative to feature
work, it should at minimum be visible.

> **Recommendation:** treat `#537`, `#158`, `#1980` as one workstream with a single owner. Doing
> `#537`'s reconciliation without `#1980`'s cause analysis risks writing "never ran" over records
> that are actually evidence of a loss.

### Moving-boundary solver (4)

`#146` (time plots and kymographs fail for moving-boundary simulations — `Shelved`), `#1577`
(retrieving remote moving-boundary results fails, with the full RPC error), `#1578` (remote
moving-boundary simulation fails with an integer overflow — `-2.71505e+09 < min value -2147483648`
at a named source line in `Voronoi32.cpp`), `#566` (simulations stick on mesh initialization for a
large planar membrane in flat 3D geometry — with a shared reproduction BioModel).

`#1577` and `#1578` were filed on the same day from the same investigation and are almost certainly
related; both are off-board. `#146` is `Shelved` and describes a third symptom of the same
subsystem being under-exercised.

`#1578`'s overflow is a concrete, located C++ bug with the value and the line number. That is
rare enough in this backlog to be worth acting on directly.

> **Recommendation:** group all four as "moving-boundary solver reliability." The mbsolver work in
> [13-export-visualization.md](13-export-visualization.md) (`#1879`) is touching the same area, so
> there may be shared context available now that was not available when `#146` was shelved.

### SLURM and scheduling (3)

`#1384` (give VCell a dedicated submit node — **Priority 12 / Importance 10, the highest Importance
on the board**; when HPC users launch big jobs on shared submit nodes, VCell submissions block),
`#168` (improve SLURM job-array efficiency for multiple stochastic trajectories, `High Priority`,
`Shelved`), `#169` (investigate whether parameter scans can be improved in SLURM — empty body,
`Shelved`).

`#1384` is an infrastructure request as much as a code change, and it is the kind of thing that
needs someone with a relationship to the HPC operators rather than an engineer. Note it may be
partly superseded — it is a 2024 issue and the deployment has moved substantially since.
**Verify it still describes current behaviour before acting.**

`#168` and `#169` are both `Shelved` SLURM-efficiency questions with thin bodies; they belong in
the refinement batch ([02](02-needs-refinement.md)).

### Server-vs-local discrepancies (2)

`#842` (a model runs locally but fails on the server with an invalid-character error — full RPC
error text, from @pmendes, an external user), `#213` (investigate errors in the "Actin Dendritic
Nucleation Detailed Branching" model — *"Actually assigned to Les but he does not yet have a
handle"*).

`#842` reads as the same class of problem as `#1674` in
[11-standards-interop.md](11-standards-interop.md) — an invalid XML character surviving into a
document that then fails to save. Worth checking whether they are the same bug; `#1674` has a much
fuller diagnosis.

`#213` should be closed or completely rewritten — see [02](02-needs-refinement.md).

---

## Observation

This group has the highest concentration of `Shelved` status (5 of 12) and the highest
concentration of `High Priority` labels relative to size (4 of 12). Those two facts sitting
together is itself a signal: work was labelled urgent and then parked, without the label being
removed or the parking being explained.

Shelving is legitimate. Shelving something still labelled `High Priority` and leaving no note about
why is how a backlog stops meaning anything.

---

## Recommendations

1. **One owner for the data-integrity trio** (`#537`, `#158`, `#1980`); sequence `#1980`'s cause
   analysis before `#537`'s corrective writes.
2. **Board `#1577`, `#1578`, `#1980`.**
3. **Group the four moving-boundary issues**; `#1578` is directly actionable today.
4. **Verify `#1384` still describes current behaviour** — highest Importance on the board, but from
   2024 and infrastructure-dependent.
5. **Check whether `#842` and `#1674` are the same defect.**
6. **Resolve the `Shelved` + `High Priority` contradiction** on `#168` and `#185` — either
   un-shelve or drop the label, and record why.

---

## All 12

| # | Title | Opened | Board status | Pri/Imp | Simplicity | Flags |
|---|---|---|---|---|---|---|
| [#158](https://github.com/virtualcell/vcell/issues/158) | Run all published models with status never ran | 2022-07 | Queued | 2/2 | Unknown&nbsp;(0) | HP REF |
| [#537](https://github.com/virtualcell/vcell/issues/537) | Reconcile database status with filesystem for sim results | 2022-11 | Queued | 4/4 | Unknown&nbsp;(0) | HP |
| [#1384](https://github.com/virtualcell/vcell/issues/1384) | Give Dedicated Submit Node to VCell (and/ or give more inteligent code to select submi… | 2024-11 | Queued | 12/10 | Intricate&nbsp;(2) | — |
| [#146](https://github.com/virtualcell/vcell/issues/146) | Results Graphing Functions errors for Moving Boundary Simulations | 2022-05 | Shelved | — | Unknown&nbsp;(0) | — |
| [#168](https://github.com/virtualcell/vcell/issues/168) | Improve efficiency of slurm job arrays for multiple stochastic trajectories | 2022-07 | Shelved | — | — | HP thin |
| [#169](https://github.com/virtualcell/vcell/issues/169) | Investigate whether parameter scans can be improved in Slurm. | 2022-07 | Shelved | — | — | REF thin |
| [#213](https://github.com/virtualcell/vcell/issues/213) | Investigate errors in simulations in "Actin Dendritic Nucleation Detailed Branching | 2022-07 | Shelved | — | — | REF thin |
| [#566](https://github.com/virtualcell/vcell/issues/566) | Simulations get stuck on initializing mesh for large planar membrane in flat 3D geometry | 2022-11 | Shelved | — | — | — |
| [#842](https://github.com/virtualcell/vcell/issues/842) | Error running in the server but no problem running local (invalid character) | 2023-03 | Pool | — | — | — |
| [#1577](https://github.com/virtualcell/vcell/issues/1577) | retrieving remote moving boundary results fails | 2025-08 | **off-board** | — | — | — |
| [#1578](https://github.com/virtualcell/vcell/issues/1578) | remote moving boundary simulation fails with integer overflow | 2025-08 | **off-board** | — | — | — |
| [#1980](https://github.com/virtualcell/vcell/issues/1980) | Missing simulation datasets: 5 confirmed losses on published models, all 2004-2005 — f… | 2026-08 | **off-board** | — | — | — |

**Flags:** `HP` = High Priority label · `BLK` = Blocked · `EPIC` = Type: Epic · `REF` = To Refine · `thin` = body under 80 chars
