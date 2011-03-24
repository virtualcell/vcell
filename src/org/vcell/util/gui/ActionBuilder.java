package org.vcell.util.gui;

import javax.swing.Action;
import javax.swing.Icon;

public class ActionBuilder {
	
	public static interface ID {}
	
	public static interface Generator { public Action generateAction(ID id); }
	
	public final ID id;
	public final String name, shortDescription, longDescription, key;
	public final Icon icon;
	
	public ActionBuilder(ID id, String name, String key, String shortDescription, String longDescription, 
			Icon icon) {
		this.id = id;
		this.name = name;
		this.key = key;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		this.icon = icon;
	}

	public ActionBuilder(ID id, String name, String key, String description, Icon icon) {
		this(id, name, key, description, description, icon);
	}
	
	public ActionBuilder(ID id, String name, String key, Icon icon) { this(id, name, key, name, icon); }
	public ActionBuilder(ID id, String name, Icon icon) { this(id, name, name, icon); }
	
	public ActionBuilder(ID id, String name, String key, String shortDescription, 
			String longDescription) {
		this(id, name, key, shortDescription, longDescription, null);
	}

	public ActionBuilder(ID id, String name, String key, String description) {
		this(id, name, key, description, description);
	}

	public ActionBuilder(ID id, String name, String key) { this(id, name, key, name); }
	public ActionBuilder(ID id, String name) { this(id, name, name); }

	public Action buildAction(Generator generator) {
		Action action = generator.generateAction(id);
		if(action != null) {
			if(name != null) { action.putValue(Action.NAME, name); }
			if(key != null) { action.putValue(Action.ACTION_COMMAND_KEY, key); }
			if(shortDescription != null) { action.putValue(Action.SHORT_DESCRIPTION, shortDescription); }
			if(longDescription != null) { action.putValue(Action.LONG_DESCRIPTION, longDescription); }
			if(icon != null) { action.putValue(Action.SMALL_ICON, icon); }			
		}
		return action;
	}
	
}
