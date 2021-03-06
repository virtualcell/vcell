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
import java.util.Set;

import org.vcell.util.gui.AutoCompleteTableModel;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.bionetgen.BNGReaction;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class GeneratedReactionTableModel extends VCellSortTableModel<GeneratedReactionTableRow> 
	implements  PropertyChangeListener, AutoCompleteTableModel{

	public static final int colCount = 4;
	public static final int iColRule = 0;
	public static final int iColStructure = 1;
	public static final int iColDepiction = 2;
	public static final int iColDefinition = 3;
	
	static final String reverse = "_reverse_";

	
	// filtering variables 
	protected static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	protected String searchText = null;

	private BNGReaction[] reactions;
	private ArrayList<GeneratedReactionTableRow> allGeneratedReactionsList;
	private Model model = null;		// fake model where we insert all the so called rules 
	
	private final NetworkConstraintsPanel owner;
	
	protected transient java.beans.PropertyChangeSupport propertyChange;

	public GeneratedReactionTableModel(EditorScrollTable table, NetworkConstraintsPanel owner) {
		super(table, new String[] {"Rule", "Structure", "Depiction", "BioNetGen Definition"});
		this.owner = owner;
		setMaxRowsPerPage(1000);
	}
	
	public Class<?> getColumnClass(int iCol) {
		switch (iCol) {
			case iColRule:
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
		GeneratedReactionTableRow reactionTableRow = getValueAt(iRow);
		BNGReaction reactionObject = reactionTableRow.getReactionObject();
		String name = reactionObject.getRuleName();
		switch(iCol) {
			case iColRule:
				if(name.endsWith(ReactionRule.DirectHalf)) {
					name = name.substring(0, name.indexOf(ReactionRule.DirectHalf));
				}
				if(name.endsWith(ReactionRule.InverseHalf)) {
					name = name.substring(0, name.indexOf(ReactionRule.InverseHalf));
					name += " (rev)";
				}
				return name;
			case iColStructure:
				if(name.endsWith(ReactionRule.DirectHalf)) {
					name = name.substring(0, name.indexOf(ReactionRule.DirectHalf));
				}
				if(name.endsWith(ReactionRule.InverseHalf)) {
					name = name.substring(0, name.indexOf(ReactionRule.InverseHalf));
				}
				SimulationContext sc = owner.getSimulationContext();
				ReactionRule rr = sc.getModel().getRbmModelContainer().getReactionRule(name);
				if(rr != null && rr.getStructure() != null) {
					return rr.getStructure().getName();
				} else {
					return "?";
				}
			case iColDepiction:
			case iColDefinition:
				return reactionTableRow.getExpression();
			default:
				return null;
		}
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
	
	public Comparator<GeneratedReactionTableRow> getComparator(final int col, final boolean ascending) {
		final int scale = ascending ? 1 : -1;
		return new Comparator<GeneratedReactionTableRow>() {
		    public int compare(GeneratedReactionTableRow o1, GeneratedReactionTableRow o2){
				switch (col) {
				case iColRule:
					return scale * o1.getReactionRule().getDisplayName().compareToIgnoreCase(o2.getReactionRule().getDisplayName());
				case iColStructure:
					return scale * o1.getReactionRule().getStructure().getName().compareToIgnoreCase(o2.getReactionRule().getStructure().getName());
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
		allGeneratedReactionsList = new ArrayList<GeneratedReactionTableRow>();
		
		for(int i = 0; i<reactions.length; i++) {
			BNGReaction reaction = reactions[i];
			GeneratedReactionTableRow newRow = createTableRow(reaction, i+1, reaction.toStringShort());
			allGeneratedReactionsList.add(newRow);
		}
		// apply text search function for particular columns
		ArrayList<GeneratedReactionTableRow> reactionObjectList = new ArrayList<GeneratedReactionTableRow>();
		if (searchText == null || searchText.length() == 0) {
			reactionObjectList.addAll(allGeneratedReactionsList);
		} else {
			String lowerCaseSearchText = searchText.toLowerCase();
			for (GeneratedReactionTableRow rs : allGeneratedReactionsList){
				boolean added = false;
				if (rs.getExpression().toLowerCase().contains(lowerCaseSearchText) ) {
					reactionObjectList.add(rs);
					added = true;
				}
				if(!added && rs.getReactionRule().getDisplayName().toLowerCase().contains(lowerCaseSearchText)) {
					reactionObjectList.add(rs);
					added = true;
				}
				if(!added && rs.getReactionRule().getStructure().getName().toLowerCase().contains(lowerCaseSearchText)) {
					reactionObjectList.add(rs);
					added = true;
				}
			}
		}
		setData(reactionObjectList);
		GuiUtils.flexResizeTableColumns(ownerTable);
	}
	
	private GeneratedReactionTableRow createTableRow(BNGReaction reaction, int index, String interactionLabel) {
		GeneratedReactionTableRow row = new GeneratedReactionTableRow(reaction, owner);
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
	
	public void setReactions(BNGReaction[] newValue) {
		if (reactions == newValue) {
			return;
		}
		reactions = newValue;
		refreshData(); 
	}
	public ArrayList<GeneratedReactionTableRow> getTableRows() {
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
