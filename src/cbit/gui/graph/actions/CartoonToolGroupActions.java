/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph.actions;

/*  Provides VCell Group Actions
 *  September 2010
 */

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import cbit.gui.graph.GraphView;
import cbit.gui.graph.Shape;
import cbit.gui.graph.groups.ShapeGroupUtil;

public class CartoonToolGroupActions {

	@SuppressWarnings("serial")
	public static class Disband extends GraphViewAction {
		public Disband(GraphView graphView, String key, String name,
				String shortDescription, String longDescription) {
			super(graphView, key, name, shortDescription, longDescription);
		}

		@Override
		public boolean canBeAppliedToShape(Shape shape) {
			if (ShapeGroupUtil.isGroup(shape)) {
				return true;
			} else {
				Shape parent = shape.getParent();
				if (parent != null && ShapeGroupUtil.isGroup(parent)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean isEnabledForShape(Shape shape) {
			if (ShapeGroupUtil.isGroup(shape)) {
				return true;
			} else {
				Shape parent = shape.getParent();
				if (parent != null && ShapeGroupUtil.isGroup(parent)) {
					return true;
				}
			}
			return false;
		}

		public void actionPerformed(ActionEvent event) {
			graphView.getGroupManager().disbandSelectedGroups();
		}
	}

	@SuppressWarnings("serial")
	private static class Expand extends GraphViewAction {
		private Expand(GraphView graphView, String key, String name,
				String shortDescription, String longDescription) {
			super(graphView, key, name, shortDescription, longDescription);
		}

		@Override
		public boolean canBeAppliedToShape(Shape shape) {
			return ShapeGroupUtil.isGroup(shape);
		}

		@Override
		public boolean isEnabledForShape(Shape shape) {
			return ShapeGroupUtil.isGroup(shape);
		}

		public void actionPerformed(ActionEvent event) {
			graphView.getGroupManager().expandSelectedGroups();
		}
	}

	@SuppressWarnings("serial")
	public static class CollapseExisting extends GraphViewAction {
		public CollapseExisting(GraphView graphView, String key,
				String name, String shortDescription,
				String longDescription) {
			super(graphView, key, name, shortDescription, longDescription);
		}

		@Override
		public boolean canBeAppliedToShape(Shape shape) {
			Shape parentShape = shape.getParent();
			if (parentShape != null) {
				return ShapeGroupUtil.isGroup(parentShape);
			}
			return false;
		}

		@Override
		public boolean isEnabledForShape(Shape shape) {
			Shape parentShape = shape.getParent();
			if (parentShape != null) {
				return ShapeGroupUtil.isGroup(parentShape);
			}
			return false;
		}

		public void actionPerformed(ActionEvent event) {
			graphView.getGroupManager().collapseExistingGroups();
		}
	}

	@SuppressWarnings("serial")
	public static class CollapseAsNew extends GraphViewAction {
		public CollapseAsNew(GraphView graphView, String key, String name,
				String shortDescription, String longDescription) {
			super(graphView, key, name, shortDescription, longDescription);
		}

		@Override
		public boolean canBeAppliedToShape(Shape shape) {
			return ShapeGroupUtil.isEligibleAsGroupMember(shape);
		}

		@Override
		public boolean isEnabledForShape(Shape shape) {
			return ShapeGroupUtil.isEligibleAsGroupMember(shape);
		}

		public void actionPerformed(ActionEvent event) {
			graphView.getGroupManager().collapseAsNewGroup();
		}
	}

	public static List<GraphViewAction> getDefaultActions(GraphView graphView) {
		List<GraphViewAction> list = new ArrayList<GraphViewAction>();
		list.add(new CollapseAsNew(graphView, "CollapseAsNewGroupAction", "Collapse As New Group", 
				"Collapse selected nodes as new group.", 
		"Create a new group from selected shapes and collapse."));

		list.add(new CollapseExisting(graphView, "CollapseExistingGroupsAction", 
				"Collapse Existing Groups", "Collapse all existing groups with selected members", 
		"For each selected shape that belongs to a group, collapse that group."));

		list.add(new Expand(graphView, "ExpandSelectedGroupsAction", "Expand Selected", 
				"Expand selected groups.", "Expand selected groups."));

		list.add(new Disband(graphView, "DisbandGroupsAction", "Disband Groups", 
				"Disband all groups with selected members", 
		"For each selected shape that belongs to a group, disband that group."));
		return list;
	}

}
