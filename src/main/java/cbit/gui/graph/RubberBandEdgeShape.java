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

public final class RubberBandEdgeShape extends EdgeShape {

	public RubberBandEdgeShape(ElipseShape startShape, ElipseShape endShape,
			GraphModel graphModel) {
		super(startShape, endShape, graphModel);
		defaultFG = java.awt.Color.red;
		forgroundColor = defaultFG;
	}

	public RubberBandEdgeShape(java.awt.Point start, java.awt.Point end,
			GraphModel graphModel) {
		super(start, end, graphModel);
		defaultFG = java.awt.Color.red;
		forgroundColor = defaultFG;
	}

	@Override
	public Shape getEndShape() {
		return endShape;
	}

	@Override
	public Object getModelObject() {
		return null;
	}

	@Override
	public Shape getStartShape() {
		return startShape;
	}

	@Override
	public void refreshLabel() {
	}

}
