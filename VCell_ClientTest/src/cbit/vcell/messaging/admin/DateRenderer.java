package cbit.vcell.messaging.admin;
/**
 * Insert the type's description here.
 * Creation date: (7/8/2004 2:17:40 PM)
 * @author: Fei Gao
 */
public class DateRenderer extends javax.swing.table.DefaultTableCellRenderer {
/**
 * DateRenderer constructor comment.
 */
public DateRenderer() {
	super();
}


	/**
	 *  This method is sent to the renderer by the drawing table to
	 *  configure the renderer appropriately before drawing.  Return
	 *  the Component used for drawing.
	 *
	 * @param	table		the JTable that is asking the renderer to draw.
	 *				This parameter can be null.
	 * @param	value		the value of the cell to be rendered.  It is
	 *				up to the specific renderer to interpret
	 *				and draw the value.  eg. if value is the
	 *				String "true", it could be rendered as a
	 *				string or it could be rendered as a check
	 *				box that is checked.  null is a valid value.
	 * @param	isSelected	true is the cell is to be renderer with
	 *				selection highlighting
	 * @param	row	        the row index of the cell being drawn.  When
	 *				drawing the header the rowIndex is -1.
	 * @param	column	        the column index of the cell being drawn
	 */
public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	if (value == null) {
		return this;
	}
	
	java.text.SimpleDateFormat sdtf = new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US);
	java.text.SimpleDateFormat ddtf = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", java.util.Locale.US);
	
	setText(sdtf.format(value));
	setToolTipText(ddtf.format(value));
	return this;
}
}