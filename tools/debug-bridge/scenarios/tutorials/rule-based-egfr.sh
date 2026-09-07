#!/usr/bin/env bash
#
# VCell Tutorial: rule-based modelling, EGFR
#
# Reproduces the storyline of
# vcell.org/webstart/VCell_Tutorials/7.7/VCell Tutorial_ Rule-Based EGFR 7.7.pdf,
# the 2025 rewrite of VCell6.1_Rule-Based_Tutorial.pdf. See storylines/rule-based-egfr.md.
#
# EGFR binding two adapter proteins: a ligand binds the receptor monomer, dimerization
# follows, the kinase transphosphorylates two tyrosines, and those independently recruit
# Grb2 and Shc.
#
# HOW THIS ONE IS BUILT, AND WHY. The tutorial builds molecules, species, observables and
# rules by right-clicking SHAPES in a graphics editor - "right click on the molecule shape
# to call up a menu... select Add site". Three of those four have a table route, and the
# tutorial names it itself: "Every table has a column BioNetGen definition... useful if you
# have separate BNGL code you want to paste". Setting that one cell to
# EGFR(ecd,tmd,Y1~u~p,Y2~u~p) replaces a dozen gestures.
#
# Reaction rules do NOT have that route. BioModelEditorReactionTableModel.isCellEditable
# allows the equation column only when the row is a ReactionStep, and a ReactionRule is
# not one - the rule editor is the canvas, and nothing else. So this script takes the
# other documented path: it writes the model as BNGL and imports it, which the 6.1
# reference guide describes as one of the two ways to get a rule-based model, and which
# creates the deterministic and network-free applications on the way in.
#
set -euo pipefail
. "$(cd "$(dirname "$0")" && pwd)/_common.sh"

WORK="${WORK:-${TMPDIR:-/tmp}}"
BNGL="$WORK/rule-based-egfr.bngl"

dismiss OK
sleep 1

step "Write the model as BNGL"
# Not invented: this is what VCell itself writes when the public reference model
# Rule-based_egfr_tutorial (VCell database, Tutorials folder) is exported to .bngl. The
# tutorial says throughout to "match all values to the model in the Tutorials folder", so
# that model is the authority for every rate constant here.
cat > "$BNGL" <<'BNGLEOF'
begin model

begin compartments
c0	3	1
end compartments

begin parameters
end parameters

begin molecule types
EGF(Site)
EGFR(ecd,tmd,Y1~u~p,Y2~u~p)
Grb2(sh2)
Shc(sh3,Y~u~p)
end molecule types

begin seed species
1 @c0:EGFR(ecd,tmd,Y1~u,Y2~u) 100.0
2 @c0:EGF(Site) 680.0
3 @c0:Grb2(sh2) 58.0
4 @c0:Shc(sh3,Y~p) 0.0
5 @c0:Shc(sh3,Y~u) 150.0
end seed species

begin observables
Molecules O0_EGF_tot @c0:EGF()
Molecules O0_EGFR_tot @c0:EGFR()
Molecules O0_Grb2_tot @c0:Grb2()
Molecules O0_Shc_tot @c0:Shc()
Molecules Dimers @c0:EGFR(tmd!+)
Species Dimers_s @c0:EGFR(tmd!+)
Molecules Y1 @c0:EGFR(Y1~p!?)
Molecules Y2 @c0:EGFR(Y2~p!?)
Molecules Y_total @c0:EGFR(Y1~p!?) @c0:EGFR(Y2~p!?)
end observables

begin functions
end functions

begin reaction rules
ligand_bind:	@c0:EGFR(ecd,tmd) + @c0:EGF(Site) <-> @c0:EGFR(ecd!1,tmd).EGF(Site!1)		0.003, 0.06
dimeriz:	@c0:EGFR(ecd!+,tmd)%1 + @c0:EGFR(ecd!+,tmd)%2 <-> @c0:EGFR(ecd!+,tmd!1)%1.EGFR(ecd!+,tmd!1)%2		0.001, 0.1
Y2_phosph:	@c0:EGFR(tmd!+,Y2~u) -> @c0:EGFR(tmd!+,Y2~p)		0.5
Y1_phosph:	@c0:EGFR(tmd!+,Y1~u) -> @c0:EGFR(tmd!+,Y1~p)		0.5
Y2_dephosph:	@c0:EGFR(Y2~p) -> @c0:EGFR(Y2~u)		4.5
Y1_dephosph:	@c0:EGFR(Y1~p) -> @c0:EGFR(Y1~u)		4.5
R_Grb2_interaction:	@c0:EGFR(Y1~p) + @c0:Grb2(sh2) <-> @c0:EGFR(Y1~p!1).Grb2(sh2!1)		0.001, 0.05
R_ShcU_interaction:	@c0:EGFR(Y2~p) + @c0:Shc(sh3,Y~u) <-> @c0:EGFR(Y2~p!1).Shc(sh3!1,Y~u)		0.045, 0.6
Shc_phosph:	@c0:EGFR(Y2~p!1).Shc(sh3!1,Y~u) -> @c0:EGFR(Y2~p!1).Shc(sh3!1,Y~p)		3.0
R_ShcP_interaction:	@c0:EGFR(Y2~p) + @c0:Shc(sh3,Y~p) <-> @c0:EGFR(Y2~p!1).Shc(sh3!1,Y~p)		4.5E-4, 0.3
end reaction rules

end model

