package cbit.vcell.desktop;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.event.*;
import javax.swing.*;
import cbit.util.*;
import cbit.vcell.client.PopupGenerator;

/**
 * Insert the type's description here.
 * Creation date: (1/26/2001 1:59:33 AM)
 * @author: Ion Moraru
 */
public class LoginDialog extends JDialog {
	//
	private Boolean isApplet = null;
	//
	private static final String USER_CANCEL = "User canceled login";
	private JButton ivjJButtonCancel = null;
	private JButton ivjJButtonOK = null;
	private JPanel ivjJDialogContentPane = null;
	private JLabel ivjJLabelPassword = null;
	private JLabel ivjJLabelUser = null;
	private JPanel ivjJPanel1 = null;
	private JPasswordField ivjJPasswordFieldPassword = null;
	private JTextField ivjJTextFieldUser = null;
	protected transient java.awt.event.ActionListener aActionListener = null;
	private java.lang.String fieldUser = new String();
	private java.lang.String fieldPassword = new String();
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JLabel ivjJLabel1 = null;
	private JLabel ivjJLabel2 = null;
	private JButton ivjJButtonRegister = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.WindowListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LoginDialog.this.getJButtonCancel()) 
				connEtoM1(e);
			if (e.getSource() == LoginDialog.this.getJTextFieldUser()) 
				connEtoM3(e);
			if (e.getSource() == LoginDialog.this.getJPasswordFieldPassword()) 
				connEtoM4(e);
			if (e.getSource() == LoginDialog.this.getJButtonOK()) 
				connEtoC3(e);
			if (e.getSource() == LoginDialog.this.getJButtonOK()) 
				connEtoM2(e);
			if (e.getSource() == LoginDialog.this.getJButtonRegister()) 
				connEtoC1(e);
		};
		public void windowActivated(java.awt.event.WindowEvent e) {};
		public void windowClosed(java.awt.event.WindowEvent e) {};
		public void windowClosing(java.awt.event.WindowEvent e) {
			if (e.getSource() == LoginDialog.this) 
				connEtoM5(e);
			if (e.getSource() == LoginDialog.this) 
				connEtoM6(e);
		};
		public void windowDeactivated(java.awt.event.WindowEvent e) {};
		public void windowDeiconified(java.awt.event.WindowEvent e) {};
		public void windowIconified(java.awt.event.WindowEvent e) {};
		public void windowOpened(java.awt.event.WindowEvent e) {};
	};

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LoginDialog() {
	super();
	initialize();
}


/**
 * LoginDialog constructor comment.
 */
public LoginDialog(java.awt.Frame owner, boolean modal,boolean argIsApplet) {
	super(owner, modal);

	this.isApplet = new Boolean(argIsApplet);
	
	initialize();
}


public void addActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.add(aActionListener, newListener);
	return;
}


/**
 * connEtoC1:  (JButtonRegister.action.actionPerformed(java.awt.event.ActionEvent) --> LoginDialog.jButtonRegister_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonRegister_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JButtonOK.action.actionPerformed(java.awt.event.ActionEvent) --> LoginDialog.confirm()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.tryLogin(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM1:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> LoginDialog.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (JButtonOK.action.actionPerformed(java.awt.event.ActionEvent) --> LoginDialog.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (JTextFieldUser.action.actionPerformed(java.awt.event.ActionEvent) --> JButtonOK.doClick(I)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJButtonOK().doClick(100);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (JPasswordFieldPassword.action.actionPerformed(java.awt.event.ActionEvent) --> JButtonOK.doClick(I)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJButtonOK().doClick(100);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (LoginDialog.window.windowClosing(java.awt.event.WindowEvent) --> JTextFieldUser.text)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJTextFieldUser().setText("");
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM6:  (LoginDialog.window.windowClosing(java.awt.event.WindowEvent) --> JPasswordFieldPassword.setText(Ljava.lang.String;)V)
 * @param arg1 java.awt.event.WindowEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.awt.event.WindowEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJPasswordFieldPassword().setText("");
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Method to support listener events.
 */
protected void fireActionPerformed(java.awt.event.ActionEvent e) {
	if (aActionListener == null) {
		return;
	};
	aActionListener.actionPerformed(e);
}


/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCancel;
}


