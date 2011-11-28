package cbit.vcell.geometry;

import cbit.vcell.render.Vect3d;

public class CSGPrimitive extends CSGNode {
	// all objects fit in unit cube of (-1,-1,-1) to (1,1,1)
	public static enum PrimitiveType {
		CONE,			// (i.e. y^2+z^2 < (0.5*(x-1))^2 and x<1 and x>-1)
		CUBE,			// (i.e. x>=-1 and x<=1 and y>=-1 and y<=1 and z>=-1 and z<=1 
		CYLINDER,		// (i.e. y^2+z^2 < 1 and x<1 and x>-1)
		SPHERE,		// (i.e. x^2+y^2+z^2 <= 1)
	};
	
	private PrimitiveType type = null;

	public CSGPrimitive(String name, PrimitiveType type) {
		super(name);
		this.type = type;
	}

	public CSGPrimitive(CSGPrimitive primitive) {
		this(primitive.getName(), primitive.type);
	}

	@Override
	public CSGNode cloneTree() {
		return new CSGPrimitive(this);
	}
	
	@Override
	public boolean isInside(Vect3d point) {
		// test if inside bounding box
		double x = point.getX();
		double y = point.getY();
		double z = point.getZ();

		if (x<-1.0){
			return false;
		}
		if (x>1.0){
			return false;
		}
		if (y<-1.0){
			return false;
		}
		if (y>1.0){
			return false;
		}
		if (z<-1.0){
			return false;
		}
		if (z>1.0){
			return false;
		}
		switch (type){
		case SPHERE: {
			double radiusSquared = point.lengthSquared();
			return radiusSquared <= 1.0;
		}
		case CYLINDER: {
			// already inside unit cube, so only test inside circle
			double radiusOfCircleSquared = y*y+z*z;
			return radiusOfCircleSquared <= 1.0;
		}
		case CONE: {
			// already inside unit cube, so only test inside circle
			double radius = 0.5*(x-1);
			return y*y+z*z < radius*radius;
		}
		case CUBE: {
			return true;
		}
		default:
			throw new RuntimeException("unknown CSG primative type");
		}
	}

	public PrimitiveType getType() {
		return type;
	}


}
