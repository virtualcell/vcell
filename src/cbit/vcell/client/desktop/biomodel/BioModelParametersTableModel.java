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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.gui.ScopedExpression;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.BioModelParametersPanel.ApplicationSelection;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel.TableUtil;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SpatialObject.SpatialQuantity;
import cbit.vcell.model.EditableSymbolTableEntry;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Kinetics.UnresolvedParameter;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class BioModelParametersTableModel extends BioModelEditorRightSideTableModel<EditableSymbolTableEntry> implements java.beans.PropertyChangeListener {
	public static final int COLUMN_PATH = 0;
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_DESCRIPTION = 2;
	public static final int COLUMN_EXPRESSION = 3;
	public static final int COLUMN_UNIT = 4;
	private static String LABELS[] = {"Defined In:", "Name", "Description", "Expression", "Units"};
	private boolean bReactions = true;
	private boolean bApplications = true;
	private boolean bGlobal = true;
	private boolean bConstants = true;
	private boolean bFunctions = true;
	private ApplicationSelection applicationSelection = null;
	
/**
 * ReactionSpecsTableModel constructor comment.
 */
public BioModelParametersTableModel(EditorScrollTable table) {
	super(table);
	setColumns(LABELS);
}

/**
 * Insert the method's description here.
 * Creation date: (2/24/01 12:24:35 AM)
 * @return java.lang.Class
 * @param column int
 */
public Class<?> getColumnClass(int col) {
	switch (col){
		case COLUMN_PATH:
			return NameScope.class;
		case COLUMN_NAME:
			return String.class;
		case COLUMN_DESCRIPTION:
			return String.class;
		case COLUMN_EXPRESSION:
			return ScopedExpression.class;
		case COLUMN_UNIT:
			return VCUnitDefinition.class;
	}
	return Object.class;
}

/**
 * Insert the method's description here.
 * Creation date: (9/23/2003 1:24:52 PM)
 * @return cbit.vcell.model.EditableSymbolTableEntry
 * @param row int
 */
protected List<EditableSymbolTableEntry> computeData() {
	ArrayList<EditableSymbolTableEntry> allEditableSymbolTableEntryList = new ArrayList<EditableSymbolTableEntry>();
	if (bioModel == null){
		return null;
	}
	if (bGlobal) {
		Map<String, SymbolTableEntry> entryMap = new HashMap<String, SymbolTableEntry>();
		bioModel.getModel().getEntries(entryMap);
		for (SymbolTableEntry ste : entryMap.values()){
			if (ste instanceof EditableSymbolTableEntry && !(ste instanceof ReservedSymbol)){
				allEditableSymbolTableEntryList.add((EditableSymbolTableEntry)ste);
			}
		}
	}
	if (bReactions) {
		for (ReactionStep reactionStep : bioModel.getModel().getReactionSteps()) {
			allEditableSymbolTableEntryList.addAll(Arrays.asList(reactionStep.getKinetics().getUnresolvedParameters()));
			allEditableSymbolTableEntryList.addAll(Arrays.asList(reactionStep.getKinetics().getKineticsParameters()));		
		}
		if(!bioModel.getModel().getRbmModelContainer().isEmpty()) {
			for(ReactionRule reactionRule : bioModel.getModel().getRbmModelContainer().getReactionRuleList()) {
				allEditableSymbolTableEntryList.addAll(Arrays.asList(reactionRule.getKineticLaw().getLocalParameters()));
				allEditableSymbolTableEntryList.addAll(Arrays.asList(reactionRule.getKineticLaw().getProxyParameters()));
				allEditableSymbolTableEntryList.addAll(Arrays.asList(reactionRule.getKineticLaw().getUnresolvedParameters()));
			}
		}
	}
	if (bApplications) {
		for (SimulationContext simContext : bioModel.getSimulationContexts()) {
			if (applicationSelection!=null && (applicationSelection.isAll() || applicationSelection.getSimulationContext()==simContext)){
				allEditableSymbolTableEntryList.addAll(getApplicationEditableSymbolTableEntryList(simContext));
			}
		}
	}
	boolean bSearchInactive = searchText == null || searchText.length() == 0;
	String lowerCaseSearchText = bSearchInactive ? null : searchText.toLowerCase();
	ArrayList<EditableSymbolTableEntry> parameterList = new ArrayList<EditableSymbolTableEntry>();
	for (EditableSymbolTableEntry parameter : allEditableSymbolTableEntryList) {
		boolean bNumeric = parameter.getExpression() == null || parameter.getExpression().isNumeric();
		if (bConstants && bNumeric || bFunctions && !bNumeric) {
			if (bSearchInactive
					|| parameter.getNameScope().getPathDescription().toLowerCase().contains(lowerCaseSearchText)
					|| parameter.getName().toLowerCase().contains(lowerCaseSearchText)
					|| parameter.getExpression() != null && parameter.getExpression().infix().toLowerCase().contains(lowerCaseSearchText)
					|| parameter.getDescription().toLowerCase().contains(lowerCaseSearchText)) {
				parameterList.add(parameter);
			}
		}
	}
	return parameterList;
}

private List<EditableSymbolTableEntry> getApplicationEditableSymbolTableEntryList(SimulationContext simulationContext) {	
	ArrayList<EditableSymbolTableEntry> parameterList = new ArrayList<EditableSymbolTableEntry>();	
	Map<String, SymbolTableEntry> entryMap = new HashMap<String, SymbolTableEntry>();
	simulationContext.getEntries(entryMap);
	for (SymbolTableEntry ste : entryMap.values()){
		if (ste instanceof EditableSymbolTableEntry && ste.getNameScope() == simulationContext.getNameScope()){
			parameterList.add((EditableSymbolTableEntry)ste);
		}
	}
	for (StructureMapping mapping : simulationContext.getGeometryContext().getStructureMappings()) {
		parameterList.addAll(mapping.computeApplicableParameterList());
	}
	for (SpeciesContextSpec spec : simulationContext.getReactionContext().getSpeciesContextSpecs()) {
		parameterList.addAll(spec.computeApplicableParameterList());
	}
	for (ElectricalStimulus elect : simulationContext.getElectricalStimuli()) {
		parameterList.addAll(Arrays.asList(elect.getParameters()));
	}	
//	for (SpatialObject sobj : simulationContext.getSpatialObjects()){
//		for (SpatialQuantity spatialQuantity : sobj.getSpatialQuantities()){
//			if (spatialQuantity.isEnabled()){
//				parameterList.add(spatialQuantity);
//			}
//		}
//	}
	return parameterList;
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
	try {
		EditableSymbolTableEntry parameter = getValueAt(row);
		switch (col){
			case COLUMN_PATH:
				return parameter.getNameScope();
			case COLUMN_NAME:
				return parameter.getName();
			case COLUMN_DESCRIPTION: 
				return parameter.getDescription();
			case COLUMN_EXPRESSION:{					
				if (parameter.getExpression() == null) {						
					return null;						
				} 
				boolean bValidateBinding = !(parameter instanceof KineticsParameter);
				return new ScopedExpression(parameter.getExpression(), parameter.getNameScope(), bValidateBinding, true, null);				
			}
			case COLUMN_UNIT:{
				return parameter.getUnitDefinition();				
			}			
		}
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
	return null;
}

public boolean isCellEditable(int row, int col) {
	EditableSymbolTableEntry parameter = getValueAt(row);
	switch (col) {
	case COLUMN_PATH:
		return false;
	case COLUMN_NAME:
		return parameter.isNameEditable();
	case COLUMN_DESCRIPTION:
		return parameter.isDescriptionEditable();
	case COLUMN_EXPRESSION:
		return parameter.isExpressionEditable();
	case COLUMN_UNIT:
		return parameter.isUnitEditable();
	}
	return false;
}

@Override
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	super.propertyChange(evt);	
	if (evt.getSource() instanceof EditableSymbolTableEntry) {
		int changeRow = getRowIndex((EditableSymbolTableEntry) evt.getSource());
		if (changeRow >= 0) {
			fireTableRowsUpdated(changeRow, changeRow);
		}
	} else {
		String propertyName = evt.getPropertyName();
		if (evt.getSource() == bioModel.getModel()) {
			if (propertyName.equals(Model.PROPERTY_NAME_MODEL_PARAMETERS)) {
				ModelParameter[] oldValue = (ModelParameter[])evt.getOldValue();
				if (oldValue!=null){
					for (EditableSymbolTableEntry parameter : oldValue) {
						parameter.removePropertyChangeListener(this);
					}
				}
				ModelParameter[] newValue = (ModelParameter[])evt.getNewValue();
				if (newValue!=null){
					for (EditableSymbolTableEntry parameter : newValue) {
						parameter.addPropertyChangeListener(this);
					}
				}
				refreshData();
			} else if (propertyName.equals(Model.PROPERTY_NAME_SPECIES_CONTEXTS)) {
				SpeciesContext[] oldValue = (SpeciesContext[])evt.getOldValue();
				if (oldValue!=null){
					for (SpeciesContext sc : oldValue) {
						sc.removePropertyChangeListener(this);
					}
				}
				SpeciesContext[] newValue = (SpeciesContext[])evt.getNewValue();
				if (newValue!=null){
					for (SpeciesContext sc : newValue) {
						sc.addPropertyChangeListener(this);
					}
				}
				refreshData();
			} else if (propertyName.equals(Model.PROPERTY_NAME_REACTION_STEPS)) {
				ReactionStep[] oldValue = (ReactionStep[])evt.getOldValue();
				if (oldValue!=null){
					for (ReactionStep reactionStep : oldValue){
						reactionStep.removePropertyChangeListener(this);
						reactionStep.getKinetics().removePropertyChangeListener(this);
						for (KineticsParameter kineticsEditableSymbolTableEntry : reactionStep.getKinetics().getKineticsParameters()) {
							kineticsEditableSymbolTableEntry.removePropertyChangeListener(this);
						}
						for (ProxyParameter proxyEditableSymbolTableEntry : reactionStep.getKinetics().getProxyParameters()) {
							proxyEditableSymbolTableEntry.removePropertyChangeListener(this);
						}
						for (UnresolvedParameter unresolvedEditableSymbolTableEntry: reactionStep.getKinetics().getUnresolvedParameters()) {
							unresolvedEditableSymbolTableEntry.removePropertyChangeListener(this);
						}
					}
				}
				ReactionStep[] newValue = (ReactionStep[])evt.getNewValue();
				if (newValue!=null){
					for (ReactionStep reactionStep : newValue){
						reactionStep.addPropertyChangeListener(this);
						reactionStep.getKinetics().addPropertyChangeListener(this);
						for (KineticsParameter kineticsEditableSymbolTableEntry : reactionStep.getKinetics().getKineticsParameters()) {
							kineticsEditableSymbolTableEntry.addPropertyChangeListener(this);
						}
						for (ProxyParameter proxyEditableSymbolTableEntry : reactionStep.getKinetics().getProxyParameters()) {
							proxyEditableSymbolTableEntry.addPropertyChangeListener(this);
						}
						for (UnresolvedParameter unresolvedEditableSymbolTableEntry: reactionStep.getKinetics().getUnresolvedParameters()) {
							unresolvedEditableSymbolTableEntry.addPropertyChangeListener(this);
						}
					}
				}
				refreshData();
			} else if (evt.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_REACTION_RULE_LIST)) {
				List<ReactionRule> oldValue = (List<ReactionRule>) evt.getOldValue();
				if (oldValue != null) {
					for (ReactionRule rs : oldValue) {
						rs.removePropertyChangeListener(this);
					}
				}
				List<ReactionRule> newValue = (List<ReactionRule>)  evt.getNewValue();
				if (newValue != null) {
					for (ReactionRule rs : newValue) {
						rs.addPropertyChangeListener(this);
					}
				}
				refreshData();
			}
		} else if (evt.getSource() == bioModel) {
			if (propertyName.equals(BioModel.PROPERTY_NAME_SIMULATION_CONTEXTS)) {
				SimulationContext[] oldValue = (SimulationContext[])evt.getOldValue();
				for (SimulationContext simulationContext : oldValue) {
					simulationContext.removePropertyChangeListener(this);
					simulationContext.getGeometryContext().removePropertyChangeListener(this);
					for (StructureMapping mapping : simulationContext.getGeometryContext().getStructureMappings()) {
						mapping.removePropertyChangeListener(this);
						for (EditableSymbolTableEntry parameter : mapping.getParameters()) {
							parameter.removePropertyChangeListener(this);
						}
					}
					simulationContext.getReactionContext().removePropertyChangeListener(this);
					for (SpeciesContextSpec spec : simulationContext.getReactionContext().getSpeciesContextSpecs()) {
						spec.removePropertyChangeListener(this);
						for (EditableSymbolTableEntry parameter : spec.getParameters()) {
							parameter.removePropertyChangeListener(this);
						}
					}
					for (ElectricalStimulus elect : simulationContext.getElectricalStimuli()) {
						elect.removePropertyChangeListener(this);
						for (EditableSymbolTableEntry parameter : elect.getParameters()) {
							parameter.removePropertyChangeListener(this);
						}
					}
				}
				SimulationContext[] newValue = (SimulationContext[])evt.getNewValue();
				for (SimulationContext simulationContext : newValue) {
					simulationContext.addPropertyChangeListener(this);
					simulationContext.getGeometryContext().addPropertyChangeListener(this);
					for (StructureMapping mapping : simulationContext.getGeometryContext().getStructureMappings()) {
						mapping.addPropertyChangeListener(this);
						for (EditableSymbolTableEntry parameter : mapping.getParameters()) {
							parameter.addPropertyChangeListener(this);
						}
					}
					simulationContext.getReactionContext().addPropertyChangeListener(this);
					for (SpeciesContextSpec spec : simulationContext.getReactionContext().getSpeciesContextSpecs()) {
						spec.addPropertyChangeListener(this);
						for (EditableSymbolTableEntry parameter : spec.getParameters()) {
							parameter.addPropertyChangeListener(this);
						}
					}
					for (ElectricalStimulus elect : simulationContext.getElectricalStimuli()) {
						elect.addPropertyChangeListener(this);
						for (EditableSymbolTableEntry parameter : elect.getParameters()) {
							parameter.addPropertyChangeListener(this);
						}
					}
				}
				refreshData();
			}
		} else if (evt.getSource() instanceof GeometryContext && evt.getPropertyName().equals(GeometryContext.PROPERTY_STRUCTURE_MAPPINGS)) {
			StructureMapping[] oldValue = (StructureMapping[]) evt.getOldValue();
			if (oldValue != null) {
				for (StructureMapping mapping : oldValue) {
					mapping.removePropertyChangeListener(this);
					for (EditableSymbolTableEntry parameter : mapping.getParameters()) {
						parameter.removePropertyChangeListener(this);
					}
				}
			}
			StructureMapping[] newValue = (StructureMapping[]) evt.getNewValue();
			if (newValue != null) {
				for (StructureMapping mapping : newValue) {
					mapping.addPropertyChangeListener(this);
					for (EditableSymbolTableEntry parameter : mapping.getParameters()) {
						parameter.addPropertyChangeListener(this);
					}
				}
			}
			refreshData();
		} else if (evt.getSource() instanceof ReactionStep && (evt.getPropertyName().equals(ReactionStep.PROPERTY_NAME_KINETICS))) {
			Kinetics oldValue = (Kinetics)evt.getOldValue();
			if (oldValue != null) {
				oldValue.removePropertyChangeListener(this);
				for (KineticsParameter kineticsEditableSymbolTableEntry : oldValue.getKineticsParameters()) {
					kineticsEditableSymbolTableEntry.removePropertyChangeListener(this);
				}
				for (ProxyParameter proxyEditableSymbolTableEntry : oldValue.getProxyParameters()) {
					proxyEditableSymbolTableEntry.removePropertyChangeListener(this);
				}
				for (UnresolvedParameter unresolvedEditableSymbolTableEntry: oldValue.getUnresolvedParameters()) {
					unresolvedEditableSymbolTableEntry.removePropertyChangeListener(this);
				}
			}
			Kinetics newValue = (Kinetics)evt.getNewValue();
			if (newValue != null) {
				newValue.addPropertyChangeListener(this);
				for (KineticsParameter kineticsEditableSymbolTableEntry : newValue.getKineticsParameters()) {
					kineticsEditableSymbolTableEntry.addPropertyChangeListener(this);
				}
				for (ProxyParameter proxyEditableSymbolTableEntry : newValue.getProxyParameters()) {
					proxyEditableSymbolTableEntry.addPropertyChangeListener(this);
				}
				for (UnresolvedParameter unresolvedEditableSymbolTableEntry: newValue.getUnresolvedParameters()) {
					unresolvedEditableSymbolTableEntry.addPropertyChangeListener(this);
				}
			}
			refreshData();
		} else if (evt.getSource() instanceof Kinetics && (evt.getPropertyName().equals(Kinetics.PROPERTY_NAME_KINETICS_PARAMETERS))) {
			EditableSymbolTableEntry oldValue[] = (EditableSymbolTableEntry[])evt.getOldValue();
			if (oldValue != null) {
				for (int i = 0; i < oldValue.length; i++){
					oldValue[i].removePropertyChangeListener(this);
				}
			}
			EditableSymbolTableEntry newValue[] = (EditableSymbolTableEntry[])evt.getNewValue();
			if (newValue != null) {
				for (int i = 0; i < newValue.length; i++){
					newValue[i].addPropertyChangeListener(this);
				}
			}
			refreshData();
//		} else if(evt.getSource() instanceof ReactionRuleEmbedded) {
//			ReactionRuleEmbedded reactionRule = (ReactionRuleEmbedded) evt.getSource();
//			int changeRow = getRowIndex(reactionRule);
//			if (changeRow >= 0) {
//				fireTableRowsUpdated(changeRow, changeRow);
//			}
		}
	}
}

