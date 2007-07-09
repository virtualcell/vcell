package cbit.vcell.client.desktop;

import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.GroupAccessSome;
import org.vcell.util.document.User;

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
	D0CB838494G88G88G460171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DF4945539D1224602089AB5A8A0C20AE0830DFD68CBAB2F259E6D4112D6FA9ED57A0ABD5426B5FD26BD3145539CCB1F13CD82018493A1DA5488DBC59FE09A82C2419815A41B1040B604A446E0D3E1F6E7129DB27B436CA459043F77FD774E3DBB3BBB1BC4CEFD9C3F4C5C6F6FFE5F1DFB3F7B5D1FD52858B13BA85FD6A18879C502716FD03EA0C8AD02507E43B5A1061B666BAE91CC7F7E82701230
	CFAE0467EC20D5BE50DDD2AE9C9FAD0672E2A86724F7177C995E378A356DD58D7092271EC2205D15D91CBAF6BDCFB530FAB610727039D2F83E881081B8FC967A317E9B4FEDB3702B8DBC03FC9B60884F3D67AA8DDC8B144FG24G2479A97F43705CC8EA3E2676304157FB53FCE16D77B55BE654E35249E0E7004A3698CF3B70FCFEEEG6B7ACFE2A74AA4C0B983G1DAF903EF1F8B7BC6B5A877ABFF23B943DFA48AD2BEA35DBD5E41FDEAD4941815D9F282D2DEFF4B6C90A6E5700D7BD077ADE570A093CA09C00
	F29E4139DDD8C7BA3CAF83D84371BDEE4457E95DA5DDG2A467C7A70FF6B8C3BFB4E7F3B10557F484632B8FEA5EA541FB4AD6CD7629ED7412B649B3C6940364E86DAE9303B448E60G10G9281DEA75FFEF7639EF8563B74C63F576B775587828D2A247D5DAF49AA3C0307AE5D40F548EE3FA689827D3EBFB9D7E5E14FD8C0651E7E42F234CD663F473EF5DA47A2E1753F5DE65BE051A62B671F374D885236D89C8C37892D5B2E452BFBE5B032AFC6567D29E02EDB78757E8DC596F5D7BE3EFFC33621E71D296E9A
	9D3F7593248FD442BB4A21BC45BFC5703A89CF7B25C270D8CE82DA0E4E7AC64F0B4C96E7619C21FC5F6D166D10725094DB7BA03541B398ED4BEA526F1287E91DE55C169C02DFE242D3DBBEA7F8ACE7816D4CD0F7896DDF6FB663B8DD8D65BC0072616E12DDGADG92C0E81875B12DF16F847BD83B28698D030AAA491AC0E55FF53C82CFB924EBE22BAF280BBE375CADEA0A68D26520E0709CF0E09B38A2645EA7FD5F8DF59C15FD32A66A0A5F875AC52F2C4BDA1072B4B8DEC29ECDA654AE45ABC3855E0040749E
	F3EC45F1AD8675CF8212284BE18ECA6FF060B799923BC1A3A800F75AAEBDC7313DD2407FF400253C9DFEC0749D17B51F2C5656BA7D7E0141C097FAA21407301D8F1158319278F306D99BF735A02E8B68EA0869A9EC5AE25263D05CE83E280D30B176248BE59200BFA5447A4DEFFBD83FF1ECDEA8D47D78D64BFEB38D7A7062906DAF1921687113CD7422EDEBC3AC4E7BDDBD939C47892148B11692329EF331B1A3E942F594C4150BA6A0C35B7921D67CE695A35DA5B5G6DA32C2DBFEE32533E5BA086E556DBCFE8
	5051E4A95C72544E275C15C61B229DB8C962FC0473638E00035C1F5F91BE8F54B18DA083E08DC071A8548D50B34A5AD6F287CC7A4CB6B35D6379364328FFA2EDF7B0224DA7113A8BAC5A90699BA720EF33E167C46AAEB31E344D8FB6E3D99E022956A187E41F84AFC103D6570CF59B71204F4FE59A88BEG71239186EC1063DB1AF10C0B3A2EA92EC1DD6E14F5DD717507696D242E6020AB908EBD2C2ECF1B51F77FB0448F4E5F5B0CE34F6D7779E472713908C17F1C0CFDD9153DE0F423FF5027D33CF494732381
	F94408BD1E236B850C9D6AA046B11AF3240B58DE81CD71C21742D0A82C9845385026EDB335D58260B0F7CA05E746A89B5F43EF31713DF3F4013074F440E6B5C725FC75C65B3673F45CD51D8F0FEFEA532996EB5BCF1398E4600354565D2FE13D667C2700709EBD7BA245B78C6A3A5FF7D4738F8658987B63B1546F04FA4FG044E33B17653962433F8E68C2EC63F2A52A6865A1273588ED56E8A13AF534E53F688771BE52D6578AD65F303B2CC9398D80F7376FE2CF5CB942D5BE8FBCAFF22955BDEB545641E8BD8
	67EE0965220597E8FD1546734085DAB7657B8567CBB568CB0C670A883E99B2634B05F271C518B3AFD2BE4FC5B35F19BE46B79B7099F179AA7A995FAA40678214DF22FC4ECBE63EA7B8DFA260E7DC0A47B759437816GFE25859F7D7E3F5F0DFD126447E745A1B1D49D9C06F1D5DBFB2CDD042FC8F3618CB21FA17F5ACBAC674D9A44BED0F599F2D1005E4B2C7F7EE8B34BF91387668B2BFF3DEFC33CDCDE3FCC6DC8399C6E3F53F26B5753BE566444BAD781AD7BB26BDF9D474C73EEFDB068F7ABE44AB73EFD7EF1
	F37F26387547B74133CF5102FAAB8420906BA7254751DE4C898C34519EFF2AFE4332BDA2060111CFDEECB67B3A246C222DA40E2F6B8C9FAB93423E9E71F7ED20F9D90744F244DE20A78350B178CD02D7FC0A2E082AB2AA6F5ACDEDF4FE0C6BBD110D59532DA60EEE4336B9DB0F98F5E7AE1AA354DD7E5CE63576C8AA7358B28D1B561AECCB3B59D53094005AD7AAB37B0EDC85637DAAE6DFD0AE26B122D3072C0A5AF6EAB30E4FC112E7357AA0C76B935D44ACA387FD046BCA87BD59GB4A65C6DB247847B5510FF
	8228D7333A2A7A76234FA410D453C7BBA877EBDED95A05DF2ADA9C562B49DCD3DDCF48CD783E1BFD57275EF817483B65E01052F7B9630BFDG86D3FB2F6FE376A600AD99D7B3FB976F3032B7970148FD584F648AA7F51754CDE2F2F78D196574C9B4C71CBD0979195A0FB936224B5EEA7CC3DA3651A37A7AE35DEC853A61A475517D493B3C0D58D7097112CB05FD94BC4C5645E88F377538474A561D094C4745DC6EB0608209CC6EC3CB397909AC9F7A0F18755B1244F08E39A2B1B61F246B1A1EA12CAFAB112EEB
	B6F23CCC7042B5E1BC356F3FC90C42F2A550922EE139383D8DE3DE961405EB3BCB368154G3483842EE531A67978FB42B8EB9D523DFD225A9911F430B507933FA76489C6394D096DC47284014D23D3CFE0BBCD01BA6783AC85D8851047ED7831F3DC9B1AC216B650F66E11225BF95BF511EDFBA02ACC657AE2642A234ABDD1F2541F2CF61CF382C01B82108A3094A08BC0C802FE13447C5A79778F47758BFBE8173F91F0ADB017B7C667E95C56AD721579E80F110B765111E4A5377C8A6B7BC90C5C7628F2EFD4FD
	F4CC17F5E25BDA4C4FF4C24271C2F24BA77B23F3CB9CF3BA68CC82207378AA326E4379F8F6929B0BB776DD19BFB3E364227DA93CBED20E5A704C8E348167494A6B198D0D7DD7E643499839E89B3A2E37EA53D79A36DB37E907A8A9FE5630545E4707D86E23F2FB57FB2E4C5E0A9839E8FBD3AD5B6CEA8F33E1893761DBD7E84332F1EDD8EBE9034EEDA87D8A1449BE47CA1297BA205C43F17769083BG65642FB0DBBF9F42FCBE834A2BG72GAAA6B35AG21E13EBB4348D3F19E25345CC173FE814A3E98E5AB82
	53474A99F17822015631F148DCC73CFD066835F274BABE9E98FB33ED715741B5ED566B603F35D92F036B5ADE22F850E15EE5E3F4C75B586BE4E7DB7CF572EDA7625BB97304351DF3CFD85B39604458F65EF7A21E1DEF1076FD7415B2A29F8C4AC1B299B8C4DDB4E8CF6BAF8B96EB77B651AB8BEC7DFA23345160E9527D0D7BF83D3FED0F6F63F36D56BE7E319DE3E650BF283965E8BFECA737F1DAF81642A48B4D21BCA5A7D1DE9735FEB9269D3690F9CA0BA77F7A49F8EDC5FDED154D3E6E67F8DB1F997FB7BE16
	DFA6E399F59E184C56F34FEEA7FB1DD0CE87D84647671316FBF6EBA6C70E313513E347DC7114C8DCA514EBG9CA6BC3573FB91F69E62E3B3F70755BEFF0FA12F42B381A099E03E01237AC67B4D7A5E637AFEBAE425EF09A13BE2CA2C8FD45F079E333E83DC5FF5B13194A14B501397D75F33917A427B09A3167ACAEFB056E5B74453770C06FBD02AB8629F54E1EE3F813F27AD48720183D0F68268GC9B7503C004E03778D63BC5C2709DE68CE1D8159ADDC180A324BF4A61B8D7C6BGAA81DF8750BB152DD3DEBD
	027D27FD5025AA6E08E953302973A22E835BB5E58896A1918C147EEBAF6A07D50A856D5D1D287B28A67A74FA32E0231B913C1DDE73E01FFCF8AAED0FD5704C199AEE9B61C66B78DFE1015F8538BA00F6930D2EA9149DB89E249B691AE2B3470788BE490427EB0DBF10710365B420ED1B46664397C84E158865E40EBB63C65CECA8AF61386F925CB2A82F62386FD73139B4A7ACDB0A383A1B40661B982E0E4876C2B940F19B6C2C5EC40ED36DCC5FB40EBBB54C742D61389F1DE17665F05C7BA22BA30F63FEC7F223
	521B21FF5C4CF01BC32C0EDD9CB74B4378C29C77020771A5F25CB44E3712631E65FCEBEEE66B4EBDC1F656DBF3CBF7096796561F771D4175874F6BF051DBDBFBD4D32486315FC9C6870DFFDA3058101FFD8B0BFF33094ED5D04E85A8CEE67BD921F7589ECF5A3CB9420ADFDA6FE72D693AE833A753BE64C88E6F23246CF19550BAFF2B33BA93001E110C384AC173BA997B7E9FECF49DC269ED91F49C93CF661B698991F434774E8879EFC450310DDEB26429EF4B5FE63E2DDB1EAE2877A746BDF797EE25BE6DB41E
	769FCE4F4FC9E9DFEF74BF720DFA01265F4A3E5BF584178465999C278EA0EEA914D7F25CB3AA627230FC9B439DBA09381DD0BE42F15FBB447AC19747D5D7A3AE954A999CB7CCF019D04E3A0D1FED6D40F8168765ED37F7179C86688188GCC391D71DCA2BC38BF1BF6BB5D4F4D04E7AEC0F98A63795376B3C6FB209EBE9F5DEE9DEF8E9B7800859DEDE16FD3EE374A53376DE0F519F352F867B59B0C2755B90DB9D70DBEEB2271F63B9D4FB4F4C8FCEAD270CE483598DE8DDA76991421FBCA38DC97FD920EBB4237
	60D418EF159BF57479FDFAFA8A696FA4A669701EGD0B19DF1BF2FF244311F593241F8A22C85B904370CF271F49AF373F91D01E11C773C10BBA9BEE65336FF04EDEA97D11F6C43E3GC920F649EEF62F239774D11916DE1421B6CE320FE7E301050DEF9B5FC9B559C8F58F7CD3606F77FF0E6F540EFE1159911A4E66540D43115F61972743F66D8CB13B12264776A7F33BA04CG9E04378CFCFA0CDDA1B12C7B950569DE92C7F741983AD744683E539556DD4CF52F0E233BE88C5D39A65DF4DC54842F6C3E4AC7D7
	A8B77C0565E81E2069B81F2CB3378D411FA77862BB42F8BA274D63F3DA8550C2F7303D4ACBEE0CCB2B211C87D0FEA74C2BGADG521DECBF6D5BA7EB04F176533A7C81A7ACFAD453D20C7A77FB7D4A6E71FCF7831BBB96FF6BDEA163DAC75C330F501D46B90F714C481C1E0FCF6A5FCBB27A378CE8D9G45B33ACB6CG87G1CB318FF6BBB2A28FFF85ED12F6B225BC3CEF818F35D0ABC9CC6E3A2EB64209E34C99C547DC13766D75520B17686BCECAEBEB3030D3DD99E0CA5AE3FC696E7B4073E5F4372E09D7826
	GAC85D8B9036ED1D039C9833139779D83CCAEE7864D3D8BE7425CB353ACE77322EDDEB124F8873D1D4A28EC58761A97ED2B815E3A190C370D2C9F831A5CA7EB1AAC91EE9EC748DC6981DEFDA60B33CF9EE2E3EE76CC6BB1E70E330B01E7594CF01CDDBDB3BA4EB6D54746515EEA963F32391DD32B09CF0AAF4A27F904B78F788AE7B15E3C17B1B7B1F6B1BBE42FFFC8064540A7C1D9B34EC10AB05FB956854130D911D549A0F21BBE723E94162F17E268E59DB81E0EF5128F0F4B07FD943FFFBF36A532627A5AA9
	3A483A0252CA73D1E76320061B964892E4F4FA2E11AC1A73291AD974FB7640136E415E6CB66F412A86BD851EE933585948A791E75F8F9BBC591CDEEFC9DFB70B65230F7D43FCD7327C8E55463ED574F7AE3F4BB8F33C4BF4EF0C446749838277795BD5F82F44A78F5B79B9D1B3CC623B5E2C5794D1FD334A3E8F7E5B3F1B0F7B5943667A8FEF3A5A72FE82C23BD12FE4BC273CB0BDFF872993621F907EC7EDFA8E1B5928636725AC36D4FEED2E60F842E1FBABCE9DB38C5DB9A69FFB7A8EEC243EDDA0B9092E8D4A
	340E533E305F17C4C6237979A7A1363F39F3F6F7C9FCBB9F42EDA1E39F652842E4FAE733DC621701B07DFDCECF6674E26C22465C9252436FD7195E5302933DA7F5A5C06BF864CB2D03466D6B5C71775A263A2D775A125D567B09F738475E571C630E3F2F3940ED3E7FC3671DD59A0E33CC78AE59G25F7433C83F060EEB6679C125E94481CE3B15FB4C97D3857073798F5D67FEBF2FCDF7FA20FFF77F10F0C7794DCFE88E7DE544F70B5B2FEAF5D9FA0C863DE95596F5314FE0FCED98D7C8764BCDE157B881A7673
	2BBD68FB1F2886110F669A0DE7CB0DF1508E7EA65D4D564FC5F64CE3234FC52A6924D8DB5B243BBD3254E0CC11C69F1FFC164D25CB6FE6EB69EF927DF9D0AE1F83794A9C36EEFFE80065B8C7G971A434E25B629CCC77A9C2663300A3C59D09666425CB9170D1F81B2CE0CBB81A76DC2D46CF2A870257092672E03B5B5D5F678331B4ACD9D8C4BBDF8E88C397D87C1EE5F411A5D5436978F33B1DCB357AAAE64FEEC0523F55EB7942EB3D89D990BD68E07E9AB37CF449E7A0D5E5E95314791BE3B62B3A535FBB9DF
	DFF866326F5B5FE4FD3745B4CB726F7AFD32CE42FEB6FBAE7BA62F09E40F844A39G4529CC6F6F7B625CA1A253A755B7454D5A511E4A3EFB0863FAD2D93EFB95799D846D4F4BE56C0F5D62D9A3232557DD58A8BC86D51DF01D155DFA17584F477540886A1C867A52D259D8DEB170EEBCBD00EC577C81D953C7A8165CA6B3F45DB30AF9D4A6684986A87DEAF749CE0043G6DG92C088E08AC0AA40920095GD9GF9G45733ACBAA81EA66B1BBEA3D3B4730435828A201C593958CADC62C7EACE44E519D202FE75E
	58E7DCD44E291865D410C918105CC791F249A0B3FF9CB96A5F9AADFE8CFCDA9BBF86661273C499E2D757C840B87BF80E6CED7B7B7A02B20F354FEB659C579AEC7787955C1D2036371E9DEFBD33E41E55FE4B27634AC51F13D239E9634AC583DD877E154442D573225707FFA3782CF9516B436F1CE531B397E8DD7730BD29E583D7362EFD7C8A654A54AB73B70D44730CFB227D3D1F609F3EA75A5F0FD4666F8A209D184F7C5DFA58DCFF8A605366CF24CF2F904732FB29058EAA77189B65D682BD9BE0DD94DF7902
	F8F7152612D839F381752B0063D3887E0089CF7D350BECCEF6GED7E82666FCDEE4BB321FB0DFC775EF8F6DF3414BBE07037D8485131959841F71EE751F0E85006C7B0E762779149554BCEE07475D37A032371477FC323630F7F652356B9500FC6E3F32047C72DF3205523669C0862FEBE0AF3CCBF44579E4E77AB0EBB45F14F901D087BD411F48F47BFCB6CC2FC0B0C95B27C9F08DD438457E3605619F027583D5473984340E58FDE30B676B44E137DD2C5D5E9F99361D1FC9052F8FB141DC79B1500E8323B0263
	50EE3A67FD0E2CDDFA6F25FD0A620688CE0F403915D8BE0F924B7709855FE791FC749CFE69564D44E6D571490D7497BA3C7F3DC75618743D4E747E4859AFFFED73337F07BA3E2106FDFA50746ED03F7C3A7F792556C13F771E4331FDE06F61583E7234484F946FE5E7B03FF2337320B59C37C7C25CDAA8EF7B9A43CDA238AAA8F7F15C13925367799A0B0149E42D1E414B33C8B913175F917F956D017A633779477F12BA625D9B223E4FF733734F9C5E9E9BF9FB64F1DCB267EBC9E338675C7CFCAC0D656562BB26
	7B63A424D1FCE5E8F3984FC21A31DF7FBF62D8679F51FB0729D0D7C69A4DEF311C19961E47695EDF194456A12B52585EDF3D04775707A069D65C22CA979F8D22566ED79577885BDFA4F2FF854866F2F7C81817C390D5C67DBEBDD6125579G112D875835106D092E1518DF1E05631FF1DCG1EE9GAFB3BF97C66F095E5308F59CF3AAC13D3E51C9776E9E0B589FCCDFC84F90D7AEE4EB57C997845E4EF6EECFF69CFB46BA97D937B07A4CE55E45306ED22E3BF85197BFAB2ADC9456BD3175AE336151CB82EF1FF7
	A4365EB5563F925E41B2EFAB18D61D555DCD9DDD2D0D754E531D0D9DA71C4E067A0E53754ECF6BCFF5F2FDBF105E9BC33E65C4C76BE7A75A3A2CB45035665BB2DBEB6EDA74C55771B917857EBD166F463EEB3556051EE054355E457A7C19C5EC2DFBD2E27527ACE27D218C1B5810791053D7F07AFE9B52E97C5993F1FF38786B30367C7AC4725E9257D8F9EF0D058EAA579DF1875C81FCBD932A2FA1C2CE851904F164E87C2A17E3671BE6B9F63EF953456F17FE1D45BE2F94AB2BC9313227AC78BE0F6023771F5E
	F731FD7C8C28A393A08720B43D3B24AA1DFD4F53AE36E765CCE777B9EEF0C55F67F8D6B05F47780EAB7ABEC7FE847DD9D774FD0E7A087B9EEF39236FF3B0F963F726E3FEEBAB207F5F08FBDCEF0132345F88C53AD0DCAFC9F6BC7B5005CD61531520A614713D29D612242E99FA2F366097F38BECC2E1AB74F81234378A0518F9378AC5A4B1EF958AA0E98F610E384D8C3AD0C2CFFCC8C541F34225C8BA65599AF9B8C4AE026942FAFA65CC97B698F74BF4E1BB39A9E6279C76AE3F9DCFG15BE45CD55036D1BA34E
	B69E397B602FB2FF569DAC9056378A2F621505CED9233F1F1270162971B370E6E1E3402FG917060D810BC0A027A08AABB050A3E8E7C89E2D01725FA36D6826BDEA63F3659BB28AB6A5E96B16851F1FD7301D3A874618F5605AD142C0A3E7E3DF4617481E91D7C7AA3A46010A5AF583B15580BDA2A39162D8F163D7D17E3D99985022DD958247AC529D9F46BFE2DD55828FBB4B968712BD2A870B1D5D4BC4CEE5997023A57397B68ECB56E94F22387C2B327EC4E5E5C8C0D9E52043D0493EFA0D4F38E2951A3EAD0
	152C356A7883FC3F3613532E5A2E3F730323FF6E358979061378B1CBC3811DB6986FB768F2F5B3D82D7F57EF9E1D73533A5C9C90E9960A488FE2206272B0D75B2057A5EB57265D7AC6C9721A49E8D7G1B07F413F268GFD22D7D1C76045815F4C5F4F71F44DC50A95588A0C3091161C3EA07ED28C2C31C7B585A3F10F2AEFF9346656854FFFC6BC8A0B6A428E7A6DC0F4EF3F6C776E2D6F6B03E58FD7G9F92543F4155A3CFF5944F1650CDAF9F7B718BAE68F88E2140DBD3C37E6EA3FF77635FFD84330FE0766D
	072F19AB0C79AF1A9E8A214F2F067810AAB2C384165D3689E38B4D7005301A30A5F6633AFE6ACF1EC8BB6D283089B6088F9E19860422E1CCC33153672FC5A09AF1D774B60067A3AC5CE4C0D1A48F4425A67C61159124AA4898371390A0CBC6900871DF7857F8EA1CCF6E3EECBE834D3B469D778C744C83F46E539F881FCF523D65DDFDB8C253811F79G5B9F8E49186F465B6F6E740A2A8AD96FC03DCF423D6F0691DD8E1FFF7D358F7587990FC1B47E3F9A64FE75D9134C7F81D0CB8788DE8320C11099GG0CC8
	GGD0CB818294G94G88G88G460171B4DE8320C11099GG0CC8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4A99GGGG
**end of data**/
}
}
