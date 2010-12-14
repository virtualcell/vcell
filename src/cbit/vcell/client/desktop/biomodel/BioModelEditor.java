package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.desktop.DatabaseWindowPanel;
import cbit.vcell.client.desktop.biomodel.BioModelEditorTreeModel.BioModelEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.BioModelEditorTreeModel.BioModelEditorTreeFolderNode;
import cbit.vcell.client.desktop.geometry.GeometrySummaryViewer;
import cbit.vcell.client.desktop.simulation.OutputFunctionsPanel;
import cbit.vcell.client.desktop.simulation.SimulationListPanel;
import cbit.vcell.client.desktop.simulation.SimulationWorkspace;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.gui.DataSymbolsPanel;
import cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel;
import cbit.vcell.mapping.gui.InitialConditionsPanel;
import cbit.vcell.mapping.gui.MicroscopeMeasurementPanel;
import cbit.vcell.mapping.gui.ReactionSpecsPanel;
import cbit.vcell.mapping.gui.StructureMappingCartoonPanel;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.model.Model;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.gui.OptTestPanel;
import cbit.vcell.opt.solvers.OptimizationService;
import cbit.vcell.solver.Simulation;
import cbit.vcell.xml.gui.MiriamTreeModel.LinkNode;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 2:55:18 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class BioModelEditor extends JPanel {
	private static final String PROPERTY_NAME_BIO_MODEL = "bioModel";
	public static final String PROPERTY_NAME_BIOMODEL_EDITOR_SELECTION = "bioModelEditorSelection";
	public static final String PROPERTY_NAME_SELECTED_VERSIONABLE = "selectedVersionable";
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private BioModelWindowManager bioModelWindowManager = null;
	private BioModel fieldBioModel = new BioModel(null);
	
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
	private ReactionSpecsPanel reactionSpecsPanel = null;
	private ElectricalMembraneMappingPanel ivjElectricalMembraneMappingPanel = null;
	private EventsDisplayPanel eventsDisplayPanel = null;
	private MathematicsPanel mathematicsPanel = null;
//	private FeatureEditorPanel featureEditorPanel = null;
//	private MembraneEditorPanel membraneEditorPanel = null;
//	private BioModelEditorSpeciesPanel bioModelEditorSpeciesPanel = null;
//	private BioModelEditorStructurePanel bioModelEditorStructurePanel = null;
//	private BioModelEditorReactionPanel bioModelEditorReactionPanel = null;
	private BioModelEditorGlobalParameterPanel bioModelEditorGlobalParameterPanel = null;
	private BioModelEditorModelPanel bioModelEditorModelPanel = null;
	private MicroscopeMeasurementPanel microscopeMeasurementPanel = null;
	private ScriptingPanel scriptingPanel = null;
	
	private BioModelEditorTreeModel bioModelEditorTreeModel = null;
	private JPanel emptyPanel = new JPanel();

	private AnnotationEditorPanel ivjAnnotationEditorPanel = null;	
	private BioModelEditorApplicationsPanel bioModelEditorApplicationsPanel = null;
	
	private JPopupMenu popupMenu = null;
	private JMenuItem expandAllMenuItem = null;
	private SelectionManager selectionManager = new SelectionManager();
	private DatabaseWindowPanel databaseWindowPanel;
		
	public static class BioModelEditorSelection {
		private Object selectedContainer;
		private Object selectedObject;
		
		public BioModelEditorSelection(Object selectedContainer, Object selectedObject) {
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
		public AnnotationEditorPanel() {
			
			textArea = new JTextArea("", 10, 45);
			textArea.setEditable(true);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);

			JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 10));
			JLabel label = new JLabel("Edit Notes: ");
			label.setVerticalAlignment(SwingConstants.TOP);
			label.setAlignmentY(TOP_ALIGNMENT);
			panel.add(label);
			panel.add(new JScrollPane(textArea));
			
			setLayout(new BorderLayout());
			add(panel, BorderLayout.CENTER);
			
			textArea.addFocusListener(new FocusListener() {
				
				public void focusLost(FocusEvent e) {
					if (getBioModel() == null) {
						return;
					}
					getBioModel().getVCMetaData().setFreeTextAnnotation(getBioModel(), textArea.getText());						
				}
				
				public void focusGained(FocusEvent e) {
				}
			});
		}
		
		public void setText(String annot) {
			textArea.setText(annot);
		}
	}
	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.TreeSelectionListener, MouseListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == expandAllMenuItem) {
				Object lastSelectedPathComponent = getBioModelEditorTree().getLastSelectedPathComponent();
				if (lastSelectedPathComponent instanceof BioModelNode) {
					expandAll((BioModelNode)lastSelectedPathComponent);
				}
			} 
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditor.this && (evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL))) 
				onPropertyChange_BioModel();
			if (evt.getPropertyName().equals(PROPERTY_NAME_BIOMODEL_EDITOR_SELECTION)) {
				getBioModelEditorTreeModel().select((BioModelEditorSelection)evt.getNewValue());
			} else if (evt.getSource() == selectionManager) {
				setSelectedObject();
			}
		};
		
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == getBioModelEditorTree()) {
				if (SwingUtilities.isRightMouseButton(e)) {	// right click		
					Point mousePoint = e.getPoint();
					TreePath path = getBioModelEditorTree().getPathForLocation(mousePoint.x, mousePoint.y);
                    if (path == null) {
                    	return; 
                    }
					Object node = getBioModelEditorTree().getLastSelectedPathComponent();
					if (node == null || !(node instanceof BioModelNode) || path.getLastPathComponent() != node) {
						return;
					}
					getPopupMenu().show(getBioModelEditorTree(), mousePoint.x, mousePoint.y);
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
				treeSelectionChanged();
		}
	};

