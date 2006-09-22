package cbit.vcell.constraints.gui;
/**
 * Insert the type's description here.
 * Creation date: (7/8/2003 11:58:47 AM)
 * @author: Jim Schaff
 */
public class ConstraintVarNode extends ConstraintGraphNode {
	private String constraintVariable = null;

/**
 * ConstraintVarNode constructor comment.
 * @param node cbit.vcell.mapping.potential.Node
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public ConstraintVarNode(String argConstraintVariable, ConstraintsGraphModel graphModel, int argDegree) {
	super(graphModel,argDegree);
	this.constraintVariable = argConstraintVariable;
	float brightness = 1.0f - Math.min(1.0f,0.15f*(getDegree()-1));
	defaultBG = new java.awt.Color(brightness,1.0f,brightness);
	defaultFGselect = java.awt.Color.black;
	backgroundColor = defaultBG;
	refreshLabel();

}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public java.lang.Object getModelObject() {
	return constraintVariable;
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
	g.setColor(backgroundColor);
	g.fillOval(absPosX+1,absPosY+1+labelPos.y,2*radius-1,2*radius-1);
	g.setColor(forgroundColor);
	g.drawOval(absPosX,absPosY+labelPos.y,2*radius,2*radius);
	//
	// draw label
	//
	java.awt.FontMetrics fm = g.getFontMetrics();
	int textX = labelPos.x + absPosX;
	int textY = labelPos.y + absPosY;
	g.setColor(forgroundColor);
	if (getLabel()!=null && getLabel().length()>0){
		g.drawString(getLabel(),textX,textY);
	}

	return;
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel(constraintVariable);
}


/**
 * Insert the method's description here.
 * Creation date: (7/10/2003 12:30:46 PM)
 * @param degree int
 */
public void setDegree(int argDegree) {
	super.setDegree(argDegree);
	float brightness = 1.0f - Math.min(1.0f,0.15f*(getDegree()-1));
	defaultBG = new java.awt.Color(brightness,1.0f,brightness);
	if (!isSelected()){
		backgroundColor = defaultBG;
	}
}
}