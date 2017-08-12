package org.vcell.solver.comsol.model;

public class VCCPhysicsField extends VCCBase {
	public final String builtinName;
	public VCCPhysicsField(String builtinName, String newName) {
		super(newName);
		this.builtinName = builtinName;
	}

}
