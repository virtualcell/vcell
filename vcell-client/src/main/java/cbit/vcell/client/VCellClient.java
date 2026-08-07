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

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.DocumentWindowAboutBox;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.ClientLogin;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.server.VCellConnectionFactory;
import com.google.inject.Inject;
import com.install4j.api.launcher.ApplicationLauncher;
import org.vcell.api.client.VCellApiClient;
import org.vcell.util.gui.DialogUtils;
import org.vcell.api.messaging.RemoteProxyVCellConnectionFactory;
import org.vcell.api.server.ClientServerManager;
import org.vcell.api.server.ClientServerManager.InteractiveContextDefaultProvider;
import org.vcell.api.utils.Auth0ConnectionUtils;
import org.vcell.util.VCellThreadChecker;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.VCDocumentType;

import javax.swing.*;
import java.util.Hashtable;
/**
 * Insert the type's description here.
 * Creation date: (5/5/2004 1:24:03 PM)
 * @author: Ion Moraru
 */
public class VCellClient {
	private static final org.apache.logging.log4j.Logger lg =
			org.apache.logging.log4j.LogManager.getLogger(VCellClient.class);

	/** install4j id of the "Updater with silent version check" application in VCell.install4j. */
	private static final String UPDATER_APPLICATION_ID = "127";
	/** Set by the update-check task when it fails, so the GUI task can report it with an owner. */
	private static final String UPDATE_CHECK_FAILURE = "updateCheckFailure";

	private final VCellConnectionFactory vcellConnectionFactory; // injected in constructor
	private final Auth0ConnectionUtils auth0ConnectionUtils;

	private ClientServerManager clientServerManager = null;
	private StatusUpdater statusUpdater = null;
	private RequestManager requestManager = null;
	private MDIManager mdiManager = null;

	private static VCellClient instance = null;

	private static void setInstance(VCellClient instance) {
		VCellClient.instance = instance;
	}
	public static VCellClient getInstance() {
		return instance;
	}
	
	public static class CheckThreadViolationRepaintManager extends RepaintManager {
	    // it is recommended to pass the complete check
	    private final boolean completeCheck = true;

	    public synchronized void addInvalidComponent(JComponent component) {
	        checkThreadViolations(component);
	        super.addInvalidComponent(component);
	    }

	    public void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
	    	// thought to be safe to call off the Event Dispatch Thread (EDT) ... actual painting is done on EDT.
	        super.addDirtyRegion(component, x, y, w, h);
	    }

	    private void checkThreadViolations(JComponent c) {
	        if (!SwingUtilities.isEventDispatchThread() && (completeCheck || c.isShowing())) {
	            Exception exception = new Exception();
	            boolean repaint = false;
	            boolean fromSwing = false;
	            StackTraceElement[] stackTrace = exception.getStackTrace();
	            for (StackTraceElement st : stackTrace) {
	                if (repaint && st.getClassName().startsWith("javax.swing.")) {
	                    fromSwing = true;
	                }
	                if ("repaint".equals(st.getMethodName())) {
	                    repaint = true;
	                }
	            }
	            if (repaint && !fromSwing) {
	                //no problems here, since repaint() is thread safe
	                return;
	            }
	            exception.printStackTrace();
	         //   throw new RuntimeException("Swing Thread Violation");
	        }
	    }
	}


@Inject
public VCellClient(VCellConnectionFactory vcellConnectionFactory, Auth0ConnectionUtils auth0ConnectionUtils) {
	this.vcellConnectionFactory = vcellConnectionFactory;
	if (vcellConnectionFactory instanceof RemoteProxyVCellConnectionFactory){
		this.auth0ConnectionUtils = ((RemoteProxyVCellConnectionFactory)vcellConnectionFactory).getAuth0ConnectionUtils();
	} else{
		this.auth0ConnectionUtils = auth0ConnectionUtils;
	}
	VCellThreadChecker.setGUIThreadChecker(SwingUtilities::isEventDispatchThread);
}


private DocumentWindowManager createAndShowGUI(VCDocument startupDoc) {
	DocumentWindowManager windowManager = null;
	/* Create the first document desktop */
	switch (startupDoc.getDocumentType()) {
		case BIOMODEL_DOC: {
			windowManager = new BioModelWindowManager(new JPanel(), getRequestManager(), (BioModel)startupDoc);
//				((BioModelWindowManager)windowManager).preloadApps();
			break;
		}
		case MATHMODEL_DOC: {
			windowManager = new MathModelWindowManager(new JPanel(), getRequestManager(), (MathModel)startupDoc);
			break;
		}
		case GEOMETRY_DOC: {
			windowManager = new GeometryWindowManager(new JPanel(), getRequestManager(), (Geometry)startupDoc);
			break;
		}
	}	
	getMdiManager().createNewDocumentWindow(windowManager);
	/* Create database window, testing framework window, etc. */
	((ClientMDIManager)getMdiManager()).createRecyclableWindows();
	return windowManager;
}

public ClientServerManager getClientServerManager() {
	return clientServerManager;
}


public MDIManager getMdiManager() {
	return mdiManager;
}


public RequestManager getRequestManager() {
	return requestManager;
}


StatusUpdater getStatusUpdater() {
	return statusUpdater;
}

private void setClientServerManager(ClientServerManager newClientServerManager) {
	clientServerManager = newClientServerManager;
}

private void setMdiManager(MDIManager newMdiManager) {
	mdiManager = newMdiManager;
}

private void setRequestManager(RequestManager newRequestManager) {
	requestManager = newRequestManager;
}

