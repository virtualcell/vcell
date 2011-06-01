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
import cbit.gui.graph.Shape;
import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class FluxShape extends ReactionParticipantShape {
/**
 * CatalystShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public FluxShape(Flux flux, ReactionStepShape reactionStepShape, 
	                 SpeciesContextShape speciesContextShape, GraphModel graphModel) {
	super(flux, reactionStepShape, speciesContextShape, graphModel);
}


/**
 * This method was created in VisualAge.
 * @return int
 */
protected int getEndAttachment() {
	Flux flux = (Flux)getModelObject();
	Membrane membrane = (Membrane)flux.getReactionStep().getStructure();
	if (membrane.getOutsideFeature() == flux.getStructure()){
		return ATTACH_LEFT;
	}else{
		return ATTACH_RIGHT;
	}
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel("");
}
}
