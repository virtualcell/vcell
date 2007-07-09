package cbit.vcell.dictionary;

/**
 * Insert the type's description here.
 * Creation date: (2/18/2003 5:40:29 PM)
 * @author: Frank Morgan
 */
public abstract class DBFormalSpecies implements SpeciesDescription {

	private org.vcell.util.document.KeyValue dbFormalSpeciesKey = null;
	private FormalSpeciesInfo formalSpeciesInfo = null;
/**
 * DBFormalSpeciesInfo constructor comment.
 */
public DBFormalSpecies(org.vcell.util.document.KeyValue argKey, FormalSpeciesInfo argFormalSpeciesInfo) {

	if(argKey == null || argFormalSpeciesInfo == null){
		throw new IllegalArgumentException(this.getClass().getName());
	}
	
	this.dbFormalSpeciesKey = argKey;
	this.formalSpeciesInfo = argFormalSpeciesInfo;
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	
	if (obj instanceof DBFormalSpecies){
		DBFormalSpecies dbFormalSpecies = (DBFormalSpecies)obj;
		if (!dbFormalSpeciesKey.compareEqual(dbFormalSpecies.getDBFormalSpeciesKey())){
			return false;
		}
		if(!formalSpeciesInfo.compareEqual(dbFormalSpecies.getFormalSpeciesInfo())){
			return false;
		}
	}else{
		return false;
	}
	
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 5:46:02 PM)
 * @return cbit.sql.KeyValue
 */
public org.vcell.util.document.KeyValue getDBFormalSpeciesKey() {
	return dbFormalSpeciesKey;
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 5:46:02 PM)
 * @return cbit.vcell.dictionary.FormalSpeciesInfo
 */
public FormalSpeciesInfo getFormalSpeciesInfo() {
	return formalSpeciesInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2003 5:55:29 PM)
 * @return int
 */
public FormalSpeciesType getFormalSpeciesType() {
	return formalSpeciesInfo.getFormalSpeciesType();
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2003 4:44:12 PM)
 * @return java.lang.String
 */
public String getPreferredName() {
	return getFormalSpeciesInfo().getPreferredName();
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2003 3:46:13 PM)
 * @return java.lang.String
 */
public String toString() {
	return "FK="+getDBFormalSpeciesKey()+"-"+formalSpeciesInfo.toString();
}
}
