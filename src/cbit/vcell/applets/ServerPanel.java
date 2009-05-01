package cbit.vcell.applets;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
	private cbit.gui.ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
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
private cbit.gui.ButtonGroupCivilized getButtonGroupCivilized1() {
	if (ivjButtonGroupCivilized1 == null) {
		try {
			ivjButtonGroupCivilized1 = new cbit.gui.ButtonGroupCivilized();
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
	AsynchClientTask killingServer = new AsynchClientTask() {
		public String getTaskName() { return "requesting shutdown of server on "+serverMonitorInfo.getHostName(); }
		public int getTaskType() { return AsynchClientTask.TASKTYPE_NONSWING_BLOCKING; }
		public void run(java.util.Hashtable hash) throws Exception {
			String ServiceName = "//"+serverMonitorInfo.getHostName()+"/VCellBootstrapServer";
			VCellBootstrap vcellBootstrap = (cbit.vcell.server.VCellBootstrap) java.rmi.Naming.lookup(ServiceName);
			VCellServer vcellServer = vcellBootstrap.getVCellServer(new User("Administrator",new KeyValue("2")), "icnia66");
			vcellServer.shutdown();
		}
		public boolean skipIfAbort() {
			return true;
		}
		public boolean skipIfCancel(UserCancelException e){
			return true;
		}
	};
	AsynchClientTask tasks[] = new AsynchClientTask[] { killingServer };
	ClientTaskDispatcher.dispatch(this,new java.util.Hashtable(),tasks,false);
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
	AsynchClientTask gatherPrimaryServerInfo = new AsynchClientTask() {
		public String getTaskName() { return "gathering primary server info"; }
		public int getTaskType() { return AsynchClientTask.TASKTYPE_NONSWING_BLOCKING; }
		public void run(java.util.Hashtable hash) throws Exception {
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
		public boolean skipIfAbort() {
			return true;
		}
		public boolean skipIfCancel(UserCancelException e){
			return true;
		}
	};
	AsynchClientTask displayPrimaryServerInfo = new AsynchClientTask() {
		public String getTaskName() { return "display primary server info"; }
		public int getTaskType() { return AsynchClientTask.TASKTYPE_SWING_BLOCKING; }
		public void run(java.util.Hashtable hash){
			ServerInfo primaryServerInfo = (ServerInfo)hash.get("primaryServerInfo");
			cbit.vcell.solvers.SolverControllerInfo solverControllerInfos[] = (cbit.vcell.solvers.SolverControllerInfo[])hash.get("solverControllerInfos");
			
			setPrimaryServerStatus(ServerPanel.SERVER_UP);
			getServerTableModel1().setServerMonitorInfos(getAdminHost()+":"+getAdminPort(),new ServerMonitorInfo(getAdminHost()+":"+getAdminPort(),ServerMonitorInfo.SERVERTYPE_MAIN,primaryServerInfo));
			cbit.gui.DefaultListModelCivilized listModel = new cbit.gui.DefaultListModelCivilized();
			listModel.setContents(primaryServerInfo.getConnectedUsers());
			getJListUsers().setModel(listModel);
			getSolverControllerTableModel1().setSolverControllerInfos(solverControllerInfos);
		}
		public boolean skipIfAbort() {
			return true;
		}
		public boolean skipIfCancel(UserCancelException e){
			return true;
		}
	};
	AsynchClientTask gatherSlaveServerInfo = new AsynchClientTask() {
		public String getTaskName() { return "gather slave server info"; }
		public int getTaskType() { return AsynchClientTask.TASKTYPE_NONSWING_BLOCKING; }
		public void run(java.util.Hashtable hash) throws Exception {
			VCellServer vcellServer = (VCellServer)hash.get("vcellServer");
			ConnectionPoolStatus connectionPoolStatus = vcellServer.getConnectionPoolStatus();
			ComputeHost activeHosts[] = connectionPoolStatus.getActiveHosts();
			ServerInfo slaveServerInfos[] = vcellServer.getSlaveServerInfos();
			hash.put("activeHosts",(activeHosts!=null)?(activeHosts):(new ComputeHost[0]));
			hash.put("serverInfos",(slaveServerInfos!=null)?(slaveServerInfos):(new ServerInfo[0]));
		}
		public boolean skipIfAbort() {
			return true;
		}
		public boolean skipIfCancel(UserCancelException e){
			return true;
		}
	};
	AsynchClientTask displaySlaveServerInfo = new AsynchClientTask() {
		public String getTaskName() { return "gather slave server info"; }
		public int getTaskType() { return AsynchClientTask.TASKTYPE_SWING_BLOCKING; }
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
		public boolean skipIfAbort() {
			return true;
		}
		public boolean skipIfCancel(UserCancelException e){
			return true;
		}
	};
	AsynchClientTask tasks[] = new AsynchClientTask[] { gatherPrimaryServerInfo, displayPrimaryServerInfo, gatherSlaveServerInfo, displaySlaveServerInfo };
	java.util.Hashtable hash = new java.util.Hashtable() {
		public synchronized Object put(Object key, Object value){
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


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA6FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E165FD8DF8D45535A8EA6C25DEDA530ACAB536E831E21B5A3425EDDA537728175E62B53D243DD8E90BADAD29529A5BD4E3CBB156131901E4A0C000A0D0A3C4C8A0119872874490A47FA698A1C20428A019E4CEB2831319E166CCB209815E5ABFEB4F19B3E7E6C2FA395F7D3E170F45B9FB6D3D76DEFB6D3557DE7B6F0C143FE3664AACC33EA4E52D14785F41ACC90A3DC512FE784434CD08137E3440AC297E
	DE8558A0BDBDEE02E79E44A5DE17E24E132AC6F2A0DC8461999F271837407BA669CF5D36E97092261C86087B097F1E67A2177345AAACA71B06C72C3970DC87D082B872D46019A75DEC5D4A711D9C0F10E5102416F412664B56820ECB04B4A924BC4067273374E570E48C5CD8D74B531D794CECA963BFBC0635A49FD51E8853B8ED82FF96CAAB32FA7ECC4A7AAF4AA721D986F10500A315B7CA3FB59741333E6B42601B7DFDF625FC38DFF6B84AADEE37C3D63CF5F5A7E54F306C6932B8E547C21A4F4BF28E251324
	B88877BEC3F0C3FE1277D6F82FGE8FF86713F99A378D9701E843070992C4F61BD751C5F1E0F3FA3AD2C9AB22D8FD31F34E7D8BD721F8D54476DFC5C300D4AFEE9B511F19744A9GD3816281128152GDE26ED7E072A5760595827B43B0606DC4EC6373B49E1753C63324A8EF82F2B03980EBBA1773BBCD6C9E26D7AF3EB198EBF110051BDF955F4CCA6332B310D63CFBCA0AD7E66ED06EC9D19AC1E7D31411441E4D110911089AB3BD08ED7F6FDC6308E8617FDDAD217BD67361F99D76A141DF2434F0CDDBC9F2B
	2A6C5999242DFFC7F5A0865E899D51CF064F2078B4951E69231762C9B86F3985667167D0B7FA56A1AF1D26FB243C4A5BF565B06321E906E54FB19E243FE8F9A99EA7F9177D051539CF7052CA7153D5F8464B13D4CFC9B893627C4B971819FE256DA77DB363EF10E64582F389C083C0AF40B8C06C8A54316A6AFD935431AE0BC7E972599DD659A3B15A346A1760A97B950F254369D5AC4EFE791445E33774B9E42F4453BCD7CDE45097C437026AFEBF1471166C14BD96456EF2C26E16A1D911BDDE1646D833012471
	48B4365BBEA4C381C3EE8973DDD3C30C2A4362D55EF3DBAD0A9CC84162B75510B699361C029CA183F8E7F259F61048AB816ABF9FE011104327E9FEEF4B9E3096F5F51DAE57851F3B1B54C4AAF91E4879A0359DE9105E74BC4AF8F3AD41B9A09C73BC6673431A0D2AFCDA6C7D04FD0BE7947B58D3FED2779910BE81E09140F200024C85665ACC5423BF76209E352C7D2AD476682D3AFA549273B3E3EFA6539FFF2636BF2D206590DEE7C25CDC00A5G528BE0BFDFC07BBF6A6F556A66730C0F024CC01FA37CE97B5E
	A29D9CC202A60E15B5BD24ACA36FC3EBB4FDDA4FB605ABEB25A64C5A6506B7C89BB7C0BD4FG0C3F00EDA36DAFE43A5EE4714A285D47BD201832B550D20C5F3D63E7384C0D3C1CEC4E5F9A4E77FA1A0E66ABED8658424BA7E53D8230C3E2636EAE00E200BDG25GFB81DE97F2F91B16B39DF8CC82C885487D7B82F38D4099009800F80005G19GC5AFAEB0F782A8GF1G73G52G722594F3BD0095E09A409C0045G262C945E56CF0F39332EEDFD2EE65C787F85186CBBBFA6323FD6F25F773F201E7F9B0175
	793F6CA7721507E52752A23BE52795DE3CBC6E057D249D7878B56092B44683846F06712E9986982F401B8F10F632A80A475E67D364133222581D0301788D87C8D9DEDF1FBBB0D4E2D9DB8F1036F70D40F8A7523FF2000C957DAE27D32646C71070789DF42C129D7290B05D6C72B99506AFBEC87C788B72A89FABCB8F2E1248D8677011F1177946899622DFEE0FFD88CC9A993A2571ACB237B8A6EFD549288CF044476F022795000DBFA33BF07CA99A3BDF1AFB4EED507329E65CFB33E19E501077C57CC9469F4653
	BFEA75F9AF24E3A647FB992FAFB791DED56EBA6DAB1B2424750466244B8178E617D371389C8E59C3C7E56A20D472F45F9D5BA0685531920FB73EC17AD513CFD1DC4E37BCAE1F3B59BEECF75847E4EBA04D1BEF51368346E8D384657F4216EDA2CE3632F25EF1B96D0A4B4375060DBBC9F5244E4BC196390694F391G2BF3DCAD09C71F008FB84D24B234AC089BB71036E99A4DD749375E405AE61D08F786454F8B093FA3A8BED3955FEDA4717FAE624BA05CE25446DF984378B8884FD14533FAFC720DBCAA070BBE
	99DCBDA26837058E463E31D193F70A6BA30B1F7186E9E3074A2F6AB411B2BFBB0E7348B4A80F78AAA64067835482EC17A41E4EF289534D00B071E7924893A015276BD9C952D5DC46F44BCD0C7FBC40935F27E4A55351CC1A6E8FA25DAC8813F1B7891E49GE9G65A23F44AB182EFDD5247C9C92266B05F4E47CA67979D7E9732B4A927508185F93A2DD82240B61794D8F49AF5E006916C54C2F570869246C946ACF107C36E6EB735BED42F4355911727B05C857856908DFC272330564B7F3A5261B1A4D7219194D
	5AB9B11B655715C352F9D6E13A9440937FE699AFBFB3DBDDEEFEB6260B83BA62834D49E1794E4BE165B2FDC995699667B0BD4900A771FF734CEAFDF9A08753951AD9FCADBC8BF93AGFF7FE246F4B100A77E967297E7D673D73A9A53251B19CF16331A653BF5B5630F25FB34C848F9B5CB574553752ED64B6F3EE2CCB7E5F5002F99701E381A15EBDDC352F9ABB1DD72EA961F3A1A71D734C64D5F41AA21D7EB180FB8FFCDA0DFD2AE33494F1612F1022E2D1C378CDB7C655E91986B6A6A0E2A56D392691C080C99
	0BA08F36EE3252C8ECE0DEEE0A398420A19747141F2C45F5136997E6CB3137BFEA8C378E54134B789D4F8D0CA9361F0F98190DDDD5CF4ACC023864DC342F1B6A54F337C62F5755EF2753C6EEFB9E2AD30FB98C77233A55709C30FB3CCA87B885FE34D3BF2EA37C12F9A5C7F3F97CE7FE3594E92DA96CA04557A9AE35296B1F347E12E1CD187AAF61751656866ABF557BD7A363633C35B4A29F1AC1D0707F25DD3B026886FD763ABA3DA1112FABBCBD1AAB31313181787083A42CA57A735A9871BDC8DFE2756A2F46
	FA450E5DAB755EFAC758FAE5707A642F8B542B7674FEA3AB6F33E3D8DEA74447GB03EBFACFAADFAFD0386E3165F036318DF926415814046721D17887F0A4D6EE57E50A2E710BF349E7AA10082B0F5BD529C3DC4F8996272E475FE2631817B5B8377C80959538CFA6B00367BE698E62EE7751D3FBED06F2E54478DD6G466BCD1710572DF9D0FE9E167BE78D2FBFD0343CCE053471GF3854D364BEADED97E6FDF467C97C13A3C8D18F6AE455BC1F6F68BE97B12D2A6738A67CEF8DA2C5612CED55FAFEF6C730227
	5C2FB02DB9D4FA8AE571556DEAD964D8EE541545345F8F9A5AB7B09938B784E4117C686306A50F22ACB6DDC1DE93A1CD3A60F56C8A110545B3D849FC1D8F95352FD332B1455C8D605F08696F390232E3BC0E3F0FBCCEC91EA3ED5D74A9DD9EA91F9B99EFF3B686F81C3174F1C37CD26471B702C753A6305DG8CFF2B04F885F071G0C57FC2F9E2F29026E43AC24DB8E380AFC442FB7A05E86381979185F0FAF6A6537C850BDAA6852GD73499711FB4A27E8C6026EF467C2E387572CB96F447CDC83784F0DBDFC2
	3CF1A5623BGB775A54C2F5225175FDCC177B025A33A3690F039DB304DAAD6113692BE38043E79872BC8FA2F8BA6D52360B67B061CAC1FBBD7E1BEAD10477896349979EF12A71D8FC9E87F0E5BDB61B9A8ABA7399B5FC84BA02B1E9627C6511902D37BF928144614BBAC00BF8993BF6760A1E1831EFF85F52A28E516D41BF1A12CCE4D5942F4E9711600CEA5BE315F187C845A3F4359A84B322DB0975B0AB219150DFA4F424959DA99B17A35B9C89F87348B85FD4358BAC9BB2FC1394E4841BE4272F8CC64113B8D
	66875BB00FB74D241E8E3B178B8F72A92752ED3601D8E4EB09DAF8F42ADD5E29CE5CCAD0252C0C84B33659B864BFE79B3659B2F3DBF07B04D876120E0E524E50B120BCA461A114490955229FC0B9C57F407A5CD294AC53870B487CF1486E949C63584F7279FB1168B71047F4110FDC4C7329E261CF9553FCAC7EB0792494E35D13A18F534B58DF1E9F8E1A77815E8AB075E5AC672B43E44DC8F612D19D476738CA4CAB8E52AD94E90DD5D504A756C86C51A5FB86DC1EA15ECE65169105B7D2232AF5301FACB591BF
	67025D61E0F2AD191848E1B0E0FD621ECD58A748D8D0FF65A35DBEB1F53D4D10DB407AC2CDC120CFD8E19C700B3120378A65BE8552A496E09DFFD4056D4756573EAF760E52F9DE26D7D2449A74D6F8AFD3058930BD15D1EA8F9BA0AE0B4B1261E3A83EDB05E77C2CB1224CED90178C40FCD25F91B2AED7EC87FF9B408AB08E908710349DFD54778E9139C65CE769F0AA6042DA9CA7039610F03F231E547D02BC4A436B6B55B60E6FA9B690BDDA84E5E6G646EG1D87E85901BC5CD69F1507D63FAE8FCC4E4F0E92
	B9DBF7844BB292B03A2CB1C2B7FE95F4CC17CABFD06F9D1BB69CB598F5F409C0BC4FBBE9C7C01718CC26B59119E482FEEBA10C2DG6DG36C21449127A32E8B241BDADAE8CD62799E3131345172EDA960C6E87635176E627968667496896C4250B575071F94647E166FFF4EE417C51FBFCEAFFF49E64138E107FAA4C7DG589CB14F05FB6E8D2F629C71F7BE520F7C900E85187DAA76235FB6133969DC88AF8648G485B19E22E816882D0F6E21B159FEE0A56E60D38C85B6D922F4D5A05D614D16C2549497673D1
	69266D8C26E3F23B94A2B7E26FE7C15A441DA8373FA9A837141DA83776E1A2371CDDB0DF87285D05FBBB67AECF0E7F2128F4DD3B0269D8BB1DEEA56DE403389800B800C40045G4B818A0A301D269CBE942D1D686083ED441DB1ED83B1BE37DD19DC7D76C4252BAF52537D171A366B6B7E890B556EE25EA1362562502E2713BE508D795A0A30ADBFCB5B1270B42588F57D31B6A243C41286A08DA0279846A900F6GC7B14A702751F5DDEC1DCFC4571F1C24AC1F0BCA97D32CC7F727B439726E0FCA9727A90F497A
	EDAF4AFACEB1768FCB5664F8F0C525CB532D73E643644A5B95154E34DB2F4EA68F5639E0B756791713646177D1693AF76B515DEC1CDCF9F7C525F36856795BEE2CF30C2873FB264971509F15AEC9374E661513ABEFCBD43A053AF53639304EE9224E8BC272AA5A93CC2B4F4363A1F4651AF0439EBD3A3BD7CD2E3C2F0550E94B6B5923B7563C57CE6C249B6226814C82188B108AA01500FFDB02F6F2771B0D52446DE4B3605BDD2E8BBAE612735B1CBD3936FDB7AADDCB099E5D261C491577EAD43AB33A65BDBE49
	721E0ACA27681677ED736474E6DE881DD6EF2669165733FAF275EBA80AC6B7CB37BCD371644A5B9815AEC9373C45951313679FC26836EB428BB565B11FE87DC9529FF5FCA2F40738BFB4E3D86B8F25C1FEF92550D7CB519FE2671B89BF7525E8476E291CDCFD92C268347A21146A5915648EE2D726C15CEC0039G0B81245740FF85E8F88D6D4A681B072509793051ED4AC05564F4C409CA577BDAB09DBB3338106E4F79A1EEBAG43FDF7A7414D06F012405DD6C4F0CBA03CDC603ED3CCF085FBE17E35975B680A
	11605AA1EC93381BA8EE9C42F1823741589301D7C22B07EF8F035782AB633508E5E84F85A298A710BF815676170F04BFBBF463917D33C37FFAC47F6C50E70EECE0F848C3FD129663BFF7A47259223B0F04BFDB74D4C3F8BE1FE95067F3F903BE1FFFEF084C672A46F0FC3EC2657BB33A776071B9957B10CC33E676C3763F4A7A51891F13F5A5123BD73B46A27AE76E5BE40DEF6EDE6C07F7B5D6697443D6ABD90144CC3AE50B6778400018841DF44B7D4877742670B23A33C9DFC6F7B7694B683E2648B27ADAD3E4
	99BD291073C8A1673E0E01F7A2615A7C72F1B3CF5325383AAAA53443032DA48F9DBBFC321F6CBC135DF989ED71321039A931450BC12669FB51967F420736B8278C5799F6B9693AA804FBG5CE5582FB73BA2592129E5217D172D273ECF57AD624A58FAEA164077D17CEC951E2D33BE65437B9EF3A0EE6B6B2883E94D615B7059E67DB67CDBB3B9E760F579BC7D32361D24162DA2AEE035BB94F908B485BF7357C268950BE7D08EE967BCCA4F624251EFEBA17B61FDF62B5D43F7EC789A76A5F57B925DE66D71AD51
	9E632FE3FB64AAC49EF3A01C8C107ABA36475F15C86D1179FA3876C822F24FAB5736478FA93E20DC5B9E470631BDCAA0EEDAB92E435FF6996B502A385E2994F2313406EFA7DB2BFEBBB9DB4337132FB5FABBDDEE8D5FCEB736C6EF27CF37066B332C2EDDD756E0FB8DF8DCC350A3D9FB45BBF2F85FCCAF47760A99A272AA5987FE84C04FBEEC2F5BF55B4B3D0F35D38CFF4E5897E8B7465BEF5A424BF3D91B3EBC7F5896DE1E4F35C517675F5B424BF365DB5165396EADFD3D5FAE29F526CA487727D230FC2B78B8
	5F655513D9BC1751DC7EDC2812995F5B4CD217D3AD701D0660F22AF1AE6F51ABE7994FB713BF73AA3465ECB42A4B299178FFB78617D3434BF9502DD7CE914F3716BFBBC34A19E6D21733DF609BD68617339F57F6F5EDF5AF4FD7614F292AF258587470CBC4575CB05C49DE6A5836D8948BAFEB55AAB58F87848F0BD6857150DC497564DCFBF83D9DE85757DBC7FBF83D7536C7575B717670FAFBC3FBF43D1D569E598E0CE5874B7BG479F4DD15B73003E651B55B2BBA870CF1B03E5D645717622607C8FF27C2162
	E0FCAD47EF2AD467CF764B58587F0EC9BD7647C1BB4F83D88610D901E3FF4AB05A2E7CCA34DDDF20F75BBAA1EC85982FC45BF5F0C4CF1F26D7B27D198D4F244A6071067177F3B5FF4D01B162E79D61F563718EFD5DD85A91DEB77E5091DDB71E6D882F9BABBA226B0671E8E45D382BAA380D5E607827E4D22E5BA38F489E0FECBDE99FA3BEDACE95E9A753GE90394105D62CA9CD7D3F612737C8E4B284B27C8D2A2CD573ACB5C49053095E09A40CCGF2470B7906DF9FA1658FF8ACC34096F5FE27D7935A2F96A1
	2D8461FC009A00EEG259A4B7CCF2ADFC0E89F728DD18E99FFF13B89FF0BA15DB211B6DB8E1C37BF29D8941F375352C74F1B31781B87084E301322157CECFA12D1FD46A13F0671DD8BCF262FEBF7EBFDD5925F89713DB5687F653D3DD6ADFF6EAFB3DDAE9CC2DD765720AE7F117A3189901E8F302486F57957BE546565004B5D9F2C37057B4379CD37537CEA76B37F48A870F7D3FC0B8A4F7C26D2A71EDF680638447DB837AEF361FC3B600058439BA64D435EED2A77BF7BA24DF37FD9E0E52C9E3E76E5FCCF483C
	816A5CFDA0D846368361E4BE057A3E638734B23F0962279D504AFC23A24EC1C11CF490E53E5907B2379EC419BF241273A32A776E7F81F98C7D8F1471F50756E6CFBD9810F35C41F0B24F247EEB4241944DFD68B5D41F972A707C7CAACDCF424BA14EDA0BB2FF006EFB26C0F809402D976D10A6F07BA8AE6F0D94F3599B089B74A2ED2D404D77A2ED0B40AD23B89B04E3842E5003343182F71D87E963846EEE8F524E95381F3A11B6D9602AAFA26DA201BBF991F94E94389897526654A16EF7222F678B5CCAB716DB
	DB07675F6EACA76B11FA73FA37432E90D34A6C6451AC2D1DEC01BCECG63F5B82E5FE842B96474BA3405158E52C64990CE85482C43BE706830D6C7728F85742264D0B89D59C44F07579FE23A10AD70BBA83EDB05E7BAF2AF75ABC85881F1A907D0964768DAF0CDBD54239EF15F9CC119F5895CED54D7A1F79EE682EE8E00A4BD7B2EA4611873404EC2F8BA6511C2F1482D45BEA4BB49656F12837BAB4A8A2B76D71432F3C5B3C754670AEAD7DF2FFB3F0D0E8F752C7E390779BCE0739D7C1B94C33BF0CE5C82F163
	07319D1FA8FFD95D0E04113A3AA6973973554986F4BE4F5F154369679C46B6DACA73CC033069CD989B5E44B3767E5DF846BE7ECB77C8B137685F1DD852FD49D073A66333674D40B92898676386D666FF95E11953A1BE79CD22CFD9456AF11948129C1CAEA73AC2632B03624938CF6E9F84626520F8426F1DC1743787452FD451333ABDD20CF54BFCA4C1F2FCABA66C77C1720F70B992FF4626FEAEABFE6F47C6268B1B3A833A304CB091DD6077DC86421CF322AE8D135B33F40D1B5CB1F19F41BE563A9B7D0D05C7
	307D761610764BEDC8B1978154B7E09FAB9E5276316E06C03F32B5046BE3FDAEBA76B5E87B18136227B7E87BD806937BD8BC4465B5E25F798B352F8A04E7885CF15A1F62A13CD0606E9F46FE372C91F93F4BC956E1EB1BB0FC9F8D137BF9AC3C4F299E47C83A7F7EF12CBD280C092E61CF94180F5C6CC23B5A8DF53081CC85188DB03F89657393AA47E5904EEDC65CEBD4E60590EE973898A147B34DA827CFA8C4EE71A29CCB43F3059C37AABDBC7E5A798315340C7FEE79854B31CC1CED1B8FF5D98C1089D05082
	7D21C50C63F44D6C8C045D82771693E5B63D85E5E220F7F3D2A13C9CA03FB545DC81508960E895FBCDB44D54D6F677105CE7A4F7A4493DCBF217134BFA281768AC273F1ABAE1598E95DDE718BCD8D9DF21E5619D7F2BA98B3F8F30D605DB93A16DA6696B34AFAAB0D1E6B67040FB9014E63752FB1CC1E78C75694FDF2473A975C58888333C86CEC9627D132783797E5A9BBD5F82F778FC63FB8279AEF8BF106F8CCF24FCD97A4E8F83342EB3815A239723534EBE9B20FD6423GED26ABBA6D9F4F11744C7FDAD222
	DDD7A17B1E691017298D47066B33F0ECA8E84331E186BBD38EE187C0CC9B6AE697BDAF9257262FD3899A7CBC8833B160DE6A4FADEA531EDDCF207834B66D5975038EBC3BAE3D85BE7ADB586F52282D1A8F614582576D473EA835A31F5B28DFD7866186009E01AF26F8FFBB3B0BC66E1D72FE60D75BECC2232EC719AA5C53FE75B60F1F8D89AAC33B67EB54243F5AB2789D0F1160BE3641C2C4185CCEF461BFC6DAF85A99BAF5C0799BB944C39A821BF95C5C7640BE8BDBEB19B70C7ED9FE877AFE8BBED47B2AF9F7
	B88CC823AD2F24035F2768501E01B77651798660E3BAF0AD25768479FE48A94B79501DD4C0F6F9DCEE59230CB2AC3D494FE56E5D956DAC02FEDBDDB7C93A6FDDB59D6B8727691D1DD99D5ABD2EDE0ACF68506EF17DDE7873C990D7F8944F695674137E9E836119G09GA9G69G39EF23AC2FBBD1A1C5B9A3526DF2F74A4332C335DD4C6A37A86AF9A3ADB03D78C1B67A4DF33E77E5A97193ADE16F5D963E4D742101BFCB16FEAE0BBCD97DFEC43F75670638E9G89G73819683E4087ABDFC025F11A06A5028A8
	16FE9B3DC9ADB66F6D72C8GCD969ED95D166B1E514869E4FC944033822025B3386F74C0582141CD0170F40059AABCAB6368EE2D7C12D4E9D2BA437956DF203EFAEA27F6DD69FE0A4F68542EABD516603A12699D70B55EC1DBD9BF0476B3D960E689DFA67D9D3407B66A33979C03BA83349CC33CC271E70E31BB12646EB1432FF0CEF4FD0864076F8547026B5BF2AC122F3249B551B25436D56BBF06332D4CFE2E9DA65FB39A86E3E26F37B858A105A60B274B6530770FF23BD7C26D9E79165178B1DCE39E98A67A
	E9F3F96CE3AE2792CA09B673FACACB4EFEA681AD334BF307DBA254C5EB17B301AE6FB8334BA4DC846FFFB3C8521EAC2C4302B132D791B8E0413EDF4246FA3B8BEDF645F11CD33F62534E29BF13251E939F77E9675404BE90FF1DCFBB278E267F26CFBB27C6FABE7F75C51739D1485CF69CE55EB8C1191F95B21FFD9CE5FE27107932637AE321D1851910A62FCBA5732E00BD46FD303CD019D7B2191F708919F7214C27C41179B72248BCB50A4CDF9A0EAC73DFBBC277EC7EA24E5F58004FA9G3381123A787DEAAA
	370F1C51F4D5AD3705C03BCCA5374C9039DDBCC177DE686AC5B5477D6EA3A2CB756D15F617D7E1F14F8FE8633AC75DB20B7BD21BB66EBD2F6CE1F17FA8592809E3BBC148475EA6ADED0BC51159DE50DBCE6DDE9011C74E3B68734CBF2BDE93AA5CFBDCF7CDE87959B1C3413BFCECFBB76073AC77969A191EECAD45BBD44A3B68075FC26F6847C3F89E4062F7515E7ECB48DD0F0CF783FA1CF3A25CB812CB6F46971C500EA3AFD1FC59896DB872F9F1873F9E62E61E906B5D5904D753C970BDCEA26E9B59B80E5488
	DC7285020B01F05CC961EB5379F4820467895C37A9AE1D5FADE338438EE26FC91A0958FCBDC808C04B4AB8C54B88F77EF2A290E9FD0269563732C27B5A66916CEB0B0047F4002D5DA96632EE3E9E5ACD3FEF65AF0C10F78BCF4B6F00FD3CCA74A3ACF7B3759DD5A76D783DEF63DEE2CF78C968374555E9893F4B55B32A55F752A6B33BF13E787C884E9767GEE0744569AF324409F1BFB3CB6C85ED99F79CDAE7611022FFCD1EAF92C4530CB270F90C863752AFFAF50C7F215C2A3637D1743245C810B439BB8FB39
	5DC6CA607716AD641B033A7384707C0EDA06AD477B4E4B7DCA37E5D050647411F244BEAD1BBE603D65BEA28B714D917A199E76DD8526BFE749A71D713C346A7DCF3B2E7DFED9CE5135AC03557FF0FF78F38FAD7D7A679E0E766B1FC36D6A0FFCE63827BF7C1961F37D6A6F9132F65FEBA7ED23001E44824C81188F3078BD1C87BC5DCF3E8B43EE57EA67G2D56C1F26E02FC85D84172D32C616B7A18B57A370317D849B71A7ADC502D06C87E08FF42CA5ACBF139A9121F01369299F96C03B605A56578BFDB69B7FA
	65810AE6B6749FF4FFECB954AB67945A55AB94178F61B201DBF51A609AA05CA3F01F207D528161D8017BA5454D0270DC012B24FB0B8BA11CAAF0B75408F3768277A245951DCEB1571FC65CFD746CFD97045D82E7217B8DB1901EA1F0CB76E0FEC92751465FDBC256AB16097057E838C56C779DAC192C0E13BCA7560F3AA6DD46C45626193D17074419169EEE6F61D9562336778FBA8BA364534253B2BB3834A160B761F79FBF3FB3F00E854FE79D7EC8B386706F915D67525AF8FA8F3887FD6BA343646C137AC3A9
	2ABC4ACF351E686EE8EE6CBCF7327944714E4E264693679ABBCFB73EFFD2707739616A88746D47CFF4FCF07CD837DE8E2163C97A91BD9C9BE38C97A4510E99914F97079F1F7F71CF56D519F4DD59F81AAC4E3559FA744653C875AFF6846AFF5AF73575E7BE5E76A1DCCB1A8965AF6A413D616A2A20BBD4ECEF384D619A61BB43F816E9E08867B7F96F235FBA1D6659CE42G7E77757D04703EC37B10A4723F6CC479443E0F6BDCBB69BE4AFC88278264FCGFEA8C08DC06787E21DCBAE11422F9932CA50B140E331
	BB95A643D7CAA349307703F0FEF5B67DA6167201F6FDFBA345CF7DC03B3E7DB989573763A0CE7A90ED646733F0FD46AAF0EB0CE8B727FE08F6F123095849B9A2DCC043643E848B4F22EBD9A47E5A799F5FB7DE4B7DB95637AEE713B159EA9F361393892EFE761DC4B62F3F1D4EB75EB7401C77C31C1FBE1C0D735345BAF5D74FBD8924C39A5319407CF46B996D7CF4698532CE6972584197D0177D7C85547922B3E8B333B6866C6FFECE1FDE9A409D60EDFFF8847BDB8F50461E417ED6599BF42606753737BCF6EB
	13E510E9ABBFAFB302E54FBF037D6D1BB4CF69AC0CABG95E7112729FEB2AFE0BE897A626773543E786251D386DCDF504A2B7DACBFFFFDB6600B2F937D7266D17C1EE67CD954BB492F6EBF2900DFFEB6328E327E7CE397E9375C0FC06781BA812CGFE00D800D9G09G49G29G19GF967D24CC5G35G6DGE7G5C67306F3B8649390DF0FD1F8BB55079A5743F2A6D61FD40BAFBF49F3059CE3F5D893E9BD917E038F6BBE983574000D7963E66BB76BC016B70F6393CF622DC2C4DAACD41FBB877DF1404BE5E
	E62267DEADFE4D39578728BF9883750FBD07ED7F4AE97A4DD017C79E7438FCE4D313255DCF7D44E4C837C8243DCBA2F58B2C25F5C369489552A5CA28F74B855DB3D2783342EB28ADAB69853B5C0B695BBDA64A13D3E1E9BE22FEE7AF44BBG6675526FC25794F25D086B8D551D04DE359F7452FE7DF340657BC93E3996688F96AC7B7F94EF8A2AE32343BE68A43382CE3328986B57A0E87C3BC3676EB70B333FEE8B73652642B34E4276D6D99D7E3A27D08A2F73F3AC6ABAF40F13BA84CE4ED5F2BB71A9B1AE4F07
	74D29F4E3F9F9563EDED9F76FFA3BDCB630670D40019FD487FA22AD3B07760FD76D7AE6C3309FD58E7FF92F426AE9D70267E48FD165195051C931A5838D1B5C93A8BD7CD4746609CAA1B2D7D5A3D358D94DF542F5DDB3BC3483D82622677231CEEF06A153F1C4BAA579AAAB3565777BA435B14AAE7F41BF24829BF2FECF4064EAB5B1C7A734ACEE7683C3226947D0AC2AB56F1AD6D33EE8847885CD60AA377B1E6885C97242B1DB339F5649323038B34F762D51721670B04DB5BE3FDB9D91CCFCD32323E3C901E4B
	2C6AF959CEE3A1CFA349217C664B6A3EDCD2915C17F19E76C0B6F9D6D7D470FE38A29BFD5AA279EAFD7EE432840277399CE43DABF86E01E52B26AEDD1D9D5DC22E4D3AF7E322773FF7A7C9F753D553317E39071E41BDA3EB7BED85455BE4ED3F7D2E702B9408DBA463DC616BDE52FE7583A9669E00F10069G33816682AC84C89BC03F61F1E724F386C234AA4F0155F17A28DE9DF386795EB1FFD64033053F33BE38051EDD6999E4F5E138820AF3F39C3FD3690AF02752955D067C5925EFC37E6A8A35A119AEFD9B
	E2F02BED8843E53B098E8F02BF55A352658A5C7B823741ED663853F62BE29378AD6E559C5FAE138245EF0D3889DFA3945743F1BBD53877B9EE0F1B0C4FD0E5985611364CCDFD8C3B43414295B40D5D892E161047FE4A235DEDD171DDA7F004EF5657AC3EC0DF7B6B29F2A96CB95D2A4A1A03B5355587EA8E1695865ECBB11F65A301FCAECB9BF91F25F9B43B9CAECFB9B8CFE27F167EFE0E3ACF1FA47F63F225373C3975D8F76B8941631E51C05E3BBDBBA270D8CB4E92D714F23AAB1783F4A3B5BB35F4ED60A415
	6C345AAD8E57604E3242CA78D7D30A3E7FC3B5E83F268D222F312206040F39BC34FD79EF33486A39C6410E0630F30DF93CDF248F864FB5982FCFDE8970FA977D0D01B0F5049AD61432F4FFAA1E289C197D784EEE349FC5B618CF58505FFAF137B807EEC3FF6B75BD74FE8204D381B2EDA8037C9222EB4DB279B6AA5A3FDBA6696F5CBDC93ADFCC52CF9A217ED03EDDEBEF27D0FC21DDEBEF4DCE14D79944455A51B7D82AFB17B7434E5AB567BC3709702CB82F36054B06C3ED615343EADB4872DF6714447B96557B
	4C21EB3F67725DEBDA862B739DCE3C1753F2DE2C3B0BB3BADD67F12D636DD2DC6BD04EC7DF6B188AE9624E8756BA924EEB57BAFE7CFAE02D827762BF5B9F405570B3254F04FCB3092C57A7C1FEA967711B6999D9289B4BGD7F8817BD23D817BD24F856CCB6769B7D8E2A1BC9BE02EC8FB5408B6E651856CDF579B09EDA7E390EB0F97033EDF1667C8B197B9A6B2F7F95D9F501B832A777FBBF26DF5E851B5AD030DE5C95E707E423FF9237B8B49DEFDFF61275ED0FF61B12F3E3F7038B754DF78AD4D57A38F022D
	E697E2398F63A56B88EC1E7D0E45E999247BFCCC773FC25719EA21DD5B9D589F724630BFF48B5CC3747E5EAC884F9738AD746C4DE288E7BAF01D4DC26DBC5F53256E88439778578670680E70B67BD1CD101E8D011E8DCDE4DDAB9946B1609D52F6837881E281E681A48124GAC9BC25F735FAEC7DA2FE61732346B555F8FB9CFB8B19DFCE412F426BD139BDB6E26632549299DDB3ECC71F9CE6D58D23D876DC78144CDF16258F249A989DEBE21FADFBF617307136F37A557348C262FAB687ACCAA54B74309BAFC4E
	AD4E373B44F751687AF397049501DBC74F50CC0570EC975EB91CEAA0B2E2F787DE0AF8F700595C71D23439C92E2B1D17FE0518E81E4FEB620CC8068B6DFCBDBB136F067182600CDB1C9D0D7A5DDA7D7648097A6DD7FD3A462B26E33A7AABBACEA9EE2D8EBFC971D35CDA9DF69BD00727C1DC3A286F7120EF48F6DFCCB15BAEC636A52CFDDF4A89750FDE4ED17BC7EC0CFCE8B4F87D380CDE23E0BE431EB1749946F54A547A8C5ABBG319735BE434F15686B46998A0E79F197F19CAF77C5DE0BED97E7A613AF22FE
	7F2A866B3F590475DFDA0375A76B7DFCBC12420FFD3F12220FFDCFCA7AE35F53D26858770C24BF76AD17C2473E97F83E9E7171B47E1D0EAC3D310F5FDF5CA3097784557B9BFB2E3D3DBBF1CD4BE0FA30419D2C2B47D785F4F5589D6D2CE9B8DDA58A1F99222BA997226B6AD3E2DFAF47033ADA6C082C2BC79CE2BD5E03FD7C3A20FDE49B6047BD9371CB77052C15452A68E66964416FD1D15B1A68516EBFBFC871499E6D7EF353A86EBFAF013896AF721DEA886858532A77E1433557371B2FE9994CDE8C9851061C
	95388BC634A19D869C3FFA46F07C1A6D453114DE01613A3446B7513585167E5C04533332EE4D8914E5F0DCDDD927A61C1E55FFC6E96838F2D729FADCE1383691BC1B3044AB3E19A16E5E1994F116177A6B9590EE97389FD05C99880F8BDC7BE88FCFB759B6ED0FC04B4A300C7E33E794A21D8D66E7C04754FDB68EF8CA08D2A736776092E70067A9EC6FE1B1F9AA6A3D079F0AB340B93E50BC8BFC6A3D073C8AB23E9CA53FE5C58E3E0B336D3F78B8F0D62C0A1F71FDB6680CEF050F6D6DF47B44EF408DEB4F0711
	F81B8F5BDD998B551951B1354EB0BBF33BD8139F875AB9BE1C5B7FDA1CE1C87321FF38162E6B978D27186B813A07B16D53FE6C0B0EE1E113C7C9584367E8EC7E76C8A939C72277ED8410887A25E3F846FE16487F6DE15C93DDBCAC3EA3B0493D0E2FCC12AEF512FBA457D119650FE8FD521BA83EF0C46B13EE720BB5BB080B9D417ABE93F4EEA38370B97E890DD7E31178AE5049031F398C790E8B55C31E3E05BFFB6069606F7C5E03EA2DA6D6753E4DFF6D47272AEBDA866BC366D16CD7DB84EE5DA8762BAD426F
	ED936B9FD37C62CE8655639488AF9138E471ED07B4017B866DFB7523506FC69117CD7B1882616923383EF8D64C95E60FC63FA31584E9D2C6836B0B2923DA7F6B72F0302F17C83788D8D9CB45FD2C74B0E5C51A17184634E5BDBC925D57CB95B6A8EF8C6D4AF6FFE4DF2F56AF4E080C217D783470955CE3D7BBE77EA5B1595CB6650BB9736CB174B9EE20473A785D27112B73830E7BA71A1E6FD3847565A540C34658C4EC4036A05B1573F10A396063094CE73F2CB306FCEDAC546F289889CD37FFC41DEE8B2D6F1E
	318DB0FB08FFDADA6975C89BC377069ABF7D7347624F35649BA4C307344AC69778BA2415A3E485506F06ACD6C81261A66FC5096E0B3110A219CFAA968F792C289C7C3E0ADDA0D33D184828AB9EB9C7DDFD056490D8502B09DCE5930FB56476CDA13BFE83BC2F97BCF7504D4AA54355F54625DFB49AA493B04CA6249D12094CAA01EF8F635F8893D24A3DC98D34966B7E992CA295B0468AD97D6A8A71BB1320C865659132122E3C78620B8420CA6B0237B9336E5DF2C35E4D55BE031453A62D9A30B85CB68BD4057E
	AEE02734260F56092CF2BA94A90F7E1469BE205DC7E98FF5CA06FE90734B81FCBF41335813EECB3F8CC94CD60B67824D01303265C349BC60996C2385A8126143368F21D89AA2515BBA25550A0D6C66932B24C8DB82D9F711B5D69EF1084AD7FA71C550B87AA7F4841C242C2DD04D35C13B26BF3CFB7F93493FBA65B5CA2BBA24ED64309BC8127DE63115FC1F769434B668E31BE4F63B2094F2EC8D1A55CB9FAB3D4A2883E411BFF0C23E68133D6427DF71468CD4FEB3635027589D7B5AADDE1BC2D6AE205AA6A779
	2D98E9A30BA6BF7F3A0F7F7CAB65BD2B311772CB0DA9703B0972CBF2A99739ECFAF0FD712BC7D3930D1221CDDA6DF0D92CED16FE72031912D931F9E42F4D65307A5DC79AE8C6B9347F2D21ED6E11DE9655A715ADA746D6B0FD41FF5734354B563641544A6F11765114C42865A2053559E6A1BF54ACFBBAGACD0E411083BEE3B327B47EFEDB9E31032F8A50968F7785D8AD11FA13BE394DE589EB5ED14DD90E4A784C4FF223F055B8675D1FE717443777C32BEBD8DB2EB63CA87AC6585D29D738D75491ECF445F7A
	4A1A69CB7E15F06CA655451C4D0A476274129FA7054C8BB5F546A841FA79AD8FD75CFA7F0A8FA86B81D2C55A915032C159B5342FF1E0406E14C58650E2107DABA2FB1226DC13E6237FB31B0FBE7AE29FE8D80BE49C2A28207FD7527F2B487F1594D3C9B115D550EC69D244BFED3C9F6BCC551286DEA3BA64A7EFFB7C228350BFE298DD9E2530E9342EB01CB14966D6CE11561F348F799C74B0F5A198C66F55992515F4EAF3B5B41278FD4EAB6AEF13AC3F31BF6CF774B22F635F4F3EDE7BED1254CBC46FBB81EF3B
	9E77EDEAAE11FB69616E9C1E9C32B89C60C9DCE8F4DA497D4326D1C58E5CF97B7DA5123F975370C89617CE3F39FFDEC5737FGD0CB87881688E2A4D9ADGG3813GGD0CB818294G94G88G88GA6FBB0B61688E2A4D9ADGG3813GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG13ADGGGG
**end of data**/
}
}