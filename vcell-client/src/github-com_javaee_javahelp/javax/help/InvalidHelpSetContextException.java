/*
 * @(#)InvalidHelpSetContextException.java	1.6 06/10/30
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

/**
 * The HelpSet is not a (transitive) sub-HelpSet of some context HelpSet.
 *
 * For example, setting an ID to a HelpModel.
 *
 * @author Eduardo Pelegri-Llopart
 * @version	1.3	01/29/99
 */
public class InvalidHelpSetContextException extends Exception {
    private HelpSet context;
    private HelpSet hs;

    /**
     * Create the exception. All parameters accept null values.
     * 
     * @param msg The message. If msg is null it is the same as if
     * no detailed message was specified.
     */
    public InvalidHelpSetContextException(String msg,
					  HelpSet context,
					  HelpSet hs) {
	super(msg);
	this.context = context;
	this.hs = hs;
    }

    /**
     * Get the context HelpSet
     */
    public HelpSet getContext() {
	return context;
    }

    /**
     * Get the offending HelpSet
     */
    public HelpSet getHelpSet() {
	return hs;
    }
}
