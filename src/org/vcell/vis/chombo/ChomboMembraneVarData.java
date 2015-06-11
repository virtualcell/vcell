package org.vcell.vis.chombo;


public class ChomboMembraneVarData {
	
	private final String name;
	private final double[] data;

	public ChomboMembraneVarData(String name, double[] data){
		this.name = name;
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public double[] getRawChomboData() {
		return data;
	}
	
}
