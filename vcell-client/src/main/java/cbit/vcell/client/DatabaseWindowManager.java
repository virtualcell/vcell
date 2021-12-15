/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.CurateSpec;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.GroupAccessSome;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.ReferenceQueryResult;
import org.vcell.util.document.ReferenceQuerySpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.DocumentCreationInfo;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.VersionableRelationship;
import org.vcell.util.document.VersionableType;
import org.vcell.util.document.VersionableTypeVersion;
import org.vcell.util.gui.AsynchProgressPopup;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.VCFileChooser;
import org.vcell.util.gui.exporter.FileFilters;

import cbit.image.BrowseImage;
import cbit.image.GIFImage;
import cbit.image.GifParsingException;
import cbit.image.ImageException;
import cbit.image.VCImageInfo;
import cbit.image.VCImageUncompressed;
import cbit.image.VCPixelClass;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.ACLEditor;
import cbit.vcell.client.desktop.DatabaseWindowPanel;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.BioModelDbTreePanel;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.desktop.BioModelNode.PublicationInfoNode;
import cbit.vcell.desktop.GeometryTreePanel;
import cbit.vcell.desktop.MathModelDbTreePanel;
import cbit.vcell.desktop.VCDocumentDbTreePanel;
import cbit.vcell.desktop.VCellBasicCellRenderer.VCDocumentInfoNode;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.xml.ExternalDocInfo;
import cbit.xml.merge.XmlTreeDiff;
/**
 * Insert the type's description here.
 * Creation date: (5/14/2004 5:06:46 PM)
 * @author: Ion Moraru
 */
public class DatabaseWindowManager extends TopLevelWindowManager{

	class  DoubleClickListener implements java.awt.event.ActionListener {
		private JDialog theJDialog = null;
		private boolean bWasDoubleClicked = false;
		
		DoubleClickListener(JDialog dialog) {
			theJDialog = dialog;
		}
		
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if(e.getActionCommand().equals(BM_MM_GM_DOUBLE_CLICK_ACTION)){
				bWasDoubleClicked = true;
				theJDialog.dispose();				
			}			
		}

		boolean wasDoubleClicked() {
			return bWasDoubleClicked;
		}
	}	
		
	private BioModelDbTreePanel bioModelDbTreePanel = new BioModelDbTreePanel();
	private ACLEditor aclEditor = new ACLEditor();
	private GeometryTreePanel geometryTreePanel = new GeometryTreePanel();
	private MathModelDbTreePanel MathModelDbTreePanel = new MathModelDbTreePanel();

	public static final String BM_MM_GM_DOUBLE_CLICK_ACTION = "bm_mm_gm_dca";


	private DatabaseWindowPanel databaseWindowPanel = null;

/**
 * Insert the method's description here.
 * Creation date: (5/17/2004 1:50:08 PM)
 * @param vcellClient cbit.vcell.client.VCellClient
 */
public DatabaseWindowManager(DatabaseWindowPanel databaseWindowPanel, RequestManager requestManager) {
	super(requestManager);
	setDatabaseWindowPanel(databaseWindowPanel);
	getBioModelDbTreePanel().setPopupMenuDisabled(true);
	getMathModelDbTreePanel().setPopupMenuDisabled(true);
	getGeometryTreePanel().setPopupMenuDisabled(true);
	
	getBioModelDbTreePanel().setDocumentManager(requestManager.getDocumentManager());
	getMathModelDbTreePanel().setDocumentManager(requestManager.getDocumentManager());
	getGeometryTreePanel().setDocumentManager(requestManager.getDocumentManager());
}

public void accessPermissions()  {
	VersionInfo selectedVersionInfo = getPanelSelection() == null ? null : getPanelSelection();
	accessPermissions(getComponent(), selectedVersionInfo, false);
}

public void copyName()  {
	VersionInfo selectedVersionInfo = getPanelSelection() == null ? null : getPanelSelection();
	if(selectedVersionInfo != null) {
		Version version = selectedVersionInfo.getVersion();
		String name = version.getName();
		StringSelection data = new StringSelection(name);
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		c.setContents(data, data);

	}
}

/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:35:55 PM)
 */
public void accessPermissions(final Component requester, final VersionInfo selectedVersionInfo, boolean bGrantSupportPermissions)  {
	final GroupAccess groupAccess = selectedVersionInfo.getVersion().getGroupAccess();
	final DocumentManager docManager = getRequestManager().getDocumentManager();
	
	AsynchClientTask task1 = new AsynchClientTask("show dialog", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			getAclEditor().clearACLList();	
			getAclEditor().setACLState(new ACLEditor.ACLState(groupAccess));
			if(bGrantSupportPermissions) {
				getAclEditor().grantVCellSupportPermissions();
			}
			Object choice = showAccessPermissionDialog(getAclEditor(), requester);
			if (choice != null) {
				hashTable.put("choice", choice);
			}		
		}		
	};
	
	AsynchClientTask task2 = new AsynchClientTask("access permission", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			Object choice = hashTable.get("choice");
			if (choice != null && choice.equals("OK")) {
				ACLEditor.ACLState aclState = getAclEditor().getACLState();
				if (aclState != null) {
					if (aclState.isAccessPrivate() || (aclState.getAccessList() != null && aclState.getAccessList().length == 0)) {						
						VersionInfo vInfo = null;
						if(selectedVersionInfo instanceof BioModelInfo){
							vInfo = docManager.setGroupPrivate((BioModelInfo)selectedVersionInfo);
						}else if(selectedVersionInfo instanceof MathModelInfo){
							vInfo = docManager.setGroupPrivate((MathModelInfo)selectedVersionInfo);
						}else if(selectedVersionInfo instanceof GeometryInfo){
							vInfo = docManager.setGroupPrivate((GeometryInfo)selectedVersionInfo);
						}else if(selectedVersionInfo instanceof VCImageInfo){
							vInfo = docManager.setGroupPrivate((VCImageInfo)selectedVersionInfo);
						}
					} else if (aclState.isAccessPublic()) {
						VersionInfo vInfo = null;
						if(selectedVersionInfo instanceof BioModelInfo){
							vInfo = docManager.setGroupPublic((BioModelInfo)selectedVersionInfo);
						}else if(selectedVersionInfo instanceof MathModelInfo){
							vInfo = docManager.setGroupPublic((MathModelInfo)selectedVersionInfo);
						}else if(selectedVersionInfo instanceof GeometryInfo){
							vInfo = docManager.setGroupPublic((GeometryInfo)selectedVersionInfo);
						}else if(selectedVersionInfo instanceof VCImageInfo){
							vInfo = docManager.setGroupPublic((VCImageInfo)selectedVersionInfo);
						}
					} else {
						String[] aclUserNames = aclState.getAccessList();
						String[] originalGroupAccesNames = new String[0];
						//Turn User[] into String[]
						if (groupAccess instanceof GroupAccessSome){
							GroupAccessSome gas = (GroupAccessSome)groupAccess;
							User[] originalUsers = gas.getNormalGroupMembers();
							for(int i=0;i<originalUsers.length;i+= 1){
								originalGroupAccesNames = (String[])BeanUtils.addElement(originalGroupAccesNames,originalUsers[i].getName());
							}
						}
						//Determine users needing adding
						String[] needToAddUsers = new String[0];
						for(int i=0;i<aclUserNames.length;i+= 1){
							if(!BeanUtils.arrayContains(originalGroupAccesNames,aclUserNames[i])){
								System.out.println("Added user="+aclUserNames[i]);
								needToAddUsers = (String[])BeanUtils.addElement(needToAddUsers,aclUserNames[i]);
							}
						}
						//Determine users needing removing
						String[] needToRemoveUsers = new String[0];
						for(int i=0;i<originalGroupAccesNames.length;i+= 1){
							if(!BeanUtils.arrayContains(aclUserNames,originalGroupAccesNames[i])){
								System.out.println("Removed user="+originalGroupAccesNames[i]);
								needToRemoveUsers = (String[])BeanUtils.addElement(needToRemoveUsers,originalGroupAccesNames[i]);
							}
						}
						
						VersionInfo vInfo = null;
						String errorNames = "";
						//Add Users to Group Access List
						for (int i = 0; i < needToAddUsers.length; i++) {
							try {
								if(selectedVersionInfo instanceof BioModelInfo){
									vInfo = docManager.addUserToGroup((BioModelInfo)selectedVersionInfo, needToAddUsers[i]);
								}else if(selectedVersionInfo instanceof MathModelInfo){
									vInfo = docManager.addUserToGroup((MathModelInfo)selectedVersionInfo, needToAddUsers[i]);
								}else if(selectedVersionInfo instanceof GeometryInfo){
									vInfo = docManager.addUserToGroup((GeometryInfo)selectedVersionInfo, needToAddUsers[i]);
								}else if(selectedVersionInfo instanceof VCImageInfo){
									vInfo = docManager.addUserToGroup((VCImageInfo)selectedVersionInfo, needToAddUsers[i]);
								}
							} catch (ObjectNotFoundException e) {
								errorNames += "Error changing permissions.\n" + selectedVersionInfo.getVersionType().getTypeName() 
									+ " \"" + selectedVersionInfo.getVersion().getName() + "\" edition (" + selectedVersionInfo.getVersion().getDate() + ")\nnot found, " 
									+ "your model list may be out of date, please go to menu Server->Reconnect to refresh the model list" +"\n";
								break;
							} catch (DataAccessException e) {
								errorNames += "Error adding user '"+needToAddUsers[i]+"' : "+e.getMessage()+"\n";
							}
						}
						// Remove users from Group Access List
						for (int i = 0; i < needToRemoveUsers.length; i++) {
							try {
								if(selectedVersionInfo instanceof BioModelInfo){
									vInfo = docManager.removeUserFromGroup((BioModelInfo)selectedVersionInfo, needToRemoveUsers[i]);
								}else if(selectedVersionInfo instanceof MathModelInfo){
									vInfo = docManager.removeUserFromGroup((MathModelInfo)selectedVersionInfo, needToRemoveUsers[i]);
								}else if(selectedVersionInfo instanceof GeometryInfo){
									vInfo = docManager.removeUserFromGroup((GeometryInfo)selectedVersionInfo, needToRemoveUsers[i]);
								}else if(selectedVersionInfo instanceof VCImageInfo){
									vInfo = docManager.removeUserFromGroup((VCImageInfo)selectedVersionInfo, needToRemoveUsers[i]);
								}
							} catch (DataAccessException e) {
								errorNames += "Error Removing user '"+needToRemoveUsers[i]+"'\n  -----"+e.getMessage()+"\n";
							}
						}
						if (errorNames.length() > 0) {
							if(DatabaseWindowManager.this.getComponent() != null){
								PopupGenerator.showErrorDialog(DatabaseWindowManager.this, errorNames);
							}else{
								DialogUtils.showErrorDialog(requester, errorNames);
							}
							accessPermissions(requester, selectedVersionInfo, false);
						}
					}
		
				}
			}
		}
	};
	
	ClientTaskDispatcher.dispatch(requester, new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2});
}


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 8:54:01 AM)
 */