public void setValueAt(Object value, int row, int col) {
	if (value == null) {
		return;
	}
	try {
		String inputValue = (String)value;
		inputValue = inputValue.trim();
		EditableSymbolTableEntry parameter = getValueAt(row);
		switch (col){
			case COLUMN_NAME:{
				if (inputValue.length() == 0) {
					return;
				}
				parameter.setName(inputValue);
				break;
			}
			case COLUMN_EXPRESSION:{
				String newExpressionString = inputValue;
				if (parameter instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
					SpeciesContextSpec.SpeciesContextSpecParameter scsParm = (SpeciesContextSpec.SpeciesContextSpecParameter)parameter;
					Expression newExp = null;
					if (newExpressionString == null || newExpressionString.trim().length() == 0) {
						if (scsParm.getRole() == SpeciesContextSpec.ROLE_InitialConcentration
								|| scsParm.getRole() == SpeciesContextSpec.ROLE_DiffusionRate
								|| scsParm.getRole() == SpeciesContextSpec.ROLE_InitialCount) {
							newExp = new Expression(0.0);
						}
					} else {
						newExp = new Expression(newExpressionString);
					}
					scsParm.setExpression(newExp);				
				} else if (parameter instanceof KineticsParameter){
					Expression exp1 = new Expression(inputValue);
					Kinetics kinetics = ((KineticsParameter) parameter).getKinetics();
					kinetics.setParameterValue((Kinetics.KineticsParameter)parameter, exp1);
				} else {
					Expression exp1 = new Expression(inputValue);
					exp1.bindExpression(parameter.getNameScope().getScopedSymbolTable());
					parameter.setExpression(exp1);
				}
				break;
			}
			case COLUMN_UNIT:{
				ModelUnitSystem modelUnitSystem = getModel().getUnitSystem();
				if (inputValue.length() == 0) {
					parameter.setUnitDefinition(modelUnitSystem.getInstance_TBD());
				} else {
					if (!parameter.getUnitDefinition().getSymbol().equals(inputValue)){
						parameter.setUnitDefinition(modelUnitSystem.getInstance(inputValue));
					}
				}
				break;
			}
		}
	} catch (Exception e){
		e.printStackTrace(System.out);
		DialogUtils.showErrorDialog(ownerTable, e.getMessage());
	}
}


  public Comparator<EditableSymbolTableEntry> getComparator(final int col, final boolean ascending) {
	  return new Comparator<EditableSymbolTableEntry>() {

		public int compare(EditableSymbolTableEntry parm1, EditableSymbolTableEntry parm2) {
			int scale = ascending ? 1 : -1;
			switch (col){
				case COLUMN_NAME:
					return scale * parm1.getName().compareToIgnoreCase(parm2.getName());
				case COLUMN_DESCRIPTION:
					return scale * parm1.getDescription().compareToIgnoreCase(parm2.getDescription());
				case COLUMN_EXPRESSION:
					return TableUtil.expressionCompare(parm1.getExpression(), parm2.getExpression(), ascending);
				case COLUMN_PATH:
					return scale * parm1.getNameScope().getPathDescription().compareToIgnoreCase(parm2.getNameScope().getPathDescription());
				case COLUMN_UNIT:
					String unit1 = parm1.getUnitDefinition() == null ? "" : parm1.getUnitDefinition().getSymbol();
					String unit2 = parm2.getUnitDefinition() == null ? "" : parm2.getUnitDefinition().getSymbol();
					return scale * unit1.compareToIgnoreCase(unit2);
			}
			return 0;
		}
	  };
  }

