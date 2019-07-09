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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;

import cbit.vcell.client.desktop.DatabaseSearchPanel.SearchCriterion;
import cbit.vcell.client.desktop.biomodel.BioModelsNetModelInfo;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.BioModelNode.PublicationInfoNode;
import cbit.vcell.desktop.BioModelNode.UserNameNode;
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
	public static final String USER_tutorial610 = "tutorial60";
	public static final String USER_tutorial611 = "tutorial61";
	public static final String USER_modelBricks = "ModelBrick";
	
	protected BioModelNode tutorialModelsNode = null;
	protected BioModelNode educationModelsNode = null;
	protected BioModelNode bngRulesBasedModelsNode = null;
	protected BioModelNode bngRulesBasedModelsNode61 = null;
	
	protected BioModelNode allPublicModelsNode = null;	// contains folders only: Published, Curated and Other
	protected BioModelNode publishedModelsNode = null;
	protected BioModelNode curatedModelsNode = null;
	protected BioModelNode otherModelsNode = null;		// public models that are neither published nor curated
	
	protected BioModelNode modelBricksNode = null;
	
	public static final String Tutorials = "Tutorials";
	public static final String Education = "Education";
//	public static final String BNGRulesBased61 = "Tutorials Beta";
	
	public static final String SHARED_BIO_MODELS = "Shared With Me";
	public static final String Public_BioModels = "Public BioModels";
	public static final String Published_BioModels = "Published";
	public static final String Curated_BioModels = "Curated";
	public static final String Other_BioModels = "Uncurated";
	public static final String ModelBricks = "Model Bricks";
	
	public static final String SHARED_MATH_MODELS = "Shared With Me";
	public static final String Public_MathModels = "Public MathModels";
	public static final String Published_MathModels = "Published";
	public static final String Curated_MathModels = "Curated";
	public static final String Other_MathModels = "Uncurated";
	
	public static final String SHARED_GEOMETRIES = "Shared Geometries";
	public static final String PUBLIC_GEOMETRIES = "Public Geometries";
	
	public static final String ModelBricksNameSeparator = "::";
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
protected synchronized static void initFinalTree(VCDocumentDbTreeModel vcDocumentDbTreeModel, TreeMap<String, BioModelNode> treeMap, User loginUser){
	BioModelNode ownerNode = (BioModelNode)treeMap.get(loginUser.getName());
	BioModelNode tempNode = new BioModelNode();
	vcDocumentDbTreeModel.myModelsNode.setUserObject(loginUser);
	vcDocumentDbTreeModel.myModelsNode.removeAllChildren();
	
	for (int c = 0; c < ownerNode.getChildCount();) {		// c stays zero, during each iteration we keep extracting the first element
		BioModelNode childNode = (BioModelNode) ownerNode.getChildAt(c);
//		VCDocumentInfoNode vcdDocumentInfoNode = (VCDocumentInfoNode) childNode.getUserObject();
//		System.out.println(vcdDocumentInfoNode.getVCDocumentInfo().getVersion().getName());

		BioModelNode cloneNode = childNode.clone();
		vcDocumentDbTreeModel.myModelsNode.add(childNode);	// this slowly empties ownerNode

		boolean isPublic = false;
		for (int i = 0; i < childNode.getChildCount(); i++) {
			BioModelNode versionBioModelNode = (BioModelNode)childNode.getChildAt(i);
			VCDocumentInfo versionVCDocumentInfo = (VCDocumentInfo) versionBioModelNode.getUserObject();
			BigDecimal groupid = versionVCDocumentInfo.getVersion().getGroupAccess().getGroupid();
//			System.out.println("    " + groupid);
		
			if (groupid.equals(GroupAccess.GROUPACCESS_ALL)) {
				isPublic = true;
			}
			// use the form below if want to populate the Other folder with my Public models
//			if (groupid.equals(GroupAccess.GROUPACCESS_ALL)) {
			// use the form below to populate only the Published folder with my Published models
//			if(	versionVCDocumentInfo.getPublicationInfos() != null && versionVCDocumentInfo.getPublicationInfos().length > 0) {
//				BioModelNode versionCloneNode = versionBioModelNode.clone();
//				cloneNode.add(versionCloneNode);
//			}
		}
		if(isPublic) {
			BioModelNode publicClone = BioModelNode.deepClone(childNode);
			tempNode.add(publicClone);		// we add all login user models that have at least one public version
		}
		
//		if(cloneNode.getChildCount() > 0) {
//			tempNode.add(cloneNode);					// we keep adding only the clones of the public versions (if any) to the cloneNode
//		}
		// ownerNode gets empty eventually
	}
	for (int c = 0; c < tempNode.getChildCount();) {	// we put back in ownerNode the clone nodes with public versions
		BioModelNode childNode = (BioModelNode) tempNode.getChildAt(c);
		ownerNode.add(childNode);
	}

	// key is publicationKey, value is the list of model nodes associated with the PublicationInfo object
	LinkedHashMap<KeyValue, LinkedList<BioModelNode>> publishedModelsMap = new LinkedHashMap<>();
	LinkedHashMap<KeyValue, LinkedList<BioModelNode>> curatedModelsMap = new LinkedHashMap<>();
	
	// key is publicationKey, value is a PublicationInfo instance associated to the DOI
	LinkedHashMap<KeyValue, PublicationInfo> publicationsMap = new LinkedHashMap<>();
	
	vcDocumentDbTreeModel.sharedModelsNode.removeAllChildren();
		
	boolean bTutorial = vcDocumentDbTreeModel.tutorialModelsNode != null;
	boolean bEducation = vcDocumentDbTreeModel.educationModelsNode != null;
	boolean bModelBricks = vcDocumentDbTreeModel.modelBricksNode != null;
	boolean bPublished = vcDocumentDbTreeModel.publishedModelsNode != null;
	boolean bCurated = vcDocumentDbTreeModel.curatedModelsNode != null;
	boolean bOther = vcDocumentDbTreeModel.otherModelsNode != null;
	
	if(bTutorial){vcDocumentDbTreeModel.tutorialModelsNode.removeAllChildren();}
	if(bEducation){vcDocumentDbTreeModel.educationModelsNode.removeAllChildren();}
	if(bModelBricks){vcDocumentDbTreeModel.modelBricksNode.removeAllChildren();}
	if(bPublished){vcDocumentDbTreeModel.publishedModelsNode.removeAllChildren();}
	if(bCurated){vcDocumentDbTreeModel.curatedModelsNode.removeAllChildren();}
	if(bOther){vcDocumentDbTreeModel.otherModelsNode.removeAllChildren();}
	
	for (String username : treeMap.keySet()) {								// iterate through all users
		BioModelNode userNode = treeMap.get(username);
		BioModelNode parentNode = vcDocumentDbTreeModel.sharedModelsNode;	// initialize to something
		boolean bSpecificUser = true;
		if ((username.equals(USER_tutorial) || username.equals(USER_tutorial610) || username.equals(USER_tutorial611)) && bTutorial) {
			parentNode = vcDocumentDbTreeModel.tutorialModelsNode;
		} else if (username.equals(USER_Education) && bEducation) {
			parentNode = vcDocumentDbTreeModel.educationModelsNode;
		} else if (username.equals(USER_modelBricks) && bModelBricks) {
			parentNode = vcDocumentDbTreeModel.modelBricksNode;
		} else {
			bSpecificUser = false;
		}
		
		boolean added = false;
		UserNameNode userNameNode = new UserNameNode(username, true);
		for (int c = 0; c < userNode.getChildCount(); c++) {	// we just navigate through all of them, remove none
			BioModelNode childNode = (BioModelNode) userNode.getChildAt(c);
//			VCDocumentInfoNode vcdDocumentInfoNode = (VCDocumentInfoNode) childNode.getUserObject();
//			String name = vcdDocumentInfoNode.getVCDocumentInfo().getVersion().getName();
			
		// --------------------------------------------- 'other' public folder ---------------------------------------
			if (!bSpecificUser) {
//				if(username.equalsIgnoreCase(loginUser.getName())) {
//					break;			// TODO: we skip login user from now because it needs real time recalculation
//				}

				parentNode = vcDocumentDbTreeModel.otherModelsNode;

				BioModelNode cloneNode = childNode.clone();
				for (int i = 0; i < childNode.getChildCount(); i++) {
					BioModelNode versionBioModelNode = (BioModelNode)childNode.getChildAt(i);
					VCDocumentInfo versionVCDocumentInfo = (VCDocumentInfo) versionBioModelNode.getUserObject();
					BigDecimal groupid = versionVCDocumentInfo.getVersion().getGroupAccess().getGroupid();
//					System.out.println("    " + groupid);

					if (groupid.equals(GroupAccess.GROUPACCESS_ALL)) {
						if(	versionVCDocumentInfo.getPublicationInfos() != null && versionVCDocumentInfo.getPublicationInfos().length > 0) {
							continue;	// we don't show in "Other" anything that is published or curated
						}
						BioModelNode versionCloneNode = versionBioModelNode.clone();	// clone the public versions
						cloneNode.add(versionCloneNode);								// and add them to the clone node
					}
				}
				if(cloneNode.getChildCount() > 0) {
					userNameNode.add(cloneNode);					// we keep adding only the clones of the public versions (if any) to the cloneNode
					if(added == false) {
						parentNode.add(userNameNode);
						added = true;
					}
				}
			} else {		// anything belonging to users Education, Tutorial or modelBricks go to their own folders
				BioModelNode clone = BioModelNode.deepClone(childNode);
				parentNode.add(clone);
			}
		}
		
		// -------------------------------------------- shared folder -----------------------------------------------
		added = false;
		userNameNode = new UserNameNode(username, true);
		for (int c = 0; c < userNode.getChildCount(); c++) {	// we just navigate through all of them, remove none
			if(username.equalsIgnoreCase(loginUser.getName())) {
				break;			// the login user can't share with himself
			}
			
			BioModelNode childNode = (BioModelNode) userNode.getChildAt(c);
//			VCDocumentInfoNode vcdDocumentInfoNode = (VCDocumentInfoNode) childNode.getUserObject();
//			System.out.println(vcdDocumentInfoNode.getVCDocumentInfo().getVersion().getName());

			if (!bSpecificUser) {
				parentNode = vcDocumentDbTreeModel.sharedModelsNode;
				
				BioModelNode cloneNode = childNode.clone();
				for (int i = 0; i < childNode.getChildCount(); i++) {
					BioModelNode versionBioModelNode = (BioModelNode)childNode.getChildAt(i);
					VCDocumentInfo versionVCDocumentInfo = (VCDocumentInfo) versionBioModelNode.getUserObject();
					BigDecimal groupid = versionVCDocumentInfo.getVersion().getGroupAccess().getGroupid();
//					System.out.println("    " + groupid);
					if(groupid.equals(GroupAccess.GROUPACCESS_ALL) || groupid.equals(GroupAccess.GROUPACCESS_NONE)) {
						continue;		// we skip private and public versions (that includes published versions)
					}

					BioModelNode versionCloneNode = versionBioModelNode.clone();	// clone the shared versions
					cloneNode.add(versionCloneNode);								// and add them to the clone node
				}
				if(cloneNode.getChildCount() > 0) {
					userNameNode.add(cloneNode);		// we keep adding only the clones of the shared versions (if any) to the cloneNode
					if(added == false) {
						parentNode.add(userNameNode);	// now that we know for sure that the user name node is populated, we add it to the shared node if it's not there already
						added = true;
					}
				}
			}
		}
		// -------------------------------------------- published folder ------------------------------------------------

//		(username.contentEquals("CMC") || username.contentEquals("eungdamr") || username.contentEquals("Leon"))
//		if(username.contentEquals("boris") && vcDocumentDbTreeModel instanceof MathModelDbTreeModel) {
//			System.out.println("boris");
//		}
//		for (int c = 0; c < userNode.getChildCount();) {
//			BioModelNode versionableNode = (BioModelNode) userNode.getChildAt(c);
//			//Search through versions of BM/MM to see if any are published
//			for (int i = 0; i < versionableNode.getChildCount(); ) {
//				BioModelNode versionBioModelNode = (BioModelNode)versionableNode.getChildAt(i);
//				VCDocumentInfo versionVCDocumentInfo = (VCDocumentInfo) versionBioModelNode.getUserObject();
//				PublicationInfo[] pi = versionVCDocumentInfo.getPublicationInfos();
//				if(	pi != null && pi.length > 0) {
//					//Make new node
//					BioModelNode newPublishedNode = new BioModelNode(new VCDocumentInfoNode(versionVCDocumentInfo), true);
//					newPublishedNode.add(versionBioModelNode);
//					if(versionVCDocumentInfo.getVersion().getFlag().compareEqual(org.vcell.util.document.VersionFlag.Published)) {
//						vcDocumentDbTreeModel.publishedModelsNode.add(newPublishedNode);
//					} else {
//						vcDocumentDbTreeModel.curatedModelsNode.add(newPublishedNode);
//					}
//				} else {
//					versionableNode.remove(i);
//				}
//			}
//			userNode.remove(c);
//		}
//	}
		
		for (int c = 0; c < userNode.getChildCount(); ) {
			BioModelNode versionableNode = (BioModelNode) userNode.getChildAt(c);
			//Search through versions of BM/MM to see if any are published
			for (int i = 0; i < versionableNode.getChildCount(); ) {	// the versions
				BioModelNode versionBioModelNode = (BioModelNode)versionableNode.getChildAt(i);
				VCDocumentInfo versionVCDocumentInfo = (VCDocumentInfo) versionBioModelNode.getUserObject();
				PublicationInfo[] piArray = versionVCDocumentInfo.getPublicationInfos();
				if(	piArray != null && piArray.length > 0) {
					for(PublicationInfo pi : piArray) {
						KeyValue key = pi.getPublicationKey();
						if(!publicationsMap.containsKey(key)) {
							publicationsMap.put(key, pi);
						}
						if(versionVCDocumentInfo.getVersion().getFlag().compareEqual(org.vcell.util.document.VersionFlag.Published)) {
							// published
							LinkedList<BioModelNode> modelsList;
							if(publishedModelsMap.containsKey(key)) {
								modelsList = publishedModelsMap.get(key);
							} else {
								modelsList = new LinkedList<> ();
							}
							BioModelNode newPublishedNode = new BioModelNode(new VCDocumentInfoNode(versionVCDocumentInfo), true);
							BioModelNode clonedNode = versionBioModelNode.clone();
							newPublishedNode.add(clonedNode);
							modelsList.add(newPublishedNode);
							publishedModelsMap.put(key, modelsList);
							
						} else {
							// curated
							// the code below shows by author + model name
							BioModelNode newCuratedNode = new BioModelNode(new VCDocumentInfoNode(versionVCDocumentInfo), true);
							BioModelNode clonedNode = versionBioModelNode.clone();
							newCuratedNode.add(clonedNode);
							vcDocumentDbTreeModel.curatedModelsNode.add(newCuratedNode);
							
							// the code below shows by Publication Title
//							LinkedList<BioModelNode> modelsList;
//							if(curatedModelsMap.containsKey(key)) {
//								modelsList = curatedModelsMap.get(key);
//							} else {
//								modelsList = new LinkedList<> ();
//							}
//							BioModelNode newCuratedNode = new BioModelNode(new VCDocumentInfoNode(versionVCDocumentInfo), true);
//							BioModelNode clonedNode = versionBioModelNode.clone();
//							newCuratedNode.add(clonedNode);
//							modelsList.add(newCuratedNode);
//							curatedModelsMap.put(key, modelsList);
						}
					}
				}
				versionableNode.remove(i);
			}
			userNode.remove(c);
		}
	}

	shallowOrderByPublication(publishedModelsMap, publicationsMap);
	for(KeyValue key : publishedModelsMap.keySet()) {
		LinkedList<BioModelNode> modelsList = publishedModelsMap.get(key);
		PublicationInfo pi = publicationsMap.get(key);
		BioModelNode newPublicationNode = new BioModelNode.PublicationInfoNode(pi, true);
		for(BioModelNode node : modelsList) {
			newPublicationNode.add(node);
		}
		vcDocumentDbTreeModel.publishedModelsNode.add(newPublicationNode);
	}
//	for(KeyValue key : curatedModelsMap.keySet()) {
//		LinkedList<BioModelNode> modelsList = curatedModelsMap.get(key);
//		PublicationInfo pi = publicationsMap.get(key);
//		BioModelNode newCurationNode = new BioModelNode.PublicationInfoNode(pi, true);
//		for(BioModelNode node : modelsList) {
//			newCurationNode.add(node);
//		}
//		vcDocumentDbTreeModel.curatedModelsNode.add(newCurationNode);
//	}
//	vcDocumentDbTreeModel.publishedModelsNode = shallowSortByPublication(vcDocumentDbTreeModel.publishedModelsNode);
//	vcDocumentDbTreeModel.curatedModelsNode = shallowSortByPublication(vcDocumentDbTreeModel.curatedModelsNode);
}

