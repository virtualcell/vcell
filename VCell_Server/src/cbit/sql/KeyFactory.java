package cbit.sql;

import cbit.util.KeyValue;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