public void archive() {

	getRequestManager().curateDocument(getPanelSelection(),CurateSpec.ARCHIVE,this);
}

//public void compareAnotherEdition1() {

public void batchOmexPublished() {

	Map <KeyValue, BioModelInfo> publishedModelMap = new LinkedHashMap<> ();
	BioModelDbTreePanel bmdbtp = getBioModelDbTreePanel();
	BioModelInfo[] bmInfos = bmdbtp.getDocumentManager().getBioModelInfos();
	for(BioModelInfo bmi : bmInfos) {
		if(bmi instanceof VCDocumentInfo) {
			VCDocumentInfo versionVCDocumentInfo = (VCDocumentInfo) bmi;
			if(	versionVCDocumentInfo.getPublicationInfos() != null && 
					versionVCDocumentInfo.getPublicationInfos().length > 0) {
				publishedModelMap.put(bmi.getModelKey(), bmi);
			}
		}
	}
	for (Map.Entry<KeyValue, BioModelInfo> entry : publishedModelMap.entrySet()) {
		KeyValue key = entry.getKey();
		BioModelInfo bmi = entry.getValue();
		System.out.println(bmi.getPublicationInfos()[0].getTitle());
		
// must make array of tasks here, or something... anyway, can't call from swing thread
//		try {
//			BioModel bm = bmdbtp.getDocumentManager().getBioModel(bmi);
//			doSomeOmexThing();
//		} catch (DataAccessException e) {
//			e.printStackTrace();
//		}

	}
}
/**
 * Comment
 */
public void compareAnotherEdition() {
	//
	// get selected DocumentInfo info from original Tree.
	//
	if (getPanelSelection()==null){
		PopupGenerator.showErrorDialog(this, "Error Comparing documents : No first document selected");
		return;
	}
	VCDocumentInfo thisDocumentInfo = getPanelSelection();
	//
	// Get the previous version of the documentInfo
	//
	VCDocumentInfo[] documentVersionsList = null;
	try {
		documentVersionsList = getDocumentVersionDates(thisDocumentInfo);
	} catch (DataAccessException e) {
		PopupGenerator.showErrorDialog(this, "Error accessing second document!");
	}
	
	if (documentVersionsList == null || documentVersionsList.length == 0) {
		PopupGenerator.showErrorDialog(this, "Error Comparing documents : Not Enough Versions to Compare!");
		return;
	}

	//
	// Obtaining the Dates of the versions as a String, to be displayed as a list
	//
	String versionDatesList[] = new String[documentVersionsList.length];
	for (int i = 0; i < documentVersionsList.length; i++) {
		versionDatesList[i] = documentVersionsList[i].getVersion().getDate().toString();
	}

	//
	// Get the user's choice of document version from the list box, use it to get the documentInfo for the
	// corresponding version 
	//

	String newVersionChoice = (String)PopupGenerator.showListDialog(this, versionDatesList, "Please select edition");

	if (newVersionChoice == null) {
		PopupGenerator.showErrorDialog(this, "Error Comparing documents : Second document not selected!");
		return;
	}

	int versionIndex = -1;
	for (int i=0;i < versionDatesList.length;i++){
		if (versionDatesList[i].equals(newVersionChoice)){
			versionIndex = i;
		}
	}
	if (versionIndex == -1){
		PopupGenerator.showErrorDialog(this, "Error Comparing documents : No such Version Exists "+newVersionChoice);
		return;
	}

	VCDocumentInfo anotherDocumentInfo = documentVersionsList[versionIndex];
	// Check if both document types are of the same kind. If not, throw an error. 
	if (((thisDocumentInfo instanceof BioModelInfo) && !(anotherDocumentInfo instanceof BioModelInfo)) ||
		((thisDocumentInfo instanceof MathModelInfo) && !(anotherDocumentInfo instanceof MathModelInfo)) ||
		((thisDocumentInfo instanceof GeometryInfo) && !(anotherDocumentInfo instanceof GeometryInfo))) {
		PopupGenerator.showErrorDialog(this, "Error Comparing documents : The two documents are not of the same type!");
		return;
	}
	// Now that we have both the document versions to be compared, do the comparison and display the result
	compareWithOther(anotherDocumentInfo, thisDocumentInfo);
}

/**
 * Comment
 */
