# Board Hygiene

Findings about the *bookkeeping* rather than the work: where board #1 and the issue tracker
disagree, and where the metadata has stopped meaning what it says.

These are cheap to fix and they block the rest of grooming, because prioritization done on top of
bad metadata just relocates the mess.

---

## 1. `Priority` is a derived field: **Priority = Importance + Simplicity**

Board #1 carries three scoring fields:

- **`Importance`** — set on **61 issues**, values 1–10. Higher = more valuable.
- **`Simplicity`** — set on **114 issues**, `Simple (5)` … `Byzantine (1)`, `Unknown (0)`.
  Higher = easier. (Documented in the option labels themselves.)
- **`Priority`** — set on **57 issues**, all in status `Queued`, values 1–12.

`Priority` is not an independent judgment. It is the sum of the other two, and the fit is
essentially exact: **56 of 57 rows satisfy `Priority = Importance + Simplicity`**, mean absolute
error 0.09. No other combination comes close:

| Formula | Exact matches | Mean abs. error |
|---|---:|---:|
| **`I + S`** | **56 / 57** | **0.09** |
| `I + (6 − S)` | 15 / 57 | 2.40 |
| `max(I, S)` | 9 / 57 | 1.98 |
| `I − S` | 9 / 57 | 5.28 |
| `I × S` | 3 / 57 | 5.28 |
| `(I + S) / 2` | 1 / 57 | 3.26 |

This resolves the ranking direction, which an earlier draft of this document could not settle:

> **Higher Priority = do sooner.** Priority 12 is the top of the queue; Priority 1 is the bottom.

It also explains the `Priority`/`Importance` correlation (r = 0.84) — they are not two opinions
that happen to agree, they are an input and a total. And it makes the scheme a **value/cost
model**: important work scores high, easy work scores high, and important *and* easy scores
highest. `#1384` (Importance 10, `Intricate (2)`) and `#1299` (Importance 1, `Simple (5)`) both
land at Priority 12 by different routes.

One consequence worth stating plainly: because ease is added rather than multiplied, a
`Byzantine (1)` item can never score above 11 no matter how important it is. `#1606` (font sizes,
accessibility) is Importance 3 + Simplicity 1 = 4, which is why the hardest accessibility work
sits low in the queue. That is the model working as designed, not a mis-ranking — but it is worth
knowing when reading [19-accessibility.md](19-accessibility.md).

### Applied 2026-08-19: five rows corrected

Three defects in the scoring data were found and **have since been fixed on the board**. Recorded
here because the reasoning matters more than the edit.

**`#1495` — arithmetic had drifted.** Stored Priority **5**, but Importance 6 + `Moderate (4)` =
**10**. Either the Importance was raised after Priority was computed, or it was a data-entry slip.
Recomputed to 10, which moves *"VCell Support Automated Email messages are too opaque to be very
useful"* from mid-pack to near the top of the queue.

**Four issues had `Importance` scored but `Priority` never computed** — so they sat in `Pool` and
never appeared in the ranked slate at all. All four have been given their computed Priority and
moved to `Queued`:

