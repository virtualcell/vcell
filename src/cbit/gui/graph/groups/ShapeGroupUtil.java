/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph.groups;

/* Useful static methods for dealing with groups
 * September 2010
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cbit.gui.graph.EdgeShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.gui.graph.ShapeUtil;
import cbit.gui.graph.visualstate.CollapsibleVisualState;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.VisualState.PaintLayer;

public class ShapeGroupUtil {

	/*
	 * Edges are not considered group members, although they may be internal to
	 * groups if they connect member nodes. Grouping of compartments is not
	 * supported.
	 */
	public static boolean isEligibleAsGroupMember(Shape shape) {
		return PaintLayer.NODE.equals(shape.getVisualState().getPaintLayer());
	}

	public static boolean willBeSkippedWhenGrouping(Shape shape) {
		return PaintLayer.EDGE.equals(shape.getVisualState().getPaintLayer());
	}

	/*
	 * Shapes can be made a group if they each are eligible and if for any two
	 * shapes the next non-eligible ancestor (e.g. compartment) is the same.
	 * This excludes nodes in different compartments
	 */
	public static boolean shapesCanBeMadeGroup(Set<Shape> shapes) {
		boolean bCanBeMadeGroup = true;
		Set<Shape> eligibleAncestors = new HashSet<Shape>();
		Set<Shape> ineligibleAncestors = new HashSet<Shape>();
		for (Shape shape : shapes) {
			if (!willBeSkippedWhenGrouping(shape)) {
				if (!isEligibleAsGroupMember(shape)) {
					bCanBeMadeGroup = false;
					break;
				}
				Shape parent = shape.getParent();
				if (parent != null) {
					if (isEligibleAsGroupMember(parent)) {
						eligibleAncestors.add(parent);
					} else {
						ineligibleAncestors.add(parent);
						if (ineligibleAncestors.size() > 1) {
							bCanBeMadeGroup = false;
							break;
						}
					}
				}
			}
		}
		while (bCanBeMadeGroup && !eligibleAncestors.isEmpty()) {
			Set<Shape> eligibleAncestorsNew = new HashSet<Shape>();
			for (Shape ancestor : eligibleAncestors) {
				Shape parent = ancestor.getParent();
				if (isEligibleAsGroupMember(parent)) {
					eligibleAncestorsNew.add(parent);
				} else {
					ineligibleAncestors.add(parent);
					if (ineligibleAncestors.size() > 1) {
						bCanBeMadeGroup = false;
						break;
					}
				}
			}
			eligibleAncestors = eligibleAncestorsNew;
		}
		return bCanBeMadeGroup;
	}

	public static boolean isGroup(Shape shape) {
		return shape.getVisualState() instanceof CollapsibleVisualState;
	}

	public static Shape findGroupParentShape(Collection<Shape> memberShapes) {
		if (memberShapes.isEmpty()) {
			return null;
		} else if (memberShapes.size() == 1) {
			return memberShapes.iterator().next().getParent();
		} else {
			return ShapeUtil.getNearestCommonAncestor(memberShapes);
		}
	}

	public static Point calculateGroupShapeAbsPos(Collection<Shape> memberShapes) {
		int xSum = 0;
		int ySum = 0;
		int posCount = 0;
		for (Shape memberShape : memberShapes) {
			if (!willBeSkippedWhenGrouping(memberShape)) {
				Point memberAbsPos = memberShape.getSpaceManager().getAbsLoc();
				xSum += memberAbsPos.x;
				ySum += memberAbsPos.y;
				++posCount;
			}
		}
		if (posCount == 0) {
			posCount = 1;
		}
		return new Point(xSum / posCount, ySum / posCount);
	}

	public static void moveMembersToGroup(Shape groupShape,
			Collection<Shape> memberShapes) {
		for (Shape memberShape : memberShapes) {
			if (!willBeSkippedWhenGrouping(memberShape)) {
				Point absLocation = memberShape.getSpaceManager().getAbsLoc();
				groupShape.addChildShape(memberShape);
				memberShape.getSpaceManager().setAbsLoc(absLocation);
			}
		}
	}

	public static void addEdgeBundleShapes(GraphModel graphModel,
			Shape groupShape, Shape groupParentShape) {
		Map<Shape, Set<EdgeShape>> connections = ShapeUtil
				.getExternalConnections(graphModel, groupShape);
		for (Map.Entry<Shape, Set<EdgeShape>> entry : connections.entrySet()) {
			Shape externalShape = entry.getKey();
			Set<EdgeShape> edgeShapes = entry.getValue();
			EdgeBundleShape edgeBundleShape = new EdgeBundleShape(graphModel,
					groupShape, externalShape, "", edgeShapes);
			groupParentShape.addChildShape(edgeBundleShape);
			graphModel.addShape(edgeBundleShape);
		}
	}

	public static GroupShape createGroup(GraphModel graphModel,
			String groupName, Collection<Shape> memberShapes) throws Exception {
		Set<Object> objects = new HashSet<Object>();
		for (Shape memberShape : memberShapes) {
			objects.add(memberShape.getModelObject());
		}
		VCNodeGroup group = new VCNodeGroup(groupName, objects);
		GroupShape groupShape = new GroupShape(graphModel, group);
		Point location = calculateGroupShapeAbsPos(memberShapes);
		Shape groupParentShape = findGroupParentShape(memberShapes);
		if(groupParentShape == null) {
			groupParentShape = graphModel.getTopShape();
		}
		groupParentShape.addChildShape(groupShape);
		groupShape.getSpaceManager().setAbsLoc(location);
		moveMembersToGroup(groupShape, memberShapes);
		addEdgeBundleShapes(graphModel, groupShape, groupParentShape);
		graphModel.addShape(groupShape);
		return groupShape;
	}

	public static void disbandGroup(GraphModel graphModel, Shape groupShape) {
		List<Shape> memberShapes = new ArrayList<Shape>(groupShape.getChildren());
		Shape parentShape = groupShape.getParent();
		if (parentShape != null) {
			for (Shape memberShape : memberShapes) {
				parentShape.addChildShape(memberShape);
			}
			parentShape.removeChild(groupShape);
		}
		groupShape.removeAllChildren();
		graphModel.removeShape(groupShape);
		for (Shape edge : ShapeUtil.getEdges(graphModel, groupShape)) {
			graphModel.removeShape(edge);
		}
	}

	public static boolean canBeCollapsed(Shape shape) {
		return shape.getVisualState() instanceof CollapsibleVisualState;
	}

	public static boolean collapseGroup(Shape groupShape) {
		return setCollapsed(groupShape, true);
	}

	public static boolean expandGroup(Shape groupShape) {
		return setCollapsed(groupShape, false);
	}

	public static boolean setCollapsed(Shape groupShape, boolean bCollapse) {
		boolean bSuccessful = false;
		VisualState visualState = groupShape.getVisualState();
		if (visualState instanceof CollapsibleVisualState) {
			((CollapsibleVisualState) visualState).setCollapsed(bCollapse);
			bSuccessful = true;
		}
		return bSuccessful;
	}

}
