package org.vcell.util.graphlayout;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface ContainedGraph {
	
	@SuppressWarnings("serial")
	public class EmbeddingException extends Exception {
		public EmbeddingException(String message) {
			super(message);
		}
	}
	
	public static class Element {
		protected final Object object;
		public Element(Object object) { this.object = object; }
		public Object getObject() { return object; }
	}

	public static class Container extends Element {
		
		protected final Rectangle boundary;
		
		public Container(Object object, Rectangle boundary) { 
			super(object);
			this.boundary = boundary;
		}
		
		public Container(Object object, Point pos, Dimension size) {
			this(object, new Rectangle(pos, size));
		}
		
		public Rectangle getBoundary() { return boundary; }
		public Point getPos() { return boundary.getLocation(); }
		public int getX() { return boundary.x; }
		public int getY() { return boundary.y; }
		public Dimension getSize() { return boundary.getSize(); }
		public int getWidth() { return boundary.width; }
		public int getHeight() { return boundary.height; }
		
	}
	
	public static class Node extends Container {

		protected final Container container;

		public Node(Object object, Container container, Rectangle placement) {
			super(object, placement);
			this.container = container;
		}
		
		public Node(Object object, Container container, Point pos, Dimension size) {
			this(object, container, new Rectangle(pos, size));
		}
		
		public Container getContainer() { return container; }
		
		public void setPos(Point pos) { boundary.setLocation(pos); }
		public void setX(int x) { boundary.x = x; }
		public void setY(int y) { boundary.y = y; }

	}
	
	public static class Edge extends Element {

		protected final Node node1, node2;
		
		public Edge(Object object, Node node1, Node node2) {
			super(object);
			this.node1 = node1;
			this.node2 = node2;
		}
		
		public Node getNode1() { return node1; }
		public Node getNode2() { return node2; }
		
	}

	public Container addContainer(Object object, Rectangle boundary);
	public Container addContainer(Object object, Point pos, Dimension size);
	public Container getContainer(Object object);
	public Collection<Container> getContainers();
	public void removeContainer(Container container);
	public Node addNode(Object object, Container container, Rectangle placement);
	public Node addNode(Object object, Container container, Point pos, Dimension size);
	public Node getNode(Object object);
	public Collection<? extends Node> getNodes();
	public void removeNode(Node node);
	public Edge addEdge(Object object, Node node1, Node node2);
	public Edge getEdge(Object object);
	public Collection<? extends Edge> getEdges();
	public void removeEdge(Edge edge);
	public Collection<? extends Edge> getNodeEdges(Node node);
	public Collection<? extends Node> getContainerNodes(Container container);
	public void clear();
	
	public static class Default implements ContainedGraph {

		protected Map<Object, Container> containerMap = new HashMap<Object, Container>();
		protected Map<Object, Node> nodeMap = new HashMap<Object, Node>();
		protected Map<Object, Edge> edgeMap = new HashMap<Object, Edge>();
		protected Map<Node, Set<Edge>> nodeEdgesMap = new HashMap<Node, Set<Edge>>();
		protected Map<Container, Set<Node>> containerNodesMap = new HashMap<Container, Set<Node>>();
		
		public Container addContainer(Object object, Rectangle boundary) {
			Container container = new Container(object, boundary);
			containerMap.put(object, container);
			return container;
		}

		public Container addContainer(Object object, Point pos, Dimension size) {
			Container container = new Container(object, pos, size);
			containerMap.put(object, container);
			return container;
		}

		public Container getContainer(Object object) { return containerMap.get(object); }
		public Collection<Container> getContainers() { return containerMap.values(); }
		
		public void removeContainer(Container container) { 
			Set<Node> containerNodes = containerNodesMap.get(container);
			if(containerNodes != null) {
				for(Node node : containerNodes) {
					removeNode(node);
				}
			}
			containerNodesMap.remove(container);
			containerMap.remove(container.getObject()); 
		}

		public Node addNode(Object object, Container container, Rectangle placement) {
			Node node = new Node(object, container, placement);
			nodeMap.put(object, node);
			Set<Node> containerNodes = containerNodesMap.get(container);
			if(containerNodes == null) {
				containerNodes = new HashSet<Node>();
				containerNodesMap.put(container, containerNodes);
			}
			containerNodes.add(node);
			return node;
		}

		public Node addNode(Object object, Container container, Point pos, Dimension size) {
			Node node = new Node(object, container, pos, size);
			nodeMap.put(object, node);
			Set<Node> containerNodes = containerNodesMap.get(container);
			if(containerNodes == null) {
				containerNodes = new HashSet<Node>();
				containerNodesMap.put(container, containerNodes);
			}
			containerNodes.add(node);
			return node;
		}

		public Node getNode(Object object) { return nodeMap.get(object); }
		public Collection<Node> getNodes() { return nodeMap.values(); }
		
		public void removeNode(Node node) { 
			nodeMap.remove(node.getObject()); 
			Set<Node> containerNodes = containerNodesMap.get(node.getContainer());
			if(containerNodes != null) {
				containerNodes.remove(node);
				if(containerNodes.isEmpty()) {
					containerNodesMap.remove(node.getContainer());
				}
			}
			Set<Edge> nodeEdges = nodeEdgesMap.get(node);
			if(nodeEdges != null) {
				for(Edge edge : nodeEdges) {
					removeEdge(edge);
				}
			}
		}

		public Edge addEdge(Object object, Node node1, Node node2) {
			Edge edge = new Edge(object, node1, node2);
			edgeMap.put(object, edge);
			Set<Edge> nodeEdges1 = nodeEdgesMap.get(node1);
			if(nodeEdges1 == null) {
				nodeEdges1 = new HashSet<Edge>();
				nodeEdgesMap.put(node1, nodeEdges1);
			}
			nodeEdges1.add(edge);
			Set<Edge> nodeEdges2 = nodeEdgesMap.get(node2);
			if(nodeEdges2 == null) {
				nodeEdges2 = new HashSet<Edge>();
				nodeEdgesMap.put(node2, nodeEdges2);
			}
			nodeEdges2.add(edge);
			return edge;
		}

		public Edge getEdge(Object object) { return edgeMap.get(object); }
		public Collection<Edge> getEdges() { return edgeMap.values(); }
		
		public void removeEdge(Edge edge) { 
			edgeMap.remove(edge.getObject()); 
			Node node1 = edge.getNode1();
			Set<Edge> nodeEdges1 = nodeEdgesMap.get(node1);
			if(nodeEdges1 != null) {
				nodeEdges1.remove(edge);
				if(nodeEdges1.isEmpty()) {
					nodeEdgesMap.remove(node1);
				}
			}
			Node node2 = edge.getNode2();
			Set<Edge> nodeEdges2 = nodeEdgesMap.get(node2);
			if(nodeEdges2 != null) {
				nodeEdges2.remove(edge);
				if(nodeEdges2.isEmpty()) {
					nodeEdgesMap.remove(node2);
				}
			}
		}

		public Set<Edge> getNodeEdges(Node node) {
			Set<Edge> nodeEdges = nodeEdgesMap.get(node);
			if(nodeEdges == null) {
				nodeEdges = new HashSet<Edge>();
			}
			return nodeEdges;
		}

		public Collection<? extends Node> getContainerNodes(
				Container container) {
			Set<Node> containerNodes = containerNodesMap.get(container);
			if(containerNodes == null) {
				containerNodes = new HashSet<Node>();				
			}
			return containerNodes;
		}

		public void clear() {
			containerMap.clear();
			containerNodesMap.clear();
			edgeMap.clear();
			nodeEdgesMap.clear();
			nodeMap.clear();
		}
		
	}
}
