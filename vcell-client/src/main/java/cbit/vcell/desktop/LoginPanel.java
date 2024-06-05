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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import cbit.vcell.client.server.DynamicClientProperties;
import org.vcell.util.Hex;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.resource.PropertyLoader;

/**
 * Insert the type's description here.
 * Creation date: (1/26/2001 1:59:33 AM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class LoginPanel extends JPanel {
	
	public static final String DIALOG_TITLE = "Virtual Cell Login";

	private JPanel ivjJPanel1 = null;
	private JButton lostPasswordJButton;
	private JButton ivjJButtonCancel = null;
	private JButton ivjJButtonOK = null;
	private JLabel ivjJLabelPassword = null;
	private JLabel ivjJLabelUser = null;
	private JPasswordField ivjJPasswordFieldPassword = null;
	private JTextField ivjJTextFieldUser = null;
	protected transient java.awt.event.ActionListener aActionListener = null;
	private java.lang.String fieldUser = new String();
	private java.lang.String fieldPassword = new String();
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JButton ivjJButtonRegister = null;
	private LoginDelegate loginDelegate;
	private JEditorPane dtrpnUseThisLink;
	private JButton btnGuestLogin;
	private static final String J_TEXT_FIELD_USER = "JTextFieldUser";

	class IvjEventHandler implements java.awt.event.ActionListener {

		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LoginPanel.this.getJTextFieldUser() || e.getSource() == LoginPanel.this.getJPasswordFieldPassword() || 
					e.getSource() == LoginPanel.this.getJButtonOK() || e.getSource() == LoginPanel.this.getBtnGuestLogin()) {
				updateFields();
				if(e.getSource() == LoginPanel.this.getBtnGuestLogin() || fieldUser.equals(User.VCELL_GUEST_NAME)) {
					JDialog dialog = new JDialog();
					dialog.setAlwaysOnTop(true);
					int confirm = JOptionPane.showOptionDialog(dialog,
							"The Guest account allows you to explore public models in\nthe VCell database and build models and run simulations on your local machine.\nYou will not be able to save a model to the database or use the VCell simulations servers.",
						"Change User...", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[] {"Continue","Cancel"}, "Continue");
					if(confirm == 0) {
						loginDelegate.login(User.VCELL_GUEST_NAME, new UserLoginInfo.DigestedPassword("vcellfrmfrm"));
					}
				}else {
					Preferences prefs = Preferences.userNodeForPackage(LoginPanel.class);
					prefs.put(J_TEXT_FIELD_USER, fieldUser);
					loginDelegate.login(fieldUser, new UserLoginInfo.DigestedPassword(fieldPassword));
				}
			}
			if (e.getSource() == LoginPanel.this.getJButtonRegister()) {
				loginDelegate.registerRequest();
			}
			if (e.getSource() == LoginPanel.this.getLostPasswordJButton()){
				updateFields();
				loginDelegate.lostPasswordRequest(fieldUser);
			}
			if (e.getSource() == LoginPanel.this.getJButtonCancel()){
				loginDelegate.userCancel();
			}
		};
	};

/**
 * LoginDialog constructor comment.
 */
public LoginPanel(LoginDelegate loginDelegate) {
	super();
	this.loginDelegate = loginDelegate;
    initialize();
}

LoginPanel( ) {
	this(null);
}


/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setText("Cancel");
			// user code begin {1}
			// user code end
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
			ivjJButtonOK = new javax.swing.JButton();
			ivjJButtonOK.setName("JButtonOK");
			ivjJButtonOK.setText("Login");
			// user code begin {1}
			// user code end
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
			ivjJButtonRegister = new javax.swing.JButton();
			ivjJButtonRegister.setName("JButtonRegister");
			ivjJButtonRegister.setFont(new Font("Tahoma", Font.BOLD, 14));
			ivjJButtonRegister.setText("New User Registration (free!)...");
			// user code begin {1}
			// user code end
	}
	return ivjJButtonRegister;
}