/**
 * Return the JButtonOK property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOK() {
	if (ivjJButtonOK == null) {
		try {
			ivjJButtonOK = new javax.swing.JButton();
			ivjJButtonOK.setName("JButtonOK");
			ivjJButtonOK.setText("Login");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOK;
}

/**
 * Return the JButtonRegister property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRegister() {
	if (ivjJButtonRegister == null) {
		try {
			ivjJButtonRegister = new javax.swing.JButton();
			ivjJButtonRegister.setName("JButtonRegister");
			ivjJButtonRegister.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJButtonRegister.setText("http://www.vcell.org/register");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRegister;
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
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelUser = new java.awt.GridBagConstraints();
			constraintsJLabelUser.gridx = 0; constraintsJLabelUser.gridy = 1;
			constraintsJLabelUser.insets = new java.awt.Insets(4, 10, 4, 4);
			getJDialogContentPane().add(getJLabelUser(), constraintsJLabelUser);

			java.awt.GridBagConstraints constraintsJLabelPassword = new java.awt.GridBagConstraints();
			constraintsJLabelPassword.gridx = 0; constraintsJLabelPassword.gridy = 2;
			constraintsJLabelPassword.insets = new java.awt.Insets(4, 10, 4, 4);
			getJDialogContentPane().add(getJLabelPassword(), constraintsJLabelPassword);

			java.awt.GridBagConstraints constraintsJTextFieldUser = new java.awt.GridBagConstraints();
			constraintsJTextFieldUser.gridx = 1; constraintsJTextFieldUser.gridy = 1;
			constraintsJTextFieldUser.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldUser.weightx = 1.0;
			constraintsJTextFieldUser.insets = new java.awt.Insets(4, 4, 4, 10);
			getJDialogContentPane().add(getJTextFieldUser(), constraintsJTextFieldUser);

			java.awt.GridBagConstraints constraintsJPasswordFieldPassword = new java.awt.GridBagConstraints();
			constraintsJPasswordFieldPassword.gridx = 1; constraintsJPasswordFieldPassword.gridy = 2;
			constraintsJPasswordFieldPassword.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPasswordFieldPassword.weightx = 1.0;
			constraintsJPasswordFieldPassword.insets = new java.awt.Insets(4, 4, 4, 10);
			getJDialogContentPane().add(getJPasswordFieldPassword(), constraintsJPasswordFieldPassword);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 3;
			constraintsJPanel1.gridwidth = 2;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.insets = new java.awt.Insets(10, 10, 10, 10);
			getJDialogContentPane().add(getJPanel1(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.gridwidth = 2;
			constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabel1.insets = new java.awt.Insets(10, 10, 10, 10);
			getJDialogContentPane().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 4;
			constraintsJLabel2.gridwidth = 2;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 2, 4);
			getJDialogContentPane().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJButtonRegister = new java.awt.GridBagConstraints();
			constraintsJButtonRegister.gridx = 0; constraintsJButtonRegister.gridy = 5;
			constraintsJButtonRegister.gridwidth = 2;
			constraintsJButtonRegister.insets = new java.awt.Insets(2, 4, 4, 4);
			getJDialogContentPane().add(getJButtonRegister(), constraintsJButtonRegister);
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
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Login:");
			ivjJLabel1.setForeground(java.awt.Color.black);
			ivjJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}


/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabel2.setText("New Users may Register at:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}

/**
 * Return the JLabelPassword property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPassword() {
	if (ivjJLabelPassword == null) {
		try {
			ivjJLabelPassword = new javax.swing.JLabel();
			ivjJLabelPassword.setName("JLabelPassword");
			ivjJLabelPassword.setText("Password");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPassword;
}


/**
 * Return the JLabelUser property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelUser() {
	if (ivjJLabelUser == null) {
		try {
			ivjJLabelUser = new javax.swing.JLabel();
			ivjJLabelUser.setName("JLabelUser");
			ivjJLabelUser.setText("Username");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelUser;
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
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJButtonOK = new java.awt.GridBagConstraints();
			constraintsJButtonOK.gridx = 1; constraintsJButtonOK.gridy = 1;
			constraintsJButtonOK.ipadx = 34;
			constraintsJButtonOK.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJButtonOK(), constraintsJButtonOK);

			java.awt.GridBagConstraints constraintsJButtonCancel = new java.awt.GridBagConstraints();
			constraintsJButtonCancel.gridx = 2; constraintsJButtonCancel.gridy = 1;
			constraintsJButtonCancel.ipadx = 14;
			constraintsJButtonCancel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJButtonCancel(), constraintsJButtonCancel);
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
 * Return the JPasswordFieldPassword property value.
 * @return javax.swing.JPasswordField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPasswordField getJPasswordFieldPassword() {
	if (ivjJPasswordFieldPassword == null) {
		try {
			ivjJPasswordFieldPassword = new javax.swing.JPasswordField();
			ivjJPasswordFieldPassword.setName("JPasswordFieldPassword");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPasswordFieldPassword;
}


/**
 * Return the JTextFieldUser property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldUser() {
	if (ivjJTextFieldUser == null) {
		try {
			ivjJTextFieldUser = new javax.swing.JTextField();
			ivjJTextFieldUser.setName("JTextFieldUser");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldUser;
}


/**
 * Gets the password property (java.lang.String) value.
 * @return The password property value.
 */
