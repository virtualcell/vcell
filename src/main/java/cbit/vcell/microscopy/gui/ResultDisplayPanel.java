/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.vcell.util.gui.AdvancedTablePanel;

import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.gui.FRAPStudyPanel.ResultPanelButtonHandler;


@SuppressWarnings("serial")
public class ResultDisplayPanel extends AdvancedTablePanel
{
	private JLabel oneDiffComponentLabel = null;
	private JLabel twoDiffComponentLabel = null;
	private JLabel reactionOffRateLabel = null;
	private JPanel titlePanel = null;
	private JPanel modelPanel = null;
	private JPanel tablePanel = null;
	private JPanel buttonPanel = null;
	private JButton show2DResultButton = null;
	private JButton runSimButton = null;
	
	private FRAPSingleWorkspace frapWorkspace = null;
	BestParameterTableModel tableModel = null;
	
	ResultDisplayPanel()
	{
		initial();
	}
	private void initial()
	{
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7,0,0,7,0,0,0,7,7,7,7,7,7,0,0,0,0,7,7,0,7,7,7};
		setLayout(gridBagLayout);
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.ipady = 0;
		gridBagConstraints1.ipadx = 0;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.ipadx = 45;
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.gridheight = 3;
		gridBagConstraints2.ipady = 0;
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.ipadx = 5;
		gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 10;
		gridBagConstraints2.gridheight = 4;
//		gridBagConstraints1.ipady = 0;
//		gridBagConstraints1.ipadx = 0;
		gridBagConstraints3.anchor = GridBagConstraints.WEST;
		
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.gridy = 22;
//		gridBagConstraints2.gridheight = 3;
//		gridBagConstraints2.ipady = 0;
//		gridBagConstraints2.ipadx = 0;
		
		add(getTitlePanel(), gridBagConstraints1);
		add(getBestModelPanel(), gridBagConstraints2);
		add(getTablePanel(), gridBagConstraints3);
		add(getButtonPanel(), gridBagConstraints4);
	}
	
	private JPanel getTitlePanel()
	{
		if(titlePanel == null)
		{
			titlePanel = new JPanel();
			JLabel titleLabel = new JLabel("Data Analysis Results");
			titleLabel.setFont(new Font("arial", Font.BOLD+Font.ITALIC, 14));
			titleLabel.setForeground(new Color(0,0,128));
			titlePanel.add(titleLabel, BorderLayout.WEST);
		}
		return titlePanel;
	}
	
	public JPanel getBestModelPanel()
	{
		if(modelPanel == null)
		{
			modelPanel = new JPanel();
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,7,0,7};
			gridBagLayout.columnWidths = new int[] {0,0,7,0,0,7,7,0,0,0,0,0,0,0,0,0,0,0,0,0};
			modelPanel = new JPanel(gridBagLayout);
			modelPanel.setBorder(new TitledBorder(new LineBorder(new Color(153, 186,243),1), "Best Model")); 
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 2;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
					
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridy = 4;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			
			modelPanel.add(getOneDiffComLabel(), gridBagConstraints1);
			modelPanel.add(getTwoDiffComLabel(), gridBagConstraints2);
			modelPanel.add(getRactionOffRateLabel(), gridBagConstraints3);
		}	
		return modelPanel;
	}
	
	public JLabel getOneDiffComLabel()
	{
		if(oneDiffComponentLabel == null)
		{
			oneDiffComponentLabel = new JLabel(" \u25CF  "+FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT]);
			oneDiffComponentLabel.setFont(new Font("arial", Font.PLAIN, 12));
		}
		return oneDiffComponentLabel;
	}
	
	public JLabel getTwoDiffComLabel()
	{
		if(twoDiffComponentLabel == null)
		{
			twoDiffComponentLabel = new JLabel(" \u25CF  "+FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS]);
			twoDiffComponentLabel.setFont(new Font("arial", Font.PLAIN, 12));
		}
		return twoDiffComponentLabel;
	}
	
	public JLabel getRactionOffRateLabel()
	{
		if(reactionOffRateLabel == null)
		{
			reactionOffRateLabel = new JLabel(" \u25CF  "+FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_REACTION_OFF_RATE]);
			reactionOffRateLabel.setFont(new Font("arial", Font.PLAIN, 12));
		}
		return reactionOffRateLabel;
	}
	
//	public JLabel getReacBindingLabel()
//	{
//		if(reacBindingLabel == null)
//		{
//			reacBindingLabel = new JLabel("Diffusion  plus  Binding");
//			reacBindingLabel.setFont(new Font("arial", Font.PLAIN, 12));
//		}
//		return reacBindingLabel;
//	}
	
	private void highLightOneDiffLabel()
	{
		oneDiffComponentLabel.setBorder(new LineBorder(Color.black, 2));
	}
	
	private void highLightTwoDiffLabel()
	{
		twoDiffComponentLabel.setBorder(new LineBorder(Color.black, 2));
	}
	
	private void highLightOffRateLabel()
	{
		reactionOffRateLabel.setBorder(new LineBorder(Color.black, 2));
	}
	
