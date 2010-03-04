package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Enumeration;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.biomodel.SPPRTreeModel.SPPRTreeFolderNode;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.gui.InitialConditionsPanel;
import cbit.vcell.mapping.gui.ReactionSpecsPanel;
import cbit.vcell.math.MathDescription;

import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.gui.ModelParameterPanel;
import cbit.vcell.parser.ExpressionException;

public class SPPRPanel extends JPanel {

	private JSplitPane outerSplitPane;
	private javax.swing.JScrollPane treePanel = null;
	private javax.swing.JTree spprTree = null;
	private InitialConditionsPanel initialConditionsPanel;
	private ModelParameterPanel modelParameterPanel;
	private ReactionSpecsPanel reactionSpecsPanel;
	private EventsDisplayPanel eventsDisplayPanel = null;
	private cbit.vcell.mapping.SimulationContext fieldSimulationContext = null;
	private SPPRTreeModel spprTreeModel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
//	private FolderStatus folderStatus = new FolderStatus();
	
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
					MathDescription md = SPPRPanel.this.fieldSimulationContext.getMathDescription();
					if (md != null && !(md.isSpatial() || md.isStoch())) {
						Object selectedObject = currentTreeSelection.getUserObject();;
						Point mousePoint = e.getPoint();
						if (selectedObject instanceof SPPRTreeFolderNode) {
							SPPRTreeFolderNode stfn = (SPPRTreeFolderNode)selectedObject;
							if (stfn.getName().equals("Events")) {
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
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(getMenuItemAddEvent())) {
				addEvent();
			}
			if (e.getSource().equals(getMenuItemDeleteEvent())) {
				deleteEvent();
			}
			
		}	
	};
	
	public static void main(String[] args) {
	}

	public SPPRPanel() {
		super();
		setLayout(new GridBagLayout());
		initialize();
	}
	
	public void setSimulationContext(SimulationContext simulationContext) {
		fieldSimulationContext = simulationContext;
		
		getInitialConditionsPanel().setSimulationContext(fieldSimulationContext);
		getModelParameterPanel().setModel(fieldSimulationContext.getModel());
		getReactionSpecsPanel().setSimulationContext(fieldSimulationContext);
		getEventsDisplayPanel().setSimulationContext(fieldSimulationContext);
		getSpprTreeModel().setSimulationContext(fieldSimulationContext);
		getSpprTree().setSelectionRow(1);
		getSpprTree().expandRow(1);
	}
	
	private void handleException(java.lang.Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}	

	private void initConnections() throws java.lang.Exception {
//		System.out.println("SPPRPanel:  initConnections()");
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

	protected JSplitPane getOuterSplitPane() {
		if (outerSplitPane == null) {
			outerSplitPane = new JSplitPane();
			outerSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			
			outerSplitPane.setLeftComponent(getTreePanel());
			outerSplitPane.setRightComponent(getInitialConditionsPanel());
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
			if(folderId == SPPRTreeModel.INITIAL_CONDITIONS_NODE) {
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
		if(!((SPPRTreeFolderNode) getSpprTreeModel().folderNodes[whatNode].getUserObject()).isExpanded()) {
			return;		// folder of interest is collapsed, nothing to do
		}
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
