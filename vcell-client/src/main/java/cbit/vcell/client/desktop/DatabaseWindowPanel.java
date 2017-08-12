/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop;
import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import org.vcell.util.BeanUtils;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.gui.JTabbedPaneEnhanced;

import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.desktop.biomodel.BioModelEditor;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.BioModelDbTreePanel;
import cbit.vcell.desktop.GeometryTreePanel;
import cbit.vcell.desktop.MathModelDbTreePanel;
import cbit.vcell.geometry.GeometryInfo;
/**
 * Insert the type's description here.
 * Creation date: (5/24/2004 1:13:39 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class DatabaseWindowPanel extends DocumentEditorSubPanel {
	public static final String PROPERTY_NAME_SELECTED_DOCUMENT_INFO = "selectedDocumentInfo";
	private BioModelDbTreePanel ivjBioModelDbTreePanel1 = null;
	private GeometryTreePanel ivjGeometryTreePanel1 = null;
	private JTabbedPane ivjJTabbedPane1 = null;
	private MathModelDbTreePanel ivjMathModelDbTreePanel1 = null;
	private DocumentManager fieldDocumentManager = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private DatabaseWindowManager fieldDatabaseWindowManager = null;
	private VCDocumentInfo fieldSelectedDocumentInfo = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DatabaseWindowPanel.this.getBioModelDbTreePanel1()) 
				connEtoC1(e);
			if (e.getSource() == DatabaseWindowPanel.this.getMathModelDbTreePanel1()) 
				connEtoC2(e);
			if (e.getSource() == DatabaseWindowPanel.this.getGeometryTreePanel1()) 
				connEtoC3(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == DatabaseWindowPanel.this && (evt.getPropertyName().equals(CommonTask.DOCUMENT_MANAGER.name))) {
				connPtoP1SetTarget();
				connPtoP2SetTarget();
				connPtoP3SetTarget();
			}
			if (evt.getSource() == DatabaseWindowPanel.this.getBioModelDbTreePanel1() && (evt.getPropertyName().equals(CommonTask.DOCUMENT_MANAGER.name))) 
				connPtoP1SetSource();
			if (evt.getSource() == DatabaseWindowPanel.this.getMathModelDbTreePanel1() && (evt.getPropertyName().equals(CommonTask.DOCUMENT_MANAGER.name))) 
				connPtoP2SetSource();
			if (evt.getSource() == DatabaseWindowPanel.this.getGeometryTreePanel1() && (evt.getPropertyName().equals(CommonTask.DOCUMENT_MANAGER.name))) 
				connPtoP3SetSource();
			if (evt.getSource() == DatabaseWindowPanel.this.getGeometryTreePanel1() && (evt.getPropertyName().equals("selectedVersionInfo")))
				currentDocumentInfo();
			if (evt.getSource() == DatabaseWindowPanel.this.getMathModelDbTreePanel1() && (evt.getPropertyName().equals("selectedVersionInfo"))) 
				currentDocumentInfo();
			if (evt.getSource() == DatabaseWindowPanel.this.getBioModelDbTreePanel1() && (evt.getPropertyName().equals("selectedVersionInfo"))) 
				currentDocumentInfo();
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == DatabaseWindowPanel.this.getJTabbedPane1()) {
				currentDocumentInfo();
			}			
		}
	}

private boolean bShowMetadata = true;
private boolean bShowSearchPanel = true;
/**
 * DatabaseWindowPanel constructor comment.
 */
public DatabaseWindowPanel() {
	this(true, true);
}

public DatabaseWindowPanel(boolean bMetadata, boolean bSearchPanel) {
	super();
	bShowMetadata = bMetadata;
	bShowSearchPanel = bSearchPanel;
	initialize();
}

@Override
public void setSelectionManager(SelectionManager selectionManager) {
	super.setSelectionManager(selectionManager);
	if (selectionManager != null) {
		getBioModelDbTreePanel1().setSelectionManager(selectionManager);
		getMathModelDbTreePanel1().setSelectionManager(selectionManager);
		getGeometryTreePanel1().setSelectionManager(selectionManager);
	}
}

/**
 * connEtoC1:  (BioModelDbTreePanel1.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseWindowPanel.bioModelDbTreePanel1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dbTreePanelActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC2:  (MathModelDbTreePanel1.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseWindowPanel.mathModelDbTreePanel1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dbTreePanelActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC3:  (GeometryTreePanel1.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseWindowPanel.geometryTreePanel1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dbTreePanelActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetSource:  (DatabaseWindowPanel.documentManager <--> BioModelDbTreePanel1.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			this.setDocumentManager(getBioModelDbTreePanel1().getDocumentManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (DatabaseWindowPanel.documentManager <--> BioModelDbTreePanel1.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			getBioModelDbTreePanel1().setDocumentManager(this.getDocumentManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetSource:  (DatabaseWindowPanel.documentManager <--> MathModelDbTreePanel1.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			this.setDocumentManager(getMathModelDbTreePanel1().getDocumentManager());
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
 * connPtoP2SetTarget:  (DatabaseWindowPanel.documentManager <--> MathModelDbTreePanel1.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			getMathModelDbTreePanel1().setDocumentManager(this.getDocumentManager());
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
 * connPtoP3SetSource:  (DatabaseWindowPanel.documentManager <--> GeometryTreePanel1.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			this.setDocumentManager(getGeometryTreePanel1().getDocumentManager());
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
 * connPtoP3SetTarget:  (DatabaseWindowPanel.documentManager <--> GeometryTreePanel1.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			getGeometryTreePanel1().setDocumentManager(this.getDocumentManager());
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
 * Comment
 */
