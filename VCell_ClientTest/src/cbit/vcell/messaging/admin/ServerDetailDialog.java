package cbit.vcell.messaging.admin;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cbit.vcell.client.test.ServerManageConsole;
import cbit.vcell.messaging.ManageConstants;
import cbit.vcell.messaging.VCServerInfo;
import cbit.vcell.messaging.VCServiceInfo;

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
	D0CB838494G88G88G3A0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD814E536D4D634D1E2E222111A79C3BAEBD818D4D618E6D4D4365B6EE5EF6CDEF72F157BDC37E51FF5F7592E5D7DC4CC94CA8CB2C054C1A1B19091D00148ABFFBA9AA9AB5C490A4201790451E1E61A7978D32CFB5E1F734DB75FFCB3986976BC27EF5E737E1D771C731E773C67BDA8E46E1832EECDD226A02CD9A7707F8E2D9104302F846142616B47A3AE58E6CC95947F6581EC969A3FC9066F94
	28EBFD46181AAECCEFCC01F2A414E3E30C299F406F8CE1D6E35A17704347BC4B3F900458FB7E7CBB7F73FC1A0D732C27659369E970FD97A08AF0641BECA773F724E7F1FCBC47A32CC90239E81B4B69399CE70072D900A9G11F656BE963E9B684CB7D6D772F6EDE34205D54FBA135EA163A846C4A8F6303E4D7C2B975EDC1368A0F34D20F412BE01D00E86A00BDFAB98B61643372679C257614E8E0BD45257A9DA2DA5BD224BE56A32583ACACC669E0B2D3A3AC5F476094E86D1B2D92C8D9613555EA5C85F12F15F
	C8CF21634098D0961C84373A0A4C95856525GABE47C22EA02EF005FEDG12935777496E9ACE7F19CB7305AC57D39BB679D8DF10132DAB5269DE1F3528E3EDB615452DB9046789D01769B2269682B481D88102G36D29D38A37BA37856F6C875761E9E3B2D566128331A1D1F594D2295FED7D7C38D471D96BB6DCE33A0B0B96B520BB56871872C5FA3BF389F63C9F8B94ADCF7FA0E907F601DC96BB5F8929FFEA9E91A0B71E2014B4D93B6771FFD4E9D6F7254C94F39BF9714F38B3FAFDB3FCEE36E5CDF162DCF60
	63A4CB6E39ABA4A2EB8715F5BE7CDE43750361AFD27C00824F74B30962C9B9906AD6CA289BE75EC5DA1A12278A697B47EB72A17449202466DEC6C3F72F1A168D35E46C10DEB6E736CC4BF60ADF2A40B3DA1E213410F2DA1FB1B58E0069D7C8AE592F537AE17FG4481A4G248F98D30B810E8C200E3D123D6F8AF52C5964146AFAADD6336894D85F101CAD709587A42769384DA519EC1D2251643418BA2C22CB60ED6E4EA1BC687068B7156AFEA74CF1CC3409CE13E4315BE0F4D30FA809CE97EBB3BBE7B3E96394
	E9ED2B85F639E46AF188B86E03B99970351ADC527FB94CA6C9F437E0750F66901974190CB0A28C40F934D9D8380D706BAC2CFFGA0D06643412DE43CCFC527CD34D6D7B7596D97FA9D2DE4A5C25CG6173464D44F604416F48816471E35B89AEF75018DAB1086374ECFDCFB1CE03251310EFF28E629E6B2BA6B659B008BA734699541906F7FEAA947FE21C264E84037EF68FB2DD8998D46F1DGBAA6210B15D32A4F7C40FD6C8B8C035EFB0F4D11F75566D82B01E37CAD5293D905838D0B81164BBC8E5F2AE7BADB
	E7F2092825271C20E022594DF1C667265AD24E4FF51CF642F7F2D812F3E993EDA3883B815E87A0E76FBEF90D79346FEA18B77FA258E7G89A08CE0B140CA004CCB46549AGB3C090C064A514410B55518AB97A1B7BDD455C7E7834DBD55EF71578BF7C391074F134C2CED7E3AD999A723E56EBE13AB3A91750AA7609B629C1F408B6B37CF0713A6939E4CD5C1E1D334BFDE6D33C836CDFBD989C170C1F1BCB56EC12A4272523D792DBC4C982B744DD7FB01D4B555B61F01BCE1CABBA1770515E8F76CFEE7FD4AE31
	9D1DF61BCD24CAACF76175CFD35BA5DA459EA03A5E5EEB13987E79EDE47FDF9087396DFCE95B5B8231FD56DEE207196F94E1A232F2B8ADBD30B508A997CA0708DD4B5B1C2560D1BC68AF71811321AEEB88EDD47FAE34D179976F93225B9DC9DAE7EC687451C983C34CDE848D39ED942369496D5A3447D09B4A71AEC66B37F642878DB70E5B85F39E99CB87F3C483448D21DDE858C16A5176F203D0EF37DA99BBE5BE6F57279339085F760AF0E491A37F294C3BCA7DFB2ABAA367A32BBF22A7FC34AA4E876BE54247
	3335689FA7GBDA900AB3E4CF8F964B26385EB17DD076D22203C94A00137AB7DCE596E57F23B814087FF67EE976151EECCBD36CB84FC7A770A713ED736EBEC40F681008FD5340B72E877F739DDCA00B1D58FF0A40035EB8DD036FB20915BCD8BE0E762825EAE5123DD6FD1EC57F09D7852GC35731F68B2ED3362BBA066D122FB72666829C391E359B3ADE59EE355CAE86704BG049BD83B0A9B146D9EB3E0BB8970C1B7385B69BC5AFDFB9C5B2D6475492358B7F794EB47F4727437C437685D633C294FB4D0626A
	073DDEDDFD82D5135FB1EE25FE8259B706D1F8175815C974B5G4A21G9123F0DF3D748E5EA5C2AE048B713F5F3F5E57DDA9065332F214FBDF2DCA28FD1B6D07B9BB491C45B742D9F4A36E05C775CAFF26566532F7DA28AB457578FC1EF25FB11CBDEFA3FC4FD91CAE69B898468154795EBCC2AF7135B81A7B1E37D01F14702799668D84E0B6652FBB14B6A59A70AB8152EF02F394E0AD9DB321161CA11D5DA6DB17D842F9FC26D28FDFB35D713037BAC4F33349A6365753C60D44E43B8A7798996F26E69C616347
	4E19AA54FC7FCD1256BDACF0D3F7D24DCD0CE7661B5C3C8B995B31B69C002D232E8E579181EDD6G845CCC700737E6AA4652834EGE03D9957F0DFDD99FCCD54381430CF3368BCE7F7760866C222A5A526FE29041EB5A535341AAEE28F6A544521EA525F61308E5675CA125D56DE2B9A244977A89F6FB1229C5E2EC77AE300B6A19069FF6B001A7ECA28EB8B447B7458EFBEA6731B4DA7457E11914066FFC41E5F81E3074B73079E521AFF19BCFF7509BA7876584D16F303445ADBBA459F4587DB8352318A66A83E
	85690829542243FC8B5231DD2A23FAE79549A17E6369585908F48C409C91B29DE9BB3474E945AD28CF37B42EF76B93579157C5C2DBA739DBD92F86ED2F48342D06F9ABFE02F3579DA53EAE58B4333D3F04FD6A2DF69758B835A6FFCEAB19A6B39A4FFE753F82B9B35567F57B679E632889D48C645625C9C711BEAB50B66DA7483B38EDDA3C0B1769DFF76C476D4533863297BA1C76FE5788994C686FB80674A7GED053722CD7C34D4E9934F82FE9AC08CC07C2D3806E70E6D257B2157A516507F57DBAD1D973C69
	7F0C54A1791C7EA74C1FF0990006545BED67ACDD54BDE97FCCB910FA896E11280A70B5949864FD84349587218C1694A9E5E086FC90C0C490529FE4A894289FDA82F09A3693DC4F3C0887D7CB29BB875E2EA03236F71D33D845239651EAEE7FD451D9CDB07686F2992D2F4934AE86BAD64B34DE5A2BC5EB45ED3867BFB412D822495959E0F17E20E9B90FA69E47F98DB0E640ED4803B5A7885EE233C83085AC9745C23E4E7127755AFB64382229115D3702B713D8D854ED5AF12FD80EDF463F2B604BE2C41F3DCF66
	CE39DD9D477A1C6233EED7473176E4E29C2B906A02EE473846A779B4B689653451F0E68354G34818C0DC67F622DBC42BF3FF12363B6895C8F133545638204711B1DEB192E70727C1D04CF744EA2603DF3F42DBAA698BA5A13A7112AB26BF737D73F92D5B9CE550F4928A8E51B206963391D9128E77B797677157BB9E1B47301B303E14F83B0DF30C40F7166A341688B0E23B10D81A887830487A34F6FAAA0FEE8B4146381D681240F813982B483C8E3106F353BBE990E6FD4D3DB6D7500B340BD25DEFD1F1479E4
	289B99FF0DDE7D362B4AA1E334783B2945078F4D8C2E4C5B93D9EA5E927FD187E3C60FC15E76EECD6174440DC1FF7A72D125BF3DE05365A4F2D7537227536FE074155E6176891759DB57337525550F0CAFB9DE7D54FCF15C61590F2DB736CC3D5EC0E82783083E8357DBF1906F95F1F7E00CA9FA04F43EB0AC1D69E3BD7BB19ADEBC04BCAF9C0BB4C88DA32361FA2FFEEA9A4EEA52700FCA24E1C0262130F1E4B49C746A273693911AB4546FC861BA9DA353705B915270C72FFEEABE88A1DA7D269D9D59FC8FF875
	D34F1795223566CD5B116F25A138666EE3A323214F2B1F1A066E90AD7BB13F7100B06C7BDF094B6E14A4627A00E1E9015FACCA03FB2D21C4395782C278DE03EFF4886E359BCAD13EF1A1E837334B085DCE9F87769D6048B864C111E1E4BC28726A27664119F1DABCA8A84C95344FA8F1C0220E8CDB675CFCAD1B6200B1C70D43F53ED5046B8C135733F2046BF9F358753CBCCECB2F9E580BB42C14E918F2FCE4B44C716A275E4F79F7FA76E3EFBBB3E94C26866A4E82B05CBFA8CE02F2100C7BAE0360A2203CD846
	BDCDF1F1D0CE389369EFF91F44E31347E379EBDAA6E798AB6F4B3CDAEFA788E4AECFDE982E7A9C4AF79A660B9C4D779DCB7FACDFBB165E122F9DCB7FA2FFB34343984A17C62CB7657B0F35DB7235E26D5BA85D4FEC59C447F639C497F50E9ACC1209573DAE11778A2F187BC9D30F48571CA1AC295D405BB4CA767A7D826E45BF6FA1FBCCE3AF92CF514A74FB497B285F796351160C7D005812B6A8CBGC1321E146B7D3D3FCF9B6F293B84781BB10D07C70DD77B7D99949FB3DE6D775F25C73FBF8E6AEAC251371C
	DE60DB1E3A82EDF9BED0C0E216AEFB2F1384A6BCE576F0C116DC6776C20FCBE28FE1AFFF97207DA51333CB741279B334BF2B73557FE7853E7402496E7BBA256C481D0549657AAC144B14D0144B0DB904AFF1D0CEGC81900F2D9162BA5977D84A60F8A789AA6F84A067974365D648E5A86F583G41G61GB193105FFD9F8EFB07EA9C503CC331355557AB57C66E515CFF4ED35BFE729E318266DDBD81EDFFF2B92EBFF3A22E3F61GD9FF93144DGC393F17DD58735569FB2112DB99C3E8BG16CEF473005177B7
	8F7A48594D46FB791056F84BF97FC478265D6549CFB65E6C8665F8876571122BB465434728002FC1F33C76C665F80764718CBB3446EB63E3B86E725E07EC3C2C234A71AAE57CAB9E78AAF91E4C6DDA730C62630742D727C97764E34A714A8434C7DD0DBE6E2FAD1DCE3B554A6F2FCC7EC3A5684FC55F05723FF39F11FFA2145343404F89C379871692BA0F04E947A3DAB5704C7E5CC6FD266EB0E6FF564878BBA9DED26019DD5AD106F9DF81D037BC8CE3AA9D060D0A756E5F0FEB78FDBE6A3061EEDCC35EC7742D
	854A21G91F7639A120A7D595698E897FF37962FF79D77183B8CE5B7FF2F3F7192E02C64C9DA637D1B67F82508FFFCB3B9074C62B9D32FD5A291D37114032746909ECFA2EB7B9575C988FF32A6E19CEBEEBF39631EF31A40756DEAF1081DC271E452F64BFBE883C2212D8EA096E0A5C05AE4646B388CB2C60F5DEC324AF368B2B0E6A1496DFE13C1744DA93AAC9739AF4D5A7ED5EEBBCAEEDBDEC65ECF792B9898ED091E67C471985F1EFBDF4D371049EEBE05CF7625CB6B3308DC97CCE6BA33CE463FC771CB95F8
	26CB93BE40785CF228B3CCC1FF6D049E7DBA5D94540DCDF944BF0B134B59349CA85F336F5123AFC55ADCCD1F6ADDC14E3D1367504A0739BAF35C1C2566FD02E2BD497778627D5F693918F58F6371FA993FEEAB4117AA700C7737527624EC00BA5DBD487B3F65925E27CFB5266A27A22EED9B4A23D8468DD1DCB3144DD36533F237EF5F6472EEED5F64065D5A3E66AD3B7D7B1AE3F67B72A9386F10A747A026225DC95CC5569C81659800E5D3D1F7B6142BF91EB8554D6734FBFD71BC0A1E457AFB55BCDFCC711577
	2AF9FE62G723C996AA26FC5DEB65338D156B430675390B76FA0723CC2467D02624EC059AA633653F8CFG1443E45CFFD1DCB81423E4DCD3A50E37D846D9A9EE8514D74B382DD5D47E53C17E539197D0A54BDF46CD2138E6A81BE5DC7B8E1CF7C046AD5E01FDC74938A29A278807F2140C8B5EAE53A763A2A8EE8514D7CFC7D9DDD70232CA1FE1CC4D1F61A91B4A993EE43531147ACDB3547EF7A645374DD07B5FE17B507F36C25D5299C853E49AF728188976EAA662DEABC55A4F4CC4BA4712A31F6F69B0456F02
	322BFD677336A1077F85F33C112F5EB3C3B35D3C8F1E69CB8E636859BFEDA663778699BF15622395F8A6075D94CF4A71D0F7A49CF91EDD0CBC1FA6632AA8AE864A71B26E053D58AEC1463D31975BE55EA767BAECA639FA8A170CBFFD4271C832E6A5D7C94329761327DA8D47CF9EEBBFF5323D3E69D4CBA34FEF1E522F4C272F5CF83DE66E9501427B98DFEA78B7689F9356B0FBD5739E7A8D9254696E23EF626FA95F47881D3FC8E27192D67F26C7BD3107AD02323EB8C3D9CFE87DF532327E9B0FFAC273B15E1F
	2D6B39820C552FFEAEC2304E9B65736F84D67175E44D62317A97A62CE91E507CB6636D228CF2D6CB4EDE11155F4944B213476F687D3B997A1AE7210CCC9F205C82E7611EFA075E87F5D00E16F1D994C75E19497D0961A6FF60CF7FF5333CF5B3DA8367DF7F237C4E21659F2C3B427189A44C22BC506B7D340967B4B31E4E5BCDFCD676285CEC6A92B1EEB6097227F236B135E9B67236BA87F96B180DBC7B87758742209C8990AB63D3297FC0F260C8DE9849B5E378B1B9BF564610F95E65F3A2AE167FE6F34C7A51
	F3A04F1572D82F40E5886F523CCC899C131533495F673CCE54DFA85591BE3936E579991B759F3B0D0CA75A0853ED66BE772CED381787F4482F1B735476B2C867DE77941D277EC56A703D581ACF63C3BA350FB1C0714BF4EA9F63FFF6210F1188F5EDBFC5196BE9ACA19A4AAF4B3807AAD08F92FF0AF4BECEFD147CB946549A0036B908FF166203F97EA249CDE4786683D7A2A3B2162F3AFCD5DD1B224C6630DC519C7E7617FABF2ED755E588DD87BD75E0E329DB8FE28F8E2F87AB8FFA6B415AC3F8779A1A03674C
	01C3440632649792D2B2594CD0E6F36EA5E1CBBEDE5F8ECFFA44B2B7BD1D154353F3C325B7BD93ABD1AF03E4FA4A2ABC6799D76E1EE7FC5570734C2F721E6705AA1CE78A4C43460D2AF00F7B4A0E61472D5B6EC9177E203B7F4C6D4377FFFA3BB7DD3F590EF4C54E41BD90CAE3ACB104CE9977A70A936E87FF7CFE44FD3A8F77CA4D7D287B69250446EE391CC34BA4AF1615276E7BB1760B0C6B2F1E4D71680F1AE338FBAC6365C325BC86A11298C4487D049F4F53358642EF964BF8CB4E1D0A835CCA0034882883
	E80AC09E2EABA0BC34C2B9C0467DF20F6C47C5A05F6E7A0864FEAF0572F20055G69F331AE1C56954EE5F9450639ACEF18556D7D688C6F67EBBDE49C72DD6E270D962C16E9BBCA677047378D4270764817BC19FE17968F2F5F4D5457954D967A0C4A743B2B98753B9B564946FAF52F3F31D83F8FCAE4BBB597F3DD1E27F74DA0A8074F457BFDC1E191FC0D2673BC8A5E88335B1DB48C8E48E6275DA1BA25C106259979DCC7A73FB7323FD9DC389E7D4F48C7E689D1B7B5784C950961F2DD413FD131935610AFDB4B
	A1EAAF339E00B395608C0083E094C0588338B6EBA12111C473017CDAC9B2F5F6532CEB8C729BADE23F9BCD02F8CC8EFFFF1F7C9D0755B4E86F15E06FBFC878193E85F99E816347GA4GA4BF48F2E44C344D4087434B7846DC42BF76D2351F639E4CA37A5F204A3FE6F55F6989AD75A24969C29E760B48435C59778A39BB0EA4ED7641C35D030CF7F18F3A73ED5C344C4B72DEE72C1C83149689F7F100A6003311AC6F1379307F4C517BD967C0245207CD329079563AF32DB97E35AF096FD127487BE4F8BB7DFB40
	FA6F3CDB3E76973FD25E4B52A7DA13B0162D26A30853919A69DEBB9BA366EB659DC8DFF4CA738E1478754524D85EB7C1B1C65B5D959B982D8B3791FBF04EE4F51158AC774D73509E664FC3FB38ED975AC3030C8B20B8AB148366219DF2D27FAF884AD3G22E47CF78C9F44F2D7C98EB5FFFF49D71E93240FAF1984713AA8BFED34EC57E6E11B479CD7620F2D3F42715DBAF2285CDB8FCF16239E464EE3FA389C3E0973587A199EBEDF01FA187E10773A729FD26AE1629061E12DA2671E613F6DA37A7619F7AEBC6F
	B7270054B7F86728737A1CC31E77603915CA1DE038D72B3CF1FF596E0D3B3F047C7DDE9F98DCCB27494A9E0F6ACC4EE63B5552B9486D7A33542E13FC654A07505FCB59C7ECE2375DE939E837C95EBD71CC48277ECEAD59238FA18FBB7669855F7209726061779F615F07F966BAE37DFFD27F1A7F3D3E285C2725171A1256EA6CB1AADFBEC77CC36A78492E7DF876254D477849BF772B63A7E356A8639FB76CD747CFC8FFF77D3C7D6A7809E77F57772B63A7581F5B4CBCEFDDADA9C0DDEDGBA5BGC681044EE739
	4F0C4FFF58630F4F917315FC8EED22E7C41329C334D6F05C388DB456294857666FBED96E3687F85B4B5FD31B6B4E3196D8EEF67CC74A35AD66742D180F395B630A356AD74FC7BD7B2E58DBE7CBCAD07FF9DE40C152D70E61B317014777FFF5100CAF67E853C73696E8E86BE2E77A68EAD21F9E85BEA6C0B30099E0A08A4F732F8F6D9468F92EF116B71A3BC40C6B49FFB3D8697B6D62D86570FFC35FDCC962AF9DF6586EBDE4FC449B69BDCC32BBA812613E2A24EFF5162EEE09B5457C99FABF330A67A81A5D67AB
	3760FBEB882CADB68A5F94DFABA1FD64F7E41287DFDDDDE7A7FFCF5944940E6D0B00AD38AF129F46F710B9F44C9AA83782D89F465C5C018F712C534D18AAACFDD57BEF5D1635DECE9A75B04FABF858FD568594F52CE5631C3F2074B9C81D2F3861F25EBF6591CF1F0349F830066C8383541981828126GC4824482AC83C8FC84657D27CA72F78D3EFCD34698AAF32749E21398EFD7E5A1EFB39F855F65D164EDF919C78EB4636DD12B3D1FF3965F18EA32653755C71137B7E753B7D0A83F8C3072D15C8F369CB2F6
	8BF46F1657B71B2409F3599F2CAA70C5BFE86C89A063D447F931D3EAB5F5497D1FBCCCECC97282E32A9E2081208D40819038G79F2294A9FDF58025CFC61FE1D1C1B923600792F11643B0065A732FBC545AEEC33EC012FDC0B714C17B1F534DAA42B486E71DF9D9164FA53CE37FFDB46F1FF6CA47CD3E4C314713F9DBA6BEEDB4EE3DDFD65A83FD5C0C371E318B773F8B94A457C984AE5ED850DEFC2B992A0D6EE7B178A4CCFD87E984AEA6F81252C986E37A5A4B823FC9FD050D8E2ECBC5DFA3C3E36293D257E74
	2926263A5A536D35CD1F57FE51A2777FF8DF391F7E06D3270FFFF96AE42B56887CBC5D27746D197CD3EAE93C61F1147593D5BB7D481A7C2D813320682F956E7F2177B246F7E381723D94666EFE9C774D0B6D9A7B661853E2D66D1BC985F2DE5763725F5F6E21F9DDD0CEGC8F982E53171A3526E245DC9E541F37A4E79486923A79B1BA3C34E1B55BF01320EADC65C19A710E7632BFD5B7DC955435B7D19556C9E5EA5368EBA7839262BA632321FBB67926573E5EEF53A0CBB6EEA363BAC64C4636FF5DBC676EF81
	B50D305F0DBF389F0BB96F24799A41CF28F3050AA8BE6C89F52ED024FCF605C3DDDAB44696CA6A087C9CD08E82188630GA09EA0B19A65F274E362A07A4D3BEB35BB1AC0F0D6C5FAG9BBF301E0C1F365018DA885084E08698G88DE086387FFDC6EE72F1C365BE9C68CC90E813B28BCBA38BCC75578561BDBEB06571B109AEDFFE1E20D373FB039C65BDF18DE23749798EE969F57A9A748B2FCC48DB90319AF7A1949E66AD27C0D511BDEB9A7D726C7A959A36BD76B51EF5A42616F19CC4F7E1B1E2DD18B557A17
	CC5F2E96AFD46B5FED5928FFCB212E62C9147DF307FD4B6EE70707175DAB0735E5775B435E32FB6D30366CD69E767675D665A13D21C06B52A751EE556CC49FAEE5915A4B73BBC95BA3D0BE8360D804ED3F5C05B6AEF0915E7D073C72DB2ECCCEBF99E13FC59E7D2297DD29FC7B69997D72A235FC83A8FE59A235FCB7D4A03FD6C2DD73E2DC6F1B954A79A3819F3358BF8DCCA6E93544EE3BBC4E4836021171607C887B5D6B51AFFE983A939663BB61F8FAFEA6BF25665D940ACFFFCA4D3BBDFB10F739D09770147C
	1EDE2419777B944F7BFD4A1B9E566FA54D5C4D34A55C2FDF622BDFA189E3705F7B953F9F59F26DF3D8FEFECD67E0BAF5284E372DB9DC271D9357D0271D93F7224E7F5FD71C2E737DEF99FDD1677DEF990D2977CDDBE87D70F6701EFAEDBBB8335E5B8E6A6A356D608375CABB48621DE7683D2AE28973AB192C566438F5E3236277586CEB2FA73AFF419CEFFA6587DCD93F141DA3BC03FFF0BFE6CFFEC34F1D26A5EABB73BA451FD92236B30E1DE8E74CD09733846DCC7BAE657C45CF9BD30FBC6D5F56B15D7877F2
	548D4CFB58ACDC2EF06B43CD0A5F1BAA2E3D5C8A2F699CEC4D1B8F10B53782FFFE1E0C38AD94E7855CB1395DA374DE9588385027716DA0261066CCC1B97AE9647D5DFB5CBC1A21783DEF4F356717611A4E41563C0F5E7DDE0675AE177990D2A467814A38954558AEB386F14765FCC1FD8C5EC16D5944F6192C16AE9B09A34AEBD93E534D3BBFA8FE776D3C76FC9CFDCD67E03C685B05FAD73596F117F6215EEDCB427B3869C33247F92C76F30BD96AE6785C01F75CF803C8EC3F007110178AC86C02BC6931770657
	6518C0034C777FA8A4EB6D34DBFBFBECAE7CFB06C2EC57AA37BBDA6419ABF2301148024529E69779FBE7E56D3FB86AEEFF566FFBB91B6FFF8A086DA9134B6F6C41B27BF721DFED9B022F6E8F42BAC9D8C71FB3A5E1FD2D592CBFA976ABFF3D4D1E126087FBB3029F6CB1D3925EE52F1DFA766FEA39G415ED0FA591F1BGA225495E25BFEA01FE4266DAE7E73725CF54B7D81CE2A7DCE387D99CF64F774AF715449BBBFD662506BE43EC2F6E99F79C165D25EB5AE98EB660131F457BD350B764CD49D72C322547E4
	35CAA66B05DA1B1944AD6B86A5516D2354361371DD5806D732BAC3BB217B3C224F7F83D0CB8788450A951E299DGG44DBGGD0CB818294G94G88G88G3A0171B4450A951E299DGG44DBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG639DGGGG
**end of data**/
}
}