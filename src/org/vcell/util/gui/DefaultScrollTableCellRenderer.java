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
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.vcell.util.NumberUtils;

import cbit.gui.ReactionEquation;
import cbit.vcell.client.desktop.biomodel.BioModelEditorRightSideTableModel;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;

@SuppressWarnings("serial")
public class DefaultScrollTableCellRenderer extends DefaultTableCellRenderer {
	public static final Color hoverColor = new Color(0xFDFCDC);
	public static final Border DEFAULT_GAP = BorderFactory.createEmptyBorder(2,4,2,4);
	static final Border focusHighlightBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
	public static final Color uneditableForeground = new Color(0x964B00/*0x967117*/)/*UIManager.getColor("TextField.inactiveForeground")*/;
	static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	static final Color everyOtherRowColor = new Color(0xe8edff);
	static Font regularFont = null;
	static Font uneditableFont = null;
	private boolean bEnableUneditableForeground = true;
	/**
	 * DefaultTableCellRendererEnhanced constructor comment.
	 */
	public DefaultScrollTableCellRenderer() {
		super();
		setOpaque(true);
		regularFont = getFont();
		uneditableFont = regularFont/*.deriveFont(Font.BOLD)*/;
	}
	
	public void disableUneditableForeground() {
		bEnableUneditableForeground = false;
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
		
		TableModel tableModel = table.getModel();
		if (tableModel instanceof VCellSortTableModel) {
			if (((VCellSortTableModel) tableModel).getIssue(row, column).size() > 0) {
				setBorder(new LineBorder(Color.red));
			} else {
				setBorder(DEFAULT_GAP);
			}
		}
		if (bEnableUneditableForeground && (!table.isEnabled() || !tableModel.isCellEditable(row, column))) {
			if (!isSelected) {
				setForeground(uneditableForeground);
				setFont(uneditableFont);
			}
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
		if (BioModelEditorRightSideTableModel.ADD_NEW_HERE_TEXT.equals(value)) {
			setText(BioModelEditorRightSideTableModel.ADD_NEW_HERE_HTML);
		} else if (value instanceof ReactionEquation && BioModelEditorRightSideTableModel.ADD_NEW_HERE_REACTION_TEXT.equals(((ReactionEquation)value).toString())) {
			setText(BioModelEditorRightSideTableModel.ADD_NEW_HERE_REACTION_HTML);
		}
		return this;
	}
}
