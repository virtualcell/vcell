package cbit.vcell.numericstest;

import java.math.BigDecimal;

public class EditTestSuiteOP extends TestSuiteOP {
	private BigDecimal[] testSuiteKeys;
	private String[] newAnnotations;
	

/**
 * EditTestCriteria constructor comment.
 * @param tsin cbit.vcell.numericstest.TestSuiteInfoNew
 */
public EditTestSuiteOP(BigDecimal[] editTheseTestSuites,String[] argAnnots) {
	
	super(null);

	if(editTheseTestSuites.length != argAnnots.length){
		throw new IllegalArgumentException(this.getClass().getName()+" argument array lengths not equal");
	}
	
	testSuiteKeys = editTheseTestSuites;
	newAnnotations = argAnnots;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 6:32:46 AM)
 * @return java.lang.String[]
 */
public java.lang.String[] getNewAnnotations() {
	return newAnnotations;
}

/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 6:32:46 AM)
 * @return java.math.BigDecimal[]
 */
public java.math.BigDecimal[] getTestSuiteKeys() {
	return testSuiteKeys;
}

}
