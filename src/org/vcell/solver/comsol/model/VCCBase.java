package org.vcell.solver.comsol.model;

import java.io.Serializable;

public abstract class VCCBase implements Serializable {
	public final String name;
	public VCCBase(String name){
		this.name = name;
	}
	//public abstract void write(PrintWriter pw);
}