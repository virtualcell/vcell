package org.vcell.solver.comsol.model;

import java.util.ArrayList;

public abstract class VCCStudyFeature extends VCCBase {
	public enum Type {
		Transient
	}
	public final VCCStudyFeature.Type type;
	public final ArrayList<VCCPhysics> activePhysics = new ArrayList<VCCPhysics>();
	public VCCStudyFeature(String name, VCCStudyFeature.Type type) {
		super(name);
		this.type = type;
	}
}