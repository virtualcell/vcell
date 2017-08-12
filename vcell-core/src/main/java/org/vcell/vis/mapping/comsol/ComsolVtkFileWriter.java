package org.vcell.vis.mapping.comsol;

import java.io.File;
import java.io.IOException;

import org.vcell.solver.comsol.ComsolSolver;
import org.vcell.util.FileUtils;
import org.vcell.vis.io.ComsolSimFiles;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;
import org.vcell.vis.io.VtuVarInfo.DataType;
import org.vcell.vis.vtk.VisMeshUtils;

import cbit.vcell.simdata.OutputContext;

public class ComsolVtkFileWriter {

	public ComsolVtkFileWriter() {
	}

	public double[] getVtuMeshData(ComsolSimFiles comsolSimFiles, OutputContext outputContext, File userDataDirectory, double time, VtuVarInfo var, int timeIndex) throws IOException {
		if (var.dataType != DataType.PointData){
			throw new RuntimeException("expecting request for point data in comsol data");
		}
		File comsolFile = comsolSimFiles.comsoldataFile;
		String prefix = comsolFile.getName().replace(".comsoldat","");
		File inputMeshFile = new File(comsolFile.getParentFile(), prefix+"_"+var.name+"_"+timeIndex+".vtu");
		double[] data = VisMeshUtils.readPointDataFromVtu(inputMeshFile, var.name);
		return data;
	}

	public double[] getVtuTimes(ComsolSimFiles comsolSimFiles, File userDataDirectory) throws IOException {
		return ComsolSolver.getTimesFromLogFile(comsolSimFiles.logFile);
	}

	public VtuFileContainer getEmptyVtuMeshFiles(ComsolSimFiles comsolSimFiles, File primaryDirectory) throws IOException {
		VtuFileContainer vtuFileContainer = new VtuFileContainer();
		File comsolFile = comsolSimFiles.comsoldataFile;
		String prefix = comsolFile.getName().replace(".comsoldat","");
		File emptyMeshFile = new File(comsolFile.getParentFile(), prefix+".vtu");
		byte[] vtuMeshFileContents = FileUtils.readByteArrayFromFile(emptyMeshFile);
		vtuFileContainer.addVtuMesh(new VtuFileContainer.VtuMesh("domain", 0.0, vtuMeshFileContents));
		return vtuFileContainer;
	}

}
