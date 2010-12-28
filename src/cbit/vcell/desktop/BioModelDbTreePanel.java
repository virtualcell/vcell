package cbit.vcell.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.desktop.DatabaseSearchPanel;
import cbit.vcell.client.desktop.DatabaseSearchPanel.SearchCriterion;
import cbit.vcell.client.desktop.biomodel.BioModelsNetModelInfo;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.VCellBasicCellRenderer.VCDocumentInfoNode;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 11:34:01 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class BioModelDbTreePanel extends DocumentEditorSubPanel {
	private JTree ivjJTree1 = null;
	private boolean ivjConnPtoP2Aligning = false;
	private DocumentManager ivjDocumentManager = null;
	private boolean ivjConnPtoP3Aligning = false;
	private JScrollPane ivjJScrollPane1 = null;
	private boolean ivjConnPtoP4Aligning = false;
	private TreeSelectionModel ivjselectionModel1 = null;
	private VersionInfo fieldSelectedVersionInfo = null;
	private boolean ivjConnPtoP5Aligning = false;
	private VersionInfo ivjselectedVersionInfo1 = null;
	private JMenuItem ivjJMenuItemDelete = null;
	private JMenuItem ivjJMenuItemOpen = null;
	protected transient ActionListener aActionListener = null;
	private JLabel ivjJLabel1 = null;
	private JSeparator ivjJSeparator1 = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private BioModelDbTreeModel ivjBioModelDbTreeModel = null;
	private JPopupMenu ivjBioModelPopupMenu = null;
	private BioModelMetaDataPanel ivjBioModelMetaDataPanel = null;
	private JPanel bottomPanel = null;
	private JMenuItem ivjAnotherEditionMenuItem = null;
	private JMenuItem ivjAnotherModelMenuItem = null;
	private JMenu ivjJMenu1 = null;
	private JMenuItem ivjLatestEditionMenuItem = null;
	private JMenuItem ivjJMenuItemPermission = null;
	private JMenuItem ivjJMenuItemExport = null;
	private JSeparator ivjJSeparator3 = null;
	private JMenuItem ivjJPreviousEditionMenuItem = null;
	private boolean fieldPopupMenuDisabled = false;
	private JPanel topPanel = null;
	private JMenuItem ivjJMenuItemArchive = null;
	private JMenuItem ivjJMenuItemPublish = null;
	private DatabaseSearchPanel dbSearchPanel = null;

class IvjEventHandler implements DatabaseListener, java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.TreeModelListener, javax.swing.event.TreeSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == BioModelDbTreePanel.this.getJMenuItemDelete()) 
				connEtoC7(e);
			if (e.getSource() == BioModelDbTreePanel.this.getJMenuItemOpen()) 
				connEtoC6(e);
			if (e.getSource() == BioModelDbTreePanel.this.getJMenuItemPermission()) 
				connEtoC10(e);
			if (e.getSource() == BioModelDbTreePanel.this.getLatestEditionMenuItem()) 
				connEtoC15(e);
			if (e.getSource() == BioModelDbTreePanel.this.getAnotherEditionMenuItem()) 
				connEtoC16(e);
			if (e.getSource() == BioModelDbTreePanel.this.getAnotherModelMenuItem()) 
				connEtoC17(e);
			if (e.getSource() == BioModelDbTreePanel.this.getJMenuItemExport()) 
				connEtoC13(e);
			if (e.getSource() == BioModelDbTreePanel.this.getJPreviousEditionMenuItem()) 
				connEtoC18(e);
			if (e.getSource() == BioModelDbTreePanel.this.getJMenuItemArchive()) 
				connEtoC22(e);
			if (e.getSource() == BioModelDbTreePanel.this.getJMenuItemPublish()) 
				connEtoC23(e);
			if (e.getSource() == getDatabaseSearchPanel()) {
				search(e.getActionCommand().equals(DatabaseSearchPanel.SEARCH_SHOW_ALL_COMMAND));
			}
		};
		public void databaseDelete(DatabaseEvent event) {
			if (event.getSource() == BioModelDbTreePanel.this.getDocumentManager()) 
				connEtoC14(event);
		};
		public void databaseInsert(DatabaseEvent event) {};
		public void databaseRefresh(DatabaseEvent event) {
			if (event.getSource() == BioModelDbTreePanel.this.getDocumentManager())
				refresh();
		};
		public void databaseUpdate(DatabaseEvent event) {
			if (event.getSource() == BioModelDbTreePanel.this.getDocumentManager()) 
				connEtoC3(event);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == BioModelDbTreePanel.this.getJTree1()) 
				connEtoC5(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelDbTreePanel.this && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == BioModelDbTreePanel.this.getJTree1() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == BioModelDbTreePanel.this && (evt.getPropertyName().equals("selectedVersionInfo"))) 
				connPtoP5SetTarget();
			if (evt.getSource() == BioModelDbTreePanel.this.getBioModelDbTreeModel() && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP3SetSource();
			if (evt.getSource() == BioModelDbTreePanel.this.getBioModelDbTreeModel() && (evt.getPropertyName().equals("latestOnly"))) 
				connEtoC4(evt);
			if (evt.getSource() == BioModelDbTreePanel.this && (evt.getPropertyName().equals("documentManager"))) 
				connEtoM2(evt);
		};
		public void treeNodesChanged(javax.swing.event.TreeModelEvent e) {
			if (e.getSource() == BioModelDbTreePanel.this.getBioModelDbTreeModel()) 
				connEtoC11(e);
		};
		public void treeNodesInserted(javax.swing.event.TreeModelEvent e) {};
		public void treeNodesRemoved(javax.swing.event.TreeModelEvent e) {};
		public void treeStructureChanged(javax.swing.event.TreeModelEvent e){};
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == BioModelDbTreePanel.this.getselectionModel1()) 
				connEtoC1();
		};
	};

	private boolean bShowMetadata = true;
