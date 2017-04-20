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
