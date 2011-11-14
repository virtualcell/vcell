package org.vcell.visit.jvisit;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.vcell.util.document.VCDataIdentifier;
import org.vcell.visit.VisitManager;
import org.vcell.visit.VisitSession;
import org.vcell.visit.VisitUtils;

import cbit.vcell.visit.VisitConnectionInfo;


public class VisitManagerJVisit implements VisitManager {
	private File visitBinDir = null;
	private ArrayList<VisitSession> visitSessions = new ArrayList<VisitSession>();
		
	public VisitManagerJVisit(){
		visitBinDir = VisitUtils.findVisitBinDirectory();
	}
	
	public File getVisitBinDir() {
		return visitBinDir;
	}

	public void setVisitBinDir(File visitBinDir) {
		this.visitBinDir = visitBinDir;
	}

	public Iterator<org.vcell.visit.VisitSession> getVisitSessions() {
		return visitSessions.iterator();
	}
	
	public VisitSession createLocalSession(){
		VisitSessionJVisit newVisitSession = new LocalVisitSessionJVisit(visitBinDir);
		visitSessions.add(newVisitSession);
		return newVisitSession;
	}

	public VisitSession createRemoteSession(VisitConnectionInfo visitConnectionInfo){
		VisitSessionJVisit newVisitSession = new RemoteVisitSessionJVisit(visitConnectionInfo,visitBinDir);
		visitSessions.add(newVisitSession);
		return newVisitSession;
	}

}
