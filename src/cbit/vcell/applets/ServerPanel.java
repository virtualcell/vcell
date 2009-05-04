package cbit.vcell.applets;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;

import org.vcell.util.CacheStatus;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.server.*;
/**
 * Insert the class' description here.
 * Creation date: (8/20/2000 6:33:46 AM)
 * @author: John Wagner
 */
public class ServerPanel extends JPanel implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
	public static final int SERVER_CONNECTING = 0;
	public static final int SERVER_UP = 1;
	public static final int SERVER_DOWN = 2;
	private JLabel ivjServerStatusLabel = null;
	//
	private Vector slaveServerPanels = new Vector ();
	private int fieldPrimaryServerStatus = SERVER_CONNECTING;
	private java.lang.String fieldAdminHost = null;
	private int fieldAdminPort = 0;
	private JLabel ivjJLabel1 = null;
	private JPanel ivjJPanel1 = null;
	private JScrollPane ivjJScrollPane1 = null;
	private JTable ivjScrollPaneTable = null;
	private SolverControllerTableModel ivjSolverControllerTableModel1 = null;
	private JLabel ivjJLabel = null;
	private JScrollPane ivjJScrollPane2 = null;
	private JTable ivjScrollPaneTable1 = null;
	private ServerTableModel ivjServerTableModel1 = null;
	private JLabel ivjJLabel2 = null;
	private JList ivjJListUsers = null;
	private org.vcell.util.gui.ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
	private JLabel ivjJLabel3 = null;
	private JPanel ivjJPanel2 = null;
	private JRadioButton ivjJRadioButtonDate = null;
	private JRadioButton ivjJRadioButtonHost = null;
	private JRadioButton ivjJRadioButtonStatus = null;
	private JRadioButton ivjJRadioButtonType = null;
	private JRadioButton ivjJRadioButtonUser = null;
	private JPanel ivjJPanel3 = null;
	private JPanel ivjJPanel4 = null;
	private JPanel ivjJPanel5 = null;
	private JPanel ivjJPanel6 = null;
	private JSplitPane ivjJSplitPane1 = null;
	private JSplitPane ivjJSplitPane2 = null;
	private boolean ivjConnPtoP3Aligning = false;
	private JButton ivjJButtonKillServer = null;
	private ServerMonitorInfo ivjselectedServerMonitorInfo = null;
	private ListSelectionModel ivjselectionModel1 = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public ServerPanel() {
	super();
	initialize();
}


/**
 * ServerPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ServerPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * ServerPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ServerPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * ServerPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ServerPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonKillServer()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (JButtonKillServer.action.actionPerformed(java.awt.event.ActionEvent) --> ServerPanel.killServer(Lcbit.vcell.applets.ServerMonitorInfo;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getselectedServerMonitorInfo() != null)) {
			this.killServer(getselectedServerMonitorInfo());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (ServerPanel.initialize() --> ButtonGroupCivilized1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getJRadioButtonDate());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (ServerPanel.initialize() --> ButtonGroupCivilized1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getJRadioButtonStatus());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (ServerPanel.initialize() --> ButtonGroupCivilized1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getJRadioButtonUser());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (ServerPanel.initialize() --> ButtonGroupCivilized1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getJRadioButtonType());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM5:  (ServerPanel.initialize() --> ButtonGroupCivilized1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getJRadioButtonHost());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM6:  (ButtonGroupCivilized1.selection --> SolverControllerTableModel1.sortByColumn)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getSolverControllerTableModel1().setSortByColumn(this.getSelectedActionCommand(getButtonGroupCivilized1().getSelection()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM7:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> selectedServerMonitorInfo.this)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setselectedServerMonitorInfo(this.getSelectedServerMonitorInfo(getselectionModel1().getMinSelectionIndex(), getselectionModel1().getMaxSelectionIndex()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM8:  (selectedServerMonitorInfo.this --> JButtonKillServer.enabled)
 * @param value cbit.vcell.applets.ServerMonitorInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(ServerMonitorInfo value) {
	try {
		// user code begin {1}
		// user code end
		getJButtonKillServer().setEnabled((getselectedServerMonitorInfo() != null));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (SolverControllerTableModel1.this <--> ScrollPaneTable.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable().setModel(getSolverControllerTableModel1());
		getScrollPaneTable().createDefaultColumnsFromModel();
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (ServerTableModel1.this <--> ScrollPaneTable1.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable1().setModel(getServerTableModel1());
		getScrollPaneTable1().createDefaultColumnsFromModel();
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (ScrollPaneTable1.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getselectionModel1() != null)) {
				getScrollPaneTable1().setSelectionModel(getselectionModel1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (ScrollPaneTable1.selectionModel <--> selectionModel1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setselectionModel1(getScrollPaneTable1().getSelectionModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Gets the adminHost property (java.lang.String) value.
 * @return The adminHost property value.
 * @see #setAdminHost
 */
