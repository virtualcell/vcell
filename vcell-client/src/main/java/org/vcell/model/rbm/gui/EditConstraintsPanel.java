/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.model.rbm.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.mapping.gui.NetworkConstraintsTableModel;
import cbit.vcell.mapping.gui.StoichiometryTableModel;

@SuppressWarnings("serial")
public class EditConstraintsPanel extends DocumentEditorSubPanel  {
	
	enum ActionButtons {
		Run,
		Apply,
		Cancel
	}
	ActionButtons buttonPushed = ActionButtons.Cancel;
	
	private EventHandler eventHandler = new EventHandler();
	
	JTextField maxIterationTextField;
	JTextField maxMolTextField;
	JTextField speciesLimitTextField;
	JTextField reactionsLimitTextField;
	
	private EditorScrollTable stoichiometryTable = null;
	private StoichiometryTableModel stoichiometryTableModel = null;

	
	private JButton runButton;
	private JButton applyButton;
	private JButton cancelButton;
	
	private final NetworkConstraintsPanel owner;
	private ChildWindow parentChildWindow;

	private class EventHandler implements FocusListener, ActionListener, TableModelListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == maxIterationTextField) {
				changeMaxIteration();
			} else if (e.getSource() == maxMolTextField) {
				changeMaxMolPerSpecies();
			} else if (e.getSource() == speciesLimitTextField) {
				changeSpeciesLimit();
			} else if (e.getSource() == reactionsLimitTextField) {
				changeReactionsLimit();
			}
		}

		@Override
		public void focusGained(FocusEvent e) {
		}

		@Override
		public void focusLost(FocusEvent e) {
			if (e.getSource() == maxIterationTextField) {
				changeMaxIteration();
			} else if (e.getSource() == maxMolTextField) {
				changeMaxMolPerSpecies();
			} else if (e.getSource() == speciesLimitTextField) {
				changeSpeciesLimit();
			} else if (e.getSource() == reactionsLimitTextField) {
				changeReactionsLimit();
			}
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			System.out.println("table changed");
			if(stoichiometryTableModel.isChanged()) {
				getApplyButton().setEnabled(true);
			} else {
				// may happen if you change a value and then change your mind and put back the old value
				getApplyButton().setEnabled(false);					
			}
		}
	}
	
public EditConstraintsPanel(NetworkConstraintsPanel owner) {
	super();
	this.owner = owner;
	initialize();
}


private void handleException(java.lang.Throwable exception) {
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}

