/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.model.rbm;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.vcell.util.gui.AutoCompleteTableModel;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class GeneratedSpeciesTableModel extends VCellSortTableModel<GeneratedSpeciesTableRow> 
	implements  PropertyChangeListener, AutoCompleteTableModel{

	public static final int colCount = 5;
	public static final int iColIndex = 0;
	public static final int iColOriginalName = 1;
	public static final int iColStructure = 2;
	public static final int iColDepiction = 3;
	public static final int iColExpression = 4;
	
	// filtering variables 
	protected static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	protected String searchText = null;

	private BNGSpecies[] speciess;
	private Model model;
	
	private ArrayList<GeneratedSpeciesTableRow> allGeneratedSpeciesList;

	private final NetworkConstraintsPanel owner;

	protected transient java.beans.PropertyChangeSupport propertyChange;

	public GeneratedSpeciesTableModel(EditorScrollTable table, NetworkConstraintsPanel owner) {
		super(table, new String[] {"Index", "Name", "Structure", "Depiction", "Expression"});
		this.owner = owner;
		setMaxRowsPerPage(1000);
	}
	
	public Class<?> getColumnClass(int iCol) {
		switch (iCol){		
			case iColIndex:{
				return String.class;
			}case iColOriginalName:{
				return String.class;
			}case iColStructure:{
				return String.class;
			}case iColDepiction:{
				return Object.class;
			}case iColExpression:{
				return String.class;
			}
		}
	return Object.class;
	}
	
	public Object getValueAt(int iRow, int iCol) {
		GeneratedSpeciesTableRow speciesTableRow = getValueAt(iRow);
		switch(iCol) {
			case iColIndex:
				return speciesTableRow.getIndex();
			case iColOriginalName:
				return speciesTableRow.getOriginalName();
			case iColStructure:{
				BNGSpecies speciesObject = speciesTableRow.getSpeciesObject();
				String expressionString = speciesTableRow.getExpression();
				
				if(expressionString.startsWith("@") && expressionString.contains(":")) {
					String structName = expressionString.substring(1, expressionString.indexOf(":"));
					return structName;
				} else {
					SimulationContext sc = owner.getSimulationContext();
					if(sc.getModel().getStructures().length > 1) {
						// sanity check: structure should be explicit in the expression 
						// if we have more than 1 compartments
						return "?";
					} else {
						Structure struct = sc.getModel().getStructure(0);
						return struct.getName();
					}
				}
			}
			case iColDepiction:
			case iColExpression:
				return speciesTableRow.getExpression();
			default:
				return null;
		}
	}
	
	public boolean isCellEditable(int iRow, int iCol) {
		switch(iCol) {
		case iColExpression:
			return true;
		default:
			return false;
		}
	}
	
	public void setValueAt(Object valueNew, int iRow, int iCol) {
		return;
	}
	
	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(final int row, final int column) {
		return null;
	}
	
	public Set<String> getAutoCompletionWords(int row, int iCol) {
		return null;
	}
	
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		
	}
	
	public Comparator<GeneratedSpeciesTableRow> getComparator(final int col, final boolean ascending) {
		return new Comparator<GeneratedSpeciesTableRow>() {
		    public int compare(GeneratedSpeciesTableRow o1, GeneratedSpeciesTableRow o2){
		    	return 0;
		    }
		};
	}
	
	public void setSearchText(String newValue) {
		if (searchText == newValue) {
			return;
		}
		searchText = newValue;
		refreshData();
	}
	
	private void refreshData() {
		allGeneratedSpeciesList = new ArrayList<GeneratedSpeciesTableRow>();
		
		LinkedHashMap<String, String> scMap = new LinkedHashMap<String, String>();
		for(int i = 0; i<speciess.length; i++) {
			BNGSpecies species = speciess[i];
			String key = species.getConcentration().infix();
			String originalName = "";
			FakeSeedSpeciesInitialConditionsParameter fakeParam = FakeSeedSpeciesInitialConditionsParameter.fromString(key);
			if (fakeParam != null){
				originalName = fakeParam.speciesContextName;
				System.out.println(originalName);
				scMap.put(originalName, originalName);
				GeneratedSpeciesTableRow newRow = createTableRow(species, i+1, originalName, species.toStringShort());
				allGeneratedSpeciesList.add(newRow);
			}
		}
				
		for(int i = 0; i<speciess.length; i++) {
			BNGSpecies species = speciess[i];
			String key = species.getConcentration().infix();
			FakeSeedSpeciesInitialConditionsParameter fakeParam = FakeSeedSpeciesInitialConditionsParameter.fromString(key);
			if (fakeParam != null){
				continue;					// we already dealt with these
			} else {
				int count = 0;				// generate unique name for the species
				String speciesName = null;
				String nameRoot = "s";
				while (true) {
					speciesName = nameRoot + count;	
					if (Model.isNameUnused(speciesName, model) && !scMap.containsKey(speciesName)) {
						break;
					}	
					count++;
				}
				scMap.put(speciesName, speciesName);
				GeneratedSpeciesTableRow newRow = createTableRow(species, i+1, speciesName, species.toStringShort());
				allGeneratedSpeciesList.add(newRow);
			}
		}
		// apply text search function for particular columns
		ArrayList<GeneratedSpeciesTableRow> speciesObjectList = new ArrayList<GeneratedSpeciesTableRow>();
		if (searchText == null || searchText.length() == 0) {
			speciesObjectList.addAll(allGeneratedSpeciesList);
		} else {
			String lowerCaseSearchText = searchText.toLowerCase();
			for (GeneratedSpeciesTableRow rs : allGeneratedSpeciesList){
				if (rs.getExpression().toLowerCase().contains(lowerCaseSearchText) ) {
					speciesObjectList.add(rs);
				}
			}
		}
		setData(speciesObjectList);
		GuiUtils.flexResizeTableColumns(ownerTable);
	}
	
	private GeneratedSpeciesTableRow createTableRow(BNGSpecies species, int index, String originalName, String interactionLabel) {
		GeneratedSpeciesTableRow row = new GeneratedSpeciesTableRow(originalName, species, owner);
		
		row.setIndex(index+" ");
		row.setExpression(interactionLabel, getModel());
		return row;
	}
	private Model getModel() {
		if(model == null) {
			try {
				model = new Model("MyTempModel");
				model.addFeature("c0");
			} catch (ModelException | PropertyVetoException e1) {
				e1.printStackTrace();
			}
		}
		return model;
	}
	
	public void setData(Model model, BNGSpecies[] speciess) {
		if (this.model == model && this.speciess == speciess) {
			return;
		}
		this.model = model;
		this.speciess = speciess;
		refreshData();
	}
	public ArrayList<GeneratedSpeciesTableRow> getTableRows() {
		return null;
	}

	@Override
	public String checkInputValue(String inputValue, int row, int column) {
		return null;
	}

	@Override
	public SymbolTable getSymbolTable(int row, int column) {
		return null;
	}

}
