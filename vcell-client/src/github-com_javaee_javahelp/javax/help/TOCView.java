/*
 * @(#)TOCView.java	1.37 06/10/30
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

import java.awt.Component;
import java.net.URL;
import java.net.URLConnection;
import java.io.Reader;
import java.io.IOException;
import java.util.Stack;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import java.util.Enumeration;
import javax.help.Map.ID;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;

import com.sun.java.help.impl.*;

/**
 * Navigational View information for a TOC.
 * This includes information about how to parse the data format.
 *
 * @author Eduardo Pelegri-Llopart
 * @version	1.18	03/10/99
 */

public class TOCView extends NavigatorView {

    /**
     * PublicID (known to this XML processor) to the DTD for version 1.0 of the TOC.
     */
    public static final String publicIDString =
        "-//Sun Microsystems Inc.//DTD JavaHelp TOC Version 1.0//EN";

    /**
     * PublicID (known to this XML processor) to the DTD for version 2.0 of the TOC.
     */
    public static final String publicIDString_V2 =
        "-//Sun Microsystems Inc.//DTD JavaHelp TOC Version 2.0//EN";


    private ID categoryOpenImageID = null;
    private ID categoryClosedImageID = null;
    private ID topicImageID = null;

    /**
     * Constructs a TOC VIew with some given data.  Locale defaults to that
     * of the HelpSet.
     *
     * @param hs The HelpSet that provides context information.
     * @param name The name of the View.
     * @param label The label (to show the user) of the View.
    * @param params A hashtable that provides different key/values for this type.
    */
    public TOCView(HelpSet hs,
		   String name,
		   String label,
		   Hashtable params) {
	this(hs, name, label, hs.getLocale(), params);
    }

    /**
     * Constructs a TOC VIew with some given data.
     *
     * @param hs The HelpSet that provides context information.
     * @param name The name of the View.
     * @param label The label (to show the user) of the View.
     * @param locale The default locale to interpret the data in this TOC.
    * @param params A hashtable that provides different key/values for this type.
    */
    public TOCView(HelpSet hs,
		   String name,
		   String label,
		   Locale locale,
		   Hashtable params) {
	super(hs, name, label, locale, params);
    }

    /**
     * Creates a navigator for a given model.
     */
    public Component createNavigator(HelpModel model) {
	return new JHelpTOCNavigator(this, model);
    }
    
    /**
     * Get the TOC navigators mergeType. Overrides getMergeType in NavigatorView
     */
    public String getMergeType() {
	String mergeType = super.getMergeType();
	if (mergeType == null) {
	    return "javax.help.AppendMerge";
	}
	return mergeType;
    }

    // turn this on if you want messages on failures
    private static boolean warningOfFailures = false;

    /**
     * Public method that gets a DefaultMutableTreeNode representing the information in this
     * view instance.
     * 
     * The default implementation parses the data in the URL but a subclass may
     * override this method and provide a different implemenation--for example,
     * by creating the tree programatically.
     */
    public DefaultMutableTreeNode getDataAsTree() {
	HelpSet hs = getHelpSet();
	Hashtable params = getParameters();
	URL url;

	if (params == null || 
	    (params != null && !params.containsKey("data"))) {
	    DefaultMutableTreeNode node = new DefaultMutableTreeNode();
	    return node;
	}

	try {
	    url = new URL(hs.getHelpSetURL(), (String) params.get("data"));
	} catch (Exception ex) {
	    throw new Error("Trouble getting URL to TOC data; "+ex);
	}

	return parse(url, hs, hs.getLocale(), new DefaultTOCFactory(), this);
    }

    /**
     * Public method for parsing a TOC in a URL.
     * Returns a DefaultMutableTreeNode whose children are the DefaultMutableTreeNode
     * corresponding to the tocitem's in the TOC.
     * The factory is invoked to create the TreeItems that are included in the
     * DefaultMutableTreeNode as user data.
     * The factory is also invoked with start data, and whenever any parsing error
     * is found.
     *
     * @param url Where the TOC lives. If null, returns a null value.
     * @param hs The HelpSet context for this TOC. Null hs is ignored.
     * @param locale The default locale to interpret the data in this TOC. Null locale 
     * is treated as the default locale.
     * @param factory A factory instance used to create the TOCItems.
     * @return A TreeNode that represents the TOC. Returns null if parsing errors were
     * encountered.
     */
    public static DefaultMutableTreeNode parse(URL url,
					       HelpSet hs,
					       Locale locale,
					       TreeItemFactory factory) {
	return parse(url, hs, locale, factory, null);
    }

