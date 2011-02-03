package org.vcell.util.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import cbit.vcell.desktop.BioModelNode;

public class GuiUtils {
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
}
