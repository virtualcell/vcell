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
 * Creation date: (10/19/2004 6:07:27 AM)
 * @author: Frank Morgan
 */
public class TestSuiteOP implements java.io.Serializable {

	private BigDecimal tsKey;

/**
 * TestSuiteOP constructor comment.
 */
protected TestSuiteOP(BigDecimal argTSKey) {

	tsKey = argTSKey;
}


/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 5:34:58 AM)
 * @return cbit.sql.KeyValue
 */
public BigDecimal getTestSuiteKey() {
	return tsKey;
}
}
