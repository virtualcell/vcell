# VCell Backlog Grooming — Index

A snapshot-and-triage pass over **all 272 open issues** in `virtualcell/vcell`, cross-referenced
against [project board #1](https://github.com/orgs/virtualcell/projects/1).

**Snapshot taken:** 2026-08-19
**Nothing in this pass edits GitHub.** These are proposals. Every disposition is a recommendation
for a human to accept, reject, or amend.

---

## Read in this order

| Doc | What it is |
|---|---|
| [00-method.md](00-method.md) | The triage plan: dispositions, decision rules, who decides what, suggested sequencing |
| [01-close-and-verify.md](01-close-and-verify.md) | 14 issues that look done, obsolete, or not-a-backlog-item — with the evidence |
| [02-needs-refinement.md](02-needs-refinement.md) | 50 issues too thin to act on; what each one needs before it can be estimated |
| [03-board-hygiene.md](03-board-hygiene.md) | 55 issues off the board, 4 marked Done but open, the Priority-field ambiguity, label/field duplication |
| [04-epic-map.md](04-epic-map.md) | Epic → child coverage, the 4 overlapping epic pairs, and the 5 strategic decisions that gate large chunks of the backlog |

## Thematic groups

Every open issue lands in exactly one group. Counts sum to 272.

| Group | Count | Doc |
|---|---:|---|
| Desktop client UI (Swing) | 51 | [10-desktop-ui.md](10-desktop-ui.md) |
| Standards & interop (SBML / SED-ML / OMEX / BioSimulators) | 42 | [11-standards-interop.md](11-standards-interop.md) |
| SpringSaLaD / Langevin | 32 | [12-springsalad.md](12-springsalad.md) |
| Data export & visualization | 32 | [13-export-visualization.md](13-export-visualization.md) |
| API, platform & database | 30 | [14-api-platform.md](14-api-platform.md) |
| Math generation & solvers | 26 | [15-math-solvers.md](15-math-solvers.md) |
| Infrastructure, CI/CD & release ops | 19 | [16-infrastructure-ci.md](16-infrastructure-ci.md) |
| Close / verify-then-close | 14 | [01-close-and-verify.md](01-close-and-verify.md) |
| Simulation execution & HPC | 12 | [17-execution-hpc.md](17-execution-hpc.md) |
| User documentation & materials | 9 | [18-user-docs.md](18-user-docs.md) |
| Accessibility (UConn Health mandate) | 4 | [19-accessibility.md](19-accessibility.md) |
| Strategic decision (Postgres migration) | 1 | [04-epic-map.md](04-epic-map.md) |

---

## The shape of the backlog

**272 open issues. Half of them predate 2025.**

| Opened | Count |
|---|---:|
| 2021 | 1 |
| 2022 | 73 |
| 2023 | 41 |
| 2024 | 26 |
| 2025 | 57 |
| 2026 | 74 |

The 2022 cohort is the largest historical block and the weakest described — it came in as a bulk
import of a pre-existing to-do list (73 issues, mostly authored by `ACowan0105` on 2022-07-19/20),
and it shows: many have an empty body and a title that was a line item in a spreadsheet.
**71 issues have not been touched since 2024.**

### Board coverage

217 of 272 issues are on project board #1; **55 are not on it at all** — and those 55 are
disproportionately the *recent, well-written* ones (all the 2026 SpringSaLaD grooming issues, the
field-viewer train, the infra issues). The board is drifting out of date at the new end, not the old end.

| Board status | Count | Reading |
|---|---:|---|
| Pool | 99 | Accepted but unscheduled — the actual backlog |
| Queued | 63 | Ranked and ready; 57 of these carry a numeric Priority |
| Shelved | 27 | Deliberately parked |
| Active | 16 | Claimed as in-progress (**several are stale — see [03](03-board-hygiene.md)**) |
| Blocked | 6 | External dependency |
| Done | 4 | **Open but marked done — close candidates** |
| (unset) | 2 | |
| off-board | 55 | Never triaged onto the board |

### Description quality

| Body length | Count |
|---|---:|
| < 80 chars (title-only, effectively) | 54 |
| < 200 chars | 107 |
| median | 295 chars |

**107 of 272 issues — 39% — cannot be estimated or assigned from what is written in them.**
This is the single biggest obstacle to grooming, and it is why [02-needs-refinement.md](02-needs-refinement.md)
exists as its own document.

### Ownership

- **71 issues have no assignee.**
- **33 issues have 3 or more assignees** — which in practice means nobody owns them. `#611` and
  `#1591` each carry five.

---

## Where the team's prioritization already lives

Three separate, partly redundant prioritization mechanisms are in play. Reconciling them is
recommendation #1 in [00-method.md](00-method.md).

1. **Board `Importance` + `Simplicity` → `Priority`** — the most deliberate signal in the system,
   and a genuine value/cost model. `Priority` is **derived**: it is the sum of `Importance`
   (1–10, higher = more valuable) and `Simplicity` (1–5, higher = easier), exact on 56 of 57
   ranked issues. **Higher Priority = do sooner.** The formula is undocumented on the board and
   was reconstructed from the data; see [03-board-hygiene.md](03-board-hygiene.md), which also
   catches one row whose arithmetic has drifted (`#1495`) and four scored-but-never-ranked issues
   — two of which tie the highest score on the board and are sitting in `Pool`.
2. **`High Priority` label** — 28 issues. Only 8 of them also carry a numeric Priority, and those
   8 spread across the whole 2–12 range, so the label and the field are *not* saying the same thing.
3. **`Next Release` label** — 19 issues, and the `VCell-7.5.0` / `7.5.1` / `7.6.0` labels on 33
   more. The current release line is **8.0.27.01**. These labels are release-planning residue from
   three major versions ago and are now actively misleading.

---

## Method note

This pass read every issue title, label set, board field, and body (truncated to 450 chars), plus
the full checklists of all 23 epics. Where a disposition rests on a claim about the code — "this
was fixed", "this dependency is gone" — the claim was checked against the working tree or git log,
and the check is cited inline. Claims that were *not* verified are marked as such.
