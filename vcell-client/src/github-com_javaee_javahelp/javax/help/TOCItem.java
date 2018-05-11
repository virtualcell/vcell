/*
 * @(#)TOCItem.java	1.17 06/10/30
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

/**
 * A class for individual TOC items
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-llopart
 * @(#)TOCItem.java 1.13 01/29/99
 */

public class TOCItem extends TreeItem { 
    private ID imageID;

    /**
     * Creates a TOCItem.
     *
     * @param id ID for the item. A null ID is valid.
     * @param image The ID for image to be displayed for this item. A null
     * image is valid.
     * @param hs The HelpSet scoping this item.  In almost all cases
     * this is the same as the HelpSet of the id field. A null ID is valid.
     * @param lang The locale for this item. A null locale indicates the
     * default locale.
     */
    public TOCItem(ID id, ID imageID, HelpSet hs, Locale locale) {
	super(id, hs, locale);
	this.imageID = imageID;
    }

    /**
     * Creates a TOCItem with a default HelpSet based on its ID.
     *
     * @param id ID for the item. The ID can be null.
     * @param image The image to be displayed for this item.
     * @param lang The locale for this item
     */
    public TOCItem(ID id, ID imageID, Locale locale){
	super(id, locale);
	HelpSet hs = null;
	if (id != null) {
	    setHelpSet(id.hs);
	}
	this.imageID = imageID;
    }

    /**
     * Creates a default TOCItem.
     */

    public TOCItem() {
	super(null, null, null);
	this.imageID = null;
    }

    /**
     * Returns the image for this TOCItem.
     */
    public ID getImageID() {
	 return imageID;
    }

}