/**
 * BioModelTreePanel constructor comment.
 */
public BioModelDbTreePanel() {
	this(true);
}

public void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length == 0 || selectedObjects.length > 1) {
		getJTree1().clearSelection();
	} else {
		if (selectedObjects[0] instanceof BioModelInfo) {
			BioModelNode node = ((BioModelNode)getBioModelDbTreeModel().getRoot()).findNodeByUserObject(selectedObjects[0]);
			if (node != null) {
				getJTree1().setSelectionPath(new TreePath(node.getPath()));
			}
		} else if (selectedObjects[0] == null || selectedObjects[0] instanceof VCDocumentInfo || selectedObjects[0] instanceof BioModelsNetModelInfo) {
			getJTree1().clearSelection();
		}
	}
	
}

public BioModelDbTreePanel(boolean bMetadata) {
	super();
	this.bShowMetadata = bMetadata;
	initialize();
}

/**
 * Comment
 */
private void actionsOnClick(MouseEvent mouseEvent) {
	if (mouseEvent.getClickCount() == 2 && getSelectedVersionInfo() instanceof BioModelInfo) {
		fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, DatabaseWindowManager.BM_MM_GM_DOUBLE_CLICK_ACTION));
		return;
	}
	if (SwingUtilities.isRightMouseButton(mouseEvent) && getSelectedVersionInfo() instanceof BioModelInfo && (! getPopupMenuDisabled())) {
		Version version = getSelectedVersionInfo().getVersion();
		boolean isOwner = version.getOwner().compareEqual(getDocumentManager().getUser());
		configureArhivePublishMenuState(version,isOwner);
		getJMenuItemPermission().setEnabled(isOwner && !version.getFlag().compareEqual(VersionFlag.Published));
		getJMenuItemDelete().setEnabled(isOwner &&
			!version.getFlag().compareEqual(VersionFlag.Archived) &&
			!version.getFlag().compareEqual(VersionFlag.Published));
		getBioModelPopupMenu().show(getJTree1(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
	}
}


public void addActionListener(ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.add(aActionListener, newListener);
	return;
}


/**
 * Comment
 */
private void anotherEditionMenuItemEnable(VersionInfo vInfo) throws DataAccessException {

	boolean bAnotherEditionMenuItem = false;
	BioModelInfo thisBioModelInfo = (BioModelInfo)vInfo;

	//
	// Get the other versions of the Biomodel
	//
	BioModelInfo bioModelVersionsList[] = getBioModelVersionDates(thisBioModelInfo);
	
	if (bioModelVersionsList == null || bioModelVersionsList.length == 0) {
		bAnotherEditionMenuItem = false;
	} else {
		bAnotherEditionMenuItem = true;
	}

	getAnotherEditionMenuItem().setEnabled(bAnotherEditionMenuItem);
}


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 8:15:47 AM)
 */
private void configureArhivePublishMenuState(Version version,boolean isOwner) {
	
	getJMenuItemArchive().setEnabled(
		isOwner
		&&
		!version.getFlag().isArchived()
		&&
		!version.getFlag().isPublished());
	
	getJMenuItemPublish().setEnabled(
		isOwner
		&&
		version.getFlag().isArchived()
		&&
		(version.getGroupAccess() instanceof GroupAccessAll)
		&&
		!version.getFlag().isPublished()
		&&
		version.getOwner().isPublisher());
}


