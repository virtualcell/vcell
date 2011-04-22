package cbit.vcell.visit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JFileChooser;

import org.vcell.util.Executable;
import org.vcell.util.ExecutableStatus;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.User;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.server.VCellThreadChecker;
import llnl.visit.ViewerMethods;
import llnl.visit.ViewerProxy;
import llnl.visit.ClientMethod;
import llnl.visit.MachineProfile;
import llnl.visit.HostProfileList;
import llnl.visit.ClientInformation;
import llnl.visit.ClientInformationList;
import llnl.visit.ViewerState;
import llnl.visit.operators.ClipAttributes;
import llnl.visit.operators.SliceAttributes;
import llnl.visit.operators.ThreeSliceAttributes;
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
		System.out.println(visitPath);
	}
	
	public void initViewerProxyOpenWindows() {
		VCellThreadChecker.checkRemoteInvocation();

		viewer = new ViewerProxy();
    	
        // Pass command line options to the viewer viewer
     	viewer.SetVerbose(true);
     	System.out.println("Setting visitPath="+visitPath);
        viewer.SetBinPath(visitPath);
		//viewer.AddArgument("-debug");
		//viewer.AddArgument("5");
        //viewer.AddArgument("-dv");
        viewer.AddArgument("-auxsessionkey");
        viewer.AddArgument(visitConnectionInfo.getAuxSessionKey());

        
        System.out.println("About to try opening the local Viewer");
        // Try and open the viewer using the viewer proxy.
        int viewerPort = 5600;
        if(viewer.Create(viewerPort)){
        	
            System.out.println("ViewerProxy opened the viewer.");

            viewer.SetSynchronous(true);

            // Show the windows
            getViewerMethods().ShowAllWindows();

        } else
            System.out.println("ViewerProxy could not open the viewer.");
    }
	
