package cbit.vcell.desktop;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Vector;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.User;

import cbit.vcell.client.desktop.DatabaseSearchPanel.SearchCriterion;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.VCellBasicCellRenderer.VCDocumentInfoNode;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
public class BioModelDbTreeModel extends javax.swing.tree.DefaultTreeModel implements DatabaseListener {
	private boolean fieldLatestOnly = false;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private DocumentManager fieldDocumentManager = null;
	private ArrayList<SearchCriterion> searchCriterionList = null;
/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public BioModelDbTreeModel() {
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
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
private BioModelNode createBaseTree() throws DataAccessException {
	BioModelInfo bioModelInfos[] = getDocumentManager().getBioModelInfos();
	BioModelNode rootRootNode = new BioModelNode("Biological Models",true);
	//
	// get list of users (owners)
	//
	Vector<User> ownerList = new Vector<User>();
	User loginUser = getDocumentManager().getUser();
	ownerList.addElement(loginUser);
	for (int i=0;i<bioModelInfos.length;i++){
		BioModelInfo bioModelInfo = bioModelInfos[i];
		if (!ownerList.contains(bioModelInfo.getVersion().getOwner())){
			ownerList.addElement(bioModelInfo.getVersion().getOwner());
		}
	}
	//
	// for each user
	//
	TreeMap<String, BioModelNode> treeMap = new TreeMap<String, BioModelNode>();
	for (int ownerIndex=0;ownerIndex<ownerList.size();ownerIndex++){
		User owner = (User)ownerList.elementAt(ownerIndex);
		BioModelNode ownerNode = createOwnerSubTree(owner);
		if(owner.equals(loginUser) || ownerNode.getChildCount() > 0){
			treeMap.put(owner.getName().toLowerCase(),ownerNode);
		}
	}
	//
	// create final tree
	//
	rootRootNode.add((BioModelNode)treeMap.remove(loginUser.getName().toLowerCase()));
	BioModelNode otherUsersNode = new BioModelNode("Shared BioModels",true);
	rootRootNode.add(otherUsersNode);
	for (BioModelNode userNode : treeMap.values()) {
		for (int c = 0; c < userNode.getChildCount();) {
			BioModelNode childNode = (BioModelNode) userNode.getChildAt(c);
			// when added to otherUserNode, this childNode was removed from userNode
			otherUsersNode.add(childNode);
		}
	}
	return rootRootNode;
}

private boolean meetSearchCriteria(BioModelInfo bioModelInfo) {
	if (searchCriterionList == null) {
		return true;		
	}
	boolean bPass = true;
	for (SearchCriterion sc : searchCriterionList) {
		if (!sc.meetCriterion(bioModelInfo)) {
			bPass = false;
			break;
		}		
	}
	return bPass;
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
private BioModelNode createOwnerSubTree(User owner) throws DataAccessException {
	BioModelInfo bioModelInfos[] = getDocumentManager().getBioModelInfos();
	//
	// for each user
	//
	BioModelNode rootNode = new BioModelNode(owner,true);
	for (int i = 0; i < bioModelInfos.length; i ++){
		BioModelInfo bioModelInfo = bioModelInfos[i];
		
		if (bioModelInfo.getVersion().getOwner().equals(owner)){
			if (!meetSearchCriteria(bioModelInfo)) {
				continue;
			}
			
			BioModelNode bioModelNode = new BioModelNode(new VCDocumentInfoNode(bioModelInfo),true);
			rootNode.add(bioModelNode);
			//
			// get list of bioModels with the same branch
			//
			Vector<BioModelInfo> bioModelBranchList = new Vector<BioModelInfo>();
			bioModelBranchList.addElement(bioModelInfo);
			for (i = i + 1; i < bioModelInfos.length; i ++){
				if (bioModelInfos[i].getVersion().getBranchID().equals(bioModelInfo.getVersion().getBranchID())){
					if (!meetSearchCriteria(bioModelInfos[i])) {
						continue;
					}
					bioModelBranchList.add(0,bioModelInfos[i]);
				}else{
					i--;
					break;
				}
			}
			BioModelInfo bioModelInfosInBranch[] = null;
			if (getLatestOnly()){
				bioModelInfosInBranch = new BioModelInfo[1];
				bioModelInfosInBranch[0] = (BioModelInfo)bioModelBranchList.elementAt(0);
			}else{
				bioModelInfosInBranch = new BioModelInfo[bioModelBranchList.size()];
				bioModelBranchList.copyInto(bioModelInfosInBranch);
			}
			for (int versionCount=0;versionCount<bioModelInfosInBranch.length;versionCount++){
				bioModelNode.add(createVersionSubTree(bioModelInfosInBranch[versionCount]));
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
private BioModelNode createVersionSubTree(BioModelInfo bmInfo) throws DataAccessException {
	BioModelNode versionNode = new BioModelNode(bmInfo,false);
	versionNode.setRenderHint(BioModelNode.MAX_ERROR_LEVEL,new Integer(getMaxErrorLevel(bmInfo)));
	return versionNode;
}
/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseDelete(DatabaseEvent databaseEvent) {
	if (databaseEvent.getOldVersionInfo() instanceof BioModelInfo){
		BioModelInfo removedBioModelInfo = (BioModelInfo)databaseEvent.getOldVersionInfo();
		BioModelNode removedNode = ((BioModelNode)getRoot()).findNodeByUserObject(removedBioModelInfo);
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
public void databaseInsert(DatabaseEvent databaseEvent) {
	if (databaseEvent.getNewVersionInfo() instanceof BioModelInfo){
		try {
			BioModelInfo insertedBioModelInfo = (BioModelInfo)databaseEvent.getNewVersionInfo();
			//
			// get parent of updated version
			//
			//   model1                           (VCDocumentInfoNode)
			//      Fri Sept 2, 2001 12:00:00     (BioModelInfo)
			//      Fri Sept 1, 2001 10:00:00     (BioModelInfo)
			//
			//
			BioModelNode newVersionNode = createVersionSubTree(insertedBioModelInfo);
			
			//
			// find owner node (if it is displayed)
			//
			User owner = insertedBioModelInfo.getVersion().getOwner();
			BioModelNode ownerRoot = ((BioModelNode)getRoot()).findNodeByUserObject(owner);
			BioModelNode parentNode = null;
			if (ownerRoot!=null){
				parentNode = ownerRoot.findNodeByUserObject(new VCDocumentInfoNode(insertedBioModelInfo));
			}
			
			if (parentNode==null){
				//
				// fresh insert 
				// Have to create parent node, for all versions of this biomodel, 
				// and stick it in the correct order in the tree.
				//
				parentNode = new BioModelNode(new VCDocumentInfoNode(insertedBioModelInfo),true);
				parentNode.insert(newVersionNode,0);
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
public void databaseRefresh(DatabaseEvent event) {

	//Our parent will tell us what to do
}
/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseUpdate(DatabaseEvent databaseEvent) {
	//
	// the ClientDocumentManager usually throws an UPDATE when changing public/private status
	//
	if (databaseEvent.getOldVersionInfo() instanceof BioModelInfo){
		BioModelNode node = ((BioModelNode)getRoot()).findNodeByUserObject(databaseEvent.getOldVersionInfo());
		if (node != null) {
			node.setUserObject(databaseEvent.getNewVersionInfo());
			nodeChanged(node);
		}
	}
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
 * Insert the method's description here.
 * Creation date: (11/6/2002 4:41:23 PM)
 * @return int
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @deprecated
 */
private int getMaxErrorLevel(BioModelInfo bioModelInfo) {
	//if (getDocumentManager()!=null){
		//int worstErrorLevel = cbit.vcell.modeldb.SimContextStatus.ERROR_NONE;
		//cbit.vcell.clientdb.DocumentManager docManager = getDocumentManager();
		//try {
			//BioModelMetaData bmMetaData = docManager.getBioModelMetaData(bioModelInfo);
			//java.util.Enumeration enum1 = bmMetaData.getSimulationContextInfos();
			//while (enum1.hasMoreElements()){
				//SimulationContextInfo scInfo = (SimulationContextInfo)enum1.nextElement();
				//if (scInfo.getSimContextStatus()!=null){
					//worstErrorLevel = Math.max(worstErrorLevel,scInfo.getSimContextStatus().getErrorLevel());
				//}
			//}
			//return worstErrorLevel;
		//}catch (cbit.vcell.server.DataAccessException e){
			//e.printStackTrace();
		//}
	//}
	return BioModelNode.ERROR_NONE;
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

	firePropertyChange("documentManager", oldValue, documentManager);

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
