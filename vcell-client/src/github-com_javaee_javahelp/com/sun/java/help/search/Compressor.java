/*
 * @(#)Compressor.java	1.9 06/10/30
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

package com.sun.java.help.search;

import java.io.*;

class Compressor
{
  private static final int NBits  = 32;
  private static final int BeginK =  5;

  private BitBuffer _buffer = new BitBuffer();

  public void write(DataOutput out) throws IOException {
    _buffer.write(out);
  }

  public int byteCount() {
    return _buffer.byteCount();
  }

  public void clear() {
    _buffer.clear();
  }

  public void concatenate(Compressor other) {
    _buffer.concatenate(other._buffer);
  }

  public void encode(IntegerArray pos, int k)
  {
    int power = 1 << k, n1 = 0;
    for (int i = 0; i < pos.cardinality(); i++)
      {
	int n2 = pos.at(i) >>> k;
	int rem = pos.at(i) % power;
	if (n2 != n1)
	  {
	    int min = n1, a = n1;
	    int lev = 0, power2 = 1;
	    if (n2 > n1)
	      for (int max = n1; max < n2; a >>>= 1, power2 <<= 1, lev++)
		if ((a & 1) != 0)
		  min -= power2;
		else
		  max += power2;
	    else
	      for ( ; min > n2; a >>>= 1, power2 <<= 1, lev++)
		if ((a & 1) != 0)
		  min -= power2;
	    // lev 0s, 1, lev bits of (n2 - min) plus following value
	    // no 'V' symbol needed here
	    if (lev*2 + 1 + k <= NBits)
	      _buffer.append((1<<lev | (n2 - min)) << k | rem, lev*2+1+k);
	    else
	      {
		if (lev*2 + 1 <= NBits)
		  _buffer.append(1 << lev | (n2 - min), lev*2 + 1);
		else
		  {
		    _buffer.append(0, lev);
		    _buffer.append(1 << lev | (n2 - min), lev + 1);
		  }
		_buffer.append(rem, k);
	      }
	    n1 = n2;
	  }
	else
	  _buffer.append(rem | power, k + 1); // 'V' + value
      }
    _buffer.append(2 | n1 & 1, 3); // marking end
    _buffer.close();
  }

  public void encode(IntegerArray pos, IntegerArray len, int k, int k2)
  {
    int power = 1 << k, n1 = 0;
    for (int i = 0; i < pos.cardinality(); i++)
      {
	int n2 = pos.at(i) >>> k;
	int rem = pos.at(i) % power;
	if (n2 != n1)
	  {
	    int min = n1, a = n1;
	    int lev = 0, power2 = 1;
	    if (n2 > n1)
	      for (int max = n1; max < n2; a >>>= 1, power2 <<= 1, lev++)
		if ((a & 1) != 0)
		  min -= power2;
		else
		  max += power2;
	    else
	      for ( ; min > n2; a >>>= 1, power2 <<= 1, lev++)
		if ((a & 1) != 0)
		  min -= power2;
	    // lev 0s, 1, lev bits of (n2 - min) plus following value
	    if (lev*2 + 1 + k <= NBits)
	      _buffer.append((1<<lev | (n2 - min)) << k | rem, lev*2+1+k);
	    else
	      {
		if (lev*2 + 1 <= NBits)
		  _buffer.append(1 << lev | (n2 - min), lev*2 + 1);
		else
		  {
		    _buffer.append(0, lev);
		    _buffer.append(1 << lev | (n2 - min), lev + 1);
		  }
		_buffer.append(rem, k);
	      }
	    _buffer.append(len.at(i), k2);
	    n1 = n2;
	  }
	else
	  _buffer.append((rem|power)<<k2 | len.at(i), k+k2+1); // 'V' + v1,v2
      }
    _buffer.append(2 | n1 & 1, 3); // marking end
    _buffer.close();
  }
  
  // k: starting value for minimization  
  public int minimize(IntegerArray array, int startK)
  {
    BitBuffer saved = new BitBuffer();
    int minK = startK;
    _buffer.clear();
    encode(array, startK);
    int min = _buffer.bitCount();	// init w/ first value
    saved.setFrom(_buffer);
  
    _buffer.clear();
    encode(array, startK + 1);

    if (_buffer.bitCount() < min)
      {
	int k = startK + 1;
	do {
	  saved.setFrom(_buffer);
	  min = _buffer.bitCount();
	  minK = k;
	  _buffer.clear();
	  encode(array, ++k);
	}
	while (_buffer.bitCount() < min);
      }
    else				// try smaller values through 1
      for (int k = startK - 1; k > 0; k--)
	{
	  _buffer.clear();
	  encode(array, k);
	  if (_buffer.bitCount() < min)
	    {
	      saved.setFrom(_buffer);
	      min = _buffer.bitCount();
	      minK = k;
	    }
	  else
	    break;
	}
  
    _buffer.setFrom(saved);
    return minK;
  }
  
  public int compressAscending(IntegerArray array)
  {
    IntegerArray differences = new IntegerArray(array.cardinality());
    array.toDifferences(differences);
    return minimize(differences, BeginK);
  }
}
