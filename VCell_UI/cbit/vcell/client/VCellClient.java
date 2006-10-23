package cbit.vcell.client;
import java.awt.event.*;

import cbit.util.document.VCDocument;
import cbit.vcell.desktop.*;
import cbit.vcell.geometry.*;
import cbit.vcell.mathmodel.*;
import cbit.vcell.client.server.*;
import cbit.vcell.client.desktop.*;
import javax.swing.*;
import cbit.vcell.biomodel.*;
/**
 * Insert the type's description here.
 * Creation date: (5/5/2004 1:24:03 PM)
 * @author: Ion Moraru
 */
public class VCellClient {
	private VCDocument startupDoc = null;
	private ClientServerManager clientServerManager = null;
	private StatusUpdater statusUpdater = null;
	private RequestManager requestManager = null;
	private MDIManager mdiManager = null;
	private boolean isApplet = false;

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
private void createAndShowGUI(boolean fromApplet) {
	try {
		if (!fromApplet) {
			/* Set Look and Feel */
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		/* Create the first document desktop */
		DocumentWindowManager windowManager = null;
		VCDocument doc = getStartupDoc();
		switch (doc.getDocumentType()) {
			case VCDocument.BIOMODEL_DOC: {
				windowManager = new BioModelWindowManager(new JPanel(), getRequestManager(), (BioModel)doc, getMdiManager().getNewlyCreatedDesktops());
				((BioModelWindowManager)windowManager).preloadApps();
				break;
			}
			case VCDocument.MATHMODEL_DOC: {
				windowManager = new MathModelWindowManager(new JPanel(), getRequestManager(), (MathModel)doc, getMdiManager().getNewlyCreatedDesktops());
				break;
			}
			case VCDocument.GEOMETRY_DOC: {
				windowManager = new GeometryWindowManager(new JPanel(), getRequestManager(), (Geometry)doc, getMdiManager().getNewlyCreatedDesktops());
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
 * Creation date: (5/10/2004 4:14:58 PM)
 * @return cbit.vcell.document.VCDocument
 */
private cbit.util.document.VCDocument getStartupDoc() {
	return startupDoc;
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
 * Creation date: (5/10/2004 4:14:58 PM)
 * @param newStartupDoc cbit.vcell.document.VCDocument
 */
private void setStartupDoc(cbit.util.document.VCDocument newStartupDoc) {
	startupDoc = newStartupDoc;
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
public static VCellClient startClient(VCDocument startupDoc, final ClientServerInfo clientServerInfo) {
	// instantiate app
	final VCellClient vcellClient = new VCellClient();
	// start management layer
	vcellClient.setClientServerManager(new ClientServerManager());
	vcellClient.getClientServerManager().setClientServerInfo(clientServerInfo);
	vcellClient.setRequestManager(new ClientRequestManager(vcellClient));
	vcellClient.setMdiManager(new ClientMDIManager(vcellClient.getRequestManager()));
	// start auxilliary stuff
	vcellClient.startStatusThreads();
	// make sure we have at least a blank document to start with
	if (startupDoc == null) {
		startupDoc = ((ClientRequestManager)vcellClient.getRequestManager()).createDefaultDocument(VCDocument.BIOMODEL_DOC);
	}
	vcellClient.setStartupDoc(startupDoc);
	// fire up the GUI
    vcellClient.createAndShowGUI(false);
	String build = System.getProperty("vcell.softwareVersion");
	if (build != null){
		DocumentWindowAboutBox.BUILD_NO = build;
	}
    // try server connection
    if (clientServerInfo.getUsername() == null) {
	    // we were not supplied login credentials; pop-up dialog
		final LoginDialog loginDialog = new LoginDialog(null, true,false);
		loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		loginDialog.pack();
		loginDialog.setResizable(false);
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ClientServerInfo newClientServerInfo = null;
				switch (clientServerInfo.getServerType()) {
					case ClientServerInfo.SERVER_LOCAL: {
						newClientServerInfo = ClientServerInfo.createLocalServerInfo(loginDialog.getUser(), loginDialog.getPassword());
						break;
					}
					case ClientServerInfo.SERVER_REMOTE: {
						newClientServerInfo = ClientServerInfo.createRemoteServerInfo(clientServerInfo.getHost(), loginDialog.getUser(), loginDialog.getPassword());
						break;
					}
				}
				vcellClient.getRequestManager().connectToServer(newClientServerInfo);
			}
		};
		loginDialog.addActionListener(listener);
		cbit.gui.ZEnforcer.showModalDialogOnTop(loginDialog);
    } else {
		vcellClient.getRequestManager().connectToServer(clientServerInfo);
    }
	return vcellClient;
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
	vcellClient.setStartupDoc(((ClientRequestManager)vcellClient.getRequestManager()).createDefaultDocument(VCDocument.BIOMODEL_DOC));
	// fire up the GUI
	vcellClientApplet.showStatus("starting the user interface");
	String build = vcellClientApplet.getParameter("VERSION");
	if (build != null){
		DocumentWindowAboutBox.BUILD_NO = build;
	}
    vcellClient.createAndShowGUI(true);
	// update the applet
	vcellClientApplet.setClient(vcellClient);
	vcellClientApplet.showStatus("VCell client application running");
	vcellClientApplet.getSplashWindow().dispose();
    // try server connection
    ClientServerInfo clientServerInfo = ClientServerInfo.createRemoteServerInfo(
	    vcellClientApplet.getParameter("HOST")+":"+vcellClientApplet.getParameter("PORT"),
	    vcellClientApplet.getParameter("USERID"),
	    vcellClientApplet.getParameter("PASSWORD")
	    );
	vcellClient.getRequestManager().connectToServer(clientServerInfo);
}
	
/**
 * Comment
 */
private void startStatusThreads() {
	StatusUpdater statusUpdater = new StatusUpdater(getMdiManager());
	setStatusUpdater(statusUpdater);
}
}