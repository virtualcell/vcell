#!/usr/bin/env bash
#
# Detach/reattach scenario for modeless child windows.
#
# WHY THIS EXISTS
#   Modeless child windows (simulation results, geometry viewers, ...) are OWNED
#   dialogs, so the OS keeps them above their document window and gives them no
#   taskbar/dock button. A Dialog also has no iconified state at all, so it cannot
#   be minimized. On a small screen there is then no way to get a results window
#   out of the way. "Detach Window" swaps the owned dialog for an un-owned frame,
#   trading the z-order guarantee for a window the user can minimize and arrange.
#
#   That behaviour is a property of the OS, not of our Java code, so it has to be
#   checked against a running client on each platform. This script does that with
#   plain assertions on the bridge's JSON - no screenshots, no image diffing, and
#   nothing for a human to eyeball in order to get a pass/fail.
#
#   A human CAN watch it: it drives the real UI, and PAUSE=<seconds> slows it down.
#
# WHAT IS ASSERTED
#   attached  ->  owner is the document window   AND  canIconify == false
#   detached  ->  owner is null                  AND  canIconify == true
#                 AND an actual minimize request is honoured by the OS
#   reattached->  owned again, and bounds are unchanged across the whole round trip
#
#   canIconify/owner come straight from the live AWT objects; the minimize check
#   asks the real window manager and reports what it actually did, so a platform
#   that refuses would fail here rather than pass silently.
#
# PREREQ  a client running with the bridge enabled:
#           tools/debug-bridge/launch-client.sh     (from target/classes)
#           ./vcell.sh --debug-bridge               (from packaged jars)
#
# USAGE   scenarios/detach-window.sh [port]
#         PAUSE=1 scenarios/detach-window.sh        # slow enough to watch
#
# PLATFORMS  macOS and Linux out of the box. On Windows run it from Git Bash or
#            WSL - it is plain POSIX shell plus curl, which both provide.

set -euo pipefail

BRIDGE="$(cd "$(dirname "$0")/.." && pwd)/bridge.sh"
export BRIDGE_PORT="${1:-${BRIDGE_PORT:-9123}}"
PAUSE="${PAUSE:-0}"

CHILD_TITLE="View VCell Properties"
OPEN_MENU="Help>VCell Properties ..."

pass=0
fail=0

step()  { echo; echo "== $* =="; }
pause() { [ "$PAUSE" = 0 ] || sleep "$PAUSE"; }

check() { # check <description> <expected> <actual>
  if [ "$2" = "$3" ]; then
    echo "  PASS  $1 (= $3)"
    pass=$((pass + 1))
  else
    echo "  FAIL  $1 -- expected '$2', got '$3'"
    fail=$((fail + 1))
  fi
}

# field <json> <key>  -- reads one field of the child window's entry in /windows
field() {
  "$BRIDGE" windows | python3 -c "
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
  "$BRIDGE" windows | python3 -c "
import json,sys
for w in json.load(sys.stdin):
    if w.get('text','') == sys.argv[1]:
        b=w['bounds']; print('%d,%d,%d,%d'%(b['x'],b['y'],b['w'],b['h'])); break
else:
    print('WINDOW-NOT-FOUND')
" "$CHILD_TITLE"
}

# the child window's own path in /windows, so nothing is hard-coded to an index
child_path() {
  "$BRIDGE" windows | python3 -c "
import json,sys
for w in json.load(sys.stdin):
    if w.get('text','') == sys.argv[1]:
        print(w['path']); break
else:
    print('WINDOW-NOT-FOUND')
" "$CHILD_TITLE"
}

# ask the real window manager to minimize, and report only what it says happened
try_minimize() {
  "$BRIDGE" iconify "$(child_path)" "$1" | python3 -c "
import json,sys
d=json.load(sys.stdin)
print(str(d.get('iconified')).lower() if 'iconified' in d else 'ERROR')
"
}

# id of the attach/detach menu item, whichever way it currently reads
toggle_id() {
  "$BRIDGE" find --contains "$1" | python3 -c "
import json,sys
d=json.load(sys.stdin)
print(d[0]['id'] if d else 'MENU-ITEM-NOT-FOUND')
"
}

step "bridge is answering"
"$BRIDGE" health >/dev/null
echo ok

