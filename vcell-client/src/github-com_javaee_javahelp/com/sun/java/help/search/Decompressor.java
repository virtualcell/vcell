/*
 * @(#)Decompressor.java	1.7 06/10/30
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

abstract class Decompressor
{
  private static final int BitsInByte = 8;
  private static final int NBits = 32;
  
  private int _readByte;
  private int _toRead = 0;
  private int _path = 0;

  abstract protected int getNextByte() throws Exception;

  protected void initReading() {
    _toRead = 0;
  }
    
  private int countZeroes() throws Exception
  {
    for (int count = 0;; _readByte = getNextByte(), _toRead = BitsInByte)
      while (_toRead-- > 0)
	if ((_readByte & (1 << _toRead)) != 0)
	  return count;
	else
	  ++count;
  }
  
  // reads 1 bit; returns non-0 for bit "1"
  private int read() throws Exception
  {
    if (_toRead-- > 0)
      return _readByte & (1 << _toRead);
    else
      {  // get next word
	_toRead = BitsInByte - 1;
	return (_readByte = getNextByte()) & 0x80;
      }
  }
  
  public int read(int kBits) throws Exception
  {
    int shift = BitsInByte - _toRead;
    if (kBits <= _toRead)
      return ((_readByte<<shift) & 0xFF) >>> (shift + (_toRead-=kBits));
    else
      {
	int result = _toRead > 0
	  ? ((_readByte << shift) & 0xFF) >>> shift
	  : 0;
	for (kBits -= _toRead; kBits >= BitsInByte; kBits -= BitsInByte)
	  result = (result << BitsInByte) | getNextByte();
	if (kBits > 0)
	  return (result << kBits)
	    | ((_readByte = getNextByte()) >>> (_toRead = BitsInByte - kBits));
	else
	  {
	    _toRead = 0;
	    return result;
	  }
      }
  }
  
  public void beginIteration() {
    _path = 0;
  }

  public boolean readNext(int k, CompressorIterator it) throws Exception
  {
    if (read() != 0)
      {
	it.value(_path | read(k));
	return true;
      }
    else
      for (int count = 1;; _readByte = getNextByte(), _toRead = BitsInByte)
	while (_toRead-- > 0)
	  if ((_readByte & (1 << _toRead)) != 0)
	    {
	      int saved = _path;
	      _path = ((_path >>> (k + count) << count) | read(count)) << k;
	      if (_path != saved)
		{
		  it.value(_path | read(k));
		  return true;
		}
	      else
		return false;
	    }
	  else
	    ++count;
  }
  
  public void decode(int k, IntegerArray array) throws Exception
  {
    for (int path = 0;;)
      if (read() != 0)
	array.add(path | read(k));
      else
	{
	  int count = countZeroes() + 1;
	  int saved = path;
	  path = ((path >>> (k + count) << count) | read(count)) << k;
	  if (path != saved)	// convention for end
	    array.add(path | read(k));
	  else
	    break;
	}
  }

  public void ascDecode(int k, IntegerArray array) throws Exception
  {
    for (int path = 0, start = 0;;)
      if (read() != 0)
	array.add(start += path | read(k));
      else
	{
	  int count = countZeroes() + 1;
	  int saved = path;
	  path = ((path >>> (k + count) << count) | read(count)) << k;
	  if (path != saved)	// convention for end
	    array.add(start += path | read(k));
	  else
	    break;
	}
  }
  
  public int ascendingDecode(int k, int start, int[] array) throws Exception
  {
    int path = 0, index = 0;
  LOOP:
    while (true)
      if (read() != 0)
	array[index++] = (start += path | read(k));
      else
	for (int cnt = 0;; _readByte = getNextByte(), _toRead = BitsInByte)
	  while (_toRead-- > 0)
	    if ((_readByte & (1 << _toRead)) != 0)
	      {
		++cnt;
		int Path = ((path >>> (k + cnt) << cnt) | read(cnt)) << k;
		if (Path != path)
		  {
		    array[index++] = (start += (path = Path) | read(k));
		    continue LOOP;
		  }
		else
		  return index;
	      }
	    else
	      ++cnt;
  }
}
