package cbit.sql;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.Hashtable;

import org.vcell.util.document.KeyValue;

/**
 * Used for internal references within (or between) Versionable objects
 * Versionable objects themselves are not stored here
 */
public class QueryHashtable {
	private Hashtable<KeyValue, Object> hash = new Hashtable<KeyValue, Object>();

public QueryHashtable() {
	super();
}

public Object get(KeyValue databaseKey) {
	if (databaseKey == null){
		throw new IllegalArgumentException("null key");
	}
	return hash.get(databaseKey);
}

public void put(KeyValue databaseKey, Object object) {
	if (databaseKey==null){
		throw new IllegalArgumentException("databaseKey is null");
	}
	if (object instanceof KeyValue){
		throw new IllegalArgumentException("QueryHashtable.put(): object was a KeyValue, should be the object retrieved from the Database");
	}
	Object oldObject = hash.put(databaseKey, object);
	if (oldObject!=null && (!oldObject.equals(object))){
		throw new RuntimeException("Inserted different object with same key oldObject='"+oldObject+"', newObject='"+object+"'");
	}
}

}