public void compareAnotherModel() {
	//
	// get selected DocumentInfo info from original Tree.
	//
	if (getPanelSelection()==null){
		PopupGenerator.showErrorDialog(this, "Error Comparing documents : First document not selected");
		return;
	}
	VCDocumentInfo thisDocumentInfo = getPanelSelection();

	// Choose the other documentInfo. Bring up the appropriate dbTreePanel depending on the type of thisDocumentInfo
	VCDocumentInfo otherDocumentInfo = null;
	try{
		otherDocumentInfo = selectDocument(thisDocumentInfo.getVCDocumentType(), this);
	}catch(Exception e){
		if(!(e instanceof UserCancelException)){
			e.printStackTrace();
			DialogUtils.showErrorDialog(this.getComponent(), "Error Comparing documents: "+e.getMessage());
		}
		return;
	}

	if (otherDocumentInfo == null){
		//PopupGenerator.showErrorDialog(this, "Error Comparing documents : Second document is null ");
		return;
	}
	// Check if both document types are of the same kind. If not, throw an error. 
	if (((thisDocumentInfo instanceof BioModelInfo) && !(otherDocumentInfo instanceof BioModelInfo)) ||
		((thisDocumentInfo instanceof MathModelInfo) && !(otherDocumentInfo instanceof MathModelInfo)) ||
		((thisDocumentInfo instanceof GeometryInfo) && !(otherDocumentInfo instanceof GeometryInfo))) {
		PopupGenerator.showErrorDialog(this, "Error Comparing documents : The two documents are not of the same type!");
		return;
	}
	// Now that we have both the document versions to be compared, do the comparison and display the result
	compareWithOther(otherDocumentInfo, thisDocumentInfo);
}


/**
 * Comment
 */
public void compareLatestEdition()  {
	//
	// get selected DocumentInfo info from original Tree.
	//
	if (getPanelSelection()==null){
		PopupGenerator.showErrorDialog(this, "Error Comparing documents : No first document selected");
		return;
	}
	VCDocumentInfo thisDocumentInfo = getPanelSelection();

	//
	// Get the latest version of the documentInfo
	//
	VCDocumentInfo[] documentVersionsList = null;
	try {
		documentVersionsList = getDocumentVersionDates(thisDocumentInfo);
	} catch (DataAccessException e) {
		PopupGenerator.showErrorDialog(this, "Error accessing second document!");
	}
	
	if (documentVersionsList == null || documentVersionsList.length == 0) {
		PopupGenerator.showErrorDialog(this, "Error Comparing documents : Not Enough Versions to Compare!");
		return;
	}
	//
	// Obtaining the latest version of the current documentInfo
	//
	VCDocumentInfo latestDocumentInfo = documentVersionsList[documentVersionsList.length-1];

	for (int i = 0; i < documentVersionsList.length; i++) {
		if (documentVersionsList[i].getVersion().getDate().after(latestDocumentInfo.getVersion().getDate())) {
			latestDocumentInfo = documentVersionsList[i];
		}
	}

	if (thisDocumentInfo.getVersion().getDate().after(latestDocumentInfo.getVersion().getDate())) {
		PopupGenerator.showErrorDialog(this, "Current Version is the latest! Choose another Version or Model to compare!");
		return;
	}

	// Check if both document types are of the same kind. If not, throw an error. 
	if (((thisDocumentInfo instanceof BioModelInfo) && !(latestDocumentInfo instanceof BioModelInfo)) ||
		((thisDocumentInfo instanceof MathModelInfo) && !(latestDocumentInfo instanceof MathModelInfo)) ||
		((thisDocumentInfo instanceof GeometryInfo) && !(latestDocumentInfo instanceof GeometryInfo))) {
		PopupGenerator.showErrorDialog(this, "Error Comparing documents : The two documents are not of the same type!");
		return;
	}
	//
	// Now that we have both the document versions to be compared, do the comparison and display the result
	//
	compareWithOther(latestDocumentInfo, thisDocumentInfo);
}


/**
 * Comment
 */
public void comparePreviousEdition()  {
	//
	// get selected DocumentInfo info from original Tree.
	//
	if (getPanelSelection()==null){
		PopupGenerator.showErrorDialog(this, "Error Comparing documents : No first document selected");
		return;
	}
	VCDocumentInfo thisDocumentInfo = getPanelSelection();
	//
	// Get the previous version of the documentInfo
	//
	VCDocumentInfo[] documentVersionsList = null;
	try {
		documentVersionsList = getDocumentVersionDates(thisDocumentInfo);
	} catch (DataAccessException e) {
		PopupGenerator.showErrorDialog(this, "Error accessing second document!");
	}
	
	if (documentVersionsList == null || documentVersionsList.length == 0) {
		PopupGenerator.showErrorDialog(this, "Error Comparing documents : Not Enough Versions to Compare!");
		return;
	}
	//
	// Obtaining the previous version of the current biomodel. Set the previousBioModelInfo to
	// the first version in the bioModelVersionList. Then compare all the versions in the list
	// with the previousBioModelInfo to see if any of them are before previousBioModelInfo
	// datewise. If so, update previousBioModelInfo. The biomodelinfo stored in previousBioModelInfo
	// when it comes out of the loop is the previous version of the biomodel.
	//
	VCDocumentInfo previousDocumentInfo = documentVersionsList[0];
	boolean bPrevious = false;

	for (int i = 0; i < documentVersionsList.length; i++) {
		if (documentVersionsList[i].getVersion().getDate().before(thisDocumentInfo.getVersion().getDate())) {
			bPrevious = true;
			previousDocumentInfo = documentVersionsList[i];
		} else {
			break;
		}
	}

	if (previousDocumentInfo.equals(documentVersionsList[0]) && !bPrevious) {
		PopupGenerator.showErrorDialog(this, "Current Version is the oldest! Choose another Version or Model to compare!");
		return;
	}

	// Check if both document types are of the same kind. If not, throw an error. 
	if (((thisDocumentInfo instanceof BioModelInfo) && !(previousDocumentInfo instanceof BioModelInfo)) ||
		((thisDocumentInfo instanceof MathModelInfo) && !(previousDocumentInfo instanceof MathModelInfo)) ||
		((thisDocumentInfo instanceof GeometryInfo) && !(previousDocumentInfo instanceof GeometryInfo))) {
		PopupGenerator.showErrorDialog(this, "Error Comparing documents : The two documents are not of the same type!");
		return;
	}

	// Now that we have both the document versions to be compared, do the comparison and display the result
	compareWithOther(previousDocumentInfo, thisDocumentInfo);
}


//Processes the model comparison,
	private void compareWithOther(final VCDocumentInfo docInfo1, final VCDocumentInfo docInfo2) {
		
		final MDIManager mdiManager = new ClientMDIManager(getRequestManager());       
		mdiManager.blockWindow(getManagerID());
		String taskName = "Comparing " + docInfo1.getVersion().getName() + " with " + docInfo2.getVersion().getName(); 
		AsynchClientTask task1 = new AsynchClientTask(taskName, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				XmlTreeDiff xmlTreeDiff = getRequestManager().compareWithOther(docInfo1, docInfo2);
				hashTable.put("xmlTreeDiff", xmlTreeDiff);
			}			
		};
		AsynchClientTask task2 = new AsynchClientTask(taskName, AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, false) {

			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				try {
					if (hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR) == null) {
						XmlTreeDiff xmlTreeDiff = (XmlTreeDiff)hashTable.get("xmlTreeDiff");
						String baselineDesc = docInfo1.getVersion().getName() + ", " + docInfo1.getVersion().getDate();
						String modifiedDesc = docInfo2.getVersion().getName() + ", " + docInfo2.getVersion().getDate();
						getRequestManager().showComparisonResults(DatabaseWindowManager.this, xmlTreeDiff, baselineDesc, modifiedDesc);						
					}
				} finally {
					mdiManager.unBlockWindow(getManagerID());
				}
			}
		};
		ClientTaskDispatcher.dispatch(getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, false);
	}

/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 9:49:06 PM)
 */
public void deleteSelected() {
	getRequestManager().deleteDocument(getPanelSelection(), this);
}

public void exportDocument() {
	if (getPanelSelection() != null) {
		getRequestManager().exportDocument(this,null);
	}
	else {
		PopupGenerator.showInfoDialog(this, "no model selected");
	}
}

/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 9:15:25 AM)
 */
