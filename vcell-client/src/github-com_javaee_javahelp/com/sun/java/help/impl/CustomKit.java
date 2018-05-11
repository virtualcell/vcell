/*
 * @(#)CustomKit.java	1.15 06/10/30
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

package com.sun.java.help.impl;

import java.util.Enumeration;
import java.util.Vector;
import java.awt.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.beans.*;
import java.lang.reflect.*;
import javax.swing.text.html.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

/**
 * Subclass of HTMLEditorKit from Swing to adds better functionality
 * for handing <OBJECT> tags and supports some special classes
 * for JavaHelp.
 *
 * @author Tim Prinzing
 * @author Eduardo Pelegri-Llopart
 * @version	1.15	10/30/06
 */

public class CustomKit extends HTMLEditorKit {

    public CustomKit() {
	super();
    }

    /**
     * Clone the editorkit
     */
    public Object clone() {
	return new CustomKit();
    }

    /**
     * Called when the kit is being installed into the
     * a JEditorPane. 
     *
     * @param c the JEditorPane
     */
      public void install(JEditorPane c) {
	c.addMouseMotionListener(mouseHandler);
	super.install(c);
    }

    /**
     * Called when the kit is being removed from the
     * JEditorPane.  This is used to unregister any 
     * listeners that were attached.
     *
     * @param c the JEditorPane
     */
    public void deinstall(JEditorPane c) {
	c.removeMouseMotionListener(mouseHandler);
	super.deinstall(c);
    }

    public Document createDefaultDocument() {
	// normally we would do exactly what HTMLEditor.createDefaultDocument
	// does:
	//
	// StyleSheet styles = getStyleSheet();
	// StyleSheet ss = new StyleSheet();
	// ss.addStyleSheet(styles);
	// HTMLDocument doc = new HTMLDocument(ss);
	//
	// However, since StyleSheet.addStyleSheet is package private, we'll
	// have to work around the problem.
	HTMLDocument tmpDoc = (HTMLDocument)super.createDefaultDocument();

	HTMLDocument doc = new CustomDocument(tmpDoc.getStyleSheet());
	doc.putProperty ("__PARSER__", getParser());
	doc.setAsynchronousLoadPriority(4);
	doc.setTokenThreshold(100);
	debug("fetch custom document");
	return doc;
    }

    /**
     * Fetch a factory that is suitable for producing 
     * views of any models that are produced by this
     * kit.  
     *
     * @return the factory
     */
    public ViewFactory getViewFactory() {
	debug("fetched custom factory");
	return new CustomFactory();
    }

    // --- variables ------------------------------------------
    private MouseMotionListener mouseHandler = new MouseHandler();

    /**
     * Class to watch the associated component and change the
     * cursor when in a hyperlink
     */
    public static class MouseHandler implements MouseMotionListener {

	private Element curElem = null;
	private Cursor origCursor;
	private Cursor handCursor=null;

	// ignore the drags
	public void mouseDragged(MouseEvent e) {
	}

	// track the moving of the mouse.
	public void mouseMoved(MouseEvent e) {
	    JEditorPane editor = (JEditorPane) e.getSource();

	    if (!editor.isEditable()) {
		Point pt = new Point(e.getX(), e.getY());
		int pos = editor.viewToModel(pt);
		if (pos >= 0) {
		    Document doc = editor.getDocument();
		    if (doc instanceof HTMLDocument) {
			HTMLDocument hdoc = (HTMLDocument) doc;
			Element elem = hdoc.getCharacterElement(pos);
			AttributeSet a = elem.getAttributes();
			AttributeSet anchor = (AttributeSet) a.getAttribute(HTML.Tag.A);
			String href = (anchor != null) ? 
			    (String) anchor.getAttribute(HTML.Attribute.HREF) 
			    : null;
			
			if (href != null) {
			    if (curElem != elem) {
				curElem = elem;
				if (origCursor == null) {
				    origCursor = editor.getCursor();
				}
				if (handCursor == null) {
				    handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				}
				editor.setCursor(handCursor);
			    }
			} else {
			    if (curElem != null) {
				curElem = null;
				editor.setCursor(origCursor);
			    }
			}
		    }
		}
	    }
	}
    }

    static class CustomFactory extends HTMLFactory {

        public View create(Element elem) {
	    Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
	    if (o instanceof HTML.Tag) {
		HTML.Tag kind = (HTML.Tag) o;
		if (kind == HTML.Tag.OBJECT) {
		    debug("creating ObjectView1 for: " + elem);
		    return new ObjectView1(elem);
		}
	    }
	    return super.create(elem);
	}

    }

    static class CustomDocument extends HTMLDocument {

	CustomDocument(StyleSheet s) {
	    super(s);
	}

        public HTMLEditorKit.ParserCallback getReader(int pos) {
	    Object desc = getProperty(Document.StreamDescriptionProperty);
	    if (desc instanceof URL) { 
		setBase((URL)desc);
	    }
	    HTMLReader reader = new CustomReader(pos);
	    return reader;
	}

