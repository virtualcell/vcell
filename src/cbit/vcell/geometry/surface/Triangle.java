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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.vcell.util.ProgrammingException;

import cbit.vcell.geometry.concept.PolygonImmutable;
import cbit.vcell.geometry.concept.ThreeSpacePoint;

/**
 * Insert the type's description here.
 * Creation date: (6/29/2003 1:10:38 AM)
 * @author: John Wagner
 */
@SuppressWarnings("serial")
public class Triangle extends AbstractPolygon {
/**
 * Triangle constructor comment.
 * @param nodes surface.Node[]
 */
public Triangle(Node[] nodes) {
	super(nodes);
}


/**
 * Triangle constructor comment.
 * @param nodes surface.Node[]
 */
public Triangle(Node n0, Node n1, Node n2) {
	super(new Node[] {n0, n1, n2});
}


/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 2:14:34 PM)
 * @return double
 */
public double getArea() {
	//
	// triangles are always convex and planar ... just use half the cross product.
	//
	double area = 0;
	Node node_0 = getNodes(0);
	Node node_1 = getNodes(1);
	Node node_2 = getNodes(2);
	if(node_2 != null)
	{
		double vect1x = node_0.getX()-node_1.getX(); 
		double vect1y = node_0.getY()-node_1.getY(); 
		double vect1z = node_0.getZ()-node_1.getZ();
		double vect2x = node_0.getX()-node_2.getX(); 
		double vect2y = node_0.getY()-node_2.getY(); 
		double vect2z = node_0.getZ()-node_2.getZ(); 
		double crossx = vect1y*vect2z - vect1z*vect2y;
		double crossy = -(vect1x*vect2z - vect1z*vect2x);
		double crossz = vect1x*vect2y - vect1y*vect2x;
		area = Math.sqrt(crossx*crossx + crossy*crossy + crossz*crossz)/2.0;
	}
	else//two dimensional geometry assume z dimension to be unity
	{
		double vect1x = node_0.getX()-node_1.getX(); 
		double vect1y = node_0.getY()-node_1.getY(); 
		double vect1z = node_0.getZ()-node_1.getZ();
		if(vect1z != 0)
		{
			System.err.println("Expecting 2D triangle to be in line segament X,Y plane from node0 to node1");
		}
		area = Math.sqrt(vect1x*vect1x + vect1y*vect1y);
	}
	return area;
}


/**
 * Insert the method's description here.
 * Creation date: (5/19/2004 10:37:31 AM)
 * @return boolean
 */
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
	// given edge lengths a, b, c, where c is longest,
	// c = a + b for a right triangle, and c < a + b for an acute triangle
	//
	if (edge_length_squared[longestEdgeIndex] < edge_length_squared[shortIndex1] + edge_length_squared[shortIndex2]){
		return true;
	}else{
		return false;
	}
}

/**
 * scale and translate set of triangles. Scaling is from origin.
 * @param source not null
 * @param factor
 * @param translation not null
 * @return new list of new Triangles, scaled and translated from source
 */
public static List<Triangle> scale(List<PolygonImmutable> source, double factor, ThreeSpacePoint translation) {
	Scaler s = new Scaler(factor,translation);
	return s.convert(source);
}

private static class Scaler {
	final double factor;
	final ThreeSpacePoint translation;
	final Map<ThreeSpacePoint, Node> map;

	Scaler(double factor, ThreeSpacePoint translation) {
		super();
		this.factor = factor;
		this.translation = translation;
		map = new HashMap<>();
	}
	
	/**
	 * @param tsp
	 * @return new or existing scaled node
	 */
	private Node fetch(ThreeSpacePoint tsp) {
		Objects.requireNonNull(tsp);
		Node n = map.get(tsp);
		if (n == null) {
			n = new Node(tsp);
			n.multiply(factor);
			n.translate(translation);
			map.put(tsp, n);
		}
		return n;
	}

	/**
	 * @param source not null
	 * @return new List of new Triangles, scaled per constructor arguments
	 */
	List<Triangle> convert(List<PolygonImmutable> source) {
		ArrayList<Triangle> rval = new ArrayList<>(source.size());
		for (PolygonImmutable pi: source) {
			List<ThreeSpacePoint> points = pi.pointList();
			if (points.size() == 3) {
				Node a = fetch(points.get(0));
				Node b = fetch(points.get(1));
				Node c = fetch(points.get(2));
				rval.add(new Triangle(a,b,c));
			}
			else {
				throw new ProgrammingException("Triangle::scale input polygons must have 3 points, " + points.size() 
						+ " found");
			}

		}
	return rval;
	}
}

}
