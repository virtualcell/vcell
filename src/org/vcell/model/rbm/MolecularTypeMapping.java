package org.vcell.model.rbm;

public class MolecularTypeMapping {
	/**
	 * either reactant or product molecular type may be null
	 * 
	 * IMPORTANT: can only delete or add a fully defined molecular type.  If reactant or product is null, then other molecular type must be fully defined.
	 * 
	 * IMPORTANT: a wildcard component cannot match an explicitly defined component.
	 */
	MolecularTypePattern reactantMolecularTypePattern;
	MolecularTypePattern productMolecularTypePattern;

}
