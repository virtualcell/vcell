package org.vcell.solver.comsol.model;

import java.util.ArrayList;

public abstract class VCCGeomFeature extends VCCBase {
	public enum Type {
		Circle,
		Square,
		
		Difference,
		Union,
		Intersection,
		
		Sphere,
		Cone,
		Cylinder,
		Block,
		
		Rotate,
		Scale,
		Move, 
		
		IntersectionSelection
	}
	public enum Keep {
		on,
		off
	}

	public final VCCGeomFeature.Type type;
	
	
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
	
	public static class VCCSphere extends VCCGeomFeature {
		public final String r;
		public final String[] pos;
		public VCCSphere(String name, String[] pos, String r){
			super(name, Type.Sphere);
			this.r = r;
			this.pos = pos;
		}
	}
	
	public static class VCCCone extends VCCGeomFeature {
		public VCCCone(String name){
			super(name, Type.Cone);
		}
	}
	
	public static class VCCCylinder extends VCCGeomFeature {
		public VCCCylinder(String name){
			super(name, Type.Cylinder);
		}
	}
	
	public static class VCCSquare extends VCCGeomFeature {
		public VCCSquare(String name){
			super(name, Type.Square);
		}
	}
	
	public static class VCCBlock extends VCCGeomFeature {
		public final String[] size;
		public final String[] pos;
		public VCCBlock(String name, String[] size, String[] pos){
			super(name, Type.Block);
			this.size = size;
			this.pos = pos;
		}
	}
	
	public static class VCCDifference extends VCCGeomFeature {
		public final ArrayList<VCCGeomFeature> input = new ArrayList<VCCGeomFeature>();
		public final ArrayList<VCCGeomFeature> input2 = new ArrayList<VCCGeomFeature>();
		public final Keep keep;
		public VCCDifference(String name, Keep keep){
			super(name, Type.Difference);
			this.keep = keep;
		}
	}
	
	public static class VCCUnion extends VCCGeomFeature {
		public final ArrayList<VCCGeomFeature> input = new ArrayList<VCCGeomFeature>();
		public final Keep keep;
		public VCCUnion(String name, Keep keep){
			super(name, Type.Union);
			this.keep = keep;
		}
	}
	
	public static class VCCIntersection extends VCCGeomFeature {
		public final ArrayList<VCCGeomFeature> input = new ArrayList<VCCGeomFeature>();
		public final Keep keep;
		public VCCIntersection(String name, Keep keep){
			super(name, Type.Intersection);
			this.keep = keep;
		}
	}
	
	public static class VCCRotate extends VCCGeomFeature {
		public final ArrayList<VCCGeomFeature> input = new ArrayList<VCCGeomFeature>();
		public final Keep keep;
		public final String[] axis;
		public final String[] pos;
		public final String rot;
		public VCCRotate(String name, String[] axis, String[] pos, String rot, Keep keep){
			super(name, Type.Rotate);
			this.axis = axis;
			this.pos = pos;
			this.rot = rot;
			this.keep = keep;
		}
	}
	
	public static class VCCScale extends VCCGeomFeature {
		public final ArrayList<VCCGeomFeature> input = new ArrayList<VCCGeomFeature>();
		public final String[] pos;
		public final String[] factor;
		public final Keep keep;
		public VCCScale(String name, String[] pos, String[] factor, Keep keep){
			super(name, Type.Scale);
			this.pos = pos;
			this.factor = factor;
			this.keep = keep;
		}
	}
	
	public static class VCCMove extends VCCGeomFeature {
		public final ArrayList<VCCGeomFeature> input = new ArrayList<VCCGeomFeature>();
		public final Keep keep;
		public final String[] displ;
		public VCCMove(String name, String[] displ, Keep keep){
			super(name, Type.Move);
			this.displ = displ;
			this.keep = keep;
		}
	}
	
	public static class VCCIntersectionSelection extends VCCGeomFeature {
		public final ArrayList<VCCGeomFeature> input = new ArrayList<VCCGeomFeature>();
		public final int entitydim;
		public VCCIntersectionSelection(String name, int entitydim){
			super(name, Type.IntersectionSelection);
			this.entitydim = entitydim;
		}
		
	}
	
	
	
	
	
	public VCCGeomFeature(String name, VCCGeomFeature.Type type) {
		super(name);
		this.type = type;
	}
}