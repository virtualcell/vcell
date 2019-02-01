/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.mathmodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.JLabelLikeTextField;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.MathModelWindowManager;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.desktop.BioModelInfoCellRenderer;
import cbit.vcell.desktop.BioModelInfoTreeModel;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.desktop.MathModelMetaDataCellRenderer;
import cbit.vcell.desktop.MathModelMetaDataTreeModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mathmodel.MathModel;

@SuppressWarnings("serial")
public class MathModelPropertiesPanel extends DocumentEditorSubPanel {
	private MathModel mathModel = null;
	private EventHandler eventHandler = new EventHandler();
	private JLabelLikeTextField nameLabel, ownerLabel, lastModifiedLabel, permissionLabel;
	private JButton changePermissionButton;
	private MathModelWindowManager mathModelWindowManager;
	private JLabel geometryLabel, detStochIconLabel;
	private JLabelLikeTextField detStochLabel;
	private LocalMetaDataPanel applicationsPanel = null;

	private class EventHandler implements ActionListener, DatabaseListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == changePermissionButton) {
				changePermissions();
			}			
		}
		public void databaseDelete(DatabaseEvent event) {			
		}
		public void databaseInsert(DatabaseEvent event) {
		}
		public void databaseRefresh(DatabaseEvent event) {
		}
		public void databaseUpdate(DatabaseEvent event) {
			if (mathModel == null || mathModel.getVersion() == null) {
				return;
			}
			VersionInfo newVersionInfo = event.getNewVersionInfo();
			if (newVersionInfo instanceof MathModelInfo
					&& newVersionInfo.getVersion().getVersionKey().equals(mathModel.getVersion().getVersionKey())) {
				updateInterface();
			}
		}
	}

/**
 * EditSpeciesDialog constructor comment.
 */
public MathModelPropertiesPanel() {
	super();
	initialize();
}

public void changePermissions() {
	if (mathModel == null || mathModel.getVersion() == null) {
		return;
	}
	mathModelWindowManager.getRequestManager().accessPermissions(this, mathModel);	
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
 * Initialize the class.
 */
private void initialize() {
	try {		
		nameLabel = new JLabelLikeTextField();
		ownerLabel = new JLabelLikeTextField();
		lastModifiedLabel = new JLabelLikeTextField();
		permissionLabel = new JLabelLikeTextField();
		detStochLabel = new JLabelLikeTextField();
		changePermissionButton = new JButton("Change Permissions...");
		changePermissionButton.setEnabled(false);
		geometryLabel = new JLabel();
		detStochIconLabel = new JLabel();
		geometryLabel.setIcon(VCellIcons.geometryIcon);
		detStochIconLabel.setIcon(VCellIcons.mathTypeIcon);
		applicationsPanel = new LocalMetaDataPanel();
		applicationsPanel.initialize();
		
		setLayout(new GridBagLayout());
		setBackground(Color.white);
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.BOTH;		
		gbc.insets = new Insets(10, 4, 4, 4);
		JLabel label = new JLabel("Saved MathModel Info");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		add(label, gbc);

		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(10, 10, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		add(new JLabel("MathModel Name:"), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(nameLabel, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel("Owner:");
		add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(ownerLabel, gbc);

		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel("Last Modified:");
		add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(lastModifiedLabel, gbc);	
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel("Permissions:");
		add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;		
		add(permissionLabel, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;		
		add(changePermissionButton, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;	
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 3;
		add(applicationsPanel, gbc);
				
		changePermissionButton.addActionListener(eventHandler);
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		MathModelPropertiesPanel aEditSpeciesPanel = new MathModelPropertiesPanel();
		frame.add(aEditSpeciesPanel);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.pack();
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of cbit.gui.JInternalFrameEnhanced");
		exception.printStackTrace(System.out);
	}
}

/**
 * Sets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @param speciesContext The new value for the property.
 * @see #getSpeciesContext
 */
public void setMathModel(MathModel newValue) {
	if (newValue == mathModel) {
		return;
	}
	mathModel = newValue;
	updateInterface();
}

public void setMathModelWindowManager(MathModelWindowManager newValue) {
	this.mathModelWindowManager = newValue;
	mathModelWindowManager.getRequestManager().getDocumentManager().addDatabaseListener(eventHandler);
	updateInterface();
}
/**
 * Comment
 */
private void updateInterface() {
	if (mathModel == null || mathModelWindowManager == null) {
		return;
	}
	nameLabel.setText(mathModel.getName());
	
	Version version = mathModel.getVersion();
	try {
		if (version != null) {
			ownerLabel.setText(version.getOwner().getName());
			lastModifiedLabel.setText(version.getDate().toString());
			MathModelInfo mathModelInfo = mathModelWindowManager.getRequestManager().getDocumentManager().getMathModelInfo(version.getVersionKey());
			permissionLabel.setText(mathModelInfo.getVersion().getGroupAccess().getDescription());
			changePermissionButton.setEnabled(true);
		}
		applicationsPanel.updateInterface();
	} catch (DataAccessException e) {
		e.printStackTrace();
	}
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	
}

private class LocalMetaDataPanel extends JPanel {

	private JTree jTree = null;
	private MathModelMetaDataCellRenderer mathModelInfoCellRenderer = new MathModelMetaDataCellRenderer();
	private MathModelMetaDataTreeModel mathModelInfoTreeModel = new MathModelMetaDataTreeModel();
	private MathModelInfo fieldMathModelInfo = null;

	public LocalMetaDataPanel() {
		super();
		initialize();
	}
	private void initialize() {
		try {
			setName("LocalMetaDataPanel");
			setLayout(new BorderLayout());
			JScrollPane sp = new JScrollPane(getJTree());
			sp.setBorder(BorderFactory.createEmptyBorder());
			add(sp, BorderLayout.CENTER);
//			this.addPropertyChangeListener(ivjEventHandler);
			getJTree().setModel(mathModelInfoTreeModel);
			getJTree().setCellRenderer(mathModelInfoCellRenderer);
			javax.swing.ToolTipManager.sharedInstance().registerComponent(getJTree());
			getJTree().getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	
	private void updateInterface() {
		try {
			if(mathModel == null || mathModel.getVersion() == null) {
				return;
			}
//			Dimension preferredSize = new Dimension(100, 50);
//			setPreferredSize(preferredSize);
			
			KeyValue keyValue = mathModel.getVersion().getVersionKey();
			fieldMathModelInfo = mathModelWindowManager.getRequestManager().getDocumentManager().getMathModelInfo(keyValue);
			mathModelInfoTreeModel.setMathModelInfo(fieldMathModelInfo);
			GuiUtils.treeExpandAllRows(getJTree());
		} catch(DataAccessException ex) {
			handleException(ex);
		}
	}
	
	private JTree getJTree() {
		if (jTree == null) {
			try {
				jTree = new JTree();
				jTree.setName("JTree1");
				jTree.setToolTipText("Contents of saved MathModel");
				jTree.setEnabled(true);
				jTree.setRootVisible(false);
				jTree.setRequestFocusEnabled(false);
				jTree.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() == 1) {
						DefaultMutableTreeNode value = (DefaultMutableTreeNode)jTree.getLastSelectedPathComponent();
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
		return jTree;
	}
}


}
