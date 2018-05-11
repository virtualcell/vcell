/*
 * @(#)IndexView.java	1.32 06/10/30
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
 * Navigational View information for an Index
 *
 * @author Eduardo Pelegri-Llopart
 * @version	1.17	03/16/99
 */

public class IndexView extends NavigatorView {

    /**
     * PublicID (known to this XML processor) to the DTD for version 1.0 of the Index
     */
    public static final String publicIDString =
        "-//Sun Microsystems Inc.//DTD JavaHelp Index Version 1.0//EN";

    /**
     * PublicID (known to this XML processor) to the DTD for version 2.0 of the Index
     */
    public static final String publicIDString_V2 =
        "-//Sun Microsystems Inc.//DTD JavaHelp Index Version 2.0//EN";

    /**
     * Construct an IndexView with some given data.  Locale defaults to that
     * of the HelpSet.
     *
     * @param hs The HelpSet that provides context information
     * @param name The name of the View
     * @param label The label (to show the user) of the View
     * @param params A hashtable providing different key/values for this type.
     * A null for params is valid.
     */
    public IndexView(HelpSet hs,
		     String name,
		     String label,
		     Hashtable params) {
	super(hs, name, label, hs.getLocale(), params);
    }

    /**
     * Constructs an IndexView with some given data. 
     *
     * @param hs The HelpSet that provides context information
     * @param name The name of the View
     * @param label The label (to show the user) of the View
     * @param locale The default locale to interpret the data in this TOC. A
     * null for locale will be interpreted as the defaultLocale.
     * @param params A Hashtable providing different key/values for this type
     * A null for params is valid.
     */
    public IndexView(HelpSet hs,
		     String name,
		     String label,
		     Locale locale,
		     Hashtable params) {
	super(hs, name, label, locale, params);
    }

