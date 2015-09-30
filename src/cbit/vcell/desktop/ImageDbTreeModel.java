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

import java.util.TreeMap;
import java.util.Vector;

import javax.swing.tree.DefaultTreeModel;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import cbit.image.VCImageInfo;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.clientdb.DocumentManager;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
public class ImageDbTreeModel extends DefaultTreeModel implements DatabaseListener {
	private boolean fieldLatestOnly = false;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;

/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public ImageDbTreeModel() {
	super(new BioModelNode("empty",false),true);
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
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
private BioModelNode createBaseTree() throws DataAccessException {
	VCImageInfo imageInfos[] = getDocumentManager().getImageInfos();
	BioModelNode rootRootNode = new BioModelNode("Images",true);
	//
	// get list of users (owners)
	//
	Vector<User> ownerList = new Vector<User>();
	ownerList.addElement(getDocumentManager().getUser());
	for (int i=0;i<imageInfos.length;i++){
		VCImageInfo imageInfo = imageInfos[i];
		if (!ownerList.contains(imageInfo.getVersion().getOwner())){
			ownerList.addElement(imageInfo.getVersion().getOwner());
		}
	}
	//
	// for each user
	//
	TreeMap<String, BioModelNode> treeMap = new TreeMap<String, BioModelNode>();
	for (int ownerIndex=0;ownerIndex<ownerList.size();ownerIndex++){
		User owner = (User)ownerList.elementAt(ownerIndex);
		BioModelNode ownerNode = createOwnerSubTree(owner);
		if(owner.equals(getDocumentManager().getUser()) || ownerNode.getChildCount() > 0){
			treeMap.put(owner.getName().toLowerCase(),ownerNode);
		}
	}
	//
	rootRootNode.add((BioModelNode)treeMap.remove(getDocumentManager().getUser().getName().toLowerCase()));
	BioModelNode otherUsersNode = new BioModelNode("Images Neighborhood",true);
	rootRootNode.add(otherUsersNode);
	Object[] bmnArr = treeMap.values().toArray();
	for(int i = 0; i < bmnArr.length;i+= 1){
		otherUsersNode.add((BioModelNode)bmnArr[i]);
	}

	return rootRootNode;
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
private BioModelNode createOwnerSubTree(User owner) throws DataAccessException {
	VCImageInfo imageInfos[] = getDocumentManager().getImageInfos();
	//
	// for each user
	//
	BioModelNode rootNode = new BioModelNode(owner,true);
	for (int i=0;i<imageInfos.length;i++){
		VCImageInfo imageInfo = imageInfos[i];
		if (imageInfo.getVersion().getOwner().equals(owner)){
			BioModelNode bioModelNode = new BioModelNode(imageInfo.getVersion().getName(),true);
			rootNode.add(bioModelNode);
			//
			// get list of images with the same branch
			//
			Vector<VCImageInfo> imageBranchList = new Vector<VCImageInfo>();
			imageBranchList.addElement(imageInfo);
			for (i=i+1;i<imageInfos.length;i++){
				if (imageInfos[i].getVersion().getBranchID().equals(imageInfo.getVersion().getBranchID())){
					imageBranchList.add(0,imageInfos[i]);
				}else{
					i--;
					break;
				}
			}
			VCImageInfo imageInfosInBranch[] = null;
			if (getLatestOnly()){
				imageInfosInBranch = new VCImageInfo[1];
				imageInfosInBranch[0] = (VCImageInfo)imageBranchList.elementAt(0);
			}else{
				imageInfosInBranch = new VCImageInfo[imageBranchList.size()];
				imageBranchList.copyInto(imageInfosInBranch);
			}
			for (int versionCount=0;versionCount<imageInfosInBranch.length;versionCount++){
				bioModelNode.add(createVersionSubTree(imageInfosInBranch[versionCount]));
			}
		}
	}
	return rootNode;
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 2:41:43 PM)
 * @param bioModelNode cbit.vcell.desktop.BioModelNode
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
private BioModelNode createVersionSubTree(VCImageInfo imageInfo) throws DataAccessException {
	
	BioModelNode versionNode = new BioModelNode(imageInfo,false);
		
	return versionNode;
}


/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseDelete(cbit.vcell.clientdb.DatabaseEvent databaseEvent) {
	if (databaseEvent.getOldVersionInfo() instanceof VCImageInfo){
		VCImageInfo removedImageInfo = (VCImageInfo)databaseEvent.getOldVersionInfo();
		BioModelNode removedNode = ((BioModelNode)getRoot()).findNodeByUserObject(removedImageInfo);
		if (removedNode.getParent()!=null && removedNode.getSiblingCount()==1){ // just this one version
			removedNode = (BioModelNode)removedNode.getParent();
		}
		removeNodeFromParent(removedNode);
	}
}


/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseInsert(cbit.vcell.clientdb.DatabaseEvent databaseEvent) {
	if (databaseEvent.getNewVersionInfo() instanceof VCImageInfo){
		try {
			VCImageInfo insertedImageInfo = (VCImageInfo)databaseEvent.getNewVersionInfo();
			//
			// get parent of updated version
			//
			//   model1                           (String)
			//      Fri Sept 2, 2001 12:00:00     (VCImageInfo)
			//      Fri Sept 1, 2001 10:00:00     (VCImageInfo)
			//
			//
			BioModelNode newVersionNode = createVersionSubTree(insertedImageInfo);
			BioModelNode parentNode = ((BioModelNode)getRoot()).findNodeByUserObject(insertedImageInfo.getVersion().getName());
			if (parentNode==null){
				//
				// fresh insert 
				// Have to create parent node, for all versions of this image, 
				// and stick it in the correct order in the tree.
				//
				parentNode = new BioModelNode(insertedImageInfo.getVersion().getName(),true);
				parentNode.insert(newVersionNode,0);
				//
				// find owner node (if it is displayed)
				//
				User owner = insertedImageInfo.getVersion().getOwner();
				BioModelNode ownerRoot = ((BioModelNode)getRoot()).findNodeByUserObject(owner);
				//
				// if owner node exists, add BioModel parent and fire events to notify of the insertion
				//
				if (ownerRoot!=null){
					ownerRoot.insert(parentNode,0);  // !!!!!!!!!!!!!! new insert on top (index=0) .... should do insertion sort !!!!
					insertNodeInto(parentNode,ownerRoot,0);
				}
			}else{
				//
				// already versions there (just add child in the correct position within parent)
				//
				parentNode.insert(newVersionNode,0); // !!!!!!!!!! right now ignore order !!!!!!!!!!
				insertNodeInto(newVersionNode,parentNode,0);
			}
		} catch (DataAccessException e){
			e.printStackTrace(System.out);
			System.out.println("exception responding to databaseInsert(), refreshing whole tree");
			refreshTree();
		}
	}
}


/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseRefresh(cbit.vcell.clientdb.DatabaseEvent event) {

	//Our parent will tell us what to do
}


/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseUpdate(cbit.vcell.clientdb.DatabaseEvent databaseEvent) {
	//
	// the ClientDocumentManager usually throws an UPDATE when changing public/private status
	//
	if (databaseEvent.getOldVersionInfo() instanceof VCImageInfo){
		System.out.println("ImageDbTreeModel.databaseUpdate(), refreshing entire tree");
		BioModelNode node = ((BioModelNode)getRoot()).findNodeByUserObject(databaseEvent.getOldVersionInfo());
		node.setUserObject(databaseEvent.getNewVersionInfo());
		nodeChanged(node);
	}
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
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}


/**
 * Gets the latestOnly property (boolean) value.
 * @return The latestOnly property value.
 * @see #setLatestOnly
 */
public boolean getLatestOnly() {
	return fieldLatestOnly;
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
public void refreshTree() {
	if (getDocumentManager()!=null && getDocumentManager().getUser() != null){
		try {
			setRoot(createBaseTree());
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
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


/**
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(DocumentManager documentManager) {
	DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;

	if (oldValue != null){
		oldValue.removeDatabaseListener(this);
	}
	if (documentManager != null){
		documentManager.addDatabaseListener(this);
	}

	firePropertyChange(CommonTask.DOCUMENT_MANAGER.name, oldValue, documentManager);

	if (documentManager != oldValue){
		refreshTree();
	}
}


/**
 * Sets the latestOnly property (boolean) value.
 * @param latestOnly The new value for the property.
 * @see #getLatestOnly
 */
public void setLatestOnly(boolean latestOnly) {
	boolean oldValue = fieldLatestOnly;
	fieldLatestOnly = latestOnly;
	firePropertyChange("latestOnly", new Boolean(oldValue), new Boolean(latestOnly));
	if (latestOnly != oldValue){
		refreshTree();
	}
}
}
