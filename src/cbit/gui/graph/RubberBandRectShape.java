package cbit.gui.graph;

import java.awt.Point;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */

public final class RubberBandRectShape extends RectangleShape {

	private Point start = new Point();
	private Point end = new Point();

	public RubberBandRectShape(Point start, Point end, GraphModel graphModel) {
		super(graphModel);
		this.start = start;
		this.end = end;
		defaultFG = java.awt.Color.red;
		forgroundColor = defaultFG;
	}

	@Override
	public Object getModelObject() {
		return null;
	}

	public void refreshLayoutSelf() {
		getSpaceManager().setSize(Math.abs(end.x-start.x), Math.abs(end.y-start.y));
		getSpaceManager().setRelPos(Math.min(end.x,start.x), Math.min(end.y,start.y));
		// this is like a row/column layout  (1 column)
		int centerX = getSpaceManager().getSize().width/2;
		int centerY = getSpaceManager().getSize().height/2;
		// position label
		labelPos.x = centerX - getLabelSize().width/2; 
		labelPos.y = centerY - getLabelSize().height/2;		
	}
	
	@Override
	public void refreshLabel() {
	}

	public void setEnd(Point end) throws Exception {
		this.end = end;
		refreshLayoutSelf();
	}

}