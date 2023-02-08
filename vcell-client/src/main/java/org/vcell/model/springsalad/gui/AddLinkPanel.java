/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.model.springsalad.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.mapping.MolecularInternalLinkSpec;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.gui.NetworkConstraintsTableModel;
import cbit.vcell.mapping.gui.StoichiometryTableModel;

@SuppressWarnings("serial")
public class AddLinkPanel extends DocumentEditorSubPanel  {
	
	enum ActionButtons {
		Apply,
		Cancel
	}
	ActionButtons buttonPushed = ActionButtons.Cancel;
	
	private EventHandler eventHandler = new EventHandler();
	
	private JButton applyButton;
	private JButton cancelButton;
	
	private final MolecularStructuresPanel owner;
	private ChildWindow parentChildWindow;
	
	private JList<MolecularComponentPattern> firstSiteList = null;
	private DefaultListModel<MolecularComponentPattern> firstSiteListModel = new DefaultListModel<>();
	private ListCellRenderer<Object> firstSiteCellRenderer = new DefaultListCellRenderer(){
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof MolecularComponentPattern && component instanceof JLabel) {
				MolecularComponentPattern mcp = (MolecularComponentPattern)value;
				((JLabel)component).setText(mcp.getMolecularComponent().getName());
			}
			return component;
		}
	};
	private JList<MolecularComponentPattern> secondSiteList = null;
	private DefaultListModel<MolecularComponentPattern> secondSiteListModel = new DefaultListModel<>();
	private ListCellRenderer<Object> secondSiteCellRenderer = new DefaultListCellRenderer() {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof MolecularComponentPattern && component instanceof JLabel){
				MolecularComponentPattern mcp = (MolecularComponentPattern)value;
				((JLabel)component).setText(mcp.getMolecularComponent().getName());
			}
			return component;
		}
	};


	private class EventHandler implements FocusListener, ActionListener, ListSelectionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == null) {
				;
			}
		}
		@Override
		public void focusGained(FocusEvent e) {
		}
		@Override
		public void focusLost(FocusEvent e) {
			if (e.getSource() == null) {
				;
			}
		}
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(e.getSource() == firstSiteList || e.getSource() == secondSiteList) {
				MolecularComponentPattern firstMcp = firstSiteList.getSelectedValue();
				MolecularComponentPattern secondMcp = secondSiteList.getSelectedValue();
				if(firstMcp != null && secondMcp != null) {
					getApplyButton().setEnabled(true);
				} else {
					getApplyButton().setEnabled(false);
				}
			}
		}
	}
	
public AddLinkPanel(MolecularStructuresPanel owner) {
	super();
	this.owner = owner;
	initialize();
}


private void handleException(java.lang.Throwable exception) {
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}

private void initialize() {
	try {
		SpeciesContextSpec scs = owner.getSpeciesContextSpec();
		SpeciesPattern sp = scs.getSpeciesContext().getSpeciesPattern();
		if(sp == null) {
			DialogUtils.showErrorDialog(parentChildWindow.getParent(), "SpeciesPattern is null");
			return;
		}
		MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(0);
		if(mtp == null) {
			DialogUtils.showErrorDialog(parentChildWindow.getParent(), "No molecule defined");
			return;
		}
		if(mtp.getComponentPatternList().size() < 2) {
			DialogUtils.showErrorDialog(parentChildWindow.getParent(), "At least 2 Sites are needed");
			return;
		}

		firstSiteList = new JList<MolecularComponentPattern>(firstSiteListModel);
		firstSiteList.setCellRenderer(firstSiteCellRenderer);
		secondSiteList = new JList<MolecularComponentPattern>(secondSiteListModel);
		secondSiteList.setCellRenderer(secondSiteCellRenderer);
		firstSiteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		secondSiteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		for(MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
			firstSiteListModel.addElement(mcp);
			secondSiteListModel.addElement(mcp);
		}
		
			
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(6, 8, 1, 1);				//  top, left, bottom, right 
		p.add(new JLabel("First Site"), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(6, 0, 0, 10);
		p.add(new JLabel("Second Site"), gbc);

		JScrollPane scrollPane1 = new JScrollPane(firstSiteList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		p.add(scrollPane1, gbc);

		JScrollPane scrollPane2 = new JScrollPane(secondSiteList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		p.add(scrollPane2, gbc);

		// --------------------------------------------------------------------
		setName("AddLinkPanel");
		setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(p, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 2, 8, 2);
		add(getApplyButton(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 2, 8, 10);
		add(getCancelButton(), gbc);

		// ------------------------------------------------
		
		firstSiteList.addListSelectionListener(eventHandler);
		secondSiteList.addListSelectionListener(eventHandler);

		getApplyButton().setEnabled(false);
		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


public ActionButtons getButtonPushed() {
	return buttonPushed;
}
private JButton getApplyButton() {
	if (applyButton == null) {
		applyButton = new javax.swing.JButton("Apply");
		applyButton.setName("ApplyButton");
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonPushed = ActionButtons.Apply;
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

public JList<MolecularComponentPattern> getFirstSiteList() {
	return firstSiteList;
}
public JList<MolecularComponentPattern> getSecondSiteList() {
	return secondSiteList;
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {

}

public void setChildWindow(ChildWindow childWindow) {
	this.parentChildWindow = childWindow;
}

}
