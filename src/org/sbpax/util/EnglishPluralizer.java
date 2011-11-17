package org.sbpax.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.sbpax.util.sets.SetUtil;

public class EnglishPluralizer {

	public static final Set<String> PLURAL_SAME_AS_SINGULAR = SetUtil.newSet("deer", "moose",
			"sheep", "bison", "salmon", "pike", "trout", "fish", "swine", "aircraft",
			"watercraft", "spacecraft", "hovercraft", "information", "head");
	
	public static final Map<String, String> IRREGULAR_PLURALS = new HashMap<String, String>();
	
	static {
		IRREGULAR_PLURALS.put("ox", "oxen");
		IRREGULAR_PLURALS.put("child", "children");
		IRREGULAR_PLURALS.put("calf", "calves");
		IRREGULAR_PLURALS.put("knife", "knives");
		IRREGULAR_PLURALS.put("leaf", "leaves");
		IRREGULAR_PLURALS.put("life", "lives");
		IRREGULAR_PLURALS.put("wife", "wives");
		IRREGULAR_PLURALS.put("foot", "feet");
		IRREGULAR_PLURALS.put("tooth", "teeth");
		IRREGULAR_PLURALS.put("goose", "geese");
		IRREGULAR_PLURALS.put("louse", "lice");
		IRREGULAR_PLURALS.put("mouse", "mice");
		IRREGULAR_PLURALS.put("man", "men");
		IRREGULAR_PLURALS.put("woman", "women");
		IRREGULAR_PLURALS.put("index", "indices");
		IRREGULAR_PLURALS.put("vortex", "vortices");
		IRREGULAR_PLURALS.put("catalysis", "catalyses");
	}
	
	public static String pluralize(String singular) {
		String plural;
		if(PLURAL_SAME_AS_SINGULAR.contains(singular)) {
			plural = singular;
		} else if(IRREGULAR_PLURALS.containsKey(singular)) {
			plural = IRREGULAR_PLURALS.get(singular);
		} else if(singular.endsWith("s") || singular.endsWith("o") 
				|| singular.endsWith("ch") || singular.endsWith("sh")
				|| singular.endsWith("x")) {
			plural = singular + "es";
		} else if(singular.endsWith("y") && 
				!(singular.endsWith("oy") || singular.endsWith("ey") 
						|| singular.endsWith("ay")|| singular.endsWith("uy"))) {
			plural = singular.substring(0, singular.length() - 1) + "ies";
		} else {
			plural = singular + "s";
		}
		return plural;
	}
	
}
