#!/usr/bin/env bash
#
# VCell Tutorial: rule-based modelling, Ran nuclear transport
#
# Reproduces the storyline of
# vcell.org/webstart/VCell_Tutorials/7.7/VCell Tutorial_ Rule-Based Ran Transport 7.7.pdf,
# the 2025 rewrite of VCell6.1_Rule-Based_Ran_Transport_Tutorial.pdf.
# See storylines/rule-based-ran-transport.md.
#
# The same biology as multi-app-transport, said the other way: molecules with sites and
# reaction rules instead of named species and reactions, and across five compartments
# rather than one.
#
# Built by importing BNGL, for the reason set out at the top of rule-based-egfr.sh: the
# reaction rule editor is a single hand-rendered component with no child components at
# all, and a rule's BioNetGen definition cell is read-only, so there is nothing a script
# can address. BNGL import is the other documented way in, and here it carries the whole
# model - unlike EGFR, this one is purely rule-based.
#
set -euo pipefail
. "$(cd "$(dirname "$0")" && pwd)/_common.sh"

WORK="${WORK:-${TMPDIR:-/tmp}}"
BNGL="$WORK/rule-based-ran-transport.bngl"

dismiss OK
sleep 1

step "Write the model as BNGL"
# What VCell writes when the public reference model Rule-based_Ran_transport (VCell
# database, Tutorials folder) is exported to .bngl.
cat > "$BNGL" <<'BNGLEOF'
begin model

begin compartments
nuc	3	1
cyt	3	1
EC	3	1
pm	2	1
nm	2	1
end compartments

begin parameters
end parameters

begin molecule types
Ran(cargo)
C(site,Y1~u~p,Y2~u~p,Y3~u~p)
RCC1(site)
end molecule types

begin anchors
RCC1(nuc)
end anchors

begin seed species
1 @nuc:Ran(cargo!1).C(site!1,Y1~u,Y2~u,Y3~u) 1000.0
2 @nuc:RCC1(site) 1000.0
end seed species

begin observables
Molecules Ran_cyt @cyt:Ran()
Molecules Cargo_cyt @cyt:C()
Molecules RCC1_nuc @nuc:RCC1()
Molecules Cargo_phosp_cyt_total @nuc:C(Y1~p!?) @nuc:C(Y2~p!?) @nuc:C(Y3~p!?)
Molecules Cargo_nuc @nuc:C()
Molecules Cargo_phosp_cyt @cyt:C(Y1~p!?,Y2~p!?,Y3~p!?)
Molecules Ran_bound_cyt @cyt:Ran(cargo!+)
end observables

begin functions
end functions

begin reaction rules
Transport:	@nuc:Ran(cargo!+) <-> @cyt:Ran(cargo!+)		2.0 * 602.0, 0.0
Ran_C_bind_cyt:	@cyt:Ran(cargo!1).C(site!1) <-> @cyt:Ran(cargo) + @cyt:C(site)		1.0, 100.0
C_p1:	@cyt:C(Y3~u!?) <-> @cyt:C(Y3~p!?)		10.0, 1.0
C_p2:	@cyt:C(Y2~u!?) <-> @cyt:C(Y2~p!?)		10.0, 1.0
C_p3:	@cyt:C(Y1~u!?) <-> @cyt:C(Y1~p!?)		10.0, 1.0
Ran_RCC1_bind:	@nuc:Ran(cargo) + @nuc:RCC1(site) <-> @nuc:Ran(cargo!1).RCC1(site!1)		1.0, 100.0
Ran_C_bind_nuc:	@nuc:Ran(cargo!1).C(site!1) <-> @nuc:Ran(cargo) + @nuc:C(site)		1.0, 100.0
end reaction rules

end model

simulate_nf({t_end=>10.0,n_steps=>200})
BNGLEOF
echo "  wrote $(wc -l < "$BNGL") lines to $BNGL" >&2

step "Import it"
must menu "File>Import..." >/dev/null; sleep 6
must choosefile "type=VCFileChooser" "$BNGL" >/dev/null; sleep 12
# The reference model counts molecules, not concentrations - its seed species are 1000
# each and its transport rate is written 2.0 * 602.0, which is a per-molecule rate.
must click "text=Molecules" >/dev/null; sleep 2
dialog_button "Bngl Units Selector" OK; sleep 15

# Importing opens a new document window and leaves the empty one VCell starts with.
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
# Five structures, three of them volumes and two membranes - BNGL says which by the
# dimension in its compartments block (3 for a volume, 2 for a membrane), so the whole
# compartment topology survives the round trip.
for expected in "Molecules (3)" "Species (2)" "Observables (7)" "Reactions (7)" "Structures (5)"; do
  got=$(counts "${expected%% (*} (")
  printf '  %-18s %s\n' "${expected%% (*}" "$got" >&2
  [ "$got" = "$expected" ] || { echo "FATAL: expected '$expected', got '$got'" >&2; exit 1; }
done

step "Give the species the names the reference uses"
navselect 'Species'; sleep 3
for pair in "Ran(cargo!1).C(site!1,Y1~u,Y2~u,Y3~u):Ran_C_nuc" "RCC1(site):RCC1"; do
  pattern=${pair%:*}; name=${pair##*:}
  must setcell name=SpeciesTable \
      "$(row name=SpeciesTable "$pattern" --exact --in 'BioNetGen Definition')" 0 "$name" >/dev/null
  sleep 2
done

step "Done -- rule-based Ran transport across five compartments."
