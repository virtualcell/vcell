package cbit.vcell.dictionary;

import cbit.util.document.KeyValue;
/**
 * Insert the type's description here.
 * Creation date: (9/19/2003 12:34:40 PM)
 * @author: Frank Morgan
 */
public class DBNonFormalUnboundSpecies  implements SpeciesDescription {

	private String descr_PreferredName = null;
/**
 * Insert the method's description here.
 * Creation date: (9/19/2003 12:54:53 PM)
 * @param argCommonname java.lang.String
 * @param argDBSpeciesKey cbit.sql.KeyValue
 */
public DBNonFormalUnboundSpecies(String argPreferredName) {

	this.descr_PreferredName = argPreferredName;
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(cbit.util.Matchable obj) {
	
	if(this == obj){
		return true;
	}

	SpeciesDescription dbsd = (SpeciesDescription)obj;
	if(this.getPreferredName().equals(dbsd.getPreferredName())){
		return true;
	}
	
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2003 12:44:34 PM)
 * @return java.lang.String
 */
public String getPreferredName() {
	return descr_PreferredName;
}
}
