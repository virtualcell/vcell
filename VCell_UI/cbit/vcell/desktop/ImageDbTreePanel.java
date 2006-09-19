package cbit.vcell.desktop;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.image.VCImageInfo;
import cbit.util.User;
import cbit.util.Version;
import cbit.util.VersionInfo;

import javax.swing.tree.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 11:34:01 AM)
 * @author: Jim Schaff
 */
public class ImageDbTreePanel extends JPanel implements cbit.vcell.client.database.DatabaseListener, ActionListener, MouseListener, java.beans.PropertyChangeListener, javax.swing.event.TreeModelListener, javax.swing.event.TreeSelectionListener {
	private JTree ivjJTree1 = null;
	private cbit.vcell.client.database.DocumentManager fieldDocumentManager = null;
	private boolean ivjConnPtoP2Aligning = false;
	private cbit.vcell.client.database.DocumentManager ivjDocumentManager = null;
	private JScrollPane ivjJScrollPane1 = null;
	private boolean ivjConnPtoP4Aligning = false;
	private TreeSelectionModel ivjselectionModel1 = null;
	private VersionInfo fieldSelectedVersionInfo = null;
	private VariableHeightLayoutCache ivjLocalSelectionModelVariableHeightLayoutCache = null;
	private boolean ivjConnPtoP5Aligning = false;
	private VersionInfo ivjselectedVersionInfo1 = null;
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
			Version version = getSelectedVersionInfo().getVersion();
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
private void connEtoC3(cbit.vcell.client.database.DatabaseEvent arg1) {
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
private void connEtoC8(cbit.vcell.client.database.DocumentManager value) {
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
private void connEtoM3(cbit.vcell.client.database.DocumentManager value) {
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
 * @param value VersionInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(VersionInfo value) {
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
private void connEtoM7(cbit.vcell.client.database.DocumentManager value) {
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
public void databaseDelete(cbit.vcell.client.database.DatabaseEvent event) {
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
public void databaseInsert(cbit.vcell.client.database.DatabaseEvent event) {
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
public void databaseRefresh(cbit.vcell.client.database.DatabaseEvent event) {
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
public void databaseUpdate(cbit.vcell.client.database.DatabaseEvent event) {
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
public cbit.vcell.client.database.DocumentManager getDocumentManager() {
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
public void setDocumentManager(cbit.vcell.client.database.DocumentManager newValue) {
	if (ivjDocumentManager != newValue) {
		try {
			cbit.vcell.client.database.DocumentManager oldValue = getDocumentManager();
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
	D0CB838494G88G88G520171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBD8DF8D45531D103A0868992B1EA349183C68898B5363122A2059AA5AAEDE9CB5B3452961FBF25FD34CDDFF9AD35B749A211CDC88229C6889081A551B492FEEA882804048AB4C0A4E18D9AA11B5D9B323259DDF66F3281A23C19F34E5C3D3BFBB79B73CA73FD433DE74E4F1C19B3E7E64E4FDD24527509CBF3F2CBA5A9E729A47E5E4B1124673BA4296A2578D5040BF5180C12666F8D0092697D9C83BC
	93A1AFF10E49D8AC5D5C1D8F69B4C837BFEDB23E866F2B242FF547F742CB98BAAE48BBBA7C6EDF8FCCE77547C4E799CB3F373CG1EABG528107CF0393699FD9DEA670D982CF10138BB8D6660B656582379F521DG71GA9CEDEFEA6BCDFE11407555709F29DE312256CA75C3905580E26CD02CDAEDEF73FF8D6C8FF4AD964C2DAE3D9BF31CE942453811079BC69412DD5705C5DF27A548716AE1BD2F356A25B6DB5D659F3DAF13A6A6B8F741ACF49CDDDEDEED9EEB1BBE4BB14351F4176FE32BC1F5517246D10DE
	4CF045F5C8A3955EB3G662B78FFC8085F6DB6995BG6CEE626B7D4D3BC53F5BAFFCC3DAF0EA56ABC5E1780AF6F3FED25CFE3EDA5E6E4AFB1D0D4127C7D1564FC3DE0147E42CGE8823082C4832CE1E3EF7E78EDF8B6F4A90D4E5EDE272341655AEB373A8FB92D329D5E6B6BA1C7600E4A96275BAAC9FCFCEFDADE2D531F0100573B73AB5763B2C95EC6E31DF2F42A14F55F385CE5BAB249CA3E101B606132C87378E542E94F8FCBFB2EA7D09783E9FFA2E9E9CBAFECDD36D407F6798F36AEFBDE3433C4C3FB0F02
	E3BD0269C0B93CE3BD54D30E3F116123B5F82E172FB2BC2613A12F54CB3A513E027A52EC98AF95575E28AB077899B139D1DE5E07F8EFF0DF9CB95876CCAF2779F735AF23983E722C9F4F7B321B69A926E3A0EF314FE4647AE5E943F9EABDE7B2C681448324824C81D888A01DA79D1B74713B0354319633DB596B35592D32DB62F5ADC7DF05275C27384D879C9E456C3048A6335BE66E324B9EC914319FC599F48554BB43F45F82B4BE129D325B2C581C8EE85D5CABAB325B434B1CBFDA02E55CB24BED33754AC020
	57A5D13BD79EC563EAB7FB14E3AE2BD9117DA5F87E4823B8A6E74DA6E8919A00F7AE1758E3A82F8A603F8EE03FAA07252D585E3FE4B7980C7A7AE62773345755061CC87167D14EFFE43623835E1573A46398936232A01D2D36333BF525261DA61B853BEFF61F23B9F6DD9D721EFF41E42C84E88268840882083FC0FA741BF652232642A9D27577EF505523D85069348BDCFFB2AF844F27DB999D6CEBB66415761B0C5B815AG7A7A498F1457F56A4C2BCE51AF6C9375D3EF7E4507416B5B1677F4E93DA27251E921
	DDDE9A260D42C150D9210E711FBE41B10E87BE53G6676535878DAAB382E6FB5FBE4526EA3EED0CC596A9FA95EDFA527DD74E9AD40FA097B52ED286FGEF82EC862885C0DF74A140BE000FGDA810E83EC845889308B60FD248B00F36D9DD5A6E3990D05503F52AFCD465DGD6009800B400ACG43C513319A208D209FA089A093A09B207C920C93009DA08EE09A40820002A8986BA89A633DD2E78E17CD5EA0E52885AD6F9BB5FCD789BED6881EEA05EC281E166F8F856FABB4B25223552AF9BF9E260CBC849E7E
	93A0665C7FCB56CBC5BB2F88F99731B6A569EF92376FAB044C51CE99B5753463721A989B1A8BEB8698D7FADF9F264CE0675CFF9A78DC6EEDC37948E7E50752A43BE40795DEBCA2CFE9C3998A3F546DD46B1CE7F8977831C6F09C9E959F5506F2B6AB0A5B5665D564D6D9D1EC0ED37E7C910C16475B65723BC022B53A8D474D6983BF26160FEBC39FE0F1BA9CB2B3AAEA9511BF0E79A059AE77C2279B1DDE0742718947B0CEBFAD1F93BEB071584B927AB03B977DA90FFDD34DB876AE372D97CC953AE4A9A58A7D53
	8AE319C6C69D004398BE8A1EF1D164D7FC9B492FECBAFF17B47D24ABD7AFD60A1FB0BAB7BB0A5B767CAB7CFE0577E90CC93F6FB7B2DFA8709E5E570FCE22DC775A1CAC1C504663F9FCBC5EC039B6B9ADDE2CF4486C00385DAD723CBB194C21C1A6C256C22D483B2E23D44D132D424363C857727CD9BBD0C687DB1CAE2F6B106C70D23D06CF0B6129D99E30D6394F58F61C45D7406FEE0036AB48E72C7A8473491F8BE75168340BFEC1DE5C95A87FE76BCA057C6C100E3E024BFF051ABFF2A765E7C1BABBA43F321E
	728DD702ED3F521F4F477729FA6D78D6EB725B60BD69CAAC33DCAA551431EB4AF0DE2476E2A637B3DE99C2B58CEE7E256A5A327615C1F9A621F7BC3F289DE5EA5744C5DB2EC21ABF4A217560C2C14FF0950FB9AA60398E703C5C9D39D42E9F52F1GE9D77138E48E150B46F23DF9D4EE3E48DFFC95EF37B41A73425B2BB4286DC5737C38E89E6384363729084A25C5F33A1922FC7EB05EDE46B0AC771CDAEE4FB01E5FBE0C17F741B38D00176BAD21F225D773F255D773F2CDD7737666DE0D65BEDCC965BA2E663E
	DC9165C708F23CFFBFD74BD58C6731D95DF0DE2E851E4943096E3155D4EE5AF04E67DCF8AEGB00C50525D38C66D5F889EB7905D6A91DA3A3354F29DA3F83EA21EB122DC7688ACF729DC25BB02479FF3C7F0FE974033C6ED6F50DAD5FE57006CG7A2E616D25DF2315F349BAAAE7984975E42E603BFC24365C4F54F2F5A305BCC6F23ABDA3357D3BFB3D2A87A3F9BC047C768979787BE72920F2F1A3F9FE4A48A03DE2652A5E20F2591A7EE13902EB357D7B29DA2E7ADADE2E69DA5E3F36EB3563B1E5831533DF4B
	63B3FDBDB832114AC50B76922E656596087638CD7DFD955A8136777105792C392F466383DFD5DFFF1019D63E4F51E74C976517DCCB7B99C9AB503EED0FB1991B812CB164933ED3C87B99F12713252C97EA17055B27690F617DC80C717B041EC58DAFF37B796527C8F3BE64AD0AA15BB932DD3B26EA70F81C969BDB4E891B32733856E7F05C87471743335B6676A887402977117DE9BA0E7D45751EC00B75EF414E82097B9343A8305DG4AA8449F58093E94ED916753320D780CBDBFC16A3C6166DCE398BE53C7F1
	7E660D7273D9FDF171D2CE6F9E15DE71F530FE3C0E687D0651C31B464B9D2927F2D1D0A6DDAD97D30F6550D67171FC392CBC4CF81277626377E7565EA2E8A7FFB4585651D47F115A416E05B1E7A7513E49177F540E7B3422AF434A23EEB417C37BE83FBC929DED4BB81F393944E7BC644F81603E76A4735FD6DD5FBEAA97E5207458BC3CAF3FEB58A3E8E5CE9DAF25AF0B49555BD36B39B3BEB7BFD64C19D8FFDF2C37F7657581707EE42A7D0902B26931649BBFF0E9FD63A240170E81DBBB067ABB46037840F803
	6214CF72283F1C46383C82B13F1A21FE345A46DF72308D85E45DCA6D54503CAD74958767D5D619B87F63D7E9795FFD6904AE7FA30AFAF2270D61FCAF9863673F6F7A2E3C186B097F0F8D447F166B41D6DECFFD9BDF3FCA520D1944BCBAD4C4BCA5C21DF9EA3D34A2DCFB74BA3D9E39067D5BE837D9CE4B56CA54569A33CF29E1B1F84DA14C5B072FD52A2DFA358A77744C0CD94F91872BD9599C36622EAA936FCBF1B17525F8AC4C313154979F435B9CB645E6365B4E4B15D51C4EF5EFE3DC6B72C2D8EF03F6DA5C
	CE176CD64E89D2F63363D44D112EAF0071DD2118AA933719A945E4B32346124D1832CEBBAE7966E13A63927342295C7431FCBC660F750F4BFC9813ECF5DC461590CF15F1A6E3E71C2AB3A541BCF17C6615084749FC9497B7EE594D5B69D9C96D44C39BF362488E14FE00CF36D6D065BFE3AF2E3D599E1A4C8CC12326394A2A66D003D193D2EC173097FFDECB32585414A455E51F561585DBB35CA066688DFED924947E6F5269G1C0758B262A18A4A245FC03278E3995AFEBB7456239CF1584F919F0666FFC94CA7
	48B4340DBDB00432F600DBE17166B825B340FAC3AE4E832ADF47B147B577229B48EEDFDCCD7327F29C0C53B86A5B332B4986F0FBF2DB1F561E440F637B0253547205EB303C8D96013C5D66B544739CA813FFA30D1B5137B1F00C8ED850B2B4EB1CC18D22F609F97A9B351DBD50C65F0DC46FD8397E5C485A31891E3283C376B627535EE6F3F9AA83DABF084EC11DFB496544FFAA347D3C5AFE51DA64476CBEA556D86F2D8D34617D47C9BF22B2532432D523426AC7C53CB86F095768C7E2D75E74C41AABE7572ABA
	8EE56644138D7F63C52D4C73EF627B19DBEE22BE7EF46DD6555E5470C70B6C6EF63AFBC36DD5834BA60345E5D56344797285BA5D830A5C5B8446D211CFB684B6D49BE241B4CD21EEF19E6A57918FCD503FFE350F33566979C5DEE759FA12FD9A14DF2856F12C573791716B17B25B4C7DBAEFE31E5A46261BCD460E1BC92FE3AA28BFF1004B3C195A7EFD8546818A4587B578F6985EBCFC0AF139112AF0FE55E002CB0F37FF748DEA5FF08B44E13710FE7F699574BBF21F4B6C30E245B667B18F9A9F12534B9B283F
	8A54C939057A553B01740B27EF5820EF8FFF300168AE003A658944EF5FC6423781AEA6015AFDE6A36ECBDA4D0A394B8CAE0DDE0E4A5DEE5953D329B1099666CC2CDDB5CD220CD0964ED74C67E82B5C229A30A376G3FB4861FE34DCC706F134F533C9370731DB266FF97A670731D25AAFEA343CB377A711C3FC9AB083FD2486B3B154EA496FE0E6B03ACC8AF86A83E4DE42C84588F50F39B45651947F12FF640B31F838E8542F6333DB5E0530B4EDEBEC5D91E164F0974551FEAFD8F17434A1DA807685B86F7C630
	C22DD7416AA5FC05FA5C7ED4FD2ABDEFB6149C4C4D8BE3FFB2C55BD937796D8F175D2DA7513E14FF8D62C20096009E0091DFA33999BF2B0EA4B7BA7F928263BCFDE30832187515E5416B9D2B9F9ABDCB7DD72547F554398BF5B179EBDCC71729788B8CBFDD03673A1B4370181E87F93B93C9F7734CA87FC4C8CF83188B30902060F6127F314FA26A6D3EBEDD3D657C6567C6BA5F2E3EBD10F7DE6F550875DA026AF1FE0EF7615A2E8772228192G528132G9683140FA73EA6FC36B792DF8D34B95D66D4DF9B03B7
	18293F5F9DA21F73A3565BBEBE301E5813EE43F3871DB5AC9AFC31FF79DD17F67F32995A6983C8GC88560711857C571D846F8BA3FCA488B6E13F4C7E09F74F91994D22FA6A8DDFE075E18BDE541B15B8EF9ADGBDG51G49GB3811E3F0346EC71E76FC754C57497ED4EC6406DF7BACF6B8C17686BA706210D179C31DEFE12DE3D56222149549CD2EFDDD03AC2175E2B45C32337A924DE70985669523B0755EBC99A1C9D33A6117D59A7635877C1BA8EA085A083E0A1C07E841331FA820D7D010EDD039CFB9E9A
	060E3C506D12A16AF6C83DE039B4CF502B67D8B9B4FA7DA1750269D9A7684D25D95DA84FFE480B87C8854884D884D0B091627609A44FDBBA9AA249D33D9EB21879B42EECE8FCA605548B566F1609FA75EADF47FABDA22FFF6240FA97BB11ECDB746A214D7B6BA356CB1A28E727FF5F9DE66F38595CC5FB475F6FC39B0CFB405354FE9EDDB3B4F9F606548B561B0ABB756AE58F11DEEEF9C4FB21CBEFEAC83D410D43340875DA6E541B977BFAD8DCGF951G89G69GD9G0B814A13E9DE7CF7C74438C0B3AFA247
	857DEB07A65711A17502674576E4BDFD4BD83AC6DF5FE805AD76E1EAAE515EDBF3B2695C1EF5C36B6B6110FA41FD0DCE562BD738FEE8BA30A6E23D84DDFABF9BA23D05916B2569523B3EE2E872CC8C2997BC27B2F569353FB1B4FA5DA1750269AD5025D730E1E872DC9D315E92DDFAD9A17586475F0BA1750279AB3DCB2F5E450DC3239793D2AF18DEF590BDFE5FB10E1DA1B6C1DEBBG471DA9C4DCBF24636E22F93A2E88F169104ED4F1D58C0771BD1ECFF3DC6A0A7688F2A6C88F6A0F2AE7E170FEDE9F9FB40D
	F07733425DBFC460B6798F1F073FF73464F37DFBC739A7746F9D651FA861F8BC34502CD6A93F78444077124ACE685DCBDA4B787B81BB87F3FB9D0A2DD7E6E439CD167B70EE99F80E23DE87F7ABC859933CFFA1112F3A4A067BF0736FA21FB47544D6A954A76D335A94271B9AE9134D6EA35D5D2AC3EAF549966A730413616517F2D2DFFE771E5417DF7A490165B77DE4B879F1FD7132FD08A540DB71A41331F292696A958C5F84E96BA4FE170163176D9A2CFE85580FB0FA0B7470D9AC1EE27FEBC8B442697332A0
	3D967BD86670E330522CBF86E5E67DB1D8E79EF88C5EB2079B832E274FA9F83FB1642E5CE108EAA4BAFF7DB07795D126C5F136ECD55B36F40567630BAEFDBEDCDD985BFA1CDE37C58E6E2B2F2BCC4D7387D19828E0F744DDC1D6DFB13BCF49A13C8EE775F9DE387A31DDF84E5EE5335A5C628699677115BCAD0F5BA50A21CE6DC3B96944D02D963753EE47DB9F925D917B2CA8788EA01EC9C503FE45CF22BD1665E7689B1854C9F4B7649AA76A7B731016A61B0CE513C95F4F3B50F7EC192C2F3FCD825F2E4967E3
	7A782BC5CCA7BC9E594396423803AE787C1E4A67BE4559D82B4AEE1EA57CD8BEE3519F4BE7AD6147F221A572D87E4192FEAC17D8A20F65D2EBB85D9677948D5AB15D2162C796E9E560479FAA56608F218EF03F1B414E266DA0677E49640BD715A0AE8652891349B69F11434B50A46B4B30C38EAFC30B9CD906F6B93C8CBDF2E499764B834B70FBA5DA19ECD57193D69AC9365DEEE7AFE892EF6F696E70B278C1373E8C323A434B60175D11E53030BB3C8C781AFBE099AC699ED886CF16E975452F977B5979F58A0C
	7FB4D5AF26B0DC8624672938458C77BC2497CF26F33087D7EB657A8F097C4F0F7B507F6427844E79F2115E1292469668605B81E78F42F35A572C899C3B2D827FCE39164F1DEA79E74B834A37EC9332BD52B3003E770451771E81743DE7907A5EB3003E778CC25FFB746D7FFDEB353CEFD547E35B256078BD8AE49A97E69C7CB2762D530E6FFBAA3EE93D96DF2B627FD691A8E33AB37DF80536DF7EF6C63F2145572978468D5A76372978D59B8347FC1B405F3FD15B0E3F7CE4A35EFD371B4FB93D8A2C0926209E4F
	D06F38A7837F53811E87D812427D9C7717777810CE375B5C8BD308851E0BD85D9105D4378352AE00D800D400B9D3E8CE8CBF08B267B7A9C9A76FE23E106F10EDA5BFEFAB544E5700D825CC7617A79936B859FD74D69942A4B3444904AFE3EB090301778DA4FE57A1EDD7707D923C6BD0B7156EC24C8E388B51BC156763BB7773E30BE8CFC519CAFEFE2D8B7DFC8A24E782E4CDA5BFDFF4E620B5F07654F0E756FFE1F1C37E5D7CFCAFCF45AFE3F44AB4F8FE6EB75A295E2D013C183B49BE0DE167BE0BA0EDC8A55C
	8BAEDAEB162A38739E76FD74D5A007D475BC3F186E257427E219BF30F4071A7FF609485F4A73773074F4B57F646BD4FFEEAAFEBB5D0A1F980A7119C9F95BF85D9F33F459BD6A1D185501ED3F4952F577D0FF17303D54F6C83BD41C659C638B52B1AAAE036162A11D2A62EE36A2EEA624672BB84FE112C9360A9B3E0B2D536FB599BB6EA55CC1361F6602F4340A9B4BF07110CE3E176EC83CE8246F2B53G3770DE1AC3351D81F7D2712ACBFD7DC7EE1BF52F79D4B31B11DC473696640B7A1B6EA39D6BAC40B6BBA0
	2DG445CC7F7697A5E227B26A99347CBF31E553FE7BC3F6D4B5C247B44BD6B7BB4F7BC5F6E4A63B4279692CDC31A4938BB8DF1D9055A3900E3F3ED9E5F676079ABD6E87351575EAFE973778464E3FFFF1E234DF78764230C0CA21F73B67BCD62ED496CD449FEFFF458EF5D2353B8CF4962D97154CDB9B363DBDE663C7D0A0DA55A0479E9B496E314B07703D5EB4267F0FC4859DC537D7EF95BFEFF38B9BC064DD57B7D41F37896066F3FBFF88EEFF4519C0E013CC57713FE3DF506F4336D6BE4D772DCB89F1254F4
	A1CB639DF91E3E41B914356EE040CFF36AE52311A7111DC8F6E96FE1F1DB708863B59D7A417D0FF4067CCF7EB7CC46F2005DG9DG7DG5C3FCD72D01984402581AC82B024736F8AB8ADE3896A6F86F634DEE337B964D6651CDD5E206A627B32769E4C168B4DE16F411427F31D28964F183F5E1443E918585AE2C3036314DD36EE606DBE7D19365D621B6D39B9E15A6D906D75247B67AFEFF7516B780D915A5A9A08108E4BBE7609D1E59549F157EAED7E913BB5A8FF55B974238A3F455886311D44EDF69206E074
	CD59B9F6EF8CB2F95EE39EFF5EB32CFE2F394F566B6DED351D172539696CDEA1333F89701E1CCEF23E470AF35865163BE537DB36325212A87F84334DE58F180C1BG229FE05FF79DEE927C37BC90BA57E88E91D8218C425FC43A6F81BE4FF2543E5E360B755566886A6B57190D480472739E203E1A19ED6FF5483DCE074D424B75B15C63G06EFD239C91782E5732202326173E4D1483D35C8406B3DB444FA6F95FC55FA5CAEEDADC47BD37ACD3F3CB8FE9743D7E8705CDEF597100F2B063C58EF52D97F13DE1CEB
	5910AEFE50E42C8458835089507FA02D27EFF6ED11A21C3F35B9DD4D30G31EB363EB8FF7795FED57E78BCFBF499791234EFCE12522FEE8A7BDDC66C03DC4FD245B3BD73269CFCF27EBAFD485FFC48DB82D0B18DE2A600E6G6BB4626F97AEF11F8D37EF9B9445EC69E1DF62281B41B65967C7E3B0ADF4C5F70F32EF1A30A36219B8ADF09E24C1BAE3DA685C48825CA28D1E7B133B0B687C4170907918E7CA4847D4BEC4767DC6E6E77B54746DAC0D5FF6083388573F437EE37B61F5F96F3F05C638BDDCEEDF5EBF
	5394212E96E2213FC98F796DCB7AC341762551026BAB66C6EA8977FD975E43F0483E0AA6395B6C35AB6ACD5FCA8D7210D7E1174945C5614A8DF627598AB149066AEE335DA3D761779FBC1E61B1817A46198FD12CF662CCF02C76CEC02C96FBA6B8D643BE7B73E71C890E55E667EA73334F8447EAD41F5B036FFAA8FED982FD5A7EB045D9737662DC8F0E3358F5687AFA0DB8DA833E9062FC2ED608CF7B4394877F4807F412A0BD8DE06E43E4F72E6AC37DDC78B04D414DE750F71B4CDF041E24G926F3A70AF9BE8
	DEFB4E41C32CC3CF1FF9B3DCDD183D87018D7E45C31B992A8BDF397402246A40E8EB0598EF75C6FB138D26BAFE9AD4D93BE3CBF5857CD3456DD222CF6B7F6B16DF297B6DB80274881FAB250F78638ACE5B596F275DF3F8B03439FC9FBC2C5E8F00B657337A90C7C97EBF5E7E42AFA5096C684F8B480E16CF3EC3EA7AE1D36E46B07D0D967D1C7B08BFDE6934ECFF0537E93A646F735FF78C264FC236177039593AD95808BA8CA744FB03665DDEF03962D33FFD9AF6D9E9083DD5EF783D381FFA757762FE6955BFD3
	FA41BB703967EF3D61FF0FE131D77BFB8CFC4E3C453E0BAF188EFE8320992093C019CEF3706A33B88779AD5BE09F384FFA8A778271570D9422EF73056755650B7C1BC867FC786DC017D3D11C3D58BE61AF79F03C94270BA1B9AE5A07B2F25BCE75A83CA87D3E110F1D89485D8C4D7D634A5D080B853E9227134F74B1DC8A24E72A386B5FC55CBCC8AFD1F14FB1DC7123A6E3752304BB3199F1FBA05D2E62D2994E8E69E895A7DFE4FB8B10CEFF1456B2735433284CC779DE5D82F8AEFE145F7B65BE6DFFCA482795
	4F88752FFF95FF6453B6C1193A99FE1F56BCA35827AD5901B6D7F3B2D69BEAAB96FD280763F37D699C7F3CFCA7E0CF2C9DE859F57A98BA5766F9A245FDD1C16D70FA7F9531DE70BDA6DEEF3D222D1790210F29104F6365C3FDA82B19B30263E893434F19919CC7BF6323B8BA8B5B78964D1D61FD98C7F882FCD4FFAE4AB6894ACCD74B9D3C003FDD934E4F51F7FD9C4B16FD023F6907357C4D0776323FB5B00F3C8F69F928DB79B3E10D86309F2093C0810881C884C88348G188F309820E0964449G5B811AG5A
	816C33287F35972B86683F589CE30642ED362129907334C68AEFABF6C811ED4587CCCFE51863ED67DC628C21D1C219BB3B3BBD32EA1376CB45AA6E0027456931E1104267C5556E40F55F0FCC12BAE72E5B2D3FA61E486CC694709FB30B560FF73D0B7D6DF13AED671D8E45EC47A5B8D12235714CF7E9FFAEDD2D77523B6157522FEDE1E7D3D0B6DBAD6F5D0C7651EC371DF220ADEE273223186D2978B60C5137296CE31B7557494F3032D6A867D24B76DFDC95507F8622A06A0C3BC8FDCF78B66D11CA4524771900
	B3BCC66768AF95D3595D0FD14CD74D3E65D5A09D831024165D3C126217E90F6945AF835924AB50F50BB551DE96CFE7C39B9873F25CC78CD710E1B2E29C4D4F5F272FC2F95B9C38BD5448FFE5CFED639FACD62D0472FBB248FE77B3DFE205F4D4862D1BDA71A796449C6C503CBF6B3B7C714ACB1715861FAFD764623B9AE3F013C33F7704BF346A7F5E127DE4850F42393CFEF81846A1C715EB56E19A079C15C6E6DE78397F3D3C48F3BFABCFBFCE78C5DEE81C70DC1EFE1C706BBCED1C40F13F4BC3DDBC85B62BDD
	AD77C795F7DC45ADE1EDA26E931BD569D171390665823FDFC6026AFDB683764B47F06D82D728419D27BBC68634DF40B2CCC52A7B1A81EDD4374DEE6769B52C0C4D8166CC15C705817BE8F319B57DFED345E13F45FEA03E8A7BB67CDD741BE1E276FF6C282E583AC55DDF7C493BF8B7CE3B7CEB45FFA97C7354B46EBB5C366F28CA6731CF7DF4DAB78F060E5E5A0557CF5ACC34D95DC6E72F4B69402FED1B777D336DE4C373010F8E9F826AAA6D97BF73577D746260F91CF2F130BC0ABB6EA15F608CEE8EBFB144FA
	F9A15F36C4829E37A4B25B9B17919C4F5C4570099941714C5692326BA910D77CB8593D6A00EFDD94400FF8FC60F8036F9F185F52FC234D268B471777957A71B4DD683E0F42662C53F7486CF231758A0F1DE5C5BD87G5AE9G0BG8CCF70FE703AE3FD6C9E03F6B7C2A2BF196223BBDE65CF90DF93F1C9A5E4FC37663D2E64725B730FAFAB8DEEFF9BD7065A647DAB35B6190F7BA8C55D8F8659D81F207D1F274A86587FA14B4C470F9F01B642CAD1965F4CA8745DE24293948B7C64AC52198B6905G06599403FC
	4B0734EBD5590CD506B6CFEE9FE23D9F33FA6533436B749648EB526473F9F404457F6D330367D7BB43DBE7874F2F1F1FA5B93BA0EF6EEC0AB5FE4BE4D517896B70CC429D70325F560534226246B25C88C8A7E5128E9F7379757604667D393317DF07254B9A13F05D4877061EA32E7722DF4C48A47D6974E23C6BF6BA95134DE3431B5A623C47CBF33E6CC9753C470BED877F269137B15F72120D29FC121FB53231FED2EBE3F6F87FDD7B41E2CF0C7122383DB25711DFF9C9C75A01EE6C13B4F7AA196EE4C0FABE62
	1E24717F2DF7E8BA7F572FDC0F6BF6826BC771D341BABF01614B1F8A56799AD567ABA1AF66A96A777833FEDD1D2CF95F7E9F505BC31715867FBF8A26DC5A89CC277CDADA2AC8A57C67FB448FCC612F78A8D2A1AC7D4E5AC0E3EC7883B7607F9469E5FE59496316D60646AC8D577D6807A9A71BCAF3255C83524BBD4C689E10167A502B76617D1CF1881EB352C54920265CD2115A528196B64EBF3B2DBE6F17F76465CA86E806075187A4834642501A1B371A87E1B4EB5320052036B105628C7883A6588F76542B59
	7ACD60DA22E12014247E0645A56D9E7B62E11630FB6C8B6670F93AF8CE70EFD8ECE7FBAF5B3FE3B236FC0776AE22F370B73C421D9334761A6DF6D88D1FEEF0D8714CE06FB9C5766F356EE46BA48F159119BCEF8F3B177F05264E7F81D0CB87885FAD815DBAA0GG90E3GGD0CB818294G94G88G88G520171B45FAD815DBAA0GG90E3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF4A0GGGG
**end of data**/
}
}
