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

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

/**
 * Class which extends the functionality of DefaultColumnTableModel to
 * also provide capabilities to group columns. This can be used for
 * instance to aid in the layout of groupable table headers.
 */
@SuppressWarnings("serial")
public class GroupableTableColumnModel extends DefaultTableColumnModel {
    
    /**
     * Hold the list of ColumnGroups which define what group each normal
     * column is within, if any.
     */    
    protected ArrayList<ColumnGroup> columnGroups = new ArrayList<ColumnGroup>();
    

    /**
     * Add a new columngroup.
     * @param columnGroup new ColumnGroup
     */    
    public void addColumnGroup(ColumnGroup columnGroup) {
        columnGroups.add(columnGroup);
    }
    
    /**
     * Provides an Iterator to iterate over the
     * ColumnGroup list.
     * @return Iterator over ColumnGroups
     */    
    public Iterator<ColumnGroup> columnGroupIterator() {
        return columnGroups.iterator();
    }
    
    /**
     * Returns a ColumnGroup specified by an index.
     * @param index index of ColumnGroup
     * @return ColumnGroup
     */    
    public ColumnGroup getColumnGroup(int index) {
        if(index >= 0 && index < columnGroups.size()) {
            return columnGroups.get(index);
        }
        return null;
    }
    
    /**
     * Provides and iterator for accessing the ColumnGroups
     * associated with a column.
     * @param col Column
     * @return ColumnGroup iterator
     */    
    public Iterator<ColumnGroup> getColumnGroups(TableColumn col) {
        if (columnGroups.isEmpty()) return null;
        Iterator<ColumnGroup> iter = columnGroups.iterator();
        while (iter.hasNext()) {
            ColumnGroup cGroup = iter.next();
            ArrayList<ColumnGroup> v_ret = cGroup.getColumnGroups(col,new ArrayList<ColumnGroup>());
            if (v_ret != null) {
                return v_ret.iterator();
            }
        }
        return null;
    }
}


