package org.vcell.physics.jgraph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.Edge;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexView;
import org.vcell.physics.component.Connection;
import org.vcell.physics.component.Connector;
import org.vcell.physics.component.ModelComponent;
import org.vcell.physics.component.OOModel;
import org.vcell.physics.component.OOModelTest;


public class HelloWorld {

	public static DefaultGraphCell[] getCells(){
		Vector<DefaultGraphCell> cellList = new Vector<DefaultGraphCell>();
		HashMap<Object, DefaultGraphCell> cellHashMap = new HashMap<Object, DefaultGraphCell>();

		OOModel ooModel = OOModelTest.getExample();
		ModelComponent[] modelComponents = ooModel.getModelComponents();
		for (int i = 0; i < modelComponents.length; i++) {
			DefaultGraphCell modelComponentCell = new DefaultGraphCell(modelComponents[i]);
			GraphConstants.setBounds(modelComponentCell.getAttributes(), new Rectangle2D.Double((i+1)*100,(i+1)*100,200,50));
			GraphConstants.setGradientColor(modelComponentCell.getAttributes(), Color.orange);
			GraphConstants.setOpaque(modelComponentCell.getAttributes(), true);
			GraphConstants.setBorderColor(modelComponentCell.getAttributes(), Color.black);
			Connector[] connectors = modelComponents[i].getConnectors();
			for (int j = 0; j < connectors.length; j++) {
				DefaultPort port = (DefaultPort)modelComponentCell.addPort(new Point2D.Double(50*j,50*j), connectors[j]);
				cellHashMap.put(connectors[j], port);
			}
			cellList.add(modelComponentCell);
			cellHashMap.put(modelComponents[i], modelComponentCell);
		}
		Connection[] connections = ooModel.getConnections();
		for (int i = 0; i < connections.length; i++) {
			DefaultGraphCell connectionCell = new DefaultGraphCell(connections[i]);
			GraphConstants.setBounds(connectionCell.getAttributes(), new Rectangle2D.Double(1,1,10,10));
			GraphConstants.setGradientColor(connectionCell.getAttributes(), Color.orange);
			GraphConstants.setOpaque(connectionCell.getAttributes(), true);
			GraphConstants.setBorderColor(connectionCell.getAttributes(), Color.black);
			cellList.add(connectionCell);
			cellHashMap.put(connections[i], connectionCell);
			Connector[] connectors = connections[i].getConnectors();
			for (int j = 0; j < connectors.length; j++) {
				DefaultEdge edge = new DefaultEdge("conn_"+i+"_"+j);
				DefaultGraphCell port = cellHashMap.get(connectors[j]);
				edge.setSource(port);
				edge.setTarget(connectionCell);
				cellList.add(edge);
			}
		}
		return cellList.toArray(new DefaultGraphCell[cellList.size()]);
	}
	
	public static void main(String[] args) {

		// Construct Model and Graph
		GraphModel model = new DefaultGraphModel();
//		GraphLayoutCache view = new GraphLayoutCache(model,
//				new DefaultCellViewFactory()
//		);
		JGraph graph = new JGraph(model); // ,view);
		graph.getGraphLayoutCache().setFactory(new DefaultCellViewFactory() {
			/**
			 * Constructs a new instance of a PortView view for the specified object
			 */
			protected PortView createPortView(Port p) {
				return new MyPortView(p);
			}
		});
		
		// Control-drag should clone selection
		graph.setCloneable(true);
		graph.setPortsVisible(true);
		// Enable edit without final RETURN keystroke
		graph.setInvokesStopCellEditing(true);

		// When over a cell, jump to its default port (we only have one, anyway)
		graph.setJumpToDefaultPort(true);

		// Insert all three cells in one call, so we need an array to store them
		DefaultGraphCell[] cells = getCells();  //new DefaultGraphCell[3];

//		// Create Hello Vertex
//		cells[0] = createVertex("Hello", 20, 20, 40, 20, null, false);
//
//		// Create World Vertex
//		cells[1] = createVertex("World", 140, 140, 40, 20, Color.ORANGE, true);
//
//		// Create Edge
//		DefaultEdge edge = new DefaultEdge();
//		// Fetch the ports from the new vertices, and connect them with the edge
//		edge.setSource(cells[0].getChildAt(0));
//		edge.setTarget(cells[1].getChildAt(0));
//		cells[2] = edge;
//
//		// Set Arrow Style for edge
//		int arrow = GraphConstants.ARROW_CLASSIC;
//		GraphConstants.setLineEnd(edge.getAttributes(), arrow);
//		GraphConstants.setEndFill(edge.getAttributes(), true);

		// Insert the cells via the cache, so they get selected
		graph.getGraphLayoutCache().insert(cells);

		// Show in Frame
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(graph));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public static DefaultGraphCell createVertex(DefaultGraphCell cell, double x,
			double y, double w, double h, Color bg, boolean raised) {

		// Create vertex with the given name
		//DefaultGraphCell cell = new DefaultGraphCell(name);

		// Set bounds
		GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(
				x, y, w, h));

		// Set fill color
		if (bg != null) {
			GraphConstants.setGradientColor(cell.getAttributes(), bg);
			GraphConstants.setOpaque(cell.getAttributes(), true);
		}

		// Set raised border
		if (raised)
			GraphConstants.setBorder(cell.getAttributes(), BorderFactory
					.createRaisedBevelBorder());
		else
			// Set black border
			GraphConstants.setBorderColor(cell.getAttributes(), Color.black);

		// Add a Floating Port
		cell.addPort();

		return cell;
	}

}