/*
 * @(#)BlockManagerParameters.java	1.11 06/10/30
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
 * @(#) BlockManagerParameters.java 1.3 - last change made 03/17/98
 */

package com.sun.java.help.search;

import java.net.URL;

/**
 * This class contains the parameters necessary for Dict BlockManager
 *
 * @date   2/3/98
 * @author Jacek R. Ambroziak
 * @author Roger D. Brinkley
 */

class BlockManagerParameters extends DBPartParameters
{
  private URL url;

  private int blockSize;
  protected int root;

  public BlockManagerParameters(Schema schema, String partName)
    throws Exception
  {
    super(schema, partName);
    url = schema.getURL(partName);
    debug(url.toString());
  }
  
  public boolean readState()
  {
    if (parametersKnown())
      {
	blockSize = integerParameter("bs");
	//	System.err.println("blockSize " + blockSize);
	root = integerParameter("rt");
	return true;
      }
    else
      return false;
  }

  public void updateSchema(String params) {
    super.updateSchema("bs="+blockSize+" rt="+root+" fl=-1 " + params);
  }

  public BlockManagerParameters(URL url, int blockSize, int root)
  {
    this.url = url;
    this.blockSize = blockSize;
    this.root = root;
  }

  public URL getURL() {
    return url;
  }

  public int getBlockSize() {
    return blockSize;
  }

  public void setBlockSize(int size) {
    blockSize = size;
  }

  public int getRootPosition() {
    return root;
  }

  public void setRoot(int root) {
    this.root = root;
  }
  /**
   * Debug code
   */

  private boolean debug=false;
  private void debug(String msg) {
    if (debug) {
      System.err.println("Block Manager Parameters: "+msg);
    }
  }
}
