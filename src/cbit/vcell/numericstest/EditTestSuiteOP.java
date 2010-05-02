package cbit.vcell.numericstest;

import java.math.BigDecimal;

public class EditTestSuiteOP extends TestSuiteOP {
	private BigDecimal[] testSuiteKeys;
	private String[] newAnnotations;
	private Boolean bLock = null;
	

	public EditTestSuiteOP(BigDecimal[] editTheseTestSuites,boolean bLock) {
		
		super(null);
		if(!bLock){
			throw new IllegalArgumentException("Only lock == true implemented for EditTestSuiteOP");
		}
		testSuiteKeys = editTheseTestSuites;
		this.bLock = new Boolean(true);
	}
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
public Boolean isLock(){
	return bLock;
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
