/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.sql;

import org.vcell.util.document.KeyValue;

/**
 * This type was created in VisualAge.
 */
public interface KeyFactory {
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
String getCreateSQL();
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
String getDestroySQL();
/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
KeyValue getNewKey(java.sql.Connection con) throws java.sql.SQLException;
/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
java.math.BigDecimal getUniqueBigDecimal(java.sql.Connection con) throws java.sql.SQLException;
}
