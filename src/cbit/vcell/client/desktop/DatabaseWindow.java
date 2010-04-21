package cbit.vcell.client.desktop;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.UIManager;

import org.vcell.util.BeanUtils;

import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.server.ConnectionStatus;
/**
 * Insert the type's description here.
 * Creation date: (5/13/2004 12:29:37 PM)
 * @author: Ion Moraru
 */
public class DatabaseWindow extends JFrame implements TopLevelWindow {
	private JMenuBar ivjDatabaseWindowJMenuBar = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JCheckBoxMenuItem ivjJCheckBoxMenuItem1 = null;
	private JPanel ivjJFrameContentPane = null;
	private JMenu ivjJMenu1 = null;
	private JMenu menuServer = null;
	private JMenu ivjJMenu2 = null;
	private JMenu ivjJMenu3 = null;
	private JMenu ivjJMenu4 = null;
	private JMenu ivjJMenu5 = null;
	private JMenuItem ivjJMenuItem1 = null;
	private JMenuItem ivjJMenuItem2 = null;
	private JMenuItem ivjJMenuItem5 = null;
	private JMenuItem ivjJMenuItem6 = null;
	private JMenuItem ivjJMenuItem7 = null;
	private JMenuItem ivjJMenuItem8 = null;
	private JSeparator ivjJSeparator1 = null;
	private JMenuItem ivjJMenuItem9 = null;
	private JSeparator ivjJSeparator3 = null;
	private JPanel ivjJPanelMemStatus = null;
	private JProgressBar ivjJProgressBarConnection = null;
	private JProgressBar ivjJProgressBarMemory = null;
	private JPanel ivjStatusBarPane = null;
	private JLabel ivjStatusMsgConnection = null;
	private JLabel ivjStatusMsgMemory = null;
	private DatabaseWindowManager fieldDatabaseWindowManager = null;
	private JDialog compareDialog = null;
	private JMenuItem ivjJMenuItem11 = null;
	private JSeparator ivjJSeparator4 = null;
	private JCheckBoxMenuItem ivjStatusbarMenuItem = null;
	private JMenuItem ivjACLMenuItem = null;
	private boolean ivjConnPtoP1Aligning = false;
	private DatabaseWindowManager ivjdatabaseWindowManager1 = null;
	private DatabaseWindowPanel ivjdatabaseWindowPanel = null;
	private JMenuItem menuItemReconnect = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DatabaseWindow.this.getJMenuItem1()) 
				connEtoC1(e);
			if (e.getSource() == DatabaseWindow.this.getJMenuItem11()) 
				connEtoC2(e);
			if (e.getSource() == DatabaseWindow.this.getJMenuItem2()) 
				connEtoC3(e);
			if (e.getSource() == DatabaseWindow.this.getJMenuItem9()) 
				connEtoC4(e);
			if (e.getSource() == DatabaseWindow.this.getJMenuItem6()) 
				connEtoC5(e);
			if (e.getSource() == DatabaseWindow.this.getJMenuItem7()) 
				connEtoC6(e);
			if (e.getSource() == DatabaseWindow.this.getJMenuItem8()) 
				connEtoC8(e);
			if (e.getSource() == DatabaseWindow.this.getACLMenuItem()) 
				connEtoC10(e);
			if (e.getSource() == DatabaseWindow.this.getMenuItemReconnect()) 
				reconnect(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == DatabaseWindow.this.getJCheckBoxMenuItem1()) 
				connEtoC9(e);
			if (e.getSource() == DatabaseWindow.this.getStatusbarMenuItem()) 
				connEtoC7(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == DatabaseWindow.this && (evt.getPropertyName().equals("databaseWindowManager"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == DatabaseWindow.this.getDatabaseWindowPanel() && (evt.getPropertyName().equals("selectedDocumentInfo"))) 
				connEtoC12(evt);
		};
	};

public DatabaseWindow() {
	super();
	initialize();
}

/**
 * Comment
 */
private void accessPermissions() {
	getDatabaseWindowManager().accessPermissions();
}

/**
 * Comment
 */
private void compareLatest() {
	getDatabaseWindowManager().compareLatestEdition();
}

/**
 * Comment
 */
private void compareOther() {
	getDatabaseWindowManager().compareAnotherEdition();
}


/**
 * Comment
 */
private void comparePrevious() {
	getDatabaseWindowManager().comparePreviousEdition();
}


