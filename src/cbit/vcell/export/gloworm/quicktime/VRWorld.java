/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gloworm.quicktime;

import java.util.Vector;

import org.vcell.util.BeanUtils;

import cbit.vcell.export.gloworm.atoms.*;


/**
 * Insert the type's description here.
 * Creation date: (11/8/2005 10:12:09 PM)
 * @author: Ion Moraru
 */
public class VRWorld {
	private Node[] nodes = new Node[0];
	private int duration;
	private abstract class Node {
		abstract String getNodeType();
	};
	private class ObjectNode extends Node {
		private VRObjectSampleAtom nodeSampleAtom;
		private ObjectNode(int columns, int rows, float defaultViewCenterH, float defaultViewCenterV) {
			nodeSampleAtom = new VRObjectSampleAtom(columns, rows, getDuration(), defaultViewCenterH, defaultViewCenterV);
		}
		String getNodeType() {
			return VRAtom.VR_OBJECT_NODE_TYPE;
		}
	};

/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 10:45:31 PM)
 */
private VRWorld() {}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2005 1:05:22 AM)
 * @return VRWorld
 * @param columns int
 * @param rows int
 * @param defaultViewCenterH float
 * @param defaultViewCenterV float
 */
public static VRWorld createSingleObjectVRWorld(int duration, int columns, int rows, float defaultViewCenterH, float defaultViewCenterV) {
	VRWorld vrWorld = new VRWorld();
	vrWorld.duration = duration;
	ObjectNode objNode = vrWorld.new ObjectNode(columns, rows, defaultViewCenterH, defaultViewCenterV);
	vrWorld.nodes = new Node[1];
	vrWorld.nodes[0] = objNode;
	return vrWorld;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 10:44:25 PM)
 * @return int
 */
public int getDuration() {
	return duration;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 10:52:53 PM)
 * @return int
 */
public int getNumberOfNodes() {
	return nodes.length;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 11:16:24 PM)
 * @return int[]
 */
public int[] getObjectNodeIndices() {
	Vector v = new Vector<Integer>();
	for (int i = 0; i < nodes.length; i++){
		if (nodes[i] instanceof ObjectNode) {
			v.add(new Integer(i));
		}
	}
	Integer[] ints = (Integer[])BeanUtils.getArray(v, Integer.class);
	int[] indices = new int[ints.length];
	for (int i = 0; i < indices.length; i++){
		indices[i] = ints[i].intValue();
	}
	return indices;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 10:26:23 PM)
 * @return VRAtomContainer
 */
public VRAtomContainer getVRNodeInfoContainer(int nodeIndex) {
	VRNodeHeaderAtom nodeHeader = new VRNodeHeaderAtom(nodes[nodeIndex].getNodeType());
	return new VRAtomContainer(new VRAtom[] {nodeHeader});
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 10:26:23 PM)
 * @return VRAtomContainer
 */
public VRAtomContainer getVRObjectInfoContainer(int nodeIndex) {
	return new VRAtomContainer(new VRAtom[] {getVRObjectSampleAtom(nodeIndex)});
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/2005 6:02:25 PM)
 * @return VRObjectSampleAtom
 * @param nodeIndex int
 */
public VRObjectSampleAtom getVRObjectSampleAtom(int nodeIndex) {
	ObjectNode objNode = (ObjectNode)nodes[nodeIndex];
	return objNode.nodeSampleAtom;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 10:26:23 PM)
 * @return VRAtomContainer
 */
public VRAtomContainer getVRWorldContainer() {
	VRNodeIDAtom[] nodeIDs = new VRNodeIDAtom[getNumberOfNodes()];
	for (int i = 0; i < nodeIDs.length; i++){
		VRNodeLocationAtom nodeLocation = new VRNodeLocationAtom(nodes[i].getNodeType());
		nodeIDs[i] = new VRNodeIDAtom(new VRNodeLocationAtom[] {nodeLocation}); 
	}
	VRNodeParentAtom nodeParent = new VRNodeParentAtom(nodeIDs);
	VRImagingParentAtom imagingParent = new VRImagingParentAtom();
	VRWorldHeaderAtom header = new VRWorldHeaderAtom();
	VRStringAtom name = new VRStringAtom("The Object");
	return new VRAtomContainer(new VRAtom[] {name, header, imagingParent, nodeParent});
}
}
