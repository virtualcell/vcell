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
public class ProductShape extends ReactionParticipantShape {
	/**
	 * ProductShape constructor comment.
	 * @param label java.lang.String
	 * @param graphModel cbit.vcell.graph.GraphModel
	 */
	public ProductShape(Product product, ReactionStepShape reactionStepShape, 
			SpeciesContextShape speciesContextShape, GraphModel graphModel) {
		super(product, reactionStepShape, speciesContextShape, graphModel);
	}


	/**
	 * This method was created in VisualAge.
	 * @return int
	 */
	@Override
	protected int getEndAttachment() {
		return ATTACH_RIGHT;
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