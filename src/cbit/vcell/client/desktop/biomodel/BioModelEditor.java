package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;

import cbit.image.ImageException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.biomodel.BioModelEditorTreeModel.BioModelEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.BioModelEditorTreeModel.BioModelEditorTreeFolderNode;
import cbit.vcell.client.desktop.geometry.GeometrySummaryViewer;
import cbit.vcell.client.desktop.simulation.OutputFunctionsPanel;
import cbit.vcell.client.desktop.simulation.SimulationListPanel;
import cbit.vcell.client.desktop.simulation.SimulationWorkspace;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.VFrapConstants;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.gui.DataSymbolsPanel;
import cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel;
import cbit.vcell.mapping.gui.InitialConditionsPanel;
import cbit.vcell.mapping.gui.ReactionSpecsPanel;
import cbit.vcell.mapping.gui.StructureMappingCartoonPanel;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.gui.FeatureEditorPanel;
import cbit.vcell.model.gui.FluxReactionPanel;
import cbit.vcell.model.gui.MembraneEditorPanel;
import cbit.vcell.model.gui.ModelParameterPanel;
import cbit.vcell.model.gui.SimpleReactionPanel;
import cbit.vcell.model.gui.SpeciesEditorPanel;
import cbit.vcell.modelopt.gui.OptTestPanel;
import cbit.vcell.opt.solvers.OptimizationService;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.xml.gui.MiriamTreeModel.LinkNode;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 2:55:18 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class BioModelEditor extends JPanel {
	private static final String PROPERTY_NAME_DOCUMENT_MANAGER = "documentManager";
	public static final String PROPERTY_NAME_SELECTED_VERSIONABLE = "selectedVersionable";
	private static final String MENU_TEXT_SPATIAL_APPLICATION = "Spatial Application";
	private static final String MENU_TEXT_NON_SPATIAL_APPLICATION = "Non-Spatial Application";
	private static final String MENU_TEXT_DETERMINISTIC_APPLICATION = "Deterministic Application";
	private static final String MENU_TEXT_STOCHASTIC_APPLICATION = "Stochastic Application";
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private BioModelWindowManager bioModelWindowManager = null;
	private BioModel fieldBioModel = new BioModel(null);
	private DocumentManager fieldDocumentManager = null;
	
	private JTree bioModelEditorTree = null;
	private BioModelEditorTreeCellRenderer bioModelEditorTreeCellRenderer = null;
	private JSplitPane splitPane = null; 
	private javax.swing.JScrollPane treePanel = null;
	
	private OptTestPanel ivjoptTestPanel = null;
	private AnalysisPanel ivjParameterEstimationPanel = null;
	private SimulationListPanel ivjSimulationListPanel = null;
	private GeometrySummaryViewer ivjGeometrySummaryViewer = null;
	private StructureMappingCartoonPanel ivjStructureMappingCartoonPanel = null;
	private OutputFunctionsPanel outputFunctionsPanel = null;
	private InitialConditionsPanel initialConditionsPanel = null;
	private DataSymbolsPanel dataSymbolsPanel = null;
	private ModelParameterPanel modelParameterPanel = null;
	private ReactionSpecsPanel reactionSpecsPanel = null;
	private ElectricalMembraneMappingPanel ivjElectricalMembraneMappingPanel = null;
	private EventsDisplayPanel eventsDisplayPanel = null;
	private MathematicsPanel mathematicsPanel = null;
	private SimpleReactionPanel simpleReactionPanel = null;
	private FluxReactionPanel fluxReactionPanel = null;
	private SpeciesEditorPanel speciesEditorPanel = null;
	private FeatureEditorPanel featureEditorPanel = null;
	private MembraneEditorPanel membraneEditorPanel = null;
	private BioModelEditorSpeciesPanel bioModelEditorSpeciesPanel = null;
	private BioModelEditorStructurePanel bioModelEditorStructurePanel = null;
	private BioModelEditorReactionPanel bioModelEditorReactionPanel = null;
	
	private BioModelEditorTreeModel bioModelEditorTreeModel = null;
	private JPanel emptyPanel = new JPanel();
	
	// popup menu items
	private JPopupMenu addEventPopupMenu = null;
	private JPopupMenu addDataPopupMenu = null;
	private JPopupMenu deleteEventPopupMenu = null;
	private JMenuItem menuItemAddEvent = null;
	private JMenuItem menuItemAddGenericData = null;
	private JMenuItem menuItemAddVFrapData = null;
	private JMenuItem menuItemDeleteEvent = null;

	// application popup menu items
	private JPopupMenu ivjAppsPopupMenu = null;
	private JPopupMenu ivjAppPopupMenu = null;
	
	private JMenu ivjJMenuAppCopyAs = null;
	private JMenuItem menuItemAppNonSpatialCopyStochastic = null;
	private JMenuItem menuItemNonSpatialCopyDeterministic = null;
	
	private JMenu menuAppSpatialCopyAsNonSpatial = null;
	private JMenuItem menuItemAppSpatialCopyAsNonSpatialStochastic = null;
	private JMenuItem menuItemAppSpatialCopyAsNonSpatialDeterministic = null;
	private JMenu menuAppSpatialCopyAsSpatial = null;
	private JMenuItem menuItemAppSpatialCopyAsSpatialStochastic = null;
	private JMenuItem menuItemAppSpatialCopyAsSpatialDeterministic = null;
	
	private JMenuItem ivjJMenuItemAppDelete = null;
	private JMenuItem appNewStochApp = null;
	private JMenuItem appNewDeterministicApp = null;
	private JSeparator ivjJSeparator1 = null;
//	private JSeparator ivjJSeparator2 = null;
	private JMenuItem ivjJMenuItemAppRename = null;
	private JMenuItem ivjJMenuItemAppAnnotation = null;
	private JMenuItem ivjJMenuItemAppCopy = null;
	
	private JMenu ivjJMenuAppNew = null;
	
	private Hashtable<SimulationContext, SimulationWorkspace> simWorkspaceHashTable = new Hashtable<SimulationContext, SimulationWorkspace>();
	private AnnotationEditorPanel ivjAnnotationEditorPanel = null;
	
	public static class SelectionEvent {
		private Object selectedContainer;
		private Object selectedObject;
		
		public SelectionEvent(Object selectedContainer, Object selectedObject) {
			super();
			this.selectedContainer = selectedContainer;
			this.selectedObject = selectedObject;
		}

		public final Object getSelectedContainer() {
			return selectedContainer;
		}

		public final Object getSelectedObject() {
			return selectedObject;
		}		
	}

	private class AnnotationEditorPanel extends JPanel {
		private JTextArea textArea = null;
		private JButton applyButton = new JButton("Apply");
		private JButton revertButton = new JButton("Revert");
		public AnnotationEditorPanel() {
			JPanel p = new JPanel();
			
			textArea = new JTextArea("", 8, 60);
			textArea.setRows(10);
			textArea.setEditable(true);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);

			p.setLayout(new BorderLayout());
			JPanel panel = new JPanel();
			panel.add(new JScrollPane(textArea));
			p.add(panel, BorderLayout.CENTER);
			
			panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
			panel.add(applyButton);	
			panel.add(revertButton);			
			p.add(panel, BorderLayout.SOUTH);
			
			add(p);
			
			applyButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					getBioModel().getVCMetaData().setFreeTextAnnotation(getBioModel(), textArea.getText());					
				}
			});
			
			revertButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					setText(getBioModel().getVCMetaData().getFreeTextAnnotation(getBioModel()));					
				}
			});
		}
		
		public void setText(String annot) {
			textArea.setText(annot);
		}
	}
	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.TreeSelectionListener, MouseListener {		
		private static final String PROPERTY_NAME_BIO_MODEL = "bioModel";

		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getJMenuItemAppDelete()
					|| e.getSource() == appNewStochApp
					|| e.getSource() == appNewDeterministicApp
					|| e.getSource() == getJMenuItemAppRename()
					|| e.getSource() == getJMenuItemAppCopy()
					|| e.getSource() == menuItemAppSpatialCopyAsNonSpatialDeterministic
					|| e.getSource() == menuItemAppSpatialCopyAsNonSpatialStochastic
					|| e.getSource() == menuItemAppSpatialCopyAsSpatialDeterministic
					|| e.getSource() == menuItemAppSpatialCopyAsSpatialStochastic) {
				applicationMenuItem_ActionPerformed(e)	;	
			} else if (e.getSource() == getJMenuItemAppAnnotation()) {
			}
