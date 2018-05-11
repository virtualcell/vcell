/*
 * @(#)BitBuffer.java	1.6 06/10/30
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
 * @date   2/17/98
 * @author Jacek R. Ambroziak
 * @group  Sun Microsystems Laboratories
 */

/*
  BitBuffer's client has to stick to a usage protocol:
  
  - start with new or cleared BitBuffer
  - do appends for a while
  - finish w/ close()
  
  */

package com.sun.java.help.search;
import java.io.*;

class BitBuffer
{
  private static final int InitSize   = 256;
  private static final int NBits      =  32;
  private static final int BitsInByte =   8;
  private static final int BytesInInt =   4;
  
  private int   _avail = NBits;
  private int   _word = 0;
  private int   _free = 0;
  private int   _size = InitSize;
  private int[] _array = new int[InitSize];

  public void close()
  {
    if (_avail < NBits)
      store(_word << _avail);
    else
      _avail = 0;
  }
  
  public void write(DataOutput out) throws IOException
  {
    for (int i = 0; i < _free - 1; i++)
      out.writeInt(_array[i]);
    int word = _array[_free - 1];
    int bytes = BytesInInt - _avail/BitsInByte;
    int shift = NBits;
    while (bytes-- > 0)
      out.writeByte((word >>> (shift -= BitsInByte)) & 0xFF);
  }

  public void clear()
  {
    _word   = 0;
    _avail  = NBits;
    _free   = 0;
  }
  
  public int byteCount() {
    return _free*BytesInInt - _avail/BitsInByte;
  }
  
  public int bitCount() {
    return NBits*_free - _avail;
  }

  public void setFrom(BitBuffer rhs)
  {
    _word  = rhs._word;
    _avail = rhs._avail;
    if ((_free = rhs._free) > _size)
      _array = new int[_size = rhs._free];
    System.arraycopy(rhs._array, 0, _array, 0, _free);
  }
  
  private void growArray(int newSize)
  {
    int[] newArray = new int[_size = newSize];
    System.arraycopy(_array, 0, newArray, 0, _free);
    _array = newArray;
  }

  private void store(int value)
  {
    if (_free == _size)
      growArray(_size * 2);
    _array[_free++] = value;
  }

  public void append(int bit)
  {
    _word = (_word << 1) | bit;
    if (--_avail == 0)
      {
	store(_word);
	_word = 0;
	_avail = NBits;
      }
  }
  
  public void append(int source, int kBits)
  {
    if (kBits < _avail)
      {
	_word = (_word << kBits) | source;
	_avail -= kBits;
      }
    else if (kBits > _avail)
      {
	int leftover = kBits - _avail;
	store((_word << _avail) | (source >>> leftover));
	_word = source;
	_avail = NBits - leftover;
      }
    else
      {
	store((_word << kBits) | source);
	_word = 0;
	_avail = NBits;
      }
  }
  
  public void concatenate(BitBuffer bb)
  {
    if (NBits*(_size - _free) + _avail < bb.bitCount())
      growArray(_free + bb._free + 1);

    if (_avail == 0)
      {
	System.arraycopy(bb._array, 0, _array, _free, bb._free);
	_avail = bb._avail;
	_free += bb._free;
      }
    else
      {
	int tp = _free - 1;	// target
	int sp = 0;		// source
	do {
	  _array[tp++] |= bb._array[sp] >>> (NBits - _avail);
	  _array[tp] = bb._array[sp++] << _avail;
	}
	while (sp < bb._free);
	_free += bb._free;
	if ((_avail += bb._avail) >= NBits)
	  {
	    _avail -= NBits;
	    _free--;
	  }
      }
  }
}
