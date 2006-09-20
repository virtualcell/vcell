package cbit.vcell.constraints.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.gui.graph.GraphModel;
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


/**
 * This method was created by a SmartGuide.
 * @param g java.awt.Graphics
 */
public void paint(java.awt.Graphics2D g2D, int parentOffsetX, int parentOffsetY) {
	cbit.vcell.constraints.AbstractConstraint constraint = (cbit.vcell.constraints.AbstractConstraint)getConstraintShape().getModelObject();
	if (!((ConstraintsGraphModel)graphModel).getConstraintContainerImpl().getActive(constraint)){
		forgroundColor = java.awt.Color.lightGray;
	}else{
		forgroundColor = java.awt.Color.black;
	}
	super.paint(g2D,parentOffsetX,parentOffsetY);
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel("");
}
}