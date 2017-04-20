package org.vcell.solver.comsol.model;

import java.util.ArrayList;

public class VCCGeomSequence extends VCCBase {	
	
	
	
	public final int dim;
	public ArrayList<VCCGeomFeature> geomfeatures = new ArrayList<VCCGeomFeature>();
	public VCCGeomSequence(String name, int dim) {
		super(name);
		this.dim = dim;
	}
}