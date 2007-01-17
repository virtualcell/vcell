package cbit.vcell.messaging.admin;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (8/22/2003 3:31:28 PM)
 * @author: Fei Gao
 */
public class ServerDetailDialog extends javax.swing.JDialog {
	private javax.swing.JButton ivjAddNewButton = null;
	private javax.swing.JButton ivjApplyButton = null;
	private javax.swing.JTextField ivjArcDirField = null;
	private javax.swing.JButton ivjCancelServiceButton = null;
	private javax.swing.JPanel ivjConfigPage = null;
	private javax.swing.JButton ivjDeleteServiceButton = null;
	private javax.swing.JTabbedPane ivjDetailTabbedPane = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JLabel ivjJLabel4 = null;
	private javax.swing.JLabel ivjJLabel5 = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private java.awt.GridLayout ivjJPanel2GridLayout = null;
	private javax.swing.JTextField ivjLogfileField = null;
	private javax.swing.JButton ivjModifyServiceButton = null;
	public VCServerInfo serverInfo = null;
	private java.util.LinkedList changeList = new LinkedList();
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private boolean action;
	private javax.swing.JButton ivjBrowseButton = null;
	private cbit.vcell.messaging.admin.sorttable.JSortTable ivjServiceConfigTable = null;
	private javax.swing.JScrollPane ivjJScrollPane2 = null;
	private boolean bChanged = false;
	private javax.swing.JPanel ivjJPanel3 = null;
	private java.awt.FlowLayout ivjJPanel3FlowLayout = null;
	private java.awt.FlowLayout ivjJPanel1FlowLayout = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.KeyListener, java.awt.event.MouseListener, java.awt.event.WindowListener, javax.swing.event.ChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ServerDetailDialog.this.getApplyButton()) 
				connEtoC3(e);
			if (e.getSource() == ServerDetailDialog.this.getAddNewButton()) 
				connEtoC4(e);
			if (e.getSource() == ServerDetailDialog.this.getModifyServiceButton()) 
				connEtoC5(e);
			if (e.getSource() == ServerDetailDialog.this.getDeleteServiceButton()) 
				connEtoC6(e);
			if (e.getSource() == ServerDetailDialog.this.getCancelServiceButton()) 
				connEtoC7();
			if (e.getSource() == ServerDetailDialog.this.getBrowseButton()) 
				connEtoC9(e);
		};
		public void keyPressed(java.awt.event.KeyEvent e) {};
		public void keyReleased(java.awt.event.KeyEvent e) {
			if (e.getSource() == ServerDetailDialog.this.getLogfileField()) 
				connEtoC11(e);
			if (e.getSource() == ServerDetailDialog.this.getArcDirField()) 
				connEtoC12(e);
		};
		public void keyTyped(java.awt.event.KeyEvent e) {};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == ServerDetailDialog.this.getServiceConfigTable()) 
				connEtoC2(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == ServerDetailDialog.this.getDetailTabbedPane()) 
				connEtoC1();
		};
		public void windowActivated(java.awt.event.WindowEvent e) {};
		public void windowClosed(java.awt.event.WindowEvent e) {};
		public void windowClosing(java.awt.event.WindowEvent e) {
			if (e.getSource() == ServerDetailDialog.this) 
				connEtoC8(e);
		};
		public void windowDeactivated(java.awt.event.WindowEvent e) {};
		public void windowDeiconified(java.awt.event.WindowEvent e) {};
		public void windowIconified(java.awt.event.WindowEvent e) {};
		public void windowOpened(java.awt.event.WindowEvent e) {};
	};

/**
 * ServerDetailDialog constructor comment.
 */
public ServerDetailDialog() {
	super();
	initialize();
}

/**
 * ServerDetailDialog constructor comment.
 * @param owner java.awt.Dialog
 */
public ServerDetailDialog(java.awt.Dialog owner) {
	super(owner);
	initialize();
}


/**
 * ServerDetailDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 */
public ServerDetailDialog(java.awt.Dialog owner, String title) {
	super(owner, title);
}


/**
 * ServerDetailDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 * @param modal boolean
 */
public ServerDetailDialog(java.awt.Dialog owner, String title, boolean modal) {
	super(owner, title, modal);
}


/**
 * ServerDetailDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param modal boolean
 */
public ServerDetailDialog(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
}


/**
 * ServerDetailDialog constructor comment.
 * @param owner java.awt.Frame
 */
public ServerDetailDialog(java.awt.Frame owner) {
	super(owner);
	initialize();
}


/**
 * ServerDetailDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public ServerDetailDialog(java.awt.Frame owner, String title) {
	super(owner, title);
}


/**
 * ServerDetailDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public ServerDetailDialog(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}


/**
 * ServerDetailDialog constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public ServerDetailDialog(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 3:19:02 PM)
 */
public void addNew() {
	AddNewServiceDialog dialog = new AddNewServiceDialog(this);
	dialog.initNew(serverInfo);
	dialog.setLocationRelativeTo(this);
	dialog.setTitle("Add New");
	dialog.setVisible(true);

	if (dialog.isAction()) {
		VCServiceInfo serviceInfo = dialog.getServiceInfo();
		do_changes(serviceInfo, ManageConstants.SERVICE_MODIFIER_NEW);
	}

	dialog = null;
}


/**
 * Comment
 */
public void addNewButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	addNew();
}


/**
 * Comment
 */
public void applyButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if (changeList.size() == 0 && !bChanged) {
		javax.swing.JOptionPane.showMessageDialog(this, "No changes have been made.", "Info", javax.swing.JOptionPane.INFORMATION_MESSAGE);
		action = false;
	} else {
		action = true;
		((ServerManageConsole)getParent()).applyChanges(serverInfo, changeList, getArchiveDir());	
	}

	dispose();
	return;
}


/**
 * Comment
 */
public void arcDirField_KeyReleased(java.awt.event.KeyEvent keyEvent) {
	getApplyButton().setEnabled(true);
	bChanged = true;
	return;
}


/**
 * Comment
 */
public void browseButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	browseChanges();
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (8/26/2003 1:37:46 PM)
 */
