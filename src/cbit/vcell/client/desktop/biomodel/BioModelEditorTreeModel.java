package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.vcell.sybil.models.miriam.MIRIAMQualifier;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEvent;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEventListener;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.document.SimulationOwner;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.OutputFunctionContext;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.modelopt.AnalysisTask;
import cbit.vcell.solver.Simulation;
import cbit.vcell.xml.gui.MiriamTreeModel;

@SuppressWarnings("serial")
public class BioModelEditorTreeModel extends DocumentEditorTreeModel implements AnnotationEventListener {

	private BioModel bioModel = null;
	
	enum ModelNodeID {		
		REACTIONS_NODE,
		STRUCTURES_NODE,
		SPECIES_NODE,
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
	private DocumentEditorTreeFolderNode bioModelChildFolderNodes[] = {
//			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MODELINFO_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.PATHWAY_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MODEL_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.APPLICATTIONS_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.SCRIPTING_NODE, true),
		};
	private BioModelNode pathwayNode = new BioModelNode(bioModelChildFolderNodes[0], false);
	private BioModelNode modelNode = new BioModelNode(bioModelChildFolderNodes[1], true);
	private BioModelNode applicationsNode = new BioModelNode(bioModelChildFolderNodes[2], true);	
	private BioModelNode scriptingNode = new BioModelNode(bioModelChildFolderNodes[3], false);	
	private BioModelNode  bioModelChildNodes[] = {
			pathwayNode,
			modelNode,
			applicationsNode,
			scriptingNode,
	};
	List<BioModelNode> annotationNodes = new ArrayList<BioModelNode>();
	List<BioModelNode> applicationsChildNodes = new ArrayList<BioModelNode>();

