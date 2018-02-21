/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.model.rbm.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.model.rbm.FakeSeedSpeciesInitialConditionsParameter;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.util.gui.AutoCompleteTableModel;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.bionetgen.ObservableGroup;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class GeneratedSpeciesTableModel2 extends VCellSortTableModel<GeneratedSpeciesTableRow> 
	implements  PropertyChangeListener, AutoCompleteTableModel{

	public static final int colCount = 5;
	public static final int iColMultiplier = 0;
	public static final int iColOriginalName = 1;
	public static final int iColStructure = 2;
	public static final int iColDepiction = 3;
	public static final int iColDefinition = 4;
	
	// filtering variables 
	protected static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	protected String searchText = null;
	protected ObservablesGroupTableRow observableFilter = null;
	
	private BNGSpecies[] speciess;
	private Model model;
	
	private ArrayList<GeneratedSpeciesTableRow> allGeneratedSpeciesList;
	private Map<Integer, String> networkFileIndexToNameMap;		// needed by the observable table model

	private final NetworkConstraintsPanel owner;

	protected transient java.beans.PropertyChangeSupport propertyChange;

	public GeneratedSpeciesTableModel2(EditorScrollTable table, NetworkConstraintsPanel owner) {
		super(table, new String[] {"Multiplier", "Name", "Structure", "Depiction", "BioNetGen Definition"});
		this.owner = owner;
		setMaxRowsPerPage(1000);
	}
	
	public Class<?> getColumnClass(int iCol) {
		switch (iCol) {		
			case iColMultiplier:
				return String.class;
			case iColOriginalName:
				return String.class;
			case iColStructure:
				return String.class;
			case iColDepiction:
				return Object.class;
			case iColDefinition:
				return String.class;
		}
		return Object.class;
	}
	
	public Object getValueAt(int iRow, int iCol) {
		GeneratedSpeciesTableRow speciesTableRow = getValueAt(iRow);
		switch(iCol) {
			case iColMultiplier:
				return speciesTableRow.getMultiplier();
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
			case iColDefinition:
				return speciesTableRow.getExpression();
			default:
				return null;
		}
	}
	
	public boolean isCellEditable(int iRow, int iCol) {
		switch(iCol) {
		case iColDefinition:
//			return true;	// being editable means that you can select a row and copy its contents to the clipboard
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
	
	@Override
	public boolean isSortable(int column) {
		switch (column) {
		default:
			return true;	// sortable by all
		}
	};
	public Comparator<GeneratedSpeciesTableRow> getComparator(final int col, final boolean ascending) {
		final int scale = ascending ? 1 : -1;
		return new Comparator<GeneratedSpeciesTableRow>() {
		    public int compare(GeneratedSpeciesTableRow o1, GeneratedSpeciesTableRow o2) {
				switch (col) {
				case iColMultiplier:
					return scale * o1.getMultiplier().compareTo(o2.getMultiplier());
				case iColOriginalName:
					return scale * o1.getOriginalName().compareToIgnoreCase(o2.getOriginalName());
				case iColStructure:
//					String es1 = o1.getExpression();
//					String n1 = es1.substring(1, es1.indexOf(":"));
					return 0;		// all generated species must belong to the same structure - the one of the observable
				case iColDefinition:	// sort just by the expression, doesn't matter if the structure is mentioned or not because 
										// all the generated species for this observable are in the same structure anyway
					return scale * o1.getExpression().compareToIgnoreCase(o2.getExpression());
				case iColDepiction:
					if(o1.getSpecies() != null && o1.getSpecies().hasSpeciesPattern() && o2.getSpecies() != null && o2.getSpecies().hasSpeciesPattern()) {
						Integer i1 = o1.getSpecies().getSpeciesPattern().getMolecularTypePatterns().size();
						Integer i2 = o2.getSpecies().getSpeciesPattern().getMolecularTypePatterns().size();
						if(scale * i1.compareTo(i2) == 0) {
							// if same number of molecule we try to sort by number of sites of the mt
							i1 = 0;
							i2 = 0;
							for(MolecularTypePattern mtp : o1.getSpecies().getSpeciesPattern().getMolecularTypePatterns()) {
								i1 += mtp.getMolecularType().getComponentList().size();
							}
							for(MolecularTypePattern mtp : o2.getSpecies().getSpeciesPattern().getMolecularTypePatterns()) {
								i2 += mtp.getMolecularType().getComponentList().size();
							}
							return scale * i1.compareTo(i2);
						} else {
							return scale * i1.compareTo(i2);
						}
					}
					return 0;
					default:
						return 0;
				}
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
	
	public void setObservableFilter(ObservablesGroupTableRow observableFilter) {
		if (this.observableFilter == observableFilter) {
			return;
		}
		this.observableFilter = observableFilter;
		refreshData();
	}
	
	private void refreshData() {
		allGeneratedSpeciesList = new ArrayList<>();
		networkFileIndexToNameMap = new HashMap<>();
		LinkedHashMap<String, String> scMap = new LinkedHashMap<>();
		String multiplier = "";

		for(int i = 0; i<speciess.length; i++) {
			BNGSpecies species = speciess[i];
			String key = species.getConcentration().infix();
			String originalName = "";
			FakeSeedSpeciesInitialConditionsParameter fakeParam = FakeSeedSpeciesInitialConditionsParameter.fromString(key);
			if(observableFilter != null) {
				int index = 0;
				ObservableGroup og = observableFilter.getObservableGroupObject();
				if(og.getSpeciesMultiplicity().length > 0) {
					multiplier = og.getSpeciesMultiplicity()[index] + "";
				}
			}
			if (fakeParam != null) {
				originalName = fakeParam.speciesContextName;
//				System.out.println(originalName);
				scMap.put(originalName, originalName);
				GeneratedSpeciesTableRow newRow = createTableRow(species, i+1, multiplier, originalName, species.toStringShort());
				allGeneratedSpeciesList.add(newRow);
				networkFileIndexToNameMap.put(species.getNetworkFileIndex(), originalName);
			}
		}
				
		for(int i = 0; i<speciess.length; i++) {
			BNGSpecies species = speciess[i];
			String key = species.getConcentration().infix();
			FakeSeedSpeciesInitialConditionsParameter fakeParam = FakeSeedSpeciesInitialConditionsParameter.fromString(key);
			if (fakeParam != null) {
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
				GeneratedSpeciesTableRow newRow = createTableRow(species, i+1, multiplier, speciesName, species.toStringShort());
				allGeneratedSpeciesList.add(newRow);
				networkFileIndexToNameMap.put(species.getNetworkFileIndex(), speciesName);
			}
		}
		// apply text search function for particular columns
		List<GeneratedSpeciesTableRow> speciesObjectList = new ArrayList<>();
		if (searchText == null || searchText.length() == 0) {
			speciesObjectList.addAll(allGeneratedSpeciesList);
		} else {
			String lowerCaseSearchText = searchText.toLowerCase();
			for (GeneratedSpeciesTableRow rs : allGeneratedSpeciesList) {
				boolean added = false;
				if (rs.getExpression().toLowerCase().contains(lowerCaseSearchText) ) {
					speciesObjectList.add(rs);
					added = true;
				}
				if(!added && rs.getOriginalName().toLowerCase().contains(lowerCaseSearchText)) {
					speciesObjectList.add(rs);
					added = true;
				}
			}
		}
		
		List<GeneratedSpeciesTableRow> speciesObjectList2;
		if(observableFilter == null) {
			speciesObjectList2 = speciesObjectList;
		} else {
			// extra filtering by observable, if needed
			speciesObjectList2 = new ArrayList<>();
			ObservableGroup og = observableFilter.getObservableGroupObject();
			List<Integer> indexesList = og.getIndexesAsIntegersList();
			for (GeneratedSpeciesTableRow rs : speciesObjectList) {
				int ourIndex = rs.getSpeciesObject().getNetworkFileIndex();
				if(indexesList.contains(ourIndex)) {
					speciesObjectList2.add(rs);
				}
			}
		}
		
		if(observableFilter != null) {		// go through each and set the correct multiplier
			Map<Integer, Integer> ogIndexMap = new HashMap<>();	// key is networkFileIndex, value is multiplicity
			ObservableGroup og = observableFilter.getObservableGroupObject();
			for(int i = 0; i < og.getListofSpecies().length; i++) {
				int networkFileIndex = og.getListofSpecies()[i].getNetworkFileIndex();
				int multiplicity = og.getSpeciesMultiplicity()[i];
				ogIndexMap.put(networkFileIndex, multiplicity);
			}
			for(GeneratedSpeciesTableRow gstr : speciesObjectList2) {
			
				int networkFileIndex = gstr.getSpeciesObject().getNetworkFileIndex();
				int multiplicity = ogIndexMap.get(networkFileIndex);
				gstr.setMultiplier(multiplicity+"");
			}
		}
		
		setData(speciesObjectList2);
		GuiUtils.flexResizeTableColumns(ownerTable);
	}
	
	private GeneratedSpeciesTableRow createTableRow(BNGSpecies species, int index, String multiplier, String originalName, String interactionLabel) {
		GeneratedSpeciesTableRow row = new GeneratedSpeciesTableRow(originalName, species, owner);
		row.setMultiplier(multiplier+" ");
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
	
	public Map<Integer, String> getNetworkFileIndexToNameMap() {
		return networkFileIndexToNameMap;
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
