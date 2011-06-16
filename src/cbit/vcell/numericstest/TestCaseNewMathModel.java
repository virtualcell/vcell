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

import org.vcell.util.document.MathModelInfo;
/**
 * Insert the type's description here.
 * Creation date: (11/10/2004 9:55:10 AM)
 * @author: Frank Morgan
 */
public class TestCaseNewMathModel extends TestCaseNew {

	private MathModelInfo mathModelInfo;

/**
 * TestCaseNewMathModel constructor comment.
 * @param argTCKey java.math.BigDecimal
 * @param argMMInfo cbit.vcell.mathmodel.MathModelInfo
 * @param argType java.lang.String
 * @param argAnnot java.lang.String
 * @param argTestCriterias cbit.vcell.numericstest.TestCriteriaNew[]
 */
public TestCaseNewMathModel(java.math.BigDecimal argTCKey, org.vcell.util.document.MathModelInfo argMMInfo, String argType, String argAnnot, cbit.vcell.numericstest.TestCriteriaNew[] argTestCriterias) {
	super(argTCKey,argMMInfo.getVersion(),argType,argAnnot, argTestCriterias);

	mathModelInfo = argMMInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (11/10/2004 9:59:54 AM)
 */
public org.vcell.util.document.MathModelInfo getMathModelInfo() {
	return mathModelInfo;
}
}
