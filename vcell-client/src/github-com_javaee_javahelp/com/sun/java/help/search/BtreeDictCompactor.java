/*
 * @(#)BtreeDictCompactor.java	1.9 06/10/30
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
 * @date   5/1/98
 * @author Jacek R. Ambroziak
 * @group  Sun Microsystems Laboratories
 */

package com.sun.java.help.search;

import java.net.URL;

public class BtreeDictCompactor extends FullBtreeDict
{
  private CompactorDictBlock _currentLeaf;
  private byte[]          _lastKey = new byte[MaxKeyLength];
  private int             _lastKeyLength = 0;
  private int             _limit;
  private int             _nEntries = 0;
  private int             _entry = 0;
  
  private int             _counter = 0;
  private InternalBlockState _parent = null;
  
  private final class CompactorDictBlock extends FullDictBlock
  {
    private int restoreKey1(int entry, byte[] buffer)
    {
      int howMany = entryKeyLength(entry);
      int where = entryCompression(entry);
      int from = entryKey(entry);
      while (howMany-- > 0)
	buffer[where++] = data[from++];
      return where;
    }
  
    protected void doMap1(BtreeDictCompactor owner, BtreeDictCompactor target)
      throws Exception
    {
      byte[] buffer = new byte[MaxKeyLength];
      final int freeSpace = free();
      int entry = firstEntry();
      if (isLeaf)
	while (entry < freeSpace) {
	  target.store(buffer, restoreKey1(entry, buffer), entryID(entry));
	  entry = nextEntry(entry);
	}
      else
	{
	  owner.lock(this);
	  int entryIndex  = 0;
	  while (entry < freeSpace) {
	    owner.accessBlock1(getChildIdx(entryIndex)).doMap1(owner,target);
	    target.store(buffer, restoreKey1(entry, buffer), entryID(entry));
	    entry = nextEntry(entry);
	    ++entryIndex;
	  }
	  owner.accessBlock1(getChildIdx(entryIndex)).doMap1(owner, target);
	  owner.unlock(this);
	}
    }
  } // end of internal class

  private final class InternalBlockState
  {
    private CompactorDictBlock _block;
    private byte[]             _lastKey = new byte[MaxKeyLength];
    private int               _lastKeyLength;
    private int             _entry;
    private int             _nEntries;
    private int             _limit;
    private InternalBlockState _parent;
  
    public InternalBlockState(int leftChild)
    {
      debug("NEW ROOT " + _counter);
      debug(params.toString());
      params.setRoot(_counter);
      _block = new CompactorDictBlock();
      _block.isLeaf = false;
      _block.setBlockNumber(_counter++);
      init(leftChild);
    }
    
    private void init(int leftChild)
    {
      _entry = _block.firstEntry();
      _nEntries = 0;
      _lastKeyLength = 0;
      _limit = 4*(lastPtrIndex - 1);
      _block.setChildIndex(0, leftChild);
    }
  
    public void store(byte[] buffer, int keyLen, int id, int newBlock)
    {
      //      System.out.println(new String(buffer, 0, keyLen) + " " + id);
      int cpr = 0;
      while (cpr < _lastKeyLength && _lastKey[cpr] == buffer[cpr])
	++cpr;
      int needed = ENTHEADERLEN + keyLen - cpr;
      if (_entry + needed <= _limit)
	{
	  _block.makeEntry(_entry, buffer, id, keyLen - cpr, cpr);
	  _entry += needed;
	  _nEntries++;
	  _block.setChildIndex(_nEntries, newBlock);
	  _limit -= 4;
	  _lastKeyLength = keyLen;
	  System.arraycopy(buffer, cpr, _lastKey, cpr, keyLen - cpr);
	}
      else
	{
	  debug("NEW: SPLIT INTERNAL");
	  _block.setFree(_entry);
	  _block.setNumberOfEntries(_nEntries);
	  if (_parent == null)
	    _parent = new InternalBlockState(_block.number);
	  _parent.store(buffer, keyLen, id, newBlock(_block));
	  init(newBlock);
	}
    }
    