public java.lang.String getAdminHost() {
	return fieldAdminHost;
}


/**
 * Gets the adminPort property (int) value.
 * @return The adminPort property value.
 * @see #setAdminPort
 */
public int getAdminPort() {
	return fieldAdminPort;
}


/**
 * Return the ButtonGroupCivilized1 property value.
 * @return cbit.gui.ButtonGroupCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.gui.ButtonGroupCivilized getButtonGroupCivilized1() {
	if (ivjButtonGroupCivilized1 == null) {
		try {
			ivjButtonGroupCivilized1 = new org.vcell.util.gui.ButtonGroupCivilized();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroupCivilized1;
}


/**
 * Return the JButtonKillServer property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonKillServer() {
	if (ivjJButtonKillServer == null) {
		try {
			ivjJButtonKillServer = new javax.swing.JButton();
			ivjJButtonKillServer.setName("JButtonKillServer");
			ivjJButtonKillServer.setText("Kill Server");
			ivjJButtonKillServer.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonKillServer;
}


/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel() {
	if (ivjJLabel == null) {
		try {
			ivjJLabel = new javax.swing.JLabel();
			ivjJLabel.setName("JLabel");
			ivjJLabel.setText("Servers");
			ivjJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel;
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
			ivjJLabel1.setText("Simulation Jobs");
			ivjJLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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
			ivjJLabel2.setText("Users");
			ivjJLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("Sort By: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}


/**
 * Return the JListUsers property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJListUsers() {
	if (ivjJListUsers == null) {
		try {
			ivjJListUsers = new javax.swing.JList();
			ivjJListUsers.setName("JListUsers");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListUsers;
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

			java.awt.GridBagConstraints constraintsJSplitPane1 = new java.awt.GridBagConstraints();
			constraintsJSplitPane1.gridx = 0; constraintsJSplitPane1.gridy = 0;
			constraintsJSplitPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJSplitPane1.weightx = 1.0;
			constraintsJSplitPane1.weighty = 1.0;
			constraintsJSplitPane1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJSplitPane1(), constraintsJSplitPane1);
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.FlowLayout());
			getJPanel2().add(getJLabel3(), getJLabel3().getName());
			getJPanel2().add(getJRadioButtonHost(), getJRadioButtonHost().getName());
			getJPanel2().add(getJRadioButtonType(), getJRadioButtonType().getName());
			getJPanel2().add(getJRadioButtonUser(), getJRadioButtonUser().getName());
			getJPanel2().add(getJRadioButtonStatus(), getJRadioButtonStatus().getName());
			getJPanel2().add(getJRadioButtonDate(), getJRadioButtonDate().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}


/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJSplitPane2 = new java.awt.GridBagConstraints();
			constraintsJSplitPane2.gridx = 0; constraintsJSplitPane2.gridy = 0;
			constraintsJSplitPane2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJSplitPane2.weightx = 1.0;
			constraintsJSplitPane2.weighty = 1.0;
			constraintsJSplitPane2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel3().add(getJSplitPane2(), constraintsJSplitPane2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}


/**
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel4() {
	if (ivjJPanel4 == null) {
		try {
			ivjJPanel4 = new javax.swing.JPanel();
			ivjJPanel4.setName("JPanel4");
			ivjJPanel4.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJListUsers = new java.awt.GridBagConstraints();
			constraintsJListUsers.gridx = 0; constraintsJListUsers.gridy = 1;
			constraintsJListUsers.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJListUsers.weightx = 1.0;
			constraintsJListUsers.weighty = 1.0;
			constraintsJListUsers.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel4().add(getJListUsers(), constraintsJListUsers);

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel4().add(getJLabel2(), constraintsJLabel2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel4;
}


/**
 * Return the JPanel5 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel5() {
	if (ivjJPanel5 == null) {
		try {
			ivjJPanel5 = new javax.swing.JPanel();
			ivjJPanel5.setName("JPanel5");
			ivjJPanel5.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel5().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 1;
			constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel5().add(getJPanel2(), constraintsJPanel2);

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 2;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.ipadx = 380;
			constraintsJScrollPane1.ipady = 147;
			constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel5().add(getJScrollPane1(), constraintsJScrollPane1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel5;
}


/**
 * Return the JPanel6 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel6() {
	if (ivjJPanel6 == null) {
		try {
			ivjJPanel6 = new javax.swing.JPanel();
			ivjJPanel6.setName("JPanel6");
			ivjJPanel6.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel = new java.awt.GridBagConstraints();
			constraintsJLabel.gridx = 0; constraintsJLabel.gridy = 0;
			constraintsJLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel6().add(getJLabel(), constraintsJLabel);

			java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
			constraintsJScrollPane2.gridx = 0; constraintsJScrollPane2.gridy = 1;
			constraintsJScrollPane2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane2.weightx = 1.0;
			constraintsJScrollPane2.weighty = 1.0;
			constraintsJScrollPane2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel6().add(getJScrollPane2(), constraintsJScrollPane2);

			java.awt.GridBagConstraints constraintsJButtonKillServer = new java.awt.GridBagConstraints();
			constraintsJButtonKillServer.gridx = 0; constraintsJButtonKillServer.gridy = 2;
			constraintsJButtonKillServer.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel6().add(getJButtonKillServer(), constraintsJButtonKillServer);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel6;
}

/**
 * Return the JRadioButtonDate property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonDate() {
	if (ivjJRadioButtonDate == null) {
		try {
			ivjJRadioButtonDate = new javax.swing.JRadioButton();
			ivjJRadioButtonDate.setName("JRadioButtonDate");
			ivjJRadioButtonDate.setText("StartDate");
			ivjJRadioButtonDate.setActionCommand("StartDate");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonDate;
}

/**
 * Return the JRadioButtonHost property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonHost() {
	if (ivjJRadioButtonHost == null) {
		try {
			ivjJRadioButtonHost = new javax.swing.JRadioButton();
			ivjJRadioButtonHost.setName("JRadioButtonHost");
			ivjJRadioButtonHost.setText("Host");
			ivjJRadioButtonHost.setActionCommand("Host");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonHost;
}

/**
 * Return the JRadioButtonStatus property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonStatus() {
	if (ivjJRadioButtonStatus == null) {
		try {
			ivjJRadioButtonStatus = new javax.swing.JRadioButton();
			ivjJRadioButtonStatus.setName("JRadioButtonStatus");
			ivjJRadioButtonStatus.setText("Status");
			ivjJRadioButtonStatus.setActionCommand("Status");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonStatus;
}

/**
 * Return the JRadioButtonType property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonType() {
	if (ivjJRadioButtonType == null) {
		try {
			ivjJRadioButtonType = new javax.swing.JRadioButton();
			ivjJRadioButtonType.setName("JRadioButtonType");
			ivjJRadioButtonType.setText("Job Type");
			ivjJRadioButtonType.setActionCommand("Job Type");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonType;
}

/**
 * Return the JRadioButtonUser property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonUser() {
	if (ivjJRadioButtonUser == null) {
		try {
			ivjJRadioButtonUser = new javax.swing.JRadioButton();
			ivjJRadioButtonUser.setName("JRadioButtonUser");
			ivjJRadioButtonUser.setText("User");
			ivjJRadioButtonUser.setActionCommand("User");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonUser;
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
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPane1().setViewportView(getScrollPaneTable());
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
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane2() {
	if (ivjJScrollPane2 == null) {
		try {
			ivjJScrollPane2 = new javax.swing.JScrollPane();
			ivjJScrollPane2.setName("JScrollPane2");
			ivjJScrollPane2.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane2.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPane2().setViewportView(getScrollPaneTable1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane2;
}


/**
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getJSplitPane1() {
	if (ivjJSplitPane1 == null) {
		try {
			ivjJSplitPane1 = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
			ivjJSplitPane1.setName("JSplitPane1");
			getJSplitPane1().add(getJPanel3(), "left");
			getJSplitPane1().add(getJPanel4(), "right");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSplitPane1;
}


/**
 * Return the JSplitPane2 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getJSplitPane2() {
	if (ivjJSplitPane2 == null) {
		try {
			ivjJSplitPane2 = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			ivjJSplitPane2.setName("JSplitPane2");
			ivjJSplitPane2.setDividerLocation(300);
			getJSplitPane2().add(getJPanel5(), "top");
			getJSplitPane2().add(getJPanel6(), "bottom");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSplitPane2;
}


/**
 * Gets the primaryServerStatus property (int) value.
 * @return The primaryServerStatus property value.
 * @see #setPrimaryServerStatus
 */
