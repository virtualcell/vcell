/*
 * @(#)SwingHelpUtilities.java	1.10 06/10/30
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.plaf.ComponentUI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Provides a number of utility functions:
 *
 * Support for Beans, mapping from a Bean class to its HelpSet and to
 * its ID.
 * Support for LAF changes.
 * Support for finding localized resources.
 *
 * This class has no public constructor.
 *
 * @author Eduardo Pelegri-Llopart
 * @author Roger D. Brinkley
 * @version	1.10	10/30/06
 */

public class SwingHelpUtilities implements PropertyChangeListener {

    private static UIDefaults uiDefaults = null;
    private static SwingHelpUtilities myLAFListener = new SwingHelpUtilities();
    private static String contentViewerUI = null;

    static {
        installUIDefaults();
    }
    
    //=========
    /**
     * LAF support
     */

    /**
     * The PropertyChange method is used to track changes to LookAndFeel
     * via the "lookAndFeel" property.
     */

    public void propertyChange(PropertyChangeEvent event) {
	String changeName = event.getPropertyName();
	if (changeName.equals("lookAndFeel")) {
	    installLookAndFeelDefaults();
        }
    }

    /**
     * Installs UIDefaults for Help components and installs "lookAndFeel"
     * property change listener.
     */
    static void installUIDefaults() {
        UIDefaults table = UIManager.getLookAndFeelDefaults();
        if (uiDefaults != table) {
            uiDefaults = table;
            UIManager.removePropertyChangeListener(myLAFListener);
            installLookAndFeelDefaults();
            UIManager.addPropertyChangeListener(myLAFListener);
        }
    }

    /**
     * Set the default ContentViewerUI
     * 
     * param cvUI - full class name of the content viewer UI
     */
    public static void setContentViewerUI(String cvUI) {
	if (cvUI != null) {
	    try {
		Class c = Class.forName(cvUI);
		// an error will be thrown if the class doesn't exist
		contentViewerUI = cvUI;
	    } catch (Throwable e) {
		System.out.println("ContentViewerClass " + cvUI + " doesn't exist");
	    }
	}
    }

    /**
     * Adds look and feel constants for Help components into UIDefaults table.
     */
    static void installLookAndFeelDefaults() {
        LookAndFeel lnf = UIManager.getLookAndFeel();
        UIDefaults table = UIManager.getLookAndFeelDefaults();

	debug("installLookAndFeelDefaults - " + lnf);

        if ((lnf != null) && (table != null)) {
	    if (lnf.getID().equals("Motif")) {
		installMotifDefaults(table);
	    } else if (lnf.getID().equals("Windows")) {
		installWindowsDefaults(table);
	    } else if (lnf.getID().equals("GTK")) {
		installGTKDefaults(table);
	    } else {
		// Default
		installMetalDefaults(table);
	    }
	}
	debug ("verifing UIDefaults; HelpUI=" + table.getString("HelpUI"));

    }

    /**
     * Dynamically invoke a cursor factory to get a cursor.
     */
    private static Object createIcon(String factoryName,
				     String method) {
	ClassLoader loader = HelpUtilities.class.getClassLoader();
	try {
	    Class types[] = new Class[0];
	    Object args[] = new Object[0];
	    Class klass;

	    if (loader == null) {
		klass = Class.forName(factoryName);
	    } else {
		klass = loader.loadClass(factoryName);
	    }
	    Method m = klass.getMethod(method, types);
	    Object back = m.invoke(null, args);
	    return back;
	} catch (Exception ex) {
	    return null;
	}
    }

    static Object basicOnItemCursor = new UIDefaults.LazyValue() {
	public Object createValue(UIDefaults table) {
	    return createIcon("javax.help.plaf.basic.BasicCursorFactory",
			      "getOnItemCursor");
	}
    };