public java.lang.String getPassword() {
	return fieldPassword;
}


/**
 * Gets the user property (java.lang.String) value.
 * @return The user property value.
 */
public java.lang.String getUser() {
	return fieldUser;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	if (exception.getMessage().equals(USER_CANCEL)) {
		System.out.println("\n" + USER_CANCEL + "\n");
	} else {
		exception.printStackTrace(System.out);
	}
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonCancel().addActionListener(ivjEventHandler);
	getJTextFieldUser().addActionListener(ivjEventHandler);
	getJPasswordFieldPassword().addActionListener(ivjEventHandler);
	this.addWindowListener(ivjEventHandler);
	getJButtonOK().addActionListener(ivjEventHandler);
	getJButtonRegister().addActionListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LoginDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(251, 199);
		setModal(true);
		setTitle("Connect to Model Database");
		setContentPane(getJDialogContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void jButtonOK_ActionPerformed(ActionEvent e) {
	fireActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
}


/**
 * Comment
 */
private void jButtonRegister_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	if(isApplet != null){
		PopupGenerator.browserLauncher(
			cbit.vcell.client.server.ClientServerManager.REGISTER_URL_STRING,
			"New users please visit "+cbit.vcell.client.server.ClientServerManager.REGISTER_URL_STRING+" to register",
			isApplet.booleanValue()
			);
	}else{
		PopupGenerator.showErrorDialog(
			this.getClass().getName()+"\n"+
			"Cannot invoke BrowserLauncher when isApplet is null");
	}
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		LoginDialog aLoginDialog;
		aLoginDialog = new LoginDialog(new java.awt.Frame(), false,false);
		aLoginDialog.setModal(true);
		aLoginDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aLoginDialog.show();
		java.awt.Insets insets = aLoginDialog.getInsets();
		aLoginDialog.setSize(aLoginDialog.getWidth() + insets.left + insets.right, aLoginDialog.getHeight() + insets.top + insets.bottom);
		aLoginDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}


public void removeActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.remove(aActionListener, newListener);
	return;
}


/**
 * Comment
 */
public void show() {
	BeanUtils.centerOnScreen(this);
	super.show();
}


/**
 * Comment
 */
private void tryLogin(ActionEvent event) {
	updateFields();
	jButtonOK_ActionPerformed(event);
	getJTextFieldUser().setText("");
	getJPasswordFieldPassword().setText("");
}


/**
 * Comment
 */
