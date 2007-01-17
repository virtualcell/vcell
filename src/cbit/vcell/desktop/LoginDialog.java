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
	D0CB838494G88G88GDDFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DD8D4571534452EED7D925B1A0DED4D56DA1310065824E59B3FAD6D5AB469A6AD791A9012350D69C73F72ED5DDD5BF5376E46F65D2F178182989462CFE293B413449FFE060181B40898D1FEE4E0D064C7A6EAA20F19878CBC66CDE69E02896A1EF3FF5EBCE65E001A46AF07776E39671EFB6EB9671EFB6F39EFC2F63C3EB4A7533203104C9C427F3D1DC9485A1304FC6A751726842E6EC0EABEB17C
	FB8360A5B221E643338D6A5A7C2979C564551CBCA8DF00F249C8EA7EAB703E1DC867D6B542CB0CFE96C3DDF172135BE66F273ADD7413CB4B95A585705C8AB0GB8FCA69E407EDBCAF6F1FCBC478B483490B2DD02B483A5459C379EE872GD78760AAE174DEF8B281665557F13A8BDFCCA09BFE9A34ECC1BE861E829E66ED577227157CA9F3E1A97675B9AAA73631C2598D00034FA207C777423321EBEC789D4F20CF33DF72480AE2774A21B1CD8D54573B54E11F3F59A7A96AF0C19972F932A40F36A3A48DF8AC26
	38E514F7973C0FG4415893C2F9471EB60FD83C0C139984F51838DDCDE77C78F12346A716C42986359DB4E4661AA8F0FE74D58F3163FD05D87BBD0477321AE91E095C086C036ADB5FFAF40EB5466175B4B60F9FCD0EBD24747D57F71C06004628DF62ADED901777AFA286138EE5923863D04B03B5EDAE2B311E7B6E06D6E3861F6CCA789B54246095DAB481A6F7E2DA557C4A7EB92BE32B45818AEFAEDE11D303E1F095937E61B6903B37B6EA7463E273E7DF4D60EC95F0B16BC1DB51F73D9E268BB450636FE195A
	BA995EB339FFB03C15620BAA42F8660F8954E730EC053A788A619B6E2DC296D776B2D264385DD48F0B9FDAE0D9DB41E4A076C8D97EC87B3459D91FAF6932ECAEC33C5B00E73228149E4B71D0D7D4191A4F7C6B23CE1C1FCB203C9260890035GF9D560DFGF5D542479EEA2F3CCE9F6B12025A09891F62150304357D2863E5F84AD3DAD0F27AC31A647748E72520CF9AD4649061B4719D280341996DBEC7FD5F83FD1C147DF2D052FC2A9F38CB6332A687C30C66CB9DB8A6CFD026353D3EF199BA988F9041772B9D
	5B6129C8A12DAF6015B4B9CC416A17F520CDAEC9E701A3B000F7261704D3D40FB07EGC01C2E075DAE6457A1877D320221C39D1B887462C84893D528675F515831885E932B040E171FC65C8EC7EA3E4DA178743A369978B47BBCA83E943CAC66D8F1A94660C60770195F3B054FB4EF390F581E3E4D54E7726EF8BA4B6DE03E92F0C44E1D7D14A74A454AABCA5CB7B80FA32151E1BE674231621FBEF69FD93354B17DEEBB0336DA8C32A4812469BA3E55E5E5BEFBC28A4942CBCF874141E4EFD863CC4EBF17DA39BE
	310CBA477527104F990C95EF69E3AAAAC35AF5504F0E6A547C86GAF408200A400B5G59B5C2479325F186BB659878EF0D68E3AEBD3C75B1F448E4D9CCE5917AACB448A362640BDC263978DD0F3C310059E6DEA776AFDF127DDA339C107DDEF889713A851DA88B0F97C32A5E66CB949F0078528493BA246397F722BC122685FD03931A5CA3EB1A4FBF9C2EDFCA7B8ACD8C864221C974F5F7A76AC01D04782253A7F6625C7428FE3FCC1DC4EF426BD75058A0AB72B8885D24CE78B506CFBA0573EBCC3E4CE3534AD3
	AF900CAD4A8446B9369739DFC29D87023EF1F0BD8C15A42E8663C6F669AE030EEC0043BDD5AFBCC7EAC48C18FCCB44003D9F5EC3D6DD8CD84C563045F75EEAF934064D4BB5B5619840E43A6734396C5F26B10A63C3CC56C999E5B5EC0F78FC6B68C69E1BD06EDA58FB560A79D65E0575A22671095624AA8AD3232E5FDDE70A509E728793B2AC8598BCBBF41D3DFEE6DBC45DD92EBFD6FF608C4A249863EEAD6A2F2DCC6CB39B216C86182AE5B1F8E5AD538123ABA897F44587D373EB81FA819681A48124GB03AB3
	B6C1178E380D91F43B8E893ACFD788BA5BA156DF73A146B78E1E8BF53A5F58855DF2839D72CD06E786C07C5BC8F730CA50EDBC447AABFA1B79C2575B46F17CD127FB62EDC697B9DE466FBE0720DB8BE5D2171AEF2DE37C866A989D33F57787E8BB3AC79E15AEC9D37650A44C217A7AF6EEF9C2585E58DF1A47E3C5E21D58833FE1C3FF480072A6009D07053FBE39C56C01970DA5105E7B5332E26D69ED0719ACDD07437EDA32E0B20B795BEDBD5867C228DBFAD8785A32B346F578F8A824FAFCF48B407D64CC3751
	1F996EEC77E6F88E7902A14D8981E7CA785405EE149777889C4D77CC9F14097DDB92743B8EA03E9E715F2F5DE110FDAF60DCG8100AC4AEFF5D9B546A36A74F676681203C3EAF0DC761620F66D5224E627314FFE1CD63760FB293045DD2F201CDEDFA8200664125233E28FFB5F1EC65EE74A0AE5A4CF1AE7B1BBD3AC7857E1CBCABD5363067A30BE6B1EFA4E6282E0E3BBD8DE40ED5FF0A4B5DFBBA246367822F1EC0900CF8358F8C40CEDC9794D0E0D719FE9B053DDC103B82BAD5C0C735B2BB6A9BE4FD8099386
	4F7C6E59F2F1A63382AD9B4307B6B186B7629A4498AE359A7BF834010DE183BC992F228A41AB6F2860F5EF05605588382923E23C5B6CD598F9FDFE2FBAE9E70FA6C58D415C089CEDBF2DE423E5EDDB6C389EE8301031FE076C225F7B75FE8BAAC53FEB81E7FDC76861162EE29A8735DEE8DE6222FDA912FF585E832B1CFF78C869D946F3D9256069FEC770FC3FCA70DC8838C75F91E3F9246A637965FB3E83B81E60E53AA4F021CC1B00F85CE717EDB77AEC433577CDFDF6FE6108059CE33EDAF2AC6C33CF24BEE7
	49C895BE7B26C34CC78DE8920F0971BE6390634DGDCF1237083F7FE242F219F74B68AFD9C2D7EF87AF8287D9DF80E1E1850B4555FAD8F7BC2F0AE39F8BC02519CEA62FB33D0A91EEBC79A4D7753539C3F10BF1743131D77EE23F3E0E5E36419746B947FE8E364197400CD68F0B554B59C97E7942D8F779FCB211C8C1086308E2060841CD9CE08181B51DDC366B883BA7D20823F24744C58EC0933D88F768B7BA3DE7E3B9E54A35D9F912107493239F28BEDA7E66A08351BB7E7BBA5229D33E1E1CF0CF50FD9956A
	587E4CFE5238BF0BBF41FCA9811E49A7C4DC792F5ABC4EB76D04D8832F349A57406442AB1697CD7C9F21200949E7EBB2446C857F435740BD7D988F94281B8F3094A099A09DE0930035D95868C06F1139ECC4DD2FD7E5FE5894394B657ABCD29E294F4666197AB33703B32A1DBD22FC21594C8E7BE81E50448E987FDAFD3262E5F6F84696E9875C73E840F3FE3330434FAF0A1838D447FD3DC158E6F533B81B25D3D90BDAE21FE1A301350B335D1CEEEE09EA97291B3DADE63A795E9E2B39EE3A6094B429863DD4BF
	EC1C77350A31B73408F16E2C40FE359B9E678395B7B74EFF0CEA97B94E79ADE66D466C5713D7DCEAE84B5AED2B3CB9B92DD16DA265CCB21573EE5AAE650675692D3AB9B903D16D76C41453A3E4617BE6B74E290DD037239562B8C09B0082B09FA031D5440C295E23647AE2C693605A606C959DB5042CA50E1B0B5F0766EC1752EA56EECDD43B6B53693FC5350B54E9C6C4FFECFE5D1A1F47F9EC02FA16737BBCBD93551E04F367C9017BB545A9D00E57F183F48F1BG65E49DF78F45BD81650C13E23EFE4D067B46
	75FA79CEDA4675065F4758BEEECEAD92302F197AE97D2B77E14C63B13F3B5297BB87945FE71E837AFC1FF98EE8E15FCB8C8FBC0C99E8BDC754B7C70E284FACC7341B4A7DDBEDA7B149871D1246E5BE2E6DE4C5D9BE27E95154CE8791317B8FB42FE39A3B61644A4F85A7455AD454A64E548F9C41FDDA9B1487G265A046D1F2FB51BA78B5B783E30ADBAA631D8D0FC8E7742AB21BE8DE09DC036B3B5FF2FD34400C3E7675C5B35CC4D3237AB690FED4B4AFEF3DB9E6C47FCC0C81D88FA64C8FBB5746F526B425146
	29496328F6C653C25BEBD2F0D80E32370B36E7F5315A7775477289E67747DEAE24BE958A49A19AG1BA5CD62768E169B6DDD23470707AB04AD1B1D4216DF24E73CE9A8AF82C8F48ADB26DD1CAD66BD6C0C36A5EB373C412C5DEACE3F4E249D0BB54F53D8C35A613DDD1C019E349947D12B77711D5659E45B5B9E2B8FDB056823CE6FE3ED05310F03BA6D2769F95885F483BA2D3352C8FBC8277DA1BD67AAC0B72D53FE2D4AC05BD4235B7E6B6E583EF8175B5C97935D31FD713B6E39FDB1599D5B97FF6C1E5B97D3
	5C31FC110D7522432817EA9D3F3D5A288307EE3FC579C67B4D6776DA421F771B580F717BC6A946BA2FBCA4CDA89AE6BA645381FE758678CD68BBA439D47878436DA2DF7140A41EEB060252B8085D9310BDA42D83E9B76AF96DC6A87782E8G0BG12BA049DA74B1147386A15943D0FDB74FCCF31CB502D22F41ACFD3B06E5ADC786D42BE096832DBEBB0F74CB31620E80D2E85FED95FDF1E0C746900AB2C07F8D734CE58D9FADF833DDFF23133F40E0E2F2278C4831E1D319FBCA272912B20EEEF27D86B379E44B1
	4D0772E29D279E947B04651DA2966FBD975BFF4B4F197B2F631C793A787639595745E367E2799D5359E3D1FB798D1DE1BD651D0A2533B7E88E2A78D4647DF4B545371D0A3C1F3E3DD25CCFEBD0C7CE8B7D7C7BC5211F85BA6E7B8DC20FEBF4DC71D1C137763470172DF45F84534E41FCEA2F8DE99E83286B926D9EEB956D5AG47687AAA845D62AE615FBB5EB166AA186F3DB798763D27ABC5DF2CAE4E03F52C0FFB691EB181F82D547B3D3D86F1A9D0DE2363B65568E36892E3F80A66058CDB871EE78477461BF4
	ADE4EFF676F41D6EE939F87A5445A6973C303C4352C96377B4F51BBFEDFAD704D0F706FFFF421F1BF6FEB5132DE5FB4A72F81BF828CBBE0338EF1599F3EAA8632D196C5C446A574C28C7FBBA0931FEEB39319EE5FDFEC67B469975A873133CBD9B574F7609FC4A261F5DCF143F0F0F791DD0F6B74FA77267420C2FE4E6BC7BE196536B1D65B4B79B1C10995F1F1FB76A2B682B0AA5B3865F814ECF69B664A959FAC677F1CB811F542D5B34CD58F4F537D8774B297F16742466B776885CA10A43FB9C3C3BE2389F9C
	79EB6F41E99C06BECB74FE1F79C47A48613E41F47D4BAA1187E7F36392CEA1B25D037A796C41666B6403F0815AA06C643689407BEE7E4E741DDEA1EC70F00F304145CAFDDE6963BED0E505E7C1AF44E700C6008BGF1G0BG92G12FB85ED9D25DD8D650D3D6CEE8F6F6D705EE2D79F6CF37A583D9A7FB62AB26E86F5057D62937BAC607D0ABAA1CFA31773FAF9B2D9D65D30AC623B04C22EEF63BD3F78B6A0175B65FAF9B2BBDB86E633F3D684387B42F6463E14BE36DEA0B09F3AEBA86CC3B5C7EF04F7248FCD
	816F967E4E7C75088D1F0E2AAA4EBF3DF5AE7EC6BF0D0F10FD31C1F646DF29907C79FD218F674790ECD8587A13C6570B65506E7EBE318E2C2819B907FEBF9C9E7F76DAA1DFFADF340FE4F25B8999B7G8D0298FF5E5930FC6C2C7835917A3D92602B4E0ABD49A31EF2F81E15C6794930456B5354A0BD9A8232AB2842AED13B4C3074461E7B6027664C4B1B79A0A1BF4A95F1BE699F3EC5D6FE36B9E65E3C6BAC7F5E09BFCBFE73154C2D7AD8F2C6693D8654A5832483EC84A868077954AF46D660C59971048B629F
	57B449B3C2EF0D4541772CCF1E8C23F1F34BF8170C21240CC737F7DFAC7DG07F6D83FCF8F0E42463FD79A567D60798A7CBEC411AE2B939A583B1F6E9574FB06BAA8F781CC81AC68E771065B5BB91BBF0AEF18043D57C1DB628E7B638EF724BF0E3D09BA67B79E27BBF47C7FB5EC89631B7033BD5C1F3FCE6B40EFC8789F734947311A5B6F578542FE4577FE13B47FF333652D9876333A397D5C6175F4115F1A45743155AE741160E67AD8854FB4B73BFFD96726773D153BCC789A67A74C394DF8275E6E1286E525
	AF049BE0066FBC33D5470BF43A28739C26F57A8D04311D6B837C26F1E686BE327D9F7D390C46AFAB8E0EDB33A3D76FCF60CE9506F1559C77C0C7E1D87762EE115F21872A0CFB245AF719AE5C6F0AFB4F32DC33FAC52F7F43BE337AB83D5E6FB32BDF844FE85B2FBF46EC6F72077DC045E309091F885A04C018B6B0C1F49BFD21863FBF774B13D67D4A349502E2491B4703F024FB53E6F540FF3525C464DA1A757B3684D359C49FDF3E046FBC4FB702E3524FBBACD409EF41C6F05CBE3FCF036EFC9F52C3AF3BEEE7
	735DB30975695057A6G6BB9588BG341D533FFF723DC9E89CB109A1AD5EE1D96C59C5FFDBC7E31F73F60E4E7D4D6C1ED17469C195DCE49C798B7C5ED174A7CD8DD0A443150E623C8C7A06C7B4C6AAF2F323F4EFA48FD1B4BB0BBDF5D2FCC76F0531ADB9A74EC00E0199F7F4E8207A7A13C11F7704B46C22D10C596535B6F1EEC8BBA7F2E04EB664D9F09E62AEC043F9F1BFBA35DF4409443B161136C473EF0452FB2FD8FA4F3378B0F5BE9CA7560FBDE7B17333583EB06FA3FCE7768CCDE39CCD82FECFGECGA8
	38GF6852883F0818CGE8G8BG16832C84C881C887D88F107D9E6C815F93765FB60E3E99EB0D640A22CE9014FC68867C6CFB48AE74ED83DE5E77441D65D53B50631277049E93A9ED9A145781E43FAF74B8BAE60C37281BD867226277197E9A5F8F6BF1AB517355C3B17255B4BAB1396E2E9273AE6EFDA12B14ABF0ABF5DC59FE44613A3DDE475D699377A7A597451E59B9A327BDGF86D626CFBDD266F31495873A7B8B9777C79E812FEA386FE5FFBB94073F0F11338A62BC3C3A1D91F2773A60BF41CB3542506
	FC9899186C3F084AA94D852C5DEFEF321D356DC65B313C4B8F687C1BFFB13C76B37CCFA8FE11814FFC72F41B704925D017BDA03E93B9F7957DE3844A71GCBG12G52G568F08F9706A94FEB3B56BDDC22F9AF0016194C3CA0A357D1B6958B63DE5FAEE1B5EBEED9E936F180E0E094B264DE3E24234B1A6B25CFD53685B43B0075DBA5DF7F45C3BBA6EC14A93F17DBE2FB62263FFC065C2FC1B0C9D6A39E1AA57A44539B96ED1836EDD0EFBFC9A7D9206BC2286C55B54E9FAEE70A98AABBFC3E9FCFEF0D3DD9F4F
	D299FD8149A0F73A0EFBD7772B066B3AFB0F7647761BEC77199BEE47FFEBC3730459D2249F17D2FC119469475F51E3EBB15445C9420F132E219FE7C0198C421A8450G5085B0B2A87CB8F4856F94E24573EED52559E5CCB4C3B8170306484E461898F5EF0FB0BD4873D103316EF706F24D5AAD6774ABE3362B5CEF56AE05532743F383C01EA7B53F5843F02C5D37FC46F6D53C2E19BF5D1ED8F7BD83F47F25F8184E99CE2638F80EE37CFF5CC674779443FBDF5BA711FB1A29937FA75A8773934F5558B1EB742AF9
	3EBEF055BCDFFF696A6C7754D72F467EAD433C2B465FB2705F7CD40675DDE9F8DFE97F64F57F64A75A870F2B57E26B7E61EBF32F970FDFB3DFAF1E3A963DDE2C3EE63EDE7C6A1AF13DE0736047D5620EE229C764A1CB9CA28F1928632E648A3ACDBA6ED73902AE4FABF02D7B851DDB475D3DDF50F9F55CAF2FA0AE894AA9BA6E3BB41F138EE5A28BDCAE5D076D00F21D0E7B11CF5CD338F45C8AAA73G1483BAEEFB3568F7DA96E783E9BF46D9FEF622CB9F4397CFED896345524777621D98A2F88E6B18E32E1C46
	ECB93C94B9B2BF983004F97FF76EDC3CF3E6613DBA0A771733423CFF38DF704E104DF3EFC67EC65E085BA8C766E27E738A7D0D029A1407036A045FAB6EC3732F224E330760FDC86484FFBA02E79C589078BE44B5C8B92E78067DF86A64EDB2C6447921CAEF334777720CB6185C90BBDA5157E11FE8B7B0A46E44FECFF18B213CC4677508E3E6CE72FB59E13D14D58B3D248C196BA55F2017F420C190BA5FB0942973DF505F313377CD384D61B9007F58AF4E9DE4B8B51F553F1E97261DC70FC7CC5EFB6979DE4FAD
	B4290A9A348FAA12E70C557F6F489BE466DDDB8F7E8DDF35B5351C6AED695679F5792E171E49F25F1930AC4B9CB7166BB8DFAD46D9B7BCDB2E435E036FAC7EEE18319F88C03B7861594FD52C5DC035315DE2E813B8C7BB9697DEBC903D9FA8BAE05C8FB05CE4F5B45DF435110E7D3F8C3E2D3C8416CE7C9D495148DF780793D6CD3552130495BF46991468B10D7E77824BD0EAA4077EBCC1A33BC7B4ADD0DFD9B9B9B949FF722F8607AB03BC17968A12C2DD75CE3AE7CE3FD4D31F751BEFE6D9C836133C405684A7
	4946406EA4B9B46EBBC9962C89D318404AB6C26883F2153CE040ECB6454EC69BA4DBE63842A35FB878AFA93FBA9B4AA2AFB849DF70F620C78E5244146C453173DFAC37127C008A436D81BC889B220F1C10F6D911DDE447D0B77E128E466BBDAE3694A10D6C243FC9281C50FCCAE51B949A51F0C9BC62A259FE7CEDB559462A7167A6157C67A6F444194787282751998C72EE2772A2973B4EE57B778B773D511E31B20BD8DA49E6C5153C2D1287F64DCE122F0D846550082AF82782479AA9233CC911A040613F268F
	9F87EB4748248BBDB6F54702ADEB3734C2D41F8A12CACA0901472ED3F81BC624A0F4A5871D9AFE302286776AF51F5A236DFF7C64AB97ACA4138F92CD30E5AAGDEB3A40D7B144B7042BE9B2146F9890A6C7825FB897D6DCDAB0CC77B656F9EDB76EC437AF5402C156450AF82C02422B055290971C1B97859445BF63F38A87D8BA8F1G07ABB86743F6449F429F07GF3EB4418C515AE3A7D4B0FD55DF64F1F4ED15143CDB572BAB392B42D9C1655714A63C3C3F08851990045007DEE1DBD5258A3E836CDFDF1E77B
	53FF9E848FEBA6D963D5D5742F037E2D463F8E0AF1D00C239A4C361E4C7AAF32FECA0C193AA5AD6CC29F1A42371DD326534322E3B79BC1A3F918FEE78E1AA7B26DE0A7BA596BB5726AA90825C892320ECB172DA2E5EE15347A997DECB61D065158993D03AA374EDCB7B26FFC65B3C53756CCD8C8DEABBA119298111C5C9DDC6445C19AAD70CC888129A8ECAA5A96E61A4583537035B05E03F8D65B9310BCB899733DD2F00CF2C011AA4F137C216070A06DC0A316732D6721DBDA42EA078BD4B4C24F22B0ABB472CA
	18F5971EC8F9851B47644F389C50FFF438AFC64DED4496C6CFD2C4EF57278A16DE46BF2F621F57E8235D742F75069D85DB3D9961A007428EE209F0921673AC3DE67CE6E363BCCF4CEFDC9678597A35549F4EF932BC009240512D817CC63FB877A72B95A476DDDE4F3824A830F39ABB6E77623D5E094B1A9CBE1FFD90C07EA1C143AB79F7768114FB5450667F81D0CB878841C1BB69E399GG90C8GGD0CB818294G94G88G88GDDFBB0B641C1BB69E399GG90C8GG8CGGGGGGGGGGGGGG
	GGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1D99GGGG
**end of data**/
}
}