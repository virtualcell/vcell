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
import java.util.Enumeration;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.vcell.util.BeanUtils;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.desktop.BioModelNode.PublicationInfoNode;
import cbit.vcell.resource.PropertyLoader;
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
				getJTree1().setModel(getbioModelInfoTreeModel());
				getJTree1().setCellRenderer(getbioModelInfoCellRenderer());
				getbioModelInfoTreeModel().showDatabaseFileInfo(true);
				getbioModelInfoTreeModel().setBioModelInfo(BioModelMetaDataPanel.this.getBioModelInfo());
				BioModelNode applicationsNode = getbioModelInfoTreeModel().getApplicationsNode();
				GuiUtils.treeExpandAllRows(tree);
				if(applicationsNode != null) {		// here we expand the application tree just 1 level under the applicationNode
					TreePath appPath = new TreePath(applicationsNode.getPath());	// collapse the applications path
					Enumeration e = applicationsNode.children();
					while (e.hasMoreElements()) {
						TreeNode childNode =  (TreeNode)e.nextElement();
						TreePath childPath = appPath.pathByAddingChild(childNode);
						getJTree1().collapsePath(childPath);
					}
				}
			} else if(evt.getSource() == BioModelMetaDataPanel.this && (evt.getPropertyName().equals("publicationInfo"))) {
				getJTree1().setModel(getPublicationInfoNodeTreeModel());
				getJTree1().setCellRenderer(getPublicationInfoNodeCellRenderer());

				getPublicationInfoNodeTreeModel().setPublicationsInfoNode(BioModelMetaDataPanel.this.getPublicationInfoNode());
				GuiUtils.treeExpandAllRows(tree);
			}
		};
	}
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JTree tree = null;
	
	private BioModelInfoCellRenderer ivjbioModelInfoCellRenderer = null;
	private BioModelInfoTreeModel ivjbioModelInfoTreeModel = null;
	private BioModelInfo fieldBioModelInfo = null;

	private PublicationInfoNodeCellRenderer publicationInfoNodeCellRenderer = null;
	private PublicationInfoNodeTreeModel publicationInfoNodeTreeModel = null;
	private PublicationInfoNode fieldPublicationInfoNode = null;

public BioModelMetaDataPanel() {
	super();
	initialize();
}

public BioModelInfo getBioModelInfo() {
	return fieldBioModelInfo;
}
public PublicationInfoNode getPublicationInfoNode() {
	return fieldPublicationInfoNode;
}

private BioModelInfoCellRenderer getbioModelInfoCellRenderer() {
	if (ivjbioModelInfoCellRenderer == null) {
		try {
			ivjbioModelInfoCellRenderer = new BioModelInfoCellRenderer();
			ivjbioModelInfoCellRenderer.setName("bioModelInfoCellRenderer");
			ivjbioModelInfoCellRenderer.setText("bioModelInfoCellRenderer");
			ivjbioModelInfoCellRenderer.setBounds(446, 285, 179, 16);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjbioModelInfoCellRenderer;
}
private PublicationInfoNodeCellRenderer getPublicationInfoNodeCellRenderer() {
	if (publicationInfoNodeCellRenderer == null) {
		try {
			publicationInfoNodeCellRenderer = new PublicationInfoNodeCellRenderer();
			publicationInfoNodeCellRenderer.setName("publicationInfoNodeCellRenderer");
			publicationInfoNodeCellRenderer.setText("publicationInfoNodeCellRenderer");
			publicationInfoNodeCellRenderer.setBounds(446, 285, 179, 16);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return publicationInfoNodeCellRenderer;
}

private BioModelInfoTreeModel getbioModelInfoTreeModel() {
	if (ivjbioModelInfoTreeModel == null) {
		try {
			ivjbioModelInfoTreeModel = new BioModelInfoTreeModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjbioModelInfoTreeModel;
}
private PublicationInfoNodeTreeModel getPublicationInfoNodeTreeModel() {
	if (publicationInfoNodeTreeModel == null) {
		try {
			publicationInfoNodeTreeModel = new PublicationInfoNodeTreeModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return publicationInfoNodeTreeModel;
}

private JTree getJTree1() {
	if (tree == null) {
		try {
			tree = new JTree();
			tree.setName("JTree1");
			tree.setToolTipText("Contents of saved BioModel");
			tree.setEnabled(true);
			tree.setRootVisible(false);
			tree.setRequestFocusEnabled(false);
			tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1) {
					DefaultMutableTreeNode value = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
					if(value instanceof BioModelNode) {
						BioModelNode node = (BioModelNode) value;
						try {
							if(node.getUserObject() instanceof PublicationInfo && "PublicationInfoDoi".equals(node.getRenderHint("type"))) {
								PublicationInfo info = (PublicationInfo)node.getUserObject();
								Desktop.getDesktop().browse(new URI(BeanUtils.getDynamicClientProperties().getProperty(PropertyLoader.DOI_URL) + info.getDoi()));
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
	return tree;
}

private void handleException(java.lang.Throwable exception) {
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}

private void initConnections() throws java.lang.Exception {
	this.addPropertyChangeListener(ivjEventHandler);
	getJTree1().setModel(getbioModelInfoTreeModel());
	getJTree1().setCellRenderer(getbioModelInfoCellRenderer());
}

private void initialize() {
	try {
		setName("BioModelMetaDataPanel");
		setLayout(new BorderLayout());
//		setSize(379, 460);

		tree = getJTree1();
		add(new JScrollPane(tree), BorderLayout.CENTER);
		initConnections();
		javax.swing.ToolTipManager.sharedInstance().registerComponent(getJTree1());
		getJTree1().getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

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
public void setPublicationInfo(PublicationInfoNode publicationInfoNode) {
	PublicationInfoNode oldValue = fieldPublicationInfoNode;
	fieldPublicationInfoNode = publicationInfoNode;
	firePropertyChange("publicationInfo", oldValue, publicationInfoNode);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length != 1) {
		setBioModelInfo(null);
		setPublicationInfo(null);
	} else if (selectedObjects[0] instanceof BioModelInfo) {
		setBioModelInfo((BioModelInfo) selectedObjects[0]);
	} else if (selectedObjects[0] instanceof PublicationInfoNode) {
		setPublicationInfo((PublicationInfoNode) selectedObjects[0]);
	} else {
		setBioModelInfo(null);
		setPublicationInfo(null);
	}
}

}
