package cbit.vcell.field;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import cbit.sql.KeyValue;
import cbit.vcell.modeldb.VersionableTypeVersion;
import cbit.vcell.server.User;
import cbit.vcell.simdata.ExternalDataIdentifier;

public class FieldDataDBOperationSpec implements Serializable {

	public int opType;
	public ExternalDataIdentifier specEDI;
	public User owner;
	public String newExtDataIDName;
	public String annotation;
	public String[] sourceNames;
	public VersionableTypeVersion sourceOwner;
	public boolean bIncludeSimRefs = false;
	
	public static final int FDDBOS_DELETE = 0;
	public static final int FDDBOS_GETEXTDATAIDS = 1;
	public static final int FDDBOS_SAVEEXTDATAID = 2;
	public static final int FDDBOS_COPY_NO_CONFLICT = 3;


	private FieldDataDBOperationSpec(){
	}
	
	private static FieldDataDBOperationSpec createOwnedFieldDataDBOperationSpec(User owner){
		FieldDataDBOperationSpec fieldDataDBOperationSpec =
			new FieldDataDBOperationSpec();
		fieldDataDBOperationSpec.owner = owner;
		return fieldDataDBOperationSpec;
	}
	public static FieldDataDBOperationSpec createCopyNoConflictExtDataIDsSpec(
			User argOwner,String[] argSourceNames,VersionableTypeVersion argSourceOwner){
		FieldDataDBOperationSpec fieldDataDBOperationSpec =
			createOwnedFieldDataDBOperationSpec(argOwner);
		fieldDataDBOperationSpec.opType = fieldDataDBOperationSpec.FDDBOS_COPY_NO_CONFLICT;
		fieldDataDBOperationSpec.sourceNames = argSourceNames;
		fieldDataDBOperationSpec.sourceOwner = argSourceOwner;
		return fieldDataDBOperationSpec;
	}
	public static FieldDataDBOperationSpec createGetExtDataIDsSpec(User owner){
		FieldDataDBOperationSpec fieldDataDBOperationSpec =
			createOwnedFieldDataDBOperationSpec(owner);
		fieldDataDBOperationSpec.opType = fieldDataDBOperationSpec.FDDBOS_GETEXTDATAIDS;
		return fieldDataDBOperationSpec;
	}
	public static FieldDataDBOperationSpec createGetExtDataIDsSpecWithSimRefs(User owner){
		FieldDataDBOperationSpec fieldDataDBOperationSpec =
			createOwnedFieldDataDBOperationSpec(owner);
		fieldDataDBOperationSpec.opType = fieldDataDBOperationSpec.FDDBOS_GETEXTDATAIDS;
		fieldDataDBOperationSpec.bIncludeSimRefs = true;
		return fieldDataDBOperationSpec;
	}
	public static FieldDataDBOperationSpec createSaveNewExtDataIDSpec(
			User owner,String newExtDataIDName,String newExtDataAnnot){
		FieldDataDBOperationSpec fieldDataDBOperationSpec =
			createOwnedFieldDataDBOperationSpec(owner);
		fieldDataDBOperationSpec.opType = fieldDataDBOperationSpec.FDDBOS_SAVEEXTDATAID;
		fieldDataDBOperationSpec.newExtDataIDName = newExtDataIDName;
		fieldDataDBOperationSpec.annotation = newExtDataAnnot;
		return fieldDataDBOperationSpec;
	}
	public static FieldDataDBOperationSpec createDeleteExtDataIDSpec(ExternalDataIdentifier deleteExtDataID){
		FieldDataDBOperationSpec fieldDataDBOperationSpec =
			createOwnedFieldDataDBOperationSpec(deleteExtDataID.getOwner());
		fieldDataDBOperationSpec.opType = fieldDataDBOperationSpec.FDDBOS_DELETE;
		fieldDataDBOperationSpec.specEDI = deleteExtDataID;
		return fieldDataDBOperationSpec;
	}
//	public static FieldDataDBOperationSpec createAdminGetAllExtDataKeysSpec(){
//		FieldDataDBOperationSpec fieldDataDBOperationSpec =
//			createOwnedFieldDataDBOperationSpec(null);
//		fieldDataDBOperationSpec.opType = fieldDataDBOperationSpec.FDDBOS_ADMIN_GETALLEXTDATAKEYS;
//		return fieldDataDBOperationSpec;
//	}

}