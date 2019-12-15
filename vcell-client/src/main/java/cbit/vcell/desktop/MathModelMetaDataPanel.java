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
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;

import org.vcell.util.BeanUtils;
import org.vcell.util.document.MathModelInfo;
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
public class MathModelMetaDataPanel extends DocumentEditorSubPanel {
	
	private class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == MathModelMetaDataPanel.this && (evt.getPropertyName().equals("mathModelInfo"))) {
				getJTree1().setModel(getmathModelMetaDataTreeModel());
				getJTree1().setCellRenderer(getmathModelMetaDataCellRenderer());

				getmathModelMetaDataTreeModel().setMathModelInfo(MathModelMetaDataPanel.this.getMathModelInfo());
				GuiUtils.treeExpandAllRows(getJTree1());
				// here we don't need to expand one lvl only, like we do for BioModels (different structure)
			} else if(evt.getSource() == MathModelMetaDataPanel.this && (evt.getPropertyName().equals("publicationInfo"))) {
				getJTree1().setModel(getPublicationInfoNodeTreeModel());
				getJTree1().setCellRenderer(getPublicationInfoNodeCellRenderer());
				getPublicationInfoNodeTreeModel().setPublicationsInfoNode(MathModelMetaDataPanel.this.getPublicationInfoNode());
				GuiUtils.treeExpandAllRows(tree);
			}
		}
	}
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JTree tree = null;
	
	private MathModelMetaDataCellRenderer ivjmathModelMetaDataCellRenderer = null;
	private MathModelMetaDataTreeModel ivjmathModelMetaDataTreeModel = null;
	private MathModelInfo fieldMathModelInfo = null;

	private PublicationInfoNodeCellRenderer publicationInfoNodeCellRenderer = null;
	private PublicationInfoNodeTreeModel publicationInfoNodeTreeModel = null;
	private PublicationInfoNode fieldPublicationInfoNode = null;


public MathModelMetaDataPanel() {
	super();
	initialize();
}

public MathModelInfo getMathModelInfo() {
	return fieldMathModelInfo;
}
public PublicationInfoNode getPublicationInfoNode() {
	return fieldPublicationInfoNode;
}

private MathModelMetaDataCellRenderer getmathModelMetaDataCellRenderer() {
	if (ivjmathModelMetaDataCellRenderer == null) {
		try {
			ivjmathModelMetaDataCellRenderer = new MathModelMetaDataCellRenderer();
			ivjmathModelMetaDataCellRenderer.setName("mathModelMetaDataCellRenderer");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjmathModelMetaDataCellRenderer;
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

private MathModelMetaDataTreeModel getmathModelMetaDataTreeModel() {
	if (ivjmathModelMetaDataTreeModel == null) {
		try {
			ivjmathModelMetaDataTreeModel = new MathModelMetaDataTreeModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjmathModelMetaDataTreeModel;
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
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
	getJTree1().setCellRenderer(getmathModelMetaDataCellRenderer());
	getJTree1().setModel(getmathModelMetaDataTreeModel());
}

private void initialize() {
	try {
		setName("BioModelMetaDataPanel");
		setLayout(new BorderLayout());
//		setSize(379, 460);

		add(new JScrollPane(getJTree1()), BorderLayout.CENTER);
		initConnections();
		ToolTipManager.sharedInstance().registerComponent(getJTree1());
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

public void setMathModelInfo(MathModelInfo mathModelInfo) {
	MathModelInfo oldValue = fieldMathModelInfo;
	fieldMathModelInfo = mathModelInfo;
	firePropertyChange("mathModelInfo", oldValue, mathModelInfo);
}
public void setPublicationInfo(PublicationInfoNode publicationInfoNode) {
	PublicationInfoNode oldValue = fieldPublicationInfoNode;
	fieldPublicationInfoNode = publicationInfoNode;
	firePropertyChange("publicationInfo", oldValue, publicationInfoNode);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length != 1) {
		setMathModelInfo(null);
		setPublicationInfo(null);
	} else if (selectedObjects[0] instanceof MathModelInfo) {
		setMathModelInfo((MathModelInfo) selectedObjects[0]);
	} else if (selectedObjects[0] instanceof PublicationInfoNode) {
		setPublicationInfo((PublicationInfoNode) selectedObjects[0]);
	} else {
		setMathModelInfo(null);
		setPublicationInfo(null);
	}	
}

}
