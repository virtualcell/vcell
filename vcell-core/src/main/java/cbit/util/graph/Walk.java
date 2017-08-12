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
import java.util.Vector;
/**
 * Insert the type's description here.
 * Creation date: (2/11/2002 12:19:18 AM)
 * @author: Jim Schaff
 */
public class Walk {
	private Vector edgeList = new Vector();

/**
 * Path constructor comment.
 */
public Walk() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 1:16:01 AM)
 * @param walk cbit.vcell.mapping.potential.Walk
 */
public Walk(Walk walk) {
	edgeList = (Vector)walk.edgeList.clone();
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:49:12 PM)
 * @param newNode cbit.vcell.mapping.potential.Node
 */
public void addEdge(Edge newEdge) {
	//
	// "Walks" require consecutive edges to be adjacent.
	//
	if (edgeList.size()!=0){
		Edge previousEdge = (Edge)edgeList.elementAt(edgeList.size()-1);
		if (!previousEdge.isAdjacent(newEdge)){
			throw new RuntimeException("new edge "+newEdge+" not adjacent to previous edge "+previousEdge);
		}
	}
	edgeList.addElement(newEdge);
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 12:24:27 AM)
 * @return boolean
 * @param edge cbit.vcell.mapping.potential.Edge
 */
public boolean contains(Edge edge) {
	return edgeList.contains(edge);
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 12:24:27 AM)
 * @return boolean
 * @param edge cbit.vcell.mapping.potential.Edge
 */
public boolean contains(Node node) {
	for (int i = 0; i < edgeList.size(); i++){
		Edge edge = (Edge)edgeList.elementAt(i);
		if (edge.hasEndPoint(node)){
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 2:37:12 PM)
 * @return cbit.vcell.mapping.potential.Edge[]
 */
public Edge[] getEdges() {
	Edge edges[] = new Edge[edgeList.size()];
	edgeList.copyInto(edges);
	return edges;
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 1:31:54 AM)
 * @return cbit.vcell.mapping.potential.Edge
 */
public Edge getFirstEdge() {
	if (edgeList.size()>0){
		return (Edge)edgeList.elementAt(0);
	}else{
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 1:31:54 AM)
 * @return cbit.vcell.mapping.potential.Edge
 */
public Edge getLastEdge() {
	if (edgeList.size()>0){
		return (Edge)edgeList.elementAt(edgeList.size()-1);
	}else{
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 1:23:27 AM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer stringBuffer = new StringBuffer();
	stringBuffer.append("Walk{");
	boolean bFirst = true;
	for (int i = 0; i < edgeList.size(); i++){
		if (bFirst){
			bFirst=false;
		}else{
			stringBuffer.append(",");
		}
		stringBuffer.append((Edge)edgeList.elementAt(i));
	}
	stringBuffer.append("}");
	return stringBuffer.toString();
}
}
