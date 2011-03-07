package org.vcell.util.graphlayout.energybased;

import java.util.Random;

import org.vcell.util.graphlayout.ContainedGraph;
import org.vcell.util.graphlayout.ContainedGraphUtil;
import org.vcell.util.graphlayout.ContainedGraph.Node;
import org.vcell.util.graphlayout.NodesShift;
import org.vcell.util.graphlayout.energybased.EnergySum.Minimizer;

public class ShootAndCutMinimizer implements Minimizer {

	public static final double cut = 0.25;
	
	protected Random random = new Random();
	
	public void minimize(EnergySum energySum) {
		ContainedGraph graph = energySum.getGraph();
		for(Node node : graph.getNodes()) {
			double shiftX = ContainedGraphUtil.getRandomShiftX(node, random);
			double shiftY = ContainedGraphUtil.getRandomShiftY(node, random);
			while(Math.abs(shiftX) + Math.abs(shiftY) > 0.001) {
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
