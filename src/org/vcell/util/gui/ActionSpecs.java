package org.vcell.util.gui;

import javax.swing.Action;
import javax.swing.Icon;

public class ActionSpecs {
	
	public final String name, shortDescription, longDescription, key;
	public final Icon icon;
	
	public ActionSpecs(String name, String key, String shortDescription, String longDescription, 
			Icon icon) {
		this.name = name;
		this.key = key;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		this.icon = icon;
	}

	public ActionSpecs(String name, String key, String description, Icon icon) {
		this(name, key, description, description, icon);
	}
	
	public ActionSpecs(String name, String key, Icon icon) { this(name, key, name, icon); }
	public ActionSpecs(String name, Icon icon) { this(name, name, icon); }
	
	public ActionSpecs(String name, String key, String shortDescription, String longDescription) {
		this(name, key, shortDescription, longDescription, null);
	}

	public ActionSpecs(String name, String key, String description) {
		this(name, key, description, description);
	}

	public ActionSpecs(String name, String key) { this(name, key, name); }
	public ActionSpecs(String name) { this(name, name); }

	public void set(Action action) {
		if(name != null) { action.putValue(Action.NAME, name); }
		if(key != null) { action.putValue(Action.ACTION_COMMAND_KEY, key); }
		if(shortDescription != null) { action.putValue(Action.SHORT_DESCRIPTION, shortDescription); }
		if(longDescription != null) { action.putValue(Action.LONG_DESCRIPTION, longDescription); }
		if(icon != null) { action.putValue(Action.SMALL_ICON, icon); }
	}
	
}
