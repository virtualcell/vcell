package org.vcell.vis.vismesh;

import java.util.ArrayList;
import java.util.List;


public class VisDataset {
	
	public static class VisDomain {
		private final String name;
		private final VisMesh visMesh;
		private final VisMeshData visMeshData;
		
		public VisDomain(String name, VisMesh visMesh, VisMeshData visMeshData){
			this.name = name;
			this.visMesh = visMesh;
			this.visMeshData = visMeshData;
		}
		
		public String getName(){
			return name;
		}
		
		public VisMesh getVisMesh() {
			return visMesh;
		}
		public VisMeshData getVisMeshData() {
			return visMeshData;
		}
	}
	
	private final ArrayList<VisDomain> domains = new ArrayList<VisDomain>(); 
	
	public VisDataset(){
	}
	
	public void addDomain(VisDomain domain){
		domains.add(domain);
	}
	
	public VisDomain getDomain(String name){
		for (VisDomain domain : domains){
			if (domain.getName().equals(name)){
				return domain;
			}
		}
		throw new RuntimeException("domain '"+name+"' not found");
	}
	
	public List<String> getDomainNames(){
		ArrayList<String> names = new ArrayList<String>();
		for (VisDomain domain : domains){
			names.add(domain.getName());
		}
		return names;
	}

	public List<VisDomain> getDomains() {
		return domains;
	}

}
