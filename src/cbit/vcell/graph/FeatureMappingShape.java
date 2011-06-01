/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.graph;
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
