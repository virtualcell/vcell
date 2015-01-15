package org.vcell.vis.chombo;

public class VCellSolution {
	
	private final String name;
	private final double[] data;

	public VCellSolution(String name, double[] data){
		this.name = name;
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public double[] getData() {
		return data;
	}
}
