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
import org.vcell.api.messaging.RemoteProxyVCellConnectionFactory;
import org.vcell.api.server.ClientServerManager;
import org.vcell.api.server.ClientServerManager.InteractiveContextDefaultProvider;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.ClientLogin;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.server.VCellConnectionFactory;
import com.google.inject.Inject;
import com.install4j.api.launcher.ApplicationLauncher;
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
	private final RemoteProxyVCellConnectionFactory vcellConnectionFactory; // injected in constructor

	private ClientServerManager clientServerManager = null;
	private StatusUpdater statusUpdater = null;
	private RequestManager requestManager = null;
	private MDIManager mdiManager = null;

	private static VCellClient instance = null;

	public static void setInstance(VCellClient instance) {
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
public VCellClient(VCellConnectionFactory vcellConnectionFactory) {
	if (!(vcellConnectionFactory instanceof RemoteProxyVCellConnectionFactory)){
		throw new IllegalStateException("VCellConnectionFactory must be an instance of RemoteProxyVCellConnectionFactory");
	}
	this.vcellConnectionFactory = (RemoteProxyVCellConnectionFactory) vcellConnectionFactory;
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
			VCellClient.this.setClientServerManager(new ClientServerManager(vcellConnectionFactory, clientServerInfo, defaultRequester));
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
		    
		}
	};
	
	AsynchClientTask task2a  = new AsynchClientTask("Checking for Updates", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			try {
				String[] install4jArgs = null;
				boolean blocking = false;
				ApplicationLauncher.Callback callback = null;
				ApplicationLauncher.launchApplication("127", install4jArgs, blocking, callback);

			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	};

	AsynchClientTask task3a = ClientLogin.popupLogin();

	AsynchClientTask task3b = ClientLogin.loginWithAuth0(vcellConnectionFactory.getAuth0ConnectionUtils());
	
	AsynchClientTask task4  = ClientLogin.connectToServer(vcellConnectionFactory.getAuth0ConnectionUtils(), clientServerInfo);

	AsynchClientTask[] taskArray = new AsynchClientTask[] { task1, task2,  task2a, task3a, task3b, task4};
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
