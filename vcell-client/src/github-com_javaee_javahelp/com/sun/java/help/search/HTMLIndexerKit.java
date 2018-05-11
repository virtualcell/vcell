/*
 * @(#)HTMLIndexerKit.java	1.26 06/10/30
 * 
 * Copyright (c) 2007 Sun Microsystems, Inc.  All Rights Reserved.
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

/*
 * @(#) HTMLIndexerKit.java 1.26 - last change made 10/30/06
 */

package com.sun.java.help.search;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.util.*;
import javax.help.search.IndexerKit;
import javax.help.search.IndexBuilder;
import javax.help.search.ConfigFile;

/**
 * This is the default implementation of html parsing.
 * It can be subclasses to use a different parser and
 * receiver.
 *
 * @author  Roger D. Brinkley
 * @version %I	%G
 */
public class HTMLIndexerKit extends DefaultIndexerKit{
   
    /**
     * Constructs an HTMLEditorKit
     */
    public HTMLIndexerKit() {

    }

    /**
     * Create a copy of the editor kit.  This
     * allows an implementation to serve as a prototype
     * for others, so that they can be quickly created.
     *
     * @return the copy
     */
    public Object clone() {
	return new HTMLIndexerKit();
    }

    /**
     * Get the MIME type of the data that this
     * kit represents support for.  This kit supports
     * the type <code>text/html</code>.
     *
     * @return the type
     */
    public String getContentType() {
	return "text/html";
    }

    /**
     * Create and initialize a model from the given
     * stream which is expected to be in a format appropriate
     * for this kind of kit.  This is implemented to read
     * html 3.2 text.
     * 
     * @param in  The stream to read from
     * @param file The file name being parsed
     * @param ignoreCharset Ignore the CharacterSet when parsing
     * @param builder The IndexBuilder for the full text insertion.
     * @param config The indexer configuration information
     * @exception IOException on any I/O error
     */
    public void parse(Reader in, String file, boolean ignoreCharset,
		      IndexBuilder builder,
		      ConfigFile config) throws IOException{

	this.builder = builder;
	this.config = config;
	this.file = file;
	documentStarted = false;

	HTMLEditorKit.Parser p = getParser();
	if (p == null) {
	    throw new IOException("Can't load parser");
	}
	if (defaultCallback == null) {
	    defaultCallback = getParserCallback(this);
	}
	defaultCallback.initialize();
	try {
	    p.parse(in, defaultCallback, ignoreCharset);
	} catch (javax.swing.text.ChangedCharSetException e4) {
	    throw new com.sun.java.help.search.ChangedCharSetException
		(e4.getCharSetSpec(), 
		 e4.keyEqualsCharSet());
	}
		    
	try {
	    defaultCallback.flush();
	} catch (BadLocationException e3) {
	    throw new IOException("Can't flush parser");
	}

	try {
	    storeTitle(defaultCallback.getTitle());
	    endStoreDocument();
	} catch (Exception e2) {
	    throw new IOException("Can't store title");
	}

	this.builder = null;
	this.config = null;
    }

    /**
     * Fetch the parser to use for reading html streams.
     * This can be reimplemented to provide a different
     * parser.  The default implementation is loaded dynamically
     * to avoid the overhead of loading the default parser if
     * it's not used.  The default parser is the HotJava parser
     * using an html 3.2 dtd.
     */
    protected HTMLEditorKit.Parser getParser() {
	if (defaultParser == null) {
	    try {
                Class c = Class.forName("javax.swing.text.html.parser.ParserDelegator");
                defaultParser = (HTMLEditorKit.Parser) c.newInstance();
	    } catch (Throwable e) {
		e.printStackTrace();
	    }
	}
	return defaultParser;
    }

    /**
     * Fetch the parser callback to use to parse the
     * html.  This is implemented to return an instance of
     * HTMLEditorKit.HTMLParserCallback.  Subclasses can reimplement this
     * method to change how the document gets parsed
     */
    public HTMLParserCallback getParserCallback(IndexerKit kit) {
	return new HTMLParserCallback(kit);
    }
    // --- variables ------------------------------------------

    private static HTMLEditorKit.Parser defaultParser = null;
    private static HTMLParserCallback defaultCallback = null;

    private static char[] NEWLINE;

    static {
	NEWLINE = new char[1];
	NEWLINE[0] = '\n';
    }

