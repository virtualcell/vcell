package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import cbit.gui.graph.GraphModel;
import cbit.vcell.model.Reactant;

public class ReactantShape extends ReactionParticipantShape {

	public ReactantShape(Reactant reactant, ReactionStepShape reactionStepShape, 
			SpeciesContextShape speciesContextShape, GraphModel graphModel) {
		super(reactant, reactionStepShape, speciesContextShape, graphModel);
	}

	@Override protected int getEndAttachment() { return ATTACH_LEFT; }
	
	@Override public void refreshLabel() {
		if (reactionParticipant.getStoichiometry()==1){
			setLabel("");
		} else {
			setLabel("(" + reactionParticipant.getStoichiometry() + ")");
		}
	}
}