    /**
     * Public method for parsing a TOC in a URL.
     * Returns a DefaultMutableTreeNode whose children are the DefaultMutableTreeNode
     * corresponding to the tocitem's in the TOC.
     * The factory is invoked to create the TreeItems that are included in the
     * DefaultMutableTreeNode as user data.
     * The factory is also invoked with start data, and whenever any parsing error
     * is found.
     *
     * @param url Where the TOC lives. If null, returns a null value.
     * @param hs The HelpSet context for this TOC. Null hs is ignored.
     * @param locale The default locale to interpret the data in this TOC. Null locale 
     * is treated as the default locale.
     * @param factory A factory instance used to create the TOCItems.
     * @param view The TOCView instance. May be null
     * @return A TreeNode that represents the TOC. Returns null if parsing errors were
     * encountered.
     */
    public static DefaultMutableTreeNode parse(URL url,
					       HelpSet hs,
					       Locale locale,
					       TreeItemFactory factory,
					       TOCView view) {
	Reader src;
	DefaultMutableTreeNode node = null;
	try {
	    URLConnection uc = url.openConnection();
	    src = XmlReader.createReader(uc);
	    factory.parsingStarted(url);
	    TOCParser tocParser = new TOCParser(factory, view);
	    node = (tocParser.parse(src, hs, locale));
	    src.close();
	} catch (Exception e) {
	    factory.reportMessage("Exception caught while parsing "+url+
				  e.toString(), false);
	}
	return factory.parsingEnded(node);
    }

    /**
     * Set the default category open image ID.
     *
     * param String ID of catagory open image
     */
    public void setCategoryOpenImageID(String imageID) {
	if (imageID == null) {
	    return;
	}
	try {
	    categoryOpenImageID = ID.create(imageID, getHelpSet());
	} catch (BadIDException bex) {
	}
    }

    /**
     * Returns the default category open image ID.
     *
     * return ID the ID for the category open image, if the category open image
     *        is null return the category closed image if it is defined,
     *        otherwise return null
     */
    public ID getCategoryOpenImageID() {
	if (categoryOpenImageID == null) {
	    // if categoryOpenImageID is null return the categoryClosedImageID
	    // it might be null but that is ok.
	    return categoryClosedImageID;
	}
	return categoryOpenImageID;
    }

    /**
     * Set the default category closed image ID.
     *
     * param String ID of catagory closed image
     */
    public void setCategoryClosedImageID(String imageID) {
	if (imageID == null) {
	    return;
	}
	try {
	    categoryClosedImageID = ID.create(imageID, getHelpSet());
	} catch (BadIDException bex) {
	}
    }

    /**
     * Returns the default category closed image ID.
     *
     * return ID the ID for the category closed image or null if not defined
     */
    public ID getCategoryClosedImageID() {
	return categoryClosedImageID;
    }

    /**
     * Set the topic image ID
     *
     * param String ID for the topic image
     */
    public void setTopicImageID(String imageID) {
	if (imageID == null) {
	    topicImageID = null;
	    return;
	}
	try {
	    topicImageID = ID.create(imageID, getHelpSet());
	} catch (BadIDException bex) {
	}
    }

    /**
     * Returns the topic image ID.
     *
     * return String the ID for the topic image or null if not defined
     */
    public ID getTopicImageID() {
	return topicImageID;
    }

    /**
     * A default TreeItemFactory that can be used to parse TOC items as used
     * by this navigator.
     */
    public static class DefaultTOCFactory implements TreeItemFactory {
	private Vector messages = new Vector();
	private URL source;
	private boolean validParse = true;

	/**
	 * Starts parsing.
	 */
	public void parsingStarted(URL source) {
	    if (source == null) {
		throw new NullPointerException("source");
	    }
	    this.source = source;
	}

	/**
	 * Processes a DOCTYPE.
	 */
	public void processDOCTYPE(String root, 
				   String publicID,
				   String systemID) {
	    if (publicID == null ||
		(publicID.compareTo(publicIDString) != 0 &&
		 publicID.compareTo(publicIDString_V2) != 0)) {
		reportMessage(HelpUtilities.getText("toc.wrongPublicID", publicID), false);
	    }
	}

	/**
	 * Finds a PI--ignore it.
	 */
	public void processPI(HelpSet hs, String target, String data) {
	}