    /**
     * An html reader to load an html document with an html
     * element structure.  This is a set of callbacks from
     * the parser, implemented to create a set index tokens
     */ 
    public class HTMLParserCallback extends HTMLEditorKit.ParserCallback {

	private IndexerKit kit;
	private String title;
	private String header;
	private boolean firstHeader;
	private int currentPos;
	private boolean receivedEndHTML;
	private boolean insertAfterImplied;
	private boolean inParagraph;
	private boolean impliedP;
	private boolean inPre;
	private boolean inTextArea;
	private boolean inTitle;
	private boolean lastWasNewline;
	private boolean emptyAnchor;
	private boolean inBody;
	private boolean foundInsertTag;
	private boolean inHead;
	private boolean inStyle;
	private boolean inOption;
	private boolean inFirstHeader;
	private boolean startTagType;
	private boolean preservesUnknownTags;
	Hashtable tagMap;
	int inBlock;	
	Stack tagStack;
	String defaultLang;
	String lastLang;

	// LangElement is a simple little internal class to keep
	// track of tags and the langs
	private class LangElement {

	    HTML.Tag tag;
	    String lang;

	    public LangElement (HTML.Tag tag, String lang) {
		this.tag = tag;
		this.lang = lang;
	    }

	    public HTML.Tag getTag() {
		return tag;
	    }

	    public String getLang() {
		return lang; 
	    }
	}
	    

        public HTMLParserCallback(IndexerKit kit) {
	    this.kit = kit;
	    tagMap = new Hashtable(57);
	    TagAction na = new TagAction();
	    TagAction ba = new BlockAction();
	    TagAction pa = new ParagraphAction();
	    TagAction ca = new CharacterAction();
	    TagAction sa = new SpecialAction();
	    TagAction fa = new FormAction();
	    TagAction ha = new HiddenAction();
	    TagAction conv = new ConvertAction();

	    // register handlers for the well known tags
	    tagMap.put(HTML.Tag.A, new AnchorAction());
	    tagMap.put(HTML.Tag.ADDRESS, ca);
	    tagMap.put(HTML.Tag.APPLET, ha);
	    tagMap.put(HTML.Tag.AREA, new AreaAction());
	    tagMap.put(HTML.Tag.B, ca);
	    tagMap.put(HTML.Tag.BASE, new BaseAction());
	    tagMap.put(HTML.Tag.BASEFONT, ca);
	    tagMap.put(HTML.Tag.BIG, ca);
	    tagMap.put(HTML.Tag.BLOCKQUOTE, ba);
	    tagMap.put(HTML.Tag.BODY, ba);
	    tagMap.put(HTML.Tag.BR, sa);
	    tagMap.put(HTML.Tag.CAPTION, ba);
	    tagMap.put(HTML.Tag.CENTER, ba);
	    tagMap.put(HTML.Tag.CITE, ca);
	    tagMap.put(HTML.Tag.CODE, ca);
	    tagMap.put(HTML.Tag.DD, ba);
	    tagMap.put(HTML.Tag.DFN, ca);
	    tagMap.put(HTML.Tag.DIR, ba);
	    tagMap.put(HTML.Tag.DIV, ba);
	    tagMap.put(HTML.Tag.DL, ba);
	    tagMap.put(HTML.Tag.DT, pa);
	    tagMap.put(HTML.Tag.EM, ca);
	    tagMap.put(HTML.Tag.FONT, conv);
	    tagMap.put(HTML.Tag.FORM, new FormTagAction());
	    tagMap.put(HTML.Tag.FRAME, sa);
	    tagMap.put(HTML.Tag.FRAMESET, ba);
	    tagMap.put(HTML.Tag.H1, pa);
	    tagMap.put(HTML.Tag.H2, pa);
	    tagMap.put(HTML.Tag.H3, pa);
	    tagMap.put(HTML.Tag.H4, pa);
	    tagMap.put(HTML.Tag.H5, pa);
	    tagMap.put(HTML.Tag.H6, pa);
	    tagMap.put(HTML.Tag.HEAD, new HeadAction());
	    tagMap.put(HTML.Tag.HR, sa);
	    tagMap.put(HTML.Tag.HTML, ba);
	    tagMap.put(HTML.Tag.I, conv);
	    tagMap.put(HTML.Tag.IMG, sa);
	    tagMap.put(HTML.Tag.INPUT, fa);
	    tagMap.put(HTML.Tag.ISINDEX, new IsindexAction());
	    tagMap.put(HTML.Tag.KBD, ca);
	    tagMap.put(HTML.Tag.LI, ba);
	    tagMap.put(HTML.Tag.LINK, new LinkAction());
	    tagMap.put(HTML.Tag.MAP, new MapAction());
	    tagMap.put(HTML.Tag.MENU, ba);
	    tagMap.put(HTML.Tag.META, new MetaAction());
	    // NOBR isn't publicly available so workaround it.
	    HTML.Tag tag = HTML.getTag("NOBR");
	    if (tag != null) {
		tagMap.put(tag, ca);
	    }
	    tagMap.put(HTML.Tag.NOFRAMES, ba);
	    tagMap.put(HTML.Tag.OBJECT, sa);
	    tagMap.put(HTML.Tag.OL, ba);
	    tagMap.put(HTML.Tag.OPTION, fa);
	    tagMap.put(HTML.Tag.P, pa);
	    tagMap.put(HTML.Tag.PARAM, new ObjectAction());
	    tagMap.put(HTML.Tag.PRE, new PreAction());
	    tagMap.put(HTML.Tag.SAMP, ca);
	    tagMap.put(HTML.Tag.SCRIPT, ha);
	    tagMap.put(HTML.Tag.SELECT, fa);
	    tagMap.put(HTML.Tag.SMALL, ca);
	    tagMap.put(HTML.Tag.STRIKE, conv);
	    tagMap.put(HTML.Tag.S, ca);
	    tagMap.put(HTML.Tag.STRONG, ca);
	    tagMap.put(HTML.Tag.STYLE, new StyleAction());
	    tagMap.put(HTML.Tag.SUB, conv);
	    tagMap.put(HTML.Tag.SUP, conv);
	    tagMap.put(HTML.Tag.TABLE, ba);
	    tagMap.put(HTML.Tag.TD, ba);
	    tagMap.put(HTML.Tag.TEXTAREA, fa);
	    tagMap.put(HTML.Tag.TH, ba);
	    tagMap.put(HTML.Tag.TITLE, new TitleAction());
	    tagMap.put(HTML.Tag.TR, ba);
	    tagMap.put(HTML.Tag.TT, ca);
	    tagMap.put(HTML.Tag.U, conv);
	    tagMap.put(HTML.Tag.UL, ba);
	    tagMap.put(HTML.Tag.VAR, ca);
	}

