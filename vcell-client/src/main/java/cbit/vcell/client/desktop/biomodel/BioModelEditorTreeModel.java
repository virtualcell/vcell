/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.vcell.pathway.PathwayEvent;
import org.vcell.pathway.PathwayListener;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.geometry.GeometryOwner;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.xml.gui.MiriamTreeModel;

@SuppressWarnings("serial")
public class BioModelEditorTreeModel extends DocumentEditorTreeModel implements PathwayListener {

	private BioModel bioModel = null;
	
	enum RunSimulationsNodeID {
		SIMULATIONS_NODE,
		OUTPUT_FUNCTIONS_NODE,
	}
	
	// first Level
	private DocumentEditorTreeFolderNode bioModelChildFolderNodes[] = {
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MODEL_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.APPLICATIONS_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.BIOMODEL_PARAMETERS_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.PATHWAY_NODE, true),
//			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.DATA_NODE, true),
//			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.SCRIPTING_NODE, true),
		};
	private BioModelNode modelNode = new BioModelNode(bioModelChildFolderNodes[0], true);
	private BioModelNode applicationsNode = new BioModelNode(bioModelChildFolderNodes[1], true);	
	private BioModelNode bioModelParametersNode = new BioModelNode(bioModelChildFolderNodes[2], false);
	private BioModelNode pathwayNode = new BioModelNode(bioModelChildFolderNodes[3], true);
//	private BioModelNode dataNode = new BioModelNode(bioModelChildFolderNodes[4], false);	
//	private BioModelNode scriptingNode = new BioModelNode(bioModelChildFolderNodes[4], false);	
	private BioModelNode  bioModelChildNodes[] = {
			modelNode,
			applicationsNode,
			bioModelParametersNode,
			pathwayNode,
//			dataNode,
//			scriptingNode,
	};
	List<BioModelNode> annotationNodes = new ArrayList<BioModelNode>();
	List<BioModelNode> childApplicationsNodeList = new ArrayList<BioModelNode>();

	// Model	
	private DocumentEditorTreeFolderNode modelChildFolderNodes[] = {			
//			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.STRUCTURE_DIAGRAM_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.REACTION_DIAGRAM_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.REACTIONS_NODE, true),			
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.STRUCTURES_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.SPECIES_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MOLECULAR_TYPES_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.OBSERVABLES_NODE, true),
		};	
