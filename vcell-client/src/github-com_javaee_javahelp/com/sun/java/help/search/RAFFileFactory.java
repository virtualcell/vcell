/*
 * @(#)RAFFileFactory.java	1.27 06/10/30
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
 * @(#) RAFFileFactory.java 1.27 - last change made 10/30/06
 */

package com.sun.java.help.search;

import java.io.*;
import java.net.*;
import java.lang.reflect.*;

/**
 * A factory for files that can be accessed as Random Access file from a URL. 
 * RAFFiles can either be an acutall RAF file or a memory version of the
 * RAF file. When a URL is it is opened as a RAF file, otherwise it is moved
 * to a temporary file if possible or into a memory resident version.
 * 
 *
 * @author Roger D. Brinkley
 * @version	1.27	10/30/06
 */
class RAFFileFactory {

    /**
     * Cannot create these, need to go through the factory method
     */
    private RAFFileFactory() {
    }

    static RAFFileFactory theFactory;

    public static synchronized RAFFileFactory create() {
	if (theFactory == null) {
	    theFactory = new RAFFileFactory();
	}
	return theFactory;
    }

    /* For this connection, when to cache in memory and when to disk */
    private int memoryCacheLimit = 10000;

    private boolean isFileURL(URL url) {
	return url.getProtocol().equalsIgnoreCase("file");
    }

    public int getMemoryCacheLimit() {
	return memoryCacheLimit;
    }

    public void setMemoryCacheLimit(int limit) {
	this.memoryCacheLimit = limit;
    }

    public synchronized RAFFile get(URL url, boolean update) 
	throws IOException {

	RAFFile result = null;

	if (isFileURL(url)) {
	    try {
        File f = new File(url.toURI());

		// refactor so it runs with verification on...

		// Object o = new FilePermission(f, update ? "write":"read");
		// here -- check if AccessController.checkPermission(p);

		result =  new RAFFile(f, update);
		debug ("Opened Dict file with file protocol:" +  f);
	    } catch (SecurityException e) {
		// cannot do "it" -- code is not yet in place
	    } catch (URISyntaxException x) {
            throw (IOException) new IOException(x.toString()).initCause(x);
        }
	}
	if (result == null) {
	    result = createLocalRAFFile(url);
	}
	if (result == null) {
	    throw new FileNotFoundException(url.toString());
	}
	return result;
    }


    /** 
     * Given a URL, retrieves a DICT file and creates a cached DICT
     * file object. If the file is larger than the size limit,
     * and if temp files are supported by the Java virtual machine,
     * the DICT file is it is cached to disk. Otherwise the DICT file 
     * is cached in memory.
     */
    private static RAFFile createLocalRAFFile(URL url) throws IOException {

	RAFFile result = null;
	URLConnection connection = url.openConnection();

	// We should be able to just do a catch on missing method but
	// IE4.0 does not like this
	//
	//	try {
	//	    Object foo = connection.getPermission();
	//
	//	    result = RAFFileFactoryOn12.get(connection);
	//
	//	} catch (NoSuchMethodError ex) {
	//	    // on 1.1 all we can do is create a memory file
	//	    result = new MemoryRAFFile(connection);
	//	    debug ("Opening a Dict file in Memory");
	//	}
	//

	try {
	    Class types[] = {};
	    Method m = URLConnection.class.getMethod("getPermission", types);
	    result = RAFFileFactoryOn12.get(connection);
	} catch (NoSuchMethodError ex) {
	    // as in JDK1.1
	} catch (NoSuchMethodException ex) {
	    // as in JDK1.1
	}

	if (result == null) {
	    // on 1.1 all we can do is create a memory file
	    result = new MemoryRAFFile(connection);
	    debug ("Opening a Dict file in Memory");
	}
	return result;
    }

    /**
     * Debug code
     */

    private static final boolean debug = false;
    private static void debug(String msg) {
        if (debug) {
            System.err.println("RAFFileFactory: "+msg);
        }
    }
}
