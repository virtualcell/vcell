#!/usr/bin/env bash
#
# VCell Tutorial: PH-GFP binding to PIP2 and IP3
#
# Reproduces the storyline of vcell.org/webstart/VCell_Tutorials/PHGFP_7.2.pdf.
# See storylines/phgfp.md.
#
# The most involved of these tutorials, and the one where the compartment rules earn
# their keep: species live on a MEMBRANE as well as in volumes, and one reaction spans
# both, so it belongs on the interface between them rather than in either.
#
set -euo pipefail
. "$(cd "$(dirname "$0")" && pwd)/_common.sh"

dismiss OK
sleep 1

step "Physiology: five structures, EC | PM | Cyt | NM | Nuc"
must tab name=ModelTabbedPane "Structures" >/dev/null; sleep 1
must setcell name=StructuresTable 0 0 "EC" >/dev/null; sleep 1
must click name=ModelNewMembraneButton >/dev/null; sleep 1
must setcell name=StructuresTable 1 0 "PM" >/dev/null; sleep 1
must click name=ModelNewButton >/dev/null; sleep 1
must setcell name=StructuresTable 2 0 "Cyt" >/dev/null; sleep 1
must click name=ModelNewMembraneButton >/dev/null; sleep 1
must setcell name=StructuresTable 3 0 "NM" >/dev/null; sleep 1
must click name=ModelNewButton >/dev/null; sleep 1
must setcell name=StructuresTable 4 0 "Nuc" >/dev/null; sleep 1

step "Physiology: two species on the membrane, four in the cytosol"
must tab name=ModelTabbedPane "Species" >/dev/null; sleep 1
new_species() {   # $1 = compartment menu label, $2 = row index, $3 = name
  must click name=ModelNewButton >/dev/null; sleep 1
  menu_pick "$1"; sleep 2
  must setcell name=SpeciesTable "$2" 0 "$3" >/dev/null; sleep 1
}
new_species 'In Membrane PM'    0 PIP2_PM
new_species 'In Membrane PM'    1 PIP2_PHGFP_PM
new_species 'In Compartment Cyt' 2 IP3_Cyt
new_species 'In Compartment Cyt' 3 IP3_PHGFP_Cyt
new_species 'In Compartment Cyt' 4 PH_GFP_Cyt
new_species 'In Compartment Cyt' 5 Stim

step "Physiology: three reactions, each where its participants meet"
new_reaction() {   # $1 = structure, $2 = name, $3 = equation
  must click name=ModelNewButton >/dev/null; sleep 2
  must combo name=ReactionStructureComboBox "$1" >/dev/null; sleep 1
  must settext name=ReactionNameTextField "$2" >/dev/null; sleep 1
  must settext name=ReactionEquationTextField "$3" >/dev/null; sleep 1
  must click "text=OK" >/dev/null; sleep 3
}
must tab name=ModelTabbedPane "Reactions" >/dev/null; sleep 2
# PIP2_PM is on the membrane and PH_GFP_Cyt in the cytosol, so this reaction spans two
# compartments and belongs on the interface between them - the membrane.
new_reaction PM  "PIP2_PH" "PIP2_PM + PH_GFP_Cyt -> PIP2_PHGFP_PM"
# Both of these have all their participants in Cyt, so they simply go there.
new_reaction Cyt "IP3PH"   "IP3_Cyt + PH_GFP_Cyt -> IP3_PHGFP_Cyt"
new_reaction Cyt "IP3synth" " -> IP3_Cyt"

step "Kinetics"
select_reaction() {
  must trow name=ReactionsTable "$(row name=ReactionsTable "$1" --in 'Name')" >/dev/null; sleep 2
}
set_param() {      # $1 = parameter name, $2 = expression
  local exp; exp=$(col name=ReactionKineticsParametersTable "Expression")
  must setcell name=ReactionKineticsParametersTable \
      "$(row name=ReactionKineticsParametersTable "$1" --in 'Name')" "$exp" "$2" >/dev/null; sleep 2
}

# Kr is expressed in terms of Kf and a dissociation constant, so naming KdPIP2PH in the
# reverse rate is what BRINGS THAT PARAMETER INTO EXISTENCE - it has to be set second.
select_reaction "PIP2_PH"
set_param Kf "0.12"
set_param Kr "(Kf*KdPIP2PH)"
set_param KdPIP2PH "2.0"

