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
import java.util.Random;

import org.vcell.util.graphlayout.ContainedGraph.Container;
import org.vcell.util.graphlayout.ContainedGraph.Node;

import cbit.vcell.graph.ReactionRuleDiagramShape;
import cbit.vcell.graph.ReactionStepShape;
import cbit.vcell.graph.RuleParticipantSignatureDiagramShape;
import cbit.vcell.graph.SpeciesContextShape;

public class SimpleElipticalLayouter extends ContainedGraphLayouter {

	public static final String LAYOUT_NAME = "Contained Graph Layout Simple Eliptical";
	
//	protected static class ContainerContext {
//
//		protected final Container container;	// vcell container (compartment)
//		public final DirectedSparseMultigraph<JungNode, JungEdge> jungGraph = new DirectedSparseMultigraph<>();
//		public final SpringLayout<JungNode, JungEdge> jungLayout = new SpringLayout<JungNode, JungEdge>(jungGraph);
//		protected final Map<Node, JungNode> extNodeMap = new HashMap<Node, JungNode>();
//		protected final Map<Node, JungNode> intNodeMap = new HashMap<Node, JungNode>();
//		protected final Map<Edge, JungEdge> edgeMap = new HashMap<Edge, JungEdge>();
//
//		public ContainerContext(Container container) {
//			this.container = container;
//		}
//		public Container getContainer() { return container; }
//		public DirectedSparseMultigraph<JungNode, JungEdge> getJungGraph() { return jungGraph; }
//		public SpringLayout<JungNode, JungEdge> getJungLayout() { return jungLayout; }
//		
//		public Map<Node, JungNode> getIntNodeMap() { return intNodeMap; }
//		public Map<Node, JungNode> getExtNodeMap() { return extNodeMap; }
//		public Map<Edge, JungEdge> getEdgeMap() { return edgeMap; }
//		
//		public boolean NodesConnected(JungNode glgNode1, JungNode glgNode2) {
//			return jungGraph.findEdge(glgNode1, glgNode2) != null;
//		}
//	}
//	
//	private static class JungNode {
//		boolean external = false;
//		Node node;  				// vcell node
//		private JungNode(Node node, boolean external) {
//			this.node = node;
//			this.external = external;
//		}
//	}
//	private static class JungEdge {
//		Edge edge;					// vcell edge
//		private JungEdge(Edge edge) {
//			this.edge = edge;
//		}
//	}
//	
//	
	protected Random random = new Random();
	protected StretchToBoundaryLayouter stretchLayouter = new StretchToBoundaryLayouter();	// vcell thingie
//	
//	
//	
//	protected static JungNode getOrAddInternalNode(ContainerContext cc, Node node) {
//		JungNode glgNode = cc.getIntNodeMap().get(node);
//		if(glgNode == null) {
//			glgNode = new JungNode(node, false);	// internal
//			cc.jungGraph.addVertex(glgNode);
//			cc.intNodeMap.put(node, glgNode);
//			double posX = node.getCenterX();
//			double posY = node.getCenterY();
//			cc.jungLayout.setLocation(glgNode, posX, posY);	// TODO: here
//		}
//		return glgNode;
//	}
//
//	// inode - internal, already dealt with
//	// enode - our external candidate
//	protected static JungNode getOrAddExternalNode(ContainerContext cc, Node enode, Node inode) {
//		JungNode glgNode = cc.getExtNodeMap().get(enode);
//		if(glgNode == null) {
//			glgNode = new JungNode(enode, true);	// external
//			cc.jungGraph.addVertex(glgNode);
//			cc.extNodeMap.put(enode, glgNode);
//			double posX = enode.getCenterX() - inode.getCenterX();
//			double posY = enode.getCenterY() - inode.getCenterY();
//			cc.jungLayout.setLocation(glgNode, posX, posY);			// TODO: here
//			cc.jungLayout.lock(glgNode, true);
//		}
//		return glgNode;
//	}
//
//	protected static void getOrAddEdge(ContainerContext cc, Edge edge) {
//		Node node1 = edge.getNode1();
//		Node node2 = edge.getNode2();
//		Container container = cc.getContainer();
//		if(container.equals(node1.getContainer()) && container.equals(node2.getContainer())) {
//			JungEdge jungEdge = cc.getEdgeMap().get(edge);
//			if(jungEdge == null) {
//				JungNode glgNode1 = getOrAddInternalNode(cc, node1);
//				JungNode glgNode2 = getOrAddInternalNode(cc, node2);
//				jungEdge = new JungEdge(edge);
//				cc.jungGraph.addEdge(jungEdge, glgNode1, glgNode2);
//				cc.edgeMap.put(edge, jungEdge);					
//			}			
//		} else if(container.equals(node1.getContainer())) {
//			JungEdge jungEdge = cc.getEdgeMap().get(edge);
//			if(jungEdge == null) {
//				JungNode glgNode1 = getOrAddInternalNode(cc, node1);
//				JungNode glgNodeExternal2 = getOrAddExternalNode(cc, node2, node1);
//				jungEdge = new JungEdge(edge);
//				cc.jungGraph.addEdge(jungEdge, glgNode1, glgNodeExternal2); 
//				cc.edgeMap.put(edge, jungEdge);
//			}
//		} else if(container.equals(node2.getContainer())) {		
//			JungEdge jungEdge = cc.getEdgeMap().get(edge);
//			if(jungEdge == null) {
//				JungNode glgNodeExternal1 = getOrAddExternalNode(cc, node1, node2);
//				JungNode glgNode2 = getOrAddInternalNode(cc, node2);
//				jungEdge = new JungEdge(edge);
//				cc.jungGraph.addEdge(jungEdge, glgNode2, glgNodeExternal1);
//				cc.edgeMap.put(edge, jungEdge);
//			}						
//		}
//	}
	
	
	
