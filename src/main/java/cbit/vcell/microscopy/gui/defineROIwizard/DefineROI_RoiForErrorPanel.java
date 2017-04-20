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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cbit.plot.gui.Plot2DPanel;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.gui.ROIImagePanel;

@SuppressWarnings("serial")
public class DefineROI_RoiForErrorPanel extends JPanel implements ActionListener
{
	JPanel centerPanel = null;
	ROIImagePanel roiImagePanel = null;
	FRAPSingleWorkspace frapWorkspace = null;
	
	private JCheckBox roi_bleachedCheckBox = null;
	private JCheckBox roi_bleached_ring1CheckBox = null;
	private JCheckBox roi_bleached_ring2CheckBox = null;
	private JCheckBox roi_bleached_ring3CheckBox = null;
	private JCheckBox roi_bleached_ring4CheckBox = null;
	private JCheckBox roi_bleached_ring5CheckBox = null;
	private JCheckBox roi_bleached_ring6CheckBox = null;
	private JCheckBox roi_bleached_ring7CheckBox = null;
	private JCheckBox roi_bleached_ring8CheckBox = null;
	
	private JLabel roi_bleachedLabel = null;
	private JLabel roi_bleached_ring1Label = null;
	private JLabel roi_bleached_ring2Label = null;
	private JLabel roi_bleached_ring3Label = null;
	private JLabel roi_bleached_ring4Label = null;
	private JLabel roi_bleached_ring5Label = null;
	private JLabel roi_bleached_ring6Label = null;
	private JLabel roi_bleached_ring7Label = null;
	private JLabel roi_bleached_ring8Label = null;
	
	private static final int NUM_SELECTED_ROIS = FRAPData.VFRAP_ROI_ENUM.values().length-2; //exclude cell and background ROIs
	private static final int IDX_ROI_BLEACHED = 0;
	private static final int IDX_ROI_BLEACHED_RING1 = 1;
	private static final int IDX_ROI_BLEACHED_RING2 = 2;
	private static final int IDX_ROI_BLEACHED_RING3 = 3;
	private static final int IDX_ROI_BLEACHED_RING4 = 4;
	private static final int IDX_ROI_BLEACHED_RING5 = 5;
	private static final int IDX_ROI_BLEACHED_RING6 = 6;
	private static final int IDX_ROI_BLEACHED_RING7 = 7;
	private static final int IDX_ROI_BLEACHED_RING8 = 8;
	
	ROI[] allROIs = null;
	Color[] allROIColors = null;
	boolean[] selectedROIs = null;
	
	
	public DefineROI_RoiForErrorPanel() {
		super();
		setLayout(new BorderLayout());

		//pre-load colors
		allROIColors = getAllROIColors();
		
		JLabel selectRoisForLabel = new JLabel();
		selectRoisForLabel.setForeground(new Color(0, 0, 128));
		selectRoisForLabel.setFont(new Font("", Font.BOLD | Font.ITALIC, 16));
		selectRoisForLabel.setText("Select ROIs for Error Calculation");
		add(selectRoisForLabel, BorderLayout.NORTH);

		centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,7,7};
		gridBagLayout.rowHeights = new int[] {0,7,7,7,7,7,7,7,7,7,7,7,0,0,0,0,0,7};
		centerPanel.setLayout(gridBagLayout);

		final JLabel selectTheRegionsLabel = new JLabel();
		selectTheRegionsLabel.setForeground(new Color(0, 0, 128));
		selectTheRegionsLabel.setFont(new Font("", Font.BOLD, 12));
		selectTheRegionsLabel.setText("Select the regions that you want to use for computing the errors in optimization");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = 5;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		centerPanel.add(selectTheRegionsLabel, gridBagConstraints);

		roi_bleachedLabel = new JLabel();
		roi_bleachedLabel.setText("ROI_Bleached");
		roi_bleachedLabel.setIcon(createColorIcon(allROIColors[IDX_ROI_BLEACHED]));
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.WEST;
		gridBagConstraints_1.gridy = 3;
		gridBagConstraints_1.gridx = 1;
		centerPanel.add(roi_bleachedLabel, gridBagConstraints_1);

		roiImagePanel = new ROIImagePanel();
		roiImagePanel.setSize(200,150);
		roiImagePanel.setBackground(Color.black);
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.weighty = 1;
		gridBagConstraints_12.weightx = 1;
		gridBagConstraints_12.gridheight = 14;
		gridBagConstraints_12.gridy = 3;
		gridBagConstraints_12.gridx = 4;
		centerPanel.add(roiImagePanel, gridBagConstraints_12);