@Override
protected void bioModelChange(PropertyChangeEvent evt) {
	super.bioModelChange(evt);
	BioModel oldValue = (BioModel)evt.getOldValue();
	if (oldValue!=null){
		for (EditableSymbolTableEntry parameter : oldValue.getModel().getModelParameters()) {
			parameter.removePropertyChangeListener(this);
		}
		for (SpeciesContext sc : oldValue.getModel().getSpeciesContexts()) {
			sc.removePropertyChangeListener(this);
		}
		for (ReactionStep reactionStep : oldValue.getModel().getReactionSteps()){
			reactionStep.removePropertyChangeListener(this);
			Kinetics kinetics = reactionStep.getKinetics();
			kinetics.removePropertyChangeListener(this);
			for (KineticsParameter kineticsEditableSymbolTableEntry : kinetics.getKineticsParameters()) {
				kineticsEditableSymbolTableEntry.removePropertyChangeListener(this);
			}
			for (ProxyParameter proxyEditableSymbolTableEntry : kinetics.getProxyParameters()) {
				proxyEditableSymbolTableEntry.removePropertyChangeListener(this);
			}
			for (UnresolvedParameter unresolvedEditableSymbolTableEntry: kinetics.getUnresolvedParameters()) {
				unresolvedEditableSymbolTableEntry.removePropertyChangeListener(this);
			}
		}
		for (SimulationContext simulationContext : oldValue.getSimulationContexts()) {
			simulationContext.removePropertyChangeListener(this);
			simulationContext.getGeometryContext().removePropertyChangeListener(this);
			for (StructureMapping mapping : simulationContext.getGeometryContext().getStructureMappings()) {
				mapping.removePropertyChangeListener(this);
				for (EditableSymbolTableEntry parameter : mapping.getParameters()) {
					parameter.removePropertyChangeListener(this);
				}
			}
			simulationContext.getReactionContext().removePropertyChangeListener(this);
			for (SpeciesContextSpec spec : simulationContext.getReactionContext().getSpeciesContextSpecs()) {
				spec.removePropertyChangeListener(this);
				for (EditableSymbolTableEntry parameter : spec.getParameters()) {
					parameter.removePropertyChangeListener(this);
				}
			}
			for (ElectricalStimulus elect : simulationContext.getElectricalStimuli()) {
				elect.removePropertyChangeListener(this);
				for (EditableSymbolTableEntry parameter : elect.getParameters()) {
					parameter.removePropertyChangeListener(this);
				}
			}
		}
	}
	BioModel newValue = (BioModel)evt.getNewValue();
	if (newValue != null){
		for (ModelParameter modelEditableSymbolTableEntry : newValue.getModel().getModelParameters()) {
			modelEditableSymbolTableEntry.addPropertyChangeListener(this);
		}
		for (SpeciesContext sc : newValue.getModel().getSpeciesContexts()) {
			sc.addPropertyChangeListener(this);
		}
		for (ReactionStep reactionStep : newValue.getModel().getReactionSteps()){
			reactionStep.addPropertyChangeListener(this);
			Kinetics kinetics = reactionStep.getKinetics();
			kinetics.addPropertyChangeListener(this);
			for (KineticsParameter kineticsEditableSymbolTableEntry : kinetics.getKineticsParameters()) {
				kineticsEditableSymbolTableEntry.addPropertyChangeListener(this);
			}
			for (ProxyParameter proxyEditableSymbolTableEntry : kinetics.getProxyParameters()) {
				proxyEditableSymbolTableEntry.addPropertyChangeListener(this);
			}
			for (UnresolvedParameter unresolvedEditableSymbolTableEntry: kinetics.getUnresolvedParameters()) {
				unresolvedEditableSymbolTableEntry.addPropertyChangeListener(this);
			}
		}
		for (SimulationContext simulationContext : newValue.getSimulationContexts()) {
			simulationContext.addPropertyChangeListener(this);
			simulationContext.getGeometryContext().addPropertyChangeListener(this);
			for (StructureMapping mapping : simulationContext.getGeometryContext().getStructureMappings()) {
				mapping.addPropertyChangeListener(this);
				for (EditableSymbolTableEntry parameter : mapping.getParameters()) {
					parameter.addPropertyChangeListener(this);
				}
			}
			simulationContext.getReactionContext().addPropertyChangeListener(this);
			for (SpeciesContextSpec spec : simulationContext.getReactionContext().getSpeciesContextSpecs()) {
				spec.addPropertyChangeListener(this);
				for (EditableSymbolTableEntry parameter : spec.getParameters()) {
					parameter.addPropertyChangeListener(this);
				}
			}
			for (ElectricalStimulus elect : simulationContext.getElectricalStimuli()) {
				elect.addPropertyChangeListener(this);
				for (EditableSymbolTableEntry parameter : elect.getParameters()) {
					parameter.addPropertyChangeListener(this);
				}
			}
		}
	}
}

