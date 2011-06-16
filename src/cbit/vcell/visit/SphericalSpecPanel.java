/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.visit;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JFormattedTextField;
import java.awt.Insets;
import javax.swing.JCheckBox;

public class SphericalSpecPanel extends JPanel {

	
	
	private JFormattedTextField centerFormattedTextField;
	private JFormattedTextField radiusFormattedTextField;
	/**
	 * Create the panel.
	 */
	public SphericalSpecPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblCenter = new JLabel("Center");
		GridBagConstraints gbc_lblCenter = new GridBagConstraints();
		gbc_lblCenter.insets = new Insets(0, 0, 5, 5);
		gbc_lblCenter.anchor = GridBagConstraints.WEST;
		gbc_lblCenter.gridx = 0;
		gbc_lblCenter.gridy = 0;
		add(lblCenter, gbc_lblCenter);
		
		centerFormattedTextField = new JFormattedTextField();
		GridBagConstraints gbc_formattedTextField = new GridBagConstraints();
		gbc_formattedTextField.insets = new Insets(0, 0, 5, 0);
		gbc_formattedTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField.gridx = 1;
		gbc_formattedTextField.gridy = 0;
		add(centerFormattedTextField, gbc_formattedTextField);
		
		JLabel lblRadius = new JLabel("Radius");
		GridBagConstraints gbc_lblRadius = new GridBagConstraints();
		gbc_lblRadius.insets = new Insets(0, 0, 5, 5);
		gbc_lblRadius.anchor = GridBagConstraints.WEST;
		gbc_lblRadius.gridx = 0;
		gbc_lblRadius.gridy = 1;
		add(lblRadius, gbc_lblRadius);
		
		JFormattedTextField radiusFormattedTextField = new JFormattedTextField();
		GridBagConstraints gbc_formattedTextField_1 = new GridBagConstraints();
		gbc_formattedTextField_1.insets = new Insets(0, 0, 5, 0);
		gbc_formattedTextField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_formattedTextField_1.gridx = 1;
		gbc_formattedTextField_1.gridy = 1;
		add(radiusFormattedTextField, gbc_formattedTextField_1);
		
		JCheckBox chckbxInverse = new JCheckBox("Inverse");
		GridBagConstraints gbc_chckbxInverse = new GridBagConstraints();
		gbc_chckbxInverse.anchor = GridBagConstraints.WEST;
		gbc_chckbxInverse.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxInverse.gridx = 0;
		gbc_chckbxInverse.gridy = 2;
		add(chckbxInverse, gbc_chckbxInverse);

	}
	
	
	
	public String getCenterAsString(){
		return centerFormattedTextField.getText();
	}
	
	public String getRadiusAsString(){
		return radiusFormattedTextField.getText();
	}


}
