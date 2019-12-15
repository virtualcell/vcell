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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;
import org.vcell.util.document.VersionInfo;

import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.clientdb.DatabaseEvent;
import cbit.vcell.desktop.BioModelNode.PublicationInfoNode;
import cbit.vcell.desktop.VCellBasicCellRenderer.VCDocumentInfoNode;
import cbit.vcell.resource.PropertyLoader;
/**
 * Insert the type's description here.
 * Creation date: (11/28/00 11:34:01 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class BioModelDbTreePanel extends VCDocumentDbTreePanel {
	private JMenuItem ivjJMenuItemDelete = null;
	private JMenuItem ivjJMenuItemOpen = null;
	protected transient ActionListener aActionListener = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JPopupMenu ivjBioModelPopupMenu = null;
	private BioModelMetaDataPanel ivjBioModelMetaDataPanel = null;
	
	private JMenuItem ivjAnotherEditionMenuItem = null;
	private JMenuItem ivjAnotherModelMenuItem = null;
	private JMenu compareWithMenu = null;
	private JMenuItem ivjLatestEditionMenuItem = null;
	private JMenuItem ivjJMenuItemPermission = null;
	private JMenuItem ivjJMenuItemExport = null;
	private JSeparator ivjJSeparator3 = null;
	private JMenuItem ivjJPreviousEditionMenuItem = null;
	private JMenuItem ivjJMenuItemArchive = null;
	private JMenuItem ivjJMenuItemPublish = null;
	
	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == BioModelDbTreePanel.this.getJMenuItemDelete())
				refireActionPerformed(e);
			if (e.getSource() == BioModelDbTreePanel.this.getJMenuItemOpen()) 
				refireActionPerformed(e);
			if (e.getSource() == BioModelDbTreePanel.this.getJMenuItemPermission()) 
				refireActionPerformed(e);
			if (e.getSource() == BioModelDbTreePanel.this.getLatestEditionMenuItem()) 
				refireActionPerformed(e);
			if (e.getSource() == BioModelDbTreePanel.this.getAnotherEditionMenuItem()) 
				refireActionPerformed(e);
			if (e.getSource() == BioModelDbTreePanel.this.getAnotherModelMenuItem()) 
				refireActionPerformed(e);
			if (e.getSource() == BioModelDbTreePanel.this.getJMenuItemExport()) 
				refireActionPerformed(e);
			if (e.getSource() == BioModelDbTreePanel.this.getJPreviousEditionMenuItem()) 
				refireActionPerformed(e);
			if (e.getSource() == BioModelDbTreePanel.this.getJMenuItemArchive()) 
				refireActionPerformed(e);
			if (e.getSource() == BioModelDbTreePanel.this.getJMenuItemPublish()) 
				onlinePublish(e);
//				refireActionPerformed(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelDbTreePanel.this && (evt.getPropertyName().equals("selectedVersionInfo"))) {
				getBioModelMetaDataPanel().setBioModelInfo((BioModelInfo)getSelectedVersionInfo());
			} else if(evt.getSource() == BioModelDbTreePanel.this && (evt.getPropertyName().equals("selectedPublicationInfo"))) {
				getBioModelMetaDataPanel().setPublicationInfo((PublicationInfoNode)evt.getNewValue());
			}
		}
	}

/**
 * BioModelTreePanel constructor comment.
 */
public BioModelDbTreePanel() {
	this(true);
}

