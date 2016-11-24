package org.vcell.solver.comsol.model;

public class VCCFunctionFeature extends VCCBase {
	public enum Type {
		ANALYTIC
	}
	public final VCCFunctionFeature.Type type;
	public final String args[];
	public final String expr;
	public final String[][] plotargs;
	public VCCFunctionFeature(String name, VCCFunctionFeature.Type type, String expr, String[] args, String[][] plotargs){
		super(name);
		this.type = type;
		this.expr = expr;
		this.args = args;
		this.plotargs = plotargs;
	}
}