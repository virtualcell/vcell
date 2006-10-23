package org.vcell.ncbc.physics.component.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphModel;
/**
 * Insert the type's description here.
 * Creation date: (7/8/2003 11:58:47 AM)
 * @author: Jim Schaff
 */
public abstract class PhysicalModelGraphNode extends ElipseShape {
/**
 * ConstraintVarNode constructor comment.
 * @param node cbit.vcell.mapping.potential.Node
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public PhysicalModelGraphNode(GraphModel graphModel) {
	super(graphModel);
	defaultBG = java.awt.Color.white;
	defaultFGselect = java.awt.Color.black;
	backgroundColor = defaultBG;
}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @param g java.awt.Graphics
 */
public Dimension getPreferedSize(java.awt.Graphics2D g) {
	java.awt.FontMetrics fm = g.getFontMetrics();
	labelSize.height = fm.getMaxAscent() + fm.getMaxDescent();
	labelSize.width = fm.stringWidth(getLabel());
//	preferedSize.height = radius*2 + labelSize.height;
//	preferedSize.width = Math.max(radius*2,labelSize.width);
	preferedSize.height = getRadius()*2;
	preferedSize.width = getRadius()*2;
	return preferedSize;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 9:31:40 PM)
 * @return int
 */
abstract int getRadius();
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public Point getSeparatorDeepCount() {	
	return new Point(0,0);
}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @param g java.awt.Graphics
 */
public void layout() {

//	if (screenSize.width<labelSize.width ||
//		 screenSize.height<labelSize.height){
//		 throw new Exception("screen size smaller than label");
//	} 
	//
	// this is like a row/column layout  (1 column)
	//
	int centerX = screenSize.width/2;
	int centerY = screenSize.height/2;
	
	//
	// position label
	//
	labelPos.x = centerX - labelSize.width/2;
	labelPos.y = 0;
	//labelPos.y =  centerY/2 + labelSize.height*4/5;
}
/**
 * This method was created by a SmartGuide.
 * @param newSize java.awt.Dimension
 */
public void resize(Graphics2D g, Dimension newSize) {
	return;
}
}
