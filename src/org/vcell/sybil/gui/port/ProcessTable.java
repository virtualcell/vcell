/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.gui.port;

/*   ProcessTable  --- by Oliver Ruebenacker, UCHC --- July 2008
 *   The table to edit translation of entities
 */

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.vcell.sybil.models.bpimport.table.Cell;
import org.vcell.sybil.models.bpimport.table.ProcessTableModel;

public class ProcessTable extends JTable {

	private static final long serialVersionUID = -2520577139535635777L;

	protected TableGUI tableGUI = new TableGUI(this);
	
	public ProcessTable() {
		setDefaultEditor(Cell.class, new ProcessTableCellEditor());
	}
	
	public ProcessTable(ProcessTableModel model) {
		super(model);
		model.setUI(tableGUI);
		setDefaultEditor(Cell.class, new ProcessTableCellEditor());
	}
	
	protected boolean columnLayoutManual = false;
	
	public void doLayout() { 
		if(tableHeader.getResizingColumn() != null) { columnLayoutManual = true; }
		super.doLayout(); 
	}
	
	public void setModel(TableModel model) {
		super.setModel(model);
		if(model instanceof ProcessTableModel) { ((ProcessTableModel) model).setUI(tableGUI); }
	}
	
	public ProcessTableModel model() { return (ProcessTableModel) getModel(); }
	
}