		roi_bleachedCheckBox = new JCheckBox("",true);
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.weightx = 0;
		gridBagConstraints_3.weighty = 1;
		gridBagConstraints_3.anchor = GridBagConstraints.WEST;
		gridBagConstraints_3.gridy = 3;
		gridBagConstraints_3.gridx = 0;
		centerPanel.add(roi_bleachedCheckBox, gridBagConstraints_3);
		roi_bleachedCheckBox.addActionListener(this);

		roi_bleached_ring1CheckBox = new JCheckBox("",true);
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.weighty = 1;
		gridBagConstraints_4.weightx = 0;
		gridBagConstraints_4.anchor = GridBagConstraints.WEST;
		gridBagConstraints_4.gridy = 4;
		gridBagConstraints_4.gridx = 0;
		centerPanel.add(roi_bleached_ring1CheckBox, gridBagConstraints_4);
		roi_bleached_ring1CheckBox.addActionListener(this);

		roi_bleached_ring1Label = new JLabel();
		roi_bleached_ring1Label.setText("ROI_Bleached_RING1");
		roi_bleached_ring1Label.setIcon(createColorIcon(allROIColors[IDX_ROI_BLEACHED_RING1]));
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.anchor = GridBagConstraints.WEST;
		gridBagConstraints_2.gridy = 4;
		gridBagConstraints_2.gridx = 1;
		centerPanel.add(roi_bleached_ring1Label, gridBagConstraints_2);
		
		roi_bleached_ring2CheckBox = new JCheckBox("",true);
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.weighty = 1;
		gridBagConstraints_5.weightx = 0;
		gridBagConstraints_5.anchor = GridBagConstraints.WEST;
		gridBagConstraints_5.gridy = 5;
		gridBagConstraints_5.gridx = 0;
		centerPanel.add(roi_bleached_ring2CheckBox, gridBagConstraints_5);
		roi_bleached_ring2CheckBox.addActionListener(this);

		roi_bleached_ring2Label = new JLabel();
		roi_bleached_ring2Label.setText("ROI_Bleached_RING2");
		roi_bleached_ring2Label.setIcon(createColorIcon(allROIColors[IDX_ROI_BLEACHED_RING2]));
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.anchor = GridBagConstraints.WEST;
		gridBagConstraints_13.gridy = 5;
		gridBagConstraints_13.gridx = 1;
		centerPanel.add(roi_bleached_ring2Label, gridBagConstraints_13);

		roi_bleached_ring3CheckBox = new JCheckBox("",true);
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.weighty = 1;
		gridBagConstraints_6.weightx = 0;
		gridBagConstraints_6.anchor = GridBagConstraints.WEST;
		gridBagConstraints_6.gridy = 6;
		gridBagConstraints_6.gridx = 0;
		centerPanel.add(roi_bleached_ring3CheckBox, gridBagConstraints_6);
		roi_bleached_ring3CheckBox.addActionListener(this);

		roi_bleached_ring3Label = new JLabel();
		roi_bleached_ring3Label.setText("ROI_Bleached_RING3");
		roi_bleached_ring3Label.setIcon(createColorIcon(allROIColors[IDX_ROI_BLEACHED_RING3]));
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.gridy = 6;
		gridBagConstraints_14.gridx = 1;
		centerPanel.add(roi_bleached_ring3Label, gridBagConstraints_14);

		roi_bleached_ring4CheckBox = new JCheckBox("",true);
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.weighty = 1;
		gridBagConstraints_7.weightx = 0;
		gridBagConstraints_7.anchor = GridBagConstraints.WEST;
		gridBagConstraints_7.gridy = 7;
		gridBagConstraints_7.gridx = 0;
		centerPanel.add(roi_bleached_ring4CheckBox, gridBagConstraints_7);
		roi_bleached_ring4CheckBox.addActionListener(this);

		roi_bleached_ring4Label = new JLabel();
		roi_bleached_ring4Label.setText("ROI_Bleached_RING4");
		roi_bleached_ring4Label.setIcon(createColorIcon(allROIColors[IDX_ROI_BLEACHED_RING4]));
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.gridy = 7;
		gridBagConstraints_15.gridx = 1;
		centerPanel.add(roi_bleached_ring4Label, gridBagConstraints_15);

