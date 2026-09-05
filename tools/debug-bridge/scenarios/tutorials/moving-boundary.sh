#!/usr/bin/env bash
#
# VCell Tutorial: BioModel using the Moving Boundary Solver
#
# Reproduces the storyline of vcell.org/webstart/VCell_Tutorials/MovingBoundaries.pdf
# against a current client. See storylines/moving-boundary.md.
#
# This is Simple FRAP in a cell that MOVES: same physiology, but the membrane is given a
# velocity and the domain deforms as the simulation runs. Everything up to the geometry
# is the FRAP script with different numbers; the Kinematics tab is the new ground.
#
set -euo pipefail
. "$(cd "$(dirname "$0")" && pwd)/_common.sh"

VX="4"                 # constant drift to the right, um/s
VY="5*sin(10*t)"       # ... while oscillating up and down

dismiss OK
sleep 1

step "Physiology: three structures, EC | PM | Cyt, and one species Dex in Cyt"
must tab name=ModelTabbedPane "Structures" >/dev/null; sleep 1
must setcell name=StructuresTable 0 0 "EC" >/dev/null; sleep 1
must click name=ModelNewMembraneButton >/dev/null; sleep 1
must setcell name=StructuresTable 1 0 "PM" >/dev/null; sleep 1
must click name=ModelNewButton >/dev/null; sleep 1
must setcell name=StructuresTable 2 0 "Cyt" >/dev/null; sleep 1
must tab name=ModelTabbedPane "Species" >/dev/null; sleep 1
must click name=ModelNewButton >/dev/null; sleep 1
must click "text=In Compartment Cyt" >/dev/null; sleep 2
must setcell name=SpeciesTable 0 0 "Dex" >/dev/null; sleep 1

step "Application: new Deterministic application, renamed FRAP"
must rrow name=bioModelEditorTree "$(navrow 'Applications')" >/dev/null; sleep 1
must click "text=New Application" >/dev/null; sleep 1
must click "text=Deterministic" >/dev/null; sleep 3
must rrow name=bioModelEditorTree "$(navrow 'Application0')" >/dev/null; sleep 1
must click "text=Rename" >/dev/null; sleep 1
must settext "text=Application0" "FRAP" --enter >/dev/null; sleep 2

step "Geometry: a circle of radius 5 at the LEFT edge of a 20x20 um domain"
must expand name=bioModelEditorTree "$(navrow 'FRAP')" true >/dev/null; sleep 2
navselect 'Geometry'; sleep 2
must tab name=ApplicationGeometryPanelTabbedPane "Geometry Definition" >/dev/null; sleep 2
must click "text=Add Geometry" >/dev/null; sleep 1
must click "text=New..." >/dev/null; sleep 2
# 2D only: the moving boundary solver has no 3D implementation.
must trow "type=JSortTable" "$(row 'type=JSortTable' 'Analytic Equations (2D)')" >/dev/null; sleep 1
must click "text=OK" >/dev/null; sleep 3
must setcell name=SubVolumesTable 0 0 "EC" >/dev/null; sleep 2
dismiss OK; sleep 1
must click "text=Add Subdomain" >/dev/null; sleep 1
must click "text=Analytic ..." >/dev/null; sleep 2
must combo name=subdomainShapeComboBox "Circle" >/dev/null; sleep 1
must settext name=circleCenterTextField "0,0" >/dev/null; sleep 1
must settext name=circleRadiusTextField "5" >/dev/null; sleep 1
must click "text=New Subdomain" >/dev/null; sleep 3
must setcell name=SubVolumesTable "$(row name=SubVolumesTable 'subdomain0')" 0 "Cyt" >/dev/null; sleep 2
dismiss OK; sleep 1
# The origin is deliberately NOT centred: the circle sits at the left edge so it has
# room to travel right across the domain during the simulation.
must click "text=Edit Domain..." >/dev/null; sleep 2
must settext name=SizeXTextField   "20"  >/dev/null; sleep 1
must settext name=SizeYTextField   "20"  >/dev/null; sleep 1
must settext name=OriginXTextField "-6"  >/dev/null; sleep 1
must settext name=OriginYTextField "-10" >/dev/null; sleep 1
must click "text=OK" >/dev/null; sleep 3

