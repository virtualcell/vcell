package org.vcell.vis.io;

import java.io.File;

import org.vcell.util.document.KeyValue;

import cbit.vcell.solver.Simulation;

public class MovingBoundarySimFiles {
	
	public final KeyValue simKey;
	public final int jobIndex;
	public final File hdf5OutputFile;
	
	
	public MovingBoundarySimFiles(KeyValue simKey, int jobIndex, File hdf5OutputFile) {
		super();
		this.simKey = simKey;
		this.jobIndex = jobIndex;
		this.hdf5OutputFile = hdf5OutputFile;
	}
	
	public String getCannonicalFilePrefix(String domainName) {
		return Simulation.createSimulationID(simKey)+"_"+jobIndex+"_"+domainName;
	}

	
}
