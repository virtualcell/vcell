package cbit.vcell.desktop;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.math.BigDecimal;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JTree;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;

import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.desktop.VCellBasicCellRenderer.VCDocumentInfoNode;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class MathModelDbTreeModel extends VCDocumentDbTreeModel {
	public static final String SHARED_MATH_MODELS = "Shared MathModels";
	
/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public MathModelDbTreeModel(JTree tree) {
	super(tree);
	educationModelsNode = new BioModelNode("Education", true);
	publicModelsNode = new BioModelNode("Public MathModels", true);	
}


/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
protected void createBaseTree() throws DataAccessException {
	if (rootNode.getChildCount() == 0) {
		rootNode.add(myModelsNode);
		rootNode.add(sharedModelsNode);
		rootNode.add(publicModelsNode);
		rootNode.add(educationModelsNode);
	}
	rootNode.setUserObject("Math Models");
	sharedModelsNode.setUserObject(SHARED_MATH_MODELS);
	
	MathModelInfo mathModelInfos[] = getDocumentManager().getMathModelInfos();
	//
	// get list of users (owners)
	//
	Vector<User> userList = new Vector<User>();
	User loginUser = getDocumentManager().getUser();
	userList.addElement(loginUser);
	for (int i=0;i<mathModelInfos.length;i++){
		MathModelInfo mathModelInfo = mathModelInfos[i];
		if (!userList.contains(mathModelInfo.getVersion().getOwner())){
			userList.addElement(mathModelInfo.getVersion().getOwner());
		}
	}
	//
	// for each user
	//
	TreeMap<String, BioModelNode> treeMap = new TreeMap<String, BioModelNode>();
	for (int ownerIndex=0;ownerIndex<userList.size();ownerIndex++){
		User owner = (User)userList.elementAt(ownerIndex);
		BioModelNode ownerNode = createOwnerSubTree(owner);
		if(owner.equals(loginUser) || ownerNode.getChildCount() > 0){
			treeMap.put(owner.getName(),ownerNode);
		}
	}
	//
	// create final tree
	//
	BioModelNode ownerNode = (BioModelNode)treeMap.remove(loginUser.getName());
	myModelsNode.removeAllChildren();
	myModelsNode.setUserObject(loginUser);
	for (int c = 0; c < ownerNode.getChildCount();) {
		BioModelNode childNode = (BioModelNode) ownerNode.getChildAt(c);
		myModelsNode.add(childNode);
	}
	sharedModelsNode.removeAllChildren();
	educationModelsNode.removeAllChildren();
	publicModelsNode.removeAllChildren();
	for (String username : treeMap.keySet()) {
		BioModelNode userNode = treeMap.get(username);
		BioModelNode parentNode = sharedModelsNode;
		boolean bSpecificUser = true;
		if (username.equals(USER_Education)) {
			parentNode = educationModelsNode;
		} else {
			bSpecificUser = false;
		}
		for (int c = 0; c < userNode.getChildCount();) {
			BioModelNode childNode = (BioModelNode) userNode.getChildAt(c);
			VCDocumentInfoNode vcdDocumentInfoNode = (VCDocumentInfoNode) childNode.getUserObject();
			if (!bSpecificUser) {
				parentNode = sharedModelsNode;
				BigDecimal groupid = GroupAccess.GROUPACCESS_NONE;
				Version version = vcdDocumentInfoNode.getVCDocumentInfo().getVersion();
				if (version != null && version.getGroupAccess() != null) {
					groupid = version.getGroupAccess().getGroupid();
				}
				if (groupid.equals(GroupAccess.GROUPACCESS_ALL)) {
					parentNode = publicModelsNode;
				}
			}
			// when added to other node, this childNode was removed from userNode
			parentNode.add(childNode);
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
private BioModelNode createOwnerSubTree(User owner) throws DataAccessException {
	MathModelInfo mathModelInfos[] = getDocumentManager().getMathModelInfos();
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