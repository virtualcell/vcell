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

# JSON parsing. Git Bash on Windows ships curl and sed but no Python, and where Python
# is installed it is often "python" rather than "python3" - so resolve it once and say
# so plainly if it is missing, rather than failing later with an empty result that looks
# like a test failure.
PY=""
for candidate in python3 python py; do
  if command -v "$candidate" >/dev/null 2>&1 && "$candidate" -c "import json,sys" >/dev/null 2>&1; then
    PY="$candidate"
    break
  fi
done
if [ -z "$PY" ]; then
  echo "ERROR: need python3 (or python) on PATH to read the bridge's JSON." >&2
  echo "       On Windows, install Python or run this from WSL." >&2
  exit 2
fi

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

# the child window's own path in /windows, so nothing is hard-coded to an index
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

# ask the real window manager to minimize, and report only what it says happened
try_minimize() {
  "$BRIDGE" iconify "$(child_path)" "$1" | "$PY" -c "
import json,sys
d=json.load(sys.stdin)
print(str(d.get('iconified')).lower() if 'iconified' in d else 'ERROR')
"
}

# The detach/reattach control is an icon with a tooltip - no text to match on - so it is
# selected by component name. Its tooltip is what tells the user which way it will go, so
# the script asserts on that too: an icon whose tooltip did not flip would be a silent lie.
TOGGLE=DetachWindowToggle

# Is the control big enough to actually draw its 16px icon? The icon is clipped rather
# than scaled if the menu bar squashes the item, which no functional assertion notices -
# it happened for real once the window-list control was removed and nothing else was left
# in the bar to hold the row open.
icon_fits() {
  "$BRIDGE" find --name "$TOGGLE" | "$PY" -c "
import json,sys
d=json.load(sys.stdin)
if not d:
    print('CONTROL-NOT-FOUND')
else:
    b=d[0]['bounds']
    print('yes' if b['h'] >= 16 and b['w'] >= 16 else 'no (%dx%d)'%(b['w'],b['h']))
"
}

# which action the tooltip currently offers: "Detach" or "Reattach"
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

# does the CHILD window carry the window-list (hamburger) control? Scoped to the child's
# own subtree - the document window has one of its own, which a global search would hit.
has_window_menu() {
  "$BRIDGE" find --type JMenu --limit 50 | "$PY" -c "
import json,sys
d=json.load(sys.stdin)
prefix = sys.argv[1] + '/'
print('yes' if any(isinstance(c,dict) and c.get('name')=='WindowIconMenu'
                   and c.get('path','').startswith(prefix) for c in d) else 'no')
" "$(child_path)"
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
warn_ok=$("$BRIDGE" find --type JButton --text OK --limit 20 | "$PY" -c "
import json,sys
d=json.load(sys.stdin)
# only a button inside a dialog, i.e. not in window 0, the document window
print(next((c['id'] for c in d if isinstance(c,dict) and not c.get('path','').startswith('0/')), ''))
")
if [ -n "$warn_ok" ]; then
  "$BRIDGE" click "$warn_ok" >/dev/null
  # Wait for it to be really gone, not just clicked. Menu activations that land while a
  # modal dialog is still tearing down are swallowed, which cost several confusing runs.
  "$BRIDGE" wait --type JButton --text OK --state gone --timeout 15000 >/dev/null || true
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
for attempt in 1 2 3 4 5 6 7 8 9 10; do
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
"$BRIDGE" wait --name "$TOGGLE" --timeout 20000 >/dev/null
pause

step "normalize: make sure we start attached"
if [ "$(field owner)" = "null" ]; then
  "$BRIDGE" click "name=$TOGGLE" >/dev/null
  "$BRIDGE" idle >/dev/null
  echo "  was detached from a previous run; reattached"
else
  echo "  ok"
fi

step "ATTACHED: owned by the document window, and cannot be minimized"
owner=$(field owner)
check "owner is a window (not null)"      "yes"   "$([ "$owner" = null ] && echo no || echo yes)"
check "canIconify"                        "false" "$(field canIconify)"
# Move it somewhere a user might have dragged it to. This matters: a window left where
# it opened already sits where "helpfully" re-centring it would put it back, so a test
# that never moves the window cannot see a re-centring bug at all. One did exist - the
# logical-window framework repositions in componentShown, after setBounds - and it went
# unnoticed here until someone dragged the window by hand.
"$BRIDGE" wbounds "$(child_path)" 80 90 640 300 >/dev/null
"$BRIDGE" idle >/dev/null
before=$(bounds)
echo "  bounds after moving it off-centre: $before"
check "window went where it was put"      "80,90,640,300" "$before"
# the user's actual complaint: asking an owned dialog to minimize does nothing
check "minimize request refused"          "false" "$(try_minimize true)"
pause

step "click the detach control (icon + tooltip, no text)"
check "tooltip offers detaching"          "Detach"   "$(toggle_offers)"
check "icon not clipped by the menu bar"  "yes"      "$(icon_fits)"
check "no window-list control while attached" "no" "$(has_window_menu)"
"$BRIDGE" click "name=$TOGGLE" >/dev/null
"$BRIDGE" wait --name "$TOGGLE" --timeout 10000 >/dev/null
"$BRIDGE" idle >/dev/null
pause

step "DETACHED: un-owned, and the OS really does minimize it"
check "owner is null"                     "null"  "$(field owner)"
check "canIconify"                        "true"  "$(field canIconify)"
check "bounds unchanged by detaching"     "$before" "$(bounds)"

# the crux: ask the real window manager, and believe only what it reports back
check "minimize request honoured"         "true"  "$(try_minimize true)"
check "restored"                          "false" "$(try_minimize false)"
pause

step "click the reattach control"
check "tooltip now offers reattaching"    "Reattach" "$(toggle_offers)"
check "icon not clipped by the menu bar"  "yes"      "$(icon_fits)"
check "window-list control appears once detached" "yes" "$(has_window_menu)"
"$BRIDGE" click "name=$TOGGLE" >/dev/null
"$BRIDGE" wait --name "$TOGGLE" --timeout 10000 >/dev/null
"$BRIDGE" idle >/dev/null
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
