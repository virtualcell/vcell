/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.server;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.vcell.util.BeanUtils;
import org.vcell.util.Preference;

import cbit.vcell.resource.ResourceUtil;

/**
User preferences, for now, fall under two different categories: warning messages and general preferences. 
Consider doing the following: (09/02/04):
- Moving the warningMessages to a GUI layer class.
- Merging the two categories for user preferences (all strings, no booleans, with convenience methods, like getBooleanPref(), setBooleanPref()
- Removing the current hashes and add a hash that has as a key the preference key (the static ints), and as a value a Preference
  object. This hash is good for easier lookups, otherwise, an array of preferences should do.
- Add to the Preference class: a default value for a preference, additional info object - if needed (?).

 * Creation date: (5/21/2004 2:59:47 AM)
 * @author: Ion Moraru
 */
public class UserPreferences {
	private ClientServerManager clientServerManager = null;

	//the two broad types of preferences
	private static final String WARN = "WARN";                     //warning message
	private static final String GEN_PREF = "GEN_PREF";             //generic preferences
	/* Warning pop-ups */

	// corresponding Preference format is:
	// key = "WARN" + warningType
	// value = String.valueOf(choice)
	
	// warning types
	//public final static int WARN_ALREADY_OPEN = 0;  			// -> error/question
	public final static int WARN_SAVE_BEFORE_RUNNING = 1;		
	public final static int WARN_UNABLE_CHECK_CHANGES = 2;  
	public final static int WARN_FOR_CLOSE_WITHOUT_SAVE = 3;  
	public final static int WARN_SAVE_UNCHANGED_DOCUMENT = 4;
	public final static int WARN_SAVE_NOT_OWNER = 5;
	public final static int WARN_DELETE_APPLICATION = 6;
	//public final static int WARN_LOST_RESULTS = 7;				// -> question
	public final static int WARN_REVERT_TO_SAVED = 8;
	public final static int WARN_OVERWRITE_FILE = 9;
	public final static int WARN_TIME_PLOT_ONLY_FOR_POINTS = 10;
	public final static int WARN_NO_SCALE_SETTINGS_FOR_EXPORT = 11;
	public final static int WARN_CHANGE_USER = 12;
	public final static int WARN_DELETE_DOCUMENT = 13; 
	public final static int WARN_EXPORT_MEMBRANE_DATA_3D = 14; 
	public final static int WARN_GEOMEDIT_HISTOGRAM_APPLY = 15; 

	private final static int NUM_WARNING_PREFERENCES = 16;
	
	// default
	private static boolean[] showWarningDefaults = new boolean[] {
		true,
		true,
		true,
		true,
		true,
		true,
		true,
		true,
		true,
		true,
		true,
		true,
		true,
		true,
		true,
		true
	};

	//a generic user preference nevertheless: Last directory used for import XML/image, export, saveImageAs
	public final static int GENERAL_LAST_PATH_USED = 0;
	public final static int SEND_MODEL_INFO_IN_ERROR_REPORT = 1;

	
	
	static {
		assert(showWarningDefaults.length == NUM_WARNING_PREFERENCES);

	}
	
	// user choices
	private Hashtable<String, Preference> warningHash = new Hashtable<String, Preference>();// keyed by "WARN"+number
	private Hashtable<String, Preference> genericHash = new Hashtable<String, Preference>();// keyed by "GEN_PREF"+number
	/**
	 * store last created object
	 */
	private static UserPreferences lastPref = null;
	
