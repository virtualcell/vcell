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
/**
 * Insert the type's description here.
 * Creation date: (10/3/2003 1:55:05 PM)
 * @author: John Wagner
 */
public class GaussianSmoothing {
/**
 * GaussianSmoothing constructor comment.
 */
public GaussianSmoothing() {
	super();
}


// Returns an array of doubles of dimension 3*surfaceCollection.getNodeCount()
// laid out like X0, Y0, Z0, X1, Y1, Z1, ..., Xn, Yn, Zn.
public static double[] calculateLaplacian(SurfaceCollection surfaceCollection) {
	double[] delta = new double[3*surfaceCollection.getNodeCount()];
	int[] weights = new int[surfaceCollection.getNodeCount()];
	for (int i = 0; i < surfaceCollection.getNodeCount(); i++) {
		delta[3*i] = delta[3*i+1] = delta[3*i+2] = 0.0;
		weights[i] = 0;
	}
	for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
		Surface surface = surfaceCollection.getSurfaces(i);
		for (int j = 0; j < surface.getPolygonCount(); j++) {
			Polygon polygon = surface.getPolygons(j);
			for (int k = 0; k < polygon.getNodeCount(); k++) {
				Node nodeA = polygon.getNodes(k);
				Node nodeB = polygon.getNodes((k + 1) % polygon.getNodeCount());
				int nA = nodeA.getGlobalIndex();
				int nB = nodeB.getGlobalIndex();
				//
				delta[3*nA  ] += nodeB.getX() - nodeA.getX();
				delta[3*nA+1] += nodeB.getY() - nodeA.getY();
				delta[3*nA+2] += nodeB.getZ() - nodeA.getZ();
				weights[nA] += 1;
				//
				delta[3*nB  ] += nodeA.getX() - nodeB.getX();
				delta[3*nB+1] += nodeA.getY() - nodeB.getY();
				delta[3*nB+2] += nodeA.getZ() - nodeB.getZ();
				weights[nB] += 1;
			}
		}
	}
	//
	for (int i = 0; i < surfaceCollection.getNodeCount(); i++) {
		Node node = surfaceCollection.getNodes(i);
		delta[3*i  ] = delta[3*i  ]/weights[i];
		delta[3*i+1] = delta[3*i+1]/weights[i];
		delta[3*i+2] = delta[3*i+2]/weights[i];
	}
	//
	return(delta);
}


public static void smooth(SurfaceCollection surfaceCollection, double lambda) {
	double[] delta = calculateLaplacian(surfaceCollection);
	//
	for (int i = 0; i < surfaceCollection.getNodeCount(); i++) {
		Node node = surfaceCollection.getNodes(i);
		if (node.getMoveX()) node.setX(node.getX() + lambda*delta[3*i  ]);
		if (node.getMoveY()) node.setY(node.getY() + lambda*delta[3*i+1]);
		if (node.getMoveZ()) node.setZ(node.getZ() + lambda*delta[3*i+2]);
	}
}
}
