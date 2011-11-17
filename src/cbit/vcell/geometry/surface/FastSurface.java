/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.surface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Insert the type's description here.
 * Creation date: (5/14/2004 5:09:39 PM)
 * @author: Jim Schaff
 */
//
// We're going with John Wagners optimized design that usings Node pointers rather than indexes 
//   (in this case, no need for indirection and the Node.equals() implementation same as ==, so same symantics as a single list).
//   there is no built in check for two nodes with same coordinate though.
//
// The approach is "Trust but Verify" (assume everything is ok, but put in "ASSERT()" type methods that verify constraints ...
//   for performance, these methods can be disabled at will.
//
//
@SuppressWarnings("serial")
public class FastSurface implements Surface, java.io.Serializable {
	private int fieldInteriorRegionIndex = 0;
	private int fieldExteriorRegionIndex = 0;


	// masks are temporary and are used during algorithms.
	// the ray-tracer assigns interior/exterior masks to (1<<(2*N) and 1<<(2*N+1)) for surface N.
	private long interiorMask = 0;
	private long exteriorMask = 0;

	public static boolean bVerify = true;
	private HashMap<Node, String> nodePolygonInstanceMap = new HashMap<Node, String>();
	private ArrayList<FastPolygon> polygonList = new ArrayList<FastPolygon>();
	
	public long getInteriorMask() {
		return interiorMask;
	}
	public long getExteriorMask() {
		return exteriorMask;
	}
	public void setInteriorMask(long mask) {
		this.interiorMask = mask;
	}
	public void setExteriorMask(long mask) {
		this.exteriorMask = mask;
	}

	//private class Edge {
		//private Node node1 = null;
		//private Node node2 = null;
	//}
	
	public abstract class FastPolygon implements Polygon {
		Node nodes[] = null;
		
		FastPolygon(Node argNodes[]){
			nodes = argNodes;
		}
			
		public Node getNodes(int index){
			return nodes[index];
		}
		
		public Node[] getNodes(){
			return nodes;
		}
	}
	
