#!/usr/bin/env bash
#
# Spatial Rule-Based Guide -- audited against the current client
#
# vcell.org/webstart/VCell_Tutorials/SpatialRuleBasedGuide.pdf (2017, VCell 6.1).
# See storylines/spatial-rule-based.md.
#
# A reference guide, like the Quick Start: prose and figures, no step sequence to follow.
# It is also the oldest unrevised rule-based document in the set - the 7.7 refresh covered
# the two single-compartment tutorials and not this one - so it is the likeliest to be
# describing a VCell that has moved on. It ends with a list of limitations explicitly
# labelled "temporary, will be lifted in future releases", written nine years ago, and
# those are worth knowing the truth of.
#
# So this reproduces the guide the way a guide can be reproduced: it checks what the
# document asserts against the client in front of it. It exits non-zero only if the client
# could not be driven; a stale claim is the FINDING, not a failure.
#
set -euo pipefail
. "$(cd "$(dirname "$0")" && pwd)/_common.sh"

WORK="${WORK:-${TMPDIR:-/tmp}}"

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

step "\"Tutorial rule-based models are located in Tutorial... under VCellDB -> BioModels\""
# The guide names three models by name. Whether they are still there is the first thing a
# reader finds out, and the first thing that would waste their time.
DB="0/0/1/0/1/0/0/1/2/0/0/0/1/0/0"
TUT=$(row "$DB" 'Tutorials')
must expand "$DB" "$TUT" true >/dev/null; sleep 6
LISTING=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
want = sys.argv[1]
def walk(n):
    if n.get("path") == want and n.get("tree"):
        print(" | ".join(str(r.get("text")) for r in n["tree"]["rows"])); raise SystemExit
    for c in n.get("children") or []:
        walk(c)
for root in json.load(sys.stdin):
    walk(root)
' "$DB")
if [ -z "$LISTING" ]; then
  echo "FATAL: could not read the database tree. Is the client logged in? A client/server" >&2
  echo "       version mismatch leaves it reading 'not connected'." >&2
  exit 1
fi
echo "  Tutorials folder: $(printf '%s' "$LISTING" | tr '|' '\n' | grep -c .) entries" >&2
for model in Rule-based_Ran_transport Mix_Reactions_Rules RB_Enzyme_Kinetics; do
  case "$LISTING" in
    *"$model"*) claim holds "the model \"$model\" the guide points at is in the Tutorials folder" ;;
    *) claim stale "the guide sends the reader to a model called \"$model\"" \
           "no such model in the Tutorials folder" ;;
  esac
done

step "\"a rule-based model will be created with two applications: Network-Free called NFSim, and... BioNetGen\""
BNGL="$WORK/spatial-rule-based-probe.bngl"
# The smallest rule-based model that exercises the claim: two molecules, one binding rule.
cat > "$BNGL" <<'BNGLEOF'
begin model
begin parameters
end parameters
begin molecule types
A(b)
B(a)
end molecule types
begin seed species
1 A(b) 100.0
2 B(a) 100.0
end seed species
begin observables
Molecules A_tot A()
end observables
begin reaction rules
bind:	A(b) + B(a) <-> A(b!1).B(a!1)		0.01, 0.1
end reaction rules
end model
BNGLEOF
must menu "File>Import..." >/dev/null; sleep 6
must choosefile "type=VCFileChooser" "$BNGL" >/dev/null; sleep 12
# "You will be asked about units and simulation volume during import" - so this prompt
# appearing is itself one of the guide's claims.
if curl -s "http://127.0.0.1:9123/windows" | grep -q "Bngl Units Selector"; then
  claim holds "the import asks about units and simulation volume"
else
  claim stale "\"You will be asked about units and simulation volume during import\"" \
      "no units prompt appeared"
fi
dialog_button "Bngl Units Selector" OK; sleep 15
must menu "File>Close" 0 >/dev/null; sleep 6

APPS=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
def walk(n):
    if n.get("name") == "bioModelEditorTree" and n.get("tree"):
        rows = [str(r.get("text")) for r in n["tree"]["rows"]]
        seen = False
        out = []
        for r in rows:
            if r.startswith("Applications ("):
                seen = True; continue
            if seen:
                if r.startswith("Parameters,"):
                    break
                out.append(r)
        print(" | ".join(out)); raise SystemExit
    for c in n.get("children") or []:
        walk(c)
for root in json.load(sys.stdin):
    walk(root)
