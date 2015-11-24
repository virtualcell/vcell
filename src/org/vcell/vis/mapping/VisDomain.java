package org.vcell.vis.mapping;

import org.vcell.vis.vismesh.VisMesh;

public class VisDomain {
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