	class CustomReader extends HTMLReader {

	    public CustomReader(int offset) {
		super(offset);
		registerTag(HTML.Tag.PARAM, new ObjectAction1());
	    }

	    Vector getParseBuffer() {
		return parseBuffer;
	    }

	    class ObjectAction1 extends SpecialAction {
		
                public void start(HTML.Tag t, MutableAttributeSet a) {
		    if (t == HTML.Tag.PARAM) {
			addParameter(a);
		    } else {
			super.start(t, a);
		    }
		}
		
  	        public void end(HTML.Tag t) {
		    if (t != HTML.Tag.PARAM) {
			super.end(t);
		    }
		}
		
		void addParameter(AttributeSet a) {
		    debug ("addParameter AttributeSet=" + a);
		    String name = (String) a.getAttribute(HTML.Attribute.NAME);
		    String value = (String) a.getAttribute(HTML.Attribute.VALUE);
		    if ((name != null) && (value != null)) {
			ElementSpec objSpec = (ElementSpec) getParseBuffer().lastElement();
			MutableAttributeSet objAttr = (MutableAttributeSet) objSpec.getAttributes();
			objAttr.addAttribute(name, value);
		    }
		}
	    }
	}
    }

    static class ObjectView1 extends ComponentView  {

	/**
	 * Creates a new ObjectView object.
	 *
	 * @param elem the element to decorate
	 */
	public ObjectView1(Element elem) {
	    super(elem);
	}
    
	/**
	 * Create the component.  The classid is used
	 * as a specification of the classname, which
	 * we try to load.
	 */
	protected Component createComponent() {
	    AttributeSet attr = getElement().getAttributes();
	    debug("attr: " + attr.copyAttributes());
	    String classname = (String) attr.getAttribute(HTML.Attribute.CLASSID);
	    try {
		int colon = classname.indexOf(':');
		if (colon != -1) {
		    String classtype= classname.substring(0, colon).toLowerCase();
		    if (classtype.compareTo("java") == 0) {
			classname = classname.substring(colon + 1);
			Class c = getClass(classname);
			Object o = c.newInstance();
			if (o instanceof Component) {
			    Component comp = (Component) o;
			    if (o instanceof ViewAwareComponent) {
				((ViewAwareComponent) comp).setViewData(this);
			    }
			    setParameters(comp, attr);
			    return comp;
			}
		    }
		}
	    } catch (Throwable e) {
		// couldn't create a component... fall through to the 
		// couldn't load representation.
	    }
	    
	    return getUnloadableRepresentation();
	}
    
	/**
	 * Fetch a component that can be used to represent the
	 * object if it can't be created.
	 */
	Component getUnloadableRepresentation() {
	    // PENDING(prinz) get some artwork and return something
	    // interesting here.
	    Component comp = new JLabel("??");
	    comp.setForeground(Color.red);
	    return comp;
	}
    
	/**
	 * Get a Class object to use for loading the classid.
	 *
	 * If possible, the Classloader used to load the associated
	 * Document is used. This would typically be the same as the ClassLoader
	 * used to load the EditorKit.
	 *
	 * If the documents ClassLoader is null,
	 * <code>Class.forName</code> is used.
	 */
	private Class getClass(String classname) throws ClassNotFoundException {
	    Class klass = null;
	    
	    Class docClass = getDocument().getClass();
	    ClassLoader loader = docClass.getClassLoader();
	    if (loader != null) {
		klass = loader.loadClass(classname);
	    }
	    if (klass == null) {
		klass = Class.forName(classname);
	    }
	    return klass;
	}
    
	/**
	 * Initialize this component according the KEY/VALUEs passed in
	 * via the &lt;param&gt; elements in the corresponding 
	 * &lt;object&gt; element.
	 */
	private void setParameters(Component comp, AttributeSet attr) {
	    Class k = comp.getClass();
	    BeanInfo bi;
	    try {
		bi = Introspector.getBeanInfo(k);
	    } catch (IntrospectionException ex) {
		debug("introspector failed, ex: "+ex);
		return;		// quit for now
	    }
	    PropertyDescriptor props[] = bi.getPropertyDescriptors();
	    for (int i=0; i < props.length; i++) {
		debug("checking on props[i]: "+props[i].getName());
		Object v = attr.getAttribute(props[i].getName());
		if (v instanceof String) {
		    // found a property parameter
		    String value = (String) v;
		    Method writer = props[i].getWriteMethod();
		    if (writer == null) {
			// read-only property. ignore
			return;	// for now
		    }
		    Class[] params = writer.getParameterTypes();
		    if (params.length != 1) {
			// zero or more than one argument, ignore
			return;	// for now
		    }
		    String [] args = { value };
		    try {
			writer.invoke(comp, (java.lang.Object[]) args);
			debug("Invocation succeeded");
		    } catch (Exception ex) {
			debug("Invocation failed");
			// invocation code
		    }
		}
	    }
	}
    }

    /**
     * For printf debugging.
     */
    private final static boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("CustomKit: " + str);
        }
    }
}
