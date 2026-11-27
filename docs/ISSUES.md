# Filing GitHub issues

Issues in `virtualcell/vcell` are prioritized on the org project **"vcell development"** (project #1). The
project's stakeholders, the professors and scientists who steer VCell, review and reprioritize it in meetings. An
issue that has only a title and body never enters that process, so **every new issue must be added to the project
with the fields below set.**

## Fields

| Field | Where | Values |
|---|---|---|
| **Issue type** | the issue | Task (a specific piece of work), Bug, Feature, Epic |
| **Status** | project | Shelved, Pool, Queued, Next, Active, Blocked, Done, Released (see below) |
| **Simplicity** | project | Simple (5), Moderate (4), Complex (3), Intricate (2), Byzantine (1), Unknown (0) |
| **Importance** | project, number | roughly 1–15; see below |
| **Priority** | project, number | **Importance + the Simplicity number** |

Assign an issue only when someone is working on it. Labels, Classification, Epic, Iteration and Milestone are
generally left empty.

### Importance

Importance is a judgement of how desirable the work is to some set of end users, combined with how useful it is in
practice. Estimate it from three questions:

1. Does the work enable future work or streamline development? Would leaving it undone get in the way?
2. Are there related areas of biology that have their own importance, past or present?
3. How much would users benefit, especially given what other tools already offer?

State your reasoning when you file an issue, so the people reviewing it can correct the number.

### Status

- **Pool** is the default.
- **Queued** only when the Priority is at or near the highest Priority currently in Queued. Check the project
  before deciding.
- **Active** when someone is already working on it, and the issue records something nobody had noticed before.

## With the `gh` CLI

Setting project fields needs the `read:project` and `project` scopes:

```bash
gh auth refresh -h github.com -s read:project,project
```

Then create the issue, add it to the project and set each field. `gh project field-list 1 --owner virtualcell`
lists the field and option IDs that `gh project item-edit` needs.
