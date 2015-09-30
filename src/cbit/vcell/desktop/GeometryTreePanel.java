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
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.vcell.util.BeanUtils;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;

import cbit.vcell.client.desktop.DatabaseWindowPanel;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.desktop.VCellBasicCellRenderer.VCDocumentInfoNode;
import cbit.vcell.geometry.GeometryInfo;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 11:34:01 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class GeometryTreePanel extends VCDocumentDbTreePanel {
	private JMenuItem ivjJMenuItemDelete = null;
	private JMenuItem ivjJMenuItemNew = null;
	private JMenuItem ivjJMenuItemOpen = null;
	private JMenuItem ivjJMenuItemCreateNewGeometry = null;
	private JSeparator ivjJSeparator2 = null;
	private JPopupMenu ivjGeometryPopupMenu = null;
	private GeometryMetaDataPanel ivjgeometryMetaDataPanel = null;
	private JMenuItem ivjJMenuItemPermission = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JMenuItem ivjJMenuItemGeomRefs = null;

private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GeometryTreePanel.this.getJMenuItemCreateNewGeometry()) 
				refireActionPerformed(e);
			if (e.getSource() == GeometryTreePanel.this.getJMenuItemOpen()) 
				DocumentWindow.showGeometryCreationWarning(GeometryTreePanel.this);
//				refireActionPerformed(e);
			if (e.getSource() == GeometryTreePanel.this.getJMenuItemDelete()) 
				refireActionPerformed(e);
			if (e.getSource() == GeometryTreePanel.this.getJMenuItemNew()) 
				refireActionPerformed(e);
			if (e.getSource() == GeometryTreePanel.this.getJMenuItemPermission()) 
				refireActionPerformed(e);
			if (e.getSource() == GeometryTreePanel.this.getJMenuItemGeomRefs()) 
				refireActionPerformed(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == GeometryTreePanel.this && (evt.getPropertyName().equals("selectedVersionInfo"))) {
				getGeometryMetaDataPanel().setGeometryInfo(getSelectedGeometryInfo());
			}
			if (evt.getSource() == GeometryTreePanel.this && (evt.getPropertyName().equals(CommonTask.DOCUMENT_MANAGER.name))) {
				getGeometryMetaDataPanel().setDocumentManager(getDocumentManager());
			}
		}		
	};
	
	/**
	 * BioModelTreePanel constructor comment.
	 */
	public GeometryTreePanel() {
		this(true);
	}

	public GeometryTreePanel(boolean bMetadata) {
		super(bMetadata);
		initialize();
	}
	
	/**
 * Comment
 */
