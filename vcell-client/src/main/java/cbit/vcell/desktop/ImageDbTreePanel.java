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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.vcell.util.document.User;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.gui.DialogUtils.SelectableTreeVersionJPanel;

import cbit.image.VCImageInfo;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.clientdb.DatabaseListener;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 11:34:01 AM)
 * @author: Jim Schaff
 */
public class ImageDbTreePanel extends JPanel implements  SelectableTreeVersionJPanel,DatabaseListener, ActionListener, MouseListener, java.beans.PropertyChangeListener, javax.swing.event.TreeModelListener, javax.swing.event.TreeSelectionListener {
	private JTree ivjJTree1 = null;
	private boolean ivjConnPtoP2Aligning = false;
	private cbit.vcell.clientdb.DocumentManager ivjDocumentManager = null;
	private JScrollPane ivjJScrollPane1 = null;
	private boolean ivjConnPtoP4Aligning = false;
	private TreeSelectionModel ivjselectionModel1 = null;
	private org.vcell.util.document.VersionInfo fieldSelectedVersionInfo = null;
	private boolean ivjConnPtoP5Aligning = false;
	private org.vcell.util.document.VersionInfo ivjselectedVersionInfo1 = null;
	private JPopupMenu ivjJPopupMenu1 = null;
	private JMenuItem ivjJMenuItemDelete = null;
	protected transient ActionListener aActionListener = null;
	private JLabel ivjJLabel1 = null;
	private JSeparator ivjJSeparator1 = null;
	private JPanel ivjJPanel2 = null;
	private ImageDbTreeModel ivjImageDbTreeModel = null;
/**
 * BioModelTreePanel constructor comment.
 */
public ImageDbTreePanel() {
	super();
	initialize();
}
/**
 * BioModelTreePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ImageDbTreePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * BioModelTreePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ImageDbTreePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * BioModelTreePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ImageDbTreePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJMenuItemDelete()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
private void actionsOnClick(MouseEvent mouseEvent) {
	if (mouseEvent.isPopupTrigger()) {
		TreePath tp = getJTree1().getPathForLocation(mouseEvent.getX(),mouseEvent.getY());
		getJTree1().setSelectionPath(tp);
		
		if(getSelectedVersionInfo() != null){
			org.vcell.util.document.Version version = getSelectedVersionInfo().getVersion();
			getJMenuItemDelete().setEnabled(version.getOwner().equals(getDocumentManager().getUser()));
			getJPopupMenu1().show(getJTree1(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
		}
	}
}
public void addActionListener(ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.add(aActionListener, newListener);
	return;
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
 * connEtoC2:  (JMenuItemDelete.action.actionPerformed(java.awt.event.ActionEvent) --> ImageDbTreePanel.jMenuItemDelete_ActionPerformed1(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemDelete_ActionPerformed1(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (DocumentManager.database.databaseRefresh(cbit.vcell.clientdb.DatabaseEvent) --> ImageDbTreePanel.refresh()V)
 * @param arg1 cbit.vcell.clientdb.DatabaseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(cbit.vcell.clientdb.DatabaseEvent arg1) {
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
 * connEtoC7:  (ImageDbTreeModel.treeModel.treeNodesChanged(javax.swing.event.TreeModelEvent) --> ImageDbTreePanel.treeSelection()V)
 * @param arg1 javax.swing.event.TreeModelEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(javax.swing.event.TreeModelEvent arg1) {
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
 * connEtoC8:  (DocumentManager.this --> ImageDbTreePanel.expandTreeToUser()V)
 * @param value cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(cbit.vcell.clientdb.DocumentManager value) {
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
 * connEtoC9:  (ImageDbTreePanel.initialize() --> ImageDbTreePanel.enableToolTips(Ljavax.swing.JTree;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9() {
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
 * connEtoM1:  (ImageDbTreePanel.initialize() --> JTree1.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getJTree1().setModel(getImageDbTreeModel());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (ResultSetDbTreePanel.initialize() --> JTree1.cellRenderer)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getJTree1().setCellRenderer(this.createImageCellRenderer());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (DocumentManager.this --> JTree1.cellRenderer)
 * @param value cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.clientdb.DocumentManager value) {
	try {
		// user code begin {1}
		// user code end
		getJTree1().setCellRenderer(this.createImageCellRenderer());
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
 * connEtoM7:  (DocumentManager.this --> ImageDbTreeModel.documentManager)
 * @param value cbit.vcell.clientdb.DocumentManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(cbit.vcell.clientdb.DocumentManager value) {
	try {
		// user code begin {1}
		// user code end
		getImageDbTreeModel().setDocumentManager(getDocumentManager());
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
private ImageCellRenderer createImageCellRenderer() {
	if (getDocumentManager()!=null){
		return new ImageCellRenderer(getDocumentManager().getUser());
	}else{
		return new ImageCellRenderer(null);
	}
}


/**
 * Method to handle events for the DatabaseListener interface.
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void databaseDelete(cbit.vcell.clientdb.DatabaseEvent event) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the DatabaseListener interface.
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void databaseInsert(cbit.vcell.clientdb.DatabaseEvent event) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the DatabaseListener interface.
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void databaseRefresh(cbit.vcell.clientdb.DatabaseEvent event) {
	// user code begin {1}
	// user code end
	if (event.getSource() == getDocumentManager()) 
		connEtoC3(event);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the DatabaseListener interface.
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void databaseUpdate(cbit.vcell.clientdb.DatabaseEvent event) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
private void enableToolTips(JTree tree) {
	ToolTipManager.sharedInstance().registerComponent(tree);
}
/**
 * Comment
 */
private void expandTreeToUser() {
	//
	// expand tree up to and including the "Owner" subtree's first children
	//
	if (getDocumentManager()==null){
		return;
	}
	User currentUser = getDocumentManager().getUser();
	BioModelNode rootNode = (BioModelNode)getImageDbTreeModel().getRoot();
	BioModelNode currentUserNode = (BioModelNode)rootNode.findNodeByUserObject(currentUser);
	if (currentUserNode!=null){
		getJTree1().expandPath(new TreePath(((DefaultTreeModel)getImageDbTreeModel()).getPathToRoot(currentUserNode)));
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
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
	// user code begin {1}
	// user code end
	return ivjDocumentManager;
}
/**
 * Return the ImageDbTreeModel property value.
 * @return cbit.vcell.desktop.ImageDbTreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ImageDbTreeModel getImageDbTreeModel() {
	if (ivjImageDbTreeModel == null) {
		try {
			ivjImageDbTreeModel = new cbit.vcell.desktop.ImageDbTreeModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImageDbTreeModel;
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
			ivjJLabel1.setText("Image:");
			ivjJLabel1.setMaximumSize(new java.awt.Dimension(65, 20));
			ivjJLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel1.setPreferredSize(new java.awt.Dimension(65, 20));
			ivjJLabel1.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
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

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = 1;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			getJPanel2().add(getJScrollPane1(), constraintsJScrollPane1);
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
 * Return the JPopupMenu1 property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getJPopupMenu1() {
	if (ivjJPopupMenu1 == null) {
		try {
			ivjJPopupMenu1 = new javax.swing.JPopupMenu();
			ivjJPopupMenu1.setName("JPopupMenu1");
			ivjJPopupMenu1.add(getJLabel1());
			ivjJPopupMenu1.add(getJSeparator1());
			ivjJPopupMenu1.add(getJMenuItemDelete());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPopupMenu1;
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
	return getImageDbTreeModel().getLatestOnly();
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
	this.addPropertyChangeListener(this);
	getJTree1().addPropertyChangeListener(this);
	getJTree1().addMouseListener(this);
	getImageDbTreeModel().addPropertyChangeListener(this);
	getJMenuItemDelete().addActionListener(this);
	getImageDbTreeModel().addTreeModelListener(this);
	connPtoP2SetTarget();
	connPtoP4SetTarget();
	connPtoP5SetTarget();
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

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = -1; constraintsJPanel2.gridy = -1;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel2.weightx = 1.0;
		constraintsJPanel2.weighty = 1.0;
		constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel2(), constraintsJPanel2);

		initConnections();
		connEtoM4();
		connEtoM2();
		connEtoC9();
		connEtoM1();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
private void jMenuItemDelete_ActionPerformed1(java.awt.event.ActionEvent actionEvent) {
	try{
		if(getSelectedVersionInfo() != null && getSelectedVersionInfo() instanceof VCImageInfo){
				getDocumentManager().delete((VCImageInfo)getSelectedVersionInfo());
		}
	}catch(Exception e){
		PopupGenerator.showErrorDialog(this,e.getMessage(), e);
	}
}
/**
 * Comment
 */
public void jMenuItemEdit_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}


/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseClicked(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTree1()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseEntered(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseExited(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mousePressed(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseReleased(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == this && (evt.getPropertyName().equals(CommonTask.DOCUMENT_MANAGER.name))) 
		connPtoP2SetTarget();
	if (evt.getSource() == getJTree1() && (evt.getPropertyName().equals("selectionModel"))) 
		connPtoP4SetTarget();
	if (evt.getSource() == this && (evt.getPropertyName().equals("selectedVersionInfo"))) 
		connPtoP5SetTarget();
	if (evt.getSource() == getImageDbTreeModel() && (evt.getPropertyName().equals("latestOnly"))) 
		connEtoC4(evt);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
private void refresh() {
	//getImageDbTreeModel().reload();
	getImageDbTreeModel().refreshTree();
	
	expandTreeToUser();
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
				ivjDocumentManager.removeDatabaseListener(this);
			}
			ivjDocumentManager = newValue;

			/* Listen for events from the new object */
			if (ivjDocumentManager != null) {
				ivjDocumentManager.addDatabaseListener(this);
			}
			connPtoP2SetSource();
			connEtoM7(ivjDocumentManager);
			connEtoC8(ivjDocumentManager);
			connEtoM3(ivjDocumentManager);
			firePropertyChange(CommonTask.DOCUMENT_MANAGER.name, oldValue, newValue);
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
	getImageDbTreeModel().setLatestOnly(arg1);
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
				ivjselectionModel1.removeTreeSelectionListener(this);
			}
			ivjselectionModel1 = newValue;

			/* Listen for events from the new object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.addTreeSelectionListener(this);
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
 * Method to handle events for the TreeModelListener interface.
 * @param e javax.swing.event.TreeModelEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void treeNodesChanged(javax.swing.event.TreeModelEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getImageDbTreeModel()) 
		connEtoC7(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the TreeModelListener interface.
 * @param e javax.swing.event.TreeModelEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void treeNodesInserted(javax.swing.event.TreeModelEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the TreeModelListener interface.
 * @param e javax.swing.event.TreeModelEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void treeNodesRemoved(javax.swing.event.TreeModelEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
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
	//
	// if selected parent of VCImageInfo, setSelection as it's SimulationInfo
	//
	}else if (object instanceof String && bioModelNode.getChildCount()>0 && ((BioModelNode)bioModelNode.getChildAt(0)).getUserObject() instanceof VCImageInfo){
		VCImageInfo imgInfo = (VCImageInfo)((BioModelNode)bioModelNode.getChildAt(0)).getUserObject();
		setSelectedVersionInfo(imgInfo);
	}else{
		setSelectedVersionInfo(null);
	}
}
/**
 * Method to handle events for the TreeModelListener interface.
 * @param e javax.swing.event.TreeModelEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void treeStructureChanged(javax.swing.event.TreeModelEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the TreeSelectionListener interface.
 * @param e javax.swing.event.TreeSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getselectionModel1()) 
		connEtoC1();
	// user code begin {2}
	// user code end
}
public void addJTreeSelectionMouseListener(MouseListener jtreeSelectionMouseListener){
	getJTree1().addMouseListener(jtreeSelectionMouseListener);
}
public void removeJTreeSelectionMouseListener(MouseListener jtreeSelectionMouseListener){
	getJTree1().removeMouseListener(jtreeSelectionMouseListener);
}

}
