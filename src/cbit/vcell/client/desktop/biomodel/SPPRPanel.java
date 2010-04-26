package cbit.vcell.client.desktop.biomodel;

import java.awt.AWTEventMulticaster;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.biomodel.SPPRTreeModel.SPPRTreeFolderNode;
import cbit.vcell.client.desktop.geometry.GeometrySummaryViewer;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.geometry.gui.GeometryViewer;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.gui.ElectricalMembraneMappingPanel;
import cbit.vcell.mapping.gui.InitialConditionsPanel;
import cbit.vcell.mapping.gui.ReactionSpecsPanel;
import cbit.vcell.mapping.gui.StructureMappingCartoonPanel;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.gui.ModelParameterPanel;

public class SPPRPanel extends JPanel {

	private JSplitPane outerSplitPane;
	private javax.swing.JScrollPane treePanel = null;
	private javax.swing.JTree spprTree = null;
	private GeometrySummaryViewer ivjGeometrySummaryViewer = null;
	private StructureMappingCartoonPanel ivjStructureMappingCartoonPanel = null;
	private InitialConditionsPanel initialConditionsPanel = null;
	private ModelParameterPanel modelParameterPanel = null;
	private ReactionSpecsPanel reactionSpecsPanel = null;
	private ElectricalMembraneMappingPanel ivjElectricalMembraneMappingPanel = null;
	private EventsDisplayPanel eventsDisplayPanel = null;
	private cbit.vcell.mapping.SimulationContext fieldSimulationContext = null;
	private SPPRTreeModel spprTreeModel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
//	private FolderStatus folderStatus = new FolderStatus();
	protected transient ActionListener commandActionListener = null;
	
	// menu items to add events
	private JMenuItem menuItemAddEvent = null;
	private JPopupMenu addEventPopupMenu = null;
	private JPopupMenu deleteEventPopupMenu = null;
	private JMenuItem menuItemDeleteEvent = null;
	
