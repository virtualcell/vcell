package org.vcell.sbml.test;

public enum BiomodelValidateStage {
	NO_FAIL,
	SBML_IMPORT,
	MATH_GEN,
	COPASI_RUN,
	COPASI_COMPARE;
}
