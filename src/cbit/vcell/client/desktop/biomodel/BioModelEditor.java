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
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.ListSelectionModel;

import org.vcell.pathway.BioPaxObject;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDocument;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DialogUtils.TableListResult;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.ClientTaskManager;
import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayData;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderNode;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.geometry.gui.CSGObjectPropertiesPanel;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.gui.DataSymbolsSpecPanel;
import cbit.vcell.mapping.gui.SpeciesContextSpecPanel;
import cbit.vcell.model.Model;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.gui.KineticsTypeTemplatePanel;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.ode.gui.SimulationSummaryPanel;
import cbit.vcell.units.UnitSystemProvider;
import cbit.vcell.units.VCUnitSystem;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 2:55:18 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class BioModelEditor extends DocumentEditor {
	private BioModelWindowManager bioModelWindowManager = null;
	private BioModel bioModel = null;
	
	private BioModelEditorModelPanel bioModelEditorModelPanel = null;
	private ScriptingPanel scriptingPanel = null;

	private BioModelEditorTreeCellRenderer bioModelEditorTreeCellRenderer = null;
	private BioModelEditorTreeModel bioModelEditorTreeModel = null;
	private BioModelPropertiesPanel bioModelPropertiesPanel = null;	
	private BioModelEditorAnnotationPanel bioModelEditorAnnotationPanel = null;	
	private BioModelEditorApplicationsPanel bioModelEditorApplicationsPanel = null;
	
	private BioModelsNetPanel bioModelsNetPanel = null;
	private BioModelsNetPropertiesPanel bioModelsNetPropertiesPanel = null;
	private BioModelEditorPathwayPanel bioModelEditorPathwayPanel = null;
	private BioModelEditorPathwayCommonsPanel bioModelEditorPathwayCommonsPanel;
	private BioModelEditorSabioPanel bioModelEditorSabioPanel;
	private BioModelEditorPathwayDiagramPanel bioModelEditorPathwayDiagramPanel = null;
	
	private ReactionPropertiesPanel reactionStepPropertiesPanel = null;
	private SpeciesPropertiesPanel speciesPropertiesPanel = null;
	private StructurePropertiesPanel structurePropertiesPanel = null;
	private ParameterPropertiesPanel parameterPropertiesPanel = null;
	private ReactionParticipantPropertiesPanel reactionParticipantPropertiesPanel = null;
	private ApplicationPropertiesPanel applicationPropertiesPanel = null;
	private SpeciesContextSpecPanel speciesContextSpecPanel = null;
	private KineticsTypeTemplatePanel kineticsTypeTemplatePanel = null;
	private SimulationSummaryPanel simulationSummaryPanel = null;
	private EventPanel eventPanel = null;
	private DataSymbolsSpecPanel dataSymbolsSpecPanel = null;
	private BioModelEditorApplicationPanel bioModelEditorApplicationPanel = null;
	private ApplicationsPropertiesPanel applicationsPropertiesPanel;
	private BioModelParametersPanel bioModelParametersPanel;
//	private DataSymbolsPanel dataSymbolsPanel = null;
	private BioPaxObjectPropertiesPanel bioPaxObjectPropertiesPanel = null;
	private ParameterEstimationTaskPropertiesPanel parameterEstimationTaskPropertiesPanel = null;
	
	private CSGObjectPropertiesPanel csgObjectPropertiesPanel;
	
	/**
 * BioModelEditor constructor comment.
 */
	public BioModelEditor() {
		super();
		initialize();
	}

@Override
protected void popupMenuActionPerformed(DocumentEditorPopupMenuAction action, String actionCommand) {	
	Model model = bioModel.getModel();
	final SimulationContext selectedSimulationContext = getSelectedSimulationContext();
	switch (action) {
	case add_new: 
		try {
			Object obj = documentEditorTree.getLastSelectedPathComponent();
			if (obj == null || !(obj instanceof BioModelNode)) {
				return;
			}
			BioModelNode selectedNode = (BioModelNode) obj;
			Object userObject = selectedNode.getUserObject();
			if (userObject instanceof DocumentEditorTreeFolderNode) {
				DocumentEditorTreeFolderClass folderClass = ((DocumentEditorTreeFolderNode) userObject).getFolderClass();
				Object newObject = null;
				switch (folderClass) {
				case REACTIONS_NODE:
					newObject = model.createSimpleReaction(model.getStructure(0));
					break;
				case STRUCTURES_NODE:
					newObject = model.createFeature();
					break;
				case SPECIES_NODE:
					newObject = model.createSpeciesContext(model.getStructure(0));
					break;
				case SIMULATIONS_NODE:
					if (selectedSimulationContext != null) {
						AsynchClientTask task1 = new AsynchClientTask("new simulation", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
							
							@Override
							public void run(Hashtable<String, Object> hashTable) throws Exception {
								selectedSimulationContext.refreshMathDescription();
							}
						};
						AsynchClientTask task2 = new AsynchClientTask("new simulation", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
							
							@Override
							public void run(Hashtable<String, Object> hashTable) throws Exception {
								Object newsim = selectedSimulationContext.addNewSimulation(SimulationOwner.DEFAULT_SIM_NAME_PREFIX);
								selectionManager.setSelectedObjects(new Object[]{newsim});
							}
						};
						ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2});
					}
					break;
				}
				if (newObject != null) {
					selectionManager.setSelectedObjects(new Object[]{newObject});
				}
			}
		} catch (Exception ex) {
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}
		break;
	case add_new_app_deterministic:
	case add_new_app_stochastic:
		AsynchClientTask task = new AsynchClientTask("show application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
				selectionManager.setSelectedObjects(new Object[]{newSimulationContext});
			}
		};
		AsynchClientTask[] newApplicationTasks = ClientTaskManager.newApplication(this,bioModel, action == DocumentEditorPopupMenuAction.add_new_app_stochastic);
		AsynchClientTask[] tasks = new AsynchClientTask[newApplicationTasks.length + 1];
		System.arraycopy(newApplicationTasks, 0, tasks, 0, newApplicationTasks.length);
		tasks[newApplicationTasks.length] = task;
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), tasks);
		break;
	case copy_app:
		if (actionCommand.equals(GuiConstants.ACTIONCMD_COPY_APPLICATION)) {
			copyApplication();
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_NON_SPATIAL_COPY_TO_STOCHASTIC_APPLICATION)) {
			copyApplication(false, true);
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_NON_SPATIAL_COPY_TO_DETERMINISTIC_APPLICATION)) {
			copyApplication(false, false);
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_DETERMINISTIC_APPLICATION)) {
			copyApplication(false, false);
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_STOCHASTIC_APPLICATION)) {
			copyApplication(false, true);
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_DETERMINISTIC_APPLICATION)) {
			copyApplication(true, false);
		} else if (actionCommand.equals(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_STOCHASTIC_APPLICATION)) {
			copyApplication(true, true);
		}
		break;
	case app_new_biomodel:
		if(actionCommand.equals(GuiConstants.MENU_TEXT_APP_NEWBIOMODEL)){
			createNewBiomodelFromApp();
		}
		break;
	case delete:
		try {
			if (selectedSimulationContext != null) {
				String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Deleting application", "You are going to delete the Application '" + selectedSimulationContext.getName() + "'. Continue?");
				if (confirm.equals(UserMessage.OPTION_CANCEL)) {
					return;
				}
				deleteSimulationcontexts(new SimulationContext[] {selectedSimulationContext});
			}
		} catch (Exception ex) {
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}
		break;
	case deleteChoose:
		try {
			SimulationContext[] allSimContexts =
				Arrays.copyOf(getBioModelWindowManager().getVCDocument().getSimulationContexts(), getBioModelWindowManager().getVCDocument().getSimulationContexts().length);
			Arrays.sort(allSimContexts, new Comparator<SimulationContext>() {
				@Override
				public int compare(SimulationContext o1, SimulationContext o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});
			String[][] rowDataOrig = new String[allSimContexts.length][2];
			for (int i = 0; i < allSimContexts.length; i++) {
				rowDataOrig[i][0] = allSimContexts[i].getName();
				rowDataOrig[i][1] = allSimContexts[i].getSimulations().length+"";
			}
			final String DELETE = "Delete";
			final String CANCEL = "Cancel";
			TableListResult result =
				DialogUtils.showComponentOptionsTableList(this, "Select Applications (and associated Simulations) to delete.",
				new String[] {"Application","# of Sims"}, rowDataOrig,ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, null, new String[] {DELETE,CANCEL}, CANCEL, null);
			if(result != null && result.selectedOption != null && result.selectedOption.equals(DELETE) && result.selectedTableRows != null && result.selectedTableRows.length > 0){
				ArrayList<SimulationContext> deleteTheseSimcontexts = new ArrayList<SimulationContext>();
				for (int i = 0; i < result.selectedTableRows.length; i++) {
					deleteTheseSimcontexts.add(allSimContexts[result.selectedTableRows[i]]);
				}
				deleteSimulationcontexts(deleteTheseSimcontexts.toArray(new SimulationContext[0]));					
			}
		} catch (Exception ex) {
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}
		break;
	}
}

