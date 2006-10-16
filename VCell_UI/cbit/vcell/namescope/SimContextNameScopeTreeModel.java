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

import cbit.vcell.mapping.gui.SpeciesContextSpecParameterTableModel;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Structure;
import cbit.vcell.model.gui.ModelParameterTableModel;
import cbit.vcell.modelapp.MembraneMapping;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.SpeciesContextSpec;
import cbit.vcell.modelapp.StructureMapping;
import cbit.vcell.model.Model;
import cbit.gui.JTableFixed;
import cbit.vcell.model.Parameter;
import java.util.Vector;

import org.vcell.expression.ui.ScopedExpression;
import org.vcell.expression.ui.ScopedExpressionTableCellRenderer;


import cbit.vcell.desktop.BioModelNode;
import cbit.util.BeanUtils;
/**
 * Insert the type's description here.
 * Creation date: (5/6/2004 10:49:26 AM)
 * @author: Anuradha Lakshminarayana
 */
public class SimContextNameScopeTreeModel extends javax.swing.tree.DefaultTreeModel {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.modelapp.SimulationContext fieldSimulationContext = null;
	private java.util.Hashtable fieldTableHash = null;
	private Boolean bCalculateVolt = Boolean.FALSE;
/**
 * SimContextNameScopeTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public SimContextNameScopeTreeModel() {
	super(new BioModelNode("empty",false),true);
	fieldTableHash = new java.util.Hashtable();
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
 * Creation date: (4/12/2004 3:49:36 PM)
 */
private BioModelNode createBaseTree() {
	if (getSimulationContext() == null) {
		return null;
	}
	
	BioModelNode rootNode = new BioModelNode(getSimulationContext(), true);
	fieldTableHash.clear();

	rootNode.add(createPhysiologySubTree());
	rootNode.add(createStructuralMappingsSubTree());
	rootNode.add(createMolecularMappingsSubTree());

	System.out.println("Finished constructing the tree!");
	return rootNode;
}
/**
 * Insert the method's description here.
 * Creation date: (4/12/2004 3:49:36 PM)
 */
private BioModelNode createMolecularMappingsSubTree() {
	BioModelNode molMapingsNode = new BioModelNode("Molecular Mappings", true);

	SpeciesContextSpec[] speciesContextSpecs = getSimulationContext().getReactionContext().getSpeciesContextSpecs();
	if (speciesContextSpecs.length > 0) {
		for (int i = 0; i < speciesContextSpecs.length; i++){
			BioModelNode speciesContextNode = new BioModelNode(speciesContextSpecs[i], true);
			Parameter[] params = speciesContextSpecs[i].getParameters();
			//
			// Create table each for reactionStep kinetics and store in tableHash
			//
			JTableFixed aJTable = new JTableFixed();
			aJTable.setDefaultRenderer(ScopedExpression.class, new ScopedExpressionTableCellRenderer());
			aJTable.setDefaultRenderer(String.class, new ScopedExpressionTableCellRenderer());
			SpeciesContextSpecParameterTableModel scsParamTableModel = new SpeciesContextSpecParameterTableModel();
			scsParamTableModel.setSpeciesContextSpec(speciesContextSpecs[i]);
			aJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			aJTable.setModel(scsParamTableModel);
			fieldTableHash.put(params, aJTable);

			BioModelNode paramsNode = null;
			if (params != null && params.length > 0) {
				paramsNode = new BioModelNode(params, false);
			}
			if (paramsNode != null) {
				speciesContextNode.add(paramsNode);
			}
			if (speciesContextNode != null) {
				molMapingsNode.add(speciesContextNode);
			}		
		}
	}
		
	return molMapingsNode;
}
/**
 * Insert the method's description here.
 * Creation date: (4/12/2004 3:49:36 PM)
 */
private void createParamTable(Parameter[] params, StructureMapping structMapping, boolean bGeoMappings) {

	//
	// Create a table for the structural mappings params and store in Hashtable
	//
	if (params != null && params.length > 0) {
		JTableFixed aJTable = new JTableFixed();
		aJTable.setDefaultRenderer(ScopedExpression.class, new ScopedExpressionTableCellRenderer());
		aJTable.setDefaultRenderer(String.class, new ScopedExpressionTableCellRenderer());
		
		StructureMappingParameterTableModel smParamTableModel = new StructureMappingParameterTableModel();
		smParamTableModel.setStructureMapping(structMapping);
		smParamTableModel.setBGeoMappingParams(bGeoMappings);
		aJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		aJTable.setModel(smParamTableModel);
		fieldTableHash.put(params, aJTable);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/12/2004 3:49:36 PM)
 */
private BioModelNode createPhysiologySubTree() {
	BioModelNode physiologyNode = new BioModelNode("Physiology", true);

	Model model = getSimulationContext().getModel();
	Model.ModelParameter[] modelParams = model.getModelParameters();

	BioModelNode paramStrNode = new cbit.vcell.desktop.BioModelNode("Model Parameters", true);
	if (modelParams.length > 0) {
		BioModelNode parameterNode = new BioModelNode(modelParams, false);
		paramStrNode.add(parameterNode);
		//
		// Create a table for these model params and store in Hashtable
		//
		JTableFixed aJTable = new JTableFixed();
		aJTable.setDefaultRenderer(ScopedExpression.class,new ScopedExpressionTableCellRenderer());
		aJTable.setDefaultRenderer(String.class, new ScopedExpressionTableCellRenderer());
		aJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		ModelParameterTableModel modelParamTableModel = new ModelParameterTableModel();
		modelParamTableModel.setModel(model);
		aJTable.setModel(modelParamTableModel);		
		fieldTableHash.put(modelParams, aJTable);
	}
	physiologyNode.add(paramStrNode);

	BioModelNode reactionParamStrNode = new cbit.vcell.desktop.BioModelNode("Reaction Parameters", true);
	for (int i = 0; i < model.getStructures().length; i++){
		Structure structure = model.getStructures(i);
		reactionParamStrNode.add(createReactionParamSubTree(structure));
	}

	physiologyNode.add(reactionParamStrNode);
	return physiologyNode;
}
/**
 * Insert the method's description here.
 * Creation date: (4/12/2004 3:49:36 PM)
 */
private BioModelNode createReactionParamSubTree(Structure structure) {
	BioModelNode structureNode = new BioModelNode(structure, true);

	// Get reactions in structure  ....
	ReactionStep[] reactionSteps = getSimulationContext().getModel().getReactionSteps();

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
				JTableFixed aJTable = new JTableFixed();
				aJTable.setDefaultRenderer(ScopedExpression.class, new ScopedExpressionTableCellRenderer());
				aJTable.setDefaultRenderer(String.class, new ScopedExpressionTableCellRenderer());
				cbit.vcell.model.gui.ParameterTableModel paramTableModel = new cbit.vcell.model.gui.ParameterTableModel();
				paramTableModel.setKinetics(kinetics);
				aJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
				aJTable.setModel(paramTableModel);
				fieldTableHash.put(reactionSteps[i], aJTable);
			}
		}
	}
	
	return structureNode;
}
/**
 * Insert the method's description here.
 * Creation date: (4/12/2004 3:49:36 PM)
 */