	/**
	 * Create an TOCItem with the given data.
	 * @param tagName The TOC type to create. 
	 * Valid types are "tocitem". Null or invalid types will throw an
	 * IllegalArgumentException
	 * @param atts Attributes of the Item. Valid attributes are "target",
	 * "image", and "text". A null atts is valid and means no attributes
	 * @param hs HelpSet this item was created under. 
	 * @param locale Locale of this item. A null locale is valid. 
	 * @returns A fully constructed TreeItem.
	 * @throws IllegalArgumentExcetpion if tagname is null or invalid.
	 */
	public TreeItem createItem(String tagName,
				   Hashtable atts,
				   HelpSet hs,
				   Locale locale) {
	    if (tagName == null || !tagName.equals("tocitem")) {
		throw new IllegalArgumentException("tagName");
	    }
	    TOCItem item = null;
	    String id = null;
	    String imageID = null;
	    String text = null;
            String mergeType = null;
	    String expand = null;
	    String presentation = null;
	    String presentationName = null;

	    if (atts != null) {
		id = (String) atts.get("target");
		imageID = (String) atts.get("image");
		text = (String) atts.get("text");
                mergeType = (String) atts.get("mergetype");
		expand = (String) atts.get("expand");
		presentation = (String) atts.get("presentationtype");
		presentationName = (String) atts.get("presentationname");
	    }

	    Map.ID mapID = null;
	    Map.ID imageMapID = null;
	    try {
		mapID = ID.create(id, hs);
	    } catch (BadIDException bex1) {
	    }
	    try {
		imageMapID = ID.create(imageID, hs);
	    } catch (BadIDException bex2) {
	    }
	    item = new TOCItem(mapID, imageMapID, hs, locale);
	    if (text != null) {
		item.setName(text);
	    }

            if (mergeType != null){
                item.setMergeType(mergeType);
            }

	    if (expand != null) {
		if (expand.equals("true")) {
		    item.setExpansionType(TreeItem.EXPAND);
		} else if (expand.equals("false")) {
		    item.setExpansionType(TreeItem.COLLAPSE);
		}
	    }
		    
            if (presentation != null){
                item.setPresentation(presentation);
            }

            if (presentationName != null){
                item.setPresentationName(presentationName);
            }
	    return item;
	}
	
	/**
	 * Creates a default TOCItem.
	 */
	public TreeItem createItem() {
	    return new TOCItem();
	}

	/**
	 * Reports an error message.
	 */
	public void reportMessage(String msg, boolean validParse) {
	    messages.addElement(msg);
	    this.validParse = this.validParse && validParse;
	}

	/**
	 * Lists all the error messages.
	 */
	public Enumeration listMessages() {
	    return messages.elements();
	}

	/**
	 * Ends parsing.  Last chance to do something
	 * to the node.
	 * @param node The DefaultMutableTreeNode that has been built during the
	 * the parsing. If <tt>node</tt> is null or there were parsing errors a null 
	 * is returned.
	 * @returns A valid DefaultMutableTreeNode if the parsing succeded or null
	 * if it failed.
	 */
	public DefaultMutableTreeNode parsingEnded(DefaultMutableTreeNode node) {
	    DefaultMutableTreeNode back = node;
	    if (! validParse) {
		// A parse with problems...
		back = null;
		System.err.println("Parsing failed for "+source);
		for (Enumeration e = messages.elements();
		     e.hasMoreElements();) {
		    String msg = (String) e.nextElement();
		    // need to think about this one...
		    System.err.println(msg);
		}
	    }
	    return back;
	}
    }


    /**
     * Inner class for parsing a TOC stream.
     *
     * WARNING!! This class is an interim solution until JavaHelp moves to a
     * real XML parser.  This is not a public class.  Clients should only use
     * the parse method in the enclosing class.
     */
    private static class TOCParser implements ParserListener {
	private HelpSet currentParseHS;	// HelpSet we are parsing into
	private Stack nodeStack;	// to track the parsing
	private Stack itemStack;
	private boolean startedtoc;
	private Stack tagStack;
	private Locale defaultLocale;
	private Locale lastLocale;
	private TreeItemFactory factory;
	private TOCView tocView;


	/**
	 * Creates a TOC Parser using a factory instance to create the item nodes.
	 *
	 * @param factory The ItemTreeFactory instance to use when a node
	 * has been recognized.
	 */

	TOCParser(TreeItemFactory factory, TOCView tocView) {
	    this.factory = factory;
	    this.tocView = tocView;
	}

	/**
	 * Parses a reader into a DefaultMutableTreeNode.
	 * Only one of these at a time.
	 */
	synchronized DefaultMutableTreeNode parse(Reader src,
						  HelpSet context,
						  Locale locale)
	    throws IOException 
	{
	    nodeStack = new Stack();
	    tagStack = new Stack();
	    itemStack = new Stack();

	    if (locale == null) {
		defaultLocale = Locale.getDefault();
	    } else {
		defaultLocale = locale;
	    }
	    lastLocale = defaultLocale;
	    
	    DefaultMutableTreeNode node = new DefaultMutableTreeNode();
	    nodeStack.push(node);
	    
	    currentParseHS = context;
	    
	    Parser parser = new Parser(src); // the XML parser instance
	    parser.addParserListener(this);
	    parser.parse();
	    return node;
	}

