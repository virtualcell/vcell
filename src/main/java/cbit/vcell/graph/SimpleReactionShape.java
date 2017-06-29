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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import cbit.vcell.model.SimpleReaction;

public class SimpleReactionShape extends ReactionStepShape {

	String linkText = "";
	
	public SimpleReactionShape(SimpleReaction simpleReaction,
			ModelCartoon modelCartoon) {
		super(simpleReaction, modelCartoon);
	}

	@Override
	public Point getAttachmentLocation(int attachmentType) {
		int centerX = getSpaceManager().getSize().width / 2;
		int centerY = getSpaceManager().getSize().height / 2;
		return new Point(centerX, centerY);
	}

	public SimpleReaction getSimpleReaction() {
		return (SimpleReaction) reactionStep;
	}

	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}

	private static int CIRCLE_DIMAETER = 9;
	public Rectangle getLabelOutline( int absPosX, int absPosY){
		int textX = absPosX + getSpaceManager().getSize().width / 2 - getLabelSize().width / 2;
		int textY = absPosY + ((getSpaceManager().getSize().height-CIRCLE_DIMAETER) / 2) - getLabelSize().height / 2;
		return new Rectangle(textX - 5, textY - getLabelSize().height + 3,
				getLabelSize().width + 10, getLabelSize().height);
	}
	@Override
	public void paintSelf(Graphics2D g2D, int absPosX, int absPosY) {
		
		// --- Added for relaxed topolgy
		Color newBackgroundColor = backgroundColor;
		if (getSimpleReaction().getKinetics().getKineticsDescription().isLumped()){
//			newBackgroundColor = newBackgroundColor.darker().darker().darker();
			float[] hbs = new float[3];
			Color.RGBtoHSB(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), hbs);
			hbs[0] -= 0.023f;		// make it just a little orange
			newBackgroundColor =  Color.getHSBColor(hbs[0], hbs[1], hbs[2]);
		}
		// --- End Add for relaxed topolgy
		int shapeHeight = getSpaceManager().getSize().height;
		int shapeWidth = getSpaceManager().getSize().width;
		int offsetX = (shapeWidth-CIRCLE_DIMAETER) / 2;
		int offsetY = (shapeHeight-CIRCLE_DIMAETER) / 2;
//		if (icon == null) {			// ----- removing for relaxed topolgy
			icon = new Area();
			//icon.add(new Area(new Ellipse2D.Double(offsetX, offsetY,circleDiameter,circleDiameter)));
			icon.add(new Area(new RoundRectangle2D.Double(offsetX, offsetY,CIRCLE_DIMAETER,CIRCLE_DIMAETER,CIRCLE_DIMAETER/2,CIRCLE_DIMAETER/2)));
//		}							// ----- removing for relaxed topolgy
		Area movedIcon = icon.createTransformedArea(
			AffineTransform.getTranslateInstance(absPosX, absPosY));

		// g2D.setColor(backgroundColor);		// arg Altered for relaxed topology ......... to newBackgroundColor 
		g2D.setColor(newBackgroundColor);
		g2D.fill(movedIcon);
		g2D.setColor(forgroundColor);
		g2D.draw(movedIcon);
		// draw label
		if (getDisplayLabels() || isSelected()) {
			g2D.setColor(forgroundColor);
			int textX = absPosX + shapeWidth / 2 - getLabelSize().width / 2;
			int textY = absPosY + offsetY - getLabelSize().height / 2;
			if (getLabel() != null && getLabel().length() > 0) {
				if (isSelected()) {
					Rectangle outlineRectangle = getLabelOutline(absPosX, absPosY);
					drawRaisedOutline(outlineRectangle.x, 
							outlineRectangle.y,
							outlineRectangle.width, outlineRectangle.height, g2D,
							Color.white, Color.black, Color.black);
				}
				g2D.drawString(getLabel(), textX, textY);
			}
		}
		if(linkText != null && linkText != "") {
			ShapePaintUtil.paintLinkMark(g2D, this, Color.BLACK);
		}
	}

	@Override
	public void refreshLabel() {
		setLabel(getSimpleReaction().getName());
	}
}
