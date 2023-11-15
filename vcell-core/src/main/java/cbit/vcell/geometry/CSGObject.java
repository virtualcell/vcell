/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry;

import org.vcell.util.Compare;
import org.vcell.util.Extent;
import org.vcell.util.Matchable;
import org.vcell.util.Origin;
import org.vcell.util.document.KeyValue;

import cbit.image.ImageException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.render.Vect3d;

@SuppressWarnings("serial")
public class CSGObject extends SubVolume {
	
	public static final String PROPERTY_NAME_ROOT = "root";
	private CSGNode root = null;
	
	public CSGObject(KeyValue key, String name, int handle) {
		super(key, name, handle);
	}

	public CSGObject(CSGObject csgObj) {
		super(csgObj.getKey(),csgObj.getName(), csgObj.getHandle());
		root = csgObj.root.clone();
	}

    public static CSGObject createBackgroundObject(Origin vcOrigin, Extent vcExtent, String name, int handle) {
		CSGPrimitive cube = new CSGPrimitive("background", CSGPrimitive.PrimitiveType.CUBE);
		CSGScale scale = new CSGScale("scale",new Vect3d(2*vcExtent.getX(),2*vcExtent.getY(), 2*vcExtent.getZ()));
		CSGTranslation translation = new CSGTranslation("translate", new Vect3d(-vcOrigin.getX(), -vcOrigin.getY(), -vcOrigin.getZ()));
		scale.setChild(translation);
		translation.setChild(cube);
		CSGObject csgObject = new CSGObject(null ,name, handle);
		csgObject.setRoot(scale);
		return csgObject;
    }

    public boolean compareEqual(Matchable obj) {
		if (!compareEqual0(obj)){
			return false;
		}
		if (!(obj instanceof CSGObject)){
			return false;
		}
		CSGObject csgo = (CSGObject)obj;

		if (!Compare.isEqualOrNull(root,csgo.root)){
			return false;
		}

		return true;
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
	
	private String getFreeName(String prefix) {
		int counter = 0;
		while (true) {
			String name = prefix + counter;
			CSGNode csgNode = findCSGNodeByName(root, name);
			if (csgNode == null) {
				return name;
			}
			counter ++;
		}
	}
	
	public String getFreeName(CSGPrimitive.PrimitiveType primitiveType) {
		return getFreeName(primitiveType.name().toLowerCase());
	}
	
	public String getFreeName(CSGSetOperator.OperatorType operatorType) {
		return getFreeName(operatorType.name().toLowerCase());
	}
	
	public String getFreeName(CSGTransformation.TransformationType transformationType) {
		return getFreeName(transformationType.name().toLowerCase());
	}
	
	public void setRoot(CSGNode newValue) {
		CSGNode oldValue = root;
		this.root = newValue;
		firePropertyChange(PROPERTY_NAME_ROOT, oldValue, newValue);
	}
}
