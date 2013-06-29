package org.vcell.rest.common;

import java.util.ArrayList;

import cbit.vcell.simdata.DataSetMetadata;


public class SimDataRepresentation {
	
	public String simkey;
	public SimDataVariableRepresentation[] variables;
	
	public SimDataRepresentation(DataSetMetadata dataSetMetadata) {
		ArrayList<SimDataVariableRepresentation> simDataVarReps = new ArrayList<SimDataVariableRepresentation>();
		String[] varNames = dataSetMetadata.getVarNames();
		for (String varName : varNames){
			SimDataVariableRepresentation simDataVariableRepresentation = new SimDataVariableRepresentation(varName);
			simDataVarReps.add(simDataVariableRepresentation);
		}
		variables = simDataVarReps.toArray(new SimDataVariableRepresentation[0]);
		simkey = dataSetMetadata.vcDataIdentifier.getID().split("_")[1];
	}

	public SimDataVariableRepresentation[] getVariables(){
		return variables;
	}
	
	public String getSimkey(){
		return simkey;
	}
	
}
