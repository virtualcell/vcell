package cbit.vcell.microscopy;

import java.util.prefs.Preferences;

public class VFRAPPreference 
{
	public static String vfrapNodeName = "/cbit/vcell/microscopy";
	private static Preferences userPref;
	
	//Keys
	public static final String ROI_ASSIST_REQUIREMENT_TYPE = "ROI_ASSIST_REQUIREMENT_TYPE"; 
	
	//Constants
	/*Assist types*/
	public static final String ROI_ASSIST_REQUIRE_ALWAYS = "ROI_ASSIST_REQUIRE_ALWAYS";
	public static final String ROI_ASSIST_REQUIRE_NO = "ROI_ASSIST_REQUIRE_NO";
	public static final String ROI_ASSIST_PREF_NOT_SET = "ROI_ASSIST_PREF_NOT_SET";
	
	public VFRAPPreference() 
	{
        userPref = Preferences.userNodeForPackage(this.getClass());/*userRoot().node(ourNodeName);*/
    }

	public static Preferences getUserPref()
	{
		if(userPref == null)
		{
			userPref = Preferences.userRoot().node(vfrapNodeName);
		}
		return userPref;
	}
	
	public static void putValue(String key, String value)
	{
		VFRAPPreference.getUserPref().put(key, value);
	}
	
	public static String getValue(String key, String defaultValue)
	{
		return VFRAPPreference.getUserPref().get(key, defaultValue);
	}
}
