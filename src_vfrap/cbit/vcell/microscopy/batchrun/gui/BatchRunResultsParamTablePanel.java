/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.batchrun.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.vcell.util.gui.AdvancedTablePanel;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.HyperLinkLabel;
import org.vcell.util.gui.StyleTable;

import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.gui.NumericTableCellRenderer;
import cbit.vcell.microscopy.gui.estparamwizard.EstParams_OneDiffComponentPanel;
import cbit.vcell.microscopy.gui.estparamwizard.EstParams_TwoDiffComponentPanel;
import cbit.vcell.microscopy.gui.estparamwizard.EstParams_ReactionOffRatePanel;

@SuppressWarnings("serial")
public class BatchRunResultsParamTablePanel extends JPanel implements PropertyChangeListener
{
	private AdvancedTablePanel paramTablePanel;
	private AdvancedTablePanel statTablePanel;
	private JTable table_param;
	private JTable table_stat;
    private BatchRunResultsParameterPanel parent;
    private JLabel modelLabel;
    private HyperLinkLabel hypDetail;
    private BatchRunResultsParamTableModel resultsTableModel = null;
    private BatchRunResultsStatTableModel statTableModel = null;
    private FRAPBatchRunWorkspace batchRunWorkspace = null;
    private EstParams_OneDiffComponentPanel oneDiffComponentPanel = null;
	private EstParams_TwoDiffComponentPanel twoDiffComponentPanel = null;
	private EstParams_ReactionOffRatePanel offRatePanel = null;
   
	public BatchRunResultsParamTablePanel(BatchRunResultsParameterPanel arg_parent) 
    {
    	this.parent = arg_parent;
        final GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0,0};
        setLayout(gridBagLayout);
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JLabel lblConfHeading = new JLabel("Documents");
        lblConfHeading.setFont(new Font("Tahoma", Font.BOLD, 11));
        hypDetail = new HyperLinkLabel("Less Details", new HyperLinkListener(), 0);
        hypDetail.setHorizontalAlignment(JLabel.RIGHT);
        
        modelLabel = new JLabel("Many VFRAP Docs");
        modelLabel.setOpaque(true);
        modelLabel.setBackground(new Color(166, 166, 255));
        modelLabel.setBorder(BorderFactory.createEmptyBorder(1,5,1,1));

        GridBagConstraints gc1 = new GridBagConstraints();
        gc1.gridx = 0;
        gc1.gridy = 0;
        gc1.weightx = 0.0;
        gc1.anchor = GridBagConstraints.WEST;
        gc1.fill = GridBagConstraints.HORIZONTAL;
        add(lblConfHeading, gc1);

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
        resultsTableModel = new BatchRunResultsParamTableModel();
        statTableModel = new BatchRunResultsStatTableModel();
        //by default, expend this table
        if (table_stat == null) setupTable_stat();
        if (table_param == null) setupTable_param();
        GridBagConstraints gc4 = new GridBagConstraints();
        gc4.gridx = 0;
        gc4.gridy = 1;
        gc4.gridwidth = 2;
        gc4.weightx = 1;
        gc4.fill = GridBagConstraints.HORIZONTAL;
        add(statTablePanel, gc4);//add statistics panel
        //add some distance between two tables
        GridBagConstraints gc6 = new GridBagConstraints();
        gc6.gridx = 0;
        gc6.gridy = 2;
        gc6.gridwidth = 2;
        gc6.weightx = 1;
        gc6.fill = GridBagConstraints.HORIZONTAL;
        add(Box.createRigidArea(new Dimension(0,40)),gc6);

        GridBagConstraints gc5 = new GridBagConstraints();
        gc5.gridx = 0;
        gc5.gridy = 3;
        gc5.gridwidth = 2;
        gc5.weightx = 1;
        gc5.fill = GridBagConstraints.HORIZONTAL;
        add(paramTablePanel, gc5);//add parameters panel
        
