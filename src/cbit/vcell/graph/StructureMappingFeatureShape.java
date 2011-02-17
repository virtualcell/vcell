package cbit.vcell.graph;

/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */

import cbit.gui.graph.GraphModel;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Model;

public class StructureMappingFeatureShape extends FeatureShape {

	public StructureMappingFeatureShape(Feature feature, Model model, GraphModel graphModel) {
		super(feature, model, graphModel);
	}

	public void refreshLayoutSelf() {
		int centerX = getSpaceManager().getSize().width/2;
		int currentY = labelSize.height;
		labelPos.x = centerX - labelSize.width/2; labelPos.y = currentY;
	}
		
}