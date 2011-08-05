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

import org.vcell.util.document.KeyValue;
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
