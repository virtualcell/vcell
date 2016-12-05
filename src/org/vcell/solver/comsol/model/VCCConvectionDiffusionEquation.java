package org.vcell.solver.comsol.model;

public class VCCConvectionDiffusionEquation extends VCCPhysics {
	public String sourceTerm_f = null;
	public String diffTerm_c = null;
	public String[] advection_be = null;
	public String fieldName = null;
	public String initial = null;
	public final int dim;
	public VCCConvectionDiffusionEquation(String name, VCCGeomSequence geom, VCCGeomFeature geomFeature, int dim) {
		super(name, Type.ConvectionDiffusionEquation, geom, geomFeature);
		this.dim = dim;
	}
}