package cbit.gui.graph;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.vcell.util.graphlayout.ContainedGraphLayouter;
import org.vcell.util.graphlayout.EdgeTugLayouter;
import org.vcell.util.graphlayout.RandomLayouter;
import org.vcell.util.graphlayout.SimpleElipticalLayouter;
import org.vcell.util.graphlayout.StretchToBoundaryLayouter;
import org.vcell.util.graphlayout.GraphLayouter.Client;
import org.vcell.util.graphlayout.GraphLayouter.Client.Default;
import org.vcell.util.graphlayout.energybased.ShootAndCutLayouter;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskStatusSupport;

import com.genlogic.GraphLayout.GlgCube;
import com.genlogic.GraphLayout.GlgGraphLayout;
import com.genlogic.GraphLayout.GlgGraphNode;
import com.genlogic.GraphLayout.GlgPoint;

import edu.rpi.graphdrawing.Annealer;
import edu.rpi.graphdrawing.Blackboard;
import edu.rpi.graphdrawing.Circularizer;
import edu.rpi.graphdrawing.Cycleizer;
import edu.rpi.graphdrawing.Embedder;
import edu.rpi.graphdrawing.ForceDirect;
import edu.rpi.graphdrawing.Leveller;
import edu.rpi.graphdrawing.Node;
import edu.rpi.graphdrawing.Randomizer;
import edu.rpi.graphdrawing.Relaxer;
import edu.rpi.graphdrawing.Stabilizer;

/*  Graph embedding (aka Layout, i.e. placement of nodes)
 *  Last change: January 2011 by Oliver
 */

public class GraphLayoutManager {
	
	public static class VCellTaskClient extends Default implements Client {
		protected final AsynchClientTask task;

		public VCellTaskClient(GraphView graphView, String layoutName, AsynchClientTask task) { 
			super(graphView, layoutName); 
			this.task = task;
		}
		
		@Override
		public boolean isRequestingStop() { 
			ClientTaskStatusSupport taskSupport = task.getClientTaskStatusSupport();
			return taskSupport != null ? taskSupport.isInterrupted() : false; 
		}

	}

	
	public static class OldLayouts {
		
		public static final String ANNEALER = "Annealer (RPI)";
		public static final String CIRCULARIZER = "Circularizer (RPI)";
		public static final String CYCLEIZER = "Cycleizer (RPI)";
		public static final String FORCEDIRECT = "ForceDirect (RPI)";
		public static final String LEVELLER = "Leveller (RPI)";
		public static final String RANDOMIZER = "Randomizer (RPI)";
		public static final String RELAXER = "Relaxer (RPI)";
		public static final String STABILIZER = "Stabilizer (RPI)";		
		public static final String GLG = "GLG";
		
		public static final List<String> LAYOUTS_RPI = Arrays.asList(ANNEALER, CIRCULARIZER, CYCLEIZER,
				FORCEDIRECT, LEVELLER, RANDOMIZER, RELAXER, STABILIZER);
			
	}
	
	public void layout(Client client) throws Exception {
		String layoutName = client.getLayoutName();
		if(ContainedGraphLayouter.DefaultLayouters.NAMES.contains(layoutName)) {
			layoutContainedGraph(client);
		} else if(OldLayouts.LAYOUTS_RPI.contains(layoutName)) {
			layoutRPI(client);
		} else if(OldLayouts.GLG.equals(layoutName)) {
			layoutGLG(client);
		} else {
			throw new Exception("Unsupported Layout " + layoutName);
		}
	}

	public void layoutContainedGraph(Client client) {
		ContainedGraphLayouter layouter = null;
		String layoutName = client.getLayoutName();
		if(RandomLayouter.LAYOUT_NAME.equals(layoutName)) {
			layouter = new RandomLayouter();				
		} else if(EdgeTugLayouter.LAYOUT_NAME.equals(layoutName)) {
			layouter = new EdgeTugLayouter();
		} else if(ShootAndCutLayouter.LAYOUT_NAME.equals(layoutName)) {
			layouter = new ShootAndCutLayouter();
		} else if(StretchToBoundaryLayouter.LAYOUT_NAME.equals(layoutName)) {
			layouter = new StretchToBoundaryLayouter();
		} else if(SimpleElipticalLayouter.LAYOUT_NAME.equals(layoutName)) {
			layouter = new SimpleElipticalLayouter();
		}
		if(layouter != null) {
			layouter.layout(client);
		}
	}
	
