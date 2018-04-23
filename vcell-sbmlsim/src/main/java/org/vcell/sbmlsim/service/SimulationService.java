package org.vcell.sbmlsim.service;

import java.util.List;

import org.sbml.jsbml.Model;
import org.vcell.sbmlsim.api.common.SimulationInfo;
import org.vcell.sbmlsim.api.common.SimulationSpec;
import org.vcell.sbmlsim.api.common.SimulationStatus;
import org.vcell.sbmlsim.api.common.VariableInfo;

public interface SimulationService {

	SimulationInfo computeModel(Model sbmlModel, SimulationSpec simSpec);

	SimulationStatus getStatus(SimulationInfo simInfo);

	List<Double> getTimePoints(SimulationInfo simInfo) throws Exception;

	List<VariableInfo> getVariableList(SimulationInfo simInfo) throws Exception;

	int sizeX(SimulationInfo simInfo);
	int sizeY(SimulationInfo simInfo);
	int sizeZ(SimulationInfo simInfo);

	List<Double> getData(SimulationInfo simInfo, VariableInfo var, int t) throws Exception;

}
