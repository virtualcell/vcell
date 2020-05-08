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

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JLabel;

import org.vcell.util.BeanUtils;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.GroupAccessSome;
import org.vcell.util.document.User;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.constants.GuiConstants;

/**
 * Insert the type's description here.
 * Creation date: (6/21/2004 5:16:25 PM)
 * @author: Anuradha Lakshminarayana
 */
@SuppressWarnings("serial")
public class ACLEditor extends javax.swing.JPanel {

	// ACL State class
	public static class ACLState {
		private boolean isPrivate = true;
		private String[] aclList = null;

		public static final ACLState PRIVATE_TYPE = new ACLState(true);
		public static final ACLState PUBLIC_TYPE = new ACLState(false);
		
		private ACLState(boolean argIsPrivate) {
			isPrivate = argIsPrivate;
		}
		public ACLState(String[] argACLList) {
			if(argACLList == null){
				aclList = new String[0];
			}else{
				aclList = argACLList;
			}
		}
		public ACLState(GroupAccess argGroupAccess) {
			if(argGroupAccess == null){
				throw new IllegalArgumentException("GroupAccess cannot be null");
			}
			if(argGroupAccess instanceof GroupAccessNone){
				isPrivate = true;
			}else if(argGroupAccess instanceof GroupAccessAll){
				isPrivate = false;
			}else if(argGroupAccess instanceof GroupAccessSome){
				User[] users = ((GroupAccessSome)argGroupAccess).getNormalGroupMembers();
				aclList = new String[users.length];
				for(int i=0;i<users.length;i+= 1){
					aclList[i] = users[i].getName();
				}
			}
		}		
		public boolean isAccessPrivate(){
			return isPrivate && (aclList == null);
		}
		public boolean isAccessPublic(){
			return !isPrivate && (aclList == null);
		}
		public String[] getAccessList(){
			return aclList;
		}
	}
	//
	private ACLState fieldACLState = null;
	private javax.swing.ButtonGroup ivjACLButtonGroup = null;
	private javax.swing.JRadioButton ivjACLRadioButton = null;
	private javax.swing.JRadioButton ivjPrivateRadioButton = null;
	private javax.swing.JRadioButton ivjPublicRadioButton = null;
	private javax.swing.JCheckBox vcellSupportCheckBox = null;
	private javax.swing.JButton ivjJButtonAddACLUser = null;
	private javax.swing.JButton ivjJButtonRemoveACLUser = null;
	private javax.swing.JList ivjJListACL = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTextField ivjJTextFieldACLUser = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	private class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ACLEditor.this.getJButtonAddACLUser()) 
				connEtoC6(e);
			if (e.getSource() == ACLEditor.this.getJButtonRemoveACLUser()) 
				connEtoC7(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == ACLEditor.this.getPublicRadioButton()) 
				connEtoC3(e);
			if (e.getSource() == ACLEditor.this.getPrivateRadioButton()) 
				connEtoC4(e);
			if (e.getSource() == ACLEditor.this.getACLRadioButton()) 
				connEtoC5(e);
			if (e.getSource() == ACLEditor.this.getVCellSupportCheckBox()) 
				actionACLState(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ACLEditor.this && (evt.getPropertyName().equals("ACLState"))) 
				connEtoC2(evt);
		}
	};
	private javax.swing.JPanel ivjGrantAccessJPanel = null;
/**
 * ACLEditor constructor comment.
 */
public ACLEditor() {
	super();
	initialize();
}
/**
 * Comment
 */
private void accessAction(java.awt.event.ActionEvent actionEvent) {
	if(actionEvent.getSource() == getJButtonAddACLUser()){
		if(getJTextFieldACLUser().getText().length() > 0){
			String[] oldAccessList = getACLState().getAccessList();
			if(oldAccessList != null && oldAccessList.length > 0){
				if(BeanUtils.arrayContains(oldAccessList,getJTextFieldACLUser().getText())){
					PopupGenerator.showErrorDialog(this, "User "+getJTextFieldACLUser().getText()+" already in list");
					return;
				}
			}
			ACLState newState =	new ACLState((String[])BeanUtils.addElement(
						(getACLState() != null?getACLState().getAccessList():new String[0]),getJTextFieldACLUser().getText()));
			setACLState(newState);
		}
	}else if(actionEvent.getSource() == getJButtonRemoveACLUser() && getACLState() != null){
		String removeUser = (String)getJListACL().getSelectedValue();
		if(removeUser != null){
			String[] newUserList = (String[])BeanUtils.removeElement(getACLState().getAccessList(),removeUser);
			ACLState newState = new ACLState(newUserList);
			setACLState(newState);
		}
	}
}
/**
 * Comment
 */
