package org.vcell.api.common;



public class SimDataValuesRepresentation {
	
	public final SimDataVariableValuesRepresentation[] variables;
		
	private SimDataValuesRepresentation(){
		variables = null;
	}
	
	public SimDataVariableValuesRepresentation[] getVariables(){
		return variables;
	}
	
}
