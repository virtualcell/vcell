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
/**
 * Insert the type's description here.
 * Creation date: (10/19/2004 6:10:48 AM)
 * @author: Frank Morgan
 */
public class RemoveTestCasesOP extends TestSuiteOP {

	private BigDecimal[] testCasesKeys;
/**
 * Insert the method's description here.
 * Creation date: (10/19/2004 6:12:02 AM)
 */
public RemoveTestCasesOP(BigDecimal[] argTCKeys) {
	
	super(null);

	testCasesKeys = argTCKeys;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 6:35:33 AM)
 * @return java.math.BigDecimal[]
 */
public java.math.BigDecimal[] getTestCasesKeys() {
	return testCasesKeys;
}
}
