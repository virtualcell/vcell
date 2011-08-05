/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.constraints.gui;
/**
 * This type was created in VisualAge.
 */
public class ConstraintDependencyEdgeShape extends cbit.gui.graph.EdgeShape {
	private Object modelObject = null;

/**
 * ReactionParticipantShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public ConstraintDependencyEdgeShape(BoundsNode boundsNodeShape, ConstraintVarNode varNodeShape, ConstraintsGraphModel graphModel) {
	super(boundsNodeShape, varNodeShape, graphModel);
	modelObject = new String(boundsNodeShape.getModelObject().toString()+"_to_"+varNodeShape.getModelObject().toString());
	refreshLabel();
}


/**
 * ReactionParticipantShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public ConstraintDependencyEdgeShape(GeneralConstraintNode generalConstraintShape, ConstraintVarNode varNodeShape, ConstraintsGraphModel graphModel) {
	super(generalConstraintShape, varNodeShape, graphModel);
	modelObject = new String(generalConstraintShape.getModelObject().toString()+"_to_"+varNodeShape.getModelObject().toString());
	refreshLabel();
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ReactionStepShape
 */
public cbit.gui.graph.ElipseShape getConstraintShape() {
	return (cbit.gui.graph.ElipseShape)startShape;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return modelObject;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ReactionStepShape
 */
public ConstraintVarNode getVarShape() {
	return (ConstraintVarNode)endShape;
}


public void paintSelf(java.awt.Graphics2D g2D, int absPosX, int absPosY) {
	cbit.vcell.constraints.AbstractConstraint constraint = (cbit.vcell.constraints.AbstractConstraint)getConstraintShape().getModelObject();
	if (!((ConstraintsGraphModel)graphModel).getConstraintContainerImpl().getActive(constraint)){
		forgroundColor = java.awt.Color.lightGray;
	}else{
		forgroundColor = java.awt.Color.black;
	}
	super.paintSelf(g2D,absPosX,absPosY);
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel("");
}
}
