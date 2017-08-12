package org.vcell.util.gui.sorttable;
/*
=====================================================================

  JSortTable.java
  
  Created by Claude Duguay
  Copyright (c) 2002
  
=====================================================================
*/

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.vcell.util.gui.ScrollTable;

@SuppressWarnings("serial")
public class JSortTable extends ScrollTable
  implements MouseListener
{  
	private int pressedXPoint = -1;

/**
 * Insert the method's description here.
 * Creation date: (4/27/2004 10:10:56 AM)
 */
public void createDefaultColumnsFromModel() {
	super.createDefaultColumnsFromModel();
	initSortHeader();
}


private void initSortHeader() {
	JTableHeader header = getTableHeader();
	if (header != null) {
		header.removeMouseListener(this);
		header.addMouseListener(this);
		for (int i = 0; i < getColumnCount(); i++) {
			header.getColumnModel().getColumn(i).setHeaderRenderer(new SortTableHeaderRenderer(getTableHeader().getDefaultRenderer()));
		}
	}
}


  public void mouseClicked(MouseEvent event) {}


  public void mouseEntered(MouseEvent event) {}


  public void mouseExited(MouseEvent event) {}


public void mousePressed(MouseEvent event) {
	pressedXPoint = event.getX();
}


public void mouseReleased(MouseEvent event) {	
	int x = event.getX();	
	if (pressedXPoint != x) {
		return;
	}
	
	TableColumnModel colModel = getColumnModel();
	int index = colModel.getColumnIndexAtX(x);
	int modelIndex = colModel.getColumn(index).getModelIndex();

	SortTableModel model = (SortTableModel) getModel();	
	if (model.isSortable(modelIndex)) {
		// toggle ascension, if already sorted
		SortPreference sortPreference = model.getSortPreference();
		boolean sortedColumnAscending = sortPreference.isSortedColumnAscending();
		if (sortPreference.getSortedColumnIndex() == modelIndex) {
			sortedColumnAscending = !sortedColumnAscending;
		}
		model.setSortPreference(new SortPreference(sortedColumnAscending, modelIndex));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/30/2004 11:05:11 AM)
 * @param newModel javax.swing.table.TableModel
 */
public void setModel(SortTableModel newModel) {
	super.setModel(newModel);
	initSortHeader();
}
}