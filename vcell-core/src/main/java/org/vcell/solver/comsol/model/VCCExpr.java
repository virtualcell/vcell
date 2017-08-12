package org.vcell.solver.comsol.model;

public class VCCExpr extends VCCBase {
	public final VCCModelNode comp;
	public final String Omega;
	public final VCCGeomSequence geom;
	public final int dim;
	public VCCExpr(String name, VCCModelNode comp, String omega, VCCGeomSequence geom, int dim) {
		super(name);
		this.comp = comp;
		this.Omega = omega;
		this.geom = geom;
		this.dim = dim;
	}
}