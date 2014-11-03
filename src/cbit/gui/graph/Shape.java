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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.VisualState.PaintLayer;

public abstract class Shape implements VisualState.Owner, ShapeSpaceManager.Owner {

	protected ShapeSpaceManager spaceManager = new ShapeSpaceManager(this);

	protected Dimension labelSize = new Dimension();
	protected Point labelPos = new Point();
	private String label = null;

	protected List<Shape> childShapeList = new ArrayList<Shape>();
	private Shape parent = null;

	protected boolean bNoFill = false;

	public static final int ATTACH_CENTER = 0;
	public static final int ATTACH_LEFT = 1;
	public static final int ATTACH_RIGHT = 2;

	protected Color defaultBG = Color.white;
	protected Color defaultFG = Color.black;
	protected Color defaultBGselect = Color.red;
	protected Color defaultFGselect = Color.white;

	protected Color backgroundColor = defaultBG;
	protected Color forgroundColor = defaultFG;

	protected GraphModel graphModel = null;

	private Font boldFont = null;

	protected final VisualState visualState = createVisualState();

	public Shape(GraphModel graphModel) {
		this.graphModel = graphModel;
	}

	public GraphModel getGraphModel() { return graphModel; }
	
	public ShapeSpaceManager getSpaceManager() { return spaceManager; }
	
	public void setRelPos(Point relPos) { spaceManager.setRelPos(relPos); }
	public void setRelPos(int x, int y) { spaceManager.setRelPos(x, y); }
	public Point getRelPos() { return spaceManager.getRelPos(); }
	public int getRelX() { return spaceManager.getRelX(); }
	public int getRelY() { return spaceManager.getRelY(); }
	public void setAbsPos(Point absPos) { spaceManager.setAbsLoc(absPos); }
	public void setAbsPos(int x, int y) { spaceManager.setAbsLoc(x, y); }
	public void setAbsX(int x) { spaceManager.setAbsX(x); }
	public void setAbsY(int y) { spaceManager.setAbsY(y); }
	public Point getAbsPos() { return spaceManager.getAbsLoc(); }
	public int getAbsX() { return spaceManager.getAbsLoc().x; }
	public int getAbsY() { return spaceManager.getAbsLoc().y; }
	public void setSize(Dimension size) { spaceManager.setSize(size); }
	public void setSize(int width, int height) { spaceManager.setSize(width, height); }
	public Dimension getSize() { return spaceManager.getSize(); }
	public int getWidth() { return spaceManager.getSize().width; }
	public int getHeight() { return spaceManager.getSize().height; }
	
	
	public void setLabelSize(int width, int height) { labelSize.width = width; labelSize.height = height; }
	public Dimension getLabelSize() { return labelSize; }
	public Point getLabelPos() { return labelPos; }
	
	public abstract VisualState createVisualState();

	public VisualState getVisualState() {
		return visualState;
	}
	
	public void addChildShape(Shape shape) {
		Point absLocation = shape.spaceManager.getAbsLoc();
		if (shape.parent != null) {
			shape.parent.removeChild(shape);
		}
		childShapeList.add(shape);
		shape.parent = this;
		shape.getSpaceManager().setAbsLoc(absLocation);
	}

	public boolean contains(Shape shape) {
		for (int i = 0; i < childShapeList.size(); i++) {
			if ((childShapeList.get(i)).equals(shape)) {
				return true;
			}
		}
		return false;
	}

	public final Color getBackgroundColor() {
		return backgroundColor;
	}
	public final Color getForegroundColor() {
		return forgroundColor;
	}
	public void setForgroundColor(Color color) {
		forgroundColor = color;
	}

	public int countChildren() {
		return childShapeList.size();
	}

	public final static void drawRaisedOutline(int x, int y, int width,
			int height, Graphics g, Color back, Color fore, Color shadow) {
		Color origColor = g.getColor();
		g.setColor(shadow);
		g.drawRect(x + 1, y + 1, width, height);
		g.drawRect(x + 2, y + 2, width, height);
		g.setColor(back);
		g.fillRect(x, y, width, height);
		g.setColor(fore);
		g.drawRect(x, y, width, height);
		g.setColor(origColor);
	}

