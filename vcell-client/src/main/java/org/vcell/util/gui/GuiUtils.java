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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.StringUtils;
import org.vcell.util.BeanUtils;
import org.vcell.util.ISize;

import cbit.vcell.desktop.BioModelNode;
import edu.uchc.connjur.wb.ExecutionTrace;

public class GuiUtils {
	public static Window getWindowForComponent(Component parentComponent) 
	        throws HeadlessException {
	        if (parentComponent == null)
	            return null;
	        if (parentComponent instanceof Window)
	            return (Window)parentComponent;
	        return getWindowForComponent(parentComponent.getParent());
    }
	
	public static void treeExpandAll(JTree tree, BioModelNode treeNode, boolean bExpand) {
		int childCount = treeNode.getChildCount();
		if (childCount > 0) {
			for (int i = 0; i < childCount; i++) {
				TreeNode n = treeNode.getChildAt(i);
				if (n instanceof BioModelNode) {
					treeExpandAll(tree, (BioModelNode)n, bExpand);
				}
			}
			if (!bExpand) {
				tree.collapsePath(new TreePath(treeNode.getPath()));
			}
		} else {
			TreePath path = new TreePath(treeNode.getPath());
			if (bExpand && !tree.isExpanded(path)) {
				tree.expandPath(path.getParentPath());
			} 
		}
	}
	
	public static void treeExpandAllRows(JTree tree) {
		for (int row = 0; row < tree.getRowCount(); row ++){
			tree.expandRow(row);
		}
	}
	
	public static void tableSetSelectedRows(JTable table, int[] rows) {
		Set<Integer> oldSelectionSet = new HashSet<Integer>();
		for (int row : table.getSelectedRows()) {
			oldSelectionSet.add(row);
		}
		Set<Integer> newSelectionSet = new HashSet<Integer>();
		if (rows != null) {
			for (int row : rows) {
				newSelectionSet.add(row);
			}
		}
		
		Set<Integer> removeSet = new HashSet<Integer>(oldSelectionSet);
		removeSet.removeAll(newSelectionSet);
		Set<Integer> addSet = new HashSet<Integer>(newSelectionSet);
		addSet.removeAll(oldSelectionSet);
		for (int row : removeSet) {
			table.removeRowSelectionInterval(row, row);
		}
		for (int row : addSet) {
			table.addRowSelectionInterval(row, row);
		}
		Rectangle r = table.getCellRect(table.getSelectedRow(), 0, true);
		table.scrollRectToVisible(r);
	}
	
