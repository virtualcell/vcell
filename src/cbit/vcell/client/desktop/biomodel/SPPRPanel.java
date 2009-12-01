package cbit.vcell.client.desktop.biomodel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import cbit.vcell.client.desktop.simulation.SimulationWorkspace;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.gui.InitialConditionsPanel;
import cbit.vcell.mapping.gui.ReactionSpecsPanel;
import cbit.vcell.model.gui.ModelParameterPanel;

public class SPPRPanel extends JPanel {
	private SimulationWorkspace fieldSimulationWorkspace = null;

	private JSplitPane outerSplitPane;
	private javax.swing.JScrollPane treePanel = null;
	private javax.swing.JTree spprTree = null;
	private InitialConditionsPanel initialConditionsPanel;
	private ModelParameterPanel modelParameterPanel;
	private ReactionSpecsPanel reactionSpecsPanel;
	private cbit.vcell.mapping.SimulationContext fieldSimulationContext = null;
	private SPPRTreeModel spprTreeModel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private FolderStatus folderStatus = new FolderStatus();

	private TreeSelectionModel treeSelectionModel = null;

	class IvjEventHandler implements java.beans.PropertyChangeListener, javax.swing.event.TreeSelectionListener, MouseListener, 
		javax.swing.event.TreeExpansionListener, javax.swing.event.TreeModelListener {
		
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SPPRPanel.this.getSpprTree() && (evt.getPropertyName().equals("selectionModel"))) 
				setSelectionModel(getSpprTree().getSelectionModel());
		};
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == SPPRPanel.this.getSelectionModel()) 
				treeValueChanged(e);
		}
		public void mouseClicked(MouseEvent e) {
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void treeCollapsed(TreeExpansionEvent e) {
			if (e.getSource() == spprTree) 
				spprTreeCollapsed(e);
		}
		public void treeExpanded(TreeExpansionEvent e) {
			if (e.getSource() == spprTree) 
				spprTreeExpanded(e);
		}
		public void treeNodesChanged(TreeModelEvent e) {
//			restoreTreeExpansion(getSpprTree());
		}
		public void treeNodesInserted(TreeModelEvent e) {
		}
		public void treeNodesRemoved(TreeModelEvent e) {
		}
		public void treeStructureChanged(TreeModelEvent e) {
//			restoreTreeExpansion(getSpprTree());
		};
	};
	
	public static void main(String[] args) {
	}
	
	public class FolderStatus {
		static final int numFolders = 5;
		private boolean[] collapsedStatus = new boolean[numFolders];
		
		FolderStatus() {
			setRateRulesFolderCollapsed();
			setReactionFolderCollapsed();
			setApplicationParametersFolderCollapsed();
			setGlobalParametersFolderCollapsed();
			setSpeciesContextsFolderCollapsed();
		}
		public boolean isRateRulesFolderCollapsed()				{ return collapsedStatus[0]; }
		public boolean isReactionFolderCollapsed()				{ return collapsedStatus[1]; }
		public boolean isApplicationParametersFolderCollapsed() { return collapsedStatus[2]; }
		public boolean isGlobalParametersFolderCollapsed()		{ return collapsedStatus[3]; }
		public boolean isSpeciesContextsFolderCollapsed()		{ return collapsedStatus[4]; }
		
		public void setRateRulesFolderCollapsed()				{ collapsedStatus[0] = true; }
		public void setReactionFolderCollapsed()				{ collapsedStatus[1] = true; }
		public void setApplicationParametersFolderCollapsed() 	{ collapsedStatus[2] = true; }
		public void setGlobalParametersFolderCollapsed()		{ collapsedStatus[3] = true; }
		public void setSpeciesContextsFolderCollapsed()			{ collapsedStatus[4] = true; }

		public void setRateRulesFolderExpanded()				{ collapsedStatus[0] = false; }
		public void setReactionFolderExpanded()					{ collapsedStatus[1] = false; }
		public void setApplicationParametersFolderExpanded() 	{ collapsedStatus[2] = false; }
		public void setGlobalParametersFolderExpanded()			{ collapsedStatus[3] = false; }
		public void setSpeciesContextsFolderExpanded()			{ collapsedStatus[4] = false; }
	}
	public FolderStatus getFolderStatus() {
		return folderStatus;
	}
	public void setFolderStatus(FolderStatus fs) {
		folderStatus = fs;
	}
	
	public SPPRPanel() {
		super();
		setLayout(new GridBagLayout());
		initialize();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.weighty = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		add(getOuterSplitPane(), gridBagConstraints);
		System.out.println("Test");
	}
	
	public void setSimulationWorkspace(SimulationWorkspace simulationWorkspace) {
		SimulationWorkspace oldValue = fieldSimulationWorkspace;
		fieldSimulationWorkspace = simulationWorkspace;
		
		SimulationContext simContext = (SimulationContext)fieldSimulationWorkspace.getSimulationOwner();
		getInitialConditionsPanel().setSimulationContext(simContext);
		getModelParameterPanel().setModel(simContext.getModel());
		getReactionSpecsPanel().setSimulationContext(simContext);
		setSimulationContext(simContext);

		firePropertyChange("simulationWorkspace", oldValue, simulationWorkspace);
	}
	public SimulationWorkspace getSimulationWorkspace() {
		return fieldSimulationWorkspace;
	}
	public void setSimulationContext(cbit.vcell.mapping.SimulationContext simulationContext) {
		cbit.vcell.mapping.SimulationContext oldValue = fieldSimulationContext;
		fieldSimulationContext = simulationContext;
		firePropertyChange("simulationContext", oldValue, simulationContext);
		getSpprTreeModel().setSimulationContext(fieldSimulationContext);
	}
	
	private void handleException(java.lang.Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}	

	private void initConnections() throws java.lang.Exception {
		System.out.println("SPPRPanel:  initConnections()");
		getSpprTree().setModel(getSpprTreeModel());
		setSelectionModel(getSpprTree().getSelectionModel());
	}	

	private void initialize() {
		try {
			System.out.println("SPPRPanel:  initialize()");
			setName("SPPRPanel");
			setSize(750, 560);
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
	public javax.swing.JTree getSpprTree() {
		if (spprTree == null) {
			try {
				spprTree = new javax.swing.JTree();
				System.out.println("SPPRPanel:  getSpprTree() - NEW tree");
				spprTree.setName("JParameterTree");
				ToolTipManager.sharedInstance().registerComponent(spprTree);
			    spprTree.setCellRenderer(new SPPRTreeCellRenderer());				
				spprTree.setBounds(0, 0, 78, 72);
				spprTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
				spprTree.addTreeSelectionListener((TreeSelectionListener) ivjEventHandler);
				spprTree.addTreeExpansionListener((TreeExpansionListener) ivjEventHandler);
				spprTree.setModel(getSpprTreeModel());
				spprTree.getModel().addTreeModelListener((TreeModelListener) ivjEventHandler);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return spprTree;
	}
	
	private SPPRTreeModel getSpprTreeModel() {
		if (spprTreeModel == null) {
			try {
				spprTreeModel = new SPPRTreeModel(this);
				System.out.println("SPPRPanel:  getSpprTreeModel()  - NEW model");
				FolderStatus fs = new FolderStatus();
				setFolderStatus(fs);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return spprTreeModel;
	}
	
	private boolean isCategoryFolder(TreePath path) {
// TODO: check that it is one of the hardcoded "Category" folders (Species, Parameters, Reactions...)
		return true;
	}
	public void spprTreeCollapsed(TreeExpansionEvent e) {
		System.out.println("Collapsed: " + e.getPath());
		if(!isCategoryFolder(e.getPath())) {
			return;		// not one of the hardcoded folders (whose partent is root)
		}
		if(e.getPath().toString().contains(SPPRTreeModel.RATERULES_FOLDER)) {
			getFolderStatus().setRateRulesFolderCollapsed();
		} else if(e.getPath().toString().contains(SPPRTreeModel.REACTIONS_FOLDER)) {
			getFolderStatus().setReactionFolderCollapsed();
		} else if(e.getPath().toString().contains(SPPRTreeModel.APPLICATIONP_FOLDER)) {
			getFolderStatus().setApplicationParametersFolderCollapsed();
		} else if(e.getPath().toString().contains(SPPRTreeModel.GLOBALP_FOLDER)) {
			getFolderStatus().setGlobalParametersFolderCollapsed();
		} else if(e.getPath().toString().contains(SPPRTreeModel.SPECIES_FOLDER)) {
			getFolderStatus().setSpeciesContextsFolderCollapsed();
		}
	}
	public void spprTreeExpanded(TreeExpansionEvent e) {
		System.out.println("Expanded: " + e.getPath());
		if(e.getPath().toString().contains(SPPRTreeModel.RATERULES_FOLDER)) {
			getFolderStatus().setRateRulesFolderExpanded();
		} else if(e.getPath().toString().contains(SPPRTreeModel.REACTIONS_FOLDER)) {
			getFolderStatus().setReactionFolderExpanded();
		} else if(e.getPath().toString().contains(SPPRTreeModel.APPLICATIONP_FOLDER)) {
			getFolderStatus().setApplicationParametersFolderExpanded();
		} else if(e.getPath().toString().contains(SPPRTreeModel.GLOBALP_FOLDER)) {
			getFolderStatus().setGlobalParametersFolderExpanded();
		} else if(e.getPath().toString().contains(SPPRTreeModel.SPECIES_FOLDER)) {
			getFolderStatus().setSpeciesContextsFolderExpanded();
		}
	}
	
	public void treeValueChanged(TreeSelectionEvent e) {
		try {
			BioModelNode node = (BioModelNode)spprTree.getLastSelectedPathComponent();
			if (node == null) {
				return;
			}
		    Object nodeInfo = node.getUserObject();
		    if (!node.getAllowsChildren()) {
		        String component = (String)nodeInfo;
				System.out.println("   leaf: " + component);
				BioModelNode parentNode = (BioModelNode) node.getParent();
				nodeInfo =  parentNode.getUserObject();
				component = (String)nodeInfo;
				System.out.println("Folder: " + component);
				if(component == SPPRTreeModel.SPECIES_FOLDER) {
					outerSplitPane.setRightComponent(getInitialConditionsPanel());
				} else if(component == SPPRTreeModel.GLOBALP_FOLDER) {
					outerSplitPane.setRightComponent(getModelParameterPanel());
				} else if(component == SPPRTreeModel.APPLICATIONP_FOLDER) {
					outerSplitPane.setRightComponent(getModelParameterPanel());
				} else if(component == SPPRTreeModel.REACTIONS_FOLDER) {
					outerSplitPane.setRightComponent(getReactionSpecsPanel());
				} else if(component == SPPRTreeModel.RATERULES_FOLDER) {
					outerSplitPane.setRightComponent(null);
				}

		    } else {
		        String component = (String)nodeInfo;
				System.out.println("Folder: " + component);
				if(component == SPPRTreeModel.SPECIES_FOLDER) {
					outerSplitPane.setRightComponent(getInitialConditionsPanel());
				} else if(component == SPPRTreeModel.GLOBALP_FOLDER) {
					outerSplitPane.setRightComponent(getModelParameterPanel());
				} else if(component == SPPRTreeModel.APPLICATIONP_FOLDER) {
					outerSplitPane.setRightComponent(getModelParameterPanel());
				} else if(component == SPPRTreeModel.REACTIONS_FOLDER) {
					outerSplitPane.setRightComponent(getReactionSpecsPanel());
				} else if(component == SPPRTreeModel.RATERULES_FOLDER) {
					outerSplitPane.setRightComponent(null);
				}
		    }
		}catch (Exception ex){
			ex.printStackTrace(System.out);
		}
	}
	
	public void restoreTreeExpansion(JTree tree) {
		System.out.println("restoreTreeExpansion()");
		DefaultMutableTreeNode start = (DefaultMutableTreeNode)tree.getModel().getRoot();
		for (Enumeration children = start.children(); children.hasMoreElements();) {
			DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) children.nextElement();
			if (!dtm.isLeaf()) {
				TreePath tp = new TreePath( dtm.getPath() );
				if(tp.toString().contains(SPPRTreeModel.RATERULES_FOLDER)) {
					if(!getFolderStatus().isRateRulesFolderCollapsed()) {
						tree.expandPath(tp);
						System.out.println("     expand RATERULES_FOLDER");
					}
				} else if(tp.toString().contains(SPPRTreeModel.REACTIONS_FOLDER)) {
					if(!getFolderStatus().isReactionFolderCollapsed()) {
						tree.expandPath(tp);
						System.out.println("     expand REACTIONS_FOLDER");
					}
				} else if(tp.toString().contains(SPPRTreeModel.APPLICATIONP_FOLDER)) {
					if(!getFolderStatus().isApplicationParametersFolderCollapsed()) {
						tree.expandPath(tp);
						System.out.println("     expand APPLICATIONP_FOLDER");
					}
				} else if(tp.toString().contains(SPPRTreeModel.GLOBALP_FOLDER)) {
					if(!getFolderStatus().isGlobalParametersFolderCollapsed()) {
						tree.expandPath(tp);
						System.out.println("     expand GLOBALP_FOLDER");
					}
				} else if(tp.toString().contains(SPPRTreeModel.SPECIES_FOLDER)) {
					if(!getFolderStatus().isSpeciesContextsFolderCollapsed()) {
						tree.expandPath(tp);
						System.out.println("     expand SPECIES_FOLDER");
					}
				}
			}
		}
		return; 
	}

	private void setSelectionModel(javax.swing.tree.TreeSelectionModel newValue) {
		if (treeSelectionModel != newValue) {
			try {
				if (treeSelectionModel != null) {
					treeSelectionModel.removeTreeSelectionListener(ivjEventHandler);
//					treeSelectionModel.removeTreeModelListener(ivjEventHandler);
				}
				treeSelectionModel = newValue;

				if (treeSelectionModel != null) {
					treeSelectionModel.addTreeSelectionListener(ivjEventHandler);
//					treeSelectionModel.addTreeModelListener(ivjEventHandler);
				}
				getSpprTree().setSelectionModel(getSelectionModel());
//				restoreTreeExpansion(getSpprTree());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
	}
	private javax.swing.tree.TreeSelectionModel getSelectionModel() {
			return treeSelectionModel;
	}
		
//------------- Right Panel	-----------------------
	
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
	

	
}
