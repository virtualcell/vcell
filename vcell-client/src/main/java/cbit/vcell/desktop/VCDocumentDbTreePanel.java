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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.vcell.util.DataAccessException;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DialogUtils.SelectableTreeVersionJPanel;

import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.desktop.DatabaseSearchPanel;
import cbit.vcell.client.desktop.DatabaseSearchPanel.SearchCriterion;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.resource.ErrorUtils;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 11:34:01 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public abstract class VCDocumentDbTreePanel extends DocumentEditorSubPanel implements SelectableTreeVersionJPanel{
	protected JTree ivjJTree1 = null;
	protected DocumentManager ivjDocumentManager = null;
	protected VersionInfo fieldSelectedVersionInfo = null;
	protected transient ActionListener aActionListener = null;
	protected boolean fieldPopupMenuDisabled = false;
	protected JPanel topPanel = null;
	protected JPanel bottomPanel = null;
	protected DatabaseSearchPanel dbSearchPanel = null;	
	protected boolean bShowMetadata = true;
	private VCDocumentDbTreeModel treeModel = null;
	protected VCDocumentDbCellRenderer treeCellRenderer = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean initializeNotCalled = true;
	private boolean exceptionHasOccured = true; 
	
	private class IvjEventHandler implements DatabaseListener, ActionListener, MouseListener, TreeModelListener, TreeSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getDatabaseSearchPanel()) {
				search(e.getActionCommand().equals(DatabaseSearchPanel.SEARCH_SHOW_ALL_COMMAND));
			}
		}
		public void databaseDelete(DatabaseEvent event) {
			if (event.getSource() == getDocumentManager()) 
				documentManager_DatabaseDelete(event);
		}
		public void databaseInsert(DatabaseEvent event) {};
		public void databaseRefresh(DatabaseEvent event) {
			if (event.getSource() == getDocumentManager())
				refresh();
		}
		public void databaseUpdate(DatabaseEvent event) {
			if (event.getSource() == getDocumentManager()) {
				documentManager_DatabaseUpdate(event);
			}
		}
		public void treeNodesChanged(javax.swing.event.TreeModelEvent e) {
			if (e.getSource() == getTreeModel( )) { 
				treeSelection();
			}
		};
		public void treeNodesInserted(javax.swing.event.TreeModelEvent e) {};
		public void treeNodesRemoved(javax.swing.event.TreeModelEvent e) {};
		public void treeStructureChanged(javax.swing.event.TreeModelEvent e){};
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == getJTree1().getSelectionModel()) { 
				treeSelection();
			}
		}
		public void mouseClicked(java.awt.event.MouseEvent e) {
			//System.out.println("---------- click - isPopupTrigger="+e.isPopupTrigger()+" "+e.getClickCount());
			//this event is not generated on some MacOS, use mouse_pressed with 2 clicks instead
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			//System.out.println("---------- press - isPopupTrigger="+e.isPopupTrigger()+" "+e.getClickCount());
			if (e.getSource() == getJTree1()){
				actionsOnClick(e);
			}
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			//System.out.println("---------- release - isPopupTrigger="+e.isPopupTrigger()+" "+e.getClickCount());
			if (e.getSource() == getJTree1()){
				actionsOnClick(e);
			}
		};
	}
/**
 * BioModelTreePanel constructor comment.
 */
public VCDocumentDbTreePanel() {
	this(true);
}

public VCDocumentDbTreePanel(boolean bMetadata) {
	super();
	this.bShowMetadata = bMetadata;
}
protected void ifNeedsDoubleClickEvent(MouseEvent mouseEvent,Class<? extends VersionInfo> versionInfoClass){
	//do this because MouseEvent.MOUSE_CLICKED not genereated on some MacOS
	if(mouseEvent.getID() == MouseEvent.MOUSE_PRESSED &&
		mouseEvent.getButton() == MouseEvent.BUTTON1 &&
		mouseEvent.getClickCount() == 2 &&
		versionInfoClass.isInstance(getSelectedVersionInfo())){
		fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, DatabaseWindowManager.BM_MM_GM_DOUBLE_CLICK_ACTION));
	}
}
private VCDocumentDbTreeModel getTreeModel( )  {
	if (treeModel != null) {
		return treeModel;
	}
	if (initializeNotCalled) {
		throw new NullPointerException("initialize( ) not called on " + getClass().getName());
	}
	if (exceptionHasOccured) {
		throw new NullPointerException("exception occured on  " + getClass().getName());
	}
	throw new NullPointerException("Unknown NullPointerException on  " + getClass().getName());
}

