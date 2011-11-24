package org.vcell.visit.jvisit;

import java.io.File;
import java.util.Vector;

import org.vcell.util.PropertyLoader;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.visit.RemoteVisitDatabaseSpec;
import org.vcell.visit.VisitDatabaseSpec;
import org.vcell.visit.VisitUtils;

import llnl.visit.MachineProfile;
import llnl.visit.ViewerProxy;

import cbit.vcell.visit.VisitConnectionInfo;

public class RemoteVisitSessionJVisit extends VisitSessionJVisit {
	private VisitConnectionInfo visitConnectionInfo = null;
	boolean bServerOpen = false;
	boolean bDatabaseOpen = false;
	String databaseConnectionString = null;

	public RemoteVisitSessionJVisit(VisitConnectionInfo visitConnectionInfo, File visitBinDir) {
		super(visitBinDir);
		this.visitConnectionInfo = visitConnectionInfo;
	}
	
	@Override
	void initViewerProxyAndShowWindows(){
		ViewerProxy viewer = getViewerProxy();
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
            viewer.GetViewerMethods().ShowAllWindows();

        } else {
            System.out.println("ViewerProxy could not open the viewer.");
        }

	}

	@Override
	public void openMDServer() {
		 // Change these for your remote system.
		String ipAddress = visitConnectionInfo.getIPAddress();
        String host = ipAddress;
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
		
		ViewerProxy viewer = getViewerProxy();
        viewer.GetViewerState().GetHostProfileList().ClearMachines();
        profile.SetActiveProfile(0);
        viewer.GetViewerState().GetHostProfileList().AddMachines(profile);
        

        viewer.GetViewerState().GetHostProfileList().Notify();
        System.out.println("HostProfileList = \n" + 
            viewer.GetViewerState().GetHostProfileList().toString());

		
		System.out.println("about to OpenMDServer("+ipAddress+","+args+")");
		bServerOpen = viewer.GetViewerMethods().OpenMDServer(ipAddress,args);
		bDatabaseOpen = false;
		databaseConnectionString = null;

	}

	@Override
	public VisitDatabaseSpec createDatabaseSpec(VCDataIdentifier vcDataIdentifier) {
		String cannonicalName = VisitUtils.getCannonicalFileName(vcDataIdentifier);
		return new RemoteVisitDatabaseSpec(new File(cannonicalName),visitConnectionInfo);
	}

}
