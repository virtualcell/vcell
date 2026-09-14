#!/usr/bin/env bash
#
# VCell Tutorial: one physiology, several applications - RAN nuclear transport
#
# Reproduces the storyline of vcell.org/webstart/VCell_Tutorials/MultiAppTransport_7.2.pdf.
# See storylines/multi-app-transport.md.
#
# The tutorial that teaches that an Application is a virtual experiment rather than a
# property of the model: one physiology, solved spatially and non-spatially,
# deterministically and stochastically, plus a parameter fit against exported data.
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

step "Physiology: the cargo complex either side of the nuclear membrane"
must tab name=ModelTabbedPane "Species" >/dev/null; sleep 1
new_species() {   # $1 = compartment menu label, $2 = row index, $3 = name
  button_menu name=ModelNewButton "$1"; sleep 2
  must setcell name=SpeciesTable "$2" 0 "$3" >/dev/null; sleep 1
}
new_species 'In Compartment Nuc' 0 RanC_nuc
new_species 'In Compartment Cyt' 1 RanC_cyt
new_species 'In Compartment Cyt' 2 C_cyt
new_species 'In Compartment Cyt' 3 Ran_cyt

step "Physiology: transport across NM, dissociation in Cyt"
new_reaction() {   # $1 = structure, $2 = name, $3 = equation
  must click name=ModelNewButton >/dev/null; sleep 2
  must combo name=ReactionStructureComboBox "$1" >/dev/null; sleep 1
  must settext name=ReactionNameTextField "$2" >/dev/null; sleep 1
  must settext name=ReactionEquationTextField "$3" >/dev/null; sleep 1
  must click "text=OK" >/dev/null; sleep 3
}
must tab name=ModelTabbedPane "Reactions" >/dev/null; sleep 2
# The PDF draws this one with the FluxReaction tool, and only the reaction diagram can
# create a FluxReaction - `Model.createFluxReaction` has exactly one interactive caller,
# in ReactionCartoonTool. The Reactions table makes SimpleReactions. So this is a membrane
# reaction with the same participants instead, which MembraneStructureAnalyzer resolves
# through the same machinery: a ResolvedFlux per adjacent volume species, reactant negated
# and product added. The generated math is checked for that below rather than assumed.
new_reaction NM  "flux0" "RanC_cyt -> RanC_nuc"
# Dissociation of the cargo complex, all three participants in the cytosol.
new_reaction Cyt "r0"    "RanC_cyt -> C_cyt + Ran_cyt"

step "Kinetics"
select_reaction() {
  must trow name=ReactionsTable "$(row name=ReactionsTable "$1" --in 'Name')" >/dev/null; sleep 2
}
set_param() {      # $1 = parameter name, $2 = expression
  local exp; exp=$(col name=ReactionKineticsParametersTable "Expression")
  must setcell name=ReactionKineticsParametersTable \
      "$(row name=ReactionKineticsParametersTable "$1" --in 'Name')" "$exp" "$2" >/dev/null; sleep 2
}

# Transport is driven by the concentration difference across the membrane, which is not a
# mass-action law - hence General, and hence Kfl coming into existence by being named.
select_reaction "flux0"
must combo name=ReactionKineticsTypeComboBox "General" >/dev/null; sleep 2
set_param J "Kfl*(RanC_cyt-RanC_nuc)"
set_param Kfl "2.0"

# Kr is a thousand times Kf, so the complex is overwhelmingly associated at equilibrium.
select_reaction "r0"
set_param Kf "1.0"
set_param Kr "1000.0"

step "Spatial deterministic application, from the neuroblastoma image stack"
# The image the PDF tells you to fetch from vcell.org. Given as an argument so the script
# says what it needs rather than guessing where a download landed.
IMAGE="${1:-}"
if [ -z "$IMAGE" ] || [ ! -f "$IMAGE" ]; then
  cat >&2 <<'USAGE'
