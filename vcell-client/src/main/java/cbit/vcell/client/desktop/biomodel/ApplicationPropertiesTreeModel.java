/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import org.vcell.util.document.BioModelChildSummary.MathType;

import cbit.vcell.desktop.Annotation;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.solver.Simulation;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class ApplicationPropertiesTreeModel extends javax.swing.tree.DefaultTreeModel {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SimulationContext simulationContext = null;
	private BioModelNode rootNode = null;
/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public ApplicationPropertiesTreeModel() {
	super(new BioModelNode("Select an application to show properties", true), true);
	rootNode = (BioModelNode)root;
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 2:41:43 PM)
 * @param bioModelNode cbit.vcell.desktop.BioModelNode
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
private void populateRootNode() {
	rootNode.removeAllChildren();
	if (simulationContext == null){
		rootNode.setUserObject("Select an application to show properties");
	} else {	
		rootNode.setUserObject(simulationContext);
			
		//add application type node
		MathType typeInfo = simulationContext.getMathType();
		
		BioModelNode appTypeNode = new BioModelNode(typeInfo,false);
		appTypeNode.setRenderHint("type","AppType");
		rootNode.add(appTypeNode);
	
		rootNode.add(new BioModelNode(simulationContext.getGeometry(),false));
		rootNode.add(new BioModelNode(simulationContext.getMathDescription()==null ? "math not generated" : "math generated",false));
	
		Simulation simArray[] = simulationContext.getSimulations();
		if (simArray!=null){
			for (Simulation sim : simArray){
				BioModelNode simNode = new BioModelNode(sim,true);
				simNode.add(new BioModelNode(new Annotation(sim.getDescription()),false));
				rootNode.add(simNode);
			}
		}
	}
	nodeStructureChanged(rootNode);	
}

/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}

private java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}

/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}

/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * Sets the bioModel property (cbit.vcell.biomodel.BioModel) value.
 * @param bioModel The new value for the property.
 * @see #getBioModel
 */
public void setSimulationContext(SimulationContext newValue) {
	if (simulationContext == newValue) {
		return;
	}
	simulationContext = newValue;
	populateRootNode();
}
}
