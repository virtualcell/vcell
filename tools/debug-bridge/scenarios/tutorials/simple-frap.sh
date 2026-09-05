#!/usr/bin/env bash
#
# VCell Tutorial: FRAP (Fluorescence Redistribution After Photobleaching)
#
# Reproduces the storyline of vcell.org/webstart/VCell_Tutorials/SimpleFRAP_7.2.pdf
# against a current client, driven entirely through the debug bridge.
#
# The PDF drives the Reaction Diagram canvas -- "select the compartment tool, hover on
# the dotted lines until they turn green". Those are pixel gestures on a custom-painted
# canvas, which the recorder deliberately never captures. Every one of them has an exact
# equivalent in the table views (the Quickstart guide says so outright: structures and
# species "can be specified and edited in both Structure and Reaction Diagram views"),
# and that is the route taken here, because it is the one that survives a relayout.
#
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
B="$HERE/../../bridge.sh"

step() { printf '\n=== %s\n' "$*" >&2; }
navrow() { row "name=bioModelEditorTree" "$1"; }

# Resolve a row by its DISPLAYED text, and stop if it is not there. A silent -1 here is
# how a mapping step turns into a no-op that is only noticed pages later, when the
# simulation quietly uses defaults.
row() {
  local r
  r=$("$B" findrow "$1" "$2" | python3 -c 'import json,sys;print(json.load(sys.stdin)["row"])')
  if [ "$r" -lt 0 ]; then
    echo "FATAL: no row reading '$2' in $1" >&2
    exit 1
  fi
  printf '%s' "$r"
}

# The version-mismatch warning is modal and appears a moment AFTER the menus do, so it
# can land in the middle of the run. Dismiss it up front; harmless when it is absent.
"$B" click "text=OK" >/dev/null 2>&1 || true
sleep 1

step "Physiology: three structures, EC | PM | Cyt"
"$B" tab name=ModelTabbedPane "Structures" >/dev/null; sleep 1
"$B" setcell name=StructuresTable 0 0 "EC" >/dev/null; sleep 1
"$B" click name=ModelNewMembraneButton >/dev/null; sleep 1
"$B" setcell name=StructuresTable 1 0 "PM" >/dev/null; sleep 1
"$B" click name=ModelNewButton >/dev/null; sleep 1        # "New Compartment" on this tab
"$B" setcell name=StructuresTable 2 0 "Cyt" >/dev/null; sleep 1

step "Physiology: one species, Dex, in Cyt"
"$B" tab name=ModelTabbedPane "Species" >/dev/null; sleep 1
# With more than one structure the table drops its "(add new here)" row and New Species
# asks which compartment -- which is the tutorial's "click a point within Cyt", named.
"$B" click name=ModelNewButton >/dev/null; sleep 1        # "New Species" on this tab
"$B" click "text=In Compartment Cyt" >/dev/null; sleep 2
"$B" setcell name=SpeciesTable 0 0 "Dex" >/dev/null; sleep 1

step "Application: new Deterministic application, renamed FRAP"
"$B" rrow name=bioModelEditorTree "$(navrow 'Applications')" >/dev/null; sleep 1
"$B" click "text=New Application" >/dev/null; sleep 1
"$B" click "text=Deterministic" >/dev/null; sleep 3
"$B" rrow name=bioModelEditorTree "$(navrow 'Application0')" >/dev/null; sleep 1
"$B" click "text=Rename" >/dev/null; sleep 1
"$B" settext "text=Application0" "FRAP" --enter >/dev/null; sleep 2

step "Geometry: analytic 2D, a circle of radius 10 in a 22x22 um square"
"$B" expand name=bioModelEditorTree "$(navrow 'FRAP')" true >/dev/null; sleep 2
"$B" trow name=bioModelEditorTree "$(navrow 'Geometry')" >/dev/null; sleep 2
"$B" tab name=ApplicationGeometryPanelTabbedPane "Geometry Definition" >/dev/null; sleep 2
"$B" click "text=Add Geometry" >/dev/null; sleep 1
"$B" click "text=New..." >/dev/null; sleep 2
"$B" trow "type=JSortTable" "$(row 'type=JSortTable' 'Analytic Equations (2D)')" >/dev/null; sleep 1
"$B" click "text=OK" >/dev/null; sleep 3

# The outer subdomain is the extracellular space.
"$B" setcell name=SubVolumesTable 0 0 "EC" >/dev/null; sleep 2
# Renaming a subdomain re-generates the math, and the structures are not mapped yet, so
# VCell raises "structure Cyt not mapped". The tutorial passes through the same invalid
# intermediate state; it is only an error because the check runs eagerly.
"$B" click "text=OK" >/dev/null 2>&1 || true; sleep 1

step "Geometry: inner subdomain Cyt, a circle of radius 10 at the origin"
"$B" click "text=Add Subdomain" >/dev/null; sleep 1
"$B" click "text=Analytic ..." >/dev/null; sleep 2
"$B" combo "type=JComboBox" "Circle" >/dev/null; sleep 1
"$B" settext name=circleCenterTextField "0,0" >/dev/null; sleep 1
"$B" settext name=circleRadiusTextField "10" >/dev/null; sleep 1
"$B" click "text=New Subdomain" >/dev/null; sleep 3
"$B" setcell name=SubVolumesTable "$(row name=SubVolumesTable 'subdomain0')" 0 "Cyt" >/dev/null; sleep 2
"$B" click "text=OK" >/dev/null 2>&1 || true; sleep 1

