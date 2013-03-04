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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.model.ModelUnitSystem.ModelUnitSystemEntry;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class ModelUnitSystemTableModel extends BioModelEditorRightSideTableModel<ModelUnitSystemEntry> {
	public final static int COLUMN_DESCRIPTION = 0;
	public final static int COLUMN_UNIT = 1;
	public final static int COLUMN_CATEGORY = 2;
	
	private static String[] columnNames = new String[] {"Description", "Unit", "Category"};
	
	public ModelUnitSystemTableModel(EditorScrollTable table) {
		super(table);
		setColumns(columnNames);
		setData(computeData());		
	}
	
	@Override
	protected List<ModelUnitSystemEntry> computeData() {
		List<ModelUnitSystemEntry> modelUnitSystemEntryList = new ArrayList<ModelUnitSystemEntry>();
		if (getModel() != null) {
			modelUnitSystemEntryList.addAll(getModel().getUnitSystem().getModelUnitSystemEntries());
		}
		
		List<ModelUnitSystemEntry> searchList = null;
		if (searchText == null || searchText.length() == 0) {
			searchList = modelUnitSystemEntryList;
		} else {
			String lowerCaseSearchText = searchText.toLowerCase();
			searchList = new ArrayList<ModelUnitSystemEntry>();
			for (ModelUnitSystemEntry muse: modelUnitSystemEntryList) {					
				if (muse.getUnitSystemRole().getDescription().toLowerCase().contains(lowerCaseSearchText)
						|| muse.getVCUnitDefinition().getSymbol().toLowerCase().contains(lowerCaseSearchText)
						|| muse.getUnitSystemRole().getUnitCategory().getDescription().toLowerCase().contains(lowerCaseSearchText)
					) {					
					searchList.add(muse);
				}				
			}
		}
		return searchList;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		ModelUnitSystemEntry muse = getValueAt(rowIndex);
		switch(columnIndex) {
		case COLUMN_DESCRIPTION:
			return muse.getUnitSystemRole().getDescription();
		case COLUMN_UNIT:
			return muse.getVCUnitDefinition().getSymbolUnicode();
		case COLUMN_CATEGORY:
			return muse.getUnitSystemRole().getUnitCategory();
		}
		return null;
	}

	@Override
	protected Comparator<ModelUnitSystemEntry> getComparator(final int col, final boolean ascending) {
		return new Comparator<ModelUnitSystemEntry>() {

			public int compare(ModelUnitSystemEntry o1, ModelUnitSystemEntry o2) {
				int scale = ascending ? 1 : -1;
				switch (col){
				case COLUMN_DESCRIPTION:
					return scale * o1.getUnitSystemRole().getDescription().compareToIgnoreCase(o2.getUnitSystemRole().getDescription());
				case COLUMN_UNIT:
					return scale * o1.getVCUnitDefinition().getSymbol().compareToIgnoreCase(o2.getVCUnitDefinition().getSymbol());
				case COLUMN_CATEGORY:
					return scale * o1.getUnitSystemRole().getUnitCategory().getDescription().compareToIgnoreCase(o2.getUnitSystemRole().getUnitCategory().getDescription());
				}
				return 0;
			}
		};
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
}
