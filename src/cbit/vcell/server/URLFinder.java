/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;

/**
 * Insert the type's description here.
 * Creation date: (3/2/01 8:27:57 PM)
 * @author: Jim Schaff
 */
public class URLFinder implements java.io.Serializable {
	private java.net.URL tutorialURL = null;
	private java.net.URL userGuideURL = null;
/**
 * URLFinder constructor comment.
 */
public URLFinder(java.net.URL argTutorialURL, java.net.URL argUserGuideURL) {
	this.userGuideURL = argUserGuideURL;
	this.tutorialURL = argTutorialURL;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/01 11:06:43 PM)
 * @return java.net.URL
 */
public java.net.URL getTutorialURL() {
	return tutorialURL;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/01 11:06:43 PM)
 * @return java.net.URL
 */
public java.net.URL getUserGuideURL() {
	return userGuideURL;
}
}
