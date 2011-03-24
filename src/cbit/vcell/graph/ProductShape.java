package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import cbit.gui.graph.GraphModel;
import cbit.vcell.model.Product;

public class ProductShape extends ReactionParticipantShape {

	public ProductShape(Product product, ReactionStepShape reactionStepShape, 
			SpeciesContextShape speciesContextShape, GraphModel graphModel) {
		super(product, reactionStepShape, speciesContextShape, graphModel);
	}

	@Override protected int getEndAttachment() { return ATTACH_RIGHT; }

	@Override
	public void refreshLabel() {
		if (reactionParticipant.getStoichiometry()==1){
			setLabel("");
		}else{
			setLabel("("+reactionParticipant.getStoichiometry()+")");
		}
	}
	
	public boolean isDirectedForward() { return false; }

}