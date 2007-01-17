package cbit.vcell.messaging.admin;

import java.util.*;
import javax.swing.*;

/**
 * Insert the type's description here.
 * Creation date: (8/26/2003 1:09:29 PM)
 * @author: Fei Gao
 */
public class BrowseChangeDialog extends JDialog {
	private JPanel ivjJDialogContentPane = null;
	private JScrollPane ivjJScrollPane1 = null;
	private JTable ivjChangeTable = null;
	private JPanel ivjJPanel1 = null;
	private JList ivjItemList = null;
	private JPanel ivjListPanel = null;
	private java.util.List changes = new LinkedList();
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JButton ivjUndoButton = null;
	private JButton ivjApplyButton = null;
	private JButton ivjCancelButton = null;
	private boolean undo = false;
	public boolean action = false;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == BrowseChangeDialog.this.getCancelButton()) 
				connEtoC6(e);
			if (e.getSource() == BrowseChangeDialog.this.getApplyButton()) 
				connEtoC1(e);
			if (e.getSource() == BrowseChangeDialog.this.getUndoButton()) 
				connEtoC7(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == BrowseChangeDialog.this.getItemList()) 
				connEtoC2(e);
			if (e.getSource() == BrowseChangeDialog.this.getChangeTable()) 
				connEtoC3(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == BrowseChangeDialog.this.getItemList()) 
				connEtoC5(e);
		};
	};
/**
 * BrowseChangeDialog constructor comment.
 */
public BrowseChangeDialog() {
	super();
	initialize();
}
/**
 * BrowseChangeDialog constructor comment.
 * @param owner java.awt.Dialog
 */
public BrowseChangeDialog(java.awt.Dialog owner) {
	super(owner);
	initialize();
}
/**
 * BrowseChangeDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 */
public BrowseChangeDialog(java.awt.Dialog owner, String title) {
	super(owner, title);
}
/**
 * BrowseChangeDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 * @param modal boolean
 */
public BrowseChangeDialog(java.awt.Dialog owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * BrowseChangeDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param modal boolean
 */
public BrowseChangeDialog(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
}
/**
 * BrowseChangeDialog constructor comment.
 * @param owner java.awt.Frame
 */
public BrowseChangeDialog(java.awt.Frame owner) {
	super(owner);
}
/**
 * BrowseChangeDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public BrowseChangeDialog(java.awt.Frame owner, String title) {
	super(owner, title);
}
/**
 * BrowseChangeDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public BrowseChangeDialog(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * BrowseChangeDialog constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public BrowseChangeDialog(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
}
/**
 * Comment
 */
public void applyButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	action = true;
	dispose();
	return;
}
/**
 * Comment
 */
public void browseChangeDialog_Initialize() {
	((javax.swing.DefaultListModel)getItemList().getModel()).addElement(ManageConstants.NEW_SERVICE);
	((javax.swing.DefaultListModel)getItemList().getModel()).addElement(ManageConstants.MODIFY_SERVICE);
	((javax.swing.DefaultListModel)getItemList().getModel()).addElement(ManageConstants.DELETE_SERVICE);
	getChangeTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	return;
}
/**
 * Comment
 */
public void cancelButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if (undo) {
		int n = javax.swing.JOptionPane.showConfirmDialog(this, "The changes you made will be lost forever. Continue?", "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
		if (n == javax.swing.JOptionPane.NO_OPTION) {
			return;
		}
	}
	
	action = false;
	dispose();	
	return;
}
/**
 * Comment
 */
public void changeTable_MouseClicked(java.awt.event.MouseEvent mouseEvent) {
	clickTable();
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/2003 3:35:36 PM)
 */
