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
import java.util.List;
import java.util.Set;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ElectricalTopology;
import cbit.vcell.model.Model.ElectricalTopologyListener;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class BioModelEditorStructureTableModel extends BioModelEditorRightSideTableModel<Structure> implements ElectricalTopologyListener {

	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_TYPE = 1;
	public final static int COLUMN_ELECTRICAL = 2;
	private static String[] columnNames = new String[] {"Name", "Type", "Electrical (Membrane Polarity)"};

	public BioModelEditorStructureTableModel(EditorScrollTable table) {
		super(table);
		setColumns(columnNames);
	}
	
	public Class<?> getColumnClass(int column) {
		switch (column){		
			case COLUMN_NAME:{
				return String.class;
			}
			case COLUMN_TYPE:{
				return String.class;
			}
			case COLUMN_ELECTRICAL:{
				return String.class;
			}
			}
		return Object.class;
	}

	protected List<Structure> computeData() {
		List<Structure> structureList = null;
		if (getModel() != null){
			Structure[] structures = getModel().getStructures();
			if (searchText == null || searchText.length() == 0) {
				structureList = Arrays.asList(structures);
			} else {
				structureList = new ArrayList<Structure>();
				StructureTopology structureTopology = getStructureTopology();
				String lowerCaseSearchText = searchText.toLowerCase();	
				for (Structure s : structures){
					if (s.getName().toLowerCase().contains(lowerCaseSearchText)
							|| s.getTypeName().toLowerCase().contains(lowerCaseSearchText)
							|| structureTopology.getParentStructure(s) != null && structureTopology.getParentStructure(s).getName().toLowerCase().contains(lowerCaseSearchText)
							|| s.getStructureSize().getName().toLowerCase().contains(lowerCaseSearchText)
							|| (s instanceof Membrane && 
									((structureTopology.getInsideFeature((Membrane)s) != null && structureTopology.getInsideFeature((Membrane)s).getName().toLowerCase().contains(lowerCaseSearchText))
									|| ((Membrane)s).getMembraneVoltage().getName().toLowerCase().contains(lowerCaseSearchText)))) {
						structureList.add(s);
					}					
				}
			}
		}
		return structureList;
	}

	public Object getValueAt(int row, int column) {
		if (getModel() == null) {
			return null;
		}
		try{
			Structure structure = getValueAt(row);
			if (structure != null) {
				switch (column) {
					case COLUMN_NAME: {
						return structure.getName();
					} 
					case COLUMN_TYPE: {
						return structure.getTypeName();
					}
					case COLUMN_ELECTRICAL: {
						String electricalOptions = "";
						if (structure instanceof Membrane) {
							Membrane membrane = (Membrane)structure;
							ElectricalTopology electricalTopology = getModel().getElectricalTopology();
							if (electricalTopology != null) {
								String posFeature = "unspecified compartment";
								if (electricalTopology.getPositiveFeature(membrane) != null) {
									posFeature = electricalTopology.getPositiveFeature(membrane).getName();
								}
								String negFeature = "unspecified compartment";
								if (electricalTopology.getNegativeFeature(membrane) != null) {
									negFeature = electricalTopology.getNegativeFeature(membrane).getName();
								}
								electricalOptions = posFeature + " (+)    " + negFeature + " (-)"; 
							}
						}
						return electricalOptions;
					} 
				}
			} else {
				if (column == COLUMN_NAME) {
					return ADD_NEW_HERE_TEXT;
				} 
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
		}
		return null;
	}

	private StructureTopology getStructureTopology() {
		return getModel().getStructureTopology();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column == COLUMN_NAME;
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL)){
			BioModel oldBioModel = (BioModel)evt.getOldValue();
			BioModel newBioModel = (BioModel)evt.getNewValue();
			if (oldBioModel!=null){
				oldBioModel.getModel().removeElectricalTopologyListener(this);
			}
			if (newBioModel!=null){
				newBioModel.getModel().addElectricalTopologyListener(this);
			}
		}
		if (evt.getSource() == getModel() && evt.getPropertyName().equals(Model.PROPERTY_NAME_STRUCTURES)) {
			Structure[] oldValue = (Structure[]) evt.getOldValue();
			if (oldValue != null) {
				for (Structure s : oldValue) {
					s.removePropertyChangeListener(this);
				}
			}
			Structure[] newValue = (Structure[]) evt.getNewValue();
			if (newValue != null) {
				for (Structure s : newValue) {
					s.addPropertyChangeListener(this);
				}
			}
			refreshData();
		} else if (evt.getSource() instanceof Structure) {
			fireTableRowsUpdated(0, getRowCount());
		}
	}

	public void setValueAt(Object value, int row, int column) {
		if (getModel() == null || value == null) {
			return;
		}
		try{
			Structure structure = getValueAt(row);
			if (structure != null) {
				switch (column) {
				case COLUMN_NAME: {
					String inputValue = (String)value;
					inputValue = inputValue.trim();
					if (!inputValue.equals(structure.getName())) {
						structure.setName(inputValue,true);
					}
					break;
				} 
				}
			} else {
				switch (column) {
				case COLUMN_NAME: {
					String inputValue = ((String)value);
					if (inputValue.equals(ADD_NEW_HERE_TEXT)) {
						return;
					}
					inputValue = inputValue.trim();
					Feature feature = getModel().createFeature();
					feature.setName(inputValue,true);
					break;
				} 
				}
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(ownerTable, e.getMessage(), e);
		}
	}

	@Override
	public boolean isSortable(int col) {
		return true;
	}
	
	@Override
	public Comparator<Structure> getComparator(final int col, final boolean ascending) {
		return new Comparator<Structure>() {
            public int compare(Structure o1, Structure o2) {
            	int scale = ascending ? 1 : -1;
                if (col==COLUMN_NAME){
					return scale * o1.getName().compareTo(o2.getName());
				} else if (col == COLUMN_TYPE) {
					return scale * o1.getTypeName().compareTo(o2.getTypeName());
				}
				return 0;
            }
		};
	}

	public String checkInputValue(String inputValue, int row, int column) {
		Structure structure = getValueAt(row);
		switch (column) {
		case COLUMN_NAME:
			if (structure == null || !structure.getName().equals(inputValue)) {
				if (getModel().getStructure(inputValue) != null) {
					return "Structure '" + inputValue + "' already exist!";
				}
			}
			break;
		}
		return null;
	}
	
	public SymbolTable getSymbolTable(int row, int column) {
		return null;
	}
	
	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(final int row, final int column) {
		return null;
	}

	public Set<String> getAutoCompletionWords(int row, int column) {
		return null;
	}

	@Override
	protected void bioModelChange(PropertyChangeEvent evt) {		
		super.bioModelChange(evt);
		
		BioModel oldValue = (BioModel)evt.getOldValue();
		if (oldValue != null) {
			for (Structure s : oldValue.getModel().getStructures()) {
				s.removePropertyChangeListener(this);
			}
		}
		BioModel newValue = (BioModel)evt.getNewValue();
		if (newValue != null) {
			for (Structure s : newValue.getModel().getStructures()) {
				s.addPropertyChangeListener(this);
			}
		}
	}
	
	@Override
	public int getRowCount() {
		return super.getRowCount();//getRowCountWithAddNew();
	}

	@Override
	public void electricalTopologyChanged(ElectricalTopology electricalTopology) {
		refreshData();
	}

}