/**
 * BioModelEditor constructor comment.
 */
public BioModelEditor() {
	super();
	initialize();
}

public void setSelectedObject() {
	Object[] selectedObjects = selectionManager.getSelectedObjects();
	if (selectedObjects == null || selectedObjects.length == 0 || selectedObjects.length > 1) {
		return;
	} 
	
	if (selectedObjects[0] instanceof VCDocumentInfo) {
		JPanel rightPanel = getBioModelEditorModelPanel();
		Component rightComponent = splitPane.getRightComponent();
		if (rightComponent != rightPanel) {
			splitPane.setRightComponent(rightPanel);
		}
		if (splitPane.isShowing()) {	
			splitPane.setDividerLocation(0.25);
		} else {		
			splitPane.setDividerLocation(220);
		}
	}		
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

//private BioModelEditorSpeciesPanel getBioModelEditorSpeciesPanel() {
//	if (bioModelEditorSpeciesPanel == null) {
//		try {
//			bioModelEditorSpeciesPanel = new BioModelEditorSpeciesPanel();
//		} catch (java.lang.Throwable ivjExc) {
//			handleException(ivjExc);
//		}
//	}
//	return bioModelEditorSpeciesPanel;
//}
//
//private BioModelEditorReactionPanel getBioModelEditorReactionPanel() {
//	if (bioModelEditorReactionPanel == null) {
//		try {
//			bioModelEditorReactionPanel = new BioModelEditorReactionPanel();
//		} catch (java.lang.Throwable ivjExc) {
//			handleException(ivjExc);
//		}
//	}
//	return bioModelEditorReactionPanel;
//}

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

//private BioModelEditorStructurePanel getBioModelEditorStructurePanel() {
//	if (bioModelEditorStructurePanel == null) {
//		try {
//			bioModelEditorStructurePanel = new BioModelEditorStructurePanel();
//		} catch (java.lang.Throwable ivjExc) {
//			handleException(ivjExc);
//		}
//	}
//	return bioModelEditorStructurePanel;
//}

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
	selectionManager.addPropertyChangeListener(ivjEventHandler);
	
	getBioModelEditorModelPanel().setSelectionManager(selectionManager);
	databaseWindowPanel.setSelectionManager(selectionManager);
//	getBioModelEditorModelPanel().addPropertyChangeListener(ivjEventHandler);
	getSimulationListPanel().addPropertyChangeListener(ivjEventHandler);
	getInitialConditionsPanel().addPropertyChangeListener(ivjEventHandler);
	getReactionSpecsPanel().addPropertyChangeListener(ivjEventHandler);
	getBioModelEditorApplicationsPanel().addPropertyChangeListener(ivjEventHandler);
	getParameterEstimationPanel().addPropertyChangeListener(ivjEventHandler);

	getBioModelEditorTree().addTreeSelectionListener(ivjEventHandler);
	getBioModelEditorTree().addTreeSelectionListener(getBioModelEditorTreeModel());
	getBioModelEditorTree().addMouseListener(ivjEventHandler);
	getBioModelEditorTree().addTreeExpansionListener(getBioModelEditorTreeModel());
	getEventsDisplayPanel().addPropertyChangeListener(ivjEventHandler);

	selectionManager.addPropertyChangeListener(getBioModelEditorTreeModel());
	getMenuItemExpandAll().addActionListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setName("ApplicationEditor");
		setLayout(new BorderLayout());
		
		JSplitPane leftSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);		
		JTabbedPane tabbedPane = new JTabbedPane();
		databaseWindowPanel = new DatabaseWindowPanel(false, false);
		tabbedPane.addTab("VCell Database", databaseWindowPanel);
		
		getTreePanel().setMinimumSize(new java.awt.Dimension(198, 148));
		leftSplitPanel.setTopComponent(getTreePanel());
		tabbedPane.setMinimumSize(new java.awt.Dimension(198, 148));
		leftSplitPanel.setBottomComponent(tabbedPane);
		leftSplitPanel.setResizeWeight(0.5);
		leftSplitPanel.setDividerLocation(300);
		leftSplitPanel.setOneTouchExpandable(true);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.3);
		splitPane.setLeftComponent(leftSplitPanel);
		splitPane.setRightComponent(getBioModelEditorModelPanel());
		
		add(splitPane, BorderLayout.CENTER);		
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
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

