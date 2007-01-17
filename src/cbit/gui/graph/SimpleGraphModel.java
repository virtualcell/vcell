package cbit.gui.graph;

import cbit.gui.graph.GraphModel;
import cbit.gui.graph.GraphEvent;
import cbit.gui.graph.EdgeShape;
import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.Shape;
/**
 * Insert the type's description here.
 * Creation date: (7/8/2003 9:11:57 AM)
 * @author: Jim Schaff
 */
public class SimpleGraphModel extends GraphModel {
	private cbit.util.graph.Graph fieldGraph = new cbit.util.graph.Graph();
/**
 * SimpleGraphModel constructor comment.
 */
public SimpleGraphModel() {
	super();
}
/**
 * Gets the graph property (cbit.vcell.mapping.potential.Graph) value.
 * @return The graph property value.
 * @see #setGraph
 */
public cbit.util.graph.Graph getGraph() {
	return fieldGraph;
}
/**
 * Insert the method's description here.
 * Creation date: (7/9/2003 4:29:13 PM)
 * @return cbit.vcell.constraints.gui.NodeShape
 */
public NodeShape getNodeShape(cbit.util.graph.Node node) {
	NodeShape nodeShape = new NodeShape(node,this,getGraph().getDegree(node));
	nodeShape.setLabel(node.getName());
	return nodeShape;
}
/**
 * Insert the method's description here.
 * Creation date: (7/8/2003 9:11:57 AM)
 */
public void refreshAll() {
	clearAllShapes();
	if (getGraph() == null){
		fireGraphChanged(new GraphEvent(this));
		return;
	}
	ContainerShape containerShape = new SimpleContainerShape(getGraph(),this);
	addShape(containerShape);
	cbit.util.graph.Node nodes[] = getGraph().getNodes();
	for (int i = 0; i < nodes.length; i++){
		NodeShape nodeShape = getNodeShape(nodes[i]);
		containerShape.addChildShape(nodeShape);
		addShape(nodeShape);
	}
	cbit.util.graph.Edge edges[] = getGraph().getEdges();
	for (int i = 0; i < edges.length; i++){
		NodeShape node1 = (NodeShape)getShapeFromModelObject(edges[i].getNode1());
		NodeShape node2 = (NodeShape)getShapeFromModelObject(edges[i].getNode2());
		EdgeShape edgeShape = new GraphEdgeShape(edges[i],node1,node2,this);
		containerShape.addChildShape(edgeShape);
		addShape(edgeShape);
	}
	fireGraphChanged(new GraphEvent(this));
}
/**
 * Sets the graph property (cbit.vcell.mapping.potential.Graph) value.
 * @param graph The new value for the property.
 * @see #getGraph
 */
public void setGraph(cbit.util.graph.Graph graph) {
	cbit.util.graph.Graph oldValue = fieldGraph;
	fieldGraph = graph;
	firePropertyChange("graph", oldValue, graph);
	refreshAll();
}
/**
 * This method was created in VisualAge.
 */
public void setRandomLayout(boolean bRandomize) {

	try {
		//
		// assert random characteristics
		//
		Shape topShape = getTopShape();
		if (topShape instanceof ContainerShape){
			ContainerShape containerShape = (ContainerShape)topShape;
			containerShape.setRandomLayout(bRandomize);
		}
		
	}catch (Exception e){
		System.out.println("top shape not found");
		e.printStackTrace(System.out);
	}

}
}
