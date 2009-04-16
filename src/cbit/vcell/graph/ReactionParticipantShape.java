package cbit.vcell.graph;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.gui.graph.*;
import cbit.vcell.model.*;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.GeneralPath;
import java.util.Enumeration;
/**
 * This type was created in VisualAge.
 */
public abstract class ReactionParticipantShape extends EdgeShape {
	protected ReactionParticipant reactionParticipant = null;

	private java.awt.geom.Point2D.Double lastp2ctrl = null;
	private java.awt.geom.Point2D.Double lastp1ctrl = null;

/**
 * ReactionParticipantShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public ReactionParticipantShape(ReactionParticipant reactionParticipant, ReactionStepShape reactionStepShape,
	                            SpeciesContextShape speciesContextShape, GraphModel graphModel) {
	super(speciesContextShape, reactionStepShape, graphModel);
	this.reactionParticipant = reactionParticipant;
}


/**
 * Insert the method's description here.
 * Creation date: (8/12/2002 11:51:55 AM)
 * @return java.awt.geom.CubicCurve2D
 */
protected java.awt.geom.CubicCurve2D.Double getCurve() {
	
	//
	// default behavior of control points is for direction at ends to follow secant between end-points.
	//
	if(lastCurve_Start == null || !lastCurve_Start.equals(start) ||
		lastCurve_End == null || !lastCurve_End.equals(end)){
		lastp1ctrl = new java.awt.geom.Point2D.Double((1.0-FRACT_WEIGHT)*start.getX()+FRACT_WEIGHT*end.getX(),(1.0-FRACT_WEIGHT)*start.getY()+FRACT_WEIGHT*end.getY());
	}
		
	java.awt.geom.Point2D.Double p2ctrl = new java.awt.geom.Point2D.Double(FRACT_WEIGHT*start.getX()+(1.0-FRACT_WEIGHT)*end.getX(),FRACT_WEIGHT*start.getY()+(1.0-FRACT_WEIGHT)*end.getY());

	//
	// calculate tangent direction at "reactionStep"
	//
	double tangentX = 1.0;
	double tangentY = 0.0;
	if (endShape instanceof ReactionStepShape){
			
		ReactionStepShape reactionStepShape = (ReactionStepShape)endShape;
		Enumeration<Shape> enumShapes = graphModel.getShapes();
		while (enumShapes.hasMoreElements()){
			Shape shape = enumShapes.nextElement();
			
			if (shape instanceof ReactionParticipantShape && 
				((ReactionParticipantShape)shape).endShape == reactionStepShape){
					
				if (shape instanceof ProductShape){
					ProductShape ps = (ProductShape)shape;
					tangentX += (ps.start.getX() - ps.end.getX());
					tangentY += (ps.start.getY() - ps.end.getY());
				}else if (shape instanceof ReactantShape){
					ReactantShape rs = (ReactantShape)shape;
					tangentX -= (rs.start.getX() - rs.end.getX());
					tangentY -= (rs.start.getY() - rs.end.getY());
				}
			}
		}
	}
	double tangentLength = Math.sqrt(tangentX*tangentX + tangentY*tangentY);
	if (tangentLength != 0) {
		tangentX = tangentX*CONTROL_WEIGHT/tangentLength;
		tangentY = tangentY*CONTROL_WEIGHT/tangentLength;
	}
	//tangentX = controlWeight;
	//tangentY = 0.0;
	if (this instanceof CatalystShape){
		//
		// choose side based on inner product with displacement vector between catalyst and reactionStep
		//
		if (((start.getX()-end.getX())*tangentY - (start.getY()-end.getY())*tangentX) > 0){
			p2ctrl.setLocation(end.getX()+tangentY,end.getY()-tangentX);
		}else{
			p2ctrl.setLocation(end.getX()-tangentY,end.getY()+tangentX);
		}
	}else if (this instanceof ProductShape){
		p2ctrl.setLocation(end.getX()+tangentX, end.getY()+tangentY);	
	}else if (this instanceof ReactantShape){
		p2ctrl.setLocation(end.getX()-tangentX,end.getY()-tangentY);	
	}else if (this instanceof FluxShape){
		//
		// choose side based on inner product with displacement vector between catalyst and reactionStep
		//
		if (((start.getX()-end.getX())*tangentX + (start.getY()-end.getY())*tangentY) > 0){
			p2ctrl.setLocation(end.getX()+tangentX,end.getY()+tangentY);
		}else{
			p2ctrl.setLocation(end.getX()-tangentX,end.getY()-tangentY);
		}
	}

	if(lastCurve != null && 
		lastCurve_Start != null && lastCurve_Start.equals(start) &&
		lastCurve_End != null && lastCurve_End.equals(end) &&
		lastp2ctrl != null && lastp2ctrl.equals(p2ctrl)){
			//Do Nothing
	}else{		
		lastCurve = new java.awt.geom.CubicCurve2D.Double(start.getX(),start.getY(),lastp1ctrl.getX(),lastp1ctrl.getY(),p2ctrl.getX(),p2ctrl.getY(),end.getX(),end.getY());

		lastCurve_Start = new java.awt.Point(start);
		lastCurve_End = new java.awt.Point(end);
		lastp2ctrl = p2ctrl;

		}
	
	return lastCurve;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return reactionParticipant;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionParticipant
 */
public ReactionParticipant getReactionParticipant() {
	return reactionParticipant;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ReactionStepShape
 */
public ReactionStepShape getReactionStepShape() {
	return (ReactionStepShape)endShape;
}


/**
 * This method was created by a SmartGuide.
 * @param g java.awt.Graphics
 */
public void paint(java.awt.Graphics2D g2D, int parentOffsetX, int parentOffsetY) {

    //
    // draw cubic spline with horizontal reactant-end (p' = 0) at reaction
    //
    g2D.setRenderingHint(
        java.awt.RenderingHints.KEY_ANTIALIASING,
        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

    java.awt.geom.CubicCurve2D.Double cubicCurve = getCurve();
	//java.awt.geom.Point2D.Double p1 = (java.awt.geom.Point2D.Double) cubicCurve.getP1();
	//java.awt.geom.Point2D.Double p2 = (java.awt.geom.Point2D.Double) cubicCurve.getP2();
	//java.awt.geom.Point2D.Double p1ctrl = (java.awt.geom.Point2D.Double) cubicCurve.getCtrlP1();
	//java.awt.geom.Point2D.Double p2ctrl = (java.awt.geom.Point2D.Double) cubicCurve.getCtrlP2();
    
	//
	// render curve (make CatalystShapes draw with a dashed line)
	//
	g2D.setColor(forgroundColor);
	if (getLineStyle() == LINE_STYLE_DASHED){
		Stroke oldStroke = g2D.getStroke();
		g2D.setStroke(DASHED_STROKE);
		g2D.draw(cubicCurve);
		g2D.setStroke(oldStroke);
	}else{
		g2D.draw(cubicCurve);
	}
    //
    // draw control points
    //
    //if (isSelected()){
	    //g2D.draw(new Ellipse2D.Double(p1ctrl.getX()-2,p1ctrl.getY()-2,4,4));
	    //g2D.draw(new Ellipse2D.Double(p2ctrl.getX()-2,p2ctrl.getY()-2,4,4));
	    //g2D.draw(new Line2D.Double(p1ctrl.getX(),p1ctrl.getY(),p1.getX(),p1.getY()));
	    //g2D.draw(new Line2D.Double(p2ctrl.getX(),p2ctrl.getY(),p2.getX(),p2.getY()));
    //}
    //
    // draw arrow-head at start of shape
    //
    if (this instanceof ProductShape || (this instanceof FluxShape && ( cubicCurve.getP1().getX()> cubicCurve.getP2().getX()))) {
	    double arcLength = integrateArcLength(cubicCurve, 0.0, 1.0, 10);
	    double centerT = getParameterAtArcLength(cubicCurve, 0.0, 1.0, arcLength/2, 20);
	    Point2D center = evaluate(cubicCurve, centerT);
	    double backT = intersectWithCircle(cubicCurve, centerT, 1.0, center.getX(), center.getY(), 4);
		Point2D back = evaluate(cubicCurve, backT);
		double frontT = intersectWithCircle(cubicCurve, centerT, 0.0, center.getX(), center.getY(), 4);
		Point2D front = evaluate(cubicCurve,frontT);
		
		GeneralPath arrow = getArrow(front, back, 8);

		g2D.fill(arrow);
    }
    if (this instanceof ReactantShape || (this instanceof FluxShape && ( cubicCurve.getP1().getX()< cubicCurve.getP2().getX()))){
	    double arcLength = integrateArcLength(cubicCurve, 0.0, 1.0, 10);
	    double centerT = getParameterAtArcLength(cubicCurve, 0.0, 1.0, arcLength/2, 20);
	    Point2D center = evaluate(cubicCurve, centerT);
	    double backT = intersectWithCircle(cubicCurve, centerT, 0.0, center.getX(), center.getY(), 4);
		Point2D back = evaluate(cubicCurve, backT);
		double frontT = intersectWithCircle(cubicCurve, centerT, 1.0, center.getX(), center.getY(), 4);
		Point2D front = evaluate(cubicCurve,frontT);

		GeneralPath arrow = getArrow(front,back,7);

		g2D.fill(arrow);
    }
    //
    // draw filled circle at end of shape
    //
    //if (this instanceof CatalystShape){
    //Point2D location = intersectWithCircle(cubicCurve,cubicCurve.getX2(),cubicCurve.getY2(),10);
    //g2D.fill(new Ellipse2D.Double(location.getX()-5,location.getY()-5,10,10));
    //}

    //
    // draw label
    //
    if (getLabel() != null && getLabel().length()>0) {
        g2D.drawString(getLabel(), (start.x + end.x) / 2, (start.y + end.y) / 2);
    }
    return;
}
}