')
echo "  applications created by the import: ${APPS:-none}" >&2
case "$APPS" in
  *NFSim*BioNetGen*|*BioNetGen*NFSim*)
    claim holds "importing BNGL creates two applications, one network-free and one deterministic" ;;
  *) claim stale "importing BNGL creates \"NFSim\" and \"BioNetGen\" applications" "${APPS:-none}" ;;
esac

step "\"Only mass-action kinetic laws are supported\" in reaction rules"
navselect 'Reactions'; sleep 3
must trow name=ReactionsTable "$(row name=ReactionsTable 'bind' --exact --in 'Name')" >/dev/null
sleep 3
KINETICS=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
def walk(n):
    combo = n.get("combo")
    if combo and any("Mass" in str(i) for i in combo.get("items", [])):
        print(" | ".join(str(i) for i in combo["items"])); raise SystemExit
    for c in n.get("children") or []:
        walk(c)
for root in json.load(sys.stdin):
    walk(root)
')
echo "  kinetic types offered for a rule: ${KINETICS:-none}" >&2
case "$KINETICS" in
  *"|"*) claim stale "\"Only mass-action kinetic laws are supported\" in reaction rules" \
             "the rule offers: $KINETICS" ;;
  *Mass*) claim holds "a reaction rule offers only mass action" ;;
  *) claim stale "reading the kinetic types offered for a rule" "${KINETICS:-none}" ;;
esac

step "\"Molecules cannot have identical sites\""
# Checkable directly: the BioNetGen definition column will either accept Twin(s,s) or it
# will not, and no amount of reading the guide settles it.
#
# The refusal arrives as a MODAL dialog, so the setcell call blocks until something
# dismisses it - which is why this runs the call in the background and then goes looking
# for the dialog. A check that can hang is not a check.
navselect 'Molecules'; sleep 3
must click name=ModelNewButton >/dev/null; sleep 3
BNG=$(col name=MolecularTypeTable 'BioNetGen Definition')
NEW=$(row name=MolecularTypeTable 'MT0' --exact)
"$B" setcell name=MolecularTypeTable "$NEW" "$BNG" 'Twin(s,s)' >/dev/null 2>&1 &
SETCELL=$!
REFUSAL=""
for i in $(seq 1 20); do
  sleep 1
  REFUSAL=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, re, sys
for root in json.load(sys.stdin):
    if "Error" not in str(root.get("text")):
        continue
    def walk(n):
        text = str(n.get("text") or "")
        if "Site" in text and "Molecule" in text:
            print(re.sub("<[^>]+>", "", text).strip()); raise SystemExit
        for c in n.get("children") or []:
            walk(c)
    walk(root)
')
  [ -n "$REFUSAL" ] && break
done
if [ -n "$REFUSAL" ]; then
  answer OK
  claim holds "identical sites are still refused - \"$REFUSAL\""
else
  sleep 3
  GOT=$("$B" readcell name=MolecularTypeTable "$NEW" 'BioNetGen Definition' \
      | python3 -c 'import json,sys; print(json.load(sys.stdin).get("value"))')
  case "$GOT" in
    *"s,s"*) claim stale "\"Molecules cannot have identical sites\"" "Twin(s,s) was accepted as $GOT" ;;
    *) claim holds "identical sites are refused - Twin(s,s) came back as $GOT" ;;
  esac
fi
wait "$SETCELL" 2>/dev/null || true

step "\"Deterministic, Stochastic and Network-Free\" applications"
OFFERED=$(curl -s "http://127.0.0.1:9123/menus" > /dev/null 2>&1; \
    "$B" rrow name=bioModelEditorTree "$(navrow 'Applications')" >/dev/null 2>&1; sleep 2; \
    curl -s "http://127.0.0.1:9123/find?text=New%20Application&limit=1" >/dev/null 2>&1; \
    curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
for root in json.load(sys.stdin):
    if "HeavyWeight" not in str(root.get("class")):
        continue
    def walk(n):
        if n.get("class", "").endswith("JMenu") and n.get("text") == "New Application":
            print(" | ".join(str(i.get("text")) for i in (n.get("menuItems") or [])))
            raise SystemExit
        for c in n.get("children") or []:
            walk(c)
    walk(root)
')
echo "  New Application offers: ${OFFERED:-none}" >&2
case "$OFFERED" in
  *Deterministic*Stochastic*Network*) claim holds "New Application offers Deterministic, Stochastic and Network-Free" ;;
  *) claim stale "\"New Application -> Deterministic, Stochastic, etc\"" "${OFFERED:-could not read the menu}" ;;
esac

step "Verdict"
printf '  %d claims still hold, %d have gone stale\n' "$HOLDS" "$STALE" >&2
