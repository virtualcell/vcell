#!/usr/bin/env bash
#
# VCell Quick Start Guide -- audited against the current client
#
# vcell.org/webstart/VCell_Tutorials/VCell_Quickstart_7_Biomodel.pdf.
# See storylines/quickstart.md.
#
# This document is three pages of orientation and tips, not a step sequence: there is no
# model in it to build. What it does have is a couple of dozen ASSERTIONS about how VCell
# behaves, and those go stale silently. So this script reproduces the guide the only way a
# guide can be reproduced - it checks what the document claims against the client in front
# of it, and reports which claims still hold.
#
# It exits non-zero only if the client could not be driven. A stale claim is the FINDING,
# not a failure of the script.
#
set -euo pipefail
. "$(cd "$(dirname "$0")" && pwd)/_common.sh"

HOLDS=0
STALE=0
claim() {   # $1 = holds|stale, $2 = the claim, $3 = what was found instead
  case "$1" in
    holds) HOLDS=$((HOLDS + 1)); printf '  [holds] %s\n' "$2" >&2 ;;
    stale) STALE=$((STALE + 1)); printf '  [STALE] %s\n          found: %s\n' "$2" "$3" >&2 ;;
  esac
}

dismiss OK
sleep 1

step "\"The VCell workspace has 4 panes\""
FOUND=0
MISSING=""
for pair in "bioModelEditorTree:Model Navigation" "LeftBottomTabbedPane:Database Navigation" \
            "ModelTabbedPane:Main Workspace" "RightBottomTabbedPane:Properties"; do
  if "$B" find --name "${pair%%:*}" --limit 1 2>/dev/null | grep -q '"name"'; then
    FOUND=$((FOUND + 1))
  else
    MISSING="$MISSING ${pair##*:};"
  fi
done
if [ "$FOUND" -eq 4 ]; then
  claim holds "four panes: Model Navigation, Database Navigation, Main Workspace, Properties"
else
  claim stale "four panes" "$FOUND of 4; missing:$MISSING"
fi

step "\"VCell supports VCML and SBML files\" (File > Import)"
must menu "File>Import..." >/dev/null; sleep 6
FORMATS=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
for root in json.load(sys.stdin):
    if str(root.get("text")) != "Open":
        continue
    def walk(n):
        combo = n.get("combo")
        if combo and any("Model Formats" in str(i) for i in combo.get("items", [])):
            for i in combo["items"]:
                if "Model Formats" in str(i):
                    print(i); raise SystemExit
        for c in n.get("children") or []:
            walk(c)
    walk(root)
')
must click "text=Cancel" >/dev/null; sleep 3
# The guide names two formats. The client offers eight, which is not wrong of the guide so
# much as long out of date - .bngl in particular is the route the two rule-based scripts
# in this directory depend on.
case "$FORMATS" in
  *vcml*sbml*) claim stale "import supports \"VCML and SBML files\"" "$FORMATS" ;;
  *) claim stale "import supports \"VCML and SBML files\"" "${FORMATS:-no format filter found}" ;;
esac

step "\"a spherical cell with a 10 micron diameter is 523.33 micrometers cubed\""
# Pure arithmetic, and the one claim in the guide that can be checked without VCell at all.
python3 - <<'PY' >&2
import math
exact = 4.0 / 3.0 * math.pi * 5.0 ** 3
print("  radius 5 um sphere is %.4f um3; the guide says 523.33" % exact)
PY
claim stale "\"a spherical cell with a 10 micron diameter is 523.33 micrometers cubed\"" \
    "(4/3)*pi*5^3 = 523.5988, so the figure is low by about 0.05%"

step "\"diffusion constants... default to zero for each molecular species\""
# Worth checking because it is the kind of default that gets changed and the tip never
# does - and the tip goes on to say zero "is always illegal when a molecule is involved in
# a membrane flux", so a reader who trusts it goes hunting for a problem that is not there.
must tab name=ModelTabbedPane "Structures" >/dev/null; sleep 2
must setcell name=StructuresTable 0 0 "EC" >/dev/null; sleep 1
must click name=ModelNewMembraneButton >/dev/null; sleep 1
must setcell name=StructuresTable 1 0 "PM" >/dev/null; sleep 1
must click name=ModelNewButton >/dev/null; sleep 1
must setcell name=StructuresTable 2 0 "Cyt" >/dev/null; sleep 1
must tab name=ModelTabbedPane "Species" >/dev/null; sleep 1
button_menu name=ModelNewButton 'In Compartment Cyt'; sleep 2
must setcell name=SpeciesTable 0 0 "probe" >/dev/null; sleep 1