		roi_bleached_ring5CheckBox = new JCheckBox("",true);
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.weighty = 1;
		gridBagConstraints_8.weightx = 0;
		gridBagConstraints_8.anchor = GridBagConstraints.WEST;
		gridBagConstraints_8.gridy = 8;
		gridBagConstraints_8.gridx = 0;
		centerPanel.add(roi_bleached_ring5CheckBox, gridBagConstraints_8);
		roi_bleached_ring5CheckBox.addActionListener(this);

		roi_bleached_ring5Label = new JLabel();
		roi_bleached_ring5Label.setText("ROI_Bleached_RING5");
		roi_bleached_ring5Label.setIcon(createColorIcon(allROIColors[IDX_ROI_BLEACHED_RING5]));
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.gridy = 8;
		gridBagConstraints_16.gridx = 1;
		centerPanel.add(roi_bleached_ring5Label, gridBagConstraints_16);

		roi_bleached_ring6CheckBox = new JCheckBox("",true);
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.weighty = 1;
		gridBagConstraints_9.weightx = 0;
		gridBagConstraints_9.anchor = GridBagConstraints.WEST;
		gridBagConstraints_9.gridy = 9;
		gridBagConstraints_9.gridx = 0;
		centerPanel.add(roi_bleached_ring6CheckBox, gridBagConstraints_9);
		roi_bleached_ring6CheckBox.addActionListener(this);

		roi_bleached_ring6Label = new JLabel();
		roi_bleached_ring6Label.setText("ROI_Bleached_RING6");
		roi_bleached_ring6Label.setIcon(createColorIcon(allROIColors[IDX_ROI_BLEACHED_RING6]));
		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.gridy = 9;
		gridBagConstraints_17.gridx = 1;
		centerPanel.add(roi_bleached_ring6Label, gridBagConstraints_17);

		roi_bleached_ring7CheckBox = new JCheckBox("",true);
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.weighty = 1;
		gridBagConstraints_10.weightx = 0;
		gridBagConstraints_10.anchor = GridBagConstraints.WEST;
		gridBagConstraints_10.gridy = 10;
		gridBagConstraints_10.gridx = 0;
		centerPanel.add(roi_bleached_ring7CheckBox, gridBagConstraints_10);
		roi_bleached_ring7CheckBox.addActionListener(this);

		roi_bleached_ring7Label = new JLabel();
		roi_bleached_ring7Label.setText("ROI_Bleached_RING7");
		roi_bleached_ring7Label.setIcon(createColorIcon(allROIColors[IDX_ROI_BLEACHED_RING7]));
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.gridy = 10;
		gridBagConstraints_18.gridx = 1;
		centerPanel.add(roi_bleached_ring7Label, gridBagConstraints_18);

		roi_bleached_ring8CheckBox = new JCheckBox("",true);
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.weighty = 1;
		gridBagConstraints_11.weightx = 0;
		gridBagConstraints_11.anchor = GridBagConstraints.WEST;
		gridBagConstraints_11.gridy = 11;
		gridBagConstraints_11.gridx = 0;
		centerPanel.add(roi_bleached_ring8CheckBox, gridBagConstraints_11);
		roi_bleached_ring8CheckBox.addActionListener(this);

