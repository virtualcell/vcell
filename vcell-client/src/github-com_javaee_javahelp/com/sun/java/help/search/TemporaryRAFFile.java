/*
 * @(#)TemporaryRAFFile.java	1.4 06/10/30
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
 * @(#) TemporaryRAFFile.java 1.4 - last change made 10/30/06
 */

package com.sun.java.help.search;

import java.io.IOException;

/**
 * This class can be used to read/write the contents of a RAF type files
 * (i.e. DICT (Dictionary) & POSITIONS (Positions))
 * file as part of JavaHelp Search Database. It uses RandamAccessFile for 
 * quick access to dictionary blocks (DictBlock). 
 * </p>
 * Extension of this class serve include memory resident  or unwriteable  
 * RAFFile.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.4	10/30/06
 */

import java.net.URL;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import java.security.Permission;

final public class TemporaryRAFFile extends RAFFile {

    private RandomAccessFile raf;
    private Permission permission;

    // provides only read access.
    public TemporaryRAFFile(File file, Permission permission) throws IOException{
	debug("TemporaryRAFFile " + file);
	raf = new RandomAccessFile(file, "r");
	this.permission = permission;
    }

    public long length() throws IOException { 
	// check permission
	return raf.length();
    }

    public long getFilePointer() throws IOException {
	// check permission
	return raf.getFilePointer();
    }

    public void close() throws IOException {
	// check permission
	raf.close();
    }

    public void seek(long pos) throws IOException {
	// check permission
	raf.seek(pos);
    }

    public int readInt() throws IOException {
	// check permission
	return raf.readInt();
    }

    public int read() throws IOException {
	// check permission
	return raf.read();
    }

    public void readFully (byte b[]) throws IOException {
	// check permission
	raf.readFully(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
	// check permission
	return raf.read(b, off, len);
    }
  
    public void writeInt(int v) throws IOException {
	// check permission
	raf.writeInt(v);
    }

    public void write(byte b[]) throws IOException {
	// check permission
	raf.write(b);
    }

    /**
     * Debug code
     */

    private static final boolean debug = false;
    private static void debug(String msg) {
        if (debug) {
            System.err.println("TemporaryRAFFile: "+msg);
        }
    }
}
