package cbit.vcell.simdata;

import java.io.File;
import java.io.FileNotFoundException;

import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vis.io.VtuFileContainer;

import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

public class VtkMeshGenerator {

	public static void generateVtkMeshes(User owner, KeyValue simKey, int jobIndex) throws FileNotFoundException, DataAccessException {
		
		VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, owner);
		VCSimulationDataIdentifier vcdataID = new VCSimulationDataIdentifier(vcSimID, jobIndex);

		final SessionLog log = new StdoutSessionLog("DataServer");
		Cachetable cacheTable = new Cachetable(MessageConstants.MINUTE_IN_MS * 20);
		DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(log, cacheTable, 
				new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty)), 
				new File(PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirProperty)));
		
		ExportServiceImpl exportServiceImpl = new ExportServiceImpl(log);
		
		DataServerImpl dataServerImpl = new DataServerImpl(log, dataSetControllerImpl, exportServiceImpl);

		VtuFileContainer vtuFileContainer = dataServerImpl.getEmptyVtuMeshFiles(owner, vcdataID);
	}

}
