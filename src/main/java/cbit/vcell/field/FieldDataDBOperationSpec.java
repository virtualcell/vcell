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

import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.User;
import org.vcell.util.document.VersionableTypeVersion;


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
