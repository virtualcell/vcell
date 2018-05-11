/*
 * @(#)Popup.java	1.5 06/10/30
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

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JWindow;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import java.lang.reflect.Method;
import java.util.Vector;
import com.sun.java.help.impl.JHSecondaryViewer;

/**
 * Popup is a Presentation class that will create a popup help window for an
 * application. There is only one popup per application. A popup contains only
 * a content viewer. It is intended to provide immediate help and then allow 
 * the user to continue working. Once a popup loses focus, it is destroyed.
 *
 * @author Roger D. Brinkley
 * @version	1.5	10/30/06
 * @since 2.0
 *
 * @see javax.help.Presentation
 */

public class Popup extends Presentation implements ActionListener{

    private JHelpContentViewer jheditor;
    private Component invoker = null;
    private JWindow window = null;
    private Vector grabbed = null;
    private PopupComponentAdapter pca= null;
    private PopupKeyAdapter pka=null;
    private PopupMouseAdapter pma=null;
    private PopupMouseWheelListener pmwl=null;
    private PopupWindowAdapter pwa=null;
    private Rectangle internalBounds=null;
    static private final String SHOW = "show";
    static private final String CANCEL = "cancel";
    static private boolean on1dot4 = false;

    static {
        try {
            // Test if method introduced in 1.4 is available.
	    Class klass = Class.forName("java.awt.event.MouseWheelEvent");
            on1dot4 = (klass == null);
        } catch (ClassNotFoundException e) {
            on1dot4 = false;
        }
    }


    /*
     * The currentPopup is the Popup that is currently displayed on the
     * screen. This state is maintained for positioning purposes when a 
     * popup invokes a popup to use the currently displayed invoker rather
     * than the invoker passed in.
     */
    static Popup currentPopup = null;

    /*
     * Private costructor
     */
    private Popup(HelpSet hs) {
	setHelpSet(hs);
    }

    /**
     * create a new Popup for a given HelpSet and HelpSet.Presentation 
     * "name". If the "name"d HelpSet.Presentation does not exist in the
     * HelpSet then the defaultHelpSet.Presentation if used.
     *
     * @param hs The HelpSEt used in this presentation
     * @param name The name of the HelpSet.Presentation to use
     * @returns Presentation A Popup.
     */
    static public Presentation getPresentation(HelpSet hs, String name) {

	Popup thePopup = new Popup(hs);

	if (hs != null) {
	    HelpSet.Presentation presentation = null;

	    // get a named presentation if one exists
	    if (name != null) {
		presentation = hs.getPresentation(name);
	    }

	    // get the default presentation if one exits
	    if (presentation == null) {
		presentation = hs.getDefaultPresentation();
	    }

	    // set the presentation
	    // a null is ok here as it will just return.
	    thePopup.setHelpSetPresentation(presentation);
	}
	return thePopup;
    }

    /**
     * Get the Component that invoked this popup
     *
     * @return Component The invoking component
     */
    public Component getInvoker() {
	return invoker;
    }

    /**
     * Set the invoking component for this popup. This must be called before
     * setDisplayed(true) is called. If there is an invoker already set it will
     * be used instead of the new invoker.
     * 
     * @param invoker The component that invoked this popup
     * @throws an IllegalArgumentException if the invoker or it's parents are
     *		not showing.
     */
    public void setInvoker(Component invoker) {
	this.invoker = invoker;
	// If we have a JMenuItem circulate up to the JMenu
	if (invoker instanceof JMenuItem) {
	    while (!(this.invoker instanceof JMenu)) {
		this.invoker = this.invoker.getParent();
		if (this.invoker instanceof JPopupMenu) {
		    this.invoker = ((JPopupMenu)this.invoker).getInvoker();
		}
	    }
	}
	if (this.invoker == null) {
	    throw new IllegalArgumentException("invoker");
	}
    }


