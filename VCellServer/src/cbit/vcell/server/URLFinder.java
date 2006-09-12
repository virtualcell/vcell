package cbit.vcell.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
