/*
 * @(#)BtreeDict.java	1.14 06/10/30
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
 * @(#) BtreeDict.java 1.5 - last change made 03/18/98
 */

package com.sun.java.help.search;

import java.io.*;

/**
 * @version	1.5	03/18/98
 * @author Jacek R. Ambroziak
 * @author Roger D. Brinkley
 */

public class BtreeDict
{
  protected static final int ENTHEADERLEN = 6;
  protected static final int BLOCKSIZE = 2048;
  protected static final int DATALEN = BLOCKSIZE - Block.HEADERLEN;
  protected static final int MaxKeyLength = 255;
  //!!! Careful with that number, Eugene
  protected static final int lastPtrIndex = 508;

  protected class DictBlock extends Block
  {
    public DictBlock() {
      super(BLOCKSIZE);
    }
  
    public int free() {
      return free + firstEntry();
    }
  
    public int numberOfEntries() {
      return integerAt(0);
    }
  
    public int nthPointer(int n) {
      return integerAt(4*(n + 1));
    }
  
    public int getChildIdx(int index) {
      return nthPointer(lastPtrIndex - index);
    }
  
    public int entryKeyLength(int i) {
      return data[i] & 0xFF;
    }
  
    public int entryCompression(int i) {
      return data[i + 1] & 0xFF;
    }
  
    public int entryID(int i) {
      return integerAt(i + 2);
    }
  
    public int entryLength(int entry) {
      return ENTHEADERLEN + entryKeyLength(entry);
    }
     
    public int entryKey(int entry) {
      return entry + ENTHEADERLEN;
    }

    public int firstEntry() {
      return 4;
    }

    public int nextEntry(int entry) {
      return entry + entryLength(entry);
    }
  
    public void restoreKeyInBuffer(int entry, byte[] buffer)
    {
      int howMany = entryKeyLength(entry);
      int where = entryCompression(entry);
      int from = entryKey(entry);
      while (howMany-- > 0)
	buffer[where++] = data[from++];
    }
    
    public String restoreKey(int entry, byte[] buffer)
    {
      int howMany = entryKeyLength(entry);
      int where = entryCompression(entry);
      int from = entryKey(entry);
      while (howMany-- > 0)
	buffer[where++] = data[from++];
      String string = null;
      try {
	  string = new String(buffer, 0, where, "UTF8");
      } catch (java.io.UnsupportedEncodingException e) {
	  // ignore
      }
      return string;
    }
  
    public String findID(int id) throws Exception
    {
      byte[] buffer = new byte[MaxKeyLength];
      int freeSpace = free();
      for (int ent = firstEntry(); ent < freeSpace; ent = nextEntry(ent))
	if (entryID(ent) == id) // found
	  return restoreKey(ent, buffer);
	else
	  restoreKeyInBuffer(ent, buffer);
      throw new Exception("ID not found in block");
    }
  
    protected void setBlockNumbers(final int[] blocks)
    {
      for (int e = firstEntry(); e < free; e = nextEntry(e))
	blocks[entryID(e)] = number;
    }
  
    /*
    protected void doMap(BtreeDict owner, EntryProcessor processor)
      throws Exception
    {
      byte[] buffer = new byte[MaxKeyLength];
      final int freeSpace = free();
      int entryPtr = firstEntry();
      if (isLeaf)
	while (entryPtr < freeSpace) {
	  processor.processEntry(restoreKey(entryPtr, buffer),
				 entryID(entryPtr));
	  entryPtr = nextEntry(entryPtr);
	}
      else
	{
	  owner.lock(this);
	  int entryIdx  = 0;
	  while (entryPtr < freeSpace) {
	    owner.accessBlock(getChildIdx(entryIdx)).doMap(owner,processor);
	    processor.processEntry(restoreKey(entryPtr, buffer),
				   entryID(entryPtr));
	    entryPtr = nextEntry(entryPtr);
	    ++entryIdx;
	  }
	  owner.accessBlock(getChildIdx(entryIdx)).doMap(owner, processor);
	  owner.unlock(this);
	}
    }
    */
  }
  // end of internal class

  protected BlockManager blockManager;
  protected int          root;
  protected int[]        blocks;
  
  protected BtreeDict() {/*empty*/}

