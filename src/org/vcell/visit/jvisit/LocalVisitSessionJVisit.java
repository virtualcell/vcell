package org.vcell.visit.jvisit;

import java.io.File;

import org.vcell.util.document.VCDataIdentifier;
import org.vcell.visit.LocalVisitDatabaseSpec;
import org.vcell.visit.VisitDatabaseSpec;
import org.vcell.visit.VisitUtils;

import llnl.visit.ViewerProxy;

public class LocalVisitSessionJVisit extends VisitSessionJVisit {

	LocalVisitSessionJVisit(File visitBinDir) {
		super(visitBinDir);
		
	}
	
	void initViewerProxyAndShowWindows(){
		ViewerProxy viewer = getViewerProxy();
        
        System.out.println("About to try opening the local Viewer");
        // Try and open the viewer using the viewer proxy.
        int viewerPort = 5600;
        if(viewer.Create(viewerPort)){
            System.out.println("ViewerProxy opened the viewer.");
            // Show the windows
            viewer.GetViewerMethods().ShowAllWindows();

        } else {
            System.out.println("ViewerProxy could not open the viewer.");
        }
	}

	@Override
	public void openMDServer() {
		System.out.println("local Visit Session, MDServer should already be opened");
	}

	public VisitDatabaseSpec createDatabaseSpec(VCDataIdentifier vcDataIdentifier) {
		String cannonicalName = VisitUtils.getCannonicalFileName(vcDataIdentifier);
		return new LocalVisitDatabaseSpec(new File(cannonicalName));
	}


}
