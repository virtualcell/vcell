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
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;

import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.desktop.VCellBasicCellRenderer.VCDocumentInfoNode;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class MathModelDbTreeModel extends VCDocumentDbTreeModel {	
/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public MathModelDbTreeModel(JTree tree) {
	super(tree);
	tutorialModelsNode = new BioModelNode(Tutorials, true);
//	educationModelsNode = new BioModelNode(Education, true);	// this may go to curated
	allPublicModelsNode = new BioModelNode(Public_MathModels, true);

	publishedModelsNode = new BioModelNode(Published_MathModels, true);
	curatedModelsNode = new BioModelNode(Curated_MathModels, true);
	otherModelsNode = new BioModelNode(Other_MathModels, true);
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
protected void createBaseTree() throws DataAccessException {
	
	rootNode.removeAllChildren();
	
	rootNode.add(myModelsNode);
	rootNode.add(sharedModelsNode);
//	rootNode.add(tutorialModelsNode);
//	rootNode.add(educationModelsNode);
	rootNode.add(allPublicModelsNode);
	
	allPublicModelsNode.add(publishedModelsNode);
	allPublicModelsNode.add(curatedModelsNode);
	allPublicModelsNode.add(otherModelsNode);
	
	rootNode.setUserObject("Math Models");
	sharedModelsNode.setUserObject(SHARED_MATH_MODELS);
	
	MathModelInfo mathModelInfos[] = getDocumentManager().getMathModelInfos();
	User loginUser = getDocumentManager().getUser();
	TreeMap<String, BioModelNode> treeMap = null;
	try{
		treeMap = VCDocumentDbTreeModel.initOwners(mathModelInfos, loginUser, this, this.getClass().getMethod("createOwnerSubTree", new Class[] {User.class,MathModelInfo[].class}));
	}catch(Exception e){
		e.printStackTrace();
		treeMap = new TreeMap<String,BioModelNode>();
		treeMap.put(loginUser.getName(), new BioModelNode("Error:"+e.getMessage()));
	}
	initFinalTree(this, treeMap, loginUser);
}
protected void createBaseTree2() throws DataAccessException {
	VCDocumentDbTreeModel.initBaseTree(rootNode,
			new BioModelNode[] {myModelsNode,sharedModelsNode,otherModelsNode,/*educationModelsNode,*/publishedModelsNode},
			"Math Models", sharedModelsNode, SHARED_MATH_MODELS);
	
	MathModelInfo mathModelInfos[] = getDocumentManager().getMathModelInfos();
	User loginUser = getDocumentManager().getUser();
	TreeMap<String, BioModelNode> treeMap = null;
	try{
		treeMap = VCDocumentDbTreeModel.initOwners(mathModelInfos, loginUser, this, this.getClass().getMethod("createOwnerSubTree", new Class[] {User.class,MathModelInfo[].class}));
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
public BioModelNode createOwnerSubTree(User owner, MathModelInfo mathModelInfos[]) throws DataAccessException {
	//
	// for each user
	//
	BioModelNode rootNode = new BioModelNode(owner,true);
	for (int i=0;i<mathModelInfos.length;i++){
		MathModelInfo mathModelInfo = mathModelInfos[i];
		if (mathModelInfo.getVersion().getOwner().equals(owner)){
			if (!meetSearchCriteria(mathModelInfo)) {
				continue;
			}
			BioModelNode bioModelNode = new BioModelNode(new VCDocumentInfoNode(mathModelInfo),true);
			rootNode.add(bioModelNode);
			//
			// get list of bioModels with the same branch
			//
			Vector<MathModelInfo> mathModelBranchList = new Vector<MathModelInfo>();
			mathModelBranchList.addElement(mathModelInfo);
			for (i=i+1;i<mathModelInfos.length;i++){
				if (mathModelInfos[i].getVersion().getBranchID().equals(mathModelInfo.getVersion().getBranchID())){
					if (!meetSearchCriteria(mathModelInfos[i])) {
						continue;
					}
					mathModelBranchList.add(0,mathModelInfos[i]);
				} else {
					i--;
					break;
				}
			}
			MathModelInfo mathModelInfosInBranch[] = null;
			if (getLatestOnly()){
				mathModelInfosInBranch = new MathModelInfo[1];
				mathModelInfosInBranch[0] = (MathModelInfo)mathModelBranchList.elementAt(0);
			}else{
				mathModelInfosInBranch = new MathModelInfo[mathModelBranchList.size()];
				mathModelBranchList.copyInto(mathModelInfosInBranch);
			}
			for (int versionCount=0;versionCount<mathModelInfosInBranch.length;versionCount++){
				bioModelNode.add(createVersionSubTree(mathModelInfosInBranch[versionCount]));
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
private BioModelNode createVersionSubTree(MathModelInfo mmInfo) throws DataAccessException {
	//MathModelMetaData mathModelMetaData = getDocumentManager().getMathModelMetaData(mmInfo);
	//if (mathModelMetaData==null){
		//return null;
	//}
	BioModelNode versionNode = new BioModelNode(mmInfo,false);
	////
	//// add children of the BioModel to the node passed in
	////
	//if (mathModelMetaData.getVersion().getAnnot()!=null && mathModelMetaData.getVersion().getAnnot().length()>0){
		//versionNode.add(new BioModelNode(new Annotation(mathModelMetaData.getVersion().getAnnot()),false));
	//}

	////
	//// add simulations to mathModel
	////
	//Enumeration simEnum = mathModelMetaData.getSimulationInfos();
	//while (simEnum.hasMoreElements()){
		//SimulationInfo simInfo = (SimulationInfo)simEnum.nextElement();
		//BioModelNode simNode = new BioModelNode(simInfo,true);
		//versionNode.add(simNode);
		//if (simInfo.getVersion().getAnnot()!=null && simInfo.getVersion().getAnnot().length()>0){
			//simNode.add(new BioModelNode(new Annotation(simInfo.getVersion().getAnnot()),false));
		//}
		////
		//// add resultSet (optional) to simulation
		////
		//Enumeration rsEnum = mathModelMetaData.getResultSetInfos();
		//while (rsEnum.hasMoreElements()){
			//SolverResultSetInfo rsInfo = (SolverResultSetInfo)rsEnum.nextElement();
			//if (rsInfo.getSimulationInfo().getVersion().getVersionKey().equals(simInfo.getVersion().getVersionKey())){
				//BioModelNode rsNode = new BioModelNode(rsInfo,false);
				//simNode.add(rsNode);
			//}
		//}
	//}
	return versionNode;
}


/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseDelete(DatabaseEvent databaseEvent) {
	if (databaseEvent.getOldVersionInfo() instanceof MathModelInfo){
		MathModelInfo removedMathModelInfo = (MathModelInfo)databaseEvent.getOldVersionInfo();
		BioModelNode removedNode = ((BioModelNode)getRoot()).findNodeByUserObject(removedMathModelInfo);
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
	if (databaseEvent.getNewVersionInfo() instanceof MathModelInfo){
		try {
			MathModelInfo insertedMathModelInfo = (MathModelInfo)databaseEvent.getNewVersionInfo();
			//
			// get parent of updated version
			//
			//   model1                           (VCDocumentInfoNode)
			//      Fri Sept 2, 2001 12:00:00     (MathModelInfo)
			//      Fri Sept 1, 2001 10:00:00     (MathModelInfo)
			//
			//
			BioModelNode newVersionNode = createVersionSubTree(insertedMathModelInfo);
			//
			// find owner node (if it is displayed)
			//
			User owner = insertedMathModelInfo.getVersion().getOwner();
			BioModelNode ownerRoot = ((BioModelNode)getRoot()).findNodeByUserObject(owner);
			BioModelNode parentNode = null;
			if (ownerRoot!=null){
				parentNode = ownerRoot.findNodeByUserObject(new VCDocumentInfoNode(insertedMathModelInfo));
			}
			if (parentNode==null){
				//
				// fresh insert 
				// Have to create parent node, for all versions of this mathModel, 
				// and stick it in the correct order in the tree.
				//
				parentNode = new BioModelNode(new VCDocumentInfoNode(insertedMathModelInfo),true);
				parentNode.insert(newVersionNode,0);
				//
				// if owner node exists, add MathModel parent and fire events to notify of the insertion
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
				nodeStructureChanged(parentNode);
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
	if (databaseEvent.getOldVersionInfo() instanceof MathModelInfo){
		BioModelNode node = ((BioModelNode)getRoot()).findNodeByUserObject(databaseEvent.getOldVersionInfo());
		node.setUserObject(databaseEvent.getNewVersionInfo());
		nodeChanged(node);
	}
}
}
