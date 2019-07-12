/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.desktop;

import java.util.StringTokenizer;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelChildSummary.MathType;

import cbit.vcell.desktop.BioModelNode.PublicationInfoNode;
import cbit.vcell.desktop.VCellBasicCellRenderer.VCDocumentInfoNode;
import cbit.vcell.geometry.GeometryInfo;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
/**
 * Insert the type's description here.
 * Creation date: (7/11/19 2:58:23 PM)
 * @author: Dan Vasilescu
 */
@SuppressWarnings("serial")
public class PublicationInfoNodeTreeModel extends javax.swing.tree.DefaultTreeModel {
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private PublicationInfoNode fieldPublicationsInfoNode = null;	// input data
	private BioModelNode publicationNode = null;
	private BioModelNode modelsNode = null;

/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public PublicationInfoNodeTreeModel() {
	super(new BioModelNode("empty",false),true);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

public BioModelNode getPublicationsNode() {
	return publicationNode;
}
public BioModelNode getApplicationsNode() {
	return modelsNode;
}

private BioModelNode createVersionSubTree(BioModelNode publicationsInfoNode) throws DataAccessException {
	BioModelNode versionNode = new BioModelNode("Root", true);
	Object uo = publicationsInfoNode.getUserObject();
	if(!(uo instanceof PublicationInfo)) {
		String title = "PublicationInfo missing";
		publicationNode = new BioModelNode(title, false);
		versionNode.add(publicationNode);
		return versionNode;
	}
	publicationNode = null;
	modelsNode = null;
	
	PublicationInfo pi = (PublicationInfo)uo;

	publicationNode = new BioModelNode("Publication", true);
	publicationNode.setRenderHint("type","PublicationsInfo");

	BioModelNode piTitleNode = new BioModelNode(pi, true);
	piTitleNode.setRenderHint("type","PublicationInfoTitle");
	publicationNode.add(piTitleNode);
	
	BioModelNode piAuthorsNode = new BioModelNode(pi, false);
	piAuthorsNode.setRenderHint("type","PublicationInfoAuthors");
	piTitleNode.add(piAuthorsNode);

	BioModelNode piCitationNode = new BioModelNode(pi, false);
	piCitationNode.setRenderHint("type","PublicationInfoCitation");
	piTitleNode.add(piCitationNode);

	BioModelNode piDoiNode = new BioModelNode(pi, false);
	piDoiNode.setRenderHint("type","PublicationInfoDoi");
	piTitleNode.add(piDoiNode);

	if(pi.getUrl() != null && !pi.getUrl().isEmpty()) {
		if(pi.getUrl().contains("pubmed") || pi.getUrl().contains("PubMed")) {
			BioModelNode piUrlNode = new BioModelNode(pi, false);
			piUrlNode.setRenderHint("type","PublicationInfoUrl");
			piTitleNode.add(piUrlNode);
		}
	}
	versionNode.add(publicationNode);
	
	String name = publicationsInfoNode.getChildCount() > 1 ? "Models" : "Model";
	modelsNode = new BioModelNode(name, true);
	modelsNode.setRenderHint("type","ModelsList");

	for(int i=0; i<publicationsInfoNode.getChildCount(); i++) {
		TreeNode child = publicationsInfoNode.getChildAt(i);
		if(!(child instanceof BioModelNode)) {
			versionNode.add(modelsNode);
			return versionNode;
		}
		BioModelNode modelNode = (BioModelNode)child;
		uo = modelNode.getUserObject();
		if(!(uo instanceof VCDocumentInfoNode)) {
			versionNode.add(modelsNode);
			return versionNode;
		}
		VCDocumentInfoNode din = (VCDocumentInfoNode)uo;
		Version version = din.getVCDocumentInfo().getVersion();
		
		BioModelNode mNameNode = new BioModelNode(version.getName(), true);
		mNameNode.setRenderHint("type","ModelName");
		VCDocumentInfo di = din.getVCDocumentInfo();
		if(di instanceof BioModelInfo) {
			mNameNode.setRenderHint("category","BioModel");
		} else if(di instanceof MathModelInfo) {
			mNameNode.setRenderHint("category","MathModel");
		} else if(di instanceof GeometryInfo) {
			mNameNode.setRenderHint("category","Geometry");
		}
		modelsNode.add(mNameNode);

		BioModelNode mOwnerNode = new BioModelNode(version.getOwner(), true);
		mOwnerNode.setRenderHint("type","ModelOwner");
		mNameNode.add(mOwnerNode);
		
		BioModelNode mDateNode = new BioModelNode(version.getDate(), true);
		mDateNode.setRenderHint("type","ModelDate");
		mNameNode.add(mDateNode);
	}
	
	versionNode.add(modelsNode);
	return versionNode;
}

public void setPublicationsInfoNode(PublicationInfoNode publicationsInfoNode) {
	if (publicationsInfoNode == fieldPublicationsInfoNode) {
		return;
	}
	BioModelNode oldValue = fieldPublicationsInfoNode;
	fieldPublicationsInfoNode = publicationsInfoNode;
	firePropertyChange("publicationInfo", oldValue, publicationsInfoNode);
	refreshTree();
}
public PublicationInfoNode getPublicationsInfoNode() {
	return fieldPublicationsInfoNode;
}

private void refreshTree() {
	if (getPublicationsInfoNode() != null) {
		try {
			PublicationInfoNode pin = getPublicationsInfoNode();
			setRoot(createVersionSubTree(pin));
		}catch (DataAccessException e) {
			e.printStackTrace(System.out);
		}
	} else {
		setRoot(new BioModelNode("empty"));
	}
}
// ---------------------------------------------------------------------------------------------------

public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}

}
