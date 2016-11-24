package org.vcell.solver.comsol.model;

import java.util.ArrayList;

public class VCCGeomSequence extends VCCBase {
	
	public static class VCCCircle extends VCCGeomFeature {
		public final String[] pos;
		public final String r;
		public VCCCircle(String name, String[] pos, String r){
			super(name, Type.Circle);
			this.pos = pos;
			this.r = r;
		}
		public VCCCircle(String name){
			super(name, Type.Circle);
			this.pos = null;
			this.r = null;
		}
	}
	
	public static class VCCSquare extends VCCGeomFeature {
		public VCCSquare(String name){
			super(name, Type.Square);
		}
	}
	
	public static class VCCDifference extends VCCGeomFeature {
		public enum Keep {
			on,
			off
		}
		public final ArrayList<VCCGeomFeature> input = new ArrayList<VCCGeomFeature>();
		public final ArrayList<VCCGeomFeature> input2 = new ArrayList<VCCGeomFeature>();
		public final Keep keep;
		public VCCDifference(String name, Keep keep){
			super(name, Type.Difference);
			this.keep = keep;
		}
	}
	
	
	
	
	public final int dim;
	public ArrayList<VCCGeomFeature> geomfeatures = new ArrayList<VCCGeomFeature>();
	public VCCGeomSequence(String name, int dim) {
		super(name);
		this.dim = dim;
	}
}