public BioModelDbTreePanel(boolean bMetadata) {
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
			if(getSelectedVersionInfo() instanceof BioModelInfo){
				Version version = getSelectedVersionInfo().getVersion();
				boolean isOwner = version.getOwner().compareEqual(getDocumentManager().getUser());
				
				boolean shouldDisable = false;
				if(treePath != null && shouldDisablePopupMenu(treePath)) {
					shouldDisable = true;
				} else if(treePath != null && treePath.getParentPath() != null && shouldDisablePopupMenu(treePath.getParentPath())) {
					shouldDisable = true;
				}
				if(shouldDisable) {
					getJMenuItemOpen().setEnabled(false);
					getJMenuItemDelete().setEnabled(false);
					getJMenuItemPermission().setEnabled(false);
					getJMenuItemArchive().setEnabled(false);
					getJMenuItemPublish().setEnabled(false);
				} else {
					configureArhivePublishMenuState(version,isOwner);
					getJMenuItemPermission().setEnabled(isOwner && !version.getFlag().compareEqual(VersionFlag.Published));
					getJMenuItemDelete().setEnabled(isOwner &&
							!version.getFlag().compareEqual(VersionFlag.Archived) &&
							!version.getFlag().compareEqual(VersionFlag.Published));
				}
				compareWithMenuItemEnable(getSelectedVersionInfo());
				getBioModelPopupMenu().show(getJTree1(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
			}
		}
	} else {
		TreePath treePath = ((JTree)mouseEvent.getSource()).getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
		boolean shouldDisable = false;
		if(treePath != null && shouldDisablePopupMenu(treePath)) {
			shouldDisable = true;
		} else if(treePath != null && treePath.getParentPath() != null && shouldDisablePopupMenu(treePath.getParentPath())) {
			shouldDisable = true;
		}
		if(!shouldDisable) {
			ifNeedsDoubleClickEvent(mouseEvent,BioModelInfo.class);
		}
	}
}


public void addActionListener(ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.add(aActionListener, newListener);
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 8:15:47 AM)
 */
private void configureArhivePublishMenuState(Version version,boolean isOwner) {
	
	getJMenuItemArchive().setEnabled(
		isOwner
		&&
		!version.getFlag().isArchived()
		&&
		!version.getFlag().isPublished());
	
	getJMenuItemPublish().setEnabled(isOwner);
//		isOwner
//		&&
//		version.getFlag().isArchived()
//		&&
//		(version.getGroupAccess() instanceof GroupAccessAll)
//		&&
//		!version.getFlag().isPublished()
//		&&
//		version.getOwner().isPublisher());
}


/**
 * Comment
 */
protected void documentManager_DatabaseDelete(DatabaseEvent event) {
	if (event.getOldVersionInfo() instanceof BioModelInfo && getSelectedVersionInfo() instanceof BioModelInfo) {
		BioModelInfo selectedBMInfo = (BioModelInfo)getSelectedVersionInfo();
		BioModelInfo eventBMInfo = (BioModelInfo)event.getOldVersionInfo();
		if (eventBMInfo.getVersion().getVersionKey().equals(selectedBMInfo.getVersion().getVersionKey())){
			setSelectedVersionInfo(null);
			getJTree1().getSelectionModel().clearSelection();
		}		
	}
}


/**
 * Comment
 */
