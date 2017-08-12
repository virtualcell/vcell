package org.vcell.api.common;



public class SimDataVariableValuesRepresentation {
	
	public final String name;
	public final double[] values;
		
	public SimDataVariableValuesRepresentation(String varName, double[] values) {
		this.name = varName;
		this.values = values;
	}

	public String getName() {
		return name;
	}
	
	public double[] getValues() {
		return values;
	}
	
}
