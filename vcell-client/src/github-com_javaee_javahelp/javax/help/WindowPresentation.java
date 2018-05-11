/*
 * @(#)WindowPresentation.java	1.22 06/10/30
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

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import javax.swing.ImageIcon;

/**
 * Window Presentation is an abstract class providing a generic interface for
 * the development of Window Presentations. Each implementation of 
 * Presentation will need to override the static method getPresentation
 * according to it's own needs. 
 *
 * WindowPresentation implements several generic methods required in all 
 * window presentations. Includes the ability to handle modal and non-modal
 * activation of the help window.
 *
 * @author Roger D.Brinkley
 * @version	1.22	10/30/06
 * @since 2.0
 *
 * @see javax.help.HelpSet
 * @see javax.help.JHelpNavigator
 * @see javax.help.HelpVisitListener
 */

public abstract class WindowPresentation extends Presentation {

    private HelpSet.Presentation hsPres=null;
    private JFrame frame = null;
    private JHelp jhelp = null;
    private JDialog dialog = null;
    private Window ownerWindow = null;
    private boolean modallyActivated = false;
    private Point location = null;
    private String title = null;
    private Image image = null;
    private String currentView = null;
    private boolean viewDisplayed = true;
    private boolean toolbarDisplayed = true;
    private boolean destroyOnExit = false;
    private boolean titleFromDocument = false;
    private WindowPropertyChangeListener propertyChangeListener = null;
    private int screen = 0;

    public WindowPresentation (HelpSet hs) {
	setHelpSet(hs);
    }

    /**
     * Set the Presentation attributes specific to WindowPresentations from a 
     * named presentation in a HelpSet.
     * 
     * @params hsPres - the HelpSet.Presentation to retrieve the presentation 
     *                  information from
     * 
     * @see HelpSet.Presentation
     */
    public void setHelpSetPresentation (HelpSet.Presentation hsPres) {
	debug("setHelpSetPrsentation");
	if (hsPres == null) {
	    return;
	}

	// make sure the underlying presentation attributes are set
	super.setHelpSetPresentation(hsPres);

	// get the presentation location
	Point location = hsPres.getLocation();
	if (location != null) {
	    setLocation(location);
	}

	// get the Title
	String title = hsPres.getTitle();
	if (title != null) {
	    setTitle(title);
	}

	// get the imageID
	javax.help.Map.ID imageID = hsPres.getImageID();
	if (imageID != null) {
	    ImageIcon icon = null;
	    try {
		javax.help.Map map = getHelpSet().getCombinedMap();
		URL url = map.getURLFromID(imageID);
		icon = new ImageIcon(url);
		image = icon.getImage();
	    } catch (Exception e) {
	    }
	}

	if (hsPres.isToolbar()) {
	    setToolbarDisplayed(true);
	}

	if (hsPres.isViewDisplayed()) {
	    setViewDisplayed(true);
	}
	
	this.hsPres = hsPres;
    }

    /**
     * Return the HelpSet.Presentation if one was set
     * @returns HelpSet.Presentation - the HelpSet.Presentation used in this
     *		Presentation.
     * 
     * @see HelpSet.Presentation
     */
    public HelpSet.Presentation getHelpSetPresentation() {
	return hsPres;
    }

    /**
     * Get the activation window. 
     *
     * @returns Window - the activation window if activatated from a modal
     *                   modal dialog, otherwise null.
     */
    public Window getActivationWindow() {
	debug("getActivationWindow");
	return ownerWindow;
    }

    /**
     * Set the activation window. If the window is an instance of a
     * Dialog and the is modal, modallyActivated help is set to true and 
     * ownerDialog is set to the window. In all other instances 
     * modallyActivated is set to false and ownerDialog is set to null.
     * @param window the activating window
     */
    public void setActivationWindow(Window window) {
	debug("setActivationWindow");
	if (window != null && window instanceof Dialog) {
	    Dialog tmpDialog = (Dialog) window;
	    if (tmpDialog.isModal()) {
		ownerWindow = window;
		modallyActivated = true;
	    } else {
		ownerWindow = null;
		modallyActivated = false;
	    }
	} else {
	    ownerWindow = null;
	    modallyActivated = false;
	}
    }
		    