  public BtreeDict(BtreeDictParameters params) throws Exception
  {
    init(params, false, new BlockFactory() {
      public Block makeBlock() { return new DictBlock(); }
    });
    blocks = new int[params.getFreeID()];
    setBlocks(blocks);
  }

  public int fetch(String key) throws Exception
  {
    //    System.err.println("fetching " + key);
    byte[] bytes = key.getBytes("UTF8");
    byte[] Key = new byte[bytes.length + 1];
    System.arraycopy(bytes, 0, Key, 0, bytes.length);
    Key[bytes.length] = 0;		// sentinel
    return find(accessBlock(root), Key);
  }
  
  public String fetch(int conceptID) throws Exception {
    return findID(blocks[conceptID], conceptID);
  }
  
  public void close() throws Exception {
    blockManager.close();
  }
  
  protected void init(BtreeDictParameters params, boolean update,
		      BlockFactory bfactory) throws Exception
  {
    blockManager = new BlockManager(params, update, bfactory);
    root = params.getRootPosition();
  }
  
  protected void lock(Block bl) {
    blockManager.lockBlock(bl.number);
  }
  
  protected void unlock(Block bl) {
    blockManager.unlockBlock(bl.number);
  }
  
  protected DictBlock accessBlock(int index) throws Exception {
    return (DictBlock)blockManager.accessBlock(index);
  }
  
  protected DictBlock child(DictBlock bl, int index) throws Exception {
    return accessBlock(bl.getChildIdx(index));
  }
  
  private String findID(int blNum, int id) throws Exception {
    return accessBlock(blNum).findID(id);
  }

  private int find(DictBlock bl, byte[] key, int index) throws Exception {
    return bl.isLeaf ? 0 : find(child(bl, index), key);
  }

  private int find(DictBlock bl, byte[] key) throws Exception
  {
    int inputKeyLen = key.length - 1;
    int entryPtr    = bl.firstEntry();
    int freeSpace   = bl.free();
    int nCharsEqual = 0;
    int compression = 0;

    for (int entryIdx = 0;;)
      {
	if (entryPtr == freeSpace)
	  return find(bl, key, bl.numberOfEntries());
	else if (compression == nCharsEqual)
	  {
	    int keyLen = bl.entryKeyLength(entryPtr);
	    int keyPtr = bl.entryKey(entryPtr), i;
	    for (i = 0;
		 i < keyLen && key[nCharsEqual] == bl.data[keyPtr + i];
		 i++)
	      ++nCharsEqual;
	    if (i == keyLen)
	      {
		if (nCharsEqual == inputKeyLen)
		  return bl.entryID(entryPtr);
	      }
	    else if ((key[nCharsEqual]&0xFF) < (bl.data[keyPtr + i]&0xFF))
	      return find(bl, key, entryIdx);
	  }
	else if (compression < nCharsEqual) // compression dropped
	  return find(bl, key, entryPtr == freeSpace
		      ? bl.numberOfEntries() : entryIdx);
	do {
	  entryPtr = bl.nextEntry(entryPtr);
	  ++entryIdx;
	}
	while (bl.entryCompression(entryPtr) > nCharsEqual);
	compression = bl.entryCompression(entryPtr);
      }
  }

  private void setBlocks(final int[] blocks) throws Exception
  {
    long start = System.currentTimeMillis();
    blockManager.mapBlocks(new BlockProcessor() {
      public void process(Block block) {
	((DictBlock)block).setBlockNumbers(blocks);
      }
    });
    debug((System.currentTimeMillis() - start) + " msec; TMAP");
  }

  /*
    can go to Full
  public void map(EntryProcessor processor) throws Exception {
    accessBlock(root).doMap(this, processor);
  }
  */
  /**
   * Debug code
   */

  private boolean debug=false;
  private void debug(String msg) {
    if (debug) {
      System.err.println("BtreeDict: "+msg);
    }
  }

  public static void main(String[] args)
  {
    try {
      Schema schema = new Schema(null, args[0], false);
      BtreeDictParameters params = new BtreeDictParameters(schema, "TMAP");
      BtreeDict source = new BtreeDict(params);
      /*
      source.map(new EntryProcessor() {
	public void processEntry(String key, int id) {
	  System.out.println(key + " " + id);
	}
      });
      */
    }
    catch (Exception e) {
      System.err.println(e);
      e.printStackTrace();
    }
  }
}
