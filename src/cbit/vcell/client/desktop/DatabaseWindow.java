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
	D0CB838494G88G88GC9FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BD8DD8DC5515D423C6B7EDF1175668E6DD5646163AE8333B3126ED3ACB3B59AEEDE31BE22CE9B769C7B5EDE9CBEB5C46B66D665BFD9972831A9FB42854C69DABD5E290A13F84C942DFA2097C8CB001A18CBF0F1987B3B0FF4CBCE0B0A459F3FF4E1DB7B36FB18956DD3E6F643DFB6E39771CFB6E39671E7B73A6D251AB295B36180AA4E94BB6097F9D5BA2C91B95C9FA74D1737D082BFAA32BC0527C3D
	8A700C7457D736423389726C8B328A8A25273764C35A8E6964DB328ADE00773D528B4ED2873C987011A02F3CB2653659791CECC1BEBBE8BA27E5A7BCF7838C820EBC535FA07C2556E20E1F477188DBCC12B43F14507C21F59F47EDG1AFC40B58258CA99FDG1ECC009B2A2BB81D7D56B4E963B70226DD249ECD1D882BF8590D7CE916FE37E551FE422B31F5A7AFD386E9A7GE9FC1E64B9D08E4F9A4B5848C94710DBAD1FF4A89ECF394363D6FCEA39D3890D297EC0F5F5232C4AC3F2C869F67B1C7EA9C9AAFE13
	543920B51F56A1C959D05FE20A2B6AA1FC6C702E82ACF8937166DE024F01F769C0D6C17181EC5B09B7EA386C360B8FC82B8FFA376EB1E8DB458156A65B01C85B4C5EF5265F53FE38CC751D82F94BGD682ECGA8AC4BAA28G78836D7F9BDA5E04E75D105A6077FA7D3E3AC0205E638C36781D0A875E2B2BA10763BA9407BF6814A4564767DA4BF464198DD8B95BB517E3BAC9BB047D1D5EF11F34761FBEE95A21231335E997CDCDE5CC9703E5911DB05E379A72CEAA0B3647E85E5D1216F772638F67ED53611D3E
	76613C94DECF1A06F7F699696BF4EA8319703E056B026117D1FC49DB91BC334D1394CF52B510177A965A06EDB74A523C75AE293072B6DDBDAC7C6A825356F2A6433E72D8D9B6D13BEBAAE7BCDF92326C31937C828D1E4972BA25A769D510377C6D2C82E6DFB7D948D8CD2B48AAD8813096E0A3C0D1A55897C0E3A55A5837DA5E3ECA9B3348C135FE426DF1AAC109153D2975F9F8AAE1B5A81F7505D45967D03A6420DB9E72A8A10953A437929D8CC5157B8435FD8770F8D771A9C1D9F57BFDD03B6CD5D4A598E2B4
	F736BEC3E8028A4D353A3D8AB07086A42C77532DFB616911C36A7900D3D61588854B3F2F157449245C85B5C285704E7432341D684B896D1F81D8A074303F0D54771E9274A91E6A6AE63FFFECA2E0A5AD1156D692BD27D15F118A6F4BAAD147CBBA884EFCB02B206AA056B358762C261EC6370308AF8727F10C7DA3351DE620F781ACB8083673CB9B5ACE632EFB253207BF21EBBB66F58F67259FE4B63362E06C987AE7DAB711AF9772F69EC21F5F58E3B398DBC40666037A636BEA00484FF8749972E0FEFBEE7523
	EFE27AFE59CA5AD7866DEA82F09DC21D7FF51B1959F0BD38FC345A36A0981C620C74G13733FFB2B38DE7338EC84CF7A60E900BD140654A9C9EFF03EEFF11BFBC942BEFC4ECE6AC8827EE9G2BGB681188F03DCG810005G99GB9G05C740BEG8681669FC1FD1575E64FA2476E98D9D0973F5748922B27B7B46FEFFDGFD7FB90135F50436D52F1D3BE6E96B8B33347539981D7CFF37F33704FE69D68BE90BB28961C82392D0FCCEF889713C059652EE6EAF077D224C1D949FG7F5AG8EADA4700BADC4A732
	2A865DC3932A5229282A5BB79249CF273CC293C30108EBC6DE7FE4A13A76CF01FF95744BAC44B7B97CBE1FC28705A842733FC8FD2362D13CA0F403FF4227B2FCC6BB09A14714E96E1BD734EF1708EF75CC90BF4F4677921974E7A0687642D0A3D305A49DA5FE73DFC4DCC9E08958BF09AFD743B367887A3E29575077153C7FD9A9E3A0E0521B43975E7DF1D353D1662B6CC7A33E0F49F4FF073E6CDF22BE1A63C3CC56DF6EA0E5E3E344BCCA5F5CCB66B4E7D4DE0B6C13C714E0A552FC128463B1B496999CFEA573
	D35D6709ACB3A0E3B2C05AD174D347BAC9BE4E8D5CC1B578BD9E569D1017C3F516F7210837B98352EB0EB21D6D967977087C0AE360F30EC572991F92EBA1318BE5FCC201A919CCE26F093EBBE0FDB6A62F0B77A34BBFE4A57D68514CFF7B2A884FCEBB467E7301DFAAC08667ABD571D806523D5007F4AEC8A78124D6B13A6CA8BA5901F452F1082F812A0EB33A70F1AD5D1F1CC8178278F48D5D4AA83A6F893A75005F2C215BD72D257B2802F4D50037D4C76882D1F4F543C8178C78B48DDDE6945D1302EEBD60B7
	836CFB0751595E5152AD9BC13AB060138116F03A8DD1F4B3AE24AB2C49AAA885E82AE1F473EA98DDEE8D21BB65C63AD41E3F2C064D7BEB6179B260ADA788DD4128E86F09C8BE098D56727AF2A85D37855D2293EC3EDA4A69B361D9AE647B0BB124BBFC125553FC1271771CE47531B1765B974938276B34D1F9D28E17072640F7D5D71FBBC362BC369E5B5FC646A6198F12CF623A6BF9B70D45A11D8D3069A47A086F6C42F5D74AD81A645930BA4FE8CDD9F80A49F178D444C7E426CD65313179F7DD04278AF973CE
	61383C4F2A0D7D6AC2A13F43CD43CEBEA67A4EEBFD88438D1DFF8A1E436EE0C8BD8BCEBE0C6347F51E48CB62D20E66F17A1B97F6CA4CFFAC843E2BG72EB89FE6482712D246FD8BB9D07301D496F5FAD8DFE628E5353866DEC2EE56D7354C65A397075A3F90C5FF7AE607AF9B1642F2D4575730129C352352E1F29EF636D18216B90D2D7D19D74BD407ABA021FBCDF2411ED9E605281D681B01F79187DA0BCE56A684A59432284077DC12F62ACA596D2AECF2965F45EAC2F235927497BFE342717CF10394E8FB3EA
	27C27CA5145A5F455700F76D2D65FCD75EF717D4F3255F2437A61F3F47E5AA2BE73A3256C7F4D634FA1D299400356FEBC3583ED420D9D30F6D3B502D573E4286EC5FDB0E8F563E3AF6621715301BAC37BCEE8735BF52C42E77DB9CA8D7897034B720DCCB2CFAF22588391E7800F2DD6E5945755E8C2B2C10CAC562B235BAD126B46017ADE4F2F56949D45C08B6F87BB19687857C41287ACC8A56D786344EC62C6F3B7D3175AD023C8CD15FD8B9196F9CFEAF44C10A90135579F9D167EA205FF99A6BDCB420D7E763
	E92C337F403311BAADC1E5526D1F88E97AE3F4986B35C119E4D16F5020DE3D19225ECA9A6B72FA5BD41792A41532BADF9E41BA5782FD51992C738B8EED1D4D001F81C8BD03FDFC4388D9433BD545DBCE7E69D4C1858DAE59B7925F45E7A11FF7B01367C747C90CB969D62618625AFC1EE9ED1F7CD048349478658A19967744363364DD182B5EC5191ADC9F4C6ECED29B129D8EA5940202DEF708AC63C322DF6FF621DCF6601972AE4A7569A1BDDD2D94F26DF44FDDD7F7DDAA643AA245A6C275F2D04863F223FF4F
	86DE45CD486F528532D6510DA7A50CA7FF65A6F3006AF20718BF7B4FBA2DBFDB36E3012EBFF3FDE6214952447CD838A9624F244756190A9EC3FFF6F094654A849A69AC4EB3773E2C6511AF5F204BE341CFC7CCD5E7791E5D59880F9970174942E7EE92BC5200A67BAC5AFA51C972246B84515671313DF17AE0738B13EB268765CADA39D4AA5E7BD1DD3988941FE372541E0B48557468BA137DD114EB69985AC8B250E41E43BE79030758C8084FA30DFE4784195849120C495DD4C37AEC026EAB55B5B437A83E89E2
	8BEC7873B5EDF99B592F145E535FBBA90A4117C21A6D635DC26D2466BD360FC76D0562D3A83ED903E7ED58A76C4A8EF9996F615ED3200744D7A54DD98535GB600GC0B24012E60CEBB61CA7F37D2CFBFBE7FDAA04BD3227B3EA9109FBECDD04AF2C7BF87A1EAEEDFFB2BDD4DCA0FAD859FC75FBC82C5CC9DAEE7DB514E376317F0276ECE16BB367CCF98676D154426AAEE9095887535D2B3DF47D8678A40005GCBGD635205E5E6EAECB24B75C67648AE3EDFA64425C765DFFB447F25759E34BEDE80956674E56
	68B4AB179CD72EBCA6DD91D30E0D0B4CEDC4BFBAB1BD0E903E6677F4E8577CCDD01787A099E0B1G0B7D6FBD0FE7B14BDB51A7DDBA230D71176F39E4987BEE64729535C57A56327257BC467FD73AD635C2DEGE0BE40E200CC0075G79966C67196E93097A197AFF2B3F81F0CD30A6EE085DBB91BADDB9B4373E78FEDC39583EA835C417E37A3B359B75D7EB41BD45E4475CE438B32EDC2C8CE1DD99FEE2C59912058CD6675CE4980AAB97ABC3262E8CFF5BA54E74048C794A5CE4F8BE2EDC2C8C456D5165183DF7
	756959FB8550B636233D6F9AC0B96D6DA86772B94A790D04F226346B696AA1A1C33A10A1B0BCB799AE47150B15E13D2E8C378F228C1B058CA5A3F31361E0DC39D8998EF7687931FF5D731A246B479ADC0AE32C5E9F0E71E72532DE7FB6C35D039D583F9B9C582EF087366BA7F3EC5713895B3524C34F8ECBECFAF2E6826DDAA1E7DDEFBE5743C6A1675F3B66A66797624A454AD95629E7836B06D00646CE144167CEB4876A4BB013305CCC0C8CEC0E286EA7F3C48A64AD814884D88F10EF05F58CC013956708EFF7
	57CB094EC7F1035E6A972FBA93851337EAF4EE6DBC13305C20D52F1D1F9BA06D8CC3DE8AC0BAC0A6C0AEC07E792C023273584ED7ED6FA4EAA71F8B59EAAD3E017CFCED0E6D5BB91628DC73F93D763DAD1376B9A1AF89E0A140D200B5G9B810A3B30FDA1DB42FE94C750D7530FDF0C13776A46CCE6DC3958B1D35195DD0E1DBF1F26FB25CD106781E0383F2138A4C8AF9438FD6D84373C0B0DFD9E975075652AAECC5FC1539BC47A0FEE2333DB04D5B1B2DD8BB09E6FA46471C14E08D99FCF77981F3DA575681F3D
	5D5823FF76362067990607BA34B79FB0BF25E7763339C5BDFAE7F3AF51F6FE57C456F441891F6A76AA14AD1B1714702B4CC6BBA6FC4CCC8967D06C1AC3583EF70874E3D1B75A78418B87F5EC7C34532D7A03D809D51103ED43434240BB830A83E55E5FEB2C3F0ADEFD7D9D6D5557DFCD6F6C7AEB6CB5529F5351571F5FC34B07C2CA08BA9E32A54D73FE221275F95CB9E6AB0CDE8977993EE0AF60B427D5FFC32504F3FF418B64DC59E08DA34D6FA6E368DCBB4E239510EE6A66775B3A595D8FB607AD6E4079FED5
	B71EDD7C221374EB3EAD2B408CD0E54371750F6775FC1B45E6BC26D87FFE1A6E7BB901AE89E0A1409200D5B66C6F7F68CD383EBF9D1EE5FD7F205D383FD75B757B7B7BF6329F9C72CF849DCAEC1F7E50DEAC72A20E1384D224EB984D4FE8F9D58E0EA8F1B67184AD4F720C4A7F4EEEE4B72C5F739C5AFEBFA87C4FA73B757AE043ACFD406A7B77287A8E097AF22DFA75ED6DC9D45FDFB835759D9675AD6E522B2FB8E1FD2794EDFDC7A40C31BE560776D956037679D2BF3D7B84699900149E344FE78774F827A764
	7D2BA85EC7C5DB5668561711303E4F8CEB6B2B92755DB928D75F6A0475750EE86BBBAEF4B3A823EEF204EE9E1709EE4C97E05C82D8AE20EE1E0CDB9B93F0DDC8447BE557D39A5E1595485BEEC35EB39710774FED0477D2C82F84D8AFF81F6955633D1973AE6C0D170171FE5425ED77B12147C769DEC3C9CC39AA1DFA226B3B5B0D75D9D43F0578D26E437B0D7D47FB7D7A7E233D5F58FF587A937B8F395F58FF387A937B0F71FE3227B464F63A037C5685EB23FFD4233316C89B3FB0E05C460C817DB6FEED40380D
	8F8EA4EE63F7870C5B38EEA0F19BFFBCB03B0FBCB276B47667F0506F059EE5650E4B46ED2D15755BFAC6B6EEEB0B1C382DDD32F1DB7B6444ED9D16757B7309312871F7905BEEEF2367B14A30BC610110459FD25A827C8AAA6067771271B8C563EA326F585C0BE7997FB0C5788C438A81D4C90326C1BBBDE3E843D8E1A550E683C800B783545A713C61900D49E904D1AF87996E628E52C76CB68434CD25E1120F44AFECAD7FCD37F6AD1FE6E73147CAF8E65BF17E9F9F5A256DE79E7F30F303E0BB1E7FEF827A0ABE
	EC47F547DF148C639F10CE1218CF7A9CAD6F02F2C9G8B7BF00FB67CBA6E5126FF7AAEE9B0D77F9EC6367512E9C99F73AB2B7BA2FB34073D6BCC4C97FD36C34F3795769B7BA2F6CE2250BB20A57D6C1CE417402B945F2441B3BD7C0A46E4A46D013CB57D38CE7ABA5DFFEE9C48AA308E08FBA08EE37B3F5F21EF7FDFF268473FDFF14C9E7FFE5DE1B4C619FC77527D5DC110ADB000B2678919E7846ECBDD081BBF007346DF756969B6E5A011EEEF2571C15AC06CBD783F21780C0158FB702F7563BD78B510D7B308
	B26D9FC019D2856EF101CB97387B879137EC90E5D7E4BD59D78CA6125DC16FED2C99E4B2EE93F89F4567EA70CC76A7E5BC9B14642C82270CB26DF422CC19B24AA45BF4638FB911CC8374BEF60E9C2B4FB10A1706E27579048D75D98CF9B3C3A8D3DFAF4A14ADF07FCCF13910169C087BAD5DD7AB02F429405553FD499AC8B7BB509FFD2C0D1C01EB06FFB9BB0785BFC4EE712B217246360156B6EB535956F7875ADA879A1A5BBACF73733754A96D79DB55D357695E55A4E0F3707BC97C397939BB363098687B423F
	AEA6F2BA88EE5822BD37A7F2124358F2B29EE87E4228FCB2F6DAB67F4F7FC4723F9115CFE4BD92D5FEF3D4BE1179765F31722CDD5F7C937A354D5FDCA2F96E1FE7784DD20913352709BF13575F3EE55E26777318FF7D55FE03FBEF74101B35BFEF9477BF131C98832E22E7554BA03D8AA047093697EE1F5B59600D57DC0E5966EB749E2D24B05BFCDA60ABA95E2C41B31B3D1F4E93A4DD8BF90B95BC1FFEC8A1FA489F4EAAA8812881308228G494368EF871C90964F3EFE357A834D6010BD9A974A6CBA23834744
	62E15455181570DC8269CC00EC00CDG05A3B09EC61066FDCAC36E1B12BB2C35A36CCE6960883B2B4AE8FE5BB998239B2C13542527BB42076AF0B812DFEAC09B5DCFC556D8DEF8CF7EE903F2851A72085BB58B9F2CF32F74C8873DE78213D8528879D670BFBC1248DB519D1D3757C772587BD1FF647DB27984074FD17774E19F2C907A3D0D4E892BA01DC368DD087F944513FB3996FE1F16617777A75CFB64F5DD0BAD9308702ECF4883F5FE2DBCF6883D5CB79E5151F70211770D21083EE6E454D731104DCDFDF6
	89249B85CE25B8F277F7335053FF49095AB09B107A713D51952FCBEE0BC39F0407F67FD6CF17CC9F760908BE42B65407D334DD21F3E3G527381D285FE1C62493D69E500DBA3703F31214C29BA6D3ADA8844141D4F530CC7217836E9479C6B0F550FD6C7CCAFA9131204E37161DE42C771114DDEA7DF136476621C39490573F8A11D5B719E9323FBC31CC36EF4A3DD0FA3FAECBFB0C5F8B1BF6D8C937AFDC0DB65C63F7C996781F8F6492371BB480034847D30E2D2279916FEBD4075F442B2376FC73F3283675E25
	DF3CC7DAF6E323613D8F1B1B5FF7634F5247EE5F32DB3465DB53443727C15E8A009CGE9B42B208820EC945B76CBA73F5BC3442FD3D5596122373A45A63BDB190A2049C20F5FA908BB572A9D0D580CF5B45E76583C5968216B2851587BDE2D949F9E0D3D6F755DB13C6FB59F72F2C7051F6CA0710BE58C468AC098E081C06A98669F2379697C9EA639F3CA6EBDAE9579DFEA570EEDD24FEC7D62B448CF6578857C999E530E1355D1BC921D679879DB1C77E267B956FFEB9563B55AE30A7E9A6D270A7E9A6DF1E576
	B31C5FA8465FD77D37227D3E0A0D2BCB97A58947701BAF127D87F1B3CA6CBF6C35C46145B7C52B5BC93B4FF5AA40CB06F1F5086313F7C5519FC7FA6BD8B47DE10E5FB174F4D47D15923B97DB93F5A7FB4D983B13CD5ED9FEF937D1BE9B235F6E55467D05F7F8CCDB8C462864E165F6FAA26BFE261F9F4DC474F3A2BF2ADDC7915FA2C7610FA17E973F0F42D7214E73AFC76ABCDEC77A135D4D9D120311DB9CBC5FC13FFD0A3B65D109F59D3892296B7C6BC40F1A5B1F9CBFFCA2DA6F159C3F2EA7CA3EA32857976D
	31FAA563B32277B3F63DFB54359E745DF9F6E2E74332A794B95BBA497CF9F0026F0139433B35F64CDCB23753C018546F7639D5376CF13FCFB73ABA0150B742FC66FFDDA179D66097GC881C883D86EC1FF39B17CC7097ACB9DDFF95AB922601A9179BDB2EDBCBEFFB01D78FB711FCC93BD8E79D5556FA575A37E97747BDD559F20C8067B4FE9E21BC17708CBE5249C3FE91A4CE99EE51822D92C718ADDF32D01F665FAF0EDFC67981E1F97FA51CF1E73905D9E96E9ABCDDBC4FA6D583546C8242E2B2565E78E574C
	63DA4E4E198D7D29F2BB5A2B70C1DF6D96B8612F1C7582F788F18F3CAFF00791777C9B82C78E21689E6695275EFDA49574B15F0B7B11372A647E064EFAD90CB0E627454455605E5A45BFC9867189D865B92826EDE894E2A22BBCA261394BA5BB2E39D33DE85BF912312DBEA5A536553D927DAE8CEC4CBA9D60E331D8A2FA708F8F0794B1A6DE128A856EEC48628F3949A8E4FABBBACA68BD7234FF82685979F34BA86AED8D483A81205887B18CG39335168236B6EB12DEE757BF81A1E350AEF95227617EF70446E
	AF133A97C05D29BEBC63FE4009BADB6643FD0AF67A5D4586C8677B338A4CFE9C9771F71189D479196DB77B6347G0BF7A6C6F11FFA90E8967A51AEFEDC9EF55718FC16D1DDDD6FA75F4CB6B3E5B1993FBC0AF171AA3FB8CF23F56687C0BE002AG6EDD0F0EE11C396F6FBFC567A523BD9ECB00492B86A2F358EEE1CBDF7620DE16861067280F6EB3C2FA6BF8D6413EF1F147B36EDEAB758963FC8FE95CA8EE7CB72AE77BB80B8F37887C37A85E2541B3BDF63BD00FAA642D99C79FF73B9D57D8B5C1447D0E621AA0
	ED8F221C97935E5B5277D5B74F315CD72F399C5FD32571F1A0982B9797454F84E3757204302F7910179B447D296BCD447EADA170C9G49GE9G4B81D60750BFAC5AC24E7B0D56C19DFEBFBD32A127B7329B62C0BA958661D545B6FAE25B18930A74776610D15F7F005AD2219A3BEF7CB30AAFD5E3770D83BE5CB72E053C45AA7673C3E2AFBBFF82F107956C7B22896C7BEF3AA451AF2BB56FFD2E8F329FF0F5F6B0792172E0F15DBEB205F31F78CCDF7C5A3A348FFDC8295009B2965103CF737E1B518FDFA70557
	BC7FDF8B707BEDA663396F2FCC09673EC5A67DB86DAED3FC1C769913FE1CF62FA9BECE7B9E752DB5508F96E1332F78B0CED3C57F2C27B61F8A698CC2A7709BA8DE1A845F8F50B80978C91F11CEC9D9A3DDE17DD757EF9FB764B15B3E51556EBDB2DBBEC50F2C185D76A9E8374EC9DCAF1C6A0BDFAFEC70444798BB3CB87F07A7D90CC15E13611D443A4BA7894DD11FF996F952A6D99946C376116F7AD82C51929A099C57737C29A9E2EF6C942405FDF7B99162F99FF7BECF73027E112092A2210A26B8DFA73D6657
	2E1336CEB1B9CB61493FB99A0F0DE7C8FE8D211B42EFC3ADB6ED9DAEDEC74A946ACED5343AE33E75EDAF4E9FE9C017BD05733D07620B43D98595GCDE12C2353C76A6884CD93C74AFF5FC451469D7BDE29B5BDA369479D03E13E3F920E0EBB785952304159D233BC04E7CB977C781B8A99E1F1D7F1BC1F4B32A9AC64276324E29AE487981C4638EB575BAF6B7350748E46C1B759B08E8ACF63B8BB621DDBBC50F04D65581C18CD7BE149F4ECBC70D30ADFBE9D9B8F84C5FFE6C2DE597B988F383793DBCF06F4BA40
	AA009C004DGC597414EGEAAFE2DCF07D36D249B8AEE0C1EAA498E06D9B70EB5BE73F486F1472673C0BC6FB721F9D47F28BB9CD3A06B6835ED7471465FA217D1BF3B176BC7C67943F79E26CF9F85007676105B360E7E730BF97681EFD93D883B439B3C6F27F18EC3B707747B56FE16F07BFFF5E7A2172E0B620EEB71EBFAFEECFBCFF4E5B2EBFFF5E32BDFE7E7C78F67D7973935B3573A7435D416B8D0A8B518C1F3A1D5E0121F3C0647BF3B63FCED21FA1DD82DB3F04F36E72G41D5C13AC960DE9D47F958AAF0
	998C8769C582B765A538A510DEA1F03F96F3F80E4015D33EC517E19CDEC6FB7BB1252B0534DD601E203899C8AF92383018EB895D1F33FFEB850FDB850F391C8D4E86F9C2771F22BEF4B970DCF3997572CB9B6AAA77B24EAB9F75EF977D0AF378E2BF4EF945D7E09E3A4274600427A46201078251E7BF748A114466772AF173ACB24EDC514E6F0F0C937B898415E1A598D41C1D6E77099D32B8E4833501E420DFF4C55CB17146479CF70B18A3936832817213E02D1C44E2B5A653AE7FECB29DCE524AF45ACF76E23D
	EE1F5BBB6125927138085AD5A350DA12D01E6D4749F895718904A07E60B42F6757D7683E30E606635906318711F7A689630A875BE2638A121FACF8FF51C66FC852DDD78B1992E065013AFBC85C87DE1C04BA5856B31B8E16F29DB03F3D520F73598A40EFCE42F9FC1B196C11454E63654C002A2BCF2B8E17622C6766446F7C9584C8DD07BF12D5508C607C884AFFFE1C74E503C2FEABG6350F272D388DCFF9E6FAB7074A9D3E6718B9A0DEE2F62A33F58D1FA2822224CFC7068FEAE6F03E27E1D017A17FEC47CAE
	4AE054BEA05B6FF9B768F6564BA3EC4307459C4F79B0664D7D8846B28DF46EAA398E62BDG6BF5A8771BFEE27B276590377D766E084CED3ED9E4BE08B2EBE3AAA22767BA0C2936DAB026DAAC787D4C067B72AC7D479E126ED6C2BC8648724FADB64A3E66F2EC2E3FC47BF36DF53131510D941FF3DDECEC34A700FD339172EC57239FFCE80EB15D8F67D86EC55F5C5A7BG350365574736B70362B32F0FED6FD99F36F7B564D54D43768E686AFB51BCB66E164CB30AF98A8C4F3FD7C019B5G39G528D11722C5C75
	74CE8D0F37B46F4F85BE7CD8297C7F004717FD11B6FDCD735EE638DE7E7371F6FD28BC34719309150AEE40B97C91BF4E61E601B3D31F910074BC01F3DB44B71C82F78F3D7B1B83692DB7A26EC90AAB06F499G73E7AAE91AB47FC69AB3789B39CC35B76AFF9702778C88D8014685C06E42933FB6F3E3E4ECB05FF54CC2744767D6BED735DA44DEBB50B33EC996641B2D43F7CB8C5F0DB1FC73EF0A657B15EE6339727B62B7AF0AEE923F67E4537EA68D23FB0D4E29A4DDF2937A6BD78268438F836E95DAD62267B7
	6CEF4329F89C4EF5377510F736167F61CE6DDA3E7111C653EB127EDA5EF3935347721BA2EB7912F1F39E13DB3110B2647EA3136F9FAC623BFCDD7924DAA64B16DEC9686A096E6879265AE60267118A347D1200569F154D07B56C7C59E3E53696FE3C17742995E883GA9G69G99GEBGF281241B4156814AG9A81ECG9E00A400E40045G4BEE4675725A1E7D1271FA19CF721A5DF3147F73D17EF4A55417FD736C723331728E6D6B0DB7B35BE238DA0A5BF98B4371733FD93EFFFC4AFE9567FFF67DB55C6F6D
	71EB38176D7AEB3892FB7C9AEEBFBD739F813D5884DD394075885CA1DAA741F53B1DF43F09FF47C365A278A605B0C47CA9AA57944559709BAE8D2E07634E5AC93C87CDF67903D83655CEECED586D7130F48725F17BC20AAA7451CDE5F487E40D5CF6016B91FD7120A53EFF32AC5A7EE1B1217C7ACEC95CDD20CDE478FD61DD91BCB6D1E27FE7464FAEE5C085693F10362952F3ED5029E67C3DC8B3B1BB086ED4E96B6930DBD5253DB41B7D6CDDA4F37BE97A3B042A34BBB62790FF86B0024A273F60D7DEDE8E35F1
	F27E0373E672C3FA2A74EC9D7D85BB3366A76CD469A9368CB157131FC95EFA466DD1D4E9E7837BC9C0F337DB0566ECA577500824CEA2698E4B4410479D827C76CEC58EBA60E5D72BDFB573CF2095E7A8A8BD9B9F4756FD6CFB0F248FB49619A453D9E93B0BAA6A2C34ED0AE8B2CC620C97411F140546254B74C2BD79B0A8D315DEB11393F4B3DB8219A7C20A335AFC2422255E7C363932821E40EB0F60F5169AC07664216A3C47BE15E7123682A3B6A04ECADB09D5833FA0631B878302F25D2A854A7D39D8AC7F2D
	2FAB5ABBC71BEEF098FEEB103A007905258BE2FF6B6BAF298F5DG781D9FC5FF746815370C7CD15D105A6915BD9ED5760C55791CC49375532A923993F69B6597C29A1E49726E24F34428264C7F82D0CB87880D09D41C74A0GGA4E6GGD0CB818294G94G88G88GC9FBB0B60D09D41C74A0GGA4E6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGAEA1GGGG
**end of data**/
}
}