private int getPrimaryServerStatus() {
	return fieldPrimaryServerStatus;
}


/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new javax.swing.JTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}


/**
 * Return the ScrollPaneTable1 property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getScrollPaneTable1() {
	if (ivjScrollPaneTable1 == null) {
		try {
			ivjScrollPaneTable1 = new javax.swing.JTable();
			ivjScrollPaneTable1.setName("ScrollPaneTable1");
			getJScrollPane2().setColumnHeaderView(ivjScrollPaneTable1.getTableHeader());
			getJScrollPane2().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable1.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable1;
}


/**
 * Comment
 */
private java.lang.String getSelectedActionCommand(javax.swing.ButtonModel selectedButtonModel) {
	String columnName = selectedButtonModel.getActionCommand();
	if (columnName!=null){
		return columnName;
	}else{
		return "Host";
	}
}


/**
 * Return the selectedServerMonitorInfo property value.
 * @return cbit.vcell.applets.ServerMonitorInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ServerMonitorInfo getselectedServerMonitorInfo() {
	// user code begin {1}
	// user code end
	return ivjselectedServerMonitorInfo;
}


/**
 * Comment
 */
private cbit.vcell.applets.ServerMonitorInfo getSelectedServerMonitorInfo(int minIndex, int maxIndex) {
	if (minIndex==maxIndex && minIndex>=0){
		return getServerTableModel1().getServerMonitorInfos(minIndex);
	}else{
		return null;
	}
}


