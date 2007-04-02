package cbit.vcell.modeldb;

import cbit.vcell.simdata.ExternalDataIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (11/6/2005 9:37:17 AM)
 * @author: Frank Morgan
 */
public class ReferenceQuerySpec implements java.io.Serializable{

	private cbit.sql.VersionableType versionableType;
	private cbit.sql.KeyValue versionableKeyValue;
	private ExternalDataIdentifier extDataID;

/**
 * ReferenceQuesrySpec constructor comment.
 */
public ReferenceQuerySpec(cbit.sql.VersionableType vType,cbit.sql.KeyValue key) {
	
	versionableType = vType;
	versionableKeyValue = key;
}

public ReferenceQuerySpec(ExternalDataIdentifier argEDI) {
	extDataID = argEDI;
}

public boolean isVersionableType(){
	return versionableType != null;
}
public boolean isExternalDataIdentiferType(){
	return extDataID != null;
}
public ExternalDataIdentifier getExternalDataIdentifier(){
	return extDataID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:31:20 AM)
 * @return cbit.sql.KeyValue
 */
public cbit.sql.KeyValue getKeyValue() {
	if(isVersionableType()){
		return versionableKeyValue;
	}else if(isExternalDataIdentiferType()){
		return extDataID.getKey();
	}
	throw new RuntimeException("ReferenceQuesrySpec error: Unknown key type");
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:31:20 AM)
 * @return cbit.sql.VersionableType
 */
public cbit.sql.VersionableType getVersionableType() {
	return versionableType;
}
}