package cbit.vcell.simdata;

import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solvers.CartesianMesh;

public class VolumeDataInfo{
	public final int volumeIndex;
	public final String volumeNamePhysiology;
	public final String volumeNameGeometry;
	public final Integer subvolumeID0;
	public final int volumeRegionID;
	public VolumeDataInfo(int volumeIndex,CartesianMesh cartesianMesh,SimulationModelInfo simulationModelInfo){
		this.volumeIndex = volumeIndex;
		volumeRegionID = cartesianMesh.getVolumeRegionIndex(volumeIndex);
		if(cartesianMesh.hasSubvolumeInfo()){
			subvolumeID0 = cartesianMesh.getSubVolumeFromVolumeIndex(volumeIndex);
			volumeNamePhysiology = simulationModelInfo.getVolumeNamePhysiology(subvolumeID0);
			volumeNameGeometry = simulationModelInfo.getVolumeNameGeometry(subvolumeID0);				
		}else{
			subvolumeID0 = null;
			volumeNamePhysiology = null;
			volumeNameGeometry = null;							
		}
	}
}