private void initialize() {
	try {
		setName("EditConstraintsPanel");
		setLayout(new GridBagLayout());
			
		maxIterationTextField = new JTextField();
		maxMolTextField = new JTextField();
		speciesLimitTextField = new JTextField();
		reactionsLimitTextField = new JTextField();
		
		stoichiometryTable = new EditorScrollTable();
		stoichiometryTableModel = new StoichiometryTableModel(stoichiometryTable);
		stoichiometryTable.setModel(stoichiometryTableModel);
		stoichiometryTableModel.setSimulationContext(owner.getSimulationContext());

		maxIterationTextField.addActionListener(eventHandler);
		maxMolTextField.addActionListener(eventHandler);
		speciesLimitTextField.addActionListener(eventHandler);
		reactionsLimitTextField.addActionListener(eventHandler);
		maxIterationTextField.addFocusListener(eventHandler);
		maxMolTextField.addFocusListener(eventHandler);
		speciesLimitTextField.addFocusListener(eventHandler);
		reactionsLimitTextField.addFocusListener(eventHandler);

		stoichiometryTableModel.addTableModelListener(eventHandler);
		
		maxIterationTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				if(isChanged()) {
					getApplyButton().setEnabled(true);
				} else {
					getApplyButton().setEnabled(false);					
				}
			}
			public void removeUpdate(DocumentEvent e) {
				if(isChanged()) {
					getApplyButton().setEnabled(true);
				} else {
					getApplyButton().setEnabled(false);					
				}
			}
			public void insertUpdate(DocumentEvent e) {
				if(isChanged()) {
					getApplyButton().setEnabled(true);
				} else {
					getApplyButton().setEnabled(false);
				}
			}
			public boolean isChanged() {
				NetworkConstraints nc = owner.getSimulationContext().getNetworkConstraints();
				String s1 = maxIterationTextField.getText();
				String s2 = nc.getMaxIteration() +"";
				if(!s1.equals(s2)) {
					return true;
				}
				return false;
			}
		});
		maxMolTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				if(isChanged()) {
					getApplyButton().setEnabled(true);
				} else {
					getApplyButton().setEnabled(false);					
				}
			}
			public void removeUpdate(DocumentEvent e) {
				if(isChanged()) {
					getApplyButton().setEnabled(true);
				} else {
					getApplyButton().setEnabled(false);					
				}
			}
			public void insertUpdate(DocumentEvent e) {
				if(isChanged()) {
					getApplyButton().setEnabled(true);
				} else {
					getApplyButton().setEnabled(false);
				}
			}
			public boolean isChanged() {
				NetworkConstraints nc = owner.getSimulationContext().getNetworkConstraints();
				String s1 = maxMolTextField.getText();
				String s2 = nc.getMaxMoleculesPerSpecies() + "";
				if(!s1.equals(s2)) {
					return true;
				}
				return false;
			}
		});
		speciesLimitTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				if(isChanged()) {
					getApplyButton().setEnabled(true);
				} else {
					getApplyButton().setEnabled(false);					
				}
			}
			public void removeUpdate(DocumentEvent e) {
				if(isChanged()) {
					getApplyButton().setEnabled(true);
				} else {
					getApplyButton().setEnabled(false);					
				}
			}
			public void insertUpdate(DocumentEvent e) {
				if(isChanged()) {
					getApplyButton().setEnabled(true);
				} else {
					getApplyButton().setEnabled(false);
				}
			}
			public boolean isChanged() {
				NetworkConstraints nc = owner.getSimulationContext().getNetworkConstraints();
				String s1 = speciesLimitTextField.getText();
				String s2 = nc.getSpeciesLimit() +"";
				if(!s1.equals(s2)) {
					return true;
				}
				return false;
			}
		});
		reactionsLimitTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				if(isChanged()) {
					getApplyButton().setEnabled(true);
				} else {
					getApplyButton().setEnabled(false);					
				}
			}
			public void removeUpdate(DocumentEvent e) {
				if(isChanged()) {
					getApplyButton().setEnabled(true);
				} else {
					getApplyButton().setEnabled(false);					
				}
			}
			public void insertUpdate(DocumentEvent e) {
				if(isChanged()) {
					getApplyButton().setEnabled(true);
				} else {
					getApplyButton().setEnabled(false);
				}
			}
			public boolean isChanged() {
				NetworkConstraints nc = owner.getSimulationContext().getNetworkConstraints();
				String s1 = reactionsLimitTextField.getText();
				String s2 = nc.getReactionsLimit() +"";
				if(!s1.equals(s2)) {
					return true;
				}
				return false;
			}
		});
		
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(6, 8, 0, 0);				//  top, left, bottom, right 
		p.add(new JLabel("Max. Iterations"), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(6, 0, 0, 10);
		p.add(maxIterationTextField, gbc);

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(6, 8, 0, 0);
		p.add(new JLabel("Max. Molecules / Species"), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(6, 0, 0, 10);
		p.add(maxMolTextField, gbc);
		
		// ---------------------------------------------------------
		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(6, 8, 0, 0);
		p.add(new JLabel("Species Limit"), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(6, 0, 0, 10);
		p.add(speciesLimitTextField, gbc);

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(6, 8, 6, 0);
		p.add(new JLabel("Reactions Limit"), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(6, 0, 6, 10);
		p.add(reactionsLimitTextField, gbc);
		
		// ---------------------------------------------------------
		
		setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(p, gbc);
		
		JScrollPane sp = new JScrollPane(stoichiometryTable);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = 3;
		gbc.insets = new Insets(5, 8, 4, 10);
		add(sp, gbc);

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 8, 8, 2);
		add(getRunButton(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 2, 8, 2);
		add(getApplyButton(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 2, 8, 10);
		add(getCancelButton(), gbc);
		
		maxIterationTextField.setText(owner.getSimulationContext().getNetworkConstraints().getMaxIteration() + "");
		maxMolTextField.setText(owner.getSimulationContext().getNetworkConstraints().getMaxMoleculesPerSpecies() + "");
		speciesLimitTextField.setText(owner.getSimulationContext().getNetworkConstraints().getSpeciesLimit() + "");
		reactionsLimitTextField.setText(owner.getSimulationContext().getNetworkConstraints().getReactionsLimit() + "");
		
		getApplyButton().setEnabled(false);	
		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

public ActionButtons getButtonPushed() {
	return buttonPushed;
}

private JButton getRunButton() {
	if (runButton == null) {
		runButton = new javax.swing.JButton("Test / Run");
		runButton.setName("RunButton");
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonPushed = ActionButtons.Run;
				try {
					int test = new Integer( maxIterationTextField.getText());
					test = new Integer( maxMolTextField.getText());
					test = new Integer( speciesLimitTextField.getText());
					test = new Integer( reactionsLimitTextField.getText());
				} catch (NumberFormatException ex) {
					DialogUtils.showErrorDialog(parentChildWindow.getParent(), "Wrong number format: " + ex.getMessage());
					return;
				}
				parentChildWindow.close();
			}
		});
	}
	return runButton;
}
private JButton getApplyButton() {
	if (applyButton == null) {
		applyButton = new javax.swing.JButton("Apply");
		applyButton.setName("ApplyButton");
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonPushed = ActionButtons.Apply;
				try {
					int test = new Integer( maxIterationTextField.getText());
					test = new Integer( maxMolTextField.getText());
					test = new Integer( speciesLimitTextField.getText());
					test = new Integer( reactionsLimitTextField.getText());
				} catch (NumberFormatException ex) {
					DialogUtils.showErrorDialog(parentChildWindow.getParent(), "Wrong number format: " + ex.getMessage());
					return;
				}
				parentChildWindow.close();
			}
		});
	}
	return applyButton;
}
private JButton getCancelButton() {
	if (cancelButton == null) {
		cancelButton = new javax.swing.JButton("Cancel");
		cancelButton.setName("CancelButton");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonPushed = ActionButtons.Cancel;
				parentChildWindow.close();
			}
		});
	}
	return cancelButton;
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {

}

public void changeMaxMolPerSpecies() {
	String text = maxMolTextField.getText();
	if (text == null || text.trim().length() == 0) {
		return;
	}
}
public void changeMaxIteration() {
	String text = maxIterationTextField.getText();
	if (text == null || text.trim().length() == 0) {
		return;
	}
}
public void changeSpeciesLimit() {
	String text = speciesLimitTextField.getText();
	if (text == null || text.trim().length() == 0) {
		return;
	}
}
public void changeReactionsLimit() {
	String text = reactionsLimitTextField.getText();
	if (text == null || text.trim().length() == 0) {
		return;
	}
}

public void setChildWindow(ChildWindow childWindow) {
	this.parentChildWindow = childWindow;
}

}
