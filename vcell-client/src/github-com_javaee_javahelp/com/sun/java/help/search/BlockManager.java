/*
 * @(#)BlockManager.java	1.14 06/10/30
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
 * @(#) BlockManager.java 1.5 - last change made 03/20/98
 */
package com.sun.java.help.search;

import java.io.*;

/**
 * This class manages Block(s) of information in DictBlock
 *
 * @author Jacek R. Ambroziak
 * @author Roger D. Brinkley
 * @version	1.5	03/20/98
 */

class BlockManager
{
  /* the Dict file factory. It handles both retrieval and caching. 
   */
  private static RAFFileFactory factory = RAFFileFactory.create();

  //!! not sure if the double list is still correct after many changes
  private class BlockDescriptor
  {
    public Block block = null;
    public boolean lock = false;
    public boolean modf = false;
    public int prev = 0;
    public int next = 0;
  
    public void reset()
    {
      lock = modf = false;
      block = null;
    }
  }

  private RAFFile file;
  private long blockSize;
  private boolean update;
  final private int         nBlocksLimit = 64;
  private int              blockTableSize;
  private BlockDescriptor[] blockTab;
  private int              nBlocks = 0;
  private int              oldest = 0;
  private int              newest = 0;
  private BlockFactory     bfactory = null;
  private final static int INCR = 256; // size increment
  private Block dummy;
  
  public BlockManager(BlockManagerParameters params,
		      boolean update,
		      BlockFactory bfactory) throws IOException
  {
    this.bfactory = bfactory;
    this.update = update;
    //    params.readState();
    blockSize = (long)params.getBlockSize();
    debug(params.getURL().toString());
    file = factory.get(params.getURL(), update);
    debug(file.toString());
    dummy = bfactory.makeBlock();
    if (file.length() == 0 && update) {
      dummy.setBlockNumber(0);
      writeBlock(dummy);
    }
    blockTableSize = (int)(file.length()/blockSize);
    blockTab = new BlockDescriptor[blockTableSize];
    mapBlock(0, new BlockDescriptor(), bfactory.makeBlock());
  }
  
  public void lockBlock(int blNum) {
    blockTab[blNum].lock = true;
  }

  public void unlockBlock(int blNum) {
    blockTab[blNum].lock = false;
  }

  public void setModified(int blNum) {
    blockTab[blNum].modf = true;
  }

  public void close() throws IOException
  {
    if (update)
      for (int i = 0; i < blockTableSize; i++)
	if (blockTab[i] != null && blockTab[i].modf)
	  writeBlock(blockTab[i].block);
    file.close();
  }

  public Block accessBlock(int blockNumber) throws Exception
  {
    if (blockTab[blockNumber] != null)
      moveToFront(blockNumber);
    else if (nBlocks < nBlocksLimit)
      mapBlock(blockNumber, new BlockDescriptor(), bfactory.makeBlock());
    else
      remapSomeBlock(blockNumber);
    return blockTab[blockNumber].block;
  }
  
  public Block getNewBlock() throws Exception
  {
    int number = (int)(file.length()/blockSize);
    if (number > blockTableSize - 1)
      {
	BlockDescriptor[] newArray =
	  new BlockDescriptor[blockTableSize + INCR];
	System.arraycopy(blockTab, 0, newArray, 0, blockTableSize);
	blockTab = newArray;
	blockTableSize += INCR;
      }
    if (nBlocks < nBlocksLimit)
      {
	Block bl = bfactory.makeBlock();
	bl.setBlockNumber(number);
	writeBlock(bl);
	addDescriptor(bl, number, new BlockDescriptor());
      }
    else
      {
	dummy.setBlockNumber(number);
	writeBlock(dummy);
	remapSomeBlock(number);
      }
    return blockTab[number].block;
  }

  private void mapBlock(int blockNumber, BlockDescriptor desc, Block block)
    throws IOException
  {
    file.seek(blockSize * blockNumber);
    Block.readIn(file, block);
    addDescriptor(block, blockNumber, desc);
  }
  
  private void addDescriptor(Block block, int blockNumber,
			     BlockDescriptor desc)
  {
    blockTab[blockNumber] = desc;
    desc.block = block;
    blockTab[desc.prev = newest].next = blockNumber;
    newest = blockNumber;
    nBlocks++;
  }
  
  private void remapSomeBlock(int blockNumber) throws Exception
  {
    int index = oldest;
    while (blockTab[index].lock && index != newest)
      index = blockTab[index].next;
    if (blockTab[index].lock)
      throw new Exception("everything locked");
    if (blockTab[index].modf)
      writeBlock(blockTab[index].block);
    
    nBlocks--;
    Block reused = blockTab[index].block;
    // delete from double-linked list
    if (index == oldest)
      oldest = blockTab[index].next;
    else if (index == newest)
      newest = blockTab[index].prev;
    else
      {
	blockTab[blockTab[index].next].prev = blockTab[index].prev;
	blockTab[blockTab[index].prev].next = blockTab[index].next;
      }
    blockTab[index].reset();
    mapBlock(blockNumber, blockTab[index], reused);
    //    System.err.println("reuse "+index+" --> "+blockNumber+" "+nBlocks);
    blockTab[index] = null;
  }

  private void moveToFront(int index)
  {
    if (index == oldest)
      {
	oldest = blockTab[index].next;
	blockTab[index].prev = newest;
	blockTab[newest].next = index;
	newest = index;
      }
    else if (index != newest)
      {
	blockTab[blockTab[index].next].prev = blockTab[index].prev;
	blockTab[blockTab[index].prev].next = blockTab[index].next;
	blockTab[index].prev = newest;
	blockTab[newest].next = index;
	newest = index;
      }
  }
  
  public void writeBlock(Block bl) throws IOException
  {
    file.seek(blockSize * bl.number);
    bl.writeOut(file);
  }

  public void mapBlocks(BlockProcessor processor) throws IOException
  {
    long nBlocks = file.length()/blockSize;
    Block block = bfactory.makeBlock();
    file.seek(0);
    for (int i = 0; i < nBlocks; i++)
      processor.process(Block.readIn(file, block));
  }

  /**
   * Debug code
   */

  private boolean debug=false;
  private void debug(String msg) {
    if (debug) {
      System.err.println("Block Manager: "+msg);
    }
  }

}