        //add distance after the parameters table
        GridBagConstraints gc7 = new GridBagConstraints();
        gc7.gridx = 0;
        gc7.gridy = 2;
        gc7.gridwidth = 2;
        gc7.weightx = 1;
        gc7.fill = GridBagConstraints.HORIZONTAL;
        add(Box.createRigidArea(new Dimension(0,20)),gc7);
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
            statTablePanel.setVisible(true);
            paramTablePanel.setVisible(true);
        }
        else {
            if (table_param != null) {
            	paramTablePanel.setVisible(false);
            }
            if (table_stat != null) {
            	statTablePanel.setVisible(false);
            }
            if(batchRunWorkspace != null)
            {
            	modelLabel.setText(batchRunWorkspace.getFrapStudies().size() + " Documents");
            }
            modelLabel.setVisible(true);
        }
        parent.repaint();
    }

    private void setupTable_param() {

    	paramTablePanel = new AdvancedTablePanel();
        TableSorter sorter = new TableSorter(resultsTableModel);
        table_param = new StyleTable(sorter);
        table_param.setCellSelectionEnabled(true);
        sorter.setTableHeader(table_param.getTableHeader());
        
        //apply table renderer for name column
        TableColumn nameCol = table_param.getColumnModel().getColumn(BatchRunResultsParamTableModel.COLUMN_FILE_NAME);
        ResultsParamTableRenderer resultsParamTableRenderer = new ResultsParamTableRenderer();
		nameCol.setCellRenderer(resultsParamTableRenderer);
		nameCol.setPreferredWidth(75);
        //apply table renderer and table editor for details column
        TableColumn detailsCol = table_param.getColumnModel().getColumn(BatchRunResultsParamTableModel.COLUMN_DETAILS);
        detailsCol.setCellRenderer(resultsParamTableRenderer);
        detailsCol.setPreferredWidth(35);
        //apply table renderer to the rest numeric columns
        TableCellRenderer resultsRanderer = new NumericTableCellRenderer(); //double precision 6 digits
        table_param.setDefaultRenderer(Double.class, resultsRanderer);
        
        for (int i = 1; i < table_param.getColumnCount()-1; i++) {
        	TableColumn col = table_param.getColumnModel().getColumn(i);
        	col.setPreferredWidth(0);
        }
        
        ResultsParamTableEditor tableEditor = new ResultsParamTableEditor(table_param);
        tableEditor.addPropertyChangeListener(this);
        detailsCol.setCellEditor(tableEditor);
        //add table to panel.By default, if you don't place a table in a scroll pane, the table  header  is not  shown. You need to explicitly display it. 
        paramTablePanel.setTable(table_param);
        paramTablePanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.gridx =0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        paramTablePanel.add(table_param.getTableHeader(), gc);
        gc.gridy = 1;
        gc.gridx =0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        paramTablePanel.add(table_param, gc);
    }
    
    private void setupTable_stat() {
    	statTablePanel = new AdvancedTablePanel();
        table_stat = new StyleTable();
        table_stat.setModel(statTableModel);
        table_stat.setCellSelectionEnabled(true);
        
        TableColumn nameCol = table_stat.getColumnModel().getColumn(BatchRunResultsParamTableModel.COLUMN_FILE_NAME);
        nameCol.setCellRenderer(new ResultsParamTableRenderer());
        //set the numeric columns' renders
        TableCellRenderer statRenderer = new  NumericTableCellRenderer();//double precision 6 digits
        for (int i = 1; i < table_stat.getColumnCount(); i++) {
        	TableColumn col = table_stat.getColumnModel().getColumn(i);
        	col.setPreferredWidth(0);
        	col.setCellRenderer(statRenderer);
        }
        //add table to panel.By default, if you don't place a table in a scroll pane, the table  header  is not  shown. You need to explicitly display it.
        statTablePanel.setTable(table_stat);
        statTablePanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.gridx =0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        statTablePanel.add(table_stat.getTableHeader(),gc);
        gc.gridy = 1;
        gc.gridx =0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        statTablePanel.add(table_stat,gc);
    }

    public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace)
	{
    	this.batchRunWorkspace = batchRunWorkspace;
		resultsTableModel.setBatchRunWorkspace(batchRunWorkspace);
		statTableModel.setBatchRunWorkspace(batchRunWorkspace);
	}
    
    public EstParams_OneDiffComponentPanel getOneDiffComponentPanel() {
    	if(oneDiffComponentPanel == null)
    	{
    		oneDiffComponentPanel = new EstParams_OneDiffComponentPanel();
    		oneDiffComponentPanel.setPreferredSize(new Dimension(980,680));
    	}
		return oneDiffComponentPanel;
	}

	public void setOneDiffComponentPanel(EstParams_OneDiffComponentPanel oneDiffComponentPanel) {
		this.oneDiffComponentPanel = oneDiffComponentPanel;
	}
    
	public EstParams_TwoDiffComponentPanel getTwoDiffComponentPanel() {
		if(twoDiffComponentPanel == null)
		{
			twoDiffComponentPanel = new EstParams_TwoDiffComponentPanel();
			twoDiffComponentPanel.setPreferredSize(new Dimension(980,680));
		}
		return twoDiffComponentPanel;
	}

	public void setTwoDiffComponentPanel(EstParams_TwoDiffComponentPanel twoDiffComponentPanel) {
		this.twoDiffComponentPanel = twoDiffComponentPanel;
	}
	
	public EstParams_ReactionOffRatePanel getOffRatePanel() {
		if(offRatePanel == null)
		{
			offRatePanel = new EstParams_ReactionOffRatePanel();
			offRatePanel.setPreferredSize(new Dimension(980,680));
		}
		return offRatePanel;
	}

	public void setOffRatePanel(EstParams_ReactionOffRatePanel offRatePanel) {
		this.offRatePanel = offRatePanel;
	}
	
	public void updateTableData()
	{
		resultsTableModel.fireTableDataChanged();
		statTableModel.fireTableDataChanged();
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		
		if(evt.getPropertyName().equals(FRAPBatchRunWorkspace.PROPERTY_CHANGE_BATCHRUN_DETAILS))
		{
			int rowNum = ((Integer)evt.getNewValue()).intValue();
			String fileName = ((File)table_param.getValueAt(rowNum, BatchRunResultsParamTableModel.COLUMN_FILE_NAME)).getAbsolutePath();
//			System.out.println("FileName---" + fileName);
			FRAPStudy selectedFrapStudy = batchRunWorkspace.getFRAPStudy(fileName);
			//display estimation result for each selected frapStudy based on model type
			if(batchRunWorkspace.getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT)
			{
				try {
					batchRunWorkspace.getWorkingSingleWorkspace().setFrapStudy(selectedFrapStudy, false, true);
					getOneDiffComponentPanel().setFrapWorkspace(batchRunWorkspace.getWorkingSingleWorkspace());
					getOneDiffComponentPanel().setData(selectedFrapStudy.getFrapOptData(), selectedFrapStudy.getFrapData(),
							                           selectedFrapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters(),
							                           selectedFrapStudy.getFrapData().getImageDataset().getImageTimeStamps(),
							                           selectedFrapStudy.getStartingIndexForRecovery(),
							                           selectedFrapStudy.getSelectedROIsForErrorCalculation());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int choice = DialogUtils.showComponentOKCancelDialog(JOptionPane.getFrameForComponent(BatchRunResultsParamTablePanel.this), getOneDiffComponentPanel(), "Estimation Details for "+selectedFrapStudy.getXmlFilename());
				if(choice == JOptionPane.OK_OPTION)
				{
					selectedFrapStudy.getFrapModel(FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT).setModelParameters(getOneDiffComponentPanel().getCurrentParameters());
					batchRunWorkspace.refreshStatisticsData();
				}
			}
			else if(batchRunWorkspace.getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
			{
				try {
					batchRunWorkspace.getWorkingSingleWorkspace().setFrapStudy(selectedFrapStudy, false, true);
					getTwoDiffComponentPanel().setFrapWorkspace(batchRunWorkspace.getWorkingSingleWorkspace());
					getTwoDiffComponentPanel().setData(selectedFrapStudy.getFrapOptData(), selectedFrapStudy.getFrapData(),
							                           selectedFrapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters(),
							                           selectedFrapStudy.getFrapData().getImageDataset().getImageTimeStamps(),
							                           selectedFrapStudy.getStartingIndexForRecovery(),
							                           selectedFrapStudy.getSelectedROIsForErrorCalculation());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int choice = DialogUtils.showComponentOKCancelDialog(JOptionPane.getFrameForComponent(BatchRunResultsParamTablePanel.this), getTwoDiffComponentPanel(), "Estimation Details for "+selectedFrapStudy.getXmlFilename());
				if(choice == JOptionPane.OK_OPTION)
				{
					selectedFrapStudy.getFrapModel(FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS).setModelParameters(getTwoDiffComponentPanel().getCurrentParameters());
					batchRunWorkspace.refreshStatisticsData();
				}
			}
			else if(batchRunWorkspace.getSelectedModel() == FRAPModel.IDX_MODEL_REACTION_OFF_RATE)
			{
				try {
					batchRunWorkspace.getWorkingSingleWorkspace().setFrapStudy(selectedFrapStudy, false, true);
					getOffRatePanel().setFrapWorkspace(batchRunWorkspace.getWorkingSingleWorkspace());
					getOffRatePanel().setData(selectedFrapStudy.getModels()[FRAPModel.IDX_MODEL_REACTION_OFF_RATE].getModelParameters(),
											  selectedFrapStudy.getFrapData(),
							                  selectedFrapStudy.getStartingIndexForRecovery());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int choice = DialogUtils.showComponentOKCancelDialog(JOptionPane.getFrameForComponent(BatchRunResultsParamTablePanel.this), getOffRatePanel(), "Estimation Details for "+selectedFrapStudy.getXmlFilename());
				if(choice == JOptionPane.OK_OPTION)
				{
					selectedFrapStudy.getFrapModel(FRAPModel.IDX_MODEL_REACTION_OFF_RATE).setModelParameters(getOffRatePanel().getCurrentParameters());
					batchRunWorkspace.refreshStatisticsData();
				}
			}
		}
	}
}

