package cbit.vcell.geometry;

import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;

import cbit.image.ImageException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.render.Vect3d;

@SuppressWarnings("serial")
public class CSGObject extends SubVolume {
	
	private enum CSGNodeType {
		cone("cone"),
		cube("cube"),
		cylinder("cylinder"),
		sphere("sphere"),
		
		difference("difference"),
		intersection("intersection"),
		union("union"),
		
		rotation("rotation"),
		scale("scale"),
		tranlation("translation");
		
		private String label;
		private CSGNodeType(String name) {
			label = name;
		}
	}
	public static final String PROPERTY_NAME_ROOT = "root";
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
		if (getRoot() == null) {
			return false;
		}
		Vect3d point = new Vect3d(x,y,z);
		return getRoot().isInside(point);
	}	
	
	public CSGNode getRoot() {
		return root;
	}	
	
	private CSGNode findCSGNodeByName(CSGNode node, String name) {	
		if (node != null && node.getName().equals(name)) {
			return node;
		}
		if (node instanceof CSGSetOperator) {
			for (CSGNode child : ((CSGSetOperator) node).getChildren()) {
				CSGNode csgNode = findCSGNodeByName(child, name);
				if (csgNode != null) {
					return csgNode;
				}
			}
		} else if (node instanceof CSGTransformation) {
			CSGNode child = ((CSGTransformation) node).getChild();
			CSGNode csgNode = findCSGNodeByName(child, name);
			if (csgNode != null) {
				return csgNode;
			}
		}
		return null;
	}
	
	private String getFreeName(CSGNodeType csgNodeType) {
		int counter = 0;
		while (true) {
			String name = csgNodeType.label + counter;
			CSGNode csgNode = findCSGNodeByName(root, name);
			if (csgNode == null) {
				return name;
			}
			counter ++;
		}
	}
	
	public String getFreeName(CSGPrimitive.PrimitiveType primitiveType) {
		switch (primitiveType) {
		case SOLID_CONE:
			return getFreeName(CSGNodeType.cone);
		case SOLID_CUBE:
			return getFreeName(CSGNodeType.cube);
		case SOLID_CYLINDER:
			return getFreeName(CSGNodeType.cylinder);
		case SOLID_SPHERE:
			return getFreeName(CSGNodeType.sphere);
		}
		return null;
	}
	
	public String getFreeName(CSGSetOperator.OperatorType operatorType) {
		switch (operatorType) {
		case DIFFERENCE:
			return getFreeName(CSGNodeType.difference);
		case INTERSECTION:
			return getFreeName(CSGNodeType.intersection);
		case UNION:
			return getFreeName(CSGNodeType.union);
		}
		return null;
	}
	
	public String getFreeName(CSGTransformation.TransformationType transformationType) {
		switch (transformationType) {
		case Homogeneous:
			throw new RuntimeException("Homogeneous is not supported yet");
		case Rotation:
			return getFreeName(CSGNodeType.rotation);
		case Scale:
			return getFreeName(CSGNodeType.scale);
		case Translation:
			return getFreeName(CSGNodeType.tranlation);
		}
		return null;
	}
	
	public void setRoot(CSGNode newValue) {
		CSGNode oldValue = root;
		this.root = newValue;
		firePropertyChange(PROPERTY_NAME_ROOT, oldValue, newValue);
	}
}
