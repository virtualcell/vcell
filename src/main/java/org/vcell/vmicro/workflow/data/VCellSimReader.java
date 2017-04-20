package org.vcell.vmicro.workflow.data;

import java.io.File;

import org.vcell.util.NullSessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

public class VCellSimReader {

	public static DataIdentifier[] getDataIdentiferListFromVCellSimulationData(File vcellSimLogFile,int jobIndex) throws Exception{
		return
			VCellSimReader.getDataSetControllerImplFromVCellSimulationData(vcellSimLogFile).getDataIdentifiers(null,
				new VCSimulationDataIdentifier(
					VCellSimReader.getVCSimulationIdentifierFromVCellSimulationData(vcellSimLogFile),jobIndex)
			);
	}

	public static DataSetControllerImpl getDataSetControllerImplFromVCellSimulationData(File vcellSimLogFile) throws Exception{
		File primaryDir = vcellSimLogFile.getParentFile();
		return new DataSetControllerImpl(
			new NullSessionLog(),new Cachetable(Cachetable.minute*10),primaryDir,primaryDir);
	}

	public static User getDotUser(){
		String bogusUserName = ".";
		return new User(bogusUserName,new KeyValue("0"));
	}

	public static VCSimulationIdentifier getVCSimulationIdentifierFromVCellSimulationData(File vcellSimLogFile){
		final String SIMID_PREFIX = "SimID_";
		String simulationKeyS =
			vcellSimLogFile.getName().substring(
					SIMID_PREFIX.length(),
					vcellSimLogFile.getName().indexOf('_', SIMID_PREFIX.length()));
		KeyValue simulationKey = new KeyValue(simulationKeyS);
		return new VCSimulationIdentifier(simulationKey,getDotUser());
	
	}

}
