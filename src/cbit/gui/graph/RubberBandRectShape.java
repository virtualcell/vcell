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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

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

	@Override
	public void paintSelf(Graphics2D g2D, int absPosX, int absPosY) {
		g2D.setColor(forgroundColor);
		g2D.drawRect(absPosX, absPosY, getSpaceManager().getSize().width, getSpaceManager().getSize().height);
	}

}
