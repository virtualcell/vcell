/*
 * @(#)BasicHelpUI.java	1.87 06/10/30
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
/*
 * @(#) BasicHelpUI.java 1.87 - last change made 10/30/06
 */

package javax.help.plaf.basic;

import javax.help.*;
import javax.help.Map.ID;
import javax.help.plaf.HelpUI;
import javax.help.event.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Stack;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import com.sun.java.help.impl.JHelpPrintHandler;
import java.awt.datatransfer.DataFlavor;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import javax.swing.Timer;

/**
 * The default UI for JHelp.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @author Richard Gregor
 * @version   1.87     10/30/06
 */

public class BasicHelpUI extends HelpUI implements PropertyChangeListener, Serializable {
    protected JHelp help;
    protected JToolBar toolbar;
    protected JSplitPane splitPane;
    protected JTabbedPane tabbedPane;
    protected Vector navs=new Vector();
    
    private static Dimension PREF_SIZE = new Dimension(600,600);
    private static Dimension MIN_SIZE = new Dimension(300,200);
    static boolean noPageSetup = false;
    
    // Simple test to determine if pageSetup works on this system
    // Yes for 1.2 on Windows, no for Solaris,Linux,HP
    // Yes for 1.3
    static {
        boolean on1dot2 = false;
        try {
            // Test if method introduced in 1.3 is available.
            Method m = DataFlavor.class.getMethod("getTextPlainUnicodeFlavor",
						  (java.lang.Class[]) null);
            on1dot2 = (m == null);
        } catch (NoSuchMethodException e) {
            on1dot2 = true;
        }
            
        if (on1dot2) {
            String osName[] = new String[] {""};
            osName[0] = System.getProperty("os.name");
            if (osName[0] != null) {
                if ((osName[0].indexOf("Solaris") != -1) ||
                (osName[0].indexOf("SunOS") != -1) ||
                (osName[0].indexOf("Linux") != -1) ||
                (osName[0].indexOf("HP-UX") != -1)) {
                        noPageSetup = true;
                }
            }
        }
    }
    
    private int dividerLocation = 0;
    private final double dividerLocationRatio = 0.30;
    private JHelpFavoritesNavigator favorites = null;
    
    public static ComponentUI createUI(JComponent x) {
        return new BasicHelpUI((JHelp) x);
    }
    
    public BasicHelpUI(JHelp b) {
        debug("createUI - sort of");
    }
    
    public void installUI(JComponent c) {
        debug("installUI");
        help = (JHelp)c;
        help.setLayout(new BorderLayout());
        
        // listen to property changes
        help.addPropertyChangeListener(this);

        // The navigators
        tabbedPane = new JTabbedPane();
        tabbedPane.setVisible(false);
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				   false,
				   tabbedPane,
				   help.getContentViewer());
        
        splitPane.setOneTouchExpandable(true);
        help.add("Center", splitPane);
        
        JHelpNavigator first = null;
        for (Enumeration e = help.getHelpNavigators(); e.hasMoreElements();) {
            JHelpNavigator nav = (JHelpNavigator)e.nextElement();
            if(nav instanceof JHelpFavoritesNavigator) {
                favorites = (JHelpFavoritesNavigator)nav;
	    }
            addNavigator(nav);
            if (first == null) {
		first = nav;
	    }
        }
        
        debug("setting the current Navigator");
        if (first != null) {
            this.setCurrentNavigator(first);
        }
        // ToolBar should be visible externally.
        
        toolbar = createToolBar(HelpUtilities.getLocale(c));
        if (toolbar != null) {
            toolbar.setFloatable(false);
            help.add("North", toolbar);
        }
        
