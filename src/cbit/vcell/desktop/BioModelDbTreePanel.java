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
import cbit.sql.VersionInfo;
import cbit.vcell.solver.*;
import cbit.vcell.mapping.*;
import cbit.vcell.server.*;
import javax.swing.tree.*;
import java.lang.reflect.*;
import cbit.vcell.biomodel.*;
import java.awt.event.*;
import javax.swing.*;

import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;
import org.vcell.util.document.User;
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
	private cbit.sql.VersionInfo fieldSelectedVersionInfo = null;
	private VariableHeightLayoutCache ivjLocalSelectionModelVariableHeightLayoutCache = null;
	private boolean ivjConnPtoP5Aligning = false;
	private cbit.sql.VersionInfo ivjselectedVersionInfo1 = null;
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
		cbit.sql.Version version = getSelectedVersionInfo().getVersion();
		boolean isOwner = version.getOwner().compareEqual(getDocumentManager().getUser());
		configureArhivePublishMenuState(version,isOwner);
		getJMenuItemPermission().setEnabled(isOwner && !version.getFlag().compareEqual(cbit.sql.VersionFlag.Published));
		getJMenuItemDelete().setEnabled(isOwner &&
			!version.getFlag().compareEqual(cbit.sql.VersionFlag.Archived) &&
			!version.getFlag().compareEqual(cbit.sql.VersionFlag.Published));
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
private void anotherEditionMenuItemEnable(cbit.sql.VersionInfo vInfo) throws DataAccessException {

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
private void configureArhivePublishMenuState(cbit.sql.Version version,boolean isOwner) {
	
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
 * connEtoC19:  (selectedVersionInfo1.this --> BioModelDbTreePanel.previousEditionMenuItemEnable(Lcbit.sql.VersionInfo;)V)
 * @param value cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(cbit.sql.VersionInfo value) {
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
 * connEtoC20:  (selectedVersionInfo1.this --> BioModelDbTreePanel.latestEditionMenuItemEnable(Lcbit.sql.VersionInfo;)V)
 * @param value cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(cbit.sql.VersionInfo value) {
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
private void connEtoC21(cbit.sql.VersionInfo value) {
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
 * @param value cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.sql.VersionInfo value) {
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
 * @exception org.vcell.util.DataAccessException The exception description.
 */
private void expandTreeToOwner() throws org.vcell.util.DataAccessException {
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
 * Gets the selectedVersionInfo property (cbit.sql.VersionInfo) value.
 * @return The selectedVersionInfo property value.
 * @see #setSelectedVersionInfo
 */
public cbit.sql.VersionInfo getSelectedVersionInfo() {
	return fieldSelectedVersionInfo;
}


/**
 * Return the selectedVersionInfo1 property value.
 * @return cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.sql.VersionInfo getselectedVersionInfo1() {
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

private void latestEditionMenuItemEnable(cbit.sql.VersionInfo vInfo) throws DataAccessException {

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
private void previousEditionMenuItemEnable(cbit.sql.VersionInfo vInfo) throws DataAccessException {

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
 * @exception org.vcell.util.DataAccessException The exception description.
 */
private void refresh() throws org.vcell.util.DataAccessException {
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
 * Sets the selectedVersionInfo property (cbit.sql.VersionInfo) value.
 * @param selectedVersionInfo The new value for the property.
 * @see #getSelectedVersionInfo
 */
private void setSelectedVersionInfo(cbit.sql.VersionInfo selectedVersionInfo) {
	cbit.sql.VersionInfo oldValue = fieldSelectedVersionInfo;
	fieldSelectedVersionInfo = selectedVersionInfo;
	firePropertyChange("selectedVersionInfo", oldValue, selectedVersionInfo);
}


/**
 * Set the selectedVersionInfo1 to a new value.
 * @param newValue cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectedVersionInfo1(cbit.sql.VersionInfo newValue) {
	if (ivjselectedVersionInfo1 != newValue) {
		try {
			cbit.sql.VersionInfo oldValue = getselectedVersionInfo1();
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
	org.vcell.util.BeanUtils.attemptResizeWeight(getJSplitPane1(), 1);
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
	D0CB838494G88G88GB9FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DFD8DFCD44535B87A02C60C8A8AB6AA55A0D123850D0A9A1557521636D8531666E11B5A68CB157FAB7D1536D4534A3FCD5B1B8FA05120D022200D3AFC55C4E34CB7A18449A7A498A0A4B1008448A6FB13DD582F6C5ECDB690601D79B833F76F5E4D06A80F5F6F30F74E4C1CB9F3661CB3E7664E1DC8396FC72DCACB4F152434D5927F37A3CD12EAA2A5E96F4FF73C0F382A8F9232A5553F4DG6B25F752
	B261379972E6FEB3A1FB1DD4F6B68B525D10DE7EE4C276C6F85EA07D68E858F4F88852CEB764FDA1FF7BDFE337333F945BD9C353CFEC4E015F5781FAGC7FE67FCC05ACF583C096343B89EA1AD9DF079244C6FB667F15C8AA8138538DA004EFCD65E8A3F0C014955D53CDC773498A965072E7435040E0AA6C2BC2F3B027F9A243F24C59610365E5A12436B64C33A8700F4BEC37A71D0A17C56341D9978342F5722948D7549D6EB11C9F61FD19C4E6A6AFA0B630043A4DB9BFBBBDD325CE6344B565C8F893D13DB32
	E8FDC9CA82DAD194D7EAA3ED9C05E7A7C0780708BFBECA70CB61B995A077A36C576E8FEAB85FC74FBDA6ADA935E53E9124DF059F317EF4FE646BD72C6D3974376998CCA9A3321E8AF973G9681AC8748A9CC48AE8478979D7BE9259F42EFDD2F526030599C76BA27335EEAF2513E41F3F5B564F0DC3B5C67F019A4090DEF5E16C29DFE4682D66F43CB2E47E492D306E3BD277DFEA969611B53576848A4A966DCFAE3A113C5F721CFA62C6D3E20ED0F967A6B22FF5BC7A4F55BF97BFE1631CA276D2A721FE5CC65F4
	22D5EDAFABA4E3FD9F55010570CC6A91BDE57847A93E70E39F1E69E5954513F49B644D7F98F563686B48CBEB661D523A122F694AA172BB916979C50C075AA2ADAFDBAF905A56A256E6136065A045CB1F78700C97A355D3121E8EF98BCA923219FE5DD4C16CF4F99958E8F9C23681208A60A8008290DE0EBA76EC69A7635431B623CB2977D82CA659A5313AB7153D853F32D7F199DB6CEE45E86F133B0CAE0B3157AA3BA5DEE6E69911C12FDF3DD9D4777B200DFD32DDF699950B438E540DB6D911DDEED6663E3275
	240CCB26391D961B8C8D581C9252FD28EC831113512DFC6EB4999559D70265BFD1C646E44858859401GBCB339AC28A4720A017EAFGD8AC64D0D1CE68FDA63B40DBD4D737BA9CE7BC4ECE529349D0C164FC167A0E25D0BE359CE5BCFF874119A0FF348269D84B7F292253E869A36C9BDDA3E8E331B6EA77D0BE96A09EE099C0D6E5C2F6FEA56A516F0F229EB52E1DAB95BEB3C3D70F6600CEB7D7B27D6929545A5337E8BB0457F0480B81D884308CA0E7874E831F5AFAB4B4898F4BB8DF2F2B78A44FD9157AF6B8
	BE5FF2AC202D355C065770B4714B2B7873EB93E867F5B1469F6E24F68B7DEC86B06F403139275C40F43D5E6816D13B8F39C0B1E513EF24983F859742394C89DF6F821049FCB3673184208CE0A751FFG98DE29C0A25E085962B10063GFBC8FBGE7GCEG00D9C8C6006BGE681BC85508160813896609A005DG078186GBE87E890B26C24BCCD027E44GAC86D889E02802FE82B881A28196GAC83D83793EC9E2087A09CA096A091C02A86DD83E8837082C481AC82D88130E9974C67GE60029GF1G49GD9
	B5895945GC781A6814454208E8D0C9A52FDF242719BAF28652A4697E852A8EFCC6F643F3BD5B2EE603257EB67B0177367FC0CF0FCCE70FCA37F15395CDF67E3262661666374BA9F6F317AF58D9F4F6B46A907D917A837D07054D7CC6F4B82531517282E201E2C1DG8D62AF343E61EDCD7A8D5A16AF3DC172694AD6006D925313F70334C37429C2E27AC47C866AA76A64477C77CD09699DF97EC0C363A3009D7CF9E308FE6D1218BEFFB2CEB914FC45E3D37695537B3240FC7557AB48B84AC332DDE9141D325D84
	8FEE1E97DDC17400479D7D8ED167BE0AF7C21C52G0101DB60E3AB08BE989545E56975A8F2072CA8967B00AFBF0E36657674BAFDA18E36752D8A222B0EE108D3C4790595E40E6FF3586DB21DB4C4951E7FFD9AE348D65986CCB7B8BCF6056163AB49BA6C0CBC42E31C45152BA59223D8BDA4DEE2EB1BD8A351EF274BE203290804DC12E1B709BF26ED59241251E2703FE40D16823FD93BB1EE985E0AF143363377CA8BCEB95375E261483BEEC8B76FE6F37968EEDF5C40F87A768EFD5EFFC0E39D0EF7B3DE771DA2
	F2456516D8ECC94C46B7C8E7B79339B6BA7ABC245281235DB8GD2E4F91E1DD466C0100A10D2A861F957F7670ABC5944A3B8B252A5ACFFC91D3A5DB6075363BCA05BBD225D44B7B6287259B210BE7278202F1A74F1A674BD96E051EE0C8F9A2AC8BE46EEBCB0E8F0D8B90F101775A9990BB576DCAE4BE4283BF2B79B0B57C5FE290279ED10B6FF2A4DDFAC723D101E2A4AE7E3BD64D10FF514AAFF91BCEF5AC34AFCB21A2BAA132CAA437AD23CF39D156120C706301C84321F893D5B31731F1A3CAE2E03ACFF4FCE
	2203D6D58CBC1F36F97785DC7B776CE1F12497FE4381E2G5E83BCAB6739086532EA99DF06DA960756542A4B6D1130DCEBAD23E32AE5316AA4DEAE3916144BCE43F2D3F9FE8C273B3016751951DBA84A9556317C46BA9677CE72EBF7CABA16B355B13EC66BD8FFA26A546D9E4F40F2B1BCFF81271B883F4575909B51F2AD2B30DCE3BD6BC7F7BDAB97D94F78E365F2D6E3394500DF861055404AB5B730F26B9A29FE08F261BCBF2A01510D035F5486EC37AB8B4BF5C3BD85E0FA23BFBDD6EE1B5833D952487A3B1C
	17CBEDD417CB9265721A92324B81BA1B7468C5ED41F2531B18DC66B4790F87AB573F954B25707C1C3D6CF75BDED6AEE2AFA9E7580665BA7732B8553A1775F794FE0B843D5F0BF20900DFC16A6E5363AF2E984B35C13EF9DF30F25692A1E7480F8ADA4ED00A65966CE37A14484BAD5B27AE17A84A95B67B17EBECD6170BA843F2E11AF2B31B55FAD0DB0E65121BD97ECA784DED01383EC5CDAF37C2482F050D2F3505151F572226772CA817391F154B5B4F64DC381F150B5BCF77C3AA05BE6FE76B016E7D0C1EF33F
	3A5D569D025EE7C08B2066B3BD792D2B42F22310BFBDE83905225CBC1E9F4FFF17FE266E47051D423F34B27999DA19DC0ADB55748ED6E33966D6565F1ED6C6AF462FDC6EAEB13E3CDCA2AFE7B820AE772CA86784FCB8C04C813DFE5CDE036516C13EF4B0309C1BBFDFAEA0FE1E6EE31EB68E993DC56EE108CB2A2B772B76AC7FCA779D487CEEB808FB131D1F53BDCBC8BB81428F627C7F533538B7B97DCC0C34F25FB3994176DC238EB2BE969E744D7F235F9A4EE073635CDD244D4CC3A053C3B8B7BE31D33DBFD2
	67F6BB7AACF4EB064F9916EAF5FC40F0766AD761375F62F2ABAD9040F9F1FED12A893FE46F062379DED611BDC7E2F3F8BE345B8390F908604F58C95CC46690564F3EB26C675433F7C9BDB3EECDFFADC8BF138E317E259E727533F5F7C586EB6F18026D1537014D34E1FBB7AB24BDB29731F22F08F251D0A6C914AB7710F2E40EE16339FAD3DE10714490050D5FEC8F2117FB986C8E2071B0566FE93904FDED7A4864F1FE2FFA5C673FF1BE28BC3A8F73386F30CF9EC9DBDF61727849856C67FC48CF81E071D465C5
	52CF45ECF13358E331D59DFB9436A7E43781A86DD87E64C5529F9B0D38F01F76253ADADEBE7E7EBB2538B59169FA7B6666FBA253435B99EF316DBE9E273E78DCFAB4G6373760B482754813E30835B3D42CEE81AF4634D82897B40E818A52461047AB10246D327C91B7EF1A94615AFEBE8142665F0DBCE067A1BBA1146ADE9040682035401F40A5066578E2F53666597F4B1995D39C1AD231A0BA7F5E5947E06B93D2D1349465B6913D1741267526316200CFE17067DDB88E5D285EF7BBD9BA45D581A5B60436942
	A6BE871B789C6B3D1301FDE7697A8CBDD9B39AE199A817E8281FA4E87CB663D5EA770A6CD68E592DA3A8134C564F181F105BDC8E276CD2C69A4CC67B001C5FCA4C28488A4FC59D30F8338F6CD2E18E751E06BE68E08A3AA466BB8D1944A711FD035CAE34E557AA642D95F0E1DD485BAFD6FDCCF55663160B687F8DD6CB5F995914CF499B0715A23A96ADBAC0721A48E301047C3FD5C06C56C88753FD48CEEBF25E75AA82030C170A55484BFC60A3258BE37B59CEBF7BBA4276EDBB0FA02FB3D7170A760A58CF1B6C
	6AF738EC017C5651ECE41871FBF8389CFEDDB211F81D263A16EDD5FD41777F97FC1B0127A841D74F9A023758AD0A45E8351C15738BD8FB57FFC85625CE8FAC4BADC08787B9FFFC034946F2461AAC5EE662911C87663E273617AC63E4DDFB09784DC07A3A234CCE2A0E7A6CA5671967528D4F203DDC4C42BECD02B2F1C7F1AC26BA7446E245D16C738A43C4470235FBE0B3361BF98C6222E3C8F75EE634B7A6432475048F596BB45ACD447DF7BA8E11BD09FC9C13D5DB100E82B4669CC37E678E6A713FD434E35D72
	6578FFCA343B92E896FF01F40B3756F0BFC7DDE891F9BA88CFEEE65A0CB61FA1B9F9B12B3196987D9B368ADD835AD1027E1F376A6BDAD245B6A2A7BBD936F6BA9C56CE0B534D948D9B5BCF5A913A5C328D7DDDA250DEF79C6947EEA372B73A86781EC362B69C8F26FB2347D077A6454F13B6ED38CED777E86CF91C691C7338CF7792DFF8AEFD798B28FB9B37E39F97C0191547F16C5E72EA472E389B7CC0B77298D67C65466ED745C236C0B32A9B5BB50C68351B580DBEF4C68956DB8E38EDA7305E07E7756AF51F
	40FAAD25D84F89381893C256257E3A9EFD201214F149C396F01ACDA68B1D09A1FCEAD1E4DB939D5A7CD69AA139872DC52AB90C38FE5E3734B2B1B7937ECEE20C3572E4391803AE0DB2237B08205B86B443855D57FB481E31516ED04C326BD2D966BCDB4A059EG5D15A7D1BE0B4B7566DA36D774FD1BFA2F2878944C59GD38122CFE17D3F6A56E7ED6E2E90F39014CF91F522AB2AC99D23E26CB54214088F6CE5EB3EAABC6D23BE5D54DB54480B784FB5470A9A835BBE5568CF2EB5A4BDA17F0CCA3457421E04EC
	D38F72FB3A52BFBE391116A3945B496631CBF6317A7FD5097D1D89F51774607865FECAFE69DE2C043C9FADA0BEECC0D6B072EED0114BAF7073AA443FD18F26AE330B790A3F3E0B3EE2DBE334D415F2A6282F48B1B29FD1EE74790A78780A0C64F8DC33F8F6A07FE1D0E63E91EDAF6F1C1E1FCFB122FC12F6FCB9DF31338A5B4D6ACD482E6D45F6578C6B593C3797ED7EF722DE84609676A2BF1DBB757D77948B51E73753EAD148AA38DDF6C3669159B2E0D644DCF74FCE24198A744A7B10666B557EBAD0C977F9FB
	796091EBE375072B313E956AC60B7A4FD76F525379F6395FA53B4D63D1FA56070893A44EF571EABE1F34FB97361B84ED661A042D6D523535C68852AF4956AAAF0EE5EB1C5C2558DA0D08150157B0930EF93EDBCF57660B7E1C2E79F23A7683516EE22019A5E33BDFA8FA6D564A78AEDA5A0AE708BAE5E6BFE659F7E6E1941E272A52F4FF061E2DD9C477D322E5F636A6C36093A8FE1E8A4F787B62F33136023C7CFEBC8F12370768EE8424E3G96GA4822C8458B4007BA8295564FD5B1867EDDA6C0A6C329B2D9D
	FEAF24705C4BAE526E99F904279F5D2576DBCC8E65F6A2077201711F57E0756AE93D56CB28477CDB41976A33FE196B7727E78471EF231CF644004F3FB1597D28160CEDBC601781C8E61099C0159965362B26B0145C706C9197986B53A9E5E23230A917AA8BD66FEF93EC6FB54F253647F47433A13A97E8E6BA7A26401F247888951E696E3DF46F092497C01E55023ADBD6CF643F69B478D300D6G93C058E9147F641A10FA5B645555DB563FE7AE04BADB98FD5A3F6F2C5E0BA16BAD5254636FD1BB49FBF71D3DB8
	B2C7735869D7D6F56C148CF4F24E407C85508840561DEBECE45DC976D13B4F202C2EE9A07BFC8BA01D841082304E8AF283E883D02CA833E7EA6AC3492C8EDFCAF7BA44E3037645B24AA27A6244E478D048FA91D63DFAF6E9E26D0D062E275BDED51A365EFCCDB97D76768654AB522493B5F458B87E3D110C638A484B3141BA8620964084B089A05A0663183FFBF7C85DA793D8272381F04D8E47991DA1643C3E9C406B78E41A169E2A5EC21BDE3D47C35653EF6F87A16BA56936B7A5E3E2E39899D06FBDCDBAC537
	3D6D1924DE0E1D4FA776317DE7399DE3856F2A0971F9D5C0BDAD1F4DF63DFA3BD6CF2C3D1600FA5A76FA6CFA7EEF51AA223BBA7E8F9725FC0F74D9A76E2BF905EC7EB1C1DE572E89E507313A32391556DB94E26C045E59518FFB76D27B05F40E836C9720964084B089A05A0176BB321B44756331DF960D86DAAF63F3F2564464F2D3C0BD6D98AEF47857E3E37383870ECD12834726E37344F8B08654530ECD2ED32F5EEB81677247575E5B817534FDAEF46A75F969A06E59B7BA314FBF19A08F4F85545376F9D437
	4FB336CE2C3D598175346DCD57ED6F503609796893A16B4D5134476C66CF7B085DAC043CE40015G390390EB83F482F88751EE1E5E9DB2FE91477647933F3C3BFDE2F27DA8201ED617268E7A57E3715D95FE6728E28659FAB19EFE1387B13EBB60C59BDBB9087A766B620971792700FADABE0BDDFABAFFE484F9E8F6A18F3715CC0C07FB836AE9F918244B0371ACFE9B95A9F8B8D4BAB19ECE84545372305825D7EFCD59445AFBB3201E363DE53A6DBDB941761E8A591E64562B67A81FD8FB67836AE95B5B24E90F
	45GAFF80B24F11CA7B8A0ABC63281448FEFB25B79E33F763CE9B1345189E0F6236D7C5D06BAB3490DE7D3E268990EB8C8AF86D866C6DDEA2818D85F8F87548B103522D7AF3BF2E2BEF5E348FA1BF45B7BC5C03D71756F5781750AB469E2C54F073BDB088FEF06BC9340A8C0A440FC00C400D485FD787B1F12B36A6375612157AF67F6CC2C1F578454530EE31EC74FAFF51CC35FD86EC1DD5AD3B5B19E8E84545372E055656153E164A1CC70707284F9C85B99EA3C67FB746A4D8D28B73E761E8C28276D73925D76
	4ED7CF2C3DEB836AE97B37DC373D06DD93748F8175347D4B9C52758FB593EBEFE3C03DGBD9E525321D3EE2147C328C3BF1AA08F3F8C4903D51707E90A50E348E75FB5EE5CCAF073A11DA8F0BBDC84379C5252B062F2E95DED102E91388A8F4175C05ABB0CFD7A7167649DCC04C8A752F434C877FCFED4436BF801508CD50635B1B261B6B246D10679556DFB02FF1FD238C77F7B1452BD7A5F27D46DD94F7064F00CEA679473EB770C7D7DCA739E3D6FD75E257D7999BD4F657258950BCD264D3258C07612EF1A40
	7B37FB6CECEAA0AD3B35FB69924EB78F6FA76338E8986715677714CA01738AF9716DF0A111CE5968BA545FAF96869DCE398FF9FE26B6387C12EA756577C22D3E7C16550EAD3F9535416447FD48D0BE7CAE053E2D84D867CD48AE84E886B0830481C4814C830877229E1BE81DE5104E9AE1E76F49F9FEF2E6BE1C1F79A56FC173C75839E8D66781CF9457A1C293752910538E26EF48D314264CD20DBD184720210666A06562B91E717A2C60758DD519EC4977BD1D9E2C927547362B71FE7B976CBB60B77DECF1D7FD
	F0DD2A2F57572566FAFDDDBAD8BF36AEF5558753A5E6EF3FD688EF815F069D0488CB42F324B99733F919B64551D6A2E13CBE50C4E8686C59F574399CD6AB094FA50C4F0FBB3471B971314D20EF5DA3981F3FEDC3FF2F0CE0FC7EAFAB7187F3A03D90A0E984F5B347C6FCE68A24334E0E7F9D149E64C17DE200C600231C963FAF20A178B85D5A20BFCED18DC4D75C8E0F2BCF560E45BD8D1BC41EAFE2A51B1BC45C7C5BBFDADFB13A48B18ECD7D47E8FD16972C7E028622033D961345453F82E3E378CB3FB1AC95E3
	FB172446178B3DF86A2DB728DE395D321BEEB61295972F63C843BACD0A2321C470175594DCDEEFB4694B6B4D2660724AEB8AAD2F6DCD4165D55894DADE95CD416C03757543F435FCAA84FE059F3ECC603F1D21168D11A70BBDEA6859DEAB685768D90CC7E6D1DCB824A385EEB94545C0BA6EAC1ECFF8E0151A260F8757EAB51EFC3F4BEC633F1D5AF8EB9150CA9A43CE787913B5AAFA87FC3CBFC84F322E007A52B964B31562561174B91CE3AF6E8DBE7ED76D559F7F083D41477F263D2147FF665E6063BFFBEF68
	711F33F76C711F1F2516F12510F13CC3AB63E210C36339D0B21E32D9CD4FE70775DBDED55BD4B136731BC1EDBBC7218D2B261DC92AB42337FA2B9F3D9244477B63CB91FF553657D01F7BDD8E9BF8F9A61FFF6C8BBE262B1B7547B427B97818EEE88EBD26F94D4147F4EBF368B1ADEC5677011D5B7D7ADE0173D87646A06F5E69D73AECEEFA5C03FBC7534F615C54CA4FAC44C3FAA9C06AB91C1B5A3DFAFBB23923EC0C72C783F5047177DE099FFF1528835DA3FA74EAB81D362074927C696DC0FA7DE7756819F468
	78533B2A540F5ECE44FFDA26461796A37EA5FF3C50413B4B55FE0658G0B917EFCCAFB3E418BBC4D84089D459821E0G5F37AC9A45F1D8CF6FB7124EA7E4EF82A8BE0F6310E19FEBEF2C79FCE0FF59D914BC872157FD1E1DC5C993786D94EFD6615999157B699EA2C9AB1017F89E7D76598A352D7B66085ACAF57FF7887C3A9DFEB6883387536773AD41EDB02CC55F862734843741E9AD21ED70561660B6B82BA5348D5E57B236DFFDE8071FAE1521EE9EBD279D331A8B8959ED97C26966D0159F3DAA31CFB32C2B
	6B1C1ED707AE1F77F72A472ECA60172A7187CA84FEDE353A7CCE01BF3B4B0F2FF22183B5FE36D10A7C1A5CFA7CC6E8780C3A207577193B7D6889F95E2C68511B272137A800DE51D6B27ECE175CAF3BDC322943F21644723997093E6F23FBB0C9D0E75985547503AEF2B70455B86270A8123410167B2E8B6D79A8241DG11GF3AE32BB29186FFDF01868FC3F4BE88375208B6E28C9246EC78A564D01348120962087E052A4EC3355C36AC2C50B4DE3239CB27E5E99A27CAD03F2A9226C76DCD2F6FF879E54AD61BE
	A73BCB6DF38CD7B05E1A61377B8A0CEB9E5B3FD62D4BFEEB23CDB211737ED6E32F6FBE08B687EBCB06E519D1F138905F3B165091A79A8E01A97B681CF8C31D478E3F723CB8F903BA2FCDF659ACEE37EFDE9B6EDE274E2FF37519ADC3B24BB3377A6535F9FA2D9637191FFBB52D46BC611BEF4E72673D8C753B520F3FA62F5361D2A4F64E7576A175B9D7658A765EB2728ABC87EB727BA6E98E4FA74FACFF4559E0794C675F64C01F3F648A74799F52F5E14E1589595BGEA2EC41F7F1EEDAC1F5F89654CD77A6BBE
	736DB7D0FA23D7EA4F545EC27191D7EA4F54EE3662195A1910A77D874645C5F48DEB0234D760A6D3DCB8242384EE398D77A767885CC6876ECFAE963807CED3FA3590E3885CC2DAAEAB8C52E1285F9D19787DCFF99839D373BF4895D228BF18D79699C66A7F18263D22EE46AA1EDF4A725F266924B0EC6F4F744CCDAA491B0C38EAFA6EE19B244BA7A31DC5EB1087C667F91A2E95799B327C5BA920699EC173C587362388DC21935B8997B8096E77CE07F40C4035D05CFCC8478B5C7D941788691449289F5F746869
	07F4D57079056941829A6B65DE4574A0DB60FFC47145AABC530FF60AA769D6480B3D8AF93250777739D7035EDE0D389FD2DCB1249B854EBD02650E8A5C1291ACE7923873E7311C176028CFAB68565BEB89037C482BB10EAA6AC11B0A3D9AED2A261F703C8C52D2B858EEB84A2CE8A0E02D936E13D7A3BC9F8D57332949B4AE3306EBED6A860A9F8D575A54C69B5AD484642D8847BE3EE3C7DB693C86F95AC2C29DBCEF21FA1EE71F68DE7AF8003F87392CED30BEBB68FB44C9505F6957209C62A8AE8652F1827745
	B00E7FC201FBF29875E431401D236FF316C2BAC56012A9CE1A12101DB785F1F395AC17AFF02FFB30DCB9816ADF1635CB927A17C21781FE6D7230C1FCAC51E83149F6B2C565179697979ACAAA0A8B58597B28E175597B2AD72F543D7309C06D9426D31D7CB7754DDB79BD3B3FF5E13C9D81F98B27E0ACD15263F71F81E1243AFA1F4BE22AB78E3452480469FE996DB7291FBA85F55F68A6B44B2FCD48EE85B0DD0B5F6CFA7F0DF7954C397BCEE9F9037E9D95491D6753C72FE5FCC671DF722D40C25BF3692C4D438A
	36198479399184F7516F7B90A24BEBB3587BC7167F20DFBE0927499DFF3E7C977D72893F3FCAD3673F69D167939975717A2CEFCFEF473E25BE9DABD99F898BFA67F1F7846B53A47EBBF569ADE9D5EF1C4BE03A70E227CF97F6FCBE06AE146C2C82DDA82F625FCE5DD694647E899125B15955F591591138A4AE82474BF51C0CD75EF5200FG2D57218E3C5C9F640E929A21B1FA4F8C619DA5A6D1EF1A920C970C0478395F00FD1F195779FCDFACBCAF3CCE4F975ECE6305446B184FDBA57077D27CF2951E69F83195
	3FA9122E87FD3CDE3C4394F143A2017B6EE90C9BB65D007EF106154C2D05A2FD87CD133BF3D87A836B6572E13EB6EBAFDB9B99C25F1AEC6A3BBBD8DC51ED23F1478D627E819B519D05FDDD5D89EBE50969EDA77914152FB7B26CB8E7C6C03DA80085G49G52D430D73A2E28257109815205GEC0D7342E93146811C93E08E4002296C1EA23E776EA03A66F28CB7536F82A5DCC73D6CC01F10B8957D4DCF0E2B7D633ADB2D6938572015413229CC4FD2267A7C8DDF23E6127B52C89CD684AB740372B03DAEAD3FG
	6DFE372CEE2378DCEB506F1F7227B15AD553FCBE18357149AAF59B072CA651864B7F8B6D1B4DAE5B9CF6CB9F6B6F7BB4E6A4F7FD1A2721EC9A1C749BC89894B6A67FA0571DF29AA9EB084F5BC2BF01A932DA6CF207B2E21537087CBDD9045E16BA7B00D5B6EDE1ED2C277162FF83F82761DCB5F84CC7F323AB102FA20A7BAD318F41D70F4B1F2F69DF78E8FC4FC557037C6C354DE6341BA04D4A4513EBDA799C7E0C87676648E9E8935F9FA26B31E8C8AF82D88E306EC64C7BAF1AC76E1BA2F70519799DDFFCDFE4
	E83C3634FC1AE6EDBFADB0360D66B852B679E5ED8C0E3B0DB51A345EBD3A788EB5DB155EA0AD77784B758F5DBE39A68C933A6CAE84935A089777DD0BEF44714CBFA9899B7A5B8866570A7C4FCE51FCF6A67D844639EDB7625C714EE162DB43C99AA0CE483F2C87FD7B629B83E53694F0ABEF54736D31B4EE4E39C99B67BEC171F9B7E9635C7AFE0CF38BA1EF6ACD68471B685EE68A2433260B351E997DFD2140BDE2A538B6C81B26A36F57D21F67956999B4CD6E9863E719EDDF35AFA6ED2975E13DD479153721FE
	8F3FF0BA69FB095D4053B327870ED1AC47717D6AF301367EF50AFB1A0C578C34751F1C7357C9FB8F519F76BC4D6853355F8C232E65093AB73A7D6B4E6B756973AB6E60FE6267FD3EB6BE15FD4FFF69A74F6C8C4BBDD49F8A212D6699389775F187392B374BF8BA70848BA0712A9A0625AE190F4D9FDD214E096A5978FA699F932CD7253B0FB996B01BF906466166995A3D7767A9DE19215DFB9FF063DAE09264AD1D015F5AA41A493C5CFCB3588840A840F400F9G0BEFC6D98EF594CBA14E1AF6BA1C2D7210ECD5
	9D2DE07D6B525DD79D8B585C7B6DB598F34FFB62BEA96E2A4620F71FAD3D1969F2665778FB239759BDDD2CFFE7E85CD18EF9EDG8AC084C094C05C5730FF5F6CA8E47DA36AD027A846BEB33D6DCE9C1A324843BEB459FC655FA7F6DD0A9D47FFCDFF6FA49970A9GD9113E7CBCF8E66379BFB4FEAF0E544643FF247846C8EDBC6CBD0E7170D1481B9F09FE6D59A19C8743AD3EF64A79F3B37CF65F624F9F73218F50784489F9E137083D3BC17435B182F7AF45AD017472DB304DE86AFF25DBA15E3ED51C15E2BE39
	9D7C3440D5FEA571AF3623EFE7073E12B6B2A47DB9FC522D74BD272DF10C3AB837A134C21D9E00B7F9DA3955E7334C0FAE276B15C500DFF22B08837B68DEB4897544DE344BD6AB3135E4235CEF74D895F163D23E8AF94023504B137805CD79DB2C8E238956EEDB8A9517C7AEA8606F8D6DF836C83A8D575FC63BF67D7D115F7A7BA63BF67DCD7A604B7F1EDD3B7EFEBADD1D7F0ADD3B7E467A4C57E712A58B6F6BBD275F17022F1B499E4A0E5282EE8B2F1E4178218A7A623C8D57300975441FE95730742A176AEA
	1588BB7CEE8BE472E9BF03720939CD3CD7256FBB13A13D92E05DCC9C2BD71D249F24AD660B9E247DE0BEA7320B5CD72FBBBF0147598FEC310B52BA0D83227EEDB4D6491F09FEAB29EBCBB09A00A4E77E596DFA625C4AA0797D407481CF67BBE95A0169A697CDFBBE60F2CFF378647E3D9C62C7773769DF5E446FD9E9A43A5C2AF7871320F3E38E795BB2F53A77A9E119FB5F497515611F32285BF9F50446922035AC1DFB96537C4C490900F723B3FDFE218D1ECDB37562536BE93C6B1D29753537D0FC44CC2D2FB5
	9CC6DF9B85F919DFC7DF383F874FDE1B856EB931B7BBAAF095CE5C0F087FBA7A42C77BC9DF12C5FA81CD133B1FD83A39FFACBFC66AFD997F463FA99D330D711C258E65C3B7C85D83016FC15D8338C710827D48399D56A9G4D37333B2F587E43AF4DD82687708A40F400F9GCBEEC73B0B37862E0FDE34629EC20AA8B7FBB55157A01FCB71F551B1618F336E40FA536C047786195C6702714A300C71CA5EB7E6CBF96F5726239F54763F768EB6C656BBFC7B91BE394CBBC7EDD0C61B143C285B7C9C56GA9AB5E31
	3250157B140787085D083B1768B53E4C83706F8B483182BE46CBD54FB67765FF97F055E5ED03757FD7666067BF1619754FFF7C5E2CFFDEB845BC76597CD4F3703FAD11E1D17FED89663B5F22F7FFC641184F87C886C881480A92FF934EC2FCB93BB9C49BFFB6198648799372173894F157726960FDBDF7BA745F6F8ABBCD4EC174BA9445E1A37445F91853E43C940713A2996E0653F4DF0E6C4931229CBF0346A7D6391F2219CFB353BD68ED502F5AA874F38FD3DCA72495017BC7977A43F0017B4E9774BDA424D3
	84EE6DF17A8EF79624E7218FDCCEF16510EE9638D7A80E1C03A2F73DB05C05636355B9C20B7CB64F9A3F7FE4EDFCED5CED205F3C143F2544E470C73A8F5F8D3CF9E721DCD2E8DC9E81691982F70C4EA7B110DEA0F0AFD15C92C8AF1F057E6B7B0EC05F7B1CB8F7AE5D494E20EC025F42BB19BC4DF752FD17C183AF53F8E7201C0E5E295ED37853A97DB3A789F41D908BE5675F097C24523B8AD52761795D1C6B3F701D99A963E7A08CCE6D998A425FB22075BE2D833AA2613F95FB74F0AC3639F71024186F7ED50E
	5AF7B77E3CB1FD2B246F3BD3F99FDBE77BFCF73C4B405F6FEC20BE15717E77B3E34529D5D590271697F09DAA72BBFF978634A3E70F2D072C5E3AB3A16F89505061E7BFC25653FEF7466A9D712B37A8840FC933F18DBB194647ABE6EB77AA2E277854595A3D0A0D8E5C2B480A0639BF9AFD649DB62237EE9E53723390F45FF2B114D99A0DBA5FFB8AFDCB56DD08FB7E942ECF4B6FC27FB07DB041B5C33ADB606E20B865AE76DDB4437D7B305A46C959CB1D1F1A4728433FFD75EB63D27F5E1A5A3F688133BDB70DCB
	2783AFD1GEC4D1AE247B5EB3C8E0F5AB5EBB214C9394B37E64D3ADB3BE6BDC97DCDAE608DF723FDBF76B635E39AD81570B13EEDG4738954A055D0DEB337FA9723B5F063D1B2EF7103F5943DECD73386C45810C67665FAD56E1E6C2B38552B977A4E4EF3B874FF024D9B0062B39875734BBAD5AB5AD79BBF2C5922EC94FD846DE5376D935EB5A1F7A3DD31EE18D3626656FFD2C8457EF343AFD777B1D7473838A709AFE4F583A46E20B15EE837C4E0432F1GC9GA9GD9B1B086G65G2DB1E8C3CD8E72FDDB30
	B5A393B40DB5DCC68B7E0D28771C41E30D6D4E5031C691558999E2044E91A70FE34B1C44169D7D7DEED944B4D54EF5825762EEF338696A10496BE2973D6B56B2E4A11A6068E3FF230269DCA40DA57EB3DD124CB128F3BF6CA26F5D9CF639536169B3B7517B3559070F2C4E52AE5C23764620AD3CF5A4547E0D5A9E42215E4C981FAD444678EC0171700BAFC8FF7A9CF6889DBD30E866E7ABE87BAFFD016D4F97BCEFBBCE66CC070BDC5E296EDF4DF15475F8D136F32B4F26CAF9FB2D8E9F8E4FA3E68D87BF1F3A19
	6E53CB77023D5C0BF4DF533CE70CA2FFFE0B53F2BA691CEE74EA6674F0BA3ED5C023D150499E541F7FB753795F8965C66F453BE4D7B9519744GAE79DEF1070B937B3D69BE3471F334EC9B244DG136E433236C13471487B10073B68FE874432FC1EBF3DCEBD4FA70DF485DD23AD3E0F3F473D4FFF0D46E4726A30CFA6EB2D4476021E6328ACB414D794733D35A362CE2E72EF607E7B4AE122F3D6C807834C81D88810883082A0F38E44E1G05G0DG5DGCE00B000C800980079G0B6720FD7FFE30E08C7B6607
	FCFC86AE61BC5AC45739CB018E591B637B9894178AB8325F473EF7DB3001681345CEDEB3B7303F74ABE858695E582639B0F74E45F9F79E255189E967DC718ECEF58EED3A6A79DD4765DF07D6DE56B638EFBF9198DF2FBFA97E867BDC96DF4F045F58396C5B8D96DF0F1CB270B20B66867A7DA40EE3F634164E65ABG17BA97757D40DADF3C0CBE617DAECDECDD4A62681F9E564651040F7C7B715D30F4562763897631E25912E250712A82BEAFEF97F71B555CFF29B17956F31230313D233E765F6F9FCF7BECFCE7
	851C419C1FDEBCB041FA4BAE399E0F09E92C225C9F9093D37C247B83E2E233B84B87F9AB6EC7DBDAEDD1375F79G685283E3478C4CAEBD425EDF9176FED6587BAD99B8A7E7A841677ED71550F37F86C5FF1F61EDA5F01F61BDC5FF1FE11B225EE7E03882CE57A5BE42E678CF1435427670C6FC5423873A48EFD9C5B1CF7F358B7756AECDCFBD67FD3A19FD5CFFAF3A63B85957777B3B946214DED1D7D3FBE7CBC3DD6B290E365616CE216B1197FC74FE63F8AF382ED7D6949ACA0B8B305E1397FD75FE6D9CFF3D67
	A5DF3DFD63EA0F695AEB815F4B0E4FC65E1EE03D76CB2E47EC688FB4C698FDC0EBDBABA9BE7C812DEDDDA1620F691037728134AD175FB7255D316037E347930FFFB74DG3FE1D0B68AE0A1C092408A002C87933273G4A9F44797A7FF946BA27A0D4C7B5E373EF9DF5E55376A0634F74E0A01F7C1BD3EFF0FB5E608DED4FF9DEFDFB5E628D34678F3C7A767C31B7501ECB3DE44E9800786428A837C3600E89DC8D25C9F0C7ACA645AC707514AF024743037CBE884A57B0459D653883AA5CB10EEB7712B99B3AECF6
	38306E913A775EEF31DAD97AB8ADE3314372C448230772E8F19AD5FC4B82C778E66BDA633FF3A431B7CFD9E778BC6FDA9FDE1CFBE436B9798CBF8FF94068F4D2BF46E2092F1F41D8428B63BC95A09EA079C1757E5B53F67ADD237A8D26A456BEF6DCFB2CFC90E3701DA7451E4BC38959358FE1DCFD45C90C413D8FE18C7EGAD9B8B69C5G49226C2CD3B8772FFCC87C7D2DD32B053E207F09A5D91C4F3F3591DB89B6376FA8A9B41495E37C1C550E740B9FC69B7D506F1C319970238F0FE76F6CDA22BA98032A1E
	5FB57F9F442017358DB6173FEB41797D03B444ED310879DD7072A7272F6FFFD7BDDF6D3C7CF2083A2CED30BEDFB308F2F8CE48617AC114436369E833BFCE8B6E971FC98B6D971FCF53770BCB53827D62EF52747D629F52827DE2CA9A7B1BA383E08F6AB867AFE9FAF18E7FA60A2EB327029DC4BD0C6B30C427780E6EE134796F1DC11BCF85DC79BC5C037B7126B14EC7E04F192FE2DB9A8D46BEB37F86E12D954F11DA67213F58C03F7B0E0674FC00A573445FAF321336CB04BE6CB9B931B86140846BDDF54975D8
	9C3175947D96E11EB63EA824781573347145A34237E6BE827179A3683B6EBE25EEBF9E70490F0C47F72DED5763BB05574DFAB410865301BA875AC21A406DF520AD78762D26E87600A46242799927BFB8F10FACCF279D6D9ED9B114A170A647B7BE6A138D6B4B53A15FC5684B6097932C77CFAB29572643BB0289723C2AFCB636D7D39D0EF8D4BB66D3A9BE72D16D183FAD6EBA08063C2C47F04C8B6819EB76DC2AFAFE7264657737CBAEEB9BCC1F7E73946A58EE61EF3FF38AF56C17225CB6BA17DBC1AE230F210F
	3A26DD3C93FE8CE38A035857CBFCCCEFBF81E38A3A9D4AE997D09F34AEAEA1FBDB9C523E02624AA15DAAF0CFD3DC8F24C763F0FC2E337B4664E655737B7F875FCBD6DD56B618ED6FEA77376D3DD4144C36235A51362362C25B76BCA8C3GEDFBD11C5636377B2D1F16C07EB29D3A01BC56DA833FD5BAC23F4BCA0D4339A352CA7617DD8E0752E5F1DB4846BE0BEDE32D985B36BE0EE5DF30925A5A3F7D4BE2693F0A3F95537DB87BAE09BCAB0F2B3F2B7F2908BBA39E9FCB87AD59BE9D2C998C7435F503013E56E6
	A57778CE7C6CA6FD03D4D0A0687D400A7B914C8FED35219FDA883C27BE0EF3F28F4517BF8176GE0FA82757FE7FEF34BA86027BEB11EB929E04C7B8402575BF14975185FDDC2790FFEC26B0F1F237858A7347E38DF48E1BE64991EC49F703B7640F1FA29DDBDCE7C7CB4B942481FDFD4BDDB3F72EFB982E5B479323631116ACD7145D9908B4F791D34CA115633BF23497F1AA5790B4EEE17744F4075D95D753F78791CD30D3969D2FA0B345ACC834296E955B0D95DFA49DA62E600C56EC169825BDC81783EA2ED6B
	35B82C0E81CB1F51EA20B66EB6389507CBB699ACF6837E0DBCC5DA4D2E76D024CCF27F07A23D6A3B4EC313F81D1DB8B57053240A74CFFEF494B1C5C5C50A348E116C6F569254EAF6BB87646131D69F0DB57CF6903FA7FEAB086FC9742D994024C8B98D8E9B39E352F0442218C1E8EF88213550DD036421326A0C97E7E724CB19A0B136DAE811B2C9488F02F3B181E640EA010AAFD38512E8EABF51E1C5FA4DE040130B551817769DE26722A409626DB4159B14DA70B219228CBD0DBB1E947D9BC85FG85C24E8C78
	878875DBD02437BB78DFAC971DB1F4F8ECB623EBC43F06A47EB65DC5753719ABA777857DBEBE791B7C7B4FEF7A5E6531FD3A7569747B7FEF018F7C966E479953485F8B8FF6AE3B43E634DA952375CC1D5DC44EE8570FA8326FCC60DB69F41E41B2BC13653DCB6FC1BC2D2A733FD0CB8788D1430BB2A3AFGGE418GGD0CB818294G94G88G88GB9FBB0B6D1430BB2A3AFGGE418GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGDD
	AFGGGG
**end of data**/
}
}