    public void close() throws java.io.IOException
    {
      _block.setFree(_entry);
      _block.setNumberOfEntries(_nEntries);
      blockManager.writeBlock(_block);
      if (_parent == null)
	debug("root: " + _block.number);
      else
	_parent.close();
    }
  } // end of internal class

  public BtreeDictCompactor(BtreeDictParameters params, boolean update)
    throws Exception
  {
    init(params, update, new BlockFactory() {
      public Block makeBlock() { return new CompactorDictBlock(); }
    });
    _currentLeaf = new CompactorDictBlock();
    _currentLeaf.setBlockNumber(_counter++);
    _limit = DATALEN - 2;
    _entry = _currentLeaf.firstEntry();
    this.params = params;
  }

  protected CompactorDictBlock accessBlock1(int index) throws Exception {
    return (CompactorDictBlock)blockManager.accessBlock(index);
  }
  
  public void store(byte[] buffer, int keyLen, int id)
  {
    //    System.out.println(new String(buffer, 0, keyLen));
    if (id > 0)
      {
	int cpr = 0;
	while (cpr < _lastKeyLength && _lastKey[cpr] == buffer[cpr])
	  ++cpr;
	int needed = ENTHEADERLEN + keyLen - cpr;
	if (_entry + needed <= _limit)
	  {
	    _currentLeaf.makeEntry(_entry, buffer, id, keyLen - cpr, cpr);
	    _entry += needed;
	    _nEntries++;
	    _lastKeyLength = keyLen;
	    System.arraycopy(buffer, cpr, _lastKey, cpr, keyLen - cpr);
	  }
	else
	  {
	    _currentLeaf.setFree(_entry);
	    _currentLeaf.setNumberOfEntries(_nEntries);
	    if (_parent == null)
	      _parent = new InternalBlockState(_currentLeaf.number);
	    _parent.store(buffer, keyLen, id, newBlock(_currentLeaf));
	    _entry = _currentLeaf.firstEntry();
	    _nEntries = 0;
	    _lastKeyLength = 0;
	  }
      }
  }

  private int newBlock(DictBlock block)
  {
    int number = _counter++;
    try {
      blockManager.writeBlock(block); // write out full
      block.setBlockNumber(number); // recycle
      blockManager.writeBlock(block); // reserve space for new
    }
    catch (java.io.IOException e) {
      e.printStackTrace();
    }
    return number;
  }

  public void close() throws java.io.IOException
  {
    _currentLeaf.setFree(_entry);
    _currentLeaf.setNumberOfEntries(_nEntries);
    blockManager.writeBlock(_currentLeaf);
    if (_parent == null)
      debug("root: " + _currentLeaf.number);
    else
      _parent.close();
    blockManager.close();
  }

  public void compact(BtreeDictParameters params) throws Exception
  {
    final BtreeDictCompactor target = new BtreeDictCompactor(params, true);
    ((CompactorDictBlock)blockManager.accessBlock(root)).doMap1(this, target);
    target.close();
    blockManager.close();
  }
  
  public static void main(String[] args)
  {
    try {
      Schema schema = new Schema(null, args[0], false);
      BtreeDictParameters params = new BtreeDictParameters(schema, "TMAP");
      BtreeDictCompactor source = new BtreeDictCompactor(params, false);
      URL url = new URL("file", "", args[1]);
      BtreeDictParameters params2 = new BtreeDictParameters(url, 2048, 0, 24);
      source.compact(params2);
    }
    catch (Exception e) {
      System.err.println(e);
      e.printStackTrace();
    }
  }
  /**
   * Debug code
   */

  private boolean debug=false;
  private void debug(String msg) {
    if (debug) {
      System.err.println("BtreeDictCompactor: "+msg);
    }
  }
}
