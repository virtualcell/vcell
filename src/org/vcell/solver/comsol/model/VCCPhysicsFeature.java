package org.vcell.solver.comsol.model;

public class VCCPhysicsFeature extends VCCBase {
	public enum Type {
		DirichletBoundary,
		InitialConditions
	}
	public final VCCPhysicsFeature.Type type;
	public final int dim;
	public VCCPhysicsFeature(String name, VCCPhysicsFeature.Type type, int dim) {
		super(name);
		this.type = type;
		this.dim = dim;
	}	
}