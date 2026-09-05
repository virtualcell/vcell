# Shared helpers for the tutorial scenarios. Source it, do not run it.
#
# The theme is the same throughout: say what a thing IS, and never let a step that
# did not happen pass for one that did.

B="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)/bridge.sh"

step() { printf '\n=== %s\n' "$*" >&2; }

# Run a bridge verb and stop if the bridge says it did not take.
#
# Every acting endpoint answers {"clicked": true} / {"set": false} / ... and throwing
# that away is how a mistake becomes invisible: `trow` is selectTableRow, so calling it
# on a JTree returns {"selected": false} and changes nothing, and a scenario that pipes
# to /dev/null carries on as though the node had been selected. That is exactly the
# failure mode these scripts exist to catch, so it should not be one they can have.
must() {
  local out rc
  out=$("$B" "$@" 2>&1); rc=$?
  if [ $rc -ne 0 ]; then
    echo "FATAL: bridge call failed ($rc): $*" >&2; echo "$out" >&2; exit 1
  fi
  printf '%s' "$out" | python3 -c '
import json, sys
raw = sys.stdin.read()
try:
    d = json.loads(raw)
except ValueError:
    sys.exit(0)                      # not a result object; nothing to assert on
if not isinstance(d, dict):
    sys.exit(0)
for k in ("clicked", "selected", "set", "doubleClicked", "rightClicked"):
    if d.get(k) is False:
        sys.stderr.write("  bridge reported %s=false\n" % k)
        sys.exit(1)
' || { echo "FATAL: step did not take: $*" >&2; exit 1; }
  printf '%s' "$out"
}

# Resolve a row by its DISPLAYED text, and stop if it never appears. A silent -1 is how a
# mapping step turns into a no-op that is only noticed pages later, when the simulation
# quietly uses defaults.
#
# It RETRIES rather than trusting a fixed sleep, because a panel's columns exist before its
# rows do: selecting a spatial process gives you a parameter table with its four headers
# immediately and its velocityX/velocityY rows a moment later, so `col` succeeds while
# `row` still sees nothing. Extra arguments are passed through to findrow (--exact, --in).
row() {
  local sel="$1" text="$2"; shift 2
  local r i
  for i in $(seq 1 20); do
    r=$("$B" findrow "$sel" "$text" "$@" | python3 -c 'import json,sys;print(json.load(sys.stdin)["row"])')
    [ "$r" -ge 0 ] && { printf '%s' "$r"; return 0; }
    sleep 0.5
  done
  echo "FATAL: no row reading '$text' in $sel after 10s" >&2
  exit 1
}

# Column index by header text, same retry for the same reason.
col() {
  local c i
  for i in $(seq 1 20); do
    c=$("$B" findcol "$1" "$2" | python3 -c 'import json,sys;print(json.load(sys.stdin)["column"])')
    [ "$c" -ge 0 ] && { printf '%s' "$c"; return 0; }
    sleep 0.5
  done
  echo "FATAL: no column headed '$2' in $1 after 10s" >&2
  exit 1
}

navrow() { row "name=bioModelEditorTree" "$1"; }

# Select a node in the model navigation tree. `row` is selectTreeRow; `trow` is
# selectTableRow and does nothing here.
navselect() { must row name=bioModelEditorTree "$(navrow "$1")" >/dev/null; }

# Dismiss a dialog that may or may not be there - the version-mismatch warning at
# startup, or the eager "structure not mapped" error raised while a geometry is still
# half-built. Deliberately NOT `must`: absence is the normal case.
dismiss() { "$B" click "text=$1" >/dev/null 2>&1 || true; }
