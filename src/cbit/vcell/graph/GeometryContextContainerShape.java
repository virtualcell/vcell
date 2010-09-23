package cbit.vcell.graph;
/*
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 */
import java.awt.Dimension;
import java.awt.Graphics2D;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.LayoutException;
import cbit.gui.graph.Shape;
import cbit.vcell.mapping.GeometryContext;

public class GeometryContextContainerShape extends ContainerShape {

	GeometryContext geometryContext = null;
	GeometryContextStructureShape structureContainer = null;
	GeometryContextGeometryShape geometryContainer = null;

	public GeometryContextContainerShape(GraphModel graphModel, GeometryContext geoContext, 
			GeometryContextStructureShape structureContainer, 
			GeometryContextGeometryShape geometryContainer) {
		super(graphModel);
		this.structureContainer = structureContainer;
		this.geometryContainer = geometryContainer;
		this.geometryContext = geoContext;
		addChildShape(structureContainer);
		addChildShape(geometryContainer);
		setLabel("geoContext");
	}

	@Override
	public Object getModelObject() { return geometryContext; }

	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		Dimension geometryChildDim = geometryContainer.getPreferedSize(g);
		Dimension structureChildDim = structureContainer.getPreferedSize(g);
		Dimension newDim = 
			new Dimension(geometryChildDim.width + structureChildDim.width, 
					Math.max(geometryChildDim.height,structureChildDim.height));
		return newDim;
	}

	@Override
	public void refreshLayout() throws LayoutException {
		int currentX = 0;
		int currentY = 0;
		// position structureContainer shape
		structureContainer.getSpaceManager().setRelPos(currentX, currentY);
		currentX += structureContainer.getSpaceManager().getSize().width;
		// position subvolumeContainer shape
		geometryContainer.getSpaceManager().setRelPos(currentX, currentY);
		currentX += geometryContainer.getSpaceManager().getSize().width;
		for (Shape child : childShapeList) {
			child.refreshLayout();
		}	
	}

	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY ) {
		g.setColor(java.awt.Color.yellow);
		g.fillRect(absPosX, absPosY, getSpaceManager().getSize().width, getSpaceManager().getSize().height);
		g.setColor(forgroundColor);
		g.drawRect(absPosX, absPosY, getSpaceManager().getSize().width, getSpaceManager().getSize().height);
	}

	@Override
	public void resize(Graphics2D g, Dimension newSize) throws Exception {
		getSpaceManager().setSize(newSize);
		// try to make geometryContainer have full width and structureContainer have rest
		int geomNewWidth = Math.min(getSpaceManager().getSize().width - 10, geometryContainer.getPreferedSize(g).width);
		int structNewWidth = getSpaceManager().getSize().width - geomNewWidth - 1;
		structureContainer.resize(g, new Dimension(structNewWidth, 
				getSpaceManager().getSize().height - getLabelSize().height));
		geometryContainer.resize(g, new Dimension(geomNewWidth, 
				getSpaceManager().getSize().height - getLabelSize().height));
		refreshLayout();
	}
}