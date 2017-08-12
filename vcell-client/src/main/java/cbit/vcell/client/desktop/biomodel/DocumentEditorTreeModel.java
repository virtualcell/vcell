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

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public abstract class DocumentEditorTreeModel extends DefaultTreeModel
	implements java.beans.PropertyChangeListener, TreeExpansionListener/*, TreeSelectionListener*/ {

	public static class DocumentEditorTreeFolderNode {
		private DocumentEditorTreeFolderClass folderClass;
		private boolean bBold;
		boolean bSupported = true;
		
		public DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass c) {
			this(c, false);
		}
		public DocumentEditorTreeFolderNode(DocumentEditorTreeFolderClass c, boolean bBold) {
			this.folderClass = c;
			this.bBold = bBold;
		}		
		public boolean isSupported() {
			return bSupported;
		}		
		public void setSupported(boolean bSupported) {
			this.bSupported = bSupported;
		}
		public final String getName() {
			return folderClass.title;
		}
		public final DocumentEditorTreeFolderClass getFolderClass() {
			return folderClass;
		}
		public boolean isBold() {
			return bBold;
		}
		public String toString() {
			return getName();
		}
	}
	
	public enum DocumentEditorTreeFolderClass {
		PATHWAY_NODE(				cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_PATHWAY),
		MODEL_NODE(					cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_MODEL),	
		DATA_NODE(					cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_DATA),	
		APPLICATIONS_NODE(			cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_APPLICATIONS),	
		BIOMODEL_PARAMETERS_NODE(	cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_BIOMODEL_PARAMETERS),	
		SCRIPTING_NODE(				cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_SCRIPTING),

		REACTIONS_NODE(				cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_REACTIONS),
		REACTION_DIAGRAM_NODE(		cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_DIAGRAM),
		STRUCTURES_NODE(			cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_STRUCTURES),
//		STRUCTURE_DIAGRAM_NODE("Structure Diagram"),
		SPECIES_NODE(				cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_SPECIES),
		MOLECULAR_TYPES_NODE(		cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_MOLECULAR_TYPE),
		OBSERVABLES_NODE(			cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_OBSERVABLES),
		
		PATHWAY_DIAGRAM_NODE(		cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_PATHWAYDIAGRAM),
		PATHWAY_OBJECTS_NODE(		cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_PATHWAYOBJECTS),
		BIOPAX_SUMMARY_NODE(		cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_BIOPAXSUMMARY),
		BIOPAX_TREE_NODE(			cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_BIOPAXTREE),
		
		GEOMETRY_NODE(				cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_GEOMETRY),
		SPECIFICATIONS_NODE(		cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_SPECIFICATIONS),
		PROTOCOLS_NODE(				cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_PROTOCOLS),
		SIMULATIONS_NODE(			cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_SIMULATIONS),
		PARAMETER_ESTIMATION_NODE(	cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_PARAMETERESTIMATION),
		
		MATH_ANNOTATION_NODE(		cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_MATH_ANNOTATION),
		MATH_VCML_NODE(				cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_MATH_VCML),
		MATH_GEOMETRY_NODE(			cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_MATH_GEOMETRY),
		MATH_SIMULATIONS_NODE(		cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_MATH_SIMULATIONS),
		MATH_OUTPUT_FUNCTIONS_NODE(	cbit.vcell.client.constants.GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_MATH_OUTPUTFUNCTIONS);
		
		private String title = null;
		DocumentEditorTreeFolderClass(String n) {
			title = n;
		}
		public final String getTitle() {
			return title;
		}
		
	}
	
	protected boolean bPopulatingRoot = false;
	protected BioModelNode rootNode = null;
	protected JTree ownerTree = null;
	private transient java.beans.PropertyChangeSupport propertyChange;
	protected BioModelNode selectedBioModelNode = null;
	protected SelectionManager selectionManager = null;
			
	public DocumentEditorTreeModel(JTree tree) {
		super(new BioModelNode("empty",true),true);
		rootNode = (BioModelNode)root;
		this.ownerTree = tree;
	}	
	
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		try {
			Object source = evt.getSource();			
			if (source == selectionManager) {
				if (evt.getPropertyName().equals(SelectionManager.PROPERTY_NAME_ACTIVE_VIEW)) {					
					onActiveViewChange(selectionManager.getActiveView());
				}
			} 
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	public void treeCollapsed(TreeExpansionEvent e) {
	}
	
	public void treeExpanded(TreeExpansionEvent e) {
	}
	
	protected void restoreTreeSelection() {
		if (bPopulatingRoot) {
			return;
		}
		if (selectedBioModelNode == null || !rootNode.isNodeDescendant(selectedBioModelNode)) {
			selectedBioModelNode = getDefaultSelectionNode();
		}
		
		TreePath path = new TreePath(selectedBioModelNode.getPath());
		if (ownerTree.isPathSelected(path)) {
			return;
		}
		ownerTree.setSelectionPath(path);
		ownerTree.scrollPathToVisible(path);				
	}
	
	protected abstract BioModelNode getDefaultSelectionNode();
	
	private BioModelNode findNode(SimulationContext simulationContext, DocumentEditorTreeFolderClass folderClass) {
		BioModelNode startNode = rootNode;
		if (simulationContext != null) {
			startNode = rootNode.findNodeByUserObject(simulationContext);
		}
		if (folderClass == null) {
			return startNode;
		}
		return findNodeByFolderClass(startNode, folderClass);
	}
	
	private BioModelNode findNodeByFolderClass(BioModelNode startNode, DocumentEditorTreeFolderClass folderClass) {
		Object userObject = startNode.getUserObject();
		if (userObject instanceof DocumentEditorTreeFolderNode && folderClass.equals(((DocumentEditorTreeFolderNode)userObject).getFolderClass())) {
			return startNode;
		}
		for (int i = 0; i < startNode.getChildCount(); i ++) {
			BioModelNode childNode = (BioModelNode) startNode.getChildAt(i);
			BioModelNode node = findNodeByFolderClass(childNode, folderClass);
			if (node != null) {
				return node;
			}
		}
		return null;
	}

	private void onActiveViewChange(ActiveView activeView) {
		SimulationContext simulationContext = activeView.getSimulationContext();
		DocumentEditorTreeFolderClass folderClass = activeView.getDocumentEditorTreeFolderClass();
		BioModelNode node = findNode(simulationContext, folderClass);		
		if (node != null) {
			selectedBioModelNode = node;				
			restoreTreeSelection();
		}		
	}

	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}
	
	private java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}
	
	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
		if (selectionManager != null) {
			selectionManager.removePropertyChangeListener(this);
			selectionManager.addPropertyChangeListener(this);
		}
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		if (!(newValue instanceof String)) {
			return;
		}
		String newName = (String)newValue;
		try {
			if (newName == null || newName.length() == 0) {
				return;
			}
			Object obj = path.getLastPathComponent();
			if (obj == null || !(obj instanceof BioModelNode)) {
				return;
			}
			BioModelNode selectedNode = (BioModelNode) obj;
			Object userObject = selectedNode.getUserObject();
			if (userObject instanceof SimulationContext) {
				((SimulationContext) userObject).setName(newName);
			}
		} catch (Exception ex) {
			DialogUtils.showErrorDialog(ownerTree, ex.getMessage());			
		}
	}
}