//	private void highLightReacBindingLabel()
//	{
//		reacBindingLabel.setBorder(new LineBorder(Color.black, 2));
//	}
	
	public void clearBestModelDisplay()
	{
		clearBestModelLabelHighlight();
		setRunSimButtonEnable(false);
		setResultsButtonEnabled(false);
	}
	
	public void clearBestModelLabelHighlight()
	{
		oneDiffComponentLabel.setBorder(null);
		twoDiffComponentLabel.setBorder(null);
		reactionOffRateLabel.setBorder(null);
//		reacBindingLabel.setBorder(null);
	}
	
	public void clearResultTable()
	{
		getBestParameterTableModel().setBestModelIndex(null);
		getRestultTable().removeAll();
	}
	
	private JPanel getTablePanel()
	{
		if(tablePanel == null)
		{
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,0,0,7,7};
			tablePanel = new JPanel(gridBagLayout);
			tablePanel.setBorder(new TitledBorder(new LineBorder(new Color(153, 186,243),1), "Best Parameters"));//new Color(153, 186,243)
			JScrollPane tableScroll = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tableScroll.setName("");
			tableScroll.setViewportView(getRestultTable());
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.ipady = 235;
			gridBagConstraints1.ipadx = 300;
			gridBagConstraints1.gridheight = 3;
			gridBagConstraints1.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
//			gridBagConstraints1.ipady = 0;
//			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;

			tablePanel.add(tableScroll, gridBagConstraints1);
		}
		return tablePanel;
	}
	 
	private JPanel getButtonPanel()
	{
		if(buttonPanel == null)
		{
			buttonPanel = new JPanel(new BorderLayout());
			buttonPanel.add(getShowResultButton(), BorderLayout.EAST);
			setRunSimButtonEnable(false); //enable it when best model is choosen and parameters are not null.
			setResultsButtonEnabled(false); //enable it after loading frap data/doc(if sim data exists).
			buttonPanel.add(getRunSimButton(), BorderLayout.WEST);
			buttonPanel.add(new JLabel("             "), BorderLayout.CENTER); //used to nicely arrange buttons
		}
		return buttonPanel;
	}
	
	public JButton getShowResultButton()
	{
		if(show2DResultButton == null)
		{
			show2DResultButton = new JButton("View Spatial Results");
			show2DResultButton.setActionCommand(VirtualFrapMainFrame.SHOW_SIM_RESULT_COMMAND);
		}
		return show2DResultButton;
	}
	
	public JButton getRunSimButton()
	{
		if(runSimButton == null)
		{
			runSimButton = new JButton("Run Spatial Simulation");
			runSimButton.setActionCommand(VirtualFrapMainFrame.RUN_SIM_COMMAND);
		}
		return runSimButton;
	}
	
	public JTable getRestultTable()
	{
		if(table == null)
		{
			table = new JTable();
			//set table model
			tableModel = getBestParameterTableModel(); 
			table.setModel(tableModel);//set table model
			table.setCellSelectionEnabled(true);
			table.addMouseListener(evtHandler);
			//set table renderer
			TableCellRenderer tableRenderer = new NumericTableCellRenderer(); //double precision 6 digits
			for(int i=0; i<tableModel.getColumnCount(); i++)
			{
				TableColumn column=table.getColumnModel().getColumn(i);
				column.setCellRenderer(tableRenderer);			
			}

		}
		return table;
	}
	
	private BestParameterTableModel getBestParameterTableModel()
	{
		if(tableModel == null)
		{
			tableModel = new BestParameterTableModel();
		}
		return tableModel;
	}
	
	public void setRunSimButtonEnable(boolean enabled)
	{
		getRunSimButton().setEnabled(enabled);
	}
	
	public void setResultsButtonEnabled(boolean enabled)
	{
		getShowResultButton().setEnabled(enabled);
	}
	
	public void setBestModel(int bestModelIndex, LocalWorkspace localWorkspace)
	{
		if(bestModelIndex == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT)
		{
			highLightOneDiffLabel();
		}
		else if(bestModelIndex == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
		{
			highLightTwoDiffLabel();
		}
		else if(bestModelIndex == FRAPModel.IDX_MODEL_REACTION_OFF_RATE)
		{
			highLightOffRateLabel();
		}
		else
		{
//			highLightReacBindingLabel();
		}
		//refresh parameter table and buttons
		FRAPStudy fStudy = getFRAPWorkspace().getWorkingFrapStudy();
		if(fStudy.getModels()[bestModelIndex].getModelParameters() != null && fStudy.getModels()[bestModelIndex].getModelParameters().length > 0)
		{
			getBestParameterTableModel().setBestModelIndex(bestModelIndex);
			if(bestModelIndex != FRAPModel.IDX_MODEL_REACTION_OFF_RATE)
			{
				setRunSimButtonEnable(true);
				if(fStudy.getBioModel() != null && fStudy.getBioModel().getSimulations() != null && fStudy.getBioModel().getSimulations().length > 0 &&
				   fStudy.getBioModel().getSimulations()[0] != null && fStudy.getBioModel().getSimulations()[0].getKey() != null &&
				   fStudy.getFrapDataExternalDataInfo() != null && fStudy.getRoiExternalDataInfo() != null &&
				   FRAPWorkspace.areSimulationFilesOK(localWorkspace, fStudy.getBioModel().getSimulations()[0].getKey()))
				{
					setResultsButtonEnabled(true);
				}
			}
		}
	}
	
	public FRAPSingleWorkspace getFRAPWorkspace() {
		return frapWorkspace;
	}
	public void setFRAPWorkspace(FRAPSingleWorkspace frapWorkspace) {
		FRAPSingleWorkspace oldFrapWorkspace = this.frapWorkspace;
		if(oldFrapWorkspace != null)
		{
			oldFrapWorkspace.removePropertyChangeListener(tableModel);
		}
		this.frapWorkspace = frapWorkspace;
		tableModel.setFrapWorkspace(frapWorkspace);
		this.frapWorkspace.addPropertyChangeListener(tableModel);
		
	}
	
	public void addButtonHandler(ResultPanelButtonHandler handler)
	{
		getRunSimButton().addActionListener(handler);
		getShowResultButton().addActionListener(handler);
	}
	
}
