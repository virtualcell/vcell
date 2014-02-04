package org.vcell.vis.chombo;

import java.util.ArrayList;
import java.util.List;

public class ChomboDataset {
	
	public static class ChomboDomain {
		private final int ordinal;
		private final String name;
		private final ChomboMesh chomboMesh;
		private final ChomboMeshData chomboMeshData;
		
		public ChomboDomain(String name, ChomboMesh chomboMesh, ChomboMeshData chomboMeshData,int ordinal){
			this.name = name;
			this.chomboMesh = chomboMesh;
			this.chomboMeshData = chomboMeshData;
			this.ordinal = ordinal;
		}
		
		public String getName(){
			return name;
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
	
	private final ArrayList<ChomboDomain> domains = new ArrayList<ChomboDomain>(); 
	
	public ChomboDataset(){
		
	}
	
	public void addDomain(ChomboDomain chomboDomain){
		domains.add(chomboDomain);
	}
	
	public ChomboDomain getDomain(String name){
		for (ChomboDomain domain : domains){
			if (domain.getName().equals(name)){
				return domain;
			}
		}
		throw new RuntimeException("domain '"+name+"' not found");
	}
	
	public List<String> getDomainNames(){
		ArrayList<String> names = new ArrayList<String>();
		for (ChomboDomain domain : domains){
			names.add(domain.getName());
		}
		return names;
	}

	public ChomboDomain getDomain(int i) {
		return domains.get(i);
	}

	public List<ChomboDomain> getDomains() {
		return domains;
	}


}