/**
 * connEtoC1:  (JMenuItem1.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseWindow.openSelected()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.openSelected();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC10:  (JMenuItem4.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseWindow.accessPermissions(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.accessPermissions();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC12:  (databaseWindowPanel1.selectedDocumentInfo --> DatabaseWindow.updateAnnotationACLMenuItems()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateACLMenuItems();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC2:  (JMenuItem11.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseWindow.exitApplication()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.exitApplication();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JMenuItem2.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseWindow.openLatest()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.openLatest();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JMenuItem9.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseWindow.export()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.export();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (JMenuItem6.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseWindow.compareLatest()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.compareLatest();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (JMenuItem7.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseWindow.comparePrevious()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.comparePrevious();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (StatusbarMenuItem.item.itemStateChanged(java.awt.event.ItemEvent) --> DatabaseWindow.viewStatusBar()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.viewStatusBar();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (JMenuItem8.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseWindow.compareOther()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.compareOther();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (JCheckBoxMenuItem1.item.itemStateChanged(java.awt.event.ItemEvent) --> DatabaseWindow.viewLatestOnly()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.viewLatestOnly();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (databaseWindowManager1.this --> databaseWindowPanel1.this)
 * @param value cbit.vcell.client.DatabaseWindowManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(DatabaseWindowManager value) {
	try {
		// user code begin {1}
		// user code end
		if ((getdatabaseWindowManager1() != null)) {
			getDatabaseWindowPanel().setDatabaseWindowManager(getdatabaseWindowManager1());
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
 * connPtoP1SetSource:  (DatabaseWindow.databaseWindowManager <--> databaseWindowManager1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getdatabaseWindowManager1() != null)) {
				this.setDatabaseWindowManager(getdatabaseWindowManager1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (DatabaseWindow.databaseWindowManager <--> databaseWindowManager1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setdatabaseWindowManager1(this.getDatabaseWindowManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
private void exitApplication() {
	getDatabaseWindowManager().exitApplication();
}


/**
 * Comment
 */
private void export() {
	getDatabaseWindowManager().exportDocument();
}


/**
 * Return the JMenuItem4 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getACLMenuItem() {
	if (ivjACLMenuItem == null) {
		try {
			ivjACLMenuItem = new javax.swing.JMenuItem();
			ivjACLMenuItem.setName("ACLMenuItem");
			ivjACLMenuItem.setText("Permissions...");
			ivjACLMenuItem.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjACLMenuItem;
}

/**
 * Comment
 */
private JDialog getCompareDialog() {
	if (compareDialog == null) {
		compareDialog = new JDialog(this,  "Comparing Selected Documents ...", false);
		compareDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		WindowAdapter listener = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (e.getSource() == DatabaseWindow.this) {
					compareDialog.dispose();
				}
			}
		};
		DatabaseWindow.this.addWindowListener(listener);
	}
	return compareDialog; 
}


/**
 * Return the DatabaseWindowJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuBar getDatabaseWindowJMenuBar() {
	if (ivjDatabaseWindowJMenuBar == null) {
		try {
			ivjDatabaseWindowJMenuBar = new javax.swing.JMenuBar();
			ivjDatabaseWindowJMenuBar.setName("DatabaseWindowJMenuBar");
			ivjDatabaseWindowJMenuBar.add(getJMenu1());
			ivjDatabaseWindowJMenuBar.add(getJMenu2());
			ivjDatabaseWindowJMenuBar.add(getMenuServer());
			ivjDatabaseWindowJMenuBar.add(getJMenu3());
			ivjDatabaseWindowJMenuBar.add(getJMenu4());
			ivjDatabaseWindowJMenuBar.add(getJMenu5());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDatabaseWindowJMenuBar;
}


/**
 * Gets the databaseWindowManager property (cbit.vcell.client.desktop.DatabaseWindowManager) value.
 * @return The databaseWindowManager property value.
 * @see #setDatabaseWindowManager
 */
public DatabaseWindowManager getDatabaseWindowManager() {
	return fieldDatabaseWindowManager;
}


