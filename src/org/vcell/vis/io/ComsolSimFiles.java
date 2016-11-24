package org.vcell.vis.io;

import java.io.File;

import org.vcell.util.document.KeyValue;

import cbit.vcell.solver.Simulation;

public class ComsolSimFiles {
	
	public final KeyValue simKey;
	public final int jobIndex;
	public final File comsoldataFile;
	public final File simTaskXMLFile;
	public final File logFile;
	
	
	public ComsolSimFiles(KeyValue simKey, int jobIndex, File comsoldataFile, File simTaskXMLFile, File logFile) {
		super();
		this.simKey = simKey;
		this.jobIndex = jobIndex;
		this.comsoldataFile = comsoldataFile;
		this.simTaskXMLFile = simTaskXMLFile;
		this.logFile = logFile;
	}
	
	public String getCannonicalFilePrefix(String domainName) {
		return Simulation.createSimulationID(simKey)+"_"+jobIndex+"_"+domainName;
	}

	
}