private void updateFields() {
	fieldUser = getJTextFieldUser().getText();
	fieldPassword = new String(getJPasswordFieldPassword().getPassword());
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G610171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DD8D45715A4E2E2A20D441F86D393095594D33424B5A96D3A0DE96D4634A4B1DD5B58065D122F36EBF6EDCA37EEEB33EEF299C0C1D0C8A455443F8191C181C1EDA50424A0BF819D11GA1D4C9631B19870C8CB3631B878C0AE64F39BFEF9E439BD0537865705EBD775CF34FBD675CF36FBD77CDC84E3E4854A4D38EA1C929047FBB11C4485A7789D9FAA06FE1018BF7F5E4905D3FDC005748333DA9
	700C043A48C7BAB232495BC9E9D0DE8265102FF5E46C00776D641F5B16D543CB10FEA2206E79EB3D996377F3D72D68E7B3ADFFE7CFA6BC378144828E1FA9EE6C7FA7FBF6F2FCBC478BC8B291320A527CF64FEE0EEB04728500F00079EEC63F941E6934674915951C2E6B6EA8327E870AE9AB72517194305B435AD67327193C1C345603FD3D35B7133771C1B986G871FCC5E3E188F4F2A163E1E77ECD607DABAE8131D4ED23B6C6DD35D1E4ACA0B3B47612AF3C8CEF70F7D9272316ECD236D88A906F2A2454D4EC7
	5ED1703E8CE035063FF0G7147150E8C8B00DD9163F977E0951737734AB7491A1E6F27E785994F084246B1DF710F47FC481A7CA6557D53F528638428CB71F6E46C8428826882988158C5ED7E635AC3702C312A356E7EFE372B4663B96934ABE75CF659896F1515D043F12D324D2D5889E1F6B56FADB610E7BCE06DCAEF389D53C954B1E163685645A47E9BDFB4EDB650C9FC549553CCAF53C534572F9356F7CF503E633C23FDF0F45F9F91FD5F6445231BD38D7A5E7D13231B93B81F753A3E73D53475D6EA6B9CF8
	CF627E4170BBA83ED707E77EB8177A8C16FDD0972F8A5F685CA6E431244CA3596577986AA1623BE1266A81A6C367C020AC09344F50815667EB1AACC985080F516119AC5D149E4B71D057BB5811417C6B39861C1F6B211CBD5411D188D08DF081E084A0FCC878585E3AA35769E3AD12221E9CF0B86D32C2D85B676A7E84CF5927AAD2134B2BCAAE1B5CA1A98E496A143D0453A454218E2C235A7D127A3E8D7AF8DFF6490A24BA5CAE60AE754B2A2CF8994DCBF5B8A61BA2535AF6C73F8C9D74FB08607B7B3A6D70F4
	CADE75C30FDDD2E5BF852BFF258EEDB2A8F5GC7E0G6FCCAFA90DD40FB07E95G2BB5BD8C57A33F533262121D98BA5CFD831EF69C89A974211E2F5158318E68D3FCC2472489F1CEA8077A841F187A57F5FC6A9CB694DFD206459C5BFEGE3F004CF784CCB1D42E76A36AEA245BF1EE568B361603FB1BE66ABABFC01F3E78F6509F23172027C4E9B1C47019061B31EF37ED8711D4F5CC772B8F5CC3F2ED3E82BC410A5F338A323F8D868783F6A4D4CE7CFCADED9F869D9859CCC367BB54E647C7D81B357A716D167
	387EE471B90331E23FB6264D85C8DB877DB881E682AC85D88310F939A363B8C057E521E36701901D1DD2F57C378574B1119E76FF869DB2D926D1D904BE33F47208B81941E51A085F75489B8C18EDDEE8407E65C1592556499E59E50797AF2FDB5B003270F8516D565A3CC4719E08AF35B0213D9AFEFD834AA3292A62308E28F21B2C2A8ED70F3FFEA36D4BBBE07578C31368AB79FD5401FB88620BC61F76BE4ECD1B5B65122913E8CDF87D969A9BE4275C8FC2573A87DCAA43E7B7627C6A1307F9EC4AE95CC4B036
	B887B04E313DC80C04BA76A80EFEF0BD8C15E475E50C9B33766E5469A894F038270A0667324BA2868C6D97B1A07F72C2326C930F49E88D0BD8B04DE43E42666571AB7E9840E4CAEDB216BD0346A80E77B2D907E414D537BD62736DAB2711C7BB706E858839A2665B6DCDD8AFE29A1FE835EE271329D1532FFAAA9B6DA1DF9A10E1A94060F9DA535915D32F8754F5F07D317AC927D0A627BE6E0E207E7EDAA0761991A0CF8C404AABAC86E70FB09DB03A640302CE05F298C0B440BA004C2B9D9979G0C2E31D050D5
	G2E656AE8BA0FC6B7DCA468C22F327E66DCE5FCD743F32DC67733C302EE230E8E7966DC03BD9DC07CB524ABAE96F4AD57D8FF3D5718AFC4DD530FA3D123AB7C1451850E17711BDFA2682A012E934077A963977BA923E336EE3D043623FB640B522064AB758E419C2A2CBC45ADCF885B9B3FCA57B77447149031877EFAAE7AC395145B811CA142DF1F5DAA7640B37B22C87C0B651B036D69C3C318ACD1A1FEFFDD721B1ACD4C5FFEEB41BE57C25D069061EB2F1C522F43B5DE2F5B6620DBG6EA70BCE6B7D19613E
	FEFA8BBC3B9D0AD7ED02006393BE95FB9A6545BD82C773BDD3EF01583FE55E5211D18790FF8B62B7166468E49F815CFC0095G49145FB385C7B19ED127AFE50F96D969F6AB7D323D9035DBAA8D2925B4761556506AFAFCAF9236F8F0874AE9F7F8BDEE2FDCD854A176300B76D673BE63964FA3E952E4135119A26C459ED37E2DCC0F16DB7D7A0C19EECDDE8E4046D6FCB0135BFEA650445DAA46F6601CFEECA913E0BD85E819A446B663604D0E0D71B7D598694EBEC91C554237607C363BEB1D8EDBDFE1938C1E79
	5D0F8E0AB3198FE85998AE950AB144A0EE1298435C9AFD9F66D0B6868BBC992F4DC5025705D041EBDE11609581381521E23C69074AB072BADCF677D0A9FB54BA5DDE189B01237D08D632513236D507F0BDD0E1A1E37DFEFCC8741BB6D9741BFCD874DB8DB85FE421073BDAF653B82836C373C28B6D4BA939FACA5BE015F3753CD35441F84EBEACF846E8BCBFAC96BC57824EFC1B984B52624F66179FBB8E62F814E13AA4F0218C1B00F85CE767ED577BEC5527FFB77459A9D93D264E5B182F065C667759C67057AE
	4DE75FAC9173B18EE8D2EE9763FD2AC40C378AF06A6D428F7EE88A74B57403685B05BE0E17FEB6FDFC7754FB703CF8F2C0D55D2ED639476105F349A7B5810CA6D0935F1B256C43F36D325B0D77532BB8FEADFFEE04A7BB6F956461583327841EC94BA85EBCA570CC9A1BABF4D886F5B32708B34A4AE65CFFEC00F24E9D20A700BAGBBC0689DA266D6DB0E11894E00CDAED001CBF2360D5AEC093318857B057D91AF3FECC1BD527D9191FAF095CC14DB083CE3340ED83B11895BAD8FE847EC185596E45DE3D605BA
	36BF8B2B566F4F626FE03EC46E84FD5DA9624A3152B44E3778CE3186DEED502F01CB332E1AB28C7C9F41FEA713AF74CE5DD93F3FFDB3334FE59A8F16C3DD8240866CF7EAC7C685C0BB00EF2A305124B36FCCE4A36AFA6DEE660735013BDC2E4F2387837599B1F5347E0C6DD0BD26DDE9C0F949D4A3BB9C207BAD83BBE07CEBF048CEBB33C3CFDE209DF04F9387BC93268ABBF41D93B1F10306FB3BC25826ACCC1C4D1E2332760685BF438682EBB7D4F8F33A19BC26DD20EEC6420CF47368DE3331EEDA6094B464D6
	6CD4BFEC1CBF2991E31F290DB333887B0D3B61F1C6955D5CB89F9D53AEF01C89E1C66D2C072EA72F38C1571635CBBFFCF3F26E9853AED04E4CAF98353B1F364B7F420D6A73FC714D49699853EEEFC039A2C0963EEF7EG67D48B54B981C281A2811683A4G245CA5E2462AB36F126B0B993500EB0433575828A1E45DD7F2F3713BE442F679F799357B57B16D2ECF27AF0CE99728532A007E587CAACBCE63BC5A211E657CF66DC3DC9814E3B41CC2F14B219C2F61FEEBC69C198631FB1A40D566A22E904AD55344FC
	AD4E43FDE323D67E8BAD63FA43F3EA391FB52796885857E87DBC778F6FC31F47E3FE77536660B92004E6639C502F1A0DF3C06B1ADFE3F860214FC0EBB9226689F2C44DC6B922BDD46E7FD05FA0867920E629DF66635ACE969424F31AFA55FD261C08587D3BA2DC2F8CE3B71CDC19CFAD2F96EBD36FB4F126BEFD8277E911D00E85D82979C2C52951BCD94B6D37F15A581844E2012F8D7742596130EF8128836882988997B1E07A598977F6753EF176F621AD41EDB935455816F737E0BE406B9ED0ECF2203DA2DAF6
	EAF57EE853244A7D28F6C673GED2FCACA0FBC465ED134BD2B8B567E6116E0BE416C7E641F3228CFF93D32178640BAC915383D9D877576BE2645078F778BDB4E8997364C23F7AF2B203C8EA065EEE1CB695CF8B1EF775DE3ED495A3DD7E1542E0C53579934E33166A5FA066E047A5E3B4599E8F121FE9C4735BE1E2F99CF361120FD6CAF92FD04EBFD7C3CC85F471FB55AE1FA9E1E8FF4319AED75E1BD6DDFB45AC768B9F7B9502D52E827976BE8EB0FE9367F5F8F027B222955589753DA037BE2D66B443E3833B5
	38AF6EEB1D58978BDB0379A29BEBC709DEAFC7B57C16D23D8E4AB57B7DB9D9EF3F84EE2F757C19B6FD2C7D983F237BB0565965EEE94029E226C3BE6B61D7EF00EF1F0E36B833CF78786E69A2DF7170901EEB3A95299F44EE7348B6D2BC83E96F58A7766B91C09B8D9087308EA0F30630E3DAAE7268F75BA52756473EDC310FD5B53A8FA81D6AD01D98F7C3E760378B371CA31A6C66E318BB6699CBD034CA578217ACF472E4F520CF2F186157C37C0C31BAE1E76947E8ACCE1C414E52299A7E87941FB2530FE7E76C
	8FCE08FC44CE289B19A9567A3374FC18G65C48D97D3AA76899BE70AD89C52965CFFEFEFB3765FAF34992F0BB35A46DF973F54964C6F184E4E6D8F5419E516DFCF97E685535957E94CD3E7855ECFFF0F62A3E7855ECF97EA7753F1D05779C5219F5FB9211FB59A6E438A21476377885C2395022E7A9E61AF290538EF02E9D74EFCEAD7A152BC89902E357B6F9A51AE92F00C4ED2A468926F917E1D731EBED741FC6FDB677D3E97F7D874456A623B300E75E121FBC692816F91225F2C924465C3793806D3CB34B1C4
	08B158E9DEC837F560F9C6F0EF3CC9D73D25F5CDEDADE75B6ABFB95B7CC92D85DED85EA1F2C87FBDCD4516DB8D6F0A9042A37877A77C99766A6CA43616FD0D4EF3EC938FF5B93391770ED91FD3C39927A531F3932B6F9FD50F76ECA27A7A39236AD156BF0CEA7F6C28FA1479D95E1E0D6B0787C4BEE5638FE308731150205F89F54D6679C47EB4BFBDBBA9BB22E593536BDFF2E9EED69910995FE74E6B7515FD2F53149404EFAC6737DC70A5AFA7B17BA550FD5C8640E75EABECFA6E2430E9593DE25D7F7E894405
	C0B9C2432D24B83C47413BAB067B6844BFFA8FCE63B0749922756B79DC7AC8653E41F45DD7A07260ECEE7C686F2871D57722FE1EA82D3BCEBE88CB208D429B5CB6AB60FD8FFFE77A3E34DF58E07717048D927335F975A5B16ED8FA5FEF077288C08440920055G6BG489C7077B90276C9CADB866516B96CEE8F6F6D705E42836570B96CDE0DFF9BC56F58EFC4D758AFBE31CF14877B95F5C21E88AE677572E432FCF04332086F9232383E75777C625B004D5CAE574B1359F95A39716C1C9CGB1F37CF6463E164F
	E16B858273A153853F8FBDD5F1A33C83FDE8A5702E676FFC8FD4084F7232B24EBF7D64C47C75FE9A9FA0FB22CEF6465FD6A47833F2058D67C7B7ECD8587AB3C0570B0D50AE6DBE318E94140C1EC327A47F7827168879AA6E9B6BA3C95CF6C2C68B50A00871DF384FAF9FBBAB7EC1225FAB81FE4AFDE2CF127B71E1F8F6C8977949305E6ED05D8ABD9A8232C5F143AED19DE6D8FAE34FFD70F5734DFDAB7878E6916717FC7BA192FBDBDD503CF9949F738A7E0C0D1B1D34CD9B0BE423779AD017F3FFC7C685C08B00
	9DE064FEB1365E0FD1C6BC610278B52AAA59FA692D31B878F6B864A1BF9AB7370CF7289DA5E5BC96FFFCA018FE0043A9D83F4FDAAF42463FDD6A517CE04AFE7CBE44A98D3B87D430777D5457EA44FEB99C4AD1GAB81565C4F628D337725F17DD1FC43A46CDDB7976253DC3FBFBA67867AE3DF9E6A1C5FF81CBD2D617F2FEA2B9FDF0B1F6D617EFC9F2D03B384717FE3BED954CDB47B3D10A96C377B2BDFA6F54F55197687311FEFAE375FDC7FFA9A5AF5BC1D69E36EFE218F92497431931E45116C7E25AE125E67
	9E58E940D7BFBF379367963CD3BFE5112C3273C3AFEE00997E4C875BB43CC827EB7374EDDA275DC0685BD9AE61B70D23B370016D7F685A4CE8DC32331C6362F3B6EB7D89DCF3169FF714639EBE1D65573D38DB64F768C9857ABDD29857C5CC243877FCB14928FE39D67FC42ED17DEA2D7EE92BD17DBAF80E35FD62DF196D23FA7CFE7043DEE268A702B665221FB64EC5B49BA517607767AEF9482CDD19B6C0D0AC4C2BD160C817D7ECAE077F0E979111EBD9DAA24E216481A359C49F3F41E3124873C9B8A66D3C43
	C21578960C0E5B61F228501D63B2BD74326BF6B65F9F73E0FD8574558E6083888308FCC07B7E491AC7E89CB108A175769ED96C59C5FF97ED414FF917EC93FFB3FB5906BEEDF5030B74A3FF018F3121BF29EE8FC5B25CE49B4ECB4551532BB2D2111B3351D8AFF7D3B4BB0B1557086F6817425856BFA04EC06597C65D512101AAAB5FD79C7613D20F05C6B1E617619AF1EEA81EA7F2E0DF21BC6DD09E8118B9CF5C0F7A8AC41C08FEF09ED979CB63EF04925A2F1A2267317830F21EBFCE9CBFE4CDB67233603E7002
	0A6F4C1E4F8CE09C4D7CB26C9381ACGF6G9FC0B840FC00D800B800B5G9B813267F7E46483D4GB482F481F8668B7B3B3A51B7032D11DCD15489944901EE404F3ECF6C977A8E85DECB670BBB4B2CFC21477573059E5F255F1497AF0058875035C06871E21FBE5E22EE021D0B5485CCFF918B7CFA5CC634FCF5F710FCB50DCECC2E0A82B16FD6AF90321ACD82177D20403DC2F3B838EEB7EA38D41B38BF89F9D06C199F9FF597938B783887475F6BB2FDBF6189BEFF1E76CCBCFF1E7350EF44406F5B07BDBC8F97
	6F41B5595D5D6D1535F97A02A7DB43B5F9DB5CDE87C686A67B09F17324C6405A555FE43BEBB75C0E65DDF61DC43FC9F8503F76B3FCBE452F536119CFAE52E24086286B7A0A78CE6451A1740FE5D00E87D88F1099856B83C0E31418870317701B29F16F925A5D9E8B984E29CBC931363F9888EE537F9C1858263F9BB00E09FF9C989B93DF99B00E09A9037A1848F06903685BBDB007BBB53AED9A6EEF9AEE7BE0BA47FD64302B3D9A7E4D41AD9C5FA8E307DAEEF890659A2238CE0E4B57617E46F1C5036817B064DE
	37A25A168E52F3034369E4652314466182B75574F1024A6870C8BA392BB45C5FB43FFAEACC1E6F7A7C71791BEC1713FFF3FE7CB00DFFDDD101FE7CED0A6F0D8A74635AFC6147AA542D0E92FE1CBDCC635CC2589B830CG4C840882D836D07871B303F82790AC1E373A5DB43B0C09E6886732220B6CEC0C6F9AFE1334EAA17F16EBE1307B1D83A623F69BB9FD76C3415AA56799354B6774957034GDCGD0B90E355BE253371B426B6670E70CC1FFAC2F321A65AB9FE2BAE738FF2338F80EE37CF3CF926D7D106E7D
	9BB51FC76EE934CE62BE57BE181FBCB694BCE67D4B10F13EBEEE48B8DF7F2321716F29FFB6947C378CAF8C69FF4B40467F4DFC3F3E17695E1BBE736FF2A6563D75F36D03475561603AB78FCF3CDE948D9B2F97C706472E9747070D570B4AE17DFA4166C1FB01382358701048C3FE2BC064A1D33EAAF0D5A6C1572E616EB3893A8B9A6E2DBCC19723612E64893A259A6EA1AACBE6B46CD32385EE3B95F195D06E54F0BD946704F238064B3509FB0A799A2E165E3B46C2F905060BBAAC7ADD95AD4E86927DD61E1F1D
	685247703BFDDB7DF83174713D78424B589D4BE92CAC18A827B1DE8EAFFFD1E0FEB06E0A1F779253C43CD34761DDB60677EFC67C3C2767895ED50B0CF3EFFA7EFA5E08EBD994180BF91C7AE337DB11FB9477004BAE6EC3FFCA6D5A85743D0BC4CE7010954FB830A1F0DC46B548D963F474387054495B54D94579E10A56661A75CF235AE0F2C36CE8C5DF3335F6310B441DD8B3750D35D0DE2F71BAF7F8F4CE7275EBFE3DBCF6D868A5FF3131DE52F5FA29G9A8421F34B62C01DD7FFCA34F79B3E729C4077724439
	23935A307A5B127C346BCCC453ED86BD5FEB3905DA37532D14DA1D122D0F551F37661251F7EDED7857FF55D6DB5F5CDE5F2A710B39EEFA6E13E77C321CB8FCE3390E4443E21C610B474BF51CB90B6FAC7E2E98359FD8816D628DECB1B6EE7F4F286FA29321CD4A5746EF4762424F73466E8756646977838C17D9BC166E35E2BD9D7BFF99E4F43F869B5A68DF13D4153C49BF1CB02BEEB3BDC91871E39C2BC40FE9743FCDACC32912D47A7384156C69D5D5CF6511A3C3C3C37CA77FEE25670842F3E9DE05E4E92AEF
	22FB660441E315493F78F2320924B411CDECCDE8A2A998581BC8AA0D7BCDA49956849F7E3EB8C58F5ECB649A592443ECB1440EC72B102D23DC617B8F7C79D7F17F5E61CDA61B1A481BF8BB50A6ABB4B1A55BF16C7C974B8DA45D63066136819E047552C72AD79DF64A9612535D0A3F2403715AEB4416422B12B768EF920E8C288E6711C6495B2B6212780E05243870375564F5D60DBFB7B942FFEEC2C71CD4F301FA9A1D41A06FF6AAAFF2A95538EC7F272C8339275644A693D3835962F4CB768649863B66A6122E
	76AA3237576D347BBCFF2D260C5206C402G073FCB9BBE8E3694A313A6F41F6F3E302DEB37B640EC76A96488254440D32AD158EBFBA5853A1215A695BFD8F1AB79DA5DADFB5502275E5F51E5A2C9FC10E8022DBE0F4A942679827D05CD83C82D7E6C57CF4EFB3EAA9142C7D283C925777E50F1361F2AF9205FAAAB37C54F5A13B1B3E1AA4A65C175D0633E865E58AD75BB1C437042BEC751706CD8C70BEF2096C4C5BA6CD2DCDE7C4D88C8E38ED0052852C6D4BA634932D98BDFBEC7C764EF2A12FD4CF65074C80F
	6C6EBFD2535D8DE7930D819892586F5158A3CDE9G4D6B3E3B5FB87563D72D60F8F5A4393F2C0C7EAD27FF0F625FF20AA927187223E04DC4B26E3F40FA1F8F477C164FF0FA18B46C96BD28A48D536F4CC153C4269D74C9A7FB25CA5EEA06D80AA4DEF33FB4EC96A9F33324D60E6AE70B61B49C0B9D55BB68EC5B68F5A3E9410EC959530E8D18C8DA839A5B6969151A3823D8E0252151824F049010327DB621EDE12619ECB08DF77971B644335AB60FE4434918EE1714BE4A81C5BAF21E24F7ABBDD65A01CACC679B
	4EC3373404556596D0D1AFBD0B422CD0498EBF6B96BC11728AB60F492BG9E7A0F8EB7E34C5CC6EC5658C90A686D4CF5119CDFF6EA9E0E25DD78E78F7E59CB494C746F7E1BF31093FE87B185B8894BF996FD2A7FE6E343E4DB50EFDC56BC42562F8D0F78F31EAC8F30420123BB7EE8C7C64B23625C3F53D1C2025F653575CBCEA76C1C7AEADCF63C57BBB92C4A7E7359D38E646F95B43C12FFE76FC039AF6A5A7CBFD0CB8788A7D26499F899GG90C8GGD0CB818294G94G88G88G610171B4A7D26499F899
	GG90C8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3299GGGG
**end of data**/
}
}