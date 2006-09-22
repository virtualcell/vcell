package cbit.vcell.messaging.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import cbit.vcell.messaging.ManageConstants;
import cbit.vcell.messaging.VCServiceInfo;

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
	D0CB838494G88G88G710171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8BF0D4D516200091C10202FC96B0FC0D92905C4C8C6E32E5F68ADDF6E4B53305963B135DC2A72EE845B5D513AD51E146178F92A6A881B24254C4091851B0E2869015099149CF936DFCC8F7BA9F0264A57D12F472525DF43FCEBA7FBD675EF7DE3F6E3CCEA0A5C3557175BD775EF36EBD3FFB6EA78AD947A376A627E489C272DEC17D77F932A01CAF9504F78E7F729461429D568CC1776FBD00C3C25B
	97E9700D003A440D560CCC61565EF4A8C7C3B99A4AC7617791A1BAAF2998FE0460338D6AF6B74B3F9B1F4FEDA662330F15D7DEBBG5F37GB6818E3FE9CE647F0F570E2978B895CF101CA288712C4D535732D5DCB914DBGA2G221D3C7DB6786EE71C27971CD75BB54F0D9412FE66CEF9936968E8921CF2713EB56AB7C7F8ADF9378BF93D4E4609FD42209C830013CF9512CBBF01EFE1CDF7470536D63B125F5BA649F2FE0F6471089DF6C7C73EE86B31BB8A8A0A5D4EBE0FD452A9BABA24D23BA8BBBB84659A52
	7D461D4E68G8DA88BEEB62FFA6435894A31G3BB47C52864417426FE6G45CD737B7243C2F57C8D03BF9162BB9E5DFFB0447CE639793C225D7E79ADBC591A7A87260BFF29C419A7C2DD16471A1187E0821081E6817C1159409326BF433728D5A9F17674B89DC5AED731ECF3D7BAED128C3F8B8A20C64555CAEDCE37CD90381E773ACF990CE7BC607D0E5CF0BFAE1348CFC867D1356B05383F3FA7E51F01CC62A287D3D6F938ACB6FB7CB2613CCDA1F947F982EDB210F73D2067AD3CF0F65FDE835E59CF1F5D1728
	52C9D37C3C4FA9286BEF192EF361F732EA9F9CEFE1F81F8E4F6DB30E598C164320AEC1A15BE8F80B46E2CADBA1E41ED9E0A80705BF1D15D26365E36874860F653DC624BD4F4BF99E5446F20661B76B70FCACAB5998319C8FF5AD3D568CEEDF3155682FF1D05E8D105987B686D08EE0837075110D9D2D7C4BF55AD80D68D60A3DF659A639855EB73672ED78CABE45ADD6B8BC0A68E8132C225BAE364A12C7D05B7CC7A54A20B5205FFFB35BEF839E5FC88E49ADAAF62783280BBD12A239BD3C4D731507300DDBE235
	96BBF839A2763884227BF26591784A22C729F359C4C5723760752FD422CEFAC5ABD08482701B4B65B7B5A82F05B07F8DGB11A9C5CD5C86FBB496D106402821353596DF5D9F0A6C2268F655C46E247F6E81F50C7B2FE759262ACD0AF7B084E7DD507F5F4CA6DEDB8FC515DCFBED65300B1F90A0FEC66FF9B48E6CA5FDCA71CFAEA3E214D04037DC678382DEC72857BCE932309636265E76B9BEE500FA702A9BE3F8FF29E3F7E41F9246AFEF379F618D1D7BB01779E006CFE12F1FCD58E3759E251A31115D63B4140
	A41BDF62FC1C9FB6E62B34B1AE204CF121BC8E006B52FB5AFCBEE76D4E810FE600E9GD1G5B8176G648E10EC6FEFF069463BD77047228C1566C473BC2E723D71783C551077BE157F757046B19E3CC15EDC667FD90D7D24DE492114CAAE49E1039F9E356E19EA642F4601F6275667F906F7C15CA881C775E878172A5137C5C5F15BDB3D0AE416948516EFFF7D7FB1DE9EEF2B4B9FF208571EEA1C37338F620656BE3986FD2E4D69F0C8CC79DA9735BE75925A11A4CBBDB06892275721F07C7ECB68B75DD23F9AF3
	5E3C740600B1C376E27C62B9478691E566F25BFB4024B0848A9183988F865C47F4B2B2818EF3A7853EE183645BFD6F13EF678E5CAF44DCF525982DCD8BD74FC959B5407DEB4F005F377918FE9BE26CE93569FE3C070FF56DD7A8870374C77527888B524A9E34E61C86A89FA4FF1AD20B759433D4C7AAF14AB29727A6679EF3A66AC53A6615A054E3F07CCE131DE2BE9CD4E7D565486B074CA8C7D997D73306D00E0EC64AABDBE0BCBE000503DC165B8739ACF83BB3CDDABBA87B067C6D168C695BBD27355BB94469
	6D9E626DCA07756D22AED33BF040C782EC9D666D52C7746DDC4D542E86701DA37EF6E1816DF22FD03BD8406F5235CB9F55375B21352B9965751D6AF70A5A0E6B32769A6A04653ADDE22F684B7774010F9494FCCBAAD5F35AF836AE21EC23C6A9F7DD580BFA0E07729E002CA9E40FBFFF13F257F95D11C25C8BE776054C4D27283979943FBD8AAF973D416D6845BA64998EF591D34806DEB16B574FA20F4759E6E7CB372A7FB5963D3DF25CDA8BEEE05A6DEE0FD2818145C736325102634535DDC52B394E82168321
	FC2201EF8240142908FF4A12259BFBAE604A81E400D46EC30D9F4337476975C8796C3FA5323D2DDB326521E47345BEA51F45357CCA2CAB431FA77DFA3800FB8E3BA275186C9E65EA253E3BA9E47FAFCEDA557CF445110B6A38362DDFA1940EFE1FE234DF88BB581932F5AA17F562D43F4CE3663626EE876073EFEA2279975E8231860047223EE1FDAC0A82FCACC092C07A2D34F7EAEC798CE3A40B8A4C7FEEE4AA2A7C5F3ACC7CF300EE432DA4DF6FE51C238C72B1CBEA4C48E7D11487A1AEE75502D57AA6FDE3AE
	F2BE4EC31FDE504B5B2AA7E33A9EBA3A71BE250DB79C463A75D6321757AA7576A2CC035C9840B40D66D358FC963EA21F887F54C86EF6273BE72C4D9469E6C0B6B3727697A8EB4C0765E22F22B89DD70B0228844B3BA8F082FC7C0B1AE97CADB0360553E87CBFCBB19AFF6CB4527540D0810E5F65127BA747DE65DFF9057847836D6C69447F3729C67CAD53097F06D3FF052F57E1F3CE123D4ABF79FB625F8934A32613FEBA995E6E30AB908B6D83D21EEABBAF9D4B01EF6B18756FEA052E291567D2AF307D668E20
	19B0FD6C7ED2182146EC751B87DF3E8FBB50073C8BE7846F955FE6F8530C603D6232DE5AABB6C3DD4C8C5ABB7457234F665EE64D388850G6082888758F09B45543F56E16C99F7EFD66100AD13C3144D8149926D116A10AF64B7EA79D7F5A8A76619826514CB9B03775D5BEE8B14472E20B26F37F6CC3FF7034A8742827BF19DBDFE924F378C56B574E4B5375AD9250FE739C0279CC086188240573D83EC8D42983BB00C5637612F756B5B660343A9998631162D516A589243F431D6366C633AF91F65A427EF0718
	GE0831882308AA086A07EF6524F30754B097443EC5A62AC81DCB964BEA541B92AA64F1626496941B126DF309E52E69A6921EB7018319E5862407510B12057C376CC2E07F278B64FA4BDBCB81C2E52D4E6D27ED1B60AB69F8565ADGF1B3E96F14F5F9F2F3BCB126DF709C13E69A757B45A4793DB0A13F83F798497420B9C44E4643A054F1396E9EB5326FDC20F97E8E126B19AA12EB4D9DA43FFB1BA7B71FF5E37A854FA7ACE8BEFC8C492969AA0D88ED8C66AB139B03B826DF7098F6980E6153B49AC322B606FD
	5FCFEE8C3F9F53AFBF281CB7AB309FBF7B91BCB8060BD0E7836038FA0673C1B9DC43898A62D6C1F93306FB4F0B381DD0CE52F0DD8C17BE9B565459B42743FD38974D534A592C0CF6414B2BFAFF6873966495A81FFF7A41F9684FF4F84C3C25BE74B9426D75466788F36A0D4F9166579F62F82021BF1D247A2575631FB32C2EB7BAE7F8070D7BDF5FBE48E8FBBC12078571D2D191553A67155F8B86678DD5E20F244E7908303DF13F5A26CCF116E0BE49E3442FCE06083BB5A26C1CF8FC286C8B0E8FF836D78E7AEA
	1ECD7161D8B57905B21B62EE8E5B5FC7C1F98BC01CE6CB076A506E13E6875AF67A1D3C1CFDE7A07E741D1423BC5004F9DEA914DBGFCG61G9B6E24756F0B06897313B25FB87949470D21ED61DC2331AD94B4621E5563743A5B24E0FD97B59E536A7CABED856CA5D0B53C4D372C3FA23ABB24B176F2097567F5217AB7B50632A92E7785CDFA3DFF22693D236C9DE33D1B5B5CCED9C66D8B9473CA86C83731F712EE1B87D13769F32C99B9G676710EE2307D137B5F30C6FC8EC86F81E2FBE4C56E85F9C1E2FA6EB
	781FB27CB49D1E67315F0D521DDCB854A54D21DC7C3B4B7A79FE2A4961F0339E7F19B63753822DEB8D61B437C3A9C8BB9C4A11GB161B4374F2A0C6E04F60473B9A5040F1D9B677DF4G6FB3824537B7D20D6889F355DC9F3EF9F383E9F2FAB32F6869FD2E51ABC8B322D7285228012F4D105E056F75744EE978239E0CABB629DD744A906A1C9E295A25DE0FGFE43DC1455D98F494FB717F46070A0BD3764318F3013B5EABB0335C51DA54D253D5946BE4C095B5DE28F181F59A53589CB6E4236C7950AB969D04E
	86A884E886983A0B781C77A20D9E27CD14B59E55DE3AD30D575ACDE56D943B424612F4975ECD2F9485ED1EA7B2712CD0BD4982C7D3D87CC68760B6B23D0F66F87EEE32115AFE3491971443GA26EA69BF927DA2F03CD005FF637DF66BB6F8EE57F73D82CCC3ABBF89F370C615366856F63BE3050BEAE8B6AFC73E8AD4FE37B10B8A8A7EA38172D084B1AEF4DB8BD1FF08575149B14EAB887433920BCEBBE4554071AC24744071B0CE362A3CD466B6323CD632F0F7F5694AA16F159168F10ECA3E0FC517383E539F5
	FEA859FEB60C320A1B9FAC5B0B8C1FB0BFD8362D03A45BBDD057F28F4967F136375882659D9A6E708849F157BDE4830F0C8A5A18376AFEDF9A7D217399BDF09EDDFF839E1F8A41F16640823F6CF396045243F496D74FAD60725E276167B23CC90767FAB84A70D836C15D5685A473CA3697B93590E242C2422D2FA2BD14EB38F758FEC1067210063BA5155A05E938958C978565988DF7A90DF8EC57F0BFCE233EBBB55C32CA4425AD32E69CDBC47126420372519F8E565B9DB6E79F04963C15D6BC792595661AEAF3
	59556A2A2BA5A678414F8BA27A74EFD54E7F6E96437B9904D30B380CAF2A5FD92FAFCA663CFF4D6EDE5AC55963DF3F3FD4A86E7A207D06C5086BD674E7E65837A7056FBFF87D5200FA74CBBC037657BF99D00F63BF18262FCF766A6B776A7A7339BE7EA7BA9B59737886C17E5134106FF2F62BF34CDE2C1E0DBC31A839F9C14D9BFC3E6DDE5C17A8EE2F4469BEF9D9AF434C45F2CAF2883A9615DE8B51556E2FE776855B773C45FE1B0EDC9C4A3E9FE3EB42664541767D6F8C3FFDF130FDB77413FDA7C0DD4D5F11
	CD1DE339F4B41437EA384B6CFEED871493B45CD3E632474CA594FB96DBD09E05DAF995AB633D93AF1F32DCCFFC407E130F8FE55745A3F59274295F91612BBAF56D17F06D5F328465E12D231C21864A1DG8B81221670FBB75E6F8D36476A35FB6C10195422CFFA9D0A1ABB9C2F27BC7740D2D8AB1732B8D01FB34ED83217F2B9F15A536B59DE000E7FF163A1D0DE322CDE734125243F2BAC2F0E06728E8D779306CB00F256B2429D9AA4BD67ADA3BD968E23DEADDA79EBD646FBC8DE6E9C1C6CBA00B42FCF4FD3A7
	4D63FAF44FE57A6C30DE4FBE140970AA1B2B8C3FF92EF8F204743E99F031G09586EDE50613DA4C313C07ED323619ECBA639AA77125C6EE0EBCE8414B78144EA78858C5F295E25627D2C1A6F25041281768DB537CD86F59186B8F55F9112473EF1643757GE7A4F7758EA30D45D1C866F8FE3C288D0A42AE141570E8407937F17FF329017DFFEE7377DF14BAF17F07D3F1AE1283B78CB655E7E2D349E7CEC7409E0F51CD127CF467D7CECC77C715E369EE2BA43A2591F436303C9DF18DD0D6A2A8775DA5FE84DF2B
	58251EA414596C0A534D0E92GD963F642EEC86967D8F6B1286A2BD8195C3B45FF5EC7EBE274BFBCA0EC1AD19A723EE0DE047A16D37DEE5A36A8993FFCAE991DA817F8289316DBB3F2818A81EAGBA17535C7E520AE344939198FE112208ED1D6CA61C8ECA2CF6294F0F460D99271DEF4711F29A5E56BF05128FD07896721F6A56AE080596915F87F01FDB5EC7FEB8341C7CB0271F7CB0E2B97955B636B6EE01F29CC022065F4170F81F0CF7FCF82F4E715F77130FE05BE0F94599600CE0CB08F61CC71F4643486F
	0C7C6DC6E21FFAFED10DEFFEDA1DEE36B16271AE2F96FDABFD852DE11B7A300DAC76BB3D8A2DFD53EA499706205DBC004DG31AB380C78DA16E04E99E7DC3BD6685732B7593E26C23D7766794AC81D36AF023667D6529E7E8E6904E0F416D44ACF8E10C0A58F60BC9FFD2C0E766872CA5ACF97DA1176AAA8EF86583E126E50FC9F10CFC42DD9A144BEE77CC6E427E5B8A5E1A59FFF66AAFFBE97F532B5154F6733FA6C7309B63F32C114E10904771564FFFD92714ADE3BD248BEFEB165D0887FB329BCFCBADE3A18
	D30FEFD87579FF4033GA60BFCF3C555B726322B66B2D3D90925223ACA6D5BBB0077113D90D86CED224C8F630AC5F70DD33637752B71EBAE4B91B0B6C52DA21BF8E2G7D335369368FB89D4A581E14073C487AFE057ADEC5F611B7B81EDD8466B8499FE1CCAA713F5BD07187C7D0026A1BF4C91F031F9EB425240610E35EEACE7B7CEA7FBE065BC8758845684E5534E7999E895E334CCD566FB99E9C895E33E0FFFF7DB3A341FB16407EC7C60377AC541F7B18D7A09F0B00B1ED82588910341A47A2AE4B7FCA9ECF
	16875668E579E5974A32487FAE02EC73B43EC32A535EABD07BF4767E21C4770E034BFB6B957DDEAA6723EA433D546EAB83A9B9AA7F52B53AF75E16F37BB97D67D375FBC70E7BE5259E273E1DEA473E5AB98DDF7A543AF56DA8C36DAD84BBACB4C3C3C787DFBFEA3B315E877C6781EC82588630F38D2DCBE7BAB01660AB6F31EBD2192DC322BDB4714B6A8CFDBE75474E095F0367F6222CDB1DA04D9E24CF78BFF7323B8C270BA155B533131DEB5ABBBA955ED445FF5A494E9924F60666367224F6E732E78D0F47D9
	77011D5C476FD6AF5E07EDBE6F1FB8FF794604BA571D4A1F61788C894747EFE94E28EDCB589BF30CB58C2D5138D05BAE844623E2363EFCAD3641FA5D1F4FDE35CB96BC9F35711A3C099A17A9ED1DDA631327F2CE7068F662A46DED23EA353F190139465CC7EB457F64234C3435829F5D9494F0BAA63E20F17FCF2AA57F5FFD9F2D91A7587B6B5311A0C30016C80AFD7BADC83B4A69D6BA85DA2B2755CD140379A20D6EE46F1A30DF70BDB16F7778387D66C506BAB35840563D484860FB0F0799BEBAB2785E2358CA
	F20D013A5C7BA9FF5DA023AD05C1F99540E6006DGC9G198FE8E7397653428477E396274B844EA46B0EE7B97D830E9C7866822D0BGB600A100F000C8004DG5BB5BE2F5A730450F9B3D7B673E337E8F7A8DC0711832703ECD4E773795632DACBC5C911692A3924365AE4AAAE2A3DDAD586C8F5CD9ABCBBCE5F726A5A0A266AAA4B585E14AFDFF0E2FE15280D7F4B2E93630C9F5F4E7008C63A370C5CE83E4FF539D4A0DD262D059CFDAD7948A7E7835EEDF09F79DAF676299EC2F954F38279083216FC64C8B23B
	F305729600383564A32F31BF58B3C3F77491BE6F4BAE3419A4E8139985799C008920854097C532185AB51EAE79007C3AD46FA43BC347E431FB62185C554D5E66C2AC35743B54FBCEE7B766EB4E76F60F2445FE25BBD343D5F8EA1C9EBB2EB6DC362F7411ECE741DC36C451BD75D7BE12591EA812D90F0F1DCB2F0379835856D16E59552D4F87F12D8CF5C6B9340E7BF7443A40FC105B47C335137B7B3718C976FB75067B7118F38F7F7B16F5413168DE06CFD8979C0B724CA4635DD0573C5E1A415FA3449CC1B959
	9D325DA115703F2A92E87F77AC4B7DE320ED6CFA5A3B7D981FD82AE3FFC4773B5CFA736FB2441B4AC33DA713C37BC30CECFC077618ECFC0736CD9E7F8D49F6B9745F2A7CC29E7B37AAC79D2147762EE3E2DF7D50E11CBFFD6C981BBF1DF59867CF679D7A7CC90D41AADD3776100563CB9CE8DBFC6FDBA9BA448E5DDC0A1D98A3BC8131F965183B2A6B330B0D136C77628D7763BEF69B0B5B716B03FDAF1C619357877B5E9F12497776C0DD4B03942FED2E507A6CF6CD2CCF45E52C4F8157D8FD0E3A0C75B95D2557
	A7174BEF465C29DC1FBCB3FC136BD7FA437D383C1FE1EB411687037570A24347BE982C870F0F749087F597B7D0BE76BA7B9B76D5D00E810887588310399176G9BC9D7472E61F9C42835355669E4F9BCBEC80035D51FB229F3DCE29634792E567D4EB75F7C38794DCD6541F316D76BB0AF735887A4FEF6F150CAEBF761C6DABBE6A77B67FD0F6E7771641BAF0373FF839E4F7B7CF3CA547D6E775DFC5EF75DD49E5C87525D216355C17744716A185BB8DE3D6B9E9B2F5EF79B472B13EEFD3C62367701155DBBG70
	7F7F455B8578B7DBD1AF89FB9561AD7EA70DB97C944C2388EF70B399C5584B8EF394A18D8FEF84BA8FBBB92ABF8F5BBD3DAD643D7E3907F87ED87E10BF0F647124299047B88D70918FD17CF8608B9CD32873F7F30FA84B0AA8F797B9ECF896DF5C2FC87EBC61EAA15277D09B351257359762383BF4FD7E9FD0CB8788914091E97696GGE0C4GGD0CB818294G94G88G88G710171B4914091E97696GGE0C4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GG
	GG81G81GBAGGGB097GGGG
**end of data**/
}
}
