# The Triage Plan

How to get from 272 undifferentiated open issues to a backlog the team can actually work from,
without spending a week on it.

---

## The core problem

It is not that there are 272 issues. It is that **the backlog does not distinguish between four
very different kinds of thing**, and they are all filed the same way:

1. **Work items** — a defect or a feature, described well enough to schedule. Maybe 90 of the 272.
2. **Reminders** — a title someone wrote so an idea wouldn't be lost. No body, no acceptance
   criteria, often 4 years old. About 107 (see [02](02-needs-refinement.md)).
3. **Containers** — 23 epics, several of which overlap or have been overtaken (see [04](04-epic-map.md)).
4. **Not-backlog** — support requests, bot notifications, done-but-open, questions from
   contributors. About 14 (see [01](01-close-and-verify.md)).

Grooming means sorting these four apart *first*. Prioritizing before that sort is how a backlog
ends up with a ranked list of things nobody can estimate — which is roughly the current state.

---

## Dispositions

Every issue gets exactly one. These are deliberately few; a taxonomy with ten buckets does not get used.

| Disposition | Meaning | What happens |
|---|---|---|
| **CLOSE** | Done, obsolete, duplicate, or not a backlog item | Close with a one-line reason. Reversible. |
| **REFINE** | Real, but unactionable as written | Assign a *refiner* (not an implementer) to add repro steps / acceptance criteria, or close it |
| **GROUP** | Real and clear, belongs to a theme | File under its group doc; rank within the group |
| **DECIDE** | Blocked on a product/architecture call, not on work | Escalate to the named decider; do not rank until answered |
| **SHELVE** | Real, understood, deliberately not now | Board status `Shelved`, with a note saying *why* and what would revive it |

The distinction that matters most is **REFINE vs SHELVE**. A four-year-old one-line issue is not
automatically shelved — it may be a real bug nobody can reproduce because nobody wrote down how.
Shelving it hides the ambiguity; refining it or closing it resolves the ambiguity. Prefer one of
those two.

---

## Decision rules

Apply in order. The first rule that fires wins.

**1. Is it a backlog item at all?**
Support requests (`#1609`), contributor questions (`#1598`), and bot-generated status issues
(`#1777`) are not backlog. → CLOSE, redirect to the right channel.

**2. Does the board say `Done`?**
Four issues are open with board status `Done` (`#166`, `#748`, `#1646`, `#1647`). Either the work
landed and nobody closed the issue, or the status is wrong. → verify, then CLOSE or correct the status.

**3. Has the code moved past it?**
Check the working tree before assuming. `#1647` (inverse cotangent) has a commit that says
`Fixed incorrect acot math!!!`; `#1686` has a merged PR. Both are close candidates on evidence,
not on vibes. → CLOSE with the commit/PR cited.

**4. Is it a release-planning artifact from a shipped version?**
`Next Release` (19 issues) and `VCell-7.5.0`/`7.5.1`/`7.6.0` (33 issues) date from three majors
back; we ship 8.0.27.01. `#1543` and `#1599` ask for release notes for 7.7. → CLOSE the notes
issues, strip the stale labels wholesale.

**5. Can a competent engineer who did not write it start work from the body?**
If no → REFINE. This is the 107-issue question and the reason [02](02-needs-refinement.md) is
structured by *what is missing*, not by topic.

**6. Does progress depend on a decision nobody has made?**
→ DECIDE. Five of these gate large chunks of the backlog; they are named in [04](04-epic-map.md).
Ranking work behind an unmade decision is wasted ranking.

**7. Otherwise** → GROUP, and rank inside the group.

---

## Who decides what

Grooming stalls when every question routes to one person. These route differently:

| Question type | Decider | Examples |
|---|---|---|
| Is this still a real user-facing problem? | Domain/user-facing owners (Ann, Michael, Les) | most of the 2022 UI and RBM cohort |
| Is this already fixed / obsoleted by a rewrite? | The engineer who owns that subsystem | `#877`, `#1048`, `#1152` |
| Do we still want to go this direction? | Jim (architecture) | the 5 DECIDE items in [04](04-epic-map.md) |
| What ships next? | Whoever owns release planning | the `Queued` slate, the stale release labels |

**Explicit recommendation:** the 2022 cohort (73 issues, mostly one bulk import, mostly bodyless)
should be reviewed by its *authors* in one sitting, not by engineers one at a time. Most of those
titles mean something specific to the person who wrote them and nothing to anyone else. A single
90-minute pass by Ann + Michael would likely resolve 50 of them to CLOSE or a one-sentence refinement.

---

## Suggested sequencing

Each step is independently useful; stopping after any of them leaves the backlog better than it
was. Estimates are for the grooming, not the engineering work.

**Step 1 — Reconcile the prioritization mechanisms.** *(~30 min, one person)*
The board's scoring model is sound — `Priority = Importance + Simplicity`, a real value/cost
tradeoff ([03](03-board-hygiene.md)). The five rows whose arithmetic had drifted or was never
computed **have been fixed** and the board is now formula-consistent; what remains is to write the
formula down where it will survive, then pick *one*
mechanism: recommend keeping the numeric fields, deleting the `High Priority` label, and stripping
the four stale release labels. Right now three mechanisms disagree and the disagreement is silent.

**Step 2 — Close the close-list.** *(~30 min)*
14 issues in [01](01-close-and-verify.md), each with its evidence. Six are already verified against
the repo; eight need a yes/no from a named person.

**Step 3 — Put the 55 off-board issues on the board.** *(~30 min)*
[03](03-board-hygiene.md). These are the best-described issues in the repo and they are invisible
to anyone planning from the board. This is the cheapest high-value step in the list.

**Step 4 — Take the 5 strategic decisions.** *(one meeting)*
[04](04-epic-map.md). Postgres, the two competing UI epics, the desktop-vs-web viewer direction,
the SpringSaLaD epic structure, the export-refactor overlap. Each one determines whether a
double-digit number of issues is worth ranking at all. **Do this before Step 5**, not after.

**Step 5 — Run the refinement pass.** *(the 90-minute 2022-cohort session, plus follow-ups)*
[02](02-needs-refinement.md). Outcome per issue is a one-sentence acceptance criterion or a close.

**Step 6 — Rank within groups.** *(per-group, by group owner)*
Only now is ranking meaningful. Rank inside each group doc (10–19); do not maintain a single
global 272-item order — nobody reads past item 20 of one.

---

## What "groomed" should mean here

A proposed definition of done for this exercise, so it is possible to tell when it is finished:

- [ ] Every open issue has a board status, and `Active` means someone is actually working on it this month
- [ ] No open issue carries a label naming a shipped release
- [ ] One prioritization mechanism, with its scoring formula documented on the board
- [ ] Every `Queued` issue has a body an outside engineer could start from
- [ ] Every epic either has an owner and a live child list, or is closed
- [ ] Issues with 3+ assignees are reduced to one owner (33 issues today)

---

## A caution on the group boundaries

The ten thematic groups in this pass are a *reading aid*, not a proposed team structure or label
scheme. Several issues genuinely belong to two groups — `#167` (field-data API for local runs) is
equally an API item and a visualization item; `#1564` (automate the SpringSaLaD build) is equally
SpringSaLaD and CI. Each was assigned to one group so the counts sum cleanly and nothing gets read
twice or missed. Where the second home is material, the group doc says so.

Do not convert these groups into GitHub labels without a separate conversation — the repo already
has 32 labels and part of the current problem is that several of them overlap.
