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
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;
import org.vcell.util.document.VersionInfo;

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
public class MathModelDbTreePanel extends VCDocumentDbTreePanel {
	private JMenuItem ivjJMenuItemDelete = null;
	private JMenuItem ivjJMenuItemOpen = null;
	private JPopupMenu ivjMathModelPopupMenu = null;
	private MathModelMetaDataPanel ivjMathModelMetaDataPanel = null;
	private JMenuItem ivjJMenuItemPermission = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JMenuItem ivjJMenuItemExport = null;
	private JSeparator ivjJSeparator3 = null;
	private JMenu ivjJMenuCompare = null;
	private JMenuItem ivjJMenuItemAnother = null;
	private JMenuItem ivjJAnotherEditionMenuItem = null;
	private JMenuItem ivjJLatestEditionMenuItem = null;
	private JMenuItem ivjJPreviousEditionMenuItem = null;
	private JMenuItem ivjJMenuItemArchive = null;
	private JMenuItem ivjJMenuItemPublish = null;	

	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == MathModelDbTreePanel.this.getJMenuItemDelete())
				refireActionPerformed(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJMenuItemOpen())
				refireActionPerformed(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJMenuItemPermission())
				refireActionPerformed(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJMenuItemExport())
				refireActionPerformed(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJMenuItemAnother())
				refireActionPerformed(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJPreviousEditionMenuItem())
				refireActionPerformed(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJLatestEditionMenuItem())
				refireActionPerformed(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJAnotherEditionMenuItem())
				refireActionPerformed(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJMenuItemArchive())
				refireActionPerformed(e);
			if (e.getSource() == MathModelDbTreePanel.this.getJMenuItemPublish())
				onlinePublish(e);
//				refireActionPerformed(e);
		}
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == MathModelDbTreePanel.this && (evt.getPropertyName().equals("selectedVersionInfo"))) {
				getMathModelMetaDataPanel().setMathModelInfo((MathModelInfo)getSelectedVersionInfo());
			} else if(evt.getSource() == MathModelDbTreePanel.this && (evt.getPropertyName().equals("selectedPublicationInfo"))) {
				getMathModelMetaDataPanel().setPublicationInfo((PublicationInfoNode)evt.getNewValue());
			}
		}
	}

	public MathModelDbTreePanel() {
		this(true);
	}
	
/**
 * BioModelTreePanel constructor comment.
 */
public MathModelDbTreePanel(boolean bMetadata) {
	super(bMetadata);
	initialize();
}

