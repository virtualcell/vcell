package cbit.vcell.client.desktop.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.biomodel.SPPRTreeModel.SPPRTreeFolderNode;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.document.SimulationOwner;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.OutputFunctionContext;
import cbit.vcell.solver.Simulation;

public class SimulationListTreeModel extends DefaultTreeModel  implements java.beans.PropertyChangeListener, TreeExpansionListener {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SimulationWorkspace simulationWorkspace = null;
	private BioModelNode rootNode = null;
	private JTree simulationListTree = null;
	
	public static class SimulationListTreeFolderNode {
		private int id;
		private String name;
		boolean bExpanded = false;
		
		public SimulationListTreeFolderNode(int arg_id, String arg_name) {
			id = arg_id;
			name = arg_name;
		}
		public final int getId() {
			return id;
		}
		public final String getName() {
			return name;
		}
		public void setExpanded(boolean expanded) {
			bExpanded = expanded;
		}
		public boolean isExpanded() {
			return bExpanded;
		}
	}
	static final int ROOT_NODE = 100;
	static final int SIMULATIONS_NODE = 0;
	static final int OUTPUT_FUNCTIONS_NODE = 1;
	
	BioModelNode[] folderNodes = null;
	static final int FOLDER_NODE_IDS[] = {
		SIMULATIONS_NODE,
		OUTPUT_FUNCTIONS_NODE,
	};
	static final String FOLDER_NODE_NAMES[] = {
		"Simulations",
		"Output Functions",
	};		
	
	public SimulationListTreeModel(JTree tree) {
		super(new BioModelNode("empty",true),true);
		this.simulationListTree = tree;
	}
	
	public void setSimulationWorkspace(SimulationWorkspace sw) {
		this.simulationWorkspace = sw;
	    refreshListeners();
		createTree();
		nodeStructureChanged(root);
	}
	
	private void createTree() {
		if (simulationWorkspace == null){
			return;
		}
		if(rootNode == null) {
			rootNode = new BioModelNode(simulationWorkspace,true);
			setRoot(rootNode);
		}
		populateTree(ROOT_NODE);
	}

