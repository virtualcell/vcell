/*
 * @(#)HelpSet.java	1.108 06/10/30
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
import java.awt.Dimension;
import java.awt.Point;
import javax.help.event.EventListenerList;
import javax.help.DefaultHelpBroker;
import javax.help.event.HelpSetListener;
import javax.help.event.HelpSetEvent;
import javax.help.Map.ID;

// implementation-specific
import com.sun.java.help.impl.Parser;
import com.sun.java.help.impl.ParserListener;
import com.sun.java.help.impl.ParserEvent;
import com.sun.java.help.impl.Tag;
import com.sun.java.help.impl.TagProperties;
import com.sun.java.help.impl.XmlReader;
import com.sun.java.help.impl.LangElement;
import javax.help.Map.ID;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
/**
 * A HelpSet  is a collection of help information consisting of a HelpSet
 * file, table of contents (TOC), index, topic files, and Map file.
 * The HelpSet file is the portal to the HelpSet.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @version	1.108	10/30/06
 */

public class HelpSet implements Serializable{
    private static String errorMsg = null;
    /*
     * Event listeners for adding to the HelpSet
     */

    protected EventListenerList listenerList = new EventListenerList();

    /**
     * PublicID (known to this XML processor) to the DTD for version 1.0 of the HelpSet
     */
    public static final String publicIDString =
        "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN";

    /**
     * PublicID (known to this XML processor) to the DTD for version 2.0 of the HelpSet
     */
    public static final String publicIDString_V2 =
        "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN";

    /**
     * Information for implementation customization.
     * 
     * helpBroker/class is used to locate the class for a HelpBroker.
     * helpBroker/loader is used to determine the ClassLoader to use.
     */

    public static final Object implRegistry =
        new StringBuffer("HelpSet.implRegistry");
    public static final String helpBrokerClass = "helpBroker/class";
    public static final String helpBrokerLoader = "helpBroker/loader";

    /**
     * HelpSet context information.
     *
     * A HelpSet can map between keys (String) and values (Strings).
     * There is a per-HelpSet value and a default value.
     * The per-HelpSet value is specified in the appropriate section of the
     * HelpSet file.
     * The default value is global and only specified at class initialization time.
     */

    public static final Object kitTypeRegistry = 
        new StringBuffer("JHelpViewer.kitTypeRegistry");
    public static final Object kitLoaderRegistry = 
        new StringBuffer("JHelpViewer.kitLoaderRegistry");

    /**
     * Creates an empty HelpSet that one can parse into.
     * @param loader The ClassLoader to use. If loader is null, the default
     * ClassLoader is used.
     */
    public HelpSet(ClassLoader loader) {
	this.helpsets = new Vector();
	this.loader = loader;
    }

    /**
     * Creates an empty HelpSet. Uses the default ClassLoader
     */
    public HelpSet() {
	this.helpsets = new Vector();
	this.loader = null;
    }

    /**
     * Creates a HelpSet.  The locale for the data is either that indicated in
     * the <tt>lang</tt> attribute of the <tt>helpset</tt> tag, or
     * <tt>Locale.getDefault()</tt> if the <tt>lang</tt> attribute is not present.
     *
     * @param loader The class loader to use to locate any classes
     * required by the navigators in the Helpset
     * If loader is null, the default ClassLoader is used.
     * @param helpset The URL to the HelpSet "file"
     *
     * @exception HelpSetException if there are problems parsing the helpset
     */
    public HelpSet(ClassLoader loader, URL helpset) throws HelpSetException {
	this(loader);
	this.helpset = helpset;	// so it can be used in parseInto()

	HelpSetFactory factory = new DefaultHelpSetFactory();

	parseInto(helpset, factory);
	HelpSet x = factory.parsingEnded(this); // use the error reporting

	if (x == null) {
	    // We had trouble parsing
	    // May need to revisit this...
            throw new HelpSetException("Could not parse\n"+errorMsg);
	}
    }


    /**
     * Locates a HelpSet file and return its URL.
     * Applies localization conventions.
     *
     * @param cl The classloader to use when searching for the resource
     * with the appropriate name. If cl is null the default 
     * ClassLoader is used.
     * @param shortName The shortname of the resource.
     * @param extension The extension of the resource.
     * @param locale The desired Locale
     * @see javax.help.HelpUtilities
     */

    public static URL findHelpSet(ClassLoader cl,
				  String shortName,
				  String extension,
				  Locale locale) {
	// Test for whether URL can be opened! - workaround Browser bugs
	return HelpUtilities.getLocalizedResource(cl,
						  shortName,
						  extension,
						  locale,
						  true);
    }

    /**
     * Locates a HelpSet file and return its URL.
     *
     * If the name does not end with the ".hs" extension, the
     * ".hs" extension is appended and localization rules
     * are applied to it.
     *
     * @param cl The classloader to use. If cl is null the default 
     * ClassLoader is used.
     * @param name The name of the resource.
     * @param locale The desired locale.
     */
    public static URL findHelpSet(ClassLoader cl,
                                 String name,
                                 Locale locale) {
	String shortName;
	String extension;
	if (name.endsWith(".hs")) {
	    shortName = name.substring(0, name.length()-3);
	    extension = ".hs";
	} else {
	    shortName = name;
	    extension = ".hs";
	}
	return findHelpSet(cl, shortName, extension, locale);
    }

    /**
     * As above but default on locale to Locale.getDefault()
     *
     * @param cl The ClassLoader to use. If cl is null the default 
     * ClassLoader is used.
     * @param name The name of the resource.
     * @return Null if not found.
     */
    public static URL findHelpSet(ClassLoader cl,
                                 String name) {
	return findHelpSet(cl, name, Locale.getDefault());
    }


    /**
     * Creates a presentation object for this HelpSet.
     * Consults the <tt>implRegistry</tt> of <tt>KeyData</tt> for 
     * the class name (as helpBrokerClass) and for the ClassLoader 
     * instance (as helpBrokerLoader) and then tries to instantiate 
     * that class.  It then invokes <tt>setHelpSet()</tt> with
     * this instance of HelpSet as the argument.  The resulting object is  
     * returned.
     * @see createHelpBroker(String)
     */
    public HelpBroker createHelpBroker() {
	return createHelpBroker(null);
    }

