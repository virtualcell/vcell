/*
 * @(#)HelpSetEvent.java	1.13 06/10/30
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

package javax.help.event;

import javax.help.HelpSet;

/**
 * Conveys information when a HelpSet is added/removed.
 *
 * @author Eduardo Pelegri-Llopart
 * @version	1.10	03/10/99
 */

public class HelpSetEvent extends java.util.EventObject {

    /**
     * Creates a HelpSetEvent.
     *
     * @param source Source of this Event.
     * @param helpset The HelpSet being added/removed.
     * @param action HELPSET_ADDED or HELPSET_REMOVED.
     * @throws IllegalArgumentException if source is null or if action is not
     * a valid action.
     */
     public HelpSetEvent(Object source, HelpSet helpset, int action) {
	 super(source);
         this.helpset = helpset;
	 if (helpset == null) {
	     throw new NullPointerException("helpset");
	 }
         this.action = action;
	 if (action < 0 || action > 1) {
	     throw new IllegalArgumentException("invalid action");
	 }
     }

    /**
     * A HelpSet was added
     */
     public static final int HELPSET_ADDED = 0;

    /**
     * A HelpSet was removed
     */
     public static final int HELPSET_REMOVED = 1;

    /**
     * @return The HelpSet.
     */
     public HelpSet getHelpSet() {
	return helpset;
     }

    /**
     * @return The action
     */
     public int getAction() {
        return action;
     }

    /**
     * Returns textual about the instance. 
     */
    public String toString() {
	if (action==HELPSET_ADDED) {
	    return "HelpSetEvent("+source+", "+helpset+"; added";
	} else {
	    return "HelpSetEvent("+source+", "+helpset+"; removed";
	}
    }

     private HelpSet helpset;
     private int action;
}
