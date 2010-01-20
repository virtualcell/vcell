package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Model.ModelParameter;

public class SPPRTreeModel extends DefaultTreeModel  implements java.beans.PropertyChangeListener {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SimulationContext simulationContext = null;
	BioModelNode rootNode = null;
	SPPRPanel hostPanel = null;
	
	// ATTENTION: make sure that one folder name is not contained in another
	static final String RATERULES_FOLDER = "Rate Rules";					// not functional
	static final String REACTIONS_FOLDER = "Reactions";
	static final String APPLICATIONP_FOLDER = "Application Parameters";		// not functional
	static final String GLOBALP_FOLDER = "Global Parameters";
	static final String SPECIES_FOLDER = "Initial Conditions";
	static final String APP_FUNCTIONS_FOLDER = "Application Functions";		// not yet implemented
	static final String APP_EQUATIONS_FOLDER = "Application Equations";		// not yet inplemented
	
	public SPPRTreeModel(SPPRPanel host) {
		super(new BioModelNode("blabla",true),true);
		this.hostPanel = host;
//		System.out.println("SPPRTreeModel:  param constructor");
	}
	
	public SimulationContext getSimulationContext() {
		return simulationContext;
	}

	public void setSimulationContext(SimulationContext simulationContext) {
		this.simulationContext = simulationContext;
		refreshTree();
		hostPanel.restoreTreeExpansion(hostPanel.getSpprTree());
	}
	
	private void refreshTree() {
		TreePath tp = hostPanel.getSpprTree().getSelectionPath();
		if (getSimulationContext()!=null){
			if(rootNode == null) {
				rootNode = new BioModelNode(getSimulationContext().getName(),true);
//				System.out.println("SPPRTreeModel:  refreshTree()  - NEW root node!!!");
				BioModelNode root = populateTree();
				setRoot(root);
			} else {
//				System.out.println("SPPRTreeModel:  refreshTree()  - same root");
				BioModelNode root = populateTree();
			}
			nodeStructureChanged(root);
			if (tp == null) {
				hostPanel.getSpprTree().setSelectionRow(1);
			}
		} else {
			setRoot(new BioModelNode("empty"));
		}
	}

