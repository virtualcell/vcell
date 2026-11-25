#!/usr/bin/env bash
#
# Reconstruct the combined updates.xml from the three per-platform fragments produced by
# build_installers.sh (updates_win64.xml, updates_linux64.xml, updates_mac64.xml).
# The 32-bit media were dropped: Windows 32 (450) had no solvers and no 32-bit Corretto JRE exists;
# Linux 32 (547) bundled a 64-bit JRE and no solvers.
#
# Installs of the dropped media still check this file for updates, and install4j's updater only
# offers an entry whose targetMediaFileId equals the installed media id. Without an entry they
# would silently never update again. So each dropped media gets a redirect: a copy of the 64-bit
# entry for its platform (newMediaFileId unchanged, i.e. the 64-bit installer) retargeted at the
# legacy id, with a comment the updater shows under "Show comments".
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

# Print the entry lines of a fragment (everything between <updateDescriptor> and its end tag),
# retargeted at a legacy media id and carrying a comment. The comment must not contain '|', '&'
# or '<'.
#   redirect_entry <fragment> <legacy media id> <comment>
redirect_entry() {
  local fragment="$1" legacy_id="$2" comment="$3" n entry
  n=$(wc -l < "$fragment")
  entry=$(sed -n -e "3,$((n - 1))p" "$fragment" \
    | sed -e "s/targetMediaFileId=\"[0-9]*\"/targetMediaFileId=\"${legacy_id}\"/" \
          -e "s|<comment language=\"en\" */>|<comment language=\"en\">${comment}</comment>|")
  if ! grep -q "targetMediaFileId=\"${legacy_id}\"" <<<"$entry" || \
     ! grep -q "<comment language=\"en\">${comment}</comment>" <<<"$entry"; then
    echo "combine_updates.sh: could not build the redirect for media ${legacy_id} from ${fragment}" >&2
    exit 1
  fi
  printf '%s\n' "$entry"
}

win64c=$(wc -l < "$OUTDIR/updates_win64.xml")
linux64c=$(wc -l < "$OUTDIR/updates_linux64.xml")
mac64c=$(wc -l < "$OUTDIR/updates_mac64.xml")

sed -n -e "1,$((win64c - 1))p"   "$OUTDIR/updates_win64.xml"   >"$OUTDIR/updates.xml"
sed -n -e "3,$((linux64c - 1))p" "$OUTDIR/updates_linux64.xml" >>"$OUTDIR/updates.xml"
redirect_entry "$OUTDIR/updates_win64.xml" 450 \
  "VCell no longer provides a 32-bit Windows version. This update installs the 64-bit version, which can also run simulations on your computer. It requires 64-bit Windows." \
  >>"$OUTDIR/updates.xml"
redirect_entry "$OUTDIR/updates_linux64.xml" 547 \
  "VCell no longer provides a separate 32-bit Linux version. This update installs the 64-bit version, which can also run simulations on your computer." \
  >>"$OUTDIR/updates.xml"
sed -n -e "3,${mac64c}p"         "$OUTDIR/updates_mac64.xml"   >>"$OUTDIR/updates.xml"

echo "combine_updates.sh: wrote $OUTDIR/updates.xml"
