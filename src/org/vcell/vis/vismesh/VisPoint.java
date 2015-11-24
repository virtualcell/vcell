package org.vcell.vis.vismesh;

import org.vcell.util.Matchable;
import org.vcell.vis.core.Vect3D;

public class VisPoint extends Vect3D {

	public VisPoint(double x, double y, double z) {
		super(x, y, z);
	}

	@Override
	public boolean compareEqual(Matchable obj){
		if (obj instanceof VisPoint){
			return super.compareEqual((VisPoint)obj);
		}
		return false;
	}

}
