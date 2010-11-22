package org.vcell.util.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.util.NumberUtils;

import cbit.vcell.client.desktop.biomodel.BioModelEditorReactionTableModel;

@SuppressWarnings("serial")
public class DefaultScrollTableCellRenderer extends DefaultTableCellRenderer {
	public static final Color hoverColor = new Color(0xFDFCDC);
	public static final Border DEFAULT_GAP = BorderFactory.createEmptyBorder(2,4,2,4);
	static final Border focusHighlightBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
	static final Color uneditableForeground = UIManager.getColor("TextField.inactiveForeground");
	static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	static final Color everyOtherRowColor = new Color(0xe8edff);
	static Font regularFont = null;
	static Font uneditableFont = null;
	/**
	 * DefaultTableCellRendererEnhanced constructor comment.
	 */
	public DefaultScrollTableCellRenderer() {
		super();
		setOpaque(true);
		regularFont = getFont();
		uneditableFont = regularFont.deriveFont(Font.BOLD);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 1:07:02 PM)
	 * @return java.awt.Component
	 * @param table javax.swing.JTable
	 * @param value java.lang.Object
	 * @param isSelected boolean
	 * @param hasFocus boolean
	 * @param row int
	 * @param column int
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setBorder(DEFAULT_GAP);
		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else {
			if (table instanceof ScrollTable && ((ScrollTable)table).getHoverRow() == row) {
				setBackground(hoverColor);
			} else {
				setBackground(row % 2 == 0 ? table.getBackground() : everyOtherRowColor);				
			}
			setForeground(table.getForeground());
		}
		
		if (!table.isEnabled() || !table.getModel().isCellEditable(row, column)) {
			setForeground(uneditableForeground);
			setFont(uneditableFont);
		}
		if (value instanceof Double) {
			Double doubleValue = (Double)value;
			if (doubleValue.isNaN() || doubleValue.isInfinite()) {
				setText(java.text.NumberFormat.getInstance().format(doubleValue.doubleValue()));
			} else {
				String formattedDouble = NumberUtils.formatNumber(doubleValue.doubleValue());
				setText(formattedDouble);
			}			
		} else if (value instanceof JComponent) {
			JComponent jc = (JComponent)value;
			if (hasFocus) {
			    jc.setBorder(focusHighlightBorder );
			} else {
			    jc.setBorder(noFocusBorder);
			}
			return jc;
		}
		if (value != null && (value.equals(EditorScrollTable.ADD_NEW_HERE_TEXT))) {
			setText("<html><i>" + value + "</i></html>");
		} 
//		if (value instanceof ReactionEquation) {
//			ReactionEquation reactionEquation = (ReactionEquation)value;
//			setText("<html>" + reactionEquation.getEquationleftHand() 
//					+ "&nbsp;<b>" + ReactionEquation.REACTION_GOESTO + "</b>" 
//					+ reactionEquation.getEquationRightHand()+ "&nbsp;</html>");
//		}
		return this;
	}
}