//			if (e.getSource() == BioModelEditor.this.getCopyMenuItem()) 
//				connEtoC3(e);
//			if (e.getSource() == BioModelEditor.this.getOpenAppMenuItem()) 
//				connEtoC4(e);
//			if (e.getSource() == BioModelEditor.this.getDeleteMenuItem()) 
//				connEtoC5(e);
//			if (e.getSource() == BioModelEditor.this.getRenameMenuItem()) 
//				connEtoC6(e);
//			if (e.getSource() == BioModelEditor.this.getBioModelTreePanel1()) 
//				connEtoC1(e);
//			if (e.getSource() == BioModelEditor.this.getJMenuItemNonStochApp()) 
//				connEtoC7(e);
//			if (e.getSource() == BioModelEditor.this.getJMenuItemStochApp()) 
//				connEtoC2(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditor.this && (evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL))) 
				onPropertyChange_BioModel();
//			if (evt.getSource() == BioModelEditor.this && (evt.getPropertyName().equals("bioModel"))) 
//				connPtoP1SetTarget();
//			if (evt.getSource() == BioModelEditor.this.getCartoonEditorPanel1() && (evt.getPropertyName().equals("bioModel"))) 
//				connPtoP1SetSource();
			if (evt.getSource() == BioModelEditor.this && (evt.getPropertyName().equals(PROPERTY_NAME_DOCUMENT_MANAGER))) 
				getBioModelEditorStructurePanel().setDocumentManager(getDocumentManager());
//			if (evt.getSource() == BioModelEditor.this.getCartoonEditorPanel1() && (evt.getPropertyName().equals("documentManager"))) 
//				connPtoP2SetSource();
//			if (evt.getSource() == getBioModelTreePanel1() && (evt.getPropertyName().equals(BioModelTreePanel.PROPERTY_NAME_SELECTED_VERSIONABLE))) {
//				updateMenuOnSelectionChange();
//			}
			if (evt.getPropertyName().equals(InitialConditionsPanel.PARAMETER_NAME_SELECTED_SPECIES_CONTEXT)
					|| evt.getPropertyName().equals(ReactionSpecsPanel.PARAMETER_NAME_SELECTED_REACTION_STEP)
					|| evt.getPropertyName().equals(ModelParameterPanel.PROPERTY_NAME_SELECTED_MODEL_PARAMETER)
					|| evt.getPropertyName().equals(OutputFunctionsPanel.PROPERTY_SELECTED_OUTPUT_FUNCTION)) {
					if (evt.getNewValue() instanceof SelectionEvent) {
						getBioModelEditorTreeModel().setSelectedValue((SelectionEvent)evt.getNewValue());
					}
				}
		};
		
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == getBioModelEditorTree()) {
				if (SwingUtilities.isRightMouseButton(e)) {	// right click		
					Object node = getBioModelEditorTree().getLastSelectedPathComponent();;
					if (node == null || !(node instanceof BioModelNode)) {
						return;
					}
					Object selectedObject = ((BioModelNode)node).getUserObject();
					Point mousePoint = e.getPoint();
					// offer "New" popup menu only for DataSymbols folder
					if (selectedObject instanceof BioModelEditorTreeFolderNode) {
						BioModelEditorTreeFolderNode selectedFolder = (BioModelEditorTreeFolderNode)selectedObject;
						switch(selectedFolder.getFolderClass()) {
						case APPLICATTIONS_NODE:	// Data Symbols
							getAppsPopupMenu().show(getBioModelEditorTree(), mousePoint.x, mousePoint.y);
							break;
						case DATA_SYMBOLS_NODE:	// Data Symbols
							getAddDataPopupMenu().show(getBioModelEditorTree(), mousePoint.x, mousePoint.y);
							break;
						}
					} else {
						SimulationContext simContext = getSelectedSimulationContext();
						if (simContext == null) {
							return;
						}
						if (simContext == selectedObject) {
							getAppPopupMenu().show(getBioModelEditorTree(), mousePoint.x, mousePoint.y);
						} else {
							// deactivate pop-up menu if simulationContext is spatial or stochastic
							int dimension = simContext.getGeometry().getDimension();
							if (dimension == 0 && !simContext.isStoch()) {
								if (selectedObject instanceof BioModelEditorTreeFolderNode) {
									BioModelEditorTreeFolderNode stfn = (BioModelEditorTreeFolderNode)selectedObject;
									if (stfn.getFolderClass() == BioModelEditorTreeFolderClass.EVENTS_NODE) { // "Events"
										getAddEventPopupMenu().show(getBioModelEditorTree(), mousePoint.x, mousePoint.y);
									}
			
								} else if (selectedObject instanceof BioEvent) {
									getDeleteEventPopupMenu().show(getBioModelEditorTree(), mousePoint.x, mousePoint.y);
								}
							}
						}
					}
				} else if (e.getClickCount() == 2) {
					Object node = getBioModelEditorTree().getLastSelectedPathComponent();
					if (node instanceof LinkNode) {
						String link = ((LinkNode)node).getLink();
						if (link != null) {
							DialogUtils.browserLauncher(getBioModelEditorTree(), link, "failed to launch", false);
						}
					}
				}
			}
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}	
		
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == getBioModelEditorTree())
				treeValueChanged();
		}
	};

/**
 * BioModelEditor constructor comment.
 */