    /**
     * Set the activation window from given Component or MenuItem. It find Window component
     * in the component tree from given Component or MenuItem end call 
     * <pre>setActivationWindow</pre>.
     * @parem comp the activation Component or MenuItem
     * @since 2.0
     *
     * @see setActivationWindow
     */
    public void setActivationObject(Object comp) {
	debug("setActivationObject");
        while (comp instanceof MenuComponent) {
            comp = ((MenuComponent)comp).getParent();
        }
        
        Window owner = null;
        if (comp instanceof Frame) {
            owner = (Window)comp;
        } else if (comp instanceof Component) {
            owner = SwingUtilities.windowForComponent((Component)comp);
        }
        
        setActivationWindow(owner);
    }
    
    /**
     * Determines the current navigator.
     */
    public String getCurrentView() {
	debug("getCurrentView");
	// always use the current view if the jhelp exists.
	if (jhelp != null) {
	    currentView = jhelp.getCurrentNavigator().getNavigatorName();
	}
	return currentView;
    }


    /**
     * Set the currentView to the navigator with the same 
     * name as the <tt>name</tt> parameter.
     *
     * @param name The name of the navigator to set as the 
     * current view. If nav is null or not a valid Navigator 
     * in this WindowPresentation then an 
     * IllegalArgumentException is thrown.
     * @throws IllegalArgumentException if nav is null or not a valid Navigator.
     */
    public void setCurrentView(String name) {
	debug("setCurrentView");
	// if the jhelp already exists then set the currentview
	if (jhelp != null) {
	    
	    JHelpNavigator nav = getNavigatorByName(name);
	    
	    if (nav == null) {
		throw new IllegalArgumentException("Invalid view name");
	    }
	    jhelp.setCurrentNavigator(nav);
	} else {
	    // jhelp didn't exist so make sure view is in HelpSet
	    HelpSet hs = getHelpSet();
	    NavigatorView view = hs.getNavigatorView(name);
	    if (view == null) {
		throw new IllegalArgumentException("Invalid view name");
	    }
	}
	currentView = name;
    }

    /*
     * Internal method to return a Navigator by name from a jhelp
     */
    private JHelpNavigator getNavigatorByName(String name) {
	JHelpNavigator nav = null;
	if (jhelp != null) {
	    for (Enumeration e = jhelp.getHelpNavigators();
		 e.hasMoreElements(); ) {
		nav = (JHelpNavigator) e.nextElement();
		if (nav.getNavigatorName().equals(name)) {
		    break;
		}
		nav = null;
	    }
	}
	return nav;
    }


    /**
     * Determines if the presentation should be distroyed on exit
     */
    public boolean isDestroyedOnExit() {
	debug("isDestoryedOnExit");
	return destroyOnExit;
    }

    /**
     * Destory the window on exit
     */
    public void setDestroyOnExit(boolean destroy) {
	debug("setDestoryOnExit");
	destroyOnExit = destroy;
    }

    /**
     * Destroy this object. Implementation of WindowPresentation that
     * maintian a list of objects should override this method and call 
     * super.destroy to clear up the WindowPresentation internal fields.
     */
    public void destroy() {
	frame = null;
	jhelp = null;
	dialog = null;
	ownerWindow = null;
	location = null;
	title = null;
	currentView = null;
	propertyChangeListener = null;
	screen = 0;
    }

    /**
     * Changes the HelpSet for this presentation.
     * @param hs The HelpSet to set for this presentation. 
     * A null hs is valid parameter.
     */
    public void setHelpSet(HelpSet hs) {
	debug("setHelpSet");

	HelpSet helpset = super.getHelpSet();
	// If we already have a model check if the HelpSet has changed.
	// If so change the model
	// This could be made smarter to cache the helpmodels per HelpSet
	if (hs != null && helpset != hs) {
	    super.setHelpSet(hs);
	    if (jhelp != null) {
		jhelp.setModel(super.getHelpModel());
	    }
	}
    }

