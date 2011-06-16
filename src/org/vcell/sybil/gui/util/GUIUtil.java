/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.gui.util;

/*   GUIUtils  --- by Oliver Ruebenacker, UCHC --- November 2007 to September 2008
 *   Useful GUI-related static public methods
 */

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.swing.JCheckBox;

public class GUIUtil {

	public static <V> Set<V> selectedValues(Map<JCheckBox, V> map) {
		Set<V> values = new HashSet<V>();
		for(Entry<JCheckBox, V> entry : map.entrySet()) {
			JCheckBox checkBox = entry.getKey();
			V value = entry.getValue();
			if(checkBox.isSelected() && value != null) { values.add(value); }
		}
		return values;
	}
	
	public static <V> Set<V> unselectedValues(Map<JCheckBox, V> map) {
		Set<V> values = new HashSet<V>();
		for(Entry<JCheckBox, V> entry : map.entrySet()) {
			JCheckBox checkBox = entry.getKey();
			V value = entry.getValue();
			if(!checkBox.isSelected() && value != null) { values.add(value); }
		}
		return values;
	}
	
}
