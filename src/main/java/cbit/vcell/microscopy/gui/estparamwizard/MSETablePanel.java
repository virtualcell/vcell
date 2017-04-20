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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.vcell.util.gui.AdvancedTablePanel;
import org.vcell.util.gui.HyperLinkLabel;
import org.vcell.util.gui.StyleTable;

import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.gui.NumericTableCellRenderer;

@SuppressWarnings("serial")
public class MSETablePanel extends AdvancedTablePanel
{

    private MSEPanel parent;
    private JLabel modelLabel;
    private HyperLinkLabel hypDetail;
    private JScrollPane scrTable;
    MSETableModel mseTableModel;
    JPopupMenu popupMenu;
    private FRAPSingleWorkspace frapWorkspace;
    
    public MSETablePanel(MSEPanel arg_parent/*may need to pass in frapstudy as parameter*/) 
    {
    	super();
    	this.parent = arg_parent;
        final GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JLabel headingLabel = new JLabel("Models under Selected ROIs");
        headingLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        hypDetail = new HyperLinkLabel("Less Details", new HyperLinkListener(), 0);
        hypDetail.setHorizontalAlignment(JLabel.RIGHT);

        modelLabel = new JLabel("3 Models");
        modelLabel.setOpaque(true);
        modelLabel.setBackground(new Color(166, 166, 255));
        modelLabel.setBorder(BorderFactory.createEmptyBorder(1,5,1,1));

        GridBagConstraints gc1 = new GridBagConstraints();
        gc1.gridx = 0;
        gc1.gridy = 0;
        gc1.weightx = 1.0;
        gc1.anchor = GridBagConstraints.WEST;
        gc1.fill = GridBagConstraints.HORIZONTAL;
        add(headingLabel, gc1);

        GridBagConstraints gc2 = new GridBagConstraints();
        gc2.fill = GridBagConstraints.HORIZONTAL;
        gc2.gridx = 1;
        gc2.gridy = 0;
        gc2.anchor = GridBagConstraints.EAST;
        add(hypDetail, gc2);

        GridBagConstraints gc3 = new GridBagConstraints();
        gc3.anchor = GridBagConstraints.WEST;
        gc3.gridx = 0;
        gc3.gridy = 1; 
        gc3.gridwidth = 2;
        gc3.weightx = 1.0;
        gc3.fill = GridBagConstraints.BOTH;
        add(modelLabel, gc3);
        //create table model
        mseTableModel = new MSETableModel();
        //by default, expend this table
        if (table == null) setupTable();
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 1;
        gc.gridwidth = 2;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        //By default, if you don't place a table in a scroll pane, the table  header  is not  shown. You need to explicitly display it.
        add(table.getTableHeader(), gc); 
        gc.weighty = 1.0;
        gc.gridy = 2;
        gc.fill = GridBagConstraints.BOTH;
        add(table, gc);
        setDetail(true);
   }



    private class HyperLinkListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            boolean isDetail = hypDetail.getText().equals("Details");
            setDetail(isDetail);
            hypDetail.setText(isDetail? "Less Details" : "Details");
        }

    }

    public void setDetail(boolean isDetail) {
    	if (isDetail) {
            modelLabel.setVisible(false);
            if(table != null)
            {
            	table.getTableHeader().setVisible(true);
            	table.setVisible(true);
            }
        }
        else {
            if (table != null) {
            	table.getTableHeader().setVisible(false);
            	table.setVisible(false);
            }
            if(frapWorkspace != null)
            {
            	modelLabel.setText(frapWorkspace.getWorkingFrapStudy().getSelectedModels().size() + " Models");
            }
            modelLabel.setVisible(true);
        }
        parent.repaint();
    }

    private void setupTable() {
        table = new StyleTable();
        table.setCellSelectionEnabled(true);
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(mseTableModel);

        TableCellRenderer mseRenderer = new  NumericTableCellRenderer(6);//double precision 6 digits
        TableColumn[] columns = new TableColumn[MSETableModel.NUM_COLUMNS];
        for (int i = 0; i < mseTableModel.getColumnCount(); i++) {
            columns[i] = new TableColumn(i, 0, mseRenderer, null);
            table.addColumn(columns[i]);
        }
        
        table.addMouseListener(evtHandler);
        scrTable = new JScrollPane(table);
        scrTable.setAutoscrolls(true);
    }

    public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace)
	{
    	this.frapWorkspace = frapWorkspace;
		mseTableModel.setFrapWorkspace(frapWorkspace);
	}
}


    
