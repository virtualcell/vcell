/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.util.graph;
/**
 * Insert the type's description here.
 * Creation date: (2/10/2002 10:44:41 PM)
 * @author: Jim Schaff
 */
public class Node {
	private final String name;
	private final Object data;

/**
 * Node constructor comment.
 */
public Node(String name) {
	this(name,null);
}


/**
 * Node constructor comment.
 */
public Node(String name, Object argData) {
	if (name==null || name.length()==0){
		throw new IllegalArgumentException("name is either null or empty");
	}
	this.name = name;
	this.data = argData;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:20:36 PM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (obj instanceof Node){
		Node node = (Node)obj;
		if (!node.getName().equals(getName())){
			return false;
		}
		if (data != null && node.data != null){
			if (!data.equals(node.data)){
				return false; // if data is not .equal(), then not equal
			}
		}else if (data != null || node.data != null){
			return false; // if only one data is null, then not equal
		}
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 11:15:03 AM)
 * @return java.lang.Object
 */
public Object getData() {
	return data;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:20:13 PM)
 * @return java.lang.String
 */
public final String getName() {
	return name;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:21:50 PM)
 * @return int
 */
public int hashCode() {
	return name.hashCode() + ((data!=null)?(data.hashCode()):(0));
}

/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:19:51 PM)
 * @return java.lang.String
 */
public String toString() {
	return getName();
}
}
