/*
 * @(#)FlatMap.java	1.36 06/10/30
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package javax.help;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.*;
import java.io.*;
import java.beans.*;
import javax.help.event.*;
import javax.help.Map.ID;
import com.sun.java.help.impl.*;

/**
 * A FlatMap is a simple implementation of a Map.  It is used to represent a
 * Map for a single file.
 *
 * @author Eduardo Pelegri-Llopart
 * @version	1.18	01/22/99
 */
public class FlatMap implements Map, Serializable {
    private URL base;		// URL to this map
    private ResourceBundle resource; // the resource
    private HelpSet helpset;	// the top HelpSet

    /**
     * PublicID (known to this XML processor) to the DTD for version 1.0 of the Map
     */
    public static final String publicIDString =
        "-//Sun Microsystems Inc.//DTD JavaHelp Map Version 1.0//EN";

    /**
     * PublicID (known to this XML processor) to the DTD for version 1.0 of the Map
     */
    public static final String publicIDString_V2 =
        "-//Sun Microsystems Inc.//DTD JavaHelp Map Version 2.0//EN";

  /**
     * Create a FlatMap from a given URL.
     *
    * @param source The URL that is the source for all references in this Map.
    * @param hs The HelpSet providing "context" for this Map.
    * @throws IllegalArgumentException if hs doesn't have nested HelpSets.
     */
    public FlatMap(URL base, HelpSet hs) throws IOException {
	debug("Creating FlatMap for: "+base);

	// Verify that this helpset indeed does not have nested HelpSets.
	for (Enumeration e = hs.getHelpSets();
	     e.hasMoreElements(); ) {
	    throw new IllegalArgumentException
		("Cannot create - HelpSet is not flat");
	}

	//	InputStream is = base.openStream();
	//	resource = new MapResourceBundle(is);
	resource = new FlatMapResourceBundle(base);
        this.base = base;
	this.helpset = hs;
    }

    /**
     * The HelpSet for this Map.
     */
    public HelpSet getHelpSet() {
	return helpset;
    }

    /**
     * Determines whether the given ID is valid. If hs is null
     * it is ignored.
     * 
     * @param id The String ID.
     * @param hs The HelpSet against which to resolve the string.
     * @return True if id is valid, false if not valid.
     */

    public boolean isValidID(String id, HelpSet hs) {
	debug("isValidID "+id);

	try {
	    String tmp = resource.getString(id);
	} catch (MissingResourceException e) {
	    return false;
	}
	return true;
    }

    /**
     * Gets an enumeration of all the IDs in a Map.
     *
     * @return An enumeration of all the IDs in a Map.
     */
    public Enumeration getAllIDs() {
	return new FlatEnumeration(resource.getKeys(), helpset);
    }

    /**
     * Gets the URL that corresponds to a given ID in the map.
     *
     * @param iden The iden to get the URL for. If iden is null it is
     * treated as an unresolved ID and will return null.
     * @return URL The matching URL.  Null if this map cannot solve the ID
     * @exception MalformedURLException if the URLspecification found  is malformed
     */
    public URL getURLFromID(ID iden) throws MalformedURLException {
	debug("getURLFromID("+iden+")");

	String id = iden.id;
	HelpSet hs = iden.hs;
	if (id == null) {
	    return null;
	}
	String tmp = null;
	try {
	    tmp = resource.getString(id);
	    URL back = new URL(base, tmp);
	    return back;
	} catch (MissingResourceException e) {
	    return null;
	}
    }

    /**
     * Determines if the URL corresponds to an ID in the Map.
     *
     * @param url The URL to check on.
     * @return true If this is an ID, otherwise false.
     */
    public boolean isID(URL url) {
	URL tmp;
	for (Enumeration e = resource.getKeys() ; e.hasMoreElements() ;) {
	    try {
		String key = (String) e.nextElement();
		tmp = new URL(base, (String) resource.getObject(key));
		// sameFile() ignores the anchor! - epll
		if (url.sameFile(tmp) == true) {
		    return true;
		}
	    } catch (Exception ex) {
	    }
	}
	return false;
    }


