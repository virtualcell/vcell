/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cbit.vcell.namescope;

import cbit.util.DataAccessException;
import cbit.vcell.model.gui.ModelParameterTableModel;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.parser.ScopedExpression;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.Expression;
import cbit.gui.JTableFixed;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Structure;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.model.Model;
import cbit.vcell.model.Kinetics;
import java.beans.PropertyChangeListener;
/**
 * Insert the type's description here.
 * Creation date: (4/12/2004 2:55:19 PM)
 * @author: Anuradha Lakshminarayana
 */
public class ModelNameScopeDisplayTreeModel extends javax.swing.tree.DefaultTreeModel implements java.beans.PropertyChangeListener {
	private java.util.Hashtable tableHash = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.model.Model fieldModel = new cbit.vcell.model.Model("");
/**
 * NameScopeDisplayTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public ModelNameScopeDisplayTreeModel() {
	super(new BioModelNode("empty",false),true);
	tableHash = new java.util.Hashtable();
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}
/**
 * Insert the method's description here.
 * Creation date: (4/20/2004 5:12:13 PM)
 * @param reactStep cbit.vcell.model.ReactionStep
 */
private void addReactStepToHashtable(ReactionStep reactStep) {
	Kinetics kinetics = reactStep.getKinetics();
	JTableFixed aJTable = new JTableFixed();
	aJTable.setDefaultRenderer(ScopedExpression.class, new cbit.vcell.geometry.gui.GeometrySubVolumeTableCellRenderer());
	aJTable.setDefaultRenderer(String.class, new cbit.vcell.geometry.gui.GeometrySubVolumeTableCellRenderer());
	cbit.vcell.model.gui.ParameterTableModel paramTableModel = new cbit.vcell.model.gui.ParameterTableModel();
	paramTableModel.setKinetics(reactStep.getKinetics());
	aJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
	aJTable.setModel(paramTableModel);
	tableHash.put(reactStep, aJTable);
}
/**
 * Insert the method's description here.
 * Creation date: (4/12/2004 3:49:36 PM)
 */
private BioModelNode createBaseTree(Model model) {
	BioModelNode rootNode = new BioModelNode("Physiology", true);

	tableHash.clear();

	Model.ModelParameter[] modelParams = model.getModelParameters();

	BioModelNode paramStrNode = new cbit.vcell.desktop.BioModelNode("Model Parameters", true);
	if (modelParams.length > 0) {
		BioModelNode parameterNode = new BioModelNode(modelParams, false);
		paramStrNode.add(parameterNode);
		//
		// Create a table for these model params and store in Hashtable
		//
		JTableFixed aJTable = new JTableFixed();
		aJTable.setDefaultRenderer(ScopedExpression.class,new cbit.vcell.geometry.gui.GeometrySubVolumeTableCellRenderer());
		aJTable.setDefaultRenderer(String.class, new cbit.vcell.geometry.gui.GeometrySubVolumeTableCellRenderer());
		aJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		ModelParameterTableModel modelParamTableModel = new ModelParameterTableModel();
		modelParamTableModel.setModel(model);
		aJTable.setModel(modelParamTableModel);		
		tableHash.put(modelParams, aJTable);
	}
	rootNode.add(paramStrNode);

	BioModelNode reactionParamStrNode = new cbit.vcell.desktop.BioModelNode("Reaction Parameters", true);

	for (int i = 0; i < model.getStructures().length; i++){
		Structure structure = model.getStructures(i);
		reactionParamStrNode.add(createReactionParamSubTree(structure));
	}

	rootNode.add(reactionParamStrNode);
	
	return rootNode;
}
/**
 * Insert the method's description here.
 * Creation date: (4/12/2004 3:49:36 PM)
 */
private BioModelNode createReactionParamSubTree(Structure structure) {
	BioModelNode structureNode = new BioModelNode(structure, true);

	// Get reactions in structure  ....
	ReactionStep[] reactionSteps = getModel().getReactionSteps();

	if (reactionSteps.length > 0) {
		for (int i = 0; i < reactionSteps.length; i++){
			if (reactionSteps[i].getStructure().equals(structure)) {
				BioModelNode reactionStepNode = new BioModelNode(reactionSteps[i], true);
				cbit.vcell.model.Kinetics kinetics = reactionSteps[i].getKinetics();
				reactionStepNode.add(new BioModelNode(kinetics, false));
				structureNode.add(reactionStepNode);
				//
				// Create table each for reactionStep kinetics and store in tableHash
				//
				addReactStepToHashtable(reactionSteps[i]);
			}
		}
	}
	
	return structureNode;
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
public cbit.vcell.model.Model getModel() {
	return fieldModel;
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * Gets the tableHash property (java.util.Hashtable) value.
 * @return The tableHash property value.
 */
public java.util.Hashtable getTableHash() {
	return tableHash;
}
/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() instanceof ReactionStep) {
		tableHash.remove(evt.getSource());
		addReactStepToHashtable((ReactionStep)evt.getSource());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/12/2004 5:26:51 PM)
 */
public void refreshTree() {
	if (getModel()!=null){
		setRoot(createBaseTree(getModel()));
	}else{
		setRoot(new BioModelNode("empty"));
	}
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}
/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(cbit.vcell.model.Model model) {
	cbit.vcell.model.Model oldValue = fieldModel;
	fieldModel = model;
	firePropertyChange("model", oldValue, model);
}
}
