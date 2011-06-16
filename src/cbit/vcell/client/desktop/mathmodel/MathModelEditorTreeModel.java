/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.mathmodel;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mathmodel.MathModel;

@SuppressWarnings("serial")
public class MathModelEditorTreeModel extends DocumentEditorTreeModel {
	private MathModel mathModel = null;	
	private DocumentEditorTreeFolderNode mathModelChildFolderNodes[] = {
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MATH_VCML_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MATH_GEOMETRY_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MATH_SIMULATIONS_NODE, true),
			new DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass.MATH_OUTPUT_FUNCTIONS_NODE, true),
		};
	private BioModelNode vcmlNode = new BioModelNode(mathModelChildFolderNodes[0], false);
	private BioModelNode geometryNode = new BioModelNode(mathModelChildFolderNodes[1], false);
	private BioModelNode simulationsNode = new BioModelNode(mathModelChildFolderNodes[2], false);	
	private BioModelNode outputFunctionsNode = new BioModelNode(mathModelChildFolderNodes[3], false);	
	private BioModelNode  mathModelChildNodes[] = {
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
		}
		if (newValue != null) {
			newValue.addPropertyChangeListener(this);
		}		
	}
	
	private void populateRoot() {
		if (mathModel == null){
			return;
		}
		try {
			bPopulatingRoot = true;
			rootNode.setUserObject(mathModel);
			nodeStructureChanged(rootNode);
		} finally {
			bPopulatingRoot = false;
		}	
		if (selectedBioModelNode == null) {
			ownerTree.setSelectionPath(new TreePath(vcmlNode.getPath()));
			selectedBioModelNode = vcmlNode;
		} else {
			restoreTreeSelection();
		}
	}

	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		try {
			super.propertyChange(evt);
			
			String propertyName = evt.getPropertyName();			
			if (propertyName.equals("name")){
				nodeChanged(rootNode);
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
