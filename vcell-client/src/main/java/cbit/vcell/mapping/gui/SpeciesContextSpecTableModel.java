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

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.mapping.*;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.springsalad.gui.MolecularStructuresPanel;
import org.vcell.util.Displayable;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

import javax.swing.*;


public class SpeciesContextSpecTableModel extends VCellSortTableModel<SpeciesContextSpec> implements java.beans.PropertyChangeListener {
	private static final Logger lg = LogManager.getLogger(SpeciesContextSpecTableModel.class);

	// Rules Provenance
	public static class RulesProvenance implements Displayable {
		public static final String displayName = "RulesProvenance";
		public static final String typeName = "RulesProvenance";

		@Override
		public String getDisplayName() {
			return displayName;
		}

		@Override
		public String getDisplayType() {
			return typeName;
		}
	}

	// Column Type
	public enum ColumnType {
		SPECIES_CONTEXT("Species"),
		STRUCTURE("Structure"),
		DEPICTION("Depiction"),
		IS_2D("Is 2D"),
		IS_CLAMPED("Clamped"),
		RULES("Rules"),
		INITIAL_CONDITION("Initial Condition"),
		WELL_MIXED("Well Mixed"),
		DIFFUSION_CONSTANT("Diffusion Constant"),
		FORCE_CONTINUOUS("Force Continuous");

		public final String label;

		ColumnType(String label) {
			this.label = label;
		}
	}

	// SpeciesContextSpecsTableModel Members
	private final List<ColumnType> columns;
	private final DocumentEditorSubPanel owner;

	private SimulationContext simulationContext;
	private AutoCompleteSymbolFilter autoCompleteSymbolFilter;
	private boolean isEditable;    // this.isCellEditable() decides
	private String searchText;

	public SpeciesContextSpecTableModel(ScrollTable table, DocumentEditorSubPanel owner) {
		super(table);
		this.columns = new ArrayList<>();
		this.owner = owner;

		this.simulationContext = null;
		this.autoCompleteSymbolFilter = null;
		this.isEditable = true;
		this.searchText = "";

		this.refreshColumns();
	}

	public boolean isEditable() {
		return this.isEditable;
	}

	public Class<?> getColumnClass(int column) {
		ColumnType columnType = this.columns.get(column);
		return switch (columnType) {
			case SPECIES_CONTEXT -> SpeciesContext.class;
			case STRUCTURE -> Structure.class;
			case DEPICTION -> SpeciesPattern.class;
			case RULES -> RulesProvenance.class;
			case IS_2D, IS_CLAMPED, WELL_MIXED, FORCE_CONTINUOUS -> Boolean.class;
			case INITIAL_CONDITION, DIFFUSION_CONSTANT -> ScopedExpression.class;
		};
	}

	@Override
	public String getColumnName(int columnIndex) {
		return this.columns.get(columnIndex).label;
	}

	@Override
	public int getColumnCount() {
		return this.columns.size();
	}

	@Override
	public SpeciesContextSpec getValueAt(int row) {
		SpeciesContextSpec scs = super.getValueAt(row);
		if (scs == null) return null;
		if (SimulationContext.Application.SPRINGSALAD != this.getSimulationContext().getApplicationType()){
			scs.provenance = SpeciesContextSpec.Provenance.GeneralInitialConditions;
			return scs;
		}
		scs.provenance = this.owner instanceof MolecularStructuresPanel ?
				SpeciesContextSpec.Provenance.LangevinSpecs : SpeciesContextSpec.Provenance.LangevinInitialConditions;
		return scs;
	}

