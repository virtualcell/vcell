package cbit.vcell.field;

import java.io.Serializable;
import java.util.Hashtable;

import cbit.sql.KeyValue;
import cbit.vcell.simdata.ExternalDataIdentifier;

public class FieldDataDBOperationResults implements Serializable {
	
	public ExternalDataIdentifier[] extDataIDArr;
	public String[] extDataAnnotArr;
	public ExternalDataIdentifier extDataID;
	public Hashtable<String,ExternalDataIdentifier> oldNameNewIDHash;
	public Hashtable<String,KeyValue> oldNameOldExtDataIDKeyHash;
}
