package cbit.vcell.graph;

/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */

import cbit.gui.graph.GraphModel;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;

public class StructureMappingStructureShape extends StructureShape {

	public StructureMappingStructureShape(Structure structure, Model model, GraphModel graphModel) {
		super(structure, model, graphModel);
	}

	public void refreshLayoutSelf() {
		int centerX = getSpaceManager().getSize().width/2;
		int currentY = labelSize.height;
		labelPos.x = centerX - labelSize.width/2; labelPos.y = currentY;
	}
		
}