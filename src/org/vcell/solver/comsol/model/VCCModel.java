package org.vcell.solver.comsol.model;

import java.util.ArrayList;

public class VCCModel {
	
	public ArrayList<VCCParameter> parameters = new ArrayList<VCCParameter>();
	public ArrayList<VCCModelNode> modelnodes = new ArrayList<VCCModelNode>();
	public ArrayList<VCCGeomSequence> geometrysequences = new ArrayList<VCCGeomSequence>();
	public ArrayList<VCCFunctionFeature> functionfeatures = new ArrayList<VCCFunctionFeature>();
	public ArrayList<VCCMeshSequence> meshes = new ArrayList<VCCMeshSequence>();
	public ArrayList<VCCExpr> variables = new ArrayList<VCCExpr>();
	public ArrayList<VCCCoupling> couplings = new ArrayList<VCCCoupling>();
	public ArrayList<VCCPhysics> physics = new ArrayList<VCCPhysics>();
	public VCCStudy study;
	public VCCResults results = new VCCResults();
	public String modelpath = null;
	public String name = null;
	public String label = null;
	public String comments = null;
	
	public VCCModel(String name) {
		this.name = name;
	}
	
}
