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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Entity;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class BioModelEditorSpeciesTableModel extends BioModelEditorRightSideTableModel<SpeciesContext> {
	
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_STRUCTURE = 1;	
	public final static int COLUMN_DEPICTION = 2;	
	public final static int COLUMN_LINK = 3;
	public final static int COLUMN_DEFINITION = 4;	
	private static String[] columnNames = new String[] {"Name","Structure","Depiction","Link","BioNetGen Definition"};

	public BioModelEditorSpeciesTableModel(EditorScrollTable table) {
		super(table);
		setColumns(columnNames);
	}
	
	public Class<?> getColumnClass(int column) {
		switch (column){
		
			case COLUMN_NAME:{
				return String.class;
			}
			case COLUMN_LINK:{
				return BioPaxObject.class;
			}
			case COLUMN_DEPICTION:{
				return SpeciesPattern.class;
			}
			case COLUMN_DEFINITION:{
				return SpeciesPattern.class;
			}
			case COLUMN_STRUCTURE:{
				return Structure.class;
			}
		}
		return Object.class;
	}

	protected ArrayList<SpeciesContext> computeData() {
		ArrayList<SpeciesContext> speciesContextList = new ArrayList<SpeciesContext>();
		if (getModel() != null) {
			if (searchText == null || searchText.length() == 0) {
				speciesContextList.addAll(Arrays.asList(getModel().getSpeciesContexts()));
			} else {
				String lowerCaseSearchText = searchText.toLowerCase();
					for (SpeciesContext s : getModel().getSpeciesContexts()){
						boolean bMatchRelationshipObj = false;
						HashSet<RelationshipObject> relObjsHash = bioModel.getRelationshipModel().getRelationshipObjects(s);
						for(RelationshipObject relObj:relObjsHash){
							if(relObj.getBioPaxObject() instanceof Entity){
								if(((Entity)relObj.getBioPaxObject()).getName().get(0).toLowerCase().contains(lowerCaseSearchText)){
									bMatchRelationshipObj = true;
									break;
								}
							}
						}
					if (bMatchRelationshipObj || s.getName().toLowerCase().contains(lowerCaseSearchText)		
						|| s.getStructure().getName().toLowerCase().contains(lowerCaseSearchText)) {
						speciesContextList.add(s);
					}
				}
			}
		}
		return speciesContextList;
	}

	public Object getValueAt(int row, int column) {
		if (getModel() == null) {	
			return null;
		}
		try{
			SpeciesContext speciesContext = getValueAt(row);
			if (speciesContext != null) {	// row with existing species
				switch (column) {
					case COLUMN_NAME: {
						return speciesContext.getName();
					} 
					case COLUMN_LINK: {
						HashSet<RelationshipObject> relObjsHash = bioModel.getRelationshipModel().getRelationshipObjects(speciesContext);
						if(relObjsHash != null && relObjsHash.size() > 0){
							return relObjsHash.iterator().next().getBioPaxObject();
						}
						return null;
					} 
					case COLUMN_DEPICTION: {
						return speciesContext.getSpeciesPattern();
					} 
					case COLUMN_DEFINITION: {
						return speciesContext.getSpeciesPattern();
					} 
					case COLUMN_STRUCTURE: {
						return speciesContext.getStructure();
					} 
				}
			} else {			// empty row
				if (column == COLUMN_NAME) {
					return ADD_NEW_HERE_TEXT;
				} 
			}
			return null;
		} catch(Exception e){
			e.printStackTrace(System.out);
			return null;
		}
	}

	public boolean isCellEditable(int row, int column) {
		switch (column) {
		case COLUMN_NAME:
			return true;
		case COLUMN_DEFINITION:
			SpeciesContext sc = getValueAt(row);
			if (sc == null) {
				return false;
			}
			SpeciesPattern sp = sc.getSpeciesPattern();
			if(sp == null) {
				return true;	// we can edit a species pattern if none is present
			}
			final List<MolecularTypePattern> mtpList = sp.getMolecularTypePatterns();
			for(MolecularTypePattern mtp : mtpList) {
				MolecularType mt = mtp.getMolecularType();
				if(mt.getComponentList().size() != 0) {
					return false;
				}
			}
			return true;
			
		default:
			return false;
		}
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getSource() == bioModel.getModel()) {
			if (evt.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_OBSERVABLE_LIST)) {
				refreshData();
				
//				List<RbmObservable> oldValue = (List<RbmObservable>) evt.getOldValue();
//				if (oldValue != null) {
//					for (RbmObservable observable : oldValue) {
//						observable.removePropertyChangeListener(this);
//						SpeciesPattern speciesPattern = observable.getSpeciesPattern(0);
//						RbmUtils.removePropertyChangeListener(speciesPattern, this);
//					}
//				}
//				List<RbmObservable> newValue = (List<RbmObservable>) evt.getNewValue();
//				if (newValue != null) {
//					for (RbmObservable observable : newValue) {
//						observable.addPropertyChangeListener(this);
//						SpeciesPattern speciesPattern = observable.getSpeciesPattern(0);
//						RbmUtils.addPropertyChangeListener(speciesPattern, this);							
//					}
//				}
			} else if(evt.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST)) {
				refreshData();		// we need this?
			} else if (evt.getPropertyName().equals(Model.PROPERTY_NAME_STRUCTURES)) {
				//updateStructureComboBox();
			} else if (evt.getPropertyName().equals(Model.PROPERTY_NAME_SPECIES_CONTEXTS)) {
				SpeciesContext[] oldValue = (SpeciesContext[]) evt.getOldValue();
				if (oldValue != null) {
					for (SpeciesContext sc : oldValue) {
						sc.removePropertyChangeListener(this);
					}
				}
				SpeciesContext[] newValue = (SpeciesContext[]) evt.getNewValue();
				if (newValue != null) {
					for (SpeciesContext sc : newValue) {
						sc.addPropertyChangeListener(this);
					}
				}
				refreshData();
			}
		} else if (evt.getSource() instanceof SpeciesContext) {
			// we update the row no matter what property change event was fired by the speciesContext
			SpeciesContext speciesContext = (SpeciesContext) evt.getSource();
			int changeRow = getRowIndex(speciesContext);
			if (changeRow >= 0) {
				fireTableRowsUpdated(changeRow, changeRow);
			}
		}
	}
	
	public void setValueAt(Object value, int row, int column) {
		if (getModel() == null || value == null) {
			return;
		}
		try{
			SpeciesContext speciesContext = getValueAt(row);
			if (speciesContext != null) {	// row with existing species
				switch (column) {
				case COLUMN_NAME: {
					String inputValue = ((String)value);
					inputValue = inputValue.trim();
					if (inputValue.length() == 0) {
						return;
					}
					speciesContext.setName(inputValue);
					break;
				} 
				case COLUMN_STRUCTURE: {
					// value might be null because no popup in JCombo editor ui.
					// the first time.
					Structure structure = (Structure)value;
					speciesContext.setStructure(structure);
					break;
				}
				case COLUMN_DEFINITION:
					String inputValue = ((String)value);
					inputValue = inputValue.trim();
					if (inputValue.length() == 0) {
						speciesContext.setSpeciesPattern(null);
						return;
					}
					SpeciesPattern sp = RbmUtils.parseSpeciesPattern(inputValue, bioModel.getModel());
					speciesContext.setSpeciesPattern(sp);
					break;
				}
			} else {				// empty row being edited - Deprecated, we now must use the "New Species" button
				switch (column) {
				case COLUMN_NAME:	// only "name" column is editable
					String inputValue = ((String)value);
					if (inputValue.length() == 0 || inputValue.equals(ADD_NEW_HERE_TEXT)) {
						return;
					}
					inputValue = inputValue.trim();
					SpeciesContext freeSpeciesContext = getModel().createSpeciesContext(getModel().getStructures()[0]);
					freeSpeciesContext.setName(inputValue);
					break;
				}
			}
		} catch(Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(ownerTable, e.getMessage(), e);
		}
	}

	@Override
	public boolean isSortable(int col) {
		switch (col) {
		case COLUMN_LINK:
			return false;
		default:
			return true;	// sortable by all
		}
	}
	
	@Override
	public Comparator<SpeciesContext> getComparator(final int col, final boolean ascending) {
		return new Comparator<SpeciesContext>() {
            public int compare(SpeciesContext o1, SpeciesContext o2) {
            	int scale = ascending ? 1 : -1;
            	
            	switch (col) {
            	
            	case COLUMN_NAME:
					return scale * o1.getName().compareTo(o2.getName());
            	case COLUMN_STRUCTURE:
					return scale * o1.getStructure().getName().compareTo(o2.getStructure().getName());
            	case COLUMN_DEPICTION:
            		if(o1.hasSpeciesPattern() && o2.hasSpeciesPattern()) {
						Integer i1 = o1.getSpeciesPattern().getMolecularTypePatterns().size();
						Integer i2 = o2.getSpeciesPattern().getMolecularTypePatterns().size();
						if(scale * i1.compareTo(i2) == 0) {
							// if same number of molecule we try to sort by number of sites of the mt
							i1 = 0;
							i2 = 0;
							for(MolecularTypePattern mtp : o1.getSpeciesPattern().getMolecularTypePatterns()) {
								i1 += mtp.getMolecularType().getComponentList().size();
							}
							for(MolecularTypePattern mtp : o2.getSpeciesPattern().getMolecularTypePatterns()) {
								i2 += mtp.getMolecularType().getComponentList().size();
							}
							if(i1 == i2) {	// equal number of molecules and sites, sort by name as a last resort
								return scale * o1.getName().compareToIgnoreCase(o2.getName());
							}
							return scale * i1.compareTo(i2);
						} else {
							return scale * i1.compareTo(i2);
						}
            		} else if(!o1.hasSpeciesPattern() && o2.hasSpeciesPattern()) {
                		return scale;
                	} else if(o1.hasSpeciesPattern() && !o2.hasSpeciesPattern()) {
                		return -scale;
            		} else {	// both species have null species pattern, just sort by name
            			return scale * o1.getName().compareToIgnoreCase(o2.getName());
            		}
            	case COLUMN_DEFINITION:
            		if(o1.hasSpeciesPattern() && o2.hasSpeciesPattern()) {
            			return scale * o1.getSpeciesPattern().toString().compareToIgnoreCase(o2.getSpeciesPattern().toString());
            		} else if(!o1.hasSpeciesPattern() && o2.hasSpeciesPattern()) {
                		return scale;
                	} else if(o1.hasSpeciesPattern() && !o2.hasSpeciesPattern()) {
                		return -scale;
            		} else {	// both species have null species pattern, just sort by name
            			return scale * o1.getName().compareToIgnoreCase(o2.getName());
            		}
            	default:
            		return 0;
            	
            	}
            }
		};
	}

	public String checkInputValue(String inputValue, int row, int column) {
		SpeciesContext speciesContext = getValueAt(row);
		String errMsg = null;
		switch (column) {
		case COLUMN_NAME:
			if (speciesContext == null || !speciesContext.getName().equals(inputValue)) {
				if (getModel().getSpeciesContext(inputValue) != null) {
					errMsg = "Species '" + inputValue + "' already exists!";
					errMsg += VCellErrorMessages.PressEscToUndo;
					errMsg = "<html>" + errMsg + "</html>";
					return errMsg;
				}
			}
			break;
		case COLUMN_STRUCTURE:
			if (getModel().getStructure(inputValue) == null) {
				errMsg = "Structure '" + inputValue + "' does not exist!";
				errMsg += VCellErrorMessages.PressEscToUndo;
				errMsg = "<html>" + errMsg + "</html>";
				return errMsg;
			}
			break;
		case COLUMN_DEFINITION:
			try {
				inputValue = inputValue.trim();
				if (inputValue.length() > 0) {
					// parsing will throw appropriate exception if molecular type or component don't exist
					SpeciesPattern spThis = RbmUtils.parseSpeciesPattern(inputValue, bioModel.getModel());	// our change
					// here we can restrict what the user can do
					for(MolecularTypePattern mtpThis : spThis.getMolecularTypePatterns()) {
						MolecularType mtThis = mtpThis.getMolecularType();
						for(MolecularComponent mcThis : mtThis.getComponentList()) {
							// we check that each component is present in the molecular type pattern (as component pattern)
							if(mtpThis.getMolecularComponentPattern(mcThis) == null) {		// not found
								errMsg = "All " + MolecularComponent.typeName + "s in the " + mtThis.getDisplayType() + " definition must be present. Missing: " + mcThis.getName();
								errMsg += VCellErrorMessages.PressEscToUndo;
								errMsg = "<html>" + errMsg + "</html>";
								return errMsg;
							} else if(mtpThis.getMolecularComponentPattern(mcThis).isImplied()) {
								errMsg = "All " + MolecularComponent.typeName + "s in the " + mtThis.getDisplayType() + " definition must be present. Missing: " + mcThis.getName();
								errMsg += VCellErrorMessages.PressEscToUndo;
								errMsg = "<html>" + errMsg + "</html>";
								return errMsg;
							} else {	// now need to also check the states
								if(mcThis.getComponentStateDefinitions().size() == 0) {
									continue;	// nothing to do if the molecular component has no component state definition
												// note that we raise exception in parseSpeciesPattern() if we attempt to use an undefined state
												// so no need to check that here
								}
								boolean found = false;
								for(ComponentStateDefinition csThis : mcThis.getComponentStateDefinitions()) {
									MolecularComponentPattern mcpThis = mtpThis.getMolecularComponentPattern(mcThis);
									if((mcpThis.getComponentStatePattern() == null) || mcpThis.getComponentStatePattern().isAny()) {
										break;		// no component state pattern means no state, there's no point to check for this component again
													// we get out of the for and complain that we found no matching state
									}
									if(csThis.getName().equals(mcpThis.getComponentStatePattern().getComponentStateDefinition().getName())) {
										found = true;
									}
								}
								if(found == false) {	// we should have found a matching state for the molecular component pattern
									errMsg = MolecularComponent.typeName + " " + mcThis.getDisplayName() + " of " + mtThis.getDisplayType() + " " + mtThis.getDisplayName() + " must be in one of the following states: ";
									for(int i=0; i<mcThis.getComponentStateDefinitions().size(); i++) {
										ComponentStateDefinition csThis = mcThis.getComponentStateDefinitions().get(i);
										errMsg += csThis.getName();
										if(i < mcThis.getComponentStateDefinitions().size()-1) {
											errMsg += ", ";
										}
									}
									errMsg += VCellErrorMessages.PressEscToUndo;
									errMsg = "<html>" + errMsg + "</html>";
									return errMsg;
								}
							}
						}
					}
				}
			} catch (Exception ex) {
				errMsg = ex.getMessage();
				errMsg += VCellErrorMessages.PressEscToUndo;
				errMsg = "<html>" + errMsg + "</html>";
				return errMsg;
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
		if (column == COLUMN_STRUCTURE) {
			Set<String> words = new HashSet<String>();
			for (Structure s : getModel().getStructures()) {
				words.add(s.getName());
			}
			return words;
		}
		return null;
	}
	
	@Override
	protected void bioModelChange(PropertyChangeEvent evt) {		
		super.bioModelChange(evt);
//		ownerTable.getColumnModel().getColumn(COLUMN_STRUCTURE).setCellEditor(getStructureComboBoxEditor());
//		updateStructureComboBox();
		
		BioModel oldValue = (BioModel)evt.getOldValue();
		if (oldValue != null) {
			for (SpeciesContext sc : oldValue.getModel().getSpeciesContexts()) {
				sc.removePropertyChangeListener(this);
			}
		}
		BioModel newValue = (BioModel)evt.getNewValue();
		if (newValue != null) {
			for (SpeciesContext sc : newValue.getModel().getSpeciesContexts()) {
				sc.addPropertyChangeListener(this);
			}
		}
	}
	
	@Override
	public int getRowCount() {
		if (bioModel == null || bioModel.getModel().getNumStructures() == 1) {
			return getRowCountWithAddNew();
		}
		return super.getRowCount();
	}
}
