/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.desktop;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Insert the type's description here.
 * Creation date: (11/28/00 1:04:38 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class BioModelNode extends javax.swing.tree.DefaultMutableTreeNode {
	
	public static class UserNameNode extends BioModelNode {
		public UserNameNode(Object userObject, boolean allowsChildren) {
			super(userObject, allowsChildren);
		}
	}
	public static class PublicationInfoNode extends BioModelNode {
		public PublicationInfoNode(Object userObject, boolean allowsChildren) {
			super(userObject, allowsChildren);
		}
	}
	
	private Hashtable<String, Object> renderHintHash = new Hashtable<String, Object>();

	public static final String MAX_ERROR_LEVEL = "MaxErrorLevel";
	public static final int ERROR_NONE = 0;
	public static final int ERROR_POSSIBLE = 1;
	public static final int ERROR_CONFIRMED = 2;

	public interface NodeMatcher {
		public boolean match(Object obj);
	};

/**
 * BioModelNode constructor comment.
 */
public BioModelNode() {
	super();
}


/**
 * BioModelNode constructor comment.
 * @param userObject java.lang.Object
 */
public BioModelNode(Object userObject) {
	super(userObject);
}


/**
 * BioModelNode constructor comment.
 * @param userObject java.lang.Object
 * @param allowsChildren boolean
 */
public BioModelNode(Object userObject, boolean allowsChildren) {
	super(userObject, allowsChildren);
}

@Override
public BioModelNode clone() {		// shallow clone, no parent, no children
	BioModelNode newNode;
	newNode = (BioModelNode)super.clone();
	newNode.setRenderHintHash((Hashtable<String, Object>)renderHintHash.clone());
	return newNode;
}

public static BioModelNode deepClone(BioModelNode root) {		// recursively clone all children
	BioModelNode newRoot = root.clone();
	for(Enumeration childEnum = root.children(); childEnum.hasMoreElements();) {
		newRoot.add(deepClone((BioModelNode)childEnum.nextElement()));
	}
	return newRoot;
}


/**
 * Insert the method's description here.
 * Creation date: (1/23/01 5:06:39 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param userObject java.lang.Object
 */
public BioModelNode findMatchingNode(NodeMatcher nodeMatcher) {
	
	if (nodeMatcher.match(getUserObject())){
		return this;
	}
	
	for (int i=0;i<getChildCount();i++){
		BioModelNode child = (BioModelNode)getChildAt(i);
		BioModelNode desiredNode = child.findMatchingNode(nodeMatcher);
		if (desiredNode != null){
			return desiredNode;
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (1/23/01 5:06:39 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param userObject java.lang.Object
 */
public BioModelNode findNodeByUserObject(Object argUserObject) {
	
	if (getUserObject().equals(argUserObject)){
		return this;
	}
	
	for (int i=0;i<getChildCount();i++){
		BioModelNode child = (BioModelNode)getChildAt(i);
		BioModelNode desiredNode = child.findNodeByUserObject(argUserObject);
		if (desiredNode != null){
			return desiredNode;
		}
	}
	return null;
}

public Hashtable<String, Object> getRenderHintHash() {
	return renderHintHash;
}
public void setRenderHintHash(Hashtable<String, Object> thatHash) {
	renderHintHash = thatHash;
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2002 10:48:55 AM)
 * @return java.lang.Object
 * @param hintName java.lang.String
 */
public Object getRenderHint(String hintName) {
	return renderHintHash.get(hintName);
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2002 10:50:04 AM)
 * @param hintName java.lang.String
 * @param hintObject java.lang.Object
 */
public void setRenderHint(String hintName, Object hintObject) {
	this.renderHintHash.put(hintName,hintObject);
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:30:19 PM)
 * @return java.lang.String
 */
public String toString() {
	if (userObject == null) {
	    return null;
	} else {
		return userObject.toString() + " " + renderHintHash.toString();
	}
}

}
