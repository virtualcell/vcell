package org.vcell.util.graphlayout;

import java.util.Collection;
import java.util.Random;

import org.vcell.util.graphlayout.ContainedGraph.Container;
import org.vcell.util.graphlayout.ContainedGraph.Node;

public class SimpleElipticalLayouter implements ContainedGraphLayouter {

	public static final String LAYOUT_NAME = "Contained Graph Layout Simple Eliptical";
	
	protected Random random = new Random();
	protected StretchToBoundaryLayouter stretchLayouter = new StretchToBoundaryLayouter();
	
	public void layout(ContainedGraph graph) {
		for(Container container : graph.getContainers()) {
			double centerX = container.getX() + container.getWidth() / 2;
			double centerY = container.getY() + container.getHeight() / 2;
			double semiaxisX = container.getWidth() / 2;
			double semiaxisY = container.getHeight() / 2;
			Collection<? extends Node> containerNodes = graph.getContainerNodes(container);
			int nNodes = containerNodes.size();
			int iNode = 0;
			for(Node node : containerNodes) {
				double angle = 2*Math.PI*(((double) iNode) / ((double) nNodes));
				node.setCenter(centerX + semiaxisX*Math.cos(angle), centerY + semiaxisY*Math.sin(angle));
				++iNode;
			}
		}
		stretchLayouter.layout(graph);
	}

	public String getLayoutName() {
		return LAYOUT_NAME;
	}
	
}