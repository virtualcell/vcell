package org.vcell.vis.vtk;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.vcell.util.exe.Executable2;
import org.vcell.util.exe.ExecutableException;
import org.vcell.util.exe.IExecutable;
import org.vcell.vis.vismesh.thrift.VisMesh;

import cbit.vcell.resource.PropertyLoader;

public class VtkServicePython extends VtkService {
	private static final String PYTHON_MODULE_PATH;
	/**
	 * path to python exe or wrapper
	 */
	private static final String PYTHON_EXE_PATH;
	/**
	 * path to python script
	 */
	private static final String VIS_TOOL;
	//These aren't going to change, so just read once
	static {
		String pm = null;
		String pe = "python";
		String pv = "visTool";
		try {
			pm = PropertyLoader.getProperty(PropertyLoader.VTK_PYTHON_MODULE_PATH, null);
			pe = PropertyLoader.getProperty(PropertyLoader.VTK_PYTHON_EXE_PATH, pe);
			pv = PropertyLoader.getProperty(PropertyLoader.VIS_TOOL, pv);
		}
		catch (Exception e){ //make fail safe
			lg.warn("error setting PYTHON_PATH",e);
		}
		finally {
			PYTHON_MODULE_PATH = pm;
			PYTHON_EXE_PATH = pe;
			VIS_TOOL=pv;
		}
	}

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
		String baseFilename = vtkFile.getName().replace(".vtu",".visMesh");
		File visMeshFile = new File(vtkFile.getParentFile(), baseFilename);
		VisMeshUtils.writeVisMesh(visMeshFile, visMesh);
		//It's 2015 -- forward slash works for all operating systems
		String[] cmd = new String[] { PYTHON_EXE_PATH,VIS_TOOL+"/vtkService.py",visMeshType,domainName,visMeshFile.getAbsolutePath(),vtkFile.getAbsolutePath(),indexFile.getAbsolutePath() };
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
		if (PYTHON_MODULE_PATH != null) {
			exe.addEnvironmentVariable("PYTHONPATH", PYTHON_MODULE_PATH);
		}
		return exe;
	}

}
