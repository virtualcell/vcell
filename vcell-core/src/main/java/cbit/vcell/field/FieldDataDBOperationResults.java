/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.field;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;


public class FieldDataDBOperationResults implements Serializable {
	
	public ExternalDataIdentifier[] extDataIDArr;
	public String[] extDataAnnotArr;
	public ExternalDataIdentifier extDataID;
	public Hashtable<String,ExternalDataIdentifier> oldNameNewIDHash;
	public Hashtable<String,KeyValue> oldNameOldExtDataIDKeyHash;
	public HashMap<ExternalDataIdentifier, Vector<KeyValue>> extdataIDAndSimRefH;
}
