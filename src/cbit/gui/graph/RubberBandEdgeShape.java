package cbit.gui.graph;

/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
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