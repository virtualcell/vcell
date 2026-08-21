#!/bin/bash
#
# Tag a release, wait for its images, and deploy a site.
#
#   tools/release/release-and-deploy.sh <version> <site> <notes-file> [--skip-regression]
#
#   version      four-part, e.g. 8.0.12.01
#   site         alpha | test | rel   (-> dev | stage | prod; see the deploy skill)
#   notes-file   markdown for the release body, normally the CHANGELOG section for <version>
#
# Assumes the release-cut docs are already on master (docs/RELEASING.md step 1), so the tag
# points at a tree that describes itself.
#
# Gates, in order. Each one exits non-zero rather than continuing on an unverified state:
#
#   1. regression on the exact commit being tagged. After an admin merge nothing runs it for
#      us, so it is dispatched and pinned by SHA.
#   2. CI-full's `tag-and-push`. That job needs ALL docker builds and is what creates the
#      friendly `version.build` image tags the deploy pulls. Deploying without it fails with
#      "manifest unknown", so a CI-full that is merely "completed" is not enough -- the job
#      itself must be success. NEVER cancel CI-full before it.
#   3. site_deploy itself.
#
# Publishing a release also triggers cd.yml ("CD"), which builds only the biosimulators
# image. Its failure does NOT block the site deploy, and it builds --no-cache, so an apt 404
# there is transient mirror lag: rerun it, do not "fix" the Dockerfile.
#
# Deploy-workflow success is not the same as deployed. site_deploy POSTs to vcell-fluxcd,
# which commits "Changed image tag of <overlay> to <version>"; Flux then reconciles. Verify
# with tools/release/verify-deploy.sh.
#
set -u

REPO="${REPO:-virtualcell/vcell}"
VERSION="${1:-}"
SITE="${2:-}"
NOTES="${3:-}"
SKIP_REGRESSION="${4:-}"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ -z "$VERSION" ] || [ -z "$SITE" ] || [ -z "$NOTES" ]; then
	echo "usage: $0 <version> <site> <notes-file> [--skip-regression]" >&2
	exit 2
fi
[ -f "$NOTES" ] || { echo "notes file not found: $NOTES" >&2; exit 2; }

case "$VERSION" in
	*.*.*.*) : ;;
	*) echo "version must be four parts (MAJOR.MINOR.PATCH.BUILD), got: $VERSION" >&2; exit 2 ;;
esac
case "$SITE" in
	alpha|test|rel) : ;;
	*) echo "site must be alpha, test or rel; got: $SITE" >&2; exit 2 ;;
esac

# site_deploy takes the version split in two
VC_VERSION="${VERSION%.*}"
VC_BUILD="${VERSION##*.}"

MASTER_SHA=$(gh api "repos/$REPO/commits/master" -q .sha)
echo "releasing $VERSION from master ${MASTER_SHA:0:10} to site=$SITE"

# ---- gate 1: regression on the exact commit being tagged
if [ "$SKIP_REGRESSION" = "--skip-regression" ]; then
	echo "WARNING: skipping regression at caller's request"
else
	REPO="$REPO" "$HERE/watch-regression.sh" "$MASTER_SHA" --dispatch \
		|| { echo "regression not green -- NOT tagging" >&2; exit 1; }
fi

# ---- tag + release
if gh release view "$VERSION" --repo "$REPO" >/dev/null 2>&1; then
	echo "release $VERSION already exists -- not recreating it"
else
	# Tag the commit regression actually passed on, not whatever master is now. A merge
	# landing between gate 1 and here would otherwise be released untested.
	gh release create "$VERSION" --repo "$REPO" --target "$MASTER_SHA" \
		--title "$VERSION" --notes-file "$NOTES" \
		|| { echo "release create failed" >&2; exit 1; }
	echo "created release $VERSION"
fi

# ---- gate 2: CI-full, and specifically tag-and-push
sleep 30
RUN=""
for _ in $(seq 1 20); do
	RUN=$(gh run list --repo "$REPO" --workflow="Build and Publish VCell Docker Images" --limit 10 \
		--json databaseId,headBranch -q "[.[] | select(.headBranch==\"$VERSION\")][0].databaseId")
	[ -n "$RUN" ] && break
	sleep 15
done
[ -n "$RUN" ] || { echo "could not find the CI-full run for $VERSION" >&2; exit 1; }
echo "CI-full run=$RUN"

STATUS=""
for _ in $(seq 1 200); do
	STATUS=$(gh run view "$RUN" --repo "$REPO" --json status,conclusion \
		-q '"\(.status)/\(.conclusion // "-")"')
	case "$STATUS" in completed/*) break ;; esac
	sleep 45
done
echo "CI-full: $STATUS"

TAG_AND_PUSH=$(gh run view "$RUN" --repo "$REPO" --json jobs \
	-q '.jobs[] | select(.name|test("tag-and-push";"i")) | .conclusion')
echo "tag-and-push=${TAG_AND_PUSH:-ABSENT}"
if [ "$TAG_AND_PUSH" != "success" ]; then
	gh run view "$RUN" --repo "$REPO" --json jobs \
		-q '.jobs[] | select(.conclusion!="success" and .conclusion!="skipped" and .conclusion!=null) | "  FAILED: \(.name)"'
	echo "NOT deploying: without tag-and-push the $VERSION image tags may not exist." >&2
	echo "Recover with: gh run rerun $RUN --failed" >&2
	exit 1
fi

# ---- gate 3: the deploy
gh workflow run site_deploy.yml --repo "$REPO" --ref master \
	-f vcell_version="$VC_VERSION" -f vcell_build="$VC_BUILD" \
	-f vcell_site="$SITE" -f server_only=false \
	|| { echo "could not dispatch site_deploy" >&2; exit 1; }
sleep 20
DEPLOY=$(gh run list --repo "$REPO" --workflow=site_deploy.yml --limit 1 --json databaseId -q '.[0].databaseId')
echo "site_deploy run=$DEPLOY"

STATUS=""
for _ in $(seq 1 200); do
	STATUS=$(gh run view "$DEPLOY" --repo "$REPO" --json status,conclusion \
		-q '"\(.status)/\(.conclusion // "-")"')
	case "$STATUS" in completed/*) break ;; esac
	sleep 45
done
echo "site_deploy: $STATUS"
gh run view "$DEPLOY" --repo "$REPO" --json jobs \
	-q '.jobs[] | select(.conclusion!="success" and .conclusion!="skipped" and .conclusion!=null) | "  FAILED: \(.name)"'

case "$STATUS" in
	completed/success)
		echo "dispatched and built. Flux still has to reconcile -- verify with:"
		echo "  tools/release/verify-deploy.sh $VERSION $SITE"
		;;
	*) exit 1 ;;
esac
