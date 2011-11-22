package cbit.vcell.geometry;

import cbit.vcell.render.Affine;
import cbit.vcell.render.Vect3d;

public class CSGRotation extends CSGTransformation {
	
	private Vect3d axis = null;
	private double rotationRadians = 0;
	
	public CSGRotation(Vect3d axis, double rotationRadians) {
		super();
		this.axis = axis;
		this.rotationRadians = rotationRadians;
		updateTransform(axis, rotationRadians);
	}
	
	public CSGRotation(CSGRotation rotation) {
		super();
		this.axis = new Vect3d(rotation.axis);
		this.rotationRadians = rotation.rotationRadians;
		updateTransform(axis, rotationRadians);
	}
	
	@Override
	public CSGNode cloneTree() {
		return new CSGRotation(this);
	}
	
	private void updateTransform(Vect3d axis, double rotationRadians) {
		Affine forward = new Affine();
		forward.setRotate(axis, rotationRadians);
		Affine inverse = new Affine();
		inverse.setRotate(axis, -rotationRadians);
		setTransforms(forward, inverse);
	}
	
	public Vect3d getAxis() {
		return axis;
	}

	public void setAxis(Vect3d axis) {
		this.axis = axis;
		updateTransform(axis, rotationRadians);
	}

	public double getRotationRadians() {
		return rotationRadians;
	}

	public void setRotationRadians(double rotationRadians) {
		this.rotationRadians = rotationRadians;
		updateTransform(axis, rotationRadians);
	}


}