	private void populateTree(int nodeId) {
		BioModelNode toBeSelectedNode = null;
		if (nodeId == ROOT_NODE) {
			rootNode.removeAllChildren();
			folderNodes = new BioModelNode[FOLDER_NODE_NAMES.length];
			for (int i = 0; i < folderNodes.length; i ++) {
				folderNodes[i] = new BioModelNode(new SimulationListTreeFolderNode(FOLDER_NODE_IDS[i], FOLDER_NODE_NAMES[i]));
				rootNode.add(folderNodes[i]);
			}
		} else {
			if (!rootNode.isNodeChild(folderNodes[nodeId])) {
				return;
			}
			TreePath selectionPath = simulationListTree.getSelectionPath();
			if (selectionPath != null) {
				BioModelNode selectedNode = (BioModelNode)selectionPath.getLastPathComponent();
				for (int i = 0; i < folderNodes.length; i ++) {
					if (selectedNode.getUserObject() == folderNodes[i].getUserObject() 
							|| ((BioModelNode)selectedNode.getParent()).getUserObject() == folderNodes[i].getUserObject()) {
						toBeSelectedNode = folderNodes[i];
						break;
					}
				}
			}
		}
		
		if (nodeId == ROOT_NODE || nodeId == SIMULATIONS_NODE) {
			folderNodes[SIMULATIONS_NODE].removeAllChildren();
		    Simulation[] simulations = simulationWorkspace.getSimulations().clone();
		    if(simulations.length > 0) {
		    	Arrays.sort(simulations, new Comparator<Simulation>() {
					public int compare(Simulation o1, Simulation o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
		    	for (Simulation sc : simulations) {
		    		BioModelNode node = new BioModelNode(sc, false);
		    		folderNodes[SIMULATIONS_NODE].add(node);
		    	}
		    }
		}
		
		if (nodeId == ROOT_NODE || nodeId == OUTPUT_FUNCTIONS_NODE) {
			folderNodes[OUTPUT_FUNCTIONS_NODE].removeAllChildren();
		    ArrayList<AnnotatedFunction> outputFunctions = new ArrayList<AnnotatedFunction>(simulationWorkspace.getSimulationOwner().getOutputFunctionContext().getOutputFunctionsList());
		    if(outputFunctions.size() != 0) {
		    	Collections.sort(outputFunctions, new Comparator<AnnotatedFunction>() {
					public int compare(AnnotatedFunction o1, AnnotatedFunction o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
		    	for (AnnotatedFunction outputFunction : outputFunctions) {
		    		BioModelNode node = new BioModelNode(outputFunction, false);
		    		folderNodes[OUTPUT_FUNCTIONS_NODE].add(node);
		    	}
		    }
		}

		if (nodeId == ROOT_NODE) {
			nodeStructureChanged(rootNode);
		} else {
			nodeStructureChanged(folderNodes[nodeId]); 
			restoreTreeExpansion();
		}
		if (toBeSelectedNode != null && rootNode.isNodeChild(toBeSelectedNode)) {
			simulationListTree.setSelectionPath(new TreePath(new Object[] {rootNode, toBeSelectedNode}));
		} else {
			simulationListTree.setSelectionRow(SIMULATIONS_NODE + 1);
		}
	}

	
	// --- event management
	protected java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}
	public synchronized boolean hasListeners(java.lang.String propertyName) {
		return getPropertyChange().hasListeners(propertyName);
	}
	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}
	private void refreshListeners(){
		simulationWorkspace.removePropertyChangeListener(this);
		simulationWorkspace.addPropertyChangeListener(this);
		SimulationOwner simulationOwner = simulationWorkspace.getSimulationOwner();
		simulationOwner.getOutputFunctionContext().removePropertyChangeListener(this);
		simulationOwner.getOutputFunctionContext().addPropertyChangeListener(this);
		simulationOwner.addPropertyChangeListener(this);
		simulationOwner.removePropertyChangeListener(this);
		for (Simulation sim : simulationOwner.getSimulations()) {
			sim.removePropertyChangeListener(this);
			sim.addPropertyChangeListener(this);
		}
	}
	
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		try {
			if (evt.getPropertyName().equals(OutputFunctionContext.PROPERTY_OUTPUT_FUNCTIONS)) {
				populateTree(OUTPUT_FUNCTIONS_NODE);
			}
			if (evt.getPropertyName().equals(GuiConstants.PROPERTY_SIMULATIONS)){
				populateTree(SIMULATIONS_NODE);
			}
			if (evt.getPropertyName().equals(GuiConstants.PROPERTY_SIMULATION_OWNER)) {
				refreshListeners();
				populateTree(ROOT_NODE);
			}
			if (evt.getPropertyName().equals(GuiConstants.PROPERTY_NAME) && evt.getSource() instanceof Simulation) {
				populateTree(SIMULATIONS_NODE);
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	public void treeCollapsed(TreeExpansionEvent e) {
		if (e.getSource() == simulationListTree) {
			TreePath path = e.getPath();
			Object lastComp = ((BioModelNode)path.getLastPathComponent()).getUserObject();
			if (lastComp instanceof SPPRTreeFolderNode) {
				((SimulationListTreeFolderNode)lastComp).setExpanded(false);
			}
		}
	}
	public void treeExpanded(TreeExpansionEvent e) {
		if (e.getSource() == simulationListTree) { 
			TreePath path = e.getPath();
			Object lastComp = ((BioModelNode)path.getLastPathComponent()).getUserObject();
			if (lastComp instanceof SPPRTreeFolderNode) {
				((SimulationListTreeFolderNode)lastComp).setExpanded(true);
			}
		}
	}
	
	public void restoreTreeExpansion() {
		for (int i = 0; i < folderNodes.length; i ++) {
			if (((SimulationListTreeFolderNode)folderNodes[i].getUserObject()).isExpanded()) {
				simulationListTree.expandPath(new TreePath(new Object[]{rootNode, folderNodes[i]}));
			}
		} 
	}

	public void setSelectedValue(Object newValue) {
		if (newValue == null) {
			return;
		}
		BioModelNode folder = null;
		if (newValue instanceof Simulation) {
			folder = folderNodes[SIMULATIONS_NODE];
		} else if (newValue instanceof AnnotatedFunction) {
			folder = folderNodes[OUTPUT_FUNCTIONS_NODE];
		}
		if (folder == null) {
			return;
		}
		BioModelNode leaf = folder.findNodeByUserObject(newValue);
		if (leaf == null) {
			return;
		}
		simulationListTree.setSelectionPath(new TreePath(leaf.getPath()));
	}
}
