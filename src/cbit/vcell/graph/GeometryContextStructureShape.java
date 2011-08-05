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
import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.vcell.model.Model;

public class GeometryContextStructureShape extends ContainerShape {
	Model model = null;

	public GeometryContextStructureShape(GraphModel graphModel, Model model) {
		super(graphModel);
		this.model = model;
		defaultBG = java.awt.Color.lightGray;
		backgroundColor = defaultBG;
		setLabel("Physiology (structures)");
	}


	@Override
	public Object getModelObject() {
		return model;
	}


	public void refreshLayoutSelf() {
		if (getSpaceManager().getSize().width <= getLabelSize().width || 
				getSpaceManager().getSize().height <= getLabelSize().height) {
		}
		// this is like a row/column layout (1 row)
		int centerX = getSpaceManager().getSize().width / 2;
		int centerY = getSpaceManager().getSize().height / 2;
		// position label
		labelPos.x = centerX - getLabelSize().width / 2; 
		labelPos.y = centerY - getLabelSize().height / 2;
		
	}
	
}
