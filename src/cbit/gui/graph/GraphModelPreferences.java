/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph;
/**
 * Insert the type's description here.
 * Creation date: (5/30/2001 4:19:49 PM)
 * @author: Ion Moraru
 */

/*
 * for now, we start with just species context name choices, but will grow when we revisit the shapes and related tools
 */

public class GraphModelPreferences {
	// we enforce consistency - this is a singleton
	private static GraphModelPreferences instance = null;
	// possible values
	public static final int DISPLAY_COMMON_NAME = 1000;
	public static final int DISPLAY_CONTEXT_NAME = 1001;
	// actual values; initialized to system defaults
	private int speciesContextDisplayName = DISPLAY_CONTEXT_NAME;

	/**
	 * GraphModelPreferences constructor comment.
	 */
	private GraphModelPreferences() {
		super();
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (5/30/2001 4:25:43 PM)
	 * @return cbit.vcell.graph.GraphModelPreferences
	 */
	public static GraphModelPreferences getInstance() {
		if (instance == null) {
			instance = new GraphModelPreferences();
		}
		return instance;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (5/30/2001 4:44:19 PM)
	 * @return int
	 */
	public int getSpeciesContextDisplayName() {
		return speciesContextDisplayName;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (5/30/2001 4:44:19 PM)
	 * @param newSpeciesContextDisplayName int
	 */
	public void setSpeciesContextDisplayName(int newSpeciesContextDisplayName) {
		if (newSpeciesContextDisplayName == DISPLAY_COMMON_NAME || newSpeciesContextDisplayName == DISPLAY_CONTEXT_NAME) {
			speciesContextDisplayName = newSpeciesContextDisplayName;
		} else {
			System.out.println("ERROR - invalid option for species context display name: " + newSpeciesContextDisplayName);
		}
	}
}
