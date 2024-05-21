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
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.VCellConnectionFactory;
import com.google.inject.Inject;
import com.install4j.api.launcher.ApplicationLauncher;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.VCDocumentType;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;
/**
 * Insert the type's description here.
 * Creation date: (5/5/2004 1:24:03 PM)
 * @author: Ion Moraru
 */
public class VCellClient {
	private final UserRegistrationManager userRegistrationManager; // injected in constructor
	private final VCellConnectionFactory vcellConnectionFactory; // injected in constructor

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


/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 3:56:09 PM)
 */
@Inject
public VCellClient(UserRegistrationManager userRegistrationManager, VCellConnectionFactory vcellConnectionFactory) {
	this.userRegistrationManager = userRegistrationManager;
	this.vcellConnectionFactory = vcellConnectionFactory;
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
			VCellClient.this.setRequestManager(new ClientRequestManager(VCellClient.this, VCellClient.this.userRegistrationManager));
			VCellClient.this.setMdiManager(new ClientMDIManager(VCellClient.this.getRequestManager()));
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
//				final JComponent component = ((DocumentWindowManager)hashTable.get("currWindowManager")).getComponent();
//				final Window windowForComponent = SwingUtilities.windowForComponent(component);
//				final Map<String, Object> installerVariables = Variables.getInstallerVariables();
//				System.out.println(installerVariables);
				
				//-----use following in BreakPoint conditional before ApplicationLauncher.launchApplication in eclipse
//				System.setProperty("install4j.runtimeDir","/home/vcell/VCell_Test/.install4j");
//				return false;

//				ApplicationLauncher.Callback callback1 = new ApplicationLauncher.Callback() {
//			        public void exited(int exitValue) {
//			        	DialogUtils.showInfoDialog(windowForComponent, "ApplicationLauncher.launchApplication.exited(), exitValue="+exitValue);
//			        }
//			        public void prepareShutdown() {
//			        	DialogUtils.showInfoDialog(windowForComponent, "ApplicationLauncher.launchApplication.prepareShutdown()");
//			        }
//			    }
				String[] install4jArgs = null;
				boolean blocking = false;
				ApplicationLauncher.Callback callback = null;
				ApplicationLauncher.launchApplication("127", install4jArgs, blocking, callback);

			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	};

	AsynchClientTask task2b = new AsynchClientTask("Login With Auth0...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			Path appState = Path.of(ResourceUtil.getVcellHome().getAbsolutePath(), "/state.json");
			boolean showPopupMenu = true;
			try{
				if (Files.exists(appState)) {
					String json = Files.readString(appState);
					showPopupMenu = false;
				}
			}
			catch(Exception e){
				throw new Exception("Failed to read state file");
			}
			if(showPopupMenu){
				int accept = JOptionPane.showConfirmDialog(null,
						"VCell is going to redirect you to your browser to login. Do you wish to proceed?");
				if(accept==JOptionPane.NO_OPTION || accept==JOptionPane.CLOSED_OPTION || accept==JOptionPane.CANCEL_OPTION){
					return;
				}
			}
			vcellConnectionFactory.auth0SignIn();
			DocumentWindowManager currWindowManager = (DocumentWindowManager)hashTable.get("currWindowManager");
			int numberOfPolls = 0;
			while(!vcellConnectionFactory.isVCellIdentityMappedToAuth0Identity()){
				if (numberOfPolls==20) {
					return;
				}
				numberOfPolls++;
				Thread.sleep(5000); // Poll every 5 seconds
			}

			ClientServerInfo newClientServerInfo = createClientServerInfo(clientServerInfo, vcellConnectionFactory.getAuth0MappedUser(), null);
			getRequestManager().connectToServer(currWindowManager, newClientServerInfo);
			Files.createFile(appState);
		}
	};
	
	AsynchClientTask task3  = new AsynchClientTask("Connecting to Server", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
		    // try server connection
		    if (clientServerInfo.getUsername() != null) {
		    	DocumentWindowManager currWindowManager = (DocumentWindowManager)hashTable.get("currWindowManager");
			    // we were not supplied login credentials; pop-up dialog
		    	VCellClient.this.getRequestManager().connectToServer(currWindowManager, clientServerInfo);
		    }
		}
	}; 	

	AsynchClientTask[] taskArray = new AsynchClientTask[] { task1, task2,  task2a, task2b, task3};
	ClientTaskDispatcher.dispatch(null, hash, taskArray);
}

public void loginAuth0(final RequestManager requestManager, final ClientServerInfo clientServerInfo, final DocumentWindowManager documentWindowManager){
	final LoginManager loginManager = new LoginManager();
	LoginDelegate loginDelegate = new LoginDelegate() {

		// legacy login to map the user to an Auth0 account
		public void login(final String userid, final UserLoginInfo.DigestedPassword digestedPassword)
		{
			AsynchClientTask task1 = new AsynchClientTask("connect to server", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					ClientServerInfo newClientServerInfo = createClientServerInfo(clientServerInfo, userid, digestedPassword);
					vcellConnectionFactory.mapVCellIdentityToAuth0Identity(newClientServerInfo.getUserLoginInfo());
					requestManager.connectToServer(documentWindowManager, newClientServerInfo);
				}
			};

			AsynchClientTask task2 = new AsynchClientTask("logging in", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					ConnectionStatus connectionStatus = requestManager.getConnectionStatus();
					loginManager.close();
					if(connectionStatus.getStatus() != ConnectionStatus.CONNECTED){
						VCellClient.this.loginAuth0(requestManager,clientServerInfo, documentWindowManager);
					}
					else {
						ErrorUtils.setLoginInfo(clientServerInfo.getUserLoginInfo());
					}
				}
			};
			ClientTaskDispatcher.dispatch(documentWindowManager.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2} );
		}

		public void registerRequest() {
			loginManager.close();
			// They don't have an existing VCell user, so we automatically create one based on what Auth0 provides us, or let them input a VCell username
		}

		public void lostPasswordRequest(String userid) {
			// Lost legacy password
		}

		public void userCancel(){
			loginManager.close();
			PopupGenerator.showInfoDialog(documentWindowManager,
					"Note:  The Login dialog can be accessed any time under the 'Server' main menu as 'Change User...'");
		}
	};

	loginManager.showLoginDialog(documentWindowManager.getComponent(), documentWindowManager, loginDelegate);

}

public void login(final RequestManager requestManager, final ClientServerInfo clientServerInfo, final DocumentWindowManager currWindowManager){

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
						VCellClient.this.login(requestManager,clientServerInfo, currWindowManager);
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
				VCellClient.this.userRegistrationManager.registrationOperationGUI(requestManager,	currWindowManager, clientServerInfo, LoginManager.USERACTION_REGISTER,null);
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
				VCellClient.this.userRegistrationManager.registrationOperationGUI(requestManager, currWindowManager, newClientServerInfo, LoginManager.USERACTION_LOSTPASSWORD,null);
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

public ClientServerInfo createClientServerInfo(ClientServerInfo clientServerInfo,String userid,DigestedPassword digestedPassword){
	switch (clientServerInfo.getServerType()) {
		case SERVER_LOCAL: {
			return ClientServerInfo.createLocalServerInfo(userid,digestedPassword);
		}
		case SERVER_REMOTE: {
			return ClientServerInfo.createRemoteServerInfo(clientServerInfo.getApihost(), clientServerInfo.getApiport(), clientServerInfo.getPathPrefix_v0(),userid,digestedPassword);
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
