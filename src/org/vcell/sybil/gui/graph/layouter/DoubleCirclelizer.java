package org.vcell.sybil.gui.graph.layouter;

/*   oubleCirclelizer  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Arrange graph elements onto two circles, inner circles for processes
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.gui.graph.Shape;
import org.vcell.sybil.gui.graph.nodes.NodeShapeReaction;
import org.vcell.sybil.gui.graph.nodes.NodeShapeReactionTransport;
import org.vcell.sybil.gui.graph.nodes.NodeShapeTransport;
import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.util.graphlayout.placer.Placer.CouldNotFindVacantSpotException;
import org.vcell.sybil.util.graphlayout.shuffler.RandomSortShuffler;
import org.vcell.sybil.util.graphlayout.shuffler.penalty.SquareTwoDegreePenalizer;
import org.vcell.sybil.util.graphlayout.tiling.OneCircleTiling;

public class DoubleCirclelizer implements Layouter {

	public static final SquareTwoDegreePenalizer<Shape> penalty =
		new SquareTwoDegreePenalizer<Shape>();
	protected RandomSortShuffler<Shape> shuffler = new RandomSortShuffler<Shape>();
	
	public boolean isProcessNodeShape(Shape shape) {
		return shape instanceof NodeShapeReaction || shape instanceof NodeShapeReactionTransport ||
		shape instanceof NodeShapeTransport;
	}
	
	public void applyToGraph(Graph graph) {
		Shape topShape = graph.topShape();
		Set<Shape> shapesOut = new HashSet<Shape>();
		Set<Shape> shapesIn = new HashSet<Shape>();
		Iterator<Shape> shapeIter = graph.shapeIter();
		while(shapeIter.hasNext()) {
			Shape shape = shapeIter.next();
			if(shape.locationIndependent() && shape.visibility().isVisible()) { 
				if(isProcessNodeShape(shape)) { shapesIn.add(shape); }
				else { shapesOut.add(shape); }
			}
		}
		int xMinOut = topShape.xMin();
		int yMinOut = topShape.yMin();
		int xMaxOut = topShape.xMax();
		int yMaxOut = topShape.yMax();
		int xMinIn = (3*xMinOut + xMaxOut)/4;
		int yMinIn = (3*yMinOut + yMaxOut)/4;
		int xMaxIn = (xMinOut + 3*xMaxOut)/4;
		int yMaxIn = (yMinOut + 3*yMaxOut)/4;
		OneCircleTiling<Shape> tilingOut = 
			new OneCircleTiling<Shape>(xMinOut, yMinOut, xMaxOut, yMaxOut, shapesOut.size());
		OneCircleTiling<Shape> tilingIn = 
			new OneCircleTiling<Shape>(xMinIn, yMinIn, xMaxIn, yMaxIn, shapesIn.size());
		for(Shape shape : shapesOut) {
			try { tilingOut.placer().place(shape, tilingOut); } 
			catch (CouldNotFindVacantSpotException e) { CatchUtil.handle(e); }
		}
		for(Shape shape : shapesIn) {
			try { tilingOut.placer().place(shape, tilingIn); } 
			catch (CouldNotFindVacantSpotException e) { CatchUtil.handle(e); }
		}
		shuffler.shuffle(tilingOut, penalty);
		shuffler.shuffle(tilingIn, penalty);
		graph.updateView();
	}

}
