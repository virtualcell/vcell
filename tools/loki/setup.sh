#!/usr/bin/env bash
# Configure logcli access to the VCell prod Loki instance.
#
# Idempotent: safe to run multiple times. Installs logcli if missing,
# starts a kubectl port-forward to loki-read in the background if one
# isn't already running on the chosen port, and prints the env vars
# required for logcli.
#
# Usage:
#   bash tools/loki/setup.sh           # start + print env (default port 3100)
#   bash tools/loki/setup.sh --port 3101
#   eval "$(bash tools/loki/setup.sh --quiet)"   # set env in current shell
#
# Required:
#   - kubectl in PATH
#   - kubeconfig with access to the prod cluster (default: $LOKI_KUBECONFIG
#     or ~/.kube/kubeconfig_vxrails.yaml)
#   - brew (for first-time logcli install on macOS)

set -euo pipefail

PORT=3100
QUIET=0
KUBECONFIG_PATH="${LOKI_KUBECONFIG:-$HOME/.kube/kubeconfig_vxrails.yaml}"
LOKI_NS="logging"
LOKI_SVC="loki-read"
LOKI_TENANT="uchc"
PF_LABEL="vcell-loki-pf"
PIDFILE="/tmp/${PF_LABEL}.pid"
LOGFILE="/tmp/${PF_LABEL}.log"

while [[ $# -gt 0 ]]; do
    case "$1" in
        --port) PORT="$2"; shift 2 ;;
        --quiet) QUIET=1; shift ;;
        --kubeconfig) KUBECONFIG_PATH="$2"; shift 2 ;;
        -h|--help)
            grep '^#' "$0" | sed 's/^# \{0,1\}//'
            exit 0 ;;
        *) echo "unknown arg: $1" >&2; exit 2 ;;
    esac
done

log() { [[ $QUIET -eq 1 ]] || echo "[loki-setup] $*" >&2; }
die() { echo "[loki-setup] ERROR: $*" >&2; exit 1; }

command -v kubectl >/dev/null || die "kubectl not found in PATH"
[[ -f "$KUBECONFIG_PATH" ]] || die "kubeconfig not found at $KUBECONFIG_PATH (set LOKI_KUBECONFIG)"

# Install logcli on macOS via brew (no-op if already installed).
if ! command -v logcli >/dev/null; then
    log "logcli not found"
    if [[ "$(uname)" == "Darwin" ]] && command -v brew >/dev/null; then
        log "installing logcli via brew..."
        brew install logcli >/dev/null
    else
        die "install logcli manually: https://grafana.com/docs/loki/latest/query/logcli/"
    fi
fi

# Reuse existing port-forward if it's still alive on the same port.
if [[ -f "$PIDFILE" ]]; then
    OLD_PID="$(cat "$PIDFILE")"
    if kill -0 "$OLD_PID" 2>/dev/null \
       && lsof -iTCP:"$PORT" -sTCP:LISTEN -P 2>/dev/null | grep -q "[ ]$OLD_PID[ ]"; then
        log "reusing existing port-forward (pid $OLD_PID, port $PORT)"
    else
        log "stale pidfile, cleaning up"
        rm -f "$PIDFILE"
    fi
fi

# Start a new port-forward if needed.
if [[ ! -f "$PIDFILE" ]]; then
    if lsof -iTCP:"$PORT" -sTCP:LISTEN -P >/dev/null 2>&1; then
        die "port $PORT already in use by another process — pass --port <other>"
    fi
    log "starting port-forward: svc/$LOKI_SVC -n $LOKI_NS :$PORT"
    nohup kubectl --kubeconfig "$KUBECONFIG_PATH" \
        -n "$LOKI_NS" port-forward "svc/$LOKI_SVC" "$PORT:3100" \
        > "$LOGFILE" 2>&1 &
    echo $! > "$PIDFILE"
    # Wait for the local port to accept connections.
    for _ in $(seq 1 20); do
        if curl -fsS "http://localhost:$PORT/ready" >/dev/null 2>&1; then break; fi
        sleep 0.5
    done
    if ! curl -fsS "http://localhost:$PORT/ready" >/dev/null 2>&1; then
        die "port-forward did not become ready — see $LOGFILE"
    fi
fi

# Verify Loki is reachable with the tenant header.
if ! curl -fsS -H "X-Scope-OrgID: $LOKI_TENANT" \
        "http://localhost:$PORT/loki/api/v1/labels" >/dev/null; then
    die "Loki not responding for tenant '$LOKI_TENANT' — port-forward may be stale"
fi

log "ready: tenant=$LOKI_TENANT, port=$PORT"
echo "export LOKI_ADDR=http://localhost:$PORT"
echo "export LOKI_ORG_ID=$LOKI_TENANT"
