package cbit.vcell.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

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
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.desktop.DatabaseSearchPanel;
import cbit.vcell.client.desktop.DatabaseSearchPanel.SearchCriterion;
import cbit.vcell.client.desktop.biomodel.BioModelsNetModelInfo;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.geometry.GeometryInfo;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 11:34:01 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public abstract class VCDocumentDbTreePanel extends DocumentEditorSubPanel {
	protected JTree ivjJTree1 = null;
	protected DocumentManager ivjDocumentManager = null;
	protected VersionInfo fieldSelectedVersionInfo = null;
	protected transient ActionListener aActionListener = null;
	protected boolean fieldPopupMenuDisabled = false;
	protected JPanel topPanel = null;
	protected JPanel bottomPanel = null;
	protected DatabaseSearchPanel dbSearchPanel = null;	
	protected boolean bShowMetadata = true;
	protected VCDocumentDbTreeModel treeModel = null;
	protected VCDocumentDbCellRenderer treeCellRenderer = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	
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
			if (e.getSource() == treeModel) { 
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
			if (e.getSource() == getJTree1()) 
				actionsOnClick(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
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
	initialize();
}

protected abstract void actionsOnClick(MouseEvent mouseEvent);

public void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length == 0 || selectedObjects.length > 1) {
		getJTree1().clearSelection();
	} else {
		if (this instanceof BioModelDbTreePanel && selectedObjects[0] instanceof BioModelInfo
				|| this instanceof MathModelDbTreePanel && selectedObjects[0] instanceof MathModelInfo
				|| this instanceof GeometryTreePanel && selectedObjects[0] instanceof GeometryInfo)  {
			BioModelNode node = ((BioModelNode)getJTree1().getModel().getRoot()).findNodeByUserObject(selectedObjects[0]);
			if (node != null) {
				getJTree1().setSelectionPath(new TreePath(node.getPath()));
			}
		} else if (selectedObjects[0] == null || selectedObjects[0] instanceof VCDocumentInfo || selectedObjects[0] instanceof BioModelsNetModelInfo) {
			getJTree1().clearSelection();
		}
	}
	
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
protected javax.swing.JTree getJTree1() {
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

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}

protected abstract JPanel getBottomPanel();

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("BioModelTreePanel");
		setLayout(new BorderLayout());
		
		getDatabaseSearchPanel().addActionListener(ivjEventHandler);
		treeCellRenderer = createTreeCellRenderer();
		treeModel = createTreeModel();
		getJTree1().setModel(treeModel);
		getJTree1().setCellRenderer(treeCellRenderer);
		treeModel.addTreeModelListener(ivjEventHandler);
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
	treeModel.refreshTree();
	treeCellRenderer.setSessionUser(getDocumentManager() == null ? null : getDocumentManager().getUser());
}
/**
 * 
 * @exception org.vcell.util.DataAccessException The exception description.
 */
public void refresh(ArrayList<SearchCriterion> newFilterList) throws DataAccessException {
	treeModel.refreshTree(newFilterList);
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
	treeModel.setDocumentManager(ivjDocumentManager);
	treeCellRenderer.setSessionUser(ivjDocumentManager == null ? null : ivjDocumentManager.getUser());
	firePropertyChange("documentManager", oldValue, newValue);
}

/**
 * Method generated to support the promotion of the latestVersionOnly attribute.
 * @param arg1 boolean
 */
public void setLatestVersionOnly(boolean arg1) {
	treeModel.setLatestOnly(arg1);
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

/**
 * Comment
 */
protected abstract void treeSelection();

private DatabaseSearchPanel getDatabaseSearchPanel() {
	if (dbSearchPanel == null) {
		dbSearchPanel = new DatabaseSearchPanel();
	}
	return dbSearchPanel;
}

public void search(boolean bShowAll) {
	try {
		ArrayList<SearchCriterion> searchCriterionList = bShowAll ? null : getDatabaseSearchPanel().getSearchCriterionList();
		refresh(searchCriterionList);
	} catch (DataAccessException e) {
		e.printStackTrace();
		DialogUtils.showErrorDialog(this, "Search failed : " + e.getMessage());
	}
}

public void expandSearchPanel(boolean bExpand) {
	getDatabaseSearchPanel().expand(bExpand);
}

public void updateConnectionStatus(ConnectionStatus connStatus) {
	treeModel.updateConnectionStatus(connStatus);
}
}