private void currentDocumentInfo() {
	VCDocumentInfo selectedDocInfo = null;
	switch (getJTabbedPane1().getSelectedIndex()) {
		case 0: { // VCDocumentType.BIOMODEL_DOC
			selectedDocInfo = (BioModelInfo)getBioModelDbTreePanel1().getSelectedVersionInfo();
			break;
		}
		case 1: { // VCDocumentType.MATHMODEL_DOC
			selectedDocInfo = (MathModelInfo)getMathModelDbTreePanel1().getSelectedVersionInfo();
			break;
		}
		case 2: { // VCDocumentType.GEOMETRY_DOC
			selectedDocInfo = (GeometryInfo)getGeometryTreePanel1().getSelectedVersionInfo();
			break;
		}
	}
	setSelectedDocumentInfo(selectedDocInfo);
}


/**
 * Comment
 */
public static final String NEW_GEOMETRY = "New Geometry...";
private void dbTreePanelActionPerformed(java.awt.event.ActionEvent e) {
	String actionCommand = e.getActionCommand();
	if (actionCommand.equals("Open") || actionCommand.equals(DatabaseWindowManager.BM_MM_GM_DOUBLE_CLICK_ACTION)) {
		// check to see if open in new or this window
		BioModelEditor bioModelEditor = null;
		Container c = this;
		while (c != null) {
			if (c instanceof BioModelEditor) {
				bioModelEditor = (BioModelEditor) c;
				break;
			}
			c = c.getParent();
		}
		if (bioModelEditor != null && getSelectedDocumentInfo() instanceof BioModelInfo) {
			if (bioModelEditor.getBioModelWindowManager().hasBlankDocument()) {
				getDatabaseWindowManager().openSelected(bioModelEditor.getBioModelWindowManager(), false);
			} else {
				getDatabaseWindowManager().openSelected(bioModelEditor.getBioModelWindowManager(), true);
			}
		} else {
			getDatabaseWindowManager().openSelected();
		}
	} else if (actionCommand.equals("Delete")) {
		getDatabaseWindowManager().deleteSelected();
	} else if (actionCommand.equals("Permission")) {
		getDatabaseWindowManager().accessPermissions();
	} else if (actionCommand.equals("Export")) {
		getDatabaseWindowManager().exportDocument();
	} else if (actionCommand.equals("Latest Edition")) {
		getDatabaseWindowManager().compareLatestEdition();
	} else if (actionCommand.equals("Previous Edition")) {
		getDatabaseWindowManager().comparePreviousEdition();
	} else if (actionCommand.equals("Another Edition...")) {
		getDatabaseWindowManager().compareAnotherEdition();
	} else if (actionCommand.equals("Another Model...")) {
		getDatabaseWindowManager().compareAnotherModel();
	} else if (actionCommand.equals("Models Using Geometry")) {
		getDatabaseWindowManager().findModelsUsingSelectedGeometry();
	} else if (actionCommand.equals("Archive")) {
		getDatabaseWindowManager().archive();
	} else if (actionCommand.equals("Publish")) {
		getDatabaseWindowManager().publish();
	} else if (actionCommand.equals(NEW_GEOMETRY)) {
		getDatabaseWindowManager().createNewGeometry();
	}
}


/**
 * Return the BioModelDbTreePanel1 property value.
 * @return cbit.vcell.desktop.BioModelDbTreePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private BioModelDbTreePanel getBioModelDbTreePanel1() {
	if (ivjBioModelDbTreePanel1 == null) {
		try {
			ivjBioModelDbTreePanel1 = new BioModelDbTreePanel(bShowMetadata);
			ivjBioModelDbTreePanel1.setName("BioModelDbTreePanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBioModelDbTreePanel1;
}


/**
 * Gets the databaseWindowManager property (cbit.vcell.client.DatabaseWindowManager) value.
 * @return The databaseWindowManager property value.
 * @see #setDatabaseWindowManager
 */
