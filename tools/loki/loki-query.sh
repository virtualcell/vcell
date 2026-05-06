#!/usr/bin/env bash
# Query VCell prod Loki via logcli, after auto-starting the port-forward.
#
# Usage:
#   bash tools/loki/loki-query.sh [logcli-args...]
#   bash tools/loki/loki-query.sh --since=15m '{namespace="prod", container="data"} |~ "ERROR"'
#   bash tools/loki/loki-query.sh --from="2026-05-05T14:15:00Z" --to="2026-05-05T14:25:00Z" \
#       --output=raw --limit=500 '{namespace="prod", container="api"} |~ "HealthService"'
#
# All arguments are passed verbatim to `logcli query`.

set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Ensure the port-forward is up; capture its env exports.
EXPORTS="$(bash "$DIR/setup.sh" --quiet)"
eval "$EXPORTS"

exec logcli query --quiet "$@"
