# Group: Accessibility — 4 issues

The smallest group, and the only one driven by an **external compliance requirement** rather than
by user requests or engineering judgment. Separated from
[10-desktop-ui.md](10-desktop-ui.md) for that reason: if the mandate is real and dated, these four
do not compete with ordinary UI work — they precede it.

**Epic:** `#1603`, which has **no children and no checklist** despite the other three plainly
being its content.

**Strategic decision required** — see
[04-epic-map.md](04-epic-map.md#decision-4--is-accessibility-a-funded-mandate-with-a-date).

---

## The four

All four (`#1603` the epic, plus `#1604`, `#1605`, `#1606`) were created on 2025-12-05 by @CodeByDrescher, all four carry `High Priority`, all four
are assigned to @danv61 and @CodeByDrescher, and all four are `Queued`.

**Two of the four are rated `Byzantine (1)` — the hardest tier on the board.** Only 7 issues in the
entire backlog carry that rating and two of them are here. This is a large programme wearing the
clothes of four tickets.

---

## What each one actually involves

### `#1603` — the mandate
*"Due to newly implemented accessibility policies introduced at UConn Health, we need to take steps
to ensure all our GUI components are satisfactorily displayed, and use color blind pallets — both
for dark and light modes. This is a massive end[eavor]…"*

The body is a statement of the requirement, not a plan. What is missing and matters:
**is there a deadline, and is there an audit we will be measured against?** Those two answers
change everything downstream. If UConn Health has a compliance date, this outranks essentially all
other UI work. If it is aspirational, it is four large issues competing normally.

### `#1606` — font sizes
*"Right now, the only way to change font sizes is through manually changing resolutions. This is
unreasonable to ask users to do for accessibility reasons. This…is going to be a lot of work."*

The `Byzantine (1)` rating is accurate. Prior investigation of this codebase established that the
obstacle is **pinned pixel constants** (e.g. `setDividerLocation`) rather than font literals, and
that a JVM-level UI-scaling flag is not a solution — it magnifies rather than reflows, and does
nothing on macOS. A meaningful fraction of the client's layout code has to change for text to grow
without breaking panels.

This is the single largest piece of work in the accessibility group and probably one of the largest
in the backlog. It should be scoped as a project with its own phasing, not carried as one ticket.

### `#1604` — cross-OS appearance and dark mode
*"While originally we had a unified appearance for each operating system, over time we've let the
appearance fall by the wayside, even as 'dark mode' became featured in operating systems."*

Also `Byzantine (1)`. Overlaps `#1606` substantially — both require touching the same layout and
theming code — and overlaps `#1886` (Mac splash screen still showing VCell 7.0) and `#1785`
(cluttered database subpanel) in [10-desktop-ui.md](10-desktop-ui.md).

Dark mode is a strict superset of the colour work in `#1605`: any palette decision has to hold in
both modes.

### `#1605` — colorblindness
*"We need to verify that all views, menus, windows, etc. are colorblind-safe. The obvious targets
are model and results viewers but all areas should be evaluated."*

Rated `Intricate (2)` — the least hard of the three, and the most separable. It naturally splits
into an **audit** (cheap, produces a concrete list) and **remediation** (sized by what the audit
finds).

Note that the reaction diagram and the spatial results viewers encode meaning in colour by design —
species colours, catalyst indication (see `#176`), field colour scales (`#1867`). Those are not
incidental decoration, so remediation here is a genuine design problem, not a palette swap.

---

## Structural observations

**`#1603` should have `#1604`, `#1605`, `#1606` as children.** It does not, which is why the board's
`Epic` field shows nothing for them. This is the cheapest fix in this document.

**The three non-epic issues overlap heavily.** All three touch theming and layout; `#1604` and
`#1606` touch the same layout constants. Scheduling them independently means paying the
learning cost three times and risks three inconsistent partial solutions. **Recommend one
programme with staged delivery**, ordered:

1. **Audit** (`#1605`'s audit half, plus a survey of pinned layout constants for `#1606`) —
   produces scope, cheap, unblocks estimation.
2. **Theming/palette** (`#1605` remediation + `#1604`'s dark-mode work) — shared foundation.
3. **Scaling** (`#1606`) — the largest, and the one that most needs the audit first.

**The scoring model actively suppresses this group, and that is worth understanding before
reading the ranks.** Board `Priority` is `Importance + Simplicity` (see
[03-board-hygiene.md](03-board-hygiene.md)), so difficulty subtracts from rank. These three score:

| # | Importance | Simplicity | Priority |
|---|---:|---:|---:|
| `#1605` colorblindness | 1 | 2 | 3 |
| `#1606` font sizes | 3 | 1 | 4 |
| `#1604` cross-OS appearance | 4 | 1 | 5 |

All three land in the bottom half of a 1–12 scale, and they cannot climb: a `Byzantine (1)` item
caps at Priority 11 even at Importance 10. So the ranking is not a judgment that accessibility
does not matter — it is arithmetic, and the arithmetic is hostile to any large, hard programme.

That is fine for ordinary work and wrong for a compliance mandate, because a mandate is not
optional at any cost. **If the answer to Decision 4 is "yes, this is dated and we will be
audited," this group should be resourced as a project and taken out of the Priority queue
entirely** rather than competing on a value/cost score it is structurally guaranteed to lose.

Note also that `#1605`'s Importance is scored **1** — the lowest value on the board — while
carrying the `High Priority` label. Those two cannot both be right, and it is the clearest single
example of the two prioritization mechanisms contradicting each other.

---

## Recommendations

1. **Answer the mandate question first** (deadline? audit? measured against what?) — everything
   else follows from it.
2. **Link `#1604`/`#1605`/`#1606` as children of `#1603`.**
3. **Split `#1605` into audit and remediation**; do the audit early regardless of the rest.
4. **Scope `#1606` as a project**, not a ticket — pinned layout constants, not font literals.
5. **Merge `#1604`'s dark-mode work with `#1605`'s palette work** — one theming foundation.
6. **Cross-reference `#1785` and `#1886`** from [10-desktop-ui.md](10-desktop-ui.md); they will be
   touched by the same effort.

---

## All 4

| # | Title | Opened | Board status | Pri/Imp | Simplicity | Flags |
|---|---|---|---|---|---|---|
| [#1605](https://github.com/virtualcell/vcell/issues/1605) | VCell GUI needs to be evaluated for Colorblindness | 2025-12 | Queued | 3/1 | Intricate&nbsp;(2) | HP |
| [#1606](https://github.com/virtualcell/vcell/issues/1606) | VCell needs ability to change font sizes | 2025-12 | Queued | 4/3 | Byzantine&nbsp;(1) | HP |
| [#1604](https://github.com/virtualcell/vcell/issues/1604) | Need to Update and Unify Appearance of GUI across operating systems (Windows, Mac, Lin… | 2025-12 | Queued | 5/4 | Byzantine&nbsp;(1) | HP |
| [#1603](https://github.com/virtualcell/vcell/issues/1603) | [EPIC] VCell GUI must adhere to new UConn Health Accessibility Guidelines | 2025-12 | Queued | — | — | HP EPIC |

**Flags:** `HP` = High Priority label · `BLK` = Blocked · `EPIC` = Type: Epic · `REF` = To Refine · `thin` = body under 80 chars