        // load everything
        rebuild();
    }
    
    protected JToolBar createToolBar(Locale locale) {
        toolbar = new JToolBar();
	Enumeration actions = null;
	
	// get the actions from the Presentation Toolbar if one exits
 	HelpSet.Presentation hsPres = help.getHelpSetPresentation();
	if (hsPres != null && hsPres.isToolbar()) {
	    actions = hsPres.getHelpActions(getModel().getHelpSet(), help);
	} 
	
	if (actions == null || !actions.hasMoreElements()) {
            actions = createDefaultActions();
        }
        
	while (actions.hasMoreElements()) {
            HelpAction action = (HelpAction)actions.nextElement();
            if (action instanceof SeparatorAction) {
                toolbar.addSeparator();
            } else {
                toolbar.add(new HelpButton(action));
            }
        }
        
        return toolbar;
    }
    
    private Enumeration createDefaultActions() {
        Vector actions = new Vector(5);
        actions.add(new BackAction(help));
        actions.add(new ForwardAction(help));
        actions.add(new SeparatorAction(help));
        actions.add(new PrintAction(help));
        actions.add(new PrintSetupAction(help));
	actions.add(new SeparatorAction(help));
	if (favorites != null) {
	    actions.add(new FavoritesAction(help));
	}
        return actions.elements();
    }
    
    private class HelpButton extends JButton implements PropertyChangeListener {

        HelpButton(HelpAction action) {
            super();
            
            setEnabled(action.isEnabled());
            
            String name = (String)action.getValue("name");
            
            Icon icon = (Icon)action.getValue("icon");
            if (icon == null) {
                icon = UIManager.getIcon("HelpAction.icon");
            }
            setIcon(icon);

	    Locale locale = null;
	    try {
		locale = help.getModel().getHelpSet().getLocale();
	    } catch (NullPointerException npe) {
		locale = Locale.getDefault();
	    }
            
            String tooltip = (String)action.getValue("tooltip");
            setToolTipText(tooltip);

            String access = (String)action.getValue("access");
            getAccessibleContext().setAccessibleName(access);
            
            if (action instanceof MouseListener) {
                addMouseListener((MouseListener)action);
            }
            
            if (action instanceof ActionListener) {
                addActionListener((ActionListener)action);
            }
            
            action.addPropertyChangeListener(this);
            
        }
        
        /**
         * This method gets called when a bound property is changed.
         * @param evt A PropertyChangeEvent object describing the event source
         *  	and the property that has changed.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("enabled")) {
                setEnabled(((Boolean)evt.getNewValue()).booleanValue());
            }
        }
            
        private boolean createEnablePropertyChangeSupport(HelpAction action) {
            boolean back = false;
            try {
                Class types[] = { String.class, PropertyChangeListener.class };
                Method m = action.getClass().getMethod("addPropertyChangeListener", types);
                Object args[] = { "enabled",  this };
                m.invoke(action, args);
                back = true;
            } catch (Exception ex) {
            }
            return back;
        }

        private boolean createPropertyChangeSupport(HelpAction action) {
            boolean back = false;
            try {
                Class types[] = { PropertyChangeListener.class };
                Method m = action.getClass().getMethod("addPropertyChangeListener", types);
                Object args[] = { this };
                m.invoke(action, args);
                back = true;
            } catch (Exception ex) {
            }
            return back;
        }

    }

    public void uninstallUI(JComponent c) {
        debug("uninstallUI");
        
        help.removePropertyChangeListener(this);
        help.setLayout(null);
        help.removeAll();
        
        HelpModel hm = getModel();
        if (hm != null) {
           // hm.removeHelpModelListener(changeListener);
        }

        help = null;
        toolbar = null;
    }
    
    public Dimension getPreferredSize(JComponent c) {
        return PREF_SIZE;
    }
    
    public Dimension getMinimumSize(JComponent c) {
        return MIN_SIZE;
    }
    
    public Dimension getMaximumSize(JComponent c) {
        // This doesn't seem right. But I'm not sure what to do for now
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    private void rebuild() {
        HelpModel hm = getModel();
        
        if (hm == null) {
            return;
        }
        //hm.addHelpModelListener(changeListener);
        
        // Discard any history
        HelpHistoryModel historyModel = getHistoryModel();
        if(historyModel != null)
            historyModel.discard();
        
        try {
	    Map.ID currentID = hm.getCurrentID();
	    if (currentID == null) {
		HelpSet hs = hm.getHelpSet();
		Map.ID homeID = hs.getHomeID();
		Locale locale = hs.getLocale();
		String string = HelpUtilities.getString(locale, "history.homePage");
		hm.setCurrentID(homeID, string, null);
	    }
        } catch (Exception e) {
            // For example, a null HelpSet!
            return;
        }
    }
    
    public void propertyChange(PropertyChangeEvent event) {
        Object source = event.getSource();
        String propertyName = event.getPropertyName();

        debug("propertyChange: " + propertyName);

        if (source == help) {
            if (propertyName.equals("helpModel")) {
                rebuild();
            } else if (propertyName.equals("font")) {
                debug("Font change");
                Font newFont = (Font)event.getNewValue();
                help.getContentViewer().setFont(newFont);
                help.getContentViewer().invalidate();
                Enumeration entries = help.getHelpNavigators();
                while (entries.hasMoreElements()) {
                    JHelpNavigator nav = (JHelpNavigator)entries.nextElement();
                    nav.setFont(newFont);
                }
            } else if (propertyName.equals("navigatorDisplayed")) {
		boolean display = ((Boolean)event.getNewValue()).booleanValue();
		if (display) {
		    // assume we're not displayed
		    help.add("Center", splitPane);
		} else {
		    help.add("Center", help.getContentViewer());
		}
            } else if (propertyName.equals("toolbarDisplayed")) {
		toolbar.setVisible(((Boolean)event.getNewValue()).booleanValue());
	    }
        }
        
    }
       
    protected HelpModel getModel() {
        if (help == null) {
            return null;
        } else {
            return help.getModel();
        }
    }
    
    /**
     * Returns actual HelpHistoryModel
     *
     * @return The HelpHistoryModel
     */
    protected HelpHistoryModel getHistoryModel(){
        if (help == null) {
            return null;
        } else {
            return help.getHistoryModel();
        }
    }
    
    public void addNavigator(JHelpNavigator nav) {
        debug("addNavigator");
        navs.addElement(nav);
        Icon icon = null;
	// check and see if there is a presentation and if the presentation
	// wants the view images displayed or not.
 	HelpSet.Presentation hsPres = help.getHelpSetPresentation();
	if (hsPres != null) {
	    if (hsPres.isViewImagesDisplayed()) {
		icon = nav.getIcon();
	    }
	} else {
	    // Humm, no presentation so try to get the icon
	    icon = nav.getIcon();
	}
        if (icon != null) {
            tabbedPane.addTab("", icon, nav, nav.getNavigatorLabel());
        } else {
            String name = nav.getNavigatorLabel();
            if (name == null) {
                name = "<unknown>";
            }
            tabbedPane.addTab(name, icon, nav);
        }
        nav.setVisible(false);
        tabbedPane.setVisible(help.isNavigatorDisplayed());
        
        help.invalidate();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // The first time, arrange for the split size...
                // This should be customizable
                // setting a ratio at this point doesn't really work.
                // instead we will set the point based on the ratio and the
                // preferred sizes
                if (dividerLocation == 0d) {
                    Dimension dem = splitPane.getSize();
                    // if there is a size then perform the estimate
                    // otherwise use the default sizes
                    if (dem.width != 0) {
                        splitPane.setDividerLocation((int)
                        ((double)(dem.width -
                        splitPane.getDividerSize())
                        * dividerLocationRatio));
                    }
                    dividerLocation = splitPane.getDividerLocation();
                }
            }
        });
    }
    
    public void removeNavigator(JHelpNavigator nav) {
        debug("removeNavigator");
        navs.removeElement(nav);
        tabbedPane.remove(nav);
        help.invalidate();
    }
    
    public Enumeration getHelpNavigators() {
        return navs.elements();
    }
    
    /**
     * Sets the current Navigator.
     *
     * @param navigator The navigator
     * @exception throws InvalidNavigatorException if not one of the HELPUI
     * navigators.
     */
    public void setCurrentNavigator(JHelpNavigator nav) {
        try {
            tabbedPane.setSelectedComponent(nav);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("JHelpNavigator must be added first");
        }
    }
    
    public JHelpNavigator getCurrentNavigator() {
        return (JHelpNavigator) tabbedPane.getSelectedComponent();
    }
    
    private ImageIcon getIcon(String name) {
        return getIcon(BasicHelpUI.class, name);
    }
    
    // public for now - need to reevalutate
    public static ImageIcon getIcon(Class klass, String name) {
        ImageIcon ig = null;
        try {
            ig = SwingHelpUtilities.getImageIcon(klass, name);
        } catch (Exception ex) {
        }
        
        if (debug || ig == null) {
            System.err.println("GetIcon");
            System.err.println("  name: "+name);
            System.err.println("  klass: "+klass);
            URL url = klass.getResource(name);
            System.err.println("  URL is "+url);
            System.err.println("  ImageIcon is "+ig);
        }
        return ig;
    }

    /**
     * For printf debugging.
     */
    private static boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("BasicHelpUI: " + str);
        }
    }       
}
