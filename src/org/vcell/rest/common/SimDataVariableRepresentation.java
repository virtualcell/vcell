package org.vcell.rest.common;


public class SimDataVariableRepresentation {
	
	public String name;
	public boolean isTime;
	public double[] data;
	
	public SimDataVariableRepresentation(){
		
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isTime() {
		return isTime;
	}
	
	public double[] getData(){
		return data;
	}
}
