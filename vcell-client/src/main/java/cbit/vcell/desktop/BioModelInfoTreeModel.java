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
import javax.swing.tree.TreePath;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelChildSummary.MathType;

import cbit.vcell.geometry.GeometryInfo;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class BioModelInfoTreeModel extends javax.swing.tree.DefaultTreeModel {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private BioModelInfo fieldBioModelInfo = null;
	private BioModelNode generalInfoNode = null;
	private BioModelNode publicationsInfoNode = null;
	private BioModelNode applicationsNode = null;
	private boolean showGeneralInfoNode = false;
/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public BioModelInfoTreeModel() {
	super(new BioModelNode("empty",false),true);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

public BioModelNode getGeneralNode() {
	return generalInfoNode;
}
public BioModelNode getPublicationsNode() {
	return publicationsInfoNode;
}
public BioModelNode getApplicationsNode() {
	return applicationsNode;
}
public void showDatabaseFileInfo(boolean bShow) {
	showGeneralInfoNode = bShow;
}

/**
 * Insert the method's description here.
 * Creation date: (11/28/00 2:41:43 PM)
 * @param bioModelNode cbit.vcell.desktop.BioModelNode
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
private BioModelNode createVersionSubTree(BioModelInfo bioModelInfo) throws DataAccessException {
	BioModelNode versionNode = new BioModelNode(bioModelInfo, true);
	generalInfoNode = null;
	publicationsInfoNode = null;
	applicationsNode = null;
	
	if(showGeneralInfoNode) {
		String name = "Database File Info";
		generalInfoNode = new BioModelNode(name, true);
		generalInfoNode.setRenderHint("type","GeneralFileInfo");
		
		Version version = bioModelInfo.getVersion();
				
		BioModelNode mNameNode = new BioModelNode(version.getName(), true);
		mNameNode.setRenderHint("type","ModelName");
		mNameNode.setRenderHint("category","BioModel");
		generalInfoNode.add(mNameNode);
	
		BioModelNode mIdentifierNode = new BioModelNode(version.getVersionKey(), true);
		mIdentifierNode.setRenderHint("type","VCellIdentifier");
		mNameNode.add(mIdentifierNode);

		BioModelNode mOwnerNode = new BioModelNode(version.getOwner(), true);
		mOwnerNode.setRenderHint("type","ModelOwner");
		mNameNode.add(mOwnerNode);
		
		BioModelNode mDateNode = new BioModelNode(version.getDate(), true);
		mDateNode.setRenderHint("type","ModelDate");
		mNameNode.add(mDateNode);
		
		BioModelNode mPermissionsNode = new BioModelNode("Public", true);
		mPermissionsNode.setRenderHint("type","Permissions");
		mNameNode.add(mPermissionsNode);
		
		versionNode.add(generalInfoNode);
	}
	
	if(bioModelInfo.getPublicationInfos().length > 0) {
		String name = bioModelInfo.getPublicationInfos().length > 1 ? "Publications"  : "Publication";
		publicationsInfoNode = new BioModelNode(name, true);
		publicationsInfoNode.setRenderHint("type","PublicationsInfo");
		
		for(int i=0; i<bioModelInfo.getPublicationInfos().length; i++) {
			BioModelNode piTitleNode = new BioModelNode(bioModelInfo.getPublicationInfos()[i], true);
			piTitleNode.setRenderHint("type","PublicationInfoTitle");
			publicationsInfoNode.add(piTitleNode);
			
			BioModelNode piAuthorsNode = new BioModelNode(bioModelInfo.getPublicationInfos()[i], false);
			piAuthorsNode.setRenderHint("type","PublicationInfoAuthors");
			piTitleNode.add(piAuthorsNode);

			BioModelNode piCitationNode = new BioModelNode(bioModelInfo.getPublicationInfos()[i], false);
			piCitationNode.setRenderHint("type","PublicationInfoCitation");
			piTitleNode.add(piCitationNode);

			BioModelNode piDoiNode = new BioModelNode(bioModelInfo.getPublicationInfos()[i], false);
			piDoiNode.setRenderHint("type","PublicationInfoDoi");
			piTitleNode.add(piDoiNode);

			if(bioModelInfo.getPublicationInfos()[i].getUrl() != null && !bioModelInfo.getPublicationInfos()[i].getUrl().isEmpty()) {
				if(bioModelInfo.getPublicationInfos()[i].getUrl().contains("pubmed") || bioModelInfo.getPublicationInfos()[i].getUrl().contains("PubMed")) {
					BioModelNode piUrlNode = new BioModelNode(bioModelInfo.getPublicationInfos()[i], false);
					piUrlNode.setRenderHint("type","PublicationInfoUrl");
					piTitleNode.add(piUrlNode);
				}
			}
		}
		versionNode.add(publicationsInfoNode);
	}

	if (bioModelInfo.getVersion().getAnnot() != null && bioModelInfo.getVersion().getAnnot().trim().length() > 0) {
		BioModelNode provenanceNode = new BioModelNode("Model Provenance", true);
		provenanceNode.setRenderHint("type","Provenance");
		versionNode.add(provenanceNode);
		
		String annotations = bioModelInfo.getVersion().getAnnot();
		StringTokenizer tokenizer = new StringTokenizer(annotations, "\n");
		while (tokenizer.hasMoreTokens()) {	
			String annotation = tokenizer.nextToken();
			provenanceNode.add(new BioModelNode(new Annotation(annotation),false));
		}
	}

	BioModelChildSummary bioModelChildSummary = bioModelInfo.getBioModelChildSummary();
	if (bioModelChildSummary==null) {
		versionNode.add(new BioModelNode("SUMMARY INFORMATION NOT AVAILABLE",false));
	} else {
		String scNames[] = bioModelChildSummary.getSimulationContextNames();
		String scAnnot[] = bioModelChildSummary.getSimulationContextAnnotations();
		int geomDims[] = bioModelChildSummary.getGeometryDimensions();
		String geomNames[] = bioModelChildSummary.getGeometryNames();
		MathType appTypes[] = bioModelChildSummary.getAppTypes();
		
		String name = scNames.length > 1 ? "Applications Provenance" : "Application Provenance";
		applicationsNode = new BioModelNode(name, true);
		applicationsNode.setRenderHint("type","Applications");

 		for (int i = 0; i < scNames.length; i++) {
			BioModelNode scNode = new BioModelNode(scNames[i],true);
			scNode.setRenderHint("type","SimulationContext");
			scNode.setRenderHint("appType",appTypes[i].getDescription());	// need these 2 hints to decide on the icon
			scNode.setRenderHint("dimension",geomDims[i] + "");
			applicationsNode.add(scNode);
			
			//add application type
			BioModelNode appTypeNode = new BioModelNode(appTypes[i],false);
			appTypeNode.setRenderHint("type","AppType");
			scNode.add(appTypeNode);
			
			if (scAnnot[i]!=null && scAnnot[i].trim().length()>0){
				scNode.add(new BioModelNode(new Annotation(scAnnot[i]),false));
			}
			
			BioModelNode geometryNode = null;
			if (geomDims[i]>0){
				geometryNode = new BioModelNode((geomNames[i]+" ("+geomDims[i]+"D)"),false);
			}else{
				geometryNode = new BioModelNode(BioModelChildSummary.COMPARTMENTAL_GEO_STR,false);
			}
			geometryNode.setRenderHint("type","Geometry");
			geometryNode.setRenderHint("dimension",new Integer(geomDims[i]));
			scNode.add(geometryNode);
			//
			// add simulations to simulationContext
			//
			String simNames[] = bioModelChildSummary.getSimulationNames(scNames[i]);
			String simAnnot[] = bioModelChildSummary.getSimulationAnnotations(scNames[i]);
			for (int j = 0; j < simNames.length; j++){
				BioModelNode simNode = new BioModelNode(simNames[j],true);
				simNode.setRenderHint("type","Simulation");
				scNode.add(simNode);
				if (simAnnot[j]!=null && simAnnot[j].trim().length()>0){
					simNode.add(new BioModelNode(new Annotation(simAnnot[j]),false));
				}
			}
		}
 		versionNode.add(applicationsNode);
	}
	return versionNode;
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Gets the bioModelInfo property (cbit.vcell.biomodel.BioModelInfo) value.
 * @return The bioModelInfo property value.
 * @see #setBioModelInfo
 */
public BioModelInfo getBioModelInfo() {
	return fieldBioModelInfo;
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/01 3:50:24 PM)
 */
private void refreshTree() {
	if (getBioModelInfo()!=null) {
		try {
			setRoot(createVersionSubTree(getBioModelInfo()));
		}catch (DataAccessException e) {
			e.printStackTrace(System.out);
		}
	} else {
		setRoot(new BioModelNode("empty"));
	}
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * Sets the bioModelInfo property (cbit.vcell.biomodel.BioModelInfo) value.
 * @param bioModelInfo The new value for the property.
 * @see #getBioModelInfo
 */
public void setBioModelInfo(BioModelInfo bioModelInfo) {
	if (bioModelInfo == fieldBioModelInfo) {
		return;
	}
	BioModelInfo oldValue = fieldBioModelInfo;
	fieldBioModelInfo = bioModelInfo;
	firePropertyChange("bioModelInfo", oldValue, bioModelInfo);
	refreshTree();
}
}