	/**
	 * make semi-Singleton class
	 * @return last created preferences or null;
	 */
	public static UserPreferences getLastUserPreferences( ) {
		return lastPref;
	}

/**
 * Insert the method's description here.
 * Creation date: (6/12/2004 9:08:03 PM)
 * @param clientServerManager cbit.vcell.client.server.ClientServerManager
 */
public UserPreferences(ClientServerManager clientServerManager) {
	this.clientServerManager = clientServerManager;
	lastPref = null;

}

public File getCurrentDialogPath() {
	try {
		String key = UserPreferences.GEN_PREF+UserPreferences.GENERAL_LAST_PATH_USED;
		if (genericHash.containsKey(key)){
			Preference preference = (Preference)genericHash.get(key);
			//extra processing: check if the path still exists.
			java.io.File dir = new java.io.File(preference.getValue());
			if (dir.exists()) {
				return new File(preference.getValue());
			}

		}
		String homeDirPath = ResourceUtil.getUserHomeDir().getAbsolutePath();
		//setGenPref(GENERAL_LAST_PATH_USED, homeDirPath);
		return new File(homeDirPath);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return new File(ResourceUtil.getUserHomeDir().getAbsolutePath());
	}
}

public boolean getSendModelInfoInErrorReportPreference(){
	String prefString = null;
	try {
		if (genericHash.containsKey(UserPreferences.GEN_PREF+UserPreferences.SEND_MODEL_INFO_IN_ERROR_REPORT)){
			Preference preference = (Preference)genericHash.get(UserPreferences.GEN_PREF+UserPreferences.SEND_MODEL_INFO_IN_ERROR_REPORT);
			prefString =  preference.getValue();
		} else {
			prefString = "true";
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		prefString = "true";
	}
	return Boolean.valueOf(prefString);
}


/**
 * return general preference as boolean
 * @param prefType key of general preference
 * @return true if present and true, false if problem
 */
//public boolean getGenPrefBoolean(int prefType) {
//	return Boolean.valueOf(getGenPref(prefType)); 
//}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:06:29 AM)
 * @return boolean
 * @param preference java.lang.String
 */
public boolean getShowWarning(int warningType) {
	try {
		if (warningHash.containsKey(UserPreferences.WARN+warningType)) {
			Preference preference = (Preference)warningHash.get(UserPreferences.WARN+warningType);
			return Boolean.valueOf(preference.getValue()).booleanValue();
		} else {
			return showWarningDefaults[warningType];
		}
	} catch (ArrayIndexOutOfBoundsException e) {
		ArrayIndexOutOfBoundsException exc = new ArrayIndexOutOfBoundsException("Unknown warning type: " + warningType);
		exc.printStackTrace(System.out);
		throw exc;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/12/2004 9:35:38 PM)
 * @return cbit.util.Preference[]
 */
private Preference[] getUserChoices() {
	Vector<Preference> userChoices = new Vector<Preference>(); 
	// warning choices
	Enumeration<Preference> en = warningHash.elements();
	while (en.hasMoreElements()) {
		Preference preference = en.nextElement();
		userChoices.add(preference);
	}
	en = genericHash.elements();
	while (en.hasMoreElements()) {
		Preference preference = en.nextElement();
		userChoices.add(preference);
	}
	// other choices
	// ...
	// done, return array (may be empty)
	return (Preference[])BeanUtils.getArray(userChoices, Preference.class);
}


	//for debugging purposes
	public String list() {

		StringBuffer buf = new StringBuffer();
		buf.append("User preferences for user: " + clientServerManager.getUser().getName());
		buf.append("\n");
		Preference prefs [] = getUserChoices();
		for (int i = 0; i < prefs.length; i++) {
			buf.append(prefs[i].toString() + "\n");
		}
		
		return buf.toString();
	}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:06:29 AM)
 * @return boolean
 * @param preference java.lang.String
 */
protected void resetFromSaved(Preference[] savedPreferences) {                  
	/* look in getUserChoices() for keys and values strings encoding of the preferences */
	// reset to defaults
	warningHash.clear();
	// apply saved choices
	for (int i = 0; i < savedPreferences.length; i++){
		String key = savedPreferences[i].getKey();
		if (key.startsWith(UserPreferences.WARN)) {
			// parse warning preferences
			int warningType = Integer.parseInt(key.substring(UserPreferences.WARN.length()));
			boolean choice = Boolean.valueOf(savedPreferences[i].getValue()).booleanValue();
			warningHash.put(UserPreferences.WARN+warningType, new Preference(UserPreferences.WARN +warningType, String.valueOf(choice)));
		} else if (key.startsWith(UserPreferences.GEN_PREF)) {              
			int prefType = Integer.parseInt(key.substring(UserPreferences.GEN_PREF.length()));
			String prefValue = savedPreferences[i].getValue();
			genericHash.put(UserPreferences.GEN_PREF+prefType, new Preference(UserPreferences.GEN_PREF + prefType, prefValue));
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/12/2004 10:47:03 PM)
 */
private void saveToDatabase() {
	// thread it; ignore failures (just record them)
	Runnable runner = new Runnable() {
		public void run() {
			try {
				clientServerManager.getDocumentManager().replacePreferences(getUserChoices());
			} catch (Throwable exc) {
				exc.printStackTrace(System.out);
			}
		}
	};
	Thread saveThread = new Thread(runner);
	saveThread.start();
}


	private void setGenPref(int prefType, Preference newPreference) {

		try {
			Preference oldPreference = (Preference)genericHash.get(UserPreferences.GEN_PREF+prefType);
			if (oldPreference==null || !oldPreference.compareEqual(newPreference)) {
				genericHash.put(UserPreferences.GEN_PREF+prefType, newPreference);
				saveToDatabase();
				System.out.println("Resetting user preference: " + newPreference.getKey() + "=" + newPreference.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			//throw e;
		}
	}


	public void setCurrentDialogPath(File path){
		if (path.exists() && path.isDirectory()) {
			String newPreferredPathString = path.getAbsolutePath();
			Preference newPreference = new Preference(UserPreferences.GEN_PREF +UserPreferences.GENERAL_LAST_PATH_USED, newPreferredPathString);
			setGenPref(UserPreferences.GENERAL_LAST_PATH_USED, newPreference);
		}
	}	
	
	public void setSendModelInfoInErrorReportPreference(boolean flag){
		Preference newPreference = new Preference(UserPreferences.GEN_PREF+UserPreferences.SEND_MODEL_INFO_IN_ERROR_REPORT, String.valueOf(flag));
		setGenPref(UserPreferences.SEND_MODEL_INFO_IN_ERROR_REPORT, newPreference);
	}

/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:06:29 AM)
 * @return boolean
 * @param preference java.lang.String
 */
//public void setGenPref(int prefType, String preferenceString) {
//	Preference newPreference = new Preference(UserPreferences.GEN_PREF +prefType, preferenceString);
//	setGenPref(prefType, newPreference);
//}
/**
 * set boolean preference
 * @param prefType
 * @param boolPref new value
 */
//public void setGenPref(int prefType, boolean boolPref) { 
//	setGenPref(prefType, Boolean.toString(boolPref) );
//}

private void setShowWarning(int warningType, Preference newPreference) {
	
	try {
		Preference oldPreference = (Preference)warningHash.get(UserPreferences.WARN+warningType);
		if (oldPreference==null || !newPreference.compareEqual(oldPreference)) {
			warningHash.put(UserPreferences.WARN+warningType, newPreference);
			saveToDatabase();
		}
	} catch (ArrayIndexOutOfBoundsException e) {
		ArrayIndexOutOfBoundsException exc = new ArrayIndexOutOfBoundsException("Unknown warning type: " + warningType);
		exc.printStackTrace(System.out);
		throw exc;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2004 3:06:29 AM)
 * @return boolean
 * @param preference java.lang.String
 */
public void setShowWarning(int warningType, boolean choice) {

	Preference newPreference = new Preference(UserPreferences.WARN +warningType, String.valueOf(choice));
	setShowWarning(warningType, newPreference);
}
}
