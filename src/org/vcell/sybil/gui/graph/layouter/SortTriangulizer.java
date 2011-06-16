/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
import org.vcell.sybil.util.graphlayout.shuffler.RandomSortShuffler;
import org.vcell.sybil.util.graphlayout.shuffler.penalty.SquareTwoDegreePenalizer;
import org.vcell.sybil.util.graphlayout.tiling.TriangularTiling;

public class SortTriangulizer implements Layouter {

	public static final SquareTwoDegreePenalizer<Shape> penalizer =
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
		TriangularTiling<Shape> tiling = new TriangularTiling<Shape>(topShape.xMin(), topShape.yMin(),
				topShape.xMax(), topShape.yMax(), shapes.size());
		for(Shape shape : shapes) {
			try { tiling.placer().place(shape, tiling); } 
			catch (CouldNotFindVacantSpotException e) { CatchUtil.handle(e); }
		}
		shuffler.shuffle(tiling, penalizer);
		graph.updateView();
	}

}
