package org.vcell.vis.vtk;

import cbit.vcell.resource.PropertyLoader;
import org.vcell.util.PythonUtils;
import org.vcell.vis.vismesh.thrift.VisMesh;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class VtkServicePython extends VtkService {

	public enum MeshType {
		chombovolume,
		chombomembrane,
		finitevolume,
		movingboundary,
		comsolvolume;
	}

	@Override
	public void writeChomboMembraneVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException, InterruptedException {
		writeVtkGridAndIndexData(MeshType.chombomembrane, visMesh, domainName, vtkFile, indexFile);
	}

	@Override
	public void writeChomboVolumeVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException, InterruptedException {
		writeVtkGridAndIndexData(MeshType.chombovolume, visMesh, domainName, vtkFile, indexFile);
	}

	@Override
	public void writeFiniteVolumeSmoothedVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException, InterruptedException {
		writeVtkGridAndIndexData(MeshType.finitevolume, visMesh, domainName, vtkFile, indexFile);
	}

	@Override
	public void writeMovingBoundaryVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException, InterruptedException {
		writeVtkGridAndIndexData(MeshType.movingboundary, visMesh, domainName, vtkFile, indexFile);
	}

	@Override
	public void writeComsolVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException, InterruptedException {
		writeVtkGridAndIndexData(MeshType.comsolvolume, visMesh, domainName, vtkFile, indexFile);
	}

	private void writeVtkGridAndIndexData(MeshType meshType, VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException, InterruptedException {
		if (lg.isDebugEnabled()) {
			lg.debug("writeVtkGridAndIndexData (python) for domain "+domainName);
		}

		String baseFilename = vtkFile.getName().replace(".vtu",".visMesh");
		File visMeshFile = new File(vtkFile.getParentFile(), baseFilename);
		VisMeshUtils.writeVisMesh(visMeshFile, visMesh);
		callVtkPython(meshType, domainName, visMeshFile.toPath(), vtkFile.getParentFile().toPath(), indexFile.toPath());
	}

	private void callVtkPython(MeshType meshtype, String domainName, Path visMeshFile, Path vtkFile, Path indexFile) throws IOException, InterruptedException {
		File vtkPythonDir = PropertyLoader.getRequiredDirectory(PropertyLoader.vtkPythonDir);
		String[] commands = new String[] {
				meshtype.name(),
				domainName,
				String.valueOf(visMeshFile.toAbsolutePath()),
				String.valueOf(vtkFile.toAbsolutePath()),
				String.valueOf(indexFile.toAbsolutePath()) };
		PythonUtils.callPoetryModule(vtkPythonDir, "vtkService.vtkService", commands);
	}

}
