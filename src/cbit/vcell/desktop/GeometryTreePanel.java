package cbit.vcell.desktop;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.*;
import cbit.vcell.server.*;
import javax.swing.tree.*;
import java.lang.reflect.*;
import cbit.vcell.biomodel.*;
import javax.swing.*;

import org.vcell.util.DataAccessException;
import org.vcell.util.Matchable;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VersionInfo;

import java.awt.event.*;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 11:34:01 AM)
 * @author: Jim Schaff
 */
public class GeometryTreePanel extends JPanel {
	private JTree ivjJTree1 = null;
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	private boolean ivjConnPtoP2Aligning = false;
	private cbit.vcell.clientdb.DocumentManager ivjDocumentManager = null;
	private JScrollPane ivjJScrollPane1 = null;
	private org.vcell.util.document.VersionInfo fieldSelectedVersionInfo = null;
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
	private org.vcell.util.document.VersionInfo ivjselectedVersionInfo1 = null;
	private JLabel ivjJLabel2 = null;
	private JPanel ivjJPanel2 = null;
	private JScrollPane ivjJScrollPane2 = null;
	private JSplitPane ivjJSplitPane1 = null;
	private JMenuItem ivjJMenuItemPermission = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JLabel ivjJLabel3 = null;
	private JPanel ivjJPanel3 = null;
	private JMenuItem ivjJMenuItemGeomRefs = null;

class IvjEventHandler implements cbit.vcell.clientdb.DatabaseListener, java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.TreeModelListener, javax.swing.event.TreeSelectionListener {
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
		public void databaseDelete(cbit.vcell.clientdb.DatabaseEvent event) {
			if (event.getSource() == GeometryTreePanel.this.getDocumentManager()) 
				connEtoC13(event);
		};
		public void databaseInsert(cbit.vcell.clientdb.DatabaseEvent event) {};
		public void databaseRefresh(cbit.vcell.clientdb.DatabaseEvent event) {
			if (event.getSource() == GeometryTreePanel.this.getDocumentManager()) 
				connEtoC7(event);
		};
		public void databaseUpdate(cbit.vcell.clientdb.DatabaseEvent event) {
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
		org.vcell.util.document.Version version = getSelectedVersionInfo().getVersion();
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
private void connEtoC12(cbit.vcell.clientdb.DocumentManager value) {
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
private void connEtoC13(cbit.vcell.clientdb.DatabaseEvent arg1) {
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
private void connEtoC7(cbit.vcell.clientdb.DatabaseEvent arg1) {
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
private void connEtoC9(cbit.vcell.clientdb.DatabaseEvent arg1) {
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
private void connEtoM1(cbit.vcell.clientdb.DocumentManager value) {
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
private void connEtoM10(cbit.vcell.clientdb.DatabaseEvent arg1) {
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
private void connEtoM3(cbit.vcell.clientdb.DocumentManager value) {
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
 * @param value cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(org.vcell.util.document.VersionInfo value) {
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
private void connEtoM8(cbit.vcell.clientdb.DocumentManager value) {
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
private void documentManager_DatabaseDelete(cbit.vcell.clientdb.DatabaseEvent event) {
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
private void documentManager_DatabaseUpdate(cbit.vcell.clientdb.DatabaseEvent event) {
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
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
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
public org.vcell.util.document.BioModelInfo getSelectedBioModelInfo(BioModelNode selectedBioModelNode) {
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
 * Gets the selectedVersionInfo property (cbit.sql.VersionInfo) value.
 * @return The selectedVersionInfo property value.
 * @see #setSelectedVersionInfo
 */
public org.vcell.util.document.VersionInfo getSelectedVersionInfo() {
	return fieldSelectedVersionInfo;
}


/**
 * Return the selectedVersionInfo1 property value.
 * @return cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.document.VersionInfo getselectedVersionInfo1() {
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
		setName("GeometryTreePanel");
		setLayout(new java.awt.GridBagLayout());
		setPreferredSize(new java.awt.Dimension(200, 150));
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
 * @exception org.vcell.util.DataAccessException The exception description.
 */
private void refresh() throws org.vcell.util.DataAccessException {
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
 * Sets the selectedVersionInfo property (cbit.sql.VersionInfo) value.
 * @param selectedVersionInfo The new value for the property.
 * @see #getSelectedVersionInfo
 */
private void setSelectedVersionInfo(org.vcell.util.document.VersionInfo selectedVersionInfo) {
	org.vcell.util.document.VersionInfo oldValue = fieldSelectedVersionInfo;
	fieldSelectedVersionInfo = selectedVersionInfo;
	firePropertyChange("selectedVersionInfo", oldValue, selectedVersionInfo);
}


/**
 * Set the selectedVersionInfo1 to a new value.
 * @param newValue cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectedVersionInfo1(org.vcell.util.document.VersionInfo newValue) {
	if (ivjselectedVersionInfo1 != newValue) {
		try {
			org.vcell.util.document.VersionInfo oldValue = getselectedVersionInfo1();
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


	try {
		org.vcell.util.BeanUtils.setCursorThroughout(this,java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		
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
		org.vcell.util.BeanUtils.setCursorThroughout(this,java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
	}

}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD5FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8BF8D45535F050C8812322A2C6C0CD85B5EA44D8E3CBAD362952DE2CE99B532858E29BDBD42E176BCFAB773F54525B13E40084D1C30D965BA891C19208B1C0829183A490C80084C2B20470E84910B9C98666454C194C64FD57FE2CBDE74E1C4904E9793E4B77AD4E6C35575EEB6D3557DEFB6D47B911725745AD4F4C4A17244C65927F37B3D312AA2FA34FB105082B5C1416AB297EFD8230C67A71C1
	83BC6BA1AF7611345CBC497EC98E244FC2BA7161345C3F406F77A477A7759360C788BE0510B7F3FD593111799C69C2BEABE97A85E5B5BC5F81E8879CF9A6ECA27CFF2B2C6578E80EC7484C82DC9121D96DA9603845C0138338EAGE3912337421389F04D2ECACEF77646F8E9490FDCD9EF13FAD4F5A22470320B7933D0FAB3B33618702A702C66E50AA05D8EC09A1FAD793C1B61D955F431EBCF47F90BD25A53A1DB2D25E659FDD1F1B8F76D2A139DB6D9F1759ADD325CE4324BD6D05FE6D25F20A70716172479D0
	D79C45B5D29EED705B89B0EEB362637B89FE817CDE86103F855B35FBD395173B2D7FDBD27AB61B615D906DAA59425AE35C62EF57A26B0BD99F52BE38361B68FAA264A5814C85D884303AA4AD378460EF346FEF6AA26D2CB92F6CF758EC8EFB0D53394FEAF6B5BA4C3295FE6F5A85B99C57ACF7B8DCE6C9E27D7B1527C4C71E110015BBF4596518CE6237E3DFA7B44F1466BFB2B9EB250ECE664777E75516B0DD1CAD716B04713EAAA46F0112C0DB8C64FDD2D273AEF961596C65BA3C6BFF78EC76C4DE4F8C956FC5
	A5242F1F21B6B087FE13F224BF997EA50AAF7B5C0FE7F659C271A45D86F9691F23ED343D0332B4986E1272365E2A2B0758A7E2321ACA198C5D25DAD91C03246EC9250C6781A14BD40AAF7A420FE7324C24F6CA5233A1EF615634DCE6DF13AE10F11ADF1E16DB86D08F508E9085B08DA0319CED6C97DDDF0C52461ACCAEE51F47E2B54BAE09151D547D81BCE51F62B29D323B95133DC3EEB539AC2673D659ADF11A5B3B098E4E87143B0B5AFE877028136D324B24D89CF6285D8423D6F639994D835DEB880DCB2639
	C60BCD8686B6270475BE527D9EBC2DA637F242E9B6A9321F0265FF271B74C90F2995EA048A60B7534B77AF92FD25C27B9782AC95FA58E1A1759D11DD60AFF66DEAF0B8AEFA1CC65292293E026879B66ABBD6436F8D952863472D849783699995D80F4D72E7D5BD35168EA23E49550BE36CF96ABFE7817DBC00A5246E9D50C7GCDG4E9DE8CB2F3721AD553E7D20D4726CAD3A36B41B58758EE6C371BB34E36AD54A0B481B8AF90B8172F6266516839881141DB89F1C72B8B575927970F75E8E7F6F329D01E3F189
	27A3324F2B509F2788CECDD9465B9744BBDBB3467177DB9A3A157CC9661565A370FD070FD5F51A755F96BB9DEB2007C400541D58077116C2B6A676195CB20E02E3AEB0E0596C6FD1A6FF4F60B85EAF595CB6BF82D8A73139F7BBC085318700BA00C3G1F82B4829C87588270B9G9903DB814EGB8GF68218087D83BCGF089609C0087408BF0ADG8CB1E9BC40DEA2AB40FE21532FD31996C2FB72AA53F2AB815A814681A4824C83103ECC4BAD82E882708144814C85D88C30F6974C69G5DG9381E681E4G64
	FC85F68A50869085908F108A309420308AFC8C0093A096A099E0A1C05EEE3431247E42AC3F1EAEE70E516A74D3CD3ED60728731D9ABA351E5ED17547915E9F6F70BE5172BF431F67D4FD22A727037713DB55CF615AB61E77656568C38B8F7C13657FAF8133153D8344D6081D049B57FA65094FA03E0278058FB579EA9B5AA8B13FC06619AD9ABA341383BC7DBEG0B4719BD925CC7BA7CB771A731BF34C9BDB90BA5E6273BA5E627DF0E22ED44FE3F7AA7753B757FC09F7F2B00796ABBAE10BE14FBE43BD2ABBBE5
	3B99FE38F95E5D9708AD70782453A14ABCC071CE08E776C3G61967807AF90DBB0A90A4BF25E2348AD3222D86CDD7E7CEFD3DEEE4FF927BF94C2DE4997082DBA3C904F887A9FDCA031C00743EE176924A10A707CFF233108EC15EDA074FE07472EB07C0FAF12755AC5391747C24FDCDCA111D8466AA1F195DB83A51A080DBBDD969BCCC5A4B413466DA6F1CA22E72DCAC71500A3EB393370F46E4658427BA946969B7A6E13124FB93374E266583BEF48CA5D4D666C853B7D31851369892B3E6CF3E9CC4471EEA6EB
	5DB92257FD96878DABD54BB2BA465F137ABEA1FA2DF5F4F8C821C6135D5485DAE4F9D7ED27BA078A298AE98DDBF9DE3C05540B4B3DA6075363EC146D9ED1EF42C730E0163AF87EFED8A4B69383F149AE167F44976F294A571EA712D1863C7C1426FC41DBB673C812D852D696BF189DC48706BDB0E782146D41782156C672B186640143FE07153701745B9E52D7E5BD79DC577510EE5F437A6A9D117F2C481F8569D4D5BE6B6BBE2F3A2FB3D479B9FB213FE99D6DFDD8C7A160AA777AE99276127C67C4BE81E52FD6
	062CFE4CDF82F92B2B7D792C2DDB6DF9D4C717BCB2047FA4E0BEA26C36527EE7CDDEAB37E116DFEDA7B6ECD5455A3EEA42730DC15CE3A828E631EAB9BCEB814CG9F831E51BDB104F4F338DC732BD92C3BB400AEE69869F2EAD8BD05B5AC9E2E2AE1F453EA88DD2B04F48DBC5FDC436A0D2A61BA59C76856E4A25DC21EAF6DE3752E0567D9GD65F2DD9C8B71B67276FE374F97BD9FD0C6EF4B65255833E9DA0EABF23CB8E207B5800F4CBG3F3AB694DD1A20AB811ADA9DBA560E314B114EDC4B74B6D04B749DD3
	4B6852EB895D4995C897DF4BE246E495FD29607B56CA24ABBC406A29BC4078B641B37EG6A6577B9C2AF00CB87D842694A8F2A5B71F8AE52A98739DC87757A433B8E69528FB2399641F399C0E11D3A9D7505C8D7D54762DCEC8721AF957632CC50996B98BFEB9DDBAF913A0F855FA7BEC13A98D5BE61BB0373AD28A7F4572F976D2DE3B1B561CB6ACD2FD337236CD3246B2EE76BB4D29F311B28FA35DE16893A58FAD6CFA2FF4E2DD737771B9B10EEE9BD0B5F738EF17BBF246E37680DC8D7F60869237E902B2F6D
	101AEF55E7C86763F493B95D3CC36AF6E4EDC23A2243FEBA22176A43EA3D7CC450359DE66B89FD3DB80B104EF918650FBB4C75FCD85D5E5AE224CBBE4C56AC24BE2247F907556DDDA66896727A24A36C3976081A6FB7B78BFBB9425AE1BC42574FC7546D085AA2465191368642F68C9CD16BEFFD89524D6575E5F03EC58DEA3A57F7A2DDF58323EBEBE0F4F181F4C915E2AF9670B90DD0CFE360B8E2736F9B45E41E207B2597CCBDA6DF295B8BF14D2EDD0769B44C76C67F5A1343693B9BF18F74E0AF195F62A0BD
	8BA035916307E75E46BD50C99763251C1F27E7075A5BDD584864D8FD549FBFA4CD76E63379F3261370EC003C33C7F16E7C0EDD3D8FD363F6BBBAACF48B084F99979C6A7802619C0ED5706C34385C4AA1185FFDB83FF89CC4DE32C74451FC4F6CE44FEA094588CE609B8F10F11460A77510380B4CA12C1D9D5B311D937B6E165AEF191275D608F6EEB8465AD7FB4C5F4E687C0AEC46EF1960B7G79C947105F17DE420F4CC52C1FD62CAD8851CF985A30FE3957CB6ADB8475981A52F28BG981F2F75A11FF240D91B
	305E0F32BE17C631DF2E8E10046D249EAE99CDD9D57044F4B7F8D0EDA3335F9D8C293B99CDCCE7F31AD4FB210F7EFF2E3B62FEEC1361B84C5747D1F71D7DC4F744AFB33A02A124D300A6810045F0D78F91BA255B62E6714E5EDEF53C33G68F21A53F2CB1A117E01A152EE9B0D82F10F79F7B5551CBEE566DD522C15B1D9FAFB7E5D7746E6D5B773FD5DE6FFDBD2FE7DE2D686G1373BF041C3300E63160FBFF8F29532C9B83778CE39BD89D0F8EE39DC5AD90AF34E09D473C04E7E02C0C31F03594D84730341A77
	F1AC14CF95F53C4266DB684C962C2794ED61EDEF1EB62F28381569682E7754BA2A9A7E072E0E463D5B1D15E3E43AA9B37AF51471339733167C8CF534AB93E59BG1AA4A34A76F50A3758AD0A45E4357449C545CC2E6BB713750C53834BB98BA82F4965F04AAE253728010C22D22B495ED5FA6C7C8590782BE0CCF1AB73FF4FE5227FDBE444717F60476AB665182E51EDD34C3FF7E5EDB8415A52F0425F267A7497334E26E31B123330CD1300A6658436E9D37667545EBCEE39147E8FED6838A81B0B2824A62FD2CA
	5736250DA46FG79D9AC46668745E4EC1AE8C7380F59E9C95EE83D0250D6A64B5559A80B21954694G9B9B1F5B8356822DEC3FF8DAAB4A7ADA76B6412F14BD1AE4D72743E58B163706E62340CC5EA75610BD97178C1EDA2E5194BF2EAAG02CA682B6B8CA8EBA24831D048DA6550133570A47A03BF08F26500331E44F22F7B744A4D90659EDAAE46A760961F4436FF3082478FCB6FD921B7DED99DD1AB4478EC03715906FEB8FF8FF952F53C70AD8327504622D21224356FDD27EBE3B456EBE336155C663731FC70
	ABC54237142F44B6671D02986E944ABEF5A5199F2DA6C5F6AB476C56DE6C8FC383597BA43D01C3E6FFB78C8DD9BDF0DA94974CC1BA83C7EFA871F3565415E4AC113D3828D328577539A85BAC40ADBE057D1162516B0F2253A87B785CFF4E660EFB4BC35A1CF628841AA0137B95A1F795482488399AD7D5F1DFC85DECA979F594FE39198A19ECFC8667550B9912C9486A0FDB073612GF5AF90752FACC4FBE3EB7D070C6A35FE419908898186GA61E41B2DF94063649CB0548A79E68B34EE03FEC7C8471EB4F42BA
	6DAC56B550CF62B01D3D8FDE5FFF08F26320CC32A857331E60F14F0594E0745FDA0FFADC8A34E5FF47B1717B322FA83D5242D955290A9695B728028E2C34D4CD72959FC7FF78884751065A99D265120BA147D1775F59781978F77FB8B24C2848DEBB836335D31F222C99C033769F583685945F218A13388EB7A0FD93500E9374B19B74672B79959B60A95B4916075161309AADCEB7B3C6B4184344D6048D7EEA836ABA996ADEAA6ABF3811580849553515256D9B83677887B722DCE56752F23B4FE1397C4F100E49
	73E6AE69EB5967B4594D043151F142AD3BE0EA67FDADFF067544C19D73C4BDA91F6DA276E6D2CC67CDB0FD618FF6A0DB246A398EBAAF1B4F1756F21240B1FA2A34B658EE4F5586D6579036BE212FAD1B049FEC87BF580E72FEF3133EED7E37200F825AD96DB8B6EEABC27CE2409519309E29B8F42C36C5B70F55E7AB46FEEC073AE2C5FD2F9487765B464D48B795E8F24E8BFB5A2CEFCF93AC44B7381DD60BC2D6F44D329BB2CF4A162EEE25C85850E664DD8D7579C41D873784723EEF0B18EF01E661F99C2713ED
	642C021E474B6A05C26090656B02D23DA58599670C47119264D15881FE2C83E5193573ABBDFBEA16BBDD323BFBB48645E40D797BAA6673E9B192CCB03EFF29C43E930167DC41F728D2570EEB41A3DD169D6F989E490EF9F517E147AF8AF9A5F3DAEE2599674CBA2F5E1C69B4E3FBEA3F0CF44EE467EF6BFA34F7F1224D7E337BD855EF84F6C765B13ADF10E0E6F7D43285FE8E454FD661D97B9AFA51A6D3A02FCC46FB95C5749E5AA4C8A7824C8548G10BAA13E6B44FD02BFBA4879590877D68E599559E5B7D9DB
	828EEC707E0813703DA8777274371C6A580C69616FD48FD51DA35FEBD083ABF71116B3DEC6B9B607951FD65F19B32CB91CC5E4541B4322F95D13BA7DF398535D8BB41ECA877CE2001CAE5099C0F5976A2D6AD2C9B83D619D9E2EB05626ACEFE43A4873DE2EAE184D6C74915BE86FE2B633C6606BA9BEC60567F39D5D93A169D910D75B0D778E2E6B0BCC6E29FD17ABB7537FDEBAB6D3017FA2G03A5AD37G204A027A9FFBA92C5D9E70695AAD136B0B7E485A333B7FF25B434A2D9F8AF7A7702CC56FDECFD958F2
	D11AF27CDC53C84E59F57640C8E0424F3D2EDB2E0E05E3211EB9G8B81168130B5C8CFAF2EC172AF20DF3945C776579AA05D8D9085B08DA099E0BE00F4917B6839CB7B4275D18D9E929B9D6267FE6DC1AF6A6237916A70CFE14B2D3D28D76EBB431171FBAAEC39B2DDFE57C85AF2759ABAFDFEB78715AB55244D1AFAD8BF2E6AA37DB8GF931G09GA9G0B81D6DBE19E31E2BF963BF6076BC7BA71999D7B81D76FF0DC5469C2AEEBF5E6E4EDBC9AD4CE5B46A62BDE39DF50F25D56510D5781AB5A7818FE221B09
	10CEG18831081E03041BCGD0EBC35D7451F8ECB43AE191C230E6181CBF4C0ACCAF69C165BE56763DCD2F5C786C48785D9AD4CE4BEF4026670F66AEA77A5271C7E4BB7E10A25B781E07590E7B0713ECB877B49AA21375CCD0B92D2CA99AD9198CEE874A30C0481039BCB2995E8DAA271521402EA743C661F74B6DA84323C1F5E9C1DF06A74316335A754A0DDD91993FC9E14BC55B757CD1A63D2FB08D72E6812482AC8248F3246516GB4B9F04C2538424EAB629A74E86615DDABA36B5B7A20F25A3E6DF66815FB
	16160B62F9139CA37B26F88776FFD1EEE4F26E88AA2715F336C34F8633FB508653058C0BA214614D20F2251A74EA27DE39FB56C5E603C9E14BEDF06A79290FD69339421E53B54A0A09ACB67985AC96CF3D56243DB3D685BC5A8194A746D32F9A715D1998A71E8F9FBDC15F1B0074FC00A5CE543537B03236DF93365C6ACBFA6536FC92993FAF43165B204B6F3791727BD358F2D53A7C9EDF9F196DBE9DD4CEEB3B46CBFA7EEC185E2B32C25EB800B80059G99G4BG8ADD684F8A5D641E6CE87DD978986B5C2711
	355396D4CE5B4EEA172E5DEC080C5F17C165347C5AF4793D9CD4EEF4F673C658F2CEDDFE0FEC0C2CFD5F8FAA27ED5FB8DDFE97BF0B0CDFEFD0B92D1F0F536537F5D3E47C7686155336EF16AE3FBFC6486F2D22F07CD2F5795D99D4EEF47C9E8CAA275567C2DDFE6E62486CF3CC58F212DBF77CED0EF07C8515536AF32DAE3F17034A0D2EFDEF04ADD7264B6F21AD11356F7120F25A76556B72BBDE92993FF3C165347C5AF579FDD499993FADC1653476695365771B88797DB6AC3F099AFEAC669972E29C998F796C
	BD409DB4364C00749201FB4FC7F0EB15345CB285E32064DEF22EE294699F5134D5241BFB5B4258D4A8A0F50623617EB8E29E23F9C70B5F7FD6C23FFFD129683FFF31C751FF7F22D6D9437064920FEA6793739B14115F4FB0AAFA6FE7FCC45B739C3DFF637258950BCD26ECD95CAC7B487BC09029B4FB6CAC0CA11C5D5A3DF089E3236F513BD893950C01DED336C941B1508133C5F138309223ECF29D6B6C948B3A96275C01B23F64892D3F059EFD7D3D6E51575F924F487ADB668925BF3EC76CAB04E78234AD99E0
	9E409200BC8F58B5C093C037876D77A8250D02F41C075DD9A7775149DDEEF28FB559436E1D73B8449B85CFD2DEEDBF492330E3B5647172893C5C92AEE319A7381EEE9D9C1365B9AA0B76FD504BFD4F8C5FC545F1124B6BFFDB07F6CD4831D3678D5D77C73D7AFDEF746A777DE96F48FDFF5E9B2A6F5978F8D5A16F38853D2BF49422F7896F901E9A4A65B4CD0A23E92B04EB4F2E83248E1DBD32168E1743EAA5EBCD89571A192D5A35A671099950DFCBBC38567C41F2DCEB2E6E41356684FA6ED38B6933GCA8F5A
	D6FFAF716DB1BD7A36944771C92AFC260FBBFD21F5FF0FCFDF7789BE523FEE074755A1EB757B08EF2D48732FF04886A1D1A12319CD4BABA6D7179C54BFCF5272ACAFD479949F313B73963345455FA4E27DD2B42C6E17ED223FFE9700AF97FD7D54876FD2DBF13BE5B75D64A5BB823C4CEC894B9CD09C7B378A79727BC26B6BEFFD7A7A7A24AF343E0A7A426BEBEBDFE8FDD5760557D7CDDFA81BE7ED75E62A5A5AC8744666735F513B20F341E66675609C3FBB0B6096C2FAE98F7A7EEB7BC36B6426FEFD1D447607
	56491D7D61F5925F9FDAA78F750757492C7E11F57247ACF57F7BEDA9B9DB0DAF17501FDA6C5A58AA471B16DB608D6D5BD9FD5186F5FD9522BE1FC3DBDF9954D59B363E4A656A7AF6087AB677EA6BEB033A2C1A7A22D4E9D65FA2F5FD0DDB857E7195EF61186AF4B9EC608519DE5F9A8CED8BFF9E543705350321EDE15DE0F8DBA89A8CED8BDF8C0637054AC1FDFFF2E925DA175B4538D8C46FF5CE82DD4D706238B81EC3F00910CE9638E994178A698CAF5EC9B9172B2E33D274CFCE50DD1345D046608B6C1F35BE
	ED7FFC3AEA152A3E2DE508FFF11D3A5F086CEC5E79E316764C38846AEC82B07BF05ED9FF9C4FCF86FCB86F3C59C2661DA4C827GAC70097DE463C8EB42E53EE0FBE577971E20FB26F93D6C7EC22640BFCD7185AABC3B57E024F87A8DA4480B69C5FD4EAC8C3443ED3C7D67BFD17B7C6DC2DFC56B55FA299078A56B836A01914FECE37CD0E8FB1EB824EF4F1307C25B736DC3616D791E2150767C60D0F8FB7E6650483EED7227EA9D6C9078739B82ECE9AB628BB6866A380263DF5B98C03F8D696F7ACCED63DF0A7A
	3BB729689B7D36DADE94287BF20EFF2EA800DE48F3FFF120BCC8FFFEF300BC65C27EAD6A76D68AF9DE599248F7BB470FABD14B3FD3509BAB836A5F0E783FFE99002F40B1FD35B7E8CC035D9A7AB4E33ACFBB269BFB48FB6ED6D32F4323007D7751F3559E9C37A5102E853082C475316F70307873E1AFE9CF274BE4036E260BAB251F145D6C433299C03F84A09F7065G46FE9CC7B66A4F8F376065492D5CDF7C21D76DAFE677B3FE8B6039349F677FA7075FD65BDBC02C7C52E6220B438D26737E77553B7E41F849
	90261BE0FD087867BBC8BD62C479988CB7D14F5ED56F2A7358A5C21E770D536F29731AE4174D62F67B7D78CF162FD16713B320E63953BF9E3A8C0172208F49235F4591650E4ADEF66FF0EC403B93F9836C5EE179G5ECB4C8CF8FF241E6753B59B4DFF4297AA1F795FAFDBF13F45B9007E77B97A9EC7BC241381668D087D14914F06978F04723F7FC3579106C16D9D459CFA97B1FFD0FBC7F1228F6FA8EE003CF103B8475DC44F3997C1DA9AC25CEB3D388F14A7F06F7793DCB9241B065066F233705E7624A1C233
	0E269511EF447CEDAC7F82CD4F93F5C6513B860BA0AD8DA36EA90A4B077486013B4101F4E582F74A03F43582D7530BF4CD82F7CDAF5219856E950AF3C2BAE698E54D4E4136448E13EF8F5EC006B07ABD4C5B4E5AB1C45373C559C7F2825B7994CDAF937C56513D3602A810BF8AF15D94D78BE923407D18625AA1BDG4046EF05C3EF1DB98E7227C561FCDF110D6B4C24A834370AA6525F0BA0ED9883F16C983437CD473576D6BE46EFE375E3C2591B5DC86ABBBBC6EBEFBE0A370E515A1BE4C4FB9B003C0CB15846
	6C93E8DB35D721CCF3CFC8927A5CE7D43FCF1F08F45FEFB4404F8A2EA88F5666A9F47F55896D0D3E8A75709B0A0B05F43C4055537D57A4C827DE05F777E3FAB0EE1A8F387C2BF12E585A9E700EAD39893DEBD71D4BE25EE76AEA20B38F330DF4EAD324FC4355E89BD9740EE9B4242781A4DD0D6F897ABE43F7DD936E39CBDA762C7E7B509946412C142B191DAC3E5AFFEFD7323D1845F8B67910E7C9B40C3BE8BAE682FCA6310BDB32597EBC4BCF8C48A77E1EFCCF4A1F7F6B00FCA26F0D196A7C7C0077E1090E1E
	6765D95B1E5E08EDDB76F422E4FDB4BA64F7BC9322D91BD279F362025BB21D2F74E5B33DCFAD8D71EE3318F9198E3677919D10F9A9679A547B901D0F2C100E8618F68D76659B1DA15EEB2733AE2B6FF12AD35216A4D16EC6A514ACB893B29B5F532A9D730B2E710FF3E9AC58555840F14F4676E17A8EE151D8B636178B7C890A2FD261192D4E976FAAB6C1DE52D8346B17166318DF7BB59C7386FA0E50AD526F50B4792683CBCF76DD29F169677950956311AD615CF1DF2F7ABDECB6BF30B31581100345F719FD68
	63D3G1781A00DCB4BAD84289E07F3CCC9BF49573E934DE24B837DD83E9D68FDGC9GF34731EFE4307295D9E4CC2D27570FCB2D963B5C2274DA6575E2FC6C1655F7764B7A9BC25E594F984747BEFFAEFBFFCAA66391C577165657583B2C32F9BD435DCA67F31BDD36B96C160E3271C45E199477AB004271A85F91870D9DE0B146665F3104C4E27433687C7F944907B44B7F546E4F3FC06773978122C57EC1321C433D20DE9A5F727B1FB61B49EEA6B6CB69FA48E79B398EFEFAC62D033CA956ACDC43EAF5B0E9BC
	EBFB5CF83F7FE33CA66510EF8511B82294D6F890F252CF85959523AC3F8A48BFE6B5EB725939DC203E9AA96EA500F4212F611EC0FDE53B7C6DDD50933A3D1F1130194729B68746298B46230FDA50CA576F93A09E86E81800E3E5D7503DD3B518A7040A9B6E23F108EF02B6EEF8046247CD50468DDFB5E15C908BF94BA6208F59C377B45A2F05B1F3AD629EEFC63F92FBAD4A299809DEE60B7490CD13EF2C30748A63159873045FBB12F86F680A7040B31AA55712367F75C461883499DC9EE68F8FFB03EDEA2E97ED
	2AAA86ED6ACDEF20CD9DD27CB6F54E975A26FEC8BED5465BFED350BED8B8E06562A2AC7742E517E37617CEE32006986D7E550B945F96235D3F4A71E29CE6063C39B1F8CF7DBE3A2EAF398EEC92209D4087908B10F41DF88FE3C5199466FE195161EC10FBE42B6A480B356F046FF25B47FC5977D7E23C1374587D522C3135A13FDBB277BAE6AB8B793368B737E512A7EBDF955DFBD8FBBD788300B6GA7C0B440346B31FD9F2F646FE21163629AC5B1F5F4532F5A0843E70B6C7523494686EBDBE55F650C13446B75
	774E67GFE9E40A2D53EF48324E9FF7E0E46DC79B7E8E318BFD2FC518D5A98A65A0EB1CC9564455D00FE6389518F0609FEBEEB796F92F8D6CF8C140F792893F4AF4088F95D9351EFCDB90DFEABC6605EBFC3F033A01DBA91F9B65138E9812417895C990AA35FFA493B91F13FB67CAB7C8D72519FE7FF7817704816744F284DB7929DACDDDEBBC2D91CA7904A20CCAD407BBC5DF623FF4CB21F353F03AC62E948A3769E1F6BA5F7BD6D323730D66EB4F92C0A781AC1118A5968D168CB6D7CE388C56B2D8E1399E26B
	75A51DA62BDBAEA65FAEE1F36826BE5CEBCF389157BA677A34EB1DAD81EB1D9B7B34EB9DA233BF7F49BE6DDA6769ACF57EFF75E957BAD81E3F1FAD3EB534GE4AA3889578B737691BF24DDAF50CFBE6C5A25D2C7CBC014495A79E8BF6EB198EF42D82118FE3FA78652B3GE65F04BE719C0DEF53EFC27F50311244192D268B4137CAGC96E53322FF3E09F7D40CE1E1B9C1B38AF0B732A63324AD5D769FE67137A301BF8CCFA13B6B6BD6121F51AB7F19EE3FAE95A3E097B3949FD04931377B16746D07202B7BB8C
	2AE3DF8AB11A3A042D19FBE8FD4ECDFC3C38024E7F4AEFD66D33406F33B76B2D395ACE921DBAEF567A2B8E0A0F3ED96B2FDEEBC5FF958BF94BEE9667D95968E35AA70975CE936917C4117EB6CD136FEF30F4CD53C8E31C147BE746BE5F53991147E86EEB0573AF50574703775F2D47F10DB4875AB19F40F08B2CC7EFE15F9FE16B3074E624A983FCAD0099A09AE046AD98A395ECA37E26CB7FB5844EFF678942FF3FCC5E5B467958AB63FCDC7040F429E0DDF5960EFBED9B665C4257D7377857967E367D4A9BE8E7
	BFA71F6746FB052A5F6FF82F7C5E5926AB4A030D45EF7404BE3F7BF60F7E795D77FA746F967D20E764FBF7BF6E897DDD64749E75F711194FD8C83FA93BE1B24C7DGE6G9FC04CE474FB673D446F3137EC35B15181F397B9BFA4FFEDC2C17E87C6384B53608B7FB7AACE7848B956F90722B8EC24FE441F71117ED29CCE0AE438F6FAB748C53E2D41C8B93E136EB3D965CE0AE63E64B41D636221DDF3A623FFB9C36703F910DEAAF07776234FD9FDAB62FEE223FF8B8852BE017B102E7FE3A0BD63D674BDCBA9EE96
	24D384AE1B2ECF4979B6F9AF1A61A658C7EBF324AE72CC39F574FE0B7118BEEA9E682FAE675E9D53418BD44F99A05BB22117F2871ED56445A26EB60AAB00F439403DCCF5DA8F6976D874CB5656E01FF7353823264432B34798F84645B2FD4E0FA5B48F1FAEC4FD45866BA9A5D63D0E4CF413B5044DE23758BC3696CB1F4C77DF765058F543EDE939E53721BC4BAA68591B7F669C7F364F05EC7FF962F6FE1E767A3076BC0E48F7966ADAC74BC03CAB613F45FB75F0EC9DFA63A0C9B1FF7B52EA353F2DFD3EB66BD3
	C95F5FBAEFE3EDCC3E4D6FEF17B98A3319AD147429677125C033FA4A4876444F957A432D5B8AB57570F7904216535E79E6659E8BA8D795C6C682AC2E381D46F0CDD3346B60FBA87E6C946DBA780BE1DC87F7C3DE4A94916375927BF373B80E7947EB06C89F15CDCD4BED180AF42F76123FD190AA96446F37B1AC5D9A6363E5BC9D87D628A7EAAA0E031CCB68EFE6885CEBAE310E1A0ABE639FA769BA8A52CB854EC2F1F95358BB028C3738D5BD6E896D654ED98BC6A843F8641ED473385C7B3AEA1F2387EC9D551D
	0D6B2832E94172E857D135C053B64D3F0E32CE532E23B63751B1CB031C2D5C8EA69DC7BB7081FD62B4DCE33CD29A701EB9BB175A67A05F0EE747D2FC5DB47FB81E91AD1C066B09D54D244E425B53F2AB811AEE47734DB71B45B7FDEF47B55636966D9A6B2424DEA319DBC6DEE3CDEE512E3146841CA74DED893546E236BDEB38901EF1A04FEC008C0025G2B6FC84B5DGD08950F4875A7CDF7A0BC630F9269C9A8B38CC967C7B830BC73813795F7D61E381E900FEDF9866F0E32F135FABDABEC04628233353AD0B
	18E355C01E409DF2B7B95C96327063776768DDD9332547C2FA4F51413EDF4CFC7E849A93BC8E6BD26B9D6873DD7DE4EF5BE1170D8ECFC777817A9DB6765281AB93A3561083D006596AF77B432D7955769A8365626E705BEA529DDADBFD59C65A536130C3E867F1F85C4C68987F7F31A17FE4A1F3997DF63F43C5BEE025EEDF3D9D6DB3D5501E58609F87F84764E9958E6F9FAD26FB25CE175CA93BDC32194E142803DCFAD6E13893FE5F09E7956D048DAF1B6820F32C492719E367D27FD785E56A6F94F32CCDFFBE
	4E2732ABC097F5A71EA3BF500A63B581F08B6E443175C6AB3633A08E47612B27882D9152D60068B824FD61B40E43E9F1A8431AB3648931A51FF7AF6429675D793D2DA157B969F14CB7C9DF8FDC67B01D9C4B766B645B86B25648760C7868D72D8522E9F2C528E86B36321242720A32E27E5D8689E52C7CBA6EC76CB6909BF3C2BA86A091E0AE40FC00A5GB9F7256596G148154833483A8G63G2681A4GA483245FA576F3870BC7984F7C905EBF20A50C797653B133906AA17BB97C4C1F622469E939E40F085D
	EBCFFE0F580F45CE0E6976333FAEA76AF8060E218220AF1F0E735674E1D2857B1D207ADDB1FC655700C72FA88F266F89C36437D837313000675DB814AF293EA5C807B05BCDE2369367880E0B3FA56E5434CDE7F131931E515359DDDA96972F3BD448E926CD8F1EC393B90E0D378F5D24CF66GAEF5BA0E0B45977CF1AE7A0ADF8EEBE262EDAC7E1DFAD29B7F92B916CD47F1F7DBB65AF45E0C4B0D09BFDC613707A9C1F7FFC65747719116FB69324B3158F308468175B334B16957A85EB8C39B1366B523EFEB073C
	B9B3F0DC7C29D94D3F68EE08756E9EB926437B1021675E9F8D071FFBFFB62C3F8EFFE1B8F89D7E4BE17DF57802E175BA1C617E1D576B92AFB4B17C7F2377B7190D6317BA51F71E6CA74F6D95E5BCBD1DFEEFDEF1F9644B3323AAA2BA2F63FDBB56414F866DE43FB4603B2F62B6CCE96B01E663217DB58D67DA1A9A8E99A5746F53F37C7595BB481AA604FF5FD9D1D2382D2CD81CB5667A4B7D5CB67AF2DB3B7D65960C2A9C332107036E4E0C4E763F97E1396F1E0AEC4C1CB9CD57F1F7EB470C19624F5E2D9DB37F
	F9CA2C63A0AF65EE9CB33FBC2D66DFF68F7847FBC69EB3EC0E7D007E2D49B620D5GE281928166G4C87D88C10F3AF4E1FF7E70E34869426231AC179BBB0C15FD9A33061DEA6DF653D41F2B2FE478D2147F31BA17CF8B6997447F317A1F8BCDB8D7A6359E5889E4FDE837169DD90AF34893A8101BBA5F0E3E81D84F752E2D63A857EEBD4AE022F279F49C57CF5D4AEAF453561FBDCAA5CA90E3B55C0A687E8F2374305E5279948586E34D82DAC9DC7E9ACF6D89E88FD5CCDE534B8CDAA396F97B8A2B7DB8B1A6877
	16795CCCC5E778825F5BFEBC0A4E4766C27A3E194B61EDB4B91D540F31B9DB9A40B93B817A598C908710447B1CFF9B5801B61D82F8C3BC4E1F1B1C9863D645E30CFB42C9E895C847G4C9034F517F02E1D9D0FF3794B2E95227F511FFC7702A4E47E37D6E27B2162531D65A5055B4BB0BECDBB0975AF0947B1F7C7405D1A127B52F22B6F9B4D9ED3F433A4FEDF277A7DC173150F7B3E3822BCD82C7BC18B464B1BB2917737960C173FAEE4B11D76377D026A779B272E3C9ED61F3E72FA782F532807C12107B7CF23
	9E3E1DA55ED3350476F3C51670FEEE2BC55F4F6D34847B39AA0B3E1F5BE78976F3F596769D6BAE988F6A382551229737703B7CB486B142B8B05F07FBFB77C8385F673C8F5735C583B86613813774BE5C077A495A914E3A31654C7730AD027D260EEEFEF757B90077624A6EC7FF51C37F5ECDB72423GE26F47317BF250B96F686C660D884B15B8A30B8F1E227EAE71FEEDFC10C671336F574687E71C28573910D772G36779BAEB57F09001F7140E8FC5573A775641E454B266854417A7C51EC347DCC01FBAC9BED
	5F3F6F53A6855EDDFB69A2A1E1FBCC0FCB3847142143C73B473484E8883C4F71B989FE5D3036148EC456F7D591167350F279892175DC84F9D52AFC56372FD11BEDCA5076799B94FFB6C15B6783C3A2A6043C14846C731F52BB326C77BC55EF13734A7B573EAB4A033FF364C29B5BAD7C6BCFDCE8E33F90F42F53393B6CC1181F9FC41F545206BE2961C10CA1DE97FBD0539E54DB2FE38CE130C9226E986AF35201FE2128FB8145AD05F45ECC44158E5233B6C8D74F946796C37EBE793D6A77D5C3D73EFF26DED19E
	ECECAFEF8B9C5B1B6C240DECECDFE84331ED1E99FEEC7B0006G0E6D18195A317DC6409AA3967293F46A8D16F17AC070BD7F4786085C49B3B10E7C43G592FF5B99CCA2B45EDA19B63EC7F683DC15C174EFB88E98F8C123A756F7B7FE390E361420758FDFF723B72A175BB08D35B50861B9E9A49868FBB7CB638AAB358573E1B996CEBD3877ED5F768787952A06EA7B0BFB4F6907D50B810BD79A11C031F247825104ECBCC4BADCAC47B0FEBD377D9B5600D09231913E286A37347D3AE3B9C7FDBGAC06C8547A63
	EA0AD792357E78A92107A848DB1008BE60DBED417DB43BCD5DCF7CBBC8AADFF0D0757B2703D75EAFAC3C22BC585F30FF7F40FCE8EC42FFCA4B95E98D7B33E07C8FBC113F8E26C886728632A22DE0AF9B430F83BE27432528FE2C723FEB2CC93CCF6D59DDF8428DA6DB081F1D8D05F73B24F7455A6F90DDACE774EC5F157D1B6959D91261103402855507A483090C8FC94BE960FCC84A06205AC7E6D4038AA4C5FA2BD0D43D8B6A7EF3703A32667A971ECFB8D71B1FA5E5C1755DB22BEF39172C32C90D19CFC86473
	F31724A1FAB093D03FAE56C0317432B44011512468275DE7026ED156C27C7BAF7A9C9469C37C6BD822F905AD9E9B79536F7AA5785F9399D65F7FDCFACDC7487B1F739F61F7679E711FA9317D29B9F574AED1920C21A45C074A2FA3FF33B054BD4F961B49EAD5CC560BB5F6B33973392FD7117DF7451E2A237E99E9F8A64BCB2DA3F2DFD01579DFD0CB878837A7BB57D5ABGGE00DGGD0CB818294G94G88G88GD5FBB0B637A7BB57D5ABGGE00DGG8CGGGGGGGGGGGGGGGGGE2F5E9
	ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0FABGGGG
**end of data**/
}
}