package cbit.vcell.client;

import java.awt.event.*;
import java.util.Hashtable;
import cbit.vcell.desktop.*;
import cbit.vcell.geometry.*;
import cbit.vcell.mathmodel.*;
import cbit.vcell.server.*;
import cbit.vcell.client.server.*;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.desktop.*;
import javax.swing.*;
import org.vcell.util.BeanUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCDocument;
import cbit.vcell.biomodel.*;
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
	private boolean isApplet = false;
	
	private static class CheckThreadViolationRepaintManager extends RepaintManager {
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
	        checkThreadViolations(component);
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
private void createAndShowGUI(VCDocument startupDoc, boolean fromApplet) {
	try {
		if (!fromApplet) {
			/* Set Look and Feel */
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		/* Create the first document desktop */
		DocumentWindowManager windowManager = null;
		switch (startupDoc.getDocumentType()) {
			case VCDocument.BIOMODEL_DOC: {
				windowManager = new BioModelWindowManager(new JPanel(), getRequestManager(), (BioModel)startupDoc, getMdiManager().getNewlyCreatedDesktops());
				((BioModelWindowManager)windowManager).preloadApps();
				break;
			}
			case VCDocument.MATHMODEL_DOC: {
				windowManager = new MathModelWindowManager(new JPanel(), getRequestManager(), (MathModel)startupDoc, getMdiManager().getNewlyCreatedDesktops());
				break;
			}
			case VCDocument.GEOMETRY_DOC: {
				windowManager = new GeometryWindowManager(new JPanel(), getRequestManager(), (Geometry)startupDoc, getMdiManager().getNewlyCreatedDesktops());
				break;
			}
		}	
		getMdiManager().createNewDocumentWindow(windowManager);
		/* Create database window, testing framework window, etc. */
		((ClientMDIManager)getMdiManager()).createRecyclableWindows();
	} catch (Throwable exc) {
		handleException (exc);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 4:36:34 PM)
 * @return cbit.vcell.client.server.ClientServerManager
 */
ClientServerManager getClientServerManager() {
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
RequestManager getRequestManager() {
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
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Insert the method's description here.
 * Creation date: (5/20/2005 12:19:36 PM)
 * @return boolean
 */
public boolean isApplet() {
	return isApplet;
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
	// instantiate app
	final VCellClient vcellClient = new VCellClient();
	
	Hashtable<String, Object> hash = new Hashtable<String, Object>();	
	AsynchClientTask task1  = new AsynchClientTask("Starting Virtual Cell", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			// start management layer
			vcellClient.setClientServerManager(new ClientServerManager());
			vcellClient.getClientServerManager().setClientServerInfo(clientServerInfo);
			vcellClient.setRequestManager(new ClientRequestManager(vcellClient));
			vcellClient.setMdiManager(new ClientMDIManager(vcellClient.getRequestManager()));
			// start auxilliary stuff
			vcellClient.startStatusThreads();
			// make sure we have at least a blank document to start with
			if (startupDoc != null) {
				hashTable.put("startupDoc",startupDoc);
			} else {
				VCDocument newStartupDoc = ((ClientRequestManager)vcellClient.getRequestManager()).createDefaultDocument(VCDocument.BIOMODEL_DOC);
				hashTable.put("startupDoc",newStartupDoc);
			}
		}
	};
	AsynchClientTask task2  = new AsynchClientTask("Creating GUI", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			// fire up the GUI
			VCDocument startupDoc = (VCDocument)hashTable.get("startupDoc");
		    vcellClient.createAndShowGUI(startupDoc, false);
		    RepaintManager.setCurrentManager(new VCellClient.CheckThreadViolationRepaintManager());		    
		}
	};
	AsynchClientTask task3  = new AsynchClientTask("Connecting to Server", clientServerInfo.getUsername() == null ? AsynchClientTask.TASKTYPE_SWING_BLOCKING : AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {		
			String build = System.getProperty(PropertyLoader.vcellSoftwareVersion);
			if (build != null){
				DocumentWindowAboutBox.BUILD_NO = build;
			}
		    // try server connection
		    if (clientServerInfo.getUsername() == null) {
			    // we were not supplied login credentials; pop-up dialog
		    	VCellClient.login(vcellClient.getRequestManager(), clientServerInfo);
		    } else {
				vcellClient.getRequestManager().connectToServer(clientServerInfo);
		    }
		}
	}; 	

	AsynchClientTask[] taskArray = new AsynchClientTask[]{task1, task2, task3};
	ClientTaskDispatcher.dispatch(null, hash, taskArray);
	return vcellClient;
}

public static void login(final RequestManager requestManager,final ClientServerInfo clientServerInfo){	
	final LoginDialog loginDialog = new LoginDialog(null);
	loginDialog.setLoggedInUser(null);
	loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	loginDialog.pack();
	loginDialog.setResizable(false);
	BeanUtils.centerOnScreen(loginDialog);
	ActionListener listener = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			if (evt.getActionCommand().equals(LoginDialog.USERACTION_LOGIN)) {
				new Thread(new Runnable() {
					public void run() {
						try {
							ClientServerInfo newClientServerInfo = createClientServerInfo(
									clientServerInfo, loginDialog.getUser(),
									loginDialog.getPassword());
							requestManager.connectToServer(newClientServerInfo);
						} catch (RuntimeException e) {
							e.printStackTrace();
							PopupGenerator.showErrorDialog("Login error:\n"+e.getMessage());
						}finally{
							ConnectionStatus connectionStatus = requestManager.getConnectionStatus();
							if(connectionStatus.getStatus() != ConnectionStatus.CONNECTED){
								SwingUtilities.invokeLater(new Runnable(){public void run(){//}});
									VCellClient.login(requestManager,clientServerInfo);
								}});
								//new Thread(new Runnable(){public void run(){VCellClient.login(requestManager,clientServerInfo);}}).start();
							}
						}
					}
				}).start();
			}else if(evt.getActionCommand().equals(LoginDialog.USERACTION_REGISTER)){
				loginDialog.dispose();
				try {
					UserRegistrationOP.registrationOperationGUI(requestManager,	clientServerInfo, LoginDialog.USERACTION_REGISTER,null);
				} catch (UserCancelException e) {
					//do nothing
				} catch (Exception e) {
					e.printStackTrace();
					PopupGenerator.showErrorDialog("New user Registration error:\n"+e.getMessage());
				}
			}else if(evt.getActionCommand().equals(LoginDialog.USERACTION_LOSTPASSWORD)){
				try {
					ClientServerInfo newClientServerInfo = createClientServerInfo(clientServerInfo,loginDialog.getUser(),null);
					UserRegistrationOP.registrationOperationGUI(requestManager, newClientServerInfo, LoginDialog.USERACTION_LOSTPASSWORD,null);
				} catch (UserCancelException e) {
					//do nothing
				} catch (Exception e) {
					e.printStackTrace();
					PopupGenerator.showErrorDialog("New user Registration error:\n"+e.getMessage());
				}
			}else if(evt.getActionCommand().equals(LoginDialog.USERACTION_CANCEL)){
				PopupGenerator.showInfoDialog(
					"Note:  The Login dialog can be accessed any time under the 'Server' main menu as 'Change User...'");
			}
		}
	};
	loginDialog.addActionListener(listener);
	org.vcell.util.gui.ZEnforcer.showModalDialogOnTop(loginDialog);
}


public static ClientServerInfo createClientServerInfo(ClientServerInfo clientServerInfo,String userid,String password){
	switch (clientServerInfo.getServerType()) {
		case ClientServerInfo.SERVER_LOCAL: {
			return ClientServerInfo.createLocalServerInfo(userid,password);
		}
		case ClientServerInfo.SERVER_REMOTE: {
			return ClientServerInfo.createRemoteServerInfo(clientServerInfo.getHosts(),userid,password);
		}
	};
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 3:51:06 PM)
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
public static void startClientFromApplet(VCellClientApplet vcellClientApplet) {
	// instantiate appl
	vcellClientApplet.showStatus("instantiating application");
	final VCellClient vcellClient = new VCellClient();
	vcellClient.isApplet = true;
	// start management layer
	vcellClientApplet.showStatus("starting managemnt layer");
	vcellClient.setClientServerManager(new ClientServerManager());
	vcellClient.setRequestManager(new ClientRequestManager(vcellClient));
	vcellClient.setMdiManager(new ClientMDIManager(vcellClient.getRequestManager()));
	// start auxilliary stuff
	vcellClientApplet.showStatus("starting auxilliary threads");
	vcellClient.startStatusThreads();
	// generate blank document to start with
	vcellClientApplet.showStatus("preparing blank BioModel");
	VCDocument startupDoc = ((ClientRequestManager)vcellClient.getRequestManager()).createDefaultDocument(VCDocument.BIOMODEL_DOC);
	// fire up the GUI
	vcellClientApplet.showStatus("starting the user interface");
	String build = vcellClientApplet.getParameter("VERSION");
	if (build != null){
		DocumentWindowAboutBox.BUILD_NO = build;
	}
    vcellClient.createAndShowGUI(startupDoc, true);
	// update the applet
	vcellClientApplet.setClient(vcellClient);
	vcellClientApplet.showStatus("VCell client application running");
	vcellClientApplet.getSplashWindow().dispose();
    // try server connection
    final ClientServerInfo clientServerInfo = ClientServerInfo.createRemoteServerInfo(
	    new String[] {vcellClientApplet.getParameter("HOST")+":"+vcellClientApplet.getParameter("PORT")},
	    vcellClientApplet.getParameter("USERID"),
	    vcellClientApplet.getParameter("PASSWORD")
	    );
	new Thread(new Runnable(){public void run(){vcellClient.getRequestManager().connectToServer(clientServerInfo);}}).start();
}
	
/**
 * Comment
 */
private void startStatusThreads() {
	StatusUpdater statusUpdater = new StatusUpdater(getMdiManager());
	setStatusUpdater(statusUpdater);
}
}