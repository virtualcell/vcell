/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui.loaddatawizard;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.Insets;

@SuppressWarnings("serial")
public class LoadFRAPData_FileTypePanel extends JPanel {
	private JRadioButton singleFileRadioButton = null;
	private JRadioButton multiFileRadiobutton = null;
	private JRadioButton postProcessingDataRadioButton;
	
	public LoadFRAPData_FileTypePanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7,7,7,0,7,7,7,0,7,0,7, 0, 0, 0};
		setLayout(gridBagLayout);

		final JLabel loadFrapDataLabel = new JLabel();
		loadFrapDataLabel.setForeground(new Color(0, 0, 128));
		loadFrapDataLabel.setFont(new Font("", Font.BOLD | Font.ITALIC, 16));
		loadFrapDataLabel.setText("Load FRAP Data");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(loadFrapDataLabel, gridBagConstraints);

		final JLabel descLabel1 = new JLabel();
		descLabel1.setText("Description: Load FRAP images either from a single file or a series of files.");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_1.anchor = GridBagConstraints.WEST;
		gridBagConstraints_1.gridy = 4;
		gridBagConstraints_1.gridx = 0;
		add(descLabel1, gridBagConstraints_1);

		final JLabel descLabel2 = new JLabel();
		descLabel2.setText("Accepted formats include .lsm(Zeiss Image Foramt), .tif and .log (VCell Log");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_3.anchor = GridBagConstraints.WEST;
		gridBagConstraints_3.gridy = 5;
		gridBagConstraints_3.gridx = 0;
		add(descLabel2, gridBagConstraints_3);

		final JLabel descLabel3 = new JLabel();
		descLabel3.setText("File Format). ");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_5.anchor = GridBagConstraints.WEST;
		gridBagConstraints_5.gridy = 6;
		gridBagConstraints_5.gridx = 0;
		add(descLabel3, gridBagConstraints_5);

		singleFileRadioButton = new JRadioButton();
		singleFileRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
			}
		});
		singleFileRadioButton.setText("From a Single File");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_2.anchor = GridBagConstraints.WEST;
		gridBagConstraints_2.gridy = 12;
		gridBagConstraints_2.gridx = 0;
		add(singleFileRadioButton, gridBagConstraints_2);

		multiFileRadiobutton = new JRadioButton();
		multiFileRadiobutton.setText("From Multiple File Series");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_4.anchor = GridBagConstraints.WEST;
		gridBagConstraints_4.gridy = 13;
		gridBagConstraints_4.gridx = 0;
		add(multiFileRadiobutton, gridBagConstraints_4);
		
		postProcessingDataRadioButton = new JRadioButton("From Post Processing Data");
		
		GridBagConstraints gbc_rdbtnNewRadioButton = new GridBagConstraints();
		gbc_rdbtnNewRadioButton.anchor = GridBagConstraints.WEST;
		gbc_rdbtnNewRadioButton.gridx = 0;
		gbc_rdbtnNewRadioButton.gridy = 14;
		add(postProcessingDataRadioButton, gbc_rdbtnNewRadioButton);
		postProcessingDataRadioButton.setVisible(false);

		ButtonGroup bg = new ButtonGroup();
		singleFileRadioButton.setSelected(true);
		bg.add(singleFileRadioButton);
		bg.add(multiFileRadiobutton);
		bg.add(postProcessingDataRadioButton);
		
	}

	public JRadioButton getSingleFileButton()
	{
		return singleFileRadioButton;
	}

	public JRadioButton getMultipleFileButton()
	{
		return multiFileRadiobutton;
	}
	public JRadioButton getPostProcessDataButton(){
		return postProcessingDataRadioButton;
	}

}
