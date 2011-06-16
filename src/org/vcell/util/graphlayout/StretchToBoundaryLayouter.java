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

import java.util.Collection;
import java.util.Iterator;

import org.vcell.util.graphlayout.ContainedGraph.Container;
import org.vcell.util.graphlayout.ContainedGraph.Node;

public class StretchToBoundaryLayouter extends ContainedGraphLayouter {

	public static final String LAYOUT_NAME = "Contained Graph Layout Stretch To Boundary";

	@Override
	public void layout(ContainedGraph graph) {
		for(Container container : graph.getContainers()) {
			layout(graph, container);
		}
	}


	public void layout(ContainedGraph graph, Container container) {
		Collection<? extends Node> containerNodes = graph.getContainerNodes(container);
		if(!containerNodes.isEmpty()) {
			Iterator<? extends Node> nodeIter = containerNodes.iterator();
			Node firstNode = nodeIter.next();
			double xMin = firstNode.getX();
			double yMin = firstNode.getY();
			double xMax = firstNode.getX() + firstNode.getWidth();
			double yMax = firstNode.getY() + firstNode.getHeight();
			if(containerNodes.size() > 2) {
				while(nodeIter.hasNext()) {
					Node node = nodeIter.next();
					double xMinNode = node.getX();
					double yMinNode = node.getY();
					double xMaxNode = node.getX() + node.getWidth();
					double yMaxNode = node.getY() + node.getHeight();
					if(xMinNode < xMin) { xMin = xMinNode; }
					if(yMinNode < yMin) { yMin = yMinNode; }
					if(xMaxNode > xMax) { xMax = xMaxNode; }
					if(yMaxNode > yMax) { yMax = yMaxNode; }
				}
				double xRange = xMax - xMin;
				if(xRange > 0) {
					for(Node node : containerNodes) {
						double spaceNew = container.getWidth() - node.getWidth();
						double spaceOld = xRange - node.getWidth();
						if(spaceNew >= 0) {
							node.setX(container.getX() + 
									((node.getX() - xMin)*spaceNew) / spaceOld);								
						}
					}						
				}
				double yRange = yMax - yMin;
				if(yRange > 0) {
					for(Node node : containerNodes) {
						double spaceNew = container.getHeight() - node.getHeight();
						double spaceOld = yRange - node.getHeight();
						if(spaceNew >= 0) {
							node.setY(container.getY() + 
									(int)(((node.getY() - yMin)*spaceNew) / spaceOld));
						}
					}						
				}
			} else {
				for(Node node : containerNodes) {
					if(node.getX() < container.getX()) { 
						node.setX(container.getX()); 
					} else {
						double xMaxNode = container.getNodeMaxX(node);
						if(node.getX() > xMaxNode) {
							node.setX(xMaxNode);
						}
					}
					if(node.getY() < container.getY()) { 
						node.setY(container.getY()); 
					} else {
						double yMaxNode = container.getNodeMaxY(node);
						if(node.getY() > yMaxNode) {
							node.setY(yMaxNode);
						}
					}
				}
			}
		}
	}

	@Override
	public String getLayoutName() {
		return LAYOUT_NAME;
	}

}
