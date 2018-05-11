/*
 * @(#)AbstractHelpAction.java	1.2 06/10/30
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Hashtable;

/**
 *
 * @author Stepan Marek
 * @version	1.2	10/30/06
 */
public abstract class AbstractHelpAction implements HelpAction {
        
    AbstractHelpAction(Object control, String name) {
        this.control = control;
        putValue("name", name);
    }
    
    /** Holds value of property enabled. */
    private boolean enabled = true;
    
    /** Holds value of property control. */
    private Object control;
    
    private Hashtable table;
    
    /** Utility field used by bound properties. */
    private PropertyChangeSupport propertyChangeSupport;;
    
    /** Add a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
        if (propertyChangeSupport == null) {
            propertyChangeSupport =  new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(l);
    }
    
    /** Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
        if (propertyChangeSupport == null) {
            propertyChangeSupport =  new PropertyChangeSupport(this);
        }
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    /**
     * Supports reporting bound property changes.  This method can be called
     * when a bound property has changed and it will send the appropriate
     * <code>PropertyChangeEvent</code> to any registered 
     * <code>PropertyChangeListeners</code>.
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertyChangeSupport == null) {
            return;
        }
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /** Getter for property enabled.
     * @return Value of property enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /** Setter for property enabled.
     * @param enabled New value of property enabled.
     */
    public void setEnabled(boolean enabled) {
        boolean oldEnabled = this.enabled;
        this.enabled = enabled;
        firePropertyChange("enabled", new Boolean(oldEnabled), new Boolean(enabled));
    }
    
    /** Getter for property control.
     * @return Value of property control.
     */
    public Object getControl() {
        return control;
    }

    /** 
     * Gets the <code>Object</code> associated with the specified key.
     *
     * @param key a string containing the specified <code>key</code>
     * @return the binding <code>Object</code> stored with this key; if there
     *		are no keys, it will return <code>null</code>
     * @see Action#getValue
     */
    public Object getValue(String key) {
	if (table == null) {
	    return null;
	}
	return table.get(key);
    }
    
    /** 
     * Sets the <code>Value</code> associated with the specified key.
     *
     * @param key  the <code>String</code> that identifies the stored object
     * @param newValue the <code>Object</code> to store using this key
     * @see Action#putValue 
     */
    public void putValue(String key, Object newValue) {
	if (table == null) {
	    table = new Hashtable();
	}
        Object oldValue;
        if (newValue == null) {
            oldValue = table.remove(key);
        } else {
            oldValue = table.put(key, newValue);
        }
	firePropertyChange(key, oldValue, newValue);
    }

}