public void findModelsUsingSelectedGeometry() {
	AsynchClientTask findModelsTask = new AsynchClientTask("Finding Models...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			VCDocumentInfo selectedDocument = getPanelSelection();
			
			if(!(selectedDocument instanceof GeometryInfo)){
				PopupGenerator.showErrorDialog(DatabaseWindowManager.this, "DatabaseWindowManager.findModelsUsingSelectedGeometry expected a GeometryInfo\nbut got type="+selectedDocument.getClass().getName()+" instead");
				return;
			}

			ReferenceQuerySpec rqs = new ReferenceQuerySpec(VersionableType.Geometry,selectedDocument.getVersion().getVersionKey());
//			try{
				ReferenceQueryResult rqr = getRequestManager().getDocumentManager().findReferences(rqs);
				//cbit.vcell.modeldb.VersionableTypeVersion[] children = (rqr.getVersionableFamily().bChildren()?rqr.getVersionableFamily().getUniqueChildren():null);
				VersionableTypeVersion[] dependants = (rqr.getVersionableFamily().bDependants()?rqr.getVersionableFamily().getUniqueDependants():null);
				//System.out.println("\n");
				//if(children != null){
					//for(int i=0;i<children.length;i+= 1){
						//if( children[i] != rqr.getVersionableFamily().getTarget()){
							//System.out.println("Children "+children[i]+" key="+children[i].getVersion().getVersionKey()+" date="+children[i].getVersion().getDate());
						//}
					//}
				//}else{
					//System.out.println("No Children");
				//}

				//if(dependants != null){
					//for(int i=0;i<dependants.length;i+= 1){
						//if( dependants[i] != rqr.getVersionableFamily().getTarget()){
							//System.out.println("Dependants "+dependants[i]+" key="+dependants[i].getVersion().getVersionKey()+" date="+dependants[i].getVersion().getDate());
						//}
					//}
				//}else{
					//System.out.println("No Dependants");
				//}

				//System.out.println("\nVersionableRelationships");
				//cbit.vcell.modeldb.VersionableRelationship[] vrArr = rqr.getVersionableFamily().getDependantRelationships();
				//for(int i=0;i<vrArr.length;i+= 1){
					//System.out.println(vrArr[i].from() +" -> "+vrArr[i].to());
				//}

				Hashtable<String, Object> choices = new Hashtable<String, Object>();
				if(dependants != null){
					//System.out.println("\nMajor Relationships");
					for(int i=0;i<dependants.length;i+= 1){
						boolean isBioModel = dependants[i].getVType().equals(VersionableType.BioModelMetaData);
						boolean isTop = isBioModel || dependants[i].getVType().equals(VersionableType.MathModelMetaData);
						if(isTop){
							VersionableRelationship[] vrArr2 = rqr.getVersionableFamily().getDependantRelationships();
							for(int j=0;j<vrArr2.length;j+= 1){
								if( (vrArr2[j].from() == dependants[i]) &&
									vrArr2[j].to().getVType().equals((isBioModel?VersionableType.SimulationContext:VersionableType.MathDescription))){
										for(int k=0;k<vrArr2.length;k+= 1){
											if( (vrArr2[k].from() == vrArr2[j].to()) &&
												vrArr2[k].to().getVType().equals(VersionableType.Geometry)){
													String s = (isBioModel?"BioModel":"MathModel")+"  "+
														"\""+dependants[i].getVersion().getName()+"\"  ("+dependants[i].getVersion().getDate() +")"+
														(isBioModel?" (App=\""+vrArr2[k].from().getVersion().getName()+"\")"/*+" -> "*/:"");
														//+" Geometry="+vrArr2[k].to().getVersion().getName()+" "+vrArr2[k].to().getVersion().getDate();
													choices.put(s,dependants[i]);
													//System.out.println(s);
												}
										}
								}
							}
						}
						
					}
				}

				if(choices.size() > 0){
					Object[] listObj = choices.keySet().toArray();
					Object o = DialogUtils.showListDialog(getComponent(),listObj,"Models Referencing Geometry (Select To Open) "+selectedDocument.getVersion().getName()+" "+selectedDocument.getVersion().getDate());
					if(o != null){
						VersionableTypeVersion v = (VersionableTypeVersion)choices.get(o);
						//System.out.println(v);
						if(v.getVType().equals(VersionableType.BioModelMetaData)){
							BioModelInfo bmi = getRequestManager().getDocumentManager().getBioModelInfo(v.getVersion().getVersionKey());
							getRequestManager().openDocument(bmi,DatabaseWindowManager.this,true);
						}else if(v.getVType().equals(VersionableType.MathModelMetaData)){
							MathModelInfo mmi = getRequestManager().getDocumentManager().getMathModelInfo(v.getVersion().getVersionKey());
							getRequestManager().openDocument(mmi,DatabaseWindowManager.this,true);
						}
					}
				}else{
					if(dependants == null){
						DialogUtils.showInfoDialog(getComponent(),
							"No Model references found.\n"+
							(rqr.getVersionableFamily().getTarget().getVersion().getFlag().isArchived()?"Info: Not Deletable (key="+rqr.getVersionableFamily().getTarget().getVersion().getVersionKey()+") because legacy ARCHIVE set":""));
					}else{
						DialogUtils.showInfoDialog(getComponent(),
							"No current Model references found.\n"+
							"Geometry has internal database references from\n"+
							"previously linked Model(s).\n"+
							"Not Deletable until database is culled (daily).");
					}
//					return;
				}
//			}catch(DataAccessException e){
//				DialogUtils.showErrorDialog(getComponent(), "Error find Geometry Model references\n"+e.getClass().getName()+"\n"+e.getMessage());
//			}
		}
	};
	ClientTaskDispatcher.dispatch(getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] {findModelsTask}, false);
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:39:00 PM)
 * @return cbit.vcell.desktop.BioModelDbTreePanel
 */
private ACLEditor getAclEditor() {
	return aclEditor;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:39:00 PM)
 * @return cbit.vcell.desktop.BioModelDbTreePanel
 */
public BioModelDbTreePanel getBioModelDbTreePanel() {
	return bioModelDbTreePanel;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 1:08:29 AM)
 * @return java.lang.String
 */
public java.awt.Component getComponent() {
	return getDatabaseWindowPanel();
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:28:00 PM)
 * @return cbit.vcell.client.desktop.DatabaseWindowPanel
 */
public DatabaseWindowPanel getDatabaseWindowPanel() {
	return databaseWindowPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/2002 10:34:00 AM)
 */
private VCDocumentInfo[] getDocumentVersionDates(VCDocumentInfo thisDocumentInfo) throws DataAccessException {
	//
	// Get list of VCDocumentInfos in workspace
	//
    if (thisDocumentInfo==null){
	    return new VCDocumentInfo[0];
    }

    VCDocumentInfo vcDocumentInfos[] = null;
    if (thisDocumentInfo instanceof BioModelInfo) {
		vcDocumentInfos = getRequestManager().getDocumentManager().getBioModelInfos();
    } else if (thisDocumentInfo instanceof MathModelInfo) {
   		vcDocumentInfos = getRequestManager().getDocumentManager().getMathModelInfos();
    }  else if (thisDocumentInfo instanceof GeometryInfo) {
   		vcDocumentInfos = getRequestManager().getDocumentManager().getGeometryInfos();
    }

	//
	// From the list of biomodels in the workspace, get list of biomodels with the same branch ID.
	// This is the list of different versions of the same biomodel.
	//
 	Vector<VCDocumentInfo> documentBranchList = new Vector<VCDocumentInfo>();
 	for (int i = 0; i < vcDocumentInfos.length; i++) {
	 	VCDocumentInfo vcDocumentInfo = vcDocumentInfos[i];
	 	if (vcDocumentInfo.getVersion().getBranchID().equals(thisDocumentInfo.getVersion().getBranchID())) {
		 	documentBranchList.add(vcDocumentInfo);
	 	}
 	}

 	if (documentBranchList.size() == 0) {
	 	PopupGenerator.showErrorDialog(this, "Error comparing BioModels : No Versions of document ");
	 	return new VCDocumentInfo[0];
 	}

 	VCDocumentInfo vcDocumentInfosInBranch[] = new VCDocumentInfo[documentBranchList.size()];
 	documentBranchList.copyInto(vcDocumentInfosInBranch);

 	//
 	// From the versions list, remove the currently selected version and return the remaining list of
 	// versions for the biomodel
 	//

 	VCDocumentInfo revisedDocInfosInBranch[] = new VCDocumentInfo[vcDocumentInfosInBranch.length-1];
 	int j=0;
 	
 	for (int i = 0; i < vcDocumentInfosInBranch.length; i++) {
		if (!thisDocumentInfo.getVersion().getDate().equals(vcDocumentInfosInBranch[i].getVersion().getDate())) {
			revisedDocInfosInBranch[j] = vcDocumentInfosInBranch[i];
			j++;
		}
 	}
			 	
	return revisedDocInfosInBranch;	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:39:00 PM)
 * @return cbit.vcell.desktop.GeometryTreePanel
 */
