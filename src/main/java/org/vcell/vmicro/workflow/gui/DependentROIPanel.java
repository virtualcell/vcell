/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.vmicro.workflow.gui;

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

import org.vcell.util.ColorUtil;

import cbit.vcell.VirtualMicroscopy.ROI;

@SuppressWarnings("serial")
public class DependentROIPanel extends JPanel implements ActionListener
{
	JPanel centerPanel = null;
	ROIImagePanel roiImagePanel = null;
	
	private JCheckBox[] roi_CheckBoxes = null;
	
	private JLabel[] roi_Labels = null;
	
	ROI[] allROIs = null;
	ROI cellROI = null;
	Color[] allROIColors = null;
	
	
	public DependentROIPanel(ROI[] allROIs, ROI cellROI) {
		super();
		this.allROIs = allROIs;
		this.cellROI = cellROI;
		this.roi_CheckBoxes = new JCheckBox[allROIs.length];
		this.roi_Labels = new JLabel[allROIs.length];
		
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

		for (int i=0; i<allROIs.length; i++){
			roi_CheckBoxes[i] = new JCheckBox("",true);
			final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
			gridBagConstraints_3.weightx = 0;
			gridBagConstraints_3.weighty = 1;
			gridBagConstraints_3.anchor = GridBagConstraints.WEST;
			gridBagConstraints_3.gridy = 3+i;
			gridBagConstraints_3.gridx = 0;
			centerPanel.add(roi_CheckBoxes[i], gridBagConstraints_3);
			roi_CheckBoxes[i].addActionListener(this);
	
			roi_Labels[i] = new JLabel();
			roi_Labels[i].setText(allROIs[i].getROIName());
			roi_Labels[i].setIcon(createColorIcon(allROIColors[i]));
			final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
			gridBagConstraints_1.anchor = GridBagConstraints.WEST;
			gridBagConstraints_1.gridy = 3+i;
			gridBagConstraints_1.gridx = 1;
			centerPanel.add(roi_Labels[i], gridBagConstraints_1);
		}
	}
		
	private Color[] getAllROIColors()
	{
		if(allROIColors == null)
		{
			allROIColors = new Color[allROIs.length*2];//double valid ROI colors (not include cell and background)
			Color[] availableColors = ColorUtil.generateAutoColor(allROIColors.length, getBackground(), new Integer(0));
			System.arraycopy(availableColors, 0, allROIColors, 0, allROIColors.length);
		}
		return allROIColors;
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() instanceof JCheckBox)
		{
			refresh();
		}
	}
		
	public void refresh() {
		Color[] allColors = getAllROIColors();
		ArrayList<ROI> plottedROIs = new ArrayList<ROI>();
		ArrayList<Color> plottedColors = new ArrayList<Color>();

		for (int i=0; i<allROIs.length; i++){
			if (roi_CheckBoxes[i].isSelected()) {
				plottedROIs.add(allROIs[i]);
				plottedColors.add(allColors[i]);
			}
		}
		
		//show ROI image
		ROI[] plottedROIArray = new ROI[plottedROIs.size()];
		plottedROIArray = plottedROIs.toArray(plottedROIArray);
		Color[] plottedColorArray = new Color[plottedColors.size()];
		plottedColorArray = plottedColors.toArray(plottedColorArray);
		roiImagePanel.refreshROIImage(plottedROIArray, plottedColorArray, cellROI, Color.white);
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
}