/**
 * Return the databaseWindowManager1 property value.
 * @return cbit.vcell.client.DatabaseWindowManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DatabaseWindowManager getdatabaseWindowManager1() {
	// user code begin {1}
	// user code end
	return ivjdatabaseWindowManager1;
}

/**
 * Return the JCheckBoxMenuItem1 property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBoxMenuItem getJCheckBoxMenuItem1() {
	if (ivjJCheckBoxMenuItem1 == null) {
		try {
			ivjJCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
			ivjJCheckBoxMenuItem1.setName("JCheckBoxMenuItem1");
			ivjJCheckBoxMenuItem1.setText("Latest Version Only");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxMenuItem1;
}

/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new java.awt.BorderLayout());
			ivjJFrameContentPane.add(getStatusBarPane(), BorderLayout.SOUTH);
			ivjJFrameContentPane.add(getDatabaseWindowPanel(), BorderLayout.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}

public DatabaseWindowPanel getDatabaseWindowPanel() {
	if (ivjdatabaseWindowPanel == null) {
		ivjdatabaseWindowPanel = new DatabaseWindowPanel();
		ivjdatabaseWindowPanel.addPropertyChangeListener(ivjEventHandler);
	}
	return ivjdatabaseWindowPanel;
}

/**
 * Return the JMenu1 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenu1() {
	if (ivjJMenu1 == null) {
		try {
			ivjJMenu1 = new javax.swing.JMenu();
			ivjJMenu1.setName("JMenu1");
			ivjJMenu1.setText("File");
			ivjJMenu1.add(getJMenuItem2());
			ivjJMenu1.add(getJMenuItem1());
			ivjJMenu1.add(getJSeparator3());
			ivjJMenu1.add(getJMenuItem9());
			ivjJMenu1.add(getJSeparator4());
			ivjJMenu1.add(getJMenuItem11());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenu1;
}

private javax.swing.JMenu getMenuServer() {
	if (menuServer == null) {
		try {
			menuServer = new javax.swing.JMenu();
			menuServer.setText("Server");
			menuServer.add(getMenuItemReconnect());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return menuServer;
}
/**
 * Return the JMenu2 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenu2() {
	if (ivjJMenu2 == null) {
		try {
			ivjJMenu2 = new javax.swing.JMenu();
			ivjJMenu2.setName("JMenu2");
			ivjJMenu2.setText("Edit");
			ivjJMenu2.add(getACLMenuItem());
			ivjJMenu2.add(getJSeparator1());
			ivjJMenu2.add(getJMenuItem5());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenu2;
}

/**
 * Return the JMenu3 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenu3() {
	if (ivjJMenu3 == null) {
		try {
			ivjJMenu3 = new javax.swing.JMenu();
			ivjJMenu3.setName("JMenu3");
			ivjJMenu3.setText("Compare With");
			ivjJMenu3.add(getJMenuItem6());
			ivjJMenu3.add(getJMenuItem7());
			ivjJMenu3.add(getJMenuItem8());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenu3;
}

/**
 * Return the JMenu4 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenu4() {
	if (ivjJMenu4 == null) {
		try {
			ivjJMenu4 = new javax.swing.JMenu();
			ivjJMenu4.setName("JMenu4");
			ivjJMenu4.setText("View");
			ivjJMenu4.add(getStatusbarMenuItem());
			ivjJMenu4.add(getJCheckBoxMenuItem1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenu4;
}

/**
 * Return the JMenu5 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenu5() {
	if (ivjJMenu5 == null) {
		try {
			ivjJMenu5 = new javax.swing.JMenu();
			ivjJMenu5.setName("JMenu5");
			ivjJMenu5.setText("Search");
			ivjJMenu5.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenu5;
}


/**
 * Return the JMenuItem1 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItem1() {
	if (ivjJMenuItem1 == null) {
		try {
			ivjJMenuItem1 = new javax.swing.JMenuItem();
			ivjJMenuItem1.setName("JMenuItem1");
			ivjJMenuItem1.setText("Open Selected Edition");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItem1;
}

/**
 * Return the JMenuItem11 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItem11() {
	if (ivjJMenuItem11 == null) {
		try {
			ivjJMenuItem11 = new javax.swing.JMenuItem();
			ivjJMenuItem11.setName("JMenuItem11");
			ivjJMenuItem11.setText("Exit");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItem11;
}


/**
 * Return the JMenuItem2 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItem2() {
	if (ivjJMenuItem2 == null) {
		try {
			ivjJMenuItem2 = new javax.swing.JMenuItem();
			ivjJMenuItem2.setName("JMenuItem2");
			ivjJMenuItem2.setText("Open Latest Edition");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItem2;
}

/**
 * Return the JMenuItem5 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItem5() {
	if (ivjJMenuItem5 == null) {
		try {
			ivjJMenuItem5 = new javax.swing.JMenuItem();
			ivjJMenuItem5.setName("JMenuItem5");
			ivjJMenuItem5.setText("Publish");
			ivjJMenuItem5.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItem5;
}


/**
 * Return the JMenuItem6 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItem6() {
	if (ivjJMenuItem6 == null) {
		try {
			ivjJMenuItem6 = new javax.swing.JMenuItem();
			ivjJMenuItem6.setName("JMenuItem6");
			ivjJMenuItem6.setText("Latest Edition");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItem6;
}

/**
 * Return the JMenuItem7 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItem7() {
	if (ivjJMenuItem7 == null) {
		try {
			ivjJMenuItem7 = new javax.swing.JMenuItem();
			ivjJMenuItem7.setName("JMenuItem7");
			ivjJMenuItem7.setText("Previous Edition");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItem7;
}

/**
 * Return the JMenuItem8 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItem8() {
	if (ivjJMenuItem8 == null) {
		try {
			ivjJMenuItem8 = new javax.swing.JMenuItem();
			ivjJMenuItem8.setName("JMenuItem8");
			ivjJMenuItem8.setText("Other...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItem8;
}

/**
 * Return the JMenuItem9 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItem9() {
	if (ivjJMenuItem9 == null) {
		try {
			ivjJMenuItem9 = new javax.swing.JMenuItem();
			ivjJMenuItem9.setName("JMenuItem9");
			ivjJMenuItem9.setText("Export");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItem9;
}

private javax.swing.JMenuItem getMenuItemReconnect() {
	if (menuItemReconnect == null) {
		try {
			menuItemReconnect = new javax.swing.JMenuItem();
			menuItemReconnect.setText("Reconnect");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return menuItemReconnect;
}

/**
 * Return the JPanelMemStatus property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelMemStatus() {
	if (ivjJPanelMemStatus == null) {
		try {
			ivjJPanelMemStatus = new javax.swing.JPanel();
			ivjJPanelMemStatus.setName("JPanelMemStatus");
			ivjJPanelMemStatus.setPreferredSize(new java.awt.Dimension(300, 26));
			ivjJPanelMemStatus.setLayout(new java.awt.GridBagLayout());
			ivjJPanelMemStatus.setMinimumSize(new java.awt.Dimension(200, 26));

			java.awt.GridBagConstraints constraintsStatusMsgMemory = new java.awt.GridBagConstraints();
			constraintsStatusMsgMemory.gridx = 1; constraintsStatusMsgMemory.gridy = 0;
			constraintsStatusMsgMemory.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStatusMsgMemory.weightx = 1.0;
			constraintsStatusMsgMemory.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelMemStatus().add(getStatusMsgMemory(), constraintsStatusMsgMemory);

			java.awt.GridBagConstraints constraintsJProgressBarMemory = new java.awt.GridBagConstraints();
			constraintsJProgressBarMemory.gridx = 0; constraintsJProgressBarMemory.gridy = 0;
			constraintsJProgressBarMemory.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJProgressBarMemory.weightx = 2.0;
			constraintsJProgressBarMemory.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelMemStatus().add(getJProgressBarMemory(), constraintsJProgressBarMemory);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelMemStatus;
}


/**
 * Return the JProgressBarConnection property value.
 * @return javax.swing.JProgressBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JProgressBar getJProgressBarConnection() {
	if (ivjJProgressBarConnection == null) {
		try {
			ivjJProgressBarConnection = new javax.swing.JProgressBar();
			ivjJProgressBarConnection.setName("JProgressBarConnection");
			ivjJProgressBarConnection.setStringPainted(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJProgressBarConnection;
}


/**
 * Return the JProgressBarMemory property value.
 * @return javax.swing.JProgressBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JProgressBar getJProgressBarMemory() {
	if (ivjJProgressBarMemory == null) {
		try {
			ivjJProgressBarMemory = new javax.swing.JProgressBar();
			ivjJProgressBarMemory.setName("JProgressBarMemory");
			ivjJProgressBarMemory.setMinimum(0);
			ivjJProgressBarMemory.setStringPainted(true);
			ivjJProgressBarMemory.setValue(50);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJProgressBarMemory;
}


/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator1() {
	if (ivjJSeparator1 == null) {
		try {
			ivjJSeparator1 = new javax.swing.JSeparator();
			ivjJSeparator1.setName("JSeparator1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator1;
}


/**
 * Return the JSeparator3 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator3() {
	if (ivjJSeparator3 == null) {
		try {
			ivjJSeparator3 = new javax.swing.JSeparator();
			ivjJSeparator3.setName("JSeparator3");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator3;
}


/**
 * Return the JSeparator4 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator4() {
	if (ivjJSeparator4 == null) {
		try {
			ivjJSeparator4 = new javax.swing.JSeparator();
			ivjJSeparator4.setName("JSeparator4");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator4;
}


/**
 * Return the StatusbarMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBoxMenuItem getStatusbarMenuItem() {
	if (ivjStatusbarMenuItem == null) {
		try {
			ivjStatusbarMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjStatusbarMenuItem.setName("StatusbarMenuItem");
			ivjStatusbarMenuItem.setSelected(true);
			ivjStatusbarMenuItem.setText("Status Bar");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusbarMenuItem;
}


/**
 * Return the StatusBarPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getStatusBarPane() {
	if (ivjStatusBarPane == null) {
		try {
			ivjStatusBarPane = new javax.swing.JPanel();
			ivjStatusBarPane.setName("StatusBarPane");
			ivjStatusBarPane.setLayout(new java.awt.BorderLayout());
			ivjStatusBarPane.add(getStatusMsgConnection(), "Center");
			ivjStatusBarPane.add(getJPanelMemStatus(), "East");
			ivjStatusBarPane.add(getJProgressBarConnection(), "West");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusBarPane;
}


/**
 * Return the StatusMsgConnection property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStatusMsgConnection() {
	if (ivjStatusMsgConnection == null) {
		try {
			ivjStatusMsgConnection = new javax.swing.JLabel();
			ivjStatusMsgConnection.setName("StatusMsgConnection");
			ivjStatusMsgConnection.setBorder(new javax.swing.border.EtchedBorder());
			ivjStatusMsgConnection.setText("Not connected");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusMsgConnection;
}


/**
 * Return the StatusMsgMemory property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStatusMsgMemory() {
	if (ivjStatusMsgMemory == null) {
		try {
			ivjStatusMsgMemory = new javax.swing.JLabel();
			ivjStatusMsgMemory.setName("StatusMsgMemory");
			ivjStatusMsgMemory.setText(" Java Memory used: 50MB / 100MB");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusMsgMemory;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 3:39:15 PM)
 * @return cbit.vcell.client.desktop.TopLevelWindowManager
 */
