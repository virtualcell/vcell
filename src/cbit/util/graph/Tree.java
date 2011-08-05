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
 * Creation date: (2/14/2002 1:26:31 AM)
 * @author: Jim Schaff
 */
public class Tree extends Graph {
/**
 * Tree constructor comment.
 */
Tree() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 12:52:45 AM)
 * @return cbit.vcell.mapping.potential.Path
 * @param node1 cbit.vcell.mapping.potential.Node
 * @param node2 cbit.vcell.mapping.potential.Node
 */
public Path getTreePath(Node currentNode, Node endNode) {
	return getTreePath(currentNode,endNode,new Path());
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 12:52:45 AM)
 * @return cbit.vcell.mapping.potential.Path
 * @param node1 cbit.vcell.mapping.potential.Node
 * @param node2 cbit.vcell.mapping.potential.Node
 */
private Path getTreePath(Node currentNode, Node endNode, Path currentPath) {
	//
	// only called if graph is a tree (no cycles).
	// trees have unique paths for any pair of nodes.
	//
	Edge adjacentEdges[] = getAdjacentEdges(currentNode);
	for (int i = 0; i < adjacentEdges.length; i++){
		Edge nextEdge = adjacentEdges[i];
		if (currentPath.contains(nextEdge)){
			continue;
		}
		Node nextNode = (nextEdge.getNode1().equals(currentNode))?(nextEdge.getNode2()):(nextEdge.getNode1());
		Path nextPath = new Path(currentPath,nextEdge);
		if (nextNode.equals(endNode)){
			//
			// done with search, add to list and return
			//
			return nextPath;
		}else{
			//
			// pass the search to each (non-terminal) adjacent edge
			//
			Path resultingPath = getTreePath(nextNode,endNode,nextPath);
			if (resultingPath!=null){
				return resultingPath;
			}
		}
	}
	//
	// can't find it from the "current" edge (dead end).
	//
	return null;
}
}
