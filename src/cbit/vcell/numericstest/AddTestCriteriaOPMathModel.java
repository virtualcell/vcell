package cbit.vcell.numericstest;

import cbit.sql.KeyValue;
import java.math.BigDecimal;
/**
 * Insert the type's description here.
 * Creation date: (10/19/2004 6:21:59 AM)
 * @author: Frank Morgan
 */
public class AddTestCriteriaOPMathModel extends AddTestCriteriaOP {

	private KeyValue mathModelSimKey;
	private KeyValue regressionMathModelKey;
	private KeyValue regressionMathModelSimKey;
/**
 * EditTestCriteria constructor comment.
 * @param tsin cbit.vcell.numericstest.TestSuiteInfoNew
 */
public AddTestCriteriaOPMathModel(BigDecimal argTCKey,KeyValue argMMSimKey,KeyValue argRegrMMKey,KeyValue argRegrMMSimKey,Double argMAE,Double argMRE,AddTestResultsOP argATROP) {
	
	super(argTCKey,argMAE,argMRE,argATROP);
	
	if(argMMSimKey == null){
		throw new IllegalArgumentException(this.getClass().getName()+" simulationKey cannot be null");
	}

	mathModelSimKey = argMMSimKey;
	regressionMathModelKey = argRegrMMKey;
	regressionMathModelSimKey = argRegrMMSimKey;
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 5:04:01 PM)
 * @return cbit.sql.KeyValue
 */
public cbit.sql.KeyValue getMathModelSimKey() {
	return mathModelSimKey;
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 5:04:01 PM)
 * @return cbit.sql.KeyValue
 */
public cbit.sql.KeyValue getRegressionMathModelKey() {
	return regressionMathModelKey;
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 5:04:01 PM)
 * @return cbit.sql.KeyValue
 */
public cbit.sql.KeyValue getRegressionMathModelSimKey() {
	return regressionMathModelSimKey;
}
}
