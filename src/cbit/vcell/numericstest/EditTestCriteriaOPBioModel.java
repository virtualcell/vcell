package cbit.vcell.numericstest;

import cbit.sql.KeyValue;
/**
 * Insert the type's description here.
 * Creation date: (11/11/2004 1:48:13 PM)
 * @author: Frank Morgan
 */
public class EditTestCriteriaOPBioModel extends EditTestCriteriaOP {

	private KeyValue bioModelRegrSimRef;
	private KeyValue bioModelRegrRef;
/**
 * EditTestCriteriaOpMathModel constructor comment.
 * @param editThisTCrit java.math.BigDecimal
 * @param argRegrSimRef cbit.sql.KeyValue
 */
public EditTestCriteriaOPBioModel(
    java.math.BigDecimal editThisTCrit,
    KeyValue argBMRegrRef,
    KeyValue argBMRegrSimRef,
    Double argMaxAbsError,
    Double argMaxRelError) {
    super(editThisTCrit, argMaxAbsError, argMaxRelError,null,null);

    bioModelRegrSimRef = argBMRegrSimRef;
    bioModelRegrRef = argBMRegrRef;
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 1:57:45 PM)
 * @return cbit.sql.KeyValue
 */
public KeyValue getBioModelRegressionRef() {
	return bioModelRegrRef;
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 1:57:45 PM)
 * @return cbit.sql.KeyValue
 */
public KeyValue getBioModelRegressionSimRef() {
	return bioModelRegrSimRef;
}
}
