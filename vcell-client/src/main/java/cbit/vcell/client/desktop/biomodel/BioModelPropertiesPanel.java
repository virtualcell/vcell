/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

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

import cbit.vcell.client.server.DynamicClientProperties;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.desktop.BioModelInfoCellRenderer;
import cbit.vcell.desktop.BioModelInfoTreeModel;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.desktop.VCellBasicCellRenderer;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.gui.MiriamTreeModel;
import cbit.vcell.xml.gui.MiriamTreeModel.LinkNode;

@SuppressWarnings("serial")
public class BioModelPropertiesPanel extends JPanel {
	
	private BioModel bioModel = null;
	private EventHandler eventHandler = new EventHandler();
//	private JLabelLikeTextField nameLabel, ownerLabel, idLabel, lastModifiedLabel, permissionLabel;
	private JLabel ownerLabel, idLabel, lastModifiedLabel, permissionLabel;
//	private JTextPane nameLabel;
	private JLabel nameLabel;
	private JButton changePermissionButton;
	private BioModelWindowManager bioModelWindowManager;
	private LocalMetaDataPanel applicationsPanel = null;
	@Deprecated
	private JPanel webLinksPanel = null;	// not in use anymore

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
			if (bioModel == null || bioModel.getVersion() == null) {
				return;
			}
			VersionInfo newVersionInfo = event.getNewVersionInfo();
			if (newVersionInfo instanceof BioModelInfo
					&& newVersionInfo.getVersion().getVersionKey().equals(bioModel.getVersion().getVersionKey())) {
				updateInterface();
			}
		}
	}

/**
 * EditSpeciesDialog constructor comment.
 */
public BioModelPropertiesPanel() {
	super();
	initialize();
}

