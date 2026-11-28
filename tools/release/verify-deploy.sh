#!/bin/bash
#
# Verify a deploy actually landed.
#
#   tools/release/verify-deploy.sh <version> <site>
#
# A green site_deploy run only means the workflow POSTed to vcell-fluxcd. Flux then has to
# reconcile before anything is really running, so this checks the end state instead:
#
#   1. vcell-fluxcd has a "Changed image tag of <overlay> to <version>" commit
#   2. every vcell deployment in the namespace is on <version>
#   3. pods are Running and Ready
#   4. NFS-mounting services are not on k8s-in-01 (that node is in the DMZ and NFS wedges
#      there -- rest, data, api, submit and sched must land elsewhere)
#   5. the site answers HTTP 200
#
# It fails closed: if the cluster cannot be read (no VPN, expired kubeconfig, API server down), it
# says so and reports NOT VERIFIED. An unreadable cluster used to look like an empty one, and an
# empty list passed checks 2-4 -- the 8.1.9.03 prod deploy was reported "all deployments on
# 8.1.9.03" from a laptop that could not reach the cluster at all.
#
set -u

VERSION="${1:-}"
SITE="${2:-}"
KUBECONFIG_PATH="${LOKI_KUBECONFIG:-$HOME/.kube/kubeconfig_vxrails.yaml}"

if [ -z "$VERSION" ] || [ -z "$SITE" ]; then
	echo "usage: $0 <version> <site>" >&2
	exit 2
fi

case "$SITE" in
	alpha) NS=dev;   URL=https://vcell-dev.cam.uchc.edu/ ;;
	test)  NS=stage; URL=https://vcell-stage.cam.uchc.edu/ ;;
	rel)   NS=prod;  URL=https://vcell.cam.uchc.edu/ ;;
	*) echo "site must be alpha, test or rel; got: $SITE" >&2; exit 2 ;;
esac

K="kubectl --kubeconfig $KUBECONFIG_PATH --request-timeout=30s -n $NS"
FAILED=0
DEPLOY_IMAGES='{range .items[*]}{.metadata.name}{"\t"}{.spec.template.spec.containers[0].image}{"\n"}{end}'

echo "== 0. cluster readable"
if ! LISTED=$($K get deploy -o jsonpath="$DEPLOY_IMAGES" 2>&1); then
	echo "   cannot read namespace $NS: $(printf '%s\n' "$LISTED" | tail -1)"
	echo
	echo "NOT VERIFIED -- the cluster could not be read (VPN down? kubeconfig expired?); nothing below would mean anything" >&2
	exit 1
fi
VCELL_DEPLOYS=$(printf '%s\n' "$LISTED" | grep -c 'ghcr.io/virtualcell/' || true)
if [ "$VCELL_DEPLOYS" -eq 0 ]; then
	echo
	echo "NOT VERIFIED -- no VCell deployments listed in namespace $NS" >&2
	exit 1
fi
echo "   $VCELL_DEPLOYS VCell deployments in $NS"

echo "== 1. vcell-fluxcd image-tag commit"
gh api repos/virtualcell/vcell-fluxcd/commits --jq '.[0:5][] | "   \(.commit.committer.date[0:16])  \(.commit.message | split("\n")[0])"' \
	| grep -F "to $VERSION" || { echo "   no commit found for $VERSION"; FAILED=1; }

echo "== 2. deployment image tags in $NS"
# Flux may still be applying the new tags, so give it a bounded chance to catch up.
OFF=""
for _ in $(seq 1 15); do
	if ! LISTED=$($K get deploy -o jsonpath="$DEPLOY_IMAGES" 2>&1); then
		OFF="(kubectl failed: $(printf '%s\n' "$LISTED" | tail -1))"
	elif ! printf '%s\n' "$LISTED" | grep -q 'ghcr.io/virtualcell/'; then
		OFF="(no VCell deployments listed)"
	else
		OFF=$(printf '%s\n' "$LISTED" | grep 'ghcr.io/virtualcell/' | grep -v ":$VERSION\$" || true)
	fi
	[ -z "$OFF" ] && break
	sleep 20
done
if [ -n "$OFF" ]; then
	echo "   NOT on $VERSION:"; echo "$OFF" | sed 's/^/     /'; FAILED=1
else
	echo "   all vcell deployments on $VERSION"
fi

echo "== 3. pod readiness"
# Wait rather than judge immediately: site_deploy finishing only means vcell-fluxcd was
# told, and Flux then rolls pods for a minute or two. Checking once reports whatever the
# cluster happened to look like at that instant -- the first run of this script called a
# deploy VERIFIED while one pod was still Pending and the previous one was still
# terminating, which is exactly the mistake it exists to prevent.
SETTLED=0
for _ in $(seq 1 30); do
	NOT_READY=$($K get pods --field-selector=status.phase=Running -o json 2>/dev/null | python3 -c "
import sys, json
try: d = json.load(sys.stdin)
except Exception: print('unreadable'); raise SystemExit
if not d.get('items'): print('no-running-pods'); raise SystemExit
print(','.join(p['metadata']['name'] for p in d.get('items', [])
      if not all(c.get('ready') for c in p['status'].get('containerStatuses', []))))")
	PENDING=$($K get pods --field-selector=status.phase=Pending -o name 2>/dev/null | tr '\n' ' ')
	if [ -z "$NOT_READY" ] && [ -z "$PENDING" ]; then SETTLED=1; break; fi
	echo "   waiting for rollout: notReady=[$NOT_READY] pending=[$PENDING]"
	sleep 20
done
$K get pods -o custom-columns='POD:.metadata.name,READY:.status.containerStatuses[0].ready,STATUS:.status.phase,RESTARTS:.status.containerStatuses[0].restartCount,NODE:.spec.nodeName' 2>/dev/null \
	| sed 's/^/   /'
if [ "$SETTLED" = "1" ]; then
	echo "   all Running pods Ready, none Pending"
else
	echo "   pods did not settle"; FAILED=1
fi

echo "== 4. NFS-mounting services must avoid k8s-in-01"
for APP in rest data api submit sched; do
	NODE=$($K get pods -l "app=$APP" -o jsonpath='{.items[0].spec.nodeName}' 2>/dev/null || true)
	[ -z "$NODE" ] && continue
	if [ "$NODE" = "k8s-in-01" ]; then
		echo "   WRONG NODE: $APP is on k8s-in-01 (DMZ; NFS wedges there)"; FAILED=1
	else
		echo "   ok: $APP on $NODE"
	fi
done

echo "== 5. site responds"
CODE=$(curl -s -o /dev/null -w '%{http_code}' --max-time 30 "$URL" || echo 000)
echo "   $URL -> HTTP $CODE"
[ "$CODE" = "200" ] || FAILED=1

echo
if [ "$FAILED" = "0" ]; then
	echo "VERIFIED: $VERSION is live on $SITE ($NS)"
else
	echo "NOT VERIFIED -- see the failures above" >&2
fi
exit "$FAILED"
