package org.vcell.vcellij.api;

import java.util.List;

import org.sbml.jsbml.Model;
import org.scijava.service.SciJavaService;
import org.vcell.util.ClientTaskStatusSupport;

public interface SimulationService extends SciJavaService {

	SimulationInfo computeModel(Model sbmlModel, SimulationSpec simSpec,ClientTaskStatusSupport clientTaskStatusSupport);

	SimulationStatus getStatus(SimulationInfo simInfo);

	List<Double> getTimePoints(SimulationInfo simInfo) throws Exception;

	List<VariableInfo> getVariableList(SimulationInfo simInfo) throws Exception;

	int sizeX(SimulationInfo simInfo);
	int sizeY(SimulationInfo simInfo);
	int sizeZ(SimulationInfo simInfo);

	List<Double> getData(SimulationInfo simInfo, VariableInfo var, int t) throws Exception;

}