public void changePermissions() {
	if (bioModel == null || bioModel.getVersion() == null) {
		return;
	}
	bioModelWindowManager.getRequestManager().accessPermissions(this, bioModel);	
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
//		nameLabel = new JLabelLikeTextField();
//		idLabel = new JLabelLikeTextField();
//		ownerLabel = new JLabelLikeTextField();
//		lastModifiedLabel = new JLabelLikeTextField();
//		permissionLabel = new JLabelLikeTextField();
//		nameLabel = new JTextPane();
		nameLabel = new JLabel();
		idLabel = new JLabel();
		ownerLabel = new JLabel();
		lastModifiedLabel = new JLabel();
		permissionLabel = new JLabel();
		changePermissionButton = new JButton("Change Permissions...");
		changePermissionButton.setEnabled(false);
		applicationsPanel = new LocalMetaDataPanel();
		applicationsPanel.initialize();
//		applicationsPanel.setBackground(Color.white);
		webLinksPanel = new JPanel();
		webLinksPanel.setBackground(Color.white);
		
		VCellBasicCellRenderer tempRenderer = new VCellBasicCellRenderer();		// we need it for its icons
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(Color.white);
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;		
		gbc.insets = new Insets(10, 4, 4, 4);
		JLabel label = new JLabel("Saved BioModel Info");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		mainPanel.add(label, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(10, 10, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel();
		label.setToolTipText("BioModel Name:");
		label.setIcon(tempRenderer.fieldFolderClosedIcon);
		mainPanel.add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;
		nameLabel.setToolTipText("BioModel Name:");
//		nameLabel.setEditable(false);
//		nameLabel.setContentType("text/html");
//		nameLabel.setBackground(null);
//		nameLabel.setBorder(null);
		mainPanel.add(nameLabel, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel();
		label.setToolTipText("VCell Identifier:");
		label.setIcon(tempRenderer.fieldDatabaseModelKeyIcon);
		mainPanel.add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		idLabel.setToolTipText("VCell Identifier:");
//		idLabel.setIcon(tempRenderer.fieldDatabaseModelKeyIcon);
		mainPanel.add(idLabel, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel();
		label.setIcon(tempRenderer.fieldUserIcon);
		label.setToolTipText("Owner:");
		mainPanel.add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		ownerLabel.setToolTipText("Owner:");
//		ownerLabel.setIcon(tempRenderer.fieldUserIcon);
		mainPanel.add(ownerLabel, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel();
		label.setIcon(tempRenderer.fieldCalendarIcon);
		label.setToolTipText("Last Modified:");
		mainPanel.add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		lastModifiedLabel.setToolTipText("Last Modified:");
//		lastModifiedLabel.setIcon(tempRenderer.fieldCalendarIcon);
		mainPanel.add(lastModifiedLabel, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel();
		label.setIcon(tempRenderer.fieldPermissionsIcon);
		label.setToolTipText("Permissions:");
		mainPanel.add(label, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;
		permissionLabel.setToolTipText("Permissions:");
//		permissionLabel.setIcon(tempRenderer.fieldPermissionsIcon);
		mainPanel.add(permissionLabel, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		mainPanel.add(changePermissionButton, gbc);

		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;	
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		mainPanel.add(applicationsPanel, gbc);

		setLayout(new BorderLayout());
		add(new JScrollPane(mainPanel), BorderLayout.CENTER);
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
		BioModelPropertiesPanel aEditSpeciesPanel = new BioModelPropertiesPanel();
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
public void setBioModel(BioModel newValue) {
	if (newValue == bioModel) {
		return;
	}
	bioModel = newValue;
	updateInterface();
}

public void setBioModelWindowManager(BioModelWindowManager bioModelWindowManager) {
	this.bioModelWindowManager = bioModelWindowManager;
	bioModelWindowManager.getRequestManager().getDocumentManager().addDatabaseListener(eventHandler);
	updateInterface();
}
/**
 * Comment
 */
private void updateInterface() {
	if (bioModel == null || bioModelWindowManager == null) {
		return;
	}
	nameLabel.setText("<html><b>" + bioModel.getName() + "</b></html>");
	
	Version version = bioModel.getVersion();
	if (version != null) {
		ownerLabel.setText(version.getOwner().getName());
		String text = "biomodel-" + version.getVersionKey().toString();
		idLabel.setText(text);
		lastModifiedLabel.setText(version.getDate().toString());
		try {
			// permissions
			BioModelInfo bioModelInfo = bioModelWindowManager.getRequestManager().getDocumentManager().getBioModelInfo(version.getVersionKey());
			String permissions = bioModelInfo.getVersion().getGroupAccess().getDescription();
			if(bioModelInfo.getPublicationInfos() != null && bioModelInfo.getPublicationInfos().length > 0) {
				if(bioModelInfo.getVersion().getFlag().compareEqual(org.vcell.util.document.VersionFlag.Published)) {
					permissions = "Published";		// must be Public
				} else {
					permissions = "Curated";
				}
			} else if(bioModelInfo.getVersion().getFlag().compareEqual(org.vcell.util.document.VersionFlag.Archived)) {
				permissions += "Archived";
			}
			permissionLabel.setText(permissions);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		changePermissionButton.setEnabled(true);
	}
	// not in use anymore and unmaintained
	webLinksPanel.removeAll();
	webLinksPanel.setLayout(new GridLayout(0,1));
	Set<MiriamRefGroup> resources = new HashSet<MiriamRefGroup>();
	Set<MiriamRefGroup> isDescribedByAnnotation = bioModel.getVCMetaData().getMiriamManager().getMiriamRefGroups(bioModel, MIRIAMQualifier.MODEL_isDescribedBy);
	Set<MiriamRefGroup> isAnnotation = bioModel.getVCMetaData().getMiriamManager().getMiriamRefGroups(bioModel, MIRIAMQualifier.MODEL_is);
	resources.addAll(isDescribedByAnnotation);
	resources.addAll(isAnnotation);
	for (MiriamRefGroup refGroup : resources){
		for (MiriamResource miriamResources : refGroup.getMiriamRefs()){
			LinkNode linkNode = new MiriamTreeModel.LinkNode(MIRIAMQualifier.MODEL_isDescribedBy, miriamResources);
			final String link = linkNode.getLink();
			String labelText = miriamResources.getDataType() == null ? "" : miriamResources.getDataType().getDataTypeName();
			String toolTip = null;
			if (link != null) {
				toolTip = "double-click to open link " + link;
				labelText = "<html><b>"+ labelText + "</b>&nbsp;" + "<font color=blue><a href=" + link + ">" + link + "</a></font></html>";
			}
			JLabel label = new JLabel(labelText);
			label.addMouseListener(new MouseListener() {				
				public void mouseReleased(MouseEvent e) {					
				}				
				public void mousePressed(MouseEvent e) {					
				}				
				public void mouseExited(MouseEvent e) {
				}				
				public void mouseEntered(MouseEvent e) {
				}				
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						DialogUtils.browserLauncher(BioModelPropertiesPanel.this, link, "failed to open " + link);
					}
				}
			});
			label.setToolTipText(toolTip);
			webLinksPanel.add(label);
		}
	}
	applicationsPanel.updateInterface();
}

//private class LocalMetaDataPanel extends DocumentEditorSubPanel {
private class LocalMetaDataPanel extends JPanel {

	private JTree jTree = null;
	private BioModelInfoCellRenderer bioModelInfoCellRenderer = new BioModelInfoCellRenderer();
	private BioModelInfoTreeModel bioModelInfoTreeModel = new BioModelInfoTreeModel();
	private BioModelInfo fieldBioModelInfo = null;

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
			getJTree().setModel(bioModelInfoTreeModel);
			getJTree().setCellRenderer(bioModelInfoCellRenderer);
			javax.swing.ToolTipManager.sharedInstance().registerComponent(getJTree());
			getJTree().getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
//			getJTree().setSelectionModel(null);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	
	private void updateInterface() {
		try {
			if(bioModel == null || bioModel.getVersion() == null) {
				return;
			}
			Dimension preferredSize = new Dimension(100, 50);
			setPreferredSize(preferredSize);
			
			KeyValue keyValue = bioModel.getVersion().getVersionKey();
			fieldBioModelInfo = bioModelWindowManager.getRequestManager().getDocumentManager().getBioModelInfo(keyValue);
			bioModelInfoTreeModel.showDatabaseFileInfo(false);
			bioModelInfoTreeModel.setBioModelInfo(fieldBioModelInfo);
			BioModelNode applicationsNode = bioModelInfoTreeModel.getApplicationsNode();
			GuiUtils.treeExpandAllRows(getJTree());
			if(applicationsNode != null) {
				TreePath appPath = new TreePath(applicationsNode.getPath());	// collapse the applications path
				Enumeration e = applicationsNode.children();
				while (e.hasMoreElements()) {
					TreeNode childNode =  (TreeNode)e.nextElement();
					TreePath childPath = appPath.pathByAddingChild(childNode);
					getJTree().collapsePath(childPath);
				}
			}
		} catch(DataAccessException ex) {
			handleException(ex);
		}
	}
	
	private JTree getJTree() {
		if (jTree == null) {
			try {
				jTree = new JTree();
				jTree.setName("JTree1");
				jTree.setToolTipText("Contents of saved BioModel");
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
									Desktop.getDesktop().browse(new URI(DynamicClientProperties.getDynamicClientProperties().getProperty(PropertyLoader.DOI_URL) + info.getDoi()));
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