	// Model	
	private DocumentEditorTreeFolderNode modelChildFolderNodes[] = {			
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.REACTIONS_NODE, true),			
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.STRUCTURES_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.SPECIES_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.GLOBAL_PARAMETER_NODE, true),			
		};	
	private BioModelNode reactionsNode = new BioModelNode(modelChildFolderNodes[0], true); 
	private BioModelNode structuresNode = new BioModelNode(modelChildFolderNodes[1], true); 
	private BioModelNode speciesNode = new BioModelNode(modelChildFolderNodes[2], true); 
	private BioModelNode globalParametersNode = new BioModelNode(modelChildFolderNodes[3], true); 
	private BioModelNode modelChildNodes[] = new BioModelNode[] {
			reactionsNode,
			structuresNode,
			speciesNode,
			globalParametersNode,
	};
		
	public BioModelEditorTreeModel(JTree tree) {
		super(tree);
		for (BioModelNode bioModeNode : bioModelChildNodes) {
			rootNode.add(bioModeNode);
		}
		for (BioModelNode bioModeNode : modelChildNodes) {
			modelNode.add(bioModeNode);
		}
	}
	
	public void setBioModel(BioModel newValue) {
		if (bioModel == newValue) {
			return;
		}		
		BioModel oldValue = this.bioModel;
		this.bioModel = newValue;
		populateRoot();
		
		if (oldValue != null) {	
			oldValue.removePropertyChangeListener(this);
			oldValue.getVCMetaData().removeAnnotationEventListener(this);
			oldValue.getModel().removePropertyChangeListener(this);
			for (Structure structure : oldValue.getModel().getStructures()){
				structure.removePropertyChangeListener(this);
			}
			for (SpeciesContext speciesContext : oldValue.getModel().getSpeciesContexts()) {
				speciesContext.removePropertyChangeListener(this);
			}
			for (ReactionStep reactionStep : oldValue.getModel().getReactionSteps()){
				reactionStep.removePropertyChangeListener(this);
				reactionStep.getKinetics().removePropertyChangeListener(this);
				for (ReactionParticipant reactionParticipant : reactionStep.getReactionParticipants()) {
					reactionParticipant.removePropertyChangeListener(this);
				}
			}			
			for (ModelParameter modelParameter : oldValue.getModel().getModelParameters()) {
				modelParameter.removePropertyChangeListener(this);
			}
			for (SimulationContext simulationContext : oldValue.getSimulationContexts()) {
				simulationContext.removePropertyChangeListener(this);
				simulationContext.getDataContext().removePropertyChangeListener(this);
				simulationContext.getOutputFunctionContext().removePropertyChangeListener(this);
			}
		}
		if (newValue != null) {
			newValue.addPropertyChangeListener(this);
			newValue.getVCMetaData().addAnnotationEventListener(this);
			newValue.getModel().addPropertyChangeListener(this);
			for (Structure structure : newValue.getModel().getStructures()){
				structure.addPropertyChangeListener(this);
			}
			for (SpeciesContext speciesContext : newValue.getModel().getSpeciesContexts()) {
				speciesContext.addPropertyChangeListener(this);
			}
			for (ReactionStep reactionStep : newValue.getModel().getReactionSteps()){
				reactionStep.getKinetics().addPropertyChangeListener(this);
				reactionStep.addPropertyChangeListener(this);
				for (ReactionParticipant reactionParticipant : reactionStep.getReactionParticipants()) {
					reactionParticipant.addPropertyChangeListener(this);
				}
			}	
			for (ModelParameter modelParameter : newValue.getModel().getModelParameters()) {
				modelParameter.addPropertyChangeListener(this);
			}
			for (SimulationContext simulationContext : newValue.getSimulationContexts()) {
				simulationContext.addPropertyChangeListener(this);
				simulationContext.getDataContext().addPropertyChangeListener(this);
				simulationContext.getOutputFunctionContext().addPropertyChangeListener(this);
			}
		}
	}
	
	private void populateRoot() {
		if (bioModel == null){
			return;
		}
		try {
			bPopulatingRoot = true;
			rootNode.setUserObject(bioModel);
			populateAnnotationNode();
			modelNode.setUserObject(bioModel.getModel());
			populateModelNode(modelNode);
			populateApplicationsNode(true);
			nodeStructureChanged(rootNode);
		} finally {
			bPopulatingRoot = false;
		}
		ownerTree.expandPath(new TreePath(modelNode.getPath()));
		ownerTree.expandPath(new TreePath(applicationsNode.getPath()));
		if (selectedBioModelNode == null) {
			ownerTree.setSelectionPath(new TreePath(reactionsNode.getPath()));
			selectedBioModelNode = reactionsNode;
		} else {
			restoreTreeSelection();
		}
	}

	private void populateAnnotationNode() {
		for (BioModelNode node : annotationNodes) {
			if (node.isNodeDescendant(selectedBioModelNode)) {
				selectedBioModelNode = rootNode;
			}
			rootNode.remove(node);
		}
		annotationNodes.clear();
		
		int childIndex = 0;
		BioModelNode newChild = null;
//		BioModelNode newChild = new BioModelNode(bioModel.getVCMetaData(), false);
//		rootNode.insert(newChild, childIndex ++);
//		annotationNodes.add(newChild);

		Set<MiriamRefGroup> isDescribedByAnnotation = bioModel.getVCMetaData().getMiriamManager().getMiriamRefGroups(bioModel, MIRIAMQualifier.MODEL_isDescribedBy);
		for (MiriamRefGroup refGroup : isDescribedByAnnotation){
			for (MiriamResource miriamResources : refGroup.getMiriamRefs()){
				newChild = new MiriamTreeModel.LinkNode(MIRIAMQualifier.MODEL_isDescribedBy, miriamResources);
				rootNode.insert(newChild, childIndex ++);
				annotationNodes.add(newChild);
			}
		}
		Set<MiriamRefGroup> isAnnotation = bioModel.getVCMetaData().getMiriamManager().getMiriamRefGroups(bioModel, MIRIAMQualifier.MODEL_is);
		for (MiriamRefGroup refGroup : isAnnotation){
			for (MiriamResource miriamResources : refGroup.getMiriamRefs()){
				newChild = new MiriamTreeModel.LinkNode(MIRIAMQualifier.MODEL_is, miriamResources);
				rootNode.insert(newChild, childIndex ++);
				annotationNodes.add(newChild);
			}
		}

	}

	private void populateModelNode(BioModelNode argNode) {
		Model model = bioModel.getModel();
		boolean bFoundSelected = false;
		Object selectedUserObject = null;
		if (selectedBioModelNode != null) {
			selectedUserObject = selectedBioModelNode.getUserObject();
		}
		if (argNode == modelNode || argNode == modelChildNodes[ModelNodeID.STRUCTURES_NODE.ordinal()]) {
			boolean bSelected = false;
			BioModelNode popNode = modelChildNodes[ModelNodeID.STRUCTURES_NODE.ordinal()];
			if (selectedBioModelNode == popNode) {
				bSelected = true;
				bFoundSelected = true;
			} else if (selectedBioModelNode != null && popNode.isNodeDescendant(selectedBioModelNode)) {
				bSelected = true;
			}
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
		    		if (bSelected && !bFoundSelected && selectedUserObject instanceof Structure
		    				&& ((Structure)selectedUserObject).getName().equals(structure.getName())) {
		    			selectedBioModelNode = node;
		    			bFoundSelected = true;
		    		}
		    	}
		    }
		    if (bSelected && !bFoundSelected && selectedUserObject instanceof Structure) {
		    	selectedBioModelNode = popNode;
		    	bFoundSelected = true;
		    }
		}
	    
		if (argNode == modelNode || argNode == modelChildNodes[ModelNodeID.SPECIES_NODE.ordinal()]) {	
			BioModelNode popNode = modelChildNodes[ModelNodeID.SPECIES_NODE.ordinal()];
			boolean bSelected = false;
			if (selectedBioModelNode == popNode) {
				bSelected = true;
				bFoundSelected = true;
			} else if (popNode.isNodeDescendant(selectedBioModelNode)) {
				bSelected = true;
			}
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
		    		if (bSelected && !bFoundSelected && selectedUserObject instanceof SpeciesContext
		    				&& ((SpeciesContext)selectedUserObject).getName().equals(sc.getName())) {
		    			selectedBioModelNode = node;
		    			bFoundSelected = true;
		    		}
		    	}
		    }
		    if (bSelected && !bFoundSelected && selectedUserObject instanceof SpeciesContext) {
		    	selectedBioModelNode = popNode;
		    	bFoundSelected = true;
		    }
		}
	    
		if (argNode == modelNode || argNode == modelChildNodes[ModelNodeID.REACTIONS_NODE.ordinal()]) {
			BioModelNode popNode = modelChildNodes[ModelNodeID.REACTIONS_NODE.ordinal()];
			boolean bSelected = false;
			if (selectedBioModelNode == popNode) {
				bSelected = true;
				bFoundSelected = true;
			}
			if (popNode.isNodeDescendant(selectedBioModelNode)) {
				bSelected = true;
			}
			popNode.removeAllChildren();
			ReactionStep[] reactionSteps = model.getReactionSteps().clone();
		    if(reactionSteps.length > 0) {
		    	Arrays.sort(reactionSteps, new Comparator<ReactionStep>() {
					public int compare(ReactionStep o1, ReactionStep o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
		    	for (ReactionStep rs : reactionSteps) {
		    		BioModelNode node = new BioModelNode(rs, false);;
		    		popNode.add(node);
		    		if (bSelected && !bFoundSelected && selectedUserObject instanceof ReactionStep
		    				&& ((ReactionStep)selectedUserObject).getName().equals(rs.getName())) {
		    			selectedBioModelNode = node;
		    			bFoundSelected = true;
		    		}
		    	}
		    }
		    if (bSelected && !bFoundSelected && selectedUserObject instanceof ReactionStep) {
		    	selectedBioModelNode = popNode;
		    	bFoundSelected = true;
		    }
		}
		
		if (argNode == modelNode || argNode == modelChildNodes[ModelNodeID.GLOBAL_PARAMETER_NODE.ordinal()]) {
			BioModelNode popNode = modelChildNodes[ModelNodeID.GLOBAL_PARAMETER_NODE.ordinal()];
			boolean bSelected = false;
			if (selectedBioModelNode == popNode) {
				bSelected = true;
				bFoundSelected = true;
			}
			if (popNode.isNodeDescendant(selectedBioModelNode)) {
				bSelected = true;
			}
			popNode.removeAllChildren();
		    ModelParameter[] modelParameters = model.getModelParameters().clone();
		    if (modelParameters.length > 0) {
		    	Arrays.sort(modelParameters, new Comparator<ModelParameter>() {
					public int compare(ModelParameter o1, ModelParameter o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
		    	for (ModelParameter mp : modelParameters) {
		    		BioModelNode node = new BioModelNode(mp, false);
		    		popNode.add(node);
		    		if (bSelected && !bFoundSelected && selectedUserObject instanceof ModelParameter
		    				&& ((ModelParameter)selectedUserObject).getName().equals(mp.getName())) {
		    			selectedBioModelNode = node;
		    			bFoundSelected = true;
		    		}
		    	}
		    }
		    if (bSelected && !bFoundSelected && selectedUserObject instanceof ModelParameter) {
		    	selectedBioModelNode = popNode;
		    	bFoundSelected = true;
		    }
		}
		
		nodeStructureChanged(argNode); 
		ownerTree.expandPath(new TreePath(modelNode.getPath()));
		if (argNode != modelNode && bFoundSelected) {
			restoreTreeSelection();
		}
	}
	
	private void populateApplicationsNode(boolean bFromRoot) {
		boolean bSelected = false;
		boolean bFoundSelected = false;
		Map<String, Boolean> selectedInSimulationContextMap = new HashMap<String, Boolean>();
		if (selectedBioModelNode != null && applicationsNode.isNodeDescendant(selectedBioModelNode)) {
			bSelected = true;
			for (BioModelNode node : applicationsChildNodes) {
				Object userObject = node.getUserObject();
				if (userObject instanceof SimulationContext) {
					SimulationContext simContext = (SimulationContext)userObject;
					boolean nodeDescendant = node.isNodeDescendant(selectedBioModelNode);
					selectedInSimulationContextMap.put(simContext.getName(), nodeDescendant);
				}
			}
		}
		applicationsNode.removeAllChildren();
		applicationsChildNodes.clear();
		SimulationContext[] simulationContexts = bioModel.getSimulationContexts();
		if (simulationContexts != null && simulationContexts.length > 0) {
			simulationContexts = simulationContexts.clone();
			Arrays.sort(simulationContexts, new Comparator<SimulationContext>() {
				public int compare(SimulationContext o1, SimulationContext o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});
			for (SimulationContext simulationContext : simulationContexts) {
				BioModelNode appNode = new BioModelNode(simulationContext, true);
				applicationsNode.add(appNode);
				applicationsChildNodes.add(appNode);
				Object selectedUserObject = null;
				if (selectedBioModelNode != null) {
					selectedUserObject = selectedBioModelNode.getUserObject();
				}
				if (bSelected && !bFoundSelected && selectedUserObject instanceof SimulationContext
						&& ((SimulationContext)selectedUserObject).getName().equals(((SimulationContext)appNode.getUserObject()).getName())) {
					bFoundSelected = true;
					selectedBioModelNode = appNode;
				}
				
				Boolean bSelectedInChild = selectedInSimulationContextMap.get(simulationContext.getName());
				boolean bSelectedInSimulationContext = false; 
				if (bSelectedInChild != null) {
					bSelectedInSimulationContext = bSelectedInChild;
				}

				BioModelNode specificationsNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.SPECIFICATIONS_NODE, true), true);
				BioModelNode mathematicsNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MATHEMATICS_NODE, true), false);
				BioModelNode simulationsNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.SIMULATIONS_NODE), true);
				BioModelNode outputFunctionsNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.OUTPUT_FUNCTIONS_NODE), true);
				BioModelNode analysisNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.ANALYSIS_NODE), true); 
				BioModelNode[] applicationsChildNodes = new BioModelNode[] {
						specificationsNode,
						mathematicsNode,
						simulationsNode,
						outputFunctionsNode,
						analysisNode,
				};
				for (BioModelNode node : applicationsChildNodes) {
					appNode.add(node);
					if (bSelectedInSimulationContext && !bFoundSelected && selectedUserObject instanceof DocumentEditorTreeFolderNode
						&& ((DocumentEditorTreeFolderNode)selectedUserObject).getName().equals(((DocumentEditorTreeFolderNode)node.getUserObject()).getName())) {
						bFoundSelected = true;
						selectedBioModelNode = node;
					}
				}
				
				BioModelNode geometryNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.GEOMETRY_NODE), false);
				BioModelNode structureMappingNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.STRUCTURE_MAPPING_NODE), false);
				BioModelNode initialConditionNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.INITIAL_CONDITIONS_NODE), true); 
				BioModelNode reactionsNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.APP_REACTIONS_NODE), true);
				BioModelNode eventsNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.EVENTS_NODE), true);
				BioModelNode electricalNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.ELECTRICAL_MAPPING_NODE), false);
				BioModelNode dataSymbolNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.DATA_SYMBOLS_NODE), true);
				BioModelNode microscopeMeasurmentNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MICROSCOPE_MEASUREMENT_NODE), true);
				
				BioModelNode[] specificationsChildNodes = new BioModelNode[] {
						geometryNode,
						structureMappingNode,
						initialConditionNode,
						reactionsNode,
						eventsNode,
						electricalNode,
						dataSymbolNode,
						microscopeMeasurmentNode,
				};
								
				for (BioModelNode node : specificationsChildNodes) {
					specificationsNode.add(node);
					if (bSelectedInSimulationContext && !bFoundSelected && selectedUserObject instanceof DocumentEditorTreeFolderNode
							&& ((DocumentEditorTreeFolderNode)selectedUserObject).getName().equals(((DocumentEditorTreeFolderNode)node.getUserObject()).getName())) {
						bFoundSelected = true;
						selectedBioModelNode = node;
					}
				}
				
				bFoundSelected = populateApplicationNode(appNode, DocumentEditorTreeFolderClass.INITIAL_CONDITIONS_NODE, bSelectedInSimulationContext, bFoundSelected, true);
				bFoundSelected = populateApplicationNode(appNode, DocumentEditorTreeFolderClass.APP_REACTIONS_NODE, bSelectedInSimulationContext, bFoundSelected, true);
				bFoundSelected = populateApplicationNode(appNode, DocumentEditorTreeFolderClass.EVENTS_NODE, bSelectedInSimulationContext, bFoundSelected, true);
				bFoundSelected = populateApplicationNode(appNode, DocumentEditorTreeFolderClass.DATA_SYMBOLS_NODE, bSelectedInSimulationContext, bFoundSelected, true);
				bFoundSelected = populateApplicationNode(appNode, DocumentEditorTreeFolderClass.MICROSCOPE_MEASUREMENT_NODE, bSelectedInSimulationContext, bFoundSelected, true);
				bFoundSelected = populateApplicationNode(appNode, DocumentEditorTreeFolderClass.SIMULATIONS_NODE, bSelectedInSimulationContext, bFoundSelected, true);
				bFoundSelected = populateApplicationNode(appNode, DocumentEditorTreeFolderClass.OUTPUT_FUNCTIONS_NODE, bSelectedInSimulationContext, bFoundSelected, true);
				bFoundSelected = populateApplicationNode(appNode, DocumentEditorTreeFolderClass.ANALYSIS_NODE, bSelectedInSimulationContext, bFoundSelected, true);
			}
		}
		nodeStructureChanged(applicationsNode);
		ownerTree.expandPath(new TreePath(applicationsNode.getPath()));
		if (bSelected) {
			if (!bFoundSelected) {
				selectedBioModelNode = applicationsNode;
			}
			if (!bFromRoot) {
				restoreTreeSelection();
			}
		}
	}

	private BioModelNode findApplicationChildNode(BioModelNode appNode, DocumentEditorTreeFolderClass folderClass) {
		for (int i = 0; i < appNode.getChildCount(); i ++) {
			BioModelNode node = (BioModelNode) appNode.getChildAt(i);
			Object userObject = node.getUserObject();
			if (userObject instanceof DocumentEditorTreeFolderNode && ((DocumentEditorTreeFolderNode)userObject).getFolderClass() == folderClass) {
				return node;
			}
			if (node.getAllowsChildren()) {
				BioModelNode node1 = findApplicationChildNode(node, folderClass);
				if (node1 != null) {
					return node1;
				}
			}
		}
		return null;
	}
	
	private void populateApplicationNode(SimulationOwner simulationContext, DocumentEditorTreeFolderClass folderClass) {
		for (BioModelNode node : applicationsChildNodes) {
			Object userObject = node.getUserObject();
			if (userObject instanceof SimulationContext && userObject == simulationContext) {
				populateApplicationNode(node, folderClass, false, false, false);
				break;
			}
		}
	}
	
	private boolean populateApplicationNode(BioModelNode appNode, DocumentEditorTreeFolderClass folderClass, boolean bSelected, boolean bFoundSelected, boolean bFromApplications) {
		if (!(appNode.getUserObject() instanceof SimulationContext)) {
			throw new RuntimeException("Application node's user Object must be an instance of SimulationContext");
		}
		SimulationContext simulationContext = (SimulationContext)appNode.getUserObject();
		BioModelNode popNode = findApplicationChildNode(appNode, folderClass);
		if (popNode == null) {
			return false;
		}
		if (!bSelected && selectedBioModelNode != null && popNode.isNodeDescendant(selectedBioModelNode)) {
			bSelected = true;
		}
		popNode.removeAllChildren();
		Object selectedUserObject = null;
		if (selectedBioModelNode != null) {
			selectedUserObject = selectedBioModelNode.getUserObject();
		}
		switch (folderClass) {
		case INITIAL_CONDITIONS_NODE: {
		    SpeciesContextSpec[] speciesContextSpecs = simulationContext.getReactionContext().getSpeciesContextSpecs().clone();
		    if(speciesContextSpecs.length > 0) {
		    	Arrays.sort(speciesContextSpecs, new Comparator<SpeciesContextSpec>() {
					public int compare(SpeciesContextSpec o1, SpeciesContextSpec o2) {
						return o1.getSpeciesContext().getName().compareToIgnoreCase(o2.getSpeciesContext().getName());
					}
				});
		    	for (SpeciesContextSpec scs : speciesContextSpecs) {
		    		BioModelNode node = new BioModelNode(scs, false);
		    		popNode.add(node);
		    		if (bSelected && !bFoundSelected && selectedUserObject instanceof SpeciesContextSpec
		    				&& ((SpeciesContextSpec)selectedUserObject).getSpeciesContext().getName().equals(scs.getSpeciesContext().getName())) {
		    			selectedBioModelNode = node;
		    			bFoundSelected = true;
		    		}
		    	}
		    }
		    if (bSelected && !bFoundSelected && selectedUserObject instanceof SpeciesContextSpec) {
		    	selectedBioModelNode = popNode;
		    	bFoundSelected = true;
		    }
		    break;
		}
		case APP_REACTIONS_NODE: {
		    ReactionSpec[] reactionSpecs = simulationContext.getReactionContext().getReactionSpecs().clone();
		    if(reactionSpecs.length != 0) {
		    	Arrays.sort(reactionSpecs, new Comparator<ReactionSpec>() {
					public int compare(ReactionSpec o1, ReactionSpec o2) {
						return o1.getReactionStep().getName().compareToIgnoreCase(o2.getReactionStep().getName());
					}
				});
		    	for (ReactionSpec rs : reactionSpecs) {
		    		BioModelNode node = new BioModelNode(rs, false);;
		    		popNode.add(node);
		    		if (bSelected && !bFoundSelected && selectedUserObject instanceof ReactionSpec
		    				&& ((ReactionSpec)selectedUserObject).getReactionStep().getName().equals(rs.getReactionStep().getName())) {
		    			selectedBioModelNode = node;
		    			bFoundSelected = true;
		    		}
		    	}
		    }
		    if (bSelected && !bFoundSelected && selectedUserObject instanceof ReactionSpec) {
		    	selectedBioModelNode = popNode;
		    	bFoundSelected = true;
		    }
		    break;
		}
		case EVENTS_NODE: {
			if ((simulationContext.getGeometry().getDimension() > 0) || simulationContext.isStoch()) {
				BioModelNode parentNode = (BioModelNode) popNode.getParent();
				if (popNode.isNodeDescendant(selectedBioModelNode)) {
					selectedBioModelNode = parentNode;
				}
				parentNode.remove(popNode);				
				((DocumentEditorTreeFolderNode)popNode.getUserObject()).setSupported(false);
			} else {
				((DocumentEditorTreeFolderNode)popNode.getUserObject()).setSupported(true);
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
				    		if (bSelected && !bFoundSelected && selectedUserObject instanceof BioEvent
				    				&& ((BioEvent)selectedUserObject).getName().equals(bevnt.getName())) {
				    			selectedBioModelNode = node;
				    			bFoundSelected = true;
				    		}
				    	}
				    }
				    if (bSelected && !bFoundSelected && selectedUserObject instanceof BioEvent) {
				    	selectedBioModelNode = popNode;
				    	bFoundSelected = true;
				    }
				}
			}
			break;
		}
		case DATA_SYMBOLS_NODE: {
		    DataSymbol[] dataSymbol = simulationContext.getDataContext().getDataSymbols().clone();
		    if(dataSymbol.length > 0) {
		    	Arrays.sort(dataSymbol, new Comparator<DataSymbol>() {
					public int compare(DataSymbol o1, DataSymbol o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
		    	for (DataSymbol ds : dataSymbol) {
		    		BioModelNode node = new BioModelNode(ds, false);
		    		popNode.add(node);
		    		if (bSelected && !bFoundSelected && selectedUserObject instanceof DataSymbol
		    				&& ((DataSymbol)selectedUserObject).getName().equals(ds.getName())) {
		    			selectedBioModelNode = node;
		    			bFoundSelected = true;
		    		}
		    	}
		    }
		    if (bSelected && !bFoundSelected && selectedUserObject instanceof DataSymbol) {
		    	selectedBioModelNode = popNode;
		    	bFoundSelected = true;
		    }
		    break;
		}		
		case SIMULATIONS_NODE: {		
		    Simulation[] simulations = simulationContext.getSimulations().clone();
		    if(simulations.length > 0) {
		    	Arrays.sort(simulations, new Comparator<Simulation>() {
					public int compare(Simulation o1, Simulation o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
		    	for (Simulation s : simulations) {
		    		BioModelNode node = new BioModelNode(s, false);
		    		popNode.add(node);
		    		if (bSelected && !bFoundSelected && selectedUserObject instanceof Simulation
		    				&& ((Simulation)selectedUserObject).getName().equals(s.getName())) {
		    			selectedBioModelNode = node;
		    			bFoundSelected = true;
		    		}
		    	}
		    }
		    if (bSelected && !bFoundSelected && selectedUserObject instanceof Simulation) {
		    	selectedBioModelNode = popNode;
		    	bFoundSelected = true;
		    }
		    break;
		}			
		case OUTPUT_FUNCTIONS_NODE: {
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
		    		if (bSelected && !bFoundSelected && selectedUserObject instanceof AnnotatedFunction
		    				&& ((AnnotatedFunction)selectedUserObject).getName().equals(outputFunction.getName())) {
		    			selectedBioModelNode = node;
		    			bFoundSelected = true;
		    		}
		    	}
		    }
		    if (bSelected && !bFoundSelected && selectedUserObject instanceof AnnotatedFunction) {
		    	selectedBioModelNode = popNode;
		    	bFoundSelected = true;
		    }
		    break;
		}
		case ANALYSIS_NODE: {
			if ((simulationContext.getGeometry().getDimension() > 0) || simulationContext.isStoch()) {
				BioModelNode parentNode = (BioModelNode) popNode.getParent();
				if (popNode.isNodeDescendant(selectedBioModelNode)) {
					selectedBioModelNode = parentNode;
				}
				parentNode.remove(popNode);
				((DocumentEditorTreeFolderNode)popNode.getUserObject()).setSupported(false);
			} else {
				AnalysisTask[] analysisTasks = simulationContext.getAnalysisTasks();
				if (analysisTasks != null && analysisTasks.length > 0) {
					analysisTasks = analysisTasks.clone();
					Arrays.sort(analysisTasks, new Comparator<AnalysisTask>() {
						public int compare(AnalysisTask o1, AnalysisTask o2) {
							return o1.getName().compareToIgnoreCase(o2.getName());
						}
					});
					for (AnalysisTask analysisTask : analysisTasks) {
						BioModelNode node = new BioModelNode(analysisTask, false);
						popNode.add(node);
						if (bSelected && !bFoundSelected && selectedUserObject instanceof AnalysisTask
			    				&& ((AnalysisTask)selectedUserObject).getName().equals(analysisTask.getName())) {
			    			selectedBioModelNode = node;
			    			bFoundSelected = true;
			    		}
					}
				}
				if (bSelected && !bFoundSelected && selectedUserObject instanceof AnalysisTask) {
					selectedBioModelNode = popNode;
					bFoundSelected = true;
				}
			}
			break;
		}
		}
		if (!bFromApplications) {
			nodeStructureChanged(popNode);
			if (bSelected && bFoundSelected) {
				restoreTreeSelection();
			}
		}
		return bFoundSelected;
	}
	
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		try {
			super.propertyChange(evt);
			
			Object source = evt.getSource();
			String propertyName = evt.getPropertyName();
			if (propertyName.equals("name")){
				nodeChanged(rootNode);
			} else if (source == bioModel) {
				if (propertyName.equals(BioModel.PROPERTY_NAME_SIMULATION_CONTEXTS)) {
					SimulationContext[] oldValue = (SimulationContext[]) evt.getOldValue();
					if (oldValue != null) {
						for (SimulationContext simulationContext : oldValue) {
							simulationContext.removePropertyChangeListener(this);
						}
					}
					SimulationContext[] newValue = (SimulationContext[]) evt.getNewValue();
					if (newValue != null) {
						for (SimulationContext simulationContext : newValue) {
							simulationContext.addPropertyChangeListener(this);
						}
					}
					populateApplicationsNode(false);
				}
			} else if (source == bioModel.getModel()) {
				if (propertyName.equals(Model.PROPERTY_NAME_SPECIES_CONTEXTS)) {
					populateModelNode(modelChildNodes[ModelNodeID.SPECIES_NODE.ordinal()]);
					SpeciesContext oldValue[] = (SpeciesContext[])evt.getOldValue();
					if (oldValue != null){
						for (SpeciesContext sc : oldValue){
							sc.removePropertyChangeListener(this);
						}
					}
					SpeciesContext newValue[] = (SpeciesContext[])evt.getNewValue();
					if (newValue != null){
						for (SpeciesContext sc : newValue){
							sc.addPropertyChangeListener(this);
						}
					}
				} else if (propertyName.equals(Model.PROPERTY_NAME_STRUCTURES)) {
					populateModelNode(modelChildNodes[ModelNodeID.STRUCTURES_NODE.ordinal()]);
					Structure oldValue[] = (Structure[])evt.getOldValue();
					if (oldValue != null){
						for (Structure s : oldValue){
							s.removePropertyChangeListener(this);
						}
					}
					Structure newValue[] = (Structure[])evt.getNewValue();
					if (newValue != null){
						for (Structure s : newValue){
							s.addPropertyChangeListener(this);
						}
					}
				} else if (propertyName.equals(Model.PROPERTY_NAME_REACTION_STEPS)) {
					populateModelNode(modelChildNodes[ModelNodeID.REACTIONS_NODE.ordinal()]);
					ReactionStep oldValue[] = (ReactionStep[])evt.getOldValue();
					if (oldValue != null){
						for (ReactionStep rs : oldValue){
							rs.removePropertyChangeListener(this);
						}
					}
					ReactionStep newValue[] = (ReactionStep[])evt.getNewValue();
					if (newValue != null){
						for (ReactionStep rs : newValue){
							rs.addPropertyChangeListener(this);
						}
					}
				} else if (propertyName.equals(Model.PROPERTY_NAME_MODEL_PARAMETERS)) {
					populateModelNode(modelChildNodes[ModelNodeID.GLOBAL_PARAMETER_NODE.ordinal()]);
					ModelParameter oldValue[] = (ModelParameter[])evt.getOldValue();
					if (oldValue != null){
						for (ModelParameter rs : oldValue){
							rs.removePropertyChangeListener(this);
						}
					}
					ModelParameter newValue[] = (ModelParameter[])evt.getNewValue();
					if (newValue != null){
						for (ModelParameter rs : newValue){
							rs.addPropertyChangeListener(this);
						}
					}
				}
			} else if (source instanceof SimulationContext) {
				if (propertyName.equals(GuiConstants.PROPERTY_NAME_SIMULATIONS)) {
					populateApplicationNode((SimulationContext)source, DocumentEditorTreeFolderClass.SIMULATIONS_NODE);
				} else if (propertyName.equals(SimulationContext.PROPERTY_NAME_BIOEVENTS)) {
						populateApplicationNode((SimulationContext)source, DocumentEditorTreeFolderClass.EVENTS_NODE);
				} else if (propertyName.equals(SimulationContext.PROPERTY_NAME_ANALYSIS_TASKS)) {
					populateApplicationNode((SimulationContext)source, DocumentEditorTreeFolderClass.ANALYSIS_NODE);
				}
			} else if (source instanceof OutputFunctionContext) {
				populateApplicationNode(((OutputFunctionContext)source).getSimulationOwner(), DocumentEditorTreeFolderClass.OUTPUT_FUNCTIONS_NODE);
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	public void annotationChanged(AnnotationEvent annotationEvent) {
		if (annotationEvent.getIdentifiable() == bioModel) {
			nodeChanged(rootNode);
			if (annotationEvent.isPathwayChange()) {
				ownerTree.setSelectionPath(new TreePath(pathwayNode.getPath()));
			}
		}
	}

	@Override
	protected BioModelNode getDefaultSelectionNode() {
		return reactionsNode;
	}
}
