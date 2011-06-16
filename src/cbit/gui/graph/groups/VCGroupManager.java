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

/*  Manages group operations on a graph view
 *  September 2010
 */

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cbit.gui.graph.GraphModel;
import cbit.gui.graph.GraphView;
import cbit.gui.graph.Shape;

public class VCGroupManager {

	public static interface GroupNamer {
		public String createName();
		
		public static class SimpleIndexer implements GroupNamer {

			protected long index = 0;
			
			public String createName() {
				String groupName = "group" + index;
				index++;
				return groupName;
			}
			
		}
	}
	
	protected final GraphView graphView;
	protected GroupNamer groupNamer;
	
	public VCGroupManager(GraphView graphView) {
		this(graphView, new GroupNamer.SimpleIndexer());
	}
	
	public VCGroupManager(GraphView graphView, GroupNamer groupNamer) {
		this.graphView = graphView;
		this.groupNamer = groupNamer;
	}
	
	public GraphView getGraphView() {
		return graphView;
	}
	
	public void collapseAsNewGroup() {
		GraphModel graphModel = graphView.getGraphModel();
		List<Shape> selectedShapes = graphModel.getSelectedShapes();
		try {
			GroupShape groupShape = ShapeGroupUtil.createGroup(graphModel,
					groupNamer.createName(), selectedShapes);
			ShapeGroupUtil.collapseGroup(groupShape);
			graphView.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void collapseExistingGroups() {
		GraphModel graphModel = graphView.getGraphModel();
		List<Shape> selectedShapes = graphModel.getSelectedShapes();
		for (Shape selectedShape : selectedShapes) {
			Shape parentShape = selectedShape.getParent();
			if(parentShape != null) {
				ShapeGroupUtil.collapseGroup(parentShape);				
			}
		}
		graphView.repaint();
	}
	
	public void expandSelectedGroups() {
		GraphModel graphModel = graphView.getGraphModel();
		List<Shape> selectedShapes = graphModel.getSelectedShapes();
		for (Shape selectedShape : selectedShapes) {
			ShapeGroupUtil.expandGroup(selectedShape);
		}
		graphView.repaint();
	}
	
	public void disbandSelectedGroups() {
		GraphModel graphModel = graphView.getGraphModel();
		List<Shape> selectedShapes = graphModel.getSelectedShapes();
		Set<Shape> groups = new HashSet<Shape>();
		for (Shape selectedShape : selectedShapes) {
			if(ShapeGroupUtil.isGroup(selectedShape)) {
				groups.add(selectedShape);
			} 
//			else {
//				Shape parent = selectedShape.getParent();
//				if(parent != null && ShapeGroupUtil.isGroup(parent)) {
//					groups.add(parent);
//				}
//			}
		}
		for(Shape group : groups) {
			ShapeGroupUtil.disbandGroup(graphModel, group);
		}
		graphView.repaint();

	}
	
}
