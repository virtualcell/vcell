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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.model.rbm.FakeSeedSpeciesInitialConditionsParameter;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.util.gui.AutoCompleteTableModel;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.bionetgen.ObservableGroup;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class ObservablesGroupTableModel extends VCellSortTableModel<ObservablesGroupTableRow> 
	implements  PropertyChangeListener, AutoCompleteTableModel{

	public static final int colCount = 5;
	public static final int iColOriginalName = 0;
	public static final int iColStructure = 1;
	public static final int iColDepiction = 2;
	public static final int iColDefinition = 3;
	public static final int iColExpression = 4;
	
	// filtering variables 
	protected static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	protected String searchText = null;

	private ObservableGroup[] observabless;
	private Model model;
	
	private ArrayList<ObservablesGroupTableRow> allObservablesList;

	private final NetworkConstraintsPanel owner;
	private final GeneratedSpeciesTableModel sibling;

	protected transient java.beans.PropertyChangeSupport propertyChange;

	public ObservablesGroupTableModel(EditorScrollTable table, NetworkConstraintsPanel owner, GeneratedSpeciesTableModel sibling) {
		super(table, new String[] {"Name", "Structure", "Depiction", "BioNetGen Definition", "Expression"});
		this.owner = owner;
		this.sibling = sibling;
		setMaxRowsPerPage(1000);
	}
	
	public Class<?> getColumnClass(int iCol) {
		switch (iCol) {		
			case iColOriginalName: {
				return String.class;
			}case iColStructure: {
				return String.class;
			}case iColDepiction: {
				return Object.class;
			}case iColDefinition: {
				return String.class;
			}case iColExpression: {
				return String.class;
			}
		}
	return Object.class;
	}
	
	public Object getValueAt(int iRow, int iCol) {
		ObservablesGroupTableRow observablesTableRow = getValueAt(iRow);
		String obsName = observablesTableRow.getObservableGroupObject().getObservableGroupName();
		RbmObservable obs = observablesTableRow.getObservable(obsName);
		String obsDefinition = ObservablesGroupTableRow.toBnglString(obs);
		switch(iCol) {
			case iColOriginalName:
				return obsName;
			case iColStructure:{
				if(obsDefinition.startsWith("@") && obsDefinition.contains(":")) {
					String structName = obsDefinition.substring(1, obsDefinition.indexOf(":"));
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
				return obsDefinition;
			case iColExpression:
				String exp;
				BNGSpecies[] speciesList = observablesTableRow.getObservableGroupObject().getListofSpecies();
				if(speciesList == null || speciesList.length == 0) {
					exp = "<html><font color=\"red\">No generated species</html>";
				} else {
					exp = getExpressionAsString(observablesTableRow.getObservableGroupObject());
				}
				return exp;
			default:
				return null;
		}
	}
	
	private String getExpressionAsString(ObservableGroup og) {
		String ret = "";
		BNGSpecies[] listofSpecies = og.getListofSpecies();
		int[] speciesMultiplicity = og.getSpeciesMultiplicity();
		Map<Integer, String> networkFileIndexToNameMap = sibling.getNetworkFileIndexToNameMap();
		
		for (int i = 0; i < listofSpecies.length; i++) {
			int networkFileIndex = listofSpecies[i].getNetworkFileIndex();
			String name = networkFileIndexToNameMap.get(listofSpecies[i].getNetworkFileIndex());
			
			if (i == listofSpecies.length-1) {
				if (speciesMultiplicity[i] == 1) {
					ret = ret + name;
				} else {
					ret = ret + speciesMultiplicity[i] + "*" + name;
				}
			} else {
				if (speciesMultiplicity[i] == 1) {
					ret = ret + name + ", ";
				} else {
					ret = ret + speciesMultiplicity[i] + "*" + name + ", ";
				}
			}
		}
		return ret;
	}
	
	public boolean isCellEditable(int iRow, int iCol) {
		return false;
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
	
	public Comparator<ObservablesGroupTableRow> getComparator(final int col, final boolean ascending) {
		final int scale = ascending ? 1 : -1;
		return new Comparator<ObservablesGroupTableRow>() {
		    public int compare(ObservablesGroupTableRow o1, ObservablesGroupTableRow o2) {
		    	String n1 = o1.getObservableGroupObject().getObservableGroupName();
		    	String n2 = o2.getObservableGroupObject().getObservableGroupName();
				switch (col) {
				case iColOriginalName:
					return scale * n1.compareToIgnoreCase(n2);
				case iColStructure:
					String es1 = ObservablesGroupTableRow.toBnglString(o1.getObservable(n1));
					String es2 = ObservablesGroupTableRow.toBnglString(o2.getObservable(n2));
					if(es1.startsWith("@") && es1.contains(":")) {		// no point to check es2 as well
						String sn1 = es1.substring(1, es1.indexOf(":"));
						String sn2 = es2.substring(1, es2.indexOf(":"));
						return scale * sn1.compareToIgnoreCase(sn2);
					} else {
						return 0;	// we should be here only if we have one single structure, so nothing to sort
					}
				}
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
		allObservablesList = new ArrayList<ObservablesGroupTableRow>();
		
		// if bng fails to produce even one species for an observable, no observable group will be created
		// we'll produce ourselves an "empty" observable group for each such observable
		List<String> rbmObsList = new ArrayList<>();
		List<String> obsGroupList = new ArrayList<>();
		for(RbmObservable o : getModel().getRbmModelContainer().getObservableList()) {
			rbmObsList.add(o.getName());
		}
		for(ObservableGroup og : observabless) {
			obsGroupList.add(og.getObservableGroupName());
		}
		rbmObsList.removeAll(obsGroupList);		// intersect
		for(String o : rbmObsList) {
			ObservableGroup og = new ObservableGroup(o, new BNGSpecies[0], new int[0]);	// empty observable group
			ObservablesGroupTableRow newRow = createTableRow(og, 0);	// all will have index 0
			allObservablesList.add(newRow);
		}
		
		for(int i = 0; i<observabless.length; i++) {
			ObservableGroup og = observabless[i];
			ObservablesGroupTableRow newRow = createTableRow(og, i+1);
			allObservablesList.add(newRow);
		}
		setData(allObservablesList);
		GuiUtils.flexResizeTableColumns(ownerTable);
	}
	
	private ObservablesGroupTableRow createTableRow(ObservableGroup og, int index) {
		ObservablesGroupTableRow row = new ObservablesGroupTableRow(og, index+" ", owner);
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
	
	public void setData(Model model, ObservableGroup[] observabless) {
		if (this.model == model && this.observabless == observabless) {
			return;
		}
		this.model = model;
		this.observabless = observabless;
		refreshData();
	}
	public ArrayList<ObservablesGroupTableRow> getTableRows() {
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
