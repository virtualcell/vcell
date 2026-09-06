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

# Click a button INSIDE the dialog whose title contains $1, and wait for that dialog to
# go away.
#
# The Edit Simulation dialog is unusual: it CLONES the simulation, edits the clone, and
# replaces the original in the document only on OK. So committing a field is not enough -
# a focusLost writes the value into the clone, and closing the dialog is what writes the
# clone back. Leave it open and every edit is discarded silently, with the table still
# showing the old numbers.
#
# A bare `click text=OK` is not safe for this either: it resolves against every showing
# window and can land on a different OK. Resolving the button within the dialog, and then
# confirming the dialog has actually closed, is what makes the edit stick.
dialog_button() {   # $1 = title fragment, $2 = button label
  local path
  path=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
frag, label = sys.argv[1], sys.argv[2]
for root in json.load(sys.stdin):
    if frag not in str(root.get("text")):
        continue
    def walk(n):
        if n.get("class", "").endswith("JButton") and n.get("text") == label:
            print(n.get("path")); raise SystemExit
        for c in n.get("children") or []:
            walk(c)
    walk(root)
' "$1" "$2")
  if [ -z "$path" ]; then
    echo "FATAL: no '$2' button in a dialog titled like '$1'" >&2
    exit 1
  fi
  must click "$path" >/dev/null
  local i
  for i in $(seq 1 20); do
    sleep 0.5
    if ! curl -s "http://127.0.0.1:9123/windows" | grep -q "$1"; then
      return 0
    fi
  done
  echo "FATAL: dialog '$1' did not close after clicking '$2'" >&2
  exit 1
}

# Right-click a model-tree row and pick an item from the context menu that appears.
#
# Retried as a UNIT, because a pop-up is transient and its appearance is not instant: the
# menu is built and shown from a dispatched mouse event, so a fixed sleep between opening
# it and clicking an item is a race. Opening it again after a miss is harmless - a second
# right-click just replaces the menu.
tree_pick() {   # $1 = row text in the model tree, $2 = menu item label
  local attempt
  for attempt in 1 2 3 4 5; do
    "$B" rrow name=bioModelEditorTree "$(navrow "$1")" >/dev/null 2>&1
    sleep 1.5
    if "$B" find --text "$2" --limit 1 2>/dev/null | grep -q '"text"'; then
      if "$B" click "text=$2" 2>/dev/null | grep -q '"clicked": true'; then
        return 0
      fi
    fi
    sleep 1
  done
  echo "FATAL: context menu item '$2' never took, right-clicking '$1'" >&2
  exit 1
}

# Pick an item from a menu that is ALREADY open (a submenu, or a button's pop-up),
# retrying for the same reason.
menu_pick() {   # $1 = menu item label
  local attempt
  for attempt in 1 2 3 4 5; do
    if "$B" find --text "$1" --limit 1 2>/dev/null | grep -q '"text"'; then
      if "$B" click "text=$1" 2>/dev/null | grep -q '"clicked": true'; then
        return 0
      fi
    fi
    sleep 1
  done
  echo "FATAL: menu item '$1' never took" >&2
  exit 1
}

# Run the selected simulation ON THIS MACHINE and wait for its results window.
#
# "Native Quick Run" executes with the bundled local solvers and does NOT save the
# document to the database, so a scripted tutorial can produce real results without an
# account and without putting anything on the server. The local install carries every
# solver these tutorials use - SundialsSolverStandalone, FiniteVolume, MovingBoundary and
# the stochastic ones - and canQuickRun only refuses parallel solvers, server-only
# features, and executables missing for the platform.
quick_run() {   # $1 = simulation row (default 0)
  must trow name=SimulationsTable "${1:-0}" >/dev/null; sleep 1
  must click name=QuickRunButton >/dev/null
  local i
  for i in $(seq 1 120); do
    sleep 1
    if "$B" windows 2>/dev/null | grep -q 'Results for Simulation'; then
      sleep 2   # let the data table populate after the window appears
      return 0
    fi
  done
  echo "FATAL: no results window after a quick run" >&2
  exit 1
}

# Read one cell of the results table. Row -1 is the last row - the end of the time course,
# which is where a steady state is. Not subject to the tree dump's 25-row cap.
#
# The table is resolved by PATH within the results window rather than by name, because
# more than one PlotDataTable exists (the document window has its own, empty) and NEITHER
# reports isShowing: the results window opens on the plot, with the spreadsheet as a
# hidden card that still holds the data. So the usual "prefer what is showing" tie-break
# has nothing to work with, and a bare name lands on whichever came first.
result_table_path() {
  curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
for root in json.load(sys.stdin):
    if "Results for Simulation" not in str(root.get("text")):
        continue
    def walk(n):
        if n.get("name") == "PlotDataTable":
            print(n.get("path")); raise SystemExit
        for c in n.get("children") or []:
            walk(c)
    walk(root)
'
}

result_cell() {   # $1 = row, $2 = column header
  local path
  path=$(result_table_path)
  if [ -z "$path" ]; then
    echo "FATAL: no results data table found" >&2
    exit 1
  fi
  "$B" readcell "$path" "$1" "$2" \
    | python3 -c 'import json,sys; print(json.load(sys.stdin).get("value"))'
}

# Row index of the last output point at or before time $1.
#
# A variable-time-step integrator (IDA, CVODE) picks its own output times, so there is no
# row sitting exactly on the moment a tutorial asks about, and reading "the value at 5 s"
# by guessing a row number quietly reports a different instant. The time column is
# monotonic, so a binary search lands on the right row in ~9 reads rather than hundreds.
result_row_at_time() {   # $1 = time
  local path rows
  path=$(result_table_path)
  rows=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
def walk(n):
    if n.get("name") == "PlotDataTable" and n.get("table"):
        print(n["table"]["rowCount"]); raise SystemExit
    for c in n.get("children") or []:
        walk(c)
for root in json.load(sys.stdin):
    if "Results for Simulation" in str(root.get("text")):
        walk(root)
')
  if [ -z "$path" ] || [ -z "$rows" ]; then
    echo "FATAL: no results data table found" >&2
    exit 1
  fi
  python3 "$(dirname "${BASH_SOURCE[0]}")/row_at_time.py" "$path" "$rows" "$1"
}

# Select a tab on the tabbed pane INSIDE the dialog whose title contains $1.
#
# The Edit Simulation dialog's pane is called JTabbedPane1, and so is the one in a results
# window - which is likely to be open, since these tutorials run their simulations. An
# unqualified name picks whichever is found first, so scope it to the dialog.
dialog_tab() {   # $1 = dialog title fragment, $2 = tab title
  local path
  path=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
frag = sys.argv[1]
for root in json.load(sys.stdin):
    if frag not in str(root.get("text")):
        continue
    def walk(n):
        if "tabs" in n:
            print(n.get("path")); raise SystemExit
        for c in n.get("children") or []:
            walk(c)
    walk(root)
' "$1")
  if [ -z "$path" ]; then
    echo "FATAL: no tabbed pane in a dialog titled like '$1'" >&2
    exit 1
  fi
  must tab "$path" "$2" >/dev/null
}

# Dismiss a dialog that may or may not be there - the version-mismatch warning at
# startup, or the eager "structure not mapped" error raised while a geometry is still
# half-built. Deliberately NOT `must`: absence is the normal case.
dismiss() { "$B" click "text=$1" >/dev/null 2>&1 || true; }
