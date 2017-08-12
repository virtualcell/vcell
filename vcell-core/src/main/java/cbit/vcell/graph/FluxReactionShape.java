/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import cbit.vcell.model.FluxReaction;

public class FluxReactionShape extends ReactionStepShape {

	public FluxReactionShape(FluxReaction fluxReaction, ModelCartoon modelCartoon) {
		super(fluxReaction, modelCartoon);
	}

	@Override
	public Point getAttachmentLocation(int attachmentType) {
		int centerX = getSpaceManager().getSize().width / 2;
		int centerY = getSpaceManager().getSize().height / 2;
		return new Point(centerX, centerY);
	}

	public FluxReaction getFluxReaction() {
		return (FluxReaction) reactionStep;
	}

	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		getSpaceManager().setSizePreferred(25, 25);
		if(getLabel() != null && getLabel().length() > 0){
			FontMetrics fontMetrics = g.getFontMetrics();
			setLabelSize(fontMetrics.stringWidth(getLabel()), 
					fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent());
		}
		return getSpaceManager().getSizePreferred();
	}

	private static int RND_RECT_WIDTH = 16;
	private static int RND_RECT_HEIGHT = 12;

	public Rectangle getLabelOutline( int absPosX, int absPosY){
		int textX = absPosX  + getSpaceManager().getSize().width/2 - getLabelSize().width/2;
		int textY = absPosY + ((getSpaceManager().getSize().height - RND_RECT_HEIGHT)/2) - getLabelSize().height / 2;

		return new Rectangle(textX - 5, textY - getLabelSize().height + 3,
		getLabelSize().width + 10, getLabelSize().height);
	}
	@Override
	public void paintSelf(Graphics2D g2D, int absPosX, int absPosY) {
		// draw and fill rounded rectangle
		int height = getSpaceManager().getSize().height;
		int width = getSpaceManager().getSize().width;
		g2D.setColor(backgroundColor);
		int offsetX = (width - RND_RECT_WIDTH)/2;
		int roundRectX = absPosX + offsetX;
		int offsetY = (height - RND_RECT_HEIGHT)/2;
		int roundRectY = absPosY + offsetY;
		int arcWidth = 10;
		g2D.fillRoundRect(roundRectX, roundRectY, RND_RECT_WIDTH, RND_RECT_HEIGHT, arcWidth, arcWidth);
		//	g.fillRoundRect(absPosX+1,absPosY+1,screenSize.width-1,screenSize.height-1,15,15);
		g2D.setColor(forgroundColor);
		g2D.drawRoundRect(roundRectX, roundRectY, RND_RECT_WIDTH, RND_RECT_HEIGHT, arcWidth, arcWidth);
		// draw and white out center channel
		g2D.setColor(Color.white);
//		g2D.setColor(getParent().getBackgroundColor());
		g2D.fillRect(roundRectX - 1, roundRectY + RND_RECT_HEIGHT/3, RND_RECT_WIDTH + 2, RND_RECT_HEIGHT/3);
		g2D.setColor(forgroundColor);
		g2D.drawLine(roundRectX, roundRectY + RND_RECT_HEIGHT/3, roundRectX + RND_RECT_WIDTH, roundRectY + RND_RECT_HEIGHT/3);
		g2D.drawLine(roundRectX, roundRectY + RND_RECT_HEIGHT*2/3, roundRectX + RND_RECT_WIDTH, roundRectY + RND_RECT_HEIGHT*2/3);

		// draw label
		if (getDisplayLabels() || isSelected()) {
			g2D.setColor(forgroundColor);
			int textX = absPosX  + width/2 - getLabelSize().width/2;
			int textY = absPosY + offsetY - getLabelSize().height / 2;
			if (getLabel()!=null && getLabel().length()>0){
				if(isSelected()){
					Rectangle outlineRectangle = getLabelOutline(absPosX, absPosY);
					drawRaisedOutline(outlineRectangle.x, outlineRectangle.y,
							outlineRectangle.width, outlineRectangle.height,
							g2D, Color.white, Color.black, Color.black);
					g2D.drawString(getLabel(),textX,textY);
				}
			}
		}
		return;
	}

	@Override
	public void refreshLabel() {
		setLabel(getFluxReaction().getName());
	}
}
