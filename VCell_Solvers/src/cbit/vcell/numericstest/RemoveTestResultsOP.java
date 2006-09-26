package cbit.vcell.numericstest;

import java.math.BigDecimal;
/**
 * Insert the type's description here.
 * Creation date: (10/19/2004 6:10:48 AM)
 * @author: Frank Morgan
 */
public class RemoveTestResultsOP extends TestSuiteOP {

	private BigDecimal[] testCriteriaKeys;
/**
 * Insert the method's description here.
 * Creation date: (10/19/2004 6:12:02 AM)
 */
public RemoveTestResultsOP(BigDecimal[] argTCritKeys) {
	
	super(null);

	testCriteriaKeys = argTCritKeys;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 6:41:55 AM)
 * @return java.math.BigDecimal[]
 */
public java.math.BigDecimal[] getTestCriteriaKeys() {
	return testCriteriaKeys;
}
}
