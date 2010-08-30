package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import cbit.gui.graph.GraphModel;
import cbit.vcell.model.Catalyst;

public class CatalystShape extends ReactionParticipantShape {

	public CatalystShape(Catalyst catalyst, ReactionStepShape reactionStepShape, 
			SpeciesContextShape speciesContextShape, GraphModel graphModel) {
		super(catalyst, reactionStepShape, speciesContextShape, graphModel);
		defaultFG = java.awt.Color.gray;
		forgroundColor = defaultFG;
	}

	@Override public int getLineStyle() { return LINE_STYLE_DASHED; }
	
	@Override public void refreshLabel() { setLabel(""); }
	
}