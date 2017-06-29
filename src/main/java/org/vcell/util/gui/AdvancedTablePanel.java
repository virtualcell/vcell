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

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;


/*
 * This panel enables mouse events on the table which this panel
 * contains. Mouse left click and drag will select the table cells to
 * copy to clipboard. Mouse right click pops up a JPopup dialog showing 
 * the choices of 'copy' or 'copy all'.  
 * author: Tracy Li
 * version: 1.0
 */
@SuppressWarnings("serial")
public class AdvancedTablePanel  extends JPanel
{
	private JMenuItem copyMenu = null;
	private JMenuItem copyAllMenu = null;
	private JPopupMenu popupMenu = null;
	protected JTable table = null;
	protected EventHandler evtHandler = null;
	
	public AdvancedTablePanel()
	{
		super();
		evtHandler = new EventHandler();
		getJMenuItemCopy().addActionListener(evtHandler);
		getJMenuItemCopyAll().addActionListener(evtHandler);
	}
	
	private JMenuItem getJMenuItemCopy() {
		if (copyMenu == null) {
			copyMenu = new javax.swing.JMenuItem();
			copyMenu.setName("JMenuItemCopy");
			copyMenu.setText("Copy");
		}
		return copyMenu;
	}
	
	private JMenuItem getJMenuItemCopyAll() {
		if (copyAllMenu == null) {
			copyAllMenu = new javax.swing.JMenuItem();
			copyAllMenu.setName("JMenuItemCopyAll");
			copyAllMenu.setText("Copy All");
		}
		return copyAllMenu;
	}
	
	private JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new javax.swing.JPopupMenu();
			popupMenu.setName("JPopupMenu");
			popupMenu.add(getJMenuItemCopy());
			popupMenu.add(getJMenuItemCopyAll());
		}
		return popupMenu;
	}
	
	public JTable getTable()
	{
		return table;
	}
	
	public void setTable(JTable arg_table)
	{
		if(table != null)
		{
			table.removeMouseListener(evtHandler);
		}
		if(arg_table != null)
		{
			arg_table.addMouseListener(evtHandler);
		}
		table = arg_table;
	}
	
	class EventHandler implements ActionListener, MouseListener 
	{
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AdvancedTablePanel.this.getJMenuItemCopy()) 
				copyCells(e.getActionCommand());
			if (e.getSource() == AdvancedTablePanel.this.getJMenuItemCopyAll()) 
				copyCells(e.getActionCommand());
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == table && e.isPopupTrigger()){
				getPopupMenu().show(table, e.getPoint().x, e.getPoint().y);;
			}
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == table && e.isPopupTrigger()){
				getPopupMenu().show(table, e.getPoint().x, e.getPoint().y);;
			}
		};
	}
	
	private synchronized void copyCells(String actionCommand) {
		try{
			int r = 0;
			int c = 0;
			int[] rows = new int[0];
			int[] columns = new int[0];
			if (actionCommand.equals("Copy")) {
				r = table.getSelectedRowCount();
				c = table.getSelectedColumnCount();
				rows = table.getSelectedRows();
				columns = table.getSelectedColumns();
			}
			if (actionCommand.equals("Copy All")) {
				r = table.getRowCount();
				c = table.getColumnCount();
				rows = new int[r];
				columns = new int[c];
				for (int i = 0; i < rows.length; i++){
					rows[i] = i;
				}
				for (int i = 0; i < columns.length; i++){
					columns[i] = i;
				}
			}
			StringBuffer buffer = new StringBuffer();
			//copy selected cells to a string buffer
			if (r + c > 2) {
				// include column headers
				for (int i = 0; i < c; i++){
					buffer.append(table.getColumnName(columns[i]) + (i==c-1?"":"\t"));
				}
				// include table cell data
				for (int i = 0; i < r; i++){
					buffer.append("\n");
					for (int j = 0; j < c; j++){
						Object cell = table.getValueAt(rows[i], columns[j]);
						cell = cell != null ? cell : ""; 
						buffer.append(cell.toString() + (j==c-1?"":"\t"));
					}
				}
			}
			// if copying a single cell, just get that value, no column header
			if (r + c == 2) {
				Object cell = table.getValueAt(rows[0], columns[0]);
				cell = (cell != null ? cell : ""); 
				buffer.append(cell.toString());
			}
			FRAPClipboard clipboard = new FRAPClipboard(buffer.toString());
			clipboard.sendToClip();
			
		}catch(Throwable e){
			DialogUtils.showErrorDialog(AdvancedTablePanel.this, "AdvancedTablePanel copy failed.  "+e.getMessage());
		}
	}
}