private void setStatusUpdater(StatusUpdater newStatusUpdater) {
	statusUpdater = newStatusUpdater;
}

public void startClient(final VCDocument startupDoc, final ClientServerInfo clientServerInfo) {
	VCellLookAndFeel.setVCellLookAndFeel();

	Hashtable<String, Object> hash = new Hashtable<String, Object>();	
	AsynchClientTask task1  = new AsynchClientTask("Starting Virtual Cell", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			// start management layer
			InteractiveContextDefaultProvider defaultRequester = new VCellGuiInteractiveContextDefaultProvider();
			VCellClient.this.setClientServerManager(new ClientServerManager(vcellConnectionFactory, clientServerInfo, defaultRequester, auth0ConnectionUtils));
			VCellClient.this.setRequestManager(new ClientRequestManager(VCellClient.this));
			VCellClient.this.setMdiManager(new ClientMDIManager(VCellClient.this.getRequestManager()));
			VCellClient.setInstance(VCellClient.this);
			// start auxilliary stuff
			VCellClient.this.startStatusThreads();
			// make sure we have at least a blank document to start with
			if (startupDoc != null) {
				hashTable.put("startupDoc",startupDoc);
			} else {
				VCDocument newStartupDoc = ((ClientRequestManager)VCellClient.this.getRequestManager()).createDefaultDocument(VCDocumentType.BIOMODEL_DOC);
				hashTable.put("startupDoc",newStartupDoc);
			}
			DocumentWindowAboutBox.parseVCellVersion();
		}
	};
	AsynchClientTask task2  = new AsynchClientTask("Creating GUI", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			// fire up the GUI
			VCDocument startupDoc = (VCDocument)hashTable.get("startupDoc");

			// needs to be set first, else throw away dirty/needs paint information stored in previous instance.
			RepaintManager.setCurrentManager(new VCellClient.CheckThreadViolationRepaintManager());

			DocumentWindowManager currWindowManager = VCellClient.this.createAndShowGUI(startupDoc);
		    if (currWindowManager != null) {
		    	hashTable.put("currWindowManager", currWindowManager);
		    }

		    // dev-only introspection surface; no-op unless -Dvcell.debugBridge=true
		    org.vcell.client.debug.SwingDebugBridge.startIfEnabled();

		    // Report a failed update check now that there is a window to own the dialog.
		    // Startup continues either way — the user is told, not blocked.
		    String updateCheckFailure = (String) hashTable.get(UPDATE_CHECK_FAILURE);
		    if (updateCheckFailure != null) {
		    	DialogUtils.showWarningDialog(currWindowManager == null ? null : currWindowManager.getComponent(),
		    			"VCell could not check whether a newer version is available:\n\n"
		    			+ updateCheckFailure
		    			+ "\n\nVCell will continue with the version already installed."
		    			+ " The latest version can be downloaded from https://vcell.org.");
		    }
		}
	};
	
	AsynchClientTask task2a  = new AsynchClientTask("Checking for Updates", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			try {
				// Blocking, and scheduled before the GUI is created. The updater's dialog belongs
				// to install4j, so it can never be an LW-owned child and nothing can raise it back
				// once something covers it. Previously this launched non-blocking, so the main
				// window appeared and the Auth0 browser flow handed focus back while the update
				// alert was still up — the alert flashed and sank behind VCell. Holding startup
				// here means nothing of ours is on screen to bury it.
				//
				// Declining the update just ends the updater, which returns normally: VCell then
				// carries on starting with the installed version.
				ApplicationLauncher.launchApplication(UPDATER_APPLICATION_ID, null, true, null);
			} catch (Throwable e) {
				// A failed update check must never stop VCell from starting. Throwable rather than
				// Exception because a source build has no install4j runtime, which surfaces as a
				// LinkageError; and an AsynchClientTask that throws aborts every later task,
				// including login.
				//
				// It also must not be silent — this used to be swallowed by printStackTrace. The
				// message is handed to the GUI task so the dialog is owned by the main window,
				// rather than floating unparented here where no window exists yet.
				lg.error("VCell update check failed", e);
				if (System.getProperty("install4j.launcherId") != null) {
					hashTable.put(UPDATE_CHECK_FAILURE, e.getMessage() == null ? e.toString() : e.getMessage());
				}
			}
		}

	};

	AsynchClientTask task3a = ClientLogin.popupLogin();

	AsynchClientTask task3b = ClientLogin.loginWithAuth0(auth0ConnectionUtils);
	
	AsynchClientTask task4  = ClientLogin.connectToServer(auth0ConnectionUtils, clientServerInfo);

	// task2a (update check) runs before task2 (GUI) so the install4j update alert has the
	// screen to itself; it blocks, so login only starts once the user has dealt with it.
	AsynchClientTask[] taskArray = new AsynchClientTask[] { task1, task2a, task2, task3a, task3b, task4};
	ClientTaskDispatcher.dispatch(null, hash, taskArray);
}


public static ClientServerInfo createClientServerInfo(ClientServerInfo clientServerInfo,String userid){
	switch (clientServerInfo.getServerType()) {
		case SERVER_LOCAL: {
			return ClientServerInfo.createLocalServerInfo(userid);
		}
		case SERVER_REMOTE: {
			return ClientServerInfo.createRemoteServerInfo(clientServerInfo.getApihost(), clientServerInfo.getApiport(), clientServerInfo.getPathPrefix_v0(),userid);
		}
	};
	return null;
}
	

private void startStatusThreads() {
	StatusUpdater statusUpdater = new StatusUpdater(getMdiManager());
	setStatusUpdater(statusUpdater);
}
}
