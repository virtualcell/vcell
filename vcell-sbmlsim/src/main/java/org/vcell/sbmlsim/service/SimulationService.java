package org.vcell.sbmlsim.service;

import java.io.File;
import java.util.List;

import org.vcell.sbmlsim.api.common.SBMLModel;
import org.vcell.sbmlsim.api.common.SimData;
import org.vcell.sbmlsim.api.common.SimulationInfo;
import org.vcell.sbmlsim.api.common.SimulationSpec;
import org.vcell.sbmlsim.api.common.SimulationStatus;
import org.vcell.sbmlsim.api.common.TimePoints;
import org.vcell.sbmlsim.api.common.VariableInfo;
import org.vcell.sbmlsim.api.common.VcmlToSbmlResults;

public interface SimulationService {

	SimulationInfo computeModel(SBMLModel sbmlModel, SimulationSpec simSpec);

	SimulationStatus getStatus(SimulationInfo simInfo);

	TimePoints getTimePoints(SimulationInfo simInfo) throws Exception;

	List<VariableInfo> getVariableList(SimulationInfo simInfo) throws Exception;

	int sizeX(SimulationInfo simInfo);
	int sizeY(SimulationInfo simInfo);
	int sizeZ(SimulationInfo simInfo);

	SimData getData(SimulationInfo simInfo, VariableInfo var, int t) throws Exception;
	
	public VcmlToSbmlResults getSBML(File vcmlFile, String applicationName, File outputFile) throws Exception;

}
