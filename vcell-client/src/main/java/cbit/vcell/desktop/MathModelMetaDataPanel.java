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

import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
/**
 * Insert the type's description here.
 * Creation date: (3/29/01 10:39:36 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class MathModelMetaDataPanel extends DocumentEditorSubPanel {
	private JTree ivjJTree1 = null;
	private MathModelMetaDataCellRenderer ivjmathModelMetaDataCellRenderer = null;
	private MathModelMetaDataTreeModel ivjmathModelMetaDataTreeModel = null;
	private MathModelInfo fieldMathModelInfo = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	private class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == MathModelMetaDataPanel.this && (evt.getPropertyName().equals("mathModelInfo"))) {
				getmathModelMetaDataTreeModel().setMathModelInfo(getMathModelInfo());
				GuiUtils.treeExpandAllRows(getJTree1());
			}
		}
	}
/**
 * BioModelMetaDataPanel constructor comment.
 */
public MathModelMetaDataPanel() {
	super();
	initialize();
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTree1;
}
/**
 * Gets the mathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @return The mathModelInfo property value.
 * @see #setMathModelInfo
 */
public MathModelInfo getMathModelInfo() {
	return fieldMathModelInfo;
}

/**
 * Return the mathModelMetaDataCellRenderer property value.
 * @return cbit.vcell.desktop.MathModelMetaDataCellRenderer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathModelMetaDataCellRenderer getmathModelMetaDataCellRenderer() {
	if (ivjmathModelMetaDataCellRenderer == null) {
		try {
			ivjmathModelMetaDataCellRenderer = new MathModelMetaDataCellRenderer();
			ivjmathModelMetaDataCellRenderer.setName("mathModelMetaDataCellRenderer");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmathModelMetaDataCellRenderer;
}
/**
 * Return the mathModelMetaDataTreeModel property value.
 * @return cbit.vcell.desktop.MathModelMetaDataTreeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathModelMetaDataTreeModel getmathModelMetaDataTreeModel() {
	if (ivjmathModelMetaDataTreeModel == null) {
		try {
			ivjmathModelMetaDataTreeModel = new MathModelMetaDataTreeModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjmathModelMetaDataTreeModel;
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
	getJTree1().setCellRenderer(getmathModelMetaDataCellRenderer());
	getJTree1().setModel(getmathModelMetaDataTreeModel());
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

/**
 * Sets the mathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @param mathModelInfo The new value for the property.
 * @see #getMathModelInfo
 */
public void setMathModelInfo(MathModelInfo mathModelInfo) {
	MathModelInfo oldValue = fieldMathModelInfo;
	fieldMathModelInfo = mathModelInfo;
	firePropertyChange("mathModelInfo", oldValue, mathModelInfo);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length != 1) {
		setMathModelInfo(null);
	} else if (selectedObjects[0] instanceof MathModelInfo) {
		setMathModelInfo((MathModelInfo) selectedObjects[0]);
	} else {
		setMathModelInfo(null);
	}	
}

}
