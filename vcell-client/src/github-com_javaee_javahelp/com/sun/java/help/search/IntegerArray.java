/*
 * @(#)IntegerArray.java	1.7 06/10/30
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

class IntegerArray
{
  private int[] _array;
  private int _size;
  private int _free = 0;
  private static final int InitialSize = 128;
  
  public IntegerArray() {
    _array = new int[_size = InitialSize];
  }
  
  public IntegerArray(int size) {
    _array = new int[_size = size];
  }

  public void clear() {
    _free = 0;
  }

  public int at(int index) {
    return _array[index];
  }

  public void add(int value)
  {
    if (_free == _size)
      growArray(_size * 2);
    _array[_free++] = value;
  }
  
  private void growArray(int size)
  {
    int[] newArray = new int[_size = size];
    System.arraycopy(_array, 0, newArray, 0, _free);
    _array = newArray;
  }

  public int popLast() {
    return _array[--_free];
  }
  
  public int cardinality() {
    return _free;
  }
  
  public void toDifferences(IntegerArray result)
  {
    if (result._size < _size)
      result.growArray(_size);
    if ((result._free = _free) > 0)
      {
	result._array[0] = _array[0];
	for (int i = 1; i < _free; i++)
	  result._array[i] = _array[i] - _array[i - 1];
      }
  }
  
  public int indexOf(int value)
  {
    int i = 0, j = _free, k;
    while (i <= j)
      if (_array[k = (i + j)/2] < value)
	i = k + 1;
      else if (value < _array[k])
	j = k - 1;
      else
	return k;
    return -1;
  }
  
  public void print(java.io.PrintStream out)
  {
    for (int i = 0; i < _free - 1; i++)
      {
	out.print(_array[i]);
	out.print(' ');
      }
    out.println(_array[_free - 1]);
  }
}
