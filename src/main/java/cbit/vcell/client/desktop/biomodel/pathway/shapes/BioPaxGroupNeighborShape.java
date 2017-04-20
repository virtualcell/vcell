/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import org.vcell.pathway.GroupNeighbor;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.InteractionParticipant.Type;
import org.vcell.sybil.util.gui.ArrowPainter;

import cbit.gui.graph.EdgeShape;
import cbit.gui.graph.visualstate.EdgeVisualState;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.DefaultEdgeVisualState;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;

public class BioPaxGroupNeighborShape extends EdgeShape implements EdgeVisualState.Owner {
	public static final float ARROW_LENGTH = 12f;
	public static final float ARROW_WIDTH = 7f;
	
	public static final Stroke CATALYST_STROKE = new BasicStroke(1f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND, 1f, new float[]{2f, 2f}, 0f);
	
	protected final GroupNeighbor groupNeighbor;
	protected final BioPaxShape groupShape;
	protected final BioPaxShape bioPaxShape;
	private Type type;
	
	public BioPaxGroupNeighborShape(
			GroupNeighbor groupNeighbor, 
			BioPaxShape bioPaxShape,
			BioPaxShape groupShape, 
			PathwayGraphModel graphModel) {
		super(bioPaxShape, groupShape, graphModel);
		this.groupNeighbor = groupNeighbor;
		this.bioPaxShape = bioPaxShape;
		this.groupShape = groupShape;
		this.type = groupNeighbor.getType();
	}

	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		// TODO Auto-generated method stub
		return new Dimension(0, 0);
	}

	@Override
	public void refreshLayoutSelf() {
		labelPos.x = (getStartShape().getAbsX() + getEndShape().getAbsX()) / 2;
		labelPos.y = (getStartShape().getAbsY() + getEndShape().getAbsY()) / 2;
	}

	@Override
	public void paintSelf(Graphics2D g2d, int xAbs, int yAbs) {
		// TODO Auto-generated method stub
		Point startPos = bioPaxShape.getSpaceManager().getAbsCenter();
		Point endPos = groupShape.getSpaceManager().getAbsCenter();
		if(getType().equals(Type.CONTROLLER) || getType().equals(Type.COFACTOR)) {
			Stroke previousStroke = g2d.getStroke();
			g2d.setStroke(CATALYST_STROKE);
			g2d.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);			
			g2d.setStroke(previousStroke);
		} else {
			g2d.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
			
		}
		if(getType().equals(Type.LEFT)) {
			ArrowPainter.paintArrow(g2d, endPos, startPos, ARROW_LENGTH, ARROW_WIDTH);
		} else if(getType().equals(Type.RIGHT)) {
			ArrowPainter.paintArrow(g2d, startPos, endPos, ARROW_LENGTH, ARROW_WIDTH);
		}
		String label = getLabel();
		if(label != null) {
			g2d.drawString(label, xAbs + labelPos.x, yAbs + labelPos.y);			
		}
	}

	@Override
	public VisualState createVisualState() {
		return new DefaultEdgeVisualState(this);
	}

	@Override
	public Object getModelObject() {
		return groupNeighbor;
	}

	@Override
	public void refreshLabel() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isDirectedForward() { 
		return !getType().equals(InteractionParticipant.Type.LEFT); 
	}
	
	public void setType (Type newType){
		type = newType;
	}
	public Type getType(){
		return type;
	}
}
