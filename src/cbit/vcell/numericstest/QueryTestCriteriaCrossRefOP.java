package cbit.vcell.numericstest;

import java.math.BigDecimal;

public class QueryTestCriteriaCrossRefOP extends TestSuiteOP {

	private BigDecimal testCriterium;
	private String varName;
	
	public QueryTestCriteriaCrossRefOP(BigDecimal argTSKey,BigDecimal argTCritKey,String argVarName) {
		super(argTSKey);
		testCriterium = argTCritKey;
		varName = argVarName;
	}

	public BigDecimal getTestCriterium() {
		return testCriterium;
	}
	public String getVarName(){
		return varName;
	}

}