public void browseChanges() {
	if (changeList.size() == 0) {
		javax.swing.JOptionPane.showMessageDialog(this, "No changes have been made.", "Warning", javax.swing.JOptionPane.INFORMATION_MESSAGE);
		return;		
	}
	
	BrowseChangeDialog dialog = new BrowseChangeDialog(this);
	dialog.setServerType(serverInfo.isServerManager());
	dialog.setChanges(changeList);
	dialog.setLocationRelativeTo(this);
	dialog.setVisible(true);

	if (dialog.isAction()) {
		List newChanges = dialog.getChanges();
		List tempList = new LinkedList(changeList);
		
		Iterator iter = tempList.iterator();
		while (iter.hasNext()) {
			VCServiceInfo serviceInfo = (VCServiceInfo)iter.next();
			if (newChanges.contains(serviceInfo)) {
				continue;
			}

			changeList.remove(serviceInfo);
				
			switch (serviceInfo.getModifier()) {
				case ManageConstants.SERVICE_MODIFIER_NEW: {
					((ServiceConfigTableModel)getServiceConfigTable().getModel()).remove(serviceInfo);
					break;
				}

				case ManageConstants.SERVICE_MODIFIER_MODIFY: {
					int index1 = serverInfo.indexOf(serviceInfo);
					int index2 = ((ServiceConfigTableModel)getServiceConfigTable().getModel()).indexOf(serviceInfo.getConfiguration());
					((ServiceConfigTableModel)getServiceConfigTable().getModel()).setValueAt(index2, (VCServiceInfo)serverInfo.getServiceList().get(index1));
					break;
				}

				case ManageConstants.SERVICE_MODIFIER_DELETE: {
					((ServiceConfigTableModel)getServiceConfigTable().getModel()).insert(serviceInfo);
					break;
				}
					
			}
		}
	}
	dialog = null;
	updateButtons();
}


/**
 * Comment
 */
public void cancelServiceButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	closeDetail();
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 3:19:02 PM)
 */
public void closeDetail() {
	if (changeList.size() > 0) {
		int n = javax.swing.JOptionPane.showConfirmDialog(this, "The changes you made will be lost. Continue?", "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
		if (n == javax.swing.JOptionPane.NO_OPTION) {
			return;
		} 	
	} 

	dispose();
	changeList.clear();			
	action = false;
}


/**
 * connEtoC1:  (DetailTabbedPane.change. --> ServerDetailDialog.detailTabbedPane_ChangeEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.detailTabbedPane_ChangeEvents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC10:  (ServerDetailDialog.initialize() --> ServerDetailDialog.serverDetailDialog_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10() {
	try {
		// user code begin {1}
		// user code end
		this.serverDetailDialog_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (LogfileField.key.keyReleased(java.awt.event.KeyEvent) --> ServerDetailDialog.logfileField_KeyReleased(Ljava.awt.event.KeyEvent;)V)
 * @param arg1 java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.KeyEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.logfileField_KeyReleased(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC12:  (ArcDirField.key.keyReleased(java.awt.event.KeyEvent) --> ServerDetailDialog.arcDirField_KeyReleased(Ljava.awt.event.KeyEvent;)V)
 * @param arg1 java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.KeyEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.arcDirField_KeyReleased(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (ServiceConfigTable.mouse.mouseClicked(java.awt.event.MouseEvent) --> ServerDetailDialog.serviceConfigTable_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.serviceConfigTable_MouseClicked(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (ApplyButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerDetailDialog.applyButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.applyButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (AddNewButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerDetailDialog.addNewButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.addNewButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (ModifyServiceButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerDetailDialog.modifyServiceButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.modifyServiceButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (DeleteServiceButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerDetailDialog.deleteServiceButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.deleteServiceButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (CancelServiceButton.action. --> ServerDetailDialog.cancelServiceButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7() {
	try {
		// user code begin {1}
		// user code end
		this.cancelServiceButton_ActionPerformed(null);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (ServerDetailDialog.window.windowClosing(java.awt.event.WindowEvent) --> ServerDetailDialog.serverDetailDialog_WindowClosing(Ljava.awt.event.WindowEvent;)V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.serverDetailDialog_WindowClosing(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (BrowseButton.action.actionPerformed(java.awt.event.ActionEvent) --> ServerDetailDialog.browseButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.browseButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
public void delete() {		
	int srow = getServiceConfigTable().getSelectedRow();
	VCServiceInfo serviceInfo = (VCServiceInfo)((ServiceConfigTableModel)getServiceConfigTable().getModel()).getValueAt(srow);
	do_changes(serviceInfo, ManageConstants.SERVICE_MODIFIER_DELETE);
		
	return;
}


/**
 * Comment
 */
public void deleteServiceButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	delete();
}


/**
 * Comment
 */
public void detailTabbedPane_ChangeEvents() {
	updateButtons();
	return;
}


/**
 * Comment
 */
