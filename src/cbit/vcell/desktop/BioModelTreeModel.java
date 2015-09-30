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

import java.util.Set;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.document.Identifiable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEvent;
import cbit.vcell.biomodel.meta.VCMetaData.AnnotationEventListener;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.solver.Simulation;
import cbit.vcell.xml.gui.MiriamTreeModel;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
public class BioModelTreeModel extends javax.swing.tree.DefaultTreeModel implements java.beans.PropertyChangeListener, AnnotationEventListener {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.biomodel.BioModel fieldBioModel = null;
/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public BioModelTreeModel() {
	super(new BioModelNode("empty",false),true);
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 2:41:43 PM)
 * @param bioModelNode cbit.vcell.desktop.BioModelNode
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
private void addBioModelContents(BioModelNode bioModelNode, BioModel bioModel) {
	//
	// add children of the BioModel to the node passed in
	//
	if (bioModel==null){
		return;
	}
//	bioModelNode.add(new BioModelNode(bioModel.getModel(),false));

	//if (bioModel.getDescription() != null && !bioModel.getDescription().equals("")){
		bioModelNode.add(new BioModelNode(new Annotation(bioModel.getVCMetaData().getFreeTextAnnotation(bioModel)),false));
	//}
		Set<MiriamRefGroup> isDescribedByAnnotation = bioModel.getVCMetaData().getMiriamManager().getMiriamRefGroups(bioModel, MIRIAMQualifier.MODEL_isDescribedBy);
		for (MiriamRefGroup refGroup : isDescribedByAnnotation){
			for (MiriamResource miriamResources : refGroup.getMiriamRefs()){
				bioModelNode.add(new MiriamTreeModel.LinkNode(MIRIAMQualifier.MODEL_isDescribedBy, miriamResources));
			}
		}
		Set<MiriamRefGroup> isAnnotation = bioModel.getVCMetaData().getMiriamManager().getMiriamRefGroups(bioModel, MIRIAMQualifier.MODEL_is);
		for (MiriamRefGroup refGroup : isAnnotation){
			for (MiriamResource miriamResources : refGroup.getMiriamRefs()){
				bioModelNode.add(new MiriamTreeModel.LinkNode(MIRIAMQualifier.MODEL_is, miriamResources));
			}
		}

	SimulationContext scArray[] = bioModel.getSimulationContexts();
	if (scArray!=null){
		for (int i=0;i<scArray.length;i++){
			BioModelNode scNode = new BioModelNode(scArray[i],true);
			bioModelNode.add(scNode);
			// scNode.add(new BioModelNode(scArray[i].getModel(),false));
			
			
			//add application type node
			MathType typeInfo = scArray[i].getMathType();
			
			BioModelNode appTypeNode = new BioModelNode(typeInfo,false);
			appTypeNode.setRenderHint("type","AppType");
			scNode.add(appTypeNode);
			//
			// Display Annotation on tree
			//
			scNode.add(new BioModelNode(new Annotation(scArray[i].getDescription()),false));
			
			scNode.add(new BioModelNode(scArray[i].getGeometry(),false));
			if (scArray[i].getMathDescription()!=null){
				scNode.add(new BioModelNode(scArray[i].getMathDescription(),false));
			}else{
				scNode.add(new BioModelNode("math not generated",false));
			}

			//
			// add simulations to simulationContext
			//
			Simulation simArray[] = bioModel.getSimulations();
			if (simArray!=null){
				for (int j=0;j<simArray.length;j++){
					if (simArray[j].getMathDescription() == scArray[i].getMathDescription()){
						BioModelNode simNode = new BioModelNode(simArray[j],true);
						scNode.add(simNode);

						//
						// add simulation children (simulation annotations)
						//
						//if (simArray[j].getDescription() != null && simArray[j].getDescription().length() > 0){
							simNode.add(new BioModelNode(new Annotation(simArray[j].getDescription()),false));
						//}
						
						//simNode.add(new BioModelNode(simArray[i].getMathDescription(),false));
						//simNode.add(new BioModelNode(simArray[i].getMathOverrides(),false));
						//simNode.add(new BioModelNode(simArray[i].getMeshSpecification(),false));
						//simNode.add(new BioModelNode(simArray[i].getSolverTaskDescription(),false));
					}
				}
			}
		}
	}
	
	
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}
/**
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
private BioModelNode createBaseTree(BioModel bioModel) {
	BioModelNode rootNode = new BioModelNode(bioModel,true);
	addBioModelContents(rootNode,bioModel);
	return rootNode;
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Gets the bioModel property (cbit.vcell.biomodel.BioModel) value.
 * @return The bioModel property value.
 * @see #setBioModel
 */
public cbit.vcell.biomodel.BioModel getBioModel() {
	return fieldBioModel;
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
 * Creation date: (5/9/01 8:28:22 AM)
 * @param evt java.beans.PropertyChangeEvent
 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	try {
		if (evt.getSource()==getBioModel() && evt.getPropertyName().equals("simulations")){
			refreshTree();
			Simulation oldSims[] = (Simulation[])evt.getOldValue();
			if (oldSims!=null){
				for (int i = 0; i < oldSims.length; i++){
					oldSims[i].removePropertyChangeListener(this);
				}
			}
			Simulation newSims[] = (Simulation[])evt.getNewValue();
			if (newSims!=null){
				for (int i = 0; i < newSims.length; i++){
					newSims[i].addPropertyChangeListener(this);
				}
			}
		}else if (evt.getSource()==getBioModel() && evt.getPropertyName().equals("simulationContexts")){
			refreshTree();
			SimulationContext oldSCs[] = (SimulationContext[])evt.getOldValue();
			if (oldSCs!=null){
				for (int i = 0; i < oldSCs.length; i++){
					oldSCs[i].removePropertyChangeListener(this);
				}
			}
			SimulationContext newSCs[] = (SimulationContext[])evt.getNewValue();
			if (newSCs!=null){
				for (int i = 0; i < newSCs.length; i++){
					newSCs[i].addPropertyChangeListener(this);
				}
			}
		} else if (evt.getSource()==getBioModel() && evt.getPropertyName().equals("description")){
			BioModel bioModel = (BioModel)evt.getSource();
			BioModelNode bioModelNode = ((BioModelNode)getRoot()).findNodeByUserObject(bioModel);
			BioModelNode annotNode = bioModelNode.findNodeByUserObject(new Annotation((String)evt.getOldValue()));
			if (annotNode == null && evt.getNewValue()!=null && !((String)evt.getNewValue()).equals("")){
				//
				// must add annotation node (was null string)
				//
				bioModelNode.insert(new BioModelNode(new Annotation((String)evt.getNewValue()),false),0);
				nodeStructureChanged(bioModelNode);
			}else if (annotNode != null && evt.getNewValue()!=null && !((String)evt.getNewValue()).equals("")) {
				//
				// change annotation content for annotation node
				//
				annotNode.setUserObject(new Annotation((String)evt.getNewValue()));
				nodeChanged(annotNode);
			}else if (annotNode != null && (evt.getNewValue()==null || ((String)evt.getNewValue()).equals(""))) {
				//
				// delete annotation node
				//
				removeNodeFromParent(annotNode);
			}
		} else if (evt.getSource() instanceof Simulation) {
			if (evt.getPropertyName().equals("name")){
				Simulation sim = (Simulation)evt.getSource();
				BioModelNode simNode = ((BioModelNode)getRoot()).findNodeByUserObject(sim);
				nodeChanged(simNode);
			}
			else if (evt.getPropertyName().equals("description")){
				Simulation sim = (Simulation)evt.getSource();
				BioModelNode simNode = ((BioModelNode)getRoot()).findNodeByUserObject(sim);
				BioModelNode annotNode = simNode.findNodeByUserObject(new Annotation((String)evt.getOldValue()));
				if (annotNode == null && evt.getNewValue()!=null && !((String)evt.getNewValue()).equals("")){
					//
					// must add annotation node (was null string)
					//
					simNode.insert(new BioModelNode(new Annotation((String)evt.getNewValue()),false),0);
					nodeStructureChanged(simNode);
				}else if (annotNode != null && evt.getNewValue()!=null && !((String)evt.getNewValue()).equals("")) {
					//
					// change annotation content for annotation node
					//
					annotNode.setUserObject(new Annotation((String)evt.getNewValue()));
					nodeChanged(annotNode);
				}else if (annotNode != null && (evt.getNewValue()==null || ((String)evt.getNewValue()).equals(""))) {
					//
					// delete annotation node
					//
					removeNodeFromParent(annotNode);
				}
			}
		}else if (evt.getSource() instanceof SimulationContext){
			if (evt.getPropertyName().equals("geometry")){
				Geometry oldGeometry = (Geometry)evt.getOldValue();
				Geometry newGeometry = (Geometry)evt.getNewValue();
				BioModelNode geoNode = ((BioModelNode)getRoot()).findNodeByUserObject(oldGeometry);
				geoNode.setUserObject(newGeometry);
				nodeChanged(geoNode);
			}else if (evt.getPropertyName().equals("name")){
				SimulationContext simContext = (SimulationContext)evt.getSource();
				BioModelNode scNode = ((BioModelNode)getRoot()).findNodeByUserObject(simContext);
				nodeChanged(scNode);
			}else if (evt.getPropertyName().equals("description")){
				SimulationContext simContext = (SimulationContext)evt.getSource();
				BioModelNode scNode = ((BioModelNode)getRoot()).findNodeByUserObject(simContext);
				BioModelNode annotNode = scNode.findNodeByUserObject(new Annotation((String)evt.getOldValue()));
				if (annotNode == null && evt.getNewValue()!=null && !((String)evt.getNewValue()).equals("")){
					//
					// must add annotation node (was null string)
					//
					scNode.insert(new BioModelNode(new Annotation((String)evt.getNewValue()),false),0);
					nodeStructureChanged(scNode);
				}else if (annotNode != null && evt.getNewValue()!=null && !((String)evt.getNewValue()).equals("")) {
					//
					// change annotation content for annotation node
					//
					annotNode.setUserObject(new Annotation((String)evt.getNewValue()));
					nodeChanged(annotNode);
				}else if (annotNode != null && (evt.getNewValue()==null || ((String)evt.getNewValue()).equals(""))) {
					//
					// delete annotation node
					//
					removeNodeFromParent(annotNode);
				}
			}else if (evt.getPropertyName().equals("mathDescription")){
				SimulationContext simContext = (SimulationContext)evt.getSource();
				BioModelNode scNode = ((BioModelNode)getRoot()).findNodeByUserObject(simContext);
				nodeStructureChanged(scNode);
			}
		}
	}catch (Exception e){
		e.printStackTrace(System.out);
	}
}
@Override
public void nodeChanged(final TreeNode node) {
	BioModelTreeModel.super.nodeChanged(node);
}
@Override
public void nodeStructureChanged(final TreeNode node) {
	BioModelTreeModel.super.nodeStructureChanged(node);
}
@Override
public void removeNodeFromParent(final MutableTreeNode node) {
	BioModelTreeModel.super.removeNodeFromParent(node);
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/01 3:50:24 PM)
 */
private void refreshTree() {
	if (getBioModel()!=null){
		setRoot(createBaseTree(getBioModel()));
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
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}
/**
 * Sets the bioModel property (cbit.vcell.biomodel.BioModel) value.
 * @param bioModel The new value for the property.
 * @see #getBioModel
 */
public void setBioModel(cbit.vcell.biomodel.BioModel bioModel) {
	cbit.vcell.biomodel.BioModel oldValue = fieldBioModel;
	if (oldValue!=null){
		oldValue.removePropertyChangeListener(this);
		SimulationContext SCs[] = oldValue.getSimulationContexts();
		for (int i = 0; SCs!=null && i < SCs.length; i++){
			SCs[i].removePropertyChangeListener(this);
			SCs[i].getGeometryContext().removePropertyChangeListener(this);
		}
		Simulation sims[] = oldValue.getSimulations();
		for (int i = 0; sims!=null && i < sims.length; i++){
			sims[i].removePropertyChangeListener(this);
		}
		oldValue.getVCMetaData().removeAnnotationEventListener(this);
	}
	fieldBioModel = bioModel;
	if (bioModel!=null){
		bioModel.addPropertyChangeListener(this);
		SimulationContext SCs[] = bioModel.getSimulationContexts();
		for (int i = 0; SCs!=null && i < SCs.length; i++){
			SCs[i].addPropertyChangeListener(this);
			SCs[i].getGeometryContext().addPropertyChangeListener(this);
		}
		Simulation sims[] = bioModel.getSimulations();
		for (int i = 0; sims!=null && i < sims.length; i++){
			sims[i].addPropertyChangeListener(this);
		}
		bioModel.getVCMetaData().addAnnotationEventListener(this);
	}
	firePropertyChange("bioModel", oldValue, bioModel);
	refreshTree();
}
public void annotationChanged(AnnotationEvent annotationEvent) {
	Identifiable identifiable = annotationEvent.getIdentifiable();
	if (identifiable!=null){
		BioModelNode scNode = ((BioModelNode)getRoot()).findNodeByUserObject(identifiable);
		if (scNode!=null){
			//nodeStructureChanged(scNode);
			refreshTree();
		}else{
			refreshTree();
		}
	}else{
		refreshTree();
	}
}
}
