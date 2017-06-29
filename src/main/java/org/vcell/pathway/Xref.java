/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import static org.vcell.pathway.PathwayXMLHelper.urlHashtable;

import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class Xref extends BioPaxObjectImpl implements UtilityClass {
	private String db;
	private String dbVersion;
	private String id;
	private String idVersion;
	
	public String getDb() {
		return db;
	}
	public String getDbVersion() {
		return dbVersion;
	}
	public String getId() {
		return id;
	}
	public String getIdVersion() {
		return idVersion;
	}
	public String getURL(){
		if(db == null) {
			return null;
		}
		String db_id = db;
		if(db.equals("REACTOME")){
			if(id.contains("REACT")) db_id = "REACTOME_STID";
			else db_id = "REACTOME_ID";
		}else if(db.contains("KEGG"))db_id = "KEGG";
		else if(db.contains("BIOMODELS"))db_id = "BIOMODELS";
		else if(db.contains("CHEBI"))db_id = "ChEBI";
		if(urlHashtable.get(db_id) == null){
//			System.err.println(db);
			return null;
		}
		return urlHashtable.get(db_id).replace("%", id);
	}
	public void setDb(String db) {
		this.db = db;
	}
	public void setDbVersion(String dbVersion) {
		this.dbVersion = dbVersion;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setIdVersion(String idVersion) {
		this.idVersion = idVersion;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printString(sb, "db",db,level);
		printString(sb, "dbVersion",dbVersion,level);
		printString(sb, "id",id,level);
		printString(sb, "idVersion",idVersion,level);
	}
}
