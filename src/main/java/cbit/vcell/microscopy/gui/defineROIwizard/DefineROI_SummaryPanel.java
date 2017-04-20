/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.defineROIwizard;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.vcell.util.BeanUtils;
import org.vcell.util.NumberUtils;

import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPDataAnalysis;
import cbit.vcell.microscopy.FRAPStudy;

@SuppressWarnings("serial")
public class DefineROI_SummaryPanel extends JPanel
{
	public static final String START_IDX_AVAILABLE_STR = "";
	public static final String START_IDX_UNAVAILABLE_STR = "Bleached ROI is required to auto-detect the starting index for recovery.";
	private JTextField imgYTextField;
	private JTextField imgXTextField;
	private JComboBox startIndexCombo = null;
	private JPanel infoPanel = null;
	private JPanel indexPanel = null;
	private JLabel roiInfo = null;
	private JCheckBox cellROIDefined = null;
	private JCheckBox cellROIUndefined = null;
	private JCheckBox bleachROIDefined = null;
	private JCheckBox bleachROIUndefined = null;
	private JCheckBox bgROIDefined = null;
	private JCheckBox bgROIUndefined = null;
	private JLabel startIndexAvaliableLabel = null;
	
	public DefineROI_SummaryPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7,7,0,7,7,7,7};
		setLayout(gridBagLayout);

		roiInfo = new JLabel();
		roiInfo.setForeground(new Color(0, 0, 128));
		roiInfo.setFont(new Font("", Font.BOLD | Font.ITALIC, 15));
		roiInfo.setText("ROIs have been saved.  Please varify the following information.");
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.gridx = 0;
		add(roiInfo, gridBagConstraints1);
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		gridBagConstraints2.gridy = 4;
		gridBagConstraints2.gridheight = 2;
		gridBagConstraints2.gridx = 0;
		add(getInfoPanel(), gridBagConstraints2);
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.ipadx = 175;
		gridBagConstraints3.anchor = GridBagConstraints.WEST;
		gridBagConstraints3.gridy = 10;
		gridBagConstraints3.gridheight = 4;
		gridBagConstraints3.gridx = 0;
		add(getIndexPanel(), gridBagConstraints3);
	}

	public JPanel getInfoPanel()
	{
		if(infoPanel == null)
		{
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,7,7,7};
			gridBagLayout.columnWidths = new int[] {7,7,7,0,0,7,7,7,0,0,7};
			infoPanel = new JPanel(gridBagLayout);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.gridx = 1;
			infoPanel.add(new JLabel("Image size after cropping: "), gridBagConstraints1);

			final JLabel eTimeLabel = new JLabel();
			eTimeLabel.setText("Image size X");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.anchor = GridBagConstraints.EAST;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.gridx = 5;
			infoPanel.add(eTimeLabel, gridBagConstraints3);

			imgXTextField = new JTextField(12);
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridx = 7;
			infoPanel.add(imgXTextField, gridBagConstraints);
			
			final JLabel sLabel2 = new JLabel();
			sLabel2.setText("um");
			final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
			gridBagConstraints_8.gridy = 0;
			gridBagConstraints_8.gridx = 9;
			infoPanel.add(sLabel2, gridBagConstraints_8);

			final JLabel eTimeLabel_1 = new JLabel();
			eTimeLabel_1.setText("Image size Y");
			final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
			gridBagConstraints_2.gridy = 1;
			gridBagConstraints_2.gridx = 5;
			infoPanel.add(eTimeLabel_1, gridBagConstraints_2);

			imgYTextField = new JTextField();
			imgYTextField.setColumns(12);
			final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
			gridBagConstraints_4.gridy = 1;
			gridBagConstraints_4.gridx = 7;
			infoPanel.add(imgYTextField, gridBagConstraints_4);

			final JLabel sLabel2_1 = new JLabel();
			sLabel2_1.setText("um");
			final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
			gridBagConstraints_3.gridy = 1;
			gridBagConstraints_3.gridx = 9;
			infoPanel.add(sLabel2_1, gridBagConstraints_3);

			final JLabel cellAreaRoiLabel = new JLabel();
			cellAreaRoiLabel.setText("Cell area ROI: ");
			final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
			gridBagConstraints_5.anchor = GridBagConstraints.EAST;
			gridBagConstraints_5.gridy = 3;
			gridBagConstraints_5.gridx = 1;
			infoPanel.add(cellAreaRoiLabel, gridBagConstraints_5);

			cellROIDefined = new JCheckBox();
			cellROIDefined.setText("Defined");
			final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
			gridBagConstraints_6.anchor = GridBagConstraints.WEST;
			gridBagConstraints_6.gridy = 3;
			gridBagConstraints_6.gridx = 5;
			infoPanel.add(cellROIDefined, gridBagConstraints_6);

			cellROIUndefined = new JCheckBox();
			cellROIUndefined.setText("Not Defined");
			final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
			gridBagConstraints_7.anchor = GridBagConstraints.WEST;
			gridBagConstraints_7.gridy = 3;
			gridBagConstraints_7.gridx = 7;
			infoPanel.add(cellROIUndefined, gridBagConstraints_7);
			
			final JLabel bleachedRoiLabel = new JLabel();
			bleachedRoiLabel.setText("Bleached ROI: ");
			final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
			gridBagConstraints_9.anchor = GridBagConstraints.EAST;
			gridBagConstraints_9.gridy = 4;
			gridBagConstraints_9.gridx = 1;
			infoPanel.add(bleachedRoiLabel, gridBagConstraints_9);

			bleachROIDefined = new JCheckBox();
			bleachROIDefined.setText("Defined");
			final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
			gridBagConstraints_10.anchor = GridBagConstraints.WEST;
			gridBagConstraints_10.gridy = 4;
			gridBagConstraints_10.gridx = 5;
			infoPanel.add(bleachROIDefined, gridBagConstraints_10);

			bleachROIUndefined = new JCheckBox();
			bleachROIUndefined.setText("Not Defined");
			final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
			gridBagConstraints_11.anchor = GridBagConstraints.WEST;
			gridBagConstraints_11.gridy = 4;
			gridBagConstraints_11.gridx = 7;
			infoPanel.add(bleachROIUndefined, gridBagConstraints_11);
			
			final JLabel backgroundRoiLabel = new JLabel();
			backgroundRoiLabel.setText("Background ROI: ");
			final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
			gridBagConstraints_12.anchor = GridBagConstraints.EAST;
			gridBagConstraints_12.gridy = 5;
			gridBagConstraints_12.gridx = 1;
			infoPanel.add(backgroundRoiLabel, gridBagConstraints_12);

			bgROIDefined = new JCheckBox();
			bgROIDefined.setText("Defined");
			final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
			gridBagConstraints_13.anchor = GridBagConstraints.WEST;
			gridBagConstraints_13.gridy = 5;
			gridBagConstraints_13.gridx = 5;
			infoPanel.add(bgROIDefined, gridBagConstraints_13);

			bgROIUndefined = new JCheckBox();
			bgROIUndefined.setText("Not Defined");
			final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
			gridBagConstraints_14.anchor = GridBagConstraints.WEST;
			gridBagConstraints_14.gridy = 5;
			gridBagConstraints_14.gridx = 7;
			infoPanel.add(bgROIUndefined, gridBagConstraints_14);
			infoPanel.setBorder(new TitledBorder(new LineBorder(new Color(153, 186,243), 1),"Image & ROI Info"));
		}
		return infoPanel;
	}
	
	public JPanel getIndexPanel()
	{
		if(indexPanel == null)
		{
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,7,7};
			gridBagLayout.columnWidths = new int[] {7,7,7};
			indexPanel = new JPanel(gridBagLayout);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.gridx = 1;
			indexPanel.add(new JLabel("Start Recovery Index"), gridBagConstraints1);

			startIndexCombo = new JComboBox();
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridx = 3;
			indexPanel.add(startIndexCombo, gridBagConstraints);

			final JLabel umLabel1 = new JLabel();
			umLabel1.setText(" s");
			final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
			gridBagConstraints_2.gridy = 0;
			gridBagConstraints_2.gridx = 5;
			indexPanel.add(umLabel1, gridBagConstraints_2);

			startIndexAvaliableLabel = new JLabel();
			startIndexAvaliableLabel.setText("");
			startIndexAvaliableLabel.setForeground(Color.red);
			final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
			gridBagConstraints_1.gridwidth = 4;
			gridBagConstraints_1.gridy = 1;
			gridBagConstraints_1.gridx = 1;
			indexPanel.add(startIndexAvaliableLabel, gridBagConstraints_1);
			indexPanel.setBorder(new TitledBorder(new LineBorder(new Color(153, 186,243), 1),"Adjust Starting Index for Recovery"));
		}
		return indexPanel;
	}
	
	public void setLoadInfo(final FRAPStudy frapStudy)
	{
		if(frapStudy!=null && frapStudy.getFrapData()!=null)
		{
			//enable components to change values
			BeanUtils.enableComponents(getInfoPanel(), true);
			BeanUtils.enableComponents(getIndexPanel(), true);
			//set info panel
			ImageDataset imgDataset = frapStudy.getFrapData().getImageDataset();
			imgXTextField.setText(NumberUtils.formatNumber(imgDataset.getExtent().getX(), 15));
			imgYTextField.setText(NumberUtils.formatNumber(imgDataset.getExtent().getY(), 15));
			FRAPData fData = frapStudy.getFrapData();
			ROI cellROI = fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
			ROI bleachedROI = fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
			ROI backgroundROI = fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name());
			if(cellROI.getNonzeroPixelsCount()<1)
			{
				cellROIUndefined.setSelected(true);
				cellROIDefined.setSelected(false);
			}
			else
			{
				cellROIUndefined.setSelected(false);
				cellROIDefined.setSelected(true);
			}
			if(bleachedROI.getNonzeroPixelsCount()<1)
			{
				bleachROIUndefined.setSelected(true);
				bleachROIDefined.setSelected(false);
			}
			else
			{
				bleachROIUndefined.setSelected(false);
				bleachROIDefined.setSelected(true);
			}
			if(backgroundROI.getNonzeroPixelsCount()<1)
			{
				bgROIUndefined.setSelected(true);
				bgROIDefined.setSelected(false);
			}
			else
			{
				bgROIUndefined.setSelected(false);
				bgROIDefined.setSelected(true);
			}
			//they are not editable
			imgXTextField.setEditable(false);
			imgYTextField.setEditable(false);
			cellROIDefined.setEnabled(false);
			cellROIUndefined.setEnabled(false);
			bleachROIDefined.setEnabled(false);
			bleachROIUndefined.setEnabled(false);
			bgROIDefined.setEnabled(false);
			bgROIUndefined.setEnabled(false);

			//set index panel
			startIndexCombo.removeAllItems();
			double[] timeSteps = frapStudy.getFrapData().getImageDataset().getImageTimeStamps();
			for(int i =0 ; i<timeSteps.length; i++)
			{
				startIndexCombo.addItem(timeSteps[i] + "");
			}
			if(bleachROIDefined.isSelected())
			{
				int index = (frapStudy.getStartingIndexForRecovery()==null?FRAPDataAnalysis.calculateRecoveryIndex(frapStudy.getFrapData()):frapStudy.getStartingIndexForRecovery());
				startIndexCombo.setSelectedIndex(index);
				startIndexAvaliableLabel.setText(START_IDX_AVAILABLE_STR);
			}
			else
			{
				startIndexCombo.setSelectedIndex(0);
				startIndexAvaliableLabel.setText(START_IDX_UNAVAILABLE_STR);
			}
		}
		else
		{
			imgXTextField.setText("");
			imgYTextField.setText("");
			cellROIDefined.setSelected(false);
			cellROIUndefined.setSelected(false);
			bleachROIDefined.setSelected(false);
			bleachROIUndefined.setSelected(false);
			bgROIDefined.setSelected(false);
			bgROIUndefined.setSelected(false);
			startIndexCombo.removeAllItems();
			startIndexCombo.addItem("     N/A     ");
			
			BeanUtils.enableComponents(getInfoPanel(), false);
			BeanUtils.enableComponents(getIndexPanel(), false);
		}
	}
	

	
	public String checkInputValidity()
	{
		String errMsg = "";
		if(cellROIUndefined.isSelected())
		{
			errMsg = "Cell ROI, ";
		}
		if(bleachROIUndefined.isSelected())
		{
			errMsg = "Bleached ROI, ";
		}
		if(bgROIUndefined.isSelected())
		{
			errMsg = "Background ROI, ";
		}
		if(!errMsg.equals(""))
		{
			errMsg = errMsg + "must be defined for further process.\n";
		}
		if(startIndexCombo.getSelectedIndex() == 0)
		{
			errMsg = errMsg + "Pre-bleach images are required. Starting Index for recovery should not be the first image.";
		}
		return errMsg;
	}
	
	public int getStartingIndex()
	{
		return startIndexCombo.getSelectedIndex();
	}
	
	
}