	public void initialize() {
	    title = null;
	    header = null;
	    firstHeader = true;
	    currentPos = 0;
	    receivedEndHTML = false;
	    insertAfterImplied = false;
	    inParagraph = false;
	    impliedP = false;
	    inPre = false;
	    inTitle = false;
	    inOption = false;
	    inFirstHeader = false;
	    startTagType = false;
	    emptyAnchor = false;
	    inBlock = 0;
	    tagStack = new Stack();
	    defaultLang = kit.getLocale().toString();
	    lastLang = defaultLang;
	    inTextArea = false;
	    lastWasNewline = false;
	    inBody = false;
	    foundInsertTag = true;
	    preservesUnknownTags = true;
	    inHead = false;
	    inStyle = false;
	}

	public String getTitle() {
	    if (title == null || title.length() < 1) {
		title = header;
		if (title == null || title.length() < 1) {
		    title = "No Title";
		}
	    }
	    return title;
	}

	// -- ParserCallback methods --------------------

	/**
	 * This is the last method called on the reader.  It allows
	 * any pending changes to be flushed into the document.  
	 * Since this is currently loading synchronously, the entire
	 * set of changes are pushed in at this point.
	 */
        public void flush() throws BadLocationException {
	    // Nothing needs to be done here
	}

	/**
	 * Called by the parser to indicate a block of text was
	 * encountered.
	 */
        public void handleText(char[] data, int pos) {
	    if (receivedEndHTML) {
		return;
	    }
	    if (inTextArea) {
		// don't do anything
	    } else if (inPre) {
		preContent(data);
	    } else if (inTitle) {
		titleContent(new String(data));
	    } else if (inOption) {
		// Nothing to do here
	    } else if (inStyle) {
		// don't do anything
	    } else if (inBlock > 0) {
		// Added test for first header to set the 
		// header content
		if (inFirstHeader) {
		    headerContent(new String(data));
		}
		if (!foundInsertTag && insertAfterImplied) {
		    // Assume content should be added.
		    foundInsertTag(false);
		    foundInsertTag = true;
		    inParagraph = impliedP = true;
		}
		if (data.length >= 1) {
		    addContent(data, 0, data.length);
		}
	    }
	}

