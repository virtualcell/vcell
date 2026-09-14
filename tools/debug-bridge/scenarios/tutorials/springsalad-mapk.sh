#!/usr/bin/env bash
#
# VCell Tutorial: SpringSaLaD, receptor kinase activation
#
# Reproduces the storyline of
# vcell.org/webstart/SpringSaLaD/SpringSaLaDUsersGuideAndTutorial.pdf as the VCell
# workflow that replaces it. See storylines/springsalad-mapk.md.
#
# That document is not an outdated VCell tutorial - it describes a DIFFERENT PROGRAM, a
# standalone pair of jars with its own GUI, which VCell has since absorbed as the
# SpringSaLaD application and the Langevin solver. So this script is a translation rather
# than a re-enactment, and the storyline carries the noun-by-noun mapping.
#
# A toy model of MAPK-like activation: an extracellular ligand binds the ligand-binding
# domain of a transmembrane receptor kinase, which allosterically switches on its
# intracellular kinase domain, which phosphorylates a substrate.
#
# TWO PARTS, AND WHY.
#
# Part 1 builds the model - structures, molecules, the membrane anchor, species, the
# SpringSaLaD application, and the whole of Molecular Structures: every site's
# compartment, position, radius, diffusion rate and colour, and the springs between them.
# All of it through tables. The standalone program's 3D editor has no equivalent here and
# needs none.
#
# Part 2 does not build the reactions, because nothing can. Reaction RULES have no
# textual route: BioModelEditorReactionTableModel.isCellEditable allows the BioNetGen
# definition column only for a ReactionStep, and a ReactionRule is not one; and the rule
# editor is a single anonymous custom-painted component with zero children. Same wall the
# two rule-based tutorials hit - issue #2068. The .ssld route does not rescue it either:
# that format is the standalone program's own save file, so writing one by hand means
# writing the whole system - geometry, times and counters included - rather than the few
# rules that are missing, and learning it by exporting first hits the bug below.
#
# So Part 2 opens a model that already has them: aaa-aSpringSaLaD-Good, a published model
# kept in this repository as a test fixture, which carries exactly one rule of every
# SpringSaLaD subtype. It reads each subtype back out, runs the Langevin solver locally,
# and drives the 3D Trajectory viewer.
#
set -euo pipefail
. "$(cd "$(dirname "$0")" && pwd)/_common.sh"

REPO="$(cd "$(dirname "$0")/../../../.." && pwd)"
REFERENCE="$REPO/vcell-core/src/test/resources/org/vcell/sbml/vcml_published/biomodel_315318780.vcml"

fail() { echo "FATAL: $*" >&2; exit 1; }
same() {   # $1 = what, $2 = got, $3 = want
  if [ "$2" = "$3" ]; then printf '  %s = %s\n' "$1" "$2"
  else fail "$1 is '$2', expected '$3'"; fi
}

dismiss OK
sleep 1

# ---------------------------------------------------------------- Part 1: the model ----

step "Three structures, with the names SpringSaLaD requires"
# Not a stylistic choice: SpringSaLaDGoodReactionsTest and LangevinLngvWriter both expect
# exactly three, named Intracellular / Membrane / Extracellular. A model with any other
# shape is rejected late, when the solver input is written.
must tab name=ModelTabbedPane "Structures" >/dev/null; sleep 1
must setcell name=StructuresTable 0 0 "Extracellular" >/dev/null; sleep 1
must click name=ModelNewMembraneButton >/dev/null; sleep 1
must setcell name=StructuresTable 1 0 "Membrane" >/dev/null; sleep 1
must click name=ModelNewButton >/dev/null; sleep 1
must setcell name=StructuresTable 2 0 "Intracellular" >/dev/null; sleep 1