usage: multi-app-transport.sh <path to NeuroblastomaStack.tif>

  curl -O https://vcell.org/webstart/VCell_Tutorials/7.7/NeuroblastomaStack.tif
USAGE
  exit 2
fi

tree_pick 'Applications' 'New Application>Deterministic'; sleep 4
tree_pick 'Application0' 'Rename'; sleep 1
must settext "text=Application0" "Spatial Deterministic" --enter >/dev/null; sleep 2
must expand name=bioModelEditorTree "$(navrow 'Spatial Deterministic' --exact)" true >/dev/null; sleep 2
navselect 'Geometry'; sleep 2

must tab name=ApplicationGeometryPanelTabbedPane "Geometry Definition" >/dev/null; sleep 2
button_menu "text=Add Geometry" 'New...'; sleep 3
must trow "type=JSortTable" \
    "$(row 'type=JSortTable' 'Image based (import from file, zip or directory)')" >/dev/null; sleep 1
must click "text=OK" >/dev/null; sleep 5
# A JFileChooser cannot be answered the way the rest of this bridge answers a dialog:
# what looks like a place to type a path is the hidden "go to folder" field, and typing
# into the listing depends on the current directory and sort order.
must choosefile "type=VCFileChooser" "$IMAGE" >/dev/null; sleep 10

# The PDF reduces the resolution by dragging a slider. "Manual XYZ Scale" is the same
# statement as numbers - 512x512x34 is more image than this model needs, and the whole
# segmentation runs on whatever comes out of here.
must click "text=Manual XYZ Scale..." >/dev/null; sleep 3
must settext name=OptionPane.textField "128,128,34" >/dev/null; sleep 1
dialog_button "INPUT:" OK; sleep 3
dialog_button "convert imported" OK; sleep 15

step "Segmentation: threshold for the nucleus, then for the whole cell"
# This is where the tutorial and a script genuinely part company, and it is worth being
# precise about where. Segmentation here is three different kinds of gesture:
#
#   - the histogram threshold, which LOOKS like a drag but MEANS "these intensities".
#     The panel's axis is pixel value; `pixelrange` says the range directly.
#   - Auto-Merge, which is a selection in a list of regions ordered by size. The PDF says
#     "select all the regions except the top two" - a positional statement a script can
#     make exactly.
#   - the paint and eraser tools, which really are per-pixel and have no model-level
#     equivalent. Those are NOT reproduced here. The PDF uses them to detach the nucleus
#     from the bright nuclear membrane; without them the segmentation is coarser, and the
#     geometry that comes out is this script's, not the PDF's.
answer "1. Add empty Domain"; sleep 3
must settext name=OptionPane.textField "Nuc" >/dev/null; sleep 1
answer OK; sleep 5

# The nucleus is the brightest thing in the stack; the 99th percentile of the whole
# volume is 213, so this takes roughly its top percent. Both thresholds are overridable
# because they are the one genuinely image-dependent number in this script.
NUC_LOW="${NUC_LOW:-180}"
CYT_LOW="${CYT_LOW:-10}"
must pixelrange name=HistogramPanel "$NUC_LOW" 255 >/dev/null; sleep 3
must click name=HistogramApplyButton >/dev/null; sleep 3
answer "Update Domain"; sleep 10

# Everything but the two largest regions - the background and the nucleus itself - is
# thresholding noise, which is exactly what Auto-Merge is for.
must list name=DomainRegionsList "2-" >/dev/null; sleep 2
must click name=AutoMergeButton >/dev/null; sleep 10

# The cytoplasm is everything above the noise floor - the stack's median pixel is 5 and
# its 90th percentile 20. 10 rather than 20 for a reason worth recording: at 20 the
# segmentation leaves an unmapped Nuc_background_membrane, meaning the nucleus touches
# the outside somewhere instead of being wrapped in cytoplasm. That is precisely the
# defect the PDF fixes with the paint tool, and a lower threshold fixes it without one -
# a parameter standing in for a gesture. At 10 the model has no warnings at all.
must click name=roiAddBtn >/dev/null; sleep 3
must settext name=OptionPane.textField "Cyt" >/dev/null; sleep 1
answer OK; sleep 5
must pixelrange name=HistogramPanel "$CYT_LOW" 255 >/dev/null; sleep 3
must click name=HistogramApplyButton >/dev/null; sleep 3
answer "Update Domain"; sleep 3
# The cytoplasm threshold takes in the nucleus too, being brighter still. Keeping the
# existing regions is what stops the second threshold erasing the first.
answer "Keep existing Domain Regions when overlapping"; sleep 12

