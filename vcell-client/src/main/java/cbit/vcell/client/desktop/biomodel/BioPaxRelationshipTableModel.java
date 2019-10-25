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

/*   EntitySelectionTableModel  --- by Oliver Ruebenacker, UCHC --- November (?) 2008 to December 2009
 *   Model for table to select entities from an SBBox
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.vcell.model.rbm.MolecularType;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.relationship.RelationshipEvent;
import org.vcell.relationship.RelationshipListener;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.BioModelEntityObject;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;

@SuppressWarnings("serial")
public class BioPaxRelationshipTableModel extends VCellSortTableModel<BioPaxRelationshipTableRow> implements PathwayListener, RelationshipListener {


	public static final int colCount = 3;
	public static final int iColSelected = 0;
	public static final int iColEntity = 1;
	public static final int iColType = 2;
	
	// filtering variables 
	protected static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	protected String searchText = null;
	//done

	private BioModel bioModel;
	private BioPaxObject bioPaxObject;
	private boolean bShowLinkOnly = false;

	public BioPaxRelationshipTableModel(ScrollTable table) {
		super(table, new String[] {"Link", "Physiology Entity", "Type"});
	}
	
	public Class<?> getColumnClass(int iCol) {
		if(iCol == iColSelected) { return Boolean.class; }
		else { return String.class; }
	}
	
	public Object getValueAt(int iRow, int iCol) {
		BioPaxRelationshipTableRow entitySelectionTableRow = getValueAt(iRow);
		BioModelEntityObject bmObject = entitySelectionTableRow.getBioModelEntityObject();
		switch(iCol) {		
			case iColSelected:{
				return entitySelectionTableRow.selected();
			}
			case iColEntity:{
				return bmObject.getName();
			}
			case iColType:{
				return bmObject.getTypeLabel();
			}
			default:{
				return null;
			}
		}
	}
	
	public boolean isCellEditable(int iRow, int iCol) {
		return iCol == iColSelected;
	}
	
	public void setValueAt(Object valueNew, int iRow, int iCol) {
		try {
			if(valueNew instanceof Boolean && iCol == iColSelected) {
				BioPaxRelationshipTableRow entitySelectionTableRow = getValueAt(iRow);
				if ((Boolean)valueNew) { // if the row is checked, then add the link to relationshipModel
					RelationshipObject reObject = new RelationshipObject(entitySelectionTableRow.getBioModelEntityObject(), bioPaxObject);
					bioModel.getRelationshipModel().addRelationshipObject(reObject);
				} else {// if the row is unchecked and the link is in the relationshipModel, 
					   // then remove the link from the relationshipModel
					for(RelationshipObject re: bioModel.getRelationshipModel().getRelationshipObjects()){
						if (re.getBioPaxObject() == bioPaxObject
								&& re.getBioModelEntityObject() == entitySelectionTableRow.getBioModelEntityObject()){
							bioModel.getRelationshipModel().removeRelationshipObject(re);
							return;
						}
					}
				}			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// generate the sortable table. Set up the functions for each column
	public Comparator<BioPaxRelationshipTableRow> getComparator(final int col, final boolean ascending) {
		return new Comparator<BioPaxRelationshipTableRow>() {
		    public int compare(BioPaxRelationshipTableRow o1, BioPaxRelationshipTableRow o2){
		    	int scale = ascending ? 1 : -1;
		    	if (col == iColSelected) {
		    		return scale * o1.selected().compareTo(o2.selected());
		    	} else 

		    	if (col == iColEntity) {// only sortable on entity column
		    		return scale * o1.getBioModelEntityObject().getName().compareToIgnoreCase(o2.getBioModelEntityObject().getName());
		    	} else 
		    		
		    	if (col == iColType) {
		    		return scale * o1.getBioModelEntityObject().getTypeLabel().compareToIgnoreCase(o2.getBioModelEntityObject().getTypeLabel());
		    	}

		    	return 0;
		    }
		};
	}
	
	// filtering functions
	public void setSearchText(String newValue) {
		if (searchText == newValue) {
			return;
		}
		searchText = newValue;
		refreshData();
	}

	private void refreshData() {
		ArrayList<BioPaxRelationshipTableRow> entityRows = new ArrayList<BioPaxRelationshipTableRow>();
		if (bioModel != null) {		
			HashSet<RelationshipObject> relationshipObjects = null;
			if (bioPaxObject != null) {
				relationshipObjects = bioModel.getRelationshipModel().getRelationshipObjects(bioPaxObject);
			}
			List<BioPaxRelationshipTableRow> allEntityRows = new ArrayList<BioPaxRelationshipTableRow>();
			if (bioPaxObject instanceof Conversion) {
				for (ReactionStep rs : bioModel.getModel().getReactionSteps()){
					BioPaxRelationshipTableRow entityRow = new BioPaxRelationshipTableRow(rs);
					if (relationshipObjects != null) {
						for (RelationshipObject ro : relationshipObjects) {
							if (ro.getBioModelEntityObject() == rs) {
								entityRow.setSelected(true);
								break;
							}
						}
					}
					if (!bShowLinkOnly || entityRow.selected) {
						allEntityRows.add(entityRow);
					}
				}
				for (ReactionRule rr : bioModel.getModel().getRbmModelContainer().getReactionRuleList()){
					BioPaxRelationshipTableRow entityRow = new BioPaxRelationshipTableRow(rr);
					if (relationshipObjects != null) {
						for (RelationshipObject ro : relationshipObjects) {
							if (ro.getBioModelEntityObject() == rr) {
								entityRow.setSelected(true);
								break;
							}
						}
					}
					if (!bShowLinkOnly || entityRow.selected) {
						allEntityRows.add(entityRow);
					}
				}
			} else if (bioPaxObject instanceof PhysicalEntity){
				for (SpeciesContext rs : bioModel.getModel().getSpeciesContexts()){
					BioPaxRelationshipTableRow entityRow = new BioPaxRelationshipTableRow(rs);
					if (relationshipObjects != null) {
						for (RelationshipObject ro : relationshipObjects) {
							if (ro.getBioModelEntityObject() == rs) {
								entityRow.setSelected(true);
								break;
							}
						}
					}
					if (!bShowLinkOnly || entityRow.selected) {
						allEntityRows.add(entityRow);
					}
				}
				for(MolecularType mt : bioModel.getModel().getRbmModelContainer().getMolecularTypeList()) {
					BioPaxRelationshipTableRow entityRow = new BioPaxRelationshipTableRow(mt);
					if (relationshipObjects != null) {
						for (RelationshipObject ro : relationshipObjects) {
							if (ro.getBioModelEntityObject() == mt) {
								entityRow.setSelected(true);
								break;
							}
						}
					}
					if (!bShowLinkOnly || entityRow.selected) {
						allEntityRows.add(entityRow);
					}
				}
			}
			if (searchText == null || searchText.length() == 0) {
				entityRows.addAll(allEntityRows);
			} else {
				for (BioPaxRelationshipTableRow rs : allEntityRows){
					BioModelEntityObject object = rs.getBioModelEntityObject();
					String lowerCaseSearchText = searchText.toLowerCase();
					if (object.getName().toLowerCase().contains(lowerCaseSearchText)
						|| object.getTypeLabel().toLowerCase().contains(lowerCaseSearchText) ) {
						entityRows.add(rs);
					}
				}
			}
		}
		setData(entityRows);
		GuiUtils.flexResizeTableColumns(ownerTable);
	}

	public void setBioModel(BioModel newValue) {
		if (bioModel == newValue) {
			return;
		}
		BioModel oldValue = bioModel;
		if (oldValue != null) {
			oldValue.getPathwayModel().removePathwayListener(this);
			oldValue.getRelationshipModel().removeRelationShipListener(this);
		}
		bioModel = newValue;
		if (newValue != null) {
			newValue.getPathwayModel().addPathwayListener(this);
			newValue.getRelationshipModel().addRelationShipListener(this);
		}
	}

	public void setBioPaxObject(BioPaxObject newValue) {
		if (bioPaxObject == newValue) {
			return;
		}
		bioPaxObject = newValue;
		refreshData();
	}

	public void pathwayChanged(PathwayEvent event) {
		refreshData();		
	}
	
	public void setShowLinkOnly(boolean newValue) {
		if (this.bShowLinkOnly == newValue) {
			return;
		}
		bShowLinkOnly = newValue;
		refreshData();
	}

	public void relationshipChanged(RelationshipEvent event) {
		if (event.getRelationshipObject() == null || event.getRelationshipObject().getBioPaxObject() == bioPaxObject) {
			refreshData();
		}
	}
	
}