public BioModelEditor() {
	super();
	initialize();
}

public DocumentManager getDocumentManager() {
	return fieldDocumentManager;
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
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

private BioModelEditorSpeciesPanel getBioModelEditorSpeciesPanel() {
	if (bioModelEditorSpeciesPanel == null) {
		try {
			bioModelEditorSpeciesPanel = new BioModelEditorSpeciesPanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return bioModelEditorSpeciesPanel;
}

private BioModelEditorReactionPanel getBioModelEditorReactionPanel() {
	if (bioModelEditorReactionPanel == null) {
		try {
			bioModelEditorReactionPanel = new BioModelEditorReactionPanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return bioModelEditorReactionPanel;
}

private BioModelEditorStructurePanel getBioModelEditorStructurePanel() {
	if (bioModelEditorStructurePanel == null) {
		try {
			bioModelEditorStructurePanel = new BioModelEditorStructurePanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return bioModelEditorStructurePanel;
}

private AnnotationEditorPanel getAnnotationEditorPanel() {
	if (ivjAnnotationEditorPanel  == null) {
		try {
			ivjAnnotationEditorPanel = new AnnotationEditorPanel();
			ivjAnnotationEditorPanel.setName("AnnotationEditorPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnnotationEditorPanel;
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


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
//	getViewModifyGeometryButton().addActionListener(ivjEventHandler);
	addPropertyChangeListener(ivjEventHandler);
	
	getSimulationListPanel().addPropertyChangeListener(ivjEventHandler);
	getInitialConditionsPanel().addPropertyChangeListener(ivjEventHandler);
	getReactionSpecsPanel().addPropertyChangeListener(ivjEventHandler);
	getModelParameterPanel().addPropertyChangeListener(ivjEventHandler);
		
	getGeometrySummaryViewer().addActionListener(ivjEventHandler);
	getBioModelEditorTree().addTreeSelectionListener(ivjEventHandler);
	getBioModelEditorTree().addMouseListener(ivjEventHandler);
	getBioModelEditorTree().addTreeExpansionListener(getBioModelEditorTreeModel());
	getEventsDisplayPanel().addPropertyChangeListener(ivjEventHandler);
	getMenuItemAddEvent().addActionListener(ivjEventHandler);
	getMenuItemDeleteEvent().addActionListener(ivjEventHandler);
	getMenuItemAddGenericData().addActionListener(ivjEventHandler);
	getMenuItemAddVFrapData().addActionListener(ivjEventHandler);
	
	getJMenuItemAppDelete().addActionListener(ivjEventHandler);
	getJMenuAppNew().addActionListener(ivjEventHandler);
	getJMenuItemAppRename().addActionListener(ivjEventHandler);
	getJMenuAppCopyAs().addActionListener(ivjEventHandler);
	getJMenuItemAppCopy().addActionListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setName("ApplicationEditor");
		setLayout(new BorderLayout());		
		add(getSplitPane(), BorderLayout.CENTER);
		
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private JSplitPane getSplitPane() {
	if (splitPane == null) {
		splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		
		splitPane.setLeftComponent(getTreePanel());
		splitPane.setRightComponent(getBioModelEditorStructurePanel());
	}
	return splitPane;
}

private javax.swing.JScrollPane getTreePanel() {
	if (treePanel == null) {
		try {
			treePanel = new javax.swing.JScrollPane();
			treePanel.setName("LeftTreePanel");
			Dimension dim = new Dimension(200, 20);
			treePanel.setPreferredSize(dim);
			treePanel.setViewportView(getBioModelEditorTree());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return treePanel;
}

/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 2:27:03 PM)
 * @param simOwner cbit.vcell.document.SimulationOwner
 */
void resetSimContext(SimulationContext simContext) {
//	/* most likely we got the same thing back (e.g. during document reset after save), so keep current selection in simulation panel */
//	// check whether it looks like same old simcontext; if so, save current selection list
//	int[] selections = null;
//	if (getSimulationContext() != null && simContext != null) {
//		Simulation[] oldValue = getSimulationWorkspace().getSimulationOwner().getSimulations();
//		Simulation[] simulations = simContext.getSimulations();
//		if (oldValue != null && simulations != null && oldValue.length == simulations.length) {
//			boolean sameNames = true;
//			for (int i = 0; i < oldValue.length; i++){
//				if(!oldValue[i].getName().equals(simulations[i].getName())) {
//					sameNames = false;
//					break;
//				}
//			}
//			if (sameNames) {
//				selections = getSimulationListPanel().getSelectedRows();
//			}
//		}
//	}
//	// reset the thing
//	getSimulationWorkspace().setSimulationOwner(simContext);
//	// now set the selection back if appropriate
//	if (selections != null) {
//		getSimulationListPanel().setSelectedRows(selections);
//	}
}

/**
 * Method generated to support the promotion of the optimizationService attribute.
 * @param arg1 cbit.vcell.opt.solvers.OptimizationService
 */
public void setOptimizationService(OptimizationService arg1) {
	getoptTestPanel().setOptimizationService(arg1);
}


/**
 * Sets the simulationWorkspace property (cbit.vcell.client.desktop.simulation.SimulationWorkspace) value.
 * @param simulationWorkspace The new value for the property.
 * @see #getSimulationWorkspace
 */
public void setSimulationWorkspace(SimulationWorkspace simulationWorkspace) {
//	SimulationWorkspace oldValue = fieldSimulationWorkspace;
//	fieldSimulationWorkspace = simulationWorkspace;
//	firePropertyChange(PROPERTY_NAME_SIMULATION_WORKSPACE, oldValue, simulationWorkspace);
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

private ModelParameterPanel getModelParameterPanel() {
	if (modelParameterPanel == null) {
		try {
			modelParameterPanel = new ModelParameterPanel();
			modelParameterPanel.setName("ModelParameterPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return modelParameterPanel;
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

private BioModelEditorTreeCellRenderer getBioModelEditorTreeCellRender() {
	if (bioModelEditorTreeCellRenderer == null) {
		try {
			bioModelEditorTreeCellRenderer = new BioModelEditorTreeCellRenderer(bioModelEditorTree);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return bioModelEditorTreeCellRenderer;
}

private javax.swing.JTree getBioModelEditorTree() {
	if (bioModelEditorTree == null) {
		try {
			bioModelEditorTree = new javax.swing.JTree();
			bioModelEditorTree.setName("bioModelEditorTree");
			ToolTipManager.sharedInstance().registerComponent(bioModelEditorTree);
			bioModelEditorTree.setCellRenderer(getBioModelEditorTreeCellRender());
			bioModelEditorTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			bioModelEditorTree.setModel(getBioModelEditorTreeModel());
			bioModelEditorTree.setRowHeight(bioModelEditorTree.getRowHeight() + 2);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return bioModelEditorTree;
}

private BioModelEditorTreeModel getBioModelEditorTreeModel() {
	if (bioModelEditorTreeModel == null) {
		try {
			bioModelEditorTreeModel = new BioModelEditorTreeModel(getBioModelEditorTree());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return bioModelEditorTreeModel;
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
			eventsDisplayPanel = new EventsDisplayPanel(true);
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
	Object node = getBioModelEditorTree().getLastSelectedPathComponent();;
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

private void treeValueChanged() {
	try {
		Object node = getBioModelEditorTree().getLastSelectedPathComponent();;
		if (node == null || !(node instanceof BioModelNode)) {
			return;
		}
		BioModelNode selectedNode = (BioModelNode)node;
	    Object userObject = selectedNode.getUserObject();
	    SimulationContext simulationContext = getSelectedSimulationContext();
	    if (userObject instanceof BioModel) {
	    	setRightPanel(null, null, null);
	    } else if (userObject instanceof BioModelEditorTreeFolderNode) { // it's a folder	    	
	    	setRightPanel((BioModelEditorTreeFolderNode)userObject, null, simulationContext);
	    } else if (userObject instanceof SimulationContext){
	    	setRightPanel(null, null, null);
	    } else if (userObject instanceof VCMetaData || userObject instanceof MiriamResource){
	    	setRightPanel(null, userObject, null);
	    } else {
	        Object leaf = userObject;
			BioModelNode parentNode = (BioModelNode) selectedNode.getParent();
			userObject =  parentNode.getUserObject();
			BioModelEditorTreeFolderNode parent = (BioModelEditorTreeFolderNode)userObject;
			setRightPanel(parent, leaf, simulationContext);
	    }
	}catch (Exception ex){
		ex.printStackTrace(System.out);
	}
}

private void setRightPanel(BioModelEditorTreeFolderNode folderNode, Object leafObject, SimulationContext simulationContext) {
	JComponent rightPanel = emptyPanel;
	if (folderNode == null) { // could be BioModel or SimulationContext or VCMetaData or MiriamResource,
		if (leafObject instanceof VCMetaData || leafObject instanceof MiriamResource) {
			rightPanel = getAnnotationEditorPanel();
			if (getBioModel() != null) {
				String annot =  getBioModel().getVCMetaData().getFreeTextAnnotation(getBioModel());
				getAnnotationEditorPanel().setText(annot);
			}
		}
	} else {
		BioModelEditorTreeFolderClass folderClass = folderNode.getFolderClass();
		if (folderClass == BioModelEditorTreeFolderClass.STRUCTURES_NODE) {
			if (leafObject == null) {
				rightPanel = getBioModelEditorStructurePanel();
			} else {
				if (leafObject instanceof Feature) {
					rightPanel = getFeatureEditorPanel();
					getFeatureEditorPanel().setFeature((Feature)leafObject);
				} else {
					rightPanel = getMembraneEditorPanel();
					getMembraneEditorPanel().setMembrane((Membrane)leafObject);					
				}
			}
		} else if (folderClass == BioModelEditorTreeFolderClass.SPECIES_NODE) {
			if (leafObject == null) {
				rightPanel = getBioModelEditorSpeciesPanel();
			} else {
				rightPanel = getSpeciesEditorPanel();
				getSpeciesEditorPanel().setModel(getBioModel().getModel());
				getSpeciesEditorPanel().setSpeciesContext((SpeciesContext)leafObject);
			}
		} else if (folderClass == BioModelEditorTreeFolderClass.REACTIONS_NODE) {
			if (leafObject == null) {
				rightPanel = getBioModelEditorReactionPanel();
			} else if (leafObject instanceof SimpleReaction) {
				rightPanel = getSimpleReactionPanel();
				getSimpleReactionPanel().setSimpleReaction((SimpleReaction)leafObject);
			} else if (leafObject instanceof FluxReaction) {
				rightPanel = getFluxReactionPanel();
				getFluxReactionPanel().setFluxReaction((FluxReaction)leafObject);
			}
		} else if(folderClass == BioModelEditorTreeFolderClass.GLOBAL_PARAMETER_NODE) {
			rightPanel = getModelParameterPanel();
			getModelParameterPanel().setModel(getBioModel().getModel());
			getModelParameterPanel().setScrollPaneTableCurrentRow((ModelParameter)leafObject);	// notify right panel about selection change
		} else if (folderClass == BioModelEditorTreeFolderClass.MATHEMATICS_NODE) {
			rightPanel = getMathematicsPanel();
			getMathematicsPanel().setSimulationContext(simulationContext);
		} else if (folderClass == BioModelEditorTreeFolderClass.ANALYSIS_NODE) {
			rightPanel = getParameterEstimationPanel();
		} else if (folderClass == BioModelEditorTreeFolderClass.GEOMETRY_NODE) {
			rightPanel = getGeometrySummaryViewer();
			getGeometrySummaryViewer().setGeometry(simulationContext.getGeometry());
		} else if(folderClass == BioModelEditorTreeFolderClass.STRUCTURE_MAPPING_NODE) {
			rightPanel = getStructureMappingCartoonPanel();
			getStructureMappingCartoonPanel().setSimulationContext(simulationContext);
		} else if(folderClass == BioModelEditorTreeFolderClass.INITIAL_CONDITIONS_NODE) {
			rightPanel = getInitialConditionsPanel();
			getInitialConditionsPanel().setSimulationContext(simulationContext);
			getInitialConditionsPanel().setScrollPaneTableCurrentRow((SpeciesContext)leafObject);	// notify right panel about selection change
		} else if(folderClass == BioModelEditorTreeFolderClass.APP_REACTIONS_NODE) {
			rightPanel = getReactionSpecsPanel();
			getReactionSpecsPanel().setSimulationContext(simulationContext);
			getReactionSpecsPanel().setScrollPaneTableCurrentRow((ReactionStep)leafObject);	// notify right panel about selection change
		} else if(folderClass == BioModelEditorTreeFolderClass.ELECTRICAL_MAPPING_NODE) {
			rightPanel = getElectricalMembraneMappingPanel();
			getElectricalMembraneMappingPanel().setSimulationContext(simulationContext);
		} else if(folderClass == BioModelEditorTreeFolderClass.EVENTS_NODE) {
			rightPanel = getReactionSpecsPanel();
			getEventsDisplayPanel().setSimulationContext(simulationContext);
			getEventsDisplayPanel().setScrollPaneTableCurrentRow((BioEvent)leafObject);	// notify right panel about selection change
		} else if(folderClass == BioModelEditorTreeFolderClass.DATA_SYMBOLS_NODE) {
			rightPanel = getDataSymbolsPanel();
			getDataSymbolsPanel().setSimulationContext(simulationContext);
			getDataSymbolsPanel().setScrollPaneTableCurrentRow((DataSymbol)leafObject);	// notify right panel about selection change
		} else if(folderClass == BioModelEditorTreeFolderClass.SIMULATIONS_NODE) {
			rightPanel = getSimulationListPanel();
			if (simWorkspaceHashTable.get(simulationContext) == null) {
				simWorkspaceHashTable.put(simulationContext, new SimulationWorkspace(getBioModelWindowManager(), simulationContext));
			}
			getSimulationListPanel().setSimulationWorkspace(simWorkspaceHashTable.get(simulationContext));
		} else if(folderClass == BioModelEditorTreeFolderClass.OUTPUT_FUNCTIONS_NODE) {
			rightPanel = getOutputFunctionsPanel();
			if (simWorkspaceHashTable.get(simulationContext) == null) {
				simWorkspaceHashTable.put(simulationContext, new SimulationWorkspace(getBioModelWindowManager(), simulationContext));
			}
			getOutputFunctionsPanel().setSimulationWorkspace(simWorkspaceHashTable.get(simulationContext));
		}
	}
	if(splitPane.getRightComponent() != rightPanel) {
		splitPane.setRightComponent(rightPanel);
	}
	splitPane.setDividerLocation(220);
}

private OutputFunctionsPanel getOutputFunctionsPanel() {
	if (outputFunctionsPanel == null) {
		try {
			outputFunctionsPanel  = new OutputFunctionsPanel();
			outputFunctionsPanel.setName("ObservablesPanel");
			addPropertyChangeListener(ivjEventHandler);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return outputFunctionsPanel;
}

private JMenuItem getMenuItemAddEvent() {
	if (menuItemAddEvent == null) {
		try {
			menuItemAddEvent = new javax.swing.JMenuItem();
			menuItemAddEvent.setName("JMenuItemAddEvent");
			menuItemAddEvent.setMnemonic('a');
			menuItemAddEvent.setText("Add Event");
			menuItemAddEvent.setActionCommand(GuiConstants.ACTIONCMD_ADD_EVENT);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return menuItemAddEvent;
}

private JPopupMenu getAddEventPopupMenu() {
	if (addEventPopupMenu == null) {
		try {
			addEventPopupMenu = new javax.swing.JPopupMenu();
			addEventPopupMenu.setName("EventPopupMenu");
			addEventPopupMenu.add(getMenuItemAddEvent());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return addEventPopupMenu;
}

private JPopupMenu getDeleteEventPopupMenu() {
	if (deleteEventPopupMenu == null) {
		try {
			deleteEventPopupMenu = new javax.swing.JPopupMenu();
			deleteEventPopupMenu.add(getMenuItemDeleteEvent());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return deleteEventPopupMenu;
}

private JMenuItem getMenuItemDeleteEvent() {
	if (menuItemDeleteEvent == null) {
		try {
			menuItemDeleteEvent = new javax.swing.JMenuItem();
			menuItemDeleteEvent.setName("JMenuItemAddEvent");
			menuItemDeleteEvent.setMnemonic('a');
			menuItemDeleteEvent.setText("Delete");
			menuItemDeleteEvent.setActionCommand(GuiConstants.ACTIONCMD_ADD_EVENT);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return menuItemDeleteEvent;
}

private void addEvent() {
	SimulationContext simContext = getSelectedSimulationContext();
	if (simContext == null) {
		return;
	}
	String eventName = simContext.getFreeEventName();
	try {
		BioEvent bioEvent = new BioEvent(eventName, simContext);
		simContext.addBioEvent(bioEvent);
		getEventsDisplayPanel().selectEvent(bioEvent);
	} catch (PropertyVetoException e) {
		e.printStackTrace(System.out);
		DialogUtils.showErrorDialog(this, "Error adding Event : " + e.getMessage());
	}
}

public void deleteEvent() {
	Object node = getBioModelEditorTree().getLastSelectedPathComponent();;
	if (node == null || !(node instanceof BioModelNode)) {
		return;
	}
	Object selectedObject = ((BioModelNode)node).getUserObject();
	if (selectedObject instanceof BioEvent) {
		BioEvent bioEvent = (BioEvent)selectedObject;
		SimulationContext simContext = getSelectedSimulationContext();
		if (simContext == null) {
			return;
		}
		try {
			simContext.removeBioEvent(bioEvent);
		} catch (PropertyVetoException ex) {
			ex.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}
	}		
}

private JPopupMenu getAddDataPopupMenu() {
	if (addDataPopupMenu == null) {
		try {
			addDataPopupMenu = new javax.swing.JPopupMenu();
			addDataPopupMenu.setName("DataSymbolPopupMenu");
			addDataPopupMenu.add(getMenuItemAddGenericData());
			addDataPopupMenu.add(getMenuItemAddVFrapData());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return addDataPopupMenu;
}

private JMenuItem getMenuItemAddGenericData() {
	if (menuItemAddGenericData == null) {
		try {
			menuItemAddGenericData = new javax.swing.JMenuItem();
			menuItemAddGenericData.setName("JMenuItemAddData");
			menuItemAddGenericData.setMnemonic('d');
			menuItemAddGenericData.setText(VFrapConstants.ADD_VFRAP_DATASET_MENU);
			menuItemAddGenericData.setEnabled(true);
			menuItemAddGenericData.setActionCommand(GuiConstants.ACTIONCMD_ADD_GENERIC_DATA);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return menuItemAddGenericData;
}
private JMenuItem getMenuItemAddVFrapData() {
	if (menuItemAddVFrapData == null) {
		try {
			menuItemAddVFrapData = new javax.swing.JMenuItem();
			menuItemAddVFrapData.setName("JMenuItemAddVFrapData");
			menuItemAddVFrapData.setMnemonic('v');
			menuItemAddVFrapData.setText(VFrapConstants.ADD_VFRAP_SPECIALS_MENU);
			menuItemAddVFrapData.setEnabled(true);
			menuItemAddVFrapData.setActionCommand(GuiConstants.ACTIONCMD_ADD_VFRAP_DATA);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return menuItemAddVFrapData;
}

public BioModelWindowManager getBioModelWindowManager() {
	return bioModelWindowManager;
}

private BioModel getBioModel() {
	return fieldBioModel;
}

/**
 * Comment
 */
private void newApplication(java.awt.event.ActionEvent event) {
	boolean isStoch = false;
	if (event.getActionCommand().equals(GuiConstants.ACTIONCMD_CREATE_STOCHASTIC_APPLICATION))
	{
		isStoch = true;
		String message = getBioModel().getModel().isValidForStochApp();
		if(!message.equals(""))
		{
			PopupGenerator.showErrorDialog(this, "Error creating stochastic application:\n" + message);
			return;
		}
	}
	
	try {
		String newApplicationName = null;
		try{
			newApplicationName = PopupGenerator.showInputDialog(getBioModelWindowManager(), "Name for the new application:");
		}catch(UserCancelException e){
			return;
		}
		if (newApplicationName != null) {
			if (newApplicationName.equals("")) {
				PopupGenerator.showErrorDialog(this, "Blank name not allowed");
			} else {
				final String finalNewAppName = newApplicationName; 
				final boolean finalIsStoch = isStoch;
				AsynchClientTask task0 = new AsynchClientTask("create application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						SimulationContext newSimulationContext = getBioModel().addNewSimulationContext(finalNewAppName, finalIsStoch);
						hashTable.put("newSimulationContext", newSimulationContext);
					}
				};
				AsynchClientTask task1 = new AsynchClientTask("process geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
						newSimulationContext.getGeometry().precomputeAll();
					}
				};
				AsynchClientTask task2 = new AsynchClientTask("show application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
						getBioModelWindowManager().showApplicationFrame(newSimulationContext);
					}
				};
				ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task0, task1, task2});
			}
		}
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Failed to create new Application!\n"+exc.getMessage(), exc);
	}
}

/**
 * Comment
 */
private void renameApplication() {
	SimulationContext simulationContext = getSelectedSimulationContext();
	if (simulationContext == null) {
		PopupGenerator.showErrorDialog(this, "Please select an application.");
		return;
	}	
	try {
		String oldApplicationName = simulationContext.getName();
		String newApplicationName = null;
		try{
			newApplicationName = PopupGenerator.showInputDialog(getBioModelWindowManager(), "New name for the application:");
		}catch(UserCancelException e){
			return;
		}
		if (newApplicationName != null) {
			if (newApplicationName.equals(oldApplicationName)) {
				PopupGenerator.showErrorDialog(this, "New name provided is the same with the existing name");
				return;
			} else {
				simulationContext.setName(newApplicationName);
			}
		}
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, exc.getMessage(), exc);
	}	
}


/**
 * Sets the bioModel property (cbit.vcell.biomodel.BioModel) value.
 * @param bioModel The new value for the property.
 * @see #getBioModel
 */
public void setBioModel(BioModel bioModel) {
	BioModel oldValue = fieldBioModel;
	fieldBioModel = bioModel;
	firePropertyChange("bioModel", oldValue, bioModel);
}


/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 5:40:13 PM)
 * @param newBioModelWindowManager cbit.vcell.client.desktop.BioModelWindowManager
 */
public void setBioModelWindowManager(BioModelWindowManager newBioModelWindowManager) {
	bioModelWindowManager = newBioModelWindowManager;
}


/**
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(DocumentManager documentManager) {
	DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange(PROPERTY_NAME_DOCUMENT_MANAGER, oldValue, documentManager);
}

public void updateMenuOnSelectionChange() {	
//	Versionable selection = getBioModelTreePanel1().getSelectedVersionable();
//	SimulationContext selectedParent = getBioModelTreePanel1().getSelectedApplicationParent();
//	boolean bAppSelected = selection != null && (selection instanceof SimulationContext || selectedParent != null);
//	getCopyMenuItem().setEnabled(bAppSelected);
//	getOpenAppMenuItem().setEnabled(bAppSelected);
//	getRenameMenuItem().setEnabled(bAppSelected);
//	getDeleteMenuItem().setEnabled(bAppSelected);
}

private void onPropertyChange_BioModel() {
	getBioModelEditorTreeModel().setBioModel(getBioModel());
	getBioModelEditorSpeciesPanel().setBioModel(getBioModel());
	getBioModelEditorStructurePanel().setBioModel(getBioModel());
	getBioModelEditorReactionPanel().setBioModel(getBioModel());
	getBioModelEditorTreeCellRender().setBioModel(getBioModel());
}

private javax.swing.JPopupMenu getAppPopupMenu() {
	if (ivjAppPopupMenu == null) {
		try {
			ivjAppPopupMenu = new javax.swing.JPopupMenu();
			ivjAppPopupMenu.setName("AppPopupMenu");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	ivjAppPopupMenu.removeAll();
	ivjAppPopupMenu.add(getJMenuItemAppRename());
	ivjAppPopupMenu.add(getJMenuItemAppDelete());
	ivjAppPopupMenu.add(getJSeparator1());
	ivjAppPopupMenu.add(getJMenuItemAppCopy());
	ivjAppPopupMenu.add(getJMenuAppCopyAs());
	return ivjAppPopupMenu;
}

private javax.swing.JPopupMenu getAppsPopupMenu() {
	if (ivjAppsPopupMenu == null) {
		try {
			ivjAppsPopupMenu = new javax.swing.JPopupMenu();
			ivjAppsPopupMenu.setName("AppPopupMenu");
			ivjAppsPopupMenu.add(getJMenuAppNew());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAppsPopupMenu;
}

private javax.swing.JSeparator getJSeparator1() {
	if (ivjJSeparator1 == null) {
		try {
			ivjJSeparator1 = new javax.swing.JSeparator();
			ivjJSeparator1.setName("JSeparator1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator1;
}

//private javax.swing.JSeparator getJSeparator2() {
//	if (ivjJSeparator2 == null) {
//		try {
//			ivjJSeparator2 = new javax.swing.JSeparator();
//			ivjJSeparator2.setName("JSeparator2");
//			// user code begin {1}
//			// user code end
//		} catch (java.lang.Throwable ivjExc) {
//			// user code begin {2}
//			// user code end
//			handleException(ivjExc);
//		}
//	}
//	return ivjJSeparator2;
//}

private javax.swing.JMenuItem getJMenuItemAppAnnotation() {
	if (ivjJMenuItemAppAnnotation == null) {
		try {
			ivjJMenuItemAppAnnotation = new javax.swing.JMenuItem();
			ivjJMenuItemAppAnnotation.setName("JMenuItemAnnotation");
			ivjJMenuItemAppAnnotation.setMnemonic('a');
			ivjJMenuItemAppAnnotation.setText("Edit Application Annotation");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemAppAnnotation;
}

/**
 * Return the JMenuItemCopy property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuAppCopyAs() {
	if (ivjJMenuAppCopyAs == null) {
		try {
			ivjJMenuAppCopyAs = new javax.swing.JMenu();
			ivjJMenuAppCopyAs.setName("JMenuCopy");
			ivjJMenuAppCopyAs.setText("Copy As");
			//Menu items in Menu-Copy
			menuItemAppNonSpatialCopyStochastic=new JMenuItem();
			menuItemAppNonSpatialCopyStochastic.setName("JMenuItemToStochApp");
			menuItemAppNonSpatialCopyStochastic.setText(MENU_TEXT_STOCHASTIC_APPLICATION);
			menuItemAppNonSpatialCopyStochastic.setActionCommand(GuiConstants.ACTIONCMD_NON_SPATIAL_COPY_TO_STOCHASTIC_APPLICATION);
			menuItemNonSpatialCopyDeterministic = new javax.swing.JMenuItem();
			menuItemNonSpatialCopyDeterministic.setName("JMenuItemToNonStochApp");
			menuItemNonSpatialCopyDeterministic.setText(MENU_TEXT_DETERMINISTIC_APPLICATION);
			menuItemNonSpatialCopyDeterministic.setActionCommand(GuiConstants.ACTIONCMD_NON_SPATIAL_COPY_TO_DETERMINISTIC_APPLICATION);
			
			
			menuAppSpatialCopyAsNonSpatial = new JMenu(MENU_TEXT_NON_SPATIAL_APPLICATION);
			menuItemAppSpatialCopyAsNonSpatialDeterministic = new JMenuItem(MENU_TEXT_DETERMINISTIC_APPLICATION);
			menuItemAppSpatialCopyAsNonSpatialDeterministic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_DETERMINISTIC_APPLICATION);
			menuItemAppSpatialCopyAsNonSpatialStochastic = new JMenuItem(MENU_TEXT_STOCHASTIC_APPLICATION);
			menuItemAppSpatialCopyAsNonSpatialStochastic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_NON_SPATIAL_STOCHASTIC_APPLICATION);
			menuAppSpatialCopyAsNonSpatial.add(menuItemAppSpatialCopyAsNonSpatialDeterministic);
			menuAppSpatialCopyAsNonSpatial.add(menuItemAppSpatialCopyAsNonSpatialStochastic);
			
			menuAppSpatialCopyAsSpatial = new JMenu(MENU_TEXT_SPATIAL_APPLICATION);
			menuItemAppSpatialCopyAsSpatialDeterministic = new JMenuItem(MENU_TEXT_DETERMINISTIC_APPLICATION);
			menuItemAppSpatialCopyAsSpatialDeterministic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_DETERMINISTIC_APPLICATION);
			menuItemAppSpatialCopyAsSpatialStochastic = new JMenuItem(MENU_TEXT_STOCHASTIC_APPLICATION);
			menuItemAppSpatialCopyAsSpatialStochastic.setActionCommand(GuiConstants.ACTIONCMD_SPATIAL_COPY_TO_SPATIAL_STOCHASTIC_APPLICATION);
			menuAppSpatialCopyAsSpatial.add(menuItemAppSpatialCopyAsSpatialDeterministic);
			menuAppSpatialCopyAsSpatial.add(menuItemAppSpatialCopyAsSpatialStochastic);

		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	ivjJMenuAppCopyAs.removeAll();
	SimulationContext selectedSimContext = getSelectedSimulationContext();
	if (selectedSimContext != null && selectedSimContext.getGeometry().getDimension() == 0) {
		//add menu items to menu
		ivjJMenuAppCopyAs.add(menuItemNonSpatialCopyDeterministic);
		ivjJMenuAppCopyAs.add(menuItemAppNonSpatialCopyStochastic);
	} else {
		ivjJMenuAppCopyAs.add(menuAppSpatialCopyAsNonSpatial);
		ivjJMenuAppCopyAs.add(menuAppSpatialCopyAsSpatial);
	}
	return ivjJMenuAppCopyAs;
}

private javax.swing.JMenuItem getJMenuItemAppCopy() {
	if (ivjJMenuItemAppCopy == null) {
		try {
			ivjJMenuItemAppCopy = new javax.swing.JMenuItem();
			ivjJMenuItemAppCopy.setName("JMenuItemCopy");
			ivjJMenuItemAppCopy.setMnemonic('c');
			ivjJMenuItemAppCopy.setText("Copy");
			ivjJMenuItemAppCopy.setActionCommand(GuiConstants.ACTIONCMD_COPY_APPLICATION);
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemAppCopy;
}

/**
 * Return the JMenuItemDelete property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemAppDelete() {
	if (ivjJMenuItemAppDelete == null) {
		try {
			ivjJMenuItemAppDelete = new javax.swing.JMenuItem();
			ivjJMenuItemAppDelete.setName("JMenuItemDelete");
			ivjJMenuItemAppDelete.setMnemonic('d');
			ivjJMenuItemAppDelete.setText("Delete");
			ivjJMenuItemAppDelete.setActionCommand(GuiConstants.ACTIONCMD_DELETE_APPLICATION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemAppDelete;
}

/**
 * Return the JMenuItem1 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemAppRename() {
	if (ivjJMenuItemAppRename == null) {
		try {
			ivjJMenuItemAppRename = new javax.swing.JMenuItem();
			ivjJMenuItemAppRename.setName("JMenuItemRename");
			ivjJMenuItemAppRename.setMnemonic('r');
			ivjJMenuItemAppRename.setText("Rename");
			ivjJMenuItemAppRename.setActionCommand(GuiConstants.ACTIONCMD_RENAME_APPLICATION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemAppRename;
}


/**
 * Return the JMenuItem1 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuAppNew() {
	if (ivjJMenuAppNew == null) {
		try {
			ivjJMenuAppNew = new javax.swing.JMenu("New");
			ivjJMenuAppNew.setName("JMenuNew");
			ivjJMenuAppNew.setMnemonic('n');
			//Menu items in Menu-New
			appNewStochApp=new JMenuItem();
			appNewStochApp.setName("JMenuItemStochApp");
			appNewStochApp.setText(MENU_TEXT_STOCHASTIC_APPLICATION);
			appNewStochApp.setActionCommand(GuiConstants.ACTIONCMD_CREATE_STOCHASTIC_APPLICATION);
			appNewDeterministicApp = new javax.swing.JMenuItem();
			appNewDeterministicApp.setName("JMenuItemNonStochApp");
			appNewDeterministicApp.setText(MENU_TEXT_DETERMINISTIC_APPLICATION);
			appNewDeterministicApp.setActionCommand(GuiConstants.ACTIONCMD_CREATE_NON_STOCHASTIC_APPLICATION);
			//add menu items to menu
			ivjJMenuAppNew.add(appNewStochApp);
			ivjJMenuAppNew.add(appNewDeterministicApp);
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuAppNew;
}

private void deleteApplication() {
	SimulationContext simulationContext = getSelectedSimulationContext();
	if (simulationContext == null) {
		PopupGenerator.showErrorDialog(this, "Please select an application.");
		return;
	}
	try {
		//
		// BioModel enforces that there be no orphaned Simulations in BioModel.vetoableChange(...)
		// Check for no Simulations in SimualtionContext that is to be removed
		// otherwise a nonsense error message will be generated by BioModel
		//
		boolean bHasSims = false;
		Simulation[] simulations = simulationContext.getSimulations();
		if(simulations != null && simulations.length != 0){
			bHasSims = true;
		}

		if (bHasSims) {
			String confirm = PopupGenerator.showWarningDialog(getBioModelWindowManager(), getBioModelWindowManager().getUserPreferences(), UserMessage.warn_DeleteSelectedAppWithSims, simulationContext.getName());
			if (!confirm.equals(UserMessage.OPTION_CANCEL)) {
				for (Simulation simulation : simulations) {
					getBioModel().removeSimulation(simulation);
				}
				getBioModel().removeSimulationContext(simulationContext);
			}
		} else {
			String confirm = PopupGenerator.showWarningDialog(getBioModelWindowManager(), getBioModelWindowManager().getUserPreferences(), UserMessage.warn_DeleteSelectedApp, simulationContext.getName());		
			if (!confirm.equals(UserMessage.OPTION_CANCEL)) {
				getBioModel().removeSimulationContext(simulationContext);
			}
		}
	} catch (Exception exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Failed to Delete!\n"+exc.getMessage(), exc);
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
/**
 * Comment
 */
private void copyApplication(final boolean bSpatial, final boolean bStochastic) {
	final SimulationContext simulationContext = getSelectedSimulationContext();
	if (simulationContext == null) {
		PopupGenerator.showErrorDialog(this, "Please select an application.");
		return;
	}

	try {
		String newApplicationName = null;
		
		if (bStochastic) {
			//check validity if copy to stochastic application
			String message = getBioModel().getModel().isValidForStochApp();
			if(!message.equals(""))
			{
				throw new Exception(message);
			}
		}
		
		//get valid application name
		try{
			newApplicationName = PopupGenerator.showInputDialog(getBioModelWindowManager(), "Name for the application copy:");
		}catch(UserCancelException e){
			return;
		}
		if (newApplicationName != null) {
			if (newApplicationName.equals("")) {
				PopupGenerator.showErrorDialog(this, "Blank name not allowed");
			} else {
				final String newName = newApplicationName;
				AsynchClientTask task1 = new AsynchClientTask("copying", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						SimulationContext newSimulationContext = copySimulationContext(simulationContext, newName, bSpatial, bStochastic);
						hashTable.put("newSimulationContext", newSimulationContext);
					}
				};
				AsynchClientTask task2 = new AsynchClientTask("showing", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
					
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
						getBioModel().addSimulationContext(newSimulationContext);
						getBioModelWindowManager().showApplicationFrame(newSimulationContext);
					}
				};
				ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2} );
			}
		}
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this, "Failed to Copy Application!\n"+exc.getMessage(), exc);
	}
}

private SimulationContext copySimulationContext(SimulationContext srcSimContext, String newSimulationContextName, boolean bSpatial, boolean bStoch) throws java.beans.PropertyVetoException, ExpressionException, MappingException, GeometryException, ImageException {
	Geometry newClonedGeometry = new Geometry(srcSimContext.getGeometry());
	newClonedGeometry.precomputeAll();
	//if stoch copy to ode, we need to check is stoch is using particles. If yes, should convert particles to concentraton.
	//the other 3 cases are fine. ode->ode, ode->stoch, stoch-> stoch 
	SimulationContext destSimContext = new SimulationContext(srcSimContext,newClonedGeometry, bStoch);
	if(srcSimContext.isStoch() && !srcSimContext.isUsingConcentration() && !bStoch)
	{
		try {
			destSimContext.convertSpeciesIniCondition(true);
		} catch (MappingException e) {
			e.printStackTrace();
			throw new java.beans.PropertyVetoException(e.getMessage(), null);
		}
	}
	if (srcSimContext.getGeometry().getDimension() > 0 && !bSpatial) { // copy the size over
		destSimContext.setGeometry(new Geometry("nonspatial", 0));
		StructureMapping srcStructureMappings[] = srcSimContext.getGeometryContext().getStructureMappings();
		StructureMapping destStructureMappings[] = destSimContext.getGeometryContext().getStructureMappings();
		for (StructureMapping destStructureMapping : destStructureMappings) {
			for (StructureMapping srcStructureMapping : srcStructureMappings) {
				if (destStructureMapping.getStructure() == srcStructureMapping.getStructure()) {
					if (srcStructureMapping.getUnitSizeParameter() != null) {
						Expression sizeRatio = srcStructureMapping.getUnitSizeParameter().getExpression();
						GeometryClass srcGeometryClass = srcStructureMapping.getGeometryClass();
						GeometricRegion[] srcGeometricRegions = srcSimContext.getGeometry().getGeometrySurfaceDescription().getGeometricRegions(srcGeometryClass);
						if (srcGeometricRegions != null) {
							double size = 0;
							for (GeometricRegion srcGeometricRegion : srcGeometricRegions) {
								size += srcGeometricRegion.getSize();
							}
							destStructureMapping.getSizeParameter().setExpression(Expression.mult(sizeRatio, new Expression(size)));
						}
					}
					break;
				}
			}
		}
	}
	destSimContext.setName(newSimulationContextName);	
	return destSimContext;
}

public void applicationMenuItem_ActionPerformed(java.awt.event.ActionEvent e) {
	String actionCommand = e.getActionCommand();
//	if (actionCommand.equals(GuiConstants.ACTIONCMD_OPEN_APPLICATION)) {
//		openApplication();
//	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_OPEN_APPLICATION_MATH)) {
//		openApplication(ApplicationEditor.TAB_IDX_VIEW_MATH);
//	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_OPEN_APPLICATION_GEOMETRY)) {
//		openApplication(ApplicationEditor.TAB_IDX_SPPR);
//	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_OPEN_APPLICATION_SIMULATION)) {
//		openApplication(ApplicationEditor.TAB_IDX_SIMULATION);
//	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_OPEN_APPLICATION_DETSTOCH)) {
//		openApplication();
//	} else 
	if (actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_NON_STOCHASTIC_APPLICATION)) {
		newApplication(e);
	}  else if (actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_STOCHASTIC_APPLICATION)) {
		newApplication(e);
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_DELETE_APPLICATION)) {
		deleteApplication();
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_RENAME_APPLICATION)) {
		renameApplication();
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_COPY_APPLICATION)) {
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
	} else if (actionCommand.equals(GuiConstants.ACTIONCMD_CREATE_NEW_APPLICATION)) {
		newApplication(e);
	} 
}

private SimpleReactionPanel getSimpleReactionPanel() {
	if (simpleReactionPanel == null) {
		try {
			simpleReactionPanel = new SimpleReactionPanel();
			simpleReactionPanel.setName("SimpleReactionPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	//BeanUtils.enableComponents(simpleReactionPanel, false);
	return simpleReactionPanel;
}

private FluxReactionPanel getFluxReactionPanel() {
	if (fluxReactionPanel == null) {
		try {
			fluxReactionPanel = new FluxReactionPanel();
			fluxReactionPanel.setName("FluxReactionPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	//BeanUtils.enableComponents(fluxReactionPanel, false);
	return fluxReactionPanel;
}

private SpeciesEditorPanel getSpeciesEditorPanel() {
	if (speciesEditorPanel == null) {
		try {
			speciesEditorPanel = new SpeciesEditorPanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return speciesEditorPanel;
}

private FeatureEditorPanel getFeatureEditorPanel() {
	if (featureEditorPanel == null) {
		try {
			featureEditorPanel = new FeatureEditorPanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return featureEditorPanel;
}

private MembraneEditorPanel getMembraneEditorPanel() {
	if (membraneEditorPanel == null) {
		try {
			membraneEditorPanel = new MembraneEditorPanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return membraneEditorPanel;
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