private static void shallowOrderByPublication(LinkedHashMap<KeyValue, LinkedList<BioModelNode>> m, LinkedHashMap<KeyValue, PublicationInfo> p) {
	List<Map.Entry<KeyValue, LinkedList<BioModelNode>>> entries = new ArrayList<>(m.entrySet());
	Collections.sort(entries, new Comparator<Map.Entry<KeyValue, LinkedList<BioModelNode>>>() {
		@Override
		public int compare(Map.Entry<KeyValue, LinkedList<BioModelNode>> lhs, Map.Entry<KeyValue, LinkedList<BioModelNode>> rhs) {

			PublicationInfo lpi = p.get(lhs.getKey());
			PublicationInfo rpi = p.get(rhs.getKey());
			
			int ly = Integer.parseInt((new SimpleDateFormat("yyyy")).format(lpi.getPubDate()));
			String[] lt = lpi.getAuthors();
			int ry = Integer.parseInt((new SimpleDateFormat("yyyy")).format(rpi.getPubDate()));
			String[] rt = rpi.getAuthors();

			
			int ret = Integer.compare(ry, ly);
			if(ret == 0 && lt != null && rt != null && lt.length > 0 && rt.length > 0) {
				ret = lt[0].compareToIgnoreCase(rt[0]);
			}
			return ret;
		}
	});
	m.clear();
	for(Map.Entry<KeyValue, LinkedList<BioModelNode>> e : entries) {
		m.put(e.getKey(), e.getValue());
	}
}

