package cbit.vcell.simdata;

import java.io.File;
import java.io.IOException;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vis.io.VtuFileContainer;

import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.StdoutSessionLog;
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
			NativeLib.HDF5.load();
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

		final SessionLog log = new StdoutSessionLog("DataServer");
		Cachetable cacheTable = new Cachetable(MessageConstants.MINUTE_IN_MS * 20);
		DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(log, cacheTable,
				new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty)),
				new File(PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirProperty)));

		ExportServiceImpl exportServiceImpl = new ExportServiceImpl(log);

		DataServerImpl dataServerImpl = new DataServerImpl(log, dataSetControllerImpl, exportServiceImpl);

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
