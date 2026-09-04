#!/usr/bin/env bash
#
# CLI for the Swing debug bridge (vcell-client/src/main/java/org/vcell/client/debug).
# Thin curl wrapper with URL-encoding, jq pretty-printing when available, and
# meaningful exit codes for `wait` / `assert` so scenarios can be plain shell.
#
# Usage: bridge.sh [-p PORT] <command> [args]   (or BRIDGE_PORT=N bridge.sh ...)
#
# Observe:
#   health | windows | tree [maxDepth] | menus
#   find    [--type T] [--name N] [--text T] [--contains S] [--limit N]
#   props   <selector>          listeners <selector>
#   findrow <selector> <text> [--exact]   row number by displayed text (searches the WHOLE
#                                         model, unlike tree's 25-row/100-row dump cap)
#   shot [window]               log [lines]      (shot takes ?scale/name/dir via replay)
# Act:
#   click <selector>            rclick <selector>
#   settext <selector> <text> [--enter]
#   tab <selector> <index>      row <selector> <row>     rrow <selector> <row>
#   expand <selector> <row> [true|false]                 drow <selector> <row>
#   trow <selector> <row> [col]   dtrow <selector> <row> [col]   rtrow <selector> <row> [col]
#   menu "<Menu>Item[>Sub]" [window]
#   highlight <selector> [ms]
#   glide <selector> [ms]       move the real cursor there (for a watched replay)
#   rbclick <selector> [glideMs] [row]  native press/release; unlike click it IS recordable
#   iconify <selector> [true|false]   minimize/restore a window; reports what the OS did
#   wbounds <selector> x y w h       move/resize a window
# Record / replay:
#   record start [file] | stop [file] | status    (script is flushed to disk each step)
#   replay <script.json> [--driver semantic|robot] [--speed N] [--max-delay MS]
#                        [--from N] [--to N] [--shots DIR] [--shot-scale F]
# Synchronize / assert (exit 0 on success, 1 on failure):
#   wait   [find opts] [--state showing|enabled|gone] [--timeout MS] [--interval MS]
#   assert [find opts] [--gone]
#   idle
#
# A <selector> is one of:
#   name=SearchButton[N]  by Component name (best); showing match wins, [N] disambiguates
#   c42                   stable component id from /tree
#   0/3/2                 positional node path (brittle)

set -eo pipefail

PORT="${BRIDGE_PORT:-9123}"
if [ "${1:-}" = "-p" ]; then
  PORT="$2"
  shift 2
fi
BASE="http://127.0.0.1:$PORT"

pretty() {
  if command -v jq >/dev/null 2>&1; then jq .; else cat; echo; fi
}

get() { # get <endpoint> [--data-urlencode k=v ...]
  local ep="$1"
  shift
  curl -fsS -G "$BASE/$ep" "$@"
}

# Build curl query args from shared find/wait selector options; leftover
# options are placed in EXTRA_OPTS for the caller.
FIND_ARGS=()
EXTRA_OPTS=()
parse_find_opts() {
  FIND_ARGS=()
  EXTRA_OPTS=()
  while [ $# -gt 0 ]; do
    case "$1" in
      --type)     FIND_ARGS+=(--data-urlencode "type=$2"); shift 2 ;;
      --name)     FIND_ARGS+=(--data-urlencode "name=$2"); shift 2 ;;
      --text)     FIND_ARGS+=(--data-urlencode "text=$2"); shift 2 ;;
      --contains) FIND_ARGS+=(--data-urlencode "textContains=$2"); shift 2 ;;
      --limit)    FIND_ARGS+=(--data-urlencode "limit=$2"); shift 2 ;;
      *)          EXTRA_OPTS+=("$1"); shift ;;
    esac
  done
}

cmd="${1:-help}"
shift || true

