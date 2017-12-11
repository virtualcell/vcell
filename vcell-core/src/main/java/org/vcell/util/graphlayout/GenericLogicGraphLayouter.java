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

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import org.vcell.util.graphlayout.ContainedGraph.Container;
import org.vcell.util.graphlayout.ContainedGraph.Edge;
import org.vcell.util.graphlayout.ContainedGraph.Node;

import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class GenericLogicGraphLayouter extends ContainedGraphLayouter {

	public static final String LAYOUT_NAME = "Generic Logic Graph Layouter";
	enum LAYOUT_TYPE {
		TYPE_DEFAULT,
		TYPE_EXTERNAL
	}
	
	private static class GlgGraphLayout {

		public final DirectedSparseMultigraph<GlgGraphNode, GlgGraphEdge> jungGraph;
		public final SpringLayout<GlgGraphNode, GlgGraphEdge> layout;
		
		SpringLayout<GlgGraphNode, GlgGraphEdge> getLayout() {
			return layout;
		}
		
		public GlgGraphLayout(int width, int height) {
			jungGraph = new DirectedSparseMultigraph<>();
			layout = new SpringLayout<GlgGraphNode, GlgGraphEdge>(this.jungGraph);
//			layout.initialize();
//			layout.reset();
			layout.setSize(new Dimension(width, height));
		}

		public GlgGraphNode AddNode(LAYOUT_TYPE layoutType, Node node) {
			GlgGraphNode glgNode = new GlgGraphNode();
			glgNode.data = node;
			glgNode.external = (layoutType == LAYOUT_TYPE.TYPE_EXTERNAL);
			boolean retcode = jungGraph.addVertex(glgNode);  // ignore retcode (false if already in graph or null).
			return glgNode;
		}

		public void SetNodePosition(GlgGraphNode glgNode, double x, double y) {
			layout.setLocation(glgNode, x, y);
		}

		public boolean NodesConnected(GlgGraphNode glgNode1, GlgGraphNode glgNode2) {
			return jungGraph.findEdge(glgNode1, glgNode2) != null;
		}

		public GlgGraphEdge AddEdge(GlgGraphNode glgNode1, GlgGraphNode glgNode2, Edge edge) {
			GlgGraphEdge glgGraphEdge = new GlgGraphEdge(edge);
			boolean retcode = jungGraph.addEdge(glgGraphEdge, glgNode1, glgNode2); // ignore retcode?
			return glgGraphEdge;
		}

		public void Update() {
			// TODO:  ... maybe we want to loop here many times
		}
		
		public boolean SpringIterate() {
			layout.step();
			return layout.done();
		}
	}
	
	private static class GlgGraphNode {
		//GlgPoint position = new GlgPoint(0,0);
		boolean external = false;
		Node data;  // vcell node
	}
	
	private static class GlgGraphEdge {
		Edge edge;
		private GlgGraphEdge(Edge edge){
			this.edge = edge;
		}
	}
	
	protected static class ContainerContext {

		protected final Container container;
		protected final GlgGraphLayout glgGraph;
		protected final Map<Node, GlgGraphNode> extNodeMap = new HashMap<Node, GlgGraphNode>();
		protected final Map<Node, GlgGraphNode> intNodeMap = new HashMap<Node, GlgGraphNode>();
		protected final Map<Edge, GlgGraphEdge> edgeMap = new HashMap<Edge, GlgGraphEdge>();

		public ContainerContext(Container container) {
			glgGraph = new GlgGraphLayout((int)container.width, (int)container.height);
			this.container = container;
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
			glgNode = context.getGlgGraph().AddNode(LAYOUT_TYPE.TYPE_DEFAULT, node);
			context.getIntNodeMap().put(node, glgNode);
		}
		double x = node.getCenterX() - context.getContainer().x;	// jung layout is 0 aligned (x=0, y=0), vCell containers have an x/y offset
		double y = node.getCenterY() - context.getContainer().y;
		context.getGlgGraph().SetNodePosition(glgNode, x, y);
		return glgNode;
	}

	// node2 - internal, already dealt with
	// node - our external candidate
	protected static GlgGraphNode getOrAddExternalNode(ContainerContext context, Node node, Node node2) {
		GlgGraphNode glgNode = context.getExtNodeMap().get(node);
		if(glgNode == null) {
			glgNode = context.getGlgGraph().AddNode(LAYOUT_TYPE.TYPE_EXTERNAL, node);
			context.getGlgGraph().getLayout().lock(glgNode, true);
			context.getExtNodeMap().put(node, glgNode);
		}
		double x = node.getCenterX() - node2.getCenterX();	// external nodes will be outside the layout rectangle
		double y = node.getCenterY() - node2.getCenterY();
		context.getGlgGraph().SetNodePosition(glgNode, x, y);
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
					glgEdge = context.getGlgGraph().AddEdge(glgNode1, glgNode2, edge);
					context.getEdgeMap().put(edge, glgEdge);					
				}
			}			
		} else if(container.equals(node1.getContainer())) {
			GlgGraphEdge glgEdge = context.getEdgeMap().get(edge);
			if(glgEdge == null) {
				GlgGraphNode glgNode1 = getOrAddInternalNode(context, node1);
				GlgGraphNode glgNodeExternal2 = getOrAddExternalNode(context, node2, node1);
				if(!context.getGlgGraph().NodesConnected(glgNode1, glgNodeExternal2)) {	
					glgEdge = context.getGlgGraph().AddEdge(glgNode1, glgNodeExternal2, edge);
					context.getEdgeMap().put(edge, glgEdge);
				}
			}
		} else if(container.equals(node2.getContainer())) {		
			GlgGraphEdge glgEdge = context.getEdgeMap().get(edge);
			if(glgEdge == null) {
				GlgGraphNode glgNodeExternal1 = getOrAddExternalNode(context, node1, node2);
				GlgGraphNode glgNode2 = getOrAddInternalNode(context, node2);
				if(!context.getGlgGraph().NodesConnected(glgNodeExternal1, glgNode2)) {					
					glgEdge = context.getGlgGraph().AddEdge(glgNode2, glgNodeExternal1, edge);
					context.getEdgeMap().put(edge, glgEdge);
				}
			}						
		}
	}

	@Override
	public void layout(ContainedGraph graph) {
		long TIMEOUT = 5000;	// stop algorithm if it takes too long
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < 400; ++i) {
			long currentTime = System.currentTimeMillis();
			if(currentTime - startTime > TIMEOUT) {
				break;		// exit if it takes too long
			}
			for(Container container : graph.getContainers()) {
				ContainerContext containerContext = new ContainerContext(container);	// size is set here
				for(Node node : graph.getContainerNodes(container)) {
					getOrAddInternalNode(containerContext, node);
					for(Edge edge : graph.getNodeEdges(node)) {
						getOrAddEdge(containerContext, edge);
					}
				}
				int count = 0;
				while (!containerContext.getGlgGraph().SpringIterate() && count < 10) {
					count++;
					;
				}
				containerContext.getGlgGraph().Update();
//				@SuppressWarnings("unchecked")
//				List<GlgGraphNode> glgNodes = containerContext.getGlgGraph().node_array;
//				for(GlgGraphNode glgNode : glgNodes) {
//					if(!glgNode.external && glgNode.data instanceof Node) {
//						Node node = (Node) glgNode.data;
//						Container nodeContainer = node.getContainer();
//						if(nodeContainer.equals(container)) {
//							GlgPoint glgNodePos = containerContext.getGlgGraph().GetNodePosition(glgNode);
//							node.setCenter(glgNodePos.x, glgNodePos.y);
//						} 
//					}
//				}
				
				for(Node node : graph.getContainerNodes(container)) {
					Container nodeContainer = node.getContainer();
					if(nodeContainer.equals(container)) {
						GlgGraphNode glgNode = containerContext.getIntNodeMap().get(node);
						double x = containerContext.getGlgGraph().getLayout().getX(glgNode) + container.x;
						double y = containerContext.getGlgGraph().getLayout().getY(glgNode) + container.y;
						node.setCenter(x, y);
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
