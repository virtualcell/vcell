package cbit.vcell.geometry;

import cbit.vcell.render.Affine;
import cbit.vcell.render.Vect3d;

public class CSGScale extends CSGTransformation {

	private Vect3d scale = null;

	public CSGScale(Vect3d scale) {
		super();
		this.scale = new Vect3d(scale);
		updateTransform();
	}

	public CSGScale(CSGScale csgScale) {
		super(csgScale);
		scale = new Vect3d(csgScale.scale);
		updateTransform();
	}

	@Override
	public CSGNode cloneTree() {
		return new CSGScale(this);
	}

	public Vect3d getScale() {
		return scale;
	}

	public void setScale(Vect3d scale) {
		this.scale = scale;
		updateTransform();
	}
	
	private void updateTransform(){
		Affine forward = new Affine();
		forward.setScale(scale);
		Affine inverse = new Affine();
		inverse.setScale(new Vect3d(1.0/scale.getX(),1.0/scale.getY(),1.0/scale.getZ()));
		setTransforms(forward, inverse);
	}
}