    /**
     * Gets the ID for this URL.
     * 
     * @param url The URL to get the ID for.
     * @return The id (Map.ID) or null if URL is not an ID.
     */
    public ID getIDFromURL(URL url) {
	String tmp;
	URL tmpURL;
	if (url == null) return null;
	String urlString = url.toExternalForm();
	for (Enumeration e = resource.getKeys() ; e.hasMoreElements() ;) {
	    String key = (String) e.nextElement();
	    try {
		tmp = resource.getString(key);
		tmpURL = new URL(base, tmp);

		// Sometimes tmp will be null because not all keys are ids
		if (tmpURL == null) continue;
		String tmpString = tmpURL.toExternalForm();
		if (urlString.compareTo(tmpString) == 0) {
		    return ID.create(key, helpset);
		}
	    } catch (Exception ex) {
	    }
	}
	return null;
    }

    /**
     * Determines the ID that is "closest" to this URL (with a given anchor).
     *
     * The definition of this is up to the implementation of Map.  In particular,
     * it may be the same as getIDFromURL().
     *
     * @param url A URL
     * @return The closest ID in this map to the given URL
     */
    public ID getClosestID(URL url) {
	return getIDFromURL(url);
    }


    /**
     * Determines the IDs related to this URL.
     *
     * @param URL The URL to compare the Map IDs to.
     * @return Enumeration of Map.IDs
     */
    public Enumeration getIDs(URL url) {
	String tmp=null;
	URL tmpURL=null;
	Vector ids = new Vector();
	for (Enumeration e = resource.getKeys() ; e.hasMoreElements() ;) {
	    String key = (String) e.nextElement();
	    try {
		tmp = resource.getString(key);
		tmpURL = new URL(base, tmp);
		if (url.sameFile(tmpURL) == true) {
		    ids.addElement(key);
		}
	    } catch (Exception ex) {
	    }
	}
	return new FlatEnumeration(ids.elements(), helpset);
    }

    private static class FlatEnumeration implements Enumeration {
	private Enumeration e;
	private HelpSet hs;

	public FlatEnumeration(Enumeration e, HelpSet hs) {
	    this.e = e;
	    this.hs = hs;
	}

	public boolean hasMoreElements() {
	    return e.hasMoreElements();
	}

	public Object nextElement() {
	    Object back = null;
	    try {
		back = ID.create((String) e.nextElement(), hs);
	    } catch (Exception ex) {
	    }
	    return back;
	}
    }


