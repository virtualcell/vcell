package cbit.vcell.desktop;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.image.VCImageInfo;
import java.awt.Image;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.solver.*;
import cbit.vcell.mapping.*;
import cbit.vcell.server.*;
import javax.swing.tree.*;
import java.lang.reflect.*;
import cbit.vcell.biomodel.*;
import java.awt.event.*;
import javax.swing.*;

import org.vcell.util.Matchable;
import org.vcell.util.document.User;
import org.vcell.util.document.VersionInfo;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 11:34:01 AM)
 * @author: Jim Schaff
 */
public class ImageDbTreePanel extends JPanel implements cbit.vcell.clientdb.DatabaseListener, ActionListener, MouseListener, java.beans.PropertyChangeListener, javax.swing.event.TreeModelListener, javax.swing.event.TreeSelectionListener {
	private JTree ivjJTree1 = null;
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	private boolean ivjConnPtoP2Aligning = false;
	private cbit.vcell.clientdb.DocumentManager ivjDocumentManager = null;
	private JScrollPane ivjJScrollPane1 = null;
	private boolean ivjConnPtoP4Aligning = false;
	private TreeSelectionModel ivjselectionModel1 = null;
	private org.vcell.util.document.VersionInfo fieldSelectedVersionInfo = null;
	private VariableHeightLayoutCache ivjLocalSelectionModelVariableHeightLayoutCache = null;
	private boolean ivjConnPtoP5Aligning = false;
	private org.vcell.util.document.VersionInfo ivjselectedVersionInfo1 = null;
	private JPopupMenu ivjJPopupMenu1 = null;
	private JMenuItem ivjJMenuItemDelete = null;
	protected transient ActionListener aActionListener = null;
	private JLabel ivjJLabel1 = null;
	private JSeparator ivjJSeparator1 = null;
	private JPanel ivjJPanel2 = null;
	private JLabel ivjJLabel2 = null;
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
	if (SwingUtilities.isRightMouseButton(mouseEvent)) {
		//make right mouse also select
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
 * connEtoM6:  (selectedVersionInfo1.this --> JLabel2.icon)
 * @param value cbit.sql.VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(org.vcell.util.document.VersionInfo value) {
	try {
		// user code begin {1}
		// user code end
		getJLabel2().setIcon(this.createImageIcon());
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
 * Comment
 */
private javax.swing.Icon createImageIcon() {
	if (getSelectedVersionInfo() instanceof cbit.image.VCImageInfo){
		VCImageInfo vcImageInfo = (VCImageInfo)getSelectedVersionInfo();
		return new ImageIcon(vcImageInfo.getBrowseGif().getJavaImage(),"preview of "+vcImageInfo.getVersion().getName());
	}else{
		return null;
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
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setToolTipText("preview image");
			ivjJLabel2.setText("");
			ivjJLabel2.setMaximumSize(new java.awt.Dimension(150, 150));
			ivjJLabel2.setPreferredSize(new java.awt.Dimension(150, 150));
			ivjJLabel2.setMinimumSize(new java.awt.Dimension(150, 150));
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

		java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
		constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 1;
		constraintsJLabel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel2(), constraintsJLabel2);
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
		cbit.vcell.client.PopupGenerator.showErrorDialog(this,e.getMessage());
	}
}
/**
 * Comment
 */
public void jMenuItemEdit_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
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
	if (evt.getSource() == this && (evt.getPropertyName().equals("documentManager"))) 
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
			connEtoM6(ivjselectedVersionInfo1);
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
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GCBFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBD8BF4D4553528414696AD2ED29B7B32DAD42AE853B6ED515216BE51262F2858D2DE343CB62E07ADFD252FD8F915F671AC551B9F49C840A881228408A142C40748A7A804G8999BE41G11C48890A013191BE4487C1C3979077076BE7B6EBBF7E66EE4E2DEE9565A5CFB76796C337759E76FFDBEF7108AB6CD4ECE4FA8102474ECC97D7BA0DD129636C9D2472287D6B1EE4F5639A6C977779640EB5269
	65D970BC86F947EE19EB4A173E55158B6996AC9BBF57348E5ED7CB533A92DA61A58A9D9F643DF16EA7D3C6267350992613A352772D4903A7F62E95F078CC5A0A749FDED3286263D4BCC3FA86604CD866D7EB0AD45CE2A8138B38EA00A6B315F74113BAB03EF20FDA2E6576295212477D99AB319DDD1B8C33553A0B55E73174E2FAC299524ADC1B2756B1C33A95G194F14FE38338C1ED58DDD9D876CEDCE253C47AE3BDC658EB95025F8FD1515F5EEDB07ECEDEB724BF2034DA33BD0FC6FE0FB9F2E4D9575A5A98D
	5A1AACF05F5C0FB41A615D87907F8E63FF1E0D7885703E8C2060DD66EB7F56AA355F4D035F13D2F73A33DE0D421765DD6227695DA0DF456EE7B25E90E3705733A86B0910B78DE09640A200BC4BDC1385E03D987B3F1FF9871EB5EDCA2D576D76FAEAFC3EC3AE077F0457A13B603D3292F2D45CE9596E75BBA40946573456E2501F11006A2D7B5475C8A6D3F771D8A71D7E2614765DBBB2F28CE412B6F5B043EAA1D934D802B2A15A8DD1E98FD9C2F5B11476D9C9CFFB6273CFE5E69B50CECAFBAAF3225A4E949D6D
	05969C6BAD87F02CD3609D6B211E92FE3740DB368571241753044EE03A8172E6ECE35DE8DE45FD294F3ADB4A5F71A5C3B9A4BCBAA143DCCEFD28AE8F6FCBCE8E366DAAA71A5B343EEC9178B9BABC7565A92127182EF88FF4F77BDC9369574B67F01E666D1CEBB283D483B4837481CC82183A13F56C485977C629E38DB63FF2285B69F248FE096A3EFCF6ADBC65BE45EF2B7384941B47AE1F31791D36B6179C1054B2394DA8033610FAAB1BD1776DC063286C117DB6456975C06BB6372C487EG15D95B7C9A16714B
	A23749691601005BA7F13B9B1BD7231CEC8165E31F4326484192143F2599472447F686DA0486601D64E23900721A857C4F87D82C4961AE515E07329F8CC6E5E53D575B556DEBC2CE246ADDA8678EE1BB32603DE8974B585C0238B8C8A76E62F666B53F2EEB476A34E377ED7EFE1EE3168348FBB2141F833088A037824688203A027568CF4D2CC756155F10ACCFFD51D00F0A1FF9AA33250274C729881FCF7B849D6CEBA2644DGC883D886D03C1B7D408F76379A4CAB6C8F768B7B447DB41AFF559546F8E35B7214
	A1AD131A3FC2A23B1C9D250D15232033CA9B63BBAE6298D7831FADGC33BF9ECD61FABA6DDBFE48B482C5D277C201832A3B8D2545F15B94DEA1FB6GEC1248176EC2FD87F89BE0AB408EG74C587818E839C85E884B8873099E0AF40BE007DC897G675A3B1ACCAC0246A4685F8C0085GF96F4FB5D5G34G4481A4812482AC83A87DE02E299EC081C884C881D884D030E72E298A4081B081E09AC05A9E9E63DFE7372613ECB2C7A9C3BD68F95F2C633BCC65E3154A538ED5B6DCCF4F77C11577D5BA999951EA54
	3D1F0BD2C69E838F7F8CA0D93F3F7C7FA36BEC359D13AA6FD7C51B1234C6A27B3ECA15B95A29953AFA7AF1D9270E8D4F0575A30CAB3FEF0AD2E634F36E1F8DB417CD67D1BEF20F6CD12C32CF76B860A52066651FC7992AFE295D2B55D9AB70BE70E33560B8829A3E68BC4A5926A8FEE7DB37A2B74A0A6274F48473CB852DC0F71BAF688219563B67F15C3C3D604734725B4F238F30FBBD9ED998952D0A1A3FD378A059A53B2153355EEE0FC278BD97B0CE6F127BD59FD8F5E13904BE4C550D7E14E25FE49B0E3D4F
	6FF40329C217ACB56DC57FB43C36D0A723AC40E18CEF06E755DE76AB3D1B592F148E5CAF4D3C6C4BB00A15923E76790C443DE45B1377867D8A75E9DB0BF15FF789DF28628354572317D12E071CDE91CE686371CCD1E760AD142B55EB6F46CAA7EC9E085B7DEADE77DEA1F3E8D008D03430C34D3B2D25C04B139D2A0747115EC179BF590DB2BA5E6075F57BCE481EEE2ED7F3BE9F1E3A6501E815FC464FAF09788A78DDG30ECAF7B0CEF35E0BE7BF355D954FADDEA3FA02F2A9265FF530182D5FE851036D4127CD7
	E97966031C9F876944087CFF537213A01D224B2771F5D56B47B7D5173F8C5E6B77E119FB328BF4E58A76854B90AF099773055CBE6916A1D4436066C3CD576E3978FAD85E99D56FA87F6B97D126AEDDDCB4CF50746770FAF012CAAFE99F459C3360399170D42EAA174B15D601CE8234D4D1DC52D725162B42F24BCDDCAEDE4D1FDCC56D4E28A2DE283DC734F2257BA93FEABF45B8216DD98B38DC4BFE222B28651377D3FB3E83A26E5656358B55FC69G15AF04E78BG153BF5AD171BF1004A252A65969D2076068E
	E2394B6B38DC56C17265C587297C7603AA9F22FF6905DCEE56C10A4D662B65164233211A69F6EF62F20EEA62F3881E93G122A75F4EB0B357ED5D35C40F4D32B75F4FF27154B2A217CA275D9D1C36592EB305CD75F526856D07CB1D4C37CCE00E739561E3BC4139F6025C3404BA1EA2F7510DE4EBBB6F33924C324A74887721D92D26EA52D5CFC351D250708EEFE2D3EFFBFAF557420166221A2D50E58EE30FFB73D4D652AEAA93F29B6CC2FC4393AADDCAE31B658BFACB72DD65F3F3FEB65D255F20BEA29FF4BEA
	75637164D6AED7E025784CD88F3CE6AEE731D2FBD6AB151BE025F2E4D3DFA8C3BBA076BE2E58FAECFD6501DE70D515154705E925FD0E05E22D026527D8F9BF2360CD34EFE910DE8210F718FD42AFD672FE46242E2912EBF1EAE634FD1A5243540FE30703BEE156545ECC321FEF34A24DF848CBB84C36F34BA57D1A2AA690705A1DE2B9275A1427AE69FD8661FEF56995F836BB7D81258E1CFA9F5B1F7917303F385ED3516A7A778131C6C77B1E84F497G949DC17C6F8F20AFC5DBC4FC5AF7B11F93873EA635FE71
	4B99AB227059FA04789BF7C4379F35E5F7A651BBF51069CD077C45C7185E0403C88FED9A15FBCEABE7BE8AF360A817FB279A4B212D2271DCAE663F51F8327722713B339A5BCB00F61281E69D657A8F6F986DDE18F0F6926F1BDCBD229F77992FDE0DAA0F79C7C98E523120BC9AE67FD515473F64B01F55105F87C03E766F8750FFBB8CFD7B16DC140152698CD0DF7EDCD32D521A7D4D3B2569B993B20C7654BA6FCB48C8D67BB0CF579769374FE494GD0FF64DCCD6EF5A077BA760DD7FA753EB18170B3G6657F1
	FFFFB4007850F803631445DAFF0946FB26BCF5FEAD017A16635C469D349687D9B7F2BB65BCEFD776660767194B4E907FF72F5673DFF56D12A17F712FF6E6B80E935F930E8779AFC8FDA6431C4A7C7F3709791F87E572BF643ED5D72F168CE3A6F59E398B1827E3D0E71CD6AF35G579EEEEFF7C0AE977F563A1C76AE59E1C6EDAD3775AA65A286AFBF01F9077135CC33D5EB4BF0CF4FA6188D1C72081A667A289577151D213EECDF43FD198E7DD82C7565CB826F74B895274D659C104DE5C46736F7B02E75F5C3D8
	6F04F69A7CDE1F6CD77AD5D2AE1B27237CD45B95E0FCDFA4266C8C594C59EB58E61A6B59E6FCE323FEDCF2ED638D47E5429FBAB2DA6BE9BC626B0363B284E3B2D19B170756B2CFF320CC6E8966E9433AF01E88FFD460F1B21F46450DDF76D3BB716B381DEAE8236F845B0102837894EB85CD7E0F9E42353758C31305A12855B5E7AE2B0FB49865914576297662EF9BD8962556A9521EA5DD063290EB06936A9CBD9114C561C33BB3AD8F318DCD5E40BC18CF429CBD4932D83791ED3F8BFA9BD0CEF9DC7D4CC7D67D
	07127089B28FEDEDA78C212C9F60C6D83CF9BA8C8658E8485573007A0DB8663866CEB849F67BAB1BF87E4C81DC6EA96EDBF6B15BG32A70FDF535B136AD334AF68504AEF93651D308824F6DD454CF39F14C9BE45633622F7F36898555951B2546B1CC1B922762973F455DB5C4EC2E823380169F53FE5BCB752F61742D376E0485E64753A1A1C3E00B9247563689C3439B76BAD663F995A1E28357FDE8972E373F728EB2C33A521B6FC689C6B4738595324425537C6550FD98D6AF9CFC3D0BF0E7D66190C165F70DC
	3923C453710FC047BFE29BBEB1CBAF73640FE8BFF35EC71A4FA95929591BF2FAB4487EF62F5F9DE92FEAC4B69BAC12D53997674B95F43AF50A6C360231D4644BB5218D6D0830E03A26D0370807731B1907C5503F52535C475FEDB6720BD4E7EBA94B3E854ACF526A5CD6EAECA32617E68B5BCCFE1D5AD82A35B19B6AE7B532DEA73D4D7D29821C5248ED2F5E02F1004271C1B93E1D0437GCDB1129B2B8A712B8593A4BDEA5F3F055BCF023697B432FE3FE8C23FA37779EC9E87D6EC72FE9CC06343F25A32157BDB
	5484B1F993776B562D2CDF14FEF02B31BD7C6BD626BB816A26B4B13F89E646AF83DC45475C6E4BE65C17F45894DB1B8DDC9A3F1C165B7DF220532CB309F661CC9CED65D6350C2AAC4457040BE82B7CEAB5E0C75D83FCAE629C4B75F1F01FFC1C6E1D014EF762C5FCBE69E3BA5F495670DF907829BABC7177661B4C5F8C48ABBE43E79271F6DC9F44C1FAB240F400B9G0B81724FF2DC5EF99977EAC7BC732973A890365BDC0DA11BDEFC76520A326C127B55F4C92B5E77109C128F209CACE7C7F7C630CA2B772F22
	1E75D354A37BD3F6DEFF5E1C755A710C4CA876C7D15B0EBB9B34BFA43B3DB25A1794404F87D88A105F8C6B34E6165BFD2D16D8F26373AFD5E04453478747A68B7B41CFAB8B2A7742986925D7FFDAFA2423ABEBD0979B1AC9C7F3B47C9B825F2943136EFED52CE5B0BD8E7296B4336EFE359D65FF6C9C78D100A100C9G534E317C7F27B5265E9E6EB354DB626F2E1CD8675B2967C2F927FA5F0AD9EFE9D8BD6267AF9D38364BBF8FEB84GABC0ABC09C40E40014734CD70D6DD0AC3EEAF8F33A492B3D5606EFB0F3
	FF3BF24746E7FF4CFAE967C36B29FB52CDF86EE03006C5032F6EDFAE69556FDFAE01F60AAF00CCG1A81A89E3B2B1763B15F85BE3F5AED8A6F53548B21FDB06625B622DEF9D8BA65025118DDF1621825C15ED2007C9698BB0086G974044969E33C9367DB1F5917DC5133796F0473C5EAE0361D27B3AB8222F239B2F17E256CBEEB12A97A8981BCCEF082837B1ACBD4B105E7BEB46C64F9AD1AFFC8C679B527B05283734E5F4F6AC6FA25B1FE72F605897C33A8A20894087B089A099A075A20F7D6FEC7BC6B97694
	9AC60EBC75F3C6C42CB0BA39BC9ED1AFDCAECBAE9A553BED5D5868A5C6548B2717F749E8AE59DCA84FD2482B86E886D0G92G26814C3B44727C202DA616BC356BA12319CF8FED989B1FA991754275FB69A523FA9FED44FA7917A92F747248FA3767B25B36A91B46B66F3F9333DE7DE5A3BB7DC2FB143D63FADB9B6F9D27CBE803F18F58217553DFBCB6F90E0B28972EB733AE9B55ABF8EBEC74CAA26A05531BEFC86FE79175C6B78EE9B16BAD3DECB4AF1EF50B382095FC8B00952095A08EE0B2C0CAAB4F0B4F35
	450C8BF473A2F6DC10D8B2B639BE90D1AFFCDE24359A695B2C6C75467A46ABECF59F6627D93C77362415F52EF573587A6A0928975ED70B4D285E36523169403E18752C0674DE9AA33D3C37E355EBB124771D08FA2313674C08FA61F3CAB1245737E5EC74EE0E2897CEEF02A1BD4B56314933B2E63DA9067416C5549B9DFF2F18E371B743105ED7A26A0D0EDED2CCFA29E174683EE356BA1C130BA0CFEA837FADF04F96A22E9452D5EDBCCFEFFB93F12D10D6B45C1D820771BD1ECF932E784D6698F2E640360C70CA
	94FC10573D2326916DFED6347B0788E413EF37C73FF714E0B73EF774953B713D23A97657880F07963A55AA67A759C73E17B44DEEF4AFE903606F698CDCE37B3BBD0A53AD8B32E41365BE3CDB861E63F437075C8AD28E046FDFC86C2BB6F9F19FAE3E0DFD52F147CEA952A79DF6B8952F1F9BE912ED7ED36D6D1AC3EA7449F66EF30D9CDDFE47E4E3791D140D6557A40FAC3F96B91A7CC8DFD60B7D09A9405BF400B91A7EEE9278C5104E3353DD8642FF2DFA347A95E2BF22686D9C95BFDDFD2A7BDFE32291CD1FF3
	4274DA5D47EA0FBE865FECB79E03E96D46E37003761147605176E8E3C0FA7AFB856FB7C65C15BB89D10D44672F73F2CCEA1986455B30D3EB7B650E68FCAC6FB466A32F93E35B00375BEF17437B3A3A33D04B8B86D19828E0F7543B02223EE273F748913CEE9675A9AFDAFDCBA71E3337B99DCE3FFA030CF83CBBD74FE30544B1D447E11413C18C55E877FBDDAE3C75A1719D31A75E8C3F8308E7D2965061EABB6F31D87A789B18E6BB5F8DF9308775FDA22427824C34333E5F5B0B3EE31E5DD8FF9729F849914C27
	B1FDEC6D2BC2A78281B9A0964238032E7269B2B11F0795EF6D8ECDF60AB37AD88EBA0D477286E774310CF7469E4B5B1D5147B24199FBAC6FF2C653ED62F56BAB7AB15D2D619FA8504BA008F72F5161CF208E105FDDA84E268BC04E258E76453B449D1F8AC8DB9DEC1B7FF8A53A8C7FFC45D806CB2FC417614BD7E24BB037AB3A8CDF6D0AAD43422E11E57842DA3DCCF6EA781F2CDB4132ED77FB5D20C954DE17AB3A8C7CAEE3997439224B601AAB368C62DD51E570F9D7EC99A438C61641F39B747A92540BCEF1FE
	5D84636F507462C90173C1FA1C06AB5C043809101E6C60F330DFEF524B75FD097D4FF2897DCF32A3F44E272869F90EA83640GAF49E0F364D0BC513E3FB8F46CF62A788F5F527339D7AB1FDD92D23EE117AA5BBF3AC750F7F794FDF70F206F6ED1683BE784FD770CC25FBD46767F5FCB743C6F5446E3F6D6F87CEE8619D64951E3E1752E4CE67D78FE20619D25FA7C8E8D3F7B6DD099731D69BF3C2D6FD7301DEFED516377E8F87BD6FD7B3BB47CAEF368186FD271CF19756D844B2FDF07F75FDD36FEEF3782EBA2
	9975F867BA76EF8D106E841888B0C5A6BFC77E725B3DC8275DEFF343949201E7C23BB897AF643AD910AE845883508C50574EF362B347D166F41312F5727E9582A7F648F6321F772E544F5710D825D08E16E799B6F845FD74C69942A49B44490CAF3C087AFBBC743E01C4F79DCE57045FAF413B8E735B79AE04A364AE4412F64A47F77273196BF9CF2528037D7C41DE7473CD10F68144F5301F5F51B7529AB831A35A19F519E8AF39034E77B2B57CAE011F294353395F8FFB343BB510D751497669C7625CA78152C9
	9AEECD9F2FB5E7E838FB87447751D5A007CE6DBCFF8D5FCBA9F50AFAEBB11D65647C63EB557C1D145FAA521DDA7E350DDCFF48095FCEBF5FADE9E3390073F6D15D97C5FA26D6773DCD21ED9F966979CE6E2FD96C25CAD7C05F2EB06E7382D78A698A8D37C4602AA15D2C616E945F023BA09D2F61E61EE119A4EA38AFD40BF5BA24333A9877DB311FD688E90B0633E48B9A10EE6862BB926757717755AD001B5445F3E8C7EB481DD43C6AD2D9F9546FF49C32F5540B99C9BAF63758A3407A333BD8476608B633DCB0
	A781AADCFC172EEF8B5FB7CD3A77EE29F51171BD6379CDD7B36ADD6ABDEBD770FC3E427DCC86515C788653CC027C85AEF10F6B8D7DDC403179DCA66DF3D07EF0C8BE7A5A87A5FDFEF2C8BE767759F4FD7EEFC272D1C6AB54FC626D09371937E5CFA4CB2E876322FE6BEEF193CF8D6AF36202BB5363168EE492EF3754A0EFE893625DBC9637ABD16E87EB560466F06E7A70B93C489D1C3712A75A9C7E2E182B851E70B97CA301AF75044F61433DBC07ABA0AF41437AF5CC1B2F4BBCECD75E6B4579D06F6574DE1146
	BB7214FE24E7ACEB5D51C010662F2E9B0DCC096DC4EA2F7E9E965902BF8ADEDB219F64FFACFD6CFF1281178230GA04B87B1BDG79372787380C95F0ADG89GC9BE7A2E00E83D3B967537C49C2D173B1C9E39D169F749A51AAE6E177577E0368F56C73D871362A31DC8D51F4B56FCB91DE8740835C5C90D2743A5BBCA285D1FDF50371B7FE5D7C6FA14F633BE217672BF894EDFEAB7FF93FEE30436369CA22413F22F784448DC46F23C9012FF4A65884BBFA83EFBD46896E39344F692596CEA7C9CC0ADF32FA46E
	0DC1A665597B03F9AB52313E5B5667F4F73B9B1D8332B474894AFDF3BA72EB057706CFD84E49E2DD63734B6D325FAFBBC4E9C9AD7F18304DB361FDB600450F38DF1E312A7CAF7DA4F22E719CE24803BA88EB54F4311F66D93A5657872AC5DF1D1E303EBEAA6C3D82654779392FAF8AFB6F7648EE2F47E927F22F89FB7F98C012D62EB6BBD4B6B78F20EC54731608B35ED8201E370C315EFD1F3A9E59253B8B517E4C50490B70DF9778D9BABC592B27B59F178AF9FB82FC566F9C443916886969GF3G968264AAB0
	9F95DECF3F5F33DD0AF17E56647555438244255B7AA27EB63F71E97923F976C88E7B12E9BFF8C01AFE33B56AF799FB945233E675E97E6D1D6978A47E168DA1FF7110B785E09640FC00A5GF95D4C5FF00FFA1F8D37EFEB9445E66F94DF62E81B41CE39B70846E0DA5D732FB13229455D540F8A75F92CBBF49E34C05A579DB9B762G17204313BF59341E4F9F123A59471C5A40BEE6CEB75B776F8BBBDB5C43699F0BB4FE5BC1698FFB7F91769F5B0F2E4B0EFF880DE8FB38E4DFAE77D9E35455439E68EFFDCF503E
	3476045B17DABB2E2F049B59413874BE3C076111FB0B2DF23B2D5B25E8B7FD4DBA6409EEC5DCA6D7AF8A1BCBDCDE1B83E21292CB3B4D95104B707B8F0AE7CE77719A445543315AE77B42E335F7C3E23599FD61319A76B9187F273E70D86D098CFDFEF1FFF82C4675499E3CB440714B9468D3DA8F47D9738E61DC8F0F3344F5684ACA1DB89AC33E90A2BE0F8CB21F853D9C87E7DFC3BA7510F6G8C75325D59B40C7AB9291767600FFB50F71F31DD09BCC981A45EF521AF9BF8DE3FFA8D9E6ABAB427776DE8F5E176
	9E87B6680B07A69BD4D7FD65DDD81D3F236EAAD647DB3B51EEF542D447CF034CBBF6EF379443BFE5E417A6776A7D7F1ED7EEB47CF69CE1EAAF4D15993D413802E82F488852FE704CE8E813FCF34FEA7783204DCD223EC4EBAB750F5A0F4726D5BB7AEC9E5B51222F5FA3D9FFE94D589C253F16BE75FB4D3EE03CD27AC9F1A63579672CE01F1F289CCD1F292CB99B1FDB9DDBD59BB1954D317A7EED5DFB5A3A6B951F866D53DF2EAB8DF5EFF5B07ADEDC7F2071DE5C35C163B3257103A31FFB5EBA987D7798A68D6A
	FF0F01664C23623B78E9B03EF3G16G6476437A341F67E049904EC13AE59B6E838FBBBAF0AF90FF5DC8E17A59D723731AF7B576EFA22D390A5F8E34F995456B4676993F7EAA0E17627589A4610A2F220C7C4E0ECE050A726F9BDD957B83F23BC013FFCC966B47BD405731FEED9F651058A30634CB43FD2782F163A0BDC543656CC65CF4C827EA3897AD08DB88E9E900F1755B44D98424AD9A6E25CC3137G695681DE4B9CDB4FEB99E500766AA640F372G5D7BA51F563E01FD5A7401C87F7A327A47BEEDB61419
	BF9074E9CB8642FD5A325DE8F3F5A7E3BBA2ED456203C6B81A6BDDB941F9F9B6E4CFCC9A1CEBAA989C79DE9F554B9A0C957719435A21FA2F472C97FE0FC90DEFC26AD9E374319972A9DE6E977E49B5989EC75FA870FD0361F1740AEB9CC747E19B03BCF7B68FE39C918871D10539A85B7AA19877A1AE77DF6237EB2279B97E2E0F30E259277237722C1E3FF8E8AFF1E8E49E298FCDA6542DE4A8BB9BE0B1C06ED51873G95G47GDAGFCG71G138126814C82C883D884A08D43188FF37F1F4CAC9B217F6A6618
	B094FE1B93CD05BACF9F4F0EEEABFE119D5BD67CCA2C63E41863CD7DBE758C61BF33D1665E7676002C59248559799A2EAE50608DB8B1C821F971531A50F55F7FDE1034B9E3B9E43CA6368A3BE1867EAB06F97D78D8857637536BF78EF8BD0A4D05CBF02644EB6385953CBF572A555B38BB7ADA7A83E1F7A6C259C42D7CEB5B50BE5ADC4E8E8F5A62E6AEDBA6EC4FACA817261535EFB3DEA78F0A32F957661A8A2FF159B5D92BC37ADF439454BA6FE5F15F2D57F80F54371E75DE81DC52B5BEC77794F1598557B866
	CBAC4232C563C0E6G7563386C17B6F07C6298E7943F0CE4136E3A08A932339E91CFA7C29B987392AEA0F053G07F1B41D3F4FDC0D72F6FAF0FB2816FEE5CFEB23CD442AF3207C42F1EC3F278E8BD95DB057E43E0157CD57C41CC96F713A77572EDD7FF8651D6BCA03664B06DCFC57E28CB2B97CFBCF3985126EFBCB7113959405133CCA4F72B824EBF2ADBB4B63102E5168B4C51F7BDED36C395FE7B20E9306CD11F142CDA663B8619613BECEA05CEDE24D5381B62BD9AB77858DF7CE43A508B691F75669D0BAB5
	7CD7C43F90FFCCC6025AFDB6512FDE01EBD6F1776BF067780E11896D97308CD3116BFE5704B62A5D69F2D17AFB220C538366CC13478C51C7274F266B77A39A8E7BCDF3A66512A431FD3B2F827DE614187D7D5D16621D5B357D453FD5605DB87D722F917F6570AFD0DEFB78E45361539A1D5FDF8E52F95EB29ABAC6EB9775779936B1EDD137566B76F9BD7835ED7D61A3CD17EB6A6B0E1EBC8154B55A2F5802F5DF489CBD0FC732C64B23FA2F36E8EC7BFB15E32C97785475A8EE49973FA9D0F5C3F8BC7306409F3B
	A1BC1E393B086DFA9364CD576CDEE248B7A0C5B74EB5ED3FF164F803768FECDBF45FE80B69C278223E15C1BCCF97F5ACE8DD6477761E3079FCE23DC231736D035AB9G50EE81C8GC8D27BC1F59F9E967798743B91927B49F943FC47AB65C666EBAD3A86D5461BF46FD30B2E3FBDFF643A52A07B7B5D8D11B6F97A863DCD26F17F7E20369F8C32493B0977FFFED6B8427E8FDBE69ABFBA822D0515222CFEB3B3445FADDAEF62D8A0E798698CC1FA92C052CD9C03BCBF0C34F7E832F9B8E65CEFAC53A747D8EF7990
	56CB39A93ACE4F033CC53AFC1AC703029FA9AEFCFE4589FCDEDC787CDAB94CF2AE043C21B80EB5B688D94D0774928D57ADF0D963A1BE9F4F380785EEBB246B47338E5FB09C545B4F6A5ED78FDFFF9D36DCD79A249B95C31167089F8E21DF740DE77D19B004712E5F6BD54EB883CE3C29CDF3FE4A904F7919DA592707306D705FB4A29B737C905B18B963692CD10C75F83D0D318D7D237603293D9F8ADE94FF37CCBA326EAA6B08F473DC531E1BF96EB4893C8F5271083B1947FF435531697C3B1F3A9E6976AC510F
	69B7076B7CAF84BE6566F01DBF2F71B38772AABE437D7E5955202E7ED2775EFA757A6BED7FF525C17FC74123D9FB0169246724ECC5FA0DFE3EC77D01A97C959FC5DA89CB3F9EA7E80C93FF60867C1FA2AD274BCE813F74FAE44CD2F35BFF7CB2693235A0C34A281316F78A23DBA7E57722D76D431FG3F83A17009B4AC8EFBA96517DE55DA2A93E1637C1EDD15193F3DA7B3C34A02E6A80C2E1332B09606567C54EAA60451224DACBD04350DA931B7247F83A6448F76D4EA5946CD60DA22E62414247D0645B57D9E
	7B52717628FB6C93EE21F9BA7916705F30284BC1BB1086782537705E451D4B71B73C221D93B43AEDAE972C063BEABC8EBCB3B8542F48413D5613B94206F099B51372BE9677722F686A7C9FD0CB878851BD7A22A9A0GG90E3GGD0CB818294G94G88G88GCBFBB0B651BD7A22A9A0GG90E3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE3A0GGGG
**end of data**/
}
}
