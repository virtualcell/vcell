package org.vcell.sybil.gui.bpimport;

/*   EntitySelectionTable  --- by Oliver Ruebenacker, UCHC --- November (?) 2008 to November 2009
 *   Table to select entities from an SBBox
 */

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;

import org.vcell.sybil.models.sbbox.SBBox;

public class EntitySelectionTable extends JTable {

	private static final long serialVersionUID = 247675989150878417L;

	protected boolean columnLayoutManual = false;
	
	public EntitySelectionTable(SBBox box) {
		super(new EntitySelectionTableModel(box));
//		setAutoCreateRowSorter(true);
		doColumnSizes();
	}
	
	public void doLayout() { 
		if(tableHeader.getResizingColumn() != null) { columnLayoutManual = true; }
		super.doLayout(); 
	}
	
	public EntitySelectionTableModel getModel() { return (EntitySelectionTableModel) super.getModel(); }
	
	public void doColumnSizes() {
		getModel();
		if(!columnLayoutManual && 
				getColumnModel().getColumnCount() == EntitySelectionTableModel.colCount) {
			int rowCount = getModel().getRowCount();
			int colCount = getModel().getColumnCount();
			final boolean isSelected = false;
			final boolean hasFocus = false;
			for(int iCol = 0; iCol < colCount; iCol++) {
				int colPrefWidth = 0;
				for(int iRow = 0; iRow < rowCount; iRow++) {
					int cellPrefWidth = getCellRenderer(iRow, iCol)
					.getTableCellRendererComponent(this, getValueAt(iRow, iCol), isSelected, hasFocus, 
							iRow, iCol).getPreferredSize().width;
					if(cellPrefWidth > colPrefWidth) { colPrefWidth = cellPrefWidth; }
				}
				getColumnModel().getColumn(iCol).setPreferredWidth(colPrefWidth);
			}
		}
	}
	
	public void tableChanged(TableModelEvent event) {
		doColumnSizes();
		super.tableChanged(event);
	}
	

	
}
