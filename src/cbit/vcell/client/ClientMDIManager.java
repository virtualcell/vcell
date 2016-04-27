/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import org.vcell.util.BeanUtils;
import org.vcell.util.document.CurateSpec;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;
import org.vcell.util.gui.GlassPane;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.desktop.BNGWindow;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.desktop.DocumentWindowAboutBox;
import cbit.vcell.client.desktop.TestingFrameworkWindow;
import cbit.vcell.client.desktop.TestingFrameworkWindowPanel;
import cbit.vcell.client.desktop.TopLevelWindow;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.field.gui.FieldDataGUIPanel;
import cbit.vcell.field.gui.FieldDataWindow;
/**
 * Insert the type's description here.
 * Creation date: (5/24/2004 10:58:42 AM)
 * @author: Ion Moraru
 */
public class ClientMDIManager implements MDIManager {
	private static final Dimension JFRAME_SIZE = new Dimension(1024, 768);
	public final static String DATABASE_WINDOW_ID = "DatabaseWindow";
	public final static String TESTING_FRAMEWORK_WINDOW_ID = "TestingFrameworkWindow";
	public final static String BIONETGEN_WINDOW_ID = "BioNetGenWindow";
	public final static String FIELDDATA_WINDOW_ID = "FieldDataWindow";
	private RequestManager requestManager = null;
	private Hashtable<String, TopLevelWindow> windowsHash = new Hashtable<String, TopLevelWindow>();
	private Hashtable<String, TopLevelWindowManager> managersHash = new Hashtable<String, TopLevelWindowManager>();
	private int numCreatedDocumentWindows = 0;

	private WindowAdapter windowListener = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			getRequestManager().closeWindow(((TopLevelWindow)e.getWindow()).getTopLevelWindowManager().getManagerID(), true);
		}
	};

/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 10:59:51 AM)
 * @param vcellClient cbit.vcell.client.VCellClient
 */
public ClientMDIManager(RequestManager requestManager) {
	setRequestManager(requestManager);
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 11:45:55 PM)
 * @param windowID java.lang.String
 */
public JFrame blockWindow(final String windowID) {
	if (haveWindow(windowID)) {
		JFrame f = (JFrame)getWindowsHash().get(windowID);
		return (JFrame)blockWindow(f);
	}
	return null;
}

public static Window blockWindow(Component component) {
	Window window = (Window) BeanUtils.findTypeParentOfComponent(component, Window.class);
	if (window instanceof RootPaneContainer) {
		GlassPane glass = new GlassPane(true);
		((RootPaneContainer)window).setGlassPane(glass);
		glass.setVisible(true);
	}
	return window;
}

/**
 * Insert the method's description here.
 * Creation date: (5/25/2004 2:03:47 AM)
 * @return int
 */