public void clickTable() {
	if (getChangeTable().getSelectedRow() >= 0) {
		getUndoButton().setEnabled(true);
	} else {
		getUndoButton().setEnabled(false);
	}

	if (undo) {
		getApplyButton().setEnabled(true);
	} else {
		getApplyButton().setEnabled(false);
	}
}
/**
 * connEtoC1:  (ApplyButton.action.actionPerformed(java.awt.event.ActionEvent) --> BrowseChangeDialog.applyButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.applyButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (ItemList.mouse.mouseClicked(java.awt.event.MouseEvent) --> BrowseChangeDialog.itemList_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.itemList_MouseClicked(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ChangeTable.mouse.mouseClicked(java.awt.event.MouseEvent) --> BrowseChangeDialog.changeTable_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.changeTable_MouseClicked(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (BrowseChangeDialog.initialize() --> BrowseChangeDialog.browseChangeDialog_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4() {
	try {
		// user code begin {1}
		// user code end
		this.browseChangeDialog_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (ItemList.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> BrowseChangeDialog.itemList_ValueChanged(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.itemList_ValueChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (CancelButton.action.actionPerformed(java.awt.event.ActionEvent) --> BrowseChangeDialog.cancelButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.cancelButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (UndoButton.action.actionPerformed(java.awt.event.ActionEvent) --> BrowseChangeDialog.undoButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.undoButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the ApplyButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getApplyButton() {
	if (ivjApplyButton == null) {
		try {
			ivjApplyButton = new javax.swing.JButton();
			ivjApplyButton.setName("ApplyButton");
			ivjApplyButton.setText("Apply");
			ivjApplyButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjApplyButton;
}
/**
 * Return the CloseButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCancelButton() {
	if (ivjCancelButton == null) {
		try {
			ivjCancelButton = new javax.swing.JButton();
			ivjCancelButton.setName("CancelButton");
			ivjCancelButton.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCancelButton;
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 8:48:14 AM)
 * @return java.util.List
 */
