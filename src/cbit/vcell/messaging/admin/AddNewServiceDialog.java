package cbit.vcell.messaging.admin;

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
	D0CB838494G88G88GB2FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DB8DF4D4C5164619384B2E0C4B6CE4E650E5E6F394DDC6D1F144B39CE5B5BB6B2C0C3247180D8E330BFB98E5D77420439E195D6C8EE77675CFC2870200860CCB9C238664A744GD1C28818BFA0C08801840892C9DE3ADF521D3C6E5769FE1D6E7CE076564F2DFE693C57810E641CFBDE57F76F2DDBF56BD6552DFAAFD2417BE9B9B6FB01A459F2A47EF740A6C9053512B4E7371C05D8654719F912616F
	8320CDD2AC6804E7A370E23D19F99B25614BAEA8F7C0F9D5CFE65EBB707BEDE96C72344F6107059D8F70260E6DECC9EE272C9B6D2C2165A33D7970FC93288BB0721C73B131FF21371063A99CC73259A5E95A8EA2B350DB443115A063822C9128E387135761499AF0F3D5A5176B786EECE955D3A17BFAD20F21CE24AC2E1B4D1F45527F5852F692DB3753F6921DF2A8FB01C8679DD2411A8AF8D63774779EF1F77B7432C137222AE5FEA59C16FBFD015EB2596377852A2AEABD1EE6A55A2A0486FDEE2541A72BDAEF
	51AED26F0B3DAEDA0FA4AD03BAE7D34C3706586A025FB120693B90AFF791FC857CF66E4E4CAB5A0D7DBB7CF1B5EFFF7B484F242CFDFE67860B7ED56CE67D6A589D6FDFC360857B3BF4AC82EE62739940DBG1481348A2820B4B32F82E8AB0D01C877AEF856F66B751A5F2F85EA0341BA5593BA2DF99495FEC3C73BF50E1DD35CDA48A3C9EC1C53FACBCD5A130C185E7D572C47FCB2FBBF0E791CF377CBCB9E7A01FD0D09CF164C9E31B715B2DFF8CA63BEE136DF36341DD2BABEA6475B3EA899ED47824FBBF2CCEC
	27DEFA5EB10357B347E0FBC9A9996B97E88CAC025FB69E9F8CFF196245FB62B80BCF0D6224DC8E3C54BD989B6DEFE2DB1A1CF7C89B777E50548FB3FEBE4D1E3D0735E1E3D9E2DBE651382BAEE3B6F3C4DB9E20F80B81E7ED790C62246C815EC2A096DFD1054C5752CFE01E82F5814500E6G4D83DA78894658E35D1FDCE50C3548A13DAE62D3BDCAC8E23AD17796F8AAB1BDA41F8804F5B960D6AE48A11F5C2DAAE1094BCCF1939FF40F533B19463E9BEC9CD782CAC856FDDAGEA177D0A2E0442CC66D677A6A293
	D2A8374D8733DC177DC1896B7D017BEDF82AF2D8BF9F74483A9217E07C9F3B49188C4A9720C628GFEB33F4C6AA17EDA8E7D174A41FF656807F7BD243ED3CAA82028D5D5CD1A569F8936111EC8DDC4CE7A83DDBBCA60F7F5B97AB80DE233203CC0545362F94BD0CF034FCD1AAF0706F00E3D34062C49996598B32F37E34CB42C3FCFAAFD6E7B26B1636AF85E31321C450AEBEF625CF90D56C95A454A11B56D57B80F2F06B238FDE663569BE243411F4C3FEF7A48D815C3FF1B00543D6863DBBC45ACE66B643002D1
	FAA6848126F8629EE76D3C4765627EF40836BFCC319428EF4EDE74D9352B5DA077CD770B59B8CFED107AF3EE008D661B1B94329FA903CAC0EFD002CA4083BF421CB7D5A1B14767EB0FA6F4EE25F89066F7BDCC283040D395B287E5DD8F793AA33A522A68BAEC33F17ECCEAAB9C698E46178634F527C27A28C5E1FE8B79598A199BEEAD90D068A089954E3F074ECDC5D57C5068FAAD925099BE370744F73FB2445706F9BD3992195BEA042CB3ACB718AB2FA3FD88797CB074E42912F66CA373F63337506023AC98EF
	12632C066746FDB887239F619CAC997E09145E99341B6DA1B36E7A8BFBF09F1B83A97B62F31035692EDE73365FCB57880E07D9DB9F33133546A4CD61F1FF3C0F54B58BEC4C874A5807F17F311770F1ED6181DF2F29AAF32770F301EFA39997E5A0224012CC9631D342F7DBFDEFA570AEF0BFB27EC7BE62C755307E4D59CF77C29766FF4B21BD92E0457B19AF3B7673FD154A5D1B07F25924DC9117AB2FE0F24C876786C8DFE8AE57A78F4A3132F094E22B2A6AA47704A4311CED4CCB7CC746E9CA8566E6057D44BF
	73211C81343C8247715975181B25764F165A34679CD6392774A9EBCB492771F1F4AE0ABA187F3F5BCFECFA0027FF0A3E7F314F38BF5406431A5BC737A66E3753FD46F1E658393E35706C7105427A891808B174F1FB9FE9AF593BB84C77720DF93817CF853B69C06D1F51FCB8D4E0E8FBB6604500B7GB9E8FD513CFDE41E52A0A8E30F96A55423057C0AE78771EE199C554B681AD0D6CB59C7496F1DB8968F0E9CA17AB49EA1AA6BA23A2E85BAEB93AAEA322E69504E8B180F5D71F68DEF67227B6F102A47AE5B4D
	7263299B3C76344F186F97FE969F034A67DE30B781B1FFE489FF949C00BD60G7A63D55D4C9FA987701C70747EDA511FFA1215285759A53EEE27F8C9EEBA6300F99EBA0763697C1985CF16335529B457B81018D7B6D1BC7BC0E2DE7947FE4CABDD15305FD5E21ED16CA7EBD88614D7GB98F42F984289A2863A04667523E7D52A4F95C09G24D781D9ED9D37E0E3BE556FE0EBACAF5F5E1F03EB2C7043163C449CBDF6F03CCFD2934ACC2FE4025E9F934A73937458B8EEE835D8A358F0820F2D715F520CEBFC8654
	358A2830AAB32F94082DA387F4972F37218A570BAB470C6B45028DD76C6BAC560B51AA563E19D571D8EDBA75DFFC3DF8AAC0563CA5404B865AF8886C8235G29C0538F6198152807A79BA39AFBEDDABDE00D308657A76E1542277FFC1DE331FC523118FBE83C9E735F8791745FC260331C782F685AACD5E76615D4A3768645AA215CA23090DD9BF5A8CF955823941B8965F9551803B52AE41FDFA84A67E8190CA50F237EEFBA67A436467B6770B7EE4318CF32B85869374E514A7D66B95AE7FE739C2D5A3F0961D0
	077104067C86FF729C2E49EF16433DC75B7D4BAD9BE85D613092260159A06BB2673D2CEF16CCF239E6B84471BE431EC8679911B92AEB75FBA51C57EF6CA47355E4DE3748303B32B97DEDB5F1CE13734DF2982F5555B8279F51B0A68B8EE3EE7052GD9C31B206C819ABDAC72FB5D6CFEA175B09B7F5907A72E6DECEE1F8D123DE6817097832D84F29D494C5BF18467740B7EC9575D2331A46BEED640BA967EA5E09E8B3F89103CA62CC5C2EEA5F13C17878A85AF3EFA1C5095BF999AA6739FD4DF17C33D4A04F849
	267A0CE725EF532CE20A0D7B263546F15FA7665676085998B49E6177E4C7A60E812B6F27DE92EB9E25C70E28907EDAD8B993645786004FB9C2467D9F3DF84E8B9E41BC6041A8D94FFBC2329F1A579AD45CD24A67C4F62D8FE3EBA548BA81AB81EAG72FC0EB9551F7511BA7C1AC7D6050D34BE3CBFDCA464B2E9AE97D2423EE11AAD236C2BFD581EA5C23679B8493DF9068B4E55691C8DA83846BE2AE2BC2F7C9C637969G09679DB519F9B5C06DB5984F0FEB093E8C56447D17D2B35117AC773898A2754D28C93C
	DF6A26781C1A447B25D786B087CB87DEC9AD2E5F2DF4BF1D8A6599821B2F635ABF2B96674797C16B786E8A1A47F7CF50FC2DD3034957BABDE895172CFD07A3586605224DBF0EE01B33EAF11C16F9491D18E1E9AA3B688BF834A88C9939655243E58DE7BA1B4F34B51EE8BE5EF92639333E69CC6BD11E7F26C50D775F15EB3FE5FA16A414DD4B46A83F0EBDD7EF3E5D4662205C073153G3C51BA02AD7099F3DE524E3F8701B2F8B27E6B6378448F105E9B7845FDC6BEE96B838EA37F5CB8FE0EC11F756B69ED18AB
	2DFEFA2E24BE1CE2F92F7F846F4FF27E1C3E6CB6DB7A569187736D4F7A481E2E07A28AAB5BE81E59A32B61F81E192EA646F6F1FDBC1EAB6A2DE27BF30D44EAE3FDE2EC1F24F8C7FDE2ECBF9B40588E82AF2B9EE362F93A2FD4B64019238131738398A741861C03DF05E88E8365798273D2CC62E7DF067DF660FAF28AD2E7A2B65384E3B6DC216B31E1F68FE5F67F45462ACF27E38793F6F183F95F74E414F43568A8714FB3FAE192BBCC7E55D8DC7E61C8B2F936777EF0081E43C1FE4AD1DCCFDC833B61F9C16E63
	BB6DD10FCF57C2F42B8530A524414E208FB1145EECF07FFC4FF7FD6FB67EEE8D467D3CC76E11667FC9036599A16DA8BF8772670E17EE33457BE29BA17DDD893C7CE319F965C00DC09DC07AB16C5B198152C612B1C073EBF5DDF6FB69ED82A69297FCF0BE97B05950D8EC7E0A6E9BD3211E1947B0DEAF84B0DE530FE19C9E25F13D8C4A5982EB2198B9BF679FC76CD701EB0DA5D26704754D840B476B4AEB36E19617D67728ACDEDF5348BD21AA8FE9919D664EF16297E790632E9C4A0DC0C12014636C8EA13F1148
	288345582F63A67D62983371E9BE595FEA475F1DF01EF37FDB02E73C074058FA6612F12F58F83BEA37D944D6DBA333595598BFFF32BA96FEE5DC170BF71FB1DD17333F9A3627F25D7906BAB242458EFE17BBC4B031F7322903F739C3E4EF7085FCBAE4AE106710442B9584833DAC361F9DA57C95D02F6B841CFD01EA005ACE08FB4B618FD9DC1B4474D1CF2F027BAA5ADBBBE21DB3EC9A197C1D537F0D10FB37EE8DFC6DA775A3DEBCC2E2CF570294E45836911A3F79FA3DBA9365786E91B2AFD4250742ECBCDFD7
	A746D30E9F63A9887D1D8AB4976809936C8EE26589A2638F204C1293D6B96E038D24ED6C84341FE3476A62D8853F6B4808185DF5D41E043D8A086DEB25C1FCFF6E81EC66C94C07776F9BF71FC2AE3E2A2A0E29DA3409F7096721FA90730F2C1318075EC16B4CBF85B9A0D075A93CC309ED47781BF3779DD2E33F791D6B5236AB7636D32C6F31D37178EB8F3CE0E7B6D387B0771DF78A7330C583245C8ACD73CAB8E7228A5AAB3A77CE29687D9A7BA60BB933025BA9EC0A5B0B2F47D73E1644D8BFBA32CD3258D3A0
	C2CF022B4EF47741C128CD6E957A29E3E49C1A20EEAF50B420D9C07301B21AB06E25519DD67582481CCEE3BFA47B70FD412FC72DE3FF696864317F4AA83D070618ED9B8A72735A6B23A4EE351E1E30A266589B239B85F6A25C2205FDE4D6337859634778D986FDA9BA0D71736FE5DBA746CF1DC65EC37088E2E39971E37C341D4678391356B98D4A3300961C46B37C7782B86ED927B1267A7A0DE3CC464DAA574CEFE6E3DB5EBCFE0CD95D2EC12CBB580C6D980B11F64C06F2BA5062E65C7376852F6F1B091A6B54
	9B3AE6BD164BEE216731154DAC1735897CFD0A2FB660ECACE70B3540F5A6B3CFBD03F74A15B6FA278C6595C04E96988F20EA2076960C5F29D74A25C96EB65A34E093842BEAB80E317E5DB6E06C1F8AF5CEE9B14F8710783C329743B395E46782E5812DG124EE66695821582B51C4576BDF3A5597CE20119B83FFEB4E6BD3FE60DCDBE3F6E9BB35FDB9E9A1B38374C9FB35FDB9E9BB36EAD4CDF7B7D5797C7355729B7FC4DFAAC4E5E21E7262E330971D7CCF175ECE27C7DC42CA5B160ADB90B73EDC640E83F669C
	648B672EA6BED6BBC85CFAC1B685E8B6D0BAD096500AF39897E7257DC962621C2651DB97F28183F1E18C916E1B7503093E11DA63EDABE8353CDB9922F7B52D4C876F89BCC0F15500B35F5CCC57A2D21E8B3C19ED681B2281C90C53B6436F7B03B7621B0B71F171648D3541462756E6BD879BED66F7C24DB673BB21B6DB727B6D8E1B75B78AEE1B719B85F6EE1ACB73102967B3734EBB90FB18E2E9009D97757E355D3A8FF75BA7DFC79E301B2FA38F5BA72EA30F584D5711F43BF19DE158AFF83DA1F1994F702769
	3900652E2765005CEBF81774DEA09EE35B8D3F9F705F78F8DBF8C3ED3071FBA000E3AA0BB11D9740B10D5850C7B9CE6BB14DF7CEBE261B1D66E33A55B9F1CC0B1D66E33A5DE99CD35656921A47AC0036E61C4773FDC58C4F772B4E0B7B28A1F21EAC3E1019D781548664851A861486748450E220D5C02E0B104783B5GF90026DC44BAF4DAC79A1453011682C96DF0B6862A816A821A821486148E3494C87A8278C0B5DFE09D374416F27F137AF1AC26DCCC3E3EA311F6CFA6B30D4B2C62FDAD6665122B3061CA10
	F942E04FEBE1FBB1772147204B7A7A8F222F6F9A645F3B06B9B0194CBA7EA45F1F30BC1C155F398A5D4D89658D065FF996BAE4BDDD4F6391B1E6D75ECE76ABFE8FF05167513D8CAF0A2D0F630D8A09E9894F350786A71E194F8D6299368BE2A6861486B46F8BF687456EE0CE450A1374EB519746BB18554D440FCDF237229EBC4831D33571EF2C9A3530CE5642E3BEC575F07E72FEFA36E6CA9C3335EC11923E4BA207A848FE3BB5347550C7AE21D7D94930DB8ECD500B1B63FADB54385E5E8A06E577EC0C6B6930
	BFD18D465B23465BF70057F1B9EF03C1DE8B9A7BF328ADFE575424759E73298A55C57BC736CF600FEB5F5F1EA57E2C0D689AED49B85D2F5FAF903AC2225E2B387B5977B8BF9DB23EBBD84A475179A5FE2FB3F35848AF7C1271EB847F0391B37DB641DFB7EA64FB397EF441FFE54CCCFF1660BFA319591FAF78EDB6B37DC502DFE5B753DFAA780F5B4D74D7897EDD8EB3FD57A5646FF21A699789FEC10E19FE39605F16E3265FA0785F4EB5725BAFB17E14CBF8DFB4BC08B9FAAAE00BAE61F97F19CE137B2263A11F
	A7613E6867621C6D6C40F376CDD17A9DBB145B00548E3C53714648FAD02F10EF114477GBD965F8350A8E4B6DE99C29BA99D583EEDBB0A4C755AE01F26514DF47FE9985F5BAE6840FB0776912CEF15282FD75DEADE9FDD816A34986FEF9D3DE3A1733374AB2C2FD8427ABC824B35215D19823B45A66EBB84B6E0C7397C4B087D27DDFC2FA430A58E149B95186C9477E682BB1503F2AB84161103F2A51D585FA3972C7ACB66F63C3F0F6422EEC7A76E71D3A61C112EE62F5BA45DFA1DFACF0E53CB6DCC3E4F4F6E44
	EF70FEC763F0C1E762F9758F94DF5819F8DE7D53A84E05AC60D5F6E1FFDF9FB2EB774CAE560E39DDD66F65968FA76B6F9316FADF0D1869AD6672ABAC750E0FA633A74919F985C0BB6444BCE61BE426D74D65DAE4ABFB6BEC496CF9AC75261B6A45387CB4CB3DC1FBB2FBE916FA3FB7551B476597DA6A7D46114C5E92CB3D9E27195ECAAE6F6C3652EB49C9E62F50D26FB9D33DD2AEDFE32977F82E19DE9B17771A683173473F52B5FF9478533A71CC32BDCA4FD4D01E570D736677749DB17B6D0C45FFFFA7FA634F
	16F75FD09B2C4FB9F4EFD9887DDDAC7C50B70CE733E5827B2D40DCEE44F43A8715C239DAE03F9EC139C601DD9EC5CC955857941B8E65B9827BA54552213CD8E055925AD8AE309FC9626CA8308FED0895FA907B9B1BE81F40DCF651BE01FDC7E02A405EF2206EF401059DA8B7D3E05982CB9758953A27AD0372AA013D68C4B9C9C12CB7873192012D20D8A3145B85F6B69765BC82FBBE9765268A6C409A14CBD5B0DF795DA07966A124C5436C3B2EF5B446C99EBEDF489C0E12F8F3EBEA449F88736F3F5A2262FBD9
	A1D7B4BC7E7B097B687F74326FA1EE9ACE76BD8413499DC9A6434FA2A363ED7CDBEE5C464A247ACCA6B2BA390D6F0F526F8F9572FFA70A877F0701757BFE9A037F8BFF0BC53F170E669B65F5453FCAA1320F0D1136AE075F2C2D1BDD71364E13A6EF6BC15B645F1CB41D0F57793EFD72BAE7A515E1F5EA9762F57E39E372BAB7B8A777E92AF37C387D6545380D7FCE2A4FE43E4E195C06A3F73C0D275A63B63C3913777B35AF62724DEBA63777E11464D187C4391F16770972AFC66D705C33974BD9E324FE7EFF56
	0A715B087211A6BB11B1FB87D859437628461E78F7F46CDD4172AFC9FFE3BD78DE207CCB729D0255FB81724DBD7B2A816F62D6FCC9A6CF18E36C7F725F55E82C3FA6656812830E443A144BBE6D50253CDDF85120CB6BF7710B00E2F203E0ACF3BEBDC3971397EBF2GAA49A76779F8F18D9C51FB60082ECBEB49013B18CA6B681B1DE3C65FE45F6C367C46F0C1AF73C9C6EF7C7DA8730DC66F34733D108BFA51BF0F8777D8792736DBEF754B2A2A4BEAFFED40C35E09578D69CA7C1EDD9FA07507D106B399EFE400
	343B4F20737FD0CB87888141CCDC3C95GGC0C1GGD0CB818294G94G88G88GB2FBB0B68141CCDC3C95GGC0C1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7695GGGG
**end of data**/
}
}