/**
 * Return the selectionModel1 property value.
 * @return javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ListSelectionModel getselectionModel1() {
	// user code begin {1}
	// user code end
	return ivjselectionModel1;
}


/**
 * Return the ServerStatusLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getServerStatusLabel() {
	if (ivjServerStatusLabel == null) {
		try {
			ivjServerStatusLabel = new javax.swing.JLabel();
			ivjServerStatusLabel.setName("ServerStatusLabel");
			ivjServerStatusLabel.setText("Server Status: Connecting...");
			ivjServerStatusLabel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjServerStatusLabel.setForeground(java.awt.Color.red);
			ivjServerStatusLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjServerStatusLabel.setPreferredSize(new java.awt.Dimension(280, 20));
			ivjServerStatusLabel.setFont(new java.awt.Font("dialog", 1, 14));
			ivjServerStatusLabel.setMinimumSize(new java.awt.Dimension(0, 0));
			ivjServerStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjServerStatusLabel;
}

/**
 * Return the ServerTableModel1 property value.
 * @return cbit.vcell.applets.ServerTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ServerTableModel getServerTableModel1() {
	if (ivjServerTableModel1 == null) {
		try {
			ivjServerTableModel1 = new cbit.vcell.applets.ServerTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjServerTableModel1;
}


/**
 * Return the SolverControllerTableModel1 property value.
 * @return cbit.vcell.applets.SolverControllerTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SolverControllerTableModel getSolverControllerTableModel1() {
	if (ivjSolverControllerTableModel1 == null) {
		try {
			ivjSolverControllerTableModel1 = new cbit.vcell.applets.SolverControllerTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSolverControllerTableModel1;
}


/**
 * This method was created in VisualAge.
 * @return VCellBootstrap
 */
