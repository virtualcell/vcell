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
# It also refreshes every entry's fileSize/md5Sum/sha256Sum, and md5sums/sha256sums, from the
# installer files themselves (see below), so it must run after the mac .dmg is notarized.
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

# install4j writes fileSize/md5Sum/sha256Sum (and md5sums/sha256sums) when it builds a media file,
# but the mac .dmg is changed afterwards: the notarize job staples the notarization ticket to it.
# The published entries then describe a file that no longer exists (8.1.8.07: 3,727 bytes short,
# both checksums wrong). And each per-media CI artifact carries its own md5sums/sha256sums, so when
# they are merged only one platform's survive. Recompute both from the installers in OUTDIR, as
# they will be published.
file_size() { stat -c %s "$1" 2>/dev/null || stat -f %z "$1"; }
file_md5() { if command -v md5sum >/dev/null; then md5sum "$1" | cut -d' ' -f1; else md5 -q "$1"; fi; }
file_sha256() { if command -v sha256sum >/dev/null; then sha256sum "$1" | cut -d' ' -f1; else shasum -a 256 "$1" | cut -d' ' -f1; fi; }

installers=$(sed -n -e 's/.* fileName="\([^"]*\)".*/\1/p' "$OUTDIR/updates.xml" | sort -u)
: >"$OUTDIR/md5sums.new"
: >"$OUTDIR/sha256sums.new"
for name in $installers; do
  if [[ ! -f "$OUTDIR/$name" ]]; then
    echo "combine_updates.sh: installer $OUTDIR/$name named in updates.xml is missing" >&2
    exit 1
  fi
  size=$(file_size "$OUTDIR/$name")
  md5=$(file_md5 "$OUTDIR/$name")
  sha256=$(file_sha256 "$OUTDIR/$name")
  echo "$md5 *$name" >>"$OUTDIR/md5sums.new"
  echo "$sha256 *$name" >>"$OUTDIR/sha256sums.new"
  for xml in "$OUTDIR"/updates.xml "$OUTDIR"/updates_win64.xml "$OUTDIR"/updates_linux64.xml "$OUTDIR"/updates_mac64.xml; do
    awk -v name="$name" -v size="$size" -v md5="$md5" -v sha256="$sha256" '
      index($0, "fileName=\"" name "\"") {
        sub(/fileSize="[0-9]*"/, "fileSize=\"" size "\"")
        sub(/md5Sum="[0-9a-f]*"/, "md5Sum=\"" md5 "\"")
        sub(/sha256Sum="[0-9a-f]*"/, "sha256Sum=\"" sha256 "\"")
      }
      { print }' "$xml" >"$xml.new"
    mv "$xml.new" "$xml"
  done
  echo "combine_updates.sh: $name  $size bytes  sha256 $sha256"
done
mv "$OUTDIR/md5sums.new" "$OUTDIR/md5sums"
mv "$OUTDIR/sha256sums.new" "$OUTDIR/sha256sums"

echo "combine_updates.sh: wrote $OUTDIR/updates.xml, md5sums, sha256sums"
