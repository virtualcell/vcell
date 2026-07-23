/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop;

import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.resource.PropertyLoader;
import com.google.common.collect.ImmutableList;
import com.sun.mail.imap.ACL;
import org.vcell.util.gui.GeneralGuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Insert the type's description here.
 * Creation date: (6/21/2004 5:16:25 PM)
 * @author: Anuradha Lakshminarayana
 */
@SuppressWarnings("serial")
public class ACLEditor extends javax.swing.JPanel {

	//
	private ACLState aclState = null;
	private javax.swing.ButtonGroup groupOfRadioButtons = null;
	private javax.swing.JRadioButton specificAccessRadioButton = null;
	private javax.swing.JRadioButton privateRadioButton = null;
	private javax.swing.JRadioButton publicRadioButton = null;
	private javax.swing.JCheckBox vcellSupportCheckBox = null;
	private javax.swing.JButton addUserJButton = null;
	private javax.swing.JButton removeUserJButton = null;
	private javax.swing.JList<Object> validUsersJList = null;
	private javax.swing.JScrollPane validUsersJScrollPane = null;
	private javax.swing.JTextField addUserJTextField = null;
	// We want to control the parent "JOptionPane"'s button settings; a good way to do that is to make the buttons here
	//  and make them available at construction time!
	private javax.swing.JButton parentConfirmChangesJButton = null;
	private javax.swing.JButton parentCancelChangesJButton = null;
	private ACLState.ACLType originalACLType = null;
	private ImmutableList<String> originalValidUsersList = null;


	private final IvjEventHandler ivjEventHandler = new IvjEventHandler();

