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
 * Creation date: (10/3/2003 1:51:29 PM)
 * @author: John Wagner
 */
public class TaubinSmoothing extends GaussianSmoothing {
/**
 * TaubinSmoothingSpecification constructor comment.
 */
public TaubinSmoothing() {
	super();
}
public void smooth(SurfaceCollection surfaceCollection, TaubinSmoothingSpecification taubinSmoothingSpecification) {
	//long t1 = System.currentTimeMillis();
	if (taubinSmoothingSpecification != null) {
		for (int iterations = 1; iterations <= taubinSmoothingSpecification.getIterations(); iterations++) {
			double lambda = taubinSmoothingSpecification.getLambda();
			double[] dLambda = calculateLaplacian(surfaceCollection);
			for (int i = 0; i < surfaceCollection.getNodeCount(); i++) {
				Node node = surfaceCollection.getNodes(i);
				if (node.getMoveX()) node.setX(node.getX() + lambda*dLambda[3*i  ]);
				if (node.getMoveY()) node.setY(node.getY() + lambda*dLambda[3*i+1]);
				if (node.getMoveZ()) node.setZ(node.getZ() + lambda*dLambda[3*i+2]);
			}
			//
			double mu = taubinSmoothingSpecification.getLambda();
			double[] dMu = calculateLaplacian(surfaceCollection);
			for (int i = 0; i < surfaceCollection.getNodeCount(); i++) {
				Node node = surfaceCollection.getNodes(i);
				if (node.getMoveX()) node.setX(node.getX() + mu*dMu[3*i  ]);
				if (node.getMoveY()) node.setY(node.getY() + mu*dMu[3*i+1]);
				if (node.getMoveZ()) node.setZ(node.getZ() + mu*dMu[3*i+2]);
			}
		}
	}
	//long t2 = System.currentTimeMillis();
	//System.out.println("smoothing time = "+(t2-t1)/1000.0+" sec");
}
}
