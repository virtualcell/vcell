package org.vcell.ncbc.physics.component.gui;


import java.util.Enumeration;

import org.vcell.ncbc.physics.component.Connection;
import org.vcell.ncbc.physics.component.Connector;
import org.vcell.ncbc.physics.component.Device;
import org.vcell.ncbc.physics.component.Location;
import org.vcell.ncbc.physics.component.SurfaceLocation;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphEvent;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
/**
 * Insert the type's description here.
 * Creation date: (7/9/2003 4:31:04 PM)
 * @author: Jim Schaff
 */
public class PhysicalModelGraphModel extends GraphModel implements java.beans.PropertyChangeListener {
	private org.vcell.ncbc.physics.component.PhysicalModel fieldPhysicalModel = new org.vcell.ncbc.physics.component.PhysicalModel();
/**
 * ConstraintsGraphModel constructor comment.
 */
public PhysicalModelGraphModel() {
	super();
	addPropertyChangeListener(this);
}
/**
 * Gets the physicalModel property (ncbc_old.physics.component.PhysicalModel) value.
 * @return The physicalModel property value.
 * @see #setPhysicalModel
 */
public org.vcell.ncbc.physics.component.PhysicalModel getPhysicalModel() {
	return fieldPhysicalModel;
}
	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("physicalModel")){
		refreshAll();
	}
	if (evt.getSource() == getPhysicalModel()){
		refreshAll();
	}
	//if (evt.getSource() instanceof cbit.vcell.constraints.GeneralConstraint){
		//if (evt.getPropertyName().equals("expression")){
			//refreshAll();
		//}else{
			//fireGraphChanged();
		//}
	//}
	//if (evt.getSource() instanceof cbit.vcell.constraints.SimpleBounds){
		//fireGraphChanged();
	//}
}
/**
 * Insert the method's description here.
 * Creation date: (7/8/2003 9:11:57 AM)
 */
