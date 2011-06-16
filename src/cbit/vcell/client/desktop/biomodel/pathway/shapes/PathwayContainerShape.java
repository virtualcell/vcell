/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.Graphics2D;

import org.vcell.pathway.PathwayModel;

import cbit.gui.graph.ContainerShape;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;

public class PathwayContainerShape extends ContainerShape {
	private PathwayModel pathwayModel = null;

	public PathwayContainerShape(PathwayGraphModel graphModel, PathwayModel pathwayModel) {
		super(graphModel);
		this.pathwayModel = pathwayModel;
	}

	@Override
	public Object getModelObject() {
		return pathwayModel;
	}

	@Override
	public void paintSelf (Graphics2D g, int absPosX, int absPosY ) {
		g.setColor(backgroundColor);
		g.fillRect(absPosX, absPosY, spaceManager.getSize().width, spaceManager.getSize().height);
		g.setColor(forgroundColor);
		g.drawRect(absPosX,absPosY, spaceManager.getSize().width, spaceManager.getSize().height);
	}


	@Override
	public void notifySelected() {
		notifyUnselected();
	}
}
