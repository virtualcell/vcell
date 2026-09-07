#!/usr/bin/env bash
#
# VCell Tutorial: FRAP with binding
#
# Reproduces the storyline of vcell.org/webstart/VCell_Tutorials/FRAPBinding_7.2.pdf
# against a current client. See storylines/frap-with-binding.md.
#
# The first of these tutorials with a REACTION NETWORK, which the PDF draws on the Reaction
# Diagram canvas. Three rules make that expressible in the table views instead:
#
#   1. Create every species BEFORE any reaction. An equation cannot place a species -
#      parseReaction reuses a name that already exists anywhere in the model, but creates an
#      unknown one in the reaction's own structure, silently.
#   2. Create each reaction in the compartment where its participants meet. Here every
#      species is in Nuc, so every reaction is too; a reaction spanning compartments would
#      belong on the membrane between them.
#   3. A catalyst is not written in the equation - it is implied by the kinetic law. A rate
#      expression referring to a species that is neither reactant nor product makes that
#      species a catalyst, so `Laser` becomes one by being named in the bleaching rates.
#
set -euo pipefail
. "$(cd "$(dirname "$0")" && pwd)/_common.sh"

dismiss OK
sleep 1

step "Physiology: three structures, Cyt | NM | Nuc"
must tab name=ModelTabbedPane "Structures" >/dev/null; sleep 1
must setcell name=StructuresTable 0 0 "Cyt" >/dev/null; sleep 1
must click name=ModelNewMembraneButton >/dev/null; sleep 1
must setcell name=StructuresTable 1 0 "NM" >/dev/null; sleep 1
must click name=ModelNewButton >/dev/null; sleep 1
must setcell name=StructuresTable 2 0 "Nuc" >/dev/null; sleep 1

step "Physiology: six species in Nuc"
# r = RAN, rf = RAN_FITC, rB = RAN_Bound, BS = binding sites,
# rfB = RAN_FITC_Bound, Laser = the light source that drives bleaching.
must tab name=ModelTabbedPane "Species" >/dev/null; sleep 1
i=0
for sp in r rf rB BS rfB Laser; do
  must click name=ModelNewButton >/dev/null; sleep 1
  must click "text=In Compartment Nuc" >/dev/null; sleep 2
  must setcell name=SpeciesTable "$i" 0 "$sp" >/dev/null; sleep 1
  i=$((i+1))
done

step "Physiology: four reactions, all in Nuc"
# The New Reaction dialog asks for all three defining properties at once - where the
# reaction occurs, its name, and its equation - so there is no half-built intermediate.
new_reaction() {   # $1 = name, $2 = equation
  must click name=ModelNewButton >/dev/null; sleep 2
  must combo name=ReactionStructureComboBox "Nuc" >/dev/null; sleep 1
  must settext name=ReactionNameTextField "$1" >/dev/null; sleep 1
  must settext name=ReactionEquationTextField "$2" >/dev/null; sleep 1
  must click "text=OK" >/dev/null; sleep 3
}
must tab name=ModelTabbedPane "Reactions" >/dev/null; sleep 2
new_reaction "RAN binding"      "BS + r -> rB"
new_reaction "RAN_FITC binding" "BS + rf -> rfB"
new_reaction "bleaching 1"      "rf -> r"
new_reaction "bleaching 2"      "rfB -> rB"

step "Kinetics: mass-action binding, and time-gated bleaching"
select_reaction() {
  must trow name=ReactionsTable "$(row name=ReactionsTable "$1" --in 'Name')" >/dev/null; sleep 2
}
set_param() {      # $1 = parameter name, $2 = expression
  local exp; exp=$(col name=ReactionKineticsParametersTable "Expression")
  must setcell name=ReactionKineticsParametersTable \
      "$(row name=ReactionKineticsParametersTable "$1" --in 'Name')" "$exp" "$2" >/dev/null; sleep 2
}

for rxn in "RAN binding" "RAN_FITC binding"; do
  select_reaction "$rxn"
  set_param Kf ".02"
  set_param Kr ".1"
done

# The bleaching reactions only run while the laser is on, from t=1.0 to t=1.5: the Boolean
# evaluates to 1 inside that window and 0 outside, so the rate is zero at all other times.
# Naming `Laser` here is also what makes it a catalyst - it is neither reactant nor product.
select_reaction "bleaching 1"
must combo name=ReactionKineticsTypeComboBox "General" >/dev/null; sleep 2
set_param J    "(Vmax*rf*Laser*((t>1.0)&&(t<1.5)))"
set_param Vmax "50"

select_reaction "bleaching 2"
must combo name=ReactionKineticsTypeComboBox "General" >/dev/null; sleep 2
set_param J     "(Vmax2*rfB*Laser*((t>1.0)&&(t<1.5)))"
set_param Vmax2 "50"

step "Application: compartmental (ODE), for the steady state"
tree_pick 'Applications' 'New Application'; sleep 1
menu_pick 'Deterministic'; sleep 3
tree_pick 'Application0' 'Rename'; sleep 1
must settext "text=Application0" "Compartmental" --enter >/dev/null; sleep 2
navselect 'Compartmental'; sleep 2

