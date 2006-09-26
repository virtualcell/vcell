package cbit.gui.graph;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public final class RubberBandEdgeShape extends EdgeShape {
/**
 * RubberBandEdgeShape constructor comment.
 * @param startShape cbit.vcell.graph.ElipseShape
 * @param endShape cbit.vcell.graph.ElipseShape
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public RubberBandEdgeShape(ElipseShape startShape, ElipseShape endShape, GraphModel graphModel) {
	super(startShape, endShape, graphModel);
	defaultFG = java.awt.Color.red;
	forgroundColor = defaultFG;
}


/**
 * RubberBandEdgeShape constructor comment.
 * @param start java.awt.Point
 * @param end java.awt.Point
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public RubberBandEdgeShape(java.awt.Point start, java.awt.Point end, GraphModel graphModel) {
	super(start, end, graphModel);
	defaultFG = java.awt.Color.red;
	forgroundColor = defaultFG;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ElipseShape
 */
public ElipseShape getEndShape() {
	return endShape;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return null;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.Shape
 */
public ElipseShape getStartShape() {
	return startShape;
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
}
}