	public Object getValueAt(int row, int col) {
		try {
			SpeciesContextSpec scSpec = this.getValueAt(row);
			ColumnType columnType = this.columns.get(col);
			return switch (columnType){
				case SPECIES_CONTEXT -> scSpec.getSpeciesContext();
				case STRUCTURE -> scSpec.getSpeciesContext().getStructure();
				case DEPICTION -> scSpec.getSpeciesContext().getSpeciesPattern();
				case IS_2D -> scSpec.getIs2D();
				case IS_CLAMPED -> scSpec.isClamped();
				case RULES -> null;
				case WELL_MIXED -> (scSpec.isClamped() || scSpec.isWellMixed())
						&& this.getSimulationContext().getApplicationType() == SimulationContext.Application.NETWORK_STOCHASTIC;
				case INITIAL_CONDITION -> this.getScopedExpressionIC(scSpec);
				case DIFFUSION_CONSTANT -> this.getScopedExpressionDC(scSpec);
				case FORCE_CONTINUOUS -> scSpec.isForceContinuous();
			};
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return null;
		}
	}

	private SimulationContext getSimulationContext() {
		return this.simulationContext;
	}

	public void setEditable(boolean bEditable) {
		this.isEditable = bEditable;
	}


	public void setSearchText(String searchText) {
		this.searchText = null == searchText ? "" : searchText;
		this.refreshData();
	}

	/**
	 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
	 *
	 * @param simulationContext The new value for the property.
	 * @see #getSimulationContext
	 */
	public void setSimulationContext(SimulationContext simulationContext) {
		SimulationContext oldValue = this.simulationContext;
		int oldColumnCount = this.getColumnCount();
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(this);
			oldValue.getGeometryContext().removePropertyChangeListener(this);
			this.updateListenersReactionContext(oldValue.getReactionContext(), true);
		}
		this.simulationContext = simulationContext;
		this.refreshColumns();
		int newColumnCount = this.getColumnCount();
		if (oldColumnCount != newColumnCount) {
			this.fireTableStructureChanged();
		}
		if (simulationContext != null) {
			simulationContext.addPropertyChangeListener(this);
			simulationContext.getGeometryContext().addPropertyChangeListener(this);
			this.updateListenersReactionContext(simulationContext.getReactionContext(), false);

			this.autoCompleteSymbolFilter = simulationContext.getAutoCompleteSymbolFilter();
			this.refreshData();
		}
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		SpeciesContextSpec scSpec = this.getValueAt(rowIndex);
		ColumnType columnType = this.columns.get(columnIndex);

