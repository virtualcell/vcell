/*
 * @(#)StreamDecompressor.java	1.6 06/10/30
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

/**
 * @date   1/13/98
 * @author Jacek R. Ambroziak
 * @group  Sun Microsystems Laboratories
 */

package com.sun.java.help.search;

import java.io.*;

class StreamDecompressor extends Decompressor
{
  private InputStream _input;
  
  public StreamDecompressor(InputStream input) {
    initReading(input);
  }

  public void initReading(InputStream input)
  {
    _input = input;
    initReading();
  }
  
  public int getNextByte() throws java.io.IOException {
    return _input.read();
  }

  public static void main(String[] args)
  {
    try {
      FileInputStream file = new FileInputStream(args[0]);
      try {
	int k1 = file.read();
	long start = System.currentTimeMillis();
	System.out.println("k1 = " + k1);
	IntegerArray concepts = new IntegerArray();
	StreamDecompressor documents = new StreamDecompressor(file);
	try {
	  documents.ascDecode(k1, concepts);
	}
	catch (Exception e) {
	  System.err.println(e);
	}
	System.out.println("index1 = " + concepts.cardinality());
	int k2 = file.read();
	System.out.println("k2 = " + k2);
	IntegerArray offs = new IntegerArray(concepts.cardinality());
	StreamDecompressor offsets = new StreamDecompressor(file);
	try {
	  offsets.decode(k2, offs);
	}
	catch (Exception e) {
	  System.err.println(e);
	}
	System.out.println("index2 = " + offs.cardinality());
	System.out.println((System.currentTimeMillis() - start) + " msec");
	file.close();
      }
      catch (IOException e) {
	System.err.println(e);
      }
    }
    catch (FileNotFoundException e) {
      System.err.println(e);
    }
  }
}