		roi_bleached_ring8Label = new JLabel();
		roi_bleached_ring8Label.setText("ROI_Bleached_RING8");
		roi_bleached_ring8Label.setIcon(createColorIcon(allROIColors[IDX_ROI_BLEACHED_RING8]));
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.gridy = 11;
		gridBagConstraints_19.gridx = 1;
		centerPanel.add(roi_bleached_ring8Label, gridBagConstraints_19);
	}
	
	public ROI[] getAllROIs()
	{
		allROIs = new ROI[NUM_SELECTED_ROIS];
		FRAPData frapData = frapWorkspace.getWorkingFrapStudy().getFrapData();
		allROIs[IDX_ROI_BLEACHED] = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
		allROIs[IDX_ROI_BLEACHED_RING1] = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name());
		allROIs[IDX_ROI_BLEACHED_RING2] = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name());
		allROIs[IDX_ROI_BLEACHED_RING3] = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name());
		allROIs[IDX_ROI_BLEACHED_RING4] = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name());
		allROIs[IDX_ROI_BLEACHED_RING5] = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name());
		allROIs[IDX_ROI_BLEACHED_RING6] = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name());
		allROIs[IDX_ROI_BLEACHED_RING7] = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name());
		allROIs[IDX_ROI_BLEACHED_RING8] = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name());
		return allROIs;
	}
	
	public Color[] getAllROIColors()
	{
		if(allROIColors == null)
		{
			allROIColors = new Color[(FRAPData.VFRAP_ROI_ENUM.values().length-2)*2];//double valid ROI colors (not include cell and background)
			Color[] availableColors = Plot2DPanel.generateAutoColor(allROIColors.length, getBackground(), new Integer(0));
			System.arraycopy(availableColors, 0, allROIColors, 0, allROIColors.length);
		}
		return allROIColors;
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() instanceof JCheckBox)
		{
			refreshROIImage();
		}
	}
	
	public void refreshROIImage()
	{
		FRAPData frapData = frapWorkspace.getWorkingFrapStudy().getFrapData();
		ROI[] allROIs = getAllROIs();
		Color[] allColors = getAllROIColors();
		ArrayList<ROI> plottedROIs = new ArrayList<ROI>();
		ArrayList<Color> plottedColors = new ArrayList<Color>();
		ArrayList<String> tempSelectedROIs = new ArrayList<String>();
		
		if(roi_bleachedCheckBox.isEnabled() && roi_bleachedCheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED]);
			tempSelectedROIs.add(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
		}
		if(roi_bleached_ring1CheckBox.isEnabled() && roi_bleached_ring1CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING1]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING1]);
			tempSelectedROIs.add(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name());
		}
		if(roi_bleached_ring2CheckBox.isEnabled() && roi_bleached_ring2CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING2]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING2]);
			tempSelectedROIs.add(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name());
		}
		if(roi_bleached_ring3CheckBox.isEnabled() && roi_bleached_ring3CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING3]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING3]);
			tempSelectedROIs.add(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name());
		}
		if(roi_bleached_ring4CheckBox.isEnabled() && roi_bleached_ring4CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING4]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING4]);
			tempSelectedROIs.add(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name());
		}
		if(roi_bleached_ring5CheckBox.isEnabled() && roi_bleached_ring5CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING5]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING5]);
			tempSelectedROIs.add(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name());
		}
		if(roi_bleached_ring6CheckBox.isEnabled() && roi_bleached_ring6CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING6]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING6]);
			tempSelectedROIs.add(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name());
		}
		if(roi_bleached_ring7CheckBox.isEnabled() && roi_bleached_ring7CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING7]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING7]);
			tempSelectedROIs.add(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name());
		}
		if(roi_bleached_ring8CheckBox.isEnabled() && roi_bleached_ring8CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING8]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING8]);
			tempSelectedROIs.add(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name());
		}
		
		//save selected ROI names
		refreshSelectedROIs(tempSelectedROIs);
		//show ROI image
		ROI[] plottedROIArray = new ROI[plottedROIs.size()];
		plottedROIArray = plottedROIs.toArray(plottedROIArray);
		Color[] plottedColorArray = new Color[plottedColors.size()];
		plottedColorArray = plottedColors.toArray(plottedColorArray);
		roiImagePanel.refreshROIImage(plottedROIArray, plottedColorArray, frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()), Color.white);
	}
	
	private void refreshSelectedROIs(ArrayList<String> arg_selectedROIs)
	{
		selectedROIs = new boolean[FRAPData.VFRAP_ROI_ENUM.values().length];
		for(int i=0; i<FRAPData.VFRAP_ROI_ENUM.values().length; i++)
		{
			for(int j=0; j<arg_selectedROIs.size(); j++)
			{
				if(FRAPData.VFRAP_ROI_ENUM.values()[i].name().equals(arg_selectedROIs.get(j)))
				{
					selectedROIs[i] = true;
				}
			}
		}
	}
	
	public void setAllCheckboxesEnabled(boolean enabled)
	{
		roi_bleachedCheckBox.setEnabled(enabled);
		roi_bleached_ring1CheckBox.setEnabled(enabled);
		roi_bleached_ring2CheckBox.setEnabled(enabled);
		roi_bleached_ring3CheckBox.setEnabled(enabled);
		roi_bleached_ring4CheckBox.setEnabled(enabled);
		roi_bleached_ring5CheckBox.setEnabled(enabled);
		roi_bleached_ring6CheckBox.setEnabled(enabled);
		roi_bleached_ring7CheckBox.setEnabled(enabled);
		roi_bleached_ring8CheckBox.setEnabled(enabled);
	}
	
	public void setAllCheckboxesSelected(boolean selected)
	{
		roi_bleachedCheckBox.setSelected(selected);
		roi_bleached_ring1CheckBox.setSelected(selected);
		roi_bleached_ring2CheckBox.setSelected(selected);
		roi_bleached_ring3CheckBox.setSelected(selected);
		roi_bleached_ring4CheckBox.setSelected(selected);
		roi_bleached_ring5CheckBox.setSelected(selected);
		roi_bleached_ring6CheckBox.setSelected(selected);
		roi_bleached_ring7CheckBox.setSelected(selected);
		roi_bleached_ring8CheckBox.setSelected(selected);
	}
	
	public void setCheckboxesForDisplay(boolean[] selectedROIs)
	{
		setAllCheckboxesSelected(false);
		for(int i=0; i<FRAPData.VFRAP_ROI_ENUM.values().length; i++)
		{
			if(selectedROIs[i] && FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED))
			{
				roi_bleachedCheckBox.setSelected(true);
			}
			else if(selectedROIs[i] && FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1))
			{
				roi_bleached_ring1CheckBox.setSelected(true);
			}
			else if(selectedROIs[i] && FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2))
			{
				roi_bleached_ring2CheckBox.setSelected(true);
			}
			else if(selectedROIs[i] && FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3))
			{
				roi_bleached_ring3CheckBox.setSelected(true);
			}
			else if(selectedROIs[i] && FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4))
			{
				roi_bleached_ring4CheckBox.setSelected(true);
			}
			else if(selectedROIs[i] && FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5))
			{
				roi_bleached_ring5CheckBox.setSelected(true);
			}
			else if(selectedROIs[i] && FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6))
			{
				roi_bleached_ring6CheckBox.setSelected(true);
			}
			else if(selectedROIs[i] && FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7))
			{
				roi_bleached_ring7CheckBox.setSelected(true);
			}
			else if(selectedROIs[i] && FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8))
			{
				roi_bleached_ring8CheckBox.setSelected(true);
			}
		}
		setAllCheckboxesEnabled(false);
	}
	
	public void refreshROIImageForDisplay()
	{
		FRAPData frapData = frapWorkspace.getWorkingFrapStudy().getFrapData();
		ROI[] allROIs = getAllROIs();
		Color[] allColors = getAllROIColors();
		ArrayList<ROI> plottedROIs = new ArrayList<ROI>();
		ArrayList<Color> plottedColors = new ArrayList<Color>();
		
		if(roi_bleachedCheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED]);
		}
		if(roi_bleached_ring1CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING1]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING1]);
		}
		if(roi_bleached_ring2CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING2]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING2]);
		}
		if(roi_bleached_ring3CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING3]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING3]);
		}
		if(roi_bleached_ring4CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING4]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING4]);
		}
		if(roi_bleached_ring5CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING5]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING5]);
		}
		if(roi_bleached_ring6CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING6]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING6]);
		}
		if(roi_bleached_ring7CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING7]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING7]);
		}
		if(roi_bleached_ring8CheckBox.isSelected())
		{
			plottedROIs.add(allROIs[IDX_ROI_BLEACHED_RING8]);
			plottedColors.add(allColors[IDX_ROI_BLEACHED_RING8]);
		}
		
		//show ROI image
		ROI[] plottedROIArray = new ROI[plottedROIs.size()];
		plottedROIArray = plottedROIs.toArray(plottedROIArray);
		Color[] plottedColorArray = new Color[plottedColors.size()];
		plottedColorArray = plottedColors.toArray(plottedColorArray);
		roiImagePanel.refreshROIImage(plottedROIArray, plottedColorArray, frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()), Color.white);
	}
	
	public void refreshCheckboxes()
	{
		FRAPData frapData = frapWorkspace.getWorkingFrapStudy().getFrapData();
		ROI[] rois = frapData.getRois();
		//disable all the checkboxes first
		setAllCheckboxesEnabled(false);
		setAllCheckboxesSelected(false);
		//disable ROIs with 0 pixel in it
		//or disselect checkboxes according to stored selected ROIS
		boolean[] storedSelectedROIs = frapWorkspace.getWorkingFrapStudy().getSelectedROIsForErrorCalculation(); 
		for(int i=0; i<rois.length; i++)
		{
			if(rois[i].getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()) &&
			   frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()).getNonzeroPixelsCount()>0)
			{
				roi_bleachedCheckBox.setEnabled(true);
				if(storedSelectedROIs == null || storedSelectedROIs.length < 1 || storedSelectedROIs[i])
				{
					roi_bleachedCheckBox.setSelected(true);
				}
			}
			else if(rois[i].getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name()) &&
			     frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name()).getNonzeroPixelsCount()>0)
			{
				roi_bleached_ring1CheckBox.setEnabled(true);
				if(storedSelectedROIs == null || storedSelectedROIs.length < 1 || storedSelectedROIs[i])
				{
					roi_bleached_ring1CheckBox.setSelected(true);
				}
			}
			else if(rois[i].getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name()) &&
			     frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name()).getNonzeroPixelsCount()>0)
			{
				roi_bleached_ring2CheckBox.setEnabled(true);
				if(storedSelectedROIs == null || storedSelectedROIs.length < 1 || storedSelectedROIs[i])
				{
					roi_bleached_ring2CheckBox.setSelected(true);
				}
			}
			else if(rois[i].getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name()) &&
			     frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name()).getNonzeroPixelsCount()>0)
			{
				roi_bleached_ring3CheckBox.setEnabled(true);
				if(storedSelectedROIs == null || storedSelectedROIs.length < 1 || storedSelectedROIs[i])
				{
					roi_bleached_ring3CheckBox.setSelected(true);
				}
			}
			else if(rois[i].getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name()) &&
			     frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name()).getNonzeroPixelsCount()>0)
			{
				roi_bleached_ring4CheckBox.setEnabled(true);
				if(storedSelectedROIs == null || storedSelectedROIs.length < 1 || storedSelectedROIs[i])
				{
					roi_bleached_ring4CheckBox.setSelected(true);
				}
			}
			else if(rois[i].getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name()) &&
			     frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name()).getNonzeroPixelsCount()>0)
			{
				roi_bleached_ring5CheckBox.setEnabled(true);
				if(storedSelectedROIs == null || storedSelectedROIs.length < 1 || storedSelectedROIs[i])
				{
					roi_bleached_ring5CheckBox.setSelected(true);
				}
			}
			else if(rois[i].getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name()) &&
			     frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name()).getNonzeroPixelsCount()>0)
			{
				roi_bleached_ring6CheckBox.setEnabled(true);
				if(storedSelectedROIs == null || storedSelectedROIs.length < 1 || storedSelectedROIs[i])
				{
					roi_bleached_ring6CheckBox.setSelected(true);
				}
			}
			else if(rois[i].getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name()) &&
			     frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name()).getNonzeroPixelsCount()>0)
			{
				roi_bleached_ring7CheckBox.setEnabled(true);
				if(storedSelectedROIs == null || storedSelectedROIs.length < 1 || storedSelectedROIs[i])
				{
					roi_bleached_ring7CheckBox.setSelected(true);
				}
			}
			else if(rois[i].getROIName().equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name()) &&
			     frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name()).getNonzeroPixelsCount()>0)
			{
				roi_bleached_ring8CheckBox.setEnabled(true);
				if(storedSelectedROIs == null || storedSelectedROIs.length < 1 || storedSelectedROIs[i])
				{
					roi_bleached_ring8CheckBox.setSelected(true);
				}
			}
		}
	}
	
	private ImageIcon createColorIcon(Color iconColor){
		final int COLORSPOT_WIDTH = 16;
		final int COLORSPOT_HEIGHT = 16;
		final int XYSIZE = COLORSPOT_WIDTH*COLORSPOT_HEIGHT;
		BufferedImage colorSpot = new BufferedImage(COLORSPOT_WIDTH,COLORSPOT_HEIGHT,BufferedImage.TYPE_INT_RGB);
		for (int j = 0; j < XYSIZE; j++) {
			colorSpot.setRGB(j%COLORSPOT_WIDTH, j/COLORSPOT_WIDTH,iconColor.getRGB());			
		}
		return new ImageIcon(colorSpot);
	}
	
	public boolean[] getSelectedROIs()
	{
		return selectedROIs;
	}
	
	public void setFrapWorkspace(FRAPSingleWorkspace arg_FrapWorkspace)
    {
    	frapWorkspace = arg_FrapWorkspace;
    }

}
