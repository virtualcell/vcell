package cbit.vcell.desktop;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.*;
import cbit.vcell.server.*;
import cbit.util.DataAccessException;
import cbit.util.Matchable;
import cbit.util.document.BioModelInfo;
import cbit.util.document.User;
import cbit.util.document.Version;
import cbit.util.document.VersionInfo;

import javax.swing.tree.*;
import java.lang.reflect.*;
import cbit.vcell.biomodel.*;
import javax.swing.*;
import java.awt.event.*;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 11:34:01 AM)
 * @author: Jim Schaff
 */
public class GeometryTreePanel extends JPanel {
	private JTree ivjJTree1 = null;
	private cbit.vcell.client.database.DocumentManager fieldDocumentManager = null;
	private boolean ivjConnPtoP2Aligning = false;
	private cbit.vcell.client.database.DocumentManager ivjDocumentManager = null;
	private JScrollPane ivjJScrollPane1 = null;
	private VersionInfo fieldSelectedVersionInfo = null;
	private boolean ivjConnPtoP4Aligning = false;
	private TreeSelectionModel ivjselectionModel1 = null;
	private JLabel ivjJLabel1 = null;
	private JMenuItem ivjJMenuItemDelete = null;
	private JMenuItem ivjJMenuItemNew = null;
	private JMenuItem ivjJMenuItemOpen = null;
	private JSeparator ivjJSeparator1 = null;
	private JSeparator ivjJSeparator2 = null;
	protected transient java.awt.event.ActionListener aActionListener = null;
	private GeometryCellRenderer ivjgeometryCellRenderer = null;
	private VariableHeightLayoutCache ivjLocalSelectionModelVariableHeightLayoutCache = null;
	private boolean fieldPopupMenuDisabled = false;
	private JPopupMenu ivjGeometryPopupMenu = null;
	private GeometryDbTreeModel ivjGeometryDbTreeModel = null;
	private boolean ivjConnPtoP3Aligning = false;
	private GeometryMetaDataPanel ivjgeometryMetaDataPanel = null;
	private JPanel ivjJPanel1 = null;
	private VersionInfo ivjselectedVersionInfo1 = null;
	private JLabel ivjJLabel2 = null;
	private JPanel ivjJPanel2 = null;
	private JScrollPane ivjJScrollPane2 = null;
	private JSplitPane ivjJSplitPane1 = null;
	private JMenuItem ivjJMenuItemPermission = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JLabel ivjJLabel3 = null;
	private JPanel ivjJPanel3 = null;
	private JMenuItem ivjJMenuItemGeomRefs = null;

class IvjEventHandler implements cbit.vcell.client.database.DatabaseListener, java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.TreeModelListener, javax.swing.event.TreeSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GeometryTreePanel.this.getJMenuItemOpen()) 
				connEtoC4(e);
			if (e.getSource() == GeometryTreePanel.this.getJMenuItemDelete()) 
				connEtoC5(e);
			if (e.getSource() == GeometryTreePanel.this.getJMenuItemNew()) 
				connEtoC6(e);
			if (e.getSource() == GeometryTreePanel.this.getJMenuItemPermission()) 
				connEtoC8(e);
			if (e.getSource() == GeometryTreePanel.this.getJMenuItemGeomRefs()) 
				connEtoC15(e);
		};
		public void databaseDelete(cbit.vcell.client.database.DatabaseEvent event) {
			if (event.getSource() == GeometryTreePanel.this.getDocumentManager()) 
				connEtoC13(event);
		};
		public void databaseInsert(cbit.vcell.client.database.DatabaseEvent event) {};
		public void databaseRefresh(cbit.vcell.client.database.DatabaseEvent event) {
			if (event.getSource() == GeometryTreePanel.this.getDocumentManager()) 
				connEtoC7(event);
		};
		public void databaseUpdate(cbit.vcell.client.database.DatabaseEvent event) {
			if (event.getSource() == GeometryTreePanel.this.getDocumentManager()) 
				connEtoC9(event);
			if (event.getSource() == GeometryTreePanel.this.getDocumentManager()) 
				connEtoM10(event);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == GeometryTreePanel.this.getJTree1()) 
				connEtoC2(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == GeometryTreePanel.this && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == GeometryTreePanel.this.getJTree1() && (evt.getPropertyName().equals("selectionModel"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == GeometryTreePanel.this.getGeometryDbTreeModel() && (evt.getPropertyName().equals("latestOnly"))) 
				connEtoC3(evt);
			if (evt.getSource() == GeometryTreePanel.this && (evt.getPropertyName().equals("selectedVersionInfo"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == GeometryTreePanel.this && (evt.getPropertyName().equals("documentManager"))) 
				connEtoM11(evt);
		};
		public void treeNodesChanged(javax.swing.event.TreeModelEvent e) {
			if (e.getSource() == GeometryTreePanel.this.getGeometryDbTreeModel()) 
				connEtoC10(e);
		};
		public void treeNodesInserted(javax.swing.event.TreeModelEvent e) {};
		public void treeNodesRemoved(javax.swing.event.TreeModelEvent e) {};
		public void treeStructureChanged(javax.swing.event.TreeModelEvent e) {};
		public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
			if (e.getSource() == GeometryTreePanel.this.getselectionModel1()) 
				connEtoC1();
		};
	};

/**
 * BioModelTreePanel constructor comment.
 */
public GeometryTreePanel() {
	super();
	initialize();
}

/**
 * BioModelTreePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public GeometryTreePanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * BioModelTreePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public GeometryTreePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * BioModelTreePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public GeometryTreePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * Comment
 */
private void actionsOnClick(MouseEvent mouseEvent) {
	if (mouseEvent.getClickCount() == 2 && getSelectedVersionInfo() instanceof GeometryInfo) {
		fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, cbit.vcell.client.DatabaseWindowManager.BM_MM_GM_DOUBLE_CLICK_ACTION));
		return;
	}
	if (SwingUtilities.isRightMouseButton(mouseEvent) && (! getPopupMenuDisabled()) && getSelectedVersionInfo() instanceof GeometryInfo) {
		Version version = getSelectedVersionInfo().getVersion();
		boolean isOwner = version.getOwner().compareEqual(getDocumentManager().getUser());
		getJMenuItemPermission().setEnabled(isOwner);
		getJMenuItemDelete().setEnabled(isOwner);
		getJMenuItemGeomRefs().setEnabled(isOwner);
		getGeometryPopupMenu().show(getJTree1(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
	}
}


public void addActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.add(aActionListener, newListener);
	return;
}


