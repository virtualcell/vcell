package cbit.vcell.client.desktop.mathmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.OutputFunctionContext;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.solver.Simulation;

@SuppressWarnings("serial")
public class MathModelEditorTreeModel extends DocumentEditorTreeModel {
	private MathModel mathModel = null;	
	private DocumentEditorTreeFolderNode mathModelChildFolderNodes[] = {
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MATH_ANNOTATION_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MATH_VCML_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MATH_GEOMETRY_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MATH_SIMULATIONS_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MATH_OUTPUT_FUNCTIONS_NODE, true),
		};
	private BioModelNode annotationNode = new BioModelNode(mathModelChildFolderNodes[0], false);
	private BioModelNode vcmlNode = new BioModelNode(mathModelChildFolderNodes[1], false);
	private BioModelNode geometryNode = new BioModelNode(mathModelChildFolderNodes[2], false);
	private BioModelNode simulationsNode = new BioModelNode(mathModelChildFolderNodes[3], true);	
	private BioModelNode outputFunctionsNode = new BioModelNode(mathModelChildFolderNodes[4], true);	
	private BioModelNode  mathModelChildNodes[] = {
			annotationNode,
			vcmlNode,
			geometryNode,
			simulationsNode,
			outputFunctionsNode,
	};
		
	public MathModelEditorTreeModel(JTree tree) {
		super(tree);
		for (BioModelNode bioModeNode : mathModelChildNodes) {
			rootNode.add(bioModeNode);
		}
	}
	
	public void setMathModel(MathModel newValue) {
		if (mathModel == newValue) {
			return;
		}
		MathModel oldValue = this.mathModel;
		mathModel = newValue;
		populateRoot();
		
		if (oldValue != null) {	
			oldValue.removePropertyChangeListener(this);
			oldValue.getOutputFunctionContext().removePropertyChangeListener(this);
		}
		if (newValue != null) {
			newValue.addPropertyChangeListener(this);
			newValue.getOutputFunctionContext().addPropertyChangeListener(this);
		}		
	}
	
	private void populateRoot() {
		if (mathModel == null){
			return;
		}
		try {
			bPopulatingRoot = true;
			rootNode.setUserObject(mathModel);
			populateNode(DocumentEditorTreeFolderClass.MATH_SIMULATIONS_NODE);
			populateNode(DocumentEditorTreeFolderClass.MATH_OUTPUT_FUNCTIONS_NODE);
			nodeStructureChanged(rootNode);
		} finally {
			bPopulatingRoot = false;
		}	
		ownerTree.expandPath(new TreePath(simulationsNode.getPath()));
		if (selectedBioModelNode == null) {
			ownerTree.setSelectionPath(new TreePath(vcmlNode.getPath()));
			selectedBioModelNode = vcmlNode;
		} else {
			restoreTreeSelection();
		}
	}
	
	private void populateNode(DocumentEditorTreeFolderClass folderClass) {
		boolean bSelected = false;	
		boolean bFoundSelected = false;	
		BioModelNode popNode = null;
		if (folderClass == DocumentEditorTreeFolderClass.MATH_SIMULATIONS_NODE) {
			popNode = simulationsNode;
		} else if (folderClass == DocumentEditorTreeFolderClass.MATH_OUTPUT_FUNCTIONS_NODE) {
			popNode = outputFunctionsNode;
		}
		if (selectedBioModelNode != null && popNode.isNodeDescendant(selectedBioModelNode)) {
			bSelected = true;
		}
		Object selectedUserObject = null;
		if (selectedBioModelNode != null) {
			selectedUserObject = selectedBioModelNode.getUserObject();
		}
		popNode.removeAllChildren();
		if (folderClass == DocumentEditorTreeFolderClass.MATH_SIMULATIONS_NODE) {
		    Simulation[] simulations = mathModel.getSimulations().clone();
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
		} else if (folderClass == DocumentEditorTreeFolderClass.MATH_OUTPUT_FUNCTIONS_NODE) {
			ArrayList<AnnotatedFunction> outputFunctions = new ArrayList<AnnotatedFunction>(mathModel.getOutputFunctionContext().getOutputFunctionsList());
		    if (outputFunctions.size() != 0) {
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
		}
		
	    nodeStructureChanged(popNode);
		if (bSelected && bFoundSelected) {
			restoreTreeSelection();
		}
	}

	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		try {
			super.propertyChange(evt);
			
			Object source = evt.getSource();
			String propertyName = evt.getPropertyName();			
			if (propertyName.equals("name")){
				nodeChanged(rootNode);
			} else if (source == mathModel) {
				if (propertyName.equals(GuiConstants.PROPERTY_NAME_SIMULATIONS)) {
					populateNode(DocumentEditorTreeFolderClass.MATH_SIMULATIONS_NODE);
				}
			} else if (source instanceof OutputFunctionContext) {
				populateNode(DocumentEditorTreeFolderClass.MATH_OUTPUT_FUNCTIONS_NODE);
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	@Override
	protected BioModelNode getDefaultSelectionNode() {
		return vcmlNode;
	}
}
