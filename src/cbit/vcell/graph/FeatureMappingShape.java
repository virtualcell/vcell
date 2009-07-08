package cbit.vcell.graph;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.gui.graph.*;
import cbit.vcell.mapping.*;
/**
 * This type was created in VisualAge.
 */
public class FeatureMappingShape extends EdgeShape {
	protected FeatureMapping featureMapping = null;

/**
 * ReactionParticipantShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public FeatureMappingShape(FeatureMapping featureMapping, FeatureShape featureShape,
	                            SubvolumeLegendShape subvolumeShape, GraphModel graphModel) {
	super(featureShape, subvolumeShape, graphModel);
	this.featureMapping = featureMapping;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ReactionStepShape
 */
public FeatureShape getFeatureShape() {
	return (FeatureShape)startShape;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getModelObject() {
	return featureMapping;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.graph.ReactionStepShape
 */
public SubvolumeLegendShape getSubvolumeLegendShape() {
	return (SubvolumeLegendShape)endShape;
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel("");
}
}