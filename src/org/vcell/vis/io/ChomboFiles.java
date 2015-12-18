package org.vcell.vis.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.vcell.util.document.KeyValue;

import cbit.vcell.solver.Simulation;


public class ChomboFiles {
	public static final String MEMBRANE_DOMAIN_SUFFIX = "_Membrane";

	public class ChomboFileEntry {
		private String feature;
		private int ivol;
		private final int timeIndex;
		private final File file;
		
		public ChomboFileEntry(String feature, int ivol, int timeIndex, File file) {
			super();
			this.feature = feature;
			this.ivol = ivol;
			this.timeIndex = timeIndex;
			this.file = file;
		}

		public String getVolumeDomainName()
		{
			return feature + ".vol" + ivol;
		}
		
		public String getMembraneDomainName(){
			return getVolumeDomainName() + MEMBRANE_DOMAIN_SUFFIX;
		}
		
		public int getTimeIndex() {
			return timeIndex;
		}

		public File getFile() {
			return file;
		}

		public String getFeature() {
			return feature;
		}

		public int getIvol() {
			return ivol;
		}
	}
	
	private final KeyValue simKey;
	private final int jobIndex;
	private final File meshFile;
	private ArrayList<ChomboFileEntry> dataFiles = new ArrayList<ChomboFileEntry>();
	
	public ChomboFiles(KeyValue simKey, int jobIndex, File meshFile) {
		super();
		this.simKey = simKey;
		this.jobIndex = jobIndex;
		this.meshFile = meshFile;
	}
	
	public int getJobIndex(){
		return this.jobIndex;
	}
	
//	public KeyValue getSimID(){
//		return this.simID;
//	}

	public File getMeshFile() {
		return meshFile;
	}
	
	public void addDataFile(String feature, int ivol, int timeIndex, File file){
		dataFiles.add(new ChomboFileEntry(feature, ivol,timeIndex,file));
	}

	public List<Integer> getTimeIndices() {
		ArrayList<Integer> timeIndices = new ArrayList<Integer>();
		for (ChomboFileEntry entry : dataFiles){
			if (!timeIndices.contains(entry.timeIndex)){
				timeIndices.add(entry.timeIndex);
			}
		}
		java.util.Collections.sort(timeIndices,new Comparator<Integer>(){
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		});
		return timeIndices;
	}
	
	public List<ChomboFileEntry> getEntries(){
		return dataFiles;
	}
	
	public List<ChomboFileEntry> getEntries(int timeIndex){
		List<ChomboFileEntry> files = new ArrayList<ChomboFileEntry>();
		for (ChomboFileEntry entry : dataFiles){
			if (timeIndex == entry.timeIndex){
				files.add(entry);
			}
		}
		return files;
	}

	public String getCannonicalFilePrefix(String domainName, int timeIndex) {
		return Simulation.createSimulationID(simKey)+"_"+getJobIndex()+"_"+domainName+"_"+String.format("%06d",timeIndex);
	}

	public String getCannonicalFilePrefix(String domainName) {
		return Simulation.createSimulationID(simKey)+"_"+getJobIndex()+"_"+domainName;
	}

}
