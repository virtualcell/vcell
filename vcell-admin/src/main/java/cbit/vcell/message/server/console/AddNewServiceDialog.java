/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.console;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.ServiceSpec;
import cbit.vcell.message.server.ServiceSpec.ServiceStartupType;
import cbit.vcell.message.server.bootstrap.ServiceType;


/**
 * Insert the type's description here.
 * Creation date: (8/22/2003 3:43:58 PM)
 * @author: Fei Gao
 */
public class AddNewServiceDialog extends javax.swing.JDialog {
	private javax.swing.JButton ivjNewButton = null;
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JComboBox ivjOrdinalCombo = null;
	private javax.swing.JTextField ivjSiteField = null;
	private javax.swing.JComboBox ivjTypeCombo = null;	
	private javax.swing.JComboBox ivjStartupCombo = null;
	private javax.swing.JTextField ivjMemoryMBField = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private JPanel ivjMainPanel = null;
	public boolean action = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AddNewServiceDialog.this.getNewButton()) 
				connEtoC1(e);
			if (e.getSource() == AddNewServiceDialog.this.getCancelButton()) 
				connEtoC2(e);
		}
	};
/**
 * AddNewServiceDialog constructor comment.
 */
public AddNewServiceDialog() {
	super();
	initialize();
}
/**
 * AddNewServiceDialog constructor comment.
 * @param owner java.awt.Dialog
 */
public AddNewServiceDialog(java.awt.Dialog owner) {
	super(owner);
	initialize();
}
/**
 * AddNewServiceDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 */
public AddNewServiceDialog(java.awt.Dialog owner, String title) {
	super(owner, title);
}
/**
 * AddNewServiceDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 * @param modal boolean
 */
