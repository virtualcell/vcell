package cbit.vcell.numericstest;

import java.math.BigDecimal;

import cbit.util.KeyValue;
import cbit.vcell.solver.test.VariableComparisonSummary;
/**
 * Insert the type's description here.
 * Creation date: (10/19/2004 6:10:48 AM)
 * @author: Frank Morgan
 */
public class AddTestResultsOP extends TestSuiteOP {

	private BigDecimal testCriteriaKey;
	private VariableComparisonSummary[] variableComparisonSummaries;
/**
 * Insert the method's description here.
 * Creation date: (10/19/2004 6:12:02 AM)
 */
public AddTestResultsOP(VariableComparisonSummary[] argVCS) {
	
	this(null,argVCS);
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2004 6:12:02 AM)
 */
public AddTestResultsOP(BigDecimal argTCritKey,VariableComparisonSummary[] argVCS) {
	
	super(null);

	testCriteriaKey = argTCritKey;//Must be null if child of an AddTestCriteriaOP
	
	if(argVCS == null){
		throw new IllegalArgumentException(this.getClass().getName()+" VariableComparisonSummary cannot be null");
	}

	variableComparisonSummaries = argVCS;

}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 6:10:30 AM)
 * @return cbit.sql.KeyValue
 */
public BigDecimal getTestCriteriaKey() {
	return testCriteriaKey;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 6:10:30 AM)
 * @return cbit.vcell.solver.test.VariableComparisonSummary
 */
public cbit.vcell.solver.test.VariableComparisonSummary[] getVariableComparisonSummaries() {
	return variableComparisonSummaries;
}
}
