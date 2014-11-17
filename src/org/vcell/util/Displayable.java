package org.vcell.util;

/*
 * Name and type of the entity, which can ONLY be used for displaying purposes
 */
public interface Displayable {
	
	// TODO: make sure to implement these as "final"
	public String getDisplayName();		// for most it will be "getName()" or "toString()" implementations
	public String getDisplayType();		// ex: Model, Species, Component, Parameter...

}