	/**
	 *  A Tag was parsed.
	 */
	public void tagFound(ParserEvent e) {
	    Locale locale = null;
	    Tag tag = e.getTag();
	    debug("TagFound: "+tag.name);
	    TagProperties attr = tag.atts;

	    if (attr != null) {
		String lang = attr.getProperty("xml:lang");
		locale = HelpUtilities.localeFromLang(lang);
	    }
	    if (locale == null) {
		locale = lastLocale;
	    }

	    if (tag.name.equals("tocitem")) {
		if (!startedtoc) {
		    factory.reportMessage(HelpUtilities.getText("toc.invalidTOCFormat"), false);
		}
		if (tag.isEnd && !tag.isEmpty) {
		    nodeStack.pop();
		    itemStack.pop();
		    removeTag(tag);
		    return;
		}


		TOCItem item;
		try {
		    Hashtable t = null;
		    if (attr != null) {
			t = attr.getHashtable();
		    }
		    item = (TOCItem) factory.createItem("tocitem",
							t,
							currentParseHS,
							locale);
		} catch (Exception ex) {
		    if (warningOfFailures) {
			String id = null;
			String imageID = null;
			if (attr != null) {
			    id = attr.getProperty("target");
			    imageID = attr.getProperty("image");
			}
			System.err.println("Failure in IndexItem Creation; ");
			System.err.println("  id: "+id);
			System.err.println("  hs: "+currentParseHS);
		    }
		    item = (TOCItem) factory.createItem();
		}

		if (!itemStack.empty()) {
		    TOCItem parent = (TOCItem)itemStack.peek();
		    if (item.getExpansionType() == TreeItem.DEFAULT_EXPANSION &&
			parent != null && 
			parent.getExpansionType() != TreeItem.DEFAULT_EXPANSION) {
			item.setExpansionType(parent.getExpansionType());
		    }
		}
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
		DefaultMutableTreeNode parent =
		    (DefaultMutableTreeNode) nodeStack.peek();
		parent.add(node);
		if (! tag.isEmpty) {
		    itemStack.push(item);
		    nodeStack.push(node);
		    addTag(tag, locale);
		}
	    } else if (tag.name.equals("toc")) {
		debug("attr: "+attr);
		if (!tag.isEnd) {
		    if (attr != null) {
		        String version = attr.getProperty("version");
			if (version != null && 
			    (version.compareTo("1.0") != 0 &&
			     version.compareTo("2.0") != 0)) {
			    factory.reportMessage(HelpUtilities.getText("toc.unknownVersion",version), false);
			}
			if (tocView != null) {
			    tocView.setCategoryOpenImageID(attr.getProperty("categoryopenimage"));
			    tocView.setCategoryClosedImageID(attr.getProperty("categoryclosedimage"));
			    tocView.setTopicImageID(attr.getProperty("topicimage"));
			}
		    }
		    if (startedtoc) {
			factory.reportMessage(HelpUtilities.getText("toc.invalidTOCFormat"), false);
		    }
		    startedtoc = true;
		    addTag(tag, locale);
		} else {
		    if (startedtoc) {
			startedtoc = false;
		    }
		    removeTag(tag);
		}
		return;
	    }
	}

	/**
	 *  A PI was parsed.  This method is not intended for general use.
	 */
	public void piFound(ParserEvent e) {
	    // ignore
	}

	/**
	 *  A DOCTYPE was parsed.  This method is not intended for general use.
	 */
	public void doctypeFound(ParserEvent e) {
	    // ignore for now
	}

	/**
	 * A continous block of text was parsed.
	 */
	public void textFound(ParserEvent e) {
	    debug("TextFound: "+e.getText().trim());

	    // Ignore text if there isn't a tag
	    if (tagStack.empty()) {
		return;
	    }
	    LangElement le = (LangElement) tagStack.peek();
	    Tag tag = (Tag) le.getTag();
	    if (tag.name.equals("tocitem")) {
		TOCItem item = (TOCItem) itemStack.peek();
		String oldName = item.getName();
		if (oldName == null) {
		    item.setName(e.getText().trim());
		} else {
		    item.setName(oldName.concat(e.getText()).trim());
		}
	    } 
	}

	// The remaing events from Parser are ignored
	public void commentFound(ParserEvent e) {}

	public void errorFound(ParserEvent e){
	    factory.reportMessage(e.getText(), false);
	}

	/**
	 * Tracks tags and their locale attributes.
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
	 * Removes a tag from the tagStack. The tagStack is
	 * used to track tags and locales.
	 */
	protected void removeTag(Tag tag) {
	    LangElement el;
	    String name = tag.name;
	    Locale newLocale = null;

	    for (;;) {
		if (tagStack.empty()) 
		    break;
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

    }

    /**
     * Debugging code
     */
    private static final boolean debug = false;
    private static void debug(String msg) {
	if (debug) {
	    System.err.println("TOCView: "+msg);
	}
    }


}