protected void documentManager_DatabaseUpdate(DatabaseEvent event) {
	if (event.getNewVersionInfo() instanceof BioModelInfo && getSelectedVersionInfo() instanceof BioModelInfo) {
		BioModelInfo selectedBMInfo = (BioModelInfo)getSelectedVersionInfo();
		BioModelInfo eventBMInfo = (BioModelInfo)event.getNewVersionInfo();
		if (eventBMInfo.getVersion().getVersionKey().equals(selectedBMInfo.getVersion().getVersionKey())){
			setSelectedVersionInfo(event.getNewVersionInfo());
		}		
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
 * Return the AnotherEditionMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getAnotherEditionMenuItem() {
	if (ivjAnotherEditionMenuItem == null) {
		try {
			ivjAnotherEditionMenuItem = new javax.swing.JMenuItem();
			ivjAnotherEditionMenuItem.setName("AnotherEditionMenuItem");
			ivjAnotherEditionMenuItem.setText("Another Edition...");
			ivjAnotherEditionMenuItem.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnotherEditionMenuItem;
}

/**
 * Return the AnotherModelMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getAnotherModelMenuItem() {
	if (ivjAnotherModelMenuItem == null) {
		try {
			ivjAnotherModelMenuItem = new javax.swing.JMenuItem();
			ivjAnotherModelMenuItem.setName("AnotherModelMenuItem");
			ivjAnotherModelMenuItem.setText("Another Model...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnotherModelMenuItem;
}

/**
 * Return the BioModelDbTreeModel property value.
 * @return cbit.vcell.desktop.BioModelDbTreeModel
 */
protected BioModelDbTreeModel createTreeModel() {
	return new BioModelDbTreeModel(getJTree1());
}


/**
 * Return the BioModelMetaDataPanel property value.
 * @return cbit.vcell.desktop.BioModelMetaDataPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private BioModelMetaDataPanel getBioModelMetaDataPanel() {
	if (ivjBioModelMetaDataPanel == null) {
		try {
			ivjBioModelMetaDataPanel = new BioModelMetaDataPanel();
			ivjBioModelMetaDataPanel.setName("BioModelMetaDataPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBioModelMetaDataPanel;
}


/**
 * Return the JPopupMenu1 property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getBioModelPopupMenu() {
	if (ivjBioModelPopupMenu == null) {
		try {
			ivjBioModelPopupMenu = new javax.swing.JPopupMenu();
			ivjBioModelPopupMenu.setName("BioModelPopupMenu");
			ivjBioModelPopupMenu.add(getJMenuItemOpen());
			ivjBioModelPopupMenu.add(getJMenuItemDelete());
			ivjBioModelPopupMenu.add(getJMenuItemPermission());
			ivjBioModelPopupMenu.add(getJMenuItemArchive());
			ivjBioModelPopupMenu.add(getJMenuItemPublish());
			ivjBioModelPopupMenu.add(getCompareWithMenu());
//			ivjBioModelPopupMenu.add(getJSeparator3());
//			ivjBioModelPopupMenu.add(getJMenuItemExport());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBioModelPopupMenu;
}

/**
 * Insert the method's description here.
 * Creation date: (10/3/2002 10:34:00 AM)
 */
private BioModelInfo[] getBioModelVersionDates(BioModelInfo thisBioModelInfo) throws DataAccessException {
	//
	// Get list of BioModelInfos in workspace
	//
    if (thisBioModelInfo==null){
	    return new BioModelInfo[0];
    }
    
	BioModelInfo bioModelInfos[] = getDocumentManager().getBioModelInfos();

	//
	// From the list of biomodels in the workspace, get list of biomodels with the same branch ID.
	// This is the list of different versions of the same biomodel.
	//
 	Vector<BioModelInfo> bioModelBranchList = new Vector<BioModelInfo>();
 	for (int i = 0; i < bioModelInfos.length; i++) {
	 	BioModelInfo bioModelInfo = bioModelInfos[i];
	 	if (bioModelInfo.getVersion().getBranchID().equals(thisBioModelInfo.getVersion().getBranchID())) {
		 	bioModelBranchList.add(bioModelInfo);
	 	}
 	}

 	if (bioModelBranchList.size() == 0) {
		JOptionPane.showMessageDialog(this,"No Versions in biomodel","Error comparing BioModels",JOptionPane.ERROR_MESSAGE);
	 	throw new NullPointerException("No Versions in biomodel!");
 	}

 	BioModelInfo bioModelInfosInBranch[] = new BioModelInfo[bioModelBranchList.size()];
 	bioModelBranchList.copyInto(bioModelInfosInBranch);

 	//
 	// From the versions list, remove the currently selected version and return the remaining list of
 	// versions for the biomodel
 	//

 	BioModelInfo revisedBMInfosInBranch[] = new BioModelInfo[bioModelInfosInBranch.length-1];
 	int j=0;
 	
 	for (int i = 0; i < bioModelInfosInBranch.length; i++) {
		if (!thisBioModelInfo.getVersion().getDate().equals(bioModelInfosInBranch[i].getVersion().getDate())) {
			revisedBMInfosInBranch[j] = bioModelInfosInBranch[i];
			j++;
		}
 	}
			 	
	return revisedBMInfosInBranch;	
}


/**
 * Return the JMenu1 property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getCompareWithMenu() {
	if (compareWithMenu == null) {
		try {
			compareWithMenu = new javax.swing.JMenu();
			compareWithMenu.setName("JMenu1");
			compareWithMenu.setText("Compare With");
			compareWithMenu.add(getJPreviousEditionMenuItem());
			compareWithMenu.add(getLatestEditionMenuItem());
			compareWithMenu.add(getAnotherEditionMenuItem());
			compareWithMenu.add(getAnotherModelMenuItem());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return compareWithMenu;
}

/**
 * Return the JMenuItemArchive property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemArchive() {
	if (ivjJMenuItemArchive == null) {
		try {
			ivjJMenuItemArchive = new javax.swing.JMenuItem();
			ivjJMenuItemArchive.setName("JMenuItemArchive");
			ivjJMenuItemArchive.setText("Archive");
			ivjJMenuItemArchive.setActionCommand("Archive");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemArchive;
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
 * Return the JMenuItemExport property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemExport() {
	if (ivjJMenuItemExport == null) {
		try {
			ivjJMenuItemExport = new javax.swing.JMenuItem();
			ivjJMenuItemExport.setName("JMenuItemExport");
			ivjJMenuItemExport.setMnemonic('e');
			ivjJMenuItemExport.setText("Export");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemExport;
}


/**
 * Return the JMenuItem1 property value.
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

/**
 * Return the JMenuItemUnpublish property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPermission() {
	if (ivjJMenuItemPermission == null) {
		try {
			ivjJMenuItemPermission = new javax.swing.JMenuItem();
			ivjJMenuItemPermission.setName("JMenuItemPermission");
			ivjJMenuItemPermission.setMnemonic('u');
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

private javax.swing.JMenuItem getJMenuItemPublish() {
	if (ivjJMenuItemPublish == null) {
		try {
			ivjJMenuItemPublish = new javax.swing.JMenuItem();
			ivjJMenuItemPublish.setName("JMenuItemPublish");
			ivjJMenuItemPublish.setText("Publish");
			ivjJMenuItemPublish.setActionCommand("Publish");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPublish;
}
private void onlinePublish(java.awt.event.ActionEvent arg1) {
	try {
		this.invokeOnlinePublish();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
private void invokeOnlinePublish() {

	PopupGenerator.browserLauncher(this, BeanUtils.getDynamicClientProperties().getProperty(PropertyLoader.ACKNOWLEGE_PUB__WEB_URL),
		"Please visit "+BeanUtils.getDynamicClientProperties().getProperty(PropertyLoader.ACKNOWLEGE_PUB__WEB_URL)+" for instructions on how to Publish your Model");
}

/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
protected javax.swing.JPanel getBottomPanel() {
	if (bottomPanel == null) {
		try {
			bottomPanel = new javax.swing.JPanel();
			bottomPanel.setName("JPanel2");
			bottomPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			bottomPanel.add(new JLabel("Selected BioModel Summary"), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
			constraintsJScrollPane2.gridx = 0; constraintsJScrollPane2.gridy = 1;
			constraintsJScrollPane2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane2.weightx = 1.0;
			constraintsJScrollPane2.weighty = 1.0;
			constraintsJScrollPane2.insets = new java.awt.Insets(0, 4, 4, 4);
			bottomPanel.add(getBioModelMetaDataPanel(), constraintsJScrollPane2);
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
 * Return the JMenuItem1 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJPreviousEditionMenuItem() {
	if (ivjJPreviousEditionMenuItem == null) {
		try {
			ivjJPreviousEditionMenuItem = new javax.swing.JMenuItem();
			ivjJPreviousEditionMenuItem.setName("JPreviousEditionMenuItem");
			ivjJPreviousEditionMenuItem.setText("Previous Edition");
			ivjJPreviousEditionMenuItem.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPreviousEditionMenuItem;
}

/**
 * Return the JSeparator3 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator3() {
	if (ivjJSeparator3 == null) {
		try {
			ivjJSeparator3 = new javax.swing.JSeparator();
			ivjJSeparator3.setName("JSeparator3");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator3;
}
protected VCDocumentDbCellRenderer createTreeCellRenderer() {
	return new BioModelCellRenderer();
}

/**
 * Return the LatestEditionMenuItem property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getLatestEditionMenuItem() {
	if (ivjLatestEditionMenuItem == null) {
		try {
			ivjLatestEditionMenuItem = new javax.swing.JMenuItem();
			ivjLatestEditionMenuItem.setName("LatestEditionMenuItem");
			ivjLatestEditionMenuItem.setText("Latest Edition");
			ivjLatestEditionMenuItem.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLatestEditionMenuItem;
}

/**
 * Initialize the class.
 */
@Override
protected void initialize() {
	try {
		super.initialize();
		addPropertyChangeListener(ivjEventHandler);
		getJMenuItemDelete().addActionListener(ivjEventHandler);
		getJMenuItemOpen().addActionListener(ivjEventHandler);
		getJMenuItemPermission().addActionListener(ivjEventHandler);
		getLatestEditionMenuItem().addActionListener(ivjEventHandler);
		getAnotherEditionMenuItem().addActionListener(ivjEventHandler);
		getAnotherModelMenuItem().addActionListener(ivjEventHandler);
		getJMenuItemExport().addActionListener(ivjEventHandler);
		getJPreviousEditionMenuItem().addActionListener(ivjEventHandler);
		getJMenuItemArchive().addActionListener(ivjEventHandler);
		getJMenuItemPublish().addActionListener(ivjEventHandler);
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
		BioModelDbTreePanel aBioModelDbTreePanel;
		aBioModelDbTreePanel = new BioModelDbTreePanel();
		frame.setContentPane(aBioModelDbTreePanel);
		frame.setSize(aBioModelDbTreePanel.getSize());
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
private void compareWithMenuItemEnable(VersionInfo vInfo) {

	boolean bPreviousEditionMenuItem = false;
	boolean bLatestEditionMenuItem = false;
	boolean bAnotherEditionMenuItem = false;
	BioModelInfo thisBioModelInfo = (BioModelInfo)vInfo;
	//
	// Get the other versions of the Biomodel
	//	
	try {
		BioModelInfo bioModelVersionsList[] = getBioModelVersionDates(thisBioModelInfo);
	
		if (bioModelVersionsList != null && bioModelVersionsList.length > 0) {
			bAnotherEditionMenuItem = true;
			//
			// Obtaining the previous version of the current biomodel.
			//		
			BioModelInfo previousBioModelInfo = bioModelVersionsList[0];
			boolean bPrevious = false;
	
			for (int i = 0; i < bioModelVersionsList.length; i++) {
				if (bioModelVersionsList[i].getVersion().getDate().before(thisBioModelInfo.getVersion().getDate())) {
					bPrevious = true;
					previousBioModelInfo = bioModelVersionsList[i];
				} else {
					break;
				}
			}
	
			if (previousBioModelInfo.equals(bioModelVersionsList[0]) && !bPrevious) {
				bPreviousEditionMenuItem = false;
			} else {
				bPreviousEditionMenuItem = true;
			}
			
			//
			// Obtaining the latest version of the current biomodel
			//
			
			BioModelInfo latestBioModelInfo = bioModelVersionsList[bioModelVersionsList.length-1];
	
			for (int i = 0; i < bioModelVersionsList.length; i++) {
				if (bioModelVersionsList[i].getVersion().getDate().after(latestBioModelInfo.getVersion().getDate())) {
					latestBioModelInfo = bioModelVersionsList[i];
				}
			}
	
			if (thisBioModelInfo.getVersion().getDate().after(latestBioModelInfo.getVersion().getDate())) {
				bLatestEditionMenuItem = false;
			} else {
				bLatestEditionMenuItem = true;
			}
		}
	} catch (DataAccessException e) {
		e.printStackTrace();
	}
	getJPreviousEditionMenuItem().setEnabled(bPreviousEditionMenuItem);
	getLatestEditionMenuItem().setEnabled(bLatestEditionMenuItem);
	getAnotherEditionMenuItem().setEnabled(bAnotherEditionMenuItem);
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
	if(object instanceof PublicationInfo) {
		setSelectedVersionInfo(null);
		setSelectedPublicationInfo(bioModelNode);
	} else if (object instanceof BioModelInfo){
		setSelectedVersionInfo((VersionInfo)object);
	}else if (object instanceof VCDocumentInfoNode && bioModelNode.getChildCount()>0 && ((BioModelNode)bioModelNode.getChildAt(0)).getUserObject() instanceof BioModelInfo){
		BioModelInfo bioModelInfo = (BioModelInfo)((BioModelNode)bioModelNode.getChildAt(0)).getUserObject();
		setSelectedVersionInfo(bioModelInfo);
	}else if (object instanceof BioModelMetaData) {
		BioModelInfo bioModelInfo = (BioModelInfo)((BioModelNode)bioModelNode.getParent()).getUserObject();
		setSelectedVersionInfo(bioModelInfo);
	}else{
		setSelectedVersionInfo(null);
	}
}

}