# Now the three largest are background, cytoplasm and nucleus, and the rest is noise.
must list name=DomainRegionsList "3-" >/dev/null; sleep 2
must click name=AutoMergeButton >/dev/null; sleep 15

step "Finish segmenting, and give the stack a real Z thickness"
answer "Finish"
# Unassigned pixels become background, and a blank slice is added above and below so a
# compartment meant to be enclosed by a membrane cannot reach the edge of the space.
answer "Assign as default 'background'"
answer "Add empty border"
sleep 15

must tab name=ApplicationGeometryPanelTabbedPane "Geometry Definition" >/dev/null; sleep 4
must click "text=Edit Domain..." >/dev/null; sleep 4
# The stack carries no Z step information, so the imported geometry is flat - 27.2 um of
# nominal depth for 34 slices. The tutorial corrects it to 26.
must settext name=SizeZTextField "26" >/dev/null; sleep 1
must click name=Button2 >/dev/null; sleep 6

step "Structure mapping: physiology onto the segmented image"
must tab name=ApplicationGeometryPanelTabbedPane "Structure Mapping" >/dev/null; sleep 4
SUB=$(col name=StructureMappingTable "Subdomain")
# SurfaceClass.createName() sorts the two subvolume names, and Java sorts capitals first,
# so the plasma membrane between Cyt and background reads Cyt_background_membrane.
for pair in "EC:background" "Cyt:Cyt" "Nuc:Nuc" \
            "PM:Cyt_background_membrane" "NM:Cyt_Nuc_membrane"; do
  must setcell name=StructureMappingTable "$(row name=StructureMappingTable "${pair%%:*}")" \
      "$SUB" "${pair##*:}" >/dev/null; sleep 3
done

# The problems panel does not recompute on a mapping edit - it still listed all five
# structures as unmapped after they were mapped. Generating the math is what settles it,
# and is also the real check that the geometry and the physiology fit together.
must tab name=ApplicationTabbedPane "Simulations" >/dev/null; sleep 3
must tab name=ApplicationSimulationsPanelTabbedPane "Generated Math" >/dev/null; sleep 2
must click name=RefreshMathButton >/dev/null; sleep 15

# And the check the flux substitution earns. A membrane reaction should generate what a
# FluxReaction generates: the rate as a function ON the membrane subdomain, plus the two
# KFlux factors that carry it into the volumes either side. If a membrane SimpleReaction
# were being treated as something else, these would not be here.
must click name=ViewVCMDLRadioButton >/dev/null; sleep 5
MATH=$("$B" props "$(math_text_path)" | python3 -c 'import json,sys; print(json.load(sys.stdin).get("text"))')
for expected in 'Cyt_Nuc_membrane::J_flux0	 (Kfl * (RanC_cyt - RanC_nuc))' \
                'Cyt_Nuc_membrane::KFlux_NM_Cyt' 'Cyt_Nuc_membrane::KFlux_NM_Nuc'; do
  case "$MATH" in
    *"$expected"*) ;;
    *) echo "FATAL: generated math has no '$expected'" >&2; exit 1 ;;
  esac
done
echo "  flux across NM generates as a membrane flux, as a FluxReaction would" >&2
must click name=ViewEqunsRadioButton >/dev/null; sleep 2

must tab name=ApplicationSimulationsPanelTabbedPane "Simulations" >/dev/null; sleep 2

