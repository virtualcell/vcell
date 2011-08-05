/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.graphlayout;

import java.util.Random;

import org.vcell.util.graphlayout.ContainedGraph.Node;

public class ContainedGraphUtil {
	
	public static double getCenterXMin(Node node) { return node.getWidth()/2; }
	
	public static double getCenterXMax(Node node) { 
		return node.getContainer().getWidth() - node.getWidth()/2; 
	}
	
	public static double getCenterYMin(Node node) { return node.getHeight()/2; }
	
	public static double getCenterYMax(Node node) { 
		return node.getContainer().getHeight() - node.getHeight()/2; 
	}
	
	public static double getRandomCenterX(Node node, Random random) {
		double r = random.nextDouble();
		return r*getCenterXMin(node) + (1 - r)*getCenterXMax(node);
	}

	public static double getRandomCenterY(Node node, Random random) {
		double r = random.nextDouble();
		return r*getCenterYMin(node) + (1 - r)*getCenterYMax(node);
	}

	public static double getRandomShiftX(Node node, Random random) {
		return getRandomCenterX(node, random) - node.getCenterX();
	}

	public static double getRandomShiftY(Node node, Random random) {
		return getRandomCenterY(node, random) - node.getCenterY();
	}

}
