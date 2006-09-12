package cbit.vcell.desktop;
import java.awt.Insets;
import cbit.xml.merge.NodeInfo;
import cbit.vcell.desktop.controls.ClientTask;
import java.util.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.clientdb.DatabaseEvent;
import javax.swing.tree.*;
import cbit.util.VersionInfo;
import cbit.vcell.solver.*;
import cbit.vcell.mapping.*;
import cbit.vcell.server.*;
import cbit.util.DataAccessException;
import cbit.util.GroupAccessAll;
import cbit.util.Matchable;
import cbit.util.User;
import cbit.util.Version;
import cbit.util.VersionFlag;

import javax.swing.tree.*;
import java.lang.reflect.*;
import cbit.vcell.biomodel.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 11:34:01 AM)
 * @author: Jim Schaff
 */
public class BioModelDbTreePanel extends JPanel {
	private JTree ivjJTree1 = null;
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	private boolean ivjConnPtoP2Aligning = false;
	private cbit.vcell.clientdb.DocumentManager ivjDocumentManager = null;
	private boolean ivjConnPtoP3Aligning = false;
	private JScrollPane ivjJScrollPane1 = null;
	private boolean ivjConnPtoP4Aligning = false;
	private TreeSelectionModel ivjselectionModel1 = null;
	private VersionInfo fieldSelectedVersionInfo = null;
	private VariableHeightLayoutCache ivjLocalSelectionModelVariableHeightLayoutCache = null;
	private boolean ivjConnPtoP5Aligning = false;
	private VersionInfo ivjselectedVersionInfo1 = null;
	private JMenuItem ivjJMenuItemDelete = null;
	private JMenuItem ivjJMenuItemOpen = null;
	protected transient ActionListener aActionListener = null;
	private JLabel ivjJLabel1 = null;
	private JSeparator ivjJSeparator1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private BioModelDbTreeModel ivjBioModelDbTreeModel = null;
	private JPopupMenu ivjBioModelPopupMenu = null;
	private BioModelMetaDataPanel ivjBioModelMetaDataPanel = null;
	private JPanel ivjJPanel1 = null;
	private JLabel ivjJLabel2 = null;
	private JPanel ivjJPanel2 = null;
	private JScrollPane ivjJScrollPane2 = null;
	private JSplitPane ivjJSplitPane1 = null;
	private JMenuItem ivjAnotherEditionMenuItem = null;
	private JMenuItem ivjAnotherModelMenuItem = null;
	private JMenu ivjJMenu1 = null;
	private JMenuItem ivjLatestEditionMenuItem = null;
	private JMenuItem ivjJMenuItemPermission = null;
	private JMenuItem ivjJMenuItemExport = null;
	private JSeparator ivjJSeparator3 = null;
	private JMenuItem ivjJPreviousEditionMenuItem = null;
	private boolean fieldPopupMenuDisabled = false;
	private JLabel ivjJLabel3 = null;
	private JPanel ivjJPanel3 = null;
	private JMenuItem ivjJMenuItemArchive = null;
	private JMenuItem ivjJMenuItemPublish = null;

class IvjEventHandler implements cbit.vcell.clientdb.DatabaseListener, java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.TreeModelListener, javax.swing.event.TreeSelectionListener {
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
		};
		public void databaseDelete(cbit.vcell.clientdb.DatabaseEvent event) {
			if (event.getSource() == BioModelDbTreePanel.this.getDocumentManager()) 
				connEtoC14(event);
		};
		public void databaseInsert(cbit.vcell.clientdb.DatabaseEvent event) {};
		public void databaseRefresh(cbit.vcell.clientdb.DatabaseEvent event) {
			if (event.getSource() == BioModelDbTreePanel.this.getDocumentManager()) 
				connEtoC9(event);
		};
		public void databaseUpdate(cbit.vcell.clientdb.DatabaseEvent event) {
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
		public void treeStructureChanged(javax.swing.event.TreeModelEvent e) {};
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == BioModelDbTreePanel.this.getselectionModel1()) 
				connEtoC1();
		};
	};

/**
 * BioModelTreePanel constructor comment.
 */
public BioModelDbTreePanel() {
	super();
	initialize();
}

/**
 * BioModelTreePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public BioModelDbTreePanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * BioModelTreePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public BioModelDbTreePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * BioModelTreePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public BioModelDbTreePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * Comment
 */
