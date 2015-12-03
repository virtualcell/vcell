package org.vcell.vis.chombo;

import java.util.ArrayList;
import java.util.List;

public class ChomboDataset {
	
	public static class ChomboCombinedVolumeMembraneDomain {
		private final int ordinal;
		private final String volumeDomainName;
		private final String membraneDomainName;
		private final ChomboMesh chomboMesh;
		private final ChomboMeshData chomboMeshData;
		
		public ChomboCombinedVolumeMembraneDomain(String volumeDomainName, String membraneDomainName, ChomboMesh chomboMesh, ChomboMeshData chomboMeshData,int ordinal){
			this.volumeDomainName = volumeDomainName;
			this.membraneDomainName = membraneDomainName;
			this.chomboMesh = chomboMesh;
			this.chomboMeshData = chomboMeshData;
			this.ordinal = ordinal;
		}
		
		public String getVolumeDomainName(){
			return volumeDomainName;
		}
		
		public String getMembraneDomainName(){
			return membraneDomainName;
		}
		
		public ChomboMesh getChomboMesh() {
			return chomboMesh;
		}
		public ChomboMeshData getChomboMeshData() {
			return chomboMeshData;
		}
		public int getOrdinal(){
			return this.ordinal;
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
