package org.vcell.solver.comsol.model;

public class VCCExportFeature extends VCCBase {
	public enum Type {
		Data
	}
	
	public final Type type;

	public VCCExportFeature(String name, Type type) {
		super(name);
		this.type = type;
	}

}
