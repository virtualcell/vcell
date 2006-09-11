package cbit.vcell.messaging.admin;

import cbit.vcell.messaging.ManageConstants;
import cbit.vcell.messaging.VCServerInfo;
import cbit.vcell.messaging.VCServiceInfo;

/**
 * Insert the type's description here.
 * Creation date: (8/22/2003 3:43:58 PM)
 * @author: Fei Gao
 */
public class AddNewServiceDialog extends javax.swing.JDialog {
	private javax.swing.JButton ivjAddNewAddButton = null;
	private javax.swing.JButton ivjAddNewCancelButton = null;
	private javax.swing.JTextField ivjAddNewServiceNameField = null;
	private javax.swing.JComboBox ivjAddNewServiceTypeCombo = null;
	private javax.swing.JTextField ivjAddNewStartField = null;
	private javax.swing.JTextField ivjAddNewStopField = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JLabel ivjJLabel14 = null;
	private javax.swing.JLabel ivjJLabel7 = null;
	private javax.swing.JLabel ivjJLabel88 = null;
	private javax.swing.JLabel ivjJLabel9 = null;
	private javax.swing.JPanel ivjJPanel3 = null;
	private javax.swing.JPanel ivjJPanel4 = null;
	private java.awt.GridLayout ivjJPanel4GridLayout = null;
	private javax.swing.JLabel ivjLabel99 = null;
	public boolean action = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextField ivjAddNewHostNameField = null;
	private javax.swing.JTextField ivjAddNewLogFileField = null;
	private javax.swing.JLabel ivjAddNewLogFileLabel = null;
	private javax.swing.JCheckBox ivjAddNewAutoStartCheck = null;
	private javax.swing.JLabel ivjAutoStartLabel = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AddNewServiceDialog.this.getAddNewAddButton()) 
				connEtoC1(e);
			if (e.getSource() == AddNewServiceDialog.this.getAddNewCancelButton()) 
				connEtoC2(e);
		};
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
public void addNewAddButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	String hostName = getAddNewHostNameField().getText();
	String serviceName = getAddNewServiceNameField().getText();
	if (hostName == null || hostName.trim().length() == 0 || serviceName == null || serviceName.trim().length() == 0) {
		javax.swing.JOptionPane.showMessageDialog(this, "Some required fields (with *) are missing!", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
		return;
	}
	
	action = true;
	dispose();
	return;
}
/**
 * Comment
 */
