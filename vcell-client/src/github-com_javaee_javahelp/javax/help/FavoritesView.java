/*
 * @(#)FavoritesView.java	1.6 06/10/30
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
import java.net.*;
import java.io.*;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;
import java.util.Stack;
import java.util.Enumeration;
import javax.help.Map.ID;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import com.sun.java.help.impl.*;


/**
 * Navigational View information for the Favorites
 *
 * @author Richard Gregor
 * @version   1.2     08/29/01
 */
public class FavoritesView extends NavigatorView{
    /**
     * PublicID (known to this XML processor) to the DTD for version 2.0 of the Index
     */
    public static final String publicIDString =
    "-//Sun Microsystems Inc.//DTD JavaHelp Favorites Version 2.0//EN";
    
    // turn this on if you want messages on failures
    private static boolean warningOfFailures = false;    
    /**
     * The HelpSet
     */
    private HelpSet hs;
    /**
     * Determines wheter save action of favorites file is enabled or not
     */
    private boolean enabledSave = true;

    /**
     * Construct an FavoritesView with some given data.  Locale defaults to that
     * of the HelpSet.
     *
     * @param hs The HelpSet that provides context information
     * @param name The name of the View
     * @param label The label (to show the user) of the View
     * @param params A hashtable providing different key/values for this type.
     * A null for params is valid.
     */
    public FavoritesView(HelpSet hs,
			 String name,
			 String label,
			 Hashtable params) {
        super(hs, name, label, hs.getLocale(), params);        
    }
    
    /**
     * Constructs an FavoritesView with some given data.
     *
     * @param hs The HelpSet that provides context information
     * @param name The name of the View
     * @param label The label (to show the user) of the View
     * @param locale The default locale to interpret the data in this TOC. A
     * null for locale will be interpreted as the defaultLocale.
     * @param params A Hashtable providing different key/values for this type
     * A null for params is valid.
     */
    public FavoritesView(HelpSet hs,
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
        return new JHelpFavoritesNavigator(this, model);
    }
    
    /**
     * Get the Index navigators mergeType. Overrides getMergeType in NavigatorView
     */
    public String getMergeType() {
	String mergeType = super.getMergeType();
	if (mergeType == null) {
	    return "javax.help.NoMerge";
	}
	return mergeType;
    }

    /**
     * Gets a DefaultMutableTreeNode representing the
     * information in this view instance.
     *
     * The default implementation parses the data in the URL, but a subclass may
     * override this method and provide a different implemenation. For example,
     * it may create the tree programatically.
     */
    public FavoritesNode getDataAsTree() {
        HelpSet hs = getHelpSet();
        debug("helpSet in "+this+hs.toString());

	return (FavoritesNode)parse(hs, hs.getLocale(), 
				    new DefaultFavoritesFactory());
    }
    
    /**
     * Public method for parsing the Favorites in a URL.
     * It returns a DefaultMutableTreeNode and its children
     * that correspond to the favoritesItems in the Favorites.  The factory is invoked to create
     * the TreeItems that are included in the DefaultMutableTreeNode as user
     * data.
     *
     * @param url Location of the Favorites. If null, causes null value to be returned.
     * @param hs The HelpSet context for this Favorites. Null hs is ignored.
     * @param locale The default locale to interpret the data in this Favorites. Null
     * locale is treated as the default locale.
     * @param factory A factory instance that is used to create the FavoritesItems
     * @return a TreeNode that represents the Favorites. Returns null if parsing errors
     * were encountered.
     */
    public FavoritesNode parse(HelpSet hs, Locale locale, TreeItemFactory factory) {
        Reader src;
        DefaultMutableTreeNode node = null;
        URL url= null;
        try {            
            String user_dir = System.getProperty("user.home");
            File file = new File(user_dir+File.separator+".JavaHelp"+File.separator+"Favorites.xml");            
            if(!file.exists())
                return new FavoritesNode(new FavoritesItem("Favorites"));
            try{
                url = file.toURL();
            }catch(MalformedURLException e){
                System.err.println(e);
            }
            
            URLConnection uc = url.openConnection();
            src = XmlReader.createReader(uc);
            factory.parsingStarted(url);
            node = (new FavoritesParser(factory)).parse(src, hs, locale);
            src.close();
        } catch (Exception e) {
            factory.reportMessage("Exception caught while parsing "+url+
            e.toString(),
            false);
        }
        return (FavoritesNode)factory.parsingEnded(node);
    }
    

    /**
     * Saves favorites file
     *
     * @param node The FavoritesNode
     */
    