    /**
     * Displays the presentation to the user.
     */
    public void setDisplayed(boolean b) {
	debug ("setDisplayed");
	// if the jhelp is null and they don't want it displayed just return
	if (jhelp == null && !b) {
	    return;
	}

	// The call to createHelpWindow is necessary as the modality
	// might have been changed and we need to change from a dialog
	// to a frame. This is only done in createHelpWindow.
	createHelpWindow();
	if (modallyActivated) {
	    if (b) {
		dialog.show();
	    } else {
		dialog.hide();
	    }
	} else {
	    frame.setVisible(b);

	// We should be able to just 
	// try {
	// 	frame.setState(Frame.NORMAL)
	// } catch (NoSuchMethodError ex) {
	// }
	// but IE4.0 barfs very badly at this
	// So...

	    try {
		Class types[] = {Integer.TYPE};
		Method m = Frame.class.getMethod("setState", types);

		if (m != null) {
		    Object args[] = {new Integer(0)}; // Frame.NORMAL
		    m.invoke(frame, args);
		}
	    } catch (NoSuchMethodError ex) {
		// as in JDK1.1
	    } catch (NoSuchMethodException ex) {
		// as in JDK1.1
	    } catch (java.lang.reflect.InvocationTargetException ex) {
		//
	    } catch (java.lang.IllegalAccessException ex) {
		//
	    }
	}
    }

    /**
     * Determines if the presentation is displayed.
     */
    public boolean isDisplayed() {
	debug ("isDisplayed");
	if (jhelp == null) {
	    return false;
	}
	if (modallyActivated) {
	    if (dialog != null) {
		return dialog.isShowing();
	    } else {
		return false;
	    }
	} else {
	    if (frame != null) {
		if (! frame.isShowing()) {
		    return false;
		}
		else {
		    // We should be able to just 
		    // try {
		    // 	return (frame.getState() == Frame.NORMAL)
		    // } catch (NoSuchMethodError ex) {
		    // }
		    // but IE4.0 barfs very badly at this
		    // So...

		    try {
			Method m = Frame.class.getMethod("getState", 
							 (java.lang.Class[]) null);

			if (m != null) {
			    int value =((Integer)(m.invoke(frame, 
							   (java.lang.Object[])null))).intValue();
			    if (value == 0)
				return true;
			    else 
				return false;

			}
		    } catch (NoSuchMethodError ex) {
			// as in JDK1.1
		    } catch (NoSuchMethodException ex) {
			// as in JDK1.1
		    } catch (java.lang.reflect.InvocationTargetException ex) {
			//
		    } catch (java.lang.IllegalAccessException ex) {
			//
		    }
		    // On 1.1 I can't tell if it's raised or not.
		    // It's on the screen so true.
		    return true;
		}
	    } else {
		return false;
	    }
	}
    }

    /**
     * Sets the font for this this WindowPresentation.
     * @param f The font.
     */
    public void setFont (Font f) {
	debug("setFont");
	super.setFont(f);
	if (jhelp != null && f != null) {
	    jhelp.setFont(f);
	}
    }

    /**
     * Gets the font for this WindowPresentation
     */
    public Font getFont() {
	debug("getFont");
	Font font = super.getFont();
	if (font == null) {
	    if (jhelp == null) {
		createHelpWindow();
	    }
	    return jhelp.getFont();
	}
	return font;
    }

    /**
     * Sets the locale of this Presentation. The locale is propagated to
     * the presentation.
     * @param l The locale to become this component's locale. A null locale
     * is the same as the defaultLocale.
     * @see #getLocale
     */
    public void setLocale(Locale l) { 
	debug("setLocale");
	super.setLocale(l);
	if (jhelp != null) {
	    jhelp.setLocale(l);
	}
    }


