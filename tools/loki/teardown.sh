#!/usr/bin/env bash
# Stop the loki-read port-forward started by setup.sh.

set -euo pipefail
PF_LABEL="vcell-loki-pf"
PIDFILE="/tmp/${PF_LABEL}.pid"

if [[ -f "$PIDFILE" ]]; then
    PID="$(cat "$PIDFILE")"
    if kill -0 "$PID" 2>/dev/null; then
        kill "$PID"
        echo "[loki-teardown] killed port-forward pid $PID" >&2
    fi
    rm -f "$PIDFILE"
else
    echo "[loki-teardown] no port-forward pidfile — nothing to do" >&2
fi
