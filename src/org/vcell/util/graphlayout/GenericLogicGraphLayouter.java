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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.vcell.util.graphlayout.ContainedGraph.Container;
import org.vcell.util.graphlayout.ContainedGraph.Edge;
import org.vcell.util.graphlayout.ContainedGraph.Node;

import com.genlogic.GraphLayout.GlgCube;
import com.genlogic.GraphLayout.GlgGraphEdge;
import com.genlogic.GraphLayout.GlgGraphLayout;
import com.genlogic.GraphLayout.GlgGraphNode;
import com.genlogic.GraphLayout.GlgPoint;

public class GenericLogicGraphLayouter extends ContainedGraphLayouter {

	public static final String LAYOUT_NAME = "Generic Logic Graph Layouter";
	public static final int TYPE_DEFAULT = 0;
	public static final int TYPE_EXTERNAL = -1;
	
	protected static class ContainerContext {

		protected final Container container;
		protected final GlgGraphLayout glgGraph;
		protected final Map<Node, GlgGraphNode> extNodeMap = new HashMap<Node, GlgGraphNode>();
		protected final Map<Node, GlgGraphNode> intNodeMap = new HashMap<Node, GlgGraphNode>();
		protected final Map<Edge, GlgGraphEdge> edgeMap = new HashMap<Edge, GlgGraphEdge>();

		public ContainerContext(Container container) {
			glgGraph = new GlgGraphLayout();
			this.container = container;
			GlgCube glgDims = new GlgCube();
			glgDims.p1 = new GlgPoint(container.x, container.y, 0);
			glgDims.p2 = new GlgPoint(container.x + container.width, container.y + container.height, 0);
			glgGraph.dimensions = glgDims;
		}
		
		public Container getContainer() { return container; }
		public GlgGraphLayout getGlgGraph() { return glgGraph; }
		public Map<Node, GlgGraphNode> getIntNodeMap() { return intNodeMap; }
		public Map<Node, GlgGraphNode> getExtNodeMap() { return extNodeMap; }
		public Map<Edge, GlgGraphEdge> getEdgeMap() { return edgeMap; }
	
	}
	
	protected StretchToBoundaryLayouter stretchLayouter = new StretchToBoundaryLayouter();
	
	protected static GlgGraphNode getOrAddInternalNode(ContainerContext context, Node node) {
		GlgGraphNode glgNode = context.getIntNodeMap().get(node);
		if(glgNode == null) {
			glgNode = context.getGlgGraph().AddNode(null, TYPE_DEFAULT, node);
			context.getIntNodeMap().put(node, glgNode);
		}
		context.getGlgGraph().SetNodePosition(glgNode, node.getCenterX(), node.getCenterY(), 0);
		return glgNode;
	}

	protected static GlgGraphNode getOrAddExternalNode(ContainerContext context, Node node, Node node2) {
		GlgGraphNode glgNode = context.getExtNodeMap().get(node);
		if(glgNode == null) {
			glgNode = context.getGlgGraph().AddNode(null, TYPE_EXTERNAL, node);
			context.getExtNodeMap().put(node, glgNode);
		}
		glgNode.position.x = node.getCenterX() - node2.getCenterX();
		glgNode.position.y = node.getCenterY() - node2.getCenterY();
		return glgNode;
	}

	protected static void getOrAddEdge(ContainerContext context, Edge edge) {
		Node node1 = edge.getNode1();
		Node node2 = edge.getNode2();
		Container container = context.getContainer();
		if(container.equals(node1.getContainer()) && container.equals(node2.getContainer())) {
			GlgGraphEdge glgEdge = context.getEdgeMap().get(edge);
			if(glgEdge == null) {
				GlgGraphNode glgNode1 = getOrAddInternalNode(context, node1);
				GlgGraphNode glgNode2 = getOrAddInternalNode(context, node2);
				if(!context.getGlgGraph().NodesConnected(glgNode1, glgNode2)) {
					glgEdge = context.getGlgGraph().AddEdge(glgNode1, glgNode2, null, TYPE_DEFAULT, edge);
					context.getEdgeMap().put(edge, glgEdge);					
				}
			}			
		} else if(container.equals(node1.getContainer())) {
			GlgGraphEdge glgEdge = context.getEdgeMap().get(edge);
			if(glgEdge == null) {
				GlgGraphNode glgNode1 = getOrAddInternalNode(context, node1);
				GlgGraphNode glgNodeExternal2 = getOrAddExternalNode(context, node2, node1);
				if(!context.getGlgGraph().NodesConnected(glgNode1, glgNodeExternal2)) {	
					glgEdge = context.getGlgGraph().AddEdge(glgNode1, glgNodeExternal2, null, 
							TYPE_EXTERNAL, edge);
					context.getEdgeMap().put(edge, glgEdge);
				}
			}
		} else if(container.equals(node2.getContainer())) {		
			GlgGraphEdge glgEdge = context.getEdgeMap().get(edge);
			if(glgEdge == null) {
				GlgGraphNode glgNodeExternal1 = getOrAddExternalNode(context, node1, node2);
				GlgGraphNode glgNode2 = getOrAddInternalNode(context, node2);
				if(!context.getGlgGraph().NodesConnected(glgNodeExternal1, glgNode2)) {					
					glgEdge = context.getGlgGraph().AddEdge(glgNode2, glgNodeExternal1, null, 
							TYPE_EXTERNAL, edge);
					context.getEdgeMap().put(edge, glgEdge);
				}
			}						
		}
	}

	@Override
	public void layout(ContainedGraph graph) {
		for(int i = 0; i < 3; ++i) {
			for(Container container : graph.getContainers()) {
				ContainerContext containerContext = new ContainerContext(container);
				for(Node node : graph.getContainerNodes(container)) {
					getOrAddInternalNode(containerContext, node);
					for(Edge edge : graph.getNodeEdges(node)) {
						getOrAddEdge(containerContext, edge);
					}
				}
				while (!containerContext.getGlgGraph().SpringIterate()) {
					;
				}
				containerContext.getGlgGraph().Update();
				@SuppressWarnings("unchecked")
				List<GlgGraphNode> glgNodes = containerContext.getGlgGraph().node_array;
				for(GlgGraphNode glgNode : glgNodes) {
					if(!glgNode.external && glgNode.data instanceof Node) {
						Node node = (Node) glgNode.data;
						Container nodeContainer = node.getContainer();
						if(nodeContainer.equals(container)) {
							GlgPoint glgNodePos = containerContext.getGlgGraph().GetNodePosition(glgNode);
							node.setCenter(glgNodePos.x, glgNodePos.y);
						} 
					}
				}
				stretchLayouter.layout(graph, container);
			}
		}
	}

	@Override
	public String getLayoutName() {
		return LAYOUT_NAME;
	}
	
}
