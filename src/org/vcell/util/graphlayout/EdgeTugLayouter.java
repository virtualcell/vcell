package org.vcell.util.graphlayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.vcell.util.graphlayout.ContainedGraph.Edge;
import org.vcell.util.graphlayout.ContainedGraph.Node;

public class EdgeTugLayouter implements ContainedGraphLayouter {

	public static final String LAYOUT_NAME = "Contained Graph Layouter Edge Tug";

	protected StretchToBoundaryLayouter stretchLayouter = new StretchToBoundaryLayouter();
	
	protected Random random = new Random();
	
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
		stretchLayouter.layout(graph);
	}

	public double distanceSquared(Node node1, Node node2) {
		double dx = node1.getCenterX() - node2.getCenterX();
		double dy = node1.getCenterY() - node2.getCenterY();
		return dx*dx + dy*dy;
	}
	
	public String getLayoutName() {
		return LAYOUT_NAME;
	}


	
}
