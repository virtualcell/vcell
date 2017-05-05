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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Interaction;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.InteractionParticipant.Type;

import cbit.gui.graph.EdgeShape;
import cbit.gui.graph.Shape;
import cbit.gui.graph.visualstate.EdgeVisualState;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.DefaultEdgeVisualState;
import cbit.vcell.graph.CatalystShape;
import cbit.vcell.graph.ProductShape;
import cbit.vcell.graph.ReactantShape;
import cbit.vcell.graph.ReactionParticipantShape;
import cbit.vcell.graph.ReactionStepShape;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;

public class BioPaxInteractionParticipantShape extends EdgeShape {

	public static final float ARROW_LENGTH = 12f;
	public static final float ARROW_WIDTH = 7f;
	
	protected ReactionParticipant reactionParticipant = null;

	private Point2D.Double lastp2ctrl = null;
	private Point2D.Double lastp1ctrl = null;

	private transient double correctionFactor = 0;
	
	public static final Stroke CATALYST_STROKE = new BasicStroke(1f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND, 1f, new float[]{2f, 2f}, 0f);
	
	protected final InteractionParticipant participant;
	protected final BioPaxInteractionShape interactionShape;
	protected final BioPaxPhysicalEntityShape physicalEntityShape;
	
	public BioPaxInteractionParticipantShape(
			InteractionParticipant participant, 
			BioPaxInteractionShape interactionShape,
			BioPaxPhysicalEntityShape physicalEntityShape, 
			PathwayGraphModel graphModel) {
		super(physicalEntityShape, interactionShape, graphModel);
		this.participant = participant;
		this.interactionShape = interactionShape;
		this.physicalEntityShape = physicalEntityShape;
	}

