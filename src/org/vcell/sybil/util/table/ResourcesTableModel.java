package org.vcell.sybil.util.table;

/*   SelectedResourcesTableModel  --- by Oliver Ruebenacker, UCHC --- February 2008 to November 2009
 *   TableModel for a Table displaying the selected Resources of a graph
 */


import java.util.Collection;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.vcell.sybil.models.sbbox.imp.SBWrapper;

public class ResourcesTableModel extends AbstractTableModel implements TableModel {

	private static final long serialVersionUID = 2692435932347527323L;

	protected Vector<SBWrapper> sbs;
	
	public ResourcesTableModel() {
		sbs = new Vector<SBWrapper>();
	}
	
	public ResourcesTableModel(Collection<? extends SBWrapper> resourcesNew) {
		sbs = new Vector<SBWrapper>(resourcesNew);
	}
	
	public void setResources(Collection<? extends SBWrapper> resourcesNew) {
		sbs = new Vector<SBWrapper>(resourcesNew);
		fireTableDataChanged();
	}
	
	public int getColumnCount() { return 1; }
	@Override
	public String getColumnName(int colInd) { return "Resources"; }
	public int getRowCount() { return sbs.size(); }

	@Override
	public Class<SBWrapper> getColumnClass(int iCol) { return SBWrapper.class; }
	
	public SBWrapper getValueAt(int rowInd, int colInd) { 
		if(colInd == 0) {
			try { return sbs.get(rowInd); }
			catch(Throwable t) { return null; }
		} 
		return null;
	}

	public SBWrapper getSBItem(int ind) { 
		try { return sbs.get(ind); }
		catch(Throwable t) { return null; }
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) { return false; }
	@Override
	public void setValueAt(Object valueNew, int rowInd, int colInd) { }

}