protected synchronized cbit.vcell.server.VCellBootstrap getVCellPortal() throws java.rmi.RemoteException {
	try {
		//String AdminHost = "ms2.vcell.uchc.edu:40100";
		String AdminHost = getAdminHost()+":"+getAdminPort();
		String ServiceName = "//"+AdminHost+"/VCellBootstrapServer";
		return (cbit.vcell.server.VCellBootstrap) java.rmi.Naming.lookup(ServiceName);
	}catch (java.rmi.RemoteException e){
		throw e;
	}catch (Exception e){
		throw new java.rmi.RemoteException(e.getMessage());
	}
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

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
	getButtonGroupCivilized1().addPropertyChangeListener(this);
	getScrollPaneTable1().addPropertyChangeListener(this);
	getJButtonKillServer().addActionListener(this);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP3SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ServerPanel");
		setPreferredSize(new java.awt.Dimension(650, 250));
		setLayout(new java.awt.BorderLayout());
		setSize(589, 575);
		setMinimumSize(new java.awt.Dimension(650, 250));
		add(getServerStatusLabel(), "North");
		add(getJPanel1(), "Center");
		initConnections();
		connEtoM1();
		connEtoM2();
		connEtoM3();
		connEtoM4();
		connEtoM5();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void killServer(final ServerMonitorInfo serverMonitorInfo) {
	if (serverMonitorInfo==null){
		return;
	}
	AsynchClientTask killingServer = new AsynchClientTask("requesting shutdown of server on "+serverMonitorInfo.getHostName(), AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hash) throws Exception {
			String ServiceName = "//"+serverMonitorInfo.getHostName()+"/VCellBootstrapServer";
			VCellBootstrap vcellBootstrap = (cbit.vcell.server.VCellBootstrap) java.rmi.Naming.lookup(ServiceName);
			VCellServer vcellServer = vcellBootstrap.getVCellServer(new User("Administrator",new KeyValue("2")), "icnia66");
			vcellServer.shutdown();
		}
	};
	AsynchClientTask tasks[] = new AsynchClientTask[] { killingServer };
	ClientTaskDispatcher.dispatch(this,new Hashtable<String, Object>(),tasks,false);
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ServerPanel aServerPanel;
		aServerPanel = new ServerPanel();
		frame.getContentPane().add("Center", aServerPanel);
		frame.setSize(aServerPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == getButtonGroupCivilized1() && (evt.getPropertyName().equals("selection"))) 
		connEtoM6(evt);
	if (evt.getSource() == getScrollPaneTable1() && (evt.getPropertyName().equals("selectionModel"))) 
		connPtoP3SetTarget();
	// user code begin {2}
	// user code end
}

/**
 * Sets the adminHost property (java.lang.String) value.
 * @param adminHost The new value for the property.
 * @see #getAdminHost
 */
public void setAdminHost(java.lang.String adminHost) {
	String oldValue = fieldAdminHost;
	fieldAdminHost = adminHost;
	firePropertyChange("adminHost", oldValue, adminHost);
}


/**
 * Sets the adminPort property (int) value.
 * @param adminPort The new value for the property.
 * @see #getAdminPort
 */
public void setAdminPort(int adminPort) {
	int oldValue = fieldAdminPort;
	fieldAdminPort = adminPort;
	firePropertyChange("adminPort", new Integer(oldValue), new Integer(adminPort));
}


/**
 * Sets the primaryServerStatus property (int) value.
 * @param primaryServerStatus The new value for the property.
 * @see #getPrimaryServerStatus
 */
private synchronized void setPrimaryServerStatus(int primaryServerStatus) {
	fieldPrimaryServerStatus = primaryServerStatus;
	java.util.Date date = new java.util.Date ();
	if (primaryServerStatus == SERVER_CONNECTING) {
		getServerStatusLabel().setForeground(new java.awt.Color(0, 0, 255));
		getServerStatusLabel().setText("Server Status: Connecting...");
	} else if (primaryServerStatus == SERVER_UP) {
		getServerStatusLabel().setForeground(new java.awt.Color(0, 0, 0));
		getServerStatusLabel().setText("Server Status: OK (" + date.toString() + ")");
	} else if (primaryServerStatus == SERVER_DOWN) {
			getServerStatusLabel().setForeground(new java.awt.Color(255, 0, 0));
			getServerStatusLabel().setText("Server Status: Down (" + date.toString() + ")");
	}
}


/**
 * Set the selectedServerMonitorInfo to a new value.
 * @param newValue cbit.vcell.applets.ServerMonitorInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectedServerMonitorInfo(ServerMonitorInfo newValue) {
	if (ivjselectedServerMonitorInfo != newValue) {
		try {
			ivjselectedServerMonitorInfo = newValue;
			connEtoM8(ivjselectedServerMonitorInfo);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}


/**
 * Set the selectionModel1 to a new value.
 * @param newValue javax.swing.ListSelectionModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setselectionModel1(javax.swing.ListSelectionModel newValue) {
	if (ivjselectionModel1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.removeListSelectionListener(this);
			}
			ivjselectionModel1 = newValue;

			/* Listen for events from the new object */
			if (ivjselectionModel1 != null) {
				ivjselectionModel1.addListSelectionListener(this);
			}
			connPtoP3SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2002 11:02:42 PM)
 */