private BioModelEditorModelPanel getBioModelEditorModelPanel() {
	if (bioModelEditorModelPanel == null) {
		try {
			bioModelEditorModelPanel = new BioModelEditorModelPanel();
			bioModelEditorModelPanel.setName("ModelParameterPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return bioModelEditorModelPanel;
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

private BioModelEditorGlobalParameterPanel getBioModelEditorGlobalParameterPanel() {
	if (bioModelEditorGlobalParameterPanel == null) {
		try {
			bioModelEditorGlobalParameterPanel = new BioModelEditorGlobalParameterPanel();
			bioModelEditorGlobalParameterPanel.setName("ModelParameterPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return bioModelEditorGlobalParameterPanel;
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
			bioModelEditorTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			bioModelEditorTree.setModel(getBioModelEditorTreeModel());
			int rowHeight = bioModelEditorTree.getRowHeight();
			if(rowHeight < 10) { rowHeight = 20; }
			bioModelEditorTree.setRowHeight(rowHeight + 2);
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

private void treeSelectionChanged() {
	try {
		Object lastSelectedPathComponent = getBioModelEditorTree().getLastSelectedPathComponent();
		TreePath[] paths = getBioModelEditorTree().getSelectionModel().getSelectionPaths();
		List<Object> selectedObjects = new ArrayList<Object>();
		if (paths != null) {
			for (TreePath path : paths) {
				Object node = path.getLastPathComponent();
				if (node != null && (node instanceof BioModelNode)) {
					selectedObjects.add(((BioModelNode)node).getUserObject());
				}
			}
		}
		selectionManager.setSelectedObjects(selectedObjects.toArray());		
		if (lastSelectedPathComponent == null || !(lastSelectedPathComponent instanceof BioModelNode)) {
			return;
		}
		BioModelNode selectedNode = (BioModelNode)lastSelectedPathComponent;
	    Object selectedObject = selectedNode.getUserObject();
	    SimulationContext simulationContext = getSelectedSimulationContext();
	    if (selectedObject instanceof BioModel) {
	    	setRightPanel(null, null, null);
	    } else if (selectedObject instanceof Model) {
	    	setRightPanel(null, selectedObject, null);
	    } else if (selectedObject instanceof BioModelEditorTreeFolderNode) { // it's a folder	    	
	    	setRightPanel((BioModelEditorTreeFolderNode)selectedObject, null, simulationContext);
	    } else if (selectedObject instanceof SimulationContext){
	    	BioModelNode parentNode = (BioModelNode) selectedNode.getParent();
			Object parentObject =  parentNode.getUserObject();
			BioModelEditorTreeFolderNode parent = (BioModelEditorTreeFolderNode)parentObject;
	    	setRightPanel(parent, selectedObject, simulationContext);
	    } else if (selectedObject instanceof VCMetaData || selectedObject instanceof MiriamResource){
	    	setRightPanel(null, selectedObject, null);
	    } else {
	        Object leafObject = selectedObject;
			BioModelNode parentNode = (BioModelNode) selectedNode.getParent();
			Object parentObject =  parentNode.getUserObject();
			if (!(parentObject instanceof BioModelEditorTreeFolderNode)) {
				return;
			}
			BioModelEditorTreeFolderNode parent = (BioModelEditorTreeFolderNode)parentObject;
			setRightPanel(parent, leafObject, simulationContext);
	    }
	}catch (Exception ex){
		ex.printStackTrace(System.out);
	}
}

private void setRightPanel(BioModelEditorTreeFolderNode folderNode, Object leafObject, SimulationContext simulationContext) {
	JComponent rightPanel = emptyPanel;
	if (folderNode == null) { // could be BioModel or SimulationContext or VCMetaData or MiriamResource,
		if (leafObject instanceof Model) {
			rightPanel = getBioModelEditorModelPanel();
		} else if (leafObject instanceof VCMetaData || leafObject instanceof MiriamResource) {
			rightPanel = getAnnotationEditorPanel();
			if (getBioModel() != null) {
				String annot =  getBioModel().getVCMetaData().getFreeTextAnnotation(getBioModel());
				getAnnotationEditorPanel().setText(annot);
			}
		}
	} else {
		BioModelEditorTreeFolderClass folderClass = folderNode.getFolderClass();
		if (folderClass == BioModelEditorTreeFolderClass.STRUCTURES_NODE 
				|| folderClass == BioModelEditorTreeFolderClass.SPECIES_NODE
				|| folderClass == BioModelEditorTreeFolderClass.GLOBAL_PARAMETER_NODE
				|| folderClass == BioModelEditorTreeFolderClass.REACTIONS_NODE) {
			rightPanel = getBioModelEditorModelPanel();
		} else if (folderClass == BioModelEditorTreeFolderClass.APPLICATTIONS_NODE) {
			rightPanel = getBioModelEditorApplicationsPanel();
			if (leafObject != null) {
				getBioModelEditorApplicationsPanel().select((SimulationContext) leafObject);
			}
		} else if (folderClass == BioModelEditorTreeFolderClass.SCRIPTING_NODE) {
			rightPanel = getScriptingPanel();
			getScriptingPanel().setBioModel(getBioModel());
		} else if (folderClass == BioModelEditorTreeFolderClass.MATHEMATICS_NODE) {
			rightPanel = getMathematicsPanel();
			getMathematicsPanel().setSimulationContext(simulationContext);
		} else if (folderClass == BioModelEditorTreeFolderClass.ANALYSIS_NODE) {
			rightPanel = getParameterEstimationPanel();
			getParameterEstimationPanel().setSimulationContext(simulationContext);
			if (leafObject != null) {
				getParameterEstimationPanel().select((ParameterEstimationTask)leafObject);
			}
		} else if (folderClass == BioModelEditorTreeFolderClass.GEOMETRY_NODE) {
			rightPanel = getGeometrySummaryViewer();
			getGeometrySummaryViewer().setGeometryOwner(simulationContext);
		} else if(folderClass == BioModelEditorTreeFolderClass.STRUCTURE_MAPPING_NODE) {
			rightPanel = getStructureMappingCartoonPanel();
			getStructureMappingCartoonPanel().setSimulationContext(simulationContext);
		} else if(folderClass == BioModelEditorTreeFolderClass.INITIAL_CONDITIONS_NODE) {
			rightPanel = getInitialConditionsPanel();
			getInitialConditionsPanel().setSimulationContext(simulationContext);
			getInitialConditionsPanel().setScrollPaneTableCurrentRow((SpeciesContextSpec)leafObject);
		} else if(folderClass == BioModelEditorTreeFolderClass.APP_REACTIONS_NODE) {
			rightPanel = getReactionSpecsPanel();
			getReactionSpecsPanel().setSimulationContext(simulationContext);
			getReactionSpecsPanel().setScrollPaneTableCurrentRow((ReactionSpec)leafObject);
		} else if(folderClass == BioModelEditorTreeFolderClass.ELECTRICAL_MAPPING_NODE) {
			rightPanel = getElectricalMembraneMappingPanel();
			getElectricalMembraneMappingPanel().setSimulationContext(simulationContext);
		} else if(folderClass == BioModelEditorTreeFolderClass.EVENTS_NODE) {
			rightPanel = getEventsDisplayPanel();
			getEventsDisplayPanel().setSimulationContext(simulationContext);
			if (leafObject != null) {
				getEventsDisplayPanel().select((BioEvent)leafObject);
			}
		} else if(folderClass == BioModelEditorTreeFolderClass.DATA_SYMBOLS_NODE) {
			rightPanel = getDataSymbolsPanel();
			getDataSymbolsPanel().setSimulationContext(simulationContext);
			if (leafObject != null) {
				getDataSymbolsPanel().select((DataSymbol)leafObject);	// notify right panel about selection change
			}
		} else if(folderClass == BioModelEditorTreeFolderClass.MICROSCOPE_MEASUREMENT_NODE) {
			rightPanel = getMicroscopeMeasurementPanel();
			getMicroscopeMeasurementPanel().setSimulationContext(simulationContext);
		} else {
			ApplicationComponents applicationComponents = bioModelWindowManager.getApplicationComponents(simulationContext);
			SimulationWorkspace simulationWorkspace = applicationComponents.getSimulationWorkspace();
			if(folderClass == BioModelEditorTreeFolderClass.SIMULATIONS_NODE) {
				rightPanel = getSimulationListPanel();
				getSimulationListPanel().setSimulationWorkspace(simulationWorkspace);
				if (leafObject != null) {
					getSimulationListPanel().select((Simulation) leafObject);
				}
			} else if(folderClass == BioModelEditorTreeFolderClass.OUTPUT_FUNCTIONS_NODE) {
				rightPanel = getOutputFunctionsPanel();
				getOutputFunctionsPanel().setSimulationWorkspace(simulationWorkspace);
				if (leafObject != null) {
					getOutputFunctionsPanel().select((AnnotatedFunction) leafObject);
				}
			}
		}
	}
	Component rightComponent = splitPane.getRightComponent();
	if (rightComponent != rightPanel) {
		splitPane.setRightComponent(rightPanel);
	}
	if (splitPane.isShowing()) {	
		splitPane.setDividerLocation(0.25);
	} else {		
		splitPane.setDividerLocation(220);
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

private JPopupMenu getPopupMenu() {
	if (popupMenu == null) {
		try {
			popupMenu = new javax.swing.JPopupMenu();
			popupMenu.add(getMenuItemExpandAll());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return popupMenu;
}

private JMenuItem getMenuItemExpandAll() {
	if (expandAllMenuItem == null) {
		try {
			expandAllMenuItem = new javax.swing.JMenuItem("Expand All");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return expandAllMenuItem;
}

public BioModelWindowManager getBioModelWindowManager() {
	return bioModelWindowManager;
}

private BioModel getBioModel() {
	return fieldBioModel;
}

/**
 * Sets the bioModel property (cbit.vcell.biomodel.BioModel) value.
 * @param bioModel The new value for the property.
 * @see #getBioModel
 */
public void setBioModel(BioModel bioModel) {
	BioModel oldValue = fieldBioModel;
	fieldBioModel = bioModel;
	firePropertyChange(PROPERTY_NAME_BIO_MODEL, oldValue, bioModel);
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
	getBioModelEditorApplicationsPanel().setBioModelWindowManager(bioModelWindowManager);
	getGeometrySummaryViewer().addActionListener(getBioModelWindowManager());
	getMathematicsPanel().addActionListener(bioModelWindowManager);
	DatabaseWindowManager windowManager = new DatabaseWindowManager(databaseWindowPanel, bioModelWindowManager.getRequestManager());
	databaseWindowPanel.setDatabaseWindowManager(windowManager);
	DocumentManager documentManager = bioModelWindowManager.getRequestManager().getDocumentManager();
	databaseWindowPanel.setDocumentManager(documentManager);
	getBioModelEditorModelPanel().setDocumentManager(documentManager);
}

private void onPropertyChange_BioModel() {
	getBioModelEditorModelPanel().setBioModel(getBioModel());
	getBioModelEditorTreeCellRender().setBioModel(getBioModel());
	getBioModelEditorGlobalParameterPanel().setBioModel(getBioModel()); 
	getBioModelEditorApplicationsPanel().setBioModel(getBioModel());
	getBioModelEditorTreeModel().setBioModel(getBioModel());
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

private void expandAll(BioModelNode treeNode) {
	int childCount = treeNode.getChildCount();
	if (childCount > 0) {
		for (int i = 0; i < childCount; i++) {
			TreeNode n = treeNode.getChildAt(i);
			if (n instanceof BioModelNode) {
				expandAll((BioModelNode)n);
			}
		}
	} else {
		TreePath path = new TreePath(treeNode.getPath());
		if (!getBioModelEditorTree().isExpanded(path)) {
			getBioModelEditorTree().expandPath(path.getParentPath());
		}
	}
}

}