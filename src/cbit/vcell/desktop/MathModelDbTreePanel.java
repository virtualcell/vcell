/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
package cbit.vcell.desktop;

import java.awt.BorderLayout;
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
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.desktop.DatabaseSearchPanel;
import cbit.vcell.client.desktop.DatabaseSearchPanel.SearchCriterion;
import cbit.vcell.client.desktop.biomodel.BioModelEditorSubPanel;
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
public class MathModelDbTreePanel extends BioModelEditorSubPanel {
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
	private MathModelDbTreeModel ivjMathModelDbTreeModel = null;
	private JPopupMenu ivjMathModelPopupMenu = null;
	private MathModelMetaDataPanel ivjMathModelMetaDataPanel = null;
	private JPanel bottomPanel = null;
	private JPanel topPanel = null;
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
	private DatabaseSearchPanel dbSearchPanel = null;

class IvjEventHandler implements DatabaseListener, java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.TreeModelListener, javax.swing.event.TreeSelectionListener {
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
			if (e.getSource() == getDatabaseSearchPanel()) {
				search(e.getActionCommand().equals(DatabaseSearchPanel.SEARCH_SHOW_ALL_COMMAND));
			}
		};
		public void databaseDelete(DatabaseEvent event) {
			if (event.getSource() == MathModelDbTreePanel.this.getDocumentManager()) 
				connEtoC14(event);
		};
		public void databaseInsert(DatabaseEvent event) {};
		public void databaseRefresh(DatabaseEvent event) {
			if (event.getSource() == MathModelDbTreePanel.this.getDocumentManager()) 
				connEtoC13(event);
		};
		public void databaseUpdate(DatabaseEvent event) {
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

	private boolean bShowMetadata = true;
	public MathModelDbTreePanel() {
		this(true);
	}
	
/**
 * BioModelTreePanel constructor comment.
 */
public MathModelDbTreePanel(boolean bMetadata) {
	super();
	this.bShowMetadata = bMetadata;
	initialize();
}

public void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length == 0 || selectedObjects.length > 1) {
		getJTree1().clearSelection();
	} else {
		if (selectedObjects[0] instanceof MathModelInfo) {
			BioModelNode node = ((BioModelNode)getMathModelDbTreeModel().getRoot()).findNodeByUserObject(selectedObjects[0]);
			if (node != null) {
				getJTree1().setSelectionPath(new TreePath(node.getPath()));
			}
		} else {
			getJTree1().clearSelection();
		}
	}
	
}

/**
 * Comment
 */
private void actionsOnClick(MouseEvent mouseEvent) {
	if (mouseEvent.getClickCount() == 2 && getSelectedVersionInfo() instanceof MathModelInfo) {
		fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, DatabaseWindowManager.BM_MM_GM_DOUBLE_CLICK_ACTION));
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
private void connEtoC13(DatabaseEvent arg1) {
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
private void connEtoC2(DocumentManager value) {
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
 * connEtoC21:  (selectedVersionInfo1.this --> MathModelDbTreePanel.anotherEditionMenuItemEnable(Lcbit.sql.VersionInfo;)V)
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
private void connEtoM1(VersionInfo value) {
	try {
		// user code begin {1}
		// user code end
		getMathModelMetaDataPanel().setMathModelInfo((MathModelInfo)getselectedVersionInfo1());
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
private void documentManager_DatabaseDelete(DatabaseEvent event) {
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
private void documentManager_DatabaseUpdate(DatabaseEvent event) {
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
private void expandTreeToOwner() throws DataAccessException {
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
			bottomPanel.setMinimumSize(new java.awt.Dimension(171, 300));

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			bottomPanel.add(new JLabel("Selected MathModel Summary"), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
			constraintsJScrollPane2.gridx = 0; constraintsJScrollPane2.gridy = 1;
			constraintsJScrollPane2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane2.weightx = 1.0;
			constraintsJScrollPane2.weighty = 1.0;
			constraintsJScrollPane2.insets = new java.awt.Insets(0, 4, 4, 4);
			bottomPanel.add(getMathModelMetaDataPanel(), constraintsJScrollPane2);
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

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 0;
			constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
			topPanel.add(new JLabel("MathModel Database"), constraintsJLabel3);

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(0, 4, 4, 4);
			topPanel.add(getJScrollPane1(), constraintsJScrollPane1);
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
private javax.swing.tree.TreeCellRenderer getMathModelCellRenderer() {
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
			ivjMathModelDbTreeModel = new MathModelDbTreeModel();
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
			ivjMathModelMetaDataPanel = new MathModelMetaDataPanel();
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
 	Vector<MathModelInfo> mathModelBranchList = new Vector<MathModelInfo>();
 	for (int i = 0; i < mathModelInfos.length; i++) {
	 	MathModelInfo mathModelInfo = mathModelInfos[i];
	 	if (mathModelInfo.getVersion().getBranchID().equals(thisMathModelInfo.getVersion().getBranchID())) {
		 	mathModelBranchList.add(mathModelInfo);
	 	}
 	}

 	if (mathModelBranchList.size() == 0) {
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
		setName("MathModelTreePanel");
		setLayout(new BorderLayout());
//		setPreferredSize(new java.awt.Dimension(200, 150));
//		setSize(240, 453);
//		setMinimumSize(new java.awt.Dimension(198, 148));
		
		add(getDatabaseSearchPanel(), BorderLayout.NORTH);
		if (bShowMetadata) {
			JSplitPane splitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			splitPane.setDividerLocation(350);
			splitPane.setResizeWeight(0.6);
			splitPane.setTopComponent(getTopPanel());
			splitPane.setBottomComponent(getBottomPanel());
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


public void refresh(ArrayList<SearchCriterion> newFilterList) throws DataAccessException{
	getMathModelDbTreeModel().refreshTree(newFilterList);
	expandTreeToOwner();
}

/**
 * 
 * @exception org.vcell.util.DataAccessException The exception description.
 */
private void refresh() throws DataAccessException{
	getMathModelDbTreeModel().refreshTree();
	getJTree1().setCellRenderer(this.getMathModelCellRenderer());
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
			connEtoC2(ivjDocumentManager);
			getJTree1().setCellRenderer(this.getMathModelCellRenderer());
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
	if (object instanceof VersionInfo){
		setSelectedVersionInfo((VersionInfo)object);
	}else if (object instanceof VCDocumentInfoNode && bioModelNode.getChildCount()>0 && ((BioModelNode)bioModelNode.getChildAt(0)).getUserObject() instanceof MathModelInfo){
		MathModelInfo mathModelInfo = (MathModelInfo)((BioModelNode)bioModelNode.getChildAt(0)).getUserObject();
		setSelectedVersionInfo(mathModelInfo);
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