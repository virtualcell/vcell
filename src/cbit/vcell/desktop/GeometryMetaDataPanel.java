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
import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;

import org.vcell.util.gui.GuiUtils;

import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.geometry.GeometryInfo;
/**
 * Insert the type's description here.
 * Creation date: (3/29/01 10:39:36 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class GeometryMetaDataPanel extends DocumentEditorSubPanel {
	private JTree ivjJTree1 = null;
	private GeometryInfo fieldGeometryInfo = null;
	private GeometryMetaDataCellRenderer ivjgeometryMetaDataCellRenderer = null;
	private GeometryMetaDataTreeModel ivjgeometryMetaDataTreeModel = null;
	private DocumentManager fieldDocumentManager = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	private class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == GeometryMetaDataPanel.this && (evt.getPropertyName().equals("geometryInfo"))) {
				getgeometryMetaDataTreeModel().setGeometryInfo(getGeometryInfo());
				GuiUtils.treeExpandAllRows(getJTree1());
			}
			if (evt.getSource() == GeometryMetaDataPanel.this && (evt.getPropertyName().equals(CommonTask.DOCUMENT_MANAGER.name))) {
				getgeometryMetaDataTreeModel().setDocumentManager(getDocumentManager());
				GuiUtils.treeExpandAllRows(getJTree1());
			}
		}
	}

/**
 * BioModelMetaDataPanel constructor comment.
 */
public GeometryMetaDataPanel() {
	super();
	initialize();
}

/**
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
public DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}

/**
 * Gets the geometryInfo property (cbit.vcell.geometry.GeometryInfo) value.
 * @return The geometryInfo property value.
 * @see #setGeometryInfo
 */
public GeometryInfo getGeometryInfo() {
	return fieldGeometryInfo;
}

/**
 * Return the geometryMetaDataCellRenderer property value.
 * @return cbit.vcell.desktop.GeometryMetaDataCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometryMetaDataCellRenderer getgeometryMetaDataCellRenderer() {
	if (ivjgeometryMetaDataCellRenderer == null) {
		try {
			ivjgeometryMetaDataCellRenderer = new GeometryMetaDataCellRenderer();
			ivjgeometryMetaDataCellRenderer.setName("geometryMetaDataCellRenderer");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjgeometryMetaDataCellRenderer;
}

/**
 * Return the geometryMetaDataTreeModel property value.
 * @return cbit.vcell.desktop.GeometryMetaDataTreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometryMetaDataTreeModel getgeometryMetaDataTreeModel() {
	if (ivjgeometryMetaDataTreeModel == null) {
		try {
			ivjgeometryMetaDataTreeModel = new GeometryMetaDataTreeModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjgeometryMetaDataTreeModel;
}

/**
 * Return the JTree1 property value.
 * @return cbit.gui.JTreeFancy
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JTree getJTree1() {
	if (ivjJTree1 == null) {
		try {
			ivjJTree1 = new JTree();
			ivjJTree1.setName("JTree1");
			ivjJTree1.setEnabled(true);
			ivjJTree1.setRootVisible(false);
			ivjJTree1.setRequestFocusEnabled(false);
//			ivjJTree1.setSelectionModel(new javax.swing.tree.DefaultTreeSelectionModel());
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
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
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
	getJTree1().setCellRenderer(getgeometryMetaDataCellRenderer());
	getJTree1().setModel(getgeometryMetaDataTreeModel());
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("BioModelMetaDataPanel");
		setLayout(new BorderLayout());
//		setSize(379, 460);

		add(new JScrollPane(getJTree1()), BorderLayout.CENTER);
		initConnections();
		ToolTipManager.sharedInstance().registerComponent(getJTree1());
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
		BioModelMetaDataPanel aBioModelMetaDataPanel;
		aBioModelMetaDataPanel = new BioModelMetaDataPanel();
		frame.setContentPane(aBioModelMetaDataPanel);
		frame.setSize(aBioModelMetaDataPanel.getSize());
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
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(DocumentManager documentManager) {
	DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange(CommonTask.DOCUMENT_MANAGER.name, oldValue, documentManager);
}

/**
 * Sets the geometryInfo property (cbit.vcell.geometry.GeometryInfo) value.
 * @param geometryInfo The new value for the property.
 * @see #getGeometryInfo
 */
public void setGeometryInfo(GeometryInfo geometryInfo) {
	GeometryInfo oldValue = fieldGeometryInfo;
	fieldGeometryInfo = geometryInfo;
	firePropertyChange("geometryInfo", oldValue, geometryInfo);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length != 1) {
		setGeometryInfo(null);
	} else if (selectedObjects[0] instanceof GeometryInfo) {
		setGeometryInfo((GeometryInfo) selectedObjects[0]);
	} else {
		setGeometryInfo(null);
	}	
	
}

}
