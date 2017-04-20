/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.model.rbm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.model.bngl.ParseException;
import org.vcell.util.Matchable;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.bionetgen.BNGSpecies;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.graph.SpeciesPatternLargeShape;
import cbit.vcell.mapping.gui.NetworkConstraintsTableModel;
import cbit.vcell.model.Model;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

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
	
	private JButton runButton;
	private JButton applyButton;
	private JButton cancelButton;
	
	private final NetworkConstraintsPanel owner;
	private ChildWindow parentChildWindow;

	private class EventHandler implements FocusListener, ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == maxIterationTextField) {
				changeMaxIteration();
			} else if (e.getSource() == maxMolTextField) {
				changeMaxMolPerSpecies();
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
		maxIterationTextField.addActionListener(eventHandler);
		maxMolTextField.addActionListener(eventHandler);
		maxIterationTextField.addFocusListener(eventHandler);
		maxMolTextField.addFocusListener(eventHandler);
		
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
				String s2 = nc.getMaxIteration()  +"";
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

		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(6, 8, 0, 0);				//  top, left, bottom, right 
		add(new JLabel("Max. Iterations"), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(6, 0, 0, 10);
		add(maxIterationTextField, gbc);

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 8, 6, 0);
		add(new JLabel("Max. Molecules / Species"), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(0, 0, 6, 10);
		add(maxMolTextField, gbc);
		
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

public void setChildWindow(ChildWindow childWindow) {
	this.parentChildWindow = childWindow;
}


}
