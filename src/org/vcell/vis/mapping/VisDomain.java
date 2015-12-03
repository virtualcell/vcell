package org.vcell.vis.mapping;

import org.vcell.vis.vismesh.thrift.VisMesh;

public class VisDomain {
	private final String name;
	private final VisMesh visMesh;
	
	public VisDomain(String name, VisMesh visMesh){
		this.name = name;
		this.visMesh = visMesh;
	}
	
	public String getName(){
		return name;
	}
	
	public VisMesh getVisMesh() {
		return visMesh;
	}
}