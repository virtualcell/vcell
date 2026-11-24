#!/usr/bin/env bash
#
# Reconstruct the combined updates.xml from the three per-platform fragments produced by
# build_installers.sh (updates_win64.xml, updates_linux64.xml, updates_mac64.xml).
# The 32-bit media were dropped: Windows 32 (450) had no solvers and no 32-bit Corretto JRE exists;
# Linux 32 (547) bundled a 64-bit JRE and no solvers.
#
# Pure bash/sed — no Install4j — so it runs either inside the clientgen container (after a full
# build) or in a fan-in CI job after the per-platform installers were built in parallel on
# separate runners.
#
# usage: combine_updates.sh [dir]      (dir defaults to the current directory)
#
set -euo pipefail

OUTDIR="${1:-.}"

for f in win64 linux64 mac64; do
  if [[ ! -f "$OUTDIR/updates_${f}.xml" ]]; then
    echo "combine_updates.sh: missing fragment $OUTDIR/updates_${f}.xml" >&2
    exit 1
  fi
done

win64c=$(wc -l < "$OUTDIR/updates_win64.xml")
linux64c=$(wc -l < "$OUTDIR/updates_linux64.xml")
mac64c=$(wc -l < "$OUTDIR/updates_mac64.xml")

sed -n -e "1,$((win64c - 1))p"   "$OUTDIR/updates_win64.xml"   >"$OUTDIR/updates.xml"
sed -n -e "3,$((linux64c - 1))p" "$OUTDIR/updates_linux64.xml" >>"$OUTDIR/updates.xml"
sed -n -e "3,${mac64c}p"         "$OUTDIR/updates_mac64.xml"   >>"$OUTDIR/updates.xml"

echo "combine_updates.sh: wrote $OUTDIR/updates.xml"
