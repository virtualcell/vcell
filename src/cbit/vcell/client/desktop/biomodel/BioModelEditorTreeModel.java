package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.vcell.sybil.models.miriam.MIRIAMQualifier;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEvent;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEventListener;
import cbit.vcell.client.desktop.biomodel.BioModelEditor.SelectionEvent;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.solver.Simulation;
import cbit.vcell.xml.gui.MiriamTreeModel;

@SuppressWarnings("serial")
public class BioModelEditorTreeModel extends DefaultTreeModel  
	implements java.beans.PropertyChangeListener, TreeExpansionListener, AnnotationEventListener {

	private BioModel bioModel = null;
	private BioModelNode rootNode = null;
	private JTree ownerTree = null;
		
	public static class BioModelEditorTreeFolderNode {
		private BioModelEditorTreeFolderClass folderClass;
		private String name;
		private boolean bFirstLevel;
		boolean bExpanded = false;
		boolean bSupported = true;
		
		public BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass c, String name) {
			this(c, name, false);
		}
		public BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass c, String name, boolean bFirstLevel) {
			this.folderClass = c;
			this.name = name;
			this.bFirstLevel = bFirstLevel;
		}		
		public boolean isSupported() {
			return bSupported;
		}		
		public void setSupported(boolean bSupported) {
			this.bSupported = bSupported;
		}
		public final String getName() {
			return name;
		}
		public final BioModelEditorTreeFolderClass getFolderClass() {
			return folderClass;
		}
		public void setExpanded(boolean expanded) {
			bExpanded = expanded;
		}
		public boolean isExpanded() {
			return bExpanded;
		}
		public boolean isFirstLevel() {
			return bFirstLevel;
		}
	}
	
	public enum BioModelEditorTreeFolderClass {
		MODEL_NODE,	
		APPLICATTIONS_NODE,		

		STRUCTURES_NODE,
		SPECIES_NODE,		
		REACTIONS_NODE,
		GLOBAL_PARAMETER_NODE,
		
		SPECIFICATIONS_NODE,
		MATHEMATICS_NODE,
		RUN_SIMULATIONS_NODE,
		ANALYSIS_NODE,
		
		GEOMETRY_NODE,
		STRUCTURE_MAPPING_NODE,
		INITIAL_CONDITIONS_NODE,		
		APP_REACTIONS_NODE,
		EVENTS_NODE,
		ELECTRICAL_MAPPING_NODE,
		DATA_SYMBOLS_NODE,
		
		SIMULATIONS_NODE,
		OUTPUT_FUNCTIONS_NODE;		
	}
	
	enum ModelNodeID {		
		STRUCTURES_NODE,
		SPECIES_NODE,
		REACTIONS_NODE,
		GLOBAL_PARAMETER_NODE,
	}
	
	enum ApplicationNodeID {		
		SPECIFICATIONS_NODE,
		MATHEMATICS_NODE,
		RUN_SIMULATIONS_NODE,
		ANALYSIS_NODE,
	}
	
	enum SpecificationNodeID {
		GEOMETRY_NODE,
		STRUCTURE_MAPPING_NODE,
		INITIAL_CONDITIONS_NODE,
		APP_REACTIONS_NODE,
		EVENTS_NODE,
		ELECTRICAL_MAPPING_NODE,
		DATA_SYMBOLS_NODE,	
	}
	
	enum RunSimulationsNodeID {
		SIMULATIONS_NODE,
		OUTPUT_FUNCTIONS_NODE,
	}
	
	// first Level
	private BioModelEditorTreeFolderNode bioModelChildFolderNodes[] = {
			new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.MODEL_NODE, "Biological Model", true),
			new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.APPLICATTIONS_NODE, "Applications", true),
		};
	private BioModelNode modelNode = new BioModelNode(bioModelChildFolderNodes[0], true);
	private BioModelNode applicationsNode = new BioModelNode(bioModelChildFolderNodes[1], true);	
	private BioModelNode  bioModelChildNodes[] = {
			modelNode,
			applicationsNode,
	};

	// Model	
	private BioModelEditorTreeFolderNode modelChildFolderNodes[] = {			
			new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.STRUCTURES_NODE, "Structures", true),
			new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.SPECIES_NODE, "Species", true),
			new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.REACTIONS_NODE, "Reactions", true),			
			new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.GLOBAL_PARAMETER_NODE, "Global Parameters and Rates", true),			
		};	
	private BioModelNode structuresNode = new BioModelNode(modelChildFolderNodes[0], true); 
	private BioModelNode speciesNode = new BioModelNode(modelChildFolderNodes[1], true); 
	private BioModelNode reactionsNode = new BioModelNode(modelChildFolderNodes[2], true); 
	private BioModelNode globalParametersNode = new BioModelNode(modelChildFolderNodes[3], true); 
	private BioModelNode modelChildNodes[] = new BioModelNode[] {
			structuresNode,
			speciesNode,
			reactionsNode,
			globalParametersNode,
	};
		
	public BioModelEditorTreeModel(JTree tree) {
		super(new BioModelNode("empty",true),true);
		rootNode = (BioModelNode)root;
		this.ownerTree = tree;
		for (BioModelNode bioModeNode : bioModelChildNodes) {
			rootNode.add(bioModeNode);
		}
		for (BioModelNode bioModeNode : modelChildNodes) {
			modelNode.add(bioModeNode);
		}
	}
	
	public void setBioModel(BioModel bioModel) {
		this.bioModel = bioModel;
	    refreshListeners();
	    populateRoot();
		nodeStructureChanged(rootNode);
		ownerTree.setSelectionPath(new TreePath(new Object[] {rootNode, modelNode, structuresNode}));
		ownerTree.expandPath(new TreePath(new Object[] {rootNode, applicationsNode}));
	}
	
	private void populateRoot() {
		if (bioModel==null){
			return;
		}
		rootNode.setUserObject(bioModel);
		populateAnnotationNode();
		populateModelNode(modelNode);
		populateApplicationsNode();
	}

	private void populateAnnotationNode() {
		int childIndex = 0;
		rootNode.insert(new BioModelNode(bioModel.getVCMetaData(), false), childIndex ++);

		Set<MiriamRefGroup> isDescribedByAnnotation = bioModel.getVCMetaData().getMiriamManager().getMiriamRefGroups(bioModel, MIRIAMQualifier.MODEL_isDescribedBy);
		for (MiriamRefGroup refGroup : isDescribedByAnnotation){
			for (MiriamResource miriamResources : refGroup.getMiriamRefs()){
				rootNode.insert(new MiriamTreeModel.LinkNode(MIRIAMQualifier.MODEL_isDescribedBy, miriamResources), childIndex ++);
			}
		}
		Set<MiriamRefGroup> isAnnotation = bioModel.getVCMetaData().getMiriamManager().getMiriamRefGroups(bioModel, MIRIAMQualifier.MODEL_is);
		for (MiriamRefGroup refGroup : isAnnotation){
			for (MiriamResource miriamResources : refGroup.getMiriamRefs()){
				rootNode.insert(new MiriamTreeModel.LinkNode(MIRIAMQualifier.MODEL_is, miriamResources), childIndex ++);
			}
		}

	}

	private void populateModelNode(BioModelNode argNode) {
		Model model = bioModel.getModel();		
		if (argNode == modelNode || argNode == modelChildNodes[ModelNodeID.STRUCTURES_NODE.ordinal()]) {
			BioModelNode popNode = modelChildNodes[ModelNodeID.STRUCTURES_NODE.ordinal()];
			popNode.removeAllChildren();
		    Structure[] structures = model.getStructures().clone();
		    if(structures.length > 0) {
		    	Arrays.sort(structures, new Comparator<Structure>() {
					public int compare(Structure o1, Structure o2) {
						if (o1 instanceof Feature && o2 instanceof Membrane) {
							return -1;
						}
						if (o1 instanceof Membrane && o2 instanceof Feature) {
							return 1;
						}
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
		    	for (Structure structure : structures) {
		    		BioModelNode node = new BioModelNode(structure, false);
		    		popNode.add(node);
		    	}
		    }
		}
	    
		if (argNode == modelNode || argNode == modelChildNodes[ModelNodeID.SPECIES_NODE.ordinal()]) {	
			BioModelNode popNode = modelChildNodes[ModelNodeID.SPECIES_NODE.ordinal()];
		    popNode.removeAllChildren();
		    SpeciesContext[] speciesContexts = model.getSpeciesContexts().clone();
		    if(speciesContexts.length > 0) {
		    	Arrays.sort(speciesContexts, new Comparator<SpeciesContext>() {
		    		public int compare(SpeciesContext o1, SpeciesContext o2) {
		    			return o1.getName().compareToIgnoreCase(o2.getName());
		    		}
		    	});
		    	for (SpeciesContext sc : speciesContexts) {
		    		BioModelNode node = new BioModelNode(sc, false);
		    		popNode.add(node);
		    	}
		    }
		}
	    
		if (argNode == modelNode || argNode == modelChildNodes[ModelNodeID.REACTIONS_NODE.ordinal()]) {
			BioModelNode popNode = modelChildNodes[ModelNodeID.REACTIONS_NODE.ordinal()];
			popNode.removeAllChildren();
			ReactionStep[] reactionSteps = model.getReactionSteps().clone();
		    if(reactionSteps.length != 0) {
		    	Arrays.sort(reactionSteps, new Comparator<ReactionStep>() {
					public int compare(ReactionStep o1, ReactionStep o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
		    	for (ReactionStep rs : reactionSteps) {
		    		BioModelNode node = new BioModelNode(rs, false);;
		    		popNode.add(node);
		    	}
		    }
		}
		
		if (argNode == modelNode || argNode == modelChildNodes[ModelNodeID.GLOBAL_PARAMETER_NODE.ordinal()]) {
			BioModelNode popNode = modelChildNodes[ModelNodeID.GLOBAL_PARAMETER_NODE.ordinal()];
			popNode.removeAllChildren();
		    ModelParameter[] modelParameters = model.getModelParameters().clone();
		    if(modelParameters.length != 0) {
		    	Arrays.sort(modelParameters, new Comparator<ModelParameter>() {
					public int compare(ModelParameter o1, ModelParameter o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
		    	for (ModelParameter mp : modelParameters) {
		    		BioModelNode node = new BioModelNode(mp, false);
		    		popNode.add(node);
		    	}
		    }
		}
		
		nodeStructureChanged(argNode); 
		restoreTreeExpansion();
		if (argNode != modelNode) {
			ownerTree.expandPath(new TreePath(new Object[] {rootNode, modelNode, argNode}));			
		}
	}
	
	private void populateApplicationsNode() {		
		SimulationContext[] simContexts = bioModel.getSimulationContexts();
		for (int i = 0; i < simContexts.length; i ++) {
			SimulationContext simContext = simContexts[i];
			BioModelNode appNode = new BioModelNode(simContext, true);
			applicationsNode.add(appNode);
			
			BioModelNode specificationNode = new BioModelNode(new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.SPECIFICATIONS_NODE, "Specifications", true), true); 
			BioModelNode mathematicsNode = new BioModelNode(new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.MATHEMATICS_NODE, "View Math", true), false); 
			BioModelNode runSimulationsNode = new BioModelNode(new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.RUN_SIMULATIONS_NODE, "Run Simulations", true), true); 
			BioModelNode analysisNode = new BioModelNode(new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.ANALYSIS_NODE, "Analysis", true), false); 
			BioModelNode applicationChildNodes[] = new BioModelNode[] {
					specificationNode,
					mathematicsNode,
					runSimulationsNode,
					analysisNode,
			};

			BioModelNode geometryNode = new BioModelNode(new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.GEOMETRY_NODE, "Geometry"), false);
			BioModelNode structureMappingNode = new BioModelNode(new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.STRUCTURE_MAPPING_NODE, "Structure Mapping"), false);
			BioModelNode initialConditionNode = new BioModelNode(new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.INITIAL_CONDITIONS_NODE, "Initial Conditions"), true); 
			BioModelNode reactionsNode = new BioModelNode(new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.APP_REACTIONS_NODE, "Reactions"), true);
			BioModelNode eventsNode = new BioModelNode(new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.EVENTS_NODE, "Events"), true);
			BioModelNode electricalNode = new BioModelNode(new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.ELECTRICAL_MAPPING_NODE, "Electrical"), false);
			BioModelNode dataSymbolNode = new BioModelNode(new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.DATA_SYMBOLS_NODE, "Data Symbols"), true);
			
			BioModelNode[] specificationChildNodes = new BioModelNode[] {
					geometryNode,
					structureMappingNode,
					initialConditionNode,
					reactionsNode,
					eventsNode,
					electricalNode,
					dataSymbolNode,
			};	
			
			BioModelNode simulationsNode = new BioModelNode(new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.SIMULATIONS_NODE, "Simulations"), true);
			BioModelNode outputFunctionsNode = new BioModelNode(new BioModelEditorTreeFolderNode(BioModelEditorTreeFolderClass.OUTPUT_FUNCTIONS_NODE, "Output Functions"), true);
			BioModelNode[] runSimulationsChildNodes = new BioModelNode[] {
					simulationsNode,
					outputFunctionsNode,
			};
			for (BioModelNode node : applicationChildNodes) {
				appNode.add(node);
			}
			for (BioModelNode node : specificationChildNodes) {
				specificationNode.add(node);				
			}
			for (BioModelNode node : runSimulationsChildNodes) {
				runSimulationsNode.add(node);				
			}
			
			populationApplicationNode(simContext, applicationChildNodes, specificationChildNodes, runSimulationsChildNodes);
		}
	}
	
	private void populationApplicationNode(SimulationContext simulationContext,
			BioModelNode[] applicationChildNodes, BioModelNode[] specificationChildNodes, BioModelNode[] runSimulationsChildNodes) {		
		//if (popNode == specificationNode || popNode == initialConditionNode) {
		BioModelNode popNode = specificationChildNodes[SpecificationNodeID.INITIAL_CONDITIONS_NODE.ordinal()];		
		popNode.removeAllChildren();
	    SpeciesContext[] speciesContexts = simulationContext.getModel().getSpeciesContexts().clone();
	    if(speciesContexts.length > 0) {
	    	Arrays.sort(speciesContexts, new Comparator<SpeciesContext>() {
				public int compare(SpeciesContext o1, SpeciesContext o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});
	    	for (SpeciesContext sc : speciesContexts) {
	    		BioModelNode node = new BioModelNode(sc, false);
	    		popNode.add(node);
	    	}
	    }
		//}
		
		//if (popNode == specificationNode || popNode == dataSymbolNode) {
		popNode = specificationChildNodes[SpecificationNodeID.DATA_SYMBOLS_NODE.ordinal()];		
		popNode.removeAllChildren();
		    DataSymbol[] dataSymbol = simulationContext.getDataContext().getDataSymbols().clone();
		    if(dataSymbol.length > 0) {
		    	Arrays.sort(dataSymbol, new Comparator<DataSymbol>() {
					public int compare(DataSymbol o1, DataSymbol o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
		    	for (DataSymbol sc : dataSymbol) {
		    		BioModelNode node = new BioModelNode(sc, false);
		    		popNode.add(node);
		    	}
		    }
		//}

		    popNode = specificationChildNodes[SpecificationNodeID.APP_REACTIONS_NODE.ordinal()];	
		//if (popNode == specificationNode || popNode == reactionsNode) {
		    popNode.removeAllChildren();
		    ReactionStep[] reactionSteps = simulationContext.getModel().getReactionSteps().clone();
		    if(reactionSteps.length != 0) {
		    	Arrays.sort(reactionSteps, new Comparator<ReactionStep>() {
					public int compare(ReactionStep o1, ReactionStep o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
		    	for (ReactionStep rs : reactionSteps) {
		    		BioModelNode node = new BioModelNode(rs, false);;
		    		popNode.add(node);
		    	}
		    }
		//}
		
		    popNode = specificationChildNodes[SpecificationNodeID.EVENTS_NODE.ordinal()];
		    BioModelNode specificationNode = applicationChildNodes[ApplicationNodeID.SPECIFICATIONS_NODE.ordinal()];
		//if (popNode == specificationNode || popNode == eventsNode) {
			if ((simulationContext.getGeometry().getDimension() > 0) || simulationContext.isStoch()) {
				specificationNode.remove(popNode);				
				((BioModelEditorTreeFolderNode)popNode.getUserObject()).setSupported(false);
			} else {
				((BioModelEditorTreeFolderNode)popNode.getUserObject()).setSupported(true);
				popNode.removeAllChildren();
				if (simulationContext.getBioEvents() != null) {
				    BioEvent[] bioEvents = simulationContext.getBioEvents().clone();
				    if(bioEvents.length != 0) {
				    	Arrays.sort(bioEvents, new Comparator<BioEvent>() {
							public int compare(BioEvent o1, BioEvent o2) {
								return o1.getName().compareToIgnoreCase(o2.getName());
							}
						});
				    	for (BioEvent bevnt : bioEvents) {
				    		BioModelNode node = new BioModelNode(bevnt, false);
				    		popNode.add(node);
				    	}
				    }
				}
			}
		//}
			
		    popNode = runSimulationsChildNodes[RunSimulationsNodeID.SIMULATIONS_NODE.ordinal()];	
			//if (popNode == runSimulationsNode || popNode == simulationsNode) {
			    popNode.removeAllChildren();
			    Simulation[] simulations = simulationContext.getSimulations().clone();
			    if(simulations.length > 0) {
			    	Arrays.sort(simulations, new Comparator<Simulation>() {
						public int compare(Simulation o1, Simulation o2) {
							return o1.getName().compareToIgnoreCase(o2.getName());
						}
					});
			    	for (Simulation sc : simulations) {
			    		BioModelNode node = new BioModelNode(sc, false);
			    		popNode.add(node);
			    	}
			    }
			//}
			
			    popNode = runSimulationsChildNodes[RunSimulationsNodeID.OUTPUT_FUNCTIONS_NODE.ordinal()];	
			//if (popNode == runSimulationsNode|| popNode == outputFunctionsNode) {
			    popNode.removeAllChildren();
			    ArrayList<AnnotatedFunction> outputFunctions = new ArrayList<AnnotatedFunction>(simulationContext.getOutputFunctionContext().getOutputFunctionsList());
			    if(outputFunctions.size() != 0) {
			    	Collections.sort(outputFunctions, new Comparator<AnnotatedFunction>() {
						public int compare(AnnotatedFunction o1, AnnotatedFunction o2) {
							return o1.getName().compareToIgnoreCase(o2.getName());
						}
					});
			    	for (AnnotatedFunction outputFunction : outputFunctions) {
			    		BioModelNode node = new BioModelNode(outputFunction, false);
			    		popNode.add(node);
			    	}
			    }
			//}			
	}

	private void populateNode(BioModelNode popNode) {
		BioModelNode toBeSelectedNode = null;
		if (popNode == rootNode) {
			return;
		} 		
		
		if (!rootNode.isNodeChild(popNode)) {
			return;
		}
		TreePath selectionPath = ownerTree.getSelectionPath();		

		nodeStructureChanged(popNode); 
		restoreTreeExpansion();
		
		if (toBeSelectedNode != null && rootNode.isNodeChild(toBeSelectedNode)) {
			ownerTree.setSelectionPath(new TreePath(new Object[] {rootNode, toBeSelectedNode}));
		}
	}

	
//	// --- event management
//	protected java.beans.PropertyChangeSupport getPropertyChange() {
//		if (propertyChange == null) {
//			propertyChange = new java.beans.PropertyChangeSupport(this);
//		};
//		return propertyChange;
//	}
//	public synchronized boolean hasListeners(java.lang.String propertyName) {
//		return getPropertyChange().hasListeners(propertyName);
//	}
//	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
//		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
//	}
//	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
//		getPropertyChange().addPropertyChangeListener(listener);
//	}
//	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
//		getPropertyChange().removePropertyChangeListener(listener);
//	}
	private void refreshListeners(){
		if (bioModel == null) {
			return;
		}
		Model model = bioModel.getModel();
//		getSimulationContext().removePropertyChangeListener(this);
//		getSimulationContext().addPropertyChangeListener(this);
//		getSimulationContext().getGeometryContext().removePropertyChangeListener(this);
//		getSimulationContext().getGeometryContext().addPropertyChangeListener(this);
//		getSimulationContext().getDataContext().removePropertyChangeListener(this);
//		getSimulationContext().getDataContext().addPropertyChangeListener(this);
		model.removePropertyChangeListener(this);
		model.addPropertyChangeListener(this);
		
		bioModel.getVCMetaData().removeAnnotationEventListener(this);
		bioModel.getVCMetaData().addAnnotationEventListener(this);
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
			Object source = evt.getSource();
			String propertyName = evt.getPropertyName();
			if (source == bioModel.getModel()) {
//				if (evt.getPropertyName().equals(Model.PROPERTY_NAME_SPECIES)) {
//					Species oldValue[] = (Species[])evt.getOldValue();
//					if (oldValue != null){
//						for (Species s : oldValue){
//							s.removePropertyChangeListener(this);
//						}
//					}
//					Species newValue[] = (Species[])evt.getNewValue();
//					if (newValue != null){
//						for (Species s : newValue){
//							s.addPropertyChangeListener(this);
//						}
//					}
//				}
				if (propertyName.equals(Model.PROPERTY_NAME_SPECIES_CONTEXTS)) {
					populateModelNode(modelChildNodes[ModelNodeID.SPECIES_NODE.ordinal()]);
					SpeciesContext oldValue[] = (SpeciesContext[])evt.getOldValue();
					if (oldValue!=null){
						for (SpeciesContext sc : oldValue){
							sc.removePropertyChangeListener(this);
						}
					}
					SpeciesContext newValue[] = (SpeciesContext[])evt.getNewValue();
					if (newValue!=null){
						for (SpeciesContext sc : newValue){
							sc.addPropertyChangeListener(this);
						}
					}
				} else if (propertyName.equals(Model.PROPERTY_NAME_STRUCTURES)) {
					populateModelNode(modelChildNodes[ModelNodeID.STRUCTURES_NODE.ordinal()]);
					Structure oldValue[] = (Structure[])evt.getOldValue();
					if (oldValue!=null){
						for (Structure s : oldValue){
							s.removePropertyChangeListener(this);
						}
					}
					Structure newValue[] = (Structure[])evt.getNewValue();
					if (newValue!=null){
						for (Structure s : newValue){
							s.addPropertyChangeListener(this);
						}
					}
				} else if (propertyName.equals(Model.PROPERTY_NAME_REACTION_STEPS)) {
					populateModelNode(modelChildNodes[ModelNodeID.REACTIONS_NODE.ordinal()]);
					ReactionStep oldValue[] = (ReactionStep[])evt.getOldValue();
					if (oldValue!=null){
						for (ReactionStep rs : oldValue){
							rs.removePropertyChangeListener(this);
						}
					}
					ReactionStep newValue[] = (ReactionStep[])evt.getNewValue();
					if (newValue!=null){
						for (ReactionStep rs : newValue){
							rs.addPropertyChangeListener(this);
						}
					}
				}
//			if (evt.getPropertyName().equals("geometry")) {
//				if (((Geometry)evt.getNewValue()).getDimension() > 0) {
//					((ApplicationFolderNode)eventsNode.getUserObject()).setSupported(false);
//					populateNode(eventsNode);
//				} 
//			}
//			if (evt.getPropertyName().equals("bioevents")) {
//				populateNode(eventsNode);
//			}
//			if (evt.getPropertyName().equals("species")){
//				populateNode(initialConditionNode);
//				Species oldValue[] = (Species[])evt.getOldValue();
//				if (oldValue!=null){
//					for (int i = 0; i < oldValue.length; i++){
//						oldValue[i].removePropertyChangeListener(this);
//					}
//				}
//				Species newValue[] = (Species[])evt.getNewValue();
//				if (newValue!=null){
//					for (int i = 0; i < newValue.length; i++){
//						newValue[i].addPropertyChangeListener(this);
//					}
//				}
//			} else if (evt.getPropertyName().equals("speciesContexts")){
//				populateNode(initialConditionNode);
//				SpeciesContext oldValue[] = (SpeciesContext[])evt.getOldValue();
//				if (oldValue!=null){
//					for (int i = 0; i < oldValue.length; i++){
//						oldValue[i].removePropertyChangeListener(this);
//					}
//				}
//				SpeciesContext newValue[] = (SpeciesContext[])evt.getNewValue();
//				if (newValue!=null){
//					for (int i = 0; i < newValue.length; i++){
//						newValue[i].addPropertyChangeListener(this);
//					}
//				}
//			} else if (evt.getPropertyName().equals("dataSymbols")){
//				populateNode(dataSymbolNode);
//				DataSymbol oldValue[] = (DataSymbol[])evt.getOldValue();
//				if (oldValue!=null){
//					for (int i = 0; i < oldValue.length; i++){
//						oldValue[i].removePropertyChangeListener(this);
//					}
//				}
//				DataSymbol newValue[] = (DataSymbol[])evt.getNewValue();
//				if (newValue!=null){
//					for (int i = 0; i < newValue.length; i++){
//						newValue[i].addPropertyChangeListener(this);
//					}
//				}
//			} else if (evt.getPropertyName().equals("reactionSteps")){
//				populateNode(reactionsNode);
//				ReactionStep oldValue[] = (ReactionStep[])evt.getOldValue();
//				if (oldValue!=null){
//					for (int i = 0; i < oldValue.length; i++){
//						oldValue[i].removePropertyChangeListener(this);
//					}
//				}
//				ReactionStep newValue[] = (ReactionStep[])evt.getNewValue();
//				if (newValue!=null){
//					for (int i = 0; i < newValue.length; i++){
//						newValue[i].addPropertyChangeListener(this);
//					}
//				}
//			} else if (evt.getPropertyName().equals("modelParameters")){
//				populateNode(globalParameterNode);
//				ModelParameter oldValue[] = (ModelParameter[])evt.getOldValue();
//				if (oldValue!=null){
//					for (int i = 0; i < oldValue.length; i++){
//						oldValue[i].removePropertyChangeListener(this);
//					}
//				}
//				ModelParameter newValue[] = (ModelParameter[])evt.getOldValue();
//				if (newValue!=null){
//					for (int i = 0; i < newValue.length; i++){
//						newValue[i].addPropertyChangeListener(this);
//					}
//				}
			} else if (propertyName.equals("name")){
				nodeChanged(rootNode);
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	public void treeCollapsed(TreeExpansionEvent e) {
		if (e.getSource() == ownerTree) {
			TreePath path = e.getPath();
			Object lastComp = ((BioModelNode)path.getLastPathComponent()).getUserObject();
			if (lastComp instanceof BioModelEditorTreeFolderNode) {
				((BioModelEditorTreeFolderNode)lastComp).setExpanded(false);
			}
		}
	}
	
	public void treeExpanded(TreeExpansionEvent e) {
		if (e.getSource() == ownerTree) {
			TreePath path = e.getPath();
			Object lastComp = ((BioModelNode)path.getLastPathComponent()).getUserObject();
			if (lastComp instanceof BioModelEditorTreeFolderNode) {
				((BioModelEditorTreeFolderNode)lastComp).setExpanded(true);
			}
		}
	}
	
	public void restoreTreeExpansion() {
		for (int i = 0; i < rootNode.getChildCount(); i ++) {
			BioModelNode node = (BioModelNode)rootNode.getChildAt(i);
			if (node.getAllowsChildren() && ((BioModelEditorTreeFolderNode)node.getUserObject()).isExpanded()) {
				ownerTree.expandPath(findTreePath(node));
			}
		}
	}
	
	private TreePath findTreePath(TreeNode node) {
		LinkedList<TreeNode> nodeList = new LinkedList<TreeNode>();
		TreeNode n = node;
		while (true) {
			if (n == null) {
				break;
			}
			nodeList.add(0, n);
			n = n.getParent();
		}
		return new TreePath(nodeList.toArray(new Object[0]));
	}

	public void setSelectedValue(SelectionEvent newValue) {
		if (newValue.getSelectedObject() == null) {
			return;
		}
		BioModelNode nodeToSearch = rootNode;
		if (newValue.getSelectedContainer() != null) {
			nodeToSearch = rootNode.findNodeByUserObject(newValue.getSelectedContainer());			
		}
		BioModelNode leaf = nodeToSearch.findNodeByUserObject(newValue.getSelectedObject());
		ownerTree.setSelectionPath(new TreePath(leaf.getPath()));
	}

	public void annotationChanged(AnnotationEvent annotationEvent) {
		nodeChanged(rootNode);
	}
}
