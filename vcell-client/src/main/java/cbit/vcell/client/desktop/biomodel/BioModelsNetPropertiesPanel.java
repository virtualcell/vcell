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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;

import javax.swing.JLabel;

import cbit.vcell.client.server.DynamicClientProperties;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.ExternalDocInfo;
import uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServices;
import uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServicesServiceLocator;

@SuppressWarnings("serial")
public class BioModelsNetPropertiesPanel extends DocumentEditorSubPanel {
	
	private JLabel urlLabel;
	private JLabel nameLabel;
	private JLabel idLabel;
	private JLabel linkLabel = null;
//	private JButton importButton = null;
	private BioModelsNetModelInfo bioModelsNetModelInfo = null;
	private DocumentWindowManager documentWindowManager = null;
	
	private EventHandler eventHandler = new EventHandler();
	
	private class EventHandler implements /*ActionListener, */MouseListener {
//		public void actionPerformed(ActionEvent e) {
//			if (e.getSource() == importButton) {
//				importFromBioModelsNet();
//			}
//		}

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				if (e.getSource() == linkLabel) {
					DialogUtils.browserLauncher(BioModelsNetPropertiesPanel.this, bioModelsNetModelInfo.getLink(), "Failed to open " + bioModelsNetModelInfo.getLink());
				} else if (e.getSource() == urlLabel) {
					DialogUtils.browserLauncher(BioModelsNetPropertiesPanel.this, DynamicClientProperties.getDynamicClientProperties().getProperty(PropertyLoader.BMDB_URL), "Failed to open " + DynamicClientProperties.getDynamicClientProperties().getProperty(PropertyLoader.BMDB_URL));
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}
	public BioModelsNetPropertiesPanel() {
		super();
		initialize();
	}
	
	public void importFromBioModelsNet() {		
		AsynchClientTask task1 = new AsynchClientTask("Importing " + bioModelsNetModelInfo.getName(), AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {		
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				BioModelsWebServicesServiceLocator bioModelsWebServicesServiceLocator =	new BioModelsWebServicesServiceLocator();
				BioModelsWebServices bioModelsWebServices = bioModelsWebServicesServiceLocator.getBioModelsWebServices();
				String bioModelSBML = bioModelsWebServices.getModelSBMLById(bioModelsNetModelInfo.getId());
				ExternalDocInfo externalDocInfo = new ExternalDocInfo(bioModelSBML, bioModelsNetModelInfo.getName());
				if (externalDocInfo != null) {
					hashTable.put("externalDocInfo", externalDocInfo);
				}
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("Opening",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {		
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				ExternalDocInfo externalDocInfo = (ExternalDocInfo) hashTable.get("externalDocInfo");
				if (externalDocInfo == null) {
					return;
				}
				documentWindowManager.getRequestManager().openDocument(externalDocInfo, documentWindowManager, true);
			}
		};
		ClientTaskDispatcher.dispatch(documentWindowManager.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, false);
	}


	private void initialize() {
		setBackground(Color.white);
		nameLabel = new JLabel();		
		urlLabel = new JLabel("<html><b><font color=blue><u>" + DynamicClientProperties.getDynamicClientProperties().getProperty(PropertyLoader.BMDB_URL) + "</u></font>" +
				", a data resource that allows researchers to" +
				" store, search and retrieve published mathematical models of biological interest.</b>" +
				"</html>");
		urlLabel.addMouseListener(eventHandler);
		idLabel = new JLabel();
//		importButton = new JButton("Import");
//		importButton.addActionListener(eventHandler);
//		importButton.setEnabled(true);
		linkLabel = new JLabel();
		linkLabel.addMouseListener(eventHandler);
		linkLabel.setForeground(Color.blue);
				
		int gridy = 0;
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(14,4,4,4);
		gbc.weightx = 0.2;
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		JLabel label = new JLabel(BioModelsNetPanel.BIO_MODELS_NET + ":");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		add(label, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(14,4,4,4);
		add(urlLabel, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,4);
		gbc.weightx = 0.2;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Model Name:"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4,4,4,4);
		add(nameLabel, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,4);
		gbc.weightx = 0.2;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Entry ID:"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		add(idLabel, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,4);
		gbc.weightx = 0.2;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Link:"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4,4,4,4);
		add(linkLabel, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.PAGE_START;
//		gbc.insets = new Insets(4,4,4,4);
		add(new JLabel(""), gbc); //put the black label here for the better alignment of the database file info.
	}

	public void setDocumentWindowManager(DocumentWindowManager newValue) {
		this.documentWindowManager = newValue;
	}

	public final void setBioModelsNetModelInfo(BioModelsNetModelInfo bioModelsNetModelInfo) {
		if (this.bioModelsNetModelInfo == bioModelsNetModelInfo) {
			return;
		}
		this.bioModelsNetModelInfo = bioModelsNetModelInfo;
		if (bioModelsNetModelInfo == null) {
//			importButton.setEnabled(false);
			nameLabel.setText(null);
			idLabel.setText(null);
		} else {
//			importButton.setEnabled(true);
			nameLabel.setText(bioModelsNetModelInfo.getName());
			idLabel.setText(bioModelsNetModelInfo.getId());
			linkLabel.setText("<html><u>" + bioModelsNetModelInfo.getLink() + "</u></html>");
		}
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1 || !(selectedObjects[0] instanceof BioModelsNetModelInfo)) {
			return;
		}
		BioModelsNetModelInfo bioModelsNetModelInfo = (BioModelsNetModelInfo) selectedObjects[0];
		setBioModelsNetModelInfo(bioModelsNetModelInfo);
	}
}
