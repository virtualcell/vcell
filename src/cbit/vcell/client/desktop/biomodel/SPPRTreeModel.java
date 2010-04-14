package cbit.vcell.client.desktop.biomodel;

import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Model.ModelParameter;

public class SPPRTreeModel extends DefaultTreeModel  implements java.beans.PropertyChangeListener, TreeExpansionListener {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SimulationContext simulationContext = null;
	private BioModelNode rootNode = null;
	private JTree spprTree = null;
	
	public static class SPPRTreeFolderNode {
		private int id;
		private String name;
		boolean bExpanded = false;
		boolean bSupported = true;
		
		public boolean isSupported() {
			return bSupported;
		}		
		public void setSupported(boolean bSupported) {
			this.bSupported = bSupported;
		}
		public SPPRTreeFolderNode(int arg_id, String arg_name) {
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
	static final int GEOMETRY_NODE = 0;
	static final int STRUCTURE_MAPPING_NODE = 1;
	static final int INITIAL_CONDITIONS_NODE = 2;
	static final int GLOBAL_PARAMETER_NODE = 3;
	static final int REACTIONS_NODE = 4;
	static final int EVENTS_NODE = 5;
	static final int ELECTRICAL_MAPPING_NODE = 6 ;
//	static final int RATE_RULES_NODE = 6 ;			// not implemented
//	static final int APP_PARAMETERS_NODE = 7;		// not functional
//	static final int APP_FUNCTIONS_NODE = 8;		// not yet implemented
//	static final int APP_EQUATIONS_NODE = 9;		// not yet inplemented
	
	BioModelNode[] folderNodes = null;
	static final int FOLDER_NODE_IDS[] = {
		GEOMETRY_NODE,
		STRUCTURE_MAPPING_NODE,
		INITIAL_CONDITIONS_NODE, 
		GLOBAL_PARAMETER_NODE,
		REACTIONS_NODE,
		EVENTS_NODE,
		ELECTRICAL_MAPPING_NODE,
//		RATE_RULES_NODE,
//		APP_PARAMETERS_NODE,
//		APP_FUNCTIONS_NODE,
//		APP_EQUATIONS_NODE,
	};
	static final String FOLDER_NODE_NAMES[] = {
		"Geometry",
		"Structure Mapping",
		"Initial Conditions", 
		"Global Parameters",
		"Reactions",
		"Events",
		"Electrical",
//		"Rate Rules",
//		"Application Parameters",
//		"Application Functions",
//		"Application Equations"
	};
		
	static final boolean FOLDER_NODE_IMPLEMENTED[] = {
		true,
		true,
		true,
		true,
		true,
		true,
		true,
//		false,
//		false,
//		false,
//		false
	};
	
	public SPPRTreeModel(JTree tree) {
		super(new BioModelNode("empty",true),true);
		this.spprTree = tree;
	}
	
	public SimulationContext getSimulationContext() {
		return simulationContext;
	}

	public void setSimulationContext(SimulationContext simulationContext) {
		this.simulationContext = simulationContext;
	    refreshListeners();
		createTree();
		nodeStructureChanged(root);
	}
	
	private void createTree() {
		if (getSimulationContext()==null){
			return;
		}
		if(rootNode == null) {
			rootNode = new BioModelNode(getSimulationContext(),true);
			setRoot(rootNode);
		}
		populateTree(ROOT_NODE);
	}

	private void populateTree(int nodeId) {
		boolean bSelected = false;
		if (nodeId == ROOT_NODE) {
			rootNode.removeAllChildren();
			folderNodes = new BioModelNode[FOLDER_NODE_NAMES.length];
			for (int i = 0; i < folderNodes.length; i ++) {
				folderNodes[i] = new BioModelNode(new SPPRTreeFolderNode(FOLDER_NODE_IDS[i], FOLDER_NODE_NAMES[i]), true);
				rootNode.add(folderNodes[i]);
			}
		} else {
			BioModelNode selectedNode = (BioModelNode)spprTree.getSelectionPath().getLastPathComponent();
			if (selectedNode.getUserObject() == folderNodes[nodeId].getUserObject() 
					|| ((BioModelNode)selectedNode.getParent()).getUserObject() == folderNodes[nodeId].getUserObject()) {
				bSelected = true;
			}
		}
		
		if (nodeId == ROOT_NODE || nodeId == INITIAL_CONDITIONS_NODE) {
			folderNodes[INITIAL_CONDITIONS_NODE].removeAllChildren();
		    SpeciesContext[] speciesContexts = getSimulationContext().getModel().getSpeciesContexts().clone();
		    if(speciesContexts.length > 0) {
		    	Arrays.sort(speciesContexts, new Comparator<SpeciesContext>() {
					public int compare(SpeciesContext o1, SpeciesContext o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
		    	for (SpeciesContext sc : speciesContexts) {
		    		BioModelNode node = new BioModelNode(sc, false);
		    		folderNodes[INITIAL_CONDITIONS_NODE].add(node);
		    	}
		    }
		}
		
		if (nodeId == ROOT_NODE || nodeId == GLOBAL_PARAMETER_NODE) {
			folderNodes[GLOBAL_PARAMETER_NODE].removeAllChildren();
		    ModelParameter[] modelParameters = getSimulationContext().getModel().getModelParameters().clone();
		    if(modelParameters.length != 0) {
		    	Arrays.sort(modelParameters, new Comparator<ModelParameter>() {
					public int compare(ModelParameter o1, ModelParameter o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
		    	for (ModelParameter mp : modelParameters) {
		    		BioModelNode node = new BioModelNode(mp, false);
		    		folderNodes[GLOBAL_PARAMETER_NODE].add(node);
		    	}
		    }
		}

		if (nodeId == ROOT_NODE || nodeId == REACTIONS_NODE) {
			folderNodes[REACTIONS_NODE].removeAllChildren();
		    ReactionStep[] reactionSteps = getSimulationContext().getModel().getReactionSteps().clone();
		    if(reactionSteps.length != 0) {
		    	Arrays.sort(reactionSteps, new Comparator<ReactionStep>() {
					public int compare(ReactionStep o1, ReactionStep o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
		    	for (ReactionStep rs : reactionSteps) {
		    		BioModelNode node = new BioModelNode(rs, false);;
		    		folderNodes[REACTIONS_NODE].add(node);
		    	}
		    }
		}
		
		if (nodeId == ROOT_NODE || nodeId == EVENTS_NODE) {
			if ((simulationContext.getGeometry().getDimension() > 0) || (SPPRTreeModel.this.simulationContext.isStoch())) {
				if (rootNode.isNodeChild(folderNodes[EVENTS_NODE])) {
					rootNode.remove(folderNodes[EVENTS_NODE]);
				}
				((SPPRTreeFolderNode)folderNodes[EVENTS_NODE].getUserObject()).setSupported(false);
			} else {
				folderNodes[EVENTS_NODE].removeAllChildren();
				if (getSimulationContext().getBioEvents() != null) {
				    BioEvent[] bioEvents = getSimulationContext().getBioEvents().clone();
				    if(bioEvents.length != 0) {
				    	Arrays.sort(bioEvents, new Comparator<BioEvent>() {
							public int compare(BioEvent o1, BioEvent o2) {
								return o1.getName().compareToIgnoreCase(o2.getName());
							}
						});
				    	for (BioEvent bevnt : bioEvents) {
				    		BioModelNode node = new BioModelNode(bevnt, false);
				    		folderNodes[EVENTS_NODE].add(node);
				    	}
				    }
				}
			}
		}

		if (nodeId == ROOT_NODE) {
			nodeStructureChanged(rootNode);
		} else {
			nodeStructureChanged(folderNodes[nodeId]); 
			restoreTreeExpansion();
			if (bSelected) {
				spprTree.setSelectionPath(new TreePath(new Object[] {rootNode, folderNodes[nodeId]}));
			}
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
		Model model = getSimulationContext().getModel();
		getSimulationContext().removePropertyChangeListener(this);
		getSimulationContext().addPropertyChangeListener(this);
		getSimulationContext().getGeometryContext().removePropertyChangeListener(this);
		getSimulationContext().getGeometryContext().addPropertyChangeListener(this);
		model.removePropertyChangeListener(this);
		model.addPropertyChangeListener(this);
		
		Structure[] structures = model.getStructures();
		if(structures != null) {
			for (int i=0;i<structures.length;i++){
				structures[i].removePropertyChangeListener(this);
				structures[i].addPropertyChangeListener(this);
			}
		}
		
		ReactionStep[] reactionSteps = model.getReactionSteps();
		if(reactionSteps != null) {
			for (int i=0;i<reactionSteps.length;i++){
				reactionSteps[i].removePropertyChangeListener(this);
				reactionSteps[i].getKinetics().removePropertyChangeListener(this);
				
				reactionSteps[i].getKinetics().addPropertyChangeListener(this);
				reactionSteps[i].addPropertyChangeListener(this);
				ReactionParticipant[] reactionParticipants = reactionSteps[i].getReactionParticipants();
				if(reactionParticipants != null) {
					for (int j=0; j<reactionParticipants.length; j++) {
						reactionParticipants[j].removePropertyChangeListener(this);
						reactionParticipants[j].addPropertyChangeListener(this);
					}
				}
			}
		}
				
		SpeciesContext[] speciesContexts = model.getSpeciesContexts();
		if(speciesContexts != null) {
			for (int i=0;i<speciesContexts.length;i++){
				speciesContexts[i].removePropertyChangeListener(this);
				speciesContexts[i].addPropertyChangeListener(this);
			}
		}
		
		Species[] species = model.getSpecies();
		if(species != null) {
			for (int i=0;i<species.length;i++){
				species[i].removePropertyChangeListener(this);
				species[i].addPropertyChangeListener(this);
			}
		}
		
		ModelParameter[] modelParameters = model.getModelParameters();
		if(modelParameters != null) {
			for (int i=0;i<modelParameters.length;i++){
				modelParameters[i].removePropertyChangeListener(this);
				modelParameters[i].addPropertyChangeListener(this);
			}
		}
	}
	
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		try {
			if (evt.getPropertyName().equals("geometry")) {
				if (((Geometry)evt.getNewValue()).getDimension() > 0) {
					((SPPRTreeFolderNode)folderNodes[EVENTS_NODE].getUserObject()).setSupported(false);
				} 
			}
			if (evt.getPropertyName().equals("bioevents")) {
				populateTree(EVENTS_NODE);
			}
			if (evt.getPropertyName().equals("species")){
				populateTree(INITIAL_CONDITIONS_NODE);
				Species oldValue[] = (Species[])evt.getOldValue();
				if (oldValue!=null){
					for (int i = 0; i < oldValue.length; i++){
						oldValue[i].removePropertyChangeListener(this);
					}
				}
				Species newValue[] = (Species[])evt.getNewValue();
				if (newValue!=null){
					for (int i = 0; i < newValue.length; i++){
						newValue[i].addPropertyChangeListener(this);
					}
				}
			} else if (evt.getPropertyName().equals("speciesContexts")){
				populateTree(INITIAL_CONDITIONS_NODE);
				SpeciesContext oldValue[] = (SpeciesContext[])evt.getOldValue();
				if (oldValue!=null){
					for (int i = 0; i < oldValue.length; i++){
						oldValue[i].removePropertyChangeListener(this);
					}
				}
				SpeciesContext newValue[] = (SpeciesContext[])evt.getNewValue();
				if (newValue!=null){
					for (int i = 0; i < newValue.length; i++){
						newValue[i].addPropertyChangeListener(this);
					}
				}
			} else if (evt.getPropertyName().equals("reactionSteps")){
				populateTree(REACTIONS_NODE);
				ReactionStep oldValue[] = (ReactionStep[])evt.getOldValue();
				if (oldValue!=null){
					for (int i = 0; i < oldValue.length; i++){
						oldValue[i].removePropertyChangeListener(this);
					}
				}
				ReactionStep newValue[] = (ReactionStep[])evt.getNewValue();
				if (newValue!=null){
					for (int i = 0; i < newValue.length; i++){
						newValue[i].addPropertyChangeListener(this);
					}
				}
			} else if (evt.getPropertyName().equals("modelParameters")){
				populateTree(GLOBAL_PARAMETER_NODE);
				ModelParameter oldValue[] = (ModelParameter[])evt.getOldValue();
				if (oldValue!=null){
					for (int i = 0; i < oldValue.length; i++){
						oldValue[i].removePropertyChangeListener(this);
					}
				}
				ModelParameter newValue[] = (ModelParameter[])evt.getOldValue();
				if (newValue!=null){
					for (int i = 0; i < newValue.length; i++){
						newValue[i].addPropertyChangeListener(this);
					}
				}
			} else if (evt.getPropertyName().equals("name")){
				Object source = evt.getSource();
				if (source instanceof SpeciesContext || source instanceof ReactionStep
						|| source instanceof Parameter) {
					nodeChanged(rootNode);
				}
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	public void treeCollapsed(TreeExpansionEvent e) {
		if (e.getSource() == spprTree) 
			spprTreeCollapsed(e);
	}
	public void treeExpanded(TreeExpansionEvent e) {
		if (e.getSource() == spprTree) 
			spprTreeExpanded(e);
	}
	
	public void spprTreeCollapsed(TreeExpansionEvent e) {
		TreePath path = e.getPath();
		Object lastComp = ((BioModelNode)path.getLastPathComponent()).getUserObject();
		if (lastComp instanceof SPPRTreeFolderNode) {
			((SPPRTreeFolderNode)lastComp).setExpanded(false);
		}
	}
	
	public void spprTreeExpanded(TreeExpansionEvent e) {
		TreePath path = e.getPath();
		Object lastComp = ((BioModelNode)path.getLastPathComponent()).getUserObject();
		if (lastComp instanceof SPPRTreeFolderNode) {
			((SPPRTreeFolderNode)lastComp).setExpanded(true);
		}
	}
	
	public void restoreTreeExpansion() {
		for (int i = 0; i < folderNodes.length; i ++) {
			if (((SPPRTreeFolderNode)folderNodes[i].getUserObject()).isExpanded()) {
				spprTree.expandPath(new TreePath(new Object[]{rootNode, folderNodes[i]}));
			}
		} 
	}

	public void setSelectedValue(Object newValue) {
		if (newValue == null) {
			return;
		}
		BioModelNode folder = null;
		if (newValue instanceof BioEvent) {
			folder = folderNodes[EVENTS_NODE];
		}		
		BioModelNode leaf = folder.findNodeByUserObject(newValue);
		if (leaf == null) {
			return;
		}
		spprTree.setSelectionPath(new TreePath(leaf.getPath()));
	}
}
