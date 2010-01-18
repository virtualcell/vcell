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

import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.gui.InitialConditionsPanel;
import cbit.vcell.mapping.gui.ReactionSpecsPanel;
import cbit.vcell.model.gui.ModelParameterPanel;

public class SPPRPanel extends JPanel {

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
		javax.swing.event.TreeExpansionListener {
		
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SPPRPanel.this.getSpprTree() && (evt.getPropertyName().equals("selectionModel"))) 
				setSelectionModel(getSpprTree().getSelectionModel());
		};
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == SPPRPanel.this.getSelectionModel()) 
				treeValueChanged(e);
		}
		public void mouseClicked(MouseEvent e) {}
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
	};
	
	public static void main(String[] args) {
	}
	
	public class FolderStatus {
		static final int RATERULES_FOLDER = 0;
		static final int REACTIONS_FOLDER = 1;
		static final int APPLICATIONP_FOLDER = 2;
		static final int GLOBALP_FOLDER = 3;
		static final int SPECIES_FOLDER = 4;
		static final int LAST_FOLDER = 4;		// make sure this is equal with the highest folder index
		static final int numFolders = LAST_FOLDER + 1;
		
		private boolean[] collapsedStatus = new boolean[numFolders];
		
		FolderStatus() {
			setRateRulesFolderCollapsed();
			setReactionFolderCollapsed();
			setApplicationParametersFolderCollapsed();
			setGlobalParametersFolderCollapsed();
			setSpeciesContextsFolderCollapsed();
		}
		public boolean isRateRulesFolderCollapsed()				{ return collapsedStatus[RATERULES_FOLDER]; }
		public boolean isReactionFolderCollapsed()				{ return collapsedStatus[REACTIONS_FOLDER]; }
		public boolean isApplicationParametersFolderCollapsed() { return collapsedStatus[APPLICATIONP_FOLDER]; }
		public boolean isGlobalParametersFolderCollapsed()		{ return collapsedStatus[GLOBALP_FOLDER]; }
		public boolean isSpeciesContextsFolderCollapsed()		{ return collapsedStatus[SPECIES_FOLDER]; }
		
		public void setRateRulesFolderCollapsed()				{ collapsedStatus[RATERULES_FOLDER] = true; }
		public void setReactionFolderCollapsed()				{ collapsedStatus[REACTIONS_FOLDER] = true; }
		public void setApplicationParametersFolderCollapsed() 	{ collapsedStatus[APPLICATIONP_FOLDER] = true; }
		public void setGlobalParametersFolderCollapsed()		{ collapsedStatus[GLOBALP_FOLDER] = true; }
		public void setSpeciesContextsFolderCollapsed()			{ collapsedStatus[SPECIES_FOLDER] = true; }

		public void setRateRulesFolderExpanded()				{ collapsedStatus[RATERULES_FOLDER] = false; }
		public void setReactionFolderExpanded()					{ collapsedStatus[REACTIONS_FOLDER] = false; }
		public void setApplicationParametersFolderExpanded() 	{ collapsedStatus[APPLICATIONP_FOLDER] = false; }
		public void setGlobalParametersFolderExpanded()			{ collapsedStatus[GLOBALP_FOLDER] = false; }
		public void setSpeciesContextsFolderExpanded()			{ collapsedStatus[SPECIES_FOLDER] = false; }
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
		System.out.println("SPPRPanel constructor");
	}
	
	public void setSimulationContext(SimulationContext simulationContext) {
		SimulationContext oldValue = fieldSimulationContext;
		fieldSimulationContext = simulationContext;
		
		getInitialConditionsPanel().setSimulationContext(fieldSimulationContext);
		getModelParameterPanel().setModel(fieldSimulationContext.getModel());
		getReactionSpecsPanel().setSimulationContext(fieldSimulationContext);
		
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
//				spprTree.getModel().addTreeModelListener((TreeModelListener) ivjEventHandler);
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
		    if (!node.getAllowsChildren()) {			// it's a leaf, no children
		        String leaf = (String)nodeInfo;
				System.out.print("   leaf: " + leaf);
				BioModelNode parentNode = (BioModelNode) node.getParent();
				nodeInfo =  parentNode.getUserObject();
				String parent = (String)nodeInfo;
				System.out.println("   ... of folder: " + parent);
		        setupRightComponent(parent, leaf);
		    } else {								// it's a folder
		    	String leaf = "";
		        String folder = (String)nodeInfo;
				System.out.println("Folder: " + folder);
		        setupRightComponent(folder, leaf);
		    }
		}catch (Exception ex){
			ex.printStackTrace(System.out);
		}
	}
	
	private void setupRightComponent(String folder, String leaf) {
		if(folder == SPPRTreeModel.SPECIES_FOLDER) {
			//  replace right-side panel only if the correct one is not there already
			if(outerSplitPane.getRightComponent() != getInitialConditionsPanel()) {
				outerSplitPane.setRightComponent(getInitialConditionsPanel());
			}
			getSpprTreeModel().firePropertyChange("spprSpeciesSelection", "", leaf);
		} else if(folder == SPPRTreeModel.GLOBALP_FOLDER) {
			if(outerSplitPane.getRightComponent() != getModelParameterPanel()) {
				outerSplitPane.setRightComponent(getModelParameterPanel());
			}
			getSpprTreeModel().firePropertyChange("spprGlobalSelection", "", leaf);
		} else if(folder == SPPRTreeModel.APPLICATIONP_FOLDER) {
			if(outerSplitPane.getRightComponent() != getModelParameterPanel()) {
				outerSplitPane.setRightComponent(getModelParameterPanel());
			}
			getSpprTreeModel().firePropertyChange("spprApplicationSelection", "", leaf);
		} else if(folder == SPPRTreeModel.REACTIONS_FOLDER) {
			if(outerSplitPane.getRightComponent() != getReactionSpecsPanel()) {
				outerSplitPane.setRightComponent(getReactionSpecsPanel());
			}
			getSpprTreeModel().firePropertyChange("spprReactionsSelection", "", leaf);
		} else if(folder == SPPRTreeModel.RATERULES_FOLDER) {
			outerSplitPane.setRightComponent(null);
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
