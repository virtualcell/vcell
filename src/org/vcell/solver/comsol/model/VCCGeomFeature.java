package org.vcell.solver.comsol.model;

public abstract class VCCGeomFeature extends VCCBase {
	public enum Type {
		Circle,
		Square,
		Difference
	}
	public final VCCGeomFeature.Type type;
	public VCCGeomFeature(String name, VCCGeomFeature.Type type) {
		super(name);
		this.type = type;
	}
}