protected void actionsOnClick(MouseEvent mouseEvent) {
	if (mouseEvent.isPopupTrigger()) {
		if(!getPopupMenuDisabled()){
			TreePath treePath = ((JTree)mouseEvent.getSource()).getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
			((JTree)mouseEvent.getSource()).setSelectionPath(treePath);
			if(getSelectedVersionInfo() instanceof GeometryInfo){
				Version version = getSelectedVersionInfo().getVersion();
				boolean isOwner = version.getOwner().compareEqual(getDocumentManager().getUser());
				getJMenuItemPermission().setEnabled(isOwner);
				getJMenuItemDelete().setEnabled(isOwner);
				getJMenuItemGeomRefs().setEnabled(isOwner);
				getGeometryPopupMenu().show(getJTree1(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
			}else if (treePath != null){
				BioModelNode bioModelNode = (BioModelNode)treePath.getLastPathComponent();
				Object object = bioModelNode.getUserObject();
				if(object instanceof User){
					User selectedUser = (User)object;
					boolean isOwner = selectedUser.compareEqual(getDocumentManager().getUser());
					if(isOwner){
						JPopupMenu jPopupMenu = new JPopupMenu();
						jPopupMenu.add(getJMenuItemCreateNewGeometry());
						jPopupMenu.show(getJTree1(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
					}
				}		
	
			}
		}
	}
	else{
		ifNeedsDoubleClickEvent(mouseEvent,GeometryInfo.class);
	}
}

/**
 * Comment
 */
protected void documentManager_DatabaseDelete(DatabaseEvent event) {
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
protected void documentManager_DatabaseUpdate(DatabaseEvent event) {
	if (event.getNewVersionInfo() instanceof GeometryInfo && getSelectedVersionInfo() instanceof GeometryInfo) {
		GeometryInfo selectedGeoInfo = (GeometryInfo)getSelectedVersionInfo();
		GeometryInfo eventGeoInfo = (GeometryInfo)event.getNewVersionInfo();
		if (eventGeoInfo.getVersion().getVersionKey().equals(selectedGeoInfo.getVersion().getVersionKey())){
			setSelectedVersionInfo(event.getNewVersionInfo());
		}		
	}
}


/**
 * Return the TreeModel property value.
 * @return javax.swing.tree.TreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
protected GeometryDbTreeModel createTreeModel() {
	return new GeometryDbTreeModel(getJTree1());
}

/**
 * Return the geometryMetaDataPanel property value.
 * @return cbit.vcell.desktop.GeometryMetaDataPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometryMetaDataPanel getGeometryMetaDataPanel() {
	if (ivjgeometryMetaDataPanel == null) {
		try {
			ivjgeometryMetaDataPanel = new GeometryMetaDataPanel();
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
			ivjGeometryPopupMenu.add(getJMenuItemOpen());
			ivjGeometryPopupMenu.add(getJMenuItemDelete());
			ivjGeometryPopupMenu.add(getJMenuItemPermission());
			ivjGeometryPopupMenu.add(getJMenuItemGeomRefs());
//			ivjGeometryPopupMenu.add(getJSeparator2());
//			ivjGeometryPopupMenu.add(getJMenuItemNew());
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

private javax.swing.JMenuItem getJMenuItemCreateNewGeometry() {
	if (ivjJMenuItemCreateNewGeometry == null) {
		try {
			ivjJMenuItemCreateNewGeometry = new javax.swing.JMenuItem();
			ivjJMenuItemCreateNewGeometry.setName("JMenuItemCreateNewGeometry");
//			ivjJMenuItemCreateNewGeometry.setMnemonic('o');
			ivjJMenuItemCreateNewGeometry.setText(DatabaseWindowPanel.NEW_GEOMETRY);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCreateNewGeometry;
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
protected javax.swing.JPanel getBottomPanel() {
	if (bottomPanel == null) {
		try {
			bottomPanel = new javax.swing.JPanel();
			bottomPanel.setName("JPanel2");
			bottomPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			bottomPanel.add(new JLabel("Selected Geometry Summary"), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
			constraintsJScrollPane2.gridx = 0; constraintsJScrollPane2.gridy = 1;
			constraintsJScrollPane2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane2.weightx = 1.0;
			constraintsJScrollPane2.weighty = 1.0;
			constraintsJScrollPane2.insets = new java.awt.Insets(0, 4, 4, 4);
			bottomPanel.add(getGeometryMetaDataPanel(), constraintsJScrollPane2);
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
 * Return the JTree1 property value.
 * @return javax.swing.JTree
 */
protected VCDocumentDbCellRenderer createTreeCellRenderer() {
	return new GeometryCellRenderer();
}

/**
 * Comment
 */
private GeometryInfo getSelectedGeometryInfo() {
	if (getSelectedVersionInfo() instanceof GeometryInfo){
		GeometryInfo geoInfo = (GeometryInfo)getSelectedVersionInfo();
		return geoInfo;
	}
	return null;
}

/**
 * Initialize the class.
 */
@Override
protected void initialize() {
	try {
		super.initialize();
		this.addPropertyChangeListener(ivjEventHandler);
		getJMenuItemCreateNewGeometry().addActionListener(ivjEventHandler);
		getJMenuItemOpen().addActionListener(ivjEventHandler);
		getJMenuItemDelete().addActionListener(ivjEventHandler);
		getJMenuItemNew().addActionListener(ivjEventHandler);
		getJMenuItemPermission().addActionListener(ivjEventHandler);
		getJMenuItemGeomRefs().addActionListener(ivjEventHandler);
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
protected void treeSelection() {
	TreePath treePath = getJTree1().getSelectionPath();
	if (treePath == null){
		setSelectedVersionInfo(null);
		return;
	}
	BioModelNode bioModelNode = (BioModelNode)treePath.getLastPathComponent();
	Object object = bioModelNode.getUserObject();

	try {
		BeanUtils.setCursorThroughout(this,java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
		
		if (object instanceof VersionInfo){
			setSelectedVersionInfo((VersionInfo)object);
		}else if (object instanceof VCDocumentInfoNode && bioModelNode.getChildCount()>0 && ((BioModelNode)bioModelNode.getChildAt(0)).getUserObject() instanceof GeometryInfo){
			GeometryInfo geometryInfo = (GeometryInfo)((BioModelNode)bioModelNode.getChildAt(0)).getUserObject();
			setSelectedVersionInfo(geometryInfo);
		}else{
			setSelectedVersionInfo(null);
		}
		
	} catch (Exception exc) {
		handleException(exc);
	} finally {
		BeanUtils.setCursorThroughout(this,java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
	}

}

}
