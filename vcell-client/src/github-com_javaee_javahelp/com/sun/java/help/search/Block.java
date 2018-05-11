/*
 * @(#)Block.java	1.10 06/10/30
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
 * @(#) Block.java 1.3 - last change made 03/20/98
 */

package com.sun.java.help.search;

import java.io.IOException;

/**
 * This class represents a Block of information in DictBlock
 *
 * @author Jacek Ambroziak
 * @author Roger D. Brinkley
 * @version	1.3	03/20/98
 */

class Block
{
  public static final int HEADERLEN = 8;
  public static final int IDLEN = 4;
  public int number;
  public boolean isLeaf = true;
  public int free = 0;
  public byte[] data = null;
  
  public Block(int blocksize) {
    data = new byte[blocksize - HEADERLEN];
  }

  public void setBlockNumber(int n) {
    number = n;
  }

  public void setFree(int free) {
    free = free;
  }
  
  public int integerAt(int i) {
    return ((((((data[i++]&0xFF)<<8)
	       |data[i++]&0xFF)<<8)
	     |data[i++]&0xFF)<<8)
      |data[i]&0xFF;
  }
  
  public void setIntegerAt(int i, int value)
  {
    for (int j = i + 3; j >= i; j--, value >>>= 8)
      data[j] = (byte)(value & 0xFF);
  }

  public static Block readIn(RAFFile in, Block block)
    throws IOException
  {
    debug("readIn");
    block.number = in.readInt();
    int twoFields = in.readInt();
    block.isLeaf = (twoFields & 0x80000000) != 0;
    block.free = twoFields & 0x7FFFFFFF;
    in.readFully(block.data);
    return block;
  }
  
  public void writeOut(RAFFile out) throws IOException
  {
    out.writeInt(number);
    out.writeInt(free | (isLeaf ? 0x80000000 : 0));
    out.write(data);
  }

  /**
   * Debug code
   */

  private static boolean debug=false;
  private static void debug(String msg) {
    if (debug) {
      System.err.println("Block: "+msg);
    }
  }

}