    /**
     * Creates a presentation object for this HelpSet.
     * Consults the <tt>implRegistry</tt> of <tt>KeyData</tt> for 
     * the class name (as helpBrokerClass) and for the ClassLoader 
     * instance (as helpBrokerLoader) and then tries to instantiate 
     * that class.  It then invokes <tt>setHelpSet()</tt> with
     * this instance of HelpSet as the argument.  The resulting object is  
     * returned.
     * @param presenationName A presentation name defined in the HelpSet 
     *                        that will dictate the presentation.
     * @return HelpBroker The created HelpBroker
     * @since 2.0
     * @see createHelpBroker()
     */
    public HelpBroker createHelpBroker(String presentationName) {
        HelpBroker back = null;

	String classname =
	    (String) getKeyData(implRegistry, helpBrokerClass);
	ClassLoader loader =
	    (ClassLoader) getKeyData(implRegistry, helpBrokerLoader);

	if (loader == null) {
	    loader = getLoader();
	}
	try {
	    Class c;
	    if (loader != null) {
		c = loader.loadClass(classname);
	    } else {
		c = Class.forName(classname);
	    }
	    back = (HelpBroker) c.newInstance();
	} catch (Throwable e) {
	    back = null;
	}

        if (back != null) {
            back.setHelpSet(this);
	    HelpSet.Presentation hsPres = null;
	    // If there is a presentation name find it otherwise get the
	    // default if one exists.
	    if (presentationName != null) {
		hsPres = getPresentation(presentationName);
	    } else {
		hsPres = getDefaultPresentation();
	    }
	    if (hsPres != null) {
		back.setHelpSetPresentation(hsPres);
	    }
	}
        return back;
    }

    /**
     * Adds a HelpSet, HelpSetEvents are generated.
     * Adding a composed HelpSet to another is equivalent to
     * adding all the HelpSets individually.
     *
     * @param hs The HelpSet to add.
     */
    public void add(HelpSet hs) {
	debug("add("+hs+")");
	helpsets.addElement(hs);
	fireHelpSetAdded(this, hs);
	combinedMap = null;	// invalidate the map
    }

