/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import cbit.gui.graph.GraphEvent;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.graph.FeatureShape;
import cbit.vcell.graph.GeometryClassLegendShape;
import cbit.vcell.graph.GeometryContextContainerShape;
import cbit.vcell.graph.GeometryContextGeometryShape;
import cbit.vcell.graph.GeometryContextStructureShape;
import cbit.vcell.graph.StructureMappingShape;
import cbit.vcell.graph.StructureMappingStructureShape;
import cbit.vcell.graph.StructureShape;
import cbit.vcell.graph.SubVolumeContainerShape;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.Structure;

public class StructureMappingCartoon extends GraphModel implements PropertyChangeListener {
	private SimulationContext simulationContext = null;
	//private GeometryContext geometryContext = null;
	private SubVolumeContainerShape subVolumeContainerShape = null;

	public GeometryContext getGeometryContext() {
		if (getSimulationContext() != null){
			return getSimulationContext().getGeometryContext();
		} else {
			return null;
		}
	}

	private SimulationContext getSimulationContext() { return simulationContext; }

	public void propertyChange(PropertyChangeEvent event) {
		try {
			refreshAll();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	@Override
	public void refreshAll() {
		{
			if (getGeometryContext() == null || getGeometryContext().getGeometry() == null) {
				return;
			}
			GeometryClass[] geometryClasses = getGeometryContext().getGeometry().getGeometryClasses();
			if (geometryClasses == null) {
				return;
			}
			for (int i=0;i<geometryClasses.length;i++){
				Shape testShape = getShapeFromModelObject(geometryClasses[i]);
				if(testShape instanceof GeometryClassLegendShape){
					geometryClasses[i].removePropertyChangeListener((GeometryClassLegendShape)testShape);
				}
			}	
		}
		clearAllShapes();
		if (getSimulationContext() == null){
			fireGraphChanged(new GraphEvent(this));
			return;
		}
		GeometryContextGeometryShape geometryShape = 
			new GeometryContextGeometryShape(this, getGeometryContext().getGeometry());
		GeometryContextStructureShape structureContainerShape = 
			new GeometryContextStructureShape(this, getGeometryContext().getModel());
		GeometryContextContainerShape containerShape = 
			new GeometryContextContainerShape(this, getGeometryContext(), structureContainerShape, geometryShape);
		addShape(containerShape);
		addShape(geometryShape);
		addShape(structureContainerShape);
		getGeometryContext().removePropertyChangeListener(this);
		getGeometryContext().addPropertyChangeListener(this);
		// create all StructureShapes
		Structure structures[] = getGeometryContext().getModel().getStructures();
		for (int i=0; i<structures.length; i++){
			StructureMappingStructureShape smShape = 
					new StructureMappingStructureShape((Structure)structures[i], 
							getGeometryContext().getModel(), this);
			addShape(smShape);
			structureContainerShape.addChildShape(smShape);
			structures[i].removePropertyChangeListener(this);
			structures[i].addPropertyChangeListener(this);
		}	
		// create all SubvolumeLegendShapes (for legend)
		GeometryClass[] geometryClasses = getGeometryContext().getGeometry().getGeometryClasses();
		for (int i=0;i<geometryClasses.length;i++){
			GeometryClassLegendShape geometryClassLegendShape = 
				new GeometryClassLegendShape(geometryClasses[i], getGeometryContext().getGeometry(), this, 10);
			geometryClasses[i].addPropertyChangeListener(geometryClassLegendShape);
			addShape(geometryClassLegendShape);
			geometryShape.addChildShape(geometryClassLegendShape);
		}	
		if((subVolumeContainerShape == null) || 
				(subVolumeContainerShape.getModelObject() != getGeometryContext().getGeometry())){
			subVolumeContainerShape = 
				new SubVolumeContainerShape(getGeometryContext().getGeometry(), this);			
		}
		subVolumeContainerShape.removeAllChildren();
		subVolumeContainerShape.setBrightImage(getGeometryContext().getGeometry().getGeometrySpec().getThumbnailImage().getCurrentValue());
		addShape(subVolumeContainerShape);
		geometryShape.addChildShape(subVolumeContainerShape);
		StructureMapping structureMappings[] = getGeometryContext().getStructureMappings();
		for (int i=0;i<structureMappings.length;i++){
			StructureMapping structureMapping = structureMappings[i];
			structureMapping.removePropertyChangeListener(this);
			structureMapping.addPropertyChangeListener(this);
			if (structureMapping.getGeometryClass()!=null){
				StructureShape sShape = 
					(StructureShape) getShapeFromModelObject(structureMapping.getStructure());
				GeometryClassLegendShape geometryClassLegendShape = 
					(GeometryClassLegendShape) getShapeFromModelObject(structureMapping.getGeometryClass());
				StructureMappingShape smShape = 
					new StructureMappingShape(structureMapping,sShape, geometryClassLegendShape, this);
				addShape(smShape);
				containerShape.addChildShape(smShape);
			}
		}
//		// assign children to shapes according to heirarchy in Model
//		int nullParentCount=0;
//		Collection<Shape> shapes = getShapes();
//		for(Shape shape : shapes) {
//			// for each featureShape, find corresponding featureShape
//			if (shape instanceof StructureShape){
//				StructureShape fs = (FeatureShape)shape;
//				if(!structureContainerShape.contains(fs)) {
//					structureContainerShape.addChildShape(fs);
//				}
//				nullParentCount++;
//			}	
//		}	
		fireGraphChanged(new GraphEvent(this));
	}

	public void setSimulationContext(SimulationContext aSimulationContext) 
	throws GeometryException, Exception {
		if(this.simulationContext != null) {
			this.simulationContext.getGeometryContext().removePropertyChangeListener(this);
			this.simulationContext.getGeometry().removePropertyChangeListener(this);
			this.simulationContext.getGeometry().getGeometrySpec().removePropertyChangeListener(this);
			if (simulationContext.getGeometry().getGeometrySurfaceDescription() != null) {
				simulationContext.getGeometry().getGeometrySurfaceDescription().removePropertyChangeListener(this);
			}
			this.simulationContext.removePropertyChangeListener(this);
			StructureMapping oldStructureMappings[] = 
				simulationContext.getGeometryContext().getStructureMappings();
			for (int i=0;i<oldStructureMappings.length;i++){
				oldStructureMappings[i].removePropertyChangeListener(this);
			}
		}
		this.simulationContext = aSimulationContext;
		if (this.simulationContext != null){
			this.simulationContext.getGeometryContext().addPropertyChangeListener(this);
			this.simulationContext.getGeometry().addPropertyChangeListener(this);
			this.simulationContext.getGeometry().getGeometrySpec().addPropertyChangeListener(this);
			if (simulationContext.getGeometry().getGeometrySurfaceDescription() != null) {
				simulationContext.getGeometry().getGeometrySurfaceDescription().addPropertyChangeListener(this);
			}
			this.simulationContext.addPropertyChangeListener(this);
			StructureMapping newStructureMappings[] = 
				simulationContext.getGeometryContext().getStructureMappings();
			for (int i=0;i<newStructureMappings.length;i++){
				newStructureMappings[i].addPropertyChangeListener(this);
			}
		}
		refreshAll();
	}
}
