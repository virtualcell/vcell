/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
