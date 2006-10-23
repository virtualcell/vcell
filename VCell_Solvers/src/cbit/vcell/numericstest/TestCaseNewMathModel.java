package cbit.vcell.numericstest;

import cbit.util.document.MathModelInfo;
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
public TestCaseNewMathModel(java.math.BigDecimal argTCKey, MathModelInfo argMMInfo, String argType, String argAnnot, cbit.vcell.numericstest.TestCriteriaNew[] argTestCriterias) {
	super(argTCKey,argMMInfo.getVersion(),argType,argAnnot, argTestCriterias);

	mathModelInfo = argMMInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (11/10/2004 9:59:54 AM)
 */
public MathModelInfo getMathModelInfo() {
	return mathModelInfo;
}
}
