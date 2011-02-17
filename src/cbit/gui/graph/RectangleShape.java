package cbit.gui.graph;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 �*/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.ImmutableVisualState;

public abstract class RectangleShape extends Shape {

	public RectangleShape(GraphModel graphModel) {
		super(graphModel);
	}

	@Override
	public VisualState createVisualState() {
		return new ImmutableVisualState(this, VisualState.PaintLayer.COMPARTMENT);
	}

	protected void drawLabel(Graphics2D g2D, int absPosX, int absPosY) {
		if (getLabel() != null && getLabel().length() > 0) {
			int textX = absPosX
					+ Math.max(0, getSpaceManager().getSize().width / 2 - getLabelSize().width / 2);
			int textY = absPosY + Math.max(0, 5 + getLabelSize().height);
			if (isSelected()) {
				drawRaisedOutline(textX - 5, textY - getLabelSize().height + 3,
						getLabelSize().width + 10, getLabelSize().height, g2D,
						Color.white, Color.black, Color.gray);
			}
			Color origColor = g2D.getColor();
			g2D.setColor(Color.black);
			Font origFont = g2D.getFont();
			g2D.setFont(getLabelFont(g2D));
			g2D.drawString(getLabel(), textX, textY);
			g2D.setColor(origColor);
			g2D.setFont(origFont);
		}
	}

	public Font getLabelFont(Graphics g) {
		return getBoldFont(g);
	}

	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		if (g == null) {
			setLabelSize(10, 20);
		} else {
			try {
				FontMetrics fm = g.getFontMetrics();
				if (getLabel() != null) {
					setLabelSize(fm.stringWidth(getLabel()), fm.getMaxAscent() + fm.getMaxDescent());
				} else {
					setLabelSize(1, fm.getMaxAscent() + fm.getMaxDescent());
				}
			} catch (NullPointerException e) {
				setLabelSize(10, 20);
			}
		}
		getSpaceManager().setSizePreferred((getLabelSize().height + 10), (getLabelSize().width + 10));
		return getSpaceManager().getSizePreferred();
	}

	@Override
	protected final boolean isInside(Point p) {
		// bring into local coordinates
		int x = p.x - getSpaceManager().getRelX();
		int y = p.y - getSpaceManager().getRelY();
		// check to see if inside rectangle
		if (x > 0 && x < getSpaceManager().getSize().width && y > 0 && y < getSpaceManager().getSize().height) {
			return true;
		} else {
			return false;
		}
	}

	public void refreshLayoutSelf() {
		if (getSpaceManager().getSize().width <= getLabelSize().width || 
				getSpaceManager().getSize().height <= getLabelSize().height) {
		}
		// this is like a row/column layout (1 row)
		int centerX = getSpaceManager().getSize().width / 2;
		int centerY = getSpaceManager().getSize().height / 2;
		// position label
		labelPos.x = centerX - getLabelSize().width / 2; 
		labelPos.y = centerY - getLabelSize().height / 2;
	}

	@Override
	public void paintSelf(Graphics2D g2D, int absPosX, int absPosY) {
		// draw rectangle
		g2D.setColor(backgroundColor);
		if (!bNoFill) {
			g2D.fillRect(absPosX, absPosY, getSpaceManager().getSize().width, getSpaceManager().getSize().height);
		}
		g2D.setColor(forgroundColor);
		g2D.drawRect(absPosX, absPosY, getSpaceManager().getSize().width, getSpaceManager().getSize().height);
		drawLabel(g2D, absPosX, absPosY);
	}
}