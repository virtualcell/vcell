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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.vcell.util.graphlayout.ContainedGraph.Edge;
import org.vcell.util.graphlayout.ContainedGraph.Node;

public class EdgeTugLayouter extends ContainedGraphLayouter {

	public static final String LAYOUT_NAME = "Contained Graph Layouter Edge Tug";

	protected StretchToBoundaryLayouter stretchLayouter = new StretchToBoundaryLayouter();
	
	protected Random random = new Random();
	
	@Override
	public void layout(ContainedGraph graph) {
		for(Node node : graph.getNodes()) {
			node.move(random.nextInt(3) - 1, random.nextInt(3) - 1);
			Collection<? extends Edge> nodeEdges = graph.getNodeEdges(node);
			for(Edge edge : nodeEdges) {
				Node node2 = edge.getNode1().equals(node) ? edge.getNode2() : edge.getNode1();
				node.move(Math.signum(node2.getCenterX() - node.getCenterX()), 
						Math.signum(node2.getCenterY() - node.getCenterY()));				
			}
			List<Node> closestNodes = new ArrayList<Node>();
			int closestNodesCount = nodeEdges.size();
			if(closestNodesCount > 0) {
				for(Node node2 : graph.getNodes()) {
					if(closestNodes.size() < closestNodesCount) {
						closestNodes.add(node2);
					} else if(distanceSquared(node, node2) < 
							distanceSquared(node, closestNodes.get(closestNodesCount - 1))) {
						closestNodes.set(closestNodesCount - 1, node2);
					}
					for(int i = closestNodes.size() - 1; i > 0; --i) {
						if(distanceSquared(node, closestNodes.get(i)) < 
								distanceSquared(node, closestNodes.get(i - 1))) {
							Collections.swap(closestNodes, i, i - 1);
						} else {
							break;
						}
					}
				}
				for(Node closeNode : closestNodes) {
					node.move(Math.signum(node.getCenterX() - closeNode.getCenterX()), 
							Math.signum(node.getCenterY() - closeNode.getCenterY()));				
				}
			}
		}
		stretchLayouter.layout(graph);
	}

	public double distanceSquared(Node node1, Node node2) {
		double dx = node1.getCenterX() - node2.getCenterX();
		double dy = node1.getCenterY() - node2.getCenterY();
		return dx*dx + dy*dy;
	}
	
	@Override
	public String getLayoutName() {
		return LAYOUT_NAME;
	}


	
}