select_reaction "IP3PH"
set_param Kf "10"
set_param Kr "(Kf*KdIP3PH)"
set_param KdIP3PH "0.1"

# Stim is neither reactant nor product; naming it in the rate is what makes it a catalyst.
select_reaction "IP3synth"
must combo name=ReactionKineticsTypeComboBox "General" >/dev/null; sleep 2
set_param J "Ksynth*Stim"
set_param Ksynth "1.0"

step "Steady-state application: compartmental, to find the resting levels"
tree_pick 'Applications' 'New Application'; sleep 1
menu_pick 'Deterministic'; sleep 3
tree_pick 'Application0' 'Rename'; sleep 1
must settext "text=Application0" "Steady State" --enter >/dev/null; sleep 2
must expand name=bioModelEditorTree "$(navrow 'Steady State')" true >/dev/null; sleep 2
navselect 'Geometry'; sleep 2

step "Sizes: a 10 um cell with a nucleus, and the areas between"
SZ=$(col name=StructureMappingTable "Size")
for pair in "Nuc:33.389" "Cyt:489.794" "NM:49.8" "EC:476.817" "PM:501.804"; do
  must setcell name=StructureMappingTable "$(row name=StructureMappingTable "${pair%%:*}")" \
      "$SZ" "${pair##*:}" >/dev/null; sleep 2
done

step "Initial conditions: resting IP3 and PH-GFP, and a membrane full of PIP2"
must tab name=ApplicationTabbedPane "Specifications" >/dev/null; sleep 3
SPEC=name=spceciesContextSpecsTable
IC=$(col "$SPEC" "Initial Condition")
for pair in "IP3_Cyt:0.1" "PH_GFP_Cyt:1.0" "PIP2_PM:120000"; do
  must setcell "$SPEC" "$(row "$SPEC" "${pair%%:*}")" "$IC" "${pair##*:}" >/dev/null; sleep 2
done

step "Events: the stimulus on at 5 s, off at 6 s"
# A compartmental application can carry timed events; this is how the tutorial turns the
# stimulus on and off without writing it into a rate law.
must tab name=ApplicationTabbedPane "Protocols" >/dev/null; sleep 3
must tab name=ApplicationProtocolsPanelTabbedPane "Events" >/dev/null; sleep 2
new_event() {   # $1 = time, $2 = name, $3 = value for Stim
  must click name=EventNewButton >/dev/null; sleep 3
  must settext name=EventSingleTimeTextField "$1" >/dev/null; sleep 1
  must click "text=OK" >/dev/null; sleep 3
  must setcell name=EventsTable "$(row name=EventsTable 'event0')" 0 "$2" >/dev/null; sleep 2
  must trow name=EventsTable "$(row name=EventsTable "$2")" >/dev/null; sleep 2
  # "Add Action" opens a dialog with a variable chooser, not a menu.
  must click name=AddEventAssgnButton >/dev/null; sleep 2
  must combo name=VariableNameComboBox "Stim" >/dev/null; sleep 1
  must click "text=OK" >/dev/null; sleep 3
  # The action defaults to setting Stim to 0.0; say what it should actually become.
  must setcell name=EventActionsTable "$(row name=EventActionsTable 'Stim')" \
      "$(col name=EventActionsTable 'Expression to evaluate at trigger time')" "$3" >/dev/null
  sleep 2
}
new_event "5.0" "Activation"   "1.0"
new_event "6.0" "Inactivation" "0.0"

step "Simulation: IDA to 30 s, so the stimulus at 5 s has somewhere to happen"
must tab name=ApplicationTabbedPane "Simulations" >/dev/null; sleep 3
must click name=NewButton >/dev/null; sleep 3
must trow name=SimulationsTable 0 >/dev/null; sleep 1
must click name=EditButton >/dev/null; sleep 3
must tab name=JTabbedPane1 "Solver" >/dev/null; sleep 2
# The default is the "Combined Stiff Solver (IDA/CVODE)", which is not the same choice:
# the tutorial asks for IDA itself, the DAE-capable integrator.
must combo name=SolverComboBox "IDA (Variable Order, Variable Time Step, ODE/DAE)" >/dev/null; sleep 2
must settext name=EndingTimeTextField      "30.0" --enter >/dev/null; sleep 1
must settext name=MaximumTimeStepTextField "0.01" --enter >/dev/null; sleep 1
must settext name=KeepEveryTextField       "10"   --enter >/dev/null; sleep 1
dialog_button "Edit:" OK; sleep 2