	private class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ACLEditor.this.getAddUserJButton()) ACLEditor.this.userRequestedToAddNewUserToShareWith(e);
			if (e.getSource() == ACLEditor.this.getRemoveUserJButton()) ACLEditor.this.userRequestedToRemoveExistingSharedUser(e);
			ACLEditor.this.setParentConfirmChangesButtonToCorrectState();
		}

		public void itemStateChanged(ItemEvent e) {
			if (    e.getSource() == ACLEditor.this.getPublicRadioButton() ||
					e.getSource() == ACLEditor.this.getPrivateRadioButton() ||
					e.getSource() == ACLEditor.this.getSpecificAccessRadioButton() ||
					e.getSource() == ACLEditor.this.getVCellSupportCheckBox()
			) ACLEditor.this.updateACLState(e);
			ACLEditor.this.setParentConfirmChangesButtonToCorrectState();
		}

		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ACLEditor.this && (evt.getPropertyName().equals("ACLState")))
				ACLEditor.this.updateUserInterface(evt);
		}
	}

	private javax.swing.JPanel grantAccessJPanel = null;
	/**
	 * ACLEditor constructor comment.
	 */
	public ACLEditor() {
		super();
		this.initialize();
	}

	public JButton regenerateParentConfirmChangesJButton() {
		try {
			// If
			this.parentConfirmChangesJButton = new JButton(DatabaseWindowManager.acceptButtonName);
			// Need some more work to make sure button is only enabled at correct times!
			this.parentConfirmChangesJButton.setEnabled(false);
			this.regenerateOriginalState();
		} catch (java.lang.Throwable throwable) {
			this.handleException(throwable);
		}
		return this.parentConfirmChangesJButton;
	}

	public JButton regenerateParentCancelChangesJButton() {
		if (null != this.parentCancelChangesJButton) return this.parentCancelChangesJButton;
		try {
			this.parentCancelChangesJButton = new JButton(DatabaseWindowManager.rejectButtonName);
		} catch (java.lang.Throwable throwable) {
			this.handleException(throwable);
		}
		return this.parentCancelChangesJButton;
	}

	private void regenerateOriginalState() {
		this.originalACLType = this.getACLState().getAclType();
		this.originalValidUsersList = this.getACLState().getAccessList();
	}

	/**
	 * Comment
	 */
	private void initializeRadioButtonGroup() {
		this.getACLRadioButtonGroup().add(this.getPublicRadioButton());
		this.getACLRadioButtonGroup().add(this.getPrivateRadioButton());
		this.getACLRadioButtonGroup().add(this.getSpecificAccessRadioButton());
	}
	/**
	 * Comment
	 */
	private void actionACLState(ItemEvent itemEvent) {
		if(itemEvent.getSource() == this.getPrivateRadioButton() && itemEvent.getStateChange() == ItemEvent.SELECTED){
			this.setACLState(ACLState.PRIVATE_TYPE);
		}else if(itemEvent.getSource() == this.getPublicRadioButton() && itemEvent.getStateChange() == ItemEvent.SELECTED){
			this.setACLState(ACLState.PUBLIC_TYPE);
		}else if(itemEvent.getSource() == this.getSpecificAccessRadioButton() && itemEvent.getStateChange() == ItemEvent.SELECTED){
			ArrayList<String> aclList = new ArrayList<>();
			for(int i = 0; i< this.getValidUsersJList().getModel().getSize(); i+= 1){
				aclList.add((String) this.getValidUsersJList().getModel().getElementAt(i));
			}
			if (this.vcellSupportCheckBox.isSelected()) {
				aclList.add(PropertyLoader.VCELL_SUPPORT_USERID);
			}
			if (!aclList.isEmpty()) {
				this.setACLState(new ACLState(aclList));
			} else {
				this.setACLState(ACLState.EMPTY_ACL);
			}
		}else if(itemEvent.getSource() == this.getVCellSupportCheckBox()){
			if (this.vcellSupportCheckBox.isSelected()) {
				this.setACLState(this.getACLState().addUserToACL(PropertyLoader.VCELL_SUPPORT_USERID));
			} else {
				this.setACLState(this.getACLState().removeUserFromACL(PropertyLoader.VCELL_SUPPORT_USERID));
			}
		}
	}
	/**
	 * Comment
	 */
	public void performFullReset() {
		this.getValidUsersJList().setListData(new Object[0]);

		this.getPublicRadioButton().setEnabled(true);
		this.getPrivateRadioButton().setEnabled(true);
		this.getVCellSupportCheckBox().setSelected(false);
		this.getAddUserJButton().setEnabled(true);
		this.getRemoveUserJButton().setEnabled(true);
	}

	/**
	 * connEtoC2:  (ACLEditor.ACLState --> ACLEditor.updateInterface()V)
	 * @param arg1 java.beans.PropertyChangeEvent
	 */
	private void updateUserInterface(java.beans.PropertyChangeEvent arg1) {
		try {
			this.updateInterface();
		} catch (java.lang.Throwable throwable) {
			this.handleException(throwable);
		}
	}
	/**
	 * connEtoC3:  (PublicRadioButton.item.itemStateChanged(ItemEvent) --> ACLEditor.actionACLState(Ljava.awt.event.ItemEvent;)V)
	 * @param arg1 ItemEvent
	 */
	private void updateACLState(ItemEvent arg1) {
		try {
			this.actionACLState(arg1);
		} catch (java.lang.Throwable throwable) {
			this.handleException(throwable);
		}
	}

	private void userRequestedToAddNewUserToShareWith(java.awt.event.ActionEvent arg1) {
		try {
			if (this.getAddUserJTextField().getText().isEmpty()){
				PopupGenerator.showErrorDialog(this, "You must enter the username of who you would like to share your model with!");
				return;
			}
			if (this.getACLState().getAccessList().contains(this.getAddUserJTextField().getText())){
				PopupGenerator.showErrorDialog(this, "User " + this.getAddUserJTextField().getText() + " already has access");
				return;
			}
			this.setACLState(this.getACLState().addUserToACL(this.getAddUserJTextField().getText()));
		} catch (java.lang.Throwable throwable) {
			this.handleException(throwable);
		}
	}

	private void userRequestedToRemoveExistingSharedUser(java.awt.event.ActionEvent arg1) {
		try {
			String removeUser = (String) this.getValidUsersJList().getSelectedValue();
			if (removeUser == null) return;
			this.setACLState(this.getACLState().removeUserFromACL(removeUser));
			if (this.getACLState().getAclType() == ACLState.ACLType.ACL && this.getACLState().getAccessList().isEmpty()){
				this.setACLState(ACLState.PRIVATE_TYPE);
			}
		} catch (java.lang.Throwable throwable) {
			this.handleException(throwable);
		}
	}
	/**
	 * Return the ACLButtonGroup property value.
	 * @return javax.swing.ButtonGroup
	 */
	private javax.swing.ButtonGroup getACLRadioButtonGroup() {
		if (this.groupOfRadioButtons != null) return this.groupOfRadioButtons;
		try {
			this.groupOfRadioButtons = new ButtonGroup();
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
		return this.groupOfRadioButtons;
	}
	/**
	 * Return the ACLRadioButton property value.
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getSpecificAccessRadioButton() {
		if (this.specificAccessRadioButton != null) return this.specificAccessRadioButton;
		try {
			this.specificAccessRadioButton = new JRadioButton();
			this.specificAccessRadioButton.setName("ACLRadioButton");
			this.specificAccessRadioButton.setText("Grant Access To Specific Users");
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
		return this.specificAccessRadioButton;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/23/2004 5:15:41 PM)
	 */
	public ACLState getACLState() {
		return this.aclState;
	}

	public void resetAndGrantVCellSupportPermissions() {
		// disables all UI except grant VCell support permissions
		this.getPublicRadioButton().setEnabled(false);
		this.getPrivateRadioButton().setEnabled(false);
		this.getSpecificAccessRadioButton().setSelected(true);
		this.getVCellSupportCheckBox().setSelected(true);
		this.getAddUserJButton().setEnabled(false);
		this.getRemoveUserJButton().setEnabled(false);
	}

	/**
	 * Return the JPanel property value.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel existingAccessSubPane() {
		if (this.grantAccessJPanel != null) return this.grantAccessJPanel;
		try {
			this.grantAccessJPanel = new JPanel();
			this.grantAccessJPanel.setName("GrantAccessJPanel");
			this.grantAccessJPanel.setBorder(new javax.swing.border.EtchedBorder());
			this.grantAccessJPanel.setLayout(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0; gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.gridwidth = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			this.grantAccessJPanel.add(this.getVCellSupportCheckBox(), gbc);

			GridBagConstraints userNameLabelGBC = new GridBagConstraints();
			userNameLabelGBC.gridx = 0; userNameLabelGBC.gridy = 1;
			userNameLabelGBC.anchor = GridBagConstraints.LINE_START;
			userNameLabelGBC.insets = new Insets(4, 4, 4, 4);
			userNameLabelGBC.gridwidth = 2;
			userNameLabelGBC.fill = GridBagConstraints.HORIZONTAL;
			this.grantAccessJPanel.add(new JLabel("Enter Name of User To Add"), userNameLabelGBC);

			GridBagConstraints addUserTextFieldGBC = new GridBagConstraints();
			addUserTextFieldGBC.gridx = 0; addUserTextFieldGBC.gridy = 2;
			addUserTextFieldGBC.anchor = GridBagConstraints.LINE_START;
			addUserTextFieldGBC.insets = new Insets(4, 4, 4, 4);
			addUserTextFieldGBC.weightx = 1.0;
			addUserTextFieldGBC.fill = GridBagConstraints.HORIZONTAL;
			this.grantAccessJPanel.add(this.getAddUserJTextField(), addUserTextFieldGBC);

			GridBagConstraints addUserJButtonGBC = new GridBagConstraints();
			addUserJButtonGBC.gridx = 0; addUserJButtonGBC.gridy = 3;
			addUserJButtonGBC.anchor = GridBagConstraints.WEST;
			addUserJButtonGBC.insets = new Insets(5, 5, 5, 5);
			addUserJButtonGBC.fill = GridBagConstraints.NONE;
			this.grantAccessJPanel.add(this.getAddUserJButton(), addUserJButtonGBC);

			GridBagConstraints currentUsersLabelGBC = new GridBagConstraints();
			currentUsersLabelGBC.gridx = 0; currentUsersLabelGBC.gridy = 4;
			currentUsersLabelGBC.anchor = GridBagConstraints.LINE_START;
			currentUsersLabelGBC.insets = new Insets(4, 4, 4, 4);
			currentUsersLabelGBC.gridwidth = 2;
			currentUsersLabelGBC.weightx = 1.0;
			currentUsersLabelGBC.fill = GridBagConstraints.HORIZONTAL;
			this.grantAccessJPanel.add(new JLabel("Other Users with Access"), currentUsersLabelGBC);

			GridBagConstraints validUsersGBC = new GridBagConstraints();
			validUsersGBC.gridx = 0; validUsersGBC.gridy = 5;
			validUsersGBC.anchor = GridBagConstraints.LINE_START;
			validUsersGBC.insets = new Insets(4, 4, 4, 4);
			validUsersGBC.weightx = 3.0;
			validUsersGBC.weighty = 3.0;
			validUsersGBC.fill = GridBagConstraints.BOTH;
			this.grantAccessJPanel.add(this.getValidUsersJScrollPane(), validUsersGBC);

			GridBagConstraints removeUserJButtonGBC = new GridBagConstraints();
			removeUserJButtonGBC.gridx = 0; removeUserJButtonGBC.gridy = 6;
			removeUserJButtonGBC.anchor = GridBagConstraints.WEST;
			removeUserJButtonGBC.insets = new Insets(5, 5, 5, 5);
			removeUserJButtonGBC.fill = GridBagConstraints.NONE;
			this.grantAccessJPanel.add(this.getRemoveUserJButton(), removeUserJButtonGBC);

		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
		return this.grantAccessJPanel;
	}
	/**
	 * Return the JButtonAddACLUser property value.
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getAddUserJButton() {
		if (this.addUserJButton != null) return this.addUserJButton;
		try {
			this.addUserJButton = new JButton();
			this.addUserJButton.setName("JButtonAddACLUser");
			this.addUserJButton.setText("Give User Access");
			this.addUserJButton.setActionCommand("JButtonAdd");
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
		return this.addUserJButton;
	}
	/**
	 * Return the JButtonRemoveACLUser property value.
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getRemoveUserJButton() {
		if (this.removeUserJButton != null) return this.removeUserJButton;
		try {
			this.removeUserJButton = new JButton();
			this.removeUserJButton.setName("JButtonRemoveACLUser");
			this.removeUserJButton.setText("Remove Access from User");
			this.removeUserJButton.setActionCommand("JButtonRemoveAccess");
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
		return this.removeUserJButton;
	}
	/**
	 * Return the JListACL property value.
	 * @return javax.swing.JList
	 */
	private javax.swing.JList<Object> getValidUsersJList() {
		if (this.validUsersJList != null) return this.validUsersJList;
		try {
			this.validUsersJList = new JList<>();
			this.validUsersJList.setName("JListACL");
			this.validUsersJList.setBounds(0, 0, 160, 120);
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
		return this.validUsersJList;
	}
	/**
	 * Return the JScrollPane1 property value.
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getValidUsersJScrollPane() {
		if (this.validUsersJScrollPane != null) return this.validUsersJScrollPane;
		try {
			this.validUsersJScrollPane = new JScrollPane();
			this.validUsersJScrollPane.setName("JScrollPane1");
			this.validUsersJScrollPane.setAutoscrolls(true);
			this.validUsersJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			this.getValidUsersJScrollPane().setViewportView(this.getValidUsersJList());
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
		return this.validUsersJScrollPane;
	}
	/**
	 * Return the JTextFieldACLUser property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getAddUserJTextField() {
		if (this.addUserJTextField != null) return this.addUserJTextField;
		try {
			this.addUserJTextField = new JTextField();
			this.addUserJTextField.setName("JTextFieldACLUser");
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
		return this.addUserJTextField;
	}
	/**
	 * Return the PrivateRadioButton property value.
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getPrivateRadioButton() {
		if (this.privateRadioButton != null) return this.privateRadioButton;
		try {
			this.privateRadioButton = new JRadioButton();
			this.privateRadioButton.setName("PrivateRadioButton");
			this.privateRadioButton.setText("Private");
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
		return this.privateRadioButton;
	}
	/**
	 * Return the PublicRadioButton property value.
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getPublicRadioButton() {
		if (this.publicRadioButton != null) return this.publicRadioButton;
		try {
			this.publicRadioButton = new JRadioButton();
			this.publicRadioButton.setName("PublicRadioButton");
			this.publicRadioButton.setText("Public");
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
		return this.publicRadioButton;
	}

	private javax.swing.JCheckBox getVCellSupportCheckBox() {
		if (this.vcellSupportCheckBox != null) return this.vcellSupportCheckBox;
		try {
			this.vcellSupportCheckBox = new JCheckBox();
			this.vcellSupportCheckBox.setText("VCell Support");
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
		return this.vcellSupportCheckBox;
	}

	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		 exception.printStackTrace(System.out);
	}
	/**
	 * Initializes connections
	 * @exception java.lang.Exception The exception description.
	 */
	private void initConnections() throws java.lang.Exception {
		this.addPropertyChangeListener(this.ivjEventHandler);
		this.getAddUserJButton().addActionListener(this.ivjEventHandler);
		this.getRemoveUserJButton().addActionListener(this.ivjEventHandler);
		this.getPublicRadioButton().addItemListener(this.ivjEventHandler);
		this.getPrivateRadioButton().addItemListener(this.ivjEventHandler);
		this.getSpecificAccessRadioButton().addItemListener(this.ivjEventHandler);
		this.getVCellSupportCheckBox().addItemListener(this.ivjEventHandler);
	}
	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
			this.setName("ACLEditor");
			this.setLayout(new java.awt.GridBagLayout());
			this.setSize(333, 379);

			java.awt.GridBagConstraints constraintsPublicRadioButton = new java.awt.GridBagConstraints();
			constraintsPublicRadioButton.gridx = 0; constraintsPublicRadioButton.gridy = 0;
			constraintsPublicRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPublicRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			this.add(this.getPublicRadioButton(), constraintsPublicRadioButton);

			java.awt.GridBagConstraints constraintsPrivateRadioButton = new java.awt.GridBagConstraints();
			constraintsPrivateRadioButton.gridx = 0; constraintsPrivateRadioButton.gridy = 1;
			constraintsPrivateRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPrivateRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			this.add(this.getPrivateRadioButton(), constraintsPrivateRadioButton);


			java.awt.GridBagConstraints constraintsACLRadioButton = new java.awt.GridBagConstraints();
			constraintsACLRadioButton.gridx = 0; constraintsACLRadioButton.gridy = 2;
			constraintsACLRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsACLRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			this.add(this.getSpecificAccessRadioButton(), constraintsACLRadioButton);

			java.awt.GridBagConstraints constraintsGrantAccessJPanel = new java.awt.GridBagConstraints();
			constraintsGrantAccessJPanel.gridx = 0; constraintsGrantAccessJPanel.gridy = 3;
			constraintsGrantAccessJPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsGrantAccessJPanel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGrantAccessJPanel.weightx = 1.0;
			constraintsGrantAccessJPanel.weighty = 1.0;
			constraintsGrantAccessJPanel.insets = new java.awt.Insets(5, 25, 5, 10);
			this.add(this.existingAccessSubPane(), constraintsGrantAccessJPanel);
			this.initConnections();
			this.initializeRadioButtonGroup();
		} catch (java.lang.Throwable throwable) {
			this.handleException(throwable);
		}
	}

	private void setParentConfirmChangesButtonToCorrectState(){
		// Type check
		ACLState.ACLType originalState = this.originalACLType;
		ACLState.ACLType currentState = this.getACLState().getAclType();
		if (!originalState.equals(currentState)) {
			// Check if they have started (but haven't yet) added users to the ACL ...if so, don't allow them to confirm yet!
			boolean hasMadeChanges = !ACLState.ACLType.ACL.equals(currentState) || !this.getACLState().getAccessList().isEmpty();
			if (hasMadeChanges) {
				this.parentConfirmChangesJButton.setEnabled(true);
				return;
			}
		}


		if (!ACLState.ACLType.ACL.equals(currentState)){
			this.parentConfirmChangesJButton.setEnabled(false);
			return;
		}
		// Check that they've modified the users, since they're in ACL mode.
		Set<String> originalSet = new HashSet<>(this.originalValidUsersList);
		Set<String> currentSet = new HashSet<>(this.getACLState().getAccessList());
		this.parentConfirmChangesJButton.setEnabled(!originalSet.equals(currentSet));
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			ACLEditor aACLEditor;
			aACLEditor = new ACLEditor();
			frame.setContentPane(aACLEditor);
			frame.setSize(aACLEditor.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				}
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
	 * Insert the method's description here.
	 * Creation date: (6/23/2004 5:15:41 PM)
	 * @param aclState java.lang.Object
	 */
	public void setACLState(ACLState aclState) {
		ACLState oldValue = this.aclState;
		this.aclState = aclState;
		this.firePropertyChange("ACLState", oldValue, aclState);
	}
	/**
	 * Comment
	 */
	private void updateInterface() {
		ACLState currentState = this.getACLState();
		Vector<String> newList = new Vector<>();
		for (String u : currentState.getAccessList()) {
			if (u.equals(PropertyLoader.VCELL_SUPPORT_USERID)) {
				if (!this.getVCellSupportCheckBox().isSelected()) {
					this.getVCellSupportCheckBox().setSelected(true);
				}
			} else {
				newList.add(u);
			}
		}
		this.getValidUsersJList().setListData(newList);

		boolean enableSubPanel = false;
		switch (currentState.getAclType()){
			case PRIVATE -> this.getPrivateRadioButton().setSelected(true);
			case PUBLIC -> this.getPublicRadioButton().setSelected(true);
			case ACL -> {
				this.getAddUserJTextField().setText(null);
				this.getSpecificAccessRadioButton().setSelected(true);
				enableSubPanel = true;
			}
		}

		GeneralGuiUtils.enableComponents(this.existingAccessSubPane(), enableSubPanel);

//		if(currentState.getAclType().equals(ACLState.ACLType.PRIVATE)){
//			if(!this.getPrivateRadioButton().isSelected()){
//				this.getPrivateRadioButton().setSelected(true);
//			}
//			if(this.existingAccessSubPane().isEnabled()){
//				GeneralGuiUtils.enableComponents(this.existingAccessSubPane(),false);
//			}
//		}else if(currentState.getAclType().equals(ACLState.ACLType.PUBLIC)){
//			if(!this.getPublicRadioButton().isSelected()){
//				this.getPublicRadioButton().setSelected(true);
//			}
//			if(this.existingAccessSubPane().isEnabled()){
//				GeneralGuiUtils.enableComponents(this.existingAccessSubPane(),false);
//			}
//		} else { // aclType is ACL
//			this.getAddUserJTextField().setText(null);
//			if(!this.getSpecificAccessRadioButton().isSelected()){
//				this.getSpecificAccessRadioButton().setSelected(true);
//			}
//			GeneralGuiUtils.enableComponents(this.existingAccessSubPane(),true);
//		}
	}

}
