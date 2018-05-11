/*
 * @(#)EventListenerList.java	1.9 06/10/30
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

import java.io.*;
import java.util.*;

/**
 * This class is a copy of the javax.swing.event.EventListenerList class.
 *
 * It is duplicated here because we do not want to introduce a dependency
 * on Swing to HelpSet and DefaultHelpModel, which are otherwise Swing-free.
 */

/**
 * A class that holds a list of EventListeners.  A single instance
 * can be used to hold all listeners (of all types) for the instance
 * using the list.  It is the responsiblity of the class using the
 * EventListenerList to provide a type-safe API (preferably conforming
 * to the JavaBeans specification) and methods that dispatch event notification
 * methods to appropriate Event Listeners on the list.
 * 
 * The main benefits this class provides are that it is relatively
 * cheap if there are no listeners, provides serialization for 
 * eventlistener lists in a single place, and provides a degree of MT safety
 * (when used correctly).
 *
 * Usage example:
 *    If one is defining a class that sends out FooEvents, and wants
 * to allow users of the class to register FooListeners and receive 
 * notification when FooEvents occur.  The following should be added
 * to the class definition:
   <pre>
   EventListenerList listenrList = new EventListnerList();
   FooEvent fooEvent = null;

   public void addFooListener(FooListener l) {
       listenerList.add(FooListener.class, l);
   }

   public void removeFooListener(FooListener l) {
       listenerList.remove(FooListener.class, l);
   }

 
    // Notify all listeners that have registered interest for
    // notification on this event type.  The event instance 
    // is lazily created using the parameters passed into 
    // the fire method.

    protected void firefooXXX() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==FooListener.class) {
		// Lazily create the event:
		if (fooEvent == null)
		    fooEvent = new FooEvent(this);
		((FooListener)listeners[i+1]).fooXXX(fooEvent);
	    }	       
	}
    }	
   </pre>
 * foo should be changed to the appropriate name, and Method to the
 * appropriate method name (one firing method should exist for each
 * notification method in the FooListener interface).
 * <p>
 * Warning: Serialized objects of this class are not be compatible with
 * future Swing releases.  The current serialization support is appropriate 
 * for short term storage or RMI between Swing1.0 applications.  It will
 * not be possible to load serialized Swing1.0 objects with future releases
 * of Swing.  The JDK1.2 release of Swing will be the compatibility
 * baseline for the serialized form of Swing objects.
 *
 * @version 1.19 03/18/98
 * @author Georges Saab
 * @author Hans Muller
 * @author James Gosling
 */
public class EventListenerList implements Serializable {
    /* A null array to be shared by all empty listener lists*/
    private final static Object[] NULL_ARRAY = new Object[0];
    /* The list of ListenerType - Listener pairs */
    protected transient Object[] listenerList = NULL_ARRAY;

    /**
     * This passes back the event listener list as an array
     * of ListenerType - listener pairs.  Note that for 
     * performance reasons, this implementation passes back 
     * the actual data structure in which the listener data
     * is stored internally!  
     * This method is guaranteed to pass back a non-null
     * array, so that no null-checking is required in 
     * fire methods.  A zero-length array of Object should
     * be returned if there are currently no listeners.
     * 
     * WARNING!!! Absolutely NO modification of
     * the data contained in this array should be made -- if
     * any such manipulation is necessary, it should be done
     * on a copy of the array returned rather than the array 
     * itself.
     */
    public Object[] getListenerList() {
	return listenerList;
    }

    /**
     * Returns the total number of listeners for this listenerList.
     */
    public int getListenerCount() {
	return listenerList.length/2;
    }

