package cbit.vcell.geometry;

import cbit.vcell.render.Affine;

public class CSGHomogeneousTransformation extends CSGTransformation {
		
	public CSGHomogeneousTransformation(Affine forward, Affine inverse){
		super(forward,inverse);
	}
	
	public CSGHomogeneousTransformation(CSGHomogeneousTransformation csgHomogeneousTransformation){
		super(csgHomogeneousTransformation);
		setTransforms(new Affine(csgHomogeneousTransformation.getForwardTransform()),new Affine(csgHomogeneousTransformation.getInverseTransform()));
	}
	
	@Override
	public CSGNode cloneTree() {
		return new CSGHomogeneousTransformation(this);
	}
	
	public void setTransformations(Affine forward, Affine inverse){
		setTransforms(forward, inverse);
	}
}
