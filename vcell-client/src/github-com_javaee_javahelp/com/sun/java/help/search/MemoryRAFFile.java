/*
 * @(#)MemoryRAFFile.java	1.12 06/10/30
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
 * @(#) MemoryRAFFile.java 1.12 - last change made 10/30/06
 */

package com.sun.java.help.search;

/**
 * A DICT (Dictionary) file cached in memory.
 *
 * @author Roger D. Brinkley
 * @version	1.12	10/30/06
 */

import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;

public class MemoryRAFFile extends RAFFile {

    private URL url;
    private byte[] data;
    private int size;
    private int filePointer;

    public MemoryRAFFile(URLConnection connection) throws IOException {
	this.url = connection.getURL();

	InputStream in = new BufferedInputStream(connection.getInputStream());
	ByteArrayOutputStream data = new ByteArrayOutputStream();

	byte[] buf = new byte[512];
	int i = 0;
	while((i = in.read(buf)) != -1) {
	    data.write(buf, 0, i);
	}
	this.data = data.toByteArray();
	size = data.size();
	filePointer = 0;
    }

    public long length() { 
	return size;
    }

    public void close() throws IOException {
	filePointer = 0;
	data = null;
	size = 0;
    }

    public long getFilePointer() throws IOException {
	return filePointer;
    }

    public void seek(long pos) throws IOException {
	if (pos > size) {
	    throw new IOException();
	}
	filePointer = (int)pos;
    }

    public int read() throws IOException {
	if (filePointer >= size) {
	    return -1;
	}
	filePointer += 1;
	return data[filePointer - 1] & 0xFF;
    }

    private int readBytes(byte b[], int off, int len) throws IOException {
	debug ("readBytes");
	if (filePointer + off + len > size) {
	    throw new IOException();
	}
	filePointer += off;
	System.arraycopy(data, filePointer, b, 0, (int)len);
	filePointer += len;
	return len;
    }

    public int read(byte b[], int off, int len) throws IOException {
	return readBytes(b, off, len);
    }

    public int readInt() throws IOException {
	debug ("readInt");
	int ch1 = this.read();
	int ch2 = this.read();
	int ch3 = this.read();
	int ch4 = this.read();
	if ((ch1 | ch2 | ch3 | ch4) < 0)
	    throw new EOFException();
	return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    public final void readFully(byte b[]) throws IOException {
	readFully(b, 0, b.length);
    }

    private void readFully (byte b[], int off, int len) 
	throws IOException {
        int n = 0;
	do {
	    int count = this.read(b, off + n, len - n);
	    if (count < 0)
		throw new EOFException();
	    n += count;
	} while (n < len);
    }

    public void writeInt(int v) throws IOException {
	throw new IOException("Unsupported Operation");
    }

    public void write(byte b[]) throws IOException {
	throw new IOException("Unsupported Operation");
    }
    /**
     * For printf debugging.
     */
    private static boolean debugFlag = false;
    private static void debug(String str) {
        if( debugFlag ) {
            System.out.println("MemoryRAFFile: " + str);
        }
    }
}
