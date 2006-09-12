package cbit.vcell.model.render;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.gui.graph.*;
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