	public int getAttachmentFromAbs(Point screenPoint) throws Exception {
		Point offset = spaceManager.getAbsLoc();
		Point localPos = new Point(screenPoint.x - offset.x, screenPoint.y
				- offset.y);
		// get distance from Center
		Point attach = getAttachmentLocation(ATTACH_CENTER);
		double d2Center = (localPos.x - attach.x) * (localPos.x - attach.x)
				+ (localPos.y - attach.y) * (localPos.y - attach.y);
		int attachType = ATTACH_CENTER;
		double attachDistance = d2Center;
		// get distance from Left
		attach = getAttachmentLocation(ATTACH_LEFT);
		double d2Left = (localPos.x - attach.x) * (localPos.x - attach.x)
				+ (localPos.y - attach.y) * (localPos.y - attach.y);
		if (d2Left < attachDistance) {
			attachType = ATTACH_LEFT;
			attachDistance = d2Left;
		}
		// get distance from Right
		attach = getAttachmentLocation(ATTACH_RIGHT);
		double d2Right = (localPos.x - attach.x) * (localPos.x - attach.x)
				+ (localPos.y - attach.y) * (localPos.y - attach.y);
		if (d2Right < attachDistance) {
			attachType = ATTACH_RIGHT;
			attachDistance = d2Right;
		}
		return attachType;
	}

	public Point getAttachmentLocation(int attachmentType) {
		return new Point(getSpaceManager().getSize().width / 2, getSpaceManager().getSize().height / 2);
	}

	public Font getBoldFont(Graphics g) {
		if (boldFont == null) {
			boldFont = g.getFont().deriveFont(Font.BOLD);
		}
		return boldFont;
	}

	public List<Shape> getChildren() {
		return childShapeList;
	}

	public final String getLabel() {
		if (label == null) {
			refreshLabel();
		}
		return label;
	}

	public abstract Object getModelObject();

	public Shape getParent() {
		return parent;
	}

	abstract public Dimension getPreferedSizeSelf(Graphics2D g);
	
	boolean isDescendant(Shape shape) {
		for (int i = 0; i < childShapeList.size(); i++) {
			Shape child = childShapeList.get(i);
			// check if child is the shape
			if (child == shape) {
				return true;
			}
			// check if child encapsulates shape
			boolean childHasShape = child.isDescendant(shape);
			if (childHasShape)
				return true;
		}
		return false;
	}

	protected abstract boolean isInside(Point p);

	public boolean isSelected() {
		return graphModel.isShapeSelected(this);
	}
	
	public abstract void refreshLayoutSelf();
	
	public void paint(Graphics2D graphics, int xParent, int yParent) {
		for(PaintLayer layer : PaintLayer.values()) {
			paint(graphics, xParent, yParent, layer);
		}
	}
	
	public void paint(Graphics2D graphics, int xParent, int yParent, PaintLayer layer) {
		int xThis = getSpaceManager().getRelX() + xParent;
		int yThis = getSpaceManager().getRelY() + yParent;
		if (visualState.isAllowedToShowByAllAncestors()) {
			if (visualState.isShowingItself() && visualState.getPaintLayer().equals(layer)) {
				paintSelf(graphics, xThis, yThis);
			}
			if (visualState.isShowingDescendents()) {
				for (Shape child : getChildren()) {
					child.paint(graphics, xThis, yThis, layer);
				}
			}
		}
	}

	public abstract void paintSelf(Graphics2D graphics, int xAbs, int yAbs);

	public final Shape pick(Point point) {
		Shape shape = null;
		for(PaintLayer layer : PaintLayer.valuesReverse) {
			shape = pick(point, layer);
			if(shape != null) { break; }
		}
		return shape;
	}
	
	public final Shape pick(Point point, PaintLayer layer) {
		if (visualState.isAllowedToShowByAllAncestors()) {
			if (visualState.isShowingDescendents()) {
				Point childPoint = new Point(point.x - getSpaceManager().getRelX(), 
						point.y - getSpaceManager().getRelY());
				for (int iChild = childShapeList.size() - 1; iChild >= 0; --iChild) {
					Shape child = childShapeList.get(iChild);
					Shape childPick = child.pick(childPoint, layer);
					if (childPick != null)
						return childPick;
				}
			}
			if (visualState.isShowingItself() && visualState.getPaintLayer().equals(layer)) {
				if (isInside(point)) {
					return this;
				}
			}
		}
		return null;
	}

	public abstract void refreshLabel();

	public void removeAllChildren() {
		childShapeList.clear();
	}

	public void removeChild(Shape shape) {
		if (childShapeList.contains(shape)) {
			childShapeList.remove(shape);
		}
	}

	public void notifySelected() {
		backgroundColor = defaultBGselect;
		forgroundColor = defaultFGselect;
	}

	public void notifyUnselected() {
		backgroundColor = defaultBG;
		forgroundColor = defaultFG;
	}

	public final void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return getClass().getName() + "@" + Integer.toHexString(hashCode())
				+ "(" + getLabel() + ")";
		// ="+getModelObject();
	}

}
