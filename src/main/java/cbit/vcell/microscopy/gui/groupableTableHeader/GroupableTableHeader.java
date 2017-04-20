/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.groupableTableHeader;

import java.awt.Color;
import java.awt.Component;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import cbit.vcell.microscopy.gui.VirtualFrapLoader;


/**
 * This is the object which manages the header of the JTable and
 * also provides functionality for groupable headers.
 */
@SuppressWarnings("serial")
public class GroupableTableHeader extends JTableHeader {

    /**
     * Identifies the UI class which draws the header.
     */    
//    private static final String uiClassID = "GroupableTableHeaderUI";
    public static Color color = new Color(166, 166, 255); // light blue
    /**
     * Constructs a GroupableTableHeader which is initialized with cm as the
     * column model. If cm is null this method will initialize the table header
     * with a default TableColumnModel.
     * @param model the column model for the table
     */    
    public GroupableTableHeader(GroupableTableColumnModel model) {
        super(model);
        setUI(new GroupableTableHeaderUI());
        setReorderingAllowed(false);
        setDefaultRenderer(createPlainTableHeaderRenderer(color));
    }
    
    
    /**
     * Sets the margins correctly for all groups within
     * the header.
     */    
    public void setColumnMargin() {
        int columnMargin = getColumnModel().getColumnMargin();
        Iterator<ColumnGroup> iter = ((GroupableTableColumnModel)columnModel).columnGroupIterator();
        while (iter.hasNext()) {
            ColumnGroup cGroup = iter.next();
            cGroup.setColumnMargin(columnMargin);
        }
    }
    
    
    public static TableCellRenderer createPlainTableHeaderRenderer(final Color color) {
        return new TableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel comp = new JLabel(value.toString(), JLabel.LEFT);
                comp.setForeground(Color.black);
                comp.setBackground(color);
                comp.setOpaque(true);
                comp.setBorder(new LineBorder(Color.WHITE, 1));
                comp.setFont(VirtualFrapLoader.defaultFont);
                comp.setHorizontalAlignment(SwingConstants.CENTER);
                return comp;
            }

        };
    }
}