public GeometryTreePanel getGeometryTreePanel() {
	return geometryTreePanel;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:51:36 AM)
 * @return java.lang.String
 */
public java.lang.String getManagerID() {
	// there's only one of these...
	return ClientMDIManager.DATABASE_WINDOW_ID;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:39:00 PM)
 * @return cbit.vcell.desktop.MathModelDbTreePanel
 */
public MathModelDbTreePanel getMathModelDbTreePanel() {
	return MathModelDbTreePanel;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 3:01:47 PM)
 * @return cbit.vcell.document.VCDocumentInfo
 */
public VCDocumentInfo getPanelSelection() {
	return getDatabaseWindowPanel().getSelectedDocumentInfo();
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:28:23 PM)
 */
public void initializeAll() {
	AsynchClientTask task1 = new AsynchClientTask("initializeAll", AsynchClientTask.TASKTYPE_SWING_NONBLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {		
			try {
				DocumentManager documentManager = getRequestManager().getDocumentManager();
				getBioModelDbTreePanel().setDocumentManager(documentManager);
				getMathModelDbTreePanel().setDocumentManager(documentManager);
				getGeometryTreePanel().setDocumentManager(documentManager);
//				getDatabaseWindowPanel().setDocumentManager(documentManager);
//				getImageBrowser().getImageDbTreePanel1().setDocumentManager(documentManager);
			} catch (Throwable exc) {
				exc.printStackTrace(System.out);
			}
		}
	};
	ClientTaskDispatcher.dispatch(null, new Hashtable<String, Object>(), new AsynchClientTask[] {task1});
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:35:55 PM)
 */
public boolean isOwnerUserEqual()  {
	User currentUser = getRequestManager().getDocumentManager().getUser();
	User selectedDocOwner = null;
	if (getPanelSelection() != null) {
		selectedDocOwner = getPanelSelection().getVersion().getOwner();
	}

	// Check if the current user is the owner of current selection in database panel.
	// If so, return true to enable the edit and access permissions menu items on Database window.
	// (these buttons should be disabled if user isn't owner of current selection on database window).
	
	if (Compare.isEqual(currentUser,selectedDocOwner)) {
		return true;
	} else {
		return false;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 3:31:47 PM)
 * @return boolean
 */
public boolean isRecyclable() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 9:49:06 PM)
 */
public void openLatest() {

	VCDocumentInfo latestDocumentInfo = null;
	if (getPanelSelection() != null) {
		VCDocumentInfo thisDocumentInfo = getPanelSelection();
		//
		// Get the latest version of the documentInfo
		//
		VCDocumentInfo[] documentVersionsList = null;
		try {
			documentVersionsList = getDocumentVersionDates(thisDocumentInfo);
		} catch (DataAccessException e) {
			PopupGenerator.showErrorDialog(this, "Error accessing document!");
		}
		
		//
		// Obtaining the latest version of the current documentInfo
		//
		if (documentVersionsList != null && documentVersionsList.length > 0) {
			latestDocumentInfo = documentVersionsList[documentVersionsList.length-1];

			for (int i = 0; i < documentVersionsList.length; i++) {
				if (documentVersionsList[i].getVersion().getDate().after(latestDocumentInfo.getVersion().getDate())) {
					latestDocumentInfo = documentVersionsList[i];
				}
			}

			if (thisDocumentInfo.getVersion().getDate().after(latestDocumentInfo.getVersion().getDate())) {
				latestDocumentInfo = thisDocumentInfo;
			}
		} else {
			latestDocumentInfo = thisDocumentInfo;
		}
	} else {
		PopupGenerator.showErrorDialog(this, "Error Opening Latest Document : no document currently selected.");
		return;
	}	
	getRequestManager().openDocument(latestDocumentInfo, this, true);
}

public void createNewGeometry(){
	AsynchClientTask editSelectTask = new AsynchClientTask("Edit/Apply Geometry", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Geometry newGeom = (Geometry)hashTable.get("doc");
				if(newGeom == null){
					throw new Exception("No Geometry found in edit task");
				}
				DocumentCreationInfo documentCreationInfo = new DocumentCreationInfo(VCDocumentType.GEOMETRY_DOC, -1);
				documentCreationInfo.setPreCreatedDocument(newGeom);
				AsynchClientTask[] newGeometryTaskArr = 
					getRequestManager().newDocument(DatabaseWindowManager.this, documentCreationInfo);
				ClientTaskDispatcher.dispatch(DatabaseWindowManager.this.getComponent(), hashTable, newGeometryTaskArr);
			}
		};

	createGeometry(null, new AsynchClientTask[] {editSelectTask},TopLevelWindowManager.DEFAULT_CREATEGEOM_SELECT_DIALOG_TITLE,"Create Geometry",null);
}

public void openSelected() {
	openSelected(this, true);
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 9:49:06 PM)
 */
public void openSelected(TopLevelWindowManager requester, boolean bInNewWindow) {
	getRequestManager().openDocument(getPanelSelection(), requester, bInNewWindow);
}


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 8:54:11 AM)
 */
public void publish() {
	
	getRequestManager().curateDocument(getPanelSelection(),CurateSpec.PUBLISH,this);	
}


