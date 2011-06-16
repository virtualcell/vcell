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
import cbit.vcell.geometry.Geometry;

public class GeometryContextGeometryShape extends ContainerShape {

	Geometry geometry = null;

	public GeometryContextGeometryShape(GraphModel graphModel, Geometry geometry) {
		super(graphModel);
		this.geometry = geometry;
		setLabel("Geometry (subdomains)");
	}

	@Override
	public Object getModelObject() {
		return geometry;
	}

	public void refreshLayoutSelf() {
		// do nothing
	}
	
}
