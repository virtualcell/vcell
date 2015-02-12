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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;

import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
/**
 * This type was created in VisualAge.
 */
public class WorkflowEdgeShape extends cbit.gui.graph.EdgeShape {
	private Object object = null;
	protected boolean bArrow = false;
	protected boolean bLabel = false;
	/**
	 * ReactionParticipantShape constructor comment.
	 * @param label java.lang.String
	 * @param graphModel cbit.vcell.graph.GraphModel
	 */
	public WorkflowEdgeShape(Object object, AbstractWorkflowNodeShape node1Shape, AbstractWorkflowNodeShape node2Shape, GraphModel graphModel, boolean displayArrow, boolean displayLabel) {
		super(node1Shape, node2Shape, graphModel);
		if (node1Shape==null || node2Shape==null){
			throw new RuntimeException("node1Shape or node2Shape is null");
		}
		this.bArrow = displayArrow;
		this.bLabel = displayLabel;
		this.object = object;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 */
	@Override
	public Object getModelObject() {
		return object;
	}
	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.graph.ReactionStepShape
	 */
	public AbstractWorkflowNodeShape getNode1Shape() {
		return (AbstractWorkflowNodeShape)startShape;
	}
	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.graph.ReactionStepShape
	 */
	public AbstractWorkflowNodeShape getNode2Shape() {
		return (AbstractWorkflowNodeShape)endShape;
	}
	/**
	 * This method was created in VisualAge.
	 */
	@Override
	public void refreshLabel() {
		if (bLabel){
			setLabel(object.toString());
		}else{
			setLabel("");
		}
	}

	@Override
	public void paintSelf(Graphics2D g2D, int absPosX, int absPosY){
		super.paintSelf(g2D,absPosX,absPosY);

		//
		// add arrow if directed
		//
		if (bArrow){
			Point startLocation = getNode1Shape().getAttachmentLocation(Shape.ATTACH_CENTER);
			startLocation.translate(getNode1Shape().getRelX(), getNode1Shape().getRelY());
			Point endLocation = getNode2Shape().getAttachmentLocation(Shape.ATTACH_CENTER);
			endLocation.translate(getNode2Shape().getRelX(), getNode2Shape().getRelY());
			double diffX = endLocation.x-startLocation.x;
			double diffY = endLocation.y-startLocation.y;
			double length = Math.sqrt(diffX*diffX+diffY*diffY);
			if (length==0){
				length = 1;
			}
			double arrowScale = 10/length;
			Point front = new Point((int)(startLocation.x+diffX/2+diffX*arrowScale/2),(int)(startLocation.y+diffY/2+diffY*arrowScale/2));
			Point back = new Point((int)(startLocation.x+diffX/2-diffX*arrowScale/2),(int)(startLocation.y+diffY/2-diffY*arrowScale/2));
			GeneralPath path = getArrow(front, back, 10);
			g2D.fill(path);
		}
	}

}
