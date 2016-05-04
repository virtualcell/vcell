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
import java.awt.Point;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
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

import cbit.gui.ModelProcessEquation;
import cbit.vcell.client.desktop.biomodel.BioModelEditorRightSideTableModel;

@SuppressWarnings("serial")
public class DefaultScrollTableCellRenderer extends DefaultTableCellRenderer {
	private static final int LEFT_ICON_MARGIN = 1;
	public static final Color hoverColor = new Color(0xFDFCDC);
	public static final Border DEFAULT_GAP = BorderFactory.createEmptyBorder(1,LEFT_ICON_MARGIN,1,1);
	static final Border focusHighlightBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
	public static final Color uneditableForeground = new Color(0x964B00/*0x967117*/)/*UIManager.getColor("TextField.inactiveForeground")*/;
	static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	public static final Color everyOtherRowColor = new Color(0xe8edff);
	private boolean bEnableUneditableForeground = true;
	public String defaultToolTipText = null;
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
		defaultToolTipText = null;
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		defaultToolTipText = getToolTipText();
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
		if (bEnableUneditableForeground && (!table.isEnabled() || !tableModel.isCellEditable(row, column))) {
			if (!isSelected) {
				setForeground(uneditableForeground);
			}
		}
		if (value instanceof Double) {
			Double doubleValue = (Double)value;
			setText(nicelyFormattedDouble(doubleValue));
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
		} else if (value instanceof ModelProcessEquation && BioModelEditorRightSideTableModel.ADD_NEW_HERE_REACTION_TEXT.equals(((ModelProcessEquation)value).toString())) {
			setText(BioModelEditorRightSideTableModel.ADD_NEW_REACTION_OR_RULE_HTML);
		}
		if (tableModel instanceof SortTableModel) {
			DefaultScrollTableCellRenderer.issueRenderer(this, defaultToolTipText, table, row, column, (SortTableModel)tableModel);
		}
		return this;
	}
	
	/**
	 * format double nicely for Nan, infinite and typical values
	 * @param doubleValue not null
	 * @return String
	 */
	public static String nicelyFormattedDouble(Double doubleValue) {
		if (doubleValue.isNaN() || doubleValue.isInfinite()) {
			return java.text.NumberFormat.getInstance().format(doubleValue.doubleValue());
		} else {
			return NumberUtils.formatNumber(doubleValue.doubleValue() );
		}
		
	}
	
	public static void issueRenderer(JLabel renderer, String defaultToolTipText, JTable table, int row, int column, SortTableModel tableModel) {
		List<Issue> issueListError = tableModel.getIssues(row, column, Issue.Severity.ERROR);
		List<Issue> issueListWarning = tableModel.getIssues(row, column, Issue.Severity.WARNING);
		Icon icon = null;
		Point mousePosition = table.getMousePosition();
		Color red = Color.getHSBColor(0f, 0.4f, 1.0f);		// hue, saturation, brightness
		
		if (issueListError.size() > 0) {
			if (column == 0) {
				icon = VCellIcons.issueErrorIcon;
				if(mousePosition !=null && mousePosition.getX()>LEFT_ICON_MARGIN && mousePosition.getX()<=(icon.getIconWidth()+LEFT_ICON_MARGIN)) {
					String tt = Issue.getHtmlIssueMessage(issueListError);
					renderer.setToolTipText(tt);
				} else {
					renderer.setToolTipText(defaultToolTipText);
				}
				renderer.setBorder(new MatteBorder(1,1,1,0, red));	// Color.red
			} else if (column == table.getColumnCount() - 1) {
				renderer.setBorder(new MatteBorder(1,0,1,1, red));
			} else {
				renderer.setBorder(new MatteBorder(1,0,1,0, red));
			}
		} else if(issueListWarning.size() > 0) {
			if (column == 0) {
				icon = VCellIcons.issueWarningIcon;
				if(mousePosition !=null && mousePosition.getX()>LEFT_ICON_MARGIN && mousePosition.getX()<=(icon.getIconWidth()+LEFT_ICON_MARGIN)) {
					renderer.setToolTipText(Issue.getHtmlIssueMessage(issueListWarning));
				} else {
					renderer.setToolTipText(defaultToolTipText);
				}
				renderer.setBorder(new MatteBorder(1,1,1,0,Color.orange));
			} else if (column == table.getColumnCount() - 1) {
				renderer.setBorder(new MatteBorder(1,0,1,1,Color.orange));
			} else {
				renderer.setBorder(new MatteBorder(1,0,1,0,Color.orange));
			}
		} else {
			if(column == 0) {
				icon = VCellIcons.issueGoodIcon;
				renderer.setToolTipText(null);	// no tooltip for column 0 when we have no issues on that line
			}
			if(column != 0 && defaultToolTipText != null && !defaultToolTipText.isEmpty()) {
				renderer.setToolTipText(defaultToolTipText);
			} else {
				renderer.setToolTipText(null);
			}
			renderer.setBorder(DEFAULT_GAP);
		}
		renderer.setIcon(icon);
	}
}