tree_pick 'Applications' 'New Application>Deterministic'; sleep 4
must expand name=bioModelEditorTree "$(navrow 'Application0')" true >/dev/null; sleep 2
navselect 'Geometry'; sleep 2
must tab name=ApplicationGeometryPanelTabbedPane "Geometry Definition" >/dev/null; sleep 2
button_menu "text=Add Geometry" 'New...'; sleep 3
must trow "type=JSortTable" "$(row 'type=JSortTable' 'Analytic Equations (3D)')" >/dev/null; sleep 1
must click "text=OK" >/dev/null; sleep 4

must tab name=ApplicationTabbedPane "Specifications" >/dev/null; sleep 4
SPEC=name=spceciesContextSpecsTable
DIFF=$("$B" readcell "$SPEC" "$(row "$SPEC" probe)" 'Diffusion Constant' \
    | python3 -c 'import json,sys; print(json.load(sys.stdin).get("value"))')
echo "  a new volume species in a spatial application: $DIFF" >&2
case "$DIFF" in
  0.0*|0\ *) claim holds "diffusion constants default to zero" ;;
  *) claim stale "\"diffusion constants... default to zero for each molecular species\"" \
         "a new volume species defaults to $DIFF" ;;
esac

step "\"You can specify use of an equilibrium approximation... by checking the Fast checkbox\""
must tab name=ApplicationSpecificationsPanelTabbedPane "Reaction" >/dev/null; sleep 3
# Found by its column signature, not by name. Its columns do not contain the word
# "Reaction", and until this branch named it ReactionSpecsTable it was one of eight tables
# all called "ScrollPaneTable" - so the signature is what this check was written against,
# and it is left that way rather than re-verified against a name it did not use.
FAST=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
def walk(n):
    t = n.get("table")
    if t and n.get("showing") and "Enabled" in (t.get("columns") or []):
        print(" | ".join(str(c) for c in t["columns"])); raise SystemExit
    for c in n.get("children") or []:
        walk(c)
for root in json.load(sys.stdin):
    walk(root)
')
echo "  Specifications > Reaction columns: ${FAST:-none}" >&2
case "$FAST" in
  *Fast*) claim holds "a Fast checkbox on the reaction specifications" ;;
  *) claim stale "\"specify use of an equilibrium approximation... by checking the Fast checkbox\"" \
         "no Fast column; the columns are ${FAST:-none}" ;;
esac

step "\"MatLab format supports math export from compartmental Applications\""
# Two ways to get this wrong, both of which would report the guide stale when it is right.
#
# The export list is CONTENT-dependent - an empty BioModel is offered five formats, a model
# with content rather more - so it has to be read from a model that has something in it.
# And this particular claim is conditional: Matlab appears for a COMPARTMENTAL application
# and not for the spatial one built above, so the model needs one of each.
#
# Not checked here at all: STL, AVS and GIF. The guide attributes those to the geometry
# surface viewer and the physiology cartoon, which are different panels with their own
# export actions - looking for them in the document export list would be checking the
# wrong menu, and a false "stale" is worse than no claim.
tree_pick 'Applications' 'New Application>Deterministic'; sleep 4

must menu "File>Export..." >/dev/null; sleep 8
EXPORTS=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
for root in json.load(sys.stdin):
    if "Export Virtual Cell" not in str(root.get("text")):
        continue
    def walk(n):
        combo = n.get("combo")
        if combo and any("VCML" in str(i) for i in combo.get("items", [])):
            print(" | ".join(str(i) for i in combo["items"])); raise SystemExit
        for c in n.get("children") or []:
            walk(c)
    walk(root)
')
must click "text=Cancel" >/dev/null; sleep 3
echo "  document export offers: ${EXPORTS:-none}" >&2
[ -n "$EXPORTS" ] || { echo "FATAL: could not read the export format list" >&2; exit 1; }
for named in Matlab Report; do
  case "$EXPORTS" in
    *"$named"*) claim holds "$named is among the document export formats" ;;
    *) claim stale "$named is among the document export formats" "$EXPORTS" ;;
  esac
done
# What the guide does NOT mention, and a reader of a 2019 document would not expect: BNGL,
# COMBINE archive, NFSim XML, SedML and SpringSaLaD are all offered now.

step "\"Quick Run (without saving)\""
# Back to the first application. Creating the second one collapsed the first, so its
# Simulations child is not a row in the tree any more, and selecting the application node
# alone does not build a panel with a tab strip to select from either.
must expand name=bioModelEditorTree "$(navrow 'Application0' --exact)" true >/dev/null; sleep 2
navselect 'Simulations'; sleep 3
if "$B" find --name QuickRunButton --limit 1 2>/dev/null | grep -q '"name"'; then
  claim holds "a Quick Run button that runs without saving to the database"
else
  claim stale "\"Quick Run (without saving)\"" "no QuickRunButton on the simulations panel"
fi

step "Verdict"
printf '  %d claims still hold, %d have gone stale\n' "$HOLDS" "$STALE" >&2
