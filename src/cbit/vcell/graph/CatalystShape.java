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