		// Note: We call update after the switch statement (assuming we did actually change something).
		switch (columnType) {
			case IS_2D -> scSpec.setIs2D((Boolean) aValue);
			case IS_CLAMPED -> scSpec.setClamped((Boolean) aValue);
			case WELL_MIXED -> scSpec.setWellMixed((Boolean) aValue);
			case FORCE_CONTINUOUS -> scSpec.setForceContinuous((Boolean) aValue);
			case INITIAL_CONDITION -> {
				if (!(aValue instanceof String newExpressionString)) return; // no update; we didn't change anything!
				SpeciesContextSpecParameter targetParameter = this.getSimulationContext().isUsingConcentration() ?
						scSpec.getInitialConcentrationParameter() : scSpec.getInitialCountParameter();
				try {
					targetParameter.setExpression(new Expression(newExpressionString));
				} catch (ExpressionException e) {

					PopupGenerator.showErrorDialog(this.ownerTable, "Wrong Expression:\n" + e.getMessage());
				}

			}
			case DIFFUSION_CONSTANT -> {
				if (!(aValue instanceof String newExpressionString)) return; // no update; we didn't change anything!
				try {
					scSpec.getDiffusionParameter().setExpression(new Expression(newExpressionString));
				} catch (ExpressionException e) {
					lg.error(e); // don't handle exception here, InitialConditionsPanel needs it.
					PopupGenerator.showErrorDialog(this.ownerTable, "Wrong Expression:\n" + e.getMessage());
				}
			}
			default -> { return; } // no update; we didn't change anything!
		}
		this.fireTableRowsUpdated(rowIndex, rowIndex);
	}

	protected List<SpeciesContextSpec> computeData() {
		if (null == this.getSimulationContext()) return null;
		List<SpeciesContextSpec> allParameterList = Arrays.asList(this.getSimulationContext().getReactionContext().getSpeciesContextSpecs());

		boolean isSearchInactive = this.searchText == null || this.searchText.isEmpty();
		if (isSearchInactive) return allParameterList;

		String lowerCaseSearchText = this.searchText.toLowerCase();
		Predicate<SpeciesContextSpec> paramSpeciesContextContainsSearchText = (SpeciesContextSpec param)->param.getSpeciesContext().getName().toLowerCase().contains(lowerCaseSearchText);
		return allParameterList.stream().filter(paramSpeciesContextContainsSearchText).toList();
	}

	private void refreshColumns() {
		SimulationContext simContext = this.getSimulationContext();
		this.columns.clear();

		this.columns.add(ColumnType.SPECIES_CONTEXT);
		this.columns.add(ColumnType.STRUCTURE);
		this.columns.add(ColumnType.DEPICTION);
		this.columns.add(ColumnType.INITIAL_CONDITION);

		if (null == simContext) return;

		boolean isSpatial = simContext.getGeometry().getDimension() > 0;
		switch (simContext.getApplicationType()){
			case SPRINGSALAD -> { // Always Spatial
				this.columns.add(ColumnType.IS_2D);
				this.columns.add(ColumnType.IS_CLAMPED);
			}
			case NETWORK_STOCHASTIC -> {
				this.columns.add(ColumnType.RULES);
				this.columns.add(ColumnType.IS_CLAMPED);
				this.columns.add(ColumnType.FORCE_CONTINUOUS);
				if (isSpatial) {
					this.columns.add(ColumnType.WELL_MIXED);
					this.columns.add(ColumnType.DIFFUSION_CONSTANT);
				}
			}
			case RULE_BASED_STOCHASTIC -> {
				this.columns.add(ColumnType.RULES);
				if (isSpatial) {
					this.columns.add(ColumnType.WELL_MIXED);
					this.columns.add(ColumnType.DIFFUSION_CONSTANT);
				}
			}
			default -> {
				this.columns.add(ColumnType.RULES);
				this.columns.add(ColumnType.IS_CLAMPED);
				if (isSpatial) {
					this.columns.add(ColumnType.WELL_MIXED);
					this.columns.add(ColumnType.DIFFUSION_CONSTANT);
				}
			}
		}
	}


	private void refreshData() {
		List<SpeciesContextSpec> speciesContextSpecList = this.computeData();
		this.setData(speciesContextSpecList);
		GuiUtils.flexResizeTableColumns(this.ownerTable);
	}


	private ScopedExpression getScopedExpressionIC(SpeciesContextSpec scSpec){
		SpeciesContextSpecParameter initialConditionParameter = scSpec.getInitialConditionParameter();
		if (null == initialConditionParameter) return null;
		return new ScopedExpression(initialConditionParameter.getExpression(), initialConditionParameter.getNameScope(), true, true, this.autoCompleteSymbolFilter);
	}

	private ScopedExpression getScopedExpressionDC(SpeciesContextSpec scSpec){
		SpeciesContextSpecParameter diffusionParameter = scSpec.getDiffusionParameter();
		if (null == diffusionParameter || scSpec.isClamped() || null == scSpec.isWellMixed() || scSpec.isWellMixed()) return null;
		return new ScopedExpression(diffusionParameter.getExpression(), diffusionParameter.getNameScope(), true, true, this.autoCompleteSymbolFilter);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		SpeciesContextSpec speciesContextSpec = this.getValueAt(rowIndex);
		ColumnType columnType = this.columns.get(columnIndex);
		boolean isStoch = SimulationContext.Application.NETWORK_STOCHASTIC == this.getSimulationContext().getApplicationType();
		return switch (columnType) {
			case SPECIES_CONTEXT, STRUCTURE, DEPICTION, RULES -> false;
			case IS_2D -> {
				yield false;
				// is2D flag permanently set to false in this version, consider re-enabling the following lines instead
//				if (!this.isEditable) return false;
//			    Structure structure = speciesContextSpec.getSpeciesContext().getStructure();
//			    return structure instanceof Membrane
			}
			case IS_CLAMPED -> this.isEditable;
			case WELL_MIXED -> !speciesContextSpec.isClamped() && !isStoch;
			case FORCE_CONTINUOUS -> !speciesContextSpec.isClamped() && isStoch;
			case INITIAL_CONDITION -> {
//			    RateRule rr = fieldSimulationContext.getRateRule(speciesContextSpec.getSpeciesContext());
				AssignmentRule ar = this.simulationContext.getAssignmentRule(speciesContextSpec.getSpeciesContext());
				if (/*rr != null || */ar != null) {
					yield false;
				}
				yield this.isEditable;
			}
			case DIFFUSION_CONSTANT -> !speciesContextSpec.isClamped() && (!speciesContextSpec.isWellMixed() || isStoch);
		};
	}

	private List<SpeciesContextSpec> getAllRows(){
		return this.allRows;
	}

	/**
	 * This method gets called when a bound property is changed.
	 */
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		if (evt.getSource() instanceof ReactionContext reactionEvent && evt.getPropertyName().equals("speciesContextSpecs")) {
			this.updateListenersReactionContext(reactionEvent, true);
			this.updateListenersReactionContext(reactionEvent, false);
			this.refreshData();
		}

		if (evt.getSource() instanceof SpeciesContext && evt.getPropertyName().equals("name")) {
			this.fireTableRowsUpdated(0, this.getRowCount() - 1);
		}

		if (evt.getSource() == this.getSimulationContext() && evt.getPropertyName().equals(SimulationContext.PROPERTY_NAME_ASSIGNMENT_RULE_CHANGE)) {
			AssignmentRule oldRule = (AssignmentRule) evt.getOldValue();
			AssignmentRule newRule = (AssignmentRule) evt.getNewValue();
			this.onRuleVariableChanged(oldRule, newRule);
		} else if (evt.getSource() == this.getSimulationContext() && evt.getPropertyName().equals(SimulationContext.PROPERTY_NAME_RATE_RULE_CHANGE)) {
			RateRule oldRule = (RateRule) evt.getOldValue();
			RateRule newRule = (RateRule) evt.getNewValue();
			this.onRuleVariableChanged(oldRule, newRule);
		} else if (evt.getSource() == this.getSimulationContext() && evt.getPropertyName().equals(SimulationContext.PROPERTY_NAME_ASSIGNMENTRULES)) {
			lg.info("Resolving assignment rules event");
			AssignmentRule[] oldRules = (AssignmentRule[]) evt.getOldValue();
			AssignmentRule[] newRules = (AssignmentRule[]) evt.getNewValue();
			if (oldRules != null && newRules != null && oldRules.length > newRules.length) this.onRuleDelete(oldRules, newRules);
		} else if (evt.getSource() == this.getSimulationContext() && evt.getPropertyName().equals(SimulationContext.PROPERTY_NAME_RATERULES)) {
			lg.info("Resolving rate rules event");
			RateRule[] oldRules = (RateRule[]) evt.getOldValue();
			RateRule[] newRules = (RateRule[]) evt.getNewValue();
			if (oldRules != null && newRules != null && oldRules.length > newRules.length) this.onRuleDelete(oldRules, newRules);
		}

		if (evt.getSource() instanceof SpeciesContextSpec) this.fireTableRowsUpdated(0, this.getRowCount() - 1);

		if (evt.getSource() instanceof SpeciesContextSpec.SpeciesContextSpecParameter) this.fireTableRowsUpdated(0, this.getRowCount() - 1);

		if (evt.getSource() instanceof GeometryContext) {
			this.refreshColumns();
			this.fireTableStructureChanged();
		}
	}

	private void removeRuleVariableMark(SpeciesContext sc, boolean unclamp) {
		SpeciesContextSpec[] scss = this.simulationContext.getReactionContext().getSpeciesContextSpecs();
		for (SpeciesContextSpec scs : scss) {
			if (scs.getSpeciesContext() != null && scs.getSpeciesContext() == sc) {
				if (unclamp) {
					scs.setClamped(false);
				}
//			try {
//				if(getSimulationContext().isUsingConcentration()) {
//					scs.getInitialConcentrationParameter().setExpression(new Expression("0"));
//				} else {
//					scs.getInitialCountParameter().setExpression(new Expression("0"));
//				}
//			} catch(ExpressionException e) {
//				lg.error(e);
//			}
			}
		}
	}

	private void setRuleVariableMark(SpeciesContext sc, Expression ex, boolean clamp) {
		for (SpeciesContextSpec scs : this.simulationContext.getReactionContext().getSpeciesContextSpecs()) {
			if (null == scs.getSpeciesContext() || sc != scs.getSpeciesContext()) continue;
			if (clamp) scs.setClamped(true);

//			try {
//				if(getSimulationContext().isUsingConcentration()) {
//					scs.getInitialConcentrationParameter().setExpression(new Expression(ex));
//				} else {
//					scs.getInitialCountParameter().setExpression(new Expression(ex));
//				}
//			} catch (ExpressionBindingException e) {
//				lg.error(e);
//			}
			this.fireTableRowsUpdated(0, this.getRowCount() - 1);
			break;        // can't find more than one
		}
	}

	private void onRuleVariableChanged(RuleVariableAccessible oldRule, RuleVariableAccessible newRule) {
		if (oldRule != null && oldRule.getSimulationContext() == this.simulationContext && oldRule.getRuleVar() instanceof SpeciesContext sc) {
			this.removeRuleVariableMark(sc, true);
		}
		if (newRule != null && newRule.getSimulationContext() == this.simulationContext && newRule.getRuleVar() instanceof SpeciesContext sc) {
			this.setRuleVariableMark(sc, newRule.getRuleExpression(), true);
		}
	}

	private void onRuleDelete(RuleVariableAccessible[] oldRules, RuleVariableAccessible[] newRules) {
		lg.info("num old rules: {}, num new rules: {}", oldRules.length, newRules.length);
		Set<String> newRuleNamesSet = Arrays.stream(newRules).map(RuleVariableAccessible::getName).collect(Collectors.toSet());

		// there is one rule in the old rules that has been deleted from the new rules. Find it and perform maintenance
		for (RuleVariableAccessible oldRuleCandidate : oldRules){
			if (newRuleNamesSet.contains(oldRuleCandidate.getName())) continue;
			if (oldRuleCandidate.getSimulationContext() != this.simulationContext) return;
			if (!(oldRuleCandidate.getRuleVar() instanceof SpeciesContext sc)) return;
			this.removeRuleVariableMark(sc, true);
			return;
		}
	}

	private void updateListenersReactionContext(ReactionContext reactionContext, boolean bRemove) {
		if (bRemove) {
			this.removeOldListenersReactionContext(reactionContext);
		} else {
			this.addNewListenersReactionContext(reactionContext);
		}
	}

	private void removeOldListenersReactionContext(ReactionContext reactionContext) {
		reactionContext.removePropertyChangeListener(this);
		for (SpeciesContextSpec oldSpec : reactionContext.getSpeciesContextSpecs()) {
			oldSpec.removePropertyChangeListener(this);
			oldSpec.getSpeciesContext().removePropertyChangeListener(this);
			for (Parameter oldParameter : oldSpec.getParameters()) {
				oldParameter.removePropertyChangeListener(this);
			}
		}
	}

	private void addNewListenersReactionContext(ReactionContext reactionContext) {
		reactionContext.addPropertyChangeListener(this);
		for (SpeciesContextSpec newSpec : reactionContext.getSpeciesContextSpecs()) {
			newSpec.addPropertyChangeListener(this);
			newSpec.getSpeciesContext().addPropertyChangeListener(this);
			for (Parameter newParameter : newSpec.getParameters()) {
				newParameter.addPropertyChangeListener(this);
			}
		}
	}

	@Override
	public Comparator<SpeciesContextSpec> getComparator(final int col, final boolean ascending) {
		return new Comparator<>() {
			/**
			 * Compares its two arguments for order.  Returns a negative integer,
			 * zero, or a positive integer as the first argument is less than, equal
			 * to, or greater than the second.<p>
			 */
			public int compare(SpeciesContextSpec speciesContextSpec1, SpeciesContextSpec speciesContextSpec2) {
				SpeciesContext speciesContext1 = speciesContextSpec1.getSpeciesContext();
				SpeciesContext speciesContext2 = speciesContextSpec2.getSpeciesContext();
				ColumnType columnType = SpeciesContextSpecTableModel.this.columns.get(col);
				int sortDirectionMultiplier = ascending ? 1 : -1;

				return switch (columnType){
					case SPECIES_CONTEXT -> {
						String name1 = speciesContext1.getName();
						String name2 = speciesContext2.getName();
						yield name1.compareToIgnoreCase(name2) * sortDirectionMultiplier;
					}
					case STRUCTURE -> {
						String name1 = speciesContext1.getStructure().getName();
						String name2 = speciesContext2.getStructure().getName();
						yield name1.compareToIgnoreCase(name2) * sortDirectionMultiplier;
					}
					case IS_2D -> {
						Boolean is2D1 = speciesContextSpec1.getIs2D();
						Boolean is2D2 = speciesContextSpec2.getIs2D();
						yield is2D1.compareTo(is2D2) * sortDirectionMultiplier;
					}
					case IS_CLAMPED -> {
						Boolean isClamped1 = speciesContextSpec1.isClamped();
						Boolean isClamped2 = speciesContextSpec2.isClamped();
						yield isClamped1.compareTo(isClamped2) * sortDirectionMultiplier;
					}
					case FORCE_CONTINUOUS -> {
						Boolean bForceContinuous1 = speciesContextSpec1.isForceContinuous();
						Boolean bForceContinuous2 = speciesContextSpec2.isForceContinuous();
						yield bForceContinuous1.compareTo(bForceContinuous2) * sortDirectionMultiplier;
					}
					case WELL_MIXED -> {
						Boolean bWellMixed1 = speciesContextSpec1.isWellMixed();
						Boolean bWellMixed2 = speciesContextSpec2.isWellMixed();
						yield bWellMixed1.compareTo(bWellMixed2) * sortDirectionMultiplier;
					}
					case INITIAL_CONDITION -> {
						Expression initExp1 = speciesContextSpec1.getInitialConditionParameter().getExpression();
						Expression initExp2 = speciesContextSpec2.getInitialConditionParameter().getExpression();
						yield TableUtil.expressionCompare(initExp1, initExp2, ascending);
					}
					case DIFFUSION_CONSTANT -> {
						Expression diffExp1 = speciesContextSpec1.getDiffusionParameter().getExpression();
						Expression diffExp2 = speciesContextSpec2.getDiffusionParameter().getExpression();
						yield TableUtil.expressionCompare(diffExp1, diffExp2, ascending);
					}
					case DEPICTION, RULES -> 1; // why isn't this just an object.compareTo? Or at least sortDirectionMultiplier?
				};
			}
		};
	}

	public static class TableUtil {
		// detects whether expressions within this column contain numbers, alphanumeric expressions or a mix
		// and sorts accordingly (numbers first (sorted numerically), alphanumeric expr next (sorted alphabetically w. ignore case))
		public static int expressionCompare(Expression e1, Expression e2, boolean ascending) {
			int sortDirectionMultiplier = ascending ? 1 : -1;
			if (e1 == null || e2 == null) return 0;

			if (e1.isNumeric() && e2.isNumeric()) {  // both are numbers
				Float f1 = Float.valueOf(e1.infix());
				Float f2 = Float.valueOf(e2.infix());
				return f1.compareTo(f2) * sortDirectionMultiplier;
			} else if (!e1.isNumeric() && !e2.isNumeric()){ // both are not-numbers
				return e1.infix().compareToIgnoreCase(e2.infix()) * sortDirectionMultiplier;
			} else { // only one is a number
				return Boolean.compare(e2.isNumeric(), e1.isNumeric()) * sortDirectionMultiplier;
			}
		}
	}

}
