package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.tree.TreeNode;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayData;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderNode;
import cbit.vcell.client.desktop.geometry.GeometrySummaryViewer;
import cbit.vcell.client.desktop.simulation.OutputFunctionsPanel;
import cbit.vcell.client.desktop.simulation.SimulationListPanel;
import cbit.vcell.client.desktop.simulation.SimulationWorkspace;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.desktop.BioModelMetaDataPanel;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.desktop.GeometryMetaDataPanel;
import cbit.vcell.desktop.MathModelMetaDataPanel;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.gui.DataSymbolsPanel;
import cbit.vcell.mapping.gui.DataSymbolsSpecPanel;
import cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel;
import cbit.vcell.mapping.gui.InitialConditionsPanel;
import cbit.vcell.mapping.gui.MicroscopeMeasurementPanel;
import cbit.vcell.mapping.gui.ReactionSpecsPanel;
import cbit.vcell.mapping.gui.SpeciesContextSpecPanel;
import cbit.vcell.mapping.gui.StructureMappingCartoonPanel;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.gui.KineticsTypeTemplatePanel;
import cbit.vcell.modelopt.gui.OptTestPanel;
import cbit.vcell.opt.solvers.OptimizationService;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.ode.gui.SimulationSummaryPanel;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 2:55:18 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class BioModelEditor extends DocumentEditor {
	private static final String DATABASE_PROPERTIES_TAB_TITLE = "Database File Info";
	private BioModelWindowManager bioModelWindowManager = null;
	private BioModel bioModel = new BioModel(null);
	
	private OptTestPanel ivjoptTestPanel = null;
	private AnalysisPanel ivjParameterEstimationPanel = null;
	private SimulationListPanel ivjSimulationListPanel = null;
	private GeometrySummaryViewer ivjGeometrySummaryViewer = null;
	private StructureMappingCartoonPanel ivjStructureMappingCartoonPanel = null;
	private OutputFunctionsPanel outputFunctionsPanel = null;
	private InitialConditionsPanel initialConditionsPanel = null;
	private DataSymbolsPanel dataSymbolsPanel = null;	
	private ReactionSpecsPanel reactionSpecsPanel = null;
	private ElectricalMembraneMappingPanel ivjElectricalMembraneMappingPanel = null;
	private EventsDisplayPanel eventsDisplayPanel = null;
	private MathematicsPanel mathematicsPanel = null;
	private BioModelEditorModelPanel bioModelEditorModelPanel = null;
	private MicroscopeMeasurementPanel microscopeMeasurementPanel = null;
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
	private BioModelEditorPathwayDiagramPanel bioModelEditorPathwayDiagramPanel = null;
	
	private ReactionPropertiesPanel reactionStepPropertiesPanel = null;
	private SpeciesPropertiesPanel speciesPropertiesPanel = null;
	private StructurePropertiesPanel structurePropertiesPanel = null;
	private ModelParameterPropertiesPanel modelParameterPropertiesPanel = null;
	private ReactionParticipantPropertiesPanel reactionParticipantPropertiesPanel = null;
	private ApplicationPropertiesPanel applicationPropertiesPanel = null;
	private SpeciesContextSpecPanel speciesContextSpecPanel = null;
	private KineticsTypeTemplatePanel kineticsTypeTemplatePanel = null;
	private SimulationSummaryPanel simulationSummaryPanel = null;
	private EventPanel eventPanel = null;
	private DataSymbolsSpecPanel dataSymbolsSpecPanel = null;
	
	private JTabbedPane rightBottomTabbedPane = null;
	private JPanel rightBottomEmptyPanel = null;

/**
 * BioModelEditor constructor comment.
 */
public BioModelEditor() {
	super();
	initialize();
}

/**
 * Method generated to support the promotion of the optimizationService attribute.
 * @return cbit.vcell.opt.solvers.OptimizationService
 */
public OptimizationService getOptimizationService() {
	return getoptTestPanel().getOptimizationService();
}


/**
 * Return the optTestPanel property value.
 * @return cbit.vcell.modelopt.gui.OptTestPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private OptTestPanel getoptTestPanel() {
	if (ivjoptTestPanel == null) {
		try {
			ivjoptTestPanel = new OptTestPanel();
			ivjoptTestPanel.setName("optTestPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjoptTestPanel;
}


/**
 * Return the ParameterEstimationPanel property value.
 * @return javax.swing.JPanel
 */
private AnalysisPanel getParameterEstimationPanel() {
	if (ivjParameterEstimationPanel == null) {
		try {
			ivjParameterEstimationPanel = new AnalysisPanel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjParameterEstimationPanel;
}

/**
 * Return the SimulationListPanel1 property value.
 * @return cbit.vcell.client.desktop.simulation.SimulationListPanel
 */
private SimulationListPanel getSimulationListPanel() {
	if (ivjSimulationListPanel == null) {
		try {
			ivjSimulationListPanel = new SimulationListPanel();
			ivjSimulationListPanel.setName("SimulationListPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSimulationListPanel;
}

private MicroscopeMeasurementPanel getMicroscopeMeasurementPanel() {
	if (microscopeMeasurementPanel == null) {
		try {
			microscopeMeasurementPanel = new MicroscopeMeasurementPanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return microscopeMeasurementPanel;
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
 * Method generated to support the promotion of the userPreferences attribute.
 * @return cbit.vcell.client.server.UserPreferences
 */
public UserPreferences getUserPreferences() {
	return getoptTestPanel().getUserPreferences();
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
private ModelParameterPropertiesPanel getModelParameterPropertiesPanel() {
	if (modelParameterPropertiesPanel == null) {
		modelParameterPropertiesPanel = new ModelParameterPropertiesPanel();
	}
	return modelParameterPropertiesPanel;
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
		leftTabbedPaneBottom.addTab("BioModels.net", bioModelsNetPanel);
		leftTabbedPaneBottom.addTab("Pathway Commons", bioModelEditorPathwayCommonsPanel);
		
		rightBottomEmptyPanel = new JPanel(new GridBagLayout());
		rightBottomEmptyPanel.setBackground(Color.white);
		JLabel label = new JLabel("Select only one object (e.g. species, reaction) to show properties.");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(10,10,4,4);
		gbc.gridy = 0;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.PAGE_START;
		rightBottomEmptyPanel.add(label, gbc);

		rightSplitPane.setTopComponent(bioModelEditorModelPanel);
		rightBottomTabbedPane = new JTabbedPane();
		rightBottomTabbedPane.addTab("Object Properties", rightBottomEmptyPanel);		
		rightBottomTabbedPane.setMinimumSize(new java.awt.Dimension(198, 148));		
		rightSplitPane.setBottomComponent(rightBottomTabbedPane);
		
		bioModelEditorTreeModel = new BioModelEditorTreeModel(documentEditorTree);
		bioModelEditorTreeCellRenderer = new BioModelEditorTreeCellRenderer(documentEditorTree);
		documentEditorTree.setModel(bioModelEditorTreeModel);
		documentEditorTree.setCellRenderer(bioModelEditorTreeCellRenderer);
		
		bioModelEditorAnnotationPanel.setSelectionManager(selectionManager);
		getOutputFunctionsPanel().setSelectionManager(selectionManager);
		bioModelEditorTreeModel.setSelectionManager(selectionManager);		
		bioModelEditorModelPanel.setSelectionManager(selectionManager);
		getBioModelsNetPropertiesPanel().setSelectionManager(selectionManager);
		getBioModelEditorApplicationsPanel().setSelectionManager(selectionManager);
		getReactionPropertiesPanel().setSelectionManager(selectionManager);
		getInitialConditionsPanel().setSelectionManager(selectionManager);
		getSpeciesContextSpecPanel().setSelectionManager(selectionManager);
		getKineticsTypeTemplatePanel().setSelectionManager(selectionManager);
		getReactionSpecsPanel().setSelectionManager(selectionManager);
		getSimulationListPanel().setSelectionManager(selectionManager);
		getSimulationSummaryPanel().setSelectionManager(selectionManager);
		getEventsDisplayPanel().setSelectionManager(selectionManager);
		getEventPanel().setSelectionManager(selectionManager);
		getDataSymbolsPanel().setSelectionManager(selectionManager);
		getParameterEstimationPanel().setSelectionManager(selectionManager);
		getDataSymbolsSpecPanel().setSelectionManager(selectionManager);
		getBioModelEditorPathwayPanel().setSelectionManager(selectionManager);
		getBioModelEditorPathwayDiagramPanel().setSelectionManager(selectionManager);
		bioModelsNetPanel.setSelectionManager(selectionManager);
		bioModelEditorPathwayCommonsPanel.setSelectionManager(selectionManager);
		getBioModelsNetPropertiesPanel().setSelectionManager(selectionManager);
		getApplicationPropertiesPanel().setSelectionManager(selectionManager);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private final static int RIGHT_BOTTOM_TAB_PROPERTIES_INDEX = 0;

@Override
protected void setRightBottomPanelOnSelection(Object[] selections) {
	JComponent bottomComponent = rightBottomEmptyPanel;
	int destComponentIndex = RIGHT_BOTTOM_TAB_PROPERTIES_INDEX;
	boolean bShowBottom = true;
	boolean bShowInDatabaseProperties = false;
	if (selections != null && selections.length == 1) {
		Object singleSelection = selections[0];
		if (singleSelection instanceof ReactionStep) {
			bottomComponent = getReactionPropertiesPanel();
		} else if (singleSelection instanceof SpeciesContext) {
			bottomComponent = getSpeciesPropertiesPanel();
			getSpeciesPropertiesPanel().setModel(bioModel.getModel());
			getSpeciesPropertiesPanel().setSpeciesContext((SpeciesContext) singleSelection);
		} else if (singleSelection instanceof Structure) {
			bottomComponent = getStructurePropertiesPanel();
			getStructurePropertiesPanel().setModel(bioModel.getModel());
			getStructurePropertiesPanel().setStructure((Structure) singleSelection);
		} else if (singleSelection instanceof ModelParameter) {
			bottomComponent = getModelParameterPropertiesPanel();
			getModelParameterPropertiesPanel().setModelParameter((ModelParameter) singleSelection);
		} else if (singleSelection instanceof KineticsParameter) {
			bottomComponent = getReactionPropertiesPanel();
		} else if (singleSelection instanceof SimulationContext) {
			bottomComponent = getApplicationPropertiesPanel();
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
		} else if (singleSelection instanceof BioModel || singleSelection instanceof VCMetaData) {
			bottomComponent = bioModelEditorAnnotationPanel;
		} else if (singleSelection instanceof PathwayData) {
			PathwayData pathwayData = (PathwayData)singleSelection;
			bottomComponent = getBioModelEditorPathwayPanel();
			for (destComponentIndex = 0; destComponentIndex < rightBottomTabbedPane.getComponentCount(); destComponentIndex ++) {
				if (rightBottomTabbedPane.getComponentAt(destComponentIndex) == bottomComponent) {
					break;
				}
			}
			String tabTitle = "Pathway " + pathwayData.getPathway().primaryId();
			if (rightBottomTabbedPane.getComponentCount() == destComponentIndex) {
				rightBottomTabbedPane.addTab(tabTitle, new TabCloseIcon(), bottomComponent);
			} else {
				rightBottomTabbedPane.setTitleAt(destComponentIndex, tabTitle);
			}
			rightSplitPane.setDividerLocation(0.5);
		} else if (singleSelection instanceof Model) {
		} else if (singleSelection instanceof DocumentEditorTreeFolderNode) {
			DocumentEditorTreeFolderClass folderClass = ((DocumentEditorTreeFolderNode)singleSelection).getFolderClass();
			if (folderClass == DocumentEditorTreeFolderClass.SIMULATIONS_NODE) {
				bottomComponent = getSimulationSummaryPanel();
			} else if (folderClass == DocumentEditorTreeFolderClass.APPLICATTIONS_NODE) {
				bottomComponent = getApplicationPropertiesPanel();
			} else if (folderClass == DocumentEditorTreeFolderClass.INITIAL_CONDITIONS_NODE) {
				bottomComponent = getSpeciesContextSpecPanel();
			} else if (folderClass == DocumentEditorTreeFolderClass.APP_REACTIONS_NODE) {
				bottomComponent = getKineticsTypeTemplatePanel();
			} else if (folderClass == DocumentEditorTreeFolderClass.EVENTS_NODE) {
				bottomComponent = getEventPanel();
			} else if (folderClass == DocumentEditorTreeFolderClass.DATA_SYMBOLS_NODE) {
				bottomComponent = getDataSymbolsSpecPanel();
			} else if (folderClass == DocumentEditorTreeFolderClass.STRUCTURES_NODE 
					|| folderClass == DocumentEditorTreeFolderClass.SPECIES_NODE
					|| folderClass == DocumentEditorTreeFolderClass.GLOBAL_PARAMETER_NODE
					|| folderClass == DocumentEditorTreeFolderClass.REACTIONS_NODE) {
			} else {
				bShowBottom = false;
			}
		} else {
			bShowBottom = false;
		}
	}
	if (bShowBottom) {
		if (bShowInDatabaseProperties) {
			for (destComponentIndex = 0; destComponentIndex < rightBottomTabbedPane.getComponentCount(); destComponentIndex ++) {
				if (rightBottomTabbedPane.getTitleAt(destComponentIndex) == DATABASE_PROPERTIES_TAB_TITLE) {
					break;
				}
			}
			if (rightBottomTabbedPane.getComponentCount() == destComponentIndex) {
				rightBottomTabbedPane.addTab(DATABASE_PROPERTIES_TAB_TITLE, new TabCloseIcon(), bottomComponent);
			}
		}
		if (rightSplitPane.getBottomComponent() != rightBottomTabbedPane) {	
			rightSplitPane.setBottomComponent(rightBottomTabbedPane);
			rightSplitPane.setDividerLocation(DEFAULT_DIVIDER_LOCATION);
		}	
		if (rightBottomTabbedPane.getComponentAt(destComponentIndex) != bottomComponent) {
			rightBottomTabbedPane.setComponentAt(destComponentIndex, bottomComponent);
			rightBottomTabbedPane.repaint();
		}
		rightBottomTabbedPane.setSelectedComponent(bottomComponent);
	}
}

/**
 * Method generated to support the promotion of the optimizationService attribute.
 * @param arg1 cbit.vcell.opt.solvers.OptimizationService
 */
public void setOptimizationService(OptimizationService arg1) {
	getoptTestPanel().setOptimizationService(arg1);
}

/**
 * Method generated to support the promotion of the userPreferences attribute.
 * @param arg1 cbit.vcell.client.server.UserPreferences
 */
public void setUserPreferences(UserPreferences arg1) {
	getoptTestPanel().setUserPreferences(arg1);
}

private DataSymbolsPanel getDataSymbolsPanel() {
	if (dataSymbolsPanel == null) {
		try {
			dataSymbolsPanel = new DataSymbolsPanel();
			dataSymbolsPanel.setName("DataSymbolsPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return dataSymbolsPanel;
}

//------------- Right Panel	-----------------------
private GeometrySummaryViewer getGeometrySummaryViewer() {
	if (ivjGeometrySummaryViewer == null) {
		try {
			ivjGeometrySummaryViewer = new GeometrySummaryViewer();
			ivjGeometrySummaryViewer.setName("GeometrySummaryViewer");
			ivjGeometrySummaryViewer.setPreferredSize(new Dimension(500,500));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGeometrySummaryViewer;
}

private InitialConditionsPanel getInitialConditionsPanel() {
	if (initialConditionsPanel == null) {
		try {
			initialConditionsPanel = new InitialConditionsPanel();
			initialConditionsPanel.setName("InitialConditionsPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return initialConditionsPanel;
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

private ReactionSpecsPanel getReactionSpecsPanel() {
	if (reactionSpecsPanel == null) {
		try {
			reactionSpecsPanel = new ReactionSpecsPanel();
			reactionSpecsPanel.setName("ReactionSpecsPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return reactionSpecsPanel;
}

private StructureMappingCartoonPanel getStructureMappingCartoonPanel() {
	if (ivjStructureMappingCartoonPanel == null) {
		try {
			ivjStructureMappingCartoonPanel = new StructureMappingCartoonPanel();
			ivjStructureMappingCartoonPanel.setName("StructureMappingCartoonPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStructureMappingCartoonPanel;
}

private EventsDisplayPanel getEventsDisplayPanel() {
	if (eventsDisplayPanel == null) {
		try {
			eventsDisplayPanel = new EventsDisplayPanel();
			eventsDisplayPanel.setName("EventsDisplayPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return eventsDisplayPanel;
}

private MathematicsPanel getMathematicsPanel() {
	if (mathematicsPanel == null) {
		try {
			mathematicsPanel = new MathematicsPanel();
			mathematicsPanel.setName("MathematicsPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return mathematicsPanel;
}

private ElectricalMembraneMappingPanel getElectricalMembraneMappingPanel() {
	if (ivjElectricalMembraneMappingPanel == null) {
		try {
			ivjElectricalMembraneMappingPanel = new ElectricalMembraneMappingPanel();
			ivjElectricalMembraneMappingPanel.setName("ElectricalMembraneMappingPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjElectricalMembraneMappingPanel;
}

private SimulationContext getSelectedSimulationContext() {
	Object node = documentEditorTree.getLastSelectedPathComponent();;
	if (!(node instanceof BioModelNode)) {
		return null;
	}
	BioModelNode n = (BioModelNode)node;
	while (true) {
		Object userObject = n.getUserObject();
		if (userObject instanceof SimulationContext) {
			return (SimulationContext)userObject;
		}
		TreeNode parent = n.getParent();
		if (parent == null || !(parent instanceof BioModelNode)) {
			return null;
		}
		n = (BioModelNode)parent;
	}
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
	    if (selectedObject instanceof BioModel) {
	    	setRightTopPanel(null, selectedObject, null);
	    } else if (selectedObject instanceof Model) {
	    	setRightTopPanel(null, selectedObject, null);
	    } else if (selectedObject instanceof DocumentEditorTreeFolderNode) { // it's a folder	    	
	    	setRightTopPanel((DocumentEditorTreeFolderNode)selectedObject, null, simulationContext);
	    } else if (selectedObject instanceof SimulationContext){
	    	BioModelNode parentNode = (BioModelNode) selectedNode.getParent();
			Object parentObject =  parentNode.getUserObject();
			DocumentEditorTreeFolderNode parent = (DocumentEditorTreeFolderNode)parentObject;
	    	setRightTopPanel(parent, selectedObject, simulationContext);
	    } else if (selectedObject instanceof VCMetaData || selectedObject instanceof MiriamResource){
	    	setRightTopPanel(null, selectedObject, null);
	    } else {
	        Object leafObject = selectedObject;
			BioModelNode parentNode = (BioModelNode) selectedNode.getParent();
			Object parentObject =  parentNode.getUserObject();
			if (!(parentObject instanceof DocumentEditorTreeFolderNode)) {
				return;
			}
			DocumentEditorTreeFolderNode parent = (DocumentEditorTreeFolderNode)parentObject;
			setRightTopPanel(parent, leafObject, simulationContext);
	    }
	}catch (Exception ex){
		ex.printStackTrace(System.out);
	}
}

private void setRightTopPanel(DocumentEditorTreeFolderNode folderNode, Object leafObject, SimulationContext simulationContext) {
	JComponent newTopPanel = emptyPanel;
	double dividerLocation = DEFAULT_DIVIDER_LOCATION;
	if (folderNode == null) { // could be BioModel or SimulationContext or VCMetaData or MiriamResource,
		if (leafObject instanceof Model) {
			newTopPanel = bioModelEditorModelPanel;
		} else if (leafObject instanceof Model) {
			newTopPanel = bioModelEditorModelPanel;
		} else if (leafObject instanceof BioModel || leafObject instanceof VCMetaData || leafObject instanceof MiriamResource) {
			dividerLocation = 1.0;
			newTopPanel = bioModelPropertiesPanel;
		}
	} else {
		DocumentEditorTreeFolderClass folderClass = folderNode.getFolderClass();
		if (folderClass == DocumentEditorTreeFolderClass.STRUCTURES_NODE 
				|| folderClass == DocumentEditorTreeFolderClass.SPECIES_NODE
				|| folderClass == DocumentEditorTreeFolderClass.GLOBAL_PARAMETER_NODE
				|| folderClass == DocumentEditorTreeFolderClass.REACTIONS_NODE) {
			newTopPanel = bioModelEditorModelPanel;
		} else if (folderClass == DocumentEditorTreeFolderClass.PATHWAY_NODE) {
			newTopPanel = getBioModelEditorPathwayDiagramPanel();
			getBioModelEditorPathwayDiagramPanel().setBioModel(bioModel);
			dividerLocation = 1.0;
		} else if (folderClass == DocumentEditorTreeFolderClass.APPLICATTIONS_NODE) {
			newTopPanel = getBioModelEditorApplicationsPanel();
		} else if (folderClass == DocumentEditorTreeFolderClass.SCRIPTING_NODE) {
			newTopPanel = getScriptingPanel();
			dividerLocation = 1.0;
		} else if (folderClass == DocumentEditorTreeFolderClass.MATHEMATICS_NODE) {
			newTopPanel = getMathematicsPanel();
			getMathematicsPanel().setSimulationContext(simulationContext);
			dividerLocation = 1.0;
		} else if (folderClass == DocumentEditorTreeFolderClass.ANALYSIS_NODE) {
			newTopPanel = getParameterEstimationPanel();
			getParameterEstimationPanel().setSimulationContext(simulationContext);
			dividerLocation = 1.0;
		} else if (folderClass == DocumentEditorTreeFolderClass.GEOMETRY_NODE) {
			newTopPanel = getGeometrySummaryViewer();
			getGeometrySummaryViewer().setGeometryOwner(simulationContext);
			dividerLocation = 1.0;
		} else if(folderClass == DocumentEditorTreeFolderClass.STRUCTURE_MAPPING_NODE) {
			newTopPanel = getStructureMappingCartoonPanel();
			dividerLocation = 1.0;
			getStructureMappingCartoonPanel().setSimulationContext(simulationContext);
		} else if(folderClass == DocumentEditorTreeFolderClass.INITIAL_CONDITIONS_NODE) {
			newTopPanel = getInitialConditionsPanel();
			getInitialConditionsPanel().setSimulationContext(simulationContext);
		} else if(folderClass == DocumentEditorTreeFolderClass.APP_REACTIONS_NODE) {
			newTopPanel = getReactionSpecsPanel();
			getReactionSpecsPanel().setSimulationContext(simulationContext);
		} else if(folderClass == DocumentEditorTreeFolderClass.ELECTRICAL_MAPPING_NODE) {
			newTopPanel = getElectricalMembraneMappingPanel();
			dividerLocation = 1.0;
			getElectricalMembraneMappingPanel().setSimulationContext(simulationContext);
		} else if(folderClass == DocumentEditorTreeFolderClass.EVENTS_NODE) {
			newTopPanel = getEventsDisplayPanel();
			dividerLocation = 0.4;
			getEventsDisplayPanel().setSimulationContext(simulationContext);
		} else if(folderClass == DocumentEditorTreeFolderClass.DATA_SYMBOLS_NODE) {
			newTopPanel = getDataSymbolsPanel();
			getDataSymbolsPanel().setSimulationContext(simulationContext);
			dividerLocation = 0.4;
		} else if(folderClass == DocumentEditorTreeFolderClass.MICROSCOPE_MEASUREMENT_NODE) {
			newTopPanel = getMicroscopeMeasurementPanel();
			getMicroscopeMeasurementPanel().setSimulationContext(simulationContext);
			dividerLocation = 1.0;
		} else if(folderClass == DocumentEditorTreeFolderClass.SIMULATIONS_NODE || folderClass == DocumentEditorTreeFolderClass.OUTPUT_FUNCTIONS_NODE) {
			ApplicationComponents applicationComponents = bioModelWindowManager.getApplicationComponents(simulationContext);
			SimulationWorkspace simulationWorkspace = applicationComponents.getSimulationWorkspace();
			if(folderClass == DocumentEditorTreeFolderClass.SIMULATIONS_NODE) {
				newTopPanel = getSimulationListPanel();
				dividerLocation = 0.4;
				getSimulationListPanel().setSimulationWorkspace(simulationWorkspace);
			} else if(folderClass == DocumentEditorTreeFolderClass.OUTPUT_FUNCTIONS_NODE) {
				dividerLocation = 1.0;
				newTopPanel = getOutputFunctionsPanel();
				getOutputFunctionsPanel().setSimulationWorkspace(simulationWorkspace);
			}
		}
	}
	Component rightTopComponent = rightSplitPane.getTopComponent();
	if (rightTopComponent != newTopPanel) {
		rightSplitPane.setTopComponent(newTopPanel);
	}
	if (dividerLocation < 1.0) {
		rightSplitPane.setBottomComponent(rightBottomTabbedPane);
		rightSplitPane.setDividerLocation(dividerLocation);
	} else {
		rightSplitPane.setBottomComponent(null);
	}
}

private OutputFunctionsPanel getOutputFunctionsPanel() {
	if (outputFunctionsPanel == null) {
		try {
			outputFunctionsPanel  = new OutputFunctionsPanel();
			outputFunctionsPanel.setName("OutputFunctionsPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return outputFunctionsPanel;
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
	bioModelEditorTreeCellRenderer.setBioModel(bioModel);
	getBioModelEditorApplicationsPanel().setBioModel(bioModel);
	getScriptingPanel().setBioModel(bioModel);	
	getBioModelEditorPathwayPanel().setBioModel(bioModel);
	getBioModelEditorPathwayDiagramPanel().setBioModel(bioModel);
	bioModelPropertiesPanel.setBioModel(bioModel);
	bioModelEditorTreeModel.setBioModel(bioModel);
	bioModelEditorAnnotationPanel.setBioModel(bioModel);
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
	getGeometrySummaryViewer().addActionListener(bioModelWindowManager);
	getMathematicsPanel().addActionListener(bioModelWindowManager);
	bioModelsNetPanel.setDocumentWindowManager(bioModelWindowManager);
	getBioModelsNetPropertiesPanel().setDocumentWindowManager(bioModelWindowManager);
	bioModelPropertiesPanel.setBioModelWindowManager(bioModelWindowManager);
	
	DatabaseWindowManager dbWindowManager = new DatabaseWindowManager(databaseWindowPanel, bioModelWindowManager.getRequestManager());
	databaseWindowPanel.setDatabaseWindowManager(dbWindowManager);
	DocumentManager documentManager = bioModelWindowManager.getRequestManager().getDocumentManager();
	databaseWindowPanel.setDocumentManager(documentManager);
	bioModelEditorModelPanel.setDocumentManager(documentManager);
	
	bioModelMetaDataPanel.setDocumentManager(documentManager);
	mathModelMetaDataPanel.setDocumentManager(documentManager);
	geometryMetaDataPanel.setDocumentManager(documentManager);
	
	getoptTestPanel().setOptimizationService(bioModelWindowManager.getOptimizationService());
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		JFrame frame = new javax.swing.JFrame();
		BioModelEditor aBioModelEditor = new BioModelEditor();
		frame.setContentPane(aBioModelEditor);
		frame.setSize(aBioModelEditor.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.pack();
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}

}