void cleanup() {
	if (numberOfWindowsShowing() == 0) {
		// last window was closed, hash still contains recyclables
		Enumeration<TopLevelWindowManager> recyclableWindowManagers = getManagersHash().elements();
		while (recyclableWindowManagers.hasMoreElements()) {
			String managerID = recyclableWindowManagers.nextElement().getManagerID();
			JFrame window = (JFrame)getWindowsHash().get(managerID);
			getManagersHash().remove(managerID);
			getWindowsHash().remove(managerID);
			window.removeWindowListener(windowListener);
			window.dispose();
		}
		// dispose of reusable dialogs
		// getDatabaseWindowManager().cleanup();
		// we are clean now... all our threads *should* die and JVM should exit if no other application running
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 11:20:58 AM)
 * @param windowID java.lang.String
 */
public int closeWindow(String windowID) {
	if (haveWindow(windowID)) {
		JFrame window = (JFrame)getWindowsHash().get(windowID);
		if (window.isShowing()) {
			TopLevelWindowManager manager = (TopLevelWindowManager)getManagersHash().get(windowID);
			if (((TopLevelWindowManager)getManagersHash().get(windowID)).isRecyclable()) {
				// just hide the window
				window.setVisible(false);
			} else {
				// dispose both window and manager
				getManagersHash().remove(windowID);
				getWindowsHash().remove(windowID);
				if (manager instanceof DocumentWindowManager || manager instanceof TestingFrameworkWindowManager) {
					getRequestManager().getAsynchMessageManager().removeSimStatusListener((SimStatusListener)manager);
					getRequestManager().getAsynchMessageManager().removeExportListener((cbit.rmi.event.ExportListener)manager);
					getRequestManager().getAsynchMessageManager().removeDataJobListener((cbit.rmi.event.DataJobListener)manager);
				}
				window.removeWindowListener(windowListener);
				window.dispose();
			}
		}
	}
	// others need to know... (e.g. last window was closed)
	return numberOfWindowsShowing();
}

/**
 * Insert the method's description here.
 * Creation date: (5/27/2006 4:10:10 PM)
 * @return java.lang.String
 * @param version cbit.sql.Version
 */
public static String createCanonicalTitle(VCDocument vcDocument) {

	Version version = vcDocument.getVersion();
	int docType = vcDocument.getDocumentType();
	String docName = (version != null?version.getName():(vcDocument.getName()==null?"NoName":vcDocument.getName()+" (NoVersion)"));
	java.util.Date docDate = (version != null?version.getDate():null);
	VersionFlag versionFlag = (version != null?version.getFlag():null);
	String title =
		(versionFlag != null && versionFlag.compareEqual(VersionFlag.Archived)? "("+CurateSpec.CURATE_TYPE_STATES[CurateSpec.ARCHIVE]+") ":"")+
		(versionFlag != null && versionFlag.compareEqual(VersionFlag.Published)?"("+CurateSpec.CURATE_TYPE_STATES[CurateSpec.PUBLISH]+") ":"")+
		(docType == VCDocument.BIOMODEL_DOC?"BIOMODEL: ":"")+
		(docType == VCDocument.MATHMODEL_DOC?"MATHMODEL: ":"")+
		(docType == VCDocument.GEOMETRY_DOC?"GEOMETRY: ":"")+
		docName+" "+
		"("+(docDate != null?docDate.toString():"NoDate")+")";
	title +=  " -- VCell " + DocumentWindowAboutBox.getVERSION_NO() + " (build " + DocumentWindowAboutBox.getBUILD_NO() + ")";
	return title;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 9:07:12 PM)
 */
private DocumentWindow createDocumentWindow() {
	DocumentWindow documentWindow = new DocumentWindow();
	// stagger 90% screen size windows
	documentWindow.setSize(JFRAME_SIZE);
	BeanUtils.centerOnScreen(documentWindow);
	Point p = documentWindow.getLocation();
	int numDocWindow = Math.max(0, getWindowsHash().size() - 3);
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int offset = 20;
	int newX = p.x + offset * numDocWindow;
	int newY = p.y + offset * numDocWindow;
	if (newX < screenSize.width && newY < screenSize.height) {
		documentWindow.setLocation(newX, newY);
	}
//	if (getNewlyCreatedDesktops() == 0) {
//		// first window
//		// cbit.util.BeanUtils.attemptMaximize(documentWindow);
//	}
	return documentWindow;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 11:20:58 AM)
 * @param vcDocument cbit.vcell.document.VCDocument
 */
public void createNewDocumentWindow(final DocumentWindowManager windowManager) {
	// used for opening new document windows
	// assumes caller checked for having this document already open

	// make the window
	DocumentWindow documentWindow = createDocumentWindow();
	documentWindow.setWorkArea(windowManager.getComponent());

	// keep track of things
	String windowID = windowManager.getManagerID();
	getWindowsHash().put(windowID, documentWindow);
	getManagersHash().put(windowID, windowManager);
	// wire manager to events
	getRequestManager().getAsynchMessageManager().addSimStatusListener(windowManager);
	getRequestManager().getAsynchMessageManager().addExportListener(windowManager);
	getRequestManager().getAsynchMessageManager().addDataJobListener(windowManager);
	// get the window ready
	setCanonicalTitle(windowID);
	documentWindow.setWindowManager(windowManager);
	documentWindow.addWindowListener(windowListener); // listen for event when user clicks window close button and send request to manager
	setNewlyCreatedDesktops(getNumCreatedDocumentWindows() + 1);
	getRequestManager().updateStatusNow(); // initialize status bar with current status (also syncs all other windows)
	// done
	documentWindow.setVisible(true);
}

/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 12:55:28 PM)
 */
