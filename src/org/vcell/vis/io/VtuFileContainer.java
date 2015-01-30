package org.vcell.vis.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cbit.vcell.math.VariableType.VariableDomain;

public class VtuFileContainer implements Serializable {
	
	public static class VtuVarInfo implements Serializable {
		public final String name;
		public final String displayName;
		public final String domainName;
		public final VariableDomain variableDomain;
		
		public VtuVarInfo(String name, String displayName, String domainName, VariableDomain variableDomain) {
			super();
			this.name = name;
			this.displayName = displayName;
			this.domainName = domainName;
			this.variableDomain = variableDomain;
		}
	}
	
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
	public final ArrayList<VtuVarInfo> vtuVarInfos = new ArrayList<VtuVarInfo>();
	
	public VtuFileContainer() {
	}
	
	public void addVtuMesh(VtuMesh vtuMesh){
		vtuMeshes.add(vtuMesh);
	}
	
	public List<VtuMesh> getVtuMeshes(){
		return vtuMeshes;
	}

	public void addVtuVarInfo(VtuVarInfo vtuVarInfo){
		vtuVarInfos.add(vtuVarInfo);
	}
	
	public List<VtuVarInfo> getVtuVarInfos(){
		return vtuVarInfos;
	}

}
