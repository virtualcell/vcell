package org.vcell.rest.common;

import java.util.ArrayList;

import cbit.vcell.simdata.DataSetTimeSeries;
import cbit.vcell.simdata.DataSetTimeSeries.VarData;
import cbit.vcell.simdata.ODEDataBlock;
import cbit.vcell.solver.DataProcessingOutput;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.util.ColumnDescription;


public class SimDataValuesRepresentation {
	
	public final SimDataVariableValuesRepresentation[] variables;
		
	public SimDataValuesRepresentation(DataSetTimeSeries dataSetTimeSeries) {
		ArrayList<SimDataVariableValuesRepresentation> simDataVarReps = new ArrayList<SimDataVariableValuesRepresentation>();
		VarData[] varDatas = dataSetTimeSeries.varDatas;
		for (VarData varData : varDatas) {
			SimDataVariableValuesRepresentation simDataVariableRepresentation = new SimDataVariableValuesRepresentation(varData.name, varData.values);
			simDataVarReps.add(simDataVariableRepresentation);
		}
		variables = simDataVarReps.toArray(new SimDataVariableValuesRepresentation[0]);
	}

	public SimDataVariableValuesRepresentation[] getVariables(){
		return variables;
	}
	
}
