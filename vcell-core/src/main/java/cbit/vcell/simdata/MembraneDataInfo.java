package cbit.vcell.simdata;

import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.MembraneElement;

public class MembraneDataInfo{
	public final int membraneIndex;
	public final MembraneElement membraneElement;
	public final String membraneName;
	public final int membraneRegionID;
	public MembraneDataInfo(int membraneIndex,CartesianMesh cartesianMesh,SimulationModelInfo simulationModelInfo){
		this.membraneIndex = membraneIndex;
		membraneElement = cartesianMesh.getMembraneElements()[membraneIndex];
		membraneRegionID = cartesianMesh.getMembraneRegionIndex(membraneIndex);
		membraneName =
			simulationModelInfo.getMembraneName(
					cartesianMesh.getSubVolumeFromVolumeIndex(membraneElement.getInsideVolumeIndex()),
					cartesianMesh.getSubVolumeFromVolumeIndex(membraneElement.getOutsideVolumeIndex()), false);
	}
}