    /**
     * internal method to test for Xinerama mode
     */
    private boolean isXinerama () {
	GraphicsEnvironment ge = 
	    GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice[] gds = ge.getScreenDevices();
	if (gds.length == 1) {
	    return false;
	} else {
	    for (int i=0; i<gds.length; i++) {
		GraphicsConfiguration loopgc =
		    gds[i].getDefaultConfiguration();
		Rectangle bounds = loopgc.getBounds();
		if (bounds.x != 0 || bounds.y !=0) {
		    return true;
		}
	    }
	}
	return false;
    }

   /**
     * Requests the location of the presentation.
     * 
     * @returns Point the location of the presentation.
     */
    public Point getLocation() {
	debug("getLocation");
	if (location != null && jhelp == null) {
	    return location;
	}
	if (jhelp == null) {
	    createHelpWindow();
	}
	if (modallyActivated) {
	    Point dlocation = dialog.getLocation();
	    if (isXinerama()) {
		GraphicsConfiguration gc = dialog.getGraphicsConfiguration();
		Rectangle gcBounds = gc.getBounds();
		return new Point(dlocation.x - gcBounds.x,
				 dlocation.y - gcBounds.y);
	    } 
	    return dlocation;
	} else {
	    Point flocation = frame.getLocation();
	    if (isXinerama()) {
		GraphicsConfiguration gc = frame.getGraphicsConfiguration();
		Rectangle gcBounds = gc.getBounds();
		return new Point(flocation.x - gcBounds.x,
				 flocation.y - gcBounds.y);
	    } 
	    return flocation;
	}
    }

    /**
     * Requests the presentation be located at a given position.
     */
    public void setLocation(Point p) {
	debug("setLocation");
	location = p;


	if (jhelp != null) {
	    if (modallyActivated) {
		if (dialog != null) {
		    GraphicsConfiguration gc = 
			dialog.getGraphicsConfiguration();
		    Rectangle gcBounds = gc.getBounds();
		    Point loc = new Point (gcBounds.x + p.x, 
					   gcBounds.y + p.y);
		    dialog.setLocation(loc);
		}
	    } else {
		if (frame != null) {
		    GraphicsConfiguration gc = 
			frame.getGraphicsConfiguration();
		    Rectangle gcBounds = gc.getBounds();
		    Point loc = new Point (gcBounds.x + p.x, 
					   gcBounds.y + p.y);
		    frame.setLocation(loc);
		}
	    }
	}
    }

    /** 
     * Requests the screen of the presentation
     * @returns int the screen of the presentation
     */
    public int getScreen() {
	debug("getScreen");
	// If there is no jhelp componet then it hasn't been "realized"
	// yet so just return the screen
	if (jhelp == null) {
	    return screen;
	}

	// Help is showing so get the screen from the presentation
	GraphicsConfiguration gc = null;
	if (modallyActivated) {
	    if (dialog != null) {
		gc = dialog.getGraphicsConfiguration();
	    }
	} else {
	    if (frame != null) {
		gc = frame.getGraphicsConfiguration();
	    }
	}
	if (gc != null) {
	    GraphicsDevice device = gc.getDevice();
	    GraphicsEnvironment ge = 
		GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gs = ge.getScreenDevices();
	    for (int i=0; i < gs.length; i++) {
		if (gs[i] == device) {
		    // got our match
		    screen = i;
		    return screen;
		}
	    }
	}
	return screen;
    }

