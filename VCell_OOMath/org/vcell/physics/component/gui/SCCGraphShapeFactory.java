package org.vcell.physics.component.gui;

import cbit.gui.graph.GraphEdgeShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.NodeShape;
import cbit.gui.graph.SimpleGraphModel.GraphShapeFactory;
import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;

public class SCCGraphShapeFactory implements GraphShapeFactory {
	public GraphEdgeShape getEdgeShape(Edge edge, NodeShape beginShape,	NodeShape endShape, GraphModel graphModel, Graph graph) {
		return new GraphEdgeShape(edge,beginShape,endShape,graphModel,true,false);
	}

	public NodeShape getNodeShape(Node node, GraphModel graphModel, Graph graph) {
		return new SCCNodeShape(node,graphModel,2);
	}

	public String getTitle(){
		return "Strongly Connected Components";
	}
}
