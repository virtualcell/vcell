package org.vcell.vis.vtk;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.vis.vismesh.thrift.VisMesh;

public abstract class VtkService {
	public static VtkService vtkService = null;
	protected static final Logger lg = LogManager.getLogger(VtkService.class);

	public static VtkService getInstance(){
		//return new VtkGridUtils();
		return new VtkServicePython();
	}

	public abstract void writeChomboMembraneVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException;

	public abstract void writeChomboVolumeVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException;

	public abstract void writeFiniteVolumeSmoothedVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException;
	
	public abstract void writeMovingBoundaryVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException;
	
	public abstract void writeComsolVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException;
}