# A compartmental application has no geometry, so Structure Mapping asks only for sizes.
# These are the volumes of a 10 um cell and its nucleus, and the area between them.
step "Sizes: Cyt 523.33, Nuc 26.1665, NM 130.8325"
SZ=$(col name=StructureMappingTable "Size")
must setcell name=StructureMappingTable "$(row name=StructureMappingTable 'Cyt')" "$SZ" "523.33"   >/dev/null; sleep 2
must setcell name=StructureMappingTable "$(row name=StructureMappingTable 'Nuc')" "$SZ" "26.1665"  >/dev/null; sleep 2
must setcell name=StructureMappingTable "$(row name=StructureMappingTable 'NM')"  "$SZ" "130.8325" >/dev/null; sleep 2

step "Initial conditions: r 5.0, rf 5.0, BS 20.0"
must tab name=ApplicationTabbedPane "Specifications" >/dev/null; sleep 3
SPEC=name=spceciesContextSpecsTable
IC=$(col "$SPEC" "Initial Condition")
for pair in "r:5.0" "rf:5.0" "BS:20.0"; do
  must setcell "$SPEC" "$(row "$SPEC" "${pair%%:*}")" "$IC" "${pair##*:}" >/dev/null; sleep 2
done

step "Simulation: run to 30 s"
must tab name=ApplicationTabbedPane "Simulations" >/dev/null; sleep 3
must click name=NewButton >/dev/null; sleep 3
must trow name=SimulationsTable 0 >/dev/null; sleep 1
must click name=EditButton >/dev/null; sleep 3
# A non-spatial simulation has no Mesh tab - the dialog here reads [Parameters, Solver],
# where a spatial one reads [Parameters, Mesh, Solver]. Naming the tab rather than
# indexing it is what makes the same idiom work for both.
must tab name=JTabbedPane1 "Solver" >/dev/null; sleep 2
must settext name=EndingTimeTextField "30.0" --enter >/dev/null; sleep 2
# The dialog must CLOSE for the ending time to take effect.
dialog_button "Edit:" OK; sleep 2

step "Run it locally, and read the steady state off the results"
# The PDF runs this on the VCell servers, then copies the final concentrations out of the
# results spreadsheet by hand. Quick Run does the same arithmetic here with the bundled
# SundialsSolverStandalone and saves nothing, and readCell fetches the last row - which
# is what the tutorial means by "the steady state".
quick_run

# Which species are selected in the results window decides which columns the data table
# has, so select the four the tutorial carries forward before reading any of them.
must list name=YAxisChoice "BS,r,rB,rf,rfB" >/dev/null; sleep 3

