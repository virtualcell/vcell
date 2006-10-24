package org.vcell.physics.component.gui;

import org.vcell.expression.IExpression;
import org.vcell.physics.component.Symbol;

import cbit.gui.graph.GraphEdgeShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.NodeShape;
import cbit.gui.graph.SimpleGraphModel.GraphShapeFactory;
import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;

public class ConnectivityGraphShapeFactory implements GraphShapeFactory {
	public GraphEdgeShape getEdgeShape(Edge edge, NodeShape beginShape,	NodeShape endShape, GraphModel graphModel, Graph graph) {
		return new GraphEdgeShape(edge,beginShape,endShape,graphModel,false);
	}

	public NodeShape getNodeShape(Node node, GraphModel graphModel, Graph graph) {
		if (node.getData() instanceof Symbol){
			return new VariableNodeShape(node,graphModel,2);
		}
		if (node.getData() instanceof IExpression){
			return new ExpressionNodeShape(node,graphModel,4);
		}
		throw new RuntimeException("unexpected node data type "+node.getData());
	}

	public String getTitle(){
		return "Variable/Equation Matching";
	}
	public void setGraph(Graph argGraph) {
	}

}
