package cbit.vcell.geometry;

import cbit.vcell.render.Affine;
import cbit.vcell.render.Vect3d;

public abstract class CSGTransformation extends CSGNode {
	private Affine forwardTransform = null;
	private Affine inverseTransform = null;
	private CSGNode child = null;
	
	
	CSGTransformation(Affine forward, Affine inverse){
		this.forwardTransform = forward;
		this.inverseTransform = inverse;
	}
	
	CSGTransformation(){
		this.forwardTransform = new Affine();
		forwardTransform.identity();
		this.inverseTransform = new Affine();
		inverseTransform.identity();
	}
	
	CSGTransformation(CSGTransformation csgTransformation){
		setChild(csgTransformation.getChild().cloneTree());
	}
	
	public Affine getForwardTransform() {
		return forwardTransform;
	}

	public Affine getInverseTransform() {
		return inverseTransform;
	}

	public CSGNode getChild() {
		return child;
	}

	public void setChild(CSGNode child) {
		this.child = child;
	}

	void setTransforms(Affine forward, Affine inverse) {
		this.forwardTransform = forward;
		this.inverseTransform = inverse;
	}

	public boolean isInside(Vect3d point) {
		Vect3d transformedPoint = inverseTransform.mult(point);
		return child.isInside(transformedPoint);
	}
}
