package org.vcell.physics.component.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Graphics2D;

import org.vcell.physics.component.Connector;

import cbit.gui.graph.GraphModel;
/**
 * This type was created in VisualAge.
 */
public class ConnectorEdge extends cbit.gui.graph.EdgeShape {
	private org.vcell.physics.component.Connection connection = null;

/**
 * ReactionParticipantShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public ConnectorEdge(DeviceNode argDeviceNode, ConnectionNode argConnectionNode, org.vcell.physics.component.Connection argConnection, GraphModel graphModel) {
	super(argDeviceNode, argConnectionNode, graphModel);
	this.connection = argConnection;
	refreshLabel();
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ReactionStepShape
 */
public cbit.gui.graph.ElipseShape getFirstPartnerShape() {
	return (cbit.gui.graph.ElipseShape)startShape;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return connection;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ReactionStepShape
 */
public cbit.gui.graph.ElipseShape getSecondPartnerShape() {
	return (cbit.gui.graph.ElipseShape)endShape;
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	if (isSelected()){
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; connection.getConnectors()!=null && i < connection.getConnectors().length; i++) {
			Connector connector = connection.getConnectors()[i];
			if (connector.getParent() == getStartShape().getModelObject()){
				buffer.append("["+((connector.getEffortVariable()!=null)?(connector.getEffortVariable().getName()):("null"))+","+((connector.getFlowVariable()!=null)?(connector.getFlowVariable().getName()):("null"))+"] ");
			}
		}
		setLabel(buffer.toString());
	}else{
		setLabel("");
	}
}

public void paint(Graphics2D g2d, int parentOffsetX, int parentOffsetY){
	refreshLabel();
	super.paint(g2d,parentOffsetX,parentOffsetY);
}
}