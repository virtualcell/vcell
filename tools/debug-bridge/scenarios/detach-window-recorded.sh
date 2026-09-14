#!/usr/bin/env bash
#
# The recorded twin of detach-window.sh.
#
# WHY THIS EXISTS
#   detach-window.sh drives the UI with hand-written bridge calls. This one makes the
#   identical assertions, but every user action comes from recordings/detach-window.json -
#   a capture of somebody actually opening the menu and clicking the toggle twice. It is
#   the acceptance test for the recorder: if a recording can stand in for a hand-written
#   scenario without weakening it, the recorder captured the right things at the right
#   level of abstraction.
#
#   It also shows the intended division of labour. The recording holds the NAVIGATION -
#   what was clicked, in what order, with what timing. The scenario holds the ASSERTIONS,
#   which no recorder can infer, plus the window moves and minimize requests that are test
#   scaffolding rather than things a user did.
#
# WHAT IS ASSERTED   (the same round trip as detach-window.sh)
#   attached   ->  owned by the document window,  canIconify == false,  minimize refused
#   detached   ->  un-owned,                      canIconify == true,   minimize honoured
#   reattached ->  owned again,  and bounds unchanged across the whole round trip
#
# HOW THE STEPS ARE INTERLEAVED
#   Replay runs a step at a time (--from/--to) so state is checked BETWEEN clicks. Playing
#   the whole script and asserting once at the end would pass even if detach and reattach
#   both silently did nothing.
#
# PREREQ  a client running with the bridge enabled:
#           tools/debug-bridge/launch-client.sh
#
# USAGE   scenarios/detach-window-recorded.sh [port]
#         DRIVER=robot scenarios/detach-window-recorded.sh   # cursor visibly moves; film this

set -euo pipefail

HERE="$(cd "$(dirname "$0")" && pwd)"
BRIDGE="$HERE/../bridge.sh"
SCRIPT="$HERE/recordings/detach-window.json"
export BRIDGE_PORT="${1:-${BRIDGE_PORT:-9123}}"
DRIVER="${DRIVER:-semantic}"

CHILD_TITLE="View VCell Properties"
TOGGLE=DetachWindowToggle

PY=""
for candidate in python3 python py; do
  if command -v "$candidate" >/dev/null 2>&1 && "$candidate" -c "import json,sys" >/dev/null 2>&1; then
    PY="$candidate"
    break
  fi
done
if [ -z "$PY" ]; then
  echo "ERROR: need python3 (or python) on PATH to read the bridge's JSON." >&2
  exit 2
fi
[ -f "$SCRIPT" ] || { echo "ERROR: no recording at $SCRIPT" >&2; exit 2; }

pass=0
fail=0
step()  { echo; echo "== $* =="; }
check() {
  if [ "$2" = "$3" ]; then
    echo "  PASS  $1 (= $3)"
    pass=$((pass + 1))
  else
    echo "  FAIL  $1 -- expected '$2', got '$3'"
    fail=$((fail + 1))
  fi
}

# play one step of the recording, by its 1-based number in the script
play() { "$BRIDGE" replay "$SCRIPT" --driver "$DRIVER" --from "$1" --to "$1" --max-delay 1500; }

field() {
  "$BRIDGE" windows | "$PY" -c "
import json,sys
key=sys.argv[1]
for w in json.load(sys.stdin):
    if w.get('text','') == sys.argv[2]:
        v = w.get(key)
        print('null' if v is None else (str(v).lower() if isinstance(v,bool) else v))
        break
else:
    print('WINDOW-NOT-FOUND')
" "$1" "$CHILD_TITLE"
}

bounds() {
  "$BRIDGE" windows | "$PY" -c "
import json,sys
for w in json.load(sys.stdin):
    if w.get('text','') == sys.argv[1]:
        b=w['bounds']; print('%d,%d,%d,%d'%(b['x'],b['y'],b['w'],b['h'])); break
else:
    print('WINDOW-NOT-FOUND')
" "$CHILD_TITLE"
}

child_path() {
  "$BRIDGE" windows | "$PY" -c "
import json,sys
for w in json.load(sys.stdin):
    if w.get('text','') == sys.argv[1]:
        print(w['path']); break
else:
    print('WINDOW-NOT-FOUND')
" "$CHILD_TITLE"
}

try_minimize() {
  "$BRIDGE" iconify "$(child_path)" "$1" | "$PY" -c "
import json,sys
d=json.load(sys.stdin)
print(str(d.get('iconified')).lower() if 'iconified' in d else 'ERROR')
"
}