//	private BioModelNode structureDiagramNode = new BioModelNode(modelChildFolderNodes[0], false); 
	private BioModelNode reactionDiagramNode = new BioModelNode(modelChildFolderNodes[0], false); 
	private BioModelNode reactionsNode = new BioModelNode(modelChildFolderNodes[1], false); 
	private BioModelNode structuresNode = new BioModelNode(modelChildFolderNodes[2], false); 
	private BioModelNode speciesNode = new BioModelNode(modelChildFolderNodes[3], false); 
	private BioModelNode molecularTypeNode = new BioModelNode(modelChildFolderNodes[4], false); 
	private BioModelNode observablesNode = new BioModelNode(modelChildFolderNodes[5], false); 
	private BioModelNode modelChildNodes[] = new BioModelNode[] {
//			structureDiagramNode,
			reactionDiagramNode,
			reactionsNode,
			structuresNode,
			speciesNode,
			molecularTypeNode,
			observablesNode,
	};
	
	// Pathway	
	private DocumentEditorTreeFolderNode pathwayChildFolderNodes[] = {			
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.PATHWAY_DIAGRAM_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.PATHWAY_OBJECTS_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.BIOPAX_SUMMARY_NODE, true),			
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.BIOPAX_TREE_NODE, true),			
		};	
	private BioModelNode pathwayDiagramNode = new BioModelNode(pathwayChildFolderNodes[0], false); 
	private BioModelNode pathwayObjectsNode = new BioModelNode(pathwayChildFolderNodes[1], false); 
	private BioModelNode biopaxSummaryNode = new BioModelNode(pathwayChildFolderNodes[2], false); 
	private BioModelNode pathwayChildNodes[] = new BioModelNode[] {
			pathwayDiagramNode,
			pathwayObjectsNode,
			biopaxSummaryNode,
	};

	private BioModelNode defaultSelectModelNode = reactionDiagramNode;
		
	public BioModelEditorTreeModel(JTree tree) {
		super(tree);
		for (BioModelNode bioModeNode : bioModelChildNodes) {
			rootNode.add(bioModeNode);
		}
		for (BioModelNode bioModeNode : modelChildNodes) {
			modelNode.add(bioModeNode);
		}
		for (BioModelNode bioModeNode : pathwayChildNodes) {
			pathwayNode.add(bioModeNode);
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
			oldValue.getModel().removePropertyChangeListener(this);
			oldValue.getPathwayModel().removePathwayListener(this);
			for (SimulationContext simulationContext : oldValue.getSimulationContexts()) {
				simulationContext.removePropertyChangeListener(this);
				simulationContext.getDataContext().removePropertyChangeListener(this);
			}
		}
		if (newValue != null) {
			newValue.addPropertyChangeListener(this);
			newValue.getModel().addPropertyChangeListener(this);
			newValue.getPathwayModel().addPathwayListener(this);
			for (SimulationContext simulationContext : newValue.getSimulationContexts()) {
				simulationContext.addPropertyChangeListener(this);
				simulationContext.getDataContext().addPropertyChangeListener(this);
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
//			populateAnnotationNode();
			populateApplicationsNode(true);
			nodeStructureChanged(rootNode);
		} finally {
			bPopulatingRoot = false;
		}
		TreePath path = new TreePath(applicationsNode.getPath());	// expand the application node at initialization
		ownerTree.expandPath(path);
		
		ownerTree.expandPath(new TreePath(modelNode.getPath()));
		if (selectedBioModelNode == null) {
			selectedBioModelNode = defaultSelectModelNode;
			ownerTree.setSelectionPath(new TreePath(selectedBioModelNode.getPath()));
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
	
	private void populateApplicationsNode(boolean bFromRoot) {
		if (bioModel.getNumSimulationContexts() == 0 && childApplicationsNodeList.size() == 0) {
			return;
		}
		boolean bSelected = false;
		boolean bFoundSelected = false;
		Map<String, Boolean> selectedInSimulationContextMap = new HashMap<String, Boolean>();
		if (selectedBioModelNode != null && applicationsNode.isNodeDescendant(selectedBioModelNode)) {
			bSelected = true;
			for (BioModelNode node : childApplicationsNodeList) {
				Object userObject = node.getUserObject();
				if (userObject instanceof SimulationContext) {
					SimulationContext simContext = (SimulationContext)userObject;
					boolean nodeDescendant = node.isNodeDescendant(selectedBioModelNode);
					selectedInSimulationContextMap.put(simContext.getName(), nodeDescendant);
				}
			}
		}
		applicationsNode.removeAllChildren();
		childApplicationsNodeList.clear();
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
				childApplicationsNodeList.add(appNode);
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

				BioModelNode geometryNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.GEOMETRY_NODE, true), false);
				BioModelNode settingsNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.SPECIFICATIONS_NODE, true), false);
				BioModelNode protocolsNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.PROTOCOLS_NODE, true), false); 
				BioModelNode simulationsNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.SIMULATIONS_NODE, true), false);
				BioModelNode fittingNode = new BioModelNode(new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.PARAMETER_ESTIMATION_NODE, true), false);
				
				BioModelNode[] applicationChildNodes = null;
				if (simulationContext.isValidForFitting()) {
					simulationContext.createDefaultParameterEstimationTask();
					applicationChildNodes = new BioModelNode[] {
							geometryNode,
							settingsNode,
							protocolsNode,
							simulationsNode,
							fittingNode,
					};					
				} else {
					applicationChildNodes = new BioModelNode[] {
							geometryNode,
							settingsNode,
							protocolsNode,
							simulationsNode,
					};
				}
				for (BioModelNode node : applicationChildNodes) {
					appNode.add(node);
					if (bSelectedInSimulationContext && !bFoundSelected && selectedUserObject instanceof DocumentEditorTreeFolderNode
						&& ((DocumentEditorTreeFolderNode)selectedUserObject).getName().equals(((DocumentEditorTreeFolderNode)node.getUserObject()).getName())) {
						bFoundSelected = true;
						selectedBioModelNode = node;
					}
				}
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
				} else {
					nodeChanged(rootNode);
				}
			} else if (source == bioModel.getModel()) {
				nodeChanged(rootNode);
			} else if (source instanceof SimulationContext) {
				if (evt.getPropertyName().equals(GeometryOwner.PROPERTY_NAME_GEOMETRY)) {
					SimulationContext simulationContext = (SimulationContext)source;
					if (!simulationContext.isValidForFitting()) {
						BioModelNode appNode = applicationsNode.findNodeByUserObject(source);
						for (int i = 0; i < appNode.getChildCount(); i ++) {
							BioModelNode child = (BioModelNode) appNode.getChildAt(i);
							if (child.getUserObject() instanceof DocumentEditorTreeFolderNode) {
								if (((DocumentEditorTreeFolderNode)child.getUserObject()).getFolderClass() == DocumentEditorTreeFolderClass.PARAMETER_ESTIMATION_NODE) {
									appNode.remove(child);
									nodeStructureChanged(appNode);
									restoreTreeSelection();
									return;
								}			
							}
						}
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	@Override
	protected BioModelNode getDefaultSelectionNode() {
		return reactionsNode;
	}

	public void pathwayChanged(PathwayEvent event) {
		nodeChanged(rootNode);
	}
}
