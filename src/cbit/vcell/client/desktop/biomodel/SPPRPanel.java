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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import cbit.vcell.client.desktop.biomodel.SPPRTreeModel.SPPRTreeFolderNode;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.gui.InitialConditionsPanel;
import cbit.vcell.mapping.gui.ReactionSpecsPanel;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Model.ModelParameter;
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
//	private FolderStatus folderStatus = new FolderStatus();


	class IvjEventHandler implements javax.swing.event.TreeSelectionListener, MouseListener {
		
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == SPPRPanel.this.getSpprTree()) 
				treeValueChanged(e);
		}
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
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
		spprTree.addTreeExpansionListener(getSpprTreeModel());
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
		if (SPPRTreeModel.FOLDER_NODE_IMPLEMENTED[folderId]) {
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
			}
		} else {
			JPanel emptyPanel = new JPanel();
			outerSplitPane.setRightComponent(emptyPanel);
		}
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
				modelParameterPanel = new ModelParameterPanel(true);
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
				reactionSpecsPanel = new ReactionSpecsPanel(true);
				reactionSpecsPanel.setName("ReactionSpecsPanel");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return reactionSpecsPanel;
	}	
}
