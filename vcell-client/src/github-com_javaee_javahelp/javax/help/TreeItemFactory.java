/*
 * @(#)TreeItemFactory.java	1.9 06/10/30
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
import java.net.URL;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A factory for creating TreeItems.  This can be used to reuse the parsers.
 *
 * @author Eduardo Pelegri-Llopart
 * @(#)TreeItemFactory.java 1.7 01/29/99
 */

public interface TreeItemFactory {
    /**
     * Starts parsing.
     *
     * @param source The URL of the document being parsed.
     */
    public void parsingStarted(URL source);


    /**
     * Processes a DOCTYPE.
     *
     * @param root The root tag of the document.
     * @param publicID PublicID from the DOCTYPE.
     * @param systemID SystemID from the DOCTYPE.
     */
    public void processDOCTYPE(String root, String publicID, String systemID);

    /**
     * A Processing Instruction.
     *
     * @param target The target of the PI.
     * @param data A String for the data in the PI.
     */
    public void processPI(HelpSet hs,
			  String target,
			  String data);

    /**
     * Creates a TreeItem from the given data.
     *
     * @param tagName The name of the tag (for example, treeItem, or tocItem)
     * @param attributes A hashtable with all the attributes.  Null is a valid value.
     * @param hs A HelpSet that provides context.
     * @param lang The locale.
     * @return A TreeItem.
     */
    public TreeItem createItem(String tagName,
			       Hashtable attributes,
			       HelpSet hs,
			       Locale locale);

    /**
     * Creates a default TreeItem.
     *
     * @return A TreeItem
     */
    public TreeItem createItem();

    /**
     * Reports a parsing error.
     *
     * @param msg The message to report.
     * @param validParse Whether the result of the parse is still valid.
     */
    public void reportMessage(String msg, boolean validParse);

    /**
     * Lists all the error messages.
     */
    public Enumeration listMessages();

    /**
     * Ends parsing.  Last chance to do something
     * to the node.  Return null to be sure the result is discarded.
     */
    public DefaultMutableTreeNode parsingEnded(DefaultMutableTreeNode node);
}

