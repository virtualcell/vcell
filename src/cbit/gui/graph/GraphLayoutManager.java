package cbit.gui.graph;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.vcell.util.graphlayout.ContainedGraphLayouter;
import org.vcell.util.graphlayout.EdgeTugLayouter;
import org.vcell.util.graphlayout.RandomLayouter;
import org.vcell.util.graphlayout.energybased.ShootAndCutLayouter;

import com.genlogic.GraphLayout.GlgCube;
import com.genlogic.GraphLayout.GlgGraphEdge;
import com.genlogic.GraphLayout.GlgGraphLayout;
import com.genlogic.GraphLayout.GlgGraphNode;
import com.genlogic.GraphLayout.GlgPoint;

import cbit.vcell.graph.CatalystShape;
import cbit.vcell.graph.FluxShape;
import cbit.vcell.graph.ProductShape;
import cbit.vcell.graph.ReactantShape;
import cbit.vcell.graph.ReactionParticipantShape;
import cbit.vcell.graph.ReactionStepShape;
import cbit.vcell.graph.SpeciesContextShape;
import cbit.vcell.model.SpeciesContext;
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
	
	protected final GraphView graphView;
	
	public GraphLayoutManager(GraphView graphView) {
		this.graphView = graphView;
	}
	
	public void layout(String layoutName) throws Exception {
		if(ContainedGraphLayouter.DefaultLayouters.NAMES.contains(layoutName)) {
			layoutContainedGraph(layoutName);
		} else if(OldLayouts.LAYOUTS_RPI.contains(layoutName)) {
			layoutRPI(layoutName);
		} else if(OldLayouts.GLG.equals(layoutName)) {
			layoutGLG();
		} else {
			throw new Exception("Unsupported Layout " + layoutName);
		}
	}

	public void layoutContainedGraph(String layoutName) {
		VCellGraphToContainedGraphMapper mapper = 
			new VCellGraphToContainedGraphMapper(graphView.getGraphModel());
		mapper.updateContainedGraphFromVCellGraph();
		ContainedGraphLayouter layouter = null;
		if(RandomLayouter.LAYOUT_NAME.equals(layoutName)) {
			layouter = new RandomLayouter();				
		} else if(EdgeTugLayouter.LAYOUT_NAME.equals(layoutName)) {
			layouter = new EdgeTugLayouter();
		} else if(ShootAndCutLayouter.LAYOUT_NAME.equals(layoutName)) {
			layouter = new ShootAndCutLayouter();
		}
		if(layouter != null) {
			layouter.layout(mapper.getContainedGraph());
			mapper.updateVCellGraphFromContainedGraph();
			graphView.repaint();				
		}
	}
	
	public void layoutRPI(String layoutName) throws Exception {
		Blackboard bb = new Blackboard();
		HashMap<String, Shape> nodeShapeMap = new HashMap<String, Shape>();
		for(Shape shape : graphView.getGraphModel().getShapes()) {
			Node newNode = null;
			if (shape instanceof SpeciesContextShape) {
				newNode = bb.addNode(shape.getLabel());
			}
			if (shape instanceof ReactionStepShape) {
				newNode = bb.addNode(shape.getLabel());
			}
			// initialize node location to current absolute position
			if (newNode != null) {
				newNode.XY(shape.getSpaceManager().getAbsLoc().x, shape.getSpaceManager().getAbsLoc().y);
				nodeShapeMap.put(newNode.label(), shape);
			}
		}
		for(Shape shape : graphView.getGraphModel().getShapes()) {
			if (shape instanceof ReactionParticipantShape) {
				ReactionParticipantShape rpShape = (ReactionParticipantShape) shape;
				SpeciesContextShape scShape = (SpeciesContextShape) rpShape
						.getStartShape();
				ReactionStepShape rsShape = (ReactionStepShape) rpShape
						.getEndShape();
				if (rpShape instanceof ReactantShape) {
					bb.addEdge(scShape.getLabel(), rsShape.getLabel());
				} else if (rpShape instanceof ProductShape) {
					bb.addEdge(rsShape.getLabel(), scShape.getLabel());
				} else if (rpShape instanceof CatalystShape) {
					bb.addEdge(scShape.getLabel(), rsShape.getLabel());
				} else if (rpShape instanceof FluxShape) {
					//
					// check if coming or going
					//
					SpeciesContext sc = scShape.getSpeciesContext();
					if (sc.getStructure() == 
						rsShape.getReactionStep().getStructure().getParentStructure()) {
						bb.addEdge(scShape.getLabel(), rsShape.getLabel());
					} else {
						bb.addEdge(rsShape.getLabel(), scShape.getLabel());
					}
				}
			}
			// edge doesn't need any init now.
		}

		bb.setArea(0, 0, graphView.getWidth(), graphView.getHeight());
		bb.globals.D(20);

		bb.addEmbedder(GraphLayoutManager.OldLayouts.ANNEALER, new Annealer(bb));
		bb.addEmbedder(GraphLayoutManager.OldLayouts.CIRCULARIZER, new Circularizer(bb));
		bb.addEmbedder(GraphLayoutManager.OldLayouts.CYCLEIZER, new Cycleizer(bb));
		bb.addEmbedder(GraphLayoutManager.OldLayouts.FORCEDIRECT, new ForceDirect(bb));
		bb.addEmbedder(GraphLayoutManager.OldLayouts.LEVELLER, new Leveller(bb));
		bb.addEmbedder(GraphLayoutManager.OldLayouts.RANDOMIZER, new Randomizer(bb));
		bb.addEmbedder(GraphLayoutManager.OldLayouts.RELAXER, new Relaxer(bb));
		bb.addEmbedder(GraphLayoutManager.OldLayouts.STABILIZER, new Stabilizer(bb));

		bb.setEmbedding(layoutName);
		@SuppressWarnings("unchecked")
		List<Node> nodeList = bb.nodes();
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.get(i);
			System.out.println("Node " + node.label() + " @ (" + node.x() + ","
					+ node.y() + ")");
		}
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
		// calculate offset and scaling so that resulting graph fits on canvas
		double lowX = 100000;
		double highX = -100000;
		double lowY = 100000;
		double highY = -100000;
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.get(i);
			lowX = Math.min(lowX, node.x());
			highX = Math.max(highX, node.x());
			lowY = Math.min(lowY, node.y());
			highY = Math.max(highY, node.y());
		}
		double scaleX = graphView.getWidth() / (1.5 * (highX - lowX));
		double scaleY = graphView.getHeight() / (1.5 * (highY - lowY));
		int offsetX = graphView.getWidth() / 6;
		int offsetY = graphView.getHeight() / 6;
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.get(i);
			Shape shape = nodeShapeMap.get(node.label());
			Point parentLoc = shape.getParent().getSpaceManager().getAbsLoc();
			shape.getSpaceManager().setRelPos(
			(int) (scaleX * (node.x() - lowX)) + offsetX + parentLoc.x,
			(int) ((scaleY * (node.y() - lowY)) + offsetY + parentLoc.y));
			System.out.println("Shape " + shape.getLabel() + " @ "
					+ shape.getSpaceManager().getAbsLoc());
		}
		graphView.repaint();
	}
	
	public void layoutGLG() throws Exception {
		GlgGraphLayout graph = new GlgGraphLayout();
		graph.SetUntangle(true); // true
		// specify dimensions for the graph! 400x400
		// System.out.println("H:"+getGraphPane().getHeight()+" W"+getGraphPane().getWidth());
		GlgCube graphDim = new GlgCube();
		GlgPoint newPoint = new GlgPoint(0, 0, 0);
		graphDim.p1 = newPoint;
		// newPoint = new com.genlogic.GlgPoint(getGraphPane().getWidth()-20,
		// getGraphPane().getHeight()-10, 0);//400,400,0
		newPoint = new GlgPoint(1600, 1600, 0);
		graphDim.p2 = newPoint;
		graph.dimensions = graphDim;
		// Add nodes (Vertex) to the graph
		Collection<Shape> shapeEnum = graphView.getGraphModel().getShapes();
		GlgGraphNode graphNode;
		HashMap<Shape, GlgGraphNode> nodeMap = new HashMap<Shape, GlgGraphNode>();
		for(Shape shape : shapeEnum) {
			// add to the graph
			if (ShapeUtil.isMovable(shape)) {
				graphNode = graph.AddNode(null, 0, null);
			} else {
				continue;
			}
			// add to the hashmap
			nodeMap.put(shape, graphNode);
		}
		// Add edges
		shapeEnum = graphView.getGraphModel().getShapes();
		for(Shape shape : graphView.getGraphModel().getShapes()) {
			if (shape instanceof ReactionParticipantShape) {
				ReactionParticipantShape rpShape = (ReactionParticipantShape) shape;
				SpeciesContextShape scShape = (SpeciesContextShape) rpShape.getStartShape();
				ReactionStepShape rsShape = (ReactionStepShape) rpShape.getEndShape();
				if (rpShape instanceof ReactantShape) {
					graph.AddEdge(nodeMap.get(scShape), nodeMap.get(rsShape), null, 0, null);
				} else if (rpShape instanceof ProductShape) {
					graph.AddEdge(nodeMap.get(rsShape), nodeMap.get(scShape), null, 0, null);
				} else if (rpShape instanceof CatalystShape) {
					graph.AddEdge(nodeMap.get(scShape), nodeMap.get(rsShape), null, 0, null);
				} else if (rpShape instanceof FluxShape) {
					// check if coming or going
					SpeciesContext sc = scShape.getSpeciesContext();
					if (sc.getStructure() == rsShape.getReactionStep()
							.getStructure().getParentStructure()) {
						graph.AddEdge(nodeMap.get(scShape), nodeMap.get(rsShape), null, 0, null);
					} else {
						graph.AddEdge(nodeMap.get(scShape), nodeMap.get(rsShape), null, 0, null);
					}
				} else {
					continue;
				}
			}
		}
		// call layout algorithm
		while (!graph.SpringIterate()) {
			;
		}
		graph.Update();
		// resize and scale the graph
		// com.genlogic.GlgObject edgeArray = graph.edge_array;
		@SuppressWarnings("unchecked")
		Vector<GlgGraphEdge> edgeVector = graph.edge_array;
		double distance, minDistance = Double.MAX_VALUE;
		for (int i = 0; i < edgeVector.size(); i++) {
			GlgGraphEdge edge = edgeVector.elementAt(i);
			distance = java.awt.geom.Point2D.distance(
					edge.start_node.display_position.x,
					edge.start_node.display_position.y,
					edge.end_node.display_position.x,
					edge.end_node.display_position.y);
			minDistance = distance < minDistance ? distance : minDistance;
		}
		double ratio = 1.0;
		if (minDistance > 40) {
			ratio = 40.0 / minDistance;
		}
		// Update positions
		shapeEnum = graphView.getGraphModel().getShapes();
		Point place;
		com.genlogic.GraphLayout.GlgPoint glgPoint;
		for(Shape shape : graphView.getGraphModel().getShapes()) {
			// test if it is contained in the nodeMap
			graphNode = nodeMap.get(shape);
			if (graphNode != null) {
				glgPoint = graph.GetNodePosition(graphNode);
				// glgPoint = graphNode.display_position;
				place = new Point();
				place.setLocation(glgPoint.x * ratio + 30, glgPoint.y * ratio
						+ 30);
				shape.getSpaceManager().setRelPos(place);
			}
		}
		Dimension graphSize = new Dimension((int) (1600 * ratio) + 50,
				(int) (1600 * ratio) + 50);
		graphView.setSize(graphSize);
	}
	
}