private BioModelNode createStructuralMappingsSubTree() {
	BioModelNode structMappingsNode = new BioModelNode("Structural Mappings", true);

	BioModelNode geoMappingsStrNode = new BioModelNode("Geometry Mapping", true);
	BioModelNode membranePotentialOptsStrNode = new BioModelNode("Membrane Potential Options", true);
	
	StructureMapping[] structureMappings = getSimulationContext().getGeometryContext().getStructureMappings();

	if (structureMappings.length > 0) {
		Vector geoMappingParamsVector = new Vector();
		Vector membranePotentialOptsParamsVector = new Vector();
		for (int i = 0; i < structureMappings.length; i++){
			BioModelNode structureMapNode = new BioModelNode(structureMappings[i], true);
			StructureMapping.StructureMappingParameter[] strucMappingParams = structureMappings[i].getParameters();
			geoMappingParamsVector.clear();
			for (int j = 0; j < strucMappingParams.length; j++) {
				if ( (strucMappingParams[j].getRole() == (StructureMapping.ROLE_SurfaceToVolumeRatio)) ||
					 (strucMappingParams[j].getRole() == (StructureMapping.ROLE_VolumeFraction)) ) {
					geoMappingParamsVector.addElement(strucMappingParams[j]);
				} 
			}
			StructureMapping.StructureMappingParameter[] geoMappingParams = (StructureMapping.StructureMappingParameter[])cbit.util.BeanUtils.getArray(geoMappingParamsVector, StructureMapping.StructureMappingParameter.class);
			BioModelNode geoMappingParamsNode = null;
			if (geoMappingParams != null && geoMappingParams.length > 0) {
				createParamTable(geoMappingParams, structureMappings[i], true);
				geoMappingParamsNode = new BioModelNode(geoMappingParams, false);
			}
			if (geoMappingParamsNode != null) {
				structureMapNode.add(geoMappingParamsNode);
			}
			if (structureMapNode != null) {
				geoMappingsStrNode.add(structureMapNode);
			}

		}
		for (int i = 0; i < structureMappings.length; i++){
			if (structureMappings[i] instanceof MembraneMapping) {
				BioModelNode structureMapNode = new BioModelNode(structureMappings[i], true);
				StructureMapping.StructureMappingParameter[] strucMappingParams = structureMappings[i].getParameters();
				membranePotentialOptsParamsVector.clear();
				for (int j = 0; j < strucMappingParams.length; j++) {
					if ( (strucMappingParams[j].getRole() == (StructureMapping.ROLE_InitialVoltage)) || 
						 (strucMappingParams[j].getRole() == (StructureMapping.ROLE_SpecificCapacitance)) ){
						membranePotentialOptsParamsVector.addElement(strucMappingParams[j]);
					} 
				}
				boolean bCalcVoltage = false;
				BioModelNode calcVoltageNode = null;
				// Only membrane mappings have CalculateVoltage parameter, so create the node only if
				// structure mapping is membrane mapping.
				bCalcVoltage = ((MembraneMapping)structureMappings[i]).getCalculateVoltage();
				bCalculateVolt = new Boolean(bCalcVoltage);
				calcVoltageNode = new BioModelNode(bCalculateVolt, false);
				
				StructureMapping.StructureMappingParameter[] membPotOptsParams = (StructureMapping.StructureMappingParameter[])BeanUtils.getArray(membranePotentialOptsParamsVector, StructureMapping.StructureMappingParameter.class);
				BioModelNode membPotOptsParamsNode = null;
				if (membPotOptsParams != null && membPotOptsParams.length > 0) {
					createParamTable(membPotOptsParams, structureMappings[i], false);
					membPotOptsParamsNode = new BioModelNode(membPotOptsParams, false);
				}
				if (membPotOptsParamsNode != null) {
					structureMapNode.add(membPotOptsParamsNode);
				}
				if (calcVoltageNode != null) {
					structureMapNode.add(calcVoltageNode);
				}
				if (structureMapNode != null) {
					membranePotentialOptsStrNode.add(structureMapNode);
				}
			}
		}
	}

	structMappingsNode.add(geoMappingsStrNode);
	structMappingsNode.add(membranePotentialOptsStrNode);
	
	return structMappingsNode;
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
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public cbit.vcell.modelapp.SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}
/**
 * Gets the tableHash property (java.util.Hashtable) value.
 * @return The tableHash property value.
 */
public java.util.Hashtable getTableHash() {
	return fieldTableHash;
}
/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
/**
 * Insert the method's description here.
 * Creation date: (4/12/2004 5:26:51 PM)
 */
public void refreshTree() {
	if (getSimulationContext() != null){
		setRoot(createBaseTree());
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
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(cbit.vcell.modelapp.SimulationContext simulationContext) {
	cbit.vcell.modelapp.SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);
}
}
