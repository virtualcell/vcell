#!/bin/bash
#
# Merge a PR once every required check has passed, then verify regression on the result.
#
#   tools/release/merge-when-green.sh <branch> [--no-regression]
#
# Merges with a merge commit (never squash; see CLAUDE.md) using --admin, which bypasses the
# 1-review requirement and therefore also the merge queue -- so regression is dispatched
# afterwards rather than being run for us.
#
# The guard is deliberately strict, because each rule here is a bug that got through once:
#
#   * Every expected check must be PRESENT and SUCCESS. A rerunning or unschedulable check
#     vanishes from statusCheckRollup rather than reporting "pending", so "nothing failing
#     and nothing pending" is vacuously true for an empty rollup. That is how PR #1845 was
#     merged without CI-Test-group-Quarkus ever having run.
#   * The head SHA is pinned at the start and rechecked. If the branch is pushed to while we
#     wait, the checks we approved no longer describe what we would merge.
#   * Running out of wait iterations exits non-zero. A timeout is not a pass.
#
# The one exception is a fast lane that ci.yml deliberately skipped -- see lane_skipped below.
#
# CodeQL is intentionally not required: it is not a required check on this repo, and a red
# CodeQL on a vcell PR is usually a pre-existing alert re-attributed to whoever last moved
# the lines (check `created_at` and whether it is open on master before treating it as new).
#
set -u

REPO="${REPO:-virtualcell/vcell}"
BRANCH="${1:-}"
SKIP_REGRESSION="${2:-}"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

REQUIRED_CHECKS=(
	build
	CI-Test-group-Fast
	CI-Test-group-Fast-core
	CI-Test-group-Fast-other
	CI-Test-group-Quarkus
)

if [ -z "$BRANCH" ]; then
	echo "usage: $0 <branch> [--no-regression]" >&2
	exit 2
fi

PR=$(gh pr list --repo "$REPO" --head "$BRANCH" --state open --json number -q '.[0].number // empty')
if [ -z "$PR" ]; then
	echo "no open PR for branch $BRANCH" >&2
	exit 1
fi
HEAD_EXPECTED=$(gh pr view "$PR" --repo "$REPO" --json headRefOid -q .headRefOid)
echo "PR #$PR head=${HEAD_EXPECTED:0:10}"

# Is this change documentation-only? Evaluated here rather than taken on faith from the
# workflow, because it is half of the evidence for accepting a skipped lane (see lane_skipped).
# The pattern mirrors ci.yml's; if the two ever drift, this one is the conservative side.
CHANGED=$(gh pr view "$PR" --repo "$REPO" --json files -q '.files[].path')
DOCS_ONLY=false
if [ -n "$CHANGED" ] && ! printf '%s\n' "$CHANGED" | grep -qvE '(^docs/|^release-notes/|\.md$)'; then
	DOCS_ONLY=true
	echo "documentation-only change"
fi

rollup_value() { echo "$ROLL" | grep "^${1}=" | cut -d= -f2; }

# ci.yml gained a should-run gate (#1925) that skips the whole fast lane when a push cannot
# affect the build or the tests. A release-notes PR is the normal case, and it is why this
# script hung on the 8.0.23.01 cut: the lane's jobs report SKIPPED, and a skipped matrix leg
# appears under its UNEXPANDED name ("CI-Test-group-Fast-${{ matrix.name }}"), so
# CI-Test-group-Fast-core and -other are not present under those names at all.
#
# Neither absence nor SKIPPED means the same thing here as it does in the rule above: the lane
# ran and decided there was nothing to do. Accepted only when both signals agree, since
# "absent" is precisely what that rule exists to catch:
#
#   * the gate itself decided -- should-run succeeded, and the two fixed-name jobs conditioned
#     on its output are SKIPPED. If only some were skipped, a job was skipped because something
#     it needed failed, which must still block; and
#   * the change really is documentation-only.
lane_skipped() {
	[ "$DOCS_ONLY" = true ] || return 1
	[ "$(rollup_value should-run)" = "SUCCESS" ] || return 1
	[ "$(rollup_value build)" = "SKIPPED" ] || return 1
	[ "$(rollup_value CI-Test-group-Quarkus)" = "SKIPPED" ] || return 1
	return 0
}

LANE_SKIPPED=false
MISSING="not-yet-checked"
for _ in $(seq 1 640); do
	HEAD=$(gh pr view "$PR" --repo "$REPO" --json headRefOid -q .headRefOid)
	if [ "$HEAD" != "$HEAD_EXPECTED" ]; then
		echo "head moved to ${HEAD:0:10} -- aborting, the green checks no longer describe this branch" >&2
		exit 1
	fi

	ROLL=$(gh pr view "$PR" --repo "$REPO" --json statusCheckRollup \
		-q '.statusCheckRollup[] | "\(.name // .context)=\(.conclusion // .status)"')

	BAD=$(echo "$ROLL" | grep -E "=(FAILURE|CANCELLED|TIMED_OUT|ACTION_REQUIRED)$" | cut -d= -f1 | tr '\n' ' ')
	if [ -n "$BAD" ]; then
		echo "NOT MERGED -- failing checks: $BAD" >&2
		exit 1
	fi

	LANE_SKIPPED=false
	if lane_skipped; then
		LANE_SKIPPED=true
	fi

	MISSING=""
	for CHECK in "${REQUIRED_CHECKS[@]}"; do
		VALUE=$(rollup_value "$CHECK")
		[ "$VALUE" = "SUCCESS" ] && continue
		if [ "$LANE_SKIPPED" = true ] && { [ "$VALUE" = "SKIPPED" ] || [ -z "$VALUE" ]; }; then
			continue
		fi
		MISSING="$MISSING ${CHECK}=${VALUE:-absent}"
	done
	[ -z "$MISSING" ] && break
	sleep 45
done

if [ -n "$MISSING" ]; then
	echo "NOT MERGED -- timed out waiting for:$MISSING" >&2
	exit 1
fi

if [ "$LANE_SKIPPED" = true ]; then
	echo "fast lane skipped by ci.yml: documentation-only, nothing to build or test"
else
	echo "all required checks green"
fi
gh pr merge "$PR" --repo "$REPO" --merge --admin || { echo "merge command failed" >&2; exit 1; }
MERGE_SHA=$(gh pr view "$PR" --repo "$REPO" --json mergeCommit -q .mergeCommit.oid)
echo "#$PR MERGED merge=$MERGE_SHA"

if [ "$SKIP_REGRESSION" = "--no-regression" ]; then
	echo "skipping regression at caller's request"
	exit 0
fi

REPO="$REPO" "$HERE/watch-regression.sh" "$MERGE_SHA" --dispatch
