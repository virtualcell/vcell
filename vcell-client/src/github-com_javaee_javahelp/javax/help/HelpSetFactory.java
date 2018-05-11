/*
 * @(#)HelpSetFactory.java	1.12 06/10/30
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

import java.util.Hashtable;
import java.util.Locale;
import java.util.Enumeration;
import java.util.Vector;
import java.net.URL;
import java.awt.Dimension;
import java.awt.Point;

/**
 * A factory for creating HelpSets.  This can be used to reuse our parser.
 *
 * @author Eduardo Pelegri-Llopart
 * @(#)HelpSetFactory.java 1.12 10/30/06
 */

interface HelpSetFactory {
    /**
     * Parsing starts.
     *
     * @param url URL to the document being parsed
     */
    public void parsingStarted(URL source);

    /**
     * Processes a DOCTYPE
     *
     * @param root The root tag of the document
     * @param publicID PublicID from the DOCTYPE
     * @param systemID SystemID from the DOCTYPE
     */
    public void processDOCTYPE(String root, String publicID, String systemID);

    /**
     * A Processing Instruction.
     *
     * @param target The target of the PI
     * @param data A String for the data in the PI
     */
    public void processPI(HelpSet hs,
			  String target,
			  String data);

    /**
     * process <title>
     *
     * @param hs The Helpset
     * @param title The title of the HelpSet
     */
    public void processTitle(HelpSet hs,
			     String title);

    /**
     * Processes &lt;homeID&gt;.
     *
     * @param hs The Helpset
     * @param homeID The home ID for the helpset
     */
    public void processHomeID(HelpSet hs,
			      String homeID);

    /**
     * Process a &l;mapref&gt;.
     *
     * @param hs The HelpSet
     * @param Attributes for this tag
     */
    public void processMapRef(HelpSet hs,
			      Hashtable attributes);

    /**
     * Creates a NavigatorView from the data.
     *
     * @param hs The HelpSet
     */

    public void processView(HelpSet hs,
			    String name,
			    String label,
			    String type,
			    Hashtable viewAttributes,
			    String data,
			    Hashtable dataAttributes,
			    Locale locale);
    
    /**
     * Processes a sub-HelpSet tag.
     *
     * @param base The base URL from where to locate the sub-HelpSet.
     * @param att A collection of attributes that might be used.
     * @returns A HelpSet to be added.
     */
    public void processSubHelpSet(HelpSet hs,
				  Hashtable attributes);

    /**
     * Creates a HelpSet.Presentation from the data. The hs and name
     * parameters are the only required parameters. All others may be null.
     *
     * @param hs The HelpSet
     * @param name Name of the Presentation
     * @param default Is the Presenation the default Presentation
     * @param displayViews Display the Views in the Presentation
     * @param displayViewImages Display the Views Images in the Presentation
     * @param size Size of the Presentation
     * @param location Location of the Presentation
     * @param title Title for the presentation
     * @param toolbar Is there a custom toolbar for the presentation
     * @param helpActions A Vector of HelpAction(s).
     * 
     * 
     */

    public void processPresentation(HelpSet hs,
				    String name,
				    boolean defaultPresentation,
				    boolean displayViews,
				    boolean displayViewImages,
				    Dimension size,
				    Point location,
				    String title,
				    String imageMapID,
				    boolean toolbar,
				    Vector helpActions);
    
    /**
     * Reports some parsing error.
     *
     * @param msg The message to report.
     * @param validParse Whether the on-going parse should return a valid object.
     */
    public void reportMessage(String msg, boolean validParse);

    /**
     * Enumerated all the error mesages.
     */
    public Enumeration listMessages();

    /**
     * Parsing ends.  Last chance to do something
     * to the HelpSet
     */
    public HelpSet parsingEnded(HelpSet hs);

    /**
     * Internal storage of HelpAction defined in a Presentaion
     */
    public class HelpAction {

	/**
	 * Name of the Action
	 */
	public String className = null;

	/**
	 * Attributes of the Action
	 */
	public Hashtable attr = null;

	public HelpAction (String className, Hashtable attr) {
	    this.className = className;
	    this.attr = attr;
	}
    }


}
