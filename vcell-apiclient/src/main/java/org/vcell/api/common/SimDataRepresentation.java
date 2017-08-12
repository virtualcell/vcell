package org.vcell.api.common;



public class SimDataRepresentation {
	
	public String simkey;
	public int scanCount;
	public SimDataVariableRepresentation[] variables;
	
	public SimDataVariableRepresentation[] getVariables(){
		return variables;
	}
	
	public String getSimkey(){
		return simkey;
	}
	
	public int getScanCount(){
		return scanCount;
	}
	
}
