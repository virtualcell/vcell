/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.constraints.graph;

import java.awt.Graphics2D;

public class ConstraintVarNode extends ConstraintGraphNode {
	private String constraintVariable = null;


	public ConstraintVarNode(String argConstraintVariable, ConstraintsGraphModel graphModel, int argDegree) {
		super(graphModel,argDegree);
		this.constraintVariable = argConstraintVariable;
		float brightness = 1.0f - Math.min(1.0f,0.15f*(getDegree()-1));
		defaultBG = new java.awt.Color(brightness,1.0f,brightness);
		defaultFGselect = java.awt.Color.black;
		backgroundColor = defaultBG;
		refreshLabel();

	}


	public Object getModelObject() {
		return constraintVariable;
	}

	public void paintSelf(Graphics2D g, int absPosX, int absPosY) {
		// draw elipse
		g.setColor(backgroundColor);
		g.fillOval(absPosX + 1, absPosY + 1 + getLabelPos().y, 2*radius-1, 2*radius-1);
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

	public void refreshLabel() {
		setLabel(constraintVariable);
	}

	public void setDegree(int argDegree) {
		super.setDegree(argDegree);
		float brightness = 1.0f - Math.min(1.0f,0.15f*(getDegree()-1));
		defaultBG = new java.awt.Color(brightness,1.0f,brightness);
		if (!isSelected()){
			backgroundColor = defaultBG;
		}
	}
}
