/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.JLabel;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.mapping.RateRule;


@SuppressWarnings("serial")
public class RateRulesDisplayPanel extends BioModelEditorApplicationRightSidePanel<RateRule> {	
	public RateRulesDisplayPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		setName("RateRulesPanel");
		setLayout(new GridBagLayout());

		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		add(new JLabel("Search "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		add(textFieldSearch, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,50,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(addNewButton, gbc);
		// for now disable 'add' button
//		addNewButton.setEnabled(false);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(deleteButton, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 6;
		gbc.fill = GridBagConstraints.BOTH;
		add(table.getEnclosingScrollPane(), gbc);
	}
	
	@Override
	protected void newButtonPressed() {
		if (simulationContext == null) {
			return;
		}
		try {
			simulationContext.createRateRule(null);
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, "Error adding RateRule : " + e.getMessage());
		}		
	}

	@Override
	protected void deleteButtonPressed() {
		int[] rows = table.getSelectedRows();
		ArrayList<RateRule> deleteList = new ArrayList<RateRule>();
		for (int r : rows) {
			if (r < tableModel.getRowCount()) {
				RateRule rateRule = tableModel.getValueAt(r);
				if (rateRule != null) {
					deleteList.add(rateRule);
				}
			}
		}
		try {
			for (RateRule rateRule : deleteList) {
				simulationContext.removeRateRule(rateRule);
			}
		} catch (PropertyVetoException ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}		
	}

	@Override
	protected BioModelEditorApplicationRightSideTableModel<RateRule> createTableModel() {
		return new RateRulesSummaryTableModel(table);
	}
	
}