toggle_offers() {
  "$BRIDGE" find --name "$TOGGLE" | "$PY" -c "
import json,sys
d=json.load(sys.stdin)
if not d:
    print('CONTROL-NOT-FOUND')
else:
    tip = d[0].get('tooltip') or ''
    print(tip.split()[0].rstrip(':,.-') if tip else 'NO-TOOLTIP')
"
}

step "bridge is answering"
"$BRIDGE" health >/dev/null
echo ok

step "wait for the client to finish starting"
"$BRIDGE" wait --type JFrame --contains VCell --timeout 90000 >/dev/null
"$BRIDGE" wait --type JMenu --text Help --timeout 30000 >/dev/null
echo ok

# A source build reports a different version than the server, so a modal "version
# mismatch" warning appears a moment AFTER the menus do. It blocks input to everything
# behind it, which makes every later click land on nothing - so wait for it rather than
# looking once and moving on.
step "dismiss the version-mismatch warning if present"
if "$BRIDGE" wait --type JButton --text OK --timeout 20000 >/dev/null 2>&1; then
  warn_ok=$("$BRIDGE" find --type JButton --text OK --limit 20 | "$PY" -c "
import json,sys
d=json.load(sys.stdin)
print(next((c['id'] for c in d if isinstance(c,dict) and not c.get('path','').startswith('0/')), ''))
")
  if [ -n "$warn_ok" ]; then
    "$BRIDGE" click "$warn_ok" >/dev/null
    "$BRIDGE" wait --type JButton --text OK --state gone --timeout 15000 >/dev/null || true
    echo "  dismissed"
  fi
else
  echo "  none showing"
fi
"$BRIDGE" idle >/dev/null

step "STEP 1 of the recording: open the child window from the menu"
if [ "$(field owner)" = "WINDOW-NOT-FOUND" ]; then
  play 1
else
  echo "  already open, reusing it"
fi
"$BRIDGE" wait --name "$TOGGLE" --timeout 20000 >/dev/null
check "child window is open"              "yes"  "$([ "$(field owner)" = WINDOW-NOT-FOUND ] && echo no || echo yes)"

step "normalize: start attached"
if [ "$(field owner)" = "null" ]; then
  "$BRIDGE" click "name=$TOGGLE" >/dev/null
  "$BRIDGE" idle >/dev/null
  echo "  was detached from a previous run; reattached"
else
  echo "  ok"
fi

step "ATTACHED: owned by the document window, and cannot be minimized"
check "owner is a window (not null)"      "yes"   "$([ "$(field owner)" = null ] && echo no || echo yes)"
check "canIconify"                        "false" "$(field canIconify)"
# Move it off-centre first: a window left where it opened already sits where a
# re-centring bug would put it back, so a test that never moves it cannot see one.
"$BRIDGE" wbounds "$(child_path)" 80 90 640 300 >/dev/null
"$BRIDGE" idle >/dev/null
before=$(bounds)
check "window went where it was put"      "80,90,640,300" "$before"
check "minimize request refused"          "false" "$(try_minimize true)"
check "tooltip offers detaching"          "Detach" "$(toggle_offers)"

step "STEP 2 of the recording: click the detach control"
play 2
"$BRIDGE" wait --name "$TOGGLE" --timeout 10000 >/dev/null
"$BRIDGE" idle >/dev/null

step "DETACHED: un-owned, and the OS really does minimize it"
check "owner is null"                     "null"  "$(field owner)"
check "canIconify"                        "true"  "$(field canIconify)"
check "bounds unchanged by detaching"     "$before" "$(bounds)"
check "minimize request honoured"         "true"  "$(try_minimize true)"
check "restored"                          "false" "$(try_minimize false)"
check "tooltip now offers reattaching"    "Reattach" "$(toggle_offers)"

step "STEP 3 of the recording: click the reattach control"
play 3
"$BRIDGE" wait --name "$TOGGLE" --timeout 10000 >/dev/null
"$BRIDGE" idle >/dev/null

step "REATTACHED: owned again, nothing moved"
check "owner is a window again"           "yes"   "$([ "$(field owner)" = null ] && echo no || echo yes)"
check "canIconify"                        "false" "$(field canIconify)"
check "bounds unchanged over round trip"  "$before" "$(bounds)"

echo
echo "-------------------------------------------"
echo "  passed: $pass    failed: $fail"
echo "-------------------------------------------"
[ "$fail" -eq 0 ] || exit 1
echo "DETACH (RECORDED) OK"