//	public void runEventLoop(){
//		VCellThreadChecker.checkCpuIntensiveInvocation();
//		System.out.println("entering the Visit Viewer event loop.");
//        //getEventLoop().Execute();
//       // System.out.println("Visit window closed, closing the Viewer proxy.");
//       // close();
//	}
	
	
	public void openMDServer(String ipAddress){
		
		 // Change these for your remote system.
        String host = ipAddress;;
        String user = new String("visit");
        String remotevisitPath =
        	PropertyLoader.getRequiredProperty(PropertyLoader.visitServerExecutableProperty);
 
        // Create a new host profile object and set it up for serial
        MachineProfile profile = new MachineProfile();
        
        profile.SetHost(host);
        profile.SetHostAliases(host);
        profile.SetHostNickname(host);
        profile.SetUserName(user);
        profile.SetClientHostDetermination(MachineProfile.CLIENTHOSTDETERMINATION_PARSEDFROMSSHCLIENT);
        profile.SetTunnelSSH(false);
      
        profile.SetDirectory(remotevisitPath);

 
		
		Vector args = new Vector();
		args.add("-auxsessionkey");
		args.add(visitConnectionInfo.getAuxSessionKey());
		
        viewer.GetViewerState().GetHostProfileList().ClearMachines();
        profile.SetActiveProfile(0);
        viewer.GetViewerState().GetHostProfileList().AddMachines(profile);
        

        viewer.GetViewerState().GetHostProfileList().Notify();
        System.out.println("HostProfileList = \n" + 
            viewer.GetViewerState().GetHostProfileList().toString());

		
		System.out.println("about to OpenMDServer("+ipAddress+","+args+")");
		bServerOpen = getViewerMethods().OpenMDServer(ipAddress,args);
		bDatabaseOpen = false;
		databaseConnectionString = null;
	}
	
	public void closeDatabase() throws VisitSessionException {
		if (databaseConnectionString!=null){
			boolean returncode = getViewerMethods().CloseDatabase(databaseConnectionString);
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
	
	private ViewerMethods getViewerMethods(){
		VCellThreadChecker.checkRemoteInvocation();
		return viewer.GetViewerMethods();
	}
	
	public void openDatabase(User user, String simLogName) throws VisitSessionException {
		String s = getVisitConnectionInfo().getIPAddress()+":"+getVisitConnectionInfo().getDatabaseOpenPath(user,simLogName);
		System.out.println("About to open " + s);
		boolean bOpened = getViewerMethods().OpenDatabase(s);
		if (bOpened){
			currentLogFile = simLogName;
			bDatabaseOpen = true;
			databaseConnectionString = s;
		}else{
			bDatabaseOpen = false;
			throw new VisitSessionException("unable to open visit database '"+s+"'");
		}
	}

	
	public void setSliderState(int sliderValue) throws VisitSessionException {
		boolean sliderChanged = getViewerMethods().SetTimeSliderState(sliderValue);
		if (!sliderChanged) throw new VisitSessionException("unable to change slider state"); 
	}
	
	public VisitConnectionInfo getVisitConnectionInfo(){
		return visitConnectionInfo;
	} 
	
	public void close(){
		VCellThreadChecker.checkRemoteInvocation();
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
		getViewerMethods().AddPlot("Pseudocolor", variableName);
		getViewerMethods().DrawPlots();
	}

	
	//delete the last created active plot
	public void deleteActivePlots() {
		getViewerMethods().DeleteActivePlots();
	}

	public void drawPlots(){
		getViewerMethods().DrawPlots();
	}
	
	public void addAndDrawSurfaceMesh(){
		getViewerMethods().AddPlot("Mesh","membrMesh");
		getViewerMethods().DrawPlots();
		
	}
	
	
	public void resetView(){
		getViewerMethods().ResetView();
	}
	
	public void showVisitGUI() {
        String clientName = new String("GUI");
        String clientProgram = new String("visit");
        Vector<String> clientArgs = new Vector<String>();
        clientArgs.add("-gui");
       getViewerMethods().OpenClient(clientName, clientProgram, clientArgs);
	}
	
	public void saveSession(String fileNameWithPath){

		getViewerMethods().ExportEntireState(fileNameWithPath);
		//InterpretPython("SaveSession(\""+fileNameWithPath+"\")");   //Python equivalent
	
	}
	
	public void restoreSession(String fileNameWithPath){
		
		getViewerMethods().ImportEntireState(fileNameWithPath, false);
	
	}
	
	
	public void makeMovie(String fileLocation){
		// 1 - save session file to user's local temp dir
		// 2 - copy from there to temp work dir on VisitPhineas
		// 3 - use that session file to set up the shot for the movie script on VisitPhineas
		
		String localSessionFile=null;
		try {
			localSessionFile = File.createTempFile(currentLogFile.substring(0, currentLogFile.length() - 4),".session").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		System.out.println("Saving current session file to: "+localSessionFile);
		saveSession(localSessionFile);
		
		try {
			ArrayList<String> args = new ArrayList<String>();
			args.add(visitPath+File.separator+"visit");//location of visit
			args.add("-movie");
			args.add("-sessionfile");
			args.add(localSessionFile);
			args.add("-format");
			args.add("mpeg");
			args.add("-output");
			args.add(fileLocation);
			//args.add("/eboyce-local/"+currentLogFile.substring(0, currentLogFile.length() - 4));
			Executable executable = new Executable(args.toArray(new String[0]));
			executable.start();
			while (!executable.getStatus().equals(ExecutableStatus.COMPLETE) && !executable.getStatus().equals(ExecutableStatus.STOPPED)){
				Thread.sleep(1000);
				System.out.println("waiting");
				
			}
            //TODO: if error, should show error message to users.
			System.out.println("done : status = " + executable.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Movie should be ready");
		
	}
	
	public String getCurrentLogFile(){
		return currentLogFile;
	}
	
	/* Slice operator methods */
	
	public void addCartesianSliceOperator(int axisType){
		getViewerMethods().AddOperator("Slice");
		
		int type = viewer.GetOperatorIndex("Slice");
		SliceAttributes atts = (SliceAttributes)viewer.GetOperatorAttributes(type);
		atts.SetAxisType(axisType);
		atts.SetProject2d(false);
		atts.Notify();
		getViewerMethods().SetOperatorOptions("Slice");
		drawPlots();
	
	}
	
	public void changeSliceProject2D(boolean projectTo2D){
		int type = viewer.GetOperatorIndex("Slice");
		SliceAttributes atts = (SliceAttributes)viewer.GetOperatorAttributes(type);
		atts.SetProject2d(projectTo2D);
		atts.Notify();
		drawPlots();
	}
	
	public void changeSliceAxis(int axisType){
		
		int type = viewer.GetOperatorIndex("Slice");
		SliceAttributes atts = (SliceAttributes)viewer.GetOperatorAttributes(type);
		atts.SetAxisType(axisType);
		atts.Notify();
		getViewerMethods().SetOperatorOptions("Slice");
		drawPlots();
	}
	
	public void changeSliceAlongAxis(double originIntercept) throws VisitSessionException{
		int type = viewer.GetOperatorIndex("Slice");
		SliceAttributes atts = (SliceAttributes)viewer.GetOperatorAttributes(type);
		//atts.SetAxisType(2);
		
		atts.SetOriginIntercept(originIntercept);
		atts.Notify();
		getViewerMethods().SetOperatorOptions("Slice");
		drawPlots();
	}
	
	
	/* Clip plane methods */
	
	public void addClipPlaneOperator(){
		getViewerMethods().AddOperator("Clip");
		
		int type = viewer.GetOperatorIndex("Clip");
		ClipAttributes atts = (ClipAttributes)viewer.GetOperatorAttributes(type);
		atts.Notify();
		getViewerMethods().SetOperatorOptions("Clip");
		drawPlots();
	
	}
	
	
	public void setClipAttributes(ClipAttributes atts){
		atts.Notify();
		getViewerMethods().SetOperatorOptions("Clip");
		drawPlots();
	}
	
	/* Three plane slice methods */
	
	public void addThreeSliceOperator(){
		getViewerMethods().AddOperator("ThreeSlice");
		
		int type = viewer.GetOperatorIndex("ThreeSlice");
		ThreeSliceAttributes atts = (ThreeSliceAttributes)viewer.GetOperatorAttributes(type);
		atts.Notify();
		getViewerMethods().SetOperatorOptions("ThreeSlice");
		drawPlots();
	
	}
	
	  /**
     * Enables or disables an interactive tool in the active visualization window.
     *
     * @param tool 0=Box, 1=Line, 2=Plane, 3=Sphere, 4=Point, 5=Extents, 6=Axis restriction
     * @param enabled true to enable the tool; false to disable.
     * @return true on success; false otherwise.
     */
	
	public void enableViewerTool(int toolID, boolean enabled) throws VisitSessionException {
		boolean b = getViewerMethods().EnableTool(toolID , enabled);		
		if (!b) throw new VisitSessionException("Couldn't enable or disable tool #"+ toolID);
	}
	
	
	public ViewerState getViewerState(){
		
		return viewer.GetViewerState();
	}
	
	
	
	
	//Python client methods:
	
    //
    // Check all of the client information until we find a client that
    // supports the Interpret method with a string argument.
    //
    protected boolean NoInterpretingClient()
    {
        // Make a copy because the reader thread could be messing with it.
        // Need to synchronize access.
        ClientInformationList cL = new ClientInformationList(
            viewer.GetViewerState().GetClientInformationList());
 
        for(int i = 0; i < cL.GetNumClients(); ++i)
        {
            ClientInformation client = cL.GetClients(i);
            for(int j = 0; j < client.GetMethodNames().size(); ++j)
            {
                String name = (String)client.GetMethodNames().elementAt(j);
                if(name.equals("Interpret"))
                {
                    String proto = (String)client.GetMethodPrototypes().elementAt(j);
                    if(proto.equals("s"))
                    {
                        // We have an interpreting client
                        return false;
                    }
                }
            }
        }
        return true;
    }
 
    //
    // If we don't have a client that can "Interpret" then tell the viewer
    // to launch a VisIt CLI.
    //    
    protected boolean InitializePython()
    {
        boolean launched = false;
        if(NoInterpretingClient())
        {
            System.out.println("Tell the viewer to create a CLI so we can execute code.");
            Vector args = new Vector();
            args.addElement(new String("-cli"));
            args.addElement(new String("-newconsole"));
            viewer.GetViewerMethods().OpenClient("CLI", 
                 "visit",
//                 viewer.GetVisItLauncher(),
                 args);
            launched = true;
 
            viewer.Synchronize();
 
            // HACK: Wait until we have an interpreting client.
            while(NoInterpretingClient())
                viewer.Synchronize();
        }
        return launched;
    }
 
    //
    // Interpret a Python command string.
    // 
    protected void InterpretPython(String cmd)
    {
        InitializePython();
 
        // Send the command to interpret as a client method.
        ClientMethod method = viewer.GetViewerState().GetClientMethod();
        method.SetIntArgs(new Vector());
        method.SetDoubleArgs(new Vector());
        Vector args = new Vector();
        args.addElement(new String(cmd + "\n"));
        method.SetStringArgs(args);
        method.SetMethodName("Interpret");
        method.Notify();
        System.out.println("Interpret: " + cmd);
 
        viewer.Synchronize();
    }
	
	
}