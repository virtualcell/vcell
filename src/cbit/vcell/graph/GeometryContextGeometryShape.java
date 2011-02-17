package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */

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