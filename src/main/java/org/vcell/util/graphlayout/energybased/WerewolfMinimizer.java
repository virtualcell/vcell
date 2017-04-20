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

import java.util.Map;
import java.util.Random;

import org.vcell.util.geometry2d.Vector2D;
import org.vcell.util.graphlayout.ContainedGraph;
import org.vcell.util.graphlayout.ContainedGraphUtil;
import org.vcell.util.graphlayout.ContainedGraph.Node;
import org.vcell.util.graphlayout.NodesShift;
import org.vcell.util.graphlayout.energybased.EnergySum.Minimizer;

public class WerewolfMinimizer implements Minimizer {

	public static final double cut = 0.4;
	public static final int nIterations = 10;
	
	protected Random random = new Random();

	public void minimize(EnergySum energySum) {
		ContainedGraph graph = energySum.getGraph();
		NodesShift.Default nodesShift = new NodesShift.Default(graph);
		for(int iIteration = 0; iIteration < nIterations; ++iIteration) {
			double absShiftMax = 0;
			for(Node node : graph.getNodes()) {
				double shiftX = ContainedGraphUtil.getRandomShiftX(node, random);
				double shiftY = ContainedGraphUtil.getRandomShiftY(node, random);
				nodesShift.getShifts().put(node, new Vector2D(shiftX, shiftY));
				double absShiftX = Math.abs(shiftX);
				if(absShiftX > absShiftMax) { absShiftMax = absShiftX; }
				double absShiftY = Math.abs(shiftY);
				if(absShiftY > absShiftMax) { absShiftMax = absShiftY; }
			}
			while(absShiftMax > 0.3) {
				for(Map.Entry<Node, Vector2D> entry : nodesShift.getShifts().entrySet()) {
					Vector2D vShift = entry.getValue();
					vShift.x *= (1 - cut);
					vShift.y *= (1 - cut);
				}
				if(energySum.getDifference(nodesShift) < 0) {
					nodesShift.apply();
					break;
				}
				absShiftMax *= (1 - cut);
			}
		}
	}

}
