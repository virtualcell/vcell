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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.ImmutableVisualState;
import cbit.util.graph.Node;

public class NodeShape extends ElipseShape {
	
	int radius = 8;
	protected Color darkerBackground = null;
	protected int fieldDegree = 0;
	protected Node fieldNode = null;

	public NodeShape(Node node, GraphModel graphModel, int degree) {
		super(graphModel);
		this.fieldNode = node;
		this.fieldDegree = degree;
		defaultBG = Color.green;
		defaultFGselect = Color.black;
		backgroundColor = defaultBG;
		darkerBackground = backgroundColor.darker().darker();
		refreshLabel();
	}

	@Override
	public VisualState createVisualState() { 
		return new ImmutableVisualState(this, VisualState.PaintLayer.NODE); 
	}

	@Override
	public Object getModelObject() {
		return fieldNode;
	}

	public Node getNode() {
		return fieldNode;
	}

	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();
		setLabelSize(fm.stringWidth(getLabel()), fm.getMaxAscent() + fm.getMaxDescent());
		getSpaceManager().setSizePreferred((radius*2), (radius*2));
		return getSpaceManager().getSizePreferred();
	}

	public void refreshLayoutSelf() {
		int centerX = getSpaceManager().getSize().width/2;
		labelPos.x = centerX - getLabelSize().width/2; 
		labelPos.y = 0;		
	}

	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY ) {
		// draw elipse
		g.setColor(backgroundColor);
		g.fillOval(absPosX + 1, absPosY + 1 + getLabelPos().y, 2*radius - 1, 2*radius - 1);
		g.setColor(forgroundColor);
		g.drawOval(absPosX, absPosY + getLabelPos().y, 2*radius, 2*radius);
		// draw label
		int textX = getLabelPos().x + absPosX;
		int textY = getLabelPos().y + absPosY;
		g.setColor(forgroundColor);
		if (getLabel()!=null && getLabel().length()>0){
			g.drawString(getLabel(),textX,textY);
		}
		return;
	}

	@Override
	public void refreshLabel() {
		setLabel(getNode().getName());
	}

}
