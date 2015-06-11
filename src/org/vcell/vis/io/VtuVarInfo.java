package org.vcell.vis.io;

import java.io.Serializable;

import cbit.vcell.math.VariableType.VariableDomain;

public class VtuVarInfo implements Serializable {
	public final String name;
	public final String displayName;
	public final String domainName;
	public final VariableDomain variableDomain;
	public final boolean bMeshVariable; // true for built-in mesh quantities like global index for FiniteVolume code, or box index for Chombo.
	
	public VtuVarInfo(String name, String displayName, String domainName, VariableDomain variableDomain, boolean bMeshVariable) {
		super();
		this.name = name;
		this.displayName = displayName;
		this.domainName = domainName;
		this.variableDomain = variableDomain;
		this.bMeshVariable = bMeshVariable;
	}
}