STEADY_BS=$(result_cell -1 BS)
STEADY_r=$(result_cell -1 r)
STEADY_rB=$(result_cell -1 rB)
STEADY_rf=$(result_cell -1 rf)
STEADY_rfB=$(result_cell -1 rfB)
for pair in "BS:$STEADY_BS" "r:$STEADY_r" "rB:$STEADY_rB" "rf:$STEADY_rf" "rfB:$STEADY_rfB"; do
  name=${pair%%:*}; value=${pair#*:}
  printf '  %-4s %s\n' "$name" "$value" >&2
  if [ -z "$value" ] || [ "$value" = "None" ]; then
    echo "FATAL: no steady-state value read for $name" >&2
    exit 1
  fi
done

step "Spatial application: a copy of the compartmental one, given a geometry"
navselect 'Compartmental'; sleep 1
tree_pick 'Compartmental' 'Copy'; sleep 3
tree_pick 'Copy of Compartmental' 'Rename'; sleep 1
must settext "text=Copy of Compartmental" "Spatial" --enter >/dev/null; sleep 2
must expand name=bioModelEditorTree "$(navrow 'Spatial')" true >/dev/null; sleep 2

# Copy brings the compartmental application's SIMULATION along, and it keeps its ODE
# solver - which cannot run a spatial application ("Combined Stiff Solver (IDA/CVODE)
# does not support ... Spatial"), and whose geometry no longer matches once one is added.
# Delete it now; a simulation created later, against the finished spatial application,
# gets an appropriate solver by default.
navselect 'Simulations'; sleep 2
while "$B" findrow name=SimulationsTable "Simulation" 2>/dev/null | grep -q '"row": 0'; do
  must trow name=SimulationsTable 0 >/dev/null; sleep 1
  must click name=DeleteButton >/dev/null; sleep 2
  dismiss Yes; dismiss OK; sleep 1
done

navselect 'Geometry'; sleep 2

step "Geometry: a nucleus of radius 10 inside a 22 x 22 um cytosol"
must tab name=ApplicationGeometryPanelTabbedPane "Geometry Definition" >/dev/null; sleep 2
must click "text=Add Geometry" >/dev/null; sleep 1
menu_pick 'New...'; sleep 2
must trow "type=JSortTable" "$(row 'type=JSortTable' 'Analytic Equations (2D)')" >/dev/null; sleep 1
must click "text=OK" >/dev/null; sleep 3
must setcell name=SubVolumesTable 0 0 "Cyt" >/dev/null; sleep 2
dismiss OK; sleep 1
must click "text=Add Subdomain" >/dev/null; sleep 1
menu_pick 'Analytic ...'; sleep 2
must combo name=subdomainShapeComboBox "Circle" >/dev/null; sleep 1
must settext name=circleCenterTextField "0,0" >/dev/null; sleep 1
must settext name=circleRadiusTextField "10" >/dev/null; sleep 1
must click "text=New Subdomain" >/dev/null; sleep 3
must setcell name=SubVolumesTable "$(row name=SubVolumesTable 'subdomain0')" 0 "Nuc" >/dev/null; sleep 2
dismiss OK; sleep 1
must click "text=Edit Domain..." >/dev/null; sleep 2
must settext name=SizeXTextField   "22"  >/dev/null; sleep 1
must settext name=SizeYTextField   "22"  >/dev/null; sleep 1
must settext name=OriginXTextField "-11" >/dev/null; sleep 1
must settext name=OriginYTextField "-11" >/dev/null; sleep 1
must click "text=OK" >/dev/null; sleep 3

step "Structure mapping: Cyt, Nuc, and the NM membrane between them"
must tab name=ApplicationGeometryPanelTabbedPane "Structure Mapping" >/dev/null; sleep 3
SUB=$(col name=StructureMappingTable "Subdomain")
must setcell name=StructureMappingTable "$(row name=StructureMappingTable 'Cyt')" "$SUB" "Cyt" >/dev/null; sleep 2
must setcell name=StructureMappingTable "$(row name=StructureMappingTable 'Nuc')" "$SUB" "Nuc" >/dev/null; sleep 3
# SurfaceClass.createName(): the two subvolume names sorted alphabetically. Cyt < Nuc.
must setcell name=StructureMappingTable "$(row name=StructureMappingTable 'NM')" "$SUB" "Cyt_Nuc_membrane" >/dev/null; sleep 3

step "Initial conditions: the steady state carried across, and the laser square"
must tab name=ApplicationTabbedPane "Specifications" >/dev/null; sleep 3
SPEC=name=spceciesContextSpecsTable
IC=$(col "$SPEC" "Initial Condition")
# The PDF copies four cells out of the results spreadsheet and pastes them into the
# column by position. Setting each species from its own steady state says the same thing
# without depending on the row order of either table.
for pair in "BS:$STEADY_BS" "r:$STEADY_r" "rB:$STEADY_rB" "rf:$STEADY_rf" "rfB:$STEADY_rfB"; do
  must setcell "$SPEC" "$(row "$SPEC" "${pair%%:*}")" "$IC" "${pair##*:}" >/dev/null; sleep 2
done
# The laser is on only inside a 4x4 um square at the origin, so bleaching is confined to
# it - the spatial counterpart of the time gate already in the reaction rates.
must setcell "$SPEC" "$(row "$SPEC" Laser)" "$IC" \
    '((x>-2.0)&&(x<2.0)&&(y>-2.0)&&(y<2.0))' >/dev/null; sleep 2

step "Refresh the math, so the application and its simulations agree"
# Adding a geometry to a copied compartmental application leaves the generated math
# stale, and VCell then refuses to open the Edit Simulation dialog at all:
# "Application geometry does not match Simulation geometry - Update Math before editing".
# Refreshing also settles the SOLVER: a simulation created against stale math keeps the
# copied application's ODE solver (Combined IDA/CVODE), which cannot run a spatial
# application; created after a refresh it gets Fully-Implicit, as it should.
must tab name=ApplicationTabbedPane "Simulations" >/dev/null; sleep 3
must tab name=ApplicationSimulationsPanelTabbedPane "Generated Math" >/dev/null; sleep 2
must click name=RefreshMathButton >/dev/null; sleep 6
must tab name=ApplicationSimulationsPanelTabbedPane "Simulations" >/dev/null; sleep 2

step "Simulation: mesh 51, run to 50 s, output every 0.5 s"
must click name=NewButton >/dev/null; sleep 3
must trow name=SimulationsTable 0 >/dev/null; sleep 1
must click name=EditButton >/dev/null; sleep 3
dialog_tab "Edit:" "Mesh"; sleep 2
must settext name=XTextField "51" --enter >/dev/null; sleep 2
dialog_tab "Edit:" "Solver"; sleep 2
must settext name=EndingTimeTextField     "50.0" --enter >/dev/null; sleep 1
must settext name=OutputTimeStepTextField "0.5"  --enter >/dev/null; sleep 1
dialog_button "Edit:" OK; sleep 2

step "Done -- both applications built, the compartmental one run, its steady state carried across."
# The PDF also runs the spatial simulation. Not done here: it is a 51x51 PDE over 50 s,
# which is minutes of local compute for a result nothing downstream reads.
