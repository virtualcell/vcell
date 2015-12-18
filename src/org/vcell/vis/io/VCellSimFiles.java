package org.vcell.vis.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.vcell.util.document.KeyValue;

import cbit.vcell.solver.Simulation;

public class VCellSimFiles {
	
	class VCellSimFileEntry {
		public final File zipFile;
		public final File zipEntry;
		public final double time;
		
		VCellSimFileEntry(File zipFile, File zipEntry, double time) {
			super();
			this.zipFile = zipFile;
			this.zipEntry = zipEntry;
			this.time = time;
		}
	}
	
	private final KeyValue simKey;
	private final int jobIndex;
	public final File cartesianMeshFile;
	public final File meshMetricsFile;
	public final File subdomainFile;
	public final File logFile;
	private final ArrayList<VCellSimFileEntry> dataFileEntries = new ArrayList<VCellSimFiles.VCellSimFileEntry>();
	public final File postprocessingFile;
	
	
	public VCellSimFiles(KeyValue simKey, int jobIndex, File cartesianMeshFile, File meshMetricsFile, File subdomainFile, File logFile, File postprocessingFile) {
		super();
		this.simKey = simKey;
		this.jobIndex = jobIndex;
		this.cartesianMeshFile = cartesianMeshFile;
		this.meshMetricsFile = meshMetricsFile;
		this.subdomainFile = subdomainFile;
		this.logFile = logFile;
		this.postprocessingFile = postprocessingFile;
	}
	
	public void addDataFileEntry(File zipFile, File zipEntry, double time){
		this.dataFileEntries.add(new VCellSimFileEntry(zipFile, zipEntry, time));
	}
	
	public List<Double> getTimes() {
		ArrayList<Double> times = new ArrayList<Double>();
		for (VCellSimFileEntry entry : dataFileEntries){
			if (!times.contains(entry.time)){
				times.add(entry.time);
			}
		}
		java.util.Collections.sort(times,new Comparator<Double>(){
			public int compare(Double o1, Double o2) {
				return o1.compareTo(o2);
			}
		});
		return times;
	}

	public File getZipFile(double time) {
		for (VCellSimFileEntry entry : dataFileEntries){
			if (entry.time == time){
				return entry.zipFile;
			}
		}
		throw new RuntimeException("no zip file found for time "+time);
	}

	public File getZipEntry(double time) {
		for (VCellSimFileEntry entry : dataFileEntries){
			if (entry.time == time){
				return entry.zipEntry;
			}
		}
		throw new RuntimeException("no zip entry found for time "+time);
	}

	public String getCannonicalFilePrefix(String domainName) {
		return Simulation.createSimulationID(simKey)+"_"+jobIndex+"_"+domainName;
	}

	public String getCannonicalFilePrefix(String domainName, int timeIndex) {
		return Simulation.createSimulationID(simKey)+"_"+jobIndex+"_"+domainName+"_"+String.format("%06d",timeIndex);
	}

}