private void do_changes(VCServiceInfo serviceInfo, int modifier) {	
		
	int index = changeList.indexOf(serviceInfo);
	if (index < 0) {
		serviceInfo.setModifier(modifier);
		switch (modifier) {
			case ManageConstants.SERVICE_MODIFIER_NEW: {
				if (((ServiceConfigTableModel)getServiceConfigTable().getModel()).contains(serviceInfo)) {
					javax.swing.JOptionPane.showMessageDialog(this, "The service you add exists and will NOT be added.", "Warning", javax.swing.JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				((ServiceConfigTableModel)getServiceConfigTable().getModel()).insert(serviceInfo);
				changeList.add(serviceInfo);
				break;
			}
				
			case ManageConstants.SERVICE_MODIFIER_DELETE: {
				int n = javax.swing.JOptionPane.showConfirmDialog(this, "The service will be deleted but can be restored later. Continue?", "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
				if (n == javax.swing.JOptionPane.NO_OPTION) {
					break;
				}
			
				((ServiceConfigTableModel)getServiceConfigTable().getModel()).remove(serviceInfo);
				changeList.add(serviceInfo);
				break;
			}

			case ManageConstants.SERVICE_MODIFIER_MODIFY: {
				((ServiceConfigTableModel)getServiceConfigTable().getModel()).setValueAt(getServiceConfigTable().getSelectedRow(), serviceInfo);
				changeList.add(serviceInfo);
				break;
			}
		}			

	} else {
		VCServiceInfo serviceInfo0 = (VCServiceInfo)changeList.get(index);
		
		switch (modifier) {				
			case ManageConstants.SERVICE_MODIFIER_NEW:	{			
				if (serviceInfo0.getModifier() == ManageConstants.SERVICE_MODIFIER_DELETE) {
					int n = javax.swing.JOptionPane.showConfirmDialog(this, "The service you add has been deleted before. Continue?", "Warning", javax.swing.JOptionPane.YES_NO_OPTION);
					if (n == javax.swing.JOptionPane.YES_OPTION) {
						serviceInfo.setModifier(ManageConstants.SERVICE_MODIFIER_MODIFY);
						changeList.set(index, serviceInfo);
						((ServiceConfigTableModel)getServiceConfigTable().getModel()).insert(serviceInfo);	
					}
				} else {
					javax.swing.JOptionPane.showMessageDialog(this, "The service you add EITHER exists OR has been added before and will NOT be added.", "Warning", javax.swing.JOptionPane.INFORMATION_MESSAGE);					
				}
				
				break;
			}

			case ManageConstants.SERVICE_MODIFIER_DELETE: {
				if (serviceInfo0.getModifier() == ManageConstants.SERVICE_MODIFIER_NEW) {
					int n = javax.swing.JOptionPane.showConfirmDialog(this, "The service you delete is a newly added service and will be lost forever. Continue?", "Warning", javax.swing.JOptionPane.YES_NO_OPTION);
					if (n == javax.swing.JOptionPane.YES_OPTION) {
						changeList.remove(serviceInfo0);
						((ServiceConfigTableModel)getServiceConfigTable().getModel()).remove(serviceInfo);	
					}
				}
				break;
			}

			case ManageConstants.SERVICE_MODIFIER_MODIFY: {
				serviceInfo.setModifier(serviceInfo0.getModifier());
				changeList.set(index, serviceInfo);
				((ServiceConfigTableModel)getServiceConfigTable().getModel()).setValueAt(getServiceConfigTable().getSelectedRow(), serviceInfo);
				break;
			}
		}
	}
	
	updateButtons();

	return;
}


/**
 * Return the AddNewButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAddNewButton() {
	if (ivjAddNewButton == null) {
		try {
			ivjAddNewButton = new javax.swing.JButton();
			ivjAddNewButton.setName("AddNewButton");
			ivjAddNewButton.setText("Add New");
			ivjAddNewButton.setEnabled(true);
			ivjAddNewButton.setActionCommand("Add New");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddNewButton;
}

/**
 * Return the ApplyButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getApplyButton() {
	if (ivjApplyButton == null) {
		try {
			ivjApplyButton = new javax.swing.JButton();
			ivjApplyButton.setName("ApplyButton");
			ivjApplyButton.setText("Apply");
			ivjApplyButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjApplyButton;
}

/**
 * Return the ArcDirField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getArcDirField() {
	if (ivjArcDirField == null) {
		try {
			ivjArcDirField = new javax.swing.JTextField();
			ivjArcDirField.setName("ArcDirField");
			ivjArcDirField.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjArcDirField;
}

/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 8:49:52 AM)
 * @return java.lang.String
 */
public String getArchiveDir() {
	return getArcDirField().getText();
}


/**
 * Return the BrowseButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getBrowseButton() {
	if (ivjBrowseButton == null) {
		try {
			ivjBrowseButton = new javax.swing.JButton();
			ivjBrowseButton.setName("BrowseButton");
			ivjBrowseButton.setText("Browse Changes");
			ivjBrowseButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBrowseButton;
}


/**
 * Return the CancelServiceButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCancelServiceButton() {
	if (ivjCancelServiceButton == null) {
		try {
			ivjCancelServiceButton = new javax.swing.JButton();
			ivjCancelServiceButton.setName("CancelServiceButton");
			ivjCancelServiceButton.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCancelServiceButton;
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 5:01:01 PM)
 * @return java.util.Map
 */
public LinkedList getChanges() {
	return changeList;
}


/**
 * Return the ConfigPage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getConfigPage() {
	if (ivjConfigPage == null) {
		try {
			ivjConfigPage = new javax.swing.JPanel();
			ivjConfigPage.setName("ConfigPage");
			ivjConfigPage.setLayout(new java.awt.BorderLayout());
			getConfigPage().add(getJPanel2(), "North");
			getConfigPage().add(getJScrollPane2(), "Center");
			getConfigPage().add(getJPanel3(), "South");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConfigPage;
}

/**
 * Return the DeleteServiceButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDeleteServiceButton() {
	if (ivjDeleteServiceButton == null) {
		try {
			ivjDeleteServiceButton = new javax.swing.JButton();
			ivjDeleteServiceButton.setName("DeleteServiceButton");
			ivjDeleteServiceButton.setText("Delete");
			ivjDeleteServiceButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeleteServiceButton;
}


/**
 * Return the DetailTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getDetailTabbedPane() {
	if (ivjDetailTabbedPane == null) {
		try {
			ivjDetailTabbedPane = new javax.swing.JTabbedPane();
			ivjDetailTabbedPane.setName("DetailTabbedPane");
			ivjDetailTabbedPane.insertTab("Configurations", null, getConfigPage(), null, 0);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDetailTabbedPane;
}

/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.BorderLayout());
			getJDialogContentPane().add(getJPanel1(), "South");
			getJDialogContentPane().add(getDetailTabbedPane(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}

/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setText("Log File");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
}


/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel5() {
	if (ivjJLabel5 == null) {
		try {
			ivjJLabel5 = new javax.swing.JLabel();
			ivjJLabel5.setName("JLabel5");
			ivjJLabel5.setText("Archive Directory");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel5;
}


/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(getJPanel1FlowLayout());
			getJPanel1().add(getApplyButton(), getApplyButton().getName());
			getJPanel1().add(getBrowseButton(), getBrowseButton().getName());
			getJPanel1().add(getCancelServiceButton(), getCancelServiceButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}

/**
 * Return the JPanel1FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel1FlowLayout() {
	java.awt.FlowLayout ivjJPanel1FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel1FlowLayout = new java.awt.FlowLayout();
		ivjJPanel1FlowLayout.setAlignment(java.awt.FlowLayout.RIGHT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel1FlowLayout;
}


/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(getJPanel2GridLayout());
			getJPanel2().add(getJLabel4(), getJLabel4().getName());
			getJPanel2().add(getLogfileField(), getLogfileField().getName());
			getJPanel2().add(getJLabel5(), getJLabel5().getName());
			getJPanel2().add(getArcDirField(), getArcDirField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}


/**
 * Return the JPanel2GridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getJPanel2GridLayout() {
	java.awt.GridLayout ivjJPanel2GridLayout = null;
	try {
		/* Create part */
		ivjJPanel2GridLayout = new java.awt.GridLayout(2, 2);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel2GridLayout;
}


/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(getJPanel3FlowLayout());
			getJPanel3().add(getAddNewButton(), getAddNewButton().getName());
			getJPanel3().add(getModifyServiceButton(), getModifyServiceButton().getName());
			getJPanel3().add(getDeleteServiceButton(), getDeleteServiceButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}

/**
 * Return the JPanel3FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel3FlowLayout() {
	java.awt.FlowLayout ivjJPanel3FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel3FlowLayout = new java.awt.FlowLayout();
		ivjJPanel3FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel3FlowLayout;
}


/**
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane2() {
	if (ivjJScrollPane2 == null) {
		try {
			ivjJScrollPane2 = new javax.swing.JScrollPane();
			ivjJScrollPane2.setName("JScrollPane2");
			ivjJScrollPane2.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane2.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPane2().setViewportView(getServiceConfigTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane2;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2003 8:49:26 AM)
 * @return java.lang.String
 */
public String getLogfile() {
	return getLogfileField().getText();
}


/**
 * Return the LogfileField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getLogfileField() {
	if (ivjLogfileField == null) {
		try {
			ivjLogfileField = new javax.swing.JTextField();
			ivjLogfileField.setName("LogfileField");
			ivjLogfileField.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLogfileField;
}

/**
 * Return the ModifyServiceButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getModifyServiceButton() {
	if (ivjModifyServiceButton == null) {
		try {
			ivjModifyServiceButton = new javax.swing.JButton();
			ivjModifyServiceButton.setName("ModifyServiceButton");
			ivjModifyServiceButton.setText("Modify");
			ivjModifyServiceButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjModifyServiceButton;
}


/**
 * Return the ServiceConfigTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.messaging.admin.sorttable.JSortTable getServiceConfigTable() {
	if (ivjServiceConfigTable == null) {
		try {
			ivjServiceConfigTable = new cbit.vcell.messaging.admin.sorttable.JSortTable();
			ivjServiceConfigTable.setName("ServiceConfigTable");
			getJScrollPane2().setColumnHeaderView(ivjServiceConfigTable.getTableHeader());
			getJScrollPane2().getViewport().setBackingStoreEnabled(true);
			ivjServiceConfigTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjServiceConfigTable;
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
	getDetailTabbedPane().addChangeListener(ivjEventHandler);
	getApplyButton().addActionListener(ivjEventHandler);
	getAddNewButton().addActionListener(ivjEventHandler);
	getModifyServiceButton().addActionListener(ivjEventHandler);
	getDeleteServiceButton().addActionListener(ivjEventHandler);
	getCancelServiceButton().addActionListener(ivjEventHandler);
	this.addWindowListener(ivjEventHandler);
	getBrowseButton().addActionListener(ivjEventHandler);
	getServiceConfigTable().addMouseListener(ivjEventHandler);
	getLogfileField().addKeyListener(ivjEventHandler);
	getArcDirField().addKeyListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ServerDetailDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(622, 362);
		setModal(true);
		setResizable(true);
		setContentPane(getJDialogContentPane());
		initConnections();
		connEtoC10();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 5:01:31 PM)
 * @return boolean
 */
public boolean isAction() {
	return action;
}


/**
 * Comment
 */
public void logfileField_KeyReleased(java.awt.event.KeyEvent keyEvent) {
	getApplyButton().setEnabled(true);
	bChanged = true;
	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		ServerDetailDialog aServerDetailDialog;
		aServerDetailDialog = new ServerDetailDialog();
		aServerDetailDialog.setModal(true);
		aServerDetailDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aServerDetailDialog.show();
		java.awt.Insets insets = aServerDetailDialog.getInsets();
		aServerDetailDialog.setSize(aServerDetailDialog.getWidth() + insets.left + insets.right, aServerDetailDialog.getHeight() + insets.top + insets.bottom);
		aServerDetailDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
public void modify() {
	AddNewServiceDialog dialog = new AddNewServiceDialog(this);
	int srow = getServiceConfigTable().getSelectedRow();
	VCServiceInfo serviceInfo = (VCServiceInfo)((ServiceConfigTableModel)getServiceConfigTable().getModel()).getValueAt(srow);
	
	dialog.initModify(serviceInfo);
	dialog.setLocationRelativeTo(this);
	dialog.setTitle("Modify");
	dialog.setVisible(true);

	if (dialog.isAction()) {		
		serviceInfo = dialog.getServiceInfo();
		do_changes(serviceInfo, ManageConstants.SERVICE_MODIFIER_MODIFY);
	}

	dialog = null;
	
	return;
}


/**
 * Comment
 */
public void modifyServiceButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	modify();
}


/**
 * Insert the method's description here.
 * Creation date: (12/2/2003 3:34:15 PM)
 */
public void openLog() {}


/**
 * Comment
 */
public void openLogButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	openLog();
	return;
}


/**
 * Comment
 */
public void serverDetailDialog_Initialize() {
	getServiceConfigTable().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
	return;
}


/**
 * Comment
 */
public void serverDetailDialog_WindowClosed(java.awt.event.WindowEvent windowEvent) {
	closeDetail();
	return;
}


/**
 * Comment
 */
public void serverDetailDialog_WindowClosing(java.awt.event.WindowEvent windowEvent) {
	closeDetail();
	return;
}


/**
 * Comment
 */
public void serviceConfigTable_MouseClicked(java.awt.event.MouseEvent mouseEvent) {
	if (mouseEvent.getClickCount() == 1) {
		updateButtons();
	} else if (mouseEvent.getClickCount() == 2) {
		modify();
	}
		
	return;
}


/**
 * Comment
 */
public void serviceStatusTable_MouseClicked(java.awt.event.MouseEvent mouseEvent) {
	updateButtons();
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 3:39:55 PM)
 * @param server cbit.vcell.messaging.admin.VCServer
 */
public void setServer(VCServerInfo serverInfo0) {
	serverInfo = serverInfo0;
	setTitle(serverInfo.getServerType() + " -- " + serverInfo.getHostName());
	getLogfileField().setText(serverInfo.getLogfile());
	getArcDirField().setText(serverInfo.getArchiveDir());

	getServiceConfigTable().setModel(new ServiceConfigTableModel(serverInfo.isServerManager()));
	((ServiceConfigTableModel)getServiceConfigTable().getModel()).setData(serverInfo.getServiceConfigList());
}


/**
 * Insert the method's description here.
 * Creation date: (8/21/2003 10:17:23 AM)
 */
public void updateButtons() {
	if (getDetailTabbedPane().getSelectedComponent() == getConfigPage()) {
		getAddNewButton().setEnabled(true);
		int srow = getServiceConfigTable().getSelectedRow();
		
		if (srow >= 0) {
			getModifyServiceButton().setEnabled(true);
			getDeleteServiceButton().setEnabled(true);
		} else {
			getModifyServiceButton().setEnabled(false);
			getDeleteServiceButton().setEnabled(false);		
		}
	
	} else {
		getAddNewButton().setEnabled(false);
		getModifyServiceButton().setEnabled(false);
		getDeleteServiceButton().setEnabled(false);

	}

	if (changeList.size() > 0) {
		getApplyButton().setEnabled(true);
		getBrowseButton().setEnabled(true);
	} else {
		getBrowseButton().setEnabled(false);
		getApplyButton().setEnabled(false);
	}	
	
	return;
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GACFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D457152CA5296D5A46EC49CAD23BCBD35335A9CDEDCB925A4FFEF53B6956B6B4254424EECA3AA6E1931AB5095D180DEDED7378B10AA1C6639FC604D17E9111FF81C17E8698F0788990A411849EB38F989DE64619471FE25CF36FFD674D1BB7EF86033AFE5F714DBD776F5CF34EBD775C734E035B179E32ADAEFE9F4745ED6324FF2762B8EE79F90E4B5ABFFDBF62AAF32212B8453F23GFBB841
	1488CF8354ED7D3B28245D5C4F5AF7C0F9904AA2148F426F3D5CAF5B13BF029FBE66097910637E4B650871BF4F6753F01E6D347C507EE4F86E8298869CF9066610799F5B1FA26183A5BCC2DCBC47AD49A5ED7E30BFD542ED02B6BBGE7G984CE56DC5F8B282EE2B2E145A8D5E39125B7CCBE77C5BE49C4518886B243EDB24270E7BF3DCC89E196B5D8349D21FA2A80F831045A7F0061D25702C693EB8F646B4E2918B27CC0255DAB8A138DC7C1845B6D6481BA7AC366A6ADE41B9A5B8750248DB2CFA8BEF350FF1
	2979E45C0F8E6C2063G7FE04C1594B7DEC7669A065FB3GCB7391FF321E60B7426F4463D1C92947F1FD35B9B5927D83D79E665615CCA43E63E3FDE547593A860F3B576730BD9DFF084A626769046741D0379AA092E0B3403E022824B20043D4879ECB4B07E7430858E41F18305B9A9C0EC62B5959E1B78BD678DDDD8DB5922EC7B0591DE60EE3F2CEB9D020C10FBFE07D32BFF3BF46131525A873501E8738689F7CE37CF68D1EC42F3C92EFACE03CB09738F942669E72B9F7E00127CEFA4EFD0ED34E3D7676A793
	36E94C9D63FAB2A1D89AA7D4B1F7F48111F5B4550188789DA7698743BFCF713A93EEBC53CF9B4513F29154851DC05D9858053498936F65F697AF5364C370CF1744EFB941E858DD2826E532050CDDD34866BCA45372C50A1FD16099AD65D4CFC9B98C6A82CEC6A5B17D0ABCCA762B3188768FC0GC0B0C0B840BA0058A254310369A72FD3473AF92758B8E9311A85A7477AC6269F0027B0A3BA79B61BCB64EDA6211FF7DA78912B606224B67F11CEF8B062516F77D477CDB0C72BE0931C3CE8315BE0F4FEC2908527
	0B35F9A1FD8FE96394E8ED1F85F63948CFB8B89C7715743D703472AE718307199785F78BD67FFABA1149945F8FA342G920F76F0FFBDC6783523B8AAC987D0D40CFCF06A48F8E78527CD30D6D79B6D760B130EBE3292AE201870F90A5A8EBD7C9EACC69E7FA903609E01F234BC4E37F46FAA4651DBCC04FC5EB90BFB6C59BAE213B795234E3CBC00BA23FF7BBBDC4193F7E96A4C0078E4426E9226AB39A56A3D33110EC968E2E531EE60B36EE3DFC068543695DF3AE9F3A4E86098FF4732082C3AE13DD600A5A548
	63981D0E69ECA36F92D0CB3B1C20E002594DF1C6671516A2091F5BA45A895F5F82A06752BB348D4765G6C87A0E76FC9F90DCBE95F95B0EF8440E600545228A4BD0095A088E0B5C08CC0F29958ED0041B21441697AB58AB97A1BFB17E2EEFFFC4AD115CF5EA47E4FFFAE5868DAD0CEB7E3ADFBB564FD2B5742F467292304D6E1CA3009FA41A1584C7043A555BDF3142CC932E723F6394F8B946FG7B57848647A563DFBACA564C0B2253B2B2A98A3D02A8029B622EFF0D4E651A9CF138CDA74E3575A86123FD9A6C
	1F5CBE6E98319DA63B4DA6D0A5163BC8758999E4BF88D6E18208EE32CF5AC406CF4AA07B7F22B0AB594E37B35E64086D33CE92BB4CFC27D5BC111543E919002DC1CCB9373E0C5835AF9DC8D170A890F044878C05671AB234D15399E823B2AFFF0BDBB3640857BAE3036F3BA3DED74E6CC5D9395BC6B11A7E660376444C9DEE3C0B51FA49CE78206146C9F6A1A41B0C5587F30C83841423DD8848A475E8FBA50350E437DA99BBE5BECFE46DA6F291AECD8AF0E491A3FFD6661D18752E2A2EDF62A32B1F4BA2FC34AA
	4E07FD95040F91AD689F8783BD2BG5615B3DE46CA3CE06DEEEB45F643D01E81882EE06D56D7A85B3599301D6ED4D4D265A9F73B01D34AF6FF155B05GBEDC51EE3DC73B6F35E13B5C4A28247ACAF73B61CAE5BB219D5B2D827CDA0058CA56EE2BC73B3433584ED8454EC4F395EB97D225EC770C5CEEA360932B2312B22BD9BBF335325DFDC6EC978A78C7GE225F61927156D46BB301D83700B81D61EE66D22BD5A65F5E23B549A304DB56EF6FDB54AF6CF4B6D02EAA45D171E0FC86D18CE76DCA23AC56F9E9778
	A9FE2650B58DFB3D3A3A9DD5D33AE33CCE7D8432EFB65560DD620EC3C4DFF3EB016FG8335382FFE73B65EA502AE2E64D6FC7E0984DFF7A5C7AD23A528563D2FB67FF6BA016D0797F3481C6B20AE269677426BD9CAFF264165321BAC54151274781B594AFD47F05F4E5E894FD10B53A53601E11CC11D7FDEB6211778DA92DA72BDDF23BEA9614F961837688C689C35A9A3D9CA1BB28E78A000F000880084BA66E0ABB9C3CC633CEDCCA814F8BCD0290327196EF858DBA302391B3789C3CD34D1B3B1592E5C3CFE46
	3BFB7756CB63C7BCF0AFD7F36D13F82DFB5862F74663E34EB01EA55739F9E7FE62697899G360E87DAF19D8350E6A9C0EEBD415F2F5B2798EBAD60B7816C2B47B56CB714401327462510BD3A8567285DB9A118F30916947253E2A1BDEB8A9BE8B5DDC49E6A546539EA525F61304EB6CE0A225DB654209A4468FB1453F97DA887278169F7GEDAB6B11FE4BE9B57D9BG1F580077692FFCF21A4CEFB6F78A538BA3004D1F5D06732740585D8DB8FFE22D567C0B65792B5B9B61B9E1B7DBC6E709353718049B6243D3
	B29D4BE10EF5B29DE5E73468C8EEC4BA5244C62AF7D6019C62B7CE47175B118E9D4CB15008F44CE4EA695352C65427575A373B75C9529157E5C21B095C2D2CB703365633C85B8A18B7DA1E7B01334457851BE636CF9732C713556E829B275664F33412E9B223F178FC9DC74ECC75F9BDF44EE39CB5010A015C3A345308746DEB0ACAB2B6A16F7499DA3C8BECC27AAF99EFECAF8E9B48DE98F15A27DD8BE4B0237FB1197EE020AD3289ED624365CA1B38C38FEB83F0G846AF18D659DA768FE18F48905747FA62B45
	F4511B7E8ED20764CB747F44DCAB49GB42449EE9B350CD177E428C3B910FA896E11280AC8EB3833C35EC7C05BBABD4A2024D8A90364E6B8D781861A117EAD9D399C75C38B81FAE0BB41754C0BF8F035143AD37EE699B9DB6CE323962B50E2912C6621330A4EEA0231B7104BE86D69C4DA2DC0470AE624750592ADDA231BF14FE77713D8A26FB469AD4E4FB42D4423B73BF05ECDE44E9664418445DBEC96913600653210AB2DF3D90FCEFB0F34A91A76337B56D69A8B9BEE510EFB0992FE3174DC8ECF96A37A61FB
	E46ED5AD6AB8563FD17C1A96F59C6B2EC398470A043A32D60CEB4C665158A41443GA2GE2G928D600B99503F284AA17C739BB7EA330960FE7056DE0F8B9046EFF292182EC865D7F3881F681D0543FB67DFDA54B141FA03A7CF86D5E556AF29D55D2FD0D58ED075E3B2BA3E6388276963391D9128E77B794F6FA977F3300179402B61998940FC41FB32B05E9CEBC0DFB00E5EDDF4ED605383F437A14FFF1BCF7C50F1A88782AC87888708865882105A0EFC3F1ADBBB9F5F292676591BGE700FBCA137ABEA973A9
	4C0BCF57475F1FFA75CBD315EB5A35787BCE2F8F9F1A99DC19378F9DD17316780FFDB066F8BB7276995D8E091E00F674272F36A87D69556FDC0DA7F7B5ADFFBADC22EFFD3B5BA7DCEEFABD012DEF4630B03E5C6E55CF4D1714331E7D58FAABAB556BAD02F6FDG63E7F13DA12771DE91F096E3CC85ED8B23335A2B1F1A4EF0951D0C0653354873C819068D8B246165F9E958E1542261629924C1E7C49A025A97C6433DDE7D54F6E2C013066F5218857537E59ABA4EAE0C0673DE7D54FCD8E9546A375B383079523C
	7A2967DB233966AB99487775721A57AD1006E76725E1F707167DF838391C1B777DDF214B6E94C5627A00E169055FACCA03FBEDD529F22F65F6C8FB8D1E639D3857DEA9C77986F4205D3E6D94315B61D00E84086DC09EAC6BD8988F6E736A276681572945032C5CD4CE7B0C92E6C46A4830F59E495732A9A9B0E6C1A72E53D20C6B54F762FA86BA97361ED12FFE6A754CF5EA69D5DAA95290A453105C35B09A5E736A275E4F91AA9A583B1D3D87898DB1D03723ABAA0961AED2DCAA144BE45C0BA984B7GE52B0CAB
	BDC4F081D08E6EC27AFF729E0947064A65DF50B2B943D8792B07EE563B9384B217A7AF42EE7A9C4A77B44C97D91467BB167E05BC6DD87A9DF95A31743B7276B0BC0C21FC530875DF4B739FEB3FAFCFAB56FE04527D0B836F5031DDAE41C51DA3BDAF72D25D8BA2F9DF6195F36F64A784E94DFB391256A429CD33E8EFAA66F0AF3ECA7DE40D3DC8BCC5AB536F1243285F91DDE8CB5EC8A536A4319B76A1C0D9B76A45BFE779FB7FEE6C763E8FC86F0C29CFB95C2D767B2FD13C23DB6D77EF4FC63FBFG6AE23A5137
	FCA65F37BCE373356579F2BE09D93A6C13CE9218701459EB548631BA37975AA68A93043D52FB815ADF641DE302174C379DCF116BFC755FF55C17DEB0597D37076C481D05496565A3A897437BA817BF26933E84C0B998E0557BA89763B1AD392CFD1F49A39A1E1B5E77148D73696FAFA0F750441E28A49DC0A5C0B7002387797D7D63735E211AE7B46FD0ECED5FB5A857C66E51127F1C2D367D64FD44D218F7C58F5AFEC7A52EFFF58F2EFFD5B5D97FE6A8A77742DD2F9757FF5FE92D75577432B5F743538CB0536B
	6681234F5426240F1C5DEC3C7ADA2D7116C87DC36099566B49CFB65E41F665F89572F80EB31A721146080667A64D719E75986F14BCDED82656F809FDEC0C14BE6FFD48468BBC2B9C2FD246379A1578AAF91E008C2DF98A24716B61595727C577DB9D4A71CAB834C7E34DBE6E2F3DA6275DEA156E2FCC7E3125684F0D77217C634B097CC3209C8E905907725FDCCE680C55D8B7012D9AF8E6FF7EC4FD265D9FB07B93A76363AB89BED501E7F6696FCFE15ED7AE54AD79GE3AA0FF56CD42C3738985770C1BE6A7026
	8FF08DDFBAC95F3574C7A555838C74639AEC457EEC2B835A8576EB717A0ECE0F39CBD0F699257E468B0631C2B547EB6A72982F8871798749B9E496C679C92BC8A226C217C3CA0DA1BC6EA7EBEB26BE89614F1AFE0CE3FDEF1A5CF1C71DBC383EE33D8E41442DBBC75AAEBA08B6209E4AFDGA2C090C058B964EB5CA1B246045D4CDB65B9D28EE14CA2F5G5B359FA27A6694DC164B12AF4D5A0E48ED8B643625A5647D2974D68C0C36C84FF322F80CEF278E2B79D6B36066D37700AFDDF29DA1F2B58FB01D59A663
	BF257899851E695236A3981FDB82F51B8650DFFBA88B7D3A3E8FD1B72EE4937FACC0AE5FC64BC5729D776DEC7425C81B1B69D36D62645CBBF98E2DFC181BB3472BC7543C8FD62CA7D4E3ED0C47020E468FBFE4BC5EAE63AFD17CFA851E717EF5FA0E1272A6286B7B88F9EFBAC6F89F8E6535B2EE6DB11447BA9997CBF1DB201CFCDEBEAB8BFC7BA6BFAC5076CDFED2206DEB7E2C403F2F792B82DFBE0564BBE44BB10873E8F74CF9E44D83D0F6GACBE0F3AB3D92966F948F9B71F434E7B62F966E9B25E5A73EA1E
	97D17C0673EA1EBFD40DBC5F82F50303484B55B4EE34864A11B26EE88D72BCDA46B5D09C77F1D4523E0F9177C50A4B0532DE461D23B120EEA88F4B3870DA9C4FAA6356D35CD2A82F10F11F27311BF0A82F15F19B4F487217F149F5D47ED0CE7E84F10FE6623CBA99D7A46F658299370C46A93A21BCAC6336E648744938D4BA5ED2A82F7884E57512EC53438197710927ECB6FC62CBD6D3745C5C7C095A7F8E207844A1357F3D239C7D6FFDD0B7B304B43DD5C1E80A0672A69957D30134F343C8679B44D51176740E
	CA776FBBCF5D6CBB1F378D397F7FE10E0F7255FBA6F3584D7B4AE1DFF208ABA2FCB58EB3FEA74978DD94BF2E40B3B9FC057A8A249C88F5314348735B0A1167C69EF177D11C834A01B22E3A945B854B380FCA315DEADE4EF5B8C8F27594AE19746A930EC712B5AB3A8A75DDC31DDDFD06364E56212E4E21A6E3D7EF33145F9CB22D4C272F5C39C8B3770AC0A44F7892A3BD376E3FA70E592B15325F10BA8276F8043E93CFD13E9FA3F4BE914F62A52CFE54231E5843DECED99F6CD1CFE8FDBAD1D97FCB0FFAC2F32B
	541F2D6B51AC0C55EFFDF495E7FDB05067F7824BC758FA56C84F25B1F7472D4A391240F8FB7290B92BC56724404A33F2194923135E3F37C05FE4934A68D729A837A2936E29D9FA9F6C0372380C8B24B8721E195C1F986E2DA37E741F74D76346B5F07E75BF536F9CDA7E41366B9C1FC0300972A0CB6727CD20C4B3636903B944E7E5AF153B79B18163E6BB5388FFB6C05B4DB2EF3F190E3CCDB1A34FAED2BFC18F65C1GD146CFD3BC4901A3F9E1A4570C617F1AFE23B6064C33CB1A93F122741B4DF1C0F723F3A0
	4F1572582E406D65AE9F233A880EC91019FC1F13C12E685CFAB36153FF9ECB71B3B66B7F46B1B21EE0A3CE37D972398FE460DE5689482FD733557632CCF02F5BA0F86A5F20006F0B57D31F45A128FD0C980ADFAC28FD0C8BF968E304C0DD62A84AFC890DA50CC3F9CE469D2BC6BD8899C5BA73296F928165980044B144D7D0FC25143FD8AF6B416A6A6B1191994BD7DD042A0E4C6939571E382EB97C6DAF75FE5C2E2A5B4BBDFE5AD38FDA4A5DFAD0D9B33F9E8C54F86B013D866FDE19E3F84EFC3D0658D0167CC2
	C2CA3C4D8CE5B6270504CB24711E4D7424676B275C747C3AF6FEFADE297526674DB32817E5B2BD7FF446F31E5FD43A67C9281BFF1E0CBA6FF92A6BF09E834C43465DDC659E37B5EB7EF19F487024EB70343B7F5E4C797B17E6FA53551E09F48D0E619EC8AC233EG21D346FDCCF1AB219CA9639EAE433D92A36F0939F2C2636EF1AC5FCE4BA4AF1615F7155F087DA2637A2BE7F3645C509C735DE399AF0FD5C8B1880144A0EA4689BF2AAA687D9BFE33D8062582E592G38A000B0D28730F99CF9F809460276D922
	12F2AD08EBBCA97BF196645B76A2127BBD8365A5GABG42653AFD34AE5242720AB7D9D85EB02B3B7B6400544F57FA42253AA5FE5AE8418AF97E6F53B97C71AD091B5F9E7912A7536F6545736B77EA6A6B8AE68BFD0D4A747B71E2546F5D97221258D86F177A9B0B757BDC29EC27AEE02ECB9235DDE5D06E3E0077FB7E44F1F876739724A8F8B34CEEF752B0B8A03B1DF6076094E79916E664CBBA7AD64A423ED97C576D68FF067D68FEAE7CF63D4FDC914785496F141E394F5D9DC71EECADAEEAAF57C0DDB4GF7
	917C9E0082G7DC5DC5B4A9304C6924D87729BC411B70D532CEB8C7277DB04E9B71A8471189C044364BB8EABBFEB1F94E16FDFA47C1CBB0CBC9F003283A098A074A24B11C9365273BBF5FE995F16CA78475ED495CB389F9CA57A2FD765DF333A973388ADCD824969C29ECE8B484354EFFF03CBCD2F0F5F630307FDD6463BG2BBB5F46CD4B51B46FF5D626619A4220CF8440E6009D93AC6F137930BFCE5779D927EEC2694346DB08FC9B5C3956927E790F086F5128487BE4F87BE112BF5F641DF7AB2D7D57671577
	325D77D863B1162D2623CC2223FE423DF6B6465A0F15F7A05D71AE4DBB50160FAF470BD25FE04598DBDCBA698EF4A2035803D15E6AA231D949B74FC1FB98B10176708B39E88FB749380D94374F86F65306F66877544FAB033281E0D8463F48704DACF71564D0CB6FDF721467846963CBA6E5D25D301FB6DA36EB8F77850FB92E479F5BFE1D633BF5649B155EFA78A3B9B7CB34B1BDDC824F909BDBBF53432AEA5443F00DF5C55814FA38E50E7030C111F3AF65AFCF91FD6B704E05C71F05FE4F21774ED1176AEF2F
	713C879FB923548906FB3F4E9B47E7FA638E17116F77264060DACC3C153DBCEA641D5DF62B45B4AB59758A6A3310FC658DB6747744B2E293476DCE4BE53BCD746E09E742D25A370168319DF978D8190E73AD1FE13B12075772717BB04FDCE72C6F223E2E743D3E205C27C5D70C71899AFB0C4AD71AA3502E0E1F5CD101E7DF189D63A7BF2ED047CF6E0CD346BFDE29D047CFC8FFF77D51D36A7809E77F5ED36A78897617ECE60E372E5EA3677CEC84BA939DF08E8154BBD86EB3637307A77D71F9402164F330111E
	91C6FEC43016C9383B12E80CCD112FAD7944A96E3665D25B2B57284DF567D8F3ACB75BD024DC1BD522EF2983F33763CA346AD7B8D04FDEAC765659FBCAD17F253C009A52D70E61B317C10A6F6F2FA1634BB95A74A5DBAFB4340D31B37D51BAD29F8E732D83588210FCA9AAC9F7894F733BEB0EF174BC57B84B1B4DE382467564EF86EBFD3F3B78DC6D7C5F50AF29A5711791BBEC7789B2BE627F015E43C43B03A299EED9ADFDD7E7999B97D9D34C1F2171F82BB0CA516CBE3F62B03EEF2D01350917701D62730524
	0F7C9E196441D7D7B75A497714C626F0ECDFECBC0C7BA274923E87F90F663644C0F9AB40BEA766664EE463D9977A4DFB39604FE9FF6B36216FEAFC01D34AABF03A4F3AC05B5371EC1C8B9715BE87297395B7DCA275DF657474B9180C9FECA07BE09354A53B22120AG8CG03GA240E20090974A7B449972DD03AF5F14B1064A5C49DBECA2636DF09A72F6B50C3359053CADAD714801E63CED315A27A54E62BB2615E9723BD591F97BAA9D538C65B900A0917743D369E46CDE68BEAE2F6F85725910A4FB6719ACDF
	740346368399DDA397841358470F497D7F56C4ECC9A84C3196E0A3C062A47836GC5134817077D7205AD484D97492F4BC19B251FE47E6BA0F9CE327CC4F62F884945B60BA7FD65DAA464917E4085254FA2DA85F60FDF564C49757CB137FFDB02F18B93611FA29B220461378D3B5B16CA312EE7ABD1FE4B0106F51318B713DF05F2C91EC239582BE8FC934A03G629436652BB1BFE149944A6A6EEA252C982E230C6414A95F8FA8E8AC6CEF6E69EBEBEAB08E75B675F4990D0D8DBDC38D46F38D9F764A7D7F252C54
	CFFFC3D7CF5BC7DD1DFDDAA3C8B9D2E5CA5F1E49DF5FC2638DD3A86B3F1CB966C75664DB83E6C151DF8B2A782C77B246771F66A35F57435C3B27F15F7CFAC8E35F34BAADE6553E59F9DC4E6B1A163F3FAD24F9DDD08E86D8B50D3218BAC95AF55A1DD496D2CE5F280F1CBEFA3231B92E95619CEB27D15615A50863E610E70F5779367BCF554DEF771F2BE37770B121EF56A11DEB31F5C4D67651D117A01FAFAF556D16F1ED2EEE3B4BC2CEB469FD1DD73E4575795B618B6C77474F5C0F451C3FCC4F0D4A99F52E50
	B20A574F28F305F42928A75DD097B60331058B2DC4FEA93360B3G98814CG01GA133A817C57564DBC73FF9F7FDF6079184E7D5248730719FB1107143E02CC8004DG491741B7G283C0C63C754177A59ABBDF6BB4D08A149B1E09715C707A44FB58D3E7566678D736BCDE403363F70E403373F703B86EDFF611986253F40F04FCB63BA6584D9065F58C04EE0660BF670B6FECC710D51E85A4274C8DCE03F6FF874B3DE1E7F1E4974EC0C662C8CDFD66B1F03622D17557A77A779FC1F013AE8D9765B753EE537CB
	BF3F6C8E6A35E5F7C46FAD3BE3FAED59656A3DFD3D619C24377ECAD4524C9534DBABF35007DBF5856D651334EDAC1439B9B87367306D3F67220DAB1A433BFF2CD7FE4B754969FF97586FE415325F785C754A77B9FAC64F4D29653B1162974F2965BBD9057C8A023AAD72FAC72B15738FDE855F652AFF9A18CC8CAD44EE3BBC4E4835DEF98E574703A797586F1D634AFE0173509DFC955F93A650BC09502BEA5EA5D3FC78D5B56F6EAAC45EBD82F5391F4A6F530BB473FEBF1572FEBF752607752BAD516A97A635DF
	6B33DF9079D4C67A7DB5456F6C54DB1F43D2F7CB67E0BA65EA75EDEBAE37EA67448534EA67445D5E6A7F7B0AAF377A7EDBC6F735FA7FAD23DF99FC53364EB03F9DFC5A20ED871FB3F85B41D803369DFC59203403AC5E79883DD7C5FF4A7CCAA62B09F437EECCA9FE3F11F66B75A46506F39D661FE3B4E701E770827BFD67B377E37624BD176651FD2A36B33D944FDDD35B19E879CCCB06BA47B534B30F66AA67DF877858EB7EED9D530D16AA548D4CFB58437D214A2D8F7F2378FD256A564BAD68164E4156FC155E
	D13782FF9ECFC45CA20A5B97901554AA374B26772AA2405587603B0332C21AB3856571G64FD52C9B70F5ED57C7EEA612D67D758AD1D032D792B746EB7876BDDA2730113738503E5DCDF8936DBAD639EAC41F6EB83708E7A3BB4E23BF82BE54CC6620872DA3AF35D3C7BD0717B591CDB4F473F5C52B998AF1E4DC53D2BCAC05C8B3928F7C76271BE4E53EF5524D86DB90BD99CE7785419375DF803C0ECBF0771105A63A4F6C1DE6931778D3DF2CCE0234C773305E42DA63BF5F24266426F998A315DD6395D770BBC
	F3C51C6DC496ACCEF5506F3BF4567E2E336E7691A5733F173DF01C581E9239BCDB08E576F721370D4C41B374A5EE1B48ED232FB3C5EEFB03592C6B9426153F5EE42F126087FBE784BF584BCC115B455EF66A585F55F281023DC319E41F1BGE20751BE26EB31C0BFEECF0353B4EE1992F4FA0BD3B041B5F61645E1732EA95F2BEC394D64B3AF35FE91333DFD0B5CF1D8F6179EB4539CEC4007AE427BD328093CD372952B6C1D602DD61137DEEC3019C95C32F1D6945CBE8AEFA6633B300DD4496A464D046E8B0ABE
	7F87D0CB8788ADF15DA1089DGG44DBGGD0CB818294G94G88G88GACFBB0B6ADF15DA1089DGG44DBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG429DGGGG
**end of data**/
}
}