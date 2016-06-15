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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;

import cbit.vcell.client.desktop.DatabaseSearchPanel.SearchCriterion;
import cbit.vcell.client.desktop.biomodel.BioModelsNetModelInfo;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.VCellBasicCellRenderer.VCDocumentInfoNode;
import cbit.vcell.geometry.GeometryInfo;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public abstract class VCDocumentDbTreeModel extends DefaultTreeModel implements DatabaseListener {
	protected boolean fieldLatestOnly = false;
	private transient java.beans.PropertyChangeSupport propertyChange;
	protected DocumentManager fieldDocumentManager = null;
	protected ArrayList<SearchCriterion> searchCriterionList = null;
	protected BioModelNode rootNode = null;
	protected BioModelNode myModelsNode = null;
	protected BioModelNode sharedModelsNode = null;
	private JTree ownerTree = null;
	
	public static final String USER_tutorial = "tutorial";
	public static final String USER_Education = "Education";
	public static final String USER_BioNetGen = "BioNetGen";
	public static final String USER_BioNetGen61 = "BioNetGenC";
	
	protected BioModelNode tutorialModelsNode = null;
	protected BioModelNode educationModelsNode = null;
	protected BioModelNode bngRulesBasedModelsNode = null;
	protected BioModelNode bngRulesBasedModelsNode61 = null;
	protected BioModelNode publicModelsNode = null;
	
	public static final String Tutorials = "Tutorials";
	public static final String Education = "Education";
	public static final String BNGRulesBased = "Tutorial VCell 6.0 (Rule-based)";
	public static final String BNGRulesBased61 = "Tutorial VCell 6.1 (Rule-based)";
	
	public static final String SHARED_BIO_MODELS = "Shared BioModels";
	public static final String Public_BioModels = "Public BioModels";
	
	public static final String SHARED_MATH_MODELS = "Shared MathModels";
	public static final String Public_MathModels = "Public MathModels";
	
	public static final String SHARED_GEOMETRIES = "Shared Geometries";
	public static final String PUBLIC_GEOMETRIES = "Public Geometries";
/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public VCDocumentDbTreeModel(JTree tree) {
	super(new BioModelNode("not connected", true),true);
	ownerTree = tree;
	rootNode = (BioModelNode)((DefaultTreeModel)this).getRoot();
	myModelsNode = new BioModelNode("My Models", true);
	sharedModelsNode = new BioModelNode("Shared Models", true);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
protected abstract void createBaseTree() throws DataAccessException;

protected synchronized static void initBaseTree(BioModelNode rootNode,BioModelNode[] mainChildNodes,Object rootNodeUserObject,BioModelNode sharedModelsNode,Object sharedModelsNodeUserObject){
	rootNode.removeAllChildren();
	for(BioModelNode mainChildNode:mainChildNodes){
		rootNode.add(mainChildNode);
	}
	rootNode.setUserObject(rootNodeUserObject);
	sharedModelsNode.setUserObject(sharedModelsNodeUserObject);
}
protected synchronized static void initFinalTree(VCDocumentDbTreeModel vcDocumentDbTreeModel,TreeMap<String, BioModelNode> treeMap,User loginUser){
	BioModelNode ownerNode = (BioModelNode)treeMap.remove(loginUser.getName());
	vcDocumentDbTreeModel.myModelsNode.setUserObject(loginUser);
	vcDocumentDbTreeModel.myModelsNode.removeAllChildren();
	for (int c = 0; c < ownerNode.getChildCount();) {
		BioModelNode childNode = (BioModelNode) ownerNode.getChildAt(c);
		vcDocumentDbTreeModel.myModelsNode.add(childNode);
	}	
	vcDocumentDbTreeModel.sharedModelsNode.removeAllChildren();
	vcDocumentDbTreeModel.publicModelsNode.removeAllChildren();
	boolean bTutorial = vcDocumentDbTreeModel.tutorialModelsNode != null;
	boolean bEducation = vcDocumentDbTreeModel.educationModelsNode != null;
	boolean bBNGRules = vcDocumentDbTreeModel.bngRulesBasedModelsNode != null;
	boolean bBNGRules61 = vcDocumentDbTreeModel.bngRulesBasedModelsNode61 != null;
	if(bTutorial){vcDocumentDbTreeModel.tutorialModelsNode.removeAllChildren();}
	if(bEducation){vcDocumentDbTreeModel.educationModelsNode.removeAllChildren();}
	if(bBNGRules){vcDocumentDbTreeModel.bngRulesBasedModelsNode.removeAllChildren();}
	if(bBNGRules61){vcDocumentDbTreeModel.bngRulesBasedModelsNode61.removeAllChildren();}
	for (String username : treeMap.keySet()) {
		BioModelNode userNode = treeMap.get(username);
		BioModelNode parentNode = vcDocumentDbTreeModel.sharedModelsNode;
		boolean bSpecificUser = true;
		if (username.equals(USER_tutorial) && bTutorial) {
			parentNode = vcDocumentDbTreeModel.tutorialModelsNode;
		} else if (username.equals(USER_Education) && bEducation) {
			parentNode = vcDocumentDbTreeModel.educationModelsNode;
		} else if (username.equals(USER_BioNetGen) && bBNGRules) {
			parentNode = vcDocumentDbTreeModel.bngRulesBasedModelsNode;
		} else if (username.equals(USER_BioNetGen61) && bBNGRules) {
			parentNode = vcDocumentDbTreeModel.bngRulesBasedModelsNode61;
		} else {
			bSpecificUser = false;
		}
		for (int c = 0; c < userNode.getChildCount();) {
			BioModelNode childNode = (BioModelNode) userNode.getChildAt(c);
			VCDocumentInfoNode vcdDocumentInfoNode = (VCDocumentInfoNode) childNode.getUserObject();
			if (!bSpecificUser) {
				parentNode = vcDocumentDbTreeModel.sharedModelsNode;
				BigDecimal groupid = GroupAccess.GROUPACCESS_NONE;
				Version version = vcdDocumentInfoNode.getVCDocumentInfo().getVersion();
				if (version != null && version.getGroupAccess() != null) {
					groupid = version.getGroupAccess().getGroupid();
				}
				if (groupid.equals(GroupAccess.GROUPACCESS_ALL)) {
					parentNode = vcDocumentDbTreeModel.publicModelsNode;
				}
			}
			// when added to other node, this childNode was removed from userNode
			parentNode.add(childNode);
		}
	}

}
protected synchronized static TreeMap<String, BioModelNode> initOwners(VCDocumentInfo[] vcDocumentInfos,User loginUser,VCDocumentDbTreeModel subTreeParent,Method subTreeMethod){
	//
	// get list of users (owners)
	//
	Vector<User> userList = new Vector<User>();
	userList.addElement(loginUser);
	for (int i=0;i<vcDocumentInfos.length;i++){
		VCDocumentInfo vcDocumentInfo = vcDocumentInfos[i];
		if (!userList.contains(vcDocumentInfo.getVersion().getOwner())){
			userList.addElement(vcDocumentInfo.getVersion().getOwner());
		}
	}
	//
	// for each user
	//
	TreeMap<String, BioModelNode> treeMap = new TreeMap<String, BioModelNode>(new Comparator<String>() {

		public int compare(String o1, String o2) {
			return o1.compareToIgnoreCase(o2);
		}		
	});
	for (int ownerIndex=0;ownerIndex<userList.size();ownerIndex++){
		User owner = (User)userList.elementAt(ownerIndex);
		BioModelNode ownerNode = null;
		try{
			if(vcDocumentInfos instanceof BioModelInfo[]){
				ownerNode = (BioModelNode)subTreeMethod.invoke(subTreeParent, new Object[] {owner,(BioModelInfo[])vcDocumentInfos});
			}else if(vcDocumentInfos instanceof MathModelInfo[]){
				ownerNode = (BioModelNode)subTreeMethod.invoke(subTreeParent, new Object[] {owner,(MathModelInfo[])vcDocumentInfos});
			}else if(vcDocumentInfos instanceof GeometryInfo[]){
				ownerNode = (BioModelNode)subTreeMethod.invoke(subTreeParent, new Object[] {owner,(GeometryInfo[])vcDocumentInfos});
			}else{
				throw new Exception("Unimplemented VCDocumentInfo type="+vcDocumentInfos.getClass().getName());
			}
		}catch(Exception e){
			ownerNode = new BioModelNode("Error"+e.getMessage());
		}
		if(owner.equals(loginUser) || ownerNode.getChildCount() > 0){
			treeMap.put(owner.getName(), ownerNode);
		}
	}
	return treeMap;
}
protected boolean meetSearchCriteria(VCDocumentInfo vcDocumentInfo) {
	if (searchCriterionList == null) {
		return true;		
	}
	boolean bPass = true;
	for (SearchCriterion sc : searchCriterionList) {
		if (!sc.meetCriterion(vcDocumentInfo)) {
			bPass = false;
			break;
		}		
	}
	return bPass;
}

/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
public DocumentManager getDocumentManager() {
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

public void refreshTree() {
	if (getDocumentManager() != null && getDocumentManager().getUser() != null){
		try {
			createBaseTree();
		}catch (DataAccessException e){
			e.printStackTrace(System.out);
		}
	} else {
		rootNode.setUserObject("not connected");
		rootNode.removeAllChildren();
	}
	nodeStructureChanged(rootNode);
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/01 3:50:24 PM)
 */
public void refreshTree(ArrayList<SearchCriterion> newFilterList) {
	searchCriterionList = newFilterList;
	refreshTree();
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
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

public void updateConnectionStatus(ConnectionStatus connStatus) {
	switch (connStatus.getStatus()) {
		case ConnectionStatus.NOT_CONNECTED: {
			rootNode.removeAllChildren();
			rootNode.setUserObject("not connected");
			nodeStructureChanged(rootNode);
			break;
		}
		case ConnectionStatus.INITIALIZING: {
			rootNode.removeAllChildren();
			rootNode.setUserObject("connecting...");
			nodeStructureChanged(rootNode);
			break;
		}
		case ConnectionStatus.DISCONNECTED: {
			rootNode.removeAllChildren();
			rootNode.setUserObject("disconnected");
			nodeStructureChanged(rootNode);
			break;
		}
	}
}

private BioModelNode findNode(BioModelNode node, VCDocumentInfo vcDocumentInfo) {	
	Object userObject = node.getUserObject();
	if (userObject instanceof VCDocumentInfoNode && ((VCDocumentInfoNode)userObject).getVCDocumentInfo() == vcDocumentInfo) {
		if (ownerTree.isPathSelected(new TreePath(node.getPath()))) {
			return node;
		}
	}
	
	for (int i = 0; i < node.getChildCount(); i ++){
		BioModelNode child = (BioModelNode)node.getChildAt(i);
		BioModelNode matchNode = findNode(child, vcDocumentInfo);
		if (matchNode != null){
			break;
		}
	}
	return null;
}

public void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length == 0 || selectedObjects.length > 1) {
		ownerTree.clearSelection();
	} else {
		if (this instanceof BioModelDbTreeModel && selectedObjects[0] instanceof BioModelInfo
				|| this instanceof MathModelDbTreeModel && selectedObjects[0] instanceof MathModelInfo
				|| this instanceof GeometryDbTreeModel && selectedObjects[0] instanceof GeometryInfo)  {
			BioModelNode node = findNode(rootNode, (VCDocumentInfo) selectedObjects[0]);
			if (node != null) {
				ownerTree.setSelectionPath(new TreePath(node.getPath()));
			}
		} else if (selectedObjects[0] == null || selectedObjects[0] instanceof VCDocumentInfo || selectedObjects[0] instanceof BioModelsNetModelInfo) {
			ownerTree.clearSelection();
		}
	}	
}
}