public TopLevelWindowManager getTopLevelWindowManager() {
	return getDatabaseWindowManager();
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
	getJMenuItem1().addActionListener(ivjEventHandler);
	getJMenuItem11().addActionListener(ivjEventHandler);
	getJMenuItem2().addActionListener(ivjEventHandler);
	getJMenuItem9().addActionListener(ivjEventHandler);
	getMenuItemReconnect().addActionListener(ivjEventHandler);
	getJMenuItem6().addActionListener(ivjEventHandler);
	getJMenuItem7().addActionListener(ivjEventHandler);
	getJMenuItem8().addActionListener(ivjEventHandler);
	getJCheckBoxMenuItem1().addItemListener(ivjEventHandler);
	getACLMenuItem().addActionListener(ivjEventHandler);
	getStatusbarMenuItem().addItemListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	connPtoP1SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DatabaseWindow");
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(666, 695);
		setJMenuBar(getDatabaseWindowJMenuBar());
		add(getJFrameContentPane());
		initConnections();
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
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		DatabaseWindow aDatabaseWindow;
		aDatabaseWindow = new DatabaseWindow();
		aDatabaseWindow.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = aDatabaseWindow.getInsets();
		aDatabaseWindow.setSize(aDatabaseWindow.getWidth() + insets.left + insets.right, aDatabaseWindow.getHeight() + insets.top + insets.bottom);
		aDatabaseWindow.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}


