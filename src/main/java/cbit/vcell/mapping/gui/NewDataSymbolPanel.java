/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;

public class NewDataSymbolPanel extends JPanel {
	private JTextField nameTextField;
	private JTextField expressionTextField;

	public NewDataSymbolPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblName = new JLabel("Name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		add(lblName, gbc_lblName);
		
		nameTextField = new JTextField();
		GridBagConstraints gbc_nameTextField = new GridBagConstraints();
		gbc_nameTextField.insets = new Insets(0, 0, 5, 0);
		gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameTextField.gridx = 1;
		gbc_nameTextField.gridy = 0;
		add(nameTextField, gbc_nameTextField);
		nameTextField.setColumns(10);
		
		JLabel lblExpression = new JLabel("Expression");
		GridBagConstraints gbc_lblExpression = new GridBagConstraints();
		gbc_lblExpression.insets = new Insets(0, 0, 0, 5);
		gbc_lblExpression.anchor = GridBagConstraints.EAST;
		gbc_lblExpression.gridx = 0;
		gbc_lblExpression.gridy = 1;
		add(lblExpression, gbc_lblExpression);
		
		expressionTextField = new JTextField();
		GridBagConstraints gbc_expressionTextField = new GridBagConstraints();
		gbc_expressionTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_expressionTextField.gridx = 1;
		gbc_expressionTextField.gridy = 1;
		add(expressionTextField, gbc_expressionTextField);
		expressionTextField.setColumns(10);
	}

	public String getSymbolName() {
		return nameTextField.getText();
	}
	public void setSymbolName(String name) {
		nameTextField.setText(name);
	}
	public String getSymbolExpression() {
		return expressionTextField.getText();
	}
	public void setSymbolExpression(String name) {
		expressionTextField.setText(name);
	}

}
