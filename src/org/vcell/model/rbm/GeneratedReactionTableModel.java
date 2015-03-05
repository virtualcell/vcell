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

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

import org.vcell.pathway.BioPaxObject;
import org.vcell.util.gui.AutoCompleteTableModel;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.bionetgen.BNGReaction;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.SymbolTable;

@SuppressWarnings("serial")
public class GeneratedReactionTableModel extends VCellSortTableModel<GeneratedReactionTableRow> 
	implements  PropertyChangeListener, AutoCompleteTableModel{

	public static final int colCount = 3;
	public static final int iColIndex = 0;
	public static final int iColExpression = 1;
	public static final int iColDepiction = 2;
	
	// filtering variables 
	protected static final String PROPERTY_NAME_SEARCH_TEXT = "searchText";
	protected String searchText = null;

	private BNGReaction[] reactions;
	private ArrayList<GeneratedReactionTableRow> allGeneratedReactionsList;
	
	protected transient java.beans.PropertyChangeSupport propertyChange;

	public GeneratedReactionTableModel(EditorScrollTable table) {
		super(table, new String[] {
				"Index", "Expression", "Depiction"});
	}
	
	public Class<?> getColumnClass(int iCol) {
		switch (iCol){		
			case iColIndex:{
				return String.class;
			}case iColExpression:{
				return String.class;
			}case iColDepiction:{
				return String.class;
			}
		}
	return Object.class;
	}
	
	public Object getValueAt(int iRow, int iCol) {
		GeneratedReactionTableRow reactionTableRow = getValueAt(iRow);
		BNGReaction reactionObject = reactionTableRow.getReactionObject();
		switch(iCol) {
			case iColIndex:{
				return reactionTableRow.getIndex();
			}
			case iColExpression:{
				return reactionTableRow.getExpression();
			}
			case iColDepiction:{
				return null;
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
		return new Comparator<GeneratedReactionTableRow>() {
		    public int compare(GeneratedReactionTableRow o1, GeneratedReactionTableRow o2){
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
			GeneratedReactionTableRow newRow = createTableRow(reaction, i+1, reaction.toStringShort(), 5);
			allGeneratedReactionsList.add(newRow);
		}
		
		setData(allGeneratedReactionsList);
		GuiUtils.flexResizeTableColumns(ownerTable);
	}
	
	private GeneratedReactionTableRow createTableRow(BNGReaction reaction, int index, String interactionLabel, int depiction) {
		GeneratedReactionTableRow row = new GeneratedReactionTableRow(reaction);
		
		row.setIndex(index);
		row.setExpression(interactionLabel);
		row.setDepiction(depiction);
		return row;
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
		// TODO Auto-generated method stub
		return null;
	}
}
