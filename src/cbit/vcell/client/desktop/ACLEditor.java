package cbit.vcell.client.desktop;

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
		public ACLState(cbit.vcell.server.GroupAccess argGroupAccess) {
			if(argGroupAccess == null){
				throw new IllegalArgumentException("GroupAccess cannot be null");
			}
			if(argGroupAccess instanceof cbit.vcell.server.GroupAccessNone){
				isPrivate = true;
			}else if(argGroupAccess instanceof cbit.vcell.server.GroupAccessAll){
				isPrivate = false;
			}else if(argGroupAccess instanceof cbit.vcell.server.GroupAccessSome){
				org.vcell.util.document.User[] users = ((cbit.vcell.server.GroupAccessSome)argGroupAccess).getNormalGroupMembers();
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
				if(org.vcell.util.BeanUtils.arrayContains(oldAccessList,getJTextFieldACLUser().getText())){
					cbit.vcell.client.PopupGenerator.showErrorDialog("User "+getJTextFieldACLUser().getText()+" already in list");
					return;
				}
			}
			ACLState newState =
				new ACLState(
					(String[])org.vcell.util.BeanUtils.addElement(
						(getACLState() != null?getACLState().getAccessList():new String[0]),getJTextFieldACLUser().getText()));
			setACLState(newState);
		}
	}else if(actionEvent.getSource() == getJButtonRemoveACLUser() && getACLState() != null){
		String removeUser = (String)getJListACL().getSelectedValue();
		if(removeUser != null){
			String[] newUserList = (String[])org.vcell.util.BeanUtils.removeElement(getACLState().getAccessList(),removeUser);
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
		frame.show();
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
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB8FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DF8D4D7152CCBDDF4339837291BF671B3D65434D20D96DDD6D9459A772394DB0CD4D1D397D72C755B68324A562C256B1B1924A4ADA9E9897DB3255152B6149012905AB40412DF7243GC3B2A421D0484B3CB7B3AFF9B3EFF873C212161FBD675EF76F3C19F91324FCF679FA725EBD771CF34F39BF671EF36F1B8A95AFE495BB1C950260A8964CFF2FBB84C16A91045B3E3461FA06EBFAA52FD4307C
	7B934093423D63C5706C013A5CF0DEE939603BD2826533D04ED072CA1F06776D42CF86EB5A61A5C5BBC5D0772DC5E52BE6EE67033D2C1DAD243C6372D6F8BE8EB082B8FCAEF9855B5FFB799993BF5F44B3F0B881D70DB4BD17ABCD5CC620A981DCAB00371A522B70248ADC575CE4521D7DE83630E9156EACC3B996198CD61B3C9B4DE71570FBC766EEEC6B825193F92A21BC8200463B04AFB56D06E70BE7BC7026EFD4B1EAAF7AE4D52D75290A9CB6EAA5B9BAEEE8116666368EF71724981A0E5D77AA4A7B674BA5
	04DF9072C1D696415D9344B60661BD823060D5064FD5903F815EB783D46CE1F69DFA254554FB785DFF9256B6040A3625302BE68F35473BA7E6D7CE68BE6733E48C7E358F7BBA9D6A16822C84A8G58DA13D7DA8370C7B2765F69FD951EED23C607968AE96136C824DD1574E31AA42B708E960E9AA62EDF76E93AA488F4FCA7AF575868B393D03E6B5EB39F6D136C7DEC2C1774FFDE483F7DE34EADB6FD121F7D2E3333067645591AD81F50363F1E326DCBB571F3B13E6DA1415AF6E57777DD45B6EDB7B5FE5F15EE
	4AD9ECE9FBDD8D0EF587996BDCF8C7BE6427786384DF39B706277372DEB2E730DC87F599FB595C98FE1C6962AE3AD9A82F7F875BFE483CA34DD938176AD0DE1B28CB4F984AEE2925EDEE633A8C933CE441D3DDD6919D311C86F5A57B72CA697C3A470D6BF4BE1433G16812C8158883035AE2F342A0E4D31277A764DF10EF9C45DE81FD0D4C956854AFBCF5FD37014278CDD6C8DC78DB16C1387C5DD91C7D5B9AA18B46B7A308FC663786EA3F35F87EDF44BE1D9978DC58B03F4B1A49B329E25B47777BD01B43ACC
	EA3DCAC80686C29101497DF55FF6F82AE254B89511C4C30ED1507A877BF0CCAE0A03A0918440BB6D174D47303FDA417EE100A06F078BC7D15ED1D98F4BEAF333DB5346A7A2DE34C4D8D6C7461C780ECB701ED1477A3870B862B6C339221E4949BE7A24C5CE2762C375C5FD1A2D31DE85F92A013E351E4D1BDF8F33F953D9F61BD0F36F8D3673E6894C613375F43E9875096B47CB6422EE0B6A191F7FF1F0F80E6B383ABEFE0D35565B2F39E41F31E94EED38924A45F360217D6C776018AD871D568194723E7E5951
	AABAF75B45284CE66B899DA61AAC45FA1E6AF9FC2C526CD35463B1G5C0FF0FFFC8160G37679C21ABEF48ABED829881D8G1083308EC0584F7AF60D923148336A4CE44FE65B8BE67BF3693B83F1FD7E1F24ED17CD9FE2FD699C641519FA4E256DED661376798F5DD816AF42D65BA9C7643084AFD133EE3D9B5BB67D01DF63BC7793FC847CC787AC58A8479738F10D0B0621AB239306BCA09B06928E446AB7113622932311186BE1EDBD64C65B35C9709F1CDEB806EB4F270543B299FC4EE256BB0F63BC12D5B984
	CAF7E893E10362CB0EE3FCB4AECF1B3EE76B71C784749D6A847AB19AF3640858DF91DD894114C2D7A8CC6DC73FD040E3A6320EG0731139B1EA37B597A1EFC112D6FDD6F5CA22CB89FF15A6DD1191F7E08F345FE3A6E566E0F2DEF2A5343A9F4F71DA8096123D4575D4FE13B567847C5E83B479E277876894350425D3AB691E1EB6C93A7D17EA6E83724917607C636462E1E40FA664F4C45552129AA6DE228139A319FB2158A0BADCD0D349FE273C6751463D84A97A6E458A650319E657D9D75BC19D0B7E876BD2D
	FF5703FD2FDAFC32F0G5B3CE10C4522CB4C76F24DE7FEA3ED1B521D98E7F46E8334DEB21FD3872CF4A5AA234B04F28E409A132E7CB5AB5DEACE5783781157D2514D8BB13AF9004F84D8769A255B94C75791E6F4B52F6715F63E1E0A6EF71CCE821ACBB6F4F47CFF3B9B67A4090F47440B62D4EDF49256D5F3F31FC704D1243170A049B510FE516BAC667D00836740DAA897G94B531797B5DB29673E60CE78B055D773AD2457215CDD40F5626587C3D740DC9971DE33F6C47B667C1DDFA931BDF8FFA2C7BEEDBB4
	2A799432651BE37F69135679CDF1371EFC941EFEC50F9A3D60002658BC49B9097AE2CCE0224D7EF83876F95B7E08DB86E6BCF979085556655BAEBB9FCBE16BE25346DC0B2D79AF7E4EC563322F0F319831680D3C5296G3A86DF9AC73C92D68CC5D415F7646A5DD4C777E14C77C436E64F77DAA886CD5DEE5E5EEA363D7A73B78BADD74FB96DF20F855B02CE638D2A5322B7E23AADDEFB1FF399G55EFEA1C6977G50D4B5B37D7EED3C047A08818322AA2A5B69B2DC1F93A44E6A8DC30C6697FDC4ADB3862DD719
	2CE110138EC0FD42D01CCFD889780DG9587D9DB19EA835ACC9CC9ADFDF8C0382607E4299AC72AD61CB4EA49DED35BC62A3B70FDB79B57FFFFFE8F61774951A82D2FF627E6BB888AD3FD7F1B6BDB8D3A0C9CE47AAE106C744D04FA4AF7CB0871AD815C864EF748E9652BEC21B1E2CF8B3333BC0CB136E24821DA7CC3FA36A3A8068349E676C23DE9A43551F7EA8F6FA3B6AAC9D6F22E180DEA1869FA9675E9E13A566BF63A2EEEE1B6D6E90CEFBD601EB944783AA3F6FC1EC3AC9EBAF3A5B1FF138E45E24829C349
	71A44DEB9EA4BEA76D904DEBCAB95E65C4FC3685CF75DBC068311C8BF555EF32D8BC570BBEAF8D4A59GABG56819482BCF318791A267E7D42AC398E195EE1D19D088BBAD84E510F6389F102D97ECDBF7693099384360FB68CE2BFB5C21B9E00A040BC00AC2E4374402CBAF4CD596AC07BB9F7AC311FD79C0E6F5B7C04B2654BCB622BCDA88B2D71FC541E85C33867BC83F50DGEE00A0C09AC0B6406AD6E6574A01C3335A05B3542BF5G2E8776720E44B80D6BFAE57C5AECCCCB62CB343120550E6FF5755A5A6B
	CA625B19D0AEEA0B6723EBFA5BG762D4D7ECCB7A4DCAFA436FCDBCB0CADF14DD5024CBAG3A0FAFA4F99F6E47BDEDECADBEF40D761406E633E7C902BDD407EFC828836E1339DC07AFA4491A1B8E7FB22B8E1BED7B7429761D76FD5AAFCA0A46BA166AEBB8D96CD3514E74554357266F3CA43EC4FD5D6DF6FD9654198E925761E56D5AF4E8CC62CB54E1112D8ED7A2CC0725D0CF4FB9F4929796C0D968E038938262F6C1392503693A5901717C88146781E481AC67F525CE2C43F837530CD3F19F25F58B9D567382
	64FDBCC1D784A60F15C7D250A582ED6333F1ED24BAE7C84C15937378D4E01E4DFAD36741EB3D76F9700F3C76F970FA6F93948FB22C27EC2C7E816F4CF972C6EF6ABC39F9B0351EED0376FAF68F5A6BF9ECF0E6BDCF8D265273F9523FF7BE350D70C723F214EC861D22A11AF53FB0F688B6397BF1B1A48BACFFFDE82C542469B2340EFA5E6E5F8F25367146A1FB9BBFB104BEB32ACD68BEB9510E4F8CBD436BE23B9486D9288E25798261B7C4BDA0A77543D788BF2DCB457F0D21D4FDC5ED7D72385556860E8F2BD6
	7CEBFCAD5F201510398FBE220365F35F7611334E4E3C52E100C8A7DB0357ABF6E7F68BBA635758224E64B51713004B0572BA00828B1E6A5990326A79BADF1B39125DB93F50C579AB60D98D508260B1F1D45E7DF17276F3F953CEBBF91249BB5515EC8315F7CB58AA2F114B8B69F67252CCB9D9A9653D2DD965454E937FBAE2A7EF29A9E7E5CAF99F3A02E7D02AB82DCD9830379F41714C3A426201FC605B84D089783AA3B4AE207B6097A7F19F766BE2882653C0C476893B3A1157AFB05EF420DF8C108B30812028
	1B65A94F36627C71CC0C2A0AAFEE5BB4F59A380CF920C7D7AEC29292C7C06BFF99C279102558546D5905323BF5B1EC3411040D9EC670FE3AC943B929F6537E18874F0C6ED85FE4F75B7B7F65B678B5005BGD0E82923B945A1895743569E1AD314F13C1B606BACF81AEB5CC5568F163DD03722076D07DFA6B1D7CDAF4C53DE06FBEA8CF1BDD016B8EE1260A2D01E47F1DB76333DB403639EF994F19B20DC48F1060C3822BE48DF7A986E4792EB370663DAE5A62F09634E79193C859C37D0E27AE5F05C0392EBA30B
	6352C9EC34944A399C77B997EBE38D474D5799DD55D1067B394E68EAB86E2688233B44F18FC5985D0223AC6FDCF9055D752E83DC391B4D677A914CBFF8DC07C9EFF3F3372EC86DE240CDD68775FF65D7CBCC7E9EB773FFEFDEC51973201C891063E667D9D3AF33B31EA51F39D9A86830BF4FDA6F3D6CDC6926F328409DBBC7498D5D6724ED26892C4D6AE3F925A3479077D5411AA7635C7F1D1366A1343EA02E9E5744FD8EEB7DCE07359E757D47B8FECFDCBD765193A6BF356D2E1718ED1B6F4A9154AF4FCFF96F
	1EFD0C5A345AFC26EF385151346DDD979D2365E4BFA9BA8EBE66B81F3BE45C6A205C49F185E44C03D03E44F163866232304CF17A99442D0672839CF7D9E273E0B347DD97C61C7B84746789063B1B608CA8271DE0FBD807047EAC8B4AABG5683889ED8ABG0D9EC653CFE870FC566B21673986BCB38116F11A6F7AC64C7EC0B9FCBF72587B1B75A64DB3B675D9967EC60FDD1CDE405B324624296EEB3618CF3BFB9AEB2C1AF857C47D6DEFA43C53B0A070D96741EFC2DEBDAF703ACFCB48C34F14B0DD9743922EBB
	C2F7ECA4C637F08CE5783530B1FC9247E1B171E71570DE8D301C60F66E6FCC21BF53E50B79C4D884BC88AF1A651C135467BAF81B93FE5C77C290BBA9E12653F2B126D3190C7264B0DE83C882556B63B27B2E23289F629142D3B00ABCD447A1F9B69DDDB6BA3EE40ED3C5FFCCC7AAFB9BF61779FE9A43D3D30FDDDC0F2AFE3627FE5699BF8E177CB13D6EF0B13D6A7A13671335DF90BA018661C593BF1C24579341186C4DBA13AD25106D1AC176D4126CC6A5A67B498BCC767C94320BE7101DE911CD57455DC9E707
	3381654B3FC63E9DD75FAB9F0D934A855CCF96DB7B06601FA5789C8B1E6EE91DD7591E369C6A2A8658D96553E3681766C3B98BE099409A000DGDB3D6CBCEDF438CE1865BC4D2BC55C10742816D40C5A77D1615A3E6379669636F7AC7D6A2D42328FF6263C7B28721A77BC66337AE7B7BA70C96D3BF59C6D0BC0DD9AC08E40CA00FC00CD5C3EDB07EB28FDF85F51E69822AFC8EEF818F1030ABC99C3E3A0CB575377B454C91CB03428CF57D4B5EA2E3D7F5058DEDCF20A2D3D1D9174A5231ACE12B39AC39F0830B8
	3892689A8102G17CE51B38A4A670AA6475E4FC599DF46A99AFBAF01670AB83E8F9828DBC81CD2C2932181659D59546DA6329F2E835A8DDC378F1DC19F9C51E53F2C6B32C428199F39196C15650330778EB2BF1BA131B557B3E83F662CFE76AC50C486E3FEF67EE0221FDD94CE76233707197FCA9FE4FA7EBCCCECD24289B6BDCCE83300EE8927AD5C013109F90A59AF07340BB2A483272232CE6B9797E33C536785E7F8C411D5492C64BA3D99FAA2465FA6A555EF6B477554B7C0869E5307FAB357EC403EC4D24C
	2F5D62A849ABE85DD6874A6C1850715082C9222C1E5EEB1C142D71543AC1BA1E42D0DEA9BD03DDA5DB4FE0AB06E8FDABBC3DC36CEE646138FBAE5524C967750D813B7A45C3AC9E3D670C75DB49721BD4A79B2B44F1DEB6E45EB98ED93E9BA37EF997F693BBA7E9406FCA4272E4953FA7BA821BF8751F5BF4C5D47FDCD3D58F7FB56C666BBEEB525AFE53239F307DBE812150ECF76BB0FDEE5EF11363855226C0E2E1760F6A34900FB34CB6FE3A1579164A4FFDCA687CC127734594EDF41A32B306E3B62E562BDC54
	3625B259CF75891936D1E8446CBE486BE8FC7EDB97BB5FDC8D32D26BF966A23ED31E3DD146D3F41A4592D7A6E3759D97D8FD8B2F7FA4DEB51BFB4BD3C26C7D854BFB5F5539FEA7F5ADC05B703D2FEDD03FDDB5167A2C6D15B17B33363DE37667097B47E6BE57BCB8167ADC33FD4C7A7D8F5DF7AECC63BAB3E0DC528116822C8448BF4D761CB5E3FF96481EE3335FF4C981BC6B432F988D567E6D6329ED7D6A786C5FBEFEEB9C3FD39855401D05D0BE435FB90E63E5E8910224383B49F91F2E840286A5E5E74964BE
	DE157D84CD6739B302367BC5B50AF4B4567856052D66BAA884FB6B5EE279F3F19546310977A235F4D3ECEE6EB2FCC1D9EAB737C8F30EBF26333DB478964B255F50D1FE961417812CF90B656D6DD19663BCG382AB36CDE6A43D1A6E3788C13310C5026C3B99BE0459936FE0A5E9584360683E72A04845F552940C861C71C55876A6AEA2A604FEE4A774A25981FAE4D4057F0G786A8F546D263AFDDBE2EBF85D99BB3F3071309D0E3659F7B956662AF03CAF8ADD0955E57B66228F9D23172A634EB8E2F7D7FC2724
	FAAF6079C579D9B63E01AE7BEFDBAC3BA49FD7A7B99742F956F3160D499D81F2C68265CC009CAE77377E945F90116D134AEB9CE37D3872AC9B7771G4389EF33F877392B821F4F120A73F1D09CB3A3DA7A398BDB05FD50540951B159E7F845GDF57C5E49C1AC01E77ED361695FDCFAAB9007468DAC4560DE90AA5DF131932DA9D98C799A0A79DE0A940EA0075G05GDB4F419C86E884F083C8GD3GE9GD9GB9G39G6B4EB1BD3E975DBD039E66C195F1AC3A2820EBB1FDF5214B9A239700BC617C4CF7DC14
	2FA1EA652BG1E6AB97175DC307235G0FE796BE73FB056954BEF05E746CBE70C353647BAD70DD5E6908F97731F09A63EB4D6F0F4A5C57DEBFDD4EF13DD10F96D570F4026AFE6705597299693C5DF94B8FE665CB3CA725FC6536F7GB3814D83EFA3BEF25E79447C70AB841FF6BEB1BFBC2CB35F1989F51B4F33B3291E683565350357487711774CC76D2AB1D07F1111C4FB8F903CBA12E86F5779DEB185F579A34C5ED5F1F7C22DA22CF3F1AEF37A6F6419748E5A4820FCDB7C48F7896A538196A750AD336123F6
	3DC6FE7731DA24F639B83E156073ADF873CCBB4076648228730CB2FB5FC87A9687E16928F9A7B41ACA6FC8400EAF5F245FE843C757D6A9F967F196F507E6DDB9B90F665FA312CFAF8700B09C2075DEC76A75FF46B17B7A179C76B15018A3B986529C76B15004439A83D15CBB6407F081702F431C6EAA471D663879C4A66206944988F27C0709DE086F1131C106CFA7FACD925C30093B4102BBED62EEF2208F831303780135F9276CA067250A2A527227880D928617467BA31B6828C4C40B5E37F19C6ACD4F3C3795
	1173F79F1DD394D7C1F015F1385EE8B25D71E8B25D798B49F45285AB9D3D07DF313D0C682CAAE139037EC2074F3F5BC90EC95F17DB5E3B7477BF37B9777F50C6039133692065BDB77A7E37FD5F7B5A869D6F1FCA49F360FEA9F92EAC0E5DA97A589D4CCEBF3B8FDA40F1238437884AABB86EEB8437964A1BB9EEC70049AB17188FEC8962DA996165A324EC70725D013FC4FF207C54FD7E0B3FC89B293E9B2236FB46587DE706447A630BE32CBF32B86EEF820CEEA347BD407BED334462F271E54B77634425D1FC65
	D4D98C4FDC1AF9DE7F35996FBF924F8E5DF2DE6908CC63DBAC9BF2EC9F27E7FF5DFE16074C1359595F349F3FDF3F88C1376293D51AFC340B3AC7D3955F344917C178768224F33EBCBF466560C415F73430114C495A7CB561ED83D8A333B3511AG332B5CBF7B9D47AE20E98258416C74A71E09FE36835B68F3ABD1232D43CD4F6EA60356333BE1BF3DC33C64E739EBA5321A7D3CF20C69131EC21F196EC5967B936FDC8E9745E47F015B1A13C276CCF7C5399659F34BF7198E23A415237DF34E4F72DDDA7E1C9F3F
	41329EABD8324E5A412EFEEFEFC71B7B7CC0C77F89373B3D2D7FFC1BFB286D74G17D79C58BF83FF4F097E5E37CE9C775AC92039665BBC57DC63FF2FF97CF538B51B63F157EE1C3BF639AE4C84332D4F87591CAF8930DC77FFF87E5B9AE073A19B171249D397E475D33C7EAE228A75BF23F171DC8E5064866692775E9E1CA96EDDE7A303727DEFDC5CD9GF4C2F0AE6D9D08EB2F82F82AE761237EEBBA103C5FDC89A46FB72B027C7B52A05FB34613F91F9BCF66F5871369CE862DF4747B271F8659B97E8834E1G
	E4GAC85D89BE46339C5E1E7D61B026CFB0E83CA62779C7FA5D83F4788A8095FF3B8626AEFD4923F67E80B7B5E63AEA571FB8E46CF7BFE2B72DEF3B37A7F0DB82A3C8842167CCAA8B60412B6C92A423B8FC3F8B4F63B925505ED7CEC2A1784296BAF6EEFF67D6CD3AE27D054ABBCC20376DE2188A36FDE211884663D028B0276A97C4114538A0670983D71A18DC5AF88D7626BA94D76784BA172A118A1BCC2BFB9B304AD6637E5063013FCA9D6C5A92A3CDA955EGAAFE45C745036EE5F1F79B5F7E64017BD77FE4
	B06A929E69951E45CF9686E41D7EFECA422FCC4D1F019F91CAA31A82150087432264D19CB526D559ADD4787B71A708D1C3165AD82E845A6DA03F3659B7E1A86A3E9EB19AB4B03FB968960A427803F561C9DA2D0A6140BE1AB89DA43D63E8EBA58E07243C206FF622AFCA2965D236FFED5BCBFF6A5B304CA5B80F880F2A1AA89D91FD0626778AB0C3F5B99A54D4E9AAF2188AAA19E4DF59970138BFF2735158DABCA964CA0FCFFDBC2D6C0132A35069D33A300FD0629788351CC26A880ABAB4A56B3D867EGDF53F7
	713A3F5AE93C7C1D6E274FBA8507E9A48E665629888C14DF8CA96AB43CF4C25FEA81B24C65D02439911FD1589935C7409E63C73F3A73669F37ECACGE1C704E272D399D029BCC6F5FCA2B4AA6B9FDCF243730FE52C7FDB54B80266B2492510DB06237813B090DE15E0B32B622A57DEFFE75D8D377C7EAD22FA0C5590DE2003842C7B8232965A576677C3FE4385400801786739F82429CD20F9F26A23BB7A6E7D43A84C30CE41952A2BA3FF6B495F867CDBCFB07584D35FG4336D11871DFE27D943319CCCBD2F89A
	6750943ED5CC71A5D5EC05B8AC7B5AA606AD3242FB426A4213498757ED8BFF7803A567BBAB1C02937CC3D0268E21F89243D01C740ECD88C4A21E0AFE8C6077F19ABEEAC2F1BC8D7825AE7C61156924AA489A2FA2AEC016CCA710E43F701731543C1F5CFD55FA87DAF81DAF659DE809C6773ECAADF6BFC94F16D72B38C20781EFE86CFC38D245F8B755F977C0C8D4D508FA475B42921EFD37CF9BF26C7E6B3BAA4A0FB29A33527C7FEA106F4047ACBC7F87D0CB87887719CBA40799GG0CC8GGD0CB818294G94
	G88G88GB8FBB0B67719CBA40799GG0CC8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4199GGGG
**end of data**/
}
}