/**
 * connEtoC1:  (selectionModel1.treeSelection. --> BioModelTreePanel.TreeSelectionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.treeSelection();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC10:  (JMenuItemUnpublish.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelDbTreePanel.fireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC11:  (BioModelDbTreeModel.treeModel.treeNodesChanged(javax.swing.event.TreeModelEvent) --> BioModelDbTreePanel.TreeSelectionEvents()V)
 * @param arg1 javax.swing.event.TreeModelEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(javax.swing.event.TreeModelEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.treeSelection();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC12:  (BioModelDbTreePanel.initialize() --> BioModelDbTreePanel.enableToolTips(Ljavax.swing.JTree;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12() {
	try {
		// user code begin {1}
		// user code end
		this.enableToolTips(getJTree1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC13:  (JMenuItem1.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelDbTreePanel.comparePreviousEdition()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC14:  (DocumentManager.database.databaseDelete(cbit.vcell.clientdb.DatabaseEvent) --> BioModelDbTreePanel.documentManager_DatabaseDelete(Lcbit.vcell.clientdb.DatabaseEvent;)V)
 * @param arg1 cbit.vcell.clientdb.DatabaseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(DatabaseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.documentManager_DatabaseDelete(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC15:  (LatestEditionMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelDbTreePanel.compareLatestEdition()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC16:  (AnotherEditionMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelDbTreePanel.compareAnotherEdition()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC17:  (AnotherModelMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelDbTreePanel.compareAnotherModel()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC18:  (JMenuItemNote.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelDbTreePanel.editBioModelInfo(Lcbit.vcell.biomodel.BioModel;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC19:  (selectedVersionInfo1.this --> BioModelDbTreePanel.previousEditionMenuItemEnable(Lcbit.sql.VersionInfo;)V)
 * @param value cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(VersionInfo value) {
	try {
		// user code begin {1}
		// user code end
		this.previousEditionMenuItemEnable(getselectedVersionInfo1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC20:  (selectedVersionInfo1.this --> BioModelDbTreePanel.latestEditionMenuItemEnable(Lcbit.sql.VersionInfo;)V)
 * @param value cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(VersionInfo value) {
	try {
		// user code begin {1}
		// user code end
		this.latestEditionMenuItemEnable(getselectedVersionInfo1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC21:  (selectedVersionInfo1.this --> BioModelDbTreePanel.anotherEditionMenuItemEnable(Lcbit.sql.VersionInfo;)V)
 * @param value cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC21(VersionInfo value) {
	try {
		// user code begin {1}
		// user code end
		this.anotherEditionMenuItemEnable(getselectedVersionInfo1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC22:  (JMenuItemArchive.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelDbTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC22(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC23:  (JMenuItemPublish.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelDbTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC23(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (DocumentManager.database.databaseUpdate(cbit.vcell.clientdb.DatabaseEvent) --> BioModelDbTreePanel.documentManager_DatabaseUpdate(Lcbit.vcell.clientdb.DatabaseEvent;)V)
 * @param arg1 cbit.vcell.clientdb.DatabaseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(DatabaseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.documentManager_DatabaseUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (BioModelDbTreeBuilder.latestOnly --> BioModelDbTreePanel.firePropertyChange(Ljava.lang.String;Ljava.lang.Object;Ljava.lang.Object;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.firePropertyChange("latestVersionOnly", arg1.getOldValue(), arg1.getNewValue());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (JTree1.mouse.mouseClicked(java.awt.event.MouseEvent) --> BioModelDbTreePanel.showPopupMenu(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.actionsOnClick(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC6:  (JMenuItemOpen.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelDbTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (JMenuItemDelete.action.actionPerformed(java.awt.event.ActionEvent) --> BioModelDbTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM1:  (selectedVersionInfo1.this --> BioModelMetaDataPanel.bioModelInfo)
 * @param value cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(VersionInfo value) {
	try {
		// user code begin {1}
		// user code end
		getBioModelMetaDataPanel().setBioModelInfo((BioModelInfo)getselectedVersionInfo1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (BioModelDbTreePanel.documentManager --> BioModelMetaDataPanel.documentManager)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getBioModelMetaDataPanel().setDocumentManager(this.getDocumentManager());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (BioModelDbTreePanel.initialize() --> JTree1.putClientProperty(Ljava.lang.Object;Ljava.lang.Object;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4() {
	try {
		// user code begin {1}
		// user code end
		getJTree1().putClientProperty("JTree.lineStyle", "Angled");
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetSource:  (BioModelTreePanel.documentManager <--> DocumentManager.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getDocumentManager() != null)) {
				this.setDocumentManager(getDocumentManager());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (BioModelTreePanel.documentManager <--> DocumentManager.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setDocumentManager(this.getDocumentManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (DocumentManager.this <--> BioModelTreeBuilder.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setDocumentManager(getBioModelDbTreeModel().getDocumentManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP3SetTarget:  (DocumentManager.this <--> BioModelTreeBuilder.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getDocumentManager() != null)) {
				getBioModelDbTreeModel().setDocumentManager(getDocumentManager());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP4SetSource:  (JTree1.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getselectionModel1() != null)) {
				getJTree1().setSelectionModel(getselectionModel1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetTarget:  (JTree1.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			setselectionModel1(getJTree1().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP5SetTarget:  (BioModelTreePanel.selectedVersionInfo <--> selectedVersionInfo1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP5SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP5Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP5Aligning = true;
			setselectedVersionInfo1(this.getSelectedVersionInfo());
			// user code begin {2}
			// user code end
			ivjConnPtoP5Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP5Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private void documentManager_DatabaseDelete(DatabaseEvent event) {
	if (event.getOldVersionInfo() instanceof BioModelInfo && getSelectedVersionInfo() instanceof BioModelInfo) {
		BioModelInfo selectedBMInfo = (BioModelInfo)getSelectedVersionInfo();
		BioModelInfo eventBMInfo = (BioModelInfo)event.getOldVersionInfo();
		if (eventBMInfo.getVersion().getVersionKey().equals(selectedBMInfo.getVersion().getVersionKey())){
			setSelectedVersionInfo(null);
			getJTree1().getSelectionModel().clearSelection();
		}		
	}
}


/**
 * Comment
 */
