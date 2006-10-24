package org.vcell.ncbc.physics.component.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.gui.graph.GraphModel;
/**
 * This type was created in VisualAge.
 */
public class LocationAdjacencyEdge extends cbit.gui.graph.EdgeShape {
	private org.vcell.ncbc.physics.component.Connection connection = null;
/**
 * ReactionParticipantShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public LocationAdjacencyEdge(LocationNode argLocationNode1, LocationNode argLocationNode2, GraphModel graphModel) {
	super(argLocationNode1, argLocationNode2, graphModel);
	defaultFG = java.awt.Color.lightGray;
	forgroundColor = defaultFG;
	refreshLabel();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public static Object createModelObject(LocationNode locationNode1, LocationNode locationNode2) {
	return locationNode1.getLocation_model().getName()+"_to_"+locationNode2.getLocation_model().getName();
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
public LocationNode getLocationNode1() {
	return (LocationNode)startShape;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ReactionStepShape
 */
public LocationNode getLocationNode2() {
	return (LocationNode)endShape;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return createModelObject(getLocationNode1(),getLocationNode2());
}
/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel("");
}
}