private void aCLEditor_Initialize() {
	getACLButtonGroup().add(getPublicRadioButton());
	getACLButtonGroup().add(getPrivateRadioButton());
	getACLButtonGroup().add(getACLRadioButton());
}
/**
 * Comment
 */
private void actionACLState(java.awt.event.ItemEvent itemEvent) {
	
	if(itemEvent.getStateChange() != java.awt.event.ItemEvent.SELECTED && itemEvent.getSource() != getVCellSupportCheckBox()){
		return;
	}
	
	if(itemEvent.getSource() == getPrivateRadioButton()){
		setACLState(ACLState.PRIVATE_TYPE);
	}else if(itemEvent.getSource() == getPublicRadioButton()){
		setACLState(ACLState.PUBLIC_TYPE);
	}else if(itemEvent.getSource() == getVCellSupportCheckBox() || itemEvent.getSource() == getACLRadioButton()){
		ArrayList<String> aclList = new ArrayList<String>();
		for(int i=0;i<getJListACL().getModel().getSize();i+= 1){
			aclList.add((String)getJListACL().getModel().getElementAt(i));
		}
		if (vcellSupportCheckBox.isSelected()) {
			aclList.add(GuiConstants.VCELL_SUPPORT_ACCOUNT_ID);
		}
		String[] array = new String[aclList.size()];
		aclList.toArray(array);
		setACLState(new ACLState(array));
	}
}
/**
 * Comment
 */
public void clearACLList() {
	getJListACL().setListData(new Object[0]);
	
	getPublicRadioButton().setEnabled(true);
	getPrivateRadioButton().setEnabled(true);
	getVCellSupportCheckBox().setSelected(false);
}
/**
 * connEtoC1:  (ACLEditor.initialize() --> ACLEditor.aCLEditor_Initialize()V)
 */
