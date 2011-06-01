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
public class CatalystShape extends ReactionParticipantShape {
/**
 * CatalystShape constructor comment.
 * @param label java.lang.String
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public CatalystShape(Catalyst catalyst, ReactionStepShape reactionStepShape, 
	                 SpeciesContextShape speciesContextShape, GraphModel graphModel) {
	super(catalyst, reactionStepShape, speciesContextShape, graphModel);
	defaultFG = java.awt.Color.gray;
	forgroundColor = defaultFG;
}


/**
 * Insert the method's description here.
 * Creation date: (1/15/2004 9:25:15 PM)
 * @return int
 */
public int getLineStyle() {
	return LINE_STYLE_DASHED;
}


/**
 * This method was created in VisualAge.
 */
public void refreshLabel() {
	setLabel("");
}
}
