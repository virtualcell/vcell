package org.vcell.vis.chombo;


public class ChomboMembraneVarData {
	
	private final String name;
	private final String domainName;
	private final double[] data;

	public ChomboMembraneVarData(String name, String domainName, double[] data){
		this.name = name;
		this.domainName = domainName;
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public double[] getRawChomboData() {
		return data;
	}

	public String getDomainName() {
		return domainName;
	}
}