private static class SelectGeomHover extends MouseMotionAdapter {
	public final Object[] lastModelChildInfoHolder = new Object[] {null};
	private Geometry selection;
	private JPopupMenu jpop;
	VCDocumentDbTreePanel myLocalVCDocDBTreePanel;
	public SelectGeomHover(VCDocumentDbTreePanel vcDocDBTreePanel) {
		this.myLocalVCDocDBTreePanel = vcDocDBTreePanel;
	}
	public Geometry getSelection() {
		return selection;
	}
	public void setSelection(Geometry selection) {
		this.selection = selection;
	}
	private void showGeomForSelection(VCDocumentInfo vcDocInfo,String simOwnerName) throws Exception {
		AsynchProgressPopup pp = null;
		try {
			pp = new AsynchProgressPopup(myLocalVCDocDBTreePanel, null, null, true, false, false, null);
			pp.setMessage("Loading "+simOwnerName);
			pp.startKeepOnTop();
			SimulationOwner simulationOwner =  (vcDocInfo instanceof BioModelInfo?
					myLocalVCDocDBTreePanel.getDocumentManager().getBioModel(((BioModelInfo)vcDocInfo)).getSimulationContext(simOwnerName):
						myLocalVCDocDBTreePanel.getDocumentManager().getMathModel(((MathModelInfo)vcDocInfo)));

			pp.setMessage("Creating image "+simOwnerName);
			VCImageUncompressed currentValue =
				(VCImageUncompressed) simulationOwner.getMathDescription().getGeometry().getGeometrySpec().createSampledImage(
						simulationOwner.getMathDescription().getGeometry().getGeometrySpec().getDefaultSampledImageSize());
			//scale For viewing
			pp.setMessage("Scaling image "+simOwnerName);
			if (currentValue.getNumPixelClasses() > 1) {
				VCPixelClass[] newPC = new VCPixelClass[currentValue.getNumPixelClasses()];
				for (int i = 0; i < newPC.length; i++) {
					//						       (outMax-outMin)(inVal - inMin)
					//						       ------------------------------ + outMin
					//						                 inMax - inMin
					newPC[i] = new VCPixelClass(
							currentValue.getPixelClasses(i).getKey(),
							currentValue.getPixelClasses(i).getPixelClassName(),
							((127 - 0) * (currentValue.getPixelClasses(i).getPixel() - 0)) / (newPC.length - 1) + 0);
					for(int j=0;j<currentValue.getPixels().length;j++) {
						if((int)(currentValue.getPixels()[j]&0x000000FF) == currentValue.getPixelClasses(i).getPixel()) {
							currentValue.getPixels()[j] = (byte)(newPC[i].getPixel()&0x000000FF);
						}
					}
				}
				currentValue.setPixelClasses(newPC);
			}
			pp.setMessage("Showing image "+simOwnerName);
			GIFImage makeBrowseGIFImage = BrowseImage.makeBrowseGIFImage2(currentValue);
			ImageIcon ii = new ImageIcon(makeBrowseGIFImage.getGifEncodedData());
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JScrollPane jsp = new JScrollPane(new JLabel(ii));
					jsp.setPreferredSize(new Dimension(300,400));
					String SEL = "Select";
					String select = DialogUtils.showOptionsDialog(
						myLocalVCDocDBTreePanel.getJTree1(), jsp, JOptionPane.QUESTION_MESSAGE, new String[] {"Back",SEL}, "Back", null,
						"View Geometry (x="+currentValue.getNumX()+" y="+currentValue.getNumY()+" z="+currentValue.getNumZ()+")");
					if(select != null &&  select.equals(SEL)) {
						//Save geom selection, Click 'Open' button
						setSelection(simulationOwner.getMathDescription().getGeometry());
						Container myWindow = BeanUtils.findTypeParentOfComponent(myLocalVCDocDBTreePanel, Window.class);
						ArrayList<Component> comps = new ArrayList<Component>();
						BeanUtils.findComponent(myWindow, JButton.class, comps);
						for(int i=0;i<comps.size();i++) {
							if(((JButton)comps.get(i)).getText().equals("Open")) {
								((JButton)comps.get(i)).setEnabled(true);
								((JButton)comps.get(i)).doClick();
								return;
							}
						}
					}else {
						//keep trying to select
						return;
					}
				}});
		} finally {
			pp.stop();
		}
	}
	private VCDocument getModelInfoWithProgress(VCDocumentInfo vcDocInfo) throws DataAccessException {
		AsynchProgressPopup pp = null;
		try {
			pp = new AsynchProgressPopup(myLocalVCDocDBTreePanel, null, null, true, false, false, null);
			pp.setMessage("Loading "+vcDocInfo.getVersion().getName());
			pp.startKeepOnTop();
			return (vcDocInfo instanceof BioModelInfo?
				myLocalVCDocDBTreePanel.getDocumentManager().getBioModel(((BioModelInfo)vcDocInfo)):
					myLocalVCDocDBTreePanel.getDocumentManager().getMathModel(((MathModelInfo)vcDocInfo)));
		}finally {
			pp.stop();
		}
	}
	private boolean processPopupMenu(Object lastModelChildInfo) {
		if(jpop != null && jpop.isShowing()) {
			if(lastModelChildInfoHolder[0] == lastModelChildInfo/*mathModelInfo.getMathModelChildSummary()*/) {
				return false;
			}
		}
		lastModelChildInfoHolder[0] = lastModelChildInfo;
		if(jpop == null) {
			jpop = new JPopupMenu();
			jpop.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		}else {
			jpop.removeAll();
		}
		return true;
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		TreePath pathForLocation = myLocalVCDocDBTreePanel.getJTree1().getPathForLocation(e.getX(), e.getY());
		if(pathForLocation != null &&
			((pathForLocation.getLastPathComponent() instanceof BioModelNode && ((BioModelNode)pathForLocation.getLastPathComponent()).getUserObject() instanceof VCDocumentInfoNode) ||
			pathForLocation.getLastPathComponent() instanceof PublicationInfoNode)) {
			if(jpop != null && jpop.isShowing()) {
				jpop.setVisible(false);
			}
			myLocalVCDocDBTreePanel.getJTree1().expandPath(pathForLocation);
		}else if(pathForLocation != null && pathForLocation.getLastPathComponent() instanceof BioModelNode) {
			BioModelNode lastPathComponent = (BioModelNode)pathForLocation.getLastPathComponent();
			if(lastPathComponent.getUserObject() instanceof MathModelInfo) {
				MathModelInfo mathModelInfo = (MathModelInfo)lastPathComponent.getUserObject();
//				if(jpop != null && jpop.isShowing()) {
//					if(lastModelChildInfoHolder[0] == mathModelInfo.getMathModelChildSummary()) {
//						return;
//					}
//				}
//				lastModelChildInfoHolder[0] = mathModelInfo.getMathModelChildSummary();
				if(!processPopupMenu(mathModelInfo.getMathModelChildSummary())) {
					return;
				}
				JMenuItem menuItem = new JMenuItem("Geom("+mathModelInfo.getMathModelChildSummary().getGeometryDimension()+"):"+mathModelInfo.getMathModelChildSummary().getGeometryName()+"");
				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									//MathModel mathModel = (MathModel) getModelInfoWithProgress(mathModelInfo);
									showGeomForSelection(mathModelInfo,mathModelInfo.getVersion().getName());
								} catch (Exception e1) {
									e1.printStackTrace();
								}								
							}}).start();
					}});
//				if(jpop == null) {
//					jpop = new JPopupMenu();
//					jpop.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
//				}else {
//					jpop.removeAll();
//				}
				jpop.add(menuItem);
				jpop.show(myLocalVCDocDBTreePanel.getJTree1(),e.getX(),e.getY());
			}else if(lastPathComponent.getUserObject() instanceof BioModelInfo) {
				BioModelInfo bioModelInfo = (BioModelInfo)lastPathComponent.getUserObject();
				BioModelChildSummary bioModelChildSummary = bioModelInfo.getBioModelChildSummary();
				if(bioModelChildSummary != null && bioModelChildSummary.getSimulationContextNames() != null && bioModelChildSummary.getSimulationContextNames().length>0) {
					if(!processPopupMenu(bioModelChildSummary)) {
						return;
					}

//					if(jpop != null && jpop.isShowing()) {
//						if(lastModelChildInfoHolder[0] == bioModelChildSummary) {
//							return;
//						}
//					}
//					lastModelChildInfoHolder[0] = bioModelChildSummary;
//					if(jpop == null) {
//						jpop = new JPopupMenu();
//						jpop.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
//					}else {
//						jpop.removeAll();
//					}
					for(int i=0;i<bioModelChildSummary.getSimulationContextNames().length;i++) {
						if(bioModelChildSummary.getGeometryDimensions()[i]>0) {
							JMenuItem menuItem = new JMenuItem("App("+bioModelChildSummary.getGeometryDimensions()[i]+"):"+bioModelChildSummary.getSimulationContextNames()[i]+"");
							menuItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									new Thread(new Runnable() {
										@Override
										public void run() {
											String substring = menuItem.getText().substring(7);
											substring = new String(substring.getBytes(java.nio.charset.Charset.forName("ISO-8859-1")));
//											System.out.println(substring+" "+bioModelInfo.getVersion().getName()+" "+menuItem.getText());
											try {
												//BioModel bioModel = (BioModel) getModelInfoWithProgress(bioModelInfo);
												//SimulationContext simulationContext = bioModel.getSimulationContext(substring);
												showGeomForSelection(bioModelInfo,substring);
//												ModelGeometryOPResults modelGeometryOPResults = (ModelGeometryOPResults) DatabaseWindowManager.this.getRequestManager().getDocumentManager()
//														.getSessionManager().getUserMetaDbServer().doTestSuiteOP(
//																new ModelGeometryOP(bioModelInfo, substring));
//												System.out.println(modelGeometryOPResults.getGeometryKey()+" "+modelGeometryOPResults.getTestSuiteKey());
//												GeometryInfo geoInfo = DatabaseWindowManager.this.getRequestManager().getDocumentManager().getGeometryInfo(modelGeometryOPResults.getGeometryKey());
//												Geometry geometry = DatabaseWindowManager.this.getRequestManager().getDocumentManager().getGeometry(geoInfo);
//												VCImage currentValue = geometry.getGeometrySpec().getSampledImage().getCurrentValue();
//												System.out.println(currentValue);
////												VCImageInfo[] imageInfos = DatabaseWindowManager.this.getRequestManager().getDocumentManager().getImageInfos();
////												for(int j=0;j<imageInfos.length;j++) {
////													if(imageInfos[j].getVersion().getVersionKey().equals(geoInfo.getImageRef())) {
////														System.out.println(imageInfos[j].getVersion());
////														//System.out.println(imageInfos[j].getBrowseGif().getSize());
////														ImageIcon ii = new ImageIcon(imageInfos[j].getBrowseGif().getGifEncodedData());
////														DialogUtils.showComponentCloseDialog(vcDocDBTreePanel.getJTree1(), new JLabel(ii), "Geometry Image");
////														break;
////													}
////												}
											} catch (Exception e1) {
												e1.printStackTrace();
											}											
										}
									}).start();
								}});
							jpop.add(menuItem);
						}
					}
					jpop.show(myLocalVCDocDBTreePanel.getJTree1(),e.getX(),e.getY());
				}
			}else {
				if(jpop != null && jpop.isShowing()) {
					jpop.setVisible(false);
				}
			}
		}else {
			if(jpop != null && jpop.isShowing()) {
				jpop.setVisible(false);
			}
		}
	}
	
};