protected abstract void actionsOnClick(MouseEvent mouseEvent);

public void onSelectedObjectsChange(Object[] selectedObjects) {
	getTreeModel( ).onSelectedObjectsChange(selectedObjects);	
}

protected abstract void documentManager_DatabaseUpdate(DatabaseEvent event);
protected abstract void documentManager_DatabaseDelete(DatabaseEvent event);

public void addActionListener(ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.add(aActionListener, newListener);
	return;
}

/**
 * Return the JTree1 property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTree getJTree1() {
	if (ivjJTree1 == null) {
		try {
			javax.swing.tree.DefaultTreeSelectionModel ivjLocalSelectionModel;
			ivjLocalSelectionModel = new javax.swing.tree.DefaultTreeSelectionModel();
			ivjLocalSelectionModel.setRowMapper(getLocalSelectionModelVariableHeightLayoutCache());
			ivjLocalSelectionModel.setSelectionMode(1);
			ivjJTree1 = new javax.swing.JTree();
			ivjJTree1.setName("JTree1");
			ivjJTree1.setSelectionModel(ivjLocalSelectionModel);
			ToolTipManager.sharedInstance().registerComponent(ivjJTree1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTree1;
}

/**
 * Return the LocalSelectionModelVariableHeightLayoutCache property value.
 * @return javax.swing.tree.VariableHeightLayoutCache
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.tree.VariableHeightLayoutCache getLocalSelectionModelVariableHeightLayoutCache() {
	javax.swing.tree.VariableHeightLayoutCache ivjLocalSelectionModelVariableHeightLayoutCache = null;
	try {
		/* Create part */
		ivjLocalSelectionModelVariableHeightLayoutCache = new javax.swing.tree.VariableHeightLayoutCache();
		ivjLocalSelectionModelVariableHeightLayoutCache.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("loading",false)));
		ivjLocalSelectionModelVariableHeightLayoutCache.setRootVisible(true);
		ivjLocalSelectionModelVariableHeightLayoutCache.setSelectionModel(new javax.swing.tree.DefaultTreeSelectionModel());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjLocalSelectionModelVariableHeightLayoutCache;
}

protected boolean shouldDisablePopupMenu(TreePath treePath) {
	if(getSelectedVersionInfo() == null || getSelectedVersionInfo().getVersion() == null) {
		return false;
	}
	Version version = getSelectedVersionInfo().getVersion();
	boolean isOwner = version.getOwner().compareEqual(getDocumentManager().getUser());
	if(!isOwner) {
		// we don't care if the login user is not the owner
		return false;
	}
	TreePath p1 = treePath.getParentPath();
	if(p1 != null && p1.getLastPathComponent() instanceof BioModelNode) {
		// return true if it's a model in the ModelBricks folder
		BioModelNode n1 = (BioModelNode)p1.getLastPathComponent();
		if(n1.getUserObject() instanceof String) {
			String str = (String)n1.getUserObject();
			if(str.equals(VCDocumentDbTreeModel.ModelBricks)) {
				return true;
			}
		}
	}
	
	TreePath p2 = null;
	if(p1 != null && p1.getParentPath() != null) {
		p2 = p1.getParentPath();
	}
	if(p2 != null && p2.getLastPathComponent() instanceof BioModelNode) {
		BioModelNode n2 = (BioModelNode)p2.getLastPathComponent();
		if(n2.getUserObject() instanceof String) {
			// return true if it's a version of a model in the ModelBricks folder
			String str = (String)n2.getUserObject();
			if(str.equals(VCDocumentDbTreeModel.Other_MathModels) || 
					str.equals(VCDocumentDbTreeModel.Other_BioModels) ||
					str.equals(VCDocumentDbTreeModel.PUBLIC_GEOMETRIES) ||
					str.equals(VCDocumentDbTreeModel.ModelBricks)) {
				return true;
			}
		}
	}
	return false;
}

/**
 * Gets the popupMenuDisabled property (boolean) value.
 * @return The popupMenuDisabled property value.
 * @see #setPopupMenuDisabled
 */
protected boolean getPopupMenuDisabled() {
	return fieldPopupMenuDisabled;
}

/**
 * Gets the selectedVersionInfo property (cbit.sql.VersionInfo) value.
 * @return The selectedVersionInfo property value.
 * @see #setSelectedVersionInfo
 */
