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
import java.util.Set;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Control;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.Entity;
import org.vcell.pathway.GroupObject;
import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.biomodel.BioModel;

@SuppressWarnings("serial")
public class PathwayModelTableModel extends VCellSortTableModel<BioPaxObject> implements PathwayListener {


	public static final int COLUMN_ENTITY = 0;
	private static final int COLUMN_TYPE = 1;
	private static final int COLUMN_LINKED_PHYSIOLOGY_OBJECTS = 2;
	private static final String[] columnNames = new String[] {"Pathway Entity", "Type", "Linked Physiology Objects"};	
	protected String searchText = null;
	private BioModel bioModel;

	public PathwayModelTableModel(ScrollTable table) {
		super(table, columnNames);
	}
	
	public void setBioModel(BioModel newValue){
		if (this.bioModel == newValue){
			return;
		}
		BioModel oldValue = bioModel;
		if (oldValue != null) {
			oldValue.getPathwayModel().removePathwayListener(this);
		}
		if (newValue != null) {
			newValue.getPathwayModel().addPathwayListener(this);
		}
		bioModel = newValue;
		refreshData();
	}
	
	public Class<?> getColumnClass(int iCol) {
		return String.class;
	}
	
	private String getLinkedModelObjectsDisplayText(BioPaxObject bioPaxObject) {
		Set<RelationshipObject> relationshipObjects = bioModel.getRelationshipModel().getRelationshipObjects(bioPaxObject);
		
		if (relationshipObjects.size() > 0) {
			StringBuilder text = new StringBuilder();
			for (RelationshipObject ro : relationshipObjects) {
				text.append(ro.getBioModelEntityObject().getTypeLabel() + ":" + ro.getBioModelEntityObject().getName() + "; ");
			}
			return text.toString();
		}
		return "";
	}
	
	public Object getValueAt(int iRow, int iCol) {
		BioPaxObject bioPaxObject = getValueAt(iRow);
		switch(iCol) {		
			case COLUMN_ENTITY:{
				return getLabel(bioPaxObject);
			}
			case COLUMN_TYPE:{
				return getType(bioPaxObject);
			}
			case COLUMN_LINKED_PHYSIOLOGY_OBJECTS: {
				return getLinkedModelObjectsDisplayText(bioPaxObject);
			}
			default:{
				return null;
			}
		}
	}
	
	public boolean isCellEditable(int iRow, int iCol) {
		return false;
	}
	
	public void setValueAt(Object valueNew, int iRow, int iCol) {
	}
	
	// generate the sortable table. Set up the functions for each column
	public Comparator<BioPaxObject> getComparator(final int col, final boolean ascending) {
		return new Comparator<BioPaxObject>() {
		    public int compare(BioPaxObject o1, BioPaxObject o2){
		    	int scale = ascending ? 1 : -1;
		    	if (col == COLUMN_ENTITY) {// only sortable on entity column
		    		return scale * getLabel(o1).compareToIgnoreCase(getLabel(o2));
		    	} else if (col == COLUMN_TYPE) {
		    		return scale * getType(o1).compareToIgnoreCase(getType(o2));
		    	} else if (col == COLUMN_LINKED_PHYSIOLOGY_OBJECTS) {
		    		return scale * getLinkedModelObjectsDisplayText(o1).compareToIgnoreCase(getLinkedModelObjectsDisplayText(o2)); 
		    	}

		    	return 0;
		    }
		};
	}
	
	private String getType(BioPaxObject bpObject){
		return bpObject.getTypeLabel();
	}
	
	private static String getLabel(BioPaxObject bpObject){
		if (bpObject instanceof Conversion){
			Conversion conversion =(Conversion)bpObject;
			if (conversion.getName().size()>0){
				return conversion.getName().get(0);
			}else{
				return conversion.getIDShort();
			}
		}else if (bpObject instanceof PhysicalEntity){
			PhysicalEntity physicalEntity =(PhysicalEntity)bpObject;
			if (physicalEntity.getName().size()>0){
				return physicalEntity.getName().get(0);
			}else{
				return physicalEntity.getIDShort();
			}
		}else if (bpObject instanceof GroupObject){
			return ((Entity)bpObject).getName().get(0);
		}else{
			return bpObject.getIDShort();
		}
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
		ArrayList<BioPaxObject> bioPaxObjectList = null;
		if (bioModel != null) {			
			bioPaxObjectList = new ArrayList<BioPaxObject>();
			if (searchText == null || searchText.length() == 0) {
				for (BioPaxObject bpObject : bioModel.getPathwayModel().getBiopaxObjects()){
					if ((bpObject instanceof Control)) {
						continue;
					}
					bioPaxObjectList.add(bpObject);
				}
//				bioPaxObjectList.addAll(bioModel.getPathwayModel().getBiopaxObjects());
			} else { 
				String lowerCaseSearchText = searchText.toLowerCase();			
				for (BioPaxObject bpObject : bioModel.getPathwayModel().getBiopaxObjects()){
					if ((bpObject instanceof Control)) {
						continue;
					}
					if ((getLabel(bpObject).toLowerCase().contains(lowerCaseSearchText)				
						|| getType(bpObject).toLowerCase().contains(lowerCaseSearchText) 
						|| getLinkedModelObjectsDisplayText(bpObject).toLowerCase().contains(lowerCaseSearchText))) {
						bioPaxObjectList.add(bpObject);
					}
				}
			}
		}
		setData(bioPaxObjectList);
	}

	public void pathwayChanged(PathwayEvent event) {
		refreshData();
	}
}
