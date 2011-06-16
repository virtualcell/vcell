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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JTextField;

import cbit.vcell.model.ReactionParticipant;

@SuppressWarnings("serial")
public class ReactionParticipantPropertiesPanel extends DocumentEditorSubPanel {
	private JTextField stoichiometryTextField = new JTextField();
	private ReactionParticipant reactionParticipant;
	private EventHandler eventHandler = new EventHandler();

	private class EventHandler implements ActionListener, FocusListener {
		public void focusLost(FocusEvent e) {
			commitChange();
		}
		
		public void focusGained(FocusEvent e) {
			
		}

		public void actionPerformed(ActionEvent e) {
			commitChange();			
		}
	}
	public ReactionParticipantPropertiesPanel() {
		super();
		initialize();
	}

	private void initialize() {
		stoichiometryTextField.setColumns(10);
		
		setBackground(Color.white);
		setLayout(new  GridBagLayout());
		// 0
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 0.3;
		gbc.insets = new Insets(20, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		add(new JLabel("Stoichiometry"), gbc);

		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(20, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(stoichiometryTextField, gbc);
		stoichiometryTextField.addActionListener(eventHandler);
		stoichiometryTextField.addFocusListener(eventHandler);
	}
	
	private void commitChange() {
		if (reactionParticipant != null) {
			try {
				int stoi = Integer.parseInt(stoichiometryTextField.getText());
				if (stoi != reactionParticipant.getStoichiometry()) {
					reactionParticipant.setStoichiometry(stoi);
				}
			} catch (NumberFormatException ex) {
				
			}
		}
	}
	
	private void setReactionParticipant(ReactionParticipant newValue) {
		if (reactionParticipant == newValue) {
			return;
		}
		commitChange();
		reactionParticipant = newValue;
		if (reactionParticipant == null) {
			stoichiometryTextField.setText(null);
		} else {
			stoichiometryTextField.setText(reactionParticipant.getStoichiometry() + "");
		}
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1) {
			setReactionParticipant(null);
		} else if (selectedObjects[0] instanceof ReactionParticipant) {
			setReactionParticipant((ReactionParticipant) selectedObjects[0]);
		} else {
			setReactionParticipant(null);
		}
		
	}
	
}
