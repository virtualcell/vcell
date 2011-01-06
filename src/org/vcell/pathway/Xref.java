package org.vcell.pathway;

public class Xref implements UtilityClass {
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
}
