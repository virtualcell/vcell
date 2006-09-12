package cbit.vcell.desktop;
import java.awt.Insets;
import cbit.xml.merge.NodeInfo;
import cbit.vcell.desktop.controls.ClientTask;
import java.util.Vector;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.mathmodel.*;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.solver.*;
import cbit.vcell.mapping.*;
import cbit.vcell.server.*;
import cbit.util.DataAccessException;
import cbit.util.GroupAccessAll;
import cbit.util.Matchable;
import cbit.util.User;
import cbit.util.Version;
import cbit.util.VersionFlag;
import cbit.util.VersionInfo;

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
public class MathModelDbTreePanel extends JPanel {
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
	private MathModelDbTreeModel ivjMathModelDbTreeModel = null;
	private JPopupMenu ivjMathModelPopupMenu = null;
	private JPanel ivjJPanel1 = null;
	private MathModelMetaDataPanel ivjMathModelMetaDataPanel = null;
	private JLabel ivjJLabel2 = null;
	private JPanel ivjJPanel2 = null;
	private JScrollPane ivjJScrollPane2 = null;
	private JSplitPane ivjJSplitPane1 = null;
	private JLabel ivjJLabel3 = null;
	private JPanel ivjJPanel3 = null;
	private JMenuItem ivjJMenuItemPermission = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JMenuItem ivjJMenuItemExport = null;
	private JSeparator ivjJSeparator3 = null;
	private JMenu ivjJMenuCompare = null;
	private JMenuItem ivjJMenuItemAnother = null;
	private JMenuItem ivjJAnotherEditionMenuItem = null;
	private JMenuItem ivjJLatestEditionMenuItem = null;
	private JMenuItem ivjJPreviousEditionMenuItem = null;
	private boolean fieldPopupMenuDisabled = false;
	private JMenuItem ivjJMenuItemArchive = null;
	private JMenuItem ivjJMenuItemPublish = null;

class IvjEventHandler implements cbit.vcell.clientdb.DatabaseListener, java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.TreeModelListener, javax.swing.event.TreeSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == MathModelDbTreePanel.this.getJMenuItemDelete()) 
				connEtoC7(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJMenuItemOpen()) 
				connEtoC6(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJMenuItemPermission()) 
				connEtoC10(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJMenuItemExport()) 
				connEtoC9(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJMenuItemAnother()) 
				connEtoC15(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJPreviousEditionMenuItem()) 
				connEtoC16(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJLatestEditionMenuItem()) 
				connEtoC17(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJAnotherEditionMenuItem()) 
				connEtoC18(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJMenuItemArchive()) 
				connEtoC22(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJMenuItemPublish()) 
				connEtoC23(e);
		};
		public void databaseDelete(cbit.vcell.clientdb.DatabaseEvent event) {
			if (event.getSource() == MathModelDbTreePanel.this.getDocumentManager()) 
				connEtoC14(event);
		};
		public void databaseInsert(cbit.vcell.clientdb.DatabaseEvent event) {};
		public void databaseRefresh(cbit.vcell.clientdb.DatabaseEvent event) {
			if (event.getSource() == MathModelDbTreePanel.this.getDocumentManager()) 
				connEtoC13(event);
		};
		public void databaseUpdate(cbit.vcell.clientdb.DatabaseEvent event) {
			if (event.getSource() == MathModelDbTreePanel.this.getDocumentManager()) 
				connEtoC3(event);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == MathModelDbTreePanel.this.getJTree1()) 
				connEtoC5(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == MathModelDbTreePanel.this && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == MathModelDbTreePanel.this.getJTree1() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == MathModelDbTreePanel.this && (evt.getPropertyName().equals("selectedVersionInfo"))) 
				connPtoP5SetTarget();
			if (evt.getSource() == MathModelDbTreePanel.this.getMathModelDbTreeModel() && (evt.getPropertyName().equals("latestOnly"))) 
				connEtoC4(evt);
			if (evt.getSource() == MathModelDbTreePanel.this.getMathModelDbTreeModel() && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP3SetSource();
			if (evt.getSource() == MathModelDbTreePanel.this && (evt.getPropertyName().equals("documentManager"))) 
				connEtoM5(evt);
		};
		public void treeNodesChanged(javax.swing.event.TreeModelEvent e) {
			if (e.getSource() == MathModelDbTreePanel.this.getMathModelDbTreeModel()) 
				connEtoC11(e);
		};
		public void treeNodesInserted(javax.swing.event.TreeModelEvent e) {};
		public void treeNodesRemoved(javax.swing.event.TreeModelEvent e) {};
		public void treeStructureChanged(javax.swing.event.TreeModelEvent e) {};
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == MathModelDbTreePanel.this.getselectionModel1()) 
				connEtoC1();
		};
	};

/**
 * BioModelTreePanel constructor comment.
 */
public MathModelDbTreePanel() {
	super();
	initialize();
}

/**
 * BioModelTreePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public MathModelDbTreePanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * BioModelTreePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public MathModelDbTreePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * BioModelTreePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public MathModelDbTreePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * Comment
 */
