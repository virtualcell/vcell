package org.vcell.pathway;

import static org.vcell.pathway.PathwayXMLHelper.*;

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
		String db_id = db;
		if(db.equals("REACTOME")){
			if(id.contains("REACT")) db_id = "REACTOME_STID";
			else db_id = "REACTOME_ID";
		}
		if(urlHashtable.get(db_id) == null){
//			System.err.println(db);
			return null;
		}
		return urlHashtable.get(db_id) + id;

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

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printString(sb, "db",db,level);
		printString(sb, "dbVersion",dbVersion,level);
		printString(sb, "id",id,level);
		printString(sb, "idVersion",idVersion,level);
	}
}
