/*
 * @(#)CSH.java	1.50 06/10/30
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

import java.lang.reflect.*;
import javax.help.Map.ID;
import java.awt.ActiveEvent;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.MenuComponent;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.applet.Applet;
import java.net.URL;
import java.util.Stack;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.WeakHashMap;
import java.lang.ref.WeakReference;

/**
 * A convenience class that provides simple
 * access to context-senstive help functionality. It creates a default JavaHelp
 * viewer as well as ActionListeners for "Help" keys, on-item help, and
 * help buttons.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @version	1.50	10/30/06
 *
 */
public class CSH {
    
    static private java.util.Map comps;
    static private java.util.Map parents;
    static private java.util.Vector managers = new Vector();
    
    /**
     * Registers the specified manager to maintain dynamic CSH.
     * @param m the CSH manager
     * @since 2.0
     */
    public static void addManager(Manager m) {
        managers.add(m);
    }
    
    /**
     * Registers the specified manager to maintain dynamic CSH at the specified
     * position in list of managers.
     * @param i index at which specified manager is to be inserted.
     * @param m the CSH manager
     * @exception ArrayIndexOutOfBoundsException index out of range
     *            (i &lt; 0 || i &gt;= CSH.getManagerCount()).
     * @since 2.0
     */
    public static void addManager(int i, Manager m) {
        managers.add(i, m);
    }
    
    /**
     * Removes the first occurrence of the specified manager in manager list.
     * @param m manager to be removed from the list, if present.
     * @return <code>true</code> if the list contained the specified manager.
     * @since 2.0
     */
    public static boolean removeManager(Manager m) {
        return managers.remove(m);
    }
    
    /**
     * Removes the manager at the specified position in manager list.
     * @param i the index of the manager to removed.
     * @exception ArrayIndexOutOfBoundsException index out of range
     *            (i &lt; 0 || i &gt;= CHS.getManagerCount()).
     * @since 2.0
     */
    public static void removeManager(int i) {
        managers.remove(i);
    }
    
    /**
     * Removes all managers from manager list.
     * @since 2.0
     */
    public static void removeAllManagers() {
        managers.clear();
    }
    
    /**
     * Returns the manager at the specified position in manager list.
     * @param i index of manager to return.
     * @exception ArrayIndexOutOfBoundsException index is out of range
     *            (i &lt; 0 || i &gt;= CSH.getManagerCount()).
     * @since 2.0
     */
    public static Manager getManager(int i) {
        return (Manager)managers.get(i);
    }
    
    /**
     * Returns array of managers registered to maintain dynamic CSH.
     *
     * @return an array containing the managers.
     * @exception ArrayStoreException the runtime type of a manager is not a
     *            <code>CSH.Manager</code>
     * @since 2.0
     */
    public static Manager[] getManagers() {
        return (Manager[])managers.toArray(new Manager[0]);
    }
    
    /**
     * Returns the number of managers registered to maintain dynamic CSH.
     *
     * @return  the number of managers.
     * @since 2.0
     */
    public static int getManagerCount() {
        return managers.size();
    }
    
    /**
     * Store HelpID String for an object.
     * @exception IllegalArgumentException comp is neither <code>Component</code> nor
     * <code>MenuItem</code>.
     */
    private static void _setHelpIDString(Object comp, String helpID) {
        
        if (comp instanceof JComponent) {
            
            // For JComponents just use client property
            ((JComponent)comp).putClientProperty("HelpID", helpID);
            
        } else if ((comp instanceof Component) || (comp instanceof MenuItem)) {
            // For MenuItems and Components we have an internal Hashtable of
            // components and their properties.
            
            // Initialize as necessary
            if (comps == null) {
                comps = new WeakHashMap(5);
            }
            
            // See if this component has already set some client properties
            // If so update.
            // If not then create the client props (as needed) and add to
            // the internal Hashtable of components and properties
            Hashtable clientProps = (Hashtable) comps.get(comp);
            if (clientProps != null) {
                if (helpID != null) {
                    clientProps.put("HelpID", helpID);
                } else {
                    clientProps.remove("HelpID");
                    if (clientProps.isEmpty()) {
                        comps.remove(comp);
                    }
                }
            } else {
                // Only create properties if there is a valid helpID
                if (helpID != null) {
                    clientProps = new Hashtable(2);
                    clientProps.put("HelpID", helpID);
                    comps.put(comp, clientProps);
                }
            }
            
        } else {
            throw new IllegalArgumentException("Invalid Component");
        }
    }
    
    /**
     * Returns the static HelpID for given object.
     * @exception IllegalArgumentException comp is neither <code>Component</code> nor
     * <code>MenuItem</code>.
     */
    private static String _getHelpIDString(Object comp) {
        String helpID = null;
        if (comp != null) {
            if (comp instanceof JComponent) {
                helpID = (String) ((JComponent)comp).getClientProperty("HelpID");
            } else if ((comp instanceof Component) || (comp instanceof MenuItem)) {
                if (comps != null) {
                    Hashtable clientProps = (Hashtable)comps.get(comp);
                    if (clientProps !=null) {
                        helpID = (String) clientProps.get("HelpID");
                    }
                }
            } else {
                throw new IllegalArgumentException("Invalid Component");
            }
        }
        return helpID;
    }
    
    
    /**
     * Returns the dynamic HelpID for an object.
     * @exception IllegalArgumentException comp is neither <code>Component</code> nor
     * <code>MenuItem</code>.
     */
    private static String _getHelpIDString(Object comp, AWTEvent evt) {
        String helpID = null;
        if (comp != null) {
            Manager managers[] = getManagers();
            for (int i = 0; i < managers.length; i++) {
                helpID = managers[i].getHelpIDString(comp, evt);
                if (helpID != null) {
                    return helpID;
                }
            }
        }
        return null;
    }
    