public void addNewCancelButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
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
		this.addNewAddButton_ActionPerformed(arg1);
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
		this.addNewCancelButton_ActionPerformed(arg1);
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
private javax.swing.JButton getAddNewAddButton() {
	if (ivjAddNewAddButton == null) {
		try {
			ivjAddNewAddButton = new javax.swing.JButton();
			ivjAddNewAddButton.setName("AddNewAddButton");
			ivjAddNewAddButton.setText("Add");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddNewAddButton;
}
/**
 * Return the AddNewAutoStartCheck property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getAddNewAutoStartCheck() {
	if (ivjAddNewAutoStartCheck == null) {
		try {
			ivjAddNewAutoStartCheck = new javax.swing.JCheckBox();
			ivjAddNewAutoStartCheck.setName("AddNewAutoStartCheck");
			ivjAddNewAutoStartCheck.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddNewAutoStartCheck;
}
/**
 * Return the AddNewCancelButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAddNewCancelButton() {
	if (ivjAddNewCancelButton == null) {
		try {
			ivjAddNewCancelButton = new javax.swing.JButton();
			ivjAddNewCancelButton.setName("AddNewCancelButton");
			ivjAddNewCancelButton.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddNewCancelButton;
}
/**
 * Return the AddNewServiceHostNameField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAddNewHostNameField() {
	if (ivjAddNewHostNameField == null) {
		try {
			ivjAddNewHostNameField = new javax.swing.JTextField();
			ivjAddNewHostNameField.setName("AddNewHostNameField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddNewHostNameField;
}
/**
 * Return the AddNewLogFileField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAddNewLogFileField() {
	if (ivjAddNewLogFileField == null) {
		try {
			ivjAddNewLogFileField = new javax.swing.JTextField();
			ivjAddNewLogFileField.setName("AddNewLogFileField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddNewLogFileField;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAddNewLogFileLabel() {
	if (ivjAddNewLogFileLabel == null) {
		try {
			ivjAddNewLogFileLabel = new javax.swing.JLabel();
			ivjAddNewLogFileLabel.setName("AddNewLogFileLabel");
			ivjAddNewLogFileLabel.setText("Logfile");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddNewLogFileLabel;
}
/**
 * Return the AddNewServiceNameField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAddNewServiceNameField() {
	if (ivjAddNewServiceNameField == null) {
		try {
			ivjAddNewServiceNameField = new javax.swing.JTextField();
			ivjAddNewServiceNameField.setName("AddNewServiceNameField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddNewServiceNameField;
}
/**
 * Return the AddNewServiceTypeCombo property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getAddNewServiceTypeCombo() {
	if (ivjAddNewServiceTypeCombo == null) {
		try {
			ivjAddNewServiceTypeCombo = new javax.swing.JComboBox();
			ivjAddNewServiceTypeCombo.setName("AddNewServiceTypeCombo");
			ivjAddNewServiceTypeCombo.setSelectedIndex(-1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddNewServiceTypeCombo;
}
/**
 * Return the AddNewStartField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAddNewStartField() {
	if (ivjAddNewStartField == null) {
		try {
			ivjAddNewStartField = new javax.swing.JTextField();
			ivjAddNewStartField.setName("AddNewStartField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddNewStartField;
}
/**
 * Return the AddNewStopField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAddNewStopField() {
	if (ivjAddNewStopField == null) {
		try {
			ivjAddNewStopField = new javax.swing.JTextField();
			ivjAddNewStopField.setName("AddNewStopField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddNewStopField;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAutoStartLabel() {
	if (ivjAutoStartLabel == null) {
		try {
			ivjAutoStartLabel = new javax.swing.JLabel();
			ivjAutoStartLabel.setName("AutoStartLabel");
			ivjAutoStartLabel.setText("Auto Start");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAutoStartLabel;
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
			getJDialogContentPane().add(getJPanel3(), "South");
			getJDialogContentPane().add(getJPanel4(), "Center");
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
 * Return the JLabel14 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel14() {
	if (ivjJLabel14 == null) {
		try {
			ivjJLabel14 = new javax.swing.JLabel();
			ivjJLabel14.setName("JLabel14");
			ivjJLabel14.setText("Start Command");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel14;
}
/**
 * Return the JLabel7 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel7() {
	if (ivjJLabel7 == null) {
		try {
			ivjJLabel7 = new javax.swing.JLabel();
			ivjJLabel7.setName("JLabel7");
			ivjJLabel7.setText("Stop Command");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel7;
}
/**
 * Return the JLabel88 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel88() {
	if (ivjJLabel88 == null) {
		try {
			ivjJLabel88 = new javax.swing.JLabel();
			ivjJLabel88.setName("JLabel88");
			ivjJLabel88.setText("*Service Name");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel88;
}
/**
 * Return the JLabel9 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel9() {
	if (ivjJLabel9 == null) {
		try {
			ivjJLabel9 = new javax.swing.JLabel();
			ivjJLabel9.setName("JLabel9");
			ivjJLabel9.setText("*Service Type");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel9;
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
			ivjJPanel3.setLayout(new java.awt.FlowLayout());
			getJPanel3().add(getAddNewAddButton(), getAddNewAddButton().getName());
			getJPanel3().add(getAddNewCancelButton(), getAddNewCancelButton().getName());
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
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel4() {
	if (ivjJPanel4 == null) {
		try {
			ivjJPanel4 = new javax.swing.JPanel();
			ivjJPanel4.setName("JPanel4");
			ivjJPanel4.setLayout(getJPanel4GridLayout());
			getJPanel4().add(getLabel99(), getLabel99().getName());
			getJPanel4().add(getAddNewHostNameField(), getAddNewHostNameField().getName());
			getJPanel4().add(getJLabel9(), getJLabel9().getName());
			getJPanel4().add(getAddNewServiceTypeCombo(), getAddNewServiceTypeCombo().getName());
			getJPanel4().add(getJLabel88(), getJLabel88().getName());
			getJPanel4().add(getAddNewServiceNameField(), getAddNewServiceNameField().getName());
			getJPanel4().add(getJLabel14(), getJLabel14().getName());
			getJPanel4().add(getAddNewStartField(), getAddNewStartField().getName());
			getJPanel4().add(getJLabel7(), getJLabel7().getName());
			getJPanel4().add(getAddNewStopField(), getAddNewStopField().getName());
			getJPanel4().add(getAddNewLogFileLabel(), getAddNewLogFileLabel().getName());
			getJPanel4().add(getAddNewLogFileField(), getAddNewLogFileField().getName());
			getJPanel4().add(getAutoStartLabel(), getAutoStartLabel().getName());
			getJPanel4().add(getAddNewAutoStartCheck(), getAddNewAutoStartCheck().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel4;
}
/**
 * Return the JPanel4GridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getJPanel4GridLayout() {
	java.awt.GridLayout ivjJPanel4GridLayout = null;
	try {
		/* Create part */
		ivjJPanel4GridLayout = new java.awt.GridLayout(7, 2);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel4GridLayout;
}
/**
 * Return the Label99 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabel99() {
	if (ivjLabel99 == null) {
		try {
			ivjLabel99 = new javax.swing.JLabel();
			ivjLabel99.setName("Label99");
			ivjLabel99.setText("*HostName");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabel99;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 4:07:31 PM)
 * @return cbit.vcell.messaging.admin.VCellServiceConfig
 */
public VCServiceInfo getServiceInfo() {
	String hostname = getAddNewHostNameField().getText();
	String stype = (String)getAddNewServiceTypeCombo().getSelectedItem();
	String sname = getAddNewServiceNameField().getText();
	String start = getAddNewStartField().getText();
	String stop = getAddNewStopField().getText();

	if (getAddNewLogFileField().isEnabled()) {
		return new VCServiceInfo(hostname, stype, sname, start, stop, getAddNewLogFileField().getText(), getAddNewAutoStartCheck().isSelected());
	}	
	return new VCServiceInfo(hostname, stype, sname, start, stop, getAddNewAutoStartCheck().isSelected());
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
	getAddNewAddButton().addActionListener(ivjEventHandler);
	getAddNewCancelButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AddNewServiceDialog");
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
/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 3:49:28 PM)
 */
public void initModify(VCServiceInfo serviceInfo) {
	if (serviceInfo.getServiceType().equals(ManageConstants.SERVER_TYPE_BOOTSTRAP) || serviceInfo.getServiceType().equals(ManageConstants.SERVER_TYPE_RMISERVICE)) {
		getAddNewLogFileField().setEnabled(false);
		getAddNewLogFileField().setBackground(getJDialogContentPane().getBackground());
		getAddNewLogFileLabel().setEnabled(false);
	} else {
		getAddNewLogFileField().setText(serviceInfo.getLogfile());
		getAddNewAutoStartCheck().setSelected(serviceInfo.isAutoStart());
	}
	
	getAddNewServiceTypeCombo().removeAllItems();
	getAddNewServiceTypeCombo().addItem(serviceInfo.getServiceType());
	getAddNewServiceTypeCombo().setToolTipText("Service type can't be modified");
		
	getAddNewHostNameField().setText(serviceInfo.getHostName());
	getAddNewHostNameField().setToolTipText("Host name can't be modified");
	getAddNewHostNameField().setEditable(false);
	
	getAddNewServiceNameField().setText(serviceInfo.getServiceName());
	getAddNewServiceNameField().setEditable(false);
	getAddNewServiceNameField().setToolTipText("Service name can't be modified");
	
	getAddNewStartField().setText(serviceInfo.getStartCommand());
	getAddNewStopField().setText(serviceInfo.getStopCommand());
	getAddNewAddButton().setText("Modify");

	getAddNewAutoStartCheck().setSelected(serviceInfo.isAutoStart());
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/2003 3:49:28 PM)
 */
public void initNew(VCServerInfo serverInfo) {	
	getAddNewAddButton().setText("Add");
		
	if (serverInfo.isServerManager()) {
		getAddNewServiceTypeCombo().removeAllItems();	
		getAddNewServiceTypeCombo().addItem(ManageConstants.SERVER_TYPE_BOOTSTRAP);
		getAddNewServiceTypeCombo().addItem(ManageConstants.SERVER_TYPE_RMISERVICE);
		
		getAddNewLogFileField().setEnabled(false);
		getAddNewLogFileField().setBackground(getJDialogContentPane().getBackground());
		getAddNewLogFileLabel().setEnabled(false);
		getAddNewHostNameField().requestFocus();
	} else {
		getAddNewServiceTypeCombo().removeAllItems();
		for (int i = 0; i < cbit.vcell.messaging.MessageConstants.AllServiceTypes.length; i ++){
			getAddNewServiceTypeCombo().addItem(cbit.vcell.messaging.MessageConstants.AllServiceTypes[i]);	
		}		

		getAddNewHostNameField().setText(serverInfo.getHostName());
		getAddNewHostNameField().setEditable(false);
		getAddNewHostNameField().setToolTipText("Host name can't be modified");
		getAddNewServiceNameField().requestFocus();
	}		

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
		aAddNewServiceDialog.show();
		java.awt.Insets insets = aAddNewServiceDialog.getInsets();
		aAddNewServiceDialog.setSize(aAddNewServiceDialog.getWidth() + insets.left + insets.right, aAddNewServiceDialog.getHeight() + insets.top + insets.bottom);
		aAddNewServiceDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G410171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DB8DF4D4C516C6A78C81237293B0A0EAC4540081F1E55C4CEE564119384B0C2C46DD1C031AB91333030A8EB3079DB933B98E3B3E7CA041848998B50CF14C2C4886A7C67ED4865149CF07F4C81BF41226096477257BA5E96874EB3BDF13CE52405E2AFA377A2573DE87B812F36EF9DDDF5DDB376A5EDBD5376A3D88456FA7EE4D4EA996046C2D027A77F936A014D48B424B53175C0858CC0F3DC0507C7D
	99E8177056B7F970CC043A74657602A221B1AF9F4AAB21EC0172BB707BCD2134BE6EEF7043C00F936A0ABB6E6A0A2E6737E7D04FB6DAF6D59742738D20944048B3CFA67A2F2BAED1718C95C74A4E91047514E7E1F5290A19215C8B148834D2E67CEB60391DEA1EFA6C284A57B9ABC9587CB3DF4E8E520E26CD248AAF13352A4FB261156CACAF5175A86DA711090572AAA0B278DC21F85B91F89E371E9B784A5167D2AA4FBBA4373BF2C8727B458117E720D2F48E39BC470ED5BB1DCD52F01B64BB6FF2C8A6176816
	8714EFC93BBBEA72E9BB508614859F4162C522AB854A69C09BB85E51C7F0937C6E84D2FCB83EAF7F6F385A7F76313F97568FBC3AFD27417862FCECDCABFD617125FC54173B077A22321358FC9354957B6D8565C096A0B7D09C503BB486BE6D78881E55FDCA2DBCB4A4FB2A3D5E9A375377356C145C709B865A2728D82B6410FDCEC1E0FEFE2C3AC227BF510849BDFD45F24CA6C9075167492D4B050C876765EC5331C9C652D84EE2BF33C52ABFEC9326FBF70D116E8C7F78189C2F7B342055AD3CF8E85BD69D5D25
	BFBF34ED135ACE1E9256FDC4A13E4E273E5E8B3F335578E078EE0A87B5B80B4F85B4E6C8B9966AB6AA989B6DEFE0DFACF90B04220337685AA16147F1B956G6B43E0A032AFCFD01D718126F3AB6F4BB394CF52602CAFA245C9B9956A4A4E5B8BD8FCFD5ECD66EB8C149301D2005201B681958E835FB04658450ECFAEB3462C22CF2989385CCE49A7B0594FBB5F062794D4FCE203472F089E07E497FDAE314FAD798515272A135820EF1CDC9D0DFD87682817BC12CFD4DC3287DA9707A4C5727999CFD367AE426313
	E82D4D8533DC91073C8236EB6FFC931EEE512F1C72BAC5C58AF3307A6ECE621373229DDA048660B7330B3307582B8A465F8EB4486D70B4ED2FD172F9A47731E396D9BE97705A48C80414E1E2677DF46D88416F78E134F1EFAF4136C039B8086D94F56D5634E3F2B9C877C55F884E31B77A481ADC9E441879EDBB460CE947B2216229393AB1B39362579CE431529B0C1CBBA534CD52AFD6CE945B2FF09EDF8E152BFA190E9FDE939D396A135977F1BA4FE2C16FE220B4EE63462EB29633B522DF42A8ED76C100C94E
	3045D9BF1F6A4BD76D194B7B1EC9318CE8AFEF84EDB650572E617B2E4745F4CC21BAC87BDB2F018EE61B6AEE329FC967A50FE2123C124789BF7CEA1D391B441CBADF7BE5AE53C4F1AF4C6FDA18D0FE0E5B3A499C949545676A8BA8D21B24A8304D066BBB29AEFF204F9BDE9AD017231B0CD19E06794D79873B495CF0489E0FC41D44C5547A33F4EECAEEE988BADDAB87BC8A433DBDA43E4FC9A36A5A20743CA610396D8E10F5066586ABC412GF9FD2EA1F0BDD92A04A9A3E45ED65514E8ECD4819849F1EC709C9C
	41B9B87C814E413D23CB04D5BD5E9C3DBDA4616E1BF3D60F30B910B1921E032CCFD22FFE5F4F51B5C2457D2C2F8F671035C6A7CDD163FE1B0334350574FF94F203D10C7B97FAC9BD2EADEA40574AEEB7B3A73773DA310878C57AB6A04112CC9631C6EE3BE7455D91F5F6550E2C7ED7540EEE4D7A17B7C677C28766FFD5501FF62060A833E54A283A2FD23E1FF3BEAB14874742FC31E30C0F592075DBB2961A4B1D954F0B41CA7FB0445631E3A7D5CB88824B591652F51778E95D9866E635F544BEC5A1308DD0D588
	7D78648E4C4D624FA58999AF9E5CE614FB3607D8DFC221309F57EC2AFE0D593F45C9F426C25D1A905A3E3BCF3BBFD47B7D3243C537A655EEBB9CDABFB3EC3763F5F8763BFCFE2581A6E290EDDC62A07DA5FB178A2BFB79978E5C4BB3C1EF4985FB410A8BB49F3694EB7AEE85AC883490A817363748F1084CD39A84156CE115FC7D32EFC8F216936BD60A43CAA5DD93AA2BE9F59D793D9FFD714058D7C41E46A3C4E5CDC0D1E4CFCFF5C4C39663163E58EF47FCEC511BD5EABF57ACDFA49C3F541D23179F476E9C4C
	493A205A7EE2588719337BF2B783B1FB3C4A6D61861E4597519EEFB56B59A363A21E939EBBDC4D47D3CB329277D58EC9DD376F273969260B7AF9E85EA50617284F8AF8321CED68844DB5AEC56615439437DE0A4CAB9B6AB02F6C053A34CB18E7E4CAE48DAB1FE2AFB081F582A9C071C0AB27E0FCA6BA8E8B1364F18D9EC82FBC223BED5C020D7914B3172D31EA790CF3AB2E315C8E550E489CBDFD4AF81BEC0CA8B3396689F2FF0AA897DDB7DE0E79F1E71B419A415C89F5EC0DDF5620DD634B21AD8B1097A88608
	2DA3FDCD79EA3B8B2F43756242896DFA113A73C2CE0141FA31D66D5F666BB4E7B959360D79E746GD9730EDCEFAF30828D8245GA5812581ED389EFD345E7965E4BE2231E713EB81B3439ADC9B39D7F21BEEF5DE1DAF5E1CA097690B7C6F0D17E37633B4237D4A201E65447FCC5766F6A807B87627DA024D04F29247EEB391EC8D14B3B976BC45B6C339B086F3B07F89324F17717285DAA63ED463286E3B4EB9092E7176997C4EF5E873C99687AFCA46B95AA6C9BFC77B3D241F23ED11F6B19C5A501E5030BE2FBF
	FA8ED7582F17433DC77B7D5337F752367DFE49CF8353A4AA22DA7702721620134BB541A1CE9D731BC2106EF30427CE11EB8F8AB82FDF5ECF662B4E3C360A303B32B93D7CC4641CA6671BAA7017AD866774F38D98136E984C8D5E3010B5F4B114D3015672D89AE8523BDF58287ADF18BAF1EDE7F3FB170B6CB545D0FFG488C548BB4E5AA4E69C57D132E3BF541A86BEE7C00F1AC5CBA209F8BF78E103C46AF87FC8EA9525FC983A53CAE3CFAB4A85290F18D63F9004AAB22EFC01A90AFA9D41E559949BFB2E094D3
	4C6F471DDA3F9F62F32B25D94F87092A6DD76A780035770B9A92EBCE29DF8C38A17CE53F546CD52F8DG4F7BBE71FBD68D1E73D6CF45BC6001E1321E777B44A168DE1BD7F28899147753DA0CADB314BB01C2C08B01D23F0FB955CA93E9E3C8F60AEE2E63B1935E9F6E1D06FC7FEDA2E375C9FE57A84D1611772DBA6C4F914E5BD4CFF2EFB5438563AAF44EFAA4DCE31FBF0171EC1E06717CFBB30967A9D0CEGDAB18D63792506C8DB2E1E9636DF46340936E4394775F4FEEC1A96F93FF4A34573E2A36F170AAD18
	0315C0DDA896576F0BF4BF4D0472A60E7D2A89577EAD31B8BF76380C633B4C259F5F7BDC7AEBDD05AB7ADAF744E594172CFF034D5867326958671FB6E31FAB26231FF65612BBB14D52D4F95A65F14A4360B2F24B2578ABCD4DBDCD4DB6F3C3D3FDCFF3D3CF2D25392DCE4DFF930735775FC7DF3FDE77ACC948BA1D794829BE63DE1D1F4D62202396E3E7A1542D1DCE3097EA35B9AF6967CF0021921E2CFE0FC9DBCF6CG693D263EF1DCBD696B7D395A7A8B636A37EA6459389E7B90F325AD0F2D905CBF08B13C57
	AF1D4146D325BE4B9E1F1FDDBE5F7A9A336DFF18481E2E788292ABD75491DD7D225B9F4EB3FFFDA2B23603B342719CB743A8364FB610D8CD1C91995B810A2F1C91995BDB4C985B2B212E62860C096C13B4A6203C18E3D39AB1CED65F00F3B056C2F398A89745A1B607E264FCCC4E3E8C7B2365EAF28A52E6A4C6F4C5E2CC47612B5221F78F25F77F45FCB537097A8EA66C01B8723EE99A390E9514B8E21FD71ACA2268E17CCB3F8D73E7B6C763E7FB6FE99FBD07837F3AB8DCCFD63B7E82CF3BF8D65DE96B1CAEC5
	76512D96C02BCF061DC199E1A83D59D06D73F06D553D5BF8E49B467D4AFFD8AA24FC5FE4F8C648D27DD5F2A3FB262C191F9D9E4BA3FEB2DEB354B901E2019201D6822D3991473673AC69A34998207B550AA2BA8669ED82A692F6971C4FB9CCB6B4961BB975F4DD03F6B65F0871FA1D99633564A60CC3AF4D170EC3594A31B10A11733313E3B98DD79ACB244D896B1B8E960E57F72FD807DEDC9A5D2332F8ADB9C96E895D62089CD0E06E5CC46C7249C90C3BD8A8A7822D864A38095DA1B8EFA6BC37B4166138EE56
	99170AB19D1F96127D2DFA7C5D09DA17F7F8B72F535EC3E0ECBD51215DAB0A9638F3328DE2ABD95519F2F3787C495AD85D25DD174B7E522C3BAEE7F50D66ECD4E50BE6065BA8F69D592E5E657A884677CEB6F570AE57C77686174725C06682F98EC93C5A0051B340E2FBD600541B205DDE209820842064197C5E52773FAC2EF5E23A4EB9A0613E0A7AFE66B74E991E74CF7E4E6999BF39F76B134156C324FD44B37DA4769459CBC106BD6727791BEBE0D0E12CAA7EEBBF1997EE291F424C1FFB6AA7465301FA0C27
	55B05ECC207CD9760252D96C8E42BC0B70A418116748AC239C778193699BBB819DD63193B5E16C08FA577107E63D3B0E19506EE2A0362F1DBE096F4FD3815BBC8B73614307465D27100B2FE347CE3865E10BBAA4B58F3D63A4669F9533B18F7DD75A26934AA1207859F807925C077117FC4FA2A17DB97DBB57CC5B051C6459EC6C6933B5F72E9F7565B21DBFEA445C37F08E66E17F55C84AED5035C181674C30047ACA6F3BCBA8FD3FAAE717411CB14DE1FA3CF3427A426B711743827775187221E03027C0041E84
	D3B5771D050310CD9C607236A07143E2E8FB9550FA20ADC0C571760272F80C7BD5CA39D13BGB22353587709AEFCDFB0BFE09C7B090149E3FFC9005EC3C34C5AC63C6AF9EDF900442D5C5F6F17789CFBB0D04431863FD5763B482CE67153DE0F71F39C462244E37CBCDF796E4478291149FB88B50218AF936BB1FE1263B1FE9E27ED2E077296206239F806FF580CFE2F180BB1F5761C564744EFC63926F3AE73ED6C5C71BEE6ED9F36E25B2B67E2BF163612FE8873E0DF84BAB08F773C2E13D7774D44392B14DBFC
	45F2AC172DEEA47DB74FE339ECB647CFD25C264119AF7F1D2F853DD017B68F6F1433C6681D72AD768293D0A71082948F3462960CDF73798342A4F79BB659EB01E0F5EB0EE3ECFCE90D5A7125C11B6BEE514F871054F9B5D6864F0D401B1FGB9B91089289D488B948334B0817BF77379E8730B85E6647C6A8A9A4FAFE7F07279B594545FDB7C4109FBCBB0283F37DC8CEA7796E62F6E7A2B0BA377D54A5DFD45F2AC4EEA6819A9A5A1B27E9AA91E169099FFEB79DA128EF5C76663FC7B09D92BBF8170647917939F
	29A192372B00B783C8D8G739828824834G63E2574861A8F151AA4B745605DC40C0DCE8C3C4354DD1EB24ED5A97047B66DEE0F83752C66FEA96B09B3C47717994CF53604CB6AB68DAC44A793742BEFDAB5A263ED160FE32EAFEBFFD72DAFCF3B1BEAEFEF7CDF5B07FE40F9A4F417CD17DBB21B7C6756F040AC7235FEF170E9AFF23704128769B85F6EEFA1666A1198B6D8527F2914B24D896E0753C5D1EB163B1740FCD3E0E48E37A6BC8E0EC62BAB2BA263F0ECC99532EA38C1B2A366B6317718C3FE10C6C21AC
	F77DDA7408831AF7C9E6F3B846DAB43F1F293F76717672B555417C770C99FDAAF21F7E520CBE8DE4230D9E8D997BF4CDE8F21FBE95527769332109BE4D8C697B74791056272C2F4DB40FA93E8DF24B5B70FC5F530A67FB4BED7CBE2A0D1CA703D00E83CA86DA85349EA88B2874F648F700ACC03DC0A12005C029C06BEE47B66E20EDE4C139648EFB41D120F620A0D082D08A50BA20AC2012C42887EA878A82A5A4E29B8F35E62A76A76D23AF565D9EFDFDC7A27D1E0CE73D4AC346C2469AD44B214B50519B41D32A
	A92FB250CD6CC6EC182A61E5E37DCFBE56BD9A7E772EE08ECC46D320BE4977A7AC8FE765F7AEC3762D0872CE4D6F6D86B2E4BD5D2146A3E2CC2F380F6CD76ABD40E917D399E4F8E9F0C798B7CBA42685BC5776DBA71E19AFD8718C1B82B1138E1485D4F8A73B03E2F7B067DB4B220CEB6F1D5ABB18ADCD440E9631CFF27F752FAA56D89D7E464AAC7B9532961EF0C9EE27DA3F619CBDDBB3A1954B363EADC4FC17C58ED1107D7649286B418F72395C51238C3B610B89F2E1F52A5C5B6E305C41A38C4B6AAF8A4BA9
	30BFD189D67731BB5C3F4F55B63A376F54704BDE6DF83E30056F1AAC72408917DB2232287F2BFD936A47756F1FDA08BD2B830ACCFBB2CE76627B45DC16F3548ECA0EF36CFB1CDF34E95F9D9CD57D58F9A7FE2F73AFB6ED3DD72DCF607516D3FA72493C7EB33B36FE15DA3F0157971F51135F426BDFB923273FE89156DF6A50135F4B6B25CEBD79233C7E25CEBDF98B2FCF6F52136F6575273A756495DE7F379EBD79D8DE1F5623A73F1057AF6F55562FD8446A57AD427B223B2D1823EF84EC6FDDF85EFF22C7673E
	285E67F2C65C977D061F33BB6F42F3F6F28B7D0E9D4A49C0E9F7611D4E1CD6329E54CA64DBA47EBDC03F4177GB48A190EE29B6A48607D7B303CD4DF4E867BB40DEEA67BBFB6FCEFDB3C986F9D3EF78A5B33AC4676865C6F6A37C7D7009AB9280EF748CEE4497C0C616DB51E4176D2B9F6308375EE6658C39D7C3E63EE4496F6A21F13E3FF6E625FABF1ECDBB7722D65D8DCB73FB73B873173BD48E762589FFA10AFF48F0E77AB3B51F8495C8E0F7739DE14DDF98F6E7177CDB8A3DD4EDE37CB7861D54A7DEE1C5C
	46FB226F73423D788DDE99BDE3955F9BF9DE5DCF7132FBA34F2BFF5702F321826AE65E0B635DE3536B7766FBD9BF72130C5E4B651E0AB65ED2C339D8DD3983AA3F49D04EE70F262F9D645CC0D312A273986B99BD39F815AF49D05FE79D5174259A4A7D232EDC3A4A3F5ED06E4E4EE87A328C6576F5694995AED16F8D17984995F6C753F744D0EE162E1CD9656FB4149B6B09264FEBA8A7746A4945287C8906F2AF694AA52B7C2BF4645879E3FB8BD99356C27D7AA5F8A6E921D896148B17623C5957AAF0DD1FE8FE
	2734DC7B33651357D4879B73813A371441F88FACC5BB4C3561596CB8477EF88A31DE0E5DC13198A847F3AC4F0EFC099C1B4E31B40EAD215886A8675D0758E60A15C079G47864E200EAA0E2DBE434F0E9C7B3A83B1AF477E2D03770FE307BBF97FB8164231B40EFD5905329BB8F6FB9772ED6658875D7CBDF5B2E2C994BB8EE58B47F674A0DFBB47E6F3AC4431F768DE158865959C8B75A2DFAA4732C5644B64D81F08FC9B13B1DFA9EBA15FBC7864E1BF7B2E6BB39A63A48FAFDA06BC03ADA45E9C32BBB064712B
	5FFFDDEA615F4BF23EFA5B786FA706AE1279403E07C836C57B9E02719CBC950D0769F86854F89DF7DF8A6BF857BE390E4428BCCC47AABB7D7ED0A27FF7A2B971FF9858381F26B178AA7C9D6063AEB8DD2865D724A14BB2427BA27DBF68AA784D7A7A14906E6BAF4FCC5ED7E747645F1C3C1793EE73E46764EDBE91150735D9159BEE7387DD1337F92CFBF21B7E28FB3C5FD6B607F56C6D19DC471228BCCCC7E56FF89D9B2DE19DF3FAA79F774E56B07F08B8393E2FDBC89E75B9AF4F2365C33C3C49CE1C7371C1AC
	3F7A8DE9DF7DBFEBC97BED4401B1CB8E6151FB87B8F3995B239217053F23E36F8ADEEDA563CDDF066F85DADB49F788C66F8548B7776C2B863C0B4BEDA3E7953F0A317F4BFF68B40975645F88DB95A1970E440A709A7B34C39136FF04978D0A3063A375A2200C5CA0E84BEABDBDC3171197EB22879AA9A46779F0F19B9C517B61082E882F1383F79965D650B67BAFE9ED13B555E1780DE1717D4CA66577075F0FB25B9452BD408978147B51BE1D5FFCECE41F6ABE25EDC8F43B9551FD2E5A63A46F44EBC694A9FC4F
	3E6B8CE95F0FBCEA257A7FCEE7C83F4FEAE47E9FD0CB87881013AB454A95GGC0C1GGD0CB818294G94G88G88G410171B41013AB454A95GGC0C1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8496GGGG
**end of data**/
}
}