	public void layoutRPI(Client client) throws Exception {
		Blackboard bb = new Blackboard();
		HashMap<String, Shape> nodeShapeMap = new HashMap<String, Shape>();
		for(Shape shape : client.getGraphModel().getShapes()) {
			Node newNode = null;
			if (ShapeUtil.isMovable(shape)) {
				newNode = bb.addNode(shape.getLabel());
			}
			// initialize node location to current absolute position
			if (newNode != null) {
				newNode.XY(shape.getSpaceManager().getAbsLoc().x, shape.getSpaceManager().getAbsLoc().y);
				nodeShapeMap.put(newNode.label(), shape);
			}
		}
		for(Shape shape : client.getGraphModel().getShapes()) {
			if (shape instanceof EdgeShape) {
				EdgeShape edgeShape = (EdgeShape) shape;
				Shape startShape = edgeShape.getStartShape();
				Shape endShape = edgeShape.getEndShape();
				if(edgeShape.isDirectedForward()) {
					bb.addEdge(startShape.getLabel(), endShape.getLabel());
				} else {
					bb.addEdge(endShape.getLabel(), startShape.getLabel());					
				}
			}
		}

		bb.setArea(0, 0, client.getWidth(), client.getHeight());
		bb.globals.D(20);

		bb.addEmbedder(GraphLayoutManager.OldLayouts.ANNEALER, new Annealer(bb));
		bb.addEmbedder(GraphLayoutManager.OldLayouts.CIRCULARIZER, new Circularizer(bb));
		bb.addEmbedder(GraphLayoutManager.OldLayouts.CYCLEIZER, new Cycleizer(bb));
		bb.addEmbedder(GraphLayoutManager.OldLayouts.FORCEDIRECT, new ForceDirect(bb));
		bb.addEmbedder(GraphLayoutManager.OldLayouts.LEVELLER, new Leveller(bb));
		bb.addEmbedder(GraphLayoutManager.OldLayouts.RANDOMIZER, new Randomizer(bb));
		bb.addEmbedder(GraphLayoutManager.OldLayouts.RELAXER, new Relaxer(bb));
		bb.addEmbedder(GraphLayoutManager.OldLayouts.STABILIZER, new Stabilizer(bb));

		bb.setEmbedding(client.getLayoutName());
		@SuppressWarnings("unchecked")
		List<Node> nodeList = bb.nodes();
		bb.PreprocessNodes();
		Embedder embedder = bb.embedder();
		embedder.Init();
		for (int i = 0; i < 1000; i++) {
			embedder.Embed();
		}
		bb.removeDummies();
		@SuppressWarnings("unchecked")
		List<Node> nodesRaw = bb.nodes();
		nodeList = nodesRaw;
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.get(i);
			Shape shape = nodeShapeMap.get(node.label());
			if(shape != null) {
				shape.setAbsPos((int) node.x(), (int) node.y());				
			}
		}
		new StretchToBoundaryLayouter().layout(client);
	}
	
	public void layoutGLG(Client client) throws Exception {
		GlgGraphLayout graph = new GlgGraphLayout();
		graph.SetUntangle(true); // true
		// specify dimensions for the graph! 400x400
		// System.out.println("H:"+getGraphPane().getHeight()+" W"+getGraphPane().getWidth());
		GraphModel graphModel = client.getGraphModel();
		Rectangle boundary = 
			graphModel.getContainerLayout().getBoundaryForAutomaticLayout(graphModel.getTopShape());
		GlgCube graphDim = new GlgCube();
		graphDim.p1 = new GlgPoint(boundary.getMinX(), boundary.getMinY(), 0);
		graphDim.p2 = new GlgPoint(boundary.getMaxX(), boundary.getMaxY(), 0);
		graph.dimensions = graphDim;
		// Add nodes (Vertex) to the graph
		HashMap<Shape, GlgGraphNode> nodeMap = new HashMap<Shape, GlgGraphNode>();
		Collection<Shape> shapes = graphModel.getShapes();
		for(Shape shape : shapes) {
			// add to the graph
			GlgGraphNode graphNode;
			if (ShapeUtil.isMovable(shape)) {
				graphNode = graph.AddNode(null, 0, null);
				graph.SetNodePosition(graphNode, ShapeUtil.getAbsCenterX(shape), 
						ShapeUtil.getAbsCenterY(shape), 0);
				nodeMap.put(shape, graphNode);
			} else {
				continue;
			}
			// add to the hashmap
		}
		// Add edges
		for(Shape shape : shapes) {
			if (shape instanceof EdgeShape) {
				EdgeShape edgeShape = (EdgeShape) shape;
				Shape startShape = edgeShape.getStartShape();
				Shape endShape = edgeShape.getEndShape();
				GlgGraphNode startNode = nodeMap.get(startShape);
				GlgGraphNode endNode = nodeMap.get(endShape);
				if(!graph.NodesConnected(startNode, endNode)) {
					if(edgeShape.isDirectedForward()) {
						graph.AddEdge(startNode, endNode, null, 0, null);						
					} else {
						graph.AddEdge(endNode, startNode, null, 0, null);						
					}
				}
			}
		}
		// call layout algorithm
		while (!graph.SpringIterate()) {
			;
		}
		graph.Update();
		// Update positions
		for(Shape shape : graphModel.getShapes()) {
			GlgGraphNode graphNode = nodeMap.get(shape);
			if (graphNode != null) {
				GlgPoint glgPoint = graph.GetNodePosition(graphNode);
				shape.setAbsPos((int) glgPoint.x, (int) glgPoint.y);
			}
		}
		new StretchToBoundaryLayouter().layout(client);
	}
	
}