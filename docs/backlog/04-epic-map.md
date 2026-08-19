# Epic Map, Overlaps, and the Five Decisions

24 issues act as epics (23 carry `Type: Epic`; `#1751` is an epic in everything but its label).
Between them their checklists claim **103 open issues** as children. **150 open issues belong to
no epic at all.**

This document covers three things: what the epics currently contain, where two epics are competing
for the same work, and the five decisions that determine whether large parts of the backlog are
worth ranking.

---

## 1. All 24 epics at a glance

`open-children` counts distinct open issues referenced from the epic body. `done`/`todo` are
checklist boxes.

| # | Board | Open children | done/todo | Last updated | Title |
|---|---|---:|---|---|---|
| [#1038](https://github.com/virtualcell/vcell/issues/1038) | Pool | **33** | 10/37 | 2026-07-29 | Epic: VCell UI fixes |
| [#1036](https://github.com/virtualcell/vcell/issues/1036) | Queued | **28** | 2/30 | 2026-08-18 | Epic: public vcell models as SBML/Omex |
| [#1729](https://github.com/virtualcell/vcell/issues/1729) | off-board | **26** | 0/13 | 2026-07-08 | SpringSaLaD in VCell — bugs & high-priority upgrades |
| [#1033](https://github.com/virtualcell/vcell/issues/1033) | Pool | 6 | 1/6 | 2025-04-29 | Epic: improve NFSim multitrial statistics |
| [#1039](https://github.com/virtualcell/vcell/issues/1039) | Pool | 4 | 0/4 | 2023-11-15 | Epic: Solver Regression Tests |
| [#870](https://github.com/virtualcell/vcell/issues/870) | Active | 3 | 8/5 | 2026-04-01 | Epic: MVP for Spring SaLaD in VCell |
| [#1040](https://github.com/virtualcell/vcell/issues/1040) | Active | 3 | 10/3 | **2024-07-19** | Epic: new vcell-rest with Quarkus |
| [#1803](https://github.com/virtualcell/vcell/issues/1803) | off-board | 3 | 0/0 | 2026-07-30 | [Epic] Web-based field & geometry visualization (vtk.js/vtk.wasm) |
| [#1031](https://github.com/virtualcell/vcell/issues/1031) | Pool | 2 | 4/5 | 2025-04-15 | Epic: enhance user docs and training |
| [#1032](https://github.com/virtualcell/vcell/issues/1032) | Queued | 2 | 4/5 | **2023-11-15** | Epic: migrate to postgresql |
| [#1046](https://github.com/virtualcell/vcell/issues/1046) | Queued | 2 | 19/2 | 2025-01-07 | Epic: biosimulators_vcell nonspatial |
| [#1069](https://github.com/virtualcell/vcell/issues/1069) | Queued | 2 | 0/2 | **2023-12-05** | Epic: biosimulators_vcell spatial |
| [#1147](https://github.com/virtualcell/vcell/issues/1147) | Blocked | 2 | 3/8 | 2025-04-29 | Epic: Quarkus REST endpoints for VCell API |
| [#1034](https://github.com/virtualcell/vcell/issues/1034) | Pool | 1 | 0/1 | **2023-11-15** | Epic: local use of field data |
| [#1035](https://github.com/virtualcell/vcell/issues/1035) | Active | 1 | 0/4 | **2024-01-10** | Epic: general reactions with stochastic simulations |
| [#1339](https://github.com/virtualcell/vcell/issues/1339) | Pool | 1 | 2/1 | 2025-03-06 | Epic: Simulation Control Update |
| [#1037](https://github.com/virtualcell/vcell/issues/1037) | Active | **0** | 7/4 | **2024-07-19** | Epic: modern auth with Keycloak and Auth0 OIDC |
| [#1199](https://github.com/virtualcell/vcell/issues/1199) | Pool | **0** | 0/4 | 2026-08-04 | Epic: Refactor Data Export Services in VCell |
| [#1556](https://github.com/virtualcell/vcell/issues/1556) | Queued | **0** | 0/7 | 2025-06-30 | Add Steady State (COPASI-style) via CVODE |
| [#1603](https://github.com/virtualcell/vcell/issues/1603) | Queued | **0** | 0/0 | 2026-01-21 | [EPIC] UConn Health Accessibility Guidelines |
| [#1652](https://github.com/virtualcell/vcell/issues/1652) | Pool | **0** | 0/3 | 2026-03-11 | Rework VFRAP workflows (Geometry + Image Data) |
| [#1751](https://github.com/virtualcell/vcell/issues/1751) | off-board | **0** | 0/0 | 2026-07-24 | VCell Desktop UI bugs + AI code-test fixturing |
| [#1508](https://github.com/virtualcell/vcell/issues/1508) | off-board | 0 | 0/0 | 2025-05-20 | Displaying statistics for multiple runs |
| [#1509](https://github.com/virtualcell/vcell/issues/1509) | off-board | 0 | 0/0 | 2025-05-20 | Displaying cluster analysis results |

### Observations

**Three epics carry 87 of the 103 claimed children** (`#1038`, `#1036`, `#1729`). The rest are
small, and eight have **no open children at all**.

**`#1508` and `#1509` are labelled `Type: Epic` but are ordinary feature requests** — one-line
bodies, no children, both about SpringSaLaD multi-run display. They should lose the label and
become children of the SpringSaLaD epic. (`#1508` overlaps `#1731`; `#1509` overlaps `#1731`'s
cluster-histogram scope.)

**`#1603` and `#1751` are epics with no children and no checklist** — accessibility and the
current desktop-UI focus respectively. Both name real initiatives; neither has been decomposed.
`#1603`'s four obvious children (`#1604`, `#1605`, `#1606`, plus the font work) exist as separate
issues but are not linked from it.

**Six epics have not been updated in over 18 months** (`#1032`, `#1034`, `#1035`, `#1037`,
`#1040`, `#1069`) while three of them sit in `Active`.

---

## 2. Four overlapping epic pairs

These are the ones where two containers claim the same work. Each needs a merge or an explicit
split of scope.

### A. `#1038` (UI fixes, 2023) vs `#1751` (Desktop UI bugs, 2026)

`#1038` is a 33-child, 47-checkbox list from Nov 2023 covering "all VCell UI bugs."
`#1751` is a July 2026 tracking epic for the **current** desktop-UI effort, described in its body
as "the current VCell development focus (per the 2026-07-22 VCell bi-weekly)" — notably window
z-order, which is also `#1038`'s territory (`#429`, `#1441`, `#1078`).

These are the same initiative three years apart. `#1038` has the inventory; `#1751` has the
mandate and is off-board. **Merge:** keep `#1751` as the live epic, migrate `#1038`'s open
children into it, close `#1038` — or explicitly scope `#1038` to the 2022-cohort backlog and
`#1751` to current-focus work. Either is fine; having both silently is not.

### B. `#1199` (Refactor Data Export Services) vs `#1008` (VCell Export needs a face-lift)

`#1008` (Oct 2023): *"The code surrounding export [is] almost 2 decades old, and could use a
re-write with modern best practices… inefficiencies… CLI has a 50% slowdown vs GUI."*
`#1199` (Mar 2024): *"VCell Export code mostly works, but is unwieldy… development with the
exporter is slow."*

Same subsystem, same complaint, same proposed remedy. `#1199` is the epic and carries Importance 9
— second only to `#1384` — but has **zero open children**, while roughly a dozen
export issues sit unlinked in [13-export-visualization.md](13-export-visualization.md).
**Merge `#1008` into `#1199` and populate `#1199`'s children.**

### C. `#870` (SpringSaLaD MVP) vs `#1729` (SpringSaLaD bugs & upgrades) vs `#1482` (Langevin solver maintenance)

Three overlapping SpringSaLaD containers. `#870` is the 2023 MVP epic, `Active`, with 8 of 13
boxes done and three open children (`#1482`, `#1508`, `#1509`). `#1729` is the July 2026 epic from
Les Loew's request list with 13 children. `#1482` is itself a multi-part umbrella ("fix bugs in
the solver / implement new algorithms / add sanity checks in the client") with 11 comments.

`#870`'s remaining scope ("support for curved surfaces", "support for parallelization") is
long-term research; `#1729` is the near-term user-driven list. **Recommend:** close `#870` as
delivered (the MVP shipped), move its two research items to new standalone issues, and make
`#1729` the single SpringSaLaD epic. See [12-springsalad.md](12-springsalad.md).

### D. `#1040` / `#1147` / `#1339` — three Quarkus/API epics

`#1040` (new vcell-rest with Quarkus, `Active`, 10/13 done), `#1147` (Quarkus REST endpoints,
`Blocked`, 3/11 done), `#1339` (Simulation Control Update, 2/3 done). All three describe stages of
the same migration; `#1040`'s remaining "implement Java Serialization-based RPC endpoints as REST
API" is `#1147`'s entire subject matter.

The Quarkus REST service exists and ships (`vcell-rest/` has 14 resource classes). What remains is
endpoint-by-endpoint migration, which is what [14-api-platform.md](14-api-platform.md) enumerates.
**Recommend:** close `#1040` (the service was built), keep `#1147` as the migration epic, fold
`#1339` into it.

---

## 3. The five decisions

Each of these gates a double-digit number of issues. **Ranking work behind an unmade decision is
wasted ranking** — this is the argument for taking them before the refinement pass, not after.

### Decision 1 — Is the PostgreSQL migration still happening?

**Gates:** `#1032` (epic), `#172`, `#840`, `#1994`, and the shape of every future DB change.

`#1032` was written to "enable cloud hosting of VCell" and "remove dependency on Oracle." It has
not been updated since **Nov 2023**, and its three infrastructure tasks (local Postgres dev DB,
production Postgres in the UCHC cluster, final data migration) are all unchecked.

Meanwhile the working tree shows **production is still Oracle** —
`vcell-rest/src/main/resources/application.properties` points at
`jdbc:oracle:thin:@vcell-oracle.cam.uchc.edu:1521/ORCLPDB1` — while tests run on PostgreSQL via
testcontainers. `#1994` documents the direct cost of that split: **55 SQL-dialect branches across
33 files are exercised only against PostgreSQL in CI and only against Oracle in production.**

So the migration is half-done in a way that is actively expensive. Three coherent answers:

1. **Finish it** — `#1032` becomes real work, `#1994` is temporary scaffolding.
2. **Abandon it** — close `#1032` and `#172`, and `#1994` becomes important, because the dual-dialect
   code is then permanent and permanently under-tested.
3. **Freeze it** — keep both dialects deliberately, document why.

All three are defensible. What is not defensible is the current state, where the epic implies (1)
and reality is (3) by accident. → **@jcschaff**

This is the one issue in the backlog whose group assignment is "pending a decision":

| # | Title | Opened | Board status | Pri/Imp | Simplicity | Flags |
|---|---|---|---|---|---|---|
| [#1032](https://github.com/virtualcell/vcell/issues/1032) | Epic: migrate to postgresql | 2023-11 | Queued | — | — | EPIC |

### Decision 2 — Desktop PDE viewer, or browser viewer?

**Gates:** `#191`, `#1494`, `#898`, `#950`, `#772`, `#986`, `#1859`, `#1867`, `#1879`, `#1803`,
and much of [13-export-visualization.md](13-export-visualization.md).

There is significant recent investment in the **browser-based** field viewer (`#1803` epic,
`#1859`, `#1867`, `#1879` — shipped through the 8.0.9/8.0.10 line). There is also a backlog of
**desktop** PDE-viewer work, including `#191` ("new GUI design… created and approved by group",
Importance 6) and `#1494` (lazy-load microscopy data).

If the browser viewer is the destination, then several desktop viewer issues are investments in a
component being retired, and should be closed or explicitly deferred rather than ranked. If the
desktop viewer remains primary for years, `#191`'s approved design matters and should be scheduled.

This needs to be stated once, in writing, in `#1803`. → **@jcschaff**

### Decision 3 — What is the standing of the 2022 cohort?

**Gates:** 73 issues, roughly 34 of them bodyless.

Covered in [02-needs-refinement.md](02-needs-refinement.md). The decision is not per-issue: it is
whether to run one authors' review session or to declare a **bulk-close with a stated policy**
(e.g. "issues opened before 2023 with no body and no activity since 2023 are closed; reopen on
request"). Both are legitimate. Grinding through them individually is the option that will not
finish. → **@ACowan0105 + @vcellmike + @jcschaff**

### Decision 4 — Is accessibility a funded mandate with a date?

**Gates:** `#1603`, `#1604`, `#1605`, `#1606` — and potentially a large share of the 51 UI issues.

`#1603` says *"Due to newly implemented accessibility policies introduced at UConn Health"* — an
external compliance requirement, not a preference. `#1606` (font sizes) is rated **`Byzantine (1)`**,
the hardest tier on the board, and prior work confirms it: the blocker is pinned pixel constants
throughout the Swing UI, not font literals.

If there is a compliance deadline, this outranks most of the UI backlog and should be resourced as
a project. If there is not, it is four large issues competing with everything else. The four are
`Queued` at Priority 3–5 today, which does not look like either answer.
→ **@CodeByDrescher / whoever owns the UConn Health relationship**

### Decision 5 — Do we still pursue full BioSimulators/SED-ML conformance?

**Gates:** `#1036` (28 children), `#1046`, `#1069`, and most of
[11-standards-interop.md](11-standards-interop.md) — 42 issues in total, the second-largest group.

`#1046` (nonspatial) is 19/21 done — nearly finished. `#1069` (spatial) is **0/2 done, both
children `Blocked`**, one of them (`#1048`) with an empty body, and its blocker is external:
*"blocked until an official biosimulations spatial standard is both established and integrated."*
It has not been updated since Dec 2023.

So the nonspatial track is worth finishing and the spatial track is blocked on a standard that may
never arrive. Treating them as one programme obscures both. **Recommend:** close `#1069` as
externally blocked with a note on what would revive it, and drive `#1046` to completion — it is two
issues from done. The separate question of how much of `#1036`'s 28-child roundtrip-fidelity work
is worth doing is a genuine scope call. → **@moraru / @CodeByDrescher**

---

## 4. The 150 orphans

150 open issues are referenced by no epic. This is not inherently wrong — not everything needs a
parent — but it does mean the epic structure describes only about a third of the backlog, and the
part it describes is skewed old.

The orphan set includes nearly all 2026 work: the infrastructure issues (`#1888`, `#1921`, `#1922`,
`#1926`, `#1978`, `#1994`), the SBML export findings (`#1905`, `#1981`, `#1984`), the data-loss
investigation (`#1980`), and the desktop crash fixes (`#1747`, `#1848`, `#1849`).

Two readings, both probably true: recent work is better described *and* less ceremonious, so it
does not get filed into epics; and the epic structure was built in one burst in Nov 2023
(`#1031`–`#1040` were all created on 2023-11-13/14) and has not been maintained as a live
instrument since.

> **Recommendation:** do not retro-fit epics onto the orphans. Instead, let the thematic groups in
> docs 10–19 carry the organization, and keep epics only where an epic has an owner and a real
> decomposition. On that test, roughly 8 of the 24 current epics survive.