step "Molecules, from the BioNetGen definition column"
# The document builds these by adding site types, then sites, then states, then links, in
# a 3D editor. Three of those four are one cell here. The fourth - the links - has no BNGL
# spelling and is set per species in the application, further down.
#
# 'Anchor' is reserved: a site of that name, with the single state 'Anchor', is what makes
# a molecule membrane-bound. It is the document's site A, the "trans-membrane anchor".
#
# THE ORDER OF THE SITES IS NOT COSMETIC. SpeciesContextSpec assigns each site a
# compartment by where it sits RELATIVE TO THE ANCHOR in the list: before it means
# Extracellular, after it means Intracellular, and the anchor itself is the one site on
# the Membrane. It lays them out along z in the same order, 4 nm apart, and links them in
# a chain. So writing the receptor kinase as B, Anchor, K - outside, membrane, inside,
# which is how the document draws it - produces the right topology and the right
# geometry with nothing left to correct. Writing Anchor first, as one might, silently
# puts the ligand-binding domain inside the cell.
navselect 'Molecules'; sleep 3
BNG=$(col name=MolecularTypeTable 'BioNetGen Definition')
for d in 'Ligand(L)' 'RK(B~State0~State1,Anchor~Anchor,K~Off~On)' 'Substrate(S~u~p)'; do
  must click name=ModelNewButton >/dev/null; sleep 3
  must setcell name=MolecularTypeTable "$(row name=MolecularTypeTable 'MT0' --exact)" "$BNG" "$d" >/dev/null
  sleep 3
done

step "Anchor RK to the membrane"
# Anchoring lives in the PHYSIOLOGY, on the molecule, not in the application - it says
# where this molecule may exist at all. The structure checkboxes are disabled until the
# "Only these:" radio is chosen, so the order matters.
must trow name=MolecularTypeTable "$(row name=MolecularTypeTable 'RK' --exact)" >/dev/null; sleep 3
must click name=AnchorOnlyButton >/dev/null; sleep 2
must click name=AnchorStructureCheckBox_Membrane >/dev/null; sleep 3

step "Species, one per molecule, sharing its name"
# SpringSaLaD wants a biunivocal correspondence: one seed species per molecule, and the
# names should match. The New button's pop-up is how a species gets PLACED - a species
# cannot be moved between compartments afterwards from this table.
navselect 'Species'; sleep 3
SBNG=$(col name=SpeciesTable 'BioNetGen Definition')
new_species() {   # $1 = compartment menu item, $2 = name, $3 = default name, $4 = pattern
  button_menu name=ModelNewButton "$1"; sleep 3
  must setcell name=SpeciesTable "$(row name=SpeciesTable "$3" --exact)" 0 "$2" >/dev/null; sleep 2
  must setcell name=SpeciesTable "$(row name=SpeciesTable "$2" --exact)" "$SBNG" "$4" >/dev/null; sleep 3
}
new_species 'In Compartment Extracellular' Ligand    s0 'Ligand(L)'
new_species 'In Membrane Membrane'         RK        s1 'RK(B~State0,Anchor~Anchor,K~Off)'
new_species 'In Compartment Intracellular' Substrate s2 'Substrate(S~u)'

step "The SpringSaLaD application"
# This is the document's "System Geometry" panel, and there is nothing to fill in: the
# application builds the cube-with-a-planar-membrane itself, as a 3D analytic geometry,
# and maps all three structures into it.
tree_pick 'Applications' 'New Application>SpringSaLaD'; sleep 10
tree_pick 'Application0' 'Rename'; sleep 1
must settext "text=Application0" "SpringSaLaD" --enter >/dev/null; sleep 2
must expand name=bioModelEditorTree "$(navrow 'SpringSaLaD' --exact)" true >/dev/null; sleep 2

step "Molecular Structures: the site geometry the 3D editor used to draw"
# The document's 3D editor is replaced by three tables: the species, its sites, and the
# springs between them. Everything it asks the reader to drag is a cell here.
#
# Its numbers, from the tutorial text: every site type gets a radius of 2 nm and the
# ligand a diffusion constant of 2 um2/s; the anchor is moved to y = 5 and the two domains
# placed 5 nm either side of it in z, which "will leave a bond of length 1 nm between the
# sites" for spheres of radius 2.
must tab name=ApplicationTabbedPane "Specifications" >/dev/null; sleep 3
must tab name=ApplicationSpecificationsPanelTabbedPane "Molecular Structures" >/dev/null; sleep 4

SPEC=name=spceciesContextSpecsTable
SITES=name=molecularComponentSpecsTable
pick() { must trow "$SPEC" "$(row "$SPEC" "$1" --exact)" >/dev/null; sleep 3; }
site() {   # $1 = site, $2 = column header, $3 = value
  must setcell "$SITES" "$(row "$SITES" "$1" --exact)" "$(col "$SITES" "$2")" "$3" >/dev/null; sleep 2
}

