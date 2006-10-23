package cbit.vcell.numericstest;

import java.math.BigDecimal;

import cbit.util.document.KeyValue;
/**
 * Insert the type's description here.
 * Creation date: (10/19/2004 6:21:59 AM)
 * @author: Frank Morgan
 */
public class AddTestCriteriaOPBioModel extends AddTestCriteriaOP {

	private KeyValue bioModelSimKey;
	private KeyValue regressionBioModelKey;
	private KeyValue regressionBioModelSimKey;
/**
 * EditTestCriteria constructor comment.
 * @param tsin cbit.vcell.numericstest.TestSuiteInfoNew
 */
public AddTestCriteriaOPBioModel(BigDecimal argTCKey,KeyValue argBMSimKey,KeyValue argRegrBMKey,KeyValue argRegrBMSimKey,Double argMAE,Double argMRE,AddTestResultsOP argATROP) {
	
	super(argTCKey,argMAE,argMRE,argATROP);
	
	if(argBMSimKey == null){
		throw new IllegalArgumentException(this.getClass().getName()+" simulationKey cannot be null");
	}

	bioModelSimKey = argBMSimKey;
	regressionBioModelKey = argRegrBMKey;
	regressionBioModelSimKey = argRegrBMSimKey;
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 5:07:05 PM)
 * @return cbit.sql.KeyValue
 */
public cbit.util.document.KeyValue getBioModelSimKey() {
	return bioModelSimKey;
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 5:07:05 PM)
 * @return cbit.sql.KeyValue
 */
public cbit.util.document.KeyValue getRegressionBioModelKey() {
	return regressionBioModelKey;
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 5:07:05 PM)
 * @return cbit.sql.KeyValue
 */
public cbit.util.document.KeyValue getRegressionBioModelSimKey() {
	return regressionBioModelSimKey;
}
}