	class IvjEventHandler implements javax.swing.event.TreeSelectionListener, MouseListener, PropertyChangeListener, ActionListener {
		
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == SPPRPanel.this.getSpprTree())
				treeValueChanged(e);
		}
		public void mouseClicked(MouseEvent e) {
			boolean bRightClick = SwingUtilities.isRightMouseButton(e);		
			if (bRightClick) {
				if (e.getSource() == SPPRPanel.this.getSpprTree()) {			
					DefaultMutableTreeNode currentTreeSelection = (DefaultMutableTreeNode)getSpprTree().getLastSelectedPathComponent();
					if(currentTreeSelection == null){
						return;
					}
					// de-activate pop-up menu if simulationContext is spatial or stochastic
					int dimension = fieldSimulationContext.getGeometry().getDimension();
					if (dimension == 0 && !fieldSimulationContext.isStoch()) {
						Object selectedObject = currentTreeSelection.getUserObject();;
						Point mousePoint = e.getPoint();
						if (selectedObject instanceof SPPRTreeFolderNode) {
							SPPRTreeFolderNode stfn = (SPPRTreeFolderNode)selectedObject;
							if (stfn.getName().equals(SPPRTreeModel.FOLDER_NODE_NAMES[SPPRTreeModel.EVENTS_NODE])) { // "Events"
								getAddEventPopupMenu().show(getSpprTree(), mousePoint.x, mousePoint.y);
							}
						} else if (selectedObject instanceof BioEvent) {
							getDeleteEventPopupMenu().show(getSpprTree(), mousePoint.x, mousePoint.y);
						}
					}
				}					
			} 

		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == getEventsDisplayPanel() && evt.getPropertyName().equals("selectedBioEvent")) {
				getSpprTreeModel().setSelectedValue(evt.getNewValue());
			}
			if (evt.getSource() == fieldSimulationContext && evt.getPropertyName().equals("geometry")) {
				getGeometrySummaryViewer().setGeometry(fieldSimulationContext.getGeometry());
			}
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(getMenuItemAddEvent())) {
				addEvent();
			}
			if (e.getSource().equals(getMenuItemDeleteEvent())) {
				deleteEvent();
			}
			if (e.getSource().equals(getGeometrySummaryViewer())) {
				refireCommandActionPerformed(e);
			}
			
		}	
	};
	
	private void refireCommandActionPerformed(ActionEvent e) {
		// relays an action event with this as the source
		fireCommandActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
	}
	
	public synchronized void addCommandActionListener(ActionListener l) {
		commandActionListener = AWTEventMulticaster.add(commandActionListener, l);
	}
	
	public synchronized void removeCommandActionListener(ActionListener l) {
		commandActionListener = AWTEventMulticaster.remove(commandActionListener, l);
	}

	protected void fireCommandActionPerformed(ActionEvent e) {
		if (commandActionListener != null) {
			commandActionListener.actionPerformed(e);
		}         
	}

	public static void main(String[] args) {
	}

	public SPPRPanel() {
		super();
		setLayout(new GridBagLayout());
		initialize();
	}
	
	public void setSimulationContext(SimulationContext simulationContext) {
		if (fieldSimulationContext!=null){ // unlisten to the old simulation context.
			fieldSimulationContext.removePropertyChangeListener(ivjEventHandler);
		}
		fieldSimulationContext = simulationContext;
		if (fieldSimulationContext!=null){
			fieldSimulationContext.addPropertyChangeListener(ivjEventHandler); // listen to the new simlation context.
			getGeometrySummaryViewer().setGeometry(fieldSimulationContext.getGeometry());
			getGeometrySummaryViewer().setStochastic(fieldSimulationContext.isStoch());
		}else{
			getGeometrySummaryViewer().setGeometry(null);
		}
		getStructureMappingCartoonPanel().setSimulationContext(fieldSimulationContext);
		getInitialConditionsPanel().setSimulationContext(fieldSimulationContext);
		getModelParameterPanel().setModel(fieldSimulationContext.getModel());
		getReactionSpecsPanel().setSimulationContext(fieldSimulationContext);
		getEventsDisplayPanel().setSimulationContext(fieldSimulationContext);
		getSpprTreeModel().setSimulationContext(fieldSimulationContext);
		getElectricalMembraneMappingPanel().setSimulationContext(fieldSimulationContext);
		getSpprTree().setSelectionRow(SPPRTreeModel.STRUCTURE_MAPPING_NODE+1); // +1 is to go from index (0 based)to row number (1 based).
//		getSpprTree().expandRow(2);
	}
	
	private void handleException(java.lang.Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}	

	private void initConnections() throws java.lang.Exception {
//		System.out.println("SPPRPanel:  initConnections()");
		getGeometrySummaryViewer().addActionListener(ivjEventHandler);
		spprTree.addTreeSelectionListener(ivjEventHandler);
		spprTree.addMouseListener(ivjEventHandler);
		spprTree.addTreeExpansionListener(getSpprTreeModel());
		getEventsDisplayPanel().addPropertyChangeListener(ivjEventHandler);
		getMenuItemAddEvent().addActionListener(ivjEventHandler);
		getMenuItemDeleteEvent().addActionListener(ivjEventHandler);
	}	

	private void initialize() {
		try {
//			System.out.println("SPPRPanel:  initialize()");
			setName("SPPRPanel");
			setSize(750, 560);
			
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.weighty = 1;
			gridBagConstraints.weightx = 1;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			add(getOuterSplitPane(), gridBagConstraints);

			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	private JSplitPane getOuterSplitPane() {
		if (outerSplitPane == null) {
			outerSplitPane = new JSplitPane();
			outerSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			
			outerSplitPane.setLeftComponent(getTreePanel());
			outerSplitPane.setRightComponent(getStructureMappingCartoonPanel());
		}
		return outerSplitPane;
	}

// ------------ Left Panel -----------------------
	
	private javax.swing.JScrollPane getTreePanel() {
		if (treePanel == null) {
			try {
				treePanel = new javax.swing.JScrollPane();
				treePanel.setName("LeftTreePanel");
				Dimension dim = new Dimension(200, 20);
				treePanel.setPreferredSize(dim);
				treePanel.setViewportView(getSpprTree());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return treePanel;
	}
	private javax.swing.JTree getSpprTree() {
		if (spprTree == null) {
			try {
				spprTree = new javax.swing.JTree();
				spprTree.setName("JParameterTree");
				ToolTipManager.sharedInstance().registerComponent(spprTree);
			    spprTree.setCellRenderer(new SPPRTreeCellRenderer());				
				spprTree.setBounds(0, 0, 78, 72);
				spprTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
				spprTree.setModel(getSpprTreeModel());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return spprTree;
	}
	
	private SPPRTreeModel getSpprTreeModel() {
		if (spprTreeModel == null) {
			try {
				spprTreeModel = new SPPRTreeModel(getSpprTree());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return spprTreeModel;
	}
	
	public void treeValueChanged(TreeSelectionEvent e) {
		try {
			BioModelNode node = (BioModelNode)spprTree.getLastSelectedPathComponent();
			if (node == null) {
				return;
			}
		    Object userObject = node.getUserObject();
		    if (userObject instanceof SPPRTreeFolderNode) { // it's a folder
		        setupRightComponent((SPPRTreeFolderNode)userObject, null);
		    } else if (userObject instanceof SimulationContext){
		    } else {
		        Object leaf = userObject;
				BioModelNode parentNode = (BioModelNode) node.getParent();
				userObject =  parentNode.getUserObject();
				SPPRTreeFolderNode parent = (SPPRTreeFolderNode)userObject;
		        setupRightComponent(parent, leaf);
		    }
		}catch (Exception ex){
			ex.printStackTrace(System.out);
		}
	}
	
	private void setupRightComponent(SPPRTreeFolderNode folderNode, Object leaf) {
		int folderId = folderNode.getId();
		if (SPPRTreeModel.FOLDER_NODE_IMPLEMENTED[folderId] && folderNode.isSupported()) {
			if(folderId == SPPRTreeModel.GEOMETRY_NODE) {
				//  replace right-side panel only if the correct one is not there already
				if(outerSplitPane.getRightComponent() != getGeometrySummaryViewer()) {
					outerSplitPane.setRightComponent(getGeometrySummaryViewer());
				}
			} else if(folderId == SPPRTreeModel.STRUCTURE_MAPPING_NODE) {
				//  replace right-side panel only if the correct one is not there already
				if(outerSplitPane.getRightComponent() != getStructureMappingCartoonPanel()) {
					outerSplitPane.setRightComponent(getStructureMappingCartoonPanel());
				}
			} else if(folderId == SPPRTreeModel.INITIAL_CONDITIONS_NODE) {
				//  replace right-side panel only if the correct one is not there already
				if(outerSplitPane.getRightComponent() != getInitialConditionsPanel()) {
					outerSplitPane.setRightComponent(getInitialConditionsPanel());
				}
				getInitialConditionsPanel().setScrollPaneTableCurrentRow((SpeciesContext)leaf);	// notify right panel about selection change
			} else if(folderId == SPPRTreeModel.GLOBAL_PARAMETER_NODE) {
				if(outerSplitPane.getRightComponent() != getModelParameterPanel()) {
					outerSplitPane.setRightComponent(getModelParameterPanel());
				}
				getModelParameterPanel().setScrollPaneTableCurrentRow((ModelParameter)leaf);	// notify right panel about selection change
			} else if(folderId == SPPRTreeModel.REACTIONS_NODE) {
				if(outerSplitPane.getRightComponent() != getReactionSpecsPanel()) {
					outerSplitPane.setRightComponent(getReactionSpecsPanel());
				}
				getReactionSpecsPanel().setScrollPaneTableCurrentRow((ReactionStep)leaf);	// notify right panel about selection change
			} else if(folderId == SPPRTreeModel.EVENTS_NODE) {
				if(outerSplitPane.getRightComponent() != getEventsDisplayPanel()) {
					outerSplitPane.setRightComponent(getEventsDisplayPanel());
				}
				getEventsDisplayPanel().setScrollPaneTableCurrentRow((BioEvent)leaf);	// notify right panel about selection change
			} else if(folderId == SPPRTreeModel.ELECTRICAL_MAPPING_NODE) {
				if(outerSplitPane.getRightComponent() != getElectricalMembraneMappingPanel()) {
					outerSplitPane.setRightComponent(getElectricalMembraneMappingPanel());
				}
//				getElectricalMembraneMappingPanel().setScrollPaneTableCurrentRow((BioEvent)leaf);	// notify right panel about selection change
			}
		} else {
			JPanel emptyPanel = new JPanel();
			outerSplitPane.setRightComponent(emptyPanel);
		}
	}
	
	public void setScrollPaneTreeCurrentRow(Object selection) {
		if (selection == null) {
			return;
		}
		BioModelNode node = (BioModelNode)spprTree.getLastSelectedPathComponent();
		Object leaf = null;
		if (node != null) {
			leaf = node.getUserObject();
	    }
		if(selection instanceof SpeciesContext) {
			selectNode(leaf, selection, SPPRTreeModel.INITIAL_CONDITIONS_NODE);
		} else if(selection instanceof ModelParameter) {
			selectNode(leaf, selection, SPPRTreeModel.GLOBAL_PARAMETER_NODE);
		} else if(selection instanceof ReactionStep) {
			selectNode(leaf, selection, SPPRTreeModel.REACTIONS_NODE);
		} else {
			System.out.println(selection.getClass() + " table selection changed");
		}
	}
	private void selectNode(Object oldSelection, Object newSelection, int whatNode) {
		if(oldSelection.equals(newSelection)) {	// already selected
		} else {
			BioModelNode folderRoot = getSpprTreeModel().folderNodes[whatNode];
			BioModelNode foundNode = folderRoot.findNodeByUserObject(newSelection);
			if(foundNode == null) {
				return;
			}
			TreePath treePath = new TreePath(foundNode.getPath());
			spprTree.setSelectionPath(treePath);
		}
	}
		
//------------- Right Panel	-----------------------
	private GeometrySummaryViewer getGeometrySummaryViewer() {
		if (ivjGeometrySummaryViewer == null) {
			try {
				ivjGeometrySummaryViewer = new GeometrySummaryViewer();
				ivjGeometrySummaryViewer.setName("GeometrySummaryViewer");
				ivjGeometrySummaryViewer.setPreferredSize(new Dimension(500,500));
				ivjGeometrySummaryViewer.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						if(evt.getPropertyName().equals(GeometryViewer.SUBVOLCNTRSHP_CHANGED)){
							getStructureMappingCartoonPanel().refreshSubVolumeContainerShapeDisplayImage((BufferedImage)evt.getNewValue());
						}
					}
				});
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjGeometrySummaryViewer;
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
	
	private InitialConditionsPanel getInitialConditionsPanel() {
		if (initialConditionsPanel == null) {
			try {
				initialConditionsPanel = new InitialConditionsPanel(this);
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
				modelParameterPanel = new ModelParameterPanel(this);
				modelParameterPanel.setName("ModelParameterPanel");
				modelParameterPanel.setEditable(false);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return modelParameterPanel;
	}
	
	private ReactionSpecsPanel getReactionSpecsPanel() {
		if (reactionSpecsPanel == null) {
			try {
				reactionSpecsPanel = new ReactionSpecsPanel(this);
				reactionSpecsPanel.setName("ReactionSpecsPanel");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return reactionSpecsPanel;
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
		String eventName = fieldSimulationContext.getFreeEventName();
		try {
			fieldSimulationContext.addBioEvent(new BioEvent(eventName, fieldSimulationContext));
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, "Error adding Event : " + e.getMessage());
		}
	}
	
	public void deleteEvent() {
		DefaultMutableTreeNode currentTreeSelection = (DefaultMutableTreeNode)getSpprTree().getLastSelectedPathComponent();
		if(currentTreeSelection == null){
			return;
		}
		Object selectedObject = currentTreeSelection.getUserObject();;
		if (selectedObject instanceof BioEvent) {
			BioEvent bioEvent = (BioEvent)selectedObject;
			try {
				fieldSimulationContext.removeBioEvent(bioEvent);
			} catch (PropertyVetoException ex) {
				ex.printStackTrace(System.out);
				DialogUtils.showErrorDialog(this, ex.getMessage());
			}
		}		
	}
}