public void refreshAll() {
	//
	// 1) mark all shapes as dirty
	// 2) traverse model adding new shapes or cleaning those that already exist
	// 3) remove remaining dirty shapes
	//

	//
	//	mark all shapes as dirty
	//
	Enumeration enum_shapes = getShapes();
	while (enum_shapes.hasMoreElements()) {
		Shape shape = (Shape) enum_shapes.nextElement();
		shape.setDirty(true);
	}

	if (getPhysicalModel()==null){
		clearAllShapes();
		fireGraphChanged(new GraphEvent(this));
		return;
	}

	
	ContainerShape containerShape = (ContainerShape)getShapeFromModelObject(getPhysicalModel());
	if (containerShape==null){
		containerShape = new SimpleContainerShape(getPhysicalModel(),this);
		containerShape.setLabel("Physical Model");
		addShape(containerShape);
	}
	containerShape.refreshLabel();
	containerShape.setDirty(false);

	Device devices[] = getPhysicalModel().getDevices();
	Connection connections[] = getPhysicalModel().getConnections();
	Location locations[] = getPhysicalModel().getLocations();
	//
	// add nodes for Locations
	//
	for (int i = 0; i < locations.length; i++){
		Shape locationNode = getShapeFromModelObject(locations[i]);
		if (locationNode == null){
			locationNode = new LocationNode(locations[i],this);
			containerShape.addChildShape(locationNode);
			addShape(locationNode);
		}
		locationNode.refreshLabel();
		locationNode.setDirty(false);
	}
	//
	// add adjacency connections between LocationNodes (process only SurfaceLocations so no redundancies)
	//
	for (int i = 0; i < locations.length; i++){
		if (locations[i] instanceof SurfaceLocation){
			SurfaceLocation surfaceLocation = (SurfaceLocation)locations[i];
			LocationNode surfaceLocationNode = (LocationNode)getShapeFromModelObject(surfaceLocation);

			//
			// handle surfaceLocation's neighbor 0
			//
			{
			LocationNode adjacentVolumeNode0 = (LocationNode)getShapeFromModelObject(surfaceLocation.getAdjacentLocations()[0]);
			Object adjacencyModelObject0 = LocationAdjacencyEdge.createModelObject(surfaceLocationNode,adjacentVolumeNode0);
			LocationAdjacencyEdge adjacencyEdge0 = (LocationAdjacencyEdge)getShapeFromModelObject(adjacencyModelObject0);
			if (adjacencyEdge0 == null){
				adjacencyEdge0 = new LocationAdjacencyEdge(surfaceLocationNode,adjacentVolumeNode0,this);
				containerShape.addChildShape(adjacencyEdge0);
				addShape(adjacencyEdge0);
			}
			adjacencyEdge0.refreshLabel();
			adjacencyEdge0.setDirty(false);
			}
			
			//
			// handle surfaceLocation's neighbor 1
			//
			{
			LocationNode adjacentVolumeNode1 = (LocationNode)getShapeFromModelObject(surfaceLocation.getAdjacentLocations()[1]);
			Object adjacencyModelObject1 = LocationAdjacencyEdge.createModelObject(surfaceLocationNode,adjacentVolumeNode1);
			LocationAdjacencyEdge adjacencyEdge1 = (LocationAdjacencyEdge)getShapeFromModelObject(adjacencyModelObject1);
			if (adjacencyEdge1 == null){
				adjacencyEdge1 = new LocationAdjacencyEdge(surfaceLocationNode,adjacentVolumeNode1,this);
				containerShape.addChildShape(adjacencyEdge1);
				addShape(adjacencyEdge1);
			}
			adjacencyEdge1.refreshLabel();
			adjacencyEdge1.setDirty(false);
			}
		}
	}
	//
	// add nodes for Devices
	//
	for (int i = 0; i < devices.length; i++){
		Shape deviceShape = getShapeFromModelObject(devices[i]);
		if (deviceShape == null){
			if (devices[i] instanceof Device){ // duh... check for other types for specific graphical representations
				deviceShape = new DeviceNode(devices[i],this);
			}
			containerShape.addChildShape(deviceShape);
			addShape(deviceShape);
		}
		deviceShape.refreshLabel();
		deviceShape.setDirty(false);
	}
	//
	// add nodes for Connectors and edges for ConnectorAnchors
	//
	for (int i = 0; i < devices.length; i++){
		DeviceNode deviceNode = (DeviceNode)getShapeFromModelObject(devices[i]);
		Connector connectors[] = devices[i].getConnectors();
		for (int j = 0; j < connectors.length; j++){
			//
			// update or create connector node
			//
			ConnectorNode connectorNode = (ConnectorNode)getShapeFromModelObject(connectors[j]);
			if (connectorNode == null){
				connectorNode = new ConnectorNode(connectors[j],this);
				containerShape.addChildShape(connectorNode);
				addShape(connectorNode);
			}
			connectorNode.refreshLabel();
			connectorNode.setDirty(false);
connectorNode.setDirty(true);
			//
			// update or create connector anchor edge
			//
			ConnectorAnchorEdgeShape connectorAnchorEdge = (ConnectorAnchorEdgeShape)getShapeFromModelObject(ConnectorAnchorEdgeShape.createModelObjectString(connectors[j]));
			if (connectorAnchorEdge == null){
				connectorAnchorEdge = new ConnectorAnchorEdgeShape(deviceNode, connectorNode, this);
				containerShape.addChildShape(connectorAnchorEdge);
				addShape(connectorAnchorEdge);
			}
			connectorAnchorEdge.refreshLabel();
			connectorAnchorEdge.setDirty(false);
connectorAnchorEdge.setDirty(true);
		}
	}
	//
	// add edges for Connections
	//
	for (int i = 0; i < connections.length; i++){
		ConnectorEdge connectionEdge = (ConnectorEdge)getShapeFromModelObject(connections[i]);
		if (connectionEdge == null){
//ConnectorNode connectorNode1 = (ConnectorNode)getShapeFromModelObject(connections[i].getConnector1());
//ConnectorNode connectorNode2 = (ConnectorNode)getShapeFromModelObject(connections[i].getConnector2());
//connectionEdge = new ConnectorEdge(connectorNode1, connectorNode2, connections[i], this);
DeviceNode deviceNode1 = (DeviceNode)getShapeFromModelObject(connections[i].getConnector1().getDevice());
DeviceNode deviceNode2 = (DeviceNode)getShapeFromModelObject(connections[i].getConnector2().getDevice());
connectionEdge = new ConnectorEdge(deviceNode1, deviceNode2, connections[i], this);
			containerShape.addChildShape(connectionEdge);
			addShape(connectionEdge);
		}
		connectionEdge.refreshLabel();
		connectionEdge.setDirty(false);
	}
	//
	// add edges for Device Locations
	//
	for (int i = 0; i < devices.length; i++){
		DeviceNode deviceNode = (DeviceNode)getShapeFromModelObject(devices[i]);
		LocationNode locationNode = (LocationNode)getShapeFromModelObject(devices[i].getLocation());
		Object deviceLocationModelObject = DeviceLocationEdge.createModelObject(deviceNode,locationNode);
		DeviceLocationEdge deviceLocationEdge = (DeviceLocationEdge)getShapeFromModelObject(deviceLocationModelObject);
		if (deviceLocationEdge == null){
			deviceLocationEdge = new DeviceLocationEdge(deviceNode, locationNode, this);
			containerShape.addChildShape(deviceLocationEdge);
			addShape(deviceLocationEdge);
		}
		deviceLocationEdge.refreshLabel();
		deviceLocationEdge.setDirty(false);
//deviceLocationEdge.setDirty(true);
	}
	
	//
	//	remove all dirty shapes (enumerations aren't editable), so build list and delete from that.
	//
	enum_shapes = getShapes();
	java.util.Vector deleteList = new java.util.Vector();
	while (enum_shapes.hasMoreElements()) {
		Shape shape = (Shape) enum_shapes.nextElement();
		if (shape.isDirty()) {
			deleteList.add(shape);
		}
	}
	for (int i = 0; i < deleteList.size(); i++){
		removeShape((Shape)deleteList.elementAt(i));
	}
	
	fireGraphChanged(new GraphEvent(this));
}
/**
 * Sets the physicalModel property (ncbc_old.physics.component.PhysicalModel) value.
 * @param physicalModel The new value for the property.
 * @see #getPhysicalModel
 */
public void setPhysicalModel(org.vcell.ncbc.physics.component.PhysicalModel physicalModel) {
	org.vcell.ncbc.physics.component.PhysicalModel oldValue = fieldPhysicalModel;
	fieldPhysicalModel = physicalModel;
	firePropertyChange("physicalModel", oldValue, physicalModel);
}
}