public VersionInfo getSelectedVersionInfo() {
	return fieldSelectedVersionInfo;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
protected void handleException(java.lang.Throwable exception) {
	exceptionHasOccured = true;

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
	ErrorUtils.sendErrorReport(exception);
}

protected abstract JPanel getBottomPanel();

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
protected void initialize() {
	try {
		initializeNotCalled = false;
		// user code begin {1}
		// user code end
		setName("BioModelTreePanel");
		setLayout(new BorderLayout());
		
		getDatabaseSearchPanel().addActionListener(ivjEventHandler);
		getDatabaseSearchPanel().addCollapsiblePropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getSource() == getDatabaseSearchPanel() && evt.getPropertyName().equals(CollapsiblePanel.SEARCHPPANEL_EXPANDED)){
					boolean bSearchPanelExpanded = (Boolean)evt.getNewValue();
					if(!bSearchPanelExpanded){
						search(true);
					}
				}
			}
		});
		treeCellRenderer = createTreeCellRenderer();
		treeModel = createTreeModel();
		getJTree1().setModel(getTreeModel( ));
		getJTree1().setCellRenderer(treeCellRenderer);
		getTreeModel( ).addTreeModelListener(ivjEventHandler);
		getJTree1().getSelectionModel().addTreeSelectionListener(ivjEventHandler);		
		getJTree1().addMouseListener(ivjEventHandler);
		
		add(getDatabaseSearchPanel(), BorderLayout.NORTH);
		JComponent topPanel = new JScrollPane(getJTree1());
		JComponent centerPanel = topPanel;
		if (bShowMetadata) {
			JSplitPane splitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			splitPane.setDividerLocation(150);
			splitPane.setResizeWeight(0.7);
			getBottomPanel().setMinimumSize(new Dimension(150,150));
			topPanel.setMinimumSize(new Dimension(150,150));
			splitPane.setTopComponent(topPanel);
			splitPane.setBottomComponent(getBottomPanel());
			centerPanel = splitPane;
		}
		add(centerPanel, BorderLayout.CENTER);		

	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

protected abstract VCDocumentDbTreeModel createTreeModel();
protected abstract VCDocumentDbCellRenderer createTreeCellRenderer();
/**
 * Method to support listener events.
 */
protected void fireActionPerformed(ActionEvent e) {
	if (aActionListener == null) {
		return;
	};
	aActionListener.actionPerformed(e);
}
/**
 * Method to support listener events.
 */
protected void refireActionPerformed(ActionEvent e) {
	fireActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
}

private void refresh() {	
	treeCellRenderer.setSessionUser(getDocumentManager() == null ? null : getDocumentManager().getUser());	
	getTreeModel( ).refreshTree();
}
/**
 * 
 * @exception org.vcell.util.DataAccessException The exception description.
 */
public void refresh(ArrayList<SearchCriterion> newFilterList) throws DataAccessException {
	getTreeModel( ).refreshTree(newFilterList);
}


public void removeActionListener(ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.remove(aActionListener, newListener);
	return;
}

/**
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
public DocumentManager getDocumentManager() {
	// user code begin {1}
	// user code end
	return ivjDocumentManager;
}

/**
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(DocumentManager newValue) {
	if (ivjDocumentManager == newValue) {
		return;
	}
	DocumentManager oldValue = getDocumentManager();
	if (oldValue != null) {
		oldValue.removeDatabaseListener(ivjEventHandler);
	}
	ivjDocumentManager = newValue;
	if (ivjDocumentManager != null) {
		ivjDocumentManager.addDatabaseListener(ivjEventHandler);				
	}
	getTreeModel( ).setDocumentManager(ivjDocumentManager);
	treeCellRenderer.setSessionUser(ivjDocumentManager == null ? null : ivjDocumentManager.getUser());
	firePropertyChange(CommonTask.DOCUMENT_MANAGER.name, oldValue, newValue);
}

/**
 * Method generated to support the promotion of the latestVersionOnly attribute.
 * @param arg1 boolean
 */
public void setLatestVersionOnly(boolean arg1) {
	getTreeModel( ).setLatestOnly(arg1);
}

/**
 * Sets the popupMenuDisabled property (boolean) value.
 * @param popupMenuDisabled The new value for the property.
 * @see #getPopupMenuDisabled
 */
public void setPopupMenuDisabled(boolean popupMenuDisabled) {
	boolean oldValue = fieldPopupMenuDisabled;
	fieldPopupMenuDisabled = popupMenuDisabled;
	firePropertyChange("popupMenuDisabled", new Boolean(oldValue), new Boolean(popupMenuDisabled));
}