public DatabaseWindowManager getDatabaseWindowManager() {
	return fieldDatabaseWindowManager;
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
 * Return the GeometryTreePanel1 property value.
 * @return cbit.vcell.desktop.GeometryTreePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GeometryTreePanel getGeometryTreePanel1() {
	if (ivjGeometryTreePanel1 == null) {
		try {
			ivjGeometryTreePanel1 = new GeometryTreePanel(bShowMetadata);
			ivjGeometryTreePanel1.setName("GeometryTreePanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometryTreePanel1;
}


/**
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new JTabbedPaneEnhanced();
			ivjJTabbedPane1.insertTab("BioModels", null, getBioModelDbTreePanel1(), null, 0);
			ivjJTabbedPane1.insertTab("MathModels", null, getMathModelDbTreePanel1(), null, 1);
			ivjJTabbedPane1.insertTab("Geometries", null, getGeometryTreePanel1(), null, 2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane1;
}

/**
 * Return the MathModelDbTreePanel1 property value.
 * @return cbit.vcell.desktop.MathModelDbTreePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathModelDbTreePanel getMathModelDbTreePanel1() {
	if (ivjMathModelDbTreePanel1 == null) {
		try {
			ivjMathModelDbTreePanel1 = new MathModelDbTreePanel(bShowMetadata);
			ivjMathModelDbTreePanel1.setName("MathModelDbTreePanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathModelDbTreePanel1;
}


/**
 * Comment
 */
public VCDocumentInfo getSelectedDocumentInfo() {
	return fieldSelectedDocumentInfo;
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
	this.addPropertyChangeListener(ivjEventHandler);
	getBioModelDbTreePanel1().addPropertyChangeListener(ivjEventHandler);
	getMathModelDbTreePanel1().addPropertyChangeListener(ivjEventHandler);
	getGeometryTreePanel1().addPropertyChangeListener(ivjEventHandler);
	getBioModelDbTreePanel1().addActionListener(ivjEventHandler);
	getMathModelDbTreePanel1().addActionListener(ivjEventHandler);
	getGeometryTreePanel1().addActionListener(ivjEventHandler);
	getJTabbedPane1().addChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setLayout(new java.awt.BorderLayout());
		setSize(662, 657);
//		JToolBar toolBar = new JToolBar();
//		toolBar.setFloatable(false);
//		toolBar.add(getSearchToolBarButton());
//		add(toolBar, BorderLayout.PAGE_START);
		add(getJTabbedPane1(), BorderLayout.CENTER);
		
		if (!bShowSearchPanel) {
			getBioModelDbTreePanel1().expandSearchPanel(false);
			getMathModelDbTreePanel1().expandSearchPanel(false);
			getGeometryTreePanel1().expandSearchPanel(false);
		}
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		JFrame frame = new javax.swing.JFrame();
		DatabaseWindowPanel aDatabaseWindowPanel;
		aDatabaseWindowPanel = new DatabaseWindowPanel();
		frame.setContentPane(aDatabaseWindowPanel);
		frame.setSize(aDatabaseWindowPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		BeanUtils.centerOnScreen(frame);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Sets the databaseWindowManager property (cbit.vcell.client.DatabaseWindowManager) value.
 * @param databaseWindowManager The new value for the property.
 * @see #getDatabaseWindowManager
 */
public void setDatabaseWindowManager(DatabaseWindowManager databaseWindowManager) {
	DatabaseWindowManager oldValue = fieldDatabaseWindowManager;
	fieldDatabaseWindowManager = databaseWindowManager;
	firePropertyChange("databaseWindowManager", oldValue, databaseWindowManager);
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
 */
public void setLatestOnly(boolean latestOnly) {
	getBioModelDbTreePanel1().setLatestVersionOnly(latestOnly);
	getMathModelDbTreePanel1().setLatestVersionOnly(latestOnly);
	getGeometryTreePanel1().setLatestVersionOnly(latestOnly);
}


/**
 * Sets the selectedDocumentInfo property (cbit.vcell.document.VCDocumentInfo) value.
 * @param selectedDocumentInfo The new value for the property.
 * @see #getSelectedDocumentInfo
 */
private void setSelectedDocumentInfo(VCDocumentInfo selectedDocumentInfo) {
	VCDocumentInfo oldValue = fieldSelectedDocumentInfo;
	fieldSelectedDocumentInfo = selectedDocumentInfo;
	firePropertyChange(PROPERTY_NAME_SELECTED_DOCUMENT_INFO, oldValue, selectedDocumentInfo);
	if (fieldSelectedDocumentInfo == null) {
		setSelectedObjects(new Object[] {});
	} else {
		setSelectedObjects(new Object[] {fieldSelectedDocumentInfo});
	}
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	// do nothing
	
}

public void updateConnectionStatus(ConnectionStatus connStatus) {
	getBioModelDbTreePanel1().updateConnectionStatus(connStatus);
	getMathModelDbTreePanel1().updateConnectionStatus(connStatus);
	getGeometryTreePanel1().updateConnectionStatus(connStatus);
}
}
