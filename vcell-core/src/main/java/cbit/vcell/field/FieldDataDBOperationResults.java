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

import org.vcell.restclient.model.FieldDataReference;
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


	public static FieldDataDBOperationResults fieldDataReferencesToDBResults(List<FieldDataReference> dto, User user){
		FieldDataDBOperationResults fieldDataDBOperationResults = new FieldDataDBOperationResults();
		ArrayList<ExternalDataIdentifier> externalDataIdentifiers = new ArrayList<>();
		ArrayList<String> externalDataAnnotations = new ArrayList<>();
		HashMap<ExternalDataIdentifier, Vector<KeyValue>> externalDataIDSimRefs = new HashMap<>();
		for (FieldDataReference fieldDataReference : dto){
			ExternalDataIdentifier externalDataIdentifier = ExternalDataIdentifier.dtoToExternalDataIdentifier(fieldDataReference.getExternalDataIdentifier());
			externalDataIdentifiers.add(externalDataIdentifier);
			externalDataAnnotations.add(fieldDataReference.getExternalDataAnnotation());
			List<KeyValue> keyValues = fieldDataReference.getExternalDataIDSimRef().stream().map(KeyValue::dtoToKeyValue).collect(Collectors.toList());
			externalDataIDSimRefs.put(externalDataIdentifier, new Vector<>(keyValues));
		}
		fieldDataDBOperationResults.extDataIDArr = externalDataIdentifiers.toArray(new ExternalDataIdentifier[0]);
		fieldDataDBOperationResults.extDataAnnotArr = externalDataAnnotations.toArray(new String[0]);
		fieldDataDBOperationResults.extdataIDAndSimRefH = externalDataIDSimRefs;
		return fieldDataDBOperationResults;
	}

}
