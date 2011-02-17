package cbit.vcell.graph;
import java.awt.Graphics2D;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
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
	
	public void refreshLayoutSelf() {
		// do nothing
	}
	
	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY ) {
		g.setColor(java.awt.Color.yellow);
		g.fillRect(absPosX, absPosY, getSpaceManager().getSize().width, getSpaceManager().getSize().height);
		g.setColor(forgroundColor);
		g.drawRect(absPosX, absPosY, getSpaceManager().getSize().width, getSpaceManager().getSize().height);
	}
	
	public GeometryContextGeometryShape getGeometryContainer() { return geometryContainer; }
	public GeometryContextStructureShape getStructureContainer() { return structureContainer; }
	
}