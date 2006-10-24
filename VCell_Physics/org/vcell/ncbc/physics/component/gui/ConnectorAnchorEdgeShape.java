package org.vcell.ncbc.physics.component.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.gui.graph.GraphModel;
/**
 * This type was created in VisualAge.
 */
public class ConnectorAnchorEdgeShape extends cbit.gui.graph.EdgeShape {
	private Object modelObject = null;
/**
 * ReactionParticipantShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public ConnectorAnchorEdgeShape(DeviceNode argDeviceNode, ConnectorNode argDevicePinNode, GraphModel graphModel) {
	super(argDeviceNode, argDevicePinNode, graphModel);
	modelObject = createModelObjectString(argDevicePinNode);
	refreshLabel();
}
/**
 * Insert the method's description here.
 * Creation date: (12/12/2003 12:38:01 PM)
 * @return java.lang.String
 * @param stateComplexNode cbit.vcell.complex.gui.StateComplexNode
 * @param bindingSiteNode cbit.vcell.complex.gui.BindingSiteNode
 */
public static String createModelObjectString(ConnectorNode devicePinNode) {
	return new String(((org.vcell.ncbc.physics.component.Connector)devicePinNode.getModelObject()).getDevice().getName()+"_to_"+devicePinNode.getLabel());
}
/**
 * Insert the method's description here.
 * Creation date: (12/12/2003 12:38:01 PM)
 * @return java.lang.String
 * @param stateComplexNode cbit.vcell.complex.gui.StateComplexNode
 * @param bindingSiteNode cbit.vcell.complex.gui.BindingSiteNode
 */
public static String createModelObjectString(org.vcell.ncbc.physics.component.Connector connector) {
	return new String(connector.getDevice().getName()+"_to_"+connector.getName());
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ReactionStepShape
 */
public ConnectorNode getConnectorNode() {
	return (ConnectorNode)endShape;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ReactionStepShape
 */
public DeviceNode getDeviceShape() {
	return (DeviceNode)startShape;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return modelObject;
}
/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel("");
}
}
