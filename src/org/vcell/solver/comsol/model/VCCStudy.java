package org.vcell.solver.comsol.model;

import java.util.ArrayList;

public class VCCStudy extends VCCBase {
	public String timeStep;
	public String endTime;
	public final ArrayList<VCCStudyFeature> features = new ArrayList<VCCStudyFeature>();
	public VCCStudy(String name) {
		super(name);
	}
}