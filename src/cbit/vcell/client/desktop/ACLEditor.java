package cbit.vcell.client.desktop;

import org.vcell.util.BeanUtils;

import cbit.vcell.client.PopupGenerator;

/**
 * Insert the type's description here.
 * Creation date: (6/21/2004 5:16:25 PM)
 * @author: Anuradha Lakshminarayana
 */
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
		public ACLState(org.vcell.util.document.GroupAccess argGroupAccess) {
			if(argGroupAccess == null){
				throw new IllegalArgumentException("GroupAccess cannot be null");
			}
			if(argGroupAccess instanceof org.vcell.util.document.GroupAccessNone){
				isPrivate = true;
			}else if(argGroupAccess instanceof org.vcell.util.document.GroupAccessAll){
				isPrivate = false;
			}else if(argGroupAccess instanceof org.vcell.util.document.GroupAccessSome){
				org.vcell.util.document.User[] users = ((org.vcell.util.document.GroupAccessSome)argGroupAccess).getNormalGroupMembers();
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
	private javax.swing.JButton ivjJButtonAddACLUser = null;
	private javax.swing.JButton ivjJButtonRemoveACLUser = null;
	private javax.swing.JList ivjJListACL = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTextField ivjJTextFieldACLUser = null;
	private javax.swing.JLabel ivjUserNameLabel = null;
	private javax.swing.JLabel ivjCurrentUsersLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
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
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ACLEditor.this && (evt.getPropertyName().equals("ACLState"))) 
				connEtoC2(evt);
		};
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
	//cbit.util.BeanUtils.enableComponents(this,false);
	getACLButtonGroup().add(getPublicRadioButton());
	getACLButtonGroup().add(getPrivateRadioButton());
	getACLButtonGroup().add(getACLRadioButton());
}
/**
 * Comment
 */
private void actionACLState(java.awt.event.ItemEvent itemEvent) {
	
	if(itemEvent.getStateChange() != java.awt.event.ItemEvent.SELECTED){
		return;
	}
	
	if(itemEvent.getSource() == getPrivateRadioButton()){
		setACLState(ACLState.PRIVATE_TYPE);
	}else if(itemEvent.getSource() == getPublicRadioButton()){
		setACLState(ACLState.PUBLIC_TYPE);
	}else if(itemEvent.getSource() == getACLRadioButton()){
		String[] aclList = new String[getJListACL().getModel().getSize()];
		for(int i=0;i<aclList.length;i+= 1){
			aclList[i] = (String)getJListACL().getModel().getElementAt(i);
		}
		setACLState(new ACLState(aclList));
	}
}
/**
 * Comment
 */
