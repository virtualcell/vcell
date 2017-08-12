package org.vcell.vis.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VtuFileContainer implements Serializable {
	
	public static class VtuMesh implements Serializable {
		public final String domainName;
		public final double time;
		public final byte[] vtuMeshContents;
		
		public VtuMesh(String domainName, double time, byte[] vtuMeshContents){
			this.domainName = domainName;
			this.time = time;
			this.vtuMeshContents = vtuMeshContents;
		}
	}
	
	public final ArrayList<VtuMesh> vtuMeshes = new ArrayList<VtuMesh>();
	
	public VtuFileContainer() {
	}
	
	public void addVtuMesh(VtuMesh vtuMesh){
		vtuMeshes.add(vtuMesh);
	}
	
	public List<VtuMesh> getVtuMeshes(){
		return vtuMeshes;
	}

}