void createRecyclableWindows() {
	// we make'em only once, during application initialization sequence; happens before first connection attempt
	/* database window */
	if (! getWindowsHash().containsKey(DATABASE_WINDOW_ID)) {
		// make the manager
		DatabaseWindowManager windowManager = new DatabaseWindowManager(null, getRequestManager());
		// keep track of things
		getManagersHash().put(DATABASE_WINDOW_ID, windowManager);
	}
	/* testing framework */
	//...
	//cbit.vcell.server.User currentUser = null;
	//if (getRequestManager().getDocumentManager()!=null){
		//currentUser = getRequestManager().getDocumentManager().getUser();
	//}
	if (! getWindowsHash().containsKey(TESTING_FRAMEWORK_WINDOW_ID) /*&& currentUser!=null && currentUser.isTestAccount()*/) {
		// make the window
		TestingFrameworkWindow testingFrameworkWindow = new TestingFrameworkWindow();
		TestingFrameworkWindowPanel testingFrameworkWindowPanel = new TestingFrameworkWindowPanel();
		testingFrameworkWindow.setWorkArea(testingFrameworkWindowPanel);
		testingFrameworkWindow.setSize(JFRAME_SIZE);
		testingFrameworkWindow.setIconImage(VCellIcons.getJFrameImageIcon());
		BeanUtils.centerOnScreen(testingFrameworkWindow);
		// make the manager
		TestingFrameworkWindowManager windowManager = new TestingFrameworkWindowManager(testingFrameworkWindowPanel, getRequestManager());
		// keep track of things
		getWindowsHash().put(TESTING_FRAMEWORK_WINDOW_ID, testingFrameworkWindow);
		getManagersHash().put(TESTING_FRAMEWORK_WINDOW_ID, windowManager);
		// get window ready
		setCanonicalTitle(TESTING_FRAMEWORK_WINDOW_ID);
		testingFrameworkWindow.setTestingFrameworkWindowManager(windowManager);
		testingFrameworkWindowPanel.setTestingFrameworkWindowManager(windowManager);
		blockWindow(TESTING_FRAMEWORK_WINDOW_ID);

		// wiring up export and simStatus listener to update viewing simulations
		getRequestManager().getAsynchMessageManager().addSimStatusListener(windowManager);
		getRequestManager().getAsynchMessageManager().addExportListener(windowManager);
		getRequestManager().getAsynchMessageManager().addDataJobListener(windowManager);

		// listen for event when user clicks window close button
		testingFrameworkWindow.addWindowListener(windowListener);
	}

	if (! getWindowsHash().containsKey(BIONETGEN_WINDOW_ID) ) {
		// make the window
		BNGWindow bngWindow = new BNGWindow();
		cbit.vcell.client.bionetgen.BNGOutputPanel bngOutputPanel = new cbit.vcell.client.bionetgen.BNGOutputPanel();
		bngWindow.setWorkArea(bngOutputPanel);
		bngWindow.setSize(JFRAME_SIZE);
		bngWindow.setIconImage(VCellIcons.getJFrameImageIcon());
		BeanUtils.centerOnScreen(bngWindow);
		// make the manager
		BNGWindowManager windowManager = new BNGWindowManager(bngOutputPanel, getRequestManager());
		// keep track of things
		getWindowsHash().put(BIONETGEN_WINDOW_ID, bngWindow);
		getManagersHash().put(BIONETGEN_WINDOW_ID, windowManager);
		// get window ready
		setCanonicalTitle(BIONETGEN_WINDOW_ID);
		// set bngService for bngOutputPanel - thro' clientServerManager or separate manager for bionetgen?
		bngWindow.setBngWindowManager(windowManager);
		bngOutputPanel.setBngWindowManager(windowManager);
//		blockWindow(BIONETGEN_WINDOW_ID);

		// listen for event when user clicks window close button
		bngWindow.addWindowListener(windowListener);
	}

	if (! getWindowsHash().containsKey(FIELDDATA_WINDOW_ID) ) {
		// make the window
		FieldDataWindow fieldDataWindow = new FieldDataWindow();
		FieldDataGUIPanel fieldDataGUIPanel = new FieldDataGUIPanel();
		fieldDataWindow.setWorkArea(fieldDataGUIPanel);
		fieldDataWindow.pack();
		fieldDataWindow.setIconImage(VCellIcons.getJFrameImageIcon());
		BeanUtils.centerOnScreen(fieldDataWindow);
		// make the manager
		FieldDataWindowManager fieldDataWindowManager =
			new FieldDataWindowManager(fieldDataGUIPanel,getRequestManager());
		// keep track of things
		getWindowsHash().put(FIELDDATA_WINDOW_ID, fieldDataWindow);
		getManagersHash().put(FIELDDATA_WINDOW_ID, fieldDataWindowManager);
		// get window ready
		setCanonicalTitle(FIELDDATA_WINDOW_ID);
		fieldDataWindow.setFieldDataWindowManager(fieldDataWindowManager);
		fieldDataGUIPanel.setFieldDataWindowManager(fieldDataWindowManager);
		blockWindow(FIELDDATA_WINDOW_ID);
		// listen for event when user clicks window close button
		fieldDataWindow.addWindowListener(windowListener);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 8:57:53 PM)
 * @return cbit.vcell.client.desktop.DatabaseWindowManager
 */
public BNGWindowManager getBNGWindowManager() {
	return (BNGWindowManager)getManagersHash().get(BIONETGEN_WINDOW_ID);
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 8:57:53 PM)
 * @return cbit.vcell.client.desktop.DatabaseWindowManager
 */
public DatabaseWindowManager getDatabaseWindowManager() {
	return (DatabaseWindowManager)getManagersHash().get(DATABASE_WINDOW_ID);
}


public FieldDataWindowManager getFieldDataWindowManager() {
	return (FieldDataWindowManager)getManagersHash().get(FIELDDATA_WINDOW_ID);
}
/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 12:48:08 PM)
 * @return java.util.Hashtable
 */
