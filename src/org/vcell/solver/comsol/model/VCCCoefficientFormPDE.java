package org.vcell.solver.comsol.model;

public class VCCCoefficientFormPDE extends VCCPhysics {
	public VCCCoefficientFormPDE(String name, VCCGeomSequence geom, VCCGeomFeature geomFeature) {
		super(name, Type.CoefficientFormPDE, geom, geomFeature);
	}
}