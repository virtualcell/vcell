package cbit.vcell.numericstest;

import java.math.BigDecimal;

public class ChangeTestCriteriaErrorLimitOP extends TestSuiteOP{

	private BigDecimal[] changeTheseTCrit;
	private double[] absErrorLimits;
	private double[] relErrorLimits;
	
	public ChangeTestCriteriaErrorLimitOP(BigDecimal[] argChangeTheseTCrit, double[] argAbsErrorLimits,double[] argRelErrorLimits) {
		super(null);
		changeTheseTCrit = argChangeTheseTCrit;
		absErrorLimits = argAbsErrorLimits;
		relErrorLimits = argRelErrorLimits;
	}

	public BigDecimal[] getTestCriteriaKeys(){
		return changeTheseTCrit;
	}

	public double[] getAbsErrorLimits() {
		return absErrorLimits;
	}

	public double[] getRelErrorLimits() {
		return relErrorLimits;
	}
}
