/*
 * @(#)DefaultIndexBuilder.java	1.23 06/10/30
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
 * @date   3/13/98
 * @author Jacek R. Ambroziak
 * @group  Sun Microsystems Laboratories
 */

package com.sun.java.help.search;

import java.net.URL;
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.help.search.IndexBuilder;

public class DefaultIndexBuilder extends IndexBuilder
{
  private static int InitSize = 4096;
  private FullBtreeDict dict;
  protected Hashtable cache = new Hashtable(15000);
  private DocumentCompressor compr;
  private int free = 0;
  private int size = InitSize;
  private ConceptLocation[] locations = new ConceptLocation[size];
  private int freeID;
  private int currentDocID = 0;
  private Schema _schema;
  private BtreeDictParameters _tmapParams;
  private int _title = 0;

  public DefaultIndexBuilder(String indexDir) throws Exception
  {
    super (indexDir);
    // temporary code to disable incremental updates
    removeExistingFiles(indexDir);
    _schema = new Schema(null, indexDir, true);
    _tmapParams = new BtreeDictParameters(_schema, "TMAP");
    if (_tmapParams.readState() == false)
      {
	_tmapParams.setBlockSize(2048);
	_tmapParams.setRoot(0);
	_tmapParams.setFreeID(1);
      }
    
    URL url = new File(indexDir).toURI().toURL();
    /*
    try {
      params = BtreeDictParameters.read(indexDir, null);
    } catch (java.io.FileNotFoundException e) {
      params = BtreeDictParameters.create(url);
    }
    */
    dict = new FullBtreeDict(_tmapParams, true);
    freeID = _tmapParams.getFreeID();
    compr = new DocumentCompressor(url);
  }

    // temporary until Jacek incorporates stop words into IndexBuilder
    private Hashtable tmpstopWords = new Hashtable();

    public void storeStopWords(Enumeration stopWords) {
	for (; stopWords.hasMoreElements() ;) {
	    String word = (String) stopWords.nextElement();
	    // temporarily keep the list here until Jacek finishes adding
	    // stop words to indexes
	    tmpstopWords.put(word, word);
	}
    }

    public Enumeration getStopWords() {
	// For now the stop words are not stored in the index
	// Jacek will change this when he supports stop words in indexes
	return null;
    }

    // temporary code until Jacek provides support for stop words in indexes
    private boolean isStopWord(String word) {
	return tmpstopWords.get(word) != null;
    }

  public void close() throws Exception
  {
    dict.close(freeID);
    _tmapParams.setFreeID(freeID);
    _tmapParams.updateSchema();
    
    debug("compacting...");
    BtreeDictCompactor source = new BtreeDictCompactor(_tmapParams, false);
    URL url = new URL("file", "", indexDir + "compacted");
    BtreeDictParameters params =
      new BtreeDictParameters(url, _tmapParams.getBlockSize(), 0, freeID);
    source.compact(params);
    URL tmapURL = new URL("file", "", indexDir + "TMAP");
    File tmap = new File(tmapURL.toURI());
    tmap.delete();
    File compacted = new File(url.toURI());
    compacted.renameTo(tmap);
    _tmapParams.setRoot(params.getRootPosition());
    _tmapParams.updateSchema();
    
    debug("freeID is " + freeID);
    compr.close(indexDir + "OFFSETS");
    debug("inverting index");
    DocumentLists.invert(indexDir);
    _schema.save();
  }

  public void openDocument(String name) throws Exception
  {
    if (currentDocID != 0) {
      throw new Exception("document already open");
    }
    currentDocID = intern(name);
  }
  
  public void closeDocument() throws Exception
  {
    if (currentDocID == 0) {
      throw new Exception("no document open");
    }
    compr.compress(currentDocID, _title, locations, free, null, 0);
    free = 0;
    currentDocID = 0;		// state: nothing open
    _title = 0;
  }

  public void storeLocation(String text, int position) throws Exception
  {
    // next line is temporary until Jacek provides support for stop words in
    // indexes
    if (isStopWord(text)) return;
    if (free == size) {
      ConceptLocation[] newArray = new ConceptLocation[size *= 2];
      System.arraycopy(locations, 0, newArray, 0, free);
      locations = newArray;
    }
    locations[free++] = new ConceptLocation(intern(text),
					    position,
					    position + text.length());
  }

  public void storeTitle(String title) throws Exception
  {
    _title = intern(title);
  }

  private int intern(String name) throws Exception
  {
    Integer cached = (Integer)cache.get(name);
    if (cached != null)
      return cached.intValue();
    else
      {
	int id = dict.fetch(name);
	if (id == 0) {
	  dict.store(name, id = freeID++);
	}
	cache.put(name, new Integer(id));
	return id;
      }
  }

    /** 
     * Temporary code to remove existing files
     * remove when updates actually works
     */
    private void removeExistingFiles(String indexDir) {
	File test = new File(indexDir);	
	try {
	    if (test.exists()) {
		try {
		    File tmap = new File(test, "TMAP");
		    tmap.delete();
		} catch (java.lang.NullPointerException te) {
		}
		try {
		    File docs = new File(test, "DOCS");
		    docs.delete();
		} catch (java.lang.NullPointerException de) {
		}
		try {
		    File docstab = new File(test, "DOCS.TAB");
		    docstab.delete();
		} catch (java.lang.NullPointerException dte) {
		}
		try {
		    File offsets = new File(test, "OFFSETS");
		    offsets.delete();
		} catch (java.lang.NullPointerException oe) {
		}
		try {
		    File positions = new File(test, "POSITIONS");
		    positions.delete();
		} catch (java.lang.NullPointerException pe) {
		}
		try {
		    File schema = new File(test, "SCHEMA");
		    schema.delete();
		} catch (java.lang.NullPointerException se) {
		}
	    }
	} catch (java.lang.SecurityException e) {
	}
	
    }

  /**
   * Debug code
   */

  private boolean debug = false;
  private void debug(String msg) {
    if (debug) {
      System.err.println("DefaultIndexBuilder: "+msg);
    }
  }
}
