package cbit.vcell.geometry;

import cbit.vcell.render.Affine;
import cbit.vcell.render.Vect3d;

public class CSGTranslation extends CSGTransformation {
	
	public Vect3d translation = null;

	public CSGTranslation(String name, Vect3d translation){
		super(name);
		this.translation = translation;
		updateTransformation();
	}
	
	public CSGTranslation(CSGTranslation csgTranslation){
		super(csgTranslation);
		this.translation = new Vect3d(csgTranslation.getTranslation());
		updateTransformation();
	}
	
	@Override
	public CSGNode cloneTree() {
		return new CSGTranslation(this);
	}

	public Vect3d getTranslation() {
		return translation;
	}

	public void setTranslation(Vect3d translation) {
		this.translation = translation;
	}
	
	private void updateTransformation(){
		Affine forward = new Affine();
		forward.setTranslate(translation);
		Affine inverse = new Affine();
		inverse.setTranslate(translation.uminus());
		setTransforms(forward,inverse);
	}

}
