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

echo "== 1. vcell-fluxcd image-tag commit"
gh api repos/virtualcell/vcell-fluxcd/commits --jq '.[0:5][] | "   \(.commit.committer.date[0:16])  \(.commit.message | split("\n")[0])"' \
	| grep -F "to $VERSION" || { echo "   no commit found for $VERSION"; FAILED=1; }

echo "== 2. deployment image tags in $NS"
OFF=$($K get deploy -o jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.spec.template.spec.containers[0].image}{"\n"}{end}' 2>/dev/null \
	| grep 'ghcr.io/virtualcell/' | grep -v ":$VERSION\$" || true)
if [ -n "$OFF" ]; then
	echo "   NOT on $VERSION:"; echo "$OFF" | sed 's/^/     /'; FAILED=1
else
	echo "   all vcell deployments on $VERSION"
fi

echo "== 3. pod readiness"
$K get pods -o custom-columns='POD:.metadata.name,READY:.status.containerStatuses[0].ready,STATUS:.status.phase,RESTARTS:.status.containerStatuses[0].restartCount,NODE:.spec.nodeName' 2>/dev/null \
	| sed 's/^/   /'

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