step "wait for the client to finish starting"
"$BRIDGE" wait --type JFrame --contains VCell --timeout 90000 >/dev/null
"$BRIDGE" wait --type JMenu --text Help --timeout 30000 >/dev/null
echo ok

# A source build reports a different version than the server, so the client opens a
# "version mismatch" warning over everything. Dismiss it rather than requiring the
# person running this to get there first.
step "dismiss the version-mismatch warning if present"
warn_ok=$("$BRIDGE" find --type JButton --text OK --limit 20 | python3 -c "
import json,sys
d=json.load(sys.stdin)
# only a button inside a dialog, i.e. not in window 0, the document window
print(next((c['id'] for c in d if isinstance(c,dict) and not c.get('path','').startswith('0/')), ''))
")
if [ -n "$warn_ok" ]; then
  "$BRIDGE" click "$warn_ok" >/dev/null
  echo "  dismissed"
else
  echo "  none showing"
fi
"$BRIDGE" idle >/dev/null
pause

# The first menu activation immediately after a modal dialog is dismissed is swallowed
# (the click lands while the dialog is still tearing down), so open with a retry rather
# than assuming one attempt takes.
step "open a modeless child window ($OPEN_MENU)"
opened=no
for attempt in 1 2 3 4 5; do
  if [ "$(field owner)" != "WINDOW-NOT-FOUND" ]; then
    opened=yes
    [ "$attempt" = 1 ] && echo "  already open, reusing it" || echo "  opened on attempt $attempt"
    break
  fi
  "$BRIDGE" menu "$OPEN_MENU" >/dev/null || true
  "$BRIDGE" idle >/dev/null || true
  sleep 1
done
if [ "$opened" != yes ]; then
  echo "  FAIL  could not open '$CHILD_TITLE' via $OPEN_MENU"
  exit 1
fi
"$BRIDGE" wait --type JMenuItem --contains "etach Window" --timeout 20000 >/dev/null
pause

step "normalize: make sure we start attached"
if [ "$(field owner)" = "null" ]; then
  "$BRIDGE" click "$(toggle_id 'Reattach Window')" >/dev/null
  "$BRIDGE" wait --type JMenuItem --contains "Detach Window" --timeout 10000 >/dev/null
  echo "  was detached from a previous run; reattached"
else
  echo "  ok"
fi

step "ATTACHED: owned by the document window, and cannot be minimized"
owner=$(field owner)
check "owner is a window (not null)"      "yes"   "$([ "$owner" = null ] && echo no || echo yes)"
check "canIconify"                        "false" "$(field canIconify)"
before=$(bounds)
echo "  bounds: $before"
# the user's actual complaint: asking an owned dialog to minimize does nothing
check "minimize request refused"          "false" "$(try_minimize true)"
pause

step "click 'Detach Window'"
"$BRIDGE" click "$(toggle_id 'Detach Window')" >/dev/null
"$BRIDGE" wait --type JMenuItem --contains "Reattach Window" --timeout 10000 >/dev/null
pause

step "DETACHED: un-owned, and the OS really does minimize it"
check "owner is null"                     "null"  "$(field owner)"
check "canIconify"                        "true"  "$(field canIconify)"
check "bounds unchanged by detaching"     "$before" "$(bounds)"

# the crux: ask the real window manager, and believe only what it reports back
check "minimize request honoured"         "true"  "$(try_minimize true)"
check "restored"                          "false" "$(try_minimize false)"
pause

step "click 'Reattach Window'"
"$BRIDGE" click "$(toggle_id 'Reattach Window')" >/dev/null
"$BRIDGE" wait --type JMenuItem --contains "Detach Window" --timeout 10000 >/dev/null
pause

step "REATTACHED: owned again, nothing moved"
owner=$(field owner)
check "owner is a window again"           "yes"   "$([ "$owner" = null ] && echo no || echo yes)"
check "canIconify"                        "false" "$(field canIconify)"
check "bounds unchanged over round trip"  "$before" "$(bounds)"

echo
echo "-------------------------------------------"
echo "  passed: $pass    failed: $fail"
echo "-------------------------------------------"
[ "$fail" -eq 0 ] || exit 1
echo "DETACH OK"
