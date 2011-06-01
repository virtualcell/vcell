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

import org.vcell.util.document.KeyValue;
/**
 * Insert the type's description here.
 * Creation date: (11/10/2004 12:01:58 PM)
 * @author: Frank Morgan
 */
public class AddTestCasesOPBioModel extends AddTestCasesOP {

	private KeyValue bioModelKey;
	private KeyValue simContextKey;
	private AddTestCriteriaOPBioModel[] addTestCriteriaOPsBioModel;

/**
 * AddTestCasesOpMathModel constructor comment.
 * @param argMMKey cbit.sql.KeyValue
 * @param argType java.lang.String
 * @param argAnnot java.lang.String
 * @param argAddTestCriteriaOPs cbit.vcell.numericstest.AddTestCriteriaOP[]
 */
public AddTestCasesOPBioModel(
    KeyValue argBioModelKey,
    KeyValue argSimContextKey,
    String argType,
    String argAnnot,
    cbit.vcell.numericstest.AddTestCriteriaOPBioModel[] argAddTestCriteriaOPs) {
    this(
        null,
        argBioModelKey,
        argSimContextKey,
        argType,
        argAnnot,
        argAddTestCriteriaOPs);
}
/**
 * AddTestCasesOpMathModel constructor comment.
 * @param argTSKey java.math.BigDecimal
 * @param argMMKey cbit.sql.KeyValue
 * @param argType java.lang.String
 * @param argAnnot java.lang.String
 * @param argAddTestCriteriaOPs cbit.vcell.numericstest.AddTestCriteriaOP[]
 */
public AddTestCasesOPBioModel(
    BigDecimal argTSKey,
    KeyValue argBioModelKey,
    KeyValue argSimContextKey,
    String argType,
    String argAnnot,
    AddTestCriteriaOPBioModel[] argAddTestCriteriaOPsBioModel) {
    super(argTSKey, argType, argAnnot);
    if (argBioModelKey == null || argSimContextKey == null) {
        throw new IllegalArgumentException(
            this.getClass().getName() + " bioModelKey and simContextKey cannot be null");
    }
    // argAddTestCriteriaOPs can be null,but if not must have all TestSuiteKeys null
    if (argAddTestCriteriaOPsBioModel != null) {
        if (argAddTestCriteriaOPsBioModel.length == 0) {
            throw new IllegalArgumentException(
                this.getClass().getName() + " TestCriteriaOPs are not null but are empty");
        }
        for (int i = 0; i < argAddTestCriteriaOPsBioModel.length; i += 1) {
            if (argAddTestCriteriaOPsBioModel[i].getTestSuiteKey() != null) {
                throw new IllegalArgumentException(
                    this.getClass().getName() + " overrides TestSuiteKeys in children");
            }
        }
    }
    
    addTestCriteriaOPsBioModel = argAddTestCriteriaOPsBioModel;

    bioModelKey = argBioModelKey;
    simContextKey = argSimContextKey;
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 5:23:47 PM)
 * @return cbit.vcell.numericstest.AddTestCriteriaOPBioModel[]
 */
public cbit.vcell.numericstest.AddTestCriteriaOPBioModel[] getAddTestCriteriaOPsBioModel() {
	return addTestCriteriaOPsBioModel;
}
/**
 * Insert the method's description here.
 * Creation date: (11/10/2004 12:03:14 PM)
 */
public org.vcell.util.document.KeyValue getBioModelKey() {
	return bioModelKey;
}
/**
 * Insert the method's description here.
 * Creation date: (11/10/2004 12:03:14 PM)
 */
public org.vcell.util.document.KeyValue getSimContextKey() {
	return simContextKey;
}
}
