package cbit.vcell.client.desktop;
import cbit.vcell.client.*;
import java.text.*;
import cbit.vcell.client.server.*;
import java.awt.*;
import cbit.vcell.geometry.*;
import cbit.vcell.mathmodel.*;
import cbit.vcell.biomodel.*;
import javax.swing.*;
import cbit.util.BeanUtils;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	private DatabaseWindowPanel ivjdatabaseWindowPanel1 = null;

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
			if (evt.getSource() == DatabaseWindow.this.getdatabaseWindowPanel1() && (evt.getPropertyName().equals("selectedDocumentInfo"))) 
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
private void ACLMenuItemEnabled(boolean arg1) {
	getACLMenuItem().setEnabled(arg1);
}


/**
 * Comment
 */
private void closeWindow() {
	getDatabaseWindowManager().closeWindow();
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
private void compareOpened() {
	return;
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
private void connEtoM1(cbit.vcell.client.DatabaseWindowManager value) {
	try {
		// user code begin {1}
		// user code end
		if ((getdatabaseWindowManager1() != null)) {
			setdatabaseWindowPanel1(getdatabaseWindowManager1().getDatabaseWindowPanel());
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
			ivjACLMenuItem.setText("Access Permissions");
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
private cbit.vcell.client.DatabaseWindowManager getdatabaseWindowManager1() {
	// user code begin {1}
	// user code end
	return ivjdatabaseWindowManager1;
}


/**
 * Return the databaseWindowPanel1 property value.
 * @return cbit.vcell.client.desktop.DatabaseWindowPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DatabaseWindowPanel getdatabaseWindowPanel1() {
	// user code begin {1}
	// user code end
	return ivjdatabaseWindowPanel1;
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
			getJFrameContentPane().add(getStatusBarPane(), "South");
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
			getStatusBarPane().add(getStatusMsgConnection(), "Center");
			getStatusBarPane().add(getJPanelMemStatus(), "East");
			getStatusBarPane().add(getJProgressBarConnection(), "West");
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
		setContentPane(getJFrameContentPane());
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
		DatabaseWindow aDatabaseWindow;
		aDatabaseWindow = new DatabaseWindow();
		aDatabaseWindow.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aDatabaseWindow.show();
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
private void setdatabaseWindowManager1(cbit.vcell.client.DatabaseWindowManager newValue) {
	if (ivjdatabaseWindowManager1 != newValue) {
		try {
			cbit.vcell.client.DatabaseWindowManager oldValue = getdatabaseWindowManager1();
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
 * Set the databaseWindowPanel1 to a new value.
 * @param newValue cbit.vcell.client.desktop.DatabaseWindowPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdatabaseWindowPanel1(DatabaseWindowPanel newValue) {
	if (ivjdatabaseWindowPanel1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjdatabaseWindowPanel1 != null) {
				ivjdatabaseWindowPanel1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjdatabaseWindowPanel1 = newValue;

			/* Listen for events from the new object */
			if (ivjdatabaseWindowPanel1 != null) {
				ivjdatabaseWindowPanel1.addPropertyChangeListener(ivjEventHandler);
			}
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
 * Creation date: (5/24/2004 11:39:07 AM)
 * @param c java.awt.Component
 */
public void setWorkArea(Component c) {
	getContentPane().add(c, BorderLayout.CENTER);
}


/**
 * Comment
 */
public void showCompareDialog(Container contentPane) {
	if (getCompareDialog().isShowing()) {
		getCompareDialog().hide();
	}
	getCompareDialog().setContentPane(contentPane);
	getCompareDialog().setSize((int)(getWidth() * 0.5), (int)(getHeight() * 0.8));
	BeanUtils.centerOnComponent(getCompareDialog(), getContentPane());
	getCompareDialog().show();
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


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G510171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BD8DF8D44535E843CFEB5420C103D29BB1EA50C803C40D92B582CA540831261536E85316BE239F6D072D2F0D166ACD88908CBFC1D05197F52DE8D1B1069FB55220798372CB9632ECB6A1A43B593D3BD9596C2E3BB7A444G6F4C4CBD33F7F76F4DC22C6F657B8E774E1919F34E1CB9F3664C4FDD044DEF472F4CAF58AC8879AB85796F73FCC1D8AE8A02F9595331080BF19B0B85455FBBG9B05B75FAB
	04E7BC6425BCEAACAE91D6FCDB846994C867BEE2ACFE8D5EB789D357EC33420B869F81722E4CFB26E7F4BEEF55A31F55B47D0B4A3570DC8F108AB872AC74907EFF2A5CA263974BF8047C82C14821E5566CAD15F17510B683CC83C87630729970DCC3B90F5FD3A1176B1A1CA8BC7B002FE09D21232009304D4B6AB64BCF1D70FC7E33DE42EB675E35F21DA8C8278110462F90DC9F154133D2FF5C71253557A9151D300AAED7195565945DD219CD749F17BC5EBDFB6AAC12255762975B1DEE1BE7D0903C5F901A43FB
	0BA88DC1280074F20A5BF40470C90177CC00A59C7FF7A341D7790C45A6G2F8F5B3677DF15326C26EFEF9316B86EDD33C123ED13FC2CCD493EC05B749F762EF81D76C3F7835177D248AB719B0B378254837481CC82789F5A7FE27D07702C6E15EABD7D7D9EF73557DB633279DABCB651856FFB76C00E0CBBACDABDBE1BA030BE2E583BDDC51E5100555BFF4E7518CE92F7E1FFA79D1EA9AC1EFDD941EA951DACCE7C36A05E4FF4116A8F680471BE2349FB11BF589E03F9378BCA5E42D3BBD72FD461DD7A0B1D2B17
	4AF484A9403BC2A2FDBD115A008E5E73E5DDB07C2594BF2240B35BFC1D62C9BA967272A4348D53FA142539F03AD03263F2D5BD444D0DAE689AE0B2C8832132BC5ACEE8478FB01EEFF2D91E2678A58ABC13A51F62C9FA5B89E37166C1E3B133AFC793992B4210313894E0B7C0B3008BE0924034A1343119751F1C250D69ADBE29E640693209BE0155F5B43C8ACFF1C872D99A5CFE4962360AC60B4FE969F509FEC1AE63EDA0BA688D2AB7C0ED5F8ABC8E08EE51E7111C9EB7D037740B12687333B2279BB612B2BE11
	669A1C7DA2B068778AC8F7C243A6F83AACFE690857E61144C08916FFD18369139396A3D08482704E74925BCC7415866D4F86D8427530261150EB92FDEE5135E7CF3347F3FC40EBA0AD91F61FA4FA3E0C7A0EBCA8DFF292F59C53C2F0C3100EB909F42A9ADFD65029F3DA0978965FC99CE397D25BC9007269GCBCE226D7C490436D3376EA6E17B4F27285ACE8C58F161B03319526150B1B41552A67255C11EED98FD7E5BC7CC9AE30B48C0E4B917B12F84A23F6CF3B4F9B03FBDB67A681B183EDFB8C85A97856D0A
	87C89FC61DFF5B28E3B6DC83AE9F2D36558786A75A82BD40647CE5FB052C579532EC84CF7A00CC1E9BE899C2D3907EA5737DD83639B7856C43E51446A260AFFCEBAC5E8620879882088748GD88630E544D8DC8550879083108A10B3027ADA5D1EB30A9C6BC3E4C1DD3C2E10A5D4CF7FD23CFF7C9D747D1F8456569A5AD635F62E9B252D2F0D5256D7C2F4727F5D4E75827A25EF1AC8DB4493900E5409DE51ED03973F1CB754C45AAD7BCB3B0757B9CD71DE702F3560507C9C9F55C4F4E211A41F33F7C0925BC4C9
	F23A9D017CE84A4BBF506B8D38E664F5E9935135E7907CABAF9F57C4FC135563F60BF4D070AAF27E9554B70AAE319F042E758C38A5060FEFA6B164F171246C1B931AD78944373A86081FE763BB59C27A536BF37643D0A3D305E0BAC57C66CDFB37A8F4D484B892DFEE07E755A974FD036F226F5BBABCC3C86F7196284D61F157DEDC90FF0A792A14D3815F47E41A5222AE7B1554C74BF8BF1375CF2BC95D5098F185AD5FFC144CE936203C960B5B6290FDBB304CE5A4988FA9233700435F417C5467FAA2CBB64818
	8BA01CC6BF755AC1120FF3036C20EABDAE976B8E482BBACDF47644114DF21B37C03A7CB45359FA1EBF5E087913A09D2F48E7FC8ADBCB08DD085F8C08B0A513C92C0977DDF16B4BA1F9C6399FD97E26D6520FAE457CA71DA1BC4BCC987B67G3FBC00ADE798DF5399B91621657E4C4B25C3FA91C01EDC2EE21C32DCE58F16B3815E8B90B30E154B8AAAA718315CD240971E97A83775BCE53994DE2E927086C5B9A928DC1F854B45GBED1D1AEA328DCE9AF164B853CF0FE201C6EFCE539ACDE2E927086G69FCD6AE
	B9285C25D6AC178578DC00229F30F275BFD016EB37E139BE400F8308174BAD164B55C511F2EF08D8AECF4EAF09E2737EEEF83E8578C4DAEE89AF9723483749CFEAD363C9399B6CD86E59A8B6DF2D9D4F4A6B60D9466533BB30DC74F8C6A7E1BC63BFFFBC23474658F3EF10F1CF57E9DFDBCED8064A7C03603B766CE95CCF62BC369EFB6DCBB2B649FC10BB9E57DD05A69A0BCFGFB8150CFC09F7148BADCF7459ECF9496BF35E335561A32EF8213A3FAC240C774AD2BDE454666F907884FCC48DBBC814765C52D4A
	582F5A6F77D81DB46C144744DE3D5207B0DC0D7EA5F85A1DBE3F54GCEFE8847CF3D1E48CB62D299AD4769AB69FA12780FE540F75BC4D8BFCFA4789AA3712D246FD8BB2D3B301DB143578A66A9D79494EB34B3E1A2EB5F7C0901F6EEBF1D371271CBB2627AF9B9646F1E046B670F86F789673AFE263ECDEEC7BB6D9FC24B85B493GAAFFC870363A4D8A5996833E90705BG184F3C4B34931E966A684A58C3AF7A6C9EDF3FE85BC6AC244CB2A81551F9332C1AE66FA36F9F20BD3D35174CF59E18D15BC462AF2156
	87C6F98DB8FDD3154CB7F366F42172CCF7015A1AFC52063E02289FB2DDA57DB0203321CBFAD7C4833076DD5D0D6D4B03B265BF4276556FD7EBDF5F0F30FDC5BD5F2DFD550708DF96071CE43965F2DA297D11A64AFA97FBD02E91601942653A2AD6CD2E25DC2E4C6FA857690EF5325E1BE115651728C832CC9F18D1A66182982F9720CC46FDEAB2F9AFC09B1C7AB90B033C9EDF903D47B93DA8A81B46694DEF892577AC64ED09C6FA474B48FCE77574C39CA4F2B1994D499624399D4A5B2211660F8E2A511C46E9F6
	FF74F200265EA71EF0FA867C0A7E58570BF41321CEAE272BEFD5232B3B90696E20312ECC37D56A93FD04A823799C27399B4A3BAEC41A579FD652CCGFCB6C05E055847E6ABD943BBA5313F0C7C53A6018AEA7BACEEC7F897B7C03E5C41CC1EBFFCC1E24C93CEF110A92E556DBA296C13B4AB4A3476A2188FAEC219AE298FED67886445DF04B23DEB7BEEF677A50DF1ACD62B6877C345FE271FAC637D3CDF4759D02E946039144B95FBC8CDD7DBAFC6391E340DDDD753CF15483AA255867CB5969F1727D6C47FDE81
	3C3C1C1F49C856AA2A7124007164C2114C81D21F534F7C591F2B157EACF5F5342ABF6B3BBE2EA071E2664732AE8E78B35B553DAB062EC6FF364E0EF269E2A0D60B41F966263714BC0AAC63D5F9C4BF65A8080911776CE282BCF2083F64BE73D14EC3188CFEE0B25A7A66AF49132E93F8DB1B9D1B4274416697A657C8874AB5AEB3C5583269C2D53988F8A7B3F962A68764CA0A6FDD119E0FF2DD6CC09B4905B23ACB30CF7E6AA0B662176711BA0FF500CC6CE4C946642E2FA4FDB6C077152AEB1BDBC477G3185B6
	7C65B5ED6917E43F52F4097A5E09AB84BF8E52EC9FEF084E1F311730FDBCEAAF94FFBE45A7A8702C8DAF19D037A9103765D25CFB529BC8FCB58269B800E4008C00DC0022D80CEB3274E42E9FF5EF2F41ADC158E3F13585ADA2F10F6D90618B6BBEB97D23C34A7EE4FAA8B692BDEC0DBD7BBDA4D66FF5DA2F729C6AB17B7820D3F936D0383131E00506FD38E45AA331817BE03A4B273EA58D700BG168194CD01D8E58A6AED43616D1174067B1C3242D81BEEB60EED5FBDFD0C750E19C26B55CF895627ADA44D6A9D
	8F2BD796121E94D20F0D0B0C15C4BFAAB1BD0E90F94D5FD92DDC7347832D79G39G4B81D86CFFE59D1E45EC3E8CFD52297D4A98BFED43A94D583779B2A61F6B32C05F463B8D2BD97FDE53CE56A8C91017811083309CC0F7B94C91G664B311F3B8E6F0D544F547F9BBC35002B07B5F1ED685E895769755DE36B0BDB426A057645384B036BB17D1D590F7A0B3B9C77940F770CCD0613E175C2E548D215616735A8C3AE1761937358E4589BD6AFD486DD1C1A8C976E63E7FAF1A8436F47A843B391E5700648406C3D
	2ED94D5EA7C159E9F1E86FFFEFC1B9D3381C17DB46A66735E175C265DC2A2A2BFB8E228C05D3D106265E31495091D6AFD4064A29EAB2CCECC5998CDC069747A843BAEBA49922272A7931F99B5E95D47DD8ED1FE8BDDE63998A71E72F6A557AB781E827CE457EFD62B036AB0B37EBEE18FCE7572E2C086DAA3AC24D8EDFBE20A6278E4A6E3E82657C2431C856C37395A867845B58640C8D2B97AAE75495EAB67060A114E19A1721C10CB4872A4B509E31DEF6088CEC0EF823034C91CBA12F68CA5091C0A50099E084
	A07ECA1CA3FEFA38C608F4BE0A9B74868FFFD519A8183C1B6CE3EB67BB916B25DE29564E0BBBC9BB33A0EFA9C061B4E8ABC09D0099A0EA9A36B33F6D5F115AA94F05EC35965EC079FCED0C6DFBB2E23D04E9EA6D5B50C55A1786F90BG16812C7D3131389C2099407BE3ECDFDBDB44FE64C750E7530FD39CE39BB35704558B9DB313FE9CDC0F1DBF6F22FB257110B79F0061AE2038C510DE46F1AFD5915C662B5858E7BCCF1B487AF25BD5181EC45375BCDDE252BA3BC5A0F5C74B9F8D980F8FA27278AEE7442C0F
	BB8C5AE7EFE6037A591B5D20FE7666B2ECE4F820213C79007912E174333991035A595C1B341D3FA8A0EBBA5F00DBF2760B14AD1B174421F7180D9E9EF0B3B3A51C7D21EB8EEE7BAD47C8BF3A2EC29BFF7948CE959B5FE7F3CA9E9F92B10896DF2B5D4E8D3C4DABDAD166B5C6ED7D15985575772AD1DDFF2546517537552825BF26237BDF5DC06B7B7D221FBA9E32A5AD67BDA91175F958B966C1983D826EB3441A5648E576C91E5A9D824E7DEBDEA3674A9AEB98A167AAB206AA2AF09E1D8469F85976B32EE2F7BF
	589CF6C9B54E775BFE02E7973F2EA17DEA067490C04CCFF0FC4557297936441FE80FA9563F933A69BA954AAD82D886D0948F6B54F86C6FB946086B7BFDC3232C6FEFEA576E6F14F6757E3E231D6C877BBD83BE2B985A277734EF61F98147C982A952B52C4C83343EE471B944B01BF80456E7F9DA75FF552EE5B72C5F1F68D6767BCE6EFF22762B75C1FD3CF69FB0FA53FB1474F6F1FA3F2AD5235795111E5D2C24371B533BE41F9ABDEFC4FAEFD81474BE95B0469AE9C67B0C0AC77BAC26E764E9104E86D89A0F76
	A99CD463DDF8F5A45E8B03F8FF465BF21FAA3DAD91691D5F2B24D74169C5372A515B9E115EE7D6A53DAF38EE8E6AD1B7D5D723EE9E27E776C3100E81C83C1A2FA58F2B71CE0F487BB96BCB8A5EBB4A11F76381641D4DF97F72G613DF63A31F8ABC065F4643D3BD10D37E1BA635DB7BDDC86467BCE1B325D1FF3BDE69DA274C6C26A4528508926B7CEC4FAFA4923A73ED476619D5A7E63238EF57F3123C35BFFD4F4C4769F559D5A7E233EA3327FB854C17614FA1DB627CF3EF5415A58E8D7682CA55046291D5AED
	0C6FD4EF63F51D5AED3C29B3F29BEF6D54EE631D1D115BF8DF6768BE3244D10C7DE977F97A21C7D93D573B345B2A6BD2EF6B7BDD5AED7D38ABF2DBBF6B52EE6B5E2E48ED5D5F255E1F190E207137935B5E78A5BD0F916D16819704AC9E3F586A152F2082BEE7BABDEB25F1B559F7CC180EE7993386899FBB2C90C015B4E8CA3D06141D7EA5468ADBA1DD81E0829882083B864F9B5E21B1B90DB0EAACBE067BF6B569A3F69B825AA651B049CD629736163F5D24DC4B8B89AC76588A4F0A841C7F8F9DDB276CE7B97E
	E1678666AABC7F57C379C989580E733FF8C3500CFFC0BAC1E0BE694734FEBA54DB8430AC8177E8075E47BD5A246B268BD9FFD03F0711E3B8D5D0F4AD73AB5B2FD51CCDFD583BC25E7B2AD673EDFD57EA7BA2F6CE52D6CB6FE4DF4B4EC956F1FCAF4547AB70CC8F3F25B199C94F073C726BF01DF49B5DFF1E8669246B78BD10EEED7B1F522DEE7FD7F62B473FD7F70F9E7F4E68569A23CC3EAB697EEEAA481641E5FE2696E54E6638997B9017F39D4E9B67B52B69F669F511F4FB0646874275217760AF2078AD5707
	5E035F580277604BA1AF76FA14693503A8D39E47BD4EF10509083B2695F1A509A8FB1BDECD765244C8329FBAC4E4A9CFE4B22E6478AE0A2FD360196C3F5763592089725238CC4F9FC619F4B3D02656832A71470CC8B2355453EFC8E6046A330362CDB3C275791B8328CFAF64E54FC0191A9AD1260A9B9017C4F1F510B6F15C13F4DF4D8569F19CD7CE5B938B69049B509F0DFCC94E4095433F0C1D43029FA23778A5FFD9DDEB4F41D6C3FD434183BD2D87FBEA1BDB5B764967EF71034A73370A174ED73D2BC9A079
	8679FE127C0CFEF1EABE0B01B238FFDDCE643C11608CDFA94F6D091C64B0360C0C871ABFA1A81F0C1D1665AF3E9848CF8D4AA732FE9AD47F6920FCA2735467D9FD562E875FC33F367C41E441F5CB1466B7CBA3B73276444BCF5D4229797363742B18FF7D4B879A775E68A1B7EB7FB3A65C7FDCF4A34600F7F5523B7BC9303E8328CAC25B33D70D6DECD0BA67FA4CB6B7D6D35BCCE236D94C71EFD37C10824FEC769ABACF10F49C64ADCF42736959E6228773CD46629100D800A400CC005C1B505FFE5583E171686B
	D703475B8C8E5925F0214C2EE7D66318D8FE936A2A231670AC1A89E3972082C08F5087B0EEA6166923E548FDD3F207B5EEA63BD31ABA135DD51577A2EB4CA13AC11A04161A6E74B23EC211BFCE23ECF0BF9554067242FB72459A7556A86AA3EE5DA8FC1066A6615EEAFA4F84A631C5B3493702F39C824F3BF9FFF05EDF7AC89EFBFF756B40FB77F1724E7AA02E997B207427283B71F4CE588669AAG9347CF26F803FC67165C27E57857DAA26EBD4A344E45168984F817C76401BABFD79E2B39DEAEF285F4B42BBF
	701E6D8E686BEBBD6A4B4BE5EB27BEFB845253B82E1762485DDF835753D37AC8ED988DC6947A1B2622CB59968FFD979E4A7DDBB5DDB2FDD4FA837A309FC0FD247136375139B18352B9GF99C5FC371645EF4C9B244F149087F438114B9EF8CB6021091D2B7C7CEB39EAFF09E2B47C8BFD4BFCA9DB13DC85F88820E45F90D040F68A61B3DB6F9CD722BC61CB37549B80F3FC06D846FB131F22F70F3C89BAFF760F070583E1C0421729EE31D1F141720ECCCB27A6509BD9F4153E879BAFC879910FA1F87D6CC52C906
	25DF8F48FA7A682B31FDBFFA4FEA1CFBD36E38C1C81DD027F96FA3B9D93E6FA6BFD3B327662F67ED19A9915FAE4C82FF84D085E082F081C44D4236AD6C116F7690712BA549E26D23373A79A63BD39C8C2049C20F356D2FE1F7E662E685ECA6E9D6386D31F9F367D13A0E1A95FA5F2B126233E6055E771A5F09773DF2A02F6EE66EA72BC97C128869B400AC00A5GF9BC7FBD1ACF6E5912FB18644EA9397738F6B6664F28D20EEDC2E734FEC953484F1371CB64E7564D4AF1B2B708C724739CADFF0B73DE68BC477A
	AF4D2C3DC61BE7D6DF235DEFD6DF23BDEC9E7D8C67B1337677D5CF18155FD731F1F56C04A060987E708D327F40EFC6717D07CD7AA0BC7F26A87B90E9F7E31B883CACB02EF6497818F5C1653F407206634165F74B78553D45C174F7886CDE6C07C1F7324BE733BB59641D65176E574AE7E37461234A383F648AD7C13E4698B54DE675EC33836BFE261FFB87837A59DB94542E4F905FE28942FF0E78BF3E9E042FC01D678E85E8FED1CD7A135D4D6D357878AD8E4C379A481D6E30DB9EBB10D671F000561177099E95
	37BFF17FFEEF305EF7487847BB02647B9475DA54942AD7B2BE83FA5F5D24F60FBAEEB67A6EE79A091D59ADAEFF60ECEBBB73673E81F98F2C413FDEE9474CA54BF62A77937ACE37D3F2DADC4EE13A515586855D8E66B3FFBDCC721300DF8640D2GA145D83CB9857DE5367F1F827517AA3EF21F4DA1621A91795DA6E90F4F3B24485F0BAF10089EFBBD126469A774917F10C47C006471D2A4437DCCA23669F3BA7AA4D6D446AF1268BAD834D3B40BB556F590DCB9342BAE855746511DF8FE5E17423FE3BECAF49B4D
	53B5B41D48538FF41EEB0CC4E81DEDD9794C611CF91C4B59B9332177F62CC2FB65BEE8EEBB47F1FFE52B6138DD083BED1863F6A36E55FFF15C273807595F26F69FA993741113027B1113A5F2FFC3E53D4CC79833533CEF855E3FD71FF8CF5008CF40AA9B01CCEB6F57909399ACE40143565C471BF04D1D47ED7B77435A367A74F0E4DB7D4BB07DAE8CEC4CF052AB0F45BC2A9B0F5D6E9779187847F08947B578759E3F130CC226372D9D243C4BF252B3G655979733FBBD0EF6537980B6B813CGD1649D72275DC2
	575D1DCA5D2A7771C93A374E3FD5885ADF769F8D5DDFA63417G6D3CDB700C7B5AB654D9492D38CF71957D3D09FAC81B8106EE45F161EDD20B7DE2EEE5361FF0EB7898E0710E35837729D3214C32DB51AE7E2BAC682EB179ACE34F1E9A8F79E63619A90B49F8E38746455BEE6367E914269952C3GB137615E755747B14EAC3D719ABAAFE96D71A45E46644D3CADB0072D6736F463D1544B5AD464E9BAC67799A15D85A02522DEEEE8D353CBF42A3C07142A95B74E26FACEC9E571E1BE4727D1FC3A82AFFF0F540E
	FA4C043C725B5147CDEC42B5D6AC4765D2DCBC24D3EEC7B91D9DE35BE79A9CE33D64F32EA76F295278B86376D03D98A9BE7B76D03D7C065BD78E64555D017BD356D3447E93A1BD9FA097C01883739E4076B9689FC6C648F93F56BA683047C30FEC48690D4589F1A01D8AFD70AAE19B0F063631EACE203F8DF3347ABE135AD25F1C50FD63C594BFEECE683EF177B15CB70E033C65F3301F6FE9433EB727A16E1F866CFBD79A767D6D6D826F17390A7726766F329FF0F6F6E07BDEF93038EE79E952A66693DFEC8ADF
	DB3F5A0CBEE49C57C9DBE7C08FDD0A77BFF4FC7FBAF961FB65A15FEFBB25BD7779CFC51E7BC6CE2947E9671F8A0F53A61ED20F53AEBC959E27AD203EB5967AA1115B6C3AE39827E5727E79993579BCC8EF39934A5D0978A7A85E8469A100E99CEFBB2625D3D2D7CBD7C87F6C7A6D14A60F51760D4EF66F1159725B4494E53BEDB2205D265D096B050F1B43578BCF9C8D0FB17E1B4F7FD9F73298033C6742BB09F5B75FC54AECBD269BC59E61AED60771303849F7FDAC56E871BB824775F27E60A031B7F68A52423E
	3B9C704BF9975BDE25F9BE0F43A77AC9282228AE2F13B6F6AB57C9DDF7B1B94741D37E66489C9A4F107CD8D26EAE7CB674A30312C63ACCE369DD283BDE03D2F74C373E59057307F037313862EE1C6FBBA95E8B69C9G71F7A30D6AE304C69BE81AEC204B3FEFA2AA630E52372B8AB68A6AF1C76A5D727E4A5D41F107FC36E457B8DBEA36746259D2FDB77E264216F4FED751DCA44B22CFC779BB68B71B13A09D8F101A0EF1573ACF5ED26721689D0C03FA8D9887E52563B8FB37EBEC71400EF32E47664405349F0A
	6E890D879E23784D77044683E65E1FBA480B3A87630106FCE26B3910AE1CEBAC5E86D085E0GF0810C83081B0BF101D558A6E84785ACC88D84832CFDADC15FF9254C156F144A4F45F3357664AF675FA4AD134B944E8B145D826F5B678557E56DFF0876EF553C507370C594EF1897FA9E5EE3477370BE484B1C077D69B0E875CB79FC082766EB491D5DA5707747956F762E6FFF7EBC733D72E0B6E048571EBF0F66C71EBFED796A73675779617327A7DFFD7E9C48D74E1F8CB7AC5375718B518CBF0E2EF5589C9078
	7E1C4D2FB66AB3CC50172E79B8675E50C3F0B1100E67388DE61C0713B8EEA643C17AD90E9B96892E68DEE3F1693D08FB4E0EF52BB8EE07034A8F6928FB515E9E256562A01D42F1CFD0DCB6241F65B8510EFDCC4A7DA77BB70E73B84DF90C65ECF0B4D841F5FF1A7A504D77411A6DBE544B7D8654D55DFDB82FFC5B3D0A77AB4E6117F4631C6705F2136EE3FAC803270947837774841F7DFCCABF53E7737B7BE65DA8B2E65F271C5F6FB5937B717AC43B68730936B667B031C39607BCC1EDA0974ABF4B651DAC0647
	9C09A24A2BDBGF3A90099C0DA40E2B5A6D309E3B4192297A8E52A2777987B1DEEE77FC0BF150849E326F6B58D4AA6AEC0F9D6FDC146AB0FCFA0847178CE4AF47EFA066E8BABE6B8B9DB0EBDCA9C21318711B7FB814695AFB4044695A4BF17731EE220F7A4692E2B1ECC893072C05D4DB460F95D728528039C43E8BAD81B41F4407C769D5DB81F15825E10017378CA9D59A38B1D474B18816D5933CF327609369A591C64BBFFFF23342201C682C0DA864ADFEBA6FDD9AB125F8A40B87431D6017703C7A4D129DB9C
	5471DF5028F3760BEE720B9D5BF61517EF576D7C6C83D95EAC91654D867AEB6F673F0BE28E5A87E47BBD87FCCEDB0D4541B6FCD84C31450EB1EF5D7D984B6C27F3578824E381126E67313003587EBE0BDF367D67758119AB6D23483C93E5E6BC8FB8B04E19FFBF46D41FB6E2CC351C733B57007B72AC7D8F1AEE977DF28C109E762D77594DE78B4F399E1B6B0F535F8C5A7DC0E8ECA4D1FC5583213151BF38ADB5C3DE72837CB723A3FEE329AE7712B156FBB36237006A6D3D035A416687C35BBB0F62F58F063637
	590E6D5D8EF9B18FE2FB1B433E5123636DC1B66E0AB235E21EBD2A77758914C21DF200BAG1322BE2B778D7D8DEF76BE2CF8DF51737D47CA257F87BC526D01B65D2FF8BFEC7F7EF99F7FDEF930795A6A40D8491509F378FC874E61C39CF7087A0C8CC8AF6638AB9A79B71C9CB71E6E5BD5BDE4AC6EFA88F1D9946705F494G73E7EF9EA63A4CF908647F5AD1A74B9477107AF7A1F84F00C09214C987A0F76109DF4BFEA8B0B6186F6A23F7D06439D51E2B4E70BBA85B969A0B995FD90D4837E2E1B85F7C903E4D8B
	03791A9706721D5626BDD75E5206F325EBA17FBD27B665EF5230F2AF50F2A4BD32907D755ABE74615100FB1B560531AC605F322F42F1B857D53511F736167F6DDA65DA3E6E313A02F78575357CFCD99F1B9F8E2C654D565DEB185C17B512BA647EA3136FF7016F729FD613CF28E2325CE090382EB25B02671BD78C85707C349CEDFFE31F529FC5815D3807C70F1559DAB86E886953A4A81B81309420B04BD83C85209C208E4084608208821886108CB09FE091C0AE40F20012C7F03DBC3E6D83C1FB3DACCF720A5D
	F37ECDF810BF5D8A74AA9E99DDFEB6D636513EEEFE04599643FDCCF1B6994764CABA223D061BFDA4729AAE6D087A9AEE6E1170B55C02A36AEB38070E042F61FEF60458018374E262659E63388E0EDBCCE992DC3B53C6771B987EF7D4AE022F9789C34467D23986A94EA4637E2840F54838BF9FA1719EB4394F63433AFFBBC2EC4D6EF439D87A6F340C536D97A52E8F41C8E4F4FAAD8A39D7F2DC876F0B979A437BE7C51332FFD8CCE8F9FF2D406FAE50A6B2FC69503AG9E1BA8307FB34375EEBA90C87A2330D292
	DEE905CE55616FC56A0859C1F4A789057B061C12A4EC22596CE76F82192B76515FA514047521B9A578B30081D4917D853F3232B220A69717FFF0DEC7FEC8CF92DE2E263FE027D37C041DA43C4416A13A9A72B349057B1DAED1925656321F845435BBA5E8CEA13907C6A43591C9D76B87FADDCEBF60D73509961F95DE569D74C8BA7993E851667789AF07473155977D7231241E3A4D85C2C103302A0FAA2AC1D8B9C8B4B9C46EE13E811EEB3B7F9B61B43DD0CFBE8C4A100437F56484DD4716C03A813FE85B237B34
	3C25C67709EEC7B9BC0157864E2B019AC04E09DDFBD67C7E1A9585C2A1B0E28322C1A8A4D68D7CFC0C6F8A989014EB2192A877D7C23172EFFD1DD15EB94A9BEF557C56A06FD1669756E6077E5657667709DD55835E160D7E48765E47DA7E283AD7EA6B3738DC1245F53C5AEDA31A28B9A90901BBE12F3FCF687B310C1CA9FFA377BE117BEBC51D7F85D0CB8788E0C6E81BAFA1GGA4E6GGD0CB818294G94G88G88G510171B4E0C6E81BAFA1GGA4E6GG8CGGGGGGGGGGGGGGGGGE2
	F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE9A1GGGG
**end of data**/
}
}