generate_network({max_iter=>12,max_agg=>12,max_stoich=>{EGF=>100,EGFR=>100,Grb2=>100,Shc=>100},overwrite=>1})
BNGLEOF
echo "  wrote $(wc -l < "$BNGL") lines to $BNGL" >&2

step "Import it"
must menu "File>Import..." >/dev/null; sleep 6
must choosefile "type=VCFileChooser" "$BNGL" >/dev/null; sleep 12

# BNGL carries numbers without units, so VCell asks what they meant. This is the same
# choice the tutorial makes on a separate page ("the unit system must be changed BEFORE
# entering any numeric values, otherwise all values will be converted") - the reference
# model is in nM, and the seed species above are concentrations.
must click "text=Concentrations" >/dev/null; sleep 2
UNITS=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
for root in json.load(sys.stdin):
    if "Bngl Units Selector" not in str(root.get("text")):
        continue
    def walk(n):
        combo = n.get("combo")
        if combo and any("nanomolar" in str(i) for i in combo.get("items", [])):
            print(n.get("path")); raise SystemExit
        for c in n.get("children") or []:
            walk(c)
    walk(root)
')
must combo "$UNITS" "nM (nanomolar)" >/dev/null; sleep 2
dialog_button "Bngl Units Selector" OK; sleep 15

# Importing opens a NEW document window and leaves the empty one VCell starts with. Two
# document windows means two bioModelEditorTrees, and every navselect after this would be
# a coin toss between them.
must menu "File>Close" 0 >/dev/null; sleep 6

step "Check what came in against the reference model"
counts() {   # $1 = node prefix, e.g. "Reactions ("
  curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
want = sys.argv[1]
def walk(n):
    if n.get("name") == "bioModelEditorTree" and n.get("tree"):
        for row in n["tree"]["rows"]:
            if str(row.get("text", "")).startswith(want):
                print(row["text"]); raise SystemExit
    for c in n.get("children") or []:
        walk(c)
for root in json.load(sys.stdin):
    walk(root)
' "$1"
}
for expected in "Molecules (4)" "Species (5)" "Observables (9)" "Reactions (10)" "Applications (2)"; do
  got=$(counts "${expected%% (*} (")
  printf '  %-18s %s\n' "${expected%% (*}" "$got" >&2
  [ "$got" = "$expected" ] || { echo "FATAL: expected '$expected', got '$got'" >&2; exit 1; }
done
# Two applications, not three: the import creates the BioNetGen (network-generating) and
# NFSim (network-free) ones. The stochastic one is made below, as the tutorial does.

step "Give the species the names the tutorial uses"
# BNGL seed species carry no names, so the import invents them from their patterns -
# Shc_sh3_Yp and so on. That matters beyond tidiness: the reaction added below is written
# "ShcP -> ShcU", and an equation cannot PLACE a species. Against the imported names those
# two would be unknown, and VCell would silently create two new empty species rather than
# reuse the structured ones.
navselect 'Species'; sleep 3
for pair in "EGFR(ecd,tmd,Y1~u,Y2~u):R" "EGF(Site):L" "Grb2(sh2):Grb2" \
            "Shc(sh3,Y~p):ShcP" "Shc(sh3,Y~u):ShcU"; do
  pattern=${pair%:*}; name=${pair##*:}
  must setcell name=SpeciesTable \
      "$(row name=SpeciesTable "$pattern" --exact --in 'BioNetGen Definition')" 0 "$name" >/dev/null
  sleep 2
done

step "Add the one reaction BNGL cannot carry"
# The reference model has eleven reactions - ten rules and one ordinary reaction,
# ShcP -> ShcU. VCell says so on the way out: "Simple Reactions cannot be exported to
# .bngl format. Some information will be lost." So the round trip drops it, and it has to
# be put back by hand - which the Reactions table can do, because its equation column IS
# editable for a ReactionStep, just not for a ReactionRule.
navselect 'Reactions'; sleep 3
must click name=ModelNewButton >/dev/null; sleep 3
# With one structure in the model there is no compartment to choose, so no dialog appears -
# the row is created directly and is edited in place.
must setcell name=ReactionsTable "$(row name=ReactionsTable 'r0' --exact --in 'Name')" 1 \
    "ShcDephosp" >/dev/null; sleep 2
must setcell name=ReactionsTable "$(row name=ReactionsTable 'ShcDephosp' --exact --in 'Name')" 0 \
    "ShcP -> ShcU" >/dev/null; sleep 4

must trow name=ReactionsTable "$(row name=ReactionsTable 'ShcDephosp' --exact --in 'Name')" >/dev/null
sleep 3
# An equation typed with a single arrow still arrives reversible; reversibility is its own
# property, not something the arrow sets. Left alone, the model warns that mass action
# "will be interpreted as a degradation of the product".
must click name=ReactionReversibleCheckBox >/dev/null; sleep 3
EXP=$(col name=ReactionKineticsParametersTable "Expression")
must setcell name=ReactionKineticsParametersTable \
    "$(row name=ReactionKineticsParametersTable 'Kf' --exact --in 'Name')" "$EXP" "0.005" >/dev/null
sleep 3

SPECIES=$(counts "Species (")
echo "  $SPECIES" >&2
[ "$SPECIES" = "Species (5)" ] || {
  echo "FATAL: expected 'Species (5)' after adding the reaction - $SPECIES means the" >&2
  echo "       equation created new species instead of reusing the imported ones." >&2
  exit 1
}

step "Done -- rule-based EGFR physiology, eleven reactions, two applications."
