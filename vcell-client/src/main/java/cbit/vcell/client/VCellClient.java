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

import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import org.vcell.util.UserCancelException;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.VCDocumentType;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.DocumentWindowAboutBox;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.client.server.ClientServerManager.InteractiveContextDefaultProvider;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.LoginDelegate;
import cbit.vcell.desktop.LoginManager;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.resource.ErrorUtils;
/**
 * Insert the type's description here.
 * Creation date: (5/5/2004 1:24:03 PM)
 * @author: Ion Moraru
 */
public class VCellClient {
	private ClientServerManager clientServerManager = null;
	private StatusUpdater statusUpdater = null;
	private RequestManager requestManager = null;
	private MDIManager mdiManager = null;
	
	public static class CheckThreadViolationRepaintManager extends RepaintManager {
	    // it is recommended to pass the complete check  
	    private boolean completeCheck = true;

	    public boolean isCompleteCheck() {
	        return completeCheck;
	    }

	    public void setCompleteCheck(boolean completeCheck) {
	        this.completeCheck = completeCheck;
	    }

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


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 3:56:09 PM)
 */
private VCellClient() {
	// just so we don't create this elsewhere
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 3:40:19 PM)
 */
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

/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 4:36:34 PM)
 * @return cbit.vcell.client.server.ClientServerManager
 */
public ClientServerManager getClientServerManager() {
	return clientServerManager;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 12:26:43 PM)
 * @return cbit.vcell.client.MDIManager
 */
public MDIManager getMdiManager() {
	return mdiManager;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 12:26:43 PM)
 * @return cbit.vcell.client.RequestManager
 */
public RequestManager getRequestManager() {
	return requestManager;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 2:59:53 PM)
 * @return cbit.vcell.client.StatusUpdater
 */
StatusUpdater getStatusUpdater() {
	return statusUpdater;
}

/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 4:36:34 PM)
 * @param newClientServerManager cbit.vcell.client.server.ClientServerManager
 */
private void setClientServerManager(ClientServerManager newClientServerManager) {
	clientServerManager = newClientServerManager;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 12:26:43 PM)
 * @param newMdiManager cbit.vcell.client.MDIManager
 */
private void setMdiManager(MDIManager newMdiManager) {
	mdiManager = newMdiManager;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2004 12:26:43 PM)
 * @param newRequestManager cbit.vcell.client.RequestManager
 */
private void setRequestManager(RequestManager newRequestManager) {
	requestManager = newRequestManager;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 2:59:53 PM)
 * @param newStatusUpdater cbit.vcell.client.StatusUpdater
 */
private void setStatusUpdater(StatusUpdater newStatusUpdater) {
	statusUpdater = newStatusUpdater;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 3:51:06 PM)
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
public static VCellClient startClient(final VCDocument startupDoc, final ClientServerInfo clientServerInfo) {
	/* Set Look and Feel */
	VCellLookAndFeel.setVCellLookAndFeel();

	// instantiate app
	final VCellClient vcellClient = new VCellClient();
	
	Hashtable<String, Object> hash = new Hashtable<String, Object>();	
	AsynchClientTask task1  = new AsynchClientTask("Starting Virtual Cell", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			// start management layer
			InteractiveContextDefaultProvider defaultRequester = new VCellGuiInteractiveContextDefaultProvider();
			vcellClient.setClientServerManager(new ClientServerManager(clientServerInfo, defaultRequester));
			vcellClient.setRequestManager(new ClientRequestManager(vcellClient));
			vcellClient.setMdiManager(new ClientMDIManager(vcellClient.getRequestManager()));
			// start auxilliary stuff
			vcellClient.startStatusThreads();
			// make sure we have at least a blank document to start with
			if (startupDoc != null) {
				hashTable.put("startupDoc",startupDoc);
			} else {
				VCDocument newStartupDoc = ((ClientRequestManager)vcellClient.getRequestManager()).createDefaultDocument(VCDocumentType.BIOMODEL_DOC);
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

			DocumentWindowManager currWindowManager = vcellClient.createAndShowGUI(startupDoc);
		    if (currWindowManager != null) {
		    	hashTable.put("currWindowManager", currWindowManager);
		    }
		    if (clientServerInfo.getUsername() == null) {
			    // we were not supplied login credentials; pop-up dialog
		    	VCellClient.login(vcellClient.getRequestManager(), clientServerInfo, currWindowManager);
		    }
		}
	};
	AsynchClientTask task3  = new AsynchClientTask("Connecting to Server", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
		    // try server connection
		    if (clientServerInfo.getUsername() != null) {
		    	DocumentWindowManager currWindowManager = (DocumentWindowManager)hashTable.get("currWindowManager");
			    // we were not supplied login credentials; pop-up dialog
		    	vcellClient.getRequestManager().connectToServer(currWindowManager, clientServerInfo);
		    }
		}
	}; 	

	AsynchClientTask[] taskArray = new AsynchClientTask[]{task1, task2, task3};
	ClientTaskDispatcher.dispatch(null, hash, taskArray);
	return vcellClient;
}