    /**
     * Removes a HelpSet from this HelpSet; HelpSetEvents are generated
     * Return True if it is found, otherwise false.
     *
     * @param hs The HelpSet to remove. 
     * @return False if the hs is null or was not in this HelpSet
     */
    public boolean remove(HelpSet hs) {
	if (helpsets.removeElement(hs)) {
	    fireHelpSetRemoved(this, hs);
	    combinedMap = null;	// HERE - invalidate it - epll
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Enumerates all the HelpSets that have been added to this one.
     *
     * @return An enumeration of the HelpSets that have been added to
     * this HelpSet.
     */
    public Enumeration getHelpSets() {
	return helpsets.elements();
    }

    /**
     * Determines if a HelpSet is a sub-HelpSet of this object.
     *
     * @param hs The HelpSet to check
     * @return true If <tt>hs</tt> is contained in this HelpSet or in one of its children.
     */
    public boolean contains(HelpSet hs) {
	if (hs == this) {
	    return true;
	}
	for (Enumeration e = helpsets.elements();
	     e.hasMoreElements();) {
	    HelpSet child = (HelpSet) e.nextElement();
	    if (child.contains(hs)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Adds a listener for the HelpSetEvent posted after the model has
     * changed.
     * 
     * @param l - The listener to add.
     * @see javax.help.HelpSet#removeHelpSetListener. 
     * @throws IllegalArgumentException if l is null.
     */
    public void addHelpSetListener(HelpSetListener l) {
	debug("addHelpSetListener("+l+")");
	listenerList.add(HelpSetListener.class, l);
    }

    /**
     * Removes a listener previously added with <tt>addHelpSetListener</tt>
     *
     * @param l - The listener to remove.
     * @see javax.help.HelpSet#addHelpSetListener. 
     * @throws IllegalArgumentException if l is null.
     */
    public void removeHelpSetListener(HelpSetListener l) {
	listenerList.remove(HelpSetListener.class, l);
    }

    /**
     * Fires a helpSetAdded event.
     */
    protected void fireHelpSetAdded(Object source, HelpSet helpset){
	Object[] listeners = listenerList.getListenerList();
	HelpSetEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == HelpSetListener.class) {
		if (e == null) {
		    e = new HelpSetEvent(this, helpset,
					 HelpSetEvent.HELPSET_ADDED);
		}
		((HelpSetListener)listeners[i+1]).helpSetAdded(e);
	    }	       
	}
    }

    /**
     * Fires a helpSetRemoved event.
     */
    protected void fireHelpSetRemoved(Object source, HelpSet helpset){
	Object[] listeners = listenerList.getListenerList();
	HelpSetEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == HelpSetListener.class) {
		if (e == null) {
		    e = new HelpSetEvent(this, helpset,
					 HelpSetEvent.HELPSET_REMOVED);
		}
		((HelpSetListener)listeners[i+1]).helpSetRemoved(e);
	    }	       
	}
    }

    // ======= Labels, etc =======
    /**
     * Gets the title of this HelpSet.
     *
     * @return the title
     */
    public String getTitle() {
	if (title == null) {
	    return "";
	} else {
	    return title;
	}
    }

    /**
     * Sest the title for this HelpSet.  This is a bound property.
     *
     * @param title The title to set.
     */
    
    public void setTitle(String title) {
	String oldTitle = this.title;
	this.title = title;
	changes.firePropertyChange("title", oldTitle, title);
    }

    /**
     * Gets the locale for this HelpSet.
     * 
     * @return The locale.
     */
    public Locale getLocale() {
	return locale;
    }

    /**
     * Sets the locale for this HelpSet.
     * Strictly a private routine but the read-only property is bound.
     *
     * @param locale The locale to set.
     */
    private void setLocale(Locale l) {
	Locale oldLocale = locale;
	locale = l;
	changes.firePropertyChange("locale", oldLocale, locale);
    }

    /**
     * Returns 
     * the ID to visit when the user makes a "go home" gesture.
     * This can be identified in the project file, but may also be changed
     * programmatically or (possibly) via the UI.
     * 
     * @return The ID of home. A null is returned if homeID is null
     * or if an ID cannot be created for the homeID.
     */
    public ID getHomeID() {
	if (homeID == null) {
	    return null;
	} else {
	    try {
		return ID.create(homeID, this);
	    } catch (Exception ex) {
		return null;
	    }
	}
    }

    /**
     * Sets the Home ID for a HelpSet.  This is a bound property.
     *
     * @param The ID (in the Map) that identifies the default topic for this HelpSet. Null is valid homeID.
     */
    public void setHomeID(String homeID) {
	String oldID = homeID;
	this.homeID = homeID;
	changes.firePropertyChange("homeID", oldID, homeID);
    }

    // WARNING/HERE: In this implementation, the Map is not updated automatically
    // so you need to come get a new one - epll

    // Warning.  This is not handling recursive aggregation

    /**
     * The map for this HelpSet.  This map involves the closure of 
     * this HelpSet's children HelpSets.
     *
     * @return The map
     */
    public Map getCombinedMap() {
 	if (combinedMap == null) {
 	    combinedMap = new TryMap();
	    if (map != null) {
		combinedMap.add(map); // the local map
	    }
	    for (Enumeration e = helpsets.elements();
		 e.hasMoreElements(); ) {
		HelpSet hs = (HelpSet) e.nextElement();
		combinedMap.add(hs.getCombinedMap());
	    }
	}
	return combinedMap;
    }

    /**
     * Get the local (i.e.<!-- --> non-recursive) Map for this HelpSet.
     * This Map does not include the Maps for its children.
     *
     * @return The Map object that associates ID->URL. A null map is valid.
     */
    public Map getLocalMap() {
	return this.map;
    }

    /**
     * Set the Map for this HelpSet.  This Map object is not recursive; for example,
     * it does not include the Maps for its children.
     *
     * @param The Map object that associates ID->URL. A null map is a valid.
     */
    public void setLocalMap(Map map) {
	this.map = map;
    }

    /**
     * The URL that is the base for this HelpSet.
     *
     * @return The URL that is base to this HelpSet.
     */
    public URL getHelpSetURL() {
	return helpset;
    }

    /**
     * A classloader to use when locating classes.
     *
     * @return The ClassLoader to use when locating classes mentioned
     * in this HelpSet.
     */

    public ClassLoader getLoader() {
	return loader;
    }

    /**
     * NavigatorView describes the navigator views that are requested
     * by this HelpSet.
     *
     * @return The array of NavigatorView.
     */

    public NavigatorView[] getNavigatorViews() {
	NavigatorView back[] = new NavigatorView[views.size()];
	views.copyInto(back);
	return back;
    }

    /**
     * Gets the NavigatorView with a specific name.
     *
     * @param The name of the desired navigator view.
     */
    public NavigatorView getNavigatorView(String name) {
	debug("getNavigatorView("+name+")");
	for (int i=0; i<views.size(); i++) {
	    NavigatorView view = (NavigatorView) views.elementAt(i);
	    if (view.getName().equals(name)) {
		debug("  = "+view);
		return view;
	    }
	}
	debug("  = null");
	return null;
    }

    /**
     * HelpSet.Presentation describes the presentations that are defined
     * by this HelpSet.
     *
     * @return The array of HelpSet.Presentations.
     */

    public HelpSet.Presentation [] getPresentations() {
	HelpSet.Presentation back[] = new HelpSet.Presentation[presentations.size()];
	presentations.copyInto(back);
	return back;
    }

    /**
     * Gets the HelpSet.Presentation with a specific name.
     *
     * @param The name of the desired HelpSet.Presentation.
     */
    public HelpSet.Presentation getPresentation(String name) {
	debug("getPresentation("+name+")");
	for (int i=0; i<presentations.size(); i++) {
	    HelpSet.Presentation pres = (HelpSet.Presentation) presentations.elementAt(i);
	    if (pres.getName().equals(name)) {
		debug("  = "+pres);
		return pres;
	    }
	}
	debug("  = null");
	return null;
    }

    public HelpSet.Presentation getDefaultPresentation() {
	return defaultPresentation;
    }

    /**
     * Prints Name for this HelpSet.
     */
    public String toString() {
	return getTitle();
    }

    // ===== Public interfaces to parsing

    /**
     * Parsed a HelpSet file.
     */
    public static HelpSet parse(URL url,
				ClassLoader loader,
				HelpSetFactory factory) {
	HelpSet hs = new HelpSet(loader); // an empty HelpSet
	hs.helpset = url;
	hs.parseInto(url, factory);
	return factory.parsingEnded(hs);
    }

    /**
     * Parses into this HelpSet.
     */
    public void parseInto(URL url,
			  HelpSetFactory factory) {
	Reader src;
	try {
	    URLConnection uc = url.openConnection();
	    src = XmlReader.createReader(uc);
	    factory.parsingStarted(url);
	    (new HelpSetParser(factory)).parseInto(src, this);
	    src.close();
	} catch (Exception ex) {
	    factory.reportMessage("Got an IOException ("+
				       ex.getMessage()+
				       ")", false);
	    if(debug)
                ex.printStackTrace();
	}

	// Now add any subhelpsets
	for (int i=0; i<subHelpSets.size(); i++) {
	    HelpSet subHS = (HelpSet) subHelpSets.elementAt(i);
	    add(subHS);
	}
    }

    /**
     * The default HelpSetFactory that processes HelpSets.
     */

    public static class DefaultHelpSetFactory implements HelpSetFactory {
	private Vector messages = new Vector();
	private URL source;
	private boolean validParse = true;

	/**
	 * Parsing starts.
	 */
	public void parsingStarted(URL source) {
	    if (source == null) {
		throw new NullPointerException("source");
	    }
	    this.source = source;
	}

	/**
	 * Process a DOCTYPE
	 * @param publicID the document. If null or is not valid a parsingError
	 * will be generated.
	 */
	public void processDOCTYPE(String root, 
				   String publicID,
				   String systemID) {
	    if (publicID == null ||
		(publicID.compareTo(publicIDString) != 0 &&
		 publicID.compareTo(publicIDString_V2) != 0)) {
		parsingError("helpset.wrongPublicID", publicID);
	    }
	}

	/**
	 * Processes a PI
	 */
	public void processPI(HelpSet hs,
			      String target,
			      String data) {
	    // ignore for now
	}

	/**
	 * A title is found
	 */
	public void processTitle(HelpSet hs,
				 String value) {
	    String title = hs.getTitle();
	    if ((title != null) && !title.equals("")) {
		parsingWarning("helpset.wrongTitle", value, title);
	    }
	    hs.setTitle(value);
	}

	/**
	 * A HomeID is found.
	 */

	public void processHomeID(HelpSet hs,
				  String value) {
	    ID homeID = hs.getHomeID();
	    if ((homeID == null) || homeID.equals("")) {
		//parsingError("helpset.wrongHomeID", value, homeID.id);
                hs.setHomeID(value);
            }else{
                parsingError("helpset.wrongHomeID", value, homeID.id);
            }

	}

	/**
	 * process a &lt;mapref&gt;
	 *
	 * @param Spec to the URL
	 * @param Attributes for the tag
	 */
	public void processMapRef(HelpSet hs,
				  Hashtable attributes) {
	    String spec = (String) attributes.get("location");
	    URL hsURL = hs.getHelpSetURL();
	    try {
		Map map = new FlatMap(new URL(hsURL, spec), hs);
		Map omap = hs.getLocalMap();
		if (omap == null) {
                    debug("map is null");
		    hs.setLocalMap(map);
		} else {
		    // to implement multiple Maps add this code:
		    //
 		    if (omap instanceof TryMap) {
                        debug("map is TryMap");
		        ((TryMap)omap).add(map);
                        ///
                        ///what about hs.setLocalMap////
                        ///
                        hs.setLocalMap(omap);
		    } else {
                        debug("map is not TryMap");
		        TryMap tmap = new TryMap();
                        tmap.add(omap);
		        tmap.add(map);
		        hs.setLocalMap(tmap);
		    }

		}
	    } catch (MalformedURLException ee) {
		parsingError("helpset.malformedURL", spec);
	    } catch (IOException ee) {
		parsingError("helpset.incorrectURL", spec);
	    } catch (Exception ex) {
		// parsing error...
	    }
	}

	/*
	 * Called per &lt;view&gt;
	 */
	public void processView(HelpSet hs,
				String name,
				String label,
				String type,
				Hashtable viewAttributes,
				String data,
				Hashtable dataAttributes,
				Locale locale) {
                
	     try {
		 NavigatorView view;
		 if (data != null) {
		     if (dataAttributes == null) {
			 dataAttributes = new Hashtable();
		     }
		     dataAttributes.put("data", data);
		     
		 }
		 
		 view = NavigatorView.create(hs,
					     name, label,
					     locale,
					     type,
					     dataAttributes);
		 
		 if (view == null) {
		     // ignore ?? 
		 } else {
		     
		     hs.addView(view);
		 }
	     } catch (Exception ex) {
		 // ignore this view...
	     }
	}

	/*
	 * Called per &lt;presentation&gt;
	 */
	public void processPresentation(HelpSet hs,
					String name,
					boolean defaultPresentation,
					boolean displayViews,
					boolean displayViewImages,
					Dimension size,
					Point location,
					String title,
					String imageID,
					boolean toolbar,
					Vector helpActions) {

	    Map.ID imageMapID = null;
	    try {
		imageMapID = ID.create(imageID, hs);
	    } catch (BadIDException bex2) {
	    } 
	    try {
		HelpSet.Presentation presentation = 
		    new HelpSet.Presentation(name, displayViews, 
					     displayViewImages, size,
					     location, title, imageMapID, 
					     toolbar, helpActions);

		if (presentation == null) {
		    // ignore ?? 
		} else {
		    hs.addPresentation(presentation, defaultPresentation);
		}
	    } catch (Exception ex) {
		// ignore this presentation...
	    }
	}

	/**
	 * Called when a sub-HelpSet is found.
	 */
	public void processSubHelpSet(HelpSet hs,
				      Hashtable attributes) {
	    debug("createSubHelpSet");

	    String spec = (String) attributes.get("location");
	    URL base = hs.getHelpSetURL();
	    
	    debug("  location: "+spec);
	    debug("  base helpset: "+base);
	    
	    URL u = null;
	    HelpSet subHS = null;
	    try {
		u = new URL(base, spec);
		// test and see if the file is there
		// if it doesnt' through an exception all is ok
		InputStream is = u.openStream();
		if (is != null) {
		    subHS = new HelpSet(hs.getLoader(), u);
		    if (subHS != null) {
			hs.addSubHelpSet(subHS);
		    }
		}
	    } catch (MalformedURLException ex) {
		// ignore a malformed URL
		// The subhelpset is just ignored
	    } catch (IOException ex) {
		// ignore an IOException
		// The subhelpset is just ignored
	    } catch (HelpSetException ex) {
		parsingError("helpset.subHelpSetTrouble", spec);
	    }
	}

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
	 * Parsing has ended. Last chance to do something
	 * to the HelpSet.
	 * @param hs The HelpSet the parsing ended on. A null hs is valid.
	 */
	public HelpSet parsingEnded(HelpSet hs) {
	    HelpSet back = hs;
	    if (! validParse) {
		// A parse with problems...
		back = null;

                String errMsg = "Parsing failed for "+source;
                //System.err.println(errMsg);
                messages.addElement(errMsg);

		for (Enumeration e = messages.elements();
		     e.hasMoreElements();) {
		    String msg = (String) e.nextElement();
                    if(debug)
                        System.err.println(msg);                        
                    if(HelpSet.errorMsg == null)
                        HelpSet.errorMsg = msg;
                    else{
                       HelpSet.errorMsg = HelpSet.errorMsg+"\n"; 
                       HelpSet.errorMsg = HelpSet.errorMsg + msg;
                    }

		}
	    }
	    return back;
	}

	// Convenience methods
	private void parsingError(String key) {
	    String s = HelpUtilities.getText(key);
	    reportMessage(s, false); // tree will be wrong
	}

	/**
	 * @throws Error if key is invalid.
	 */
	private void parsingError(String key, String s) {
	    String msg = HelpUtilities.getText(key, s);
	    reportMessage(msg, false); // tree will be wrong
	}

	/**
	 * @throws Error if key is invalid.
	 */
	private void parsingError(String key, String s1, String s2) {
	    String msg = HelpUtilities.getText(key, s1, s2);
	    reportMessage(msg, false); // tree will be wrong
	}

	private void parsingWarning(String key, String s1, String s2) {
	    String msg = HelpUtilities.getText(key, s1, s2);
	    reportMessage(msg, true); // warning only
	}

    } // End of DefaultHelpSetFactory


    /**
     * HelpSet Presentation class. Contains information concerning a
     * presentation in a HelpSet file
     * @since 2.0
     */
    public static class Presentation {

	private String name;
	private boolean displayViews;
	private boolean displayViewImages;
	private Dimension size;
	private Point location;
	private String title;
	private boolean toolbar;
	private Vector helpActions;
	private Map.ID imageID;

	public Presentation (String name,
			     boolean displayViews,
			     boolean displayViewImages,
			     Dimension size,
			     Point location,
			     String title,
			     Map.ID imageID,
			     boolean toolbar,
			     Vector helpActions) {
	    this.name = name;
	    this.displayViews = displayViews;
	    this.displayViewImages = displayViewImages;
	    this.size = size;
	    this.location = location;
	    this.title = title;
	    this.imageID = imageID;
	    this.toolbar = toolbar;
	    this.helpActions = helpActions;
	}

	public String getName() {
	    return name;
	}

	public String getTitle() {
	    return title;
	}

	public Map.ID getImageID() {
	    return imageID;
	}

	public boolean isViewDisplayed() {
	    return displayViews;
	}
	
	public boolean isViewImagesDisplayed() {
	    return displayViewImages;
	}
	
	public Dimension getSize() {
	    return size;
	}

	public Point getLocation() {
	    return location;
	}

	public boolean isToolbar() {
	    return toolbar;
	}

	/**
	 * Returns an Enumeration HelpActions created from the
	 * list of Actions in the Presentation.
	 *
	 * @see HelpAction
	 */
	public Enumeration getHelpActions(HelpSet hs, Object control) {
	    Vector actions = new Vector();
	    ClassLoader loader = hs.getLoader();
	    Class klass;
	    Constructor konstructor;
	    HelpAction action;

	    if (helpActions == null) {
		// got a nutziod who didn't check the isToolbar
		// just return back the empty vector elements
		return actions.elements();
	    }
	    Enumeration actionEnum = helpActions.elements();
	    while (actionEnum.hasMoreElements()) { 
		HelpSetFactory.HelpAction act = 
		    (HelpSetFactory.HelpAction)actionEnum.nextElement();

		try {
		    Class types[] = { Object.class };
		    Object args[] = { control };
		    if (loader == null) {
			klass = Class.forName(act.className);
		    } else {
			klass = loader.loadClass(act.className);
		    }
		    konstructor = klass.getConstructor(types);
		    action = (HelpAction) konstructor.newInstance(args);

		    // The HelpAction has been added now add any known
		    // attributes
		    if (act.attr.containsKey("image")) {
			String imageID = (String) act.attr.get("image");
			try {
			    Map.ID id = Map.ID.create(imageID, hs);
			    javax.swing.ImageIcon icon = null;
			    Map map = hs.getCombinedMap();
			    URL url = map.getURLFromID(id);
			    icon = new javax.swing.ImageIcon(url);
			    action.putValue("icon", icon);
			} catch (Exception ex) {
			}
		    }
		    actions.add(action);
		} catch (Exception ex) {
		    throw new RuntimeException("Could not create HelpAction " +
					       act.className);
		}
	    }
	    
	    return actions.elements();
	}

    }

    // =========== Protected Methods for use by subclasses ==========

    /**
     * Adds a NavigatorView to the current list.
     */
    protected void addView(NavigatorView view) {
	views.addElement(view);
    }

    /**
     * Adds a SubHelpSet to the current list.
     */

    protected void addSubHelpSet(HelpSet hs) {
	subHelpSets.addElement(hs);
    }

    /**
     * Adds a HelpSet.Presentation to the current list.
     */
    protected void addPresentation(HelpSet.Presentation presentation,
				   boolean defaultPres) {
	presentations.addElement(presentation);
	if (defaultPres) {
	    defaultPresentation = presentation;
	}
    }

    // ============= Simple registry ============

    /**
     * Gets some Data for a Key in a given context.
     * Local (per HelpSet instance) data is searched first, then defaults.
     */
      
    public Object getKeyData(Object context,
			     String key) {
	Object back = null;
	Hashtable h = (Hashtable) localKeys.get(context);
	if (h != null) {
	    back = h.get(key);
	}
	if (back == null) {
	    h = (Hashtable) defaultKeys.get(context);
	    if (h != null) {
		back = h.get(key);
	    }
	}
	return back;
    }

    /**
     * Sets some local KeyData on a given context.  The information is set on
     * a per-HelpSet basis.
     */
    public void setKeyData(Object context,
			   String key,
			   Object data) {
	Hashtable h = (Hashtable) localKeys.get(context);
	if (h == null) {
	    h = new Hashtable();
	    localKeys.put(context, h);
	}
	h.put(key, data);
    }

    /**
     * Default initialization.  This can only be done from within this class.
     */
    private static void setDefaultKeyData(Object context,
					  String key,
					  Object data) {
	if (defaultKeys == null) {
	    defaultKeys = new Hashtable();
	}
	Hashtable h = (Hashtable) defaultKeys.get(context);
	if (h == null) {
	    h = new Hashtable();
	    defaultKeys.put(context, h);
	}
	h.put(key, data);
    }


    /**
     * Initializes the default registries.
     */
    static {
        setDefaultKeyData(implRegistry,
			  helpBrokerClass, "javax.help.DefaultHelpBroker");
	setDefaultKeyData(kitTypeRegistry,
			  "text/html", "com.sun.java.help.impl.CustomKit");

	ClassLoader cl = HelpSet.class.getClassLoader();
	if (cl != null) {
	    setDefaultKeyData(implRegistry,
			      helpBrokerLoader, cl);
	    setDefaultKeyData(kitLoaderRegistry,
			      "text/html", cl);
	}
    }

    //======== Private members =========

    private String title;	// the title for this helpset
    private Map map;		// the local map
    private TryMap combinedMap;	// the combined ID<->URL map
    private URL helpset;	// the HelpSet from where to search
    private String homeID;
    private Locale locale = Locale.getDefault();

    private transient ClassLoader loader;	// encapsulates loading...

    private Vector views = new Vector();

    private Vector presentations = new Vector();

    private HelpSet.Presentation defaultPresentation = null;

    private Vector helpsets;	// All the helpsets added to this one

    private static HelpBroker defaultHelpBroker = null; // the default HelpBroker

    private Vector subHelpSets = new Vector();

    // Default and Local Hashtables for keys

    private static Hashtable defaultKeys;
    private Hashtable localKeys = new Hashtable();

    private PropertyChangeSupport changes = new PropertyChangeSupport(this);

    // ============= PRIVATE Parsing Class ========

    /**
     * Inner class for parsing a TOC stream.
     *
     * WARNING!! This class is an interim solution until when we move to a
     * real XML parser.  This is not a public class.  Clients should only use
     * the parse method in the enclosing class.
     */

    private static class HelpSetParser implements ParserListener {
	private Stack tagStack;	// the collection of active Parse tags
	private Locale defaultLocale;
	private Locale lastLocale;
	private HelpSet myHS;	// my HelpSet
	private Locale myHSLocale; // its locale
	private HelpSetFactory factory;	// the factory


	private String tagName;	  // genericly used for views and presentation 
	private String viewLabel;
	private String viewType;
	private String viewEngine;
        private String tagImage;
        private String helpActionImage;
	private String viewData;
        private String viewMergeType;
	private Hashtable htData;

	private boolean defaultPresentation = false;
	private boolean displayViews = true;
	private boolean displayViewImages = true;
	private Dimension size;
	private Point location;
	private String presentationTitle;
	private boolean toolbar;
	private Vector helpActions;
	private String helpAction;

	/**
	 * Creates a Parser (Listener) instance.
	 */
	HelpSetParser (HelpSetFactory factory) {
	    this.factory = factory;
	}

	/**
	 * Parses a reader into a HelpSet.
	 */
	synchronized void parseInto(Reader src,
				    HelpSet hs) 
	    throws IOException
        {
	    tagStack = new Stack();
	    defaultLocale = hs.getLocale();
	    lastLocale = defaultLocale;
	    myHS = hs;
	    myHSLocale = hs.getLocale();
	    Parser parser = new Parser(src); // the XML parser instance
	    parser.addParserListener(this);
	    parser.parse();
	}

	public void tagFound(ParserEvent e) {
            debug("tagFound " + e.getTag().name);
	    Locale locale = null;
	    LangElement le;
	    Tag tag = e.getTag();
	    String name = tag.name;
	    int x=0, y=0, width=0, height=0;
	    TagProperties attr = tag.atts;
	    Hashtable ht = (attr == null) ? null : attr.getHashtable();

	    if (attr != null) {
		String lang = attr.getProperty("xml:lang");
		locale = HelpUtilities.localeFromLang(lang);
                viewMergeType = attr.getProperty("mergetype");
		helpActionImage = attr.getProperty("image");
		String value = null;
		value = attr.getProperty("width");
		if (value != null) {
		    width = Integer.parseInt(value);
		}
		value = null;
		value = attr.getProperty("height");
		if (value != null) {
		    height = Integer.parseInt(value);
		}
		value = null;
		value = attr.getProperty("x");
		if (value != null) {
		    x = Integer.parseInt(value);
		}
		value = null;
		value = attr.getProperty("y");
		if (value != null) {
		    y = Integer.parseInt(value);
		}
		value = null;
		value = attr.getProperty("default");
		if (value != null && value.equals("true")) {
		    defaultPresentation = true;
		}
		value = null;
		value = attr.getProperty("displayviews");
		if (value != null && value.equals("false")) {
		    displayViews = false;
		}
		value = null;
		value = attr.getProperty("displayviewimages");
		if (value != null && value.equals("false")) {
		    displayViewImages = false;
		}
	    }
	    if (locale == null) {
		locale = lastLocale;
	    }
            
	    if (name.equals("helpset")) {
		if (tag.isEnd) {
		    removeTag(tag);
		} else {
		    // Check and see if the locale is different from the
		    // defaultLocale. If it is then reset the locale.
 		    if (! locale.equals(defaultLocale) &&
			! locale.equals(myHSLocale)) {
			if (locale != null) {
			    myHS.setLocale(locale);
			    defaultLocale = locale;
			}
		    }
		    if (attr != null) {
		        String version = attr.getProperty("version");
			if (version != null && 
			    (version.compareTo("1.0") != 0 &&
			     version.compareTo("2.0") != 0)) {
			    parsingError("helpset.unknownVersion", version);
			}
		    }
		    addTag(tag, locale);
		}
		return;
	    }

	    if (tagStack.empty()) {
		parsingError("helpset.wrongTopLevel", name);
	    }

	    // Get the parents name
	    le = (LangElement) tagStack.peek();
	    String pname = ((Tag) le.getTag()).name; // the parent

	    if (name.equals("title")) {
		// TITLE tag
		if (tag.isEnd) {
		    removeTag(tag);		// processing was done in textFound()
		} else {
		    if ((! pname.equals("helpset")) &&
			(! pname.equals("presentation"))){
			wrongParent(name, pname);
		    }
 		    if (! locale.equals(defaultLocale) &&
			! locale.equals(myHSLocale)) {
			wrongLocale(locale, defaultLocale, myHSLocale);
		    }
		    addTag(tag, locale);
		}
	    } else if (name.equals("homeID")) {
		// HOMEID tags
		if (tag.isEnd) {
		    removeTag(tag);		// processing was done in textFound()
		} else {
		    if (! pname.equals("maps")) {
			wrongParent(name, pname);
		    }
		    addTag(tag, locale);
		}
	    } else if (name.equals("mapref")) {
		// MAPREF tags

		// Remove from stack if an empty tag
		if (tag.isEnd && !tag.isEmpty) {
		    removeTag(tag);
		} else {
		    if (! pname.equals("maps")) {
			wrongParent(name, pname);
		    }
		    // add the tag if not an empty tag
		    if (! tag.isEmpty) {
			addTag(tag, locale);
		    }
		    // Process the tag
		    factory.processMapRef(myHS,
					  ht);
		}
	    } else if (name.equals("data")) {
		// DATA tag
		if (tag.isEnd) {
		    removeTag(tag);
		} else {
		    if (! pname.equals("view")) {
			wrongParent(name, pname);
		    } else {
			addTag(tag, locale);
		    }
		    htData = ht;
		}
	    } else if (name.equals("name") ||
		       name.equals("type") ||
		       name.equals("image")){                           
		// NAME, TYPE, IMAGE tag
		if (tag.isEnd) {
		    removeTag(tag);
		} else {
		    if ((! pname.equals("view")) && 
			(! pname.equals("presentation"))) {
			wrongParent(name, pname);
		    } else {
			addTag(tag, locale);
		    }
		}
	    } else if (name.equals("label")) {
		// LABEL tag
		// Special processing to check the locale attribute.
		if (tag.isEnd) {
		    removeTag(tag);
		} else {
		    if (! pname.equals("view")) {
			wrongParent(name, pname);
		    } else {
			if (! locale.equals(defaultLocale) &&
			    ! locale.equals(myHSLocale)) {
			    wrongLocale(locale, defaultLocale, myHSLocale);
			}
			addTag(tag, locale);
		    }
		}
	    } else if (name.equals("view")) {
		// VIEW tag
		if (tag.isEnd) {
		    removeTag(tag);

                    if (tagImage != null) {
                        if (htData == null) {
                            htData = new Hashtable();
			}
                        htData.put("imageID", tagImage);
                    }
                    
                    if (viewMergeType != null) {
                        if(htData == null) {
                            htData = new Hashtable();
			}
                        htData.put("mergetype",viewMergeType);
                    }

		    factory.processView(myHS,
					tagName,
					viewLabel,
					viewType,
					ht,
					viewData,
					htData,
					locale);
                    tagName = null;
                    viewLabel = null;
                    viewType = null;
                    tagImage = null;
                    viewData = null;
                    htData = null;
                    viewMergeType = null;

		} else {
		    if (! pname.equals("helpset")) {
			wrongParent(name, pname);
		    } else {
			addTag(tag, locale);
		    }
		}
	    } else if (name.equals("presentation")) {
		// Presentation tag
		if (tag.isEnd) {
		    removeTag(tag);

		    factory.processPresentation(myHS,
						tagName,
						defaultPresentation,
						displayViews,
						displayViewImages,
						size,
						location,
						presentationTitle,
						tagImage,
						toolbar,
						helpActions);

                    tagName = null;
                    defaultPresentation = false;
                    displayViews = true;
                    displayViewImages = true;
                    size = null;
                    location = null;
                    presentationTitle = null;
		    tagImage = null;
		    toolbar = false;
		    helpActions = null;

		} else {
		    if (! pname.equals("helpset")) {
			wrongParent(name, pname);
		    } else {
			addTag(tag, locale);
		    }
		}
	    } else if (name.equals("size")) {
		// DATA tag
		if (tag.isEnd) {
		    if (size == null) {
			size = new Dimension(width, height);
		    } else {
			size.setSize(width, height);
		    }
		    width = 0;
		    height = 0;
		    if (!tag.isEmpty) {
			removeTag(tag);
		    }
		} else {
		    if (! pname.equals("presentation")) {
			wrongParent(name, pname);
		    } else {
			addTag(tag, locale);
			size = new Dimension();
		    }
		}
	    } else if (name.equals("location")) {
		// DATA tag
		if (tag.isEnd) {
		    if (location == null) {
			location = new Point(x, y);
		    } else {
			location.setLocation(x, y);
		    }
		    x = 0;
		    y = 0;
		    if (!tag.isEmpty) {
			removeTag(tag);
		    }
		} else {
		    if (! pname.equals("presentation")) {
			wrongParent(name, pname);
		    } else {
			addTag(tag, locale);
			location = new Point();
		    }
		}
	    } else if (name.equals("toolbar")) {
		// DATA tag
		if (tag.isEnd) {
		    removeTag(tag);
		} else {
		    if (! pname.equals("presentation")) {
			wrongParent(name, pname);
		    } else {
			addTag(tag, locale);
			helpActions = new Vector();
			toolbar = true;
		    }
		}
	    } else if (name.equals("helpaction")) {
		// DATA tag
		if (tag.isEnd) {
		    removeTag(tag);
		    if (helpAction != null) {
			Hashtable tmp = new Hashtable();		      
			helpActions.add(new HelpSetFactory.HelpAction(helpAction, tmp));
			if (helpActionImage != null) {
			    tmp.put("image", helpActionImage);
			    helpActionImage = null;
			}
			helpAction = null;
		    }
		} else {
		    if (! pname.equals("toolbar")) {
			wrongParent(name, pname);
		    } else {
			addTag(tag, locale);
		    }
		}
	    } else if (name.equals("maps")) {
		// MAPS tag
		if (tag.isEnd) {
		    removeTag(tag);
		} else {
		    if (! pname.equals("helpset")) {
			wrongParent(name, pname);
		    } else {
			addTag(tag, locale);
		    }
		}
	    } else if (name.equals("subhelpset")) {
		// SUBHELPSET tag
		
		// Remove from stack if an empty tag
		if (tag.isEnd && !tag.isEmpty) {
		    removeTag(tag);
		} else {
		    // Add the tag if it isn't an inline tag
		    if (!tag.isEmpty) {
			addTag(tag, locale);
		    }
		    // Process the tag
		    factory.processSubHelpSet(myHS, ht);
		}
	    } else if (name.equals("impl")) {
		// Presentation tag
		if (tag.isEnd) {
		    removeTag(tag);
		    // Nothing to do here. Everything is done while
		    // processing the sub tags
		} else {
		    if (! pname.equals("helpset")) {
			wrongParent(name, pname);
		    } else {
			addTag(tag, locale);
		    }
		}
	    } else if (name.equals("helpsetregistry")) {
		if (tag.isEnd && !tag.isEmpty) {
		    removeTag(tag);
		} else {
		    if (! pname.equals("impl")) {
			wrongParent(name, pname);
		    } else {
			if (!tag.isEnd) {
			    addTag(tag, locale);
			}
			if (attr != null) {
			    String hbClass = attr.getProperty("helpbrokerclass");
			    if (hbClass != null) {
				myHS.setKeyData(implRegistry,
						helpBrokerClass, 
						hbClass);
			    }
			}
		    }
		}
	    } else if (name.equals("viewerregistry")) {
		if (tag.isEnd && !tag.isEmpty) {
		    removeTag(tag);
		} else {
		    if (! pname.equals("impl")) {
			wrongParent(name, pname);
		    } else {
			if (!tag.isEnd) {
			    addTag(tag, locale);
			}
			if (attr != null) {
			    String viewerType = attr.getProperty("viewertype");
			    String viewerClass = attr.getProperty("viewerclass");
			    if (viewerType != null && viewerClass != null) {
				ClassLoader cl = HelpSet.class.getClassLoader();
				myHS.setKeyData(kitTypeRegistry,
						viewerType, viewerClass);
				myHS.setKeyData(kitLoaderRegistry,
						viewerType, cl);
			    }
			}
		    }
		}
	    }
	}

	public void piFound(ParserEvent e) {
	    factory.processPI(myHS, e.getTarget(), e.getData());
	}

	public void doctypeFound(ParserEvent e) {
	    factory.processDOCTYPE(e.getRoot(), e.getPublicId(), e.getSystemId());
	}

	private void checkNull(String name, String t) {
	    if (! t.equals("")) {
		parsingError("helpset.wrongText", name, t);
	    }
	}

	public void textFound(ParserEvent e) {
	    debug("textFound: ");
	    debug("  text: "+e.getText());

	    if (tagStack.empty()) {
		return;		// ignore
	    }
	    LangElement le = (LangElement) tagStack.peek();
	    Tag tag = le.getTag();
	    TagProperties attr = tag.atts;
	    Hashtable ht = (attr == null) ? null : attr.getHashtable();
	    String text = e.getText().trim();
	    String name = tag.name;

	    if (name.equals("helpset")) {
		// HELPSET tag
		checkNull("helpset", text);
		return;
	    }
	    int depth = tagStack.size();
	    String pname = "";
	    if (depth >= 2) {
		le = (LangElement) tagStack.elementAt(depth-2);
		pname = ((Tag) le.getTag()).name; // the parent
	    }

	    if (name.equals("title")) {
		// TITLE tag
		if (pname.equals("helpset")) {
		    factory.processTitle(myHS, text);
		} else {
		    presentationTitle = text.trim();
		}
	    } else if (name.equals("homeID")) {
		// HOMEID tag
		factory.processHomeID(myHS, text);
	    } else if (name.equals("mapref")) {
		checkNull("mapref", text);
	    } else if (name.equals("subhelpset")) {
		checkNull("subhelpset", text);
	    } else if (name.equals("data")) {
		// DATA tag -- this is in a view
		viewData = text.trim();
	    } else if (name.equals("label")) {
		// LABEL tag -- this is in a view
		viewLabel = text.trim();
	    } else if (name.equals("name")) {
		// NAME tag -- this is in a view
		tagName = text.trim();
	    } else if (name.equals("helpaction")) {
		// NAME tag -- this is in a view
		helpAction = text.trim();
	    } else if (name.equals("type")) {
		// TYPE tag -- this is in a view
		viewType = text.trim();
            } else if (name.equals("image")) {
                // IMAGE tag -- this is in the view
                tagImage = text.trim();
	    } else if (name.equals("view")) {
		// VIEW tag
		checkNull("view", text);
	    } else if (name.equals("maps")) {
		// MAP tag
		checkNull("maps", text);
            } else if (name.equals("mergetype")) {
                 checkNull("mergetype",text);
            }
	}

	/**
	 * Method used to parse a HelpSet.
	 */
	public void errorFound(ParserEvent e) {
	    // Ignore it for now
	}

	/**
	 * Method used to parse a HelpSet.
	 */
	public void commentFound(ParserEvent e) {
	    // Ignore it for now
	}
    
    	/**
	 * addTag keeps track of tags and their locale attributes.
	 */
	protected void addTag(Tag tag, Locale locale) {
	    LangElement el = new LangElement(tag, locale);
	    tagStack.push(el);
	    // It's possible for lastLocale not be specified ergo null.
	    // If it is then set lastLocale to null even if locale is null.
	    // It is impossible for locale to be null
	    if (lastLocale == null) {
		lastLocale = locale;
		return;
	    }
	    if (locale == null) {
		lastLocale = locale;
		return;
	    }
	    if (! lastLocale.equals(locale)) {
		lastLocale = locale;
	    }
	}

	/**
	 * removeTag removes a tag from the tagStack. The tagStack is
	 * used to keep track of tags and locales.
	 */
	protected void removeTag(Tag tag) {
	    LangElement el;
	    String name = tag.name;
	    Locale newLocale = null;

	    for (;;) {
		if (tagStack.empty()) 
		    unbalanced(name);
		el = (LangElement) tagStack.pop();
		if (el.getTag().name.equals(name)) {
		    if (tagStack.empty()) {
			newLocale = defaultLocale;
		    } else {
			el = (LangElement) tagStack.peek();
			newLocale = el.getLocale();
		    }
		    break;
		}
	    }
	    // It's possible for lastLocale not be specified ergo null.
	    // If it is then set lastLocale to null even if locale is null.
	    // It also possible for locale to be null so if lastLocale is set
	    // then reset lastLocale to null;
	    // Otherwise if lastLocale doesn't equal locale reset lastLocale to locale
	    if (lastLocale == null) {
		lastLocale = newLocale;
		return;
	    }
	    if (newLocale == null) {
		lastLocale = newLocale;
		return;
	    }
	    if (! lastLocale.equals(newLocale)) {
		lastLocale = newLocale;
	    }
	}

	/**
	 * Handy error message methods.
	 */

	private void parsingError(String key) {
	    String s = HelpUtilities.getText(key);
	    factory.reportMessage(s, false); // tree will be wrong
	}

	private void parsingError(String key, String s) {
	    String msg = HelpUtilities.getText(key, s);
	    factory.reportMessage(msg, false); // tree will be wrong
	}

	private void parsingError(String key, String s1, String s2) {
	    String msg = HelpUtilities.getText(key, s1, s2);
	    factory.reportMessage(msg, false); // tree will be wrong
	}

	private void wrongParent(String name, String pname) {
	    parsingError("helpset.wrongParent", name, pname);
	}
	
	private void unbalanced(String name) {
	    parsingError("helpset.unbalanced", name);
	}

	private void wrongLocale(Locale found, Locale l1, Locale l2) {
	    String msg = HelpUtilities.getText("helpset.wrongLocale",
					     found.toString(),
					     l1.toString(),
					     l2.toString());
	    factory.reportMessage(msg, true); // will continue
	}
    } // End of HelpSetParser

    /**
     * For printf debugging.
     */
    private final static boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("HelpSet: " + str);
        }
    }
}
