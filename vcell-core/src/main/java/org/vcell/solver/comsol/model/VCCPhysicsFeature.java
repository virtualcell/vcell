package org.vcell.solver.comsol.model;

public abstract class VCCPhysicsFeature extends VCCBase {
	public enum Type {
		DirichletBoundary,
		InitialConditions,
		FluxBoundary
	}
	public final VCCPhysicsFeature.Type type;
	public final int dim;
	public VCCPhysicsFeature(String name, VCCPhysicsFeature.Type type, int dim) {
		super(name);
		this.type = type;
		this.dim = dim;
	}
	
	public static class VCCFluxBoundary extends VCCPhysicsFeature {
		public final VCCGeomFeature selection;
		public String flux_g;
		public VCCFluxBoundary(String name, VCCGeomFeature selection, int dim){
			super(name, VCCPhysicsFeature.Type.FluxBoundary, dim);
			this.selection = selection;
		}
	}
}