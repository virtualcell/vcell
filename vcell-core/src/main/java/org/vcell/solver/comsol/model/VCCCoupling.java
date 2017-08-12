package org.vcell.solver.comsol.model;

public class VCCCoupling extends VCCBase {
	public enum Type {
		Integration
	}
	public final VCCCoupling.Type type;
	public final VCCGeomSequence geom;
	public VCCCoupling(String name, VCCCoupling.Type type, VCCGeomSequence geom) {
		super(name);
		this.type = type;
		this.geom = geom;
	}
}