package org.vcell.visit.jvisit;

import java.io.File;
import java.util.ArrayList;

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
			viewerProxy.SetSynchronous(false);
	     	System.out.println("Setting visitPath="+visitBinDir.getAbsolutePath());
	     	viewerProxy.SetBinPath(visitBinDir.getAbsolutePath());
		}
		return viewerProxy;
	}
	
	abstract void initViewerProxyAndShowWindows();
	
	abstract void openMDServer();
	
	public void init(){
		initViewerProxyAndShowWindows();
		openMDServer();
	}

	public VisitDatabaseInfo openDatabase(VisitDatabaseSpec visitDatabaseSpec) throws VisitSessionException {
		String visitOpenDatabaseString = visitDatabaseSpec.getVisitOpenDatabaseString();
		System.out.println("About to open " + visitOpenDatabaseString);
		boolean bOpened = getViewerProxy().GetViewerMethods().OpenDatabase(visitOpenDatabaseString);
		if (bOpened){
			VisitDatabaseInfo visitDatabaseInfo = new VisitDatabaseInfo(visitOpenDatabaseString,visitDatabaseSpec);
			openDatabaseInfos.add(visitDatabaseInfo);
			return visitDatabaseInfo;
		}else{
			throw new VisitSessionException("unable to open visit database '"+visitOpenDatabaseString+"'");
		}
	}

	public void closeDatabase(VisitDatabaseInfo visitDatabaseInfo) {
	}
	
	
	public void addAndDrawPseudocolorPlot(String variableName) {
		System.out.println("attempting to plot variable '"+variableName+"'");
//		for (VisitDatabaseInfo visitDBInfo : openDatabaseInfos){
//			if (visitDBInfo.equals(visitDatabaseInfo)){
//				
//			}
//		}
		getViewerProxy().GetViewerMethods().AddPlot("Pseudocolor", variableName);
		getViewerProxy().GetViewerMethods().DrawPlots();
	}


	public void synchronize(){
		getViewerProxy().GetViewerMethods().DoSynchronize();
	}

	public abstract VisitDatabaseSpec createDatabaseSpec(VCDataIdentifier vcDataIdentifier);	
}
