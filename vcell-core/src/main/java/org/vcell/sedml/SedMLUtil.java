package org.vcell.sedml;

import org.jlibsedml.components.SedBase;

public class SedMLUtil {
	
	public static String getName(SedBase thing) {
		if(thing == null) {
			return null;
		}
		if(thing.getName() != null) {
			return thing.getName();
		}
		if(thing.getId() != null) {
			return thing.getId().string();
		}
		return null;
	}
	



}
