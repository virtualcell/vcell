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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Displayable;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.AssignmentRule;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.RateRule;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SiteAttributesSpec;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel.ColumnType;
import cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel.RulesProvenance;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (2/23/01 10:52:36 PM)
 * @author: 
 */
@SuppressWarnings("serial")
public class MolecularTypeSpecsTableModel extends VCellSortTableModel<MolecularComponentPattern> implements java.beans.PropertyChangeListener {
	

	private enum ColumnType {
		COLUMN_SITE("Site"),
		COLUMN_SPECIESCONTEXT("Molecule"),
		COLUMN_STRUCTURE("Location"),
		COLUMN_STATE("Initial State"),
		COLUMN_RADIUS("Radius"),
		COLUMN_DIFFUSION("Diffusion Rate");
			
		public final String label;
		private ColumnType(String label){
			this.label = label;
		}
	}
	
	ArrayList<ColumnType> columns = new ArrayList<ColumnType>();
	private SimulationContext fieldSimulationContext = null;
	private SpeciesContextSpec fieldSpeciesContextSpec = null;
	
	public MolecularTypeSpecsTableModel(ScrollTable table) {
		super(table);
		refreshColumns();
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		ColumnType columnType = columns.get(column);
		switch (columnType) {
		case COLUMN_SITE:
			return MolecularComponentPattern.class;
		case COLUMN_SPECIESCONTEXT:
				return SpeciesContext.class;
		case COLUMN_STRUCTURE:
				return Structure.class;
		case COLUMN_STATE:
			return ComponentStatePattern.class;
		case COLUMN_RADIUS:
		case COLUMN_DIFFUSION:
			return Expression.class;
		default:
			return Object.class;
		}
	}
	@Override
	public String getColumnName(int columnIndex){
		return columns.get(columnIndex).label;
	}
	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		try {
			MolecularComponentPattern mcp = getValueAt(row);
			SpeciesContext sc = fieldSpeciesContextSpec.getSpeciesContext();
			SpeciesPattern sp = sc.getSpeciesPattern();
			MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(0);
			ColumnType columnType = columns.get(col);
			switch (columnType) {
			case COLUMN_SITE:
				return mcp.getMolecularComponent().getName();
			case COLUMN_SPECIESCONTEXT:
					return sc.getName();
			case COLUMN_STRUCTURE:
					return sc.getStructure();
			case COLUMN_STATE:
				return mcp.getComponentStatePattern().getComponentStateDefinition().getName();
			case COLUMN_RADIUS:
				return new Expression("1");		// nm
			case COLUMN_DIFFUSION:
				return new Expression("1");		// um^2/s
			default:
				return null;
			}
		} catch(Exception ex) {
			ex.printStackTrace(System.out);
			return null;
		}
	}
	
	public void setValueAt(Object aValue, int row, int col) {
		MolecularComponentPattern mcp = getValueAt(row);
		ColumnType columnType = columns.get(col);
		switch (columnType) {
		case COLUMN_SITE:
		case COLUMN_SPECIESCONTEXT:
		case COLUMN_STRUCTURE:
		case COLUMN_STATE:
			return;
		case COLUMN_RADIUS:
			if (aValue instanceof String) {
				String newExpressionString = (String)aValue;
				double result = Double.parseDouble(newExpressionString);
				SiteAttributesSpec sas = getSpeciesContextSpec().getSiteAttributesMap().get(mcp);
//				sas.setRadius();	//  TODO: make setters
			}
		case COLUMN_DIFFUSION:
			if (aValue instanceof String) {
				String newExpressionString = (String)aValue;
				double result = Double.parseDouble(newExpressionString);
				SiteAttributesSpec sas = getSpeciesContextSpec().getSiteAttributesMap().get(mcp);
//				sas.setDiffusionRate();
			}
		default:
			return;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		MolecularComponentPattern mcp = getValueAt(row);
		ColumnType columnType = columns.get(col);
		switch (columnType) {
		case COLUMN_SITE:
		case COLUMN_SPECIESCONTEXT:
		case COLUMN_STRUCTURE:
		case COLUMN_STATE:
			return false;
		case COLUMN_RADIUS:
		case COLUMN_DIFFUSION:
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public Comparator<MolecularComponentPattern> getComparator(final int col, final boolean ascending) {
		return new Comparator<MolecularComponentPattern>() {	
			/**
			 * Compares its two arguments for order.  Returns a negative integer,
			 * zero, or a positive integer as the first argument is less than, equal
			 * to, or greater than the second.<p>
			 */
			public int compare(MolecularComponentPattern mcp1, MolecularComponentPattern mcp2){			
				
				ColumnType columnType = columns.get(col);
				switch (columnType) {
				case COLUMN_SITE:
				case COLUMN_SPECIESCONTEXT:
				case COLUMN_STRUCTURE:
				case COLUMN_STATE:
				case COLUMN_RADIUS:
				case COLUMN_DIFFUSION:
				default:
					return 1;
				}
			}
		};
	}
	
	private void refreshColumns() {
		columns.clear();
		columns.addAll(Arrays.asList(ColumnType.values())); // initialize to all columns
		// TODO: may remove some columns ex: columns.remove(ColumnType.COLUMN_STRUCTURE)
	}

	public void setSimulationContext(SimulationContext simulationContext) {
		SimulationContext oldValue = fieldSimulationContext;
		int oldColumnCount = getColumnCount();
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(this);
			oldValue.getGeometryContext().removePropertyChangeListener(this);
			updateListenersReactionContext(oldValue.getReactionContext(),true);
		}
		fieldSimulationContext = simulationContext;
		refreshColumns();
		int newColumnCount = getColumnCount();
		if (oldColumnCount != newColumnCount) {
			fireTableStructureChanged();
		}
		if (simulationContext != null) {
			simulationContext.addPropertyChangeListener(this);
			simulationContext.getGeometryContext().addPropertyChangeListener(this);
			updateListenersReactionContext(simulationContext.getReactionContext(),false);
			
//			autoCompleteSymbolFilter  = simulationContext.getAutoCompleteSymbolFilter();
			refreshData();
		}
	}
	private SimulationContext getSimulationContext() {
		return fieldSimulationContext;
	}
	
	public void setSpeciesContextSpec(SpeciesContextSpec speciesContextSpec) {
		SpeciesContextSpec oldValue = fieldSpeciesContextSpec;
		int oldColumnCount = getColumnCount();
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(this);
		}
		fieldSpeciesContextSpec = speciesContextSpec;
		refreshColumns();
		int newColumnCount = getColumnCount();
		if (oldColumnCount != newColumnCount) {
			fireTableStructureChanged();
		}
		if (speciesContextSpec != null) {
			speciesContextSpec.addPropertyChangeListener(this);
			refreshData();
		}
	}
	private SpeciesContextSpec getSpeciesContextSpec() {
		return fieldSpeciesContextSpec;
	}


	private void updateListenersReactionContext(ReactionContext reactionContext,boolean bRemove) {

		if(bRemove){
			reactionContext.removePropertyChangeListener(this);
			SpeciesContextSpec oldSpecs[] = reactionContext.getSpeciesContextSpecs();
			for (int i=0;i<oldSpecs.length;i++){
				oldSpecs[i].removePropertyChangeListener(this);
				oldSpecs[i].getSpeciesContext().removePropertyChangeListener(this);
				Parameter oldParameters[] = oldSpecs[i].getParameters();
				for (int j = 0; j < oldParameters.length ; j++){
					oldParameters[j].removePropertyChangeListener(this);
				}
			}
		}else{
			reactionContext.addPropertyChangeListener(this);
			SpeciesContextSpec newSpecs[] = reactionContext.getSpeciesContextSpecs();
			for (int i=0;i<newSpecs.length;i++){
				newSpecs[i].addPropertyChangeListener(this);
				newSpecs[i].getSpeciesContext().addPropertyChangeListener(this);
				Parameter newParameters[] = newSpecs[i].getParameters();
				for (int j = 0; j < newParameters.length ; j++){
					newParameters[j].addPropertyChangeListener(this);
				}
			}
		}
	}

	private void refreshData() {
		List<MolecularComponentPattern> molecularComponentPatternList = computeData();
		setData(molecularComponentPatternList);
		GuiUtils.flexResizeTableColumns(ownerTable);
	}
	protected List<MolecularComponentPattern> computeData() {
		ArrayList<MolecularComponentPattern> allParameterList = new ArrayList<MolecularComponentPattern>();
		if(fieldSpeciesContextSpec != null) {
			MolecularTypePattern mtp = fieldSpeciesContextSpec.getSpeciesContext().getSpeciesPattern().getMolecularTypePatterns().get(0);
			MolecularType mt = mtp.getMolecularType();
			List<MolecularComponent> componentList = mt.getComponentList();
			for(MolecularComponent mc : componentList) {
				MolecularComponentPattern mcp = mtp.getMolecularComponentPattern(mc);
				allParameterList.add(mcp);
			}
		}else{
			return null;
		}
		return allParameterList;
//		boolean bSearchInactive = searchText == null || searchText.length() == 0;
//		if(bSearchInactive){
//			return allParameterList;
//		}
//		String lowerCaseSearchText = bSearchInactive ? null : searchText.toLowerCase();
//		ArrayList<SpeciesContextSpec> parameterList = new ArrayList<SpeciesContextSpec>();
//		for (SpeciesContextSpec parameter : allParameterList) {
//			if (bSearchInactive
//				|| parameter.getSpeciesContext().getName().toLowerCase().contains(lowerCaseSearchText)
//				/*|| parameter.getSpeciesContext().getStructure().getName().toLowerCase().contains(lowerCaseSearchText)*/) {
//				parameterList.add(parameter);
//			}
//		}
//		return parameterList;
	}
	
	

	


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof ReactionContext && evt.getPropertyName().equals("speciesContextSpecs")) {
			refreshData();
		}
		if (evt.getSource() instanceof SpeciesContext && evt.getPropertyName().equals("name")) {
			fireTableRowsUpdated(0,getRowCount()-1);
		}

		if (evt.getSource() instanceof SpeciesContextSpec) {
			fireTableRowsUpdated(0,getRowCount()-1);
		}
		if (evt.getSource() instanceof SpeciesContextSpec.SpeciesContextSpecParameter) {
			fireTableRowsUpdated(0,getRowCount()-1);
		}
		if (evt.getSource() instanceof GeometryContext) {
			refreshColumns();
			fireTableStructureChanged();
		}

	}
	

}