	// very similar with the code in ReactionParticipantShape
	@Override 
	protected final CubicCurve2D.Double getCurve() {
		refreshLayoutSelf();
		// default behavior of control points is for direction at ends to follow secant between end-points.
		if(lastCurve_Start == null || !lastCurve_Start.equals(start) ||
				lastCurve_End == null || !lastCurve_End.equals(end)) {
			lastp1ctrl = 
				new Point2D.Double((1.0 - FRACT_WEIGHT)*start.getX() + FRACT_WEIGHT*end.getX(),
					(1.0 - FRACT_WEIGHT)*start.getY() + FRACT_WEIGHT*end.getY());
		}
		Point2D.Double p2ctrl = 
			new Point2D.Double(FRACT_WEIGHT*start.getX() + (1.0 - FRACT_WEIGHT)*end.getX(),
					FRACT_WEIGHT*start.getY() + (1.0 - FRACT_WEIGHT)*end.getY());
		
		// check for siblings in the reaction (like a+a-> or a->a) and calculate a correction factor
		// so that the edges won't overlap
		correctionFactor = 0;
		if (endShape instanceof BioPaxInteractionShape) {
			int myPosition = 0;		// index in the array of siblings
			int numSiblings = 0;			// in total
			BioPaxInteractionShape reactionStepShape = (BioPaxInteractionShape) endShape;
			Interaction rs = reactionStepShape.getInteraction();
			for(InteractionParticipant rp : rs.getParticipants()) {
				if(rp == participant) {		// myself
					if(rp.getType() == InteractionParticipant.Type.LEFT || rp.getType() == InteractionParticipant.Type.RIGHT) {
						myPosition = numSiblings;
						numSiblings++;
					}
				} else if(rp.getPhysicalEntity().getName().equals(participant.getPhysicalEntity().getName())) {
					if(rp.getType() == InteractionParticipant.Type.LEFT || rp.getType() == InteractionParticipant.Type.RIGHT) {
						numSiblings++;
					}
				}
			}
			if(numSiblings > 1) {
				double offset = numSiblings / 2 - 0.5;
				correctionFactor = (myPosition - offset) * 0.08;
			}
		}
		
		// calculate tangent direction at interaction
		double tangentX = 0.0;
		double tangentY = 0.0;
		if (endShape instanceof BioPaxInteractionShape) {
			BioPaxInteractionShape reactionStepShape = (BioPaxInteractionShape) endShape;

			for(Shape shape : graphModel.getShapes()) {
				if (shape instanceof BioPaxInteractionParticipantShape && ((BioPaxInteractionParticipantShape) shape).endShape == reactionStepShape) {
					BioPaxInteractionParticipantShape rpShape = (BioPaxInteractionParticipantShape)shape;
					double dx = rpShape.start.getX()-rpShape.end.getX();
					double dy = rpShape.start.getY()-rpShape.end.getY();
					double len = dx*dx+dy*dy;
					if (rpShape.participant.getType() == InteractionParticipant.Type.LEFT) {
						tangentX += (rpShape.start.getX() - rpShape.end.getX())/len;
						tangentY += (rpShape.start.getY() - rpShape.end.getY())/len;
					}else if (rpShape.participant.getType() == InteractionParticipant.Type.RIGHT) {
						tangentX -= (rpShape.start.getX() - rpShape.end.getX())/len;
						tangentY -= (rpShape.start.getY() - rpShape.end.getY())/len;
					}
				}
			}
		}
		double tangentLength = Math.sqrt(tangentX*tangentX + tangentY*tangentY);
		if (tangentLength != 0) {
			tangentX = tangentX*CONTROL_WEIGHT/tangentLength;
			tangentY = tangentY*CONTROL_WEIGHT/tangentLength;
		}

		if(participant.getType() == InteractionParticipant.Type.CONTROLLER) {
			// choose side based on inner product with displacement vector between catalyst and reactionStep
			if(((start.getX() - end.getX())*tangentY - (start.getY() - end.getY())*tangentX) > 0){
				p2ctrl.setLocation(end.getX() + tangentY, end.getY() - tangentX);
			}else{
				p2ctrl.setLocation(end.getX() - tangentY, end.getY() + tangentX);
			}
		} else if(participant.getType() == InteractionParticipant.Type.RIGHT) {
			p2ctrl.setLocation(end.getX()+tangentX, end.getY()+tangentY);	
		} else if(participant.getType() == InteractionParticipant.Type.LEFT) {
			p2ctrl.setLocation(end.getX()-tangentX,end.getY()-tangentY);	
		}

		if(lastCurve != null && 
				lastCurve_Start != null && lastCurve_Start.equals(start) &&
				lastCurve_End != null && lastCurve_End.equals(end) &&
				lastp2ctrl != null && lastp2ctrl.equals(p2ctrl)){
			//Do Nothing
		} else {
			lastCurve = 
				new CubicCurve2D.Double(start.getX(), start.getY(),
						lastp1ctrl.getX()*(1+correctionFactor), lastp1ctrl.getY()*(1+correctionFactor),
						p2ctrl.getX(),p2ctrl.getY(),end.getX(),end.getY());
			lastCurve_Start = new Point(start);
			lastCurve_End = new Point(end);
			lastp2ctrl = p2ctrl;
		}
		return lastCurve;
	}

	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		// TODO Auto-generated method stub
		return new Dimension(0, 0);
	}

	@Override
	public void refreshLayoutSelf() {
		
		if (startShape != null) {
			start = startShape.getAttachmentLocation(getStartAttachment());
			start.x += startShape.getSpaceManager().getAbsLoc().x;
			start.y += startShape.getSpaceManager().getAbsLoc().y;
		}
		if (endShape != null) {
			end = endShape.getAttachmentLocation(getEndAttachment());
			end.x += endShape.getSpaceManager().getAbsLoc().x;
			end.y += endShape.getSpaceManager().getAbsLoc().y;
		}
		getSpaceManager().setRelPos(Math.min(start.x, end.x), Math.min(start.y, end.y));
		getSpaceManager().setSize(Math.abs(start.x - end.x), Math.abs(start.y - end.y));
		// this is like a row/column layout (1 column)
		int centerX = getSpaceManager().getSize().width / 2;
		int centerY = getSpaceManager().getSize().height / 2;
		// position label
		labelPos.x = centerX - getLabelSize().width / 2; 
		labelPos.y = centerY - getLabelSize().height / 2;
		
//		labelPos.x = (getStartShape().getAbsX() + getEndShape().getAbsX()) / 2;
//		labelPos.y = (getStartShape().getAbsY() + getEndShape().getAbsY()) / 2;
	}

	@Override
	public void paintSelf(Graphics2D g2d, int xAbs, int yAbs) {

		RenderingHints oldRenderingHints = g2d.getRenderingHints();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color oldColor = g2d.getColor();
		g2d.setColor(forgroundColor);
		
		CubicCurve2D.Double cubicCurve = getCurve();
		
		if(participant.getType().equals(Type.CONTROLLER) || participant.getType().equals(Type.COFACTOR)) {
			Stroke previousStroke = g2d.getStroke();
			g2d.setStroke(DASHED_STROKE);				// g2d.setStroke(DASHED_STROKE);   CATALYST_STROKE
			g2d.draw(cubicCurve);
			g2d.setStroke(previousStroke);
		} else {
			g2d.draw(cubicCurve);
		}
		
		int arrowDirection = 0;
		if(participant.getType().equals(Type.LEFT)) {
			arrowDirection = -1;
		} else if(participant.getType().equals(Type.RIGHT)) {
			arrowDirection = 1;
		}
		if (arrowDirection == 1) {
			double arcLength = integrateArcLength(cubicCurve, 0.0, 1.0, 10);
			double centerT = getParameterAtArcLength(cubicCurve, 0.0, 1.0, arcLength/2, 20);
			Point2D center = evaluate(cubicCurve, centerT);
			double backT = intersectWithCircle(cubicCurve, centerT, 1.0, center.getX(), center.getY(), 4);
			Point2D back = evaluate(cubicCurve, backT);
			double frontT = intersectWithCircle(cubicCurve, centerT, 0.0, center.getX(), center.getY(), 4);
			Point2D front = evaluate(cubicCurve,frontT);
			GeneralPath arrow = getArrow(front, back, 7);
			g2d.fill(arrow);
		}
		if (arrowDirection == -1){
			double arcLength = integrateArcLength(cubicCurve, 0.0, 1.0, 10);
			double centerT = getParameterAtArcLength(cubicCurve, 0.0, 1.0, arcLength/2+2, 20);
			Point2D center = evaluate(cubicCurve, centerT);
			double backT = intersectWithCircle(cubicCurve, centerT, 0.0, center.getX(), center.getY(), 4);
			Point2D back = evaluate(cubicCurve, backT);
			double frontT = intersectWithCircle(cubicCurve, centerT, 1.0, center.getX(), center.getY(), 4);
			Point2D front = evaluate(cubicCurve,frontT);
			GeneralPath arrow = getArrow(front,back,7);
			g2d.fill(arrow);
		}
		
		// draw label (nothing expected, we don't have stoichiometry around here)
		if (getLabel() != null && getLabel().length()>0) {
			int x = start.x + (int)(start.x*correctionFactor);
			int y = start.y + (int)(start.y*correctionFactor);
			g2d.drawString(getLabel(), (x + end.x) / 2, (y + end.y) / 2);
		}
		
// TODO: there are types other than LEFT, RIGHT, CONTROLLER, COFACTOR, we need to draw something for them too
//		we'll add code for that as we find them 
//
//		Keeping below the simple code that only draws straight line
//		
//		Point startPos = interactionShape.getSpaceManager().getAbsCenter();
//		Point endPos = physicalEntityShape.getSpaceManager().getAbsCenter();
//		
//		if(participant.getType().equals(Type.CONTROLLER) || participant.getType().equals(Type.COFACTOR)) {
//			Stroke previousStroke = g2d.getStroke();
//			g2d.setStroke(CATALYST_STROKE);
//			g2d.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);			
//			g2d.setStroke(previousStroke);
//		} else {
//			g2d.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
//			
//		}
//		if(participant.getType().equals(Type.LEFT)) {
//			ArrowPainter.paintArrow(g2d, endPos, startPos, ARROW_LENGTH, ARROW_WIDTH);
//		} else if(participant.getType().equals(Type.RIGHT)) {
//			ArrowPainter.paintArrow(g2d, startPos, endPos, ARROW_LENGTH, ARROW_WIDTH);
//		}
//		String label = getLabel();
//		if(label != null) {
//			g2d.drawString(label, xAbs + labelPos.x, yAbs + labelPos.y);			
//		}
		
		g2d.setColor(oldColor);
		g2d.setRenderingHints(oldRenderingHints);
	}

	@Override
	public VisualState createVisualState() {
		return new DefaultEdgeVisualState(this);
	}

	@Override
	public Object getModelObject() {
		return participant;
	}

	@Override
	public void refreshLabel() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isDirectedForward() { 
		return !participant.getType().equals(InteractionParticipant.Type.LEFT); 
	}

}
