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
