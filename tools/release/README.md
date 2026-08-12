# Release tooling

Scripts the release/deploy procedure depends on. The procedure itself is
`.claude/skills/deploy/SKILL.md`; conventions are `docs/RELEASING.md`.

These live in the repo rather than in a scratch directory for a specific reason: during the
8.0.12.01 cut, the equivalent scripts were sitting in `/private/tmp` and one of them was
removed by the OS between two steps of the same release. The chain stopped at its regression
gate and refused to tag — the right outcome, but the tooling a release depends on should not
be able to disappear underneath it.

| script | does |
|---|---|
| `merge-when-green.sh <branch>` | merge a PR once every required check is present and green, then verify regression on the merge commit |
| `watch-regression.sh <sha> [--dispatch]` | wait for `regression.yml` to pass on exactly that commit |
| `release-and-deploy.sh <version> <site> <notes>` | regression → tag → CI-full (`tag-and-push` gate) → `site_deploy` |
| `verify-deploy.sh <version> <site>` | fluxcd commit, image tags, pod readiness, node placement, site HTTP |

All take `REPO` from the environment (default `virtualcell/vcell`), and
`verify-deploy.sh` takes `LOKI_KUBECONFIG` (default `~/.kube/kubeconfig_vxrails.yaml`).

## Typical cut

```bash
# 1. land the release-cut docs first, so the tag points at a self-describing tree
tools/release/merge-when-green.sh release/8.0.12.01

# 2. regression -> tag -> images -> deploy
tools/release/release-and-deploy.sh 8.0.12.01 alpha /tmp/changelog-section.md

# 3. a green workflow is not a deployed site
tools/release/verify-deploy.sh 8.0.12.01 alpha
```

## Why the gates are strict

Every rule below is a real failure, not a hypothetical:

- **A missing check is not a passing check.** A rerunning check disappears from
  `statusCheckRollup` rather than reporting "pending", so "nothing failing and nothing
  pending" is vacuously true for an empty rollup. PR #1845 was merged that way with
  `CI-Test-group-Quarkus` never having run. Each expected check must be *present* and
  `SUCCESS`.
- **A timeout is not a pass.** Every wait loop exits non-zero if it runs out.
- **`tag-and-push` is the deploy's real precondition.** It needs all docker builds and
  creates the friendly `version.build` image tags; without it the deploy fails "manifest
  unknown". Never cancel CI-full before it finishes.
- **Runs are matched by head SHA, never by position.** `regression.yml` uses
  `cancel-in-progress`, so a later merge cancels an earlier run and a `cancelled` conclusion
  means superseded rather than broken. Matching "the latest run" follows someone else's merge.
- **An admin merge bypasses the queue**, so regression does not run on its own — it has to be
  dispatched.
- **A green deploy workflow only means Flux was told.** Verify the end state.

## Not covered

Smoke-testing whatever the release actually changed. That needs a client and a human;
`verify-deploy.sh` only establishes that the right images are running and the site answers.