public static void login(final RequestManager requestManager, final ClientServerInfo clientServerInfo, final DocumentWindowManager currWindowManager){	

	final LoginManager loginManager = new LoginManager();
	LoginDelegate loginDelegate = new LoginDelegate() {
		
		public void login(final String userid, final UserLoginInfo.DigestedPassword digestedPassword)
		{
			AsynchClientTask task1 = new AsynchClientTask("connect to server", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					ClientServerInfo newClientServerInfo = createClientServerInfo(clientServerInfo, userid, digestedPassword);
					requestManager.connectToServer(currWindowManager, newClientServerInfo);
				}	
			};
			
			AsynchClientTask task2 = new AsynchClientTask("logging in", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					ConnectionStatus connectionStatus = requestManager.getConnectionStatus();
					loginManager.close();
					if(connectionStatus.getStatus() != ConnectionStatus.CONNECTED){
						VCellClient.login(requestManager,clientServerInfo, currWindowManager);
					}
					else {
						ErrorUtils.setLoginInfo(clientServerInfo.getUserLoginInfo());
					}
				}
			};
			ClientTaskDispatcher.dispatch(currWindowManager.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2} );
		}

		public void registerRequest() {
			loginManager.close();
			try {
				UserRegistrationManager.registrationOperationGUI(requestManager,	currWindowManager, clientServerInfo, LoginManager.USERACTION_REGISTER,null);
			} catch (UserCancelException e) {
				//do nothing
			} catch (Exception e) {
				e.printStackTrace();
				PopupGenerator.showErrorDialog(currWindowManager, "New user Registration error:\n"+e.getMessage());
			}
		}

		public void lostPasswordRequest(String userid) {
			try {
				ClientServerInfo newClientServerInfo = createClientServerInfo(clientServerInfo,userid,null);
				UserRegistrationManager.registrationOperationGUI(requestManager, currWindowManager, newClientServerInfo, LoginManager.USERACTION_LOSTPASSWORD,null);
			} catch (UserCancelException e) {
				//do nothing
			} catch (Exception e) {
				e.printStackTrace();
				PopupGenerator.showErrorDialog(currWindowManager, "New user Registration error:\n"+e.getMessage());
			}
		}
		
		public void userCancel(){
			loginManager.close();
			PopupGenerator.showInfoDialog(currWindowManager, 
					"Note:  The Login dialog can be accessed any time under the 'Server' main menu as 'Change User...'");
		}
	};
	
	loginManager.showLoginDialog(currWindowManager.getComponent(), currWindowManager, loginDelegate);

}


public static ClientServerInfo createClientServerInfo(ClientServerInfo clientServerInfo,String userid,DigestedPassword digestedPassword){
	switch (clientServerInfo.getServerType()) {
		case SERVER_LOCAL: {
			return ClientServerInfo.createLocalServerInfo(userid,digestedPassword);
		}
		case SERVER_REMOTE: {
			return ClientServerInfo.createRemoteServerInfo(clientServerInfo.getApihost(), clientServerInfo.getApiport(),userid,digestedPassword);
		}
	};
	return null;
}
	
/**
 * Comment
 */
private void startStatusThreads() {
	StatusUpdater statusUpdater = new StatusUpdater(getMdiManager());
	setStatusUpdater(statusUpdater);
}
}
