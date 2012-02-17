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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sbpax.schemas.util.SBPAX3Util;
import org.vcell.pathway.BioPAXUtil;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Control;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.Interaction;
import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.pathway.sbpax.SBMeasurable;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.biomodel.BioModel;

@SuppressWarnings("serial")
public class PathwayTableModel extends VCellSortTableModel<BioPaxObject> implements PathwayListener {


	public static final int colCount = 3;
	public static final int iColEntity = 0;
	public static final int iColType = 1;
	public static final int iColImported = 2;
	
	// filtering variables 
	protected String searchText = null;
	//done

	private BioModel bioModel = null;
	private Map<BioPaxObject, Boolean> bioPaxObjectImportedMap = new HashMap<BioPaxObject, Boolean>();
	private PathwayModel pathwayModel;

	public PathwayTableModel(ScrollTable table) {
		super(table, new String[] {"Entity Name", "Type", "Imported?"});
	}
	
	public void setPathwayModel(PathwayModel newValue){
		if (this.pathwayModel == newValue){
			return;
		}
		PathwayModel oldValue = pathwayModel;
		if (oldValue != null) {
			oldValue.removePathwayListener(this);
		}
		if (newValue != null) {
			newValue.addPathwayListener(this);
		}
		this.pathwayModel = newValue;
		refreshData();
	}
	
	public Class<?> getColumnClass(int iCol) {
		if(iCol == iColImported) { return Boolean.class; }
		else { return String.class; }
	}
	
	public Object getValueAt(int iRow, int iCol) {
		BioPaxObject bpObject = getValueAt(iRow);
		switch(iCol) {		
			case iColImported:{
				return bioPaxObjectImportedMap.get(bpObject);
			}
			case iColEntity:{
				return getLabel(bpObject);
			}
			case iColType:{
				return getType(bpObject);
			}
			default:{
				return null;
			}
		}
	}
	
	// generate the sortable table. Set up the functions for each column
	public Comparator<BioPaxObject> getComparator(final int col, final boolean ascending) {
		return new Comparator<BioPaxObject>() {
		    public int compare(BioPaxObject o1, BioPaxObject o2){
		    	if (col == iColImported) {
		    		int c  = bioPaxObjectImportedMap.get(o1).compareTo(bioPaxObjectImportedMap.get(o2));
		    		return ascending ? c : -c;
		    	} else 

		    	if (col == iColEntity) {// only sortable on entity column
		    		int c  = getLabel(o1).compareToIgnoreCase(getLabel(o2));
		    		return ascending ? c : -c;
		    	} else 
		    		
		    	if (col == iColType) {
		    		int c  = getType(o1).compareToIgnoreCase(getType(o2));
		    		return ascending ? c : -c;
		    	}

		    	return 0;
		    }
		};
	}
	
	private String getType(BioPaxObject bpObject){
		if (bpObject instanceof Interaction && hasMeasuredData((Interaction)bpObject)){
			return bpObject.getTypeLabel()+" (with data)";
		}
		return bpObject.getTypeLabel();
	}
	
	private boolean hasMeasuredData(Interaction interaction) {
		
		Set<SBEntity> subEntities = new HashSet<SBEntity>();
		subEntities.add(interaction);

		Set<Control> controls = BioPAXUtil.findAllControls(interaction, pathwayModel);
		subEntities.addAll(controls);
		subEntities = SBPAX3Util.extractAllEntities(subEntities);
		for(SBEntity subEntity : subEntities) {
			if(subEntity instanceof SBMeasurable) {
				return true;
			}
		}
		return false;
	}

	private String getLabel(BioPaxObject bpObject){
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
		}else if (bpObject instanceof Interaction){
			Interaction interaction =(Interaction)bpObject;
			if (interaction.getName().size()>0){
				return interaction.getName().get(0);
			}else{
				return interaction.getIDShort();
			}
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
		if (pathwayModel == null) {
			setData(null);
			return;
		}
		
		List<BioPaxObject> allPathwayObjectList = new ArrayList<BioPaxObject>();
		for (BioPaxObject bpObject1 : pathwayModel.getBiopaxObjects()){
			if (bpObject1 instanceof PhysicalEntity || (bpObject1 instanceof Interaction && !(bpObject1 instanceof Control))){
				allPathwayObjectList.add(bpObject1);
			}
		}
		ArrayList<BioPaxObject> pathwayObjectList = new ArrayList<BioPaxObject>();
		for (BioPaxObject bpObject : allPathwayObjectList){
			if (searchText == null || searchText.length() == 0 
					|| getLabel(bpObject).toLowerCase().contains(searchText.toLowerCase())
					|| getType(bpObject).toLowerCase().contains(searchText.toLowerCase()) ) {
				pathwayObjectList.add(bpObject);
				bioPaxObjectImportedMap.put(bpObject, bioModel != null && bioModel.getPathwayModel().find(bpObject) != null);
			}
		}
		setData(pathwayObjectList);
	}

	public void pathwayChanged(PathwayEvent event) {
		refreshBioPaxImportedMap();
	}
	
	private void refreshBioPaxImportedMap() {
		bioPaxObjectImportedMap.clear();
		for (int i = 0; i < getRowCount(); i ++) {
			BioPaxObject bpObject = getValueAt(i);
			bioPaxObjectImportedMap.put(bpObject, bioModel != null && bioModel.getPathwayModel().find(bpObject) != null);
		}
		fireTableRowsUpdated(0, getRowCount());
	}

	public void setBioModel(BioModel newValue) {
		BioModel oldValue = bioModel;
		this.bioModel = newValue;
		if (oldValue != null) {
			oldValue.getPathwayModel().removePathwayListener(this);
		}
		if (newValue != null) {
			newValue.getPathwayModel().addPathwayListener(this);
		}
	}
}