    /**
     * Sets the screen of the presentation
     * @param screen the screen number
     * @throws IllegalArgumentException if the screen is invalid
     */
    public void setScreen(int screen) {
	debug ("setScreen");

	if (screen == this.screen) {
	    // There is nothing to do as it is either already the screen
	    // or the 
	    return;
	}

	if (screen < 0) {
	    throw new IllegalArgumentException("Invalid screen");
	}

	GraphicsEnvironment ge = 
	    GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice[]gs = ge.getScreenDevices();

	// make sure there is a screen device
	if (gs.length <= screen) {
	    throw new IllegalArgumentException ("Invalid Screen");
	}

	this.screen = screen;

	if (jhelp != null) {

	    boolean xinerama = isXinerama();
	    
	    GraphicsDevice gd = gs[screen];
	    GraphicsConfiguration gc = gd.getDefaultConfiguration();
	    Rectangle gcBounds = gc.getBounds();
	    if (modallyActivated) {
		if (dialog != null) {
		    if (xinerama) {
			Point p = getLocation();
			Point loc = new Point (gcBounds.x + p.x, 
					       gcBounds.y + p.y);
			dialog.setLocation(loc);
		    } else {
			location = getLocation();
			dialog.hide();
			dialog = null;
			createHelpWindow();
		    }
		}
	    } else {
		if (frame != null) {
		    if (xinerama) {
			Point p = getLocation();
			Point loc = new Point (gcBounds.x + p.x, 
					       gcBounds.y + p.y);
			frame.setLocation(loc);
		    } else {
			location = getLocation();
			frame.setVisible(false);
			frame = null;
			createHelpWindow();
		    }
		}
	    }
	}
    }

    /**
     * Requests the size of the presentation.
     * @returns Point the location of the presentation.
     */
    public Dimension getSize() {
	debug("getSize");
	// if the jhelp is created then just use the current sizes 
	if (jhelp != null) {
	    if (modallyActivated) {
		if (dialog != null) {
		    return dialog.getSize();
		}
	    } else {
		if (frame != null) {
		    return frame.getSize();
		}
	    }
	}
	return super.getSize();
    }

    /**
     * Requests the presentation be set to a given size. Updates the
     * the presentation on the fly. This is an override of 
     * Presentation.SetSize.
     */
    public void setSize(Dimension d) {
	debug("setSize");
	super.setSize(d);
	if (jhelp != null) {
	    if (modallyActivated) {
		dialog.setSize(d);
		dialog.validate();
	    } else {
		frame.setSize(d);
		frame.validate();
	    }
	}
    }

    public String getTitle() {
	debug("getTitle");

	// if the title comes from the document use that first if 
	// jhelp exists
	if (titleFromDocument && jhelp != null) {
	    String docTitle = jhelp.getContentViewer().getDocumentTitle();
	    if (docTitle != null) {
		return docTitle;
	    }
	}
	
	// otherwise use the title that has been set...
	if (title != null) {
	    return title;
	} else {
	    // Unless there wasn't a title set and then use the HelpSet
	    // title
	    HelpSet hs = getHelpSet();
	    if (hs != null) {
		title = hs.getTitle();
	    }
	}
	return title;
    }

    public void setTitle(String title) {
	debug("setTitle");
	this.title = title;
	if (jhelp != null) {
	    if (modallyActivated) {
		dialog.setTitle(title);
		dialog.validate();
	    } else {
		frame.setTitle(title);
		frame.validate();
	    }
	}

    }

    /**
     * Is the title set from the Document. This is generally useful
     * in SecondaryWindows.
     * @return boolean True if title is set from the Document, false otherwise.
     */
    public boolean isTitleSetFromDocument() {
	debug("isTitleSetFromDocument");
	return titleFromDocument;
    }

    /**
     * Set the title from the Document. 
     * @param b if true will set the title form the document, otherwise will
     *	set the title from the HelpSet.
     */
    public void setTitleFromDocument(boolean b) {
	debug("setTitleFromDocument");
	if (titleFromDocument != b) {
	    titleFromDocument = b;
	    if (titleFromDocument) {
		propertyChangeListener = new WindowPropertyChangeListener();
		if (jhelp != null) {
		    jhelp.getContentViewer().
			addPropertyChangeListener("page",
						  propertyChangeListener);
		}
	    } else {
		if (jhelp != null) {
		    jhelp.getContentViewer().
			removePropertyChangeListener("page",
						     propertyChangeListener);
		}
	    }
	}
    }

