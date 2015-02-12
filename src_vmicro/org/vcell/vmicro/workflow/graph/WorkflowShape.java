/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.vmicro.workflow.graph;

import java.awt.Color;
import java.awt.Graphics2D;

import org.vcell.workflow.Workflow;

import cbit.gui.graph.GraphModel;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.ImmutableVisualState;

public class WorkflowShape extends AbstractWorkflowNodeShape {
	
	int radius = 8;
	protected Workflow fieldWorkflow = null;

	public WorkflowShape(Workflow workflow, GraphModel graphModel) {
		super(graphModel);
		this.fieldWorkflow = workflow;
		defaultBG = Color.blue;
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
		return fieldWorkflow;
	}

	public Workflow getWorkflow() {
		return fieldWorkflow;
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
		setLabel("workflow-params");
	}

}
