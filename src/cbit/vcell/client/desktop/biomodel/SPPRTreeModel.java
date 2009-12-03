package cbit.vcell.client.desktop.biomodel;

import javax.swing.tree.DefaultTreeModel;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.desktop.Annotation;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.Species;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.Product;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.solver.Simulation;

public class SPPRTreeModel extends DefaultTreeModel  implements java.beans.PropertyChangeListener {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SimulationContext simulationContext = null;
	BioModelNode rootNode = null;
	SPPRPanel hostPanel = null;
	
	// ATTENTION: make sure that one folder name is not contained in another
	static final String RATERULES_FOLDER = "Rate Rules";
	static final String REACTIONS_FOLDER = "Reactions";
	static final String APPLICATIONP_FOLDER = "Application Parameters";
	static final String GLOBALP_FOLDER = "Global Parameters";
	static final String SPECIES_FOLDER = "Species Contexts";
	
	
	public SPPRTreeModel() {
		super(new BioModelNode("blabla",true),true);
		System.out.println("SPPRTreeModel:  constructor");
	}
	public SPPRTreeModel(SPPRPanel host) {
		super(new BioModelNode("blabla",true),true);
		this.hostPanel = host;
		System.out.println("SPPRTreeModel:  param constructor");
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
		if (getSimulationContext()!=null){
			if(rootNode == null) {
				rootNode = new BioModelNode(getSimulationContext().getName(),true);
				System.out.println("SPPRTreeModel:  refreshTree()  - NEW root node!!!");
				BioModelNode root = populateTree();
				setRoot(root);
			} else {
				System.out.println("SPPRTreeModel:  refreshTree()  - same root");
				BioModelNode root = populateTree();
			}
			nodeStructureChanged(root);
			
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
	    for (int i = 0; i < speciesContexts.length; i++) {
			speciesNode = new BioModelNode(speciesContexts[i].getName(), false);
			categoryNode.add(speciesNode);
		}
	    
	    categoryNode = new BioModelNode(SPPRTreeModel.GLOBALP_FOLDER, true);
	    rootNode.add(categoryNode);
	    BioModelNode globalParamNode = null;
	    ModelParameter[] modelParameters = getSimulationContext().getModel().getModelParameters();
	    for (int i = 0; i < modelParameters.length; i++) {
			globalParamNode = new BioModelNode(modelParameters[i].getName(), false);
			categoryNode.add(globalParamNode);
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
	    for (int i = 0; i < reactionSteps.length; i++) {
			reactionsNode = new BioModelNode(reactionSteps[i].getName(), false);
			categoryNode.add(reactionsNode);
		}
	    
//	    categoryNode = new BioModelNode(SPPRTreeModel.RATERULES_FOLDER, true);
//	    BioModelNode rateRules = null;
//	    rootNode.add(categoryNode);

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
	public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
		getPropertyChange().firePropertyChange(evt);
	}
	public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}
	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}
	public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}
	public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(propertyName, listener);
	}
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}
	public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(propertyName, listener);
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
		
		if(getSimulationContext().getModel().getReactionSteps() != null) {
			for (int i=0;i<getSimulationContext().getModel().getReactionSteps().length;i++){
				getSimulationContext().getModel().getReactionSteps()[i].removePropertyChangeListener(this);
				getSimulationContext().getModel().getReactionSteps()[i].getKinetics().removePropertyChangeListener(this);
				
				getSimulationContext().getModel().getReactionSteps()[i].getKinetics().addPropertyChangeListener(this);
				getSimulationContext().getModel().getReactionSteps()[i].addPropertyChangeListener(this);
//				try {	// ???
//					getSimulationContext().getModel().getReactionSteps()[i].rebindAllToModel(getSimulationContext().getModel());
//				}catch (Exception e){
//					e.printStackTrace(System.out);
//				}
				if(getSimulationContext().getModel().getReactionSteps()[i].getReactionParticipants() != null) {
					for (int j=0; j<getSimulationContext().getModel().getReactionSteps()[i].getReactionParticipants().length; j++) {
						getSimulationContext().getModel().getReactionSteps()[i].getReactionParticipants()[j].removePropertyChangeListener(this);
						getSimulationContext().getModel().getReactionSteps()[i].getReactionParticipants()[j].addPropertyChangeListener(this);
					}
				}
			}
		}

				
		if(getSimulationContext().getModel().getSpeciesContexts() != null) {
			for (int i=0;i<getSimulationContext().getModel().getSpeciesContexts().length;i++){
				getSimulationContext().getModel().getSpeciesContexts()[i].removePropertyChangeListener(this);
				getSimulationContext().getModel().getSpeciesContexts()[i].addPropertyChangeListener(this);
			}
		}
		
		if(getSimulationContext().getModel().getSpecies() != null) {
			for (int i=0;i<getSimulationContext().getModel().getSpecies().length;i++){
				getSimulationContext().getModel().getSpecies()[i].removePropertyChangeListener(this);
				getSimulationContext().getModel().getSpecies()[i].addPropertyChangeListener(this);
			}
		}
	}
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		try {
			if (evt.getPropertyName().equals("species")){
	            System.out.println("TreeModel event: species");
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
	            System.out.println("TreeModel event: speciesContexts");
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
	            System.out.println("TreeModel event: reactionSteps");
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
	            System.out.println("TreeModel event: reactionParticipants");
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
	            System.out.println("TreeModel event: modelParameter");
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
	            System.out.println("TreeModel event: name");
				refreshTree();
				hostPanel.restoreTreeExpansion(hostPanel.getSpprTree());
			} else {
				// System.out.println("TreeModel untreated event:   " + evt.getPropertyName());
				// + ", old = " + evt.getOldValue() + ", new = " + evt.getNewValue());
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

}
