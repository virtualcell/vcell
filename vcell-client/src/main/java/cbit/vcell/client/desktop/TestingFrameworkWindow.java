/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop;

/**
 * Insert the type's description here.
 * Creation date: (7/15/2004 12:00:01 PM)
 * @author: Anuradha Lakshminarayana
 */
import org.vcell.client.logicalwindow.LWTopFrame;
import org.vcell.util.UserCancelException;

import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.PopupGenerator;
@SuppressWarnings("serial")
public class TestingFrameworkWindow extends LWTopFrame implements TopLevelWindow {
	private cbit.vcell.client.TestingFrameworkWindowManager fieldTestingFrameworkWindowManager = null;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private javax.swing.JMenuItem ivjAddTSMenuItem = null;
	private javax.swing.JMenu ivjFileMenu = null;
	private javax.swing.JMenuBar ivjTestingFrameworkWindowJMenuBar = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JMenuItem ivjExitMenuItem = null;
	private javax.swing.JSeparator ivjJSeparator1 = null;
	private ChildWindowManager childWindowManager = null;
	private final String menuDesc;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TestingFrameworkWindow.this.getAddTSMenuItem())
				connEtoC1(e);
			if (e.getSource() == TestingFrameworkWindow.this.getExitMenuItem())
				connEtoC3(e);
		};
	};
/**
 * TestingFrameworkWindow constructor comment
 */
public TestingFrameworkWindow() {
	super();
	childWindowManager = new ChildWindowManager(this);
	initialize();
	menuDesc = LWTopFrame.nextSequentialDescription("Testing framework");
}


@Override
public String menuDescription() {
	return menuDesc;
}


/**
 * Comment
 */
private void addTestSuite() {
	try {
		getTestingFrameworkWindowManager().addNewTestSuiteToTF();
	} catch(UserCancelException e){
		//ignore
	}catch (Exception e) {
		PopupGenerator.showErrorDialog(this, e.getMessage(), e);
	}
}
/**
 * connEtoC1:  (AddTSMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkWindow.addTestSuite(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.addTestSuite();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ExitMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkWindow.exitApplication()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * Comment
 */
private void exitApplication() {
	getTestingFrameworkWindowManager().exitApplication();
}
/**
 * Return the AddTSMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getAddTSMenuItem() {
	if (ivjAddTSMenuItem == null) {
		try {
			ivjAddTSMenuItem = new javax.swing.JMenuItem();
			ivjAddTSMenuItem.setName("AddTSMenuItem");
			ivjAddTSMenuItem.setText("Add Test Suite");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddTSMenuItem;
}
/**
 * Return the ExitMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getExitMenuItem() {
	if (ivjExitMenuItem == null) {
		try {
			ivjExitMenuItem = new javax.swing.JMenuItem();
			ivjExitMenuItem.setName("ExitMenuItem");
			ivjExitMenuItem.setText("Exit");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExitMenuItem;
}
/**
 * Return the FileMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getFileMenu() {
	if (ivjFileMenu == null) {
		try {
			ivjFileMenu = new javax.swing.JMenu();
			ivjFileMenu.setName("FileMenu");
			ivjFileMenu.setText("File");
			ivjFileMenu.add(getAddTSMenuItem());
			ivjFileMenu.add(getJSeparator1());
			ivjFileMenu.add(getExitMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileMenu;
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
 * Return the TestingFrameworkWindowJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuBar getTestingFrameworkWindowJMenuBar() {
	if (ivjTestingFrameworkWindowJMenuBar == null) {
		try {
			ivjTestingFrameworkWindowJMenuBar = new javax.swing.JMenuBar();
			ivjTestingFrameworkWindowJMenuBar.setName("TestingFrameworkWindowJMenuBar");
			ivjTestingFrameworkWindowJMenuBar.add(getFileMenu());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTestingFrameworkWindowJMenuBar;
}
/**
 * Gets the testingFrameworkWindowManager property (cbit.vcell.client.TestingFrameworkWindowManager) value.
 * @return The testingFrameworkWindowManager property value.
 * @see #setTestingFrameworkWindowManager
 */
public cbit.vcell.client.TestingFrameworkWindowManager getTestingFrameworkWindowManager() {
	return fieldTestingFrameworkWindowManager;
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 12:00:01 PM)
 * @return cbit.vcell.client.desktop.TopLevelWindowManager
 */
public cbit.vcell.client.TopLevelWindowManager getTopLevelWindowManager() {
	return getTestingFrameworkWindowManager();
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
	getAddTSMenuItem().addActionListener(ivjEventHandler);
	getExitMenuItem().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TestingFrameworkWindow");
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(616, 603);
		setJMenuBar(getTestingFrameworkWindowJMenuBar());
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
		TestingFrameworkWindow aTestingFrameworkWindow;
		aTestingFrameworkWindow = new TestingFrameworkWindow();
		aTestingFrameworkWindow.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = aTestingFrameworkWindow.getInsets();
		aTestingFrameworkWindow.setSize(aTestingFrameworkWindow.getWidth() + insets.left + insets.right, aTestingFrameworkWindow.getHeight() + insets.top + insets.bottom);
		aTestingFrameworkWindow.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}
/**
 * Sets the testingFrameworkWindowManager property (cbit.vcell.client.TestingFrameworkWindowManager) value.
 * @param testingFrameworkWindowManager The new value for the property.
 * @see #getTestingFrameworkWindowManager
 */
public void setTestingFrameworkWindowManager(cbit.vcell.client.TestingFrameworkWindowManager testingFrameworkWindowManager) {
	cbit.vcell.client.TestingFrameworkWindowManager oldValue = fieldTestingFrameworkWindowManager;
	fieldTestingFrameworkWindowManager = testingFrameworkWindowManager;
	firePropertyChange("testingFrameworkWindowManager", oldValue, testingFrameworkWindowManager);
}
/**
 * Sets the workArea property (java.awt.Component) value.
 * @param workArea The new value for the property.
 */
public void setWorkArea(java.awt.Component c) {
	getContentPane().add(c, java.awt.BorderLayout.CENTER);
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 12:00:01 PM)
 * @param connectionStatus cbit.vcell.client.server.ConnectionStatus
 */
public void updateConnectionStatus(cbit.vcell.client.server.ConnectionStatus connectionStatus) {}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 12:00:01 PM)
 * @param freeBytes long
 * @param totalBytes long
 */
public void updateMemoryStatus(long freeBytes, long totalBytes) {}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 12:00:01 PM)
 * @param i int
 */
public void updateWhileInitializing(int i) {}

public ChildWindowManager getChildWindowManager() {
	return childWindowManager;
}
}