    /**
     * FlatMapResourceBundle is a ResourceBundle but unlike most 
     * ResourceBundles it is not locale-based and is loaded via the
     * constructor, not getBundle.
     */
    protected class FlatMapResourceBundle extends ResourceBundle 
        implements ParserListener, Serializable  
    {

	private Hashtable lookup = null;
	private boolean startedmap;
	private URL source;

	/**
	 * Creates the FlatMap from the data.
	 */
	public FlatMapResourceBundle(URL url) {
	    source = url;
	    Reader src;
	    try {
		URLConnection uc = url.openConnection();
		src = XmlReader.createReader(uc);
		parse(src);
		src.close();
	    } catch (Exception e) {
		reportMessage("Exception caught while parsing "+url+" "+
				   e.toString(), false);
	    }
	    parsingEnded();
	    for (Enumeration e = lookup.keys() ; e.hasMoreElements() ;) {
		String key1 = (String) e.nextElement();
		String url1 = (String) lookup.get(key1);
	    }
	}

	/**
	 * Overrides ResourceBundle, same semantics.
	 */
	public final Object handleGetObject(String key) {
	    return lookup.get(key); // this class ignores locales
	}

	/**
	 * Implements ResourceBundle.getKeys.
	 */
	public Enumeration getKeys() {
	    return lookup.keys();
	}

	/**
	 * Parses a reader into a MutableTreeNode
	 * Only one of these at a time.
	 */
	synchronized void parse(Reader src)
	    throws IOException 
	{
	    lookup = new Hashtable(10);

	    Parser parser = new Parser(src); // the XML parser instance
	    parser.addParserListener(this);
	    parser.parse();
	}

	/**
	 *  A tag was parsed.
	 */
	public void tagFound(ParserEvent e) {
	    Locale locale = null;
	    Tag tag = e.getTag();
	    FlatMap.debug("TagFound: "+tag.name);
	    TagProperties attr = tag.atts;

	    // Nothing tricky about mapID it doesn't have any hierarchy to it.
	    if (tag.name.equals("mapID")) {
		if (!startedmap) {
		    parsingError("map.invalidMapFormat");
		}

		String target = null;
		String url = null;
		if (attr != null) {
		    target = attr.getProperty("target");
		    url = attr.getProperty("url");
		}
		if (target == null || url == null) {
		    reportMessage("Failure in mapID Creation;", true);
		    reportMessage("  target: "+ target, true);
		    reportMessage("  url: "+ url, true);
		    return;
		}
		lookup.put(target, url);
		return;
	    } else if (tag.name.equals("map")) {
		if (!tag.isEnd) {
		    if (attr != null) {
		        String version = attr.getProperty("version");
			if (version != null && 
			    (version.compareTo("1.0") != 0 &&
			     version.compareTo("2.0") != 0)) {
			    parsingError("map.unknownVersion", version);
			}
		    }
		    if (startedmap) {
			parsingError("map.invalidMapFormat");
		    }
		    startedmap = true;
		} else {
		    if (startedmap) {
			startedmap = false;
		    }
		}
		return;
	    }
	}

	/**
	 *  A PI was parsed.  This method is not intended to be of general use.
	 */
	public void piFound(ParserEvent e) {
	    // ignore
	}

	/**
	 *  A DOCTYPE was parsed.  This method is not intended to be of general use.
	 */
	public void doctypeFound(ParserEvent e) {
	    String publicID = e.getPublicId();
	    if (publicID == null ||
		(publicID.compareTo(publicIDString) != 0 &&
		 publicID.compareTo(publicIDString_V2) != 0)) {
		parsingError("map.wrongPublicID", publicID);
	    }
	}

	/**
	 * A continous block of text was parsed.
	 */
	public void textFound(ParserEvent e) {
	    // At the current time I don't care about text. All the text is
	    // within the attributes in the tag
	}

	// The remaing events from Parser are ignored
	public void commentFound(ParserEvent e) {}

	public void errorFound(ParserEvent e){
	    reportMessage(e.getText(), false);
	}


	private Vector messages = new Vector();
	private boolean validParse = true;

	/**
	 * Reports an error message.
	 */
	public void reportMessage(String msg, boolean validParse) {
	    messages.addElement(msg);
	    this.validParse = this.validParse && validParse;
	}

	/**
	 * Enumerates all the error messages.
	 */
	public Enumeration listMessages() {
	    return messages.elements();
	}

	/**
	 * Parsing has ended.  We are given a last chance to do something
	 * to the HelpSet
	 */
	private void parsingEnded() {
	    if (! validParse) {
		if (lookup != null) {
		    lookup.clear();
		}

		// A parse with problems...
		FlatMap.debug("Parsing failed for "+source);

		for (Enumeration e = messages.elements();
		     e.hasMoreElements();) {
		    String msg = (String) e.nextElement();

		    FlatMap.debug(msg);
		}
	    } else {
		// little memory clean up
		source = null;
	    }
	}

	// Convenience methods
	private void parsingError(String key) {
	    String s = HelpUtilities.getText(key);
	    reportMessage(s, false); // tree will be wrong
	}

	private void parsingError(String key, String s) {
	    String msg = HelpUtilities.getText(key, s);
	    reportMessage(msg, false); // tree will be wrong
	}

    }

    /**
     * For printf debugging...
     */
    private static final boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("FlatMap: " + str);
        }
    }

}