private void actionsOnClick(MouseEvent mouseEvent) {
	if (mouseEvent.getClickCount() == 2 && getSelectedVersionInfo() instanceof BioModelInfo) {
		fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, cbit.vcell.client.DatabaseWindowManager.BM_MM_GM_DOUBLE_CLICK_ACTION));
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
private void connEtoC14(cbit.vcell.clientdb.DatabaseEvent arg1) {
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
 * connEtoC19:  (selectedVersionInfo1.this --> BioModelDbTreePanel.previousEditionMenuItemEnable(LVersionInfo;)V)
 * @param value VersionInfo
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
 * connEtoC2:  (DocumentManager.this --> BioModelDbTreePanel.expandTreeToOwner()V)
 * @param value cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(cbit.vcell.clientdb.DocumentManager value) {
	try {
		// user code begin {1}
		// user code end
		this.expandTreeToOwner();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC20:  (selectedVersionInfo1.this --> BioModelDbTreePanel.latestEditionMenuItemEnable(LVersionInfo;)V)
 * @param value VersionInfo
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
 * connEtoC21:  (selectedVersionInfo1.this --> BioModelDbTreePanel.anotherEditionMenuItemEnable(LVersionInfo;)V)
 * @param value VersionInfo
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
private void connEtoC3(cbit.vcell.clientdb.DatabaseEvent arg1) {
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
 * connEtoC8:  (BioModelDbTreePanel.initialize() --> BioModelDbTreePanel.splitPaneResizeWeight()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8() {
	try {
		// user code begin {1}
		// user code end
		this.splitPaneResizeWeight();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (DocumentManager.database.databaseRefresh(cbit.vcell.clientdb.DatabaseEvent) --> BioModelDbTreePanel.refresh()V)
 * @param arg1 cbit.vcell.clientdb.DatabaseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(cbit.vcell.clientdb.DatabaseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refresh();
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
 * @param value VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(VersionInfo value) {
	try {
		// user code begin {1}
		// user code end
		getBioModelMetaDataPanel().setBioModelInfo((cbit.vcell.biomodel.BioModelInfo)getselectedVersionInfo1());
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
 * connEtoM7:  (DocumentManager.this --> JTree1.cellRenderer)
 * @param value cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(cbit.vcell.clientdb.DocumentManager value) {
	try {
		// user code begin {1}
		// user code end
		getJTree1().setCellRenderer(this.getBioModelCellRenderer());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (JTree1.model <--> TreeModel.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getJTree1().setModel(getBioModelDbTreeModel());
		// user code begin {1}
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
private void documentManager_DatabaseDelete(cbit.vcell.clientdb.DatabaseEvent event) {
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
private void documentManager_DatabaseUpdate(cbit.vcell.clientdb.DatabaseEvent event) {
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
public void enableToolTips(JTree tree) {
	ToolTipManager.sharedInstance().registerComponent(tree);
}


/**
 * 
 * @exception cbit.util.DataAccessException The exception description.
 */
private void expandTreeToOwner() throws cbit.util.DataAccessException {
	//
	// expand tree up to and including the "Owner" subtree's first children
	//
	if (getDocumentManager()==null){
		return;
	}
	User currentUser = getDocumentManager().getUser();
	BioModelNode rootNode = (BioModelNode)getBioModelDbTreeModel().getRoot();
	BioModelNode currentUserNode = (BioModelNode)rootNode.findNodeByUserObject(currentUser);
	if (currentUserNode!=null){
		getJTree1().expandPath(new TreePath(getBioModelDbTreeModel().getPathToRoot(currentUserNode)));
	}
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
public javax.swing.tree.TreeCellRenderer getBioModelCellRenderer() {
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
			ivjBioModelDbTreeModel = new cbit.vcell.desktop.BioModelDbTreeModel();
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
			ivjBioModelMetaDataPanel = new cbit.vcell.desktop.BioModelMetaDataPanel();
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
 	Vector bioModelBranchList = new Vector();
 	for (int i = 0; i < bioModelInfos.length; i++) {
	 	BioModelInfo bioModelInfo = bioModelInfos[i];
	 	if (bioModelInfo.getVersion().getBranchID().equals(thisBioModelInfo.getVersion().getBranchID())) {
		 	bioModelBranchList.add(bioModelInfo);
	 	}
 	}

 	if (bioModelBranchList == null) {
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
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
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
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("Selected BioModel Summary");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}

/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("BioModel Database");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
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
			ivjJMenuItemPermission.setText("Permission");
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
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.BorderLayout());
			ivjJPanel1.setBounds(0, 0, 232, 41);
			ivjJPanel1.setEnabled(false);
			getJPanel1().add(getBioModelMetaDataPanel(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}

/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());
			ivjJPanel2.setMinimumSize(new java.awt.Dimension(171, 300));

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
			constraintsJScrollPane2.gridx = 0; constraintsJScrollPane2.gridy = 1;
			constraintsJScrollPane2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane2.weightx = 1.0;
			constraintsJScrollPane2.weighty = 1.0;
			constraintsJScrollPane2.insets = new java.awt.Insets(0, 4, 4, 4);
			getJPanel2().add(getJScrollPane2(), constraintsJScrollPane2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}

/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(new java.awt.GridBagLayout());
			ivjJPanel3.setMinimumSize(new java.awt.Dimension(196, 450));

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(0, 4, 4, 4);
			getJPanel3().add(getJScrollPane1(), constraintsJScrollPane1);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 0;
			constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJLabel3(), constraintsJLabel3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
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
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane2() {
	if (ivjJScrollPane2 == null) {
		try {
			ivjJScrollPane2 = new javax.swing.JScrollPane();
			ivjJScrollPane2.setName("JScrollPane2");
			ivjJScrollPane2.setPreferredSize(new java.awt.Dimension(110, 48));
			ivjJScrollPane2.setMinimumSize(new java.awt.Dimension(110, 48));
			getJScrollPane2().setViewportView(getJPanel1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane2;
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
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getJSplitPane1() {
	if (ivjJSplitPane1 == null) {
		try {
			ivjJSplitPane1 = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			ivjJSplitPane1.setName("JSplitPane1");
			ivjJSplitPane1.setDividerLocation(350);
			ivjJSplitPane1.setOneTouchExpandable(true);
			ivjJSplitPane1.setContinuousLayout(true);
			getJSplitPane1().add(getJPanel2(), "bottom");
			getJSplitPane1().add(getJPanel3(), "top");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSplitPane1;
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
			ivjJTree1.setRowHeight(0);
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
 * Method generated to support the promotion of the latestVersionOnly attribute.
 * @return boolean
 */
public boolean getLatestVersionOnly() {
	return getBioModelDbTreeModel().getLatestOnly();
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
public boolean getPopupMenuDisabled() {
	return fieldPopupMenuDisabled;
}


/**
 * Comment
 */
public cbit.vcell.biomodel.BioModelInfo getSelectedBioModelInfo(BioModelNode selectedBioModelNode) {
	if (selectedBioModelNode.getUserObject() instanceof BioModelInfo){
		return (BioModelInfo)selectedBioModelNode.getUserObject();
	}
	return null;
}


/**
 * Comment
 */
public Object getSelectedObject(BioModelNode selectedBioModelNode) {
	return selectedBioModelNode.getUserObject();
}


/**
 * Gets the selectedVersionInfo property (VersionInfo) value.
 * @return The selectedVersionInfo property value.
 * @see #setSelectedVersionInfo
 */
public VersionInfo getSelectedVersionInfo() {
	return fieldSelectedVersionInfo;
}


/**
 * Return the selectedVersionInfo1 property value.
 * @return VersionInfo
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
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
	connPtoP3SetTarget();
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
		setPreferredSize(new java.awt.Dimension(200, 150));
		setLayout(new java.awt.GridBagLayout());
		setSize(240, 453);
		setMinimumSize(new java.awt.Dimension(198, 148));

		java.awt.GridBagConstraints constraintsJSplitPane1 = new java.awt.GridBagConstraints();
		constraintsJSplitPane1.gridx = 0; constraintsJSplitPane1.gridy = 0;
		constraintsJSplitPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJSplitPane1.weightx = 1.0;
		constraintsJSplitPane1.weighty = 1.0;
		constraintsJSplitPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJSplitPane1(), constraintsJSplitPane1);
		initConnections();
		connEtoM4();
		connEtoC12();
		connEtoC8();
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
		frame.show();
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


/**
 * 
 * @exception cbit.util.DataAccessException The exception description.
 */
private void refresh() throws cbit.util.DataAccessException {
	//getBioModelDbTreeModel().reload();
	getBioModelDbTreeModel().refreshTree();
	expandTreeToOwner();
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
public void setDocumentManager(cbit.vcell.clientdb.DocumentManager newValue) {
	if (ivjDocumentManager != newValue) {
		try {
			cbit.vcell.clientdb.DocumentManager oldValue = getDocumentManager();
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
			connEtoC2(ivjDocumentManager);
			connEtoM7(ivjDocumentManager);
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
 * Sets the selectedVersionInfo property (VersionInfo) value.
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
 * @param newValue VersionInfo
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
private void splitPaneResizeWeight() {
	cbit.util.BeanUtils.attemptResizeWeight(getJSplitPane1(), 1);
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
	}else if (object instanceof String && bioModelNode.getChildCount()>0 && ((BioModelNode)bioModelNode.getChildAt(0)).getUserObject() instanceof BioModelInfo){
		BioModelInfo bioModelInfo = (BioModelInfo)((BioModelNode)bioModelNode.getChildAt(0)).getUserObject();
		setSelectedVersionInfo(bioModelInfo);
	}else if (object instanceof BioModelMetaData) {
		BioModelInfo bioModelInfo = (BioModelInfo)((BioModelNode)bioModelNode.getParent()).getUserObject();
		setSelectedVersionInfo(bioModelInfo);
	}else{
		setSelectedVersionInfo(null);
	}
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G470171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DFD8FDCD4D576383F3ED4D4D4EAD6D8EC2151C65F28EC2332220D0ADAACAAAAAAEA2928A8B12D28250D0A36479F7F941A18D4D6E833E59B98A1FF95158A84CB8B13E4C25C350D01F9B0A3434CB87390D4505F39FF4E1DB7EF5EB0C8797573B94E3B675EFB6E39671EF36E39775DF7110ABE8E1F1F13DBA4C9B973A57EEFFD0EA45507CA524389E91390B7515EDEA0297E7D93E019347FE0BE7C06C3DE66
	7CDD8505D2517EC5100E06F4F35E2E027761F939D43AB3E88ABC78E9E78F641DBD786901515B29AA47F6965274F36B97436F5BGB381C7FE739D6D507E023A959C1F427188B9390023E57ED9D74CF1DBA15D89908A9065E065636077CD5A72719B6BF839BD13A2254C5BDD39CB889D95CD0455CED6F7AB7FB5C82F66E4B8C9DB5F57AD66F5A6C0FAA6G69FC1EF4577EB2782DEF6D6B7D3A3B4B2A146F6F16ED36F2336C6ED39C4E0D9BB7DB9D5B9DE659565CE5F449F22B498E397B883D49F50BE8FDC92A01F496
	452D3110B6E640F3BCC02A406F98A278FAD7FB0191406642FEFD75F9BD673B6350B5D2DA6F2DEFAE75532FA0976BCF144B532F20B5DDF99F50B1F02DA532CE073C456E768283C0B30099A08860A3BA76874A5740EFE3175264686FF7589B1D4E4DB6330B768D1EB7EE049C0E5BA9F7BBDCE6C9E2635BDAD726434FE8406A7D77286BB119C4D663D8C76D3CCCCA39722C5C05BAB2C909BC149B66E63208F6FBE4425ABE5BEF5BC9EEEFDD74EEFB1724EEDB1AD335F03ECE5B458FD4ADCC67F432D5EDB7A8E42C27D3
	9DA806E7D20F68A943DFC171C1AABC534B8A0AA769C848AB9AC05D68F89BF9E9491FAE95D61E2DAB07501BC3F2A78CB09EC287343C3CFF10500E9BE0EDEE913CEC26780E7D9EBC6365E72A27A4BD8F72D69CE8AFE07AF5281258E973A130D100A10009GB3G92GD28F210E5DDC3EF60CBA56EAF2A91B872CB6336C12D85DC3EB5F03DFF9C8F11936595D0A495EAD371BDCD6D317CDF6CB3CCC50DAA203AE2FFAA7D15D6F06B63E136D324B24D89DF620EE6A17955965E6E5A62FDDC64A38E41AEB34764B50C03F
	D3C23A672CDD8E3FB613DB7959E9B6A93227844B1F3E160C49FED3BBD08482704C6492D9C564A58D032CGCA06D18EABAB883D9FE497F80B0D9BDB9C0E3E81271174C49A9AA6F23E0C7A0EFAF8B68E230CA32A89AE8652C9020E31629D951DE6EBB7E15F64BA00B696E921F68F65970D34972C86E8G688418B002FA74DC876AD17312CB25327B4F54552309205361A3CCFFE60EE86D699A5A8E61B59572244360E3G9AG4C07F19ED8E76954501C4079D94D69A11F6439F3C45F8E4766DBFE72E9EB89376105BC
	CD7C72FC7EDCB00EF65E96E37CCEAD35DB68E7B8C06CE19C1B53AB8CCC57B71B5CB2EA778E97A826EC760C9463F76541E0AEF34257CA000FA5B61F929EAB812A81B6907D87A093F029C43C9133455DG7F81780634875087708BG1805E482B895E09A40AC00B6008100D3GCE82788A60A700DE001F811A048C37D21E1221BF529150DD00D600A100F000F8008C009593E08C81ACG9381E6822482AC7AFF6D8595G9DG93GA281928132G8C473597EC85F082048244822483947ECFFBC19DC0A7C0B0408C00
	E4GA988F568BFC306DC0F1CF07C468AEA392A71251AB44A9B539B786FD7AA99B7F1596B35739317734FFC0CF0FCFE6179A67EABF3393F4D47CCCD434D4769EDBE5E23756BA4BE1E270ED18E530ED2EE01E0566FCC6F3782531527292E201EAC99878D62AF343E6183CDFAA9ED4B13DEAEF9F465D3004FA426A7AB7D34C37429D6E27AC47C866AA76A64177C77DD09699DF97EDCC3638B00757C797DG7D5AA4B1FDDEBBC6B9D47E4EE3D37DBB537B2D40FC757115E49C657D32DDE9161D325D8C8FEE1E97D2C974
	00479DBD8ED1E7B245BBA1CEE9024040AD702115C49FCC0A6232F68DA8F21B2CA8D6FB2FA7BF1C36659E68F2FAC29CEC6B22CA222B0EC108D3C47928CAB247F7BB6CF619CE9A228A4F3F0C469832CD6E87261B9C83F60561232B48BA2CCFBE40E31C19D58BA492235886C83C4456B6B3CCC43F1DAEEBBFCCC5A464120602C87CF12BD81B91A8819CD9233540EFE790468D031FE25C307A6045D26C2F4EDC3DD8B8744253F3E30358DC1E9464099B98CF97D76B73FEB90DF5B85E4DF87D6ED7A2D7DCEE09451644EC
	FC39F4701FC42E4D0E6E81D2E93B49EE6A85A9323C018DD466C0100A10D22864F9276DA992F93219C7F0E424ABD9FED2233A5DD607F34039DD368F08F61317AED76533E5A0FD6471418F6BC89FB3216F0B0E07396FF80C8F3E28A179983B71402049E163BCC2DE6771E4AC5E3094F1D956C13A75F8B696EF0B7CB58E4C0F04F42CCFFE3C48CF04F43AAA1F0D355D299E6BACD53E61841823CFA0E5BE9DAAD21529BB41D30675E5C5EDA115613E819942F292487EA0746E635AF7B4F96DDC87D97E6735C487ED2A98
	3868C45266B987F16DBF738496C7A642EFAA0084792BG4F4AD98FE1394E9399DFC3A732B8F40AD7396AE1AC97F1A22393F3A20BD513F9393AE0D26EF5D1AE1D67CB410CEEF1B06BB323B7F3844B85717C30E09677A6FB35FB78B0160B89E6FCA585337E24852B5B5DF1844BC9A7317C95A7B13A95709B8C404AFD15036542CEE27D086665B2CEE27C31F2D2AE16ABB999E2B4004E13D9397013D9B94BA924DC1CA8174A7333CEE6F48BA15FF88A367BFDBE160B86DC82403CD33C6931F293BE44F2E5A12C3F4DA1
	2C1CB1C4DD2E6EA3AC27GBE84A0AAC40FDE36A8B7AF0449A57FD46F71E065EEAE46F2ADBC5F4CFF07CFE56552CEA56506D7E23928D3D91C9AF7AA6BEF927C168BFA5F2F42F29527C13C8AE0B9CD0F3F22B5D8AE9272E37D160B9765D2A1BF4BEF3961AF305C0A5319BED51C4E4AB51C2EAED7D3A674CFD3AE4C2BDC3AA81722A917F93ADA8F423E44F2F593D9FEAB7C5AGA6CED453F3168B79CDE4639B474BAF1E242637EEAD165BB30915D3A6B1B98771F2051369BEE0055067C9ECBD90BD09510B1F24EEB7D2
	145B83759400A9137564E72B44F2C910BF4FEFB9C39516DBFC864BDF4DFF6B4FD07763E1D12E738CA63F21B318DC024FD0537B5FEAAC97FE866B6FCCCECF1A62A53F9AB13ED3D8390AA95CEFF915DBD70B6562C9B9G69CC3DFE3CAA4AB5C0FE47193E65587C7937D2626769BE66DE53FE53D039FB9062120D9B3FD76DD92E5DC066DDB23F8F1D09FB13F76E22FB16100E87C8BD93677FFB16605E64143EC8A9E5CE65C2FFFB2ED9E7B2BE0A4F724C7F93B39A973079714C75244DBD10E7BB8B674669356A7D11C6
	375B51ED25DBB3FC4E78E91DBABEE03876F5EF41EF0F5565D636C1GB704734B1EF504DF32F74351FCAF6BDF5645921B43A7C03BB381B24EA278EEAB091B489C427A59DD0D7D1CF870C229734CF3F28B7C743366EC56BF63591EFE169D4E1A4F5A7B49016D05C0FE5C59585EE98E529E190BD83967C539ECA8D3930A654A1C249C19E358F8AED8D16CE7BCB1C4E163F72E135033811DA00030D02C5F396DA8763569A3134748376AF10FD9BA62D79E5121BC6E8BD56DBBBA0C8BD9BFEFB9087DAC1A8A31A5G0B27
	569CA27DD4ACD6B70BBD3E6DD1479EC1D0AE92A0C1145FF90874271FC6DC38CF7BE2E383AF1FF059F4E9664210DC3DFDF34BC5213929D399EF0B4E717098BA2DABAF8A0071B97990725981E5A61E036D9E30921AE65DF8F365B076015158B30CB462213EF4AE52382B0734699D17E2DCB9DBC363DFA30B39AD57C1FD2720F16A881DC3E1105A10CEB95A7C1241C2EDDEC9E9BB13517465EA99559F792F2E0C0217DAF2A34FE532C9BC57A3232849DDF93113D1C60F0FE07F0A43E01E8FC35EB6BB17CB3A31B53741
	0B8E8B1B00BAF1225E5BC7306FAC3D6E081E2C990D21A3A817EC28DF73872471581137285DAB32DB59E1379DC01964377C407C045C6AF2B8E517F22049E232774AA5AD440C4AED70DC5E860BB7FB6FA695E6C757DE68038E2634DDE23E33C8A2BE096C9B587E00365C13033CC5GAEC570F6E74E17D4E7875CF2B97D3F49E66D6E134DA5143CE9D0A927EB51726DA4EF8BF9AC15107F77CA095D1A68E03AF758E9CD4E3BDEC5E01071D21A0B3C941D8776F69E4676E75B3C6C6BBC36EF9BF59E72BAB137CA34D74E
	FEDAE5D70F43556F4BEFA34DC606993FBF8D56402FCBA692EF54D457322D2AAF78FED6709D8BBCE5893E0CF984EF35DB952B49E6BDA817143276CEDBC356254E81D816DB018E8EF2495886130D65C9F90BF81B956763BCF069AA353DACB29D2FEBAFA1F3FAF3AD67B3BB19F83E47DE0A4E684AABB9836D451D0FFDCA06B20561B896C1FDFAE331B59C7B5C7261F8470235FB7107586E9E20B9C550AD7C886D0D49B0E59961C39EF21A6CE6627E0D0E9DE4CF22844744749152C9G9A7953107FE97DFA7C57CF43F6
	62BF7AED7CFFD60C6D36824DE0C1B724381E7BB96AC24B49530F7064E6264DE87399121397339AEB01511FAB6847826DACC1FF67CAFDDDCB29DDCD64E4A74BD62343E1B3DA1DEE26E8585877249D214B972EC4FFD7B19D627F69C8FF71AAA2FF132B176FB9542C4271E03AB73C9BF5EFC2C23434E279293A3AC7E34F69CC676227FBF4AFB53CABAFB3DC685EAA6C630A8BA0463FG476EADC5BBF6411097F5817218DA7A5B466E2BB5C236C0B3CB34DBB42057EEC5847A5097C43DE6408DC7E03DD57B756AC50BFA
	97FE01756281A7DDA8E4DD662D6B915B5791FF611277DB41E9EEB1DB69CC8C6153B6C56E5FC207362405C6C86EFD36F2559CC6DCBF6F5BAFE5E2EE06F61C97E20C15755F9AB1879D9DE5C6F75917C8B792E8268A3AEFF712BDE3135D21D8E45751324CF93ED550DE71C75003BF22FC4A4A7566DA36D7F423D53DD7948CF562G528132C57DDD3A75D91BD34B459CF4914CC197E19DE96DC6D24724183ACCB0A56283FB59DA228ACF3B29CFB7F715B773A25EF34D6E72E65F36FFED76A65792101E107F2FEB51DE03
	005798416FEDEB3D631367E8B9C2F1A759BCF649AED6FFED8576B7936A16C5627895FDCDFE69DE2C043CF714929F56ABAB98F9B729481514FAF995625F2887D3175944FC45EBAB51D72CEE0E106AB27B7C7A8AF3A47391A1112A38EE49AB73E381987F77887ED320CC5145E8FB0503FAFE3E65E214CFDD65EF7395E7D6E23B1DC0B3D4347B1ACB4F6693AFC61B5FD6057552GD77C3F484F0CAAFD7FFDB255E73753E6D548AAF8276C064CDD323557220839AE2F9AE99A01DE082039375ADB87A653F2DDFC700835
	317A77097AF1D0B7DB545FD833C9CF67F74ABDAE59ED990B5233BE047CC262DC972F6671C9D3EB305D1ACBE0EDFD093035DADDDBEB06A07D28ECED5D1151EC0D13BB9ADB2B9531B2701AF2890EF931C3CF570A2204AD567EB6DD7BF49D36DB82B4BB23305D9F1DFA6D06C6613B681A8DF806A8AA0A59CFEC14674CC292BC272B52F4FF061E2DE96CA06DE5C73133B5F9827F8345AF3E5403677A33CB2C2DA0EF4225F89EA4FF93515DB4C8CB1735972CG28GE885F0DE067BA8E96B487B36D14F5BEC33AB324BEE
	3235F93D1042F3AF6BC93BFD72811EBEEF3D5AEFB1B914D9099CC2AE9B7BF98DD6EFA32D97F1947518FFAB7D377A2CDF7E326FF3737C7837A4CEBB6DB20FFFE3323B2A1E0C6D6A4BE1CC81BAG1CG93AFC739FDCA4F070DAAB7BCFB4485467A344BB1BED9480E2315852B7752B85B4BF39EEDFBCCC71B76513D404B190E3EAB706D941F2642B35D1DC6771EC8FA458C0895E6206EFE74B5113F738A7027G91GB1GA9D7207C7B6B826A6D16A1DD3DE57D1BF5B05059426CAB3C7B4E6ABD98301E61CF5E7578FB
	D4A3F96F2E3397C766E89EBBED6DD147CEF5C0478C908C908640561DA3BDE45DC976D1237F04325A778D5967DBF1E5FBC18DC08B0085A084A092A061CA14598DF51B83492C91DFCA9B9D6231C97BE299E591FAE8FCB27CE340FAE9D76A556B9A9EDFFB4E4075F45BAB9B5156AB0A76AE275F5EC61FFA651AF405068E9B47279B48B8EE05BCB340B0C0A8C08CC0B2C0F6B40EE361062F826ABE19400C0EA640EDF5B87AF4061073BA5B07573149742500750A2F522BF769617135F7FD40FAB53A6D9DBEB23EB18C71
	2937CA13EE51ED6F830954B3733C212BC6771FA1D7E12CE04B999F1F87FD6AE9798C5765F3ED6E785A7B4A271E363D19D769793F7879C4F7F57C9FAECA799E69DDB65CD7CB9432795BB8F94D498BE4070B2E562BF79A2DE738FAECF3DF4D556807FF59CC6D9752E600E100D0009800E4006C2B51FECD9BC8DCBF967BE551282F75B2BE8F640FCFAEA77A54530EE171B55E755858DC5F07E3D3F38D0E4D659F0E0F079BFC6AE94746F60DDE3D5E0F46575E00CFBDED1F03F47B1C5C0FFB76E1224FDF940F0F078DBE
	7534FDCE526D738B63EC6F0D006D4D53ED6F121563735157862C17BFD3EF5EFB240958CDB1645581348258GA682C481A44EC43B39F2C34078C59C5B9FCB7CF2E055B86D47271ED61752B57DE4715D40BEF5FCA7DD4B560B2B61376EDA0C6F9A95343156EBD15F1A560C0F4F1FFC6AE9798C3ED6CF67FF98C09E42858FAF0F130779DF8462A1D917071F776337D1990207CBFCE80D0D07EB8372D0F21DDE3D6E327135374F271E363D865D765E7FF2FC6DFD66D3CF5BDE07EEFBF70C333D07823667543447E200A7
	064A25B11CA758AEABA63281448FEFB25BF926CBFB5EB4985A0882083D8EED67E5AB6ACC72F5F8B6E55ADE323EAC3C3E3D20842061FA5425734B475777CBFD6A79487AFA3DFA725A7179D4F740FACE5D76EAAB46573FA61FFA651AF47075FABE7CBFDB088F8F073C9800A4008C00221876028AGE38C7A70059B499975317A70406B178747594F27FC6AE947D1095173CB5F8D22EF8C09C1DDBA37F2FCBCDC6CD3CF4BC31CAE8FD5EE64A1C5703023EAFCBC7C62D3CFBB5EC5B76855FB33FAFC6D3D6FD3CF5B67B2
	5D769E9AE7FB33FD6AE97B572C5B5E79B563748FBE75347D5B235B1EDCBB3E765CBE75FC747886BD9DEAF388BD3E81F528F45D78F8D86FD34FC70FF5F9B85EA974987259F70DBF507305C5FF863F71E7447D59C2F04D106E9038F377925CB0242788DC8CDD1F4C04F4621F31CF5FF510F7B0E9A27D93CDE70B74C33BBAB43C0E95884DC0E5D89BAF0E3B0D3CB114E1FEF549A67F5F2794ED527FBE6543CD7A5F2798B6ADE3F8F2B8C635F30A79A51BC67FFE25E2135E77ABABE9FF9E20E7F5DC83F6455AAF53E6D9
	ECA08F11EF1A407B6F9C30332901346C566E25CBB85F1C7BAD99C7430DB82F5C3629CA721DD7480BEF078B0998E513EBC7CF0FD89834B965EE647906FA7F720B2B57175F6D757A72CB2C9FDDFE49757E644774E55B3E92782D073E3582D8G02G4281E281D2G32G9647425AA19675F88F2D53G694ED8F6761E1C67A7E7664939E0F266173C871D904B4EC533BA97B943398E911A28CFC11C36BFFDC31E32EE74AED32FC9475E9858BE88047BA9371A7340F83DD7703AD4D566CD4973BD1D9E4C9775C7372B31
	FE7B676FBB60F73DEC71532F7D6B521A2F75F52962EBFDDDDA7775683A7475577EF409595BEC05706673ED580F90E1C9F80ED4BA74A6AF532AB8DAABA50C57FB37909ABAFBF6ED5DAE074DC662F38963F3E31FB6BEA7BEB69C46B0BA96637389BD686F93E2B1BE1FA493FF10FFD3FBC1B1C04DCD281B4775901F5982694E1B467E8ECA8F94289F8C9086B00353627785FC63FF1C0E7BC6FF1C023FA13A62F68C383AE56DD81C7E4D8A116709D84966A691B77F760F56D7CCAEF20CC3D37FFCDA1F65792B9F798D51
	41AE2B596A62DF0131B13C5BEB8C2B4458CE9DD663EB04DE4CFAEFA955AB37DBF6534DC6326262F53AC7304E964551D4A9784BE870AF2F179B746575FA03FFF965B786165712C67F72AAEA8CAC2F150D7E6C0375F555E135FCEA85BE4D8BDFAD70D79DD14B064813459E9574ECEF9C68D752CD980F1CC9F129104E9038D40A13EE06671B71FC4285B9EA1A9E9EFAF255F8727DAE330D7BEC5AF84BG34EAEE76EFA77C5CFC1E0A5EF68F6F9752332CDB21FE474D48E78645D9C87AE61CE3FBB77B9FFF7BE67D71D7
	B67B9F7FC31B830FFF50E67F63FF4A664063BFE5736863BFA3DFAD63F5C24631FDDA9987039C42824A78698F5574BCF6F85EC7EF29ED2A825BF924DF5B4E8CE8A3CE53CE32AA4D68F595FB512BC4FC49CAAFFC95621FD8D9007A5C63F2740317E772195D64FFCC1FEB529F534CA67FE37ADAD360B14DEF76BF26854D01473428D95F874ED8655577DA1C475EFC5F4F3BF77A15AE1B1BAE5D07FBC773EE46392941C5662655373497548398EF413969DBC5EFCF46F68B9B238937786A887F7EF80D97FF6B447DC883
	FA7426F0BA11FE6915FF61C5EFBD527B77FEBDFAB1BAF43C69BD61CDEF83624FA9D3632BAA907F6317DEF821033965EABFC3EC00458813A56D7906C460A993E0D19C46881FF4637B96C39C0E43DB74BB2C8EC8BB810263F09C322D236D0D05477976171DC5F9370F500B0EE3E7D1F284FE9545472A707C9B943A07C8528910D7F1AB7A6D87547DEC724C91E1EB557DDFAF70368AAF9B04590369333C453F8D5A36685B20EB0BFF9BBC30A5308D9E37453F8D1E34A5308DCE5EB23ADFFD3B52CB172AD1B7DB8635E3
	B6856499F9EBA05D3C571BDE1D58FBF06B6ABA271728C31751BB21CABDF6F5825FD02D9E3BCA01AF2CD1175FA0708F54F871D5A3F420564BB62A105F8E079E3FE99ABE33EE557A7B4EF5DE7404BCCFF66A51DBFC1BB7BD43EDBE734786B27ECE175CA33BDC32394DFA104472365B083E9F22FBB0B5705CF09B6A7A7D96F2B7044DF440B120C8D271DF68774F622C778CA8978F1081107F97F6A795733DD78C921D6FF1197AC1BD6802BB0B567D53DE2CEB067490C0A840CC00643FE01B7775113AD0515ABF50CFB9
	E47CCD23E7849A625B8BDA62316CE7C5246C77EDF8D037127B1C63BA54BEE7A81E71968E3F517198571C7F6D9235AEFB2D0DD648C44E5F37183ABC77C134BAD8DBB2AC4BCC0A4305782EA5040EB8513083CC59C36717256ABCF67815674D14172B73DAE5D73F556D764CEB03FB8A55790D2EEE0BF53F4C72ACADDEF92D83DDB62B5B424F9F1B97E01E704DE7AD72663D1A632357F97137E54869F0A992BB67FA56BE75B95704F8765EB2A39E4F41F6F8FD1314BF0B6513E7161F365FDFBE7379A1FD68734BE6F92F
	8B4D109E8618B28BFD7E71BD23797CA8A897BB4BDB7719EF1F5FC368A54D521E29DDCA71E93334E7EA4F1671CCEDA664F55C0EF1F184DD4346C0BAD160DE2338D4C8E789DCD7AF6ECF665F0138D3AD38BFD9A2F057B0FAA7C20CA1F03328BCBAC97A8E546F6FA47C7EA7648EF227E65FF6C968D69466D5E75CC16A5FCC5309226E4B18DF4572176410F4CD8236778C35A7A324AD82D7CE4FBD8CC3BAA481694464A18F0C4E3DB49DAA729766FB37331226E78A1A8F76E1BB8982E730E1BB298217C95B1E87E969CE
	44FDE5277B4210DEADF053A92E8252ADF722FE7CE91F1EFEF45C69FFFEE1FA90CDE3BD5B1DCC8F5E94781BA8BED805E77A71AD4513F48464AD3A8BF97A053E3F37C1FAD8607ECCF141108E93385E81ACB7C360628730DC0C406D5B0F6592890E7A3416AA3D3D1694484F388B63285B4537260B6EC61B2A6DA2BCB7C03A8340F9B74A6C73EE1F354E5D9EF90541730C3B75EC6AB00D4B626E565AD4B045A75D2D3529C271BDFD9A64EDCD44BEAE97FB79D1094853C7643AB3BE472DD6BDDF3AF73CFB69E3815646ED
	47348D5667EE7A9EB1997ABBCF48A10A6224FB60799E447D684671AF96384B5D28A7A58267206FF36AA15DA2F0C9945781E9C56026B9315C84014B5D0765C288D07F127823A421FF3952E200DF3BBCE8909FCBB4DB7BE5BB1922CA2AAAAA4A8C153595256C6CFD78207A6CFD5DDB47695E79C4A0749E26D3D17CB7640D297C1EDD1795636DB448AB3E97E3094ACE2F7B8C88A39BB7FE67321AB71BFADBE8E442F47FBAFA8E0054B75E0B3A1FCEE906C0BA82A066DE7CE6F768DFF8D7C1549F27CB09CF695FD111EA
	9C49CD3A17711945FF69BDGEB3A7278B9313D58E64DFDE00377915C822FF911487214BC767E116557F96513F81AA83AA7FF2FD7BE6177719CF5FE18D7BE11D1B72F4F7AF647E758376CBBE6C8362B027C5EF99CFD9F6BD3B27FB55CB5B5E7DEE86B82268BF734FAF4E1462ED1F421F2C39D68C2CD9D7FF66A5CF2BF77CF08A80D0FD787119D09CB8A13F03C5877DD8A24C3GA212D0877E5663670E929A21B1FAF76E43BBCAE2C43DC90ABFDEB092E236BF49E75FA7B349637B965D8F7AF83F7F782262FE667366
	8BFCA145B72B706A780224BBA0AF66FE3445309E7405068744FB4D9E0C9B1C8F20FFAC68A1F3EB10C83FCB53646E9C167E03FC2CFC18274D2B0FD99BF9C25F2E6FD15F5D41638E5A575087B06EF82717680E423E2EB642DAD9E2FADB3F1368ADDBEF9C6845B9B38D6AE5812CF8303D208E2083002DAB2E267159902403G589AE73678A6E386606281721321EEB23B2708FF775CC7F44D65985CCA3F8B14F09D155507BE21A2997D4D5D7FD17B4742F3EC3938572015C1C3B2533316E40F3F617749C9643EB49207
	15438A7DC7F910DE17D6D20AF67F152CEE2362D00B5F6F1FA6F05A9313BDBE183571E90E3A0D9DB633E803FF4FCA7B56EF177B9DF6EBB76B6FF29A37113BBEE313D1B66BED749BC89894B6A6334935271CC6FA9E71F91F50CFE04AEDD63B5C269C30491F087C6AFCC26F13C6FB2FCDB6FF425A18CF634547G9213F12E5A7D3307E6015D172FCFA8EEAE318F4117516E4D57F03B074635F63A9E64E72F7B7BCDF6B3118DAD97CD2EEB65F3782CFDB8B7E7A423CDDC67A26B31ECC89B9EEAAFE88630BC04F9FF21F9
	643E292D7C9EB0F247975F97F10D551688CDF55A7010EFEC1B4D475372107A9D2ABC66B696EA52FA776862BB54B7D5E93005FD5EF2FD3043A3575B5C04AE3B8B414CEDE436027B2E418F61F8967EDB92B67442G66070A7C6A7F50FC6A6B1F2946B8B772A11CBBDE5EC9FCEBAAC98394BE0CB23EDBC25FDE72302F4C6A81577A301EEF3F18464D660735F16E15942FBC2C0DF3B7F5E11C9B84F9698F239F7F1A6EED367C353D20732F083B410C7EBEC860AE23FEA89252B1FFC55E03681DD309A2FD9ACD13BB46D8
	7ABD6B6F6D0BC9DBEAFDD8A62D7D5D5BD03F07AFCEA1FDDF3357405319FF759D23C5A98C47775387FDEDFD92455DC146AB85ED7D16C1EF1DEC5FC37407BD9F784523EB0F38D15794D1778407F75D307FFA74F92E433F1F3871D7CF9B7FB2F91E1F68A24F6C8C4B69D49F02202D70945C0BFAF787392B375D345777848BA0712A9A0625AE190F4D5028FB617EECFC19F442B86B5DF85475184D6C21FB6931A95A3DF7196293D234FB6F4F0935C0B26455BF025F5A441A493C9C8E699800A400F9G0B9F05B55123A8
	4B7F6E2810821CB5B5BA1CAD72FE5926BADA415F4B6DBD5A7E313977260598F3C7DF773F524C931A7D5EFDD67FA85365BD7CF7E642541C3705EE345138A38472A28192G52G32G8A9F437E7DF1C7996B9FD107C6C5B1F5DB68EDF7625014D59E74204966AB9F3B0E233163550F696F1D54813E8520D3152F40B39B4FB769FD8A410FE96361A20A8FFBCC9B8F1FAB6EF31881F9C52968576E957BB9C3291EF6C278F3B87CC627FA7347FCE8A40DCF62A1AFA5957D6A677D68EB2547459E924515C13A79F1CFECCD
	574310F68A5CFB94C76E158BFA7C770D31319DFDBB7BFDE26CBCC9FF8ECFFE1C4EE3BD4D235445398DA1826A4C84F81727939E7758AC7323CFD1D9991EE8AFA8FB827DE8D3B75D0BA6211E580BCE6FE91058DA32D96EB18D5894F163D2098A39FDC02117A7718B1BCABE31B9CCE6D83BFDD22638866452D296871C5F0BE70BBA1E407577033D5A757797DE6B6FB7AD5A75B76903A7FF13C53B7E3EA3D71D5FEF512E3F31BE737573FAA5C96C5176FCAC79DFB713BD1475D5255C961676E27CB0917A927F842EE113
	B793FF26DD43522BDEB6EED409304D6B36C0A61FDDC2BE521398A73DC75FF756C13A954072A40E55D29BDD33C8680B26DAC8B566F3A66CA477556B4ECF60F13E873658C5E9C6D32F287FBFB4D61970A47A2D381D1F782381C8F2661F5D2EA74E2D74135F4F4D1F73F4310D269D185EE02769014F395C676C75487D5645440FFE5F2AFFF993AB73E8B35165963DBB18841D490B495F16E9543DCF894BDC7CE111278C7F14C55D4EBF949ACB00563274227D18E6367E3C4FD975A9CFFA7CC2A4BC47BC29971F763511
	F1CCFCD26BEB77D3FC5A13DADF7B7ACE7435D91037A78DFD6153921E3D0E9538D2A77A4CA4013B420A7B912B1F925F01F6113E54097435B4CD6EFEE269462E517C982977DB7C9BBF77B0EA9BE3B9CB9D4807AE17FE6E76FD8FEA6B46BD0296680799A084A07CA9F677955BFFC8B2E3191900CFG18873078E97011CF235D5DEA71DD9F25D8F08F21C5143BE08151D7BF1FCB71F5514E3DE86F1D225E49FB896FCDB2394F856315C199631562CBAE100ABFEE48C5BF286DFF6853EC0C621E766CC7F86492B9C8EDD0
	C61B4CF423EE73F3D85D242CF8474AC2D76ED34E6DA6F6A36EDE225778B28F4046350B5AB3FB6ED3BDE77E6E6B91DF1DDAF2CC5F85307E4FB27BBF7F11E856BF7F7100D97F3CF00AF9743379E9E67FFFDBE22ED97D37A5186F7E875DE34E02B1AF1A5DDED087508250B9DB7CCDB83ADF43EE8E51461FDB4C3D647C8979CBDC0A38EB3947FFDF3BFB827F7DAEDB8FB9875165D094C7BF21AF4E43741071D29CCE0AE438419E3AAFC77664D8D10EE7F72858649E0AE6BE6DF13A87BD8C7D8A1D0DFE2EECB741C5C1BA
	C16006F623BFCC9538883ACFD5118E32C9C7DCC5A5FD07CB5269688317D691DC882443856EC30AA3672048DDAF8CB72DF22CBAC7E8115F7074317BCF3ECFB566B650EF9E4D5FD2E2B218C37747220137C4A11739B4AECF03F426403552BDBDE9CEFB410AB9623D9775DFE510EE1E037E6B46BEDF5F7B00B8F75EB1071DC1F142EF509CA64F58B9F45F255F404B044D7115530CB96ABD05956B744F1C5CC157890B1E81BBF886794926771129CE43733BB9177D5BF3E6241A1F01784C26BDC3C178EBGDA9F53BA92
	BB97CC7FE5FC2307E3314DA49212F05F7D78E2356FEEFE30B977D3C95FF79B1FE1FD0CF846633BBBFAEA5EE4B4DF33CB227F0ABCDA1CDAD787F1EACDA9572108DEF53C128234B31E99DD8F793B0A5EC0EB6B7C39FA5F19FD9A301E763BB37EED0CD7BD435C51F92C190BEB5813E9FC3CF52EF62F628C0AB74E556ED5947761DEC5A764454DC59FF92215682D1B4734CC3F97541031AD1957DED0BF8FF57E78DA74AD1D8217338E5727A17350BF6CEBA338F0C8C78B5CB045A54CE35FC5B3DCDE1B5A46C959231D1F
	42C729435AA876EA63E87F5E1A5A3F6881333D25B974BC8870128540562C8DCEDC332EFE5617C76D1A358E4A34BC6BD933F6BE2BDD33BECCE3DA9B60071EC57B3E6683EA47B4302A64E3BC259B47B8824A25BC0BEB33274B3D6E37E16F26B7BB485F6CE12F26F9DC16520D71DC51F362FC2B1950EC013499E078B9BC43712A99E338A94F611A36CA562EE9498B15F2895724E6F974B56D9E1FB56DBDDE6F14CF733B26E5B2791B0560FACCB63767FE3FDDDEFBE58940EB6AF3236B9A0BADD29D86784D0432054F37
	9754G34GF4828C8104GC4BC0FB674E59F793E4D5F1A11891A469AAE13957FC654821B7FD8E309ADF02C719E1D17E408910C871CBC0E7D48C6EC5951536316C5CCE330958A5CB6F72B43CDD707CCDED99D742EDB6BFEAB5184C7B77B9B95CC671651D86206DCC90AFDDE6C136CA66F5D9CF65968986836EC2177EB338F9FD91DDFF6639EF56273E88B276F8E34FF233607D42817793C479696E5F8EC013FF34DA77D69F658A1F49C00C5B3BFDBC15BFF37925BAF4A90E753AA491C69F0114BBB557D7B3592F5FD
	35A8EB7C54E3D3D53C3D960787076791A5177F73299F38687E9C50B48B3A391A770C417BA4511FDE9B1D53CDC31AB97D889D5F09C0A3CC5011776A4F7FA3B49E0907F2C999F8176CBF043F17DE00587885345BEFED58EF678BE8630A0D140D04F4ACC032A85B530FB61E718272F09E5DAF00D8164F73FB8B5573FC4A01F63FEB341297195D99DF74DE23B1193C687248C411096D79BD47312E4CD0D3DB4177567608BB39C2DE447D77D4B75139B8C82782647F2D3D20982082E0AB409EGA7C090C098C0B4C0BCC0
	8AC086001489F194C0C9A65A77837D252358B7BF6463B1F08967516B693ED6BD50A1FBF38C97CBF1C640117DBE763DDB6CF222CFD6BBF94D5C447E522F206126651DD0BEB893675DEB69F8C7C1BABE936753092AF3E8D3D54F1F749D7BF56826E35A86530915B53E71F5CD0D789B6C19AC3E4E045FC5AF31EFB7D8FC9DD6EB60E58CAF797A7D9A0EE3F6D4C163192D00B33E047A3EFD09A7DEC61F70F13BA6362EE2F174E5ED5AB81A70B161A5FCB71CBE605171D54E51E2594A8A50713AD2BEAF6F93F791CCF969
	E8E37202C1C958D87910277D95DDE3E91F0D6FB9BEE7B047269711632C7778D15763E7BEE92C127012B6A6BE116213DF52464405E6F1168F72367E9DED698D333A7DA840477EFD7418015925FDAF5A7B4B425E5DFB515E2766611CBC5761FF6EFF5191F86EFF4D213F4F1063745DE7D860545FE7D86CD46FB3B05CB2A7236B929FE1B37C7B4EA5427670C6FC71CD5FEE72DBDDDB013EFBB76E2D9D1D1E7EF240231BE5155EFB517BAB493E3E575F259027744A5B376CB4EEEBEAEC79352D35E51BD1687A04039EFA
	29FD2B7C6B7A3A5AB2C3D5C5A9563B601027DE535E31573BED58D3EF53185AE33A166F733D6C58EC641DF1567B61286BB19BCA27B1C2525F3536758245277EDDEBDB87EDE8DB73A02F75E5342DBE2FEFCA23819F7F72D86271CBC68C701B82E533G0A33A0A687588A508920G04E461FCFD673E514EA98855D14D580CB7132EECA23398FFB1D93EFC72EFCE5D7E6D79B5F7E0FB4ED7746D79AD45571E17AA7A767C2E62EB4F9FAAE44E68057824C3147BD860F68B5CEACA1360F6D94D0AC56057D03E889E8F8F72
	7BA0A8DF03945741F135AA5CEE0E5B2810B99B3AECF138306EB774BDD30F55E6E369A6DA46EA0765091047D64A2355E9D2713DDD60885FECDDEB7A57E2C96C4DD356993EF8E80987AF4EBD72778C3D7CBC64F61353C97D980BA5260BB31809B04E69G2BDF01387995757EDB62DE7ADD237A8D260431431C3D3876E8FD85E370BDB5E24F85F0212FE0DC9DD103B1F862AB98035FCB4BAEFA353D40GD077AA160D2B45393F75D54F5E5E8221AF68FFC20FC8024F07F792DB71B7372F2FACB3D4D7E07C7CD49B528F
	FE95EDF43F57F7FB3100CFFAF5ACFBE7C1640891FFBED5757C1E79584720EB0FE99BECAEFFCF46797D739C44FDA40B79DD70721C4D537717D54F93EC47DE8ED30FE99B2C4F4775239C9E91F2B8319F65F0EDAE5A6C55A37E7D628DA3017D62EDA37AFEB1E14457AF5EB3226F979F9C71750BA9A36CEF0E7402BD28631CA7C674629C7EEE052EB353418E32DE45F558A3B6719D5DEBE8733777224D9B8197729A6E415D35E214739158F3662B5816C613295B423FC1F8470267C863DEC3FF31021EC94B06F4D1F6FB
	C1D936787BA8FD246DCA218FDDBEF73E0DCD0F1C632CF771D157E3F1443774FDC5F336B63E58C1712D595A78A2D97856BD10971B0D3EEB16571DF32BDF87DF7A7AD8FC57B3EDFAFC37703A1DBAB478F713CE34059C011B6CC4DB706CDB1D2E5983CA27DBB3EC0FAC5306FBE40ACEBB5ABD32E0A8C360DD0E8FFB5DA39B561744006FA274E57068B86B3DEFA175A2C711F38C64A52A72595806D09DCEFBDDBB66E7D2FC466B5AB1DFA56EBA48063C4EFF08736964DA9A4E4B4F2A67C7EB0E3D3FFD791836416F6529
	C59D7BCA785B27EAD1477EAA4A659889AE8E6412748F74D1BDC671CE789F98D3FCA67675AA5E505BCF4018023A94CE3B1C7AA08B149FFE83E91FC0F1A1100E9038C4769DB124135E4071192CBA97F12E6AF9757F417712DF9D53B678DE0A515B364BC9125B760891EDBB6B0D40363DD85AD5C0GED5BG4F5E363D46EB7DD486798DG01F95CE2717DD6E90F05B0EA047A2C4C9FACE4FF5965F0A86DD63715EC6C335876AA8B463691226CEC8B212D7D5B3FAC1616445D8B51D01EFC17C41E93B81FCC87AFB3228E
	26815E3F8E7EB45F23030D7D3E3E765BFEDFDF6B3210FBFC47FFF6133EC1AAAD957492AC389F417CD009B877DD1C332B40884066E419FD478969880098GB6E6D79A55E3168478741C51470C55AB9F6779777AF11EFFFF08721F1D33CB630F1F2078C5393BB47E38CF48218872068850B1395D68BBCEF79B55634478FC4CAA0967F455733E7F0333D3A79F53B65E27FA93B2B48DE26128E725790A340C7D99CD7E57AC49DFF4F63B24F7FC57E70D27BD74E0542F4DC539D26EB6E9010586045B247903E4F5BB6484
	32E781443B77C907296027G5C26C82B3B2C8E1B23575AED3299280D3B8DEE4561124D862B5D00FFA3CF1196302BBD94A91F5C7F21C8EFF92E7350A45EE6A7CE8D7CB429A23D430F0EA2263C3CDC118A91497EEEADC1ADE037F3C09E9EEB7550D848EF8771FA623702F81EC45F36821895E9F113231F5CB1E958E5D5ACA03425C2E85B682EC16A7E6A0DF9CFDE1017AB65034458EAE11B14CFC2FE901C0B89B08FD68BD4FC79AA10C4D35F8344A8D2014100A797B7E2DE4E4D1097A7CA12A85ECED3C5FE2979AF13
	AF4A505338E3C9513F21F389A890F2E640BFC0285F02A2FD50467FE239680C21ED203F5F64BA20DFC392FF1B6E087A5B4C2C633B7DFE9FDF3700792F967E6B795BF47D0768777F000FG60EB3D03646F057BBB175D56EF325994132D2F51EEA6E734B79FD0E44F19C057A1BA4FE0991E494F7751FBA5772A6A7CFFD0CB8788DFF4D1B3B1AFGGE418GGD0CB818294G94G88G88G470171B4DFF4D1B3B1AFGGE418GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB85
	86GGGG81G81GBAGGGEBAFGGGG
**end of data**/
}
}