private void connEtoC1() {
	try {
		this.aCLEditor_Initialize();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (ACLEditor.ACLState --> ACLEditor.updateInterface()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
private void connEtoC2(java.beans.PropertyChangeEvent arg1) {
	try {
		this.updateInterface();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (PublicRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ACLEditor.actionACLState(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
	try {
		this.actionACLState(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (PrivateRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ACLEditor.actionACLState(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC4(java.awt.event.ItemEvent arg1) {
	try {
		this.actionACLState(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (ACLRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ACLEditor.actionACLState(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC5(java.awt.event.ItemEvent arg1) {
	try {
		this.actionACLState(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JButtonAddACLUser.action.actionPerformed(java.awt.event.ActionEvent) --> ACLEditor.accessAction(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		this.accessAction(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (JButtonRemoveACLUser.action.actionPerformed(java.awt.event.ActionEvent) --> ACLEditor.accessAction(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		this.accessAction(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Return the ACLButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
private javax.swing.ButtonGroup getACLButtonGroup() {
	if (ivjACLButtonGroup == null) {
		try {
			ivjACLButtonGroup = new javax.swing.ButtonGroup();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjACLButtonGroup;
}
/**
 * Return the ACLRadioButton property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getACLRadioButton() {
	if (ivjACLRadioButton == null) {
		try {
			ivjACLRadioButton = new javax.swing.JRadioButton();
			ivjACLRadioButton.setName("ACLRadioButton");
			ivjACLRadioButton.setText("Grant Access To Specific Users");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjACLRadioButton;
}
/**
 * Insert the method's description here.
 * Creation date: (6/23/2004 5:15:41 PM)
 * @param aclState java.lang.Object
 */
public ACLState getACLState() {
	return fieldACLState;
}

public void grantVCellSupportPermissions() {
	// disables all UI except grant vCell support permissions
	getPublicRadioButton().setEnabled(false);
	getPrivateRadioButton().setEnabled(false);
	getACLRadioButton().setSelected(true);
	getVCellSupportCheckBox().setSelected(true);
}

/**
 * Return the JPanel property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getGrantAccessJPanel() {
	if (ivjGrantAccessJPanel == null) {
		try {
			ivjGrantAccessJPanel = new javax.swing.JPanel();
			ivjGrantAccessJPanel.setName("GrantAccessJPanel");
			ivjGrantAccessJPanel.setBorder(new javax.swing.border.EtchedBorder());
			ivjGrantAccessJPanel.setLayout(new java.awt.GridBagLayout());
			
			java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
			gbc.gridx = 0; gbc.gridy = 0;
			gbc.anchor = java.awt.GridBagConstraints.WEST;
			gbc.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjGrantAccessJPanel.add(getVCellSupportCheckBox(), gbc);
			
			java.awt.GridBagConstraints constraintsCurrentUsersLabel = new java.awt.GridBagConstraints();
			constraintsCurrentUsersLabel.gridx = 0; constraintsCurrentUsersLabel.gridy = 1;
			constraintsCurrentUsersLabel.anchor = java.awt.GridBagConstraints.LINE_START;
			constraintsCurrentUsersLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			constraintsCurrentUsersLabel.gridwidth = 2;
			constraintsCurrentUsersLabel.weightx = 1.0;
			constraintsCurrentUsersLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			getGrantAccessJPanel().add(new JLabel("Users Granted Access"), constraintsCurrentUsersLabel);

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 2;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
			constraintsJScrollPane1.anchor = GridBagConstraints.LINE_START;
			getGrantAccessJPanel().add(getJScrollPane1(), constraintsJScrollPane1);

			java.awt.GridBagConstraints constraintsJButtonRemoveACLUser = new java.awt.GridBagConstraints();
			constraintsJButtonRemoveACLUser.gridx = 1; constraintsJButtonRemoveACLUser.gridy = 2;
			constraintsJButtonRemoveACLUser.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonRemoveACLUser.anchor = java.awt.GridBagConstraints.PAGE_START;
			constraintsJButtonRemoveACLUser.insets = new java.awt.Insets(5, 3, 5, 7);
			getGrantAccessJPanel().add(getJButtonRemoveACLUser(), constraintsJButtonRemoveACLUser);

			java.awt.GridBagConstraints constraintsUserNameLabel = new java.awt.GridBagConstraints();
			constraintsUserNameLabel.gridx = 0; constraintsUserNameLabel.gridy = 3;
			constraintsUserNameLabel.anchor = java.awt.GridBagConstraints.LINE_START;
			constraintsUserNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getGrantAccessJPanel().add(new JLabel("Enter User"), constraintsUserNameLabel);

			java.awt.GridBagConstraints constraintsJTextFieldACLUser = new java.awt.GridBagConstraints();
			constraintsJTextFieldACLUser.gridx = 0; constraintsJTextFieldACLUser.gridy = 4;
			constraintsJTextFieldACLUser.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJTextFieldACLUser.anchor = java.awt.GridBagConstraints.LINE_START;
			constraintsJTextFieldACLUser.weightx = 1.0;
			constraintsJTextFieldACLUser.insets = new java.awt.Insets(4, 4, 4, 4);
			getGrantAccessJPanel().add(getJTextFieldACLUser(), constraintsJTextFieldACLUser);

			java.awt.GridBagConstraints constraintsJButtonAddACLUser = new java.awt.GridBagConstraints();
			constraintsJButtonAddACLUser.gridx = 1; constraintsJButtonAddACLUser.gridy = 4;
			constraintsJButtonAddACLUser.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJButtonAddACLUser.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonAddACLUser.insets = new java.awt.Insets(5, 5, 5, 5);
			getGrantAccessJPanel().add(getJButtonAddACLUser(), constraintsJButtonAddACLUser);

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjGrantAccessJPanel;
}
/**
 * Return the JButtonAddACLUser property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getJButtonAddACLUser() {
	if (ivjJButtonAddACLUser == null) {
		try {
			ivjJButtonAddACLUser = new javax.swing.JButton();
			ivjJButtonAddACLUser.setName("JButtonAddACLUser");
			ivjJButtonAddACLUser.setText("Add User");
			ivjJButtonAddACLUser.setActionCommand("JButtonAdd");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonAddACLUser;
}
/**
 * Return the JButtonRemoveACLUser property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getJButtonRemoveACLUser() {
	if (ivjJButtonRemoveACLUser == null) {
		try {
			ivjJButtonRemoveACLUser = new javax.swing.JButton();
			ivjJButtonRemoveACLUser.setName("JButtonRemoveACLUser");
			ivjJButtonRemoveACLUser.setText("Remove User");
			ivjJButtonRemoveACLUser.setActionCommand("JButtonRemoveAccess");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonRemoveACLUser;
}
/**
 * Return the JListACL property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getJListACL() {
	if (ivjJListACL == null) {
		try {
			ivjJListACL = new javax.swing.JList();
			ivjJListACL.setName("JListACL");
			ivjJListACL.setBounds(0, 0, 160, 120);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJListACL;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setAutoscrolls(true);
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getJScrollPane1().setViewportView(getJListACL());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the JTextFieldACLUser property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getJTextFieldACLUser() {
	if (ivjJTextFieldACLUser == null) {
		try {
			ivjJTextFieldACLUser = new javax.swing.JTextField();
			ivjJTextFieldACLUser.setName("JTextFieldACLUser");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldACLUser;
}
/**
 * Return the PrivateRadioButton property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getPrivateRadioButton() {
	if (ivjPrivateRadioButton == null) {
		try {
			ivjPrivateRadioButton = new javax.swing.JRadioButton();
			ivjPrivateRadioButton.setName("PrivateRadioButton");
			ivjPrivateRadioButton.setText("Private");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPrivateRadioButton;
}
/**
 * Return the PublicRadioButton property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getPublicRadioButton() {
	if (ivjPublicRadioButton == null) {
		try {
			ivjPublicRadioButton = new javax.swing.JRadioButton();
			ivjPublicRadioButton.setName("PublicRadioButton");
			ivjPublicRadioButton.setText("Public");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPublicRadioButton;
}

private javax.swing.JCheckBox getVCellSupportCheckBox() {
	if (vcellSupportCheckBox == null) {
		try {
			vcellSupportCheckBox = new javax.swing.JCheckBox();
			vcellSupportCheckBox.setText("VCell Support");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return vcellSupportCheckBox;
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
	this.addPropertyChangeListener(ivjEventHandler);
	getJButtonAddACLUser().addActionListener(ivjEventHandler);
	getJButtonRemoveACLUser().addActionListener(ivjEventHandler);
	getPublicRadioButton().addItemListener(ivjEventHandler);
	getPrivateRadioButton().addItemListener(ivjEventHandler);
	getACLRadioButton().addItemListener(ivjEventHandler);
	getVCellSupportCheckBox().addItemListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ACLEditor");
		setLayout(new java.awt.GridBagLayout());
		setSize(333, 379);

		java.awt.GridBagConstraints constraintsPublicRadioButton = new java.awt.GridBagConstraints();
		constraintsPublicRadioButton.gridx = 0; constraintsPublicRadioButton.gridy = 0;
		constraintsPublicRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPublicRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPublicRadioButton(), constraintsPublicRadioButton);

		java.awt.GridBagConstraints constraintsPrivateRadioButton = new java.awt.GridBagConstraints();
		constraintsPrivateRadioButton.gridx = 0; constraintsPrivateRadioButton.gridy = 1;
		constraintsPrivateRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPrivateRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPrivateRadioButton(), constraintsPrivateRadioButton);


		java.awt.GridBagConstraints constraintsACLRadioButton = new java.awt.GridBagConstraints();
		constraintsACLRadioButton.gridx = 0; constraintsACLRadioButton.gridy = 2;
		constraintsACLRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsACLRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getACLRadioButton(), constraintsACLRadioButton);

		java.awt.GridBagConstraints constraintsGrantAccessJPanel = new java.awt.GridBagConstraints();
		constraintsGrantAccessJPanel.gridx = 0; constraintsGrantAccessJPanel.gridy = 3;
		constraintsGrantAccessJPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGrantAccessJPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsGrantAccessJPanel.weightx = 1.0;
		constraintsGrantAccessJPanel.weighty = 1.0;
		constraintsGrantAccessJPanel.insets = new java.awt.Insets(5, 25, 5, 10);
		add(getGrantAccessJPanel(), constraintsGrantAccessJPanel);
		initConnections();
		connEtoC1();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
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
			};
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
	
	ACLState oldValue = fieldACLState;
	fieldACLState = aclState;
	firePropertyChange("ACLState", oldValue, aclState);

}
/**
 * Comment
 */
private void updateInterface() {

	
	ACLState currentState = getACLState();


	if(currentState.isAccessPrivate()){
		if(!getPrivateRadioButton().isSelected()){
			getPrivateRadioButton().setSelected(true);
		}
		if(getGrantAccessJPanel().isEnabled()){
			BeanUtils.enableComponents(getGrantAccessJPanel(),false);
		}
	}else if(currentState.isAccessPublic()){
		if(!getPublicRadioButton().isSelected()){
			getPublicRadioButton().setSelected(true);
		}
		if(getGrantAccessJPanel().isEnabled()){
			BeanUtils.enableComponents(getGrantAccessJPanel(),false);
		}
	}else if(currentState != null){
		String[] currentUserList = (currentState != null?currentState.getAccessList():new String[0]);
		Vector<String> newList = new Vector<String>();
		for (String u : currentUserList) {
			if (u.equals(GuiConstants.VCELL_SUPPORT_ACCOUNT_ID)) {
				if (!getVCellSupportCheckBox().isSelected()) {
					getVCellSupportCheckBox().setSelected(true);				
				}
			} else {
				newList.add(u);
			}
		}		
		getJListACL().setListData(newList);
		getJTextFieldACLUser().setText(null);
		if(!getACLRadioButton().isSelected()){
			getACLRadioButton().setSelected(true);
		}
		if(!getJTextFieldACLUser().isEnabled()){
			BeanUtils.enableComponents(getGrantAccessJPanel(),true);
		}
	}
}

}