protected synchronized static void initFinalTree2(VCDocumentDbTreeModel vcDocumentDbTreeModel, TreeMap<String, BioModelNode> treeMap, User loginUser){
	BioModelNode ownerNode = (BioModelNode)treeMap.get(loginUser.getName());
	BioModelNode tempNode = new BioModelNode();
	vcDocumentDbTreeModel.myModelsNode.setUserObject(loginUser);
	vcDocumentDbTreeModel.myModelsNode.removeAllChildren();
	for (int c = 0; c < ownerNode.getChildCount();) {
		BioModelNode childNode = (BioModelNode) ownerNode.getChildAt(c);
		BioModelNode clone = BioModelNode.deepClone(childNode);
		vcDocumentDbTreeModel.myModelsNode.add(clone);
		
		BigDecimal groupid = GroupAccess.GROUPACCESS_NONE;	// we keep in tempNode only those children that are public
		VCDocumentInfoNode vcdDocumentInfoNode = (VCDocumentInfoNode) childNode.getUserObject();
		Version version = vcdDocumentInfoNode.getVCDocumentInfo().getVersion();
		if (version != null && version.getGroupAccess() != null) {
			groupid = version.getGroupAccess().getGroupid();
		}
		if (groupid.equals(GroupAccess.GROUPACCESS_ALL)) {
			tempNode.add(childNode);	// also removes the child from owner node
		} else {
			ownerNode.remove(c);		// c always stays at 0
		}
		// ownerNode gets empty eventually
	}
	for (int c = 0; c < tempNode.getChildCount();) {		// we put back the public nodes for the login user
		BioModelNode childNode = (BioModelNode) tempNode.getChildAt(c);
		ownerNode.add(childNode);
	}
	
	vcDocumentDbTreeModel.sharedModelsNode.removeAllChildren();
	vcDocumentDbTreeModel.otherModelsNode.removeAllChildren();
	if(vcDocumentDbTreeModel.publishedModelsNode != null) {
		vcDocumentDbTreeModel.publishedModelsNode.removeAllChildren();
	}
	boolean bTutorial = vcDocumentDbTreeModel.tutorialModelsNode != null;
	boolean bEducation = vcDocumentDbTreeModel.educationModelsNode != null;
	boolean bModelBricks = vcDocumentDbTreeModel.modelBricksNode != null;
	if(bTutorial){vcDocumentDbTreeModel.tutorialModelsNode.removeAllChildren();}
	if(bEducation){vcDocumentDbTreeModel.educationModelsNode.removeAllChildren();}
	if(bModelBricks){vcDocumentDbTreeModel.modelBricksNode.removeAllChildren();}
	
	for (String username : treeMap.keySet()) {
		BioModelNode userNode = treeMap.get(username);
		BioModelNode parentNode = vcDocumentDbTreeModel.sharedModelsNode;
		boolean bSpecificUser = true;
		if ((username.equals(USER_tutorial) || username.equals(USER_tutorial610) || username.equals(USER_tutorial611)) && bTutorial) {
			parentNode = vcDocumentDbTreeModel.tutorialModelsNode;
		} else if (username.equals(USER_Education) && bEducation) {
			parentNode = vcDocumentDbTreeModel.educationModelsNode;
		} else if (username.equals(USER_modelBricks) && bModelBricks) {
			parentNode = vcDocumentDbTreeModel.modelBricksNode;
		} else {
			bSpecificUser = false;
		}
		for (int c = 0; c < userNode.getChildCount(); c++) {	// we just navigate through all of them, remove none
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
						parentNode = vcDocumentDbTreeModel.otherModelsNode;
				}
			}
			BioModelNode clone = BioModelNode.deepClone(childNode);
			parentNode.add(clone);
		}
		//Populate 'Published' models tree node
		for (int c = 0; c < userNode.getChildCount();) {
			BioModelNode versionableNode = (BioModelNode) userNode.getChildAt(c);
			//Search through versions of BM/MM to see if any are published
			for (int i = 0; i < versionableNode.getChildCount(); i++) {
				BioModelNode versionBioModelNode = (BioModelNode)versionableNode.getChildAt(i);
				VCDocumentInfo versionVCDocumentInfo = (VCDocumentInfo) versionBioModelNode.getUserObject();
				if(	versionVCDocumentInfo.getPublicationInfos() != null && 
					versionVCDocumentInfo.getPublicationInfos().length > 0) {
					//Make new node
					BioModelNode newPublishedNode = new BioModelNode(new VCDocumentInfoNode(versionVCDocumentInfo),true);
					newPublishedNode.add(versionBioModelNode);
					vcDocumentDbTreeModel.publishedModelsNode.add(newPublishedNode);
				}
			}
			userNode.remove(c);
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
	
	if(allPublicModelsNode != null) {
		TreePath path = new TreePath(allPublicModelsNode.getPath());
		getOwnerTree().expandPath(path);
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

protected BioModelNode getPublicNode() {
	return allPublicModelsNode;
}
protected JTree getOwnerTree() {
	return ownerTree;
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
