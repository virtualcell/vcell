package cbit.vcell.simdata;

import java.io.File;
import java.io.IOException;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vis.io.VtuFileContainer;

import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * Generate mesh after simulation finishes, where applicable.
 */
public class VtkMeshGenerator implements PortableCommand {
	final String username;
	final String userkey;
	final String simkey;
	final int jobIndex;
	/**
	 * transient to avoid capture by PortableCommand
	 */
	private transient Exception exc = null;

	public VtkMeshGenerator(User owner, KeyValue simKey, int jobIndex) {
		username = owner.getName();
		userkey = owner.getID().toString();
		simkey = simKey.toString();
		this.jobIndex = jobIndex;
	}

	@Override
	public int execute() {
		try {
			// skip loading legacy native HDF5 library if the system is a macos arm64
			// will get runtime errors for Chombo and MovingBoundary until HDF5 is updated
			boolean MacosArm64 = System.getProperty("os.arch").equals("aarch64") && System.getProperty("os.name").equals("Mac OS X");
			if (!MacosArm64) {
				NativeLib.HDF5.load();
			}
			KeyValue u = new KeyValue(userkey);
			User owner = new User(username,u);
			KeyValue simKey = new KeyValue(simkey);
			generateVtkMeshes(owner, simKey, jobIndex);
			return 0;
		} catch (Exception e) {
			exc = e;
			return 1;
		}
	}

	private void generateVtkMeshes(User owner, KeyValue simKey, int jobIndex) throws DataAccessException, IOException {

		VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, owner);
		VCSimulationDataIdentifier vcdataID = new VCSimulationDataIdentifier(vcSimID, jobIndex);

		Cachetable cacheTable = new Cachetable(MessageConstants.MINUTE_IN_MS * 20,1000000L);
		DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(cacheTable,
				new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty)),
				new File(PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirInternalProperty)));

		ExportServiceImpl exportServiceImpl = new ExportServiceImpl();

		DataServerImpl dataServerImpl = new DataServerImpl(dataSetControllerImpl, exportServiceImpl);

//		@SuppressWarnings("unused")
		if (!dataSetControllerImpl.getIsMovingBoundary(vcdataID)){
			//
			// precompute single static vtk mesh
			//
			VtuFileContainer vtuFileContainer = dataServerImpl.getEmptyVtuMeshFiles(owner, vcdataID, 0);
		}else{
			//
			// precompute static vtk mesh for each saved time point
			//
			double[] times = dataServerImpl.getDataSetTimes(owner, vcdataID);
			for (int i=0;i<times.length;i++){
				VtuFileContainer vtuFileContainer = dataServerImpl.getEmptyVtuMeshFiles(owner, vcdataID, i);
			}
		}
	}


	@Override
	public Exception exception() {
		return exc;
	}

}