/**
 * Sets the selectedVersionInfo property (cbit.sql.VersionInfo) value.
 * @param selectedVersionInfo The new value for the property.
 * @see #getSelectedVersionInfo
 */
protected void setSelectedVersionInfo(VersionInfo selectedVersionInfo) {
	if (fieldSelectedVersionInfo == selectedVersionInfo) {
		return;
	}
	fieldSelectedVersionInfo = selectedVersionInfo;
	// force it to fire, since BioModelInfo.equals only compare key
	// but some else might have changes, e.g. GroupAccess
	firePropertyChange("selectedVersionInfo", null, selectedVersionInfo);
}
protected void setSelectedPublicationInfo(BioModelNode selectedModelNode) {
	firePropertyChange("selectedPublicationInfo", null, selectedModelNode);
}

/**
 * Comment
 */
protected abstract void treeSelection();

private DatabaseSearchPanel getDatabaseSearchPanel() {
	if (dbSearchPanel == null) {
		dbSearchPanel = new DatabaseSearchPanel();
		if(this instanceof BioModelDbTreePanel){
			dbSearchPanel.enableSpeciesSearch();
		}
	}
	return dbSearchPanel;
}



public void search(final boolean bShowAll) {
	final Object[] status = new Object[]{null};
	class SearchCancel implements ProgressDialogListener{
		public void cancelButton_actionPerformed(EventObject newEvent) {
			status[0] = UserCancelException.CANCEL_GENERIC;
		}
	}
	final SearchCancel searchCancel = new SearchCancel();
	
	final ArrayList<SearchCriterion> searchCriterionListFinal = new ArrayList<DatabaseSearchPanel.SearchCriterion>();
	AsynchClientTask searchCriteriaTask = new AsynchClientTask("Creating search criteria...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING,true) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if(bShowAll){
				return;
			}
			new Thread(new Runnable() {
				public void run() {
					try{
						ArrayList<SearchCriterion> searchCriterionList = getDatabaseSearchPanel().getSearchCriterionList(getDocumentManager());
						if(status[0] == null){//update status only if no one else has
							if(searchCriterionList != null && searchCriterionList.size() > 0){
								searchCriterionListFinal.addAll(searchCriterionList);
							}
							status[0] = "OK";
							return;
						}else{
							return;
						}
					}catch(Exception e){
						status[0] = e;
					}
				}
			}).start();
			
			long startTime = System.currentTimeMillis();
			while((System.currentTimeMillis()-startTime) < 60000){
				if(status[0] != null){
					if(status[0] instanceof Exception){
						throw (Exception)status[0];//User cancelled or there was an exception in search criteria thread
					}
					return;//search criteria thread completed with 'OK'
				}
				try{
					Thread.sleep(100);
				}catch(InterruptedException ie){
					//ignore
				}
			}
			throw new Exception("getSearchCriterionList timed out");
		}
	};
	AsynchClientTask refreshTask = new AsynchClientTask("Refreshing tree...",AsynchClientTask.TASKTYPE_SWING_BLOCKING,false) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if(searchCriterionListFinal.size() > 0){
				refresh(searchCriterionListFinal);//show with search criteria
			}else{
				refresh(null);//show all
			}
		}
	};

	Hashtable<String, Object> hashTable = new Hashtable<String, Object>();
	if(!bShowAll && getDatabaseSearchPanel().hasRemoteDatabaseSearchDefined()){
		ClientTaskDispatcher.dispatch(this,hashTable, new AsynchClientTask[] {searchCriteriaTask,refreshTask},true, false, true, searchCancel, true);
	}else{
		try {
			searchCriteriaTask.run(hashTable);
			refreshTask.run(hashTable);
		} catch (Exception e) {
			e.printStackTrace();
			DialogUtils.showErrorDialog(this, e.getMessage());
		}
	}
}

public void expandSearchPanel(boolean bExpand) {
	getDatabaseSearchPanel().expand(bExpand);
}

public void updateConnectionStatus(ConnectionStatus connStatus) {
	getTreeModel( ).updateConnectionStatus(connStatus);
}

public void addJTreeSelectionMouseListener(MouseListener jtreeSelectionMouseListener){
	getJTree1().addMouseListener(jtreeSelectionMouseListener);
}
public void removeJTreeSelectionMouseListener(MouseListener jtreeSelectionMouseListener){
	getJTree1().removeMouseListener(jtreeSelectionMouseListener);
}

}