    /**
     * Determines if the current view is visible.
     */
    public boolean isViewDisplayed() {
	debug ("isViewDisplayed");
	if (jhelp != null) {
	    return jhelp.isNavigatorDisplayed();
	}
	return viewDisplayed;
    }

    /**
     * Hides/Shows view.
     */
    public void setViewDisplayed(boolean displayed) {
	debug ("setViewDisplayed");
	if (jhelp != null) {
	    jhelp.setNavigatorDisplayed(displayed);
	}
	viewDisplayed = displayed;
    }

    /**
     * Determines if the toolbar is visible.
     */
    public boolean isToolbarDisplayed() {
	debug ("isToolbarDisplayed");
	if (jhelp != null) {
	    return jhelp.isToolbarDisplayed();
	}
	return toolbarDisplayed;
    }

    /**
     * Hides/Shows Toolbar
     */
    public void setToolbarDisplayed(boolean displayed) {
	debug ("setToolbarDisplayed=" + displayed);
	if (jhelp != null) {
	    jhelp.setToolbarDisplayed(displayed);
	}
	toolbarDisplayed = displayed;
    }

    private synchronized void createJHelp() {
	debug ("createJHelp");
	if (jhelp == null) {
	    jhelp = new JHelp(getHelpModel(), null, getHelpSetPresentation());
	    Font font = super.getFont();
	    if (font != null) {
		jhelp.setFont(font);
	    }
	    Locale locale = getLocale();
	    if (locale != null) {
		jhelp.setLocale(locale);
	    }
	    jhelp.setToolbarDisplayed(toolbarDisplayed);
	    jhelp.setNavigatorDisplayed(viewDisplayed);
	    if (currentView != null) {
		JHelpNavigator nav = getNavigatorByName(currentView);
		if (nav != null) {
		    jhelp.setCurrentNavigator(nav);
		}
	    }
	    if (titleFromDocument) {
		jhelp.getContentViewer().
		    addPropertyChangeListener("page", propertyChangeListener);
	    }
	}
    }

    WindowListener dl;
    boolean modalDeactivated = true;

