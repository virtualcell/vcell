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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import org.vcell.model.rbm.MolecularType;

import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.MutableVisualState;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Model.RbmModelContainer;

public class ReactionRuleDiagramShape extends ElipseShape {
	ReactionRule reactionRule = null;
	Area icon = null;
	private static boolean bDisplayLabels = false;

	public ReactionRuleDiagramShape(ReactionRule reactionRule, GraphModel graphModel) {
		super(graphModel);
		this.reactionRule = reactionRule;
		defaultBG = java.awt.Color.yellow;
		defaultFGselect = java.awt.Color.black;
		backgroundColor = defaultBG;
	}

	@Override
	public VisualState createVisualState() { 
		return new MutableVisualState(this, VisualState.PaintLayer.NODE); 
	}

	public static boolean getDisplayLabels() {
		return bDisplayLabels;
	}

	@Override
	public Object getModelObject() {
		return reactionRule;
	}

	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		getSpaceManager().setSizePreferred(12, 12);
		if(getLabel() != null && getLabel().length() > 0){
			FontMetrics fontMetrics = g.getFontMetrics();
			setLabelSize(fontMetrics.stringWidth(getLabel()), 
					fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent());
		}
		return getSpaceManager().getSizePreferred();
	}

	public ReactionRule getReactionRule() {
		return reactionRule;
	}

	public final void refreshLayoutSelf() {
		int centerX = getSpaceManager().getSize().width/2;
		// position label
		labelPos.x = centerX - getLabelSize().width/2; 
		labelPos.y = 0;
	}

	public static void setDisplayLabels(boolean argDisplayLabels) {
		bDisplayLabels = argDisplayLabels;
	}

	@Override
	public void refreshLabel() {
		setLabel(reactionRule.getDisplayName());
	}
	
	@Override
	public void paintSelf(Graphics2D g2D, int absPosX, int absPosY) {
		Color newBackgroundColor = backgroundColor;
		int shapeHeight = getSpaceManager().getSize().height;
		int shapeWidth = getSpaceManager().getSize().width;
		int offsetX = (shapeWidth-CIRCLE_DIMAETER) / 2;
		int offsetY = (shapeHeight-CIRCLE_DIMAETER) / 2;
		for(int i=0; i<3; i++) {
		icon = new Area();
		icon.add(new Area(new RoundRectangle2D.Double(offsetX+i*3, offsetY-i*3,CIRCLE_DIMAETER,CIRCLE_DIMAETER,CIRCLE_DIMAETER/2,CIRCLE_DIMAETER/2)));
		Area movedIcon = icon.createTransformedArea(AffineTransform.getTranslateInstance(absPosX, absPosY));

		g2D.setColor(newBackgroundColor);
		g2D.fill(movedIcon);
		g2D.setColor(forgroundColor);
		g2D.draw(movedIcon);
		}
		// draw label
		if (getDisplayLabels() || isSelected()) {
			g2D.setColor(forgroundColor);
			int textX = absPosX + shapeWidth / 2 - getLabelSize().width / 2;
			int textY = absPosY + offsetY - getLabelSize().height / 2;
			if (getLabel() != null && getLabel().length() > 0) {
				if (isSelected()) {
					Rectangle outlineRectangle = getLabelOutline(absPosX, absPosY);
					drawRaisedOutline(outlineRectangle.x, outlineRectangle.y, outlineRectangle.width, outlineRectangle.height, g2D, Color.white, Color.black, Color.black);
				}
				g2D.drawString(getLabel(), textX, textY);
			}
		}
		return;
	}
	

	private static int CIRCLE_DIMAETER = 10;
	public Rectangle getLabelOutline( int absPosX, int absPosY){
		int textX = absPosX + getSpaceManager().getSize().width / 2 - getLabelSize().width / 2;
		int textY = absPosY + ((getSpaceManager().getSize().height-CIRCLE_DIMAETER) / 2) - getLabelSize().height / 2;
		return new Rectangle(textX - 5, textY - getLabelSize().height + 3,
				getLabelSize().width + 10, getLabelSize().height);
	}

}
