/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel.pathway;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.List;

import javax.swing.JViewport;

import org.vcell.util.gui.DialogUtils;

import cbit.gui.graph.GraphLayoutManager;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.RubberBandRectShape;
import cbit.gui.graph.Shape;
import cbit.gui.graph.actions.CartoonToolEditActions;
import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayDiagramPanel;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.BioPaxShape;
import cbit.vcell.client.desktop.biomodel.pathway.shapes.PathwayContainerShape;
import cbit.vcell.graph.BioCartoonTool;

public class PathwayGraphTool extends BioCartoonTool {
	
	private Mode mode = Mode.SELECT;
	private boolean bMoving = false;
	private Shape movingShape = null;	
	private Point movingOffsetWorld = null;
	private Point movingPointWorld = null;
	private boolean bRectStretch = false;
	private RubberBandRectShape rectShape = null;
	private Point startPointWorld = null;
	private Point endPointWorld = null;
	
	@Override
	public GraphModel getGraphModel() {
		return getGraphPane().getGraphModel();
	}
	
	public GraphLayoutManager getGraphLayoutManager() { return graphEmbeddingManager; }

	@Override
	public void mouseClicked(MouseEvent event) {
		Point worldPoint = screenToWorld(event.getX(), event.getY());
		try {
			// if right mouse button, then do popup menu
			if ((event.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
				return;
			}
			switch (mode) {
			case SELECT: {
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			System.out.println("CartoonTool.mouseClicked: uncaught exception");
			e.printStackTrace(System.out);
			Point canvasLoc = getGraphPane().getLocationOnScreen();
			canvasLoc.x += worldPoint.x;
			canvasLoc.y += worldPoint.y;
			DialogUtils.showErrorDialog(getGraphPane(), e.getMessage(), e);
		}
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if ((event.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
			return;
		}
		boolean bShift = (event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
		boolean bCntrl = (event.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
		try {
			switch (mode) {
			case SELECT: {
				Point worldPoint = screenToWorld(event.getX(), event.getY());
				if (bMoving) {
					List<Shape> selectedShapes = getGraphModel().getSelectedShapes();
					// constrain to stay within the corresponding parent for the
					// "movingShape" as well as all other selected (hence
					// moving) shapes.
					Point movingParentLoc = movingShape.getParent().getSpaceManager().getAbsLoc();
					Dimension movingParentSize = movingShape.getParent().getSpaceManager().getSize();
					worldPoint.x = Math.max(movingOffsetWorld.x
							+ movingParentLoc.x, Math.min(movingOffsetWorld.x
							+ movingParentLoc.x + movingParentSize.width
							- movingShape.getSpaceManager().getSize().width, worldPoint.x));
					worldPoint.y = Math.max(movingOffsetWorld.y
							+ movingParentLoc.y, Math.min(movingOffsetWorld.x
							+ movingParentLoc.y + movingParentSize.height
							- movingShape.getSpaceManager().getSize().height, worldPoint.y));
					for (Shape shape : selectedShapes) {
						if (shape != movingShape && shape instanceof BioPaxShape) {
							Point selectedParentLoc = shape.getParent().getSpaceManager().getAbsLoc();
							Dimension selectedParentSize = shape.getParent().getSpaceManager().getSize();
							int selectedMovingOffsetX = movingOffsetWorld.x + 
							(movingShape.getSpaceManager().getAbsLoc().x - 
									shape.getSpaceManager().getAbsLoc().x);
							int selectedMovingOffsetY = movingOffsetWorld.y + 
							(movingShape.getSpaceManager().getAbsLoc().y - 
									shape.getSpaceManager().getAbsLoc().y);
							worldPoint.x = Math.max(selectedMovingOffsetX + selectedParentLoc.x,
									Math.min(selectedMovingOffsetX + selectedParentLoc.x + 
											selectedParentSize.width - 
											shape.getSpaceManager().getSize().width, worldPoint.x));
							worldPoint.y = Math.max(selectedMovingOffsetY + selectedParentLoc.y,
									Math.min(selectedMovingOffsetY + selectedParentLoc.y + 
											selectedParentSize.height - 
											shape.getSpaceManager().getSize().height, worldPoint.y));
						}
					}
					getGraphPane().setCursor(
							Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					Point newMovingPoint = new Point(worldPoint.x
							- movingOffsetWorld.x, worldPoint.y
							- movingOffsetWorld.y);
					int deltaX = newMovingPoint.x - movingPointWorld.x;
					int deltaY = newMovingPoint.y - movingPointWorld.y;
					movingPointWorld = newMovingPoint;
					movingShape.getSpaceManager().setRelPos(movingPointWorld.x - movingParentLoc.x, 
					movingPointWorld.y - movingParentLoc.y);
					// for any other "movable" shapes that are selected, move
					// them also
					for (Shape shape : selectedShapes) {
						if (shape != movingShape) {
							shape.getSpaceManager().move(deltaX, deltaY);
						}
					}
					getGraphPane().invalidate();
					((JViewport) getGraphPane().getParent()).revalidate();
					getGraphPane().repaint();
				} else if (bRectStretch) {
					// constrain to stay within parent
					Point parentLoc = rectShape.getParent().getSpaceManager().getAbsLoc();
					Dimension parentSize = rectShape.getParent().getSpaceManager().getSize();
					worldPoint.x = Math.max(1, Math.min(parentSize.width - 1,
							worldPoint.x - parentLoc.x))
							+ parentLoc.x;
					worldPoint.y = Math.max(1, Math.min(parentSize.height - 1,
							worldPoint.y - parentLoc.y))
							+ parentLoc.y;
					getGraphPane().setCursor(
							Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					// getGraphPane().repaint();
					Graphics2D g = (Graphics2D) getGraphPane().getGraphics();
					AffineTransform oldTransform = g.getTransform();
					g.scale(0.01 * getGraphModel().getZoomPercent(),
							0.01 * getGraphModel().getZoomPercent());
					g.setXORMode(Color.white);
					rectShape.setEnd(endPointWorld);
					rectShape.paint(g, 0, 0);
					endPointWorld = worldPoint;
					rectShape.setEnd(endPointWorld);
					rectShape.paint(g, 0, 0);
					g.setTransform(oldTransform);
				} else {
					Shape shape = (getGraphModel().getSelectedShape() != null ?
						getGraphModel().getSelectedShape():getGraphModel().pickWorld(worldPoint));
					if (!bCntrl && !bShift && shape instanceof BioPaxShape){
						bMoving=true;
						movingShape = shape;
						movingPointWorld = shape.getSpaceManager().getAbsLoc();
						movingOffsetWorld = new Point(worldPoint.x-movingPointWorld.x,worldPoint.y-movingPointWorld.y);
					} 
					else if (shape instanceof PathwayContainerShape || bShift || bCntrl){
						bRectStretch = true;
						endPointWorld = new Point(worldPoint.x + 1,
								worldPoint.y + 1);
						rectShape = new RubberBandRectShape(worldPoint,
								endPointWorld, getGraphModel());
						rectShape.setEnd(endPointWorld);
						if (!(shape instanceof PathwayContainerShape)) {
							shape.getParent().addChildShape(rectShape);
						} else {
							shape.addChildShape(rectShape);
						}
						Graphics2D g = (Graphics2D) getGraphPane()
								.getGraphics();
						AffineTransform oldTransform = g.getTransform();
						g.scale(0.01 * getGraphModel().getZoomPercent(),
								0.01 * getGraphModel().getZoomPercent());
						g.setXORMode(Color.white);
						rectShape.paint(g, 0, 0);
						g.setTransform(oldTransform);
					}
				}
				break;
			}
			default: {
				break;
			}
			}
		} catch (Exception e) {
			System.out.println("CartoonTool.mouseDragged: uncaught exception");
			e.printStackTrace(System.out);
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if(getGraphModel() == null){ return; }
		try {
			int eventX = event.getX();
			int eventY = event.getY();
			startPointWorld = new java.awt.Point(
					(int) (eventX * 100.0 / getGraphModel().getZoomPercent()),
					(int) (eventY * 100.0 / getGraphModel().getZoomPercent()));
//			startShape = getGraphModel().pickWorld(startPointWorld);
			// Always select with MousePress
			boolean bShift = (event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
			boolean bCntrl = (event.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
			if (mode == Mode.SELECT
					|| (event.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
				selectEventFromWorld(startPointWorld, bShift, bCntrl);
			}
			// if mouse popupMenu event, popup menu
			if (event.isPopupTrigger() && mode == Mode.SELECT) {
				popupMenu(getGraphModel().getSelectedShape(), eventX, eventY);
				return;
			}
		} catch (Exception e) {
			System.out.println("CartoonTool.mousePressed: uncaught exception");
			e.printStackTrace(System.out);
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if(getGraphModel() == null){ return; }
		try {
			// Pick shape
//			Point worldPoint = screenToWorld(event.getX(), event.getY());
//			Shape endShape = getGraphModel().pickWorld(worldPoint);
			// if mouse popupMenu event, popup menu
			if (event.isPopupTrigger() && mode == Mode.SELECT) {
				popupMenu(getGraphModel().getSelectedShape(), event.getX(), event.getY());
				return;
			}
			if ((event.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
				return;
			}
			// else do select and move
			switch (mode) {
			case SELECT: {
				getGraphPane().setCursor(Cursor.getDefaultCursor());
				if (bMoving){
					getGraphPane().invalidate();
					((JViewport) getGraphPane().getParent()).revalidate();
					getGraphPane().repaint();
					saveDiagram();
				} else if (bRectStretch) {
					Point absLoc = rectShape.getSpaceManager().getRelPos();
					Dimension size = rectShape.getSpaceManager().getSize();
					// remove temporary rectangle
					getGraphModel().removeShape(rectShape);
					rectShape = null;
					Rectangle rect = new Rectangle(absLoc.x, absLoc.y, size.width, size.height);
					boolean bShift = (event.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
					boolean bCntrl = (event.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
					selectEventFromWorld(rect, bShift, bCntrl);
					getGraphPane().repaint();
				}
				bMoving = false;
				movingShape = null;
				bRectStretch = false;
				rectShape = null;
				break;
			}
			default: {
				break;
			}
			}
		} catch (Exception e) {
			System.out.println("CartoonTool.mouseReleased: uncaught exception");
			e.printStackTrace(System.out);
		}
		resetMouseActionHistory();
		getGraphPane().repaint();
	}
	
	private void selectEventFromWorld(Point worldPoint, boolean bShift, boolean bCntrl) {
		if (getGraphModel() == null) {
			return;
		}
		if (!bShift && !bCntrl) {
			Shape pickedShape = getGraphModel().pickWorld(worldPoint);
			if (pickedShape == null || !pickedShape.isSelected()) {
				getGraphModel().clearSelection();
			}
			if (pickedShape != null && pickedShape.isSelected()) {
				return;
			}
			if (pickedShape != null) {
				getGraphModel().selectShape(pickedShape);
			}

		} else if (bShift) {
			if (getGraphModel().getSelectedShape() instanceof PathwayContainerShape) {
				getGraphModel().clearSelection();
			}
			Shape pickedShape = getGraphModel().pickWorld(worldPoint);
			if (pickedShape == null) {
				return;
			}
			if (pickedShape instanceof PathwayContainerShape) {
				return;
			}
			getGraphModel().selectShape(pickedShape);
		} else if (bCntrl) {
			if (getGraphModel().getSelectedShape() instanceof PathwayContainerShape) {
				getGraphModel().clearSelection();
			}
			Shape pickedShape = getGraphModel().pickWorld(worldPoint);
			if (pickedShape == null) {
				return;
			}
			if (pickedShape instanceof PathwayContainerShape) {
				return;
			}
			if (pickedShape.isSelected()) {
				getGraphModel().deselectShape(pickedShape);
			} else {
				getGraphModel().selectShape(pickedShape);
			}
		}
	}

	private void selectEventFromWorld(Rectangle rect, boolean bShift,
			boolean bCntrl) {
		if (!bShift && !bCntrl) {
			getGraphModel().clearSelection();
			List<Shape> shapes = getGraphModel().pickWorld(rect);
			for (Shape shape : shapes) {
				if (shape instanceof BioPaxShape) {
					getGraphModel().selectShape(shape);
				}
			}
		} else if (bShift) {
			if (getGraphModel().getSelectedShape() instanceof PathwayContainerShape) {
				getGraphModel().clearSelection();
			}
			List<Shape> shapes = getGraphModel().pickWorld(rect);
			for (Shape shape : shapes) {
				if (shape instanceof BioPaxShape) {
					getGraphModel().selectShape(shape);
				}
			}
		} else if (bCntrl) {
			if (getGraphModel().getSelectedShape() instanceof PathwayContainerShape) {
				getGraphModel().clearSelection();
			}
			List<Shape> shapes = getGraphModel().pickWorld(rect);
			for (Shape shape : shapes) {
				if (shape instanceof BioPaxShape) {
					if (shape.isSelected()) {
						getGraphModel().deselectShape(shape);
					} else {
						getGraphModel().selectShape(shape);
					}
				}
			}
		}
	}
	
	public void saveDiagram() {
		// TODO
//		for(Structure structure : getReactionCartoon().getStructureSuite().getStructures()) {
//			getReactionCartoon().setPositionsFromReactionCartoon(
//					getModel().getDiagram(structure));			
//		}
	}
	
	protected void resetMouseActionHistory() {
		bMoving = false;
		movingShape = null;
		bRectStretch = false;
		rectShape = null;
//		startShape = null;
		startPointWorld = null;
		endPointWorld = null;
	}
	
	@Override
	protected void menuAction(Shape shape, String menuAction) {
		 if (menuAction.equals(CartoonToolEditActions.Delete.MENU_ACTION)) {
			try {
				BioModelEditorPathwayDiagramPanel.deleteSelectedBioPaxObjects(getGraphPane(), ((PathwayGraphModel)getGraphModel()).getBioModel(), getGraphModel());
			} catch (Exception e) {
				DialogUtils.showErrorDialog(getGraphPane(), e.getMessage(), e);
			}
		 }
	}

	@Override
	public boolean shapeHasMenuAction(Shape shape, String menuAction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shapeHasMenuActionEnabled(Shape shape, String menuAction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateMode(Mode newMode) {
		// TODO Auto-generated method stub

	}

	public void saveNodePositions() {
		saveDiagram();
	}
	
}