private void deleteSimulationcontexts(SimulationContext[] deleteTheseSimcontexts) throws PropertyVetoException{
	for (int i = 0; i < deleteTheseSimcontexts.length; i++) {
		Simulation[] simulations = deleteTheseSimcontexts[i].getSimulations();
		if(simulations != null && simulations.length != 0){
			for (Simulation simulation : simulations) {
				bioModel.removeSimulation(simulation);
			}
		}
		bioModel.removeSimulationContext(deleteTheseSimcontexts[i]);
	}
}
private void copyApplication() {
	SimulationContext simulationContext = getSelectedSimulationContext();
	if (simulationContext == null) {
		PopupGenerator.showErrorDialog(this, "Please select an application.");
		return;
	}
	copyApplication(simulationContext.getGeometry().getDimension() > 0, simulationContext.isStoch());
}

private void createNewBiomodelFromApp(){
	SimulationContext simulationContext = getSelectedSimulationContext();
	if (simulationContext == null) {
		PopupGenerator.showErrorDialog(this, "Please select an application.");
		return;
	}
	//getBioModelWindowManager().getRequestManager().getDocumentManager().getSessionManager().getUserMetaDbServer().getBioModelXML(key);
	//getBioModelWindowManager().getRequestManager().exportDocument(manager);
//	bioModel.getVCMetaData().cleanupMetadata();
	try {
		String newBMXML = XmlHelper.bioModelToXML(bioModel);
		BioModel newBioModel = XmlHelper.XMLToBioModel(new XMLSource(newBMXML));
		newBioModel.clearVersion();
		SimulationContext[] newBMSimcontexts = newBioModel.getSimulationContexts();
		for (int i = 0; i < newBMSimcontexts.length; i++) {
			if(!newBMSimcontexts[i].getName().equals(simulationContext.getName())){
				//Remove sims before removing simcontext
				Simulation[] newBMSims = newBMSimcontexts[i].getSimulations();
				for (int j = 0; j < newBMSims.length; j++) {
					newBMSimcontexts[i].removeSimulation(newBMSims[j]);
				}
				newBioModel.removeSimulationContext(newBMSimcontexts[i]);
			}
		}
		VCDocument.DocumentCreationInfo newBMDocCreateInfo = new VCDocument.DocumentCreationInfo(VCDocument.BIOMODEL_DOC,VCDocument.BIO_OPTION_DEFAULT);
		newBMDocCreateInfo.setPreCreatedDocument(newBioModel);
		AsynchClientTask[] newBMTasks = getBioModelWindowManager().newDocument(newBMDocCreateInfo);
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), newBMTasks);
	} catch (Exception e) {
		e.printStackTrace();
		PopupGenerator.showErrorDialog(this, "Error creating new BioModel from Application '"+simulationContext.getName()+"'\n"+e.getMessage());
	}
}
private void copyApplication(final boolean bSpatial, final boolean bStochastic) {		
	final SimulationContext simulationContext = getSelectedSimulationContext();
	if (simulationContext == null) {
		PopupGenerator.showErrorDialog(this, "Please select an application.");
		return;
	}
	if (bStochastic) {
		//check validity if copy to stochastic application
		String message = bioModel.getModel().isValidForStochApp();
		if (!message.equals("")) {
			PopupGenerator.showErrorDialog(this, message);
			return;
		}
	}
	AsynchClientTask[] copyTasks = ClientTaskManager.copyApplication(this, bioModel, simulationContext, bSpatial, bStochastic);
	AsynchClientTask[] allTasks = new AsynchClientTask[copyTasks.length + 1];
	System.arraycopy(copyTasks, 0, allTasks, 0, copyTasks.length);
	allTasks[allTasks.length - 1] = new AsynchClientTask("showing", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {			
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
			selectionManager.setActiveView(new ActiveView(newSimulationContext, null, null));
		}
	};
	ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), allTasks,  false);
}