	private BioModelNode populateTree() {
		//  Root - simulationContext?
//		rootNode = new BioModelNode(getSimulationContext().getName(),true);
	    BioModelNode categoryNode = null;
	    rootNode.removeAllChildren();
	    
	    categoryNode = new BioModelNode(SPPRTreeModel.SPECIES_FOLDER, true);
	    rootNode.add(categoryNode);
	    BioModelNode speciesNode = null;
	    SpeciesContext[] speciesContexts = getSimulationContext().getModel().getSpeciesContexts();
    	ArrayList <String> aList = new ArrayList<String>();
	    for (int i = 0; i < speciesContexts.length; i++) {
	    	aList.add(speciesContexts[i].getName());
		}
	    if(aList != null) {
	    	Collections.sort(aList);
	    	Iterator<String> iterator = aList.iterator();
	    	while(iterator.hasNext()) {
	    		speciesNode = new BioModelNode(iterator.next(), false);
	    		categoryNode.add(speciesNode);
	    	}
	    }
	    
	    categoryNode = new BioModelNode(SPPRTreeModel.GLOBALP_FOLDER, true);
	    rootNode.add(categoryNode);
	    BioModelNode globalParamNode = null;
	    ModelParameter[] modelParameters = getSimulationContext().getModel().getModelParameters();
	    aList = new ArrayList<String>();
	    for (int i = 0; i < modelParameters.length; i++) {
	    	aList.add(modelParameters[i].getName());
		}
	    if(aList != null) {
	    	Collections.sort(aList);
	    	Iterator<String> iterator = aList.iterator();
	    	while(iterator.hasNext()) {
	    		globalParamNode = new BioModelNode(iterator.next(), false);
	    		categoryNode.add(globalParamNode);
	    	}
	    }

//	    categoryNode = new BioModelNode(SPPRTreeModel.APPLICATIONP_FOLDER, true);
//	    rootNode.add(categoryNode);
//	    BioModelNode applnParamNode = null;
//	    applnParamNode = new BioModelNode("an appl param", false);
//	    categoryNode.add(applnParamNode);
//	    applnParamNode = new BioModelNode("a param", false);
//	    categoryNode.add(applnParamNode);
//	    applnParamNode = new BioModelNode("appl param", false);
//	    categoryNode.add(applnParamNode);

	    categoryNode = new BioModelNode(SPPRTreeModel.REACTIONS_FOLDER, true);
	    rootNode.add(categoryNode);
	    BioModelNode reactionsNode = null;
	    ReactionStep[] reactionSteps = getSimulationContext().getModel().getReactionSteps();
	    aList = new ArrayList<String>();
	    for (int i = 0; i < reactionSteps.length; i++) {
	    	aList.add(reactionSteps[i].getName());
		}
	    if(aList != null) {
	    	Collections.sort(aList);
	    	Iterator<String> iterator = aList.iterator();
	    	while(iterator.hasNext()) {
				reactionsNode = new BioModelNode(iterator.next(), false);
				categoryNode.add(reactionsNode);
	    	}
	    }
	    
//	    categoryNode = new BioModelNode(SPPRTreeModel.RATERULES_FOLDER, true);
//	    BioModelNode rateRules = null;
//	    rootNode.add(categoryNode);
	    
	    categoryNode = new BioModelNode(SPPRTreeModel.APP_FUNCTIONS_FOLDER, true);
	    BioModelNode applicationFunctions = null;
	    rootNode.add(categoryNode);
	    categoryNode = new BioModelNode(SPPRTreeModel.APP_EQUATIONS_FOLDER, true);
	    BioModelNode applicationEquations = null;
	    rootNode.add(categoryNode);

	    refreshListeners();
		return rootNode;
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
//		getSimulationContext().getModel().addTreeModelListener(this);
		// Look at cbit.vcell.model.Model.refreshDependencies().
		getSimulationContext().getModel().removePropertyChangeListener(this);
		getSimulationContext().getModel().addPropertyChangeListener(this);
		
		if(getSimulationContext().getModel().getStructures() != null) {
			for (int i=0;i<getSimulationContext().getModel().getStructures().length;i++){
				getSimulationContext().getModel().getStructures()[i].removePropertyChangeListener(this);
				getSimulationContext().getModel().getStructures()[i].addPropertyChangeListener(this);
			}
		}
		
		ReactionStep[] reactionSteps = getSimulationContext().getModel().getReactionSteps();
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

				
		SpeciesContext[] speciesContexts = getSimulationContext().getModel().getSpeciesContexts();
		if(speciesContexts != null) {
			for (int i=0;i<speciesContexts.length;i++){
				speciesContexts[i].removePropertyChangeListener(this);
				speciesContexts[i].addPropertyChangeListener(this);
			}
		}
		
		Species[] species = getSimulationContext().getModel().getSpecies();
		if(species != null) {
			for (int i=0;i<species.length;i++){
				species[i].removePropertyChangeListener(this);
				species[i].addPropertyChangeListener(this);
			}
		}
	}
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		try {
			int[] rows = hostPanel.getSpprTree().getSelectionRows();
			if (evt.getPropertyName().equals("species")){
//	            System.out.println("TreeModel event: species");
				refreshTree();
				hostPanel.restoreTreeExpansion(hostPanel.getSpprTree());
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
//	            System.out.println("TreeModel event: speciesContexts");
				refreshTree();
				hostPanel.restoreTreeExpansion(hostPanel.getSpprTree());
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
//	            System.out.println("TreeModel event: reactionSteps");
				refreshTree();
				hostPanel.restoreTreeExpansion(hostPanel.getSpprTree());
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
			} else if (evt.getPropertyName().equals("reactionParticipants")){
//	            System.out.println("TreeModel event: reactionParticipants");
				refreshTree();
				hostPanel.restoreTreeExpansion(hostPanel.getSpprTree());
				ReactionParticipant oldValue[] = (ReactionParticipant[])evt.getOldValue();
				if (oldValue!=null){
					for (int i = 0; i < oldValue.length; i++){
						oldValue[i].removePropertyChangeListener(this);
					}
				}
				ReactionParticipant newValue[] = (ReactionParticipant[])evt.getNewValue();
				if (newValue!=null){
					for (int i = 0; i < newValue.length; i++){
						newValue[i].addPropertyChangeListener(this);
					}
				}
			} else if (evt.getPropertyName().equals("modelParameters")){
//	            System.out.println("TreeModel event: modelParameter");
				refreshTree();
				hostPanel.restoreTreeExpansion(hostPanel.getSpprTree());
				ModelParameter oldValue[] = (ModelParameter[])evt.getOldValue();
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
			} else if (evt.getPropertyName().equals("name")){
//	            System.out.println("TreeModel event: name");
				refreshTree();
				hostPanel.restoreTreeExpansion(hostPanel.getSpprTree());
			} else {
				// System.out.println("TreeModel untreated event:   " + evt.getPropertyName());
				// + ", old = " + evt.getOldValue() + ", new = " + evt.getNewValue());
			}
			if (rows == null) {
				hostPanel.getSpprTree().setSelectionRow(1);
			} else {
				int rowCount = hostPanel.getSpprTree().getRowCount();
				hostPanel.getSpprTree().setSelectionRow(Math.min(rows[0],  rowCount - 1));
			}
//			hostPanel.getSpprTree().setSelectionPath(tp);
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

}
