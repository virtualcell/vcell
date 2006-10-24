package org.vcell.ncbc.physics.component.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.gui.graph.GraphModel;
/**
 * This type was created in VisualAge.
 */
public class DeviceLocationEdge extends cbit.gui.graph.EdgeShape {
	private org.vcell.ncbc.physics.component.Connection connection = null;
/**
 * ReactionParticipantShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public DeviceLocationEdge(DeviceNode argDeviceNode, LocationNode argLocationNode, GraphModel graphModel) {
	super(argDeviceNode, argLocationNode, graphModel);
	defaultFG = java.awt.Color.lightGray;
	forgroundColor = defaultFG;
	refreshLabel();
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 9:07:18 PM)
 * @return java.lang.Object
 * @param deviceNode ncbc.physics.component.gui.DeviceNode
 * @param locationNode ncbc.physics.component.gui.LocationNode
 */
public static Object createModelObject(DeviceNode deviceNode, LocationNode locationNode) {
	return deviceNode.getDevice().getName()+"_to_"+locationNode.getLocation_model().getName();
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ReactionStepShape
 */
public DeviceNode getDeviceNode() {
	return (DeviceNode)startShape;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 9:26:21 PM)
 * @return int
 */
public int getLineStyle() {
	return LINE_STYLE_DASHED;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ReactionStepShape
 */
public LocationNode getLocationNode() {
	return (LocationNode)endShape;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return createModelObject(getDeviceNode(),getLocationNode());
}
/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel("");
}
}
