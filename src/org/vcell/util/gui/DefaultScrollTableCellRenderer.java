/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.vcell.util.Issue;
import org.vcell.util.NumberUtils;
import org.vcell.util.gui.sorttable.SortTableModel;

import cbit.gui.ReactionEquation;
import cbit.vcell.client.desktop.biomodel.BioModelEditorRightSideTableModel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;

@SuppressWarnings("serial")
public class DefaultScrollTableCellRenderer extends DefaultTableCellRenderer {
	public static final Color hoverColor = new Color(0xFDFCDC);
//	public static final Border DEFAULT_GAP = BorderFactory.createEmptyBorder(2,4,2,4);
	public static final Border DEFAULT_GAP = BorderFactory.createEmptyBorder(1,1,1,1);
	static final Border focusHighlightBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
	public static final Color uneditableForeground = new Color(0x964B00/*0x967117*/)/*UIManager.getColor("TextField.inactiveForeground")*/;
	static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	static final Color everyOtherRowColor = new Color(0xe8edff);
	private boolean bEnableUneditableForeground = true;
	/**
	 * DefaultTableCellRendererEnhanced constructor comment.
	 */
	public DefaultScrollTableCellRenderer() {
		super();
		setOpaque(true);
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
//		if (tableModel instanceof VCellSortTableModel) {
		if (tableModel instanceof SortTableModel) {
			List<Issue> issueListError = ((VCellSortTableModel<?>) tableModel).getIssues(row, column, Issue.SEVERITY_ERROR);
			List<Issue> issueListWarning = ((VCellSortTableModel<?>) tableModel).getIssues(row, column, Issue.SEVERITY_WARNING);
			Icon icon = null;
			if (issueListError.size() > 0) {
				setToolTipText(Issue.getHtmlIssueMessage(issueListError));
				if (column == 0) {
					icon = VCellIcons.issueErrorIcon;
					setBorder(new MatteBorder(1,1,1,0,Color.red));
				} else if (column == table.getColumnCount() - 1) {
					setBorder(new MatteBorder(1,0,1,1,Color.red));
				} else {
					setBorder(new MatteBorder(1,0,1,0,Color.red));
				}
			} else if(issueListWarning.size() > 0) {
				setToolTipText(Issue.getHtmlIssueMessage(issueListWarning));
				if (column == 0) {
					icon = VCellIcons.issueWarningIcon;
					setBorder(new MatteBorder(1,1,1,0,Color.orange));
				} else if (column == table.getColumnCount() - 1) {
					setBorder(new MatteBorder(1,0,1,1,Color.orange));
				} else {
					setBorder(new MatteBorder(1,0,1,0,Color.orange));
				}
			} else {
				if(column == 0) {
					icon = VCellIcons.issueGoodIcon;
				}
				setToolTipText(null);
				setBorder(DEFAULT_GAP);
			}
			setIcon(icon);
		}
		if (bEnableUneditableForeground && (!table.isEnabled() || !tableModel.isCellEditable(row, column))) {
			if (!isSelected) {
				setForeground(uneditableForeground);
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