public java.util.List getChanges() {
	return changes;
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getChangeTable() {
	if (ivjChangeTable == null) {
		try {
			ivjChangeTable = new javax.swing.JTable();
			ivjChangeTable.setName("ChangeTable");
			getJScrollPane1().setColumnHeaderView(ivjChangeTable.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjChangeTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChangeTable;
}
/**
 * Return the ItemList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getItemList() {
	if (ivjItemList == null) {
		try {
			javax.swing.DefaultListModel ivjLocalModel;
			ivjLocalModel = new javax.swing.DefaultListModel();
			ivjLocalModel.setSize(0);
			ivjItemList = new javax.swing.JList();
			ivjItemList.setName("ItemList");
			ivjItemList.setModel(ivjLocalModel);
			ivjItemList.setVisibleRowCount(3);
			ivjItemList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjItemList;
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
			getJDialogContentPane().add(getJScrollPane1(), "Center");
			getJDialogContentPane().add(getJPanel1(), "South");
			getJDialogContentPane().add(getListPanel(), "North");
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
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.FlowLayout());
			getJPanel1().add(getApplyButton(), getApplyButton().getName());
			getJPanel1().add(getUndoButton(), getUndoButton().getName());
			getJPanel1().add(getCancelButton(), getCancelButton().getName());
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
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			getJScrollPane1().setViewportView(getChangeTable());
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getListPanel() {
	if (ivjListPanel == null) {
		try {
			ivjListPanel = new javax.swing.JPanel();
			ivjListPanel.setName("ListPanel");
			ivjListPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Select"));
			ivjListPanel.setLayout(new java.awt.BorderLayout());
			getListPanel().add(getItemList(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjListPanel;
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getUndoButton() {
	if (ivjUndoButton == null) {
		try {
			ivjUndoButton = new javax.swing.JButton();
			ivjUndoButton.setName("UndoButton");
			ivjUndoButton.setText("Undo");
			ivjUndoButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUndoButton;
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
	getItemList().addMouseListener(ivjEventHandler);
	getChangeTable().addMouseListener(ivjEventHandler);
	getItemList().addListSelectionListener(ivjEventHandler);
	getCancelButton().addActionListener(ivjEventHandler);
	getApplyButton().addActionListener(ivjEventHandler);
	getUndoButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("BrowseChangeDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setSize(519, 310);
		setModal(true);
		setTitle("Browse Changes");
		setContentPane(getJDialogContentPane());
		initConnections();
		connEtoC4();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 8:25:30 AM)
 * @return boolean
 */
public boolean isAction() {
	return action;
}
/**
 * Comment
 */
public void itemList_MouseClicked(java.awt.event.MouseEvent mouseEvent) {
	showItem();
	clickTable();
	return;
}
/**
 * Comment
 */
public void itemList_ValueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
	showItem();
	getChangeTable().clearSelection();
	clickTable();
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		BrowseChangeDialog aBrowseChangeDialog;
		aBrowseChangeDialog = new BrowseChangeDialog();
		aBrowseChangeDialog.setModal(true);
		aBrowseChangeDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aBrowseChangeDialog.show();
		java.awt.Insets insets = aBrowseChangeDialog.getInsets();
		aBrowseChangeDialog.setSize(aBrowseChangeDialog.getWidth() + insets.left + insets.right, aBrowseChangeDialog.getHeight() + insets.top + insets.bottom);
		aBrowseChangeDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 8:48:14 AM)
 * @param newChanges java.util.List
 */
void setChanges(java.util.List newChanges) {
	changes.clear();
	changes.addAll(newChanges);
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/2003 1:35:57 PM)
 * @param changes java.util.Map
 */
public void setServerType(boolean isservermanager) {
	getChangeTable().setModel(new ServiceConfigTableModel(isservermanager));
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/2003 1:41:21 PM)
 */
public void showItem() {
	int modifier = getItemList().getSelectedIndex();
	List list = new ArrayList();

	Iterator iter = changes.iterator();
	while (iter.hasNext()) {
		VCServiceInfo serviceInfo = (VCServiceInfo)iter.next();
		if (serviceInfo.getModifier() == modifier) {
			list.add(serviceInfo.getConfiguration());
		}
	}
	
	((ServiceConfigTableModel)getChangeTable().getModel()).setData(list);
}
/**
 * Insert the method's description here.
 * Creation date: (8/28/2003 7:59:35 AM)
 */
public void undo() {
	undo = true;
	getApplyButton().setEnabled(true);
	
	int srow = getChangeTable().getSelectedRow();
	VCServiceInfo serviceInfo = (VCServiceInfo)((ServiceConfigTableModel)getChangeTable().getModel()).getValueAt(srow);
	((ServiceConfigTableModel)getChangeTable().getModel()).remove(srow);	
	changes.remove(serviceInfo);
}
/**
 * Comment
 */
public void undoButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	undo();
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF4FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DF4D455192831325B6C0AB6F5234B312CE5DD2AD8632EED59B66ED1175DC3B5BD46ACD876940FF80C1A1E45963B9CB76B527AB2891054A0C1A2C59BEB34D003449888E00C7C6497929812494C640F7C3EE4DE1289AF7306B7AF7F81767B6EFD5F1BB713B78964C8F34E47FB773B77FE5F3D5F5F7D6E3DEF90F2BFD832A54D16AB88E9DB847D6F73B4C1A82D9604DFAD29788D61CABFCE4A92CCFF9F
	826C9426CEE64033966AE2071232F2048727B2215C81658EA87F8E5EF78989051BAA61A5821F8428FB27FF7DB7E66773AD877159464A713EECF83E855083B8FCAE7F98792F766D5171D1BA1EA04DA68851855866173EBC9D37915AE4822E962023003757604987F0F3D9295E2E6336E54226A7D45BF624E322C930DA6F1B2ABF73055753166CC7DEFB14EC3DCFB1148781F07269C2DAF589BC0FB9AE8C1C686B75EAC523FD12AC978DCB0100B860758D94091EE12F2F2C2CD2D54682D25520689B102A3D222C8C88
	F91FA05D9EA513519104E420390C615EF2A12F9EF89F87D874896157B07C86F84FB8101415F70066F77C63E37A78DB27FEA02CBEB41C31A3427C8E9F60736AB8901CDF42503336F719AE1EEBC41947C2DDBCC0A240A6005C42242C43G3FE7B670126393F8D674EAD54A7030622B707BABE50FFACE71C8B23C1715C10D0EF3CAFD0A6A91842E67BFA8059663198DF83F42EB6E47E5322C04F43E5C79DDE15DBF5DE15BE6A113F54B26EC76C2AE8BCFE1D0A61C379411F7D4E128CD0672EE914C3CD7DDDC133E4502
	F7F2571A74D81D4EF2936FF505286B9B192E9360BDCD378F0E0FE6787CCF03F8EE1F398C0F65E2280B7914EC2375AD9A0BBD639EA167605FD84AA17671E8DB6A27FC8CB9C561E3E9F3A36DE3C51C678EE3ACDE06771870FCACBFE3E341F2B454E5FE1614456DAB45057E9A8565A5GABG9281B682E497A7E56597130DFD567859D55A18C3D4354A912F6C11D40177CDE95C8DCFE9DCD345BADFC093FDFDD23328FA45DED98A88FA1BD79BD1863DA17DDEE3365F87BCCECBBEC995B52F62836A623024C9EA0037F9
	23F1A736D1A5D66B760217EB6230DFA03A5B1C3B60A90B812D496F91B5A95802576FF022CEC645E6208884601D4B651DA614D7B94C3F95E0501043DFB2FAE7A555A749E5E5F6C539B062F763CC049545A867E996BB2661BD2618E43C3399F11B211CFB1068BC66FC5BC4275A5B0743975589723105AE0C49858749E6FE51CAB6D33D7D8121F04DB7ADED2635FBCDFA6DC1EEABBD8743FD67F6C69347454B6F39DA2F510F67020203C19F64BCCA3EF29E6926F7AE5FABAD282B0540FBA940CAC3461B1D795CE6AB45
	00C4D65A200201C91E204479B83B5DF9BAED0C8BA873B781BEG40F569C3E3BE8AEB378EF8E49CCA4AAA84F083CC83AC85C8B8C432FD5E65B70DF70B900CC5EF6AB4671A6787BA5FEB0F4F025B0A77B61D7F557046B16E38C65EDC663FF2E1BFE9D472E95512DF72F960A520577D2F8B796BF120DFB17A3C4170FE089BD5602881833F450D3EADEA1A6A6D9D51A41724E930FC876B373B11D7E024579F8CB944AB570D73D646A0EE986D5FF5234F75A9BE1F4414EFF4516BFF4FFCDE1225E198F415B26253B87E43
	A6741B8B52049EF376B5ED95B0E648A3983FF84E91A7224C7C2AF798CC8AC320D0DB0271609B4A9E130CB601FDE06E1487F545A5645BE3FFA45F5EBB799D6151EE3F4DEAED0A7D7BDBED0BCA38FFADAD897AB69F536EE66B31673158236383FC2C771FC4B9D824BF3ABF2DE8C5DAAB0147DA000DA564CF314DD8CFB1CBF724AAC51639B88DB95F5C1203FA11AE0EC8906AB1B81EB5E4975D72F6D8DD33AEC7DE9F5302F214CDF1B57EB04A71DEB7651519D04E87A8BF4CE5B9FD184B023773B6996D0EC03BA341F6
	55C74C6D5EEF26F68B0FF0FACB0E70F69BC25AA59B6DCA0FA6E5B9G7CC7F93B382366F64BDA28DDAA60F3BE8F36AB7E5C5C2E23155A0D83FE1129DDDCC83B1CB6035E673CBE2714BF8BCAF9BB2ECB67C554894BF50744D1F13CA8B086BED2D6F606D42A67343FE66B924A56DDCA396BC38154F3B4141782441712BDBE3D1DF257188B4B04256A6A74083939BE16545220BD96760C25F3BB321DC71E25DF409E608B32219D2D667533A290D07A3CEC6956757FAF2DE6FB6538FFED7DADBC7B3DEAC02B0300B2CE36
	7263D69CAF2E6DBADA4FF51EE2B9904A27837846G9414A1FE3BA357B4768440EF8448053AF4C66FE85327709CD6C682D2917B37CA7676DD10BC85A859A2F1CCABE2F12D689C5655606B7E209ECE601E432BC943F6EFC06BBEE76EEE0F587F4B7D4DFAFEFA4F2EF29A57F76F910EDD6932D96D97966E9834794B382CE34B02B2FF7469E7ED2B81787CEFEF2279A7C31B3CAFC17EAC96AD1AB447A2B7604781968344FDC9FB27361E2398A3D9D4E07EF7ADD35165FF2A19782F823A42B1126F834DB8C79964631274
	18D14422A88FC2DC4E3A856B746D6646DC64FC1CBB0F1CB04B3B59CC46FEB5F4CC635D5EC263AD05317A0F113D1C35196DE599609381B6997351DA8E41D36493618F072476AB6A70CC1B29B04D00EC66726EAFD156188F4B15A31A26783AAB4228044B3BA2F482FC7CCF9B634FBC8E716EB80D7F05ABD6639FBFCE3A1E1CAE43717B7D72447C586B7CEFEEA57E51C0FB25417FF7E9D67CB79B7C638A3F0067084F234C133D4E7F0BB6621FF382EC7D8469E78943FBFDDE8DE221F7D2AA50ED6715BD79706C1D317E
	F557191AB673DC4A66477D66822099F3E2667EF2190E0B571F0970647B30FD9A72CEBE913ED77C0C61B71D885FAB7EA0C0FB450C13305FB8C9FB075B5A51E793201C82A014A7E56D81A885E8AD27185A580631E7563DD91D8F36CCBED1F605A4CB34C7BA0FFCA13F514BEF1EC7B9B14F94A827DC638E5FF7EB652172D894D6667DDE18516F8FE16507437AF19DBD399F4F37AC56B574E4BD37BA18EE0EE789C0E7A3C0EE85589FGDF772A9D381EE00CAD2F20756D52A9737A963F63126DCD0BD84B56688ABD56D6
	84E3EDCE7DEB7A7A76E5A72E51EB21EE93C0F6A5708530838C82C4D712FE3EF97E78DC7AE1B66DD62AGD78B39CFD5F80EEA48B336E9FEFA38F7C63FF0BDAC2F34524350549EEBBD3045016BE16F18D98FABAB399EB642B3230A7430EAB2D32719D7C579C757B45A3C9B4AB2C0D4956D1D6C4D731B6379997D426738384A2A5F2E1679717BF0C63F9936DDE5A5539D2E88B99B8F03D047653A731215FDA7G4D7506DCEF3411DCD38D797DF41E73F9F14E7994D7077663E3F8CF4854E954D653983E5EBA3FB15CB9
	23DF789896D80E21BE0D4690EB0C61E45B7C46F0EEC63F2230F2E2589878594F3BFE9CC38A54E557A4E5F11C1F617221DCEA605E3D08B8BB14BD862ECCC55CC2A8AFB6F0B7B1DC9C14D756501C8EE83897CDB44AC7D8996D02179F89FC5567AD48ABD4BE4FFC65BC4CE7BABCE65E559E799C616F5A2D4F917E215D7A9C6101761D9C8FB44C2713D47F3D76594F999EE937BAE7F81F0D7B473BF7B05A010094E0C13CDA54C43D6EE56D9D41623C21DE9C1674B96F92321B3274B6B51AD20579A40F917F3DBFC25CF5
	0830F3627121DF8B0F8FF83637917415F10A6203B71D7CA26F94455D0B19AC6EC2D98608BAC536D367C03BDFFCAA54366374724AB07C5AD31423BC5E05F95E86A8E71E063DB4C0A9C06BE9DA7FDC9DF366A7B5633364A7B51D11ED415EE9ED8B4ECE5C3386149135CF8A57F7DB679E23AE38525641DE82D5435B74327E1A288EC8B36C454B7A733AC87D554EC8B645757ED4085ECB8C3D8F543CEF2DF7D71F2A48B2EADF201857B1C63A9DBFCD3A9D99C75D46C1F995407A53245B1FCC22EED3CFDB5F11E4574E44
	73FCB5092D517935BCDFCDB370BFE378C2931E67311EE93A13AB053A4535140B5F54EC1E6F91C38EE7DA4C7823465C6AAE513AA654515CBE390234CB216CG982C23395DE2333A93DAD0476794D3B7F3EE1C77DB2DE65E87850AEF9F88D6741669F462611998C61353FBBC045E6786BDD71A953DE41DC6AABC334FD8519BEDB353BBEC600B7D98D7BCD23FB8A2C328D382D203DF3F9E81FC6B9936F775137C724F108EEE71A3BDD58AF8A759499A357D96EB0BBADBFC0676E68F0DE1CE5C2F0A43E0FEAE3F54A7D4
	1F45364597A96644C15B15G49G99D03777AC71E9D41146306291E5034720CAF72A51C63B3BD8BB4D2B3131ACBE0BF753BF42D7FD1E9F6560D921FE12850E2631780D8E40ED64EE0D66387EAC594805D13411BD76242CE200DABB5908AF5D2C039E40EB76204C975AA359FFC28352DBEC8F5F47BD43704B6D617B3831CC5A4745C3DD7EB9DA4BFB58BEA48A4A31866EE3A76262213C564029CE4A8DB69838FB99EECFFDD25661FA0A29CFF4C50E09CFF7D947441FF6D92F0F4FF64D3EBE3E5895A916F1590AE3A4
	5BDA98DFC7FD28AC7D7511E45BB009320A2A8F17ED9B434754074BF6EA1CE43B946AB29BC8BE49ECEFA1C3F90101BBB0C5F2DC54C0B630F6DAB0467C3C69FDE87A2B4EE74C40F95C74E760D1FFA9BC4EBC5C90147D2A06C8FA381B4555F58DDC5E5B8C7CFD8C3F490467FAA8E6F8ACE7BB12327C8E12795DECAF329A4A49866E8D9B69E12301BB4A768B390D90B39A8977378235ABB6F0BFE2B8B714878D5CD09A7118B6F0CF25D15F05866E178C379C4A0FB6D23C69E5E7B9664341962F4F230CC1E841DBE9ADD0
	D4DD67F2B438EA3A9B6A3B2B6C70424F8B160C193FD5A97D6D0D1677B3882B9B390CD3746766F76ECA633CF3AFE21FFED18E84576F660B94F7731D10AFB99197F551FCE606FD07EDFC7F416B3F9FD20FFE09E750417A1410FA9C7F0E8CF37DFB2A39FE0B29BF1F6B137B68ECE47313F1027C3D28085F65ACF16AF9217EDC14FCE7DA4A031369FC3ED7D45C17E86A084469BE55EE16E14EDD32ADAD825D4DBA3DCCD7787D753735F07BBE668A5A344395493E1FE3EB024795EE5F7F4570532EF07B3EB8CA769D83F5
	29AE32291BD9AE5D61861BF793EE79D644AD00F20C017BD3A659638AB7451E248614C732D1DE474AF86F444B17B22FA6BEE07F794707BB2E0AC77ABC68D33FDD4218C3DF7BA5DC7B65A61447DFBBA8E7C8057167G2E9C405D446F5DF83F93CEDC4FC63D81AFE4A6CE7449911F26678EADCE4AF39F06BEEB994D3FE0F77B11463232094B0953FE44497682F47C0F9B8F017212E71C068FB6137E46D9DE5D81658586AE1161E2209CEF60BE99A7BDA7B6139E1BA6D12F1B0DF2B7AB63BDA4AF5FB0BE5FF5GE9DE1D
	1E6F1AB70F2B51BD17697FCD1A751C5F0272F8FB8A671A8B6FBCD72C1CA23DFBGB78E908B3082E0ED8B49D03AC47E13EA601E3BCCF24DEBA539ADE5EBCEAD14FBG468DFC9C436768F729F8BF4B71C72FC41281760DB4379E0B3ADA8B9C675190114736D964374582E7A5F7AE679B52D89C05E40E67478F61152E3028956534BB647C5B3A7FB9A1347F4F87037D7FD1183BFF921B0B64438D03C777191F8B64B3EB018E271BBD94247BD25A5CF4375AE652FD5FC6F4B734525942BF8FA1CEE883DBE8235CD768BF
	G4FE6F1C8BFC92871F8B5C5E5C78900F4288A6C0634890EE597033A3E5AAF4E6F3B454736511A78708F6F93D6FC2DBA62FD4131B67DDBCE7DD970421DE97864F379230CF209063AE5G89G49G29GB967E9EE277BF10CF8A282432F50B431EF105D0453C1C933D79A8B22F1E346E91F9A4611F29AFF553FAF12FC00429948FF9AFA07A0963AC5B4A66EF3BF54488F771EA7BF7CE2147C3076BC79550BECED14219C8590EB60DFE5F83CCF46BBBE3CD7677831D17291EC9BAE2FA88B1C9548915AF19E0B8C9ED6
	FEE765EF57927B383DAECE47EFFEFA9515ED0CF83C1B580A3E95F71E563027B5ECA30B934A08C6EB5F1A2D648BFB5B13320E81F8G465B390C78DAF6BCB3FF16F1ADEAB72FE55BD9EC2C536F3DF93E72FD0731AF02366B5AE98F7FF569A3416AAC291A1F9CA001F3BC0073FC74B78E5A2367F650FEDAEBC45AF6A8FBG26BB688EED7CCF64934B6F3DC70875D9FFA3325EFD49965341473F22A3184F2D9FFA56466763F3E21F92E3FEDD63A843AA896FAB497F46A46215F77F37053C8F4AEDBBA3785FA662519964
	95145DABCE7C06551C7F07FC9640E4D1642A2BBFED2F69F65558EB2A5CF58D75FA5FDB4770BEF294820B37CF1479E1DC2528BA94595BB721472F7BD80E0031495DC9B67172987A672022FAA7951FB633A765A169AC17B8097A6EA43B28981F4DAEC2F31C348398132A025FED687802A914207ECD3AE44E410B276CCC87D6F2CC5465373EB3380F61B6B2B8C5B1BA270B76ACF7CC056FD9EECBB36FB9D6CD056FD930FF307E752970BDCBE87F426970BD8B755773CB631E2C964654833090E0F1970FC5DC162FDD1E
	CD168FF719E5F9FC88E5D9917CAEC247579463F7C8CD46778A54BE3393FD244A749D8717772ACE73DEAA7FC00365DEAA35F35236CA673F21ABE837AB0673753D947FD1876D9DB96E304D0C533F1D9A423E46B98DDF7A743AFF9BC2999A5FC23043C297B4748D7075E3C8417A7CEE08D1GBDG9A4042EEDA176AAEE0AC412F3CE72EC9B51E81097650442FD00EFCBED5A24F7DBD78B199E55D2B00B40711BE61ABE50C711A62E7C8FD4D14593926F7E0D063CDF57CB9191D8BC97D8C4DEDA54538B3D95A4D63F1BC
	BC933B795DEACAB736F10C4E1D3F1C36234ECD277287B97ECD894747EFE98E6AED2B4E6178B056B034C163562D7DC2E8BC2AE46B4BA9318F566B09A276D53B644673D18F2FA9182BF10D56B7E8B45EDF187F910FEE9F6D273D6D2F379A3F1969C94A9A6C21356225A2141931D660C7B7E5E51C0E1DAFE85C7FEB36117FAF6921B5A2308DE92E0572C600CC11E25F79CC24DD2F289AA61D7AEF0B9CF365E07922551D6C93F3768B3FA7667D529BE76BF7CC0CF4E6104256BD0798FE6F71AA43F70861779E130DA457
	C128CB90A9FFFD440F36D45C1B14E58770GCC83AC86D851EB1C658E978BF35C0F39953F9D1CC9B69D4FF27A6F2A7970CCGDAA9G59FD103F811482B8GBCG53FD44E7272FC0081CB7F3E5B3BFD6C52FCF63BAFCEC2CB84CC6CDB6DF54DC63F457D5D5583BDDD54E863B3D324259DDDF83C85E773D71C333742DEDF0561DEF28F74F6CCD797259G66D73146781BFC9F4DB2FE7CF606C7B4527D705435667BFABEF509F4399CF8A777110F149C8A79EE037B48A9D99953BD444823AE110F64F948C78AAF33BBD7
	A84BGD19E7211DDD710368B3A230F70F98F329835985A2CGD8873089A0D3CA4A4A17C896F74E2ACBBE2020AE75BBC9A5F2CC1ED0660E49B732EF08A508256E893FFE4FF90B02791A525F9F100C589F2D6498383A00C389F8F13561324D99A1599E063948925DD3370E104C16CAA4332F31366B203C89A03B1FF24F218B66FC90574AC8E714FB7B39FF5776076603FAEC58B63F5F3F6D1CE73FB3575C0F471C17D9FCDD589F9E0BB6B2FCCCFFF8AC1A58C6B2DE82F59983C9D97CFB04C7F7211C3CBE596B132A78
	2F2A845A7F3D5708729F0436638334F77BC523E00C7DFFCC6F8B9A2F7FDD461D5715873753FF77C776071F782D6F507E43EFFD07761F7E593FA1F95E9F7937AAA97E193FD5592BC69E5B81F5EEDFBD2CDA67CFE56A4C7C6924EA1DBF5528667C09634E68F4D563C3960EF728E8DBFC6FFBCE7409832639B484B0C684C2E2F37C0C3B2A2B330BA76659AF7D727CFCEF890B5B5183613E779D060F9D88773DC317497716C2DD66A0456B0BDAE4FD0EEBF36B73864DDA1F37E8B375992DD96B73F64D2CCFAE175DA373
	13675EF9766B3C66FEDC5E2F3335C09E8C57C3BABFF79A8C57C334314EC4C1DD4AA065E359AC3737FBA177850886D88A3082A051CB3A5AB70267911156D6272230BC9EBFC80035551CB269F3FC859F7A7BEB2677AB5B2EFF5C0C3D2EBCF84EF23091733200F7D262E797279BE96DCE76525AF16F65603C9FB43D17DE3E7EB2E87CB37090C603F34AB23D5FBEF27DF95FFFDDF9F09FF8E7B4F23C4A9B1DBBDE6D9B350ED787C6E7462B62D16BF8F5F4549C2F747341C636F785607F7FC551897C4D5672D704AD1A70
	967FC9E3BEBF858BE842D6FEA62389DB58E10EA6E460610DC067E17B2F184F43D2EF6E0BF82F3FEE08670F9B0702F9A40FA7EAB90E3170C2D2D66D850A9F0F1F44B1C5BAFFF78D0B322C0972058A1F874F62ABA7B4A918A70C15A37DG3551ABF95D65F29C7710294F7F83D0CB87880E5A7C2F6996GGE0C4GGD0CB818294G94G88G88GF4FBB0B60E5A7C2F6996GGE0C4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA397G
	GGG
**end of data**/
}
}
