package org.vcell.sybil.gui.graphinfo;

/*   SelectedStatementsTable  --- by Oliver Ruebenacker, UCHC --- February to March 2009
 *   Creates a table to display the selected Resources of a graph
 */

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;

import org.vcell.sybil.rdf.RDFToken;
import org.vcell.sybil.util.table.StatementsPOTableModel;

public class SelectedStatementsTable extends JTable {
	private static final long serialVersionUID = -5647673037995352933L;
	
	public SelectedStatementsTable(StatementsPOTableModel tableModelNew) { 
		super(tableModelNew); 
		this.setDefaultRenderer(RDFToken.class, new RDFTokenRenderer());
	}
	
	protected boolean columnLayoutManual = false;
	
	@Override
	public void doLayout() { 
		if(tableHeader.getResizingColumn() != null) { columnLayoutManual = true; }
		super.doLayout(); 
	}
	
	@Override
	public StatementsPOTableModel getModel() { return (StatementsPOTableModel) super.getModel(); }
	
	public void doColumnSizes() {
		getModel();
		if(!columnLayoutManual && 
				getColumnModel().getColumnCount() == StatementsPOTableModel.columnCount) {
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
	
	@Override
	public void tableChanged(TableModelEvent event) {
		doColumnSizes();
		super.tableChanged(event);
	}
	
}