private void documentManager_DatabaseUpdate(DatabaseEvent event) {
	if (event.getNewVersionInfo() instanceof BioModelInfo && getSelectedVersionInfo() instanceof BioModelInfo) {
		BioModelInfo selectedBMInfo = (BioModelInfo)getSelectedVersionInfo();
		BioModelInfo eventBMInfo = (BioModelInfo)event.getNewVersionInfo();
		if (eventBMInfo.getVersion().getVersionKey().equals(selectedBMInfo.getVersion().getVersionKey())){
			setSelectedVersionInfo(null);
			setSelectedVersionInfo(event.getNewVersionInfo());
		}		
	}
}


/**
 * Comment
 */
private void enableToolTips(JTree tree) {
	ToolTipManager.sharedInstance().registerComponent(tree);
}

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
 * Return the AnotherEditionMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getAnotherEditionMenuItem() {
	if (ivjAnotherEditionMenuItem == null) {
		try {
			ivjAnotherEditionMenuItem = new javax.swing.JMenuItem();
			ivjAnotherEditionMenuItem.setName("AnotherEditionMenuItem");
			ivjAnotherEditionMenuItem.setText("Another Edition...");
			ivjAnotherEditionMenuItem.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnotherEditionMenuItem;
}

/**
 * Return the AnotherModelMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getAnotherModelMenuItem() {
	if (ivjAnotherModelMenuItem == null) {
		try {
			ivjAnotherModelMenuItem = new javax.swing.JMenuItem();
			ivjAnotherModelMenuItem.setName("AnotherModelMenuItem");
			ivjAnotherModelMenuItem.setText("Another Model...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnotherModelMenuItem;
}


/**
 * Comment
 */
private javax.swing.tree.TreeCellRenderer getBioModelCellRenderer() {
	if (getDocumentManager()!=null){
		return new BioModelCellRenderer(getDocumentManager().getUser());
	}else{
		return new BioModelCellRenderer(null);
	}
}


/**
 * Return the BioModelDbTreeModel property value.
 * @return cbit.vcell.desktop.BioModelDbTreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private BioModelDbTreeModel getBioModelDbTreeModel() {
	if (ivjBioModelDbTreeModel == null) {
		try {
			ivjBioModelDbTreeModel = new BioModelDbTreeModel(getJTree1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBioModelDbTreeModel;
}


/**
 * Return the BioModelMetaDataPanel property value.
 * @return cbit.vcell.desktop.BioModelMetaDataPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private BioModelMetaDataPanel getBioModelMetaDataPanel() {
	if (ivjBioModelMetaDataPanel == null) {
		try {
			ivjBioModelMetaDataPanel = new BioModelMetaDataPanel();
			ivjBioModelMetaDataPanel.setName("BioModelMetaDataPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBioModelMetaDataPanel;
}


/**
 * Return the JPopupMenu1 property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getBioModelPopupMenu() {
	if (ivjBioModelPopupMenu == null) {
		try {
			ivjBioModelPopupMenu = new javax.swing.JPopupMenu();
			ivjBioModelPopupMenu.setName("BioModelPopupMenu");
			ivjBioModelPopupMenu.add(getJLabel1());
			ivjBioModelPopupMenu.add(getJSeparator1());
			ivjBioModelPopupMenu.add(getJMenuItemOpen());
			ivjBioModelPopupMenu.add(getJMenuItemDelete());
			ivjBioModelPopupMenu.add(getJMenuItemPermission());
			ivjBioModelPopupMenu.add(getJMenuItemArchive());
			ivjBioModelPopupMenu.add(getJMenuItemPublish());
			ivjBioModelPopupMenu.add(getJMenu1());
			ivjBioModelPopupMenu.add(getJSeparator3());
			ivjBioModelPopupMenu.add(getJMenuItemExport());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBioModelPopupMenu;
}

/**
 * Insert the method's description here.
 * Creation date: (10/3/2002 10:34:00 AM)
 */
private BioModelInfo[] getBioModelVersionDates(BioModelInfo thisBioModelInfo) throws DataAccessException {
	//
	// Get list of BioModelInfos in workspace
	//
    if (thisBioModelInfo==null){
	    return new BioModelInfo[0];
    }
    
	BioModelInfo bioModelInfos[] = getDocumentManager().getBioModelInfos();

	//
	// From the list of biomodels in the workspace, get list of biomodels with the same branch ID.
	// This is the list of different versions of the same biomodel.
	//
 	Vector<BioModelInfo> bioModelBranchList = new Vector<BioModelInfo>();
 	for (int i = 0; i < bioModelInfos.length; i++) {
	 	BioModelInfo bioModelInfo = bioModelInfos[i];
	 	if (bioModelInfo.getVersion().getBranchID().equals(thisBioModelInfo.getVersion().getBranchID())) {
		 	bioModelBranchList.add(bioModelInfo);
	 	}
 	}

 	if (bioModelBranchList.size() == 0) {
		JOptionPane.showMessageDialog(this,"No Versions in biomodel","Error comparing BioModels",JOptionPane.ERROR_MESSAGE);
	 	throw new NullPointerException("No Versions in biomodel!");
 	}

 	BioModelInfo bioModelInfosInBranch[] = new BioModelInfo[bioModelBranchList.size()];
 	bioModelBranchList.copyInto(bioModelInfosInBranch);

 	//
 	// From the versions list, remove the currently selected version and return the remaining list of
 	// versions for the biomodel
 	//

 	BioModelInfo revisedBMInfosInBranch[] = new BioModelInfo[bioModelInfosInBranch.length-1];
 	int j=0;
 	
 	for (int i = 0; i < bioModelInfosInBranch.length; i++) {
		if (!thisBioModelInfo.getVersion().getDate().equals(bioModelInfosInBranch[i].getVersion().getDate())) {
			revisedBMInfosInBranch[j] = bioModelInfosInBranch[i];
			j++;
		}
 	}
			 	
	return revisedBMInfosInBranch;	
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
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setPreferredSize(new java.awt.Dimension(65, 20));
			ivjJLabel1.setText("  BioModel:");
			ivjJLabel1.setMaximumSize(new java.awt.Dimension(65, 20));
			ivjJLabel1.setMinimumSize(new java.awt.Dimension(65, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}

/**
 * Return the JMenu1 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenu1() {
	if (ivjJMenu1 == null) {
		try {
			ivjJMenu1 = new javax.swing.JMenu();
			ivjJMenu1.setName("JMenu1");
			ivjJMenu1.setText("Compare With");
			ivjJMenu1.add(getJPreviousEditionMenuItem());
			ivjJMenu1.add(getLatestEditionMenuItem());
			ivjJMenu1.add(getAnotherEditionMenuItem());
			ivjJMenu1.add(getAnotherModelMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenu1;
}

/**
 * Return the JMenuItemArchive property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemArchive() {
	if (ivjJMenuItemArchive == null) {
		try {
			ivjJMenuItemArchive = new javax.swing.JMenuItem();
			ivjJMenuItemArchive.setName("JMenuItemArchive");
			ivjJMenuItemArchive.setText("Archive");
			ivjJMenuItemArchive.setActionCommand("Archive");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemArchive;
}

/**
 * Return the JMenuItem2 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemDelete() {
	if (ivjJMenuItemDelete == null) {
		try {
			ivjJMenuItemDelete = new javax.swing.JMenuItem();
			ivjJMenuItemDelete.setName("JMenuItemDelete");
			ivjJMenuItemDelete.setMnemonic('d');
			ivjJMenuItemDelete.setText("Delete");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemDelete;
}

/**
 * Return the JMenuItemExport property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemExport() {
	if (ivjJMenuItemExport == null) {
		try {
			ivjJMenuItemExport = new javax.swing.JMenuItem();
			ivjJMenuItemExport.setName("JMenuItemExport");
			ivjJMenuItemExport.setMnemonic('e');
			ivjJMenuItemExport.setText("Export");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemExport;
}


/**
 * Return the JMenuItem1 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemOpen() {
	if (ivjJMenuItemOpen == null) {
		try {
			ivjJMenuItemOpen = new javax.swing.JMenuItem();
			ivjJMenuItemOpen.setName("JMenuItemOpen");
			ivjJMenuItemOpen.setMnemonic('o');
			ivjJMenuItemOpen.setText("Open");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemOpen;
}

/**
 * Return the JMenuItemUnpublish property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPermission() {
	if (ivjJMenuItemPermission == null) {
		try {
			ivjJMenuItemPermission = new javax.swing.JMenuItem();
			ivjJMenuItemPermission.setName("JMenuItemPermission");
			ivjJMenuItemPermission.setMnemonic('u');
			ivjJMenuItemPermission.setText("Permissions...");
			ivjJMenuItemPermission.setActionCommand("Permission");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPermission;
}

/**
 * Return the JMenuItemPublish property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPublish() {
	if (ivjJMenuItemPublish == null) {
		try {
			ivjJMenuItemPublish = new javax.swing.JMenuItem();
			ivjJMenuItemPublish.setName("JMenuItemPublish");
			ivjJMenuItemPublish.setText("Publish");
			ivjJMenuItemPublish.setActionCommand("Publish");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPublish;
}

/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getBottomPanel() {
	if (bottomPanel == null) {
		try {
			bottomPanel = new javax.swing.JPanel();
			bottomPanel.setName("JPanel2");
			bottomPanel.setLayout(new java.awt.GridBagLayout());
//			bottomPanel.setMinimumSize(new java.awt.Dimension(171, 300));

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			bottomPanel.add(new JLabel("Selected BioModel Summary"), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
			constraintsJScrollPane2.gridx = 0; constraintsJScrollPane2.gridy = 1;
			constraintsJScrollPane2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane2.weightx = 1.0;
			constraintsJScrollPane2.weighty = 1.0;
			constraintsJScrollPane2.insets = new java.awt.Insets(0, 4, 4, 4);
			bottomPanel.add(getBioModelMetaDataPanel(), constraintsJScrollPane2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return bottomPanel;
}

/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTopPanel() {
	if (topPanel == null) {
		try {
			topPanel = new javax.swing.JPanel();
			topPanel.setName("JPanel3");
			topPanel.setLayout(new java.awt.GridBagLayout());
			topPanel.setMinimumSize(new java.awt.Dimension(196, 450));

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(0, 4, 4, 4);
			topPanel.add(getJScrollPane1(), constraintsJScrollPane1);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 0;
			constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
			topPanel.add(new javax.swing.JLabel("BioModel Database"), constraintsJLabel3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return topPanel;
}

/**
 * Return the JMenuItem1 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJPreviousEditionMenuItem() {
	if (ivjJPreviousEditionMenuItem == null) {
		try {
			ivjJPreviousEditionMenuItem = new javax.swing.JMenuItem();
			ivjJPreviousEditionMenuItem.setName("JPreviousEditionMenuItem");
			ivjJPreviousEditionMenuItem.setText("Previous Edition");
			ivjJPreviousEditionMenuItem.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPreviousEditionMenuItem;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setMinimumSize(new java.awt.Dimension(188, 74));
			getJScrollPane1().setViewportView(getJTree1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}

/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator1() {
	if (ivjJSeparator1 == null) {
		try {
			ivjJSeparator1 = new javax.swing.JSeparator();
			ivjJSeparator1.setName("JSeparator1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator1;
}


/**
 * Return the JSeparator3 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator3() {
	if (ivjJSeparator3 == null) {
		try {
			ivjJSeparator3 = new javax.swing.JSeparator();
			ivjJSeparator3.setName("JSeparator3");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator3;
}

/**
 * Return the JTree1 property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTree getJTree1() {
	if (ivjJTree1 == null) {
		try {
			javax.swing.tree.DefaultTreeSelectionModel ivjLocalSelectionModel;
			ivjLocalSelectionModel = new javax.swing.tree.DefaultTreeSelectionModel();
			ivjLocalSelectionModel.setRowMapper(getLocalSelectionModelVariableHeightLayoutCache());
			ivjLocalSelectionModel.setSelectionMode(1);
			ivjJTree1 = new javax.swing.JTree();
			ivjJTree1.setName("JTree1");
			ivjJTree1.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("loading",true)));
			ivjJTree1.setToolTipText("biological models stored in database");
			ivjJTree1.setBounds(0, 0, 357, 405);
			ivjJTree1.setMinimumSize(new java.awt.Dimension(100, 72));
			ivjJTree1.setSelectionModel(ivjLocalSelectionModel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTree1;
}

/**
 * Return the LatestEditionMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getLatestEditionMenuItem() {
	if (ivjLatestEditionMenuItem == null) {
		try {
			ivjLatestEditionMenuItem = new javax.swing.JMenuItem();
			ivjLatestEditionMenuItem.setName("LatestEditionMenuItem");
			ivjLatestEditionMenuItem.setText("Latest Edition");
			ivjLatestEditionMenuItem.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLatestEditionMenuItem;
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
private boolean getPopupMenuDisabled() {
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
 * Return the selectedVersionInfo1 property value.
 * @return cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private VersionInfo getselectedVersionInfo1() {
	// user code begin {1}
	// user code end
	return ivjselectedVersionInfo1;
}


/**
 * Return the selectionModel1 property value.
 * @return javax.swing.tree.TreeSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.tree.TreeSelectionModel getselectionModel1() {
	// user code begin {1}
	// user code end
	return ivjselectionModel1;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getJTree1().addPropertyChangeListener(ivjEventHandler);
	getBioModelDbTreeModel().addPropertyChangeListener(ivjEventHandler);
	getJTree1().addMouseListener(ivjEventHandler);
	getJMenuItemDelete().addActionListener(ivjEventHandler);
	getJMenuItemOpen().addActionListener(ivjEventHandler);
	getJMenuItemPermission().addActionListener(ivjEventHandler);
	getBioModelDbTreeModel().addTreeModelListener(ivjEventHandler);
	getLatestEditionMenuItem().addActionListener(ivjEventHandler);
	getAnotherEditionMenuItem().addActionListener(ivjEventHandler);
	getAnotherModelMenuItem().addActionListener(ivjEventHandler);
	getJMenuItemExport().addActionListener(ivjEventHandler);
	getJPreviousEditionMenuItem().addActionListener(ivjEventHandler);
	getJMenuItemArchive().addActionListener(ivjEventHandler);
	getJMenuItemPublish().addActionListener(ivjEventHandler);
	getJTree1().setModel(getBioModelDbTreeModel());
	connPtoP2SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
	connPtoP3SetTarget();
	
	getDatabaseSearchPanel().addActionListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("BioModelTreePanel");
//		setPreferredSize(new java.awt.Dimension(200, 150));
		setLayout(new BorderLayout());
//		setSize(240, 453);
//		setMinimumSize(new java.awt.Dimension(198, 148));
		
		add(getDatabaseSearchPanel(), BorderLayout.NORTH);
		if (bShowMetadata) {
			JSplitPane splitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			splitPane.setDividerLocation(150);
			splitPane.setResizeWeight(0.7);
			getBottomPanel().setMinimumSize(new Dimension(150,150));
			getTopPanel().setMinimumSize(new Dimension(150,150));
			splitPane.setBottomComponent(getBottomPanel());
			splitPane.setTopComponent(getTopPanel());
			add(splitPane, BorderLayout.CENTER);
		} else {
			add(getTopPanel(), BorderLayout.CENTER);
		}

		initConnections();
		connEtoM4();
		connEtoC12();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

private void latestEditionMenuItemEnable(VersionInfo vInfo) throws DataAccessException {

	boolean bLatestEditionMenuItem = false;
	BioModelInfo thisBioModelInfo = (BioModelInfo)vInfo;

	//
	// Get the other versions of the Biomodel
	//
	BioModelInfo bioModelVersionsList[] = getBioModelVersionDates(thisBioModelInfo);
	
	if (bioModelVersionsList == null || bioModelVersionsList.length == 0) {
		bLatestEditionMenuItem = false;
	} else {
		//
		// Obtaining the latest version of the current biomodel
		//
		
		BioModelInfo latestBioModelInfo = bioModelVersionsList[bioModelVersionsList.length-1];

		for (int i = 0; i < bioModelVersionsList.length; i++) {
			if (bioModelVersionsList[i].getVersion().getDate().after(latestBioModelInfo.getVersion().getDate())) {
				latestBioModelInfo = bioModelVersionsList[i];
			}
		}

		if (thisBioModelInfo.getVersion().getDate().after(latestBioModelInfo.getVersion().getDate())) {
			bLatestEditionMenuItem = false;
		} else {
			bLatestEditionMenuItem = true;
		}
	}
	
	getLatestEditionMenuItem().setEnabled(bLatestEditionMenuItem);
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		BioModelDbTreePanel aBioModelDbTreePanel;
		aBioModelDbTreePanel = new BioModelDbTreePanel();
		frame.setContentPane(aBioModelDbTreePanel);
		frame.setSize(aBioModelDbTreePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
private void previousEditionMenuItemEnable(VersionInfo vInfo) throws DataAccessException {

	boolean bPreviousEditionMenuItem = false;
	BioModelInfo thisBioModelInfo = (BioModelInfo)vInfo;

	//
	// Get the other versions of the Biomodel
	//
	BioModelInfo bioModelVersionsList[] = getBioModelVersionDates(thisBioModelInfo);
	
	if (bioModelVersionsList == null || bioModelVersionsList.length == 0) {
		bPreviousEditionMenuItem = false;
	} else {
		//
		// Obtaining the previous version of the current biomodel.
		//
	
		BioModelInfo previousBioModelInfo = bioModelVersionsList[0];
		boolean bPrevious = false;

		for (int i = 0; i < bioModelVersionsList.length; i++) {
			if (bioModelVersionsList[i].getVersion().getDate().before(thisBioModelInfo.getVersion().getDate())) {
				bPrevious = true;
				previousBioModelInfo = bioModelVersionsList[i];
			} else {
				break;
			}
		}

		if (previousBioModelInfo.equals(bioModelVersionsList[0]) && !bPrevious) {
			bPreviousEditionMenuItem = false;
		} else {
			bPreviousEditionMenuItem = true;
		}
	}

	getJPreviousEditionMenuItem().setEnabled(bPreviousEditionMenuItem);
}


/**
 * Method to support listener events.
 */
private void refireActionPerformed(ActionEvent e) {
	fireActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
}

private void refresh() {
	getBioModelDbTreeModel().refreshTree();
	getJTree1().setCellRenderer(this.getBioModelCellRenderer());
}
/**
 * 
 * @exception org.vcell.util.DataAccessException The exception description.
 */
public void refresh(ArrayList<SearchCriterion> newFilterList) throws DataAccessException {
	getBioModelDbTreeModel().refreshTree(newFilterList);
}


public void removeActionListener(ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.remove(aActionListener, newListener);
	return;
}


/**
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(DocumentManager newValue) {
	if (ivjDocumentManager != newValue) {
		try {
			DocumentManager oldValue = getDocumentManager();
			/* Stop listening for events from the current object */
			if (ivjDocumentManager != null) {
				ivjDocumentManager.removeDatabaseListener(ivjEventHandler);
			}
			ivjDocumentManager = newValue;

			/* Listen for events from the new object */
			if (ivjDocumentManager != null) {
				ivjDocumentManager.addDatabaseListener(ivjEventHandler);
			}
			connPtoP2SetSource();
			connPtoP3SetTarget();
			getJTree1().setCellRenderer(this.getBioModelCellRenderer());
			firePropertyChange("documentManager", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * Method generated to support the promotion of the latestVersionOnly attribute.
 * @param arg1 boolean
 */
public void setLatestVersionOnly(boolean arg1) {
	getBioModelDbTreeModel().setLatestOnly(arg1);
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
private void setSelectedVersionInfo(VersionInfo selectedVersionInfo) {
	VersionInfo oldValue = fieldSelectedVersionInfo;
	fieldSelectedVersionInfo = selectedVersionInfo;
	firePropertyChange("selectedVersionInfo", oldValue, selectedVersionInfo);
}


/**
 * Set the selectedVersionInfo1 to a new value.
 * @param newValue cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectedVersionInfo1(VersionInfo newValue) {
	if (ivjselectedVersionInfo1 != newValue) {
		try {
			VersionInfo oldValue = getselectedVersionInfo1();
			ivjselectedVersionInfo1 = newValue;
			connEtoC19(ivjselectedVersionInfo1);
			connEtoC20(ivjselectedVersionInfo1);
			connEtoC21(ivjselectedVersionInfo1);
			connEtoM1(ivjselectedVersionInfo1);
			firePropertyChange("selectedVersionInfo", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * Set the selectionModel1 to a new value.
 * @param newValue javax.swing.tree.TreeSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectionModel1(javax.swing.tree.TreeSelectionModel newValue) {
	if (ivjselectionModel1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.removeTreeSelectionListener(ivjEventHandler);
			}
			ivjselectionModel1 = newValue;

			/* Listen for events from the new object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.addTreeSelectionListener(ivjEventHandler);
			}
			connPtoP4SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * Comment
 */
private void treeSelection() {
	TreeSelectionModel treeSelectionModel = getselectionModel1();
	TreePath treePath = treeSelectionModel.getSelectionPath();
	if (treePath == null){
		setSelectedVersionInfo(null);
		return;
	}
	BioModelNode bioModelNode = (BioModelNode)treePath.getLastPathComponent();
	Object object = bioModelNode.getUserObject();
	if (object instanceof BioModelInfo){
		setSelectedVersionInfo((VersionInfo)object);
	}else if (object instanceof VCDocumentInfoNode && bioModelNode.getChildCount()>0 && ((BioModelNode)bioModelNode.getChildAt(0)).getUserObject() instanceof BioModelInfo){
		BioModelInfo bioModelInfo = (BioModelInfo)((BioModelNode)bioModelNode.getChildAt(0)).getUserObject();
		setSelectedVersionInfo(bioModelInfo);
	}else if (object instanceof BioModelMetaData) {
		BioModelInfo bioModelInfo = (BioModelInfo)((BioModelNode)bioModelNode.getParent()).getUserObject();
		setSelectedVersionInfo(bioModelInfo);
	}else{
		setSelectedVersionInfo(null);
	}
}

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

}