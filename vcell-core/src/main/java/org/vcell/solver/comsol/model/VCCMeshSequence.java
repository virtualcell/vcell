package org.vcell.solver.comsol.model;

public class VCCMeshSequence extends VCCBase {
	public final VCCGeomSequence geom;
	public VCCMeshSequence(String name, VCCGeomSequence geom) {
		super(name);
		this.geom = geom;
	}
	
}