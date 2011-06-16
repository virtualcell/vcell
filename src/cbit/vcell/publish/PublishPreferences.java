/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.publish;
import cbit.vcell.xml.XMLTags;

/**
This class is the user's publishing preferences for a 'model':
- Preferences for publishing a BioModel:
  - Option of whether to publish the 'physiology' section (no control on including individual reactions, kinetics, ...etc)
  - Option of whether to publish the 'Application' section.
  - Option of whether to publish the 'Math Description' section (set once for all applications).
  - Option of whether to include the 'Geometry' section (set once for all applications).
  - Option of whether to include the 'Simulation settings' section (set once for all applications). 

- Preferences for publishing a MathModel:
  - Option of whether to publish the 'Math Description' section.
  - Option of whether to include the 'Geometry' section (set for all applications).
  - Option of whether to include the 'Simulation settings' section (set for all applications).

- Preferences for publishing a Geometry:
  - No options (for now).

  Future refactoring may provide subclasses that match each of the above 'publishable' types. 
   
 * Creation date: (7/14/2004 11:18:47 AM)
 * @author: Rashad Badrawi
 */
 
public class PublishPreferences {

	//default settings: includes all. 
	public static final PublishPreferences DEFAULT_BIO_PREF = PublishPreferences.getInstance(PublishPreferences.BIO_PREF);
	public static final PublishPreferences DEFAULT_MATH_PREF = PublishPreferences.getInstance(PublishPreferences.MATH_PREF);
	public static final PublishPreferences DEFAULT_GEOM_PREF = PublishPreferences.getInstance(PublishPreferences.GEOM_PREF);

	//Specifies the object type we are publishing. Using XML tags for convenience.
	public final static String BIO_PREF = XMLTags.BioModelTag;
	public final static String MATH_PREF = XMLTags.MathModelTag;
	public final static String GEOM_PREF = XMLTags.GeometryTag;

	String publishedType;
	//consider using bitmasks...
	boolean physioFlag;
	boolean appFlag;
	boolean mathDescFlag;
	boolean geomFlag;
	boolean simFlag;

	public PublishPreferences(String pType, boolean includePhysio, boolean includeApp, boolean includeMathDesc, 
		                      boolean includeGeom, boolean includeSim) {

		setPublishedType(pType);
		if( (pType.equals(PublishPreferences.BIO_PREF) && !includeApp && (includeMathDesc || includeGeom || includeSim)) ||
			 (pType.equals(PublishPreferences.MATH_PREF) && (includePhysio || includeApp)) ||
			 (pType.equals(PublishPreferences.GEOM_PREF) && (includePhysio || includeMathDesc || includeSim)) ) {	
			throw new IllegalArgumentException("Settings for preferences do not match the published type: " + pType);
		}          
		this.physioFlag = includePhysio;
		this.appFlag = includeApp;
		this.mathDescFlag = includeMathDesc;
		this.geomFlag = includeGeom;
		this.simFlag = includeSim;
	}


//convenience method, allows settings all the preferences to the defaults.
	private static PublishPreferences getInstance(String pType) {

		PublishPreferences pp;
		if (PublishPreferences.BIO_PREF.equals(pType)) {
			pp = new PublishPreferences(pType, true, true, true, true, true);
		} else if (PublishPreferences.MATH_PREF.equals(pType)) {
			pp = new PublishPreferences(pType, false, false, true, true, true);
		} else if (PublishPreferences.GEOM_PREF.equals(pType)) {
			pp = new PublishPreferences(pType, false, false, false, true, false);
		} else {
			throw new IllegalArgumentException("Publish preferences for an invalid type: " + pType);
		}

		return pp;
	}


	public String getPublishedType() { return publishedType; }


	public boolean includeApp() { return appFlag; }


	public boolean includeGeom() { return geomFlag; }


	public boolean includeMathDesc() { return mathDescFlag; }


	public boolean includePhysio() { return physioFlag; }


	public boolean includeSim() { return simFlag; }


	private void setPublishedType(String pType) {
		
		if (!PublishPreferences.BIO_PREF.equals(pType) && !PublishPreferences.MATH_PREF.equals(pType) &&
			!PublishPreferences.GEOM_PREF.equals(pType)) {
			throw new IllegalArgumentException("Publish preferences for an invalid type: " + pType);
		}
		this.publishedType = pType;
	}
}
