package org.vcell.visit.jvisit;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import llnl.visit.LostConnectionException;
import llnl.visit.ViewerProxy;

import org.vcell.util.document.VCDataIdentifier;
import org.vcell.visit.VisitDatabaseInfo;
import org.vcell.visit.VisitDatabaseSpec;
import org.vcell.visit.VisitSession;

import cbit.vcell.visit.VisitSession.VisitSessionException;

public abstract class VisitSessionJVisit implements VisitSession {
	private File visitBinDir = null;
	private ViewerProxy viewerProxy = null;
	private ArrayList<VisitDatabaseInfo> openDatabaseInfos = new ArrayList<VisitDatabaseInfo>();
	
	VisitSessionJVisit(File visitBinDir){
		this.visitBinDir = visitBinDir;
	}
	
	ViewerProxy getViewerProxy(){
		if (viewerProxy == null){
			viewerProxy = new ViewerProxy();
			viewerProxy.AddArgument("-debug");
			viewerProxy.AddArgument("5");
	        viewerProxy.AddArgument("-dv");
			viewerProxy.SetVerbose(true);
			viewerProxy.SetSynchronous(true);
	     	System.out.println("Setting visitPath="+visitBinDir.getAbsolutePath());
	     	viewerProxy.SetBinPath(visitBinDir.getAbsolutePath());
		}
		return viewerProxy;
	}
	
	abstract void initViewerProxyAndShowWindows();
	
	abstract void openMDServer();
	
	public void interrupt(){
		getViewerProxy().GetViewerMethods().InterruptComputeEngine();
	}
	
	public synchronized void init(){
		initViewerProxyAndShowWindows();
		openMDServer();
	}

	public synchronized VisitDatabaseInfo openDatabase(VisitDatabaseSpec visitDatabaseSpec) throws VisitSessionException, LostConnectionException {
		String visitOpenDatabaseString = visitDatabaseSpec.getVisitOpenDatabaseString();
		System.out.println("About to open " + visitOpenDatabaseString);
		boolean bOpened = getViewerProxy().GetViewerMethods().OpenDatabase(visitOpenDatabaseString);
		if (bOpened){
			if (!synchronize()){
				throw new VisitSessionException("failed to synchronize");
			}
			//viewerProxy.GetEventLoop().Process();
			VisitDatabaseInfo visitDatabaseInfo = new VisitDatabaseInfo(visitOpenDatabaseString,visitDatabaseSpec);
			openDatabaseInfos.add(visitDatabaseInfo);
			return visitDatabaseInfo;
		}else{
			throw new VisitSessionException("unable to open visit database '"+visitOpenDatabaseString+"'");
		}
	}

	public synchronized void closeDatabase(VisitDatabaseInfo visitDatabaseInfo) {
	}
	
	
	public synchronized void addAndDrawPseudocolorPlot(String variableName) throws VisitSessionException {
		System.out.println("attempting to plot variable '"+variableName+"'");
//		for (VisitDatabaseInfo visitDBInfo : openDatabaseInfos){
//			if (visitDBInfo.equals(visitDatabaseInfo)){
//				
//			}
//		}
		getViewerProxy().GetViewerMethods().AddPlot("Pseudocolor", variableName);
		getViewerProxy().GetViewerMethods().DrawPlots();
		if (!synchronize()){
			throw new VisitSessionException("failed to synchronize");
		}
	}

	public synchronized boolean synchronize(){
		return getViewerProxy().GetEventLoop().Synchronize();
	}
	
	public void close() {
		viewerProxy.Close();
	}
	
	public synchronized void openGUI() throws VisitSessionException {
        String clientName = new String("GUI");
        String clientProgram = new String("visit");
        Vector<String> clientArgs = new Vector<String>();
        clientArgs.add("-gui");
		boolean retcode = getViewerProxy().GetViewerMethods().OpenClient(clientName, clientProgram, clientArgs);
		if (!retcode){
			throw new VisitSessionException("open client failed");			
		}
		retcode = synchronize();
		if (!retcode){
			throw new VisitSessionException("open client failed");			
		}
	}
	
	public abstract VisitDatabaseSpec createDatabaseSpec(VCDataIdentifier vcDataIdentifier);	
}
