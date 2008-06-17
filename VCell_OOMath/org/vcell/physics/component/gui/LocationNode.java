package org.vcell.physics.component.gui;

import org.vcell.physics.component.Location;
import org.vcell.physics.component.LumpedLocation;
import org.vcell.physics.component.ResolvedLocation;

public class LocationNode extends DeviceNode {
	private static java.awt.Polygon triangle2D = null;
	private static java.awt.Polygon triangle3D_1 = null;
	private static java.awt.Polygon triangle3D_2 = null;
/**
 * ConstraintVarNode constructor comment.
 * @param node cbit.vcell.mapping.potential.Node
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public LocationNode(Location argLocation, cbit.gui.graph.GraphModel graphModel) {
	super(argLocation,graphModel);
	if (argLocation instanceof ResolvedLocation && argLocation.getDimension()==3){
		defaultBG = java.awt.Color.yellow;
	}else if (argLocation instanceof LumpedLocation && argLocation.getDimension()==3){
		defaultBG = java.awt.Color.white;
	}else if (argLocation instanceof ResolvedLocation && argLocation.getDimension()==2){
		defaultBG = java.awt.Color.lightGray;
	}else if (argLocation instanceof LumpedLocation && argLocation.getDimension()==2){
		defaultBG = java.awt.Color.white;
	}
	defaultFGselect = java.awt.Color.black;
	backgroundColor = defaultBG;
	refreshLabel();
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 9:33:37 PM)
 * @return int
 */
int getRadius() {
	return 20;
}
/**
 * This method was created by a SmartGuide.
 * @param g java.awt.Graphics
 */
public void paint(java.awt.Graphics2D g, int parentOriginX, int parentOriginY) {
	

	int radius = getRadius();
	if (triangle2D==null){
		triangle2D = new java.awt.Polygon(new int[] { radius*4/5, (2*radius*4/5), 0}, new int[] { 0, (radius*9/5), (radius*6/5) }, 3);
	}
	if (triangle3D_1==null){
		triangle3D_1 = new java.awt.Polygon(new int[] { radius*3/5, (2*radius*4/5), 0}, new int[] { 0, (radius*9/5), (radius*9/5) }, 3);
	}
	if (triangle3D_2==null){
		triangle3D_2 = new java.awt.Polygon(new int[] { radius*3/5,  0, 0}, new int[] { 0, (radius*9/5), (radius*4/5) }, 3);
	}
	
	int absPosX = screenPos.x + parentOriginX;
	int absPosY = screenPos.y + parentOriginY;
	g.translate(absPosX,absPosY);
	//
	boolean isBound = false;
	if (((Location)getDevice()).getDimension()==2){
		g.setColor(backgroundColor);
		g.fill(triangle2D);
		g.setColor(forgroundColor);
		g.draw(triangle2D);
		//g.drawOval(0,0,2*radius,2*radius);
	}else if (((Location)getDevice()).getDimension()==3){
		g.setColor(backgroundColor);
		g.fill(triangle3D_1);
		g.setColor(backgroundColor.darker());
		g.fill(triangle3D_2);
		g.setColor(backgroundColor.darker().darker());
		g.drawLine((2*radius*4/5),(radius*9/5),0,(radius*4/5));
		g.setColor(forgroundColor);
		g.draw(triangle3D_1);
		g.draw(triangle3D_2);
		//g.drawOval(0,0,2*radius,2*radius);
	}else{
		//
		// draw elipse
		//
		g.setColor(backgroundColor);
		g.fillOval(1,1,2*radius-1,2*radius-1);
		g.setColor(forgroundColor);
		g.drawOval(0,0,2*radius,2*radius);
	}
	//
	// draw label
	//
//	if (isSelected()){
		java.awt.FontMetrics fm = g.getFontMetrics();
		int textX = labelPos.x;
		int textY = labelPos.y-2;
		g.setColor(forgroundColor);
		if (getLabel()!=null && getLabel().length()>0){
			g.drawString(getLabel(),textX,textY);
		}
//	}
	g.translate(-absPosX,-absPosY);
	return;
}
}
