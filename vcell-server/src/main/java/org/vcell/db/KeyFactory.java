/*
 * Copyright (C) 1999-2017 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.db;

import org.vcell.util.document.KeyValue;

public interface KeyFactory {
	
	String getCreateSQL();
	
	String getDestroySQL();
	
	String nextSEQ();
	
	String currSEQ();
	
	KeyValue getNewKey(java.sql.Connection con) throws java.sql.SQLException;
	
	java.math.BigDecimal getUniqueBigDecimal(java.sql.Connection con) throws java.sql.SQLException;
}
