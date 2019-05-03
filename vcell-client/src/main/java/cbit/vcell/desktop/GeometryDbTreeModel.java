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

import javax.swing.JTree;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.desktop.VCellBasicCellRenderer.VCDocumentInfoNode;
import cbit.vcell.geometry.GeometryInfo;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class GeometryDbTreeModel extends VCDocumentDbTreeModel {
/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public GeometryDbTreeModel(JTree tree) {
	super(tree);
	otherModelsNode = new BioModelNode(PUBLIC_GEOMETRIES, true);	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
protected void createBaseTree() throws DataAccessException {
	VCDocumentDbTreeModel.initBaseTree(rootNode, new BioModelNode[] {myModelsNode,sharedModelsNode,otherModelsNode}, "Geometries", sharedModelsNode, SHARED_GEOMETRIES);
	
	GeometryInfo geometryInfos[] = getDocumentManager().getGeometryInfos();
	User loginUser = getDocumentManager().getUser();
	TreeMap<String, BioModelNode> treeMap = null;
	try{
		treeMap = VCDocumentDbTreeModel.initOwners(geometryInfos, loginUser, this, this.getClass().getMethod("createOwnerSubTree", new Class[] {User.class,GeometryInfo[].class}));
	}catch(Exception e){
		e.printStackTrace();
		treeMap = new TreeMap<String,BioModelNode>();
		treeMap.put(loginUser.getName(), new BioModelNode("Error:"+e.getMessage()));
	}
	
	initFinalTree(this, treeMap, loginUser);
}

/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
public BioModelNode createOwnerSubTree(User owner, GeometryInfo geometryInfos[]) throws DataAccessException {
	//
	// for each user
	//
	BioModelNode rootNode = new BioModelNode(owner,true);
	for (int i=0;i<geometryInfos.length;i++){
		GeometryInfo geometryInfo = geometryInfos[i];
		if (geometryInfo.getVersion().getOwner().equals(owner) && geometryInfo.getDimension()>0){
			if (!meetSearchCriteria(geometryInfo)) {
				continue;
			}
			BioModelNode bioModelNode = new BioModelNode(new VCDocumentInfoNode(geometryInfo),true);
			rootNode.add(bioModelNode);
			//
			// get list of geometries with the same branch
			//
			Vector<GeometryInfo> geometryBranchList = new Vector<GeometryInfo>();
			geometryBranchList.addElement(geometryInfo);
			for (i = i + 1; i < geometryInfos.length; i ++){
				if (geometryInfos[i].getVersion().getBranchID().equals(geometryInfo.getVersion().getBranchID()) && geometryInfos[i].getDimension()>0){
					if (!meetSearchCriteria(geometryInfos[i])) {
						continue;
					}
					geometryBranchList.add(0,geometryInfos[i]);
				} else {
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
public void databaseDelete(DatabaseEvent databaseEvent) {
	if (databaseEvent.getOldVersionInfo() instanceof GeometryInfo){
		GeometryInfo removedGeometryInfo = (GeometryInfo)databaseEvent.getOldVersionInfo();
		BioModelNode removedNode = ((BioModelNode)getRoot()).findNodeByUserObject(removedGeometryInfo);
		if (removedNode != null) {
			if (removedNode.getParent()!=null && removedNode.getSiblingCount()==1){ // just this one version
				removedNode = (BioModelNode)removedNode.getParent();
			}
			removeNodeFromParent(removedNode);
		}
	}
}


/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseInsert(DatabaseEvent databaseEvent) {
	if (databaseEvent.getNewVersionInfo() instanceof GeometryInfo){
		try {
			GeometryInfo insertedGeometryInfo = (GeometryInfo)databaseEvent.getNewVersionInfo();
			//
			// get parent of updated version
			//
			//   model1                           (VCDocumentInfoNode)
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
				parentNode = ownerRoot.findNodeByUserObject(new VCDocumentInfoNode(insertedGeometryInfo));
			}
			if (parentNode==null){
				//
				// fresh insert 
				// Have to create parent node, for all versions of this geometry, 
				// and stick it in the correct order in the tree.
				//
				parentNode = new BioModelNode(new VCDocumentInfoNode(insertedGeometryInfo),true);
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
	if (databaseEvent.getOldVersionInfo() instanceof GeometryInfo){
		System.out.println("GeometryDbTreeModel.databaseUpdate(), refreshing entire tree");
		BioModelNode node = ((BioModelNode)getRoot()).findNodeByUserObject(databaseEvent.getOldVersionInfo());
		node.setUserObject(databaseEvent.getNewVersionInfo());
		nodeChanged(node);
	}
}
}
