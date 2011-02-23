package org.vcell.util.graphlayout;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.vcell.util.graphlayout.ContainedGraph.Container;
import org.vcell.util.graphlayout.ContainedGraph.Node;

public interface ContainedGraphLayouter {

	public static final String LAYOUT_NAME_FILL_BOUNDARY = "Contained Graph Layout Fill Boundary";
	public static final String LAYOUT_NAME_RANDOM = "Contained Graph Layout Random";
	
	public static final List<String> LAYOUT_NAMES = Arrays.asList(LAYOUT_NAME_RANDOM);
	
	public String getLayoutName();
	public void layout(ContainedGraph graph);
	
	public static class StretchToBoundaryLayouter implements ContainedGraphLayouter {

		public void layout(ContainedGraph graph) {
			for(Container container : graph.getContainers()) {
				Collection<? extends Node> containerNodes = graph.getContainerNodes(container);
				if(containerNodes.size() > 1) {
					Iterator<? extends Node> nodeIter = containerNodes.iterator();
					Node firstNode = nodeIter.next();
					int xMin = firstNode.getPos().x;
					int yMin = firstNode.getPos().y;
					int xMax = firstNode.getPos().x + firstNode.getSize().width;
					int yMax = firstNode.getPos().y + firstNode.getSize().height;
					while(nodeIter.hasNext()) {
						Node node = nodeIter.next();
						int xMinNode = node.getPos().x;
						int yMinNode = node.getPos().y;
						int xMaxNode = node.getPos().x + node.getSize().width;
						int yMaxNode = node.getPos().y + node.getSize().height;
						if(xMinNode < xMin) { xMin = xMinNode; }
						if(yMinNode < yMin) { yMin = yMinNode; }
						if(xMaxNode > xMax) { xMax = xMaxNode; }
						if(yMaxNode > yMax) { yMax = yMaxNode; }
					}
					int xRange = xMax - xMin;
					if(xRange > 0) {
						for(Node node : containerNodes) {
							int spaceNew = container.getWidth() - node.getWidth();
							int spaceOld = xRange - node.getWidth();
							if(spaceNew >= 0) {
								node.setX(container.getX() + 
										((node.getX() - xMin)*spaceNew) / spaceOld);								
							}
						}						
					}
					int yRange = yMax - yMin;
					if(yRange > 0) {
						for(Node node : containerNodes) {
							int spaceNew = container.getHeight() - node.getHeight();
							int spaceOld = yRange - node.getHeight();
							if(spaceNew >= 0) {
								node.setY(container.getY() + 
										((node.getY() - yMin)*spaceNew) / spaceOld);								
							}
						}						
					}
				}
			}
		}
		
		public String getLayoutName() {
			return LAYOUT_NAME_FILL_BOUNDARY;
		}

	}
	
	public static class RandomLayouter implements ContainedGraphLayouter {

		protected Random random = new Random();
		protected StretchToBoundaryLayouter stretchLayouter = new StretchToBoundaryLayouter();
		
		public void layout(ContainedGraph graph) {
			for(Node node : graph.getNodes()) {
				Dimension nodeSize = node.getSize();
				Container container = node.getContainer();
				Point containerPos = container.getPos();
				Dimension containerSize = container.getSize();
				node.getBoundary().x = 
					containerPos.x + random.nextInt(containerSize.width - nodeSize.width);
				node.getBoundary().y = 
					containerPos.y + random.nextInt(containerSize.height - nodeSize.height);
			}
			stretchLayouter.layout(graph);
		}

		public String getLayoutName() {
			return LAYOUT_NAME_RANDOM;
		}
		
	}
	
}