/**
 * Comment
 */
private void openLatest() {
	getDatabaseWindowManager().openLatest();
}


/**
 * Comment
 */
private void openSelected() {
	getDatabaseWindowManager().openSelected();
}


/**
 * Sets the databaseWindowManager property (cbit.vcell.client.desktop.DatabaseWindowManager) value.
 * @param databaseWindowManager The new value for the property.
 * @see #getDatabaseWindowManager
 */
public void setDatabaseWindowManager(DatabaseWindowManager databaseWindowManager) {
	DatabaseWindowManager oldValue = fieldDatabaseWindowManager;
	fieldDatabaseWindowManager = databaseWindowManager;
	firePropertyChange("databaseWindowManager", oldValue, databaseWindowManager);
}


/**
 * Set the databaseWindowManager1 to a new value.
 * @param newValue cbit.vcell.client.DatabaseWindowManager
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdatabaseWindowManager1(DatabaseWindowManager newValue) {
	if (ivjdatabaseWindowManager1 != newValue) {
		try {
			DatabaseWindowManager oldValue = getdatabaseWindowManager1();
			ivjdatabaseWindowManager1 = newValue;
			connPtoP1SetSource();
			connEtoM1(ivjdatabaseWindowManager1);
			firePropertyChange("databaseWindowManager", oldValue, newValue);
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
 * Comment
 */