protected void actionsOnClick(MouseEvent mouseEvent) {
	if (mouseEvent.isPopupTrigger()) {
		if(!getPopupMenuDisabled()){
			TreePath treePath = ((JTree)mouseEvent.getSource()).getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
			
			((JTree)mouseEvent.getSource()).setSelectionPath(treePath);
			if(getSelectedVersionInfo() instanceof MathModelInfo){
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
				getMathModelPopupMenu().show(getJTree1(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
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
			ifNeedsDoubleClickEvent(mouseEvent,MathModelInfo.class);
		}
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 8:15:47 AM)
 */
private void configureArhivePublishMenuState(Version version,boolean isOwner) {
	
	getJMenuItemArchive().setEnabled(
		isOwner
		&& !version.getFlag().isArchived()
		&& !version.getFlag().isPublished());
	
	getJMenuItemPublish().setEnabled(isOwner);
//		isOwner
//		&& version.getFlag().isArchived()
//		&& (version.getGroupAccess() instanceof GroupAccessAll)
//		&& !version.getFlag().isPublished()
//		&& version.getOwner().isPublisher());
}


/**
 * Comment
 */
protected void documentManager_DatabaseDelete(DatabaseEvent event) {
	if (event.getOldVersionInfo() instanceof MathModelInfo && getSelectedVersionInfo() instanceof MathModelInfo) {
		MathModelInfo selectedMMInfo = (MathModelInfo)getSelectedVersionInfo();
		MathModelInfo eventMMInfo = (MathModelInfo)event.getOldVersionInfo();
		if (eventMMInfo.getVersion().getVersionKey().equals(selectedMMInfo.getVersion().getVersionKey())){
			setSelectedVersionInfo(null);
			getJTree1().getSelectionModel().clearSelection();
		}		
	}
}


/**
 * Comment
 */
protected void documentManager_DatabaseUpdate(DatabaseEvent event) {
	if (event.getNewVersionInfo() instanceof MathModelInfo && getSelectedVersionInfo() instanceof MathModelInfo) {
		MathModelInfo selectedMMInfo = (MathModelInfo)getSelectedVersionInfo();
		MathModelInfo eventMMInfo = (MathModelInfo)event.getNewVersionInfo();
		if (eventMMInfo.getVersion().getVersionKey().equals(selectedMMInfo.getVersion().getVersionKey())){
			setSelectedVersionInfo(event.getNewVersionInfo());
		}		
	}
}

/**
 * Return the JAnotherEditionMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJAnotherEditionMenuItem() {
	if (ivjJAnotherEditionMenuItem == null) {
		try {
			ivjJAnotherEditionMenuItem = new javax.swing.JMenuItem();
			ivjJAnotherEditionMenuItem.setName("JAnotherEditionMenuItem");
			ivjJAnotherEditionMenuItem.setMnemonic('E');
			ivjJAnotherEditionMenuItem.setText("Another Edition...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJAnotherEditionMenuItem;
}

/**
 * Return the JLatestEditionMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJLatestEditionMenuItem() {
	if (ivjJLatestEditionMenuItem == null) {
		try {
			ivjJLatestEditionMenuItem = new javax.swing.JMenuItem();
			ivjJLatestEditionMenuItem.setName("JLatestEditionMenuItem");
			ivjJLatestEditionMenuItem.setMnemonic('L');
			ivjJLatestEditionMenuItem.setText("Latest Edition");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLatestEditionMenuItem;
}


/**
 * Return the JMenuCompare property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuCompare() {
	if (ivjJMenuCompare == null) {
		try {
			ivjJMenuCompare = new javax.swing.JMenu();
			ivjJMenuCompare.setName("JMenuCompare");
			ivjJMenuCompare.setMnemonic('C');
			ivjJMenuCompare.setText("Compare with..");
			ivjJMenuCompare.add(getJPreviousEditionMenuItem());
			ivjJMenuCompare.add(getJLatestEditionMenuItem());
			ivjJMenuCompare.add(getJAnotherEditionMenuItem());
			ivjJMenuCompare.add(getJMenuItemAnother());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuCompare;
}

/**
 * Return the JMenuItemAnother property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemAnother() {
	if (ivjJMenuItemAnother == null) {
		try {
			ivjJMenuItemAnother = new javax.swing.JMenuItem();
			ivjJMenuItemAnother.setName("JMenuItemAnother");
			ivjJMenuItemAnother.setMnemonic('A');
			ivjJMenuItemAnother.setText("Another Model...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemAnother;
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
			bottomPanel.add(new JLabel("Selected MathModel Summary"), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
			constraintsJScrollPane2.gridx = 0; constraintsJScrollPane2.gridy = 1;
			constraintsJScrollPane2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane2.weightx = 1.0;
			constraintsJScrollPane2.weighty = 1.0;
			constraintsJScrollPane2.insets = new java.awt.Insets(0, 4, 4, 4);
			bottomPanel.add(getMathModelMetaDataPanel(), constraintsJScrollPane2);
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
 * Return the JPreviousEditionMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJPreviousEditionMenuItem() {
	if (ivjJPreviousEditionMenuItem == null) {
		try {
			ivjJPreviousEditionMenuItem = new javax.swing.JMenuItem();
			ivjJPreviousEditionMenuItem.setName("JPreviousEditionMenuItem");
			ivjJPreviousEditionMenuItem.setMnemonic('P');
			ivjJPreviousEditionMenuItem.setText("Previous Edition");
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

/**
 * Return the JTree1 property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
protected VCDocumentDbCellRenderer createTreeCellRenderer() {
	return new MathModelCellRenderer();
}

/**
 * Return the MathModelDbTreeModel property value.
 * @return cbit.vcell.desktop.MathModelDbTreeModel
 */
protected VCDocumentDbTreeModel createTreeModel() {
	return new MathModelDbTreeModel(getJTree1());
}


/**
 * Return the MathModelMetaDataPanel property value.
 * @return cbit.vcell.desktop.MathModelMetaDataPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MathModelMetaDataPanel getMathModelMetaDataPanel() {
	if (ivjMathModelMetaDataPanel == null) {
		try {
			ivjMathModelMetaDataPanel = new MathModelMetaDataPanel();
			ivjMathModelMetaDataPanel.setName("MathModelMetaDataPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathModelMetaDataPanel;
}


/**
 * Return the JPopupMenu1 property value.
 * @return javax.swing.JPopupMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getMathModelPopupMenu() {
	if (ivjMathModelPopupMenu == null) {
		try {
			ivjMathModelPopupMenu = new javax.swing.JPopupMenu();
			ivjMathModelPopupMenu.setName("MathModelPopupMenu");
			ivjMathModelPopupMenu.add(getJMenuItemOpen());
			ivjMathModelPopupMenu.add(getJMenuItemDelete());
			ivjMathModelPopupMenu.add(getJMenuItemPermission());
			ivjMathModelPopupMenu.add(getJMenuItemArchive());
			ivjMathModelPopupMenu.add(getJMenuItemPublish());
			ivjMathModelPopupMenu.add(getJMenuCompare());
//			ivjMathModelPopupMenu.add(getJSeparator3());
//			ivjMathModelPopupMenu.add(getJMenuItemExport());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathModelPopupMenu;
}

/**
 * Insert the method's description here.
 * Creation date: (10/7/2002 3:06:29 PM)
 */
private MathModelInfo[] getMathModelVersionDates(MathModelInfo thisMathModelInfo) throws DataAccessException {
	//
	// Get list of MathModelInfos in workspace
	//

	MathModelInfo mathModelInfos[] = getDocumentManager().getMathModelInfos();

	//
	// From the list of mathmodels in the workspace, get list of mathmodels with the same branch ID.
	// This is the list of different versions of the same mathmodel.
	//
 	Vector<MathModelInfo> mathModelBranchList = new Vector<MathModelInfo>();
 	for (int i = 0; i < mathModelInfos.length; i++) {
	 	MathModelInfo mathModelInfo = mathModelInfos[i];
	 	if (mathModelInfo.getVersion().getBranchID().equals(thisMathModelInfo.getVersion().getBranchID())) {
		 	mathModelBranchList.add(mathModelInfo);
	 	}
 	}

 	if (mathModelBranchList.size() == 0) {
		JOptionPane.showMessageDialog(this,"No Versions in Mathmodel","Error comparing MathModels",JOptionPane.ERROR_MESSAGE);
	 	throw new NullPointerException("No Versions in Mathmodel!");
 	}

 	MathModelInfo mathModelInfosInBranch[] = new MathModelInfo[mathModelBranchList.size()];
 	mathModelBranchList.copyInto(mathModelInfosInBranch);

 	//
 	// From the versions list, remove the currently selected version and return the remaining list of
 	// versions for the mathmodel
 	//

 	MathModelInfo revisedMMInfosInBranch[] = new MathModelInfo[mathModelInfosInBranch.length-1];
 	int j=0;
 	
 	for (int i = 0; i < mathModelInfosInBranch.length; i++) {
		if (!thisMathModelInfo.getVersion().getDate().equals(mathModelInfosInBranch[i].getVersion().getDate())) {
			revisedMMInfosInBranch[j] = mathModelInfosInBranch[i];
			j++;
		}
 	}
			 	
	return revisedMMInfosInBranch;	
}


/**
 * Initialize the class.
 */
@Override
protected void initialize() {
	try {
		super.initialize();
		this.addPropertyChangeListener(ivjEventHandler);
		getJMenuItemDelete().addActionListener(ivjEventHandler);
		getJMenuItemOpen().addActionListener(ivjEventHandler);
		getJMenuItemPermission().addActionListener(ivjEventHandler);
		getJMenuItemExport().addActionListener(ivjEventHandler);
		getJMenuItemAnother().addActionListener(ivjEventHandler);
		getJPreviousEditionMenuItem().addActionListener(ivjEventHandler);
		getJLatestEditionMenuItem().addActionListener(ivjEventHandler);
		getJAnotherEditionMenuItem().addActionListener(ivjEventHandler);
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
	if (vInfo!=null){
		MathModelInfo thisMathModelInfo = (MathModelInfo)vInfo;

		try {
			//
			// Get the other versions of the Mathmodel
			//
			MathModelInfo mathModelVersionsList[] = getMathModelVersionDates(thisMathModelInfo);
			
			if (mathModelVersionsList != null && mathModelVersionsList.length > 0) {
				bAnotherEditionMenuItem = true;			
		
				//
				// Obtaining the previous version of the current biomodel
				//
			
				MathModelInfo previousMathModelInfo = mathModelVersionsList[0];
				boolean bPrevious = false;
	
				for (int i = 0; i < mathModelVersionsList.length; i++) {
					if (mathModelVersionsList[i].getVersion().getDate().before(thisMathModelInfo.getVersion().getDate())) {
						bPrevious = true;
						previousMathModelInfo = mathModelVersionsList[i];
					} else {
						break;
					}
				}
	
				if (previousMathModelInfo.equals(mathModelVersionsList[0]) && !bPrevious) {
					bPreviousEditionMenuItem = false;
				} else {
					bPreviousEditionMenuItem = true;
				}
				
				//
				// Obtaining the latest version of the current mathmodel
				//
				MathModelInfo latestMathModelInfo = mathModelVersionsList[mathModelVersionsList.length-1];

				for (int i = 0; i < mathModelVersionsList.length; i++) {
					if (mathModelVersionsList[i].getVersion().getDate().after(latestMathModelInfo.getVersion().getDate())) {
						latestMathModelInfo = mathModelVersionsList[i];
					}
				}

				if (thisMathModelInfo.getVersion().getDate().after(latestMathModelInfo.getVersion().getDate())) {
					bLatestEditionMenuItem = false;
				} else {
					bLatestEditionMenuItem = true;
				}
			}
		} catch (DataAccessException ex) {
			ex.printStackTrace();
		}
	}
	getJPreviousEditionMenuItem().setEnabled(bPreviousEditionMenuItem);
	getJLatestEditionMenuItem().setEnabled(bLatestEditionMenuItem);
	getJAnotherEditionMenuItem().setEnabled(bAnotherEditionMenuItem);
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
		setSelectedVersionInfo(null);				// TODO: is this line needed?
		setSelectedPublicationInfo(bioModelNode);
	} else if (object instanceof VersionInfo) {
		setSelectedVersionInfo((VersionInfo)object);
	}else if (object instanceof VCDocumentInfoNode && bioModelNode.getChildCount()>0 && ((BioModelNode)bioModelNode.getChildAt(0)).getUserObject() instanceof MathModelInfo){
		MathModelInfo mathModelInfo = (MathModelInfo)((BioModelNode)bioModelNode.getChildAt(0)).getUserObject();
		setSelectedVersionInfo(mathModelInfo);
	}else{
		setSelectedVersionInfo(null);
	}
}

}
