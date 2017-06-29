/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.graphlayout.energybased;

import java.util.Random;

import org.vcell.util.graphlayout.ContainedGraph;
import org.vcell.util.graphlayout.ContainedGraph.Node;
import org.vcell.util.graphlayout.ContainedGraphUtil;
import org.vcell.util.graphlayout.NodesShift;
import org.vcell.util.graphlayout.energybased.EnergySum.Minimizer;

public class ShootAndCutMinimizer implements Minimizer {

	public static final double cut = 0.4;

	protected Random random = new Random();
	
	public void minimize(EnergySum energySum) {
		ContainedGraph graph = energySum.getGraph();
		for(Node node : graph.getNodes()) {
			double shiftX = ContainedGraphUtil.getRandomShiftX(node, random);
			double shiftY = ContainedGraphUtil.getRandomShiftY(node, random);
			while(Math.abs(shiftX) + Math.abs(shiftY) > 0.3) {
				NodesShift nodesShift = new NodesShift.SingleNode(graph, node, shiftX, shiftY);
				if(energySum.getDifference(nodesShift) < 0) {
					nodesShift.apply();
					break;
				}
				shiftX = (1 - cut)*shiftX;
				shiftY = (1 - cut)*shiftY;
			}
		}
	}

}
