package cbit.vcell.numericstest;

import java.math.BigDecimal;
/**
 * Insert the type's description here.
 * Creation date: (10/19/2004 6:21:59 AM)
 * @author: Frank Morgan
 */
public class RemoveTestCriteriaOP extends TestSuiteOP {

	private BigDecimal[] testCriteriaKeys;
/**
 * EditTestCriteria constructor comment.
 * @param tsin cbit.vcell.numericstest.TestSuiteInfoNew
 */
public RemoveTestCriteriaOP(BigDecimal[] argTestCriteriaKeys) {
	
	super(null);

	testCriteriaKeys = argTestCriteriaKeys;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 6:39:52 AM)
 * @return java.math.BigDecimal[]
 */
public java.math.BigDecimal[] getTestCriteriaKeys() {
	return testCriteriaKeys;
}
}