private void actionsOnClick(MouseEvent mouseEvent) {
	if (mouseEvent.getClickCount() == 2 && getSelectedVersionInfo() instanceof MathModelInfo) {
		fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, cbit.vcell.client.DatabaseWindowManager.BM_MM_GM_DOUBLE_CLICK_ACTION));
		return;
	}	
	if (SwingUtilities.isRightMouseButton(mouseEvent) && getSelectedVersionInfo() instanceof MathModelInfo && (! getPopupMenuDisabled())) {
		Version version = getSelectedVersionInfo().getVersion();
		boolean isOwner = version.getOwner().compareEqual(getDocumentManager().getUser());
		configureArhivePublishMenuState(version,isOwner);
		getJMenuItemPermission().setEnabled(isOwner && !version.getFlag().compareEqual(VersionFlag.Published));
		getJMenuItemDelete().setEnabled(isOwner &&
			!version.getFlag().compareEqual(VersionFlag.Archived) &&
			!version.getFlag().compareEqual(VersionFlag.Published));
		getMathModelPopupMenu().show(getJTree1(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
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
	if (vInfo!=null){
		MathModelInfo thisMathModelInfo = (MathModelInfo)vInfo;

		//
		// Get the other versions of the Mathmodel
		//
		MathModelInfo mathModelVersionsList[] = getMathModelVersionDates(thisMathModelInfo);
		
		if (mathModelVersionsList == null || mathModelVersionsList.length == 0) {
			bAnotherEditionMenuItem = false;
		} else {
			bAnotherEditionMenuItem = true;
		}
	}
	getJAnotherEditionMenuItem().setEnabled(bAnotherEditionMenuItem);
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
 * connEtoC10:  (JMenuItemPublish.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelDbTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
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
 * connEtoC11:  (MathModelDbTreeModel.treeModel.treeNodesChanged(javax.swing.event.TreeModelEvent) --> MathModelDbTreePanel.TreeSelectionEvents()V)
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
 * connEtoC12:  (MathModelDbTreePanel.initialize() --> MathModelDbTreePanel.enableToolTips(Ljavax.swing.JTree;)V)
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
 * connEtoC13:  (DocumentManager.database.databaseRefresh(cbit.vcell.clientdb.DatabaseEvent) --> MathModelDbTreePanel.refresh()V)
 * @param arg1 cbit.vcell.clientdb.DatabaseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(cbit.vcell.clientdb.DatabaseEvent arg1) {
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
 * connEtoC14:  (DocumentManager.database.databaseDelete(cbit.vcell.clientdb.DatabaseEvent) --> MathModelDbTreePanel.documentManager_DatabaseDelete(Lcbit.vcell.clientdb.DatabaseEvent;)V)
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
 * connEtoC15:  (JMenuItemAnother.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelDbTreePanel.compareWithAnother(Ljava.awt.event.ActionEvent;)V)
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
 * connEtoC16:  (JPreviousEditionMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelDbTreePanel.comparePreviousEdition()V)
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
 * connEtoC17:  (JLatestEditionMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelDbTreePanel.compareLatestEdition()V)
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
 * connEtoC18:  (JAnotherEditionMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelDbTreePanel.compareAnotherEdition()V)
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
 * connEtoC19:  (selectedVersionInfo1.this --> MathModelDbTreePanel.latestEditionMenuItemEnable(LVersionInfo;)V)
 * @param value VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(VersionInfo value) {
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
 * connEtoC20:  (selectedVersionInfo1.this --> MathModelDbTreePanel.previousEditionMenuItemEnable(LVersionInfo;)V)
 * @param value VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(VersionInfo value) {
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
 * connEtoC21:  (selectedVersionInfo1.this --> MathModelDbTreePanel.anotherEditionMenuItemEnable(LVersionInfo;)V)
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
 * connEtoC22:  (JMenuItemArchive.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelDbTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
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
 * connEtoC23:  (JMenuItemPublish.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelDbTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
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
 * connEtoC3:  (DocumentManager.database.databaseUpdate(cbit.vcell.clientdb.DatabaseEvent) --> MathModelDbTreePanel.documentManager_DatabaseUpdate(Lcbit.vcell.clientdb.DatabaseEvent;)V)
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
 * connEtoC8:  (MathModelDbTreePanel.initialize() --> MathModelDbTreePanel.splitPaneResizeWeight()V)
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
 * connEtoC9:  (JMenuItemExport.action.actionPerformed(java.awt.event.ActionEvent) --> MathModelDbTreePanel.exportData()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
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
 * connEtoM1:  (selectedVersionInfo1.this --> MathModelMetaDataPanel.mathModelInfo)
 * @param value VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(VersionInfo value) {
	try {
		// user code begin {1}
		// user code end
		getMathModelMetaDataPanel().setMathModelInfo((cbit.vcell.mathmodel.MathModelInfo)getselectedVersionInfo1());
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
 * connEtoM5:  (MathModelDbTreePanel.documentManager --> MathModelMetaDataPanel.documentManager)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getMathModelMetaDataPanel().setDocumentManager(this.getDocumentManager());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM7:  (DocumentManager.this --> JTree1.model)
 * @param value cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(cbit.vcell.clientdb.DocumentManager value) {
	try {
		// user code begin {1}
		// user code end
		getJTree1().setCellRenderer(this.getMathModelCellRenderer());
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
		getJTree1().setModel(getMathModelDbTreeModel());
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
			setDocumentManager(getMathModelDbTreeModel().getDocumentManager());
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
				getMathModelDbTreeModel().setDocumentManager(getDocumentManager());
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
	if (event.getOldVersionInfo() instanceof MathModelInfo && getSelectedVersionInfo() instanceof MathModelInfo) {
		MathModelInfo selectedMMInfo = (MathModelInfo)getSelectedVersionInfo();
		MathModelInfo eventMMInfo = (MathModelInfo)event.getOldVersionInfo();
		if (eventMMInfo.getVersion().getVersionKey().equals(selectedMMInfo.getVersion().getVersionKey())){
			setSelectedVersionInfo(null);
			getJTree1().getSelectionModel().clearSelection();
		}		
	}
}


/**
 * Comment
 */
private void documentManager_DatabaseUpdate(cbit.vcell.clientdb.DatabaseEvent event) {
	if (event.getNewVersionInfo() instanceof MathModelInfo && getSelectedVersionInfo() instanceof MathModelInfo) {
		MathModelInfo selectedMMInfo = (MathModelInfo)getSelectedVersionInfo();
		MathModelInfo eventMMInfo = (MathModelInfo)event.getNewVersionInfo();
		if (eventMMInfo.getVersion().getVersionKey().equals(selectedMMInfo.getVersion().getVersionKey())){
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
	BioModelNode rootNode = (BioModelNode)getMathModelDbTreeModel().getRoot();
	BioModelNode currentUserNode = (BioModelNode)rootNode.findNodeByUserObject(currentUser);
	if (currentUserNode!=null){
		getJTree1().expandPath(new TreePath(getMathModelDbTreeModel().getPathToRoot(currentUserNode)));
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
 * Insert the method's description here.
 * Creation date: (10/7/2002 3:06:29 PM)
 */
private BioModelInfo[] getBioModelVersionDates(BioModelInfo thisBioModelInfo) throws DataAccessException {
	//
	// Get list of BioModelInfos in workspace
	//

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
 * Return the JAnotherEditionMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJAnotherEditionMenuItem() {
	if (ivjJAnotherEditionMenuItem == null) {
		try {
			ivjJAnotherEditionMenuItem = new javax.swing.JMenuItem();
			ivjJAnotherEditionMenuItem.setName("JAnotherEditionMenuItem");
			ivjJAnotherEditionMenuItem.setMnemonic('E');
			ivjJAnotherEditionMenuItem.setText("Another Edition...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJAnotherEditionMenuItem;
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
			ivjJLabel1.setText("MathModel:");
			ivjJLabel1.setMaximumSize(new java.awt.Dimension(65, 20));
			ivjJLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			ivjJLabel1.setPreferredSize(new java.awt.Dimension(65, 20));
			ivjJLabel1.setMinimumSize(new java.awt.Dimension(65, 20));
			ivjJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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
			ivjJLabel2.setText("Selected MathModel Summary");
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
			ivjJLabel3.setText("MathModel Database");
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
 * Return the JLatestEditionMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJLatestEditionMenuItem() {
	if (ivjJLatestEditionMenuItem == null) {
		try {
			ivjJLatestEditionMenuItem = new javax.swing.JMenuItem();
			ivjJLatestEditionMenuItem.setName("JLatestEditionMenuItem");
			ivjJLatestEditionMenuItem.setMnemonic('L');
			ivjJLatestEditionMenuItem.setText("Latest Edition");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLatestEditionMenuItem;
}


/**
 * Return the JMenuCompare property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuCompare() {
	if (ivjJMenuCompare == null) {
		try {
			ivjJMenuCompare = new javax.swing.JMenu();
			ivjJMenuCompare.setName("JMenuCompare");
			ivjJMenuCompare.setMnemonic('C');
			ivjJMenuCompare.setText("Compare with..");
			ivjJMenuCompare.add(getJPreviousEditionMenuItem());
			ivjJMenuCompare.add(getJLatestEditionMenuItem());
			ivjJMenuCompare.add(getJAnotherEditionMenuItem());
			ivjJMenuCompare.add(getJMenuItemAnother());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuCompare;
}

/**
 * Return the JMenuItemAnother property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemAnother() {
	if (ivjJMenuItemAnother == null) {
		try {
			ivjJMenuItemAnother = new javax.swing.JMenuItem();
			ivjJMenuItemAnother.setName("JMenuItemAnother");
			ivjJMenuItemAnother.setMnemonic('A');
			ivjJMenuItemAnother.setText("Another Model...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemAnother;
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
 * Return the JMenuItemPublish property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPermission() {
	if (ivjJMenuItemPermission == null) {
		try {
			ivjJMenuItemPermission = new javax.swing.JMenuItem();
			ivjJMenuItemPermission.setName("JMenuItemPermission");
			ivjJMenuItemPermission.setMnemonic('p');
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
			getJPanel1().add(getMathModelMetaDataPanel(), "Center");
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
			ivjJPanel2.setMinimumSize(new java.awt.Dimension(181, 300));

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
			ivjJPanel3.setMinimumSize(new java.awt.Dimension(127, 450));

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 0;
			constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJLabel3(), constraintsJLabel3);

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(0, 4, 4, 4);
			getJPanel3().add(getJScrollPane1(), constraintsJScrollPane1);
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
 * Return the JPreviousEditionMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJPreviousEditionMenuItem() {
	if (ivjJPreviousEditionMenuItem == null) {
		try {
			ivjJPreviousEditionMenuItem = new javax.swing.JMenuItem();
			ivjJPreviousEditionMenuItem.setName("JPreviousEditionMenuItem");
			ivjJPreviousEditionMenuItem.setMnemonic('P');
			ivjJPreviousEditionMenuItem.setText("Previous Edition");
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
			getJSplitPane1().add(getJPanel3(), "top");
			getJSplitPane1().add(getJPanel2(), "bottom");
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
			ivjJTree1.setBounds(0, 0, 357, 405);
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
 * Method generated to support the promotion of the latestVersionOnly attribute.
 * @return boolean
 */
public boolean getLatestVersionOnly() {
	return getMathModelDbTreeModel().getLatestOnly();
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
 * Comment
 */
public javax.swing.tree.TreeCellRenderer getMathModelCellRenderer() {
	User sessionUser = (getDocumentManager()!=null)?(getDocumentManager().getUser()):(null);
	return new MathModelCellRenderer(sessionUser);
}


/**
 * Return the MathModelDbTreeModel property value.
 * @return cbit.vcell.desktop.MathModelDbTreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathModelDbTreeModel getMathModelDbTreeModel() {
	if (ivjMathModelDbTreeModel == null) {
		try {
			ivjMathModelDbTreeModel = new cbit.vcell.desktop.MathModelDbTreeModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathModelDbTreeModel;
}


/**
 * Return the MathModelMetaDataPanel property value.
 * @return cbit.vcell.desktop.MathModelMetaDataPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathModelMetaDataPanel getMathModelMetaDataPanel() {
	if (ivjMathModelMetaDataPanel == null) {
		try {
			ivjMathModelMetaDataPanel = new cbit.vcell.desktop.MathModelMetaDataPanel();
			ivjMathModelMetaDataPanel.setName("MathModelMetaDataPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathModelMetaDataPanel;
}


/**
 * Return the JPopupMenu1 property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getMathModelPopupMenu() {
	if (ivjMathModelPopupMenu == null) {
		try {
			ivjMathModelPopupMenu = new javax.swing.JPopupMenu();
			ivjMathModelPopupMenu.setName("MathModelPopupMenu");
			ivjMathModelPopupMenu.add(getJLabel1());
			ivjMathModelPopupMenu.add(getJSeparator1());
			ivjMathModelPopupMenu.add(getJMenuItemOpen());
			ivjMathModelPopupMenu.add(getJMenuItemDelete());
			ivjMathModelPopupMenu.add(getJMenuItemPermission());
			ivjMathModelPopupMenu.add(getJMenuItemArchive());
			ivjMathModelPopupMenu.add(getJMenuItemPublish());
			ivjMathModelPopupMenu.add(getJMenuCompare());
			ivjMathModelPopupMenu.add(getJSeparator3());
			ivjMathModelPopupMenu.add(getJMenuItemExport());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathModelPopupMenu;
}

/**
 * Insert the method's description here.
 * Creation date: (10/7/2002 3:06:29 PM)
 */
private MathModelInfo[] getMathModelVersionDates(MathModelInfo thisMathModelInfo) throws DataAccessException {
	//
	// Get list of MathModelInfos in workspace
	//

	MathModelInfo mathModelInfos[] = getDocumentManager().getMathModelInfos();

	//
	// From the list of mathmodels in the workspace, get list of mathmodels with the same branch ID.
	// This is the list of different versions of the same mathmodel.
	//
 	Vector mathModelBranchList = new Vector();
 	for (int i = 0; i < mathModelInfos.length; i++) {
	 	MathModelInfo mathModelInfo = mathModelInfos[i];
	 	if (mathModelInfo.getVersion().getBranchID().equals(thisMathModelInfo.getVersion().getBranchID())) {
		 	mathModelBranchList.add(mathModelInfo);
	 	}
 	}

 	if (mathModelBranchList == null) {
		JOptionPane.showMessageDialog(this,"No Versions in Mathmodel","Error comparing MathModels",JOptionPane.ERROR_MESSAGE);
	 	throw new NullPointerException("No Versions in Mathmodel!");
 	}

 	MathModelInfo mathModelInfosInBranch[] = new MathModelInfo[mathModelBranchList.size()];
 	mathModelBranchList.copyInto(mathModelInfosInBranch);

 	//
 	// From the versions list, remove the currently selected version and return the remaining list of
 	// versions for the mathmodel
 	//

 	MathModelInfo revisedMMInfosInBranch[] = new MathModelInfo[mathModelInfosInBranch.length-1];
 	int j=0;
 	
 	for (int i = 0; i < mathModelInfosInBranch.length; i++) {
		if (!thisMathModelInfo.getVersion().getDate().equals(mathModelInfosInBranch[i].getVersion().getDate())) {
			revisedMMInfosInBranch[j] = mathModelInfosInBranch[i];
			j++;
		}
 	}
			 	
	return revisedMMInfosInBranch;	
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
	getJTree1().addMouseListener(ivjEventHandler);
	getJMenuItemDelete().addActionListener(ivjEventHandler);
	getJMenuItemOpen().addActionListener(ivjEventHandler);
	getMathModelDbTreeModel().addPropertyChangeListener(ivjEventHandler);
	getJMenuItemPermission().addActionListener(ivjEventHandler);
	getMathModelDbTreeModel().addTreeModelListener(ivjEventHandler);
	getJMenuItemExport().addActionListener(ivjEventHandler);
	getJMenuItemAnother().addActionListener(ivjEventHandler);
	getJPreviousEditionMenuItem().addActionListener(ivjEventHandler);
	getJLatestEditionMenuItem().addActionListener(ivjEventHandler);
	getJAnotherEditionMenuItem().addActionListener(ivjEventHandler);
	getJMenuItemArchive().addActionListener(ivjEventHandler);
	getJMenuItemPublish().addActionListener(ivjEventHandler);
	connPtoP2SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
	connPtoP3SetTarget();
	connPtoP1SetTarget();
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
		setLayout(new java.awt.GridBagLayout());
		setSize(240, 453);

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

/**
 * Comment
 */
private void latestEditionMenuItemEnable(VersionInfo vInfo) throws DataAccessException {

	boolean bLatestEditionMenuItem = false;

	if (vInfo!=null){
		MathModelInfo thisMathModelInfo = (MathModelInfo)vInfo;

		//
		// Get the other versions of the Mathmodel
		//
		MathModelInfo mathModelVersionsList[] = getMathModelVersionDates(thisMathModelInfo);
		
		if (mathModelVersionsList == null || mathModelVersionsList.length == 0) {
			bLatestEditionMenuItem = false;
		} else {
			//
			// Obtaining the latest version of the current mathmodel
			//
			
			MathModelInfo latestMathModelInfo = mathModelVersionsList[mathModelVersionsList.length-1];

			for (int i = 0; i < mathModelVersionsList.length; i++) {
				if (mathModelVersionsList[i].getVersion().getDate().after(latestMathModelInfo.getVersion().getDate())) {
					latestMathModelInfo = mathModelVersionsList[i];
				}
			}

			if (thisMathModelInfo.getVersion().getDate().after(latestMathModelInfo.getVersion().getDate())) {
				bLatestEditionMenuItem = false;
			} else {
				bLatestEditionMenuItem = true;
			}
		}
	}	
	getJLatestEditionMenuItem().setEnabled(bLatestEditionMenuItem);
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
	if (vInfo!=null){
		MathModelInfo thisMathModelInfo = (MathModelInfo)vInfo;

		//
		// Get the other versions of the Mathmodel
		//
		MathModelInfo mathModelVersionsList[] = getMathModelVersionDates(thisMathModelInfo);
		
		if (mathModelVersionsList == null || mathModelVersionsList.length == 0) {
			bPreviousEditionMenuItem = false;
		} else {
			//
			// Obtaining the previous version of the current biomodel
			//
		
			MathModelInfo previousMathModelInfo = mathModelVersionsList[0];
			boolean bPrevious = false;

			for (int i = 0; i < mathModelVersionsList.length; i++) {
				if (mathModelVersionsList[i].getVersion().getDate().before(thisMathModelInfo.getVersion().getDate())) {
					bPrevious = true;
					previousMathModelInfo = mathModelVersionsList[i];
				} else {
					break;
				}
			}

			if (previousMathModelInfo.equals(mathModelVersionsList[0]) && !bPrevious) {
				bPreviousEditionMenuItem = false;
			} else {
				bPreviousEditionMenuItem = true;
			}
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
private void refresh() throws DataAccessException{
	//getMathModelDbTreeModel().reload();
	getMathModelDbTreeModel().refreshTree();
	expandTreeToOwner();
}


public void removeActionListener(ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.remove(aActionListener, newListener);
	return;
}


/**
 * This method places the divider location programatically. Accepts values from 0 to 1.
 * Creation date: (7/17/2002 4:26:44 PM)
 * @param position double
 */
public void setDividerPosition(double position) {
	ivjJSplitPane1.setDividerLocation(position);
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
	getMathModelDbTreeModel().setLatestOnly(arg1);
}


/**
 * Sets the popupMenuDisabled property (boolean) value.
 * @param popupMenuDisabled The new value for the property.
 * @see #getPopupMenuDisabled
 */
public void setMetadataPanelPopupDisabled(boolean popupMenuDisabled) {
	getMathModelMetaDataPanel().setPopupMenuDisabled(popupMenuDisabled);
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
	if (object instanceof VersionInfo){
		setSelectedVersionInfo((VersionInfo)object);
	}else if (object instanceof String && bioModelNode.getChildCount()>0 && ((BioModelNode)bioModelNode.getChildAt(0)).getUserObject() instanceof MathModelInfo){
		MathModelInfo mathModelInfo = (MathModelInfo)((BioModelNode)bioModelNode.getChildAt(0)).getUserObject();
		setSelectedVersionInfo(mathModelInfo);
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
	D0CB838494G88G88G390171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8FDCD4D576B8DB24E4D414D4D45612D151C645FE4322E2F73158E20B4CED29583EECE951E6E5AE2DD4D6E48F4432A0432222EF6E0ACA09650F2F824ADF93819395F40489B0B1991887CC8EB3534C2018183FF37F1CBBEF5E3CE1E0DAFF1F9F1F4F613DFB6E39671CFB6F39671EFB5FFDEF2402D561CB33328BA4A9EB29447FAA33A4296B92C9CA6EDFBD97F1A1160EDCC971379AE00554581B8357F0
	484BDF5A191BAFB557AC03F48C24D332BBF3BF017B8F25561A42D03871A2A7FD2AA47D6A22CDC7461633F68F4AF91B26DBD76541F5B9C0AC6048B5478ABAC8722AC20ECF65F8042CECC91AC3E982D62D6438E6C877GCC830836B27AC4383EC3A51FD5D345693AAF0814B26E33E73FC778A8F8A21458D85996FEAD12DE4B4A3091D957D3BDC919C0C84781104AAF11927F677F4035CEF7E4606B3EDE133374E81FECB6179AE547912755D6D353EAF08E36DA0D323929D7EF17E51D41A21B015E76A36179622AE514
	07A4D5C1BA136292AB091C9838CF82C89378A97584DFEF6F486D82305931EEDB5755F15D3BCE5CA695BA7E784E7BDE6A96E4E7F50A363B6AE67E2AF74927349F7ADAC8FB4F073CFCC7C7EEB1C0B340A0C09040E7347FFF5873A5DC3777BA9B2DC3C3D64BF61B2D41EC34536A41FDCD8D64F0DC1B5CE7359BA50975714D2BB6E868B39630F23F1BF0B956A61165585FD1EDB7C92937DC1C7D36C61B24C61E488EF7303608F5385A0449FE592B6C94073BBD3A4B6E14143225E74B5EDE2AA1FB65A3E5EF4F67FCA427
	CBF6131374F5FD95696BA238A765082DB27CBE0A8FD26019ED4E22B6C352D110D7B80C3651359CF5E94919AE65EF3AC4339D426E8A4E8E9CE6BACC9BD66B5256C0F8A78EB319BB04AEA7A93E7B288B4FF48921F6CA5269103772F8C7AE33AF73DEB2D61BCFF4649A81C681C281E2GE683241DC09B3BE54F7F0E5346F4863B33E158E4B64AF60915B537FC8CD7F944E9B76C32B81C86CB1F5CE1301B8C3DE659A1F11A619652863DEE65FE22365F87B2F64A9659EEF01A2C9660EE98121D325D41E84EECD9C1E86C
	B24D551B06E490B0E413106FB9AD9F4255ECF0B83F35998DCE59C54172273610BEB9EA68G0E40G6ED93BDC3A0F34D74EA834954006D1EC07652D045F9E598E8E23262645EABDB2EC53131AC82323241D5FABA23E239E6E3BC6310DAF5291DCBC24D3841F9D2D9FA878B4197A087A867BF19CE35FD451F18F74F9A7BBF2CBG1AG0CG01A7510E96F4219DB53DF7233461610BB46DA884ECBA62A4331F38136A7174AD15C3F4CD033C1C1F4147GB4818C7E0CF3C1F2E50F4638EA62FAADD768097ACD44B7387B
	16FFE84AFA076713FB621749584D65E34B9FD92CDF163451F18B750CGC878997BA620350859FA0341A123F56F3303E14AC6D7CFB1FD3B9A0238CE7F82D8A53179349C201A58BCGF42934096B3C1E774FD7GE4FEBA883099209D60G00956090318B00E1E257G6782FC8BE082B889C0A67299G35GDB813691DD819AC59B1EC9F5CA057A641C825B8550830C82C4GA481E4G2C8C00FE86B083048244812481647D2AA33782209BA090A08AA099A09320780C0E5C96GA740B400840079G85E7F664568198
	810281E2GD281F2825106AE2EAF4AE6F5477E1BC8FFA95BD609DF2FE84B653C1D4BF91BAE67FD2055CECA9EFBD469F65E5E4BF91F9C64FD0279FD3CDF1773BEB9426F2D8A1AE1FEBD4E2FA7FD546FDCBF6D589B4C788F737BFF854CD6D6EFA736C246A831150FA648E3A967730E8ACFE2E5B23E7F4553440F939F02B6C5FC095ACEF628F8FC25CA2F15183D117B4FA5E6236B9579E44EFC1F5F931B2C66776B94B4E83BAB783552C77DB67D075B3C767F03FE7787182FBE3A17342FFCD436B81BE41BECB1420D03
	671D5CCBEC00479D7DD6D166CC0A37C11C52880101C3604F5ECB7A4060F45ACD3D43CE39DDF6BACD1681D77E85D416E33857668AF1D0D658DEE2F756E39027887AAB7612B93E4FEA3148F45290C5F87E2FE90CA11B65A1D03A51BAECF1B27CB57B48DA6C08FC1C47B8576FFBCBA2B10AF91844CBECFD93EDA036EB331B06E0AAA2A117B49AC8620F6222C2C59BEDG9CD9276960EA8C44386158DA0C9B0AFF3ADE0ABFEC4B560A05432EB9BFBBA1104D65A9012E380169F419CEDB77F09A6BF03C03693A73B0E957
	861315060B4A755692DA66275524DD1B2CFD4324D02B41E29800D6E4F94355344D01A1EDC24AE1934FBB2F3BC06449C69E41111E5E44720FFDCD5ADFAC75F4D65B302DD5368C235C972A8A1479ECA9C8EFD9FCD057CE6A38886A1EF7964CFDE7E1FC30F2BF4947580D87860DD6B35791720CE7113E380272A76DD687E97DD92CAF160B7C06EA4C0F02F402C77E9C111F8C69790AFC565713EA14FD1D2948AF1E5411BBB20950FCDFDF2020291B6422E1F5F927AD1F36610F43B20465A4105DA36CAE3F6D83D5DE87
	37C116DF58C6EC502C0801F3A693198FB7607ABF1E4BCB1944624B8C387E9B700C6E570DC8270374A0C050E49603C6CC66F4C1044E5404F4B1BCBFE9B263BBEFB25F1B207C0AF788FEC1ACFFB00845336E7C1E93F4C1C1CCEEB827CF8D627CA85D0D5FA0DD415990C781341C4D68024FE6F47AA9046E471DC8174473534EE6FCB361DA8FB40C5FAB4DC8978E38D80014A96E7C985D1D3B102E709C9607EFBC0751B543B59C0055777ABD22DD4EE16D1B4469D34FD15677279624DB44697203993F22E0E57B1535A2
	DDFDB06353954C6AB1982C6417359769C6819F8A90932CD50FBF8A3A8C4EAF6FDCD53FD13AF376A11D1E671B79B570DC3E3EB817506D50097A1E4B625424F319FE29F0AD957CBE5C0FF4D567016F83301D27255F2302AE9A72938166E952DD5E06F4A567C3FF82741C2FC5F738DD74AF6447FA257B74B05225C0FE3AD73A8784DDFE8833631290C6D7952224BB2B8769C6D4F4A1EEF4558624CBD6514D8FD15A738B02EE63855C6E60EA84883AC049EFE6AF52C5DC406CAA0E5367DC2864B739CF746F050C4EFCA1
	6B37D1CE17B71550F59A11AE74C2362E083A10710B3FD0A977D5C157B515655B78B5E82AD26EADB252C5CDE575CD184A6A11BCD549AF309F695227B27D160572F9B3D4C937FBG6986C399DDG27CBF4234B9F946377A2983BG3A0B347AB7D95005C0FE24869D1BBF972EA7FE1E6EE57EE0B8EA98A9F59C03382426E6B71DC6591E65F9F45F0150A7DE04FB13F72CA57317F4F1C7EEA140460BF17E7F73FB38B799FAA4D2CAFDF6535B5E76DD1BAFE6FA18AFD66C3B26EFFF0B4D0F1374C4E6946445DD0CF36354
	B6657E48F607435AE722DBB3FC4EE8E8D746878C37337DDD38761B6C8E67AE8860C6F0FEE9EDA77A123D9B0E66FBD989F43F0D4C6133C16E32CBE0CCDEC27DD2B5091B48DC436A59D70E758C79699A2967224B32F33D54B37092D63F08CBDC755C70732225CC1E299A6525C2FEC1984A3B164AA3F39623FB3B8669DA00A6D050B55590BAB297317EFC2BF025177E448805755FAD14DF9C70C986189F06657BBE1D485EB63D15F09F7464B74A3E0FFB7F2457B64939144792173A5AE40ED57FB62B6B62862C2B9172
	278130186A939A2F99B5E339339BC9BBB887CD8E264BBF3757F3D9C9B7CD17E25F8E4E565AA39F3CAEACBB196B102950A5E0FA6F12D0G26CFE223E87B4B206DAF4378E73ACE997F4481FE8EC04E65286FF9ED846F9EDFE2FC585E047AB29997B66571B1D68865F502472BCD0407935A3A9D79146258FD6FD83EBA2FE4FD872B7F748F15752FBB753DE67D035E9F4C8E3A1CBFA3385CD57F50293DCBA227E27DDBF7E07DB30126E29A6A76DD4D0712E60C4C4752075FE01D4CD0A6C2147B699BE2E3C356E107DCCA
	7FB71ACDFDC7E4E3893158D243B1E7A9DDF31536123C9D64F63D70D79F2FA7FB7486DAD947BE8BADD9526235E06D7A8E264BA3C217D45023608A6C436D954ABEEC39026DCFBA2FC0DD376EAC9372CA59C5A75B7B2D76A1CFFD3753ECD418693B7FD885DC6DB2F8A0F93B2A38DAEDC5F92177E5BBD16FA050A9DE681555CC70A60B49E9B218CDBF49A56B193C733EA46BAF5BB0ACBFCD40C7E7375AE43B73B817E5B6D886CA77757EG9DD66B09D95F417C7D16E674770B2EC0FFF7633F1576344CF01626BD85BFBB
	103D71CAE6C7DDD73A6CA993ECA9BF946D29E89756A99CE8D22E443EA8AD576A0B7CDFE31DA76F7237AF185CB4A13798F8F68B3EFBF613B942ECF04A8E67BE0B79B876DDCE8B593FA3BD074D58B8884DA5AB9B339D967416810D4654EADE7E8CB0E0B7E9DF320F947AEB746F8F6D41B11684381CF0542D3A857D84CB9BDA34FCA063F1F58B562F8E4ABB850FF7DB11872BD76A8A524E72084DE0B192AF2E376EA3FB8BA5E8F387DBD117A86011A6785C556A2E4BF32DCB69B8E6F3GAB7B65DE54E165D530AE3D8A
	7BF64FE62D3E8D3E8AF9DF307717756D82A1B79CF826883E5F6E2B63BE123A5FD2F237976E9C2CAB99EFBECBF276E2E2E392987F18FD48BFFFBA442C53117FE7BA6DF1183A3918343105ACDD75D62BD9EF32B9185D20305DC40E9867969D36F9G700E957C9F5611B6B75887783E439BBA77F9EE74G0E4B002498297043F3B547A50DBF273371D8F835EBDCA6DE553BA475AA9C17F57B310E03C093FE35683BEDDAFD17F2B56AF8757EDF56F7D29B4A1D8FBC0BA3D06EC7DF2B65F6C3DEC0844A9DE87BE5F29304
	5CD06019A464AE2851121BF38D4A7D2A7D17498DEA97F3BD7054DD03F2336A346486DD03BE2261B0161B8638E4D1AEF72BD6393CEB315C4B22DC91603A2E45FA9C6AF1778B0B3E2F90FEF00751C4E3968898F7B965219D5410CBDAE8CC687851DC2A186D058D5F5403F2C2C0C6623598D3C634EEA1F312DDBEEA0209F9E21C314DFA8D48DB3A8E562857A16F65BDE46F5BE031BA87E57BC4D9F35E9F77A2EF9B700D3C8E5B67E42F162FE57BE365DB147BE3F3204C4AC80849817411D8BED233BC13791C10E9867A
	88D1E6FFDF8DA9E3F09AFA8D909E618DFB40DC2288497B687CEE6CADED62A46EF1470152A6CF59071B5C59357864A77AB63ACF44CF20EB4175A2A6B73A4F895B0C040EF0ECA39B66F6594E4A879831FC8B148D3C9E7B2F60EBF2257B4F926A3E3812BCAB99101DE235512860D7325E4D0D9207CEDD361206BCF7E07E7175FF21FFACEE0A102AB20EF8750FF157B33F18F63D4BBFC63D776A52F8GD607B5B2562162B79D39665FE09B1CADEB4F81D3CC44C69CB63349C9D653ED3283B2BBE5534020D34C352902E7
	8470CB95BC9B7B5D5B75C8BFD9C78FE10370E78E0C470D7D482360060E5C169B10C741C02D16AD35497DF659B1B89EE3E27588BEC462A3BBAF669A130E81149B88B263045C4786B4ED3889968A933261AD274632E14EEE82B6DCBD087AE6002E9B2250FF5E33C5EBFEB3C6E1FD4287FF193FFFC3489D811E51C26E22CAAD39732270397654A275F9244CA85779077C9BBD4FC230F3BA2545C4DE710D6C1C4E92012F22788A851E69973F96E30FA6480B3C914F163C54C96CB76F260E5C9200FA00AE009100301BF0
	CF6661F6720CF24C33BB3BACCE59EEB1185B5D9EEE61999ABD11FBC4BE4E53676B157E003543FDD5249DE2EE9A7F598FD6AE0516CB1AC0B966B356FF27BCBB1833E2F77692AFBEA313734E3B5965B3D85BDDFB0074ED8B60878182G42GE2EF46F6AB78F6032FF643F3CC3C41D81D2C557E3545496A093685AB77DE0DFF72BE2B1928BCE62387EB09AD265C4CEC74A30137D0FCDE348B4FEC77F63A0FC5527510B7BF9AEDB76FA0E97F305FF66446GA4812481647C97367F775F7A345B9DA31AF64B6A1753686B
	1CE271FF39571D157B40E739E6D5B9D61F5EEE322F5783F923G21G51G49G69G8537E03DEE7A3641D73D3663C3E83DD55CB62A9FA4233E337C2CE72A4FF29BEEF1AF471FBD6B49D98A0D7DCBB24573584BF1CA99FBB5819F9BC0A8C0A43927EB6C3F6AF00D9DFF8B7A5330A635CE0BD4BAE85765DA0FF2252AF4C10CD61FBDFA0874598648EB866881988588870883189303FD1655315527AD1289C5EFED84DC3355FAC4233B382E6DBB7C6BAF034FF2528C2DF21FFE63DF1B2E73A877EFD53AD0D3DE2A1F72
	1E73A9EF638CADFBCCDC7A1936BD62A2036F690CD4601ED9738C3437701D7E69FA13C7B9353D85E836CDEDB3A997B6E3FCBEB7EA867A4A3D74D9E0BC24D381B2G72EF6D482DG5081B8EFC5BBED6B289D271D32A84853CA191EDF6C722FDDAABC4A297BB074D677F22CEF64F26C1B28DB31EF1E73D307C5BEF5C83BD52B5CD93BC939CC1E17FF5B58FDD4FC9B6A1932473F717CF71F652AEE53AAF7FD0BFF6DF21BC7B9354D6AB46599DA7D2B1F55E739C1CDF99B777AD73F2FBD4A297BBD60B6AD5BBBE3B34EC3
	E122CFB37C54A1FB1F2FB61E2DD967BBBC4A0DCFDE22CFF973B465B9F57E756999BE4BC931DA736CACFAC6219072AAGDAG86810281A28112E251FFC5F4780C0D44ABG63090D9A767B572EBA0FF29EBEA4D6531636219F4B0CC5DBFA2B4DBF9D8ABC4A29F528385D3D9C0B3DD6D5ABE32F165B591A558657405BB176EA799A751CF6BB6AF91F1FFA267854B379F62D367A3C96F518AFF4887D5EBF9D26FB14D36BD0FC07168ED5F528C35D9D28C373E17FF46870A82756412929C343D654A1D8681069278E6F76
	7852A1618E2DF2B19E6546A76FAE1F72D2B5658D9A7C13F754231CDADE06263C153D7E497B52231CDADE5E1DDAB169A68BB97B6D7B0CC62B6CB4101DB0F6A2161F61ADD31F61AD86994DG5DF76278BD32855FD1F45E0967FDBE226F1BC5C1BA81A075CE3425F4BF6B3E58E75DB36E54AAF7EB1FFF7292BC4A152A5BBACE2B5CA423FF72C2BD4A2965952B6431392C051EFB2B033CAEG9BC0B0C0B4C092C0FA9C4EE50BBA48FB84631D4BFC2F9BCBE47F6A3945231C3A1E793F53AA77241F7252BD4A296D26C4D3
	5EAD7D7E491B69D1CEDD3FFACDF947877C13B759231C3AFEDD1A72368D7AA72F55231CDA1ECDA50F7908B736603C9274BB7485CBCC7E693042231CDA07F8CD9D7E378AF5C801FC760E64EF68D9457C5F03DF7BBD62A62FA138E6C8777C9EF52DA8A64FD782C57AEB1A1EA652772C6DD269B0DEA0BCFD51B099CF78ADE349B8E8181FD85C697D9D1625DD5A6F3064F6E93F433222EB85431303C50A9DD14C7F34EB6CF7DCD6F7E93D6372AFDA1FC732497CE29F36B8CDC3B2954B66BAF9043CC3855E2CED5842DC9D
	116CD06F11CB68BF037BC8BF467F9E7D64EDDDE512271FA48F0A2DF6E422178D76FD7D7DA260EF3749FD2873CD873C375F0C835A6DF767816D761BF9E06C767B5381EF6D47574A35A5F0CD053A2583AC7B8358B8C0BDC08F40A8C098C0B4C0629F500E1D34CC9A24F36259FB8164DD83F22EFEA3BF97CC4E8A944733336C2C4C9FEA42398D911EE26CF25E5E6C8DF5D263D3D56321478B0FB0953E5E8BDDA24773FD9C216B7B8A1AF7A4768E1EB7DD71BDBEDF634A5BBB40ABD469DCAFF478FEA1FF66F3503BADFD
	F6D05B16D69F543625F5874736257FBD684D1658F8FB46C9F473F8FFECAFC48C921ED12DEDFC0753681CD6DD1960ED6E76DE8FC737F6BD0EF71336F4D8076DFD32DA57006EC211670AD04866AAD1073FBFC74BBB8D768159232EA134BC4B73D67E52EE52C73DA623494E5F24E2F5DC5A242CE30504B176408E52CE9A7B3E6DFDF62B59CCC2EA89574457DA54B1B519C71AE0CCF545E3CCFD43A90C29ED7198D33FC04F14C5C2BA9EA0A59E475F63F49FA4BDDE5B7EF3E672F3DCB3DD792CCF7F74717B54A69C8E59
	C1B7E2C9704F6B792028678E2735F113E83B39073C77653CC35AFD396010773EFC69106F3EDCFC48FBDFAE7D5EF7DFAE7F5E1BED333ACE59216C534D822F7BC65986AE7C07BB1578CAC9BCB32DD047A59B204D6BE7FA77010C5F23CAFE2D4426D89C52C64F556A213CF1A646A6D7D01C935201B3F14E6ABA6C3DCF8E9F566E93793077BEB19F765DA743073D774968E15FFDF25661317B6450AEE59BEF91ED7CC3393A0DC3219DA2FD36716A5DCAFE2EB61EC64F5646C179A45146D9FB88AE855273842E056296C1
	FA59DDF84E637D96255D148BD9F3DA14324A84BE2CD5C93FC96087763E2B9CEB9BC5EC3FD9DD57C2103D61AE553C27C87373067B5C78EDC27C0B6E78B2445F204BC53D7A6D56A1500C75D1CA0FF73BFA22C75B2E1E6E71EED773FBFC5B55CBBD5E6DAA334737DDADB5E87B6D637B15FDD2AD6CE972B6F59B6B21BD876F72E5CFDB5B5C5AF2B34E836FFC6265F9B4FD591779764EEADC070C5E053E3D1C1ECB0801F492C05ADD685B77FF6DF1268772726F7625DFD63B1BFEDB449ED22D1ADFB1702A72496F7E6F5D
	78D5A23F2DF5EAFEAD402B47A73FD3075D78D5A13F1DDB557C1C40AB48A73F729EB7FE5508FF5E8DDF3691713FB638614598E96FD57AE26273591C7DED1D7A8C41B450A99EA079EE1C333B4B70594D7C3B31DFFF5ECC4FD6A5F4645681689330DFEDDB46FAF6E1CE702CAFBB57F2B65DD78BC8E067DA32847EC20A8FD66059F9174FA81E2443A0AFB381FD573ABE65F8F0792E97147856CA017F1D514DC70087E5632D23573B0FB8542B6DA37AFA3D7B089FFAFD7B88C72FF79F71D32FEF9FF1E66F58F34FE1D959
	B6D5827FE93F32EDDC7E7B39FEB75BABC7FC74009B3E8271C7875DEC2FCCBC2F2B5432091EBF3A5B02730FEAFFDFE3F263D7AE1EFFD7E9718BD2711B6641EFE69179460359F05CBA6C1424157790BBCA915FF208867AC400F4001CFB583725186F7B6DB1E2A37DF64390B4A7DD94AF22E52FDC03E50D109E8188830885C839876DF1C6B5A93B3B9D8F3FEE62634EDA2C9CF77577B2F903F09D3D97631F103E77147D6936A6A914C9DB6CEEB1743A3E552033B2D9B2AC078C302EC7FC6FFB040FB829318F4C594507
	463DA20F9DA665F9CF54FD284C534976A11343611AFB86327215795B6DFD032623B24B8BF04F538D771ACD0EC11667B467E1DE23F5082C5D787BB91D6E7A17F3B9470E386938E344E635BBA5F6F67426DA65595150FB59F33858FB71ECE9365B3BD4C9BC1F5C337CD55B3C65B35F773885FDDF463D687B5E276FADECC884DA006EC474FD51274672FD4EC4EF3E4FC05FF989CAD41FD3BDC271E1096AF32A8B7761B955C8484B7B9346F2AF5133DEB6C8878ADC4BFE5C738B9538E3ED84C75E290D9538D0FAEEA795
	52698217C2F1B977F564965C0776ECEA4677F06A6FA3B427E83AC86447E0FE994B3F0F26836FC31E3FAAC7B9E182F7DFB94A0992FCEE590DF2E26FA35F526C7EC1F2596C9E1ED74EE41452F426A85B3747DD0763B43D72FE14F7A5BD0BD087E91D40BDC3F1C6C8BB852E056E6D86C0FA1A2BACFD769C8969B8D7D90A1B8D69B97723ADF4D72BEDE1BE64C9C95E67FB56675751F829A0097579BB829FC3719B95F8FE161B62C93A85722213D027C17A6C3960010E5C12879097C5F1F510EE91386D7479F097244D82
	B7056282A09DA2F07ABA241BA6F0E1F5C897ADF05FEFC53AB8011B3E9569E6939C75FF066F34768AD2A1BF7D810CBB4E2D433D02E533F06CD9B7117A56C33A8B40B68B5B5BD026EE6F20D92E361E867751335C5B1E356BBFE99C13B0CBBD76B2A9BEF916FA6C85EE46311786F94D8F0A3D75ADB84E229EC41D4EA7E1861F334294772BAB7D5D6F9E8FB099B527D5862B732715F4BD89751DA75A61E70A4B00F47EEC446D29C23A158297D505F4A533717DACE9B546F49500B34F4679F7D30F5B3B7C64AD031A1A1D
	F6133141B050C2E7F3E6AF4D2B177172913351DE0ED21E29C486C05E1F71BD5E11AF709D7D28EB27CB331F567EBE439C7D496C62BFB33BE966D77ADE7CD73DCB184C04B5A8B39072637ECCEDEA0DF2CEA136F24E92763C0C65972FD56613180DFC5F4E155F6116CF741D1B254C8FF24BA7ED54474B333A5D7FB956AD737EE849BCA3506BB7FFF39EE2F5AA615722D917E669AE553D455A7D72D2AF5FD090518CEB036B0AC99B10393B67A1EC77FF8C51398B5251G098FE1DFAE6C77721D8D9A49B0FECFD37FC3F4
	1EA34ADD6074268BC68B4C6E1F32287DC053DFDC633E8B6E4DFF517283E752B9BD60AFEC3CAF957873A8BED401E7367A11F8F7B192727212512E6FBD05FE40160CFEE0003E2B9AAF52BF52B479B68BCB3F3A6FF40DD5174C8FF427CB46928967653C7D4A7720595C3BF6BF29EBB26841E2663E7D68770B9E6E482DG5083B881C281D8CCFEDD9B5244GAE89E059A31077887B1E8D5FF322B14310C59E32DACCFDCD0F10F23F23714A1FGAA9EC13A3965747DB1D82C31B862F3229A577722E6B2765650A37525E6
	13C5EEF79EB74BEB44B85AAAAB5FFF59F822456B7BAFC10FB01B8A635760B7AF4D62FBE6F4DFF34DF64B00D9B62EE1FA5ECCF5FF82A09152CC2FF306A4A1774103CA3979171933333C48CD6172529EF179A6A637E98F792E98091FCAE14538D7BEC6BFABD6329E6BFE3EDB7EBE33D115BFE333E77BA6D19C7918FA6123583E0BB73B376FC20B2BFD7B3650758BBF83BDB4E43098A14D681AEDAE3A995BB4E4D15C7DGA5AED95B5CE59D791170E0634ED10D71CE5523B896632AC53C73A80E039BEB483A240B50G
	0483A4083CBBE8DEBD7F2E9479E6D422F896382DC6B9FEC839DFB2E63AF879D1B779DDEEB2341E4BCDF4DC627C700EE86BA9551EEDFD810D8B4F8418A35A7AAF556EEDFD1E5D55DFDD351EBC2C94F795C03A60F1D52DBB0F5F122D8809511F24315D25841102E320F45815BF19460C67G640B7C7DC7C9BE1BA3AE7D8E57E5C5A9B867DCC33FA15485E99BC0D08A366B059EFBF9CA88CF7136AEFB0A461AB1A96A5870850ACFC8D14706431BB0B6CC063C2A3F229D0E507D33E9100E9638D74AF11EC89638B5F4FC
	AD02F45EE3287BA2BA3E0AC4BAFB8BC9136FEB3174144D63716B243CBFB644E4DCBDAE9963B90B21856647683AEACB91363D062EDD9C476CEAE3253BDDCDB966324D0BAA3D7B1C9B49672D787D6C632E7B05BFC9222EEF2D76F576DA3B0D8A7CAC57BA61F24C861B57D05BFCCC3DDF3A1F62639FD36F170E0A58BA8972B67EB73E73724889B25706C2BA9AA091E08E00148A73EDAA6EC3DD7D43C6494799BB3D5556A29F154D0AC7702CFEFFDAB3517A31B9F0665B9843465C719BA9F6D21357EFE8EDCCE5362163
	575824CB334815554FF41254AFG722681448324G4C8748799B566F799FB6307A1163B05B1DCEC35FA07DF21AB8DCE3120F3950E4831055AD2A1814AB7C1B273DEEGDCBD00DE11B788772C0F7AAC24AFC67F260EB5AD949F72B7F52C795290461A9110376CF1716ECE2D5638B4BF4EE48670EB68636E7AC5BE2E6D8B62GBFDB1147FC52EBF47FAB8D700B9EC7BFF55C063EEB6593087B4ECEF1106EF98275BB1BFE074E8969A0018B253870A758B74278BB326D7F09581448A0323447D945FFC486DB0BF86217
	4D21FB0574BDFD120E575083678A04E42019877091CFE7BE619A337C5B87FDE43F1A062762B9CE757EFA38DA64E3C5CDF23FE1586C94DF69A9D1A0DB071D7403BB7CA3BFA5EB4CD6039156CBEBB6B86D43727A75EC8EBDFBBFBE2F534D41356B4C7D6A356BD7EEEB57057B55EBD7222FAB7F73B6755A757EECE5FEE71BFA6D0A65192DBF5806F3E8906894BF8757FFA98D44872957FF742B97B5B50A26E8F77B4A9C2B67A76D62BBDEF3B0D618F6006E274D6D48ED8668190BB1CBF285392EB32E63FEA87C18B296
	2FFA778C4D6FB89398194BE37F392EB5G73C1FF79194CD789F3515F8C1A499A2343700367A9BCC012B34A6CEBDA58677714D31D2C6B38CF3B7B94510C71FAF068F3EF3C40F36D0626E2DF5D529B8604DDBD351972332D4374B61A165771586BAA9AA3CE86C8190B3157DAD2E5CE3FE00478545DBA6D8F59B01A40C36477C8E0D52F71919D6463ACA14F14F7EF577CE88D2739F6337BB386C64B72A23F26FAEF47FACC2A25E99D26C36BE83A8553D7EE25699D6B38CF3872BB755CB4EF2EEB5CE642FD7E13DAFB85
	1F52BD5762A7557E3B18622B1ED47B6F9B2A51FF37C0DE741368B32F2FC3BFDA70947A42E7B75178CE249752B479268ECB1F58B4169FA365FE09FF63BEFACC997E46F94A72307EAC73FC96F72DF8A7E3846A9182908B1074947B26905BBFD8DE01B4E900DF84D0188631B6C0D71A78D65CE6D28F764DB1A337E95DE65C279B91F45BAA090DFAF9CD042FB36E91673B03C53945DB086E0DB2791E8746AB47E40CD7D65EF03534F2D5FDB67AC6F57D9352D89F252739566C2E365FCBDC833F6FD45C1F317A746F99DF
	FEDAE57073B5A73CBFD77F7B896D676A73CFE81F7D7C6709314FD93FF6427BEF89E40DAAFFCB007955AA7243EED27153903B819881C6G021FC63FFD7EA86990766588F50C3843B8C01E6B13DFDEF222FC7BC96FF5BDFE52776FB51DF112BC7F6E35BA1D56A1429F71C1A7C9FFB92DB60AE438F3CF12B63213EFAAB1D27C6D251344FE4DF2BFC5B33FF3B75D2F0D04FAA5BC0D3EA861F33AC78F69C582177F857A273C3FA3EE5B9041F5C3FAC46026D0DCB024A37E0EFE4AC46346D8C8A7891C1362EC7C0C89433D
	BEB4DE1BA3FC16F31E93336B824BF8E560EF66CC444731B648233EFC8E6816AE5A65C70A4B04F441B3086B237E248452AD82F79B45751034405DC077B2C3A09DA9F05368BEC29C24D31FC1DF34426A69C7CB2C68A3531FE1E79A164D839FBA0FFD9F24EB9E21893195F11A0AF91E6D59BCCF395E7E51CE56DEC3A60BE9E8F8285D74134CFD6A59B4668F875A28F9284FCD7FF61DCB4067776CD91A624470A67E3D29F67533FB22DF4ABCFC7E773EA45EDD855CAA4ACB22B876173ECD8B4762159749D1C86E1B6766
	A9FDF35323CD59EBA5ED5FDC7CAC2B3773D917EF36DDD1718E3323735C5ED70E821A78E74736C5D66EE10FF7F755102C62431FE578AC27FEBF083F07592EAC176EC3471C67F0DD1A7F9D6927426754FB8A1FD2FC71F36ABD056987F0CFE1A36405BE07FB8ACF33FDACC897BC0F3E3378E7723BBE5EE2DE7C2EAC43522DE86E3F9E7D19583743AD9E0E2D26F1DC63BABE76E6503D408A10557CBC0E150AEE74DDE6010B6E46B5EA587368FFD6515F7A0C02F43C40FDC9F1A94F33F7C2996E3755CA9FC2E8A7BA7FC5
	0DD1063F9B6EA6E3223F5F654BEF31B5EAFC9D2ED197E96823DE23664D6F48AD1A6FDA23D64CD72FD15FED25631D46CC1B70B7994B50AE6A01FEE4BE2E617EDE6A768D99769C3741CAFE5B05BD4665B1574765982BC54F4775DAD3B961B98F52528B90733D00BE22C2FCEFE6638B3806B5D7285730647746CAA5DC03DED9B1769AF6F605FA8D7BE737672F591B3D2DE17939284D8457EFB0BBDC5FEE3B5F6DDC64A068BA7A4258B64446D2E1E591DCC300B68AE0B640BC00CC0002859D39A5G758BF00C8DE4150C
	B146D8C3539845EEB061EF8955E7FB0FD1F6E67B0ED1F459743786A036509F3771F3085FE613B1EB6D6FF748A2967AAEBBDF60F6B9F4D687DD6C3176FA2C18FE9F54F454C4AC415A47FE4B004DB18BE92C723BEC08B996609C735F1F13E7D7D60B2C378E778D6E205FEFE6AF3F31B2AFFD0E67C64CD006597D3AAF506EC797E85B3D5276C300A6E2014B6EE397286D3EE30854274FEA0110F39896414C00793310A1141FA0F43EEA084C21D6BB79B02332FE378F212DA78B5A6D3BDCE38A67500C8DAEDC99472D2D
	F35F870FA7DB95BCAF280E4E590691551C9DCEE312E57FG7B7907788E7D96524F03205CCFD09F03D98F9C310730CDAF22B18A998B75225CAC7AFC4FE6177BE53BDDB6D2A9C87F8C25B7836D08203F2BC2BB0EF80C0E6188200B96343714FD682653F633E940C2A2EEDE6601B2EC3754FF6079911D957DCEDEBA4CE96968A3422CC8BB1A0E7E649ECA9B8369A400B4C17BEF3B7836C8BA6A52C1779BA05E6671448F794AF8A275F8075735DE450B4C56FADEF4DF6B317E7A324655DF7FECA3631CEC61098FE9B619
	203E6438E54926324A8DC5951BB772FD3BB744FED6580BB8BF95C8CA7FB29B70E9AF0E4B3F5412F13188E88B7E896BE8G9D0091E094A084A082A096E0B640BC00CC007C05B0B782D4GB483F4AFC49F34E5737AB1FC90BF3463F2C292464F57503DC89B70A17B057C9DE60A8B829C5903E46FF645FFC8EC4EE4A10F6B9B592F568A9E6534DDA200BEEE21D82F9C20F1B924B397E2DBFD7C9DE9F7BE7FAB6EA38F1C7EB5764C53AA037541F31B493DD89733F00A673DC867AE710DDE6A0A58EEA33343246FBC5793
	CF0933FA7999ECBDD182573A8C360EE36B09BD5DC51CC61F61E9F7039C47466EDFE99CB68A38208C9CE337D73A568D685FEACF29568DE5EC5D50DD23DEB790BDE2B2F08C3FA83EB71318B151F5C3C61D4B9E12BD4E940E2F0F1F74335CE79E6F007982961F475258C8FAC99D374F24783C1754F17B6EF274130510B77A920E0BEA37777C66GBE632531FD889BE3CFEE41317B0A983B4C7F1331FB6992343D35155EE30DAFABFD479A65155A7BA155151E7BA1DFD7EA6F07B4D5AA77C398EE9767EB97AF7EB27CBE
	7AAC190D857CEAB97A6B784F4935FC73C6BC7B74851D0B6D437CCCDC5797E8034BDE1E288DFE7235A464CC9AC23EACBD1B6E773B7DFE02B80DD75A3123CD3F2BF1FB4B61F6DD4BAE3D0473CC4DB6973F2EADE44D68E51E5932F9C3D159C6F186A80ABE13D74AEAA77FF11B4BD15A36EBE7331E49B8D46F1291DEB19E992CDC40F6D7398B4A462FDBDA596A31F5EB5C31D73F23CD0C2D8E2BFFE35268E739ABA6DC0E0D599A9BBD5F7C32FAACB7D2FC4B4B6A311CA4E21EAE480BFB994772DA3BD2FE51ABB0D73FB2
	1EF8C0EEA0FD5E8C34C600A000F0009800A400B9G0BDE4139FEE975D8EBFAE1160A591E1FE151EB35CD41A2FE96F511271E4C27FCA5211F4992380D927A9902E3FAED28756EE74AEBFD7B193ADAEDBF53D06B69E7F656EA7B1956DACFBF53D6CB7C41G44C0DD022ED3608E88DCB765C9F01DA623F3D060FB29DE845FCCBF1E0F78C12A57B10A6B6238A1856EG6EC75712399D2A8CE1B516BDC64FE8751B4CE616BEC1E9CC96D82609768820BA1AEC86055EE7899C511B2D6F8DDF64C9A236202AB37C4A1177DC
	F8D41D0F635D7477676C56E32D861B0D7AD796F37436E34CD187762087888108D8245C176CB8C05F37D3BE7115B046309E40B5D85CA2DC9F649E94E7E0G1773AA468B6D87F1FDD077AA2E8FFEC5E91D108E860890340EEE746BF12FE26CF2C377DB42DE502FD513309A57E255E4CCF90B6D2BB6EDA8AA5F08317D549A641F71AA0E65175D4E2FEDF88D567C2F0DE75F71776475C7FEFF2F62DED7FE7A63DC53E91541463E2E827D413AAC44FDDB017E60AA214B21AF146D2707369B9CD77B555BDCED36CBF17F10
	7574375F7327D586EB2B9ABB365F83227D3659317D485E879B6B36866F7E74D803EFFF9A50206DCFA7B5F87A53F39A347D69858D1E7E74628676FB9C83B00E14F15B34862D380D3F33C2FFCFF3947ABF78B5714E0A9E770A43DEC3DFB1259DFD45BC40953D0EFB18338A47B80702B5E7BE0EED89B59A7A86F9CC78D0BB1ED96DFA9D7D4C15DD744C9724A38162DFC71FB269G113DC958430A037E45A12B7CAC57B561F2ACCEF9097A4D145755714BE20ACFFBDD9D3FCC96BEB99D7274196873C63A1572E3811F14
	B91EB19BD62325779CDEB6C3038733017736FA46969FECD546968C677C42136E7897CABAFEC6D56F3EB7573D8312FC8F7EA2BD6EC5AEFB4377DE64CA20A1709147EFFC4355062C4EA16D7E757115FE16FB14162B53509DC187F9C6C5BE33018F68732211B75436719945873E21360D08AE340D90484BF8C3BCB3BEA889DDF23ADD77679EBC7DFE795A53AA035953795D1EFEB9349BED6C00203B19468ADD0BE1BEDB0C3E2C2096FD19F3B146AC7768B126C8D82C359645186554B712603D007A2ACC204FFF93F9FF
	C37994C13ACA60BABA892E855203EFE27F949EF07549EA457DEF3ACEFF7F5CFDDAE57073F8B56EE37B16E6D2C7B6363F2A45319D7C266F319D8EB484F0EC473C299E5B773B2D1562A1BFD9032F270ECB751E6FE22D24BEE95E1B62F76F74E4BF5EEE35BABBCC8E93F90042E267E1BD464ED5D2A7279D6AA43C553F614BE275C91D982BB783BDF9DF0B5C7740BD3972EF08086FD00FG5E3B8DBE324BE503CF6ABDFD6DB3FACFDFDB26A71B887E1F0925CF6A562F977C5ABAF14F057F1EDFA77A21E850FD9EG1B3B
	1F2778222C4E5CAAG9DGFF97596DDBB403009F4D6A9C475C9553691FBF3EEB4265185FBD4CE28F504D5D9F8FD27CB4851E35430B229DA2A1AFAF3B935FEF29756C2713354AFEE2FA4AE4E872FB0B627E154E536F9772CF2BDF78045A4D311E7559129475023454A92DE0BF9F49FFE9117C1A2ED3FA0BFDDD43A96510CFF0B825F7DDDF54D0A55EE25F38F0CA797C40EB917B555652D2C061395BA2FEE256A9ADE747ECDD088F3098475002EF738FF4385D718FF3A86F16730FF2949DB3B987CBCB9DF669FD31EE
	5DC5B786669CAD2FD9725455CB32251CDD52DBAC305FA5651068FC173414866F3B24A590580F10D5D30E82A42828B8F0D9832CBF70DC92EFBF6F2F0FC69DEEAA481632017B204C58ADBDC6B69488432C3B004FA9470F524F74DB17EE6CB531B994CB1E991367865B47134AA2A3EA86B424D03688FFCBCFDB04D37A1FF67E8360C52EB26D43C3C3867BF16DA212781D35D34AB3660B4E6A73FA46DC3F147911C1FEF57D4EDACD9FFD768578F8G368E1A52CBFEDF5C5B597076A103596CB4180FEC379849B9710663
	CE59F5F6F2DB9F7577C843B3D95EB774DBCABFA84A7CDFD0CB8788E6E5EC4A4DAEGG0017GGD0CB818294G94G88G88G390171B4E6E5EC4A4DAEGG0017GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG87AFGGGG
**end of data**/
}
}