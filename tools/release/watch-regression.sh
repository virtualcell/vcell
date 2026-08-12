#!/bin/bash
#
# Wait for regression.yml to pass on a specific commit.
#
#   tools/release/watch-regression.sh <sha> [--dispatch]
#
# --dispatch  kick regression on master first. Needed after an admin merge, which bypasses
#             the merge queue and so does not run regression on its own.
#
# Exits 0 only if a regression run whose head is exactly <sha> completed successfully.
# Anything else -- no run found, failure, cancellation, or the wait timing out -- exits
# non-zero. Callers gate tagging and deploying on that, so "could not verify" must never
# look like "verified".
#
# Two behaviours worth knowing:
#
#   * regression.yml sets `concurrency: cancel-in-progress`, so a later merge cancels an
#     earlier run. A `cancelled` conclusion therefore means superseded, not broken -- this
#     script pins to a SHA rather than "the latest run" so it never follows someone else's.
#   * A run is matched by headSha, not by position in the list. Matching by position races
#     with any other merge landing at the same time.
#
set -u

REPO="${REPO:-virtualcell/vcell}"
SHA="${1:-}"
DISPATCH="${2:-}"

if [ -z "$SHA" ]; then
	echo "usage: $0 <sha> [--dispatch]" >&2
	exit 2
fi

if [ "$DISPATCH" = "--dispatch" ]; then
	gh workflow run regression.yml --repo "$REPO" --ref master \
		|| { echo "could not dispatch regression.yml" >&2; exit 1; }
	sleep 25
fi

RUN=""
for _ in $(seq 1 20); do
	RUN=$(gh run list --repo "$REPO" --workflow=regression.yml --limit 10 \
		--json databaseId,headSha \
		-q "[.[] | select(.headSha==\"$SHA\")][0].databaseId")
	[ -n "$RUN" ] && break
	sleep 15
done

if [ -z "$RUN" ]; then
	echo "no regression run found for $SHA" >&2
	exit 1
fi
echo "regression run=$RUN head=${SHA:0:10}"

STATUS=""
for _ in $(seq 1 200); do
	STATUS=$(gh run view "$RUN" --repo "$REPO" --json status,conclusion \
		-q '"\(.status)/\(.conclusion // "-")"')
	case "$STATUS" in completed/*) break ;; esac
	sleep 45
done

case "$STATUS" in
	completed/success)
		echo "regression: success"
		exit 0
		;;
	completed/*)
		echo "regression: ${STATUS#completed/}"
		gh run view "$RUN" --repo "$REPO" --json jobs \
			-q '.jobs[] | select(.conclusion!="success" and .conclusion!=null) | "  FAILED: \(.name)"'
		exit 1
		;;
	*)
		echo "regression did not finish in time (last status: $STATUS)"
		exit 1
		;;
esac
