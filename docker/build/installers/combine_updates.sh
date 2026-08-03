#!/usr/bin/env bash
#
# Reconstruct the combined updates.xml from the five per-platform fragments produced by
# build_installers.sh (updates_win64.xml, updates_win32.xml, updates_linux64.xml,
# updates_linux32.xml, updates_mac64.xml).
#
# Pure bash/sed — no Install4j — so it runs either inside the clientgen container (after a full
# 5-media build) or in a fan-in CI job after the per-platform installers were built in parallel on
# separate runners.
#
# usage: combine_updates.sh [dir]      (dir defaults to the current directory)
#
set -euo pipefail

OUTDIR="${1:-.}"

for f in win64 win32 linux64 linux32 mac64; do
  if [[ ! -f "$OUTDIR/updates_${f}.xml" ]]; then
    echo "combine_updates.sh: missing fragment $OUTDIR/updates_${f}.xml" >&2
    exit 1
  fi
done

win64c=$(wc -l < "$OUTDIR/updates_win64.xml")
win32c=$(wc -l < "$OUTDIR/updates_win32.xml")
linux64c=$(wc -l < "$OUTDIR/updates_linux64.xml")
linux32c=$(wc -l < "$OUTDIR/updates_linux32.xml")
mac64c=$(wc -l < "$OUTDIR/updates_mac64.xml")

sed -n -e "1,$((win64c - 1))p"   "$OUTDIR/updates_win64.xml"   >"$OUTDIR/updates.xml"
sed -n -e "3,$((win32c - 1))p"   "$OUTDIR/updates_win32.xml"   >>"$OUTDIR/updates.xml"
sed -n -e "3,$((linux64c - 1))p" "$OUTDIR/updates_linux64.xml" >>"$OUTDIR/updates.xml"
sed -n -e "3,$((linux32c - 1))p" "$OUTDIR/updates_linux32.xml" >>"$OUTDIR/updates.xml"
sed -n -e "3,${mac64c}p"         "$OUTDIR/updates_mac64.xml"   >>"$OUTDIR/updates.xml"

echo "combine_updates.sh: wrote $OUTDIR/updates.xml"