	/**
	 * Callback from the parser.  Route to the appropriate
	 * handler for the tag. 
	 * This method differes from HTMLDcoument since there
	 * is not the possibility of doing a mid insert in the
	 * document and we don't really need to be concerned with
	 * the construction of the style sheet.
	 */
	public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            if (receivedEndHTML) {
                return;
            }
	    if (!inBody && t == HTML.Tag.BODY) {
		inBody = true;
	    }
	    TagAction action = (TagAction) tagMap.get(t);
	    if (action != null) {
		action.start(t, a);
	    }
	}

        public void handleComment(char[] data, int pos) {
            if (receivedEndHTML) {
		// don't do anything here
                return;
            }
	    if (inStyle) {
		// don't do anything here
	    } else if (preservesUnknownTags) {
		if (inBlock == 0) {
                    // Comment outside of body, will not be able to show it,
                    // but can add it as a property on the Document.
		    // In the indexer we don't care though so
		    // do nothing.
		    return;
		}
		SimpleAttributeSet sas = new SimpleAttributeSet();
		sas.addAttribute(HTML.Attribute.COMMENT, new String(data));
		addSpecialElement(HTML.Tag.COMMENT, sas);
		debug ("comment added currentPos=" + currentPos);
	    }
	}

	/**
	 * Callback from the parser.  Route to the appropriate
	 * handler for the tag.
	 * 
	 * Ignore the midInsert statements in HTMLDocument
	 */
	public void handleEndTag(HTML.Tag t, int pos) {
	    if (receivedEndHTML) {
		return;
	    }
            if (t == HTML.Tag.HTML) {
                receivedEndHTML = true;
            }
	    if (t == HTML.Tag.BODY) {
		inBody = false;
	    }
	    TagAction action = (TagAction) tagMap.get(t);
	    if (action != null) {
		action.end(t);
	    }
	}

	/**
	 * Callback from the parser.  Route to the appropriate
	 * handler for the tag.
	 *
	 * Ignore the midInsert and style statements in HTMLDocument
	 */
	public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
	    if (receivedEndHTML) {
		return;
	    }

