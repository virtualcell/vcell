package org.vcell.vis.vtk;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.vcell.util.Executable2;
import org.vcell.util.ExecutableException;
import org.vcell.vis.vismesh.thrift.VisMesh;
import org.vcell.vis.vismesh.thrift.VarData;

public class VtkServicePython extends VtkService {

	@Override
	public void writeChomboMembraneVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException {
		writeVtkGridAndIndexData("chombomembrane", visMesh, domainName, vtkFile, indexFile);
	}
	
	@Override
	public void writeChomboVolumeVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException {
		writeVtkGridAndIndexData("chombovolume", visMesh, domainName, vtkFile, indexFile);
	}

	@Override
	public void writeFiniteVolumeSmoothedVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException {
		writeVtkGridAndIndexData("finitevolume", visMesh, domainName, vtkFile, indexFile);
	}

	private void writeVtkGridAndIndexData(String visMeshType, VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException {
		System.out.println("writeVtkGridAndIndexData (python) for domain "+domainName);
		String baseFilename = vtkFile.getName().replace(".vtu",".visMesh");
		File visMeshFile = new File(vtkFile.getParentFile(), baseFilename);
		VisMeshUtils.writeVisMesh(visMeshFile, visMesh);
		String[] cmd = new String[] { "python","visTool\\vtkService.py",visMeshType,domainName,visMeshFile.getAbsolutePath(),vtkFile.getAbsolutePath(),indexFile.getAbsolutePath() };
for (String a : cmd){
	System.out.print(a+" ");
}
System.out.println();
		Executable2 exe = new Executable2(cmd);
		try {
			exe.start();
		} catch (ExecutableException e) {
			e.printStackTrace();
			throw new RuntimeException("vtkService.py invocation failed: "+e.getMessage(),e);
		}
	}

	@Override
	public void writeDataArrayToNewVtkFile(File emptyMeshFile, String variableVtuName, double[] data, File newMeshFile) throws IOException {
//		VtkGridUtils vtkGridUtils = new VtkGridUtils();
//		vtkGridUtils.writeDataArrayToNewVtkFile(emptyMeshFile, variableVtuName, data, newMeshFile);
		System.out.println("writeDataArrayToNewVtkFile (python) for variable "+variableVtuName);
		String baseFilename = newMeshFile.getName().replace(".vtu","");
		File varDataFile = File.createTempFile(baseFilename+"_"+variableVtuName+"_", ".vardata", newMeshFile.getParentFile());
		VarData varData = new VarData(variableVtuName, Arrays.asList(ArrayUtils.toObject(data)));
		VisMeshUtils.writeVarData(varDataFile, varData);
		String[] cmd = new String[] { "python","visTool\\vtkAddData.py",varDataFile.getAbsolutePath(),emptyMeshFile.getAbsolutePath(),newMeshFile.getAbsolutePath() };
for (String a : cmd){
	System.out.print(a+" ");
}
System.out.println();
		Executable2 exe = new Executable2(cmd);
		try {
			exe.start();
		} catch (ExecutableException e) {
			e.printStackTrace();
			throw new RuntimeException("vtkAddData.py invocation failed: "+e.getMessage(),e);
		}
	}

}