    /**
     * Get the internal bounds for the invoker.
     */
    public Rectangle getInvokerInternalBounds() {
	return internalBounds;
    }

    /**
     * Set the the internal bounds for an invoker. For some invokers like
     * JTrees and JTables it is necessary to set an internal bounds to 
     * further define the location of where to place the popup.
     *
     * @param bounds A Rectangle of the internal bounds
     */
    public void setInvokerInternalBounds(Rectangle bounds) {
	internalBounds = bounds;
    }

    /**
     * return the JWindow if created. Will return a null if not created.
     */
    private JWindow getWindow() {
	return window;
    }

    /**
     * Displays the presentation to the user
     */
    public void setDisplayed(boolean b) {
	Container top = getTopMostContainer();

	// if the window is null and they don't want it displayed just return
	if (window == null && !b) {
	    return;
	}

	// if window is null create the window
	if (window == null) {
	    window = new JWindow((Window)getTopMostContainer());
	    jheditor = new JHelpContentViewer(getHelpModel());
	    window.getRootPane().setBorder(BorderFactory.createLineBorder(Color.black, 2));

	    window.getContentPane().add(jheditor, BorderLayout.CENTER);
	}
	if (grabbed == null) {
	    grabbed = new Vector();
	    pca = new PopupComponentAdapter();
	    pma = new PopupMouseAdapter();
	    pwa = new PopupWindowAdapter();
	    pka = new PopupKeyAdapter();
	    if (on1dot4) {
		pmwl = new PopupMouseWheelListener();
	    }
	} else {
	    grabbed.clear();
	}
	grabContainer(top);
	window.addWindowListener(pwa);
	if (on1dot4) {
	    try {
		Class types[] = { Class.forName("java.awt.event.WindowFocusListener") };
		Object args[] = { pwa };
		Method m = window.getClass().getMethod("addWindowFocusListener", types);
		m.invoke(window, args);
		// window.addWindowFocusListener(pwa);
	    } catch (java.lang.Exception ex) {
		//ignore it
	    }
	}

	JRootPane root = getRootPane();
	if (root != null) {
	    root.registerKeyboardAction(this, "cancel",
					KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	} else if (top != null) {
	    top.addKeyListener(pka);
	}
	window.getRootPane().registerKeyboardAction(this, "cancel",
						    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
						    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	
	if (b) {
	    showPopup();
	} else {
	    cancelPopup();
	}
    }

    public boolean isDisplayed() {
	if (window != null) {
	    return true;
	}
	return false;
    }

    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand();
        if (command == "cancel") {
	    cancelPopup();
        }
    }

    /**
     * show the window
     */
    private void showPopup() {
	if (currentPopup != null &&
	    SwingUtilities.getAncestorOfClass(JWindow.class, invoker) == 
	    currentPopup.getWindow()) {
	    setInvoker(currentPopup.getInvoker());
	}

        Rectangle popupBounds = computePopupBounds(getSize());

	jheditor.setPreferredSize(new Dimension(popupBounds.width, 
						popupBounds.height));
	window.setBounds(popupBounds.x, popupBounds.y, 
			 popupBounds.width, popupBounds.height);
	window.pack();
	window.show();
	currentPopup = this;
    }

    private Rectangle computePopupBounds(Dimension popupSize) {
	// Note all Points in computePopupBounds are either relative to
	// the screen. The desired boundry must fit on the screen. If it is
	// in a Modal Dialg it has to fit within the dialog

        Rectangle absBounds;
	Point p;

	// Calculate the absolute boundry. Modal Dialogs must be within the 
	// Dialog.
        boolean inModalDialog = inModalDialog();

        /** Workaround for modal dialogs. See also JPopupMenu.java **/
        if ( inModalDialog ) {
            Dialog dlg = getDialog();
            if ( dlg instanceof JDialog ) {
                JRootPane rp = ((JDialog)dlg).getRootPane();
                p = rp.getLocationOnScreen();
                absBounds = rp.getBounds();
                absBounds.x = p.x;
                absBounds.y = p.y;
            } else {
                absBounds = dlg.getBounds();
	    }
        } else {
            Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
            absBounds = new Rectangle(0, 0, scrSize.width, scrSize.height);
        }

	if (internalBounds == null) {
	    internalBounds = invoker.getBounds();
	}
	debug("\nabsBounds=" + absBounds + "\ninternalBounds=" + internalBounds + "\n");

	// Get a rectangle below and to the right and see if it fits
	p = new Point(internalBounds.x, 
		      internalBounds.y + internalBounds.height);
	SwingUtilities.convertPointToScreen(p, invoker);
        Rectangle br = new Rectangle(p.x, p.y, 
				     popupSize.width, popupSize.height);
	debug("below/right " + br + "\n");
        if ( SwingUtilities.isRectangleContainingRectangle(absBounds, br) ) {
	    // below and to the right fits return the rectangle
            return br;
        }

	// below and right failed - try to adjust the right side to fit
	Rectangle bradjust = new Rectangle(br);
	bradjust.setLocation(br.x + internalBounds.width - br.width, 
			     br.y);
	debug("below/right adjust " + bradjust + "\n");
	if ( SwingUtilities.isRectangleContainingRectangle(absBounds, 
							   bradjust) ) {
	    // below and to the right fits return the rectangle
            return bradjust;
	} 

	// below and right adjust failed - try above right
	Rectangle ar = new Rectangle(br.x, 
				     br.y-(br.height+internalBounds.height),
				     br.width, br.height);
	debug("above/right " + ar + "\n");
	if (SwingUtilities.isRectangleContainingRectangle(absBounds, ar)) {
	    // above and to the right fits return the rectangle
            return ar;
	} 

	// above and right failed - adjust the right side to fit
	Rectangle aradjust = new Rectangle(ar);
	aradjust.setLocation(ar.x + internalBounds.width - ar.width, 
			     aradjust.y);
	debug("above/right adjust " + aradjust + "\n");
	if ( SwingUtilities.isRectangleContainingRectangle(absBounds, 
							   aradjust) ) {
	    // below and to the right fits return the rectangle
            return aradjust;
	} 

	// above and right adjust failed - try right below
	p = new Point(internalBounds.x + internalBounds.width, 
		      internalBounds.y);
	SwingUtilities.convertPointToScreen(p, invoker);
	Rectangle rb = new Rectangle(p.x, p.y, 
				     popupSize.width, popupSize.height);
	debug("right/below " + rb + "\n");
	if (SwingUtilities.isRectangleContainingRectangle(absBounds, rb)) {
	    // right and below fits return the rectangle
            return rb;
	} 

	// right and below failed - adjust the top side to fit
	Rectangle rbadjust = new Rectangle(rb);
	rbadjust.setLocation(rbadjust.x,
			     rb.y + internalBounds.height - rb.height);
	debug("right/below adjust " + rbadjust + "\n");
	if ( SwingUtilities.isRectangleContainingRectangle(absBounds,
							   rbadjust) ) {
	    // right and below adjusted fits return the rectangle
            return rbadjust;
	} 

	// right and below adjust failed - try left and below
	Rectangle lb = new Rectangle(rb.x-(rb.width+internalBounds.width), 
				     rb.y,
				     rb.width, rb.height);
	debug("left/below " + lb + "\n");
	if (SwingUtilities.isRectangleContainingRectangle(absBounds, lb)) {
	    // right and below fits return the rectangle
            return lb;
	} 

	// left and below failed - adjust the top side to fit
	Rectangle lbadjust = new Rectangle(lb);
	lbadjust.setLocation(lbadjust.x,
			     lb.y + internalBounds.height - lb.height);
	debug("left/below adjust " + lbadjust + "\n");
	if ( SwingUtilities.isRectangleContainingRectangle(absBounds, 
							   lbadjust) ) {
	    // right and below fits return the rectangle
            return lbadjust;
	} 

	// Bummer - tried all around the object so no try covering it up
	// Nothing fancy here upper left corner
	Rectangle cover = new Rectangle(0, 0, 
					popupSize.width, 
					popupSize.height);
	debug("upper left hand corner " + cover + "\n");
	if (SwingUtilities.isRectangleContainingRectangle(absBounds, cover)) {
	    // covering up the object fits return the rectangle
            return cover;
	} 

	// Humm. The desired size is just to large. Shrink to fit.
	SwingUtilities.computeIntersection(absBounds.x,
					   absBounds.y,
					   absBounds.width,
					   absBounds.height,
					   cover);
	return cover;
    }

    private Container getTopMostContainer() {
	if (invoker == null) {
	    return null;
	}
	
        for(Container p=((invoker instanceof Container) ? (Container)invoker :
			 invoker.getParent());
			 p != null; p = p.getParent()) {
	    if (p instanceof JPopupMenu) {
		Component comp = ((JPopupMenu)p).getInvoker();
		if (comp instanceof Container) {
		    p = (Container) comp;
		}
	    }
            if (p instanceof Window || p instanceof Applet) {
                return p;
            }
        }
        return null;
    }
	
    private JRootPane getRootPane() {
	Container parent = null;
	JRootPane pane = null;
	if (invoker == null || !(invoker instanceof JComponent)) {
	    return null;
	}

	pane = ((JComponent)invoker).getRootPane();
	if (pane != null) {
	    return pane;
	}
	
	// for loop through the parents until you reach a Dialog or null
        for ( parent = ((invoker instanceof Container) ? (Container)invoker : 
		  invoker.getParent()); 
	      parent != null && !(parent instanceof JDialog) && 
		  !(parent instanceof JWindow) && !(parent instanceof JFrame) ; 
	      parent = parent.getParent() ) {
	    if (parent instanceof JPopupMenu) {
		Component comp = ((JPopupMenu)parent).getInvoker();
		if (comp instanceof Container) {
		    parent = (Container) comp;
		}
	    }
	}

	if (parent instanceof JFrame) {
	    pane = ((JFrame)parent).getRootPane();
	} else if (parent instanceof JWindow) {
	    pane = ((JWindow)parent).getRootPane();
	} else if (parent instanceof JDialog) {
	    pane = ((JDialog)parent).getRootPane();
	}

	return pane;
    }
	

    /*
     * Get the Dialog from the invoker
     */
    private Dialog getDialog() {
	Container parent;
	if (invoker == null) {
	    return null;
	}
	
	// for loop through the parents until you reach a Dialog or null
        for ( parent = ((invoker instanceof Container) ? (Container)invoker : 
		  invoker.getParent()); 
	      parent != null && !(parent instanceof Dialog) ; 
	      parent = parent.getParent() );

	// return the dialog if we got one
        if ( parent instanceof Dialog )
            return (Dialog) parent;
        else
            return null;
    }

    private boolean inModalDialog() {
        return (getDialog() != null);
    }

    private class PopupWindowAdapter extends WindowAdapter {

	public void windowClosing(WindowEvent e) {
	    cancelPopup();
	}

	public void windowClosed(WindowEvent e) {
	    cancelPopup();
	}

	public void windowIconified(WindowEvent e) {
	    cancelPopup();
	}

	public void windowGainedFocus(WindowEvent e) {
	    window.toFront();
	}

    }

    private class PopupMouseAdapter extends MouseInputAdapter {

	public void mousePressed(MouseEvent e) {
	    cancelPopup();
	}
    }

    private class PopupMouseWheelListener implements MouseWheelListener {
	public void mouseWheelMoved(MouseWheelEvent e) {
	    cancelPopup();
	}
    }
    
    private class PopupComponentAdapter extends ComponentAdapter {

	public void componentResized(ComponentEvent e) {
	    cancelPopup();
	}

	public void componentMoved(ComponentEvent e) {
	    cancelPopup();
	}

	public void componentShown(ComponentEvent e) {
	    cancelPopup();
	}

	public void componentHidden(ComponentEvent e) {
	    cancelPopup();
	}

    }
    
    /*
     * Only used for AWT components
     */
    private class PopupKeyAdapter extends KeyAdapter {

	public void keyReleased(KeyEvent e) {
	    int code = e.getKeyCode();
	    if (code == KeyEvent.VK_ESCAPE) {
		cancelPopup();
	    }
	}
    }
 
   private void grabContainer (Container c) {
	if(c instanceof java.awt.Window) {
	    ((java.awt.Window)c).addWindowListener(pwa);
	    ((java.awt.Window)c).addComponentListener(pca);
	    grabbed.addElement(c);
	}
	synchronized(c.getTreeLock()) {
	    int ncomponents = c.getComponentCount();
	    Component[] component = c.getComponents();
	    for (int i = 0 ; i < ncomponents ; i++) {
		Component comp = component[i];
		if(!comp.isVisible())
		    continue;
		comp.addMouseListener(pma);
		comp.addMouseMotionListener(pma);
		if (on1dot4) {
		    try {
			Class types[] = { Class.forName("java.awt.event.MouseWheelListener") };
			Object args[] = { pmwl };
			Method m = window.getClass().getMethod("addMouseWheelListener", types);
			m.invoke(comp, args);
			// comp.addMouseWheelListener(pmwl);
		    } catch (java.lang.Exception ex) {
			//ignore it
		    }
		}
		grabbed.addElement(comp);
		if (comp instanceof Container) {
		    Container cont = (Container) comp;
		    grabContainer(cont);
		}
	    }
	}
    }
	
    void ungrabContainers() {
	int i,c;
	Component cpn;
	for(i=0,c=grabbed.size();i<c;i++) {
	    cpn = (Component)grabbed.elementAt(i);
	    if(cpn instanceof java.awt.Window) {
		((java.awt.Window)cpn).removeWindowListener(pwa);
		((java.awt.Window)cpn).removeComponentListener(pca);
	    } else {
		cpn.removeMouseListener(pma);
		cpn.removeMouseMotionListener(pma);
		if (on1dot4) {
		    try {
			Class types[] = { Class.forName("java.awt.event.MouseWheelListener") };
			Object args[] = { pmwl };
			Method m = window.getClass().getMethod("removeMouseWheelListener", types);
			m.invoke(cpn, args);
			// cpn.removeMouseWheelListener(pmwl);
		    } catch (java.lang.Exception ex) {
			//ignore it
		    }
		}
	    }
	}
	grabbed = null;
    }
	
    private void cancelPopup() {
	Container top = getTopMostContainer();
	ungrabContainers();
	JRootPane root = getRootPane();
	if (root != null) {
	    root.unregisterKeyboardAction
		(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
	} else if (top != null) {
	    top.removeKeyListener(pka);
	}
	window.removeWindowListener(pwa);
	if (on1dot4) {
	    try {
		Class types[] = { Class.forName("java.awt.event.WindowFocusListener") };
		Object args[] = { pwa };
		Method m = window.getClass().getMethod("removeWindowFocusListener", types);
		m.invoke(window, args);
		// window.removeWindowFocusListener(pwa);
	    } catch (java.lang.Exception ex) {
		//ignore it
	    }
	}
	window.getRootPane().unregisterKeyboardAction
	    (KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
	window.dispose();
	window = null;
	jheditor = null;
	currentPopup = null;
    }
	
    /**
     * Debugging code...
     */

    private static final boolean debug = false;
    private static void debug(Object msg) {
	if (debug) {
	    System.err.println("Popup: "+msg);
	}
    }

}
