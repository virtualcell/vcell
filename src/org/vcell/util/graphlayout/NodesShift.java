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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.vcell.util.geometry2d.Point2D;
import org.vcell.util.geometry2d.Vector2D;
import org.vcell.util.graphlayout.ContainedGraph.Node;

public interface NodesShift {

	public ContainedGraph getGraph();
	public Set<Node> getMovedNodes();
	public boolean isMovedNode(Node node);
	public boolean areMovedTheSame(Node node1, Node node2);
	public Vector2D getDelta(Node node);
	public double getDeltaX(Node node);
	public double getDeltaY(Node node);
	public Point2D getShiftedCenter(Node node);
	public double getShiftedX(Node node);
	public double getShiftedY(Node node);
	public void apply();
	
	public static class Default implements NodesShift {

		protected final ContainedGraph graph;
		protected final Map<Node, Vector2D> shifts = new HashMap<Node, Vector2D>();
		
		public Default(ContainedGraph graph) { this.graph = graph; }
		
		public ContainedGraph getGraph() { return graph; }
		public Map<Node, Vector2D> getShifts() { return shifts; }
		public Set<Node> getMovedNodes() { return shifts.keySet(); }
		public boolean isMovedNode(Node node) { return shifts.containsKey(node); }
		public boolean areMovedTheSame(Node node1, Node node2) { return node1 == node2; }

		public Vector2D getDelta(Node node) {
			Vector2D v = shifts.get(node);
			return v != null ? v : new Vector2D();
		}

		public double getDeltaX(Node node) { 
			Vector2D v = shifts.get(node);
			return v != null ? v.x : 0;
		}
		
		public double getDeltaY(Node node) { 
			Vector2D v = shifts.get(node);
			return v != null ? v.y : 0;
		}

		public Point2D getShiftedCenter(Node node) {
			return new Point2D(node.x + getDeltaX(node), node.y + getDeltaY(node));
		}
		
		public double getShiftedX(Node node) { return node.x + getDeltaX(node); }
		public double getShiftedY(Node node) { return node.y + getDeltaY(node); }

		public void apply() {
			for(Map.Entry<Node, Vector2D> shift : shifts.entrySet()) {
				Node node = shift.getKey();
				Vector2D v = shift.getValue();
				node.move(v);
			}
		}
		
	}
	
	public static class SingleNode implements NodesShift {

		protected final ContainedGraph graph;
		protected final Node node;
		protected final Vector2D shift;
		
		public SingleNode(ContainedGraph graph, Node node, Vector2D shift) {
			this.graph = graph;
			this.node = node;
			this.shift = shift;
		}
		
		public SingleNode(ContainedGraph graph, Node node, double x, double y) {
			this(graph, node, new Vector2D(x, y));
		}
		
		public ContainedGraph getGraph() { return graph; }
		public Set<Node> getMovedNodes() { return Collections.<Node>singleton(node); }
		public boolean isMovedNode(Node node) { return this.node == node; }
		public boolean areMovedTheSame(Node node1, Node node2) { return node1 == node2; }
		public Vector2D getDelta(Node node) { return this.node == node ? shift : new Vector2D(0, 0); }

		public double getDeltaX(Node node) { return this.node == node ? shift.x : 0; }
		public double getDeltaY(Node node) { return this.node == node ? shift.y : 0; }

		public Point2D getShiftedCenter(Node node) {
			return new Point2D(node.getCenterX() + getDeltaX(node), node.getCenterY() + getDeltaY(node));
		}
		
		public double getShiftedX(Node node) { return node.getCenterX() + getDeltaX(node); }
		public double getShiftedY(Node node) { return node.getCenterY() + getDeltaY(node); }

		public void apply() { node.move(shift);	}

	}
	
}
