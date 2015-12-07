package org.vcell.vis.vtk;

import java.io.File;
import java.io.IOException;

import org.vcell.vis.vismesh.thrift.VisMesh;

public abstract class VtkService {
	public static VtkService vtkService = null;
	
	public static VtkService getInstance(){
		//return new VtkGridUtils();
		return new VtkServicePython();
	}
	
	public abstract void writeChomboMembraneVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException;
	
	public abstract void writeChomboVolumeVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException;

	public abstract void writeFiniteVolumeSmoothedVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException;

	public abstract void writeDataArrayToNewVtkFile(File emptyMeshFile, String variableVtuName, double[] data, File meshFileForVariableAndTime) throws IOException;
}
