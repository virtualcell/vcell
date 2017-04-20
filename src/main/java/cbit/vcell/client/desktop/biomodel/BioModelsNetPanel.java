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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jdom.DataConversionException;
import org.jdom.Element;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.gui.CollapsiblePanel;

import uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServices;
import uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServicesServiceLocator;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.xml.ExternalDocInfo;

@SuppressWarnings("serial")
public class BioModelsNetPanel extends DocumentEditorSubPanel {
	public static final String BIO_MODELS_NET = "BMDB";	

	private class BioModelsNetTreeCellRenderer extends DefaultTreeCellRenderer  {
		public BioModelsNetTreeCellRenderer() {
			super();
			setPreferredSize(new Dimension(250,30));
			setBorder(new EmptyBorder(0, 2, 0, 0));
		}
		public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			String text = null;
			if (value instanceof BioModelNode) {
		        BioModelNode node = (BioModelNode)value;
		        Object userObj = node.getUserObject();
		    	if (userObj instanceof BioModelsNetModelInfo) {
		    		text = ((BioModelsNetModelInfo) userObj).getName();
		    	} else if (userObj instanceof String) {
		    		text = (String)userObj;
		    	}
			}
			setText(text);
			return this;
		}
	}
	private class BioModelsNetTreeModel extends DefaultTreeModel {
		private BioModelNode rootNode = null;
		private String searchText = null;
		

		private BioModelsNetTreeModel() {
			super(new BioModelNode(BIO_MODELS_NET, true),true);			
			rootNode = (BioModelNode)root;
			populateRootNode();
		}

		public void setSearchText(String searchText) {
			this.searchText = searchText;
			populateRootNode();
		}
		private void populateRootNode() {
			try {
				List<BioModelsNetModelInfo> infoList = readVCellCompatibleBioModels_ID_Name_Hash();
				Collections.sort(infoList, new Comparator<BioModelsNetModelInfo>() {
	
					public int compare(BioModelsNetModelInfo o1, BioModelsNetModelInfo o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
				rootNode.removeAllChildren();
				for (BioModelsNetModelInfo bioModelsNetInfo : infoList) {
					boolean bAddNode = true;
					if (searchText != null) {
						String lowerCaseSearchText = searchText.toLowerCase();
						if (lowerCaseSearchText.length() > 0) {
							bAddNode = bioModelsNetInfo.getId().toLowerCase().contains(lowerCaseSearchText) 
								|| bioModelsNetInfo.getName().toLowerCase().contains(lowerCaseSearchText)
								|| bioModelsNetInfo.getLink().toLowerCase().contains(lowerCaseSearchText);
						}
					}
					if (bAddNode) {
						BioModelNode node = new BioModelNode(bioModelsNetInfo, false);
						rootNode.add(node);
					}
				}
				nodeStructureChanged(rootNode);
			} catch (Exception ex) {
				rootNode.setUserObject("Failed to load " + BIO_MODELS_NET + " databases.");
			}
		}

		public void select(BioModelsNetModelInfo bioModelsNetInfo) {
			BioModelNode node = rootNode.findNodeByUserObject(bioModelsNetInfo);
			if (node != null) {
				tree.setSelectionPath(new TreePath(node.getPath()));
			}			
		}
	}
	
	private static final String BIOMODELS_DATABASE_URL = "http://www.ebi.ac.uk/biomodels-main/";
	private DocumentWindowManager documentWindowManager = null;
	private JTextField searchTextField;
	private JButton searchButton = null;
	private JButton showAllButton = null;
	private JButton importButton = null;
	private EventHandler eventHandler = new EventHandler();
	private JTree tree = null;
	private BioModelsNetTreeModel treeModel = null;
	public static final String BIOMODELINFO_ELEMENT_NAME = "BioModelInfo";
	public static final String ID_ATTRIBUTE_NAME = "ID";
	public static final String MODELNAME_ATTRIBUTE_NAME = "Name";
	public static final String SUPPORTED_ATTRIBUTE_NAME = "Supported";	
	
	private class EventHandler implements ActionListener, TreeSelectionListener {
		
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == importButton) {
				importFromBioModelsNet();
			} else if (e.getSource() == searchButton || e.getSource() == searchTextField) {
				treeModel.setSearchText(searchTextField.getText());
			} else if (e.getSource() == showAllButton) {
				searchTextField.setText(null);
				treeModel.setSearchText(null);
			}
		}

		public void valueChanged(TreeSelectionEvent e) {
			if (e.getSource() == tree.getSelectionModel()) {
				treeSelectionChanged();
			}
		}
		
	}
	public BioModelsNetPanel() {
		super();
		initialize();
	}
	
	public void importFromBioModelsNet() {
		Object obj = tree.getLastSelectedPathComponent();
		if (obj == null || !(obj instanceof BioModelNode)) {
			return;
		}
		BioModelNode selectedNode = (BioModelNode)obj;
		Object userObject = selectedNode.getUserObject();
		if (!(userObject instanceof BioModelsNetModelInfo)) {
			return;
		}
		final BioModelsNetModelInfo bioModelsNetInfo = (BioModelsNetModelInfo) userObject;
		AsynchClientTask task1 = new AsynchClientTask("Importing " + bioModelsNetInfo.getName(), AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {		
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				BioModelsWebServicesServiceLocator bioModelsWebServicesServiceLocator =	new BioModelsWebServicesServiceLocator();
				BioModelsWebServices bioModelsWebServices = bioModelsWebServicesServiceLocator.getBioModelsWebServices();
				String bioModelSBML = bioModelsWebServices.getModelSBMLById(bioModelsNetInfo.getId());
				ExternalDocInfo externalDocInfo = ExternalDocInfo.createBioModelsNetExternalDocInfo(bioModelSBML, bioModelsNetInfo.getName());
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

	public void treeSelectionChanged() {
		importButton.setEnabled(false);
		
		Object obj = tree.getLastSelectedPathComponent();
		if (obj == null || !(obj instanceof BioModelNode)) {
			return;
		}
		BioModelNode selectedNode = (BioModelNode)obj;
		Object userObject = selectedNode.getUserObject();
		if (userObject instanceof BioModelsNetModelInfo) {
			importButton.setEnabled(true);
			setSelectedObjects(new Object[] {userObject});
		}
	}

	private void initialize() {
		searchTextField = new JTextField(10);
		searchTextField.putClientProperty("JTextField.variant", "search");
		searchTextField.addActionListener(eventHandler);
		searchButton = new JButton("Search");
		searchButton.addActionListener(eventHandler);
		showAllButton = new JButton("Show All");
		showAllButton.addActionListener(eventHandler);
		importButton = new JButton("Import");
		importButton.addActionListener(eventHandler);
		importButton.setEnabled(false);
		tree = new JTree();
		tree.setCellRenderer(new BioModelsNetTreeCellRenderer());
		treeModel = new BioModelsNetTreeModel();
		tree.setModel(treeModel);
		tree.getSelectionModel().addTreeSelectionListener(eventHandler);
		tree.addMouseListener(new MouseAdapter() {
		     public void mouseClicked(MouseEvent e) {
		         int selRow = tree.getRowForLocation(e.getX(), e.getY());
		         if(selRow > -1) {
		             if(e.getClickCount() == 1) {
//		                 for other feature implementation use single mouse click
		             }
		             else if(e.getClickCount() == 2) {
		            	 importFromBioModelsNet();
		             }
		         }
		     }
		 });
				
		CollapsiblePanel searchPanel = new CollapsiblePanel("Search", false);
		searchPanel.getContentPanel().setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		searchPanel.getContentPanel().add(searchTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(0,2,0,0);
		searchPanel.getContentPanel().add(searchButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(0,2,0,0);
		searchPanel.getContentPanel().add(showAllButton, gbc);	
		
		setPreferredSize(new Dimension(475, 300));
		setLayout(new GridBagLayout());
		int gridy = 0;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,0,4,0);
		add(searchPanel, gbc);	

		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(4,0,4,0);
		gbc.fill = GridBagConstraints.BOTH;
		add(new JScrollPane(tree), gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4,0,4,0);
		add(importButton, gbc);
	}

	public void setDocumentWindowManager(DocumentWindowManager newValue) {
		this.documentWindowManager = newValue;
	}
	
	private List<BioModelsNetModelInfo> readVCellCompatibleBioModels_ID_Name_Hash() throws IOException, DataConversionException {
		List<BioModelsNetModelInfo> vcellCompatibleBioModelsList = new ArrayList<BioModelsNetModelInfo>();

		final String BIOMODELSNET_INFO_FILENAME = "/bioModelsNetInfo.xml";
		InputStream tableInputStream = getClass().getResourceAsStream(BIOMODELSNET_INFO_FILENAME);
		if (tableInputStream==null){
			throw new FileNotFoundException(BIOMODELSNET_INFO_FILENAME + " not found");
		}
		//Process the Info files
		org.jdom.input.SAXBuilder saxparser = new org.jdom.input.SAXBuilder(false);
		org.jdom.Document doctable = null;
		try {
			doctable = saxparser.build(tableInputStream);
		} catch (org.jdom.JDOMException e) {
			e.printStackTrace();
			throw new java.io.IOException("An error occurred when trying to parse the rules file ");
		}
		@SuppressWarnings("unchecked")
		Iterator<Element> ruleiterator = doctable.getRootElement().getChildren().iterator();
		while (ruleiterator.hasNext()) {
			Element temp = (Element) ruleiterator.next();
			//System.out.println(temp.getAttributeValue("TagName") + ":" + temp.getAttributeValue("AttrName"));
			boolean bSupported = temp.getAttribute(BioModelsNetPanel.SUPPORTED_ATTRIBUTE_NAME).getBooleanValue();
			if(bSupported){
				String id = temp.getAttributeValue(BioModelsNetPanel.ID_ATTRIBUTE_NAME);
				String name = temp.getAttributeValue(BioModelsNetPanel.MODELNAME_ATTRIBUTE_NAME);
				vcellCompatibleBioModelsList.add(new BioModelsNetModelInfo(id, name, BIOMODELS_DATABASE_URL + "/" + id));
			}
		}

		return vcellCompatibleBioModelsList;
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1 || selectedObjects[0] == null || selectedObjects[0] instanceof VCDocumentInfo) {
			tree.clearSelection();
			return;
		}
		if (selectedObjects[0] instanceof BioModelsNetModelInfo) {
			BioModelsNetModelInfo bioModelsNetModelInfo = (BioModelsNetModelInfo) selectedObjects[0];
			treeModel.select(bioModelsNetModelInfo);
		}
	}
}