    public synchronized void createHelpWindow() {
	debug ("createHelpWindow");
	// pos is used for determining the screen adjust location of a 
	// dialog or frame that already exist. This should only be used
	// when a switch is required. If it is null this is a creation.
	Point pos = null;
	Dimension size = getSize();
	JDialog tmpDialog = null;

	createJHelp();

	
	// The graphics variables below are only used during the initial
	// creation. If there is a modality change then the actual position
	// of the Window would be used instead.
	GraphicsEnvironment ge = 
	    GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice[] gds = ge.getScreenDevices();
	GraphicsDevice gd = gds[screen];
	GraphicsConfiguration gc = gd.getDefaultConfiguration();
	Rectangle gcBounds = gc.getBounds();

	if (modallyActivated) {
	    // replace dialog.getOwner() with the following code
	    Window owner=null;
	    try {
		Method m = Window.class.getMethod("getOwner", 
						  (java.lang.Class[]) null);
		
		if (m != null && dialog != null) {
		    owner = (Window) m.invoke(dialog, 
					      (java.lang.Class[]) null);
		}
	    } catch (NoSuchMethodError ex) {
		// as in JDK1.1
	    } catch (NoSuchMethodException ex) {
		// as in JDK1.1
	    } catch (java.lang.reflect.InvocationTargetException ex) {
		//
	    } catch (java.lang.IllegalAccessException ex) {
		//
	    }
	    
	    if (dialog == null || owner != ownerWindow || modalDeactivated) {
		if (frame != null) {
		    // pos is already screen adjusted
		    pos = frame.getLocation();
		    size = frame.getSize();
		    frame = null;
		}
		if (dialog != null) {
		    // pos is already screen adjusted
		    pos = dialog.getLocation();
		    size = dialog.getSize();
		    tmpDialog = dialog;
		}
		dialog = new JDialog((Dialog)ownerWindow, getTitle(), 
				     false, gc);

		// Modal dialogs are really tricky. When the modal dialog
		// is dismissed the JDialog will be dismissed as well.
		// When that happens we need to make sure the ownerWindow
		// is set to null so that a new dialog will be created so
		// that events aren't blocked in the HelpViewer.
		dl = new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
			debug ("modal window closing");
			if (destroyOnExit) {
			    destroy();
			    return;
			}
			// JDK1.2.1 bug not closing owned windows
			if (dialog.isShowing()) {
			    dialog.hide();
			}
			if (ownerWindow != null)
			    ownerWindow.removeWindowListener(dl);
			ownerWindow = null;
			modalDeactivated = true;
		    }

		    public void windowClosed(WindowEvent e) {
			debug ("modal window closing");
			if (destroyOnExit) {
			    destroy();
			    return;
			}
		    }
		};
		debug ("adding windowlistener");

		ownerWindow.addWindowListener(dl);
		modalDeactivated = false;
		if (size != null) {
		    dialog.setSize(size);
		} else {
		    dialog.setSize(getSize());
		}

		// if the pos variable is not null then either a frame
		// or a dialog already exists and you should use that position
		// instead. If it is null then base the point on the location
		// and screen or just the screen
		if (pos != null) {
		    dialog.setLocation(pos);
		} else {
		    // set based on location and screen or just screen
		    Point loc = null;
		    if (location != null) {
			if (isXinerama()) {
			    loc = new Point (gcBounds.x + location.x, 
					     gcBounds.y + location.y);
			} else {
			    loc = location;
			}
			dialog.setLocation(loc);
		    }
		}
		dialog.setTitle(getTitle());
		dialog.getContentPane().add(jhelp);
		if (tmpDialog != null) {
		    tmpDialog.hide();
		    tmpDialog = null;
		}
	    }
	} else {
	    if (frame == null) { 
		frame = new JFrame(getTitle(), gc);

		WindowListener l = new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
			if (destroyOnExit) {
			    destroy();
			    return;
			}
			frame.setVisible(false);
		    }
		    public void windowClosed(WindowEvent e) {
			frame.setVisible(false);
			if (destroyOnExit) {
			    destroy();
			    return;
			}
		    }
		};
		frame.addWindowListener(l);
		if (image != null) {
		    frame.setIconImage(image);
		}
	    }
	    if (dialog != null) {
		// pos is already screen adjusted
		pos = dialog.getLocation();
		size = dialog.getSize();
		dialog.hide();
		dialog = null;
		ownerWindow = null;
	    }
	    if (size != null) {
		frame.setSize(size);
	    } else {
		frame.setSize(getSize());
	    }


	    // if the pos variable is not null then either a frame
	    // or a dialog already exists and you should use that position
	    // instead. If it is null then base the point on the location
	    // and screen or just the screen
	    if (pos != null) {
		frame.setLocation(pos);
	    } else {
		// set based on location and screen or just screen
		Point loc = null;
		if (location != null) {
		    if (isXinerama()) {
			loc = new Point (gcBounds.x + location.x, 
					 gcBounds.y + location.y);
		    } else {
			loc = location;
		    }
		    frame.setLocation(loc);
		}
	    }
	    frame.getContentPane().add(jhelp);
            frame.setTitle(getTitle());
	}

    }                 

    /**
     * Get the current window that help is displayed in
     *
     * @returns Window the current Window
     */
    public Window getHelpWindow() {
	if (modallyActivated) {
	    return dialog;
	}
	return frame;
    }

    private class WindowPropertyChangeListener implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent event) {
	    String changeName = event.getPropertyName();
	    if (changeName.equals("page")) {
		String title = getTitle();
		if (modallyActivated) {
		    dialog.setTitle(title); 
		} else {
		    frame.setTitle(title);
		}
	    }
	}
    }

    /**
     * Debugging code...
     */

    private static final boolean debug = false;
    private static void debug(Object msg) {
	if (debug) {
	    System.err.println("WindowPresentation: "+msg);
	}
    }
 
}


