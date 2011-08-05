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
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cbit.vcell.microscopy.gui.VFrap_OverlayEditorPanelJAI;

@SuppressWarnings("serial")
public class DefineROITopTitlePanel extends JPanel
{
	public URL arrowUrl = getClass().getResource("/images/arrow.gif");
	
	JLabel cropLabel = null;
	JLabel cellRoiLabel = null;
	JLabel bleachedRoiLabel = null;
	JLabel backgroundLabel = null;
	public DefineROITopTitlePanel() {
		super();
		setLayout(new GridBagLayout());
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7};
		gridBagLayout.columnWidths = new int[] {7,7,7,7,7,7,7,7,0,7,0,7,0,7};
		setLayout(gridBagLayout);

		cropLabel = new JLabel();
		cropLabel.setForeground(new Color(0, 0, 128));
		cropLabel.setFont(new Font("", Font.BOLD, 14));
		cropLabel.setText("Crop Image");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 1;
		add(cropLabel, gridBagConstraints);

		final JLabel arrowLabel1 = new JLabel(new ImageIcon(arrowUrl));
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.gridy = 0;
		gridBagConstraints_1.gridx = 3;
		add(arrowLabel1, gridBagConstraints_1);

		cellRoiLabel = new JLabel();
		cellRoiLabel.setForeground(new Color(0, 0, 128));
		cellRoiLabel.setFont(new Font("", Font.BOLD, 14));
		cellRoiLabel.setText("Cell ROI");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.gridy = 0;
		gridBagConstraints_2.gridx = 5;
		add(cellRoiLabel, gridBagConstraints_2);

		final JLabel arrowLabel2 = new JLabel(new ImageIcon(arrowUrl));
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.gridy = 0;
		gridBagConstraints_3.gridx = 7;
		add(arrowLabel2, gridBagConstraints_3);

		bleachedRoiLabel = new JLabel();
		bleachedRoiLabel.setForeground(new Color(0, 0, 128));
		bleachedRoiLabel.setFont(new Font("", Font.BOLD, 14));
		bleachedRoiLabel.setText("Bleached ROI");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.gridy = 0;
		gridBagConstraints_4.gridx = 9;
		add(bleachedRoiLabel, gridBagConstraints_4);

		final JLabel arrowLabel3 = new JLabel(new ImageIcon(arrowUrl));
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.gridy = 0;
		gridBagConstraints_5.gridx = 12;
		add(arrowLabel3, gridBagConstraints_5);

		backgroundLabel = new JLabel();
		backgroundLabel.setForeground(new Color(0, 0, 128));
		backgroundLabel.setFont(new Font("", Font.BOLD, 14));
		backgroundLabel.setText("Representative Background");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.gridy = 0;
		gridBagConstraints_6.gridx = 15;
		add(backgroundLabel, gridBagConstraints_6);
	}
	
	public void adjustComponent(int choice)
	{
		if(choice == VFrap_OverlayEditorPanelJAI.DEFINE_CROP)
		{
			cropLabel.setEnabled(true);
			cellRoiLabel.setEnabled(false);
			bleachedRoiLabel.setEnabled(false);
			backgroundLabel.setEnabled(false);
		}
		else if(choice == VFrap_OverlayEditorPanelJAI.DEFINE_CELLROI)
		{
			cropLabel.setEnabled(false);
			cellRoiLabel.setEnabled(true);
			bleachedRoiLabel.setEnabled(false);
			backgroundLabel.setEnabled(false);
		}
		else if(choice == VFrap_OverlayEditorPanelJAI.DEFINE_BLEACHEDROI)
		{
			cropLabel.setEnabled(false);
			cellRoiLabel.setEnabled(false);
			bleachedRoiLabel.setEnabled(true);
			backgroundLabel.setEnabled(false);
		}
		else if(choice == VFrap_OverlayEditorPanelJAI.DEFINE_BACKGROUNDROI)
		{
			cropLabel.setEnabled(false);
			cellRoiLabel.setEnabled(false);
			bleachedRoiLabel.setEnabled(false);
			backgroundLabel.setEnabled(true);
		}
	}

}
