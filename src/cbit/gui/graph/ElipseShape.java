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

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;


public abstract class ElipseShape extends Shape {

	public ElipseShape(GraphModel graphModel) {
		super(graphModel);
	}

	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		ShapeSpaceManager spaceManager = getSpaceManager();
		FontMetrics fm = g.getFontMetrics();
		setLabelSize(fm.stringWidth(getLabel()), fm.getMaxAscent() + fm.getMaxDescent());
		spaceManager.setSizePreferred(getLabelSize().width + 10, getLabelSize().height + 10);
		return spaceManager.getSizePreferred();
	}

	final double getRadius(Point pick) {
		ShapeSpaceManager spaceManager = getSpaceManager();
		int centerX = spaceManager.getRelX() + spaceManager.getSize().width / 2;
		int centerY = spaceManager.getRelY() + spaceManager.getSize().height / 2;
		double radiusX = pick.x - centerX;
		double radiusY = pick.y - centerY;
		double b = spaceManager.getSize().height / 2;
		double a = spaceManager.getSize().width / 2;
		double radius = radiusX * radiusX / (a * a) + radiusY * radiusY
				/ (b * b);

		return radius;
	}

	@Override
	public final boolean isInside(Point p) {
		if (getRadius(p) < 1.0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void refreshLayoutSelf() {
		if (getSpaceManager().getSize().width <= getLabelSize().width
				|| getSpaceManager().getSize().height <= getLabelSize().height) {
			LayoutErrorLog.logErrorMessage("screen size smaller than label");
		}
		// this is like a row/column layout (1 column)
		int centerX = getSpaceManager().getSize().width / 2;
		int centerY = getSpaceManager().getSize().height / 2;
		// position label
		labelPos.x = centerX - getLabelSize().width / 2; 
		labelPos.y = centerY - getLabelSize().height / 2;
	}
	
	@Override
	public void paintSelf(Graphics2D g2D, int absPosX, int absPosY) {
		// draw elipse
		g2D.setColor(backgroundColor);
		g2D.fillOval(absPosX, absPosY, getSpaceManager().getSize().width, getSpaceManager().getSize().height);
		g2D.setColor(forgroundColor);
		g2D.drawOval(absPosX, absPosY, getSpaceManager().getSize().width, getSpaceManager().getSize().height);
		// draw label
		FontMetrics fm = g2D.getFontMetrics();
		int textX = absPosX + getSpaceManager().getSize().width / 2 - fm.stringWidth(getLabel())
				/ 2;
		int textY = absPosY + 5 + fm.getMaxAscent();
		if (getLabel() != null && getLabel().length() > 0) {
			g2D.drawString(getLabel(), textX, textY);
		}
		return;
	}

}
