package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.gui.graph.*;
import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class ReactantShape extends ReactionParticipantShape {
	/**
	 * ReactantShape constructor comment.
	 * @param label java.lang.String
	 * @param graphModel cbit.vcell.graph.GraphModel
	 */
	public ReactantShape(Reactant reactant, ReactionStepShape reactionStepShape, 
			SpeciesContextShape speciesContextShape, GraphModel graphModel) {
		super(reactant, reactionStepShape, speciesContextShape, graphModel);
	}


	/**
	 * This method was created in VisualAge.
	 * @return int
	 */
	@Override
	protected int getEndAttachment() {
		return ATTACH_LEFT;
	}


	/**
	 * This method was created in VisualAge.
	 */
	@Override
	public void refreshLabel() {
		if (reactionParticipant.getStoichiometry()==1){
			setLabel("");
		}else{
			setLabel("("+reactionParticipant.getStoichiometry()+")");
		}
	}
}