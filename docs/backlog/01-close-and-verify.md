# Close / Verify-Then-Close — 14 issues

Issues that look done, obsolete, or not a backlog item. **Nothing here has been closed** — each
entry states the evidence and who should confirm.

Six are verified against the repo and can be closed on the evidence shown. Eight need a yes/no
from a named person first.

---

## A. Verified against the repo — close on this evidence

### `#1647` — VCell has been doing inverse co-tangent wrong
Board status is already `Done`. `git log` carries **`e79926b50a Fixed incorrect acot math!!!`**.
The issue body notes "a fix is being pushed" — it was.

> **One caveat before closing:** the body says this "has ramifications for results in the past."
> If nothing was ever done about previously-computed results, that follow-up deserves its own
> issue rather than dying with this one. Ask @CodeByDrescher whether that tail exists.

### `#1686` — Dan ss structural sites
Board status `Active`. Body is a pointer to **PR #1685, which is merged.** The issue is a PR
tracker with nothing left in it. Close; the remaining structural-site work already lives in
`#1688` (graphical interface) and `#1689` (3D visualization).

### `#1646` — VCell does not properly handle -0.0 in IEEE Expressions
Board status `Done`. Substantial 5.5k-char writeup, marked done by whoever moved the card.
**Not independently confirmed in the tree** — I could not isolate a commit, and the change is
diffuse by nature. → confirm with @CodeByDrescher, then close. Listed here rather than in section B
because the board already asserts it.

### `#1718` — 🗂 VCell — Zenodo release archiving
Master tracking issue. `.github/workflows/` now contains **`zenodo-archive.yml` and
`zenodo-drift.yml`** — the pipeline it asked for exists and runs. Close, or reduce to whatever
single item is genuinely outstanding.

### `#1777` — Zenodo release archiving has drifted
Opened by `app/github-actions` — this is the **drift bot's own alert**, reporting
`Latest GitHub release 8.0.2.01` against `Latest Zenodo version ?`. We are on 8.0.27.01; the alert
is nine months stale. Close it. If drift monitoring is still firing, that is a live signal to act
on, not a backlog item to carry.

### `#1320` — Help needs changes (for Importing Images, after change)
Entire body is **"See #1172"**, and **`#1172` is closed**. Either the help change was made with
the fix, or this needs re-stating as its own request with actual content. Currently it is a
dangling pointer that has been `Queued` at Priority 6 since 2024. → @ACowan0105.

---

## B. Not a backlog item — close and redirect