	    TagAction action = (TagAction) tagMap.get(t);
	    if (action != null) {
		action.start(t, a);
		action.end(t);
	    } else if (preservesUnknownTags) {
		addSpecialElement(t, a);
	    }
	}

	// ---- tag handling support ------------------------------

	/**
	 * Register a handler for the given tag.  By default
	 * all of the well-known tags will have been registered.
	 * This can be used to change the handling of a particular
	 * tag or to add support for custom tags.
	 */
        protected void registerTag(HTML.Tag t, TagAction a) {
	    tagMap.put(t, a);
	}

	/**
	 * This is an action to be performed in response
	 * to parsing a tag.  This allows customization
	 * of how each tag is handled and avoids a large
	 * switch statement.
	 */
	public class TagAction {

	    /**
	     * Called when a start tag is seen for the
	     * type of tag this action was registered
	     * to.  The tag argument indicates the actual
	     * tag for those actions that are shared across
	     * many tags. 
	     * 
	     * Different from HTMLDocument it doesn't ignore
	     * the tag.
	     */
	    public void start(HTML.Tag t, MutableAttributeSet a) {
		String lang = (String) a.getAttribute(HTML.Attribute.LANG);
		if (lang == null) {
		    lang = lastLang;
		}
		addTag(t, lang);
	    }

	    /**
	     * Called when an end tag is seen for the
	     * type of tag this action was registered
	     * to.  The tag argument indicates the actual
	     * tag for those actions that are shared across
	     * many tags. 
	     * 
	     * Different from HTMLDocument it doesn't ignore
	     * the tag.
	     */
	    public void end(HTML.Tag t) {
		removeTag(t);
	    }

	}

	public class BlockAction extends TagAction {

	    public void start(HTML.Tag t, MutableAttributeSet attr) {
		blockOpen(t, attr);
		String lang = (String) attr.getAttribute(HTML.Attribute.LANG);
		if (lang == null) {
		    lang = lastLang;
		}
		addTag(t, lang);
	    }

	    public void end(HTML.Tag t) {
		blockClose(t);
		removeTag(t);
	    }
	}

        /**
         * Action used for the actual element form tag. This is named such
         * as there was already a public class named FormAction. 
	 * 
	 * Unlike HTMLDocument there is no need to setup/destroy
	 * a ButtonGroup
         */
        private class FormTagAction extends BlockAction {
            public void start(HTML.Tag t, MutableAttributeSet attr) {
                super.start(t, attr);
		// do nothing else
            }

	    public void end(HTML.Tag t) {
                super.end(t);
		// do nothing else
            }
        }


	public class ParagraphAction extends BlockAction {

	    public void start(HTML.Tag t, MutableAttributeSet a) {
		// trap the first header as a substitute title if needed
		if (firstHeader && 
		    ((t == HTML.Tag.H1) || (t == HTML.Tag.H2) ||
		     (t == HTML.Tag.H3) || (t == HTML.Tag.H4) ||
		     (t == HTML.Tag.H5) || (t == HTML.Tag.H6))) {
		    inFirstHeader = true;
		}
		// addTag done in super
		super.start(t, a);
		inParagraph = true;
	    }

	    public void end(HTML.Tag t) {
		// trapped the first header as a substitute title if needed
		if (firstHeader && 
		    ((t == HTML.Tag.H1) || (t == HTML.Tag.H2) ||
		     (t == HTML.Tag.H3) || (t == HTML.Tag.H4) ||
		     (t == HTML.Tag.H5) || (t == HTML.Tag.H6))) {
		    inFirstHeader = false;
		    firstHeader = false;
		}
		// removeTag done in super
		super.end(t);
		inParagraph = false;
	    }
	}

	public class SpecialAction extends TagAction {

	    public void start(HTML.Tag t, MutableAttributeSet a) {
		String lang = (String) a.getAttribute(HTML.Attribute.LANG);
		if (lang == null) {
		    lang = lastLang;
		}
		addTag(t, lang);
		addSpecialElement(t, a);
	    }
	    // removeTag is handled in super class
	}

	public class IsindexAction extends TagAction {

	    public void start(HTML.Tag t, MutableAttributeSet a) {
		blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
		addSpecialElement(t, a);
		blockClose(HTML.Tag.IMPLIED);
	    }

	}

	public class HiddenAction extends TagAction {

	    public void start(HTML.Tag t, MutableAttributeSet a) {
		String lang = (String) a.getAttribute(HTML.Attribute.LANG);
		if (lang == null) {
		    lang = lastLang;
		}
		addTag(t, lang);
		addSpecialElement(t, a);
	    }

	    public void end(HTML.Tag t) {
		if (!isEmpty(t)) {
		    MutableAttributeSet a = new SimpleAttributeSet();
		    a.addAttribute(HTML.Attribute.ENDTAG, "true");
		    addSpecialElement(t, a);
		} 
		removeTag(t);
	    }

	    private boolean isEmpty(HTML.Tag t) {
		if (t == HTML.Tag.APPLET ||
		    t == HTML.Tag.TITLE ||
		    t == HTML.Tag.SCRIPT) {
		    return false;
		}
		return true;
	    }
	}

	/**
	 * Subclass of HiddenAction to set the content type for style sheets,
	 * and to set the name of the default style sheet.
	 * 
	 * None of this method is really needed for the Indexer.
	 * Don't do anything related tot he style sheet and just
	 * take the actions of HiddenAction to record the position
	 */
	class MetaAction extends HiddenAction {

	    public void start(HTML.Tag t, MutableAttributeSet a) {
		// ignore all the style sheet setting code
		super.start(t, a);
	    }

	    private boolean isEmpty(HTML.Tag t) {
		return true;
	    }
	}


	/**
	 * End if overridden to create the necessary stylesheets that
	 * are referenced via the link tag. It is done in this manner
	 * as the meta tag can be used to specify an alternate style sheet,
	 * and is not guaranteed to come before the link tags.
	 *
	 * For the indexer we don't care much about the style
	 * sheets.
	 */
	class HeadAction extends BlockAction {

	    public void start(HTML.Tag t, MutableAttributeSet a) {
		inHead = true;
		super.start(t, a);
	    }

	    public void end(HTML.Tag t) {
		inHead = inStyle = false;
		// ignore the StyleSheet statements
		super.end(t);
	    }
	}

	/**
	 * A subclass to add the AttributeSet to styles if the
	 * attributes contains an attribute for 'rel' with value
	 * 'stylesheet' or 'alternate stylesheet'.
	 *
	 * For the indexer we don't care about style sheets.
	 */
	class LinkAction extends HiddenAction {

	    public void start(HTML.Tag t, MutableAttributeSet a) {
		// ignore the style sheet statements
		super.start(t, a);
	    }
	}

	/**
	 * Don't do anything for Maps in Indexer. It just stores
	 * the information in a property HashTable.
	 */
	class MapAction extends TagAction {
	}


	/**
	 * Don't do anthing for an Area action for Indexer. 
	 * It's stored locally in HTMLDocument and doesn't
	 * require an entry as a tag
	 */
	class AreaAction extends TagAction {
	}


	/**
	 * Handle style actions
	 * 
	 * Ignore the storing the styles as HTMLDocument does
	 */
	class StyleAction extends TagAction {

	    public void start(HTML.Tag t, MutableAttributeSet a) {
		if (inHead) {
		    inStyle = true;
		}
		super.start(t, a);
	    }

	    public void end(HTML.Tag t) {
		inStyle = false;
		super.end(t);
	    }
	}
	    

	public class PreAction extends BlockAction {

	    public void start(HTML.Tag t, MutableAttributeSet attr) {
		inPre = true;
		blockOpen(t, attr);
		attr.addAttribute(CSS.Attribute.WHITE_SPACE, "pre");
		blockOpen(HTML.Tag.IMPLIED, attr);
		String lang = (String) attr.getAttribute(HTML.Attribute.LANG);
		if (lang == null) {
		    lang = lastLang;
		}
		addTag(t, lang);
	    }

	    public void end(HTML.Tag t) {
		blockClose(HTML.Tag.IMPLIED);
		// set inPre to false after closing, so that if a newline
		// is added it won't generate a blockOpen.
		inPre = false;
		blockClose(t);
		removeTag(t);
	    }
	}

	public class CharacterAction extends TagAction {

	    /**
	     * In the indexer we don't care about character style
	     * and the foundInsertTag will always be true so there 
	     * really isn't much to do here
	     *
	     */
	    public void start(HTML.Tag t, MutableAttributeSet a) {
		String lang = (String) a.getAttribute(HTML.Attribute.LANG);
		if (lang == null) {
		    lang = lastLang;
		}
		addTag(t, lang);
	    }

	    public void end(HTML.Tag t) {
		removeTag(t);
	    }
	}

	/**
	 * Provides conversion of HTML tag/attribute
	 * mappings that have a corresponding StyleConstants
	 * and CSS mapping.  The conversion is to CSS attributes.
	 *
	 * For the indexer we really don't care much. We'll keep
	 * the method here for consistency sake, but it really 
	 * isn't needed.
	 */
	class ConvertAction extends TagAction {
	}

	class AnchorAction extends CharacterAction {
            // This class has a dependancy on the "one[0]" character
	    // used in javax.swing.text.html.HTMLDocument$AnchorAction
	    // we need to ensure we use the same character value.
	    private boolean post4207472 = isPost4207472();

	    public void start(HTML.Tag t, MutableAttributeSet attr) {
		// set flag to catch empty anchors
		emptyAnchor = true;
		// addTag done in super
		super.start(t, attr);
	    }
	
	    public void end(HTML.Tag t) {
		if (emptyAnchor) {
		    // if the anchor was empty it was probably a
		    // named anchor point and we don't want to throw
		    // it away.
		    char[] one = new char[1];
		    if (post4207472)
			one[0] = '\n';
		    else
		        one[0] = ' ';
		    debug ("emptyAnchor currentPos=" + currentPos);
		    addContent(one, 0, 1);
                }
		// remove tag done in super
		super.end(t);
	    }
	   private boolean isPost4207472()  {
		try {
	   	    String ver = System.getProperty("java.version"); 
		    int major = Integer.parseInt(ver.substring(2,3));
		    int minor = 0;
		    // allow for FCS case - we leave minor as 0 if dealing with FCS
		    if (ver.length() > 6)
		    	minor = Integer.parseInt(ver.substring(6,8)); 
		    if ((major > 5 ) || (major==5 && minor >= 4)) {
		    	return true;
		    } else {
		    	return false;
		    }
		} catch (Exception e) {
		    debug ("Exception in isPost4207472 : " + e);
		    return true;  // assume true if we encounter problem
	        }	
	   }
	}

	class TitleAction extends HiddenAction {

	    public void start(HTML.Tag t, MutableAttributeSet attr) {
		inTitle = true;
		super.start(t, attr);
	    }
	
	    public void end(HTML.Tag t) {
		inTitle = false;
		super.end(t);
	    }
	}

	class BaseAction extends TagAction {

	    // Nothing here for indexer
	}


	class ObjectAction extends SpecialAction {

	    public void start(HTML.Tag t, MutableAttributeSet a) {
		if (t == HTML.Tag.PARAM) {
		    // don't do anything if a parameter
		} else {
		    super.start(t, a);
		}
	    }

	    public void end(HTML.Tag t) {
		if (t != HTML.Tag.PARAM) {
		    super.end(t);
		}
	    }

	}

	public class FormAction extends SpecialAction {

	    /**
	     * Ignore INPUT and Select. Only be concerned with
	     * TEXTAREA and OPTION
	     */
	    public void start(HTML.Tag t, MutableAttributeSet attr) {
		if (t == HTML.Tag.TEXTAREA) {
		    inTextArea = true;
		}

		// Occupy one space for the element, unless this is an option.
		if (t == HTML.Tag.OPTION) {
		    //options don't take any room. They are internal to selects
		} else {
		    super.start(t, attr);
		}
	    }

	    public void end(HTML.Tag t) {
		if (t == HTML.Tag.TEXTAREA) {
		    inTextArea = false;
		}

		if (t == HTML.Tag.OPTION) {
		    // ignore options
		} else {
		    super.end(t);
		} 
	    }

	}

	// --- utility methods used by the reader ------------------

	/**
	 * Set the title for the doc
	 */
	protected void titleContent(String s) {
	    if (title == null) {
		title = new String(s);
	    } else {
		title.concat(s);
	    }
	}
	
	/**
	 * Set the first header for the doc
	 */
	protected void headerContent(String s) {
	    if (header == null) {
		header = new String(s);
	    } else {
		header.concat(s);
	    }
	}

	/**
	 * Add the given content that was encountered in a 
	 * PRE element.  This synthesizes lines to hold the
	 * runs of text, and makes calls to addContent to
	 * actually add the text.
	 */
	protected void preContent(char[] data) {
	    int last = 0;
	    for (int i = 0; i < data.length; i++) {
		if (data[i] == '\n') {
		    debug ("preContent currentPos=" + currentPos);
		    addContent(data, last, i - last + 1);
		    blockClose(HTML.Tag.IMPLIED);
		    MutableAttributeSet a = new SimpleAttributeSet();
		    a.addAttribute(CSS.Attribute.WHITE_SPACE, "pre");
		    blockOpen(HTML.Tag.IMPLIED, a);
		    last = i + 1;
		}
	    }
	    if (last < data.length) {
		debug ("preContent currentPos=" + currentPos);
		addContent(data, last, data.length - last);
	    }
	}

	/**
	 * Add an instruction to the parse buffer to create a 
	 * block element with the given attributes.
	 */
	protected void blockOpen(HTML.Tag t, MutableAttributeSet attr) {
	    debug ("blockOpen");
	    if (impliedP) {
		blockClose(HTML.Tag.IMPLIED);
	    }
		
	    inBlock++;
	    if (!canInsertTag(t, attr, true)) {
		return;
	    }
	    startTagType = true;
	    lastWasNewline = false;
	    // parse buffer code not used in indexer
	}

	/**
	 * Add an instruction to the parse buffer to close out
	 * a block element of the given type.
	 */
	protected void blockClose(HTML.Tag t) {
	    debug ("blockClose");
	    inBlock --;

	    if (!foundInsertTag) {
		return;
	    }

	    // Add a new line, if the last character wasn't one. This is
	    // needed for proper positioning of the cursor.
	    if(!lastWasNewline) {
		debug ("blockClose adding NEWLINE currentPos=" + currentPos);
		addContent(NEWLINE, 0, 1, false);
		lastWasNewline = true;
	    }

	    if (impliedP) {
		impliedP = false;
		inParagraph = false;
                if (t != HTML.Tag.IMPLIED) {
                    blockClose(HTML.Tag.IMPLIED);
                }
	    }
	    
	    // This is different from HTMLReader. I don't keep parseBuffer
	    // so I'll use the boolean startTagType instead.
	    //
	    // an open/close with no content will be removed, so we
	    // add a space of content to keep the element being formed.
	    if (startTagType) {
		char[] one = new char[1];
		one[0] = ' ';
		debug ("blockclose open/close nocontent currentPos=" + currentPos);
		addContent(one, 0, 1);
	    }
	    startTagType = false;
	}

	/**
	 * Add some text with the current character attributes.
	 *
	 * @param embedded the attributes of an embedded object.
	 */
	protected void addContent(char[] data, int offs, int length) {
	    addContent(data, offs, length, true);
	}

	/**
	 * Add some text with the current character attributes.
	 *
	 * @param embedded the attributes of an embedded object.
	 */
	protected void addContent(char[] data, int offs, int length,
				  boolean generateImpliedPIfNecessary) {
	    debug ("addContent");
	    if (!foundInsertTag) {
		return;
	    }

	    if (generateImpliedPIfNecessary && (! inParagraph) && (! inPre)) {
		blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
		inParagraph = true;
		impliedP = true;
	    }
	    emptyAnchor = false;

	    // This is different from HTMLDocument. The indexer doesn't use
	    // parseBuffer. Instead pass the strings over to IndexerKit to
	    // parse the string into tokens. Also set startTagType to false
	    // to simulate that portion of parseBuffer
	    debug ("Pre parseIntoTokens String=" + new String(data, offs, length) + " currentPos=" + currentPos);
	    startTagType = false;
	    currentPos = kit.parseIntoTokens(new String(data, offs, length), 
					     currentPos);
	    debug ("Post parseIntoTokens currentPos=" + currentPos);

	    // No flushing like HTMLDocument but set the lastWasNewline
	    // appropriately
	    if(length > 0) {
		lastWasNewline = (data[offs + length - 1] == '\n');
	    }
	}

	/**
	 * Add content that is basically specified entirely
	 * in the attribute set.
	 */
	protected void addSpecialElement(HTML.Tag t, MutableAttributeSet a) {
	    if ((t != HTML.Tag.FRAME) && (! inParagraph) && (! inPre)) {
		blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
		inParagraph = true;
		impliedP = true;
	    }

	    if (!canInsertTag(t, a, true)) {
		return;
	    }
	    emptyAnchor = false;

	    // This is different from HTMLDocument. The indexer doesn't use
	    // parseBuffer. Just increment to currentPos to simulate the
	    // addition
	    currentPos += 1;
	    debug ("addingSpecialElement tag=" + t + " currentPos=" + currentPos);
	    startTagType = false;

	    // Set this to avoid generating a newline for frames, frames
	    // shouldn't have any content, and shouldn't need a newline.
	    if (t == HTML.Tag.FRAME) {
		lastWasNewline = true;
	    }
	}

	/**
	 * Returns true if can insert starting at <code>t</code>. This 
	 * will return false if the insert tag is set, and hasn't been found
	 * yet.
	 */
	private boolean canInsertTag(HTML.Tag t, AttributeSet attr,
				     boolean isBlockTag) {
	    if (!foundInsertTag) {
		foundInsertTag(isBlockTag);
		return false;
	    }
	    return true;
	}

	private boolean isInsertTag(HTML.Tag tag) {
	    return (false);
	}

	private void foundInsertTag(boolean isBlockTag) {
	    foundInsertTag = true;
	    // The rest of the code is differnt from HTMLDocument since we
	    // don't need to worry about added to a document we don't care 
	    // about adding a newline or pushing or poping
	}

	/**
	 * addTag keeps track of tags and their lang attributes
	 */
	protected void addTag(HTML.Tag tag, String lang) {
	    LangElement el = new LangElement(tag, lang);
	    tagStack.push(el);
	    if (lastLang.compareTo(lang) != 0) {
		kit.setLocale(lang);
		lastLang = lang;
	    }
	}

	/**
	 * removeTag removes a tag from the tagStack. The tagStack is
	 * used to keep track of tags and locales
	 */
	protected void removeTag(HTML.Tag tag) {
	    LangElement el;
	    String name = tag.toString();
	    String newLang=defaultLang;

	    for (;;) {
		if (tagStack.empty()) 
		    break;
		el = (LangElement) tagStack.pop();
		if (el.getTag().toString().compareTo(name) == 0) {
		    if (tagStack.empty()) {
			newLang = defaultLang;
		    } else {
			el = (LangElement) tagStack.peek();
			newLang = el.getLang();
		    }
		    break;
		}
	    }
	    if (lastLang.compareTo(newLang) != 0) {
		kit.setLocale(newLang);
		lastLang = newLang;
	    }
	}
	
    }

    /**
     * Debug code
     */

    private boolean debugFlag=false;
    private void debug(String msg) {
        if (debugFlag) {
            System.err.println("HTMLIndexerKit: "+msg);
        }
    }
}
