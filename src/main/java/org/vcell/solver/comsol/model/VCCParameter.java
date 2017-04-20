package org.vcell.solver.comsol.model;

public class VCCParameter extends VCCBase {
	public String exp = null;
	public VCCParameter(String name, String exp){
		super(name);
		this.exp = exp;
	}
}