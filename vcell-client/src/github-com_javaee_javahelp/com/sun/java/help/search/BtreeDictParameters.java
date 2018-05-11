/*
 * @(#)BtreeDictParameters.java	1.15 06/10/30
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
 * @(#) BtreeDictParameters.java 1.5 - last change made 04/01/98
 */

package com.sun.java.help.search;

import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.io.*;

/**
 *
 * @version	1.5	04/01/98
 * @author Jacek R. Ambroziak
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 */

class BtreeDictParameters extends BlockManagerParameters
{
  private int id1;
  private String dirName;
  
  // some form of this is needed for starting new DBs
  public BtreeDictParameters(URL fileName, int blockSize, int root,
			     int freeID)
  {
    super(fileName, blockSize, root);
    id1 = freeID;
  }
    
  public BtreeDictParameters(Schema schema, String partName)
    throws Exception
  {
    super(schema, partName);
  }
  
  public boolean readState()
  {
    if (super.readState())
      {
	setFreeID(integerParameter("id1"));
	return true;
      }
    else
      return false;
  }

  public void writeState() {
  }
  
  public int getFreeID() {
    return id1;
  }

  public final void setFreeID(int id) {
    id1 = id;
  }

  private void setDirName(String dirName) {
    this.dirName = dirName;
  }

  public static BtreeDictParameters create(URL dirName)
  {
    try {
      URL url = new URL(dirName, "TMAP");
      BtreeDictParameters bdp = null;
      //	new BtreeDictParameters(url, 2048, 0, 1);
      bdp.setDirName(dirName.getFile());
      return bdp;
    } catch (java.net.MalformedURLException e) {
      System.out.println ("Couldn't create " + dirName + File.separator + "TMAP");
    }
    return null;
  }

  public static BtreeDictParameters read(String dir, URL hsBase)
    throws Exception
  {
    URL baseURL=null, url, tmapURL=null;
    URLConnection connect;
    BufferedReader in;
    File file;

    int blockSize = -1;
    int rootPosition = -1;
    int freeID = -1;

    if (hsBase == null) {
	file = new File(dir);
	if (file.exists()) {
	    // On Win32 we need to convert all "\" to "/"
	    if (File.separatorChar != '/') {
		dir = dir.replace(File.separatorChar, '/');
	    }
	    // Make sure the last character is a file separator
	    if (dir.lastIndexOf(File.separatorChar)
		!= dir.length() - 1) {
		dir = dir.concat(File.separator);
	    }
	    debug ("file:" + dir);
	    // Use a file protocol
	    baseURL = new URL("file", "", dir);
	} else {
	    // Assume that some protocol was specified and try it
	baseURL = new URL(dir);
      }
    }
      
    // Read the SCHEMA data
    if (hsBase != null) {
      url = new URL(hsBase, dir + "/SCHEMA");
    } else {
      url = new URL(baseURL, "SCHEMA");
    }
    connect = url.openConnection();

    in = new BufferedReader
      (new InputStreamReader(connect.getInputStream()));

    // This needs to be replaced with our XML Parser
    String line;
    do {
      line = in.readLine();
    }
    while (!line.startsWith("TMAP"));
    in.close();
    StringTokenizer tokens = new StringTokenizer(line, " =");
    tokens.nextToken();		// skip over 'TMAP'
    while (tokens.hasMoreTokens())
      {
	String token = tokens.nextToken();
	if (token.equals("bs"))
	  blockSize = Integer.parseInt(tokens.nextToken());
	else if (token.equals("rt"))
	  rootPosition = Integer.parseInt(tokens.nextToken());
	else if (token.equals("id1"))
	  freeID = Integer.parseInt(tokens.nextToken());
      }

    if (hsBase != null) {
      tmapURL = new URL(hsBase, dir + "/TMAP");
    } else {
      tmapURL = new URL(baseURL, "TMAP");
    }
    BtreeDictParameters bdp = null;
    //      new BtreeDictParameters(tmapURL, blockSize, rootPosition, freeID);

    if (hsBase == null) {
      bdp.setDirName(new File(baseURL.toURI()).getAbsolutePath());
    }
    return bdp;
  }

  public void updateSchema() {
    super.updateSchema("id1="+id1+" id2=1");
  }
    
  public void write() throws java.io.IOException
  {
    FileWriter out = new FileWriter(dirName + "/SCHEMA");
    out.write("JavaSearch 1.0\n");
    out.write("TMAP bs=2048 rt="+root+" fl=-1 id1="+id1+" id2=1\n");
    out.close();
  }

  /**
   * For printf debugging.
   */
  private static boolean debugFlag = false;
  private static void debug(String str) {
    if( debugFlag ) {
      System.out.println("BtreeDictParamters: " + str);
    }
  }
}
