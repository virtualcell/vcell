/*
 * @(#)TreeItem.java	1.28 06/10/30
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

import javax.help.Map.ID;
import java.util.Locale;
import java.net.URL;
import java.io.Serializable;
import java.io.IOException;

/**
 * The base items known to TOC, Index and Favorites Navigators.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Richard Gregor
 * @version   1.28     10/30/06
 */

public class TreeItem implements Serializable
{

    /**
     * A state of expansion determined by the view
     */
    public static final int DEFAULT_EXPANSION = -1;

    /**
     * Show the children of the node collapsed in the view
     */
    public static final int COLLAPSE = 0;

    /**
     * Show the children of the node expanded in the view
     */
    public static final int EXPAND = 1;

    private String name;
    private ID id;
    protected Locale locale;
    private String mergeType;
    private int expand = DEFAULT_EXPANSION;
    private String presentation;
    private String presentationName;
    private HelpSet hs;

     /**
     * Create an TreeItem.
     *
     * @param id ID for the item. The ID can be null.
     * @param hs A HelpSet scoping this item.
     * @param locale The locale for this item
     */
    public TreeItem(ID id, HelpSet hs, Locale locale) {
	this.id = id;
	this.hs = hs;
	this.locale = locale;
    }

   /**
     * Creates a TreeItem.
     *
     * @param id ID for the item. Null is a valid ID.
     * @param The lang for this item. A null is valid and indicates the default
     * locale.
     */
    public TreeItem(ID id, Locale locale){
	this (id, null, locale);
    }
    /**
     * Creates a TreeItem.
     *
     * @param name The name for the item.
     */
    public TreeItem(String name){
        this(null,null, null);
        setName(name);
    }
    
    /**
     * Creates an empty TreeItem.
     */
    public TreeItem(){
        this(null,null);        
    }
    /**
     * Sets the name of the item.
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * Returns the name of the item.
     */
    public String getName() {
	return name;
    }
    
    /**
     * Set the ID for the item.
     */
    public void setID (ID id) {
	this.id = id;
    }

    /**
     * Returns the ID for the item.
     */
    public ID getID() {
	return id;
    }

    /**
     * Returns the URL for the item.
     */
    public URL getURL() {
        try {
            return id.getURL();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Set the HelpSet for this TreeItem.
     */
    public void setHelpSet(HelpSet hs) {
	this.hs = hs;
    }

    /**
     * Returns the HelpSet scoping this IndexItem. Will return the ID HelpSet
     * if one exists. Null otherwise
     */
    public HelpSet getHelpSet() {
	return hs;
    }
    
    /**
     * Returns the locale for the item.
     */
    public Locale getLocale() {
	return locale;
    }
    
    /**
     * Sets the merge type
     */
    public void setMergeType(String mergeType){
        this.mergeType = mergeType;
    }
    
    /**
     * Returns the merge type for the item
     */
    public String getMergeType(){
        return mergeType;
    }

    /**
     * Sets the expansion type
     * @throws IllegalArgumentException if not a valid type
     */
    public void setExpansionType(int type) {
	if (type < DEFAULT_EXPANSION || type > EXPAND) {
	    throw new IllegalArgumentException("Invalid expansion type");
	}
	expand = type;
    }

    /**
     * Returns the exansion type
     */
    public int getExpansionType() {
	return expand;
    }

    /**
     * Sets the presentation
     * @see Presentation
     */
    public void setPresentation(String presentation) {
	this.presentation = presentation;
    }

    /**
     * Returns the presentation
     * @see Presentation
     */
    public String getPresentation() {
	return presentation;
    }

    /**
     * Sets the presentation name
     * @see Presentation
     */
    public void setPresentationName(String presentationName) {
	this.presentationName = presentationName;
    }

    /**
     * Returns the presentation name
     * @see Presentation
     */
    public String getPresentationName() {
	return presentationName;
    }

    /**
     * Returns a String used when displaying the object.
     * Used by CellRenderers.
     *
     * @see TOCCellRenderer
     */
    public String toString() {
	return (id+"("+name+")");
    }

    // for serialization
     private void writeObject(java.io.ObjectOutputStream out) throws IOException {
         //ignore so that FavoritesItem will work
     }
     
     private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
         //ignore so that FavoritesItem will work
     }
 

}

