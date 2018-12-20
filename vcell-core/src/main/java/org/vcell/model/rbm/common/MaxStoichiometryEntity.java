package org.vcell.model.rbm.common;

import org.vcell.model.rbm.MolecularType;

public class MaxStoichiometryEntity {
	
	private MolecularType mt = null;
	private Integer value = null;
	private boolean isChanged = false;	// changed in the test map
	
	public MaxStoichiometryEntity(MolecularType mt, Integer value) {
		this.mt = mt;
		this.value = value;
	}
	
	public MolecularType getMolecularType() {
		return mt;
	}
	public void setMolecularType(MolecularType mt) {
		this.mt = mt;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public boolean isChanged() {
		return isChanged;
	}
	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}
}
