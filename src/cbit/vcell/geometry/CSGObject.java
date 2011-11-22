package cbit.vcell.geometry;

import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;

import cbit.image.ImageException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.render.Vect3d;

public class CSGObject extends SubVolume {
	
	private CSGNode root = null;
	
	public CSGObject(KeyValue key, String name, int handle) {
		super(key, name, handle);
		// TODO Auto-generated constructor stub
	}

	public CSGObject(CSGObject csgObj) {
		super(csgObj.getKey(),csgObj.getName(), csgObj.getHandle());
		setRoot(root.cloneTree());
	}

	public boolean compareEqual(Matchable obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getVCML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInside(double x, double y, double z, GeometrySpec geometrySpec) throws GeometryException, ImageException, ExpressionException {
		Vect3d point = new Vect3d(x,y,z);
		return getRoot().isInside(point);
	}	
	
	public CSGNode getRoot() {
		return root;
	}
	
	public void setRoot(CSGNode root) {
		this.root = root;
	}
}