public void updateAll() {
	AsynchClientTask gatherPrimaryServerInfo = new AsynchClientTask("gathering primary server info", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hash) throws Exception {
			VCellBootstrap vcellBootstrap = getVCellPortal();
			VCellServer vcellServer = vcellBootstrap.getVCellServer(new User("Administrator",new KeyValue("2")), "icnia66");
			ServerInfo primaryServerInfo = vcellServer.getServerInfo();
			cbit.vcell.solvers.SolverControllerInfo[] solverControllerInfos = vcellServer.getSolverControllerInfos();
			if (vcellServer!=null){
				hash.put("vcellServer",vcellServer);
			}
			if (primaryServerInfo!=null){
				hash.put("primaryServerInfo",primaryServerInfo);
			}
			if (solverControllerInfos!=null){
				hash.put("solverControllerInfos",solverControllerInfos);
			}
		}
	};
	AsynchClientTask displayPrimaryServerInfo = new AsynchClientTask("display primary server info", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(java.util.Hashtable hash){
			ServerInfo primaryServerInfo = (ServerInfo)hash.get("primaryServerInfo");
			cbit.vcell.solvers.SolverControllerInfo solverControllerInfos[] = (cbit.vcell.solvers.SolverControllerInfo[])hash.get("solverControllerInfos");
			
			setPrimaryServerStatus(ServerPanel.SERVER_UP);
			getServerTableModel1().setServerMonitorInfos(getAdminHost()+":"+getAdminPort(),new ServerMonitorInfo(getAdminHost()+":"+getAdminPort(),ServerMonitorInfo.SERVERTYPE_MAIN,primaryServerInfo));
			org.vcell.util.gui.DefaultListModelCivilized listModel = new org.vcell.util.gui.DefaultListModelCivilized();
			listModel.setContents(primaryServerInfo.getConnectedUsers());
			getJListUsers().setModel(listModel);
			getSolverControllerTableModel1().setSolverControllerInfos(solverControllerInfos);
		}
	};
	AsynchClientTask gatherSlaveServerInfo = new AsynchClientTask("gather slave server info", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(java.util.Hashtable hash) throws Exception {
			VCellServer vcellServer = (VCellServer)hash.get("vcellServer");
			ConnectionPoolStatus connectionPoolStatus = vcellServer.getConnectionPoolStatus();
			ComputeHost activeHosts[] = connectionPoolStatus.getActiveHosts();
			ServerInfo slaveServerInfos[] = vcellServer.getSlaveServerInfos();
			hash.put("activeHosts",(activeHosts!=null)?(activeHosts):(new ComputeHost[0]));
			hash.put("serverInfos",(slaveServerInfos!=null)?(slaveServerInfos):(new ServerInfo[0]));
		}
	};
	AsynchClientTask displaySlaveServerInfo = new AsynchClientTask("gather slave server info", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(java.util.Hashtable hash){
			ComputeHost[] activeHosts = (ComputeHost[])hash.get("activeHosts");
			ServerInfo[] serverInfos = (ServerInfo[])hash.get("serverInfos");
			int slaveServerCount = (activeHosts!=null)?(activeHosts.length):(0);
			for (int i = 0; i < slaveServerCount; i++) {
				//
				// find serverInfo (if availlable) for current host, and display
				//
				ServerInfo serverInfo = null;
				for (int j=0;j<serverInfos.length;j++){
					if (ServerTableModel.nameSameWithoutDomain(serverInfos[j].getHostName(),activeHosts[i].getHostName())){
						serverInfo = serverInfos[j];
					}
				}
				getServerTableModel1().setServerMonitorInfos(activeHosts[i].getHostName(),new ServerMonitorInfo(activeHosts[i].getHostName(),ServerMonitorInfo.SERVERTYPE_COMPUTE,serverInfo));
			}
		}
	};
	AsynchClientTask tasks[] = new AsynchClientTask[] { gatherPrimaryServerInfo, displayPrimaryServerInfo, gatherSlaveServerInfo, displaySlaveServerInfo };
	Hashtable<String, Object> hash = new Hashtable<String, Object>() {
		public synchronized Object put(String key, Object value){
			System.out.println("put("+key+","+value+")");
			return super.put(key,value);
		}
	};
	ClientTaskDispatcher.dispatch(this,hash,tasks,true);
}


/**
 * Method to handle events for the ListSelectionListener interface.
 * @param e javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanged(javax.swing.event.ListSelectionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getselectionModel1()) 
		connEtoM7(e);
	// user code begin {2}
	// user code end
}
}