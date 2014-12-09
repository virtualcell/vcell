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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.vcell.util.ISize;

import cbit.vcell.VirtualMicroscopy.ROI;

@SuppressWarnings("serial")
public class ROIImagePanel extends JPanel {
	
	private final JLabel roiImageJLabel;
	private ImageIcon roiImageIcon;
	
	public ROIImagePanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,7};
		gridBagLayout.rowHeights = new int[] {7};
		setLayout(gridBagLayout);

		roiImageJLabel = new JLabel();
		roiImageJLabel.setText("New JLabel");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.weighty = 0;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(roiImageJLabel, gridBagConstraints);
	}

	public void init(ROI[] analysisROIs,Color[] rinROIColors,ROI cellROI,Color cellROIColor,ROI bleachROI,Color bleachROIColor){
		
	}
	
	public void refreshROIImage(ROI[] analysisROIs,Color[] rinROIColors,ROI cellROI,Color cellROIColor/*,ROI bleachROI,Color bleachROIColor*/)
	{
		ISize roiISize = cellROI.getISize();
		final int ROISIZE_XYZ = roiISize.getXYZ();
		BufferedImage roiImage = new BufferedImage(roiISize.getX(),roiISize.getY(),BufferedImage.TYPE_INT_RGB);
		
		//fill ROIs
		for (int i = 0; i < analysisROIs.length; i++) {
			short[] roiPixels = analysisROIs[i].getPixelsXYZ();
			for (int j = 0; j < ROISIZE_XYZ; j++) {
				if(roiPixels[j] != 0){
					roiImage.setRGB(j%roiISize.getX(), j/roiISize.getX(), rinROIColors[i].getRGB());
				}
			}
		}
		//outline cell
		short[] cellROIPixels = cellROI.getPixelsXYZ();
		for (int y = 0; y < roiISize.getY(); y++) {
			int yindex = y*roiISize.getX();
			for (int x = 0; x < roiISize.getX(); x++) {
				if(cellROIPixels[yindex+x] != 0){
					boolean bEdge = false;
					if(x > 0){if(cellROIPixels[yindex+x-1] == 0){bEdge = true;}}
					if(x < (roiISize.getX()-1)){if(cellROIPixels[yindex+x+1] == 0){bEdge = true;}}
					if(y > 0){if(cellROIPixels[yindex+x-roiISize.getX()] == 0){bEdge = true;}}
					if(y < (roiISize.getY()-1)){if(cellROIPixels[yindex+x+roiISize.getX()] == 0){bEdge = true;}}
					if(bEdge){
						roiImage.setRGB(x,y, cellROIColor.getRGB());
					}
				}
			}
		}
		
		Image scaledImage = roiImage;
		int largestDimension = Math.max(roiISize.getX(),roiISize.getY());
//		if(largestDimension > 320){
			double scale = 280.0/largestDimension;
			scaledImage = roiImage.getScaledInstance((int)(roiISize.getX()*scale), (int)(roiISize.getY()*scale),Image.SCALE_REPLICATE);
//		}

		roiImageIcon = new ImageIcon(scaledImage);

		roiImageJLabel.setIcon(roiImageIcon);
		roiImageJLabel.setText(null);
	}
}
