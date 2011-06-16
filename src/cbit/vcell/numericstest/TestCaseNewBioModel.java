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

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;

/**
 * Insert the type's description here.
 * Creation date: (11/10/2004 9:55:10 AM)
 * @author: Frank Morgan
 */
public class TestCaseNewBioModel extends TestCaseNew {

	private BioModelInfo bioModelInfo;
	private String simContextName;
	private KeyValue simContextKey;

/**
 * TestCaseNewMathModel constructor comment.
 * @param argTCKey java.math.BigDecimal
 * @param argMMInfo cbit.vcell.mathmodel.MathModelInfo
 * @param argType java.lang.String
 * @param argAnnot java.lang.String
 * @param argTestCriterias cbit.vcell.numericstest.TestCriteriaNew[]
 */
public TestCaseNewBioModel(java.math.BigDecimal argTCKey, BioModelInfo argBMInfo, String argSimContextName, KeyValue argSimContextKey,String argType, String argAnnot, cbit.vcell.numericstest.TestCriteriaNew[] argTestCriterias) {
	super(argTCKey,argBMInfo.getVersion(),argType,argAnnot, argTestCriterias);

	bioModelInfo = argBMInfo;
	simContextKey = argSimContextKey;
	simContextName = argSimContextName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/10/2004 9:59:54 AM)
 */
public BioModelInfo getBioModelInfo() {
	return bioModelInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (11/10/2004 12:31:02 PM)
 * @return java.lang.String
 */
public KeyValue getSimContextKey() {
	return simContextKey;
}
/**
 * Insert the method's description here.
 * Creation date: (11/10/2004 12:31:02 PM)
 * @return java.lang.String
 */
public String getSimContextName() {
	return simContextName;
}
}
