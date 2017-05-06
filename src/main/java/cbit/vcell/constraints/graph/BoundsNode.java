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

public class BoundsNode extends ConstraintGraphNode {
	private cbit.vcell.constraints.SimpleBounds simpleBounds = null;

	public BoundsNode(cbit.vcell.constraints.SimpleBounds argSimpleBounds, ConstraintsGraphModel graphModel) {
		super(graphModel,1);
		this.simpleBounds = argSimpleBounds;
		defaultBG = java.awt.Color.white;
		defaultFGselect = java.awt.Color.black;
		backgroundColor = defaultBG;
		refreshLabel();
	}

	public java.lang.Object getModelObject() {
		return simpleBounds;
	}

	public void paintSelf(Graphics2D g, int absPosX, int absPosY) {
		// draw elipse
		if (getConstraintContainerImpl().getActive(simpleBounds)){
			g.setColor(backgroundColor);
			g.fillRect(absPosX + 1, absPosY + 1 + getLabelPos().y,
					2*radius-1,2*radius-1);
			g.setColor(forgroundColor);
			g.drawRect(absPosX, absPosY + getLabelPos().y, 2*radius, 2*radius);
		}else{
			if (isSelected()){
				g.setColor(java.awt.Color.pink);
				g.fillRect(absPosX+1, absPosY + 1 + getLabelPos().y,2*radius-1,2*radius-1);
				g.setColor(java.awt.Color.lightGray);
				g.drawRect(absPosX, absPosY + getLabelPos().y, 2*radius, 2*radius);
			} else {
				g.setColor(java.awt.Color.white);
				g.fillRect(absPosX + 1, absPosY + 1 + getLabelPos().y, 2*radius - 1, 2*radius - 1);
				g.setColor(java.awt.Color.lightGray);
				g.drawRect(absPosX, absPosY + getLabelPos().y, 2*radius, 2*radius);
			}
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
		setLabel(simpleBounds.getIdentifier()+"=["+simpleBounds.getBounds().lo()+","+simpleBounds.getBounds().hi()+"]");
	}
}
