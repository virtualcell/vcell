/*
 * @(#)HelpHistoryModelEvent.java	1.4 06/10/30
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

/**
 * Notifies interested parties that a change in a
 * Help History Model source has occurred.
 *
 * @author  Richard Gregor
 * @version   1.4     10/30/06
 */

public class HelpHistoryModelEvent extends java.util.EventObject{

    private boolean next;
    private boolean previous;    
    
    /**
     * Represents a history change 
     *
     * @param source The source for this event.
     * @param previous If true a previous action is allowed.
     * @param next If true a next action is allowed.
     * @throws IllegalArgumentException if source is null.
     */
    public HelpHistoryModelEvent(Object source, boolean previous, 
				 boolean next) {
        super(source);
        this.next = next;
        this.previous = previous;        
    }
    
    /**
     * Returns if action "previous" is allowed.
     * 
     * @return True if action is allowed, false otherwise. 
     */
    public boolean isPrevious(){
        return previous;
    }
    
    /**
     * Returns if action "next" is allowed
     *
     * @return True if action is allowed, false otherwise.
     */
    public boolean isNext() {
        return next;
    }
    
}