    /**
     * Returns ancestor for an object.
     * @exception IllegalArgumentException comp is neither <code>Component</code> nor
     * <code>MenuItem</code>.
     */
    private static Object getParent(Object comp) {
        
        if (comp == null) {
            return null;
        }
        
        Object parent = null;
        if (comp instanceof MenuComponent) {
            parent = ((MenuComponent)comp).getParent();
        } else if (comp instanceof JPopupMenu) {
            parent = ((JPopupMenu)comp).getInvoker();
        } else if (comp instanceof Component) {
            parent = ((Component)comp).getParent();
        } else {
            throw new IllegalArgumentException("Invalid Component");
        }
        
        if (parent == null && parents != null) {
            parent = parents.get(comp);
        }
        
        return parent;
    }
    
    
    /**
     * Sets the helpID for a Component.
     * If helpID is null this method removes the helpID from the component.
     * @exception IllegalArgumentException comp is neither <code>Component</code> nor
     */
    public static void setHelpIDString(Component comp, String helpID) {
        _setHelpIDString(comp, helpID);
    }
    
    /**
     * Sets the helpID for a MenuItem.
     * If helpID is null, this method removes the helpID from the component.
     * @exception IllegalArgumentException comp is neither <code>Component</code> nor
     */
    public static void setHelpIDString(MenuItem comp, String helpID) {
        _setHelpIDString(comp, helpID);
    }
    
    /**
     * Returns the dynamic HelpID for a object. The method passes the arguments
     * into all registered CSH manageres to obtain dynamic HelpID. If no manager
     * provides HelpID for the object, the static HelpID is returned or traverse
     * to the component's ancestors for help.
     * @exception IllegalArgumentException comp is neither <code>Component</code> nor
     * <code>MenuItem</code>.
     * @since 2.0
     */
    public static String getHelpIDString(Object comp, AWTEvent evt) {
        
        if (comp == null) {
            return null;
        }
        
        String helpID = _getHelpIDString(comp, evt);
        
        if (helpID == null) {
            helpID = _getHelpIDString(comp);
        }
        
        if (helpID == null) {
            helpID = getHelpIDString(getParent(comp), evt);
        }
        
        return helpID;
    }
    
    /**
     * Returns the static helpID for a component.
     * If the component doesn't have associated help, traverse the
     * component's ancestors for help.<p>
     *
     * This method calls <code>CSH.getHelpIDString(comp, null)</code>.
     *
     * @see #setHelpIDString(Component, String)
     */
    public static String getHelpIDString(Component comp) {
        return getHelpIDString(comp, null);
        
        /*
         if (comp == null) {
            return null;
        }
         
        String helpID = _getHelpIDString(comp);
         
        // return the helpID if it exists
        if (helpID != null) {
            return helpID;
        }
         
        // loop through the parents to try to find a valid helpID
        Component parent = null;
        if (comp instanceof JPopupMenu) {
            parent = ((JPopupMenu)comp).getInvoker();
        } else {
            parent = comp.getParent();
        }
         
        if (parent == null && parents != null) {
            parent = (Component)parents.get(comp);
        }
         
        if (parent != null) {
            return getHelpIDString(parent);
        } else {
            return null;
        }
         */
    }
    
