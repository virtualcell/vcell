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