private Hashtable<String, TopLevelWindowManager> getManagersHash() {
	return managersHash;
}


/**
 * Return the value of the field newlyCreatedDesktops
 */
public int getNumCreatedDocumentWindows() {
	return numCreatedDocumentWindows;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 11:13:23 AM)
 * @return cbit.vcell.client.RequestManager
 */
private RequestManager getRequestManager() {
	return requestManager;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 8:57:53 PM)
 * @return cbit.vcell.client.desktop.DatabaseWindowManager
 */
public TestingFrameworkWindowManager getTestingFrameworkWindowManager() {
	return (TestingFrameworkWindowManager)getManagersHash().get(TESTING_FRAMEWORK_WINDOW_ID);
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 7:55:59 PM)
 * @return cbit.vcell.client.desktop.TopLevelWindowManager
 * @param windowID java.lang.String
 */
public TopLevelWindowManager getWindowManager(java.lang.String windowID) {
	return (TopLevelWindowManager)getManagersHash().get(windowID);
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 8:08:49 PM)
 * @return java.util.Enumeration
 */
public Enumeration<TopLevelWindowManager> getWindowManagers() {
	return getManagersHash().elements();
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 11:21:31 AM)
 * @return java.util.Hashtable
 */
private Hashtable<String, TopLevelWindow> getWindowsHash() {
	return windowsHash;
}

public TopLevelWindowManager getFocusedWindowManager() {
	Set<Entry<String, TopLevelWindow>> entrySet = getWindowsHash().entrySet();
	Iterator<Entry<String, TopLevelWindow>> iter = entrySet.iterator();
	TopLevelWindowManager showingTopLevelWindowManager = null;
	TopLevelWindowManager firstTopLevelWindowManager = null;
	while (iter.hasNext()) {
		Entry<String, TopLevelWindow> entry = iter.next();
		JFrame window = (JFrame)entry.getValue();
		TopLevelWindowManager topLevelWindowManager = getManagersHash().get(entry.getKey());
		if (window == KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow()) {
			return topLevelWindowManager;
		}
		if (firstTopLevelWindowManager == null) {
			firstTopLevelWindowManager = topLevelWindowManager;
		}
		if (window.isShowing() && showingTopLevelWindowManager == null) {
			showingTopLevelWindowManager = topLevelWindowManager;
		}
	}
	// if none has focus, pick one that are showing
	if (showingTopLevelWindowManager != null) {
		return showingTopLevelWindowManager;
	}

	// pick anything
	return firstTopLevelWindowManager;
}

/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 8:29:15 PM)
 * @return boolean
 * @param windowID java.lang.String
 */
public boolean haveWindow(java.lang.String windowID) {
	return getWindowsHash().containsKey(windowID);
}


/**
 * Insert the method's description here.
 * Creation date: (5/25/2004 2:03:47 AM)
 * @return int
 */
private int numberOfWindowsShowing() {
	int i = 0;
	Enumeration<TopLevelWindow> en = getWindowsHash().elements();
	while (en.hasMoreElements()) {
		i = ((JFrame)en.nextElement()).isShowing() ? i + 1 : i;
	}
	return i;
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/2005 5:28:51 PM)
 */
public void refreshRecyclableWindows() {
	createRecyclableWindows();
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 1:21:27 AM)
 * @param windowID java.lang.String
 */
public void setCanonicalTitle(java.lang.String windowID) {
	if (! haveWindow(windowID)) {
		return;
	}
	String windowTitle = null;
	TopLevelWindowManager manager = getWindowManager(windowID);
	if (manager instanceof DocumentWindowManager) {
		windowTitle =
			createCanonicalTitle(
				((DocumentWindowManager)manager).getVCDocument());
	} else if (manager instanceof DatabaseWindowManager) {
		windowTitle = "Database Manager";
	} else if (manager instanceof TestingFrameworkWindowManager) {
		windowTitle = "VCell Testing Framework";
	} else if (manager instanceof BNGWindowManager) {
		windowTitle = "BioNetGen";
	} else if (manager instanceof FieldDataWindowManager) {
		windowTitle = "Field Data Manager";
	}
	((JFrame)getWindowsHash().get(windowID)).setTitle(windowTitle);
}

