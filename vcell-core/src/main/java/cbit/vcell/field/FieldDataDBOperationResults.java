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
import java.util.*;
import java.util.stream.Collectors;

import org.vcell.restclient.model.FieldDataReferences;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;


public class FieldDataDBOperationResults implements Serializable {
	
	public ExternalDataIdentifier[] extDataIDArr;
	public String[] extDataAnnotArr;
	public ExternalDataIdentifier extDataID;
	public Hashtable<String,ExternalDataIdentifier> oldNameNewIDHash;
	public Hashtable<String,KeyValue> oldNameOldExtDataIDKeyHash;
	public HashMap<ExternalDataIdentifier, Vector<KeyValue>> extdataIDAndSimRefH;


	public static FieldDataDBOperationResults fieldDataReferencesToDBResults(FieldDataReferences dto, User user){
		FieldDataDBOperationResults fieldDataDBOperationResults = new FieldDataDBOperationResults();
		fieldDataDBOperationResults.extDataIDArr = dto.getExternalDataIdentifiers().stream().map(ExternalDataIdentifier::dtoToExternalDataIdentifier).toArray(ExternalDataIdentifier[]::new);
		fieldDataDBOperationResults.extDataAnnotArr = dto.getExternalDataAnnotations().toArray(new String[0]);
		fieldDataDBOperationResults.extdataIDAndSimRefH = new HashMap<>();
		if (dto.getExternalDataIDSimRefs() == null){
			return fieldDataDBOperationResults;
		}
		for (Map.Entry<String, List<org.vcell.restclient.model.KeyValue>> entry : dto.getExternalDataIDSimRefs().entrySet()){
			fieldDataDBOperationResults.extdataIDAndSimRefH.put(new ExternalDataIdentifier(new KeyValue(entry.getKey()), user, ""),
					entry.getValue().stream().map(KeyValue::dtoToKeyValue).collect(Collectors.toCollection(Vector::new)));
		}
		return fieldDataDBOperationResults;
	}

}