step "Initial conditions and a simulation for the spatial application"
must tab name=ApplicationTabbedPane "Specifications" >/dev/null; sleep 4
SPEC=name=spceciesContextSpecsTable
must setcell "$SPEC" "$(row "$SPEC" RanC_nuc)" "$(col "$SPEC" "Initial Condition")" \
    "4.5E-4" >/dev/null; sleep 3

must tab name=ApplicationTabbedPane "Simulations" >/dev/null; sleep 3
must click name=NewButton >/dev/null; sleep 4
must trow name=SimulationsTable 0 >/dev/null; sleep 1
must click name=EditButton >/dev/null; sleep 5
# The PDF asks for "the larger mesh elements to save on your simulation time" without
# naming them. The imported image would default this to 79 x 79 x 20; 41 halves it.
dialog_tab "Edit:" "Mesh"; sleep 2
must settext name=XTextField "41" --enter >/dev/null; sleep 3
dialog_tab "Edit:" "Solver"; sleep 2
# 15 s at 0.5 s intervals - the span and sampling of the CSV the tutorial ships, which is
# what the parameter estimation later in this script is fitted against.
must settext name=EndingTimeTextField     "15.0" --enter >/dev/null; sleep 1
must settext name=OutputTimeStepTextField "0.5"  --enter >/dev/null; sleep 1
dialog_button "Edit:" OK; sleep 4

step "Non-spatial stochastic copy, run locally"
# The point of the tutorial: one physiology, several virtual experiments. Copy As does not
# just duplicate the application - it re-derives it for a different kind of solver, and a
# non-spatial copy of a spatial application keeps the compartment sizes the image geometry
# implies, which is why the sizes never have to be typed anywhere in this script.
#
# --exact throughout, because the tree sorts the applications and every name here is a
# substring of another: without it "Spatial Deterministic" resolves to "Non-Spatial
# Deterministic", and the copy is made from the wrong application - which shows up much
# later as a Copy As menu that has lost its Spatial branch.
copy_app() {   # $1 = Copy As path, e.g. "Non-Spatial>Stochastic", $2 = new name
  navselect 'Spatial Deterministic' --exact; sleep 2
  tree_pick 'Spatial Deterministic' "Copy As>$1" --exact; sleep 5
  answer OK; sleep 15
  tree_pick 'Copy of Spatial Deterministic' 'Rename' --exact; sleep 1
  must settext "text=Copy of Spatial Deterministic" "$2" --enter >/dev/null; sleep 3
  # Collapse everything and expand only the new application. Child nodes are called
  # Geometry, Specifications, Simulations under EVERY application, so with two expanded
  # at once `navselect Simulations` is a coin toss between them.
  must expand name=bioModelEditorTree "$(navrow 'Applications')" false >/dev/null; sleep 1
  must expand name=bioModelEditorTree "$(navrow 'Applications')" true  >/dev/null; sleep 2
  must expand name=bioModelEditorTree "$(navrow "$2" --exact)" true >/dev/null; sleep 2
  # Collapsing deselects, and with nothing selected the right-hand pane is not an
  # application panel at all - so its tabs cannot be found. Select the new one.
  navselect "$2" --exact; sleep 3
}
copy_app "Non-Spatial>Stochastic" "Non-Spatial Stochastic"

navselect 'Simulations'; sleep 3
must click name=NewButton >/dev/null; sleep 4
must trow name=SimulationsTable 0 >/dev/null; sleep 1
must click name=EditButton >/dev/null; sleep 5
must tab name=JTabbedPane1 "Solver" >/dev/null; sleep 2
must settext name=EndingTimeTextField     "15.0" --enter >/dev/null; sleep 1
must settext name=OutputTimeStepTextField "0.5"  --enter >/dev/null; sleep 1
dialog_button "Edit:" OK; sleep 4
quick_run
must list name=YAxisChoice "RanC_cyt" >/dev/null; sleep 3