public String checkInputValue(String inputValue, int row, int column) {
	return null;
}

public SymbolTable getSymbolTable(int row, int column) {
	return null;
}

public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(int row, int column) {
	return null;
}

public Set<String> getAutoCompletionWords(int row, int column) {
	return null;
}

public final void setIncludeReactions(boolean newValue) {
	if (newValue == bReactions) {
		return;
	}	
	this.bReactions = newValue;
	refreshData();
}

public final void setIncludeGlobal(boolean newValue) {
	if (newValue == bGlobal) {
		return;
	}	
	this.bGlobal = newValue;
	refreshData();
}

public final void setIncludeApplications(boolean newValue) {
	if (newValue == bApplications) {
		return;
	}	
	this.bApplications = newValue;
	refreshData();
}

public final void setIncludeConstants(boolean newValue) {
	if (newValue == bConstants) {
		return;
	}	
	this.bConstants = newValue;
	refreshData();
}

public final void setIncludeFunctions(boolean newValue) {
	if (newValue == bFunctions) {
		return;
	}	
	this.bFunctions = newValue;
	refreshData();
}

public void setApplicationSelection(ApplicationSelection newSelection) {
	if (this.applicationSelection == newSelection){
		return;
	}
	this.applicationSelection = newSelection;
	refreshData();
}

}