	@Override
	public void layout(ContainedGraph graph) {
		
		for(Container container : graph.getContainers()) {
			
//			ContainerContext containerContext = new ContainerContext(container);
//			int edgeCount = 0;
//			ContainerContext containerContext = new ContainerContext(container);
//			DirectedSparseMultigraph<String, Number> dag = new DirectedSparseMultigraph<>();
//			Collection<? extends Node> containerNodes = graph.getContainerNodes(container);
//			Map<String, Node> speciesNodesMap = new HashMap<>();
//			ReactionContainerShape rcs = (ReactionContainerShape)container.getObject();
//			Structure structure = rcs.getStructure();
//			for(Node node : containerNodes) {
//				if(node.object instanceof SpeciesContextShape) {
//					SpeciesContextShape scs = (SpeciesContextShape)node.object;
//					SpeciesContext sc = (SpeciesContext)(scs.getModelObject());
//					dag.addVertex(sc.getName());
//					speciesNodesMap.put(sc.getName(), node);
//				}
//			}
//			for(Node node : containerNodes) {
//				if(node.object instanceof ReactionStepShape) {
//					ReactionStepShape rss = (ReactionStepShape)node.object;
//					ReactionStep rs = (ReactionStep)(rss.getModelObject());
//					for(ReactionParticipant rp1 : rs.getReactionParticipants()) {
//						for(ReactionParticipant rp2 : rs.getReactionParticipants()) {
//							if(structure == rp1.getStructure() && structure == rp2.getStructure()) {
//								if(rp1 instanceof Reactant && rp2 instanceof Product) {		// edges from reactants to products
//									dag.addEdge(edgeCount, rp1.getName(), rp2.getName());
//									edgeCount++;
//								}
//								if(rp1 instanceof Catalyst && rp2 instanceof Reactant) {	// edges from catalysts to reactants
//									dag.addEdge(edgeCount, rp1.getName(), rp2.getName());
//									edgeCount++;
//								}
//								if(rp1 instanceof Reactant && rp2 instanceof Reactant && rp1 != rp2) {	// edges between reactants
//									dag.addEdge(edgeCount, rp1.getName(), rp2.getName());
//									edgeCount++;
//									dag.addEdge(edgeCount, rp2.getName(), rp1.getName());
//									edgeCount++;
//								}
//								if(rp1 instanceof Product && rp2 instanceof Product && rp1 != rp2) {	// edges between products
//									dag.addEdge(edgeCount, rp1.getName(), rp2.getName());
//									edgeCount++;
//									dag.addEdge(edgeCount, rp2.getName(), rp1.getName());
//									edgeCount++;
//								}
//							}
//						}
//					}
//				}
//			}
//			SpringLayout<String, Number> layout = new SpringLayout<String, Number>(dag);
//			layout.setSize(new Dimension((int)container.width,(int)container.height));
//
//			for(String v : dag.getVertices()) {
//				Node node = speciesNodesMap.get(v);
//				layout.setLocation(v, node.getCenterX(), node.getCenterY());
//			}
//			
//			int step = 0;
//			while (!springIterate(layout) && step < 1000) {
//				step++;
//			}
//			
//			// position the nodes on the new locations
//			for(String v : dag.getVertices()) {
//				Node node = speciesNodesMap.get(v);
//				double x = layout.getX(v);
//				double y = layout.getY(v);
//				node.setCenter(x, y);
//			}
//			
//			// place all the reaction nodes in the center of mass of its reactants
//			for(Node node : containerNodes) {
//				if(node.object instanceof ReactionStepShape) {
//					int count = 0;
//					double x = 0;
//					double y = 0;
//					ReactionStepShape rss = (ReactionStepShape)node.object;
//					ReactionStep rs = (ReactionStep)(rss.getModelObject());
//					for(ReactionParticipant rp : rs.getReactionParticipants()) {
//						if(structure == rp.getStructure()) {
//							x += layout.getX(rp.getName());
//							y += layout.getY(rp.getName());
//							count++;
//						} else {		// reactant is in another structure
//							x += 5;		// just shift it a little
//							y += 5;
//							// TODO: make big correction as if it's far away to the left or to the right
//							// depending on the order of structures in the diagram
//							count++;
//						}
//					}
//					if(count > 0) {
//						node.setCenter(x/count, y/count);
//					}
//				}
//			}


			
			
			double centerX = container.getX() + container.getWidth() / 2;
			double centerY = container.getY() + container.getHeight() / 2;
			double quartaxisX = container.getWidth() / 3;
			double quartaxisY = container.getHeight() / 3;
			double semiaxisX = container.getWidth() / 2;
			double semiaxisY = container.getHeight() / 2;
			Collection<? extends Node> containerNodes = graph.getContainerNodes(container);
			int nNodes = containerNodes.size();
			int iNode = 0;
			for(Node node : containerNodes) {
				if(node.object instanceof SpeciesContextShape || node.object instanceof RuleParticipantSignatureDiagramShape) {
					double angle = 2*Math.PI*(((double) iNode) / ((double) nNodes));
					node.setCenter(centerX + quartaxisX*Math.cos(angle), centerY + quartaxisY*Math.sin(angle));
				} else if(node.object instanceof ReactionStepShape || node.object instanceof ReactionRuleDiagramShape) {
					double angle = 2*Math.PI*(((double) iNode) / ((double) nNodes));
					node.setCenter(centerX + semiaxisX*Math.cos(angle), centerY + semiaxisY*Math.sin(angle));
				}
				++iNode;
			}
		}
		stretchLayouter.layout(graph);
	}
	
//	public boolean springIterate(SpringLayout<String, Number> layout) {
//		layout.step();
//		return layout.done();
//	}
//
	@Override
	public String getLayoutName() {
		return LAYOUT_NAME;
	}

}
