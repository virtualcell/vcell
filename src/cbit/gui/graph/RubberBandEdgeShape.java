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