/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setupLoginPanelContentPane() {
	
			this.setName("JDialogContentPane");
			final java.awt.GridBagLayout gridBagLayout = new java.awt.GridBagLayout();
//			gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
//			gridBagLayout.columnWeights = new double[]{1.0, 0.0};
//			gridBagLayout.rowHeights = new int[] {0,0,0,0,0, 0};
			this.setLayout(gridBagLayout);

			java.awt.GridBagConstraints constraintsJLabelUser = new java.awt.GridBagConstraints();
			constraintsJLabelUser.gridx = 0; constraintsJLabelUser.gridy = 1;
			constraintsJLabelUser.insets = new Insets(4, 10, 5, 5);
			this.add(getJLabelUser(), constraintsJLabelUser);

			java.awt.GridBagConstraints constraintsJLabelPassword = new java.awt.GridBagConstraints();
			constraintsJLabelPassword.gridx = 0; constraintsJLabelPassword.gridy = 2;
			constraintsJLabelPassword.insets = new Insets(4, 10, 5, 5);
			this.add(getJLabelPassword(), constraintsJLabelPassword);

			java.awt.GridBagConstraints constraintsJTextFieldUser = new java.awt.GridBagConstraints();
			constraintsJTextFieldUser.gridx = 1; constraintsJTextFieldUser.gridy = 1;
			constraintsJTextFieldUser.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldUser.weightx = 1.0;
			constraintsJTextFieldUser.insets = new Insets(4, 4, 5, 10);
			this.add(getJTextFieldUser(), constraintsJTextFieldUser);

			java.awt.GridBagConstraints constraintsJPasswordFieldPassword = new java.awt.GridBagConstraints();
			constraintsJPasswordFieldPassword.gridx = 1; constraintsJPasswordFieldPassword.gridy = 2;
			constraintsJPasswordFieldPassword.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPasswordFieldPassword.weightx = 1.0;
			constraintsJPasswordFieldPassword.insets = new Insets(4, 4, 5, 10);
			this.add(getJPasswordFieldPassword(), constraintsJPasswordFieldPassword);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.fill = GridBagConstraints.HORIZONTAL;
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 3;
			constraintsJPanel1.gridwidth = 2;
			constraintsJPanel1.weightx = 1;
			constraintsJPanel1.insets = new Insets(4, 10, 5, 3);
			this.add(getJPanel1(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsJButtonRegister = new java.awt.GridBagConstraints();
			constraintsJButtonRegister.fill = GridBagConstraints.HORIZONTAL;
			constraintsJButtonRegister.gridx = 0; constraintsJButtonRegister.gridy = 5;
			constraintsJButtonRegister.gridwidth = 2;
			constraintsJButtonRegister.insets = new Insets(2, 10, 5, 10);
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new Insets(4, 10, 5, 10);
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.gridy = 4;
			gridBagConstraints.gridx = 0;
			this.add(getLostPasswordJButton(), gridBagConstraints);
			this.add(getJButtonRegister(), constraintsJButtonRegister);
			GridBagConstraints gbc_dtrpnUseThisLink = new GridBagConstraints();
			gbc_dtrpnUseThisLink.fill = GridBagConstraints.BOTH;
			gbc_dtrpnUseThisLink.weighty = 1.0;
			gbc_dtrpnUseThisLink.gridwidth = 2;
			gbc_dtrpnUseThisLink.insets = new Insets(4, 10, 5, 10);
			gbc_dtrpnUseThisLink.gridx = 0;
			gbc_dtrpnUseThisLink.gridy = 6;
			add(getDtrpnUseThisLink(), gbc_dtrpnUseThisLink);
			GridBagConstraints gbc_btnGuestLogin = new GridBagConstraints();
			gbc_btnGuestLogin.insets = new Insets(2, 10, 5, 10);
			gbc_btnGuestLogin.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnGuestLogin.gridwidth = 2;
			gbc_btnGuestLogin.gridx = 0;
			gbc_btnGuestLogin.gridy = 7;
			add(getBtnGuestLogin(), gbc_btnGuestLogin);
			// user code begin {1}
			// user code end
}

/**
 * Return the JLabelPassword property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPassword() {
	if (ivjJLabelPassword == null) {
			ivjJLabelPassword = new javax.swing.JLabel();
//			ivjJLabelPassword.setEnabled(false);
			ivjJLabelPassword.setName("JLabelPassword");
			ivjJLabelPassword.setText("Password");
			// user code begin {1}
			// user code end
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
			ivjJLabelUser = new javax.swing.JLabel();
			ivjJLabelUser.setName("JLabelUser");
			ivjJLabelUser.setText("User Name");
			// user code begin {1}
			// user code end
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
			ivjJPasswordFieldPassword = new javax.swing.JPasswordField();
//			ivjJPasswordFieldPassword.setEnabled(false);
			ivjJPasswordFieldPassword.setName("JPasswordFieldPassword");
			// user code begin {1}
			// user code end
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
			ivjJTextFieldUser = new javax.swing.JTextField();
			ivjJTextFieldUser.setName(J_TEXT_FIELD_USER);
			// user code begin {1}
			// user code end
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() {
	// user code begin {1}
	// user code end
	getJTextFieldUser().addActionListener(ivjEventHandler);
//	getJTextFieldUser().getDocument().addUndoableEditListener(new UndoableEditListener() {
//		
//		@Override
//		public void undoableEditHappened(UndoableEditEvent e) {
//			String useridText = getJTextFieldUser().getText();
//			boolean isValidNonGuest = (useridText != null) && (useridText.length()>0) && !User.VCELL_GUEST.equals(useridText);
//			getJLabelPassword().setEnabled(isValidNonGuest);
//			getJPasswordFieldPassword().setEnabled(getJLabelPassword().isEnabled());
//			getLostPasswordJButton().setEnabled(isValidNonGuest);
//		}
//	});
	getJPasswordFieldPassword().addActionListener(ivjEventHandler);
	//ChildWindowManager.findChildWindowManager(this).getChildWindowFromContentPane(this).addChildWindowListener(ivjEventHandler);
	getJButtonCancel().addActionListener(ivjEventHandler);
	getJButtonOK().addActionListener(ivjEventHandler);
	getJButtonRegister().addActionListener(ivjEventHandler);
	getLostPasswordJButton().addActionListener(ivjEventHandler);
	getBtnGuestLogin().addActionListener(ivjEventHandler);
}

/**
 * Initialize the class.
 * @param owner 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void initialize() {
		// user code begin {1}
		// user code end
		setName("LoginPanel");
		//setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
//		setSize(315, 250);
		//setModal(true);
		setupLoginPanelContentPane();
		initConnections();
		setLoggedInUser(null);
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void updateFields() {
	fieldUser = getJTextFieldUser().getText();
	fieldPassword = new String(getJPasswordFieldPassword().getPassword());
}
		
	public void setLoggedInUser(User loggedInUser){
		try {
			Preferences prefs = Preferences.userNodeForPackage(LoginPanel.class);
			getJTextFieldUser().setText(prefs.get(J_TEXT_FIELD_USER, ""));
			if(getJTextFieldUser().getText() != null && getJTextFieldUser().getText().length()>0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						getJPasswordFieldPassword().requestFocusInWindow();	
					}
				});
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @return
	 */
	protected JButton getLostPasswordJButton() {
		if (lostPasswordJButton == null) {
			lostPasswordJButton = new JButton();
//			lostPasswordJButton.setEnabled(false);
			lostPasswordJButton.setText("Forgot Login Password...");
		}
		return lostPasswordJButton;
	}
	
	private JEditorPane getDtrpnUseThisLink() {
		if (dtrpnUseThisLink == null) {
			dtrpnUseThisLink = new JEditorPane();
			dtrpnUseThisLink.setBorder(null);
			dtrpnUseThisLink.setBackground(getBackground());
			dtrpnUseThisLink.setFont(new Font("Arial", Font.BOLD, 14));
			dtrpnUseThisLink.setContentType("text/html");
			String s =
					"<html><body bgcolor=\"#"+Hex.toString(new byte[] {(byte)(getBackground().getRed()&0xFF),(byte)(getBackground().getGreen()&0xFF),(byte)(getBackground().getBlue()&0xFF)})+
					"\"><center><font size=5 face=Arial>Use <a href=\""+ DynamicClientProperties.getDynamicClientProperties().getProperty(PropertyLoader.ACKNOWLEGE_PUB__WEB_URL)+"\">this link</a> for details on how to<br>acknowledge Virtual Cell in your<br>publication and how to share your<br>published research through<br>the VCell database.</font></center></body></html>";
//			System.out.println(s);
			dtrpnUseThisLink.setText(s);
			dtrpnUseThisLink.setEditable(false);
			dtrpnUseThisLink.addHyperlinkListener(new HyperlinkListener() {
		        @Override
		        public void hyperlinkUpdate(HyperlinkEvent e) {
		            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		            	DialogUtils.browserLauncher(LoginPanel.this, e.getURL().toString(), "Please visit "+ DynamicClientProperties.getDynamicClientProperties().getProperty(PropertyLoader.VCELL_URL)+" for Online Help");
		            }
		        }
		    });
		}
		return dtrpnUseThisLink;
	}
	private JButton getBtnGuestLogin() {
		if (btnGuestLogin == null) {
			btnGuestLogin = new JButton("Login as Guest (no Registration)...");
			btnGuestLogin.setFont(new Font("Tahoma", Font.BOLD, 14));
			btnGuestLogin.setEnabled(true);
		}
		return btnGuestLogin;
	}
}
