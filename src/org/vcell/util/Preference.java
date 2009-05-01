package org.vcell.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Vector;


/**
 * Insert the type's description here.
 * Creation date: (6/10/2004 7:17:23 PM)
 * @author: Jim Schaff
 */
public class Preference implements java.io.Serializable, Matchable {
	private String key = null;
	private String value = null;
	private boolean isSystemClientProperty = false;

	//
	// max string lengths
	//
	// remember to keep these consistent with UserPreferenceTable column definitions
	//
	public static final int MAX_KEY_LENGTH = 128;
	public static final int MAX_VALUE_LENGTH = 4000;
	
	//-------------------------------------------------------------------------------------------------
	//
	//Global System Client Property Names
	//Declaration name MUST begin with "SYSCLIENT_" or it will not be available to clients
	//from UserPreferences.
	//Declaration value must match a property NAME defined in a an external properties file or the database.
	//
	public static final String SYSCLIENT_pslidAllProteinListURL = "SYSCLIENT_vcell.pslid.allProteinListURL";
	public static final String SYSCLIENT_pslidCellProteinListExpURL = "SYSCLIENT_vcell.pslid.cellProteinListExpURL";
	public static final String SYSCLIENT_pslidCellProteinListGenURL = "SYSCLIENT_vcell.pslid.cellProteinListGenURL";
	public static final String SYSCLIENT_pslidCellProteinImageInfoExpURL = "SYSCLIENT_vcell.pslid.cellProteinImageInfoExpURL";
	public static final String SYSCLIENT_pslidCellProteinImageInfoGenURL = "SYSCLIENT_vcell.pslid.cellProteinImageInfoGenURL";
	public static final String SYSCLIENT_pslidCellProteinImageExpURL = "SYSCLIENT_vcell.pslid.cellProteinImageExpURL";
	public static final String SYSCLIENT_pslidCellProteinImageGenURL_1 = "SYSCLIENT_vcell.pslid.cellProteinImageGenURL_1";
	public static final String SYSCLIENT_pslidCellProteinImageGenURL_2 = "SYSCLIENT_vcell.pslid.cellProteinImageGenURL_2";
	
	private static final String[] SYSTEM_CLIENT_PROPERTIES = getAllDefinedSystemClientPropertyNames();
	//-------------------------------------------------------------------------------------------------


/**
 * Preference constructor comment.
 */
public Preference(String argKey, String argValue) {
	if (argKey==null || argValue==null){
		throw new IllegalArgumentException("key or value is null");
	}
	if (TokenMangler.getSQLEscapedString(argKey).length() > MAX_KEY_LENGTH){
		throw new IllegalArgumentException("key ("+TokenMangler.getSQLEscapedString(argKey)+") is longer than "+MAX_KEY_LENGTH);
	}
	if (TokenMangler.getSQLEscapedString(argValue).length() > MAX_VALUE_LENGTH){
		throw new IllegalArgumentException("value ("+TokenMangler.getSQLEscapedString(argValue)+") is longer than "+MAX_VALUE_LENGTH);
	}
	this.key = argKey;
	this.value = argValue;
}

public Preference(String argKey, String argValue,boolean isSystemClientProperty) {
	this(argKey,argValue);
	this.isSystemClientProperty = isSystemClientProperty;
	if(isSystemClientProperty && !Preference.isDefinedSystemClientProperty(argKey)){
		throw new IllegalArgumentException("SYSTEM_CLIENT_PROPERTY "+argKey+" is not defined in "+Preference.class.getName());
	}
}

public static final String[] getAllDefinedSystemClientPropertyNames(){
	final String SYSTEM_CLIENT_PROPERTY_PREFIX = "SYSCLIENT_";
	Vector<String> allDefinedSystemClientPropertiesV = new Vector<String>();
	Field[] systemClientFields = Preference.class.getFields();
	for (int i = 0; i < systemClientFields.length; i++) {
		if(	systemClientFields[i].getType().isAssignableFrom(String.class) &&
			systemClientFields[i].getName().startsWith(SYSTEM_CLIENT_PROPERTY_PREFIX)){
			int modifiers = systemClientFields[i].getModifiers();
			if(Modifier.isPublic(modifiers) && Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)){
				try {
					allDefinedSystemClientPropertiesV.add((String)systemClientFields[i].get(null));
				}catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	if(allDefinedSystemClientPropertiesV.size() > 0){
		String[] allDefinedSystemClientProperties = new String[allDefinedSystemClientPropertiesV.size()];
		allDefinedSystemClientPropertiesV.copyInto(allDefinedSystemClientProperties);
		return allDefinedSystemClientProperties;
	}
	return null;
}

private static boolean isDefinedSystemClientProperty(String propName){
	for (int i = 0; i < Preference.SYSTEM_CLIENT_PROPERTIES.length; i++) {
		if(Preference.SYSTEM_CLIENT_PROPERTIES[i].equals(propName)){
			return true;
		}
	}
	return false;
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj instanceof Preference){
		Preference other = (Preference)obj;
		if (!other.value.equals(value)){
			return false;
		}
		if (!other.key.equals(key)){
			return false;
		}
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:20:07 PM)
 * @return java.lang.String
 */
public java.lang.String getKey() {
	return key;
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:20:07 PM)
 * @return java.lang.String
 */
public java.lang.String getValue() {
	return value;
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 7:23:32 PM)
 * @return java.lang.String
 */
public String toString() {
	return "Preference['"+key+"','"+value+"']";
}

public boolean isSystemClientProperty() {
	return isSystemClientProperty;
}
}