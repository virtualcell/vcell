package org.vcell.solver.comsol.model;

import java.util.ArrayList;

public abstract class VCCPhysics extends VCCBase {
	public enum Type {
		CoefficientFormPDE, 
		ConvectionDiffusionEquation
	}
	
	public final VCCPhysics.Type type;
	public final VCCGeomSequence geom;
	public final VCCGeomFeature geomFeature;
	public final ArrayList<VCCPhysicsFeature> features = new ArrayList<VCCPhysicsFeature>();
	public final ArrayList<VCCPhysicsField> fields = new ArrayList<VCCPhysicsField>();
	public VCCPhysics(String name, VCCPhysics.Type type, VCCGeomSequence geom, VCCGeomFeature geomFeature){
		super(name);
		this.type = type;
		this.geom = geom;
		this.geomFeature = geomFeature;
	}		
}