public void clearACLList() {
	getJListACL().setListData(new Object[0]);
}
/**
 * connEtoC1:  (ACLEditor.initialize() --> ACLEditor.aCLEditor_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.aCLEditor_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (ACLEditor.ACLState --> ACLEditor.updateInterface()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateInterface();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (PublicRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ACLEditor.actionACLState(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.actionACLState(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (PrivateRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ACLEditor.actionACLState(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.actionACLState(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (ACLRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> ACLEditor.actionACLState(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.actionACLState(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JButtonAddACLUser.action.actionPerformed(java.awt.event.ActionEvent) --> ACLEditor.accessAction(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.accessAction(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (JButtonRemoveACLUser.action.actionPerformed(java.awt.event.ActionEvent) --> ACLEditor.accessAction(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.accessAction(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the ACLButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getACLButtonGroup() {
	if (ivjACLButtonGroup == null) {
		try {
			ivjACLButtonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjACLButtonGroup;
}
/**
 * Return the ACLRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getACLRadioButton() {
	if (ivjACLRadioButton == null) {
		try {
			ivjACLRadioButton = new javax.swing.JRadioButton();
			ivjACLRadioButton.setName("ACLRadioButton");
			ivjACLRadioButton.setText("Grant Access To Specific Users");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
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
/**
 * Return the CurrentUsersLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCurrentUsersLabel() {
	if (ivjCurrentUsersLabel == null) {
		try {
			ivjCurrentUsersLabel = new javax.swing.JLabel();
			ivjCurrentUsersLabel.setName("CurrentUsersLabel");
			ivjCurrentUsersLabel.setText("Users Granted Access");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurrentUsersLabel;
}
/**
 * Return the JPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getGrantAccessJPanel() {
	if (ivjGrantAccessJPanel == null) {
		try {
			ivjGrantAccessJPanel = new javax.swing.JPanel();
			ivjGrantAccessJPanel.setName("GrantAccessJPanel");
			ivjGrantAccessJPanel.setAutoscrolls(false);
			ivjGrantAccessJPanel.setBorder(new javax.swing.border.EtchedBorder());
			ivjGrantAccessJPanel.setLayout(new java.awt.GridBagLayout());
			ivjGrantAccessJPanel.setMaximumSize(new java.awt.Dimension(500, 500));
			ivjGrantAccessJPanel.setPreferredSize(new java.awt.Dimension(250, 150));
			ivjGrantAccessJPanel.setEnabled(true);
			ivjGrantAccessJPanel.setMinimumSize(new java.awt.Dimension(250, 150));

			java.awt.GridBagConstraints constraintsJButtonRemoveACLUser = new java.awt.GridBagConstraints();
			constraintsJButtonRemoveACLUser.gridx = 1; constraintsJButtonRemoveACLUser.gridy = 1;
			constraintsJButtonRemoveACLUser.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonRemoveACLUser.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJButtonRemoveACLUser.insets = new java.awt.Insets(5, 3, 5, 7);
			getGrantAccessJPanel().add(getJButtonRemoveACLUser(), constraintsJButtonRemoveACLUser);

			java.awt.GridBagConstraints constraintsJTextFieldACLUser = new java.awt.GridBagConstraints();
			constraintsJTextFieldACLUser.gridx = 0; constraintsJTextFieldACLUser.gridy = 3;
			constraintsJTextFieldACLUser.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJTextFieldACLUser.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJTextFieldACLUser.weightx = 1.0;
			constraintsJTextFieldACLUser.insets = new java.awt.Insets(5, 5, 5, 0);
			getGrantAccessJPanel().add(getJTextFieldACLUser(), constraintsJTextFieldACLUser);

			java.awt.GridBagConstraints constraintsJButtonAddACLUser = new java.awt.GridBagConstraints();
			constraintsJButtonAddACLUser.gridx = 1; constraintsJButtonAddACLUser.gridy = 3;
			constraintsJButtonAddACLUser.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJButtonAddACLUser.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonAddACLUser.insets = new java.awt.Insets(5, 5, 5, 5);
			getGrantAccessJPanel().add(getJButtonAddACLUser(), constraintsJButtonAddACLUser);

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
			getGrantAccessJPanel().add(getJScrollPane1(), constraintsJScrollPane1);

			java.awt.GridBagConstraints constraintsUserNameLabel = new java.awt.GridBagConstraints();
			constraintsUserNameLabel.gridx = 0; constraintsUserNameLabel.gridy = 2;
			constraintsUserNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUserNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getGrantAccessJPanel().add(getUserNameLabel(), constraintsUserNameLabel);

			java.awt.GridBagConstraints constraintsCurrentUsersLabel = new java.awt.GridBagConstraints();
			constraintsCurrentUsersLabel.gridx = 0; constraintsCurrentUsersLabel.gridy = 0;
			constraintsCurrentUsersLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCurrentUsersLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getGrantAccessJPanel().add(getCurrentUsersLabel(), constraintsCurrentUsersLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGrantAccessJPanel;
}
/**
 * Return the JButtonAddACLUser property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAddACLUser() {
	if (ivjJButtonAddACLUser == null) {
		try {
			ivjJButtonAddACLUser = new javax.swing.JButton();
			ivjJButtonAddACLUser.setName("JButtonAddACLUser");
			ivjJButtonAddACLUser.setText("Add User");
			ivjJButtonAddACLUser.setActionCommand("JButtonAdd");
			ivjJButtonAddACLUser.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJButtonAddACLUser.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjJButtonAddACLUser.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAddACLUser;
}
/**
 * Return the JButtonRemoveACLUser property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRemoveACLUser() {
	if (ivjJButtonRemoveACLUser == null) {
		try {
			ivjJButtonRemoveACLUser = new javax.swing.JButton();
			ivjJButtonRemoveACLUser.setName("JButtonRemoveACLUser");
			ivjJButtonRemoveACLUser.setText("Remove User");
			ivjJButtonRemoveACLUser.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjJButtonRemoveACLUser.setActionCommand("JButtonRemoveAccess");
			ivjJButtonRemoveACLUser.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRemoveACLUser;
}
/**
 * Return the JListACL property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJListACL() {
	if (ivjJListACL == null) {
		try {
			ivjJListACL = new javax.swing.JList();
			ivjJListACL.setName("JListACL");
			ivjJListACL.setBounds(0, 0, 160, 120);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListACL;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setAutoscrolls(true);
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPane1().setViewportView(getJListACL());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the JTextFieldACLUser property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldACLUser() {
	if (ivjJTextFieldACLUser == null) {
		try {
			ivjJTextFieldACLUser = new javax.swing.JTextField();
			ivjJTextFieldACLUser.setName("JTextFieldACLUser");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldACLUser;
}
/**
 * Return the PrivateRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getPrivateRadioButton() {
	if (ivjPrivateRadioButton == null) {
		try {
			ivjPrivateRadioButton = new javax.swing.JRadioButton();
			ivjPrivateRadioButton.setName("PrivateRadioButton");
			ivjPrivateRadioButton.setText("Private");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPrivateRadioButton;
}
/**
 * Return the PublicRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getPublicRadioButton() {
	if (ivjPublicRadioButton == null) {
		try {
			ivjPublicRadioButton = new javax.swing.JRadioButton();
			ivjPublicRadioButton.setName("PublicRadioButton");
			ivjPublicRadioButton.setText("Public");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPublicRadioButton;
}
/**
 * Return the UserNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUserNameLabel() {
	if (ivjUserNameLabel == null) {
		try {
			ivjUserNameLabel = new javax.swing.JLabel();
			ivjUserNameLabel.setName("UserNameLabel");
			ivjUserNameLabel.setText("Enter User");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUserNameLabel;
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getJButtonAddACLUser().addActionListener(ivjEventHandler);
	getJButtonRemoveACLUser().addActionListener(ivjEventHandler);
	getPublicRadioButton().addItemListener(ivjEventHandler);
	getPrivateRadioButton().addItemListener(ivjEventHandler);
	getACLRadioButton().addItemListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
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
		constraintsGrantAccessJPanel.insets = new java.awt.Insets(5, 10, 5, 10);
		add(getGrantAccessJPanel(), constraintsGrantAccessJPanel);
		initConnections();
		connEtoC1();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
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
		if(!getPrivateRadioButton().isSelected()){getPrivateRadioButton().setSelected(true);}
		if(getGrantAccessJPanel().isEnabled()){
			org.vcell.util.BeanUtils.enableComponents(getGrantAccessJPanel(),false);
		}
	}else if(currentState.isAccessPublic()){
		if(!getPublicRadioButton().isSelected()){getPublicRadioButton().setSelected(true);}
		if(getGrantAccessJPanel().isEnabled()){
			org.vcell.util.BeanUtils.enableComponents(getGrantAccessJPanel(),false);
		}
	}else if(currentState != null){
		String[] currentUserList = (currentState != null?currentState.getAccessList():new String[0]);
		getJListACL().setListData(currentUserList);
		getJTextFieldACLUser().setText(null);
		if(!getACLRadioButton().isSelected()){getACLRadioButton().setSelected(true);}
		if(!getCurrentUsersLabel().isEnabled()){
			org.vcell.util.BeanUtils.enableComponents(getGrantAccessJPanel(),true);
		}
	}
}

}
