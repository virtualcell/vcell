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

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


public class ColumnGroup {
    /**
     * Cell renderer for group header.
     */    
    protected TableCellRenderer renderer;
    /**
     * Holds the TableColumn or ColumnGroup objects contained
     * within this ColumnGroup instance.
     */    
    protected ArrayList<Object> v;
    /**
     * The ColumnGroup instance name.
     */    
    protected String text;
    /**
     * The margin to use for renderering.
     */    
    protected int margin=0;
    
    /**
     * Standard ColumnGroup constructor.
     * @param text Name of the ColumnGroup which will be displayed
     * when the ColumnGroup is renderered.
     */    
    public ColumnGroup(String text) {
        this(null,text);
    }
    
    /**
     * Standard ColumnGroup constructor.
     * @param renderer a TableCellRenderer for the group.
     * @param text Name of the ColumnGroup which will be displayed
     * when the ColumnGroup is renderered.
     */    
    @SuppressWarnings("serial")
	public ColumnGroup(TableCellRenderer renderer,String text) {
        if (renderer == null) {
            this.renderer = new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
                    JTableHeader header = table.getTableHeader();
                    if (header != null) {
                        setForeground(header.getForeground());
                        setBackground(header.getBackground());
//                        setFont(header.getFont());
                    }
                    setHorizontalAlignment(JLabel.CENTER);
                    setText((value == null) ? "" : value.toString());
                    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                    return this;
                }
            };
        } else {
            this.renderer = renderer;
        }
        this.text = text;
        v = new ArrayList<Object>();
    }
    
    
    /**
     * Add a TableColumn or ColumnGroup object to the
     * ColumnGroup instance.
     * @param obj TableColumn or ColumnGroup
     */
    public void add(Object obj) {
        if (obj == null) { return; }
        v.add(obj);
    }
    
    
    /**
     * Get the ColumnGroup list containing the required table
     * column.
     * @param g vector to populate with the ColumnGroup/s
     * @param c TableColumn
     * @return Vector containing the ColumnGroup/s
     */
	public ArrayList<ColumnGroup> getColumnGroups(TableColumn c, ArrayList<ColumnGroup> g) {
        g.add(this);
        if (v.contains(c)) return g;
        Iterator<Object> iter = v.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof ColumnGroup) {
            	 @SuppressWarnings("unchecked")
                ArrayList<ColumnGroup> groups = ((ColumnGroup)obj).getColumnGroups(c,(ArrayList<ColumnGroup>)g.clone());
                if (groups != null) return groups;
            }
        }
        return null;
    }
    
    /**
     * Returns the TableCellRenderer for the ColumnGroup.
     * @return the TableCellRenderer
     */    
    public TableCellRenderer getHeaderRenderer() {
        return renderer;
    }
    
    /**
     * Set the TableCellRenderer for this ColumnGroup.
     * @param renderer the renderer to use
     */    
    public void setHeaderRenderer(TableCellRenderer renderer) {
        if (renderer != null) {
            this.renderer = renderer;
        }
    }
    
    /**
     * Get the ColumnGroup header value.
     * @return the value.
     */    
    public Object getHeaderValue() {
        return text;
    }
    
    /**
     * Get the dimension of this ColumnGroup.
     * @param table the table the header is being rendered in
     * @return the dimension of the ColumnGroup
     */    
    public Dimension getSize(JTable table) {
        Component comp = renderer.getTableCellRendererComponent(
        table, getHeaderValue(), false, false,-1, -1);
        int height = comp.getPreferredSize().height;
        int width  = 0;
        Iterator<Object> iter = v.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof TableColumn) {
                TableColumn aColumn = (TableColumn)obj;
                width += aColumn.getWidth();
            } else {
                width += ((ColumnGroup)obj).getSize(table).width;
            }
        }
        return new Dimension(width, height);
    }
    
    /**
     * Sets the margin that ColumnGroup instance will use and all
     * held TableColumns and/or ColumnGroups.
     * @param margin the margin
     */    
    public void setColumnMargin(int margin) {
        this.margin = margin;
        Iterator<Object> iter = v.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof ColumnGroup) {
                ((ColumnGroup)obj).setColumnMargin(margin);
            }
        }
    }
}

