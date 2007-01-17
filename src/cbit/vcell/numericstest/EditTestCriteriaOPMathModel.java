package cbit.vcell.numericstest;

import cbit.sql.KeyValue;
/**
 * Insert the type's description here.
 * Creation date: (11/11/2004 1:48:13 PM)
 * @author: Frank Morgan
 */
public class EditTestCriteriaOPMathModel extends EditTestCriteriaOP {

	private KeyValue mathModelRegrSimRef;
	private KeyValue mathModelRegrRef;
/**
 * EditTestCriteriaOpMathModel constructor comment.
 * @param editThisTCrit java.math.BigDecimal
 * @param argRegrSimRef cbit.sql.KeyValue
 */
public EditTestCriteriaOPMathModel(
    java.math.BigDecimal editThisTCrit,
    KeyValue argMMRegrRef,
    KeyValue argMMRegrSimRef,
    Double argMaxAbsError,
    Double argMaxRelError) {
    super(editThisTCrit, argMaxAbsError, argMaxRelError,null,null);

    mathModelRegrSimRef = argMMRegrSimRef;
    mathModelRegrRef = argMMRegrRef;
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 1:57:45 PM)
 * @return cbit.sql.KeyValue
 */
public KeyValue getMathModelRegressionRef() {
	return mathModelRegrRef;
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 1:57:45 PM)
 * @return cbit.sql.KeyValue
 */
public KeyValue getMathModelRegressionSimRef() {
	return mathModelRegrSimRef;
}
}
