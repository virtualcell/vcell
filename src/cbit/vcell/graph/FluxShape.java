package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import cbit.gui.graph.GraphModel;
import cbit.vcell.model.Flux;
import cbit.vcell.model.Membrane;

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
	
}