    static Object basicDnDCursor = new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults table) {
	    return createIcon("javax.help.plaf.basic.BasicCursorFactory",
			      "getDnDCursor");
	}
    };

    static Object gtkOnItemCursor = new UIDefaults.LazyValue() {
	public Object createValue(UIDefaults table) {
	    return createIcon("javax.help.plaf.gtk.GTKCursorFactory",
			      "getOnItemCursor");
	}
    };

    static Object gtkDnDCursor = new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults table) {
	    return createIcon("javax.help.plaf.gtk.GTKCursorFactory",
			      "getDnDCursor");
	}
    };


    /*
     * Utility method that creates a UIDefaults.LazyValue that creates
     * an ImageIcon for the specified <code>image</code>
     * filename. I tried to use the similar LookAndFeel.makeIcon() method but it
     * creates IconUIResource instaed of Icon Image and buttons cannot then produce
     * disabled icon from it.
     */
    static private Object makeBasicIcon(final String image) {
        return new UIDefaults.LazyValue() {
            public Object createValue(UIDefaults table) {
                return SwingHelpUtilities.getImageIcon(javax.help.plaf.basic.BasicHelpUI.class, image);
            }
        };
    }

    /**
     * The basic LAF does what we need.
     */
    static private void installBasicDefaults(UIDefaults table) {

        String basicPackageName = "javax.help.plaf.basic.";

	String basicContentViewerUI = basicPackageName + "BasicContentViewerUI";
	if (contentViewerUI != null) {
	    basicContentViewerUI = contentViewerUI;
	}

	Object[] uiDefaults = {
	                "HelpUI", basicPackageName + "BasicHelpUI",
	    "HelpTOCNavigatorUI", basicPackageName + "BasicTOCNavigatorUI",
	  "HelpIndexNavigatorUI", basicPackageName + "BasicIndexNavigatorUI",
	 "HelpSearchNavigatorUI", basicPackageName + "BasicSearchNavigatorUI",
       "HelpGlossaryNavigatorUI", basicPackageName + "BasicGlossaryNavigatorUI",
      "HelpFavoritesNavigatorUI", basicPackageName + "BasicFavoritesNavigatorUI",
	   "HelpContentViewerUI", basicContentViewerUI,
                 "HelpDnDCursor", basicDnDCursor,
	      "HelpOnItemCursor", basicOnItemCursor,
               "BackAction.icon", makeBasicIcon("images/Back.gif"),
            "ForwardAction.icon", makeBasicIcon("images/Forward.gif"),
              "PrintAction.icon", makeBasicIcon("images/Print.gif"),
         "PrintSetupAction.icon", makeBasicIcon("images/PrintSetup.gif"),
             "ReloadAction.icon", makeBasicIcon("images/Reload.gif"),
          "FavoritesAction.icon", makeBasicIcon("images/Favorites.gif"),
               "HomeAction.icon", makeBasicIcon("images/Home.gif"),
      	     "FavoritesNav.icon", makeBasicIcon("images/FavoritesNav.gif"),
                 "IndexNav.icon", makeBasicIcon("images/IndexNav.gif"),
                   "TOCNav.icon", makeBasicIcon("images/TOCNav.gif"),
                "SearchNav.icon", makeBasicIcon("images/SearchNav.gif"),
              "GlossaryNav.icon", makeBasicIcon("images/GlossaryNav.gif"),
               "HistoryNav.icon", makeBasicIcon("images/HistoryNav.gif"),
                "SearchLow.icon", makeBasicIcon("images/SearchLow.gif"),
             "SearchMedLow.icon", makeBasicIcon("images/SearchMedLow.gif"),
                "SearchMed.icon", makeBasicIcon("images/SearchMed.gif"),
            "SearchMedHigh.icon", makeBasicIcon("images/SearchMedHigh.gif"),
               "SearchHigh.icon", makeBasicIcon("images/SearchHigh.gif")
	};

	table.putDefaults(uiDefaults);
    }

    /**
     * If we had any LAF-specific classes, they would be invoked from here.
     */
    static private void installMetalDefaults(UIDefaults table) {
	installBasicDefaults(table);
    }

    static private void installWindowsDefaults(UIDefaults table) {
	installBasicDefaults(table);
    }

    static private void installMotifDefaults(UIDefaults table) {
	installBasicDefaults(table);
    }

    /*
     * Utility method that creates a UIDefaults.LazyValue that creates
     * an ImageIcon for the specified <code>image</code>
     * filename. I tried to use the similar LookAndFeel.makeIcon() method but it
     * creates IconUIResource instaed of Icon Image and buttons cannot then produce
     * disabled icon from it.
     */
    static private Object makeGTKIcon(final String image) {
        return new UIDefaults.LazyValue() {
            public Object createValue(UIDefaults table) {
                return SwingHelpUtilities.getImageIcon(javax.help.plaf.gtk.GTKCursorFactory.class, 
						       image);
            }
        };
    }

    static private void installGTKDefaults(UIDefaults table) {
        String basicPackageName = "javax.help.plaf.basic.";
	String gtkPackageName = "javax.help.plaf.gtk.";

	String basicContentViewerUI = basicPackageName + "BasicContentViewerUI";
	if (contentViewerUI != null) {
	    basicContentViewerUI = contentViewerUI;
	}

	Object[] uiDefaults = {
	                "HelpUI", basicPackageName + "BasicHelpUI",
	    "HelpTOCNavigatorUI", basicPackageName + "BasicTOCNavigatorUI",
	  "HelpIndexNavigatorUI", basicPackageName + "BasicIndexNavigatorUI",
	 "HelpSearchNavigatorUI", basicPackageName + "BasicSearchNavigatorUI",
       "HelpGlossaryNavigatorUI", basicPackageName + "BasicGlossaryNavigatorUI",
      "HelpFavoritesNavigatorUI", basicPackageName + "BasicFavoritesNavigatorUI",
	   "HelpContentViewerUI", basicContentViewerUI,
                 "HelpDnDCursor", gtkDnDCursor,
	      "HelpOnItemCursor", gtkOnItemCursor,
               "BackAction.icon", makeGTKIcon("images/Back.png"),
            "ForwardAction.icon", makeGTKIcon("images/Forward.png"),
              "PrintAction.icon", makeGTKIcon("images/Print.png"),
         "PrintSetupAction.icon", makeGTKIcon("images/PrintSetup.png"),
             "ReloadAction.icon", makeGTKIcon("images/Reload.png"),
          "FavoritesAction.icon", makeGTKIcon("images/Favorites.png"),
               "HomeAction.icon", makeGTKIcon("images/Home.png"),
      	     "FavoritesNav.icon", makeGTKIcon("images/FavoritesNav.png"),
                 "IndexNav.icon", makeGTKIcon("images/IndexNav.gif"),
                   "TOCNav.icon", makeGTKIcon("images/TOCNav.gif"),
                "SearchNav.icon", makeGTKIcon("images/SearchNav.gif"),
              "GlossaryNav.icon", makeGTKIcon("images/GlossaryNav.gif"),
               "HistoryNav.icon", makeGTKIcon("images/HistoryNav.gif"),
                "SearchLow.icon", makeGTKIcon("images/SearchLow.gif"),
             "SearchMedLow.icon", makeGTKIcon("images/SearchMedLow.gif"),
                "SearchMed.icon", makeGTKIcon("images/SearchMed.gif"),
            "SearchMedHigh.icon", makeGTKIcon("images/SearchMedHigh.gif"),
               "SearchHigh.icon", makeGTKIcon("images/SearchHigh.gif")
	};

	table.putDefaults(uiDefaults);
    }

    /**
     * Create an Icon from a given resource.
     * 
     * This works uisng getResourceAsStream() because several browsers do not
     * correctly implement getResource().
     *
     * This method may change...
     */

    public static ImageIcon getImageIcon(final Class baseClass,
					 final String image) {
	if (image == null) {
	    return null;
	}
	final byte[][] buffer = new byte[1][];
	try {
	    InputStream resource = baseClass.getResourceAsStream(image);
	    if (resource == null) {
		return null; 
	    }
	    BufferedInputStream in = new BufferedInputStream(resource);
	    ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
	    buffer[0] = new byte[1024];
	    int n;
	    while ((n = in.read(buffer[0])) > 0) {
		out.write(buffer[0], 0, n);
	    }
	    in.close();
	    out.flush();
	    buffer[0] = out.toByteArray();
	} catch (IOException ioe) {
	    System.err.println(ioe.toString());
	    return null;
	}
	if (buffer[0] == null) {
	    System.err.println(baseClass.getName() + "/" + 
			       image + " not found.");
	    return null;
	}
	if (buffer[0].length == 0) {
	    System.err.println("warning: " + image + 
			       " is zero-length");
	    return null;
	}
	
	return new ImageIcon(buffer[0]);
    }

    static void addPropertyChangeListener(Object object, PropertyChangeListener listener) {
        try {
            Class types[] = { PropertyChangeListener.class };
            Object args[] = { listener };
            object.getClass().getMethod("addPropertyChangeListener", types).invoke(object, args);
        } catch (Exception ex) {
        }
    }

    /**
     * Debug support
     */

    private static final boolean debug = false;
    private static void debug(Object msg1) {
	if (debug) {
	    System.err.println("GUIHelpUtilities: "+msg1);
	}
    }
}
