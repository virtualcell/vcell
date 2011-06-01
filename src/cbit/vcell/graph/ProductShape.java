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
import cbit.vcell.model.gui.*;
import cbit.vcell.model.*;
import javax.swing.*;
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
protected int getEndAttachment() {
	return ATTACH_RIGHT;
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	if (reactionParticipant.getStoichiometry()==1){
		setLabel("");
	}else{
		setLabel("("+reactionParticipant.getStoichiometry()+")");
	}
}
}