step "Run it locally, and read the resting state the moment before the stimulus"
quick_run
# Which species are selected decides which columns the data table has, so choose the five
# the tutorial carries across before reading any of them. Stim is deliberately not among
# them - the spatial application drives it from an expression instead.
must list name=YAxisChoice "IP3_Cyt,IP3_PHGFP_Cyt,PH_GFP_Cyt,PIP2_PHGFP_PM,PIP2_PM" >/dev/null; sleep 3

# NOT the last row: at t = 30 the stimulus has been and gone and IP3 is still elevated.
# What the spatial application wants is the resting state the model settles into before
# anything happens, which the tutorial reads at t = 5 - the instant the stimulus fires.
REST_ROW=$(result_row_at_time 5.0)
REST_IP3=$(result_cell "$REST_ROW" IP3_Cyt)
REST_IP3PH=$(result_cell "$REST_ROW" IP3_PHGFP_Cyt)
REST_PH=$(result_cell "$REST_ROW" PH_GFP_Cyt)
REST_PIP2PH=$(result_cell "$REST_ROW" PIP2_PHGFP_PM)
REST_PIP2=$(result_cell "$REST_ROW" PIP2_PM)
for pair in "IP3_Cyt:$REST_IP3" "IP3_PHGFP_Cyt:$REST_IP3PH" "PH_GFP_Cyt:$REST_PH" \
            "PIP2_PHGFP_PM:$REST_PIP2PH" "PIP2_PM:$REST_PIP2"; do
  printf '  %-16s %s\n' "${pair%%:*}" "${pair#*:}" >&2
  if [ -z "${pair#*:}" ] || [ "${pair#*:}" = "None" ]; then
    echo "FATAL: no resting value read for ${pair%%:*}" >&2
    exit 1
  fi
done

step "Spatial application: a sphere of cytosol with a nucleus off centre"
# Built fresh rather than copied from the compartmental one. A copy would bring the two
# events along, and they set Stim - which the spatial application drives from a clamped
# expression instead. The two ways of saying the same thing do not belong in one model.
tree_pick 'Applications' 'New Application'; sleep 1
menu_pick 'Deterministic'; sleep 4
tree_pick 'Application0' 'Rename'; sleep 1
must settext "text=Application0" "Spatial" --enter >/dev/null; sleep 2
must expand name=bioModelEditorTree "$(navrow 'Spatial')" true >/dev/null; sleep 2
navselect 'Geometry'; sleep 2

must tab name=ApplicationGeometryPanelTabbedPane "Geometry Definition" >/dev/null; sleep 2
must click "text=Add Geometry" >/dev/null; sleep 1
menu_pick 'New...'; sleep 2
must trow "type=JSortTable" "$(row 'type=JSortTable' 'Analytic Equations (3D)')" >/dev/null; sleep 1
must click "text=OK" >/dev/null; sleep 4
must setcell name=SubVolumesTable 0 0 "EC" >/dev/null; sleep 2
dismiss OK; sleep 1

# The cell. A 3D analytic geometry starts as a 10 um cube, so the shape panel already
# offers its centre - the tutorial types only the radius, and so does this.
must click "text=Add Subdomain" >/dev/null; sleep 1
menu_pick 'Analytic ...'; sleep 2
must combo name=subdomainShapeComboBox "Sphere" >/dev/null; sleep 1
must settext name=circleRadiusTextField "5.0" >/dev/null; sleep 1
must click "text=New Subdomain" >/dev/null; sleep 4
must setcell name=SubVolumesTable "$(row name=SubVolumesTable 'subdomain0')" 0 "Cyt" >/dev/null; sleep 2
dismiss OK; sleep 1

# The nucleus, deliberately off centre so the slice views show it moving through.
must click "text=Add Subdomain" >/dev/null; sleep 1
menu_pick 'Analytic ...'; sleep 2
must combo name=subdomainShapeComboBox "Sphere" >/dev/null; sleep 1
must settext name=circleCenterTextField "3.5,3.5,3.5" >/dev/null; sleep 1
must settext name=circleRadiusTextField "2.0" >/dev/null; sleep 1
must click "text=New Subdomain" >/dev/null; sleep 4
must setcell name=SubVolumesTable "$(row name=SubVolumesTable 'subdomain0')" 0 "Nuc" >/dev/null; sleep 2
dismiss OK; sleep 1

