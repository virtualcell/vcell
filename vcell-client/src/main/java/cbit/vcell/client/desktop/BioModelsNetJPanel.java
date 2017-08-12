/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.jdom.Element;
import org.vcell.util.BeanUtils;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.desktop.biomodel.BioModelsNetPanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.xml.ExternalDocInfo;
import uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServices;
import uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServicesServiceLocator;

@SuppressWarnings("serial")
public class BioModelsNetJPanel extends JPanel {
	private DocumentWindow documentWindow;
	private static final String BIOMODELS_DATABASE_URL = "http://www.ebi.ac.uk/biomodels/";
	private JTextField modelNameTextField;
	private JTextField publicationTextField;
	private JTextField modelEntryIDTextField;
	
	public BioModelsNetJPanel() {
		super();
		setPreferredSize(new Dimension(475, 300));
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowHeights = new int[] {7,0,7,0,0,7,7};
		setLayout(gridBagLayout);

		final JLabel biomodelsnetLabel = new JLabel();
		biomodelsnetLabel.setText("BioModels DataBase");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_4.gridx = 0;
		gridBagConstraints_4.gridy = 0;
		add(biomodelsnetLabel, gridBagConstraints_4);

		final JLabel helpingToDefineLabel_1 = new JLabel();
		helpingToDefineLabel_1.setText("A data resource that allows researchers to ");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_2.gridx = 0;
		gridBagConstraints_2.gridy = 1;
		add(helpingToDefineLabel_1, gridBagConstraints_2);

		final JLabel helpingToDefineLabel = new JLabel();
		helpingToDefineLabel.setText("store, search and retrieve published mathematical models");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 2;
		add(helpingToDefineLabel, gridBagConstraints_1);

		final JLabel modelsOfBiologicalLabel = new JLabel();
		modelsOfBiologicalLabel.setText("of biological interest.");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints_3.gridy = 3;
		gridBagConstraints_3.gridx = 0;
		add(modelsOfBiologicalLabel, gridBagConstraints_3);

		final JButton httpbiomodelsnetLabel = new JButton();
		httpbiomodelsnetLabel.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				DialogUtils.browserLauncher(BioModelsNetJPanel.this, BIOMODELS_DATABASE_URL,BIOMODELS_DATABASE_URL);
			}
		});
		httpbiomodelsnetLabel.setText(BIOMODELS_DATABASE_URL);
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(4, 4, 5, 4);
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridx = 0;
		add(httpbiomodelsnetLabel, gridBagConstraints);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 2), "Search/Import VCell Compatible Models (empty field matches any)", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(4, 4, 4, 4);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 5;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblModelEntryId = new JLabel("Model Entry ID");
		GridBagConstraints gbc_lblModelEntryId = new GridBagConstraints();
		gbc_lblModelEntryId.anchor = GridBagConstraints.EAST;
		gbc_lblModelEntryId.insets = new Insets(0, 0, 5, 5);
		gbc_lblModelEntryId.gridx = 0;
		gbc_lblModelEntryId.gridy = 0;
		panel.add(lblModelEntryId, gbc_lblModelEntryId);
		
		modelEntryIDTextField = new JTextField();
		GridBagConstraints gbc_modelEntryIDTextField = new GridBagConstraints();
		gbc_modelEntryIDTextField.insets = new Insets(0, 0, 5, 0);
		gbc_modelEntryIDTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_modelEntryIDTextField.gridx = 1;
		gbc_modelEntryIDTextField.gridy = 0;
		panel.add(modelEntryIDTextField, gbc_modelEntryIDTextField);
		modelEntryIDTextField.setColumns(10);
		
		JLabel lblName = new JLabel("Model Name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 1;
		panel.add(lblName, gbc_lblName);
		
		modelNameTextField = new JTextField();
		GridBagConstraints gbc_modelNameTextField = new GridBagConstraints();
		gbc_modelNameTextField.insets = new Insets(0, 0, 5, 0);
		gbc_modelNameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_modelNameTextField.gridx = 1;
		gbc_modelNameTextField.gridy = 1;
		panel.add(modelNameTextField, gbc_modelNameTextField);
		modelNameTextField.setColumns(10);
		
		JLabel lblPublication = new JLabel("Publication Info");
		GridBagConstraints gbc_lblPublication = new GridBagConstraints();
		gbc_lblPublication.anchor = GridBagConstraints.EAST;
		gbc_lblPublication.insets = new Insets(0, 0, 5, 5);
		gbc_lblPublication.gridx = 0;
		gbc_lblPublication.gridy = 2;
		panel.add(lblPublication, gbc_lblPublication);
		
		publicationTextField = new JTextField();
		GridBagConstraints gbc_publicationTextField = new GridBagConstraints();
		gbc_publicationTextField.insets = new Insets(0, 0, 5, 0);
		gbc_publicationTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_publicationTextField.gridx = 1;
		gbc_publicationTextField.gridy = 2;
		panel.add(publicationTextField, gbc_publicationTextField);
		publicationTextField.setColumns(10);
		
		JButton btnSearch = new JButton("Search VCell Compatible...");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchImport();
		}});
		GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.gridwidth = 2;
		gbc_btnSearch.gridx = 0;
		gbc_btnSearch.gridy = 3;
		panel.add(btnSearch, gbc_btnSearch);
	}

	public void setDocumentWindow(DocumentWindow documentWindow) {
		this.documentWindow = documentWindow;
	}

	private void disposeParentDialog(){
		Container parent =
			BeanUtils.findTypeParentOfComponent(BioModelsNetJPanel.this, Dialog.class);
		if(parent instanceof JDialog){
			((JDialog)parent).dispose();
		}

	}
	
	private Hashtable<String, String> readVCellCompatibleBioModels_ID_Name_Hash() throws Exception{
		Hashtable<String, String> vcellCompatibleBioModelsHash = new Hashtable<String, String>();

		final String BIOMODELSNET_INFO_FILENAME = "/bioModelsNetInfo.xml";
		InputStream tableInputStream = getClass().getResourceAsStream(BIOMODELSNET_INFO_FILENAME);
		if (tableInputStream==null){
			throw new FileNotFoundException(BIOMODELSNET_INFO_FILENAME+" not found");
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
		Iterator<Element> ruleiterator = doctable.getRootElement().getChildren().iterator();
		while (ruleiterator.hasNext()) {
			Element temp = (Element) ruleiterator.next();
			//System.out.println(temp.getAttributeValue("TagName") + ":" + temp.getAttributeValue("AttrName"));
			boolean bSupported = temp.getAttribute(BioModelsNetPanel.SUPPORTED_ATTRIBUTE_NAME).getBooleanValue();
			if(bSupported){
				String id = temp.getAttributeValue(BioModelsNetPanel.ID_ATTRIBUTE_NAME);
				String name = temp.getAttributeValue(BioModelsNetPanel.MODELNAME_ATTRIBUTE_NAME);
				vcellCompatibleBioModelsHash.put(id, name);
			}
		}

		return vcellCompatibleBioModelsHash;
	}
	
	private void searchImport(){
		
		AsynchClientTask searchTask = new AsynchClientTask("Search BioModels",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {		
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				
				Hashtable<String, String> vcellCompatibleBioModels_ID_Name_Hash = readVCellCompatibleBioModels_ID_Name_Hash();
				
				ClientTaskStatusSupport clientTaskStatusSupport = getClientTaskStatusSupport();
//				clientTaskStatusSupport.setProgress(0);
				clientTaskStatusSupport.setMessage("Initializing BioModels Search...");
				BioModelsWebServicesServiceLocator bioModelsWebServicesServiceLocator =
					new BioModelsWebServicesServiceLocator();
				BioModelsWebServices bioModelsWebServices =
					bioModelsWebServicesServiceLocator.getBioModelsWebServices();
				
//				clientTaskStatusSupport.setProgress(10);
				clientTaskStatusSupport.setMessage("Executing BioModels Search...");
				TreeSet<String> modelIDsMergedSearch = new TreeSet<String>();
				boolean bHasModelEntryID = modelEntryIDTextField.getText() != null && modelEntryIDTextField.getText().length() > 0;
				if(bHasModelEntryID){
					clientTaskStatusSupport.setMessage("Executing BioModels Search (Model ID matches)...");
//					String[] modelIDsAll = bioModelsWebServices.getAllModelsId();
					String[] modelIDsAll = vcellCompatibleBioModels_ID_Name_Hash.keySet().toArray(new String[0]);
					for (int i = 0; i < modelIDsAll.length; i++) {
						if(modelIDsAll[i].toUpperCase().indexOf(modelEntryIDTextField.getText().toUpperCase()) != -1){
							modelIDsMergedSearch.add(modelIDsAll[i]);
						}
					}
				}
				boolean bHasModelName = modelNameTextField.getText() != null && modelNameTextField.getText().length() > 0;
				if(bHasModelName){
					clientTaskStatusSupport.setMessage("Executing BioModels Search (Model Name matches)...");
					TreeSet<String> modelIDsByNameSet = new TreeSet<String>();
//					String[] modelIDsByName = bioModelsWebServices.getModelsIdByName(modelNameTextField.getText());
					String[] modelIDsAll = vcellCompatibleBioModels_ID_Name_Hash.keySet().toArray(new String[0]);
					for (int i = 0; i < modelIDsAll.length; i++) {
						if(vcellCompatibleBioModels_ID_Name_Hash.get(modelIDsAll[i]).toUpperCase().indexOf(modelNameTextField.getText().toUpperCase()) != -1){
							modelIDsByNameSet.add(modelIDsAll[i]);
						}
					}
//					for (int i = 0; i < modelIDsByName.length; i++) {
//						modelIDsByNameSet.add(modelIDsByName[i]);
//					}
					if(bHasModelEntryID){
						modelIDsMergedSearch.retainAll(modelIDsByNameSet);//intersection
					}else{
						modelIDsMergedSearch = modelIDsByNameSet;
					}
				}
				boolean bHasModelPublication = publicationTextField.getText() != null && publicationTextField.getText().length() > 0;
				if(bHasModelPublication){
					clientTaskStatusSupport.setMessage("Executing BioModels Search (Publication matches)...");
					TreeSet<String> modelIDsByPublicationSet = new TreeSet<String>();
					String[] modelIDsbyPublication = bioModelsWebServices.getModelsIdByPublication(publicationTextField.getText());
					for (int i = 0; i < modelIDsbyPublication.length; i++) {
						modelIDsByPublicationSet.add(modelIDsbyPublication[i]);
					}
					if(bHasModelEntryID || bHasModelName){
						modelIDsMergedSearch.retainAll(modelIDsByPublicationSet);//intersection
					}else{
						modelIDsMergedSearch = modelIDsByPublicationSet;
					}

				}
				if(!(bHasModelEntryID || bHasModelName || bHasModelPublication)){//Get all models
//					modelIDsMergedSearch.addAll(Arrays.asList(bioModelsWebServices.getAllModelsId()));
					modelIDsMergedSearch.addAll(Arrays.asList(vcellCompatibleBioModels_ID_Name_Hash.keySet().toArray(new String[0])));
				}
//				final int COLLECTION_PROGRESS = 20;
//				clientTaskStatusSupport.setProgress(COLLECTION_PROGRESS);
				if(modelIDsMergedSearch.size() > 0){
					//Intersect with supported BioModel IDs
					modelIDsMergedSearch.retainAll(Arrays.asList(vcellCompatibleBioModels_ID_Name_Hash.keySet().toArray(new String[0])));
					clientTaskStatusSupport.setMessage("Found "+modelIDsMergedSearch.size()+" VCell compatible BioModels");

					String[] modelIDMergedSearchArr = modelIDsMergedSearch.toArray(new String[0]);
					TreeMap<String, String> model_ID_Name_Map = new TreeMap<String, String>();
					String nameNotfound = "NameNotFound_0";
					for (int i = 0; i < modelIDMergedSearchArr.length; i++) {
//						clientTaskStatusSupport.setProgress(COLLECTION_PROGRESS+((100-COLLECTION_PROGRESS)*(i+1)/modelIDMergedSearchArr.length));
						
						if(clientTaskStatusSupport.isInterrupted()){
//							DialogUtils.showWarningDialog(BioModelsNetJPanel.this, "Search Cancelled");
							throw UserCancelException.CANCEL_GENERIC;
						}
						try{
//							clientTaskStatusSupport.setMessage("Retrieving "+modelIDMergedSearchArr[i]+" "+(i+1)+" of "+modelIDMergedSearchArr.length);
//							String modelName = bioModelsWebServices.getModelNameById(modelIDMergedSearchArr[i]);
							String modelName = vcellCompatibleBioModels_ID_Name_Hash.get(modelIDMergedSearchArr[i]);
							//Assume model names are unique
							model_ID_Name_Map.put(modelName, modelIDMergedSearchArr[i]);
						}catch(Exception e){
							// For some reason Web Service API sometimes throws java.util.NoSuchElementException
							// if id exists but name can't be found.
							e.printStackTrace();
							model_ID_Name_Map.put(nameNotfound, modelIDMergedSearchArr[i]);
							nameNotfound = TokenMangler.getNextEnumeratedToken(nameNotfound);
						}

					}
					//Make name,id rowdata for tablelist to allow user selection
					String[] sortedModelNames = model_ID_Name_Map.keySet().toArray(new String[0]);
					final String[][] rowData = new String[sortedModelNames.length][2];
					for (int i = 0; i < sortedModelNames.length; i++) {
						rowData[i][0] = sortedModelNames[i];
						rowData[i][1] = model_ID_Name_Map.get(sortedModelNames[i]);
					}
					//Allow user to select model for opening
					final String importNow = "Import";
					final String cancel = "Cancel";
					DialogUtils.TableListResult result = DialogUtils.showComponentOptionsTableList(
							BioModelsNetJPanel.this, "Select a BioModel to import",
							new String[] {"Model Names","BioModels Entry ID"}, rowData,
							ListSelectionModel.SINGLE_SELECTION,null,
							new String[] {importNow,cancel},importNow,new Comparator<Object>() {

								public int compare(Object o1, Object o2) {
									if (o1 instanceof String && o2 instanceof String) {
										return ((String)o1).compareTo((String)o2);
									}
									throw new RuntimeException("row data should only be String");
								}
								
							});
					
					if(result.selectedOption != null && result.selectedOption.equals(importNow)){
						//Close Dialog showing "this" panel so ProgressPopup not obscured during openDocument
						SwingUtilities.invokeAndWait(new Runnable() {public void run() {disposeParentDialog();}});
						closeClientTaskStatusSupport(clientTaskStatusSupport);
						
						//Download and open (in new window) SBML document selected by user
						String bioModelSBML = bioModelsWebServices.getModelSBMLById(rowData[result.selectedTableRows[0]][1]);
						final ExternalDocInfo externalDocInfo = new ExternalDocInfo(bioModelSBML,sortedModelNames[result.selectedTableRows[0]]);
						new Thread(new Runnable() {
							public void run() {
								documentWindow.getTopLevelWindowManager().getRequestManager().openDocument(
										externalDocInfo,documentWindow.getTopLevelWindowManager(), true);
							}
						}).start();
					}
				}else{
					closeClientTaskStatusSupport(clientTaskStatusSupport);
					DialogUtils.showWarningDialog(BioModelsNetJPanel.this,
							"No BioModels found matching current search criteria.");
					throw UserCancelException.CANCEL_GENERIC;
				}

			}
		};
		

		ClientTaskDispatcher.dispatch(
				BioModelsNetJPanel.this, new Hashtable<String, Object>(), new AsynchClientTask[] { searchTask},
				true,true,null,true);
	}

	private void closeClientTaskStatusSupport(ClientTaskStatusSupport clientTaskStatusSupport){
		if(clientTaskStatusSupport instanceof AsynchProgressPopup){
			((AsynchProgressPopup)clientTaskStatusSupport).stop();
		}
	}
}
