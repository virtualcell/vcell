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
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
/**
 * Insert the type's description here.
 * Creation date: (3/29/01 10:39:36 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class BioModelMetaDataPanel extends DocumentEditorSubPanel {

	private class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelMetaDataPanel.this && (evt.getPropertyName().equals("bioModelInfo"))) {
				getbioModelInfoTreeModel().setBioModelInfo(BioModelMetaDataPanel.this.getBioModelInfo());
				BioModelNode publicationsInfoNode = getbioModelInfoTreeModel().getPublicationsNode();
				BioModelNode applicationsNode = getbioModelInfoTreeModel().getApplicationsNode();
				if(publicationsInfoNode == null) {
					GuiUtils.treeExpandAllRows(getJTree1());
				} else {
					GuiUtils.treeExpandAllRows(getJTree1());
			 		TreePath appPath = new TreePath(applicationsNode.getPath());	// collapse the applications path
			 		int begin = getJTree1().getRowForPath(appPath);
			 		int count = appPath.getPathCount();
			 		for(int i = begin; i< begin+count; i++) {
			 			getJTree1().collapseRow(i);
			 		}
				}
			}
		};
	}
	private JTree ivjJTree1 = null;
	private BioModelInfoCellRenderer ivjbioModelInfoCellRenderer = null;
	private BioModelInfoTreeModel ivjbioModelInfoTreeModel = null;
	private BioModelInfo fieldBioModelInfo = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
/**
 * BioModelMetaDataPanel constructor comment.
 */
public BioModelMetaDataPanel() {
	super();
	initialize();
}

/**
 * Gets the bioModelInfo property (cbit.vcell.biomodel.BioModelInfo) value.
 * @return The bioModelInfo property value.
 * @see #setBioModelInfo
 */
public BioModelInfo getBioModelInfo() {
	return fieldBioModelInfo;
}
/**
 * Return the bioModelInfoCellRenderer property value.
 * @return cbit.vcell.desktop.BioModelInfoCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private BioModelInfoCellRenderer getbioModelInfoCellRenderer() {
	if (ivjbioModelInfoCellRenderer == null) {
		try {
			ivjbioModelInfoCellRenderer = new BioModelInfoCellRenderer();
			ivjbioModelInfoCellRenderer.setName("bioModelInfoCellRenderer");
			ivjbioModelInfoCellRenderer.setText("bioModelInfoCellRenderer");
			ivjbioModelInfoCellRenderer.setBounds(446, 285, 179, 16);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjbioModelInfoCellRenderer;
}
/**
 * Return the bioModelMetaDataTreeModel property value.
 * @return cbit.vcell.desktop.BioModelMetaDataTreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private BioModelInfoTreeModel getbioModelInfoTreeModel() {
	if (ivjbioModelInfoTreeModel == null) {
		try {
			ivjbioModelInfoTreeModel = new BioModelInfoTreeModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjbioModelInfoTreeModel;
}

/**
 * Return the JTree1 property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JTree getJTree1() {
	if (ivjJTree1 == null) {
		try {
			ivjJTree1 = new JTree();
			ivjJTree1.setName("JTree1");
			ivjJTree1.setToolTipText("Contents of saved BioModel");
			ivjJTree1.setEnabled(true);
			ivjJTree1.setRootVisible(false);
			ivjJTree1.setRequestFocusEnabled(false);
			ivjJTree1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1) {
					DefaultMutableTreeNode value = (DefaultMutableTreeNode)ivjJTree1.getLastSelectedPathComponent();
					if(value instanceof BioModelNode) {
						BioModelNode node = (BioModelNode) value;
						try {
							if(node.getUserObject() instanceof PublicationInfo && "PublicationInfoDoi".equals(node.getRenderHint("type"))) {
								PublicationInfo info = (PublicationInfo)node.getUserObject();
								Desktop.getDesktop().browse(new URI("https://doi.org/" + info.getDoi()));
							} else if (node.getUserObject() instanceof PublicationInfo && "PublicationInfoUrl".equals(node.getRenderHint("type"))) {
								PublicationInfo info = (PublicationInfo)node.getUserObject();
								Desktop.getDesktop().browse(new URI(info.getUrl()));
							}
						} catch (URISyntaxException | IOException ex) {
							handleException(ex);
						}
					}
				}
			}
		});
		} catch (java.lang.Throwable ex) {
			handleException(ex);
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
private void initConnections() throws java.lang.Exception {
	this.addPropertyChangeListener(ivjEventHandler);
	getJTree1().setModel(getbioModelInfoTreeModel());
	getJTree1().setCellRenderer(getbioModelInfoCellRenderer());
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
		javax.swing.ToolTipManager.sharedInstance().registerComponent(getJTree1());
		getJTree1().getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
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
///**
// * Perform the refresh method.
// */
//public void refresh() {
//	getbioModelInfoTreeModel().setBioModelInfo(getBioModelInfo());
//	GuiUtils.treeExpandAllRows(getJTree1());
//}
/**
 * Sets the bioModelInfo property (cbit.vcell.biomodel.BioModelInfo) value.
 * @param bioModelInfo The new value for the property.
 * @see #getBioModelInfo
 */
public void setBioModelInfo(BioModelInfo bioModelInfo) {
	BioModelInfo oldValue = fieldBioModelInfo;
	fieldBioModelInfo = bioModelInfo;
	firePropertyChange("bioModelInfo", oldValue, bioModelInfo);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length != 1) {
		setBioModelInfo(null);
	} else if (selectedObjects[0] instanceof BioModelInfo) {
		setBioModelInfo((BioModelInfo) selectedObjects[0]);
	} else {
		setBioModelInfo(null);
	}
}

}
