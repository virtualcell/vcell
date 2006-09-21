package cbit.vcell.messaging.admin;
import cbit.vcell.simulation.SolverTaskDescription;
/**
 * Insert the type's description here.
 * Creation date: (7/8/2004 2:26:27 PM)
 * @author: Fei Gao
 */
public class SolverTaskDescriptionRenderer extends javax.swing.table.DefaultTableCellRenderer {
/**
 * SolverTaskDescriptionRenderer constructor comment.
 */
public SolverTaskDescriptionRenderer() {
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
	setText(value.toString());
	setToolTipText(value.toString());
	return this;
}
}