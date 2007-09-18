package cbit.vcell.numericstest;

import java.math.BigDecimal;
/**
 * Insert the type's description here.
 * Creation date: (10/19/2004 6:21:59 AM)
 * @author: Frank Morgan
 */
public class EditTestCasesOP extends TestSuiteOP {

	private BigDecimal[] testCasesKeys;
	private String[] newAnnotations;
	private boolean[] newSteadyState;
	
	public EditTestCasesOP(BigDecimal[] editTheseTCases,boolean[] argIsSteadyState) {
		
		super(null);

		if(editTheseTCases.length != argIsSteadyState.length){
			throw new IllegalArgumentException(this.getClass().getName()+" argument array lengths not equal");
		}
		
		testCasesKeys = editTheseTCases;
		newSteadyState = argIsSteadyState;
	}

/**
 * EditTestCriteria constructor comment.
 * @param tsin cbit.vcell.numericstest.TestSuiteInfoNew
 */
public EditTestCasesOP(BigDecimal[] editTheseTCases,String[] argAnnots) {
	
	super(null);

	if(editTheseTCases.length != argAnnots.length){
		throw new IllegalArgumentException(this.getClass().getName()+" argument array lengths not equal");
	}
	
	testCasesKeys = editTheseTCases;
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

public boolean[] getNewSteadyStates(){
	return newSteadyState;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 6:32:46 AM)
 * @return java.math.BigDecimal[]
 */
public java.math.BigDecimal[] getTestCasesKeys() {
	return testCasesKeys;
}
}
