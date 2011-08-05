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
 * Creation date: (2/10/2002 10:45:40 PM)
 * @author: Jim Schaff
 */
public class Edge {
	private Node node1 = null;
	private Node node2 = null;
	private Object data = null;

/**
 * Edge constructor comment.
 */
public Edge(Node node1, Node node2) {
	this(node1,node2,null);
}


/**
 * Edge constructor comment.
 */
public Edge(Node node1, Node node2, Object argData) {
	this.node1 = node1;
	this.node2 = node2;
	this.data = argData;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:15:21 PM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (obj instanceof Edge){
		Edge edge = (Edge)obj;
		if (!edge.getNode1().equals(getNode1())){
			return false;
		}
		if (!edge.getNode2().equals(getNode2())){
			return false;
		}
		if (data != null && !data.equals(edge.data)){
			return false;
		}
		if (edge.data != null && !edge.data.equals(data)){
			return false;
		}
		return true;
	}else{
		return false;
	}
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
 * Creation date: (2/10/2002 11:01:44 PM)
 * @return cbit.vcell.mapping.potential.Node
 */
public Node getNode1() {
	return this.node1;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:01:44 PM)
 * @return cbit.vcell.mapping.potential.Node
 */
public Node getNode2() {
	return this.node2;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:59:51 PM)
 * @return boolean
 * @param node cbit.vcell.mapping.potential.Node
 */
public boolean hasEndPoint(Node node) {
	if (node.equals(node1) || node.equals(node2)){
		return true;
	}else{
		return false;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:17:47 PM)
 * @return int
 */
public int hashCode() {
	return node1.hashCode()+node2.hashCode();
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:11:54 PM)
 * @return boolean
 * @param edge cbit.vcell.mapping.potential.Edge
 */
public boolean isAdjacent(Edge edge) {
	if (edge.equals(this)){
		throw new RuntimeException("checking edge with itself for adjacency");
	}
	if (hasEndPoint(edge.getNode1()) || hasEndPoint(edge.getNode2())){
		return true;
	}else{
		return false;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:01:11 PM)
 * @return boolean
 */
public boolean isLoop() {
	return node1.equals(node2);
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 11:14:39 AM)
 * @param data java.lang.Object
 */
public void setData(Object argData) {
	this.data = argData;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:30:47 PM)
 * @return java.lang.String
 */
public String toString() {
	return "<\""+getData()+"\": "+getNode1()+","+getNode2()+">";
}
}
