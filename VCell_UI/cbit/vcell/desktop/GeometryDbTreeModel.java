package cbit.vcell.desktop;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.*;
import java.util.Vector;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
public class GeometryDbTreeModel extends javax.swing.tree.DefaultTreeModel implements cbit.vcell.client.database.DatabaseListener {
	private boolean fieldLatestOnly = false;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.client.database.DocumentManager fieldDocumentManager = null;

/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public GeometryDbTreeModel() {
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
	GeometryInfo geometryInfos[] = getDocumentManager().getGeometryInfos();
	BioModelNode rootRootNode = new BioModelNode("geometries",true);
	//
	// get list of users (owners)
	//
	Vector ownerList = new Vector();
	ownerList.addElement(getDocumentManager().getUser());
	for (int i=0;i<geometryInfos.length;i++){
		GeometryInfo geometryInfo = geometryInfos[i];
		if (!ownerList.contains(geometryInfo.getVersion().getOwner())){
			ownerList.addElement(geometryInfo.getVersion().getOwner());
		}
	}
	//
	// for each user
	//
	java.util.TreeMap treeMap = new java.util.TreeMap();
	for (int ownerIndex=0;ownerIndex<ownerList.size();ownerIndex++){
		User owner = (User)ownerList.elementAt(ownerIndex);
		BioModelNode ownerNode = createOwnerSubTree(owner);
		if(owner.equals(getDocumentManager().getUser()) || ownerNode.getChildCount() > 0){
			treeMap.put(owner.getName().toLowerCase(),ownerNode);
		}
	}
	//
	rootRootNode.add((BioModelNode)treeMap.remove(getDocumentManager().getUser().getName().toLowerCase()));
	BioModelNode otherUsersNode = new BioModelNode("Shared Geometries",true);
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
	GeometryInfo geometryInfos[] = getDocumentManager().getGeometryInfos();
	//
	// for each user
	//
	BioModelNode rootNode = new BioModelNode(owner,true);
	for (int i=0;i<geometryInfos.length;i++){
		GeometryInfo geometryInfo = geometryInfos[i];
		if (geometryInfo.getVersion().getOwner().equals(owner) && geometryInfo.getDimension()>0){
			BioModelNode bioModelNode = new BioModelNode(geometryInfo.getVersion().getName(),true);
			rootNode.add(bioModelNode);
			//
			// get list of geometries with the same branch
			//
			Vector geometryBranchList = new Vector();
			geometryBranchList.addElement(geometryInfo);
			for (i=i+1;i<geometryInfos.length;i++){
				if (geometryInfos[i].getVersion().getBranchID().equals(geometryInfo.getVersion().getBranchID()) && geometryInfos[i].getDimension()>0){
					geometryBranchList.add(0,geometryInfos[i]);
				}else{
					i--;
					break;
				}
			}
			GeometryInfo geometryInfosInBranch[] = null;
			if (getLatestOnly()){
				geometryInfosInBranch = new GeometryInfo[1];
				geometryInfosInBranch[0] = (GeometryInfo)geometryBranchList.elementAt(0);
			}else{
				geometryInfosInBranch = new GeometryInfo[geometryBranchList.size()];
				geometryBranchList.copyInto(geometryInfosInBranch);
			}
			for (int versionCount=0;versionCount<geometryInfosInBranch.length;versionCount++){
				bioModelNode.add(createVersionSubTree(geometryInfosInBranch[versionCount]));
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
private BioModelNode createVersionSubTree(GeometryInfo geoInfo) throws DataAccessException {
	
	BioModelNode versionNode = new BioModelNode(geoInfo,false);
	
	//if (geoInfo.getVersion().getAnnot()!=null && geoInfo.getVersion().getAnnot().length()>0){
		//versionNode.add(new BioModelNode(new Annotation(geoInfo.getVersion().getAnnot()),false));
	//}
	//versionNode.add(new BioModelNode(geoInfo.getExtent(),false));
	//if (geoInfo.getImageRef()!=null){
		//versionNode.add(new BioModelNode("Image ("+geoInfo.getImageRef()+")",false));
	//}
	//BioModelInfo bioModelInfoArray[] = getDocumentManager().getBioModelReferences(geoInfo);
	//if (bioModelInfoArray!=null && bioModelInfoArray.length>0){
		//BioModelNode bioAppNode = new BioModelNode("BioModel references",true);
		//for (int i=0;i<bioModelInfoArray.length;i++){
			//bioAppNode.add(new BioModelNode(bioModelInfoArray[i],false));
		//}
		//versionNode.add(bioAppNode);
	//}
	//MathModelInfo mathModelInfoArray[] = getDocumentManager().getMathModelReferences(geoInfo);
	//if (mathModelInfoArray!=null && mathModelInfoArray.length>0){
		//BioModelNode mathAppNode = new BioModelNode("MathModel references",true);
		//for (int i=0;i<mathModelInfoArray.length;i++){
			//mathAppNode.add(new BioModelNode(mathModelInfoArray[i],false));
		//}
		//versionNode.add(mathAppNode);
	//}
	
	return versionNode;
}


/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseDelete(cbit.vcell.client.database.DatabaseEvent databaseEvent) {
	if (databaseEvent.getOldVersionInfo() instanceof GeometryInfo){
		GeometryInfo removedGeometryInfo = (GeometryInfo)databaseEvent.getOldVersionInfo();
		BioModelNode removedNode = ((BioModelNode)getRoot()).findNodeByUserObject(removedGeometryInfo);
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
public void databaseInsert(cbit.vcell.client.database.DatabaseEvent databaseEvent) {
	if (databaseEvent.getNewVersionInfo() instanceof GeometryInfo){
		try {
			GeometryInfo insertedGeometryInfo = (GeometryInfo)databaseEvent.getNewVersionInfo();
			//
			// get parent of updated version
			//
			//   model1                           (String)
			//      Fri Sept 2, 2001 12:00:00     (GeometryInfo)
			//      Fri Sept 1, 2001 10:00:00     (GeometryInfo)
			//
			//
			BioModelNode newVersionNode = createVersionSubTree(insertedGeometryInfo);
			//
			// find owner node (if it is displayed)
			//
			User owner = insertedGeometryInfo.getVersion().getOwner();
			BioModelNode ownerRoot = ((BioModelNode)getRoot()).findNodeByUserObject(owner);
			BioModelNode parentNode = null;
			if (ownerRoot!=null){
				parentNode = ownerRoot.findNodeByUserObject(insertedGeometryInfo.getVersion().getName());
			}
			if (parentNode==null){
				//
				// fresh insert 
				// Have to create parent node, for all versions of this geometry, 
				// and stick it in the correct order in the tree.
				//
				parentNode = new BioModelNode(insertedGeometryInfo.getVersion().getName(),true);
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
public void databaseRefresh(cbit.vcell.client.database.DatabaseEvent event) {

	//Our parent will tell us what to do
}


/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseUpdate(cbit.vcell.client.database.DatabaseEvent databaseEvent) {
	//
	// the ClientDocumentManager usually throws an UPDATE when changing public/private status
	//
	if (databaseEvent.getOldVersionInfo() instanceof GeometryInfo){
		System.out.println("GeometryDbTreeModel.databaseUpdate(), refreshing entire tree");
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
public cbit.vcell.client.database.DocumentManager getDocumentManager() {
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
	if (getDocumentManager()!=null){
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
public void setDocumentManager(cbit.vcell.client.database.DocumentManager documentManager) {
	cbit.vcell.client.database.DocumentManager oldValue = fieldDocumentManager;
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