public void showCompareDialog(Container contentPane) {
	if (getCompareDialog().isShowing()) {
		getCompareDialog().setVisible(false);
	}
	getCompareDialog().setContentPane(contentPane);
	getCompareDialog().setSize((int)(getWidth() * 0.5), (int)(getHeight() * 0.8));
	BeanUtils.centerOnComponent(getCompareDialog(), getContentPane());
	getCompareDialog().setVisible(true);
}


/**
 * Comment
 */
private void updateACLMenuItems() {
	boolean isEnabled2 = getDatabaseWindowManager().isOwnerUserEqual();
	getACLMenuItem().setEnabled(isEnabled2);
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 4:32:17 PM)
 */
public void updateConnectionStatus(ConnectionStatus connStatus) {
	// fooling around with menus not necessary since glass pane used to block input, we only update status area
	String status = "";
	switch (connStatus.getStatus()) {
		case ConnectionStatus.NOT_CONNECTED: {
			status = "";
			getJProgressBarConnection().setString("NOT CONNECTED");
			getJProgressBarConnection().setValue(0);
			break;
		}
		case ConnectionStatus.CONNECTED: {
			status = "Server: " + connStatus.getServerHost() + " User: " + connStatus.getUserName();
			getJProgressBarConnection().setString("CONNECTED");
			getJProgressBarConnection().setValue(100);
			break;
		}
		case ConnectionStatus.INITIALIZING: {
			status = "Server: " + connStatus.getServerHost() + " User: " + connStatus.getUserName();
			getJProgressBarConnection().setString("INITIALIZING...");
			getJProgressBarConnection().setValue(0);
			break;
		}
		case ConnectionStatus.DISCONNECTED: {
			status = "Server: " + connStatus.getServerHost() + " User: " + connStatus.getUserName();
			getJProgressBarConnection().setString("DISCONNECTED");
			getJProgressBarConnection().setValue(0);
			break;
		}
	}
	getStatusMsgConnection().setText(status);
}


/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 11:51:20 AM)
 */
public void updateMemoryStatus(long freeBytes, long totalBytes) {
	DecimalFormat df = (DecimalFormat)DecimalFormat.getInstance();
	df.setMaximumFractionDigits(1);
	String usedMB = df.format((totalBytes - freeBytes)/1000000.0);
	String totalMB = df.format(totalBytes/1000000.0);
	getStatusMsgMemory().setText("Java Memory Used: " + usedMB + "MB / " + totalMB + "MB");
	getJProgressBarMemory().setValue((int)(100 * (totalBytes - freeBytes) / totalBytes));
}


/**
 * Insert the method's description here.
 * Creation date: (5/10/2004 4:32:17 PM)
 */
public void updateWhileInitializing(int i) {
	// extra animation while initializing connection
	getJProgressBarConnection().setValue(i);
}


/**
 * Comment
 */
private void viewLatestOnly() {
	getDatabaseWindowManager().setLatestOnly(getJCheckBoxMenuItem1().isSelected());
}


private void viewStatusBar() {
	/* Hide or show the statusbar */
	getStatusBarPane().setVisible(getStatusbarMenuItem().isSelected());
}

private void reconnect(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getDatabaseWindowManager().reconnect();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

}