/**
 * connEtoC1:  (selectionModel1.treeSelection. --> GeometryTreePanel.TreeSelectionEvents()V)
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
 * connEtoC10:  (GeometryDbTreeModel.treeModel.treeNodesChanged(javax.swing.event.TreeModelEvent) --> GeometryTreePanel.TreeSelectionEvents()V)
 * @param arg1 javax.swing.event.TreeModelEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(javax.swing.event.TreeModelEvent arg1) {
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
 * connEtoC11:  (GeometryTreePanel.initialize() --> GeometryTreePanel.enableToolTips(Ljavax.swing.JTree;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11() {
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
 * connEtoC12:  (DocumentManager.this --> GeometryTreePanel.expandTreeToUser()V)
 * @param value cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(cbit.vcell.client.database.DocumentManager value) {
	try {
		// user code begin {1}
		// user code end
		this.expandTreeToUser();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC13:  (DocumentManager.database.databaseDelete(cbit.vcell.clientdb.DatabaseEvent) --> GeometryTreePanel.documentManager_DatabaseDelete(Lcbit.vcell.clientdb.DatabaseEvent;)V)
 * @param arg1 cbit.vcell.clientdb.DatabaseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(cbit.vcell.client.database.DatabaseEvent arg1) {
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
 * connEtoC14:  (GeometryTreePanel.initialize() --> GeometryTreePanel.splitPaneResizeWeight()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14() {
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
 * connEtoC15:  (JMenuItemGeomRefs.action.actionPerformed(java.awt.event.ActionEvent) --> GeometryTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
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
 * connEtoC2:  (JTree1.mouse.mouseClicked(java.awt.event.MouseEvent) --> GeometryTreePanel.showPopupMenu(QMouseEvent;Qjavax.swing.JPopupMenu;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.MouseEvent arg1) {
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
 * connEtoC3:  (GeometryDbTreeModel.latestOnly --> GeometryTreePanel.firePropertyChange(Ljava.lang.String;Ljava.lang.Object;Ljava.lang.Object;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
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
 * connEtoC4:  (JMenuItemOpen.action.actionPerformed(java.awt.event.ActionEvent) --> GeometryTreePanel.refireActionPerformed(QActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * connEtoC5:  (JMenuItemDelete.action.actionPerformed(java.awt.event.ActionEvent) --> GeometryTreePanel.refireActionPerformed(QActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
 * connEtoC6:  (JMenuItemNew.action.actionPerformed(java.awt.event.ActionEvent) --> GeometryTreePanel.refireActionPerformed(QActionEvent;)V)
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
 * connEtoC7:  (DocumentManager.database.databaseRefresh(cbit.vcell.clientdb.DatabaseEvent) --> GeometryTreePanel.refresh()V)
 * @param arg1 cbit.vcell.clientdb.DatabaseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(cbit.vcell.client.database.DatabaseEvent arg1) {
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
 * connEtoC8:  (JMenuItemPublish.action.actionPerformed(java.awt.event.ActionEvent) --> GeometryTreePanel.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
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
 * connEtoC9:  (DocumentManager.database.databaseUpdate(cbit.vcell.clientdb.DatabaseEvent) --> GeometryTreePanel.documentManager_DatabaseUpdate(Lcbit.vcell.clientdb.DatabaseEvent;)V)
 * @param arg1 cbit.vcell.clientdb.DatabaseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(cbit.vcell.client.database.DatabaseEvent arg1) {
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
 * connEtoM1:  (DocumentManager.this --> GeometryDbTreeModel.documentManager)
 * @param value cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.client.database.DocumentManager value) {
	try {
		// user code begin {1}
		// user code end
		if ((getDocumentManager() != null)) {
			getGeometryDbTreeModel().setDocumentManager(getDocumentManager());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM10:  (DocumentManager.database.databaseUpdate(cbit.vcell.clientdb.DatabaseEvent) --> geometryMetaDataPanel.geometryInfo)
 * @param arg1 cbit.vcell.clientdb.DatabaseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(cbit.vcell.client.database.DatabaseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getgeometryMetaDataPanel().setGeometryInfo(this.getSelectedGeometryInfo());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM11:  (GeometryTreePanel.documentManager --> geometryMetaDataPanel.documentManager)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getgeometryMetaDataPanel().setDocumentManager(this.getDocumentManager());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (GeometryTreePanel.initialize() --> JTree1.putClientProperty(Ljava.lang.Object;Ljava.lang.Object;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
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
 * connEtoM3:  (DocumentManager.this --> geometryMetaDataPanel.documentManager)
 * @param value cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.client.database.DocumentManager value) {
	try {
		// user code begin {1}
		// user code end
		if ((getDocumentManager() != null)) {
			getgeometryMetaDataPanel().setDocumentManager(getDocumentManager());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (selectedVersionInfo1.this --> geometryMetaDataPanel.geometryInfo)
 * @param value VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(VersionInfo value) {
	try {
		// user code begin {1}
		// user code end
		getgeometryMetaDataPanel().setGeometryInfo(this.getSelectedGeometryInfo());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM6:  (geometryCellRenderer.this --> JTree1.cellRenderer)
 * @param value cbit.vcell.desktop.GeometryCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(GeometryCellRenderer value) {
	try {
		// user code begin {1}
		// user code end
		if ((getgeometryCellRenderer() != null)) {
			getJTree1().setCellRenderer(getgeometryCellRenderer());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
public java.awt.Cursor connEtoM7_Value() {
	return java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR);
}


/**
 * connEtoM8:  (DocumentManager.this --> geometryCellRenderer.this)
 * @param value cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(cbit.vcell.client.database.DocumentManager value) {
	try {
		// user code begin {1}
		// user code end
		setgeometryCellRenderer(this.createCellRenderer());
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
		getJTree1().setModel(getGeometryDbTreeModel());
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
 * connPtoP3SetTarget:  (GeometryTreePanel.selectedVersionInfo <--> selectedVersionInfo1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setselectedVersionInfo1(this.getSelectedVersionInfo());
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
 * Comment
 */
public GeometryCellRenderer createCellRenderer() {
	if (getDocumentManager()!=null){
		return new GeometryCellRenderer(getDocumentManager().getUser());
	}else{
		return new GeometryCellRenderer(null);
	}
}


/**
 * Comment
 */
private void documentManager_DatabaseDelete(cbit.vcell.client.database.DatabaseEvent event) {
	if (event.getOldVersionInfo() instanceof GeometryInfo && getSelectedVersionInfo() instanceof GeometryInfo) {
		GeometryInfo selectedGeoInfo = (GeometryInfo)getSelectedVersionInfo();
		GeometryInfo eventGeoInfo = (GeometryInfo)event.getOldVersionInfo();
		if (eventGeoInfo.getVersion().getVersionKey().equals(selectedGeoInfo.getVersion().getVersionKey())){
			setSelectedVersionInfo(null);
			getJTree1().getSelectionModel().clearSelection();
		}		
	}
}


/**
 * Comment
 */