### `#1609` — Issue With VCELL Server.
A **user support request** ("I am trying to sign in to VCell today, but keep having this
Message…"), filed by an external user in Dec 2025. Off-board, no assignee, no triage.

If the underlying RPC failure was real it should be a described defect; if it was environmental it
should be an email reply. Either way it should not sit open in the backlog for eight months —
that is a bad experience for the reporter more than it is a problem for us. → answer the user, close.

### `#1598` — Looking to re-engage and contribute, any beginner friendly issues available?
A **contributor asking a question** (`@gmarupilla`, a former VCell contributor of two years,
offering to come back). Open and unanswered since Nov 2025.

This is worth noticing for its own sake: someone with two years of VCell experience volunteered
and got no reply for nine months. Answer it, point them at a real starter issue, close. This pass
produces good candidates — see the `Simple (5)` rows in [10-desktop-ui.md](10-desktop-ui.md).

### `#1552` — [RECURRING] Credentials for Docker Singularity Token need renewing
Body states **"Current Expiration: Sept 2025"** — eleven months past. Board status `Blocked`.

A recurring operational chore is not backlog; it is either automated or it is a calendar entry.
Close it and do one of those two. (If the token has in fact been renewed since, that also confirms
the issue is not tracking anything.)

---

## C. Release-planning residue — close

### `#1543` — Create Release Notes for Next Production Release
### `#1599` — Make new release notes for vcell 7.7

`#1599` asks for notes for **7.7.0.47**. The current line is **8.0.27.01**, and
`release-notes/major/` already contains `7.7.md`, `7.7-initial-release.md`,
`7.7-feature-update.md`, `7.7-stability-update.md`, and `8.0.md`. The 7.7 notes exist.

`#1543` ("next production release") is unfalsifiable by construction — it can never be closed,
because there is always a next release. Release notes are part of the release procedure
(`docs/RELEASING.md`), not a backlog item. Close both.

Both are currently marked `Active`, which is a good illustration of why the `Active` column
cannot be trusted for planning — see [03-board-hygiene.md](03-board-hygiene.md).

---

## D. Needs a decision before closing

### `#166` — Relax restrictions on reaction kinetics for stochastic applications
Board status `Done`, but the issue is also a **live child of epic `#1035`** (general reactions
with stochastic simulations), which is itself `Active` and has three unchecked sibling tasks
(Gibson / NFSim / Smoldyn).

So either `#166` shipped and `#1035` should be updated to reflect it, or the `Done` status is
wrong. These cannot both be true. → @jcschaff, as `#1035`'s owner.

### `#1655` — finish publication/curation PR
Body is its own title, verbatim: *"finish publication/curation PR"*. `Active` since April 2026,
assigned to @jcschaff. No PR linked.

Either the PR is identifiable — in which case link it and let the PR carry the work — or this is a
four-month-old note-to-self. It cannot be handed to anyone else in its current form. → close, or
add the PR link and a scope sentence.

---

## Summary table

| # | Title | Opened | Board status | Pri/Imp | Simplicity | Flags |
|---|---|---|---|---|---|---|
| [#1320](https://github.com/virtualcell/vcell/issues/1320) | Help needs changes (for Importing Images, after change) | 2024-07 | Queued | 6/6 | Unknown&nbsp;(0) | BLK thin |
| [#166](https://github.com/virtualcell/vcell/issues/166) | Relax restrictions on reaction kinetics for stochastic applications | 2022-07 | Done | — | — | — |
| [#748](https://github.com/virtualcell/vcell/issues/748) | need script to configure vcell ssl keystore from LetsEncrypt | 2023-01 | Done | — | — | thin |
| [#1543](https://github.com/virtualcell/vcell/issues/1543) | Create Release Notes for Next Production Release | 2025-06 | Active | — | — | — |
| [#1552](https://github.com/virtualcell/vcell/issues/1552) | [RECURRING] Credentails for Docker Singularty Token need renewing when they expire | 2025-06 | Blocked | — | Moderate&nbsp;(4) | — |
| [#1598](https://github.com/virtualcell/vcell/issues/1598) | Looking to re-engage and contribute, any beginner friendly issues available? | 2025-11 | **off-board** | — | — | — |
| [#1599](https://github.com/virtualcell/vcell/issues/1599) | Make new release notes for vcell 7.7 | 2025-12 | Active | — | Complex&nbsp;(3) | — |
| [#1609](https://github.com/virtualcell/vcell/issues/1609) | Issue With VCELL Server. | 2025-12 | **off-board** | — | — | — |
| [#1646](https://github.com/virtualcell/vcell/issues/1646) | VCell does not properly handle -0.0 in the IEEE standard within Expressions, creating … | 2026-02 | Done | — | — | — |
| [#1647](https://github.com/virtualcell/vcell/issues/1647) | VCell has been doing inverse co-tangent wrong. | 2026-02 | Done | — | — | — |
| [#1655](https://github.com/virtualcell/vcell/issues/1655) | finish publication/curation PR | 2026-04 | Active | — | — | thin |
| [#1686](https://github.com/virtualcell/vcell/issues/1686) | Dan ss structural sites | 2026-05 | Active | — | Complex&nbsp;(3) | HP thin |
| [#1718](https://github.com/virtualcell/vcell/issues/1718) | 🗂 VCell — Zenodo release archiving | 2026-07 | **off-board** | — | — | — |
| [#1777](https://github.com/virtualcell/vcell/issues/1777) | Zenodo release archiving has drifted | 2026-07 | **off-board** | — | — | — |

**Flags:** `HP` = High Priority label · `BLK` = Blocked · `EPIC` = Type: Epic · `REF` = To Refine · `thin` = body under 80 chars

---

## `#748` — the one I could not resolve

**need script to configure vcell ssl keystore from LetsEncrypt.** Board status `Done`. The body is
the title repeated. I searched for a LetsEncrypt or keystore-provisioning script and found only
`vcell-api/keystore_macbook.jks` and `docker/build/installers/NO-WIN-KEYSTORE` — neither is the
script described.

It is plausible this was overtaken by the Kubernetes migration (cert management would now be
cluster-side, in `vcell-fluxcd`, not in a VCell script), in which case it is obsolete rather than
done. **I did not verify that.** → @jcschaff: obsolete, or done elsewhere?
