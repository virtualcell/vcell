/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph;

/*  Static methods useful for shapes
 *  September 2010
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cbit.gui.graph.groups.VCEdge;
import cbit.gui.graph.groups.VCEdgeBundle;
import cbit.gui.graph.visualstate.VisualState.PaintLayer;

public class ShapeUtil {

	public static Shape getNearestCommonAncestor(Collection<Shape> shapes) {
		if (shapes.isEmpty()) {
			return null;
		}
		if (shapes.size() == 1) {
			return shapes.iterator().next().getParent();
		}
		Set<Shape> youngerGenerations = new HashSet<Shape>();
		Collection<Shape> thisGeneration = shapes;
		while (thisGeneration.size() > 1) {
			youngerGenerations.addAll(thisGeneration);
			Set<Shape> nextGeneration = new HashSet<Shape>();
			for (Shape shape : thisGeneration) {
				Shape parent = shape.getParent();
				if (parent != null && !youngerGenerations.contains(parent)
						&& !thisGeneration.contains(parent)) {
					nextGeneration.add(parent);
				}
			}
			thisGeneration = nextGeneration;
		}
		Shape commonAncestor = thisGeneration.iterator().next();
		return commonAncestor;
	}

	public static boolean isMovable(Shape shape) {
		return shape.getVisualState().isShowingItself()
				&& PaintLayer.NODE.equals(shape.getVisualState()
						.getPaintLayer());
	}

	public static Set<Shape> getEdges(GraphModel graph, Shape shape) {
		HashSet<Shape> edges = new HashSet<Shape>();
		for(Shape aShape : graph.getShapes()) {
			if(aShape instanceof EdgeShape) {
				EdgeShape edge = (EdgeShape) aShape;
				if(shape.equals(edge.getStartShape()) || shape.equals(edge.getEndShape())) {
					edges.add(edge);
				}
			}
		}
		return edges;
	}
	
	public static Map<Shape, Set<EdgeShape>> getExternalConnections(
			GraphModel graph, Shape groupShape) {
		Map<Shape, Set<EdgeShape>> connections = new HashMap<Shape, Set<EdgeShape>>();
		List<Shape> children = groupShape.getChildren();
		for(Shape shape : graph.getShapes()) {
			if (shape instanceof EdgeShape) {
				EdgeShape edge = (EdgeShape) shape;
				Shape startShape = edge.getStartShape();
				Shape endShape = edge.getEndShape();
				if (startShape != null && endShape != null) {
					Shape externalShape = null;
					if (children.contains(startShape)
							&& !children.contains(endShape)) {
						externalShape = endShape;
					} else if (children.contains(endShape)
							&& !children.contains(startShape)) {
						externalShape = startShape;
					}
					if (externalShape != null) {
						Set<EdgeShape> edges = connections.get(externalShape);
						if (edges == null) {
							edges = new HashSet<EdgeShape>();
							connections.put(externalShape, edges);
						}
						edges.add(edge);
					}
				}
			}
		}
		return connections;
	}

	public static VCEdgeBundle createEdgeBundle(String name, Set<EdgeShape> edgeShapes) {
		Set<VCEdge> edges = new HashSet<VCEdge>();
		for (EdgeShape edgeShape : edgeShapes) {
			Shape startShape = edgeShape.getStartShape();
			Shape endShape = edgeShape.getEndShape();
			Object startObject = startShape.getModelObject();
			Object endObject = endShape.getModelObject();
			Object edgeObject = edgeShape.getModelObject();
			edges.add(new VCEdge(startObject, endObject, edgeObject));
		}
		return new VCEdgeBundle(name, edges);

	}
	
	public static int getAbsCenterX(Shape shape) { return shape.getAbsX() + shape.getWidth() / 2; }
	public static int getAbsCenterY(Shape shape) { return shape.getAbsY() + shape.getHeight() / 2; }
	
	public static void setAbsCenter(Shape shape, int x, int y) { 
		shape.setAbsPos(x - shape.getWidth() / 2, y - shape.getHeight());
	}

}
