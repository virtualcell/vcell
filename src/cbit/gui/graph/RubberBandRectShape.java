package cbit.gui.graph;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.*;
/**
 * This type was created in VisualAge.
 */
public final class RubberBandRectShape extends RectangleShape {
	private Point start = new Point();
	private Point end = new Point();

/**
 * RubberBandEdgeShape constructor comment.
 * @param start java.awt.Point
 * @param end java.awt.Point
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public RubberBandRectShape(java.awt.Point start, java.awt.Point end, GraphModel graphModel) {
	super(graphModel);
	this.start = start;
	this.end = end;
	defaultFG = java.awt.Color.red;
	forgroundColor = defaultFG;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return null;
}


/**
 * This method was created in VisualAge.
 */
public void layout() {

	screenSize.width = Math.abs(end.x-start.x);
	screenSize.height = Math.abs(end.y-start.y);
	screenPos.x = Math.min(end.x,start.x);
	screenPos.y = Math.min(end.y,start.y);
	//
	// this is like a row/column layout  (1 column)
	//
	int centerX = screenSize.width/2;
	int centerY = screenSize.height/2;
	
	//
	// position label
	//
	labelPos.x = centerX - labelSize.width/2;
	labelPos.y = centerY - labelSize.height/2;
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
}


/**
 * This method was created in VisualAge.
 * @param start java.awt.Point
 * @param end java.awt.Point
 */
public void setEnd(Point end) throws Exception {
	
	this.end = end;

	layout();
}
}