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
import java.awt.Color;
import java.awt.Graphics2D;

public class GeneralConstraintNode extends ConstraintGraphNode {
	private cbit.vcell.constraints.GeneralConstraint generalConstraint = null;


	public GeneralConstraintNode(cbit.vcell.constraints.GeneralConstraint argGeneralConstraint, ConstraintsGraphModel graphModel, int argDegree) {
		super(graphModel,argDegree);
		if (argGeneralConstraint==null){
			throw new IllegalArgumentException("generalConstraint is null");
		}
		this.generalConstraint = argGeneralConstraint;
		float brightness = 1.0f - Math.min(1.0f,0.15f*(getDegree()-1));
		defaultBG = new java.awt.Color(brightness,brightness,1.0f);
		defaultFGselect = java.awt.Color.black;
		backgroundColor = defaultBG;
		refreshLabel();
	}


	public Object getModelObject() {
		return generalConstraint;
	}

	public void paintSelf(Graphics2D g, int absPosX, int absPosY) {
		// draw elipse
		if (!getConstraintContainerImpl().getActive(generalConstraint)){
			if (isSelected()){
				g.setColor(Color.pink);
				g.fillRect(absPosX + 1, absPosY + 1 + getLabelPos().y, 2*radius-1, 2*radius-1);
				g.setColor(Color.lightGray);
				g.drawRect(absPosX, absPosY + getLabelPos().y, 2*radius, 2*radius);
			}else{
				g.setColor(Color.white);
				g.fillRect(absPosX + 1, absPosY + 1 + getLabelPos().y, 2*radius - 1, 2*radius - 1);
				g.setColor(Color.lightGray);
				g.drawRect(absPosX, absPosY + getLabelPos().y, 2*radius, 2*radius);
			}
		} else if (getConstraintContainerImpl().getConsistent(generalConstraint)){
			g.setColor(backgroundColor);
			g.fillRect(absPosX + 1, absPosY + 1 + getLabelPos().y, 2*radius - 1, 2*radius - 1);
			g.setColor(forgroundColor);
			g.drawRect(absPosX, absPosY + getLabelPos().y, 2*radius, 2*radius);
		} else {
			g.setColor(Color.red);
			g.fillRect(absPosX - 2, absPosY + getLabelPos().y - 2, 2*radius + 5, 2*radius + 5);
			g.setColor(backgroundColor);
			g.fillRect(absPosX + 1, absPosY + 1 + getLabelPos().y, 2 * radius - 1, 2*radius - 1);
		}
		// draw label
		if (isSelected()){
			int textX = getLabelPos().x + absPosX;
			int textY = getLabelPos().y + absPosY;
			g.setColor(forgroundColor);
			if (getLabel()!=null && getLabel().length()>0){
				g.drawString(getLabel(),textX,textY);
			}
		}
		return;
	}

	public void refreshLabel() {
		setLabel(generalConstraint.getExpression().infix());
	}

	public void setDegree(int argDegree) {
		super.setDegree(argDegree);
		float brightness = 1.0f - Math.min(1.0f,0.15f*(getDegree()-1));
		defaultBG = new Color(brightness,brightness,1.0f);
		if (!isSelected()){
			backgroundColor = defaultBG;
		}
	}
}
