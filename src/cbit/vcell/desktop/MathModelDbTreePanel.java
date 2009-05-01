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
public class MathModelDbTreePanel extends JPanel {
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
		cbit.sql.Version version = getSelectedVersionInfo().getVersion();
		boolean isOwner = version.getOwner().compareEqual(getDocumentManager().getUser());
		configureArhivePublishMenuState(version,isOwner);
		getJMenuItemPermission().setEnabled(isOwner && !version.getFlag().compareEqual(cbit.sql.VersionFlag.Published));
		getJMenuItemDelete().setEnabled(isOwner &&
			!version.getFlag().compareEqual(cbit.sql.VersionFlag.Archived) &&
			!version.getFlag().compareEqual(cbit.sql.VersionFlag.Published));
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
private void anotherEditionMenuItemEnable(cbit.sql.VersionInfo vInfo) throws DataAccessException {

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
 * connEtoC19:  (selectedVersionInfo1.this --> MathModelDbTreePanel.latestEditionMenuItemEnable(Lcbit.sql.VersionInfo;)V)
 * @param value cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(cbit.sql.VersionInfo value) {
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
 * connEtoC20:  (selectedVersionInfo1.this --> MathModelDbTreePanel.previousEditionMenuItemEnable(Lcbit.sql.VersionInfo;)V)
 * @param value cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(cbit.sql.VersionInfo value) {
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
 * connEtoC21:  (selectedVersionInfo1.this --> MathModelDbTreePanel.anotherEditionMenuItemEnable(Lcbit.sql.VersionInfo;)V)
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
 * @param value cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.sql.VersionInfo value) {
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
private void latestEditionMenuItemEnable(cbit.sql.VersionInfo vInfo) throws DataAccessException {

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
private void previousEditionMenuItemEnable(cbit.sql.VersionInfo vInfo) throws DataAccessException {

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
 * @exception org.vcell.util.DataAccessException The exception description.
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
	D0CB838494G88G88GAAFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DD8DC4555B0AA1A2828E8924598ADC622D1D123C1C50BED5AB76D9BAB5514C6CDADFE262F319F36E9CBAB2D34DF5ADEA0B1209851C4CBADAAEAB420141204A47C06DDD89492881B30A1441004053D6CEED8F6B73B173FC84CFBE6661E593BF76F3230B6FD3EBC4F495EB9F3661CB3B3E74E1C19BBF790B63F973BBEABFB33A0E42D9764FF3B3384217B9AC178C9D3FC8E622A362664890AFF6F833C
	AEBCE3DD873FAD101778D0CADE01303AA19752BD100E78E6CA5E5F60F91370FCC361ECF888A2A7FD16A03C93736A194965B499D14E861A6E2F49075F0DG3D00A33F71DB097CAFEB8AE5FC240CC7484A960419A504666A1AA299178EB43900B3GF417B0FAB77CB285AE2C291269FA2EDCA8E47C40137DAA6123600930DCAE1BA17F968BFF4A1AD7CAE4FD1B6AC94A14C15A82C0AA1FA3ACFB7B1F70DBE79C346E696F33CB65A37D2243D1EE913D03124BDDD353EE16EC6DAE0B68B07419BC22E8B4BBC5875097FE
	C2F8EE284D25BC84E195700B23380CBAA227871EA50028CF905F7C994127413370E9CADE6127D8377A2DF5326E5DDF5EAF2C58BE346E35A0F52B781455297BD3DF5D5C4E27337FCE7B610283243DE7C3DEA2C0B2C0BAC0C1D9CADE8540BFE87FDFE27C84FE75FDD213EBE84865543B5D0D8E0B07D68F1EEBEAA0C7469D947BDD9E0BA030BE7EDECD1906BE1381AB7763E917E3ED32F08776F77C41BB055445D7E7EF50E8135405DFE63714313668AD733589131DDF9BCCF6C4193FBD7A4BBEACA8E5AF3B68091C75
	9A32D7FB1F4819AD73D928103D260C7435231E7475B2F8A665082DB27C1794DF71CF9F1E59661F284D10348972167C93ED23FBA36A5236EE3ED0306D9A4DF60879EED4F6D9B95341D02E56655596425BDD4EE4B6F3DD2AA8FE5D3FFCF8264BBEEA27A49D83F9CB372564B17B0A6AA4E3B5FDC7CADEFEE5CA5E9600BA009E00B10028CA34319F9B7FB5C59BB31ABDD263305DE191BD82AB9BF560AD789547A40F395569154C4EFE314B6C311B7B9C22D710E9669CA0ED5067D7EEAE357DFE1071396894BDE6496EF2
	82F77310A8099EAF233969406B0446A352DC13FDC88481C3EE81795EFEE0937CBA4CDE69105BE216C49F854BDFFC00744908398BB88283F8E66D72000934D7BC54FFA9408A5E8E6587893FFD22879CC6CDCD1B4BB5B86CB6111A88DBF612F67E107A0EB42097F6E29B5FFB0860EC100E5809FC4403EFA878986C7DC4FD33E79C4718271E0EFB20CFGD88E3086A0FFD7CADE59AE3423DFF7239D99DE3DC3A8FBFC0E269DF5CBCF64346DE276E359259ECF67D1B9C457A8480B87C886D883D0309B6782213ED7E3DC
	2D11755A285093751B0EEF7077AD7F501415A76713E762174958FDC59EDB6148E27D7261E1BAEE211EEDG0E5D58B731870B192DB71A3DA2DAF707878CD33478FA0A693B29E5262C53BBG6F89ECBE5D81D0CDEC9EG3AD458A66BDCAA774F27GE4FEBACA1A9E20936088008B6098318B00E1E257G17819C82308310C9FC9060DE00DA00FA008622ABC093EF43BDD427C828CFBC408A0035GDB2A201EG9240BC0025GEBGB6D743188730GC481A4G24822C2B013E84B081CCG4481A483E4G9455421C8E
	60G188D1084309AA03FAEA52F922087A092A03E8EED6840E745592C6E58FF5369AFE55BAA71250A365CA837738E39CDB74AFD2055CECA9E7BD469CE393DB74AFDF2D46E934C6F177BF5235CA7037233CBC1B3AC7F0E4B3FE7C2546F32B06DB8985C7BEF66771F82E6AB2954D64898A5367266B4F92C1779642970A4D6A6637B9DB9CD7CB871A1E8D34417286D24D94563D3D57AC301599BF97EC8E0B6DA2A48A7F366EB72B33149EA79F92B0286ED77F579F7F7087AED7BB737F96D7F877D9E8EB0DFFDF5A7E9DF
	F1C4F4CA8651ADBAAD706015732E6FA4B6A0479D83AEDE66A60AF7C31C528401011763EF6DA4FDE016A40F3DEFD892BBC5C932BB2D3E7C3B29AC6FF01F5B9762202C44CEE2F72ED108D3B87DC31DE40E6FF7B91DA21DB4F891B97F5BB446909D6290A85D649AF6CA8C7FBD93D90B8D0A63F20C733069A50144A80EE192AF31754DA2B331DD3747BE84D391893904AD75A47E2829ADD434518A6207E18D16893F797598B70CFE08F1431653378ACBCE383335E2611885D7E4BB6A59DC9ED16F0B9B18CE8F9E5256FD
	890DF5E43C17697A7989522E0DF6978D97156B2D9CDA66747B24DD8D2E7EE1D2285D6CB4DB2195D95EF0B5EDF3E0C81B10F258A667DD5E3319670996B902A3BD3D0D650F6EA16D4F17FAC617FB585DAEBA07D16E2F2AB6AB7359D210BE327820271B54B1966A1EG10DC0F71C1DD97494758CD8E8C1ADC8ED9C7484B5FC37A62617A4DF2DB2E0632EB6BD9DFEC64794EBD18EF0234E30FBABF3F8173A7A0BDDB114F7A7A1E86E5DF47A9721361393801507C7D334D8A1A558A9AD617524385348DCF8D0B901613C0
	F69F373B0A43EF28723AE49BE479D507098DBA94B1F0BC15995D026BFFDB8313975140624BF9707BAE60995D972DC817C952BAD8536AD88C5A261369F404EE4BDE246B1173A59D639B2D1377A6A83FE7B8DD261CDF20E7712CBF3F857B102EC24F64366899FD24DE66C769ECEDC817847895G99B2DDE5A323DB5BC8684A5A11CEEAE479D10D0CEF9C7C2681B0FE776CC73A1626143CDE0008A6FFFE0CEE24836916B431B8FC25CC178E3FAD8660C76BEBB9405B45405AD7B2B07AC803323EBB8FA2DD2CCC17E8E0
	7C1699146D77ABCE17A673911AD9BD8A1A157C12BA11EE8B606BGFA1A356A91E1C23AF94D0CDFC2332A5FA85DE70710EE2D1C3F79B376DB76193C3E780C5089DD3C3E1F31B8D57A0C6997893F651C5F231CEE9560B3818ABF57526F2A43C8578D79EE00E8CD3A2EEE24DB8E79E9G39ADDAF4EF9F617D8B793DAD4168D6D810AE8272E302524D9411AE31055971F219EED50B12CEB700F4452D7EF4D52DCA3A8CCEB721221B5D2A346705D624DB59AA5B9D7C666F057139D749EF4206F4EDFB19DDD976B27A783D
	CAFE0DF65E3FB25D66FD2C5F366CE3F489A4AD3CF192696A7631F505E99F63E75B27141B406904B616DFA87FD634A965BA87114E54466A6BE8E37518E8D372EBF0A0DDCC9B53EF114CAF530FEE7D90529534B33A12F6C667EED752BD426916GFEA5C0E63BD67FDE61C43A2A7DA9F9467D01F4EC7EFC211478F93A17F952BCE29EAB770EC2DCD2D33317CE23EC4FB20E6EBB90FA77FE5C1BDC3E074CDF8BA13D84E065FE1C7FFF74AA6ECD4E9EDCA8444DF8BCA7583EEB7AFE2647668E5F7C1F7164E88E1B9FEFBE
	C2E41AA04F5201F36362434A7D913D576B6A37532D99F94E686BD646878CE76DFE99FE876C9E2F548A815C984EAFC35DC4DF32F7A323653D2CF43A5FC66670B1103B88A02D03607F390744CDE42EE1756C5F01750CBE3DC0681DB3B77B15A075ACB3327A3599FD75DCFAE8E78E13B7E78F4A0B047CA4A34ACB2172481C4568F6B4A0DD8650149DC0BAF78321A3F3916B4F178A0B0274A7C6A82C7F1E217CAC40E782E07681AC5F7F77696CED53C78177C14FFC266C7B24574E84ED137883F2ACF1C031675E7F225C
	A68F34E0DD738F266499GD8CC55340744EB964DD84E50CA5AC132593DCC175F69F5322C64BB678B099B2232357648ED3744E4CF9CE4BA449D7469E2DC7DF436850069F3C1ABEFFB20A96B4478E76438B27E31GFEA6C0FCA76A5B57CB707E71A546079B77223ECCC67B5EFCF90CAD0172191CC742DEBA77C3DBF7A21FF29C3B2F0E962873CACA3BD87D67EFD2563F6E6CF14D7A4FFC4D16DDE1125F9118FC7537BC7EF4765863D87F5CFDD87FB820C9B521EE97B5EC92B4E3E4F9ACBD3A8F6B34799044C907305C
	4EB6E2E3C32EE12FD8CE7FEFF2587B87C5CB893158F27328D4CE57DC656DA42F19BC16F2FF75D6A95923B7534AFABB1C34E4C9DB50023525DDCC17AB5BD017C850A3699076615AEAE59FE69CE27B13C5DD28EBE65BF6AE2F1C7D98C54F004BB394282F1EE62342CC5F832315706B91418309FAD5F1355A0A72DC6FC36D28F78568E463FAA5D13C5DE9176CE6077D34D8D24A64DD7E89D9FF3907E179E987BEC60F4BADFA24F1D916436C3416F7741D048E2B8D4414F6B1FF7F5BF6747731DD686F6EF8D7E9CF3966
	8BB56DA96AF9EB764AAEE6C742E11FBDA503AD2D627674537DD827962009B80CFD71FC15D6DFA49E46BAB7F6045B97CC6EB7BAD0EEB270DC570DFCDFB212B942E116C42F5461F40CE35F2DEBA37BF7246730991BEC50DC2232B1BBE1C16F34EAB426D6734A6FGEB0D24FD49BED2DDB77A774B8E609813G974FF54BB800FE02250B8EEA79C0466378C12C5FEAA8DFF484F9FC6FA072E075CAFD1D3433B866B6BBAD440B1BDC9DE4EF2184ED6E2DCE544584BC22B81FB31D7E3AAC207B92C3729C404ABE5709BAAC
	05F2EB0FE05F3ED42B553715DFA06FB653576B5BDBCD5C2600E78467DBF028CE7611547D1613277D7064E5DD49F84B33244C1ECFECCC82636FBA047C9301F7BA677F44A16DF1183AF38BE9E3A7D93A1ADCAE07496E76B23BC1E1FB099CBE4ECB3B304DCB0EC2FCFF9479C7F711B6B7FB2C723EC3E2177FBCB7F18447E5C4F202D03869B24DF1C9634F23ECBCAEB96A9B17B14FBE1D9D7FAC0E4B973B300E85BD30966C61FD572C55F791BD28637143DF2F6F3EF998654E861E49DC6E72A6355CF547A0AEBE06F2BF
	687EFAF24FF6235CBA60A99DC339376A346446F339BF7F1AF21B0E70399EF8E6F239C9FAAD39954751C77C5902658C001BB80E653E5F28D5AE01173BDB44F24BGA71C40FA948E787B054C63155C8FB6DB6CB4E6010031D592071A29A11734511850FB4AD12E186D398D5B87D0CE9548F01F4018B22EFD971913BC62089DA66669F146B6FB470A3C97825FD51C7746DE3277EDF63AA41B6819AEEB19770FB86F42DED8AB75E27BD45934FCAD5B9FBB613797BA934AAC85C883D84B4B77EB16E7B29758F8ECE40658
	480CE5726DB5240CD9B27719A1BC4287760239C491127753795D52D7EE10C97C630EA36506C059A78C7E6C5AC27263FDBBE8677193681A4475CD317B4F89FF22F40463C132E16E91BD2CFC6DC9AC1F81E54B7A307FB66FA13FF47FD9C05D7F321B3CAB310A92DFEDB4A97815147A39D16250294BD65210778E4CBF7E3FF750BFEEB14489D59903C17D23250F794528BE1FFFB4BC37B3277BB9DC2BBD4D6B108AB41B7B318D8C035AF340A5F6E2A3DE3743AE115574C1518B1907C53B55A67139761AC16459867CA2
	B94F3F8C7A376B478E320E9E4286115FB9B09EB68772C80272991C47E30EDAADDBBAA88EF8C42FEDAA46446A91F50C44C79E3918EFCC168F215CB28B2C53ADA8F74E10268D9BE021B0AD9B5EF5F6B29B1659CD4306B3383E73C0579596741F6755E94DEF79A2566728736B797BC4A74AAD861E5DA24ADDDC2FA5B7DA44775A37861CC70A93FD679F92954F886C1C0E012E6D13C5F6CEA7076377D3FC2A824F74FB026FC12C01BC63G1EADF97398315F84C8AF87C883902C20BB004E0AFBB23F6FA66FA8A7BD3B53
	6A14C40F536C6874FB3905E7E80E90390362381C3E6B08529F30F678C3BDE9079E6B544FFE30F26B7610F252B44AB11FD17A05726C603A5777E667847199F1B26F842B4FE730367BBE5D9B48GFC018DE296G9DC02F8D5B2D720B32D06D0667186486E3F53AEECFF8EDF16B345B02152BEE88CF5E678D531547ECF4060E58E2040D59681B9CBF17629394F8F93DC1ED1A2453A0EF369DED372C17343F6EE4CADE8F0084908590FF925B7F2BAFC25AED7318265D327AFD3BA554B94564137EF5E765FE9832DC3A2A
	9C2B4F79E6322F17BB18123785208E209BE082A086A0E9906B7568970D216A25471750A697FFECD23FC8C6FDAFEE8D2F1E5704AC37E2503F1C7C6E59C44ED2E86CDF12A9DE0E3DEEBF240C3D56G1FC28734890091002D316FB80EEBEC1B837DE967DE35CE318EFF9D346BE28EA8D72ECAA7B9347A6C45BE52E7ABA0AF9DA0F7887A8E20854082B0F3887B6C7D2375A1ED11CCA8A6D79360DADC2EC10D6E12F55D38AF3C7EAA8AD9EE6110D6399581652656267F93D06EDDD5FA09263CEB5A421337A0201CDA5E4A
	A1ADFBDC367E9F5A76080B8CF9CFE7C78D6E19258F213D9DED8FCFD7FBC0B9353D15B8354A7D011653B92766F3CDCE7415CE7A4E508669C800F9G09G29G19GC5AE34D34F515AA95AA90B02822D146979337D61354BEF824A297B304E65DF0E75CDF1B5760D4905FD3320A3BC9D968714D36B906552AA27B312F2F1F2DE22EB72BECA66FA4EBA905EF83EB1E439D51AFADA8E0657AE1E00F2EA1B4D5414D75499DE7DBE89D92E402DD56E17816526D63FBF8614D377FB09DB4B766AEBF19E523931CF6FB20527
	43C3816554EDBC26D96761C361493BA0201CDADE34263CFFF505572775A14BADD449E37E6B5774AC439248CB8548GA8B81512D789E08410CE217FDAFAB4E4EC44BF8118CAEC7467GFD27562E2F9C8E69C3CEE95AD2B37A3138D3E8CB5F8960B5B59D9E8B29C32ACA8796FBBD35C799FBE51CE2EB56C2CFCADE1987E32F8D86545360C1BDAF6C8ECF4FD9816554FACEF8345AEA158EF5184DF568300427433100F2EA9D12B5F5F8C10FBA2C66BA6408616970FAC0B9358EC5DEAD9D7E5C08BAD4FAD1077B435461
	FBA1F5F0F8354A39864213F7A6201CDADE24263C8F2D61495B96D0CEADEF1E263CA7431477DCC8F989AAF9ACA65D66A4E73FC31F51E897A5B35989E3A7E25978B56ED61F61CD869969G6BA49C3F5770EF940BA4BC6F23275F1B19A06DG08145016EE3705D77787824A85343524D56E14BDBCF9E7834A152B5BDAD31E7EE4F872BA824A2965A52B6431394CC14F472D06BCE1987A832092209BC082089946392C3007FCC7B05539AC743A7159B06B795B1075CC9C56AAF77DE0F872EE8FA827361B651A721C0E70
	64CD8414D357AFCDD3DE65D0F8729A834A296BA70CE8157BD31872F21D2164952A64B19F11D80773CA45887A02EF85701A1A8E49A1F530E96A700BFA54A1827259B712CB286FCA04F4B247DDCF6952A11DBB0A3A6E2567664AF85AC453861ECE5D532D52E12AC0F80622E1B2FE9F360C1CA950B0BF716E31605F30FCF4CC7B9B16CF0FE9FF43326D586B8CCF8E96A9F6C4B13F7A58645F38680FE9FD63728E2D4F9359E4FE718CBBA57B10C8453239CE9CA35FD001B7BBB86CE42E0EC8762A774885741F71B6520F
	36D17413CF9E5BAE847AC972225865C1A6A6516C6998986081FF27DB6CC71D9FBD9E3C7D9EBF2E5DFEBFBD2E5DFEBFBBBEF97BBDFFBCD87BB1FB39CCD7823F11D0379800C5G49GE9G39E3E06BGBA00EEG77985AF1ACAD93856978B176DDG7956001C2BA7E7074939E0F2D6A0F90C1DE51757488D31328D911EFC6C0EB25E416C8DF5D263A3D5385CA0BCF4AAFCDA10F1641669182EEF69D0575794B4F9827B86AF182E789DDF28F1956C9B6057D569D7025061770572BB1F5E6036D45B2BEDCB7ADEEDDB7A
	3CF7F2DB6A688DE6CBEC3C7DDF096896707D58FE089884BC237A0716BC1946A8390C5BB96FA8F370FA4CB2EB5763DAB3E9CB2FEB5853AF2AF50DB59772BCDF04C2B6D709BA7277F3343CE476D845003AAE2265D9DE30728F50EF5A7A6C963BC77E120A55F1C92B320E1582465856E652CE9A7B3E1D7D9E1743C1C2EA815744B78D2BE3EAB20F2C81BB9546B126FE76904654056398D3FF679871F9C6C85BGA246F17C5DF31C4C07B1635A769FAF6317A872D91FBE7C56EB54A63CDE51CBB7E2C970AF5773123DD8
	4FE64955340D37DDD6FF703E4C6D576E4B0D7D417B724D7E50FD79EEFF703E7C28BFF4DF16778733EDD65766FD4ABE5D49712F34A95B4007FF548F3FDBC05FD295F097498AE87334A07D3211733B2ADD412F1D58940BC3DE5DCFECE0AD144FBF0D31C9B74595C13A6CB44ED9E31660FDF256225DA797DA0277C914A5F41F4C31846F13F91650FD3240B2F91F94F6A85BF897EF630F2A55EDDC876DE0BC9D2A0DD37D78795A78B0BDDBEB017292EF63A40A0B00F4B447EDB8C0F03110DEF49A4FF97C600052EEF6F0
	D9579DD44A5A4E711D1DCA7AED9C7F0169E565D82B6031FD40F90EA5A0FB052A2EE90A34FC5E501F5FB644479F7243EFC77CC057AB28570047B5841A31BE4A94035B55DFC5ED3BDABF905C2E7287C25B551B83416D2AE8A034DDFDB4206D37AB8FAB7B241A5BD3E3333A0D57C2FB96FC994A1EB28F7B3565CE1C87727E9664FDB47D58177976B79AF09D3265CB746D3F266792FAA0AD81C4FD093EBD5F90F026877292C36A17546D275FAE3E0724D373CB86DE2BC2723B486247EFB7724B542B79E5G2F5C09D07C
	EAC4BFFED548AF3BC94D2F88F8D5046477EBFFFE55083FF9C0095FDE0178AF2CFEF8BEC6B65A143E1878FCB6E72F552B4F9098C0A79B4044844E59B3F7633B1B5993582F3751BB4112A13D9AE06D84766B3C20F7C79158FCA6303E6CDC4B3DF4DF2D648CBB571245715F2278CA851E1DF7791C62C9DA87F9F1E750F77D0F321ECDBE5F35502E2C7FEE0E9FBB6967A340033271B6E28D6EA34ED835FD44855660BE6292EBE89FB14B9A5CC74C35067691B7D9A71FFB5E9ED436CD95472FD0625BFD7EFB01434F76F6
	A0FEF0488FDF0978EDCEBF5B5B4E5F5755E959C46ED77E36D074155A5F7F5E1F5F8E7E7E3BDE0BDF050A1FA1005F1335640E8707F95CB5AC8942522F28FF6267173BA16D860881087F0A5DAD45FC5F5D2344C686BC66A1E8CE3AA80EBDCB3F112D47327910AE8650817482C41CC5FB3C371A145D5B0907DF3749636EC23FFB85524EB2F98591B0AFC7E07C3348762A32BF7D56A405A2E90B3DED66BE5FDD8DC69713A542F2488C6BFA44773DCA7870139A9DE04EBEBEF4AF0267314324F25EB3F51B14F9C651B3E4
	77FAFDF30FB52BC0192F77745B6CA3A24B0B704FB38E77B96CDE9B4B1368B9DD1A57649AA2EBB706FF79303F7EBBE4B92303FEBAB60F39DD9EC9E0E7C71F727BC621AE023D076B0D4033258F79FDCBA5497964D9AEEF88164FFC5FCF0751774D0BC05F77A27D0EE0852185D8F79E7A3E34007744FE76FDDEB05F3773B861D7F11E7A1CEA83456B4ED31FD37D6FE3F8CE5588F98967E1AC77B0BD6BD5F8BE445067A32E78846E7955F15C3EDE02A35F5476F2DC87BD37938969980E1BC5F17110CEBA9F6DF94BFE7C
	8EA76DFCC2D3C3534BF83E8B7337337C8BE93A6C8264D9D70DF2F49CF7E1B54AB1DD00FC1CC61453FB8139CBF30238D9599EFE0CF9BB180C67E9BA0E17FD77003F8E15B43D144BBBC277C6D7C3BA136366D3DCFEE4CADED1A40F0D695EEE89248D9CF7043EFBB6C25A42F173A9EE8C52B3A351967E56203605591037B0B278FC4F7A3C0F1E6FCC0AE4FD1E4771AE0ADF29404BE739E9FCC552991057FDA16A74A1FD771C8469659CE7253855104E603835747D30F0D1CA5E660B9057CC5F5716C03A0A63DEEBC4BA
	8347F5B6A2DDB7477D3D8969AC9CF72C896946880E7A3F1DB62D3D02C8480F3988630E4AC65CABD8F4910E3D6BF6117A26C1DA1881F4B3303DAF5B2DEE6F0A993E36B640F3778C7F36E76D7AEEAD616718219EFBA594BFB1C3BD76965560580B023C7499D8474535B84ECCB3D127EF12CF6E64B9EB2962393DAE5C7D6E29G13B1F0CEE530BA2F2063A3826A9BBD935B211A3EE3188769C40EFBE98F52AD65B86B9E24DBBE933F4FFA0B47F429005BFCB14E3F5BFA7D3E65A7DF995454FC6E31DB9A4D56B6BA1BB3
	FBB9CE75A1655BAEC6FB0922BCA3A1BD8FA061E27C0EF76CE37CC6BF7E6679C24C855A77B32CB21D49CE3E1859CD3A7CCB4E36C7BA1F4EE6B21F2AC719E517004DDFC2F039FB14F38A3115CBF3587BB216DF6317CFE2B6F23F1DAF5F6117CF747DD916B27F863FFC52C67DF2F9D637C7BE423A2DFDE4116038B7B2681D3F711730BAAD17FF23D7DF1B35E86B17B92C5D2FAB8FF207820FE6D89BBCC457FDE46E4E3D945BFD0DC467AEC8F78338AF453EFCE1A048BD9BB412E17CDE277E0768BC13173BD28A268BC6
	8B4C6ED78D2B7D401ACBFD63DE08823B0A52728303741B3D12A8B65E57F37C884557A97072B7087CDBC2A364A5C421DD7F75907A01424B508F549E276FE2F83A1926495DAC72D978E367EA2C7AE47E7C1C494891F0DEDEF5DC799DB41BFBDF24F51DGBDD84C3C7B847A7DE500CB85D88BD0F4B944A7GACA67F3297E9FAGA781ACA2F9G244D64BDA79AB38CB945A117535E3F066489E3B4DEF998A075F2241BD7CD3F9F03459A0BA31ACF89DCDF63FEB276BE20C76A4B9DF62758A90DBB448F78B82A97155F3F
	D4FC5996747B170AAB18CD69645F35EF4E4D12774C683E6687FA2755A1DABEE0FA1E243ABF836086FA26576DE429A74BFD6C28D2EE41DCC7F6D6903991323C28ABFC3E09497D2B115CABC66227F2D8B16E97C76935E2A525D8773D8714799D8E0BAA5FDD9B583EB3A86EE7GCB2E4076FD20563FFDB7C93E76FD370E2EDF64B350C3C3E62785520C4EB66C23F3B7EB482238C7G167BE4B57B4B3AF40470E0632E3C81631DD5D760D89CED60714E95B88EECBA32AE91220186208540910DF943B40F5CBD55AB5F99
	C56E2B124F95B4A8478FA977F5460CA0175F62A7FF13CEA9C36B3D5CF447A54E8FF93C2D1B9B825B3A0D460597G4C0C46363E3C413F2D6F9C757557A6FDA00FCFA86EC600984E23C76F4F63C764FAA10151D751586EDA000551B886761F7665B7521871D200C41EFF6ACB124F6608A19B2E4B16C5631C333E1178E461CA703DG95D7E23BD6B7CE36AEEB39B2583A6C355DF46F72CAF5EC78F70AF7DC290E8DEF5C05316184642D3A926DB00E6E1F992E023970AA9E1FD763BCB141F17B297F0A05F442D5287B96
	BA3E1671F4B9CD137B35D87A3E1A2978F5D2BE9C9B124F48CEC946D44EE2E84166D9246EBF28AD46365750D51845F04C2EFED967EFD737CD78EC53D89F5C677C107EFD86761C7E156F196CF270F3781AFBD5215B6813B04B75CF3B9C33416374FE2E1ED96A7DD2AB455BE62977CB2F62313584F9ABE761B7AFFF90E8BCB69BEC93408DB093E0A1405259388F75DF2E8AA14499BB134B5DA60E088E45ABF8D63FE7B4776AA683B687FEE7834630898F5EA6A4DEE488FA07564A594CB6B2655F12E72F4DA23F2CFE97
	E7137A154C0175A5008DA082E0B6C07C9C2C5FAB6EB2D6BFF29CC6AFC966FE9B3DB90D9F2E310B23BEB459GE4F57BAEDD63AF1993E82FABG178630D611D7F0F5CA9E6B235EE15297DB2ED6471A0394DFF535BA567C1D0431E69B64AD3A9A477EDBFA2DF139799A399D645F3AEB7C75B3DE236D8BAC009FD364B11F74A25D7F0A82FC6CB56827C6C651F7AD653843639487695C98546F54GFD278B698A0E1B20381698F6C718FC96FC605F911B9299C41676B8AB7A3748E0EB11C07C22983AFE6DB248E91B069E
	B8D7A0CCGCDB4401BF2BAAE46B7E619AFEB6AA77B55B4BC656FF1DEBD2103DF27B8DAEC90874C438E095F52D322C0368FCB74429D79121F128F9CAE338556CB9F14C91EE13134144D2147CE607B3A4C98DC3B4EB921DE3BFE6A37F67D6F936A35AB5157177F47936A356BA3594A7CCFFB55EBD7AC4FEC7D26DE1CC3AB2E0571FAAD2E7FD6B6929F24DE7F51DBAFEAEA94CD5169F74B9C2B67AFFB79BDDE57E22C6026F71D2C04F4BAC06EDC0CD92E28A13FDBADDBE5BF94BB2A0C452BDEBEDF739EE78245F3D91F
	D74E752D81188FFAA1074CD70E3968EFE61EA2EB0CAE734940D3F800A4E71459EDDA586767D5D31DDCDBE51F76ACE1A73F977A0D6723E03C40F36D0526E237EE194CD6EED7B754D2FE6E2D18EE26E9F12B9CFB755098F186C044DC0C3D1A08A962BB49B162D3779A35AF32E1B41147485FA301D53D46A5BA48C7AAA16F14776AB5AF2D41771CBB7D5FB1B0DA1657EF20FA6B319EFABDCD9BB15D51C853ED18BE52C4534DDB71DB3D00337751F3FD63B68E1E9367EA6D950C53B8B3F92E5AFFC7D27C2A39EA7F7DCA
	A37A6F8C486B3E8EFD2631917DE852F5688B5F5CCD633BF918FE0F26491DBAACFD4B2E497C98A977F57C9B1331E4D2996146F94A721B04EF6E8EFC9717423F49A806FAD4817482C873581DC2EC7F20269AE922G9F8B3084E09500F0BD0EE1310654035DB9E611EDFA228677690AB9DD43EEE223C1BE931157993F63F73ED672F21F54915D1BC4F21F8746AB23A246ABC5375FA4943D274BC65F282E3F63FA56C7B1577B566C3E363710CF606567A145F34CFFE04F786EF3AAC3BEDFA384FF2F7ED21676FB757CAC
	6D331FEFE4CDFE4E7A6D2C60FFCB607DAC655F92E0FE35FB83191B1221DF52G72EFC849AB86283C817D76DDD944EF331BA354B1E23345CA5E6B133F3CA4217C2B320357755A6C50FF2FE9FEB6F97F5D6712A45790610F780574EF90C9AEB7C5B25C9D59240DBC64CEA5C60AFFFBA91B582FC39C20E866F7FEC27D3B916A653881FD51D374BDD08424E3B96EE3BD7A27840E2B1388EE5DB720ED3E01389994D7896936EF201F9A20F1E3AF24A50EBBC5F1640E8A83BFAF12A9CD5566881F0DB24F6959756B43D315
	01FFB3E7BABE0E35C195ED2B1920DB8CEF176BA9AE8E52C99C7786FDC7319C52999CB7C0F13931100E6571B475D7D510B67AF0D574CC9F24A3E35197ED9C8974239F0D200F0C09E5E79A96416F52D8F6BF08F0A32139EC34D826C90D8DEC4F74D865FA5BB9CE56DEC3F627FDE8F82853FEDA14FD6AC5B466EF81FE269BD11FBB5F751DCB407777EBF752777D3E93435B583B77BB87546F6E09FE91B7627B3F4F3B78372B00FBCFE0ABF57AFE117ECBEF504231F865B2F275166C1BFF162F744D06A78D599F8A5A3E
	B979C6D66F2279BE5F1CAF9567B0BB6ABB210C97CCC0E31BBF39AD32F2377606DA83CF2878485F2706AC277EBE0815CB77AB9793C247787938AEDDCDE362A57355FB8A3F227864796ABD0591AB6EA92C043C3A1BF0CF61BCBAE746C2BA69A6741D069C72F7FD02453CF82FAC43522DE859FF3D18C36C5B6B978FFB9BE89C5734D59EFB97533D40D41015FE930E155C13683BB647A1AE72A42ED1F5F168FF4E52984C84E99B47DDC2F191F16C1BD05907B4AAFD88211D6E7CE71A240C3C0764A7E33AFF3FAB145FE2
	EBD4EBA32ED1E3B574D12FD19300E6D91CEF0D1A9A27DE233E5CCE47BB0D1936615FE45C0DF6118674458BF08D7773F23FBBE458FB5CC697795BAE6CB52E9CF3B5D6E12C563D002F572A884FE8C8AF84D832GFDC4AF3F53E46582DC4346D42B5730646F0D158B38867DC17564EBD87F3B30098F79115F7B57721AE0EBD856A633EA88EE406C707A6EEEFB516FDCE4414DE013B7CFEEC3ECACA5549543EF95501AG46G228162G12G1683245D0CE3EC46CBA5130CB15650B4C671186D783704FAB7840FD12C9B
	C247A86E8D74EF8DC0ECE19AF74B679007B710B16B9A98700ABC96BA3D210063DA3DC61717AE76D8FB25B7507BC16DA3F6E2892EFE76378C589C13C763120732A166388567185F6A483BAB17D3B439067BED4D747EE676719BAB730A9E4F0DEC06B24C6E6B75E877DBEE5136FB256DD781CD5BADBE3B6F3DC5ED771D9229CF3F4B89A167B0AC021981B3798E8965BB384EB7C8E48EF5F948450C4A7A5DA7212DCFF0DAFD2BEFCC619C1AD16643ED47752D6A7DCA4F9E016B172027F336F9CCB5E7A75336D9843212
	383C52BA524FB6D06EB454476CB081C76CA1ECD3830DD148D8C863654E5077FBEE0FB8A0FABC2205CAC17A2BA97D6605B07E9772B3DDB5C162889A6B3481DD370F36EA131FCEFA075D6AA49137DCE6A67791118B717CC87B887A1D84402DD908BEE24E8852EE39957D49FFD15A9EC8CBGD137A26DBF46795DA23772F74D63649762EDB91EB8D9200CA7D2473B022E75D2EFE53616FB1B7FDA0F7557779BFC7D7514C846B959426397E99A6CD0DFF25C32E45B765DE54515BBAB64FD3B6786D0C75DEDB8BF35795D
	41B28678285B2664DFF4E4DC4482ED9240AA00CC007C5B41BF81D481348174820C81C4834481A482A483248224832C0BC79F14D2D3BA098F128F6D781C10007173B85D0BAC84BEE43F10613E22388A4011BDC876ED5712CD44666CCE723A3E097D55DA4EE303195A9150DB62F14E5FE425F1B9246362312DFEC13642E43F7C0262F942FA6E5758F34E298C5687975512E73EAEE661141CF7993DFF1E5F51CBDD915BEDE4F698EB8BDCCF5C4F4F6AA546337544F278DD9D4F56F1ECBD715E49E219E6EDFC205D955C
	41F0EC6C5EC16330AD002B38033F5B5F6DDBB720FF2BBD2BDAB7EC176F87EFD42F9B089EBDF760980E61F73D386F186E3AA13A51E78FC2D5F86F74B606D96E73E9171363F69A9BAD3CA3A0EE277804BB826276AA7413CBA0EF4B1DB8AEC43FEF50E682FE5E1D137B90B646B6566158FD110F5DCDF5B8F62F4D61FF0B31AEF82C717D3A503146E3F55A7BA10F57856E07BCD5273D9F322ACE399F42F04F49FCBD7C43DF067FA51D5F58D840DB4B51DFBF21A33FBBF6D66037EEECAE768C4BE7621C7C4E23C5F7CE57
	8697B789DC4E859272E5699FCAE43F5F6F6FA77053F865DD4D87CD2DCD7A36931D4636D61300734C93869F3F8DF5E44D98E41E5935332CF8FB85BF83F414461ECAD91D64FF5C6672169FEC7D3C4544E47C3159A7232DE6AAB2D839224F95652A262E5BB72A5E1FDC3726667D266603FCECF50E04B7A64DE1161BBB6DF2EC4C6E9E25671B6FD40FE59D45E75C299E4B8F7318C7388B663D3BF0AC970FAB65AF83FC6ADDD3098732DAC81F2783ED7EA2704FGADGBDG9240CC0058C5B8575F38E732B5BDB7CB45EC
	AF4F3F8177D991C8DAA41FC5DD9428A773A97DDD68E732B84E5605FE066018DE4BF4417D4CE33A50FEE625CE5B4F7C9FDD201FF9D6276DE71E57857A19DF6B08BF30C28C544D69DE6038A39C17C9F9925CE13BC532F17CDF28DE845FC2AF4FC77CBABD51EB14623AE5DC1E82F7843FD75713399D2A8CE1B5165DC47D56005D61E0693FD19A3B9316E93CBD0A74C4C73B5B2C507BFD0EA3FA33753D7963FC014796D4F506AF9AFB5507C755710CA03DA741639AEDB73B5D543F3218E31799E30E55E08FEB812A6E06
	D874EE653E64A76CFBBB659BDF81E30CA6AB2E41ACF7637A60999BBF838338783BB1DE58EA4775416A3BF1FDF0087E7D0622FBD272AA815A6E61FB12A7512FDB6E4158656C4917383D20DFFB3CD560FA266949188A965BD7EDABAB5ED101317D8BBCF619F78F0E656B7D4E2F2DGFC5ABDD359F77C85F9F50DF12E62F9226ABF90671ED399EC6CCFD423BF581A0538732A519F5C48F5717AFD4B3B965A2EE071D45A2FFE5457E61FA91E9F9BB9776D17F6CEE530365ABD0E6D77C35EFEF56358FEE46FC3FE7F5692
	5C1FEEEA896DCF0BDA347D6987AD017EF4EB0B36BF7DE7CB20BF5D5E427E9E07954611B2EE2BEA510A5BD87D3228DF58827DDF39183FFF3162DE31EEB17A0A63E674955100DB369877B09FAD1C649C8A561C79B836A554E46E3749B1614DE6BC33AAA4201F71506F8B2AA0ED8430A520CF7942CAE4EF637670DC40D9052959518BE116AB8B38A7B09430B865E16AB7A3925471CB8A45C7A528631723F6EC679848DB1B4077B37C6E536B3D97620FFB27B2E6B3B54F7A4E144B4E536041ECA021B1B036F820D199DB
	B05CA57AC03AAB74CABA56FF05D67F3D394C3D245B59DE6499AB6EC5AE5250C73D973994E8883CA963D75E6BEBC3794C00B93CBE7604D96EF6DAEE75A47D1189F979777972198D7C1C5AFA71FDEA5B788D45175D27360D3181340DAA481BF79F5A46E3F6016B32D271EC361FFB3FFC7A1C4A10635C1301FE59F692ED6C8827FB0DFDEBF9BFBC5F0F3ECCEAC2DFD6F4BF46ACF7DAB126F05C2F35964518659FED82677DDD6A2B6200BE1173FE3B0F6016C1FA9547FDA292DC8651A1917BE74D002FCFFE2FF87E6A5F
	F286F3727E39661C4AE0E37B63A67F315D311F54110D6DCB1BF0ECD7A6069E5BADC0C3G47F6CF22FAECAF763B9B578679939AFC83F57C09B970DB2CF4B351BBBA916718EDFDE4BF5E63F2C9DDF62F1D3CC0E131F3EB9F464E2BB82DDEA43C55FF4317456ADF08982B27A7326F3548F36E834A6F68131A50860B9F184C86BD9DBE9B7C01B55057BEE68D7435AF7511BBE043BF93CB5F5415167A7AD744BD9766074E97518FF5036E518F605C7DED0ADF8669D5G198F703B487D6EFAA9F890464A03D3193BE6865C
	ABBDB51B3FFA5A65185F5D41E20F87557E380E628D8F2A7D7177F8BB98A1AF61C17481AB1B827B6969A6E5BF495F61A8FCC123627991715C7B055567D4465F285DEC9AA84D960478DF896BA561F57667A3653F34C87E1A2EA43C44EE571004F5648A8EC9F859F72306AA71923B63C2928A64832F456C2F361617838A4F5D964BA7E6A5E1A3BBE66BC33C0145E48CAD38C13E20436FC93E18C37934D13E1423F854AE594A4B3D9E61B53EEEED259B832BC6F65464BCFBD3CE36302E95C69F8D6CDB05F5A4BAEF9556
	53603DD5480140FE4C8D8D33CE8182D4149F382C8156EF84AE0975177F64497893064D59C2B6F037090C5D7AD13221C098E6FD97781C751E923E22E7897D58EBE257D1ACF9E7CC5E9B6827124AA2A36ADEE8C82EECB17EADBDED911270F6277C87400BFDE5BA0707064C1EF16DA2827FBBEBE715E74CB3AF6C8FFA46FC6DC34C0F94FC4B77BE116D5BBDC14F4034815E76AD5C1F7B58C67E3EF83033611DC3E607C3B2BB8675CE8BB9A75EB8AE093E3313BF35D1FF0FB4F2A64BE3F7AC1DD41479DFD0CB8788F1B6
	E4F030AEGG0017GGD0CB818294G94G88G88GAAFBB0B6F1B6E4F030AEGG0017GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6AAEGGGG
**end of data**/
}
}