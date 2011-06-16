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
import cbit.gui.graph.GraphModel;
import cbit.vcell.model.Flux;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;

public class FluxShape extends ReactionParticipantShape {

	public FluxShape(Flux flux, ReactionStepShape reactionStepShape, 
			SpeciesContextShape speciesContextShape, GraphModel graphModel) {
		super(flux, reactionStepShape, speciesContextShape, graphModel);
	}


	@Override
	protected int getEndAttachment() {
		Flux flux = (Flux)getModelObject();
		Membrane membrane = (Membrane) flux.getReactionStep().getStructure();
		if(membrane.getOutsideFeature() == flux.getStructure()){
			return ATTACH_LEFT;
		} else {
			return ATTACH_RIGHT;
		}
	}

	@Override public void refreshLabel() { setLabel(""); }
	
	public boolean isDirectedForward() { 
		SpeciesContext speciesContext = getSpeciesContextShape().getSpeciesContext();
		ReactionStep reactionStep = getReactionStepShape().getReactionStep();
		return speciesContext.getStructure() == reactionStep.getStructure().getParentStructure();
	}

}
