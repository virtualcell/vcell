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

import java.util.*;

import org.vcell.util.ObjectReferenceWrapper;
import org.vcell.util.document.KeyValue;
/**
 * Used for internal references within (or between) Versionable objects
 * Versionable objects themselves are not stored here
 */
public class InsertHashtable {
	private Hashtable<Object, KeyValue> hash = new Hashtable<Object, KeyValue>();
/**
 * InsertHashtable constructor comment.
 */
public InsertHashtable() {
	super();
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param object java.lang.Object
 */
public KeyValue getDatabaseKey(Object objectReference) {
	if (objectReference == null){
		throw new IllegalArgumentException("null objectReference");
	}
	return (KeyValue)hash.get(new ObjectReferenceWrapper(objectReference));
}
/**
 * This method was created in VisualAge.
 * @param object java.lang.Object
 * @param key cbit.sql.KeyValue
 */
public void put(Object objectReference, KeyValue databaseKey) {
	if (databaseKey==null){
		throw new IllegalArgumentException("databaseKey is null");
	}
	if (objectReference instanceof KeyValue){
		throw new IllegalArgumentException("InsertHashtable.put(): object reference was a KeyValue, should be the object to be stored in the Database");
	}
	KeyValue oldKeyValue = (KeyValue)hash.put(new ObjectReferenceWrapper(objectReference),databaseKey);
	if (oldKeyValue!=null && (!oldKeyValue.equals(databaseKey))){
		throw new RuntimeException("Inserted same objectReference with different keys old='"+oldKeyValue+"', new='"+databaseKey+"'");
	}
}
}
