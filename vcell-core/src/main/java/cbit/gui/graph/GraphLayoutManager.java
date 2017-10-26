/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.vcell.util.graphlayout.ContainedGraphLayouter;
import org.vcell.util.graphlayout.EdgeTugLayouter;
import org.vcell.util.graphlayout.ExpandCanvasLayouter;
import org.vcell.util.graphlayout.GenericLogicGraphLayouter;
import org.vcell.util.graphlayout.GraphLayouter.Client;
import org.vcell.util.graphlayout.RandomLayouter;
import org.vcell.util.graphlayout.ShrinkCanvasLayouter;
import org.vcell.util.graphlayout.SimpleElipticalLayouter;
import org.vcell.util.graphlayout.StretchToBoundaryLayouter;
import org.vcell.util.graphlayout.energybased.ShootAndCutLayouter;

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
		
		public static final List<String> LAYOUTS_RPI = Arrays.asList(ANNEALER, CIRCULARIZER, CYCLEIZER,
				FORCEDIRECT, LEVELLER, RANDOMIZER, RELAXER, STABILIZER);
			
	}
	
	public void layout(Client client) throws Exception {
		String layoutName = client.getLayoutName();
		if(ContainedGraphLayouter.DefaultLayouters.NAMES.contains(layoutName)) {
			layoutContainedGraph(client);
		} else if(OldLayouts.LAYOUTS_RPI.contains(layoutName)) {
			layoutRPI(client);
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
		} else if(ShrinkCanvasLayouter.LAYOUT_NAME.equals(layoutName)) {
			layouter = new ShrinkCanvasLayouter();
		} else if(ExpandCanvasLayouter.LAYOUT_NAME.equals(layoutName)) {
			layouter = new ExpandCanvasLayouter();
		} else if(GenericLogicGraphLayouter.LAYOUT_NAME.equals(layoutName)) {
			layouter = new GenericLogicGraphLayouter();
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
		
}
