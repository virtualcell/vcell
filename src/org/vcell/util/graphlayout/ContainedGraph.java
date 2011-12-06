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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.vcell.util.geometry2d.Point2D;
import org.vcell.util.geometry2d.Vector2D;

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
		
		protected double x, y, width, height;
		
		public Container(Object object, double x, double y, double width, double height) { 
			super(object);
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		public double getX() { return x; }
		public double getY() { return y; }
		public double getWidth() { return width; }
		public void setWidth(double width) { this.width = width; }
		public double getHeight() { return height; }
		public void setHeight(double height) { this.height = height; }
		public double getNodeMaxX(Node node) { return getX() + getWidth() - node.getWidth(); }
		public double getNodeMaxY(Node node) { return getY() + getHeight() - node.getHeight(); }
		
	}
	
	public static class Node extends Container {

		protected final Container container;

		public Node(Object object, Container container, double x, double y, double width, double height) {
			super(object, x, y, width, height);
			this.container = container;
		}
		
		public Container getContainer() { return container; }
		
		public void setPos(double x, double y) { setX(x); setY(y); }
		public void setX(double x) { this.x = x; }
		public void setY(double y) { this.y = y; }
		public Point2D getCenter() { return new Point2D(getX() + getWidth()/2, getY() + getHeight()/2); }
		public double getCenterX() { return getX() + getWidth()/2; }
		public double getCenterY() { return getY() + getHeight()/2; }
		public void setCenterX(double x) { setX(x - getWidth()/2); }
		public void setCenterY(double y) { setY(y - getHeight()/2); }
		public void setCenter(double x, double y) { setCenterX(x); setCenterY(y); }
		public void moveX(double dx) { setX(getX() + dx); }
		public void moveY(double dy) { setY(getY() + dy); }
		public void move(double dx, double dy) { setPos(getX() + dx, getY() + dy); }
		public void move(Vector2D v) { setPos(getX() + v.x, getY() + v.y); }
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

	public Container addContainer(Object object, double x, double y, double width, double height);
	public Container getContainer(Object object);
	public boolean containsContainer(Object object);
	public Collection<Container> getContainers();
	public void removeContainer(Container container);
	public Node addNode(Object object, Container container, double x, double y, 
			double width, double height);
	public boolean containsNode(Object object);
	public Node getNode(Object object);
	public Collection<? extends Node> getNodes();
	public void removeNode(Node node);
	public Edge addEdge(Object object, Node node1, Node node2);
	public boolean containsEdge(Object object);
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
		
		public Container addContainer(Object object, double x, double y, double width, double height) {
			Container container = new Container(object, x, y, width, height);
			containerMap.put(object, container);
			return container;
		}

		public boolean containsContainer(Object object) { return containerMap.containsKey(object); }
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

		public Node addNode(Object object, Container container, double x, double y, 
				double width, double height) {
			Node node = new Node(object, container, x, y, width, height);
			nodeMap.put(object, node);
			Set<Node> containerNodes = containerNodesMap.get(container);
			if(containerNodes == null) {
				containerNodes = new HashSet<Node>();
				containerNodesMap.put(container, containerNodes);
			}
			containerNodes.add(node);
			return node;
		}

		public boolean containsNode(Object object) { return nodeMap.containsKey(object); }
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

		public boolean containsEdge(Object object) { return edgeMap.containsKey(object); }
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
