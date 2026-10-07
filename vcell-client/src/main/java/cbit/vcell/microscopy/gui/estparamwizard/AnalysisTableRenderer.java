/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import cbit.vcell.microscopy.FRAPModel;


@SuppressWarnings("serial")
public class AnalysisTableRenderer extends DefaultTableCellRenderer
{
	/** Dark red. #A40000 on the pink row is 4.49:1, under 4.5:1. This ink is 7.96:1 on that pink. */
	static final Color NOT_IDENTIFIABLE_TEXT = new Color(0x5C, 0x00, 0x00);
	static final Color NOT_IDENTIFIABLE_PINK = new Color(255, 170, 170);

	private NumberFormat format;
	private JButton button = null;
	public AnalysisTableRenderer(int precision)
	{
		super();
        setFont(new Font("Arial", Font.PLAIN, 11));
        //set double precision
        format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(precision);
        format.setMinimumFractionDigits(0);
		//create button 
		button = new JButton("Plot...");
		button.setVerticalTextPosition(SwingConstants.CENTER); 
		button.setHorizontalTextPosition(SwingConstants.LEFT); 
		button.setBackground(Color.white);
	}
	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) 
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 1));
		if(value != null)
        {
        	setToolTipText(value.toString());
        }
    	if(value == null)
		{
			setBackground(new Color(228,228,228)); //light gray
		}
    	else
		{
    		if (isSelected) {
    			setBackground(table.getSelectionBackground());
    		} else {
    			setBackground(table.getBackground());
    		}
			setForeground(Color.black);
		}
    	
    	if(value != null && (value instanceof Double || value instanceof Float || value instanceof Integer))
		{
			setText(format.format(value));
		}
    	
		if((column == AnalysisTableModel.COLUMN_DIFF_ONE_CI_PLOT && row < FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF &&/*there is a CI*/ table.getValueAt(row , column -1) != null) ||
		   (column == AnalysisTableModel.COLUMN_DIFF_TWO_CI_PLOT && row < FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF &&/*there is a CI*/ table.getValueAt(row , column -1) != null) ||
		   (column == AnalysisTableModel.COLUMN_KOFF_CI_PLOT && row < FRAPModel.NUM_MODEL_PARAMETERS_REACTION_OFF_RATE &&/*there is a CI*/ table.getValueAt(row , column -1) != null))
		{
			return button;
		}
		
		// The words already say the result. The pink row stays. Selected rows keep the
		// look-and-feel selection colors: this ink on the Mac selection blue is 2.06:1.
		if (isNotIdentifiable(table, value, column)) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(NOT_IDENTIFIABLE_TEXT);
				if (notIdentifiableGroupColumn(column) >= 0) {
					setBackground(NOT_IDENTIFIABLE_PINK);
				}
			}
		}

		return this;
	}

	private static boolean isNotIdentifiable(JTable table, Object value, int column) {
		if (value instanceof String && AnalysisTableModel.STR_NOT_SIGNIFICANT.equals(value)) {
			return true;
		}
		int significanceColumn = notIdentifiableGroupColumn(column);
		if (significanceColumn < 0) {
			return false;
		}
		Object identifyStr = table.getValueAt(AnalysisTableModel.INDEX_MODEL_SIGNIFICANCE, significanceColumn);
		return identifyStr instanceof String && AnalysisTableModel.STR_NOT_SIGNIFICANT.equals(identifyStr);
	}

	/** Parameter or confidence-interval column whose significance cell paints the pink row, or -1. */
	private static int notIdentifiableGroupColumn(int column) {
		if (column == AnalysisTableModel.COLUMN_DIFF_ONE_PARAMETER_VAL || column == AnalysisTableModel.COLUMN_DIFF_ONE_CI) {
			return AnalysisTableModel.COLUMN_DIFF_ONE_PARAMETER_VAL;
		}
		if (column == AnalysisTableModel.COLUMN_DIFF_TWO_PARAMETER_VAL || column == AnalysisTableModel.COLUMN_DIFF_TWO_CI) {
			return AnalysisTableModel.COLUMN_DIFF_TWO_PARAMETER_VAL;
		}
		if (column == AnalysisTableModel.COLUMN_KOFF_PARAMETER_VAL || column == AnalysisTableModel.COLUMN_KOFF_CI) {
			return AnalysisTableModel.COLUMN_KOFF_PARAMETER_VAL;
		}
		return -1;
	}
}
