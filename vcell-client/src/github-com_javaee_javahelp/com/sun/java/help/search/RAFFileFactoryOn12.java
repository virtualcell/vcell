/*
 * @(#)RAFFileFactoryOn12.java	1.4 06/10/30
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
 * @(#) RAFFileFactoryOn12.java 1.4 - last change made 10/30/06
 */

package com.sun.java.help.search;

import java.io.*;
import java.net.URLConnection;
import java.security.Permission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;

/**
 * This Factory assumes we are on JDK 1.2
 */

final public class RAFFileFactoryOn12 {
    /**
     * Creata a RAFFile from a URLConnection.  Try to use a temporary file
     * if possible.  This is a static public method
     *
     * @param url The URL with the data to the file
     * @return the RAFFile for this data
     * @exception IOException if there is problem reading from the file
     */
    public static RAFFile get(final URLConnection connection) throws IOException {
	RAFFile topBack = null;

	debug("get on "+connection);
	final Permission permission = connection.getPermission();
	
	/* get the permission after the connection is established */
	int dictLength = connection.getContentLength();
	
	try { 
	    topBack = (RAFFile)
		AccessController.doPrivileged(new PrivilegedExceptionAction() {
		    
		    RAFFile back = null;
		    public Object run() throws IOException {
			InputStream in = null;
			OutputStream out = null;
			try {
			    File tmpFile = File.createTempFile("dict_cache", null);
			    tmpFile.deleteOnExit();

			    if (tmpFile != null) {
				in = connection.getInputStream();
				out  = new FileOutputStream(tmpFile);
				int read = 0;
				byte[] buf = new byte[BUF_SIZE];
				while ((read = in.read(buf)) != -1) {
				    out.write(buf, 0, read);
				}
				back = new TemporaryRAFFile(tmpFile, permission);
			    }  else {
				back = new MemoryRAFFile(connection);
			    }
			} finally {
			    if (in != null) {
				in.close();
			    }
			    if (out != null) {
				out.close();
			    }
			}
			return back;
		    }
		});
	} catch (PrivilegedActionException pae) {
	    topBack = new MemoryRAFFile(connection);
	} catch (SecurityException se) {
	    topBack = new MemoryRAFFile(connection);
	}
	return topBack;
    }

    // The size of the buffer
    private static int BUF_SIZE = 2048;

    /**
     * Debug code
     */

    private static final boolean debug = false;
    private static void debug(String msg) {
        if (debug) {
            System.err.println("RAFFileFactoryOn12: "+msg);
        }
    }
}

