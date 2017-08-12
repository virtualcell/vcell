package org.vcell.vis.chombo;

import java.util.ArrayList;
import java.util.List;

import org.vcell.vis.io.ChomboFiles.ChomboFileEntry;

public class ChomboDataset {
		
	public static class ChomboCombinedVolumeMembraneDomain {
		private final int ordinal;
		private ChomboFileEntry chomboFileEntry;
		private final ChomboMesh chomboMesh;
		private final ChomboMeshData chomboMeshData;
		
		public ChomboCombinedVolumeMembraneDomain(ChomboFileEntry chomboFileEntry, ChomboMesh chomboMesh, ChomboMeshData chomboMeshData,int ordinal){
			this.chomboFileEntry = chomboFileEntry;
			this.chomboMesh = chomboMesh;
			this.chomboMeshData = chomboMeshData;
			this.ordinal = ordinal;
		}
		
		public String getVolumeDomainName(){
			return chomboFileEntry.getVolumeDomainName();
		}
		
		public String getMembraneDomainName(){
			return chomboFileEntry.getMembraneDomainName();
		}
		
		public ChomboMesh getChomboMesh() {
			return chomboMesh;
		}
		public ChomboMeshData getChomboMeshData() {
			return chomboMeshData;
		}
		
		public boolean shouldIncludeVertex(boolean bInPhase1)
		{
			// if the point is in phase 1, return true if the domain is also in phase 1 otherwise false 
			// if the phase info does not exist, use old logic
			Integer iphase = chomboMesh.getPhase(chomboFileEntry.getFeature());
			return iphase == null ? ((ordinal > 0) ^ bInPhase1) : (bInPhase1 ? iphase == 1 : iphase == 0);
		}
	}
	
	private final ArrayList<ChomboCombinedVolumeMembraneDomain> domains = new ArrayList<ChomboCombinedVolumeMembraneDomain>(); 
	
	public ChomboDataset(){
		
	}
	
	public void addDomain(ChomboCombinedVolumeMembraneDomain chomboCombinedVolumeMembraneDomain){
		domains.add(chomboCombinedVolumeMembraneDomain);
	}
	
	public ChomboCombinedVolumeMembraneDomain getDomainFromVolumeOrMembraneName(String name){
		for (ChomboCombinedVolumeMembraneDomain domain : domains){
			if (domain.getVolumeDomainName().equals(name) || domain.getMembraneDomainName().equals(name)){
				return domain;
			}
		}
		throw new RuntimeException("domain '"+name+"' not found");
	}
	
	public List<String> getVolumeAndMembraneDomainNames(){
		ArrayList<String> names = new ArrayList<String>();
		for (ChomboCombinedVolumeMembraneDomain domain : domains){
			names.add(domain.getVolumeDomainName());
			names.add(domain.getMembraneDomainName());
		}
		return names;
	}

	public ChomboCombinedVolumeMembraneDomain getDomain(int i) {
		return domains.get(i);
	}

	public List<ChomboCombinedVolumeMembraneDomain> getCombinedVolumeMembraneDomains() {
		return domains;
	}


}