    /**
     * create a navigator for a given model.
     * @param model The HelpModel to create this navigator with. A null model
     * is valid.
     * @return The appropriate Component for this view.
     */
    public Component createNavigator(HelpModel model) {
	return new JHelpIndexNavigator(this, model);
    }

    
    /**
     * Get the Index navigators mergeType. Overrides getMergeType in NavigatorView
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
     * Gets a DefaultMutableTreeNode representing the 
     * information in this view instance.
     * 
     * The default implementation parses the data in the URL, but a subclass may
     * override this method and provide a different implemenation. For example,
     * it may create the tree programatically.
     */
    public DefaultMutableTreeNode getDataAsTree() {
	HelpSet hs = getHelpSet();
        debug("helpSet in "+this+hs.toString());
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
	    throw new Error("Trouble getting URL to Index data; "+ex);
	}
        debug("url,hs: "+url.toString()+";"+hs.toString());
	return parse(url, hs, hs.getLocale(), new DefaultIndexFactory());
    }

    /**
     * Public method for parsing an Index in a URL.
     * It returns a DefaultMutableTreeNode and its children
     * that correspond to the indexitems in the Index.  The factory is invoked to create
     * the TreeItems that are included in the DefaultMutableTreeNode as user
     * data.
     *
     * @param url Location of the Index. If null, causes null value to be returned.
     * @param hs The HelpSet context for this Index. Null hs is ignored.
     * @param locale The default locale to interpret the data in this Index. Null
     * locale is treated as the default locale.
     * @param factory A factory instance that is used to create the IndexItems
     * @return a TreeNode that represents the Index. Returns null if parsing errors 
     * were encountered.
     */
    public static DefaultMutableTreeNode parse(URL url,
					HelpSet hs,
					Locale locale,
					TreeItemFactory factory) {
	Reader src;
	DefaultMutableTreeNode node = null;
	try {
	    URLConnection uc = url.openConnection();
	    src = XmlReader.createReader(uc);
	    factory.parsingStarted(url);
	    node = (new IndexParser(factory)).parse(src, hs, locale);
	    src.close();
	} catch (Exception e) {
	    factory.reportMessage("Exception caught while parsing "+url+
				  e.toString(),
				  false);
	}
        return factory.parsingEnded(node);
    }

    /**
     * A default TreeItemFactory that can be used to parse TOC items as used
     * by this navigator.
     */
    public static class DefaultIndexFactory implements TreeItemFactory {
	private Vector messages = new Vector();
	private URL source;
	private boolean validParse = true;

	/**
	 * Parsing has started
	 */
	public void parsingStarted(URL source) {
	    if (source == null) {
		throw new NullPointerException("source");
	    }
	    this.source = source;
	}

	/**
	 * Process a DOCTYPE
	 */
	public void processDOCTYPE(String root, 
				   String publicID,
				   String systemID) {
	    if (publicID == null ||
		(publicID.compareTo(publicIDString) != 0 &&
		 publicID.compareTo(publicIDString_V2) != 0)) {
		reportMessage(HelpUtilities.getText("index.invalidIndexFormat", publicID), false);
	    }
	}

	/**
	 * We have found a PI; ignore it
	 */
	public void processPI(HelpSet hs, String target, String data) {
	}

	/**
	 * Creates an IndexItem with the given data.
	 * @param tagName The index type to create. 
	 * Valid types are "indexitem". Null or invalid types throw an
	 * IllegalArgumentException.
	 * @param atts Attributes of the Item. Valid attributes are "target"
	 * and "text". A null <tt>atts</tt> is valid and means no attributes.
	 * @param hs The HelpSet this item was created under. 
	 * @param locale Locale of this item. A null locale is valid. 
	 * @returns A fully constructed TreeItem.
	 * @throws IllegalArgumentException if tagname is null or invalid.
	 */
	public TreeItem createItem(String tagName,
				   Hashtable atts,
				   HelpSet hs,
				   Locale locale) {
	    if (tagName == null || !tagName.equals("indexitem")) {
		throw new IllegalArgumentException("tagName");
	    }
	    IndexItem item = null;
	    String id = null;
	    String text = null;
            String mergeType = null;
	    String expand = null;
	    String presentation = null;
	    String presentationName = null;

	    if (atts != null ) {
		id = (String) atts.get("target");
		text = (String) atts.get("text");
                mergeType = (String) atts.get("mergetype");
		expand = (String) atts.get("expand");
		presentation = (String) atts.get("presentationtype");
		presentationName = (String) atts.get("presentationname");		
	    }
	    
	    try { 
		item = new IndexItem(ID.create(id, hs),
				     hs,
				     locale);
	    } catch (BadIDException ex) {
		item = new IndexItem();
	    }

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
	 * Creates a default IndexItem.
	 */
	public TreeItem createItem() {
	    return new IndexItem();
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
	 * Parsing has ended.  The last chance to do something
	 * to the node
	 * @param node The DefaultMutableTreeNode that has been built during the
	 * the parsing. If <tt>node</tt> is null or there were parsing errors a null 
	 * is returned.
	 * @returns A valid DefaultMutableTreeNode if the parsing succeded or null
	 * if it failed
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
     * Inner class for parsing an Index stream.
     *
     * WARNING!! This class is an interim solution until JH moves to a
     * real XML parser.  This is not a public class.  Clients should only use
     * the parse method in the enclosing class.
     */

    private static class IndexParser implements ParserListener {
	private HelpSet currentParseHS; // HelpSet we are parsing into
	private Stack nodeStack;	// to track the parsing
	private Stack itemStack;
	private Stack tagStack;
	private Locale defaultLocale;
	private Locale lastLocale;
	private boolean startedindex;
	private TreeItemFactory factory;

	/**
	 * Creates an Index Parser using a factory instance to create the item nodes.
	 *
	 * @param factory The ItemTreeFactory instance to use when a node
	 * has been recognized.
	 */

	IndexParser(TreeItemFactory factory) {
	    this.factory = factory;
	}

	/**
	 * Parse a reader into a DefaultMutableTreeNode.
	 * Only one of these at a time.
	 */
	synchronized DefaultMutableTreeNode parse(Reader src,
					   HelpSet context,
					   Locale locale)
	    throws IOException 
	{
	    nodeStack = new Stack();
	    itemStack = new Stack();
	    tagStack = new Stack();

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
	 *  A Tag was parsed.  This method is not intended to be of general use.
	 */
	public void tagFound(ParserEvent e) {
	    Locale locale = null;
	    Tag tag = e.getTag();
	    TagProperties attr = tag.atts;

	    if (attr != null) {
		String lang = attr.getProperty("xml:lang");
		locale = HelpUtilities.localeFromLang(lang);
	    }
	    if (locale == null) {
		locale = lastLocale;
	    }

	    if (tag.name.equals("indexitem")) {
		if (!startedindex) {
		    factory.reportMessage(HelpUtilities.getText("index.invalidIndexFormat"),
					  false);
		}
		if (tag.isEnd && ! tag.isEmpty) {
		    nodeStack.pop();
		    itemStack.pop();
		    removeTag(tag);
		    return;
		}

		TreeItem item = null;
		try {
		    Hashtable t = null;
		    if (attr != null) {
			t = attr.getHashtable();
		    }
		    item = factory.createItem("indexitem",
					      t,
					      currentParseHS,
					      locale);
		} catch (Exception ex) {
		    if (warningOfFailures) {
			String id=null;
			if (attr != null) {
			    id = attr.getProperty("target");
			}
			System.err.println("Failure in IndexItem Creation; ");
			System.err.println("  id: "+id);
			System.err.println("  hs: "+currentParseHS);
		    }
		    item = factory.createItem();
		}

		if (!itemStack.empty()) {
		    IndexItem parent = (IndexItem)itemStack.peek();
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
	    }  else if (tag.name.equals("index")) {
		if (!tag.isEnd) {
		    if (attr != null) {
		        String version = attr.getProperty("version");
			if (version != null && 
			    (version.compareTo("1.0") != 0 &&
			     version.compareTo("2.0") != 0 )) {
			    factory.reportMessage(HelpUtilities.getText("index.unknownVersion", version), false);
			}
		    }
		    if (startedindex) {
			factory.reportMessage(HelpUtilities.getText("index.invalidIndexFormat"),
					      false);
		    }
		    startedindex = true;
		    addTag(tag, locale);
		} else {
		    if (startedindex) {
			startedindex = false;
		    }
		    removeTag(tag);
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
	    // ignore for now
	    factory.processDOCTYPE(e.getRoot(), e.getPublicId(), e.getSystemId());
	}

	/**
	 * A continous block of text was parsed.
	 */
	public void textFound(ParserEvent e) {
	    if (tagStack.empty()) {
		return;		// ignore
	    }
	    LangElement le = (LangElement) tagStack.peek();
	    Tag tag = (Tag) le.getTag();
	    if (tag.name.equals("indexitem")) {
		IndexItem item = (IndexItem) itemStack.peek();
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
	 * Keeps track of tags and their locale attributes.
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
	 * used to keep track of tags and locales.
	 */
	protected void removeTag(Tag tag) {
	    LangElement el;
	    String name = tag.name;
	    Locale newLocale=null;

	    for (;;) {
		if (tagStack.empty()) 
		    break;
		el = (LangElement) tagStack.pop();
		if (! el.getTag().name.equals(name)) {
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
  	    System.err.println("IndexView: "+msg);
	}
    }

}