/**
 * Set the value of the field newlyCreatedDesktops
 */
private void setNewlyCreatedDesktops(int aNewlyCreatedDesktops) {
	numCreatedDocumentWindows = aNewlyCreatedDesktops;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 11:13:23 AM)
 * @param newRequestManager cbit.vcell.client.RequestManager
 */
private void setRequestManager(RequestManager newRequestManager) {
	requestManager = newRequestManager;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 11:20:58 AM)
 * @param windowID java.lang.String
 */
public void showWindow(java.lang.String windowID) {
	if (haveWindow(windowID)) {
		JFrame frame = (JFrame)getWindowsHash().get(windowID);
		frame.setState(Frame.NORMAL);
		frame.setVisible(true);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 11:45:55 PM)
 * @param windowID java.lang.String
 */
public void unBlockWindow(final String windowID) {
	if (haveWindow(windowID)) {
		JFrame f = (JFrame)getWindowsHash().get(windowID);
		unBlockWindow(f);
	}
}

public static void unBlockWindow(Component component) {
	Window window = (Window) BeanUtils.findTypeParentOfComponent(component, Window.class);
	if (window instanceof RootPaneContainer) {
		((RootPaneContainer)window).getGlassPane().setVisible(false);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 3:15:04 PM)
 * @param connectionStatus cbit.vcell.client.server.ConnectionStatus
 */
public void updateConnectionStatus(ConnectionStatus connectionStatus) {
	if (connectionStatus.getStatus() == ConnectionStatus.CONNECTED) {
//		unBlockWindow(DATABASE_WINDOW_ID);
		unBlockWindow(TESTING_FRAMEWORK_WINDOW_ID);
//		unBlockWindow(BIONETGEN_WINDOW_ID);
		unBlockWindow(FIELDDATA_WINDOW_ID);
	} else {
//		blockWindow(DATABASE_WINDOW_ID);
		blockWindow(TESTING_FRAMEWORK_WINDOW_ID);
//		blockWindow(BIONETGEN_WINDOW_ID);
		blockWindow(FIELDDATA_WINDOW_ID);
	}
	Enumeration<TopLevelWindow> windows = getWindowsHash().elements();
	while (windows.hasMoreElements()) {
		TopLevelWindow window = windows.nextElement();
		window.updateConnectionStatus(connectionStatus);
	}
	Enumeration<TopLevelWindowManager> managers = getWindowManagers();
	while (managers.hasMoreElements()) {
		TopLevelWindowManager topLevelWindowManager = managers.nextElement();
		if (topLevelWindowManager instanceof DocumentWindowManager) {
			((DocumentWindowManager) topLevelWindowManager).updateConnectionStatus(connectionStatus);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:17:39 AM)
 * @param oldID java.lang.String
 * @param newID java.lang.String
 */
public void updateDocumentID(java.lang.String oldID, java.lang.String newID) {
	Objects.requireNonNull(oldID);
	Objects.requireNonNull(newID);
	TopLevelWindow window = windowsHash.get(oldID);
	TopLevelWindowManager manager = managersHash.get(oldID);
	Objects.requireNonNull(window);
	Objects.requireNonNull(manager);
	windowsHash.remove(oldID);
	managersHash.remove(oldID);
	windowsHash.put(newID, window);//Null pointer exception on VCellSupport 4/27/2016 -- added requireNonNull statements for localization of problem
	managersHash.put(newID, manager);
	setCanonicalTitle(newID);
}

/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 3:15:04 PM)
 * @param freeBytes long
 * @param totalBytes long
 */
public void updateMemoryStatus(long freeBytes, long totalBytes) {
	Enumeration<TopLevelWindow> windows = getWindowsHash().elements();
	while (windows.hasMoreElements()) {
		TopLevelWindow window = windows.nextElement();
		window.updateMemoryStatus(freeBytes, totalBytes);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 3:15:04 PM)
 * @param progress int
 */
public void updateWhileInitializing(int progress) {
	Enumeration<TopLevelWindow> windows = getWindowsHash().elements();
	while (windows.hasMoreElements()) {
		TopLevelWindow window = windows.nextElement();
		window.updateWhileInitializing(progress);
	}
}
}
