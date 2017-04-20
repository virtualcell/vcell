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
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import org.vcell.model.rbm.MolecularType;
import org.vcell.util.gui.ShapePaintUtil;

import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.MutableVisualState;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Model.RbmModelContainer;

public class ReactionRuleDiagramShape extends ElipseShape {
	ReactionRule reactionRule = null;
//	Area icon = null;
	private static boolean bDisplayLabels = false;

	private static final int height = 12;			// area considered "the inside" of the shape
	private static final int width = 12;
	private static final int circleDiameter = 8;	// size of the 2 small rectangles we use to depict the rule
	private static final int displacement = 3;		// distance between the 2 small rectangles we use to depict the rule
	// note that the 2 small rectangles occupy 8+3=11 pixels in each direction, so with height and width of 12 the "inside"
	// area is actually 1 pixel larger (up and right) than it needs to be

	String linkText = "";

	public ReactionRuleDiagramShape(ReactionRule reactionRule, GraphModel graphModel) {
		super(graphModel);
		this.reactionRule = reactionRule;
		defaultBG = java.awt.Color.yellow;
		defaultFGselect = java.awt.Color.black;
		backgroundColor = defaultBG;
	}

	public void setLinkText(String linkText) {
		this.linkText = linkText;
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
		getSpaceManager().setSizePreferred(width, height);
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
		
		// this is the "inside" of the shape; note it's 1 pixel larger than the approximate area occupied by the 2 yellow rectangles we use to depict the rule
//		Rectangle2D contour = new Rectangle2D.Double(absPosX, absPosY, shapeWidth, shapeHeight);
//		g2D.setColor(Color.gray);
//		g2D.draw(contour);

		int offsetX = 0;
		int offsetY = shapeHeight-circleDiameter;
		for(int i=0; i<2; i++) {
			int x = absPosX + offsetX+i*displacement;
			int y = absPosY + offsetY-i*displacement;
			RoundRectangle2D icon = new RoundRectangle2D.Double(x, y, circleDiameter, circleDiameter, circleDiameter/2, circleDiameter/2);

			g2D.setColor(newBackgroundColor);
			g2D.fill(icon);
			g2D.setColor(forgroundColor);
			g2D.draw(icon);
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
		if(linkText != null && linkText != "") {
			ShapePaintUtil.paintLinkMarkRule(g2D, this, Color.BLACK);
		}
	}
	

	public Rectangle getLabelOutline( int absPosX, int absPosY){
		int textX = absPosX + getSpaceManager().getSize().width / 2 - getLabelSize().width / 2;
		int textY = absPosY + ((getSpaceManager().getSize().height-circleDiameter) / 2) - getLabelSize().height / 2;
		return new Rectangle(textX - 5, textY - getLabelSize().height + 3,
				getLabelSize().width + 10, getLabelSize().height);
	}

}