step "Structure mapping: EC, Cyt and the PM membrane"
must tab name=ApplicationGeometryPanelTabbedPane "Structure Mapping" >/dev/null; sleep 3
SUB=$(col name=StructureMappingTable "Subdomain")
must setcell name=StructureMappingTable "$(row name=StructureMappingTable 'EC')"  "$SUB" "EC"  >/dev/null; sleep 2
must setcell name=StructureMappingTable "$(row name=StructureMappingTable 'Cyt')" "$SUB" "Cyt" >/dev/null; sleep 3
must setcell name=StructureMappingTable "$(row name=StructureMappingTable 'PM')" "$SUB" "Cyt_EC_membrane" >/dev/null; sleep 3

step "Kinematics: the membrane moves, and the cytosol moves with it"
must tab name=ApplicationGeometryPanelTabbedPane "Kinematics" >/dev/null; sleep 3

# The membrane's own velocity.
must click name=SpatialProcessNewButton >/dev/null; sleep 2
must click "text=new Surface Kinematics" >/dev/null; sleep 3
set_velocities() {   # $1 = row in the process table
  must trow name=SpatialProcessesTable "$1" >/dev/null; sleep 2
  local exp; exp=$(col name=SpatialProcessParametersTable "Expression")
  # The parameter name lives in the "Parameter" column; column 0 is a prose description
  # ("surface velocity (x coord)"), which is why findrow has to be told where to look.
  must setcell name=SpatialProcessParametersTable "$(row name=SpatialProcessParametersTable 'velocityX' --in 'Parameter')" "$exp" "$VX" >/dev/null; sleep 2
  must setcell name=SpatialProcessParametersTable "$(row name=SpatialProcessParametersTable 'velocityY' --in 'Parameter')" "$exp" "$VY" >/dev/null; sleep 2
}
set_velocities "$(row name=SpatialProcessesTable 'sproc_0')"

# Volume kinematics. VCell creates one for the NEXT volume in the spatial-object table
# each time, so the first attempt lands on EC and a second is needed to reach Cyt; the
# tutorial says to delete the one you did not want.
must click name=SpatialProcessNewButton >/dev/null; sleep 2
must click "text=new Volume Kinematics" >/dev/null; sleep 3
must click name=SpatialProcessNewButton >/dev/null; sleep 2
must click "text=new Volume Kinematics" >/dev/null; sleep 3
must trow name=SpatialProcessesTable "$(row name=SpatialProcessesTable 'vobj_EC0' --in 'Spatial Objects (and Quantities)')" >/dev/null; sleep 1
must click name=SpatialProcessDeleteButton >/dev/null; sleep 3

# A volume process reports an error until its spatial object is allowed to HAVE an
# interior velocity - the checkbox is on the object, not on the process.
must trow name=SpatialObjectsTable "$(row name=SpatialObjectsTable 'vobj_Cyt1')" >/dev/null; sleep 2
must click "text=Interior Velocity" >/dev/null; sleep 2

# In this example the cytosol travels with the membrane, so the same expressions.
set_velocities "$(row name=SpatialProcessesTable 'vobj_Cyt1' --in 'Spatial Objects (and Quantities)')"

step "Specifications: the bleached square (Dex keeps its default diffusion of 10)"
must tab name=ApplicationTabbedPane "Specifications" >/dev/null; sleep 3
SPEC=name=spceciesContextSpecsTable
must setcell "$SPEC" 0 "$(col "$SPEC" 'Initial Condition')" \
    '(10.0*((x<-2.5)||(x>2.5)||(y<-2.5)||(y>2.5)))' >/dev/null; sleep 2

step "Done -- model built, kinematics specified."
# The PDF now creates a simulation with a deliberately coarse mesh and runs it on the
# Moving Boundary solver. Not scripted, for the same reason as the FRAP tutorial: it
# needs an account and dispatches a real job. Note the PDF's own warning that VCell's
# spatial analysis tools do not work on moving-boundary results - export to NRRD/HDF5.