//private static void showGeomForSelection(VCDocumentDbTreePanel myLocalVCDocDBTreePanel,SimulationOwner simulationOwner,SelectGeomHover selectGeomHover)
//		throws GeometryException, ImageException, ExpressionException,
//		PropertyVetoException, GifParsingException {
//	try {
//		VCImageUncompressed currentValue =
//			(VCImageUncompressed) simulationOwner.getMathDescription().getGeometry().getGeometrySpec().createSampledImage(
//					simulationOwner.getMathDescription().getGeometry().getGeometrySpec().getDefaultSampledImageSize());
//		//scale For viewing
//		if (currentValue.getNumPixelClasses() > 1) {
//			VCPixelClass[] newPC = new VCPixelClass[currentValue.getNumPixelClasses()];
//			for (int i = 0; i < newPC.length; i++) {
//				//						       (outMax-outMin)(inVal - inMin)
//				//						       ------------------------------ + outMin
//				//						                 inMax - inMin
//				newPC[i] = new VCPixelClass(
//						currentValue.getPixelClasses(i).getKey(),
//						currentValue.getPixelClasses(i).getPixelClassName(),
//						((127 - 0) * (currentValue.getPixelClasses(i).getPixel() - 0)) / (newPC.length - 1) + 0);
//				for(int j=0;j<currentValue.getPixels().length;j++) {
//					if((int)(currentValue.getPixels()[j]&0x000000FF) == currentValue.getPixelClasses(i).getPixel()) {
//						currentValue.getPixels()[j] = (byte)(newPC[i].getPixel()&0x000000FF);
//					}
//				}
//			}
//			currentValue.setPixelClasses(newPC);
//		}
//		GIFImage makeBrowseGIFImage = BrowseImage.makeBrowseGIFImage2(currentValue);
//		ImageIcon ii = new ImageIcon(makeBrowseGIFImage.getGifEncodedData());
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				JScrollPane jsp = new JScrollPane(new JLabel(ii));
//				jsp.setPreferredSize(new Dimension(300,400));
//				String SEL = "Select";
//				String select = DialogUtils.showOptionsDialog(
//					myLocalVCDocDBTreePanel.getJTree1(), jsp, JOptionPane.QUESTION_MESSAGE, new String[] {"Back",SEL}, "Back", null,
//					"View Geometry (x="+currentValue.getNumX()+" y="+currentValue.getNumY()+" z="+currentValue.getNumZ()+")");
//				if(select != null &&  select.equals(SEL)) {
//					//Save geom selection, Click 'Open' button
//					selectGeomHover.setSelection(simulationOwner.getMathDescription().getGeometry());
//					Container myWindow = BeanUtils.findTypeParentOfComponent(myLocalVCDocDBTreePanel, Window.class);
//					ArrayList<Component> comps = new ArrayList<Component>();
//					BeanUtils.findComponent(myWindow, JButton.class, comps);
//					for(int i=0;i<comps.size();i++) {
//						if(((JButton)comps.get(i)).getText().equals("Open")) {
//							((JButton)comps.get(i)).setEnabled(true);
//							((JButton)comps.get(i)).doClick();
//							return;
//						}
//					}
//				}else {
//					//keep trying to select
//					return;
//				}
//			}});
//	} finally {
//
//	}
//}
public Object selectDocument2(VCDocumentType documentType, TopLevelWindowManager requester) throws Exception {
	VCDocumentDbTreePanel myDBTreePanel = null;
	DocumentManager documentManager = null;
	String title = null;
	switch (documentType) {
		case BIOMODEL_DOC:
			documentManager = getBioModelDbTreePanel().getDocumentManager();
			myDBTreePanel = new BioModelDbTreePanel();
			title = "Select BioModel:Geometry";
			break;
		case MATHMODEL_DOC:
			documentManager = getMathModelDbTreePanel().getDocumentManager();
			myDBTreePanel = new MathModelDbTreePanel();
			title = "Select MathModel:Geometry";
			break;
		default:
			throw new RuntimeException("ERROR: Unknown document type: " + documentType);
	}
//	switch (documentType) {
//	case BIOMODEL_DOC: //{
//		//JCheckBox chkboxHasSpatial = getBioModelDbTreePanel().getDatabaseSearchPanel().getChckbxHasSpatial();
//		//boolean isSelected = chkboxHasSpatial.isSelected();
////		try {
//			documentManager = getBioModelDbTreePanel().getDocumentManager();
//			myDBTreePanel = new BioModelDbTreePanel();
////			myDBTreePanel.setDocumentManager(documentManager);
////			ToolTipManager.sharedInstance().unregisterComponent(myDBTreePanel.getJTree1());
////			selectGeomHover = new SelectGeomHover(myDBTreePanel);
////			myDBTreePanel.getJTree1().addMouseMotionListener(selectGeomHover);
////			//chkboxHasSpatial.setSelected(true);
////			VersionInfo vInfo = DialogUtils.getDBTreePanelSelection(requester.getComponent(),myDBTreePanel,"Open","Select BioModel:Geometry");
////			selection = (selectGeomHover.getSelection() != null?selectGeomHover.getSelection():vInfo);
////		}finally {
////			if(myDBTreePanel!= null){
////				myDBTreePanel.getJTree1().removeMouseMotionListener(selectGeomHover);
////				ToolTipManager.sharedInstance().registerComponent(myDBTreePanel.getJTree1());
////				//chkboxHasSpatial.setSelected(isSelected);				
////			}
////		}
//	//}
//	break;
//	case MATHMODEL_DOC: //{
////		try {
//			documentManager = getMathModelDbTreePanel().getDocumentManager();
//			myDBTreePanel = new MathModelDbTreePanel();
////			myDBTreePanel.setDocumentManager(documentManager);
////			ToolTipManager.sharedInstance().unregisterComponent(myDBTreePanel.getJTree1());
////			selectGeomHover = new SelectGeomHover(myDBTreePanel);
////			myDBTreePanel.getJTree1().addMouseMotionListener(selectGeomHover);
////			//chkboxHasSpatial.setSelected(true);
////			VersionInfo vInfo = DialogUtils.getDBTreePanelSelection(requester.getComponent(),myDBTreePanel,"Open","Select BioModel:Geometry");
////			return (selectGeomHover.getSelection() != null?selectGeomHover.getSelection():vInfo);
////		}finally {
////			if(myDBTreePanel!= null){
////				myDBTreePanel.getJTree1().removeMouseMotionListener(selectGeomHover);
////				ToolTipManager.sharedInstance().registerComponent(myDBTreePanel.getJTree1());
////				//chkboxHasSpatial.setSelected(isSelected);				
////			}
////		}
//	//}
//	break;
//	default: //{
//		throw new RuntimeException("ERROR: Unknown document type: " + documentType);
//	//}

	SelectGeomHover selectGeomHover = null;
	try {
		myDBTreePanel.setDocumentManager(documentManager);
		myDBTreePanel.setSpatialGeomMode(true);
		ToolTipManager.sharedInstance().unregisterComponent(myDBTreePanel.getJTree1());
		selectGeomHover = new SelectGeomHover(myDBTreePanel);
		myDBTreePanel.getJTree1().addMouseMotionListener(selectGeomHover);
		VersionInfo vInfo = DialogUtils.getDBTreePanelSelection(requester.getComponent(),myDBTreePanel,"Open",title);
		return (selectGeomHover.getSelection() != null?selectGeomHover.getSelection():vInfo);
	}finally {
		if(myDBTreePanel!= null && selectGeomHover != null){
			myDBTreePanel.getJTree1().removeMouseMotionListener(selectGeomHover);
			ToolTipManager.sharedInstance().registerComponent(myDBTreePanel.getJTree1());
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:35:55 PM)
 */
public VCDocumentInfo selectDocument(VCDocumentType documentType, TopLevelWindowManager requester) throws Exception {

	// Set doubleClickValue to null.
	// if doubleClickValue is not null when dialog returns, use doubleClickValue value
	// otherwise use dialog return value
	switch (documentType) {
		case BIOMODEL_DOC: {
			return (BioModelInfo)DialogUtils.getDBTreePanelSelection(requester.getComponent(), getBioModelDbTreePanel(),"Open","Select BioModel");
		} 
		case MATHMODEL_DOC: {
			return (MathModelInfo)DialogUtils.getDBTreePanelSelection(requester.getComponent(), getMathModelDbTreePanel(),"Open","Select MathModel");
		} 
		case GEOMETRY_DOC: {
			return (GeometryInfo)DialogUtils.getDBTreePanelSelection(requester.getComponent(), getGeometryTreePanel(),"Open","Select Geometry");
		}
		case EXTERNALFILE_DOC: {
			// Get XML FIle, read the chars into a stringBuffer and create new XMLInfo.
			File modelFile = showFileChooserDialog(requester, FileFilters.FILE_FILTER_EXTERNALDOC);
			return new ExternalDocInfo(modelFile, true);
		}		
		default: {
			throw new RuntimeException("ERROR: Unknown document type: " + documentType);
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 2:28:00 PM)
 * @param newDatabaseWindowPanel cbit.vcell.client.desktop.DatabaseWindowPanel
 */
private void setDatabaseWindowPanel(DatabaseWindowPanel newDatabaseWindowPanel) {
	databaseWindowPanel = newDatabaseWindowPanel;
}


/**
 * Comment
 */
public void setLatestOnly(boolean latestOnly) {
	getDatabaseWindowPanel().setLatestOnly(latestOnly);
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private Object showAccessPermissionDialog(final JComponent aclEditor,final Component requester) {
	JOptionPane accessPermissionDialog = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"OK", "Cancel"});
	aclEditor.setPreferredSize(new java.awt.Dimension(300, 350));
	accessPermissionDialog.setMessage("");
	accessPermissionDialog.setMessage(aclEditor);
	accessPermissionDialog.setValue(null);
	JDialog d = accessPermissionDialog.createDialog(requester, "Changing Permissions");
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	DialogUtils.showModalJDialogOnTop(d,requester);
	return accessPermissionDialog.getValue();
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private File showFileChooserDialog(TopLevelWindowManager requester, FileFilter fileFilter) throws Exception {

	return showFileChooserDialog(requester.getComponent(), fileFilter,getUserPreferences(),JFileChooser.FILES_ONLY);
}


///**
// * Insert the method's description here.
// * Creation date: (5/14/2004 6:11:35 PM)
// */
//public static File showFileChooserDialog(TopLevelWindowManager requester, final FileFilter fileFilter,
//		final UserPreferences currentUserPreferences,int fileSelectMode) throws Exception{
//
//	return showFileChooserDialog0(requester.getComponent(),fileFilter,currentUserPreferences,fileSelectMode);
//}
public static File showFileChooserDialog(Component requesterComp, final FileFilter fileFilter,
		final UserPreferences currentUserPreferences,int fileSelectMode) throws Exception{
	// the boolean isXMLNotImage is true if we are trying to choose an XML file
	// It is false if we are trying to choose an image file
	// This is used to set the appropriate File filters.

	File defaultPath = (File) (currentUserPreferences != null?currentUserPreferences.getCurrentDialogPath():new File("."));
	VCFileChooser fileChooser = new VCFileChooser(defaultPath);
	fileChooser.setFileSelectionMode(fileSelectMode);

	// setting fileFilter for xml files
	fileChooser.setFileFilter(fileFilter);
	
    int returnval = fileChooser.showOpenDialog(requesterComp);
    if (returnval == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        //reset the user preference for the default path, if needed.
        File newPath = selectedFile.getParentFile();
        if (newPath != null && !newPath.equals(defaultPath)) {
			if(currentUserPreferences != null){
				currentUserPreferences.setCurrentDialogPath(newPath);
			}
        }
        //System.out.println("New preferred file path: " + newPath + ", Old preferred file path: " + defaultPath);
        return selectedFile;
    }else{ // user didn't select a file
	    throw UserCancelException.CANCEL_FILE_SELECTION;
    }
}

/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
public String showSaveDialog(final VCDocumentType documentType, final Component requester, final String oldName) throws Exception {
	JOptionPane saveDialog = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"Save", "Cancel"});
	saveDialog.setWantsInput(true);
	saveDialog.setInitialSelectionValue(oldName);
	JPanel panel = new JPanel(new BorderLayout());
	JComponent tree = null;
	switch (documentType) {
		case BIOMODEL_DOC: {
			tree = getBioModelDbTreePanel();
			break;
		}
		case MATHMODEL_DOC: {
			tree = getMathModelDbTreePanel();
			break;
		}
		case GEOMETRY_DOC: {
			tree = getGeometryTreePanel();
			break;
		}
		default: {
			throw new RuntimeException("DatabaseWindowManager.showSaveDialog() - unknown document type");
		}
	}
	tree.setPreferredSize(new java.awt.Dimension(405, 600));
	panel.add(tree, BorderLayout.CENTER);
	panel.add(new JLabel("Please type a new name:"), BorderLayout.SOUTH);
	saveDialog.setMessage("");
	saveDialog.setMessage(panel);
	JDialog d = saveDialog.createDialog(requester, "Save document:");
	d.setResizable(true);
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	final JOptionPane finalSaveDialog = saveDialog;
	ActionListener al = new ActionListener() {
		public void actionPerformed(ActionEvent ae){
			finalSaveDialog.selectInitialValue();
		 }
	};
	final Timer getFocus = new Timer(100, al);
	getFocus.setRepeats(false);
	getFocus.start();	
	DialogUtils.showModalJDialogOnTop(d,requester);
	if ("Save".equals(saveDialog.getValue())) {
		return saveDialog.getInputValue() == null ? null : saveDialog.getInputValue().toString();
	} else {
		// user cancelled
		throw UserCancelException.CANCEL_NEW_NAME;
	}
}

public void reconnect() {
	getRequestManager().reconnect(this);
}
}