step "Export the run as CSV, the way the tutorial exports it to a spreadsheet"
# The PDF copies the results spreadsheet, pastes it into Excel and saves it as CSV, then
# imports that file as experimental data. Reading the same cells and writing the same file
# is the model-level version of the same round trip - and it keeps the fit self-consistent,
# fitting the model against a run of itself rather than against shipped data.
DATA_CSV="${DATA_CSV:-$(cd "$(dirname "$IMAGE")" && pwd)/RanC_cyt_stochastic.csv}"
{
  echo "t,RanC_cyt"
  for i in $(seq 0 "$(result_row_at_time 15.0)"); do
    printf '%s,%s\n' "$(result_cell "$i" t)" "$(result_cell "$i" RanC_cyt)"
  done
} > "$DATA_CSV"
echo "  wrote $(wc -l < "$DATA_CSV") lines to $DATA_CSV" >&2
if [ "$(wc -l < "$DATA_CSV")" -lt 30 ]; then
  echo "FATAL: the stochastic run produced no usable series" >&2
  exit 1
fi

step "Non-spatial deterministic copy, and a parameter fit against that run"
copy_app "Non-Spatial>Deterministic" "Non-Spatial Deterministic"

must tab name=ApplicationTabbedPane "Parameter Estimation" >/dev/null; sleep 4
must click name=NewAnalysisTaskButton >/dev/null; sleep 5
must settext name=OptionPane.textField "Fit Kf" >/dev/null; sleep 1
answer OK; sleep 6

# Kf, the forward rate of the cytoplasmic dissociation, is the one the tutorial fits.
# --exact matters: Kfl, the flux coefficient, is in the same list.
must click name=AddEstimationParameterButton >/dev/null; sleep 4
PARAMS=$(dialog_table 'Select Parameters')
must trow "$PARAMS" "$(row "$PARAMS" 'Kf' --exact --in 'Parameter')" >/dev/null; sleep 1
answer OK; sleep 4

must tab name=ParameterEstimationPanelTabbedPane "Experimental Data Import" >/dev/null; sleep 4
must click name=ImportButton >/dev/null; sleep 5
must choosefile "type=VCFileChooser" "$DATA_CSV" >/dev/null; sleep 8

# The PDF maps the imported column to a model variable by hand. VCell matches them by name
# on import when the names agree, so this asserts the mapping rather than making it.
must tab name=ParameterEstimationPanelTabbedPane "Experimental Data Mapping" >/dev/null; sleep 4
MAP=name=ExperimentalDataMappingTable
if ! "$B" readcell "$MAP" "$(row "$MAP" RanC_cyt)" 1 | grep -q 'RanC_cyt'; then
  echo "FATAL: the imported data column was not mapped to a model variable" >&2
  exit 1
fi

must tab name=ParameterEstimationPanelTabbedPane "Run Task" >/dev/null; sleep 3
must click name=SolveByCopasiButton >/dev/null
# Copasi runs locally, like the solvers - nothing is sent anywhere.
for i in $(seq 1 180); do
  sleep 2
  "$B" windows 2>/dev/null | grep -q "Running Parameter Estimation" || break
done
sleep 3
BEST=$("$B" readcell name=ParameterEstimationResultsTable 0 "Best Estimate" \
    | python3 -c 'import json,sys; print(json.load(sys.stdin).get("value"))')
echo "  Kf: model value 1.0, best estimate $BEST" >&2
if [ -z "$BEST" ] || [ "$BEST" = "None" ]; then
  echo "FATAL: the parameter estimation produced no estimate" >&2
  exit 1
fi

step "Spatial stochastic copy, counting molecules instead of concentrations"
copy_app "Spatial>Stochastic" "Spatial Stochastic"

navselect 'Specifications'; sleep 4
# A stochastic application can state its initial conditions as particle counts, which is
# what the species actually are at these concentrations - a few tens of molecules.
must click name=NumberOfParticlesRadioButton >/dev/null; sleep 4
navselect 'Simulations'; sleep 3
must click name=NewButton >/dev/null; sleep 4

step "Done -- one physiology, four applications, a local run and a local fit."