public AddNewServiceDialog(java.awt.Dialog owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * AddNewServiceDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param modal boolean
 */
public AddNewServiceDialog(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
}
/**
 * AddNewServiceDialog constructor comment.
 * @param owner java.awt.Frame
 */
public AddNewServiceDialog(java.awt.Frame owner) {
	super(owner);
	initialize();
}
/**
 * AddNewServiceDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public AddNewServiceDialog(java.awt.Frame owner, String title) {
	super(owner, title);
}
/**
 * AddNewServiceDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public AddNewServiceDialog(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * AddNewServiceDialog constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public AddNewServiceDialog(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
}
/**
 * Comment
 */
public void newButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {	
	if (getTypeCombo().getSelectedIndex() < 0 
			|| getOrdinalCombo().getSelectedItem() == null || getStartupCombo().getSelectedIndex() < 0
			|| getMemoryMBField().getText().length() == 0) {
		javax.swing.JOptionPane.showMessageDialog(this, "Some fields are missing!", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
		return;
	}
	
	try {
		int ordinal = Integer.parseInt("" + getOrdinalCombo().getSelectedItem());
	} catch (NumberFormatException ex) {
		javax.swing.JOptionPane.showMessageDialog(this, "Ordinal must be a number!", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
		return;
	}
	
	action = true;
	dispose();
	return;
}
/**
 * Comment
 */
public void cancelButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	action = false;
	dispose();
	return;
}

/**
 * connEtoC1:  (AddNewAddButton.action.actionPerformed(java.awt.event.ActionEvent) --> AddNewServiceDialog.addNewAddButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.newButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (AddNewCancelButton.action.actionPerformed(java.awt.event.ActionEvent) --> AddNewServiceDialog.addNewCancelButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.cancelButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the AddNewAddButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getNewButton() {
	if (ivjNewButton == null) {
		try {
			ivjNewButton = new javax.swing.JButton();
			ivjNewButton.setText("New");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNewButton;
}

/**
 * Return the AddNewCancelButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCancelButton() {
	if (ivjCancelButton == null) {
		try {
			ivjCancelButton = new javax.swing.JButton();
			ivjCancelButton.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCancelButton;
}

/**
 * Return the AddNewLogFileField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMemoryMBField() {
	if (ivjMemoryMBField == null) {
		try {
			ivjMemoryMBField = new javax.swing.JTextField();
			ivjMemoryMBField.setText("100");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMemoryMBField;
}

/**
 * Return the AddNewServiceNameField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getOrdinalCombo() {
	if (ivjOrdinalCombo == null) {
		try {
			ivjOrdinalCombo = new javax.swing.JComboBox();
			for (int i = 0; i < 20; i ++) {
				ivjOrdinalCombo.addItem(i);
			}
			ivjOrdinalCombo.setSelectedIndex(-1);
			ivjOrdinalCombo.setEditable(true);
			
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOrdinalCombo;
}

private javax.swing.JTextField getSiteField() {
	if (ivjSiteField == null) {
		try {
			ivjSiteField = new javax.swing.JTextField();
			ivjSiteField.setText(VCellServerID.getSystemServerID().toString());
			ivjSiteField.setEditable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSiteField;
}

private javax.swing.JComboBox getStartupCombo() {
	if (ivjStartupCombo == null) {
		try {
			ivjStartupCombo = new javax.swing.JComboBox();
			for (ServiceStartupType serviceStartupType : ServiceStartupType.values()){
				ivjStartupCombo.addItem(serviceStartupType.getDescription());
			}			
			ivjStartupCombo.setSelectedIndex(0);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartupCombo;
}

private javax.swing.JComboBox getTypeCombo() {
	if (ivjTypeCombo == null) {
		try {
			ivjTypeCombo = new javax.swing.JComboBox();
			for (ServiceType st : ServiceType.values()) {
				if (!st.equals(ServiceType.SERVERMANAGER)) {
					ivjTypeCombo.addItem(st);
				}
			}			
			ivjTypeCombo.setSelectedIndex(-1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTypeCombo;
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
			
			JPanel panel3 = new JPanel(new java.awt.FlowLayout());
			panel3.add(getNewButton());
			panel3.add(getCancelButton());
			
			getJDialogContentPane().add(panel3, "South");
			getJDialogContentPane().add(getMainPanel(), "Center");
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
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getMainPanel() {
	if (ivjMainPanel == null) {
		try {
			ivjMainPanel = new javax.swing.JPanel();
			ivjMainPanel.setLayout(new java.awt.GridLayout(5, 2));
			ivjMainPanel.add(new JLabel("Site"));
			ivjMainPanel.add(getSiteField());
			ivjMainPanel.add(new JLabel("Type"));
			ivjMainPanel.add(getTypeCombo());
			ivjMainPanel.add(new JLabel("Ordinal"));
			ivjMainPanel.add(getOrdinalCombo());
			ivjMainPanel.add(new JLabel("Startup Type"));
			ivjMainPanel.add(getStartupCombo());
			ivjMainPanel.add(new JLabel("Memory MB"));
			ivjMainPanel.add(getMemoryMBField());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMainPanel;
}

/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 4:07:31 PM)
 * @return cbit.vcell.messaging.admin.VCellServiceConfig
 */
public ServiceSpec getServiceSpec() {
	VCellServerID site = VCellServerID.getServerID(getSiteField().getText());
	ServiceType stype = (ServiceType)getTypeCombo().getSelectedItem();
	int ordinal = 0;
	try {
		ordinal = Integer.parseInt("" + getOrdinalCombo().getSelectedItem());
	} catch (NumberFormatException ex) {
		throw new RuntimeException("Ordinal must be a number!");		
	}
	ServiceStartupType startup = ServiceStartupType.fromDescription((String)getStartupCombo().getSelectedItem());
	int memoryMB = Integer.parseInt(getMemoryMBField().getText());

	return new ServiceSpec(site, stype, ordinal, startup, memoryMB);
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
	getNewButton().addActionListener(ivjEventHandler);
	getCancelButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setTitle("Add new service");
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(471, 225);
		setModal(true);
		setResizable(false);
		setContentPane(getJDialogContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

public void modifyService(ServiceSpec ss) {
	this.setTitle("Modify Service");
	getNewButton().setText("Modify");	 
	getSiteField().setText(ss.getServerID().toString());
	getSiteField().setEditable(false);
	getSiteField().setBackground(Color.white);
	getTypeCombo().setSelectedItem(ss.getType());
	getTypeCombo().setEnabled(false);
	getTypeCombo().setBackground(Color.white);
	getOrdinalCombo().setSelectedItem(ss.getOrdinal());
	getOrdinalCombo().setEnabled(false);
	getOrdinalCombo().setBackground(Color.white);
	getStartupCombo().setSelectedItem(ss.getStartupType().getDescription());
	getMemoryMBField().setText(ss.getMemoryMB() + "");
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 5:10:29 PM)
 * @return boolean
 */
public boolean isAction() {
	return action;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		AddNewServiceDialog aAddNewServiceDialog;
		aAddNewServiceDialog = new AddNewServiceDialog();
		aAddNewServiceDialog.setModal(true);
		aAddNewServiceDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aAddNewServiceDialog.setVisible(true);
		java.awt.Insets insets = aAddNewServiceDialog.getInsets();
		aAddNewServiceDialog.setSize(aAddNewServiceDialog.getWidth() + insets.left + insets.right, aAddNewServiceDialog.getHeight() + insets.top + insets.bottom);
		aAddNewServiceDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}
}
