package cbit.rmi.event.client.common;



public class SimDataValuesRepresentation {
	
	public final SimDataVariableValuesRepresentation[] variables;
		
	private SimDataValuesRepresentation(){
		variables = null;
	}
	
	public SimDataVariableValuesRepresentation[] getVariables(){
		return variables;
	}
	
}
