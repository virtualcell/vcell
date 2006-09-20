package cbit.vcell.constraints.gui;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.ElipseShape;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Graphics2D;
/**
 * Insert the type's description here.
 * Creation date: (7/8/2003 11:58:47 AM)
 * @author: Jim Schaff
 */
public class GeneralConstraintNode extends ConstraintGraphNode {
	private cbit.vcell.constraints.GeneralConstraint generalConstraint = null;

/**
 * ConstraintVarNode constructor comment.
 * @param node cbit.vcell.mapping.potential.Node
 * @param graphModel cbit.vcell.graph.GraphModel
 */
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


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public java.lang.Object getModelObject() {
	return generalConstraint;
}


/**
 * This method was created by a SmartGuide.
 * @param g java.awt.Graphics
 */
public void paint(java.awt.Graphics2D g, int parentOriginX, int parentOriginY) {

   int absPosX = screenPos.x + parentOriginX;
   int absPosY = screenPos.y + parentOriginY;
	//
	boolean isBound = false;
	//
	// draw elipse
	//
	if (!getConstraintContainerImpl().getActive(generalConstraint)){
		if (isSelected()){
			g.setColor(java.awt.Color.pink);
			g.fillRect(absPosX+1,absPosY+1+labelPos.y,2*radius-1,2*radius-1);
			g.setColor(java.awt.Color.lightGray);
			g.drawRect(absPosX,absPosY+labelPos.y,2*radius,2*radius);
		}else{
			g.setColor(java.awt.Color.white);
			g.fillRect(absPosX+1,absPosY+1+labelPos.y,2*radius-1,2*radius-1);
			g.setColor(java.awt.Color.lightGray);
			g.drawRect(absPosX,absPosY+labelPos.y,2*radius,2*radius);
		}
		//g.setColor(java.awt.Color.white);
		//g.fillRect(absPosX+1,absPosY+1+labelPos.y,2*radius-1,2*radius-1);
		//g.setColor(java.awt.Color.lightGray);
		//g.drawRect(absPosX,absPosY+labelPos.y,2*radius,2*radius);
	}else if (getConstraintContainerImpl().getConsistent(generalConstraint)){
		g.setColor(backgroundColor);
		g.fillRect(absPosX+1,absPosY+1+labelPos.y,2*radius-1,2*radius-1);
		g.setColor(forgroundColor);
		g.drawRect(absPosX,absPosY+labelPos.y,2*radius,2*radius);
	}else{
		g.setColor(java.awt.Color.red);
		g.fillRect(absPosX-2,absPosY+labelPos.y-2,2*radius+5,2*radius+5);
		g.setColor(backgroundColor);
		g.fillRect(absPosX+1,absPosY+1+labelPos.y,2*radius-1,2*radius-1);
	}
	//
	// draw label
	//
	if (isSelected()){
		java.awt.FontMetrics fm = g.getFontMetrics();
		int textX = labelPos.x + absPosX;
		int textY = labelPos.y + absPosY;
		g.setColor(forgroundColor);
		if (getLabel()!=null && getLabel().length()>0){
			g.drawString(getLabel(),textX,textY);
		}
	}
	return;
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel(generalConstraint.getExpression().infix());
}


/**
 * Insert the method's description here.
 * Creation date: (7/10/2003 12:34:02 PM)
 * @param argDegree int
 */
public void setDegree(int argDegree) {
	super.setDegree(argDegree);
	float brightness = 1.0f - Math.min(1.0f,0.15f*(getDegree()-1));
	defaultBG = new java.awt.Color(brightness,brightness,1.0f);
	if (!isSelected()){
		backgroundColor = defaultBG;
	}
}
}