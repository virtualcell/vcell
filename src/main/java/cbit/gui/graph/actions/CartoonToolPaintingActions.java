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

/*  Organizes cartoon tool actions connected to the painting of shapes
 *  September 2010
 */

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.sbpax.util.StringUtil;

import cbit.gui.graph.GraphModel.NotReadyException;
import cbit.gui.graph.GraphView;
import cbit.gui.graph.Shape;
import cbit.gui.graph.visualstate.VisualStateUtil;

public class CartoonToolPaintingActions {

	@SuppressWarnings("serial")
	public static class ShowShapeTree extends GraphViewAction {
		public final boolean bActivated = false;
		public static final int maximumShapeTreeDepth = 20;

		public ShowShapeTree(GraphView graphView, String key, String name,
				String shortDescription, String longDescription) {
			super(graphView, key, name, shortDescription, longDescription);
		}

		@Override
		public boolean canBeAppliedToShape(Shape shape) {
			return bActivated;
		}

		@Override
		public boolean isEnabledForShape(Shape shape) {
			return bActivated;
		}

		public void showShapeTree(Shape shape) {
			showShapeTree(shape, 0);
		}

		public void showShapeTree(Shape shape, int depth) {
			String indent = StringUtil.multiply("  ", depth);
			System.out.println(indent + shape + " rel.pos.:" + shape.getSpaceManager().getRelPos()
					+ " abs.pos.:" + shape.getSpaceManager().getAbsLoc());
			if (depth < maximumShapeTreeDepth) {
				for (Shape child : shape.getChildren()) {
					showShapeTree(child, depth + 1);
				}
			}
		}

		public void actionPerformed(ActionEvent event) {
			try {
				showShapeTree(graphView.getGraphModel().getTopShape());
			} catch (NotReadyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("serial")
	public static class Unhide extends GraphViewAction {
		public Unhide(GraphView graphView, String key, String name,
				String shortDescription, String longDescription) {
			super(graphView, key, name, shortDescription, longDescription);
		}

		@Override
		public boolean canBeAppliedToShape(Shape shape) {
			return true;
		}

		@Override
		public boolean isEnabledForShape(Shape shape) {
			return true;
		}

		public void actionPerformed(ActionEvent event) {
			for(Shape shape : getGraphView().getGraphModel().getShapes()) {
				VisualStateUtil.show(shape);
			}
			getGraphView().repaint();
		}
	}

	@SuppressWarnings("serial")
	public static class Hide extends GraphViewAction {
		public Hide(GraphView graphView, String key, String name,
				String shortDescription, String longDescription) {
			super(graphView, key, name, shortDescription, longDescription);
		}

		@Override
		public boolean canBeAppliedToShape(Shape shape) {
			return VisualStateUtil.canBeHidden(shape);
		}

		@Override
		public boolean isEnabledForShape(Shape shape) {
			return VisualStateUtil.canBeHidden(shape);
		}

		public void actionPerformed(ActionEvent event) {
			List<Shape> selectedShapes = graphView.getGraphModel().getSelectedShapes();
			for(Shape selectedShape :selectedShapes) {
				VisualStateUtil.hide(selectedShape);
			}
			graphView.repaint();
		}
	}

	public static List<GraphViewAction> getDefaultActions(GraphView graphView) { 
		List<GraphViewAction> list = new ArrayList<GraphViewAction>();
		list.add(new Hide(graphView, "HideSelectedShapesAction", "Hide Selected", 
				"Hide selected shapes, where possible.", 
		"Hide selected shapes which support being hidden."));

		list.add(new Unhide(graphView, "UnhideAllShapesAction", "Unhide All", "Unhide all hidden shapes.", 
		"Unhide all hidden shapes."));

		list.add(new ShowShapeTree(graphView, "ShowShapeTreeAction", "Show Shape Tree", 
				"Print the shape tree.", 
		"Print the shape tree (parent-child relations) to the console."));
		return list; 
	}

}
