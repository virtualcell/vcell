package org.vcell.sybil.gui.graph.layouter;

/*   Circlelizer  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Arrange graph elements onto a circle
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.gui.graph.Shape;
import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.util.graphlayout.placer.Placer.CouldNotFindVacantSpotException;
import org.vcell.sybil.util.graphlayout.shuffler.RandomSortShuffler;
import org.vcell.sybil.util.graphlayout.shuffler.penalty.SquareTwoDegreePenalizer;
import org.vcell.sybil.util.graphlayout.tiling.OneCircleTiling;

public class Circlelizer implements Layouter {

	public static final SquareTwoDegreePenalizer<Shape> penalty =
		new SquareTwoDegreePenalizer<Shape>();
	protected RandomSortShuffler<Shape> shuffler = new RandomSortShuffler<Shape>();
	
	public void applyToGraph(Graph graph) {
		Shape topShape = graph.topShape();
		Set<Shape> shapes = new HashSet<Shape>();
		Iterator<Shape> shapeIter = graph.shapeIter();
		while(shapeIter.hasNext()) {
			Shape shape = shapeIter.next();
			if(shape.locationIndependent() && shape.visibility().isVisible()) { shapes.add(shape); }
		}
		OneCircleTiling<Shape> tiling = new OneCircleTiling<Shape>(topShape.xMin(), topShape.yMin(),
				topShape.xMax(), topShape.yMax(), shapes.size());
		for(Shape shape : shapes) {
			try { tiling.placer().place(shape, tiling); } 
			catch (CouldNotFindVacantSpotException e) { CatchUtil.handle(e); }
		}
		shuffler.shuffle(tiling, penalty);
		graph.updateView();
	}

}