step "Structure mapping: three volumes and the two membranes between them"
# The PDF draws these with a line tool on a diagram. The same statement, made in the table
# the diagram edits. Membrane names are SurfaceClass.createName(): the two subvolume names
# sorted alphabetically - Cyt < EC, and Cyt < Nuc.
must tab name=ApplicationGeometryPanelTabbedPane "Structure Mapping" >/dev/null; sleep 3
SUB=$(col name=StructureMappingTable "Subdomain")
for pair in "EC:EC" "Cyt:Cyt" "Nuc:Nuc" "PM:Cyt_EC_membrane" "NM:Cyt_Nuc_membrane"; do
  must setcell name=StructureMappingTable "$(row name=StructureMappingTable "${pair%%:*}")" \
      "$SUB" "${pair##*:}" >/dev/null; sleep 3
done

step "Initial conditions: the resting state carried across, and Stim as a time window"
must tab name=ApplicationTabbedPane "Specifications" >/dev/null; sleep 3
SPEC=name=spceciesContextSpecsTable
IC=$(col "$SPEC" "Initial Condition")
# The PDF copies five cells out of the results spreadsheet and pastes them into the column
# by position. Setting each species from its own resting value says the same thing without
# depending on the row order of either table.
for pair in "IP3_Cyt:$REST_IP3" "IP3_PHGFP_Cyt:$REST_IP3PH" "PH_GFP_Cyt:$REST_PH" \
            "PIP2_PHGFP_PM:$REST_PIP2PH" "PIP2_PM:$REST_PIP2"; do
  must setcell "$SPEC" "$(row "$SPEC" "${pair%%:*}")" "$IC" "${pair#*:}" >/dev/null; sleep 2
done
# Clamped first: a species VCell still solves for cannot have an initial condition that
# depends on t. Clamping it turns the row from a state variable into a prescribed
# function of time, which is what makes the window expression legal - and what replaces
# the compartmental application's two events.
must setcell "$SPEC" "$(row "$SPEC" Stim)" "$(col "$SPEC" "Clamped")" true >/dev/null; sleep 2
must setcell "$SPEC" "$(row "$SPEC" Stim)" "$IC" '((t>5)&&(t<6))' >/dev/null; sleep 2

step "Simulation: mesh 31, run to 20 s, output every 0.2 s"
must tab name=ApplicationTabbedPane "Simulations" >/dev/null; sleep 3
must click name=NewButton >/dev/null; sleep 3
must trow name=SimulationsTable 0 >/dev/null; sleep 1
must click name=EditButton >/dev/null; sleep 4
dialog_tab "Edit:" "Mesh"; sleep 2
must settext name=XTextField "31" --enter >/dev/null; sleep 2
dialog_tab "Edit:" "Solver"; sleep 2
must settext name=EndingTimeTextField     "20.0" --enter >/dev/null; sleep 1
must settext name=OutputTimeStepTextField "0.2"  --enter >/dev/null; sleep 1
dialog_button "Edit:" OK; sleep 3

step "Output function: the fluorescence the experiment actually sees"
# The microscope cannot tell free PH-GFP from PH-GFP bound to IP3 - both are in the
# cytosol and both fluoresce. A function says that once, so the results window plots the
# measurable quantity rather than two variables the experiment cannot separate.
must tab name=ApplicationSimulationsPanelTabbedPane "Output Functions" >/dev/null; sleep 3
must click name=AddFnButton >/dev/null; sleep 3
must settext name=FunctionNameTextField       "Fluorescence" >/dev/null; sleep 1
must settext name=FunctionExpressionTextField "IP3_PHGFP_Cyt+PH_GFP_Cyt" >/dev/null; sleep 1
must click name=NextButton >/dev/null; sleep 3
# The wizard's second page asks which domain the function lives in. Both cytosolic
# species are in Cyt, so it offers Cyt or the membrane; the tutorial wants Cyt.
must combo name=FunctionDomainComboBox "Cyt" >/dev/null; sleep 1
must click name=FinishButton >/dev/null; sleep 3

step "Done -- both applications built, the compartmental one run, its resting state carried across."
# The PDF also runs the spatial simulation. Not done here: it is a 31x31x31 PDE over 20 s,
# which is minutes of local compute for a result nothing downstream reads.
