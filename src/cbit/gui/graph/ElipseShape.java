package cbit.gui.graph;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */

public abstract class ElipseShape extends Shape {

	public ElipseShape(GraphModel graphModel) {
		super(graphModel);
	}

	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();
		labelSize.height = fm.getMaxAscent() + fm.getMaxDescent();
		labelSize.width = fm.stringWidth(getLabel());
		preferredSize.height = labelSize.height + 10;
		preferredSize.width = labelSize.width + 10;
		return preferredSize;
	}

	final double getRadius(Point pick) {
		int centerX = relativePos.x + shapeSize.width / 2;
		int centerY = relativePos.y + shapeSize.height / 2;
		double radiusX = pick.x - centerX;
		double radiusY = pick.y - centerY;
		double b = shapeSize.height / 2;
		double a = shapeSize.width / 2;
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
	public void refreshLayout() throws LayoutException {

		if (LayoutException.bActivated) {
			if (shapeSize.width <= labelSize.width
					|| shapeSize.height <= labelSize.height) {
				throw new LayoutException("screen size smaller than label");
			}
		}
		// this is like a row/column layout (1 column)
		int centerX = shapeSize.width / 2;
		int centerY = shapeSize.height / 2;
		// position label
		labelPos.x = centerX - labelSize.width / 2;
		labelPos.y = centerY - labelSize.height / 2;
	}

	@Override
	public void paintSelf(Graphics2D g2D, int absPosX, int absPosY) {
		// draw elipse
		g2D.setColor(backgroundColor);
		g2D.fillOval(absPosX, absPosY, shapeSize.width, shapeSize.height);
		g2D.setColor(forgroundColor);
		g2D.drawOval(absPosX, absPosY, shapeSize.width, shapeSize.height);
		// draw label
		FontMetrics fm = g2D.getFontMetrics();
		int textX = absPosX + shapeSize.width / 2 - fm.stringWidth(getLabel())
				/ 2;
		int textY = absPosY + 5 + fm.getMaxAscent();
		if (getLabel() != null && getLabel().length() > 0) {
			g2D.drawString(getLabel(), textX, textY);
		}
		return;
	}

}