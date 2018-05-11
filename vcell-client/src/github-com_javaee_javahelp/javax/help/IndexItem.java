/*
 * @(#)IndexItem.java	1.18 06/10/30
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

import java.util.Vector;
import java.util.Locale;
import javax.help.Map.ID;

/**
 * A class for individual index items.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.18	10/30/06
 */

public class IndexItem extends TreeItem {

    /**
     * Create an IndexItem.
     *
     * @param id ID for the item. The ID can be null.
     * @param hs A HelpSet scoping this item.
     * @param locale The locale for this item
     */
    public IndexItem(ID id, HelpSet hs, Locale locale) {
	super(id, hs, locale);
    }

    /**
     * Create an IndexItem defaulting the HelpSet to that of its ID.
     *
     * @param id ID for the item. The ID can be null.
     * @param locale The locale to use for this item.
     */
    public IndexItem(ID id, Locale locale) {
	super(id, locale);
	HelpSet hs = null;
	if (id != null) {
	    setHelpSet(id.hs);
	}

    }

    /**
     * Create a default IndexItem.
     */
    public IndexItem() {
	super(null, null, null);
    }
}
