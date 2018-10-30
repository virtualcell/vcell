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

import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelChildSummary.MathType;

import cbit.vcell.solver.ode.ODESolverResultSet;

import org.vcell.util.document.MathModelChildSummary;
import org.vcell.util.document.MathModelInfo;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class MathModelMetaDataTreeModel extends javax.swing.tree.DefaultTreeModel {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private MathModelInfo fieldMathModelInfo = null;
/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public MathModelMetaDataTreeModel() {
	super(new BioModelNode("empty",false),true);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 2:41:43 PM)
 * @param mathModelNode cbit.vcell.desktop.BioModelNode
 * @param mathModelInfo cbit.vcell.mathmodel.MathModelInfo
 */
private BioModelNode createVersionSubTree(MathModelInfo mathModelInfo) throws DataAccessException {
	BioModelNode versionNode = new BioModelNode(mathModelInfo,true);
	//
	// add children of the MathModel to the node passed in
	//
	if (mathModelInfo.getVersion().getAnnot()!=null && mathModelInfo.getVersion().getAnnot().trim().length()>0) {
		BioModelNode provenanceNode = new BioModelNode("Provenance", true);
		provenanceNode.setRenderHint("type","Provenance");
		versionNode.add(provenanceNode);

		String annotations = mathModelInfo.getVersion().getAnnot();
		StringTokenizer tokenizer = new StringTokenizer(annotations, "\n");
		while (tokenizer.hasMoreTokens()) {	
			String annotation = tokenizer.nextToken();
			provenanceNode.add(new BioModelNode(new Annotation(annotation),false));
		}
	}
	
	if(mathModelInfo.getPublicationInfos().length > 0) {
		String name = mathModelInfo.getPublicationInfos().length > 1 ? "Publications" : "Publication";
		BioModelNode publicationsInfoNode = new BioModelNode(name, true);
		publicationsInfoNode.setRenderHint("type","PublicationsInfo");
		
		for(int i=0; i<mathModelInfo.getPublicationInfos().length; i++) {
			BioModelNode piTitleNode = new BioModelNode(mathModelInfo.getPublicationInfos()[i], true);
			piTitleNode.setRenderHint("type","PublicationInfoTitle");
			publicationsInfoNode.add(piTitleNode);
			
			BioModelNode piAuthorsNode = new BioModelNode(mathModelInfo.getPublicationInfos()[i], false);
			piAuthorsNode.setRenderHint("type","PublicationInfoAuthors");
			piTitleNode.add(piAuthorsNode);
			
			BioModelNode piCitationNode = new BioModelNode(mathModelInfo.getPublicationInfos()[i], false);
			piCitationNode.setRenderHint("type","PublicationInfoCitation");
			piTitleNode.add(piCitationNode);

			BioModelNode piDoiNode = new BioModelNode(mathModelInfo.getPublicationInfos()[i], false);
			piDoiNode.setRenderHint("type","PublicationInfoDoi");
			piTitleNode.add(piDoiNode);

			if(mathModelInfo.getPublicationInfos()[i].getUrl() != null && !mathModelInfo.getPublicationInfos()[i].getUrl().isEmpty()) {
				if(mathModelInfo.getPublicationInfos()[i].getUrl().contains("pubmed") || mathModelInfo.getPublicationInfos()[i].getUrl().contains("PubMed")) {
					BioModelNode piUrlNode = new BioModelNode(mathModelInfo.getPublicationInfos()[i], false);
					piUrlNode.setRenderHint("type","PublicationInfoUrl");
					piTitleNode.add(piUrlNode);
				}
			}
		}
		versionNode.add(publicationsInfoNode);
	}	
	
	MathModelChildSummary mathModelChildSummary = mathModelInfo.getMathModelChildSummary();
	if (mathModelChildSummary==null){
		versionNode.add(new BioModelNode("SUMMARY INFORMATION NOT AVAILABLE",false));
	}else{
		int geomDim = mathModelChildSummary.getGeometryDimension();
		String geomName = mathModelChildSummary.getGeometryName();
		MathType modelType = mathModelChildSummary.getModelType();
		//add model type tree node
		BioModelNode modelTypeNode = new BioModelNode(modelType,false);
		modelTypeNode.setRenderHint("type","AppType");
		versionNode.add(modelTypeNode);
		BioModelNode geometryNode = null;
		if (geomDim>0){
			geometryNode = new BioModelNode(geomName + " ("+geomDim+"D)",false);
		}else{
			geometryNode = new BioModelNode(BioModelChildSummary.COMPARTMENTAL_GEO_STR,false);
		}
		geometryNode.setRenderHint("type","Geometry");
		geometryNode.setRenderHint("dimension",new Integer(geomDim));
		versionNode.add(geometryNode);
		//
		// add simulations to simulationContext
		//
		String simNames[] = mathModelChildSummary.getSimulationNames();
		String simAnnot[] = mathModelChildSummary.getSimulationAnnotations();
		for (int j = 0; j < simNames.length; j++){
			BioModelNode simNode = new BioModelNode(simNames[j],true);
			simNode.setRenderHint("type","Simulation");
			versionNode.add(simNode);
			if (simAnnot[j]!=null && simAnnot[j].trim().length()>0){
				simNode.add(new BioModelNode(new Annotation(simAnnot[j]),false));
			}
		}
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
 * Gets the mathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @return The mathModelInfo property value.
 * @see #setMathModelInfo
 */
public MathModelInfo getMathModelInfo() {
	return fieldMathModelInfo;
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
	if (getMathModelInfo()!=null){
		try {
			setRoot(createVersionSubTree(getMathModelInfo()));
		}catch (DataAccessException e){
			e.printStackTrace(System.out);
		}
	}else{
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
 * Sets the mathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @param mathModelInfo The new value for the property.
 * @see #getMathModelInfo
 */
public void setMathModelInfo(MathModelInfo mathModelInfo) {
	if (mathModelInfo == fieldMathModelInfo) {
		return;
	}
	MathModelInfo oldValue = fieldMathModelInfo;
	fieldMathModelInfo = mathModelInfo;
	firePropertyChange("mathModelInfo", oldValue, mathModelInfo);
	refreshTree();
}
}