step "Geometry: computational domain 22 x 22 um centred on the origin"
"$B" click "text=Edit Domain..." >/dev/null; sleep 2
"$B" settext name=SizeXTextField   "22"  >/dev/null; sleep 1
"$B" settext name=SizeYTextField   "22"  >/dev/null; sleep 1
"$B" settext name=OriginXTextField "-11" >/dev/null; sleep 1
"$B" settext name=OriginYTextField "-11" >/dev/null; sleep 1
"$B" click "text=OK" >/dev/null; sleep 3

step "Structure mapping: EC, Cyt AND the PM membrane between them"
# The PDF drags a line from each structure to its subdomain. The Structure Mapping TABLE
# states the same relation as a value in the "Subdomain" column, which is addressable.
"$B" tab name=ApplicationGeometryPanelTabbedPane "Structure Mapping" >/dev/null; sleep 3
SUB=$("$B" findcol name=StructureMappingTable "Subdomain" | python3 -c 'import json,sys;print(json.load(sys.stdin)["column"])')
"$B" setcell name=StructureMappingTable "$(row name=StructureMappingTable 'EC')"  "$SUB" "EC"  >/dev/null; sleep 2
"$B" setcell name=StructureMappingTable "$(row name=StructureMappingTable 'Cyt')" "$SUB" "Cyt" >/dev/null; sleep 3

# The membrane needs mapping too -- it does NOT fall out of mapping the two volumes it
# separates, and an unmapped PM reads "Unmapped" in the table while the model still
# reports zero errors, so nothing complains until the results are wrong. Its geometry
# class is the surface between the two subvolumes, named by SurfaceClass.createName():
# the two subvolume names sorted alphabetically, joined with "_membrane". Cyt < EC.
"$B" setcell name=StructureMappingTable "$(row name=StructureMappingTable 'PM')" "$SUB" "Cyt_EC_membrane" >/dev/null; sleep 3

# Nothing downstream is meaningful until this is resolved: the species Specifications
# tab only offers a diffusion constant for a structure that has somewhere to diffuse in.
python3 - "$SUB" <<'PY' || { echo "FATAL: structure mapping did not resolve" >&2; exit 1; }
import json, sys, urllib.request
sub = int(sys.argv[1])
tree = json.load(urllib.request.urlopen("http://127.0.0.1:9123/tree", timeout=30))
def walk(n):
    yield n
    for c in n.get("children") or []:
        yield from walk(c)
for root in tree:
    for n in walk(root):
        if n.get("name") == "StructureMappingTable" and n.get("showing"):
            mapped = {r[0]: r[sub] for r in n["table"]["cells"]}
            print("  mapping:", mapped, file=sys.stderr)
            want = {"EC": "EC", "Cyt": "Cyt", "PM": "Cyt_EC_membrane"}
            sys.exit(0 if all(mapped.get(k) == v for k, v in want.items()) else 1)
sys.exit(1)
PY

step "Specifications: the bleached square, and Dex diffusion"
"$B" tab name=ApplicationTabbedPane "Specifications" >/dev/null; sleep 3
SPEC=name=spceciesContextSpecsTable
IC=$("$B" findcol "$SPEC" "Initial Condition"  | python3 -c 'import json,sys;print(json.load(sys.stdin)["column"])')
DC=$("$B" findcol "$SPEC" "Diffusion Constant" | python3 -c 'import json,sys;print(json.load(sys.stdin)["column"])')
# 10 uM everywhere except the square from -5..5 in x and y, which starts bleached to 0.
"$B" setcell "$SPEC" 0 "$IC" '(10.0*((x<-5.0)||(x>5.0)||(y<-5.0)||(y>5.0)))' >/dev/null; sleep 2
"$B" setcell "$SPEC" 0 "$DC" '20' >/dev/null; sleep 2

step "Simulation: mesh 51, run to 3 s, max step 0.01, output every 0.05 s"
"$B" tab name=ApplicationTabbedPane "Simulations" >/dev/null; sleep 3
"$B" click name=NewButton >/dev/null; sleep 3
"$B" setcell name=SimulationsTable 0 0 "FRAP" >/dev/null; sleep 2
"$B" trow name=SimulationsTable 0 >/dev/null; sleep 1
"$B" click name=EditButton >/dev/null; sleep 3
"$B" tab name=JTabbedPane1 "Mesh" >/dev/null; sleep 2
"$B" settext name=XTextField "51" --enter >/dev/null; sleep 2 # Y follows: aspect locked
"$B" tab name=JTabbedPane1 "Solver" >/dev/null; sleep 2
"$B" settext name=EndingTimeTextField     "3.0"  --enter >/dev/null; sleep 1
"$B" settext name=MaximumTimeStepTextField "0.01" --enter >/dev/null; sleep 1
"$B" settext name=OutputTimeStepTextField  "0.05" --enter >/dev/null; sleep 1
"$B" click "text=OK" >/dev/null; sleep 3

step "Done -- model built. The PDF now saves and runs it on the VCell servers."
# Deliberately NOT scripted: File > Save needs a logged-in account, and the green Run
# button dispatches a real job to shared compute. The model is complete and valid at
# this point; running it is a decision for whoever is at the keyboard.
