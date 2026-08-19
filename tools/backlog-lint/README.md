# backlog-lint

Mechanical consistency checks over the open issue backlog and
[project board #1](https://github.com/orgs/virtualcell/projects/1).

**It checks facts, never judgment.** Every rule is decidable from metadata alone: an issue on
no board, a `Priority` that does not equal the sum it is defined to be, a card marked `Done`
whose issue is still open. Nothing here decides whether an issue *matters* — that stays with
people, and the reasoning behind the current backlog lives in [`docs/backlog/`](../../docs/backlog/).

## Why it gates instead of reporting

A scheduled job that posts a report gets ignored. This repo already learned that with the BMDB
nightly, which was changed to **fail** on changed results rather than post an unread summary.

So findings are compared against a committed baseline of accepted violations
(`baseline.json`), and the run fails only on findings **not** in it. Accepting a new violation
is therefore a deliberate, reviewable act: regenerate the baseline, look at the diff, commit it.

The baseline shrinking is the grooming getting done. The baseline growing is a decision someone
made on purpose.

## The checks

| id | Fails when | Why it matters |
|---|---|---|
| `not-on-board` | An open issue is on no project board | Board-invisible work does not get planned. 55 issues were in this state, skewed toward the *newest and best-described* ones. |
| `priority-formula` | `Priority ≠ Importance + Simplicity` | `Priority` is a derived field. A hand-entered value that disagrees with its inputs is stale arithmetic. |
| `scored-not-ranked` | `Importance` set, `Priority` never computed | The issue was judged, then never entered the ranked queue. Two such issues tied the highest score on the board. |
| `half-scored` | `Simplicity` set, `Importance` not | No `Priority` is possible until someone rates the value. |
| `done-but-open` | Board says `Done`, issue still open | The issue is the source of truth; the card follows it. |
| `stale-active` | `Active` and untouched 30+ days | `Active` is the one status a planner must be able to trust. |
| `shipped-release-label` | Carries a label naming a shipped release | `Next Release` has meant "next release" across dozens of actual releases. |
| `many-assignees` | 3+ assignees | In GitHub this means "interested", not "owns". Nobody is accountable. |
| `queued-thin-body` | `Queued` with a body under 200 chars | Nothing should be *ready* that an outside engineer cannot start from. |

Thresholds and the shipped-release label set are constants at the top of `backlog_lint.py`.

## The scoring model

Board `Priority` is **derived**, not an independent judgment:

```
Priority = Importance + Simplicity
```

- `Importance` — 1–10, higher = more valuable
- `Simplicity` — `Simple (5)` … `Byzantine (1)`, higher = **easier** (parsed from the trailing
  number in the option label, so renaming options does not break the check)
- `Priority` — 1–12, **higher = do sooner**

This is a value/cost model: important scores high, easy scores high, important *and* easy scores
highest. One consequence worth knowing — because ease is *added* rather than multiplied, a
`Byzantine (1)` item caps at `Priority` 11 however important it is, so the model is structurally
hostile to large, hard programmes. Work that must happen regardless of cost (a compliance
mandate, say) should be resourced outside the queue rather than ranked inside it.

The formula was reconstructed from the board data (exact on 56 of 57 ranked issues) and is
documented here because it existed nowhere else. **Consider making `Priority` a computed column** —
hand-entry is what let one row drift out of sync in the first place.

## Running it

Needs a PAT in `GH_PROJECT_TOKEN` with **`read:project`** scope. `GITHUB_TOKEN` cannot read
organization ProjectV2 boards, which is why this is not wired to the default token.

```bash
export GH_PROJECT_TOKEN=$(gh auth token)      # if your gh token has read:project

python3 tools/backlog-lint/backlog_lint.py                    # lint; exit 1 on new findings
python3 tools/backlog-lint/backlog_lint.py --strict           # fail on ANY finding
python3 tools/backlog-lint/backlog_lint.py --format markdown  # markdown report
python3 tools/backlog-lint/backlog_lint.py --update-baseline  # accept current state
```

If `gh` reports a missing scope: `gh auth refresh -s read:project`.

### Accepting new findings

```bash
python3 tools/backlog-lint/backlog_lint.py --update-baseline
git diff tools/backlog-lint/baseline.json      # read this before committing
```

### Adding unboarded issues to the board

The one write the tool can perform, and it is **opt-in**:

```bash
gh auth refresh -s project                     # write scope, not just read
python3 tools/backlog-lint/backlog_lint.py --fix-board
```

Issues land in the board's default column and still need triaging into `Pool` and scoring.
Everything else the lint finds is left for a human, on purpose.

## In CI

`.github/workflows/backlog-lint.yml` runs it Mondays at 08:00 UTC, on `workflow_dispatch`
(with `strict` and `fix_board` toggles), and on pull requests that touch this directory so the
tool is exercised by its own changes.

The workflow needs the repository secret **`GH_PROJECT_TOKEN`**; it fails with an explicit
message if that secret is absent rather than reporting a false pass.

## What this deliberately does not do

- **Detect duplicates.** Tested and rejected: TF-IDF similarity over title+body found only 5 of
  12 hand-identified duplicate pairs, missed the two most important (`#1008`/`#1199` ranked 579th,
  `#1604`/`#1606` ranked 9377th), and its top hit was a *deliberate* split. Lexical similarity
  finds sibling issues in a subsystem, which is not the same thing.
- **Close anything, or edit issue content.** Bulk-closing by rule and auto-writing bodies for
  under-described issues both produce confident-looking output that is really invention. A blank
  body is honestly blank; a generated one is not.
- **Assign `Importance`.** That is a judgment about users and belongs to people.