	public class FastTriangle extends FastPolygon {
		FastTriangle(Node node0, Node node1, Node node2) {
			super(new Node[] { node0, node1, node2 });
		}
		FastTriangle(Node[] argNodes) {
			super(argNodes);
		}
		public double getArea() {
			Node node_0 = nodes[0];
			Node node_1 = nodes[1];
			Node node_2 = nodes[2];
			double vect1x = node_0.getX()-node_1.getX(); 
			double vect1y = node_0.getY()-node_1.getY(); 
			double vect1z = node_0.getZ()-node_1.getZ();
			double vect2x = node_0.getX()-node_2.getX(); 
			double vect2y = node_0.getY()-node_2.getY(); 
			double vect2z = node_0.getZ()-node_2.getZ(); 
			double crossx = vect1y*vect2z - vect1z*vect2y;
			double crossy = -(vect1x*vect2z - vect1z*vect2x);
			double crossz = vect1x*vect2y - vect1y*vect2x;
			return Math.sqrt(crossx*crossx + crossy*crossy + crossz*crossz)/2.0;
		}
	public void getUnitNormal(cbit.vcell.render.Vect3d unitNormal) {
		Node node_0 = nodes[0];
		Node node_1 = nodes[1];
		Node node_2 = nodes[2];
		double vect1x = node_0.getX()-node_1.getX(); 
		double vect1y = node_0.getY()-node_1.getY(); 
		double vect1z = node_0.getZ()-node_1.getZ();
		double vect2x = node_0.getX()-node_2.getX(); 
		double vect2y = node_0.getY()-node_2.getY(); 
		double vect2z = node_0.getZ()-node_2.getZ(); 
		double crossx = vect1y*vect2z - vect1z*vect2y;
		double crossy = -(vect1x*vect2z - vect1z*vect2x);
		double crossz = vect1x*vect2y - vect1y*vect2x;
		double length = Math.sqrt(crossx*crossx + crossy*crossy + crossz*crossz);
		unitNormal.set(crossx/length,crossy/length,crossz/length);
	}
		public void reverseDirection() {
			Node temp = nodes[1];
			nodes[1] = nodes[2];
			nodes[2] = temp;
		}
		public int getNodeCount() {
			return 3;
		}
		public boolean isAcute() {
			Node nodes[] = getNodes();
			double edge_length_squared[] = new double[] {
				// edge 0-1
				(nodes[0].getX()-nodes[1].getX())*(nodes[0].getX()-nodes[1].getX()) + 
				(nodes[0].getY()-nodes[1].getY())*(nodes[0].getY()-nodes[1].getY()) + 
				(nodes[0].getZ()-nodes[1].getZ())*(nodes[0].getZ()-nodes[1].getZ()),
				// edge 0-2
				(nodes[0].getX()-nodes[2].getX())*(nodes[0].getX()-nodes[2].getX()) + 
				(nodes[0].getY()-nodes[2].getY())*(nodes[0].getY()-nodes[2].getY()) + 
				(nodes[0].getZ()-nodes[2].getZ())*(nodes[0].getZ()-nodes[2].getZ()),
				// edge 1-2
				(nodes[1].getX()-nodes[2].getX())*(nodes[1].getX()-nodes[2].getX()) + 
				(nodes[1].getY()-nodes[2].getY())*(nodes[1].getY()-nodes[2].getY()) + 
				(nodes[1].getZ()-nodes[2].getZ())*(nodes[1].getZ()-nodes[2].getZ())
			};
			//
			// find index of longest edge
			//
			int longestEdgeIndex = 0;
			if (edge_length_squared[1] > edge_length_squared[longestEdgeIndex]){
				longestEdgeIndex = 1;
			}
			if (edge_length_squared[2] > edge_length_squared[longestEdgeIndex]){
				longestEdgeIndex = 2;
			}
			int shortIndex1 = (longestEdgeIndex+1)%3;
			int shortIndex2 = (longestEdgeIndex+2)%3;
			
			//
			// given edge squared lengths a, b, c, where c is longest,
			// c/(a+b) = 1 for a right triangle, and c/(a+b) < 1 for an acute triangle
			//
			double ratio = edge_length_squared[longestEdgeIndex]/(edge_length_squared[shortIndex1] + edge_length_squared[shortIndex2]);
			if (ratio < 1) {
				return true;
			}else{
				return false;
			}
		}
		public Node[] getLongestEdge() {
			Node nodes[] = getNodes();
			double edge_length_squared[] = new double[] {
				// edge 0-1
				(nodes[0].getX()-nodes[1].getX())*(nodes[0].getX()-nodes[1].getX()) + 
				(nodes[0].getY()-nodes[1].getY())*(nodes[0].getY()-nodes[1].getY()) + 
				(nodes[0].getZ()-nodes[1].getZ())*(nodes[0].getZ()-nodes[1].getZ()),
				// edge 0-2
				(nodes[0].getX()-nodes[2].getX())*(nodes[0].getX()-nodes[2].getX()) + 
				(nodes[0].getY()-nodes[2].getY())*(nodes[0].getY()-nodes[2].getY()) + 
				(nodes[0].getZ()-nodes[2].getZ())*(nodes[0].getZ()-nodes[2].getZ()),
				// edge 1-2
				(nodes[1].getX()-nodes[2].getX())*(nodes[1].getX()-nodes[2].getX()) + 
				(nodes[1].getY()-nodes[2].getY())*(nodes[1].getY()-nodes[2].getY()) + 
				(nodes[1].getZ()-nodes[2].getZ())*(nodes[1].getZ()-nodes[2].getZ())
			};
			//
			// find index of longest edge
			//
			int longestEdgeIndex = 0;
			if (edge_length_squared[1] > edge_length_squared[longestEdgeIndex]){
				longestEdgeIndex = 1;
			}
			if (edge_length_squared[2] > edge_length_squared[longestEdgeIndex]){
				longestEdgeIndex = 2;
			}
			switch (longestEdgeIndex){
				case 0: {
					return new Node[] { nodes[0], nodes[1] };
				}
				case 1: {
					return new Node[] { nodes[0], nodes[2] };
				}
				case 2: {
					return new Node[] { nodes[1], nodes[2] };
				}
				default: {
					throw new RuntimeException("failed to find longest edge");
				}
			}
		}
	}		
	public class FastQuad extends FastPolygon {
		FastQuad(Node node0, Node node1, Node node2, Node node3) {
			super(new Node[] { node0, node1, node2, node3 });
		}
		FastQuad(Node[] argNodes) {
			super(argNodes);
		}
		public double getArea() {
			// assume convex
			Node node_0 = nodes[0];
			Node node_1 = nodes[1];
			Node node_2 = nodes[2];
			Node node_3 = nodes[3];
			double vect1x = node_0.getX()-node_1.getX(); 
			double vect1y = node_0.getY()-node_1.getY(); 
			double vect1z = node_0.getZ()-node_1.getZ();
			double vect2x = node_0.getX()-node_2.getX(); 
			double vect2y = node_0.getY()-node_2.getY(); 
			double vect2z = node_0.getZ()-node_2.getZ(); 
			double vect3x = node_0.getX()-node_3.getX(); 
			double vect3y = node_0.getY()-node_3.getY(); 
			double vect3z = node_0.getZ()-node_3.getZ(); 
			double cross12x = vect1y*vect2z - vect1z*vect2y;
			double cross12y = -(vect1x*vect2z - vect1z*vect2x);
			double cross12z = vect1x*vect2y - vect1y*vect2x;
			double cross23x = vect2y*vect3z - vect2z*vect3y;
			double cross23y = -(vect2x*vect3z - vect2z*vect3x);
			double cross23z = vect2x*vect3y - vect2y*vect3x;
			return (Math.sqrt(cross12x*cross12x + cross12y*cross12y + cross12z*cross12z) + 
				    Math.sqrt(cross23x*cross23x + cross23y*cross23y + cross23z*cross23z))/2.0;
		}
		public void getUnitNormal(cbit.vcell.render.Vect3d unitNormal) {
			// assume convex
			Node node_0 = nodes[0];
			Node node_1 = nodes[1];
			Node node_2 = nodes[2];
			double vect1x = node_0.getX()-node_1.getX(); 
			double vect1y = node_0.getY()-node_1.getY(); 
			double vect1z = node_0.getZ()-node_1.getZ();
			double vect2x = node_0.getX()-node_2.getX(); 
			double vect2y = node_0.getY()-node_2.getY(); 
			double vect2z = node_0.getZ()-node_2.getZ(); 
			double cross12x = vect1y*vect2z - vect1z*vect2y;
			double cross12y = -(vect1x*vect2z - vect1z*vect2x);
			double cross12z = vect1x*vect2y - vect1y*vect2x;
			double length = Math.sqrt(cross12x*cross12x + cross12y*cross12y + cross12z*cross12z);
			unitNormal.set(cross12x/length,cross12y/length,cross12z/length);
		}	
		public void reverseDirection() {
			Node temp = nodes[1];
			nodes[1] = nodes[3];
			nodes[3] = temp;
		}
		public int getNodeCount() {
			return 4;
		}
	}		

/**
 * FastSurface constructor comment.
 */
public FastSurface(int argInteriorRegionIndex, int argExteriorRegionIndex) {
	super();
	fieldInteriorRegionIndex = argInteriorRegionIndex;
	fieldExteriorRegionIndex = argExteriorRegionIndex;
}


/**
 * FastSurface constructor comment.
 */
public FastSurface(int argInteriorRegionIndex, int argExteriorRegionIndex, Polygon argPolygon) {
	this(argInteriorRegionIndex,argExteriorRegionIndex);
	addPolygon(argPolygon);
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:12:53 PM)
 * @param polygon cbit.vcell.geometry.surface.Polygon
 */
private void addPolygon(Polygon polygon) {
	Node nodes[] = polygon.getNodes();
	if (bVerify){
		verifyNoDuplicateNodes(nodes);
	}
	if (nodes.length==3){
		polygonList.add(new FastTriangle((Node[])nodes.clone()));
	}else if (nodes.length==4){
		polygonList.add(new FastQuad((Node[])nodes.clone()));
	}else{
		throw new IllegalArgumentException("FastSurface only supports triangles and quads");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:09:39 PM)
 * @param newSurface cbit.vcell.geometry.surface.Surface
 */
public void addSurface(Surface newSurface) {
	for (int i = 0; i < newSurface.getPolygonCount(); i++){
		addPolygon(newSurface.getPolygons(i));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:09:39 PM)
 * @return double
 */
public double getArea() {
	double area = 0.0;
	int N = getPolygonCount();
	for (int i = 0; i < N; i++){
		area += getPolygons(i).getArea();
	}
	return area;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:09:39 PM)
 * @return int
 */
public int getExteriorRegionIndex() {
	return fieldExteriorRegionIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (5/16/2004 12:19:10 PM)
 * @return cbit.vcell.geometry.surface.Node[]
 */
public Node[] getfindAllNodes() {
	HashSet<Node> nodeSet = new HashSet<Node>();
	for (int i = 0; i < polygonList.size(); i++){
		FastPolygon poly = (FastPolygon)polygonList.get(i);
		for (int j = 0; j < poly.nodes.length; j++){
			nodeSet.add(poly.nodes[j]);
		}
	}
	return nodeSet.toArray(new Node[0]);
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:09:39 PM)
 * @return int
 */
public int getInteriorRegionIndex() {
	return fieldInteriorRegionIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:09:39 PM)
 * @return int
 */
public int getPolygonCount() {
	return polygonList.size();
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:09:39 PM)
 * @return cbit.vcell.geometry.surface.Polygon
 * @param index int
 */
public Polygon getPolygons(int index) {
	return (Polygon)polygonList.get(index);
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:09:39 PM)
 */
public void reverseDirection() {
	int regionIndex = fieldInteriorRegionIndex;
	fieldInteriorRegionIndex = fieldExteriorRegionIndex;
	fieldExteriorRegionIndex = regionIndex;
	for (int i = 0; i < getPolygonCount(); i++) {
		getPolygons(i).reverseDirection();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/16/2004 2:21:39 PM)
 * @param nodes cbit.vcell.geometry.surface.Node[]
 */
private void verifyNoDuplicateNodes(Node[] nodes) {
	for (int j = 0; j < nodes.length; j++){
		//
		// check if node already present with same coordinates.
		//
		String coordinateString = "("+nodes[j].getX()+","+nodes[j].getY()+","+nodes[j].getZ()+")";
		if (nodePolygonInstanceMap.containsValue(coordinateString)){
			//
			// if so, node instance should already be present
			//
			if (!nodePolygonInstanceMap.containsKey(nodes[j])){
				throw new RuntimeException("duplicate nodes with coordinates: "+coordinateString);
			}
		}
	}
}


}