#
# COLOUR IS NOT DECORATION HERE. The trajectory viewer draws on a BLACK canvas, so a dark
# colour is a site you cannot see - and the reserved Anchor site defaults to DARK_GRAY,
# which on black is a smudge. (SpringSaladViewerCanvas has a floor that lifts a colour
# whose every channel is under 40, but DARK_GRAY is 64,64,64 and sails past it, then loses
# most of that to the sphere's own shading.) So every site gets a light colour, and the
# ligand gets the green the document asks for.
pick RK
for s in B Anchor K; do
  site "$s" 'Radius'     2.0
  site "$s" 'Diff. Rate' 2.0
  site "$s" ' Y '        5
done
site B ' Z ' 3
site K ' Z ' 13
site B      'Color' CYAN
site Anchor 'Color' WHITE
site K      'Color' ORANGE

pick Ligand
site L 'Radius' 2.0; site L 'Diff. Rate' 2.0; site L 'Color' GREEN

pick Substrate
site S 'Radius' 2.0; site S 'Diff. Rate' 2.0; site S 'Color' YELLOW

step "Initial conditions: 20 ligand, 10 RK, 20 substrate"
# The document counts particles; VCell's default is a concentration. The radio button
# switches the whole table, and the units in the cells change with it.
#
# The document also says to tick "Set at 2D" on the receptor, which confines a membrane
# molecule's diffusion to the plane. VCell has the column - "Is 2D" - but it is fixed:
# SpeciesContextSpecsTableModel returns false for it unconditionally, over four
# commented-out lines that would have allowed it for a membrane species, with the note
# "is2D flag permanently set to false in this version". So there is nothing to click, and
# the model runs with 3D diffusion for every molecule.
must tab name=ApplicationSpecificationsPanelTabbedPane "Species" >/dev/null; sleep 3
must click name=NumberOfParticlesRadioButton >/dev/null; sleep 3
IC=$(col "$SPEC" 'Initial Condition')
must setcell "$SPEC" "$(row "$SPEC" 'Ligand' --exact)"    "$IC" 20 >/dev/null; sleep 2
must setcell "$SPEC" "$(row "$SPEC" 'RK' --exact)"        "$IC" 10 >/dev/null; sleep 2
must setcell "$SPEC" "$(row "$SPEC" 'Substrate' --exact)" "$IC" 20 >/dev/null; sleep 2

step "The box: 100 x 100 nm, 200 nm of cytosol and 200 nm outside"
# The document's System Geometry panel, which is the one place the application's
# auto-built geometry is not already what the reader wants. It starts as a 100 nm cube
# with the membrane at z = 90 nm; the document asks for 200 nm on each side of it.
#
# Two edits, and they are not interchangeable: the box height is the geometry's extent,
# and where the membrane sits inside it is the Intracellular subdomain's expression. The
# solver requires that expression to be exactly of the form "z < number" - LangevinLngvWriter
# parses it - and Extracellular to be exactly 1.0.
navselect 'Geometry'; sleep 3
must tab name=ApplicationGeometryPanelTabbedPane "Geometry Definition" >/dev/null; sleep 4
must click name=JButtonChangeDomain >/dev/null; sleep 4
must settext name=SizeZTextField "0.4" --enter >/dev/null; sleep 1
dialog_button "Geometry Size" "OK"; sleep 4
must setcell name=SubVolumesTable "$(row name=SubVolumesTable 'Intracellular' --exact)" 1 "(z < 0.2)" >/dev/null
sleep 4

step "Drop the auto-generated observables that landed in the wrong compartment"
# Creating a molecule creates an observable counting it, and Model.createObservable puts
# it in getStructure(0) - Extracellular here - because nothing tells it otherwise. For RK,
# which is anchored to the membrane, that is a standing warning: "Molecule RK cannot be
# present in the structure due to anchoring."
#
# There is no way to move it. ObservableTableModel makes the Structure column read-only,
# and building a replacement through New > In Membrane leaves a DIFFERENT complaint -
# addObservable() seeds an empty species pattern that setting the BioNetGen definition
# does not consume, so the new observable reports "Molecule of Species Pattern is empty".
# Deleting is the only clean move. Nothing is lost for this application: the Langevin
# writer does not read observables at all - SpringSaLaD results come from the solver's own
# molecule, state, bond, site and cluster counters.
navselect 'Observables'; sleep 3
for o in O0_RK_tot O0_Substrate_tot; do
  must trow name=ObservablesTable "$(row name=ObservablesTable "$o" --exact)" >/dev/null; sleep 2
  must click name=ModelDeleteButton >/dev/null; sleep 3
  dismiss Yes; dismiss OK; sleep 2
