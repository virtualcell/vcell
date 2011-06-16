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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;

import cbit.gui.graph.GraphModel;
/**
 * This type was created in VisualAge.
 */
public class GraphEdgeShape extends cbit.gui.graph.EdgeShape {
	protected cbit.util.graph.Edge fieldEdge = null;
	protected boolean bArrow = false;
	protected boolean bLabel = false;
	/**
	 * ReactionParticipantShape constructor comment.
	 * @param label java.lang.String
	 * @param graphModel cbit.vcell.graph.GraphModel
	 */
	public GraphEdgeShape(cbit.util.graph.Edge edge, NodeShape node1Shape, NodeShape node2Shape, GraphModel graphModel, boolean displayArrow, boolean displayLabel) {
		super(node1Shape, node2Shape, graphModel);
		this.fieldEdge = edge;
		this.bArrow = displayArrow;
		this.bLabel = displayLabel;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 */
	@Override
	public Object getModelObject() {
		return fieldEdge;
	}
	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.graph.ReactionStepShape
	 */
	public NodeShape getNode1Shape() {
		return (NodeShape)startShape;
	}
	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.graph.ReactionStepShape
	 */
	public NodeShape getNode2Shape() {
		return (NodeShape)endShape;
	}
	/**
	 * This method was created in VisualAge.
	 */
	@Override
	public void refreshLabel() {
		if (bLabel){
			setLabel(fieldEdge.getData().toString());
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
			Point endLocation = getNode2Shape().getAttachmentLocation(Shape.ATTACH_CENTER);
			double diffX = endLocation.x-startLocation.x;
			double diffY = endLocation.y-startLocation.y;
			double length = Math.sqrt(diffX*diffX+diffY*diffY);
			double arrowScale = 10/length;
			Point front = new Point((int)(startLocation.x+diffX/2+diffX*arrowScale/2),(int)(startLocation.y+diffY/2+diffY*arrowScale/2));
			Point back = new Point((int)(startLocation.x+diffX/2-diffX*arrowScale/2),(int)(startLocation.y+diffY/2-diffY*arrowScale/2));
			GeneralPath path = getArrow(front, back, 10);
			g2D.fill(path);
		}
	}

}
