package org.vcell.vis.vtk;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.vcell.util.exe.Executable2;
import org.vcell.util.exe.ExecutableException;
import org.vcell.util.exe.IExecutable;
import org.vcell.vis.vismesh.thrift.VisMesh;

import cbit.vcell.resource.CondaSupport;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.resource.VCellConfiguration;

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

	@Override
	public void writeMovingBoundaryVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException {
		writeVtkGridAndIndexData("movingboundary", visMesh, domainName, vtkFile, indexFile);
	}

	@Override
	public void writeComsolVtkGridAndIndexData(VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException {
		writeVtkGridAndIndexData("comsolvolume", visMesh, domainName, vtkFile, indexFile);
	}

	private void writeVtkGridAndIndexData(String visMeshType, VisMesh visMesh, String domainName, File vtkFile, File indexFile) throws IOException {
		if (lg.isDebugEnabled()) {
			lg.debug("writeVtkGridAndIndexData (python) for domain "+domainName);
		}
		File PYTHON = CondaSupport.getPythonExe();
		String baseFilename = vtkFile.getName().replace(".vtu",".visMesh");
		File visMeshFile = new File(vtkFile.getParentFile(), baseFilename);
		VisMeshUtils.writeVisMesh(visMeshFile, visMesh);
		File vtkServiceFile = new File(ResourceUtil.getVisToolDir(),"vtkService.py");
		//It's 2015 -- forward slash works for all operating systems
		String[] cmd = new String[] { PYTHON.getAbsolutePath(), vtkServiceFile.getAbsolutePath(),visMeshType,domainName,visMeshFile.getAbsolutePath(),vtkFile.getAbsolutePath(),indexFile.getAbsolutePath() };
		IExecutable exe = prepareExecutable(cmd);
		try {
			exe.start( new int[] { 0 });
			if (exe.getExitValue() != 0){
				throw new RuntimeException("mesh generation script for domain "+domainName+" failed with return code "+exe.getExitValue()+": "+exe.getStderrString());
			}
		} catch (ExecutableException e) {
			e.printStackTrace();
			throw new RuntimeException("vtkService.py invocation failed: "+e.getMessage(),e);
		}
	}

	private IExecutable prepareExecutable(String[] cmd) {
		if (lg.isInfoEnabled()) {
			lg.info("python command string:" + StringUtils.join(cmd," "));
		}
		System.out.println("python command string:" + StringUtils.join(cmd," "));
		Executable2 exe = new Executable2(cmd);
		return exe;
	}

}
