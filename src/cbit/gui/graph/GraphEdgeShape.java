package cbit.gui.graph;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.gui.graph.GraphModel;
/**
 * This type was created in VisualAge.
 */
public class GraphEdgeShape extends cbit.gui.graph.EdgeShape {
	protected cbit.util.graph.Edge fieldEdge = null;
/**
 * ReactionParticipantShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public GraphEdgeShape(cbit.util.graph.Edge edge, NodeShape node1Shape, NodeShape node2Shape, GraphModel graphModel) {
	super(node1Shape, node2Shape, graphModel);
	this.fieldEdge = edge;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return fieldEdge;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ReactionStepShape
 */
public NodeShape getNode1Shape() {
	return (NodeShape)startShape;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ReactionStepShape
 */
public NodeShape getNode2Shape() {
	return (NodeShape)endShape;
}
/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel("");
}
}
