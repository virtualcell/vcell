# Board Hygiene

Findings about the *bookkeeping* rather than the work: where board #1 and the issue tracker
disagree, and where the metadata has stopped meaning what it says.

These are cheap to fix and they block the rest of grooming, because prioritization done on top of
bad metadata just relocates the mess.

---

## 1. The Priority / Importance polarity is undocumented — resolve this first

Board #1 carries two numeric fields:

- **`Priority`** — set on **57 issues**, all in status `Queued`, values 1–12
- **`Importance`** — set on **61 issues**, values 1–10

They are **strongly correlated (r = 0.84, n = 57)**, so they move together and largely encode one
judgment expressed twice.

**I could not determine which end is "more important," and the data does not settle it.**

The obvious tiebreaker fails. Of the 28 issues carrying the `High Priority` label, only 8 also
carry a numeric `Priority`, and those 8 are spread across the range — `2, 3, 4, 4, 4, 5, 12, 12`.
Mean Priority for `High Priority`-labelled issues is 5.75 against 6.51 for the rest: a tilt toward
"lower = more urgent," but on n=8 that is noise, not evidence.

Reading it the two ways gives opposite slates:

| If **1 = most urgent** | If **12 = most urgent** |
|---|---|
| `#1082` Testing Framework RPC DataAccessException | `#1384` Give VCell a dedicated submit node |
| `#563` `#522` `#523` `#912` `#158` (roundtrip/validation cluster) | `#792` Smart copy/paste redesign |
| `#498` `#1605` (repeated tasks, colorblindness) | `#1292` Webapp uses Auth0 dev keys |
| | `#1564` `#1555` `#167` `#1607` |

Both readings are coherent, which is exactly why it needs an answer rather than a guess. The
`Simplicity` field on the same board *is* documented in its option labels (`Simple (5)` …
`Byzantine (1)`, so **higher = easier**), which mildly suggests higher = more on the other two,
but `Simplicity` and `Priority` are different kinds of quantity and I would not lean on it.

> **Action:** whoever set these fields — one sentence in the board README, and add the scale to
> the field description so it survives. Everything downstream in these docs is written
> polarity-neutral (`Pri/Imp` shown as raw values) until this is answered.

---

## 2. Three prioritization mechanisms that disagree

| Mechanism | Count | Problem |
|---|---:|---|
| Board `Priority`/`Importance` | 57 / 61 | Polarity undocumented (above) |
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

## 3. Fifty-five issues are not on the board

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

> **Action:** bulk-add all 55 to the board with status `Pool`, then triage. This is one of the
> cheapest meaningful improvements available — perhaps 30 minutes — and it roughly doubles the
> board's coverage of 2026 work.

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
| Document Priority/Importance polarity | 5 min | Makes the ranked slate readable |
| Delete 4 stale release labels (45 issues) | 10 min | Removes actively misleading signal |
| Bulk-add 55 off-board issues | 30 min | Board covers 2026 work |
| Close the 4 `Done`-but-open issues | 15 min | Tracker and board agree |
| Demote stale `Active` → `Queued`/`Pool` | 15 min | `Active` becomes trustworthy |
| Reduce 3+ assignees to one owner | 45 min | Creates accountability on 33 issues |
| Auto-add rule for new issues | 15 min | Stops the drift recurring |
| Migrate epic links to native sub-issues | 2 h | One mechanism instead of three |