    /**
     * Returns the static helpID for a MenuItem.
     * If the component doesn't have associated help, traverse the
     * component's ancestors for help.<p>
     *
     * This method calls <code>CSH.getHelpIDString(comp, null)</code>.
     *
     * @see #setHelpIDString(MenuItem, String)
     */
    public static String getHelpIDString(MenuItem comp) {
        return getHelpIDString(comp, null);
        
        /*
        String helpID = _getHelpIDString(comp);
        // return the helpID if it exists
        if (helpID != null) {
            return helpID;
        }
         
        // loop through the parents to try to find a valid helpID
        MenuContainer parent = comp.getParent();
        if (parent instanceof MenuItem) {
            return getHelpIDString((MenuItem)parent);
        } else {
            return null;
        }
         */
    }
    
    
    /**
     * Store HelpSet for an object.
     * @exception IllegalArgumentException comp is neither <code>Component</code> nor
     * <code>MenuItem</code>.
     */
    private static void _setHelpSet(Object comp, HelpSet hs) {
        
        if (comp instanceof JComponent) {
            
            // For JComponents just use client property
            ((JComponent)comp).putClientProperty("HelpSet", hs);
            
        } else if ((comp instanceof Component) || (comp instanceof MenuItem)) {
            
            // For MenuItem and Components we have an internal Hashtable of
            // components and their properties.
            
            // Initialize as necessary
            if (comps == null) {
                comps = new WeakHashMap(5);
            }
            
            // See if this component has already set some client properties
            // If so update.
            // If not then create the client props (as needed) and add to
            // the internal Hashtable of components and properties
            Hashtable clientProps = (Hashtable) comps.get(comp);
            if (clientProps != null) {
                if (hs != null) {
                    clientProps.put("HelpSet", hs);
                } else {
                    clientProps.remove("HelpSet");
                    if (clientProps.isEmpty()) {
                        comps.remove(comp);
                    }
                }
            } else {
                // Only create properties if there is a valid helpID
                if (hs != null) {
                    clientProps = new Hashtable(2);
                    clientProps.put("HelpSet", hs);
                    comps.put(comp, clientProps);
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid Component");
        }
    }
    
    /**
     * Returns the static HelpSet for an object.
     * @exception IllegalArgumentException comp is neither <code>Component</code> nor
     * <code>MenuItem</code>.
     */
    private static HelpSet _getHelpSet(Object comp) {
        HelpSet hs = null;
        if (comp != null) {
            if (comp instanceof JComponent) {
                hs = (HelpSet) ((JComponent)comp).getClientProperty("HelpSet");
            } else if ((comp instanceof Component) || (comp instanceof MenuItem)) {
                if (comps != null) {
                    Hashtable clientProps = (Hashtable)comps.get(comp);
                    if (clientProps !=null) {
                        hs = (HelpSet) clientProps.get("HelpSet");
                    }
                }
            } else {
                throw new IllegalArgumentException("Invalid Component");
            }
        }
        return hs;
    }
    
    /**
     * Returns the dynamic HelpSet for an object.
     * @exception IllegalArgumentException comp is neither <code>Component</code> nor
     * <code>MenuItem</code>.
     */
    private static HelpSet _getHelpSet(Object comp, AWTEvent evt) {
        HelpSet hs = null;
        if (comp != null) {
            Manager[] managers = getManagers();
            for (int i = 0; i < managers.length; i++) {
                hs = managers[i].getHelpSet(comp, evt);
                if (hs != null) {
                    return hs;
                }
            }
        }
        return hs;
    }
    
    /**
     * Sets the static HelpSet for a Component.
     * If HelpSet is null, this method removes the HelpSet
     * from the component.
     */
    public static void setHelpSet(Component comp, HelpSet hs) {
        _setHelpSet(comp, hs);
    }
    
    /**
     * Sets the static HelpSet for a MenuItem.
     * If HelpSet is null, this method removes the HelpSet
     * from the component.
     */
    public static void setHelpSet(MenuItem comp, HelpSet hs) {
        _setHelpSet(comp, hs);
    }
    
    /**
     * Returns the dynamic HelpSet for an object.
     * HelpSets are stored in conjunction with helpIDs. It is possible for a
     * object to have a helpID without a HelpSet, but a object cannot have a HelpSet
     * without a helpID.
     * If the component doesn't have an associated helpID, traverse the
     * component's ancestors for a helpID. If the componet has a helpID but
     * doesn't have a HelpSet, return null.
     *
     * @exception IllegalArgumentException comp is neither <code>Component</code> nor
     * <code>MenuItem</code>.
     *
     * @see getHelpID
     */
    public static HelpSet getHelpSet(Object comp, AWTEvent evt) {
        
        if (comp == null) {
            return null;
        }
        
        String helpID = _getHelpIDString(comp, evt);
        if (helpID == null) {
            helpID = _getHelpIDString(comp);
        }
        
        if (helpID != null) {
            HelpSet hs = _getHelpSet(comp, evt);
            if (hs == null) {
                hs = _getHelpSet(comp);
            }
            return hs;
        }
        
        return (getHelpSet(getParent(comp), evt));
    }
    
    /**
     * Returns the static HelpSet for a Component.
     * HelpSets are stored in conjunction with helpIDs. It is possible for a
     * Component to have
     * a helpID without a HelpSet, but a Component cannot have a HelpSet
     * without a helpID.
     * If the component doesn't have an associated helpID, traverse the
     * component's ancestors for a helpID. If the componet has a helpID but
     * doesn't have a HelpSet, return null.<p>
     *
     * This method calls <code>CSH.getHelpSet(comp, null)</code>.
     *
     * @see getHelpID
     * @exception IllegalArgumentException comp is not <code>Component</code
     */
    public static HelpSet getHelpSet(Component comp) {
        return getHelpSet(comp, null);
        
        /*
         if (comp == null) {
            return null;
        }
         
        String helpID = _getHelpIDString(comp);
         
        if (helpID != null) {
            HelpSet hs = _getHelpSet(comp);
            if (hs != null) {
                return hs;
            }
        }
         
        Component parent = null;
        if (comp instanceof JPopupMenu) {
            parent = ((JPopupMenu)comp).getInvoker();
        } else {
            parent = comp.getParent();
        }
         
        if (parent == null && parents != null) {
            parent = (Component)parents.get(comp);
        }
         
        if (parent != null) {
            return getHelpSet(parent);
        } else {
            return null;
        }
         */
    }
    
    
    /**
     * Returns the static HelpSet for a MenuItem.
     * HelpSets are stored in conjunction with helpIDs. It is possible for a
     * MenuItem to have a helpID without a HelpSet, but a MenuItem
     * cannot have a HelpSet without a helpID.
     * If the component doesn't have an associated helpID, traverse the
     * component's ancestors for a helpID. If the componet has a helpID, but
     * doesn't have a HelpSet return null.
     *
     * This method calls <code>CSH.getHelpIDString(comp, null)</code>.
     * @exception IllegalArgumentException comp is not <code>MenuItem</code>
     *
     * @see getHelpID
     */
    public static HelpSet getHelpSet(MenuItem comp) {
        return getHelpSet(comp, null);
        
        /*
         if (comp == null) {
            return null;
        }
         
        String helpID = _getHelpIDString(comp);
         
        if (helpID != null) {
            HelpSet hs = _getHelpSet(comp);
            if (hs != null) {
                return hs;
            }
        }
         
        MenuContainer parent = comp.getParent();
        if (parent instanceof MenuItem) {
            return getHelpSet((MenuItem)parent);
        } else {
            return null;
        }
         */
    }
    
    /**
     * Context Sensitive Event Tracking
     *
     * Creates a new EventDispatchThread from which to dispatch events. This
     * method returns when stopModal is invoked.
     *
     * @return Object The object on which the event occurred. Null if
     * cancelled on an undetermined object.
     */
    public static Object trackCSEvents() {
        MouseEvent e = getMouseEvent();
        if (e != null) {
            return getDeepestObjectAt(e.getSource(), e.getX(), e.getY());
        } else {
            return null;
        }
    }

    /*
     * Generic displayHelp for all CSH.Display* subclasses
     * 
     * @param hb The HelpBroker to display in. Can be null but hs and 
     *		and presentation must be supplies
     * @param hs The HelpSet to display in. Can be null if hb != null
     * @param presentation The Presentation class to display the content in
     * @param presentationName The named presentation to modify the 
     *		Presentation class with. In some Presenations this is also
     *		a "named" Presentation.
     * @param obj The object for which the help is displayed for
     * @param source The Window for focusOwner purposes
     * @param event The event that caused this action.
     */
    private static void displayHelp(HelpBroker hb, HelpSet hs, 
				    String presentation, 
				    String presentationName, Object obj, 
				    Object source, AWTEvent event) {

	Presentation pres=null;

	if (hb != null) {
	    // Start by setting the ownerWindow in the HelpBroker
	    if (hb instanceof DefaultHelpBroker) {
		((DefaultHelpBroker)hb).setActivationObject(source);
	    }
	} else {
	    // using a Presentation
	    // Get a Presentation
	    ClassLoader loader;
	    Class klass;
	    Class types[] = { HelpSet.class,
			      String.class};
	    Object args[] = { hs,
			      presentationName};		
	    try {
		loader = hs.getLoader();
		if (loader == null) {
		    klass = Class.forName(presentation);
		} else {
		    klass = loader.loadClass(presentation);
		}
		Method m = klass.getMethod("getPresentation", types);
		pres = (Presentation)m.invoke(null, args);
	    } catch (Exception ex) {
		throw new RuntimeException("error invoking presentation" );
	    }

	    if (pres == null) {
		return;
	    }

	    if (pres instanceof WindowPresentation) {
		((WindowPresentation)pres).setActivationObject(source);
	    }
	    if (pres instanceof Popup && obj instanceof Component) {
		((Popup)pres).setInvoker((Component)obj);
	    }
	}

	    
	// OK now do the CSH stuff
	String helpID = null;
	HelpSet objHS = null;
	helpID = CSH.getHelpIDString(obj, event);
	objHS = CSH.getHelpSet(obj, event);
	if (objHS == null) {
	    if (hb != null) {
		objHS = hb.getHelpSet();
	    } else {
		objHS = hs;
	    }
	}
	try {
	    ID id = ID.create(helpID, objHS);
	    if (id == null) {
		id = objHS.getHomeID();
	    }
	    if (hb != null) {
		hb.setCurrentID(id);
		hb.setDisplayed(true);
	    } else {
		pres.setCurrentID(id);
		pres.setDisplayed(true);
	    }
	} catch (Exception e2) {
	    e2.printStackTrace();
	}
    }

    /**
     * Context Sensitive Event Tracking
     *
     * Creates a new EventDispatchThread from which to dispatch events. This
     * method returns when stopModal is invoked.
     *
     * @return MouseEvent The mouse event occurred. Null if
     * cancelled on an undetermined object.
     */
    private static MouseEvent getMouseEvent() {
        // Should the cursor change to a quesiton mark here or
        // require the user to change the cursor externally to this method?
        // The problem is that each component can have it's own cursor.
        // For that reason it might be better to have the user change the
        // cusor rather than us.
        
        // To track context-sensitive events get the event queue and process
        // the events the same way EventDispatchThread does. Filter out
        // ContextSensitiveEvents SelectObject & Cancel (MouseDown & ???).
        // Note: This code only handles mouse events. Accessiblity might
        // require additional functionality or event trapping
        
        // If the eventQueue can't be retrieved, the thread gets interrupted,
        // or the thread isn't a instanceof EventDispatchThread then return
        // a null as we won't be able to trap events.
        try {
            if (EventQueue.isDispatchThread()) {
                EventQueue eq = null;
                
                // Find the eventQueue. If we can't get to it then just return
                // null since we won't be able to trap any events.
                
                try {
                    eq = Toolkit.getDefaultToolkit().getSystemEventQueue();
                } catch (Exception ee) {
                    debug(ee);
                }
                
                // Safe guard
                if (eq == null) {
                    return null;
                }
                
                int eventNumber = -1;
                
                // Process the events until an object has been selected or
                // the context-sensitive search has been canceled.
                while (true) {
                    // This is essentially the body of EventDispatchThread
                    // modified to trap context-senstive events and act
                    // appropriately
                    eventNumber++;
                    AWTEvent event = eq.getNextEvent();
                    Object src = event.getSource();
                    // can't call eq.dispatchEvent
                    // so I pasted it's body here
                    
                    // debug(event);
                    
                    // Not sure if I should suppress ActiveEvents or not
                    // Modal dialogs do. For now we will not suppress the
                    // ActiveEvent events
                    
                    if (event instanceof ActiveEvent) {
                        ((ActiveEvent)event).dispatch();
                        continue;
                    }
                    
                    if (src instanceof Component) {
                        // Trap the context-sensitive events here
                        if (event instanceof KeyEvent) {
                            KeyEvent e = (KeyEvent) event;
                            // if this is the cancel key then exit
                            // otherwise pass all other keys up
                            if (e.getKeyCode() == KeyEvent.VK_CANCEL ||
                            e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                                e.consume();
                                return null;
                            } else {
                                e.consume();
                                // dispatchEvent(event);
                            }
                        } else if (event instanceof MouseEvent) {
                            MouseEvent e = (MouseEvent) event;
                            int eID = e.getID();
                            if ((eID == MouseEvent.MOUSE_CLICKED ||
                            eID == MouseEvent.MOUSE_PRESSED ||
                            eID == MouseEvent.MOUSE_RELEASED) &&
                            SwingUtilities.isLeftMouseButton(e)) {
                                if (eID == MouseEvent.MOUSE_CLICKED) {
                                    if (eventNumber == 0) {
                                        dispatchEvent(event);
                                        continue;
                                    }
                                }
                                e.consume();
                                return e;
                            } else {
                                e.consume();
                            }
                        } else {
                            dispatchEvent(event);
                        }
                    } else if (src instanceof MenuComponent) {
                        if (event instanceof InputEvent) {
                            ((InputEvent)event).consume();
                        }
                    } else {
                        System.err.println("unable to dispatch event: " + event);
                    }
                }
            }
        } catch(InterruptedException e) {
            debug(e);
        }
        debug("Fall Through code");
        return null;
    }
    
    private static void dispatchEvent(AWTEvent event) {
        Object src = event.getSource();
        if (event instanceof ActiveEvent) {
            // This could become the sole method of dispatching in time.
            ((ActiveEvent)event).dispatch();
        } else if (src instanceof Component) {
            ((Component)src).dispatchEvent(event);
        } else if (src instanceof MenuComponent) {
            ((MenuComponent)src).dispatchEvent(event);
        } else {
            System.err.println("unable to dispatch event: " + event);
        }
    }
    
    /**
     * Gets the higest visible component in a ancestor hierarchy at
     * specific x,y coordinates
     */
    private static Object getDeepestObjectAt(Object parent, int x, int y) {
        if (parent instanceof Container) {
            Container cont = (Container)parent;
            // use a copy of 1.3 Container.findComponentAt
            Component child = findComponentAt(cont, cont.getWidth(), cont.getHeight(), x, y);
            if (child != null && child != cont) {
                if (child instanceof JRootPane) {
                    JLayeredPane lp = ((JRootPane)child).getLayeredPane();
                    Rectangle b = lp.getBounds();
                    child = (Component)getDeepestObjectAt(lp, x - b.x, y - b.y);
                }
                if (child != null) {
                    return child;
                }
            }
        }
        // if the parent is not a Container then it might be a MenuItem.
        // But even if it isn't a MenuItem just return the parent because
        // that's a close as we can come.
        return parent;
    }
    
    private static Component findComponentAt(Container cont, int width, int height, int x, int y) {
        synchronized (cont.getTreeLock()) {
            
            if (!((x >= 0) && (x < width) && (y >= 0) && (y < height) && cont.isVisible() && cont.isEnabled())) {
                return null;
            }
            
            Component[] component = cont.getComponents();
            int ncomponents = cont.getComponentCount();
            
            // Two passes: see comment in sun.awt.SunGraphicsCallback
            for (int i = 0 ; i < ncomponents ; i++) {
                Component comp = component[i];
                Rectangle rect = null;
                
                if (comp instanceof CellRendererPane) {
                    Component c = getComponentAt((CellRendererPane)comp, x, y);
                    if (c != null) {
                        rect = getRectangleAt((CellRendererPane)comp, x, y);
                        comp = c;
                    }
                }
                
                if (comp != null && !comp.isLightweight()) {
                    if (rect == null || rect.width == 0 || rect.height == 0) {
                        rect = comp.getBounds();
                    }
                    if (comp instanceof Container) {
                        comp = findComponentAt((Container)comp, rect.width, rect.height, x - rect.x, y - rect.y);
                    } else {
                        comp = comp.getComponentAt(x - rect.x, y - rect.y);
                    }
                    if (comp != null && comp.isVisible() && comp.isEnabled()) {
                        return comp;
                    }
                }
            }
            
            for (int i = 0 ; i < ncomponents ; i++) {
                Component comp = component[i];
                Rectangle rect = null;
                
                if (comp instanceof CellRendererPane) {
                    Component c = getComponentAt((CellRendererPane)comp, x, y);
                    if (c != null) {
                        rect = getRectangleAt((CellRendererPane)comp, x, y);
                        comp = c;
                    }
                }
                
                if (comp != null && comp.isLightweight()) {
                    if (rect == null || rect.width == 0 || rect.height == 0) {
                        rect = comp.getBounds();
                    }
                    if (comp instanceof Container) {
                        comp = findComponentAt((Container)comp, rect.width, rect.height, x - rect.x, y - rect.y);
                    } else {
                        comp = comp.getComponentAt(x - rect.x, y - rect.y);
                    }
                    if (comp != null && comp.isVisible() && comp.isEnabled()) {
                        return comp;
                    }
                }
            }
            return cont;
        }
    }
    
    /**
     * Returns the Rectangle enclosing component part that the component
     * provided by renderer will be draw into.
     */
    private static Rectangle getRectangleAt(CellRendererPane cont, int x, int y) {
        Rectangle rect = null;
        Container c = cont.getParent();
        // I can process only this four components at present time
        if (c instanceof JTable) {
            rect = getRectangleAt((JTable)c, x, y);
        } else if (c instanceof JTableHeader) {
            rect = getRectangleAt((JTableHeader)c, x, y);
        } else if (c instanceof JTree) {
            rect = getRectangleAt((JTree)c, x, y);
        } else if (c instanceof JList) {
            rect = getRectangleAt((JList)c, x, y);
        }
        return rect;
    }
    
    /**
     * Returns the Component provided by Renderer at x, y coordinates.
     */
    private static Component getComponentAt(CellRendererPane cont, int x, int y) {
        Component comp = null;
        Container c = cont.getParent();
        // I can process only this four components at present time
        if (c instanceof JTable) {
            comp = getComponentAt((JTable)c, x, y);
        } else if (c instanceof JTableHeader) {
            comp = getComponentAt((JTableHeader)c, x, y);
        } else if (c instanceof JTree) {
            comp = getComponentAt((JTree)c, x, y);
        } else if (c instanceof JList) {
            comp = getComponentAt((JList)c, x, y);
        }
        
        // store reference from comp to CellRendererPane
        // It is needed for backtrack searching of HelpSet and HelpID
        // in getHelpSet() and getHelpIDString().
        if (comp != null) {
            if (parents == null) {
                // WeakHashMap of WeakReferences
                parents = new WeakHashMap(4) {
                    public Object put(Object key, Object value) {
                        return super.put(key, new WeakReference(value));
                    }
                    public Object get(Object key) {
                        WeakReference wr = (WeakReference)super.get(key);
                        if (wr != null) {
                            return wr.get();
                        } else {
                            return null;
                        }
                    }
                };
            }
            parents.put(comp, cont);
        }
        return comp;
    }
    
    private static Rectangle getRectangleAt(JTableHeader header, int x, int y) {
        Rectangle rect = null;
        try {
            int column = header.columnAtPoint(new Point(x, y));
            rect = header.getHeaderRect(column);
        } catch (Exception e) {
        }
        return rect;
    }
    
    private static Component getComponentAt(JTableHeader header, int x, int y) {
        try {
            
            if (!(header.contains(x, y) && header.isVisible() && header.isEnabled())) {
                return null;
            }
            
            TableColumnModel columnModel = header.getColumnModel();
            int columnIndex = columnModel.getColumnIndexAtX(x);
            TableColumn column = columnModel.getColumn(columnIndex);
            
            TableCellRenderer renderer = column.getHeaderRenderer();
            if (renderer == null) {
                renderer = header.getDefaultRenderer();
            }
            
            return renderer.getTableCellRendererComponent(
            header.getTable(), column.getHeaderValue(), false, false, -1, columnIndex);
            
        } catch (Exception e) {
            // NullPointerException in case of (header == null) or (columnModel == null)
            // ArrayIndexOutOfBoundsException from getColumn(columnIndex)
        }
        return null;
    }
    
    private static Rectangle getRectangleAt(JTable table, int x, int y) {
        Rectangle rect = null;
        try {
            Point point = new Point(x, y);
            int row = table.rowAtPoint(point);
            int column = table.columnAtPoint(point);
            rect = table.getCellRect(row, column, true);
        } catch (Exception e) {
        }
        return rect;
    }
    
    private static Component getComponentAt(JTable table, int x, int y) {
        try {
            
            if (!(table.contains(x, y) && table.isVisible() && table.isEnabled())) {
                return null;
            }
            
            Point point = new Point(x, y);
            int row = table.rowAtPoint(point);
            int column = table.columnAtPoint(point);
            
            if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) {
                // Pointed component is provided by TableCellEditor. Editor
                // component is part of component hierarchy so it is checked
                // directly in loop in findComponentAt()
                // comp = table.getEditorComponent();
                return null;
            }
            
            TableCellRenderer renderer = table.getCellRenderer(row, column);
            return table.prepareRenderer(renderer, row, column);
            
        } catch (Exception e) {
        }
        return null;
    }
    
    private static Rectangle getRectangleAt(JTree tree, int x, int y) {
        Rectangle rect = null;
        try {
            TreePath path = tree.getPathForLocation(x, y);
            rect = tree.getPathBounds(path);
        } catch (Exception e) {
        }
        return rect;
    }
    
    private static Component getComponentAt(JTree tree, int x, int y) {
        try {
            
            TreePath path = tree.getPathForLocation(x, y);
            
            if (tree.isEditing() && tree.getSelectionPath() == path) {
                return null;
            }
            
            int row = tree.getRowForPath(path);
            Object value = path.getLastPathComponent();
            boolean isSelected = tree.isRowSelected(row);
            boolean isExpanded = tree.isExpanded(path);
            boolean isLeaf = tree.getModel().isLeaf(value);
            boolean hasFocus= tree.hasFocus() && tree.getLeadSelectionRow() == row;
            
            return tree.getCellRenderer().getTreeCellRendererComponent(
            tree, value, isSelected, isExpanded, isLeaf, row, hasFocus);
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private static Rectangle getRectangleAt(JList list, int x, int y) {
        Rectangle rect = null;
        try {
            int index = list.locationToIndex(new Point(x, y));
            rect = list.getCellBounds(index, index);
        } catch (Exception e) {
        }
        return rect;
    }
    
    private static Component getComponentAt(JList list, int x, int y) {
        try {
            
            int index = list.locationToIndex(new Point(x, y));
            Object value = list.getModel().getElementAt(index);
            boolean isSelected = list.isSelectedIndex(index);
            boolean hasFocus = list.hasFocus() && list.getLeadSelectionIndex() == index;
            
            return list.getCellRenderer().getListCellRendererComponent(
            list, value, index, isSelected, hasFocus);
        } catch (Exception e) {
            return null;
        }
    }
    
    /*
    private static Rectangle getRectangleAt(JComboBox combo, int x, int y) {
        return combo.getBounds();
    }
     
    private static Component getComponentAt(JComboBox combo, int x, int y) {
        try{
            if (combo.isEditable()) {
                return null;
            }
     
            Object value = combo.getSelectedItem();
            boolean hasFocus = combo.hasFocus() && !combo.isPopupVisible();
     
            // How to get JList for getListCellRendererComponent()?
            // That's the question...
            JList list = new JList();
            return combo.getRenderer().getListCellRendererComponent(
            list, value, -1, hasFocus, false);
        } catch (Exception e) {
            return null;
        }
    }
     */
    
    /**
     * An ActionListener that displays the help of the
     * object that currently has focus. This method is used
     * to enable HelpKey action listening for components other than
     * the RootPane. This listener determines if the
     * object with the current focus has a helpID. If it does, the helpID
     * is displayed,
     * otherwise the helpID on the action's source is displayed (if one exists).
     *
     * @see HelpBroker.enableHelpKey
     */
    public static class DisplayHelpFromFocus implements ActionListener {
        
        private HelpBroker hb = null;
	private HelpSet hs = null;
	private String presentation = null;
	private String presentationName = null;
         
        public DisplayHelpFromFocus(HelpBroker hb) {
            if (hb == null) {
                throw new NullPointerException("hb");
            }
            this.hb = hb;
        }

	/**
	 * Create a DisplayHelpFromFocus actionListener for a given
	 * HelpSet. Display the results in specific Presentation of given
	 * PresentationName.
	 *
	 * @param hs A valid HelpSet.
	 * @param presention A valid javax.help.Presentation class. Throws
	 *	an IllegalArgumentException if the presentation class cannot
	 *	instantiated.
	 * @param name The name of the presentation. This will retrieve the
	 *	presentation details from the HelpSet hs if one exists. For
	 *	some Presentation this name will also indicate the "named"
	 *	Presentation to display the information in.
	 * @throws NullPointerException if hs is Null.
	 * @throws IllegalArgumentException if presentation is not valid.
	 */
	public DisplayHelpFromFocus(HelpSet hs,
				    String presentation,
				    String presentationName) {
	    if (hs == null) {
		throw new NullPointerException("hs");
	    }

	    ClassLoader loader;
	    Class klass;

	    try {
		loader = hs.getLoader();
		if (loader == null) {
		    klass = Class.forName(presentation);
		} else {
		    klass = loader.loadClass(presentation);
		}
	    } catch (Exception ex) {
		throw new IllegalArgumentException(presentation + "presentation  invalid");
	    }

	    this.presentation = presentation;
	    this.presentationName = presentationName;
	    this.hs = hs;
	}

        
        public void actionPerformed(ActionEvent e) {
            
            Component src = (Component) e.getSource();
            // Start by setting the ownerWindow in the HelpBroker
            if (hb instanceof DefaultHelpBroker) {
                ((DefaultHelpBroker)hb).setActivationObject(src);
            }
            
            Component comp = CSH.findFocusOwner(src);
            
            debug("focusOwner:"+comp);
            
            if (comp == null) {
                comp = src;
            }
            
	    displayHelp(hb, hs, presentation, presentationName, comp, comp, e);
	}	    
    }
    
    
    /**
     * Returns the popup menu which is at the root of the menu system
     * for this popup menu.
     *
     * @return the topmost grandparent <code>JPopupMenu</code>
     */
    private static JPopupMenu getRootPopupMenu(JPopupMenu popup) {
        while((popup != null) &&
        (popup.getInvoker() instanceof JMenu) &&
        (popup.getInvoker().getParent() instanceof JPopupMenu)
        ) {
            popup = (JPopupMenu) popup.getInvoker().getParent();
        }
        return popup;
    }
    
    /**
     * Returns the deepest focused Component or the deepest JPopupMenu
     * or the deepest armed MenuItem from JPopupMenu hierarchy.
     */
    private static Component findFocusOwner(JPopupMenu popup) {
        if (popup == null) {
            return null;
        }
        synchronized (popup.getTreeLock()) {
            if (!popup.isVisible()) {
                return null;
            }
            Component comp = null;
            for (int i = 0, n = popup.getComponentCount(); i < n; i++) {
                Component c = popup.getComponent(i);
                if (c.hasFocus()) {
                    comp = c;
                    break;
                }
                if (c instanceof JMenu && ((JMenu)c).isPopupMenuVisible()) {
                    comp = c;
                }
                if (c instanceof JMenuItem && ((JMenuItem)c).isArmed()) {
                    comp = c;
                }
            }
            if (comp instanceof JMenu) {
                comp = findFocusOwner(((JMenu)comp).getPopupMenu());
            }
            if (comp != null) {
                return comp;
            }
        }
        return popup;
    }
    
    /**
     * Returns the child component which has focus with respects
     * of PopupMenu visibility.
     */
    private static Component findFocusOwner(Component c) {
        synchronized (c.getTreeLock()) {
            
            if (c instanceof JPopupMenu) {
                return findFocusOwner(getRootPopupMenu((JPopupMenu)c));
            }
            
            if (c.hasFocus()) {
                return c;
            }
            
            if (c instanceof Container) {
                for (int i = 0, n = ((Container)c).getComponentCount(); i < n; i++) {
                    Component focusOwner = findFocusOwner(((Container)c).getComponent(i));
                    if (focusOwner != null) {
                        return focusOwner;
                    }
                }
            }
            return null;  // Component doesn't have hasFocus().
        }
    }
    
    /**
     * An ActionListener that displays help on a
     * selected object after tracking context-sensitive events.
     * It is normally activated
     * from a button. It uses CSH.trackingCSEvents to track context-sensitive
     * events and when an object is selected it gets
     * the helpID for the object and displays the helpID in the help viewer.
     */
    public static class DisplayHelpAfterTracking implements ActionListener {
        
        private HelpBroker hb = null;
	private HelpSet hs = null;
	private String presentation = null;
	private String presentationName = null;
        
        public DisplayHelpAfterTracking(HelpBroker hb) {
            if (hb == null) {
                throw new NullPointerException("hb");
            }
            this.hb = hb;
        }
        
	/**
	 * Create a DisplayHelpAfterTracking actionListener for a given
	 * HelpSet. Display the results in specific Presentation of given
	 * PresentationName.
	 *
	 * @param hs A valid HelpSet.
	 * @param presention A valid javax.help.Presentation class. Throws
	 *	an IllegalArgumentException if the presentation class cannot
	 *	instantiated.
	 * @param name The name of the presentation. This will retrieve the
	 *	presentation details from the HelpSet hs if one exists. For
	 *	some Presentation this name will also indicate the "named"
	 *	Presentation to display the information in.
	 * @throws NullPointerException if hs is Null.
	 * @throws IllegalArgumentException if presentation is not valid.
	 */
	public DisplayHelpAfterTracking(HelpSet hs,
					String presentation,
					String presentationName) {
	    if (hs == null) {
		throw new NullPointerException("hs");
	    }

	    ClassLoader loader;
	    Class klass;

	    try {
		loader = hs.getLoader();
		if (loader == null) {
		    klass = Class.forName(presentation);
		} else {
		    klass = loader.loadClass(presentation);
		}
	    } catch (Exception ex) {
		throw new IllegalArgumentException(presentation + "presentation  invalid");
	    }

	    this.presentation = presentation;
	    this.presentationName = presentationName;
	    this.hs = hs;
	}

        public void actionPerformed(ActionEvent e) {
            Cursor onItemCursor, oldCursor;
            
            // Make sure that LAF is installed.
            // It is necessery for UIManager.get("HelpOnItemCursor");
            SwingHelpUtilities.installUIDefaults();
            
            // Get the onItemCursor
            onItemCursor = (Cursor) UIManager.get("HelpOnItemCursor");
            if (onItemCursor == null) {
                return;
            }
            
            // change all the cursors on all windows
            Vector topComponents = null;
            cursors = null;
            
            if (onItemCursor != null) {
                cursors = new Hashtable();
                topComponents = getTopContainers(e.getSource());
                Enumeration enum1 = topComponents.elements();
                while (enum1.hasMoreElements()) {
                    setAndStoreCursors((Container)enum1.nextElement(), onItemCursor);
                }
            }
            
            MouseEvent event = CSH.getMouseEvent();
            debug("CSH.getMouseEvent() >>> "+event);
            
            if (event != null) {
                
                Object obj = CSH.getDeepestObjectAt(event.getSource(), event.getX(), event.getY());
                debug("CSH.getDeepestObjectAt() >>> "+obj);
                
                if (obj != null) {

                    displayHelp(hb, hs, presentation, presentationName,
				obj, e.getSource(), event);
		}
            }
            
            // restore the old cursors
            if (topComponents != null) {
                Enumeration containers = topComponents.elements();
                while (containers.hasMoreElements()) {
                    resetAndRestoreCursors((Container)containers.nextElement());
                }
            }
            cursors = null;
        }
        
        private Hashtable cursors;
        
        /*
         * Get all top level containers to change it's cursors
         */
        private static Vector getTopContainers(Object source) {
            // This method is used to obtain all top level components of application
            // for which the changing of cursor to question mark is wanted.
            // Method Frame.getFrames() is used to get list of Frames and
            // Frame.getOwnedWindows() method on elements of the list
            // returns all Windows, Dialogs etc. It works correctly in application.
            // Problem is in applets. There is no way how to get reference to applets
            // from elsewhere than applet itself. So, if request for CSH (this means
            // pressing help button or select help menu item) does't come from component
            // in a Applet, cursor for applets is not changed to question mark. Only for
            // Frames, Windows and Dialogs is cursor changed properly.
            
            Vector containers = new Vector();
            Component topComponent = null;
            topComponent = getRoot(source);
            if (topComponent instanceof Applet) {
                try {
                    Enumeration applets = ((Applet)topComponent).getAppletContext().getApplets();
                    while (applets.hasMoreElements()) {
                        containers.add(applets.nextElement());
                    }
                } catch (NullPointerException npe) {
                    containers.add(topComponent);
                }
            }
            Frame frames[] = Frame.getFrames();
            for (int i = 0; i < frames.length; i++) {
                Window[] windows = frames[i].getOwnedWindows();
                for (int j = 0; j < windows.length; j++) {
                    containers.add(windows[j]);
                }
                if (!containers.contains(frames[i])) {
                    containers.add(frames[i]);
                }
            }
            return containers;
        }
        
        
        private static Component getRoot(Object comp) {
            Object parent = comp;
            while (parent != null) {
                comp = parent;
                if (comp instanceof MenuComponent) {
                    parent = ((MenuComponent)comp).getParent();
                } else if (comp instanceof Component) {
                    if (comp instanceof Window) {
                        break;
                    }
                    if (comp instanceof Applet) {
                        break;
                    }
                    parent = ((Component)comp).getParent();
                } else {
                    break;
                }
            }
            if (comp instanceof Component) {
                return ((Component)comp);
            }
            return null;
        }
        
        
        /*
         * Set the cursor for a component and its children.
         * Store the old cursors for future resetting
         */
        private void setAndStoreCursors(Component comp, Cursor cursor) {
            if (comp == null) {
                return;
            }
            Cursor compCursor = comp.getCursor();
            if (compCursor != cursor) {
                cursors.put(comp, compCursor);
                debug("set cursor on " + comp);
                comp.setCursor(cursor);
            }
            if (comp instanceof Container) {
                Component component[] = ((Container)comp).getComponents();
                for (int i = 0 ; i < component.length; i++) {
                    setAndStoreCursors(component[i], cursor);
                }
            }
        }
        
        /*
         * Actually restore the cursor for a component and its children
         */
        private void resetAndRestoreCursors(Component comp) {
            if (comp == null) {
                return;
            }
            Cursor oldCursor = (Cursor) cursors.get(comp);
            if (oldCursor != null) {
                debug("restored cursor " + oldCursor + " on " + comp);
                comp.setCursor(oldCursor);
            }
            if (comp instanceof Container) {
                Component component[] = ((Container)comp).getComponents();
                for (int i = 0 ; i < component.length; i++) {
                    resetAndRestoreCursors(component[i]);
                }
            }
        }
    }
    
    /**
     * An ActionListener that
     * gets the helpID for the action source and displays the helpID in the
     * help viewer.
     *
     * @see HelpBroker.enableHelp
     */
    public static class DisplayHelpFromSource implements ActionListener {
        
        private HelpBroker hb;
	private HelpSet hs = null;
	private String presentation = null;
	private String presentationName = null;
        
        public DisplayHelpFromSource(HelpBroker hb) {
            if (hb == null) {
                throw new NullPointerException("hb");
            }
            this.hb = hb;
        }
        
	/**
	 * Create a DisplayHelpFromSource actionListener for a given
	 * HelpSet. Display the results in specific Presentation of given
	 * PresentationName.
	 *
	 * @param hs A valid HelpSet.
	 * @param presention A valid javax.help.Presentation class. Throws
	 *	an IllegalArgumentException if the presentation class cannot
	 *	instantiated.
	 * @param name The name of the presentation. This will retrieve the
	 *	presentation details from the HelpSet hs if one exists. For
	 *	some Presentation this name will also indicate the "named"
	 *	Presentation to display the information in.
	 * @throws NullPointerException if hs is Null.
	 * @throws IllegalArgumentException if presentation is not valid.
	 */
	public DisplayHelpFromSource(HelpSet hs,
				    String presentation,
				    String presentationName) {
	    if (hs == null) {
		throw new NullPointerException("hs");
	    }

	    ClassLoader loader;
	    Class klass;

	    try {
		loader = hs.getLoader();
		if (loader == null) {
		    klass = Class.forName(presentation);
		} else {
		    klass = loader.loadClass(presentation);
		}
	    } catch (Exception ex) {
		throw new IllegalArgumentException(presentation + "presentation  invalid");
	    }

	    this.presentation = presentation;
	    this.presentationName = presentationName;
	    this.hs = hs;
	}

        
        public void actionPerformed(ActionEvent e) {
	    Object source = e.getSource();
	    displayHelp(hb, hs, presentation, presentationName, 
			source, source, e);
            
        }
    }
    
    /**
     * CSH Manager Interface to support dynamic <code>HelpSet</code> and <code>ID</code>
     * for object. CSH is allowed only for <code>Component</code> and <code>MenuItem</code>.
     *
     * @since 2.0
     */
    public interface Manager {
        /**
         * Returns HelpSet of the object depending on the event.
         * @param comp The object to get <code>HelpSet</code> for it. Only <code>Component</code>
         * and <code>MenuItem</code> is allowed.
         * @param e The event by which dynamic CSH was invoked. It can be <code>MouseEvent</code>
         * in case of window level CSH, <code>ActionEvent</code> in case of field level CSH or
         * <code>null</code> otherwise.
         * @return A HelpSet for given object or null if no HelpSet is assigned to object
         * @see CSH.DisplayHelpAfterTracking
         * @see CSH.DisplayHelpFromFocus
         * @see CSH.DisplayHelpFromSource
         */
        public HelpSet getHelpSet(Object comp, AWTEvent e);

        /**
         * Returns String represent Map.ID of the object depending on the event.
         * @param comp The object to get <code>Map.ID</code> for it. Only <code>Component</code>
         * and <code>MenuItem</code> is allowed.
         * @param e The event by which dynamic CSH was invoked. It can be <code>MouseEvent</code>
         * in case of window level CSH, <code>ActionEvent</code> in case of field level CSH or
         * <code>null</code> otherwise.
         * @return A Map.ID string for given object or null if no Map.ID is assigned to object
         * @see CSH.DisplayHelpAfterTracking
         * @see CSH.DisplayHelpFromFocus
         * @see CSH.DisplayHelpFromSource
         */
        public String getHelpIDString(Object comp, AWTEvent e);
    }
    
    /**
     * Debugging code...
     */
    
    private static final boolean debug = false;
    private static void debug(Object msg) {
        if (debug) {
            System.err.println("CSH: "+msg);
        }
    }
    
}
