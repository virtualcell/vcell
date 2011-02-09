package cbit.vcell.visit;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.vcell.util.document.User;

import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.server.VCellThreadChecker;
import llnl.visit.ViewerProxy;
//import llnl.visit.VisitClients;

public class VisitSession {
	
	public static class VisitSessionException extends Exception {
		public VisitSessionException(String message){
			super(message);
		}
	}

	private ClientRequestManager clientRequestManager;
	private VisitConnectionInfo visitConnectionInfo;
	private String visitPath;
	private ViewerProxy viewer;
	private boolean bServerOpen = false;
	private boolean bDatabaseOpen = false;
	private String currentLogFile;
	private String databaseConnectionString = null;
	
	public VisitSession(ClientRequestManager clientRequestManager, String visitPath, VisitConnectionInfo visitConnectionInfo) {
		this.visitConnectionInfo = visitConnectionInfo;
		this.visitPath = visitPath;
	}
	
	public void initViewerProxyOpenWindows() {
		VCellThreadChecker.checkCpuIntensiveInvocation();

		viewer = new ViewerProxy();
    	
        // Pass command line options to the viewer viewer
     	viewer.SetVerbose(true);
        viewer.SetBinPath(visitPath);
        viewer.AddArgument("-dv");
        viewer.AddArgument("-auxsessionkey");
        viewer.AddArgument(visitConnectionInfo.getAuxSessionKey());

        // Try and open the viewer using the viewer proxy.
        int viewerPort = 5600;
        if(viewer.Create(viewerPort)){
        	
            System.out.println("ViewerProxy opened the viewer.");

            viewer.SetSynchronous(true);

            // Show the windows.
            viewer.GetViewerMethods().ShowAllWindows();

        } else
            System.out.println("ViewerProxy could not open the viewer.");
    }
	
	public void runEventLoop(){
		VCellThreadChecker.checkCpuIntensiveInvocation();
		System.out.println("entering the Visit Viewer event loop.");
        //viewer.GetEventLoop().Execute();
       // System.out.println("Visit window closed, closing the Viewer proxy.");
       // close();
	}
	
	
	public void openMDServer(String ipAddress){
		Vector args = new Vector();
		args.add("-auxsessionkey");
		args.add(visitConnectionInfo.getAuxSessionKey());
		bServerOpen = viewer.GetViewerMethods().OpenMDServer(ipAddress,args);
		bDatabaseOpen = false;
		databaseConnectionString = null;
	}
	
	public void closeDatabase() throws VisitSessionException {
		if (databaseConnectionString!=null){
			boolean returncode = viewer.GetViewerMethods().CloseDatabase(databaseConnectionString);
			if (returncode==false){
				throw new VisitSessionException("unable to close database '"+databaseConnectionString+"'");
			}else{
				databaseConnectionString = null;
				bDatabaseOpen = false;
			}
		}else{
			throw new VisitSessionException("database not open");
		}
	}
	
	public void openDatabase(User user, String simLogName) throws VisitSessionException {
		String s = getVisitConnectionInfo().getDatabaseOpenPath(user,simLogName);
		System.out.println("About to open " + s);
		boolean bOpened = viewer.GetViewerMethods().OpenDatabase(s);
		if (bOpened){
			currentLogFile = simLogName;
			bDatabaseOpen = true;
			databaseConnectionString = s;
		}else{
			bDatabaseOpen = false;
			throw new VisitSessionException("unable to open visit database '"+s+"'");
		}
	}

	
	public VisitConnectionInfo getVisitConnectionInfo(){
		return visitConnectionInfo;
	} 
	
	public void close(){
		if (viewer!=null){
			try {
				closeDatabase();
			}catch (VisitSessionException e){
				e.printStackTrace(System.out);
			}
			viewer.Close();
			bServerOpen = false;
		}
	}

	public void addAndDrawPseudocolorPlot(String variableName) {
		System.out.println("attempting to plot variable '"+variableName+"'");
		viewer.GetViewerMethods().AddPlot("Pseudocolor", variableName);
		viewer.GetViewerMethods().DrawPlots();
	}

	public void deleteActivePlots() {
		viewer.GetViewerMethods().DeleteActivePlots();
	}

	public void showVisitGUI() {
        String clientName = new String("GUI");
        String clientProgram = new String("visit");
        Vector<String> clientArgs = new Vector<String>();
        clientArgs.add("-gui");
        viewer.GetViewerMethods().OpenClient(clientName, clientProgram, clientArgs);
	}
	
}