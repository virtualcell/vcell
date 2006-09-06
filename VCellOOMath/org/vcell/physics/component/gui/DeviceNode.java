package org.vcell.physics.component.gui;
import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphModel;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Graphics2D;
/**
 * Insert the type's description here.
 * Creation date: (7/8/2003 11:58:47 AM)
 * @author: Jim Schaff
 */
public class DeviceNode extends OOModelGraphNode {
	private org.vcell.physics.component.ModelComponent device = null;

/**
 * ConstraintVarNode constructor comment.
 * @param node cbit.vcell.mapping.potential.Node
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public DeviceNode(org.vcell.physics.component.ModelComponent argComponent, GraphModel graphModel) {
	super(graphModel);
	this.device = argComponent;
	//if (argDevice.getLocation().getResolved()){
		if (argComponent instanceof org.vcell.physics.component.TwoPortElectricalComponent || argComponent instanceof org.vcell.physics.component.OnePortElectricalDevice){
			defaultBG = java.awt.Color.green;
		}else if (argComponent instanceof org.vcell.physics.component.Species ||
				  argComponent instanceof org.vcell.physics.component.Reaction){
			defaultBG = java.awt.Color.pink;
		}else{
			defaultBG = java.awt.Color.white;
		}
	//}else{
		//defaultBG = java.awt.Color.white;
	//}
	defaultFGselect = java.awt.Color.black;
	backgroundColor = defaultBG;
	refreshLabel();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public org.vcell.physics.component.ModelComponent getDevice() {
	return device;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public java.lang.Object getModelObject() {
	return device;
}


/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 9:33:37 PM)
 * @return int
 */
int getRadius() {
	return 13;
}


/**
 * This method was created by a SmartGuide.
 * @param g java.awt.Graphics
 */
public void paint(java.awt.Graphics2D g, int parentOriginX, int parentOriginY) {

	int absPosX = screenPos.x + parentOriginX;
	int absPosY = screenPos.y + parentOriginY;
	
	g.translate(absPosX,absPosY);
	
	//
	boolean isBound = false;
	int radius = getRadius();
	int diameter = radius*2;

	//
	// draw outline box
	//
	g.setColor(backgroundColor);
	g.fillRect(1,1,diameter-1,diameter-1);
	g.setColor(forgroundColor);
	g.drawRect(0,0,diameter,diameter);

	//
	// draw device specific art (e.g. capacitor should be --||--)
	//
	if (getDevice() instanceof org.vcell.physics.component.Capacitor){
		g.drawLine(diameter/10,diameter/2, diameter*4/10,diameter/2);
		g.drawLine(diameter*9/10,diameter/2, diameter*6/10,diameter/2);
		g.drawLine(diameter*4/10,diameter/3, diameter*4/10,diameter*2/3);
		g.drawLine(diameter*6/10,diameter/3, diameter*6/10,diameter*2/3);
	//}else if (getDevice() instanceof ncbc.physics.component.InfiniteVolumeConductor){
		//g.drawLine(diameter/10,diameter/2, diameter*9/10,diameter/2);
		//g.fillOval(diameter*7/16,diameter*7/16,diameter/4,diameter/4);
	}else if (getDevice() instanceof org.vcell.physics.component.Resistor){
		g.drawLine(diameter*1/10,diameter/2, diameter*3/10,diameter/2);
		g.drawLine(diameter*3/10,diameter/2, diameter*4/10,diameter/3);
		g.drawLine(diameter*4/10,diameter/3, diameter*5/10,diameter*2/3);
		g.drawLine(diameter*5/10,diameter*2/3, diameter*6/10,diameter*1/3);
		g.drawLine(diameter*6/10,diameter/3, diameter*7/10,diameter*2/3);
		g.drawLine(diameter*7/10,diameter*2/3, diameter*8/10,diameter/2);
		g.drawLine(diameter*8/10,diameter/2, diameter*10/10,diameter/2);
	}else if (getDevice() instanceof org.vcell.physics.component.Inductor){
		g.drawLine(diameter*1/20,diameter/2, diameter*2/10,diameter/2);
		g.drawArc(diameter*5/20,diameter/4,diameter/4,diameter/2,180,235);
		g.drawArc(diameter*8/20,diameter/4,diameter/4,diameter/2,135,270);
		g.drawArc(diameter*11/20,diameter/4,diameter/4,diameter/2,135,270);
		g.drawLine(diameter*16/20,diameter/2, diameter*9/10,diameter/2);
	}else if (getDevice() instanceof org.vcell.physics.component.Ground){
		g.drawLine(diameter*1/2,diameter*2/20, diameter*1/2,diameter/2);
		g.drawLine(diameter*2/10,diameter*10/20, diameter*8/10,diameter*10/20);
		g.drawLine(diameter*3/10,diameter*13/20, diameter*7/10,diameter*13/20);
		g.drawLine(diameter*4/10,diameter*15/20, diameter*6/10,diameter*15/20);
	//}else if (getDevice() instanceof ncbc.physics.component.InfiniteVolumeConductor){
		//g.drawLine(diameter/10,diameter/2, diameter*9/10,diameter/2);
		//g.fillOval(diameter*7/16,diameter*7/16,diameter/4,diameter/4);
	}else if (getDevice() instanceof org.vcell.physics.component.VoltageSource){
		g.drawOval(diameter/6,diameter/6,diameter*2/3,diameter*2/3);
		// + sign at top
		g.drawLine(diameter*4/10,diameter*5/12, diameter*13/20,diameter*5/12);
		g.drawLine(diameter/2,diameter/4, diameter/2,diameter*6/12);
		// - sign at bottom
		g.drawLine(diameter*4/10,diameter*2/3, diameter*6/10,diameter*2/3);
		
		g.drawLine(0,diameter/2, diameter/6,diameter/2);
		g.drawLine(diameter*5/6,diameter/2, diameter,diameter/2);
	}else if (getDevice() instanceof org.vcell.physics.component.CurrentSource){
		java.awt.Polygon arrow = new java.awt.Polygon(	new int[] {diameter/4,   diameter*5/9, diameter*5/9, diameter*3/4, diameter*5/9, diameter*5/9, diameter/4},
														new int[] {diameter*10/21, diameter*10/21, diameter*5/12,   diameter/2,   diameter*8/12, diameter*12/21, diameter*12/21}, 
														7);
		g.drawOval(diameter/6,diameter/6,diameter*2/3,diameter*2/3);
		// + sign at top
		g.fill(arrow);
		
		g.drawLine(0,diameter/2, diameter/6,diameter/2);
		g.drawLine(diameter*5/6,diameter/2, diameter,diameter/2);
	}else if (getDevice() instanceof org.vcell.physics.component.Species){
		g.fillOval(diameter/2,diameter/2,diameter/6,diameter/6);
		g.fillOval(diameter/6,diameter/6,diameter/6,diameter/6);
		g.fillOval(diameter/3,diameter/5,diameter/6,diameter/6);
		g.fillOval(diameter*2/3,diameter*3/5,diameter/6,diameter/6);
		g.fillOval(diameter/4,diameter/2,diameter/6,diameter/6);
		g.fillOval(diameter/6,diameter*5/8,diameter/6,diameter/6);
	}else if (getDevice() instanceof org.vcell.physics.component.Reaction){
		java.awt.Polygon arrow = new java.awt.Polygon(	new int[] {diameter/4,   diameter*5/9, diameter*5/9, diameter*3/4, diameter*5/9, diameter*5/9, diameter/4},
														new int[] {diameter*10/21, diameter*10/21, diameter*5/12,   diameter/2,   diameter*8/12, diameter*12/21, diameter*12/21}, 
														7);
		g.drawOval(diameter/6,diameter/6,diameter*2/3,diameter*2/3);
		// + sign at top
		g.fill(arrow);
		
	}
	
	//
	// draw label
	//
	refreshLabel();
	//if (isSelected() || getDevice() instanceof MembraneSpecies || getDevice() instanceof VolumeSpecies){
		java.awt.FontMetrics fm = g.getFontMetrics();
		int textX = labelPos.x;
		int textY = labelPos.y-2;
		g.setColor(forgroundColor);
		if (getLabel()!=null && getLabel().length()>0){
			g.drawString(getLabel(),textX,textY);
		}
	//}
	
	g.translate(-absPosX,-absPosY);
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel(device.getName());
}
}