case "$cmd" in
  health)    get health; echo ;;
  windows)   get windows | pretty ;;
  tree)      get tree ${1:+--data-urlencode "maxDepth=$1"} | pretty ;;
  menus)     get menus | pretty ;;
  idle)      get idle | pretty ;;
  log)       get log --data-urlencode "lines=${1:-200}" ;;
  shot)      get screenshot ${1:+--data-urlencode "window=$1"} | pretty ;;

  find)
    parse_find_opts "$@"
    get find "${FIND_ARGS[@]}" | pretty
    ;;

  wait)
    parse_find_opts "$@"
    state=showing timeout=10000 interval=250
    set -- ${EXTRA_OPTS[@]+"${EXTRA_OPTS[@]}"}
    while [ $# -gt 0 ]; do
      case "$1" in
        --state)    state="$2"; shift 2 ;;
        --timeout)  timeout="$2"; shift 2 ;;
        --interval) interval="$2"; shift 2 ;;
        *) echo "wait: unknown option $1" >&2; exit 2 ;;
      esac
    done
    out="$(get waitFor "${FIND_ARGS[@]}" \
      --data-urlencode "state=$state" \
      --data-urlencode "timeoutMs=$timeout" \
      --data-urlencode "intervalMs=$interval")"
    echo "$out" | pretty
    echo "$out" | grep -q '"satisfied":true'
    ;;

  assert)
    parse_find_opts "$@"
    gone=false
    for opt in ${EXTRA_OPTS[@]+"${EXTRA_OPTS[@]}"}; do
      [ "$opt" = "--gone" ] && gone=true
    done
    out="$(get find "${FIND_ARGS[@]}")"
    if [ "$out" = "[]" ]; then present=false; else present=true; fi
    if [ "$present" != "$gone" ]; then
      echo "$out" | pretty
      exit 0
    fi
    if [ "$gone" = true ]; then expected="no matches"; else expected="at least one match"; fi
    echo "ASSERT FAILED: expected $expected, got: $out" >&2
    exit 1
    ;;

  click)     get click --data-urlencode "path=$1" | pretty ;;
  rclick)    get rightClick --data-urlencode "path=$1" | pretty ;;
  settext)
    sel="$1" text="$2"
    enter=false
    [ "${3:-}" = "--enter" ] && enter=true
    get setText --data-urlencode "path=$sel" --data-urlencode "text=$text" \
      --data-urlencode "enter=$enter" | pretty
    ;;
  tab)       get selectTab --data-urlencode "path=$1" --data-urlencode "index=$2" | pretty ;;
  row)       get selectTreeRow --data-urlencode "path=$1" --data-urlencode "row=$2" | pretty ;;
  expand)    get expandTreeRow --data-urlencode "path=$1" --data-urlencode "row=$2" \
               --data-urlencode "expand=${3:-true}" | pretty ;;
  drow)      get doubleClickTreeRow --data-urlencode "path=$1" --data-urlencode "row=$2" | pretty ;;
  trow)      get selectTableRow --data-urlencode "path=$1" --data-urlencode "row=$2" \
               ${3:+--data-urlencode "column=$3"} | pretty ;;
  dtrow)     get doubleClickTableRow --data-urlencode "path=$1" --data-urlencode "row=$2" \
               ${3:+--data-urlencode "column=$3"} | pretty ;;
  rtrow)     get rightClickTableRow --data-urlencode "path=$1" --data-urlencode "row=$2" \
               ${3:+--data-urlencode "column=$3"} | pretty ;;
  rrow)      get rightClickTreeRow --data-urlencode "path=$1" --data-urlencode "row=$2" | pretty ;;
  menu)      get menu --data-urlencode "path=$1" ${2:+--data-urlencode "window=$2"} | pretty ;;
  findrow)   get findRow --data-urlencode "path=$1" \
               $([ "${3:-}" = "--exact" ] && echo "--data-urlencode text=$2" || echo "--data-urlencode contains=$2") | pretty ;;
  props)     get props --data-urlencode "path=$1" | pretty ;;
  listeners) get listeners --data-urlencode "path=$1" | pretty ;;
  highlight) get highlight --data-urlencode "path=$1" --data-urlencode "ms=${2:-2000}" | pretty ;;
  glide)     get glide --data-urlencode "path=$1" --data-urlencode "ms=${2:-600}" | pretty ;;
  rbclick)   get robotClick --data-urlencode "path=$1" --data-urlencode "glideMs=${2:-0}" \
               ${3:+--data-urlencode "row=$3"} | pretty ;;

  record)
    action="${1:-status}"
    case "$action" in
      status) get record --data-urlencode "action=status" | pretty ;;
      start) get record --data-urlencode "action=start" ${2:+--data-urlencode "file=$2"} | pretty ;;
      stop)  get record --data-urlencode "action=stop" ${2:+--data-urlencode "file=$2"} | pretty ;;
      *) echo "record: expected start, stop or status" >&2; exit 2 ;;
    esac
    ;;

  replay)
    script="$1"
    shift || true
    PY=""
    for candidate in python3 python py; do
      if command -v "$candidate" >/dev/null 2>&1; then PY="$candidate"; break; fi
    done
    [ -n "$PY" ] || { echo "replay: need python3 on PATH" >&2; exit 2; }
    "$PY" "$(dirname "$0")/replay.py" "$script" --port "$PORT" "$@"
    ;;
  iconify)   get iconify --data-urlencode "path=$1" --data-urlencode "iconified=${2:-true}" | pretty ;;
  wbounds)   get windowBounds --data-urlencode "path=$1" --data-urlencode "x=$2" --data-urlencode "y=$3" --data-urlencode "w=$4" --data-urlencode "h=$5" | pretty ;;

  help|*)
    sed -n '2,36p' "$0" | sed 's/^# \{0,1\}//'
    [ "$cmd" = help ] || exit 2
    ;;
esac
