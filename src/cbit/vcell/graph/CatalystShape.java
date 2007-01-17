package cbit.vcell.graph;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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