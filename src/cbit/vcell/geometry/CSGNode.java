package cbit.vcell.geometry;

import cbit.vcell.render.Vect3d;

public abstract class CSGNode {

	public abstract boolean isInside(Vect3d point);
	public abstract CSGNode cloneTree();

}