    public void saveFavorites(FavoritesNode node){
        if(!enabledSave)
            return;
        try{
            FileOutputStream out;
            String user_dir = System.getProperty("user.home");
            File file = new File(user_dir+File.separator+".JavaHelp");
            file.mkdirs();                            
            String userFile = file.getPath()+File.separator+"Favorites.xml";
            debug("new file:"+userFile);
            node.export(out = new FileOutputStream(userFile));
            out.close();
        }catch(SecurityException se){
            enabledSave = false;
            se.printStackTrace();
        }catch(Exception excp){
            excp.printStackTrace();
        }
    }
    /**
     * A default TreeItemFactory that can be used to parse Favorites items as used
     * by this navigator.
     */
    public static class DefaultFavoritesFactory implements TreeItemFactory{
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
            !publicID.equals(publicIDString)) {
                reportMessage(HelpUtilities.getText("favorites.invalidFavoritesFormat", publicID), false);
            }
        }
        
        /**
         * We have found a PI; ignore it
         */
        public void processPI(HelpSet hs, String target, String data) {
        }
        
        /**
         * Creates an FavoritesItem with the given data.
         * @param tagName The favorites type to create.
         * Valid types are "favoriteitem". Null or invalid types throw an
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
            if (tagName == null || !tagName.equals("favoriteitem")) {
                throw new IllegalArgumentException("tagName");
            }
            FavoritesItem item = null;
            String target = null;
            String name = null;
            String mergeType = null;
            String url = null;
            String hstitle = null;
            
            if (atts != null ) {
                target = (String) atts.get("target");
                debug("target:"+target);
                name = (String) atts.get("text");                
                url = (String) atts.get("url");
                hstitle = (String) atts.get("hstitle");            
                item = new FavoritesItem(name,target,url,hstitle,locale);       
                if((item.getTarget() == null) &&(item.getURLSpec() == null))
                    item.setAsFolder();
            }else
                item = new FavoritesItem();
            
                       
       
        return item;
    }
    
    /**
     * Creates a default FavoritesItem.
     */
    public TreeItem createItem() {
        debug("empty item created");
        return new FavoritesItem();
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
 * Inner class for parsing an Favorites stream.
 *
 * WARNING!! This class is an interim solution until JH moves to a
 * real XML parser.  This is not a public class.  Clients should only use
 * the parse method in the enclosing class.
 */

    private static class FavoritesParser implements ParserListener {
	private HelpSet currentParseHS; // HelpSet we are parsing into
	private Stack nodeStack;	// to track the parsing
	private Stack itemStack;
	private Stack tagStack;
	private Locale defaultLocale;
	private Locale lastLocale;
	private boolean startedfavorites;
	private TreeItemFactory factory;
    
	/**
	 * Creates an Favorites Parser using a factory instance to create the item nodes.
	 *
	 * @param factory The ItemTreeFactory instance to use when a node
	 * has been recognized.
	 */
    
	FavoritesParser(TreeItemFactory factory) {
	    this.factory = factory;
	}
    
	/**
	 * Parse a reader into a DefaultMutableTreeNode.
	 * Only one of these at a time.
	 */
	synchronized FavoritesNode parse(Reader src,
					 HelpSet context,
					 Locale locale)
	    throws IOException {
		nodeStack = new Stack();
		itemStack = new Stack();
		tagStack = new Stack();
        
		if (locale == null) {
		    defaultLocale = Locale.getDefault();
		} else {
		    defaultLocale = locale;
		}
		lastLocale = defaultLocale;
        
		FavoritesNode node = new FavoritesNode(new FavoritesItem("Favorites"));
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
        
	    if (tag.name.equals("favoriteitem")) {
		if (!startedfavorites) {
		    factory.reportMessage(HelpUtilities.getText("favorites.invalidFavoritesFormat"),
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
		    item = factory.createItem("favoriteitem",
					      t,
					      currentParseHS,
					      locale);
		} catch (Exception ex) {
		    if (warningOfFailures) {
			String id=null;
			if (attr != null) {
			    id = attr.getProperty("target");
			}
			System.err.println("Failure in FavoritesItem Creation; ");
			System.err.println("  id: "+id);
			System.err.println("  hs: "+currentParseHS);
			ex.printStackTrace();
		    }
		    debug("empty item !");
		    item = factory.createItem();
		}
            
		FavoritesNode node = new FavoritesNode((FavoritesItem)item);
		FavoritesNode parent =
		    (FavoritesNode) nodeStack.peek();
		parent.add(node);
		if (! tag.isEmpty) {
		    itemStack.push(item);
		    nodeStack.push(node);
		    addTag(tag, locale);
		}
	    }  else if (tag.name.equals("favorites")) {
		if (!tag.isEnd) {
		    if (attr != null) {
			String version = attr.getProperty("version");
			if (version != null &&
			    version.compareTo("2.0") != 0) {
			    factory.reportMessage(HelpUtilities.getText("favorites.unknownVersion", version), false);
			}
		    }
		    if (startedfavorites) {
			factory.reportMessage(HelpUtilities.getText("favorites.invalidFavoritesFormat"),
					      false);
		    }
		    startedfavorites = true;
		    addTag(tag, locale);
		} else {
		    if (startedfavorites) {
			startedfavorites = false;
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
	    if (tag.name.equals("favoriteitem")) {
		FavoritesItem item = (FavoritesItem) itemStack.peek();
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
	    System.err.println("FavoritesView: "+msg);
	}
    }
}
