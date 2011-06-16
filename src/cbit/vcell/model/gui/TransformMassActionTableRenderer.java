/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import cbit.vcell.model.gui.TransformMassActions.TransformedReaction;

/**
 * This table cell renderer is created for TransformMassActionTable
 * Which basically wants to show the checkbox for column 4 (tranformed or not)
 * and highlight the rows (corresponding to reactions) that can not be transformed
 * in pink color.
 * @author Tracy LI
 */
public class TransformMassActionTableRenderer extends DefaultTableCellRenderer
{
	private TableCellRenderer booleanCellRenderer;
	
	public TransformMassActionTableRenderer(TableCellRenderer booleanCellRenderer){
		this.booleanCellRenderer = booleanCellRenderer;
	}
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component tcr = null;
		if(booleanCellRenderer != null && value instanceof Boolean){
			tcr = booleanCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if(table.getValueAt(row, TransformMassActionTableModel.COLUMN_REMARK).equals(TransformMassActions.TransformedReaction.Label_Transformable))
			{
				tcr.setEnabled(true);
			}
			else
			{
				tcr.setEnabled(false);
			}
		}else{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			tcr = this;
		}
		//  set row color to pink if the reaction on the row is not able to be transformed.
		if (!table.getValueAt(row, TransformMassActionTableModel.COLUMN_REMARK).equals("") && 
			((String)table.getValueAt(row, TransformMassActionTableModel.COLUMN_REMARK)).indexOf(TransformedReaction.Label_Failed) >= 0)
		{
			tcr.setBackground( new Color(255,200,200));
			tcr.setForeground(Color.BLACK);
		}
		else 
		{
			tcr.setBackground(table.getBackground());
			tcr.setForeground(table.getForeground());
		}
		return tcr;
	}
}