done

step "No errors, no warnings"
# The whole point of Part 1. A SpringSaLaD model has more ways to be wrong than an
# ordinary one - three named structures, an anchored molecule, a site on each side of the
# membrane - and every one of them is reported here rather than at solve time.
must tab name=RightBottomTabbedPane 2 >/dev/null; sleep 3
PROBLEMS=$(curl -s "http://127.0.0.1:9123/tree" | python3 -c '
import json, sys
def walk(n):
    t = n.get("table")
    if t and "Description" in str(t.get("columns")):
        for r in t.get("cells") or []:
            print(r[0])
    for c in n.get("children") or []:
        walk(c)
for root in json.load(sys.stdin):
    walk(root)
')
if [ -n "$PROBLEMS" ]; then
  echo "$PROBLEMS" >&2
  fail "the model has problems"
fi
echo "  Problems tab: empty"

step "Read the model back"
cell() {   # $1 = table selector, $2 = row, $3 = column header
  "$B" readcell "$1" "$2" "$3" \
    | python3 -c 'import json,sys; print(json.load(sys.stdin).get("value"))'
}

navselect 'Structures'; sleep 3
for s in Extracellular Membrane Intracellular; do
  row name=StructuresTable "$s" --exact >/dev/null
done
echo "  three structures, correctly named"

navselect 'Molecules'; sleep 3
same "RK definition" \
  "$(cell name=MolecularTypeTable "$(row name=MolecularTypeTable RK --exact)" 'BioNetGen Definition')" \
  'RK(B~State0~State1,Anchor~Anchor,K~Off~On)'

navselect 'SpringSaLaD' --exact; sleep 2
must tab name=ApplicationTabbedPane "Specifications" >/dev/null; sleep 3
must tab name=ApplicationSpecificationsPanelTabbedPane "Molecular Structures" >/dev/null; sleep 4
pick RK
# The point of the whole Molecular Structures step, in three lines: the receptor spans the
# membrane, outside to inside, with the anchor in the middle - and nothing placed it there
# but the order the sites were written in.
same "B location"      "$(cell "$SITES" "$(row "$SITES" B --exact)"      'Location')" 'Extracellular'
same "Anchor location" "$(cell "$SITES" "$(row "$SITES" Anchor --exact)" 'Location')" 'Membrane'
same "K location"      "$(cell "$SITES" "$(row "$SITES" K --exact)"      'Location')" 'Intracellular'
same "B z"             "$(cell "$SITES" "$(row "$SITES" B --exact)"      ' Z ')"      '3'
same "K z"             "$(cell "$SITES" "$(row "$SITES" K --exact)"      ' Z ')"      '13'
same "B radius"        "$(cell "$SITES" "$(row "$SITES" B --exact)"      'Radius')"   '2.0 [nm]'
same "Anchor colour"   "$(cell "$SITES" "$(row "$SITES" Anchor --exact)" 'Color')"    'WHITE'
# The springs follow the same order, so the receptor is a star centred on its anchor and
# not a chain through it - which is what the document draws, and what it spends a page of
# Add Link gestures achieving.
same "first link"  "$(cell name=linkSpecsTable 0 0)" 'Anchor :: B'
same "second link" "$(cell name=linkSpecsTable 1 0)" 'Anchor :: K'
same "link length" "$(cell name=linkSpecsTable 0 1)" '5 [nm]'

must tab name=ApplicationSpecificationsPanelTabbedPane "Species" >/dev/null; sleep 3
same "RK initial condition" \
  "$(cell "$SPEC" "$(row "$SPEC" RK --exact)" 'Initial Condition')" '10.0 [molecules]'

# -------------------------------------------------- Part 2: the reactions and the run ----

step "Open a model that already has the reactions"
# aaa-aSpringSaLaD-Good: a published model, kept here as a test fixture, whose nine rules
# are one of every subtype the SpringSaLaD application understands. Its molecules are
# called Molecule0 and Molecule1 - it is a conformance model, not a biological one - and
# that is exactly why it is the clearest thing to read the subtypes off.
must menu "File>Open>Local..." >/dev/null; sleep 6
must choosefile "type=VCFileChooser" "$REFERENCE" >/dev/null
for i in $(seq 1 30); do
  "$B" windows 2>/dev/null | grep -q 'SpringSaLaD-Good' && break
  sleep 3
done
"$B" windows 2>/dev/null | grep -q 'SpringSaLaD-Good' || fail "the reference model did not open"

# Close the model we built. Two documents mean two of every named table, and a lookup that
# resolves against the wrong window reports the wrong answer rather than failing.
must menu "File>Close" 0 >/dev/null; sleep 3
answer "No"; sleep 5

step "One rule of every SpringSaLaD subtype"
# This table is the document's three reaction panels - binding, transition, allosteric -
# plus the two it describes in the framework section but has no panel for, creation and
# decay. Subtype and Condition are not properties of the rule: they are how THIS
# application reads it, which is why they live in Specifications and not in Physiology.
navselect 'Application0' --exact; sleep 3
must tab name=ApplicationTabbedPane "Specifications" >/dev/null; sleep 3
must tab name=ApplicationSpecificationsPanelTabbedPane "Reaction" >/dev/null; sleep 4
RS=name=ReactionSpecsTable
check_rule() {   # $1 = rule, $2 = subtype, $3 = condition
  local r; r=$(row "$RS" "$1" --exact)
  same "$1 subtype" "$(cell "$RS" "$r" 'Subtype')" "$2"
  if [ -n "$3" ]; then same "$1 condition" "$(cell "$RS" "$r" 'Condition')" "$3"; fi
}
check_rule creation    Creation   ''
check_rule decay       Decay      ''
check_rule binding     Binding    ''
check_rule allosteric  Allosteric ''
# All three transition conditions, and the reason the storyline spends a table on them:
# the words the document uses are not the words VCell shows. SpringSaLaD's "None" - no
# condition at all - is VCell's "Any"; SpringSaLaD's "Free" is VCell's "Unbound".
check_rule transition_none  Transition Unbound
check_rule transition_free  Transition Any
check_rule transition_bound Transition Bound
# Binding is the only subtype that carries a bond length: it is the spring the reaction
# creates, and the one number a transition or a decay has no use for.
same "binding bond length" "$(cell "$RS" "$(row "$RS" binding --exact)" 'Bond Length')" '1.0 [nm]'
# transition_bound is switched off in this application. Enabled is per-application too, so
# the same physiology can be read by one application and ignored by another - the document
# has no equivalent, because there is only ever one system.
same "transition_bound enabled" "$(cell "$RS" "$(row "$RS" transition_bound --exact)" 'Enabled')" 'false'

step "Run the Langevin solver on this machine"
# Native Quick Run, with the bundled localsolvers/<platform>/langevin_x64 - no account, no
# server, and nothing saved. This is the document's "Run Simulation", minus the walk it
# recommends taking while it finishes.
must tab name=ApplicationTabbedPane "Simulations" >/dev/null; sleep 4
quick_run 0

step "The 3D Trajectory viewer"
# The document's 3D Viewer, and the mapping is close to one-for-one: drag to rotate, wheel
# to zoom, a slider over the time points, a play button with a chosen frame rate, movie
# export, and toggles for the box and the membrane. What it adds is per-species and
# per-site visibility, which matters here because a SpringSaLaD molecule IS its sites.
dialog_tab "Results for Simulation" "3D Trajectory"; sleep 8

readout() { "$B" props name=SpringSaladReadout | python3 -c 'import json,sys; print(json.load(sys.stdin).get("text"))'; }
FIRST=$(readout)
echo "  at open: $FIRST"
case "$FIRST" in
  *"t = 0.000"*) ;;
  *) fail "the viewer did not open on the first frame: $FIRST" ;;
esac

# -1 means the end, the same way it does for readcell's last row. The last frame is where
# a trajectory has actually gone somewhere.
must slider name=SpringSaladFrameSlider -1 >/dev/null; sleep 4
LAST=$(readout)
echo "  at the end: $LAST"
case "$LAST" in
  *"t = 0.02"*) ;;
  *) fail "the last frame is not the end of the run: $LAST" ;;
esac

# The document's Options menu, as three checkboxes on the canvas.
for t in SpringSaladToggleMembrane SpringSaladToggleBox SpringSaladToggleLinks; do
  must click "name=$t" >/dev/null; sleep 1
  must click "name=$t" >/dev/null; sleep 1
done
echo "  membrane, box and link display all toggle"

must click name=SpringSaladResetViewButton >/dev/null; sleep 2
echo
echo "=== done"
