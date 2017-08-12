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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableColumn;

import org.vcell.util.gui.ScrollTable;

import cbit.vcell.model.Model;

/**
 * The panel is used to accommodate the TransformMassActionTable, which 
 * intuitively shows the possible reactions(whose rate laws are not
 * mass actions) transform to Mass Actions.
 * @author Tracy LI
 */
public class TransformMassActionPanel extends JPanel implements ActionListener {
	
	private Model fieldModel = null;
	private TransformMassActionTableModel transMATableModel = null;
	private ScrollTable transMATable = null;	
	
	public TransformMassActionPanel()
	{
		initialize();
		initConnections();
	}// end of constructor

	public void actionPerformed(ActionEvent arg0) {
		
		
	}
	
	private void initialize() {
		//set panel name, layout and size
		setName("TransformMassActionPanel");
		setLayout(new java.awt.BorderLayout());
		setPreferredSize(new Dimension(700, 250));
		//setSize(800, 250);
		//set border
		EtchedBorder eb=new EtchedBorder(EtchedBorder.LOWERED);
	    setBorder(eb);
	    //add some invisibel labels to make the dialog looks nicer.
	    add(new JLabel("List of all the reactions :"), BorderLayout.NORTH);
	    add(new JLabel(" "), BorderLayout.WEST);
	    add(new JLabel(" "), BorderLayout.EAST);
		//add table scrollPane
		add(getTable().getEnclosingScrollPane(), BorderLayout.CENTER);
	}
	
	private void initConnections()
	{
		
	}
	
	private ScrollTable getTable()
	{
		if(transMATable == null)
		{
			transMATable = new ScrollTable()
			{
				//Implement table cell tool tips.
			    public String getToolTipText(MouseEvent e) {
			      String tip = null;
			      java.awt.Point p = e.getPoint();
			      int rowIndex = rowAtPoint(p);
			      int colIndex = columnAtPoint(p);
			      int realColumnIndex = convertColumnIndexToModel(colIndex);

			      if (realColumnIndex == TransformMassActionTableModel.COLUMN_REMARK) { //Remark column
			        tip = getValueAt(rowIndex, colIndex)+"";
			      } 
			      else {
			        //You can omit this part if you know you don't
			        //have any renderers that supply their own tool
			        //tips.
			        tip = super.getToolTipText(e);
			      }
			      return tip;
			    }
			};
			transMATable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			transMATable.setName("ScrollPaneTable1");
			transMATable.setBounds(0, 0, 450, 400);
			//add table model
			transMATable.setModel(getTableModel());
			transMATable.createDefaultColumnsFromModel();
			
			// set cell renderer
			for(int i=0; i<transMATable.getModel().getColumnCount(); i++)
			{
				TableColumn column=transMATable.getColumnModel().getColumn(i);
				column.setCellRenderer(new TransformMassActionTableRenderer(transMATable.getDefaultRenderer(Boolean.class)));
			}
		}
		return transMATable;
	}
	
	private TransformMassActionTableModel getTableModel()
	{
		if(transMATableModel == null)
		{
			transMATableModel = new TransformMassActionTableModel();
			return transMATableModel;
		}
		return transMATableModel;
	}
	
	public Model getModel() {
		return fieldModel;
	}

	public void setModel(Model argModel) {
		fieldModel = argModel;
		getTableModel().setModel(argModel);
	}
	
	public void saveTransformedReactions() throws Exception
	{
		try
		{
			getTableModel().saveTransformedReactions();
		}catch(Exception e)
		{
			throw new Exception(e.getMessage());
		}
	}
	
}