private void documentManager_DatabaseUpdate(cbit.vcell.client.database.DatabaseEvent event) {
	if (event.getNewVersionInfo() instanceof GeometryInfo && getSelectedVersionInfo() instanceof GeometryInfo) {
		GeometryInfo selectedGeoInfo = (GeometryInfo)getSelectedVersionInfo();
		GeometryInfo eventGeoInfo = (GeometryInfo)event.getNewVersionInfo();
		if (eventGeoInfo.getVersion().getVersionKey().equals(selectedGeoInfo.getVersion().getVersionKey())){
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
 * Comment
 */
public void expandTreeToUser() {
	//
	// expand tree up to and including the "Owner" subtree's first children
	//
	if (getDocumentManager()==null){
		return;
	}
	User currentUser = getDocumentManager().getUser();
	BioModelNode rootNode = (BioModelNode)getGeometryDbTreeModel().getRoot();
	BioModelNode currentUserNode = (BioModelNode)rootNode.findNodeByUserObject(currentUser);
	if (currentUserNode!=null){
		getJTree1().expandPath(new TreePath(((DefaultTreeModel)getGeometryDbTreeModel()).getPathToRoot(currentUserNode)));
	}
}


/**
 * Method to support listener events.
 */
protected void fireActionPerformed(java.awt.event.ActionEvent e) {
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
public cbit.vcell.client.database.DocumentManager getDocumentManager() {
	// user code begin {1}
	// user code end
	return ivjDocumentManager;
}

/**
 * Return the geometryCellRenderer property value.
 * @return cbit.vcell.desktop.GeometryCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometryCellRenderer getgeometryCellRenderer() {
	// user code begin {1}
	// user code end
	return ivjgeometryCellRenderer;
}


/**
 * Return the TreeModel property value.
 * @return javax.swing.tree.TreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometryDbTreeModel getGeometryDbTreeModel() {
	if (ivjGeometryDbTreeModel == null) {
		try {
			ivjGeometryDbTreeModel = new cbit.vcell.desktop.GeometryDbTreeModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometryDbTreeModel;
}

/**
 * Return the geometryMetaDataPanel property value.
 * @return cbit.vcell.desktop.GeometryMetaDataPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometryMetaDataPanel getgeometryMetaDataPanel() {
	if (ivjgeometryMetaDataPanel == null) {
		try {
			ivjgeometryMetaDataPanel = new cbit.vcell.desktop.GeometryMetaDataPanel();
			ivjgeometryMetaDataPanel.setName("geometryMetaDataPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjgeometryMetaDataPanel;
}


/**
 * Return the JPopupMenu1 property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getGeometryPopupMenu() {
	if (ivjGeometryPopupMenu == null) {
		try {
			ivjGeometryPopupMenu = new javax.swing.JPopupMenu();
			ivjGeometryPopupMenu.setName("GeometryPopupMenu");
			ivjGeometryPopupMenu.add(getJLabel1());
			ivjGeometryPopupMenu.add(getJSeparator1());
			ivjGeometryPopupMenu.add(getJMenuItemOpen());
			ivjGeometryPopupMenu.add(getJMenuItemDelete());
			ivjGeometryPopupMenu.add(getJMenuItemPermission());
			ivjGeometryPopupMenu.add(getJMenuItemGeomRefs());
			ivjGeometryPopupMenu.add(getJSeparator2());
			ivjGeometryPopupMenu.add(getJMenuItemNew());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometryPopupMenu;
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
			ivjJLabel1.setPreferredSize(new java.awt.Dimension(75, 20));
			ivjJLabel1.setText("  Geometry:");
			ivjJLabel1.setMaximumSize(new java.awt.Dimension(75, 20));
			ivjJLabel1.setMinimumSize(new java.awt.Dimension(75, 20));
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
			ivjJLabel2.setText("Selected Geometry Summary");
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
			ivjJLabel3.setText("Geometry Database");
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
 * Return the JMenuItemDelete property value.
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
 * Return the JMenuItemGeomRefs property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemGeomRefs() {
	if (ivjJMenuItemGeomRefs == null) {
		try {
			ivjJMenuItemGeomRefs = new javax.swing.JMenuItem();
			ivjJMenuItemGeomRefs.setName("JMenuItemGeomRefs");
			ivjJMenuItemGeomRefs.setMnemonic('p');
			ivjJMenuItemGeomRefs.setText("Models Using Geometry");
			ivjJMenuItemGeomRefs.setActionCommand("Models Using Geometry");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemGeomRefs;
}

/**
 * Return the JMenuItemNew property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemNew() {
	if (ivjJMenuItemNew == null) {
		try {
			ivjJMenuItemNew = new javax.swing.JMenuItem();
			ivjJMenuItemNew.setName("JMenuItemNew");
			ivjJMenuItemNew.setMnemonic('n');
			ivjJMenuItemNew.setText("Export");
			ivjJMenuItemNew.setActionCommand("Export");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemNew;
}

/**
 * Return the JMenuItemOpen property value.
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
			ivjJPanel1.setBounds(0, 0, 192, 43);
			getJPanel1().add(getgeometryMetaDataPanel(), "Center");
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
			ivjJPanel2.setMinimumSize(new java.awt.Dimension(174, 300));

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
			ivjJPanel3.setMinimumSize(new java.awt.Dimension(120, 450));

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
 * Return the JSeparator2 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator2() {
	if (ivjJSeparator2 == null) {
		try {
			ivjJSeparator2 = new javax.swing.JSeparator();
			ivjJSeparator2.setName("JSeparator2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator2;
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
			ivjJTree1 = new javax.swing.JTree();
			ivjJTree1.setName("JTree1");
			ivjJTree1.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("loading",false)));
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
	return getGeometryDbTreeModel().getLatestOnly();
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
public cbit.util.document.BioModelInfo getSelectedBioModelInfo(BioModelNode selectedBioModelNode) {
	if (selectedBioModelNode.getUserObject() instanceof BioModelInfo){
		return (BioModelInfo)selectedBioModelNode.getUserObject();
	}
	return null;
}


/**
 * Comment
 */
public cbit.vcell.geometry.GeometryInfo getSelectedGeometryInfo() throws DataAccessException {
	if (getselectedVersionInfo1() instanceof GeometryInfo){
		GeometryInfo geoInfo = (GeometryInfo)getselectedVersionInfo1();
		return geoInfo;
	}else{
		return null;
	}
}


/**
 * Comment
 */
public cbit.vcell.geometry.GeometryInfo getSelectedGeometryInfo(BioModelNode selectedBioModelNode) {
	if (selectedBioModelNode.getUserObject() instanceof BioModelInfo){
		return (GeometryInfo)selectedBioModelNode.getUserObject();
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
	getJMenuItemOpen().addActionListener(ivjEventHandler);
	getJMenuItemDelete().addActionListener(ivjEventHandler);
	getJMenuItemNew().addActionListener(ivjEventHandler);
	getGeometryDbTreeModel().addPropertyChangeListener(ivjEventHandler);
	getJMenuItemPermission().addActionListener(ivjEventHandler);
	getGeometryDbTreeModel().addTreeModelListener(ivjEventHandler);
	getJMenuItemGeomRefs().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP4SetTarget();
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
		setLayout(new java.awt.GridBagLayout());
		setSize(200, 458);

		java.awt.GridBagConstraints constraintsJSplitPane1 = new java.awt.GridBagConstraints();
		constraintsJSplitPane1.gridx = 0; constraintsJSplitPane1.gridy = 0;
		constraintsJSplitPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJSplitPane1.weightx = 1.0;
		constraintsJSplitPane1.weighty = 1.0;
		constraintsJSplitPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJSplitPane1(), constraintsJSplitPane1);
		initConnections();
		connEtoM2();
		connEtoC11();
		connEtoC14();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GeometryTreePanel aGeometryTreePanel;
		aGeometryTreePanel = new GeometryTreePanel();
		frame.setContentPane(aGeometryTreePanel);
		frame.setSize(aGeometryTreePanel.getSize());
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
	//getGeometryDbTreeModel().reload();
	getGeometryDbTreeModel().refreshTree();

	expandTreeToUser();
}


public void removeActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.remove(aActionListener, newListener);
	return;
}


/**
 * Comment
 */
private void selectSavedGeometry(Geometry geometry) {
	//
	// select saved version if availlable
	//
	BioModelNode bmNode = (BioModelNode)getGeometryDbTreeModel().getRoot();
	GeometrySpec gemoetrySpec = geometry.getGeometrySpec();
	GeometryInfo savedGeometryInfo = new GeometryInfo(
							geometry.getVersion(),
							gemoetrySpec.getDimension(),
							gemoetrySpec.getExtent(),
							gemoetrySpec.getOrigin(),
							((gemoetrySpec.getImage()!=null)?(gemoetrySpec.getImage().getKey()):(null)));
	BioModelNode newNode = bmNode.findNodeByUserObject(savedGeometryInfo);
	if (newNode != null){
		TreePath newNodePath = new TreePath(newNode.getPath());
		getJTree1().expandPath(newNodePath);
		getselectionModel1().setSelectionPath(newNodePath);
	}
}


/**
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(cbit.vcell.client.database.DocumentManager newValue) {
	if (ivjDocumentManager != newValue) {
		try {
			cbit.vcell.client.database.DocumentManager oldValue = getDocumentManager();
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
			connEtoM1(ivjDocumentManager);
			connEtoM3(ivjDocumentManager);
			connEtoC12(ivjDocumentManager);
			connEtoM8(ivjDocumentManager);
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
 * Set the geometryCellRenderer to a new value.
 * @param newValue cbit.vcell.desktop.GeometryCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setgeometryCellRenderer(GeometryCellRenderer newValue) {
	if (ivjgeometryCellRenderer != newValue) {
		try {
			ivjgeometryCellRenderer = newValue;
			connEtoM6(ivjgeometryCellRenderer);
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
	getGeometryDbTreeModel().setLatestOnly(arg1);
}


/**
 * Sets the popupMenuDisabled property (boolean) value.
 * @param popupMenuDisabled The new value for the property.
 * @see #getPopupMenuDisabled
 */
public void setMetadataPanelPopupDisabled(boolean popupMenuDisabled) {
	getgeometryMetaDataPanel().setPopupMenuDisabled(popupMenuDisabled);
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
			connEtoM5(ivjselectedVersionInfo1);
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


	try {
		cbit.util.BeanUtils.setCursorThroughout(this,java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		
		if (object instanceof VersionInfo){
			setSelectedVersionInfo((VersionInfo)object);
		}else if (object instanceof String && bioModelNode.getChildCount()>0 && ((BioModelNode)bioModelNode.getChildAt(0)).getUserObject() instanceof GeometryInfo){
			GeometryInfo geometryInfo = (GeometryInfo)((BioModelNode)bioModelNode.getChildAt(0)).getUserObject();
			setSelectedVersionInfo(geometryInfo);
		}else{
			setSelectedVersionInfo(null);
		}
		
	} catch (Exception exc) {
		handleException(exc);
	} finally {
		cbit.util.BeanUtils.setCursorThroughout(this,java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
	}

}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G590171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DD814D576385B0F362928502522E2B7AA5B25222222224DD6AAAACA36F0F3130C12B6CAABF3A949345CE7DFBED492B5DC5916929315920B888513147E79011B9FD894B4D9392B8373428C4C97B3AFB0A8627F5C0FF3671DF75EE110DDFF4F4F67B93EF34FBD771EF34FBD775CF3BF5E9729E4CD4C425C3C92C94ADDA871FFDBF3A5297EBCC97A670E8DB5080BF0980BA4553F35GAB240FF79640B3
	867232DFB79695CB8F2FAA04F482248B679B0B7E813FD7CA0F2E5AFB8E7C8842279C723CD2D24370FCBEEAC3BE0BE97AA95BD2F8AE87C8849CF996B8897F3936D20E4F60F8045CBC40D11A9576B20E5B8B6923GD1GF1CEC61F8C4FB7A967B33755F33A43E3E3251C075CF94BC8BD2ABA91AADC2C6CDE7EAC17DE4F4DF691DE5B6CCBF919B110CE84A00D4F173C7D9F423321393B737F3537D9156A3EF659E62BB64B1EEE45695A366DCB59E9179577C02BDB161BCD8E59A6C9369ED25F9976C2DADE126AA0BD0F
	62DEF2929E7170BB85A0D360D77591FC035BD8548AE0F3E33BBE5F504065BEF47CF6A92B733EB75F8A522EB0B7EBCF1C5B572E728F5B725F21FD505DCEF4BD937216FA0CC565G0DGE600B000D5346F5DED241DBB5B145DCE3B5D695869F26D32195D871DE659863F37ED039C0E7BD6EEF73A4D12447AF7073DCAC71E6100157B6A144BB11D4456E2DF47FDFB0314F14BC5F90BF5F41291FBBCAF5A43F4116071690471BEABA86FA99EFFDB74677D3D2466ADBD3FF971C29D5EE50FEDDEBC135733C0453BD1A1FD
	5D63A2FDDD863FC9B9521F8C7FB30AB728704CAE67D01BA169F8482B6AC55BB8349CE5E9AA38D2AA5EF4312E9E226E894F0B6DE5B2A475EAE5F9261F54BD2B17715CA3E4D96EA578B1FDBEBC13E5AD35D312AE69B7966D98B096B17BD2E4B2CEED470DC586009800C400F4009C0042C13431BB5BBE99210DB51B5C4A2EDE2B4DAC3BA5D6D6E9FF9B1E32D7F11B76B9BC0A4951AE9BCDEE2B294DA6FBA4CEF3221D68204D2F5C9954765B01471732C3F61B942B5381351BE0544AEE8F23B937FD8521F14BB43755EA
	1701015DA5E13D3FE8DFC95AE772A85F394CA6C576D1307C4B5AC91F74190CD0A3D4G3F19DE2E68A07A2A04766FGE896FAF85BCC6ABBA03B41DFEC5B5664F4F6773ADAC9CB2418C12267DFD15FE1065F03032863DFF692DC96249708FA8E1A7F262A27515ACE44B73987F00C753BC83F941FB09655GB481D8G8CG31GA9A75016E69FC2DBEADCF63DD475478BF5EDA902587589E6C352D04018223C083C151037974082908E908710BA047341FC27CBD3EF71895F183328FE9BCE780F45A61E0E656D994ED7
	24E872996FD21716F73EE60C636FA28D5DE27EA4734A42E178AE67E3D51DE67DD7E425E38D74D0F892F4F4927B50E0AEE7E3E21749A363A8785A8D86AC1BFDBD4A64FF3E5F407BA51F5B66EA00B5921BFBEB813690FBG7892E09F40FA0003G5FGFC8470B1G99031BGFE82F082EC85B0917B87388E20876088C0AFC0BF4039GB0442433813EA032826C96BAAD719219F6C0FBACG91G09G19G05E3201FG8E810C810885C8831887D07EB33091G97C094C09240CC0062B30CC575GC7818CG71G69G
	527F004EG1A813CGB1GA9G59G25E1B03EGACE1E8E39B7A4A73FCFABA15B9C62B53751AFC2D8ED167DBB5F4EABDADD77547815E9F4BF91FE8797F441FC7D4FD22A727137713C755CF215AF6B66F4BD35107962E7B8F4B7FDF82E6ABB7D3DBA1F692EADC6B15A7BE03788A62975E51642BED68FD097985B24FFC2421C3BB594353FF87E071B833C702DB2D43FF83FF927BC31B5413F323446C74F3095969E7A3E89B315F6D7F21FEB77D9F68637F96B0DFBDA413BE147BE40752A83BE40799FEF8F85E19B231
	859E1FF4B8C519F3A95E85714CEE88A0BC82BFD6A636E0D29437352DD711DBE4C531BABAFD7917D0DE1E5EB617AF94C2DEB1B231D5E7BF44B3027EEA1944826DCE07C3261306A84273FFC3E39159A65BC1685D4EDE07427057F71075DA37BC40E3211BBA96C9A416317512380A2D0162CD4446DDEE2B9D26A2921AC919E1A4CE3955DE2A52D1846048DAAE811EA9E198DB742F4758226258B5D25291D71EDE4C9CF57585F915E732B93B61CCDFEC41E43A32D3DF76D89A93F13C07497A65912257DDD6A78DABD54B
	B2BA46D7CA475652185B595ECB8A9DB4B9CC1D20C516F7C6AD55B9D4C8D5C8EB584473E22D24DEDC6EB5BBDD3D2E033223D7549B379A964CD2A74F5F8D0B44EF090138E5B74B3F6713152A720DEDC4B24A00173F3439C470164DBC12A4163409458F0DDDC48707C1B7AEG431998BFD4D8C8BE4600BCF0586D3471B6C0DE4A19242F52DCA5DC57B110CEBC137555F2115F5B037945BF073976673EFC56576F3A55FDDD2F4ABF8A3FD99D0FAAD80797F091AA1A0233C87E3E5E92D59D29BF8F10C164B7813D79ACDF
	BEEB6B8AEBB155D1CF2F8C61BF89188F883BAD357ECD13E764B64C724B2D4406ED2AD8BB0D4A546145BD8665AC962B0643B39AE08240FB00E7F4DB86102E4C40642AB330D8375920267B2B20BBEAE075F88DAC9E0E64F4B986C27760B1249B4F73A770FA538DDCA7E7933A3063C83763EC16FF68ECD62F8B1E89G2C3EA5A7102E649C16DFF58E23371C436AE3F43F97F4B100CF84C867F425672A69468EA1DD93604D6786235BF3926942G9F2DC3475AB1DF50CDB81769ED4A39CC5FD91C2EAA1C50BDA4A11D94
	4EE24652F09FFD3560BB140BF45EF0D6CFC4B8639B8BCF69BC540BADDF6885F0D5GCD67B13A7073546D28A9C03A547338DC6769750775DD242BBA1F4955884FD6G6F796AF6ECDB05F4116733B8975BC1682B053D4C96F4F167B3FE496733759221FBCF70CDA8C33AACD5BE613B0073D5AEA0F4E3D60B76DE40E26AD22E172A8B546DD8779E52A5DD4056E924BEE2B769972875B2DB50E5DF406AA90CE04F7288F5FBE357A0DDF3840B5FAD915C7EA3547D56D70EF40688260F98DEDFFC041AEF75DA24CB63F4B3
	B9DD4DD8F5BBF2845D1831BEBA22172831EA3DA42DC33A7831ECBD212F173675C817B21665E70E65FA9E2BEE6F2795C8D7BA0E2DD9C8FDC40FB563546D1DAD68F60C636317BFDD6354FCE35F9776B20E35A3EE9CDFBF0FD337437D0198C763589A885BB1E51CDAFFA59B102E7C972C3E7ADFB03AB13FD053E5EEC63AA8CE974F696679517D3AD66C45C6C27F820C09749FC7EC7E1D33114C93F43F344B54E772D6FB7AA12E5936EDBF1D06595E689AC7A127CF0A44BD5083BDE4FE1B8769628BC10E8BB1FE183C8C
	77C0A33BE3250C67B7AD8E36373B63C2A60779C2DF7CD0153DF3911BBF4F6FA6BC47C3DE4205B8F7DEE6D56F436C74F81C6DD63A8544670C7DDD6A7802613E6EDA824F8E2B5B236C03795D0B734BF7DDC4DE32C74451FC4F6CB53A2FC766789460ABDD847DF09141577510380B4CA12C1D6D35584E08E3D7CBC7AF3CB42FA8C8BB87AFE26D0B3EC8554E21F98B993F689E64B7857217DE0C7C9E277C48DC447AE9D1E9D910FE425006754B17EED2DFA354F398C081E0FCDED710CFB86012AF46FAD767FDAC0DE03F
	DC9DA0895BC95BDFB512322A6009696E44BF55B6B2612D93C1F53760E2263332A81F6E526D2D0B396EFA31CD07A1BFB2CA682E17680E78E5C6F7019769D201266092E3910B6166FA891DE231FAD83C33472A0EF79A006EA8C01820FFEF00345BCE23C05CE3FEE5678ECE1FFA4315D2626270BC3DBDFF4BEF227222AE617B3A17785AD2F7C5DB7EDEGA6673F87D04E62CBA1463894796E6FA1F51AF5E3606787308D2C0E4AE3D84798A81FA86A78F38F61699FABE3ACFC4BB17FBADE21F51036E6C3794A4B300E9F
	0ED33F861D590275D423ADAC6BAF5666D5EEB4B29DDD39D22D2306137F56551161ADCB5E514B18EE8C1779F434F7DCDB7E61F128236B0F23ECD300E6E9B44AD6B2C870D607D5311AEC56E3F265C6A657799F12750C2B9716F3D6D0DE335B69125D4AC0E59399C555B61323337A6B36AE90F8FBA0E62311793FFD03687F9A23F17CDF7F1E3ACD0526B3F55B947EFCE75EE0B4EB4B78E8DF1B6221BD4922CD35A730CD33002662175826CBCFFCCC6D2D57A3D7537F218D6D5D323912CAEA6AD72A695A367AA0495BC3
	FEEE94E3736D0DE4EC1AE8C7F83EF650123C51FA85212DCC1639C21643A0C7A4G9B9BB1963F3540AF59FEF10E1075DFC31B853FEA76E8165D9DCE37BDD05E1DB49B85E6725E33026C3938E5705472CECD71EFD485C0D089FD75EF07D05642DF990BF67C8AE53D512AA72B77D7688F9CA731DCB86012C5B913C72F5C82D1EE1DA4466765B0BEAF47361FAD6178E1691BA43D714A4F07C49DE320FC62656807CB7E9FF952F53C70AD03BF200D0DC9CD10CAD71E27EBE3B4563B1C59D6E90C4F4686E35A72A3E25046
	FE130BED36GCDCC8C4A3EBC0F4C0FB613A2FB142F9D36816C0F02A63277C9FA83074CEE8B8C8DD9BDF0DA94B74CC1BA83C7EFA871F3565DF9E4AC113D387498546B0579A8DB71952057AB30BF3EF069754718ABD07685797F194DFD53DF97546634C3455F84195CBF94205C11A0D32A102B765D866E8B291B2DA63F3E02DF9E26C2A69B1F41F975E206E492327A473D0B36D2F025312861CA2CFF7E3BE8EFFCDFA7DF3D56D700AE9EE08A40CCD1E66D2A60B6593E8A79C8D7419CFA95764BBF4A906F82DC6CD5E2
	AE69A5F1184E5E872F6FC9D1AE934A140E47F21655840FFBAE24G233FE6B56A3199E88D63F1CC4C2F59CE6915964E6ACBD5514A0DCD2A2083AB2DD613EC6763680D55B80EAA9A47CB75B95DC147D152F8B6FEE60E770D2338E57397A681B0D98F3C073256DF8DEB022B31ED0FD1FC3BAACC62BADC037431C01BA9680756684FD799DBAA60A9BB4816C72B53E9EB353ABC4C9851E07693DB91B6B8F98D6A3A7457462266DFE37D756544C6CC6E4ECDACFD345CFF0E3F3C9C65B2C019A4D1EE51DA24E3723C7EA669
	EB596BB2B94C04F12B73BB0F6C06291D777577EB311EF9D0C74DEF301E5F2D5DC66C4D24185ACCB0FD618FF6A0DB296A39F6BAAF1B5B2A9BB9097F987D213AB150EE0FB47AD7579432BE212F556B049F84D9E3043C316B74ED73F9C11F8E344531B8B64EDD0F783D009B930B753C32BEF82C363AA27898BCD2017D18C8F8087A52AB7C7B6D9F6FA35F4AEBA0BE3DC658537B7A76F40E1578860F4BE6D5480A6EDB5983195F4B56CE0BD2A9EC68FD649D85752509BA6BBF7067FD5987E23E3D96665BEBF11CDEE4A7
	E79574BCDED6AF947C07A8DF97D46BADA948B8E7BCBE5F00BC3CD0FF7C35A84B351B376B595337F207DB76D8C6E2D0CC5670FFADE1BE1F96A34184637BE6AD721D89BC4B2FC33E2EDADDBBEE840FF4CAF67C6949616C18D7F78AF67C08107790489AF19D4E193F6D511BB3D3C4FBBE2D9B6D1C494E5FAA9C5A3BB89957794E6E33D53F91589D1509F41DDB90476E28648B7C0394DF22423376FDA356459510E70843FB95FFF190FB1E8569426B211FG6A818E81F82F47FD02A73B4879593077D676B994596DB059
	DA7C8EEC707EC8B7615BAD8F7074455D6A580C6961AFAE2207486B073F572086D62E2807140BBB05F2EC8E5B78237A4EDC410A7DF9C4C63DB9AC0357BD6BFA5F9C46F4F7830D272AEE00988B60A80097A06A8654DB192DAA145E708E8FD798EB5315BD2353C57CA96B02594CD43A4711F88333199582BF136233D4F8E6CB87A91E240BEFB496C55F08778E245E51493D3C77D465E67AAFF1937DD7469B0B9A818E83A8G1171287F8EDBC83B5D635535DB5E8F23ECCF7CA93707153B509B6ACEE0C23C5E3D1E2BC2
	16CB571463671A2D641CDDE78F0C84A67C5C6B2DD6F5AC1C8D75145D84F395C0ABGDB038CDAF18DE23B897DCA17076C2F0D07F492C0BAC08EC0694D4622BA00C3B7E39F5DE65B952A0FF662A1F12BD37C5C2DBD68C5DD1887C627C3F748F22E1B754AD59F9B9D3F0610658C3A7C6685140B51506973DBF4DCDB2EDA131E2029077563F405746394484B86A83C45D8D4813097408CE038857B71CD9A338CBF564844576A5C8D383DCEE737CE97F2D9EF8910F5E4ED3CBBE49BE3EF51AB7755A0A917F44B48466B14
	DB50460DFDC4B7B3A1DD10G6382209E60B00097A0BA81F57323FD7B88F543A204C04DB0B9BFBBB1BA3DFC99D06EBDED5FA76815DBB0CAFECB06C2711B1220670FD29692FD6978A3329D3FCF116DFC4FE32A85778FE7A5605C139440F3E432BE9CD2560ADB7D4BB1991E33228C8D37228C4A495149F0C6C0B92D8C0A2E8CD19E743B61C206CAE9F47E6853106512EF55ABB7FF147C966706AA17F12B1EBFFA2C1F0C399C48AB3E4DD8D4895088E0818883083D8D47DC1CBD643CA22EC10FE4DE39BEC05E11756D1D
	816534FD1BF41BDE393DF924DCBA4F1BF55B703EC93A9D7B7F327C5149F9CDC0B92D1CA5376B59E01F8BED30CA48F024E0F4B258834AD5EB52665B754AC53DBBBA9B7CF548F203376B792955CB09DCA14F698E4A0A09ACB67985AC96CFE51F545E990B849E09G2937E3BC7552E6FCF7A66BF6BC9FEE256B5332C4081FG1A92D15756D523EBFBFF48F266C43DF22B4BC6476F23106586F579BDB3CAFEAF07AC97294B6F465523335D3B824AE9EDB7AED14F1F35527BD249101789B08F20648E08A3GDA813CF720
	BF139C641E6CC87DD968986B1B77C6574EA38165346D0C3AC357EE560C0E5FC78165347C62F579CD8DA8B7B23BF9B6E43994DDFED7170F2EFD3784145336AFD3175F3F560E0EDFF7C0B92D1F1F274BEF7D3A51715B94D0CE5B3E625F6A157B73A8797DA5A43FCADDFEE3570F0E5F658165347A5C214BCF2E981DFDFAC216BB244BEF757B239CFF8165347AF469721B9AD0EEE46DFBB6E4B903AE3FABBE98DD7BEE8CA827EDDF0CAE3F5D9BC6476F1B00F2DAFE093A7C5E2A9D9D3F558165347619264B6F0F236477
	CCC8FEB3B57CD84C73F28F4611521D46A2769E60F6FACF2E9E52CD82D7CA63CD97248DF7E28CF49F3D5B95A7520F50F432C8FF5FF3A804CD8583D2E7A89A4643B5EA9EA3F9C70B45G0B1C415F3FD86654FF7FE225D37F7D0BF71CAB981EDC62D16DFCE27EDA67706FE7ECF46A3D1F311A366731BC922B3AFB9D0A55AED336ACEE163D64FDA008D43E6DF530B006F076E87742A50C0DA269BD2A19F7E28CB45139D98A0C0176182D0A530D15344AA677579D9DE2C15762125BD1665BDC4175F717CBDFFF773A7475
	7720EBF87DCDF685539F3317A75D6570AC18E0ACAA852881E88230G98GE28112A6207D4E263469101EB7015DD9A7775149DDEEF28F3574AEF66F1C51363847401314D75BCF690450F62C868B27AF604FA6AE23C1271EA49D9C13250F4A22FD9F74D45FB343F7D1F11C3C496BDF26C33BA26858A9F3876F7B75EE7D3E5F68566F7BCF5C43777DD6F7303EE763E306C25EF18BF8D769AB085EA53CC3186AFD1353B4AB4E66CD922EBDBB7710BAF47648DA5A5DCE1B0D2CB5A5DCEBAE2E532EB509CF2C87FBE93A8B
	571ADF3662DA53FC972EB5A3ACE47CC6C3BA81A075AE342D1FD909EF4F3ACB5F1666F17C525F7972793B6B1E603ABF6E51577D4FBC24FFBD4EDEF73B2C552F41D3AA72FCAB9C32C1C8D4485FAF236595133BD38E681FA8DA1E6585ABFF050758DD1B55ECF573B709D83FDCB6206E174D223F64E3EAFC1D686B875FFE0B5A0A47A3FB68A6AF599160E5BEB90EE576A84E5D1B04FC4FAB4175152D686BEB2E92DCDFFFD1C26BEBD1EFF0FDAD6B8D2D2F37FB0359BCEB6B8B6A369EA4FAE37339CC6F0216034D547C8E
	6778F80A5B8169665F216FBF5A97DCA71DFD7ABA31778557095297DAA7A77A026BA42CAF34CE4E6B9BDEA72E936A7E7759D26D109ADFA721BF4D30E8E32B23209F65F741FDBB2B6FB53F7A36087ADE34EA6BB3C0DD51A16B3B6E243A3ECFC5FD3F74E86B0B073A12B575252B522C3EA3125AB6B689FCCDEE910E298E37538EDE1069B533BF38AD4C68573705177A035BC2CEFFE8DB78CBFFF0DBA870063605E5DEFDFF12152B56E52D9897C7683D4ED9202B85E2DCA4D3DC61C41873A7A26E2DFC022B04F47DC43C
	137207FCF51D7522FF0E3B347D33974A9C1E685FBF2E095A7E3928E0092A3ECDB508FF64DDF53F9159593C7311D1FBE69C86F54682CC180873CE250467A7D3A6623CA351FB2BCB130CC595G8DC9B86F3C32F938B5E1EBD230BBB2A9F44F5412446EAF648A7CE40AD7D4F8F62F61C70A275FC0023C2CA454E74CAAFFBB5C4C5B5FDA26767935C2DF6F2ED66BE50B40BF375A2F9E98714CB67EBD905C1E6581FDFB6E9E88EE4F6E015076FCFCA038BD1FB1905A1E4F9D985E37995ED36B60D301EFDD63E7CB1B903F
	3C5CDF47DBB8BE235C0FFEB352C72ED55B78E7227E9F5729688F7AEC7503757E3A2F63781475FE74C21E688AFFF9103E75FDBFF96A047C9F285BDBAF64C97F401FEFAD3E073DC1AD7FD6C17F4FDA3F7AEB913F344E8F3F8547745C1E00B1FDB70C693BB5E37AEE6D187E56C15EF33719861C3D8A587F5D745DCE870E5BB0C8C783A483245F4D3E4343624F1B7AC9FBBA5CA6BBF4B7DDDC255EC34AFE6242327510EE8230810483445D0363483E08145D5F0217A7B7F1FFA1793DDBDDF2AF6357G4F66FBF17E0FBA
	36CCEDEFFE3172539F92DD6CEFB235795ED76F7CB763A5C318EE0275A162273613FA44097257B05CC4BDDFACF9CB1D47AE91723C1BFFDC294EEB165DF62B476373638FAFDC214EA7E7C05F4A9D3E7150D960AF8F7A10E247D2F5392F64FEF66F70833FF7A7AC77726F895D0B7792953F77C7E2F8BEDD33317735BD4172197F7D228E77DBD26EC57FFB280548A2A5437C82D0130C7E3729F5B87F3BB7B9187FAD205F7CB91C2C3D23381C62ED495ABB0A97D9700E62A064E5A6639C97C54F5A9B6FB3969D3A8FF159
	D65C8732885CAA7A0EE3B824E36FC31B9B9C44FB5B336E236FG50F42A48FFG73B7337CA7E93A66FE2CF38E3DEB508869C38257C0F95BA0BDA8F0F9DDC8E79038D4A752C58BDC028769E285EE1E8769A6885C8F8A4125C0BA6BFE1435AF8F5B12FDBF79762019280F5B519318D74B5A71B2CD57BD00E5BF4877EFE783CD37BE007C4651FDB585528601FB026222A19DA7F0BB68BEDDA22427G30713B5522374E4C047C1C87F03EEFB762BAF3E98A5A5BF67A5ED0A3248F83A8A9E8EF9F875CA988CF7159D8CCCA
	B0FBD3E87C1010223537A10ACFCE515A5B22FC3437A910D77FA036714DDA342D6887D1260749740EFB5B2A5FC7EBC73B6FB792E0BC86CEAB8F5666E5EEEAEF505E8C2187A345E5C3DAFA88F1F7D25CD2C8D7BE04F77747BAB0EE2A831C6DA11CABB69D75FB47165C045E366DCB37553C4B5459C4E79EE69B0F53FDDED2FE7CC3E89B0B69F77FB2A01D8330F4923EA7687DG5FF50D7B7515D25A337A6FC3E7361E482B18446CE46FA45F3D5D3A8F5B72994F6FDC48B38C72A7CCA2387F71FBBF0A58450579ECFF1E
	655F6A17CF7CBD791E14AFFF06DFBE11F7EC2EBA7F9D3F3B64C4C7D3F9F95636C96FE35B96CC0A17EC3706857D0EE7C1AAEBD3A5FF16BFFCC92EA12AF991537BE555C15EED96B3AF53C1BD6DCBB2AF9DCDC53D0F215F1AC906F486C0CEAA76651C0EA06F3553D9175577AF6A47C8DB167E9E4B0DD5024902B3A1337146BA6D18EF7C3DEF1C9F025F365F7B0FFBB636D3691CB56677ECECAF947829949F294273F702DB701EFFAC64ADFD986D3A3D95473C6BE19C73CB2C44C7A609F489CD13EFBA30F43465F40DCB
	9F4FDBCF9B0FFC89670E9B2D6A7730597CF0BFED6B94100345F70B3B5047D7BC823E9260900097A06A911CE3B6F713FC6DBB51AC36BC500D659301BE8DE0E91A3128BC0DFDA3033FB3B4C84654BAFA7D385AE6F548AD4A00CDDEA7464767327A4EFE4D712620F7766B537858674F703FDE124BF84C21FBCB6BF6BABAED32F99D43AD2173395DA15B1D8EEB3BA10D483B16622683F853D03E7B28CE94D80C317977A77A0A9F237F084E7F8F8224C21A651FE57165BFC9474613G99A27FDA2BA47A40C5776FCC7C7E
	275DEEF218094DD23AE9DD125041A3BF29F5D0FC29AD8F5730DA9D4C62ED1F176673FF0C5762FC722DA092C7D4438A8FC2CE7A29204A0DE2ED60177F354D2C493F4695282FA40AFB9A20EA32F8F74B652F2FCE1B2F3D5F7784EF6FB9F6C247FC0A5302F1EA43E474D1776F276BF7C807830C1F0CE345E29CAECE1DB0B9D85CF0A30DC352A6EB6306BBA8BEF332B6EE58F9926306EC48EB7D837A10C63A27118869D4013BB7977DCA769FD04EE3B476AAF99453E1AC96BB9F53CB73CF4718A77C16CB98D33CFFDAF8
	6099CD5323246D653565435056F3F918BDD43883EDEA3B9BEDAA72D13106F47B5B54758E1FCD7D419D5C26BEA41FBE65ED3FB8601D2BD0406F780C325CD327DC0E595F63B4869A7F28F67F6ACF6CBB5A0FEA772F16B9B18E1BGF965D3701E7A329E62FB43A09D8310881086108D30740F383E3EAE17FCC0FD587BE52DCED7135CA75BD4C7DE2CFDBF866CAB0682664B6EDE0C71CE42EF2F1592FF5E98743BA565FFE4363203BF93D3AF49A5CF563E983AFF60023CF000F80094008C009C513EB9397CDDACF2DC3C
	D3D1CC6D967AD59BF178EC157BFDE83231415A76397BD446C961E37AFB67E5002F81E8D4659FA2E95A1FB9B46632BD260DE17ECA71E39E5346B0BF6F4498A6927266BD06FE23CD6C3F9F1E6A6363623F436099B555DFBE6623FE22FB81F11017B4957DD6CCBD7A2DAC01DB7599419527C36C1F0EBC7F50C2F08D10EE9538A7A90EFC6B45A2F066167F063FC1BE7A636C44FF05C73E24FFC6BDA11D6860D8EB63B0E5F11EC0B0C019E8003F73F4FC3AEF4CB21F353B1D6C435110C76CBDE6D849DDCF075CDF5EA8F7
	18FAED0A781AC1258AF930D721AF35730FA1D42E33B9CDE6082D57D5F518EC9EF9A379F6891BC3BF6E423D760247F12DA3F7E957BA9F792DF5AE6E522EF5084C3E7C873B34EB1DC9F96A7CB73A34EB9DAC4FEC58530DF3E8834824BC0E6B0574DD448FE9578B74138F5B362954516297E532F65E590DFB8CF10FE32C70897509D910DEGD0B28DFD623D9EB20FD4CDC37FD01FC762CC2329AB70D689A049FDDA76F58E6C23FAABF9EEF0EE603EAC26DF9D1755AFB9C377BB1F5407CDE3765BBACD9B1B26B8E91D66
	8D1CC78E7D7E498647866E67DED788A7A66FFB5D6F8713973C59FED09D7BD2C82B29D3585AD4972D4F35010F97A560BCB1FC1AEA1F85FEA7CC53DBF3EDBBC0F41AB2CD6B2F76D0FC46B42D3FCA5D0F7EAA9B72DA1FC0FFF25208BEA67189315EB9C97A25B0835393E91AFCFF033F4BF4F238B1CE4A7DA7E31F6F698C4BE3A47735C279171552CFD2607E3BDB42B5D29934238E60B000AB03FDFF042D432665A20D817051G93GB2G96E4E00CD43619781BCE7D5790B87F4FEB897F5DB2F9EF9B6763FE99676332
	6B2E124A566C4843F12FEDC35913FCFD7524EFED61EB5B37EEFFBB1BCE0E2D786F192A5FAB1D27FF6F2C6A347260F79FFA021F5F2D6E51BF3FDB5F23FF37E8E34F70776EB675847FAEF2FD0F7A3B484CE7B47591BFBC887D9485B081A08DA06BC974FB7777903F475E325546C4FB4C1D647C107C35898579DFB34CBD2AF8F7683FD1F1071B1CE335B99545E9A775A3FEA20DA595270BA299AE195E3FF213EFEBB0D20EFF082E8BECF287C57373F20A131EB29615BD057E65A8DDF357C03AD96062BC68F34C821756
	C17F9690245384EE957BFB9310DE70947A1E57A92EF83A3128E2BA62965275A9B95FA66FC5B3DCF867C8ED0E54C51E9553C76E3778DAE444BC50DF1D4A3DBB2603812A2BFA102DF5BA6A6586F176E1913815B43ED0A09DAEF017D33D44C0BAF1BA7AA5F7DD204FBBDB5CD1CB1D4E4E9C3360B9EFBA53E7DDA621393D3E9C75159928270ACC75BAB25FC65690F62B43EA6F3537D80F49FC7F2511462E03C0EB48945F3B5AC24F5EFCB7677837FD3A72FD6709357CBCAD4729BD0FA372A5C0DDEBE899092E7D593F6C
	AF74F0EC9D7AAA79D3855C5FBE3DD46DEF9B27B6662D17747DED8AEFE36953BEFF1BF4346ECDE68B1B3B5473F8B350181F9E5E1E7839C2F728F51BD7D38FABF7D648F25ABB5F7C9DDD3FF211A1E4A440620A2BE88C977B34F69D9CC77189CFEB5741DB44BAB88972AA7E043E6E818F31BF8F0F6318FFFC5ECB7A48GB463855D81057C0D02E031A0FE3F0DE16956989FAF67D21BCF06FA527F0463E0C9977A1B8582B72BDB2C231EC11F3163GDDC7C13AD9608ED01C6599760EA0DFEF6DD70FFBC2FB2AF3D643B0
	E5783DB0BF9E27FADFD76DF374002D23AEB262BA4A20A30FF69D958DB4714F7856D1494FE857D19F9E22E31686B91B389DDCAA219D2481FDE1962EB11E2B76FB4F1C1DCB6DF212EF4733E3A93EEE7A1304E7C4BB32F0BDD1CC3F7B6705F484C0EC961EEFCAF9621B3ED938462A4F532E313E1754EB242E3C6157D851F95AB5564F7C4E139E4E8B3646E236DDF62C9C1E73C01E12E70DC575G4DGE600C100880058E751660F77D68EE373CCB9B496F01B2C787787DA07391379D3DF68D8405CC73FAF8CF3F86B00
	0B5FAB327611B16A6C687048A266F074958B5CBECF3353E3A58BBFFEA6CE639133354FCAFA4F594E3EDF4CFC7E85B4A6388B5625494F224F6F77103DED27C3EEF57636DB76506F3031178ED819319EDCC3CE01B24CD66F7504DA732B6DB58B4A4DFB56E72BCB1F535A6A0C8E521EF62783C23BDEE72F07999D63AFF5A27F52674439CAA719531CEE7281ABF57B76F722FDD68A5A6FAAFC63G6F18CCD261707ED1843D9F6DF24B9D325BAD1B69CC09BA38066A60B054E9F98E4FAA120FCB3E3156C567D813D7B347
	6E223EAE924A4488F9EE33684F478FD3DF198AF4694F61B972CDF5B8DE8BE6980B9AE660581ADF076DD4E6083FA538155046C1BA99A0C350CE2F47F118B383E57847E7648931A51FF73B0A5573EE4600B168BA27EAA673CD07E67A2FF318CEEEB96153497696B25648760C7868D7239522E9F2C528F253661A2A723AADB59B190C9F39D0460819389FF14590313194C8E78194BEEFACAA872883E882B88A20G98GE2G9281D281B281F2G8ADEB0961582D43D0063F91BF763B063199F427B863404B15FBE5A07
	BB209E321F43F0DFD15CA14011BDA2F62FBDE9A5319F2B039C536DE6FFDDCE54B1154EEF8A50073F007356B52AF55F4D2A5F5B7F9756002D7F8BEB4059DE72DB2C5BD8D84073DE7516C82AEF8952A14CF613186DDCE9890C0B6F90F7EA62DFE0F1F18ABCB3DEE0F7E9D9DC3C362B1C53643C90B807963E48F0EC3C2D3211BEA983DC650BB8AE323BFCF1AE7A0A27CEEAE2624DAC7EBDF1C09B7F92B9DADF44F1770091ED5A7262294644F498713E0A8979BD917DBE3EE11465E61CF2B996FB0651B8A066C5EDCCFA
	9E45473D280DC95F4EC55F1688F9E533447AB1CF4DFF8C60A3E78D9F53B11B5AF0AC785CDBFDAC745C3B75187EBA7C73E3016B70DD47745761FB0F2957618CF710576B96AFB4B1FC8B3D3F49EC9C3F54093E73B05D672C5DD2435331F4EFD6F1774A27E6C7B12A7BF07F6844BAD85A54C976CB7D3E7BAAEE43D49B77FC5B3AEF774E26A3AD4DCD7BDAA5746F0B73FD75DDE5A5EB1AA07EFD6B162A724DB59B45D9E3012FDC24E5646556377B4AFDBF22F24C06EE8F38BBB3B25B3FEF146512370EEE4C186A69BAEE
	16F64CF4D1FC42AC6D181933D52C63A02F62A59CB3D975EA7E8640C73FB47C18E1F3ECA17DDB1371C01B8A108DD0B89B46A2C09D40DE00233345774C8607DB838A53D14D20CC369A5D33504159CC3E085901F2B2FE378E859F4F9306C20F677B06744773242140711CB624BF1E9F9B8A9C4FCF8C911F5E897142A1C117A9F0BF885C3334CE027B5EEAD6AC827FA2150B6077520F64A27EE5AAD7BF459D42F758D4389FB86E0DA1B2B7C213ADCEB716156819C00755E6636F5F1CA4B4D687AC8F04BE5EBCC9E4343A
	CCAA39178B9C111B2D85CD749B3CFCEE2622B3FC19F7198F0F227331B93B1B6A4F59FF5064F2D1BF46666CC562EFFA0F07FE1EGB08FE0691FD91FB31BFE5A02B6DD817843FF4679F32395E35C48BFE30C7BBD3DA71D8A69AC008502F61FB84FAB494639FCC677A2517F68CF62C957621D1D7D4476034527DB6B2A4AEBEBB0BECDB80075B7E56318CB753BDB938678286C116CB11DC3FDB27B7D8B556F325C539F77EDB92DBCD82CDB1607717206DC442D4D43F8798AA1CBE73D2F6DAE556F79DBCF3F9E7ED6FF7A
	7570C6BD6A6104500374996A610EBC71BD82F3F0BF772EB9341FDBEF5677F39B4D01FE2E5A2C6F676A4C01FE6EB3B37B0EF5A70C87F55C7205D9AFEEE16D7B122E6362E09CCC4896678B627B94A959382E2D366118AFFD59D8547CB26EC3BDDCBA4CD9B7361C799E36C5305B54EE61F7F77BECF8AF4E70B27A0BC1FA8FBB895269G59AF63581D91F04EBBB23BF9F514652A835EAB8D85AC8E18C47DDD61AB5A78E08A45173C220D8F7EE5C53D16C3DE58AB585E843FFD661900DF704AC8FC559D877464AE1E434F
	E8668456417A7C99A35AFE2E404DB4226D7B76FD7E2D398F79E887A479FE683DAE5CE32A5761235DE3EA829A82FF677823F3FC3AE1ED29330D2E6FF60D325CF1DA4E26A3BB4298480BD465333E4D26B69BBBC75B6773A9BEE10E364F4F906F9AA4C1DEC58E7679E4FA17167D1E267A5DEEBD7D7EF5703472E076B4299BED6CF361DF533A51461E90F4E9F46EB600DE22F250A7EDE9C21FB4BE87E3089C318715132337DE4798E2AE39BE426B9EC7FDCE552B46229D2FE25DAFD0DCB324AD82D7E727E7ED100EFA95
	7BE72E4357A7FFD57DB6B8CEFF7FDCF1DAF970786B007F58FE075AA09B5BCDCDB836A73C9AFAEC27818D819C5BD92FEA4776FC3FB5C6B664974C9D5E3F71985096F84FBF49C664AE1D0BF1E42E1D6C573A1DCE45E875D84946B85BBFFA470E7B5296C1FB40CE6A563F6FEF96FF9B59BB175D77A73FA3662A5FC1BC5104B698BBF7B89BEC687259E0F1CF202F7DFBCF202FFD4C765F3AC3476A2B3163FE8273C361F674C319A0FB69EBB887BFC8714D1036G0CF98D6D7F0CA6F51FC581BE6E35114CC9636C237347
	B127DC0E795D4FD98C711A569F7F1362D3DF537A63C7049E52A12FE19E7A00CB1A827B69D7CD6AFEE2F26ED67902A6556FBF5ACF3FDF18FDDAF9303FE15F7BC586B4B66EA5E921A22DE0FF960C7F01A77257419429003C012CC80B584B4670E30F5765F4AB2A9FCBFC6F9AEB92FF27766CA97F4E83A6DB0E1F1D8D0677382537445AEF9FDDACE77655EE4BFF662A7CBC29E01F340885557B248292996F13965240F91F148FC135175C1EAFD00124C8C565226AEDD0775F825715BB4FFFFCEA5C114612BCA98F2A37
	482C3E057DE415CDEA4C3D87AA72FAFA24A1FAB063D73FAE3600E2E9B088F0E0A4A9FA6D7B8650BD4ADA0EFF7FC51F03A23D03FF9DCBB42F3C2557CE7E743BFE897E77C4CE2A6FFF4EBB33BD687D4F3AB7785D39B7FCE7CAECFF2AE2973DCB847818B7F09FCA59C97EE6E130FB1EADF6134D2618EC5DBB9DE6F267F35700A22B6E126D227E99E9F8A64B2B59C5646ED215797FD0CB87884A9C9785E1ABGGE00DGGD0CB818294G94G88G88G590171B44A9C9785E1ABGGE00DGG8CGGGGGGGG
	GGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1BABGGGG
**end of data**/
}
}