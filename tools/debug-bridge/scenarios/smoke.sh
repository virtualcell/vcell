#!/usr/bin/env bash
#
# Smoke scenario: verifies bridge + running client end-to-end using only
# semantic selectors (no hard-coded index paths, so it survives UI changes).
#
# Prereq: a client running with the bridge enabled, e.g.
#   tools/debug-bridge/launch-client.sh        (from target/classes)
#   ./vcell.sh --debug-bridge                  (from packaged jars)
#
# Usage: scenarios/smoke.sh [port]

set -euo pipefail

BRIDGE="$(cd "$(dirname "$0")/.." && pwd)/bridge.sh"
export BRIDGE_PORT="${1:-${BRIDGE_PORT:-9123}}"

step() { echo; echo "== $* =="; }

step "bridge is answering"
"$BRIDGE" health

step "a VCell frame is showing (waits up to 60s for startup)"
"$BRIDGE" wait --type JFrame --contains VCell --timeout 60000 >/dev/null
echo ok

step "menu bar has a File menu with items"
"$BRIDGE" assert --type JMenu --text File >/dev/null
echo ok

step "menu structure is readable without opening popups"
"$BRIDGE" menus | head -40

step "EDT is responsive"
"$BRIDGE" idle

step "screenshot renders"
"$BRIDGE" shot

echo
echo "SMOKE OK"
