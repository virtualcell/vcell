package org.vcell.physics.component.gui;

import cbit.gui.graph.GraphModel;
/**
 * Insert the type's description here.
 * Creation date: (7/8/2003 11:58:47 AM)
 * @author: Jim Schaff
 */
public class ConnectionNode extends OOModelGraphNode {
	private org.vcell.physics.component.Connection connection = null;
/**
 * ConstraintVarNode constructor comment.
 * @param node cbit.vcell.mapping.potential.Node
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public ConnectionNode(org.vcell.physics.component.Connection argConnection, GraphModel graphModel) {
	super(graphModel);
	this.connection = argConnection;
	defaultFGselect = java.awt.Color.black;
	backgroundColor = defaultBG;
	refreshLabel();

}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public java.lang.Object getModelObject() {
	return connection;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 9:33:37 PM)
 * @return int
 */
int getRadius() {
	return 3;
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
	int radius = getRadius();
	//
	// draw elipse
	//
	g.setColor(backgroundColor);
	g.fillRect(absPosX+1,absPosY+1,2*radius-1,2*radius-1);
	g.setColor(forgroundColor);
	g.drawRect(absPosX,absPosY,2*radius,2*radius);
	//
	// draw label
	//
	if (isSelected()){
		java.awt.FontMetrics fm = g.getFontMetrics();
		int textX = labelPos.x + absPosX;
		int textY = labelPos.y + absPosY-2;
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
	StringBuffer buffer = new StringBuffer("(");
	for (int i = 0; i < connection.getConnectors().length; i++){
		if (i>0){
			buffer.append(", ");
		}
		buffer.append(connection.getConnectors()[i].getParent().getName()+"."+connection.getConnectors()[i].getEffortVariable().getName());
	}
	buffer.append(")");
	setLabel(buffer.toString());
}
}
