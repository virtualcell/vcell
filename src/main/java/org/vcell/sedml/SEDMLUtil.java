package org.vcell.sedml;

import org.jlibsedml.AbstractIdentifiableElement;

public class SEDMLUtil {
	
	public static String getName(AbstractIdentifiableElement thing) {
		if(thing == null) {
			return null;
		}
		if(thing.getName() != null) {
			return thing.getName();
		}
		if(thing.getId() != null) {
			return thing.getId();
		}
		return null;
	}
	



}
