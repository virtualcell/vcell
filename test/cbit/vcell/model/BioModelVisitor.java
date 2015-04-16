package cbit.vcell.model;

import org.vcell.util.document.BioModelInfo;

import cbit.vcell.biomodel.BioModel;

public interface BioModelVisitor {

	/**
	 * @param bmi not null
	 * @return true if model should be retrieved from database
	 */
	boolean filterBioModel(BioModelInfo bmi);

	/**
	 * process model  
	 * @param bioModel not null
	 */
	void visitBioModel(BioModel bioModel);

}