    /**
     * Returns the total number of listeners of the supplied type 
     * for this listenerList.
     */
    public int getListenerCount(Class t) {
	int count = 0;
	Object[] lList = listenerList;
	for (int i = 0; i < lList.length; i+=2) {
	    if (t == (Class)lList[i])
		count++;
	}
	return count;
    }
    /**
     * Adds the listener as a listener of the specified type.
     * @param t The type of the listener to be added.
     * @param l The listener to be added.
     * @throws IllegalArgumentException if <tt>l</tt> or <tt>t</tt> is 
     * null or <tt>t</tt> is not an instance of <tt>l</tt>.
     */
    public synchronized void add(Class t, EventListener l) {
	if (l ==null || t == null) {
	    throw new IllegalArgumentException("Listener " + l +
					 " is null");
	}
	if (!t.isInstance(l)) {
	    throw new IllegalArgumentException("Listener " + l +
					 " is not of type " + t);
	}
	if (listenerList == NULL_ARRAY) {
	    // if this is the first listener added, 
	    // initialize the lists
	    listenerList = new Object[] { t, l };
	} else {
	    // Otherwise copy the array and add the new listener
	    int i = listenerList.length;
	    Object[] tmp = new Object[i+2];
	    System.arraycopy(listenerList, 0, tmp, 0, i);

	    tmp[i] = t;
	    tmp[i+1] = l;

	    listenerList = tmp;
	}
    }

    /**
     * Removes the listener as a listener of the specified type. If the
     * listener for a given class does not exist in the list, it is ignored.
     * @param t The type of the listener to be removed.
     * @param l The listener to be removed.
     * @throws IllegalArgumentException if <tt>l</tt> or <tt>t</tt> is 
     * null or <tt>t</tt> is not an instance of <tt>l</tt>.
     */
    public synchronized void remove(Class t, EventListener l) {
	if (l == null || t == null) {
	    throw new IllegalArgumentException("Listener " + l +
					 " is null");
	}

	if (!t.isInstance(l)) {
	    throw new IllegalArgumentException("Listener " + l +
					 " is not of type " + t);
	}
	// Is l on the list?
	int index = -1;
	for (int i = listenerList.length-2; i>=0; i-=2) {
	    if ((listenerList[i]==t) && (listenerList[i+1] == l)) {
		index = i;
		break;
	    }
	}
	
	// If so,  remove it
	if (index != -1) {
	    Object[] tmp = new Object[listenerList.length-2];
	    // Copy the list up to index
	    System.arraycopy(listenerList, 0, tmp, 0, index);
	    // Copy from two past the index, up to
	    // the end of tmp (which is two elements
	    // shorter than the old list)
	    if (index < tmp.length)
		System.arraycopy(listenerList, index+2, tmp, index, 
				 tmp.length - index);
	    // set the listener array to the new array or null
	    listenerList = (tmp.length == 0) ? NULL_ARRAY : tmp;
	    }
    }

    // Serialization support.  
    private void writeObject(ObjectOutputStream s) throws IOException {
	Object[] lList = listenerList;
	s.defaultWriteObject();
	
	// Save the non-null event listeners:
	for (int i = 0; i < lList.length; i+=2) {
	    Class t = (Class)lList[i];
	    EventListener l = (EventListener)lList[i+1];
	    if ((l!=null) && (l instanceof Serializable)) {
		s.writeObject(t.getName());
		s.writeObject(l);
	    }
	}
	
	s.writeObject(null);
    }

    private void readObject(ObjectInputStream s) 
	throws IOException, ClassNotFoundException {
        listenerList = NULL_ARRAY;
	s.defaultReadObject();
	Object listenerTypeOrNull;
	
	while (null != (listenerTypeOrNull = s.readObject())) {
	    EventListener l = (EventListener)s.readObject();
	    add(Class.forName((String)listenerTypeOrNull), l);
	}	    
    }

    /**
     * Returns a string representation of the EventListenerList.
     */
    public String toString() {
	Object[] lList = listenerList;
	String s = "EventListenerList: ";
	s += lList.length/2 + " listeners: ";
	for (int i = 0 ; i <= lList.length-2 ; i+=2) {
	    s += " type " + ((Class)lList[i]).getName();
	    s += " listener " + lList[i+1];
	}
	return s;
    }
}