private BioModelEditorPathwayDiagramPanel getBioModelEditorPathwayDiagramPanel() {
	if (bioModelEditorPathwayDiagramPanel == null) {
		bioModelEditorPathwayDiagramPanel = new BioModelEditorPathwayDiagramPanel();		
	}
	return bioModelEditorPathwayDiagramPanel;
}

private BioModelEditorPathwayPanel getBioModelEditorPathwayPanel() {
	if (bioModelEditorPathwayPanel == null) {
		bioModelEditorPathwayPanel = new BioModelEditorPathwayPanel();		
	}
	return bioModelEditorPathwayPanel;
}

private BioModelsNetPropertiesPanel getBioModelsNetPropertiesPanel() {
	if (bioModelsNetPropertiesPanel == null) {
		bioModelsNetPropertiesPanel = new BioModelsNetPropertiesPanel();
	}
	return bioModelsNetPropertiesPanel;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}

private DataSymbolsSpecPanel getDataSymbolsSpecPanel() {
	if (dataSymbolsSpecPanel == null) {
		try {
			dataSymbolsSpecPanel = new DataSymbolsSpecPanel();
			dataSymbolsSpecPanel.setName("DataSymbolsSpecPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return dataSymbolsSpecPanel;
}

private EventPanel getEventPanel() {
	if (eventPanel == null) {
		eventPanel = new EventPanel();
		eventPanel.setName("EventPanel");
	}
	
	return eventPanel;
}
private SimulationSummaryPanel getSimulationSummaryPanel() {
	if (simulationSummaryPanel == null) {
		try {
			simulationSummaryPanel = new SimulationSummaryPanel();
			simulationSummaryPanel.setName("SimulationSummaryPanel1");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return simulationSummaryPanel;
}
private ApplicationPropertiesPanel getApplicationPropertiesPanel() {
	if (applicationPropertiesPanel == null) {
		applicationPropertiesPanel = new ApplicationPropertiesPanel();
	}
	return applicationPropertiesPanel;
}
private ReactionParticipantPropertiesPanel getReactionParticipantPropertiesPanel() {
	if (reactionParticipantPropertiesPanel == null) {
		reactionParticipantPropertiesPanel = new ReactionParticipantPropertiesPanel();
	}
	return reactionParticipantPropertiesPanel;
}
private ReactionPropertiesPanel getReactionPropertiesPanel() {
	if (reactionStepPropertiesPanel == null) {
		reactionStepPropertiesPanel = new ReactionPropertiesPanel();
	}
	return reactionStepPropertiesPanel;
}
private KineticsTypeTemplatePanel getKineticsTypeTemplatePanel() {
	if (kineticsTypeTemplatePanel == null) {
		try {
			kineticsTypeTemplatePanel = new KineticsTypeTemplatePanel(false);
			kineticsTypeTemplatePanel.setName("SimpleReactionPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return kineticsTypeTemplatePanel;
}
private SpeciesPropertiesPanel getSpeciesPropertiesPanel() {
	if (speciesPropertiesPanel == null) {
		speciesPropertiesPanel = new SpeciesPropertiesPanel();
	}
	return speciesPropertiesPanel;
}

private StructurePropertiesPanel getStructurePropertiesPanel() {
	if (structurePropertiesPanel == null) {
		structurePropertiesPanel = new StructurePropertiesPanel();
	}
	return structurePropertiesPanel;
}
private ParameterPropertiesPanel getParameterPropertiesPanel() {
	if (parameterPropertiesPanel == null) {
		parameterPropertiesPanel = new ParameterPropertiesPanel(new UnitSystemProvider() {
			public VCUnitSystem getUnitSystem() {
				return bioModel.getModel().getUnitSystem();
			}
		});
	}
	return parameterPropertiesPanel;
}
private ApplicationsPropertiesPanel getApplicationsPropertiesPanel() {
	if (applicationsPropertiesPanel == null) {
		applicationsPropertiesPanel = new ApplicationsPropertiesPanel();
	}
	return applicationsPropertiesPanel;
}

private SpeciesContextSpecPanel getSpeciesContextSpecPanel() {
	if (speciesContextSpecPanel == null) {
		try {
			speciesContextSpecPanel = new SpeciesContextSpecPanel();
			speciesContextSpecPanel.setName("SpeciesContextSpecPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return speciesContextSpecPanel;
}

private void initialize() {
	try {
		bioModelPropertiesPanel = new BioModelPropertiesPanel();
		bioModelEditorAnnotationPanel = new BioModelEditorAnnotationPanel();
		bioModelEditorModelPanel = new BioModelEditorModelPanel();
		bioModelEditorModelPanel.setMinimumSize(new java.awt.Dimension(198, 148));
		
		bioModelsNetPanel = new BioModelsNetPanel();
		bioModelEditorPathwayCommonsPanel = new BioModelEditorPathwayCommonsPanel();
		leftBottomTabbedPane.addTab(BioModelsNetPanel.BIO_MODELS_NET, bioModelsNetPanel);
		leftBottomTabbedPane.addTab("Pathway Comm", bioModelEditorPathwayCommonsPanel);
		bioModelEditorSabioPanel = new BioModelEditorSabioPanel();
		leftBottomTabbedPane.addTab("Sabio", bioModelEditorSabioPanel);
		rightSplitPane.setTopComponent(bioModelEditorModelPanel);
		
		bioModelEditorTreeModel = new BioModelEditorTreeModel(documentEditorTree);
		bioModelEditorTreeCellRenderer = new BioModelEditorTreeCellRenderer();
		documentEditorTree.setModel(bioModelEditorTreeModel);
		documentEditorTree.addTreeExpansionListener(bioModelEditorTreeModel);
		documentEditorTree.setCellRenderer(bioModelEditorTreeCellRenderer);
		
		bioModelParametersPanel = new BioModelParametersPanel();
		bioModelParametersPanel.setSelectionManager(selectionManager);
		bioModelParametersPanel.setIssueManager(issueManager);
		
		bioModelEditorApplicationPanel = new BioModelEditorApplicationPanel();
		bioModelEditorApplicationPanel.setSelectionManager(selectionManager);
		bioModelEditorApplicationPanel.setIssueManager(issueManager);
				
//		dataSymbolsPanel = new DataSymbolsPanel();
//		dataSymbolsPanel.setSelectionManager(selectionManager);
		
		bioPaxObjectPropertiesPanel = new BioPaxObjectPropertiesPanel();
		bioPaxObjectPropertiesPanel.setSelectionManager(selectionManager);
		
		parameterEstimationTaskPropertiesPanel = new ParameterEstimationTaskPropertiesPanel();
		parameterEstimationTaskPropertiesPanel.setSelectionManager(selectionManager);
		
		bioModelEditorAnnotationPanel.setSelectionManager(selectionManager);
		bioModelEditorTreeModel.setSelectionManager(selectionManager);		
		bioModelEditorModelPanel.setSelectionManager(selectionManager);
		bioModelEditorModelPanel.setIssueManager(issueManager);
		getBioModelsNetPropertiesPanel().setSelectionManager(selectionManager);
		getBioModelEditorApplicationsPanel().setSelectionManager(selectionManager);
		getReactionPropertiesPanel().setSelectionManager(selectionManager);
		getSpeciesContextSpecPanel().setSelectionManager(selectionManager);
		getKineticsTypeTemplatePanel().setSelectionManager(selectionManager);
		getSimulationSummaryPanel().setSelectionManager(selectionManager);
		getEventPanel().setSelectionManager(selectionManager);
		getDataSymbolsSpecPanel().setSelectionManager(selectionManager);
		getBioModelEditorPathwayPanel().setSelectionManager(selectionManager);
		getBioModelEditorPathwayDiagramPanel().setSelectionManager(selectionManager);
		bioModelsNetPanel.setSelectionManager(selectionManager);
		bioModelEditorPathwayCommonsPanel.setSelectionManager(selectionManager);
		bioModelEditorSabioPanel.setSelectionManager(selectionManager);
		getBioModelsNetPropertiesPanel().setSelectionManager(selectionManager);
		getApplicationPropertiesPanel().setSelectionManager(selectionManager);
		getStructurePropertiesPanel().setSelectionManager(selectionManager);
		getSpeciesPropertiesPanel().setSelectionManager(selectionManager);
		getParameterPropertiesPanel().setSelectionManager(selectionManager);
		getReactionParticipantPropertiesPanel().setSelectionManager(selectionManager);
		
		csgObjectPropertiesPanel = new CSGObjectPropertiesPanel();
		csgObjectPropertiesPanel.setSelectionManager(selectionManager);
		csgObjectPropertiesPanel.setIssueManager(issueManager);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

@Override
protected void setRightBottomPanelOnSelection(Object[] selections) {
	if (selections == null) {
		return;
	}
	JComponent bottomComponent = rightBottomEmptyPanel;
	int destComponentIndex = DocumentEditorTabID.object_properties.ordinal();
	boolean bShowInDatabaseProperties = false;
	boolean bShowPathway = false;
	if (selections.length == 1) {
		Object singleSelection = selections[0];
		if (singleSelection instanceof ReactionStep) {
			bottomComponent = getReactionPropertiesPanel();
		} else if (singleSelection instanceof SpeciesContext) {
			bottomComponent = getSpeciesPropertiesPanel();
		} else if (singleSelection instanceof Structure) {
			bottomComponent = getStructurePropertiesPanel();
			getStructurePropertiesPanel().setModel(bioModel.getModel());
		} else if (singleSelection instanceof Parameter) {
			bottomComponent = getParameterPropertiesPanel();
		} else if (singleSelection instanceof SimulationContext) {
			bottomComponent = getApplicationPropertiesPanel();
		} else if (singleSelection instanceof ParameterEstimationTask) {
			bottomComponent = parameterEstimationTaskPropertiesPanel;
		} else if (singleSelection instanceof Product || singleSelection instanceof Reactant) {
			bottomComponent = getReactionParticipantPropertiesPanel();
		} else if (singleSelection instanceof BioModelInfo) {
			bShowInDatabaseProperties = true;
			bottomComponent = bioModelMetaDataPanel;
		} else if (singleSelection instanceof MathModelInfo) {
			bShowInDatabaseProperties = true;
			bottomComponent = mathModelMetaDataPanel;
		} else if (singleSelection instanceof GeometryInfo) {
			bShowInDatabaseProperties = true;
			bottomComponent = geometryMetaDataPanel;
		} else if (singleSelection instanceof SpeciesContextSpec) {
			bottomComponent = getSpeciesContextSpecPanel();
		} else if (singleSelection instanceof ReactionSpec) {
			bottomComponent = getKineticsTypeTemplatePanel();
		} else if (singleSelection instanceof BioModelsNetModelInfo) {
			bShowInDatabaseProperties = true;
			bottomComponent = getBioModelsNetPropertiesPanel();
		} else if (singleSelection instanceof Simulation) {
			bottomComponent = getSimulationSummaryPanel();
		} else if (singleSelection instanceof DataSymbol) {
			bottomComponent = getDataSymbolsSpecPanel();
		} else if (singleSelection instanceof BioEvent) {
			bottomComponent = getEventPanel();
		} else if (singleSelection instanceof BioPaxObject) {
			bottomComponent = bioPaxObjectPropertiesPanel;
		} else if (singleSelection instanceof BioModel || singleSelection instanceof VCMetaData) {
			bottomComponent = bioModelEditorAnnotationPanel;
		} else if (singleSelection instanceof PathwayData) {
			bShowPathway = true;
			bottomComponent = getBioModelEditorPathwayPanel();
		} else if (singleSelection instanceof Model) {
		} else if (singleSelection instanceof CSGObject) {
			bottomComponent = csgObjectPropertiesPanel;
			csgObjectPropertiesPanel.setSimulationContext(getSelectedSimulationContext());
		} else if (singleSelection instanceof DocumentEditorTreeFolderNode) {
			DocumentEditorTreeFolderClass folderClass = ((DocumentEditorTreeFolderNode)singleSelection).getFolderClass();
			if (folderClass == DocumentEditorTreeFolderClass.REACTIONS_NODE) {
				bottomComponent = getReactionPropertiesPanel();
			} else if (folderClass == DocumentEditorTreeFolderClass.STRUCTURES_NODE) {
				bottomComponent = getStructurePropertiesPanel();
			} else if (folderClass == DocumentEditorTreeFolderClass.SPECIES_NODE) {
				bottomComponent = getSpeciesPropertiesPanel();
			} else if (folderClass == DocumentEditorTreeFolderClass.APPLICATIONS_NODE) {
				bottomComponent = getApplicationsPropertiesPanel();
				getApplicationsPropertiesPanel().setBioModel(bioModel);
			} else if (folderClass == DocumentEditorTreeFolderClass.PARAMETER_ESTIMATION_NODE) {
				bottomComponent = parameterEstimationTaskPropertiesPanel;
			}
		}
	}
	if (bShowPathway) {
		for (destComponentIndex = 0; destComponentIndex < rightBottomTabbedPane.getTabCount(); destComponentIndex ++) {
			if (rightBottomTabbedPane.getComponentAt(destComponentIndex) == bottomComponent) {
				break;
			}
		}
		String tabTitle = "Pathway Preview";
		if (rightBottomTabbedPane.getTabCount() == destComponentIndex) {
			rightBottomTabbedPane.addTab(tabTitle, new TabCloseIcon(), bottomComponent);
		}
	} else if (bShowInDatabaseProperties) {
		for (destComponentIndex = 0; destComponentIndex < rightBottomTabbedPane.getTabCount(); destComponentIndex ++) {
			Component c = rightBottomTabbedPane.getComponentAt(destComponentIndex);
			if (c == bioModelMetaDataPanel
				|| c == mathModelMetaDataPanel
				|| c == geometryMetaDataPanel
				|| c == getBioModelsNetPropertiesPanel()) {
				break;
			}
		}
		if (rightBottomTabbedPane.getTabCount() == destComponentIndex) {
			rightBottomTabbedPane.addTab(DATABASE_PROPERTIES_TAB_TITLE, new TabCloseIcon(), bottomComponent);
		}
	}
	if (rightBottomTabbedPane.getComponentAt(destComponentIndex) != bottomComponent) {
		bottomComponent.setBorder(GuiConstants.TAB_PANEL_BORDER);
		rightBottomTabbedPane.setComponentAt(destComponentIndex, bottomComponent);
		rightSplitPane.repaint();
	}
	if (rightBottomTabbedPane.getSelectedComponent() != bottomComponent) {
		rightBottomTabbedPane.setSelectedComponent(bottomComponent);
	}
}


private ScriptingPanel getScriptingPanel() {
	if (scriptingPanel == null) {
		try {
			scriptingPanel = new ScriptingPanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return scriptingPanel;
}

@Override
protected void treeSelectionChanged() {
	try {
		Object lastSelectedPathComponent = documentEditorTree.getLastSelectedPathComponent();
		if (lastSelectedPathComponent == null || !(lastSelectedPathComponent instanceof BioModelNode)) {
			return;
		}
		BioModelNode selectedNode = (BioModelNode)lastSelectedPathComponent;
	    Object selectedObject = selectedNode.getUserObject();
	    SimulationContext simulationContext = getSelectedSimulationContext();
	    setRightTopPanel(selectedObject, simulationContext);
	}catch (Exception ex){
		ex.printStackTrace(System.out);
	}
}

private void setRightTopPanel(Object selectedObject, SimulationContext simulationContext) {
	 JComponent newTopPanel = emptyPanel;
	int dividerLocation = rightSplitPane.getDividerLocation();//DEFAULT_DIVIDER_LOCATION;
	if (selectedObject instanceof Model) {
		newTopPanel = bioModelEditorModelPanel;
	} else if (selectedObject instanceof BioModel || selectedObject instanceof VCMetaData || selectedObject instanceof MiriamResource) {			
		newTopPanel = bioModelPropertiesPanel;
		bioModelPropertiesPanel.setBioModel(bioModel);
	} else if (selectedObject instanceof SimulationContext) {
		newTopPanel = bioModelEditorApplicationPanel;
		bioModelEditorApplicationPanel.setSimulationContext(simulationContext);
	} else if (selectedObject instanceof DocumentEditorTreeFolderNode) {
		DocumentEditorTreeFolderNode folderNode = (DocumentEditorTreeFolderNode) selectedObject;
		DocumentEditorTreeFolderClass folderClass = folderNode.getFolderClass();
		if (folderClass == DocumentEditorTreeFolderClass.MODEL_NODE) {
			newTopPanel = bioModelEditorModelPanel;
		} else if (folderClass == DocumentEditorTreeFolderClass.STRUCTURES_NODE
				|| folderClass == DocumentEditorTreeFolderClass.SPECIES_NODE
				|| folderClass == DocumentEditorTreeFolderClass.REACTIONS_NODE
				|| folderClass == DocumentEditorTreeFolderClass.REACTION_DIAGRAM_NODE
//				|| folderClass == DocumentEditorTreeFolderClass.STRUCTURE_DIAGRAM_NODE
				) {
			newTopPanel = bioModelEditorModelPanel;
		} else if (folderClass == DocumentEditorTreeFolderClass.PATHWAY_DIAGRAM_NODE
				|| folderClass == DocumentEditorTreeFolderClass.PATHWAY_OBJECTS_NODE
				|| folderClass == DocumentEditorTreeFolderClass.BIOPAX_SUMMARY_NODE
				|| folderClass == DocumentEditorTreeFolderClass.BIOPAX_TREE_NODE) {
			newTopPanel = bioModelEditorPathwayDiagramPanel;
		} else if (folderClass == DocumentEditorTreeFolderClass.PATHWAY_NODE) {
			newTopPanel = getBioModelEditorPathwayDiagramPanel();
			getBioModelEditorPathwayDiagramPanel().setBioModel(bioModel);
		} else if (folderClass == DocumentEditorTreeFolderClass.BIOMODEL_PARAMETERS_NODE) {
			newTopPanel = bioModelParametersPanel;
		} else if (folderClass == DocumentEditorTreeFolderClass.APPLICATIONS_NODE) {
			newTopPanel = bioModelEditorApplicationsPanel;
//		} else if (folderClass == DocumentEditorTreeFolderClass.DATA_NODE) {
//			newTopPanel = dataSymbolsPanel;
		} else if (folderClass == DocumentEditorTreeFolderClass.SCRIPTING_NODE) {
			newTopPanel = getScriptingPanel();
		} else if (folderClass == DocumentEditorTreeFolderClass.SPECIFICATIONS_NODE
				|| folderClass == DocumentEditorTreeFolderClass.PROTOCOLS_NODE
				|| folderClass == DocumentEditorTreeFolderClass.SIMULATIONS_NODE
				|| folderClass == DocumentEditorTreeFolderClass.GEOMETRY_NODE
				|| folderClass == DocumentEditorTreeFolderClass.PARAMETER_ESTIMATION_NODE) {
			newTopPanel = bioModelEditorApplicationPanel;
			bioModelEditorApplicationPanel.setSimulationContext(simulationContext);
		}
	}
	Component rightTopComponent = rightSplitPane.getTopComponent();
	if (rightTopComponent != newTopPanel) {		
		rightSplitPane.setTopComponent(newTopPanel);
	}
	rightSplitPane.setDividerLocation(dividerLocation);	
}

private BioModelEditorApplicationsPanel getBioModelEditorApplicationsPanel() {
	if (bioModelEditorApplicationsPanel == null) {
		try {
			bioModelEditorApplicationsPanel   = new BioModelEditorApplicationsPanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return bioModelEditorApplicationsPanel;
}

/**
 * Sets the bioModel property (cbit.vcell.biomodel.BioModel) value.
 * @param bioModel The new value for the property.
 * @see #getBioModel
 */
public void setBioModel(BioModel bioModel) {
	if (this.bioModel == bioModel) {
		return;
	}
	this.bioModel = bioModel;
	bioModelEditorModelPanel.setBioModel(bioModel);
	getBioModelEditorApplicationsPanel().setBioModel(bioModel);
	getScriptingPanel().setBioModel(bioModel);	
	getBioModelEditorPathwayPanel().setBioModel(bioModel);
	getBioModelEditorPathwayDiagramPanel().setBioModel(bioModel);
	bioModelEditorTreeModel.setBioModel(bioModel);
	bioModelEditorAnnotationPanel.setBioModel(bioModel);
	bioModelParametersPanel.setBioModel(bioModel);
	issueManager.setVCDocument(bioModel);
	
	bioModelEditorTreeCellRenderer.setBioModel(bioModel);
	
	getReactionPropertiesPanel().setBioModel(bioModel); 
	getSpeciesPropertiesPanel().setBioModel(bioModel);
	bioPaxObjectPropertiesPanel.setBioModel(bioModel);
}

/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 5:40:13 PM)
 * @param newBioModelWindowManager cbit.vcell.client.desktop.BioModelWindowManager
 */
public void setBioModelWindowManager(BioModelWindowManager bioModelWindowManager) {
	if (this.bioModelWindowManager == bioModelWindowManager) {
		return;
	}
	this.bioModelWindowManager = bioModelWindowManager;
	bioModelsNetPanel.setDocumentWindowManager(bioModelWindowManager);
	getBioModelsNetPropertiesPanel().setDocumentWindowManager(bioModelWindowManager);
	bioModelPropertiesPanel.setBioModelWindowManager(bioModelWindowManager);
	bioModelParametersPanel.setBioModelWindowManager(bioModelWindowManager);
	
	DatabaseWindowManager dbWindowManager = new DatabaseWindowManager(databaseWindowPanel, bioModelWindowManager.getRequestManager());
	databaseWindowPanel.setDatabaseWindowManager(dbWindowManager);
	DocumentManager documentManager = bioModelWindowManager.getRequestManager().getDocumentManager();
	databaseWindowPanel.setDocumentManager(documentManager);
	bioModelEditorModelPanel.setDocumentManager(documentManager);
	
	geometryMetaDataPanel.setDocumentManager(documentManager);
	
	bioModelEditorApplicationPanel.setBioModelWindowManager(bioModelWindowManager);
}

public final BioModelWindowManager getBioModelWindowManager() {
	return bioModelWindowManager;
}

}