	public static void flexResizeTableColumns(JTable table) {
		int rowCount = table.getRowCount();
		int colCount = table.getColumnCount();
		final int[] maxColumnWidths = new int[colCount];
		final int[] headerWidths = new int[colCount];
		java.util.Arrays.fill(maxColumnWidths,0);
		for(int iCol = 0; iCol < colCount; iCol++) {
			TableColumn column = table.getColumnModel().getColumn(iCol);
			TableCellRenderer headerRenderer = column.getHeaderRenderer();
			if (headerRenderer == null) {
				headerRenderer = table.getTableHeader().getDefaultRenderer();
			}
			if (headerRenderer != null) {
				Component comp = headerRenderer.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, 0, iCol);
				headerWidths[iCol] = comp.getPreferredSize().width;
				maxColumnWidths[iCol] = Math.max(maxColumnWidths[iCol], headerWidths[iCol]);
			}
			for(int iRow = 0; iRow < rowCount; iRow++) {
				TableCellRenderer cellRenderer = table.getCellRenderer(iRow,iCol);
				if (cellRenderer == null) {
					continue;
				}
				Component comp = cellRenderer.getTableCellRendererComponent(table, table.getValueAt(iRow, iCol), false, false, iRow, iCol);
				maxColumnWidths[iCol] = Math.max(maxColumnWidths[iCol], comp.getPreferredSize().width);
			}
		}
		for(int iCol = 0; iCol < colCount; iCol++) {
			table.getColumnModel().getColumn(iCol).setPreferredWidth(maxColumnWidths[iCol]);
			table.getColumnModel().getColumn(iCol).setMinWidth(headerWidths[iCol]);
		}
	}
	
	public static JLabel createBoldJLabel() {
		return createBoldJLabel(null);
	}
	
	public static JLabel createBoldJLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		return label;
	}
	
	public static void selectClickTreePath(JTree jtree, MouseEvent e) {
		Point mousePoint = e.getPoint();
		TreePath clickPath = jtree.getPathForLocation(mousePoint.x, mousePoint.y);
	    if (clickPath == null) {
	    	return; 
	    }
		Object rightClickNode = clickPath.getLastPathComponent();
		if (rightClickNode == null || !(rightClickNode instanceof BioModelNode)) {
			return;
		}
		boolean bFound = false;
		TreePath[] selectedPaths = jtree.getSelectionPaths();
		if (selectedPaths != null) {
			for (TreePath tp : selectedPaths) {
				if (tp.equals(clickPath)) {
					bFound = true;
					break;
				}
			}
		}
		if (!bFound) {
			jtree.setSelectionPath(clickPath);
		}
	}
	public static String getMeshSizeText(int dim, ISize size, boolean bTotal)
	{
		int nx = size.getX();
		int ny = size.getY();
		int nz = size.getZ();
		switch (dim)
		{
		case 1:
			return nx + (bTotal ? " = " + nx : "");
		case 2:
			return nx + "x" + ny + (bTotal ? " = " + nx * ny : "");
		case 3:
			return nx + "x" + ny + "x" + nz + (bTotal ? " = " + size.getXYZ() : "");
		}
		return null;
	}
	
	/**
	 * first first child of Container of given type. Performs breadth first search
	 * @param container to search. not null
	 * @param clzz type to search for. not null
	 * @return first found child or null if none
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findFirstChild(Container container, Class<T> clzz) {
		for (Component c : container.getComponents()) {
			if (clzz.isAssignableFrom(c.getClass())) {
				return (T) c;
			}
		}
		for (Component c : container.getComponents()) {
			if (c instanceof Container) {
				Container child = (Container) c;
				T find = findFirstChild(child, clzz);
				if (find != null) {
					return find;
				}
			}
		}
		return null;
	}
	
	/**
	 * recursively transcribe preferred sizes to destination
	 * @param destination not null
	 * @param level recursion level
	 * @param cmpnt not null
	 */
	private static void prefSize(StringBuilder destination, int level, Component cmpnt) {
		Dimension dim = cmpnt.getPreferredSize();
		destination.append(StringUtils.repeat(' ', level) + ExecutionTrace.justClassName(cmpnt) + " prefers " + dim);
		destination.append('\n');
		Container container = BeanUtils.downcast(Container.class, cmpnt);
		if (container != null) {
			for ( Component c : container.getComponents()) {
				prefSize(destination, level + 1, c);
			}
		}
	}
	
	/**
	 * get preferred sizes of components. May not be valid if Component has not been displayed yet
	 * @param container
	 * @return nested String with components and sizes
	 */
	public static String getPreferredSizes(Component container) {
		StringBuilder sb = new StringBuilder(1024);
		prefSize(sb,0,container);
		return sb.toString();
	}
	
	/**
	 * attempt to describe window
	 * @param w could be null
	 * @return null or description String (e.g. title) 
	 */
	public static String describe(Window w) {
		if (w != null) {
			String className = ExecutionTrace.justClassName(w) + " ";
			Frame f = BeanUtils.downcast(Frame.class, w);
			if (f != null) {
				return className + f.getTitle();
			}
			Dialog d = BeanUtils.downcast(Dialog.class, w);
			if (d != null) {
				return className + d.getTitle();
			}
			return className; 
		}
		return "null";
		
	}
	
	
	
}
