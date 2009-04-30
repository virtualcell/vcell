package cbit.vcell.desktop;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.*;
import javax.swing.*;

import org.vcell.util.document.User;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import cbit.gui.DialogUtils;
import cbit.sql.UserInfo;
import cbit.util.*;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.RequestManager;
import cbit.vcell.server.UserRegistrationOP;

/**
 * Insert the type's description here.
 * Creation date: (1/26/2001 1:59:33 AM)
 * @author: Ion Moraru
 */
public class LoginDialog extends JDialog {
	
	private JButton lostPasswordJButton;
	private static final String DIALOG_TITLE = "Virtual Cell Login";
	
	public static final String USERACTION_LOGIN = "USERACTION_LOGIN";
	public static final String USERACTION_REGISTER = "USERACTION_REGISTER";
	public static final String USERACTION_EDITINFO = "USERACTION_EDITINFO";
	public static final String USERACTION_LOSTPASSWORD = "USERACTION_LOSTPASSWORD";
	public static final String USERACTION_CANCEL = "USERACTION_CANCEL";
	//
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
			if (e.getSource() == LoginDialog.this.getLostPasswordJButton()){
				updateFields();
				fireActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
			}
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
public LoginDialog(java.awt.Frame owner) {
	super(owner, true);
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
		fireActionPerformed(arg1);
//		this.jButtonRegister_ActionPerformed(arg1);
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
		fireActionPerformed(arg1);
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
			ivjJButtonCancel.setActionCommand(USERACTION_CANCEL);
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
			ivjJButtonOK.setActionCommand(USERACTION_LOGIN);
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
			ivjJButtonRegister.setActionCommand(USERACTION_REGISTER);
			ivjJButtonRegister.setName("JButtonRegister");
			ivjJButtonRegister.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJButtonRegister.setText("New User Registration...");
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
			final java.awt.GridBagLayout gridBagLayout = new java.awt.GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,0,0,7,0};
			ivjJDialogContentPane.setLayout(gridBagLayout);

			java.awt.GridBagConstraints constraintsJLabelUser = new java.awt.GridBagConstraints();
			constraintsJLabelUser.gridx = 0; constraintsJLabelUser.gridy = 0;
			constraintsJLabelUser.insets = new java.awt.Insets(4, 10, 4, 4);
			getJDialogContentPane().add(getJLabelUser(), constraintsJLabelUser);

			java.awt.GridBagConstraints constraintsJLabelPassword = new java.awt.GridBagConstraints();
			constraintsJLabelPassword.gridx = 0; constraintsJLabelPassword.gridy = 1;
			constraintsJLabelPassword.insets = new java.awt.Insets(4, 10, 4, 4);
			getJDialogContentPane().add(getJLabelPassword(), constraintsJLabelPassword);

			java.awt.GridBagConstraints constraintsJTextFieldUser = new java.awt.GridBagConstraints();
			constraintsJTextFieldUser.gridx = 1; constraintsJTextFieldUser.gridy = 0;
			constraintsJTextFieldUser.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldUser.weightx = 1.0;
			constraintsJTextFieldUser.insets = new java.awt.Insets(4, 4, 4, 10);
			getJDialogContentPane().add(getJTextFieldUser(), constraintsJTextFieldUser);

			java.awt.GridBagConstraints constraintsJPasswordFieldPassword = new java.awt.GridBagConstraints();
			constraintsJPasswordFieldPassword.gridx = 1; constraintsJPasswordFieldPassword.gridy = 1;
			constraintsJPasswordFieldPassword.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPasswordFieldPassword.weightx = 1.0;
			constraintsJPasswordFieldPassword.insets = new java.awt.Insets(4, 4, 4, 10);
			getJDialogContentPane().add(getJPasswordFieldPassword(), constraintsJPasswordFieldPassword);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.fill = GridBagConstraints.HORIZONTAL;
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 2;
			constraintsJPanel1.gridwidth = 2;
			constraintsJPanel1.weightx = 1;
			constraintsJPanel1.insets = new java.awt.Insets(4, 10, 4, 3);
			getJDialogContentPane().add(getJPanel1(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsJButtonRegister = new java.awt.GridBagConstraints();
			constraintsJButtonRegister.fill = GridBagConstraints.HORIZONTAL;
			constraintsJButtonRegister.gridx = 0; constraintsJButtonRegister.gridy = 4;
			constraintsJButtonRegister.gridwidth = 2;
			constraintsJButtonRegister.insets = new java.awt.Insets(2, 10, 2, 10);
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new Insets(4, 10, 4, 10);
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.gridx = 0;
			ivjJDialogContentPane.add(getLostPasswordJButton(), gridBagConstraints);
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
			ivjJLabelUser.setText("User Name");
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
			final java.awt.GridBagLayout gridBagLayout = new java.awt.GridBagLayout();
			gridBagLayout.columnWidths = new int[] {0,0,7,0};
			ivjJPanel1.setLayout(gridBagLayout);

			java.awt.GridBagConstraints constraintsJButtonOK = new java.awt.GridBagConstraints();
			constraintsJButtonOK.anchor = GridBagConstraints.WEST;
			constraintsJButtonOK.weightx = .5;
			constraintsJButtonOK.fill = GridBagConstraints.HORIZONTAL;
			constraintsJButtonOK.gridx = 0; constraintsJButtonOK.gridy = 0;
			constraintsJButtonOK.insets = new java.awt.Insets(4, 0, 4, 4);
			getJPanel1().add(getJButtonOK(), constraintsJButtonOK);

			java.awt.GridBagConstraints constraintsJButtonCancel = new java.awt.GridBagConstraints();
			constraintsJButtonCancel.fill = GridBagConstraints.HORIZONTAL;
			constraintsJButtonCancel.anchor = GridBagConstraints.EAST;
			constraintsJButtonCancel.weightx = .5;
			constraintsJButtonCancel.gridx = 1; constraintsJButtonCancel.gridy = 0;
			constraintsJButtonCancel.insets = new java.awt.Insets(4, 4, 4, 0);
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
	getLostPasswordJButton().addActionListener(ivjEventHandler);
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
		setSize(315, 226);
		setModal(true);
		setContentPane(getJDialogContentPane());
		initConnections();
		setLoggedInUser(null);
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
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		LoginDialog aLoginDialog;
		aLoginDialog = new LoginDialog(new java.awt.Frame());
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

		
	public void setLoggedInUser(User loggedInUser){
		getJButtonRegister().setEnabled(loggedInUser == null);
//		getLostPasswordJButton().setEnabled(loggedInUser == null);
		setTitle(DIALOG_TITLE+(loggedInUser == null?"":" ("+loggedInUser.getName()+")"));
	}
	/**
	 * @return
	 */
	protected JButton getLostPasswordJButton() {
		if (lostPasswordJButton == null) {
			lostPasswordJButton = new JButton();
			lostPasswordJButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
				}
			});
			lostPasswordJButton.setText("Forgot Login Password...");
			lostPasswordJButton.setActionCommand(USERACTION_LOSTPASSWORD);
		}
		return lostPasswordJButton;
	}
	
}