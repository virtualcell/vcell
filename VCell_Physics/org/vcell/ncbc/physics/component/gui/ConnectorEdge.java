package org.vcell.ncbc.physics.component.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.gui.graph.GraphModel;
/**
 * This type was created in VisualAge.
 */
public class ConnectorEdge extends cbit.gui.graph.EdgeShape {
	private org.vcell.ncbc.physics.component.Connection connection = null;
/**
 * ReactionParticipantShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public ConnectorEdge(ConnectorNode argConnectorNode1, ConnectorNode argConnectorNode2, org.vcell.ncbc.physics.component.Connection argConnection, GraphModel graphModel) {
	super(argConnectorNode1, argConnectorNode2, graphModel);
	this.connection = argConnection;
	refreshLabel();
}
/**
 * ReactionParticipantShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public ConnectorEdge(DeviceNode argDeviceNode1, DeviceNode argDeviceNode2, org.vcell.ncbc.physics.component.Connection argConnection, GraphModel graphModel) {
	super(argDeviceNode1, argDeviceNode2, graphModel);
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
	setLabel("");
}
}
