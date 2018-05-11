/*
 * @(#)BasicCursorFactory.java	1.11 06/10/30
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

package javax.help.plaf.basic;

import java.io.*;
import javax.swing.ImageIcon;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

/**
 * Factory object that can vend cursors appropriate for the basic L & F.
 * <p>
 *
 * @version 1.11	10/30/06
 * @author Roger D. Brinkley
 */
public class BasicCursorFactory {
    private static Cursor onItemCursor;
    private static Cursor dndCursor;
    private static BasicCursorFactory theFactory;

    /**
     * Returns the OnItem cursor.
     */
    public static Cursor getOnItemCursor() {
	debug ("getOnItemCursor");
	if (theFactory == null) {
	    theFactory = new BasicCursorFactory();
	}
	if (onItemCursor == null) {
	    onItemCursor = theFactory.createCursor("OnItemCursor");
	}
	return onItemCursor;
    }

    /**
     * Returns the DnDCursor.
     */
    public static Cursor getDnDCursor(){
        debug("getDnDCursor");
        if (theFactory == null) {
	    theFactory = new BasicCursorFactory();
	}
	if (dndCursor == null) {
	    dndCursor = theFactory.createCursor("DnDCursor");
	}
	return dndCursor;
    }
    
    private Cursor createCursor(String name) {
 	String gifFile = null;
	String hotspot = null;
	ImageIcon icon;
	Point point;

	debug("CreateCursor for " + name);

	// Get the Property file
	InputStream is = getClass().getResourceAsStream("images/" + name + ".properties");
	if (is == null) {
	    debug(getClass().getName() + "/" + 
			       "images/" + name + ".properties" + " not found.");
	    return null;
	}
	try {
	    ResourceBundle resource = new PropertyResourceBundle(is);
	    gifFile = resource.getString("Cursor.File");
	    hotspot = resource.getString("Cursor.HotSpot");
	} catch (MissingResourceException e) {
	    debug(getClass().getName() + "/" + 
			       "images/" + name + ".properties" + " invalid.");
	    return null;
	} catch (IOException e2) {
	    debug(getClass().getName() + "/" + 
			       "images/" + name + ".properties" + " invalid.");
	    return null;
	}

	// Create the icon
	byte[] buffer = null;
	try {
	    /* Copies resource into a byte array.  This is
	     * necessary because several browsers consider
	     * Class.getResource a security risk because it
	     * can be used to load additional classes.
	     * Class.getResourceAsStream returns raw
	     * bytes, which JH can convert to an image.
	     */
	    InputStream resource = 
		getClass().getResourceAsStream(gifFile);
	    if (resource == null) {
		debug(getClass().getName() + "/" + 
				   gifFile + " not found.");
		return null; 
	    }
	    BufferedInputStream in = 
		new BufferedInputStream(resource);
	    ByteArrayOutputStream out = 
		new ByteArrayOutputStream(1024);
	    buffer = new byte[1024];
	    int n;
	    while ((n = in.read(buffer)) > 0) {
		out.write(buffer, 0, n);
	    }
	    in.close();
	    out.flush();
	    
	    buffer = out.toByteArray();
	    if (buffer.length == 0) {
		debug("warning: " + gifFile + 
				   " is zero-length");
		return null;
	    }
	} catch (IOException ioe) {
	    debug(ioe.toString());
	    return null;
	}

	icon = new ImageIcon(buffer);

	// create the point
	int k = hotspot.indexOf(',');
	point = new Point(Integer.parseInt(hotspot.substring(0,k)),
			  Integer.parseInt(hotspot.substring(k+1)));
	
	debug ("Toolkit fetching cursor");
	try {
            
            Image image = icon.getImage();
            int width = icon.getIconWidth();
            int height = icon.getIconHeight();
            
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension d = toolkit.getBestCursorSize(width, height);

            if ((d.width > width) || (d.height > height)) {
                try {
                    Image bimage = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
                    bimage.getGraphics().drawImage(icon.getImage(), 0, 0, icon.getImageObserver());
                    image = bimage;
                } catch (Exception ex) {
                }
            }
            
	    return toolkit.createCustomCursor(image, point, name);
            
	} catch (NoSuchMethodError err) {
	    //	    return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	    return null;
	}
    }

    /**
     * For printf debugging.
     */
    private static final boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("BasicCursorFactory: " + str);
        }
    }

}
