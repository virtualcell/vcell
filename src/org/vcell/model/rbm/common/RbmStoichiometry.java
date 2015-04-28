package org.vcell.model.rbm.common;

import org.vcell.model.rbm.MolecularType;

public class RbmStoichiometry {

	private MolecularType mt;
	private Integer stoichiometricCoefficient;
		
	public RbmStoichiometry ( MolecularType mt, Integer stoichiometricCoefficient) {
		this.mt = mt;
		this.stoichiometricCoefficient = stoichiometricCoefficient;
	}
	public MolecularType getMolecularType() {
		return mt;
	}
	public Integer getStoichiometricCoefficient() {
		return stoichiometricCoefficient;
	}
}