| # | Importance | Simplicity | Priority | Was | Title |
|---|---:|---|---:|---|---|
| [#1473](https://github.com/virtualcell/vcell/issues/1473) | 7 | `Simple (5)` | **12** | Pool, unranked | ImageJ N5 export metadata needs time array, origin, extent |
| [#1451](https://github.com/virtualcell/vcell/issues/1451) | 7 | `Simple (5)` | **12** | Pool, unranked | Add "Save as Local" to the error message |
| [#1199](https://github.com/virtualcell/vcell/issues/1199) | 9 | `Intricate (2)` | **11** | Pool, unranked | Epic: Refactor Data Export Services |
| [#191](https://github.com/virtualcell/vcell/issues/191) | 6 | `Intricate (2)` | **8** | Pool, unranked | New GUI design for spatial sim results viewer |

**`#1473` and `#1451` tie the highest score on the board** — important *and* rated `Simple (5)` —
and had been sitting in `Pool` since 2025. By the team's own formula they are among the best
available work.

Verified after the edit against a fresh read of the board: **61 issues now carry a Priority, zero
violate `Priority = Importance + Simplicity`, and nothing remains scored-but-unranked.**

> **Caveat on `#1199`:** it is an *epic* with zero linked children, so it is now queued as an empty
> container. Populating it (see
> [04-epic-map.md](04-epic-map.md#b-1199-refactor-data-export-services-vs-1008-vcell-export-needs-a-face-lift))
> or returning it to `Pool` are both reasonable; queuing it was done as instructed and is trivially
> reversible.

**53 issues have `Simplicity` but no `Importance`**, so they remain half-scored and cannot receive
a Priority until someone rates their value. That is the remaining gap in the scoring data.

> **Still to do:** record the formula in the board README so it survives — it is currently
> reconstructable only from the data. Consider making `Priority` a computed column rather than
> hand-entered, since hand-entry is what let `#1495` drift in the first place.

---

## 2. Three prioritization mechanisms that disagree

| Mechanism | Count | Problem |
|---|---:|---|
| Board `Importance` + `Simplicity` → `Priority` | 61 / 114 / 57 | Formula undocumented; 4 issues scored but never ranked; 53 half-scored (above) |
| `High Priority` label | 28 | Only 8 overlap the numeric fields; **5 are not on the board at all** |
| Release labels `Next Release`, `VCell-7.5.0/7.5.1/7.6.0` | 45 issues | Name shipped versions; we are on **8.0.27.01** |

The release labels are the clearest cleanup. `VCell-7.6.0` alone is on 26 open issues — a version
that was superseded before the 8.0 line began. `Next Release` on 19 issues has meant "next release"
for up to four years across dozens of actual releases.

> **Action:** delete all four release labels. If a genuine "ship in the next release" concept is
> needed, the board's `Next` status column already exists and is currently unused.

> **Action:** pick one of `High Priority` / numeric `Priority` and retire the other. Two
> prioritization systems that overlap on 8 of 28 issues are worse than either alone, because a
> reader cannot tell whether an unlabelled issue was judged unimportant or simply never seen by
> the other system.

---

## 3. Fifty-five issues were not on the board — **fixed 2026-08-19**

**These are disproportionately the best-written issues in the repo**, and they are invisible to
anyone planning from board #1. The board is stale at the *new* end, not the old end.

Notably absent: the **entire 2026 SpringSaLaD grooming set** (`#1719`–`#1734`, 14 issues, all with
structured bodies and provenance), the **field-viewer train** (`#1859`, `#1867`, `#1879`, `#1894`,
`#1964`), the **infrastructure set** (`#1888`, `#1921`, `#1922`, `#1926`, `#1978`, `#1994`), and
three epics (`#1729`, `#1751`, `#1803`).

Five of the 55 carry the `High Priority` label — so the board omits work the labels call urgent.

<details>
<summary><b>All 55 off-board issues</b></summary>

`#1262` `#1268` `#1330` `#1338` `#1341` `#1365` `#1423` `#1494` `#1577` `#1578` `#1598` `#1609`
`#1644` `#1712` `#1718` `#1719` `#1720` `#1721` `#1722` `#1723` `#1724` `#1725` `#1726` `#1727`
`#1728` `#1729` `#1730` `#1731` `#1732` `#1734` `#1747` `#1751` `#1777` `#1786` `#1803` `#1848`
`#1849` `#1859` `#1867` `#1874` `#1875` `#1876` `#1879` `#1888` `#1894` `#1905` `#1921` `#1922`
`#1926` `#1964` `#1978` `#1980` `#1981` `#1984` `#1994`

</details>

> **Done.** All 56 (the 55 below plus `#2005`, filed the same day) were added via
> `backlog_lint.py --fix-board`. They landed in the board's default column and **still need
> triaging into `Pool` and scoring** — being on the board is not the same as being groomed.
> The `not-on-board` check now reports zero, and the lint's committed baseline shrank from 233
> accepted findings to 178.

**Root cause worth fixing separately:** issues are evidently being added to the board by hand.
A GitHub Actions workflow or the project's built-in auto-add rule would stop this recurring.

---

## 4. Four issues are `Done` on the board but open in the tracker

`#166`, `#748`, `#1646`, `#1647` — covered individually in
[01-close-and-verify.md](01-close-and-verify.md). Two are confirmed complete against the repo.

Either the close step is being skipped after the card moves, or the card is moved optimistically.
Worth a one-line convention: **the issue is the source of truth; the card follows it.**

---

## 5. `Active` does not mean active

16 issues are in status `Active`. Their last-updated dates:

| # | Last updated | Title |
|---|---|---|
| `#1035` | **2024-01-10** | Epic: general reactions with stochastic simulations |
| `#1037` | **2024-07-19** | Epic: modern auth with Keycloak and Auth0 OIDC |
| `#1040` | **2024-07-19** | Epic: new vcell-rest with Quarkus |
| `#1534` | 2025-06-10 | General Help Updates for 7.7 |
| `#1543` | 2025-06-19 | Create Release Notes for Next Production Release |
| `#870` | 2026-04-01 | Epic: MVP for Spring SaLaD in VCell |
| `#1542` | 2026-04-01 | Export History Should Be Saved on the Server |
| `#1599` | 2026-04-01 | Make new release notes for vcell 7.7 |
| `#1654` | 2026-04-01 | Update vcell.org stack |
| `#1655` | 2026-04-01 | finish publication/curation PR |
| `#1657` | 2026-04-01 | simularium export for SpringSaLaD |
| `#1658` | 2026-04-01 | export SpringSaLaD sim results for external viewing/analysis |
| `#1686` | 2026-05-07 | Dan ss structural sites *(PR merged — close)* |
| `#1706` | 2026-06-10 | Publication Submission Pipeline seems to be broken |
| `#1708` | 2026-07-15 | Spatial geometry mapping — HELP section |
| `#1930` | 2026-08-14 | Copy/paste of overridden parameters in parameter scans |

**Three have not been touched in over two years.** Only `#1930` was updated this month. Six share
an identical 2026-04-01 timestamp, which is a bulk edit, not six people starting work.

> **Action:** reserve `Active` for work someone is doing *now*. Everything above that is not
> genuinely in flight moves back to `Queued` or `Pool`. `Active` is the one status a planner needs
> to be able to trust, and today it cannot be.

---

## 6. Thirty-three issues have three or more assignees

Five people are assigned to `#1591` (show reaction names in the reaction diagram). Four each to
`#168`, `#611`, `#792`, `#1441`, `#1473`, `#1565`, `#1593`, `#1884`.

In GitHub, multiple assignees means "these people are interested" — it does not create an owner,
and the practical effect is that nobody is accountable. Every one of these 33 is old or stalled,
which is consistent with that.

The pattern looks like assignment-as-notification: adding people so they see the issue. Watchers
or a mention does that without diluting ownership.

> **Action:** one assignee per issue — the person who will move it. Others become watchers.
> 71 issues currently have **no** assignee at all, so the corollary is that assignment is being
> used inconsistently in both directions.

---

## 7. The board's `Epic` field and the epics' own checklists disagree

Board #1 has a single-select `Epic` field with 17 options, set on **95 issues**. Separately, the
23 epic issues maintain markdown task-lists in their bodies, which claim **103 open issues** as
children.

These are two hand-maintained parallel structures for the same relationship, and they do not
match. The board field also lists epics from *other repos* (`imageJ MVP vcell-fiji#11`,
`solver builds vcell-solver#25`) that have no counterpart in this repo's epic set, and it has no
option for any epic created after `#1147` — so `#1199`, `#1339`, `#1556`, `#1603`, `#1652`,
`#1729`, `#1751`, `#1803` cannot be represented in it at all.

GitHub now has **native sub-issues** (the board already exposes `Parent issue` and
`Sub-issues progress` fields, currently unpopulated). That is one mechanism instead of three.

> **Action:** migrate epic→child links to native sub-issues, then delete the custom `Epic`
> single-select and stop maintaining checklists by hand. See [04-epic-map.md](04-epic-map.md)
> for what the current links actually contain.

---

## Summary of recommended board actions

| Action | Effort | Effect |
|---|---|---|
| Document the `Priority = Importance + Simplicity` formula | 5 min | Makes the ranked slate readable and reproducible |
| ~~Recompute `#1495`; queue `#1473`/`#1451`/`#1199`/`#191`~~ | — | **Done 2026-08-19** — board is now formula-consistent |
| Delete 4 stale release labels (45 issues) | 10 min | Removes actively misleading signal |
| ~~Bulk-add 55 off-board issues~~ | — | **Done 2026-08-19** — board covers 2026 work |
| Close the 4 `Done`-but-open issues | 15 min | Tracker and board agree |
| Demote stale `Active` → `Queued`/`Pool` | 15 min | `Active` becomes trustworthy |
| Reduce 3+ assignees to one owner | 45 min | Creates accountability on 33 issues |
| Auto-add rule for new issues | 15 min | Stops the drift recurring |
| Migrate epic links to native sub-issues | 2 h | One mechanism instead of three |
