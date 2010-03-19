package org.vcell.sybil.gui.graph.layouter;

/*   Triangulizer  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Arrange graph elements on a triangular lattice
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.gui.graph.Shape;
import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.util.graphlayout.placer.Placer.CouldNotFindVacantSpotException;
import org.vcell.sybil.util.graphlayout.shuffler.RandomShuffler;
import org.vcell.sybil.util.graphlayout.shuffler.penalty.ZeroPenalizer;
import org.vcell.sybil.util.graphlayout.tiling.TriangularTiling;

public class Triangulizer implements Layouter {

	public static final ZeroPenalizer<Shape> zeroPenalizer =
		new ZeroPenalizer<Shape>();
	protected RandomShuffler<Shape> shuffler = new RandomShuffler<Shape>();
	
	public void applyToGraph(Graph graph) {
		Shape topShape = graph.topShape();
		Set<Shape> shapes = new HashSet<Shape>();
		Iterator<Shape> shapeIter = graph.shapeIter();
		while(shapeIter.hasNext()) {
			Shape shape = shapeIter.next();
			if(shape.locationIndependent() && shape.visibility().isVisible()) { shapes.add(shape); }
		}
		TriangularTiling<Shape> tiling = new TriangularTiling<Shape>(topShape.xMin(), topShape.yMin(),
				topShape.xMax(), topShape.yMax(), shapes.size());
		for(Shape shape : shapes) {
			try { tiling.placer().place(shape, tiling); } 
			catch (CouldNotFindVacantSpotException e) { CatchUtil.handle(e); }
		}
		shuffler.shuffle(tiling, zeroPenalizer);
		graph.updateView();
	}

}
